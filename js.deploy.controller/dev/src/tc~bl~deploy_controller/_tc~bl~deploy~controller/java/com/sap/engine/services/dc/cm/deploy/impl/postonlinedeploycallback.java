package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.List;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.services.dc.cm.deploy.DeployFactory;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.SafeModeDeployer;
import com.sap.engine.services.dc.cm.utils.EngineThreadUtil;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.engine.services.deploy.DeployCallback;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.deploy.container.ProgressEvent;
import com.sap.tc.logging.Location;

/**
 * This callback is notified when Deploy Service starts all the applications.
 * When a notification is received a thread is started that will poll the engine
 * state until the engine is RUNNING. Then the post online deployment process
 * will take place.
 * 
 * @author I040924
 * 
 */
public class PostOnlineDeployCallback implements DeployCallback {

	private  final Location location = DCLog.getLocation(this.getClass());
	
	private DeploymentData deploymentData;
	private List<DeploymentBatchItem> postOnlines;
	private Runnable finishedCallback;

	/**
	 * 
	 * @param postOnlines
	 *            the post online items sorted in order of deployment
	 * @param deploymentData
	 *            the whole deployment data
	 * @param finishedCallback
	 *            this callback shold be notified when the post online
	 *            deployment is finished, so the state of the DCManager is
	 *            updated and the other nodes are notified
	 */
	public PostOnlineDeployCallback(List<DeploymentBatchItem> postOnlines,
			DeploymentData deploymentData, Runnable finishedCallback) {

		checkForNull(deploymentData, "deploymentData");
		checkForNull(postOnlines, "postOnlines");
		checkForNull(finishedCallback, "finishedCallback");

		this.deploymentData = deploymentData;
		this.postOnlines = postOnlines;
		this.finishedCallback = finishedCallback;
	}

	private void checkForNull(Object arg, String name) {
		if (arg == null) {
			throw new IllegalArgumentException("Argument " + name
					+ " cannot be null.");
		}
	}

	public void processApplicationEvent(DeployEvent event) {

		// the event of interest (apps are started)
		if (event.getActionType() == DeployEvent.INITIAL_START_APPLICATIONS
				&& event.getAction() == DeployEvent.LOCAL_ACTION_FINISH) {

			DCLog
					.logInfo(location, "ASJ.dpl_dc.001094",
							"Received an event from DS that the applications are started.");
			startPollingEngineState();

		}
	}

	/**
	 * Call this method externally only if the post online deployment should be
	 * scheduled for execution at another phase in case of an unexpected
	 * situation
	 * 
	 */
	public void processPostOnlines() {
		try {
			DCLog.logInfo(location, "ASJ.dpl_dc.001095",
					"Starting deployment of post online items");
			deploy();
			DCLog.logInfo(location,  "ASJ.dpl_dc.001096",
					"Post online deployment finished");
		} finally {
			// restore the state of the DC manager and notify the other nodes
			// upon completion
			finishedCallback.run();
		}
	}

	private void deploy() {

		SafeModeDeployer deployer = null;
		try {

			deployer = DeployFactory.getInstance().createSafeModeDeployer(
					this.deploymentData);

		} catch (DeploymentException e) {
			DCLog
					.logErrorThrowable(location, 
							null,
							"An error occured during the post online deployment while getting deployer.",
							e);
			return; // this exception is only thrown when the deployment data is
			// null
		}

		try {
			deployer.deployPostOnlineData(this.postOnlines);
		} catch (DeploymentException e) {
			DCLog
					.logErrorThrowable(location, 
							null,
							"An error occured while deploying the post online items",
							e);
		}
	}

	private void startPollingEngineState() {

		Runnable engineStateSniffer = new Runnable() {

			public void run() {

				ApplicationServiceContext appServiceContext = ServiceConfigurer
						.getInstance().getApplicationServiceContext();
				ClusterElement participant = appServiceContext
						.getClusterContext().getClusterMonitor()
						.getCurrentParticipant();

				// wait until the engine state becomes running
				while (participant.getRealState() != ClusterElement.RUNNING) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						if (location.beDebug()) {
							traceDebug(location, 
									"Engine state polling interrupted");
						}
						// TODO decide if we should exit the loop when
						// inerrupted
					}
				}
				// deploy the post onlines
				processPostOnlines();

			}

		};

		String userId = PostOnlineDeployCallback.this.deploymentData
				.getUserUniqueId();
		String taskName = "[Deploy Controller] - poll for server state running in order to schedule the post online items";
		String threadName = "Deploy controller poller thread";

		EngineThreadUtil.executeInAuthorizedApplicationThreadAsync(
				engineStateSniffer, taskName, threadName, userId);
	}

	public void callbackLost(String arg0) {
	}

	public void processContainerEvent(ProgressEvent arg0) {
	}

	public void processInterfaceEvent(DeployEvent arg0) {
	}

	public void processLibraryEvent(DeployEvent arg0) {
	}

	public void processReferenceEvent(DeployEvent arg0) {
	}

	public void processServiceEvent(DeployEvent arg0) {
	}

	public void processStandaloneModuleEvent(DeployEvent arg0) {
	}

	public void serverAdded(String arg0) {
	}

}
