/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/Messages.java#9 $ SAP*/
package com.sap.engine.services.locking;


/**
 * All Messages, which are defined for this package,
 * and which are logged with a severity >= INFO.
 */
public interface Messages
{
  public static final String FAILED_TO_GET_$1          = "Failed to access {0}";
  public static final String $1_STARTED                = "{0} started successfully";
  public static final String $1_CAN_NOT_START          = "{0} can not start. {0} cannot be used to adminstrate locks. Please try to start the service manually.";
  public static final String PROPERTIES_OF_$1_CHANGED  = "Properties of {0} changed successfully";
  public static final String $1_STOPPED                = "{0} stopped successfully";
  public static final String $1_CAN_NOT_CLEANUP        = "can not cleanup {0} correctly when trying to stop it";
  public static final String VALUE_OF_$1_IS_$2         = "Value of {0} is {1}";
  public static final String START_$1_TESTS            = "Start execution of {0} tests";
  public static final String TEST_$1_SUCCESSFUL        = "Test {0} finished successfully";
  public static final String TESTS_SUCCESSFUL_$1_MS    = "All tests finished successfully ({0} milliseconds)";
  public static final String BOUND_$1_IN_JNDI          = "{0} successfully bound in JNDI";
  public static final String UNBOUND_$1_FROM_JNDI      = "{0} successfully unbound from JNDI";
}
