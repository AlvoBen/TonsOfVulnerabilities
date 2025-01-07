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
 * Created on 2007-6-13
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
public interface TicketIssuerCertificate 
	extends CompositeData 
{
	
	public static final int TICKET_ISSUER_STATUS_UNKNOWN = -1;
	public static final int TICKET_ISSUER_STATUS_OK = 0;
	public static final int TICKET_ISSUER_STATUS_REPEATED_CERTS = 1;
	public static final int TICKET_ISSUER_STATUS_CONFLICT_CERTS = 2;
	
	/**
	 * 
	 * @return
	 */
	public String getSystemId();

	
	/**
	 * 
	 * @return
	 */
	public String getClientId();

	
	/**
	 * 
	 * @return
	 */
	public int getStatus();
	
	
	/**
	 * 
	 * @return
	 */
	public CertificateInfo[] getCertificates();	
}
