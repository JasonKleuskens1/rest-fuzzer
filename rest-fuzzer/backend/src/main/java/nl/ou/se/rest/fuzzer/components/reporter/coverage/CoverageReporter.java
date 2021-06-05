package nl.ou.se.rest.fuzzer.components.reporter.coverage;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.report.dao.ReportService;
import nl.ou.se.rest.fuzzer.components.data.report.domain.Report;
import nl.ou.se.rest.fuzzer.components.data.task.dao.TaskService;
import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.MetaDataUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.MetaDataUtil.Meta;
import nl.ou.se.rest.fuzzer.components.reporter.Reporter;
import nl.ou.se.rest.fuzzer.components.reporter.ReporterBase;
import nl.ou.se.rest.fuzzer.components.reporter.coverage.calculation.CoverageFile;
import nl.ou.se.rest.fuzzer.components.reporter.coverage.calculation.PhpFile;
import nl.ou.se.rest.fuzzer.components.shared.Constants;
import nl.ou.se.rest.fuzzer.components.shared.JsonUtil;

@Service
public class CoverageReporter extends ReporterBase implements Reporter {

    // variable(s)
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static String PATH_XDEBUG_FILES = "C:\\xdebug_dump";

    /**
     * Variants:
     * 
     * - "c:\\xampp\\apps\\wordpress\\htdocs\\wp-includes\\rest-api\\"; -
     * "c:/xampp/apps/wordpress/htdocs/wp-includes/rest-api/"; -
     * "\\/var\\/www\\/html\\/wordpress\\/wp-includes\\/rest-api\\/";
     * 
     */
    private static String PATH_ENDPOINTS = "c:/xampp/htdocs/wordpress/wp-includes/rest-api/";

    private Report report;
    private Task task;

    private MetaDataUtil metaDataUtil = null;
    private List<List<Object>> dataLines = new ArrayList<>();

    private static final String SEPERATOR_COMMA = ",";

    private static final String HEADER_TIME_PASSED = "time";
    private static final String HEADER_TOTAL_RESPONSES = "responses";
    private static final String HEADER_CC_PERC_ENDPOINTS = "cc-endpoints";
    private static final String HEADER_LOC_EXECUTED_ENDPOINTS = "loc-executed-endpoints";
    private static final String HEADER_CC_PERC_TOTAL = "cc-total";
    private static final String HEADER_LOC_EXECUTED_TOTAL = "loc-executed-total";

    // variable(s) for velocity template
    private static final String VM_DATA_ROWS = "dataRows";
    private static final String VM_PLOTS = "plots";
    private static final String VM_X_TICKS_LABELS = "xTicksLabels";
    private static final String VM_X_TICKS = "xTicks";
    private static final String VM_X_MAX = "xMax";
    private static final String VM_Y_TICKS = "yTicks";

    @Autowired
    private ReportService reportService;

    @Autowired
    private TaskService taskService;

    // method(s)
    @Override
    public void init(Report report, Task task) {
        this.report = report;
        this.task = task;

        dataLines.clear(); // reset
    }

    public void generate() {
        gatherData();

        this.report.setOutput(parseTemplate());
        this.report.setCompletedAt(LocalDateTime.now());
        this.reportService.save(this.report);

        task.setProgress(new BigDecimal(100));
        taskService.save(this.task);
    }

    public Boolean isMetaDataValid(Map<String, Object> metaDataTuples) {
        this.metaDataUtil = new MetaDataUtil(metaDataTuples);
        return metaDataUtil.isValid(Meta.POINTS_INTERVAL, Meta.X_TICK_INTERVAL, Meta.Y_TICK_INTERVAL);
    }

    private void gatherData() {
        Integer pointsInterval = this.metaDataUtil.getIntegerValue(Meta.POINTS_INTERVAL);

        List<Path> filesOnDisk = Stream.of(new File(PATH_XDEBUG_FILES).listFiles()).filter(file -> !file.isDirectory())
                .map(file -> file.toPath()).collect(Collectors.toList());

        Collections.sort(filesOnDisk);

        CoverageFile current = null;
        CoverageFile sum = null;

        Integer responsesCount = 0;
        LocalDateTime startTime = null;

        // line with zeros for each column
        List<Object> zeros = new ArrayList<>();
        IntStream.rangeClosed(1, 6).forEach(i -> zeros.add(0));
        this.dataLines.add(zeros);

        for (Path fileOnDisk : filesOnDisk) {
            responsesCount++;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS");
            LocalDateTime localDateTime = LocalDateTime.parse(fileOnDisk.getFileName().toString(), formatter);

            logger.info(String.format(Constants.Reporter.PROCESSING_FILE, fileOnDisk.getFileName().toString(),
                    localDateTime));

            if (startTime == null) {
                startTime = localDateTime;
            }

            Integer time = Long.valueOf(ChronoUnit.SECONDS.between(startTime, localDateTime)).intValue();

            try {
                current = processFile(fileOnDisk);

                if (current != null && sum != null) {
                    sum.merge(current);
                }
            } catch (IOException e) {
                logger.warn(String.format(Constants.Reporter.IO_EXCEPTION, report.getId(), e.getMessage()));
                break;
            }

            if (sum == null) { 
                sum = current; // initialize sum
            }

            if (responsesCount % pointsInterval == 0 || responsesCount == filesOnDisk.size()) {
                logger.debug(String.format(Constants.Reporter.ADD_INTERVAL, responsesCount));
                List<Object> dataLine = new ArrayList<>();

                dataLine.add(responsesCount);
                dataLine.add(time);
                dataLine.add(sum.codeCoveragePercentageFiltered(PATH_ENDPOINTS));
                dataLine.add(sum.linesExecuted(PATH_ENDPOINTS));
                dataLine.add(sum.codeCoveragePercentage());
                dataLine.add(sum.linesExecuted());

                this.dataLines.add(dataLine);
            }

            task.setProgress(new BigDecimal((responsesCount * 100) / filesOnDisk.size()));
            taskService.save(this.task);
        }
    }

