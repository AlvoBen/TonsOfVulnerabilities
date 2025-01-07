/**
 * Copyright (c) 2002 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.services.security.server;

/**
 *
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public interface SecurityConfigurationPath {

  public final static String CUSTOM_POLICY_CONFIGURATION_PATH = "policy_configurations";

  public final static String SECURITY_PATH = "security";
  public final static String SECURITY_CONFIGURATIONS_PATH = "security/configurations";

  public final static String AUTHENTICATION_PATH = "security/authentication";
  public final static String RESOURCES_PATH = "security/resource";
  public final static String ROLES_PATH = "security/roles";
  public final static String SECURESTORE_PATH = "security/securestore";
  public final static String USERSTORES_PATH = "security/userstores";

  public static final String USERSTORE_INSTALL_PATH = "cluster_data/server/persistent/security/";
}