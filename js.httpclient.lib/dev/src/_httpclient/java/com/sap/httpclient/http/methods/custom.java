/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.http.methods;

import com.sap.tc.logging.Location;

/**
 * This class is used for custom methods.
 *
 * @author Nikolai Neichev
 */
public class CUSTOM extends DataContainingRequest {

  /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(CUSTOM.class);

  private String methodName;

  /**
   * Default constructor.
   *
   * @param methodName the method name
   */
  public CUSTOM(String methodName) {
    this(methodName, null);
  }

  /**
   * Constructor specifying a URI.
   *
   * @param methodName the method name
   * @param uri either an absolute or relative URI
   */
  public CUSTOM(String methodName, String uri) {
    super(uri);
    this.methodName = methodName;
    LOG.infoT("Custom method created : " + methodName);
    LOG.infoT("URI: " + uri);
  }

  /**
   * Returns the current method name.
   *
   * @return the method name
   */
  public String getName() {
    return methodName;
  }

}