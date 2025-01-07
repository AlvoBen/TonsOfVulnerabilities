/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/BaseLockingContextTest.java#11 $ SAP*/
package com.sap.engine.services.locking.test;

import java.util.Properties;

import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.locking.LockEntry;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.Util;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.locking.exception.LockingTechnicalLockException;

/**
 * A BaseTest, which accesses the locking via the interface LockingRuntimeInterface.
 */
public abstract class BaseLockingContextTest implements ILockingContextTest
{
  protected static final Util UTIL = new Util();

  /**
   * This method must be implemented.
   * 
   * @param properties These are simply forwarded from the start-method.
   * @param logging The TestResult must only be used for logging. Do not call other methods on it.
   * @param locking The LockingRuntimeInterface.
   * @param owner1 A first owner.
   * @param owner2 A second owner.
   *
   * @exception Whatever happens in the Test.
   */
  protected abstract void test(ThreadSystem threadSystem, Properties properties, TestResult logging, AdministrativeLocking locking, String owner1, String owner2) throws Exception;

  /**
   * Simple implementation of the start-method, which gets/creates some parameters
   * and forwards them to test().
   */ 
  public TestResult start(ThreadSystem threadSystem, AdministrativeLocking locking, Properties properties)
  {
    TestResult result = new TestResult();
    
    try
    {
      result.log("START TEST " + getName());
      result.log("-------------------------------------------------------");
      result.log("DESCRIPTION:");
      result.log(getDescription());
      result.log("-------------------------------------------------------");
      test(threadSystem, locking, properties, result);
      result.close(null);
    }
    catch (Exception e)
    {
      result.close(e);
    }
    return result;
  }
  
  /**
   * Creates owners and makes the real test.
   * Finally all locks for the owners are removed again.
   */
  protected void test(ThreadSystem threadSystem, AdministrativeLocking locking, Properties properties, TestResult logging) throws Exception
  {
    String owner1 = locking.createUniqueOwner();
    String owner2 = locking.createUniqueOwner();
    try
    {
      test(threadSystem, properties, logging, locking, owner1, owner2);
    }
    finally
    {
      boolean success = true;
      success &= cleanupOwner(locking, logging, owner1);
      success &= cleanupOwner(locking, logging, owner2); 
      if (! success)
        throw new LockingTechnicalLockException(LockingTechnicalLockException.TEST_CLEANUP_FAILED, new Object[] { owner1, owner2 });
    }
  }

  /**
   * Returns getName()
   */
  public String toString()
  {
    return getName();
  }  

    
  // ================= private helper-methods ==================================


  private boolean cleanupOwner(AdministrativeLocking administrativeLocking, TestResult logging, String owner)
  {
    try
    {
      administrativeLocking.unlockAll(owner, false);
      return true;
    }
    catch (Exception e)
    {
      logging.log("-------------------------------------------------------");
      logging.log("CRITICAL ERROR: CLEANUP FAILED");
      logging.log("SOME OF THE CREATED LOCKS COULD NOT BE REMOVED");
      logging.log("OWNER = [" + owner + "]");
      logging.log("-------------------------------------------------------");
      return false;
    }
  }
  

  // =============== helper-methods for sub-classes ============================
  
  
  protected void lockExpectException(AdministrativeLocking locking, String owner, String name, String argument, char mode) throws Exception
  {
    try 
    {
      locking.lock(owner, name, argument, mode);
    }
    catch (LockException e)
    {
      return;
    }
    throw new Exception("Expected LockException did not occur for " + getLockDescription(owner, name, argument, mode));
  }
  
  protected void lockExpectException(AdministrativeLocking locking, String owner, String name[], String argument[], char mode[]) throws Exception
  {
    try 
    {
      locking.lock(owner, name, argument, mode);
    }
    catch (LockException e)
    {
      return;
    }
    throw new Exception("Expected LockException did not occur for [" + owner + "]");
  }
  
  protected void lockExpectIllegalArgumentException(AdministrativeLocking locking, String owner, String name, String argument, char mode) throws Exception
  {
    try 
    {
      locking.lock(owner, name, argument, mode);
    }
    catch (IllegalArgumentException e)
    {
      return;
    }
    throw new Exception("Expected IllegalArgumentException did not occur for " + getLockDescription(owner, name, argument, mode));
  }
  
  protected void lockExpectIllegalArgumentException(AdministrativeLocking locking, String owner, String name[], String argument[], char mode[]) throws Exception
  {
    try 
    {
      locking.lock(owner, name, argument, mode);
    }
    catch (IllegalArgumentException e)
    {
      return;
    }
    throw new Exception("Expected IllegalArgumentException did not occur for [owner=" + owner + "]");
  }
  
  protected void unlockExpectIllegalArgumentException(AdministrativeLocking locking, String owner, String name, String argument, char mode) throws Exception
  {
    try 
    {
      locking.unlock(owner, name, argument, mode, false);
    }
    catch (IllegalArgumentException e)
    {
      return;
    }
    throw new Exception("Expected IllegalArgumentException did not occur for " + getLockDescription(owner, name, argument, mode));
  }
  
  protected void unlockExpectIllegalArgumentException(AdministrativeLocking locking, String owner, String name[], String argument[], char mode[]) throws Exception
  {
    try 
    {
      locking.unlock(owner, name, argument, mode, false);
    }
    catch (IllegalArgumentException e)
    {
      return;
    }
    throw new Exception("Expected IllegalArgumentException did not occur for [owner=" + owner + "]");
  }
    
  protected void checkLockEntries(LockEntry entries[], Integer expectedLength, String owner, String name, String argument, Character mode, Integer count) throws Exception
  {
    if (name != null) name = UTIL.adjustStringLengthRight(name, LockingConstants.MAX_NAME_LENGTH, LockingConstants.FILL_CHARACTER);
    if (argument != null)  argument  = UTIL.adjustStringLengthRight(argument, LockingConstants.MAX_ARGUMENT_LENGTH,   LockingConstants.FILL_CHARACTER);
    
    if (expectedLength != null && entries.length != expectedLength.intValue())
      throw new Exception("Received " + entries.length + " LockEntries, but expected " + expectedLength + " LockEntries");
    for (int i = 0; i < entries.length; i++)
    {
      LockEntry entry = entries[i];
      int differences = 0;
      if (owner != null && (! owner.equals(entry.getOwner()))) differences ++;
      if (name != null &&  (! name.equals(entry.getName()))) differences ++;
      if (argument != null &&   (! argument.equals(entry.getArgument()))) differences ++;
      if (mode != null &&  (mode.charValue() != entry.getMode())) differences ++;
      if (count != null && (count.intValue() != entry.getCount())) differences ++;
        
      if (differences > 0)
        throw new Exception("Received wrong LockEntry (" + differences + " differences):\n"
                            + "expected: [owner=" + owner + ", name=" + name + ", argument=" + argument + ", mode=" + mode + ", count=" + count + "]\n"
                            + "received: [owner=" + entry.getOwner() + ", name=" + entry.getName() + ", argument=" + entry.getArgument() + ", mode=" + entry.getMode() + ", count=" + entry.getCount() + "]\n");
    }
  }

  protected String getLockDescription(String owner, String name, String argument, char mode)
  {
    return "[owner=" + owner + ", name=" + name + ", argument=" + argument + ", mode=" + mode + "]";
  }
}
