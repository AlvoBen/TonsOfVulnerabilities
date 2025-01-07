package com.sap.engine.services.security.jmx.auth.impl;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.gui.userstore.stores.RuntimeUserStoreConfiguration;
import com.sap.engine.services.security.jmx.auth.AuthStackEntry;
import com.sap.engine.services.security.jmx.auth.AuthenticationManagerMBean;
import com.sap.engine.services.security.jmx.auth.LoginModule;
import com.sap.engine.services.security.jmx.auth.MapEntry;
import com.sap.engine.services.security.jmx.auth.PolicyConfiguration;
import static com.sap.engine.services.security.jmx.auth.impl.AuthUtil.convertMapEntriesToMap;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.logon.imp.AuthSchemeFactory;
import com.sap.security.core.logon.imp.AuthSchemeFactory.AuthschemeProps;
import com.sap.security.core.logon.imp.AuthSchemeFactory.AuthschemeRefProps;
import com.sap.security.core.util.config.IUMConfigAdmin;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import static com.sap.tc.logging.Severity.INFO;
import com.sap.tc.logging.SimpleLogger;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import javax.management.openmbean.CompositeData;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.AppConfigurationEntry;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @author Georgi Dimitrov ( i031654 )
 */

public class AuthenticationManager extends StandardMBean implements AuthenticationManagerMBean {

  private static final String LOCKING_NAMESPACE = "$AuthenticationManager";
  private static final String LOCKING_DESCRIPTION = "Lock for AuthenticationManagerMBean";
  private static final String LOCK_ARGUMENT = "ReadForUpdate";
  private static final String FALLBACK_USER = "<AuthenticationManager>";
  private static final String SECURITY_SERVICE = "security";

  private static final String AUTH_XML_FILE_PROPERTY = "login.authschemes.definition.file";

  private static final Location LOCATION = Location.getLocation(AuthenticationManager.class);
  public final static Category CHANGE_LOG_CATEGORY = Category.getCategory(Category.SYS_CHANGES, "Properties/Security");

  private SecurityContext securityContext = null;

  //  private ServerInternalLocking locking = null;

  private ApplicationServiceContext serviceContext = null;

  public AuthenticationManager() throws NotCompliantMBeanException, NamingException, TechnicalLockException, IllegalArgumentException {

    super(AuthenticationManagerMBean.class);
    InitialContext initialContext = new InitialContext();
    securityContext = (SecurityContext) (initialContext.lookup(SECURITY_SERVICE));
    serviceContext = SecurityServerFrame.getServiceContext();

    //    locking = SecurityServerFrame.lockingContext.createServerInternalLocking(
    //        LOCKING_NAMESPACE, LOCKING_DESCRIPTION );
  }

  /**
   * Makes enqueue server lock for current user
   *
   * @throws Exception
   * @deprecated this method does nothing
   */

  public synchronized void lock() throws Exception {
    //    String currentUser = null;
    //    try {
    //      currentUser = getCurrentUser();
    //
    //      locking.lock( LOCKING_NAMESPACE, LOCK_ARGUMENT,
    //          LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE, currentUser );
    //
    //    } catch (LockException le) {
    //      String collisionOwner = le.getCollisionUserName();
    //      if (collisionOwner.equals( currentUser )) {
    //        location.debugT( "Authentication Manager Data is already locked by "
    //            + currentUser );
    //        return;
    //      } else {
    //        location
    //            .debugT( "Authentication Manager Data is already locked by another user" );
    //        throwException( LOCK_FAILURE
    //            + ": data is already locked by another user" );
    //      }
    //    } catch (Exception e) {
    //      location.traceThrowableT( Severity.DEBUG,
    //          "Error while locking Authentication data", e );
    //      throwException( LOCK_FAILURE );
    //    }
  }

  /**
   * Removes the lock created with lock method
   * @deprecated this method does nothing
   * @throws Exception
   */
  public synchronized void unlock() throws Exception {
    //    try {
    //      if (!isDataLockedFromCurrentUser()) {
    //        throwException( DATA_NOT_LOCKED );
    //      }
    //
    //      locking.unlock( LOCKING_NAMESPACE, LOCK_ARGUMENT,
    //          LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE, false );
    //
    //    } catch (Exception e) {
    //      location.traceThrowableT( Severity.DEBUG,
    //          "Error while unlocking Authentication data", e );
    //      throwException( UNLOCK_FAILURE + ":" + e.getMessage() );
    //    }
    //
    //    //check if removal of lock was successful
    //    if (isDataLockedFromCurrentUser()) {
    //      location.debugT( "Error while unlocking Authentication data" );
    //      throwException( UNLOCK_FAILURE + ": data still locked" );
    //    }

  }

  /**
   * @return the policy configuration names
   * @throws Exception
   */

  public String[] getPolicyConfigurationNames() throws Exception {

    String[] policyConfigurationNames = null;
    try {
      policyConfigurationNames = securityContext.listPolicyConfigurations();
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Error while getting policy configuration names", e);
      throwException(ERROR_GET_POLICY_CONFIGURATIONS_DATA, e);
    }
    return policyConfigurationNames;

  }

  /**
   * @return all policy configurations
   * @throws Exception
   */

  public PolicyConfiguration[] getPolicyConfigurations() throws Exception {

    String[] policyConfigurationNames = getPolicyConfigurationNames();
    PolicyConfiguration[] policyConfigurations = new PolicyConfiguration[policyConfigurationNames.length];
    for (int i = 0; i < policyConfigurationNames.length; i++) {
      policyConfigurations[i] = getPolicyConfiguration(policyConfigurationNames[i]);
    }
    return policyConfigurations;

  }

  /**
   * @param name policy configuration name
   * @return the policy configuration with that name
   * @throws Exception
   */

  public PolicyConfiguration getPolicyConfiguration(String name) throws Exception {

    SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext(name);

    if (policyConfiguration == null) {
      LOCATION.debugT("Invalid Policy Configuration " + name);
      throwException(ERROR_GET_POLICY_CONFIGURATIONS_DATA + ":" + name, null);
    }

    PolicyConfiguration jmxPolicyConfiguration = null;
    try {
      jmxPolicyConfiguration = new PolicyConfigurationImpl(name, policyConfiguration);
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Error while creating composite data policy configuration for: " + name, e);
      throwException(ERROR_GET_POLICY_CONFIGURATIONS_DATA, e);
    }

    return jmxPolicyConfiguration;

  }

