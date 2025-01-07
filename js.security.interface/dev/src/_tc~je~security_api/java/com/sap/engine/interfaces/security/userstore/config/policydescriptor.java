/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.config;

import com.sap.engine.interfaces.security.SecurityContext;
import java.util.Enumeration;
/**
 *   Configuration of the default policy (authorization + authentication) for the activation of a 
 * single userstore.
 *
 * @author Jako Blagoev
 * @author Stephan Zlatarev
 * @author Svetlana Stancheva
 * @author Ekaterina Jeleva
 */
public interface PolicyDescriptor extends java.io.Serializable {
  public static final String ROOT_POLICY_CONFIGURATION = SecurityContext.ROOT_POLICY_CONFIGURATION;
  
  public static final String BASIC_TEMPLATE       = "BASIC";
  public static final String FORM_TEMPLATE        = "FORM";
  public static final String CLIENT_CERT_TEMPLATE = "CLIENT-CERT";
  public static final String DIGEST_TEMPLATE      = "DIGEST";
  public static final String OTHER_TEMPLATE       = "OTHER";
  
  public String getUserStore() throws SecurityException;
    
  public Enumeration listPolicyConfigurations() throws SecurityException;
  
  public AuthorizationDescriptor getAuthorizationDescriptor(String policyConfiguration) throws SecurityException;
  
  public AuthenticationDescriptor getAuthenticationDescriptor(String policyConfiguration) throws SecurityException;
  
  public String getPolicyConfigurationType(String policyConfiguration) throws SecurityException;
 }