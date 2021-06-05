package nl.ou.se.rest.fuzzer.components.service.fuz;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzProjectService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzRequestService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzResponseService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzSequenceService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdSutService;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdSut;
import nl.ou.se.rest.fuzzer.components.service.fuz.domain.FuzProjectDto;
import nl.ou.se.rest.fuzzer.components.service.fuz.mapper.FuzProjectMapper;
import nl.ou.se.rest.fuzzer.components.service.fuz.mapper.FuzRequestMapper;
import nl.ou.se.rest.fuzzer.components.service.util.ValidatorUtil;
import nl.ou.se.rest.fuzzer.components.shared.Constants;
import nl.ou.se.rest.fuzzer.components.shared.FilterUtil;

@RestController()
@RequestMapping("/rest/projects")
public class FuzProjectController {

    // variable(s)
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    FuzProjectService projectService;

    @Autowired
    FuzSequenceService sequenceService;

    @Autowired
    FuzRequestService requestService;

    @Autowired
    FuzResponseService responseService;

    @Autowired
    RmdSutService sutService;

    // method(s)
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<FuzProjectDto> findAll() {
        List<FuzProject> projects = projectService.findAll();
        return FuzProjectMapper.toDtos(projects);
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        Optional<FuzProject> project = projectService.findById(id);

        if (!project.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, FuzProject.class, id));
            return ResponseEntity.badRequest().body(new FuzProjectDto());
        }

        return ResponseEntity.ok(FuzProjectMapper.toDto(project.get(), true));
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> add(@RequestBody FuzProjectDto projectDto) {
        FuzProject project = FuzProjectMapper.toDomain(projectDto);
        project.setCreatedAt(LocalDateTime.now());

        if (projectDto.getSut() != null && projectDto.getSut().getId() != null) {
            Optional<RmdSut> sut = sutService.findById(projectDto.getSut().getId());
            if (sut.isPresent()) {
                project.setSut(sut.get());
            }
        }

        List<String> violations = ValidatorUtil.getViolations(project);
        if (!violations.isEmpty()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_FAILED, FuzProject.class, violations.size()));
            return ValidatorUtil.getResponseForViolations(violations);
        }

        project = projectService.save(project);
        return ResponseEntity.ok(FuzProjectMapper.toDto(project, false));
    }

    @Transactional
    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Optional<FuzProject> project = projectService.findById(id);

        if (!project.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, FuzProject.class, id));
            return ResponseEntity.badRequest().body(new FuzProjectDto());
        }

        responseService.deleteByProjectId(id);
        requestService.deleteByProjectId(id);
        sequenceService.deleteByProjectId(id);
        projectService.deleteById(id);

        return ResponseEntity.ok(FuzProjectMapper.toDto(project.get(), false));
    }

    @RequestMapping(path = "{id}/requests", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findRequestsByProjectId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "curPage") int curPage, @RequestParam(name = "perPage") int perPage,
            @RequestParam(name = "filter", required = false) String filter) {

        List<HttpMethod> httpMethods = FilterUtil.getHttpMethods(filter);
        String path = FilterUtil.getValueFromFilter(filter, FilterUtil.PATH);

        return ResponseEntity.ok(FuzRequestMapper.toDtos(requestService.findByFilter(id, httpMethods,
                FilterUtil.toLike(path), FilterUtil.toPageRequest(curPage, perPage))));
    }

    @RequestMapping(path = "{id}/requests/count", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> countRequestsByProjectId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "filter", required = false) String filter) {

        List<HttpMethod> httpMethods = FilterUtil.getHttpMethods(filter);
        String path = FilterUtil.getValueFromFilter(filter, FilterUtil.PATH);

        return ResponseEntity.ok(requestService.countByFilter(id, httpMethods, FilterUtil.toLike(path)));
    }

    @Transactional
    @RequestMapping(path = "{id}/requests", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> deleteRequestsByProjectId(@PathVariable(name = "id") Long id) {
        Optional<FuzProject> project = projectService.findById(id);
        requestService.deleteByProjectId(id);
        return ResponseEntity.ok(FuzProjectMapper.toDto(project.get(), false));
    }
}