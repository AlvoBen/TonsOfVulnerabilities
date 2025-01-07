package com.sap.sdm.is.cs.cmd.client;

import java.io.IOException;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * Title: sdm Description: Software Deployment Manager Copyright: Copyright (c)
 * 2001 Company: SAP AG
 * 
 * @author DL software logistics for Java
 * @version 1.0
 */
public abstract class CmdClient {

	public static final int DEFAULT_LOGIN_TIMEOUT = 50000;
	public static final String DEFAULT_CLIENT_HOST = "0.0.0.0";
	public static final int DEFAULT_CLIENT_PORT = 0;
	public static final String DEFAULT_CLIENT_PORT_STRING = "0";
	public static final String DEFAULT_REMOTE_HOST = "localhost";

	private static CmdClient instance;
	protected static CmdCommErrorHandler errorHandler;

	public static CmdClient getInstance() {
		return instance;
	}

	public static void setInstance(CmdClient cmdClient) {
		CmdClient.instance = cmdClient;
	}

	public static void setErrorHandlerInstance(CmdCommErrorHandler errHandler) {
		CmdClient.errorHandler = errHandler;
	}

	public static CmdCommErrorHandler getErrorHandlerInstance() {
		return errorHandler;
	}

	public abstract CmdIF processCommand(CmdIF _outboundCommand);

	public abstract CmdIF processCommandWithTimeout(CmdIF _outboundCommand);

	public abstract boolean isIOExceptionThrown();

	public abstract boolean isClosed();

	public abstract void close() throws IOException;

}