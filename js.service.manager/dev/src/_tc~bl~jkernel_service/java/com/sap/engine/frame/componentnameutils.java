package com.sap.engine.frame;

import com.sap.engine.frame.core.configuration.*;

import java.util.*;
import java.io.*;

import static com.sap.engine.frame.container.monitor.InterfaceMonitor.*;


/**
 * This class provides a way get the mapping between DC and RT names. DC and RT names should be equals but due to
 * historical reasons some of them differ. The class is deprecated because all differences should be removed in
 * the future versions.
 *
 * @author Dimitar Kostadinov
 * @version 7.10
 * @deprecated
 */
public class ComponentNameUtils {

  /**
   * Default SAP providers for RT names
   */
  private static final String[] SAP_PROVIDERS = new String[] {"engine.sap.com", "sap.com"};

  /**
   * array with all forbidden characters and legal char from:
   * //engine/js.deploy.controller/CoreDev_stream/src/tc~bl~dc_core/_tc~bl~dc~core/java/com/sap/engine/services/dc/util/ComponentPropsCorrector.java
   */
  private static final char [] ILLEGAL_CHARS = new char[] {'/', '\\', ':', '*', '?', '"', '<', '>', '|', ';', ',', '=', '%', '[', ']', '#', '&'};
  private static final char LEGAL_REPLACEMENT_CHAR = '~';

  /**
   * Construct RT name from provider-name and component-name.
   *
   * @param providerName - provider-name from provider.xml
   * @param componentName - component-name from provider.xml
   * @return RT runtime name
   */
  public static String getRuntimeName(String providerName, String componentName) {
    String result;
    for (String aSAP_PROVIDERS : SAP_PROVIDERS) {
      if (componentName.startsWith(aSAP_PROVIDERS + '/')) {
        componentName = componentName.substring(aSAP_PROVIDERS.length() + 1);
        break;
      }
    }
    componentName = componentName.replace('/', '~');
    result = componentName;
    if (!providerName.equals("")) {
      boolean isSapProvider = false;
      for (String aSAP_PROVIDERS : SAP_PROVIDERS) {
        if (providerName.equalsIgnoreCase(aSAP_PROVIDERS)) {
          isSapProvider = true;
          break;
        }
      }
      if (!isSapProvider) {
        providerName = providerName.replace('/', '~');
        result = providerName + '~' + result;
      }
    }
    //check for double interface name
    if (result.endsWith("_api")) {
      for (String aINTERFACE_NAMES_API : INTERFACE_NAMES_API) {
        if (result.equals(aINTERFACE_NAMES_API)) {
          result = result.substring(0, result.length() - 4);
        }
      }
    }
    return result;
  }

  /**
   * Construct DC name from keyvendor and keyname.
   *
   * @param keyvendor - value from SAP_MANIFEST.MF
   * @param keyname - value from SAP_MANIFEST.MF
   * @return DC name
   */
  public static String getDCName(String keyvendor, String keyname) {
    return getCorrected(keyname);
  }

