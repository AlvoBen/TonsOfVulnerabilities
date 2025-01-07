package com.sap.sdo.testcase;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import test.SdoTestUtil;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.util.SDOResult;
import com.sap.sdo.api.helper.util.SDOSource;
import com.sap.sdo.api.helper.util.SDOContentHandler;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.HelperContextImpl;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

@RunWith(Parameterized.class)
public abstract class SdoTestCase {

    protected static final String PACKAGE = "com/sap/sdo/testcase/schemas/";
    protected static final String IGNORE_FOR_FEATURE_TEST = "ignoreForFeatureTest";
    protected static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();

    private static final XMLReader XML_READER;

    static {
        XML_INPUT_FACTORY.setProperty("javax.xml.stream.supportDTD", false);
        try {
            XML_READER = XMLReaderFactory.createXMLReader();
            XML_READER.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (SAXException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected SapHelperContext _helperContext;
    private final String _contextId;
    private final Feature _feature;

    private String _element = null;

    public SdoTestCase(String pContextId, Feature pFeature) {
        super();
        _contextId = pContextId;
        _feature = pFeature;
    }

    @Parameters
    public static Collection<Object[]> helperContexts() {
        return Arrays.asList(
            new Object[][] {
                {SapHelperProvider.DEFAULT_CONTEXT_ID, null},
                {"test", null},
                {"instance", null},
                {SapHelperProvider.DEFAULT_CONTEXT_ID, Feature.XML_STREAM_READER},
                {SapHelperProvider.DEFAULT_CONTEXT_ID, Feature.XML_READER},
                {SapHelperProvider.DEFAULT_CONTEXT_ID, Feature.CONTENT_HANDLER},
                {SapHelperProvider.DEFAULT_CONTEXT_ID, Feature.SAX_PARSER}
            });
    }

    @Before
    public void setUpHelperContext() throws Exception {
        _helperContext = getHelperContext();
    }

    @After
    public void tearDownHelperContext() throws Exception {
        if ("instance".equals(_contextId)) {
            SapHelperProvider.removeContext(SapHelperProvider.DEFAULT_CONTEXT_ID);
        } else {
            SapHelperProvider.removeContext(_contextId);
        }
        _helperContext = null;
    }

  /**
     * Return always a fresh HelperContext.
     *
     * @return The HelperContext.
     */
    public SapHelperContext getHelperContext() {
        if ("instance".equals(_contextId)) {
            if (getClass().getName().startsWith("com.sap.sdo.testcase.internal")) {
                return (SapHelperContext)SapHelperProvider.getContext(SapHelperProvider.DEFAULT_CONTEXT_ID);
            } else {
                return new DefaultHelperContext();
            }
        }
        SapHelperContext helperContext = (SapHelperContext)SapHelperProvider.getContext(_contextId);
        if (_feature != null) {
            return new DelegatingHelperContext(helperContext, _feature);
        }
        return helperContext;
    }

    protected String readFile(URL pUrl) throws IOException {
        return readFile(new InputStreamReader(pUrl.openStream()));
    }

    protected String readFile(File pFile) throws IOException {
        FileReader fileReader = new FileReader(pFile);
        try {
            return readFile(fileReader);
        } finally {
            fileReader.close();
        }
    }

    protected String readFile(InputStreamReader pReader) throws IOException {
        BufferedReader in = new BufferedReader(pReader);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        while (true) {
            String line = in.readLine();
            if (line == null) {
                return writer.toString();
            }
            printWriter.print(line);
            printWriter.print('\n');
        }
    }

    protected void assertLineInclusion(String expected, String actual) {
        StringTokenizer tokenizer = new StringTokenizer(expected);
        if (tokenizer.countTokens() < 2) {
            assertEquals(expected, actual);
        }
        String token1 = tokenizer.nextToken();
        String token2 = tokenizer.nextToken();
        int start = actual.indexOf(token1 + ' ' + token2);
        if (start < 0) {
            assertEquals(expected, actual);
        }
        String[] expectedLines = expected.split("\n");
        String[] actualLines = actual.substring(start).split("\n");
        for (int i=0; i<expectedLines.length; ++i) {
            assertLineEquality(expectedLines[i], actualLines[i]);
        }
    }

    protected void assertLineEquality(String expected, String actual) {
        assertLineEquality(null, expected, actual);
    }

    protected void assertLineEquality(String message, String expected, String actual) {
        if (expected == null || actual == null) {
            assertEquals(message, expected, actual);
        }
        StringTokenizer tokenizerE = new StringTokenizer(expected, "\n");
        StringTokenizer tokenizerA = new StringTokenizer(actual, "\n");
        if (tokenizerE.countTokens() != tokenizerA.countTokens()) {
            assertEquals(message, expected, actual);
        }
        String tokenE;
        String tokenA;
        while (tokenizerE.hasMoreTokens()) {
            tokenE = tokenizerE.nextToken().trim();
            tokenA = tokenizerA.nextToken().trim();
            if (!tokenE.equals(tokenA) && !checkSameTokensInLine(tokenE, tokenA)) {
                assertEquals(message, expected, actual);
            }
        }
    }

    /**
     * @param tokenE
     * @param tokenA
     * @return
     */
    private boolean checkSameTokensInLine(String tokenE, String tokenA) {
        StringTokenizer expected = new StringTokenizer(tokenE, " \t>");
        StringTokenizer actual = new StringTokenizer(tokenA, " \t>");
        int expectedCount = expected.countTokens();
        int actualCount = actual.countTokens();
        int closingElements = 0;
        int closingOffset = 0;
        if (expectedCount != actualCount) {
            closingElements = actualCount-expectedCount;
            if (closingElements < 0) {
                return false;
            }
        }
        List<String> tokens = new ArrayList<String>(expectedCount);
        while (expected.hasMoreTokens()) {
            tokens.add(normalizeToken(expected.nextToken()));
        }
        while (actual.hasMoreTokens()) {
            String token = normalizeToken(actual.nextToken());
            if (token.charAt(0)=='<' && token.charAt(1)!='/') {
                _element = token.substring(1);
            }
            if (!tokens.remove(token)) {
                if (closingElements > 0
                        && token.charAt(0)=='<' && token.charAt(1)=='/'
                        && token.substring(2).equals(_element)) {
                    --closingElements;
                    closingOffset += token.length();
                } else {
                    return false;
                }
            }
        }
        if (closingElements > 0 || tokenE.length() + closingOffset != tokenA.length()) {
            return false;
        }
        return true;
    }

    private String normalizeToken(String token) {
        int length = token.length();
        if (token.charAt(length-1) == '/') {
            return token.substring(0, length-1);
        }
        return token;
    }

    protected void compareReaders(DataObject pRoot, String pRootUri, String pRootName)
        throws IOException, XMLStreamException {

        compareReaders(
            _helperContext.getXMLHelper().createDocument(pRoot, pRootUri, pRootName));
    }

    protected void compareReaders(XMLDocument pDoc) throws IOException, XMLStreamException {
        SdoTestUtil.compareReaders(pDoc);
    }

    protected void buildRootProp(Type pType) {
        DataObject property = _helperContext.getDataFactory().create(
            URINamePair.PROPERTY.getURI(),
            URINamePair.PROPERTY.getName());
        property.set(PropertyConstants.NAME, pType.getName());
        property.set(PropertyConstants.TYPE, pType);
        property.set(PropertyConstants.CONTAINMENT, true);
        _helperContext.getTypeHelper().defineOpenContentProperty(pType.getURI(), property);
    }

    public static enum Feature {
        XML_STREAM_READER {
            @Override
            public SapXmlDocument testFeature(XMLDocument document, Map options) {
                if (document != null
                        && (options == null || !options.containsKey(IGNORE_FOR_FEATURE_TEST))
                        && !("com.sap.sdo.testcase.typefac".equals(document.getRootElementURI())
                                && "root".equals(document.getRootElementName())
                                && "MyDataGraphRootType".equals(document.getRootObject().getType().getName()))
                         && !("commonj.sdo".equals(document.getRootElementURI())
                                 && "type".equals(document.getRootElementName()))
                         && !("http://schemas.xmlsoap.org/wsdl/".equals(document.getRootElementURI())
                                 && "definitions".equals(document.getRootElementName()))) {

                    try {
                        SdoTestUtil.compareReaders(document);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
                return (SapXmlDocument)document;
            }
        },
        XML_READER {
            @Override
            public SapXmlDocument testFeature(XMLDocument document, Map options) {
                if (document != null
                        && (options == null || !options.containsKey(IGNORE_FOR_FEATURE_TEST))
                        && !(URINamePair.DATAGRAPH_TYPE.equalsUriName(document.getRootObject().getType()))
                        && !("com.sap.sdo.testcase.typefac".equals(document.getRootElementURI())
                                && "root".equals(document.getRootElementName())
                                && "MyDataGraphRootType".equals(document.getRootObject().getType().getName()))
                         && !("commonj.sdo".equals(document.getRootElementURI())
                                 && "type".equals(document.getRootElementName()))
                         && !("http://schemas.xmlsoap.org/wsdl/".equals(document.getRootElementURI())
                                 && "definitions".equals(document.getRootElementName()))) {

                    try {
                        SdoTestUtil.checkXmlReader(document, options);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
                return (SapXmlDocument)document;
            }
        },
        CONTENT_HANDLER {
            @Override
            public SapXmlDocument testFeature(XMLDocument document, Map options) {
                if (document != null
                        && (options == null || !options.containsKey(IGNORE_FOR_FEATURE_TEST))
                        && !("com.sap.sdo.testcase.typefac".equals(document.getRootElementURI())
                                && "root".equals(document.getRootElementName())
                                && "MyDataGraphRootType".equals(document.getRootObject().getType().getName()))
                         && !("commonj.sdo".equals(document.getRootElementURI())
                                 && "type".equals(document.getRootElementName()))
                         && !("http://schemas.xmlsoap.org/wsdl/".equals(document.getRootElementURI())
                                 && "definitions".equals(document.getRootElementName()))) {

                    try {
                        SdoTestUtil.checkContentHandler(document, options);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
                return (SapXmlDocument)document;
            }
        },
        SAX_PARSER {
            @Override
            public SapXmlDocument testFeature(XMLDocument document, Map options) {
                if (document != null
                        && (options == null || !options.containsKey(IGNORE_FOR_FEATURE_TEST))
                        && !("com.sap.sdo.testcase.typefac".equals(document.getRootElementURI())
                                && "root".equals(document.getRootElementName())
                                && "MyDataGraphRootType".equals(document.getRootObject().getType().getName()))
                         && !("commonj.sdo".equals(document.getRootElementURI())
                                 && "type".equals(document.getRootElementName()))
                         && !("http://schemas.xmlsoap.org/wsdl/".equals(document.getRootElementURI())
                                 && "definitions".equals(document.getRootElementName()))) {

                    try {
                        SdoTestUtil.checkContentHandler(document, options);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
                return (SapXmlDocument)document;
            }
        };
        public abstract SapXmlDocument testFeature(XMLDocument document, Map options);
    };

    static class DelegatingHelperContext extends HelperContextImpl implements SapHelperContext {
        private static final long serialVersionUID = -5926019795800317913L;
        private SapHelperContext _delegate;
        private Feature _feature;

        /**
         * @param pDelegate
         * @param pTest
         */
        private DelegatingHelperContext(SapHelperContext pDelegate, Feature pTest) {
            super(null, null, null);
            _delegate = pDelegate;
            _feature = pTest;
        }

        @Override
        public ClassLoader getClassLoader() {
            return _delegate.getClassLoader();
        }

        @Override
        public Object getContextOption(String pKey) {
            return _delegate.getContextOption(pKey);
        }

        @Override
        public CopyHelper getCopyHelper() {
            return _delegate.getCopyHelper();
        }

        @Override
        public DataFactory getDataFactory() {
            return _delegate.getDataFactory();
        }

        @Override
        public DataHelper getDataHelper() {
            return _delegate.getDataHelper();
        }

        @Override
        public EqualityHelper getEqualityHelper() {
            return _delegate.getEqualityHelper();
        }

        @Override
        public String getId() {
            return _delegate.getId();
        }

        @Override
        public HelperContextImpl getParent() {
            return (HelperContextImpl)_delegate.getParent();
        }

        @Override
        public TypeHelper getTypeHelper() {
            return _delegate.getTypeHelper();
        }

        @Override
        public XMLHelper getXMLHelper() {
            if (Feature.SAX_PARSER.equals(_feature)) {
                return SaxSourceDelegator.getInstance((SapXmlHelper)_delegate.getXMLHelper(), _feature);
            }
            return DelegatingXmlHelper.getInstance((SapXmlHelper)_delegate.getXMLHelper(), _feature);
        }

        @Override
        public XSDHelper getXSDHelper() {
            return _delegate.getXSDHelper();
        }

        @Override
        public boolean isAssignableContext(HelperContext pAssignableFrom) {
            return _delegate.isAssignableContext(pAssignableFrom);
        }

        @Override
        public void setContextOption(String pKey, Object pValue) {
            _delegate.setContextOption(pKey, pValue);
        }

        @Override
        public void setMappingStrategyProperty(Property pMappingProperty) {
            _delegate.setMappingStrategyProperty(pMappingProperty);
        }
    }

    static class DelegatingXmlHelper implements SapXmlHelper {
        private static final DelegatingXmlHelper INSTANCE = new DelegatingXmlHelper();
        private SapXmlHelper _delegate = null;
        private Feature _feature = null;

        private DelegatingXmlHelper() {
            super();
        }

        public static DelegatingXmlHelper getInstance(SapXmlHelper pXmlHelper, Feature pTest) {
            INSTANCE._delegate = pXmlHelper;
            INSTANCE._feature = pTest;
            return INSTANCE;
        }

        public XMLDocument createDocument(DataObject pDataObject,
            String pRootElementURI, String pRootElementName) {
            XMLDocument document =
                _delegate.createDocument(pDataObject, pRootElementURI, pRootElementName);
            return _feature.testFeature(document, null);
        }

        public XMLStreamReader createXMLStreamReader(XMLDocument pXmlDocument,
            Map<String, Object> pOptions) {
            return _delegate.createXMLStreamReader(
                _feature.testFeature(pXmlDocument, pOptions), pOptions);
        }

        public XMLReader createXMLReader(XMLDocument pXmlDocument,
            Map<String, Object> pOptions) {
            return _delegate.createXMLReader(
                _feature.testFeature(pXmlDocument, pOptions), pOptions);
        }

        public SapXmlDocument load(InputStream pInputStream,
            String pLocationURI, Object pOptions) throws IOException {
            return _feature.testFeature(
                _delegate.load(pInputStream, pLocationURI, pOptions), getOptions(pOptions));
        }

        public SapXmlDocument load(InputStream pInputStream) throws IOException {
            return _feature.testFeature(_delegate.load(pInputStream), null);
        }

        public SapXmlDocument load(Reader pInputReader, String pLocationURI,
            Object pOptions) throws IOException {
            return _feature.testFeature(
                _delegate.load(pInputReader, pLocationURI, pOptions), getOptions(pOptions));
        }

        public SapXmlDocument load(Source pInputSource, String pLocationURI,
            Object pOptions) throws IOException {
            return _feature.testFeature(
                _delegate.load(pInputSource, pLocationURI, pOptions), getOptions(pOptions));
        }

        public SapXmlDocument load(String pInputString) {
            return _feature.testFeature(_delegate.load(pInputString), null);
        }

        public Object load(XMLStreamReader pReader, Map pOptions)
            throws XMLStreamException {
            return _delegate.load(pReader, pOptions);
        }

        public Object load(XMLStreamReader pReader, String pXsdTypeUri,
            String pXsdTypeName, Map pOptions) throws XMLStreamException {
            return _delegate.load(pReader, pXsdTypeUri, pXsdTypeName, pOptions);
        }

        public void save(DataObject pDataObject, String pRootElementURI,
            String pRootElementName, OutputStream pOutputStream)
            throws IOException {
            _delegate.save(pDataObject, pRootElementURI, pRootElementName,
                pOutputStream);
        }

        public String save(DataObject pDataObject, String pRootElementURI,
            String pRootElementName) {
            return _delegate.save(pDataObject, pRootElementURI, pRootElementName);
        }

        public void save(Object pData, String pElementUri, String pElementName,
            String pXsdTypeUri, String pXsdTypeName, XMLStreamWriter pWriter,
            Map pOptions) throws IOException {
            _delegate.save(pData, pElementUri, pElementName, pXsdTypeUri,
                pXsdTypeName, pWriter, pOptions);
        }

        public void save(XMLDocument pXmlDocument, OutputStream pOutputStream,
            Object pOptions) throws IOException {
            _delegate.save(_feature.testFeature(pXmlDocument, getOptions(pOptions)), pOutputStream, pOptions);
        }

        public void save(XMLDocument pXmlDocument, Result pOutputResult,
            Object pOptions) throws IOException {
            _delegate.save(_feature.testFeature(pXmlDocument, getOptions(pOptions)), pOutputResult, pOptions);
        }

        public void save(XMLDocument pXmlDocument, Writer pOutputWriter,
            Object pOptions) throws IOException {
            _delegate.save(_feature.testFeature(pXmlDocument, getOptions(pOptions)), pOutputWriter, pOptions);
        }

        public SDOContentHandler createContentHandler(Object pOptions) {
            return _delegate.createContentHandler(pOptions);
        }

        @Override
        public SDOResult createSDOResult(Object pOptions) {
            return _delegate.createSDOResult(pOptions);
        }

        @Override
        public SDOSource createSDOSource(XMLDocument pXmlDocument,
            Object pOptions) {
            return _delegate.createSDOSource(pXmlDocument, pOptions);
        }

        private Map getOptions(Object pOptions) {
            if (pOptions instanceof Map) {
                return (Map)pOptions;
            }
            return null;
        }
    }

    static class SaxSourceDelegator implements SapXmlHelper {
        private static final SaxSourceDelegator INSTANCE = new SaxSourceDelegator();
        private SapXmlHelper _delegate = null;
        private Feature _feature = null;

        private SaxSourceDelegator() {
            super();
        }

        public static SaxSourceDelegator getInstance(SapXmlHelper pXmlHelper, Feature pTest) {
            INSTANCE._delegate = pXmlHelper;
            INSTANCE._feature = pTest;
            return INSTANCE;
        }

        public SDOContentHandler createContentHandler(Object pOptions) {
            return _delegate.createContentHandler(pOptions);
        }

        public XMLDocument createDocument(DataObject pDataObject,
            String pRootElementURI, String pRootElementName) {
            return _delegate.createDocument(pDataObject, pRootElementURI,
                pRootElementName);
        }

        public XMLReader createXMLReader(XMLDocument pXmlDocument,
            Map<String, Object> pOptions) {
            return _delegate.createXMLReader(pXmlDocument, pOptions);
        }

        public XMLStreamReader createXMLStreamReader(XMLDocument pXmlDocument,
            Map<String, Object> pOptions) {
            return _delegate.createXMLStreamReader(pXmlDocument, pOptions);
        }

        public SapXmlDocument load(InputStream pInputStream,
            String pLocationURI, Object pOptions) throws IOException {
            SAXSource source = new SAXSource(XML_READER, new InputSource(pInputStream));
            return _delegate.load(source, pLocationURI, pOptions);
        }

        public SapXmlDocument load(InputStream pInputStream) throws IOException {
            SAXSource source = new SAXSource(XML_READER, new InputSource(pInputStream));
            return _delegate.load(source, null, null);
        }

        public SapXmlDocument load(Reader pInputReader, String pLocationURI,
            Object pOptions) throws IOException {
            SAXSource source = new SAXSource(XML_READER, new InputSource(pInputReader));
            return _delegate.load(source, pLocationURI, pOptions);
        }

        public SapXmlDocument load(Source pInputSource, String pLocationURI,
            Object pOptions) throws IOException {
            return _delegate.load(pInputSource, pLocationURI, pOptions);
        }

        public SapXmlDocument load(String pInputString) {
            if (pInputString == null) {
                return null;
            }
            boolean version = false;
            boolean encoding = false;
            if (pInputString.startsWith("<?xml")) {
                version = true;
                if (pInputString.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")) {
                    encoding = true;
                }
            }
            SAXSource source = new SAXSource(XML_READER, new InputSource(new StringReader(pInputString)));
            try {
                SapXmlDocument doc = _delegate.load(source, null, null);
                if (!version) {
                    doc.setXMLVersion(null);
                }
                if (!encoding) {
                    doc.setEncoding(null);
                }
                doc.setXMLDeclaration(version || encoding);
                return doc;
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        public Object load(XMLStreamReader pReader, Map pOptions)
            throws XMLStreamException {
            return _delegate.load(pReader, pOptions);
        }

        public Object load(XMLStreamReader pReader, String pXsdTypeUri,
            String pXsdTypeName, Map pOptions) throws XMLStreamException {
            return _delegate.load(pReader, pXsdTypeUri, pXsdTypeName, pOptions);
        }

        public void save(DataObject pDataObject, String pRootElementURI,
            String pRootElementName, OutputStream pOutputStream)
            throws IOException {
            _delegate.save(pDataObject, pRootElementURI, pRootElementName,
                pOutputStream);
        }

        public String save(DataObject pDataObject, String pRootElementURI,
            String pRootElementName) {
            return _delegate.save(pDataObject, pRootElementURI,
                pRootElementName);
        }

        public void save(Object pData, String pElementUri, String pElementName,
            String pXsdTypeUri, String pXsdTypeName, XMLStreamWriter pWriter,
            Map pOptions) throws IOException {
            _delegate.save(pData, pElementUri, pElementName, pXsdTypeUri,
                pXsdTypeName, pWriter, pOptions);
        }

        public void save(XMLDocument pXmlDocument, OutputStream pOutputStream,
            Object pOptions) throws IOException {
            _delegate.save(pXmlDocument, pOutputStream, pOptions);
        }

        public void save(XMLDocument pXmlDocument, Result pOutputResult,
            Object pOptions) throws IOException {
            _delegate.save(pXmlDocument, pOutputResult, pOptions);
        }

        public void save(XMLDocument pXmlDocument, Writer pOutputWriter,
            Object pOptions) throws IOException {
            _delegate.save(pXmlDocument, pOutputWriter, pOptions);
        }

        @Override
        public SDOResult createSDOResult(Object pOptions) {
            return _delegate.createSDOResult(pOptions);
        }

        @Override
        public SDOSource createSDOSource(XMLDocument pXmlDocument,
            Object pOptions) {
            return _delegate.createSDOSource(pXmlDocument, pOptions);
        }
    }

    static class DefaultHelperContext implements SapHelperContext
    {
      public CopyHelper getCopyHelper() { return CopyHelper.INSTANCE; }
      public DataFactory getDataFactory() { return DataFactory.INSTANCE; }
      public DataHelper getDataHelper() { return DataHelper.INSTANCE; }
      public EqualityHelper getEqualityHelper() { return EqualityHelper.INSTANCE; }
      public TypeHelper getTypeHelper() { return TypeHelper.INSTANCE; }
      public XMLHelper getXMLHelper() { return XMLHelper.INSTANCE; }
      public XSDHelper getXSDHelper() { return XSDHelper.INSTANCE; }

      /* (non-Javadoc)
         * @see com.sap.sdo.api.helper.SapHelperContext#getClassLoader()
         */
        public ClassLoader getClassLoader() {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see com.sap.sdo.api.helper.SapHelperContext#getContextOption(java.lang.String)
         */
        public Object getContextOption(String key) {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see com.sap.sdo.api.helper.SapHelperContext#getId()
         */
        public String getId() {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see com.sap.sdo.api.helper.SapHelperContext#getParent()
         */
        public SapHelperContext getParent() {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see com.sap.sdo.api.helper.SapHelperContext#isAssignableContext(commonj.sdo.helper.HelperContext)
         */
        public boolean isAssignableContext(HelperContext assignableFrom) {
            // TODO Auto-generated method stub
            return false;
        }
        /* (non-Javadoc)
         * @see com.sap.sdo.api.helper.SapHelperContext#setContextOption(java.lang.String, java.lang.Object)
         */
        public void setContextOption(String key, Object value) {
            // TODO Auto-generated method stub

        }
        /* (non-Javadoc)
         * @see com.sap.sdo.api.helper.SapHelperContext#setMappingStrategyProperty(commonj.sdo.Property)
         */
        public void setMappingStrategyProperty(Property mappingProperty) {
            // TODO Auto-generated method stub

        }
    }

}
