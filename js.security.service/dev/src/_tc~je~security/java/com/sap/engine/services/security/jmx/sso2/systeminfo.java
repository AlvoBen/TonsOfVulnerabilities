/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on 2007-6-25
 * 
 */
package com.sap.engine.services.security.jmx.sso2;

import javax.management.openmbean.CompositeData;

/**
 * @author Petrov, Stefan (I043568)
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface SystemInfo 
	extends CompositeData 
{
	
	public long SYSTEM_TIME_UNKNOWN = -1;
	
	/**
	 * 
	 * @return
	 */
	public long getSystemTime();

	/**
	 * 
	 * @return
	 */
	public String getDomainName();

}
