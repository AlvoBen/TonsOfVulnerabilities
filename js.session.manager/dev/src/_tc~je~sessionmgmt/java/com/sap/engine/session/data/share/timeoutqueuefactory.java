package com.sap.engine.session.data.share;

import com.sap.engine.core.Names;
import com.sap.engine.session.data.share.exceptions.*;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.WeakHashMap;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class TimeoutQueueFactory {
  private static Timer timer = null;
  private static Location loc = Location.getLocation(TimeoutQueueFactory.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  static {
    timer = new Timer("Session TimeoutQueue timer", true);
  }

  /** Number of queues per classloader
   *
   */
  public static final int TIMEOUT_QUEUES_THRESHOLD = 5;

  /** Default threshold for number of elements in each queue
   *
   */
  public static final int TIMEOUT_QUEUES_ELEMENTS_DEFAULT_THRESHOLD = 100;

  private static WeakHashMap<ClassLoader, HashMap<Object, Queue>> queueTimeoutMap = new WeakHashMap<ClassLoader, HashMap<Object, Queue>>();


  public static Queue createTimeoutQueue(Class _class, long timeout) throws NullClassLoaderException, TooManyQueuesException {
    return createTimeoutQueue(_class.getClassLoader(), _class.getName(), timeout, TIMEOUT_QUEUES_ELEMENTS_DEFAULT_THRESHOLD);
  }

  public static Queue createTimeoutQueue(Class _class, long timeout, int threshold) throws NullClassLoaderException, TooManyQueuesException {
    return createTimeoutQueue(_class.getClassLoader(), _class.getName(), timeout, threshold);
  }

  public static Queue createTimeoutStringQueue(ClassLoader loader, long timeout) throws NullClassLoaderException, TooManyQueuesException {
    return createTimeoutQueue(loader, String.class.getName(), timeout, TIMEOUT_QUEUES_ELEMENTS_DEFAULT_THRESHOLD);
  }

  public static Queue createTimeoutStringQueue(ClassLoader loader, long timeout, int threshold) throws NullClassLoaderException, TooManyQueuesException {
    return createTimeoutQueue(loader, String.class.getName(), timeout, threshold);
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
   * @return the queue
   * @throws NullClassLoaderException   if the class loader is null
   * @throws TooManyQueuesException if there are too many queues created
   */
  private static synchronized Queue createTimeoutQueue(ClassLoader loader, String className, long timeout, int threshold) throws NullClassLoaderException, TooManyQueuesException {
    if (loc.beInfo()) {
      String msg = "Trying to create timeout queue for classloader : " + loader + ", for class : " + className + ", with threshold = " + threshold;
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to create timeout queues for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Queue> map = queueTimeoutMap.get(loader);
    if (map == null) {
      map = new HashMap<Object, Queue>();
      queueTimeoutMap.put(loader, map);
    } else {
      int size = map.size();
      if (size == TIMEOUT_QUEUES_THRESHOLD) {
        String msg = "The timeout queue threshold per classloader is reached!";
        loc.logT(Severity.ERROR, msg);
        throw new TooManyQueuesException(msg);
      }
    }

    if(loc.beInfo()){
      String msg = "For the loader <" + loader + "> there are registered yet: " + map.size() + " Timeout Queues : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Queue q = map.get(className);
    if (q == null) {
      q = new Queue(className, loader.toString(), threshold, timeout);
      map.put(className, q);
    }

      if(loc.beInfo()){
        String msg = "For the class <" + className + "> the session manager creates new Timeout Queue: " + q ;
        loc.logT(Severity.INFO, msg);
      }

    return q;
  }

  /**
   * Removes a queue holding instances of the given class.
   * It throws a NoSuchQueueException if there is no
   * Queue for the given class and its classloader.
   *
   * @param _class the class name
   * @throws NullClassLoaderException   if the class loader is null
   * @throws NoSuchQueueException if there is no such queue
   */
  public static synchronized void removeTimeoutQueue(Class _class) throws NoSuchQueueException, NullClassLoaderException {
    ClassLoader loader = _class.getClassLoader();

    if (loc.beInfo()) {
      String msg = "Trying to remove timeout queue for classloader : " + loader + ", for class : " + _class.getName();
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to remove queues for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Queue> map = queueTimeoutMap.get(loader);
    if (map == null) {
      String msg = "There is no timeout queue registered for "+_class.getName()+" class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }

    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Queue q = map.remove(_class.getName());
    if (q == null) {
      String msg = "There is no timeout queue registered for "+_class.getName()+" class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }

    if (loc.beInfo()) {
      String msg = "There is a Timeout Queue which is removed <" + q + ">";
      loc.logT(Severity.INFO, msg);
    }
    
    q.removeImpl();
  }

  public static synchronized void removeTimeoutStringQueue(ClassLoader loader) throws NoSuchQueueException, NullClassLoaderException {
    if (loc.beInfo()) {
      String msg = "Trying to remove timeout queue for classloader : " + loader + ", for class : " + String.class.getName();
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to remove timeout queues for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Queue> map = queueTimeoutMap.get(loader);
    if (map == null) {
      String msg = "There is no timeout queue registered for "+String.class.getName()+" class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }

    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Queue q = map.remove(String.class.getName());
    if (q == null) {
      String msg = "There is no timeout queue registered for "+String.class.getName()+" class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }

    if (loc.beInfo()) {
      String msg = "There is a Timeout Queue which is removed <" + q + ">";
      loc.logT(Severity.INFO, msg);
    }
    
    q.removeImpl();
 }



  /**
   * Returns a queue holding instances of the given class.
   * It throws an NoSuchQueueException if there is no Queue for the given class and its classloader.
   *
   * @param _class the class name 
   * @return the queue
   * @throws NullClassLoaderException   if the class loader is null
   * @throws NoSuchQueueException if there is no such queue
   */
  public static synchronized Queue getTimeoutQueue(Class _class) throws NoSuchQueueException, NullClassLoaderException {
    ClassLoader loader = _class.getClassLoader();

    if (loc.beInfo()) {
      String msg = "Trying to get timeout queue for classloader : " + loader + ", for class : " + _class.getName();
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to get timeout queues for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Queue> map = queueTimeoutMap.get(loader);
    if (map == null) {
      String msg = "There is no timeout queue registered for "+_class.getName()+" class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }

    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Queue q = map.get(_class.getName());
    if (q == null) {
      String msg = "There is no timeout queue registered for "+_class.getName()+" class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }

    if (loc.beInfo()) {
      String msg = "There is a Timeout Queue <" + q + ">";
      loc.logT(Severity.INFO, msg);
    }

    return q;
  }

  public static synchronized Queue getTimeoutStringQueue(ClassLoader loader) throws NoSuchQueueException, NullClassLoaderException {
    if (loc.beInfo()) {
      String msg = "Trying to get timeout queue for classloader : " + loader + ", for class : " + String.class.getName();
      loc.logT(Severity.INFO, msg);
    }

    if (loader == null) {
      String msg = "It is forbiden to get timeout queues for classes loaded by bootstrap classloader!";
      loc.logT(Severity.ERROR, msg);
      throw new NullClassLoaderException(msg);
    }

    HashMap<Object, Queue> map = queueTimeoutMap.get(loader);
    if (map == null) {
      String msg = "There is no timeout queue registered for "+String.class.getName()+" class. Maybe there is a classloader problem.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }

    if (loc.beInfo()) {
      String msg = "For the loader<" + loader + "> there are/is : " + map;
      loc.logT(Severity.INFO, msg);
    }

    Queue q = map.get(String.class.getName());
    if (q == null) {
      String msg = "There is no timeout queue registered for "+String.class.getName()+" class.";
      loc.logT(Severity.ERROR, msg);
      throw new NoSuchQueueException(msg);
    }

    if (loc.beInfo()) {
      String msg = "There is a Timeout Queue <" + q + ">";
      loc.logT(Severity.INFO, msg);
    }

    return q;
  }


  /** Returns maximum allowed number of queues per classloader
   *
   * @return value
   *
   */
  public static int getQueuesThreshold() {
    return TIMEOUT_QUEUES_THRESHOLD;
  }

  protected static void setForTimeout(TimeoutQueueElement element, long delay) {
    timer.schedule(element, delay);
  }
}




