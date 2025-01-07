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

import com.sap.engine.services.security.Util;
import com.sap.engine.frame.core.configuration.*;
import com.sap.engine.lib.security.domain.PermissionsFactory;
import com.sap.tc.logging.Severity;

import java.util.*;

/**
 *
 * This class manages the persistency of the known protection domain permissions
 * from the cluster. It is initialized from the other class!
 *
 * Used configuration structure:
 *
 * rootCfg {path == this.ROOT_CONFIGURATION }
 *  |
 *  + -- > subCfg: {path == this.KNOWN_PERMISSIONS_CONFIGURATION }
 *  |           |
 *  |           +--> subCfg: {path == <permission_class_name>}
 *  |                  |
 *  |                  +---> subCfg: {path == KNOWN_PERMISSION_NAMES} ---> {cfgEntry: {alias == <instance_name_1>, value = ""}, ..}
 *  |                  |
 *  |                  +---> subCfg: {path == KNOWN_PERMISSION_ACTIONS} ---> {cfgEntry: {alias == <action_1>, value = ""}, ..}
 *  |
 *  |
 *  +-----> subCfg: {path == this.DOMAINS}
 *  |          |
 *  |          + ---> subCfg: {path == <registered_domain_name>}
 *  |          ..        |
 *  |                    + ---> subCfg: {path == this.GRANTED_PERMISSIONS}
 *  |                    ..        |
 *  |                              +---> {}
 *  |
 *
 *
 * @author Ilia Kacarov
 */
class KnownPermissionsStorage extends PermissionsStorageUtils {

  private static final String KNOWN_PERMISSIONS_CONFIGURATION = "known_permissions";
  private static final String KNOWN_PERMISSION_NAMES = "instances";
  private static final String KNOWN_PERMISSION_ACTIONS = "actions";

  synchronized void addKnownPermission(String className, String[] names, String[] actions) throws SecurityException {
    ConfigurationHandler handler = null;
    Configuration rootCfg = null;

    try {
      handler = getHandler();
      rootCfg = getWriteConfiguration(handler);
      add_known_permission(className, names, actions, rootCfg);
      commit(handler);
    } finally {
      close(handler);
    }
  }

  /**
   *
   *   String[0][0] -> class name
   *   String[1][0..N] -> names
   *   String[2][0..M]  -> actions,
   *   N >= 0, M >= 0
   *
   * @return
   */
  Vector getKnownPermissions() throws SecurityException {
    ConfigurationHandler handler = null;

    try {
      handler = getHandler();
      return getKnownPermissions(getReadConfiguration(handler));
    } finally {
      close(handler);
    }
  }

  boolean isStartedForTheFirstTime() throws SecurityException {
    ConfigurationHandler handler = null;

    try {
      handler = getHandler();

      if (getReadConfiguration(handler).existsSubConfiguration(KNOWN_PERMISSIONS_CONFIGURATION)) {
        return false;
      } else {
        handler.closeAllConfigurations();
        initKnownPermissions(getWriteConfiguration(handler));
        commit(handler);
        return true;
      }
    } catch (Exception e) {
      SecurityException bse = new SecurityException("Unexpected - cannot get the root configuration.", e);
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Unexpected - cannot get the root configuration.", e);
      throw bse;
    } finally {
      close(handler);
    }
  }

  synchronized void removeKnownPermission(String className, String[] name, String[] actions) throws SecurityException {
    ConfigurationHandler handler = null;

    try {
      handler = getHandler();
      remove_known_permission(className, name, actions, getWriteConfiguration(handler));
      commit(handler);
    } finally {
      close(handler);
    }
  }



  private void initKnownPermissions(Configuration rootCfg) throws SecurityException {
    try {
      rootCfg.createSubConfiguration(KNOWN_PERMISSIONS_CONFIGURATION);
      String[] defaultPermissionAliases = PermissionsFactory.getAliases();
      for (int i = 0; i < defaultPermissionAliases.length; i++) {
        String className = PermissionsFactory.getClassName(defaultPermissionAliases[i]);
        String[] names = PermissionsFactory.getPermissionNames(defaultPermissionAliases[i]);
        String[] actions = PermissionsFactory.getPermissionActions(defaultPermissionAliases[i]);

        add_known_permission(className, names, actions, rootCfg);
      }
    } catch (Exception ce) {
      Object[] params = new Object[]{(rootCfg == null)? "null root configuration": rootCfg.getPath()};
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Cannot add default permission: {0}", new Object[]{params}, ce);
      throw new SecurityException("Cannot add default permission: " + params, ce);
    }
  }

