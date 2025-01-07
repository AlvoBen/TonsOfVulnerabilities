/*
 * Created on 2004.8.18
 *
 */
package com.sap.engine.cache.test;

import com.sap.engine.cache.util.dump.DumpWriter;

/**
 * @author petio-p
 *
 */
public class GCTest {
  
  private static PossessedByEvilPowers creature;
  private static GCTest that;
  
  private class PossessedByEvilPowers {
    
    private String me = null;
    private Runnable hellOnEarth = null;
    
    public PossessedByEvilPowers(String me, Runnable hellOnEarth) {
      this.hellOnEarth = hellOnEarth;
      this.me = me;
    }
    
    public void finalize() throws Throwable {
      super.finalize();
      hellOnEarth.run();
      new PossessedByEvilPowers(me, hellOnEarth);
    }
  
  }
  
  public static int scourge = 0;
  public static int heavenlySouls = 0;

  public GCTest(){
    that = this;
    this.new PossessedByEvilPowers("Mephistopheles", new Runnable() {
			public void run() {
        scourge++;
        DumpWriter.dump("onGC() " + scourge);
        Runtime runtime = Runtime.getRuntime();
        DumpWriter.dump("; T " + runtime.totalMemory());
        DumpWriter.dump("; F " + runtime.freeMemory());
			}
		});
    
    Runnable divineDivinity = new Runnable() { 
      public void run() {
        for (int i = 0; i < 50; i++) {
          byte[] a = new byte[1000000];
        }
      }
    };
    
    for (int i = 0; i < 10; i++) {
      new Thread(divineDivinity).start();
    }
    
  }
  
  public static void main(String[] args) {
    new GCTest();
  }
  
}
