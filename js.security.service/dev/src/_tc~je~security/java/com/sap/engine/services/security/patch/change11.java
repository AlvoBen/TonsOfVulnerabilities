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

import com.sap.engine.frame.core.configuration.*;
import com.sap.engine.interfaces.security.JACCSecurityRoleMappingContext;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.security.api.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Rumen Barov i033802
 * Date: Sep 23, 2005
 * Time: 21:14:58 PM
 */
public class Change11 implements Change {

  public static final String ACTIVE = "active_userstore";
  public static final String ROLE = "SAP_JAVA_SUPPORT";

  /**
   * 
   * @param sRoleName
   * @return true if role created successfully
   * @throws UMException
   */
  public boolean createEmptyRoleUME( String sRoleName ) throws UMException{
    IRole role;
    IRoleFactory roleFactory = UMFactory.getRoleFactory();

    try {
      role = roleFactory.getRoleByUniqueName( sRoleName );
      //role already exists
      return false;
    } catch ( NoSuchRoleException e ) {
      //$JL-EXC$
    }
    role = roleFactory.newRole( sRoleName );
    role.commit();
    return true;
  }

  protected Configuration openRole( ConfigurationHandler configHandler, String sRoleName, int iAccess, boolean bCreateIfNotExist )
      throws ConfigurationException{
    Configuration configWrite = configHandler.openConfiguration( SecurityConfigurationPath.USERSTORES_PATH, iAccess );
    String activeUserStoreName = null;
    if ( null != configWrite ) {
      if ( configWrite.existsConfigEntry( ACTIVE ) ) {
        activeUserStoreName = (String) configWrite.getConfigEntry( ACTIVE );
      }
    }

    if ( activeUserStoreName == null || activeUserStoreName.trim().length() == 0 ) {
      return null;
    }

    configHandler.closeAllConfigurations();

    Configuration roles = configHandler.openConfiguration( SecurityConfigurationPath.ROLES_PATH,
                                                           iAccess );
    if ( activeUserStoreName == null || activeUserStoreName.trim().length() == 0 ) {
      return null;
    }

    Configuration activeRoles = roles.getSubConfiguration( activeUserStoreName );

    Configuration theRole = null;
    if ( null != activeRoles ) {
      if ( !activeRoles.existsSubConfiguration( sRoleName ) ) {
        if ( bCreateIfNotExist && ( ConfigurationHandler.WRITE_ACCESS == iAccess ) ) {
          theRole = activeRoles.createSubConfiguration( sRoleName );
        }
      } else {
        theRole = activeRoles.getSubConfiguration( sRoleName );
      }
    }
    return theRole;
  }


  /**
   * 
   * @param sName
   * @return true if role created successfully.
   * @throws ConfigurationException
   */
  public boolean createEmptyRole( String sName ) throws ConfigurationException{
    ConfigurationHandler configHandler = null;
    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();

    try {
      Configuration theRole = openRole( configHandler, sName, ConfigurationHandler.WRITE_ACCESS, true );
      if ( null != theRole ) {
        if ( !theRole.existsSubConfiguration( "groups" ) ) {
          theRole.createSubConfiguration( "groups" );
        }
        if ( !theRole.existsSubConfiguration( "users" ) ) {
          theRole.createSubConfiguration( "users" );
        }
        if ( !theRole.existsConfigEntry( "description" ) ){
          theRole.addConfigEntry( "description", "" );
        }
      }
      configHandler.commit();
    } catch ( ConfigurationException e ) {
      configHandler.rollback();
      throw e;
    }
    finally {
      try {
        configHandler.closeAllConfigurations();
      } catch ( ConfigurationException e ) {
        throw e;
      }
    }
    return true;
  }


