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
package com.sap.engine.services.dc.api.model;

/**
 * 
 * Title: J2EE Deployment Team Description: Abstract class for SDA/SCA ID.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: Apr 19, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public interface SduId {
	/**
	 * Gets this component's name.
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Returns the vendor's name of this component.
	 * 
	 * @return vendor's name
	 */
	public String getVendor();
}
