package com.sap.security.core.server.jaas.spnego.util;

/**
 * The purpose of the IpSessionCache is to provide a short lifetime session
 * facility to the SPNegoLoginModule. In the case where more than one roundtrip
 * is necessary the gss context an other status information need to be cached.
 * This cache provides the utility to do so. The lifetime of individual items
 * can be customized via parameters
 * {@link com.sap.security.core.server.jaas.spnego.IConstants#CONF_ISC_ITEM_LIFETIME}.
 * The value should be such that the status is cached throughout the entire
 * negociation phase (which should not take longer than 3-4 seconds). Usually,
 * successful completion of the negociation removes the item from the cache so
 * making the item lifetime longer is usually not a problem. Only if the
 * negociation has been terminated unexpectedly (e.g. if the end-user closes the
 * browser window) then it is important that the item times out quickly
 * (otherwise the user might reopen another browser window and the negociation
 * restarts with an old context).
 */
public class IpSessionCache extends ShortLifetimeCache {
  /**
   * @param msecsUntilCleanup
   * @param itemLifetime
   */
  public IpSessionCache(int msecsUntilCleanup, int itemLifetime) {
    super(msecsUntilCleanup, itemLifetime, "IpSessionCache");
  }
}
