package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class ParallelSaveTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ParallelSaveTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testParallelSave() throws IOException {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        XMLHelper xmlHelper = _helperContext.getXMLHelper();

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "1.xml");
        final XMLDocument doc1 = xmlHelper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc1);
        String save1 = xmlHelper.save(doc1.getRootObject(), doc1.getRootElementURI(), doc1.getRootElementName());

        int count = 50;

        List<MyThread> threads = new ArrayList<MyThread>(count);
        for (int i = 0; i < count; i++) {
            threads.add(new MyThread(doc1));
        }

        for (int i = 0; i < count; i++) {
            threads.get(i).start();
        }

        while (!threads.isEmpty()) {
            for (Iterator<MyThread> it = threads.iterator(); it.hasNext();) {
                MyThread thread = it.next();
                String save = thread.getSaved();
                if (save != null) {
                    assertEquals(save1, save);
                    it.remove();
                }
            }
        }
    }

    private class MyThread extends Thread {

        private XMLDocument _doc;
        private String _saved;

        public MyThread(XMLDocument pDoc) {
            super();
            _doc = pDoc;
        }

        public String getSaved() {
            return _saved;
        }

        @Override
        public void run() {
            _saved = _helperContext.getXMLHelper().save(_doc.getRootObject(), _doc.getRootElementURI(), _doc.getRootElementName());
        }

    }

}
