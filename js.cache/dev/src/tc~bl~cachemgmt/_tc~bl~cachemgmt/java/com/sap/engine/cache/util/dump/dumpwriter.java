package com.sap.engine.cache.util.dump;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Hashtable;

public class DumpWriter implements Runnable {

  private static int counter = 0;
  private static Hashtable entities = new Hashtable();
  private static DumpWriter writer = null;
  private static LinkedList queue = null;
  private static boolean stop = false;
  private static Thread thread = null;

  static {
    writer = new DumpWriter();
    thread = new Thread(writer);
    thread.setDaemon(true);
    thread.start();
  }

  private DumpWriter() {
    queue = new LinkedList();
  }

  public static synchronized int getEntity(String fileName) {
    counter++;
    entities.put(new Integer(counter), new DumpEntity(fileName));
    return counter;
  }

  public static void dump(String s) {
    dump(-1, s);
  }

  public static void dump(int id, String s) {
    synchronized(queue) {
      queue.addLast(new DumpPair(id, s));
      queue.notify();
    }
  }

  public static void dumpStack() {
    dumpStack(-1);
  }

  public static void dumpStack(int id) {
    String stack;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    try {
      throw new Exception();
    } catch (Exception e) {
      e.printStackTrace(ps);
      stack = new String(baos.toByteArray());
    }
    dump(id, "<Thread Stack> : " + stack);
  }

  public static void dumpStack(String title) {
    dumpStack(-1, title);
  }

  public static void dumpStack(int id, String title) {
    String stack;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    try {
      throw new Exception();
    } catch (Exception e) {
      e.printStackTrace(ps);
      stack = new String(baos.toByteArray());
    }
    dump(id, title + " : " + stack);
  }

  public void run() {
    while (!stop) {
      synchronized (queue) {
        if (queue.isEmpty()) {
          synchronized (DumpWriter.class) {
            DumpWriter.class.notify(); // notify stop
          }
          try {
            queue.wait();
            if (stop) {
              break;
            }
          } catch (InterruptedException e) {
            LogUtil.logTInfo(e);
          }
        }
        DumpPair pair = null;
        synchronized (queue) {
          pair = (DumpPair)queue.getFirst();
          queue.removeFirst();
        }
        DumpEntity entity = (DumpEntity) entities.get(new Integer(pair.id));
        if (entity != null) {
          entity.write(pair.dump);
        } else if (pair.id == -1) {
          DumpEntity.writeStd(pair.dump);
        }
      }
    }
  }

  public static void stop() {
    while (!queue.isEmpty()) {
      synchronized (DumpWriter.class) {
        try {
          DumpWriter.class.wait();
        } catch (InterruptedException e) {
          LogUtil.logTInfo(e);
        }
        stop = true;
        synchronized (queue) {
          queue.notify();
        }
      }
      return;
    }
    stop = true;
    synchronized (queue) {
      queue.notify();
    }
  }

  public static void setStandart(String name) {
    DumpEntity.stdEntity = new DumpEntity(name);
  }

  //Test
  public static void main(String[] args) {
    final int t1 = DumpWriter.getEntity("t1.txt");
    final int t2 = DumpWriter.getEntity("t2.txt");
    final int t3 = DumpWriter.getEntity("t3.txt");
    final int dumpId = DumpWriter.getEntity("ClientContextDumps2.txt");
    for (int t = 0; t < 10; t++) {
      Runnable runnable = new Runnable() {
        public void run() {
          for (int i = 0; i < 700; i++) {
            DumpWriter.dump(t1, "1");
            DumpWriter.dump(t2, "2");
            DumpWriter.dumpStack(t3);
            DumpWriter.dumpStack(t1, "Creten");
            DumpWriter.dump(65423, "3");
            DumpWriter.dump(dumpId, "4");
          }
          DumpWriter.dump("========");
        }
      };
      new Thread(runnable).start();
    }
    DumpWriter.dump("-----------------------------");
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      LogUtil.logT(e);
    }
    DumpWriter.stop();
  }

}
