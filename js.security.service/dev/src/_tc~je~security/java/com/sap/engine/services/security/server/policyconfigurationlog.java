/**
 * Copyright (c) 2000 by SAP AG, Walldorf., url: http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 */

package com.sap.engine.services.security.server;

import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * 
 * @author Diana Berberova
 * @version 6.30
 * 
 */
public class PolicyConfigurationLog {

  public static final Location location;

  public static final Category category;

  private static final String POLICY_CONFIGURATION_CATEGORY = "PolicyConfiguration";

  private static final String POLICY_CONFIGURATION_LOCATION = "com.sap.engine.services.security.policyconfiguration";

  static {
    location = Location.getLocation(POLICY_CONFIGURATION_LOCATION);
    category = Category.getCategory(Category.SYS_SECURITY, POLICY_CONFIGURATION_CATEGORY);
  }

}
