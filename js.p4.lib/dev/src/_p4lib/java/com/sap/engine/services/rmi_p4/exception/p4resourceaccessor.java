package com.sap.engine.services.rmi_p4.exception;

import com.sap.localization.ResourceAccessor;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

public class P4ResourceAccessor extends ResourceAccessor {

  static final long serialVersionUID =-3817690724385439272L;

  public static String CATEGORY = "/System/Server";
  public static final String LOCATION_PATH = "com.sap.engine.services.rmi_p4.server";
  public static final String SEC_LOCATION_PATH = "com.sap.engine.services.rmi_p4.security";
  public static final String NAM_LOCATION_PATH = "com.sap.engine.services.rmi_p4.naming";
  public static final String CLIENT_LOCATION = "com.sap.engine.services.rmi_p4";
  private static String BUNDLE_NAME = "com.sap.engine.services.rmi_p4.exception.P4ResourceBundle";
  private static ResourceAccessor resourceAccessor = new P4ResourceAccessor();
  public static Category category = null;
  public static Location location = null;

  public P4ResourceAccessor() {
    super(BUNDLE_NAME);
  }

  public void init(Category category, Location location) {
    P4ResourceAccessor.category = category;
    P4ResourceAccessor.location = location;
  }

  public static synchronized ResourceAccessor getResourceAccessor() {
    if (resourceAccessor == null) {
      resourceAccessor = new P4ResourceAccessor();
    }
    return resourceAccessor;
  }

}
