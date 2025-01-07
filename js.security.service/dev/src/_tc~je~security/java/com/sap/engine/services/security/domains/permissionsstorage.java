/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.domains;

import com.sap.engine.services.security.Util;
import com.sap.engine.frame.core.configuration.*;
import static com.sap.engine.lib.security.domain.ProtectionDomainFactory.dump;
import static com.sap.engine.lib.security.domain.ProtectionDomainFactory.inDebug;
import com.sap.engine.lib.security.domain.PermissionsFactory;
import com.sap.engine.lib.security.domain.ProtectionDomainFactory;
import com.sap.engine.lib.security.EnginePermission;
import com.sap.engine.lib.security.VeilPermission;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;



//import java.security.*;
import java.security.cert.Certificate;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Permission;

import java.util.*;
import java.io.IOException;
import java.net.URL;

/**
 *
 * This class manages the persistency of the granted and the known protection domain permissions
 * from the cluster. It is initialized from ProtectionDomainsRuntime class.
 * Saved permissions formats:
 *
 * root := "protected_domains/"
 * group permissions roots := root + {"all/" | "bin_system/" | "apps_dir/"}
 * special roots:= "known_permissions/" | "domains/"
 * known permission := root + "known_permissions/" + <permission_class> + {"actions" | "instances"} + {<val1>, ..}
 * domain permission : root + "domains/" + <domain name> + {"granted" | "denied"} + {<targets1, ..> [+ {<action1, ..>]}}
 *
 * @author Ilia Kacarov
 */
public class PermissionsStorage extends PermissionsStorageUtils {

  private static final String DOMAINS = "domains";
  private static final String GRANTED_PERMISSIONS = "granted";
  private static final String DENIED_PERMISSIONS = "denied";

  private static final String GRANTED_TO_ALL_PERMISSIONS = "all";
  private static final String APPS_DIR_PERMISSIONS = "apps_dir";
  private static final String BIN_PERMISSIONS = "bin_system";

  private static Vector root_bin_permissions = null;
  private static Vector apps_dir_permissions = null;

  private static final String PERMISSION_NOT_DENIED = "PERMISSION_NOT_DENIED";
  private static final String CONFIGURATION_NOT_ACCESSIBLE = "CONFIGURATION_NOT_ACCESSIBLE";
  private static final String GRANT_PERMISION_FAILED = "GRANT_PERMISION_FAILED";

  private static final PermissionsCache cache = PermissionsCache.getInstance();// can be null when CML is unable to provide the requested cache

