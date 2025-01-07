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

import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.engine.services.security.exceptions.StorageException;
import com.sap.engine.interfaces.security.resource.ResourceHandle;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;

public class InstanceManager {

  public final static String INSTANCES_CONTAINER = "instance";
  public final static String PARENTSHIP_CONTAINER = "instance-tree";

  public void prepareContainer(Configuration parentContainer) throws SecurityException, ConfigurationException {
    try {
      parentContainer.getSubConfiguration(INSTANCES_CONTAINER);
    } catch (Exception e) {
      try {
        parentContainer.createSubConfiguration(INSTANCES_CONTAINER);
      } catch (StorageLockedException sle) {
        throw sle;
      } catch (StorageException se) {
        throw se;
      }
    }
    try {
      parentContainer.getSubConfiguration(PARENTSHIP_CONTAINER);
    } catch (Exception e) {
      try {
        parentContainer.createSubConfiguration(PARENTSHIP_CONTAINER);
      } catch (StorageLockedException sle) {
        throw sle;
      } catch (StorageException se) {
        throw se;
      }
    }

    try {
      parentContainer.getSubConfiguration(INSTANCES_CONTAINER).getSubConfiguration(ResourceHandle.ALL);
    } catch (Exception e) {
      try {
        parentContainer.getSubConfiguration(INSTANCES_CONTAINER).createSubConfiguration(ResourceHandle.ALL);
      } catch (StorageLockedException sle) {
        throw sle;
      } catch (StorageException se) {
        throw se;
      }
    }
  }

  public String[] getInstances(Configuration parentContainer) throws ConfigurationException, SecurityException {
    return parentContainer.getSubConfiguration(INSTANCES_CONTAINER).getAllSubConfigurationNames();
  }

  public String getParent(String instance, Configuration parentContainer) throws ConfigurationException, SecurityException {
    Configuration configuration = parentContainer.getSubConfiguration(PARENTSHIP_CONTAINER);
    return (String) configuration.getConfigEntry(instance);
  }

  public String[] getChildren(String instance, Configuration parentContainer) throws ConfigurationException, SecurityException {
    return parentContainer.getSubConfiguration(INSTANCES_CONTAINER).getSubConfiguration(instance).getAllConfigEntryNames();
  }

  public void createInstance(String alias, Configuration parentContainer) throws ConfigurationException, SecurityException {
    parentContainer.getSubConfiguration(INSTANCES_CONTAINER).createSubConfiguration(alias);
    parentContainer.getSubConfiguration(PARENTSHIP_CONTAINER).addConfigEntry(alias, ResourceHandle.ALL);
    parentContainer.getSubConfiguration(INSTANCES_CONTAINER).getSubConfiguration(ResourceHandle.ALL).addConfigEntry(alias, "");
  }

  public void removeInstance(String alias, Configuration parentContainer) throws ConfigurationException, SecurityException {
    String parent = (String) parentContainer.getSubConfiguration(PARENTSHIP_CONTAINER).getConfigEntry(alias);

    parentContainer.getSubConfiguration(INSTANCES_CONTAINER).getSubConfiguration(parent).deleteConfigEntry(alias);
    parentContainer.getSubConfiguration(PARENTSHIP_CONTAINER).deleteConfigEntry(alias);
    parentContainer.getSubConfiguration(INSTANCES_CONTAINER).deleteConfiguration(alias);
  }

  public void group(String alias, String newParent, Configuration parentContainer) throws ConfigurationException, SecurityException {
    String parent = (String) parentContainer.getSubConfiguration(PARENTSHIP_CONTAINER).getConfigEntry(alias);

    if (!newParent.equals(parent)) {
      parentContainer.getSubConfiguration(INSTANCES_CONTAINER).getSubConfiguration(parent).deleteConfigEntry(alias);
      parentContainer.getSubConfiguration(INSTANCES_CONTAINER).getSubConfiguration(newParent).addConfigEntry(alias, "");
      parentContainer.getSubConfiguration(PARENTSHIP_CONTAINER).modifyConfigEntry(alias, newParent);
    }
  }

  public void ungroup(String alias, Configuration parentContainer) throws ConfigurationException, SecurityException {
    group(alias, ResourceHandle.ALL, parentContainer);
  }

}