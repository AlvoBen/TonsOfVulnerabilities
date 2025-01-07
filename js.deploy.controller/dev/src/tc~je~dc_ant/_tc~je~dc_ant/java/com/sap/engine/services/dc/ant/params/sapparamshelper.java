/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.ant.params;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.ant.SAPJ2EEEngine;
import com.sap.engine.services.dc.ant.params.SAPParametersData;
import com.sap.engine.services.dc.ant.params.SAPParams;
import com.sap.engine.services.dc.ant.params.SAPParamsException;
import com.sap.engine.services.dc.ant.params.SAPParamsResult;
import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.Client;
import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.params.Param;
import com.sap.engine.services.dc.api.params.ParamsException;
import com.sap.engine.services.dc.api.params.ParamsProcessor;

/**
 * 
 * This class contains the complete functionality for triggering a parameter
 * change on the engine.
 * 
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public final class SAPParamsHelper {

	private static SAPParamsHelper INSTANCE;

	public static synchronized SAPParamsHelper getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SAPParamsHelper();
		}
		return INSTANCE;
	}

	private SAPParamsHelper() {
	}

	public SAPParamsResult setParameters(SAPParams task,
			SAPParametersData paramsData) throws SAPParamsException {
		final ParamsProcessor paramsProcessor = getParamsProcessor(paramsData
				.getEngine());
		int niParamsUpdated = 0;
		int niParamsAdded = 0;
		int niParamsRemoved = 0;
		try {
			Collection newParams = paramsData.getParams();
			final Param[] currentParams = paramsProcessor.getAllParams();
			for (int i = 0; i < currentParams.length; i++) {
				Param param = currentParams[i];
				String[] paramMatch = null;
				for (Iterator iter = newParams.iterator(); iter.hasNext();) {
					String[] paramData = (String[]) iter.next();
					if (param.getName().equals(paramData[0])) {
						if (!param.getValue().equals(paramData[1])) {
							task.log("update param '" + param.getName()
									+ "' -> " + paramData[1]);
							paramsProcessor
									.updateParam(paramsProcessor.createParam(
											param.getName(), paramData[1]));
							niParamsUpdated++;
						}
						paramMatch = paramData;
						break;
					}
				}
				// parameter is already handled
				if (paramMatch != null) {
					newParams.remove(paramMatch);
				} else if (paramsData.isRemove()) {
					task.log("remove param '" + param.getName() + "'");
					paramsProcessor.removeParam(param);
					niParamsRemoved++;
				}
			}
			// add remaining new parameters
			Iterator leftParams = newParams.iterator();
			while (leftParams.hasNext()) {
				String[] paramData = (String[]) leftParams.next();
				String sName = paramData[0];
				String sValue = paramData[1];
				task.log("add param '" + sName + "'");
				paramsProcessor.addParam(paramsProcessor.createParam(sName,
						sValue));
				niParamsAdded++;
			}
		} catch (ParamsException pe) {
			throw new SAPParamsException(pe);
		}
		return new SAPParamsResult(niParamsAdded, niParamsUpdated,
				niParamsRemoved);
	}

	private ParamsProcessor getParamsProcessor(SAPJ2EEEngine engine)
			throws SAPParamsException {
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());

		final Client dcClient;
		try {
			dcClient = ClientFactory.getInstance().createClient(
					engine.getServerHost(), engine.getServerPort(),
					engine.getUserName(), engine.getUserPassword());
		} catch (AuthenticationException ae) {
			throw new SAPParamsException(ae);
		} catch (ConnectionException ce) {
			throw new SAPParamsException(ce);
		}

		final ParamsProcessor paramsProcessor;
		try {
			paramsProcessor = dcClient.getComponentManager()
					.getParamsProcessor();
		} catch (ParamsException de) {
			throw new SAPParamsException(de);
		} catch (ConnectionException ce) {
			throw new SAPParamsException(ce);
		}

		return paramsProcessor;
	}
}
