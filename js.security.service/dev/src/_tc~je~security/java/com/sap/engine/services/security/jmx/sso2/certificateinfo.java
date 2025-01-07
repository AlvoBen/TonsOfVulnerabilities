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
 * Created on 2007-6-15
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
public interface CertificateInfo 
	extends CompositeData 
{
	public static final int CERTIFICATE_STATUS_UNKNOWN = -1;
	public static final int CERTIFICATE_STATUS_OK = 0;
	public static final int CERTIFICATE_STATUS_INVALID_ALGORITHM = 1;
	public static final int CERTIFICATE_STATUS_NOT_VALID_YET = 2;
	public static final int CERTIFICATE_STATUS_EXPIRED = 3;
	public static final int CERTIFICATE_STATUS_MISSING_CA_CHAIN = 4;
	public static final int CERTIFICATE_STATUS_NOT_FOUND = 5;
	
	/**
	* @return
	*/
	public String getKeystoreEntryName();
	
	
	/**
	 * @return
	 */
	public String getCertificateSubject();
	
	
	/**
	 * @return
	 */
	public String getCertificateIssuer();
	
	
	/**
	 * @return
	 */
	public String getCertificate();
	
	
	/**
	 * @return
	 */
	public int getStatus();
}
