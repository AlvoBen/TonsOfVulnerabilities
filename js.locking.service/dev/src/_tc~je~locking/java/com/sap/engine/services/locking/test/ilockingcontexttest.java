package com.sap.engine.services.locking.test;


import java.util.Properties;

import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.thread.ThreadSystem;


/**
 * Simple interface for tests of the AdministrativeLocking.
 */
public interface ILockingContextTest extends ITest
{
  public TestResult start(ThreadSystem threadSystem, AdministrativeLocking locking, Properties properties);
}
  