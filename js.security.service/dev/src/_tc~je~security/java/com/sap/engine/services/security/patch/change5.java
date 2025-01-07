
package com.sap.engine.services.security.patch;

import java.util.Iterator;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.interfaces.security.JACCSecurityRoleMappingContext;
import com.sap.engine.services.security.server.SecurityConfigurationPath;

import com.sap.engine.interfaces.security.SecurityRoleContext;
/**
 *  Migration of the default server role to UME server roles.
 * Roles that are migrated : administrators, guests, all.
 * 
 * @author Jako Blagoev
 */
public class Change5 implements Change {

  private static final String ACTIVE_USER_STORE = "UME User Store";
  
  private JACCSecurityRoleMappingContext mapper = null;
	
	public void run() throws Exception {
     try {
        mapper = (JACCSecurityRoleMappingContext) Class.forName("com.sap.security.core.server.ume.service.jacc.JACCSecurityRoleMapperContextImpl").newInstance();
     } catch (NoClassDefFoundError noClassErr) {
       throw new SecurityException(noClassErr.getMessage());
     } catch (ClassNotFoundException ex) {
       throw new SecurityException(ex.getMessage());     
     }
     
		 ConfigurationHandler configHandler = null;

		 try {
	  	 configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
	   	 Configuration configRead = configHandler.openConfiguration(SecurityConfigurationPath.ROLES_PATH, ConfigurationHandler.READ_ACCESS);
		   configRead = configRead.getSubConfiguration(ACTIVE_USER_STORE);		   
       
       try {
				 upgradeSecurityRole(configRead, SecurityRoleContext.ROLE_ADMINISTRATORS, JACCSecurityRoleMappingContext.UME_ADMINSTRATOR_SECURITY_ROLE);
       } catch (Exception se) {
         //$JL-EXC$
       }
       
       try {
				 upgradeSecurityRole(configRead, SecurityRoleContext.ROLE_GUESTS, JACCSecurityRoleMappingContext.UME_GUEST_SECURITY_ROLE);
       } catch (Exception se) {
         //$JL-EXC$
       }
       
	   	 try {
	   	   upgradeSecurityRole(configRead, SecurityRoleContext.ROLE_ALL, JACCSecurityRoleMappingContext.UME_EVERYONE_SECURITY_ROLE);	          
	   	 } catch (Exception se) {
          //$JL-EXC$
	   	 }
		 } catch (Exception e) {
		   throw new SecurityException(e.getMessage());
		 } finally {
		 	 if (configHandler != null) {
		     configHandler.rollback();
		     configHandler.closeAllConfigurations();
		 	 }
		 }
     
	}
	
  private void upgradeSecurityRole(Configuration roleConfig, String securityRole, String umeSecurityRole) throws Exception {
	  Configuration config = roleConfig.getSubConfiguration(securityRole);
		String[] users  = getKeys(config, true);
		String[] groups = getKeys(config, false);
		mapper.addUsersAndGroupsToJACCRole(umeSecurityRole, null, users, groups); 
  }
  
  private String[] getKeys(Configuration config, boolean user) throws Exception {
	  config = config.getSubConfiguration(((user) ? "users" : "groups"));	
		Set users = config.getAllConfigEntries().keySet(); 
		String[] usersArr = new String[users.size()];
		Iterator iter = users.iterator();
		int i = 0;
		while (iter.hasNext()) {
		  usersArr[i++] = (String) iter.next();
		}	
		return usersArr;  
  }
}
