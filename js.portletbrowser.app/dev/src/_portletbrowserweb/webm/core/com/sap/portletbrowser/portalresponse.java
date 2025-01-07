/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.portletbrowser;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Nikolai Dokovski
 * @version 1.0
 * 
 * Simple wrapper, but later could be a portal page builder :)
 */
public class PortalResponse {
	
	private HttpServletResponse response;
	
	public PortalResponse(HttpServletResponse response){
		this.response = response;
	}

	/**
	 * @return Returns the response.
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
}
