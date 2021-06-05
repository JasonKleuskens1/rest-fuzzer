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

import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdParameterService;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterContext;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterType;
import nl.ou.se.rest.fuzzer.components.service.rmd.mapper.RmdParameterMapper;
import nl.ou.se.rest.fuzzer.components.shared.FilterUtil;

@RestController()
@RequestMapping("/rest/suts/{id}/parameters")
public class RmdParametersController {

    // variable(s)
    @Autowired
    private RmdParameterService parameterService;

    // method(s)
    @RequestMapping(path = "count", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> countActionsBySutId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "filter", required = false) String filter) {

        List<ParameterContext> parameterContexts = FilterUtil.getParameterContexts(filter);
        List<ParameterType> parameterTypes = FilterUtil.getParameterTypes(filter);
        String name = FilterUtil.getValueFromFilter(filter, FilterUtil.NAME);

        return ResponseEntity
                .ok(parameterService.countByFilter(id, parameterContexts, parameterTypes, FilterUtil.toLike(name)));
    }

    @RequestMapping(path = "paginated", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findActionsBySutId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "curPage") int curPage, @RequestParam(name = "perPage") int perPage,
            @RequestParam(name = "filter", required = false) String filter) {

        List<ParameterContext> parameterContexts = FilterUtil.getParameterContexts(filter);
        List<ParameterType> parameterTypes = FilterUtil.getParameterTypes(filter);
        String name = FilterUtil.getValueFromFilter(filter, FilterUtil.NAME);

        return ResponseEntity.ok(RmdParameterMapper.toDtos(parameterService.findByFilter(id, parameterContexts,
                parameterTypes, FilterUtil.toLike(name), FilterUtil.toPageRequest(curPage, perPage)), true));
    }
}