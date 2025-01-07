/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/LockSpecialParametersTest.java#8 $ SAP*/
package com.sap.engine.services.locking.test;


import java.util.Properties;

import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.thread.ThreadSystem;
 

/**
 * For specific infos on this test check getName() and getDescription(). 
 */
public class LockSpecialParametersTest extends BaseLockingContextTest
{
  public String getName() 
  { 
    return "Create/remove locks with special parameters"; 
  }
  
  public String getDescription() 
  { 
    return "1) Creates locks where the paramters are invalid\n"
         + "2) Creates/remove locks where one parameter contains the multi-wildcard\n";
   }
  
  protected void test(ThreadSystem threadSystem, Properties properties, TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    String name1 = NAME_PREFIX + "name1";
    String argument1  = ARGUMENT_PREFIX + "argument1";
    String name1Max = UTIL.adjustStringLengthRight(name1, MAX_NAME_LENGTH, 'X');
    String argument1Max  = UTIL.adjustStringLengthRight(argument1, MAX_ARGUMENT_LENGTH, 'X');
    String name1WildcardMulti = name1 + WILDCARD_CHARACTER_MULTI;
    String argument1WildcardMulti  = argument1 + WILDCARD_CHARACTER_MULTI;
    String name1WildcardSingle      = NAME_PREFIX + WILDCARD_CHARACTER_SINGLE;
    String name1InvalidA = name1 + '\0';
    String name1InvalidB = name1 + '\31';
    String argument1InvalidA  = argument1  + '\0';
    String argument1InvalidB  = argument1  + '\31';
    
    logging.log("Create lock where every parameter has the maximum-size");
    locking.lock(owner1, name1Max, argument1Max, MODE_SHARED);

    logging.log("Create locks where the maximum-size is exceeded => expect exception");
    lockExpectIllegalArgumentException(locking, owner1, name1Max + 'X', argument1Max, MODE_SHARED);
    lockExpectIllegalArgumentException(locking, owner1, name1Max, argument1Max + 'X', MODE_SHARED);

    logging.log("Create locks where the mode is invalid => expect exception");
    lockExpectIllegalArgumentException(locking, owner1, name1Max + 'X', argument1Max, '\0');
    lockExpectIllegalArgumentException(locking, owner1, name1Max + 'X', argument1Max, '\n');

    logging.log("Create locks with the invalid characters => expect exception");
    lockExpectIllegalArgumentException(locking, owner1, name1InvalidA, argument1, MODE_SHARED);
    lockExpectIllegalArgumentException(locking, owner1, name1InvalidB, argument1, MODE_SHARED);
    lockExpectIllegalArgumentException(locking, owner1, name1, argument1InvalidA, MODE_SHARED);
    lockExpectIllegalArgumentException(locking, owner1, name1, argument1InvalidB, MODE_SHARED);
    
    logging.log("Create locks with null => expect exception");
    lockExpectIllegalArgumentException(locking, null, name1, argument1, MODE_SHARED);
    lockExpectIllegalArgumentException(locking, owner1, null, argument1, MODE_SHARED);
    lockExpectIllegalArgumentException(locking, owner1, name1, null, MODE_SHARED);

    logging.log("Create locks with the multi-wildcard => expect exception");
    lockExpectIllegalArgumentException(locking, owner1, name1WildcardMulti, argument1, MODE_SHARED);
    lockExpectIllegalArgumentException(locking, owner1, name1, argument1WildcardMulti, MODE_SHARED);
    lockExpectIllegalArgumentException(locking, owner1, new String[] { name1WildcardMulti }, new String[] { argument1 }, new char[] { MODE_SHARED });
    lockExpectIllegalArgumentException(locking, owner1, new String[] { name1 }, new String[] { argument1WildcardMulti }, new char[] { MODE_SHARED });

    logging.log("Create locks with the single-wildcard => expect exception");
    lockExpectIllegalArgumentException(locking, owner1, name1WildcardSingle, argument1, MODE_SHARED);
    lockExpectIllegalArgumentException(locking, owner1, new String[] { name1WildcardSingle }, new String[] { argument1 }, new char[] { MODE_SHARED });

    logging.log("Remove locks with null => expect exception");
    unlockExpectIllegalArgumentException(locking, null, name1, argument1, MODE_SHARED);
    unlockExpectIllegalArgumentException(locking, owner1, null, argument1, MODE_SHARED);
    unlockExpectIllegalArgumentException(locking, owner1, name1, null, MODE_SHARED);

    logging.log("Remove locks with the multi-wildcard => expect exception");
    unlockExpectIllegalArgumentException(locking, owner1, name1WildcardMulti, argument1, MODE_SHARED);
    unlockExpectIllegalArgumentException(locking, owner1, name1, argument1WildcardMulti, MODE_SHARED);
    unlockExpectIllegalArgumentException(locking, owner1, new String[] { name1WildcardMulti }, new String[] { argument1 }, new char[] { MODE_SHARED });
    unlockExpectIllegalArgumentException(locking, owner1, new String[] { name1 }, new String[] { argument1WildcardMulti }, new char[] { MODE_SHARED });
  }
}
