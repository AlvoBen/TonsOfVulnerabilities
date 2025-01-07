package com.sap.engine.services.dc.api.deploy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.Settings;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Contains deploy settings.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2007</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2007-11-13</DD>
 * </DL>
 * 
 * @author Todor Atanasov
 * @version 1.0
 * @since 7.1
 */
public interface DeploySettings extends Settings {
	/**
	 * Return all filters set in this deploy processor.
	 * 
	 * @return ArrayList of BatchFilters
	 * @see #setBatchFilters
	 */
	public ArrayList getBatchFilters();

	/**
	 * Set all filters in this deploy processor.
	 * 
	 * @param batchFilters
	 *            list fo batch filters
	 * @see #getBatchFilters
	 */
	public void setBatchFilters(List batchFilters);

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
	 * Map copy with related <code>ErrorStrategy</code>s
	 * 
	 * @return <code>Maps<ErrorStrategy></code>
	 */
	public Map getErrorStrategies();

	/**
	 * Returns <code>LifeCycleDeployStrategy</code> which is set. By default it
	 * is <code>LifeCycleDeployStrategy.BULK</code>.
	 * 
	 * @return <code>LifeCycleDeployStrategy</code> which is set.
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

}
