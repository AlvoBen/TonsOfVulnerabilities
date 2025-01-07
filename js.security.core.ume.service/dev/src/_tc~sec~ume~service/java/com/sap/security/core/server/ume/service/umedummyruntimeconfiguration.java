package com.sap.security.core.server.ume.service;

import java.util.Properties;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;
import com.sap.tc.logging.Location;

public class UMEDummyRuntimeConfiguration extends RuntimeConfiguration {

	private Location _loc = Location.getLocation(UMEDummyRuntimeConfiguration.class);

	@SuppressWarnings("unused")
	@Override
	public void updateProperties(Properties updatedProperties) throws ServiceException {
		_loc.debugT("UME dummy RuntimeConfiguration instance notified. This listener " +
			"will not do anything because there is another listener based on the " +
			"configuration manager API / configuration library which handles UME " +
			"properties updates.");
	}

}
