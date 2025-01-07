/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.util.WeakValueHashMap;
import com.sap.sdo.testcase.SdoTestCase;

/**
 * @author D042774
 *
 */
public class WeakValueHashMapTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public WeakValueHashMapTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConstructor() {
        try {
            new WeakValueHashMap<String,String>(-1);
        } catch (IllegalArgumentException ex) {
            assertEquals("Illegal Initial Capacity: -1", ex.getMessage());
        }

        try {
            new WeakValueHashMap<String,String>((1 << 30) + 1, 0f);
        } catch (IllegalArgumentException ex) {
            assertEquals("Illegal Load factor: 0.0", ex.getMessage());
        }

        WeakValueHashMap<String, String> map = new WeakValueHashMap<String,String>(10, 0.5f);
        assertNotNull(map);
        assertEquals(0, map.size());

        map = new WeakValueHashMap<String,String>(10);
        assertNotNull(map);
        assertEquals(0, map.size());

        map = new WeakValueHashMap<String,String>(map);
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    public void testFunctionality() {
        WeakValueHashMap<MyKey,MyValue> map = new WeakValueHashMap<MyKey,MyValue>();
        assertNotNull(map);
        assertEquals(0, map.size());

        MyKey key = new MyKey("key");
        MyValue value = new MyValue("value");
        assertEquals(false, map.containsKey(key));
        assertEquals(false, map.containsKey(null));
        assertEquals(false, map.containsValue(value));
        assertEquals(false, map.containsValue(null));

        map.put(key, value);
        assertSame(value, map.get(key));
        for (Entry<MyKey,MyValue> entry : map.entrySet()) {
            assertSame(key, entry.getKey());
            assertSame(value, entry.getValue());
        }
        assertEquals(false, map.isEmpty());
        value = null;

        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {}

        assertEquals(true, map.isEmpty());

        assertEquals(null, map.get(key));
        assertEquals(false, map.containsKey(key));
        assertEquals(0, map.size());

        MyKey a = new MyKey("a");
        MyKey a2 = new MyKey("a");
        MyValue myValue = new MyValue("my value");
        MyValue otherValue = new MyValue("other");
        map.put(a, myValue);
        map.put(a2, myValue);
        map.put(null, otherValue);
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(a));
        assertEquals(true, map.containsValue(myValue));
        assertEquals(myValue, map.get(a));
        assertEquals(true, map.containsKey(a2));
        assertEquals(true, map.containsValue(myValue));
        assertEquals(myValue, map.get(a2));
        assertEquals(true, map.containsKey(null));
        assertEquals(true, map.containsValue(otherValue));
        assertEquals(otherValue, map.get(null));

