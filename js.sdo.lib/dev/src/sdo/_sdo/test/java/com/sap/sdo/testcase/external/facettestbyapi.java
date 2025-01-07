package com.sap.sdo.testcase.external;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.types.ctx.Facets;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;

public class FacetTestByApi extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public FacetTestByApi(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
    }

    @Test
    public void testFacets() {
        //How to create a Type with facets?

        DataFactory dataFactory = _helperContext.getDataFactory();
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        //Create a simple type that extends a base simple type
        //URINamePair helps with a lot of constants
        DataObject string10DO = dataFactory.create(URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        //TypeConstants knows the properties of a type
        string10DO.setString(TypeConstants.URI, "com.sap.test");
        string10DO.setString(TypeConstants.NAME, "String10");
        string10DO.setBoolean(TypeConstants.DATA_TYPE, true);
        string10DO.getList(TypeConstants.BASE_TYPE).add(typeHelper.getType(String.class));

        //Create a facets metadata object
        //For the Facets-Type an interface is available
        DataObject facetsDO = dataFactory.create(Facets.class);
        //It can be handled as a ordinary DataObject, but it's much easier to use the interface
        Facets facets = (Facets)facetsDO;
        facets.setMaxLength(10);
        facets.setMinLength(2);

        //Get the global open content property, that holds the facets
        Property facetsProp = typeHelper.getOpenContentProperty(URINamePair.PROP_CTX_FACETS.getURI(), URINamePair.PROP_CTX_FACETS.getName());
        //Add the facets as opent content to the type
        string10DO.set(facetsProp, facets);

        //All metadata for the type are collected, now we can define it
        Type string10 = typeHelper.define(string10DO);

        //Now we have a String-type with a length from 2 to 10, let's test it

        //Create a Property
        DataObject propDO = dataFactory.create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        //PropertyConstants knows the properties of a property
        propDO.setString(PropertyConstants.NAME, "myPropName");
        //assign our new type to the property
        propDO.set(PropertyConstants.TYPE, string10);
        //propDO.set(PropertyConstants.TYPE, string10DO); would be also possible

        //Create a complex type
        DataObject myComplexTypeDO = dataFactory.create(URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());

        myComplexTypeDO.set(TypeConstants.URI, "com.sap.test");
        myComplexTypeDO.set(TypeConstants.NAME, "myComplexType");
        myComplexTypeDO.set(TypeConstants.DATA_TYPE, false);

        //Add the property with the new type
        myComplexTypeDO.getList(TypeConstants.PROPERTY).add(propDO);

        //Define the complex type
        Type myComplexType = typeHelper.define(myComplexTypeDO);

        //Now we can test it with a DataObject
        DataObject myDO = dataFactory.create(myComplexType);
        myDO.set("myPropName", "ok");

        try {
            myDO.set("myPropName", "?");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
            e.printStackTrace();
        }

        try {
            myDO.set("myPropName", "really to long");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
            e.printStackTrace();
        }

        //How can a user introspect the facets?
        Type introspectType = myDO.getType().getProperty("myPropName").getType();

        //Like above, get the global open content property, that holds the facets
        Property facetsProperty = typeHelper.getOpenContentProperty(URINamePair.PROP_CTX_FACETS.getURI(), URINamePair.PROP_CTX_FACETS.getName());

        //Get the facets-DataObject
        DataObject introspectFacets = (DataObject)introspectType.get(facetsProperty);
        for (Property facetItemProp: (List<Property>)introspectFacets.getInstanceProperties()) {
            if (introspectFacets.isSet(facetItemProp)) {
                System.out.println(facetItemProp.getName() + ':' + introspectFacets.getString(facetItemProp));
            }
        }
        //Cast to com.sap.sdo.api.types.ctx.Facets is also possible
        Facets facetsObject = (Facets)introspectFacets;
    }

}
