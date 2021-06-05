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
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzSequenceService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzSequenceStatus;
import nl.ou.se.rest.fuzzer.components.service.fuz.mapper.FuzProjectMapper;
import nl.ou.se.rest.fuzzer.components.service.fuz.mapper.FuzSequenceMapper;
import nl.ou.se.rest.fuzzer.components.shared.FilterUtil;

@RestController()
@RequestMapping("/rest/projects")
public class FuzSequenceController {

    // variable(s)
    @Autowired
    FuzProjectService projectService;

    @Autowired
    FuzSequenceService sequenceService;

    // method(s)
    @RequestMapping(path = "{id}/sequences", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findSequencesByProjectId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "curPage") int curPage, @RequestParam(name = "perPage") int perPage,
            @RequestParam(name = "filter", required = false) String filter) {

        List<FuzSequenceStatus> statuses = FilterUtil.getStatuses(filter);
        List<Integer> lengths = FilterUtil.getLengths(filter);
        String filterId = FilterUtil.getValueFromFilter(filter, FilterUtil.ID);

        return ResponseEntity.ok(FuzSequenceMapper.toDtos(sequenceService.findByProjectId(id, statuses, lengths,
                FilterUtil.toLike(filterId), FilterUtil.toPageRequest(curPage, perPage))));
    }

    @RequestMapping(path = "{id}/sequences/count", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> countSequencesByProjectId(@PathVariable(name = "id") Long id,
            @RequestParam(name = "filter", required = false) String filter) {

        List<FuzSequenceStatus> statuses = FilterUtil.getStatuses(filter);
        List<Integer> lengths = FilterUtil.getLengths(filter);
        String filterId = FilterUtil.getValueFromFilter(filter, FilterUtil.ID);

        return ResponseEntity.ok(sequenceService.countByProjectId(id, statuses, lengths, FilterUtil.toLike(filterId)));
    }

    @Transactional
    @RequestMapping(path = "{id}/sequences", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> deleteSequencesByProjectId(@PathVariable(name = "id") Long id) {
        Optional<FuzProject> project = projectService.findById(id);
        sequenceService.deleteByProjectId(id);
        return ResponseEntity.ok(FuzProjectMapper.toDto(project.get(), false));
    }
}