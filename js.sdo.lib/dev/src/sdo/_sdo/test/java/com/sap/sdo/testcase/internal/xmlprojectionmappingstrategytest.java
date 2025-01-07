package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.projections.AbstractProjectionMappingStrategy;
import com.sap.sdo.impl.objects.projections.XmlProjectionMappingStrategy;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleTypesIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;

public class XmlProjectionMappingStrategyTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public XmlProjectionMappingStrategyTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testGetProperty() {
        HelperContext helperContext2 = SapHelperProvider.getContext("hc2");
        String xsd1 = PACKAGE + "propertyRenaming.xsd";
        String xsd2 = PACKAGE + "propertyRenaming2.xsd";

        _helperContext.getXSDHelper().define(getClass().getClassLoader().getResourceAsStream(xsd1), xsd1);
        helperContext2.getXSDHelper().define(getClass().getClassLoader().getResourceAsStream(xsd2), xsd2);
        TypeHelper typeHelper1 = _helperContext.getTypeHelper();
        TypeHelper typeHelper2 = helperContext2.getTypeHelper();

        AbstractProjectionMappingStrategy strategy = XmlProjectionMappingStrategy.getInstance();

        Type typeA1 = typeHelper1.getType("com.sap.test", "typeA");
        Type typeA2 = typeHelper2.getType("com.sap.test", "typeA");

        int[] map = strategy.getPropertyMap(typeA1, typeA2);
        assertEquals(4, map.length);
        for (int i = 0; i < map.length; i++) {
            assertEquals(i, map[i]);
        }

        map = strategy.getInversePropertyMap(typeA1, typeA2);
        assertEquals(4, map.length);
        for (int i = 0; i < map.length; i++) {
            assertEquals(i, map[i]);
        }

        map = strategy.getPropertyMap(typeA2, typeA1);
        assertEquals(4, map.length);
        for (int i = 0; i < map.length; i++) {
            assertEquals(i, map[i]);
        }

        map = strategy.getInversePropertyMap(typeA2, typeA1);
        assertEquals(4, map.length);
        for (int i = 0; i < map.length; i++) {
            assertEquals(i, map[i]);
        }
    }

    @Test
    public void testGetOpenProperty() {
        HelperContext helperContext2 = SapHelperProvider.getContext("hc2");
        String xsd1 = PACKAGE + "propertyRenaming.xsd";
        String xsd2 = PACKAGE + "propertyRenaming2.xsd";

        _helperContext.getXSDHelper().define(getClass().getClassLoader().getResourceAsStream(xsd1), xsd1);
        helperContext2.getXSDHelper().define(getClass().getClassLoader().getResourceAsStream(xsd2), xsd2);
        TypeHelper typeHelper1 = _helperContext.getTypeHelper();
        TypeHelper typeHelper2 = helperContext2.getTypeHelper();

        AbstractProjectionMappingStrategy strategy = XmlProjectionMappingStrategy.getInstance();

        Property refElement1 = typeHelper1.getOpenContentProperty("com.sap.test", "refElement");
        Property refElement2 = typeHelper2.getOpenContentProperty("com.sap.test", "refElement2");

        assertSame(refElement2, strategy.getOpenProperty(helperContext2, refElement1));
        assertSame(refElement1, strategy.getOpenProperty(_helperContext, refElement2));

        DataObject propObj = _helperContext.getDataFactory().create(
            URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        propObj.set(PropertyConstants.NAME, "onDemand");
        propObj.set(PropertyConstants.CONTAINMENT, false);
        propObj.set(PropertyConstants.TYPE, typeHelper1.getType(int.class));
        propObj.set(PropertyType.getXmlElementProperty(), false);
        Property onDemandProp1 = typeHelper1.defineOpenContentProperty(null, propObj);

        Property onDemandProp2 = strategy.getOpenProperty(helperContext2, onDemandProp1);
        compareProperties(onDemandProp1, onDemandProp2, typeHelper1, typeHelper2);

        propObj = _helperContext.getDataFactory().create(
            URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        propObj.set(PropertyConstants.NAME, "complexOnDemand");
        propObj.set(PropertyConstants.CONTAINMENT, true);
        propObj.set(PropertyConstants.TYPE, typeHelper1.getType("com.sap.test", "typeA"));
        propObj.set(PropertyType.getXmlElementProperty(), true);
        Property complexOnDemandProp1 = typeHelper1.defineOpenContentProperty(null, propObj);
        Property complexOnDemandProp2 = strategy.getOpenProperty(helperContext2, complexOnDemandProp1);
        compareProperties(complexOnDemandProp1, complexOnDemandProp2, typeHelper1, typeHelper2);


        propObj = _helperContext.getDataFactory().create(
            URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        propObj.set(PropertyConstants.NAME, "complexOnDemand3");
        propObj.set(PropertyConstants.CONTAINMENT, true);
        propObj.set(PropertyConstants.TYPE, typeHelper1.getType(SimpleTypesIntf.class));
        propObj.set(PropertyType.getXmlElementProperty(), true);
        Property complexOnDemandProp3 = typeHelper1.defineOpenContentProperty(null, propObj);
        try {
            Property complexOnDemandProp4 = strategy.getOpenProperty(helperContext2, complexOnDemandProp3);
            fail();
        } catch (IllegalArgumentException e) {
            //expected
        }
    }
    private void compareProperties(Property onDemandProp1, Property onDemandProp2, TypeHelper pTypeHelper1, TypeHelper pTypeHelper2) {
        List<Property> props1 = ((DataObject)onDemandProp1).getInstanceProperties();
        List<Property> props2 = ((DataObject)onDemandProp2).getInstanceProperties();
        assertEquals(props1.size(), props2.size());
        for (Property propProp: props1) {
            if (URINamePair.TYPE.equalsUriName(propProp.getType())) {
                Type type1 = (Type)onDemandProp1.get(propProp);
                Type type2 = (Type)onDemandProp2.get(propProp);
                if (type1 == null) {
                    assertNull(type2);
                } else {
                    assertSame(type1, pTypeHelper1.getType(type2.getURI(), type2.getName()));
                    assertSame(type2, pTypeHelper2.getType(type1.getURI(), type1.getName()));
                }
            } else {
                assertEquals(onDemandProp1.get(propProp), onDemandProp2.get(propProp));
            }
        }
    }


}
