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

package com.sap.engine.services.httpserver.lib.exceptions;

import com.sap.tc.logging.Location;
import com.sap.localization.ResourceAccessor;

/**
 *
 * @author Violeta Uzunova
 * @version 6.30
 */
public class HttpLibResourceAccessor extends ResourceAccessor {
  private static String BUNDLE_NAME = "com.sap.engine.services.httpserver.lib.exceptions.httpLibResourceBundle";
  private static HttpLibResourceAccessor resourceAccessor = null;
  public static Location location = null;

  public HttpLibResourceAccessor() {
    super(BUNDLE_NAME);
  }

  public static void init(Location l) {
    location = l;
  }

  public static synchronized HttpLibResourceAccessor getResourceAccessor() {
    if(resourceAccessor == null) {
      resourceAccessor = new HttpLibResourceAccessor();
    }
    return resourceAccessor;
  }

}