  /**
   * Save a single policy configuration. If a policy configuration with that
   * name already exists then update is performed. Else a new policy
   * configuration is created if its type is custom.
   *
   * @param data the policy configuration to save
   * @throws Exception
   */
  public void savePolicyConfiguration(CompositeData data) throws Exception {

    try {

      PolicyConfiguration cdPolicyConfiguration = new PolicyConfigurationImpl(data);

      //data to be saved
      String name = cdPolicyConfiguration.getName();
      byte type = cdPolicyConfiguration.getType().byteValue();
      String template = cdPolicyConfiguration.getTemplate();
      CompositeData[] stackEntries = cdPolicyConfiguration.getAuthStack();
      CompositeData[] properties = cdPolicyConfiguration.getProperties();

      SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext(name);

      // needed for ChangeLog
      boolean isExisting = (policyConfiguration != null);
      PolicyConfiguration existingPC = null;

      if (policyConfiguration == null) {
        LOCATION.debugT("PolicyConfiguration does not exist: " + name);

        //check if a new policy configuration is to be created
        if (type != SecurityContext.TYPE_CUSTOM && type != SecurityContext.TYPE_AUTHSCHEME && type != SecurityContext.TYPE_AUTHSCHEME_REFERENCE) {
          LOCATION.debugT("Policy configuration with type [" + type + "] can not be created.");
          throwException(POLICY_CONFIGURATION_NOT_FOUND + ":" + name, null);
        }
        //create new custom policy configuration
        securityContext.registerPolicyConfiguration(name, type);
        policyConfiguration = securityContext.getPolicyConfigurationContext(name);
        LOCATION.debugT("New PolicyConfiguration is created: " + name);
      }

      try {
        //TODO: ChangeLog
        existingPC = getPolicyConfiguration(name);
      } catch (Exception e) {
        LOCATION.throwing(e);
        LOCATION.warningT("ChangeLog problem - cannot get policy configuration object for an existing policy configuration context. ChangeLog details will be incomplete");
      }

      AuthenticationContext authenticationContext = policyConfiguration.getAuthenticationContext();

      Map templateProperties = null;
      if (template != null && template.length() > 0) {
        SecurityContext templatePolicyConfiguration = securityContext.getPolicyConfigurationContext(template);
        AuthenticationContext templateAuthenticationContext = templatePolicyConfiguration.getAuthenticationContext();
        templateProperties = templateAuthenticationContext.getProperties();
      }
      if (templateProperties == null) {
        templateProperties = new HashMap(0);
      }

      //Save custom properties
      //Saving the properties must be before saving authentication stack
      MapEntry[] jmxProperties = AuthUtil.makeMapEntryArray(properties);
      for (int p = 0; p < jmxProperties.length; p++) {
        MapEntry me = jmxProperties[p];

        //to be reconsidered
        authenticationContext.setProperty(me.getKey(), me.getValue());

        //
        /*
         String key = me.getKey();
         String value = me.getValue();
         if( value==null || value.trim().length()==0 ) {
         //delete property
         authenticationContext.setProperty( key, null );
         } else if( !templateProperties.containsKey(key) ) {
         authenticationContext.setProperty( key, value );
         } else {
         String templateValue = (String)templateProperties.get(key);
         if( templateValue != value ) {
         authenticationContext.setProperty( key, value );
         }
         }
         */

      }

      //save auth stack
      if (template == null || template.length() == 0) {
        AuthStackEntry[] jmxStackEntries = AuthUtil.makeAuthStackEntryArray(stackEntries);
        //convert jmxStackEntries to authentication stack entries
        AppConfigurationEntry[] appConfigurationEntries = new AppConfigurationEntry[jmxStackEntries.length];
        for (int i = 0; i < jmxStackEntries.length; i++) {
          appConfigurationEntries[i] = AuthUtil.makeAppConfigurationEntry(jmxStackEntries[i]);
        }

        //remove ticket reference
        authenticationContext.setLoginModules((String) null);
        authenticationContext.setLoginModules(appConfigurationEntries);

      } else {
        //set template
        authenticationContext.setLoginModules(template);
      }

      try {// ChangeLog

        if (isExisting) {
          String changeDetails = getChangeDescription(existingPC, cdPolicyConfiguration);

          if (changeDetails != null) {
            SimpleLogger.log(INFO
                , CHANGE_LOG_CATEGORY
                , LOCATION
                , "ASJ.secsrv.000215"
                , "Policy configuration [{0}] was changed. Details:[{1}]."
                , new Object[]{name, changeDetails});
          }
        } else {
          String newDetails = getStringRepresentation(cdPolicyConfiguration);
          SimpleLogger.log(INFO
              , CHANGE_LOG_CATEGORY
              , LOCATION
              , "ASJ.secsrv.000212"
              , "Policy configuration [{0}] was created. Details: [{1}]"
              , new Object[]{name, newDetails});
        }
      } catch (Exception _) {
        LOCATION.throwing(_);
        LOCATION.warningT("ChangeLog problem - cannot get details for policy configuration change. ChangeLog details will be incomplete");
      }
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Error while save of Policy Configuration", e);
      throwException(ERROR_SAVE_POLICY_CONFIGURATION, e);
    }
  }

  private static String getStringRepresentation(PolicyConfiguration pc) {
    if (pc == null) {
      return "<no details>";
    }
    StringBuilder sb = new StringBuilder();
    sb.append("\nname=");
    sb.append(pc.getName());
    sb.append("\ntemplate=");
    sb.append(pc.getTemplate());
    sb.append("\ntype=");
    sb.append(pc.getType());
    sb.append("\nproperties=");
    MapEntry[] pcProps = pc.getProperties();
    sb.append((pcProps == null)? "null": convertMapEntriesToMap(pcProps));
    sb.append("\nauthStack=");
    AuthStackEntry[] authStack = pc.getAuthStack();
    sb.append(toString(authStack));
    return sb.toString();
  }

