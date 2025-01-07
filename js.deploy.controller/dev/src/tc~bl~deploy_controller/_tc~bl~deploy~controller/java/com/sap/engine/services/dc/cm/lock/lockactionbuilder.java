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
package com.sap.engine.services.dc.cm.lock;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public abstract class LockActionBuilder {

	private static LockActionBuilder INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.lock.impl.LockActionBuilderImpl";

	public LockActionBuilder() {
	}

	public static synchronized LockActionBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static LockActionBuilder createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (LockActionBuilder) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003109 An error occurred while creating an instance of "
					+ "class LockActionBuilder! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract LockActionLocation buildSingleThread();

	public abstract LockActionLocation build(LockAction lockAction);

	public abstract LockActionLocation build(String location);

	public abstract LockActionLocation buildForInstance(LockAction lockAction,
			int instanceId);
}
