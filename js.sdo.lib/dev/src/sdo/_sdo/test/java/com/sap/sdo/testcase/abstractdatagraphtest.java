package com.sap.sdo.testcase;

import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.testcase.typefac.EmptyOpenInterface;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public abstract class AbstractDataGraphTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public AbstractDataGraphTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates a DataGraph with an open type root object. The root object has an
     * open property called like pRootPropertyName with the value pRootPropertyValue.
     * @param pRootPropertyName The name of the first usefull DataObject.
     * @param pRootPropertyValue The first usefull DataObject.
     * @return The DataGraph tha surounds the DataObject.
     */
    public DataGraph getDataGraph(String pRootPropertyName, DataObject pRootPropertyValue) {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");
        Type rootType = _helperContext.getTypeHelper().getType(EmptyOpenInterface.class);
        DataObject rootObject = dataGraph.createRootObject(rootType);

        DataObject rootProp = _helperContext.getDataFactory().create(PropertyType.getInstance());
        rootProp.set(PropertyType.NAME, "root");
        rootProp.set(PropertyType.TYPE, pRootPropertyValue.getType());
        rootProp.set(PropertyType.CONTAINMENT, true);

        Property rootProperty = _helperContext.getTypeHelper().defineOpenContentProperty(null, rootProp);
        rootObject.set(rootProperty, pRootPropertyValue);

        return dataGraph;
    }
}
