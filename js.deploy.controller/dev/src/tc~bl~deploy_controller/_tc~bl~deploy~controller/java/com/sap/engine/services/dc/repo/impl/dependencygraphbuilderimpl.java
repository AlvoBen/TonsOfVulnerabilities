package com.sap.engine.services.dc.repo.impl;

import java.util.Collection;

import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.repo.DependencyDeploymentGraph;
import com.sap.engine.services.dc.repo.DependencyGraphBuilder;
import com.sap.engine.services.dc.repo.DependencyUndeploymentGraph;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-15
 * 
 * @author dimitar
 * @version 1.0
 * @since 7.0
 * 
 */
public class DependencyGraphBuilderImpl extends DependencyGraphBuilder {

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.repository.DependencyGraphBuilder#
	 * buildDeploymentGraph(java.util.Map)
	 */
	public DependencyDeploymentGraph buildDeploymentGraph(
			final Collection<DeploymentItem> deploymentItems) {
		return new DependencyDeploymentGraphImpl(deploymentItems);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.repository.DependencyGraphBuilder#
	 * buildUndeploymentGraph(java.util.Map)
	 */
	public DependencyUndeploymentGraph buildUndeploymentGraph(
			final Collection<UndeployItem> sdaUndeployItems) {
		return new DependencyUndeploymentGraphImpl(sdaUndeployItems);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repository.DependencyGraphBuilder#buildSubGraph
	 * (com.sap.engine.services.dc.repository.DependencyGraph,
	 * com.sap.engine.services.dc.gd.DeploymentItemImpl)
	 */
	public void buildSubGraph(final DependencyDeploymentGraph dg,
			final DeploymentItem withoutDeploymentItem) {
		this.buildSubGraph(dg, withoutDeploymentItem, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repository.DependencyGraphBuilder#buildSubGraph
	 * (com.sap.engine.services.dc.repository.DependencyGraph,
	 * com.sap.engine.services.dc.gd.DeploymentItemImpl, boolean)
	 */
	public void buildSubGraph(final DependencyDeploymentGraph dg,
			final DeploymentItem withoutDeploymentItem,
			final boolean withoutDepending) {
		dg.remove(withoutDeploymentItem, withoutDepending);
	}

	public void buildSubGraph(final DependencyUndeploymentGraph dg,
			final UndeployItem withoutUndeployItem) {
		dg.remove(withoutUndeployItem);
	}
}
