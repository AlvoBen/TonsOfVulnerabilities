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
package com.sap.engine.services.deploy.server.prl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.tc.logging.Location;

/**
 * Class that starts/stops application during start or stop of the engine.
 * 
 * @author Assia Djambazova
 * 
 */
public class ParallelApplicationThread implements Runnable {

	private static final Location location = 
		Location.getLocation(ParallelApplicationThread.class);	

	private Component appName;
	@SuppressWarnings("unchecked")
	private Set callbackMonitor;
	private ParallelTraverser traverse;
	private List<String> errors = new ArrayList<String>();
	private final int id;

	@SuppressWarnings("unchecked")
	public ParallelApplicationThread(int id, Set callbackMonitor,
			ParallelTraverser traverse) {
		this.callbackMonitor = callbackMonitor;
		this.id = id;
		this.traverse = traverse;
		if (location.beDebug()) {
			DSLog.traceDebug(
								location, 
								"[{0}] was created successfully",
								traverse.getThreadName() + " " + id);
		}
	}

	public void run() {
		try {
			boolean isOperationSuccessful = false;
			while ((appName = traverse.next()) != null) {
				isOperationSuccessful = false;
				if (location.beDebug()) {
					DSLog.traceDebug(
									location, 
							"The next application in the traverser is [{0}]",
							appName);
				}
				final TransactionTimeStat timeStat = TransactionTimeStat
						.createIfNotAvailable(traverse.getThreadName(), appName
								.getName());
				final String tagName = traverse.getThreadName()
						+ appName.getName();
				try {
					Accounting.beginMeasure(tagName, this.getClass());
					if (location.beDebug()) {
						DSLog.traceDebug(location, 
										"Will perform operation with application [{0}]]",
										appName);
					}
					isOperationSuccessful = traverse.execute(appName);
				} catch (DeploymentException dex) {
					String opName;
					if(traverse.getThreadName().equals(DeployConstants.DEPLOY_PARALLEL_STOP_THREAD_NAME)){
						opName = "final stop";
					}else{
						opName = "initial start";
					}
					
                                        DSLog
						.logError(location,
							"ASJ.dpl_ds.006391",
							"Error occurred during [{0}] of application [{1}]: [{2}]",  
							new Object[]{opName, appName.toString(), dex.getMessage()});
					DSLog
						.traceError(location,
							"ASJ.dpl_ds.000338",
							"Error occurred during [{0}] of application [{1}] ", dex, 
							new Object[]{opName, appName.toString()});
				} catch (OutOfMemoryError oofmer) {
					throw oofmer;
				} catch (ThreadDeath td) {
					throw td;
				} finally {
					Accounting.endMeasure(tagName);
					if (isOperationSuccessful) {
						traverse.success(appName);
					} else {
						traverse.fail(appName);
					}
					timeStat.finish();
				}
			}
		} finally {
			synchronized (callbackMonitor) {
				if (location.beDebug()) {
					DSLog.traceDebug(location, 
									"[{0}] will notify all threads waiting on [{2}] monitor",
									traverse.getThreadName() + " " + id,
									callbackMonitor);
				}
				callbackMonitor.remove(this);
				callbackMonitor.notifyAll();
			}
		}
	}
}
