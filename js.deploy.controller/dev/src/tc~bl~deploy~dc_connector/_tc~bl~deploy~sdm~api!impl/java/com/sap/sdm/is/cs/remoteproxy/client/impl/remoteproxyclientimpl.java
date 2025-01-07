package com.sap.sdm.is.cs.remoteproxy.client.impl;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Vector;

import com.sap.sdm.is.cache.Cache;
import com.sap.sdm.is.cache.CacheFactory;
import com.sap.sdm.is.cs.cmd.CmdError;
import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.client.CmdClient;
import com.sap.sdm.is.cs.remoteproxy.client.CmdClientGetter;
import com.sap.sdm.is.cs.remoteproxy.client.ExceptionFactory;
import com.sap.sdm.is.cs.remoteproxy.client.ProxyLockHandler;
import com.sap.sdm.is.cs.remoteproxy.client.RemoteProxyClient;
import com.sap.sdm.is.cs.remoteproxy.client.ServerExceptionWrapperFactory;
import com.sap.sdm.is.cs.remoteproxy.common.CmdFactory;
import com.sap.sdm.is.cs.remoteproxy.common.CmdRemoteCall;
import com.sap.sdm.is.cs.remoteproxy.common.CmdRemoteException;
import com.sap.sdm.is.cs.remoteproxy.common.CmdRemoteReturn;
import com.sap.sdm.is.cs.remoteproxy.common.InterfaceID;
import com.sap.sdm.is.cs.remoteproxy.common.InterfaceIDFactory;

/**
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 * 
 */
final class RemoteProxyClientImpl implements RemoteProxyClient {
	private final Cache proxyCache;
	private final List clientInstanceDeletionList = new Vector();
	private final ClassLoader classLoader;
	private final CmdClientGetter cmdClientGetter;
	private final ExceptionFactory exceptionFactory;
	private final ServerExceptionWrapperFactory serverExceptionWrapperFactory;
	private final String id;
	private final ProxyLockHandler proxyLockHandler;

	RemoteProxyClientImpl(String id, ClassLoader classLoader,
			CmdClientGetter clientGetter, ProxyLockHandler proxyLockHandler,
			ExceptionFactory excFactory,
			ServerExceptionWrapperFactory srvExcWrapperFactory, int cacheSize) {
		this.id = id;
		this.classLoader = classLoader;
		this.cmdClientGetter = clientGetter;
		this.proxyLockHandler = proxyLockHandler;
		this.exceptionFactory = excFactory;
		this.serverExceptionWrapperFactory = srvExcWrapperFactory;
		proxyCache = CacheFactory.getInstance().newLruCache(cacheSize);
	}

	public Object createInitialProxy(String interfaceName, String methodName)
			throws Exception {
		CmdRemoteCall cmdRC = CmdFactory.createCmdRemoteCall(id, interfaceName,
				methodName, null);
		CmdIF answerCmd = cmdClientGetter.get().processCommand(cmdRC);
		if (answerCmd instanceof CmdError) {
			Exception e = exceptionFactory
					.create("Error received from server: "
							+ ((CmdError) answerCmd).getErrorText());
			throw e;
		}
		if (answerCmd instanceof CmdRemoteException) {
			Exception e = exceptionFactory
					.create(((CmdRemoteException) answerCmd).getExceptionMsg());
			throw e;
		}
		if (answerCmd instanceof CmdRemoteReturn) {
			CmdRemoteReturn initReturn = (CmdRemoteReturn) answerCmd;
			InterfaceID clIFID = InterfaceIDFactory.getInstance()
					.createInterfaceID(interfaceName,
							initReturn.getInstanceID());
			return buildNewProxy(interfaceName, clIFID, initReturn
					.hasCacheableNoArgMethods());
		} else {
			Exception e = exceptionFactory
					.create("Received unexpected server answer: "
							+ answerCmd.getMyName());
			throw e;
		}

	}

	Object searchInstanceByID(InterfaceID ciID) {
		return proxyCache.get(ciID);
	}

	void registerIDForDeletion(InterfaceID ciID) {
		synchronized (clientInstanceDeletionList) {
			if (ciID != null) {
				clientInstanceDeletionList.add(ciID);
			}
		}
	}

	InterfaceID[] getIDDeletionArr() {
		InterfaceID[] resultArr = null;
		synchronized (clientInstanceDeletionList) {
			resultArr = new InterfaceID[clientInstanceDeletionList.size()];
			clientInstanceDeletionList.toArray(resultArr);
			clientInstanceDeletionList.clear();
		}
		return resultArr;
	}

	String getID() {
		return id;
	}

	Object buildNewProxy(String interfaceName, InterfaceID newID,
			boolean hasCacheableNoArgMethods) {
		Object newProxy = null;
		try {
			ClientInvocationHandler handler = new ClientInvocationHandler(
					newID, this, hasCacheableNoArgMethods);
			newProxy = Proxy.newProxyInstance(classLoader, new Class[] { Class
					.forName(interfaceName) }, handler);
			handler.setProxyInstance(newProxy);
		} catch (ClassNotFoundException newProxyExc) {
			throw new RuntimeException("Runtime Exception: "
					+ newProxyExc.getMessage());
		}
		proxyCache.put(newID, newProxy);

		return newProxy;
	}

	CmdClient getCmdClient() throws CmdClientGetter.Exception {
		return cmdClientGetter.get();
	}

	ProxyLockHandler getProxyLockHandler() {
		return this.proxyLockHandler;
	}

	ExceptionFactory getExceptionFactory() {
		return exceptionFactory;
	}

	/**
	 * @return Returns the serverExceptionWrapperFactory.
	 */
	public ServerExceptionWrapperFactory getServerExceptionWrapperFactory() {
		return serverExceptionWrapperFactory;
	}
}
