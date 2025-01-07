package com.sap.sdm.is.cs.ncwrapper.impl;

import java.util.HashMap;
import java.util.Map;

import com.sap.bc.cts.tp.net.Manager;
import com.sap.bc.cts.tp.net.Service;
import com.sap.bc.cts.tp.net.ServiceFactory;

/**
 * @author Java Change Management May 18, 2004
 */
final class ServiceFactoryWrapper extends AbstractWrapper implements
		ServiceFactory {
	private final static Map WRAPPER_MAP = new HashMap();

	static ServiceFactoryWrapper getInstance(
			com.sap.sdm.is.cs.ncwrapper.ServiceFactory wrappedFactory) {
		if (WRAPPER_MAP.containsKey(wrappedFactory)) {
			return (ServiceFactoryWrapper) WRAPPER_MAP.get(wrappedFactory);
		} else {
			return new ServiceFactoryWrapper(wrappedFactory);
		}
	}

	private final com.sap.sdm.is.cs.ncwrapper.ServiceFactory wrappedFactory;

	private ServiceFactoryWrapper(
			com.sap.sdm.is.cs.ncwrapper.ServiceFactory wrappedFactory) {
		super(wrappedFactory);

		this.wrappedFactory = wrappedFactory;

		WRAPPER_MAP.put(wrappedFactory, this);
	}

	public Service makeService(Manager manager) {
		return ServiceWrapper.getInstance(wrappedFactory
				.makeService(ManagerWrapper.getInstance(manager)));
	}

}
