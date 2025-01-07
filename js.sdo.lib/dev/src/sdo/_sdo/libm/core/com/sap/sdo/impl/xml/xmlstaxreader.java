package com.sap.sdo.impl.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sap.sdo.api.helper.ErrorHandler;
import com.sap.sdo.api.helper.NamingHandler;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.helper.SchemaResolver;
import com.sap.sdo.api.helper.Validator;
import com.sap.sdo.api.helper.util.SDOContentHandler;
import com.sap.sdo.api.types.SapType;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.ChangeSummaryImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.InternalDataObjectModifier;
import com.sap.sdo.impl.objects.OldStateDataObject;
import com.sap.sdo.impl.objects.DataStrategy.State;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeAndContext;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.ChangeSummaryType;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.DataObjectType;
import com.sap.sdo.impl.types.builtin.OpenType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.builtin.UndecidedType;
import com.sap.sdo.impl.types.schema.SchemaTypeFactory;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.util.Base64Util;
import com.sap.sdo.impl.util.DataObjectBehavior;
import com.sap.sdo.impl.xml.PathReference.PathStep;
import com.sap.sdo.impl.xml.sax.SdoContentHandlerImpl;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;


public class XmlStaxReader {

    private static final DefaultErrorHandler DEFAULT_ERROR_HANDLER = new DefaultErrorHandler();
    public static final URINamePair XSI_SCHEMA_LOCATION = new URINamePair(URINamePair.XSI_URI, "schemaLocation");
    public static final URINamePair XSI_NO_NAMESPACE_SCHEMA_LOCATION = new URINamePair(URINamePair.XSI_URI, "noNamespaceSchemaLocation");
    public static final URINamePair XSI_TYPE = new URINamePair(URINamePair.XSI_URI, "type");
    public static final URINamePair XSI_NIL = new URINamePair(URINamePair.XSI_URI, "nil");

    public static final URINamePair SDO_REF = new URINamePair(URINamePair.MODELTYPE_URI, "ref");
    public static final URINamePair SDO_UNSET = new URINamePair(URINamePair.MODELTYPE_URI, "unset");
    public static final URINamePair SDO_ID = new URINamePair(URINamePair.MODELTYPE_URI, "id"); //TODO remove that

    public static final Set<URINamePair> NO_CONTENT_ATTRIBUTES = new HashSet<URINamePair>();
    static {
        NO_CONTENT_ATTRIBUTES.add(XSI_SCHEMA_LOCATION);
        NO_CONTENT_ATTRIBUTES.add(XSI_NO_NAMESPACE_SCHEMA_LOCATION);
        NO_CONTENT_ATTRIBUTES.add(XSI_TYPE);
        NO_CONTENT_ATTRIBUTES.add(XSI_NIL);
        NO_CONTENT_ATTRIBUTES.add(SDO_REF);
        NO_CONTENT_ATTRIBUTES.add(SDO_UNSET);
        NO_CONTENT_ATTRIBUTES.add(SDO_ID);

    }

    private static enum ParseMode {XML, XSD, CHANGE_SUMMARY}

    private final SapHelperContext _helperContext;
    private final Type _stringType;
    private XMLStreamReader _parser;
    private String _rootElementURI;
    private String _rootElementName;
    private DataObjectDecorator _dataObject;
    private String _noNamespaceSchemaLocation;
    private final List<String> _nameSpaceLocationPairs = new ArrayList<String>();
    private String _encoding;
    private String _version;
    private List<XsdToTypesTranslator> _xsdToTypesTranslators;
    private ParseMode _parseMode = ParseMode.XML;
    private Map<Reference,DataObjectDecorator> _referenceToObject;
    Set<Reference> _createdReferences = Collections.emptySet();
    Set<Reference> _deletedReferences = Collections.emptySet();
    private SchemaResolver _schemaResolver;
    private String _defineSchemas = SapXmlHelper.OPTION_VALUE_AUTO;
    private String _defineXmlTypes = SapXmlHelper.OPTION_VALUE_AUTO;
    private Map<URINamePair, Type> _xmlTypes;
    final private Map _options = new HashMap();
    private String _locationUri;
    private final List<ChangeSummary> _loggingChangeSummaries = new ArrayList<ChangeSummary>();
    private final Map<ChangeSummary, List<DataObjectDecorator>> _deletedOrphans = new HashMap<ChangeSummary, List<DataObjectDecorator>>();
    private ErrorHandler _errorHandler = DEFAULT_ERROR_HANDLER;
    private NamingHandler _namingHandler = new DefaultRenamingHandler();
    private boolean _simplifyOpenContent = true;
    private final Map<PathReference, Integer> _orphanIndex = new HashMap<PathReference, Integer>();
    private final Map<Property,Integer> _orphansInCs = new HashMap<Property,Integer>();

