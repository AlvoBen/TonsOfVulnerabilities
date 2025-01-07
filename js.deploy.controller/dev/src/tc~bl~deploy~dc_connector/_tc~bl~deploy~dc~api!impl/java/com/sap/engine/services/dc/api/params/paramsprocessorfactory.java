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
package com.sap.engine.services.dc.api.params;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Factory providing mechanism for creating ParamsProcessor.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Jan 26, 2005</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public abstract class ParamsProcessorFactory {
	private static ParamsProcessorFactory instance;

	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.api.params.impl.ParamsProcessorFactoryImpl";

	public synchronized static final ParamsProcessorFactory getInstance() {
		if (instance == null) {
			try {
				Class classFactory = Class.forName(FACTORY_IMPL);
				instance = (ParamsProcessorFactory) classFactory.newInstance();
			} catch (Exception e) {
				final String errMsg = "[ERROR CODE DPL.DCAPI.1138] An error occurred while creating an "
						+ "instance of DeployProcessorFactory! "
						+ DAConstants.EOL + e.getMessage();
				throw new RuntimeException(errMsg);
			}

		}
		return instance;
	}

	/**
	 * Creates new Parameters processor implementation.
	 * 
	 * @param session
	 *            client session
	 * @return new Parameters processor implementation.
	 * @throws ConnectionException
	 * @throws ParamsException
	 */
	public abstract ParamsProcessor createParamsProcessor(Session session)
			throws ParamsException, ConnectionException;
}