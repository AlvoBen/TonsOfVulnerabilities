package com.sap.engine.services.dc.api.undeploy;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.ServiceNotAvailableException;
import com.sap.engine.services.dc.api.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.api.event.ClusterListener;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.ListenerMode;
import com.sap.engine.services.dc.api.event.UndeploymentListener;
import com.sap.engine.services.dc.api.lock_mng.AlreadyLockedException;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The goal is to remove an SDU or set of SDUs from the J2EE Engine.<br>
 * The undeployment process is basically the same as the deployment one. It
 * consists of:
 * <UL>
 * <LI>Creating and setting an UndeployProcessor</LI>
 * <LI>Creating a software presentation of the SDU archive that will be
 * undeployed.</LI>
 * <LI>Undeployment activities</LI>
 * <LI>Receiving a result. It could be:
 * <UL>
 * <LI>exception - indicates that some of the methods of the deployment
 * activities has returned with an error.</LI>
 * <LI>an UndeployItemResult</LI>
 * </UL>
 * <LI>Undeployment exceptions</LI>
 * </UL>
 * </DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public interface UndeployProcessor {
	/**
	 * undeploys array of given <code>UndeployItem</code> s regarding
	 * <code>UndeploymentStrategy</code> and <code>ErrorStrategy</code>.
	 * 
	 * @param undeployItems
	 *            array with items to be undeployed
	 * @return @throws APIConnectionException
	 * @throws UndeployResultNotFoundException
	 * @throws EngineTimeoutException
	 *             in case there is no server response after the defined timeout
	 * @throws UndeployException
	 * @throws ConnectionException
	 *             connection is broken
	 * @throws AlreadyLockedException
	 *             the deploy controller has already locked for deploy/undeploy
	 *             operation
	 */
	public UndeployResult undeploy(UndeployItem[] undeployItems)
			throws ConnectionException, UndeployResultNotFoundException,
			EngineTimeoutException, AlreadyLockedException, UndeployException;

	/**
	 * Returns list with all available offline undeploy transaction IDs.
	 * 
	 * @return list with all available offline undeploy transaction IDs
	 * 
	 * @throws UndeployException
	 * @throws ConnectionException
	 * @throws ServiceNotAvailableException
	 */
	public String[] getOfflineUndeployTransactionIDs()
			throws UndeployException, ConnectionException,
			ServiceNotAvailableException;

	/**
	 * Returns the obtained result from previous undeployment.
	 * 
	 * @param transactionId
	 *            the session ID for which you would like to get the result
	 * @return result
	 * @throws UndeployResultNotFoundException
	 *             thrown if the result is not found. Results are normally kept
	 *             at most 24 hours or until the next offline undeployment
	 * @throws UndeployException
	 *             in case of other error
	 */
	public UndeployResult getUndeployResultById(String transactionId)
			throws UndeployResultNotFoundException, UndeployException;

	/**
	 * Returns the last undeployment transaction id or null if none.
	 * 
	 * @return transaction id
	 */
	public String getLastUndeploymentTransactionId();

	/**
	 * Sets the undeployment strategy rule.
	 * 
	 * @param undeploymentStrategy
	 *            undeployment strategy
	 * @see #getUndeploymentStrategy
	 */
	public void setUndeploymentStrategy(
			UndeploymentStrategy undeploymentStrategy);

	/**
	 * Returns the undeployment strategy rule.
	 * 
	 * @return undeployment strategy rule
	 * @see #setUndeploymentStrategy
	 */
	public UndeploymentStrategy getUndeploymentStrategy();

	/**
	 * Sets the workflow strategy.
	 * 
	 * @param workflowStrategy
	 *            undeployment workflow strategy
	 * @see #getUndeployWorkflowStrategy
	 */
	public void setUndeployWorkflowStrategy(
			UndeployWorkflowStrategy workflowStrategy);

	/**
	 * Returns the undeploy workflow strategy.
	 * 
	 * @return <code>UndeployWorkflowStrategy</code>
	 * @see #setUndeployWorkflowStrategy
	 */
	public UndeployWorkflowStrategy getUndeployWorkflowStrategy();

	/**
	 * Returns the error stategy which is mapped to the specified type.
	 * 
	 * @param errorStrategyAction
	 *            <code>ErrorStrategyAction</code> specifies the error strategy
	 *            action.
	 * @return error stategy
	 * @see ErrorStrategy
	 * @see ErrorStrategyAction
	 * @see #setErrorStrategy
	 */
	public ErrorStrategy getErrorStrategy(
			ErrorStrategyAction errorStrategyAction);

	/**
	 * Maps the specified error strategy with the specified type.
	 * 
	 * @param errorStrategyType
	 *            the error type.
	 * @param stategy
	 *            the error strategy.
	 * @see ErrorStrategy
	 * @see ErrorStrategyAction
	 * @see #getErrorStrategy
	 */
	public void setErrorStrategy(ErrorStrategyAction errorStrategyType,
			ErrorStrategy stategy);

	/**
	 * Creates a <code>SdaUndeployItem</code>. The two arguments
	 * <code>vendor</code> and <code>name</code> are used to identify the
	 * component (development or software component) of the deployment that
	 * should be undeployed.
	 * 
	 * Thus any deployment that belongs to the same component will be undeployed
	 * not matter what the current version (location/counter) of the deployment
	 * is.
	 * 
	 * @param vendor
	 *            the vendor of the component to be undeployed
	 * @param name
	 *            the name of the component to be undeployed
	 * @return a <code>UndeployItem</code>
	 * @throws IllegalArgumentException
	 *             if <code>vendor</code> or <code>name</code> was null
	 * @deprecated Please use {@link #createSdaUndeployItem(String, String)}
	 */
	public abstract UndeployItem createUndeployItem(String name, String vendor);

	/**
	 * Creates a <code>SdaUndeployItem</code>. The four arguments
	 * <code>vendor</code>,<code>name</code>,<code>location</code> and
	 * <code>version</code> are used to identify the component (development or
	 * software component) of the deployment that should be undeployed.
	 * 
	 * @param vendor
	 *            the vendor of the component to be undeployed
	 * @param name
	 *            the name of the component to be undeployed
	 * @param location
	 *            the location of the component to be undeployed
	 * @param version
	 *            the version of the component to be undeployed
	 * @return a <code>UndeployItem</code>
	 * @throws IllegalArgumentException
	 *             if <code>vendor</code>,<code>name</code>,
	 *             <code>location</code> or <code>version</code> was null or
	 *             <code>version</code> had a wrong format.
	 * @deprecated Please use {@link #createSdaUndeployItem(String, String, String, String)}
	 */
	public abstract UndeployItem createUndeployItem(String name, String vendor,
			String location, String version);

	/**
	 * Creates a <code>SdaUndeployItem</code>. The two arguments
	 * <code>vendor</code> and <code>name</code> are used to identify the
	 * component (development or software component) of the deployment that
	 * should be undeployed.
	 * 
	 * Thus any deployment that belongs to the same component will be undeployed
	 * not matter what the current version (location/counter) of the deployment
	 * is.
	 * 
	 * @param vendor
	 *            the vendor of the component to be undeployed
	 * @param name
	 *            the name of the component to be undeployed
	 * @return a <code>UndeployItem</code>
	 * @throws IllegalArgumentException
	 *             if <code>vendor</code> or <code>name</code> was null
	 */
	public abstract SdaUndeployItem createSdaUndeployItem(String name, String vendor);

	/**
	 * Creates a <code>SdaUndeployItem</code>. The four arguments
	 * <code>vendor</code>,<code>name</code>,<code>location</code> and
	 * <code>version</code> are used to identify the component (development or
	 * software component) of the deployment that should be undeployed.
	 * 
	 * @param vendor
	 *            the vendor of the component to be undeployed
	 * @param name
	 *            the name of the component to be undeployed
	 * @param location
	 *            the location of the component to be undeployed
	 * @param version
	 *            the version of the component to be undeployed
	 * @return a <code>UndeployItem</code>
	 * @throws IllegalArgumentException
	 *             if <code>vendor</code>,<code>name</code>,
	 *             <code>location</code> or <code>version</code> was null or
	 *             <code>version</code> had a wrong format.
	 */
	public abstract SdaUndeployItem createSdaUndeployItem(String name, String vendor,
			String location, String version);
	
	/**
	 * Creates a <code>ScaUndeployItem</code>. The two arguments
	 * <code>vendor</code> and <code>name</code> are used to identify the
	 * component (development or software component) of the deployment that
	 * should be undeployed.
	 * 
	 * Thus any deployment that belongs to the same component will be undeployed
	 * not matter what the current version (location/counter) of the deployment
	 * is.
	 * 
	 * @param vendor
	 *            the vendor of the component to be undeployed
	 * @param name
	 *            the name of the component to be undeployed
	 * @return a <code>UndeployItem</code>
	 * @throws IllegalArgumentException
	 *             if <code>vendor</code> or <code>name</code> was null
	 */
	public abstract ScaUndeployItem createScaUndeployItem(String name, String vendor);

	/**
	 * Creates a <code>ScaUndeployItem</code>. The four arguments
	 * <code>vendor</code>,<code>name</code>,<code>location</code> and
	 * <code>version</code> are used to identify the component (development or
	 * software component) of the deployment that should be undeployed.
	 * 
	 * @param vendor
	 *            the vendor of the component to be undeployed
	 * @param name
	 *            the name of the component to be undeployed
	 * @param location
	 *            the location of the component to be undeployed
	 * @param version
	 *            the version of the component to be undeployed
	 * @return a <code>UndeployItem</code>
	 * @throws IllegalArgumentException
	 *             if <code>vendor</code>,<code>name</code>,
	 *             <code>location</code> or <code>version</code> was null or
	 *             <code>version</code> had a wrong format.
	 */
	public abstract ScaUndeployItem createScaUndeployItem(String name, String vendor,
			String location, String version);

	
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
	 *            specifies whether the events will be synchroneous or
	 *            asynchroneous.
	 * @see #removeUndeploymentListener
	 */
	public void addUndeploymentListener(UndeploymentListener listener,
			ListenerMode listenerMode, EventMode eventMode);

	/**
	 * Removes the specified <code>UndeploymentListener</code>.
	 * 
	 * @param listener
	 *            <code>UndeploymentListener</code> which has to be removed from
	 *            the list with registered listeners.
	 * @see #addUndeploymentListener
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
	 *            specifies whether the events will be synchroneous or
	 *            asynchroneous.
	 * @see #removeClusterListener
	 */
	public void addClusterListener(ClusterListener listener,
			ListenerMode listenerMode, EventMode eventMode);

	/**
	 * Removes the specified <code>ClusterListener</code>.
	 * 
	 * @param listener
	 *            <code>ClusterListener</code> which has to be removed from the
	 *            list with registered listeners.
	 * @see #addClusterListener
	 */
	public void removeClusterListener(ClusterListener listener);

	/**
	 * Sets new timeout for the current deploy processor.
	 * 
	 * @param newTimeout
	 *            new timeout in milliseconds. If the new value is -1 then the
	 *            default timeout will be used( 1h ).
	 * @return previous set timeout
	 * @see #getCustomServerTimeout
	 */
	public long setCustomServerTimeout(long newTimeout);

	/**
	 * Returns custom timeout in milliseconds or -1 if the default value(1h) is
	 * used.
	 * 
	 * @return timeout in milliseconds or -1
	 * @see #setCustomServerTimeout
	 */
	public long getCustomServerTimeout();

}