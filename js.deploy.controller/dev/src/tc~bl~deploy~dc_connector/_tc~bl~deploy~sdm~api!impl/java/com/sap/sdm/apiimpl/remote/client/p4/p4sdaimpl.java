/*
 * Created on 2005-2-8
 *
 * Author: Lalo Ivanov
 * Team: Software Deployment Manager(SDM)
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import java.util.Iterator;
import java.util.Set;

import com.sap.sdm.api.remote.model.Dependency;
import com.sap.sdm.api.remote.model.Sda;

final class P4SdaImpl extends P4SduImpl implements Sda {

	private static final String NEW_LINE = "\r\n";

	private Dependency[] dependencies;
	private String softwareType;

	P4SdaImpl(com.sap.engine.services.dc.api.model.Sda sda) {
		super(sda);
		this.dependencies = buildDependencies(sda.getDependencies());
		this.softwareType = sda.getSoftwareType().getName();
	}

	public String getSoftwareType() {
		return softwareType;
	}

	public Dependency[] getDependencies() {
		return dependencies;
	}

	public String toString() {
		StringBuffer sBuf = new StringBuffer(super.toString());
		sBuf.append("Software type: ").append(softwareType).append(NEW_LINE);
		if (dependencies == null || dependencies.length == 0) {
			sBuf.append("No dependencies").append(NEW_LINE);
		} else {
			for (int i = 0; i < dependencies.length; i++) {
				sBuf.append(dependencies[i].toString()).append(NEW_LINE);
			}
		}
		return sBuf.toString();
	}

	private Dependency[] buildDependencies(Set dependencies) {
		if (dependencies == null) {
			return null;
		}

		Dependency[] resultDependecy = new Dependency[dependencies.size()];

		int i = 0;
		for (Iterator iter = dependencies.iterator(); iter.hasNext();) {
			com.sap.engine.services.dc.api.model.Dependency dependency = (com.sap.engine.services.dc.api.model.Dependency) iter
					.next();
			resultDependecy[i] = P4ModelFactoryImpl.getInstance()
					.createDependency(dependency);
			i++;
		}

		return resultDependecy;
	}

}
