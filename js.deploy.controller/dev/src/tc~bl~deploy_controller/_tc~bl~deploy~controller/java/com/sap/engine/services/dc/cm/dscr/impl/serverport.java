package com.sap.engine.services.dc.cm.dscr.impl;

import java.io.Serializable;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class ServerPort implements Serializable {

	private static final long serialVersionUID = 1579489231909259339L;

	public enum Type {
		J2EE, HTTP, P4, IIOP, TELNET;
	}

	protected final Type type;
	protected int port;

	private int hashCode = -1;
	private String toString = null;

	public ServerPort(Type type, int port) {
		if (type == null) {
			throw new NullPointerException("The 'type' field cannot be null.");
		}
		this.type = type;
		if (port == -1) {
			throw new NullPointerException("The 'port' field cannot be -1.");
		}
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		if (this.getPort() == port) {
			// do nothing
		} else {
			hashCode = -1;
			toString = null;
			this.port = port;
		}
	}

	public Type getType() {
		return type;
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

		final ServerPort otherServerPort = (ServerPort) obj;

		if (!this.getType().equals(otherServerPort.getType())) {
			return false;
		}

		if (this.getPort() != otherServerPort.getPort()) {
			return false;
		}

		return true;
	}

	// ****************************************//

	private int generateHashCode() {
		int result = 17 + getType().hashCode();
		result = result * 59 + getPort();
		return result;
	}

	private String generateToString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getType());
		sb.append(Constants.TAB);
		sb.append(getPort());
		sb.append(Constants.TAB);
		return sb.toString();
	}

}
