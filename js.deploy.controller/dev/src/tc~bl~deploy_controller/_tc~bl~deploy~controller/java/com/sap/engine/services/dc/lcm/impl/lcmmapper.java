package com.sap.engine.services.dc.lcm.impl;

import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.lcm.LCMException;
import com.sap.engine.services.dc.repo.Sda;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class LCMMapper {

	private LCMMapper() {
	}

	static AbstractLCMProcessor map(Sda sda) throws LCMException {
		final SoftwareTypeService softwareTypeService = (SoftwareTypeService) ServerFactory
				.getInstance()
				.createServer()
				.getServerService(
						ServerFactory.getInstance().createSoftwareTypeRequest());
		if (softwareTypeService.getApplicationSoftwareTypes().contains(
				sda.getSoftwareType())) {
			return J2EELCMProcessor.getInstance();
		} else {
			return DefaultLCMProcessor.getInstance();
		}
		// else {
		// throw new
		// IllegalArgumentException("The system does not support life cycle " +
		// "management for the component '" + sda.getId() +
		// "' with software type '" +
		// sda.getSoftwareType() + "'.");
		// }
	}

}
