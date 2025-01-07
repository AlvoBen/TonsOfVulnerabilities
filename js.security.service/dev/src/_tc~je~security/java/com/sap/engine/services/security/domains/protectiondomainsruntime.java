package com.sap.engine.services.security.domains;

import com.sap.engine.lib.security.domain.PermissionsFactory;
import com.sap.engine.lib.security.domain.ProtectionDomainFactory;
import com.sap.engine.services.security.restriction.Restrictions;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.boot.SystemProperties;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

import java.security.CodeSource;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

/**
 *  
 * Runtime changes of the permission collections and runtime monitoring of them
 * is managed here.
 *
 *  Another functionality is the synchronization of the permissions with the
 * database. The configuration manager is used for that.
 *
 * @version 7.0
 * @author  Ilia Kacarov
 * @author  Stephan Zlatarev
 */
public class ProtectionDomainsRuntime {

  private static final String PROP_KEY_accept_old_domain_name = "AcceptOldDomainNames";

  private boolean acceptOldFormatDomainNames = false;

  boolean isKnownPermissionsInitialized = false;

  private PermissionsStorage storage = null;
  private KnownPermissionsStorage knownPermissions = null;

  public ProtectionDomainsRuntime() {
    acceptOldFormatDomainNames = "true".equals(SecurityServerFrame.getServiceProperties().getProperty(PROP_KEY_accept_old_domain_name, "true"));
    if (Util.SEC_SRV_LOCATION.beDebug()) {
      Util.SEC_SRV_LOCATION.debugT(" [getComponentForOldDomainName].property1: " + PROP_KEY_accept_old_domain_name + "' has value: " + SecurityServerFrame.getServiceProperties().getProperty(PROP_KEY_accept_old_domain_name, "true"));
      Util.SEC_SRV_LOCATION.debugT(" [getComponentForOldDomainName].property : " + PROP_KEY_accept_old_domain_name + "' has value: " + SecurityServerFrame.getServiceProperties().getProperty(PROP_KEY_accept_old_domain_name));
      Util.SEC_SRV_LOCATION.debugT(" [getComponentForOldDomainName].acceptOldFormatDomainNames: " + acceptOldFormatDomainNames);
    }

    try {
      knownPermissions = new KnownPermissionsStorage();
    } catch (Exception e) {
      SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000030", "Unexpected problem, KnownPermissionsStorage class not instantiated. Security service is unavailable.");
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Unexpected problem, KnownPermissionsStorage class not instantiated. Security service is unavailable.", e);
    }

    try {
      storage = new PermissionsStorage();
      storage.isStartedForTheFirstTime();
    } catch (Exception e) {
      SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000014", "Unexpected problem, PermissionsStorage class not instantiated. Security service is unavailable.");
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Unexpected problem, PermissionsStorage class not instantiated. Security service is unavailable.", e);
    }
  }

  public PermissionsStorage getPermissionsStorage() {
    return storage;
  }


  public void grantPermission(String domain, String permission, String instance, String action) throws SecurityException {
    String componentName = domain;

    if (acceptOldFormatDomainNames) {
      componentName = getComponentForOldDomainName(domain);
      if (componentName == null) {// just wrong or unknown name
        componentName = domain;
      }
    }

    Restrictions.checkPermission(Restrictions.COMPONENT_PROTECTION_DOMAINS, Restrictions.RESTRICTION_GRANT_PERMISSION, componentName);
    checkPermission(componentName, permission, instance, action, "grantPermission(..): The permission's instance cannot be empty.", "grantPermission(..): The permission's action cannot be empty.");
    storage.grantPermission(componentName, permission, instance, action);
  }

  public void clearPermission(String domain, String permission, String instance, String action) throws SecurityException {
    String componentName = domain;

    if (acceptOldFormatDomainNames) {
      componentName = getComponentForOldDomainName(domain);
      if (componentName == null) {// just wrong or unknown name
        componentName = domain;
      }
    }

    Restrictions.checkPermission(Restrictions.COMPONENT_PROTECTION_DOMAINS, Restrictions.RESTRICTION_DENY_PERMISSION, componentName);
    checkPermission(componentName, permission, instance, action, "clearPermission(..): The permission's instance cannot be empty.", "clearPermission(..): The permission's action cannot be empty.");
    storage.denyPermission(componentName, permission, instance, action);
  }

