package com.sap.sdm.is.cs.ncwrapper.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import com.sap.bc.cts.tp.net.ServerSocketFactory;

/**
 * @author Java Change Management May 18, 2004
 */
final class ServerSocketFactoryWrapper extends AbstractWrapper implements
		ServerSocketFactory {
	private final static Map WRAPPER_MAP = new HashMap();

	static ServerSocketFactoryWrapper getInstance(
			com.sap.sdm.is.cs.ncwrapper.ServerSocketFactory wrappedFactory) {
		if (WRAPPER_MAP.containsKey(wrappedFactory)) {
			return (ServerSocketFactoryWrapper) WRAPPER_MAP.get(wrappedFactory);
		} else {
			return new ServerSocketFactoryWrapper(wrappedFactory);
		}
	}

	private final com.sap.sdm.is.cs.ncwrapper.ServerSocketFactory wrappedFactory;

	private ServerSocketFactoryWrapper(
			com.sap.sdm.is.cs.ncwrapper.ServerSocketFactory wrappedFactory) {
		super(wrappedFactory);

		this.wrappedFactory = wrappedFactory;

		WRAPPER_MAP.put(wrappedFactory, this);
	}

	public ServerSocket create(int port) throws IOException {
		return wrappedFactory.create(port);
	}

}
