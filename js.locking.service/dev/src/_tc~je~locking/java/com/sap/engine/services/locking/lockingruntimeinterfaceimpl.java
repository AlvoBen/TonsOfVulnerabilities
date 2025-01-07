/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/LockingRuntimeInterfaceImpl.java#40 $ SAP*/
package com.sap.engine.services.locking;

import java.util.Properties;

import javax.naming.NamingException;

import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.locking.LockEntry;
import com.sap.engine.frame.core.locking.LockEntryInfo;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.locking.SAPLockingIllegalArgumentException;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.locking.TimeStatistics;
import com.sap.engine.frame.core.locking.TimeStatisticsEntry;
import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.services.locking.test.AllTests;
import com.sap.engine.services.locking.test.ILockingContextTest;
import com.sap.engine.services.locking.test.TestResult;
import com.sap.tc.logging.Location;

/** 
 * An implementation of the LockingRuntimeInterface.
 * It extends PortableRemoteObject, so that it can be used remote.
 */
public class LockingRuntimeInterfaceImpl implements LockingRuntimeInterface {
  private static final Location LOCATION = Location.getLocation(LockingRuntimeInterfaceImpl.class);
  private final AdministrativeLocking _locking;

  /** Used for LockingContext (created in the constructor) */
  private LockingContext _lockingContext;

  /** The LockingApplicationFrame (given in the constructor) */
  private LockingApplicationFrame _frame;

  /**
   * Constructor
   */
  public LockingRuntimeInterfaceImpl(LockingApplicationFrame frame, LockingContext lockingContext) throws NamingException {
    _frame = frame;
    _lockingContext = lockingContext;
    _locking = lockingContext.getAdministrativeLocking();

    String METHOD = "<init>";
    LOCATION.pathT(METHOD, "begin");

    LOCATION.pathT(METHOD, "end");
  }

  /**
   * Registers the ManagementListener
   */
  public void registerManagementListener(ManagementListener managementListener) {
    // TODO
  }

  // =================== interface LockingRuntimeInterface =====================

  public LockingContext getLockingContext() {
    return _lockingContext;
  }

  public AdministrativeLocking getAdministrativeLocking() {
    return _lockingContext.getAdministrativeLocking();
  }

