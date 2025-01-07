/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.patch;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.NoWriteAccessException;
import com.sap.engine.services.security.server.SecurityConfigurationPath;

/**
 * Removes the global properties for login, error, password change and password
 * change error pages.
 * 
 * @author Krasimira Velikova
 */
public class Change9 implements Change {
  
  /* (non-Javadoc)
   * @see com.sap.engine.services.security.patch.Change#run()
   */
  public void run() throws Exception {
    ConfigurationHandler configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
    
    try {
      Configuration conf = configHandler.openConfiguration(
          SecurityConfigurationPath.AUTHENTICATION_PATH, 
          ConfigurationHandler.WRITE_ACCESS);

      remove(conf, "global_password_change-error-page");
      remove(conf, "global_password_change_login_page");
      remove(conf, "global_form_error_page");
      remove(conf, "global_form_login_page");
      
      configHandler.commit();
      
    } catch (Exception e) {
      try {
        configHandler.rollback();
      } catch (Exception re) {
        throw re;
      }
      throw e;
      
    } finally {
      try {
        configHandler.closeAllConfigurations();
      } catch (Exception e) {
        throw e;
      }
    }
  }
  
  private void remove(Configuration conf, String name) throws InconsistentReadException, NameNotFoundException, NoWriteAccessException, ConfigurationException {
    if (conf.existsConfigEntry(name)) {
      conf.deleteConfigEntry(name);
    }
  }
}
