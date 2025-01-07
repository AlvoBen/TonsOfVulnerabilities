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
 * Created on 2007-6-21
 * 
 */
package com.sap.engine.services.security.jmx.sso2.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.OpenDataException;

import com.sap.engine.services.security.jmx.sso2.CertificateInfo;
import com.sap.engine.services.security.jmx.sso2.TicketIssuerCertificate;

/**
 * @author Petrov, Stefan (I043568)
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TicketIssuerCertificateModel 
{
	private String systemId;
	private String clientId;
	private int status;
	private Map certificates;
	
	
	public TicketIssuerCertificateModel(String systemId, String clientId, int status)
	{
		this.systemId = systemId;
		this.clientId = clientId;
		this.status = status;
		certificates = new HashMap();
	}
	
	/**
	 * @return
	 */
	public CertificateInfo getCertificate(String key) 
	{
		return (CertificateInfo)certificates.get(key);
	}

	/**
	* @return
	*/
	public Map getCertificates() 
	{
		return certificates;
	}

	/**
	 * @return
	 */
	public String getClientId() 
	{
		return clientId;
	}

	/**
	 * @return
	 */
	public int getStatus() 
	{
		return status;
	}

	/**
	 * @return
	 */
	public String getSystemId() 
	{
		return systemId;
	}


	/**
	 * @param i
	 */
	public void setStatus(int i) 
	{
		status = i;
	}


	/**
	 * @param certificate
	 */
	public void putCertificate(String key, CertificateInfo certificate) 
	{
		this.certificates.put(key, certificate);
	}

	/**
	* @param certificates
	*/
	public void putCertificates(Map certificates) 
	{
		this.certificates.putAll(certificates);
	}


	/**
	 * Convert this TicketIssuerCertificateModel to the corresponding TicketIssuerCertificate composite data
	 * 
	 * @return
	 * @throws OpenDataException
	 */
	public TicketIssuerCertificate convertToCompositeData()
		throws OpenDataException
	{
		CertificateInfo[] certificateInfos = (CertificateInfo[])certificates.values().toArray(new CertificateInfo[0]);

		TicketIssuerCertificate issuerCertificate = new TicketIssuerCertificateImpl(systemId, clientId, status, certificateInfos);
		
		return issuerCertificate;				
	}

	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() 
	{
		StringBuffer buffer = new StringBuffer("\n");
		buffer.append(super.toString());
		buffer.append("\n");
		buffer.append("systemId: ");
		buffer.append(systemId);
		buffer.append("\n");
		buffer.append("clientId: ");
		buffer.append(clientId);
		buffer.append("\n");
		buffer.append("status: ");
		buffer.append(status);
		buffer.append("\n");
		buffer.append("--Certificates: ");
		buffer.append("\n");
		
		try 
		{
			if (certificates != null) 
			{
				Set keySet = certificates.keySet();
				for (Iterator iter = keySet.iterator(); iter.hasNext();) 
				{
					String key = (String) iter.next();
					CertificateInfo cert = (CertificateInfo)certificates.get(key);
					if (cert != null) 
					{
						buffer.append(cert.toString());
						buffer.append("\n");					
					}
				}	
			}	
		} 
		catch (Exception err) 
		{
			buffer.append("Failed to display certificates, due to " + err.getMessage());
		}
		
		
		buffer.append("\n");
		
		return buffer.toString();
	}

	

}
