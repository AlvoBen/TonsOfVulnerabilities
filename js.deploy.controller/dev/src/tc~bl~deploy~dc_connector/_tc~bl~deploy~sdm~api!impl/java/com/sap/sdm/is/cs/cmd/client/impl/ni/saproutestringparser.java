/*===========================================================================*/
/*                                                                           */
/*  (C) Copyright SAP AG, Walldorf  1998                                     */
/*                                                                           */
/*===========================================================================*/

package com.sap.sdm.is.cs.cmd.client.impl.ni;

/*===========================================================================*/

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Create a parser for a SAP route string The substrings of the SAP route string
 * can be obtained by calling nextElement() Algorithm: split the whole string at
 * /H/ look for /S/ in substring, first part -> hostname look for /W/ in the
 * second part, first part of the second part ->port second part of the second
 * part -> password fill in the default port, if no port is specified in the
 * substring when the old syntax with /P/ is used the password entry in the
 * previous substring is entered
 * 
 * For the last substring, the port entered is: the port specified in the /S/
 * field, if non specified the port given as parameter is used
 * 
 * if the host does not begin with /H/ a normal host name is assumed and only
 * one SapRouteSubString is returned with the port specified in the constructor
 * argument list.
 * 
 * @author Harald Mueller
 * @version 1.0
 * @see java.util.Enumeration
 */
class SapRouteStringParser implements Enumeration {
	/**
	 * default port to use when non is specified with /S/
	 */
	private static final int DEFAULT_SAPROUTE_PORT = 3299;

	protected static final String HOST_TAG = "/H/";
	protected static final String PORT_TAG = "/S/";
	protected static final String PASSWORD_TAG = "/W/";
	protected static final String PASSWORD_TAG_OLD = "/P/";

	/**
	 * true: the host entered as parameter is a SAP route string, i.e. starts
	 * with /H/
	 */
	private boolean isSapRouteString;

	/**
	 * String for intermediate storage of the hostname
	 */
	private String currentHostname = "";
	/**
	 * String for intermediate storage of the port
	 */
	private String currentPort = "";
	/**
	 * String for intermediate storage of the password
	 */
	private String currentPassword = "";

	/**
	 * For compatibility with the old format, i.e. /P/ notation for the
	 * password, we need to hold two SAP route substrings to enter the password
	 * in the previous one when /P/ occurs String for intermediate storage of
	 * the hostname
	 */
	private String previousHostname = "";
	/**
	 * For compatibility with the old format, i.e. /P/ notation for the
	 * password, we need to hold two SAP route substrings to enter the password
	 * in the previous one when /P/ occurs String for intermediate storage of
	 * the port
	 */
	private String previousPort = "";
	/**
	 * For compatibility with the old format, i.e. /P/ notation for the
	 * password, we need to hold two SAP route substrings to enter the password
	 * in the previous one when /P/ occurs String for intermediate storage of
	 * the password
	 */
	private String previousPassword = "";

	/**
	 * host supplied to the constructor
	 */
	private String host;

	/**
	 * port supplied to the constructor
	 */
	private int port;

	/**
	 * string to store the part of a SAP route string which still has to be
	 * analyzed
	 */
	private String restStr;

	/**
	 * the password tag to be used. Old and new syntax cannot be mixed. Which
	 * one to use is checked in the constructor of the parser. If the old syntax
	 * is used the default value is modified. ( default: new style: /W/)
	 * 
	 * @see SapRouteStringParser#SapRouteStringParser
	 */
	private String passwdTag = PASSWORD_TAG;

	/**
	 * set to true when no more SAP route substrings can be returned
	 */
	private boolean lastSubStringReturned = false;

	/**
	 * state variable holding the type of syntax used for the sap route string
	 * false : new style /H/host/S/port/W/pass true : old style
	 * /H/host/S/port/P/pass
	 */
	private boolean oldStyle = false;

	/**
	 * Create a parser for the SAP route string specified
	 * 
	 * @param host
	 *            the SAP route string or an ordinary hostname
	 * @param port
	 *            the port of the endpoint of the connection
	 */
	public SapRouteStringParser(String host, int port)
			throws SapRouteStringFormatException {
		// check that the host parameter contains a valid value
		if (host == null) {
			throw new SapRouteStringFormatException(
					"Malformed SAP route string: string must not be a null reference");
		}

		// check whether host is a sap route string
		// i.e. must begin with /H/
		if (host.startsWith(HOST_TAG)) {
			isSapRouteString = true;
		} else {
			isSapRouteString = false;
		}

		// check that old and new style of password specification, i.e. /W/ ,/P/
		// are not mixed
		if (isSapRouteString) {

			if (host.indexOf(PASSWORD_TAG) > 0) {
				if (host.indexOf(PASSWORD_TAG_OLD) > 0) {
					throw new SapRouteStringFormatException(
							"Malformed SAP route string: it is not allowed to mix "
									+ PASSWORD_TAG + " and " + PASSWORD_TAG_OLD);
				}
			} else if (host.indexOf(PASSWORD_TAG_OLD) > 0) {
				passwdTag = PASSWORD_TAG_OLD;
				oldStyle = true;
			}

			// initialize the string, which holds the part of the SAP route
			// string
			// which still has to be parsed
			restStr = host.substring(HOST_TAG.length());

			// initialize the parser, i.e. the currentHostname and
			// previousHostname
			// fields
			analyzeRestString();
			saveSapRouteSubString();
		}

		this.port = port;
		this.host = host;

	}

