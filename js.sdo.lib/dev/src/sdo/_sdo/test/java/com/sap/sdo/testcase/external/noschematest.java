package com.sap.sdo.testcase.external;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

public class NoSchemaTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public NoSchemaTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final String FILE_NAME =
        "com/sap/sdo/testcase/schemas/OpenWithoutSchema.xml";

    @Test
    public void testSchemaLocation() throws IOException {

        URL url = getClass().getClassLoader().getResource(FILE_NAME);
        XMLDocument document = _helperContext.getXMLHelper().load(url.openStream(), url.toString(), null);

        StringWriter stringWriter = new StringWriter();
        _helperContext.getXMLHelper().save(document, stringWriter, null);
        String xml = stringWriter.toString();
        System.out.println(xml);

        DataObject container = document.getRootObject();
        List<DataObject> unknownList = container.getList("unknown");
        DataObject unknown0 = unknownList.get(0);
        System.out.println(unknown0);

    }

}
