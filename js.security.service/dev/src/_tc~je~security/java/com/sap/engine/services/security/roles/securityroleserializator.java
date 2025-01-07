package com.sap.engine.services.security.roles;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.interfaces.security.AuthorizationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityRole;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.exceptions.StorageException;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.tc.logging.Severity;

public class SecurityRoleSerializator {

  private final static String DESCRIPTION = "description";
  private final static String GROUPS_LIST = "groups";
  private final static String REFERENCE_POLICY_CONFIGURATION = "reference_policy";
  private final static String REFERENCE_SECURITY_ROLE = "reference_role";
  private final static String USERS_LIST = "users";
  protected final static String RUN_AS_IDENTITY = "run-as_identity";
  protected final static String RUN_AS_GENERATION_POLICY = "run-as_generation_policy";


  static boolean existsSecurityRole(Configuration configuration, String name) {
    try {
      return (configuration.getSubConfiguration(name) != null);
    } catch (Exception e) {
      return false;
    }
  }

  static void removeSecurityRole(Configuration configuration, String name) {
    try {
      configuration.deleteConfiguration(name);
    } catch (Exception e) {
      if (e instanceof StorageLockedException) {
        throw (StorageLockedException) e;
      }
      throw new StorageException("Cannot remove security role: " + name, e);
    }
  }

