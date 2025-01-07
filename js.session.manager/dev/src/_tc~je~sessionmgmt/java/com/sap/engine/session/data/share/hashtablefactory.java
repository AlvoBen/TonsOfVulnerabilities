package com.sap.engine.session.data.share;

import com.sap.engine.core.Names;
import com.sap.engine.session.data.share.exceptions.*;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


import java.util.HashMap;
import java.util.WeakHashMap;


public class HashtableFactory {
  /**
   * Number of hashtables per classloader
   */
  public static final int HASHTABLE_THRESHOLD = 5;

  /**
   * Default threshold for number of elements in each hashtable
   */
  public static final int HASHTABLE_ELEMENTS_DEFAULT_THRESHOLD = 100;

  private static Location loc = Location.getLocation(HashtableFactory.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  private static WeakHashMap<ClassLoader, HashMap<Object, Hashtable>> hashtableMap = new WeakHashMap<ClassLoader, HashMap<Object, Hashtable>>();


  /**
   * Creates a queue parameterized by the given class or returns an already created one.
   * If the number of already created queues for the classloader is equal to the threshold,
   * TooManyQueuesException is thrown.
   *
   * @param _class the values class
   * @throws TooManyHashtablesException if there are too many tables created
   * @throws NullClassLoaderException if the class loader is null
   * @return the created hashtable instance
   */
  public static Hashtable createHashtable(Class _class) throws NullClassLoaderException, TooManyHashtablesException {
    return createHashtable(_class.getClassLoader(), _class.getName(), HASHTABLE_ELEMENTS_DEFAULT_THRESHOLD);
  }

  public static Hashtable createHashtable(Class _class, int threshold) throws NullClassLoaderException, TooManyHashtablesException {
    return createHashtable(_class.getClassLoader(), _class.getName(), threshold);
  }

  public static Hashtable createStringHashtable(ClassLoader loader) throws NullClassLoaderException, TooManyHashtablesException {
    return createHashtable(loader, String.class.getName(), HASHTABLE_ELEMENTS_DEFAULT_THRESHOLD);
  }

  public static Hashtable createStringHashtable(ClassLoader loader, int threshold) throws NullClassLoaderException, TooManyHashtablesException {
    return createHashtable(loader, String.class.getName(), threshold);
  }

  /**
   * Creates a queue parameterized by the given class or returns an already created one.
   * If the number of already created queues for the classloader is equal to the threshold,
   * TooManyQueuesException is thrown.
   *
   * @param loader the class loader
   * @param className the class name
   * @param threshold the threshold 
   * @throws NullClassLoaderException if the class loader is null
   * @throws TooManyHashtablesException if there are too many tables created
   * @return the created hashtable instance
   */
  private static synchronized Hashtable createHashtable(ClassLoader loader, String className, int threshold) throws NullClassLoaderException, TooManyHashtablesException {
    if (loc.beInfo()) {
      String msg = "Trying to create hashtable for classloader : " + loader + ", for class : " + className + ", with threshold = " + threshold;
      loc.logT(Severity.INFO, msg);
    }
    if (loader == null) {
      String msg = "It is forbiden to create hashtable for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }
    HashMap<Object, Hashtable> map = hashtableMap.get(loader);
    if (map == null) {
      map = new HashMap<Object, Hashtable>();
      hashtableMap.put(loader, map);
    } else {
      int size = map.size();
      if (size == HASHTABLE_THRESHOLD) {
        String msg = "The hashtable threshold per classloader is reached!";
        loc.logT(Severity.ERROR, msg);
        throw new TooManyHashtablesException(msg);
      }
    }
    if (loc.beInfo()) {
      String msg = "For the loader <" + loader + "> there are registered yet: " + map.size() + " hashtables : " + map;
      loc.logT(Severity.INFO, msg);
    }
    Hashtable htbl = map.get(className);
    if (htbl == null) {
      htbl = new Hashtable(className, loader.toString(), threshold);
      map.put(className, htbl);
    }
    if (loc.beInfo()) {
      String msg = "For the class <" + className + "> the session manager creates new hashtable: " + htbl;
      loc.logT(Severity.INFO, msg);
    }
    return htbl;
  }

  /**
   * Removes a queue holding instances of the given class.
   * It throws a NoSuchQueueException if there is no
   * Queue for the given class and its classloader.
   *
   * @param _class the value class
   * @throws NullClassLoaderException if the class loader is null
   * @throws NoSuchHashtableException if no such hashtable is found
   */
  public static synchronized void removeHashtable(Class _class) throws NoSuchHashtableException, NullClassLoaderException {
    ClassLoader loader = _class.getClassLoader();
    if (loc.beInfo()) {
      String msg = "Trying to remove hashtable for classloader : " + loader + ", for class : " + _class.getName();
      loc.logT(Severity.INFO, msg);
    }
    if (loader == null) {
      String msg = "It is forbiden to remove hashtable for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }
    HashMap<Object, Hashtable> map = hashtableMap.get(loader);
    if (map == null) {
      String msg = "There is no hashtable registered for " + _class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }
    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }
    Hashtable htbl = map.remove(_class.getName());
    if (htbl == null) {
      String msg = "There is no hashtable registered for " + _class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }
    if (loc.beInfo()) {
      String msg = "There is a hashtable which is removed <" + htbl + ">";
      loc.logT(Severity.INFO, msg);
    }
    htbl.removeImpl();
  }

  public static synchronized void removeStringHashtable(ClassLoader loader) throws NoSuchHashtableException, NullClassLoaderException {
    if (loc.beInfo()) {
      String msg = "Trying to remove hashtable for classloader : " + loader + ", for class : " + String.class.getName();
      loc.logT(Severity.INFO, msg);
    }
    if (loader == null) {
      String msg = "It is forbiden to remove hashtable for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }
    HashMap<Object, Hashtable> map = hashtableMap.get(loader);
    if (map == null) {
      String msg = "There is no hashtable registered for " + String.class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }
    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }
    Hashtable htbl = map.remove(String.class.getName());
    if (htbl == null) {
      String msg = "There is no hashtable registered for " + String.class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }
    if (loc.beInfo()) {
      String msg = "There is a hashtable which is removed <" + htbl + ">";
      loc.logT(Severity.INFO, msg);
    }
    htbl.removeImpl();
  }


  /**
   * Returns a queue holding instances of the given class.
   * It throws an NoSuchQueueException if there is no Queue for the given class and its classloader.
   *
   * @param _class the value class
   * @throws NullClassLoaderException if the class loader is null
   * @throws NoSuchHashtableException if no such hashtable is found
   * @return the hashtable
   */
  public static synchronized Hashtable getHashtable(Class _class) throws NoSuchHashtableException, NullClassLoaderException {
    ClassLoader loader = _class.getClassLoader();
    if (loc.beInfo()) {
      String msg = "Trying to get hashtable for classloader : " + loader + ", for class : " + _class;
      loc.logT(Severity.INFO, msg);
    }
    if (loader == null) {
      String msg = "It is forbiden to get hashtable for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }
    HashMap<Object, Hashtable> map = hashtableMap.get(loader);
    if (map == null) {
      String msg = "There is no hashtable registered for " + _class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }
    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }
    Hashtable htbl = map.get(_class.getName());
    if (htbl == null) {
      String msg = "There is no hashtable registered for " + _class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }
    if (loc.beInfo()) {
      String msg = "There is a hashtable <" + htbl + ">";
      loc.logT(Severity.INFO, msg);
    }
    return htbl;
  }

  public static synchronized Hashtable getStringHashtable(ClassLoader loader) throws NoSuchHashtableException, NullClassLoaderException {
    if (loc.beInfo()) {
      String msg = "Trying to get hashtable for classloader : " + loader + ", for class : " + String.class.getName();
      loc.logT(Severity.INFO, msg);
    }
    if (loader == null) {
      String msg = "It is forbiden to get hashtable for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }
    HashMap<Object, Hashtable> map = hashtableMap.get(loader);
    if (map == null) {
      String msg = "There is no hashtable registered for " + String.class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }
    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }
    Hashtable htbl = map.get(String.class.getName());
    if (htbl == null) {
      String msg = "There is no hashtable registered for " + String.class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchHashtableException(msg);
    }
    if (loc.beInfo()) {
      String msg = "There is a hashtable <" + htbl + ">";
      loc.logT(Severity.INFO, msg);
    }
    return htbl;
  }

  /**
   * Returns maximum allowed number of hashtables per classloader
   *
   * @return value
   */
  public static int getHashtablesThreshold() {
    return HASHTABLE_THRESHOLD;
  }
}