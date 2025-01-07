package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.security.PrivilegedAction;
import java.util.concurrent.CountDownLatch;

import javax.security.auth.Subject;

import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.util.ThreadUtil;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

class PostDeployThread implements Runnable {
	private  final Location location = DCLog.getLocation(this.getClass());

	private final DeploymentBatchItem deploymentBatchItem;
	private final DeploymentParallelTraverser parallelTraverser;
	private final DeploymentData deploymentData;
	private final Subject userSubject;
	private final CountDownLatch maxThreadDumpsCount;

	PostDeployThread(final DeploymentBatchItem deploymentBatchItem,
			final DeploymentParallelTraverser deploymentsEnum,
			final DeploymentData deploymentData, final Subject userSubject,
			final CountDownLatch maxThreadDumpsCount) {
		this.userSubject = userSubject;
		this.deploymentBatchItem = deploymentBatchItem;
		this.parallelTraverser = deploymentsEnum;
		this.deploymentData = deploymentData;
		this.maxThreadDumpsCount = maxThreadDumpsCount;
	}

	public void run() {
		final PrivilegedAction postDeployPrivilegedAction = getPostDeployPrivilegedAction();

		Subject.doAs(userSubject, postDeployPrivilegedAction);
	}

	private PrivilegedAction getPostDeployPrivilegedAction() {
		return new PrivilegedAction() {
			public Object run() {
				deploy();
				return null;
			}
		};
	}

	private void deploy() {
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());

		DeploymentException deploymentException = null;
		try {
			DCLog.Session.begin(deploymentData.getSessionId());

			if (location.beDebug()) {
				traceDebug(location,
						"PostDeployThread.run().enter: [{0}]",
						new Object[] { deploymentBatchItem.getBatchItemId() });
			}

			final AbstractDeplStatusSafeDeplProcessor deplStatusDeplProcessor = DeplStatusSafeDeplProcessorMapper
					.getInstance().map(
							deploymentBatchItem.getDeploymentStatus());

			deplStatusDeplProcessor
					.process(deploymentBatchItem, deploymentData);
			if (location.bePath()) {
				tracePath(location, 
						"PostDeployThread.run() after process: [{0}]",
						new Object[] { deploymentBatchItem.getBatchItemId() });
			}
		} catch (final DeploymentException de) {
			if (location.beWarning()) {
				traceWarning(location, "ASJ.dpl_dc.001155",
						"PostDeployThread.run() exception is thrown: [{0}]",
						new Object[] { deploymentBatchItem.getBatchItemId() });
			}
			
			deploymentException = de;

			ThreadUtil.getThreadDump4ConccurentIssue(deploymentException, maxThreadDumpsCount,
					this.deploymentBatchItem);
		} catch (OutOfMemoryError e) { // OOM, ThreadDeath and Internal error
			// are not consumed
			throw e;

		} catch (ThreadDeath e) {
			throw e;

		} catch (InternalError e) {
			throw e;

		} catch (Throwable t) { // // catch all the rest of the throwables and
			// wrap them in a deployment exception to
			// improve error reporting
			deploymentException = new DeploymentException(
					"[ERROR CODE DPL.DC.3475] Unexpected throwable occured during the deployment operation",
					t);

			ThreadUtil.getThreadDump4ConccurentIssue(deploymentException,  maxThreadDumpsCount,
					this.deploymentBatchItem);
		} finally {
			if (location.beDebug()) {
				traceDebug(location, 
						"PostDeployThread.run() finally - will notify: [{0}]",
						new Object[] { deploymentBatchItem.getBatchItemId() });
			}
			parallelTraverser.notifyPerformed(deploymentBatchItem,
					deploymentException);
			if (location.bePath()) {
				tracePath(location, 
						"PostDeployThread.run() finally - will notify: [{0}]",
						new Object[] { deploymentBatchItem.getBatchItemId() });
			}

			DCLog.Session.clear();
		}
	}

}
