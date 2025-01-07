package com.sap.engine.cache.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.lang.ref.ReferenceQueue;

import com.sap.engine.cache.util.dump.DumpWriter;

public class SoftMap implements SimpleMap {

  private HashMap aggregate = null;

  private ReferenceQueue queue = null;

  private boolean working = false;

  public SoftMap(RunnableReferenceQueue queue) {
    aggregate = new HashMap();
    this.queue = queue;
  }

  public Object get(Object key) {
    SoftEntry entry = (SoftEntry) aggregate.get(key);
    if (entry != null) {
      return entry.get();
    } else {
      return null;
    }
  }

  public void put(Object key, Object value) {
    SoftEntry entry = new SoftEntry(key, value, this, queue);
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
    Set set = aggregate.keySet();
    DumpWriter.dump("num             = " + aggregate.size());
    DumpWriter.dump("set             = " + set);
    DumpWriter.dump("get (aggregate) = " + aggregate.get("1"));
    DumpWriter.dump("get             = " + get("1"));
  }

  public static void main(String[] args) {
    RunnableReferenceQueue refQueue = new RunnableReferenceQueue();
    SoftMap hashMap = new SoftMap(refQueue);
    hashMap.test();
    refQueue.stop();
  }

}
