package com.sap.engine.services.dc.lcm;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class LCMCompNotFoundException extends LCMException {

	private static final long serialVersionUID = -5121256422738190171L;

	public LCMCompNotFoundException(String errMessage) {
		super(errMessage);
	}

	public LCMCompNotFoundException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
