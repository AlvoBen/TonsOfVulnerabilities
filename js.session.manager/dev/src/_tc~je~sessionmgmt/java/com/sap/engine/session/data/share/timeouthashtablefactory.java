package com.sap.engine.session.data.share;

import com.sap.engine.session.data.share.exceptions.*;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import com.sap.engine.core.Names;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.Timer;
import java.util.TimerTask;


public class TimeoutHashtableFactory {
  /**
   * Number of hashtables per classloader
   */
  public static final int TIMEOUT_HASHTABLE_THRESHOLD = 5;

  /**
   * Default threshold for number of elements in each hashtable
   */
  public static final int TIMEOUT_HASHTABLE_ELEMENTS_DEFAULT_THRESHOLD = 100;

  private static WeakHashMap<ClassLoader, HashMap<Object, Hashtable>> hashtableMap = new WeakHashMap<ClassLoader, HashMap<Object, Hashtable>>();

  private static Timer timer = null;

  private static Location loc = Location.getLocation(TimeoutHashtableFactory.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);  

  static {
    timer = new Timer("Session TimeoutHashtable timer", true);
  }


  /**
   * Creates a queue parameterized by the given class or returns an already created one.
   * If the number of already created queues for the classloader is equal to the threshold,
   * TooManyQueuesException is thrown.
   *
   * @param _class the class name
   * @param timeout   the timeout
   * @return hashtable the hashtable
   * @throws NullClassLoaderException if the class loader is null
   * @throws TooManyHashtablesException if there are too many tables created
   */
  public static Hashtable createTimeoutHashtable(Class _class, long timeout) throws NullClassLoaderException, TooManyHashtablesException {
    return createTimeoutHashtable(_class.getClassLoader(), _class.getName(), timeout, TIMEOUT_HASHTABLE_ELEMENTS_DEFAULT_THRESHOLD);
  }

  public static Hashtable createTimeoutHashtable(Class _class, long timeout, int threshold) throws NullClassLoaderException, TooManyHashtablesException {
    return createTimeoutHashtable(_class.getClassLoader(), _class.getName(), timeout, threshold);
  }

  public static Hashtable createTimeoutStringHashtable(ClassLoader loader, long timeout) throws NullClassLoaderException, TooManyHashtablesException {
    return createTimeoutHashtable(loader, String.class.getName(), timeout, TIMEOUT_HASHTABLE_ELEMENTS_DEFAULT_THRESHOLD);
  }

  public static Hashtable createTimeoutStringHashtable(ClassLoader loader, long timeout, int threshold) throws NullClassLoaderException, TooManyHashtablesException {
    return createTimeoutHashtable(loader, String.class.getName(), timeout, threshold);
  }

