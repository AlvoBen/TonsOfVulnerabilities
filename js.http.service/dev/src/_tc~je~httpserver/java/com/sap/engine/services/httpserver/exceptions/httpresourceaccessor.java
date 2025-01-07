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

package com.sap.engine.services.httpserver.exceptions;

import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.localization.ResourceAccessor;

/**
 *
 * @author Violeta Uzunova
 * @version 6.30
 */
public class HttpResourceAccessor extends ResourceAccessor {
  private static String BUNDLE_NAME = "com.sap.engine.services.httpserver.exceptions.httpResourceBundle";
  private static HttpResourceAccessor resourceAccessor = null;
  public static Category category = null;
  public static Location location = null;

  public HttpResourceAccessor() {
    super(BUNDLE_NAME);
  }

  public static void init(Location l) {
    location = l;
  }

  public static synchronized HttpResourceAccessor getResourceAccessor() {
    if(resourceAccessor == null) {
      resourceAccessor = new HttpResourceAccessor();
    }
    return resourceAccessor;
  }

}
