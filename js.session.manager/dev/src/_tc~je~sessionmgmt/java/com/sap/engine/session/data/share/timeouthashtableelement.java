package com.sap.engine.session.data.share;

import com.sap.engine.core.Names;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;

import java.util.TimerTask;

public class TimeoutHashtableElement extends TimerTask {
  private HashtableImpl table = null;

  private String key = null;

  private Object value = null;

  private static Location loc = Location.getLocation(TimeoutHashtableElement.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  public TimeoutHashtableElement(String key, Object value, HashtableImpl table) {
    this.key = key;
    this.value = value;
    this.table = table;
  }

  public Object getValue() {
    return value;
  }

  public void run() {
    try {
      if (loc.beInfo()) {
	String msg = "Trying to timeout hashtable element : " + value + " with a key = " + key + " in a hashtable : " + table;
	loc.logT(Severity.INFO, msg);
      }
      table.timeout(key);
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable t) {
      if (loc.beError()) {	
	loc.traceThrowableT(Severity.ERROR, "Problem timeouting a hashtable element", t);
      }
    }
  }
}
