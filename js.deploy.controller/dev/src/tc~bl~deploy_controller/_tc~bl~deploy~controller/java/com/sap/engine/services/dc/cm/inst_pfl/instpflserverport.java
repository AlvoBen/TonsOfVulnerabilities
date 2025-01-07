package com.sap.engine.services.dc.cm.inst_pfl;

import com.sap.engine.services.dc.cm.dscr.impl.ServerPort;

public class InstPflServerPort extends ServerPort implements
		Comparable<InstPflServerPort> {

	public final static String PROT = "PROT=";
	public final static String PORT = "PORT=";

	protected final String propKey;
	protected final String addValue;

	private int hashCode = -1;
	private String toString = null;

	public InstPflServerPort(String propKey, Type type, int port,
			String addValue) {
		super(type, port);
		if (propKey == null) {
			throw new NullPointerException(
					"The 'propKey' field cannot be null.");
		}
		this.propKey = propKey;
		this.addValue = addValue == null ? "" : addValue;
	}

	public String getPropKey() {
		return propKey;
	}

	public String getAddValue() {
		return addValue;
	}

	public void setPort(int port) {
		if (this.getPort() == port) {
			// do nothing
		} else {
			hashCode = -1;
			toString = null;
			super.setPort(port);
		}
	}

	// ****************************************//
	public int hashCode() {
		if (hashCode == -1) {
			hashCode = generateHashCode();
		}
		return hashCode;
	}

	public String toString() {
		if (toString == null) {
			toString = generateToString();
		}
		return toString;
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

		final InstPflServerPort otherServerPort = (InstPflServerPort) obj;

		if (!super.equals(otherServerPort)) {
			return false;
		}

		if (!this.getPropKey().equals(otherServerPort.getPropKey())) {
			return false;
		}

		if (!this.getAddValue().equals(otherServerPort.getAddValue())) {
			return false;
		}

		return true;
	}

	// ****************************************//

	private int generateHashCode() {
		int result = 17 + super.hashCode();
		result = result * 59 + getPropKey().hashCode();
		result = result * 59 + getAddValue().hashCode();
		return result;
	}

	private String generateToString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getPropKey());
		sb.append(" = ");
		sb.append(PROT);
		sb.append(getType());
		sb.append(", ");
		sb.append(PORT);
		sb.append(getPort());
		sb.append(getAddValue());
		return sb.toString();
	}

	public int compareTo(InstPflServerPort other) {
		return getPropKey().compareTo(other.getPropKey());
	}

}