  public TestResult[] runTests(boolean nofunctional, boolean noload) {
    String METHOD = "runTests(nofunctional, noload)";

    LOCATION.pathT(METHOD, "nofunctional={0}, noload={1}", new Object[] { new Boolean(nofunctional), new Boolean(noload)});

    ILockingContextTest test[];
    if (nofunctional && noload)
      return new TestResult[0];
    else if (nofunctional && (!noload))
      test = AllTests.getLoadLockingContextTests();
    else if ((!nofunctional) && noload)
      test = AllTests.getFunctionalLockingContextTests();
    else
      test = AllTests.getAllLockingContextTests();

    TestResult result[] = new TestResult[test.length];
    for (int i = 0; i < test.length; i++) {
      LOCATION.pathT(METHOD, "start test {0}", new Object[] { test[i].getName()});
      result[i] = test[i].start(_frame.getThreadSystem(), getAdministrativeLocking(), null);
      LOCATION.pathT(METHOD, "test-result:\n{0}", new Object[] { result[i].getLog()});
    }

    LOCATION.pathT(METHOD, "finished");
    return result;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.locking.LockingRuntimeInterface#getLocks(String, String)
   */
  public Properties[] getLocks(String name, String argument, String user) throws TechnicalLockException, IllegalArgumentException {
    LockEntry[] lockEntry = _locking.getLocks(name, argument, user);
    Properties[] result = transformToProperties(lockEntry);
    return result;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.locking.LockingRuntimeInterface#getLockDetails(Properties)
   */
  public Properties getLockDetails(Properties lockEntry) throws IllegalArgumentException {
    LockEntry lockEntryInternal = transformToLockEntry(lockEntry);
    if (lockEntryInternal == null)
      new SAPLockingIllegalArgumentException(SAPLockingIllegalArgumentException.PARAMETER_NULL, new Object[] { "lockEntry" });
    LockEntryInfo lockEntryInfo = _locking.getLockEntryInfo(lockEntryInternal);
    String namespace = lockEntryInfo.getNamespace();
    String namespaceDescription = lockEntryInfo.getNamespaceDescription();
    lockEntry.setProperty(NAMESPACE, namespace);
    lockEntry.setProperty(NAMESPACE_DESCRIPTION, namespaceDescription);
    return lockEntry;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.locking.LockingRuntimeInterface#lock(java.lang.String, java.lang.String, java.lang.String, char)
   */
  public void lock(String owner, String name, String argument, char mode) throws LockException, TechnicalLockException, IllegalArgumentException {
    _locking.lock(owner, name, argument, mode);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.locking.LockingRuntimeInterface#unlock(java.lang.String, java.lang.String, java.lang.String, char, boolean)
   */
  public void unlock(String owner, String name, String argument, char mode, boolean asynchronous)
    throws TechnicalLockException, IllegalArgumentException {
    _locking.unlock(owner, name, argument, mode, asynchronous);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.locking.LockingRuntimeInterface#unlockAll(java.lang.String, boolean)
   */
  public void unlockAll(String owner, boolean asynchronous) throws TechnicalLockException, IllegalArgumentException {
    _locking.unlockAll(owner, asynchronous);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.locking.LockingRuntimeInterface#unlockAllCumulativeCounts(java.lang.String, java.lang.String, java.lang.String, char)
   */
  public void unlockAllCumulativeCounts(String owner, String name, String argument, char mode)
    throws TechnicalLockException, IllegalArgumentException {
    _locking.unlockAllCumulativeCounts(owner, name, argument, mode, false);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.locking.LockingRuntimeInterface#unlockAllGeneric(java.lang.String, boolean)
   */
  public void unlockAllGeneric(String owner, boolean asynchronous) throws TechnicalLockException, IllegalArgumentException {
    _locking.unlockAllGeneric(owner, asynchronous);
  }

  /* (non-Javadoc)
     * @see com.sap.engine.services.locking.LockingRuntimeInterface#genericLockOp(java.util.Properties[])
     */
  public void genericLockOp(Properties properties) throws LockException, TechnicalLockException, IllegalArgumentException {
    String action = properties.getProperty(ACTION);

    String owner = null;
    String name = null;
    String argument = null;
    String modeString = null;
    char mode = ' ';
    boolean lockMode = false;

    if (action.equals(OP_ENQ)) {
      owner = properties.getProperty(OWNER);
      name = properties.getProperty(NAME);
      argument = properties.getProperty(ARGUMENT);
      modeString = properties.getProperty(MODE);
      try {
        mode = modeString.charAt(0);
      } catch (Exception e) {
        new SAPLockingIllegalArgumentException(SAPLockingIllegalArgumentException.PARAMETER_NULL, new Object[] { "mode" });
      }
      _locking.lock(owner, name, argument, mode);
    } else if (action.equals(OP_DEQ)) {
      owner = properties.getProperty(OWNER);
      name = properties.getProperty(NAME);
      argument = properties.getProperty(ARGUMENT);
      modeString = properties.getProperty(MODE);
      String actionModeString = properties.getProperty(ACTION_MODE);
      try {
        mode = modeString.charAt(0);
      } catch (Exception e) {
        new SAPLockingIllegalArgumentException(SAPLockingIllegalArgumentException.PARAMETER_NULL, new Object[] { "mode" });
      }
      if (null != actionModeString)
        lockMode = actionModeString.equals(ASYNC);
      _locking.unlock(owner, name, argument, mode, lockMode);
    } else if (action.equals(OP_DEQALL)) {
      owner = properties.getProperty(OWNER);
      String actionModeString = properties.getProperty(ACTION_MODE);
      if (null != actionModeString)
        lockMode = actionModeString.equals(ASYNC);
      _locking.unlockAll(owner, lockMode);
    } else if (action.equals(OP_DEQALL_GENERIC)) {
      owner = properties.getProperty(OWNER);
      String actionModeString = properties.getProperty(ACTION_MODE);
      if (null != actionModeString)
        lockMode = actionModeString.equals(ASYNC);
      _locking.unlockAllGeneric(owner, lockMode);
    } else if (action.equals(OP_REMOVE)) {
      owner = properties.getProperty(OWNER);
      name = properties.getProperty(NAME);
      argument = properties.getProperty(ARGUMENT);
      modeString = properties.getProperty(MODE);
      String actionModeString = properties.getProperty(ACTION_MODE);
      try {
        mode = modeString.charAt(0);
      } catch (Exception e) {
        new SAPLockingIllegalArgumentException(SAPLockingIllegalArgumentException.PARAMETER_NULL, new Object[] { "mode" });
      }
      if (null != actionModeString)
        lockMode = actionModeString.equals(ASYNC);
      _locking.unlockAllCumulativeCounts(owner, name, argument, mode, lockMode);
    }
  }

  /**
    * Activates the client-side time measurement on the current node.
    */
  public void enableTimeStatistics() {
    TimeStatistics timeStatistics = _locking.getTimeStatistics();
    timeStatistics.setTimeStatisticsLevel(1);
  }

  /**
   * Diesables the client-side time measurement on the current node.
   */
  public void disableTimeStatistics() {
    TimeStatistics timeStatistics = _locking.getTimeStatistics();
    timeStatistics.setTimeStatisticsLevel(0);
  }

  /* (non-Javadoc)
     * @see com.sap.engine.services.locking.LockingRuntimeInterface#getTimeStatisticsEntries()
     */
  public Properties[] getTimeStatisticsEntries() {
    TimeStatisticsEntry[] timeStatisticsEntry = _locking.getTimeStatisticsEntries();
    Properties[] result = transformToProperties(timeStatisticsEntry);
    return result;
  }

  /* (non-Javadoc)
    * @see com.sap.engine.services.locking.LockingRuntimeInterface#resetTimeStatistics()
    */
  public void resetTimeStatistics() {
    _locking.resetTimeStatistics();
  }

  /**
    * Activates the logging functionality in the locking server.
    * 
    * @exception TechnicalLockException if activation of the server-side logging failed
    */
  public void enableServerLogging() throws TechnicalLockException {
    _locking.enableServerLogging();
  }

  /**
   * Disables the logging functionality in the locking server.
   * 
   * @exception TechnicalLockException if disabling of the server-side logging failed
   */
  public void disableServerLogging() throws TechnicalLockException {
    _locking.disableServerLogging();

  }

  //******************** private methods **************************************

  private Properties[] transformToProperties(Object[] tbt) {
    if (null == tbt || tbt.length == 0 || null == tbt[0])
      return new Properties[0];

    int length = tbt.length;
    Properties[] result = new Properties[length];

    if (tbt[0] instanceof LockEntry) {
      for (int i = 0; i < tbt.length; i++) {
        LockEntry lockEntry = (LockEntry) tbt[i];
        result[i] = new Properties();
        result[i].setProperty(TYPE, LOCK);
        result[i].setProperty(OWNER, lockEntry.getOwner());
        result[i].setProperty(USER, lockEntry.getUser());
        result[i].setProperty(NAME, lockEntry.getName());
        result[i].setProperty(ARGUMENT, lockEntry.getArgument());
        result[i].setProperty(MODE, String.valueOf(lockEntry.getMode()));
        result[i].setProperty(COUNT, String.valueOf(lockEntry.getCount()));
      }
    } else if (tbt[0] instanceof TimeStatisticsEntry) {
      for (int i = 0; i < tbt.length; i++) {
        TimeStatisticsEntry tsEntry = (TimeStatisticsEntry) tbt[i];
        result[i] = new Properties();
        result[i].setProperty(TYPE, TS_ELEMENT);
        result[i].setProperty(TS_DESCR, tsEntry.getDescription());
        result[i].setProperty(TS_MIN, String.valueOf(tsEntry.getMinDelta()));
        result[i].setProperty(TS_MAX, String.valueOf(tsEntry.getMaxDelta()));
        result[i].setProperty(TS_AVG, String.valueOf(tsEntry.getAverageDelta()));
        result[i].setProperty(TS_COUNT, String.valueOf(tsEntry.getCounts()));
      }
    }
    return result;
  }

  private LockEntry transformToLockEntry(Properties lock) {
    if (lock == null) //should not happen
      return null;
    LockEntry result = null;
    String user = lock.getProperty(USER);
    String owner = lock.getProperty(OWNER);
    String name = lock.getProperty(NAME);
    String argument = lock.getProperty(ARGUMENT);
    String countString = lock.getProperty(COUNT);
    String modeString = lock.getProperty(MODE);
    int count = -1;
    char mode = ' ';
    try{
     count = Integer.parseInt(countString);
     mode = modeString.charAt(0);
    }catch(Exception e) {
      return null;
    }
    result = new LockEntry(user, owner, name, argument, count, mode);
    return result;
  }
}