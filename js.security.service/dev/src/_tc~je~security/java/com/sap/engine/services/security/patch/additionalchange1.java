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
package com.sap.engine.services.security.patch;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.security.server.SecurityConfigurationPath;

public class AdditionalChange1 implements Change {

  public void run() throws Exception {
    ConfigurationHandler configHandler = null;
    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
    removeEmptyOptionsEditor(configHandler);
  }

  private void removeEmptyOptionsEditor(ConfigurationHandler configHandler) throws Exception {
    try {
      Configuration configWrite = configHandler.openConfiguration(SecurityConfigurationPath.USERSTORES_PATH, ConfigurationHandler.WRITE_ACCESS);
      if (configWrite != null) {
        Configuration loginModule = configWrite.getSubConfiguration("UME User Store/login-module/BasicPasswordLoginModule");

        if (loginModule.existsConfigEntry("options-editor")) {
          loginModule.deleteConfigEntry("options-editor");
        }
      }

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

}