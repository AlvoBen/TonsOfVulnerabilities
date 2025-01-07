package com.sap.engine.session.data.share;

import com.sap.engine.core.Names;
import com.sap.engine.session.data.share.exceptions.*;
import com.sap.engine.session.usr.UserContext;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class Queue {
  protected String className = null;
  protected String loaderName = null;
  protected int threshold = -1;
  protected Object ref = null;
  protected long timeout = -1;

  private static Location loc = Location.getLocation(Queue.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  public Queue(String className, String loaderName, int threshold) {
    this(className, loaderName, threshold, -1);
  }

  public Queue(String className, String loaderName, int threshold, long timeout) {
    this.timeout = timeout;
    this.loaderName = loaderName;
    this.className = className;
    this.threshold = threshold;
    this.ref = new Object();
  }

  protected QueueImpl getImpl() throws NullUserContextException {
    QueueImpl impl;
    UserContext userContext = UserContext.getCurrentUserContext();
    if(loc.beInfo()){
      String msg = "Current User Context : " + userContext;
      loc.logT(Severity.INFO, msg);
    }
    if (userContext != null) {
      impl = userContext.getQueue(this.ref);
      if(loc.beInfo()){
        String msg;
        if(impl == null){
          msg = "For the current UserContext there is no queue for className<" + className + ">";
        } else {
          msg = "For the current UserContext there is a queue <" + impl + "> for className<" + className + ">";
        }
        loc.logT(Severity.INFO, msg);
      }
      if (impl == null) {
        impl = new QueueImpl(className, loaderName, threshold, timeout);
        if(loc.beInfo()){
          String msg = "Currently there is no queue and now is registered<" + impl + "> for className<" + className + ">";
          loc.logT(Severity.INFO, msg);
        }
        userContext.putQueue(this.ref, impl);
      }
    } else {
      throw new NullUserContextException("The current user context is null!");
    }
    return impl;
  }

  protected void removeImpl() {
    ref = null;
  }

  /** Add a new element in the queue.
   *  If the number of elements becomes greater than the threshold the TooManyElementsException is thrown.
   *  If the element class doesn't matches to the Queue class type ClassNotAcceptable is thrown
   *  (it could happen also if the element's class is loaded by the different classloader than
   *  the classloader of the Class given as parameter to createQueue(Class) QueueFactory method)
   *
   * @param element an element
   * @throws TooManyElementsException if the elements are too many
   * @throws ClassNotAcceptableException if the class is not acceptable
   * @throws NullUserContextException if the user context is null
   */
  public synchronized void add(Object element) throws TooManyElementsException, ClassNotAcceptableException, NullUserContextException {
    getImpl().add(element);
  }

  /**
   * Returns the last added element in the queue and removes it or returns null if the queue is empty
   *
   * @return value
   * @throws NullUserContextException if the user context is null
   */
   public synchronized Object get() throws NullUserContextException {
     return getImpl().get();
   }

  /**
   * The same as get() but keeps the element in the queue
   *
   * @return value
   * @throws NullUserContextException if the user context is null
   */
   public synchronized Object read() throws NullUserContextException {
     return getImpl().read();
   }


  /** Returns true if the queue is empty and false otherwise
   *
   * @return value
   * @throws NullUserContextException if the user context is null
   */
  public synchronized boolean isEmpty() throws NullUserContextException {
    return getImpl().isEmpty();
  }


  /** Returns the number of the queue elements
   *
   * @return value
   * @throws NullUserContextException if the user context is null
   */
  public synchronized int size() throws NullUserContextException {
    return getImpl().size();
  }
}
