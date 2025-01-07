/**
 * Copyright (c) 2007 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 *
 */

/*
 * Created on 2007-5-4 by I027020
 */
 
package com.sap.engine.services.security.server.deploy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.security.auth.login.AppConfigurationEntry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.RuntimeLoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.RuntimeUserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.server.AuthenticationContextImpl;
import com.sap.engine.services.security.server.ConfigurationLock;
import com.sap.engine.services.security.server.ModificationContextImpl;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.engine.services.security.userstore.descriptor.LoginModuleConfigurationImpl;
import com.sap.engine.services.security.userstore.descriptor.UserStoreConfigurationImpl;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @author Ralin Chimev
 *
 * @
 */
public class DeployUtil {

  private static final String LOGIN_MODULE_CONTAINER_SUBCONFIGURATION_DATA = "data";
  private static final String LOGIN_MODULE_ELEMENT_NAME = "login-module";
  private static final String DISPLAY_NAME_ELEMENT_NAME = "display-name";
  private static final String CLASS_NAME_ELEMENT_NAME = "class-name";
  private static final String DESCRIPTION_ELEMENT_NAME = "description";
  private static final String OPTIONS_ELEMENT_NAME = "options";
  private static final String OPTION_ELEMENT_NAME = "option";
  private static final String OPTION_NAME_ELEMENT_NAME = "name";
  private static final String OPTION_VALUE_ELEMENT_NAME = "value";
  
  
  private static final Location LOCATION = Location.getLocation(LoginModuleContainer.LOGIN_MODULE_CONTAINER_LOCATION);
  private static final Category CATEGORY = Category.getCategory(Category.SYS_SECURITY, LoginModuleContainer.LOGIN_MODULE_CONTAINER_CATEGORY);
  
  //	=== check methods ===
  
