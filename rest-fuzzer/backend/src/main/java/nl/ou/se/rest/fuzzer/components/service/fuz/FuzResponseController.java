package nl.ou.se.rest.fuzzer.components.service.fuz;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzProjectService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzResponseService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;
import nl.ou.se.rest.fuzzer.components.service.fuz.mapper.FuzProjectMapper;
import nl.ou.se.rest.fuzzer.components.service.fuz.mapper.FuzResponseMapper;
import nl.ou.se.rest.fuzzer.components.shared.FilterUtil;

@RestController()
@RequestMapping("/rest/projects")
public class FuzResponseController {

    // variable(s)
    @Autowired
    private FuzProjectService projectService;

    @Autowired
    private FuzResponseService responseService;

    // method(s)
    @RequestMapping(path = "{id}/responses", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findResponsesByProjectId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "curPage") int curPage, @RequestParam(name = "perPage") int perPage,
            @RequestParam(name = "filter", required = false) String filter) {

        List<HttpMethod> httpMethods = FilterUtil.getHttpMethods(filter);
        List<Integer> statusCodes = FilterUtil
                .getHttpResponseCodes(responseService.findUniqueStatusCodesForProject(id), filter);
        String path = FilterUtil.getValueFromFilter(filter, FilterUtil.PATH);

        return ResponseEntity.ok(FuzResponseMapper.toDtos(responseService.findByFilter(id, httpMethods, statusCodes,
                FilterUtil.toLike(path), FilterUtil.toPageRequest(curPage, perPage))));
    }

    @RequestMapping(path = "{id}/responses/count", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> countResponsesByProjectId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "filter", required = false) String filter) {

        List<HttpMethod> httpMethods = FilterUtil.getHttpMethods(filter);
        List<Integer> statusCodes = FilterUtil.getHttpResponseCodes(responseService.findUniqueStatusCodesForProject(id), filter);
        String path = FilterUtil.getValueFromFilter(filter, FilterUtil.PATH);

        return ResponseEntity.ok(responseService.countByFilter(id, httpMethods, statusCodes, FilterUtil.toLike(path)));
    }

    @Transactional
    @RequestMapping(path = "{id}/responses", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> deleteResponsesByProjectId(@PathVariable(name = "id") Long id) {
        Optional<FuzProject> project = projectService.findById(id);
        responseService.deleteByProjectId(id);
        return ResponseEntity.ok(FuzProjectMapper.toDto(project.get(), false));
    }
}