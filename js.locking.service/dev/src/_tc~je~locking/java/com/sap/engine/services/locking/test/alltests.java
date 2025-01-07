/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/AllTests.java#17 $ SAP*/
package com.sap.engine.services.locking.test;


/**
 * Just a collection of all implemented tests.
 */
public class AllTests
{
  public static ILockingContextTest[] getFunctionalLockingContextTests()
  {
    return new ILockingContextTest[]  
          {
            new CreateLocksTest(),
            new GetLocksTest(),
            new CreateLocksArrayTest(),
            new CreateLocksWildcardTest(),
            new RemoveLocksTest(),
            new RemoveLocksArrayTest(),
            new RemoveLocksWildcardTest(),
            new LockSpecialParametersTest(),
          };
  }
  
  public static ILockingContextTest[] getLoadLockingContextTests()
  {
    return new ILockingContextTest[]  
          {
            new CreateLocksMultiThreadedTest(),
          };
  }
  
  public static ILockingContextTest[] getAllLockingContextTests()
  {
    ILockingContextTest t1[] = getFunctionalLockingContextTests();
    ILockingContextTest t2[] = getLoadLockingContextTests();
    ILockingContextTest result[] = new ILockingContextTest[t1.length + t2.length];
    System.arraycopy(t1, 0, result, 0, t1.length);
    System.arraycopy(t2, 0, result, t1.length, t2.length);
    return result;
  }
}
