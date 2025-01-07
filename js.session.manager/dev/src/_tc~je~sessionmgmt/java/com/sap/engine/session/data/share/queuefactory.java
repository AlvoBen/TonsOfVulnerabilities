package com.sap.engine.session.data.share;

import com.sap.engine.core.Names;
import com.sap.engine.session.data.share.exceptions.TooManyQueuesException;
import com.sap.engine.session.data.share.exceptions.NoSuchQueueException;
import com.sap.engine.session.data.share.exceptions.NullClassLoaderException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.WeakHashMap;
import java.util.HashMap;


public class QueueFactory {
  /**
   * Number of queues per classloader
   */
  public static final int QUEUES_THRESHOLD = 5;

  /**
   * Default threshold for number of elements in each queue
   */
  public static final int QUEUES_ELEMENTS_DEFAULT_THRESHOLD = 100;

  private static WeakHashMap<ClassLoader, HashMap<Object, Queue>> queueMap = new WeakHashMap<ClassLoader, HashMap<Object, Queue>>();

  private static Location loc = Location.getLocation(QueueFactory.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  /**
   * Creates a queue parameterized by the given class or returns an already created one.
   * If the number of already created queues for the classloader is equal to the threshold,
   * TooManyQueuesException is thrown.
   *
   * @param _class the elements class
   * @return the created queue
   * @throws TooManyQueuesException   if the queues are too many
   * @throws NullClassLoaderException if the classloader is null
   */
  public static Queue createQueue(Class _class) throws NullClassLoaderException, TooManyQueuesException {
    return createQueue(_class.getClassLoader(), _class.getName(), QUEUES_ELEMENTS_DEFAULT_THRESHOLD);
  }

  public static Queue createQueue(Class _class, int threshold) throws NullClassLoaderException, TooManyQueuesException {
    return createQueue(_class.getClassLoader(), _class.getName(), threshold);
  }

  public static Queue createStringQueue(ClassLoader loader) throws NullClassLoaderException, TooManyQueuesException {
    return createQueue(loader, String.class.getName(), QUEUES_ELEMENTS_DEFAULT_THRESHOLD);
  }

  public static Queue createStringQueue(ClassLoader loader, int threshold) throws NullClassLoaderException, TooManyQueuesException {
    return createQueue(loader, String.class.getName(), threshold);
  }


  /**
   * Creates a queue parameterized by the given class or returns an already created one.
   * If the number of already created queues for the classloader is equal to the threshold,
   * TooManyQueuesException is thrown.
   *
   * @param loader    the class loader
   * @param className the class name
   * @param threshold the threshold
   * @return the created queue
   * @throws TooManyQueuesException   if the queues are too many
   * @throws NullClassLoaderException if the classloader is null
   */
  private static synchronized Queue createQueue(ClassLoader loader, String className, int threshold) throws NullClassLoaderException, TooManyQueuesException {
    if (loc.beInfo()) {
      String msg = "Trying to create queue for classloader : " + loader + ", for class : " + className + ", with threshold = " + threshold;
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to create queues for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Queue> map = queueMap.get(loader);
    if (map == null) {
      map = new HashMap<Object, Queue>();
      queueMap.put(loader, map);
    } else {
      int size = map.size();
      if (size == QUEUES_THRESHOLD) {
        String msg = "The queue threshold per classloader is reached!";
        loc.logT(Severity.ERROR, msg);
        throw new TooManyQueuesException(msg);
      }
    }
    if (loc.beInfo()) {
      String msg = "For the loader <" + loader + "> there are registered yet: " + map.size() + " queues : " + map;
      loc.logT(Severity.INFO, msg);
    }
    Queue q = map.get(className);
    if (q == null) {
      q = new Queue(className, loader.toString(), threshold);
      map.put(className, q);
    }
    if (loc.beInfo()) {
      String msg = "For the class <" + className + "> the session manager creates new queue: " + q;
      loc.logT(Severity.INFO, msg);
    }
    return q;
  }

  /**
   * Removes a queue holding instances of the given class.
   * It throws a NoSuchQueueException if there is no
   * Queue for the given class and its classloader.
   *
   * @param _class the values class
   * @throws NoSuchQueueException     if the queue does not exist
   * @throws NullClassLoaderException if the classloader is null
   */
  public static synchronized void removeQueue(Class _class) throws NoSuchQueueException, NullClassLoaderException {
    ClassLoader loader = _class.getClassLoader();
    if (loc.beInfo()) {
      String msg = "Trying to remove queue for classloader : " + loader + ", for class : " + _class;
      loc.logT(Severity.INFO, msg);
    }
    if (loader == null) {
      String msg = "It is forbiden to remove queues for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }
    HashMap<Object, Queue> map = queueMap.get(loader);
    if (map == null) {
      String msg = "There is no queue registered for " + _class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }
    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }
    Queue q = map.remove(_class.getName());
    if (q == null) {
      String msg = "There is no queue registered for " + _class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }
    if (loc.beInfo()) {
      String msg = "There is a queue which is removed <" + q + ">";
      loc.logT(Severity.INFO, msg);
    }
    q.removeImpl();
  }

  public static synchronized void removeStringQueue(ClassLoader loader) throws NoSuchQueueException, NullClassLoaderException {
    if (loc.beInfo()) {
      String msg = "Trying to remove queue for classloader : " + loader + ", for class : " + String.class.getName();
      loc.logT(Severity.INFO, msg);
    }
    if (loader == null) {
      String msg = "It is forbiden to remove queues for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }
    HashMap<Object, Queue> map = queueMap.get(loader);
    if (map == null) {
      String msg = "There is no queue registered for " + String.class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }
    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }
    Queue q = map.remove(String.class.getName());
    if (q == null) {
      String msg = "There is no queue registered for " + String.class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }
    if (loc.beInfo()) {
      String msg = "There is a queue which is removed <" + q + ">";
      loc.logT(Severity.INFO, msg);
    }
    q.removeImpl();
  }


  /**
   * Returns a queue holding instances of the given class.
   * It throws an NoSuchQueueException if there is no Queue for the given class and its classloader.
   *
   * @param _class the values class
   * @return the queue
   * @throws NoSuchQueueException     if the queue does not exist
   * @throws NullClassLoaderException if the classloader is null
   */
  public static synchronized Queue getQueue(Class _class) throws NoSuchQueueException, NullClassLoaderException {
    ClassLoader loader = _class.getClassLoader();

    if (loc.beInfo()) {
      String msg = "Trying to get queue for classloader : " + loader + ", for class : " + _class;
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to get queues for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Queue> map = queueMap.get(loader);
    if (map == null) {
      String msg = "There is no queue registered for " + _class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }
    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }
    Queue q = map.get(_class.getName());
    if (q == null) {
      String msg = "There is no queue registered for " + _class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }
    if (loc.beInfo()) {
      String msg = "There is a queue <" + q + ">";
      loc.logT(Severity.INFO, msg);
    }
    return q;
  }

  public static synchronized Queue getStringQueue(ClassLoader loader) throws NoSuchQueueException, NullClassLoaderException {
    if (loc.beInfo()) {
      String msg = "Trying to get queue for classloader : " + loader + ", for class : " + String.class.getName();
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to get queues for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Queue> map = queueMap.get(loader);
    if (map == null) {
      String msg = "There is no queue registered for " + String.class.getName() + " class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }

    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Queue q = map.get(String.class.getName());
    if (q == null) {
      String msg = "There is no queue registered for " + String.class.getName() + " class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }
    if (loc.beInfo()) {
      String msg = "There is a queue <" + q + ">";
      loc.logT(Severity.INFO, msg);
    }

    return q;
  }


  /**
   * Returns maximum allowed number of queues per classloader
   *
   * @return value
   */
  public static int getQueuesThreshold() {
    return QUEUES_THRESHOLD;
  }
}



