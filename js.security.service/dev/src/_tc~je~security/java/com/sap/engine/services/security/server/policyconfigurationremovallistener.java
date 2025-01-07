package com.sap.engine.services.security.server;

import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.services.security.server.PolicyConfigurationLog;
import com.sap.security.core.InternalUMFactory;
import com.sap.tc.logging.Severity;

/**
 * Handles the delete events for policy configuration paths.
 * 
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class PolicyConfigurationRemovalListener implements ConfigurationChangedListener {

  final private static int pcNamePrefixLength = SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH.length() + 1;//"security/configurations"
  final private static int pcNameSuffixLength = SecurityConfigurationPath.AUTHENTICATION_PATH.length() + 1;//"security/authentication"

  private SecurityContext root = null;

  public PolicyConfigurationRemovalListener(SecurityContext root) {
    this.root = root;
  }

  /**
   * Invoked by configurqation manager. Enforces security context root to update
   * the list of policy configurations.
   * 
   * @param e the event.
   */
  public void configurationChanged(ChangeEvent e) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurationRemovalListener.configurationChanged(ChangeEvent e) ");
    }

    try {
      ChangeEvent[] events = e.getDetailedChangeEvents();
      int last = events.length - 1;

      int action = events[last].getAction();

      switch (action) {
      case ChangeEvent.ACTION_DELETED: {
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "ChangeEvent.ACTION_DELETED catched for configuration path [{0}]", new Object[] { events[last].getPath() });
        }
        /*
         * if something is deleted from the security policy configuration in
         * the database this policy configuration is deleted only from the
         * cache, and on next access is loaded from the database if \security
         * subconfiguration exists.
         */
        ((PolicyConfigurations) root).delete();
        ((PolicyConfigurations) root).modify(events[last].getPath());
        break;
      }
      case ChangeEvent.ACTION_CREATED: {
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "ChangeEvent.ACTION_CREATED catched for configuration path [{0}]", new Object[] { events[last].getPath() });
        }
        ((PolicyConfigurations) root).modify(events[last].getPath());
        break;
      }
      case ChangeEvent.ACTION_MODIFIED: {
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "ChangeEvent.ACTION_MODIFIED catched for configuration path [{0}]", new Object[] { events[last].getPath() });
        }
        ((PolicyConfigurations) root).modify(events[last].getPath());
        break;
      }
      default: {

      }
      }

      
      /*
      String configurationPath = events[last].getPath();
      //configurationPath is "security/configurations" + policyConfigurationName + "security/authentication"

      try {
        String pcName = configurationPath.substring(pcNamePrefixLength, configurationPath.length() - pcNameSuffixLength);
        byte type = ((PolicyConfigurations) root).getPolicyConfigurationType(pcName);
        if (type == SecurityContext.TYPE_AUTHSCHEME || type == SecurityContext.TYPE_AUTHSCHEME_REFERENCE) {
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.debugT("Policy configuration " + pcName + " was modified. Authschemes cache is to be deleted!");
          }
          InternalUMFactory.clearAuthschemesCache();
        }
      } catch (Exception ex) {
        //$JL-EXC$
      }
      */
      
      //clear cache always
      InternalUMFactory.clearAuthschemesCache();
      
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurationRemovalListener.configurationChanged(ChangeEvent e)");
      }
    }
  }

}