package com.sap.engine.services.dc.gd;

import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-18
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public abstract class GDFactory {

	private static GDFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.gd.impl.GDFactoryImpl";

	protected GDFactory() {
	}

	public static synchronized GDFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static GDFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (GDFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003311 An error occurred while creating an instance of "
					+ "class GDFactory! " + Constants.EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract GenericDelivery createGenericDelivery(
			DeliveryType deliveryType) throws DeliveryException;

	public abstract SyncDelivery createSyncDelivery();

}
