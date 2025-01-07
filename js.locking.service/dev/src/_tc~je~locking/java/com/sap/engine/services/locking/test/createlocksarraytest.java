/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/CreateLocksArrayTest.java#10 $ SAP*/
package com.sap.engine.services.locking.test;


import java.util.Properties;

import com.sap.engine.frame.core.locking.LockEntry;
import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.thread.ThreadSystem;


/**
 * For specific infos on this test check getName() and getDescription(). 
 */
public class CreateLocksArrayTest extends BaseLockingContextTest
{
  public String getName() 
  { 
    return "Create locks in array-operation"; 
  }
  
  public String getDescription() 
  { 
    return "1) Create several locks for owner1 and owner2\n"
         + "2) Create conflicting locks to check transactional behaviour\n"
         + "3) Repeat the step 1)\n"
         + "\n"
         + "After every step the locks are always checked for consistency.";  
  }
  
  protected void test(ThreadSystem threadSystem, Properties properties, TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    String name1 = NAME_PREFIX + "name1";
    String argument1  = ARGUMENT_PREFIX + "argument1";
    String argument2  = ARGUMENT_PREFIX + "argument2";
    String argument3  = ARGUMENT_PREFIX + "argument3";
    
    logging.log("CREATE LOCKS");
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

    checkLocks(logging, locking, name1, 3);

    logging.log("Repeat locks for owner1");
    locking.lock(owner1, names1, arguments1, modes1);
    
    logging.log("Repeat locks for owner2");
    locking.lock(owner2, names2, arguments2, modes2);
    
    checkLocks(logging, locking, name1, 6);

    logging.log("");
    
    logging.log("CHECK TRANSACTIONAL CONSISTENCY");
    logging.log("Redo locks of owner1 for owner2 => expect exception");
    lockExpectException(locking, owner2, names1, arguments1, modes1);    
    checkLocks(logging, locking, name1, 6);

    logging.log("Redo locks of owner2 for owner1 => expect exception");
    lockExpectException(locking, owner1, names2, arguments2, modes2);    
    checkLocks(logging, locking, name1, 6);

    logging.log("");
    
    logging.log("CREATE LOCKS");
    logging.log("Repeat locks for owner1");
    locking.lock(owner1, names1, arguments1, modes1);
    
    logging.log("Repeat locks for owner2");
    locking.lock(owner2, names2, arguments2, modes2);
    checkLocks(logging, locking, name1, 9);
  }
  
  private void checkLocks(TestResult logging, AdministrativeLocking locking, String name, int count) throws Exception
  {
    logging.log("Check locks");
    LockEntry entries[] = locking.getLocks(name, null);
    checkLockEntries(entries, new Integer(4), null, name, null, null, new Integer(count));
  }
}
