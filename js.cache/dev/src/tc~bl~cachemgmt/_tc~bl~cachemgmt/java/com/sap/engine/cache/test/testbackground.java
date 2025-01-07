/*
 * Created on 2004.9.15
 *
 */
package com.sap.engine.cache.test;

import com.sap.engine.cache.job.Background;
import com.sap.engine.cache.job.Task;
import com.sap.engine.cache.job.impl.BackgroundExactImpl;
import com.sap.engine.cache.util.dump.DumpWriter;

/**
 * @author petio-p
 *
 */
public class TestBackground {
  
  private static Background background = new BackgroundExactImpl();

	public static void main(String[] args) {

    background.registerTask(new Task() {
      int counter = 0;
      public String getName() { return "One Sec"; }
      public boolean repeatable() { 
        if (counter == 10) {
          background.unregisterTask(this);
        } 
        return true; 
      }
      public int getInterval() { return 500; }
      public byte getScope() { return 1; }
      public void run() { counter++; DumpWriter.dump("----: " + counter); }
    });

    background.registerTask(new Task() {
      int counter = 0;
			public String getName() { return "One Sec"; }
			public boolean repeatable() {	return (counter < 10); }
			public int getInterval() { return 1000; }
			public byte getScope() { return 1; }
			public void run() { counter++; DumpWriter.dump("++++: " + counter); }
    });

    background.registerTask(new Task() {
      int counter = 0;
      public String getName() { return "Five Sec"; }
      public boolean repeatable() { return (counter < 5); }
      public int getInterval() { return 5000; }
      public byte getScope() { return 1; }
      public void run() { counter++; DumpWriter.dump("****: " + counter); }
    });

	}
}
