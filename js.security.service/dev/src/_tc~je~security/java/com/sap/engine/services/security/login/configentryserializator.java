/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.login;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.services.security.Util;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 *
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class ConfigEntrySerializator {
  public static final int MAX_BYTES = 2000;
  
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);

  public final static AppConfigurationEntry[] readAppConfigurationEntryArray(Configuration configuration) {
    try {
      int size = getSize(configuration);
      AppConfigurationEntry[] result = new AppConfigurationEntry[size];
      Configuration sub = null;
      String appconfigentryClassname = null;
      String classname = null;
      AppConfigurationEntry.LoginModuleControlFlag flag = null;
      Hashtable options = null;
      Object flagValue = null;

      for (int i = 0; i < size; i++) {
        sub = configuration.getSubConfiguration("" + i);
        classname = (String) sub.getConfigEntry("classname");
        flagValue = sub.getConfigEntry("flag");

        if (flagValue instanceof Integer) {
          switch (((Integer) flagValue).intValue()) {
            case 0: {
              flag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
              break;
            }
            case 1: {
              flag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
              break;
            }
            case 2: {
              flag = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
              break;
            }
            case 3: {
              flag = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
              break;
            }
          }
        } else if (flagValue instanceof String) {
          if ("OPTIONAL".equals(flagValue)) {
            flag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
          } else if ("REQUIRED".equals(flagValue)) {
            flag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
          } else if ("REQUISITE".equals(flagValue)) {
            flag = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
          } else if ("SUFFICIENT".equals(flagValue)) {
            flag = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
          }
        }

        options = new Hashtable(11);
        try {
          Configuration optionsConfiguration = sub.getSubConfiguration("options");
          String[] keys = optionsConfiguration.getAllConfigEntryNames();
          String[] fileKeys = optionsConfiguration.getAllFileEntryNames();
          Object value = null;


          for (int k = 0; k < keys.length; k++) {
            value = optionsConfiguration.getConfigEntry(keys[k]);

            if (value instanceof byte[]) {
              byte[] array = (byte[]) value;
              options.put(keys[k], Util.array2object(array, 0, array.length));
            } else {
              options.put(keys[k], value);
            }
          }

          for (int k = 0; k < fileKeys.length; k++) {
            byte[] arr = readInputStream(optionsConfiguration.getFile(fileKeys[k]));
            options.put(fileKeys[k], Util.array2object(arr, 0, arr.length));
          }
        } catch (Exception e) {
          if (LOCATION.beWarning()) {
            LOCATION.traceThrowableT(Severity.WARNING, "Could not properly read the login module options from the configuration with path {0}.", new Object[] {sub.getPath()}, e);
          }
        }

        try {
          appconfigentryClassname = (String) sub.getConfigEntry("appconfigentryclassname");
        } catch (Exception e) {
          /////
          //  ignore this. goes to default
          appconfigentryClassname = null;
        }

        if ((appconfigentryClassname == null) || (appconfigentryClassname.equals(AppConfigurationEntry.class.getName()))) {
          result[i] = new AppConfigurationEntry(classname, flag, options);
        } else {
          try {
            Class classs = Class.forName(appconfigentryClassname);
            Constructor constructor = classs.getConstructor(new Class[] { String.class, AppConfigurationEntry.LoginModuleControlFlag.class, Map.class} );
            result[i] = (AppConfigurationEntry) constructor.newInstance(new Object[] { classname, flag, options });
          } catch (Throwable t) {
            if (LOCATION.beWarning()) {
              LOCATION.traceThrowableT(Severity.WARNING, t.getLocalizedMessage(), t);
            }
          }
        }
      }

      return result;
    } catch (Exception e) {
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Cannot deserialize AppConfigurationEntry[].", e);
      }
      return new AppConfigurationEntry[0];
    }
  }

  private static byte[] readInputStream(InputStream file) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int bufferSize = 256;
    byte[] io_buffer = new byte[bufferSize];
    int readBytesCount = -1;

    while ((readBytesCount = file.read(io_buffer)) != -1) {
      out.write(io_buffer, 0, readBytesCount);
    }

    return out.toByteArray();
  }

  public final static void writeAppConfigurationEntryArray(Configuration configuration, AppConfigurationEntry[] array) {
    try {
      if (array == null) {
        array = new AppConfigurationEntry[0];
      }

      String flag = "OPTIONAL";
      Hashtable properties = null;
      Configuration sub = null;

      try {
        configuration.deleteAllSubConfigurations();
      } catch (Exception e) {
        String[] subnames = configuration.getAllSubConfigurationNames();

        if ((subnames != null) && (subnames.length > 0)) {
          if (LOCATION.beWarning()) {
            LOCATION.traceThrowableT(Severity.WARNING, e.getLocalizedMessage(), e);
          }
        }
      }

      try {
        configuration.addConfigEntry("size", new Integer(array.length));
      } catch (Exception e) {
        configuration.modifyConfigEntry("size", new Integer(array.length));
      }

      for (int i = 0; i < array.length; i++) {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Trying to serialize {0}.", new Object[] {array[i].getLoginModuleName()});
        }

        sub = configuration.createSubConfiguration("" + i);

        if (!array[i].getClass().getName().equals(AppConfigurationEntry.class.getName())) {
          sub.addConfigEntry("appconfigentryclassname", array[i].getClass().getName()); 
        }

        sub.addConfigEntry("classname", array[i].getLoginModuleName());

        if (array[i].getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL) {
          flag = "OPTIONAL";
        } else if (array[i].getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED) {
          flag = "REQUIRED";
        } else if (array[i].getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE) {
          flag = "REQUISITE";
        } else if (array[i].getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT) {
          flag = "SUFFICIENT";
        }

        sub.addConfigEntry("flag", flag);

        /////
        //  do it with serialization of the whole map but be careful with class loaders
        if (array[i].getOptions() instanceof Hashtable) {
          properties = (Hashtable) array[i].getOptions();
        } else {
          properties = new Hashtable();
          properties.putAll(array[i].getOptions());
        }

        Configuration optionsConfiguration = sub.createSubConfiguration("options");
        Enumeration enumeration = properties.keys();
        String key = null;
        Object value = null;

        try {
          while (enumeration.hasMoreElements()) {
            key = (String) enumeration.nextElement();
            value = properties.get(key);

            if (value instanceof Serializable) {
              if ((value != null) && (value.getClass().getClassLoader() != null)) {
                byte[] arr = Util.object2array((Serializable) properties.get(key));
                if (arr.length <= MAX_BYTES) {
                  optionsConfiguration.addConfigEntry(key, arr);
                } else {
                  InputStream in = new ByteArrayInputStream(arr);
                  optionsConfiguration.addFileAsStream(key, in);
                }
              } else {
                optionsConfiguration.addConfigEntry(key, properties.get(key));
              }
            } else {
              if (LOCATION.beError()) {
                SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000183", "The option {0} of {1} is not serializable and cannot be set.", new Object[] {key, array[i].getLoginModuleName()});
              }
            }
          }
        } catch (Exception e) {
          SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.000184", "The option {0} of {1} or some of its fields is not serializable and cannot be set.", new Object[] {key, array[i].getLoginModuleName()});
        }
      }
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000185", "Cannot serialize AppConfigurationEntry[].", e);
    }
  }

  public final static int getSize(Configuration configuration) {
    try {
      return ((Integer) configuration.getConfigEntry("size")).intValue();
    } catch (Exception e) {
      return 0;
    }
  }

      }
