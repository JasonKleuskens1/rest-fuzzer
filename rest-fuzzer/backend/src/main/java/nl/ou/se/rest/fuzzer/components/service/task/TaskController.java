package nl.ou.se.rest.fuzzer.components.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.se.rest.fuzzer.components.data.task.dao.TaskService;
import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;
import nl.ou.se.rest.fuzzer.components.extractor.ExtractorTask;
import nl.ou.se.rest.fuzzer.components.fuzzer.type.FuzzerTask;
import nl.ou.se.rest.fuzzer.components.reporter.ReporterTask;
import nl.ou.se.rest.fuzzer.components.service.rmd.domain.RmdSutDto;
import nl.ou.se.rest.fuzzer.components.service.task.domain.TaskDto;
import nl.ou.se.rest.fuzzer.components.service.task.mapper.TaskMapper;
import nl.ou.se.rest.fuzzer.components.service.util.ValidatorUtil;
import nl.ou.se.rest.fuzzer.components.shared.Constants;
import nl.ou.se.rest.fuzzer.components.shared.FilterUtil;

@RestController()
@RequestMapping("/rest/tasks")
public class TaskController {

    // variable(s)
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static final String EXTRACTOR = "extractor";
    private static final String FUZZER = "fuzzer";
    private static final String REPORTER = "reporter";
    private static final int MAX_TASKS_ENDED_SPECIFIC = 250;

    @Autowired
    TaskService taskService;

    // method(s)
    @RequestMapping(path = "active", method = RequestMethod.GET)
    public @ResponseBody List<TaskDto> findAllActive() {
        List<Task> tasks = new ArrayList<>();
        tasks = taskService.findQueuedAndRunning();
        return TaskMapper.toDtos(tasks);
    }

    @RequestMapping(path = "archive/count", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findArchivedCount() {
        Long count = taskService.countEnded();
        return ResponseEntity.ok().body(count);
    }

    @RequestMapping(path = "archive", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findArchived(@RequestParam(name = "curPage") int curPage,
            @RequestParam(name = "perPage") int perPage) {
        return ResponseEntity.ok(TaskMapper.toDtos(taskService.findEnded(FilterUtil.toPageRequest(curPage, perPage))));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        Optional<Task> task = taskService.findById(id);

        if (!task.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, Task.class, id));
            return ResponseEntity.badRequest().body(new TaskDto());
        }

        return ResponseEntity.ok(TaskMapper.toDto(task.get()));
    }

    @RequestMapping(path = "recent/suts/{sut_id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> recentForSut(@PathVariable(value = "sut_id") Long sutId) {
        List<Task> tasks = taskService.findQueuedAndRunning();
        tasks.addAll(taskService.findEnded(PageRequest.of(0, MAX_TASKS_ENDED_SPECIFIC)));
        tasks = tasks.stream().filter(t -> t.isForSut(sutId)).collect(Collectors.toList());

        return ResponseEntity.ok().body(TaskMapper.toDtos(tasks));
    }

    @RequestMapping(path = "recent/projects/{project_id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> recentForProject(@PathVariable(value = "project_id") Long projectId) {
        List<Task> tasks = taskService.findQueuedAndRunning();
        tasks.addAll(taskService.findEnded(PageRequest.of(0, MAX_TASKS_ENDED_SPECIFIC)));
        tasks = tasks.stream().filter(t -> t.isForProject(projectId)).collect(Collectors.toList());

        return ResponseEntity.ok().body(TaskMapper.toDtos(tasks));
    }

    @RequestMapping(path = "running_or_queued/suts/{sut_id}/count", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> runningOrQueuedForSutCount(@PathVariable(value = "sut_id") Long sutId) {
        List<Task> tasks = taskService.findQueuedAndRunning();
        Long count = tasks.stream().filter(t -> t.isForSut(sutId)).count();

        return ResponseEntity.ok().body(count);
    }

    @RequestMapping(path = "running_or_queued/projects/{project_id}/count", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> runningOrQueuedForProjectCount(
            @PathVariable(value = "project_id") Long projectId) {
        List<Task> tasks = taskService.findQueuedAndRunning();
        Long count = tasks.stream().filter(t -> t.isForProject(projectId)).count();

        return ResponseEntity.ok().body(count);
    }

    @RequestMapping(path = "/{name}/start", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> add(@PathVariable(value = "name") String name,
            @RequestBody Map<String, Object> metaDataTuples) {
        Task task = null;

        switch (name) {
        case EXTRACTOR:
            task = new Task(ExtractorTask.class.getCanonicalName());
            break;
        case FUZZER:
            task = new Task(FuzzerTask.class.getCanonicalName());
            break;
        case REPORTER:
            task = new Task(ReporterTask.class.getCanonicalName());
            break;
        default:
            return ValidatorUtil.getResponseForViolation("Unkown task name.");
        }

        task.setMetaDataTuples(metaDataTuples);
        taskService.save(task);

        return ResponseEntity.ok().body(TaskMapper.toDto(task));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Optional<Task> task = taskService.findById(id);

        if (!task.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, Task.class, id));
            return ResponseEntity.badRequest().body(new RmdSutDto());
        }

        taskService.deleteById(id);

        return ResponseEntity.ok(TaskMapper.toDto(task.get()));
    }
}