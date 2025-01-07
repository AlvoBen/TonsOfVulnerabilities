package com.sap.engine.services.security.migration;

import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;

public class UserManagementUtil {
  private static boolean init = false;
  private static String[] ADMINISTRATOR_GROUPS = null;
  private static String[] GUEST_GROUPS = null;
  private static final String[] EVERYONE_GROUPS = new String[]{"Everyone"};
   
  private static final String CONFIGURATION_PATH = "security/roles/UME User Store"; 
  
  private static final String ADMINISTRATOR_ROLE = "administrators";
  private static final String GUEST_ROLE = "guests";
  
  private static final String GROUPS_SUFFIX = "groups";
  
  public static boolean init() {    
    String anonymousUser = null;
    ConfigurationHandler handler = null;
   
    try {
      handler = MigrationFramework.getConfigurationHandler();
      Configuration config = handler.openConfiguration(CONFIGURATION_PATH, ConfigurationHandler.READ_ACCESS);
      Configuration adminConfig = config.getSubConfiguration(ADMINISTRATOR_ROLE);
      Set adminGroupsSet = adminConfig.getSubConfiguration(GROUPS_SUFFIX).getAllConfigEntries().keySet();      
      ADMINISTRATOR_GROUPS = toArray(adminGroupsSet);
      
      Configuration guestConfig = config.getSubConfiguration(GUEST_ROLE);
      Set guestGroupsSet = adminConfig.getSubConfiguration(GROUPS_SUFFIX).getAllConfigEntries().keySet();      
      GUEST_GROUPS = toArray(guestGroupsSet);    
    } catch (ConfigurationException e) {
      //scratch install
      return false;
    } finally {
      if (handler != null) {
        try {
          handler.closeAllConfigurations();
        } catch (ConfigurationException e1) {
          //$JL-EXC$
        }
      }
    }
        
    init = true;
    return true;    
  }
  
  
  public static String[] getAdministratorGroup() {
    if (!init) {
      init();
    }
    return ADMINISTRATOR_GROUPS;
  }
  
  
  public static String[] getGuestGroup() {
    if (!init) {
      init();
    }
    return GUEST_GROUPS;
  }
  
  public static String[] getEveryoneGroup() {
    return EVERYONE_GROUPS;
  }
  
  private static final String[] toArray(Set set) {
    Object[] arr = set.toArray();
    if (arr == null) {
      return null;
    }
    String[] result = new String[arr.length];
    for (int i = 0; i < arr.length; i++) {
      result[i] = (String) arr[i];
    }
    return result;
  }
}