  static void storeSecurityRole(Configuration configuration, SecurityRole role) {
    Configuration sub = null;
    String runAsIdentity = null;
    Byte runAsGenerationPolicy = null;
    try {
      sub = configuration.getSubConfiguration(role.getName());
      if (sub.existsConfigEntry(RUN_AS_IDENTITY)) {
        runAsIdentity = (String) sub.getConfigEntry(RUN_AS_IDENTITY);
      }
      if (sub.existsConfigEntry(RUN_AS_GENERATION_POLICY)) {
        runAsGenerationPolicy = (Byte) sub.getConfigEntry(RUN_AS_GENERATION_POLICY);
      }
      sub.deleteAllConfigEntries();
      sub.deleteAllSubConfigurations();
    } catch (Exception e) {
      if (e instanceof StorageLockedException) {
        throw (StorageLockedException) e;
      }

      try {
        sub = configuration.createSubConfiguration(role.getName());
      } catch (Exception ee) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "storeSecurityRole", ee);

        if (ee instanceof StorageLockedException) {
          throw (StorageLockedException) ee;
        }
        throw new StorageException("Cannot store security role: " + role.getName(), e);
      }
    }

    try {
      if (role instanceof SecurityRoleReference) {
        String[] ref = role.getReference();

        sub.addConfigEntry(REFERENCE_POLICY_CONFIGURATION, ref[0]);
        sub.addConfigEntry(REFERENCE_SECURITY_ROLE, ref[1]);
      } else {
        Configuration list = null;
        String[] groups = role.getGroups();
        String[] users = role.getUsers();

        sub.addConfigEntry(DESCRIPTION, (role.getDescription() != null) ? role.getDescription() : "");

        list = sub.createSubConfiguration(USERS_LIST);
        for (int i = 0; i < users.length; i++) {
          list.addConfigEntry(users[i], "");
        }

        list = sub.createSubConfiguration(GROUPS_LIST);
        for (int i = 0; i < groups.length; i++) {
          list.addConfigEntry(groups[i], "");
        }
      }

      if (runAsIdentity != null) {
        sub.addConfigEntry(RUN_AS_IDENTITY, runAsIdentity);
      }
      if (runAsGenerationPolicy != null) {
        sub.addConfigEntry(RUN_AS_GENERATION_POLICY, runAsGenerationPolicy);
      }
    } catch (Exception e) {
      if (e instanceof StorageLockedException) {
        throw (StorageLockedException) e;
      }
      throw new StorageException("Cannot store security role: " + role.getName(), e);
    }
  }

  static SecurityRole loadSecurityRole(SecurityContext root, Configuration configuration, String name) {
    Configuration sub = null;
    //Map map = null;
    SecurityRole role = null;

    try {
      sub = configuration.getSubConfiguration(name);
      String runAsIdentity = null;
      if (sub.existsConfigEntry(RUN_AS_IDENTITY)) {
        runAsIdentity = (String) sub.getConfigEntry(RUN_AS_IDENTITY);
      }
      if (sub.existsConfigEntry(REFERENCE_POLICY_CONFIGURATION)) {
        String policyReference = (String) sub.getConfigEntry(REFERENCE_POLICY_CONFIGURATION);
        String roleReference = (String) sub.getConfigEntry(REFERENCE_SECURITY_ROLE);
        String path = configuration.getPath();
        int lastIndex = path.lastIndexOf('/');
        String userStore = path.substring(lastIndex + 1);
        AuthorizationContext authorizationContext = root.getPolicyConfigurationContext(policyReference).getAuthorizationContext();
        SecurityRoleContextImpl roleContext = (SecurityRoleContextImpl) authorizationContext.getSecurityRoleContext(userStore);
        role = roleContext.readSecurityRole(roleReference);
        if (runAsIdentity == null) {
          return new SecurityRoleReference(name, policyReference, role);
        } else {
          return new SecurityRoleReference(name, policyReference, role, runAsIdentity);
        }
      } else {
        if (runAsIdentity == null) {
          role = new SecurityRoleImpl(name, sub.getSubConfiguration(USERS_LIST).getAllConfigEntryNames(),
                                          sub.getSubConfiguration(GROUPS_LIST).getAllConfigEntryNames());
        } else {
          role = new SecurityRoleImpl(name, sub.getSubConfiguration(USERS_LIST).getAllConfigEntryNames(),
                                          sub.getSubConfiguration(GROUPS_LIST).getAllConfigEntryNames(), runAsIdentity);
        }
        role.setDescription((String) sub.getConfigEntry(DESCRIPTION));
      }
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "loadSecurityRole", e);
    }

    return role;
  }

  static void refreshSecurityRole(SecurityContext root, Configuration configuration, SecurityRole role){
    Configuration sub = null;
    //Map map = null;
    try {
      sub = configuration.getSubConfiguration(role.getName());
      if (sub.existsConfigEntry(REFERENCE_POLICY_CONFIGURATION)) {
        // nothing to do
      } else {
        if (role instanceof SecurityRoleImpl) {
          ((SecurityRoleImpl)role).setUsers(sub.getSubConfiguration(USERS_LIST).getAllConfigEntryNames());
          ((SecurityRoleImpl)role).setGroups(sub.getSubConfiguration(GROUPS_LIST).getAllConfigEntryNames());
        } else if (role instanceof SecurityRoleReference){
          SecurityRoleImpl reference = new SecurityRoleImpl(role.getName());
          reference.setContext((SecurityRoleContextImpl) root.getAuthorizationContext().getSecurityRoleContext());
          ((SecurityRoleReference) role).setReference(role.getName(), reference);
        }
      }
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "refreshSecurityRole", e);
    }
  }

  static void setRunAsIdentityToSecurityRole(Configuration configuration, SecurityRole role, String principal) {
    Configuration sub = null;

    try {
      sub = configuration.getSubConfiguration(role.getName());
      if (sub.existsConfigEntry(RUN_AS_IDENTITY)) {
        sub.modifyConfigEntry(RUN_AS_IDENTITY, principal);
      } else {
        sub.addConfigEntry(RUN_AS_IDENTITY, principal);
      }
    } catch (Exception e) {
      if (e instanceof StorageLockedException) {
        throw (StorageLockedException) e;
      }
      throw new StorageException("Cannot set run-as identity for security role: " + role.getName(), e);
    }
  }

  static void setRunAsAccountGenerationPolicy(Configuration configuration, SecurityRole role, byte type) {
    Configuration sub = null;

    try {
      sub = configuration.getSubConfiguration(role.getName());
      if (sub.existsConfigEntry(RUN_AS_GENERATION_POLICY)) {
        sub.modifyConfigEntry(RUN_AS_GENERATION_POLICY, new Byte(type));
      } else {
        sub.addConfigEntry(RUN_AS_GENERATION_POLICY, new Byte(type));
      }
    } catch (Exception e) {
      if (e instanceof StorageLockedException) {
        throw (StorageLockedException) e;
      }
      throw new StorageException("Cannot set run-as account generation policy for security role: " + role.getName(), e);
    }
  }

}