/*
 * Created on 2006-1-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.services.security.jmx.sso2.impl;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

import com.sap.engine.lib.security.Base64;
import com.sap.engine.services.security.jmx.sso2.LogonTicketIssuer;
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
public class LogonTicketIssuerImpl extends TicketIssuerImpl implements LogonTicketIssuer {
  
  private static transient Location myLoc = Location.getLocation(LogonTicketIssuerImpl.class);
  private static final String CERTIFICATE = "Certificate";
  
  private static CompositeType COMPOSITE_TYPE;

  static {
    try {
      COMPOSITE_TYPE = OpenTypeFactory.getCompositeType(LogonTicketIssuer.class);
    } catch (OpenDataException ex) {
      SimpleLogger.traceThrowable(Severity.ERROR, myLoc, ex, "ASJ.secsrv.009528", "Failed to get composite type LogonTicketIssuer");
    }
  }

  public LogonTicketIssuerImpl(String systemID, String client, X509Certificate cert) {
    super(COMPOSITE_TYPE, systemID, client, cert);
    
    if (cert != null) {
      try {
        setCertificate(encode(cert));
      } catch (Exception e) {
        SimpleLogger.traceThrowable(Severity.ERROR, myLoc, e, "ASJ.secsrv.009529", "Failed to get certificate encoded");
      }
    }
  }
  
  /**
   * @param systemId
   * @param sapclient
   * @param certBase64
   */
  public LogonTicketIssuerImpl(String systemId, String sapclient, String certBase64) {
    super(COMPOSITE_TYPE);
    setSystemID(systemId);
    setClient(sapclient);
    setCertificate(certBase64);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.security.jmx.sso2.LogonTicketIssuer#getCertificate()
   */
  public String getCertificate() {
    return (String) get(CERTIFICATE);
  }
  
  public void setCertificate(String certEnc) {
    set(CERTIFICATE, certEnc);
    
    try {
      X509Certificate cert = decode(certEnc);
      setCertificateSubject(cert.getSubjectDN().getName());
      setCertificateIssuer(cert.getIssuerDN().getName());
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, myLoc, e, "ASJ.secsrv.009530", "Failed to decode certificate");
    }
  }

  static String encode(X509Certificate cert) throws Exception {
    if (cert == null) {
      return null;
    }
    
    return new String(Base64.encode(cert.getEncoded()));
  }

  static X509Certificate decode(String certDerEnc) throws Exception {
    byte[] certBase64 = Base64.decode(certDerEnc.getBytes());
    ByteArrayInputStream in = new ByteArrayInputStream(certBase64);
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    return (X509Certificate) cf.generateCertificate(in);
  }
}
