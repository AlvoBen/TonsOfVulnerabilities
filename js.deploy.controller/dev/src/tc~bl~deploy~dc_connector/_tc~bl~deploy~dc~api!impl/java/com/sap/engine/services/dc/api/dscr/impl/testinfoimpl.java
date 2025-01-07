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
package com.sap.engine.services.dc.api.dscr.impl;

import com.sap.engine.services.dc.api.dscr.ICMInfo;
import com.sap.engine.services.dc.api.dscr.ServerDescriptor;
import com.sap.engine.services.dc.api.dscr.TestInfo;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * <code>TestInfo</code> implementation.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
final class TestInfoImpl implements TestInfo {

	private final ICMInfo icmInfo;

	private final int hashCode;
	private final StringBuffer toString;

	TestInfoImpl(ICMInfo icmInfo) {
		this.icmInfo = icmInfo;

		this.hashCode = evaluateHashCode();
		this.toString = evaluateToStsring();
	}

	public ICMInfo getICMInfo() {
		return icmInfo;
	}

	// ************************** OBJECT **************************//

	public int hashCode() {
		return hashCode;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final TestInfo otherTestInfo = (TestInfo) obj;
		if (!this.getICMInfo().equals(otherTestInfo.getICMInfo())) {
			return false;
		}

		return true;
	}

	public String toString() {
		return toString.toString();
	}

	// ************************** OBJECT **************************//

	// ************************** PRIVATE **************************//

	public int evaluateHashCode() {
		final int offset = 17;
		final int multiplier = 59;

		int result = offset + getICMInfo().hashCode();

		return result;
	}

	private StringBuffer evaluateToStsring() {
		final StringBuffer sb = new StringBuffer();
		sb.append(getICMInfo());
		sb.append(DAConstants.EOL);
		return sb;
	}

	// ************************** PRIVATE **************************//

}
