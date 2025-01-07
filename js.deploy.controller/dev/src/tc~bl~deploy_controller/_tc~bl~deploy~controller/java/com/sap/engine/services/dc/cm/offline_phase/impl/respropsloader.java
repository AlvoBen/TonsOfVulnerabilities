package com.sap.engine.services.dc.cm.offline_phase.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

import com.sap.engine.services.dc.cm.offline_phase.OfflinePhaseProcessException;
import com.sap.engine.services.dc.util.PropertiesLoader;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-6-29
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class ResPropsLoader {
	private static final String RESOURCE_PROPS = "com/sap/engine/services/dc/cm/offline_phase/resources/res.properties";

	private static final ResPropsLoader INSTANCE = new ResPropsLoader();

	public static ResPropsLoader getInstance() {
		return INSTANCE;
	}

	private ResPropsLoader() {
	}

	public Properties loadResProps() throws OfflinePhaseProcessException {
		try {
			return PropertiesLoader.load(RESOURCE_PROPS);
		} catch (IOException ioe) {
			throw new OfflinePhaseProcessException(
					"ASJ.dpl_dc.003117 An error occurred while loading the property file "
							+ RESOURCE_PROPS, ioe);
		}
	}

	public String[] loadJarNames() throws OfflinePhaseProcessException {
		final Properties resProps = loadResProps();

		final Collection jarNames = new ArrayList();
		if (resProps != null && !resProps.isEmpty()) {
			final Enumeration propNamesEnum = resProps.propertyNames();

			while (propNamesEnum.hasMoreElements()) {
				final String jarName = (String) propNamesEnum.nextElement();
				if (jarName != null && !jarName.trim().equals("")) {
					jarNames.add(jarName);
				}
			}
		}

		return (String[]) jarNames.toArray(new String[jarNames.size()]);
	}
}
