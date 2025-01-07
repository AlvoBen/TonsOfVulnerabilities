package com.sap.engine.services.dc.repo;

import com.sap.engine.services.dc.cm.deploy.DeploymentItem;

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
public interface DependencyDeploymentGraph extends DependencyGraph {

	/**
	 * Remove item form graph and all items in branch which depend on if
	 * <code>withoutDepending<code> is <code>true</code>.
	 * 
	 * @param sda
	 */
	public void remove(DeploymentItem withoutDeploymentItem,
			boolean withoutDepending);
}
