/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.domains;

import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.services.security.Util;

import static com.sap.engine.frame.core.configuration.ChangeEvent.ACTION_CREATED;
import static com.sap.engine.frame.core.configuration.ChangeEvent.ACTION_DELETED;
import static com.sap.engine.frame.core.configuration.ChangeEvent.ACTION_INTERNAL;
import static com.sap.engine.frame.core.configuration.ChangeEvent.ACTION_MODIFIED;
import static com.sap.engine.frame.core.configuration.ChangeEvent.ACTION_UNDEFINED;
import static com.sap.engine.services.security.domains.PermissionsStorageUtils.decode;

import static com.sap.engine.lib.security.domain.ProtectionDomainFactory.removeMappedComponent;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

import static com.sap.tc.logging.Severity.WARNING;


import java.security.Policy;


/**
 *
 * This class handles all protection domain permissions changes across the cluster.
 *
 * @author Ilia Kacarov
 * @author Stephan Zlatarev
 */
public class ProtectedDomainsChangeListener implements ConfigurationChangedListener {

  private final PermissionsStorage permissionStorage;
  private final String rootPath;
  
  private final static Location location = Location.getLocation(ProtectedDomainsChangeListener.class);

  public ProtectedDomainsChangeListener(PermissionsStorage storage, String rootPath) {
    permissionStorage = storage;
    this.rootPath = rootPath;
  }

  public void configurationChanged(ChangeEvent e) {
    try {

      if (location.beDebug()) {
        dumpEvent(e);
      }

      String encodedComponent = null;

      try {
        encodedComponent = parseComponentName(e);
      } catch (Exception e1) {
        //$JL-EXC
        if (location.beDebug()) {
          location.traceThrowableT(WARNING, "ComponentName parse error", e1);
        }
        encodedComponent = null;
      }

      if (encodedComponent != null) {
        permissionStorage.invalidateCache(encodedComponent);
        if (location.beDebug()) {
          location.debugT("  cacheInvalidated: " + encodedComponent);
        }
        if (e.getAction() == ACTION_DELETED) {
          removeMappedComponent(decode(encodedComponent));
          if (location.beDebug()) {
            location.debugT("  component removed: " + encodedComponent);
          }
        }
      } else {
        permissionStorage.reloadPermissions();
        if (location.beDebug()) {
          location.debugT("  reloading global permissions ");
        }
      }

      Policy.getPolicy().refresh();
    } catch (Exception ex) {
      if (location.beWarning()){
        Object[] params = new String[]{e.getDetailedChangeEvents()[e.getDetailedChangeEvents().length - 1].getPath()};
        location.traceThrowableT(Severity.WARNING, "change configuration event for configuration [{0}] received, but not processed", params, ex);
      }
    }
  }

  private void dumpEvent(ChangeEvent e) {
    ChangeEvent[] details = e.getDetailedChangeEvents();
    location.debugT("event {");
    location.debugT("  path: " + e.getPath());
    location.debugT("  action: " + parseAction(e.getAction()));
    for (ChangeEvent detail: details) {
      location.debugT("    [" + detail.getAction() + "]" + detail.getPath());
    }
    location.debugT("event } ok");
  }


  private String parseAction(final int action) {
    switch (action) {
      case ACTION_CREATED: return "CREATED";
      case ACTION_DELETED: return "DELETED";
      case ACTION_INTERNAL: return "INTERNAL";
      case ACTION_MODIFIED: return "MODIFIED";
      case ACTION_UNDEFINED: return "???";
      default: return null;
    }
  }

  private String parseComponentName(ChangeEvent e) {
    try {
      String domainName = e.getDetailedChangeEvents()[e.getDetailedChangeEvents().length - 1].getPath();

      int start_pos = rootPath.length() + 1;
      int end_pos = domainName.indexOf("/", start_pos);
      end_pos = (end_pos == -1)? domainName.length(): end_pos;
      domainName = domainName.substring(start_pos, end_pos);

      return domainName;
    } catch (StringIndexOutOfBoundsException e1) {
      return null;
    }
  }

}
