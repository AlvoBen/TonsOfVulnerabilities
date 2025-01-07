package com.sap.engine.services.dc.api.lcm.impl;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.services.dc.api.lcm.LifeCycleManagerFactory;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-24
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class LCMMapper {

	private static Map lcmResultStatuses = new HashMap();
	private static Map lcmStatuses = new HashMap();
	private static Map lcmStatusDetailsMap = new HashMap();

	static {

		lcmResultStatuses.put(
				com.sap.engine.services.dc.lcm.LCMResultStatus.SUCCESS,
				com.sap.engine.services.dc.api.lcm.LCMResultStatus.SUCCESS);
		lcmResultStatuses.put(
				com.sap.engine.services.dc.lcm.LCMResultStatus.WARNING,
				com.sap.engine.services.dc.api.lcm.LCMResultStatus.WARNING);
		lcmResultStatuses
				.put(
						com.sap.engine.services.dc.lcm.LCMResultStatus.NOT_SUPPORTED,
						com.sap.engine.services.dc.api.lcm.LCMResultStatus.NOT_SUPPORTED);
		lcmResultStatuses.put(
				com.sap.engine.services.dc.lcm.LCMResultStatus.ERROR,
				com.sap.engine.services.dc.api.lcm.LCMResultStatus.ERROR);

		lcmStatuses.put(
				com.sap.engine.services.dc.lcm.LCMStatus.IMPLICIT_STOPPED,
				com.sap.engine.services.dc.api.lcm.LCMStatus.IMPLICIT_STOPPED);
		lcmStatuses.put(com.sap.engine.services.dc.lcm.LCMStatus.NOT_SUPPORTED,
				com.sap.engine.services.dc.api.lcm.LCMStatus.NOT_SUPPORTED);
		lcmStatuses.put(com.sap.engine.services.dc.lcm.LCMStatus.STARTED,
				com.sap.engine.services.dc.api.lcm.LCMStatus.STARTED);
		lcmStatuses.put(com.sap.engine.services.dc.lcm.LCMStatus.STARTING,
				com.sap.engine.services.dc.api.lcm.LCMStatus.STARTING);
		lcmStatuses.put(com.sap.engine.services.dc.lcm.LCMStatus.STOPPED,
				com.sap.engine.services.dc.api.lcm.LCMStatus.STOPPED);
		lcmStatuses.put(com.sap.engine.services.dc.lcm.LCMStatus.STOPPING,
				com.sap.engine.services.dc.api.lcm.LCMStatus.STOPPING);
		lcmStatuses.put(com.sap.engine.services.dc.lcm.LCMStatus.UNKNOWN,
				com.sap.engine.services.dc.api.lcm.LCMStatus.UNKNOWN);
		lcmStatuses.put(com.sap.engine.services.dc.lcm.LCMStatus.UPGRADING,
				com.sap.engine.services.dc.api.lcm.LCMStatus.UPGRADING);

		lcmStatusDetailsMap
				.put(
						com.sap.engine.services.dc.lcm.LCMStatusDetails.IMPLICIT_STOPPED_OK,
						com.sap.engine.services.dc.api.lcm.LCMStatusDetails.IMPLICIT_STOPPED_OK);
		lcmStatusDetailsMap
				.put(
						com.sap.engine.services.dc.lcm.LCMStatusDetails.IMPLICIT_STOPPED_ON_ERROR,
						com.sap.engine.services.dc.api.lcm.LCMStatusDetails.IMPLICIT_STOPPED_ON_ERROR);
		lcmStatusDetailsMap
				.put(
						com.sap.engine.services.dc.lcm.LCMStatusDetails.NO_STOPPED_FLAG,
						com.sap.engine.services.dc.api.lcm.LCMStatusDetails.NO_STOPPED_FLAG);
		lcmStatusDetailsMap.put(
				com.sap.engine.services.dc.lcm.LCMStatusDetails.STOPPED_OK,
				com.sap.engine.services.dc.api.lcm.LCMStatusDetails.STOPPED_OK);
		lcmStatusDetailsMap
				.put(
						com.sap.engine.services.dc.lcm.LCMStatusDetails.STOPPED_ON_ERROR,
						com.sap.engine.services.dc.api.lcm.LCMStatusDetails.STOPPED_ON_ERROR);

	}

	private LCMMapper() {
	}

	static com.sap.engine.services.dc.api.lcm.LCMResultStatus map(
			com.sap.engine.services.dc.lcm.LCMResultStatus lcmResultStatus) {
		final com.sap.engine.services.dc.api.lcm.LCMResultStatus apiLCMResultStatus = (com.sap.engine.services.dc.api.lcm.LCMResultStatus) lcmResultStatuses
				.get(lcmResultStatus);

		if (apiLCMResultStatus == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1056] Unknown LCM status "
							+ lcmResultStatus + " detected.");
		}

		return apiLCMResultStatus;
	}

	static com.sap.engine.services.dc.api.lcm.LCMResult map(
			com.sap.engine.services.dc.lcm.LCMResult lcmResult) {
		final com.sap.engine.services.dc.api.lcm.LCMResultStatus lcmResultStatus = map(lcmResult
				.getLCMResultStatus());

		return LifeCycleManagerFactory.getInstance().createLCMResult(
				lcmResultStatus, lcmResult.getDescription());
	}

	static com.sap.engine.services.dc.api.lcm.LCMStatus mapLCMStatus(
			com.sap.engine.services.dc.lcm.LCMStatus lcmStatus) {
		final com.sap.engine.services.dc.api.lcm.LCMStatus apiLCMStatus = (com.sap.engine.services.dc.api.lcm.LCMStatus) lcmStatuses
				.get(lcmStatus);

		if (apiLCMStatus == null) {
			return com.sap.engine.services.dc.api.lcm.LCMStatus.UNKNOWN;
		}

		final com.sap.engine.services.dc.lcm.LCMStatusDetails lcmStatusDetails = lcmStatus
				.getLCMStatusDetails();

		if (lcmStatusDetails != null) {
			final com.sap.engine.services.dc.api.lcm.LCMStatusDetails apiLCMStatusDetails = (com.sap.engine.services.dc.api.lcm.LCMStatusDetails) lcmStatusDetailsMap
					.get(lcmStatusDetails);
			apiLCMStatusDetails.setDescription(lcmStatusDetails
					.getDescription());

			apiLCMStatus.setLCMStatusDetails(apiLCMStatusDetails);
		}

		return apiLCMStatus;
	}

}
