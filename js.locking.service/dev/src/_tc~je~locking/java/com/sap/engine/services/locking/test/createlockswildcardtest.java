/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/CreateLocksWildcardTest.java#10 $ SAP*/
package com.sap.engine.services.locking.test;


import java.util.Properties;

import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.thread.ThreadSystem;


/**
 * For specific infos on this test check getName() and getDescription(). 
 */
public class CreateLocksWildcardTest extends BaseLockingContextTest
{
  public String getName() 
  { 
    return "Create locks which contain wildcards"; 
  }
  
  public String getDescription() 
  { 
    return "1) Create several locks (some arguments have wildcards) and force conflicts\n"
         + "2) Create locks (some names have wildcards) => names must not support wildcards";
  }
  
  protected void test(ThreadSystem threadSystem, Properties properties, TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    String name1 = NAME_PREFIX + "name1";
    String argument1  = ARGUMENT_PREFIX + "argument1";
    String argument2  = ARGUMENT_PREFIX + "argument2";
    String wildcardAArgument      = ARGUMENT_PREFIX + "argument" + LockingConstants.WILDCARD_CHARACTER_SINGLE;
    String wildcardBArgument1     = ARGUMENT_PREFIX + "argument1" + LockingConstants.WILDCARD_CHARACTER_SINGLE;
    String wildcardCArgument1     = ARGUMENT_PREFIX + LockingConstants.WILDCARD_CHARACTER_SINGLE + "rgument1";
    String wildcardDArgumentNone  = ARGUMENT_PREFIX + LockingConstants.WILDCARD_CHARACTER_SINGLE;
    
    logging.log("Create shared locks for owner1/owner2 [name1, argument1]");
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    logging.log("Create shared locks for owner1/owner2 [name1, <all wildcardarguments>]");
    locking.lock(owner1, name1, wildcardAArgument, MODE_SHARED);
    locking.lock(owner1, name1, wildcardBArgument1, MODE_SHARED);
    locking.lock(owner1, name1, wildcardCArgument1, MODE_SHARED);
    locking.lock(owner1, name1, wildcardDArgumentNone, MODE_SHARED);
    locking.lock(owner2, name1, wildcardAArgument, MODE_SHARED);
    locking.lock(owner2, name1, wildcardBArgument1, MODE_SHARED);
    locking.lock(owner2, name1, wildcardCArgument1, MODE_SHARED);
    locking.lock(owner2, name1, wildcardDArgumentNone, MODE_SHARED);
    cleanupLocks(logging, locking, owner1, owner2);
    
    logging.log("Create shared lock for owner1 [name1, argument1]");
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    logging.log("Create exclusive lock for owner2 [name1, <all wildcardarguments>] => expect exception");
    lockExpectException(locking, owner2, name1, wildcardAArgument, MODE_EXCLUSIVE_CUMULATIVE);
    lockExpectException(locking, owner2, name1, wildcardBArgument1, MODE_EXCLUSIVE_CUMULATIVE);
    lockExpectException(locking, owner2, name1, wildcardCArgument1, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner2, name1, wildcardDArgumentNone, MODE_EXCLUSIVE_CUMULATIVE);
    cleanupLocks(logging, locking, owner1, owner2);

    logging.log("Create shared lock for owner1 [name1, <wildcardargument 1>]");
    locking.lock(owner1, name1, wildcardAArgument, MODE_SHARED);
    logging.log("Create exclusive lock for owner2 => some expect exception");
    lockExpectException(locking, owner2, name1, argument1, MODE_EXCLUSIVE_CUMULATIVE);
    lockExpectException(locking, owner2, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    lockExpectException(locking, owner2, name1, wildcardBArgument1, MODE_EXCLUSIVE_CUMULATIVE);
    lockExpectException(locking, owner2, name1, wildcardCArgument1, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner2, name1, wildcardDArgumentNone, MODE_EXCLUSIVE_CUMULATIVE);
    cleanupLocks(logging, locking, owner1, owner2);
   
    logging.log("Create shared lock for owner1 [name1, <wildcardargument 2>]");
    locking.lock(owner1, name1, wildcardBArgument1, MODE_SHARED);
    logging.log("Create exclusive lock for owner2 => some expect exception");
    lockExpectException(locking, owner2, name1, argument1, MODE_EXCLUSIVE_CUMULATIVE);
    lockExpectException(locking, owner2, name1, wildcardAArgument, MODE_EXCLUSIVE_CUMULATIVE);
    lockExpectException(locking, owner2, name1, wildcardCArgument1, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner2, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner2, name1, wildcardDArgumentNone, MODE_EXCLUSIVE_CUMULATIVE);
    cleanupLocks(logging, locking, owner1, owner2);
   
    logging.log("Create shared lock for owner1 [name1, <wildcardargument 3>]");
    locking.lock(owner1, name1, wildcardCArgument1, MODE_SHARED);
    logging.log("Create exclusive lock for owner2 => some expect exception");
    lockExpectException(locking, owner2, name1, argument1, MODE_EXCLUSIVE_CUMULATIVE);
    lockExpectException(locking, owner2, name1, wildcardAArgument, MODE_EXCLUSIVE_CUMULATIVE);
    lockExpectException(locking, owner2, name1, wildcardBArgument1, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner2, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner2, name1, wildcardDArgumentNone, MODE_EXCLUSIVE_CUMULATIVE);
    cleanupLocks(logging, locking, owner1, owner2);
   
    logging.log("Create shared lock for owner1 [name1, <wildcardargument 4>]");
    locking.lock(owner1, name1, wildcardDArgumentNone, MODE_SHARED);
    logging.log("Create exclusive lock for owner2 => some expect exception");
    locking.lock(owner2, name1, argument1, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner2, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    cleanupLocks(logging, locking, owner1, owner2);

  }
  
  private void cleanupLocks(TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    logging.log("Remove all locks");
    locking.unlockAll(owner1, false);
    locking.unlockAll(owner2, false);
    logging.log("");
  }
}
