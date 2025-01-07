package com.sap.engine.services.dc.frame.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.List;

import com.sap.engine.boot.soft.CriticalOperationNotAlowedException;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.impl.PostOnlineDeployCallback;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.lock.DCLockManager;
import com.sap.engine.services.dc.cm.lock.DCLockManagerFactory;
import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.cm.server.spi.RestartServerService;
import com.sap.engine.services.dc.cm.server.spi.ServerMode;
import com.sap.engine.services.dc.cm.server.spi.RestartServerService.RestartServerServiceException;
import com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentException;
import com.sap.engine.services.dc.frame.ServiceStateProcessingException;
import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description: This class is reponsible for the
 * service state processing when the server is in safe mode ( 1 server node in
 * the whole cluster is up)
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-11
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.0
 * 
 */
public final class SafeServiceStateProcessorImpl extends
		AbstractServiceStateProcessorImpl {
	
	private Location location = DCLog.getLocation(this.getClass());

	public SafeServiceStateProcessorImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.frame.ServiceStateProcessor#stop()
	 */
	public void stop() {
		this.doStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.frame.impl.AbstractServiceStateProcessorImpl
	 * #getServerMode()
	 */
	protected ServerMode getServerMode() {
		return ServerMode.SAFE;
	}

	private void finalizeUndeployment(LockAction lockAction)
			throws ServiceStateProcessingException {

		String sessionId = this.undeploymentData.getSessionId();
		
		unlockDB(lockAction);
		unlockEnqueue(lockAction);
		gcDeploymentData();
		DCLog.Session.clear();

		boolean isSafetyWorkflowStrategy = UndeployWorkflowStrategy.SAFETY
				.equals(this.undeploymentData.getUndeployWorkflowStrategy());

		if (isSafetyWorkflowStrategy) {

			restartToPreviousMode(sessionId);

		} else {
			
			ServiceConfigurer.getInstance().exitCriticalOperation(sessionId);
			// the workflow is not safety. Just become WORKING
			DCManager.getInstance().setDCState(DCState.WORKING);

		}

	}

	private void finalizeDeployment(LockAction lockAction)
			throws ServiceStateProcessingException {

		boolean isSafetyWorkflowStrategy = DeployWorkflowStrategy.SAFETY
				.equals(this.deploymentData.getDeployWorkflowStrategy());
		List<DeploymentBatchItem> postOnlines = getPostOnlinesLeft();
		boolean postOnlinesLeft = postOnlines.size() > 0;
		String sessionId = this.deploymentData.getSessionId();
		
		if (!isSafetyWorkflowStrategy) {

			if (postOnlinesLeft) {

				DCLog
						.logWarning(
								location, 
								"ASJ.dpl_dc.004339",
								"There are post online items left but the workflow strategy is not safety and the server is in safe mode which means"
										+ " that there is no way to deploy these items in the post online phase. Scheduling the deployment now...");

				Runnable finishedCallback = new Runnable() {

					public void run() {

						if (location.bePath()) {
							tracePath(location, 
										"Post online deployment callback has been notified");
						}
					}

				};

				PostOnlineDeployCallback callback = new PostOnlineDeployCallback(
						postOnlines, this.deploymentData, finishedCallback);
				
				// process the post onlines synchronously
				callback.processPostOnlines();
			}

			// if the server is in safe mode but the workflow strategy is not
			// safety this means that
			// the server was in safe mode before the restart and there were
			// just some offline items
			// that caused the restart and we have to
			// delete the enq and db locks and start the node

			gcDeploymentData();
			unlockDB(lockAction);
			unlockEnqueue(lockAction);
			
			ServiceConfigurer.getInstance().exitCriticalOperation(sessionId);
			
			DCLog.Session.clear();
			
			
			DCManager.getInstance().setDCState(DCState.WORKING);
			return;

		}

		// assuming workflow stategy = safety
		if (!postOnlinesLeft) {

			gcDeploymentData();

			unlockDB(lockAction);
			unlockEnqueue(lockAction);

			DCLog.Session.clear();

		} else {
			DCLog
					.logInfo(
							location, 
							"ASJ.dpl_dc.001165",
							"The locks will not be deleted, because there are post online items left for deployment.");
		}

		// TODO: if it is implemented rolling update with safety workflow
		// stategy it should be restart only the instance in this place.
		restartToPreviousMode(sessionId);
		return;

	}

	private void restartToPreviousMode(String sessionId) throws ServiceStateProcessingException {

		final RestartServerService restartService = getRestartServerService();
		try {

			restartService.restartToPreviousMode(sessionId);
		} catch (RestartServerServiceException rsse) {
			throw new ServiceStateProcessingException(
					"ASJ.dpl_dc.0091 An error occurred while restarting the server.",
					rsse);
		}

	}

	private RestartServerService getRestartServerService()
			throws ServiceStateProcessingException {
		final Server server = ServerFactory.getInstance().createServer();
		final ServerService serverService = server
				.getServerService(ServerFactory.getInstance()
						.createRestartServerRequest());

		if (serverService == null
				|| !(serverService instanceof RestartServerService)) {
			final String errMsg = "ASJ.dpl_dc.003292 Received ServerService for restarting the server "
					+ "which is not of type RestartServerService.";
			throw new ServiceStateProcessingException(errMsg);
		}

		return (RestartServerService) serverService;
	}

	protected boolean isClusterInstanceApplicable()
			throws ServiceStateProcessingException {

		// in safe mode only one node on the instance that is the performer
		// should be started so the instance is
		// always applicable
		return true;
	}

	public synchronized void notifyMainRollingNode()
			throws ServiceStateProcessingException {
		// in safe mode - do nothing
	}

	@Override
	public void containerStarted() throws ServiceStateProcessingException {

		LockAction lockAction;
		try {
			lockAction = getLockAction();

		} catch (DCLockException e) {

			// if the lock action exist and is read by deploy service later on,
			// it will prevent the startup of the applications
			// The solution is to resolve the root cause of the problem and
			// restart the cluster
			logErrorThrowable(location, 
					null,
					"An error occured while obtaining the lock action. The operation cannot be performed.",
					e);
			DCManager.getInstance().setDCState(DCState.WORKING);
			return;
		}

		// In order to perform the operation we have a to have:
		// 1. A valid lock action ( i.e. Deploy or Undeploy )
		// 2. The runtime action is DEPLOY ( if it isn't probably we just have
		// leaked DB locks )
		// 	  

		if (lockAction == null) {

			// if the DB lock does not exist it doesn't matter for us what is
			// the runtime action
			if (location.beDebug()) {
				traceDebug(
						location,
						"Received event containerStarted and no locks exist.");
			}

			DCManager.getInstance().setDCState(DCState.WORKING);
			return;

		}

		if (!isLockActionValid(lockAction)) {

			// if the lock action is unknown we just clean up if possible and
			// start the node
			logError(
					location,
					"ASJ.dpl_dc.004341",
					"Unknown lock action [{0}]. The lock action will be deleted.",
					new Object[] { lockAction });
			unlockDB(lockAction);
			DCManager.getInstance().setDCState(DCState.WORKING);
			return;

		}

		// being here means that we have a valid lock action in the DB
		this.handleContainerStarted(lockAction); // the logic here is
		// responsible for setting
		// the state of DCManager to
		// WORKING

	}

	@Override
	public void handleRegisterDC() {
		DCLog.logError( 
				location,
				"ASJ.dpl_dc.004343",
				"No other nodes should exist in safe mode.");

	}

	/**
	 * 
	 * Calling this method means that there is an operation (deployment or
	 * undeployment ) that has to be done after the restart
	 * 
	 * @param _lockAction
	 * @throws ServiceStateProcessingException
	 */
	private void handleContainerStarted(final LockAction lockAction)
			throws ServiceStateProcessingException {

		final DCLockManager dcLockManager = DCLockManagerFactory.getInstance()
				.createDCLockManager();
		try {
			dcLockManager.lockEnqueue(lockAction);

		} catch (DCLockException e) {

			// in safe mode there should be just one server node and there
			// should never be a problem getting this lock
			// under normal circumstances
			logErrorThrowable(location, 
					null,
					"There was a problem obtaining an enqueue lock. No operation will be performed",
					e);

			unlockDB(lockAction);
			DCManager.getInstance().setDCState(DCState.WORKING);
			return;
		}

		// we locked the enqueue successfully now we can proceed

		if (LockAction.DEPLOY.equals(lockAction)) {

			try {
				loadDeploymentData();
			} catch (DeploymentException e) {
				// just log the error and set deploy controller in working state

				logErrorThrowable(location, null,
						"An error occurred while loading the deployment data.",
						e);

				unlockDB(lockAction);
				unlockEnqueue(lockAction);
				DCManager.getInstance().setDCState(DCState.WORKING);
				return;

			}
			
			String sessionId = this.deploymentData.getSessionId();
			try {
				ServiceConfigurer.getInstance().enterCriticalOperation(sessionId);
				
			} catch (CriticalOperationNotAlowedException e1) {
				// if shutdown is already in progress don't do anything. The operation shall be
				// completed after the restart
				throw new ServiceStateProcessingException("Cannot enter critical operation." +
						" Deployment operation will not be started ", e1);
			}
			
			try {
					
		    	  DCManager.getInstance().setDCState( DCState.DEPLOYING );
		    	  
		    	  DCLog.Session.begin( sessionId );
		    	  deploy();
				  
			} catch(OutOfMemoryError e) { // OOM, ThreadDeath and Internal error are not consumed					
				throw e;
				
			} catch (ThreadDeath e) {				
				throw e;
				
			} catch (InternalError e) {			
				throw e;
				
			} catch(Throwable t) {
				DCLog.logErrorThrowable(location, "ASJ.dpl_dc.001035", "Unexpected problem occured while processing the container started event.", t);
			}finally {
				  finalizeDeployment(lockAction);
				  
			}  

		} else if (LockAction.UNDEPLOY.equals(lockAction)) {

			try {
				loadUndeploymentData();
			} catch (UndeploymentException e) {
				// just log the error and set deploy controller in working state

				logErrorThrowable(location, 
						null,
						"An error occurred while "
								+ "executing the logic for post processing in safe mode.",
						e);

				unlockDB(lockAction);
				unlockEnqueue(lockAction);
				DCManager.getInstance().setDCState(DCState.WORKING);
				return;
			}

			String sessionId = this.undeploymentData.getSessionId();
	    	try {
				ServiceConfigurer.getInstance().enterCriticalOperation(sessionId);
			} catch (CriticalOperationNotAlowedException e1) {
				// if shutdown is already in progress don't do anything. The operation shall be
				// completed after the restart
				throw new ServiceStateProcessingException("Cannot start critical operation." +
						" Undeployment operation will not be started ", e1);
			}
			
			try {

				DCManager.getInstance().setDCState(DCState.UNDEPLOYING);
				

				
				undeploy();

			} catch (UndeploymentException e) {
				throw new ServiceStateProcessingException(
						"ASJ.dpl_dc.003293 An error occurred while "
								+ "executing the logic for post processing in safe mode.",
						e);
			} finally {
				finalizeUndeployment(lockAction);
			}

		}

	}

}
