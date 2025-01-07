package com.sap.engine.core.service630.container;

import com.sap.engine.core.Names;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.event.EventProcessingTimeoutException;
import com.sap.engine.frame.*;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.localization.LocalizableTextFormatter;

import java.util.HashMap;

/**
 * This class represents a container event and holds all objects related to the event.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ContainerEvent {

  //special mask used for internal synchronization
  static final int CONTAINER_SYNCHRONIZATION = 0x10000000;

  //types
  boolean isAdmin;
  boolean isBefore;

  //method constant - see ContainerEventListener.MASK_*
  int method;

  //holds component type
  byte type;
  //holds component name
  String name;

  //holds management impl object
  ManagementInterface managementInterface;
  //holds interface impl
  Object object;

  int maxProcessorsCount = 16;  // Default value should not be used; always assign the number before calling startProcess(...)
  private HashMap<String, String> processors = null;
  private boolean send = false;

  private static final Location location = Location.getLocation(ContainerEvent.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  /////////////////////////////// BEFORE SYNCHRONIZATION ///////////////////////////////////////////////////////////////

  //1. this method is called when the event is added in the listener wrapper queue
  synchronized void startProcess(final String processorName, final String processorCSNComponent) {
    if (processors == null) {
      processors = new HashMap<String, String>(maxProcessorsCount);
    }
    processors.put(processorName, processorCSNComponent);
  }

  //2. this method is called when the event is already added in all listeners
  synchronized void sendFinished(long beforeTimeout) {
    send = true;
    try {
      if (method == ContainerEventListener.MASK_BEGIN_CONTAINER_STOP) {
        //if method == begin container stop wait until the event is processed form all listeners - application stop is perform in this event
        if (location.beDebug()) {
          location.debugT("Start waiting on begin container stop.");
        }
        while (hasActiveProcessors()) {
          this.wait();
        }
        if (location.beDebug()) {
          location.debugT("Begin container stop is processed.");
        }
      } else {
        //for other events use 'before timeout'
        final long defaultBeforeTimeout = beforeTimeout;
        while (hasActiveProcessors() && beforeTimeout > 0) {
          long time = System.currentTimeMillis();
          this.wait(beforeTimeout);
          beforeTimeout -= System.currentTimeMillis() - time;
          if (beforeTimeout <= 0 && hasActiveProcessors()) {
            String msg = ResourceUtils.formatString(ResourceUtils.EVENT_TIMEOUT_EXPIRED, new Object[] {ContainerEventListenerWrapper.getMethod(this), (defaultBeforeTimeout/1000), processors.size()});
            ProcessEnvironment.getThreadDump(msg);
            if (location.beError()) {
              location.errorT(msg);
            }
            throw new EventProcessingTimeoutException(location,
                new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                    ResourceUtils.getKey(ResourceUtils.EVENT_TIMEOUT_EXPIRED2),
                    new Object[] {
                        ContainerEventListenerWrapper.getMethod(this),
                        (defaultBeforeTimeout/1000),
                        processors.size(),
                        name,
                        processors.toString()
                    })
            );
          }
        }
      }
    } catch (InterruptedException e) {
      if (location.beDebug()) {
        location.traceThrowableT(Severity.DEBUG, ResourceUtils.getString(ResourceUtils.WAIT_INTERRUPTED), e);
      }
    }
  }

  //3. this method is called when the event is already processed
  synchronized void finishProcess(final String processorName) {
    processors.remove(processorName);
    if (send && processors.isEmpty()) {
      this.notify();
    }
  }

  private boolean hasActiveProcessors() {
    return processors != null && !processors.isEmpty();
  }

}