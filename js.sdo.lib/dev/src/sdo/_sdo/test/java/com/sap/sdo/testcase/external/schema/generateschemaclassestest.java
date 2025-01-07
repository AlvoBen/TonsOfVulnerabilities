package com.sap.sdo.testcase.external.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.impl.context.HelperContextImpl;
import com.sap.sdo.impl.types.java.InterfaceGeneratorImpl;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.types.simple.ListSimpleType;
import com.sap.sdo.impl.util.VisitorException;
import com.sap.sdo.impl.xml.DefaultSchemaResolver;
import com.sap.sdo.impl.xml.SchemaLocation;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.impl.HelperProvider;

public class GenerateSchemaClassesTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public GenerateSchemaClassesTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final String SCHEMA_URI = "http://www.w3.org/2001/XMLSchema";
    private static final String PACKAGE_FOLDER = Schema.class.getPackage().getName().replace('.', '/');
    private static final String TARGET_ROOT = "C:/test/";
    private static final String PATH = "com/sap/sdo/testcase/external/schema/";

    @Test
    public void testLoadSchemaSchema() throws IOException, VisitorException {
        URL hfpUrl = getClass().getClassLoader().getResource(PATH + "XMLSchema-hasFacetAndProperty.xsd");
        List<Type> hfpTypes = _helperContext.getXSDHelper().define(hfpUrl.openStream(), hfpUrl.toString());
        for (Type type: hfpTypes) {
            String typeXml = _helperContext.getXMLHelper().save((DataObject)type, "commonj.sdo", "type");
            System.out.println(typeXml);
        }
        URL xsdUrl = getClass().getClassLoader().getResource(PATH + "XMLSchema.xsd");
        URL importUrl = getClass().getClassLoader().getResource("com/sap/sdo/impl/xml/XmlNamespace.xsd");
        SchemaLocation schemaLocation = new SchemaLocation(SCHEMA_URI, xsdUrl.toString());
        final DefaultSchemaResolver defaultSchemaResolver = getSchemaResolver();
        defaultSchemaResolver.defineSchemaLocationMapping("http://www.w3.org/2001/xml.xsd", importUrl.toString());
        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_SCHEMA_RESOLVER, defaultSchemaResolver);
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_FALSE);
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        SapXmlDocument xmlDocument = xmlHelper.load(xsdUrl.openStream(), xsdUrl.toString(), options);
        List<DataObject> types = xmlDocument.getDefinedTypes();
        Map namespaceToProperties = xmlDocument.getDefinedProperties();
        List<Type> noBuildInTypes = new ArrayList<Type>();
        for (DataObject typeObj: types) {
            Type type = (Type)typeObj;
            Type buildIType = _helperContext.getTypeHelper().getType(type.getURI(), type.getName());
            if (!(buildIType instanceof JavaSimpleType)) {
                noBuildInTypes.add(type);
                String typeXml = _helperContext.getXMLHelper().save((DataObject)type, "commonj.sdo", "type");
                System.out.println(typeXml);
            } else if (buildIType instanceof ListSimpleType) {
                noBuildInTypes.add(type);
                String typeXml = _helperContext.getXMLHelper().save(((ListSimpleType)type).getTypeDataObject(), "commonj.sdo", "type");
                System.out.println(typeXml);
            } else {
                fail("Built-in types should be filtered");
            }
        }

        // clean test folder
        String target = TARGET_ROOT + PACKAGE_FOLDER;
        File targetFolder = new File(target);
        if (targetFolder.exists()) {
            assertEquals(true, targetFolder.isDirectory());
            File[] interfaces = targetFolder.listFiles();
            for (int i = 0; i < interfaces.length; i++) {
                assertTrue(interfaces[i].getPath(), interfaces[i].delete());
            }
        }

        InterfaceGeneratorImpl javaGenerator = new InterfaceGeneratorImpl(TARGET_ROOT, namespaceToProperties);
        javaGenerator.addPackage(SCHEMA_URI, Schema.class.getPackage().getName());
        javaGenerator.generate(noBuildInTypes);
    }

    @Test
    public void testCompareJavaFiles() throws MalformedURLException, IOException {
        String target = TARGET_ROOT + PACKAGE_FOLDER;
        File targetFolder = new File(target);
        assertEquals(true, targetFolder.isDirectory());

        String perforceSrcRoot = System.getProperty("PerforceSrcRoot");
        assertNotNull("set perforce source root, e.g. -DPerforceSrcRoot=C:/perforce/sdo/base/common.libs/FrwkDev_stream/src/sdo_api/_sdo_api_extension/libm/api/", perforceSrcRoot);
        String perforce = perforceSrcRoot + PACKAGE_FOLDER;
        File schemaSrcFolder = new File(perforce);
        assertEquals(true, schemaSrcFolder.isDirectory());

        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.toString().endsWith(".java");
            }
        };
        File[] targetFiles = targetFolder.listFiles(fileFilter);
        Arrays.sort(targetFiles);
        File[] schemaSrcFiles = schemaSrcFolder.listFiles(fileFilter);
        Arrays.sort(schemaSrcFiles);

        assertEquals(getFileList(schemaSrcFiles), getFileList(targetFiles));
        for (int i = 0; i < targetFiles.length; i++) {
            File targetFile = targetFiles[i];
            File schemaSrcFile = schemaSrcFiles[i];
            assertEquals(schemaSrcFile.getName(), targetFile.getName());
            String targetClass = readFile(targetFile);
            String schemaSrcClass = readFile(schemaSrcFile).replaceAll(" {4}@Deprecated\n {4}boolean get.*();\n", "");
            assertEquals(schemaSrcClass, targetClass);
        }
    }

    @Test
    public void testSchemaLocation() {
        URL xsdUrl = getClass().getClassLoader().getResource(PATH + "XMLSchema.xsd");
        SchemaLocation schemaLocation = new SchemaLocation(SCHEMA_URI, xsdUrl.toString());
        assertEquals(
            "http://www.w3.org/2001/XMLSchema -> " + xsdUrl.toString(),
            schemaLocation.toString());

        assertEquals(schemaLocation, schemaLocation);
        assertFalse(schemaLocation.equals(null));
    }

    private String getFileList(File[] pFiles) {
        StringBuilder stringBuilder = new StringBuilder();
        for (File file: pFiles) {
            stringBuilder.append(file.getName());
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    /**
     * @return
     */
    private DefaultSchemaResolver getSchemaResolver() {
        if (_helperContext instanceof HelperContextImpl) {
            return new DefaultSchemaResolver(_helperContext);
        } else {
            return new DefaultSchemaResolver(HelperProvider.getDefaultContext());
        }
    }
}
