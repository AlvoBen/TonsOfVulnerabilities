package com.sap.engine.services.dc.api.deploy;

import java.util.ArrayList;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.ServiceNotAvailableException;
import com.sap.engine.services.dc.api.event.ClusterListener;
import com.sap.engine.services.dc.api.event.DeploymentListener;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.ListenerMode;
import com.sap.engine.services.dc.api.filters.BatchFilter;
import com.sap.engine.services.dc.api.lock_mng.AlreadyLockedException;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The class consists of operations for deploying <code>DeployItem</code> s
 * (SDA/SCA). It also delivers operations for setuping the deployment process,
 * like setting deployment filters, the error handling strategy, etc. The
 * deployment process is separate on the following sub processes:
 * <ol>
 * <li>Uploading the specified archives;</li>
 * <li>Checking (validating) the archives;</li>
 * <li>Delivering the archives;</li>
 * <li>Starting the archives;</li>
 * <li>Return deploy result;</li>
 * </ol>
 * 
 * Before performing the deployment the following setups are possible:
 * <ol>
 * <li>Add component filter - by adding a filter one could specify the only
 * components with a specific software type have to be deployed. All the other
 * components will be removed from the list with the deplyments.</li>
 * <li>Set an error handling strategy - by setting a specific error handling
 * strategy, the clients are able to decide whether the system has to continue
 * with the depoyments in case of error or not.</li>
 * <li>Set a version handling rule - by setting the rule, the clients are able
 * control the deployment processes in way that no component with wrong version
 * will be deployed.</li>
 * </ol>
 * </DD>
 * <DT><B>Usage: </B></DT>
 * <DD>ComponentManager componentManager = client.getComponentManager();//gets
 * component manager</DD>
 * <DD>DeployProcessor deployProcessor =
 * componentManager.getDeployProcessor();//creates new deploy processor</DD>
 * <DD>deployProcessor.setComponentVersionHandlingRule(
 * componentVersionHandlingRule );//sets the version strategy</DD>
 * <DD>deployProcessor.setErrorStrategy( ErrorStrategyAction.DEPLOYMENT_ACTION,
 * deploymentErrorStrategy);</DD>
 * <DD>deployProcessor.setErrorStrategy(
 * ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,prerequisiteErrorStrategy);</DD>
 * <DD>deployProcessor.addBatchFilter( batchFilter1 );//if is necessary</DD>
 * <DD>deployProcessor.addBatchFilter( batchFilter2 );//if is necessary</DD>
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
 * @see com.sap.engine.services.dc.api.ComponentManager#getDeployProcessor()
 */
public interface DeployProcessor {

	/**
	 * The operation uploads specified deployItems, checks, delivers and starts
	 * them. If there are filters set, they are applied after uploading the
	 * archives. The version handling rule and error handling strategies are
	 * taking place by the time of the steps: loading, checking, delivering and
	 * starting.
	 * 
	 * @param deployItems
	 *            <code>DeployItem []</code> a list with the items to be
	 *            deployed.
	 * @return <code>DeployResut</code> which specifies whether the deployment
	 *         process was successfull or not.
	 * @throws ConnectionException
	 *             in case there are problems marked as communication problems(
	 *             can not create InitialContext, something with the
	 *             communication with the server happened meanwhile deploying,
	 *             etc). by the time of deployment.
	 * @throws DeployResultNotFoundException
	 *             in case the deploy result was not found.
	 * @throws EngineTimeoutException
	 *             in case there is no server response after the defined timeout
	 * @throws DeployException
	 *             in case there are problems the process which are performed by
	 *             the time of deployment. The throwing of the exception
	 *             dependes on the error handling strategy, versions, etc.
	 * @throws TransportException
	 *             in case there are archives to validade that can not exists,
	 *             can not be read or can not be upload to the server.
	 * @throws AlreadyLockedException
	 *             in case Deploy controller is locked either explicitly or
	 *             other deploy operation is performed in the same moment.
	 * @throws APIException
	 *             in case there are problems with creating or getting the
	 *             deployer from the server
	 * @throws ValidationException
	 *             in case the validation could not be performed or there are
	 *             invalid archives and the <code>Deployer</code>'s error
	 *             handling strategy is <code>ErrorStrategy.ON_ERROR_STOP</code>
	 *             . Additionally, the exception will be trown in case all the
	 *             archives are not admitted for deployment, regardless of the
	 *             error handling strategy. Therefore, when the error handling
	 *             strategy is <code>ErrorStrategy.ON_ERROR_STOP</code> throwing
	 *             a <code>ValidationException</code> could mean that the
	 *             specified archives are not correct.
	 */
	public DeployResult deploy(DeployItem[] deployItems)
			throws ConnectionException, DeployResultNotFoundException,
			TransportException, ValidationException, EngineTimeoutException,
			DeployException, AlreadyLockedException,
			// RollingException,
			APIException;

