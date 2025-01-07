/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/LockingRuntimeInterface.java#18 $ SAP*/
package com.sap.engine.services.locking;

import java.rmi.Remote;
import java.util.Properties;

import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.services.locking.test.TestResult;

/**
 * The runtime interface of the locking service.
 * It is exactly the same as the AdministrativeLocking.
 *
 * @see com.sap.engine.frame.core.locking.AdministrativeLocking
 */
public interface LockingRuntimeInterface extends LockingConstants, ManagementInterface, Remote {
  //Property object as container 

  //definition of keys in the property object
  //typed property object 
  public final static String TYPE = "type"; //lock description, timestatistics element

  //lock description
  public final static String OWNER = "owner";
  public final static String USER = "user";
  public final static String NAME = "name";
  public final static String ARGUMENT = "argument";
  public final static String MODE = "mode";
  public final static String COUNT = "count";

  //lock details 
  public final static String NAMESPACE = "namespace";
  public final static String NAMESPACE_DESCRIPTION = "nsdesciption";

  //timestatistics entries
  public final static String TS_DESCR = "ts_descr";
  public final static String TS_MIN = "ts_min";
  public final static String TS_MAX = "ts_max";
  public final static String TS_AVG = "ts_avg";
  public final static String TS_COUNT = "ts_count";

  //definition of values in the property object
  //type identifier of the properties object
  public final static String LOCK = "L";
  public final static String TS_ELEMENT = "T";

  //lock modes -> see definition in LockingConstants
  //  public final static String MODE_SHARED_STRING = "S";
  //  public final static String MODE_EXCLUSIVE_CUMULATIVE_STRING = "E";
  //  public final static String MODE_EXCLUSIVE_NONCUMULATIVE_STRING = "X";

  //action types and modes (synchronous, asynchronous)
  //only needed for genericLockOp()
  public final static String ACTION = "action";
  public final static String ACTION_MODE = "action_mode";

  //Lock action types
  //only needed for genericLockOp()
  public final static String OP_ENQ = "1";
  public final static String OP_DEQ = "2";
  public final static String OP_DEQALL = "3";
  public final static String OP_DEQALL_GENERIC = "4";
  public final static String OP_REMOVE = "R";

  //Lock action modes
  //only needed for genericLockOp
  public final static String SYNC = "0";
  public final static String ASYNC = "1";

  //********************** already defined operations *********************************

  public LockingContext getLockingContext();

  public AdministrativeLocking getAdministrativeLocking();

  public TestResult[] runTests(boolean nofunctional, boolean noload);
  

  //******************** new methods that comply to the new jmx model *****************

  /**
   * Returns all current locks held in the message server that match the arguments.
   * @param name name of the lock
   * @param argument of the lock
   * @param user of the lock
   * @return Properties[] all locks that match the arguments
   * @see #OWNER
   * @see #NAME
   * @see #ARGUMENT
   * @see #COUNT
   * @see #MODE
   * @see #USER
   */
  public Properties[] getLocks(String name, String argument, String user) throws TechnicalLockException, IllegalArgumentException;

  /**
   * Returns the lock entry enriched by the namespace and namespace description
   * @param lockEntry full specified lock entry
   * @return Properties lock entry enriched by namespace and namespace description
   * @see #NAMESPACE
   * @see #NAMESPACE_DESCRIPTION
   */
  public Properties getLockDetails(Properties lockEntry) throws IllegalArgumentException;

  /**
   * Locks an argument, which belongs to a name, which belongs to an owner.
   * 
   * @param owner The owner to which the name belongs. The maximum size is MAX_OWNER_LENGTH characters.
   * @param name The name, to which the argument belongs. The maximum size is MAX_NAME_LENGTH characters. 
   * @param argument The argument which to lock. The maximum size is MAX_ARGUMENT_LENGTH characters.
   * @param mode The mode can be MODE_SHARED or MODE_EXCLUSIVE_CUMULATIVE or MODE_EXCLUSIVE_NONCUMULATIVE.
   * 
   * @exception LockException if the argument is already locked
   */
  public void lock(String owner, String name, String argument, char mode) throws LockException, TechnicalLockException, IllegalArgumentException;

