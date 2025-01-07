package com.sap.sdm.apiimpl.remote.client;

import com.sap.sdm.api.remote.APIProtocols;
import com.sap.sdm.api.remote.AuthenticationException;
import com.sap.sdm.api.remote.ClientSession;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.UnsupportedProtocolException;
import com.sap.sdm.api.remote.WrongPasswordException;

/**
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 * 
 */
public class APIClientSessionFactoryImpl {

	static {
		APIClientInitializer.init();
	}

	public static ClientSession createAPIClientSession(int localPort,
			String localHost, int port, String host, String password,
			int loginTimeout) throws RemoteException {
		return APIClientSessionImpl.newInstance(localPort, localHost, port,
				host, password, loginTimeout);
	}

	public static ClientSession createAPIClientSession(String protocol,
			int localPort, String localHost, int port, String host,
			String user, String password, int loginTimeout)
			throws UnsupportedProtocolException, AuthenticationException,
			RemoteException {
		if (protocol.equals(APIProtocols.SDM_PROTOCOL)) {
			return createAPIClientSession(localPort, localHost, port, host,
					password, loginTimeout);
		} else if (protocol.equals(APIProtocols.P4_PROTOCOL)) {
			return P4APIClientSessionImpl.newInstance(port, host, user,
					password);
		} else {
			throw new APIUnsupportedProtocolException("The protocol '"
					+ protocol + "' is not supported.");
		}
	}

	public static ClientSession createAPIClientSessionIDE(int localPort,
			String localHost, int port, String host, String password,
			int loginTimeout) throws RemoteException, WrongPasswordException {
		return APIClientSessionImpl.newInstanceIDE(localPort, localHost, port,
				host, password, loginTimeout);
	}

}
