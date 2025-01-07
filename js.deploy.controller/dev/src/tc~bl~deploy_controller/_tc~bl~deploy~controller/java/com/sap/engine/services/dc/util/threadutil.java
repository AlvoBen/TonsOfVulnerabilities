package com.sap.engine.services.dc.util;

import java.util.concurrent.CountDownLatch;

import com.sap.engine.frame.ProcessEnvironment;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.system.ThreadWrapper;

public final class ThreadUtil {

	private ThreadUtil() {
	}

	public static void pushTask(final String taskMessage) {
		ThreadWrapper.pushTask(taskMessage, ThreadWrapper.TS_PROCESSING);
	}

	public static void pushTask(final String operation, final String name,
			final String vendor) {
		final String taskMessage = evaluateTaskMessage(operation, name, vendor);
		pushTask(taskMessage);
	}

	public static void popTask() {
		ThreadWrapper.popTask();
	}

	public static String evaluateTaskMessage(final String operation,
			final String name, final String vendor) {
		final StringBuilder taskMessage = new StringBuilder(
				"[Deploy Controller] - performing [");
		taskMessage.append(operation);
		taskMessage.append("] operation of component [");
		taskMessage.append(vendor);
		taskMessage.append("/");
		taskMessage.append(name);
		taskMessage.append("]");
		return taskMessage.toString();
	}

	public static void getThreadDump4ConccurentIssue(DeploymentException de,
			final CountDownLatch maxThreadDumpsCount,
			final DeploymentBatchItem deploymentBatchItem) {
		//not enough precise and synchronized check - but that is ok
		if (maxThreadDumpsCount.getCount() > 0 && couldBeConcurrencyIssue(de)) {
			maxThreadDumpsCount.countDown();
			final String dumpMsg = "Possible concurrency issue related to item ["
					+ deploymentBatchItem.getBatchItemId() + "].";
			deploymentBatchItem.addDescription(
							"[ASJ.dpl_dc.006005] The reason for nested issue could be concurrency one in components involved in exception stack trace.\r\n"
							+ "Hint: 1) Search in [std_server.out] file in work dir for message ["
							+ dumpMsg + "] or thread dumps.");
			ProcessEnvironment.getThreadDump(dumpMsg);
		}		
	}

	private static boolean couldBeConcurrencyIssue(DeploymentException de) {
		if (de == null) {
			return false;
		}

		Throwable cause = de;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}

		if (cause != null) {
			final String causeName = cause.getClass().getName();
			if (causeName.startsWith("java")) {
				return true;
			}
		}
		return false;
	}
}