  private static String toString(AuthStackEntry[] authStack) {
    StringBuilder sb = new StringBuilder();

    sb.append("\n[");
    for (AuthStackEntry entry: authStack) {
      sb.append("\n  ");
      sb.append(toString(entry));
    }
    sb.append("]\n");

    return sb.toString();
  }

  private static String toString(AuthStackEntry entry) {
    StringBuilder sb = new StringBuilder();

    sb.append("[");
    sb.append(entry.getFlag());
    sb.append("/");
    sb.append(entry.getClassName());
    sb.append("/");
    sb.append(convertMapEntriesToMap(entry.getOptions()));
    sb.append("]");

    return sb.toString();
  }

  private static String getChangeDescription(AuthStackEntry[] oldStack, AuthStackEntry[] newStack) {
    class AuthStackEntryDescriptor {
      private final int possition;
      private final MapEntry[] options;
      private final String flag;

      AuthStackEntryDescriptor(int possition, String flag, MapEntry[] options) {
        this.possition = possition;
        this.options = options;
        this.flag = flag;
      }
    }
    boolean isSomethingChanged = false;
    Hashtable<String, AuthStackEntryDescriptor> oldEntries = new Hashtable<String,AuthStackEntryDescriptor>(oldStack.length);
    Hashtable<String, AuthStackEntryDescriptor> newEntries = new Hashtable<String,AuthStackEntryDescriptor>(newStack.length);

    AuthStackEntry entry;
    for (int i = 0; i < oldStack.length; i++) {
      entry = oldStack[i];
      oldEntries.put(entry.getClassName(), new AuthStackEntryDescriptor(i
                                                    , entry.getFlag()
                                                    , entry.getOptions()));
    }
    for (int i = 0; i < newStack.length; i++) {
       entry = newStack[i];
       newEntries.put(entry.getClassName(), new AuthStackEntryDescriptor(i
                                                    , entry.getFlag()
                                                    , entry.getOptions()));
    }

    StringBuilder removedEntries = new StringBuilder();
    StringBuilder addedEntries = new StringBuilder();
    StringBuilder priorityChangedEntries = new StringBuilder();
    StringBuilder flagChangedEntries = new StringBuilder();
    StringBuilder propertiesChangedEntries = new StringBuilder();

    AuthStackEntryDescriptor newEntry;
    AuthStackEntryDescriptor oldEntry;
    Map oldOpts;
    Map newOpts;
    String key;
    for (int i = 0; i < oldStack.length; i ++) {
      key = oldStack[i].getClassName();
      newEntry = newEntries.get(key);
      if (newEntry == null) {
        removedEntries.append("[").append(key).append("]");
        isSomethingChanged = true;
      } else {
        if (newEntry.possition != i) {
          priorityChangedEntries.append("[").append(key).append(": from ").append(i).append(" to ").append(newEntry.possition).append("]");
          isSomethingChanged = true;
        }
        if (!newEntry.flag.equals(oldStack[i].getFlag())) {
          flagChangedEntries.append("[").append(key).append(": from ").append(oldStack[i].getFlag()).append(" to ").append(newEntry.flag).append("]");
          isSomethingChanged = true;
        }
        if (!areEqual(oldStack[i].getOptions(), newEntry.options)) {
          oldOpts = convertMapEntriesToMap(oldStack[i].getOptions());
          newOpts = convertMapEntriesToMap(newEntry.options);
          propertiesChangedEntries.append("[").append(key).append(": from ").append(oldOpts).append(" to ").append(newOpts).append(" ]");
          isSomethingChanged = true;
        }
      }
    }
    for (int i = 0; i < newStack.length; i++) {
      key = newStack[i].getClassName();
      oldEntry = oldEntries.get(key);
      if (oldEntry == null) {
        addedEntries.append("[").append(toString(newStack[i])).append(" at possition ").append(i).append("]");
        isSomethingChanged = true;
      }
    }

    String result = null;

    if (isSomethingChanged) {
      StringBuilder sb = new StringBuilder();
      if (removedEntries.length() != 0) {
        sb.append("\n- Removed Login Modules: ").append(removedEntries);
      }
      if (addedEntries.length() != 0) {
        sb.append("\n- Added Login Modules: ").append(addedEntries);
      }
      if (flagChangedEntries.length() != 0) {
        sb.append("\n- Login Modules with Changed Flag: ").append(flagChangedEntries);
      }
      if (priorityChangedEntries.length() != 0) {
        sb.append("\n- Login Modules with Changed Priority: ").append(priorityChangedEntries);
      }
      if (propertiesChangedEntries.length() != 0) {
        sb.append("\n- Login Modules with Changed Properties: ").append(propertiesChangedEntries);
      }
      result = sb.toString();
    }

    return result;
  }

  private static String getChangeDescription(PolicyConfiguration oldPC, PolicyConfiguration newPC) {
    StringBuilder details = new StringBuilder();
    Map oldProps = convertMapEntriesToMap(oldPC.getProperties());
    Map newProps = convertMapEntriesToMap(newPC.getProperties());
    boolean isSomethingChanged = false;

    if (!oldProps.equals(newProps)) {
      details.append("\nProperties: \n  old value [").append(oldProps).append("], new value [").append(newProps).append("]");
      isSomethingChanged = true;
    }

    /*
    *    0 0
    *    x 0
    *    0 x
    *    x y
    * */
    if (isEmpty(oldPC.getTemplate())) {
      if (isEmpty(newPC.getTemplate())) {
        // 0 0
        String authStackChange = getChangeDescription(oldPC.getAuthStack(), newPC.getAuthStack());
        if (authStackChange != null) {
          details.append("\nAuthentication Stack: ").append(authStackChange);
          isSomethingChanged = true;
        }
      } else {
        // 0 x
        details.append("\nUsed Template: \n  old value [").append(oldPC.getTemplate()).append("], new value [").append(newPC.getTemplate()).append("]");
        isSomethingChanged = true;
      }
    } else {
      if (isEmpty(newPC.getTemplate())) {
        //x 0
        details.append("\nUsed Template: \n  old value [").append(oldPC.getTemplate()).append("], new value [").append(newPC.getTemplate()).append("]");
        String authStackChange = getChangeDescription(oldPC.getAuthStack(), newPC.getAuthStack());
        if (authStackChange != null) {
          details.append("\nAuthentication Stack: ").append(authStackChange);
        }
        isSomethingChanged = true;
      } else {
        // x y
        if (!oldPC.getTemplate().equals(newPC.getTemplate())) {
          details.append("\nUsed Template: \n  old value [").append(oldPC.getTemplate()).append("], new value [").append(newPC.getTemplate()).append("]");
          isSomethingChanged = true;
        }
      }
    }
    return (isSomethingChanged)
            ? details.toString()
            : null;
  }


