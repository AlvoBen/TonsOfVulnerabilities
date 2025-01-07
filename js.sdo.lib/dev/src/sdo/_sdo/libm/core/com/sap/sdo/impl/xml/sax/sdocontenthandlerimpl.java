package com.sap.sdo.impl.xml.sax;

import java.io.IOException;
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
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sap.sdo.api.helper.ErrorHandler;
import com.sap.sdo.api.helper.NamingHandler;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.helper.SchemaResolver;
import com.sap.sdo.api.helper.Validator;
import com.sap.sdo.api.helper.util.IntegrationHandler;
import com.sap.sdo.api.helper.util.SDOContentHandler;
import com.sap.sdo.api.helper.util.IntegrationHandler.Mode;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.ChangeSummaryImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
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
import com.sap.sdo.impl.xml.DefaultErrorHandler;
import com.sap.sdo.impl.xml.DefaultRenamingHandler;
import com.sap.sdo.impl.xml.Key;
import com.sap.sdo.impl.xml.OrphanHolder;
import com.sap.sdo.impl.xml.PathReference;
import com.sap.sdo.impl.xml.PathReferenceStep;
import com.sap.sdo.impl.xml.Reference;
import com.sap.sdo.impl.xml.SchemaLocation;
import com.sap.sdo.impl.xml.XMLDocumentImpl;
import com.sap.sdo.impl.xml.XsdToTypesTranslator;
import com.sap.sdo.impl.xml.stream.SdoNamespaceContext;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class SdoContentHandlerImpl implements SDOContentHandler {

    public static final URINamePair XSI_SCHEMA_LOCATION = new URINamePair(URINamePair.XSI_URI, "schemaLocation");
    public static final URINamePair XSI_NO_NAMESPACE_SCHEMA_LOCATION = new URINamePair(URINamePair.XSI_URI, "noNamespaceSchemaLocation");
    public static final URINamePair XSI_TYPE = new URINamePair(URINamePair.XSI_URI, "type");
    public static final URINamePair XSI_NIL = new URINamePair(URINamePair.XSI_URI, "nil");
    public static final URINamePair XMLNS = new URINamePair(XMLConstants.NULL_NS_URI, XMLConstants.XMLNS_ATTRIBUTE);

    public static final URINamePair SDO_REF = new URINamePair(URINamePair.MODELTYPE_URI, "ref");
    public static final URINamePair SDO_UNSET = new URINamePair(URINamePair.MODELTYPE_URI, "unset");

    public static final String OPTION_KEY_LOCATION_URI = SDOContentHandler.class.getName() + ".LocationURI";

    private static final DefaultErrorHandler DEFAULT_ERROR_HANDLER = new DefaultErrorHandler();
    private SdoNamespaceContext _namespaceContext = new SdoNamespaceContext();
    private int _level;
    private int _pathOffset = 0;
    private final List<Element> _elements = new ArrayList<Element>();
    private final ComplexContentHandler _complexContentHandler = new ComplexContentHandler();
    private final SimpleContentHandler _simpleContentHandler = new SimpleContentHandler();
    private final ValuePropContentHandler _valueContentHandler = new ValuePropContentHandler();
    private final ReferenceContentHandler _referenceContentHandler = new ReferenceContentHandler();
    private final SapHelperContext _helperContext;
    final private Map _options = new HashMap();
    private String _noNamespaceSchemaLocation;
    private final List<String> _nameSpaceLocationPairs = new ArrayList<String>();
    private Map<Reference,DataObjectDecorator> _referenceToObject;
    Set<Reference> _createdReferences = Collections.emptySet();
    Set<Reference> _deletedReferences = Collections.emptySet();
    private final List<ChangeSummary> _loggingChangeSummaries = new ArrayList<ChangeSummary>();
    private final List<ChangeSummary> _stoppedChangeSummaries = new ArrayList<ChangeSummary>();
    private final Map<OrphanHolder,List<DataObjectDecorator>> _orphanList = new HashMap<OrphanHolder,List<DataObjectDecorator>>();
    private final Map<Property,Integer> _orphansInCs = new HashMap<Property,Integer>();
    private final Map<ChangeSummary, List<DataObjectDecorator>> _deletedOrphans = new HashMap<ChangeSummary, List<DataObjectDecorator>>();

    private final Type _stringType;
    private XMLDocumentImpl _xmlDocument;
    private static enum ParseMode {XML, XSD, CHANGE_SUMMARY}
    private ParseMode _parseMode = ParseMode.XML;
    private SchemaResolver _schemaResolver;
    private List<XsdToTypesTranslator> _xsdToTypesTranslators;
    private String _defineSchemas = SapXmlHelper.OPTION_VALUE_AUTO;
    private boolean _simplifyOpenContent = true;
    private ErrorHandler _errorHandler = DEFAULT_ERROR_HANDLER;
    private NamingHandler _namingHandler = new DefaultRenamingHandler();
    private Validator _validator;
    private IntegrationHandler _integrationHandler;
    private ChangeSummary _currentChangeSummary;

    public static final Set<URINamePair> NO_CONTENT_ATTRIBUTES = new HashSet<URINamePair>();
    static {
        NO_CONTENT_ATTRIBUTES.add(XSI_SCHEMA_LOCATION);
        NO_CONTENT_ATTRIBUTES.add(XSI_NO_NAMESPACE_SCHEMA_LOCATION);
        NO_CONTENT_ATTRIBUTES.add(XSI_TYPE);
        NO_CONTENT_ATTRIBUTES.add(XSI_NIL);
        NO_CONTENT_ATTRIBUTES.add(SDO_REF);
        NO_CONTENT_ATTRIBUTES.add(SDO_UNSET);
        NO_CONTENT_ATTRIBUTES.add(XMLNS);
    }

    public SdoContentHandlerImpl(final SapHelperContext pHelperContext, Map pOptions) {
        _helperContext = pHelperContext;
        _stringType = _helperContext.getTypeHelper().getType(URINamePair.STRING.getURI(), URINamePair.STRING.getName());
        if (pOptions != null) {
            _options.putAll(pOptions);
            _integrationHandler = (IntegrationHandler)_options.get(IntegrationHandler.class.getName());
            _simplifyOpenContent = _integrationHandler == null && 
                !SapXmlHelper.OPTION_VALUE_FALSE.equals(
                _options.get(SapXmlHelper.OPTION_KEY_SIMPLIFY_OPEN_CONTENT));
            final Object errorHandler = _options.get(SapXmlHelper.OPTION_KEY_ERROR_HANDLER);
            if (errorHandler instanceof ErrorHandler) {
                _errorHandler = (ErrorHandler)errorHandler;
            }
            final Object namingHandler = _options.get(SapXmlHelper.OPTION_KEY_NAMING_HANDLER);
            if (namingHandler instanceof NamingHandler) {
                _namingHandler = (NamingHandler)namingHandler;
            }
            _validator = (Validator)_options.get(SapXmlHelper.OPTION_KEY_VALIDATOR);
            final Object defineSchemas = _options.get(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS);
            if (SapXmlHelper.OPTION_VALUE_FALSE.equals(defineSchemas) ||
                SapXmlHelper.OPTION_VALUE_TRUE.equals(defineSchemas) ||
                SapXmlHelper.OPTION_VALUE_AUTO.equals(defineSchemas)) {
                _defineSchemas = (String)defineSchemas;
            }

        }

        // initialize
        Element element = createElement();
        _elements.add(element);
        element._namespaceContext = _namespaceContext;
        element._contentHandler = _complexContentHandler;
        _level = 0;
    }

    public void characters(char[] pCh, int pStart, int pLength)
        throws SAXException {
        getContentHandler().characters(pCh, pStart, pLength);
    }

    public void endDocument() throws SAXException {
        if (_level != -1) {
            throw new SAXException("Illegal parser level: " + _level);
        }
    }

    public void endElement(String pUri, String pLocalName, String pName)
        throws SAXException {
        getContentHandler().endElement(pUri, pLocalName, pName);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        //ignored
    }

    public void ignorableWhitespace(char[] pCh, int pStart, int pLength)
        throws SAXException {
        getContentHandler().ignorableWhitespace(pCh, pStart, pLength);
    }

    public void processingInstruction(String pTarget, String pData)
        throws SAXException {
        getContentHandler().processingInstruction(pTarget, pData);
    }

    public void setDocumentLocator(Locator pLocator) {
        //ignored
    }

    public void skippedEntity(String pName) throws SAXException {
        getContentHandler().skippedEntity(pName);
    }

    public void startDocument() throws SAXException {
        //ignored - done in constructor
    }

    public void startElement(String pUri, String pLocalName, String pName,
        Attributes pAtts) throws SAXException {
        getContentHandler().startElement(pUri, pLocalName, pName, pAtts);
    }

    public void startPrefixMapping(String prefix, String pUri)
        throws SAXException {
        getContentHandler().startPrefixMapping(prefix, pUri);
    }

    public XMLDocument getDocument() {
        return _xmlDocument;
    }

    public Map getOptions() {
        return _options;
    }

    private Element getElement() {
        return _elements.get(_level);
    }

    private Element createElement() {
        if (_integrationHandler == null) {
            return new Element();
        }
        return new IntegrationElement();
    }

    private Element newElementLevel() {
        Element element = getElement();
        if (element._name != null) {
            SdoNamespaceContext parentNamespaceContext = getNamespaceContext();
            _level++;
            if (_level == _elements.size()) {
                element = createElement();
                _elements.add(element);
            } else {
                element = _elements.get(_level);
            }
            element._namespaceContext = new SdoNamespaceContext(parentNamespaceContext);
            element._contentHandler = _complexContentHandler;
        }
        return element;
    }
    
    private Mode getIntegrationMode() {
        return _integrationHandler.getIntegrationMode((List)_elements.subList(_pathOffset, _level + 1));
    }

    private SdoNamespaceContext getNamespaceContext() {
        return getElement()._namespaceContext;
    }

    private ContentHandler getContentHandler() {
        return getElement()._contentHandler;
    }

    private void getPropertyAndType(Attributes pAttributes) {
        Element element = getElement();
        DataObjectDecorator parentDo = getParentDataObject();
        SdoType<?> parentType = null;
        if (parentDo != null) {
            parentType = (SdoType<?>)parentDo.getInstance().getType();
            element._property = parentType.getPropertyFromXmlName(element._uri, element._name, true);
        }
        if (element._property == null) {
            element._property = (SdoProperty)((TypeHelperImpl)_helperContext.getTypeHelper()).getOpenContentPropertyByXmlName(element._uri, element._name, true);
            if ((element._property != null) && (parentType != null)) {
                // look for a substituted property
                for (final SdoProperty prop: (List<SdoProperty>)parentType.getProperties()) {
                    final URINamePair ref = prop.getRef();
                    if (ref != null) {
                        final Property headProp = _helperContext.getXSDHelper().getGlobalProperty(ref.getURI(), ref.getName(), true);
                        if (((DataObject)headProp).isSet(PropertyType.getSubstitutesProperty())) {
                            for (final Object uriObj: ((DataObject)headProp).getList(PropertyType.getSubstitutesProperty())) {
                                final URINamePair substUNP = URINamePair.fromStandardSdoFormat((String) uriObj);
                                if (element._uri.equals(substUNP.getURI()) && element._name.equals(substUNP.getName())) {
                                    element._valueType = (SdoType<?>)element._property.getType();
                                    element._property = prop;
                                    element._uri = ref.getURI();
                                    element._name = ref.getName();
                                    break;
                                }
                            }
                            if (element._valueType != null) {
                                // type was null before, this means a substituted property was found
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (element._property == null) {
            if (parentDo != null) {
                element._property = (SdoProperty)parentDo.getInternalModifier().findOpenProperty(element._uri, element._name, true);
                final DataObject propObj = (DataObject)element._property;
                if ((propObj != null) && !propObj.isSet(PropertyType.getXmlElementProperty())) {
                    propObj.setBoolean(PropertyType.getXmlElementProperty(), true);
                    element._property = (SdoProperty)_helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
                }
            }
            // getPropertyFromPath is not implemented as in XmlStaxReader
        }
        if ((element._property != null)) {
            // the type is taken from the property
            if (element._valueType == null) {
                element._valueType = (SdoType<?>)element._property.getType();
            }
        }
        final String explicitType = pAttributes.getValue(XSI_TYPE.getURI(), XSI_TYPE.getName());
        if (explicitType != null) {
            // the type is explicitly set
            final URINamePair qName = URINamePair.getQNameFromString(explicitType);
            final String namespaceURI = getNamespaceContext().getNamespaceURI(qName.getURI());
            if (namespaceURI != null) {
                qName.setURI(namespaceURI);
            }
            element._valueType = (SdoType<?>)((TypeHelperImpl)_helperContext.getTypeHelper()).getTypeByXmlName(qName.getURI(), qName.getName());
            if ((element._property == null || element._property.isNullable()) && element._valueType instanceof JavaSimpleType) {
                element._valueType = ((JavaSimpleType<?>)element._valueType).getNillableType();
            }
            if (!qName.equalsUriName(element._valueType)) {
                element._xsdType = qName;
            }
        }

        if ((element._valueType == null) && !_defineSchemas.equals(SapXmlHelper.OPTION_VALUE_FALSE )
            && (_parseMode != ParseMode.XSD)) {
            // if type is unknown and there where schemas in this XML
            // translate and define the schemas and try again to find the type
            if (defineSchemas()) {
                getPropertyAndType(pAttributes);
                return;
            }
        }

        if (element._valueType == null  || element._valueType == DataObjectType.getInstance()) {
            element._valueType = UndecidedType.getInstance();
        }
        if (element._property == null) {
            if ((parentType != null) && !parentType.isOpen()) {
                // use errorHandler to decide if exception should be thrown
                _errorHandler.handleUnknownProperty(
                    new IllegalArgumentException(
                        "Type " + parentType.getQName().toStandardSdoFormat() + " is not open. " +
                        "Property '" + element._name + "' is not defined."));

                // exception wasn't thrown, let's ignore this property
                element._skipData = true;
            }
            final DataObject propObj = _helperContext.getDataFactory().create(PropertyType.getInstance());
            if (_simplifyOpenContent) {
                propObj.setBoolean(PropertyType.getManyUnknownProperty(), true);
            }
            propObj.set(PropertyType.MANY, true);
            propObj.set(PropertyType.TYPE, DataObjectType.getInstance());
            propObj.set(PropertyType.CONTAINMENT, true);
            propObj.set(PropertyType.getXmlElementProperty(), true);
            if (element._uri.length() > 0 ) {
                final URINamePair ref = new URINamePair(element._uri, element._name);
                // that forces, that the prefix is rendered again
                propObj.setString(PropertyType.getReferenceProperty(), ref.toStandardSdoFormat());
            }
            _namingHandler.nameOpenContentProperty(propObj, element._uri, element._name, true, parentDo);
            element._property = (SdoProperty)_helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        }


    }

    private void parseSchemaLocationProperty(Attributes pAttributes) {
        for (int i = 0; i < pAttributes.getLength(); i++) {
            String attrNs = pAttributes.getURI(i);
            if (attrNs == null || XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attrNs)) {
                continue;
            }
            if (attrNs.length() > 0) {
                URINamePair unp = new URINamePair(attrNs, pAttributes.getLocalName(i));
                if (NO_CONTENT_ATTRIBUTES.contains(unp)) {
                    if (unp.equals(XSI_SCHEMA_LOCATION)) {
                        resolveSchemaLocations(pAttributes.getValue(i));
                    } else if (unp.equals(XSI_NO_NAMESPACE_SCHEMA_LOCATION)) {
                        _noNamespaceSchemaLocation = pAttributes.getValue(i);
                        resolveSchemaLocation("", _noNamespaceSchemaLocation);
                    }
                }
            }
        }
    }

    private void parseAttributeProperties(Attributes pAttributes) {
        DataObjectDecorator elementDo = getElement()._dataObject;
        SdoType<?> type = (SdoType<?>)elementDo.getInstance().getType();
        for (int i = 0; i < pAttributes.getLength(); i++) {
            boolean skipData = false;
            String attrNs = pAttributes.getURI(i);
            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attrNs)) {
                continue;
            }
            if (attrNs == null) {
                attrNs = XMLConstants.NULL_NS_URI;
            }
            final String attrLocalName = pAttributes.getLocalName(i);
            URINamePair unp = null;
            if (attrNs.length() > 0) {
                unp = new URINamePair(attrNs, attrLocalName);
                if (NO_CONTENT_ATTRIBUTES.contains(unp)) {
                    if (unp.equals(XSI_SCHEMA_LOCATION)) {
                        resolveSchemaLocations(pAttributes.getValue(i));
                    } else if (unp.equals(XSI_NO_NAMESPACE_SCHEMA_LOCATION)) {
                        _noNamespaceSchemaLocation = pAttributes.getValue(i);
                    }
                    continue;
                }
            }
            final String valueString = pAttributes.getValue(i);
            SdoProperty attrProp = type.getPropertyFromXmlName(attrNs, attrLocalName, false);
            Object value = null;
            if (attrProp == null) {
                if (!type.isOpen()) {
                    // use errorHandler to decide if exception should be thrown
                    _errorHandler.handleUnknownProperty(
                        new IllegalArgumentException(
                            "Type " + type.getQName().toStandardSdoFormat() + " is not open. " +
                            "Property '" + attrLocalName + "' is not defined."));

                    // exception wasn't thrown, let's ignore this property
                    skipData = true;
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
                    }
                }
            }
            if (!skipData) {
                SdoType<?> attrType = (SdoType<?>)attrProp.getType();
                if (attrType.isDataType()) {
                    value = checkSpecialXsdType(attrProp, valueString);
                } else {
                    if (attrType == TypeType.getInstance()) {
                        if (valueString.length() > 0) {
                            final URINamePair uriName = URINamePair.fromStandardSdoFormat(valueString);
                            value = _helperContext.getTypeHelper().getType(uriName.getURI(), uriName.getName());
                            if (value == null) {
                                value = createTypeDummy(uriName);
                             }
                        }
                    } else {
                        value = getOrCreateReferencedDataObject(Reference.createReference(getPathReference(), attrType, valueString, getNamespaceContext()), attrType);
                    }
                }
                try {
                    elementDo.getInternalModifier().addPropertyValueWithoutCheck(attrProp, value);
                } catch (final RuntimeException ex) {
                    _errorHandler.handleInvalidValue(ex);
                }
            }
        }
    }

    private void parseUnsetProperties(final Attributes pAtts) {
        Element element = getElement();
        final String unsetProps = pAtts.getValue(SDO_UNSET.getURI(), SDO_UNSET.getName());
        if (unsetProps == null) {
            return;
        }
        boolean sequenced = element._valueType.isSequenced();
        boolean emptySequence = false;
        final StringTokenizer tokenizer = new StringTokenizer(unsetProps);
        while (tokenizer.hasMoreElements()) {
            final String propName = tokenizer.nextToken(); // TODO could be a qName?
            Property property = element._dataObject.getInstanceProperty(propName);
            if (property == null) {
                final DataObject propObj = _helperContext.getDataFactory().create(PropertyType.getInstance());
                propObj.set(PropertyType.NAME, propName);
                propObj.set(PropertyType.TYPE, DataObjectType.getInstance());
                if (_simplifyOpenContent) {
                    propObj.setBoolean(PropertyType.getManyUnknownProperty(), true);
                }
                propObj.set(PropertyType.MANY, true);
                propObj.set(PropertyType.CONTAINMENT, true);
                property = (Property)propObj;
            }
            if (sequenced && ((SdoProperty)property).isXmlElement()) {
                emptySequence = true;
            } else {
                element._dataObject.getInternalModifier().unsetPropertyWithoutCheck(property);
            }
        }
        if (emptySequence) {
            List<ChangeSummary.Setting> emptySettings = Collections.emptyList();
            element._dataObject.getInternalModifier().setSequenceWithoutCheck(emptySettings);
        }
    }


    private boolean defineSchemas() {
        // only the last XsdToTypesTranslator could be not defined
        final XsdToTypesTranslator xsdToTypesTranslator = getLastXsdToTypesTranslator();
        if ((xsdToTypesTranslator != null) && !xsdToTypesTranslator.isDefined()) {
            try {
                xsdToTypesTranslator.defineTypes();
            } catch (final IOException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            return true;
        }
        return false;
    }

    /**
     * @param pProperty
     * @param pValue
     * @return
     */
    private Object checkSpecialXsdType(final SdoProperty pProperty, final String pValue) {
        if (DataObjectBehavior.isBase64BinaryProperty(pProperty)) {
            return Base64Util.decodeBase64(pValue);
        }
        if (DataObjectBehavior.isQnameProperty(pProperty)) {
            final SdoType<?> type = (SdoType<?>)pProperty.getType();
            if (type.getInstanceClass() == List.class) {
                final List<String> qNames = (List<String>)type.convertFromJavaClass(pValue);
                final List<String> uris = new ArrayList<String>(qNames.size());
                for (final String qName: qNames) {
                    uris.add(qNameToUri(qName));
                }
                return uris;
            } else {
                return qNameToUri(pValue);
            }
        }

        return pValue;
    }

    private String qNameToUri(final String pQName) {
        final URINamePair qName = URINamePair.getQNameFromString(pQName);
        String ns = getNamespaceContext().getNamespaceURI(qName.getURI());
        if (ns != null) {
            qName.setURI(ns);
            return qName.toStandardSdoFormat();
        }
        return pQName;
    }

    private DataObjectDecorator getParentDataObject() {
        if (_level > 0) {
            return _elements.get(_level - 1)._dataObject;
        }
        return null;
    }

    PathReference getPathReference() {
        return new PathReference((List)_elements.subList(_pathOffset, _level + 1));
    }

    private void resolveSchemaLocations(final String pAttributeContent) {
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

    private void resolveSchemaLocation(final String pNamespace, final String pLocation) {
        try {
            final SchemaResolver schemaResolver = getSchemaResolver();
            final String location =
                schemaResolver.getAbsoluteSchemaLocation(
                    pLocation,
                    (String)_options.get(SdoContentHandlerImpl.OPTION_KEY_LOCATION_URI));
            final SchemaLocation schemaLocation = new SchemaLocation(pNamespace, location);

            getUndefinedXsdToTypesTranslator().addSchemaLocation(schemaLocation);
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
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

    private DataObjectDecorator getOrCreateDataObject(Attributes pAtts) {
        DataObjectDecorator keyRefObject = null;
        SdoType<?> type = getElement()._valueType;
        Key key = null;
        if (Boolean.TRUE.equals(type.hasXmlFriendlyKey())) {
            SdoType<?> keyType = (SdoType<?>)type.getKeyType();
            DataObjectDecorator parentDo = getParentDataObject();
            if (keyType.isDataType()) {
                SdoProperty keyProperty = type.getKeyProperties().get(0);
                if (keyProperty.isOppositeContainment() && parentDo != null) {
                    key = new Key(type.getTypeForKeyUniqueness(), DataObjectBehavior.getKey(parentDo));
                } else {
                    final String id = getAttributeValue(keyProperty, pAtts);
                    if (id != null) {
                        key = new Key(type.getTypeForKeyUniqueness(), id);
                    }
                }
            } else {
                DataObject keyDo = _helperContext.getDataFactory().create(keyType);
                List<SdoProperty> keyProperties = type.getKeyProperties();
                for (int i = 0; i < keyProperties.size(); i++) {
                    SdoProperty keyProperty = keyProperties.get(i);
                    if (keyProperty.isOppositeContainment() && parentDo != null) {
                        Object keyObject = DataObjectBehavior.getKey(parentDo);
                        if (keyObject == null) {
                            keyDo = null;
                            break;
                        }
                        SdoProperty keyTypeProperty = keyType.getPropertyFromXmlName(keyProperty.getUri(), keyProperty.getName(), keyObject instanceof DataObject);
                        keyDo.set(keyTypeProperty, keyObject);
                    } else {
                        final String id = getAttributeValue(keyProperty, pAtts);
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
        PathReference pathReference = null;
        // in most cases pathReference is not needed
        if (_referenceToObject != null || !_createdReferences.isEmpty() || !_deletedReferences.isEmpty()) {
            pathReference = getPathReference();
        }
        if (_referenceToObject != null) {
            pathObject = referenceToObjectRemove(pathReference);
        }
        DataObjectDecorator obj = pathObject;
        if (obj == null) {
            obj = keyRefObject;
        }
        if (obj == null || ((SdoType<?>)obj.getInstance().getType()).getKeyType() == type) {
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
        if (pathReference != null) {
            if (_createdReferences.remove(pathReference)) {
                obj.getInternalModifier().setChangeStateWithoutCheck(State.CREATED);
            }
            if (_deletedReferences.remove(pathReference)) {
                obj.getInternalModifier().setChangeStateWithoutCheck(State.DELETED);
            }
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

    private DataObjectDecorator getOrCreateReferencedDataObject(Reference pReference, SdoType<?> type) {
        if (pReference == null) {
            return null;
        }
        DataObjectDecorator dataObject = null;
        if (_referenceToObject != null) {
            dataObject = _referenceToObject.get(pReference);
        }
        if (dataObject == null) {
            dataObject = resolvePathReference(pReference);
        }
        if (dataObject != null) {
            dataObject.getInstance().refineType(type);
        }
        if (dataObject == null) {
        if (type.isAbstract()) {
            final TypeHelperImpl typeHelper = (TypeHelperImpl)_helperContext.getTypeHelper();
            final TypeAndContext typeAndContext = typeHelper.getTypeAndContext(type);
            dataObject = new GenericDataObject(typeAndContext);
        } else {
            dataObject = (DataObjectDecorator)_helperContext.getDataFactory().create(type);
        }
        referenceToObjectPut(pReference, dataObject);
        }
        if (_createdReferences.remove(pReference)) {
            dataObject.getInternalModifier().setChangeStateWithoutCheck(State.CREATED);
        }
        if (_deletedReferences.remove(pReference)) {
            dataObject.getInternalModifier().setChangeStateWithoutCheck(State.DELETED);
        }
        if (_parseMode == ParseMode.CHANGE_SUMMARY) {
            dataObject = OldStateDataObject.getOldStateFacade(dataObject);
        }
        return dataObject;
    }

    private SdoProperty getOrCreateSimpleContentProperty(final DataObjectDecorator pDataObject) {
        SdoProperty valueProp = (SdoProperty)pDataObject.getInternalModifier().findOpenProperty("", TypeType.VALUE, true);
        if (valueProp == null
                || !((DataObject)valueProp).getBoolean(PropertyType.getSimpleContentProperty())) {
            final DataObject propObj = _helperContext.getDataFactory().create(PropertyType.getInstance());
            propObj.setString(PropertyType.NAME, TypeType.VALUE);
            propObj.set(PropertyType.TYPE, getElement()._valueType);
            propObj.setBoolean(PropertyType.MANY, false);
            propObj.setBoolean(PropertyType.getXmlElementProperty(), true);
            propObj.setBoolean(PropertyType.getSimpleContentProperty(),true);

            if (getElement()._xsdType != null) {
                // the type is explicitly set
                final URINamePair defaultXsdType = SchemaTypeFactory.getInstance().getXsdName(getElement()._valueType.getQName());
                if (!getElement()._xsdType.equals(defaultXsdType)) {
                    propObj.setString(PropertyType.getXsdTypeProperty(), getElement()._xsdType.toStandardSdoFormat());
                }
            }

            valueProp = (SdoProperty)_helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        }
        return valueProp;
    }

    private String getAttributeValue(SdoProperty pProperty, Attributes pAtts) {
        String uri = pProperty.getUri();
        if (uri == null) {
            uri = XMLConstants.NULL_NS_URI;
        }
        return pAtts.getValue(uri, pProperty.getXmlName());
    }

    private DataObjectDecorator resolvePathReference(final Reference pathRef) {

        if (pathRef.isKeyReference()) {
            return null;
        }
        DataObjectDecorator ref = _elements.get(0)._dataObject;
        final Iterator<PathReferenceStep> steps = ((PathReference)pathRef).getSteps().iterator();
        if (steps.hasNext() && ref.getType() != DataGraphType.getInstance()) {
            // first step points to _dataObject
            steps.next();
        }
        while (steps.hasNext() && ref != null) {
            final PathReferenceStep step = steps.next();
            final SdoProperty prop =
                (SdoProperty)ref.getInstance().getInstanceProperty(step.getUri(), step.getName(), true);
            Object result = null;
            if (prop != null) {
                result = ref.get(prop);
                if (prop.isMany() && ((List<?>)result).size() > step.getIndex()) {
                    result = ((List<?>)result).get(step.getIndex());
                } else if (prop.isOrphanHolder()) {
                    List<DataObjectDecorator> orphanList = _orphanList.get(new OrphanHolder(ref, prop));
                    if (orphanList != null && orphanList.size() > step.getIndex()) {
                        result = orphanList.get(step.getIndex());
                    }
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

    private class Element implements PathReferenceStep {
        String _uri;
        String _name;
        int _index;
        DataObjectDecorator _dataObject;
        boolean _nil = false;
        SdoProperty _property;
        SdoType<?> _valueType;
        URINamePair _xsdType;
        SdoNamespaceContext _namespaceContext;
        ContentHandler _contentHandler;
        StringBuilder _characters = new StringBuilder();
        boolean _skipData = false;

        public int getIndex() {
            return _index;
        }

        public String getName() {
            return _name;
        }

        public String getUri() {
            return _uri;
        }

        public boolean isIdRef() {
            return false;
        }

        public void clear() {
            _uri = null;
            _name = null;
            _index = 0;
            _dataObject = null;
            _property = null;
            _valueType = null;
            _xsdType = null;
            _namespaceContext = null;
            _characters.setLength(0);
            _skipData = false;
        }

        @Override
        public boolean equals(Object pObj) {
            if (pObj == this) {
                return true;
            }
            if (!(pObj instanceof PathReferenceStep)) {
                return false;
            }
            PathReferenceStep step = (PathReferenceStep)pObj;
            String stepUri = step.getUri();
            if (stepUri == null) {
                stepUri = "";
            }
            return step.getIndex() == _index &&
                   step.getName().equals(_name) &&
                   stepUri.equals(_uri);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 37*result + _name.hashCode();
            result = 37*result + _index;
            return result;
        }

        @Override
        public String toString() {
            return _name + '.' + _index;
        }

    }

    private class IntegrationElement extends Element implements IntegrationHandler.Element {
        Map<Property, IndexMode> _propsIndexMode = new HashMap<Property, IndexMode>();
        Mode _mode;
        int _index;

        @Override
        public void clear() {
            super.clear();
            _propsIndexMode.clear();
            _mode = null;
            _index = 0;
        }
        
    }
    
    private class IndexMode {
        int _index;
        Mode _integrationMode;
    }

    private Set<Reference> getReferences(final String pReferenceString, final PathReference pCurrentPath, final Set<Reference> pExistingReferences) {
        final Set<Reference> references = new HashSet<Reference>();
        if (pReferenceString != null) {
            final StringTokenizer tokenizer = new StringTokenizer(pReferenceString);
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


    private void addProperty(DataObjectDecorator parent, SdoProperty property, Object value) {
        try {
            parent.getInternalModifier().addPropertyValueWithoutCheck(property, value);
        } catch (final IllegalStateException ex) {
            Property globalProperty = null;
            if (parent.getType().isOpen() && !property.isMany() && !property.isOpenContent()) {
                globalProperty = _helperContext.getXSDHelper().getGlobalProperty(
                    property.getUri(), property.getXmlName(), true);
            }
            if (globalProperty == null) {
                _errorHandler.handleInvalidValue(ex);
            } else {
                try {
                    parent.getInternalModifier().addPropertyValueWithoutCheck(globalProperty, value);
                } catch (final RuntimeException ex2) {
                    _errorHandler.handleInvalidValue(ex2);
                }
            }
        } catch (final RuntimeException ex) {
            _errorHandler.handleInvalidValue(ex);
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

    private class ComplexContentHandler extends BaseContentHandler {

        @Override
        public void startElement(String pUri, String pLocalName, String pName, Attributes pAtts) throws SAXException {
            deferredCharacters();
            Element element = newElementLevel();
            if (pUri == null) {
                element._uri = XMLConstants.DEFAULT_NS_PREFIX;
            } else {
                element._uri = pUri;
            }
            element._name = pLocalName;
            if ((_parseMode == ParseMode.XML) && URINamePair.PROP_SCHEMA_SCHEMA.equalsUriName(element._uri, element._name)) {
                // if a schema element is found while parsing xml
                _parseMode = ParseMode.XSD;
            }
            if (_parseMode == ParseMode.XML) {
                parseSchemaLocationProperty(pAtts);
            }
            getPropertyAndType(pAtts);
            if (_parseMode == ParseMode.CHANGE_SUMMARY) {
                String ref = pAtts.getValue(SDO_REF.getURI(), SDO_REF.getName());
                if (ref != null) {
                    final Reference reference = Reference.createReference(
                        getPathReference(), element._valueType, ref, getNamespaceContext());
                    element._dataObject = getOrCreateReferencedDataObject(reference, element._valueType);
                    DataObjectDecorator parent = getParentDataObject();
                    parent.getInternalModifier().addPropertyValueWithoutCheck(element._property, element._dataObject);
                    if (element._property.isContainment()) {
                        final Property oppositeProp = element._property.getOpposite();
                        if (oppositeProp != null) {
                            element._dataObject.getInternalModifier().setPropertyWithoutCheck(oppositeProp, parent);
                        }
                    }
                    return;
                }
            }
            if (ChangeSummaryType.getInstance() == element._valueType) {
                _parseMode = ParseMode.CHANGE_SUMMARY;
                element._contentHandler = new ChangeSummaryContentHandler();
                PathReference path = getPathReference();
                _createdReferences = getReferences(pAtts.getValue(XMLConstants.NULL_NS_URI, ChangeSummaryType.CREATE), path, _createdReferences);
                _deletedReferences = getReferences(pAtts.getValue(XMLConstants.NULL_NS_URI, ChangeSummaryType.DELETE), path, _deletedReferences);
                _orphansInCs.clear();
                final boolean logging = !Boolean.FALSE.toString().equals(
                    pAtts.getValue(XMLConstants.NULL_NS_URI, ChangeSummaryType.LOGGING));
                ChangeSummary changeSummary = getParentDataObject().getChangeSummary();
                _deletedOrphans.put(changeSummary, new ArrayList<DataObjectDecorator>());
                if (logging) {
                    //TODO collecting the change summaries and starting them later
                    // avoids a bug at getPropValue, that modifies the DataObject in some special cases
                    _loggingChangeSummaries.add(changeSummary);
                } else if (!_createdReferences.isEmpty() || !_deletedReferences.isEmpty()) {
                    _stoppedChangeSummaries.add(changeSummary);
                } else {
                    _currentChangeSummary = changeSummary;
                }
                return;
            }
            boolean nil = Boolean.parseBoolean(pAtts.getValue(XSI_NIL.getURI(), XSI_NIL.getName()));
            element._nil = nil;
            DataObjectDecorator parent = getParentDataObject();
            IntegrationElement integrationElement = null;
            if (_integrationHandler != null) {
                integrationElement = (IntegrationElement)element;
                if (!element._property.isMany() && element._valueType.isDataType()) {
                    integrationElement._mode = Mode.SET;
                } else if (_level > 0) {
                    IntegrationElement parentElement = (IntegrationElement)_elements.get(_level - 1);
                    if (parentElement._mode != Mode.MERGE) {
                        integrationElement._mode = Mode.APPEND;
                    } else if (element._property.isMany()) {
                        Map<Property, IndexMode> propsIndexMode = parentElement._propsIndexMode;
                        IndexMode indexMode = propsIndexMode.get(element._property);
                        if (indexMode == null) {
                            integrationElement._mode = getIntegrationMode();
                            indexMode = new IndexMode();
                            indexMode._index = 0;
                            indexMode._integrationMode = integrationElement._mode;
                            propsIndexMode.put(element._property, indexMode);
                            
                            if (integrationElement._mode == Mode.SET) {
                                parent.getList(element._property).clear();
                            }
                        } else {
                            indexMode._index++;
                            element._index = indexMode._index;
                            integrationElement._mode = indexMode._integrationMode;
                        }
                    }
                }
                
                if (integrationElement._mode == null) {
                    integrationElement._mode = getIntegrationMode();
                }
            }
            if (element._property.getType().isDataType() && _level > 0) {
                element._contentHandler = _simpleContentHandler;
                return;
            }

            List<DataObjectDecorator> orphanList = null;
            if (parent != null && element._property.isMany()) {
                if (parent.isSet(element._property)) {
                    if (integrationElement == null) {
                        element._index = parent.getList(element._property).size();
                    }
                } else if (element._property.isOrphanHolder()) {
                    OrphanHolder orphanHolder = new OrphanHolder(parent, element._property);
                    orphanList = _orphanList.get(orphanHolder);
                    if (orphanList == null) {
                        orphanList = new ArrayList<DataObjectDecorator>();
                        _orphanList.put(orphanHolder, orphanList);
                    }
                    element._index = orphanList.size();
                }
            } else if (_level == 0 && element._valueType == DataGraphType.getInstance()) {
                _pathOffset = 1;
            }

            if (!element._property.isContainment() && _level > 0) {
                element._contentHandler = _referenceContentHandler;
                Type keyType = element._valueType.getKeyType();
                if (keyType != null && !keyType.isDataType()) {
                    element._dataObject = (DataObjectDecorator)_helperContext.getDataFactory().create(keyType);
                    parseAttributeProperties(pAtts);
                }
                return;
            }

            element._contentHandler = _complexContentHandler;
            SdoProperty valueProperty;
            boolean integratedDO = false;
            if (element._valueType.isDataType()) {
                SdoType<?> tmpType = element._valueType;
                element._valueType = OpenType.getInstance();
                element._dataObject = getOrCreateDataObject(pAtts);
                element._valueType = tmpType;
                valueProperty = getOrCreateSimpleContentProperty(element._dataObject);
            } else {
                if (integrationElement != null && integrationElement._mode == Mode.MERGE) {
                    if (_level == 0) {
                        element._dataObject = (DataObjectDecorator)_integrationHandler.getRootObject();
                    } else {
                        if (element._property.isMany()) {
                            List<DataObject> values = parent.getList(element._property);
                            if (element._index < values.size()) {
                                element._dataObject = (DataObjectDecorator)values.get(element._index);
                            }
                        } else {
                            element._dataObject = (DataObjectDecorator)parent.getDataObject(element._property);
                        }
                    }
                }
                if (element._dataObject == null) {
                    element._dataObject = getOrCreateDataObject(pAtts);
                } else {
                    integratedDO = true;
                }
                valueProperty = TypeHelperImpl.getSimpleContentProperty(element._dataObject.getInstance());
            }

            if (!integratedDO && parent != null && !element._skipData) {
                if (orphanList != null) {
                    orphanList.add(element._dataObject);
                } else {
                    boolean hasKeyWithElements = Boolean.FALSE.equals(element._valueType.hasXmlFriendlyKey());
                    if (hasKeyWithElements) {
                        element._dataObject.getInternalModifier().setContainerWithoutCheck(parent, element._property);
                    } else {
                        parent.getInternalModifier().addPropertyValueWithoutCheck(element._property, element._dataObject);
                    }
                    final Property opposite = element._property.getOpposite();
                    if (opposite != null) {
                        element._dataObject.getInternalModifier().setPropertyWithoutCheck(opposite, parent);
                    }
                }
            }

            if (_parseMode == ParseMode.CHANGE_SUMMARY) {
                parseUnsetProperties(pAtts);
                element._dataObject.getInternalModifier().setChangeStateWithoutCheck(State.DELETED);
            }

            if (!element._valueType.isDataType() || _level == 0) {
                parseAttributeProperties(pAtts);
            }

            if (valueProperty != null) {
                Element valueElement = newElementLevel();
                valueElement._name = TypeType.VALUE;
                valueElement._property = valueProperty;
                valueElement._contentHandler = _valueContentHandler;
                valueElement._nil = nil;

                if (element._xsdType != null) {
                    // the type is explicitly set
                    final URINamePair defaultXsdType =
                        SchemaTypeFactory.getInstance().getXsdName(element._valueType.getQName());
                    if (!element._xsdType.equals(defaultXsdType)) {
                        valueElement._xsdType = element._xsdType;
                    }
                }

            }
        }

        @Override
        public void endElement(String pUri, String pLocalName, String pName) throws SAXException {
            deferredCharacters();
            Element element = getElement();
            if (Boolean.FALSE.equals(element._valueType.hasXmlFriendlyKey())) {
                Object key = DataObjectBehavior.getKey(element._dataObject);
                if (key != null) {
                    Key reference = new Key(element._valueType.getTypeForKeyUniqueness(), key);
                    DataObjectDecorator refObject = referenceToObjectGet(reference);
                    if (refObject != null) {
                        refObject.getInstance().stealCurrentState(element._dataObject);
                        element._dataObject = refObject;
                    } else {
                        referenceToObjectPut(reference, element._dataObject);
                    }
                    DataObjectDecorator parent = getParentDataObject();
                    if (parent != null) {
                        addProperty(parent, element._property, element._dataObject);
                    }
                }
            }
            GenericDataObject gdo = element._dataObject.getInstance();
            if (_simplifyOpenContent && _parseMode == ParseMode.XML) {
                gdo.simplifyOpenContent();
            }
            gdo.setXsiNilWithoutCheck(element._nil);
            gdo.trimMemory();
            if ((_parseMode == ParseMode.XSD) && URINamePair.PROP_SCHEMA_SCHEMA.equalsUriName(element._uri, element._name)) {
                final Schema schema = (Schema)gdo.getFacade();
                final XsdToTypesTranslator xsdToTypesTranslator = getUndefinedXsdToTypesTranslator();
                String locationUri = (String)_options.get(SdoContentHandlerImpl.OPTION_KEY_LOCATION_URI);
                xsdToTypesTranslator.addSchema(new SchemaLocation(schema.getTargetNamespace(), locationUri), schema);
                _parseMode = ParseMode.XML;
            }

            if (_level == 0) {
                for (final Reference ref : _createdReferences) {
                    resolvePathReference(ref).getInternalModifier().setChangeStateWithoutCheck(State.CREATED);
                }
                for (final ChangeSummary changeSummary: _loggingChangeSummaries) {
                    ((ChangeSummaryImpl)changeSummary).beginLoggingWithoutCheck();
                }
                for (final ChangeSummary changeSummary: _stoppedChangeSummaries) {
                    changeSummary.endLogging();
                }
                for (Entry<ChangeSummary, List<DataObjectDecorator>> entry : _deletedOrphans.entrySet()) {
                    ((ChangeSummaryImpl)entry.getKey()).addDeletedOrphansWithoutCheck(entry.getValue());
                }
                if (_validator != null) {
                    try {
                        _validator.validate(gdo);
                    } catch (final IllegalArgumentException e) {
                        throw new SAXParseException(e.getMessage(), null, e);
                    }
                }
                _xmlDocument = new XMLDocumentImpl(gdo.getFacade(), element._uri, element._name);
                _xmlDocument.setNoNamespaceSchemaLocation(_noNamespaceSchemaLocation);
                _xmlDocument.setSchemaLocation(getSchemaLocation());
                _xmlDocument.setXsdToTypesTranslators(_xsdToTypesTranslators);


            }
            element.clear();
            _level--;
        }

        @Override
        public void startPrefixMapping(String prefix, String pUri) throws SAXException {
            deferredCharacters();
            Element element = newElementLevel();
            element._namespaceContext.addPrefix(prefix, pUri);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
        }

        @Override
        public void characters(char[] pCh, int pStart, int pLength) throws SAXException {
            getElement()._characters.append(pCh, pStart, pLength);
        }

        @Override
        public void ignorableWhitespace(char[] pCh, int pStart, int pLength) throws SAXException {
            getElement()._characters.append(pCh, pStart, pLength);
        }

        private void deferredCharacters() {
            Element element = getElement();
            StringBuilder characters = element._characters;
            if (characters.length() == 0) {
                return;
            }
            if (element._valueType.isMixedContent()) {
                element._dataObject.getInternalModifier().addToSequenceWithoutCheck(null, characters.toString());
            }
            characters.setLength(0);
        }

    }

    private class SimpleContentHandler extends BaseContentHandler {

        @Override
        public void characters(char[] pCh, int pStart, int pLength) throws SAXException {
            getElement()._characters.append(pCh, pStart, pLength);
        }

        @Override
        public void ignorableWhitespace(char[] pCh, int pStart, int pLength) throws SAXException {
            getElement()._characters.append(pCh, pStart, pLength);
        }

        @Override
        public void endElement(String pUri, String pLocalName, String pName) throws SAXException {
            Element element = getElement();
            StringBuilder characters = element._characters;
            DataObjectDecorator dataObject = getParentDataObject();
            Object value;
            if (element._nil) {
                value = null;
            } else {
                value = checkSpecialXsdType(element._property, characters.toString());
                if (JavaSimpleType.OBJECT == element._property.getType() && JavaSimpleType.STRING != element._valueType) {
                    value = element._valueType.convertFromJavaClass(value);
                }
            }
            if (_integrationHandler != null) {
                IntegrationElement integrationElement = (IntegrationElement)element;
                if (integrationElement._mode == Mode.MERGE && element._property.isMany()) {
                    List values = dataObject.getList(element._property);
                    if (values.size() > element._index) {
                        values.set(element._index, value);
                    } else {
                        values.add(value);
                    }
                } else {
                    dataObject.getInternalModifier().addPropertyValueWithoutCheck(element._property, value);
                }
            } else {
                try {
                    addProperty(dataObject, element._property, value);
                } catch (RuntimeException e) {
                    // empty string tolerant
                    if (!"".equals(value) || _errorHandler != DEFAULT_ERROR_HANDLER) {
                        _errorHandler.handleInvalidValue(e);
                    }
                }
            }
            element.clear();
            _level--;
        }
    }

    private class ReferenceContentHandler extends ComplexContentHandler {

        @Override
        public void endElement(String pUri, String pLocalName, String pName) throws SAXException {
            Element element = getElement();
            DataObjectDecorator parent = getParentDataObject();
            if (element._valueType == TypeType.getInstance()) {
                final URINamePair uriName = URINamePair.fromStandardSdoFormat(element._characters.toString());
                addProperty(parent, element._property, createTypeDummy(uriName));
                element.clear();
                _level--;
                return;
            }
            DataObjectDecorator value;
            if (element._nil) {
                value = null;
            } else {
                DataObjectDecorator keyDo = element._dataObject;
                Reference reference;
                if (keyDo != null && isKeyComplete(keyDo)) {
                    reference = new Key(element._valueType.getTypeForKeyUniqueness(), keyDo);
                } else {
                    reference = Reference.createReference(getPathReference(), element._valueType, element._characters.toString(), getNamespaceContext());
                }
                value = getOrCreateReferencedDataObject(reference, element._valueType);
            }
            addProperty(parent, element._property, value);
            element.clear();
            _level--;
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

    }

    private class ValuePropContentHandler extends BaseContentHandler {

        @Override
        public void characters(char[] pCh, int pStart, int pLength) throws SAXException {
            getElement()._characters.append(pCh, pStart, pLength);
        }

        @Override
        public void endElement(String pUri, String pLocalName, String pName) throws SAXException {
            Element element = getElement();
            StringBuilder characters = element._characters;
            DataObjectDecorator dataObject = getParentDataObject();
            Object value;
            if (element._nil) {
                value = null;
            } else {
                value = checkSpecialXsdType(element._property, characters.toString());
            }
            try {
            dataObject.getInternalModifier().addPropertyValueWithoutCheck(element._property, value);
            } catch (RuntimeException e) {
                // empty string tolerant
                if (!"".equals(value) || _errorHandler != DEFAULT_ERROR_HANDLER) {
                    _errorHandler.handleInvalidValue(e);
                }
            }
            element.clear();
            _level--;
            getContentHandler().endElement(pUri, pLocalName, pName);
        }
    }

    private class ChangeSummaryContentHandler extends BaseContentHandler {

        @Override
        public void characters(char[] pCh, int pStart, int pLength)
            throws SAXException {
        }

        @Override
        public void ignorableWhitespace(char[] pCh, int pStart, int pLength)
            throws SAXException {
        }

        @Override
        public void startElement(String pUri, String pLocalName, String pName,
            Attributes pAtts) throws SAXException {

            if (_currentChangeSummary != null) {
                _stoppedChangeSummaries.add(_currentChangeSummary);
                _currentChangeSummary = null;
            }
            Element element = newElementLevel();
            if (pUri == null) {
                element._uri = XMLConstants.DEFAULT_NS_PREFIX;
            } else {
                element._uri = pUri;
            }
            element._name = pLocalName;
            getPropertyAndType(pAtts);
            element._contentHandler = _complexContentHandler;
            // tracking indexes used for deleted orphans only
            Integer index = _orphansInCs.get(element._property);
            if (index == null) {
                index = 0;
            }
            element._index = index;
            _orphansInCs.put(element._property, ++index);
            final Reference reference = Reference.createReference(
                getPathReference(), element._valueType,
                pAtts.getValue(SDO_REF.getURI(), SDO_REF.getName()),
                getNamespaceContext());

            SdoProperty valueProperty = null;

            if (element._valueType.isDataType()) {
                valueProperty = TypeHelperImpl.getSimpleContentProperty(element._property.getType());
                if (valueProperty != null) {
                    element._dataObject = getOrCreateReferencedDataObject(reference, (SdoType<?>)element._property.getType());
                } else {
                    element._dataObject = getOrCreateReferencedDataObject(reference, OpenType.getInstance());
                    valueProperty = getOrCreateSimpleContentProperty(element._dataObject);
                }
                element._dataObject.getInternalModifier().setChangeStateWithoutCheck(State.MODIFIED);
            } else if (reference != null) {
                valueProperty = TypeHelperImpl.getSimpleContentProperty(element._valueType);
                element._dataObject = getOrCreateReferencedDataObject(reference, element._valueType);
                element._dataObject.getInternalModifier().setChangeStateWithoutCheck(State.MODIFIED);
            } else {
                // deleted orphan
                element._dataObject = getOrCreateDataObject(pAtts).getInstance();
                List<DataObjectDecorator> orphans = _deletedOrphans.get(_elements.get(_level-2)._dataObject.getChangeSummary());
                if (orphans != null) {
                    orphans.add(element._dataObject);
            }
            }
            parseAttributeProperties(pAtts);
            parseUnsetProperties(pAtts);

            if (valueProperty == null) {
                element._contentHandler = _complexContentHandler;
            } else {
                Element valueElement = newElementLevel();
                valueElement._name = TypeType.VALUE;
                valueElement._property = valueProperty;
                valueElement._contentHandler = _valueContentHandler;
//                valueElement._nil = nil;

                if (element._xsdType != null) {
                    // the type is explicitly set
                    final URINamePair defaultXsdType =
                        SchemaTypeFactory.getInstance().getXsdName(element._valueType.getQName());
                    if (!element._xsdType.equals(defaultXsdType)) {
                        valueElement._xsdType = element._xsdType;
                    }
                }

            }

        }

        @Override
        public void endElement(String pUri, String pLocalName, String pName)
            throws SAXException {
            _parseMode = ParseMode.XML;
            Element element = getElement();
            if (ChangeSummaryType.getInstance() == element._valueType) {
                _currentChangeSummary = null;
            }
            element.clear();
            _level--;
        }

        @Override
        public void startPrefixMapping(String prefix, String pUri) throws SAXException {
            Element element = newElementLevel();
            element._namespaceContext.addPrefix(prefix, pUri);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
        }

    }

    private class BaseContentHandler implements ContentHandler {

        public void characters(char[] pCh, int pStart, int pLength) throws SAXException {
            throw new SAXException("Illegal event: characters(\""
                + new String(pCh) + "\", " + pStart + ", " + pLength + ") at " + getPathReference());
        }

        public void endDocument() throws SAXException {
            throw new SAXException("Illegal event: endDocument at " + getPathReference());
        }

        public void endElement(String pUri, String pLocalName, String pName) throws SAXException {
            throw new SAXException("Illegal event: endElement(\""
                + pUri + "\", \"" + pLocalName + "\", \"" + pName + "\") at " + getPathReference());
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            throw new SAXException("Illegal event: endPrefixMapping(\"" + prefix + "\") at " + getPathReference());
        }

        public void ignorableWhitespace(char[] pCh, int pStart, int pLength) throws SAXException {
            throw new SAXException("Illegal event: ignorableWhitespace(\""
                + new String(pCh) + "\", " + pStart + ", " + pLength + ") at " + getPathReference());
        }

        public void processingInstruction(String pTarget, String pData) throws SAXException {
            throw new SAXException("Illegal event: processingInstruction(\""
                + pTarget + ", " + pData + ") at " + getPathReference());
        }

        public void setDocumentLocator(Locator pLocator) {
        }

        public void skippedEntity(String pName) throws SAXException {
            throw new SAXException("Illegal event: skippedEntity(\"" + pName + "\") at " + getPathReference());
        }

        public void startDocument() throws SAXException {
            throw new SAXException("Illegal event: startDocument at " + getPathReference());
        }

        public void startElement(String pUri, String pLocalName, String pName, Attributes pAtts) throws SAXException {
            throw new SAXException("Illegal event: startElement(\""
                + pUri + "\", \"" + pLocalName + "\", \"" + pName + "\") at " + getPathReference());
        }

        public void startPrefixMapping(String prefix, String pUri) throws SAXException {
            throw new SAXException("Illegal event: startPrefixMapping(\""
                + prefix + "\", \"" + pUri + "\") at " + getPathReference());
        }

    }
}
