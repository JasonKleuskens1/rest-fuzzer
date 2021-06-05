package nl.ou.se.rest.fuzzer.components.fuzzer.type;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.task.dao.TaskService;
import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;

@Service
public class FuzzerBase {
    
    @Autowired
    private TaskService taskService;

    protected void saveTaskProgress(Task task, int count, int total) {
        BigDecimal progress = BigDecimal.valueOf(count).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        if (task.getProgress() == null || task.getProgress().compareTo(progress) < 0) {
            task.setProgress(progress);
            taskService.save(task);
        }
    }
}