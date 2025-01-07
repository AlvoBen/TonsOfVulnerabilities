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
package com.sap.engine.services.dc.api.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * Title: Software Deployment Manager
 * 
 * Description: The class consists of methods related with networks operations.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date 2003-10-30
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 2.0
 * @since 6.30
 * 
 */

public final class NetUtils {
	public static final String LOCALHOST = "localhost";

	private NetUtils() {
	}

	/**
	 * Gets instances of <code>InetAddress</code> for the specified hosts and
	 * compares them by using <i>equals(Object) </i> operation. If an exception
	 * occurs during getting the <code>InetAddress</code> instances the hosts
	 * are compared with the operation
	 * <i>java.lang.String.equalsIgnoreCase(String) </i>.
	 * 
	 * @see java.net.InetAddress#getByName(String)
	 * @param host1
	 * @param host2
	 * @return <code>boolean</code> <i>true </i> if the specified hosts are
	 *         equals.
	 */
	public static boolean areHostsEquals(String host1, String host2) {
		if (host1 == null) {
			return false;
		}
		if (host2 == null) {
			return false;
		}
		try {
			host1 = host1.trim();
			host2 = host2.trim();
			InetAddress host1Address = null;
			if (host1.equalsIgnoreCase(LOCALHOST)) {
				host1Address = InetAddress.getLocalHost();
			} else {
				host1Address = InetAddress.getByName(host1);
			}
			InetAddress host2Address = null;
			if (host2.equalsIgnoreCase(LOCALHOST)) {
				host2Address = InetAddress.getLocalHost();
			} else {
				host2Address = InetAddress.getByName(host2);
			}
			return host1Address.equals(host2Address);
		} catch (UnknownHostException uhe) {
			return host1.equalsIgnoreCase(host2);
		}
	}

	/**
	 * Checks whether the specified host is equal with the localhost. In order
	 * to get the local host, the operation
	 * <i>java.net.InetAddress.getLocalHost() </i> is used. In order to get an
	 * instance of <code>InetAddress</code> for the specified host the operation
	 * <i>java.net.InetAddress.getByName(String) </i> is used. If an exception
	 * occurs during getting the <code>InetAddress</code> instances the
	 * specified host is compared with the string <i>localhost </i>.
	 * 
	 * @see java.net.InetAddress#getLocalHost()
	 * @see java.net.InetAddress#getByName(String)
	 * @param host
	 *            specifies the host which will be checked.
	 * @return <code>boolean</code> <i>true </i> if the specified host is equal
	 *         to the localhost.
	 */

	public static boolean isLocalhost(String host) {
		if (host == null) {
			return false;
		}
		host = host.trim();
		if (host.equalsIgnoreCase(LOCALHOST)) {
			return true;
		} else {
			try {
				return InetAddress.getLocalHost().equals(
						InetAddress.getByName(host));
			} catch (UnknownHostException e) {
				return false;
			}
		}
	}
}
