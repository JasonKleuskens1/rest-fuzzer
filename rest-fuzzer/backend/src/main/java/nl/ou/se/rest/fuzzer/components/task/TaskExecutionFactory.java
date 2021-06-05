package nl.ou.se.rest.fuzzer.components.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;
import nl.ou.se.rest.fuzzer.components.extractor.ExtractorTask;
import nl.ou.se.rest.fuzzer.components.fuzzer.type.FuzzerTask;
import nl.ou.se.rest.fuzzer.components.reporter.ReporterTask;
import nl.ou.se.rest.fuzzer.components.shared.Constants;

@Service
public class TaskExecutionFactory {

	// variable(s)
	private Task task;

	@Autowired
	private ExtractorTask extractorTask;

	@Autowired
	private FuzzerTask fuzzerTask;
	
	@Autowired
	private ReporterTask reporterTask;

	private String executionName;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	public TaskExecutionFactory create(String executionName) {
		this.executionName = executionName;
		return this;
	}

	public TaskExecutionFactory setTask(Task task) {
		this.task = task;
		return this;
	}

	public TaskExecution build() {
		TaskExecution taskExecution = null;

		if (ExtractorTask.class.getCanonicalName().equals(executionName)) {
			taskExecution = extractorTask;
		} else if (FuzzerTask.class.getCanonicalName().equals(executionName)) {
			taskExecution = fuzzerTask;
        } else if (ReporterTask.class.getCanonicalName().equals(executionName)) {
            taskExecution = reporterTask;
		} else {
			logger.error(String.format(Constants.Task.EXECUTION_FACTORY_UNKNOWN, executionName));
			return null;
		}

		taskExecution.setTask(this.task);

		return taskExecution;
	}
}