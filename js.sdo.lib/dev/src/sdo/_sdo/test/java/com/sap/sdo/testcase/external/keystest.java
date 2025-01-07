package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.util.DataObjectBehavior;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class KeysTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public KeysTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testDefinitionByApi1() throws Exception {
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type stringType = typeHelper.getType(
            URINamePair.STRING.getURI(), URINamePair.STRING.getName());

        DataObject carTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        carTypeObject.set(TypeConstants.NAME, "Car");
        carTypeObject.set(TypeConstants.URI, "com.sap.test");
        carTypeObject.set(TypeConstants.KEY_TYPE, stringType);

        DataObject propObject = carTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Owner");
        propObject.set(PropertyConstants.TYPE, stringType);

        propObject = carTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "LicensePlate");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.KEY, true);

        DataObject companyCarTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        companyCarTypeObject.set(TypeConstants.NAME, "CompanyCar");
        companyCarTypeObject.set(TypeConstants.URI, "com.sap.test");
        companyCarTypeObject.getList(TypeConstants.BASE_TYPE).add(carTypeObject);

        propObject = companyCarTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Company");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject companyCarContainerTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        companyCarContainerTypeObject.set(TypeConstants.NAME, "CompanyCarContainer");
        companyCarContainerTypeObject.set(TypeConstants.URI, "com.sap.test");

        propObject = companyCarContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Car");
        propObject.set(PropertyConstants.TYPE, companyCarTypeObject);
        propObject.set(PropertyType.CONTAINMENT, true);

        propObject = companyCarContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "CarRef");
        propObject.set(PropertyConstants.TYPE, companyCarTypeObject);
        propObject.set(PropertyType.CONTAINMENT, false);

        SdoType t = (SdoType)typeHelper.define(companyCarTypeObject);
        SdoType c = (SdoType)typeHelper.define(companyCarContainerTypeObject);

        SdoType baseTypeWithKey = t.getTypeForKeyUniqueness();
        assertSame(t.getBaseTypes().get(0), baseTypeWithKey);
        assertSame(stringType, baseTypeWithKey.getKeyType());

        List<SdoProperty> keyProps = baseTypeWithKey.getKeyProperties();
        assertEquals(1, keyProps.size());
        assertEquals("LicensePlate", keyProps.get(0).getName());
        assertEquals(true, keyProps.get(0).isKey());

        String schema = _helperContext.getXSDHelper().generate(Collections.singletonList(c));
        System.out.println(schema);

        DataObject companyCarContainer = _helperContext.getDataFactory().create(c);
        DataObject companyCar = companyCarContainer.createDataObject("Car");
        companyCarContainer.setDataObject("CarRef", companyCar);
        companyCar.setString("Owner", "Max Mustermann");
        companyCar.setString("LicensePlate", "HD-MM 1234");
        companyCar.setString("Company", "SAP AG");

        Object key = DataObjectBehavior.getKey(companyCar);

        assertEquals("HD-MM 1234", key);

        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        String xmlOut1 = xmlHelper.save(companyCarContainer, c.getURI(), c.getName());
        System.out.println(xmlOut1);
        XMLDocument document = xmlHelper.load(xmlOut1);
        StringWriter xmlOut2 = new StringWriter();
        xmlHelper.save(document, xmlOut2, null);
        assertLineEquality(xmlOut1, xmlOut2.toString());

        DataObject container = document.getRootObject();
        assertSame(container.get("CarRef"), container.get("Car"));

        compareReaders(document);
    }

    @Test
    public void testDefinitionByApi2() throws Exception {
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type stringType = typeHelper.getType(
            URINamePair.STRING.getURI(), URINamePair.STRING.getName());

        DataObject carTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        carTypeObject.set(TypeConstants.NAME, "Car");
        carTypeObject.set(TypeConstants.URI, "com.sap.test");

        DataObject propObject = carTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Owner");
        propObject.set(PropertyConstants.TYPE, stringType);

        propObject = carTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "LicensePlate");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.CONTAINMENT, false);
        propObject.set(PropertyType.KEY, true);

        DataObject companyCarTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        companyCarTypeObject.set(TypeConstants.NAME, "CompanyCar");
        companyCarTypeObject.set(TypeConstants.URI, "com.sap.test");
        companyCarTypeObject.getList(TypeConstants.BASE_TYPE).add(carTypeObject);

        propObject = companyCarTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Company");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject companyCarContainerTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        companyCarContainerTypeObject.set(TypeConstants.NAME, "CompanyCarContainer");
        companyCarContainerTypeObject.set(TypeConstants.URI, "com.sap.test");

        propObject = companyCarContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Car");
        propObject.set(PropertyConstants.TYPE, companyCarTypeObject);
        propObject.set(PropertyType.CONTAINMENT, true);

        propObject = companyCarContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "CarRef");
        propObject.set(PropertyConstants.TYPE, companyCarTypeObject);
        propObject.set(PropertyType.CONTAINMENT, false);

        SdoType t = (SdoType)typeHelper.define(companyCarTypeObject);
        SdoType c = (SdoType)typeHelper.define(companyCarContainerTypeObject);

        SdoType baseTypeWithKey = t.getTypeForKeyUniqueness();
        assertSame(t.getBaseTypes().get(0), baseTypeWithKey);
        assertSame(stringType, baseTypeWithKey.getKeyType());

        List<SdoProperty> keyProps = baseTypeWithKey.getKeyProperties();
        assertEquals(1, keyProps.size());
        assertEquals("LicensePlate", keyProps.get(0).getName());
        assertEquals(true, keyProps.get(0).isKey());

        String schema = _helperContext.getXSDHelper().generate(Collections.singletonList(c));
        System.out.println(schema);

        DataObject companyCarContainer = _helperContext.getDataFactory().create(c);
        DataObject companyCar = companyCarContainer.createDataObject("Car");
        companyCarContainer.setDataObject("CarRef", companyCar);
        companyCar.setString("Owner", "Max Mustermann");
        companyCar.setString("LicensePlate", "HD-MM 1234");
        companyCar.setString("Company", "SAP AG");

        Object key = DataObjectBehavior.getKey(companyCar);

        assertEquals("HD-MM 1234", key);

        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        String xmlOut1 = xmlHelper.save(companyCarContainer, c.getURI(), c.getName());
        System.out.println(xmlOut1);
        XMLDocument document = xmlHelper.load(xmlOut1);
        StringWriter xmlOut2 = new StringWriter();
        xmlHelper.save(document, xmlOut2, null);
        assertLineEquality(xmlOut1, xmlOut2.toString());

        DataObject container = document.getRootObject();
        assertSame(container.get("CarRef"), container.get("Car"));

        compareReaders(document);
    }

    @Test
    public void testDefinitionByApi3() throws Exception {
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type stringType = typeHelper.getType(
            URINamePair.STRING.getURI(), URINamePair.STRING.getName());

        DataObject licensePlateTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        licensePlateTypeObject.set(TypeConstants.NAME, "LicensePlate");
        licensePlateTypeObject.set(TypeConstants.URI, "com.sap.test");

        DataObject propObject = licensePlateTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "id");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject carTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        carTypeObject.set(TypeConstants.NAME, "Car");
        carTypeObject.set(TypeConstants.URI, "com.sap.test");

        propObject = carTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Owner");
        propObject.set(PropertyConstants.TYPE, stringType);

        propObject = carTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "LicensePlate");
        propObject.set(PropertyConstants.TYPE, licensePlateTypeObject);
        propObject.set(PropertyConstants.CONTAINMENT, true);
        propObject.set(PropertyType.KEY, true);

        DataObject companyCarTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        companyCarTypeObject.set(TypeConstants.NAME, "CompanyCar");
        companyCarTypeObject.set(TypeConstants.URI, "com.sap.test");
        companyCarTypeObject.getList(TypeConstants.BASE_TYPE).add(carTypeObject);

        propObject = companyCarTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Company");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject companyCarContainerTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        companyCarContainerTypeObject.set(TypeConstants.NAME, "CompanyCarContainer");
        companyCarContainerTypeObject.set(TypeConstants.URI, "com.sap.test");

        propObject = companyCarContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Car");
        propObject.set(PropertyConstants.TYPE, companyCarTypeObject);
        propObject.set(PropertyType.CONTAINMENT, true);

        propObject = companyCarContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "CarRef");
        propObject.set(PropertyConstants.TYPE, companyCarTypeObject);
        propObject.set(PropertyType.CONTAINMENT, false);

        SdoType t = (SdoType)typeHelper.define(companyCarTypeObject);
        SdoType c = (SdoType)typeHelper.define(companyCarContainerTypeObject);

        SdoType baseTypeWithKey = t.getTypeForKeyUniqueness();
        assertSame(t.getBaseTypes().get(0), baseTypeWithKey);
        assertSame(licensePlateTypeObject, baseTypeWithKey.getKeyType());

        List<SdoProperty> keyProps = baseTypeWithKey.getKeyProperties();
        assertEquals(1, keyProps.size());
        assertEquals("LicensePlate", keyProps.get(0).getName());
        assertEquals(true, keyProps.get(0).isKey());

        String schema = _helperContext.getXSDHelper().generate(Collections.singletonList(c));
        System.out.println(schema);

        DataObject companyCarContainer = _helperContext.getDataFactory().create(c);
        DataObject companyCar = companyCarContainer.createDataObject("Car");
        companyCarContainer.setDataObject("CarRef", companyCar);
        companyCar.setString("Owner", "Max Mustermann");
        DataObject licensePlate = companyCar.createDataObject("LicensePlate");
        licensePlate.setString("id", "HD-MM 1234");
        companyCar.setString("Company", "SAP AG");

        Object key = DataObjectBehavior.getKey(companyCar);

        DataObject keyDo = (DataObject)key;
        assertEquals("HD-MM 1234", keyDo.getString("id"));
        assertEquals("LicensePlate", keyDo.getType().getName());

        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        String xmlOut1 = xmlHelper.save(companyCarContainer, c.getURI(), c.getName());
        System.out.println(xmlOut1);
        XMLDocument document = xmlHelper.load(xmlOut1);
        StringWriter xmlOut2 = new StringWriter();
        xmlHelper.save(document, xmlOut2, null);
        assertLineEquality(xmlOut1, xmlOut2.toString());

        DataObject container = document.getRootObject();
        assertSame(container.get("CarRef"), container.get("Car"));

        compareReaders(document);
    }

    @Test
    public void testDefinitionByApi4() throws Exception {
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type stringType = typeHelper.getType(
            URINamePair.STRING.getURI(), URINamePair.STRING.getName());

        DataObject licensePlateTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        licensePlateTypeObject.set(TypeConstants.NAME, "LicensePlate");
        licensePlateTypeObject.set(TypeConstants.URI, "com.sap.test");

        DataObject propObject = licensePlateTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "id");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.KEY, true);

        DataObject carTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        carTypeObject.set(TypeConstants.NAME, "Car");
        carTypeObject.set(TypeConstants.URI, "com.sap.test");
        carTypeObject.set(TypeConstants.KEY_TYPE, stringType);

        propObject = carTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Owner");
        propObject.set(PropertyConstants.TYPE, stringType);

        propObject = carTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "LicensePlate");
        propObject.set(PropertyConstants.TYPE, licensePlateTypeObject);
        propObject.set(PropertyConstants.CONTAINMENT, true);
        propObject.set(PropertyType.KEY, true);

        DataObject companyCarTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        companyCarTypeObject.set(TypeConstants.NAME, "CompanyCar");
        companyCarTypeObject.set(TypeConstants.URI, "com.sap.test");
        companyCarTypeObject.getList(TypeConstants.BASE_TYPE).add(carTypeObject);

        propObject = companyCarTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Company");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject companyCarContainerTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        companyCarContainerTypeObject.set(TypeConstants.NAME, "CompanyCarContainer");
        companyCarContainerTypeObject.set(TypeConstants.URI, "com.sap.test");

        propObject = companyCarContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Car");
        propObject.set(PropertyConstants.TYPE, companyCarTypeObject);
        propObject.set(PropertyType.CONTAINMENT, true);

        propObject = companyCarContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "CarRef");
        propObject.set(PropertyConstants.TYPE, companyCarTypeObject);
        propObject.set(PropertyType.CONTAINMENT, false);

        SdoType t = (SdoType)typeHelper.define(companyCarTypeObject);
        SdoType c = (SdoType)typeHelper.define(companyCarContainerTypeObject);

        SdoType baseTypeWithKey = t.getTypeForKeyUniqueness();
        assertSame(t.getBaseTypes().get(0), baseTypeWithKey);
        assertSame(stringType, baseTypeWithKey.getKeyType());

        List<SdoProperty> keyProps = baseTypeWithKey.getKeyProperties();
        assertEquals(1, keyProps.size());
        assertEquals("LicensePlate", keyProps.get(0).getName());
        assertEquals(true, keyProps.get(0).isKey());

        String schema = _helperContext.getXSDHelper().generate(Collections.singletonList(c));
        System.out.println(schema);

        DataObject companyCarContainer = _helperContext.getDataFactory().create(c);
        DataObject companyCar = companyCarContainer.createDataObject("Car");
        companyCarContainer.setDataObject("CarRef", companyCar);
        companyCar.setString("Owner", "Max Mustermann");
        DataObject licensePlate = companyCar.createDataObject("LicensePlate");
        licensePlate.setString("id", "HD-MM 1234");
        companyCar.setString("Company", "SAP AG");

        Object key = DataObjectBehavior.getKey(companyCar);

        assertEquals("HD-MM 1234", key);

        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        String xmlOut1 = xmlHelper.save(companyCarContainer, c.getURI(), c.getName());
        System.out.println(xmlOut1);
        XMLDocument document = xmlHelper.load(xmlOut1);
        StringWriter xmlOut2 = new StringWriter();
        xmlHelper.save(document, xmlOut2, null);
        assertLineEquality(xmlOut1, xmlOut2.toString());

        DataObject container = document.getRootObject();
        assertSame(container.get("CarRef"), container.get("Car"));

        compareReaders(document);
    }

    @Test
    public void testDefinitionByApi5() throws Exception {
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type stringType = typeHelper.getType(
            URINamePair.STRING.getURI(), URINamePair.STRING.getName());

        DataObject carTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        carTypeObject.set(TypeConstants.NAME, "Car");
        carTypeObject.set(TypeConstants.URI, "com.sap.test");
        carTypeObject.set(TypeConstants.KEY_TYPE, carTypeObject);

        DataObject propObject = carTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Owner");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.KEY, true);

        propObject = carTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "LicensePlate");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.KEY, true);

        DataObject companyCarTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        companyCarTypeObject.set(TypeConstants.NAME, "CompanyCar");
        companyCarTypeObject.set(TypeConstants.URI, "com.sap.test");
        companyCarTypeObject.getList(TypeConstants.BASE_TYPE).add(carTypeObject);

        propObject = companyCarTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Company");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject companyCarContainerTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        companyCarContainerTypeObject.set(TypeConstants.NAME, "CompanyCarContainer");
        companyCarContainerTypeObject.set(TypeConstants.URI, "com.sap.test");

        propObject = companyCarContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Car");
        propObject.set(PropertyConstants.TYPE, companyCarTypeObject);
        propObject.set(PropertyType.CONTAINMENT, true);

        propObject = companyCarContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "CarRef");
        propObject.set(PropertyConstants.TYPE, companyCarTypeObject);
        propObject.set(PropertyType.CONTAINMENT, false);

        SdoType t = (SdoType)typeHelper.define(companyCarTypeObject);
        SdoType c = (SdoType)typeHelper.define(companyCarContainerTypeObject);

        SdoType baseTypeWithKey = t.getTypeForKeyUniqueness();
        assertSame(t.getBaseTypes().get(0), baseTypeWithKey);
        assertSame(baseTypeWithKey, baseTypeWithKey.getKeyType());

        List<SdoProperty> keyProps = baseTypeWithKey.getKeyProperties();
        assertEquals(2, keyProps.size());
        assertEquals("Owner", keyProps.get(0).getName());
        assertEquals(true, keyProps.get(0).isKey());
        assertEquals("LicensePlate", keyProps.get(1).getName());
        assertEquals(true, keyProps.get(1).isKey());

        String schema = _helperContext.getXSDHelper().generate(Collections.singletonList(c));
        System.out.println(schema);

        DataObject companyCarContainer = _helperContext.getDataFactory().create(c);
        DataObject companyCar = companyCarContainer.createDataObject("Car");
        companyCarContainer.setDataObject("CarRef", companyCar);
        companyCar.setString("Owner", "Max Mustermann");
        companyCar.setString("LicensePlate", "HD-MM 1234");
        companyCar.setString("Company", "SAP AG");

        Object key = DataObjectBehavior.getKey(companyCar);

        DataObject keyDo = (DataObject)key;
        assertEquals("HD-MM 1234", keyDo.getString("LicensePlate"));
        assertEquals("Max Mustermann", keyDo.getString("Owner"));
        assertEquals("Car", keyDo.getType().getName());

        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        String xmlOut1 = xmlHelper.save(companyCarContainer, c.getURI(), c.getName());
        System.out.println(xmlOut1);
        XMLDocument document = xmlHelper.load(xmlOut1);
        StringWriter xmlOut2 = new StringWriter();
        xmlHelper.save(document, xmlOut2, null);
        assertLineEquality(xmlOut1, xmlOut2.toString());

        DataObject container = document.getRootObject();
        assertSame(container.get("CarRef"), container.get("Car"));

        compareReaders(document);
    }

    @Test
    public void testDefinitionByXsd3() throws IOException {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "EmbeddedKey.xsd");
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        TypeHelper typeHelper = _helperContext.getTypeHelper();
        SdoType t = (SdoType)typeHelper.getType("com.sap.test", "CompanyCar");

        SdoType baseTypeWithKey = t.getTypeForKeyUniqueness();
        assertSame(t.getBaseTypes().get(0), baseTypeWithKey);
        assertSame(typeHelper.getType("com.sap.test", "LicensePlate"), baseTypeWithKey.getKeyType());

        List<SdoProperty> keyProps = baseTypeWithKey.getKeyProperties();
        assertEquals(1, keyProps.size());
        assertEquals("LicensePlate", keyProps.get(0).getName());
        assertEquals(true, keyProps.get(0).isKey());

        SdoType c = (SdoType)typeHelper.getType("com.sap.test", "CompanyCarContainer");
        Property carProp = c.getProperty("Car");
        assertEquals(true, carProp.isContainment());

        Property carRefProp = c.getProperty("CarRef");
        assertEquals(false, carRefProp.isContainment());

    }

    @Test
    public void testDefinitionByApi6() throws Exception {
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type stringType = typeHelper.getType(
            URINamePair.STRING.getURI(), URINamePair.STRING.getName());

        DataObject orderKeyTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        orderKeyTypeObject.set(TypeConstants.NAME, "OrderKey");
        orderKeyTypeObject.set(TypeConstants.URI, "com.sap.test");

        DataObject propObject = orderKeyTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "ID1");
        propObject.set(PropertyConstants.TYPE, stringType);

        propObject = orderKeyTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "ID2");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject itemKeyTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        itemKeyTypeObject.set(TypeConstants.NAME, "ItemKey");
        itemKeyTypeObject.set(TypeConstants.URI, "com.sap.test");

        propObject = itemKeyTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "NR");
        propObject.set(PropertyConstants.TYPE, stringType);

        propObject = itemKeyTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Order");
        propObject.set(PropertyConstants.TYPE, orderKeyTypeObject);
        propObject.set(PropertyConstants.CONTAINMENT, true);

        DataObject itemTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        itemTypeObject.set(TypeConstants.NAME, "Item");
        itemTypeObject.set(TypeConstants.URI, "com.sap.test");
        itemTypeObject.set(TypeConstants.KEY_TYPE, itemKeyTypeObject);

        propObject = itemTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "NR");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.KEY, true);

        DataObject orderPropObject = itemTypeObject.createDataObject(TypeConstants.PROPERTY);
        orderPropObject.set(PropertyConstants.NAME, "Order");
        orderPropObject.set(PropertyConstants.OPPOSITE_INTERNAL, "Item");
        orderPropObject.set(PropertyType.KEY, true);

        propObject = itemTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Name");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject orderTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        orderTypeObject.set(TypeConstants.NAME, "Order");
        orderTypeObject.set(TypeConstants.URI, "com.sap.test");
        orderTypeObject.set(TypeConstants.KEY_TYPE, orderKeyTypeObject);

        orderPropObject.set(PropertyConstants.TYPE, orderTypeObject);

        propObject = orderTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "ID1");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.KEY, true);

        propObject = orderTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "ID2");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.KEY, true);

        propObject = orderTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Item");
        propObject.set(PropertyConstants.TYPE, itemTypeObject);
        propObject.set(PropertyConstants.CONTAINMENT, true);
        propObject.set(PropertyConstants.MANY, true);
        propObject.set(PropertyConstants.OPPOSITE_INTERNAL, "Order");

        DataObject itemContainerTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        itemContainerTypeObject.set(TypeConstants.NAME, "ItemContainer");
        itemContainerTypeObject.set(TypeConstants.URI, "com.sap.test");

        propObject = itemContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "ItemRef");
        propObject.set(PropertyConstants.TYPE, itemTypeObject);
        propObject.set(PropertyType.CONTAINMENT, false);

        propObject = itemContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Order");
        propObject.set(PropertyConstants.TYPE, orderTypeObject);
        propObject.set(PropertyType.CONTAINMENT, true);


        SdoType c = (SdoType)typeHelper.define(itemContainerTypeObject);

        String schema = _helperContext.getXSDHelper().generate(Collections.singletonList(c));
        System.out.println(schema);

        DataObject itemContainer = _helperContext.getDataFactory().create(c);
        DataObject order = itemContainer.createDataObject("Order");
        order.setString("ID1", "1");
        order.setString("ID2", "A");

        DataObject item = order.createDataObject("Item");
        item.setString("NR", "1");
        item.setString("Name", "Buy me!");
        itemContainer.setDataObject("ItemRef", item);

        DataObject orderKey = (DataObject)DataObjectBehavior.getKey(order);
        assertEquals("1", orderKey.getString("ID1"));
        assertEquals("A", orderKey.getString("ID2"));

        DataObject itemKey = (DataObject)DataObjectBehavior.getKey(item);
        assertEquals("1", itemKey.getString("NR"));
        orderKey = itemKey.getDataObject("Order");
        assertEquals("1", orderKey.getString("ID1"));
        assertEquals("A", orderKey.getString("ID2"));


        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        String xmlOut1 = xmlHelper.save(itemContainer, c.getURI(), c.getName());
        System.out.println(xmlOut1);
        XMLDocument document = xmlHelper.load(xmlOut1);
        StringWriter xmlOut2 = new StringWriter();
        xmlHelper.save(document, xmlOut2, null);
        assertLineEquality(xmlOut1, xmlOut2.toString());

        DataObject container = document.getRootObject();
        item = container.getDataObject("ItemRef");
        order = container.getDataObject("Order");
        assertSame(item, order.get("Item[1]"));
        assertSame(order, item.getContainer());
        assertSame(order, item.get("Order"));

        compareReaders(document);

        DataObject dataGraph = _helperContext.getDataFactory().create(
            URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());
        dataGraph.set("root", container);
        DataObject item2 = order.createDataObject("Item");
        item2.setString("NR", "2");
        item2.setString("Name", "For free!");

        dataGraph.getChangeSummary().beginLogging();
        String xmlOutDataGraph0 = xmlHelper.save(dataGraph, URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());

        item2.setString("Name", "For less!");

        DataObject item3 = order.createDataObject("Item");
        item3.setString("NR", "3");
        item3.setString("Name", "For free!");

        item.delete();
        container.set("ItemRef", item3);

        String xmlOutDataGraph1 = xmlHelper.save(dataGraph, URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());
        System.out.println(xmlOutDataGraph1);
        XMLDocument documentDataGraph = xmlHelper.load(xmlOutDataGraph1);
        StringWriter xmlOutDataGraph2 = new StringWriter();
        xmlHelper.save(documentDataGraph, xmlOutDataGraph2, null);
        assertLineEquality(xmlOutDataGraph1, xmlOutDataGraph2.toString());

        compareReaders(documentDataGraph);

        documentDataGraph.getRootObject().getChangeSummary().undoChanges();

        StringWriter xmlOutDataGraph3 = new StringWriter();
        xmlHelper.save(documentDataGraph, xmlOutDataGraph3, null);
        assertLineEquality(xmlOutDataGraph0, xmlOutDataGraph3.toString());
    }

    @Test
    public void testDefinitionByApi7() throws Exception {
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type stringType = typeHelper.getType(
            URINamePair.STRING.getURI(), URINamePair.STRING.getName());

        DataObject itemKeyTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        itemKeyTypeObject.set(TypeConstants.NAME, "ItemKey");
        itemKeyTypeObject.set(TypeConstants.URI, "com.sap.test");

        DataObject propObject = itemKeyTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "NR");
        propObject.set(PropertyConstants.TYPE, stringType);

        propObject = itemKeyTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Order");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject itemTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        itemTypeObject.set(TypeConstants.NAME, "Item");
        itemTypeObject.set(TypeConstants.URI, "com.sap.test");
        itemTypeObject.set(TypeConstants.KEY_TYPE, itemKeyTypeObject);

        propObject = itemTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "NR");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.KEY, true);

        DataObject orderPropObject = itemTypeObject.createDataObject(TypeConstants.PROPERTY);
        orderPropObject.set(PropertyConstants.NAME, "Order");
        orderPropObject.set(PropertyConstants.OPPOSITE_INTERNAL, "Item");
        orderPropObject.set(PropertyType.KEY, true);

        propObject = itemTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Name");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject orderTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        orderTypeObject.set(TypeConstants.NAME, "Order");
        orderTypeObject.set(TypeConstants.URI, "com.sap.test");
        orderTypeObject.set(TypeConstants.KEY_TYPE, stringType);

        orderPropObject.set(PropertyConstants.TYPE, orderTypeObject);

        propObject = orderTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "ID");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.KEY, true);

        propObject = orderTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Item");
        propObject.set(PropertyConstants.TYPE, itemTypeObject);
        propObject.set(PropertyConstants.CONTAINMENT, true);
        propObject.set(PropertyConstants.MANY, true);
        propObject.set(PropertyConstants.OPPOSITE_INTERNAL, "Order");

        DataObject itemContainerTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        itemContainerTypeObject.set(TypeConstants.NAME, "ItemContainer");
        itemContainerTypeObject.set(TypeConstants.URI, "com.sap.test");

        propObject = itemContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "ItemRef");
        propObject.set(PropertyConstants.TYPE, itemTypeObject);
        propObject.set(PropertyType.CONTAINMENT, false);

        propObject = itemContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Order");
        propObject.set(PropertyConstants.TYPE, orderTypeObject);
        propObject.set(PropertyType.CONTAINMENT, true);


        SdoType c = (SdoType)typeHelper.define(itemContainerTypeObject);

        String schema = _helperContext.getXSDHelper().generate(Collections.singletonList(c));
        System.out.println(schema);

        DataObject itemContainer = _helperContext.getDataFactory().create(c);
        DataObject order = itemContainer.createDataObject("Order");
        order.setString("ID", "1");

        DataObject item = order.createDataObject("Item");
        item.setString("NR", "1");
        item.setString("Name", "Buy me!");
        itemContainer.setDataObject("ItemRef", item);

        String orderKey = (String)DataObjectBehavior.getKey(order);
        assertEquals("1", orderKey);

        DataObject itemKey = (DataObject)DataObjectBehavior.getKey(item);
        assertEquals("1", itemKey.getString("NR"));
        orderKey = itemKey.getString("Order");
        assertEquals("1", orderKey);


        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        String xmlOut1 = xmlHelper.save(itemContainer, c.getURI(), c.getName());
        System.out.println(xmlOut1);
        XMLDocument document = xmlHelper.load(xmlOut1);
        StringWriter xmlOut2 = new StringWriter();
        xmlHelper.save(document, xmlOut2, null);
        assertLineEquality(xmlOut1, xmlOut2.toString());

        DataObject container = document.getRootObject();
        item = container.getDataObject("ItemRef");
        order = container.getDataObject("Order");
        assertSame(item, order.get("Item[1]"));
        assertSame(order, item.getContainer());
        assertSame(order, item.get("Order"));

        compareReaders(document);

        DataObject dataGraph = _helperContext.getDataFactory().create(
            URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());
        dataGraph.set("root", container);
        DataObject item2 = order.createDataObject("Item");
        item2.setString("NR", "2");
        item2.setString("Name", "For free!");

        dataGraph.getChangeSummary().beginLogging();
        String xmlOutDataGraph0 = xmlHelper.save(dataGraph, URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());

        item2.setString("Name", "For less!");

        DataObject item3 = order.createDataObject("Item");
        item3.setString("NR", "3");
        item3.setString("Name", "For free!");

        item.delete();
        container.set("ItemRef", item3);

        String xmlOutDataGraph1 = xmlHelper.save(dataGraph, URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());
        System.out.println(xmlOutDataGraph1);
        XMLDocument documentDataGraph = xmlHelper.load(xmlOutDataGraph1);
        StringWriter xmlOutDataGraph2 = new StringWriter();
        xmlHelper.save(documentDataGraph, xmlOutDataGraph2, null);
        assertLineEquality(xmlOutDataGraph1, xmlOutDataGraph2.toString());

        compareReaders(documentDataGraph);

        documentDataGraph.getRootObject().getChangeSummary().undoChanges();

        StringWriter xmlOutDataGraph3 = new StringWriter();
        xmlHelper.save(documentDataGraph, xmlOutDataGraph3, null);
        assertLineEquality(xmlOutDataGraph0, xmlOutDataGraph3.toString());
    }

    @Test
    public void testDefinitionByApi8() throws Exception {
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type stringType = typeHelper.getType(
            URINamePair.STRING.getURI(), URINamePair.STRING.getName());

        DataObject itemKeyTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        itemKeyTypeObject.set(TypeConstants.NAME, "ItemKey");
        itemKeyTypeObject.set(TypeConstants.URI, "com.sap.test");

        DataObject propObject = itemKeyTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "NR");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.getXmlElementProperty(), true);

        propObject = itemKeyTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Order");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject itemTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        itemTypeObject.set(TypeConstants.NAME, "Item");
        itemTypeObject.set(TypeConstants.URI, "com.sap.test");
        itemTypeObject.set(TypeConstants.KEY_TYPE, itemKeyTypeObject);

        propObject = itemTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "NR");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.getXmlElementProperty(), true);
        propObject.set(PropertyType.KEY, true);

        DataObject orderPropObject = itemTypeObject.createDataObject(TypeConstants.PROPERTY);
        orderPropObject.set(PropertyConstants.NAME, "Order");
        orderPropObject.set(PropertyConstants.OPPOSITE_INTERNAL, "Item");
        orderPropObject.set(PropertyType.KEY, true);

        propObject = itemTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Name");
        propObject.set(PropertyConstants.TYPE, stringType);

        DataObject orderTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        orderTypeObject.set(TypeConstants.NAME, "Order");
        orderTypeObject.set(TypeConstants.URI, "com.sap.test");
        orderTypeObject.set(TypeConstants.KEY_TYPE, stringType);

        orderPropObject.set(PropertyConstants.TYPE, orderTypeObject);

        propObject = orderTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "ID");
        propObject.set(PropertyConstants.TYPE, stringType);
        propObject.set(PropertyType.KEY, true);

        propObject = orderTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Item");
        propObject.set(PropertyConstants.TYPE, itemTypeObject);
        propObject.set(PropertyConstants.CONTAINMENT, true);
        propObject.set(PropertyConstants.MANY, true);
        propObject.set(PropertyConstants.OPPOSITE_INTERNAL, "Order");

        DataObject itemContainerTypeObject = _helperContext.getDataFactory().create(
            URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        itemContainerTypeObject.set(TypeConstants.NAME, "ItemContainer");
        itemContainerTypeObject.set(TypeConstants.URI, "com.sap.test");

        propObject = itemContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "ItemRef");
        propObject.set(PropertyConstants.TYPE, itemTypeObject);
        propObject.set(PropertyType.CONTAINMENT, false);

        propObject = itemContainerTypeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.set(PropertyConstants.NAME, "Order");
        propObject.set(PropertyConstants.TYPE, orderTypeObject);
        propObject.set(PropertyType.CONTAINMENT, true);


        SdoType c = (SdoType)typeHelper.define(itemContainerTypeObject);

        String schema = _helperContext.getXSDHelper().generate(Collections.singletonList(c));
        System.out.println(schema);

        DataObject itemContainer = _helperContext.getDataFactory().create(c);
        DataObject order = itemContainer.createDataObject("Order");
        order.setString("ID", "1");

        DataObject item = order.createDataObject("Item");
        item.setString("NR", "1");
        item.setString("Name", "Buy me!");
        itemContainer.setDataObject("ItemRef", item);

        String orderKey = (String)DataObjectBehavior.getKey(order);
        assertEquals("1", orderKey);

        DataObject itemKey = (DataObject)DataObjectBehavior.getKey(item);
        assertEquals("1", itemKey.getString("NR"));
        orderKey = itemKey.getString("Order");
        assertEquals("1", orderKey);


        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        String xmlOut1 = xmlHelper.save(itemContainer, c.getURI(), c.getName());
        System.out.println(xmlOut1);
        XMLDocument document = xmlHelper.load(xmlOut1);
        StringWriter xmlOut2 = new StringWriter();
        xmlHelper.save(document, xmlOut2, null);
        assertLineEquality(xmlOut1, xmlOut2.toString());

        DataObject container = document.getRootObject();
        item = container.getDataObject("ItemRef");
        order = container.getDataObject("Order");
        assertSame(item, order.get("Item[1]"));
        assertSame(order, item.getContainer());
        assertSame(order, item.get("Order"));

        compareReaders(document);

        DataObject dataGraph = _helperContext.getDataFactory().create(
            URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());
        dataGraph.set("root", container);
        DataObject item2 = order.createDataObject("Item");
        item2.setString("NR", "2");
        item2.setString("Name", "For free!");

        dataGraph.getChangeSummary().beginLogging();
        String xmlOutDataGraph0 = xmlHelper.save(dataGraph, URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());

        item2.setString("Name", "For less!");

        DataObject item3 = order.createDataObject("Item");
        item3.setString("NR", "3");
        item3.setString("Name", "For free!");

        item.delete();
        container.set("ItemRef", item3);

        String xmlOutDataGraph1 = xmlHelper.save(dataGraph, URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());
        System.out.println(xmlOutDataGraph1);
        XMLDocument documentDataGraph = xmlHelper.load(xmlOutDataGraph1);
        StringWriter xmlOutDataGraph2 = new StringWriter();
        xmlHelper.save(documentDataGraph, xmlOutDataGraph2, null);
        assertLineEquality(xmlOutDataGraph1, xmlOutDataGraph2.toString());

        compareReaders(documentDataGraph);

        documentDataGraph.getRootObject().getChangeSummary().undoChanges();

        StringWriter xmlOutDataGraph3 = new StringWriter();
        xmlHelper.save(documentDataGraph, xmlOutDataGraph3, null);
        assertLineEquality(xmlOutDataGraph0, xmlOutDataGraph3.toString());
	}
}
