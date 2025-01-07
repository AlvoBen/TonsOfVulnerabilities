package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2007 Company: SAP AG Date: 2007-12-28
 * 
 * @author Boyko Popov
 * 
 */
public interface DeploymentBatchItemObserver {

	public void statusChanged(DeploymentBatchItem item,
			DeploymentStatus oldStatus, DeploymentStatus newStatus);
}
