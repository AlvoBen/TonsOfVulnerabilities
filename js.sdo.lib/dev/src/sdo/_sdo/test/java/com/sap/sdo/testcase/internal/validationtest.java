package com.sap.sdo.testcase.internal;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.impl.xml.XmlParseException;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.OppositePropsA;
import com.sap.sdo.testcase.typefac.OppositePropsB;

import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class ValidationTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ValidationTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testValidation() throws IOException {
        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_VALIDATOR, SapHelperProvider.getValidator());

        Type t = _helperContext.getTypeHelper().getType(OpenInterface.class);
        _helperContext.getTypeHelper().getType(OppositePropsA.class);
        _helperContext.getTypeHelper().getType(OppositePropsB.class);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <a xsi:type=\"o:OppositePropsA\">\n"
            + "    <bs>#/o:" + t.getName() + "/b.0</bs>\n"
            + "    <bs>#/o:" + t.getName() + "/b.1</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "</o:"+t.getName()+">\n";
        Reader reader = new StringReader(xml);
        XMLDocument doc = _helperContext.getXMLHelper().load(reader, null, options);

        StringWriter writer = new StringWriter();

        _helperContext.getXMLHelper().save(doc,writer, null);
        System.out.println(writer.toString());
    }

    @Test
    public void testValidationFailOpposite() throws IOException {
        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_VALIDATOR, SapHelperProvider.getValidator());

        Type t = _helperContext.getTypeHelper().getType(OpenInterface.class);
        buildRootProp(t);
        _helperContext.getTypeHelper().getType(OppositePropsA.class);
        _helperContext.getTypeHelper().getType(OppositePropsB.class);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <a xsi:type=\"o:OppositePropsA\">\n"
            + "    <bs>#/" + t.getName() + "/b.0</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "</o:"+t.getName()+">\n";
        Reader reader = new StringReader(xml);
        XMLDocument doc;
        try {
            doc = _helperContext.getXMLHelper().load(reader, null, options);
            fail("XmlParseException expected");
        } catch (XmlParseException e) {
            // expected
        }
    }
}
