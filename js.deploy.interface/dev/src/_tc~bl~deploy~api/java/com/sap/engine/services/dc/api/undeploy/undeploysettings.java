package com.sap.engine.services.dc.api.undeploy;

import java.util.Map;

import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.Settings;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Contains undeploy settings.</DD>
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
public interface UndeploySettings extends Settings {
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
	 *            workflow strategy
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
	 * Map copy with related <code>ErrorStrategy</code>s
	 * 
	 * @return <code>Maps<ErrorStrategy></code>
	 */
	public Map getErrorStrategies();

}
