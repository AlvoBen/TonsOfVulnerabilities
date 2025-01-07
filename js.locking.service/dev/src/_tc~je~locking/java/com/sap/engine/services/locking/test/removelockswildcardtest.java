/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/RemoveLocksWildcardTest.java#9 $ SAP*/
package com.sap.engine.services.locking.test;


import java.util.Properties;

import com.sap.engine.frame.core.locking.LockEntry;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.thread.ThreadSystem;


/**
 * For specific infos on this test check getName() and getDescription(). 
 */
public class RemoveLocksWildcardTest extends BaseLockingContextTest
{
  public String getName() 
  { 
    return "Remove locks which contain wildcards"; 
  }
  
  public String getDescription() 
  { 
    return "1) Create two lock\n"
         + "2) Try to remove this lock using wildcards, which must not work\n"
         + "   Wildcards are only for the creation of locks";
  }
  
  protected void test(ThreadSystem threadSystem, Properties properties, TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    String name1 = NAME_PREFIX + "name1";
    String argument1  = ARGUMENT_PREFIX + "argument1";
    String wildcardAArgument      = ARGUMENT_PREFIX + "argument" + LockingConstants.WILDCARD_CHARACTER_SINGLE;
    String wildcardBArgument1     = ARGUMENT_PREFIX + "argument1" + LockingConstants.WILDCARD_CHARACTER_SINGLE;
    String wildcardCArgument1     = ARGUMENT_PREFIX + LockingConstants.WILDCARD_CHARACTER_SINGLE + "rgument1";
    
    logging.log("Create two locks [owner1, name1, argument1, shared]");
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);

    logging.log("Remove lock [owner1, name1, <wildcardargument 1>, shared]");
    locking.unlock(owner1, name1, wildcardAArgument, MODE_SHARED, false);
    checkLocks(logging, locking, owner1, name1, argument1);

    logging.log("Remove lock [owner1, name1, <wildcardargument 2>, shared]");
    locking.unlock(owner1, name1, wildcardBArgument1, MODE_SHARED, false);
    checkLocks(logging, locking, owner1, name1, argument1);

    logging.log("Remove lock [owner1, name1, <wildcardargument 3>, shared]");
    locking.unlock(owner1, name1, wildcardCArgument1, MODE_SHARED, false);
    checkLocks(logging, locking, owner1, name1, argument1);
  }
  
  private void checkLocks(TestResult logging, AdministrativeLocking locking, String owner, String name, String argument) throws Exception
  {
    logging.log("Check locks");
    LockEntry entries[] = locking.getLocks(name, argument);
    checkLockEntries(entries, new Integer(1), owner, name, argument, new Character(MODE_SHARED), new Integer(2));
  }
}
