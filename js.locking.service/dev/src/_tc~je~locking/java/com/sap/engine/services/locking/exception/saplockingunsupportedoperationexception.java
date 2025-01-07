/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/exception/SAPLockingUnsupportedOperationException.java#1 $ SAP*/
package com.sap.engine.services.locking.exception;


import com.sap.exception.standard.SAPUnsupportedOperationException;

/**
 * Indicates that invalid parameters were passed to the methods of the LockingContext,
 * for example too long Strings etc.
 */
public class SAPLockingUnsupportedOperationException extends SAPUnsupportedOperationException
{ 
  public static final String TESTRESULT_CLOSED = "locking_2100";  
  
  
  public SAPLockingUnsupportedOperationException(String resourceId, Object[] parameters, Exception nested)
  {
    super(LockingResourceAccessor.getInstance(), resourceId, parameters, nested);
  }
  
  public SAPLockingUnsupportedOperationException(String resourceId, Exception nested)
  {
    this(resourceId, null, nested);
  }
  
  public SAPLockingUnsupportedOperationException(String resourceId, Object[] parameters)
  {
    this(resourceId, parameters, null);
  }
  
  public SAPLockingUnsupportedOperationException(String resourceId)
  {
    this(resourceId, null, null);
  }
}