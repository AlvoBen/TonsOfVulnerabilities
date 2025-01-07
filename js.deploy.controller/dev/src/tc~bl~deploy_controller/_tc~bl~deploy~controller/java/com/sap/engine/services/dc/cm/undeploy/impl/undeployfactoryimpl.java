package com.sap.engine.services.dc.cm.undeploy.impl;

import java.rmi.RemoteException;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.security.authorize.AuthorizationException;
import com.sap.engine.services.dc.cm.security.authorize.Authorizer;
import com.sap.engine.services.dc.cm.security.authorize.AuthorizerFactory;
import com.sap.engine.services.dc.cm.undeploy.AbstractUndeployFactory;
import com.sap.engine.services.dc.cm.undeploy.Undeployer;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentException;
import com.sap.engine.services.dc.util.ComponentPropsCorrector;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.UndeployItemStatusLogger;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class UndeployFactoryImpl extends AbstractUndeployFactory {
	
	private Location location = DCLog.getLocation(this.getClass());

	public UndeployFactoryImpl() throws RemoteException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.AbstractUndeployFactory#
	 * createUndeployer()
	 */
	public Undeployer createUndeployer() throws RemoteException {
		final String performerUserUniqueId = doAuthorize();
		return new UndeployerImpl(performerUserUniqueId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.AbstractUndeployFactory#
	 * createUndeployItem(java.lang.String, java.lang.String)
	 */
	public UndeployItem createUndeployItem(String name, String vendor)
			throws UndeploymentException {
		final String correctedName = getCorrectedParameter(name, "name");
		final String correctedVendor = getCorrectedParameter(vendor, "vendor");

		UndeployItemImpl item = new UndeployItemImpl(correctedName,
				correctedVendor);

		item.addUndeployItemObserver(UndeployItemStatusLogger.getInstance());
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.AbstractUndeployFactory#
	 * createUndeployItem(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	public UndeployItem createUndeployItem(String name, String vendor,
			String location, String version) throws UndeploymentException {
		final String correctedName = getCorrectedParameter(name, "name");
		final String correctedVendor = getCorrectedParameter(vendor, "vendor");

		UndeployItemImpl item = new UndeployItemImpl(correctedName,
				correctedVendor, location, version);
		item.addUndeployItemObserver(UndeployItemStatusLogger.getInstance());

		return item;
	}

	private String getCorrectedParameter(String parameter, String parameterName) {
		if (parameter == null) {
			return null;
		}

		final String trimmedParameter = parameter.trim();
		final String correctedParameter = ComponentPropsCorrector
				.getCorrected(trimmedParameter);

		checkCorrectedParameter(trimmedParameter, correctedParameter,
				parameterName);

		return correctedParameter;
	}

	private void checkCorrectedParameter(String value, String correctedValue,
			String paramName) {
		if (!value.equals(correctedValue)) {
			DCLog
					.logInfo(location, 
							"ASJ.dpl_dc.002550",
							"Undeploy item property [{0}] [{1}] will be corrected to [{2}] because it contains charactes which are not allowed by the system.",
							new Object[] { paramName, value, correctedValue });
		}
	}

	private String doAuthorize() throws AuthorizationException {
		final Authorizer authorizer = AuthorizerFactory.getInstance()
				.createAuthorizer();

		authorizer.doAuthorize();

		return authorizer.getUserUniqueId();
	}

	public ScaUndeployItem createScaUndeployItem(String name, String vendor,
			String location, String version) throws UndeploymentException {
		final String correctedName = getCorrectedParameter(name, "name");
		final String correctedVendor = getCorrectedParameter(vendor, "vendor");

		ScaUndeployItemImpl item = new ScaUndeployItemImpl(correctedName,
				correctedVendor, location, version);
		item.addUndeployItemObserver(UndeployItemStatusLogger.getInstance());

		return item;
	}

	public ScaUndeployItem createScaUndeployItem(String name, String vendor)
			throws UndeploymentException {
		final String correctedName = getCorrectedParameter(name, "name");
		final String correctedVendor = getCorrectedParameter(vendor, "vendor");

		ScaUndeployItemImpl item = new ScaUndeployItemImpl(correctedName,
				correctedVendor);

		item.addUndeployItemObserver(UndeployItemStatusLogger.getInstance());
		return item;
	}


	
	
}


