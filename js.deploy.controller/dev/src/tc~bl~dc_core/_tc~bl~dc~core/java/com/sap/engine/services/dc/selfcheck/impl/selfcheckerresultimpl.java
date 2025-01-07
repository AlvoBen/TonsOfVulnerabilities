/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.selfcheck.impl;

import com.sap.engine.services.dc.selfcheck.SelfCheckerResult;
import com.sap.engine.services.dc.selfcheck.SelfCheckerStatus;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: Mar 30, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class SelfCheckerResultImpl implements SelfCheckerResult {
	static final long serialVersionUID = 2061762151038335228L;

	private final SelfCheckerStatus status;
	private final String description;
	private final String toString;
	private final int hashCode;

	SelfCheckerResultImpl(SelfCheckerStatus status, String description) {
		this.status = status;
		this.description = description;
		this.toString = "Self Check status '"
				+ this.status
				+ ((this.description != null && this.description.length() > 0) ? "'"
						+ Constants.EOL + "description: " + this.description
						: "") + Constants.EOL;
		this.hashCode = 17
				* status.hashCode()
				+ ((this.description != null) ? 59 * this.description
						.hashCode() : 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.selfchecker.SelfCheckResult#getStatus()
	 */
	public SelfCheckerStatus getStatus() {
		return this.status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.selfchecker.SelfCheckResult#getDescription
	 * ()
	 */
	public String getDescription() {
		return this.description;
	}

	public String toString() {
		return this.toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof SelfCheckerResult)) {
			return false;
		}
		SelfCheckerResult other = (SelfCheckerResult) obj;
		if (!this.getStatus().equals(other.getStatus())) {
			return false;
		}
		if (!this.getDescription().equals(other.getDescription())) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}
}
