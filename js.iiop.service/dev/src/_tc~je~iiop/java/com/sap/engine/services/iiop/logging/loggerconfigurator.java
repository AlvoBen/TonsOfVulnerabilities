package com.sap.engine.services.iiop.logging;

import com.sap.tc.logging.*;

import java.util.Properties;
import java.lang.reflect.Field;

/**
 * Created by IntelliJ IDEA.
 * User: pavel-b
 * Date: 2004-9-8
 * Time: 11:57:29
 * To change this template use Options | File Templates.
 */
public class LoggerConfigurator {

  public static final String DEST_GENERAL_ID = "com.sap.engine.services.iiop.general";
  public static final String DEST_ACTIVATION_ID = "com.sap.engine.services.iiop.activation";
  public static final String DEST_REQUEST_FLOW_ID = "com.sap.engine.services.iiop.request_flow";

  private static final String[] PROP_TYPE = {"clientlog.general.type",
                                             "clientlog.activation.type",
                                             "clientlog.request_flow.type"};

  private static final String[] PROP_SEVERITY = {"clientlog.general.severity",
                                                 "clientlog.activation.severity",
                                                 "clientlog.request_flow.severity"};

  public static final String PROP_TYPE_CONSOLE_VALUE = "CONSOLE";


  public static final String PROPS_FILE_NAME = "clientlog.properties";

  public static final int TYPE_SERVER = 0;
  public static final int TYPE_CLIENT = 1;

  public static final int DEST_GENERAL = 0;
  public static final int DEST_ACTIVATION = 1;
  public static final int DEST_REQUEST_FLOW = 2;

  private static final int DEFAULT_DEST = DEST_GENERAL;

  private static int loggerType = TYPE_SERVER;

  public static Location[] location = new Location[3];

  static {
    setType(loggerType);
  }

  public static void setType(int newType) throws IllegalArgumentException {
    if ((newType == TYPE_SERVER) ||
        (newType == TYPE_CLIENT)) {
      loggerType = newType;
      init();
    } else {
      throw new IllegalArgumentException("Unknown logging type:"+newType);
    }

  }

  public static int getType() {
    return loggerType;
  }

  public static Location getLocation(int dest) {
    // Possible ArrayIndexOutOfBoundsException!!!
    return location[dest];
  }

  public static Location getLocation() {
    return location[DEFAULT_DEST];
  }

  public static String exceptionTrace(Throwable t) {
    java.io.ByteArrayOutputStream ostr = new java.io.ByteArrayOutputStream();
    t.printStackTrace(new java.io.PrintStream(ostr));
    
    return ostr.toString();
  }

  private static void init() {
    location[DEST_GENERAL] = Location.getLocation(DEST_GENERAL_ID);
    location[DEST_ACTIVATION] = Location.getLocation(DEST_ACTIVATION_ID);
    location[DEST_REQUEST_FLOW] = Location.getLocation(DEST_REQUEST_FLOW_ID);

    if (loggerType == TYPE_CLIENT) {
      Properties props = getProps();

      for (int i = 0; i < 3; i++) {
        String typeProp = props.getProperty(PROP_TYPE[i],PROP_TYPE_CONSOLE_VALUE);
        String severityProp = props.getProperty(PROP_SEVERITY[i],"ERROR");

        if (typeProp != null && !typeProp.equals("")) {
          if (typeProp.equals(PROP_TYPE_CONSOLE_VALUE)) {
            location[i].addLog(new ConsoleLog()); //$JL-CONSOLE_LOG$
          } else {
            location[i].addLog(new FileLog(typeProp,true));
          }
        }

        int severity = turnIntoInt(severityProp);
        location[i].setEffectiveSeverity(severity);
      }
    }
  }

  private static Properties getProps() {
    Properties props = new Properties();
    try {
      java.net.URL propsURL = ClassLoader.getSystemResource(PROPS_FILE_NAME);
      if (propsURL != null) {
        props.load(propsURL.openStream());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return props;
    
  }

  private static int turnIntoInt(String severityStr) {
    int severity = Severity.ERROR;

    if (severityStr == null || severityStr.equals("")) {
      return severity;
    }

    try {
      Field field = Severity.class.getField(severityStr);
      severity = field.getInt(Severity.class);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return severity;
  }
}

