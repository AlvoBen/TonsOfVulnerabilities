package com.sap.engine.services.dc.cm.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.DeploymentsContainer;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-16
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class SduDependingFinder {

	private SduDependingFinder() {
	}

	public static Set findSdasDependingOnThoseRecurByIds(Set sdaIds) {
		final Set result = new HashSet();
		final DeploymentsContainer deplContainer = RepositoryContainer
				.getDeploymentsContainer();
		for (Iterator<SdaId> iter = sdaIds.iterator(); iter.hasNext();) {
			result.addAll(deplContainer.getRecursiveAllDependingFrom(iter
					.next()));
		}
		return result;
	}

	public static Set<Sdu> findSdasDependingOnThoseRecur(Set<Sdu> sdas) {
		final Set<Sdu> result = new HashSet<Sdu>();
		final DeploymentsContainer deplContainer = RepositoryContainer
				.getDeploymentsContainer();
		for (Iterator<Sdu> iter = sdas.iterator(); iter.hasNext();) {
			result.addAll(deplContainer.getRecursiveAllDependingFrom(iter
					.next()));
		}
		return result;
	}
}