  private final void checkPermission(String domain, String permission, String instance, String action, String instanceErrorMessage, String actionErrorMessage) throws SecurityException {
    if ((instance != null) && instance.trim().equals("")) {
      Util.SEC_SRV_LOCATION.errorT(instanceErrorMessage);
      throw new SecurityException(instanceErrorMessage);
    }
    if ((action != null) && action.trim().equals("")) {
      Util.SEC_SRV_LOCATION.errorT(actionErrorMessage);
      throw new SecurityException(actionErrorMessage);
    }
  }

  public Vector getAllKnownPermissions() {
    if ((knownPermissions != null) && isKnownPermissionsInitialized()) {
      return knownPermissions.getKnownPermissions();
    } else {
      Vector result = new Vector();
      String[] actionsArray = null;
      String[] namesArray = null;
      String[] defaultPermissionAliases = PermissionsFactory.getAliases();
      final String[] EMPTY_ARRAY = new String[0];

      for (int i = 0; i < defaultPermissionAliases.length; i++) {
        namesArray = PermissionsFactory.getPermissionNames(defaultPermissionAliases[i]);
        actionsArray = PermissionsFactory.getPermissionActions(defaultPermissionAliases[i]);

        result.add(new String[][] {
          new String[] { PermissionsFactory.getClassName(defaultPermissionAliases[i]) },
          (namesArray == null) ? EMPTY_ARRAY: namesArray,
          (actionsArray == null) ? EMPTY_ARRAY: actionsArray
        });
      }

      return result;
    }
  }

  public String[] getComponentNames() throws SecurityException {
    Vector temp = new Vector();
    Enumeration x = ProtectionDomainFactory.getAllMapedComponents();
    while (x.hasMoreElements()) {
      temp.add(x.nextElement());
    }

    String[] result = new String[temp.size()];
    temp.toArray(result);
     Arrays.sort(result, new Comparator() {public int compare(Object o1, Object o2) {
        try {
          return ((String) o1).compareTo((String) o2);
        } catch (Exception e) {
          //$JL-EXC$
          return 0;
        }
      }});

    return result;
  }

  public void addKnownPermission(String className, String[] names, String[] actions) throws SecurityException {
    if ((knownPermissions != null) && isKnownPermissionsInitialized()) {
      knownPermissions.addKnownPermission(className, names, actions);
    }
  }

  public void removeKnownPermission(String className, String[] names, String[] actions) throws SecurityException {
    if ((knownPermissions != null) && isKnownPermissionsInitialized()) {
      knownPermissions.removeKnownPermission(className, names, actions);
    }
  }

  public Vector getPermissionsForCodeSource(CodeSource cs) throws SecurityException {
    return storage.getStoredPermissionsForCodeSource(cs);
  }

  public Vector getPermissions(String component) throws SecurityException {
    return storage.getStoredPermissionsForComponent(component);
  }

  private final boolean isKnownPermissionsInitialized() {
    if (!isKnownPermissionsInitialized) {
      if (knownPermissions != null) {
        try {
          knownPermissions.isStartedForTheFirstTime();
          isKnownPermissionsInitialized = true;
        } catch (Exception e) {
          Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "All known permissions cannot be initialized.", e);
        }
      }
    }

    return isKnownPermissionsInitialized;
  }

  private static String getComponentForOldDomainName(String oldDomainName) {
    String codeSourceName = SystemProperties.getProperty("j2ee.engine.apps.folder") + oldDomainName;
    
    if (!codeSourceName.endsWith(".jar") || !codeSourceName.endsWith("/")) {
      codeSourceName = codeSourceName + "/"; // the internal represantation is /../apps/../<dir_name>/
    }
    
    String result = ProtectionDomainFactory.getComponentForCodeSource(codeSourceName);

    if (Util.SEC_SRV_LOCATION.beDebug()) {
      Util.SEC_SRV_LOCATION.debugT(" [getComponentForOldDomainName].old domain: " + oldDomainName);
      Util.SEC_SRV_LOCATION.debugT(" [getComponentForOldDomainName].codesource: " + codeSourceName);
      Util.SEC_SRV_LOCATION.debugT(" [getComponentForOldDomainName].component : " + result);
    }

    return result;
  }

}