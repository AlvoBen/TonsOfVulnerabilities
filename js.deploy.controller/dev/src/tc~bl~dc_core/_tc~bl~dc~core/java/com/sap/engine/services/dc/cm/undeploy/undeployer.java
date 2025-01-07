package com.sap.engine.services.dc.cm.undeploy;

import java.rmi.Remote;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.ErrorStrategyAction;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.event.ClusterListener;
import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.ListenerMode;
import com.sap.engine.services.dc.event.UndeploymentListener;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-18
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public interface Undeployer extends Remote {

	public UndeployResult undeploy(GenericUndeployItem[] undeployItems,
			String sessionId) throws UndeploymentException, DCLockException;
	
	public UndeployResult undeploy(UndeployItem[] undeployItems,
			String sessionId) throws UndeploymentException, DCLockException;	

	public void setUndeploymentStrategy(
			UndeploymentStrategy undeploymentStrategy);

	public UndeploymentStrategy getUndeploymentStrategy();

	public void setUndeployWorkflowStrategy(
			UndeployWorkflowStrategy workflowStrategy);

	public UndeployWorkflowStrategy getUndeployWorkflowStrategy();

	/**
	 * @param errorStrategyType
	 *            <code>Integer</code> specifies the error strategy type.
	 * @return the error strategy which is mapped to the specified type.
	 * @see com.sap.engine.services.dc.cm.ErrorStrategy
	 * @see com.sap.engine.services.dc.cm.ErrorStrategies
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
	 * Attaches an observer to the undeployer. The undeployer invokes the
	 * attached observers after an undeployment has been performed.
	 * 
	 * @param observer
	 *            <code>UndeploymentObserver</code> specifying the concrete
	 *            observer.
	 */
	public void addObserver(UndeploymentObserver observer);

	/**
	 * Detaches an observer from the undeployer.
	 * 
	 * @param observer
	 *            <code>UndeploymentObserver</code> specifying the concrete
	 *            observer.
	 */
	public void removeObserver(UndeploymentObserver observer);

	/**
	 * @return a list with all availabel offline undeploy transaction ids
	 * @throws UndeploymentException
	 */
	public String[] getOfflineUndeployTransactionIDs()
			throws UndeploymentException;

	/**
	 * Obtains the <code>UndeployResut</code> for the specified session ID.
	 * 
	 * @param sessionId
	 *            <code>String</code>
	 * @return <code>UndeployResut</code> which specifies whether the
	 *         undeployment process was successful or not.
	 * @throws UndeploymentException
	 *             in case the result cannot be obtained.
	 * @throws UndeployResultNotFoundException
	 *             in case there is no <code>UndeployResut</code> for given
	 *             session ID.
	 */
	public UndeployResult getUndeployResult(String sessionId)
			throws UndeploymentException, UndeployResultNotFoundException;

	/**
	 * Adds the specified <code>UndeploymentListener</code> to the list with all
	 * the other listeners.
	 * 
	 * @param listener
	 *            <code>UndeploymentListener</code> which will be triggered on
	 *            specific undeploy events like pre and post undeploy.
	 * @param listenerMode
	 *            specifies whether the listener will 'listen' for all the
	 *            undeploy events which happen on the cluster or only for the
	 *            ones which are triggered by the current Undeployer.
	 * @param eventMode
	 *            specifies whether the events will be synchronous or
	 *            asynchronous.
	 */
	public void addUndeploymentListener(UndeploymentListener listener,
			ListenerMode listenerMode, EventMode eventMode);

	/**
	 * Removes the specified <code>UndeploymentListener</code>.
	 * 
	 * @param listener
	 *            <code>UndeploymentListener</code> which has to be removed from
	 *            the list with registered listeners.
	 */
	public void removeUndeploymentListener(UndeploymentListener listener);

	/**
	 * Adds the specified <code>ClusterListener</code> to the list with all the
	 * other listeners.
	 * 
	 * @param listener
	 *            <code>ClusterListener</code> which will be triggered on
	 *            specific undeploy events like restart cluster due to 'offline'
	 *            undeployment.
	 * @param listenerMode
	 *            specifies whether the listener will 'listen' for all the
	 *            undeploy events which happen on the cluster or only for the
	 *            ones which are triggered by the current Undeployer.
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

	public void setOnlineDeployemtOfCoreComponents(boolean value);

}
