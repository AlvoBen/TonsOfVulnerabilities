package com.sap.engine.session.data.share;

import com.sap.engine.core.Names;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.TimerTask;


public class TimeoutQueueElement extends TimerTask {
  private Object value = null;
  private QueueImpl queue = null;

  private static Location loc = Location.getLocation(TimeoutQueueElement.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  public TimeoutQueueElement(QueueImpl queue) {
    this.queue = queue;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return this.value;
  }

  public void run() {
    if (loc.beInfo()) {
      String msg = "Trying to timeout queue element : "+value+" to a Queue : " + queue;
      loc.logT(Severity.INFO, msg);
    }

    if (value != null) {
      value = null;
      queue.decSize();
    }
  }
}
