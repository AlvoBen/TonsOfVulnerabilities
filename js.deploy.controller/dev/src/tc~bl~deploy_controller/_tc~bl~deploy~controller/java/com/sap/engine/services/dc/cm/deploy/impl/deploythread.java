package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.concurrent.CountDownLatch;

import com.sap.engine.services.dc.cm.deploy.DeployListenersList;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.util.ThreadUtil;
import com.sap.engine.services.dc.util.logging.DCLog;

class DeployThread implements Runnable {

	private final DeploymentBatchItem deploymentBatchItem;
	private final DeploymentParallelTraverser parallelTraverser;
	private final AbstractDeployProcessor deployProcessor;
	private final DeploymentData deploymentData;
	private final DeployListenersList deployListenersList;
	private final CountDownLatch maxThreadDumpsCount;

	DeployThread(final DeploymentBatchItem deploymentBatchItem,
			final DeploymentParallelTraverser parallelTraverser,			
			final AbstractDeployProcessor deployProcessor,
			final DeploymentData deploymentData,
			final DeployListenersList deployListenersList,
			final CountDownLatch maxThreadDumpsCount) {
		this.deploymentBatchItem = deploymentBatchItem;
		this.parallelTraverser = parallelTraverser;		
		this.deployProcessor = deployProcessor;
		this.deploymentData = deploymentData;
		this.deployListenersList = deployListenersList;
		this.maxThreadDumpsCount = maxThreadDumpsCount;
	}

	public void run() {
		DeploymentException deploymentException = null;
		try {
			DCLog.Session.begin(deploymentData.getSessionId());
			deployProcessor.deploy(deploymentBatchItem, deploymentData,
					this.deployListenersList);
		} catch (DeploymentException de) {
			deploymentException = de;

			ThreadUtil.getThreadDump4ConccurentIssue(deploymentException,
					maxThreadDumpsCount, this.deploymentBatchItem);
		} catch (OutOfMemoryError e) { // OOM, ThreadDeath and Internal error
			// are not consumed
			throw e;

		} catch (ThreadDeath e) {
			throw e;

		} catch (InternalError e) {
			throw e;

		} catch (Throwable t) { // catch all the rest of the throwables and wrap
			// them in a deployment exception to improve
			// error reporting
			deploymentException = new DeploymentException(
					"[ERROR CODE DPL.DC.3475] Unexpected throwable occured during the deployment operation",
					t);
			ThreadUtil.getThreadDump4ConccurentIssue(deploymentException,
					maxThreadDumpsCount, this.deploymentBatchItem);
		} finally {
			parallelTraverser.notifyPerformed(deploymentBatchItem,
					deploymentException);
			DCLog.Session.clear();
		}
	}

}
