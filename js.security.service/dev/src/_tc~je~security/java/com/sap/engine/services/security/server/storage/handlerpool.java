/**
 * Property of SAP AG, Walldorf (c) Copyright SAP AG, Walldorf, 2000-2002. All
 * rights reserved.
 */

package com.sap.engine.services.security.server.storage;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.security.server.PolicyConfigurationLog;
import com.sap.tc.logging.Severity;

import java.util.*;

/**
 * Handler pool is cache for all used ConfigurationHandlers of the security
 * service.
 * 
 * @author Jako Blagoev
 */
public class HandlerPool {

  private static List freeHandlers = new LinkedList();

  static ConfigurationHandlerFactory configHandlerFactory = null;

  static synchronized ConfigurationHandler getFreeHandler() {
    ConfigurationHandler handler = null;

    if (!freeHandlers.isEmpty()) {
      handler = (ConfigurationHandler) freeHandlers.remove(0);
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration handler [{0}] taken from the handler pool.", new Object[] { handler });
      }
    } else {
      try {
        handler = configHandlerFactory.getConfigurationHandler();
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "New configuration handler [{0}] created.", new Object[] { handler });
        }
      } catch (Exception ex) {
        PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Cannot create configuration handler.", ex);
        throw new SecurityException(ex.getMessage());
      }
    }

    return handler;
  }

  static synchronized void freeHandler(ConfigurationHandler handler) {
    freeHandlers.add(handler);
    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration handler [{0}] returned to the handler pool.", new Object[] { handler });
    }
  }

  public static void setFactory(ConfigurationHandlerFactory factory) {
    configHandlerFactory = factory;
    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration factory [{0}] set for the handler pool.", new Object[] { factory });
    }
  }
}