  private static boolean isEmpty(String str) {
    return str == null || str.length() == 0 || str.trim().length() == 0;
  }
  private static boolean areEqual(MapEntry[] oldOpts, MapEntry[] newOpts) {
    if (oldOpts == newOpts) {
      // 0 0
      // x x
      return true;
    }
    if (oldOpts == null || newOpts == null) {
      // 0 *
      // * 0
      return false;
    }
    if (oldOpts.length != newOpts.length) {
      // x y
      return false;
    }

    // x y
    // x x
    for (int i = 0; i < oldOpts.length; i++) {
      if (!oldOpts[i].equals(newOpts[i])) {
        return false;
      }
    }

    return true;
  }

  public void saveNewAuthscheme(String name, String template, String priority, String frontendtarget) throws Exception {

    try {

      SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext(name);

      if (policyConfiguration != null) {
        LOCATION.debugT("Authscheme will not be created. It already exists!: " + name);
        return;
      }

      //create new empty policy configuration
      securityContext.registerPolicyConfiguration(name, SecurityContext.TYPE_AUTHSCHEME);
      policyConfiguration = securityContext.getPolicyConfigurationContext(name);

      AuthenticationContext authenticationContext = policyConfiguration.getAuthenticationContext();

      authenticationContext.setProperty("priority", priority);
      authenticationContext.setProperty("frontendtarget", frontendtarget);

      authenticationContext.setLoginModules(template);


       SimpleLogger.log(INFO
          , CHANGE_LOG_CATEGORY
          , LOCATION
          , "ASJ.secsrv.000213"
          , "Authscheme with name [{0}], template [{1}], priority [{2}] and frontendtarget [{3}] was saved."
          , new Object[]{name, template, priority, frontendtarget});


    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.009500", "Error while creating new authscheme");
      throwException(ERROR_SAVE_POLICY_CONFIGURATION, e);
    }

  }

  public void saveNewAuthschemeReference(String name, String template) throws Exception {

    try {
      SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext(name);

      if (policyConfiguration != null) {
        LOCATION.debugT("Authscheme reference  will not be created. It already exists!: " + name);
        return;
      }

      //create new empty policy configuration
      securityContext.registerPolicyConfiguration(name, SecurityContext.TYPE_AUTHSCHEME_REFERENCE);
      policyConfiguration = securityContext.getPolicyConfigurationContext(name);
      LOCATION.debugT("Empty authscheme created: " + name);

      AuthenticationContext authenticationContext = policyConfiguration.getAuthenticationContext();
      authenticationContext.setLoginModules(template);

    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.009501", "Error while creating new authscheme");
      throwException(ERROR_SAVE_POLICY_CONFIGURATION, e);
    }

  }

  /**
   * Save for array of policy configurations: Update for old policy
   * configurations and Create for new CUSTOM policy configurations
   *
   * @param data the policy configurations to save
   * @throws Exception
   */
  public synchronized void savePolicyConfigurations(CompositeData[] data) throws Exception {
    //comment locking checks since lock()/unlock() are deprecated
    //if (!isDataLockedFromCurrentUser()) {
    //  throwException( DATA_NOT_LOCKED );
    //}

    PolicyConfiguration[] jmxPolicyConfigurations = AuthUtil.makePolicyConfigurationArray(data);

    for (int i = 0; i < jmxPolicyConfigurations.length; i++) {
      savePolicyConfiguration(jmxPolicyConfigurations[i]);
    }

  }

  /**
   * Remove policy configuration.
   * Only Custom, Authscheme, Authscheme-References and Other can be removed!
   *
   * @param name name of the policy configuration to be removed
   * @throws Exception
   */
  public synchronized void removePolicyConfiguration(String name) throws Exception {
    // comment locking checks since lock()/unlock() are deprecated
    //if (!isDataLockedFromCurrentUser()) {
    //  throwException( DATA_NOT_LOCKED );
    //}

    SecurityContext policyConfiguration = null;
    try {
      policyConfiguration = securityContext.getPolicyConfigurationContext(name);
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Error in checks before remove policy configuration, can not get policy configuration: " + name, e);
      throwException(ERROR_REMOVE_POLICY_CONFIGURATION + ":" + name + "- checks before remove failed", null);
    }

    if (policyConfiguration == null) {
      LOCATION.debugT("PolicyConfiguration does not exist: " + name);
      throwException(POLICY_CONFIGURATION_NOT_FOUND + ":" + name, null);
    }

    byte type = policyConfiguration.getPolicyConfigurationType();

    if (type != SecurityContext.TYPE_CUSTOM && type != SecurityContext.TYPE_AUTHSCHEME && type != SecurityContext.TYPE_AUTHSCHEME_REFERENCE && type != SecurityContext.TYPE_OTHER) {
      LOCATION.debugT("PolicyConfiguration of this type can not be removed: " + name);
      throwException(POLICY_CONFIGURATION_TYPE_NOT_DELETABLE + ":" + name, null);
    }

    boolean isReferenced = false;
    try {
      isReferenced = isPolicyConfigurationReferenced(name);
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Error in checks before remove policy configuration, can not check if template is referenced: " + name, e);
      throwException(ERROR_REMOVE_POLICY_CONFIGURATION + ":" + name + "- checks before remove failed", e);
    }

    if (isReferenced) {
      LOCATION.debugT("PolicyConfiguration is still referenced: " + name);
      throwException(POLICY_CONFIGURATION_IS_USED + ":" + name, null);
    }

    try {
      securityContext.unregisterPolicyConfiguration(name);
    } catch (Exception e) {
      LOCATION.debugT("Error in remove policy configuration: " + name);
      throwException(ERROR_REMOVE_POLICY_CONFIGURATION + ":" + name, e);
    }

    SimpleLogger.log(INFO
        , CHANGE_LOG_CATEGORY
        , LOCATION
        , "ASJ.secsrv.000211"
        , "Policy configuration [{0}] was removed."
        , new Object[]{name});
  }