    public XmlStaxReader(final SapHelperContext pHelperContext) {
        super();
        _helperContext = pHelperContext;
        _stringType = _helperContext.getTypeHelper().getType(URINamePair.STRING.getURI(), URINamePair.STRING.getName());
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.lang.String)
     */
    public SapXmlDocument load(final String pInputString) throws IOException {
        if (pInputString == null) {
            throw new XmlParseException("input must not be null.");
        }
        return load(new StringReader(pInputString), null, null);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.io.InputStream, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(final InputStream pInputStream, final String pLocationURI, final Map pOptions)
    throws IOException {
        try {
            final XMLStreamReader parser = XMLHelperImpl.XML_INPUT_FACTORY.createXMLStreamReader(pInputStream);
            return parse(parser, pLocationURI, pOptions);
        } catch (final XMLStreamException e) {
            final Throwable cause = e.getNestedException();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new XmlParseException(e);
        }
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.io.InputStream, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(final Reader pInputReader, final String pLocationURI, final Map pOptions)
    throws IOException {
        try {
            final XMLStreamReader parser = XMLHelperImpl.XML_INPUT_FACTORY.createXMLStreamReader(pInputReader);
            return parse(parser, pLocationURI, pOptions);
        } catch (final XMLStreamException e) {
            throw new XmlParseException(e);
        }
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.io.InputStream, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(final Source pInputSource, final String pLocationURI, final Map pOptions)
    throws IOException {
        try {
            XMLStreamReader parser = null;
            if (pInputSource instanceof StAXSource) {
                parser = ((StAXSource)pInputSource).getXMLStreamReader();
                if (parser != null) {
                    return parse(parser, pLocationURI, pOptions);
                }
            } else if (pInputSource instanceof StreamSource) {
                try {
                    parser = XMLHelperImpl.XML_INPUT_FACTORY.createXMLStreamReader(pInputSource);
                } catch (final XMLStreamException e) {
                    return loadFallback(pInputSource, pLocationURI, pOptions);
                } catch (final RuntimeException e) {
                    return loadFallback(pInputSource, pLocationURI, pOptions);
                }
            }
            if (parser == null) {
                return loadFallback(pInputSource, pLocationURI, pOptions);
            }
            return parse(parser, pLocationURI, pOptions);
        } catch (final XMLStreamException e) {
            throw new XmlParseException(e);
        }
    }

    private SapXmlDocument loadFallback(final Source pInputSource, final String pLocationURI, final Map pOptions) throws TransformerFactoryConfigurationError, IOException {
        // fallback for all the stupid parsers
        if (pInputSource instanceof StreamSource) {
            final StreamSource streamSource = (StreamSource)pInputSource;
            final Reader reader = streamSource.getReader();
            if (reader != null) {
                return load(reader, pLocationURI, pOptions);
            }
            final InputStream inputStream = streamSource.getInputStream();
            if (inputStream != null) {
                return load(inputStream, pLocationURI, pOptions);
            }
        } else if (pInputSource instanceof SAXSource) {
            SAXSource saxSource = (SAXSource)pInputSource;
            XMLReader xmlReader = saxSource.getXMLReader();
            if (xmlReader != null) {
                SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
                Map<String,Object> options = new HashMap<String,Object>();
                if (pOptions != null) {
                    options.putAll(pOptions);
                }
                if (pLocationURI != null) {
                    options.put(SdoContentHandlerImpl.OPTION_KEY_LOCATION_URI, pLocationURI);
                }
                SDOContentHandler contentHandler = xmlHelper.createContentHandler(options);
                xmlReader.setContentHandler(contentHandler);
                try {
                    xmlReader.parse(saxSource.getInputSource());
                } catch (SAXException e) {
                    throw new XmlParseException(e);
                }
                return (SapXmlDocument)contentHandler.getDocument();
            } else  {
                final InputSource inputSource = saxSource.getInputSource();
                final Reader reader = inputSource.getCharacterStream();
                if (reader != null) {
                    return load(reader, pLocationURI, pOptions);
                }
                final InputStream inputStream = inputSource.getByteStream();
                if (inputStream != null) {
                    return load(inputStream, pLocationURI, pOptions);
                }
            }
        }
        try {
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
            SDOContentHandler contentHandler = xmlHelper.createContentHandler(pOptions);
            final SAXResult saxResult = new SAXResult(contentHandler);
            transformer.transform(pInputSource, saxResult);
            return (SapXmlDocument)contentHandler.getDocument();
        } catch (final TransformerException ex) {
            throw new XmlParseException(ex);
        }
    }

    private SapXmlDocument parse(final XMLStreamReader parser, final String pLocationURI, final Map pOptions) throws XMLStreamException, IOException {
        Validator validator = null;
        if (pOptions != null) {
            _options.putAll(pOptions);
            _schemaResolver = (SchemaResolver)_options.get(SapXmlHelper.OPTION_KEY_SCHEMA_RESOLVER);
            final Object defineSchemas = _options.get(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS);
            if (SapXmlHelper.OPTION_VALUE_FALSE.equals(defineSchemas) ||
                SapXmlHelper.OPTION_VALUE_TRUE.equals(defineSchemas) ||
                SapXmlHelper.OPTION_VALUE_AUTO.equals(defineSchemas)) {
                _defineSchemas = (String)defineSchemas;
            }
            final Object defineXmlTypes = _options.get(SapXmlHelper.OPTION_KEY_DEFINE_XML_TYPES);
            if (SapXmlHelper.OPTION_VALUE_FALSE.equals(defineXmlTypes) ||
                SapXmlHelper.OPTION_VALUE_TRUE.equals(defineXmlTypes) ||
                SapXmlHelper.OPTION_VALUE_AUTO.equals(defineXmlTypes)) {
                _defineXmlTypes = (String)defineXmlTypes;
            }
            final Object errorHandler = _options.get(SapXmlHelper.OPTION_KEY_ERROR_HANDLER);
            if (errorHandler instanceof ErrorHandler) {
                _errorHandler = (ErrorHandler)errorHandler;
            }
            final Object namingHandler = _options.get(SapXmlHelper.OPTION_KEY_NAMING_HANDLER);
            if (namingHandler instanceof NamingHandler) {
                _namingHandler = (NamingHandler)namingHandler;
            }
            _simplifyOpenContent = !SapXmlHelper.OPTION_VALUE_FALSE.equals(
                _options.get(SapXmlHelper.OPTION_KEY_SIMPLIFY_OPEN_CONTENT));
            validator = (Validator)_options.get(SapXmlHelper.OPTION_KEY_VALIDATOR);
        }
        _locationUri = pLocationURI;

        loadDocument(parser);

        for (final Reference ref : _createdReferences) {
            resolvePathReference(ref).getInternalModifier().setChangeStateWithoutCheck(State.CREATED);
        }

        for (final ChangeSummary changeSummary: _loggingChangeSummaries) {
            ((ChangeSummaryImpl)changeSummary).beginLoggingWithoutCheck();
        }
        for (Entry<ChangeSummary, List<DataObjectDecorator>> entry : _deletedOrphans.entrySet()) {
            ((ChangeSummaryImpl)entry.getKey()).addDeletedOrphansWithoutCheck(entry.getValue());
        }

        if (validator != null) {
            try {
                validator.validate(_dataObject);
            } catch (final IllegalArgumentException e) {
                throw new XmlParseException(e);
            }
        }

        final XMLDocumentImpl xmlDocument = new XMLDocumentImpl(_dataObject, _rootElementURI, _rootElementName);
        xmlDocument.setNoNamespaceSchemaLocation(_noNamespaceSchemaLocation);
        xmlDocument.setSchemaLocation(getSchemaLocation());
        xmlDocument.setXMLDeclaration((_encoding != null) || (_version != null));
        xmlDocument.setEncoding(_encoding);
        xmlDocument.setXMLVersion(_version);
        xmlDocument.setXsdToTypesTranslators(_xsdToTypesTranslators);
        xmlDocument.setXmlTypes(getXmlTypes());
        return xmlDocument;
    }

    public void loadDocument(final XMLStreamReader pParser) throws XMLStreamException {
        _parser = pParser;
        if (_parser.getEventType() != XMLStreamConstants.START_DOCUMENT) {
            throw new IllegalStateException("Parser is not at start document");
        }

        while (_parser.next() != XMLStreamConstants.START_ELEMENT) {
            if (_parser.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                throw new IllegalArgumentException("File contains no Element");
            }
        }
        _encoding = _parser.getCharacterEncodingScheme();
        _version = _parser.getVersion();

        _rootElementURI = _parser.getNamespaceURI();
        _rootElementName = _parser.getLocalName();
        parseElement(null, new PathReference());
        _dataObject = _dataObject.getInstance().getFacade();
        if (_defineSchemas.equals(SapXmlHelper.OPTION_VALUE_TRUE)) {
            defineSchemas();
        }
//        _parser.next();
//        if (_parser.isWhiteSpace()) {
//            _parser.next();
//        }
//        if (_parser.getEventType() != XMLStreamConstants.END_DOCUMENT) {
//            throw new IllegalStateException("Parser is not at end document:  parser state:" + _parser.getEventType());
//        }
    }

    public Object loadDocumentPart(final XMLStreamReader pParser, final Map pOptions) throws XMLStreamException {
        _parser = pParser;
        if (pOptions != null) {
            final Object errorHandler = pOptions.get(SapXmlHelper.OPTION_KEY_ERROR_HANDLER);
            if (errorHandler instanceof ErrorHandler) {
                _errorHandler = (ErrorHandler)errorHandler;
            }
        }
        return parseElement(null, new PathReference(), true);
    }

    public Object loadDocumentPart(final XMLStreamReader pParser, final String pXsdTypeUri, final String pXsdTypeName, final Map pOptions) throws XMLStreamException {
        _parser = pParser;
        if (pOptions != null) {
            final Object errorHandler = pOptions.get(SapXmlHelper.OPTION_KEY_ERROR_HANDLER);
            if (errorHandler instanceof ErrorHandler) {
                _errorHandler = (ErrorHandler)errorHandler;
            }
        }
        final SdoType type = (SdoType)((TypeHelperImpl)_helperContext.getTypeHelper()).getTypeByXmlName(pXsdTypeUri, pXsdTypeName);
        if (type == null) {
            throw new XMLStreamException("Type " + pXsdTypeUri + '#' + pXsdTypeName + " is unknown in HelperContext");
        }
        final String nil = getAttributeValue(XSI_NIL);
        if (Boolean.parseBoolean(nil)) {
            while (_parser.next() != XMLStreamConstants.END_ELEMENT) {
                if (!_parser.isWhiteSpace()) {
                    throw new IllegalArgumentException("xsi:nil element must be empty");
                }
            }
            return null;
        }
        if (type.isDataType()) {
            final String textValue = _parser.getElementText();
            if (URINamePair.SCHEMA_BASE64BINARY.equalsUriName(pXsdTypeUri, pXsdTypeName)) {
                return Base64Util.decodeBase64(textValue);
            } else if (URINamePair.SCHEMA_Q_NAME.equalsUriName(pXsdTypeUri, pXsdTypeName)) {
                final URINamePair qName = URINamePair.getQNameFromString(textValue);
                qName.setURI(_parser.getNamespaceURI(qName.getURI()));
                return qName.toStandardSdoFormat();
            } else {
                return type.convertFromJavaClass(textValue);
            }
        } else {
            final PathReference pathReference = new PathReference(new PathStep(_parser.getNamespaceURI(), _parser.getLocalName(), 0));
            final PropertyAndType propType = new PropertyAndType();
            propType._type = type;
            return parseDataObject(null, propType, pathReference);
        }
    }

    private Object parseElement(final DataObjectDecorator pParentDo, final PathReference pParentPathReference)
    throws XMLStreamException {
        return parseElement(pParentDo, pParentPathReference, false);
    }

    private Object parseElement(final DataObjectDecorator pParentDo, final PathReference pParentPathReference, final boolean pSimpleContent) throws XMLStreamException {
        boolean schemaElementInXml = false;
        if ((_parseMode == ParseMode.XML) && URINamePair.PROP_SCHEMA_SCHEMA.equalsUriName(_parser.getNamespaceURI(), _parser.getLocalName())) {
            // if a schema element is found while parsing xml
            _parseMode = ParseMode.XSD;
            schemaElementInXml = true;
        }
        if (_parseMode == ParseMode.XML) {
            parseSchemaLocation();
        }
        final PropertyAndType propType = getPropertyAndType(pParentDo, pParentPathReference);

        Object value = null;
        if (propType._type == ChangeSummaryType.getInstance()) {
            PathReference pathReference;
            if (pParentDo.getType() == DataGraphType.getInstance()) {
                pathReference = getPathReference(pParentDo, propType._property, new PathReference());
            } else {
                pathReference = getPathReference(pParentDo, propType._property, pParentPathReference);
            }
            final boolean logging =
                !Boolean.FALSE.toString().equals(
                    _parser.getAttributeValue(null, ChangeSummaryType.LOGGING));
            ChangeSummary changeSummary = pParentDo.getChangeSummary();
            parseChangeSummary(pathReference, changeSummary);
            if (logging) {
                //TODO collecting the change summaries and starting them later
                // avoids a bug at getPropValue, that modifies the DataObject in some special cases
                _loggingChangeSummaries.add(changeSummary);
            }
        } else {
            boolean nil = Boolean.parseBoolean(getAttributeValue(XSI_NIL));
            SdoProperty valueProperty = TypeHelperImpl.getSimpleContentProperty(propType._type);
            final SdoType propertyType = (SdoType)propType._property.getType();
            if (propType._type.isDataType() ||
                ((valueProperty != null) && (propType._property.isContainment() || propertyType.isDataType()))) {
                if (!propertyType.isDataType()) {
                    final PathReference pathReference = getPathReference(pParentDo, propType._property, pParentPathReference);
                    final DataObjectDecorator wrapper;
                    if (valueProperty != null) {
                        wrapper = getOrCreateDataObject(propType._type, pathReference, false, false, null);
                    } else {
                        wrapper = getOrCreateDataObject(OpenType.getInstance(), pathReference, false, false, null);
                        valueProperty = getOrCreateSimpleContentProperty(wrapper, propType);
                    }
                    parseAttributeProperties(wrapper, propType._type, pathReference);
                    final Object simpleObject;
                    if (nil) {
                        simpleObject = null;
                    } else {
                        simpleObject = parseSimpleObject(valueProperty, (SdoType<?>)valueProperty.getType());
                    }
                    try {
                        wrapper.getInternalModifier().addPropertyValueWithoutCheck(
                            valueProperty,
                            simpleObject);
                    } catch (final RuntimeException ex) {
                        // empty string tolerant
                        if (!"".equals(simpleObject) || _errorHandler != DEFAULT_ERROR_HANDLER) {
                            _errorHandler.handleInvalidValue(ex);
                        }
                    }
                    wrapper.getInstance().setXsiNilWithoutCheck(nil);
                    value = addPropertyValue(pParentDo, propType, wrapper, pSimpleContent);
                } else {
                    if (!nil) {
                        value = parseSimpleObject(propType._property, propType._type);
                    }
                    if (pParentDo == null) {
                        try {
                        //convert the value because there is no parent DataObject to do it
                        value = propType._type.convertFromJavaClass(value);
                        } catch (final RuntimeException ex) {
                            // empty string tolerant
                            if (!"".equals(value) || _errorHandler != DEFAULT_ERROR_HANDLER) {
                                _errorHandler.handleInvalidValue(ex);
                            }
                            value = null;
                        }
                    }
                    value = addPropertyValue(pParentDo, propType, value, pSimpleContent);
                }
            } else if (!nil) {
                if (propType._property.isContainment() || pParentDo == null) {
                    PathReference pathReference;
                    if (propType._type == DataGraphType.getInstance()) {
                        pathReference = new PathReference();
                    } else {
                        pathReference = getPathReference(pParentDo, propType._property, pParentPathReference);
                    }
                    final DataObjectDecorator valueDO = parseDataObject(pParentDo, propType, pathReference);
                    final Property opposite = propType._property.getOpposite();
                    if (opposite != null) {
                        valueDO.getInternalModifier().setPropertyWithoutCheck(opposite, pParentDo);
                    }
                    valueDO.getInstance().setXsiNilWithoutCheck(nil);
                    value = valueDO;
                } else {
                    final PathReference pathReference = getPathReference(pParentDo, propType._property, pParentPathReference);
                    value = parseElementReference(pParentDo, pathReference, propType);
                }
            } else if (propType._property.isContainment()) {
                final PathReference pathReference = getPathReference(pParentDo, propType._property, pParentPathReference);
                final DataObjectDecorator dataObject = getOrCreateDataObject(propType._type, pathReference, true, false, null);
                parseAttributeProperties(dataObject, propType._type, pathReference);
                if (_simplifyOpenContent && _parseMode == ParseMode.XML) {
                    dataObject.getInstance().simplifyOpenContent();
                }
                // in this case is nil == true
                dataObject.getInstance().setXsiNilWithoutCheck(true);
                if (propType._property.isOrphanHolder()) {
                    referenceToObjectPut(pathReference, dataObject);
                    value = dataObject;
                } else {
                	value = addPropertyValue(pParentDo, propType, dataObject, pSimpleContent);
                }
            } else {
                value = addPropertyValue(pParentDo, propType, null, pSimpleContent);
            }
            if (nil) {
                while (_parser.next() != XMLStreamConstants.END_ELEMENT) {
                    if (!_parser.isWhiteSpace()) {
                        throw new IllegalArgumentException("xsi:nil element must be empty");
                    }
                }
            }
        }
        if (schemaElementInXml) {
            final Schema schema = (Schema)value;
            final XsdToTypesTranslator xsdToTypesTranslator = getUndefinedXsdToTypesTranslator();
            xsdToTypesTranslator.addSchema(new SchemaLocation(schema.getTargetNamespace(), _locationUri), schema);
            _parseMode = ParseMode.XML;
        }
        return value;
    }

    private Object parseElementReference(final DataObjectDecorator pParentDo, final PathReference pParentPathReference, final PropertyAndType propType) throws XMLStreamException {
        if (propType._type == TypeType.getInstance()) {
            final URINamePair uriName = URINamePair.fromStandardSdoFormat(_parser.getElementText());
            return addPropertyValue(
                pParentDo, propType, createTypeDummy(uriName), false);
        }
        Reference reference = null;
        SdoType keyType = (SdoType)propType._type.getKeyType();
        if (keyType != null && !keyType.isDataType()) {
            DataObjectDecorator keyDo = (DataObjectDecorator)_helperContext.getDataFactory().create(keyType);
            parseAttributeProperties(keyDo, keyType, pParentPathReference);
            StringBuilder ref = null;
            while(_parser.next() != XMLStreamConstants.END_ELEMENT) {
                if (_parser.isStartElement()) {
                    parseElement(keyDo, pParentPathReference);
                } else if (_parser.isCharacters() && !_parser.isWhiteSpace()) {
                    if (ref == null) {
                        ref = new StringBuilder();
                    }
                    ref.append(_parser.getText());
                } else if (_parser.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                    throw new IllegalArgumentException("Unexpected end of file");
                }
            }
            //Check if all key-props are set
            boolean allKeysSet = isKeyComplete(keyDo);
            if (allKeysSet) {
                reference = new Key(propType._type.getTypeForKeyUniqueness(), keyDo);
            } else if (ref != null) {
                reference = Reference.createReference(pParentPathReference, propType._type, ref.toString(), getNamespaceContext());
            }
        } else {
            reference = Reference.createReference(pParentPathReference, propType._type, _parser.getElementText(), getNamespaceContext());
        }
        DataObjectDecorator referencedDo = reference==null?null:
            getOrCreateDataObject(propType._type, reference, false, true, null);

        return addPropertyValue(pParentDo, propType, referencedDo, false);
    }

    private boolean isKeyComplete(DataObject keyDo) {
        List<SdoProperty> keyProperties = keyDo.getType().getProperties();
        for (int i = 0; i < keyProperties.size(); i++) {
            if (!keyDo.isSet(keyProperties.get(i))) {
                return false;
            }
        }
        return true;
    }

    private Object addPropertyValue(final DataObjectDecorator pParentDo,
        final PropertyAndType propType, Object value, boolean pSimpleContent) {
        if (!propType._skipData) {
            if (pParentDo != null) {
                try {
                    pParentDo.getInternalModifier().addPropertyValueWithoutCheck(propType._property, value);
                } catch (final IllegalStateException ex) {
                    Property globalProperty = null;
                    if (pParentDo.getType().isOpen() && !propType._property.isMany() && !propType._property.isOpenContent()) {
                        globalProperty = _helperContext.getXSDHelper()
                            .getGlobalProperty(propType._property.getUri(), propType._property.getXmlName(), true);
                    }
                    if (globalProperty == null) {
                        _errorHandler.handleInvalidValue(ex);
                    } else {
                        try {
                            pParentDo.getInternalModifier().addPropertyValueWithoutCheck(globalProperty, value);
                        } catch (final RuntimeException ex2) {
                            _errorHandler.handleInvalidValue(ex2);
                        }
                    }
                } catch (final RuntimeException ex) {
                    _errorHandler.handleInvalidValue(ex);
                }
            } else if (!pSimpleContent && propType._type.isDataType()) {
                final DataObjectDecorator data =
                    (DataObjectDecorator)_helperContext.getDataFactory().create(OpenType.getInstance());
                data.set(getOrCreateSimpleContentProperty(data, propType), value);
                value = data;
            }
        }
        if (_dataObject == null && value instanceof DataObjectDecorator) {
            _dataObject = (DataObjectDecorator)value;
        }
        return value;
    }

    private XsdToTypesTranslator getUndefinedXsdToTypesTranslator() {
        XsdToTypesTranslator xsdToTypesTranslator = getLastXsdToTypesTranslator();
        if ((xsdToTypesTranslator != null) && xsdToTypesTranslator.isDefined()) {
            xsdToTypesTranslator = null;
        }
        if (xsdToTypesTranslator == null) {
            xsdToTypesTranslator = new XsdToTypesTranslator(_helperContext, _options);
            if (_xsdToTypesTranslators == null) {
                _xsdToTypesTranslators = new ArrayList<XsdToTypesTranslator>();
            }
            _xsdToTypesTranslators.add(xsdToTypesTranslator);
        }
        return xsdToTypesTranslator;
    }

    private XsdToTypesTranslator getLastXsdToTypesTranslator() {
        if ((_xsdToTypesTranslators == null) || _xsdToTypesTranslators.isEmpty()) {
            return null;
        }
        return _xsdToTypesTranslators.get(_xsdToTypesTranslators.size() - 1);
    }


    private SchemaResolver getSchemaResolver() {
        if (_schemaResolver == null) {
            //get the SchemaResolver from the HelperContext
            _schemaResolver = ((SapXsdHelper)_helperContext.getXSDHelper()).getDefaultSchemaResolver();
            _options.put(SchemaResolver.class.getName(), _schemaResolver);
        }
        return _schemaResolver;
    }

    private SdoProperty getOrCreateSimpleContentProperty(final DataObjectDecorator pDataObject, final PropertyAndType pPropType) {
        SdoProperty valueProp = (SdoProperty)pDataObject.getInternalModifier().findOpenProperty("", TypeType.VALUE, true);
        if (valueProp == null
                || !((DataObject)valueProp).getBoolean(PropertyType.getSimpleContentProperty())) {
            final DataObject propObj = _helperContext.getDataFactory().create(PropertyType.getInstance());
            propObj.setString(PropertyType.NAME, TypeType.VALUE);
            propObj.set(PropertyType.TYPE, pPropType._type);
            propObj.setBoolean(PropertyType.MANY, false);
            propObj.setBoolean(PropertyType.getXmlElementProperty(), true);
            propObj.setBoolean(PropertyType.getSimpleContentProperty(),true);

            if (pPropType._xsdType != null) {
                // the type is explicitly set
                final URINamePair defaultXsdType = SchemaTypeFactory.getInstance().getXsdName(pPropType._type.getQName());
                if (!pPropType._xsdType.equals(defaultXsdType)) {
                    propObj.setString(PropertyType.getXsdTypeProperty(), pPropType._xsdType.toStandardSdoFormat());
                }
            }

            valueProp = (SdoProperty)_helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        }
        return valueProp;
    }

    /**
     * Calculates the new PathReference from the parent PathReference and the
     * current parser state. Parent DataObject and Property are necessary to
     * calculate the index of the current element.
     * @param pParentDo The parent DataObject.
     * @param pProperty The current Property.
     * @param pParentPath The parent path.
     * @return The new path.
     */
    private PathReference getPathReference(final DataObject pParentDo, final SdoProperty pProperty, final PathReference pParentPath) {
        int index = 0;
        if ((pProperty.isMany()) && (pParentDo != null)) {
            if (pProperty.isOrphanHolder()) {
                PathStep step = new PathStep(pProperty.getUri(), pProperty.getXmlName(), 0);
                PathReference reference = new PathReference(pParentPath, step);
                Integer i = _orphanIndex.get(reference);
                if (i != null) {
                    index = i;
                    _orphanIndex.put(reference, ++i);
                } else {
                    _orphanIndex.put(reference, 1);
                }
            } else {
                final List<?> existingValues = pParentDo.getList(pProperty);
            if (existingValues != null) {
                index = existingValues.size();
            }
        }
        }
        final PathStep step = new PathStep(pProperty.getUri(), pProperty.getXmlName(), index);
        return new PathReference(pParentPath, step);
    }

    /**
     * Determines the Property and the Type of the parsers current element
     * without changing the parser state. The parent DataObject is used as a help
     * to find these information.
     * @param pParentDo The parent DataObject if known or null.
     * @return The Property and Type.
     */
    private PropertyAndType getPropertyAndType(final DataObjectDecorator pParentDo, final Reference pParentPathReference) throws XMLStreamException {
        final PropertyAndType propType = new PropertyAndType();
        final String localName = _parser.getLocalName();
        String ns = _parser.getNamespaceURI();
        if (ns == null) {
            ns = "";
        }
        SdoType parentType = null;
        if (pParentDo != null) {
            parentType = (SdoType)pParentDo.getInstance().getType();
            propType._property = parentType.getPropertyFromXmlName(ns, localName, true);
        }

        if (propType._property == null) {
            propType._property = (SdoProperty)((TypeHelperImpl)_helperContext.getTypeHelper()).getOpenContentPropertyByXmlName(ns, localName, true);
            if ((propType._property != null) && (parentType != null)) {
                // look for a substituted property
                for (final SdoProperty prop: (List<SdoProperty>)parentType.getProperties()) {
                    final URINamePair ref = prop.getRef();
                    if (ref != null) {
                        final Property headProp = _helperContext.getXSDHelper().getGlobalProperty(ref.getURI(), ref.getName(), true);
                        if (headProp != null && ((DataObject)headProp).isSet(PropertyType.getSubstitutesProperty())) {
                            for (final Object uriObj: ((DataObject)headProp).getList(PropertyType.getSubstitutesProperty())) {
                                final URINamePair substUNP = URINamePair.fromStandardSdoFormat((String) uriObj);
                                if (ns.equals(substUNP.getURI()) && localName.equals(substUNP.getName())) {
                                    propType._type = (SdoType)propType._property.getType();
                                    propType._property = prop;
                                    break;
                                }
                            }
                            if (propType._type != null) {
                                // type was null before, this means a substituted property was found
                                break;
                            }
                        }
                    }
                }
            }
            if (propType._property == null) {
                if (pParentDo != null) {
                    propType._property = (SdoProperty)pParentDo.getInternalModifier().findOpenProperty(ns, localName, true);
                    final DataObject propObj = (DataObject)propType._property;
                    if ((propObj != null) && !propObj.isSet(PropertyType.getXmlElementProperty())) {
                        propObj.setBoolean(PropertyType.getXmlElementProperty(), true);
                        propType._property = (SdoProperty)_helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
                    }
                }
                if ((propType._property == null) && (_parseMode == ParseMode.CHANGE_SUMMARY) && !pParentPathReference.isKeyReference()) {
                    final String ref = getAttributeValue(SDO_REF);
                    if (ref != null) {
                        Type rootType = null;
                        if (_dataObject != null) {
                            rootType = _dataObject.getType();
                        }
                        Reference reference = Reference.createReference((PathReference)pParentPathReference, null, ref, getNamespaceContext());
                        if (reference != null && !reference.isKeyReference()) {
                            propType._property = (SdoProperty)getPropertyFromPath((PathReference)reference, rootType);
                        }
                    }
                }
            }
        }
        if ((propType._property != null)) {
            // the type is taken from the property
            if (propType._type == null) {
                propType._type = (SdoType)propType._property.getType();
            }
        }
        final String explicitType = getAttributeValue(XSI_TYPE);
        if (explicitType != null) {
            // the type is explicitly set
            final URINamePair qName = URINamePair.getQNameFromString(explicitType);
            final String namespaceURI = _parser.getNamespaceURI(qName.getURI());
            if (namespaceURI != null) {
                qName.setURI(namespaceURI);
            }
            propType._type = (SdoType)((TypeHelperImpl)_helperContext.getTypeHelper()).getTypeByXmlName(qName.getURI(), qName.getName());
            if ((propType._property == null || propType._property.isNullable()) && propType._type instanceof JavaSimpleType) {
                propType._type = ((JavaSimpleType)propType._type).getNillableType();
            }
            if (!qName.equalsUriName(propType._type)) {
                propType._xsdType = qName;
            }
        }

        //TODO Fall back hack remove this!
        if (propType._type == null && pParentDo == null) {
            propType._type = (SdoType)_helperContext.getTypeHelper().getType(ns, localName);
        }

        if ((propType._type == null) && !_defineSchemas.equals(SapXmlHelper.OPTION_VALUE_FALSE )
            && (_parseMode != ParseMode.XSD)) {
            // if type is unknown and there where schemas in this XML
            // translate and define the schemas and try again to find the type
            if (defineSchemas()) {
                return getPropertyAndType(pParentDo, pParentPathReference);
            }
        }

        if (propType._type == null  || propType._type == DataObjectType.getInstance()) {
            propType._type = UndecidedType.getInstance();
        }
        if (propType._property == null) {
            if ((parentType != null) && !parentType.isOpen()) {
                // use errorHandler to decide if exception should be thrown
                _errorHandler.handleUnknownProperty(
                    new IllegalArgumentException(
                        "Type " + parentType.getQName().toStandardSdoFormat() + " is not open. " +
                        "Property '" + localName + "' is not defined."));

                // exception wasn't thrown, let's ignore this property
                propType._skipData = true;
            }
            final DataObject propObj = _helperContext.getDataFactory().create(PropertyType.getInstance());
            if (_simplifyOpenContent) {
                propObj.setBoolean(PropertyType.getManyUnknownProperty(), true);
            }
            propObj.set(PropertyType.MANY, true);
            propObj.set(PropertyType.TYPE, DataObjectType.getInstance());
            propObj.set(PropertyType.CONTAINMENT, true);
            propObj.set(PropertyType.getXmlElementProperty(), true);
            if (_parser.getPrefix() != null) {
                final URINamePair ref = new URINamePair(ns, localName);
                // that forces, that the prefix is rendered again
                propObj.setString(PropertyType.getReferenceProperty(), ref.toStandardSdoFormat());
            }
            _namingHandler.nameOpenContentProperty(propObj, ns, localName, true, pParentDo);
            propType._property = (SdoProperty)_helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        }
        return propType;
    }

    private boolean defineSchemas() throws XMLStreamException {
        // only the last XsdToTypesTranslator could be not defined
        final XsdToTypesTranslator xsdToTypesTranslator = getLastXsdToTypesTranslator();
        if ((xsdToTypesTranslator != null) && !xsdToTypesTranslator.isDefined()) {
            try {
                xsdToTypesTranslator.defineTypes();
            } catch (final IOException e) {
                throw new XMLStreamException(e.getMessage(), e);
            }
            return true;
        }
        return false;
    }

    private void parseChangeSummary(final PathReference pCSPathReference, final ChangeSummary pChangeSummary) throws XMLStreamException {
        _createdReferences = getReferences(ChangeSummaryType.CREATE, pCSPathReference, _createdReferences);
        _deletedReferences = getReferences(ChangeSummaryType.DELETE, pCSPathReference, _deletedReferences);
        _deletedOrphans.put(pChangeSummary, new ArrayList<DataObjectDecorator>());
        _orphansInCs.clear();
        if (!_createdReferences.isEmpty() || !_deletedReferences.isEmpty()) {
            pChangeSummary.endLogging();
        }
        _parseMode = ParseMode.CHANGE_SUMMARY;
        final DataObjectDecorator csDummyDo = (DataObjectDecorator)_helperContext.getDataFactory().create(UndecidedType.getInstance());
        while(_parser.next() != XMLStreamConstants.END_ELEMENT) {
            if (_parser.isStartElement()) {
                parseCSRootElement(csDummyDo, pCSPathReference, pChangeSummary);
            } else if (_parser.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                throw new IllegalArgumentException("Unexpected end of file");
            }
        }
        _parseMode = ParseMode.XML;

    }

    public Object parseCSRootElement(final DataObjectDecorator pCSDummyDo, final PathReference pParentCSPathReference, final ChangeSummary pChangeSummary) throws XMLStreamException {

        pChangeSummary.endLogging();
        final PropertyAndType propType = getPropertyAndType(pCSDummyDo, pParentCSPathReference);

        boolean deletedOrphan = false;
        final Reference reference;
        final String refAttribute = getAttributeValue(SDO_REF);
        // tracking indexes used for deleted orphans only
        Integer index = _orphansInCs.get(propType._property);
        if (index == null) {
            index = 0;
        }
        _orphansInCs.put(propType._property, index+1);
        if (refAttribute != null) {
            reference = Reference.createReference(pParentCSPathReference, propType._type, refAttribute, getNamespaceContext());
        } else {
            // found deleted orphan
            deletedOrphan = true;
            PathStep step = new PathStep(propType._property.getUri(), propType._property.getXmlName(), index);
            reference = new PathReference(pParentCSPathReference, step);
        }
        if (reference == null) {
            throw new IllegalArgumentException("No reference in ChangeSummary at " + pParentCSPathReference);
        }
        final PathReference csPathReference = getPathReference(pCSDummyDo, propType._property, pParentCSPathReference);
        SdoProperty valueProp = null;

        DataObjectDecorator elementDo;
        if (propType._type.isDataType()) {
            valueProp = TypeHelperImpl.getSimpleContentProperty(propType._property.getType());
            if (valueProp != null) {
                elementDo = getOrCreateDataObject((SdoType)propType._property.getType(), reference, false, true, null);
            } else {
                elementDo = getOrCreateDataObject(OpenType.getInstance(), reference, false, true, null);
                valueProp = getOrCreateSimpleContentProperty(elementDo, propType);
            }
        } else {
            valueProp = TypeHelperImpl.getSimpleContentProperty(propType._type);
            elementDo = getOrCreateDataObject(propType._type, reference, true, true, null);
        }

        if (deletedOrphan) {
            elementDo = elementDo.getInstance();
            List<DataObjectDecorator> orphans = _deletedOrphans.get(pChangeSummary);
            if (orphans != null) {
                orphans.add(elementDo);
            }
        } else {
            elementDo.getInternalModifier().setChangeStateWithoutCheck(State.MODIFIED);
        }

        parseUnsetProperties(elementDo);
        parseAttributeProperties(elementDo, propType._type, csPathReference);
        if (valueProp != null) {
            final Object value = parseSimpleObject(valueProp, (SdoType<?>)valueProp.getType());
            elementDo.getInternalModifier().addPropertyValueWithoutCheck(valueProp, value);
        } else {
            while(_parser.next() != XMLStreamConstants.END_ELEMENT) {
                if (_parser.isStartElement()) {
                    final String ref = getAttributeValue(SDO_REF);
                    if (ref != null) {
                        PropertyAndType elementPropType = getPropertyAndType(elementDo, reference);
                        parseCSReference(
                            elementDo,
                            Reference.createReference(csPathReference, elementPropType._type, ref, getNamespaceContext()),
                            elementPropType);
                    } else {
                        parseElement(elementDo, csPathReference);
                    }
                } if (_parser.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                    throw new IllegalArgumentException("Unexpected end of file");
                }
            }
        }

        return elementDo;
    }

    public DataObject parseCSReference(final DataObjectDecorator pCSParentDo, final Reference pReference) throws XMLStreamException {
        final PropertyAndType propType = getPropertyAndType(pCSParentDo, pReference);
        return parseCSReference(pCSParentDo, pReference, propType);
    }

    private DataObject parseCSReference(final DataObjectDecorator pCSParentDo, final Reference pReference, final PropertyAndType propType) throws XMLStreamException {
        final DataObjectDecorator referencedDo = getOrCreateDataObject(propType._type, pReference, false, true, null);
        while(_parser.next() != XMLStreamConstants.END_ELEMENT) {
            if (_parser.isStartElement()) {
                throw new IllegalArgumentException("ChangeSummary reference to " + pReference + " must not have sub-elements");
            } else if (_parser.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                throw new IllegalArgumentException("Unexpected end of file");
            }
        }
        pCSParentDo.getInternalModifier().addPropertyValueWithoutCheck(propType._property, referencedDo);
        if (propType._property.isContainment()) {
            final Property oppositeProp = propType._property.getOpposite();
            if (oppositeProp != null) {
                referencedDo.getInternalModifier().setPropertyWithoutCheck(oppositeProp, pCSParentDo);
            }
        }
        return referencedDo;
    }

    private Property getPropertyFromPath(final PathReference pPathReference, final Type rootType) {
        SdoType type = null;
        Property property = null;
        final List<PathReferenceStep> steps = pPathReference.getSteps();
        boolean skipStep = false;
        if (rootType != null && steps.size() > 1 && rootType != DataGraphType.getInstance()) {
            type = (SdoType)rootType;
            skipStep = true;
        }
        final XSDHelper xsdHelper = _helperContext.getXSDHelper();
        for(final PathReferenceStep pathStep: steps) {
            if (skipStep) {
                skipStep = false;
                continue;
            }
            if (pathStep.getUri() == null) {
                return null;
            }
            property = null;
            if (type != null) {
                property = type.getPropertyFromXmlName(pathStep.getUri(), pathStep.getName(), true);
                type = null;
            }
            if (property == null) {
                property = xsdHelper.getGlobalProperty(pathStep.getUri(), pathStep.getName(), true);
            }
            if (property == null) {
                return null;
            }
            type = (SdoType)property.getType();
        }
        return property;
    }

    private Set<Reference> getReferences(final String pAttrName, final PathReference pCurrentPath, final Set<Reference> pExistingReferences) {
        final String referenceString = _parser.getAttributeValue(null, pAttrName);
        final Set<Reference> references = new HashSet<Reference>();
        if (referenceString != null) {
            final StringTokenizer tokenizer = new StringTokenizer(referenceString);
            while (tokenizer.hasMoreElements()) {
                final Reference reference = Reference.createReference(pCurrentPath, null, tokenizer.nextToken(), getNamespaceContext());
                final DataObjectDecorator pathObject = referenceToObjectGet(reference);
                if (pathObject != null) {
                    pathObject.getInternalModifier().setChangeStateWithoutCheck(State.CREATED);
                } else {
                    references.add(reference);
                }
            }
        }
        if (references.isEmpty()) {
            return pExistingReferences;
        }
        if (pExistingReferences.isEmpty()) {
        return references;
    }
        pExistingReferences.addAll(references);
        return pExistingReferences;
    }

    private void parseXsd() throws XMLStreamException {
        while(_parser.next() != XMLStreamConstants.END_ELEMENT) {
            if (_parser.isStartElement()) {
                parseElement(null, new PathReference());
            } else if (_parser.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                throw new IllegalArgumentException("Unexpected end of file");
            }
        }
    }

    private Object parseSimpleObject(final SdoProperty pProperty, SdoType<?> pType) throws XMLStreamException {
        final StringBuilder valueBuilder = new StringBuilder();
        while (_parser.next() == XMLStreamConstants.CHARACTERS) {
            valueBuilder.append(_parser.getText());
        }
        if (_parser.getEventType() != XMLStreamConstants.END_ELEMENT) {
            throw new IllegalStateException("Property " + pProperty.getName() + " is no data type");
        }
        final String valueString = valueBuilder.toString();
        Object value = checkSpecialXsdType(pProperty, pType, valueString);
        if (JavaSimpleType.OBJECT == pProperty.getType() && JavaSimpleType.STRING != pType) {
            value = pType.convertFromJavaClass(value);
        }
        return value;
    }

    private DataObjectDecorator parseDataObject(final DataObjectDecorator pParentDo, final PropertyAndType propType, final PathReference pPathReference) throws XMLStreamException {
        DataObjectDecorator elementDo = getOrCreateDataObject(propType._type, pPathReference, true, false, pParentDo);
        boolean hasKeyWithElements = Boolean.FALSE.equals(propType._type.hasXmlFriendlyKey());
        if (propType._property != null && propType._property.isOrphanHolder()) {
            referenceToObjectPut(pPathReference, elementDo);
        } else if (hasKeyWithElements) {
            elementDo.getInternalModifier().setContainerWithoutCheck(pParentDo, propType._property);
        } else {
            addPropertyValue(pParentDo, propType, elementDo, false);
        }
        if (_parseMode == ParseMode.CHANGE_SUMMARY) {
            parseUnsetProperties(elementDo);
            elementDo.getInternalModifier().setChangeStateWithoutCheck(State.DELETED);
        } else if (_dataObject == null) {
            _dataObject = elementDo;
        }
        parseAttributeProperties(elementDo, propType._type, pPathReference);
        InternalDataObjectModifier mixedDo = null;
        if (propType._type.isMixedContent()) {
            mixedDo = elementDo.getInternalModifier();
        }
        while(_parser.next() != XMLStreamConstants.END_ELEMENT) {
            if (_parser.isStartElement()) {
                parseElement(elementDo, pPathReference);
            } else if ((mixedDo != null) && _parser.isCharacters()) {
                mixedDo.addToSequenceWithoutCheck(null, _parser.getText());
            } else if (_parser.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                throw new IllegalArgumentException("Unexpected end of file");
            }
        }
        if (_parseMode == ParseMode.XML) {
            if (propType._type == TypeType.getInstance()) {
               saveXmlType(elementDo);
            } else if (_simplifyOpenContent) {
                elementDo.getInstance().simplifyOpenContent();
            }
        }

        if (hasKeyWithElements) {
            Object key = DataObjectBehavior.getKey(elementDo);
            Key reference = new Key(propType._type.getTypeForKeyUniqueness(), key);
            DataObjectDecorator refObject = referenceToObjectGet(reference);
            if (refObject != null) {
                refObject.getInstance().stealCurrentState(elementDo);
                elementDo = refObject;
            } else {
                referenceToObjectPut(reference, elementDo);
            }
            addPropertyValue(pParentDo, propType, elementDo, false);
        }
        elementDo.getInstance().trimMemory();
        return elementDo;
    }

    private void saveXmlType(final DataObject pTypeObject) {
        if (SapXmlHelper.OPTION_VALUE_FALSE.equals(_defineXmlTypes)) {
            return;
        }
        if (_xmlTypes == null) {
            _xmlTypes = new TreeMap<URINamePair, Type>();
        }
        final SdoType type = (SdoType)pTypeObject;
        _xmlTypes.put(type.getQName(), type);
    }

    private List<DataObject> getXmlTypes() {
        if (_xmlTypes == null) {
            return null;
        }
        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        for (final Type type: _xmlTypes.values()) {
            final List<Type>baseTypes = type.getBaseTypes();
            if (!baseTypes.isEmpty()) {
                final List<Type> realBaseTypes = new ArrayList<Type>(baseTypes.size());
                for (final Type baseType: baseTypes) {
                    final Type realType = getRealType(baseType);
                    if (realType != type) {
                        realBaseTypes.add(realType);
                    }
                }
                ((DataObject)type).set(TypeType.BASE_TYPE, realBaseTypes);
            }
            for (final Property property: (List<Property>)type.getProperties()) {
                final Type propertyType = property.getType();
                final Type realType = getRealType(propertyType);
                if (realType != type) {
                    ((DataObject)property).set(PropertyType.TYPE, realType);
                }
            }
        }
        final List types = new ArrayList(_xmlTypes.values());
        if (SapXmlHelper.OPTION_VALUE_AUTO.equals(_defineXmlTypes)) {
            return types;
        }
        return typeHelper.define(types);
    }

    private Type getRealType(final Type type) {
        Type realType = _helperContext.getTypeHelper().getType(type.getURI(), type.getName());
        if (realType == null) {
            realType = _xmlTypes.get(((SapType)type).getQName());
        }
        if (realType == null) {
            throw new IllegalArgumentException("Type " +
                ((SapType)type).getQName().toStandardSdoFormat() + " is unknown");
        }
        return realType;
    }

    private void parseAttributeProperties(final DataObjectDecorator elementDo, final SdoType pType, final PathReference pParentPathReference) {
        for (int i = 0; i < _parser.getAttributeCount(); i++) {
            String attrNs = _parser.getAttributeNamespace(i);
            if (attrNs == null) {
                attrNs = "";
            }
            final String attrLocalName = _parser.getAttributeLocalName(i);
            URINamePair unp = null;
            if (attrNs.length() > 0) {
                unp = new URINamePair(attrNs, attrLocalName);
                if (NO_CONTENT_ATTRIBUTES.contains(unp)) {
                    continue;
                }
            }
            final String valueString = _parser.getAttributeValue(i);
            SdoProperty attrProp = pType.getPropertyFromXmlName(attrNs, attrLocalName, false);
            Object value = null;
            if (attrProp == null) {
                if (!pType.isOpen()) {
                    // use errorHandler to decide if exception should be thrown
                    _errorHandler.handleUnknownProperty(
                        new IllegalArgumentException(
                            "Type " + pType.getQName().toStandardSdoFormat() + " is not open. " +
                            "Property '" + attrLocalName + "' is not defined."));
                } else {
                    attrProp = (SdoProperty)((TypeHelperImpl)_helperContext.getTypeHelper()).getOpenContentPropertyByXmlName(attrNs, attrLocalName, false);
                    if (attrProp == null) {
                        attrProp = (SdoProperty)elementDo.getInternalModifier().findOpenProperty(attrNs, attrLocalName, false);
                        final DataObject propObj = (DataObject)attrProp;
                        if ((propObj != null) && !propObj.isSet(PropertyType.getXmlElementProperty())) {
                            propObj.setBoolean(PropertyType.getXmlElementProperty(), false);
                            attrProp = (SdoProperty)_helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
                        }
                    }
                    if (attrProp == null) {
                        final DataObject propObj = _helperContext.getDataFactory().create(PropertyType.getInstance());
                        propObj.set(PropertyType.TYPE, _stringType);
                        propObj.set(PropertyType.MANY, false);
                        propObj.set(PropertyType.getXmlElementProperty(), false);
                        if (unp != null) {
                            // that forces, that the prefix is rendered again
                            propObj.setString(PropertyType.getReferenceProperty(), unp.toStandardSdoFormat());
                        }
                        _namingHandler.nameOpenContentProperty(propObj, attrNs, attrLocalName, false, elementDo);
                        attrProp = (SdoProperty)_helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
                        value = valueString;
                    } else {
                        value = calculateAttributeValue(attrProp, valueString, pParentPathReference);
                    }
                }
            } else {
                value = calculateAttributeValue(attrProp, valueString, pParentPathReference);
            }
            try {
                elementDo.getInternalModifier().addPropertyValueWithoutCheck(attrProp, value);
            } catch (final RuntimeException ex) {
                _errorHandler.handleInvalidValue(ex);
            }
        }
    }

    private Object createTypeDummy(final URINamePair pUriName) {
        Type type = _helperContext.getTypeHelper().getType(pUriName.getURI(), pUriName.getName());
        if (type != null) {
            return type;
        }
        DataObject typeDO = _helperContext.getDataFactory().create(TypeType.getInstance());
        typeDO.set(TypeType.URI, pUriName.getURI());
        typeDO.set(TypeType.NAME, pUriName.getName());
        return typeDO;
    }

    /**
     *
     * @param pAttrProp
     * @param pValueString
     * @param pParentPathReference
     * @return
     */
    private Object calculateAttributeValue(
        final SdoProperty pAttrProp,
        final String pValueString,
        final PathReference pParentPathReference) {

        if (pAttrProp.getType().isDataType()) {
            return checkSpecialXsdType(pAttrProp, (SdoType<?>)pAttrProp.getType(), pValueString);
        } else {
            if (pAttrProp.getType() == TypeType.getInstance()) {
                if (pValueString.length() == 0) {
                    return null;
                }
                final URINamePair uriName = URINamePair.fromStandardSdoFormat(pValueString);
                final Type type = _helperContext.getTypeHelper().getType(uriName.getURI(), uriName.getName());
                if (type != null) {
                    return type;
                }
                return createTypeDummy(uriName);
            } else {
                Reference reference = Reference.createReference(pParentPathReference, (SdoType)pAttrProp.getType(), pValueString, getNamespaceContext());
                if (reference == null) {
                    return null;
                }
                return getOrCreateDataObject(
                    (SdoType)pAttrProp.getType(),
                    reference,
                    false, true, null);
            }
        }
    }

    private DataObjectDecorator resolvePathReference(final Reference pathRef) {
        if (_dataObject == null || pathRef.isKeyReference()) {
            return null;
        }
        DataObjectDecorator ref = _dataObject;
        final Iterator<PathReferenceStep> steps = ((PathReference)pathRef).getSteps().iterator();
        if (steps.hasNext() && ref.getType() != DataGraphType.getInstance()) {
            // first step points to _dataObject
            steps.next();
        }
        while (steps.hasNext() && ref != null) {
            final PathReferenceStep step = steps.next();
            final Property prop = ref.getInstance().getInstanceProperty(step.getUri(), step.getName(), true);
            Object result = null;
            if (prop != null) {
                result = ref.get(prop);
                if (prop.isMany() && ((List<?>)result).size() > step.getIndex()) {
                    result = ((List<?>)result).get(step.getIndex());
                }
            }
            if (result instanceof DataObjectDecorator) {
                ref = (DataObjectDecorator)result;
            } else {
                ref = null;
            }
        }
        return ref;
    }

    private DataObjectDecorator getOrCreateDataObject(final SdoType<?> type, final Reference pReference, final boolean pLookForIdAttr, final boolean isReference, DataObjectDecorator pParentDo) {
        DataObjectDecorator keyRefObject = null;
        Key key = null;
        if (pLookForIdAttr && Boolean.TRUE.equals(type.hasXmlFriendlyKey())) {
            SdoType keyType = (SdoType)type.getKeyType();
            if (keyType.isDataType()) {
                SdoProperty keyProperty = type.getKeyProperties().get(0);
                if (keyProperty.isOppositeContainment() && pParentDo != null) {
                    key = new Key(type.getTypeForKeyUniqueness(), DataObjectBehavior.getKey(pParentDo));
                } else {
                    final String id = getAttributeValue(keyProperty);
                    if (id != null) {
                        key = new Key(type.getTypeForKeyUniqueness(), id);
                    }
                }
            } else {
                DataObject keyDo = _helperContext.getDataFactory().create(keyType);
                List<SdoProperty> keyProperties = type.getKeyProperties();
                for (int i = 0; i < keyProperties.size(); i++) {
                    SdoProperty keyProperty = keyProperties.get(i);
                    if (keyProperty.isOppositeContainment() && pParentDo != null) {
                        Object keyObject = DataObjectBehavior.getKey(pParentDo);
                        if (keyObject == null) {
                            keyDo = null;
                            break;
                        }
                        SdoProperty keyTypeProperty = keyType.getPropertyFromXmlName(keyProperty.getUri(), keyProperty.getName(), keyObject instanceof DataObject);
                        keyDo.set(keyTypeProperty, keyObject);
                    } else {
                        final String id = getAttributeValue(keyProperty);
                        if (id == null) {
                            keyDo = null;
                            break;
                        }
                        SdoProperty keyTypeProperty = keyType.getPropertyFromXmlName(keyProperty.getUri(), keyProperty.getName(), false);
                        keyDo.setString(keyTypeProperty, id);
                    }
                }
                if (keyDo != null) {
                    key = new Key(type.getTypeForKeyUniqueness(), keyDo);
                }
            }
            if (key != null) {
                keyRefObject = referenceToObjectGet(key);
            }
        }
        DataObjectDecorator pathObject = null;
        if (isReference) {
            if (pReference.isKeyReference()) {
                key = (Key)pReference;
                //TODO is the look-up redundant
                keyRefObject = referenceToObjectGet(key);
            } else {
                pathObject = referenceToObjectGet(pReference);
                if (pathObject == null && !_deletedReferences.contains(pReference)) {
                    pathObject = resolvePathReference(pReference);
                }
            }
        } else {
            pathObject = referenceToObjectRemove(pReference);
        }
        DataObjectDecorator obj = pathObject;
        if (obj == null) {
            obj = keyRefObject;
        }
        if (obj == null || ((SdoType)obj.getInstance().getType()).getKeyType() == type) {
            if (type.isAbstract()) {
                final TypeHelperImpl typeHelper = (TypeHelperImpl)_helperContext.getTypeHelper();
                final TypeAndContext typeAndContext = typeHelper.getTypeAndContext(type);
                obj = new GenericDataObject(typeAndContext);
            } else {
                obj = (DataObjectDecorator)_helperContext.getDataFactory().create(type);
            }
        } else {
            obj.getInstance().refineType(type);
        }
        if (isReference && pathObject == null /**&& !pPathReference.isIdRef()*/) {
            referenceToObjectPut(pReference, obj);
        }
        if (_createdReferences.remove(pReference)) {
            obj.getInternalModifier().setChangeStateWithoutCheck(State.CREATED);
        }
        if (_deletedReferences.remove(pReference)) {
            obj.getInternalModifier().setChangeStateWithoutCheck(State.DELETED);
        }

        if (key != null) {
            if (keyRefObject == null) {
                referenceToObjectPut(key, obj);
            }
            if (_createdReferences.remove(key)) {
                obj.getInternalModifier().setChangeStateWithoutCheck(State.CREATED);
            }
            if (_deletedReferences.remove(key)) {
                obj.getInternalModifier().setChangeStateWithoutCheck(State.DELETED);
            }
        }
        if (_parseMode == ParseMode.CHANGE_SUMMARY) {
            obj = OldStateDataObject.getOldStateFacade(obj);
        }
        return obj;
    }

    private DataObjectDecorator referenceToObjectGet(final Reference pReference) {
        return _referenceToObject==null?null:_referenceToObject.get(pReference);
    }

    private DataObjectDecorator referenceToObjectRemove(final Reference pPathReference) {
        if (_referenceToObject == null) {
            return null;
        }
        final DataObjectDecorator removed = _referenceToObject.remove(pPathReference);
        if (removed != null && _referenceToObject.isEmpty()) {
            _referenceToObject = null;
        }
        return removed;
    }

    private void referenceToObjectPut(final Reference pReference, final DataObjectDecorator pDataObject) {
        if (_referenceToObject == null) {
            _referenceToObject = new HashMap<Reference, DataObjectDecorator>();
        }
        _referenceToObject.put(pReference, pDataObject);
    }

    /**
     * @param pPropType
     * @param pValue
     * @return
     */
    private Object checkSpecialXsdType(final SdoProperty pProperty, final SdoType<?> pType, final String pValue) {
        URINamePair xsdType = pProperty.getXsdType();
        if (xsdType == null) {
            xsdType = pType.getSpecialBaseType();
        }
        if (xsdType != null) {
            if (URINamePair.SCHEMA_BASE64BINARY.equals(xsdType)) {
                return Base64Util.decodeBase64(pValue);
            }
            if (URINamePair.SCHEMA_Q_NAME.equals(xsdType)) {
                if (pType.getInstanceClass() == List.class) {
                    final List<String> qNames = (List<String>)pType.convertFromJavaClass(pValue);
                    final List<String> uris = new ArrayList<String>(qNames.size());
                    for (final String qName: qNames) {
                        uris.add(qNameToUri(qName));
                    }
                    return uris;
                } else {
                    return qNameToUri(pValue);
                }
            }

        }
        return pValue;
    }

    private String qNameToUri(final String pQName) {
        // TODO fix workaround
        // collect all namespaces that are about to go out of scope
        // it depends on implementation if these namespaces are available
        // with getNamespaceURI(prefix)
        final Map<String,String> prefixToURI = new HashMap<String,String>();
        final int namespaceCount = _parser.getNamespaceCount();
        for (int i=0; i<namespaceCount; ++i) {
            prefixToURI.put(_parser.getNamespacePrefix(i), _parser.getNamespaceURI(i));
        }

        final URINamePair qName = URINamePair.getQNameFromString(pQName);
        String ns = prefixToURI.get(qName.getURI());
        if (ns == null) {
            ns = _parser.getNamespaceURI(qName.getURI());
        }
        if (ns != null) {
            qName.setURI(ns);
            return qName.toStandardSdoFormat();
        }
        return pQName;
    }

    private void parseSchemaLocation() throws XMLStreamException {
        String schemaLocation = getAttributeValue(XSI_SCHEMA_LOCATION);
        if (schemaLocation != null) {
            resolveSchemaLocations(schemaLocation);
        }
        schemaLocation = getAttributeValue(XSI_NO_NAMESPACE_SCHEMA_LOCATION);
        if (schemaLocation != null) {
            if (_noNamespaceSchemaLocation == null) {
                _noNamespaceSchemaLocation = schemaLocation;
            }
            resolveSchemaLocation("", schemaLocation);
        }
    }

    private void resolveSchemaLocations(final String pAttributeContent) throws XMLStreamException {
        final StringTokenizer tokenizer = new StringTokenizer(pAttributeContent);
        while(tokenizer.hasMoreElements()) {
            final String namespace = tokenizer.nextToken();
            if (!tokenizer.hasMoreElements()) {
                throw new IllegalArgumentException("schemaLocation is not a pair");
            }
            final String location = tokenizer.nextToken();
            final String locationPair = namespace + ' ' + location;
            if (!_nameSpaceLocationPairs.contains(locationPair)) {
                _nameSpaceLocationPairs.add(locationPair);
            }
            resolveSchemaLocation(namespace, location);
        }

    }

    private void resolveSchemaLocation(final String pNamespace, final String pLocation) throws XMLStreamException {
        try {
            final SchemaResolver schemaResolver = getSchemaResolver();
            final String location = schemaResolver.getAbsoluteSchemaLocation(pLocation, _locationUri);
            final SchemaLocation schemaLocation = new SchemaLocation(pNamespace, location);

            getUndefinedXsdToTypesTranslator().addSchemaLocation(schemaLocation);
        } catch (final URISyntaxException e) {
            throw new XMLStreamException(e);
        }
    }

    private String getSchemaLocation() {
        if (_nameSpaceLocationPairs.size() == 0) {
            return null;
        }
        final StringBuilder schemaLocation = new StringBuilder();
        for (final String namespaceLocationPair: _nameSpaceLocationPairs) {
            if (schemaLocation.length() > 0) {
                schemaLocation.append(' ');
            }
            schemaLocation.append(namespaceLocationPair);
        }
        return schemaLocation.toString();
    }

    private void parseUnsetProperties(final DataObjectDecorator pOldStateDataObject) {
        final String unsetProps = getAttributeValue(SDO_UNSET);
        if (unsetProps == null) {
            return;
        }
        boolean sequenced = pOldStateDataObject.getType().isSequenced();
        boolean emptySequence = false;
        final StringTokenizer tokenizer = new StringTokenizer(unsetProps);
        while (tokenizer.hasMoreElements()) {
            final String propName = tokenizer.nextToken(); // TODO could be a qName?
            Property property = pOldStateDataObject.getInstanceProperty(propName);
            if (property == null) {
                final DataObject propObj = _helperContext.getDataFactory().create(PropertyType.getInstance());
                propObj.set(PropertyType.NAME, propName);
                propObj.set(PropertyType.TYPE, DataObjectType.getInstance());
                propObj.set(PropertyType.MANY, true);
                if (_simplifyOpenContent) {
                    propObj.setBoolean(PropertyType.getManyUnknownProperty(), true);
                }
                propObj.set(PropertyType.CONTAINMENT, true);
                property = (Property)propObj;
            }
            if (sequenced && ((SdoProperty)property).isXmlElement()) {
                emptySequence = true;
            } else {
                pOldStateDataObject.getInternalModifier().unsetPropertyWithoutCheck(property);
            }
        }
        if (emptySequence) {
            List<ChangeSummary.Setting> emptySettings = Collections.emptyList();
            pOldStateDataObject.getInternalModifier().setSequenceWithoutCheck(emptySettings);
        }
    }

    private String getAttributeValue(final URINamePair pUriNamePair) {
        return _parser.getAttributeValue(pUriNamePair.getURI(), pUriNamePair.getName());
    }

    private String getAttributeValue(SdoProperty keyProperty) {
        String uri = keyProperty.getUri();
        if (uri != null && uri.length() == 0) {
            uri = null;
        }
        return _parser.getAttributeValue(uri, keyProperty.getXmlName());
    }

    private NamespaceContext _dummyNamespaceContext;

    private NamespaceContext getNamespaceContext() {
        NamespaceContext namespaceContext = _parser.getNamespaceContext();
        if (namespaceContext == null) {
            //workaround for SAP XMLStreamReader
            if (_dummyNamespaceContext == null) {
                _dummyNamespaceContext = new NamespaceContext() {
                    public String getNamespaceURI(String pPrefix) {
                        return _parser.getNamespaceURI(pPrefix);
                    }
                    public String getPrefix(String pNamespaceURI) {
                        throw new UnsupportedOperationException("getPrefix");
                    }
                    public Iterator getPrefixes(String pNamespaceURI) {
                        throw new UnsupportedOperationException("getPrefixes");
                    }
                };
            }
            return _dummyNamespaceContext;
        }
        return namespaceContext;
    }

    private static class PropertyAndType {
        private SdoProperty _property;
        /**
         * The type of the value.
         * Note that it can be more concrete than <code>_property.getType()</code>
         * and has to be assignable to the properties type.
         */
        private SdoType _type;
        private URINamePair _xsdType;
        private boolean _skipData = false;
    }
}
