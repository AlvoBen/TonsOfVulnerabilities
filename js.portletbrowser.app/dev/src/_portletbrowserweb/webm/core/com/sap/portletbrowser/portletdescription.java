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

/**
 * @author Nikolai Dokovski
 * @version 1.0
 * @Portlet Info Holder
 */
public class PortletDescription {
	private String name;
	private String application;

	public PortletDescription(String name,String application){
		this.name = name;
		this.application = application;
	}

	/**
	 * @return Returns the application.
	 */
	public String getApplication() {
		return application;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
}
