/*
 * Created on Oct 19, 2004
 *
 */
package com.sap.engine.services.dc.api.session.impl;

import java.util.Properties;

import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.session.SessionFactory;
import com.sap.engine.services.dc.api.util.DALog;

/**
 * @author Georgi Danov
 * @author Boris Savov
 */
public class SessionFactoryImpl extends SessionFactory {

	public Session newSession(DALog daLog, String host, int p4port,
			String user, String password) throws AuthenticationException,
			ConnectionException {

		int sapcontrolPort = calculateSapcontrolPort(p4port, daLog);

		return new SessionImpl(daLog, host, p4port, sapcontrolPort, user,
				password, null);
	}

	public Session newSession(DALog daLog, String host, int p4port,
			String user, String password, Properties p4props)
			throws AuthenticationException, ConnectionException {

		int sapcontrolPort = calculateSapcontrolPort(p4port, daLog);
		return new SessionImpl(daLog, host, p4port, sapcontrolPort, user,
				password, p4props);
	}

	public String toString() {
		return "Session Factory Implementation";
	}

	public Session newSession(DALog daLog, String host, int p4port,
			int sapcontrolPort, String user, String password, Properties p4props)
			throws AuthenticationException, ConnectionException {

		return new SessionImpl(daLog, host, p4port, sapcontrolPort, user,
				password, p4props);
	}

	private int calculateSapcontrolPort(int p4port, DALog daLog) {

		if (p4port < 50000) {

			// no valid p4 port has been provided
			return -1;
		}

		// sapcontrol port = 5<NR>13
		int instanceNumber = (p4port / 100) - 500;
		int sapcontrolPort = 50000 + 100 * instanceNumber + 13;

		if (daLog.isInfoTraceable()) {
			daLog
					.traceInfo(
							"ASJ.dpl_api.001165",
							"The value [{0}] of SAP Control port was calculated based on P4 port [{1}]",
							new Object[] { new Integer(sapcontrolPort),
									new Integer(p4port) });
		}

		return sapcontrolPort;

	}

}