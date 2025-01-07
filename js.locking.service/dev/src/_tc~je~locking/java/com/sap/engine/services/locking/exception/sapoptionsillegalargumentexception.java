/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/exception/SAPOptionsIllegalArgumentException.java#1 $ SAP*/
package com.sap.engine.services.locking.exception;


import com.sap.exception.standard.SAPIllegalArgumentException;

/**
 * Indicates that invalid parameters were passed to the methods of the OptionParser.
 */
public class SAPOptionsIllegalArgumentException extends SAPIllegalArgumentException
{ 
  public static final String OPTION_NAME_MULTIPLE_TIMES = "locking_2200";  // 0=option-name 
  public static final String OPTION_UNKNOWN = "locking_2201";  // 0=option-name 
  public static final String OPTION_WITH_MULTIPLE_VALUES = "locking_2202";  // 0=option-name 
  public static final String OPTION_MUST_HAVE_VALUE = "locking_2203";  // 0=option-name 
  public static final String OPTION_WITHOUT_NAME = "locking_2204";  
  
  
  public SAPOptionsIllegalArgumentException(String resourceId, Object[] parameters, Exception nested)
  {
    super(LockingResourceAccessor.getInstance(), resourceId, parameters, nested);
  }
  
  public SAPOptionsIllegalArgumentException(String resourceId, Exception nested)
  {
    this(resourceId, null, nested);
  }
  
  public SAPOptionsIllegalArgumentException(String resourceId, Object[] parameters)
  {
    this(resourceId, parameters, null);
  }
  
  public SAPOptionsIllegalArgumentException(String resourceId)
  {
    this(resourceId, null, null);
  }
}