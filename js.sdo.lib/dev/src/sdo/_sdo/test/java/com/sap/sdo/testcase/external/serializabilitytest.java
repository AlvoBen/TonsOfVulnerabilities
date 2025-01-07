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
package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.NestedMVInner;
import com.sap.sdo.testcase.typefac.NestedMVOuter;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.SimpleIntf1;
import com.sap.sdo.testcase.typefac.SimpleMVIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class SerializabilityTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public SerializabilityTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final String FILE = "serialization.ser";

    private boolean serialize(Object obj) throws Exception {
        File of = new File(FILE);
        ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(of));
        try {
            o.writeObject(obj);
        } catch (Exception nse) {
            nse.printStackTrace();
            return false;
        } finally {
            o.close();
        }
        logger.info("serialization size: " + of.length());
        return true;
    }

    private Object deserialize() throws Exception {
        File of = new File(FILE);
        ObjectInputStream i = new ObjectInputStream(new FileInputStream(of));
        try {
            Object obj = i.readObject();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            i.close();
        }
    }

    @Test
    public void testEmptyOpenTypeSerializable() throws Exception {
        logger.info("testEmptyOpenTypeSerializable");
        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("open",true);
        typeDO.set("name","OpenType");
        typeDO.set("uri","commonj.sdo");
        Type t = _helperContext.getTypeHelper().define(typeDO);
        _helperContext.getTypeHelper().define((DataObject)t);
        DataObject ot = _helperContext.getDataFactory().create(t);
        assertTrue("serialization failed", serialize(ot));
        Object o2 = deserialize();
        assertTrue("deserialization failed", o2 != null);
        assertTrue("not a data object", o2 instanceof DataObject);
        DataObject do2 = (DataObject)o2;
        assertTrue("wrong type " + do2.getType(), t.equals(do2
                        .getType()));
    }

    @Test
    public void testEmptyTypedSerializable() throws Exception {
        logger.info("testEmptyTypedSerializable");
        Type t = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        DataObject ot = _helperContext.getDataFactory().create(t);
        assertTrue("serialization failed", serialize(ot));
        Object o2 = deserialize();
        assertTrue("deserialization failed", o2 != null);
        assertTrue("not a data object", o2 instanceof DataObject);
        assertTrue("not of the same typed interface",
                o2 instanceof SimpleIntf1);
        DataObject do2 = (DataObject)o2;
        assertTrue("wrong type " + do2.getType(), t.equals(do2
                        .getType()));
    }

    @Test
    public void testFilledOpenTypeSerializable() throws Exception {
        logger.info("testFilledOpenTypeSerializable");
        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("open",true);
        typeDO.set("name","OpenType");
        typeDO.set("uri","commonj.sdo");
        Type t = _helperContext.getTypeHelper().define(typeDO);
        DataObject propDO1 = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propDO1.set("name","aprop");
        propDO1.set("type",_helperContext.getTypeHelper().getType(String.class));
        Property property1 = _helperContext.getTypeHelper().defineOpenContentProperty(null, propDO1);
        DataObject propDO2 = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propDO2.set("name","aprop2");
        propDO2.set("type",_helperContext.getTypeHelper().getType(Integer.class));
        DataObject ot = _helperContext.getDataFactory().create(t);
        Property property2 = _helperContext.getTypeHelper().defineOpenContentProperty(null, propDO2);
        ot.set(property1, "hello");
        assertEquals("hello", ot.get("aprop"));
        ot.setInt(property2, 2);
        assertEquals(2, ot.getInt("aprop2"));
        assertTrue("serialization failed", serialize(ot));
        Object o2 = deserialize();
        assertTrue("deserialization failed", o2 != null);
        assertTrue("not a data object", o2 instanceof DataObject);
        DataObject do2 = (DataObject)o2;
        // was
        //super
        //        .assertTrue("wrong type " + do2.getType(), t.equals(do2
        //                .getType()));
        // but that doesn't work, because Type does not implement ReadResolve.
        // but since 2.1 calls for XML serialization anyway...
        assertTrue("wrong type " + do2.getType(), t.getName().equals(do2
                        .getType().getName()));
        assertTrue("wrong propval " + ot.get("aprop"), "hello".equals(ot
                .get("aprop")));
        assertTrue("wrong propval " + ot.get("aprop2"), ot
                .getInt("aprop2") == 2);

    }

    @Test
    public void testFilledTypedSerializable() throws Exception {
        logger.info("testFilledTypedSerializable");
        Type t = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        DataObject ot = _helperContext.getDataFactory().create(t);
        SimpleIntf1 si = (SimpleIntf1)ot;
        si.setData("data");
        si.setGreen(true);
        assertTrue("serialization failed", serialize(ot));
        Object o2 = deserialize();
        assertTrue("deserialization failed", o2 != null);
        assertTrue("not a data object", o2 instanceof DataObject);
        assertTrue("not of the same typed interface",
                o2 instanceof SimpleIntf1);
        DataObject do2 = (DataObject)o2;
        assertTrue("wrong type " + do2.getType(), t.equals(do2
                        .getType()));
        si = (SimpleIntf1)do2;
        assertTrue("wrong propval " + si.getData(), "data".equals(si
                .getData()));
        assertTrue("wrong propval " + si.isGreen(), si.isGreen());
    }

    @Test
    public void testManyValuedTypedEmptySerializable() throws Exception {
        logger.info("testManyValuedTypedEmptySerializable");
        Type t = _helperContext.getTypeHelper().getType(SimpleMVIntf.class);
        DataObject ot = _helperContext.getDataFactory().create(t);
        assertTrue("serialization failed", serialize(ot));
        Object o2 = deserialize();
        assertTrue("deserialization failed", o2 != null);
        assertTrue("not a data object", o2 instanceof DataObject);
        assertTrue("not of the same typed interface",
                o2 instanceof SimpleMVIntf);
        DataObject do2 = (DataObject)o2;
        assertTrue("wrong type " + do2.getType(), t.equals(do2
                        .getType()));
    }

    @Test
    public void testManyValuedTypedFilledSerializable() throws Exception {
        logger.info("testManyValuedTypedFilledSerializable");
        Type t = _helperContext.getTypeHelper().getType(SimpleMVIntf.class);
        DataObject ot = _helperContext.getDataFactory().create(t);
        SimpleMVIntf si = (SimpleMVIntf)ot;
        si.getNames().add("s1");
        si.getNames().add("s2");
        assertTrue("serialization failed", serialize(ot));
        Object o2 = deserialize();
        assertTrue("deserialization failed", o2 != null);
        assertTrue("not a data object", o2 instanceof DataObject);
        assertTrue("not of the same typed interface",
                o2 instanceof SimpleMVIntf);
        DataObject do2 = (DataObject)o2;
        assertTrue("wrong type " + do2.getType(), t.equals(do2
                        .getType()));
        si = (SimpleMVIntf)do2;
        List li = si.getNames();
        assertTrue("wrong propval " + si.getNames(), ((li.size() == 2)
                && ("s1".equals(li.get(0))) && ("s2".equals(li.get(1)))));
    }

    @Test
    public void testNestedTypedFilledSerializable() throws Exception {
        logger.info("testNestedTypedFilledSerializable");
        Type t = _helperContext.getTypeHelper().getType(NestedMVOuter.class);
        DataObject ot = _helperContext.getDataFactory().create(t);
        NestedMVOuter si = (NestedMVOuter)ot;
        NestedMVInner i1 = (NestedMVInner)_helperContext.getDataFactory()
                .create(NestedMVInner.class);
        NestedMVInner i2 = (NestedMVInner)_helperContext.getDataFactory()
                .create(NestedMVInner.class);
        i1.setId("1");
        i2.setId("2");
        si.getInner().add(i1);
        si.getInner().add(i2);
        assertTrue("serialization failed", serialize(ot));
        Object o2 = deserialize();
        assertTrue("deserialization failed", o2 != null);
        assertTrue("not a data object", o2 instanceof DataObject);
        assertTrue("not of the same typed interface",
                o2 instanceof NestedMVOuter);
        DataObject do2 = (DataObject)o2;
        assertSame(_helperContext.getTypeHelper().getType(NestedMVOuter.class),do2.getType());
        assertSame(_helperContext.getTypeHelper().getType(NestedMVInner.class),do2.getProperty("inner").getType());
        assertTrue("wrong type " + do2.getType(), t.equals(do2
                        .getType()));
        si = (NestedMVOuter)do2;
        List li = si.getInner();
        assertEquals("wrong list size " + li, 2, li.size());
        assertTrue("not equal " + i1 + " " + li.get(0),
            _helperContext.getEqualityHelper().equal((DataObject)i1, (DataObject)li.get(0)));
        assertTrue("not equal " + i2 + " " + li.get(1),
            _helperContext.getEqualityHelper().equal((DataObject)i1, (DataObject)li.get(0)));

        for (int i = 0; i < 10; i++) {
            NestedMVInner in = (NestedMVInner)_helperContext.getDataFactory()
                    .create(NestedMVInner.class);
            assertSame(((DataObject)in).getType(),do2.getProperty("inner").getType());
            in.setId("i" + (3 + i));
            si.getInner().add(in);
        }
        logger.info("re-serialize");
        serialize(si);
        o2 = deserialize();
        assertTrue("deserialization failed", o2 != null);
        assertTrue("not a data object", o2 instanceof DataObject);
        assertTrue("not of the same typed interface",
                o2 instanceof NestedMVOuter);
        assertTrue("not of the same typed interface",
                o2 instanceof NestedMVOuter);
        do2 = (DataObject)o2;
        assertTrue("wrong type " + do2.getType(), t.equals(do2
                        .getType()));
        si = (NestedMVOuter)do2;
        li = si.getInner();
        assertTrue("wrong propval " + li, (li.size() == 12));
    }

    @Test
    public void testProjectAfterSerialization() throws Exception {
        DataObject data = _helperContext.getDataFactory().create(SimpleIntf1.class);
        ((SimpleIntf1)data).setName("a name");
        ((SimpleIntf1)data).setGreen(true);
        ((SimpleIntf1)data).setData("some data");

        assertTrue("serialization failed", serialize(data));
        Object o2 = deserialize();
        assertNotNull("deserialization failed", o2);
        assertTrue("not a data object", o2 instanceof DataObject);
        DataObject do2 = (DataObject)o2;
        assertTrue("wrong type " + do2.getType(), data.getType().equals(do2.getType()));
        assertEquals(data.get("name"), do2.get("name"));
        assertEquals(data.get("green"), do2.get("green"));
        assertEquals(data.get("data"), do2.get("data"));

        DataObject root = _helperContext.getDataFactory().create(OpenInterface.class);
        root.setDataObject("data", do2);
        assertSame(root.getDataObject("data"), do2);
    }

    private final static Logger logger = Logger
            .getLogger(SimpleDataObjectTest.class.getName());
}
