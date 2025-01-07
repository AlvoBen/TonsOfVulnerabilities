package com.sap.engine.services.dc.cm.utils.filters.impl;

import com.sap.engine.services.dc.cm.utils.filters.BatchFilter;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SoftwareType;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-20
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class SoftwareTypeBatchFilter implements BatchFilter {

	private static final long serialVersionUID = 2286721579262914916L;

	private static final String FILTER_NAME = "Software Type Batch Filter";

	private final SoftwareType softwareType;
	private final String toString;

	SoftwareTypeBatchFilter(String softwareTypeName) {
		this(softwareTypeName, null);
	}

	SoftwareTypeBatchFilter(String softwareTypeName, String softwareSubTypeName) {
		if (softwareTypeName == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3417] The software type could not be null.");
		}

		this.softwareType = SoftwareType.getSoftwareTypeByName(
				softwareTypeName, softwareSubTypeName);
		this.toString = FILTER_NAME
				+ ": filtered software type "
				+ softwareTypeName
				+ (softwareSubTypeName == null ? "" : ", software sub type "
						+ softwareSubTypeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.utils.filters.BatchFilter#accept(com.sap
	 * .engine.services.dc.cm.deploy.DeploymentBatchItem)
	 */
	public boolean accept(Sdu sdu) {
		if (sdu == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3419] The sdu could not be null.");
		}

		if (sdu instanceof Sda) {
			return ((Sda) sdu).getSoftwareType().equals(this.softwareType);
		}

		return true;
	}

	public String toString() {
		return this.toString;
	}

}
