/*
 * Created on 2004.7.7
 *
 */
package com.sap.engine.cache.test;

import com.sap.engine.cache.admin.Monitor;
import com.sap.engine.cache.admin.impl.MonitorsAccessor;
import com.sap.engine.cache.core.impl.InternalRegionFactory;
import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;

/**
 * @author petio-p
 *
 */
public class TestStorages {
  
  public static void main(String[] args) {
    new InternalRegionFactory();
    CacheRegionFactory factory = CacheRegionFactory.getInstance();
    try {
			factory.initDefaultPluggables();
		} catch (CacheException e) {
      LogUtil.logT(e);
		}
    RegionConfigurationInfo configuration = new RegionConfigurationInfo() {
      public byte getRegionScope() {
        return SCOPE_INSTANCE;
      }

      public byte getInvalidationScope() {
        return SCOPE_LOCAL;
      }

      public String getName() {
        return null;
      }

      public int getSizeQuota(byte b) {
        return 10000000 + b * 50000000;
      }

      public int getCountQuota(byte b) {
        return 100 + b * 500;
      }

      public int getId() {
        return 0;
      }

      public boolean getDirectObjectInvalidationMode() {
        return true;
      }

      public boolean getTraceMode() {
        return false;
      }

      public boolean getLoggingMode() {
        return false;
      }

      public boolean isSynchronous() {
        return true;
      }

			public boolean isClientDependent() {
				return false;
			}
      public boolean getPutIsModificationMode() { return false; }
	  public boolean getSenderIsReceiverMode() { return true; }
    };
    try {
      factory.defineRegion("HARD", "HashMapStorage", "SimpleLRU", configuration);
			factory.defineRegion("SOFT", "SoftStorage", "SimpleLRU", configuration);
      factory.defineRegion("FILE", "FileStorage", "SimpleLRU", configuration);
      factory.defineRegion("COMB", "CombinatorStorage (SoftStorage + FileStorage)", "SimpleLRU", configuration);

      CacheTest test = new CacheTest(factory.getCacheRegion("SOFT"));
      test.bench();
      Monitor mon = MonitorsAccessor.getMonitor("SOFT");
      DumpWriter.dump("" + mon);
      test = null;
      System.gc();
      Thread.yield();
      System.gc();
      Thread.yield();

      test = new CacheTest(factory.getCacheRegion("HARD"));
      test.bench();
      mon = MonitorsAccessor.getMonitor("HARD");
      DumpWriter.dump("" + mon);
      test = null;
      System.gc();
      Thread.yield();
      System.gc();
      Thread.yield();

      test = new CacheTest(factory.getCacheRegion("FILE"));
      test.bench();
      mon = MonitorsAccessor.getMonitor("FILE");
      DumpWriter.dump("" + mon);
      test = null;
      System.gc();
      Thread.yield();
      System.gc();
      Thread.yield();

      test = new CacheTest(factory.getCacheRegion("COMB"));
      test.bench();
      mon = MonitorsAccessor.getMonitor("COMB");
      DumpWriter.dump("" + mon);
      test = null;
      System.gc();
      Thread.yield();
      System.gc();
      Thread.yield();
		} catch (CacheException e) {
      LogUtil.logT(e);
		}
  }

}
