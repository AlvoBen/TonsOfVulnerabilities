package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class WsPerformanceStandaloneTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public WsPerformanceStandaloneTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final int ITERATION_COUNT = 200;
    @Test
    public void testLoadSave() throws IOException {
        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_TRUE);

        URL wsdlUrl = getClass().getClassLoader().getResource(PACKAGE + "KpiConfigPortType.xml");
        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        xmlHelper.load(wsdlUrl.openStream(), wsdlUrl.toString(), options);
        Type longClassType = _helperContext.getTypeHelper().getType("urn:kpi_test/long", "LongClass");
        assertNotNull(longClassType);

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "output.xml");
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        InputStream input = xmlUrl.openStream();
        byte[] bytes = new byte[1024];
        while (true) {
            int n = input.read(bytes);
            if (n == -1) {
                break;
            }
            byteOut.write(bytes, 0, n);
        }
        byteOut.flush();
        XMLDocument document = null;

        String byteStr = byteOut.toString();
        for (int i=0; i<ITERATION_COUNT/2; ++i) {
            document = xmlHelper.load(byteStr);
            assertNotNull(document);
        }
        long start = System.nanoTime();
        for (int i=0; i<ITERATION_COUNT; ++i) {
            document = xmlHelper.load(byteStr);
        }
        long end = System.nanoTime();
        System.out.println("Done " + ITERATION_COUNT + " iterations of deserialization. (String)");
        System.out.println("Each message is " + byteStr.length() + " bytes long.");
        System.out.println("Time taken : " + (end - start)/1000000 + " ms.");

        String locationURI = xmlUrl.toString();
        for (int i=0; i<ITERATION_COUNT/2; ++i) {
            InputStream byteStream = new ByteArrayInputStream(byteOut.toByteArray());
            document = xmlHelper.load(byteStream, locationURI, null);
            assertNotNull(document);
        }
        start = System.nanoTime();
        for (int i=0; i<ITERATION_COUNT; ++i) {
            InputStream byteStream = new ByteArrayInputStream(byteOut.toByteArray());
            document = xmlHelper.load(byteStream, locationURI, null);
        }
        end = System.nanoTime();
        System.out.println("Done " + ITERATION_COUNT + " iterations of deserialization. (InputStream)");
        System.out.println("Each message is " + byteStr.length() + " bytes long.");
        System.out.println("Time taken : " + (end - start)/1000000 + " ms.");

        StringWriter output = null;
        for (int i=0; i<ITERATION_COUNT/2; ++i) {
            output = new StringWriter();
            xmlHelper.save(document, output, Collections.singletonMap(SapXmlHelper.OPTION_KEY_INDENT, null));
            assertNotNull(output.toString());
        }
        start = System.nanoTime();
        for (int i=0; i<ITERATION_COUNT; ++i) {
            output = new StringWriter();
            xmlHelper.save(document, output, Collections.singletonMap(SapXmlHelper.OPTION_KEY_INDENT, null));
        }
        end = System.nanoTime();
        System.out.println("Done " + ITERATION_COUNT + " iterations of serialization.");
        System.out.println("Each message is " + output.toString().length() + " bytes long.");
        System.out.println("Time taken : " + (end - start)/1000000 + " ms.");
        //System.out.println(output.toString());
    }

    public static void main(String[] args) throws Exception {
        WsPerformanceStandaloneTest test =
            new WsPerformanceStandaloneTest(SapHelperProvider.DEFAULT_CONTEXT_ID, null);

        test.testLoadSave();
    }
}