        map.put(a, myValue);
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(a));
        assertEquals(true, map.containsValue(myValue));
        assertEquals(myValue, map.get(a));

        map.put(a, otherValue);
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(a));
        assertEquals(true, map.containsValue(otherValue));
        assertEquals(otherValue, map.get(a));

        map.put(a2, otherValue);
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(a2));
        assertEquals(true, map.containsValue(otherValue));
        assertEquals(otherValue, map.get(a2));

        map.put(null, null);
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(null));
        assertEquals(true, map.containsValue(null));
        assertEquals(null, map.get(null));

        WeakValueHashMap<MyKey, MyValue> newMap = new WeakValueHashMap<MyKey, MyValue>(map);
        assertEquals(3, newMap.size());
        assertSame(map.get(a), newMap.get(a));
        assertSame(map.get(a2), newMap.get(a2));
        assertSame(map.get(null), newMap.get(null));

        WeakValueHashMap<MyKey, MyValue> newMap2 = new WeakValueHashMap<MyKey, MyValue>(1, 0.1f);
        newMap2.putAll(map);
        assertEquals(3, newMap2.size());
        assertSame(map.get(a), newMap2.get(a));
        assertSame(map.get(a2), newMap2.get(a2));
        assertSame(map.get(null), newMap2.get(null));

        map.clear();
        assertEquals(true, map.isEmpty());
    }

    @Test
    public void testEntrySet() {
        WeakValueHashMap<MyKey,MyValue> map = new WeakValueHashMap<MyKey,MyValue>();
        MyKey a = new MyKey("a");
        MyKey a2 = new MyKey("a");
        MyValue myValue = new MyValue("my value");
        MyValue otherValue = new MyValue("other");
        map.put(a, myValue);
        map.put(a2, myValue);
        map.put(null, otherValue);
        assertEquals(3, map.size());

        Set<Entry<MyKey, MyValue>> entries = map.entrySet();

        assertEquals(3, entries.size());

        Iterator<Entry<MyKey, MyValue>> iterator = entries.iterator();
        assertNotNull(iterator);
        assertEquals(true, iterator.hasNext());
        Entry<MyKey, MyValue> last = null;
        while (iterator.hasNext()) {
            Entry<MyKey, MyValue> entry = iterator.next();
            assertEquals(true, entries.contains(entry));
            assertEquals(entry.getKey() + "=" + entry.getValue(), entry.toString());
            if (last != null) {
                assertEquals(false, last.equals(entry));
            }
            assertSame(entry.getValue(), entry.setValue(entry.getValue()));
            try {
                entry.setValue(new MyValue("test"));
                fail("UnsupportedOperationException expected");
            } catch (UnsupportedOperationException ex) {
                assertEquals("WeakValueHashMap.setValue(V newValue)", ex.getMessage());
            }
            assertEquals(entry, entry);
            assertEquals(false, entry.equals(null));
            assertEquals(false, entry.equals(new Object()));
            assertNotNull(entry.hashCode());
            last = entry;
        }
        try {
            iterator.next();
            fail("NoSuchElementException expected");
        } catch (NoSuchElementException ex) {
            assertEquals(null, ex.getMessage());
        }
        assertEquals(false, entries.contains(null));

        Entry<MyKey,MyValue> e = new Entry<MyKey,MyValue>() {

            public MyKey getKey() {
                return null;
            }

            public MyValue getValue() {
                return null;
            }

            public MyValue setValue(MyValue pValue) {
                return null;
            }

        };

        assertEquals(false, entries.remove(e));

        for (int i=0; i<3; ++i) {
            Iterator<Entry<MyKey, MyValue>> it = entries.iterator();
            assertEquals(true, entries.remove(it.next()));
            try {
                it.next();
                fail("ConcurrentModificationException expected");
            } catch (ConcurrentModificationException ex) {
                assertEquals(null, ex.getMessage());
            }
            try {
                it.remove();
                fail("ConcurrentModificationException expected");
            } catch (ConcurrentModificationException ex) {
                assertEquals(null, ex.getMessage());
            }
        }
        assertEquals(0, entries.size());

        map.put(a, myValue);
        map.put(a2, myValue);
        map.put(null, otherValue);
        for (Iterator<Entry<MyKey, MyValue>> it = entries.iterator(); it.hasNext();) {
            it.next();
            it.remove();
            try {
                it.remove();
                fail("IllegalStateException expected");
            } catch (IllegalStateException ex) {
                assertEquals(null, ex.getMessage());
            }
        }
        assertEquals(0, entries.size());

        map.put(a, myValue);
        map.put(a2, myValue);
        map.put(null, otherValue);
        entries.removeAll(entries);
        assertEquals(0, entries.size());

        map.put(a, myValue);
        map.put(a2, myValue);
        map.put(null, otherValue);
        entries.clear();
        assertEquals(0, entries.size());
    }

    @Test
    public void testSimpleEntry() {
        WeakValueHashMap<MyKey,MyValue> map = new WeakValueHashMap<MyKey,MyValue>();
        MyKey a = new MyKey("a");
        MyKey a2 = new MyKey("a");
        MyValue myValue = new MyValue("my value");
        MyValue otherValue = new MyValue("other");
        map.put(a, myValue);
        map.put(a2, myValue);
        map.put(null, otherValue);
        assertEquals(3, map.size());

        Set<Entry<MyKey, MyValue>> entries = map.entrySet();
        assertEquals(3, entries.size());

        Object[] array = entries.toArray();
        assertEquals(3, array.length);
        for (Object obj : array) {
            assertEquals(true, obj instanceof Entry);
            Entry entry = (Entry)obj;
            assertEquals(entry.getValue(), map.get(entry.getKey()));
        }

        Entry<MyKey,MyValue>[] entryArray = new Entry[3];
        entries.toArray(entryArray);
        assertEquals(3, entryArray.length);
        for (Entry<MyKey,MyValue> entry : entryArray) {
            assertEquals(entry.getValue(), map.get(entry.getKey()));
            assertEquals(false, entry.equals(null));
            assertTrue(entry.hashCode() > 0);
        }

        Entry<MyKey,MyValue> entry = entryArray[0];
        assertNotNull(entry);
        MyValue oldValue = entry.getValue();
        assertEquals(oldValue, map.get(entry.getKey()));
        assertEquals(true, map.entrySet().contains(entry));
        assertEquals(true, entry.toString().length() > 0);
        Entry<MyKey,MyValue> mapEntry = null;
        for (Entry<MyKey,MyValue> e : map.entrySet()) {
            if (entry.equals(e)) {
                assertNull(mapEntry);
                mapEntry = e;
            }
        }
        assertNotNull(mapEntry);
        assertEquals(entry.hashCode(), mapEntry.hashCode());

        MyValue newValue = new MyValue("test");
        entry.setValue(newValue);
        assertEquals(oldValue, mapEntry.getValue());
        assertEquals(newValue, entry.getValue());
    }

    @Test
    public void testKeySet() {
        WeakValueHashMap<MyKey,MyValue> map = new WeakValueHashMap<MyKey,MyValue>();
        MyKey a = new MyKey("a");
        MyKey a2 = new MyKey("a");
        MyValue myValue = new MyValue("my value");
        MyValue otherValue = new MyValue("other");
        map.put(a, myValue);
        map.put(a2, myValue);
        map.put(null, otherValue);
        assertEquals(3, map.size());

        Set<MyKey> keys = map.keySet();
        assertEquals(3, keys.size());
        assertSame(keys, map.keySet());

        Object[] keyArray = keys.toArray();
        assertEquals(3, keyArray.length);
        for (Object object : keyArray) {
            if (object != null) {
                assertEquals(true, object instanceof MyKey);
            }
        }

        MyKey[] myKeyArray = new MyKey[3];
        keys.toArray(myKeyArray);

        Iterator<MyKey> it = keys.iterator();
        MyKey key = it.next();
        if (key == null && it.hasNext()) {
            key = it.next();
        }
        assertNotNull(key);

        assertEquals(true, keys.contains(key));
        assertEquals(true, keys.contains(null));
        assertEquals(false, keys.contains(new MyKey("test")));


        assertEquals(false, keys.remove(new MyKey("test")));
        assertEquals(true, keys.remove(key));
        assertEquals(2, keys.size());
        assertEquals(false, keys.contains(key));
        assertEquals(true, keys.remove(null));
        assertEquals(1, keys.size());
        assertEquals(false, keys.contains(null));

        keys.clear();
        assertEquals(0, keys.size());
        assertEquals(0, map.size());
    }

    @Test
    public void testValues() {
        WeakValueHashMap<MyKey,MyValue> map = new WeakValueHashMap<MyKey,MyValue>();
        MyKey a = new MyKey("a");
        MyKey a2 = new MyKey("a");
        MyValue myValue = new MyValue("my value");
        MyValue otherValue = new MyValue("other");
        map.put(a, myValue);
        map.put(a2, myValue);
        map.put(null, otherValue);
        assertEquals(3, map.size());

        Collection<MyValue> values = map.values();
        assertEquals(3, values.size());
        assertSame(values, map.values());

        Object[] valueArray = values.toArray();
        assertEquals(3, valueArray.length);
        for (Object object : valueArray) {
            assertEquals(true, object instanceof MyValue);
        }

        MyValue[] myValueArray = new MyValue[3];
        values.toArray(myValueArray);

        MyValue value = values.iterator().next();
        assertNotNull(value);

        assertEquals(true, values.contains(value));
        assertEquals(false, values.contains(null));
        assertEquals(false, values.contains(new MyValue("test")));

        values.clear();
        assertEquals(0, values.size());
        assertEquals(0, map.size());
    }

    private static class MyValue {
        String _value;

        MyValue(String pValue) {
            _value = pValue;
        }

        @Override
        public int hashCode() {
            return _value.hashCode();
        }
    }

    private static class MyKey {
        String _key;

        MyKey(String pKey) {
            _key = pKey;
        }

        @Override
        public int hashCode() {
            return _key.hashCode();
        }
    }
}
