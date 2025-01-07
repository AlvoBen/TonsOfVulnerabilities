/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.refgraph;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;

/**
 * @author Luchesar Cekov
 */
public class Util {
	private static final String CONTAINER_NAME = "container_name";
	public static final String APPLICATION = "APPLICATION";

	public static DeploymentInfo createDI(String applicationName,
		ReferenceObject[] referencesTo,
		ResourceReference[] resReferencesTo, Resource[] providedResources) {
		DeploymentInfo di = new DeploymentInfo(applicationName);
		di.setReferences(referencesTo);

		ContainerData cdata = new ContainerData(CONTAINER_NAME);

		di.setContainerData(cdata);
		for (int i = 0; i < resReferencesTo.length; i++) {
			di.addResourceReference(CONTAINER_NAME, resReferencesTo[i]);
		}

		for (int i = 0; i < providedResources.length; i++) {
			cdata.addProvidedResource(providedResources[i]);
		}

		cdata.addProvidedResource(makeProvidedResource(applicationName,
			APPLICATION));

		Applications.add(di);
		return di;
	}

	public static DeploymentInfo createDI(String applicationName,
		ReferenceObject[] refs) {
		DeploymentInfo di = new DeploymentInfo(applicationName);
		di.setReferences(refs);
		ContainerData cdata = new ContainerData(CONTAINER_NAME);
		cdata.addProvidedResource(makeProvidedResource(applicationName,
			APPLICATION));
		di.setContainerData(cdata);
		Applications.add(di);
		return di;
	}

	public static DeploymentInfo createDI(Component app,
		ReferenceObject[] referencesTo,
		ResourceReference[] resReferencesTo, Resource[] providedResources) {
		return createDI(app.getName(), referencesTo, resReferencesTo,
			providedResources);
	}

	public static void addReferenceFrom(Map map, String applicationName,
		ReferenceObject ref) {
		DeploymentInfo refto = (DeploymentInfo) map.get(ref.toString());
		if (refto == null)
			return;
		ReferenceObject refFrom = new ReferenceObject(applicationName, ref
				.getReferenceType());
		// refto.addReferenceFrom(refFrom);
	}

	public static void addReferencesFrom(Map map) {
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Entry) iter.next();
			DeploymentInfo di = (DeploymentInfo) entry.getValue();
			ReferenceObject[] refsTo = di.getReferences();
			for (int i = 0; i < refsTo.length; i++) {
				addReferenceFrom(map, di.getApplicationName(), refsTo[i]);
			}
		}
	}

	public static ReferenceObject makeHardReftoApplicaiton(Component app) {
		return makeHardReftoApplicaiton(app.getName());
	}

	public static ReferenceObject makeHardReftoApplicaiton(String app) {
		return new ReferenceObject(app, ReferenceObjectIntf.REF_TYPE_HARD);
	}

	public static ReferenceObject makeWeakRefToApplicaiton(String app) {
		return new ReferenceObject(app, ReferenceObjectIntf.REF_TYPE_WEAK);
	}

	public static ResourceReference makeResourceRef(String refName,
			String refType) {
		return new ResourceReference(refName, refType,
				ReferenceObjectIntf.REF_TYPE_HARD);
	}

	public static ResourceReference makeResourceApplicationRef(
			String compositAppNameName) {
		return makeResourceRef(compositAppNameName, APPLICATION);
	}

	public static ResourceReference makeResourceApplicationRef(
		Component compositApp) {
		return makeResourceRef(compositApp.getName(), APPLICATION);
	}

	public static Resource makeProvidedResource(String resName, String resType) {
		return new Resource(resName, resType, Resource.AccessType.PUBLIC);
	}

}
