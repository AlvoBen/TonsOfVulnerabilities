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
package com.sap.engine.services.security.resource.action;

import com.sap.engine.services.security.exceptions.StorageException;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.interfaces.security.resource.ResourceHandle;

public class ActionManager {

  public static final String CONTAINER = "action";

  public void prepareContainer(Configuration parentContainer) throws SecurityException, ConfigurationException {
    try {
      parentContainer.getSubConfiguration(CONTAINER);
    } catch (Exception e) {
      try {
        parentContainer.createSubConfiguration(CONTAINER);

        Configuration configuration = parentContainer.getSubConfiguration(CONTAINER);
        try {
          if (!configuration.existsSubConfiguration(ResourceHandle.ALL)) {
            configuration.createSubConfiguration(ResourceHandle.ALL);
          }
        } catch (ConfigurationException e1) {
          throw new StorageException("Unexpected exception during initialization of resource actions configuration", e1);
        }

      } catch (StorageLockedException sle) {
        throw sle;
      } catch (StorageException se) {
        throw se;
      }
    }
  }

  /**
   *  Creates action with given name and id.
   *
   * @param   alias            name of the action
   * @param   parentContainer  parent container
   *
   * @exception   SecurityException  thrown when database errors occur
   */
  public void createAction(String alias, Configuration parentContainer) throws SecurityException {
    try {
      Configuration configuration = parentContainer.getSubConfiguration(CONTAINER);

      if (configuration.existsSubConfiguration(alias)) {
        throw new SecurityException("Action with alias [" + alias + "] already exists.");
      }

      configuration.createSubConfiguration(alias);
    } catch (StorageLockedException sle) {
      throw sle;
    } catch (StorageException se) {
      throw se;
    } catch (ConfigurationLockedException se) {
      throw new StorageLockedException("Cannot create action [" + alias + "]: storage locked.", se);
    } catch (ConfigurationException se) {
      throw new StorageException("Cannot create action [" + alias + "].", se);
    }
  }

  /**
   *  Removes action with given name and id.
   *
   * @param   alias            name of the action
   * @param   parentContainer  parent container
   *
   * @exception   SecurityException  thrown when database errors occur
   */
  public void removeAction(String alias, Configuration parentContainer) throws SecurityException {
    try {
      Configuration configuration = parentContainer.getSubConfiguration(CONTAINER);

      if (!configuration.existsSubConfiguration(alias)) {
        throw new SecurityException("Action with alias [" + alias + "] does not exist.");
      }

      configuration.deleteConfiguration(alias);
    } catch (StorageLockedException sle) {
      throw sle;
    } catch (StorageException se) {
      throw se;
    } catch (ConfigurationLockedException se) {
      throw new StorageLockedException("Cannot remove action [" + alias + "]: storage locked.", se);
    } catch (ConfigurationException se) {
      throw new StorageException("Cannot remove action [" + alias + "].", se);

    }
  }

  /**
   *  Gets all available actions for this ResourceHandle
   *
   * @return  names of all actions
   * @exception   SecurityException throw when dbms error occurs
   */
  public String[] getActions(Configuration parentContainer) throws SecurityException {
    try {
      return parentContainer.getSubConfiguration(CONTAINER).getAllSubConfigurationNames();
    } catch (StorageException se) {
      throw se;
    } catch (ConfigurationLockedException se) {
      throw new StorageLockedException("Cannot get actions: storage locked.", se);
    } catch (ConfigurationException se) {
      throw new StorageException("Cannot get actions.", se);

    }
  }

}

