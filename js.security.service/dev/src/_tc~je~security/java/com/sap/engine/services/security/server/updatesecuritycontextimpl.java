package com.sap.engine.services.security.server;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.engine.interfaces.security.*;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.services.security.migration.ApplicationSecurityConfigurationAccessorImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import javax.security.auth.login.AppConfigurationEntry;

/**
 * Context for updating the security policy of deployed instances of components.
 *
 * @author  Ekaterina Zheleva
 * @version 6.40
 */
public class UpdateSecurityContextImpl implements UpdateSecurityContext {

  private static final Location LOCATION = Location.getLocation(UpdateSecurityContextImpl.class);
	
  private static final String OPTIONAL = "OPTIONAL";
  private static final String REQUIRED = "REQUIRED";
  private static final String REQUISITE = "REQUISITE";
  private static final String SUFFICIENT = "SUFFICIENT";
  
  private static final String AUTH_METHOD_PROP = "auth_method";

  private SecurityContext policyConfigurationContext = null;
  private SecurityRoleContext roleContext = null;
  private AuthenticationContext authenticationContext = null;

  private NodeList defaultSourceRoles = null;
  private NodeList defaultTargetRoles = null;
  private NodeList defaultSourceRoleMappings = null;
  private NodeList defaultTargetRoleMappings = null;
  //private NodeList defaultSourceAuthenticationStack = null;
  //private NodeList defaultTargetAuthenticationStack = null;
  
  private AppConfigurationEntry[] defaultSourceAuthStack;
  private String defaultSourceAuthTemplate;
  private Map defaultSourceAuthProperties = new HashMap();
  
  private AppConfigurationEntry[] defaultTargetAuthStack;
  private String defaultTargetAuthTemplate;
  private Map defaultTargetAuthProperties = new HashMap();
  