  public PermissionsStorage() throws IOException {
    if (cache == null) {
      SimpleLogger.log(Severity.INFO, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000013", "Code Base permissions cache not available");
    }

    ConfigurationHandler handler = null;

    try {
      String rootPath = ROOT_CONFIGURATION + "/" + DOMAINS;
      handler = getHandler();
      handler.addConfigurationChangedListener(new ProtectedDomainsChangeListener(this, rootPath), rootPath);
      handler.commit();
    } catch (Exception e) {
      SimpleLogger.log(Severity.FATAL, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000014", "Unexpected problem, PermissionsStorage class not instantiated. Security service is unavailable.");
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.FATAL, "Unexpected problem, PermissionsStorage class not instantiated. Security service is unavailable.", e);
      throw new IOException(e.toString());
    }
  }


  public boolean isStartedForTheFirstTime() throws SecurityException {
    boolean loadingOrCreating = false;
    ConfigurationHandler handler = null;

    try {
      handler = getHandler();
      Configuration rootCfg = getReadConfiguration(handler);

      try {
        if (rootCfg.existsSubConfiguration(DOMAINS)) {
          reloadPermissions();
          loadingOrCreating = false;
        } else {
          // cluster started for the first time
          handler.closeAllConfigurations();
          rootCfg = getWriteConfiguration(handler);
          initDirectoryPermissions(rootCfg);
          commit(handler);
          loadingOrCreating = true;
        }
      } catch (SecurityException _) {
        // already traced
        throw _;
      } catch (Exception e) {
        String message = null;
        
        if (loadingOrCreating) {
          message = "Code based permissions storage is unable to load its configuration structure. Security service is unavailable.";
          SimpleLogger.log(Severity.FATAL, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000015", "Code based permissions storage is unable to load its configuration structure. Security service is unavailable.");
        } else {
          message = "Code based permissions storage is unable to create its initial configuration structure. Security service is unavailable.";
          SimpleLogger.log(Severity.FATAL, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000016", "Code based permissions storage is unable to create its initial configuration structure. Security service is unavailable.");
        }
        
        SecurityException bse = new SecurityException(message, e);
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.FATAL, message, e);
        throw bse;
      }
    } finally {
      close(handler);
    }
    return loadingOrCreating;
  }

  void reloadPermissions() throws Exception {
    ConfigurationHandler handler = null;
    try {
      handler = getHandler();
      Configuration readRoot = getReadConfiguration(handler);
      loadDirectoryPermissions(readRoot);
    } finally {
      close(handler);
    }
  }

  void invalidateCache(String encodedComponent) throws Exception {
    if (cache != null) {
      cache.invalidateCachedComponent(encodedComponent);
    }
  }

  private void loadDirectoryPermissions(Configuration rootCfg) throws Exception {
    Configuration bin_cfg = null;
    Configuration apps_cfg = null;
    Configuration grantedToAllCfg = rootCfg.getSubConfiguration(GRANTED_TO_ALL_PERMISSIONS);
    Vector granted_to_ALL_permissions = loadPermissionTree(grantedToAllCfg);

    if (rootCfg.existsSubConfiguration(BIN_PERMISSIONS)) {
      bin_cfg = rootCfg.getSubConfiguration(BIN_PERMISSIONS);
    } else {
      bin_cfg = rootCfg.createSubConfiguration(BIN_PERMISSIONS);
    }
    root_bin_permissions = loadPermissionTree(bin_cfg);
    root_bin_permissions.addAll(granted_to_ALL_permissions);

    if (rootCfg.existsSubConfiguration(APPS_DIR_PERMISSIONS)) {
      apps_cfg = rootCfg.getSubConfiguration(APPS_DIR_PERMISSIONS);
      apps_dir_permissions = loadPermissionTree(apps_cfg);
    }
  }

  private void initDirectoryPermissions(Configuration rootCfg) throws Exception {
    rootCfg.createSubConfiguration(DOMAINS);
    Configuration grantedToAllCfg = rootCfg.createSubConfiguration(GRANTED_TO_ALL_PERMISSIONS);
    Configuration bin_cfg = rootCfg.createSubConfiguration(BIN_PERMISSIONS);
    Configuration appsCfg = rootCfg.createSubConfiguration(APPS_DIR_PERMISSIONS);

    /* permissions for all code sources are taken from the policy on the first startup of the server
    */
    Vector granted_to_ALL_permissions = parsePermissionCollection(new CodeSource(new URL("file://"), (Certificate[]) null));

    /* SAP J2EE Engine grants all permissions to itself (including services) by default
    */
    root_bin_permissions = new Vector();
    root_bin_permissions.add(new PRecord("java.security.AllPermission", "<all permissions>", "<all actions>"));

    /* J2EE.6.2.3
    Web Components
       java.lang.RuntimePermission loadLibrary
       java.lang.RuntimePermission queuePrintJob
       java.net.SocketPermission * connect
       java.io.FilePermission * read,write
       java.util.PropertyPermission * read
    EJB Components
       java.lang.RuntimePermission queuePrintJob
       java.net.SocketPermission * connect
       java.util.PropertyPermission * read
    */

    apps_dir_permissions = new Vector();
    apps_dir_permissions.add(new PRecord("java.lang.RuntimePermission", "loadLibrary", null));
    apps_dir_permissions.add(new PRecord("java.lang.RuntimePermission", "queuePrintJob", null));
    apps_dir_permissions.add(new PRecord("java.net.SocketPermission", "*", "connect"));
    apps_dir_permissions.add(new PRecord("java.io.FilePermission", "*", "read,write"));
    apps_dir_permissions.add(new PRecord("java.util.PropertyPermission", "*", "read"));

    savePermissionTree(granted_to_ALL_permissions, grantedToAllCfg);
    savePermissionTree(apps_dir_permissions, appsCfg);
    savePermissionTree(root_bin_permissions, bin_cfg);

    apps_dir_permissions.addAll(granted_to_ALL_permissions);
    root_bin_permissions.addAll(granted_to_ALL_permissions);
  }

  public void removeStoredPermissionsForComponent(String component) {
    component = encode(component);

    ConfigurationHandler handler = null;
    try {
      try {
        handler = getHandler();
        Configuration domainsCfg = getWriteConfiguration(handler).getSubConfiguration(DOMAINS);
        if (domainsCfg.existsSubConfiguration(component)) {
          domainsCfg.getSubConfiguration(component).deleteConfiguration();
          if (cache != null) {
            cache.invalidateCachedComponent(component);
          }
          commit(handler);
        }
      } finally {
        close(handler);
      }
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "stored permissions component [" + component + "] not unregistered!", e );
    }
  }

  public void denyPermission(String component, String permission, String instance, String action) throws SecurityException {
    ConfigurationHandler handler = null;
    Configuration domainsCfg = null;
    Configuration single_domainsCfg = null;
    Configuration grantedCfg = null;
    Configuration deniedCfg = null;

    try {
      component = encode(component);
      PRecord permission_record = new PRecord(permission, instance, action);
      Vector tree = new Vector();
      tree.add(permission_record);
      handler = getHandler();
      domainsCfg = getWriteConfiguration(handler).getSubConfiguration(DOMAINS);
      if (!domainsCfg.existsSubConfiguration(component)) {
        if (!(permission.equals(EnginePermission.class.getName()) && instance.equals(EnginePermission.TARGET_KEYSTORE))) {
          single_domainsCfg = domainsCfg.createSubConfiguration(component);
          deniedCfg = single_domainsCfg.createSubConfiguration(DENIED_PERMISSIONS);
          savePermissionTree(tree, deniedCfg);
          commit(handler);
        }

        return;
      } else {
        single_domainsCfg = domainsCfg.getSubConfiguration(component);
        if (single_domainsCfg.existsSubConfiguration(GRANTED_PERMISSIONS)) {
          grantedCfg = single_domainsCfg.getSubConfiguration(GRANTED_PERMISSIONS);
          Vector domain_granted_permissions = loadPermissionTree(grantedCfg);
          if ((permission.equals(EnginePermission.class.getName()) && instance.equals(EnginePermission.TARGET_KEYSTORE))) {
            EnginePermission ep = new EnginePermission(EnginePermission.TARGET_KEYSTORE, action);
            for (int i = 0; i < domain_granted_permissions.size(); i++) {
              PRecord tp = (PRecord) domain_granted_permissions.get(i);
              if (ep.implies(new EnginePermission(tp.getName(), tp.getActions()))) {
                tree.add(tp);
              }
            }
          }

          deletePermissionTree(tree, grantedCfg);
          commit(handler);
          return;
        }

        Vector inherited = getStoredParentPermissionsForComponent(component);
        if (memberOf(inherited, permission_record)) {
          if (single_domainsCfg.existsSubConfiguration(DENIED_PERMISSIONS)) {
            deniedCfg = single_domainsCfg.getSubConfiguration(DENIED_PERMISSIONS);
          } else {
            deniedCfg = single_domainsCfg.createSubConfiguration(DENIED_PERMISSIONS);
          }

          if (!(permission.equals(EnginePermission.class.getName()) && instance.equals(EnginePermission.TARGET_KEYSTORE))) {
            savePermissionTree(tree, deniedCfg);
            commit(handler);
          }
        }
      }
    } catch (SecurityException _) {
      // already traced
      throw _;
    } catch (Exception e) {
      Object[] params = new Object[]{component, permission, instance, action};
      SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000017", "Code based permission [{1}, {2}, {3}] not denied for the persistent record of domain [{0}]", params);
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "code based permission [{1}, {2}, {3}] not denied for the persistent record of domain [{0}]", params, e);
      throw new SecurityException("Code based permission [" + params[1] + ", " + params[2] + ", " + params[3] + "] not denied for the persistent record of domain [" + params[0] + "]", e);
    } finally {
      // the cache is invalidated
      if (cache != null) {
        cache.invalidateCachedComponent(component);
      }
      close(handler);
    }
  }

