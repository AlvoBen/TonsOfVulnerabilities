package com.sap.engine.services.dc.repo;

import com.sap.engine.services.dc.cm.undeploy.UndeployItem;

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
public interface DependencyUndeploymentGraph extends DependencyGraph {

	/**
	 * Remove item form graph.
	 * 
	 * @param withoutUndeployItem
	 */
	public void remove(UndeployItem withoutUndeployItem);
}
