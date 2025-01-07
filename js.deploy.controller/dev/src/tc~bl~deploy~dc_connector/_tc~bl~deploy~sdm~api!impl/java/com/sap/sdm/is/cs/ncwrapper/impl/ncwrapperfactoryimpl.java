package com.sap.sdm.is.cs.ncwrapper.impl;

import java.io.InputStream;
import java.io.OutputStream;

import com.sap.sdm.is.cs.ncwrapper.Listener;
import com.sap.sdm.is.cs.ncwrapper.Manager;
import com.sap.sdm.is.cs.ncwrapper.NCWrapperFactory;
import com.sap.sdm.is.cs.ncwrapper.NetComm;
import com.sap.sdm.is.cs.ncwrapper.ServerSocketFactory;
import com.sap.sdm.is.cs.ncwrapper.ServiceFactory;

/**
 * @author Java Change Management May 18, 2004
 */
final class NCWrapperFactoryImpl extends NCWrapperFactory {
	static NCWrapperFactory INSTANCE = new NCWrapperFactoryImpl();

	private NCWrapperFactoryImpl() {
	}

	public Listener createListener(Manager manager, int port,
			ServiceFactory serviceFactory) {
		return new ListenerWrapper(new com.sap.bc.cts.tp.net.Listener(
				((ManagerWrapper) manager).getWrappedManager(), port,
				ServiceFactoryWrapper.getInstance(serviceFactory)));
	}

	public Listener createListener(Manager manager, int port,
			ServiceFactory serviceFactory,
			ServerSocketFactory serverSocketFactory) {
		return new ListenerWrapper(new com.sap.bc.cts.tp.net.Listener(
				((ManagerWrapper) manager).getWrappedManager(), port,
				ServiceFactoryWrapper.getInstance(serviceFactory),
				ServerSocketFactoryWrapper.getInstance(serverSocketFactory)));
	}

	public Manager createManager(ThreadGroup group, int maxConnections,
			int maxServants, int adminPort, int workerTimeout) {
		com.sap.bc.cts.tp.net.Manager netMgr = new com.sap.bc.cts.tp.net.Manager(
				group, maxConnections, maxServants, adminPort, null);
		netMgr.setWorkerTimeout(workerTimeout);

		return new ManagerWrapper(netMgr);
	}

	public NetComm createNetComm(InputStream in, OutputStream out) {
		return new NetCommWrapper(new com.sap.bc.cts.tp.net.NetComm(in, out));
	}

	public NetComm createNetComm(InputStream in, OutputStream out,
			boolean bufferedSend) {
		return new NetCommWrapper(new com.sap.bc.cts.tp.net.NetComm(in, out,
				bufferedSend));
	}

}
