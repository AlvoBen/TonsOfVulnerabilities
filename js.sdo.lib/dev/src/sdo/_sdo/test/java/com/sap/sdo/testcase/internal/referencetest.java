package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.ReferenceTestConcrete;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.XMLDocument;

public class ReferenceTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ReferenceTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }


    @Test
    public void testCreateSchema() {
        Type baseType = _helperContext.getTypeHelper().getType(ReferenceTestConcrete.class);
        Type conreteType = _helperContext.getTypeHelper().getType(ReferenceTestConcrete.class);
        List<Type> types = new ArrayList<Type>();
        types.add(baseType);
        types.add(conreteType);
        String schema = _helperContext.getXSDHelper().generate(types);
        System.out.println(schema);
    }


    @Test
    public void testCreateXml() throws IOException {
        final DataFactory dataFactory = _helperContext.getDataFactory();
        DataObject outerDo = dataFactory.create(ReferenceTestConcrete.class);
        ReferenceTestConcrete outer = (ReferenceTestConcrete)outerDo;
        DataObject innerDo = dataFactory.create(ReferenceTestConcrete.class);
        ReferenceTestConcrete inner = (ReferenceTestConcrete)innerDo;

        inner.setValue1("value1");
        inner.setValue2("value2");

        outer.setDataObjectProp(innerDo);
        outer.setBaseProp(inner);
        outer.setConcreteProp(inner);

        String xml = _helperContext.getXMLHelper().save(outerDo, outerDo.getType().getURI(), outerDo.getType().getName());
        System.out.println(xml);

        XMLDocument document = _helperContext.getXMLHelper().load(xml);

        StringWriter writer = new StringWriter();
        _helperContext.getXMLHelper().save(document, writer, null);
        assertEquals(xml, writer.toString());

    }


}
