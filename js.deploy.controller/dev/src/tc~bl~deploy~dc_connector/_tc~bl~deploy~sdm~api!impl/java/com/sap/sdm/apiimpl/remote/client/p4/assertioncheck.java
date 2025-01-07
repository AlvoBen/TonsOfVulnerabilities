package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.util.log.Logger;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class AssertionCheck {

	private final static Logger log = Logger.getLogger();

	private AssertionCheck() {
	}

	public static void checkForNullArgs(Class cls, String methodName,
			Object[] params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i] == null) {
				String errText = cls.getName() + '.' + methodName
						+ ": received null parameter";
				log.fatal(errText);
				throw new NullPointerException(errText);
			}
		}

		return;
	}

	public static void checkForNullArg(Class cls, String methodName,
			Object param) {
		if (param == null) {
			String errText = cls.getName() + '.' + methodName
					+ ": received null parameter";
			log.fatal(errText);
			throw new NullPointerException(errText);
		}

		return;
	}

}
