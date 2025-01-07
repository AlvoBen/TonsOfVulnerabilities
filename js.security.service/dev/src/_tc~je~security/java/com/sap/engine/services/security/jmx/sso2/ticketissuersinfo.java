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
public interface TicketIssuersInfo
	extends CompositeData 
{
	
	public static final int ACTION_STATUS_UNKNOWN = -1;
	public static final int ACTION_STATUS_OK = 0;
	public static final int ACTION_STATUS_CERTIFICATE_INCONSISTENCY = 1;
	public static final int ACTION_STATUS_SSO2_INCONSISTENCY = 2;
	

	/**
	 * <p>Returns status result of the operation.</p>
	 * <pre>
	 * Possible values:
	 * -1 - Unkown status (ACTION_STATUS_UNKNOWN)
	 *  0 - OK (ACTION_STATUS_OK)
	 *  1 - Found certificate inconsistency (ACTION_STATUS_CERTIFICATE_INCONSISTENCY)
	 *  2 - Found SSO2 inconsistency (ACTION_STATUS_SSO2_INCONSISTENCY)
	 * </pre>
	 * 
	 * @return
	 */
	public int getActionStatus();
	
	
	/**
	 * <pre>
	 * Returns depends on the ActionStatus (AS):
	 * AS=-1 (ACTION_STATUS_UNKNOWN) - undefined result, should be ignored
	 * AS=0 (ACTION_STATUS_OK) - configured SSO2 trusted systems
	 * AS=1 (ACTION_STATUS_CERTIFICATE_INCONSISTENCY) - repeated certificates (with equal DN and Issuer DN), without SID and ClientId
	 * AS=2 (ACTION_STATUS_SSO2_INCONSISTENCY) - union with all found trusted systems with available certificate(s)
	 * </pre>
	 * @return
	 */
	public TicketIssuerCertificate[] getTicketIssuerCertificates();
	
}
