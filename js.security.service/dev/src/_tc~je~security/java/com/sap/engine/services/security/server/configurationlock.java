
package com.sap.engine.services.security.server;

import java.util.Properties;

import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.tc.logging.Severity;

public class ConfigurationLock {
  private static String LOCK_AREA = "$service.security";
  public static String USERSTORE_LOCK_NAME = "UME_Userstore_Lock";
  public static String USERSTORE_CONFIGURATION_NAME = "UME User Store";
  
  private Properties serviceProperties = null;

  public ConfigurationLock() {
    serviceProperties = SecurityServerFrame.getServiceProperties();
  }

  public void lock(String configuration, String lockname) {
    long waitLimit = 1800000;
    long waitInterval = 100;
    long beginTime = System.currentTimeMillis();

    try {
      waitLimit = new Long(serviceProperties.getProperty("ServiceStartupLockTimeLimit", "" + waitLimit)).longValue();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG, "Unable to get value of \"ServiceStartupLockTimeLimit\" property! Used default value: " + waitLimit,  e);
    }

    while (System.currentTimeMillis() <= beginTime + waitLimit) {
      try {
        SecurityServerFrame.internalLock.lock(LOCK_AREA, lockname, ServerInternalLocking.MODE_EXCLUSIVE_NONCUMULATIVE);
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration lock [{0}] created for configuration [{1}].", new Object[] { lockname, configuration });
        }
        return;
      } catch (Exception e) {
        synchronized (this) {
          try {
            this.wait(waitInterval);
          } catch (InterruptedException ex) {
            break;
          }
        }
      }
    }
    throw new StorageLockedException("Cannot lock configuration: " + configuration);
  }

  public void releaseLock(String configuration, String lockname) {
    try {
      SecurityServerFrame.internalLock.unlock(LOCK_AREA, lockname, SecurityServerFrame.internalLock.MODE_EXCLUSIVE_NONCUMULATIVE);
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration lock [{0}] released for configuration [{1}].", new Object[] { lockname, configuration });
      }
    } catch (TechnicalLockException e) {
      throw new StorageLockedException("Cannot unlock configuration: " + configuration, e);
    }
  }
}
