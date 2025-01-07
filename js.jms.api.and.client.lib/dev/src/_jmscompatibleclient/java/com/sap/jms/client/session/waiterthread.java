/*
 * Copyright (c) 2000 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.jms.client.session;

import com.sap.engine.lib.util.base.NextItem;
import com.sap.jms.JMSConstants;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;

/**
 * Thread object to use with ThreadPool.
 * On creating of new instance method waitToStart() can be used 
 * to ensure that thread run method is in synchronized block.
 * After that you can use setRunnableObject() to load the WaiterThread
 * with Runnable object wich run() method will be executed.
 * @autor Nikolai Angelov / nikolai.angelov@sap.com /
 */ 
public class WaiterThread extends Thread implements NextItem, JMSConstants {
    
    static final long serialVersionUID = -66958173242953759L;
  
    private static final String LOG_COMPONENT = "session.WaiterThread";
    private static final LogService log = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);

    transient Runnable runnableObject = null;
  ThreadPool threadPool = null;
  private volatile boolean isRunning = false; 
  
  /**
   * Successor of the item.
   */
  NextItem nextItem = null;
  
  public WaiterThread( ThreadPool threadPool ) {
    this.threadPool = threadPool;
    setDaemon(true);
  }
  
  /**
   * Loads this object with Runnable target specified.
   * Then notifies the Thread to execute the run method of the target.
   */ 
  public synchronized void setRunnableObject( Runnable runnableObject ) {
    this.runnableObject = runnableObject;
    notify();
  }
  
  public void run() {
	try {
      synchronized( this ) {
        isRunning = true;
        notify();
        while (true) {
			wait();
	      if(runnableObject != null){
	          runnableObject.run();
	          runnableObject = null;
	          threadPool.releaseObject(this);
		  }
        }
      }
	} catch (InterruptedException e) {
        log.exception(LogService.WARNING, LOG_COMPONENT, e);
	  return;
	}
  }

  /**
   * Waits until this Thread is started and the 
   * run() method gets into the synchronized block 
   */ 
  public void waitToStart() throws InterruptedException {
    synchronized( this ) {
      while ( !isRunning ) {
        wait();
      }
    }
  }
  
  /**
   * Retrieves the successor of this item.<p>
   * @return  the successor of this item.
   */
  public NextItem getNext() {
    return nextItem;
  }

  /**
   * Sets the successor of this item.<p>
   * @param   <tt>ni</tt> the successor of this item.
   */
  public void setNext(NextItem nextItem) {
    this.nextItem = nextItem;
  }

  /**
   * Prepare item to be pooled.<p>
   */
  public void clearItem() {
    nextItem = null;
  }

  /**
   * Clones the item.<p>
   * @return the cloning of the object.
   */
  public Object clone(){
    try {
      return super.clone();
    } catch (CloneNotSupportedException exc) {
      // never coming here because ItemAdapter implements Cloneable via Item
      log.exception(LogService.WARNING, LOG_COMPONENT, exc);
      return null;
    }
  }

}
