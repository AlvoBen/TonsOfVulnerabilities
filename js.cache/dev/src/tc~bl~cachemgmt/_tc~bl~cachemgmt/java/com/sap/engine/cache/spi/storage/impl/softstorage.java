package com.sap.engine.cache.spi.storage.impl;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.cache.util.GCListener;
import com.sap.engine.cache.util.RunnableReferenceQueue;
import com.sap.engine.cache.util.SimpleMap;
import com.sap.engine.cache.util.SoftMap;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.AttributeNames;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.PluginContext;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author Petev, Petio, i024139
 */
public class SoftStorage extends HashMapStorage {

  public SoftStorage() {
    super("SoftStorage");
  }

}
