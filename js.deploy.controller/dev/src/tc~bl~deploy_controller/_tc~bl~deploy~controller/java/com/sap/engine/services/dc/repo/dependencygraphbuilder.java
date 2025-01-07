package com.sap.engine.services.dc.repo;

import java.util.Collection;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-15
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public abstract class DependencyGraphBuilder {

	private static DependencyGraphBuilder INSTANCE;

	// TODO: Could be read from a global deploy service configurator
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.repo.impl.DependencyGraphBuilderImpl";

	protected DependencyGraphBuilder() {
	}

	public static synchronized DependencyGraphBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createBuilder();
		}
		return INSTANCE;
	}

	private static DependencyGraphBuilder createBuilder() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (DependencyGraphBuilder) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003366 An error occurred while creating an instance of "
					+ "class DependencyGraphBuilder! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract DependencyDeploymentGraph buildDeploymentGraph(
			Collection<DeploymentItem> deploymentItems);

	public abstract DependencyUndeploymentGraph buildUndeploymentGraph(
			Collection<UndeployItem> sdaUndeployItems);

	public abstract void buildSubGraph(DependencyDeploymentGraph dg,
			DeploymentItem withoutDeploymentItem);

	public abstract void buildSubGraph(DependencyUndeploymentGraph dg,
			UndeployItem withoutUndeployItem);

	public abstract void buildSubGraph(DependencyDeploymentGraph dg,
			DeploymentItem withoutDeploymentItem, boolean withoutDepending);

}
