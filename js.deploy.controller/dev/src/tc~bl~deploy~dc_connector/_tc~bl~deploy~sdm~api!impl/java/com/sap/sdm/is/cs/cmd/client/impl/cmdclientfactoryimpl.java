package com.sap.sdm.is.cs.cmd.client.impl;

import java.io.IOException;

import com.sap.sdm.is.cs.cmd.client.CmdClient;
import com.sap.sdm.is.cs.cmd.client.CmdClientFactory;

/**
 * Title: Software Delivery Manager Description: Copyright: Copyright (c) 2001
 * Company: SAP AG
 * 
 * @author Software Logistics - here: d019309
 * 
 */
final class CmdClientFactoryImpl extends CmdClientFactory {
	private final static CmdClientFactoryImpl instance = new CmdClientFactoryImpl();

	private CmdClientFactoryImpl() {
	}

	static void registerToAbstractFactory() {
		CmdClientFactoryImpl.setInstance(CmdClientFactoryImpl.instance);
	}

	public CmdClient create(int port, String host) throws IOException {
		return new CmdClientImpl(port, host);
	}

	public CmdClient create(int port, String host, boolean suppressErrorMessages)
			throws IOException {
		return new CmdClientImpl(port, host, suppressErrorMessages);
	}

	public CmdClient create(int localPort, int port, String host, int lTimeout)
			throws IOException {
		return new CmdClientImpl(localPort, port, host, lTimeout);
	}

	public CmdClient create(String localHost, int localPort, int _port,
			String _host, int lTimeout) throws IOException {
		return new CmdClientImpl(localHost, localPort, _port, _host, lTimeout);
	}

	public CmdClient create(int port, String host, int lTimeout)
			throws IOException {
		return new CmdClientImpl(port, host, lTimeout);
	}

	public CmdClient create(int port) throws IOException {
		return new CmdClientImpl(port);
	}

	public CmdClient create(int port, boolean suppressErrorMessages)
			throws IOException {
		return new CmdClientImpl(port, suppressErrorMessages);
	}

}
