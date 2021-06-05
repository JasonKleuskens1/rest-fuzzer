package nl.ou.se.rest.fuzzer.components.service.rmd;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdParameterService;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;
import nl.ou.se.rest.fuzzer.components.service.rmd.mapper.RmdActionMapper;
import nl.ou.se.rest.fuzzer.components.service.rmd.mapper.RmdParameterMapper;
import nl.ou.se.rest.fuzzer.components.shared.FilterUtil;

@RestController()
@RequestMapping("/rest/suts/{id}/actions")
public class RmdActionController {

    // variable(s)
    @Autowired
    private RmdActionService actionService;

    @Autowired
    private RmdParameterService parameterService;

    // method(s)
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findAllActionsBySutId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "filter", required = false) String path) {
        return ResponseEntity.ok(RmdActionMapper.toDtos(actionService.findBySutId(id)));
    }

    @RequestMapping(path = "{actionId}/parameters", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findAllParametersBySutIdAndActionId(@PathVariable(name = "id") Long id,
            @PathVariable(name = "actionId") Long actionId) {
        return ResponseEntity.ok(RmdParameterMapper.toDtos(parameterService.findByActionId(actionId), false));
    }

    @RequestMapping(path = "count", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> countActionsBySutId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "filter", required = false) String filter) {

        List<HttpMethod> httpMethods = FilterUtil.getHttpMethods(filter);
        String path = FilterUtil.getValueFromFilter(filter, FilterUtil.PATH);

        return ResponseEntity.ok(actionService.countByFilter(id, httpMethods, FilterUtil.toLike(path)));
    }

    @RequestMapping(path = "paginated", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findActionsBySutId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "curPage") int curPage, @RequestParam(name = "perPage") int perPage,
            @RequestParam(name = "filter", required = false) String filter) {

        List<HttpMethod> httpMethods = FilterUtil.getHttpMethods(filter);
        String path = FilterUtil.getValueFromFilter(filter, FilterUtil.PATH);

        return ResponseEntity.ok(RmdActionMapper.toDtos(actionService.findByFilter(id, httpMethods,
                FilterUtil.toLike(path), FilterUtil.toPageRequest(curPage, perPage))));
    }
}