	/**
	 * Indicates whether more SAP route substring can be returned
	 * 
	 * @return true: if there are more SAP route substring to return false
	 *         otherwise
	 * @see SapRouteSubString
	 * @see java.util.Enumeration
	 */
	public final boolean hasMoreElements() {
		return !lastSubStringReturned;
	}

	/**
	 * Return the next SAP route substring to the client of the parser If the
	 * SAP route string has a wrong format exception
	 * SapRouteStringFormatException with appropriate text is thrown
	 * 
	 * @return the next SAP route substring when parsing the SAP route string
	 * @exception NoSuchElementException
	 *                if the function is called although the enumeration is
	 *                finished
	 * @see SapRouteSubString
	 * @see java.util.Enumeration
	 */
	public final Object nextElement() throws NoSuchElementException {
		if (isSapRouteString == false && lastSubStringReturned == false) {
			lastSubStringReturned = true;
			return new SapRouteSubString(host, String.valueOf(port));
		}

		if (isSapRouteString == true && lastSubStringReturned == false) {
			boolean moreSubStrings = analyzeRestString();

			// the end of the iteration is reached
			if (moreSubStrings == false) {
				lastSubStringReturned = true;
			}

			// modify the port: if port in the SAP route string is specified
			// use this, otherwise use the port from the constructor prameter
			// list
			if (previousPort.length() == 0) {
				if (moreSubStrings == false) {
					previousPort = String.valueOf(port);
				} else {
					previousPort = String.valueOf(DEFAULT_SAPROUTE_PORT);
				}

			}

			return createSapRouteSubString();

		} else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * analyze the part of the sap route string not parsed yet and fill the
	 * hostname, password and port fields accordingly
	 * 
	 * @return true if a SAP route substring was found, false otherwise
	 */
	private boolean analyzeRestString() {

		if (restStr.length() != 0) {
			int endOfHost;
			String workStr;

			if ((endOfHost = restStr.indexOf(HOST_TAG)) >= 0) {
				workStr = restStr.substring(0, endOfHost);
				restStr = restStr.substring(endOfHost + HOST_TAG.length());
			} else {
				workStr = new String(restStr);
				restStr = "";
			}

			// now look in subHostStr for /S/ and/or /W/ or /P/ respectively.
			int beginOfPort;

			if ((beginOfPort = workStr.indexOf(PORT_TAG)) >= 0) {
				// port Tag found
				currentHostname = workStr.substring(0, beginOfPort);

				workStr = workStr.substring(beginOfPort + PORT_TAG.length());

			}

			int beginOfPasswd;

			if ((beginOfPasswd = workStr.indexOf(passwdTag)) >= 0) {
				// password tag found
				if (beginOfPort < 0) {
					// not filled yet, because no port found
					currentHostname = workStr.substring(0, beginOfPasswd);
				} else {
					currentPort = workStr.substring(0, beginOfPasswd);
				}

				if (oldStyle) {
					previousPassword = workStr.substring(beginOfPasswd
							+ passwdTag.length());
				} else {
					currentPassword = workStr.substring(beginOfPasswd
							+ passwdTag.length());
				}

			} else {
				if (beginOfPort < 0) {
					// not filled yet because no port and password found
					currentHostname = new String(workStr);
				} else {
					// not filled yet because no password found
					currentPort = new String(workStr);
				}

			}

			return true;

		} else {
			return false;
		}

	}

	/**
	 * create a SAP route substring from the buffered values in the parser and
	 * refresh those values.
	 * 
	 * @see SapRouteStringParser#previousHostname
	 * @see SapRouteStringParser#previousPort
	 * @see SapRouteStringParser#previousPassword
	 */
	private SapRouteSubString createSapRouteSubString() {
		SapRouteSubString retval = new SapRouteSubString(previousHostname,
				previousPort, previousPassword);

		saveSapRouteSubString();

		Trace.print(Trace.ALL,
				"SapRouteStringParser.createSapRouteSubString(): SapRouteSubString "
						+ "created:\n" + retval);

		return retval;
	}

	/**
	 * Save the SAP route substring currently collected in the Previous* fields
	 * 
	 * @see SapRouteStringParser#currentHostname
	 * @see SapRouteStringParser#currentPort
	 * @see SapRouteStringParser#currentPassword
	 * @see SapRouteStringParser#previousHostname
	 * @see SapRouteStringParser#previousPort
	 * @see SapRouteStringParser#previousPassword
	 */
	private void saveSapRouteSubString() {
		previousHostname = currentHostname;
		previousPort = currentPort;
		previousPassword = currentPassword;
		currentHostname = "";
		currentPort = "";
		currentPassword = "";
	}

}
