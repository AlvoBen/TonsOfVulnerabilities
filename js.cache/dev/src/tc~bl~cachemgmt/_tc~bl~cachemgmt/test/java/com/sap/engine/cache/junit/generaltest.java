/*
 * Copyright (c) 2008 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.cache.junit;

import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheGroup;
import com.sap.util.cache.exception.CacheException;
//import com.sap.tc.logging.Location;
//import com.sap.tc.logging.Severity;
//import com.sap.tc.logging.ConsoleLog;
import com.sap.engine.cache.core.impl.CombinatorStorageWriteTrue;
import com.sap.engine.cache.core.impl.PluggableFramework;
import com.sap.engine.cache.core.impl.CombinatorStorage;

import java.util.Properties;
import java.util.Set;
import java.util.Collection;

import static org.junit.Assert.*;
import org.junit.*;


/**
 * @author Nikola Arnaudov
 * @version 7.20
 */
public class GeneralTest {

//  private static final Location TRACE = Location.getLocation(GeneralTest.class);

  private static final String[] STORAGES = {"HashMapStorage", "CombinatorStorageWriteTrue", "SoftStorage", /*"DummyStorage", "FileStorage", "CombinatorStorage"*/ };


  private static final CacheRegionFactory cacheRegionFactory = CacheRegionFactory.getInstance();
  private static int count = 0;

  private static CacheRegion getNewRegion(String storage) throws CacheException {
    String regionName;
    CacheRegion region;
    do {
      regionName = storage + "_" + count;
      region = cacheRegionFactory.getCacheRegion(regionName);
      count++;
    } while (region != null);
    cacheRegionFactory.defineRegion(regionName, storage, "LRUEvictionPolicy", (Properties)null);
    return cacheRegionFactory.getCacheRegion(regionName);
  }


  /*
   * This method creates two instances of P2PConnectorImpl, which represent two server nodes.
   * These instances are used throughout the tests to send one-way messages and requests from the one
   * to the other.
   *
   */
  @BeforeClass
  public static void initialize() throws Exception {
//    Location.getRoot().addLog(new ConsoleLog()); //$JL-CONSOLE_LOG$ This is used in Junit tests
//    TRACE.setEffectiveSeverity(Severity.ALL);
    CombinatorStorageWriteTrue storage = new CombinatorStorageWriteTrue();
    CombinatorStorage combinatorStorage = new CombinatorStorage();
    Properties props = new Properties();
    props.setProperty("CombinatorStorage.FRONTEND_STORAGE", "HashMapStorage");
    props.setProperty("CombinatorStorage.BACKEND_STORAGE", "HashMapStorage");
    storage.init("CombinatorStorageWriteTrue", props);
    combinatorStorage.init("CombinatorStorage", props);
    PluggableFramework.putPluggable("CombinatorStorageWriteTrue", storage);
    PluggableFramework.putPluggable("CombinatorStorage", combinatorStorage);
    System.out.println("EvictionNames = " + PluggableFramework.listEvictionNames());
    System.out.println("StorageNames = " + PluggableFramework.listStorageNames());
  }

  @org.junit.Test
  public void testIsolation00() throws Exception {
    for (String storage : STORAGES) {
      System.out.println("storage = " + storage);//todo put logs
      CacheRegion region = getNewRegion(storage);
      System.out.println("region = " + region);

      assertFalse("region.getCacheGroupNames().isEmpty", region.getCacheGroupNames().isEmpty()); // at least 1 null
      assertEquals("region.getCacheGroupNames().isSize", 1, region.getCacheGroupNames().size()); //

      final CacheFacade facade = region.getCacheFacade();
      {
        assertTrue("facade.isEmpty()", facade.isEmpty());
        Set keys = facade.keySet();
        Collection values = facade.values();
        assertEquals("keys.size", 0, keys.size());
        assertEquals("values.size", 0, values.size());
      }
      {
        String key = "key1";
        String val = "val1";
        facade.put(key, val);

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());
      }

      {
        String key = "key1";
        String val = "val11";
        facade.put(key, val);

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());
      }

