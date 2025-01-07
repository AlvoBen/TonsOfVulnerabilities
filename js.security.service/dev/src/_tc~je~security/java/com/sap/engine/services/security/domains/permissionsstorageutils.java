/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.domains;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.tc.logging.Severity;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

abstract class PermissionsStorageUtils {

  protected static final char[] FORBIDDEN_CONFIGNAME_CHARS = new char[] {'%','[',']','#','/'};
  protected static final String ROOT_CONFIGURATION = "protected_domains";

  private ConfigurationHandlerFactory configurationHandlerFactory = null;

  public PermissionsStorageUtils() {
    configurationHandlerFactory = SecurityServerFrame.getServiceContext().getCoreContext().getConfigurationHandlerFactory();
  }

  protected void close(ConfigurationHandler handler) throws SecurityException {
    if (handler != null) {
      try {
        handler.rollback();
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "unexpected configuration problem", e);
      }
      try {
        handler.closeAllConfigurations();
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "unexpected configuration problem", e);
        throw new SecurityException("Unexpected configuration problem", e);
      }
    }
  }

  protected void commit(ConfigurationHandler handler) throws SecurityException {
    if (handler != null) {
      try {
        handler.commit();
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "unexpected configuration problem", e);
        throw new SecurityException("Unexpected configuration problem", e);
      }
    }
  }

  protected final static String decode(String in) throws SecurityException {
    if (in == null) {
      return null;
    }
    StringBuffer temp = new StringBuffer();
    int i = 0;
    char next = 0;

    while (i < in.length()) {
      next = 0;
      try {
        next = in.charAt(i + 1);
      } catch (IndexOutOfBoundsException e) {
        next = 0;
      }
      if (in.charAt(i) != '$' || next == 0) {
        temp.append(in.charAt(i));
        i++;
      } else {
        if (next == '$') {
          temp.append(in.charAt(i));
        } else {
          int pos = -1;
          try {
            pos = Character.getNumericValue(next);
            temp.append(FORBIDDEN_CONFIGNAME_CHARS[pos]);
          } catch (Exception e) {
            Object[] params = new Object[] {in, "" + next};
            Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "decoding of [{0}] fails - unexpected character after '$' - [{1}].", params, e);
            throw new SecurityException("Decoding of [" + params[0] + "] fails - unexpected character after '$' - [" + params[1] + "].", e);
          }

        }
        i += 2;
      }
    }

    return temp.toString();
  }

  protected final String[] decode(String[] strings) throws SecurityException {
    if (strings == null) {
      return null;
    }
    for (int i = 0; i < strings.length; i++) {
      if (strings[i] != null) {
        strings[i] = decode(strings[i]);
      }
    }
    return strings;
  }

  protected static final String encode(String in) {
    if (in == null || in.trim().equals("")) {
      return null;
    }

    StringBuffer temp = new StringBuffer();
    for (int i = 0; i < in.length(); i++) {
      int illegalCharPos = -1;
      for (int j = 0; j < FORBIDDEN_CONFIGNAME_CHARS.length; j++) {
        if (in.charAt(i) == FORBIDDEN_CONFIGNAME_CHARS[j]) {
          illegalCharPos = j;
          break;
        }
      }
      if (illegalCharPos == -1) {
        if (in.charAt(i) == '$') {
          temp.append(in.charAt(i));
        }
        temp.append(in.charAt(i));
      } else {
        temp.append("$" + illegalCharPos);
      }
    }

    String result = temp.toString();
    return result;
  }

  protected static final String[] encode(String[] strings) {
    if (strings == null) {
      return null;
    }
    for (int i = 0; i < strings.length; i++) {
      strings[i] = encode(strings[i]);
    }
    return strings;
  }

  protected ConfigurationHandler getHandler() throws SecurityException {
    try {
      return configurationHandlerFactory.getConfigurationHandler();
    } catch (Exception e) {
      SecurityException bse = new SecurityException("Cannot get a handler.", e);
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Cannot get a handler.", e);
      throw bse;
    }
  }

  protected Configuration getReadConfiguration(ConfigurationHandler handler) throws SecurityException {
    try {
      while (true) {
        try {
          try {
            return handler.openConfiguration(ROOT_CONFIGURATION, handler.READ_ACCESS);
          } catch (NameNotFoundException ne) {
            return handler.createRootConfiguration(ROOT_CONFIGURATION);
          }
        } catch (ConfigurationLockedException _) {
          try {
            synchronized (this) {
              wait(100);
            }
          } catch (InterruptedException e) {
            return null;
          }
        }
      }
    } catch (Exception e) {
      Object[] params = new Object[] {ROOT_CONFIGURATION};
      SecurityException bse = new SecurityException("Cannot open the root configuration [" + params[0] + "] (READ_ACCESS mode).", e);
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "getReadConfiguration(): Cannot open the root configuration \"{0}\" (READ_ACCESS mode).", params, e);
      throw bse;
    }
  }

  protected Configuration getWriteConfiguration(ConfigurationHandler handler) throws SecurityException {
    try {
      while (true) {
        try {
          try {
            return handler.openConfiguration(ROOT_CONFIGURATION, handler.WRITE_ACCESS);
          } catch (NameNotFoundException ne) {
            return handler.createRootConfiguration(ROOT_CONFIGURATION);
          }
        } catch (ConfigurationLockedException _) {
          try {
            synchronized (this) {
              wait(100);
            }
          } catch (InterruptedException e) {
            return null;
          }
        }

      }
    } catch (Exception e) {
      Object[] params = new Object[] {ROOT_CONFIGURATION};
      SecurityException bse = new SecurityException("Cannot open the root configuration [" + params[0] + "] (WRITE_ACCESS mode).", e);
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "getWriteConfiguration(): Cannot open the root configuration \"{0}\" (WRITE_ACCESS mode).", params, e);
      throw bse;
    }
  }


  protected void rollback(ConfigurationHandler handler) throws SecurityException {
    if (handler != null) {
      try {
        handler.rollback();
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "unexpected configuration problem", e);
        throw new SecurityException("Unexpected configuration problem", e);
      }
    }
  }

  protected final boolean memberOf(Vector permissions, PRecord single_permission) {
    if (permissions == null) {
      return false;
    }
    return permissions.contains(single_permission);
  }

  protected static void deletePermissionTree(Vector permissions, Configuration rootCfg) throws Exception {
    if (permissions == null) {
      rootCfg.deleteConfiguration();
      return;
    }

    Configuration permission_cfg = null;
    Configuration target_cfg = null;
    PRecord p_record = null;
    String permission_class = null;
    String permission_target = null;
    String target_actions = null;
    String[] actions =null;


    for (int i = 0; i < permissions.size(); i++) {
      p_record = (PRecord) permissions.get(i);

      permission_class = encode(p_record.getClassName());
      permission_target = encode(p_record.getName());
      target_actions = encode(p_record.getActions());

      if (!rootCfg.existsSubConfiguration(permission_class)) {
        continue;
      }

      permission_cfg = rootCfg.getSubConfiguration(permission_class);
      if (permission_target == null || permission_target.length() == 0) {
        // removing the whole permission !!!
        permission_cfg.deleteConfiguration();
        continue;
      }

      if (!permission_cfg.existsSubConfiguration(permission_target)) {
        continue;
      }

      target_cfg = permission_cfg.getSubConfiguration(permission_target);

      if (target_actions == null || target_actions.length() == 0) {
        // permission with target and NO actions, dropping the target
        target_cfg.deleteConfiguration();
      } else {
        // actions found - removing them one by one from target
        actions = parseActions(target_actions);
        for (int j = 0; j < actions.length; j++) {
          if (target_cfg.existsConfigEntry(actions[j])) {
            target_cfg.deleteConfigEntry(actions[j]);
          }
        }

        if (target_cfg.getAllConfigEntryNames().length == 0) {
          //  all actions has being removed, so there is no sence to keep target's
          //  configuration in this case
          target_cfg.deleteConfiguration();
        }
      }

      if (permission_cfg.getAllSubConfigurationNames().length == 0) {
        // no more targets for this permission, removing it
        permission_cfg.deleteConfiguration();
      }
    }

    if (rootCfg.getAllSubConfigurationNames().length == 0) {
      // no more  permissions left in this root, removing it
      rootCfg.deleteConfiguration();
    }
  }

  protected static Vector loadPermissionTree(Configuration cfg) throws Exception {
    String[] permissions = cfg.getAllSubConfigurationNames();
    Vector result = new Vector(permissions.length);

    for (int i = 0; i < permissions.length; i++) {
      Configuration p_cfg = cfg.getSubConfiguration(permissions[i]);
      String[] names = p_cfg.getAllSubConfigurationNames();
      if (names.length == 0) {
        result.add(new PRecord(decode(permissions[i]), "", ""));
        continue;
      }
      for (int j = 0; j < names.length; j++) {
        Configuration n_cfg = p_cfg.getSubConfiguration(names[j]);
        String[] actions = n_cfg.getAllConfigEntryNames();
        if (actions.length == 0) {
          result.add(new PRecord(decode(permissions[i]), decode(names[j]), null));
        } else {
          for (int k = 0; k < actions.length; k++) {
            result.add(new PRecord(decode(permissions[i]), decode(names[j]), decode(actions[k])));
          }
        }
      }
    }

    return result;
  }


  protected static void savePermissionTree(Vector permissions, Configuration cfg) throws Exception {
    if (permissions == null) {
      return;
    }

    Configuration permission_class_cfg = null;
    Configuration target_cfg = null;
    String permission_class = null;
    String permission_target = null;
    String permission_actions = null;
    PRecord p_record = null;


    for (int i = 0; i < permissions.size(); i++) {
      p_record = (PRecord)  permissions.get(i);
      permission_class = encode(p_record.getClassName());
      permission_target = encode(p_record.getName());
      permission_actions = encode(p_record.getActions());

      if (cfg.existsSubConfiguration(permission_class)){
        permission_class_cfg = cfg.getSubConfiguration(permission_class);
      } else {
        permission_class_cfg = cfg.createSubConfiguration(permission_class);
      }

      permission_class_cfg = cfg.getSubConfiguration(permission_class);
      if (permission_target == null || permission_target.length() == 0) { // no instance
        continue;
      }

      if (permission_class_cfg.existsSubConfiguration(permission_target)) {
        target_cfg = permission_class_cfg.getSubConfiguration(permission_target);
      } else {
        target_cfg = permission_class_cfg.createSubConfiguration(permission_target);
      }

      if (permission_actions != null && permission_actions.length() > 0) {
        if (!target_cfg.existsConfigEntry(permission_actions)) {
          target_cfg.addConfigEntry(permission_actions, "-");
        }
      }
    }
  }

  protected static Vector parsePermissionCollection(CodeSource codeSource) throws Exception {
    Policy policy = Policy.getPolicy();

    PermissionCollection rootPermissions = policy.getPermissions(codeSource);
    return parsePermissionCollection(rootPermissions);
  }

  private static Vector parsePermissionCollection(PermissionCollection collection) throws Exception {
    Vector result = new Vector();
    Enumeration elements = collection.elements();

    while (elements.hasMoreElements()) {
      result.add(new PRecord((Permission) elements.nextElement()));
    }

    return result;
  }

  private static String[] parseActions(String actions) {
    if (actions == null) {
      return null;
    }

    if (actions.indexOf(",") == -1) {
      return new String[]{actions};
    }

    StringTokenizer tokens = new StringTokenizer(actions, ",", false);
    String[] result = new String[tokens.countTokens() + 1];
    int i = 0;
    while (tokens.hasMoreTokens()) {
      result[i++] = tokens.nextToken();
    }
    result[result.length - 1] = actions; // for bakward compatibility, if there are some actions "a,b,c"
    return result;
  }

}