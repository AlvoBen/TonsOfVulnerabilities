/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.resource.instance;

import java.util.Vector;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.interfaces.security.resource.ResourceHandle;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.exceptions.StorageException;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.tc.logging.Severity;

public class ACLManager {

  public final static String CONTAINER = "action";
  public final static String LIST_GRANTED = "granted";
  public final static String LIST_DENIED = "denied";

  public void prepareContainer(Configuration parentContainer) throws SecurityException, ConfigurationException {
    try {
      parentContainer.getSubConfiguration(CONTAINER);
    } catch (Exception e) {
      try {
        parentContainer.createSubConfiguration(CONTAINER);
      } catch (StorageLockedException sle) {
        throw sle;
      } catch (StorageException se) {
        throw se;
      }
    }

    try {
      parentContainer.getSubConfiguration(CONTAINER).getSubConfiguration(ResourceHandle.ALL);
    } catch (Exception e) {
      try {
        parentContainer.getSubConfiguration(CONTAINER).createSubConfiguration(ResourceHandle.ALL);
      } catch (StorageLockedException sle) {
        throw sle;
      } catch (StorageException se) {
        throw se;
      }
    }
  }


  public void grantPermission(String role, String action, String instance, Configuration parentContainer) throws ConfigurationException, SecurityException {
    try {
      clearPermission(role, action, instance, parentContainer);
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "grantPermission", e);
    }

    Configuration configuration = getConfiguration(parentContainer, CONTAINER);
    configuration = getConfiguration(configuration, action);
    configuration = getConfiguration(configuration, instance);
    configuration = getConfiguration(configuration, LIST_GRANTED);
    configuration.addConfigEntry(role, "");
  }

  public void denyPermission(String role, String action, String instance, Configuration parentContainer) throws ConfigurationException, SecurityException {
    try {
      clearPermission(role, action, instance, parentContainer);
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "denyPermission", e);
    }

    Configuration configuration = getConfiguration(parentContainer, CONTAINER);
    configuration = getConfiguration(configuration, action);
    configuration = getConfiguration(configuration, instance);
    configuration = getConfiguration(configuration, LIST_DENIED);
    configuration.addConfigEntry(role, "");
  }

  public void clearPermission(String role, String action, String instance, Configuration parentContainer) throws ConfigurationException, SecurityException {
    Configuration list = null;
    Configuration configuration = getConfiguration(parentContainer, CONTAINER);
    configuration = getConfiguration(configuration, action);
    configuration = getConfiguration(configuration, instance);
    list = getConfiguration(configuration, LIST_DENIED);

    if (list.existsConfigEntry(role)) {
      list.deleteConfigEntry(role);
    }

    list = getConfiguration(configuration, LIST_GRANTED);

    if (list.existsConfigEntry(role)) {
      list.deleteConfigEntry(role);
    }
  }

  public Vector listGrantedSecurityRoles(String action, String instance, Configuration parentContainer) throws ConfigurationException, SecurityException {
    Vector result = new Vector();
    Configuration configuration = parentContainer;
    try {
      configuration = configuration.getSubConfiguration(CONTAINER);
    } catch (Exception e) {
      return result;
    }

    try {
      configuration = configuration.getSubConfiguration(action);
    } catch (Exception e) {
      return result;
    }

    try {
      configuration = configuration.getSubConfiguration(instance);
    } catch (Exception e) {
      return result;
    }

    try {
      configuration = configuration.getSubConfiguration(LIST_GRANTED);
    } catch (Exception e) {
      return result;
    }

    String[] names = configuration.getAllConfigEntryNames();

    for (int i = 0; i < names.length; i++) {
      result.add(names[i]);
    }

    return result;
  }

  public java.util.Vector listDeniedSecurityRoles(String action, String instance, Configuration parentContainer) throws ConfigurationException, SecurityException {
    Vector result = new Vector();
    Configuration configuration = parentContainer;
    try {
      configuration = configuration.getSubConfiguration(CONTAINER);
    } catch (Exception e) {
      return result;
    }

    try {
      configuration = configuration.getSubConfiguration(action);
    } catch (Exception e) {
      return result;
    }

    try {
      configuration = configuration.getSubConfiguration(instance);
    } catch (Exception e) {
      return result;
    }

    try {
      configuration = configuration.getSubConfiguration(LIST_DENIED);
    } catch (Exception e) {
      return result;
    }

    String[] names = configuration.getAllConfigEntryNames();

    for (int i = 0; i < names.length; i++) {
      result.add(names[i]);
    }

    return result;
  }

  public void instanceRemoved(String instance, Configuration parentContainer) throws ConfigurationException, SecurityException {
    Configuration actionConfiguration = null;
    Configuration configuration = parentContainer;
    configuration = getConfiguration(configuration, CONTAINER);
    String[] actions = configuration.getAllSubConfigurationNames();

    for (int i = 0; i < actions.length; i++) {
      try {
        actionConfiguration = configuration.getSubConfiguration(actions[i]);
        actionConfiguration.deleteConfiguration(instance);
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "instanceRemoved", e);
      }
    }
  }

  public void actionRemoved(String action, Configuration parentContainer) throws ConfigurationException, SecurityException {
    try {
      getConfiguration(parentContainer, CONTAINER).deleteConfiguration(action);
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "instanceRemoved", e);
    }
  }

  public void resourceRenamed(String action, Configuration parentContainer) throws ConfigurationException, SecurityException {
    throw new UnsupportedOperationException();
  }

  private final Configuration getConfiguration(Configuration parent, String name) throws ConfigurationException {
    try {
      return parent.getSubConfiguration(name);
    } catch (Exception e) {
      return parent.createSubConfiguration(name);
    }
  }

}

