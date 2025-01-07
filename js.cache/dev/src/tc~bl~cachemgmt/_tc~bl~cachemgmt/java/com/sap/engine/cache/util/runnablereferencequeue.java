/*
 * Created on 2004.7.7
 *
 */
package com.sap.engine.cache.util;

import java.lang.ref.ReferenceQueue;

import com.sap.engine.cache.util.dump.LogUtil;

/**
 * @author petio-p
 *
 */
public class RunnableReferenceQueue extends ReferenceQueue implements Runnable {

  private boolean working = false;
  
  private Thread myThread = null;
  
  private GCListener listener = null;
  
  public RunnableReferenceQueue() {
    this(null);
  }
  
  public RunnableReferenceQueue(GCListener listener) {
    super();
    this.listener = listener;
    working = true;
    myThread = new Thread(this);
    myThread.setDaemon(true);
    myThread.start();
  }
  
  public void stop() {
    working = false;
    myThread.interrupt();
  }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
    while (working) {
      try {
        SimpleEntry entry = (SimpleEntry) remove();
        Object key = entry.getKey();
        SimpleMap map = entry.getSimpleMap();
        if (listener != null) {
          listener.garbageCollected(key, map);
        }
        map.remove(key);
      } catch (InterruptedException e) {
        LogUtil.logT(e);
        working = false;
      }
    }
  }
  
}
