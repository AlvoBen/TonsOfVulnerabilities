/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.jmx.sso2.impl;

import java.security.cert.X509Certificate;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

import com.sap.engine.services.security.jmx.sso2.TicketIssuer;
import com.sap.jmx.modelhelper.ChangeableCompositeData;
import com.sap.jmx.modelhelper.OpenTypeFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @author Krasimira Velikova
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TicketIssuerImpl extends ChangeableCompositeData implements TicketIssuer {
  private static transient Location myLoc = Location.getLocation(TicketIssuerImpl.class);
  
  private static final String SYSTEM_ID           = "SystemID";
  private static final String CLIENT              = "Client";
  private static final String CERTIFICATE_SUBJECT = "CertificateSubject";
  private static final String CERTIFICATE_ISSUER  = "CertificateIssuer";
  
  
  private static CompositeType COMPOSITE_TYPE;

  static {
    try {
      COMPOSITE_TYPE = OpenTypeFactory.getCompositeType(TicketIssuer.class);
    } catch (OpenDataException ex) {
      SimpleLogger.traceThrowable(Severity.ERROR, myLoc, ex, "ASJ.secsrv.009533", "Failed to get composite type TicketIssuer");
    }
  }
  
  public TicketIssuerImpl(String systemID, String client, X509Certificate cert) {
    this(COMPOSITE_TYPE, systemID, client, cert);
  }

  public TicketIssuerImpl(String systemID, String client, String subject, String issuer) {
    this(COMPOSITE_TYPE);
    setSystemID(systemID);
    setClient(client);
    setCertificateSubject(subject);
    setCertificateIssuer(issuer);
  }

  protected TicketIssuerImpl(CompositeType cType, String systemID, String client, X509Certificate cert) {
    super(cType);
    setSystemID(systemID);
    setClient(client);
  
    if (cert != null) {
      setCertificateIssuer(cert.getIssuerDN().getName());
      setCertificateSubject(cert.getSubjectDN().getName());
    }
  }

  
  protected TicketIssuerImpl(CompositeType type) {
    super(type);
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.services.security.jmx.sso2.TicketIssuer#getSystemID()
   */
  public String getSystemID() {
    return (String) get(SYSTEM_ID);
  }
  
  /**
   * @param systemId
   */
  public void setSystemID(String systemId) {
    set(SYSTEM_ID, systemId);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.security.jmx.sso2.TicketIssuer#getClient()
   */
  public String getClient() {
    return (String) get(CLIENT);
  }
  
  /**
   * @param client
   */
  public void setClient(String client) {
    set(CLIENT, client);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.security.jmx.sso2.TicketIssuer#getCertificateSubject()
   */
  public String getCertificateSubject() {
    return (String) get(CERTIFICATE_SUBJECT);
  }
  
  /**
   * @param subject
   */
  public void setCertificateSubject(String subject) {
    set(CERTIFICATE_SUBJECT, subject);
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.services.security.jmx.sso2.TicketIssuer#getCertificateIssuer()
   */
  public String getCertificateIssuer() {
    return (String) get(CERTIFICATE_ISSUER);
  }
  
  /**
   * @param issuer
   */
  public void setCertificateIssuer(String issuer) {
    set(CERTIFICATE_ISSUER, issuer);
  }
}
