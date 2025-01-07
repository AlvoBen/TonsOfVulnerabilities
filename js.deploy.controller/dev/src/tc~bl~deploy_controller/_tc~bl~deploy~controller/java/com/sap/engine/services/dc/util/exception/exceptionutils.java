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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: Apr 6, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class ExceptionUtils {

	/**
	 * This method obtains the stack trace of an exception and returns it as a
	 * string
	 * 
	 * @param e
	 *            the exception whose stack trace will be returned
	 * @return the stack trace
	 */
	public static final String getStackTrace(Throwable t) {
		if (t == null) {
			return "Throwable is null.";
		}
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);
		t.printStackTrace(pWriter);
		pWriter.flush();
		pWriter.close();
		return sWriter.toString();
	}
}