  public ApplicationSecurityConfigurationAccessor getApplicationSecurityConfigigurationAccessor(String applicationName) {
  	return new ApplicationSecurityConfigurationAccessorImpl(applicationName);
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setSecurityContext(SecurityContext ctx)
   */
  public void setSecurityContext(SecurityContext ctx) {
    this.policyConfigurationContext = ctx;
    this.roleContext = policyConfigurationContext.getAuthorizationContext().getSecurityRoleContext();
    this.authenticationContext = policyConfigurationContext.getAuthenticationContext();
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultSourceRoles(NodeList defaultSourceRoles)
   */
  public void setDefaultSourceRoles(NodeList sourceRoles) {
    this.defaultSourceRoles = sourceRoles;
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultSourceRoleMappings(NodeList defaultSourceRoleMappings)
   */
  public void setDefaultSourceRoleMappings(NodeList sourceRoleMappings) {
    this.defaultSourceRoleMappings = sourceRoleMappings;
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultTargetRoles(NodeList defaultTargetRoles)
   */
  public void setDefaultTargetRoles(NodeList targetRoles) {
    this.defaultTargetRoles = targetRoles;
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultTargetRoleMappings(NodeList defaultTargetRoleMappings)
   */
  public void setDefaultTargetRoleMappings(NodeList targetRoleMappings) {
    this.defaultTargetRoleMappings = targetRoleMappings;
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultSourceAuthenticationStack(NodeList list)
   */
  public void setDefaultSourceAuthenticationStack(NodeList list) {
    //this.defaultSourceAuthenticationStack = list;
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultTargetAuthenticationStack(NodeList list)
   */
  public void setDefaultTargetAuthenticationStack(NodeList list) {
    //this.defaultTargetAuthenticationStack = list;
  }
  
  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultSourceAuthenticationProperty(String, String)
   */
  public void setDefaultSourceAuthenticationProperty(String key, String value) {
    this.defaultSourceAuthProperties.put(key, value);
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Added new authentication source property with key: {0} and value: {1}", new Object[]{key, value});
    }
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultSourceAuthenticationStack(AppConfigurationEntry[])
   */
  public void setDefaultSourceAuthenticationStack(AppConfigurationEntry[] entries) {
    this.defaultSourceAuthStack = entries;
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultSourceAuthenticationTemplate(String)
   */
  public void setDefaultSourceAuthenticationTemplate(String template) {
  	// normalize template
  	template = template.toLowerCase();
  	if (template.indexOf('-') > -1) {
  	  template = template.replace('-', '_');
  	}
  	this.defaultSourceAuthTemplate = template;
  	
  	if (LOCATION.beDebug()) {
  	  LOCATION.debugT("Default source authentication template is set to: {0}", new Object[]{template});
    }
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultTargetAuthenticationProperty(String, String)
   */
  public void setDefaultTargetAuthenticationProperty(String key, String value) {
	  this.defaultTargetAuthProperties.put(key, value);
	  if (LOCATION.beDebug()) {
	    LOCATION.debugT("Added new authentication target property with key: {0} and value: {1}", new Object[]{key, value});
	  }
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultTargetAuthenticationStack(AppConfigurationEntry[])
   */
  public void setDefaultTargetAuthenticationStack(AppConfigurationEntry[] entries) {
    this.defaultTargetAuthStack = entries;
  }

  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#setDefaultTargetAuthenticationTemplate(String)
   */
  public void setDefaultTargetAuthenticationTemplate(String template) {
    this.defaultTargetAuthTemplate = template;
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Default target authentication template is set to: {0}", new Object[]{template});
    }
  }
  
  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#updateAuthentication()
   */
  public void updateAuthentication() {
    try {
      if (isUpdateNeeded()) {
        // the previous configuration is not changed -> apply the new configuration
        if (LOCATION.beDebug()) {
          LOCATION.debugT("The previous configuration is not changed -> apply the new configuration.");
        }
        applyNewConfiguration();
      }
    	
      // Apply the new authentication properties
      Set keys = defaultTargetAuthProperties.keySet();
      for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
    	  String key = (String) iterator.next();
    	  String value = (String)defaultTargetAuthProperties.get(key);
    	  if (value != null) {
    		  authenticationContext.setProperty(key, value);
    	  }	
      }
    } catch (Exception e) {
      throw new SecurityException("Unexpected exception while updating the authentication context of the security policy.", e);
    }
  }

  private boolean isUpdateNeeded() {
    boolean result = false;
	
  	String template;
  	String authMethod;
  	AppConfigurationEntry[] currentStack;
  	try {
  		template = authenticationContext.getTemplate();
  		if (LOCATION.beDebug()) {
  		  LOCATION.debugT("Resolved template: {0}", new Object[]{template});
  		}
  	} catch (Exception e) {
  		LOCATION.debugT("Failed to resolve current template. Reason: {0}", new Object[]{e});
  		template = null;
  	}
  	try {
  		authMethod = authenticationContext.getProperty(AUTH_METHOD_PROP);
  		if (LOCATION.beDebug()) {
  		  LOCATION.debugT("Resolved authentication method: {0}", new Object[]{authMethod});
  		}
  	} catch (Exception e) {
  	  LOCATION.debugT("Failed to resolve authentication method property. Reason: {0}", new Object[]{e});
  	  authMethod = null;
  	}
    try {
    	currentStack = authenticationContext.getLoginModules();
    } catch (Exception e) {
      LOCATION.debugT("Failed to resolve current authentication stack. Reason: {0}", new Object[]{e});
    	currentStack = new AppConfigurationEntry[0];
    }
		
  	if (template != null && template.length() > 0) {
  	  // the current configuration is template
  	  LOCATION.debugT("The current configuration is template with name: {0}", new Object[]{template});
  		if (defaultSourceAuthTemplate != null && defaultSourceAuthTemplate.length() > 0) {
  			// the previous configuration was template - check equals
  			if (template.equalsIgnoreCase(defaultSourceAuthTemplate)) {
  			  // there arn't custom changes -> apply the new configuration
  			  LOCATION.debugT("The current template is equal to the previous template: {0}", new Object[]{template});
  				result = true;
  			}
  		}
  	} 
  	else if (currentStack != null && currentStack.length > 0) {
  		// the current configuration is authentication stack
  	  LOCATION.debugT("The current configuration is authentication stack.");
  		AppConfigurationEntry[] resolvedSourceStack = getResolvedAuthenticationStack(defaultSourceAuthStack);
  		
  		if (stackEquals(currentStack, resolvedSourceStack)) {
  			// there arn't custom changes -> apply the new configuration
  		  LOCATION.debugT("The current authentication stack is equal to the previous authentication stack.");
  			result = true;
  		}
  	}
  	else {
  		// the current configuration is empty check previous configuration
  	  LOCATION.debugT("The current authentication configuration is empty.");
  		
  	  if ((defaultSourceAuthStack == null || defaultSourceAuthStack.length < 1) &&
  			(defaultSourceAuthTemplate == null || defaultSourceAuthTemplate.length() < 1)) {
  		  LOCATION.debugT("The current empty configuration is equal to the previous configuration.");
  		  result = true;
  		}
  	}
  	return result;
  }
  
  private void applyNewConfiguration() {
	  AppConfigurationEntry[] resolvedTargetStack = getResolvedAuthenticationStack(defaultTargetAuthStack);
	  
	  if (resolvedTargetStack.length > 0) {
		  authenticationContext.setLoginModules((AppConfigurationEntry[])null); //clean the stack
		  authenticationContext.setLoginModules(resolvedTargetStack);
		  LOCATION.debugT("The new authentication stack is applyed.");
	  }
	  else if (defaultTargetAuthTemplate != null && defaultTargetAuthTemplate.length() > 0) {
		  authenticationContext.setLoginModules(defaultTargetAuthTemplate);
		  LOCATION.debugT("The new authentication template is applyed: {0}", new Object[]{defaultTargetAuthTemplate});
	  }
	  else {
	    LOCATION.debugT("There aren't new configuration for applying.");
	  }
  }
  
  private boolean stackEquals(AppConfigurationEntry[] stack, AppConfigurationEntry[] stackToCompare) {
	  if (stack == null && stackToCompare == null) {
		  return true;
	  } 
	  else if (stack == null || stackToCompare == null) {
	    return false;
	  }
	  
	  if (stack.length == stackToCompare.length) {
		  for(int i = 0; i < stack.length; i++) {
	      AppConfigurationEntry stackEntry = stack[i];
	      AppConfigurationEntry stackToCompareEntry = stackToCompare[i];	
					
	      Map sourceOptions = stackEntry.getOptions();
	      if(sourceOptions == null) {
	        sourceOptions = new HashMap();
				}
	      Map currOptions = stackToCompareEntry.getOptions();
	      if(currOptions == null) {
	        currOptions = new HashMap();
				}
						
	      if(!stackEntry.getLoginModuleName().equals(stackToCompareEntry.getLoginModuleName()) || 
	         stackEntry.getControlFlag() != stackToCompareEntry.getControlFlag() || 
	         !sourceOptions.equals(currOptions)) {
	        return false;
	      }
		  }
		  return true;
	  }
	  
	  return false;
  }
  
  /**
   * @see com.sap.engine.interfaces.security.UpdateSecurityContext#updateAuthorization()
   */
  public void updateAuthorization() {
    try {
      SecurityRole[] currentRoles;
      SecurityRole role = null;
      try {
        currentRoles = roleContext.listSecurityRoles();
      } catch (Exception e) {
        currentRoles = new SecurityRole[0];
      }

      if (defaultSourceRoles != null && defaultSourceRoles.getLength() > 0) {
        Node sourceNode = null;
        Node mappingsNode = null;
        String sourceRoleName = null;
        for (int i = 0; i < defaultSourceRoles.getLength(); i++) {
          sourceNode = defaultSourceRoles.item(i);
          sourceRoleName = getRoleName(sourceNode);
          if (sourceRoleName != null) {
            mappingsNode = getDefaultRoleMappings(sourceRoleName, defaultSourceRoleMappings);
            if (mappingsNode != null) {
              sourceNode = mappingsNode;
            }
            role = getRole(sourceRoleName, currentRoles);
            if (role != null) {
              if (!isSourceMappingChanged(role, defaultSourceRoles, defaultSourceRoleMappings) && getDefaultRoleMappings(sourceRoleName, defaultTargetRoles) == null) {
                roleContext.removeSecurityRole(sourceRoleName);
              }
            }
          }
        }
      }

      if (defaultTargetRoles != null && defaultTargetRoles.getLength() > 0) {
        Node targetNode = null;
        Node mappingsNode = null;
        String targetRoleName = null;
        for (int j = 0; j < defaultTargetRoles.getLength(); j++) {
          targetNode = defaultTargetRoles.item(j);
          targetRoleName = getRoleName(targetNode);
          if (targetRoleName != null) {
            mappingsNode = getDefaultRoleMappings(targetRoleName, defaultTargetRoleMappings);
            if (mappingsNode != null) {
              targetNode = mappingsNode;
            }
            role = getRole(targetRoleName, currentRoles);
            if (role == null) {
              addSecurityRole(targetNode, false);
            } else {
              if (!isSourceMappingChanged(role, defaultSourceRoles, defaultSourceRoleMappings) && isMappingChanged(role, targetNode)) {
                addSecurityRole(targetNode, true);
              }
            }
          } else {
            log("No role in " + targetNode);
          }
        }
      }

//      // JACC deployment
//      try {
//       RoleDeployment deployRoles = new RoleDeployment(this.policyConfigurationContext, policyConfigurationContext.getPolicyConfigurationName(), null, defaultTargetRoles, defaultTargetRoleMappings, true);
//        deployRoles.deployWebApplication();
//      } catch (NoClassDefFoundError error) {
//       error.printStackTrace();
//      }

    } catch (Exception e) {
      throw new SecurityException("Unexpected exception while updating the authorization context of the security policy.", e);
    }
  }

  private Node getDefaultRoleMappings(String roleName, NodeList defaultRoleMappings) {
    if (defaultRoleMappings != null && defaultRoleMappings.getLength() > 0) {
      Node mappingsNode = null;
      for (int i = 0; i < defaultRoleMappings.getLength(); i++) {
        mappingsNode = defaultRoleMappings.item(i);
        if (roleName.equals(getRoleName(mappingsNode))) {
          return mappingsNode;
        }
      }
    }
    return null;
  }

  private void addSecurityRole(Node roleNode, boolean isExistent) throws SecurityException {
    String roleName = getRoleName(roleNode);
    if (isExistent) {
      roleContext.removeSecurityRole(roleName);
    }
    String referencedRoleName = getReferencedRoleName(roleNode);
    if (referencedRoleName != null) {
      roleContext.addSecurityRoleReference(roleName, SecurityContext.ROOT_POLICY_CONFIGURATION, referencedRoleName);
    } else {
      roleContext.addSecurityRole(roleName);
    }
  }

  private SecurityRole getRole(String roleName, SecurityRole[] currentRoles) {
    if (currentRoles != null && currentRoles.length > 0) {
      SecurityRole role = null;
      for (int i = 0; i < currentRoles.length; i++) {
        role = currentRoles[i];
        if (roleName.equals(role.getName())) {
          return role;
        }
      }
    }
    return null;
  }

  private boolean isMappingChanged(SecurityRole role, Node node) {
    if (role.getReference().length > 0) {
      String referencedRoleName = role.getReference()[1];
      if (referencedRoleName.equals(getReferencedRoleName(node))) {
        log("Role reference of " + getRoleName(node) + " to " + referencedRoleName + " is not changed");
        return false;
      }
    } else if (getReferencedRoleName(node) == null) {
      log("Role mapping of " + getRoleName(node) + " is not changed");
      return false;
    }
    return true;
  }

  private boolean isSourceMappingChanged(SecurityRole role, NodeList defaultRoles, NodeList defaultRoleMappings) {
    if (defaultRoles != null && defaultRoles.getLength() > 0) {
      Node sourceNode = null;
      Node mappingsNode = null;
      for (int i = 0; i < defaultRoles.getLength(); i++) {
        sourceNode = defaultRoles.item(i);
        if (role.getName().equals(getRoleName(sourceNode))) {
          mappingsNode = getDefaultRoleMappings(role.getName(), defaultRoleMappings);
          return isMappingChanged(role, (mappingsNode != null) ? mappingsNode : sourceNode);
        }
      }
    }
    return true;
  }

  private static String getRoleName(Node node) {
    return getTagValue(node, "role-name");
  }

  private static String getReferencedRoleName(Node node) {
    return getTagValue(node, "server-role-name");
  }

  private static String getLoginModuleName(Node node) {
    return getTagValue(node, "login-module-name");
  }

  private static AppConfigurationEntry.LoginModuleControlFlag getControlFlag(Node node) {
    String flag = getTagValue(node, "flag");
    if (SUFFICIENT.equalsIgnoreCase(flag)) {
      return AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
    } else if (REQUIRED.equalsIgnoreCase(flag)) {
      return AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
    } else if (REQUISITE.equalsIgnoreCase(flag)) {
      return AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
    } else if (OPTIONAL.equalsIgnoreCase(flag)) {
      return AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
    } else {
      throw new IllegalArgumentException("Invalid flag name " + flag + " is specified in [login-module] with name " + getLoginModuleName(node) + ".");
    }
  }

  /**
   * Logs debug messages.
   *
   * @param message   the message to be logged.
   */
  public static void log(String message) {
    LOCATION.logT(Severity.INFO, message);
  }

  private static boolean areMapsEqual(Map map1, Map map2) {
    if (map1 == null && map2 == null) {
      return true;
    }
    if (map1 == null && map2 != null || map1 != null && map2 == null) {
      return false;
    }
    return map1.equals(map2);
  }

  private final static String getTagValue(Node node, String tagName) {
    try {
      NodeList list = node.getChildNodes();
      for (int i = 0; i < list.getLength(); i++) {
        if (tagName.equals(list.item(i).getNodeName())) {
          return list.item(i).getChildNodes().item(0).getNodeValue();
        }
      }
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.INFO, "", e);
    }

    return null;
  }
  
  //If there is match by display name do the following:
  //Skip all options defined in the xml and use all options from LM in User Store with the same display name
  private AppConfigurationEntry[] getResolvedAuthenticationStack(AppConfigurationEntry[] entries) {
  	List lst = new ArrayList();		
	if(entries != null) {		
		UserStoreConfiguration userStoreConfiguration = authenticationContext.getAuthenticationUserStore().getConfiguration();
			
		LoginModuleConfiguration[] definedLoginModules = userStoreConfiguration.getLoginModules();
		int dlmLen = 0;
		if(definedLoginModules!=null) {			
			dlmLen = definedLoginModules.length;
		}
	  	
		for (int i = 0; i < entries.length; i++) {
			AppConfigurationEntry entry = entries[i];
			
			String appLMName = entry.getLoginModuleName();
			if (appLMName == null || appLMName.length() == 0) {
			  throw new IllegalArgumentException("Login module name is not specified in the [login-module] tag.");
			}
			    
			Map appOptions = entry.getOptions();
			if(appOptions == null) {
			  appOptions = new HashMap();
			}			
				
			boolean lmDNIsFoundInUserStore = false;
			for(int k=0; !lmDNIsFoundInUserStore && (k < dlmLen); k++) {
				//Exit only if there is a match by Display Name
				LoginModuleConfiguration userstoreLM = definedLoginModules[k];
		      	
				Map userstoreLMOptions = userstoreLM.getOptions();
				if(userstoreLMOptions == null) {
					userstoreLMOptions = new HashMap();
				}			
						
				//Check whether this LM is a reference to the User Store's LM
				//It is true if this LM has a class name equals to display name property in User Store
				if(appLMName.equals(userstoreLM.getName())) {		
					lmDNIsFoundInUserStore = true;
					Map options = new HashMap();
					if(!userstoreLMOptions.isEmpty()){
					  options.putAll(userstoreLMOptions);
					}			
					entry = new AppConfigurationEntry(appLMName, entry.getControlFlag(), options);															
				}
				else if(appLMName.equals(userstoreLM.getLoginModuleClassName())) {            						
					//only if all options are the same put display name instead of class name
					if(userstoreLMOptions.equals(appOptions)) { 
						//Found a reference 						  
						entry = new AppConfigurationEntry(userstoreLM.getName(), entry.getControlFlag(), appOptions);                  						  
					}						                        
				}          				
			}			        		  			
			lst.add(entry);			
		}
	}
	
	return (AppConfigurationEntry[])lst.toArray(new AppConfigurationEntry[lst.size()]);
  }	

}