      {
        String key = "key2";
        String val = "val2";
        facade.put(key, val);

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 2, keys.size());
        assertEquals("values.size", 2, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());
      }

      //Group Test
      //facade.clear();

      {
        String key = "gkey1";
        String val = "gval1";
        String group1 = "group1";
        String group2 = "group2";
        facade.put(key, val, group1);

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 3, keys.size());
        assertEquals("values.size", 3, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());

        CacheGroup gr1 = region.getCacheGroup(group1);
        CacheGroup gr2 = region.getCacheGroup(group2);


        keys = gr1.keySet();
        values = gr1.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", val, gr1.get(key));
        assertEquals("get", val, gr1.get(key, true));
        assertEquals("get", val, gr1.get(key, false));
        assertEquals("get", null, gr1.getAttributes(key));
        assertEquals("get", null, gr1.getAttributes(key, true));
        assertEquals("get", null, gr1.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr1.isEmpty());


        keys = gr2.keySet();
        values = gr2.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 0, keys.size());
        assertEquals("values.size", 0, values.size());
        assertEquals("get", null, gr2.get(key));
        assertEquals("get", null, gr2.get(key, true));
        assertEquals("get", null, gr2.get(key, false));
        assertEquals("get", null, gr2.getAttributes(key));
        assertEquals("get", null, gr2.getAttributes(key, true));
        assertEquals("get", null, gr2.getAttributes(key, false));
        assertTrue("facade.isEmpty()", gr2.isEmpty());
      }

      {
        String key = "gkey1";
        String val = "gval11";
        String group1 = "group1";
        String group2 = "group2";
        facade.put(key, val, group1);

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 3, keys.size());
        assertEquals("values.size", 3, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());

        CacheGroup gr1 = region.getCacheGroup(group1);
        CacheGroup gr2 = region.getCacheGroup(group2);


        keys = gr1.keySet();
        values = gr1.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", val, gr1.get(key));
        assertEquals("get", val, gr1.get(key, true));
        assertEquals("get", val, gr1.get(key, false));
        assertEquals("get", null, gr1.getAttributes(key));
        assertEquals("get", null, gr1.getAttributes(key, true));
        assertEquals("get", null, gr1.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr1.isEmpty());


        keys = gr2.keySet();
        values = gr2.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 0, keys.size());
        assertEquals("values.size", 0, values.size());
        assertEquals("get", null, gr2.get(key));
        assertEquals("get", null, gr2.get(key, true));
        assertEquals("get", null, gr2.get(key, false));
        assertEquals("get", null, gr2.getAttributes(key));
        assertEquals("get", null, gr2.getAttributes(key, true));
        assertEquals("get", null, gr2.getAttributes(key, false));
        assertTrue("facade.isEmpty()", gr2.isEmpty());
      }

      {
        String key = "gkey2";
        String val = "gval2";
        String group1 = "group1";
        String group2 = "group2";
        facade.put(key, val, group1);

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 4, keys.size());
        assertEquals("values.size", 4, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());

        CacheGroup gr1 = region.getCacheGroup(group1);
        CacheGroup gr2 = region.getCacheGroup(group2);


        keys = gr1.keySet();
        values = gr1.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 2, keys.size());
        assertEquals("values.size", 2, values.size());
        assertEquals("get", val, gr1.get(key));
        assertEquals("get", val, gr1.get(key, true));
        assertEquals("get", val, gr1.get(key, false));
        assertEquals("get", null, gr1.getAttributes(key));
        assertEquals("get", null, gr1.getAttributes(key, true));
        assertEquals("get", null, gr1.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr1.isEmpty());


        keys = gr2.keySet();
        values = gr2.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 0, keys.size());
        assertEquals("values.size", 0, values.size());
        assertEquals("get", null, gr2.get(key));
        assertEquals("get", null, gr2.get(key, true));
        assertEquals("get", null, gr2.get(key, false));
        assertEquals("get", null, gr2.getAttributes(key));
        assertEquals("get", null, gr2.getAttributes(key, true));
        assertEquals("get", null, gr2.getAttributes(key, false));
        assertTrue("facade.isEmpty()", gr2.isEmpty());
      }

      //change group

      {
        String key = "gkey1";
        String val = "gval1";
        String group1 = "group2";
        String group2 = "group1";
        facade.put(key, val, group1);

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 4, keys.size());
        assertEquals("values.size", 4, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());

        CacheGroup gr1 = region.getCacheGroup(group1);
        CacheGroup gr2 = region.getCacheGroup(group2);


        keys = gr1.keySet();
        values = gr1.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", val, gr1.get(key));
        assertEquals("get", val, gr1.get(key, true));
        assertEquals("get", val, gr1.get(key, false));
        assertEquals("get", null, gr1.getAttributes(key));
        assertEquals("get", null, gr1.getAttributes(key, true));
        assertEquals("get", null, gr1.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr1.isEmpty());


        keys = gr2.keySet();
        values = gr2.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", null, gr2.get(key));
        assertEquals("get", null, gr2.get(key, true));
        assertEquals("get", null, gr2.get(key, false));
        assertEquals("get", null, gr2.getAttributes(key));
        assertEquals("get", null, gr2.getAttributes(key, true));
        assertEquals("get", null, gr2.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr2.isEmpty());
      }

      {
        String key = "gkey1";
        String val = "gval11";
        String group1 = "group2";
        String group2 = "group1";
        facade.put(key, val, group1);

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 4, keys.size());
        assertEquals("values.size", 4, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());

        CacheGroup gr1 = region.getCacheGroup(group1);
        CacheGroup gr2 = region.getCacheGroup(group2);


        keys = gr1.keySet();
        values = gr1.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", val, gr1.get(key));
        assertEquals("get", val, gr1.get(key, true));
        assertEquals("get", val, gr1.get(key, false));
        assertEquals("get", null, gr1.getAttributes(key));
        assertEquals("get", null, gr1.getAttributes(key, true));
        assertEquals("get", null, gr1.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr1.isEmpty());

        keys = gr2.keySet();
        values = gr2.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", null, gr2.get(key));
        assertEquals("get", null, gr2.get(key, true));
        assertEquals("get", null, gr2.get(key, false));
        assertEquals("get", null, gr2.getAttributes(key));
        assertEquals("get", null, gr2.getAttributes(key, true));
        assertEquals("get", null, gr2.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr2.isEmpty());
      }

      {
        String key = "gkey2";
        String val = "gval2";
        String group1 = "group2";
        String group2 = "group1";
        facade.put(key, val, group1);

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 4, keys.size());
        assertEquals("values.size", 4, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());

        CacheGroup gr1 = region.getCacheGroup(group1);
        CacheGroup gr2 = region.getCacheGroup(group2);


        keys = gr1.keySet();
        values = gr1.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 2, keys.size());
        assertEquals("values.size", 2, values.size());
        assertEquals("get", val, gr1.get(key));
        assertEquals("get", val, gr1.get(key, true));
        assertEquals("get", val, gr1.get(key, false));
        assertEquals("get", null, gr1.getAttributes(key));
        assertEquals("get", null, gr1.getAttributes(key, true));
        assertEquals("get", null, gr1.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr1.isEmpty());


        keys = gr2.keySet();
        values = gr2.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 0, keys.size());
        assertEquals("values.size", 0, values.size());
        assertEquals("get", null, gr2.get(key));
        assertEquals("get", null, gr2.get(key, true));
        assertEquals("get", null, gr2.get(key, false));
        assertEquals("get", null, gr2.getAttributes(key));
        assertEquals("get", null, gr2.getAttributes(key, true));
        assertEquals("get", null, gr2.getAttributes(key, false));
        assertTrue("facade.isEmpty()", gr2.isEmpty());
      }

      {
        String key = "rem1";
        String val = "remval2";
        String group1 = "group1";
        String group2 = "group2";
        facade.put(key, val, group1);

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 5, keys.size());
        assertEquals("values.size", 5, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());

        CacheGroup gr1 = region.getCacheGroup(group1);
        CacheGroup gr2 = region.getCacheGroup(group2);

        keys = gr1.keySet();
        values = gr1.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", val, gr1.get(key));
        assertEquals("get", val, gr1.get(key, true));
        assertEquals("get", val, gr1.get(key, false));
        assertEquals("get", null, gr1.getAttributes(key));
        assertEquals("get", null, gr1.getAttributes(key, true));
        assertEquals("get", null, gr1.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr1.isEmpty());

        keys = gr2.keySet();
        values = gr2.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 2, keys.size());
        assertEquals("values.size", 2, values.size());
        assertEquals("get", null, gr2.get(key));
        assertEquals("get", null, gr2.get(key, true));
        assertEquals("get", null, gr2.get(key, false));
        assertEquals("get", null, gr2.getAttributes(key));
        assertEquals("get", null, gr2.getAttributes(key, true));
        assertEquals("get", null, gr2.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr2.isEmpty());
      }

      {
        String key = "gkey1";
        String val = "gval11";
        String group1 = "group1";
        String group2 = "group2";

        CacheGroup gr1 = region.getCacheGroup(group1);
        CacheGroup gr2 = region.getCacheGroup(group2);
        gr2.remove("bau");
        gr2.remove(key);
        gr2.remove("rem1");

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 4, keys.size());
        assertEquals("values.size", 4, values.size());
        assertEquals("get", null, facade.get(key));
        assertEquals("get", null, facade.get(key, true));
        assertEquals("get", null, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());


        keys = gr1.keySet();
        values = gr1.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", null, gr1.get(key));
        assertEquals("get", null, gr1.get(key, true));
        assertEquals("get", null, gr1.get(key, false));
        assertEquals("get", null, gr1.getAttributes(key));
        assertEquals("get", null, gr1.getAttributes(key, true));
        assertEquals("get", null, gr1.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr1.isEmpty());


        keys = gr2.keySet();
        values = gr2.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", null, gr2.get(key));
        assertEquals("get", null, gr2.get(key, true));
        assertEquals("get", null, gr2.get(key, false));
        assertEquals("get", null, gr2.getAttributes(key));
        assertEquals("get", null, gr2.getAttributes(key, true));
        assertEquals("get", null, gr2.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr2.isEmpty());
      }

      {
        String key = "gkey2";
        String val = "gval2";
        String group1 = "group1";
        String group2 = "group2";

        CacheGroup gr1 = region.getCacheGroup(group1);
        CacheGroup gr2 = region.getCacheGroup(group2);
        gr1.clear();


        Set keys = facade.keySet();
        Collection values = facade.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 3, keys.size());
        assertEquals("values.size", 3, values.size());
        assertEquals("get", val, facade.get(key));
        assertEquals("get", val, facade.get(key, true));
        assertEquals("get", val, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertFalse("facade.isEmpty()", facade.isEmpty());

        keys = gr1.keySet();
        values = gr1.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 0, keys.size());
        assertEquals("values.size", 0, values.size());
        assertEquals("get", null, gr1.get(key));
        assertEquals("get", null, gr1.get(key, true));
        assertEquals("get", null, gr1.get(key, false));
        assertEquals("get", null, gr1.getAttributes(key));
        assertEquals("get", null, gr1.getAttributes(key, true));
        assertEquals("get", null, gr1.getAttributes(key, false));
        assertTrue("facade.isEmpty()", gr1.isEmpty());

        keys = gr2.keySet();
        values = gr2.values();
        assertTrue("keys.contains(key)", keys.contains(key));
        assertTrue("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 1, keys.size());
        assertEquals("values.size", 1, values.size());
        assertEquals("get", val, gr2.get(key));
        assertEquals("get", val, gr2.get(key, true));
        assertEquals("get", val, gr2.get(key, false));
        assertEquals("get", null, gr2.getAttributes(key));
        assertEquals("get", null, gr2.getAttributes(key, true));
        assertEquals("get", null, gr2.getAttributes(key, false));
        assertFalse("facade.isEmpty()", gr2.isEmpty());
      }
      {
        String key = "gkey2";
        String val = "gval2";
        String group1 = "group1";
        String group2 = "group2";

        CacheGroup gr1 = region.getCacheGroup(group1);
        CacheGroup gr2 = region.getCacheGroup(group2);
        facade.clear();

        Set keys = facade.keySet();
        Collection values = facade.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 0, keys.size());
        assertEquals("values.size", 0, values.size());
        assertEquals("get", null, facade.get(key));
        assertEquals("get", null, facade.get(key, true));
        assertEquals("get", null, facade.get(key, false));
        assertEquals("get", null, facade.getAttributes(key));
        assertEquals("get", null, facade.getAttributes(key, true));
        assertEquals("get", null, facade.getAttributes(key, false));
        assertTrue("facade.isEmpty()", facade.isEmpty());

        keys = gr1.keySet();
        values = gr1.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 0, keys.size());
        assertEquals("values.size", 0, values.size());
        assertEquals("get", null, gr1.get(key));
        assertEquals("get", null, gr1.get(key, true));
        assertEquals("get", null, gr1.get(key, false));
        assertEquals("get", null, gr1.getAttributes(key));
        assertEquals("get", null, gr1.getAttributes(key, true));
        assertEquals("get", null, gr1.getAttributes(key, false));
        assertTrue("facade.isEmpty()", gr1.isEmpty());

        keys = gr2.keySet();
        values = gr2.values();
        assertFalse("keys.contains(key)", keys.contains(key));
        assertFalse("values.contains(val)", values.contains(val));
        assertEquals("keys.size", 0, keys.size());
        assertEquals("values.size", 0, values.size());
        assertEquals("get", null, gr2.get(key));
        assertEquals("get", null, gr2.get(key, true));
        assertEquals("get", null, gr2.get(key, false));
        assertEquals("get", null, gr2.getAttributes(key));
        assertEquals("get", null, gr2.getAttributes(key, true));
        assertEquals("get", null, gr2.getAttributes(key, false));
        assertTrue("facade.isEmpty()", gr2.isEmpty());
      }

    }
  }

  @org.junit.Test
  public void test0() throws CacheException {


  }


//  public static void main(String[] args) throws Exception {
//    initialize();
//  }
}
