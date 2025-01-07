/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.pj.jmx.exception;

/**
 * Accessor for exception messages resource bundle.
 */
public class ResourceAccessor extends com.sap.localization.ResourceAccessor {

  private static final String BUNDLE_NAME =
    "com.sap.pj.jmx.exception.ResourceBundle"; //$NON-NLS-1$
  private static final ResourceAccessor instance = new ResourceAccessor();

  /**
   * Constructor for JmxResourceAccessor.
   */
  private ResourceAccessor() {
    super(BUNDLE_NAME);
  }

  /**
   * Method getInstance.
   * @return JmxResourceAccessor
   */
  public static ResourceAccessor getInstance() {
    return instance;
  }

}
