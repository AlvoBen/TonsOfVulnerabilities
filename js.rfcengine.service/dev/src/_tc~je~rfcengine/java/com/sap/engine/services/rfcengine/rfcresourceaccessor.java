package com.sap.engine.services.rfcengine;

import com.sap.localization.ResourceAccessor;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

public class RFCResourceAccessor extends ResourceAccessor {


  public static final String BEAN_NOT_FOUND = "rfc_0000";
  public static final String NO_PROCESS_FUNC = "rfc_0001";
  public static final String NO_INIT_CONTEX = "rfc_0002";
  public static final String NO_JCO_CONTEXT = "rfc_0003";
  public static final String BUNDEL_CONFIG_FAILED = "rfc_0004";


  private static String BUNDLE_NAME = "com.sap.engine.services.rfcengine.RFCResourceBundle";
  public static String CATEGORY = "/System/Server";
  public static final String LOCATION_PATH = "com.sap.engine.services.rfcengine";
  private static ResourceAccessor resourceAccessor = null;
  public static Category category = null;
  public static Location location = null;

  public RFCResourceAccessor() {
    super(BUNDLE_NAME);
  }

  public void init(Category _category, Location _location) {
    category = _category;
    location = _location;
  }

  public static synchronized ResourceAccessor getResourceAccessor() {
    if (resourceAccessor == null) {
      resourceAccessor = new RFCResourceAccessor();
    }
    return resourceAccessor;
  }

}
