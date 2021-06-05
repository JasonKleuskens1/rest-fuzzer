package nl.ou.se.rest.fuzzer.components.task;

import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;

public interface TaskExecution {

	public void execute();
	
	public void setTask(Task task);
	public Task getTask();

}