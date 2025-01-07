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
import com.sap.engine.services.dc.api.dscr.TestInfo;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * <code>ICMInfo</code> implementation.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
final class ICMInfoImpl implements ICMInfo {

	private int port;
	private String host;

	private final StringBuffer toString;

	ICMInfoImpl(String host, int port) {
		this.host = host;
		this.port = port;

		this.toString = evaluateToString();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	// ************************** OBJECT **************************//

	public int hashCode() {
		return evaluateHashCode();
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

		final ICMInfo otherTestInfo = (ICMInfo) obj;
		if (!this.getHost().equals(otherTestInfo.getHost())) {
			return false;
		}

		if (this.getPort() != otherTestInfo.getPort()) {
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

		int result = offset + getPort();
		result += result * multiplier + getHost().hashCode();

		return result;
	}

	private StringBuffer evaluateToString() {
		final StringBuffer sb = new StringBuffer("ICMInfo[");
		sb.append("host=");
		sb.append(getHost());
		sb.append(",port=");
		sb.append(getPort());
		sb.append("]");
		return sb;
	}

	// ************************** PRIVATE **************************//

}
