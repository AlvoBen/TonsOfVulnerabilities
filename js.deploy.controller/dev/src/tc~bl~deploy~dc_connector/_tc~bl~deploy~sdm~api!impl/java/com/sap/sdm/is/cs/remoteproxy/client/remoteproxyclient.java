package com.sap.sdm.is.cs.remoteproxy.client;

import com.sap.sdm.is.cs.remoteproxy.common.InterfaceID;

/**
 * @author Christian Gabrisch 11.08.2003
 */
public interface RemoteProxyClient {
	/**
	 * Creates the initial proxy object of this <code>RemoteProxyClient</code>.
	 * 
	 * @throws Exception
	 *             an Exception created by this <code>RemoteProxyClient</code>'s
	 *             <code>ExceptionFactory</code> if the proxy object cannot be
	 *             created.
	 */
	public Object createInitialProxy(String interfaceName, String methodName)
			throws Exception;
}
