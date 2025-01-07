package com.sap.security.core.server.ume.service.jacc;

import java.util.Iterator;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.interfaces.security.JACCMigrationContext;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

public class JACCMigrationContextImpl implements JACCMigrationContext {
  private static ConfigurationHandlerFactory configurationHandlerFactory = null;
  private static com.sap.security.core.server.ume.service.jacc.JACCSecurityRoleMapperContextImpl mapper =new com.sap.security.core.server.ume.service.jacc.JACCSecurityRoleMapperContextImpl();
  private static String BACKUP_APPS_SECURITY_PATH = "security/apps_migration";
  private static Location myLoc = Location.getLocation(JACCMigrationContextImpl.class);
  private static Category myCat = Category.getCategory(LoggingHelper.SYS_SECURITY, "JACC");
  
  private String policyConfiguration = null;

  private static final String SECURITY_CONFIG = "security";
  private static final String ROLES_CONFIG = "roles";
  private static final String UME_USER_STORE_CONFIG = "UME User Store";
  private static final String USERS_CONFIG = "users";
  private static final String GROUPS_CONFIG = "groups";
  private static final String REFERENCE_POLICY_CONFIG = "reference_policy";
  private static final String REFERENCE_ROLE_CONFIG = "reference_role";
  private final static String ROOT_POLICY_CONFIGURATION = "SAP-J2EE-Engine";
  
  private static final char[] FORBIDDEN_CONFIGNAME_CHARS = new char[] {'%','[',']','#','/'};

  public JACCMigrationContextImpl(String policyConfiguration) {
    this.policyConfiguration = policyConfiguration;
  }
  
  public void migratePolicyConfiguration(Configuration config) throws SecurityException {
    try {
      Configuration rolesConfig = null;
      try {
        Configuration secConfig = config.getSubConfiguration(SECURITY_CONFIG);
        Configuration secRoleConfig = secConfig.getSubConfiguration(ROLES_CONFIG);
        rolesConfig = secRoleConfig.getSubConfiguration(UME_USER_STORE_CONFIG);
      } catch (NameNotFoundException nnfe) {
        // $JL-EXC$ 
        //nothing to migrate
      }
      
      if (rolesConfig == null) {
        return;
      }
      String[] names = rolesConfig.getAllSubConfigurationNames();
      for (int i = 0; i < names.length; i++) {
        migrateSecurityRole(rolesConfig.getSubConfiguration(names[i]), policyConfiguration, names[i]);
      }
    } catch (Exception e) {
      throw new SecurityException(e.getMessage());
    }
  }
  
  public void migratePolicyConfiguration(String applicationName, String moduleName) throws SecurityException {
  	ConfigurationHandler configHandler = null;
  	try {
  	  configHandler = configurationHandlerFactory.getConfigurationHandler();
      Configuration rolesConfig = null;
      try {
        Configuration secConfig = configHandler.openConfiguration(BACKUP_APPS_SECURITY_PATH + "/" + encode(applicationName + "*" + moduleName), ConfigurationHandler.READ_ACCESS);
        secConfig = secConfig.getSubConfiguration(SECURITY_CONFIG);
        Configuration secRoleConfig = secConfig.getSubConfiguration(ROLES_CONFIG);
        rolesConfig = secRoleConfig.getSubConfiguration(UME_USER_STORE_CONFIG);
      } catch (NameNotFoundException nnfe) {
        // $JL-EXC$ 
        //nothing to migrate
      }
      
      if (rolesConfig == null) {
        return;
      }
      String[] names = rolesConfig.getAllSubConfigurationNames();
      for (int i = 0; i < names.length; i++) {
        migrateSecurityRole(rolesConfig.getSubConfiguration(names[i]), policyConfiguration, names[i]);
      }
    } catch (Exception e) {
      throw new SecurityException(e.getMessage());
    } finally {
      if (configHandler != null) {
      	try {
      	  configHandler.closeAllConfigurations();
      	} catch (Exception _) {
          // $JL-EXC$
      	}
      }
    }
  }
  
  protected void migrateSecurityRole(Configuration config, String policyConfiguration, String roleName) throws Exception {
  	final String methodName = "migrateSecurityRole(...)";
  	String umeRole = null;
    if (config.existsConfigEntry(REFERENCE_ROLE_CONFIG)) {
      String refPolicy = (String) config.getConfigEntry(REFERENCE_POLICY_CONFIG);
      if (refPolicy != null && !refPolicy.equals(ROOT_POLICY_CONFIGURATION)) {
        if (myLoc.beWarning()) {
          myLoc.warningT(methodName, "Not supported role reference '" + roleName + "'. It must refer the root security policy configuration.");
        }
        return;
      }
      umeRole = (String) config.getConfigEntry(REFERENCE_ROLE_CONFIG);	
    } else {
      String[] users  = getKeys(config, true);
      String[] groups = getKeys(config, false);
      if (users == null && groups == null) {
        return;
      } else {
        umeRole = mapper.migrateUsersAndGroupsToJACCRoleMappings(roleName, policyConfiguration, users, groups);
      }
    }

    mapper.migrateUMERoleToJACCRoleMappings(roleName, policyConfiguration, umeRole); 
  }
  
  
  protected String[] getKeys(Configuration config, boolean user) throws Exception {
    Set users = null;
    try {
      config = config.getSubConfiguration(((user) ? "users" : "groups"));    
      users = config.getAllConfigEntries().keySet(); 
    } catch (ConfigurationException ce) {
      // $JL-EXC$ 
      //nothing to migrate
      return null;
    }
      
    String[] usersArr = new String[users.size()];
    Iterator iter = users.iterator();
    int i = 0;
    while (iter.hasNext()) {
      usersArr[i++] = (String) iter.next();
    }  
    return usersArr;  
  }  

  public static void setConfigurationHandlerFactory(ConfigurationHandlerFactory configHandlerFactory) {
    configurationHandlerFactory = configHandlerFactory;
  }

  public static String encode(String in) {
	if (in == null || in.trim().equals("")) {
	  return null;
	}

	StringBuffer temp = new StringBuffer();
	for (int i = 0; i < in.length(); i++) {
	  int illegalCharPos = -1;
	  for (int j = 0; j < FORBIDDEN_CONFIGNAME_CHARS.length; j++) {
		if (in.charAt(i) == FORBIDDEN_CONFIGNAME_CHARS[j]) {
		  illegalCharPos = j;
		  break;
		}
	  }
	  if (illegalCharPos == -1) {
		if (in.charAt(i) == '$') {
		  temp.append(in.charAt(i));
		}
		temp.append(in.charAt(i));
	  } else {
		temp.append("$" + illegalCharPos);
	  }
	}

	String result = temp.toString();
	return result;
  }
}
