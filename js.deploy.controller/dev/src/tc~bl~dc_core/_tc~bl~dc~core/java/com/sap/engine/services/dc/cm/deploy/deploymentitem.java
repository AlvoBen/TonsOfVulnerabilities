package com.sap.engine.services.dc.cm.deploy;

import java.util.Set;

import com.sap.engine.services.dc.repo.Sda;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-11
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public interface DeploymentItem extends DeploymentBatchItem {

	public BatchItemId getParentId();

	public Sda getSda();

	public Set getDepending();

	public void addDepending(DeploymentItem deploymentItem);

	public void removeDepending(DeploymentItem deploymentItem);

	/**
	 * If the <code>VersionStatus</code> is one of the <code>SAME</code>,
	 * <code>LOWER</code> <code>HIGHER</code> the operation returns the
	 * previously deployed <code>Sda</code>. If the <code>VersionStatus</code>
	 * is NEW then the operation returns <code>null</code>.
	 * 
	 * @return <code>Sda</code> in case of update and <code>null</code> in case
	 *         of deploy.
	 * @see com.sap.engine.services.dc.cm.deploy.VersionStatus
	 */
	public Sda getOldSda();

}
