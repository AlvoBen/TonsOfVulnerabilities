/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/exception/LockingResourceAccessor.java#1 $ SAP*/
package com.sap.engine.services.locking.exception;


import com.sap.localization.ResourceAccessor;


/**
 * The ResourceAccessor for the software component "locking".
 */
public class LockingResourceAccessor extends ResourceAccessor
{
  private static final String BUNDLE_NAME = "com.sap.engine.services.locking.exception.LockingResourceBundle";
  private static final LockingResourceAccessor instance = new LockingResourceAccessor();
  
  private LockingResourceAccessor() 
  {
    super(BUNDLE_NAME); 
  }
  
  public static LockingResourceAccessor getInstance() 
  {
    return instance; 
  }
}
