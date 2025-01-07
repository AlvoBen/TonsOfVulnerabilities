package com.sap.engine.cache.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.cache.admin.Monitor;
import com.sap.engine.cache.admin.impl.MonitorsAccessor;
import com.sap.engine.cache.core.LockerHook;
import com.sap.engine.cache.core.impl.InternalRegionFactory;
import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.AttributeNames;
import com.sap.util.cache.CacheControl;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheGroup;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.InvalidationListener;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.HolderRuntimeException;

/**
 * Created by IntelliJ IDEA.
 * User: Petio-P
 * Date: Mar 15, 2004
 * Time: 4:36:21 PM
 * To change this template use Options | File Templates.
 */
public class CacheTest implements InvalidationListener {

  private CacheRegion region = null;
  private static Thread mainThread = null;
  private static String errorMessage = null;
  private static LinkedList list = new LinkedList();
  private static CacheRegionFactory factory = null;
  private static final boolean dump = true;
  private static int totalCnt;
  private CacheControl control = null;

  public CacheTest(CacheRegion region) {
    this.region = region;
    this.control = region.getCacheControl();
    CacheTest.mainThread = Thread.currentThread();
//    this.control.registerInvalidationListener(this);
  }

  /**
   * The method is invoked when an event about changes in the cache region is due.
   *
   * @param key The cached object key of the object that has been changed.
   * @param event The type of the invalidation, can be
   * <code>InvalidationListener.EVENT_INVALIDATION</code> - if an explicit invalidation was done
   * <code>InvalidationListener.EVENT_REMOVAL</code> - if invalidation due to remove invokation was done
   * <code>InvalidationListener.EVENT_MODIFICATION</code> - if invalidation due to successive put invokation was done
   */
  public void invalidate(String key, byte event) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("(EVENT) : ");
    buffer.append("key = " + key);
    buffer.append(" ; event = ");
    switch (event) {
      case InvalidationListener.EVENT_INVALIDATION :
        buffer.append("INVALIDATION");
        break;
      case InvalidationListener.EVENT_MODIFICATION :
        buffer.append("MODIFICATION");
        break;
      case InvalidationListener.EVENT_REMOVAL :
        buffer.append("REMOVAL");
        break;
      default:
        buffer.append("UNKNOWN");
        break;
    }
    if (dump) DumpWriter.dump(buffer.toString());
  }

  /**
   * The method is invoked when an event about changes in the cache region is due. This method is called
   * only when the storage plugin supports transportation of objects
   *
   * @param key The cached object key of the object that has been changed
   * @param cachedObject The value of the cached object that has been changed
   */
  public void invalidate(String key, Object cachedObject) {
  }

  public void start() {
    synchronized (mainThread) {
      totalCnt++;
    }
    if (dump) DumpWriter.dump("Test started!");
    try {
      CacheFacade cf = region.getCacheFacade();
      cf.put("Placid", "Ocean Pacific");
      if (dump) DumpWriter.dump("(TEST) Putting keys from 0 to 9. This uses CacheGroup.put(String, Object)");
      for (int i = 0; i < 10; i++) {
        cf.put("'Cache Key " + i + "'", new Object[i]);
      }
      if (dump) DumpWriter.dump("(TEST) Using CacheGroup.keySet() to get all keys.");
      Set keySet = cf.keySet();
      Iterator keySetIterator = keySet.iterator();
      if (dump) DumpWriter.dump("(TEST) Iterating through the set and invoking CacheGroup.get(String) twice per key.");
      while (keySetIterator.hasNext()) {
        String key  = (String) keySetIterator.next();
        StringBuffer buffer = new StringBuffer();
        buffer.append("(TEST) : key = " + key);
        buffer.append("; value = " + cf.get(key));
        Object value = cf.get(key);
        if (key.charAt(0) == '\'') {
          if (value != null) {
            buffer.append("; semantics = " + ((Object[]) value).length);
          }
        }
        if (dump) DumpWriter.dump(buffer.toString());
      }
      if (dump) DumpWriter.dump("(TEST) We put using keys again from 0 to 9. Expecting events");
      for (int i = 0; i < 10; i++) {
        cf.put("'Cache Key " + i+ "'", new Object[i]);
      }
      if (dump) DumpWriter.dump("(TEST) Waiting 3 seconds in mode 0 (no background eviction)");
//      synchronized (this) {
//        this.wait(3000);
//      }
      if (dump) DumpWriter.dump("(TEST) Putting objects with keys 10 to 39. Expecting mode 1 (background eviction)");
      for (int i = 10; i < 40; i++) {
        cf.put("'Cache Key " + i + "'", new Object[i]);
      }
      if (dump) DumpWriter.dump("(TEST) Waiting 5 seconds in mode 1 (background eviction)");
//      synchronized (this) {
//        this.wait(5000);
//      }
      if (dump) DumpWriter.dump("(TEST) Putting objects with keys 40 to 99. Expecting mode 2 (active eviction on put)");
      for (int i = 40; i < 100; i++) {
        cf.put("'Cache Key " + i + "'", new Object[i]);
      }
      for (int i = 0; i < 500; i++) {
        cf.get("'Cache Key " + ((int)(Math.random()*100)) +"'");
      }

      if (dump) DumpWriter.dump("(TEST) Waiting 10 seconds in mode 2 (active eviction). Expecting mode 1 (background eviction) to start");
//      synchronized (this) {
//        this.wait(10000);
//      }
      if (dump) DumpWriter.dump("(TEST) Removing objects with keys 0 to 100. Expecting events for existing objects and mode 0");
      for (int i = 0; i < 100; i++) {
        cf.remove("'Cache Key " + i + "'");
      }
      if (dump) DumpWriter.dump("(TEST) Waiting 5 seconds on level 0 (no background eviction)");
//      synchronized (this) {
//        this.wait(5000);
//      }
      if (dump) DumpWriter.dump("(TEST) Getting non-existing object to affect hitrate (should be 66.6)");
      for (int i = 0; i < 10; i++) {
        String key = "'Cache Key " + i + "'";
        Object object = cf.get(key);
        StringBuffer buffer = new StringBuffer();
        buffer.append("GET: key = " + key);
        buffer.append("; value = " + object);
        if (dump) DumpWriter.dump(buffer.toString());
      }
      if (dump) DumpWriter.dump("(TEST) Removing All Objects");
      for (int i = 0; i < 100; i++) {
        String key = "'Cache Key " + i + "'";
        cf.remove(key);
      }
      if (dump) DumpWriter.dump("(TEST) Testing Group Operations");
      CacheGroup groupA = region.getCacheGroup("GroupA");
      CacheGroup groupB = region.getCacheGroup("GroupB");
      groupA.put("A.1", new Integer(1));
      groupA.put("A.2", new Integer(2));
      groupA.put("A.3", new Integer(3));
      groupB.put("B.1", new Integer(10));
      groupB.put("B.2", new Integer(20));
      groupB.put("B.3", new Integer(30));
      groupA.put("A/A", new String("A"));
      groupA.put("A/B", new String("B"));
      groupA.put("A/C", new String("C"));
      groupB.put("B/A", new String("AA"));
      groupB.put("B/B", new String("BB"));
      groupB.put("B/C", new String("CC"));
      groupB.put("B/A/D", new String("AA"));
      groupB.put("B/B/D", new String("BB"));
      groupB.put("B/C/D", new String("CC"));
      keySetIterator = cf.keySet().iterator();
      if (dump) DumpWriter.dump("-------------------------------------");
      while (keySetIterator.hasNext()) {
        String key = (String) keySetIterator.next();
        if (dump) DumpWriter.dump("(TEST) Found: " + key);
        if (dump) DumpWriter.dump("(TEST) Attributes: " + cf.getAttributes(key));
      }
      if (dump) DumpWriter.dump("-------------------------------------");
      if (dump) DumpWriter.dump("(TEST) Removing Group A");
      cf.remove(null, "GroupA");
      keySetIterator = cf.keySet().iterator();
      if (dump) DumpWriter.dump("-------------------------------------");
      while (keySetIterator.hasNext()) {
        String key = (String) keySetIterator.next();
        if (dump) DumpWriter.dump("(TEST) Found: " + key);
        if (dump) DumpWriter.dump("(TEST) Attributes: " + cf.getAttributes(key));
      }
      if (dump) DumpWriter.dump("-------------------------------------");
      if (dump) DumpWriter.dump("(TEST) Testing Wildcards");
      if (dump) DumpWriter.dump("(TEST) Removing \"B*\", \"'*\" and \"B/*\"");
      cf.remove("B*");
      cf.remove("'*");
      cf.remove("B/*");
      keySetIterator = cf.keySet().iterator();
      if (dump) DumpWriter.dump("-------------------------------------");
      while (keySetIterator.hasNext()) {
        String key = (String) keySetIterator.next();
        if (dump) DumpWriter.dump("(TEST) Found: " + key);
      }
      if (dump) DumpWriter.dump("-------------------------------------");
      if (dump) DumpWriter.dump("(TEST) Testing direct invalidation");
      InvalidationListenerImpl listenerObject = new InvalidationListenerImpl();
      cf.put("<invalidateable>", listenerObject);
      cf.put("<invalidateable>", "<empty>");
      cf.put("<invalidateable>", listenerObject);

      if (dump) DumpWriter.dump("(TEST) Testing explicit invalidation");

      groupA.put("AAA", "<for explicit invalidation>");
      if (dump) DumpWriter.dump("(TEST) Testing invalidate(\"**\")");
      control.invalidate("**");
      if (dump) DumpWriter.dump("(TEST) Testing invalidate(null, \"GroupA\")");
      control.invalidate(null, "GroupA");
      if (dump) DumpWriter.dump("(TEST) Removing \"**\"");
      // we will test the hierarchical relation
      
      CacheGroup attro = region.getCacheGroup("Attro");
      CacheGroup bennissimo = region.getCacheGroup("Bennissimo"); 
      attro.addChild("Bennissimo");
      attro.put("AT1", "dummy");
      bennissimo.put("BN1", "dummy");
      Iterator it = bennissimo.keySet().iterator();
      DumpWriter.dump("Before hierarchical operation:");
      while (it.hasNext()) {
        DumpWriter.dump("" + it.next());
      }
      DumpWriter.dump("-----------------");
      attro.remove((Map)null);
      it = bennissimo.keySet().iterator();
      DumpWriter.dump("After hierarchical operation:");
      while (it.hasNext()) {
        DumpWriter.dump("" + it.next());
      }
      DumpWriter.dump("-----------------");
      cf.remove("**");
      HashMap attrs = new HashMap();
      attrs.put(AttributeNames.TTL, "5000");
      cf.put("<invalidateable>", listenerObject, attrs);
      for (int i = 0; i < 1; i++) {
        try {
          synchronized (this) {
            this.wait(6000);
          }
        } catch (InterruptedException e) {
          LogUtil.logT(e);
        }
      }
    } catch (Throwable t) {
      ByteArrayOutputStream baos;
      t.printStackTrace(new PrintStream((baos = new ByteArrayOutputStream())));
      synchronized (list) {
        errorMessage = "\n" + baos.toString();
        list.addLast(errorMessage);
      }
    } finally {
      synchronized (mainThread) {
        totalCnt--;
        if (totalCnt == 0) {
          mainThread.interrupt();
        }
      }
    }
  }

  private boolean benchWork = false;
  private int baseCount = 0;
  private int putCount = 0;
  private int getCount = 0;
  private int putHashCount = 0;
  private int getHashCount = 0;
  private int invalidateCount = 0;

  public void bench() {
    final CacheFacade cf = region.getCacheFacade();
    final Object dummy = "Bla-Bla Text";

    DumpWriter.dump("(TEST) Benchmarks: [" + region.getRegionConfigurationInfo().getName() + "] Object: " + dummy.getClass().getName() + " = " + dummy);

    final Hashtable compareTo = new Hashtable();

    Runnable baseTest = new Runnable() {
      public void run() {
        while (benchWork) {
          Thread.yield();
          synchronized (mainThread) {
            synchronized (this) {
              try {
                this.wait(100);
              } catch (InterruptedException e) {
                LogUtil.logTInfo(e);
              }
            }
          }
          baseCount++;
        }
      }
    };

    Runnable putHashMapBench = new Runnable() {
      public void run() {
        while (benchWork) {
          Thread.yield();
          for (int i = 0; i < 10; i++) {
            compareTo.put("", dummy);
          }
          putHashCount++;
        }
      }
    };

    Runnable getHashMapBench = new Runnable() {
      public void run() {
        while (benchWork) {
          Thread.yield();
          for (int i = 0; i < 10; i++) {
            compareTo.get("1");
          }
          getHashCount++;
        }
      }
    };

    Runnable putBench = new Runnable() {
      public void run() {
        try {
          while (benchWork) {
            Thread.yield();
            for (int i = 0; i < 10; i++) {
              cf.put("1", dummy);
//              cf.get("!" + putCount);
            }
            putCount++;
          }
        } catch (CacheException e) {
          LogUtil.logT(e);
        }
      }
    };

    Runnable getBench = new Runnable() {
      public void run() {
        while (benchWork) {
          Thread.yield();
          for (int i = 0; i < 10; i++) {
            cf.get("1");
          }
          getCount++;
        }
      }
    };

    Runnable invalidateBench = new Runnable() {
      public void run() {
        while (benchWork) {
          Thread.yield();
          for (int i = 0; i < 10; i++) {
            control.invalidate("1");
          }
          invalidateCount++;
        }
      }
    };


    benchWork = true;
    new Thread(putBench).start();
    new Thread(putBench).start();
    new Thread(putBench).start();
    new Thread(putBench).start();
    new Thread(putBench).start();
    synchronized (mainThread) {
      try {
        for (int i = 0; i < 1; i++) mainThread.wait(10000);
      } catch (InterruptedException e) {
        LogUtil.logTInfo(e);
      }
    }
    benchWork = false;
    synchronized (mainThread) {
      try {
        for (int i = 0; i < 1; i++) mainThread.wait(1000);
      } catch (InterruptedException e) {
        LogUtil.logTInfo(e);
      }
    }
    if (true) DumpWriter.dump("Put : " + (putCount));

    benchWork = true;
    new Thread(getBench).start();
    new Thread(getBench).start();
    new Thread(getBench).start();
    new Thread(getBench).start();
    new Thread(getBench).start();
    synchronized (mainThread) {
      try {
        for (int i = 0; i < 1; i++) mainThread.wait(10000);
      } catch (InterruptedException e) {
        LogUtil.logTInfo(e);
      }
    }
    benchWork = false;
    synchronized (mainThread) {
      try {
        for (int i = 0; i < 1; i++) mainThread.wait(1000);
      } catch (InterruptedException e) {
        LogUtil.logTInfo(e);
      }
    }
    if (true) DumpWriter.dump("Get : " + (getCount));

    benchWork = true;
    new Thread(invalidateBench).start();
    new Thread(invalidateBench).start();
    new Thread(invalidateBench).start();
    new Thread(invalidateBench).start();
    new Thread(invalidateBench).start();
    synchronized (mainThread) {
      try {
        for (int i = 0; i < 1; i++) mainThread.wait(10000);
      } catch (InterruptedException e) {
        LogUtil.logTInfo(e);
      }
    }
    benchWork = false;
    synchronized (mainThread) {
      try {
        for (int i = 0; i < 1; i++) mainThread.wait(1000);
      } catch (InterruptedException e) {
        LogUtil.logTInfo(e);
      }
    }
    if (true) DumpWriter.dump("Invalidate: " + (invalidateCount));

  }

  private void stop() {
    if (dump) DumpWriter.dump("(TEST) Test stopped!");
  }

  public CacheTest() {
    main(new String[0]);
  }

  public static void main(String[] args) {
    int seconds = 180;
    //DumpWriter.setStandart("res.txt");
    mainThread = Thread.currentThread();

    if (args.length != 0) {
      try {
        seconds = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        LogUtil.logTInfo(e);
      }
    }
    new InternalRegionFactory();
    factory = CacheRegionFactory.getInstance();
    DumpWriter.dump("(TEST) factory.getCacheRegion(\"default\") = " + factory.getCacheRegion("default"));
    Properties props = new Properties();
    props.setProperty("_ALT_DELIMITER", "~");
    props.setProperty("_DIRECT_INVALIDATION_MODE", "true");
    try {
      factory.defineRegion("default_alt", "HashMapStorage", "SimpleLRU", props);
    } catch (CacheException e1) {
      // TODO Auto-generated catch block
      LogUtil.logT(e1);
    }
    final CacheTest test = new CacheTest(factory.getCacheRegion("default_alt"));

//    InternalRegionFactory.setLockerHook(new LockerHookImpl());

//    test.bench();

    Runnable testRunnable = new Runnable() {
      public void run() {
        test.start();
      }
    };

    long start = System.currentTimeMillis();
    long period = 0;

    for (int i = 0; i < 1; new Thread(testRunnable).start(), i++) {
//    for (int i = 0; i < 1; testRunnable.run()) {
      DumpWriter.dump("Thread: " + i + " / " + totalCnt + " / " + ((System.currentTimeMillis() - start) / (seconds * 10)));
      synchronized (mainThread) {
        try {
          mainThread.wait((int) (Math.random() * 100) + 150);
        } catch (InterruptedException e) {
          LogUtil.logTInfo(e);
        }
      }
      if ((System.currentTimeMillis() - start) > seconds * 1000) {
        break;
      }
    }

    synchronized (mainThread) {
      try {
        for (int i = 0; i < 1; i++) Thread.currentThread().wait(500);
        if (totalCnt > 0) {
          for (int i = 0; i < 1; i++) Thread.currentThread().wait(60000);
        }
      } catch (InterruptedException e) {
        LogUtil.logT(e);
        DumpWriter.dump("(TEST) Premature stop due to: ");
        DumpWriter.dump("(TEST) " + errorMessage);
      }
    }

    test.stop();
    period = System.currentTimeMillis() - start;

    DumpWriter.dump("(TEST) Time elapsed: " + period);
    DumpWriter.dump("(TEST) Getting monitoring info");
    Iterator messages = list.listIterator();
    while (messages.hasNext()) {
      String message = (String) messages.next();
      DumpWriter.dump("(MESSAGE) " + message);
    }
    Iterator regions = CacheRegionFactory.getInstance().iterateRegions();
    while (regions.hasNext()) {
      CacheRegion next = (CacheRegion) regions.next();
      DumpWriter.dump("(TEST) " + next.getRegionConfigurationInfo().toString());
      Monitor monitor = MonitorsAccessor.getMonitor(next.getRegionConfigurationInfo().getName());
      DumpWriter.dump("(TEST) " + monitor.toString());
    }
    synchronized(mainThread) {
			try {
        for (int i = 0; i < 1; i++) mainThread.wait(10000);
			} catch (InterruptedException e) {
        LogUtil.logT(e);
			}
    }
    DumpWriter.stop();
    // Too many dumps
    test.region.close();
  }

}

class InvalidationListenerImpl implements InvalidationListener, Serializable {

  static final long serialVersionUID = -8984895880116063569L;
  
  public void invalidate(String key, byte event) {
    if (true) DumpWriter.dump("(TEST) (INTERNAL LISTENER) key = " + key);
  }

  public void invalidate(String key, Object cachedObject) {
    if (true) DumpWriter.dump("(TEST) (INTERNAL LISTENER) key = " + key);
  }

};

class LockerHookImpl implements LockerHook {

  HashMap locks = new HashMap();

  public void execute(String name, Runnable runnable) throws CacheException {
    Object lock;
    synchronized (locks) {
      lock = locks.get(name);
      if (locks.get(name) == null) {
        locks.put(name, name);
        lock = name;
      }
    }
    synchronized (lock) {
      try {
        runnable.run();
      } catch (HolderRuntimeException e) {
        throw ((CacheException) e.getCause());
      }
    }
  }

}
