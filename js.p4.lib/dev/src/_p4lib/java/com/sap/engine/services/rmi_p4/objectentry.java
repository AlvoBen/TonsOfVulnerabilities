package com.sap.engine.services.rmi_p4;

import com.sap.engine.lib.lang.ConvertTools;
import com.sap.engine.services.rmi_p4.interfaces.P4Notification;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.tc.logging.Severity;

import java.util.Hashtable;
import java.lang.ref.WeakReference;
import java.rmi.Remote;
import java.rmi.server.Unreferenced;

/**
 * @author Georgy Stanev
 * @version 7.0
 */
public class ObjectEntry {

  public static final ConvertTools ct = new ConvertTools(false);
  
  /**
   * reference to the next free object entry or week reference to remote object.
   */
  public Object reference;
  
  /**
   * hard reference to exported remote object (delegate for our skeleton)
   */
  protected Object referent;
  private int index;
  protected int counter = 0; // how many times this entry was reused to store some remote object
  protected boolean valid = false;
  private int currentLinks;
  public int links = 0; //active links in this moment (remote references to the object)
  protected long initialTime = 0;
  public Hashtable connectionStatistics = new Hashtable(1);

  protected ObjectEntry(int _index, Object _ref) {
    this.index = _index;
    this.reference = _ref;
  }

  public boolean isValid() {
    return valid;
  }

  protected synchronized void increaseLinksInStatistics(Object _obj, int _count) {
    Integer i;

    if (connectionStatistics.containsKey(_obj)) {
      i = (Integer) connectionStatistics.get(_obj);
      i = new Integer(i.intValue() + _count);
    } else {
      i = new Integer(_count);
    }

    currentLinks += _count;
    connectionStatistics.put(_obj, i);

    if (currentLinks > links) {
      links += (currentLinks - links);
    }
    if ((referent == null) && (reference instanceof WeakReference)){
      referent = ((WeakReference)reference).get(); 
    }
  }

  protected synchronized void decreaseLinksInStatistics(Object _obj, int _count) {
    if (connectionStatistics.containsKey(_obj)) {
      Integer i = (Integer) connectionStatistics.get(_obj);
      i = new Integer(i.intValue() - _count);
      connectionStatistics.put(_obj, i);
      currentLinks -= _count;
      links -= _count;
    }

    if ((links <= 0) && (!P4ObjectBroker.getBroker().useReiterationOfGC)) {
      notifyForClosedConnection(referent); //notify register remote objects for close connection
      referent = null;
    }

  }

  /**
   * Given connection is checked how many references it has to object refered by this object entry.
   * Then links counter of this entry is decreased by this value. This method is designed for cases
   * when some connection fails, to maintain remote references counter.    
   *
   * @param _key
   */
  protected synchronized void disposeConnection(Object _key) {
    if (valid) {
      if (connectionStatistics.containsKey(_key)) {
        Integer i = (Integer) connectionStatistics.get(_key);
        links -= i.intValue();
        currentLinks -= i.intValue();
        connectionStatistics.remove(_key);

        if (connectionStatistics.isEmpty()) {
          links = 0;
          currentLinks = 0;
        }
      }
    }
    if (P4ObjectBroker.getBroker() != null) {
      if ((links <= 0) && (!P4ObjectBroker.getBroker().useReiterationOfGC)) {
        notifyForClosedConnection(referent); //notify register remote objects for close connection
        referent = null;
      }
    }
  }

  protected void notifyForClosedConnection(Object obj) {
    if ((obj != null) && (obj instanceof P4RemoteObject)) {
      Remote delegate = ((P4RemoteObject) obj).delegate;
      if (delegate != null) {
        try {
          if (delegate instanceof P4Notification) {
            ((P4Notification) delegate).closedConnection();
          }
        } catch (Throwable th) { // shield from buggy implementations
           if (P4Logger.getLocation().beDebug()) {
             P4Logger.getLocation().traceThrowableT(Severity.DEBUG, "ObjectEntry.notifyForClosedConnection(Object)", th);
           }
        }
        try {
           if (delegate instanceof Unreferenced) {
             ((Unreferenced)delegate).unreferenced();
           }
        } catch (Throwable th) { // shield from buggy implementations
           if (P4Logger.getLocation().beDebug()) {
             P4Logger.getLocation().traceThrowableT(Severity.DEBUG, "ObjectEntry.notifyForClosedConnection(Object)", th);
           }
        }
      }
    }
  }

  protected synchronized byte[] setReference(Object _reference) {
    reference = _reference;
    valid = true;
    return getKey();
  }

  /**
   * 
   * @param _entry - current free entry
   * @return
   */
  protected synchronized ObjectEntry delete(ObjectEntry _entry) {
    if (!valid) {
      return _entry;
    } else {
      valid = false;
      counter++;
      connectionStatistics.clear();
      notifyForClosedConnection(referent); //notify register remote objects for close connection
      this.referent = null;
      this.reference = _entry;
      return this;
    }
  }

  private byte[] getKey() {
    byte[] key = new byte[Message.OBJECT_KEY_SIZE];
    ct.intToArr(index, key, 0);
    ct.intToArr(counter, key, 4);
    System.arraycopy(P4ObjectBroker.init().getTimeStamp(), 0, key, 8, 4);
    return key;
  }

  public int getCounter() {
    return counter;
  }

  public boolean isReferentNull() {
    return (referent == null);
  }
}

