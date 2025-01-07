package com.sap.engine.services.dc.cm.undeploy;

import java.io.Serializable;

import com.sap.engine.services.dc.repo.Sdu;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-24
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface UndeploymentObserver extends Serializable {

	public void sduUndeployed(Sdu sdu) throws UndeploymentObserverException;

}
