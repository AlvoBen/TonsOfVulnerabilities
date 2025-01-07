package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import static com.sap.engine.services.deploy.server.utils.DSConstants.*;

import com.sap.engine.system.ThreadWrapper;

/**
 * Class used to describe a given thread.
 */
final class ThreadDescriptor {
	private final long id;
	private final String name;
	private final String task;
	private final String subTask;
	
	private ThreadDescriptor(final Thread thread,
		final String task, final String subTask) {
		this.id = thread.getId();
		this.name = thread.getName();
		this.task = task;
		this.subTask = subTask;
	}
	
	
	/**
	 * Factory method that creates descriptor for the current thread.
	 * @return a descriptor for the current thread.
	 */
	protected static ThreadDescriptor create() {
		return new ThreadDescriptor(Thread.currentThread(),
			ThreadWrapper.getTaskName(), ThreadWrapper.getSubTaskName());
	}

	String getName() {
		return name;
	}
	
	long getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		ThreadDescriptor other = (ThreadDescriptor)obj;
		return this.id == other.id;
	}
	
	@Override
	public int hashCode() {
		return (int)id;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name).append(EOL_TAB)
			.append("id:      ").append(id).append(EOL_TAB)
			.append("task:    ").append(task).append(EOL_TAB)
			.append("subtask: ").append(subTask);
		return sb.toString();
	}
}