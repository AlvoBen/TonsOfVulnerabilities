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

import com.sap.engine.services.security.jmx.sso2.TicketIssuerCertificate;
import com.sap.engine.services.security.jmx.sso2.TicketIssuersInfo;
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
public class TicketIssuersInfoImpl
	extends ChangeableCompositeData
	implements TicketIssuersInfo
{

	private static final String EMPTY = "";
	private static final String ACTION_STATUS = "ActionStatus";
	private static final String TICKET_ISSUER_CERTIFICATES = "TicketIssuerCertificates";

	private static transient Location loc = Location.getLocation(TicketIssuersInfoImpl.class);

	private static CompositeType COMPOSITE_TYPE;

	static 
	{
		try 
		{
			COMPOSITE_TYPE = OpenTypeFactory.getCompositeType(TicketIssuersInfo.class);
		} 
		catch (OpenDataException exc) 
		{
			SimpleLogger.traceThrowable(Severity.ERROR, loc, exc, "ASJ.secsrv.009534", "Failed to get composite type TicketIssuersInfo");
		}
	}
	
	
	public TicketIssuersInfoImpl() 
	{
		super(COMPOSITE_TYPE);
	}

	/**
	 * @param type
	 */
	public TicketIssuersInfoImpl(CompositeType type) 
	{
		super(type);
	}


	/**
	 * @param data
	 */
	public TicketIssuersInfoImpl(CompositeData data) 
	{
		super(data);
	}


	/**
	 * 
	 * @param actionStatus
	 * @param ticketIssuerCertificates
	 * @throws OpenDataException
	 */
	public TicketIssuersInfoImpl(int actionStatus, TicketIssuerCertificate[] ticketIssuerCertificates) 
		throws OpenDataException 
	{
		this();
		setActionStatus(actionStatus);
		setTicketIssuerCertificates(ticketIssuerCertificates);
	}


	/**
	 * @see com.sap.engine.services.security.jmx.sso2.TicketIssuersInfo#getActionStatus()
	 */
	public int getActionStatus() 
	{
		Integer status = (Integer)get(ACTION_STATUS);		
		if (status != null) 
		{
			return status.intValue();
		}
		else
		{
			return ACTION_STATUS_UNKNOWN;			
		}
	}


	/**
	 * 
	 * @param actionStatus
	 */
	public void setActionStatus(int actionStatus)
	{
		set(ACTION_STATUS, new Integer(actionStatus));
	}


	/**
	 * @see com.sap.engine.services.security.jmx.sso2.TicketIssuersInfo#getTicketIssuerCertificates()
	 */
	public TicketIssuerCertificate[] getTicketIssuerCertificates() 
	{
		TicketIssuerCertificate[] result = null;
		CompositeData[] data = (CompositeData[])get(TICKET_ISSUER_CERTIFICATES);
		if (data == null) 
		{
			result = new TicketIssuerCertificate[0];
		}
		else
		{
			result = new TicketIssuerCertificate[data.length];
			for (int i = 0; i < data.length; i++) 
			{
				result[i] = new TicketIssuerCertificateImpl(data[i]);
			}
		}
		
		return result;
	}


	/**
	 * 
	 * @param ticketIssuerCertificates
	 */
	public void setTicketIssuerCertificates(TicketIssuerCertificate[] ticketIssuerCertificates)
	{
		set(TICKET_ISSUER_CERTIFICATES, ticketIssuerCertificates);
	}

}
