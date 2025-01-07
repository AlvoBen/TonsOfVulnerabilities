package com.sap.engine.services.dc.cm.params.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.services.dc.cm.params.Param;
import com.sap.engine.services.dc.cm.params.ParamManager;
import com.sap.engine.services.dc.cm.params.ParamsFactory;

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
public final class ParamsFactoryImpl extends ParamsFactory {

	public ParamsFactoryImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamsFactory#createParamManager()
	 */
	public ParamManager createParamManager() {
		return new ParamManagerImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamsFactory#createParams(java.
	 * util.Map)
	 */
	public Param[] createParams(Map nameValulePairs) {
		if (nameValulePairs == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003147 The specified Map argument is null.");
		}

		final Param[] params = new Param[nameValulePairs.size()];
		int idx = 0;
		final Set entries = nameValulePairs.entrySet();
		for (Iterator iter = entries.iterator(); iter.hasNext();) {
			final Map.Entry entry = (Map.Entry) iter.next();
			params[idx++] = createParam((String) entry.getKey(), (String) entry
					.getValue());
		}

		return params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamsFactory#createParam(java.lang
	 * .String, java.lang.String)
	 */
	public Param createParam(String name, String value) {
		return new ParamImpl(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamsFactory#createParamManager
	 * (com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory)
	 */
	public ParamManager createParamManager(
			ConfigurationHandlerFactory cfgHandlerFactory) {
		return new ParamManagerImpl(cfgHandlerFactory);
	}

}
