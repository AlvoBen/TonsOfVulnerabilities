/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/exception/LockingTechnicalLockException.java#1 $ SAP*/
package com.sap.engine.services.locking.exception;


import com.sap.engine.frame.core.locking.TechnicalLockException;

/**
 * Indicates technical problems when using the Application Locking.
 */
public class LockingTechnicalLockException extends TechnicalLockException
{ 
  public static final String TEST_CLEANUP_FAILED = "locking_2000";  // 0=first test-owner, 1=second test-owner

  
  public LockingTechnicalLockException(String resourceId, Object[] parameters, Exception nested)
  {
    super(LockingResourceAccessor.getInstance(), resourceId, parameters, nested);
  }
  
  public LockingTechnicalLockException(String resourceId, Exception nested)
  {
    this(resourceId, null, nested);
  }
  
  public LockingTechnicalLockException(String resourceId, Object[] parameters)
  {
    this(resourceId, parameters, null);
  }
  
  public LockingTechnicalLockException(String resourceId)
  {
    this(resourceId, null, null);
  }
}