package com.sap.engine.services.dc.api.deploy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.api.deploy.DeploySettings;
import com.sap.engine.services.dc.api.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.api.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.api.util.DAConstants;
import com.sap.engine.services.dc.api.util.DeployApiMapper;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2007</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2008-01-07</DD>
 * </DL>
 * 
 * @author Todor Atanasov
 * @version 1.0
 * @since 7.1
 */
public class DeploySettingsImpl implements DeploySettings {

	private final ArrayList batchFilters = new ArrayList(0);
	private ComponentVersionHandlingRule componentVersionHandlingRule;
	private DeployWorkflowStrategy deployWorkflowStrategy;
	private HashMap errorStrategies = new HashMap(1);
	private LifeCycleDeployStrategy lifeCycleDeployStrategy;

	public ArrayList getBatchFilters() {
		return batchFilters;
	}

	public ComponentVersionHandlingRule getComponentVersionHandlingRule() {
		return componentVersionHandlingRule;
	}

	public DeployWorkflowStrategy getDeployWorkflowStrategy() {
		return deployWorkflowStrategy;
	}

	public void setBatchFilters(List batchFilters) {
		this.batchFilters.clear();
		this.batchFilters.addAll(batchFilters);
	}

	public void setComponentVersionHandlingRule(
			ComponentVersionHandlingRule rule) {
		this.componentVersionHandlingRule = rule;
	}

	public void setDeployWorkflowStrategy(
			DeployWorkflowStrategy workflowStrategy) {
		this.deployWorkflowStrategy = workflowStrategy;
	}

	public LifeCycleDeployStrategy getLifeCycleDeployStrategy() {
		return this.lifeCycleDeployStrategy;
	}

	public void setLifeCycleDeployStrategy(
			LifeCycleDeployStrategy lifeCycleDeployStrategy) {
		this.lifeCycleDeployStrategy = lifeCycleDeployStrategy;
	}

	public ErrorStrategy getErrorStrategy(
			ErrorStrategyAction errorStrategyAction) {
		return (ErrorStrategy) this.errorStrategies.get(errorStrategyAction);
	}

	public void setErrorStrategy(ErrorStrategyAction errorStrategyAction,
			ErrorStrategy strategy) {
		if (DeployApiMapper.isValidErrorStrategyAction(errorStrategyAction)) {
			this.errorStrategies.put(errorStrategyAction, strategy);
		} else {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1040] Unknown strategy type "
							+ errorStrategyAction + " detected");
		}
	}

	public Map getErrorStrategies() {
		return Collections.unmodifiableMap(errorStrategies);
	}

	public String toString() {
		final StringBuffer sb = new StringBuffer(DAConstants.EOL);

		sb.append("batchFilters");
		sb.append(DAConstants.EQUAL);
		sb.append(batchFilters);
		sb.append(DAConstants.EOL);

		sb.append("componentVersionHandlingRule");
		sb.append(DAConstants.EQUAL);
		sb.append(componentVersionHandlingRule);
		sb.append(DAConstants.EOL);

		sb.append("deployWorkflowStrategy");
		sb.append(DAConstants.EQUAL);
		sb.append(deployWorkflowStrategy);
		sb.append(DAConstants.EOL);

		sb.append("errorStrategies");
		sb.append(DAConstants.EQUAL);
		sb.append(errorStrategies);
		sb.append(DAConstants.EOL);

		sb.append("lifeCycleDeployStrategy");
		sb.append(DAConstants.EQUAL);
		sb.append(lifeCycleDeployStrategy);
		sb.append(DAConstants.EOL);

		return sb.toString();
	}
}
