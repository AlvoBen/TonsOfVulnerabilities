package com.sap.engine.services.dc.cm.deploy;

import java.util.Collection;
import java.util.Enumeration;

import com.sap.engine.services.dc.repo.Sca;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface CompositeDeploymentItem extends DeploymentBatchItem {

	public Sca getSca();

	/**
	 * If the <code>VersionStatus</code> is one of the <code>SAME</code>,
	 * <code>LOWER</code> <code>HIGHER</code> the operation returns the
	 * previously deployed <code>Sca</code>. If the <code>VersionStatus</code>
	 * is NEW then the operation returns <code>null</code>.
	 * 
	 * @return <code>Sca</code> in case of update and <code>null</code> in case
	 *         of deploy.
	 * @see com.sap.engine.services.dc.cm.deploy.VersionStatus
	 */
	public Sca getOldSca();

	public Collection getDeploymentItems();

	public void removeDeploymentItem(DeploymentItem deploymentItem);

	public void removeDeploymentItems(Collection deploymentItems);

	public Enumeration getAdmittedDeploymentItems();

}
