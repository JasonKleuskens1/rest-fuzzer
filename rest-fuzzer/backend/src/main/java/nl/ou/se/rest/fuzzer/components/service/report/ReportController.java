package nl.ou.se.rest.fuzzer.components.service.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzProjectService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.data.report.dao.ReportService;
import nl.ou.se.rest.fuzzer.components.data.report.domain.Report;
import nl.ou.se.rest.fuzzer.components.service.report.domain.ReportDto;
import nl.ou.se.rest.fuzzer.components.service.report.mapper.ReportMapper;
import nl.ou.se.rest.fuzzer.components.service.util.ValidatorUtil;
import nl.ou.se.rest.fuzzer.components.shared.Constants;

@RestController()
@RequestMapping("/rest/reports")
public class ReportController {

    // variable(s)
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReportService reportService;

    @Autowired
    FuzProjectService projectService;

    // method(s)
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findAll() {
        List<Report> reports = reportService.findAll();
        return ResponseEntity.ok(ReportMapper.toDtos(reports));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        Optional<Report> report = reportService.findById(id);

        if (!report.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, Report.class, id));
            return ResponseEntity.badRequest().body(new ReportDto());
        }

        return ResponseEntity.ok(ReportMapper.toDto(report.get()));
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> add(@RequestBody ReportDto reportDto) {
        Report report = ReportMapper.toDomain(reportDto);
        report.setCreatedAt(LocalDateTime.now());

        if (reportDto.getProject() != null && reportDto.getProject().getId() != null) {
            Optional<FuzProject> project = projectService.findById(reportDto.getProject().getId());
            if (project.isPresent()) {
                report.setProject(project.get());
            }
        }

        List<String> violations = ValidatorUtil.getViolations(report);
        if (!violations.isEmpty()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_FAILED, Report.class, violations.size()));
            return ValidatorUtil.getResponseForViolations(violations);
        }

        report = reportService.save(report);
        return ResponseEntity.ok(ReportMapper.toDto(report));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Optional<Report> report = reportService.findById(id);

        if (!report.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, Report.class, id));
            return ResponseEntity.badRequest().body(new ReportDto());
        }

        reportService.deleteById(id);
        return ResponseEntity.ok(ReportMapper.toDto(report.get()));
    }
}