	/**
	 * The operation synchronizes the deployment items matching to the given
	 * transactionId in the cluster with the data base. The deployment had been
	 * executed with <code>DeployWorkflowStrategy</code>.ROLLING.
	 * 
	 * @param transactionId
	 *            deployment transaction Id.
	 * @return <code>DeployResut</code> which specifies whether the commit
	 *         process was successfull or not.
	 * @throws ConnectionException
	 *             in case there are problems marked as communication problems(
	 *             can not create InitialContext, something with the
	 *             communication with the server happened meanwhile deploying,
	 *             etc). by the time of deployment.
	 * @throws EngineTimeoutException
	 *             in case there is no server response after the defined timeout
	 * @throws APIException
	 *             in case there are problems with creating or getting the
	 *             deployer from the server
	 * @deprecated The method will only be used for proofing the concept in the
	 *             prototyping phase. It will not be shipped to external
	 *             customers and is not considered as public interface, without
	 *             reviewing it.
	 */
	public DeployResult commit(String transactionId)
			throws ConnectionException, EngineTimeoutException, APIException;

	/**
	 * The operation rolls back the given deployment items matching to the given
	 * transactionId in the cluster. The deployment had been executed with
	 * <code>DeployWorkflowStrategy</code>.ROLLING.
	 * 
	 * @param transactionId
	 *            deployment or rollback transaction Id.
	 * @param deployItems
	 *            <code>DeployItem []</code> a list with the items to be rolled
	 *            back.
	 * @return <code>DeployResut</code> which specifies whether the rollback
	 *         process was successfull or not.
	 * @throws ConnectionException
	 *             in case there are problems marked as communication problems(
	 *             can not create InitialContext, something with the
	 *             communication with the server happened meanwhile deploying,
	 *             etc). by the time of deployment.
	 * @throws DeployResultNotFoundException
	 *             in case the deploy result was not found.
	 * @throws EngineTimeoutException
	 *             in case there is no server response after the defined timeout
	 * @throws DeployException
	 *             in case there are problems the process which are performed by
	 *             the time of deployment. The throwing of the exception
	 *             dependes on the error handling strategy, versions, etc.
	 * @throws TransportException
	 *             in case there are archives to validade that can not exists,
	 *             can not be read or can not be upload to the server.
	 * @throws AlreadyLockedException
	 *             in case Deploy controller is locked either explicitly or
	 *             other deploy operation is performed in the same moment.
	 * @throws ValidationException
	 *             in case the validation could not be performed or there are
	 *             invalid archives and the <code>Deployer</code>'s error
	 *             handling strategy is <code>ErrorStrategy.ON_ERROR_STOP</code>
	 *             . Additionally, the exception will be trown in case all the
	 *             archives are not admitted for deployment, regardless of the
	 *             error handling strategy. Therefore, when the error handling
	 *             strategy is <code>ErrorStrategy.ON_ERROR_STOP</code> throwing
	 *             a <code>ValidationException</code> could mean that the
	 *             specified archives are not correct.
	 * @throws APIException
	 *             in case there are problems with creating or getting the
	 *             deployer from the server
	 * @deprecated The method will only be used for proofing the concept in the
	 *             prototyping phase. It will not be shipped to external
	 *             customers and is not considered as public interface, without
	 *             reviewing it.
	 */
	public DeployResult rollback(String transactionId, DeployItem[] deployItems)
			throws ConnectionException, DeployResultNotFoundException,
			TransportException, ValidationException, EngineTimeoutException,
			DeployException, AlreadyLockedException,
			// RollingException,
			APIException;

	/**
	 * Returns deploy Result for the given <code>transactionId</code>.
	 * 
	 * @param transactionId
	 *            - id of the transaction which result is asked
	 * @return deploy Result for the given <code>transactionId</code>
	 * @throws ConnectionException
	 *             if cannot get remote component manager
	 * @throws DeployException
	 * @throws DeployResultNotFoundException
	 *             if there is no result found
	 * @throws ServiceNotAvailableException
	 *             if the Deploy controller is bound to the registry but is not
	 *             available at the moment due to initialization or other
	 *             operation that is in progress for the given
	 *             <code>transactionId</code>
	 */
	public DeployResult getDeployResultById(String transactionId)
			throws ConnectionException, DeployException,
			DeployResultNotFoundException, ServiceNotAvailableException;

