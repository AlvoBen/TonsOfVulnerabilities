package com.sap.engine.services.dc.ant;

import org.apache.tools.ant.BuildException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-8
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */
public class SAPJ2EEEngine {

	private String serverHost;
	private int serverPort;
	private String userName;
	private String userPassword;

	public SAPJ2EEEngine() {
	}

	/**
	 * @return Returns the serverHost.
	 */
	public String getServerHost() {
		return this.serverHost;
	}

	/**
	 * @param serverHost
	 *            The serverHost to set.
	 */
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	/**
	 * @return Returns the serverPort.
	 */
	public int getServerPort() {
		return this.serverPort;
	}

	/**
	 * @param serverPort
	 *            The serverPort to set.
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName
	 *            The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return Returns the userPassword.
	 */
	public String getUserPassword() {
		return this.userPassword;
	}

	/**
	 * @param userPassword
	 *            The userPassword to set.
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public void validate() throws BuildException {
		if (this.serverHost == null || this.serverHost.trim().equals("")) {
			throw new BuildException("The server host attribute must be set.");
		}

		if (this.userName == null || this.userName.trim().equals("")) {
			throw new BuildException("The user name attribute must be set.");
		}

		if (this.userPassword == null || this.userPassword.trim().equals("")) {
			throw new BuildException("The user password attribute must be set.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "server host: '" + this.serverHost + "', server port '"
				+ this.serverPort + "', user name '" + this.userName + "'.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int offset = 17;
		final int multiplier = 59;

		int result = offset
				+ (this.serverHost != null ? this.serverHost.hashCode() : 0);
		return result * multiplier + this.serverPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

		final SAPJ2EEEngine otherServer = (SAPJ2EEEngine) obj;

		if (this.getServerHost() == null && otherServer.getServerHost() != null
				|| !this.getServerHost().equals(otherServer.getServerHost())) {
			return false;
		}

		if (this.getServerPort() != otherServer.getServerPort()) {
			return false;
		}

		return true;
	}

}