  public static boolean checkLoginModuleConfigurationXmlFile(File[] files) {
    
    final String METHOD_NAME = "checkLoginModuleConfigurationXmlFile";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {files});
    }
    
    int count = 0;
    for ( int i = 0 ; i < files.length ; i++ ) {
      String fileName = files[i].getName();
      if (fileName.equalsIgnoreCase(LoginModuleContainer.CONFIGURATION_XML_FILE)) {
        count++;        
        if (count > 1) {
          SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000155", "Only one configuration XML file [{0}] can be used in login module deployment. Please merge the XML files describing login module configurations into one.", new Object[]{fileName});
  	      if (LOCATION.bePath()) {
            LOCATION.exiting(METHOD_NAME, new Boolean(false));
          }
          return false;
        }
      }
    }    
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, new Boolean(count == 1));
    }
    return count == 1;
  }
  
  public static boolean checkClassNameUniqueInXml(LoginModuleConfiguration[] loginModuleConfigurations) {    
    
    final String METHOD_NAME = "checkClassNameUniqueInXml";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {loginModuleConfigurations});
    }
    
    Set setClassNames = new HashSet();
    for ( int i = 0 ; i < loginModuleConfigurations.length ; i++ ) {
      LoginModuleConfiguration lmc = loginModuleConfigurations[i];
      String className = lmc.getLoginModuleClassName();
      if (setClassNames.contains(className)) {
        SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000153", "Login module class name {0} is not unique in configuration XML file. All class names in configuration XML file must be unique.", new Object[] {className});
        if (LOCATION.bePath()) {
          LOCATION.exiting(METHOD_NAME, new Boolean(false));
        }
	    return false;
      } else {
        setClassNames.add(className);
      }
    } 
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, new Boolean(true));
    }
    return true;
  }
  
  public static boolean checkDisplayNameUniqueInXml(LoginModuleConfiguration[] loginModuleConfigurations) {    
    
    final String METHOD_NAME = "checkDisplayNameUniqueInXml";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {loginModuleConfigurations});
    }
    
    Set setDisplayNames = new HashSet();
    for ( int i = 0 ; i < loginModuleConfigurations.length ; i++ ) {
      LoginModuleConfiguration lmc = loginModuleConfigurations[i];
      String displayName = lmc.getName();
      if (setDisplayNames.contains(displayName)) {
        SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000154", "Login module display name {0} is not unique in configuration xml file. All display names in configuration xml file must be unique.", new Object[] {displayName});
        if (LOCATION.bePath()) {
          LOCATION.exiting(METHOD_NAME, new Boolean(false));
        }
	    return false;
      } else {
        setDisplayNames.add(displayName);
      }
    } 
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, new Boolean(true));
    }
    return true;
  }
  
  public static boolean checkClassNameEqualDisplayName(LoginModuleConfiguration[] loginModuleConfigurations) {
    
    final String METHOD_NAME = "checkClassNameEqualDisplayName";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {loginModuleConfigurations});
    }
    
    Set setDisplayNames = new HashSet();
    for ( int i = 0 ; i < loginModuleConfigurations.length ; i++ ) {
      LoginModuleConfiguration lmc = loginModuleConfigurations[i];
      String displayName = lmc.getName();
      setDisplayNames.add(displayName);
    }
    
    LoginModuleConfiguration[] lmcUS = getLoginModuleConfigurations();
    for ( int i = 0 ; i < lmcUS.length ; i++ ) {
      LoginModuleConfiguration lmc = lmcUS[i];
      String displayName = lmc.getName();
      if (!setDisplayNames.contains(displayName)) {
        setDisplayNames.add(displayName);
      }
    }
    
    for ( int i = 0 ; i < loginModuleConfigurations.length ; i++ ) {
      LoginModuleConfiguration lmc = loginModuleConfigurations[i];
      String className = lmc.getLoginModuleClassName();
      if (setDisplayNames.contains(className)) {
        SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000156", "Login module class name {0} matches display name of another login module in configuration xml or userstore.", new Object[] {className});
        if (LOCATION.bePath()) {
          LOCATION.exiting(METHOD_NAME, new Boolean(false));
        }
        return false;
      }
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, new Boolean(true));
    }
    return true;
  }
  
  //	=== deploy check methods ===
  
  public static boolean checkDisplayNameInUserstore(LoginModuleConfiguration[] loginModuleConfigurations) {
    
    final String METHOD_NAME = "checkDisplayNameInUserstore";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {loginModuleConfigurations});
    }
    
    Set setDisplayNames = new HashSet();
    LoginModuleConfiguration[] lmcUS = getLoginModuleConfigurations();
    for ( int i = 0 ; i < lmcUS.length ; i++ ) {
      LoginModuleConfiguration lmc = lmcUS[i];
      String displayName = lmc.getName();
      setDisplayNames.add(displayName);
    }
    
    for ( int i = 0 ; i < loginModuleConfigurations.length ; i++ ) {
      LoginModuleConfiguration lmc = loginModuleConfigurations[i];
      String displayName = lmc.getName();
      if (setDisplayNames.contains(displayName)) {
        SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000157", "Login module display name exists in userstore. Login module cannot be deployed with that display name [{0}].", new Object[] {displayName});
        if (LOCATION.bePath()) {
          LOCATION.exiting(METHOD_NAME, new Boolean(false));
        }
        return false;
      }
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, new Boolean(true));
    }
    return true;
  }
  
  public static boolean checkClassNameInUserstore(LoginModuleConfiguration[] lmcNew) {
    
    final String METHOD_NAME = "checkClassNameInUserstore";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {lmcNew});
    }
    
    Set setClassNames = new HashSet();
    LoginModuleConfiguration[] lmcUS = getLoginModuleConfigurations();
    for ( int i = 0 ; i < lmcUS.length ; i++ ) {
      LoginModuleConfiguration lmc = lmcUS[i];
      String className = lmc.getLoginModuleClassName();
      if (!setClassNames.contains(className)) {
        setClassNames.add(className);
      }
    }
    
    for ( int i = 0 ; i < lmcNew.length ; i++ ) {
      LoginModuleConfiguration lmc = lmcNew[i];
      String className = lmc.getLoginModuleClassName();
      if (setClassNames.contains(className)) {
        SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000158", "Login module class name exists in userstore. Login module cannot be deployed with that class name [{0}].", new Object[] {className});
        if (LOCATION.bePath()) {
          LOCATION.exiting(METHOD_NAME, new Boolean(false));
        }
        return false;
      }
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, new Boolean(true));
    }
    return true;
  }
  
  //	=== update check methods ===
  
  /*
   * Checks whether a login module from the new xml points to more than one login module from old xml on diplay name and class name.
   */
  public static boolean checkLoginModuleRelationsToPreviousXml(LoginModuleConfiguration[] oldLMC, LoginModuleConfiguration[] newLMC) {
    
    final String METHOD_NAME = "checkLoginModuleRelationsToPreviousXml";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {oldLMC, newLMC});
    }

    Map mapOldDNs = new HashMap(oldLMC.length);
    Map mapOldCNs = new HashMap(oldLMC.length);
    
    for ( int j = 0 ; j < oldLMC.length ; j++ ) {
      LoginModuleConfiguration oldModule = oldLMC[j];
      String oldClassName = oldModule.getLoginModuleClassName();
      String oldDisplayName = oldModule.getName();      
      mapOldDNs.put(oldDisplayName, oldModule);
      mapOldCNs.put(oldClassName, oldModule);
    }
    
    for ( int i = 0 ; i < newLMC.length ; i++ ) {
      LoginModuleConfiguration newModule = newLMC[i];
      String newClassName = newModule.getLoginModuleClassName();
      String newDisplayName = newModule.getName();
      
      Object lmc1 = mapOldDNs.get(newDisplayName);      
      Object lmc2 = mapOldCNs.get(newClassName);
      
      if (lmc1 != null && lmc2 != null && lmc1 != lmc2) {
        SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000159", "Login module from new configuration xml maps to more than one login module from previous xml based on display name and class name.\n" +
            "New login module display name [{0}] matches previous login module display name [{1}].\n" +
            "New login module class name [{2}] matches previous login module class name [{3}].\n", 
            new Object[] {newDisplayName, ((LoginModuleConfiguration)lmc1).getName(), newClassName, ((LoginModuleConfiguration)lmc2).getLoginModuleClassName()});
        
        if (LOCATION.bePath()) {
          LOCATION.exiting(METHOD_NAME, new Boolean(false));
        }
        return false;
      }
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, new Boolean(true));
    }
    return true;
  }
  
  public static boolean checkRenameDisplayName(LoginModuleConfiguration[] oldLMC, LoginModuleConfiguration[] newLMC) {
    
    final String METHOD_NAME = "checkRenameDisplayName";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {oldLMC, newLMC});
    }
    
    for ( int i = 0 ; i < oldLMC.length ; i++ ) {
      LoginModuleConfiguration oldModule = oldLMC[i];
      String oldClassName = oldModule.getLoginModuleClassName();
      String oldDisplayName = oldModule.getName();
      for ( int j = 0 ; j < newLMC.length ; j++ ) {
        LoginModuleConfiguration newModule = newLMC[j];
        String newClassName = newModule.getLoginModuleClassName();
        String newDisplayName = newModule.getName();
        if (newClassName.equals(oldClassName) && !newDisplayName.equals(oldDisplayName)) {
          SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000160", "Login module cannot be deployed with different display name than previous deployment. " +
              "Class name: [{0}], new display name: [{1}], old display name: [{2}].", 
              new Object[] {newClassName, newDisplayName, oldDisplayName});
          if (LOCATION.bePath()) {
            LOCATION.exiting(METHOD_NAME, new Boolean(false));
          }
          return false;
        }
      }
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, new Boolean(true));
    }
    return true;
  }
  
  public static boolean checkLoginModuleDisplayNameInUserstore(LoginModuleConfiguration lmcNew) {
    
    final String METHOD_NAME = "checkLoginModuleDisplayNameInUserstore";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {lmcNew});
    }
    
    LoginModuleConfiguration[] lmcUS = getLoginModuleConfigurations();
    for ( int i = 0 ; i < lmcUS.length ; i++ ) {
      LoginModuleConfiguration lmc = lmcUS[i];
      if (lmc.getName().equals(lmcNew.getName())) {
        if (LOCATION.bePath()) {
          LOCATION.exiting(METHOD_NAME, new Boolean(true));
        }
        return true;
      }
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, new Boolean(false));
    }
    return false;
  }
  
  public static LoginModuleConfiguration findLoginModuleInPrevious(String newDN, LoginModuleConfiguration[] lmcOld) {
    
    for ( int i = 0 ; i < lmcOld.length ; i++ ) {
      LoginModuleConfiguration lmc = lmcOld[i];
      if (lmc.getName().equals(newDN)) {
        return lmc;
      }
    }
    
    return null;
  }
  
  public static void updateClassName(String oldClassName, LoginModuleConfiguration lmc, Configuration lmConfig) throws ConfigurationException
  {
    final String METHOD_NAME = "updateClassName";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {oldClassName, lmc, lmConfig});
    }
    
    Set setDisplayNames = new HashSet();
    
    // update CN in app config
    String[] lmcNames = lmConfig.getAllSubConfigurationNames();
    LoginModuleConfiguration[] loginModules = new LoginModuleConfiguration[lmcNames.length];
    for ( int i = 0 ; i < lmcNames.length ; i++ ) {
      String lmcConfigName = lmcNames[i];
      Configuration lmcConfig = lmConfig.getSubConfiguration(lmcConfigName);
      String displayName = lmcConfigName;
      String className = (String) lmcConfig.getConfigEntry("class-name");
      if (className.equals(oldClassName)) {
        String newClassName = lmc.getLoginModuleClassName();
        lmcConfig.modifyConfigEntry("class-name", newClassName);
        setDisplayNames.add(displayName);        
        SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000083", "Login module in userstore with display name [{0}] updated class name from [{1}] to [{2}]", new Object[] {displayName, className, newClassName});
      }
    }
    
    // update CN in userstore
    LoginModuleConfiguration[] srcLMC = getLoginModuleConfigurations();
    RuntimeLoginModuleConfiguration[] dstLMC = new RuntimeLoginModuleConfiguration[srcLMC.length];
    for ( int i = 0 ; i < srcLMC.length ; i++ ) {
      LoginModuleConfiguration src = srcLMC[i];
      if (src.getLoginModuleClassName().equals(oldClassName) && !setDisplayNames.contains(src.getName())) {
        dstLMC[i] = makeRuntimeLoginModuleConfiguration(src.getName(), src.getDescription(), lmc.getLoginModuleClassName(), src.getOptions());
        SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000083", "Login module in userstore with display name [{0}] updated class name from [{1}] to [{2}]", new Object[] {src.getName(), src.getLoginModuleClassName(), lmc.getLoginModuleClassName()});
      } else {
        dstLMC[i] = makeRuntimeLoginModuleConfiguration(src.getName(), src.getDescription(), src.getLoginModuleClassName(), src.getOptions());
      }
    }
    saveLoginModulesToUserstore(dstLMC);
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    }
  }
  
  private static Map readLoginModuleConfigurationOptions(Configuration loginModuleConfiguration) throws ConfigurationException {
    Map options = new HashMap();
    if (loginModuleConfiguration.existsSubConfiguration("options")) {
      Configuration optionsConfiguration = loginModuleConfiguration.getSubConfiguration("options");
      String[] optionEntries = optionsConfiguration.getAllConfigEntryNames();
      for (int i = 0; i < optionEntries.length; i++) {
        String name = optionEntries[i];
        String value = (String) optionsConfiguration.getConfigEntry(name);
        options.put(name, value);
      }
    }
    return options;
  }
  
  public static void updateOptions(LoginModuleConfiguration lmc, Configuration lmConfig, Map orgOptions) throws ConfigurationException
  {
    final String METHOD_NAME = "updateOptions";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {lmc, lmConfig});
    }
    
    Set setDisplayNames = new HashSet();
    
    // update OP in app config
    String[] lmcNames = lmConfig.getAllSubConfigurationNames();
    LoginModuleConfiguration[] loginModules = new LoginModuleConfiguration[lmcNames.length];
    for ( int i = 0 ; i < lmcNames.length ; i++ ) {
      String lmcConfigName = lmcNames[i];
      Configuration lmcConfig = lmConfig.getSubConfiguration(lmcConfigName);
      String className = (String) lmcConfig.getConfigEntry("class-name");
      Map lmcOptions = readLoginModuleConfigurationOptions(lmcConfig);
      if (className.equals(lmc.getLoginModuleClassName()) && lmcOptions.equals(orgOptions)) {
        Configuration optionsConfig = createSubConfiguration(lmcConfig, "options");
    	optionsConfig.deleteAllConfigEntries();
    	Map options = lmc.getOptions();
    	Iterator enumeration = options.keySet().iterator();
    	while (enumeration.hasNext()) {
    	  String key = (String) enumeration.next();
    	  addConfigEntry(optionsConfig, key, options.get(key));
    	}
    	setDisplayNames.add(lmcConfigName);
    	SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000084", "Login module in userstore with display name [{0}] updated options to [{2}]", new Object[] {lmcConfigName, lmc.getOptions()});
      }
    }
    
    //  update OP in userstore
    LoginModuleConfiguration[] srcLMC = getLoginModuleConfigurations();
    RuntimeLoginModuleConfiguration[] dstLMC = new RuntimeLoginModuleConfiguration[srcLMC.length];
    for ( int i = 0 ; i < srcLMC.length ; i++ ) {
      LoginModuleConfiguration src = srcLMC[i];
      if (!setDisplayNames.contains(src.getName()) && src.getLoginModuleClassName().equals(lmc.getLoginModuleClassName()) && src.getOptions().equals(orgOptions)) {
        dstLMC[i] = makeRuntimeLoginModuleConfiguration(src.getName(), src.getDescription(), src.getLoginModuleClassName(), lmc.getOptions());
        SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000085", "Login module in userstore with display name [{0}] updated options from [{1}] to [{2}]", new Object[] {src.getName(), src.getOptions(), lmc.getOptions()});
      } else {
        dstLMC[i] = makeRuntimeLoginModuleConfiguration(src.getName(), src.getDescription(), src.getLoginModuleClassName(), src.getOptions());
      }
    }
    saveLoginModulesToUserstore(dstLMC);
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    }
  }
  
  public static void updateCNinPolicyConfigurations(LoginModuleConfiguration oldLoginModule, LoginModuleConfiguration newLoginModule) throws Exception {

    final String METHOD_NAME = "updateCNinPolicyConfigurations";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {oldLoginModule, newLoginModule});
    }
    
    InitialContext initialContext = new InitialContext();
    SecurityContext securityContext = (SecurityContext) ( initialContext.lookup( LoginModuleContainer.SECURITY_SERVICE ) );
    
    String newLoginModuleClassName = newLoginModule.getLoginModuleClassName();
    String newLoginModuleDisplayName = newLoginModule.getName();
    
    String oldLoginModuleClassName = oldLoginModule.getLoginModuleClassName();
    String oldLoginModuleDisplayName = oldLoginModule.getName();

    String[] policyConfigurationNames = securityContext.listPolicyConfigurations();
    
    for (int i = 0; i < policyConfigurationNames.length; i++) {

      SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext( policyConfigurationNames[i] );
      if (policyConfiguration == null) {
        continue;
      }
      AuthenticationContext authentication = policyConfiguration.getAuthenticationContext();

      // do not update if template is referenced
      String template = authentication.getTemplate();
      if (template != null && template.length() > 0) {
        continue;
      }

      boolean update = false;
      AppConfigurationEntry[] authStackEntries = authentication.getLoginModules();
      for (int entryIndex = 0; entryIndex < authStackEntries.length; entryIndex++) {
        String entryName = authStackEntries[entryIndex].getLoginModuleName();        
        if (oldLoginModuleClassName.equals( entryName )) {
          authStackEntries[entryIndex] = new AppConfigurationEntry(newLoginModuleClassName, authStackEntries[entryIndex].getControlFlag(), authStackEntries[entryIndex].getOptions() );
          update = true;
          
          SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000086", "Policy configuration [{0}] updated. Login module [{1}] with class name [{2}] updated to class name [{3}].", 
              new Object[] {authentication.getPolicyConfigurationName(), entryName, oldLoginModuleClassName, newLoginModuleClassName});
        } 
      }
      
      if (update) {
        if (authentication instanceof AuthenticationContextImpl) {
          ((AuthenticationContextImpl)authentication).setLoginModulesNoCheck(authStackEntries);
        } else {
          throw new Exception("Cannot set login modules to authentication stack: " + authentication.getPolicyConfigurationName());
        }
      }
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    }    
  }
  
  public static void updateOPinPolicyConfigurations(LoginModuleConfiguration newLoginModule, Map orgOptions) throws Exception {

    final String METHOD_NAME = "updateCNinPolicyConfigurations";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {newLoginModule});
    }
    
    InitialContext initialContext = new InitialContext();
    SecurityContext securityContext = (SecurityContext) ( initialContext.lookup( LoginModuleContainer.SECURITY_SERVICE ) );
    
    Map newLMOptions = newLoginModule.getOptions();
    String newLoginModuleClassName = newLoginModule.getLoginModuleClassName();
    String newLoginModuleDisplayName = newLoginModule.getName();
    
    String[] policyConfigurationNames = securityContext.listPolicyConfigurations();
    
    for (int i = 0; i < policyConfigurationNames.length; i++) {

      SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext( policyConfigurationNames[i] );
      if (policyConfiguration == null) {
        continue;
      }
      AuthenticationContext authentication = policyConfiguration.getAuthenticationContext();

      // do not update if template is referenced
      String template = authentication.getTemplate();
      if (template != null && template.length() > 0) {
        continue;
      }

      boolean update = false;
      AppConfigurationEntry[] authStackEntries = authentication.getLoginModules();
      for (int entryIndex = 0; entryIndex < authStackEntries.length; entryIndex++) {
        String entryClassName = authStackEntries[entryIndex].getLoginModuleName();        
        Map entryOptions = authStackEntries[entryIndex].getOptions();
        if (newLoginModuleClassName.equals(entryClassName) && entryOptions.equals(orgOptions)) {
          authStackEntries[entryIndex] = new AppConfigurationEntry(entryClassName, authStackEntries[entryIndex].getControlFlag(), newLMOptions );
          update = true;
          
          SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000087", "Policy configuration [{0}] updated. Login module [{1}] with options [{2}] updated to options [{3}].", 
              new Object[] {authentication.getPolicyConfigurationName(), entryClassName, entryOptions, newLMOptions});
        }
      }
      
      if (update) {
        if (authentication instanceof AuthenticationContextImpl) {
          ((AuthenticationContextImpl)authentication).setLoginModulesNoCheck(authStackEntries);
        } else {
          throw new Exception("Cannot set login modules to authentication stack: " + authentication.getPolicyConfigurationName());
        }
      }
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    } 
  }
  
  //	=== remove methods ===
  
  public static void cleanApplicationConfiguration(Configuration appConfig, ConfigurationLock configLock) throws ConfigurationException {
    
    final String METHOD_NAME = "cleanApplicationConfiguration";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {appConfig});
    }
    
    List mapClassNames = new ArrayList();
    
    Configuration lmConfig = appConfig.getSubConfiguration(LoginModuleContainer.LOGIN_MODULES_CONFIGURATION_NAME);
    String[] lmcNames = lmConfig.getAllSubConfigurationNames();
    for ( int i = 0 ; i < lmcNames.length ; i++ ) {
      String lmcConfigName = lmcNames[i];
      Configuration lmcConfig = lmConfig.getSubConfiguration(lmcConfigName);
      String className = (String) lmcConfig.getConfigEntry("class-name");
      mapClassNames.add(className);
    }
    lmConfig.deleteConfiguration();
    
    UserStoreFactory userStoreFactory = SecurityServerFrame.getSecurityContext().getUserStoreContext();
    UserStoreConfiguration userStoreConfiguration = userStoreFactory.getActiveUserStore().getConfiguration();
    ClassLoader classLoader = userStoreFactory.getClass().getClassLoader();
    ModificationContextImpl modificationContext = (ModificationContextImpl)SecurityContextImpl.getRoot().getModificationContext();
    configLock.lock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    try {      
      modificationContext.beginModifications();
      
      Configuration usContainer = modificationContext.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH + "/" + Util.encode(userStoreConfiguration.getName()), true, true);
      Configuration loginModulesContainer = usContainer.getSubConfiguration(LoginModuleContainer.LOGIN_MODULES_CONFIGURATION_NAME);
    
      Iterator it = mapClassNames.iterator();
      while (it.hasNext()) {
        String className = (String) it.next();  
        
        String[] entryNames = loginModulesContainer.getAllSubConfigurationNames();
        for ( int i = 0 ; i < entryNames.length ; i++ ) {
          String entryName = entryNames[i];
          Configuration entry = loginModulesContainer.getSubConfiguration(entryName);
          String entryClassName = (String) entry.getConfigEntry("class-name");
          if (className.equals(entryClassName)) {
            entry.deleteConfiguration();
            
            SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000088", "Login module configuration [{0}] has been deleted.", new Object[] {entryName});
          }
        }
      }
      
      modificationContext.commitModifications();
    } catch(ConfigurationException e) {
      modificationContext.rollbackModifications();
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.000171", "{0}", new Object[]{ e.getMessage() });
      throw e;
    } finally {
      configLock.releaseLock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    } 
  }
  
  public static String checkLoginModulesUsedInPolicyConfigurations(Configuration appConfig, LoginModuleConfiguration[] lmcOLD) throws Exception {
    
    final String METHOD_NAME = "checkLoginModulesUsedInPolicyConfigurations";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {appConfig, lmcOLD});
    }
    
    InitialContext initialContext = new InitialContext();
    SecurityContext securityContext = (SecurityContext) ( initialContext.lookup( LoginModuleContainer.SECURITY_SERVICE ) );
    
    StringBuffer warnings = new StringBuffer();
    
    for ( int i = 0 ; i < lmcOLD.length ; i++ ) {
      
      LoginModuleConfiguration lmc = lmcOLD[i];
      String displayName = lmc.getName();
      String className = lmc.getLoginModuleClassName();
      
      String[] policyConfigurationNames = securityContext.listPolicyConfigurations();
      
      for (int j = 0; j < policyConfigurationNames.length; j++) {

        SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext( policyConfigurationNames[j] );
        if (policyConfiguration == null) {
          continue;
        }
        
        AuthenticationContext authentication = policyConfiguration.getAuthenticationContext();

        String template = authentication.getTemplate();
        if (template != null && template.length() > 0) {
          continue;
        }

        AppConfigurationEntry[] authStackEntries = authentication.getLoginModules();
        for (int entryIndex = 0; entryIndex < authStackEntries.length; entryIndex++) {
          String entryClassName = authStackEntries[entryIndex].getLoginModuleName();        
          if (className.equals( entryClassName ) || displayName.equals( entryClassName )) {
            
            warnings.append("[WARN]: Login module [");
            warnings.append(displayName);
            warnings.append("] is used in policy configuration [");
            warnings.append(authentication.getPolicyConfigurationName());
            warnings.append("]. After application undeploy the policy configuration will not work as expected.\n");
            
            SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000089", "Login module [{0}] is used in policy configuration [{1}]. After application undeploy the policy configuration will not work as expected.\n",
                new Object[] {displayName, authentication.getPolicyConfigurationName()});
          }
        }
      }
    }
    
    String returnValue = warnings.length() == 0 ? null : warnings.toString();
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, returnValue);
    } 
    return returnValue;
  }
  
  public static void storeLoginModuleConfigurationXmlFile(Configuration appConfig, File file) {
    
    final String METHOD_NAME = "storeLoginModuleConfigurationXmlFile";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {appConfig, file});
    }
    
    try {
      
      if (appConfig.existsSubConfiguration(LOGIN_MODULE_CONTAINER_SUBCONFIGURATION_DATA)) {

        Configuration configData = appConfig.getSubConfiguration(LOGIN_MODULE_CONTAINER_SUBCONFIGURATION_DATA);
        
        // store previous configuration xml file
        InputStream prevConfigFile = configData.getFile(LoginModuleContainer.CONFIGURATION_XML_FILE);
        addFileAsStream(configData, LoginModuleContainer.PREVIOUS_DEPLOYMENT_CONFIGURATION_XML_FILE, prevConfigFile);
      
        // store current configuration xml file
        addFileEntry(configData, file);
      
      } else {
        
        Configuration configData = appConfig.createSubConfiguration(LOGIN_MODULE_CONTAINER_SUBCONFIGURATION_DATA);
        
        // store current configuration xml file
        addFileEntry(configData, file);
      }
      
    } catch(Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000164", "Error storing login module configuration xml file.", e);
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    } 
  }
  
  public static LoginModuleConfiguration[] readLoginModuleConfigurationFromXmlFile(Configuration appConfig, String fileName) throws Exception {
    
    final String METHOD_NAME = "readLoginModuleConfigurationFromXmlFile";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {appConfig, fileName});
    }
    
    LoginModuleConfiguration[] prevXMLModules = new LoginModuleConfiguration[0];
    
    if (appConfig.existsSubConfiguration(LOGIN_MODULE_CONTAINER_SUBCONFIGURATION_DATA)) {
      Configuration dataConfig = appConfig.getSubConfiguration(LOGIN_MODULE_CONTAINER_SUBCONFIGURATION_DATA);
      if (dataConfig.existsFile(fileName)) {
        InputStream stream = dataConfig.getFile(fileName);
        prevXMLModules = readLoginModules(stream);
      }        
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, prevXMLModules);
    }
    return prevXMLModules;
  }
  
  //  === userstore methods ===
  
  public static void storeLoginModule(LoginModuleConfiguration loginModule, Configuration appConfig, ConfigurationLock configLock) throws Exception {
    
    final String METHOD_NAME = "storeLoginModule";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {loginModule, appConfig});
    }
    UserStoreFactory userStoreFactory = SecurityServerFrame.getSecurityContext().getUserStoreContext();
    UserStoreConfiguration userStoreConfiguration = userStoreFactory.getActiveUserStore().getConfiguration();
    ClassLoader classLoader = userStoreFactory.getClass().getClassLoader();
    ModificationContextImpl modificationContext = (ModificationContextImpl)SecurityContextImpl.getRoot().getModificationContext();
    configLock.lock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    try {      
      modificationContext.beginModifications();
      
      Configuration usContainer = modificationContext.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH + "/" + Util.encode(userStoreConfiguration.getName()), true, true);
      Configuration loginModulesContainer = usContainer.getSubConfiguration(LoginModuleContainer.LOGIN_MODULES_CONFIGURATION_NAME);
      Configuration loginModulesApplication = createSubConfiguration(appConfig, LoginModuleContainer.LOGIN_MODULES_CONFIGURATION_NAME);
      LoginModuleConfigurationImpl.storeLink(loginModulesContainer, loginModulesApplication, loginModule);
      modificationContext.commitModifications();
      
      if (LOCATION.beInfo()) {
        LOCATION.infoT("Login module configuration [{0}] successfully saved.", new Object[] {loginModule.getName()});
      }
    } catch(Exception e) {
      modificationContext.rollbackModifications();
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.000165", "{0}", new Object[]{e.getMessage()});
      throw e;
    } finally {
      configLock.releaseLock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);  
    }
  }
  
  private static RuntimeLoginModuleConfiguration makeRuntimeLoginModuleConfiguration(String displayName, String description, String className, Map options) {
    String[] suitableAuth = new String[0];
    String[] notSuitableAuth = new String[0];
    String editor = "";
    RuntimeLoginModuleConfiguration runtimeLoginModuleConfiguration = new RuntimeLoginModuleConfiguration(displayName, description, className, options, suitableAuth, notSuitableAuth, editor );
    return runtimeLoginModuleConfiguration;
  }
  
  public static LoginModuleConfiguration[] getLoginModuleConfigurations() {
    UserStoreFactory userStoreFactory = SecurityServerFrame.getSecurityContext().getUserStoreContext();
    UserStoreConfiguration userStoreConfiguration = userStoreFactory.getActiveUserStore().getConfiguration();
    LoginModuleConfiguration[] loginModuleConfigurations = userStoreConfiguration.getLoginModules();
    return loginModuleConfigurations;
  }
  
  private static void saveLoginModulesToUserstore(LoginModuleConfiguration[] configurations) {    
    
    final String METHOD_NAME = "saveLoginModulesToUserstore";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {configurations});
    }
    
    UserStoreFactory userStoreFactory = SecurityServerFrame.getSecurityContext().getUserStoreContext();
    UserStoreConfiguration userStoreConfiguration = userStoreFactory.getActiveUserStore().getConfiguration();
    RuntimeUserStoreConfiguration runtimeUserStoreConfiguration = new RuntimeUserStoreConfiguration(userStoreConfiguration );
    runtimeUserStoreConfiguration.setLoginModules( configurations );
    userStoreFactory.updateUserStore( runtimeUserStoreConfiguration, userStoreFactory.getClass().getClassLoader() );
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);  
    }
  }
  
  public static void refreshUserstore() {
    
    final String METHOD_NAME = "refreshUserstore";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME);
    }    
    
    ModificationContextImpl modificationContext = null;
    try {
      UserStoreFactory userStoreFactory = SecurityServerFrame.getSecurityContext().getUserStoreContext();
      UserStoreConfiguration activeUserstoreConfiguration = userStoreFactory.getActiveUserStore().getConfiguration();    
      modificationContext = (ModificationContextImpl)SecurityContextImpl.getRoot().getModificationContext();
      modificationContext.beginModifications();
      Configuration userstoresContainer = modificationContext.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH, false, false);
      UserStoreConfiguration updatedUserstoreConfiguration = new UserStoreConfigurationImpl(Util.decode(activeUserstoreConfiguration.getName()), userstoresContainer);
      userStoreFactory.updateUserStore( updatedUserstoreConfiguration, userStoreFactory.getClass().getClassLoader() );
    } catch (Exception e) { 
      if (LOCATION.beWarning()) {
        LOCATION.traceThrowableT(Severity.WARNING, "Active userstore is not reloaded to reflect the new configuration state.", e);
      }
    } finally {
      if (modificationContext != null) {
        modificationContext.forgetModifications();
      }
      if (LOCATION.bePath()) {
        LOCATION.exiting(METHOD_NAME);  
      } 
    }
  }
  
  public static void reloadPolicyConfigurations(Set<String> names) throws Exception {
    
    final String METHOD_NAME = "reloadPolicyConfigurations";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {names});
    }
    
    if (!names.isEmpty()) {
      SecurityContext securityContext = SecurityServerFrame.getSecurityContext();
      String[] policyConfigurationNames = securityContext.listPolicyConfigurations();
      for (String policyConfigurationName : policyConfigurationNames) {
        SecurityContext policyConfigurationContext = securityContext.getPolicyConfigurationContext(policyConfigurationName);
        AppConfigurationEntry[] modules = policyConfigurationContext.getAuthenticationContext().getLoginModules();
        for (AppConfigurationEntry module : modules) {
          String moduleName = module.getLoginModuleName();
          if (names.contains(moduleName)) {
            AuthenticationContextImpl authenticationContext = (AuthenticationContextImpl)policyConfigurationContext.getAuthenticationContext();
            authenticationContext.update();
            break;
          }
        }    
      }
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);  
    }
  }
  
  //	=== xml methods === 
  
  public static LoginModuleConfiguration[] readLoginModules(InputStream xmlStream) throws Exception {
    
    final String METHOD_NAME = "readLoginModules";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {xmlStream});
    }
    
    if (xmlStream == null) {
      throw new Exception("Invalid xml stream.");
    }
    
    Document xmlDocument = getXMLDocument(new InputSource(xmlStream));
    NodeList lmElements = xmlDocument.getDocumentElement().getElementsByTagName(LOGIN_MODULE_ELEMENT_NAME);
    if (lmElements.getLength() == 0) {
      throw new Exception("Invalid configuration xml file. No login modules found.");
    }
        
    LoginModuleConfiguration[] loginModules = new LoginModuleConfiguration[lmElements.getLength()];
    for (int i = 0; i < lmElements.getLength(); i++) {
      Element lmElement = (Element) lmElements.item(i);
      LoginModuleConfiguration loginModule = parseLoginModule(lmElement);
      loginModules[i] = loginModule;
      
      if (LOCATION.beInfo()) {
        LOCATION.infoT("Login module read from xml stream with display name: [{0}], class name: [{1}], options: [{2}].", new Object[] {loginModule.getName(), loginModule.getLoginModuleClassName(), loginModule.getOptions()});
      }
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, loginModules);  
    }
    
    return loginModules;    
  }
  
  private static LoginModuleConfiguration parseLoginModule(Element loginModuleElement) throws Exception {
    
    final String METHOD_NAME = "parseLoginModule";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {loginModuleElement});
    }
    
    String className = getLoginModuleValue(loginModuleElement, CLASS_NAME_ELEMENT_NAME);
    String displayName = getLoginModuleValue(loginModuleElement, DISPLAY_NAME_ELEMENT_NAME);
    String description = getLoginModuleValue(loginModuleElement, DESCRIPTION_ELEMENT_NAME);
    Properties options = getLoginModuleOptions(loginModuleElement);
    LoginModuleConfiguration loginModule = makeRuntimeLoginModuleConfiguration(displayName.trim(), description.trim(), className.trim(), options);
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, loginModule);  
    }
    
    return loginModule;
   }
   
   private static Properties getLoginModuleOptions(Element parent) throws Exception {
     
     final String METHOD_NAME = "getLoginModuleOptions";
     if (LOCATION.bePath()) {
       LOCATION.entering(METHOD_NAME, new Object[] {parent});
     }
     
     NodeList optionsElement = parent.getElementsByTagName(OPTIONS_ELEMENT_NAME);
     if (optionsElement.getLength() != 1) {
       if (LOCATION.bePath()) {
         LOCATION.exiting(METHOD_NAME);  
       }
       return new Properties(); 
     }
      
     NodeList optionElements = ((Element)optionsElement.item(0)).getElementsByTagName(OPTION_ELEMENT_NAME);
     if (optionsElement.getLength() == 0) {
       if (LOCATION.bePath()) {
         LOCATION.exiting(METHOD_NAME);  
       }
       return new Properties(); 
     }
     
     Properties options = new Properties();    
     for (int i = 0; i < optionElements.getLength(); i++) {
       Element optionElement = (Element) optionElements.item(i);
       NodeList nameNode = optionElement.getElementsByTagName(OPTION_NAME_ELEMENT_NAME);
       NodeList valueNode = optionElement.getElementsByTagName(OPTION_VALUE_ELEMENT_NAME);
       if (nameNode.getLength() == 1 && valueNode.getLength() == 1) {
         Node nameText = nameNode.item(0).getFirstChild();
         String name = nameText == null ? "" : nameText.getNodeValue() ;
         Node valueText = valueNode.item(0).getFirstChild();
         String value = valueText == null ? "" : valueText.getNodeValue() ;
         options.put(name.trim(), value.trim());
       } else {
         if (LOCATION.beDebug()) {
           LOCATION.debugT("Invalid xml file. Options tags are not valid.");
         }
       }
     }
     
     if (LOCATION.bePath()) {
       LOCATION.exiting(METHOD_NAME, options);  
     }
     
     return options;
   }
   
  private static String getLoginModuleValue(Element parent, String nodeName) throws Exception {
    NodeList nodes = parent.getElementsByTagName(nodeName);
    if (nodes.getLength() == 0) {
      throw new Exception("Invalid xml file. No " + nodeName + " tag found.");
    }
    Node nodeText = nodes.item(0).getFirstChild();
    return nodeText == null ? "" : nodeText.getNodeValue();
  }
  
  public static Document getXMLDocument(InputSource in) throws ParserConfigurationException, SAXException, IOException {
    
    final String METHOD_NAME = "getXMLDocument";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {in});
    }
    
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);
    dbf.setNamespaceAware(false);
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(in);
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, document);  
    }
    return document;
  }
  
  //	=== database configuration methods === 
  
  public static void addConfigEntry(Configuration configuration, String entryName, Object entryValue) throws ConfigurationException {
    try {
      configuration.addConfigEntry(entryName, entryValue);
  	} catch (NameAlreadyExistsException e) {
  	  configuration.modifyConfigEntry(entryName, entryValue);
  	}
  }
  
  public static void addFileEntry(Configuration configuration, File file) throws ConfigurationException {
  	try {
  	  configuration.addFileEntry(file);
  	} catch (NameAlreadyExistsException ex) {
  	  configuration.updateFile(file);
  	} 
  }
  
  public static void addFileAsStream(Configuration configuration, String fileName, InputStream stream) throws ConfigurationException {
    try {
      configuration.addFileAsStream(fileName, stream);
    } catch (NameAlreadyExistsException ex) {
      configuration.updateFileAsStream(fileName, stream);
    } 
  }
  	
  public static Configuration createSubConfiguration(Configuration rootConfiguration, String subConfigurationName) throws ConfigurationException {
    Configuration subConfiguration = null;
    try {
      subConfiguration = rootConfiguration.createSubConfiguration(subConfigurationName);
    } catch (NameAlreadyExistsException e) {
      subConfiguration = rootConfiguration.getSubConfiguration(subConfigurationName);
  	} 
  	return subConfiguration;
  }
  
  public static LoginModuleConfiguration[] readLoginModuleConfigurtionsFromAppConfig(Configuration lmConfig) throws Exception {
    
    final String METHOD_NAME = "readLoginModuleConfigurtionsFromAppConfig";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {lmConfig});
    }
    
    String[] lmcNames = lmConfig.getAllSubConfigurationNames();
    LoginModuleConfiguration[] loginModules = new LoginModuleConfiguration[lmcNames.length];
    for ( int i = 0 ; i < lmcNames.length ; i++ ) {
      String lmcConfigName = lmcNames[i];
      Configuration lmcConfig = lmConfig.getSubConfiguration(lmcConfigName);
      String displayName = lmcConfigName;
      String className = (String) lmcConfig.getConfigEntry("class-name");
      String description = (String) lmcConfig.getConfigEntry("description");
      Configuration optionsConfig = lmcConfig.getSubConfiguration("options");      
      String[] optionNames = optionsConfig.getAllConfigEntryNames();
      HashMap options = new HashMap(optionNames.length);
      for (int j = 0; j < optionNames.length; j++) {
        String optionName = optionNames[j];
        String optionValue = (String) optionsConfig.getConfigEntry(optionName);
        options.put(optionName, optionValue);
      }
      LoginModuleConfiguration lmc = makeRuntimeLoginModuleConfiguration(displayName, description, className, options);
      loginModules[i] = lmc;
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, loginModules);  
    }
    return loginModules;
  }
  
  public static void removeLoginModuleLinks(String[] lmNames, ConfigurationLock configLock) throws Exception {
      
    final String METHOD_NAME = "removeLoginModuleLink";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {lmNames, configLock});
    }
    UserStoreFactory userStoreFactory = SecurityServerFrame.getSecurityContext().getUserStoreContext();
    UserStoreConfiguration userStoreConfiguration = userStoreFactory.getActiveUserStore().getConfiguration();
    ModificationContextImpl modificationContext = (ModificationContextImpl)SecurityContextImpl.getRoot().getModificationContext();
    configLock.lock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
   
    try {      
      modificationContext.beginModifications();
      
      Configuration usContainer = modificationContext.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH + "/" + Util.encode(userStoreConfiguration.getName()), true, true);
      Configuration loginModulesContainer = usContainer.getSubConfiguration(LoginModuleContainer.LOGIN_MODULES_CONFIGURATION_NAME);
      loginModulesContainer.deleteSubConfigurations(lmNames);
      modificationContext.commitModifications();
      
      if (LOCATION.beInfo()) {
        LOCATION.infoT("Login module link [{0}] successfully removed.", new Object[] {lmNames});
      }
    } catch(Exception e) {
      modificationContext.rollbackModifications();
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.000165", "{0}", new Object[]{e.getMessage()});
      throw e;
    } finally {
      configLock.releaseLock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);  
    }
  }
  
}
