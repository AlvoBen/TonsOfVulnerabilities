/*
 * Created on 2004.7.19
 *
 */
package com.sap.engine.cache.examples;

import com.sap.engine.cache.core.impl.InternalRegionFactory;
import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.exception.CacheException;

/**
 * @author petio-p
 *
 */
public class ExampleStub implements Example {
  
  private Example example = null;
  
  public void setDestination(Example example) {
    this.example = example;
  }
  
	public void work() {
		if (this.example != null) {
      example.work();
		} else {
      DumpWriter.dump("Nothing to do!");
		}
	}

  public static void main(String[] args) {
    ExampleStub stub = new ExampleStub();
    if (args.length > 0) {
      String className = args[0];
      if (!className.startsWith("com.")) {
        className = "com.sap.engine.cache.examples." + className; 
      }
      try {
				Class exampleClass = Class.forName(className);
        Object exampleInstance = exampleClass.newInstance();
				try {
					stub.setDestination((Example)exampleInstance);
				} catch (ClassCastException e) {
          LogUtil.logT(e);
          DumpWriter.dump("Class " + className + " does not implement Example!");
				}
			} catch (ClassNotFoundException e) {
        LogUtil.logT(e);
        DumpWriter.dump("Example class not found: " + className);
			} catch (InstantiationException e) {
        LogUtil.logT(e);
        DumpWriter.dump("Cannot create instance: " + className);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
        LogUtil.logT(e);
        DumpWriter.dump("Illegal Access: " + className);
			}
    }

    new InternalRegionFactory();
    CacheRegionFactory factory = CacheRegionFactory.getInstance();

    try {
			factory.initDefaultPluggables();
      stub.work();
		} catch (CacheException e) {
      LogUtil.logT(e);
      DumpWriter.dump("FATAL: Cannot initialize default plugins");
		}
    
  }

}
