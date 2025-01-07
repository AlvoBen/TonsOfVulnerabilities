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
package com.sap.engine.services.security.jmx.sso2.impl;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

import com.sap.engine.services.security.jmx.sso2.CertificateInfo;
import com.sap.engine.services.security.jmx.sso2.TicketIssuerCertificate;
import com.sap.jmx.modelhelper.ChangeableCompositeData;
import com.sap.jmx.modelhelper.OpenTypeFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @author Petrov, Stefan (I043568)
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TicketIssuerCertificateImpl
	extends ChangeableCompositeData
	implements TicketIssuerCertificate 
{

	private static final String EMPTY = "";
	private static final String SYSTEM_ID = "SystemId";
	private static final String CLIENT_ID = "ClientId";
	private static final String STATUS = "Status";
	private static final String CERTIFICATES = "Certificates";

	private static transient Location loc = Location.getLocation(TicketIssuerCertificateImpl.class);

	private static CompositeType COMPOSITE_TYPE;

	static 
	{
		try 
		{
			COMPOSITE_TYPE = OpenTypeFactory.getCompositeType(TicketIssuerCertificate.class);
		} 
		catch (OpenDataException exc) 
		{
			SimpleLogger.traceThrowable(Severity.ERROR, loc, exc, "ASJ.secsrv.009532", "Failed to get composite type TicketIssuerCertificate");
		}
	}
	
	
	public TicketIssuerCertificateImpl() 
	{
		super(COMPOSITE_TYPE);
	}


	/**
	 * @param type
	 */
	public TicketIssuerCertificateImpl(CompositeType type) 
	{
		super(type);
	}


	/**
	 * @param data
	 */
	public TicketIssuerCertificateImpl(CompositeData data) 
	{
		super(data);
	}


	/**
	 * 
	 * @param systemId
	 * @param clientId
	 * @param status
	 * @param certificates
	 * @throws OpenDataException
	 */
	public TicketIssuerCertificateImpl(String systemId, String clientId, int status, CertificateInfo[] certificates) 
		throws OpenDataException 
	{
		this();
		setSystemId(systemId);
		setClientId(clientId);
		setStatus(status);
		setCertificates(certificates);
	}


	/**
	* 
	* @param status
	* @param certificates
	* @throws OpenDataException
	*/
	public TicketIssuerCertificateImpl(int status, CertificateInfo[] certificates) 
		throws OpenDataException 
	{
		this(EMPTY, EMPTY, status, certificates);
	}


	/**
	 * @see com.sap.engine.services.security.jmx.sso2.TicketIssuerCertificate#getSystemId()
	 */
	public String getSystemId() 
	{
		return (String)get(SYSTEM_ID);
	}


	/**
	 * 
	 * @param systemId
	 */
	public void setSystemId(String systemId)
	{
		set(SYSTEM_ID, systemId);
	}


	/**
	 * @see com.sap.engine.services.security.jmx.sso2.TicketIssuerCertificate#getClientId()
	 */
	public String getClientId() 
	{
		return (String)get(CLIENT_ID);
	}


	/**
	 * 
	 * @param clientId
	 */
	public void setClientId(String clientId)
	{
		set(CLIENT_ID, clientId);
	}


	/**
	 * @see com.sap.engine.services.security.jmx.sso2.TicketIssuerCertificate#getStatus()
	 */
	public int getStatus() 
	{
		Integer status = (Integer)get(STATUS);		
		if (status != null) 
		{
			return status.intValue();
		}
		else
		{
			return TICKET_ISSUER_STATUS_UNKNOWN;			
		}
	}


	/**
	 * 
	 * @param status
	 */
	public void setStatus(int status)
	{
		set(STATUS, new Integer(status));
	}


	/**
	 * @see com.sap.engine.services.security.jmx.sso2.TicketIssuerCertificate#getCertificates()
	 */
	public CertificateInfo[] getCertificates() 
	{	
		CertificateInfo[] result = null;
		CompositeData[] data = (CompositeData[])get(CERTIFICATES);
		if (data == null) 
		{
			result = new CertificateInfo[0];
		}
		else
		{
			result = new CertificateInfo[data.length];
			for (int i = 0; i < data.length; i++) 
			{
				result[i] = new CertificateInfoImpl(data[i]);
			}
		}
		
		return result;
	}


	/**
	 * 
	 * @param certificates
	 */
	public void setCertificates(CertificateInfo[] certificates)
	{
		set(CERTIFICATES, certificates);
	}

}
