package com.sap.engine.services.dc.api.impl;

import com.sap.engine.services.dc.api.SettingsFactory;
import com.sap.engine.services.dc.api.deploy.DeploySettings;
import com.sap.engine.services.dc.api.deploy.impl.DeploySettingsImpl;
import com.sap.engine.services.dc.api.undeploy.UndeploySettings;
import com.sap.engine.services.dc.api.undeploy.impl.UndeploySettingsImpl;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2007</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2008-01-07</DD>
 * </DL>
 * 
 * @author Todor Atanasov
 * @version 1.0
 * @since 7.1
 */
public class SettingsFactoryImpl implements SettingsFactory {

	private static SettingsFactoryImpl instance = null;

	private SettingsFactoryImpl() {
	};

	public synchronized static final SettingsFactoryImpl getInstance() {
		if (instance == null) {
			instance = new SettingsFactoryImpl();
		}

		return instance;
	}

	public DeploySettings createDeploySettings() {
		return new DeploySettingsImpl();
	}

	public UndeploySettings createUndeploySettings() {
		return new UndeploySettingsImpl();
	}

}