  /**
   * Returns raw mapping between DC and Runtime names. If keyname from SAP_MANIFEST.MF is not equals to component-name
   * form provider.xml and/or keyvendor from SAP_MANIFEST.MF is not equals to provider-name from provider.xml the
   * following entry is added in the result:
   * <keyvendor>$<keyname>=<provider-name>$<component-name>
   *
   * @param factory - configuration handler factory (from serviceContext.getCoreContext().getConfigurationHandlerFactory())
   * @return raw name mapping
   * @throws ConfigurationException if DB error occurs
   * @throws IOException if IO error occurs
   */
  public static Properties getRawMapping(ConfigurationHandlerFactory factory) throws ConfigurationException, IOException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    Configuration cfg = null;
    Properties mapping = new Properties();
    try {
      boolean success = false;
      while (!success) {
        try {
          cfg = handler.openConfiguration("cluster_config/globals/undeploy", ConfigurationHandler.READ_ACCESS);
          success = true;
        } catch (ConfigurationLockedException e) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException ie) {
            // $JL-EXC$
          }
        }
      }
      if (cfg.existsFile("mapping.txt")) {
        InputStream is = null;
        try {
          is = cfg.getFile("mapping.txt");
          mapping.load(is);
        } finally {
          if (is != null) is.close();
        }
      }
    } finally {
      if (handler != null) handler.closeAllConfigurations();
    }
    return mapping;
  }

  /**
   * Returns mapping between DC and RT names which are not equals.
   *
   * @param factory - configuration handler factory (from serviceContext.getCoreContext().getConfigurationHandlerFactory())
   * @return DC to RT names mapping
   * @throws ConfigurationException if DB error occurs
   * @throws IOException if IO error occurs
   */
  public static HashMap<String, String> getDCtoRTMapping(ConfigurationHandlerFactory factory) throws ConfigurationException, IOException {
    return getMapping(factory, true);
  }

  /**
   * Returns mapping between RT and DC names which are not equals. In this case some disparities can be missed because there are
   * equals RT names for different software types (for example security service and security interface).
   * In addition mapping between "double name" interfaces is supported, so that the mapping must be carefully used;
   * Recommendation how to use the mapping: There are components with equals runtime names, but different component types;
   * therefore first try to use the original name and if such DC doesn’t exists try to use the mapping; the example is cross s
   * ervice & cross interface, log service & log interface, etc.
   *
   * @param factory - configuration handler factory (from serviceContext.getCoreContext().getConfigurationHandlerFactory())
   * @return RT to DC names mapping
   * @throws ConfigurationException if DB error occurs
   * @throws IOException if IO error occurs
   */
  public static HashMap<String, String> getRTtoDCMapping(ConfigurationHandlerFactory factory) throws ConfigurationException, IOException {
    return getMapping(factory, false);
  }

  /**
   * Replaces all forbiden characters with the character '~'. This method is from:
   * //engine/js.deploy.controller/CoreDev_stream/src/tc~bl~dc_core/_tc~bl~dc~core/java/com/sap/engine/services/dc/util/ComponentPropsCorrector.java
   *
   * @param name the <code>String</code> which has to be chekced and fixed.
   * @return <code>String</code> which has been checked and fixed.
   */
  public static String getCorrected(String name) {
    // in case of null or blank string ( white space only ) do not go any further
    if ( name == null || name.trim().length() == 0  ) {
      return name;
    }
    char[] chars = name.toCharArray();
    // for each character check if it is an illegal one and replace it accordingly
    for (int i = 0; i < chars.length; i++) {
      for (char aILLEGAL_CHARS : ILLEGAL_CHARS) {
        if (chars[i] == aILLEGAL_CHARS) {
          chars[i] = LEGAL_REPLACEMENT_CHAR;
          break; // no need to check for the rest of the illegal chars
        }
      }
    }
    // since the name of the appliation can't end with '.' if this is the case replace the dot with the legal char
    if (chars[chars.length - 1] == '.' ) {
      chars[chars.length - 1] = LEGAL_REPLACEMENT_CHAR;
    }
    return new String(chars);
  }

  private static HashMap<String, String> getMapping(ConfigurationHandlerFactory factory, boolean straight) throws ConfigurationException, IOException {
    Properties mapping = getRawMapping(factory);
    HashMap<String, String> result = new HashMap<String, String>();
    for (Object keyObj : mapping.keySet()) {
      String[] kvkn = splitValue((String) keyObj);
      String[] pncn = splitValue(mapping.getProperty((String) keyObj));
      String DC = getDCName(kvkn[0], kvkn[1]);
      String RT = getRuntimeName(pncn[0], pncn[1]);
      if (!DC.equals(RT)) {
        if (straight) {
          result.put(DC, RT);
        } else {
          result.put(RT, DC);
          //if double name interface add additional entry
          if (DC.endsWith("_api")) {
            String RT_api = RT + "_api";
            for (String aINTERFACE_NAMES_API : INTERFACE_NAMES_API) {
              if (RT_api.equals(aINTERFACE_NAMES_API)) {
                result.put(RT_api, DC);
                break;
              }
            }
          }
        }
      }
    }
    //for all double name interfaces add additional entry (<i_name> -> <i_name>_api) in RT -> DC case
    if (!straight) {
      for (int i = 0; i < INTERFACE_NAMES.length; i++) {
        if (!result.containsKey(INTERFACE_NAMES[i])) {
          result.put(INTERFACE_NAMES[i], INTERFACE_NAMES_API[i]);
        }
      }
    }
    return result;
  }

  //split <vendor>$<name> string
  private static String[] splitValue(String value) {
    String[] result = new String[2];
    int index = value.indexOf('$');
    result[0] = value.substring(0, index);
    result[1] = value.substring(index + 1);
    return result;
  }

  //TEST
//  public static void main(String[] args) throws Exception {
//    Properties p = new Properties();
//    FileInputStream f = new FileInputStream("C:\\usr\\sap\\N10\\JC60\\j2ee\\configtool\\config.properties");
//    p.load(f);
//    f.close();
//    ConfigurationHandlerFactory factory = new ConfigurationManagerBootstrapImpl(p);
//    System.out.println(getRawMapping(factory));
//    System.out.println(getDCtoRTMapping(factory));
//    System.out.println(getRTtoDCMapping(factory));
//    System.exit(0);
//  }

}