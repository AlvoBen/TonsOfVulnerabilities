/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/GetLocksTest.java#9 $ SAP*/
package com.sap.engine.services.locking.test;


import java.util.Properties;

import com.sap.engine.frame.core.locking.LockEntry;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.thread.ThreadSystem;


/**
 * For specific infos on this test check getName() and getDescription(). 
 */
public class GetLocksTest extends BaseLockingContextTest
{
  public String getName() 
  { 
    return "Receive locks"; 
  }
  
  public String getDescription() 
  { 
    return "1) Creates several locks\n"
         + "2) Receives the locks and checks them against the created locks";
     
  }
  
  protected void test(ThreadSystem threadSystem, Properties properties, TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    String name1 = NAME_PREFIX + "name1";
    String name2 = NAME_PREFIX + "name2";
    String argument1 = ARGUMENT_PREFIX + "argument1";
    String argument2 = ARGUMENT_PREFIX + "argument2";
    String wildcardAName  = NAME_PREFIX + LockingConstants.WILDCARD_CHARACTER_MULTI;
    String wildcardBName1 = NAME_PREFIX + "name1" + LockingConstants.WILDCARD_CHARACTER_MULTI;
    String wildcardCName1 = LockingConstants.WILDCARD_CHARACTER_MULTI + NAME_PREFIX + "name1";
    String wildcardDName  = LockingConstants.WILDCARD_CHARACTER_MULTI + NAME_PREFIX + LockingConstants.WILDCARD_CHARACTER_MULTI;
    String wildcardAArgument  = ARGUMENT_PREFIX + LockingConstants.WILDCARD_CHARACTER_MULTI;
    String wildcardBArgument1 = ARGUMENT_PREFIX + "argument1" + LockingConstants.WILDCARD_CHARACTER_MULTI;
    String wildcardCArgument1 = LockingConstants.WILDCARD_CHARACTER_MULTI + ARGUMENT_PREFIX + "argument1";
    String wildcardDArgument  = LockingConstants.WILDCARD_CHARACTER_MULTI + ARGUMENT_PREFIX + LockingConstants.WILDCARD_CHARACTER_MULTI;
    
    logging.log("CREATE LOCKS");
    logging.log("Create locks for owner1: 3x[name1, argument1], 3x[name1, argument2], 3x[name2, argument1]");
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument2, MODE_SHARED);
    locking.lock(owner1, name1, argument2, MODE_SHARED);
    locking.lock(owner1, name1, argument2, MODE_SHARED);
    locking.lock(owner1, name2, argument1, MODE_SHARED);
    locking.lock(owner1, name2, argument1, MODE_SHARED);
    locking.lock(owner1, name2, argument1, MODE_SHARED); 
    
    logging.log("Create locks for owner2: 3x[name1, argument1]");
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    locking.lock(owner2, name1, argument1, MODE_SHARED);
    
    LockEntry entries[];
    
    logging.log("RECEIVE LOCKS");
    logging.log("Receive locks for [name1, argument1]");
    entries = locking.getLocks(name1, argument1);
    checkLockEntries(entries, new Integer(2), null, name1, argument1, new Character(MODE_SHARED), new Integer(3));
    
    logging.log("Receive locks for [name1, null]");
    entries = locking.getLocks(name1, null);
    checkLockEntries(entries, new Integer(3), null, name1, null, new Character(MODE_SHARED), new Integer(3));
    
    logging.log("Receive locks for [null, argument1]");
    entries = locking.getLocks(null, argument1);
    checkLockEntries(entries, new Integer(3), null, null, argument1, new Character(MODE_SHARED), new Integer(3));
    
    logging.log("Receive locks for [name1, argument2]");
    entries = locking.getLocks(name1, argument2);
    checkLockEntries(entries, new Integer(1), owner1, name1, argument2, new Character(MODE_SHARED), new Integer(3));
    
    logging.log("Receive locks for [name2, argument1]");
    entries = locking.getLocks(name2, argument1);
    checkLockEntries(entries, new Integer(1), owner1, name2, argument1, new Character(MODE_SHARED), new Integer(3));

    logging.log("");
    
    logging.log("RECEIVE LOCKS USING WILDCARDS");
    logging.log("Receive locks for [name1, wildcardAArgument]");
    entries = locking.getLocks(name1, wildcardAArgument);
    checkLockEntries(entries, new Integer(3), null, name1, null, new Character(MODE_SHARED), new Integer(3));

    logging.log("Receive locks for [name1, wildcardBArgument1]");
    entries = locking.getLocks(name1, wildcardBArgument1);
    checkLockEntries(entries, new Integer(2), null, name1, argument1, new Character(MODE_SHARED), new Integer(3));

    logging.log("Receive locks for [name1, wildcardCArgument1]");
    entries = locking.getLocks(name1, wildcardCArgument1);
    checkLockEntries(entries, new Integer(2), null, name1, argument1, new Character(MODE_SHARED), new Integer(3));

    logging.log("Receive locks for [name1, wildcardDArgument]");
    entries = locking.getLocks(name1, wildcardDArgument);
    checkLockEntries(entries, new Integer(3), null, name1, null, new Character(MODE_SHARED), new Integer(3));

    logging.log("Receive locks for [wildcardAName, argument1]");
    entries = locking.getLocks(wildcardAName, argument1);
    checkLockEntries(entries, new Integer(3), null, null, argument1, new Character(MODE_SHARED), new Integer(3));

    logging.log("Receive locks for [wildcardBName1, argument1]");
    entries = locking.getLocks(wildcardBName1, argument1);
    checkLockEntries(entries, new Integer(2), null, name1, argument1, new Character(MODE_SHARED), new Integer(3));

    logging.log("Receive locks for [wildcardCName1, argument1]");
    entries = locking.getLocks(wildcardCName1, argument1);
    checkLockEntries(entries, new Integer(2), null, name1, argument1, new Character(MODE_SHARED), new Integer(3));

    logging.log("Receive locks for [wildcardDName, argument1]");
    entries = locking.getLocks(wildcardDName, argument1);
    checkLockEntries(entries, new Integer(3), null, null, argument1, new Character(MODE_SHARED), new Integer(3));
  }
}