  /**
   * Creates a queue parameterized by the given class or returns an already created one.
   * If the number of already created queues for the classloader is equal to the threshold,
   * TooManyQueuesException is thrown.
   *
   * @param loader    the class loader
   * @param className the class name
   * @param timeout   the timeout
   * @param threshold the threshold
   * @return the hashtable
   * @throws NullClassLoaderException   if the class loader is null
   * @throws TooManyHashtablesException if there are too many tables created
   */
  private static synchronized Hashtable createTimeoutHashtable(ClassLoader loader, String className, long timeout, int threshold) throws NullClassLoaderException, TooManyHashtablesException {
    if (loc.beInfo()) {
      String msg = "Trying to create timeout hashtable for classloader : " + loader + ", for class : " + className + ", with threshold = " + threshold;
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to create timeout hashtable for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Hashtable> map = hashtableMap.get(loader);
    if (map == null) {
      map = new HashMap<Object, Hashtable>();
      hashtableMap.put(loader, map);
    } else {
      int size = map.size();
      if (size == TIMEOUT_HASHTABLE_THRESHOLD) {
        String msg = "The timeout hashtable threshold per classloader is reached!";
        loc.logT(Severity.ERROR, msg);
        throw new TooManyHashtablesException(msg);
      }
    }

    if (loc.beInfo()) {
      String msg = "For the loader <" + loader + "> there are registered yet: " + map.size() + " TimeoutHashtables : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Hashtable htbl = map.get(className);
    if (htbl == null) {
      htbl = new Hashtable(className, loader.toString(), threshold, timeout);
      map.put(className, htbl);
    }

    if (loc.beInfo()) {
      String msg = "For the class <" + className + "> the session manager creates new Timeout Hashtable: " + htbl;
      loc.logT(Severity.INFO, msg);
    }

    return htbl;
  }

  /**
   * Removes a queue holding instances of the given class.
   * It throws a NoSuchQueueException if there is no
   * Queue for the given class and its classloader.
   *
   * @param _class the values class
   * @throws NullClassLoaderException   if the class loader is null
   * @throws NoSuchHashtableException if there in no such hashtabkle
   */
  public static synchronized void removeTimeoutHashtable(Class _class) throws NoSuchHashtableException, NullClassLoaderException {
    ClassLoader loader = _class.getClassLoader();

    if (loc.beInfo()) {
      String msg = "Trying to remove timeout hashtable for classloader : " + loader + ", for class : " + _class;
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to remove timeout hashtable for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Hashtable> map = hashtableMap.get(loader);
    if (map == null) {
      String msg = "There is no timeout hashtable registered for " + _class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }

    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Hashtable htbl = map.remove(_class.getName());
    if (htbl == null) {
      String msg = "There is no timeout hashtable registered for " + _class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }

    if (loc.beInfo()) {
      String msg = "There is a Timeout Hashtable which is removed <" + htbl + ">";
      loc.logT(Severity.INFO, msg);
    }

    htbl.removeImpl();
  }

  public static synchronized void removeTimeoutStringHashtable(ClassLoader loader) throws NoSuchHashtableException, NullClassLoaderException {
    if (loc.beInfo()) {
      String msg = "Trying to remove timeout hashtable for classloader : " + loader + ", for class : " + String.class.getName();
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to remove timeout hashtable for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Hashtable> map = hashtableMap.get(loader);
    if (map == null) {
      String msg = "There is no timeout hashtable registered for " + String.class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }

    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Hashtable htbl = map.remove(String.class.getName());
    if (htbl == null) {
      String msg = "There is no timeout hashtable registered for " + String.class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }

    if (loc.beInfo()) {
      String msg = "There is a Timeout Hashtable which is removed <" + htbl + ">";
      loc.logT(Severity.INFO, msg);
    }

    htbl.removeImpl();
  }


  /**
   * Returns a queue holding instances of the given class.
   * It throws an NoSuchQueueException if there is no Queue for the given class and its classloader.
   *
   * @param _class the values class
   * @return the hashtable
   * @throws NullClassLoaderException   if the class loader is null
   * @throws NoSuchHashtableException if there in no such hashtabkle
   */
  public static synchronized Hashtable getTimeoutHashtable(Class _class) throws NoSuchHashtableException, NullClassLoaderException {
    ClassLoader loader = _class.getClassLoader();

    if (loc.beInfo()) {
      String msg = "Trying to get timeout hashtable for classloader : " + loader + ", for class : " + _class;
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to get timeout hashtable for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Hashtable> map = hashtableMap.get(loader);
    if (map == null) {
      String msg = "There is no timeout hashtable registered for " + _class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }

    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Hashtable htbl = map.get(_class.getName());
    if (htbl == null) {
      String msg = "There is no timeout hashtable registered for " + _class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }

    if (loc.beInfo()) {
      String msg = "There is a Timeout Hashtable <" + htbl + ">";
      loc.logT(Severity.INFO, msg);
    }

    return htbl;
  }

  public static synchronized Hashtable getTimeoutStringHashtable(ClassLoader loader) throws NoSuchHashtableException, NullClassLoaderException {
    if (loc.beInfo()) {
      String msg = "Trying to get timeout hashtable for classloader : " + loader + ", for class : " + String.class.getName();
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to get timeout hashtable for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Hashtable> map = hashtableMap.get(loader);
    if (map == null) {
      String msg = "There is no timeout hashtable registered for " + String.class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }

    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Hashtable htbl = map.get(String.class.getName());
    if (htbl == null) {
      String msg = "There is no timeout hashtable registered for " + String.class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }

    if (loc.beInfo()) {
      String msg = "There is a Timeout Hashtable <" + htbl + ">";
      loc.logT(Severity.INFO, msg);
    }

    return htbl;
  }

  /**
   * Returns maximum allowed number of hashtables per classloader
   *
   * @return value
   */
  public static int getTimeoutHashtablesThreshold() {
    return TIMEOUT_HASHTABLE_THRESHOLD;
  }


  protected static void setForTimeout(TimeoutHashtableElement element, long delay) {
    timer.schedule(element, delay);
  }
}

