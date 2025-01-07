package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.services.dc.repo.DCReferenceSubstitutor;
import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.Sdu;

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
final class DependencyResolvabilityChecker {

	private static final DependencyResolvabilityChecker INSTANCE = new DependencyResolvabilityChecker();

	private DependencyResolvabilityChecker() {
	}

	static DependencyResolvabilityChecker getInstance() {
		return INSTANCE;
	}

	boolean isDepResolvableBy(Dependency dependency, Sdu sdu) {
		if (sdu instanceof Sca) {
			return false;
		}

		return doResolve(dependency, (Sda) sdu);
	}

	private static boolean doResolve(Dependency dependency, Sda sda) {
		if (isDepResolvableBy(dependency, sda)) {
			return true;
		}

		// This is commented because component "com.inqmy.lib.xml" is no more
		// available in NY and there is no need to check
		// for fake old dependency - the performance is very poor for this check
		// for a lot of deployed DCs .

		// if
		// (isDepResolvableBy(DCReferenceSubstitutor.substituteFor(dependency),
		// sda)) {
		// return true;
		// }

		return false;
	}

	private static boolean isDepResolvableBy(Dependency dep, Sda sda) {
		if (!dep.getName().equals(sda.getName())) {
			return false;
		}
		if (!dep.getVendor().equals(sda.getVendor())) {
			return false;
		}

		return true;
	}

}