  public PermissionCollection getStoredPermissionCollectionForCodeSource(CodeSource codeSource) {
    Vector storedPermissions = getStoredPermissionsForCodeSource(codeSource);
    return assembleCollection(storedPermissions, codeSource);
  }

  public PermissionCollection getStoredPermissionCollectionForComponent(String component) {
    PermissionCollection result = null;
    Vector storedPermissions = null;
    PRecord permission = null;
    final String encodedComponent = encode(component);


    if (cache != null) {
      result = cache.getCachedPermissionsCollection(encodedComponent);
      if (result != null) {
        return result;
      } else {
        storedPermissions = cache.getCachedPermissionsVector(encodedComponent);
      }
    }

    if (storedPermissions == null) {
      if (inDebug()) dump("getStoredPermissions for: " + component);
      storedPermissions = getStoredPermissionsForComponent(component);
      if (cache != null) {
        cache.cachePermissionsAsVector(encodedComponent,storedPermissions );
      }
    }

    for (Object storedPermission : storedPermissions) {
      permission = (PRecord) storedPermission;
      if (inDebug()) {
        dump("    = " + permission);
      }
    }
    if (inDebug()) dump("getStoredPermissions } OK");
    result = assembleCollection(storedPermissions, component);

    if (cache != null) {
      cache.cachePermissionsAsCollection(encodedComponent, result);
    }

    return result;
  }

