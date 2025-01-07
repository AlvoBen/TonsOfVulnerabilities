package com.sap.engine.services.dc.lcm.impl;

import com.sap.engine.services.dc.lcm.LCMException;
import com.sap.engine.services.dc.lcm.LCMResult;
import com.sap.engine.services.dc.lcm.LCMStatus;
import com.sap.engine.services.dc.repo.Sda;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-29
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class AbstractLCMProcessor {

	abstract LCMResult doStart(Sda sda) throws LCMException;

	abstract LCMResult doStop(Sda sda) throws LCMException;

	abstract LCMStatus getLCMStatus(Sda sda) throws LCMException;

	public static String getCompId(Sda sda) {
		return sda.getVendor() + '/' + sda.getName();
	}

}
