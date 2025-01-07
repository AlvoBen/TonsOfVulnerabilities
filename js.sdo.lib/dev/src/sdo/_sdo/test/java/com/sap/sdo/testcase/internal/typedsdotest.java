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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.java.JavaTypeFactory;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleIntf1;
import com.sap.sdo.testcase.typefac.SimpleMVIntf;
import com.sap.sdo.testcase.typefac.SimpleMappedIntf;
import com.sap.sdo.testcase.typefac.SimpleTypesIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class TypedSDOTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public TypedSDOTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testTypeFromInterfaceByUNP() throws Exception {
        JavaTypeFactory javaTypeFactory = ((TypeHelperImpl)_helperContext.getTypeHelper()).getJavaTypeFactory();
        URINamePair unp = javaTypeFactory.getQNameFromClass(
                SimpleIntf1.class);
        Class clz = javaTypeFactory.getClassByQName(unp,null,null);
        assertTrue("failed to retrieve interface by UNP: " + clz,
                SimpleIntf1.class.equals(clz));
    }

    @Test
    public void testTypeFromInterface() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        assertTrue("type was not provided", t != null);
        // list meta-data for interest
        List props = t.getProperties();
        for (Iterator it = props.iterator(); it.hasNext();) {
            Property p = (Property)it.next();
            System.err.println("Property: " + p.getName()
                    + "\nContaining Type: " + p.getContainingType()
                    + "\ndefault value: " + p.getDefault() + "\nOpposite: "
                    + p.getOpposite() + "\nType: " + p.getType()
                    + "\nisContainment: " + p.isContainment() + "\nisMany: "
                    + p.isMany() + "\nisReadOnly: " + p.isReadOnly());
        }

        Type str = _helperContext.getTypeHelper().getType(URINamePair.STRING.getURI(),
                URINamePair.STRING.getName());
        Type bol = _helperContext.getTypeHelper().getType(URINamePair.BOOLEAN.getURI(),
                URINamePair.BOOLEAN.getName());
        Property pName = t.getProperty("name");
        Property pId = t.getProperty("id");
        assertTrue("missing property name", pName != null);
        assertTrue("missing property id", pId != null);
        assertTrue("name should be of type common.jdo:string", str
                .equals(pName.getType()));
        assertTrue("name should be readwrite", !pName.isReadOnly());
        assertTrue("name should have no default",
                pName.getDefault() == null);
        assertTrue("name should be single valued", !pName.isMany());
        assertTrue("id should be readonly", pId.isReadOnly());
        /*assertTrue("id should have default \"an id\"", "an id".equals(pId
                .getDefault()));*/
        assertTrue("id should be single valued", !pId.isMany());
        Property gr = t.getProperty("green");
        Property bl = t.getProperty("blue");
        assertTrue("missing property green", gr != null);
        assertTrue("missing property blue", bl != null);
        assertTrue("green should be of type common.jdo:boolean", bol
                .equals(gr.getType()));
        assertTrue("blue should be of type common.jdo:boolean", bol
                .equals(bl.getType()));
        assertTrue("green should be readwrite", !gr.isReadOnly());
        // assertTrue("blue should be readonly",bl.isReadOnly());
        /*assertTrue("blue should have default true",
                (bl.getDefault() instanceof Boolean)
                        && ((Boolean)bl.getDefault()).booleanValue());*/
        assertTrue("green should be single-value", !gr.isMany());
        assertTrue("blue should be single-value", !bl.isMany());
    }

    @Test
    public void testMultiValueProperty() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleMVIntf.class);
        assertTrue("type was not provided", t != null);
        Property p = t.getProperty("names");
        assertTrue("property was not provided", p != null);
        assertTrue("property \"names\" should be many-value", p.isMany());
        // assertTrue("property \"names\" should be
        // read-only",p.isReadOnly());
    }

    @Test
    public void testSimpleTypeDetection() {
        Type t = _helperContext.getTypeHelper().getType(SimpleTypesIntf.class);
        Property p = t.getProperty("booleanObject");
        assertTrue("property booleanObject not found", p != null);
        assertTrue("property booleanObject incorrect type:" + p.getType(),
                p.getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo/java",
                                "BooleanObject")));
        p = t.getProperty("boolean");
        assertTrue("property boolean not found", p != null);
        assertTrue("property boolean incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Boolean")));
        p = t.getProperty("byteObject");
        assertTrue("property byteObject not found", p != null);
        assertTrue("property byteObject incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo/java",
                                "ByteObject")));
        p = t.getProperty("byte");
        assertTrue("property byte not found", p != null);
        assertTrue("property byte incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Byte")));
        p = t.getProperty("bytes");
        assertTrue("property bytes not found", p != null);
        assertTrue("property bytes incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Bytes")));
        p = t.getProperty("characterObject");
        assertTrue("property characterObject not found", p != null);
        assertTrue("property characterObject incorrect type:"
                + p.getType(), p.getType().equals(
                _helperContext.getTypeHelper().getType("commonj.sdo/java",
                        "CharacterObject")));
        p = t.getProperty("character");
        assertTrue("property character not found", p != null);
        assertTrue("property character incorrect type:" + p.getType(),
                p.getType()
                        .equals(
                                _helperContext.getTypeHelper().getType("commonj.sdo",
                                        "Character")));
        p = t.getProperty("date");
        assertTrue("property date not found", p != null);
        assertTrue("property date incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Date")));
        p = t.getProperty("day");
        assertTrue("property day not found", p != null);
        assertTrue("property day has no type", p.getType() != null);
        assertTrue("property day incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Day")));
        p = t.getProperty("decimal");
        assertTrue("property decimal not found", p != null);
        assertTrue("property decimal incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Decimal")));
        p = t.getProperty("doubleObject");
        assertTrue("property doubleObject not found", p != null);
        assertTrue("property doubleObject incorrect type:" + p.getType(),
                p.getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo/java",
                                "DoubleObject")));
        p = t.getProperty("double");
        assertTrue("property double not found", p != null);
        assertTrue("property double incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Double")));
        p = t.getProperty("duration");
        assertTrue("property duration not found", p != null);
        assertTrue("property duration incorrect type:" + p.getType(), p
                        .getType().equals(
                                _helperContext.getTypeHelper().getType("commonj.sdo",
                                        "Duration")));
        p = t.getProperty("floatObject");
        assertTrue("property floatObject not found", p != null);
        assertTrue("property floatObject incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo/java",
                                "FloatObject")));
        p = t.getProperty("float");
        assertTrue("property float not found", p != null);
        assertTrue("property float incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Float")));
        p = t.getProperty("intObject");
        assertTrue("property intObject not found", p != null);
        assertTrue("property intObject incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo/java",
                                "IntObject")));
        p = t.getProperty("int");
        assertTrue("property int not found", p != null);
        assertTrue("property int incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Int")));
        p = t.getProperty("integer");
        assertTrue("property integer not found", p != null);
        assertTrue("property integer incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Integer")));
        p = t.getProperty("longObject");
        assertTrue("property longObject not found", p != null);
        assertTrue("property longObject incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo/java",
                                "LongObject")));
        p = t.getProperty("long");
        assertTrue("property long not found", p != null);
        assertTrue("property long incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Long")));
        p = t.getProperty("month");
        assertTrue("property month not found", p != null);
        assertTrue("property month incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Month")));
        p = t.getProperty("monthDay");
        assertTrue("property monthDay not found", p != null);
        assertTrue("property monthDay incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "MonthDay")));
        p = t.getProperty("object");
        assertTrue("property object not found", p != null);
        assertTrue("property object incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Object")));
        p = t.getProperty("shortObject");
        assertTrue("property shortObject not found", p != null);
        assertTrue("property shortObject incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo/java",
                                "ShortObject")));
        p = t.getProperty("short");
        assertTrue("property short not found", p != null);
        assertTrue("property short incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Short")));
        p = t.getProperty("string");
        assertTrue("property string not found", p != null);
        assertTrue("property string incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "String")));
        p = t.getProperty("stringMany");
        assertTrue("property strings not found", p != null);
        assertTrue("property strings incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "String")));
        assertEquals(p.getName() + " many", true, p.isMany());
        p = t.getProperty("stringsAttr");
        assertTrue("property strings not found", p != null);
        assertTrue("property strings incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Strings")));
        assertEquals(p.getName() + " many", false, p.isMany());
        p = t.getProperty("strings");
        assertTrue("property strings not found", p != null);
        assertTrue("property strings incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Strings")));
        assertEquals(p.getName() + " many", false, p.isMany());
        p = t.getProperty("time");
        assertTrue("property time not found", p != null);
        assertTrue("property time incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Time")));
        p = t.getProperty("uRI");
        assertTrue("property uRI not found", p != null);
        assertTrue("property uRI incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "URI")));
        p = t.getProperty("year");
        assertTrue("property year not found", p != null);
        assertTrue("property year incorrect type:" + p.getType(), p
                .getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo", "Year")));
        p = t.getProperty("yearMonth");
        assertTrue("property yearMonth not found", p != null);
        assertTrue("property yearMonth incorrect type:" + p.getType(),
                p.getType()
                        .equals(
                                _helperContext.getTypeHelper().getType("commonj.sdo",
                                        "YearMonth")));
        p = t.getProperty("yearMonthDay");
        assertTrue("property yearMonthDay not found", p != null);
        assertTrue("property yearMonthDay incorrect type:" + p.getType(),
                p.getType().equals(
                        _helperContext.getTypeHelper().getType("commonj.sdo",
                                "YearMonthDay")));

    }

    @Test
    public void testSDONameMappingOnInterface()
    {
        Type t = _helperContext.getTypeHelper().getType(SimpleMappedIntf.class);
        assertTrue("Type Helper did not provide interface type",
                t != null);
        Property p = t.getProperty("xyz");
        assertTrue("Type does not have property xyz", p != null);

        // testing property mapping
        DataObject d = _helperContext.getDataFactory().create(t);
        assertTrue("Data Object does not implement JAVA interface",d instanceof SimpleMappedIntf);
        d.set("xyz","test123");
        String x = ((SimpleMappedIntf) d).getData();
        assertTrue("Getter does not match property ["+x+"]","test123".equals(x));
        ((SimpleMappedIntf)d).setData("test456");
        String y = (String) d.get("xyz");
        assertEquals("Getter does not match property","test456", y);
    }

}