  /**
   * Remove policy configurations
   *
   * @param names, the names of the policy configurations to be removed
   * @throws Exception
   */
  public synchronized void removePolicyConfigurations(String names[]) throws Exception {

    for (int i = 0; i < names.length; i++) {
      removePolicyConfiguration(names[i]);
    }

  }

  /**
   * @return the login module names
   * @throws Exception
   */
  public String[] getLoginModuleNames() throws Exception {
    String[] loginModuleNames = null;
    try {
      LoginModuleConfiguration[] loginModuleConfigurations = getLoginModuleConfigurations();
      loginModuleNames = new String[loginModuleConfigurations.length];
      for (int i = 0; i < loginModuleConfigurations.length; i++) {
        loginModuleNames[i] = loginModuleConfigurations[i].getName();
      }
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Error while getting login module names", e);
      throwException(ERROR_GET_LOGIN_MODULES_DATA, e);
    }
    return loginModuleNames;
  }

  /**
   * Returns Login Module from userstore by its name
   *
   * @param name name to search for
   * @return login module with that name
   * @throws Exception
   */
  public LoginModule getLoginModule(String name) throws Exception {

    LoginModule loginModule = null;
    try {

      LoginModuleConfiguration[] loginModuleConfigurations = getLoginModuleConfigurations();
      for (int i = 0; i < loginModuleConfigurations.length; i++) {
        if (loginModuleConfigurations[i].getName().equals(name)) {
          loginModule = new LoginModuleImpl(loginModuleConfigurations[i]);
          break;
        }
      }

    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Error while get Login Module: " + name, e);
      throwException(ERROR_GET_LOGIN_MODULES_DATA, e);
    }
    return loginModule;

  }

  /**
   * Get all Login Modules from Userstore
   *
   * @return all Login Modules from userstore
   * @throws Exception
   */
  public LoginModule[] getLoginModules() throws Exception {

    LoginModule[] jmxLoginModules = null;
    try {

      LoginModuleConfiguration[] loginModules = getLoginModuleConfigurations();
      jmxLoginModules = new LoginModule[loginModules.length];
      for (int i = 0; i < loginModules.length; i++) {
        jmxLoginModules[i] = new LoginModuleImpl(loginModules[i]);
      }

    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Error while getting login modules from userstore", e);
      throwException(ERROR_GET_LOGIN_MODULES_DATA, e);
    }
    return jmxLoginModules;
  }

  /**
   * Replace Login Modules from Userstore Update is performed to all policy
   * configurations referencing an updated login module
   *
   * @param data new login modules
   * @throws Exception
   */
  public synchronized void saveLoginModules(CompositeData[] data) throws Exception {

    //comment locking checks since lock()/unlock() are deprecated
    //if (!isDataLockedFromCurrentUser()) {
    //  throwException( DATA_NOT_LOCKED );
    //}

    for (int i = 0; i < data.length; i++) {
      saveLoginModule(data[i]);
    }
  }

  /**
   * Add/Update Login Module from Userstore In case of update also update is
   * performed to all policy configurations referencing this login module
   *
   * @param data login module to update/add
   * @throws Exception
   */

  public synchronized void saveLoginModule(CompositeData data) throws Exception {

    //comment locking checks since lock()/unlock() are deprecated
    //if (!isDataLockedFromCurrentUser()) {
    //  throwException( DATA_NOT_LOCKED );
    //}

    try {
      LoginModule loginModule = new LoginModuleImpl(data);

      String newLMDisplayName = loginModule.getDisplayName();
      String newLMClassName = loginModule.getClassName();

      String newLMDescription = loginModule.getDescription();
      Map newLMOptions = AuthUtil.convertMapEntriesToMap(loginModule.getOptions());

      LoginModuleConfiguration newLoginModule = AuthUtil.makeRuntimeLoginModuleConfiguration(newLMDisplayName, newLMDescription, newLMClassName, newLMOptions);

      LoginModuleConfiguration[] oldLoginModules = getLoginModuleConfigurations();

      //stack is updated only when old login module is updated
      boolean isAuthStacksUpdateNeeded = false;
      //position where new LM will be inserted
      int newLoginModulePosition = oldLoginModules.length;
      int newLoginModulesArraySize = oldLoginModules.length + 1;

      //check if login module is being updated
      boolean isExisting = false;
      Map oldProps = new HashMap();

      for (int i = 0; i < oldLoginModules.length; i++) {
        if (oldLoginModules[i].getName().equals(newLMDisplayName)) {

          newLoginModulePosition = i;
          newLoginModulesArraySize = oldLoginModules.length;
          isAuthStacksUpdateNeeded = true;

          // needed for ChangeLog
          isExisting = true;
          oldProps = oldLoginModules[i].getOptions();
          break;

        }
      }

      LoginModuleConfiguration[] newLoginModules = new LoginModuleConfiguration[newLoginModulesArraySize];

      System.arraycopy(oldLoginModules, 0, newLoginModules, 0, oldLoginModules.length);
      newLoginModules[newLoginModulePosition] = newLoginModule;

      //login modules are ready for save in userstore
      saveLoginModulesToUserstore(newLoginModules);



      if (isAuthStacksUpdateNeeded) {
        updatePolicyConfigurations(oldLoginModules[newLoginModulePosition], newLMOptions);
      }

      {// ChangeLog here
        String oldProperties = oldProps.toString();
        String newProperties = newLMOptions.toString();
        String lmID = newLMDisplayName + "/" + newLMClassName + "/" + newLMDescription;
        if (isExisting) {
          SimpleLogger.log(INFO
              , CHANGE_LOG_CATEGORY
              , LOCATION
              , "ASJ.secsrv.000210"
              , "Login module [{0}] was updated. Old options [{1}], new options: [{2}]."
              , new Object[]{lmID, oldProperties, newProperties});
        } else {
          SimpleLogger.log(INFO
              , CHANGE_LOG_CATEGORY
              , LOCATION
              , "ASJ.secsrv.000214"
              , "Login module [{0}] was created, with options [{1}]."
              , new Object[]{lmID, newProperties});
        }
      }
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Error while saving login module to userstore", e);
      throwException(ERROR_SAVE_LOGIN_MODULE, e);
    }
  }

