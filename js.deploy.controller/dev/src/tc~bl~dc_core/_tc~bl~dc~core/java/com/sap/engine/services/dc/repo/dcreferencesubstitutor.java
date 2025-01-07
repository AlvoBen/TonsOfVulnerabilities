package com.sap.engine.services.dc.repo;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.services.dc.repo.impl.DCReferenceImpl;
import com.sap.engine.services.dc.repo.impl.DependencyImpl;
import com.sap.engine.services.dc.repo.impl.SdaIdImpl;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-9
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class DCReferenceSubstitutor {

	private final static Map DEPS_MAP = new HashMap();

	static {

		DCReference key, val;

		key = new DCReferenceImpl("com.inqmy.lib.xml", "sap.com");
		val = new DCReferenceImpl("sapxmltoolkit", "sap.com");
		DEPS_MAP.put(key, val);

		key = new DCReferenceImpl("com.inqmy.lib.xml", "com.sap");
		val = new DCReferenceImpl("sapxmltoolkit", "sap.com");
		DEPS_MAP.put(key, val);

	}

	private DCReferenceSubstitutor() {
	}

	public static Dependency substituteFor(Dependency dep) {
		final DCReference dcRef = new DCReferenceImpl(dep.getName(), dep
				.getVendor());

		final DCReference key = findKey(dcRef);

		if (key == null) {
			return dep;
		}

		final DCReference substDcRef = getValue(key);

		return new DependencyImpl(substDcRef);
	}

	public static SdaId substituteFor(SdaId sdaId) {
		final DCReference dcRef = new DCReferenceImpl(sdaId.getName(), sdaId
				.getVendor());

		final DCReference key = findKey(dcRef);

		if (key == null) {
			return sdaId;
		}

		final DCReference substDcRef = getValue(key);

		return new SdaIdImpl(substDcRef.getName(), substDcRef.getVendor());
	}

	private static DCReference findKey(DCReference dcRef) {
		if (DEPS_MAP.containsKey(dcRef)) {
			return dcRef;
		} else {
			return null;
		}
	}

	private static DCReference getValue(DCReference dcRef) {
		return (DCReference) DEPS_MAP.get(dcRef);
	}

}
