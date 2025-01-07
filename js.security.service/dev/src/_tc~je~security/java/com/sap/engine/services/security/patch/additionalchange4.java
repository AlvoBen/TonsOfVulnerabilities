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

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.services.security.server.SecurityConfigurationPath;

/**
 * @author Rumen Barov i033802
 * @Date: Oct 21, 2005
 * Time: 7:22:16 PM
 */

//fix the AdditionalChange2 bug
public class AdditionalChange4 implements Change {

  public static final String ACTIVE = "active_userstore";

  public void run() throws Exception{
    
    AdditionalChange2 adc2 = new AdditionalChange2();
    adc2.rudeAddLoginModule( "SPNegoLoginModule",
                             "com.sap.security.core.server.jaas.SPNegoLoginModule",
                             "Login module for authentication via SPNego protocol.",
                             null,
                             null,
                             null );

    
    
    
    
    

    ConfigurationHandler configHandler = null;
    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();

    Configuration configWrite = configHandler.openConfiguration( SecurityConfigurationPath.USERSTORES_PATH,
                                                                 ConfigurationHandler.WRITE_ACCESS );

    try {
      if ( configWrite == null ) {
        return;
      }

      String activeUserStoreName = null;
      if ( configWrite.existsConfigEntry( ACTIVE ) ) {
        activeUserStoreName = (String) configWrite.getConfigEntry( ACTIVE );
      }

      if ( activeUserStoreName == null || activeUserStoreName.trim().length() == 0 ) {
        return;
      }

      Configuration activeUserStoreConf = configWrite.getSubConfiguration( activeUserStoreName );
      if ( activeUserStoreConf.existsSubConfiguration( "login-module" ) ) {
        Configuration loginModules = activeUserStoreConf.getSubConfiguration( "login-module" );

        if ( loginModules.existsSubConfiguration( "QueueAndResumeLoginModule" ) ) {
          Configuration theModule = loginModules.getSubConfiguration( "QueueAndResumeLoginModule" );

          if ( theModule.existsSubConfiguration( "options" ) ) {
            //delete the wrong one
            Configuration optionsConf = theModule.getSubConfiguration( "options" );

            if ( optionsConf.existsConfigEntry( "options-editor" ) ) {
              if ( 0 == "com.sap.engine.services.security.gui.userstore.stores.EmptyOptionsEditor".compareTo((String) optionsConf.getConfigEntry( "options-editor" ) ) ) {
                optionsConf.deleteConfigEntry( "options-editor" );
              }
            }
            //set the correct one
            try {
              theModule.addConfigEntry( "options-editor",
                                        "com.sap.engine.services.security.gui.userstore.stores.EmptyOptionsEditor" );
            } catch ( NameAlreadyExistsException e ) {
              //$JL-EXC$
            }
          }
        }
      }

      configHandler.commit();
    } catch ( Exception e ) {
      try {
        configHandler.rollback();
      } catch ( Exception re ) {
        throw re;
      }
      throw e;
    }
    finally {
      try {
        configHandler.closeAllConfigurations();
      } catch ( Exception e ) {
        throw e;
      }
    }
  }
}