package com.sap.sdm.is.cs.remoteproxy.client;

/**
 * @author Christian Gabrisch 11.08.2003
 */
public abstract class RemoteProxyClientFactory {
	private static RemoteProxyClientFactory instance;

	public static void setInstance(RemoteProxyClientFactory instance) {
		RemoteProxyClientFactory.instance = instance;
	}

	public static RemoteProxyClientFactory getInstance() {
		return instance;
	}

	public abstract RemoteProxyClient createNewClient(
			String id, // kept for downward compatibility to older SDM servers
			ClassLoader classLoader, CmdClientGetter cmdClientGetter,
			ProxyLockHandler proxyLockHandler, ExceptionFactory excFactory,
			ServerExceptionWrapperFactory srvExcWrapperFactory, int cacheSize);
}
