package nl.ou.se.rest.fuzzer;

import org.springframework.beans.factory.annotation.Autowired;

import nl.ou.se.rest.fuzzer.components.data.task.dao.TaskService;
import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;
import nl.ou.se.rest.fuzzer.components.extractor.ExtractorTask;

public class ApplicationTest {

	@Autowired
	private TaskService taskService;
	
	public void insertJobs() {
		Task task = new Task(ExtractorTask.class.getCanonicalName());
		taskService.save(task);
	}
	
	public static void main(String[] args) {
		ApplicationTest at = new ApplicationTest();
		at.insertJobs();
	}
}