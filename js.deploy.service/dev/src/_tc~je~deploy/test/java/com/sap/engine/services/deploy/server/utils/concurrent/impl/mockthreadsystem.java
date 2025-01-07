package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.Task;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.core.thread.execution.Executor;

public class MockThreadSystem implements ThreadSystem {
	public Executor createCleanThreadExecutor(String arg0, int arg1, int arg2,
			byte arg3) {
		return null;
	}

	public Executor createExecutor(String arg0, int arg1, int arg2) {
		return null;
	}

	public Executor createExecutor(String arg0, int arg1, int arg2, byte arg3) {
		return null;
	}

	public void destroyExecutor(Executor arg0) {
		// empty
	}

	public void executeInDedicatedThread(Runnable arg0, String arg1) {
		// empty
	}

	public void executeInDedicatedThread(Runnable arg0, String arg1, String arg2) {
		// empty
	}

	public int getContextObjectId(String arg0) {
		return 0;
	}

	public ThreadContext getThreadContext() {
		return null;
	}

	public int registerContextObject(String arg0, ContextObject arg1) {
		return 0;
	}

	public void startCleanThread(Runnable arg0, boolean arg1) {
		// empty
	}

	public void startCleanThread(Task arg0, boolean arg1) {
		// empty
	}

	public void startCleanThread(Runnable arg0, boolean arg1, boolean arg2) {
		// empty
	}

	public void startCleanThread(Task arg0, boolean arg1, boolean arg2) {
		// empty
	}

	public void startTask(Task arg0, boolean arg1) {
		// empty
	}

	public void startTask(Task arg0, long arg1) {
		// empty
	}

	public void startThread(Runnable arg0, boolean arg1) {
		// empty
	}

	public void startThread(Runnable arg0, boolean arg1, boolean arg2) {
		// empty
	}

	public void startThread(Runnable arg0, String arg1, String arg2,
		boolean arg3) {
		// empty
	}

	public void startThread(Task arg0, String arg1, String arg2, boolean arg3) {
		// empty
	}

	public void startThread(Task arg0, String arg1, String arg2, long arg3) {
		// empty
	}

	public void startThread(Runnable arg0, String arg1, String arg2,
		boolean arg3, boolean arg4) {
		// empty
	}

	public void unregisterContextObject(String arg0) {
		// empty
	}
}