  private Vector getKnownPermissions(Configuration rootCfg) throws SecurityException {
    try {
      Configuration knownCfg = null;
      Configuration actionsCfg = null;
      Configuration namesCfg = null;
      Vector result = new Vector();

      knownCfg = rootCfg.getSubConfiguration(KNOWN_PERMISSIONS_CONFIGURATION);
      String[] aliases = knownCfg.getAllSubConfigurationNames();
      for (int i = 0; i < aliases.length; i++) {
        String[][] entry =  new String[3][];
        String className = aliases[i];
        entry[0] = new String[]{className};
        actionsCfg = knownCfg.getSubConfiguration(aliases[i]).getSubConfiguration(KNOWN_PERMISSION_ACTIONS);
        entry[2] = decode(actionsCfg.getAllConfigEntryNames());
        namesCfg = knownCfg.getSubConfiguration(aliases[i]).getSubConfiguration(KNOWN_PERMISSION_NAMES);
        entry[1] = decode(namesCfg.getAllConfigEntryNames());
        result.add(entry);
      }
      return result;
    } catch (Exception e) {
      SecurityException bse = new SecurityException("Unexpected - getKnownPermissionAliases().", e);
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Unexpected - getKnownPermissionAliases().", e);
      throw bse;
    }
  }

  private final void add_known_permission(String className, String[] names, String[] actions, Configuration rootCfg) throws SecurityException {
    try {
      Configuration knownCfg = null;
      Configuration aliasCfg = null;
      Configuration nameCfg = null;
      Configuration actionsCfg = null;

      names = encode(names);
      actions = encode(actions);

      knownCfg = rootCfg.getSubConfiguration(KNOWN_PERMISSIONS_CONFIGURATION);

      if (knownCfg.existsSubConfiguration(className)) {
        aliasCfg = knownCfg.getSubConfiguration(className);
      } else {
        aliasCfg = knownCfg.createSubConfiguration(className);
      }
      if (aliasCfg.existsSubConfiguration(KNOWN_PERMISSION_NAMES)) {
        nameCfg = aliasCfg.getSubConfiguration(KNOWN_PERMISSION_NAMES);
      } else {
        nameCfg = aliasCfg.createSubConfiguration(KNOWN_PERMISSION_NAMES);
      }
      if (aliasCfg.existsSubConfiguration(KNOWN_PERMISSION_ACTIONS)) {
        actionsCfg = aliasCfg.getSubConfiguration(KNOWN_PERMISSION_ACTIONS);
      } else {
        actionsCfg = aliasCfg.createSubConfiguration(KNOWN_PERMISSION_ACTIONS);
      }
      if (names != null) {
        for (int j = 0; j < names.length; j++) {
          if (names[j] != null && names[j].length() > 0) {
            if (!nameCfg.existsConfigEntry(names[j])) {
              nameCfg.addConfigEntry(names[j], "");
            }
          }
        }
      }
      if (actions != null) {
        for (int j = 0; j < actions.length; j++) {
          if (actions[j] != null && actions[j].length() != 0) {
            if (!actionsCfg.existsConfigEntry(actions[j])) {
              actionsCfg.addConfigEntry(actions[j], "");
            }
          }
        }
      }
    } catch (ConfigurationException cfg_exc) {
      Object[] params = new Object[] {className, names, actions};
      SecurityException bse = new SecurityException("Cannot add new permission: " + params, cfg_exc);
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Cannot add new permission: {0}.", new Object[]{params}, cfg_exc);
      throw bse;
    }
  }

  private final void remove_known_permission(String className, String[] names, String[] actions, Configuration rootCfg) throws SecurityException {
    try {
      Configuration knownCfg = null;
      Configuration aliasCfg = null;
      Configuration nameCfg = null;
      Configuration actionsCfg = null;

      names = encode(names);
      actions = encode(actions);
      knownCfg = rootCfg.getSubConfiguration(KNOWN_PERMISSIONS_CONFIGURATION);

      if (!knownCfg.existsSubConfiguration(className)) {
        return;
      }
      aliasCfg = knownCfg.getSubConfiguration(className);

      if (names == null && actions == null) {
        aliasCfg.deleteConfiguration();
        return;
      }
      if (aliasCfg.existsSubConfiguration(KNOWN_PERMISSION_NAMES)) {
        nameCfg = aliasCfg.getSubConfiguration(KNOWN_PERMISSION_NAMES);
      } else {
        return;
      }
      if (aliasCfg.existsSubConfiguration(KNOWN_PERMISSION_ACTIONS)) {
        actionsCfg = aliasCfg.getSubConfiguration(KNOWN_PERMISSION_ACTIONS);
      } else {
        return;
      }
      if (names != null) {
        for (int j = 0; j < names.length; j++) {
          if (names[j] == null || names[j] != null && names[j].trim().length() == 0) {
            continue;
          }
          try {
            nameCfg.deleteConfigEntry(names[j]);
          } catch (NameNotFoundException not_found) {
            // already removed
            continue;
          }
        }
      }

      if (actions != null) {
        for (int j = 0; j < actions.length; j++) {
          if (actions[j] == null || actions[j] != null && actions[j].trim().length() == 0) {
            continue;
          }
          try {
            actionsCfg.deleteConfigEntry(actions[j]);
          } catch (NameNotFoundException not_found) {
            // already removed
            continue;
          }
        }
      }
    } catch (ConfigurationException cfg_exc) {
      Object[] params = new Object[] {className, names, actions};
      SecurityException bse = new SecurityException("Cannot remove known permission: " + params, cfg_exc);
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Cannot remove known permission: {0}.", new Object[]{params}, cfg_exc);
      throw bse;
    }
  }

}
