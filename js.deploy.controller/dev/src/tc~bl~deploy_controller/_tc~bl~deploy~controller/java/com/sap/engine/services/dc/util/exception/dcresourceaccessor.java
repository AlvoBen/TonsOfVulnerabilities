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
package com.sap.engine.services.dc.util.exception;

import java.util.Locale;

import com.sap.engine.services.dc.util.Constants;
import com.sap.localization.ResourceAccessor;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DCResourceAccessor extends ResourceAccessor {

	private static final long serialVersionUID = 6961319273028001447L;

	private transient static final String BUNDLE_NAME = "com.sap.engine.services.dc.util.exception.resources.ResourceBundle";
	private static final DCResourceAccessor INSTANCE = new DCResourceAccessor();

	private DCResourceAccessor() {
		super(BUNDLE_NAME);
	}

	public String getMessageText(String key) {
		return super.getMessageText(Constants.DC_LOCALE, key);
	}

	public static DCResourceAccessor getInstance() {
		return INSTANCE;
	}

}
