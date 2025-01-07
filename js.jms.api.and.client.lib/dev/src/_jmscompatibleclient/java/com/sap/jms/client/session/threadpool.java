package com.sap.jms.client.session;

import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.Task;
import com.sap.engine.frame.core.thread.execution.Executor;
import com.sap.engine.lib.util.base.ListPool;
import com.sap.engine.lib.util.base.NextItem;
import com.sap.jms.util.logging.LogServiceImpl;
import com.sap.jms.util.logging.LogService;

public class ThreadPool extends ListPool implements ThreadSystem {
    private static final String LOG_COMPONENT = "session.ThreadPool";
    private static final LogService log = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);

  private int maxThreads = 0;
  private int instancesCount = 0;
  private int freeInstances = 0;
  
  public ThreadPool( int initialSize, int maxThreads ) {
    super(initialSize, maxThreads);
    this.maxThreads = maxThreads;
  }

  /**
   * Get thread context of the current thread.
   *
   */
  public ThreadContext getThreadContext() {
    return null;
  }

  /**
   * It starts the <source> Runnable <source> object into a new thread from the
   * internal thread pool. If there is no free thread the object is add to a
   * waiting queue. You can't rely on priority, internal system can ignore it in
   * some situations. The thread context of the parent thread is copied to the
   * new thread using child method of Context objects. This is done by parent
   * thread.
   *
   * @param   thread - runnable object that is going to be executed
   * @param   system - indicate the type of thread that has to be used for 
   * execution of the runnable object
   * @param   priority - this is the priority with which the thread has to be
   * executed. The interval of values for this parameter is the as for 
   * java.lang.Thread priority  
   *
   */
  public void startThread(Runnable thread, boolean system, boolean instantly) {
    startThread(thread, system);
  }

  public synchronized WaiterThread getWaiterThread() 
                                      throws InterruptedException {
    while ( instancesCount == maxThreads && freeInstances == 0 ) {
      wait();
    }

    freeInstances--;
    return (WaiterThread)getObject();
  }

  public synchronized void releaseObject(NextItem item) {
    super.releaseObject(item);
    freeInstances++;
    notify();
  }

  public void startThread(Runnable thread, boolean system) {
    try {
      getWaiterThread().setRunnableObject( thread );
    } catch (InterruptedException e) {
      log.exception(LOG_COMPONENT, e);
    }
  }

  public NextItem newInstance() {
    WaiterThread waiterThread = new WaiterThread(this);
    waiterThread.start();
    try {
      waiterThread.waitToStart();
    } catch (InterruptedException e) {
      log.exception(LOG_COMPONENT, e);
    }
    instancesCount++;
    freeInstances++;
    return waiterThread;
  }
  
  public synchronized int getLimit() {
    return maxThreads;
  }

  public synchronized void setLimit(int maxThreads) {
    super.setLimit(maxThreads);
    if ( maxThreads > this.maxThreads ) {
      notifyAll();
    }
    this.maxThreads = maxThreads;
  }

  /**
   * Releases all objects from pool.<p>
   */
  public synchronized void freeMemory() {
    freeMemory(0);
  }

  /**
   * Releases some objects from pool.<p>
   *
   * @param  <tt>count</tt> maximum objects that will remain in pool.<p>
   */
  public synchronized void freeMemory(int count) {
    Thread thread;
    for ( int i = freeInstances - count - 1; i >= 0; i-- ) {
      thread = (Thread)removeFirstItem();
      
      if (thread != null) {
        thread.interrupt();
        freeInstances--;
        instancesCount--;
      }
    }
  }

  public int registerContextObject(String name, ContextObject object) {
      throw new java.lang.UnsupportedOperationException();
  }

  public int getContextObjectId(String name) {
      throw new java.lang.UnsupportedOperationException();
  }

  public void unregisterContextObject(String name) {
      throw new java.lang.UnsupportedOperationException();
  }

  public void startTask(Task task, boolean instantly) {
      throw new java.lang.UnsupportedOperationException();
  }
 
  public void startTask(Task task, long timeout) {
      throw new java.lang.UnsupportedOperationException();
  }

  /**
   * This method should be called only in client virtual machine, so we don't care about the 
   * thread's name 
   */
  public void startThread(Runnable thread, String taskName, String threadName, boolean system) {
  	startThread(thread, system);
  }
  
  /**
   * This method should be called only in client virtual machine, so we don't care about the 
   * thread's name 
   */
  public void startThread(Runnable thread, String taskName, String threadName, boolean system, boolean instantly) {
  	startThread(thread, system, instantly);
  }
    
  public void startThread(Task task, String taskName, String threadName, boolean instantly) {
      throw new java.lang.UnsupportedOperationException();
  }
  
  public void startThread(Task task, String taskName, String threadName, long timeout) {
      throw new java.lang.UnsupportedOperationException();
  }
    
  public void startCleanThread(Runnable thread, boolean instantly) {
      throw new java.lang.UnsupportedOperationException();
  }
    
  public void startCleanThread(Task task, boolean instantly) {
      throw new java.lang.UnsupportedOperationException();
  }
  
  public void startCleanThread(Runnable thread, boolean system, boolean instantly) {
      throw new java.lang.UnsupportedOperationException();
  }
  
  public void startCleanThread(Task task, boolean system, boolean instantly) {
      throw new java.lang.UnsupportedOperationException();
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#createExecutor(java.lang.String, int, int)
   */
  public Executor createExecutor(String arg0, int arg1, int arg2) {
    throw new java.lang.UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#createExecutor(java.lang.String, int, int, byte)
   */
  public Executor createExecutor(String arg0, int arg1, int arg2, byte arg3) {
    throw new java.lang.UnsupportedOperationException();
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#createCleanThreadExecutor(java.lang.String, int, int, byte)
   */
  public Executor createCleanThreadExecutor(String arg0, int arg1, int arg2, byte arg3) {
    throw new java.lang.UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#destroyExecutor(com.sap.engine.frame.core.thread.execution.Executor)
   */
  public void destroyExecutor(Executor arg0) {
    throw new java.lang.UnsupportedOperationException();
  }
 
  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#executeInDedicatedThread(java.lang.Runnable, java.lang.String)
   */
  public void executeInDedicatedThread(Runnable arg0, String arg1) {
    throw new java.lang.UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#executeInDedicatedThread(java.lang.Runnable, java.lang.String, java.lang.String)
   */
  public void executeInDedicatedThread(Runnable arg0, String arg1, String arg2) {
    throw new java.lang.UnsupportedOperationException();
  }
  
}