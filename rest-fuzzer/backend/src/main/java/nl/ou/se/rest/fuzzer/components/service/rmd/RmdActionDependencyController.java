package nl.ou.se.rest.fuzzer.components.service.rmd;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionDependencyService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdParameterService;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.DiscoveryModus;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdActionDependency;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;
import nl.ou.se.rest.fuzzer.components.data.rmd.factory.RmdActionDependencyFactory;
import nl.ou.se.rest.fuzzer.components.service.rmd.mapper.RmdActionDependencyMapper;
import nl.ou.se.rest.fuzzer.components.service.util.ValidatorUtil;
import nl.ou.se.rest.fuzzer.components.shared.Constants;
import nl.ou.se.rest.fuzzer.components.shared.FilterUtil;

@RestController()
@RequestMapping("/rest/suts/{id}/actions")
public class RmdActionDependencyController {

    // variable(s)
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private RmdActionService actionService;

    @Autowired
    private RmdParameterService parameterService;

    @Autowired
    private RmdActionDependencyService actionDependencyService;

    // method(s)
    @RequestMapping(path = "dependencies", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addActionDependency(@RequestBody Map<String, Object> parameters) {
        Optional<RmdParameter> parameter = parameterService.findById(
                Long.valueOf((parameters.get("parameter") == null ? -1 : (Integer) parameters.get("parameter"))));

        if (!parameter.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, RmdParameter.class,
                    parameters.get("parameterId")));
        }

        Optional<RmdAction> action = actionService
                .findById(Long.valueOf(parameters.get("action") == null ? -1 : (Integer) parameters.get("action")));

        if (!action.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, RmdAction.class,
                    parameters.get("actionId")));
        }

        Optional<RmdAction> actionDependsOn = actionService.findById(Long
                .valueOf(parameters.get("actionDependsOn") == null ? -1 : (Integer) parameters.get("actionDependsOn")));

        if (!actionDependsOn.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, RmdAction.class,
                    parameters.get("actionDependsOnId")));
        }

        String parameterDependsOn = (String) parameters.get("parameterDependsOn");

        RmdActionDependency actionDependency = new RmdActionDependencyFactory().create(DiscoveryModus.MANUAL,
                action.orElse(null), parameter.orElse(null), actionDependsOn.orElse(null), parameterDependsOn).build();

        List<String> violations = ValidatorUtil.getViolations(actionDependency);
        if (!violations.isEmpty()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_FAILED, RmdActionDependency.class,
                    violations.size()));
            return ValidatorUtil.getResponseForViolations(violations);
        }

        actionDependency = actionDependencyService.save(actionDependency);
        return ResponseEntity.ok(RmdActionDependencyMapper.toDto(actionDependency));
    }

    @RequestMapping(path = "dependencies/{actionDependencyId}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> deleteActionDependency(
            @PathVariable(name = "actionDependencyId") Long actionDependencyId) {
        Optional<RmdActionDependency> actionDependency = actionDependencyService.findById(actionDependencyId);

        if (!actionDependency.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, RmdActionDependency.class,
                    actionDependencyId));
            return ResponseEntity.badRequest().body(new RmdActionDependency());
        }

        actionDependencyService.deleteById(actionDependencyId);
        return ResponseEntity.ok(RmdActionDependencyMapper.toDto(actionDependency.get()));
    }

    @RequestMapping(path = "dependencies/count", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> countActionsDependenciesBySutId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "filter", required = false) String filter) {

        List<DiscoveryModus> discoveryModes = FilterUtil.getDiscoveryModes(filter);
        List<HttpMethod> httpMethods = FilterUtil.getHttpMethods(filter);
        String path = FilterUtil.getValueFromFilter(filter, FilterUtil.PATH);

        return ResponseEntity
                .ok(actionDependencyService.countByFilter(id, discoveryModes, httpMethods, FilterUtil.toLike(path)));
    }

    @RequestMapping(path = "dependencies/paginated", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findActionsDependenciesByFilter(@PathVariable(name = "id") Long id,
            @RequestParam(name = "curPage") int curPage, @RequestParam(name = "perPage") int perPage,
            @RequestParam(name = "filter", required = false) String filter) {

        List<DiscoveryModus> discoveryModes = FilterUtil.getDiscoveryModes(filter);
        List<HttpMethod> httpMethods = FilterUtil.getHttpMethods(filter);
        String path = FilterUtil.getValueFromFilter(filter, FilterUtil.PATH);

        return ResponseEntity.ok(RmdActionDependencyMapper.toDtos(actionDependencyService.findByFilter(id,
                discoveryModes, httpMethods, FilterUtil.toLike(path), FilterUtil.toPageRequest(curPage, perPage))));
    }
}