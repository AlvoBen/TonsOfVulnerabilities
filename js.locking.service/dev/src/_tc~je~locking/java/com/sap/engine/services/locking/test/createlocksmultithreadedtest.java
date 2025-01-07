/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/CreateLocksMultiThreadedTest.java#11 $ SAP*/
package com.sap.engine.services.locking.test;


import java.util.Properties;
import java.util.Random;

import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.locking.Util;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.lib.util.concurrent.CountDown;


/**
 * For specific infos on this test check getName() and getDescription(). 
 */
public class CreateLocksMultiThreadedTest extends BaseLockingContextTest
{
  public static final int NUMBER_OF_THREADS = 10;
  public static final int NUMBER_OF_LOCKS = 400;
  
  public static final int NUMBER_OF_LOCKS_TO_SUCCEED = NUMBER_OF_LOCKS;
  public static final int NUMBER_OF_LOCKS_TO_FAIL = (NUMBER_OF_THREADS - 1) * NUMBER_OF_LOCKS;
  private static final Util UTIL = new Util();
  private static final Random RANDOM = new Random();
  
  
  public String getName() 
  { 
    return "Create locks multi threaded"; 
  }
  
  public String getDescription() 
  { 
    return "1) Creates locks N locks by T threads (total of N*T locks)\n"
         + "   Every thread trys to make the same locks,\n"
         + "   so only N locks must succeed and (N-1)*T locks must fail.";
  }
  
  protected void test(ThreadSystem threadSystem, Properties properties, TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception
  {    
    logging.log("Create T threads which name prepared to create N locks");
    
    String owner = owner1;
    String name = NAME_PREFIX + "name";
    char mode = MODE_EXCLUSIVE_NONCUMULATIVE;
    
    CountDown countDown = new CountDown(NUMBER_OF_THREADS);
    LockRunner runners[] = new LockRunner[NUMBER_OF_THREADS];
    for (int i = 0; i < runners.length; i++)
    {
      String arguments[] = createArguments();
      runners[i] = new LockRunner(countDown, locking, owner, name, arguments, mode);
    }
    
    logging.log("Start the threads");
    if (threadSystem != null)
    {
      for (int i = 0; i < runners.length; i++)
        threadSystem.startThread(runners[i], false);
    }
    else
    {
      Thread t[] = new Thread[runners.length];
      for (int i = 0; i < t.length; i++)
        t[i] = new Thread(runners[i]);
      for (int i = 0; i < t.length; i++)
        t[i].start();
    }
      
    logging.log("Wait until threads finished");
    countDown.acquire();
    
    logging.log("All threads finished");

    // if one thread failed with exception, then the whole test fails
    for (int i = 0; i < runners.length; i++)
      if (runners[i]._resultException != null)
        throw new TechnicalLockException("Thread " + i + " finished with Exception", runners[i]._resultException);
  
    // check if logical success
    int totalSucceeded = 0;
    int totalFailed = 0;
    for (int i = 0; i < runners.length; i++)
    {
      totalSucceeded += runners[i]._resultLocksSucceeded;
      totalFailed += runners[i]._resultLocksFailed;
    }
    
    boolean success = (totalSucceeded == NUMBER_OF_LOCKS_TO_SUCCEED && totalFailed == NUMBER_OF_LOCKS_TO_FAIL);
    if (success)
    {
      logging.log("Success: " + NUMBER_OF_LOCKS_TO_SUCCEED + " locks succeed and " + NUMBER_OF_LOCKS_TO_FAIL + " locks failed");
      logging.log("Details:");
      for (int i = 0; i < runners.length; i++)
        logging.log("Thread " + i + ": (success/fail) = " + runners[i]._resultLocksSucceeded + " / " + runners[i]._resultLocksFailed);
    }
    else
    {
      String message = "Wrong results:\n"
                     + "Expected: " + NUMBER_OF_LOCKS_TO_SUCCEED + " locks succeed and " + NUMBER_OF_LOCKS_TO_FAIL + " locks failed\n"
                     + "Received: " + totalSucceeded + " locks succeed and " + totalFailed + " locks failed";
      logging.log(message);
      logging.log("Details:");
      for (int i = 0; i < runners.length; i++)
        logging.log("Thread " + i + ": (success/fail) = " + runners[i]._resultLocksSucceeded + " / " + runners[i]._resultLocksFailed);
      throw new TechnicalLockException(message);
    }
  }
  
  private String[] createArguments()
  {
    String result[] = new String[NUMBER_OF_LOCKS];
    String argumentStart = ARGUMENT_PREFIX + "argument";
    int numberLength = Integer.toString(NUMBER_OF_LOCKS).length();
    for (int i = 0; i < result.length; i++)
    {
      String number = UTIL.adjustStringLengthLeft(Integer.toString(i), numberLength, '0');
      result[i] = argumentStart + number;
    }
    // scramble
    for (int i = 0; i < result.length; i++)
    {
      int pos1 = RANDOM.nextInt(result.length);
      int pos2 = RANDOM.nextInt(result.length);
      String temp = result[pos1];
      result[pos1] = result[pos2];
      result[pos2] = temp;
    }
    return result;
  }
}



// =============================================================================



class LockRunner implements Runnable
{
  private CountDown _countDown;
  private AdministrativeLocking _locking;
  private String _owner;
  private String _name;
  private String _arguments[];
  private char _mode;
  
  protected int _resultLocksSucceeded;
  protected int _resultLocksFailed;
  protected Exception _resultException;
  
  public LockRunner(CountDown countDown, AdministrativeLocking locking, String owner, String name, String arguments[], char mode)
  { 
    _countDown = countDown; 
    _locking = locking;
    _owner = owner;
    _name = name;
    _arguments = arguments;
    _mode = mode;
  }  
  
  public void run()
  {
    try
    {
      for (int i = 0; i < _arguments.length; i++)
      {
        // assure, that all threads create more or less the same number of locks
        if (i % 10 == 0)
          Thread.currentThread().yield();
          
        try
        {
          _locking.lock(_owner, _name, _arguments[i], _mode);
          _resultLocksSucceeded ++;
        }
        catch (LockException e)
        {
          _resultLocksFailed ++;
        }
      }
    }
    catch (Exception e)
    {
      _resultException = e;
    }
    finally
    {
      _countDown.release();
    }
  }
}