  /**
   * Remove Login Modules from userstore
   *
   * @param names names of login modules to be removed
   * @throws Exception
   */

  public synchronized void removeLoginModules(String[] names) throws Exception {

    for (int i = 0; i < names.length; i++) {
      removeLoginModule(names[i]);
    }

  }

  /**
   * Remove Login Module from userstore
   *
   * @param names names of login modules to be removed
   * @throws Exception
   */

  public synchronized void removeLoginModule(String name) throws Exception {

    // comment locking checks since lock()/unlock() are deprecated
    //if (!isDataLockedFromCurrentUser()) {
    //  throwException( DATA_NOT_LOCKED );
    //}

    LoginModuleConfiguration[] loginModuleConfigurations = null;
    try {
      loginModuleConfigurations = getLoginModuleConfigurations();
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Error while getting login module configurations", e);
      throwException(ERROR_GET_LOGIN_MODULES_DATA, e);
    }

    String displayName = null;
    String className = null;
    int positionToRemove = -1;

    for (int i = 0; i < loginModuleConfigurations.length; i++) {
      if (loginModuleConfigurations[i].getName().equals(name)) {
        displayName = loginModuleConfigurations[i].getName();
        className = loginModuleConfigurations[i].getLoginModuleClassName();
        positionToRemove = i;
        break;
      }
    }
    if (displayName == null || className == null) {
      throwException(LOGIN_MODULE_NOT_FOUND + ":" + name, null);
    }

    List<String> lstDisplayNameUsed = getDisplayNameUsed(displayName);
    //LM should not be removed if it is used in some stack by display name
    if (lstDisplayNameUsed.size() > 0) {
      LOCATION.warningT("Can not remove Login Module: " + name + ". It is in use by the following policy configurations: " + lstDisplayNameUsed);
      throw new Exception(LOGIN_MODULE_IS_USED + ": " + name);
    }

    int classNameCounter = 0;
    for (int i = 0; i < loginModuleConfigurations.length; i++) {
      if (loginModuleConfigurations[i].getLoginModuleClassName().equals(className)) {
        classNameCounter++;
      }
    }

    //className should not be removed if it is used in some stack
    if (classNameCounter < 2) { //only 1 Login Module is using this class
      if (isClassNameUsed(className)) {
        LOCATION.warningT("Login Module: " + name + " is in use and can not be removed");
        throw new Exception(LOGIN_MODULE_IS_USED + ": " + name);
      }
    }

    //make new array of login module configurations
    LoginModuleConfiguration[] newLoginModuleConfigurations = new LoginModuleConfiguration[loginModuleConfigurations.length - 1];

    for (int i = 0, newLMIndex = 0; i < loginModuleConfigurations.length; i++) {
      if (i != positionToRemove) {
        newLoginModuleConfigurations[newLMIndex++] = loginModuleConfigurations[i];
      }
    }

    try {
      saveLoginModulesToUserstore(newLoginModuleConfigurations);
    } catch (Exception e) {
      LOCATION.debugT("Error in remove login module: " + name);
      throwException(ERROR_REMOVE_LOGIN_MODULE + ":" + name, e);
    }


    SimpleLogger.log(INFO
        , CHANGE_LOG_CATEGORY
        , LOCATION
        , "ASJ.secsrv.000209"
        , "Login module [{0}] was removed"
        , new Object[]{name});
  }

  /**
   *
   * @return version of the jmx model
   */
  public int getVersion() {
    return VERSION;
  }

  /**
   *
   * @return status of the authschemes migration
   */
  public String getAuthschemesMigrationStatus() {

    String xmlFileName = InternalUMFactory.getAuthschemesXMLFileName();
    if (xmlFileName == null || xmlFileName.trim().length() == 0) {
      return AUTHSCHEMES_MIGRATION_COMPLETED;
    }

    //if xml file is not empty then no migration is done
    //now we check if there is an authscheme and a policy configuration with the same name
    try {

      if (!InternalUMFactory.getAuthSchemeFactory().isXMLFileValid()) {
        return AUTHSCHEMES_FILE_INVALID + xmlFileName;
      }

      String[] authschemeNamesFromXML = InternalUMFactory.getAuthSchemeFactory().getAuthschemeNamesFromXML();

      if (authschemeNamesFromXML == null || authschemeNamesFromXML.length == 0 ) {
        return AUTHSCHEMES_NOT_FOUND_IN_XML + xmlFileName;
      }

      String[] pcNames = getPolicyConfigurationNames();
      List<String> pcNamesList = Arrays.asList(pcNames);
      ArrayList<String> existingNames = new ArrayList<String>();
      for (String authschemeName : authschemeNamesFromXML) {
        if (pcNamesList.contains(authschemeName)) {
          existingNames.add(authschemeName);
        }
      }

      //no matching names - migration can be done
      if (existingNames.isEmpty()) {
        return AUTHSCHEMES_MIGRATION_PREPARED;
      } else {
        return AUTHSCHEMES_CONFLICTS + existingNames.toString();
      }

    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.009502", "Error in getting migration status");
      return AUTHSCHEMES_MIGRATION_PROBLEM + e.toString();
    }

  }

  public boolean isAuthschemesXMLFileUsed() {
    String xmlFileName = InternalUMFactory.getAuthschemesXMLFileName();
    if (xmlFileName == null || xmlFileName.trim().length() == 0) {
      return false;
    }
    return true;
  }

