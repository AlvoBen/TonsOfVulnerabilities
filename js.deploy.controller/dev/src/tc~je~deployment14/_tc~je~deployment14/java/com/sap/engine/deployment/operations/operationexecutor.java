package com.sap.engine.deployment.operations;

import com.sap.engine.frame.core.thread.execution.Executor;

/**
 * Executes an operation (deploy, start etc.) in a new thread by the server pool
 * if the application is running on the server side or by starting a new
 * java.lang.Thread if the application is running outside the server.
 * 
 * @author Radoslav Popov
 */
public class OperationExecutor {

	private static Executor engineThreadSystem = null;

	public static void execute(Runnable runnable) {

		if (null == engineThreadSystem) {
			new Thread(runnable).start();
			return;
		}
		engineThreadSystem.execute(runnable);

	}

	public static synchronized void setEngineThreadSystem(Executor executor) {
		engineThreadSystem = executor;
	}

}