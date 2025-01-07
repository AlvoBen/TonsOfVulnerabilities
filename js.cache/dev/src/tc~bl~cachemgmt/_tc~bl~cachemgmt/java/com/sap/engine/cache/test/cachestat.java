package com.sap.engine.cache.test;

import java.util.Iterator;
import java.util.Set;

import com.sap.engine.cache.core.impl.CacheRegionImpl;
import com.sap.engine.cache.core.impl.InternalRegionFactory;
import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;

/**
 * @author Petev, Petio, i024139
 */
//todo not used to be removed
public class CacheStat {

  public static void main(String[] args) {

    new InternalRegionFactory();
    final CacheRegionFactory factory = CacheRegionFactory.getInstance();

    Runnable runnable = new Runnable() {
      public void run() {
        while (true) {
          try {
            CacheRegion region = new CacheRegionImpl("default");
            CacheFacade facade = region.getCacheFacade();
            Object aGet = facade.get("Placid");
            DumpWriter.dump(aGet + " : ");
            Set groups = region.getCacheGroupNames();
            Iterator groupIter = groups.iterator();
            DumpWriter.dump("[");
            while (groupIter.hasNext()) {
              String s = (String) groupIter.next();
              DumpWriter.dump(s);
              if (groupIter.hasNext()) DumpWriter.dump("; ");
            }
            DumpWriter.dump("]");
            Set set = facade.keySet();
//          Iterator iter = set.iterator();
            DumpWriter.dump("{");
            DumpWriter.dump("" + set.size());
//          while (iter.hasNext()) {
//            String s = (String) iter.next();
//            DumpWriter.dump(s);
//            if (iter.hasNext()) DumpWriter.dump(", ");
//          }
            DumpWriter.dump("}");
            region.close();
            synchronized (this) {
              try {
                this.wait(100);
              } catch (InterruptedException e) {
                LogUtil.logTInfo(e);
                return;
              }
            }
          } catch (Exception e) {
            LogUtil.logT(e);
          }
        }
      }
    };

    new Thread(runnable).start();

  }

}
