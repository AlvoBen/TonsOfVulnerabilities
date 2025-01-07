package com.sap.sdm.is.cs.cmd.client;

import java.io.IOException;

/**
 * Title: Software Delivery Manager Description: Copyright: Copyright (c) 2001
 * Company: SAP AG
 * 
 * @author Software Logistics - here: d019309
 * 
 */
public abstract class CmdClientFactory {

	static private CmdClientFactory instance = null;

	public static void setInstance(CmdClientFactory guiFactory) {
		CmdClientFactory.instance = guiFactory;
	}

	public static CmdClientFactory getInstance() {
		return CmdClientFactory.instance;
	}

	abstract public CmdClient create(int _port) throws IOException;

	abstract public CmdClient create(int _port, boolean suppressErrorMessages)
			throws IOException;

	abstract public CmdClient create(int _port, String _host)
			throws IOException;

	abstract public CmdClient create(int _port, String _host, int lTimeout)
			throws IOException;

	abstract public CmdClient create(int _port, String _host,
			boolean suppressErrorMessages) throws IOException;

	abstract public CmdClient create(int localPort, int _port, String _host,
			int lTimeout) throws IOException;

	abstract public CmdClient create(String localHost, int localPort,
			int _port, String _host, int lTimeout) throws IOException;

}
