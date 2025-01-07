package com.sap.engine.services.dc.api.lcm.impl;

import com.sap.engine.services.dc.api.lcm.LCMResult;
import com.sap.engine.services.dc.api.lcm.LCMResultStatus;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-24
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class LCMResultImpl implements LCMResult {

	private final LCMResultStatus lcmResultStatus;
	private final String description;
	private String toString;

	LCMResultImpl(LCMResultStatus lcmResultStatus, String description) {
		this.lcmResultStatus = lcmResultStatus;
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.lcm.LCMResult#getLCMResultStatus()
	 */
	public LCMResultStatus getLCMResultStatus() {
		return this.lcmResultStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.lcm.LCMResult#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	public String toString() {
		if (this.toString == null) {
			this.toString = "LCMResult[LCMResultStatus=["
					+ this.lcmResultStatus + "], description=["
					+ this.description + "]]";
		}
		return this.toString;
	}

}
