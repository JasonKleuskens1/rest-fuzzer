package nl.ou.se.rest.fuzzer.components.service.rmd;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzProjectService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionDependencyService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdParameterService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdResponseService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdSutService;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdSut;
import nl.ou.se.rest.fuzzer.components.service.rmd.domain.RmdSutDto;
import nl.ou.se.rest.fuzzer.components.service.rmd.mapper.RmdSutMapper;
import nl.ou.se.rest.fuzzer.components.service.util.ValidatorUtil;
import nl.ou.se.rest.fuzzer.components.shared.Constants;

@RestController()
@RequestMapping("/rest/suts")
public class RmdSutController {

    // variable(s)
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private RmdSutService sutService;

    @Autowired
    private RmdActionService actionService;

    @Autowired
    private RmdParameterService parameterService;

    @Autowired
    private RmdResponseService responseService;

    @Autowired
    private RmdActionDependencyService actionDependencyService;

    @Autowired
    private FuzProjectService projectService;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<RmdSutDto> findAll() {
        List<RmdSut> suts = sutService.findAll();
        return RmdSutMapper.toDtos(suts);
    }

    // method(s)
    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        Optional<RmdSut> sut = sutService.findById(id);

        if (!sut.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, RmdSut.class, id));
            return ResponseEntity.badRequest().body(new RmdSutDto());
        }

        return ResponseEntity.ok(RmdSutMapper.toDto(sut.get(), true));
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> add(@RequestBody RmdSutDto sutDto) {
        RmdSut sut = RmdSutMapper.toDomain(sutDto);
        sut.setCreatedAt(LocalDateTime.now());

        List<String> violations = ValidatorUtil.getViolations(sut);
        if (!violations.isEmpty()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_FAILED, RmdSut.class, violations.size()));
            return ValidatorUtil.getResponseForViolations(violations);
        }

        sut = sutService.save(sut);
        return ResponseEntity.ok(RmdSutMapper.toDto(sut, false));
    }

    @Transactional
    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Optional<RmdSut> sut = sutService.findById(id);

        if (!sut.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, RmdSut.class, id));
            return ResponseEntity.badRequest().body(new RmdSutDto());
        }

        if (projectService.countBySutId(id) > 0) {
            logger.warn(String.format(Constants.Service.VALIDATION_SUT_USED_BY_PROJECTS, id));
            return ValidatorUtil
                    .getResponseForViolation(String.format(Constants.Service.VALIDATION_SUT_USED_BY_PROJECTS, id));
        }

        List<Long> actionDependencyIds = actionDependencyService.findIdsBySutId(id);
        if (!actionDependencyIds.isEmpty()) {
        	actionDependencyService.deleteByIds(actionDependencyIds);
        }

        List<Long> parameterIds = parameterService.findIdsBySutId(id);
        if (!parameterIds.isEmpty()) {
        	parameterService.deleteByIds(parameterIds);
        }

        List<Long> responseIds = responseService.findIdsBySutId(id);
        if (!responseIds.isEmpty()) {
        	responseService.deleteByIds(responseIds);
        }

        actionService.deleteBySutId(id);

        sutService.deleteById(id);

        return ResponseEntity.ok(RmdSutMapper.toDto(sut.get(), false));
    }
}