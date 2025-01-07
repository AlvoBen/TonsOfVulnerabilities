/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */

package com.sap.engine.services.jndi.persistent;

import com.sap.localization.ResourceAccessor;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/*
 *
 * @author Elitsa Pancheva
 * @version 6.30
 */

public class JNDIResourceAccessor extends ResourceAccessor {

  private static String BUNDLE_NAME = "com.sap.engine.services.jndi.persistent.JNDIResourceBundle";
  public static String CATEGORY = "/System/Server";
  public static String LOCATION_PATH = "com.sap.engine.services.jndi";
  private static ResourceAccessor resourceAccessor = null;
  public static Category category = null;
  public static Location location = null;

  public JNDIResourceAccessor() {
    super(BUNDLE_NAME);
  }

  public static void init(Category c, Location l) {
    category = c;
    location = l;
  }

  public static synchronized ResourceAccessor getResourceAccessor() {
    if(resourceAccessor == null) {
      resourceAccessor = new JNDIResourceAccessor();
    }
    return resourceAccessor;
  }
}