    private String parseTemplate() {
        VelocityEngine ve = this.getVelocityEngine();

        Template t = ve.getTemplate("velocity/report-code-coverage.vm");

        VelocityContext vc = new VelocityContext();

        List<String> dataLineStrings = new ArrayList<>();
        dataLineStrings.add(ObjectListtoString(this.getHeaders(), SEPERATOR_COMMA));
        dataLineStrings.addAll(this.dataLines.stream().map(line -> ObjectListtoString(line, SEPERATOR_COMMA))
                .collect(Collectors.toList()));

        vc.put(VM_DATA_ROWS, dataLineStrings);
        vc.put(VM_PLOTS, getPlots());

        Integer xTicksInterval = this.metaDataUtil.getIntegerValue(Meta.X_TICK_INTERVAL);
        List<Integer> xTicks = getXticks(this.dataLines, xTicksInterval);

        vc.put(VM_X_MAX, xTicks.remove(xTicks.size() - 1));
        vc.put(VM_X_TICKS, ObjectListtoString(xTicks, SEPERATOR_COMMA));
        vc.put(VM_X_TICKS_LABELS, ObjectListtoString(getXticksLabels(this.dataLines, xTicks), SEPERATOR_COMMA));

        Integer yTicksInterval = this.metaDataUtil.getIntegerValue(Meta.Y_TICK_INTERVAL);
        vc.put(VM_Y_TICKS, ObjectListtoString(getYticks(dataLines, yTicksInterval), SEPERATOR_COMMA));

        StringWriter sw = new StringWriter();
        t.merge(vc, sw);

        return sw.toString();
    }

    private List<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        headers.add(HEADER_TOTAL_RESPONSES);
        headers.add(HEADER_TIME_PASSED);
        headers.add(HEADER_CC_PERC_ENDPOINTS);
        headers.add(HEADER_LOC_EXECUTED_ENDPOINTS);
        headers.add(HEADER_CC_PERC_TOTAL);
        headers.add(HEADER_LOC_EXECUTED_TOTAL);

        return headers;
    }

    private List<Object[]> getPlots() {
        List<Object[]> plots = new ArrayList<>();
        plots.add(getPlot(HEADER_LOC_EXECUTED_ENDPOINTS));
        plots.add(getPlot(HEADER_LOC_EXECUTED_TOTAL));

        return plots;
    }

    private Object[] getPlot(String title) {
        Object[] plot = new Object[2];
        plot[0] = title;
        plot[1] = 0;

        return plot;
    }

    private static CoverageFile processFile(Path path) throws IOException {
        String fileContent = Files.readString(path, StandardCharsets.UTF_8);
        Map<String, Object> objects = JsonUtil.stringToMap(fileContent);

        CoverageFile coverageFile = new CoverageFile();

        Map<String, PhpFile> phpFiles = new HashMap<>();
        objects.entrySet().forEach(entry -> {
            phpFiles.put(entry.getKey(), processFileEntry(entry));
        });
        coverageFile.setPhpFiles(phpFiles);

        return coverageFile;
    }

    private static PhpFile processFileEntry(Entry<String, Object> entry) {
        PhpFile file = new PhpFile();
        file.setName(entry.getKey());
        JSONObject jsonObject = (JSONObject) entry.getValue();
        jsonObject.keySet().forEach(key -> {
            processLineEntry(file, (JSONObject) entry.getValue());
        });

        return file;
    }

    private static void processLineEntry(PhpFile phpFile, JSONObject jsonObject) {
        List<Integer> linesExecuted = new ArrayList<>();
        List<Integer> linesNotExecuted = new ArrayList<>();

        jsonObject.keySet().forEach(key -> {
            // 1 => line is executed
            if (1 == jsonObject.getInt(key)) {
                linesExecuted.add(Integer.parseInt(key));
            } else {
                linesNotExecuted.add(Integer.parseInt(key));
            }
        });

        phpFile.setLinesExecuted(linesExecuted);
        phpFile.setLinesNotExecuted(linesNotExecuted);
    }
}