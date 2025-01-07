package com.sap.sdm.is.cs.ncwrapper.impl;

import java.net.ServerSocket;

import com.sap.bc.cts.tp.net.Listener;

/**
 * @author Java Change Management May 18, 2004
 */
final class ListenerWrapper extends AbstractWrapper implements
		com.sap.sdm.is.cs.ncwrapper.Listener {
	private final Listener wrappedListener;

	ListenerWrapper(Listener wrappedListener) {
		super(wrappedListener);

		this.wrappedListener = wrappedListener;
	}

	public ServerSocket getServerSocket() {
		return wrappedListener.getServerSocket();
	}

	public void run() {
		wrappedListener.run();
	}
}
