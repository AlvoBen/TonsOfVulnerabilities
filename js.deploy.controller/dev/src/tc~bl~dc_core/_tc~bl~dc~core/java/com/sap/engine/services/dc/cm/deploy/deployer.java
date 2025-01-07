package com.sap.engine.services.dc.cm.deploy;

import java.rmi.Remote;

import com.sap.engine.services.dc.cm.*;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.utils.filters.BatchFilter;
import com.sap.engine.services.dc.event.ClusterListener;
import com.sap.engine.services.dc.event.DeploymentListener;
import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.ListenerMode;

/**
 * 
 * Title: Software Deployment Manager Description: The class consists of
 * operations for deploying SDUs (SDA/SCA). It also delivers operations for
 * setup the deployment process, like setting deployment filters, the error
 * handling strategy, etc. The deployment process is separate on the following
 * sub processes:
 * <ol>
 * <li>Loading the specified archives;
 * <li>Checking (validating) the archives;
 * <li>Delivering the archives;
 * <li>Starting the archives;
 * </ol>
 * 
 * As the deployer is part of the Component Manager it communicates with the
 * following components:
 * <ol>
 * <li>Repository - the component is used in order to deliver an access
 * (read/write) to the storage where the Deploy Controller is storing its data;
 * <li>Generic Delivery - the component is used for the actual deployment of a
 * component to the J2EE containers;
 * <li>LCM (Life Cycle Manager) - the component is responsible for the starting
 * and stopping of the applications;
 * </ol>
 * 
 * Before performing the deployment the following setups are possible:
 * <ol>
 * <li>Add component filter - by adding a filter one could specify the only
 * components with a specific software type have to be deployed. All the other
 * components will be removed from the list with the deployments.
 * <li>Set an error handling strategy - by setting a specific error handling
 * strategy, the clients are able to decide whether the system has to continue
 * with the deployments in case of error or not.
 * <li>Set a version handling rule - by setting the rule, the clients are able
 * control the deployment processes in way that no component with wrong version
 * will be deployed.
 * </ol>
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-18
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface Deployer extends Remote {

	/**
	 * The operation loads specified archives, checks, delivers and starts them.
	 * If there are filters set, they are applied after loading the archives.
	 * The version handling rule and error handling strategies are taking place
	 * by the time of the steps: loading, checking, delivering and starting.
	 * 
	 * @param archiveFilePathNames
	 *            <code>String []</code> consists of the absolute file paths for
	 *            the archives selected for deployment.
	 * @return <code>DeployResut</code> which specifies whether the deployment
	 *         process was successful or not.
	 * @throws DeploymentException
	 *             in case there are problems the process which are performed by
	 *             the time of deployment. The throwing of the exception depends
	 *             on the error handling strategy.
	 * @throws ValidationException
	 *             in case there are specified archives which are not correct.
	 *             The throwing of the exception depends on the error handling
	 *             strategy.
	 * @throws DCLockException
	 *             in case another (un)deployment has already been started.
	 * 
	 * @see com.sap.engine.services.dc.cm.ErrorStrategy
	 */
	public DeployResult deploy(String[] archiveFilePathNames, String sessionId)
			throws ValidationException, DeploymentException, DCLockException;

	// /**
	// * The operation checks, delivers and starts the deployment items which
	// the
	// * specified batch contains.
	// * If there are filters set, they are applied after loading the archives.
	// * The version handling rule and error handling strategies are taking
	// place by
	// * the time of the steps: loading, checking, delivering and starting.
	// * @param deploymentBatch <code>DeploymentBatch</code> consists of the
	// * deployment items which has to be deployed.
	// * @return <code>DeployResut</code> which specifies whether the deployment
	// process
	// * was successful or not.
	// * @throws DeploymentException in case there are problems the process
	// which are performed
	// * by the time of deployment. The throwing of the exception depends on the
	// * error handling strategy.
	// * @throws ValidationException in case there are specified archives
	// * which are not correct. The throwing of the exception depends on the
	// * error handling strategy.
	// * @throws DCLockException in case another (un)deployment has already been
	// started.
	// *
	// * @see com.sap.engine.services.dc.cm.ErrorStrategy
	// */
	// public DeployResult deploy(DeploymentBatch deploymentBatch, String
	// sessionId)
	// throws ValidationException, DeploymentException, DCLockException;

	/**
	 * @return the version handling rule.
	 * @see com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule
	 */
	public ComponentVersionHandlingRule getComponentVersionHandlingRule();

	/**
	 * Enables/disables time statistics during the deployment. default value is
	 * disabled( false )
	 * 
	 * @param enabled
	 */
	public void enableTimeStats(boolean enabled);

	/**
	 * Retrieves whether the Time statistics are switched on or not. Default
	 * value is disabled( false )
	 * 
	 * @return true - if enabled otherwise false
	 */
	public boolean getTimeStatsEnabled();

	/**
	 * Set the version handling rule.
	 * 
	 * @param rule
	 *            <code>ComponentVersionHandlingRule</code> specifies the
	 *            version handling rule which is going to be used by the time of
	 *            deployment.
	 * @see com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule
	 */
	public void setComponentVersionHandlingRule(
			ComponentVersionHandlingRule rule);

	/**
	 * @param errorStrategyAction
	 *            <code>ErrorStrategyAction</code> specifies the error strategy
	 *            action.
	 * @return the error strategy which is mapped to the specified action.
	 * @see com.sap.engine.services.dc.cm.ErrorStrategy
	 * @see com.sap.engine.services.dc.cm.ErrorStrategyAction
	 */
	public ErrorStrategy getErrorStrategy(
			ErrorStrategyAction errorStrategyAction);

	/**
	 * Maps the specified error strategy with the specified action.
	 * 
	 * @param errorStrategyAction
	 *            the error action.
	 * @param stategy
	 *            the error strategy.
	 * @see com.sap.engine.services.dc.cm.ErrorStrategy
	 * @see com.sap.engine.services.dc.cm.ErrorStrategyAction
	 */
	public void setErrorStrategy(ErrorStrategyAction errorStrategyAction,
			ErrorStrategy stategy);

	/**
	 * @return <code>DeployWorkflowStrategy</code> which is set. By default it
	 *         is <code>DeployWorkflowStrategy.NORMAL</code>.
	 */
	public DeployWorkflowStrategy getDeployWorkflowStrategy();

	/**
	 * Sets the deployment workflow strategy which has to be applied.
	 * 
	 * @param workflowStrategy
	 *            the <code>DeployWorkflowStrategy</code> to be set.
	 */
	public void setDeployWorkflowStrategy(
			DeployWorkflowStrategy workflowStrategy);

	/**
	 * @return <code>LifeCycleDeployStrategy</code> which is set. By default it
	 *         is <code>LifeCycleDeployStrategy.BULK</code>.
	 */
	public LifeCycleDeployStrategy getLifeCycleDeployStrategy();

	/**
	 * Sets the life cycle deployment strategy which has to be applied.
	 * 
	 * @param lifeCycleDeployStrategy
	 *            the <code>LifeCycleDeployStrategy</code> to be set.
	 */
	public void setLifeCycleDeployStrategy(
			LifeCycleDeployStrategy lifeCycleDeployStrategy);

	/**
	 * Adds the specified filter to the filters container. By the time of
	 * deployment all the filters within the container are applied in order to
	 * check whether each of the archives is admitted to be deployed. So, the
	 * specified filter will be applied.
	 * 
	 * @param batchFilter
	 *            <code>BatchFilter</code> specified the filter.
	 */
	public void addBatchFilter(BatchFilter batchFilter);

	/**
	 * Removes the specified filter from the filters container. By the time of
	 * deployment all the filters within the container are applied in order to
	 * check whether each of the archives is admitted to be deployed. So, the
	 * specified filter will not be applied.
	 * 
	 * @param batchFilter
	 *            <code>BatchFilter</code> specified the filter.
	 */
	public void removeBatchFilter(BatchFilter batchFilter);

	/**
	 * Attaches an observer to the deployer. The deployer invokes the attached
	 * observers after deployment has been performed. The added observer will be
	 * lost in case of server restart, which is done in case of offline
	 * deployment.
	 * 
	 * @param observer
	 *            <code>DeploymentObserver</code> specifying the concrete
	 *            observer.
	 */
	public void addObserver(DeploymentObserver observer);

	/**
	 * Detaches an observer from the deployer.
	 * 
	 * @param observer
	 *            <code>DeploymentObserver</code> specifying the concrete
	 *            observer.
	 */
	public void removeObserver(DeploymentObserver observer);

	/**
	 * Obtains the <code>DeployResut</code> for the specified session ID.
	 * 
	 * @param sessionId
	 *            <code>String</code>
	 * @param synchListener
	 *            asynchronous deployment lister in order to receive deployment
	 *            events during the online phase after the offline deployment
	 * @param asynchListener
	 *            synchronous deployment lister in order to receive deployment
	 *            events during the online phase after the offline deployment
	 * @return <code>DeployResut</code> which specifies whether the deployment
	 *         process was successful or not.
	 * @throws DeploymentException
	 *             in case the result cannot be obtained.
	 * @throws DeployResultNotFoundException
	 *             in case there is no <code>DeployResut</code>
	 * @see com.sap.engine.services.dc.cm.deploy.Deployer#getDeployResult(String)
	 * 
	 * @deprecated the client api now supports reconnection of the deployment
	 *             listeners which makes this method unnecessary
	 */
	public DeployResult getDeployResult(String sessionId,
			DeploymentListener synchListener, DeploymentListener asynchListener)
			throws DeploymentException, DeployResultNotFoundException;

	/**
	 * Obtains the <code>DeployResut</code> for the specified session ID.
	 * 
	 * @param sessionId
	 *            <code>String</code>
	 * @return <code>DeployResut</code> which specifies whether the deployment
	 *         process was successful or not.
	 * @throws DeploymentException
	 *             in case the result cannot be obtained.
	 * @throws DeployResultNotFoundException
	 *             in case there is no <code>DeployResut</code> for given
	 *             session ID.
	 */
	public DeployResult getDeployResult(String sessionId)
			throws DeploymentException, DeployResultNotFoundException;

	/**
	 * This command is mostly for debug purposes
	 * 
	 * @return list with all available offline deployment transaction IDs
	 * @throws DeploymentException
	 */
	public String[] getOfflineDeployTransactionIDs() throws DeploymentException;

	/**
	 * The operation validates the specified array with archive file paths and
	 * returns a <code>ValidationResult</code> which specifies generally whether
	 * the archives are successfully validate or not. The
	 * <code>ValidationStatus</code> of the returned
	 * <code>ValidationResult</code> is <code>SUCCESS</code> if all the items
	 * are admitted for deployment and <code>ERROR</code> in other cases.
	 * Additionally, the result contains the following information:
	 * <ol>
	 * <li>Whether the Engine should be restarted in case a deployment with the
	 * specified archives is performed;
	 * <li>All the sorted and admitted for deployment items;
	 * <li>All the items corresponding to the specified archives.
	 * </ol>
	 * 
	 * @param archiveFilePathNames
	 *            <code>String []</code> with the archives which have to be
	 *            validated.
	 * @return <code>ValidationResult</code>
	 * @throws ValidationException
	 *             in case the validation could not be performed or there are
	 *             invalid archives and the <code>Deployer</code>'s error
	 *             handling strategy is <code>ErrorStrategy.ON_ERROR_STOP</code>
	 *             . Additionally, the exception will be thrown in case all the
	 *             archives are not admitted for deployment, regardless of the
	 *             error handling strategy. Therefore, when the error handling
	 *             strategy is <code>ErrorStrategy.ON_ERROR_STOP</code> throwing
	 *             a <code>DeploymentException</code> could mean that the
	 *             specified archives are not correct;
	 */
	public ValidationResult validate(String[] archiveFilePathNames,
			String sessionId) throws ValidationException;

	/**
	 * Adds the specified <code>DeploymentListener</code> to the list with all
	 * the other listeners.
	 * 
	 * @param listener
	 *            <code>DeploymentListener</code> which will be triggered on
	 *            specific deploy events like pre and post deploy.
	 * @param listenerMode
	 *            specifies whether the listener will 'listen' for all the
	 *            deploy events which happen on the cluster or only for the ones
	 *            which are triggered by the current Deployer.
	 * @param eventMode
	 *            specifies whether the events will be synchronous or
	 *            asynchronous.
	 */
	public void addDeploymentListener(DeploymentListener listener,
			ListenerMode listenerMode, EventMode eventMode);

	/**
	 * Removes the specified <code>DeploymentListener</code>.
	 * 
	 * @param listener
	 *            <code>DeploymentListener</code> which has to be removed from
	 *            the list with registered listeners.
	 */
	public void removeDeploymentListener(DeploymentListener listener);

	/**
	 * Adds the specified <code>ClusterListener</code> to the list with all the
	 * other listeners.
	 * 
	 * @param listener
	 *            <code>ClusterListener</code> which will be triggered on
	 *            specific deploy events like restart cluster due to 'offline'
	 *            deployment.
	 * @param listenerMode
	 *            specifies whether the listener will 'listen' for all the
	 *            deploy events which happen on the cluster or only for the ones
	 *            which are triggered by the current Deployer.
	 * @param eventMode
	 *            specifies whether the events will be synchronous or
	 *            asynchronous.
	 */
	public void addClusterListener(ClusterListener listener,
			ListenerMode listenerMode, EventMode eventMode);

	/**
	 * Removes the specified <code>ClusterListener</code>.
	 * 
	 * @param listener
	 *            <code>ClusterListener</code> which has to be removed from the
	 *            list with registered listeners.
	 */
	public void removeClusterListener(ClusterListener listener);

	public DeployResult commit(String transactionId)
			throws DeploymentException, DCLockException, RollingDeployException;

	public DeployResult rollback(String transactionId,
			String[] archiveFilePathNames) throws ValidationException,
			DeploymentException, DCLockException, RollingDeployException;

	public void setOnlineDeployemtOfCoreComponents(boolean value);
}
