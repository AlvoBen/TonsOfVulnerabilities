package com.sap.engine.core.cache.impl;

import com.sap.engine.core.Names;
import com.sap.bc.proj.jstartup.sadm.ShmCache;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.engine.cache.admin.Monitor;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @author Petev, Petio, i024139
 */
class ShmMonitor implements Monitor {

  private Monitor father;
  private ShmCache shmCache;
  private static final Location LOCATION = Location.getLocation(ShmMonitor.class.getName(), Names.KERNEL_DC_NAME, Names.CACHE_MANAGER_CSN_COMPONENT);
  
  public ShmMonitor(Monitor father, String owner, String description, int mode) {
    this.father = father;
		try {
      shmCache = new ShmCache(father.name(), owner, description, mode);
		} catch (ShmException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
          "ShmMonitor(Monitor, int)", e);
		}
  }


  public String name() {
    return father.name();
  }

  public int size() {
    if (shmCache != null) return (int) shmCache.getSize(); else return -1;
  }

  public int attributesSize() {
    if (shmCache != null) return (int) shmCache.getAttrSize(); else return -1;
  }

  public int namesSize() {
    return -1;
  }

  public int count() {
    if (shmCache != null) return shmCache.getCount(); else return -1;
  }

  public int hitRate() {
    if (shmCache != null) {
      long gets = shmCache.getGets();
      return gets == 0 ? 0 : (int)((long) shmCache.getHits() * 1000 / gets);
    } else {
      return 0;
    }
  }

  public int puts() {
    if (shmCache != null) return shmCache.getPuts(); else return -1;
  }

  public int modifications() {
    if (shmCache != null) return shmCache.getChanges(); else return -1;
  }

  public int removals() {
    if (shmCache != null) return shmCache.getRemoves(); else return -1;
  }

  public int evictions() {
    if (shmCache != null) return shmCache.getEvictions(); else return -1;
  }

  public int utilization() {
    if (shmCache != null) return shmCache.getUsedCount(); else return -1;
  }

  public void setHook(Monitor hook) {
    // nothing here
  }

  public Monitor getHook() {
    // we don't set hooks here
    return null;
  }

  public String toString() {
    return father.toString();
  }
  
  public void close() {
    try {
      if (shmCache != null) {
      	shmCache.close();
      	shmCache = null;
      }
    } catch (ShmException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
          "ShmMonitor.close()", e);
    }
  }

  public int gets() {
    if (shmCache != null) return shmCache.getGets(); else return -1;
  }

  public int hits() {
    if (shmCache != null) return shmCache.getHits(); else return -1;
  }

  public void onEvict(int arg0, int arg1, int arg2) {
    try {
      if (shmCache != null) shmCache.onEvict(arg0, arg1, arg2);
    } catch (ShmException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
          "ShmMonitor.onEvict(int, int, int)", e);
      e.printStackTrace();
    }
  }
  
  public void onGet(boolean arg0) {
    try {
      if (shmCache != null) shmCache.onGet(arg0);
    } catch (ShmException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
          "ShmMonitor.onGet(boolean)", e);
    }
  }
  
  public void onModify(int arg0, int arg1, int arg2, int arg3) {
    try {
      if (shmCache != null) shmCache.onModify(arg0, arg1, arg2, arg3);
    } catch (ShmException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
          "ShmMonitor.onModify(int, int, int, int)", e);
    }
  }
  
  public void onPut(int arg0, int arg1, int arg2) {
    try {
      if (shmCache != null) shmCache.onPut(arg0, arg1, arg2);
    } catch (ShmException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
          "ShmMonitor.onPut(int, int, int)", e);
    }
  }

  public void onRemove(int arg0, int arg1, int arg2) {
    try {
      if (shmCache != null) shmCache.onRemove(arg0, arg1, arg2);
    } catch (ShmException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
          "ShmMonitor.onRemove(int, int, int)", e);
    }
  }

  public void onNotify(int arg0) {
    try {
      if (shmCache != null) shmCache.onNotify(arg0);
    } catch (ShmException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
          "ShmMonitor.onNotify(int)", e);
    }
  }
}
