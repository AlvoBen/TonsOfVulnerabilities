/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.library_container.deploy;

import com.sap.engine.system.ThreadWrapper;

/**
 *@author I039168 Accessor class for transparent usage of ThreadWrapper for
 *         pushing and popping tasks in and out of the monitored threads pool.
 *         Access to ThreadWrapper is transparent to the user even in cases when
 *         ThreadWrapper class is not available for usage
 */
public class LCThreadWrapperAccessor {

	private static LCThreadWrapperAccessor instance = new LCThreadWrapperAccessor();
	private static final String DISABLE_MONITORING_PROP = "com.sap.engine.disable.monitoring";
	private boolean threadWrapperAvailable = false;

	// MMC message fragments
	private final static String baseMsg = "[" + LibraryContainer.CONTAINER_NAME
			+ "] provided by service [";
	private final static String baseMsg2 = "] for component [";
	private final static String leftBracket = "[";
	private final static String rightBracket = "] ";

	LCThreadWrapperAccessor() {
		threadWrapperAvailable = !(Boolean.getBoolean(DISABLE_MONITORING_PROP));
	}

	/**
	 * @return LCThreadWrapperAccessor instance (can never be <code>null</code>)
	 */
	public static LCThreadWrapperAccessor getInstance() {
		return instance;
	}

	/**
	 * Current task is pushed among the monitored threads (it becomes visible in
	 * "AS Java Threads" section of SAP MMC). Must be coupled with the usage of
	 * the popTask() method in a try-finally construction, pushTask() being
	 * invoked in the try block and popTask() - in the finally block.
	 * 
	 * @param operation
	 *            operation type (cannot be <code>null</null>)
	 * @param componentName
	 *            component Name (cannot be <code>null</null>)
	 * @param contentHanlder
	 *            name of the content handler for which the operation is invoked
	 *            (cannot be <code>null</null>)
	 */
	public void pushTask(String operation, String componentName) {
		if (threadWrapperAvailable) {
			// "[container] by service [service name] [operation type] for component [component name]"
			StringBuilder msg = new StringBuilder(baseMsg);
			msg.append(LibraryContainer.getLCServiceName())
					.append(rightBracket).append(leftBracket).append(operation)
					.append(baseMsg2).append(componentName)
					.append(rightBracket);
			ThreadWrapper.pushTask(msg.toString(), ThreadWrapper.TS_PROCESSING);
		}
	}

	/**
	 * Removes the current task from the monitored threads pool. Must be coupled
	 * with the usage of the pushTask() method in a try-finally construction,
	 * pushTask() being invoked in the try block and popTask() - in the finally
	 * block.
	 */
	public void popTask() {
		if (threadWrapperAvailable) {
			ThreadWrapper.popTask();
		}
	}
}
