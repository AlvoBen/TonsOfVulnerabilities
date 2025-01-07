package com.sap.engine.services.dc.cm.dscr.impl;

import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * @author I031421
 * 
 * 
 */
final class RollingInfoImpl implements RollingInfo {

	private static final long serialVersionUID = -6134750924269608503L;

	private byte itemType;
	private String itemName;

	private final StringBuffer toString;

	RollingInfoImpl(String itemName) {
		this(itemName, (byte) -1);
	}

	RollingInfoImpl(String itemName, byte itemType) {
		this.itemName = itemName;
		this.itemType = itemType;

		this.toString = evaluateToString();
	}

	public String getItemName() {
		return itemName;
	}

	public byte getItemType() {
		return itemType;
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

		final RollingInfo otherRollingInfo = (RollingInfo) obj;
		if (!this.getItemName().equals(otherRollingInfo.getItemName())) {
			return false;
		}

		if (this.getItemType() != otherRollingInfo.getItemType()) {
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

		int result = offset + getItemType();
		result += result * multiplier + getItemName().hashCode();

		return result;
	}

	private StringBuffer evaluateToString() {
		final StringBuffer sb = new StringBuffer("RollingInfo[");
		sb.append("itemName=");
		sb.append(getItemName());
		sb.append(",itemType=");
		sb.append(getItemType());
		sb.append("]");
		return sb;
	}

	// ************************** PRIVATE **************************//

}
