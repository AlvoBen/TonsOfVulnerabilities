package com.sap.sdm.is.cs.ncwrapper.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.sap.bc.cts.tp.net.Service;

/**
 * @author Java Change Management May 18, 2004
 */
final class ServiceWrapper extends AbstractWrapper implements Service {
	private final static Map WRAPPER_MAP = new HashMap();

	static ServiceWrapper getInstance(
			com.sap.sdm.is.cs.ncwrapper.Service wrappedService) {
		if (WRAPPER_MAP.containsKey(wrappedService)) {
			return (ServiceWrapper) WRAPPER_MAP.get(wrappedService);
		} else {
			return new ServiceWrapper(wrappedService);
		}
	}

	private final com.sap.sdm.is.cs.ncwrapper.Service wrappedService;

	private ServiceWrapper(com.sap.sdm.is.cs.ncwrapper.Service wrappedService) {
		super(wrappedService);

		this.wrappedService = wrappedService;

		WRAPPER_MAP.put(wrappedService, this);
	}

	public void serve(InputStream in, OutputStream out)
			throws InterruptedIOException, IOException {
		wrappedService.serve(in, out);
	}

	public void endIt(InputStream in, OutputStream out) {
		wrappedService.endIt(in, out);
	}

	public void setNumber(int number) {
		wrappedService.setNumber(number);
	}

	public int getNumber() {
		return wrappedService.getNumber();
	}

}
