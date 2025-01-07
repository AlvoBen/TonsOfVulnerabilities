/*
 * Created on Dec 23, 2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.security.jmx.sso2.impl;

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.sap.engine.services.security.jmx.sso2.CertificateInfo;
import com.sap.engine.services.security.jmx.sso2.LogonTicketIssuer;
import com.sap.engine.services.security.jmx.sso2.SystemInfo;
import com.sap.engine.services.security.jmx.sso2.TicketIssuer;
import com.sap.engine.services.security.jmx.sso2.TicketIssuerCertificate;
import com.sap.engine.services.security.jmx.sso2.TicketIssuersInfo;

/**
 * @author I030665
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class JmxUtil {
	public static CompositeData getCDataForSystemInfo(SystemInfo metric) throws OpenDataException {
		if (metric == null) {
			return null;
		} 
		String[] attrnames={"SystemTime", "DomainName"};
		Object[] attrobj={new Long(metric.getSystemTime()), metric.getDomainName()};
		return new CompositeDataSupport(getCTypeForSystemInfo(), attrnames, attrobj);	
	}  
	private static CompositeType getCTypeForSystemInfo() throws OpenDataException {
		String[] itemNames={"SystemTime", "DomainName"};
		String[] itemDescriptions={"Description:", "Description:"};
		OpenType[] itemTypes={SimpleType.LONG, SimpleType.STRING};	
		return new CompositeType("SystemInfo","Description:", itemNames, itemDescriptions, itemTypes);
	} 


	public static CompositeData getCDataForLogonTicketIssuer(LogonTicketIssuer metric) throws OpenDataException {
		if (metric == null) {
			return null;
		} 
		String[] attrnames={"Certificate", "SystemID" ,"Client", "CertificateSubject", "CertificateIssuer"};
		Object[] attrobj={metric.getCertificate(), metric.getSystemID(), metric.getClient(), metric.getCertificateSubject(), metric.getCertificateIssuer()};
		return new CompositeDataSupport(getCTypeForLogonTicketIssuer(), attrnames, attrobj);	
	}  
	private static CompositeType getCTypeForLogonTicketIssuer() throws OpenDataException {
		String[] itemNames={"Certificate", "SystemID" ,"Client", "CertificateSubject", "CertificateIssuer"};
		String[] itemDescriptions={"Description:", "Description:", "Description:", "Description:", "Description:"};
		OpenType[] itemTypes={SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING};	
		return new CompositeType("LogonTicketIssuer","Description:", itemNames, itemDescriptions, itemTypes);
	} 


	public static CompositeData[] getCDataArrForTicketIssuer(TicketIssuer[] crr) throws OpenDataException{
		if (crr == null) {
			return null;
		}			 
		CompositeDataSupport[] cd = new CompositeDataSupport[crr.length]; 
		for(int i=0;i<crr.length;i++){		
			cd[i]=(CompositeDataSupport) getCDataForTicketIssuer(crr[i]);
		}
		return cd;
	}
	private static CompositeData getCDataForTicketIssuer(TicketIssuer metric) throws OpenDataException {
		if (metric == null) {
			return null;
		} 
		String[] attrnames={"SystemID" ,"Client", "CertificateSubject", "CertificateIssuer"};
		Object[] attrobj={metric.getSystemID(), metric.getClient(), metric.getCertificateSubject(), metric.getCertificateIssuer()};
		return new CompositeDataSupport(getCTypeForTicketIssuer(), attrnames, attrobj);	
	}  
	private static CompositeType getCTypeForTicketIssuer() throws OpenDataException {
		String[] itemNames={"SystemID" ,"Client", "CertificateSubject", "CertificateIssuer"};
		String[] itemDescriptions={"Description:", "Description:", "Description:", "Description:"};
		OpenType[] itemTypes={SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING};	
		return new CompositeType("TicketIssuer","Description:", itemNames, itemDescriptions, itemTypes);
	} 


	public static CompositeData getCDataForTicketIssuersInfo(TicketIssuersInfo metric) throws OpenDataException {
		if (metric == null) {
			return null;
		} 
		String[] attrnames={"ActionStatus", "TicketIssuerCertificates"};
		Object[] attrobj={new Integer(metric.getActionStatus()), getCDataArrForTicketIssuerCertificate(metric.getTicketIssuerCertificates())};
		return new CompositeDataSupport(getCTypeForTicketIssuersInfo(), attrnames, attrobj);	
	}  
	private static CompositeType getCTypeForTicketIssuersInfo() throws OpenDataException {
		String[] itemNames={"ActionStatus", "TicketIssuerCertificates"};
		String[] itemDescriptions={"Description:", "Description:"};
		OpenType[] itemTypes={SimpleType.INTEGER, new ArrayType(1,getCTypeForTicketIssuerCertificate())};	
		return new CompositeType("TicketIssuersInfo","Description:", itemNames, itemDescriptions, itemTypes);
	} 


	private static CompositeData[] getCDataArrForTicketIssuerCertificate(TicketIssuerCertificate[] crr) throws OpenDataException{
		if (crr == null) {
			return null;
		}			 
		CompositeDataSupport[] cd = new CompositeDataSupport[crr.length]; 
		for(int i=0;i<crr.length;i++){		
			cd[i]=(CompositeDataSupport) getCDataForTicketIssuerCertificate(crr[i]);
		}
		return cd;
	}
	private static CompositeData getCDataForTicketIssuerCertificate(TicketIssuerCertificate metric) throws OpenDataException {
		if (metric == null) {
			return null;
		} 
		String[] attrnames={"SystemId", "ClientId", "Status", "Certificates"};
		Object[] attrobj={metric.getSystemId(), metric.getClientId(), new Integer(metric.getStatus()), getCDataArrForCertificateInfo(metric.getCertificates())};
		return new CompositeDataSupport(getCTypeForTicketIssuerCertificate(), attrnames, attrobj);	
	}  	
	private static CompositeType getCTypeForTicketIssuerCertificate() throws OpenDataException {
		String[] itemNames={"SystemId", "ClientId", "Status", "Certificates"};
		String[] itemDescriptions={"Description:", "Description:", "Description:", "Description:"};
		OpenType[] itemTypes={SimpleType.STRING, SimpleType.STRING, SimpleType.INTEGER, new ArrayType(1,getCTypeForCertificateInfo())};	
		return new CompositeType("TicketIssuerCertificate","Description:", itemNames, itemDescriptions, itemTypes);
	} 


	private static CompositeData[] getCDataArrForCertificateInfo(CertificateInfo[] crr) throws OpenDataException{
		if (crr == null) {
			return null;
		}			 
		CompositeDataSupport[] cd = new CompositeDataSupport[crr.length]; 
		for(int i=0;i<crr.length;i++){		
			cd[i]=(CompositeDataSupport) getCDataForCertificateInfo(crr[i]);
		}
		return cd;
	}
	private static CompositeData getCDataForCertificateInfo(CertificateInfo metric) throws OpenDataException {
		if (metric == null) {
			return null;
		} 
		String[] attrnames={"KeystoreEntryName", "CertificateSubject", "CertificateIssuer", "Certificate", "Status"};
		Object[] attrobj={metric.getKeystoreEntryName(), metric.getCertificateSubject(), metric.getCertificateIssuer(), metric.getCertificate(), new Integer(metric.getStatus())};
		return new CompositeDataSupport(getCTypeForCertificateInfo(), attrnames, attrobj);	
	}  
	private static CompositeType getCTypeForCertificateInfo() throws OpenDataException {
		String[] itemNames={"KeystoreEntryName", "CertificateSubject", "CertificateIssuer", "Certificate", "Status"};
		String[] itemDescriptions={"Description:", "Description:", "Description:", "Description:", "Description:"};
		OpenType[] itemTypes={SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.INTEGER};	
		return new CompositeType("CertificateInfo","Description:", itemNames, itemDescriptions, itemTypes);
	} 
}
