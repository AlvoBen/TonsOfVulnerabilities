/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.internal;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.internal.j1.sdo.ICity;
import com.sap.sdo.testcase.internal.j1.sdo.ISequencedCity;

import commonj.sdo.DataObject;
import commonj.sdo.Sequence;

/**
 * @author D042774
 *
 */
public class SDOMemoryFootprintTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public SDOMemoryFootprintTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /**
     *
     */
    private static final int LOOP_COUNT = 1;
    private static final int INSTANCE_COUNT = 1000;

    private MemoryMXBean _memBean = null;

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {

        _memBean = ManagementFactory.getMemoryMXBean();
        // _memBean.setVerbose(true);
    }

    /*
     * @see TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _memBean = null;
    }

    @Test
    public void testSequencedSdo() {
        long heapCount = 0;
        long nonHeapCount = 0;
        long maxHeap = 0;
        long minHeap = Long.MAX_VALUE;
        long maxNonHeap = 0;
        long minNonHeap = Long.MAX_VALUE;
        for (int j = 0; j < LOOP_COUNT; ++j) {
            final List<ISequencedCity> cities = new ArrayList<ISequencedCity>(INSTANCE_COUNT);
            _memBean.gc();
            long initialHeap = _memBean.getHeapMemoryUsage().getUsed();
            long initialNonHeap = _memBean.getNonHeapMemoryUsage().getUsed();
//            DataObject propObj1 = _helperContext.getDataFactory().create("commonj.sdo", "Property");
//            propObj1.set("name", "prop1");
//            propObj1.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
//            Property prop1 = _helperContext.getTypeHelper().defineGlobalProperty(null, propObj1);
//            DataObject propObj2 = _helperContext.getDataFactory().create("commonj.sdo", "Property");
//            propObj2.set("name", "prop1");
//            propObj2.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
//            Property prop2 = _helperContext.getTypeHelper().defineGlobalProperty(null, propObj2);
//            DataObject propObj3 = _helperContext.getDataFactory().create("commonj.sdo", "Property");
//            propObj3.set("name", "prop1");
//            propObj3.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
//            Property prop3 = _helperContext.getTypeHelper().defineGlobalProperty(null, propObj3);
            for (int i = 0; i < INSTANCE_COUNT; i++) {
                final ISequencedCity city = (ISequencedCity)_helperContext.getDataFactory().create(ISequencedCity.class);
                city.setName("name");
                city.setState("state");
                city.setZip("zip");
                city.setStreets(Arrays.asList(new String[]{"eins", "zwei", "drei"}));
//                ((DataObject)city).set("prop1", "prop1");
//                ((DataObject)city).set("prop2", "prop2");
//                ((DataObject)city).set("prop3", "prop3");
//                ((DataObject)city).set(prop1, "prop1");
//                ((DataObject)city).set(prop2, "prop1");
//                ((DataObject)city).set(prop3, "prop1");
                cities.add(city);
            }
            for (ISequencedCity city : cities) {
                city.getName();
                city.getState();
                city.getZip();
                city.getStreets();
                Sequence sequence = ((DataObject)city).getSequence();
//                for (int i = 0; i < 3; i++) {
//                    sequence.add("streets", "vier");
//                }
                sequence.add(1, "streets", "vier");
            }
            long heap = _memBean.getHeapMemoryUsage().getUsed() - initialHeap;
            long nonHeap = _memBean.getNonHeapMemoryUsage().getUsed() - initialNonHeap;
            heapCount += heap;
            nonHeapCount += nonHeap;
            if (heap > maxHeap) {
                maxHeap = heap;
            }
            if (heap < minHeap) {
                minHeap = heap;
            }
            if (nonHeap > maxNonHeap) {
                maxNonHeap = nonHeap;
            }
            if (nonHeap < minNonHeap) {
                minNonHeap = nonHeap;
            }
        }
        System.out.println("\nSequenced SDO");
        System.out.println("heap usage: " + (heapCount / LOOP_COUNT)
            + " min: " + minHeap + " max: " + maxHeap);
        System.out.println("non heap usage: " + (nonHeapCount / LOOP_COUNT)
            + " min: "+ minNonHeap + " max: " + maxNonHeap);
    }

//    @Test
//    public void testSdo() {
//        long heapCount = 0;
//        long nonHeapCount = 0;
//        long maxHeap = 0;
//        long minHeap = Long.MAX_VALUE;
//        long maxNonHeap = 0;
//        long minNonHeap = Long.MAX_VALUE;
//        for (int j = 0; j < LOOP_COUNT; ++j) {
//            final List<ICity> cities = new ArrayList<ICity>(INSTANCE_COUNT);
//            _memBean.gc();
//            long initialHeap = _memBean.getHeapMemoryUsage().getUsed();
//            long initialNonHeap = _memBean.getNonHeapMemoryUsage().getUsed();
//            for (int i = 0; i < INSTANCE_COUNT; i++) {
//                final ICity city = (ICity)_helperContext.getDataFactory().create(ICity.class);
//                city.setName("name");
//                city.setState("state");
//                city.setZip("zip");
//                cities.add(city);
//            }
//            long heap = _memBean.getHeapMemoryUsage().getUsed() - initialHeap;
//            long nonHeap = _memBean.getNonHeapMemoryUsage().getUsed() - initialNonHeap;
//            heapCount += heap;
//            nonHeapCount += nonHeap;
//            if (heap > maxHeap) {
//                maxHeap = heap;
//            }
//            if (heap < minHeap) {
//                minHeap = heap;
//            }
//            if (nonHeap > maxNonHeap) {
//                maxNonHeap = nonHeap;
//            }
//            if (nonHeap < minNonHeap) {
//                minNonHeap = nonHeap;
//            }
//        }
//        System.out.println("\nSDO");
//        System.out.println("heap usage: " + (heapCount / LOOP_COUNT)
//            + " min: " + minHeap + " max: " + maxHeap);
//        System.out.println("non heap usage: " + (nonHeapCount / LOOP_COUNT)
//            + " min: "+ minNonHeap + " max: " + maxNonHeap);
//    }

//    @Test
//    public void testStatic() {
//        long heapCount = 0;
//        long nonHeapCount = 0;
//        long maxHeap = 0;
//        long minHeap = Long.MAX_VALUE;
//        long maxNonHeap = 0;
//        long minNonHeap = Long.MAX_VALUE;
//        for (int j = 0; j < LOOP_COUNT; ++j) {
//            final List<City> cities = new ArrayList<City>(INSTANCE_COUNT);
//            _memBean.gc();
//            long initialHeap = _memBean.getHeapMemoryUsage().getUsed();
//            long initialNonHeap = _memBean.getNonHeapMemoryUsage().getUsed();
//            for (int i = 0; i < INSTANCE_COUNT; i++) {
//                final City city = new City();
//                city.setName("name");
//                city.setState("state");
//                city.setZip("zip");
//                cities.add(city);
//            }
//            long heap = _memBean.getHeapMemoryUsage().getUsed() - initialHeap;
//            long nonHeap = _memBean.getNonHeapMemoryUsage().getUsed() - initialNonHeap;
//            heapCount += heap;
//            nonHeapCount += nonHeap;
//            if (heap > maxHeap) {
//                maxHeap = heap;
//            }
//            if (heap < minHeap) {
//                minHeap = heap;
//            }
//            if (nonHeap > maxNonHeap) {
//                maxNonHeap = nonHeap;
//            }
//            if (nonHeap < minNonHeap) {
//                minNonHeap = nonHeap;
//            }
//        }
//        System.out.println("\nStatic Java");
//        System.out.println("heap usage: " + (heapCount / LOOP_COUNT)
//            + " min: " + minHeap + " max: " + maxHeap);
//        System.out.println("non heap usage: " + (nonHeapCount / LOOP_COUNT)
//            + " min: "+ minNonHeap + " max: " + maxNonHeap);
//    }

    class City implements ICity
    {
        private String name;
        private String state;
        private String zip;

        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }
        /**
         * @param pName The name to set.
         */
        public void setName(String pName) {
            name = pName;
        }
        /**
         * @return Returns the state.
         */
        public String getState() {
            return state;
        }
        /**
         * @param pState The state to set.
         */
        public void setState(String pState) {
            state = pState;
        }
        /**
         * @return Returns the zip.
         */
        public String getZip() {
            return zip;
        }
        /**
         * @param pZip The zip to set.
         */
        public void setZip(String pZip) {
            zip = pZip;
        }
    }
}