	/**
	 * Returns list with all available offline deployment transaction ids.
	 * 
	 * @return list with all available offline deployment transaction ids
	 * @throws DeployException
	 * @throws ServiceNotAvailableException
	 * @throws ConnectionException
	 */
	public String[] getOfflineDeployTransactionIDs() throws DeployException,
			ServiceNotAvailableException, ConnectionException;

	/**
	 * The operation validates the specified array with DeployItems and returns
	 * a <code>ValidationResult</code> which specifies generaly whether the
	 * archives are successfully validate or not. The
	 * <code>ValidationStatus</code> of the returned
	 * <code>ValidationResult</code> is <code>SUCCESS</code> if all the items
	 * are addmitted for deployment and <code>ERROR</code> in other cases.
	 * Additionally, the result contains the following information:
	 * <ol>
	 * <li>Whether the Engine should be restarted in case a deployment with the
	 * specified achives is performed;
	 * <li>All the sorted and admitted for deployment items;
	 * <li>All the items corresponding to the specified archives.
	 * </ol>
	 * 
	 * @param deployItems
	 *            <code>DeployItem []</code> with the DeployItems which have to
	 *            be validated.
	 * @return <code>ValidationResult</code>
	 * @throws ValidationException
	 *             in case the validation could not be performed or there are
	 *             invalid archives and the <code>Deployer</code>'s error
	 *             handling strategy is <code>ErrorStrategy.ON_ERROR_STOP</code>
	 *             . Additionally, the exception will be trown in case all the
	 *             archives are not admitted for deployment, regardless of the
	 *             error handling strategy. Therefore, when the error handling
	 *             strategy is <code>ErrorStrategy.ON_ERROR_STOP</code> throwing
	 *             a <code>DeploymentException</code> could mean that the
	 *             specified archives are not correct;
	 * @throws TransportException
	 *             in case there are archives to validade that can not exists,
	 *             can not be read or can not be upload to the server.
	 * @throws ConnectionException
	 *             in case of connection error
	 * @throws APIException
	 *             in case there are problems with creating or getting the
	 *             deployer from the server.
	 * @deprecated use
	 *             {@link com.sap.engine.services.dc.api.validate.ValidateProcessor#validate(com.sap.engine.services.dc.api.Batch[])}
	 */
	public com.sap.engine.services.dc.api.deploy.ValidationResult validate(
			DeployItem[] deployItems) throws ConnectionException,
			ValidationException, TransportException, APIException;

	/**
	 * Returns the version handling rule.
	 * 
	 * @return the version handling rule.
	 * @see ComponentVersionHandlingRule
	 * @see #setComponentVersionHandlingRule
	 */
	public ComponentVersionHandlingRule getComponentVersionHandlingRule();

	/**
	 * Set the version handling rule.
	 * 
	 * @param rule
	 *            <code>ComponentVersionHandlingRule</code> specifies the
	 *            version handling rule which is going to be used by the time of
	 *            deployment.
	 * @see ComponentVersionHandlingRule
	 * @see #getComponentVersionHandlingRule
	 */
	public void setComponentVersionHandlingRule(
			ComponentVersionHandlingRule rule);

	/**
	 * Returns the error stategy which is mapped to the specified type.
	 * 
	 * @param errorStrategyAction
	 *            <code>ErrorStrategyAction</code> specifies the error strategy
	 *            type.
	 * @return the error stategy which is mapped to the specified type.
	 * @see ErrorStrategy
	 * @see ErrorStrategyAction
	 * @see #setErrorStrategy
	 */
	public ErrorStrategy getErrorStrategy(
			ErrorStrategyAction errorStrategyAction);

	/**
	 * Maps the specified error strategy with the specified type.
	 * 
	 * @param errorStrategyAction
	 *            the error type.
	 * @param stategy
	 *            the error strategy.
	 * @see ErrorStrategy
	 * @see ErrorStrategyAction
	 * @see #getErrorStrategy
	 */
	public void setErrorStrategy(ErrorStrategyAction errorStrategyAction,
			ErrorStrategy stategy);