  protected String[] getKeys( Configuration config, boolean user ){
    Set users = null;
    try {
      config = config.getSubConfiguration( ( ( user ) ?"users" :"groups" ) );
      users = config.getAllConfigEntries().keySet();
    } catch ( ConfigurationException ce ) {
      // $JL-EXC$ 
      //no keys
      return null;
    }

    String[] usersArr = new String[users.size()];
    Iterator iter = users.iterator();
    int i = 0;
    while ( iter.hasNext() ) {
      usersArr[i++] = (String) iter.next();
    }
    return usersArr;
  }

  public boolean migrateServerRole( String sRoleName ) throws ConfigurationException{
    JACCSecurityRoleMappingContext mapper = null;
    try {
      mapper = (JACCSecurityRoleMappingContext) Class.forName( "com.sap.security.core.server.ume.service.jacc.JACCSecurityRoleMapperContextImpl" )
          .newInstance();
    } catch ( InstantiationException eInst ) {
      throw new SecurityException( eInst.getMessage() );
    } catch ( IllegalAccessException illegalAccess ) {
      throw new SecurityException( illegalAccess.getMessage() );
    } catch ( NoClassDefFoundError noClassErr ) {
      throw new SecurityException( noClassErr.getMessage() );
    } catch ( ClassNotFoundException ex ) {
      throw new SecurityException( ex.getMessage() );
    }

    ConfigurationHandler configHandler = null;
    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();

    try {
      Configuration theRole = openRole( configHandler, sRoleName, ConfigurationHandler.READ_ACCESS, false );
      if ( null != theRole ) {
        String[] users  = getKeys( theRole, true );
        String[] groups = getKeys( theRole, false );
        mapper.addUsersAndGroupsToJACCRole( sRoleName, null, users, groups );
      } else {
        return false;
      }
    } catch ( ConfigurationException e ) {
      configHandler.rollback();
      throw e;
    }
    finally {
      try {
        configHandler.closeAllConfigurations();
      } catch ( ConfigurationException e ) {
        throw e;
      }
    }
    return true;
  }


