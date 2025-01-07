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
package com.sap.engine.services.dc.api.util.exception;

import com.sap.localization.ResourceAccessor;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-30</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class APIResourceAccessor extends ResourceAccessor {

	private static final String BUNDLE_NAME = "com.sap.engine.services.dc.api.util.exception.resources.exceptions";
	private static final APIResourceAccessor INSTANCE = new APIResourceAccessor();

	private APIResourceAccessor() {
		super(BUNDLE_NAME);
	}

	public static ResourceAccessor getInstance() {
		return INSTANCE;
	}
	/*
	 * public String getMessageText(Locale locale, String pattern) { return
	 * pattern; }
	 */
}
