/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/ITest.java#8 $ SAP*/
package com.sap.engine.services.locking.test;


import com.sap.engine.frame.core.locking.LockingConstants;


/**
 * Simple interface for tests.
 */
public interface ITest extends LockingConstants
{
  public static final String NAME_PREFIX_DESCRIPTION = "Internal tests of the locking";
  public static final String NAME_PREFIX = "X$$_TESTNAME_$$";
  public static final String ARGUMENT_PREFIX  = "$$_TESTARGUMENT_$$";

  public String getName();
  public String getDescription();
}
 