	/**
	 * Returns <code>DeployWorkflowStrategy</code> which is set.
	 * 
	 * @return <code>DeployWorkflowStrategy</code> which is set. By default it
	 *         is <code>DeployWorkflowStrategy.NORMAL</code>.
	 * @see #setDeployWorkflowStrategy
	 */
	public DeployWorkflowStrategy getDeployWorkflowStrategy();

	/**
	 * Sets the deployment workflow strategy which has to be applied.
	 * 
	 * @param workflowStrategy
	 *            the <code>DeployWorkflowStrategy</code> to be set.
	 * @see #getDeployWorkflowStrategy
	 */
	public void setDeployWorkflowStrategy(
			DeployWorkflowStrategy workflowStrategy);

	/**
	 * Returns <code>LifeCycleDeployStrategy</code> which is set.
	 * 
	 * @return <code>LifeCycleDeployStrategy</code> which is set. By default it
	 *         is <code>LifeCycleDeployStrategy.BULK</code>.
	 * @see #setLifeCycleDeployStrategy
	 */
	public LifeCycleDeployStrategy getLifeCycleDeployStrategy();

	/**
	 * Sets the life cycle deployment strategy which has to be applied.
	 * 
	 * @param lifeCycleDeployStrategy
	 *            the <code>LifeCycleDeployStrategy</code> to be set.
	 * @see #getLifeCycleDeployStrategy
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
	 * Creates new deploy item.
	 * 
	 * @param archiveLocation
	 *            a string, representing a file path of an SCA or SDA file. The
	 *            file must exist and be readable.
	 * 
	 * @return created deploy item
	 * @see DeployItem
	 */
	public DeployItem createDeployItem(String archiveLocation);

	/**
	 * Adds the specified <code>DeploymentListener</code> to this deploy
	 * processor.
	 * 
	 * @param listener
	 *            <code>DeploymentListener</code> which will be notified about
	 *            specific deploy events like pre and post deploy.
	 * @param listenerMode
	 *            specifies whether the listener will 'listen' for all the
	 *            deploy events which happen on the cluster or only for the ones
	 *            which are triggered by the current Deployer.
	 * @param eventMode
	 *            specifies whether the events will be synchroneous or
	 *            asynchroneous.
	 * @see #removeDeploymentListener
	 */
	public void addDeploymentListener(DeploymentListener listener,
			ListenerMode listenerMode, EventMode eventMode);

	/**
	 * Removes the specified <code>DeploymentListener</code>.
	 * 
	 * @param listener
	 *            <code>DeploymentListener</code> which has to be removed from
	 *            the list with registered listeners.
	 * @see #addDeploymentListener
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
	 * Sets a custom server timeout for the current deploy processor. This value
	 * is used to determine for how long the API should try to re-establish the
	 * connection to the server and get the deploy result after offline
	 * deployment If this value is not explicitly set the default one will be
	 * used.
	 * 
	 * @param newTimeout
	 *            new timeout in milliseconds.
	 * @return the previous value that has been explicitly set or 0 if no value
	 *         has been set so far
	 * @see #getCustomServerTimeout
	 */
	public long setCustomServerTimeout(long newTimeout);

	/**
	 * Returns custom timeout in milliseconds or -1 if the default value(1h) is
	 * used.
	 * 
	 * @return custom timeout in milliseconds or -1 if the default value(1h) is
	 *         used.
	 * @see #setCustomServerTimeout
	 */
	public long getCustomServerTimeout();

	/**
	 * This method is generaly for debug purposes and is tight connected to
	 * {@link DeployProcessor#getDeployResultById(String)} method.
	 * 
	 * @return last triggered deployment transaction Id
	 */
	public String getLastDeploymentTransactionId();

	/**
	 * Enables/disables time statistics during the deployment. default value is
	 * disabled( false )
	 * 
	 * @param enabled
	 *            - enables/disables time statistics
	 * @see #getTimeStatEnabled
	 */
	public void setTimeStatEnabled(boolean enabled);

	/**
	 * Retrieves whether the time statistics are switched on or not. Default
	 * value is disabled( false )
	 * 
	 * @return true - if enabled otherwise false
	 * @see #setTimeStatEnabled
	 */
	public boolean getTimeStatEnabled();

	/**
	 * Return all filters set in this deploy processor.
	 * 
	 * @return ArrayList of BatchFilters
	 */
	public ArrayList getBatchFilters();

}