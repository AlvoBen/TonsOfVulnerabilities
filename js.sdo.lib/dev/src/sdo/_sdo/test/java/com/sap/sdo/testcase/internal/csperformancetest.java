/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
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

import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class CSPerformanceTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public CSPerformanceTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testMapping() throws Exception {
        String sourceSchema = PACKAGE + "source.xsd";
        String targetSchema = PACKAGE + "target.xsd";
        String xmlFile = PACKAGE + "source.xml";

        URL sourceUrl = getClass().getClassLoader().getResource(sourceSchema);
        URL targetUrl = getClass().getClassLoader().getResource(targetSchema);
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFile);

        _helperContext.getXSDHelper().define(sourceUrl.openStream(), sourceUrl.toString());
        _helperContext.getXSDHelper().define(targetUrl.openStream(), targetUrl.toString());

        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream());

        DataObject root = doc.getRootObject();

        DataObject copy = null;
        for (int i=0; i<10000; ++i) {
            copy = createCopy(root);
        }
        assertEquals(
            _helperContext.getXMLHelper().save(root, doc.getRootElementURI(), doc.getRootElementName()),
            _helperContext.getXMLHelper().save(copy, doc.getRootElementURI(), doc.getRootElementName()));
    }

    /**
     * @param root
     */
    private DataObject createCopy(DataObject pSource) {
        DataObject target =
            _helperContext.getDataFactory().create(
                "http://www.target.com", pSource.getType().getName());
        for (Property prop : (List<Property>)pSource.getInstanceProperties()) {
            if (prop.getType().isDataType()) {
                if (prop.isMany()) {
                    List<Object> list = target.getList(prop.getName());
                    for (Object data : (List<Object>)pSource.getList(prop)) {
                        list.add(data);
                    }
                } else {
                    target.set(prop.getName(), pSource.get(prop));
                }
            } else {
                if (prop.isMany()) {
                    List<DataObject> list = target.getList(prop.getName());
                    for (DataObject object : (List<DataObject>)pSource.getList(prop)) {
                        list.add(createCopy(object));
                    }
                } else {
                    target.set(prop.getName(), createCopy(pSource.getDataObject(prop)));
                }
            }
        }
        return target;
    }
}
