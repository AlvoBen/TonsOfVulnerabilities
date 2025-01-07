/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/RemoveLocksArrayTest.java#8 $ SAP*/
package com.sap.engine.services.locking.test;


import java.util.Properties;

import com.sap.engine.frame.core.locking.LockEntry;
import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.thread.ThreadSystem;


/**
 * For specific infos on this test check getName() and getDescription(). 
 */
public class RemoveLocksArrayTest extends BaseLockingContextTest
{
  public String getName() 
  { 
    return "Remove locks in array-operation"; 
  }
  
  public String getDescription() 
  { 
    return "1) Create several locks for owner1 and owner2\n"
         + "2) Remove locks as created\n"
         + "3) Repeat the step 1)\n"
         + "4) Remove locks, but less than created\n"
         + "5) Repeat the step 1)\n"
         + "6) Remove locks, but more than created\n"
         + "\n"
         + "After every step the locks are always checked for consistency.";  
  }
  
  protected void test(ThreadSystem threadSystem, Properties properties, TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    String name1 = NAME_PREFIX + "name1";
    String argument1  = ARGUMENT_PREFIX + "argument1";
    String argument2  = ARGUMENT_PREFIX + "argument2";
    String argument3  = ARGUMENT_PREFIX + "argument3";
    LockEntry entries[];
    
    logging.log("Create locks for owner1");
    String names1[] = { name1,       name1,       name1,       name1,                     name1,                     name1 };
    String arguments1[]  = { argument1,        argument1,        argument1,        argument3,                      argument3,                      argument3 };
    char modes1[]   = { MODE_SHARED, MODE_SHARED, MODE_SHARED, MODE_EXCLUSIVE_CUMULATIVE, MODE_EXCLUSIVE_CUMULATIVE, MODE_EXCLUSIVE_CUMULATIVE };
    locking.lock(owner1, names1, arguments1, modes1);
    logging.log("Create locks for owner2");
    String names2[] = { name1,       name1,       name1,       name1,                     name1,                     name1 };
    String arguments2[]  = { argument1,        argument1,        argument1,        argument2,                      argument2,                      argument2 };
    char modes2[]   = { MODE_SHARED, MODE_SHARED, MODE_SHARED, MODE_EXCLUSIVE_CUMULATIVE, MODE_EXCLUSIVE_CUMULATIVE, MODE_EXCLUSIVE_CUMULATIVE };
    locking.lock(owner2, names2, arguments2, modes2);

    logging.log("Remove locks for owner1");
    locking.unlock(owner1, names1, arguments1, modes1, false);
    entries = locking.getLocks(name1, null);
    checkLockEntries(entries, new Integer(2), owner2, name1, null, null, new Integer(3));
    
    logging.log("Remove locks for owner2");
    locking.unlock(owner2, names2, arguments2, modes2, false);
    entries = locking.getLocks(name1, null);
    checkLockEntries(entries, new Integer(0), null, name1, null, null, new Integer(3));
    
    cleanupLocks(logging, locking, owner1, owner2);
    logging.log("");
    
    logging.log("Create locks for owner1");
    locking.lock(owner1, names1, arguments1, modes1);
    logging.log("Create locks for owner2");
    locking.lock(owner2, names2, arguments2, modes2);
    
    logging.log("Remove locks for owner1");
    String names1a[] = { name1,       name1,       name1,                     name1 };
    String arguments1a[]  = { argument1,        argument1,        argument3,                      argument3 };
    char modes1a[]   = { MODE_SHARED, MODE_SHARED, MODE_EXCLUSIVE_CUMULATIVE, MODE_EXCLUSIVE_CUMULATIVE };
    locking.unlock(owner1, names1a, arguments1a, modes1a, false);
    entries = locking.getLocks(name1, null);
    checkLockEntries(entries, new Integer(4), null, name1, null, null, null);
    
    logging.log("Remove locks for owner2");
    String names2a[] = { name1,       name1,       name1,                     name1 };
    String arguments2a[]  = { argument1,        argument1,        argument2,                      argument2 };
    char modes2a[]   = { MODE_SHARED, MODE_SHARED, MODE_EXCLUSIVE_CUMULATIVE, MODE_EXCLUSIVE_CUMULATIVE };
    locking.unlock(owner2, names2a, arguments2a, modes2a, false);
    entries = locking.getLocks(name1, null);
    checkLockEntries(entries, new Integer(4), null, name1, null, null, new Integer(1));

    cleanupLocks(logging, locking, owner1, owner2);
    logging.log("");
    
    logging.log("Create locks for owner1");
    locking.lock(owner1, names1, arguments1, modes1);
    logging.log("Create locks for owner2");
    locking.lock(owner2, names2, arguments2, modes2);
    
    logging.log("Remove locks for owner1");
    String names1b[] = { name1,       name1,       name1,       name1,       name1,                     name1 };
    String arguments1b[]  = { argument1,        argument1,        argument1,        argument1,        argument3,                      argument3 };
    char modes1b[]   = { MODE_SHARED, MODE_SHARED, MODE_SHARED, MODE_SHARED, MODE_EXCLUSIVE_CUMULATIVE, MODE_EXCLUSIVE_CUMULATIVE };
    locking.unlock(owner1, names1b, arguments1b, modes1b, false);
    entries = locking.getLocks(name1, null);
    checkLockEntries(entries, new Integer(3), null, name1, null, null, null);
    entries = locking.getLocks(name1, argument3);
    checkLockEntries(entries, new Integer(1), null, name1, null, null, new Integer(1));
    
    logging.log("Remove locks for owner2");
    String names2b[] = { name1,       name1,       name1,       name1,       name1,                     name1 };
    String arguments2b[]  = { argument1,        argument1,        argument1,        argument1,        argument2,                      argument2 };
    char modes2b[]   = { MODE_SHARED, MODE_SHARED, MODE_SHARED, MODE_SHARED, MODE_EXCLUSIVE_CUMULATIVE, MODE_EXCLUSIVE_CUMULATIVE };
    locking.unlock(owner2, names2b, arguments2b, modes2b, false);
    entries = locking.getLocks(name1, null);
    checkLockEntries(entries, new Integer(2), null, name1, null, new Character(MODE_EXCLUSIVE_CUMULATIVE), new Integer(1));
  }
  
  private void cleanupLocks(TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    logging.log("Remove all locks");
    locking.unlockAll(owner1, false);
    locking.unlockAll(owner2, false);
    logging.log("");
  }  
}
