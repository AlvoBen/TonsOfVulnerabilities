/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.memory.server;

import java.util.*;

import com.sap.engine.debug.Debug;
import com.sap.engine.services.memory.MSManagement;
import com.sap.engine.services.memory.MemoryListener;
import com.sap.engine.services.memory.MemoryManager;
import com.sap.engine.services.memory.MemoryManagerException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class is used for managing usage of memory available for a JVM.
 * To allow this class to notify some other class about memory usage the other
 * class must implements <code>MemoryBalance</code> interface. When the object
 * is created it is registered by using <code>register</code> method of
 * MemoryManager class. Once the object is regestered it will be notified for
 * memory usage every time the usage changes from one level to other. These levels
 * are defined in advance.
 *
 * @author Doichin Tsvetkov
 * @version 4.01
 */
public final class MemoryManagerImpl implements
/*TimeoutListener, */
                                     MemoryManager {

  /**
   * The Default size of the array holding MemoryListener objects.
   * This default value is <code>10</code>.
   */
  public static final int DEFAULT_MEMORY_BALANCE_ARRAY_OBJECTS = 10;
  /**
   * One megabyte
   */
  public static final int ONE_MEGABYTE = 1024 * 1024;
  /**
   * Indicate after how many "free" calls, must start Garbage Collector.
   * The default is 5.
   */
  public static final int GC_CALL = 5;
  /*
   * limits of memory usage for going from one level to other.
   */
  private byte[] memoryLevels = {40, 55, 65, 71, 75, 100};
  /*
   * how often to check memory usage (in millis) for current level of usage of memory (0, 1, 2 ...)
   */
  private long[] sleepTimes = {2500, 2000, 1500, 1000, 500, 200};
  /*
   * Runtime object, from which can obtain system information about memory.
   */
  private Runtime runtime = null;
  /*
   * An Array holding MemoryListener objects.
   */
  private MemoryListener[] mbArray = null;
  /*
   * An array holding IDs for registered MemoryListener objects.
   */
  private int[] mbIDs = null;
  /*
   * Count of registering Memory Balance objects.
   */
  private int count = 0;
  /*
   * Index of current memory level.
   */
  protected int level = 0;
  /*
   * Usage of memory in a moment of checking.
   */
  protected int iCurrentUsage = 0;
  /*
   * Usage of memory in a previous moment of checking.
   */
  private int iPrevUsage = 0;
  /*
   * Indicates a maximal index of Memory Balance objects.
   */
  private int maxID = 1;
  /*
   * If the usage of the memory becomes smaller than the dLimit
   * then the index of the current memory level decreases.
   */
  private int dLimit = 0;
  /*
   * The TimeoutManger to which the MemoryManger is registered
   */
  //  private TimeoutManager timeoutManager = null;
  /*
   * Shows if the <code>MemoryManager</code> is started.
   * Takes <code>true </code> value in the <code>init</code> method.
   */
  private boolean isStarted = false;
  /*
   * Shows if the <code>shutDown()</code> method is called.
   */
  private boolean isShutDown = false;
  /*
   * The diagram with the names of the visualized graphics.
   */
  private String[] statDiagram = {"Registered objects", "Current memory usage", "Current memory level"};
  /*
   * The max possible values of the statistics.
   */
  private int[] maxStatisticValues = {-1, 100, 5};
  /*
   * The statistic values.
   */
  private int[] statisticValues = new int[3];
  private String snapShotPath = null;
  private MemoryManagerSnapShotProvider snapShotProvider = null;
  protected MSManagement manInterface = null;

  /**
   * Category used for logging
   */
  public static Category category = Category.getCategory("/System/Server");

  /**
   * Location used for logging
   */
  public static Location location = Location.getLocation(MemoryManagerImpl.class);

  /**
   * Constructor. Creates an instance of MemoryManager.
   */
  public MemoryManagerImpl() {

  }

  // new from the interface
  public long getMaxMemory() {
    return runtime.maxMemory();
  }

   // new from the interface
  public boolean isFree(long memory) {
    return (getFreeMemory() > memory);
  }

  /**
   * Returns the used memory of the JVM.
   */
  private long getUsedMemory() {
    return (runtime.totalMemory() - runtime.freeMemory());
  }

  /**
   * Returns all the free memory of the JVM. Including the heap.
   */
  public long getFreeMemory() {
    return runtime.maxMemory() - getUsedMemory();
  }

  /**
   * Registers a <code>MemoryListener</code> object and starts to inform that object about
   * the level of usage of system memory.
   *
   * @param memoryBalance - object that is going to be registered
   *
   * @return an unique value used for unregistering this MemoryListener object
   *
   * @exception MemoryManagerException if method <code>init</code>
   *                                  is not yet called or method <code>shutDown</code> is already called.
   * @exception IllegalArgumentException if <code>null</code> argument is passed.
   *
   */
  public synchronized int registerMemoryListener(MemoryListener memoryBalance) throws MemoryManagerException, IllegalArgumentException {
//    if (!isStarted) {
//      //      MemoryApplicationFrame.log(LogContext.ERROR, "ID000200: Can't register new MemoryListener. The MemoryManager is not started properly yet.");
//      throw new MemoryManagerException("ID000200: The MemoryManager is not started properly yet.");
//    }
//
//    if (isShutDown) {
//      //      MemoryApplicationFrame.log(LogContext.ERROR, "ID000201: Can't register new MemoryListener. The system is in process of shutting down.");
//      throw new MemoryManagerException("ID000201: The system is in process of shutting down.");
//    }
//
//    if (memoryBalance == null) {
//      //      MemoryApplicationFrame.log(LogContext.ERROR, "ID000202: Can't register new MemoryListener.Null object can't be registered.");
//      throw new IllegalArgumentException("ID000202: Null object can't be registered.");
//    }
//
//    //{{IF THE ARRAY IS FULL, MUST RESIZE IT
//    if (count >= mbArray.length) {
//      MemoryListener[] mb1 = new MemoryListener[mbArray.length * 2];
//      System.arraycopy(mbArray, 0, mb1, 0, count);
//      mbArray = mb1;
//      int[] h = new int[mbIDs.length * 2];
//      System.arraycopy(mbIDs, 0, h, 0, count);
//      mbIDs = h;
//    }
//
//    //}}
//    //{{ ENTER A NEW MEMORY BALANCE OBJECT IN ARRAY
//    mbArray[count] = memoryBalance;
//    mbIDs[count] = maxID;
//    //}}
//    count++;
//    return maxID++;
    return 0;
  }

  /**
   * Unregisters the <code>MemoryListener</code> object mapped with this key.
   * If there is not an object with such a key the method throws
   * <code>MemoryManagerException</code>.
   *
   * @param key - the value returned by <code>register</code> method.
   * @exception MemoryManagerException if there is not registered object
   *                                with key <code>key</code>.
   */
  public synchronized void unregisterMemoryListener(int key) throws MemoryManagerException {
//    if (isShutDown) {
//      //      MemoryApplicationFrame.log(LogContext.ERROR, "ID000203: Can't unregister a MemoryListener. The system is in process of shutting down.");
//      throw new MemoryManagerException("ID000203: The system is in process of shutting down.");
//    }
//
//    for (int i = 0; i < count; i++) {
//      if (key == mbIDs[i]) {
//        count--;
//        mbArray[i] = mbArray[count];
//        mbIDs[i] = mbIDs[count];
//        mbArray[count] = null;
//        return;
//      }
//    }
//
//    //    MemoryApplicationFrame.log(LogContext.ERROR, "ID000204: Can't unregister a MemoryListener. There is not registered object with key " + key + ".");
//    throw new MemoryManagerException("ID000204: There is not registered object with key " + key + ".");
  }

  /**
   * Does the actual work for checking current usage of memory and notifing
   * registered MemoryListener objects if used memory changes from one level
   * to other.
   *
   * @exception RuntimeException if can't refresh itself.
   */
  public void timeout() throws RuntimeException {
    int iCallNumber = 0;
    synchronized (this) {
      if (isShutDown) {
        return;
      }

      memoryUsage();

      //{{DEBUG
      if (Debug.MemoryManagmentDebug) {
        location.debugT(MemoryResourceAccessor.formatString(MemoryResourceAccessor.LEVEL_MEMORY,
                                                            new Object[]{new Integer(level), new Integer(iCurrentUsage), new Integer(iPrevUsage), new Integer(memoryLevels[level])}));

      }

      //}}
      if (iCurrentUsage > iPrevUsage) {
        if (iCurrentUsage >= memoryLevels[level]) {
          setCurrentLevel();
          location.debugT(MemoryResourceAccessor.formatString(MemoryResourceAccessor.INCREASING_LEVEL,
                                                              new Object[]{new Integer(level)}));
          //          try {
          //          timeoutManager.changeRepeatTime(this, sleepTimes[level]);
          //          } catch(TimeoutException e) {
          //            logManager.log(logID, (byte)3, "ID000205:Can't refresh the MemoryManager.");
          //            throw new RuntimeException(e.getMessage() + "\nID000205:Can't refresh the MemoryManager.");
          //          }
          for (int i = 0; i < count; i++) {
            mbArray[i].notify(level - 1, true);

            /*
             * In every GC_CALL-th call of method free, of interface Memory Balance
             * must start Garbage Collector, to clean by memory unused objects.
             */
            if (++iCallNumber == GC_CALL) {
              runtime.gc();
              iCallNumber = 0;
            }

            //{{DEBUG
            if (Debug.MemoryManagmentDebug) {
              location.debugT(MemoryResourceAccessor.formatString(MemoryResourceAccessor.FREEING_MEMORY,
                                                                  new Object[]{new Integer(i), new Integer(count)}));
            }

            //}}
          }//end of for cicle

          runtime.gc();
          iCallNumber = 0;
        }
      } else {
        // if we want we may notify that there is enough free memory to load some objects
        if (iCurrentUsage < dLimit) {
          setCurrentLevel();
          location.debugT(MemoryResourceAccessor.formatString(MemoryResourceAccessor.DECREASING_LEVEL,
                                                              new Object[]{new Integer(level)}));
          //          try {
          //          timeoutManager.changeRepeatTime(this, sleepTimes[level]);
          //          } catch(TimeoutException e) {
          //            logManager.log(logID, (byte)3, "ID000206:Can't refresh the MemoryManager.");
          //            throw new RuntimeException(e.getMessage() + "\nID000206:Can't refresh the MemoryManager.");
          //          }
          for (int i = 0; i < count; i++) {
            mbArray[i].notify(level, false);

            if (++iCallNumber == GC_CALL) {
              runtime.gc();
              iCallNumber = 0;
            }
          }//end of for

          runtime.gc();
          iCallNumber = 0;
        } //if
      }//else
    }//synchronization block
  }

  public boolean check() {
    long free = runtime.freeMemory();
    long total = runtime.totalMemory();
    int usage = (int) (((total - free) * 100) / getMaxMemory());

    if (((usage > iCurrentUsage) && (usage >= memoryLevels[level])) || ((usage < iCurrentUsage) && (usage < memoryLevels[level]))) {
      return true;
    } else {
      return false;
    }
  }

  /*
   * Sets  level and dLimit to be like current memory usage
   */
  private void setCurrentLevel() {
    for (int i = 0; i < memoryLevels.length; i++) {
      level = i;

      if (iCurrentUsage < memoryLevels[i]) {
        break;
      }
    }

    dLimit = (level > 1) ? memoryLevels[level - 2] : (level == 1) ? memoryLevels[level] / 2 : 0;
  }

  /*
   * Calculates the level of usage of system memory.
   *
   */
  private void memoryUsage() {
    float free = runtime.freeMemory();
    float total = runtime.totalMemory();
    iPrevUsage = iCurrentUsage;
    iCurrentUsage = (int) (((total - free) / getMaxMemory()) * 100);
  }

  // extended from Manager class
  /**
   * Informs the <code>MemoryManager</code> to stop to register objects.
   *
   * @param p - this parameter is not used in this method.
   It's included only to implement the <code>Manager<code> interface.
   */
  public synchronized void shutDown(Properties p) {
    mbArray = null;
    mbIDs = null;
    isShutDown = true;
    location.pathT(MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_MANAGER_STOPPED));
  }

  /**
   * Sets the initial values of some internal variables.
   * On the exit of this method the <code>MemoryManager</code> is ready to work.
   *
   * @return false if can't get <code>LogManager</code> and <code>TimeoutManager</code>
   *         or the method <code>init</code> is already invoked.
   *         true  otherwise
   */
  public synchronized boolean init(Properties mp) { //, TimeoutManager tm) {
    String s = null;

    if (isStarted) {
      return false;
    }

    mbArray = new MemoryListener[DEFAULT_MEMORY_BALANCE_ARRAY_OBJECTS];
    mbIDs = new int[DEFAULT_MEMORY_BALANCE_ARRAY_OBJECTS];
    runtime = Runtime.getRuntime();
    count = 0;

    if (mp == null) {
      mp = new Properties();
    }

    //    timeoutManager = tm;
    //    if (timeoutManager == null) {
    //      throw  new RuntimeException("ID000210:Can't get TimeoutManager");
    //      try {
    //        MemoryApplicationFrame.log(LogContext.ERROR, "ID000210: Can't initialize MemoryManager. Can't get TimeoutManager");
    //      } catch (IllegalArgumentException e) {
    //
    //      }
    //      return false;
    //    }

    s = mp.getProperty("MemoryLevels");

    if (s != null) {
      if (!setMemoryLevels(s)) {
        return false;
      }
    }//if(s != null)

    s = mp.getProperty("SleepTimes");

    if (s != null) {
      if (!setSleepTimes(s)) {
        return false;
      }
    }//if(s != null)

    memoryUsage();
    setCurrentLevel();
    //    try {
    //    try {
    //timeoutManager.registerTimeoutListener(this, 0, sleepTimes[level]);
    //    } catch (IllegalArgumentException e) {
    //      try {
    //        MemoryApplicationFrame.log(LogContext.ERROR, "ID000213: Can't initialize MemoryManager. MemoryMnager can't be registered to TimeoutManager.");
    //      } catch (IllegalArgumentException e1) {
    //
    //      }
    //      return false;
    //    }
    //    } catch(TimeoutException e) {
    //      throw new MemoryManagerException(e.getMessage() +
    //                                 "\nID000214:MemoryManager can't be registered.");
    //    }
    /////
    //  get path for the snapshot provider
    //    snapShotPath = "root." + getClusterElementInfo()[0]+ ".Memory Monitor.Memory Manager";
    snapShotPath = mp.getProperty("SnapShotPath", "root.Application Server.Memory Monitor.Memory Manager");
    isStarted = true;
    manInterface = new MSManagement(this);

    location.pathT(MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_MANAGER_STARTED));
    return true;
  }

  //  private static final String[] getClusterElementInfo() {
  //    String[] result = new String[4];
  //    System.out.println(Framework.getManager("ClusterManager"));
  //    ClusterElement host = (ClusterElement) ((ClusterManager) Framework.getManager("ClusterManager")).getCurrentClusterParticipant();
  //    result[0] = host.getName();
  //    result[1] = "" + host.getClusterId();
  //    result[2] = host.getAddress().toString();
  //    result[3] = "" + host.getJoinPort();
  //
  //    return result;
  //  }
  /*
   * This method is invoked by init method if the "MemoryLevels" property exist.
   * The "MemoryLevels" property must have the format:
   * {byte0,byte1,byte2,byte3,byte4}
   * where bytei are proper bytes.
   * Bytes must be in increasing order and must contain positive values less than 100.
   * If the property has incorrect value then this method returns false.
   */
  private boolean setMemoryLevels(String s) {
    StringTokenizer strTok = null;
    byte[] tempMemoryLevels = new byte[6];
    byte temp = 0;
    int i = 0;
    int j = 0;
    int k = 0;
    tempMemoryLevels[5] = 100;
    i = s.indexOf('{');
    j = s.lastIndexOf('}');

    if ((i >= 0) && (i < j)) {
      strTok = new StringTokenizer(s.substring(i + 1, j), ",", false);

      if (strTok.countTokens() != 5) {
        category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_LEVELS_NUMBER_INVALID));
        return false;
      }

      for (k = 0; k < 5; k++) {
        try {
          temp = Byte.parseByte(strTok.nextToken().trim());

          if ((temp <= 0) || (temp >= 100)) {
            category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_LEVELS_PROPERTY_INVALID));
            return false;
          }
        } catch (NumberFormatException e) {
          if (category.beWarning()) {
            category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_LEVELS_NOT_PROPER_BYTE_VALUE));
            category.logThrowableT(Severity.WARNING, location, "setMemoryLevels(String)", e);
          }
          return false;
        } //try-catch
        tempMemoryLevels[k] = temp;
      } //for

      for (k = 1; k < 5; k++) {
        if (tempMemoryLevels[k] <= tempMemoryLevels[k - 1]) {
          category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_LEVELS_NOT_IN_INCREASING_ORDER));
          return false;
        }
      }
    } else {
      category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_LEVELS_HAS_INCORRECT_FORMAT));
      return false;
    }

    memoryLevels = tempMemoryLevels;
    return true;
  }

  /*
   * This method is invoked by init method if "SleepTimes" property exist.
   * The "SleepTimes" property must have the format:
   * {long0,long1,long2,long3,long4,long5}
   * where longi are proper long.
   * If the property has incorrect value then this method returns false.
   */
  private boolean setSleepTimes(String s) {
    StringTokenizer strTok = null;
    long[] tempSleepTimes = new long[6];
    long temp = 0;
    int k = 0;
    int i = 0;
    int j = 0;
    i = s.indexOf('{');
    j = s.lastIndexOf('}');

    if ((i >= 0) && (i < j)) {
      strTok = new StringTokenizer(s.substring(i + 1, j), ",", false);

      if (strTok.countTokens() != 6) {
        category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.SLEEP_TIMES_NUMBER_INVALID));
        return false;
      }

      for (k = 0; k < 6; k++) {
        try {
          temp = Long.parseLong(strTok.nextToken().trim());

          if (temp < 0) {
            category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.SLEEP_TIMES_HAS_NEGATIVE_VALUE));
            return false;
          }
        } catch (NumberFormatException e) {
          if (category.beWarning()) {
            category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.SLEEP_TIMES_NOT_PROPER_LONG_VALUE));
            category.logThrowableT(Severity.WARNING, location, "setSleepTimes(String)", e);
          }
          return false;
        }
        tempSleepTimes[k] = temp;
      }

      for (k = 1; k < 6; k++) {
        if (tempSleepTimes[k] > tempSleepTimes[k - 1]) {
          category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.SLEEP_TIMES_NOT_DECREASING));
          break;
        }
      }
    } else {
      category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.SLEEP_TIMES_HAS_INCORRECT_FORMAT));
      return false;
    }

    sleepTimes = tempSleepTimes;
    return true;
  }

  /**
   * Trys to change some internal variables.
   * Throws <code>RuntimeException</code> if the 'SleepTimes' property exists
   * and can't refresh itself.
   *
   * @return true if it is not necessary to restart the system
   *         false in other case
   */
  public synchronized boolean changeProperties(Properties mp) {
    //    Properties oldProp = null;
    //    try {
    //      oldProp = logManager.getProperties(logID);
    //    } catch (IllegalArgumentException iae) {
    //      MemoryApplicationFrame.log(LogContext.INFO, "ID000226 : changeProperties method: Can't take properties from LogManager!");
    //      return false;
    //    }
    //    Enumeration en = oldProp.propertyNames();
    //    String key = null;
    //    Object obj = null;
    //    String oldValue = null;
    //    String newValue = null;
    //
    //    while (en.hasMoreElements()) {
    //      obj = en.nextElement();
    //
    //      if (mp.containsKey(obj)) {
    //        oldValue = oldProp.getProperty((String) obj);
    //        newValue = mp.getProperty((String) obj);
    //
    //        if (!oldValue.equals(newValue)) {
    //          return false;
    //        }
    //      } else {
    //        //nothing
    //      }
    //    }
    //    String s = null;
    //    int i = 0;
    //    int j = 0;
    //    byte k = 0;
    //    byte m = 0;
    //    StringTokenizer strTok = null;
    //    s = mp.getProperty("MaxMemory");
    //
    //    if (s != null) {
        //        try {
    //        i = Integer.parseInt(s.trim());
    //
    //        if (i * ONE_MEGABYTE != maxMemory) {
    //          return false;
    //        }
    //      } catch (NumberFormatException e) {
    //        if (category.beWarning()) {
    //          category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.MAXMEMORY_INVALID_FORMAT));
    //          category.logThrowableT(Severity.WARNING, location, "changeProperties(Properties)", e);
    //        }
    //        return false;
    //      }
    //    }
    //
    //    s = mp.getProperty("MemoryLevels");
    //
    //    if (s != null) {
    //      i = s.indexOf("{");
    //      j = s.lastIndexOf("}");
    //
    //      if ((i >= 0) && (i < j)) {
    //        strTok = new StringTokenizer(s.substring(i + 1, j), ",", false);
    //
    //        if (strTok.countTokens() != 5) {
    //          return false;
    //        }
    //
    //        for (k = 0; k < 5; k++) {
    //          try {
    //            m = Byte.parseByte(strTok.nextToken().trim());
    //
    //            if (m != memoryLevels[k]) {
    //              return false;
    //            }
    //          } catch (NumberFormatException e) {
    //            if (category.beWarning()) {
    //              category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_LEVELS_HAS_INCORRECT_FORMAT));
    //              category.logThrowableT(Severity.WARNING, location, "changeProperties(Properties)", e);
    //            }
    //            return false;
    //          }
    //        }
    //      } else {
    //        return false;
    //      }
    //    }
    //
    //    s = mp.getProperty("SleepTimes");
    //
    //    if (s != null) {
    //      if (setSleepTimes(s)) {
    //        //        try {
    //        //        timeoutManager.changeRepeatTime(this, sleepTimes[level]);
    //        //        } catch (TimeoutException e) {
    //        //          logManager.log(logID, (byte)3, "ID000229:Can't refresh the MemoryManager.");
    //        //          throw new RuntimeException(e.getMessage() + "\nID000229:Can't refresh the MemoryManager.");
    //        //        }//try-catch
    //      } else {
    //        category.warningT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.SLEEP_TIMES_HAS_INCORRECT_FORMAT));
    //        return false;
    //      }
    //    }

    return true;
  }

  /**
   * This method do nothing, because <code>MemoryManager</code>
   * have not "callback" relationship with other managers.
   */
  public void loadAdditional() {

  }

  /**
   * The Framework invokes it when some change in properties is needed.
   *
   * @return currently used properties by the manager
   */
  public Properties getCurrentProperties() {
    Properties p = new Properties();
    //    String s;
    //    //p.setProperty("MaxMemory", "" + maxMemory / ONE_MEGABYTE);
    //    s = "{";
    //
    //    for (int i = 0; i < 5; i++) {
    //      s = s + memoryLevels[i];
    //
    //      if (i < 4) {
    //        s = s + ",";
    //      }
    //    }
    //
    //    s = s + "}";
    //    p.setProperty("MemoryLevels", s);
    //    s = "{";
    //
    //    for (int i = 0; i < 6; i++) {
    //      s = s + sleepTimes[i];
    //
    //      if (i < 5) {
    //        s = s + ",";
    //      }
    //    }
    //
    //    s = s + "}";
    //    p.setProperty("SleepTimes", s);
    return p;
  }

  /**
   * The Framework invokes it to check the status of the manager. Returned
   * value is used from AI System.
   *
   * @return status of the manager, which is number between 0 and 100.
   *         This is degree value of the burdering of manager.
   */
  public byte getStatus() {
    return (byte) iCurrentUsage;
  }

  //  public State[] getInternalState() {
  //    return null;
  //  }
  //  /**
  //   * The Framework invokes it to get a number of diagrams, which are
  //   * visualized in Visual Administrator. There can be at most 3 diagrams
  //   * for one manager. In this diagrams are present the statistics of
  //   * the manager. In one diagram can be at most 5 statistics.
  //   *
  //   * @return number of diagrams for the manager.
  //   */
  //  public byte  getDiagramCount() {
  //    return 1;
  //  }
  //
  //  /**
  //   * The Framework invokes it to get names of statistics for defined
  //   * diagram. Maximum of statistics in one diagram  is 5.
  //   *
  //   * @return Names of statistics of defined diagram.
  //   */
  //  public String[]  getStatisticName(byte diagram) {
  //    return statDiagram;
  //  }
  //
  //  /**
  //   * The Framework invokes it to get maximum values of properties, which
  //   * take part in statistics in defined diagram.
  //   *
  //   * @return Maximum values of properties, which take part in
  //   *         corresponding statistics.
  //   */
  //  public int[]  getMaxStatisticValues(byte diagram) {
  //    return maxStatisticValues;
  //  }
  //
  //  /**
  //   * The Framework invokes it to get current values of properties, which
  //   * take part in statistics in defined diagram.
  //   *
  //   * @return Current values of properties, which take part in
  //   *         corresponding statistics.
  //   */
  //  public int[]  getStatisticValues(byte diagram) {
  //    statisticValues[0] = count;
  //    statisticValues[1] = iCurrentUsage;
  //    statisticValues[2] = level;
  //    return statisticValues;
  //  }
  //  public SnapShotProvider getSnapShotProvider() {
  //    if (snapShotProvider == null) {
  //      snapShotProvider = new MemoryManagerSnapShotProvider(this);
  //    }
  //
  //    return snapShotProvider;
  //  }
  /**
   * Get debug information about the manager's current state.
   *
   * @param flag can be used to determines which parts of the info to be returned.
   *
   * @return a String object containing the debug info. A return value of <code>null</code>
   *         means that this manager does not provide debug information.
   */
  public String getDebugInfo(int flag) {
    return null;
  }

}

