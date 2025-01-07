package com.sap.sdm.is.cs.ncwrapper;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A factory for creating wrapper classes for the
 * <code>com.sap.bc.cts.tp.net</code> package.
 * 
 * See documentation of the wrapped package for further information.
 * 
 * @author Java Change Management May 17, 2004
 */
public abstract class NCWrapperFactory {
	private static NCWrapperFactory INSTANCE;

	public static void setFactory(NCWrapperFactory instance) {
		INSTANCE = instance;
	}

	public static NCWrapperFactory getInstance() {
		return INSTANCE;
	}

	public abstract Listener createListener(Manager manager, int port,
			ServiceFactory serviceFactory);

	public abstract Listener createListener(Manager manager, int port,
			ServiceFactory serviceFactory,
			ServerSocketFactory serverSocketFactory);

	public abstract Manager createManager(ThreadGroup group,
			int maxConnections, int maxServants, int adminPort,
			int workerTimeout);

	public abstract NetComm createNetComm(InputStream in, OutputStream out);

	public abstract NetComm createNetComm(InputStream in, OutputStream out,
			boolean bufferedSend);
}
