/*
 * Created on 2004-10-19
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.cache.util;

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;

import com.sap.engine.cache.util.dump.LogUtil;

/**
 * @author ilian-n
 *
 */
public class ReferenceQueueRunnable extends ReferenceQueue implements Runnable {

  private boolean working = false;
  
  private Thread myThread = null;
  
  public ReferenceQueueRunnable() {
    super();
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
        SoftHashMapEntry entry = (SoftHashMapEntry) remove();
        Object key = entry.getKey();
        HashMap map = entry.getHashMap();
        map.remove(key);
      } catch (InterruptedException e) {
        LogUtil.logT(e);
        working = false;
      }
    }
  }
}
