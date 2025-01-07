package com.sap.engine.services.security.jmx.auth;

import javax.management.openmbean.CompositeData;

/**
 * @author Georgi Dimitrov (i031654)
 */
public interface AuthenticationManagerMBean {

  public static final String DATA_NOT_LOCKED = "DATA_NOT_LOCKED";
  public static final String LOCK_FAILURE = "LOCK_FAILURE";
  public static final String UNLOCK_FAILURE = "UNLOCK_FAILURE";
  public static final String ERROR_GET_POLICY_CONFIGURATIONS_DATA = "ERROR_GET_POLICY_CONFIGURATIONS_DATA";
  public static final String ERROR_GET_LOGIN_MODULES_DATA = "ERROR_GET_LOGIN_MODULES_DATA";
  public static final String POLICY_CONFIGURATION_NOT_FOUND = "POLICY_CONFIGURATION_NOT_FOUND";
  public static final String LOGIN_MODULE_NOT_FOUND = "LOGIN_MODULE_NOT_FOUND";
  public static final String ERROR_SAVE_POLICY_CONFIGURATION = "ERROR_SAVE_POLICY_CONFIGURATION";
  public static final String ERROR_SAVE_LOGIN_MODULE = "ERROR_SAVE_LOGIN_MODULE";
  public static final String ERROR_REMOVE_POLICY_CONFIGURATION = "ERROR_REMOVE_POLICY_CONFIGURATION";
  public static final String ERROR_REMOVE_LOGIN_MODULE = "ERROR_REMOVE_LOGIN_MODULE";
  public static final String POLICY_CONFIGURATION_IS_USED = "POLICY_CONFIGURATION_IS_USED";
  public static final String LOGIN_MODULE_IS_USED = "LOGIN_MODULE_IS_USED";
  public static final String NON_CUSTOM_POLICY_CONFIGURATION = "NON_CUSTOM_POLICY_CONFIGURATION";
  public static final String POLICY_CONFIGURATION_TYPE_NOT_DELETABLE = "POLICY_CONFIGURATION_TYPE_NOT_DELETABLE";
    
  public static final String AUTHSCHEMES_MIGRATION_COMPLETED = "AUTHSCHEMES_MIGRATION_COMPLETED";
  public static final String AUTHSCHEMES_MIGRATION_PREPARED = "AUTHSCHEMES_MIGRATION_PREPARED";
  public static final String AUTHSCHEMES_CONFLICTS = "AUTHSCHEMES_CONFLICTS";
  public static final String AUTHSCHEMES_FILE_INVALID = "AUTHSCHEMES_FILE_INVALID";
  public static final String AUTHSCHEMES_MIGRATION_PROBLEM = "AUTHSCHEMES_MIGRATION_PROBLEM";
  public static final String AUTHSCHEMES_MIGRATION_SAVE_ERROR = "AUTHSCHEMES_MIGRATION_SAVE_ERROR";
  public static final String AUTHSCHEMES_NOT_FOUND_IN_XML = "AUTHSCHEMES_NOT_FOUND_IN_XML";
  
  public static int VERSION = 1;
  
  /**
   * Makes enqueue server lock for current user
   * 
   * @throws Exception
   * @deprecated this method does nothing
   */
  public void lock() throws Exception;

  /**
   * Removes the lock created with lock method
   * 
   * @throws Exception
   * @deprecated this method does nothing
   */
  public void unlock() throws Exception;

  /**
   * @return the policy configuration names
   * @throws Exception
   */
  public String[] getPolicyConfigurationNames() throws Exception;

  /**
   * @return the login module names
   * @throws Exception
   */
  public String[] getLoginModuleNames() throws Exception;

  /**
   * @param name policy configuration name
   * @return the policy configuration with that name
   * @throws Exception
   */
  public PolicyConfiguration getPolicyConfiguration(String name)
      throws Exception;

  /**
   * Add/Update a single policy configuration. If a policy configuration with
   * that name already exists then update is performed, else a new policy
   * configuration is created if its type is custom.
   * 
   * @param data the policy configuration to save
   * @throws Exception, if policyConfiguration does not exist and is not custom
   */
  public void savePolicyConfiguration(CompositeData data) throws Exception;

  /**
   * Remove CUSTOM policy configuration, if it is not used by another policy
   * configuration
   * 
   * @param name name of the policy configuration to be removed
   * @throws Exception, if policy configuration does not exist or is not custom
   *           or is used by another policy configuration as template
   */
  public void removePolicyConfiguration(String name) throws Exception;

  /**
   * @return all policy configurations
   * @throws Exception
   */
  public PolicyConfiguration[] getPolicyConfigurations() throws Exception;

  /**
   * Remove CUSTOM policy configurations
   * 
   * @param name name of the policy configurations to be removed
   * @throws Exception
   */
  public void savePolicyConfigurations(CompositeData[] data) throws Exception;

  /**
   * Remove CUSTOM policy configurations
   * 
   * @param names the names of the policy configurations to be removed
   * @throws Exception
   */
  public void removePolicyConfigurations(String[] names) throws Exception;

  /**
   * Returns Login Module from userstore by its name
   * 
   * @param name name to search for
   * @return login module with that name
   * @throws Exception
   */
  public LoginModule getLoginModule(String name) throws Exception;

  /**
   * Add/Update Login Module from Userstore, In case of update also update is
   * performed to all policy configurations referencing this login module
   * 
   * @param data login module to update/add
   * @throws Exception
   */
  public void saveLoginModule(CompositeData data) throws Exception;

  /**
   * Remove Login Module from userstore
   * 
   * @param names names of login modules to be removed
   * @throws Exception, if login module does not exist or ( is used in some auth
   *           stack and no other login module is using the same class )
   */
  public void removeLoginModule(String name) throws Exception;

  /**
   * Get all Login Modules from Userstore
   * 
   * @return all Login Modules from userstore
   * @throws Exception
   */
  public LoginModule[] getLoginModules() throws Exception;

  /**
   * Replace Login Modules from Userstore, Update is performed to all policy
   * configurations referencing an updated login module
   * 
   * @param data new login modules
   * @throws Exception
   */
  public void saveLoginModules(CompositeData[] data) throws Exception;

  /**
   * Remove Login Modules from userstore
   * 
   * @param names names of login modules to be removed
   * @throws Exception
   */
  public void removeLoginModules(String[] names) throws Exception;

  
  /**
   * 
   * @return version of the jmx model
   */
  public int getVersion();
  
  
  /**
   * 
   * @return status of the authschemes migration 
   */
  public String getAuthschemesMigrationStatus();
  
  /**
   * 
   * @return true if ume property login.authschemes.definition.file is not empty, 
   * otherwise false 
   */
  public boolean isAuthschemesXMLFileUsed();
  
  
  /**
   * Migrates the authschemes from the XML file
   * @throws Exception
   */
  public void migrateAuthschemes()  throws Exception;
  
}