  /**
   * Unlocks an argument, which belongs to a name, which belongs to an owner.
   * 
   * @param owner The owner, to which the name belongs. The maximum size is MAX_OWNER_LENGTH characters.
   * @param name The name, to which the argument belongs. The maximum size is MAX_NAME_LENGTH characters. 
   * @param argument The argument which to unlock. The maximum size is MAX_ARGUMENT_LENGTH characters.
   * @param mode The mode can be MODE_SHARED or MODE_EXCLUSIVE_CUMULATIVE or MODE_EXCLUSIVE_NONCUMULATIVE.
   * @param asynchronous If true, then the unlock may be done asynchronously, otherwise it is guaranteed to be done synchronously.
   */
  public void unlock(String owner, String name, String argument, char mode, boolean asynchronous)
    throws TechnicalLockException, IllegalArgumentException;

  /**
   * Unlocks all names/arguments, which belong to an owner.
   * 
   * @param The owner, to which the names/arguments belongs. The maximum size is MAX_OWNER_LENGTH characters.
   * @param asynchronous If true, then the unlock may be done asynchronously, otherwise it is guaranteed to be done synchronously.
   */
  public void unlockAll(String owner, boolean asynchronous) throws TechnicalLockException, IllegalArgumentException;

  /**
   * ATTENTION: this method is for administrative purposes only and must be
   * used with care!
   * Unlocks all names/arguments, which belong to an owner. The owner can contain 
   * several wildard-characters OWNER_WILDCARD_CHARACTER, which represent
   * a single arbitrary character.
   * 
   * @param The owner, to which the names/arguments belongs. The maximum size is MAX_OWNER_LENGTH characters.
   * @param asynchronous If true, then the unlock may be done asynchronously, otherwise it is guaranteed to be done synchronously.
   */
  public void unlockAllGeneric(String owner, boolean asynchronous) throws TechnicalLockException, IllegalArgumentException;

  /**
   * This mehtod unlocks all cumulative counts for this lock, whereas the method
   * unlock() only unlocks one single count.
   * 
   * @param The owner, to which the name belongs. The maximum size is MAX_OWNER_LENGTH characters.
   * @param name The name, to which the argument belongs. The maximum size is MAX_NAME_LENGTH characters. 
   * @param argument The argument which to unlock. The maximum size is MAX_ARGUMENT_LENGTH characters.
   * @param mode The mode can be MODE_SHARED or MODE_EXCLUSIVE_CUMULATIVE or MODE_EXCLUSIVE_NONCUMULATIVE.
   * @param asynchronous If true, then the unlock may be done asynchronously, otherwise it is guaranteed to be done synchronously.
   */
  public void unlockAllCumulativeCounts(String owner, String name, String argument, char mode)
    throws TechnicalLockException, IllegalArgumentException;

  /**
     * Generic lock operation that can be used instead of the explicitly declared
     * lock, unlock operations.
     * 
     * @param properties specifies the lock arguments
     * @throws LockException lock collision occured 
     * @throws TechnicalLockException technical problems
     * @throws IllegalArgumentException specified argument does mot adhere to the contract
     * @see #OP_ENQ
     * @see #OP_DEQ
     * @see #OP_DEQALL
     * @see #OP_DEQALL_GENERIC
     * @see #OP_REMOVE
     */
  public void genericLockOp(Properties properties) throws LockException, TechnicalLockException, IllegalArgumentException;

  /**
   * Activates the client-side time measurement on the current node.
   */
  public void enableTimeStatistics();

  /**
   * Diesables the client-side time measurement on the current node.
   */
  public void disableTimeStatistics();

  /**
   * Returns the current time-statistic entries of the current node.
   * 
   * @return The current time-statistic entries.
   * @see TS_DESCR
   * @see TS_MIN
   * @see TS_MAX
   * @see TS_AVG
   * @see TS_COUNT
   */
  public Properties[] getTimeStatisticsEntries();

  /**
   * Resets the TimeStatistics of the current node.
   */
  public void resetTimeStatistics();

  /**
    * Activates the logging functionality in the locking server.
    * 
    * @exception TechnicalLockException if activation of the server-side logging failed
    */
  public void enableServerLogging() throws TechnicalLockException;

  /**
   * Disables the logging functionality in the locking server
   * 
   * @exception TechnicalLockException if disabling of the server-side logging failed
   */
  public void disableServerLogging() throws TechnicalLockException;

}