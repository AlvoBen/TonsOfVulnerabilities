package com.sap.engine.services.dc.lcm.impl;

import com.sap.engine.services.dc.lcm.LCMResult;
import com.sap.engine.services.dc.lcm.LCMResultStatus;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class LCMResultImpl implements LCMResult {

	private static final long serialVersionUID = -8132033186754427665L;

	private final LCMResultStatus lcmResultStatus;
	private final String description;

	LCMResultImpl(LCMResultStatus lcmResultStatus, String description) {
		this.lcmResultStatus = lcmResultStatus;
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.lcm.LCMResult#getLCMResultStatus()
	 */
	public LCMResultStatus getLCMResultStatus() {
		return this.lcmResultStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.lcm.LCMResult#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

}