  public void migrateAuthschemes() throws Exception {

    String migrationStatus = getAuthschemesMigrationStatus();

    if (migrationStatus.equals(AUTHSCHEMES_MIGRATION_COMPLETED)) {
      LOCATION.debugT("Authschemes are already migrated.");
      throw new Exception(AUTHSCHEMES_MIGRATION_COMPLETED);
    }

    if (!migrationStatus.equals(AUTHSCHEMES_MIGRATION_PREPARED)) {
    	SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.009503", "Authschemes can not be migrated. {0}", migrationStatus);
    	throw new Exception(migrationStatus);
    }

    ArrayList<String> createdPolicyConfigurationNames = new ArrayList<String>();

    String authschemesFileName = InternalUMFactory.getAuthschemesXMLFileName();

    InputStream xmlInputStream = InternalUMFactory.getConfigExtended().readConfigFile(authschemesFileName);
    ArrayList<AuthschemeProps> authschemesArray = AuthSchemeFactory.getAuthschemesArray(xmlInputStream);

    xmlInputStream = InternalUMFactory.getConfigExtended().readConfigFile(authschemesFileName);
    ArrayList<AuthschemeRefProps> authschemesRefsArray = AuthSchemeFactory.getAuthschemeRefsArray(xmlInputStream);

    if (authschemesArray == null || authschemesArray.isEmpty()) {
      //normally we should not enter here
      //if no authschemes are defined the status must not be AUTHSCHEMES_MIGRATION_PREPARED
      SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.009504", "Authschemes can not be migrated. Authschemes not found in XML.");
      throw new Exception(AUTHSCHEMES_NOT_FOUND_IN_XML+authschemesFileName);
    }

    try {
      //create policy configurations for the authschemes
      {
        if (authschemesArray != null) {
          for (AuthschemeProps authschemeProps : authschemesArray) {
            if (authschemeProps == null) {
              continue;
            }
            // { name, template, frontendTarget, priority };
            String name = authschemeProps.getName();
            String template = authschemeProps.getTemplate();
            String frontendtarget = authschemeProps.getFrontendtarget();
            String priority = authschemeProps.getPriority();

            saveNewAuthscheme(name, template, priority, frontendtarget);
            createdPolicyConfigurationNames.add(name);
          }
        }
      }

      //create policy configurations for the authscheme references
      {
        if (authschemesRefsArray != null) {
          for (AuthschemeRefProps authschemeRefProps : authschemesRefsArray) {
            if (authschemeRefProps == null) {
              continue;
            }
            // { name, authschemeName};
            String name = authschemeRefProps.getName();
            String template = authschemeRefProps.getTemplate();

            saveNewAuthschemeReference(name, template);
            createdPolicyConfigurationNames.add(name);
          }
        }
      }

      IUMConfigAdmin configAdmin = InternalUMFactory.getConfigAdmin();
      configAdmin.setString(AUTH_XML_FILE_PROPERTY, "");

    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.009505", "Can not migrate the authschemes from XML file.");
      String[] pcNamesToRemove = new String[createdPolicyConfigurationNames.size()];
      createdPolicyConfigurationNames.toArray(pcNamesToRemove);
      removePolicyConfigurations(pcNamesToRemove);
      throw new Exception(AUTHSCHEMES_MIGRATION_SAVE_ERROR, e);
    }

  }

