package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.sax.SAXSource;

import org.junit.Test;
import org.xml.sax.XMLReader;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.util.SDOContentHandler;
import com.sap.sdo.api.helper.util.SDOSource;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleAttrIntf;
import com.sap.xi.appl.se.global.OutboundDeliveryByBatchIDQueryResponseInManyResponse;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class InputSourceTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public InputSourceTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
    }

    @Test
    public void testJaxbByXmlStreamReader() throws Exception {

        // sdo setup
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        // jaxb setup
        JAXBContext ctx =
            JAXBContext.newInstance("com.sap.xi.appl.se.global");

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "1.xml");
        XMLHelper helper = _helperContext.getXMLHelper();
        XMLDocument doc = helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);

        JAXBResult result = new JAXBResult(ctx);

        helper.save(doc, result, null);

        Object data = result.getResult();

        assertEquals(
            OutboundDeliveryByBatchIDQueryResponseInManyResponse.class,
            ((JAXBElement)data).getDeclaredType());

        JAXBSource source = new JAXBSource(ctx, data);
        DataObject sdo = _helperContext.getXMLHelper().load(source, null, null).getRootObject();

        assertTrue(_helperContext.getEqualityHelper().equal(doc.getRootObject(), sdo));
    }

    @Test
    public void testSaxSource() throws Exception {

        // sdo setup
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "1.xml");
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        XMLDocument doc = xmlHelper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);

        SAXSource sdoSaxSource = new SAXSource(xmlHelper.createXMLReader(doc,null), null);
        XMLDocument doc2 = xmlHelper.load(sdoSaxSource, null, null);

        assertTrue(_helperContext.getEqualityHelper().equal(doc.getRootObject(), doc2.getRootObject()));
    }

    @Test
    public void testSdoSource() throws Exception {

        // sdo setup
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "1.xml");
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        XMLDocument doc = xmlHelper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);

        SDOSource sdoSaxSource = xmlHelper.createSDOSource(doc, null);
        XMLDocument doc2 = xmlHelper.load(sdoSaxSource, null, null);

        assertTrue(_helperContext.getEqualityHelper().equal(doc.getRootObject(), doc2.getRootObject()));
    }

    @Test
    public void testSaxSourceRoundtrip() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleAttrIntf.class);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<simple:"+t.getName()+" name=\"Stefan\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:simple=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</simple:"+t.getName()+">\n";
        InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        XMLDocument doc = _helperContext.getXMLHelper().load(is);

        Feature.XML_READER.testFeature(doc, null);
    }

    @Test
    public void testXmlParsing() {
        String xml = "<element" +
                            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                        "<gMonth xsi:type=\"xsd:gMonth\">--12-01:00</gMonth>" +
                     "</element>";

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);

        Feature.XML_READER.testFeature(doc, null);
    }

    @Test
    public void testNilElement() throws Exception {
        String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "  <openInterface>\n" +
            "    <a xsi:nil=\"true\"></a>\n" +
            "  </openInterface>\n" +
            "</root>\n";
        Map options = Collections.singletonMap(SapXmlHelper.OPTION_KEY_SIMPLIFY_OPEN_CONTENT, SapXmlHelper.OPTION_VALUE_FALSE);
        XMLDocument doc = _helperContext.getXMLHelper().load(new StringReader(xml), null, options);

        Feature.XML_READER.testFeature(doc, options);
    }

    @Test
    public void testContentHandler() throws Exception {

        // sdo setup
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "1.xml");
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        Map options = Collections.singletonMap(SapXmlHelper.OPTION_KEY_SIMPLIFY_OPEN_CONTENT, SapXmlHelper.OPTION_VALUE_FALSE);
        XMLDocument doc = xmlHelper.load(xmlUrl.openStream(), xmlUrl.toString(), options);
        assertNotNull(doc);

        XMLReader xmlReader = xmlHelper.createXMLReader(doc, null);
        SDOContentHandler sdoContentHandler = xmlHelper.createContentHandler(null);
        xmlReader.setContentHandler(sdoContentHandler);
        xmlReader.parse("");
        XMLDocument doc2 = sdoContentHandler.getDocument();


        StringWriter save1 = new StringWriter();
        _helperContext.getXMLHelper().save(doc, save1, null);
        StringWriter save2 = new StringWriter();
        _helperContext.getXMLHelper().save(doc2, save2, null);
        assertEquals(save1.toString(), save2.toString());
        assertTrue(_helperContext.getEqualityHelper().equal(doc.getRootObject(), doc2.getRootObject()));
    }
}
