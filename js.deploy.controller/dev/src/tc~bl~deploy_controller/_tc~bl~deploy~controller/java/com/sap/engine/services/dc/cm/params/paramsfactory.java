package com.sap.engine.services.dc.cm.params;

import java.util.Map;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class ParamsFactory {

	private static ParamsFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.params.impl.ParamsFactoryImpl";

	protected ParamsFactory() {
	}

	public static synchronized ParamsFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static ParamsFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (ParamsFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003150 An error occurred while creating an instance of "
					+ "class CMFactory! " + Constants.EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract Param createParam(String name, String value);

	/**
	 * Returns created <code>Param</code>s
	 * 
	 * @param nameValulePairs
	 *            <code>Map</code>, where the key is param name as
	 *            <code>String</code> and value is param value as
	 *            <code>String</code>.
	 * @return <code>Param</code>[]
	 */
	public abstract Param[] createParams(Map nameValulePairs);

	public abstract ParamManager createParamManager()
			throws ParamsFactoryException;

	public abstract ParamManager createParamManager(
			ConfigurationHandlerFactory cfgHandlerFactory)
			throws ParamsFactoryException;

}
