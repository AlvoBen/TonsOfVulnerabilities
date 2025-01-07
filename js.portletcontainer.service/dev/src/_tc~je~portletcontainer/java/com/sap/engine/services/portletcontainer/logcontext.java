package com.sap.engine.services.portletcontainer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import com.sap.engine.services.portletcontainer.logging.LogCategory;
import com.sap.engine.services.portletcontainer.logging.LogLocation;
/**
*
* To change the template for this generated type comment go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
* @author Diyan Yordanov
*/
public class LogContext {
  public static final int CATEGORY_DEPLOY = 1;
  public static final int CATEGORY_REQUESTS = 2;
  public static final int CATEGORY_SERVICE = 3;

  public static final int LOCATION_DEPLOY = 1;
  public static final int LOCATION_REQUESTS = 2;
  public static final int LOCATION_SERVICE = 3;
  public static final int LOCATION_PORTLET_SESSION = 4;

  public static final String CATEGORY_DEPLOY_NAME = "System/Server";
  public static final String CATEGORY_REQUESTS_NAME = "System/Server/WebRequests";
  public static final String CATEGORY_SERVICE_NAME = "System/Server";

  public static final String LOCATION_DEPLOY_NAME = "com.sap.engine.services.portletcontainer.Deploy";
  public static final String LOCATION_REQUESTS_NAME = "com.sap.engine.services.portletcontainer.Requests";
  public static final String LOCATION_SERVICE_NAME = "com.sap.engine.services.portletcontainer.Service";
  public static final String LOCATION_PORTLET_SESSION_NAME = "com.sap.engine.services.portletcontainer.PortletSession";

  public static HashMap categories = new HashMap();
  public static HashMap locations = new HashMap();

  public static void init() {
    categories.put(new Integer(CATEGORY_DEPLOY), new LogCategory(CATEGORY_DEPLOY_NAME, LOCATION_DEPLOY));
    categories.put(new Integer(CATEGORY_REQUESTS), new LogCategory(CATEGORY_REQUESTS_NAME, LOCATION_REQUESTS));
    categories.put(new Integer(CATEGORY_SERVICE), new LogCategory(CATEGORY_SERVICE_NAME, LOCATION_SERVICE));

    locations.put(new Integer(LOCATION_DEPLOY), new LogLocation(LOCATION_DEPLOY_NAME));
    locations.put(new Integer(LOCATION_REQUESTS), new LogLocation(LOCATION_REQUESTS_NAME));
    locations.put(new Integer(LOCATION_SERVICE), new LogLocation(LOCATION_SERVICE_NAME));
    locations.put(new Integer(LOCATION_PORTLET_SESSION), new LogLocation(LOCATION_PORTLET_SESSION_NAME));
  } //end of init()

  public static LogLocation getLocation(int location) {
    return (LogLocation)locations.get(new Integer(location));
  } //end of getLocation(int location)

  public static LogCategory getCategory(int category) {
    return (LogCategory)categories.get(new Integer(category));
  } //end of getCategory(int category)

  public static String getExceptionStackTrace(Throwable t) {
    ByteArrayOutputStream ostr = new ByteArrayOutputStream();
    t.printStackTrace(new PrintStream(ostr));
    return ostr.toString();
  } //end of getExceptionStackTrace(Throwable t)


}
