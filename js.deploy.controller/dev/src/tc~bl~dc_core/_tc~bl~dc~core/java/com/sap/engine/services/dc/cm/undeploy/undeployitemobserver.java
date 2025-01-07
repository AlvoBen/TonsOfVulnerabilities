package com.sap.engine.services.dc.cm.undeploy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2007 Company: SAP AG Date: 2007-12-28
 * 
 * @author Boyko Popov
 * 
 */

public interface UndeployItemObserver {

	public void statusChanged(GenericUndeployItem item, UndeployItemStatus oldStatus,
			UndeployItemStatus newStatus);
}