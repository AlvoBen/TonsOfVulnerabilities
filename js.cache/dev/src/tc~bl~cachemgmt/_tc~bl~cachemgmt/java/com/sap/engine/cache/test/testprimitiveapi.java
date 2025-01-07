/*
 * Created on 2005.8.8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.cache.test;

import java.util.Iterator;
import java.util.Properties;

import com.sap.engine.cache.core.impl.InternalRegionFactory;
import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.PrimitiveCacheGroup;
import com.sap.util.cache.exception.CacheException;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestPrimitiveAPI {

  
  public static void main(String arg[]) {
    new InternalRegionFactory();
    CacheRegionFactory factory = CacheRegionFactory.getInstance();
    try {
      factory.initDefaultPluggables();
      factory.defineRegion("myRegion", "HashMapStorage", "SimpleLRU", new Properties());
    } catch (CacheException e) {
      LogUtil.logT(e);
    }
    CacheRegion region = factory.getCacheRegion("myRegion");
    //PrimitiveCacheGroup group = region.getPrimitiveCacheGroup("A");
//    try {
//      group.getOrPut(1, "1");
//      group.getOrPut(12, "2");
//      group.getOrPut(123, "3");
//      group.getOrPut(1234, "4");
//      group.getOrPut(12345, "5");
//      group.getOrPut(123456, "6");
//      group.getOrPut(1, "11");
//      group.getOrPut(12, "22");
//      group.getOrPut(123, "33");
//      group.getOrPut(1234, "44");
//      group.getOrPut(12345, "55");
//      group.getOrPut(123456, "66");
      DumpWriter.dump("------------------");
//      DumpWriter.dump("" + group.get(1));
//      DumpWriter.dump("" + group.get(12));
//      DumpWriter.dump("" + group.get(123));
//      DumpWriter.dump("" + group.get(1234));
//      DumpWriter.dump("" + group.get(12345));
//      DumpWriter.dump("" + group.get(123456));
      DumpWriter.dump("------------------");
//      Iterator it = group.keySet().iterator();
//      while (it.hasNext()) {
//        Long element = (Long) it.next();
//        DumpWriter.dump("" + element);
//      }
//      DumpWriter.dump("------------------");
//    } catch (CacheException e) {
//      e.printStackTrace();
//    }
  }

}
