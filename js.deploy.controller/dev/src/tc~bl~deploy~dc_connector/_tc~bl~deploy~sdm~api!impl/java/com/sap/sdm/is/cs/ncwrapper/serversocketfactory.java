package com.sap.sdm.is.cs.ncwrapper;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * A wrapper for <code>com.sap.bc.cts.tp.net.ServerSocketFactory</code>.
 * 
 * See documentation of the wrapped class for further information.
 * 
 * @author Java Change Management May 17, 2004
 */
public interface ServerSocketFactory {
	public ServerSocket create(int port) throws IOException;
}
