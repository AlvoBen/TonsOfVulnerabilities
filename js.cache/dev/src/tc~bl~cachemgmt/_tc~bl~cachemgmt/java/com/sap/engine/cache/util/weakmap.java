package com.sap.engine.cache.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.lang.ref.ReferenceQueue;

import com.sap.engine.cache.util.dump.DumpWriter;

/**
 * @author Petev, Petio, i024139
 */
public class WeakMap implements SimpleMap {

  private HashMap aggregate = null;

  private ReferenceQueue queue = null;

  private boolean working = false;

  public WeakMap(RunnableReferenceQueue queue) {
    aggregate = new HashMap();
    this.queue = queue;
  }

  public Object get(Object key) {
    WeakEntry entry = (WeakEntry) aggregate.get(key);
    if (entry != null) {
      return entry.get();
    } else {
      return null;
    }
  }

  public void put(Object key, Object value) {
    WeakEntry entry = new WeakEntry(key, value, this, queue);
    aggregate.put(key, entry);
  }

  public void remove(Object key) {
    aggregate.remove(key);
  }

  public Map getAggregate() {
    return aggregate;
  }

  public void test() {
    for (int i = 0; i < 1024; i++) {
      DumpWriter.dump("i = " + i);
      put("" + i, new byte[1024 * 1024]);
    }
    System.gc();
    Thread.yield();
    System.gc();
    Set set = aggregate.keySet();
    DumpWriter.dump("num             = " + aggregate.size());
    DumpWriter.dump("set             = " + set);
    DumpWriter.dump("get (aggregate) = " + aggregate.get("1"));
    DumpWriter.dump("get             = " + get("1"));
  }

  public static void main(String[] args) {
    RunnableReferenceQueue refQueue = new RunnableReferenceQueue();
    WeakMap hashMap = new WeakMap(refQueue);
    hashMap.test();
    refQueue.stop();
  }


}
