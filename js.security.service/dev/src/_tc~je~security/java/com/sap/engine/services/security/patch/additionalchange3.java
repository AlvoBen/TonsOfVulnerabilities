package com.sap.engine.services.security.patch;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.engine.services.security.server.AuthenticationContextImpl;

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
public class AdditionalChange3 implements Change {
  public void run() throws Exception {
    ConfigurationHandler configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
    addQueueAndResumePolicyConfiguration(configHandler);
  }

  private void addQueueAndResumePolicyConfiguration(ConfigurationHandler configHandler) throws Exception {
    try {
      Configuration securityConfigurations = configHandler.openConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, ConfigurationHandler.WRITE_ACCESS);
      
      if (securityConfigurations != null) {
        if (!securityConfigurations.existsConfigEntry(AuthenticationContextImpl.QUEUE_AND_RESUME)) {
          securityConfigurations.addConfigEntry(AuthenticationContextImpl.QUEUE_AND_RESUME, SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH + "/" + AuthenticationContextImpl.QUEUE_AND_RESUME);
        } else {
          securityConfigurations.modifyConfigEntry(AuthenticationContextImpl.QUEUE_AND_RESUME, SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH + "/" + AuthenticationContextImpl.QUEUE_AND_RESUME);
        }

        if (securityConfigurations.existsSubConfiguration(AuthenticationContextImpl.QUEUE_AND_RESUME)) {
          securityConfigurations.deleteConfiguration(AuthenticationContextImpl.QUEUE_AND_RESUME);
        }

        Configuration template = securityConfigurations.createSubConfiguration(AuthenticationContextImpl.QUEUE_AND_RESUME);
        Configuration security = template.createSubConfiguration("security");
        security.addConfigEntry("type", "5");
        Configuration authentication = security.createSubConfiguration("authentication");
        Configuration UME = authentication.createSubConfiguration("UME User Store");
        UME.addConfigEntry("size", new Integer(1));
        Configuration stack = UME.createSubConfiguration("0");
        stack.addConfigEntry("classname", "com.sap.security.core.server.wssec.jaas.ResumeAsLoginModule");
        stack.addConfigEntry("flag", "REQUIRED");
        stack.createSubConfiguration("options");
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
