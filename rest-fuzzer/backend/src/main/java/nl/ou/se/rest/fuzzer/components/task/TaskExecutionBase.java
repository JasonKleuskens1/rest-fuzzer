package nl.ou.se.rest.fuzzer.components.task;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;
import nl.ou.se.rest.fuzzer.components.shared.Constants;

public class TaskExecutionBase {

	// variable(s)
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private Task task;

	// method(s)
	public void logStart(String className) {
		logger.info(String.format(Constants.Task.START, className));
	}

	public void logStop(String className) {
		logger.info(String.format(Constants.Task.STOP, className));
	}

	public Object getValueForKey(Class<?> clazz, String key) {
		if (!this.getTask().getMetaDataTuples().containsKey(key)) {
			logger.warn(String.format(Constants.Task.VALUE_FOR_KEY_NOT_PRESENT, this.getClass().getName(), key));
			return null;
		}

		return this.getTask().getMetaDataTuples().get(key);
	}

	public boolean isOptionalPresent(Class<?> clazz, Optional<?> optional, Object id) {
		if (!optional.isPresent()) {
			logger.warn(String.format(Constants.Task.OBJECT_NOT_FOUND, clazz.getClass().getName(), id));
			return false;
		}
		return true;
	}

	// getters and setters
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}