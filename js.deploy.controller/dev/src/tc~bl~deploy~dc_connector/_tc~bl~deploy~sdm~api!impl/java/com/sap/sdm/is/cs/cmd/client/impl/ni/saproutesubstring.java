/*===========================================================================*/
/*                                                                           */
/*  (C) Copyright SAP AG, Walldorf  1998                                     */
/*                                                                           */
/*===========================================================================*/

package com.sap.sdm.is.cs.cmd.client.impl.ni;

/*===========================================================================*/

/**
 * Class to hold the data for a SAP route substring used by class
 * <code>SapRouteString</code> and <code>NiSocket</code>
 * 
 * @author Harald Mueller
 * @version 1.0
 * @see SapRouteString
 * @see NiSocket
 */

public class SapRouteSubString {

	/**
	 * hostname part of a SAP route substring, i.e. of
	 * /H/<host>/S/<port>/W/<password>
	 */
	private String host;

	/**
	 * service part of a SAP route substring i.e. of
	 * /H/<host>/S/<port>/W/<password>
	 */
	private String port;

	/**
	 * password part of a SAP route substring i.e. of
	 * /H/<host>/S/<port>/W/<password>
	 */
	private String password;

	/**
	 * Create a SAP route substring with just a hostname, i.e. /H/<host>
	 * 
	 * @param hostname
	 *            the host part of a SAP route substring
	 */
	public SapRouteSubString(String hostname) {
		initialize(hostname, "", "");
	}

	/**
	 * Create a SAP route substring with hostname and service name, i.e.
	 * /H/<host>/S/<port>
	 * 
	 * @param hostname
	 *            the host part of a SAP route substring
	 * @param port
	 *            the service part of a SAP route substring
	 */
	public SapRouteSubString(String hostname, String port) {
		initialize(hostname, port, "");
	}

	/**
	 * Create a SAP route substring with hostname and service name and password,
	 * i.e. /H/<host>/S/<port>/W/<password>
	 * 
	 * @param hostname
	 *            the host part of a SAP route substring
	 * @param port
	 *            the service part of a SAP route substring
	 * @param passwd
	 *            the password part of a SAP route substring
	 */
	public SapRouteSubString(String hostname, String port, String passwd) {
		initialize(hostname, port, passwd);
	}

	/**
	 * the working function for creating a new SapRouteSubString
	 * 
	 * @param hostname
	 *            the host part of a SAP route substring
	 * @param port
	 *            the service part of a SAP route substring
	 * @param passwd
	 *            the password part of a SAP route substring
	 */
	private void initialize(String hostname, String port, String passwd) {
		this.host = new String(hostname);
		this.port = new String(port);
		this.password = new String(passwd);
	}

	/**
	 * Return the host part of the SapRouteSubString
	 * 
	 * @return the hostname of the SAP route substring
	 */
	public String getHost() {
		// not exposing the private member to be changed by clients
		// since the String object is immutable
		return host;
	}

	/**
	 * Return the service part of the SapRouteSubString
	 * 
	 * @return the service naem of the SAP route substring as a String
	 */
	public String getPortAsString() {
		// not exposing the private member to be changed by clients
		// since the String object is immutable
		return port;
	}

	/**
	 * Return the password part of the SapRouteSubString
	 * 
	 * @return the password of the SAP route substring
	 */
	public String getPassword() {
		// not exposing the private member to be changed by clients
		// since the String object is immutable
		return password;
	}

	/**
	 * Return the service part of the SapRouteSubString as integer If the
	 * service is an empty string -1 is returned
	 * 
	 * @return the port number, -1 if no port is specified
	 * @exception java.lang.NumberFormatException
	 *                if the string cannot be converted to an integer
	 * @see java.lang.Integer#parseInt
	 */
	public int getPort() throws NumberFormatException {
		if (port.length() == 0) {
			return -1;
		}
		try {
			return Integer.parseInt(port);
		} catch (NumberFormatException e) {
			throw new NumberFormatException(
					"SapRouteSubString: Cannot convert port name to an "
							+ "integer");
		}
	}

	/**
	 * Return the SAP route substring in the 'new' notation i.e.
	 * /H/<host>/S/<service>/W/<password>
	 */
	public String toString() {
		String retval = SapRouteStringParser.HOST_TAG + getHost();

		if (getPortAsString().length() > 0) {
			retval = retval + SapRouteStringParser.PORT_TAG + getPortAsString();
		}

		if (getPassword().length() > 0) {
			retval = retval + SapRouteStringParser.PASSWORD_TAG + getPassword();
		}
		return new String(retval);
	}
}
