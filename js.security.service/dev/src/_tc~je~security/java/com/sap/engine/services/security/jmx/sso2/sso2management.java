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
package com.sap.engine.services.security.jmx.sso2;

import javax.management.openmbean.CompositeData;

import com.sap.jmx.ObjectNameFactory;

/**
 * @author Krasimira Velikova
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SSO2Management {
  public static final String SYSTEM_DOES_NOT_CREATE_TICKETS = "SYSTEM_DOES_NOT_CREATE_TICKETS";
  public static final String SYSTEM_DOES_NOT_ACCEPT_TICKETS = "SYSTEM_DOES_NOT_ACCEPT_TICKETS";
  public static final String NOT_AUTHORIZED                 = "NOT_AUTHORIZED";
  public static final String CERTIFICATE_ALREADY_EXISTS     = "CERTIFICATE_ALREADY_EXISTS";
  public static final String ACL_ENTRY_ALREADY_EXISTS       = "ACL_ENTRY_ALREADY_EXISTS";
  public static final String CERTIFICATE_SYNTAX_ERROR       = "CERTIFICATE_SYNTAX_ERROR";
  public static final String ACL_ENTRY_NOT_FOUND            = "ACL_ENTRY_NOT_FOUND";
  public static final String NOT_AVAILABLE                  = "NOT_AVAILABLE";
  public static final String MISSING_CERTIFICATE_PARAMETER  = "MISSING_CERTIFICATE_PARAMETER";
  public static final String FOREIGN_ENQUEUE_LOCK           = "FOREIGN_ENQUEUE_LOCK";
  public static final String CONSISTENCY_CHECK_FAILED       = "CONSISTENCY_CHECK_FAILED";
  public static final String CERTIFICATE_REPLACE_NOT_ALLOWED= "CERTIFICATE_REPLACE_NOT_ALLOWED"; 
  public static final String USERSTORE_ETLM_NOT_FOUND	    = "USERSTORE_ETLM_NOT_FOUND";
  public static final String USERSTORE_EATLM_NOT_FOUND	    = "USERSTORE_EATLM_NOT_FOUND";
  
  
  public static final String JMX_J2EETYPE = ObjectNameFactory.SAP_J2EEServiceRuntimePerNode;
  public static final String MBEAN_NAME = "sso2";
  
  public static final int SSO2_JMX_MODEL_VERSION_0 = 0;
  public static final int SSO2_JMX_MODEL_VERSION_1 = 1;
  
  /**
   * Returns jmx model version. Should be used to determine available functionality
   * @return
   */
  public int getVersion();
  
  
  /**
   * Returns system time and domain name
   * @return
   * @throws Exception
   */
  public CompositeData getSystemInfo() throws Exception;
  
  
  /**
   * Return information about the issued by the system tickets. The exception that
   * is thrown can have the following messages: {@link INTERNAL_ERROR} or
   * {@link SYSTEM_DOES_NOT_CREATE_TICKETS)
   * 
   * @return information about the issued by the system tickets. 
   */
  public CompositeData getLogonTicketIssuerInfos() throws Exception;

  /**
   * Returns a list of systems from which the managed system accepts tickets. 
   * 
   *  EXCEPTIONS
   *      SYSTEM_DOES_NOT_ACCEPT_TICKETS
   *      NOT_AUTHORIZED
   *      INTERNAL_ERROR
   * 
   * @deprecated
   * 
   * @return list of systems that managed system accepts tickets from
   */
  public CompositeData[] getListOfTrustedTicketIssuers() throws Exception;
  
  
  /**
   * This method replace getListOfTrustedTicketIssuers()
   * 
   * It adds certificate and SSO2 consistency checks before returns configured trusted ticket issuers.
   * 
   * If certificate consistency check fails, the method returns list with repeated certificates.
   * If SSO2 consistency check fails, the method returns union with found corrected trusted ticket issuers.
   * If there is not certificate or SSO2 inconsistency, the method returns configured trusted ticket issuers.
   * 
   * @version SSO2_JMX_MODEL_VERSION_1
   * @param checkMode
   * @return
   * @throws Exception
   */
  public CompositeData getTrustedTicketIssuersInfo(int checkMode) throws Exception;
  

  /**
   * Configure all ETLMs and EATLMs in the US and policy configurations to use
   * given trusted ticket <code>issuers</code> as module options.
   * 
   * In addition is added option with name "ume.configuration.active" and value "true"
   * 
   * @version SSO2_JMX_MODEL_VERSION_1
   * @param issuers
   * @throws Exception
   */
  public void storeTrustedTicketIssuers(CompositeData[] issuers) throws Exception;
  
  
  /**
   *  EXCEPTIONS
   *      NOT_AUTHORIZED
   *      CERTIFICATE_ALREADY_EXISTS
   *      ACL_ENTRY_ALREADY_EXISTS
   *      CERTIFICATE_SYNTAX_ERROR
   *      INTERNAL_ERROR
   *      MISSING_CERTIFICATE_PARAMETER
   *       FOREIGN_ENQUEUE_LOCK
   * 
   * @param SystemID
   * @param client
   * @param certificate
   * @param replaceAllowed
   * @return
   */
  public void addTrustedTicketIssuer(String SystemID, String client, String certificateBase64, boolean replaceAllowed) throws Exception;

  /**
   *  EXCEPTIONS
   *      NOT_AUTHORIZED
   *      ACL_ENTRY_NOT_FOUND
   *      INTERNAL_ERROR
   * @param SystemID
   * @param client
   * @return
   */
  public void removeTrustedTicketIssuer(String SystemID, String client) throws Exception;
  
  /**
   * @deprecated this method does nothing
   */
  public void lock() throws Exception;
  
  /**
   * @deprecated this method does nothing
   */
  public void unlock() throws Exception;
  
}
