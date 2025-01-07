package com.sap.sdm.is.cs.ncwrapper.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sap.bc.cts.tp.net.NetComm;

/**
 * @author Java Change Management May 18, 2004
 */
final class NetCommWrapper extends AbstractWrapper implements
		com.sap.sdm.is.cs.ncwrapper.NetComm {
	private final NetComm wrappedNetComm;

	NetCommWrapper(NetComm wrappedNetComm) {
		super(wrappedNetComm);

		this.wrappedNetComm = wrappedNetComm;
	}

	public String getEOCS() {
		return NetComm.eocs;
	}

	public OutputStream getOutputStream() {
		return wrappedNetComm.getOutputStream();
	}

	public InputStream getInputStream() {
		return wrappedNetComm.getInputStream();
	}

	public String receive() throws IOException {
		return wrappedNetComm.receive();
	}

	public void send(String str) {
		wrappedNetComm.send(str);
	}

}
