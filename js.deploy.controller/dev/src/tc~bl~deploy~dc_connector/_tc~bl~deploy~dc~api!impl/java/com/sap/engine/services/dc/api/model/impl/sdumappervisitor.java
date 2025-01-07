/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.model.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.api.model.ModelFactory;
import com.sap.engine.services.dc.api.model.ScaId;
import com.sap.engine.services.dc.api.model.SdaId;
import com.sap.engine.services.dc.api.model.SoftwareType;
import com.sap.engine.services.dc.api.model.Version;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-11
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class SduMapperVisitor implements
		com.sap.engine.services.dc.repo.SduVisitor {
	private com.sap.engine.services.dc.api.model.Sdu createdSdu;

	public SduMapperVisitor() {
	}

	protected com.sap.engine.services.dc.api.model.Sda createSda(
			com.sap.engine.services.dc.repo.Sda remoteSda) {

		final com.sap.engine.services.dc.repo.Version remoteVersion = remoteSda
				.getVersion();
		final com.sap.engine.services.dc.repo.SoftwareType remoteSoftwareType = remoteSda
				.getSoftwareType();
		final Set remoteDependencies = remoteSda.getDependencies();
		final Set remoteDependingFrom = remoteSda.getDependingFrom();
		final Version version = mapVersion(remoteVersion);
		final SoftwareType softwareType = mapSoftwareType(remoteSoftwareType);
		final String componentElementXML = remoteSda.getComponentElementXML();
		final String csnComponent = remoteSda.getCsnComponent();
		final Set dependencies = mapDependencies(remoteDependencies);
		final Set dependendingFrom = mapDependencies(remoteDependingFrom);
		final ScaId scaId = mapScaId(remoteSda.getScaId());

		return ModelFactoryImpl.getInstance().createSda(remoteSda.getName(),
				remoteSda.getVendor(), remoteSda.getLocation(), version,
				softwareType, componentElementXML, csnComponent, dependencies,
				dependendingFrom, scaId);
	}

	public synchronized void visit(com.sap.engine.services.dc.repo.Sda remoteSda) {
		this.createdSdu = createSda(remoteSda);
	}

	public synchronized void visit(com.sap.engine.services.dc.repo.Sca remoteSca) {
		com.sap.engine.services.dc.repo.Version remoteVersion = remoteSca
				.getVersion();
		Version version = mapVersion(remoteVersion);
		Set sdaIdSet = new HashSet();
		Set set = remoteSca.getSdaIds();
		if (set != null && !set.isEmpty()) {
			com.sap.engine.services.dc.repo.SdaId dcSdaId;
			ModelFactory sduFactory = ModelFactoryImpl.getInstance();
			SdaId tmpSdaId;
			for (Iterator iterator = set.iterator(); iterator.hasNext();) {
				dcSdaId = (com.sap.engine.services.dc.repo.SdaId) iterator
						.next();
				if (dcSdaId != null) {
					tmpSdaId = sduFactory.createSdaId(dcSdaId.getName(),
							dcSdaId.getVendor());
					sdaIdSet.add(tmpSdaId);
				}
			}
		}
		Set originalSdaIdSet = new HashSet();
		Set originalSet = remoteSca.getOrigSdaIds();
		if (originalSet != null && !originalSet.isEmpty()) {
			com.sap.engine.services.dc.repo.SdaId dcSdaId;
			ModelFactory sduFactory = ModelFactoryImpl.getInstance();
			SdaId tmpSdaId;
			for (Iterator iterator = originalSet.iterator(); iterator.hasNext();) {
				dcSdaId = (com.sap.engine.services.dc.repo.SdaId) iterator
						.next();
				if (dcSdaId != null) {
					tmpSdaId = sduFactory.createSdaId(dcSdaId.getName(),
							dcSdaId.getVendor());
					originalSdaIdSet.add(tmpSdaId);
				}
			}
		}
		
		Set notDeployedSdaIds = new HashSet();
		notDeployedSdaIds.addAll(originalSdaIdSet);
		notDeployedSdaIds.removeAll(sdaIdSet);

		this.createdSdu = ModelFactoryImpl.getInstance().createSca(
				remoteSca.getName(), remoteSca.getVendor(),
				remoteSca.getLocation(), version,
				remoteSca.getComponentElementXML(),
				remoteSca.getCsnComponent(), 
				sdaIdSet, originalSdaIdSet, notDeployedSdaIds);
	}

	public synchronized com.sap.engine.services.dc.api.model.Sdu getGeneratedSdu() {
		return this.createdSdu;
	}

	private Version mapVersion(com.sap.engine.services.dc.repo.Version version) {
		if (version != null) {
			return ModelFactoryImpl.getInstance().createVersion(
					version.getVersionAsString());
		}

		return null;
	}

	private Set mapDependencies(Set remoteDependencies) {
		Set dependencies = new HashSet();
		if (remoteDependencies != null) {
			com.sap.engine.services.dc.repo.Dependency nextRemoteDependency;
			for (Iterator iterator = remoteDependencies.iterator(); iterator
					.hasNext();) {
				nextRemoteDependency = (com.sap.engine.services.dc.repo.Dependency) iterator
						.next();
				dependencies.add(ModelFactoryImpl.getInstance()
						.createDependency(nextRemoteDependency.getName(),
								nextRemoteDependency.getVendor()));
			}
		}
		return dependencies;
	}

	private SoftwareType mapSoftwareType(
			com.sap.engine.services.dc.repo.SoftwareType softwareType) {
		if (softwareType != null) {
			return ModelFactoryImpl.getInstance().createSoftwareType(
					softwareType.getName(), softwareType.getSubTypeName(),
					softwareType.getDescription());
		}
		return null;
	}

	private com.sap.engine.services.dc.api.model.ScaId mapScaId(
			com.sap.engine.services.dc.repo.ScaId scaId) {
		if (scaId == null) {
			return null;
		}

		return ModelFactoryImpl.getInstance().createScaId(scaId.getName(),
				scaId.getVendor());
	}
}
