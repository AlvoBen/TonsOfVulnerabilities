/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/CreateLocksTest.java#10 $ SAP*/
package com.sap.engine.services.locking.test;


import java.util.Properties;

import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.thread.ThreadSystem;


/**
 * For specific infos on this test check getName() and getDescription(). 
 */
public class CreateLocksTest extends BaseLockingContextTest
{
  public String getName() 
  { 
    return "Create locks"; 
  }
  
  public String getDescription() 
  { 
    return "1) Creates locks of all modes for owner1\n"
         + "2) Creates locks of all modes for owner2 for the same arguments as before\n"
         + "3) Creates locks for different name";
  }
  
  protected void test(ThreadSystem threadSystem, Properties properties, TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {
    String name1 = NAME_PREFIX + "name1";
    String name2 = NAME_PREFIX + "name2";
    String argument1  = ARGUMENT_PREFIX + "argument1";
    String argument2  = ARGUMENT_PREFIX + "argument2";
    String argument3  = ARGUMENT_PREFIX + "argument3";
    
    logging.log("CREATE LOCKS FOR OWNER1 WITH SAME NAME BUT DIFFERENT ARGUMENTS");
    logging.log("Create several shared locks");
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    locking.lock(owner1, name1, argument1, MODE_SHARED);
    
    logging.log("Create several cumulative exclusive locks");
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    locking.lock(owner1, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);
    
    logging.log("Create one non-cumulative exclusive lock");
    locking.lock(owner1, name1, argument3, MODE_EXCLUSIVE_NONCUMULATIVE);
    logging.log("Create another non-cumulative exclusive lock => expect exception");
    lockExpectException(locking, owner1, name1, argument3, MODE_EXCLUSIVE_NONCUMULATIVE);

    logging.log("");
    
    logging.log("CREATE LOCKS FOR OWNER2 FOR THE SAME ARGUMENTS AS BEFORE");
    logging.log("Create shared lock for shared lock");
    locking.lock(owner2, name1, argument1, MODE_SHARED);    
    logging.log("Create cumulative exclusiv lock for shared lock => expect exception");
    lockExpectException(locking, owner2, name1, argument1, MODE_EXCLUSIVE_CUMULATIVE);    
    logging.log("Create non-cumulative exclusiv lock for shared lock => expect exception");
    lockExpectException(locking, owner2, name1, argument1, MODE_EXCLUSIVE_NONCUMULATIVE);    

    logging.log("Create shared lock for cumulative exclusive lock => expect exception");
    lockExpectException(locking, owner2, name1, argument2, MODE_SHARED);    
    logging.log("Create cumulative exclusive lock for cumulative exclusive lock => expect exception");
    lockExpectException(locking, owner2, name1, argument2, MODE_EXCLUSIVE_CUMULATIVE);    
    logging.log("Create non-cumulative exclusive lock for cumulative exclusive lock => expect exception");
    lockExpectException(locking, owner2, name1, argument2, MODE_EXCLUSIVE_NONCUMULATIVE);    

    logging.log("Create shared lock for non-cumulative exclusive lock => expect exception");
    lockExpectException(locking, owner2, name1, argument3, MODE_SHARED);    
    logging.log("Create cumulative exclusive lock for non-cumulative exclusive lock => expect exception");
    lockExpectException(locking, owner2, name1, argument3, MODE_EXCLUSIVE_CUMULATIVE);    
    logging.log("Create shared lock for non-cumulative exclusive lock => expect exception");
    lockExpectException(locking, owner2, name1, argument3, MODE_EXCLUSIVE_NONCUMULATIVE);    

    logging.log("");
    
    logging.log("CREATE LOCKS FOR DIFFERENT NAME");
    logging.log("Create shared lock");
    locking.lock(owner1, name2, argument1, MODE_SHARED);

    logging.log("Create cumulative exclusive lock");
    locking.lock(owner1, name2, argument2, MODE_EXCLUSIVE_CUMULATIVE);

    logging.log("Create non-cumulative exclusive lock");
    locking.lock(owner1, name2, argument3, MODE_EXCLUSIVE_NONCUMULATIVE);
  }
}
