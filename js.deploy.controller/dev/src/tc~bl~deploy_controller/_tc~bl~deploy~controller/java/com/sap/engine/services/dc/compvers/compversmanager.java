package com.sap.engine.services.dc.compvers;

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
public interface CompVersManager {

	public void sduChanged(Sdu sdu) throws CompVersException;

	public void sduUndeployed(Sdu sdu) throws CompVersException;

}
