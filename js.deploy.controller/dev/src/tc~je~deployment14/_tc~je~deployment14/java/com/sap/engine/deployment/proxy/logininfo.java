package com.sap.engine.deployment.proxy;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.Logger;

/**
 * @author Mariela Todorova
 */
public class LoginInfo {
	private static final Location location = Location
			.getLocation(LoginInfo.class);
	private String host = null;
	private String port = null;
	private String user = null;
	private String password = null;

	public LoginInfo(String hostName, String portNumber, String userName,
			String pass) {
		this.host = hostName;
		this.port = portNumber;
		this.user = userName;
		this.password = pass;
		Logger.trace(location, Severity.DEBUG, "Created login info " + user
				+ "@" + host + ":" + port);
	}

	public String getHost() {
		return this.host;
	}

	public String getPort() {
		return this.port;
	}

	public String getUser() {
		return this.user;
	}

	public String getPassword() {
		return this.password;
	}

}