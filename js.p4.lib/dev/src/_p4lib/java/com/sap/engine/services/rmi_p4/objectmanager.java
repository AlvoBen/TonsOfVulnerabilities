package com.sap.engine.services.rmi_p4;

import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.lib.lang.ConvertTools;
import com.sap.engine.services.rmi_p4.exception.P4Logger;

import java.lang.ref.WeakReference;
import java.util.Currency;

/**
 * @author Georgy Stanev
 * @version 7.0
 */
public class ObjectManager {

  public static final ConvertTools ct = new ConvertTools(false);

  private ObjectEntry[] entries; // array of entries
  private ObjectEntry freeEntry; // "next" free entry
  private int size; // entries array size
  private int objectCount = 0; //Number of objects stored in the entries array

  protected ObjectManager(int _initialSize) {
    this.size = _initialSize;
    entries = new ObjectEntry[_initialSize];
    entries[_initialSize - 1] = new ObjectEntry(_initialSize - 1, null);

    for (int i = _initialSize - 2; i >= 0; i--) {
      entries[i] = new ObjectEntry(i, entries[i + 1]);
    }

    freeEntry = entries[0];
  }

  public byte[] storeObject(P4RemoteObject _obj) {
    ObjectEntry entry = null;
    synchronized (this) {
      objectCount++;
      if (freeEntry == null) {
        resizeUp();
      }

      entry = freeEntry;
      freeEntry = (ObjectEntry) freeEntry.reference;
    }
    return entry.setReference(new WeakReference(_obj));
  }

  /**
   * Remove this object from the entries list
   * @param _obj
   */
  public synchronized void deleteObject(P4RemoteObject _obj) {
    objectCount--;
    int position = ct.arrToInt(_obj.getInfo().key, 0);
    int counter = ct.arrToInt(_obj.getInfo().key, 4);

    if ((counter == entries[position].counter) && entries[position].valid) {
      freeEntry = entries[position].delete(freeEntry);
    }
  }

  public P4RemoteObject getObject(byte[] _key) {
    //TO Do check if identical
    int position = ct.arrToInt(_key, 0);
    int counter = ct.arrToInt(_key, 4);

    if (position < entries.length && ((counter == entries[position].counter) && entries[position].valid)) {
      return (P4RemoteObject) ((WeakReference) entries[position].reference).get();
    } else {
      return null;
    }
  }

  public void addLink(byte[] _key) {
    int position = ct.arrToInt(_key, 0);
    int counter = ct.arrToInt(_key, 4);
    synchronized (entries[position]) {
      if ((counter == entries[position].counter) && entries[position].valid) {
        entries[position].links++;
        entries[position].referent = ((WeakReference) entries[position].reference).get(); // restore hard reference to the object on this position (in case it was not Garbage Collected)
        entries[position].initialTime = System.currentTimeMillis();
      }
    }
  }

  public void inform(Object _identifer, byte[] _array) {
    try {
      int position = ct.arrToInt(_array, 1);
      int counter = ct.arrToInt(_array, 5);
      synchronized (entries[position]) {
        if ((counter == entries[position].counter) && entries[position].valid) {
          if (_array[0] == 0) {
            entries[position].increaseLinksInStatistics(_identifer, 1);
          } else if (_array[0] == 1) {
            entries[position].decreaseLinksInStatistics(_identifer, 1);
          }
        }
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ObjectManager.inform(Object, byte[])", P4Logger.exceptionTrace(e));
      }
      return;
    }
  }

  public void disposeConnection(Object _connection) {
    int i = -1;

    while (true) {
      synchronized (this) {
        if (++i >= size) {
          break;
        }
      }
      entries[i].disposeConnection(_connection);
    }
  }

  private synchronized void resizeUp() {
    int new_size  = size * 2;
    ObjectEntry[] temp = new ObjectEntry[new_size];
    System.arraycopy(entries, 0, temp, 0, size);
    temp[new_size - 1] = new ObjectEntry(new_size - 1, null);

    for (int j = new_size - 2; j >= size; j--) {
      temp[j] = new ObjectEntry(j, temp[j + 1]);
    }

    entries = temp;
    freeEntry = entries[size];
    size = new_size;
  }

  public void collect() {
    synchronized (this) {
      long time = System.currentTimeMillis() - 1000;

      for (int i = 0; i < entries.length; i++) {
        synchronized (entries[i]) {
          if (!entries[i].valid) {
            continue;
          }

          if ((entries[i].initialTime < time) && (entries[i].links <= 0) && (entries[i].referent != null)) {
            entries[i].notifyForClosedConnection(entries[i].referent); //notify register remote objects for close connection
            entries[i].referent = null;
          }
        }
      }
    }
    System.gc();
    synchronized (this) {
      for (int i = 0; i < entries.length; i++) {
        synchronized (entries[i]) {
          if (!entries[i].valid) {
            continue;
          }

          if (((WeakReference) entries[i].reference).get() == null) {
            freeEntry = entries[i].delete(freeEntry);
          }
        }
      }
    }
  }

  public ObjectEntry[] getEntries() {
    return entries;
  }

  /**
   * @return Number of objects stored in the entries array
   */
  public int getObjectCount() {
    return objectCount;
  }

}

