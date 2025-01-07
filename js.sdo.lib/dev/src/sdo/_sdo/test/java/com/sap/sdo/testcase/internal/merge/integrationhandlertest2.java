package com.sap.sdo.testcase.internal.merge;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.transform.sax.SAXResult;

import org.junit.Assert;
import org.junit.Test;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.util.IntegrationHandler;
import com.sap.sdo.api.helper.util.SDOContentHandler;
import com.sap.sdo.api.helper.util.IntegrationHandler.Mode;
import com.sap.sdo.testcase.SdoTestCase;
import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

public class IntegrationHandlerTest2 extends SdoTestCase {
    
    public IntegrationHandlerTest2(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
    }

    public static String PACKAGE = "com/sap/sdo/testcase/internal/merge/";
    
    @Test
    public void testSetSet() throws IOException {
        integrate(Mode.SET, Mode.SET, "mergeSource2.xml");
    }
    
    @Test
    public void testMergeSet() throws IOException {
        integrate(Mode.MERGE, Mode.SET, "targetMergeSet2.xml");
    }

    @Test
    public void testAppendAppend() throws IOException {
        integrate(Mode.APPEND, Mode.APPEND, "targetAppendAppend2.xml");
    }

    @Test
    public void testMergeAppend() throws IOException {
        integrate(Mode.MERGE, Mode.APPEND, "targetMergeAppend2.xml");
    }
    
    @Test
    public void testMergeMerge() throws IOException {
        integrate(Mode.MERGE, Mode.MERGE, "targetMergeMerge2.xml");
    }
    
    public void integrate(Mode pLevel1Mode, Mode pLevel2Mode, String controlFile) throws IOException {
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        String mergeSourceFile = PACKAGE + "mergeSource2.xml";
        URL mergeSourceUrl = getClass().getClassLoader().getResource(mergeSourceFile);
        XMLDocument mergeSourceDoc = xmlHelper.load(mergeSourceUrl.openStream(), mergeSourceFile, null);

        String targetOriginalFile = PACKAGE + "targetOriginal2.xml";
        URL targetOriginalUrl = getClass().getClassLoader().getResource(targetOriginalFile);
        XMLDocument targetOriginalDoc = xmlHelper.load(targetOriginalUrl.openStream(), targetOriginalFile, null);

        IntegrationHandler integrationHandler = new IntegrationHandlerImpl(targetOriginalDoc.getRootObject(), pLevel1Mode, pLevel2Mode);
        Map options = Collections.singletonMap(IntegrationHandler.class.getName(), integrationHandler);
        SDOContentHandler contentHandler = xmlHelper.createContentHandler(options);
        
        SAXResult result = new SAXResult(contentHandler);
        xmlHelper.save(mergeSourceDoc, result, null);
        
        Assert.assertSame(targetOriginalDoc.getRootObject(), contentHandler.getDocument().getRootObject());
        
        StringWriter targetMerged = new StringWriter();
        xmlHelper.save(targetOriginalDoc, targetMerged, null);
        
        String controlXml = readFile(getClass().getClassLoader().getResource(PACKAGE + controlFile));
        assertLineEquality(controlXml, targetMerged.toString());
    }
    
    public class IntegrationHandlerImpl implements IntegrationHandler {
        
        Mode _level1mode;
        Mode _level2mode;
        final DataObject _root;

        public IntegrationHandlerImpl(DataObject pRoot, Mode pLevel1Mode, Mode pLevel2Mode) {
            _level1mode = pLevel1Mode;
            _level2mode = pLevel2Mode;
            _root = pRoot;
        }

        public Mode getIntegrationMode(List<Element> path) {
            Mode mode;
            if (path.size() == 1) {
                mode = Mode.MERGE;
            } else if (path.size() == 2) {
                mode = _level1mode;
            } else {
                mode = _level2mode;
            }
            
            for (Element element: path) {
                System.out.print(element.getName() + '/');
            }
            System.out.println(' ' + mode.toString());
            return mode;
        }

        public DataObject getRootObject() {
            return _root;
        }
        
    }

}
