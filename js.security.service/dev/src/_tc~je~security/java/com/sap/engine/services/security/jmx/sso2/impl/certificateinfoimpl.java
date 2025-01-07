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
public class CertificateInfoImpl
	extends ChangeableCompositeData
	implements CertificateInfo 
{

	private static final String EMPTY = "";
	private static final String KEYSTORE_ENTRY_NAME = "KeystoreEntryName";
	private static final String CERTIFICATE_SUBJECT = "CertificateSubject";
	private static final String CERTIFICATE_ISSUER = "CertificateIssuer";
	private static final String CERTIFICATE = "Certificate";
	private static final String STATUS = "Status";

	private static transient Location loc = Location.getLocation(CertificateInfoImpl.class);

	private static CompositeType COMPOSITE_TYPE;

	static 
	{
		try 
		{
			COMPOSITE_TYPE = OpenTypeFactory.getCompositeType(CertificateInfo.class);
		} 
		catch (OpenDataException exc) 
		{
			SimpleLogger.traceThrowable(Severity.ERROR, loc, exc, "ASJ.secsrv.009506", "Failed to get composite type CertificateInfo");
		}
	}
	
	
	public CertificateInfoImpl() 
	{
		super(COMPOSITE_TYPE);
	}


	/**
	 * @param type
	 */
	public CertificateInfoImpl(CompositeType type) 
	{
		super(type);
	}


	/**
	 * @param data
	 */
	public CertificateInfoImpl(CompositeData data) 
	{
		super(data);
	}


	/**
	 * 
	 * @param keystoreEntryName
	 * @param certificateSubject
	 * @param certificateIssuer
	 * @param certificate
	 * @param status
	 * @throws OpenDataException
	 */
	public CertificateInfoImpl(String keystoreEntryName, String certificateSubject, 
							   String certificateIssuer, String certificate, int status) 
		throws OpenDataException 
	{
		this();
		setKeystoreEntryName(keystoreEntryName);
		setCertificateSubject(certificateSubject);
		setCertificateIssuer(certificateIssuer);
		setCertificate(certificate);
		setStatus(status);
	}


	/**
	* @see com.sap.engine.services.security.jmx.sso2.CertificateInfo#getKeystoreEntryName()
	*/
	public String getKeystoreEntryName() 
	{
		return (String)get(KEYSTORE_ENTRY_NAME);
	}


	/**
	* @param keystoreEntryName
	*/
	public void setKeystoreEntryName(String keystoreEntryName) 
	{
		set(KEYSTORE_ENTRY_NAME, keystoreEntryName);
	}



	/**
	 * @see com.sap.engine.services.security.jmx.sso2.CertificateInfo#getCertificateSubject()
	 */
	public String getCertificateSubject() 
	{
		return (String)get(CERTIFICATE_SUBJECT);
	}


	/**
	 * 
	 * @param certificateSubject
	 */
	public void setCertificateSubject(String certificateSubject) 
	{
		set(CERTIFICATE_SUBJECT, certificateSubject);
	}


	/**
	 * @see com.sap.engine.services.security.jmx.sso2.CertificateInfo#getCertificateIssuer()
	 */
	public String getCertificateIssuer() 
	{
		return (String)get(CERTIFICATE_ISSUER);
	}
	
	
	/**
	 * 
	 * @param certificateIssuer
	 */
	public void setCertificateIssuer(String certificateIssuer) 
	{
		set(CERTIFICATE_ISSUER, certificateIssuer);
	}

	/**
	 * @see com.sap.engine.services.security.jmx.sso2.CertificateInfo#getCertificate()
	 */
	public String getCertificate() 
	{
		return (String)get(CERTIFICATE);
	}


	/**
	 * 
	 * @param certificate
	 */
	public void setCertificate(String certificate) 
	{
		set(CERTIFICATE, certificate);
	}


	/**
	 * @see com.sap.engine.services.security.jmx.sso2.CertificateInfo#getStatus()
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
			return CERTIFICATE_STATUS_UNKNOWN;			
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

}
