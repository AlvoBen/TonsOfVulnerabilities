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
 * Created on 2007-7-2
 * 
 */
package com.sap.engine.services.security.jmx.sso2.impl;

import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.openmbean.OpenDataException;

import com.sap.engine.services.security.jmx.sso2.CertificateInfo;
import com.sap.tc.logging.Location;

/**
 * @author Petrov, Stefan (I043568)
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CertificateInfoModel 
{
	
	private static final Location trace = Location.getLocation(CertificateInfoModel.class);
	
	private String keystoreEntryName;
	private X509Certificate certificate;
	
	private String subjectDN;
	private String issuerDN;

	private static final String PUBLIC_KEY_ALGORITHM_DSA = "DSA";

	/**
	 * 
	 */
	public CertificateInfoModel(String keystoreEntryName, X509Certificate certificate) 
	{
		this.keystoreEntryName = keystoreEntryName;
		this.certificate = certificate;
		
		this.subjectDN = certificate.getSubjectDN().getName().trim();
		this.issuerDN = certificate.getIssuerDN().getName().trim();
	}

	
	/**
	 * Checks certificate chain availability in the <code>caCerts</code>
	 * 
	 * @param certificate - certificate which will be checked
	 * @param caCerts - Map with all CA certificates with key->subjectDN and value->X509Certificate
	 * 
	 * @return true, if either <code>certificate</code> is self-signed or whole certificate chain
	 * is available in the caCerts; false otherwise
	 */
	public static boolean checkCertificateChain(X509Certificate certificate, Map caCerts)
	{
		Principal subjectDN = certificate.getSubjectDN();
		Principal issuerDN = certificate.getIssuerDN();
		
		if (subjectDN.equals(issuerDN)) 
		{
			return true;
		}
		else
		{
			List certs = (List)caCerts.get(issuerDN.getName());
			if (certs != null) 
			{
				for (Iterator iter = certs.iterator(); iter.hasNext();) 
				{
					X509Certificate caCert = (X509Certificate) iter.next();
					try 
					{
						certificate.verify(caCert.getPublicKey());
					
						boolean isFound = checkCertificateChain(caCert, caCerts);
						if (isFound) 
						{
							return true;	
						}
					} 
					catch (Exception e) 
					{
						// this certificate is not signed by the current caCert
						continue;
					}
				}	
			}
						
			return false;
		}
	}
	
	
	public CertificateInfo convertToCompositeData(Map caCerts)
		throws OpenDataException
	{	
		int status = CertificateInfo.CERTIFICATE_STATUS_OK;
				
		PublicKey publicKey = certificate.getPublicKey();
		String algorithm = publicKey.getAlgorithm();
		Date currentDate = new Date();
				
		if (currentDate.before(certificate.getNotBefore())) 
		{
			status = CertificateInfo.CERTIFICATE_STATUS_NOT_VALID_YET;
		}
		else if (currentDate.after(certificate.getNotAfter()))
		{
			status = CertificateInfo.CERTIFICATE_STATUS_EXPIRED;
		}
		else if (!checkCertificateChain(caCerts))
		{
			status = CertificateInfo.CERTIFICATE_STATUS_MISSING_CA_CHAIN;
		}
		else if (!PUBLIC_KEY_ALGORITHM_DSA.equals(algorithm)) 
		{
			status = CertificateInfo.CERTIFICATE_STATUS_INVALID_ALGORITHM;
		}
			
		String encCert = "";
				
		try 
		{
			encCert = LogonTicketIssuerImpl.encode(certificate);
		} 
		catch (Exception err) 
		{
			trace.catching("Failed to encode certificate", err);
		}
		
		CertificateInfo certInfo = new CertificateInfoImpl(keystoreEntryName, subjectDN, issuerDN, encCert, status);
		
		return certInfo;
	}
	
	
	private boolean checkCertificateChain(Map caCerts)
	{
		boolean isCertChainOK = checkCertificateChain(this.certificate, caCerts);
		
		return isCertChainOK;
	}
	
	
	/**
	 * @return
	 */
	public X509Certificate getCertificate() 
	{
		return certificate;
	}


	/**
	 * @return
	 */
	public String getKeystoreEntryName() 
	{
		return keystoreEntryName;
	}


	/**
	 * @return
	 */
	public String getIssuerDN() 
	{
		return issuerDN;
	}


	/**
	 * @return
	 */
	public String getSubjectDN() 
	{
		return subjectDN;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() 
	{
		StringBuffer buffer = new StringBuffer("\n");
		buffer.append(super.toString());
		buffer.append("\n");
		buffer.append("KeystoreEntryName: ");
		buffer.append(keystoreEntryName);
		buffer.append("\n");
		buffer.append("subjectDN: ");
		buffer.append(subjectDN);
		buffer.append("\n");
		buffer.append("issuerDN: ");
		buffer.append(issuerDN);
		buffer.append("\n");
		buffer.append("--Certificate: ");
		buffer.append("\n");
	
		try 
		{
			buffer.append(certificate.toString());	
		} 
		catch (Exception err) 
		{
			buffer.append("Failed to display certificates, due to " + err.getMessage());
		}
	
	
		buffer.append("\n");
	
		return buffer.toString();
	}


}