  /**
   * @param className
   * @return true if this class name is used by any policy configuration
   * @throws Exception
   */
  private boolean isClassNameUsed(String className) throws Exception {

    String[] policyConfigurationNames = getPolicyConfigurationNames();

    for (int i = 0; i < policyConfigurationNames.length; i++) {

      SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext(policyConfigurationNames[i]);
      if (policyConfiguration == null) {
        LOCATION.debugT("Invalid Policy Configuration " + policyConfigurationNames[i]);
        continue;
      }

      AuthenticationContext authentication = policyConfiguration.getAuthenticationContext();
      String template = authentication.getTemplate();
      if (template == null || template.length() == 0) { //There is no reference to template
        AppConfigurationEntry[] loginModules = authentication.getLoginModules();
        for (int lmIndex = 0; lmIndex < loginModules.length; lmIndex++) {
          if (loginModules[lmIndex].getLoginModuleName().equals(className)) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * @param displayName
   * @return List of all policy configurations names, which have reference to this LM in user store
   * @throws Exception
   */
  private List<String> getDisplayNameUsed(String displayName) throws Exception {

    List<String> lst = new ArrayList<String>();

    String[] policyConfigurationNames = getPolicyConfigurationNames();

    for (int i = 0; i < policyConfigurationNames.length; i++) {

      SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext(policyConfigurationNames[i]);
      if (policyConfiguration == null) {
        LOCATION.debugT("Invalid Policy Configuration " + policyConfigurationNames[i]);
        continue;
      }

      AuthenticationContext authentication = policyConfiguration.getAuthenticationContext();
      String template = authentication.getTemplate();
      if (template == null || template.length() == 0) { //There is no reference to template
        AppConfigurationEntry[] loginModules = authentication.getLoginModules();
        for (int lmIndex = 0; lmIndex < loginModules.length; lmIndex++) {
          if (loginModules[lmIndex].getLoginModuleName().equals(displayName)) {
            lst.add(authentication.getPolicyConfigurationName());
            break;
          }
        }
      }
    }
    return lst;
  }

  /**
   * @param template the policy configuration to check
   * @return true if this name is a template for some policy configuration,
   *         otherwise false
   * @throws Exception
   */

  private boolean isPolicyConfigurationReferenced(String template) throws Exception {

    String[] policyConfigurationNames = getPolicyConfigurationNames();

    for (int i = 0; i < policyConfigurationNames.length; i++) {

      SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext(policyConfigurationNames[i]);
      if (policyConfiguration == null) {
        LOCATION.debugT("Invalid Policy Configuration " + policyConfigurationNames[i]);
        continue;
      }
      String currentTemplate = policyConfiguration.getAuthenticationContext().getTemplate();
      if (currentTemplate == null) {
        continue;
      }
      if (currentTemplate.equals(template)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Replace Login Module Configurations from Userstore
   *
   * @param configurations new login module configurations
   */
  private void saveLoginModulesToUserstore(LoginModuleConfiguration[] configurations) {

    UserStoreFactory userStoreFactory = securityContext.getUserStoreContext();
    UserStoreConfiguration userStoreConfiguration = userStoreFactory.getActiveUserStore().getConfiguration();
    RuntimeUserStoreConfiguration runtimeUserStoreConfiguration = new RuntimeUserStoreConfiguration(userStoreConfiguration);
    runtimeUserStoreConfiguration.setLoginModules(configurations);
    userStoreFactory.updateUserStore(runtimeUserStoreConfiguration, userStoreFactory.getClass().getClassLoader());
  }

  /**
   * Get Login Module Configurations from userstore
   *
   * @return login module configurations from userstore
   */

  private LoginModuleConfiguration[] getLoginModuleConfigurations() {

    UserStoreFactory userStoreFactory = securityContext.getUserStoreContext();
    UserStoreConfiguration userStoreConfiguration = userStoreFactory.getActiveUserStore().getConfiguration();
    LoginModuleConfiguration[] loginModuleConfigurations = userStoreConfiguration.getLoginModules();
    return loginModuleConfigurations;

  }

  /**
   * Update Policy Configurations: Update the options for the authentication
   * stack entries where the same login module is used
   *
   * @param oldLoginModule the login module configuration before the update in
   *          userstore
   * @param newLoginModuleOptions the new login module options
   * @throws Exception
   */

  private void updatePolicyConfigurations(LoginModuleConfiguration oldLoginModule, Map newLoginModuleOptions) throws Exception {

    Map oldLMOptions = oldLoginModule.getOptions();
    String oldLoginModuleClassName = oldLoginModule.getLoginModuleClassName();
    String oldLoginModuleDisplayName = oldLoginModule.getName();

    String[] policyConfigurationNames = getPolicyConfigurationNames();

    int pcLen = policyConfigurationNames.length;
    for (int i = 0; i < pcLen; i++) {

      SecurityContext policyConfiguration = securityContext.getPolicyConfigurationContext(policyConfigurationNames[i]);
      if (policyConfiguration == null) {
        LOCATION.debugT("Invalid Policy Configuration " + policyConfigurationNames[i]);
        continue;
      }
      AuthenticationContext authentication = policyConfiguration.getAuthenticationContext();

      // do not update if template is referenced
      String template = authentication.getTemplate();
      if (template != null && template.length() > 0) {
        continue;
      }

      //this policy configuration is to be updated
      AppConfigurationEntry[] authStackEntries = authentication.getLoginModules();
      boolean isToBeUpdated = false;

      int lmLen = authStackEntries.length;
      for (int entryIndex = 0; entryIndex < lmLen; entryIndex++) {

        String entryClassName = authStackEntries[entryIndex].getLoginModuleName();
        Map entryOptions = authStackEntries[entryIndex].getOptions();

        if (oldLoginModuleClassName.equals(entryClassName)) {

          //compare options
          if (((entryOptions == null) && (oldLMOptions == null)) || ((oldLMOptions != null) && (entryOptions != null) && oldLMOptions.equals(entryOptions))) {
            //options should be replaced with the new ones
            authStackEntries[entryIndex] = new AppConfigurationEntry(entryClassName, authStackEntries[entryIndex].getControlFlag(), newLoginModuleOptions);
            isToBeUpdated = true;
          }
        }

        //It is a tricky code.
        //If the LM, which is subject of storing in UserStore is stored
        //before calling this method (it must be done in this way),
        //this entry will contain all its parent options as well
        else if (oldLoginModuleDisplayName.equals(entryClassName)) {
          isToBeUpdated = true;
        }
      }
      //save updated stack entries
      if (isToBeUpdated) {
        //Setting Login Modules does update(refresh) as well
        authentication.setLoginModules(authStackEntries);
      }
    }
  }

  /**
   * @return the current user
   */
  private String getCurrentUser() {

    String currentUser = null;
    try {

      ThreadSystem threadSystem = serviceContext.getCoreContext().getThreadSystem();
      ThreadContext threadContext = threadSystem.getThreadContext();

      SecurityContextObject securityContextObject = (SecurityContextObject) (threadContext.getContextObject(threadSystem.getContextObjectId(SECURITY_SERVICE)));
      SecuritySession securitySession = securityContextObject.getSession();

      java.security.Principal principal = securitySession.getPrincipal();
      currentUser = principal.getName();

    } catch (Exception e) {
      // Fallback for all cases in which user was not determined
      LOCATION.traceThrowableT(Severity.DEBUG, "Current user can not be determined!", e);
    }

    if (currentUser == null || currentUser.trim().length() == 0) {
      currentUser = FALLBACK_USER;
    }

    return currentUser;

  }

  /**
   * Checks if there is a lock for the current user
   *
   * @return true if lock is found, otherwise return false
   */
  private boolean isDataLockedFromCurrentUser() {
    //    String currentUser = getCurrentUser();
    //    try {
    //      locking.lock( LOCKING_NAMESPACE, LOCK_ARGUMENT,
    //          LockingConstants.MODE_CHECK_EXCLUSIVE_NONCUMULATIVE, currentUser );
    //      //lock is possible -> data is not locked
    //      return false;
    //    } catch (LockException le) {
    //      //data is already locked
    //      String collisionOwner = le.getCollisionUserName();
    //      if (collisionOwner.equals( currentUser )) {
    //        //data is locked by current user
    //        return true;
    //      } else {
    //        location
    //            .debugT( "Authentication Manager Data is already locked by another user" );
    //        return false;
    //      }
    //    } catch (Exception e) {
    //      location.traceThrowableT( Severity.DEBUG,
    //          "Exception in check AuthenticationManager data lock", e );
    //      return false;
    //    }
    return true;
  }

  /**
   * Method used for throwing an exception
   *
   * @param message exception message
   * @throws Exception
   */
  private void throwException(String message, Exception cause) throws Exception {

    Exception e = new Exception(message, cause);
    LOCATION.throwing(e);
    throw e;

  }
}