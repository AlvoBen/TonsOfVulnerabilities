/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/RemoveLocksTest.java#8 $ SAP*/
package com.sap.engine.services.locking.test;


import java.util.Properties;

import com.sap.engine.frame.core.locking.LockEntry;
import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.thread.ThreadSystem;


/**
 * For specific infos on this test check getName() and getDescription(). 
 */
public class RemoveLocksTest extends BaseLockingContextTest
{
  public String getName() 
  { 
    return "Remove locks"; 
  }
  
  public String getDescription() 
  { 
    return "1) Creates several locks and removes them with different procedures\n"
         + "2) Receives the locks and checks them against the expected locks";
  }
  
  protected void test(ThreadSystem threadSystem, Properties properties, TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    String name1 = NAME_PREFIX + "name1";
    String name2 = NAME_PREFIX + "name2";
    String argument1  = ARGUMENT_PREFIX + "argument1";
    String argument2  = ARGUMENT_PREFIX + "argument2";
    String argument3  = ARGUMENT_PREFIX + "argument3";
    LockEntry entries[];
    
    logging.log("Create several locks of all modes for [owner1, name1]");
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner1, name1, argument3, MODE_EXCLUSIVE_NONCUMULATIVE);
    logging.log("Create several locks of mode shared for [owner2, name1]");
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    
    logging.log("Remove the shared locks one by one");
    locking.unlock(owner1, name1, argument1, MODE_SHARED, false);
    entries = locking.getLocks(name1, argument1);
    checkLockEntries(entries, new Integer(2), null, name1, argument1, new Character(MODE_SHARED), null);
    locking.unlock(owner1, name1, argument1, MODE_SHARED, false);
    locking.unlock(owner1, name1, argument1, MODE_SHARED, false);
    entries = locking.getLocks(name1, argument1);
    checkLockEntries(entries, new Integer(1), owner2, name1, argument1, new Character(MODE_SHARED), new Integer(3));

    logging.log("Remove the exclusive cumulative locks one by one");
    locking.unlock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE, false);
    entries = locking.getLocks(name1, argument2);
    checkLockEntries(entries, new Integer(1), owner1, name1, argument2, new Character(MODE_EXCLUSIVE_CUMULATIVE), new Integer(2));
    locking.unlock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE, false);
    locking.unlock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE, false);
    entries = locking.getLocks(name1, argument2);
    checkLockEntries(entries, new Integer(0), owner1, name1, argument2, new Character(MODE_EXCLUSIVE_CUMULATIVE), new Integer(0));

    logging.log("Remove the exclusive non-cumulative locks one by one");
    entries = locking.getLocks(name1, argument3);
    checkLockEntries(entries, new Integer(1), owner1, name1, argument3, new Character(MODE_EXCLUSIVE_NONCUMULATIVE), new Integer(1));
    locking.unlock(owner1, name1, argument3, MODE_EXCLUSIVE_NONCUMULATIVE, false);
    entries = locking.getLocks(name1, argument3);
    checkLockEntries(entries, new Integer(0), owner1, name1, argument3, new Character(MODE_EXCLUSIVE_NONCUMULATIVE), new Integer(0));
    
    cleanupLocks(logging, locking, owner1, owner2);
    
    logging.log("Create one shared lock for [owner1, name1, argument1]");
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    logging.log("Remove several locks, which do not match => the lock should still remain");
    locking.unlock(owner2, name1, argument1, MODE_SHARED, false);
    locking.unlock(owner1, name2, argument1, MODE_SHARED, false);
    locking.unlock(owner1, name1, argument2, MODE_SHARED, false);
    locking.unlock(owner1, name1, argument1, MODE_EXCLUSIVE_CUMULATIVE, false);
    locking.unlock(owner1, name1, argument1, MODE_EXCLUSIVE_NONCUMULATIVE, false);
    entries = locking.getLocks(name1, argument1);
    checkLockEntries(entries, new Integer(1), owner1, name1, argument1, new Character(MODE_SHARED), new Integer(1));
   
    cleanupLocks(logging, locking, owner1, owner2);
 
    logging.log("Create several locks of all modes for [owner1, name1]");
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner1, name1, argument3, MODE_EXCLUSIVE_NONCUMULATIVE);
    logging.log("Create several locks of mode shared for [owner2, name1]");
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    
    logging.log("Remove the shared locks cumulative for owner1");
    locking.unlockAllCumulativeCounts(owner1, name1, argument1, MODE_SHARED, false);
    entries = locking.getLocks(name1, argument1);
    checkLockEntries(entries, new Integer(1), owner2, name1, argument1, new Character(MODE_SHARED), new Integer(3));
    entries = locking.getLocks(name1, argument2);
    checkLockEntries(entries, new Integer(1), owner1, name1, argument2, new Character(MODE_EXCLUSIVE_CUMULATIVE), new Integer(3));
    entries = locking.getLocks(name1, argument3);
    checkLockEntries(entries, new Integer(1), owner1, name1, argument3, new Character(MODE_EXCLUSIVE_NONCUMULATIVE), new Integer(1));
    
    logging.log("Remove the exclusive cumulative locks cumulative for owner1");
    locking.unlockAllCumulativeCounts(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE, false);
    entries = locking.getLocks(name1, argument1);
    checkLockEntries(entries, new Integer(1), owner2, name1, argument1, new Character(MODE_SHARED), new Integer(3));
    entries = locking.getLocks(name1, argument2);
    checkLockEntries(entries, new Integer(0), owner1, name1, argument2, new Character(MODE_EXCLUSIVE_CUMULATIVE), new Integer(3));
    entries = locking.getLocks(name1, argument3);
    checkLockEntries(entries, new Integer(1), owner1, name1, argument3, new Character(MODE_EXCLUSIVE_NONCUMULATIVE), new Integer(1));
    
    logging.log("Remove the exclusive non-cumulative locks cumulative for owner1");
    locking.unlockAllCumulativeCounts(owner1, name1, argument3, MODE_EXCLUSIVE_NONCUMULATIVE, false);
    entries = locking.getLocks(name1, argument1);
    checkLockEntries(entries, new Integer(1), owner2, name1, argument1, new Character(MODE_SHARED), new Integer(3));
    entries = locking.getLocks(name1, argument3);
    checkLockEntries(entries, new Integer(0), owner1, name1, argument3, new Character(MODE_EXCLUSIVE_NONCUMULATIVE), new Integer(1));

    cleanupLocks(logging, locking, owner1, owner2);

    logging.log("Create several locks for owner1 and owner2");
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    locking.lock(owner2, name1, argument3, MODE_EXCLUSIVE_NONCUMULATIVE);
    
    logging.log("Remove all locks for owner1 cumulative");
    locking.unlockAll(owner1, false);
    entries = locking.getLocks(name1, null);
    checkLockEntries(entries, new Integer(2), owner2, name1, null, null, null);
    
    logging.log("Remove all locks for owner2 cumulative");
    locking.unlockAll(owner2, false);
    entries = locking.getLocks(name1, null);
    checkLockEntries(entries, new Integer(0), null, name1, null, null, null);
  }
  
  
  private void cleanupLocks(TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    logging.log("Remove all locks");
    locking.unlockAll(owner1, false);
    locking.unlockAll(owner2, false);
    logging.log("");
  }  
}
