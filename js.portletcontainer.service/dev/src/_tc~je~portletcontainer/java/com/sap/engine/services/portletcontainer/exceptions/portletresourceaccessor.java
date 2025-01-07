/*
* Copyright (c) 2005-2006 by SAP AG, Walldorf.,
* http://www.sap.com
* All rights reserved.
*
* This software is the confidential and proprietary information
* of SAP AG, Walldorf. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms
* of the license agreement you entered into with SAP.
*/
package com.sap.engine.services.portletcontainer.exceptions;

import com.sap.localization.ResourceAccessor;
import com.sap.tc.logging.Location;

/**
 * @author Violeta Georgieva
 * @version 7.1
 */
public class PortletResourceAccessor extends ResourceAccessor {
  private static String BUNDLE_NAME = "com.sap.engine.services.portletcontainer.exceptions.portletContainerServiceBundle";
  private static PortletResourceAccessor resourceAccessor = null;
  public static Location location = null;

  public PortletResourceAccessor() {
    super(BUNDLE_NAME);
  }//end of constructor

  public void init(Location _location) {
    location = _location;
  }//end of init(Location _location)

  public static synchronized PortletResourceAccessor getResourceAccessor() {
    if (resourceAccessor == null) {
      resourceAccessor = new PortletResourceAccessor();
    }
    return resourceAccessor;
  }//end of getResourceAccessor()

}//end of class
