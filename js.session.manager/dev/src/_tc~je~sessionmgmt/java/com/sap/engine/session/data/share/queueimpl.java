package com.sap.engine.session.data.share;

import com.sap.engine.core.Names;
import com.sap.engine.session.data.share.exceptions.*;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;


public class QueueImpl {
  private TimeoutQueueElement[] elements = null;

  private int head = 0;
  private int tail = 0;
  private int size = 0;

  private String className = null;
  private String loaderName = null;
  private int threshold = 0;
  private long timeout = -1;

  private static Location loc = Location.getLocation(QueueImpl.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  public QueueImpl(String className, String loaderName, int threshold, long timeout) {
    this.className = className;
    this.loaderName = loaderName;
    this.timeout = timeout;
    this.threshold = threshold;

    this.elements = new TimeoutQueueElement[threshold];
    for (int i=0;i<threshold;i++) {
      elements[i] = new TimeoutQueueElement(this);
    }
  }

  public synchronized void add(Object element) throws TooManyElementsException, ClassNotAcceptableException {
    if (loc.beInfo()) {
      String msg = "Trying to add element : "+element+" to a Queue<"+className+">";
      loc.logT(Severity.INFO, msg);
    }

    if (element == null) {
      return;
    }

    if (element.getClass().getName().equals(className)) {
      if (threshold == size) {
        String msg = "The queue limit is reached. The queue contains \"+size+\" elements!";
        loc.logT(Severity.ERROR, msg);
        throw new TooManyElementsException(msg);
      }

      elements[tail].setValue(element);
      tail = (tail+1)%threshold;
      size++;


      if (loc.beInfo()){
        String msg = "Put an element with element = " + element;
        loc.logT(Severity.INFO, msg);
      }

      if (timeout > 0) {
        if (loc.beInfo()) {
          String msg = "Trying to set element : "+element+" to a Queue<"+className+"> for timeout "+timeout;
          loc.logT(Severity.INFO, msg);
        }
        TimeoutQueueFactory.setForTimeout(elements[tail], timeout);
      }
    } else {
      String msg = "Queue is parametrized by " + className + ", trying to insert instance of "+element.getClass().getName();
      loc.logT(Severity.ERROR, msg);
      throw new ClassNotAcceptableException(msg);
    }
  }

  public synchronized boolean isEmpty () {
    return size == 0;
  }

  public synchronized int size() {
    return size;
  }

  public synchronized Object get() {
    if (loc.beInfo()) {
      String msg = "Trying to get element from a Queue<"+className+">";
      loc.logT(Severity.INFO, msg);
    }

    TimeoutQueueElement queueElement = findFirstNotNull();

    Object retValue = queueElement.getValue();

    if (retValue != null) {
      if (timeout > 0) {
        if (loc.beInfo()) {
          String msg = "Trying to cancel timeout element from a Queue<"+className+"> with timeout "+timeout;
          loc.logT(Severity.INFO, msg);
        }
        queueElement.cancel();
      }

      queueElement.setValue(null);
      size--;
    }
    if (loc.beInfo()) {
      String msg = "Return element from a Queue<" + retValue + ">";
      loc.logT(Severity.INFO, msg);
    }

    return retValue;
  }

  synchronized void decSize() {
    size--;
  }

  public synchronized Object read() {
    if (loc.beInfo()) {
      String msg = "Trying to read element from a Queue<"+className+">";
      loc.logT(Severity.INFO, msg);
    }

    TimeoutQueueElement queueElement = findFirstNotNull();

    return queueElement.getValue();
  }

  private TimeoutQueueElement findFirstNotNull() {
    if (size == 0) {
      return null;
    }

    Object retValue = elements[head].getValue();

    int counter = threshold;
    while (retValue == null && counter > 0) {
      head = (head+1)%threshold;
      retValue = elements[head].getValue();
      counter--;
    }

    return elements[head];
  }
  
  public String getLoaderName(){
    return this.loaderName;
  }

  public String getClassName(){
    return this.className;
  }
}
