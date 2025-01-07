package com.sap.engine.session.data.share;

import com.sap.engine.core.Names;
import com.sap.engine.session.data.share.exceptions.*;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.*;

public class HashtableImpl {
  private HashMap<String, TimeoutHashtableElement> map = null;

  private String className = null;
  private int threshold = 0;
  private long timeout = -1;
  private String loaderName = null;

  private static Location loc = Location.getLocation(HashtableImpl.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  public HashtableImpl(String className, String loaderName, int threshold, long timeout) {
    this.className = className;
    this.timeout = timeout;
    this.threshold = threshold;
    this.loaderName = loaderName;

    int initialCapacity = Math.min(threshold, 16);

    map = new HashMap<String, TimeoutHashtableElement>(initialCapacity);
  }

  public synchronized void put(String key, Object value) throws TooManyElementsException, ClassNotAcceptableException {
    if (loc.beInfo()) {
      String msg = "Trying to put element " + value + " with key = " + key + " into a Hashtable<" + className + ">";
      loc.logT(Severity.INFO, msg);
    }
    if (value.getClass().getName().equals(className)) {
      if (threshold == map.size()) {
        String msg = "The hashtable limit is reached. The hashtable contains "+map.size()+" elements!";
        loc.logT(Severity.ERROR, msg);
        throw new TooManyElementsException(msg);
      }
      remove(key);
      TimeoutHashtableElement element = new TimeoutHashtableElement(key, value, this);
      map.put(key, element);
      if (loc.beInfo()){
        String msg = "Put an element with key = " + key + " and value = " + value;
        loc.logT(Severity.INFO, msg);
      }
      if (timeout > 0) {
        if (loc.beInfo()) {
          String msg = "Trying to cancel timeout element with key = " + key + " from a Hashtable<"+
                        className+"> with timeout "+timeout;
          loc.logT(Severity.INFO, msg);
        }
        TimeoutHashtableFactory.setForTimeout(element, timeout);
      }
    } else {
      throw new ClassNotAcceptableException("The hashtable is parametrized by " + className + ", trying to put instance of "+value.getClass().getName());
    }
  }

  /**
   * Returns the element associated with this name or returns null if there is no such key.
   *
   * @param key the key
   * @return value
   */
  public synchronized Object get(String key) {
    if (loc.beInfo()) {
      String msg = "Trying to get element from a Hashtable<"+className+"> providing key = "+key;
      loc.logT(Severity.INFO, msg);
    }
    TimeoutHashtableElement element = map.get(key);
    Object value = null;
    if (element != null) {
      value = element.getValue();
      if(loc.beInfo()){
        String msg = "Found element<" + value + "> for key <" + key + "> and className<" + className + ">";
        loc.logT(Severity.INFO, msg);
      }
    }
    return value;
  }

  /**
   * Removes the element associated with this name
   *
   * @param key the key
   * @return the removed object
   */
  public synchronized Object remove(String key) {
    if (loc.beInfo()) {
      String msg = "Trying to remove element from a Hashtable<"+className+"> providing key = "+key;
      loc.logT(Severity.INFO, msg);
    }
    TimeoutHashtableElement element = map.remove(key);
    Object value = null;
    if (element != null) {
      value = element.getValue();
      element.cancel();
      if (loc.beInfo()){
        String msg = "The element is found " + value;
        loc.logT(Severity.INFO, msg);
      }
    }
    return value;
  }

  /**
   * Returns the number of the elements
   * @return the size
   */
  public synchronized int size() {
    return map.size();
  }

  /**
   * Returns the set of keys
   *
   * @return value
   */
  public synchronized Set<String> keys() {
    return map.keySet();
  }

  /**
   * Returns the set of values
   *
   * @return value
   */
  public synchronized Collection values() {
    ArrayList list = new ArrayList();
    for (TimeoutHashtableElement element : map.values()) {
      list.add(element.getValue());
    }
    return list;
  }

  void timeout(String key) {
    map.remove(key);
  }

  public String getLoaderName(){
    return this.loaderName;
  }

  public String getClassName(){
    return this.className;
  }
}