  public void changeLoginModuleClassName( ConfigurationHandler configHandler, String sDisplayName, String sClassName )
      throws Exception{

    try {
      Configuration configWrite = configHandler.openConfiguration( SecurityConfigurationPath.USERSTORES_PATH,
                                                                   ConfigurationHandler.WRITE_ACCESS );

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

        if ( loginModules.existsSubConfiguration( sDisplayName ) ) {
          Configuration theModule = loginModules.getSubConfiguration( sDisplayName );
          if ( theModule.existsConfigEntry( "class-name" ) ) {
            theModule.modifyConfigEntry( "class-name", sClassName );
          } else {
            theModule.addConfigEntry( "class-name", sClassName );
          }
        }//if sDisplayName exists
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

  /**
   * Checks the level of existence of a login module
   *
   * @param   sDisplayName The name of the "dispaly-name" proerty of the configuration entry
   * @return  level of existence
   *         0 - not found
   *         1 - a login module with same "display-name" found
   *         2 - a login module with same "display-name" and "class-name" found  
   */
  public int checkLoginModuleExistence( String sDisplayName, String sClassName ) throws Exception{
    int res = 0;
    ConfigurationHandler configHandler = null;
    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();

    try {
      Configuration config = configHandler.openConfiguration( SecurityConfigurationPath.USERSTORES_PATH,
                                                              ConfigurationHandler.READ_ACCESS );

      if ( config == null ) {
        return res;
      }

      String activeUserStoreName = null;

      if ( config.existsConfigEntry( ACTIVE ) ) {
        activeUserStoreName = (String) config.getConfigEntry( ACTIVE );
      }

      if ( activeUserStoreName == null || activeUserStoreName.trim().length() == 0 ) {
        return res;
      }

      Configuration activeUserStoreConf = config.getSubConfiguration( activeUserStoreName );

      if ( activeUserStoreConf.existsSubConfiguration( "login-module" ) ) {
        Configuration loginModules = activeUserStoreConf.getSubConfiguration( "login-module" );

        if ( !loginModules.existsSubConfiguration( sDisplayName ) ) {
          return res;
        } else {
          res = 1;
        }

        //check display-name
        Configuration theModule = loginModules.getSubConfiguration( sDisplayName );
        Object name = null;
        //check class-name if display-name exists
        if ( 1 == res ) {
          try {
            name = theModule.getConfigEntry( "class-name" );
            if ( 0 == sClassName.compareTo( name.toString() ) )
              res = 2;
          } catch ( NameNotFoundException nnfe ) {
            return res;
          }
        }//if display-name exists
      }//if exist active userstore conf
    } catch ( ConfigurationException e ) {
      configHandler.rollback();
      return 0;
    }
    finally {
      try {
        configHandler.closeAllConfigurations();
      } catch ( Exception e ) {
        throw e;
      }
    }
    return res;
  }

  /**
   * Adds a login module configuration. If configuration exists adds this module with different "display-name" name
   *
   * @see com.sap.engine.services.security.patch.Change11, method checkLoginModuleExistence(ConfigurationHandler configHandler, String sDisplayName, String sClassName )   
   * @see com.sap.engine.services.security.patch.Change6, method addActiveUserstoreConfiguration   
   */
  public void carefullyAddLoginModule( String sDisplayName,
                                      String sClassName,
                                      String sDescription,
                                      Map configentries,
                                      Map options,
                                      String[] suitableMechanisms,
                                      String[] notSuitableMechanisms ) throws Exception{

    int existence = checkLoginModuleExistence( sDisplayName, sClassName );

    String add = "";
    int i = 0;
    while ( 1 == existence ) {//same display-name, different class-name
      add = "" + i;
      existence = checkLoginModuleExistence( sDisplayName + add, sClassName );
      i++;
    }
    sDisplayName = sDisplayName + add;

    if ( 2 == existence )
      return;//module already exists. do nothing.


    ConfigurationHandler configHandler = null;
    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
    addActiveUserstoreConfiguration( configHandler,
                                     sDisplayName,
                                     sClassName,
                                     sDescription,
                                     configentries,
                                     options,
                                     suitableMechanisms,
                                     notSuitableMechanisms );
  }

  /**
   * Adds a login module configuration. If configuration exists change its class-name only
   *
   * @see com.sap.engine.services.security.patch.Change11, method checkLoginModuleExistence(ConfigurationHandler configHandler, String sDisplayName, String sClassName )   
   * @see com.sap.engine.services.security.patch.Change6, method addActiveUserstoreConfiguration   
   */
  public void rudeAddLoginModule( String sDisplayName,
                                 String sClassName,
                                 String sDescription,
                                 Map configentries,
                                 Map options,
                                 String[] suitableMechanisms,
                                 String[] notSuitableMechanisms ) throws Exception{

    int existence = checkLoginModuleExistence( sDisplayName, sClassName );

    if ( 2 == existence )
      return;//module already exists. do nothing.


    ConfigurationHandler configHandler = null;
    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();

    if ( 0 == existence ) {
      addActiveUserstoreConfiguration( configHandler,
                                       sDisplayName,
                                       sClassName,
                                       sDescription,
                                       configentries,
                                       options,
                                       suitableMechanisms,
                                       notSuitableMechanisms );
    }
    if ( 1 == existence ) {
      changeLoginModuleClassName( configHandler, sDisplayName, sClassName );
    }
  }

  static void addActiveUserstoreConfiguration( ConfigurationHandler configHandler,
                                              String displayName,
                                              String className,
                                              String description,
                                              Map configentries,
                                              Map options,
                                              String[] suitableMechanisms,
                                              String[] notSuitableMechanisms ) throws Exception{

    try {
      Configuration configWrite = configHandler.openConfiguration( SecurityConfigurationPath.USERSTORES_PATH,
                                                                   ConfigurationHandler.WRITE_ACCESS );

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

        if ( !loginModules.existsSubConfiguration( displayName ) ) {
          Configuration newLoginModuleConf = loginModules.createSubConfiguration( displayName );

          String[] subConfigs = new String[] { "not-suitable-mechanisms", "options", "suitable-mechanisms" };

          Map map = newLoginModuleConf.createSubConfigurations( subConfigs );
          newLoginModuleConf.addConfigEntry( "class-name", className );
          newLoginModuleConf.addConfigEntry( "description", description );

          if ( configentries != null && !configentries.isEmpty() ) {
            newLoginModuleConf.addConfigEntries( configentries );
          }

          if ( options != null && !options.isEmpty() ) {
            Configuration optionsConf = (Configuration) map.get( "options" );
            optionsConf.addConfigEntries( options );
          }

          if ( suitableMechanisms != null && suitableMechanisms.length > 0 ) {
            Configuration suitableMechanismsConf = (Configuration) map.get( "suitable-mechanisms" );
            Object value = new byte[0];

            for ( int i = 0; i < suitableMechanisms.length; i++ ) {
              suitableMechanismsConf.addConfigEntry( suitableMechanisms[i], value );
            }
          }

          if ( notSuitableMechanisms != null && notSuitableMechanisms.length > 0 ) {
            Configuration notSuitableMechanismsConf = (Configuration) map.get( "not-suitable-mechanisms" );
            Object value = new byte[0];

            for ( int i = 0; i < notSuitableMechanisms.length; i++ ) {
              notSuitableMechanismsConf.addConfigEntry( notSuitableMechanisms[i], value );
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

  public void run() throws Exception{


    this.createEmptyRole( ROLE );
    if ( this.createEmptyRoleUME( ROLE ) ) {
      migrateServerRole( ROLE );

    }

    //    <display-name>SPNegoLoginModule</display-name>
    //    <description>Login module for authentication via SPNego protocol.</description>
    //    <class-name>....SPNegoLoginModule</class-name>
    rudeAddLoginModule( "SPNegoLoginModule",
                        "com.sap.security.core.server.jaas.SPNegoLoginModule",
                        "Login module for authentication via SPNego protocol.",
                        null,
                        null,
                        null,
                        null );

    //    <display-name>AnonymousLoginModule</display-name>
    //    <description>Login module for named anonymous users.</description>
    //    <class-name>com.sap.security.core.server.jaas.AnonymousLoginModule</class-name>
    rudeAddLoginModule( "AnonymousLoginModule",
                        "com.sap.security.core.server.jaas.AnonymousLoginModule",
                        "Login module for named anonymous users.",
                        null,
                        null,
                        null,
                        null );

    //    <display-name>CertPersisterLoginModule</display-name>
    //    <description>Login module for mapping a client certificate to a user.</description>
    //    <class-name>com.sap.security.core.server.jaas.CertPersisterLoginModule</class-name>
    rudeAddLoginModule( "CertPersisterLoginModule",
                        "com.sap.security.core.server.jaas.CertPersisterLoginModule",
                        "Login module for mapping a client certificate to a user.",
                        null,
                        null,
                        null,
                        null );

    //    <display-name>QueueAndResumeLoginModule</display-name>
    //    <description>Login module to resume a login for a queued message.</description>
    //    <class-name>com.sap.security.core.server.wssec.jaas.ResumeAsLoginModule</class-name>
    //    <options-editor>com.sap.engine.services.security.gui.userstore.stores.EmptyOptionsEditor</options-editor> 
    Map editor = new HashMap();
    editor.put( "options-editor", "com.sap.engine.services.security.gui.userstore.stores.EmptyOptionsEditor" );
    rudeAddLoginModule( "QueueAndResumeLoginModule",
                        "com.sap.security.core.server.wssec.jaas.ResumeAsLoginModule",
                        "Login module to resume a login for a queued message.",
                        editor,
                        null,
                        null,
                        null );
  }
}