  private PermissionCollection assembleCollection(Vector storedPermissions, Object key) {
    Permissions permissions = new Permissions();

    try {
      if (storedPermissions == null) {
        return permissions;
      }

      PRecord p_record = null;
      Permission p = null;
      for (Object storedPermission : storedPermissions) {
        p_record = (PRecord) storedPermission;

        try {
          if (p_record.getClassName().equals("com.sap.engine.lib.security.VeilPermission")) {
            if (key instanceof CodeSource) {
              p = new VeilPermission((CodeSource) key);
            } else {
              // component name used as a key - no way to find the correct code source !!!!
              //
            }

          } else {
            p = PermissionsFactory.createPermission(p_record.getClassName(), p_record.getName(), new String[]{p_record.getActions()}, this.getClass().getClassLoader());
          }
          permissions.add(p);
        } catch (Exception e) {
          if (Util.SEC_SRV_LOCATION.beWarning()){
            Object[] params = new Object[]{key, p_record};
            SimpleLogger.log(Severity.WARNING, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000018", "Permission [{1}] not included to the code based permission of domain [{0}] with code source [{0}]", params);
            Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "permission [{1}] not included to the code based permission of domain [{0}] with code source [{0}]", params, e);
          }
        }
      }

      return permissions;
    } catch (Exception e) {
      Object[] params =  new Object[]{key};
      SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000019", "Unexpected - code based permissions for domain [{0}] with code source [{0}] are not available", params);
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "unexpected - code based permissions for domain [{0}] with code source [{0}] are not available", params, e);
      return null;
    }
  }


  /**
   *
   *
   * @param codeSource
   * @return
   * @throws SecurityException
   */
  public Vector getStoredPermissionsForCodeSource(CodeSource codeSource) throws SecurityException {
    String component = ProtectionDomainFactory.codesourceToString(codeSource);
    return getStoredPermissionsForComponent(component);
  }


  public synchronized Vector getStoredPermissionsForComponent(String component) throws SecurityException {
    Vector result = null;
    Configuration rootCfg = null;
    Configuration singleDomainCfg = null;
    ConfigurationHandler handler = null;

    try {
      component = encode(component);

      // checking the cache first
      if (cache != null) {
        result = cache.getCachedPermissionsVector(component);
        if (result != null) {
          return result;
        }
      }

      try {
        handler = getHandler();
        rootCfg = getReadConfiguration(handler).getSubConfiguration(DOMAINS);
      } catch (Exception e) {
        SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000020", "System configuration not accessible. Code based permissions persistent storage is corrupted.");
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "System configuration not accessible. Code based permissions persistent storage is corrupted.", e);
        throw new SecurityException("System configuration not accessible. Code based permissions persistent storage is corrupted.", e);
      }

      Vector granted = null;
      Vector denied  = null;
      Vector inherited = null;

      try {
        inherited = getStoredParentPermissionsForComponent(component);

        if (rootCfg.existsSubConfiguration(component)) {
          singleDomainCfg = rootCfg.getSubConfiguration(component);

          if (singleDomainCfg.existsSubConfiguration(GRANTED_PERMISSIONS)) {
            granted = loadPermissionTree(singleDomainCfg.getSubConfiguration(GRANTED_PERMISSIONS));
          }
          if (singleDomainCfg.existsSubConfiguration(DENIED_PERMISSIONS)) {
            denied = loadPermissionTree(singleDomainCfg.getSubConfiguration(DENIED_PERMISSIONS));
          }
        }

        result = inherited;
        if ((denied != null) || (granted != null)) {
          // need a copy because some changes will be done
          result = (Vector) result.clone();
        }
        if (denied != null) {
          result.removeAll(denied);
        }
        if (granted != null) {
          result.addAll(granted);
        }
      } catch (Exception e) {
        result = new Vector();
        if (Util.SEC_SRV_LOCATION.beWarning()){
          Object[] params = new Object[]{ component };
          SimpleLogger.log(Severity.WARNING, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000021", "code based permissions for domain [{0}] with code source [{0}] are not readable", params);
          Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "code based permissions for domain [{0}] with code source [{0}] are not readable", params, e);
        }
      }

      if (cache != null) {
        cache.cachePermissionsAsVector(component, result);
      }
      return result;
    } finally {
      close(handler);
    }
  }



  public void grantPermission(String component, String permission, String instance, String action) throws SecurityException {
    PRecord p_record = new PRecord(permission, instance, action);
    Vector p_tree = new Vector();
    p_tree.add(p_record);

    ConfigurationHandler handler = null;
    Configuration domainCfg = null;
    Configuration rootCfg = null;
    Configuration grantedCfg = null;
    Configuration deniedCfg = null;

    try {
      component = encode(component);
      Vector inherited = getStoredParentPermissionsForComponent(component);

      handler = getHandler();
      rootCfg = getWriteConfiguration(handler).getSubConfiguration(DOMAINS);

      if (rootCfg.existsSubConfiguration(component)) {
        domainCfg = rootCfg.getSubConfiguration(component);
        if (memberOf(inherited, p_record)) {
          if (domainCfg.existsSubConfiguration(DENIED_PERMISSIONS)) {
            deniedCfg = domainCfg.getSubConfiguration(DENIED_PERMISSIONS);
            deletePermissionTree(p_tree, deniedCfg);
          }
        } else {
          if (domainCfg.existsSubConfiguration(GRANTED_PERMISSIONS)) {
            grantedCfg = domainCfg.getSubConfiguration(GRANTED_PERMISSIONS);
          } else {
            grantedCfg = domainCfg.createSubConfiguration(GRANTED_PERMISSIONS);
          }
          savePermissionTree(p_tree, grantedCfg);
        }
      } else {
        domainCfg = rootCfg.createSubConfiguration(component);
        grantedCfg = domainCfg.createSubConfiguration(GRANTED_PERMISSIONS);
        savePermissionTree(p_tree, grantedCfg);
      }

      // invalidating the cache
      //
      if (cache != null) {
        cache.invalidateCachedComponent(component);
      }
      commit(handler);
    } catch (SecurityException _) {
      //already traced
      throw _;
    } catch (Exception ce) {
      Object[] params = new Object[]{component, permission, instance, action};
      SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000022", "Code based permission [{1}, {2}, {3}] not granted for the persistent record of domain [{0}]", params);
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Code based permission [{1}, {2}, {3}] not granted for the persistent record of domain [{0}]", params, ce);
      throw new SecurityException("Code based permission [" + params[1] + ", " + params[2] + ", " + params[3] + "] not granted for the persistent record of domain [" + params[0] + "]", ce);
    } finally {
      close(handler);
    }
  }

  private Vector getStoredParentPermissionsForComponent(String component) {
    if (ProtectionDomainFactory.getComponentType(component) == ProtectionDomainFactory.APPLICATION_COMPONENTS) {
      return apps_dir_permissions;
    } else {
      return root_bin_permissions;
    }
  }
}
