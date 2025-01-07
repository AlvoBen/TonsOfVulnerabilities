package com.sap.sdm.is.cs.ncwrapper.impl;

import java.util.HashMap;
import java.util.Map;

import com.sap.bc.cts.tp.net.Manager;

/**
 * @author Java Change Management May 18, 2004
 */
final class ManagerWrapper extends AbstractWrapper implements
		com.sap.sdm.is.cs.ncwrapper.Manager {
	private final static Map WRAPPER_MAP = new HashMap();

	static ManagerWrapper getInstance(Manager wrappedManager) {
		if (WRAPPER_MAP.containsKey(wrappedManager)) {
			return (ManagerWrapper) WRAPPER_MAP.get(wrappedManager);
		} else {
			return new ManagerWrapper(wrappedManager);
		}
	}

	private final Manager wrappedManager;

	ManagerWrapper(Manager wrappedManager) {
		super(wrappedManager);

		this.wrappedManager = wrappedManager;

		WRAPPER_MAP.put(wrappedManager, this);
	}

	Manager getWrappedManager() {
		return wrappedManager;
	}
}
