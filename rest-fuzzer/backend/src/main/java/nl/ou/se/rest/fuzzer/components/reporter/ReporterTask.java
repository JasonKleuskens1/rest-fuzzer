package nl.ou.se.rest.fuzzer.components.reporter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.report.dao.ReportService;
import nl.ou.se.rest.fuzzer.components.data.report.domain.Report;
import nl.ou.se.rest.fuzzer.components.extractor.ExtractorTask;
import nl.ou.se.rest.fuzzer.components.reporter.coverage.CoverageReporter;
import nl.ou.se.rest.fuzzer.components.reporter.responses.ResponsesReporter;
import nl.ou.se.rest.fuzzer.components.task.TaskExecution;
import nl.ou.se.rest.fuzzer.components.task.TaskExecutionBase;

@Service
public class ReporterTask extends TaskExecutionBase implements TaskExecution {

    // variable(s)
    public static final String KEY_REPORT_ID = "report_id";

    private Reporter reporter;

    @Autowired
    private ReportService reportService;
    
    @Autowired
    private CoverageReporter coverageReporter;
    
    @Autowired
    private ResponsesReporter responsesReporter;

    // method(s)
    public void execute() {
        this.logStart(ReporterTask.class.getTypeName());

        Object objReportId = this.getValueForKey(ReporterTask.class, KEY_REPORT_ID);
        if (objReportId == null) {
            return;
        }

        Long reportId = Long.valueOf((Integer) objReportId);
        Optional<Report> oReport = reportService.findById(reportId);

        if (!this.isOptionalPresent(ExtractorTask.class, oReport, reportId)) {
            return;
        }

        Report report = oReport.get();

        switch (report.getType()) {
        case CODE_COVERAGE:
            reporter = coverageReporter;
            break;
        case RESPONSES:
            reporter = responsesReporter;
            break;
        default:
            break;
        }

        if (reporter.isMetaDataValid(report.getMetaDataTuples())) {
            reporter.init(report, this.getTask());
            reporter.generate();
        }

        this.logStop(ReporterTask.class.getTypeName());
    }
}