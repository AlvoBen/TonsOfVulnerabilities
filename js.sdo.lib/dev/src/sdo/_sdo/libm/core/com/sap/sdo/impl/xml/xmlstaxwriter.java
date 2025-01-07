/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.xml;

import static com.sap.sdo.api.util.URINamePair.CHANGESUMMARY_TYPE;
import static com.sap.sdo.api.util.URINamePair.DATAGRAPH_TYPE;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_JAVA_URI;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_URI;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_XML_URI;
import static com.sap.sdo.api.util.URINamePair.MIXEDTEXT_TYPE;
import static com.sap.sdo.api.util.URINamePair.PROP_SCHEMA_SCHEMA;
import static com.sap.sdo.api.util.URINamePair.SCHEMA_BASE64BINARY;
import static com.sap.sdo.api.util.URINamePair.SCHEMA_Q_NAME;
import static com.sap.sdo.api.util.URINamePair.SCHEMA_SCHEMA;
import static com.sap.sdo.api.util.URINamePair.SCHEMA_URI;
import static com.sap.sdo.api.util.URINamePair.STRING;
import static com.sap.sdo.api.util.URINamePair.TYPE;
import static com.sap.sdo.api.util.URINamePair.XMLNS_URI;
import static com.sap.sdo.api.util.URINamePair.XML_URI;
import static com.sap.sdo.api.util.URINamePair.XSD_TYPE;
import static com.sap.sdo.api.util.URINamePair.XSI_URI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.types.schema.Import;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.Namespace;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.ChangeSummaryType;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.OpenType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.DataGraphType.XsdType;
import com.sap.sdo.impl.types.schema.SchemaTypeFactory;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.types.simple.ListSimpleType;
import com.sap.sdo.impl.util.Base64Util;
import com.sap.sdo.impl.util.DataObjectBehavior;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class XmlStaxWriter {
    private static final String PREFIX_NS = "ns";
    private static final String PREFIX_TNS = "tns";
    private static final String PREFIX_SDO = "sdo";
    private static final String PREFIX_SDO_JAVA = "sdoj";
    private static final String PREFIX_SDO_XML = "sdox";
    private static final String PREFIX_XML = "xml";
    private static final String PREFIX_XMLNS = "xmlns";
    private static final String PREFIX_XSD = "xsd";
    private static final String PREFIX_XSI = "xsi";
    private Map<String,Integer> _lastIndexMap = new HashMap<String,Integer>();

    private int _indentation = 0;
    private String _indentionStr = "  ";
    private final XMLStreamWriter _output;
    private final Set<String> _prefixes = new HashSet<String>();
    private XMLDocument _xmlDocument = null;
    private OrphanHandler _orphanHandler = null;
    private ReferenceHandler _referenceHandler = null;
    private final SapHelperContext _helperContext;
    private final Map<String,String> _emptyMap = Collections.emptyMap();
    private final Type _schemaType;

    private static final Map<String, String> _namespaceToPredefinedPrefix = new HashMap<String, String>();

    {
        _namespaceToPredefinedPrefix.put(XML_URI, PREFIX_XML);
        _namespaceToPredefinedPrefix.put(XMLNS_URI, PREFIX_XMLNS);
    }

    public XmlStaxWriter(final XMLStreamWriter pOutputWriter, final SapHelperContext pHelperContext) {
        _helperContext = pHelperContext;
        _output = pOutputWriter;
        _schemaType = _helperContext.getTypeHelper().getType(
            SCHEMA_SCHEMA.getURI(), SCHEMA_SCHEMA.getName());
    }
    public void generate(final XMLDocument pXmlDocument, final Property p, final Map<?,?> pOptions) throws XMLStreamException {
        _xmlDocument = pXmlDocument;
        _orphanHandler = new OrphanHandler(pXmlDocument.getRootObject());
        _referenceHandler = new ReferenceHandlerImpl(pXmlDocument, _orphanHandler);

        if (pXmlDocument.isXMLDeclaration()) {
            String version = pXmlDocument.getXMLVersion();
            String encoding = pXmlDocument.getEncoding();
            if (encoding != null) {
                _output.writeStartDocument(
                    encoding,
                    version != null ? version : "1.0");
            } else if (version != null) {
                _output.writeStartDocument(version);
            } else {
                _output.writeStartDocument();
            }
            writeNewLine(false);
        }
        Map<String,String> nsDefinitions = new HashMap<String,String>();
        if (pOptions != null) {
            Map<String,String> prefixMap =
                (Map<String,String>)pOptions.get(SapXmlHelper.OPTION_KEY_PREFIX_MAP);
            if (prefixMap != null) {
                for (Entry<String,String> uriMapping : prefixMap.entrySet()) {
                    nsDefinitions.put(uriMapping.getKey(),uriMapping.getValue());
                }
            }

            if (pOptions.containsKey((SapXmlHelper.OPTION_KEY_INDENT))) {
                _indentionStr = (String)pOptions.get((SapXmlHelper.OPTION_KEY_INDENT));
            }
        }
        if (_output.getPrefix(XSI_URI) == null
                && (p == null
                     || !PROP_SCHEMA_SCHEMA.equalsUriName(
                        ((SdoProperty)p).getUri(), ((SdoProperty)p).getName()))) {
            nsDefinitions.put(PREFIX_XSI, XSI_URI);
        }
        if (_output.getPrefix(SCHEMA_URI) == null) {
            nsDefinitions.put(PREFIX_XSD, SCHEMA_URI);
        }

        writeElement(
            pXmlDocument.getRootElementURI(),
            pXmlDocument.getRootElementName(),
            pXmlDocument.getRootObject(),
            (SdoProperty)p, false,
            nsDefinitions);
        if (pXmlDocument.isXMLDeclaration()) {
            _output.writeEndDocument();
        }
    }

    /**
     * Outputs into the Writer an elment with uri and name of pElement.
     * The 'pData' should be serialized as:
     * <p>- if the 'pData' is mapped from schema primitive type,
     *      that is in java representation Integer, String, Decimal, etc
     *      then based on uri and name of pXsdType it is serialized
     *      into XML (for example xs:Qname is mapped to String)
     * <p>- if the 'pData' is DataObject then it is serialized accordingly its XSD type.
     *      The pXsdType param wouldn't be taken in consideration in this case
     * @param pData
     * @param pType
     * @param pXsdType
     * @param pElement
     * @param pOptions
     */
    public void generate(Object pData, SdoType<Object> pType, URINamePair pXsdType, URINamePair pElement, Map<?,?> pOptions)
        throws XMLStreamException {

        writeStartElement(pElement.getURI(), pElement.getName());

        if (pData != null) {
            final String value;
            if (pData instanceof String) {
                if (SCHEMA_Q_NAME.equals(pXsdType)) {
                    URINamePair unp = URINamePair.fromStandardSdoFormat((String)pData);
                    value = getQname(unp.getURI(), unp.getName());
                } else {
                    value = (String)pData;
                }
            } else {
                Class<?> instanceClass = pType.getInstanceClass();
                final Object data;
                if (instanceClass != null && !instanceClass.isAssignableFrom(pData.getClass())) {
                    data = pType.convertFromJavaClass(pData);
                } else {
                    data = pData;
                }
                if (SCHEMA_BASE64BINARY.equals(pXsdType)) {
                    value = Base64Util.encodeBase64((byte[])data);
                } else {
                    value = pType.convertToJavaClass(data, String.class);
                }
            }
            _output.writeCharacters(value);
        } else {
            Property element =
                ((TypeHelperImpl)_helperContext.getTypeHelper())
                    .getOpenContentPropertyByXmlName(pElement.getURI(), pElement.getName(), true);
            if (element != null && !element.isNullable()) {
                throw new XMLStreamException("element " + pElement + " is not nillable");
            }
            if (_output.getPrefix(XSI_URI) == null) {
                _output.setPrefix(PREFIX_XSI, XSI_URI);
                _output.writeNamespace(PREFIX_XSI, XSI_URI);
            }
            _output.writeAttribute(XSI_URI, "nil", "true");
        }

        _output.writeEndElement();
        writeNewLine(false);
    }

    private String getPrefix(final String namespace) throws XMLStreamException {
        String prefix = _output.getPrefix(namespace);
        if (prefix == null) {
            prefix = _namespaceToPredefinedPrefix.get(namespace);
        }
        return prefix;
    }

    private String createPrefix(final String namespace) throws XMLStreamException {
        if (DATATYPE_URI.equals(namespace)) {
            return PREFIX_SDO;
        }
        if (DATATYPE_JAVA_URI.equals(namespace)) {
            return PREFIX_SDO_JAVA;
        }
        if (DATATYPE_XML_URI.equals(namespace)) {
            return PREFIX_SDO_XML;
        }
        return createPrefix(namespace, PREFIX_NS);
    }

    private String createPrefix(final String namespace, final String prefixGroup)
        throws XMLStreamException {

        Integer lastIndex = _lastIndexMap.get(prefixGroup);
        if (lastIndex == null) {
            lastIndex = 0;
        }
        String prefix;
        ++lastIndex;
        if (PREFIX_TNS.equals(prefixGroup) && lastIndex == 1) {
            prefix = PREFIX_TNS;
        } else {
            prefix = prefixGroup + lastIndex;
        }
        _prefixes.add(prefix);
        _lastIndexMap.put(prefixGroup, lastIndex);
        return prefix;
    }

    private SdoProperty findBestMatch(final DataObject pDataObject, final Property headProp) {
        Property best = headProp;
        DataObject headPropObj = (DataObject)headProp;
        if (headPropObj.isSet(PropertyType.getSubstitutesProperty())) {
            for (Object refObj: headPropObj.getList(PropertyType.getSubstitutesProperty())) {
                final URINamePair unp = URINamePair.fromStandardSdoFormat((String)refObj);
                final Property s = _helperContext.getXSDHelper().getGlobalProperty(unp.getURI(), unp.getName(), true);
                if (!s.getType().isInstance(pDataObject)) {
                    continue;
                }
                if (s.getType() == pDataObject.getType()) {
                    return (SdoProperty)s;
                } else if (((SdoType)best.getType()).isAssignableType(s.getType())) {
                    best = s;
                } else if (!(((SdoType)s.getType()).isAssignableType(best.getType()))) {
                    throw new IllegalArgumentException("Types are from different hierarchies in substitution group "+headProp.getName());
                }
            }
        }
        return (SdoProperty)best;
    }

    private String getQname(final Type pType) throws XMLStreamException {
        SdoType type = (SdoType)pType;
        return getQname(type.getXmlUri(), type.getXmlName());
    }

    private String getQname(final String uri, String name) throws XMLStreamException {
        String prefix = getPrefix(uri);
        if (prefix == null && uri.length() > 0) {
            prefix = createPrefix(uri);
            _output.setPrefix(prefix, uri);
            _output.writeNamespace(prefix, uri);
        }
        if (prefix != null && prefix.length()>0) {
            return prefix + ':' + name;
        }
        return name;
    }

    /**
     * @param value
     * @param ret
     * @return
     * @throws XMLStreamException
     */
    private String getQnameFromUriProperty(final String value) throws XMLStreamException {
        URINamePair uriName = URINamePair.fromStandardSdoFormat(value);
        String uri = uriName.getURI();
        if (uri.length() > 0) {
            String prefix = getPrefix(uri);
            if (prefix == null) {
                prefix = createPrefix(uri);
                _output.setPrefix(prefix, uri);
                _output.writeNamespace(prefix, uri);
            }
            if (prefix.length() > 0) {
                final StringBuilder ret = new StringBuilder();
                ret.append(prefix+':');
                ret.append(uriName.getName());
                return ret.toString();
            }
        }
        return uriName.getName();
    }

    private String getURI(final Type pType) {
        if (MIXEDTEXT_TYPE.equalsUriName(pType)) {
            return null;
        }
        return pType.getURI();
    }

    /**
     * @throws XMLStreamException
     */
    private void writeIndent(boolean pMixed) throws XMLStreamException {
        if (!pMixed && _indentionStr != null) {
            for(int i=0; i<_indentation; ++i) {
                _output.writeCharacters(_indentionStr);
            }
        }
    }

    /**
     * @throws XMLStreamException
     */
    private void writeIndentIncrease(boolean pMixed) throws XMLStreamException {
        writeIndent(pMixed);
        ++_indentation;
    }

    /**
     * @throws XMLStreamException
     */
    private void writeIndentDecrease(boolean pMixed) throws XMLStreamException {
        --_indentation;
        writeIndent(pMixed);
    }

    /**
     * @throws XMLStreamException
     */
    private void writeNewLine(boolean pMixed) throws XMLStreamException {
        if (!pMixed && _indentionStr != null) {
            _output.writeCharacters("\n");
        }
    }

    /**
     * @param namespaceURI
     * @param localName
     * @throws XMLStreamException
     */
    private void writeAttribute(final String pUri, final String pName, final SdoProperty pProperty, final String pValue) throws XMLStreamException {
        String value = pValue;
        if (pProperty != null && value != null) {
            value = DataObjectBehavior.encodeBase64Binary(pProperty, value);
            if (DataObjectBehavior.isQnameProperty(pProperty)) {
                if (value.indexOf(' ') > 0) {
                    final StringBuilder ret = new StringBuilder();
                    StringTokenizer tokenizer = new StringTokenizer(value, " ");
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        ret.append(getQnameFromUriProperty(token));
                        ret.append(' ');
                    }
                    value = ret.toString().trim();
                } else {
                    value = getQnameFromUriProperty(value);
                }
            }
        }
        if (pUri != null && pUri.length() > 0) {
            String prefix = getPrefix(pUri);
            if (prefix == null) {
                prefix = createPrefix(pUri);
                _output.writeAttribute(prefix, pUri, pName, value);
                _output.writeNamespace(prefix, pUri);
            } else {
                if (_namespaceToPredefinedPrefix.containsKey(pUri)) {
                    _output.writeAttribute(prefix, pUri, pName, value);
                } else {
                    _output.writeAttribute(pUri, pName, value);
                }
            }
        } else {
            _output.writeAttribute(pName, value);
        }
    }

    /**
     * @param pDataObject
     * @param pProperty
     * @throws XMLStreamException
     */
    private void writeXsiTypeAttribute(
        final DataObject pDataObject,
        SdoProperty pProperty,
        SdoProperty pValueProp) throws XMLStreamException {

        URINamePair unp = DataObjectBehavior.getXsiTypeUnp(pDataObject, pProperty, pValueProp);
        if (unp != null) {
            writeAttribute(XSI_URI, "type", null, getQname(unp.getURI(), unp.getName()));
        }
    }

    /**
     * @param pRootElementName
     * @param pRootObject
     * @param pProp
     * @param pNsDefinitions
     * @throws XMLStreamException
     */
    private void writeElement(
        String pElementUri,
        String pElementName,
        DataObject pValueObject,
        SdoProperty pProp,
        boolean pMixed,
        Map<String, String> pNsDefinitions)
        throws XMLStreamException {

        for (Entry<String,String> nsMapping : pNsDefinitions.entrySet()) {
            _output.setPrefix(nsMapping.getKey(), nsMapping.getValue());
        }

        writeIndentIncrease(pMixed);
        writeStartElement(pElementUri, pElementName);

        SdoType<?> type = null;
        GenericDataObject gdo = null;
        SdoProperty valueProp = null;
        if (pValueObject != null) {
            try {
                gdo = ((DataObjectDecorator)pValueObject).getInstance();
            } catch (ClassCastException e) {
                if (pValueObject instanceof ListSimpleType) {
                    DataObject typeDO = ((ListSimpleType)pValueObject).getTypeDataObject();
                    gdo = ((DataObjectDecorator)typeDO).getInstance();
                } else {
                    throw e;
                }
            }
            type = (SdoType<?>)gdo.getType();
            valueProp = TypeHelperImpl.getSimpleContentProperty(gdo);
        }

        URINamePair unp =
            DataObjectBehavior.getXsiTypeUnpForElement(
                gdo, type, pProp, valueProp, _xmlDocument.getRootObject() != pValueObject);
        if (unp != null) {
            writeAttribute(XSI_URI, "type", null, getQname(unp.getURI(), unp.getName()));
        }

        List<SdoProperty> elements = Collections.emptyList();
        boolean nilStatus = pProp != null && pProp.isNullable();
        if (gdo != null) {
            nilStatus = nilStatus && gdo.getXsiNil();
            List<Property> props = gdo.getInstanceProperties();
            int propsSize = props.size();
            elements = new ArrayList<SdoProperty>(propsSize);
            for (int i = 0; i < propsSize; i++) {
                SdoProperty sdoProp = (SdoProperty)props.get(i);
                if (gdo.isSet(sdoProp)) {
                    if (sdoProp.isOppositeContainment()) {
                        continue;
                    }
                    if (!sdoProp.isXmlElement() || (valueProp != null && sdoProp != valueProp)) {
                        SdoType<?> t = (SdoType<?>)sdoProp.getType();
                        String stringValue;
                        if (TYPE.equalsUriName(t)) {
                            Type typeType = (Type)gdo.get(sdoProp);
                            if ((typeType == null) || (typeType instanceof Namespace)) {
                                stringValue = null;
                            } else {
                                stringValue = ((SdoType<?>)typeType).getQName().toStandardSdoFormat();
                            }
                        } else if (!t.isDataType()) {
                            stringValue = _referenceHandler.generateReference(gdo.getDataObject(sdoProp), null);
                        } else if (DataObjectBehavior.isQnameProperty(sdoProp)) {
                            Object o = gdo.get(sdoProp);
                            if (o instanceof List) {
                                final StringBuilder buf = new StringBuilder();
                                for (String value : (List<String>)o) {
                                    buf.append(value);
                                    buf.append(' ');
                                }
                                stringValue = buf.toString().trim();
                            } else {
                                stringValue = gdo.getString(sdoProp);
                            }
                        } else {
                            stringValue = gdo.getString(sdoProp);
                        }
                        if (stringValue != null) {
                            String xmlName = sdoProp.getXmlName();
                            writeAttribute(
                                sdoProp.getUri(),
                                xmlName,
                                sdoProp,
                                stringValue);
                            if ("targetNamespace".equals(xmlName)
                                    && stringValue.length() > 0
                                    && _output.getPrefix(stringValue) == null) {
                                String prefix = createPrefix(stringValue, PREFIX_TNS);
                                _output.setPrefix(prefix, stringValue);
                                _output.writeNamespace(prefix, stringValue);

                            }
                        }
                    } else {
                        elements.add(sdoProp);
                    }
                } else if (sdoProp.isOrphanHolder()) {
                    elements.add(sdoProp);
                }
            }
        }

        if (type != null) {
            if (type.getElementFormDefaultQualified()) {
                String uri = type.getXmlUri();
                if ((uri.length() > 0) && (_output.getPrefix(uri) == null)) {
                    String prefix = createPrefix(uri);
                    _output.setPrefix(prefix, uri);
                    _output.writeNamespace(prefix, uri);
                }
            }
            if (_schemaType == type) {
                Schema schema = (Schema)pValueObject;
                List<Import> imports = schema.getImport();
                for (int i=0; i<imports.size(); ++i) {
                    String nsUri = imports.get(i).getNamespace();
                    if (_output.getPrefix(nsUri) == null && !XML_URI.equals(nsUri)) {
                        String prefix = createPrefix(nsUri);
                        _output.setPrefix(prefix, nsUri);
                        _output.writeNamespace(prefix, nsUri);
                    }
                }
            }
        }

        for (Entry<String,String> nsMapping : pNsDefinitions.entrySet()) {
            _output.writeNamespace(nsMapping.getKey(), nsMapping.getValue());
        }

        if (_xmlDocument.getRootObject() == pValueObject) {
            if (DATAGRAPH_TYPE.equalsUriName(type)) {
                final DataObject root = ((DataGraph)pValueObject).getRootObject();
                if (root!=null) {
                    String uri = getURI(root.getType());
                    if (uri != null && uri.length() > 0) {
                        _output.setPrefix("data", uri);
                        _output.writeNamespace("data", uri);
                    }
                }
            }
            if (_xmlDocument.getSchemaLocation() != null) {
                writeAttribute(XSI_URI, "schemaLocation", null,
                    _xmlDocument.getSchemaLocation());
            }
            if (_xmlDocument.getNoNamespaceSchemaLocation() != null) {
                writeAttribute(XSI_URI,
                    "noNamespaceSchemaLocation", null, _xmlDocument
                        .getNoNamespaceSchemaLocation());
            }
        }

        boolean noIndent = (gdo == null);
        if (nilStatus) {
            noIndent = true;
            writeAttribute(XSI_URI, "nil", null, "true");
        } else if (valueProp != null) {
            noIndent = true;
            String value = gdo.getString(valueProp);
            if (value != null) {
                value = DataObjectBehavior.encodeBase64Binary(valueProp, value);
                if (DataObjectBehavior.isQnameProperty(valueProp)) {
                    value = getQnameFromUriProperty(value);
                }
                _output.writeCharacters(value);
            }
        } else if (type!=null && type.isSequenced()) {
            Sequence sequence = gdo.getSequence();
            boolean mixedContent = pMixed || type.isMixedContent();
            if (sequence.size()>0 && !mixedContent) {
                writeNewLine(pMixed);
            } else {
                noIndent  = true;
            }
            for(int i=0; i<sequence.size(); ++i) {
                SdoProperty element = (SdoProperty)sequence.getProperty(i);
                Object value = sequence.getValue(i);
                if (element == null) {
                    // mixed test
                    _output.writeCharacters((String)value);
                } else {
                    handleProperty(element, value, gdo, mixedContent);
                }
            }
        } else {
            if (elements.isEmpty()) {
                noIndent = true;
            } else {
                writeNewLine(pMixed);
            }
            SdoProperty element = null;
            int elementsSize = elements.size();
            for (int n=0; n < elementsSize; ++n) {
                element = elements.get(n);
                if (element.isMany()) {
                    if (element.isOrphanHolder()) {
                        List<DataObject> orphanList = _orphanHandler.getOrphanList(gdo, element);
                        if (orphanList != null) {
                            for (DataObject orphan : orphanList) {
                                handleProperty(element, orphan, gdo, pMixed);
                            }
                        }
                    } else {
                        List<Object> values = gdo.getList(element);
                        int size = values.size();
                        for(int i=0; i < size; ++i) {
                            handleProperty(element, values.get(i), gdo, pMixed);
                        }
                    }
                } else {
                    handleProperty(element, gdo.get(element), gdo, pMixed);
                }
            }
        }

        if (noIndent) {
            --_indentation;
        } else {
            writeIndentDecrease(pMixed);
        }
        _output.writeEndElement();
        writeNewLine(pMixed);
    }

    /**
     * @param element
     * @param pDataObject
     * @throws XMLStreamException
     */
    private void handleProperty(
        SdoProperty element,
        Object pValue,
        GenericDataObject pParent,
        boolean pMixed) throws XMLStreamException {

        SdoType<?> propType = (SdoType<?>)element.getType();
        if (ChangeSummaryType.getInstance() == propType) {
            writeChangeSummary(element.getUri(), element.getXmlName(), (ChangeSummary)pValue);
        } else if (XsdType.getInstance() == propType) {
            writeXsd(element, (DataObject)pValue, pParent);
        } else {
            SdoProperty prop = element;
            if (prop != null) {
                URINamePair refPropUnp = prop.getRef();
                if (refPropUnp != null
                        && pValue instanceof DataObject
                        && (prop.getType() != ((DataObject)pValue).getType())) {
                    final Property headProp = _helperContext.getXSDHelper().getGlobalProperty(refPropUnp.getURI(), refPropUnp.getName(), true);
                    if (headProp != null) {
                        prop = findBestMatch((DataObject)pValue, headProp);
                    }
                }
            }
            writeSingleValuedElement(prop.getUri(), prop, pValue, pMixed);
        }
    }

    private void handlePropertyInCs(SdoProperty pProperty, Object pValue, ChangeSummary pChangeSummary, boolean pMixed)
        throws XMLStreamException {

        SdoProperty element = pProperty;
        String elementUri = null;
        URINamePair refPropUnp = element.getRef();
        if (refPropUnp != null) {
            if ((pValue != null) && pValue instanceof DataObject && (element.getType() != ((DataObject)pValue).getType())) {
                final Property headProp = _helperContext.getXSDHelper().getGlobalProperty(refPropUnp.getURI(), refPropUnp.getName(), true);
                if (headProp != null) {
                    final SdoProperty best = findBestMatch((DataObject)pValue, headProp);
                    element = best;
                    if (best != headProp) {
                        elementUri = _helperContext.getXSDHelper().getNamespaceURI(best);
                    }
                }
            }
            elementUri = refPropUnp.getURI();
        }

        Type type = element.getType();
        if (elementUri == null) {
            elementUri = element.getUri();
        }
        if (XSD_TYPE.equalsUriName(type)) {
            // XSD was not changed
            // TODO is empty XSD in ChangeSummary right?
            writeSimpleElement(elementUri, element, "", pMixed);
        } else if (CHANGESUMMARY_TYPE.equalsUriName(type)) {
            // when the logging begun the ChangeSummary was empty
            writeSimpleElement(elementUri, element, "", pMixed);
        } else if (type.isDataType()) {
            writeSingleValuedElement(elementUri, element, pValue, pMixed);
        } else {
            if (pChangeSummary.isDeleted((DataObject)pValue) && element.isContainment()) {
                writeCSElement(elementUri, element.getXmlName(), (DataObject)pValue, element, pChangeSummary, pMixed);
            } else {
                writeCSReference(elementUri, element.getXmlName(), (DataObject)pValue, pChangeSummary, pMixed);
            }
        }
    }

    private void writeCSElement(String pElementUri, String pElementName, DataObject pDataObject, SdoProperty pP, ChangeSummary pChangeSummary, boolean pMixed) throws XMLStreamException {
        writeIndentIncrease(pMixed);
        writeStartElement(pElementUri, pElementName);

        SdoType type = (SdoType)pDataObject.getType();
        SdoProperty valueProp = TypeHelperImpl.getSimpleContentProperty(pDataObject);

        if (pChangeSummary.isDeleted(pDataObject)) {
            if (!MIXEDTEXT_TYPE.equalsUriName(type)) {
                if (pP == null
                        || (type != null && pP.getType() != type)
                        || (pP.isOpenContent() && _xmlDocument.getRootObject() != pDataObject
                                && pP.getContainingType()==null)) {
                    writeXsiTypeAttribute(pDataObject, pP, valueProp);
                }
            } else if (pP != null && pP.get(PropertyType.getXsdTypeProperty()) != null) {
                writeXsiTypeAttribute(pDataObject, pP, valueProp);
            }
        } else {
            if (valueProp == null || type != OpenType.getInstance()) {
                if (!type.isLocal()) {
                    writeAttribute(XSI_URI, "type", null, getQname(type));
                }
            } else {
                URINamePair xsdUnp = valueProp.getXsdType();
                if (xsdUnp != null) {
                    writeAttribute(
                        XSI_URI, "type", null,
                        getQname(xsdUnp.getURI(), xsdUnp.getName()));
                }
            }
            writeAttribute(DATATYPE_URI, "ref", null, _referenceHandler.generateReference(pDataObject, pChangeSummary));
        }

        StringBuilder unsetProperties = new StringBuilder();
        List<Setting> elementSettings = new ArrayList<Setting>();
        for (Setting setting : (List<Setting>)pChangeSummary.getOldValues(pDataObject)) {
            SdoProperty sdoProp = (SdoProperty)setting.getProperty();
            if (!setting.isSet()) {
                if (!sdoProp.isOpenContent() || sdoProp.getContainingType()==null) {
                    unsetProperties.append(sdoProp.getXmlName());
                } else {
                    String uri = ((SdoType)sdoProp.getContainingType()).getXmlUri();
                    String prefix = getPrefix(uri);
                    if (prefix == null) {
                        prefix = createPrefix(uri);
                        _output.setPrefix(prefix, uri);
                        _output.writeNamespace(prefix, uri);
                    }
                    unsetProperties.append(prefix+":"+sdoProp.getXmlName());
                }
                unsetProperties.append(' ');
            } else if (sdoProp.isXmlElement()) {
                if (valueProp == null) {
                    elementSettings.add(setting);
                }
            } else {
                SdoType<Object> sdoType = (SdoType<Object>)sdoProp.getType();
                if (sdoType.isDataType()) {
                    String value = sdoType.convertToJavaClass(setting.getValue(), String.class);
                    writeAttribute(
                        sdoProp.getUri(),
                        sdoProp.getXmlName(), sdoProp, value);
                } else if (!sdoProp.isOppositeContainment()) {
                    DataObject dataValue = (DataObject)setting.getValue();
                    writeAttribute(
                        sdoProp.getUri(),
                        sdoProp.getXmlName(), sdoProp,
                        _referenceHandler.generateReference(dataValue, pChangeSummary));
                }
            }
        }
        if (unsetProperties.length()>0) {
            writeAttribute(DATATYPE_URI, "unset", null, unsetProperties.toString().trim());
        }

        boolean mixedContent = pMixed || type.isMixedContent();
        Sequence oldSequence = pChangeSummary.getOldSequence(pDataObject);
        boolean notEmpty = !elementSettings.isEmpty() || oldSequence != null;
        if (notEmpty) {
            writeNewLine(mixedContent);
        }

        if (valueProp != null) {
            String value;
            Setting oldSetting = pChangeSummary.getOldValue(pDataObject, valueProp);
            if (oldSetting == null) {
                value = pDataObject.getString(valueProp);
            } else {
                value = ((SdoType<Object>)valueProp.getType())
                    .convertToJavaClass(oldSetting.getValue(), String.class);
            }
            writeValueCharacters(valueProp, value);
        } else {
            if (oldSequence != null) {
                for(int i=0; i<oldSequence.size(); ++i) {
                    SdoProperty element = (SdoProperty)oldSequence.getProperty(i);
                    // TODO Text property shouldn't be passed
                    if (element == null || ("text".equals(element.getName()) && STRING.equalsUriName(element.getType()))) {
                        // mixed test
                        _output.writeCharacters((String)oldSequence.getValue(i));
                    } else {
                        handlePropertyInCs(
                            element,
                            oldSequence.getValue(i),
                            pChangeSummary,
                            mixedContent);
                    }
                }
            } else {
                for (Setting setting : elementSettings) {
                    SdoProperty element = (SdoProperty)setting.getProperty();
                    Object value = setting.getValue();
                    if (element.isMany()) {
                        for(Object listElement : (List)value) {
                            handlePropertyInCs(element, listElement, pChangeSummary, mixedContent);
                        }
                    } else {
                        handlePropertyInCs(element, setting.getValue(), pChangeSummary, mixedContent);
                    }
                }
            }
        }

        if (notEmpty) {
            writeIndentDecrease(mixedContent);
        } else {
            --_indentation;
        }
        _output.writeEndElement();
        writeNewLine(pMixed);
    }

    /**
     * @param pObject
     * @param pName
     * @param pDataObject
     * @throws XMLStreamException
     */
    private void writeCSReference(String pElementUri, String pElementName, DataObject pDataObject, ChangeSummary pChangeSummary, boolean pMixed) throws XMLStreamException {
        writeIndent(pMixed);
        writeStartElement(pElementUri, pElementName);
        // this data object could not be deleted,
        // therefore we don't need a change summary while generating a reference
        writeAttribute(DATATYPE_URI, "ref", null, _referenceHandler.generateReference(pDataObject, pChangeSummary));
        _output.writeEndElement();
        writeNewLine(pMixed);
    }

    /**
     * @param pObject
     * @param pName
     * @param pDataObject
     * @throws XMLStreamException
     */
    private void writeReference(String pElementUri, String pElementName, DataObject pDataObject, SdoProperty pElement,  boolean pMixed) throws XMLStreamException {
        if (pDataObject == null) {
            writeElement(pElementUri, pElementName, null, pElement,  pMixed, _emptyMap);
            return;
        }
        Type keyType = ((SdoType)pDataObject.getType()).getKeyType();
        if (keyType == null) {
            writeIndent(pMixed);
            writeStartElement(pElementUri, pElementName);
            _output.writeCharacters(_referenceHandler.generateReference(pDataObject, null));
            _output.writeEndElement();
            writeNewLine(pMixed);
        } else if (keyType.isDataType()) {
            // key is a string
            String key = (String)DataObjectBehavior.getKey(pDataObject);
            if (key == null) {
                // fall back to reference
                key = _referenceHandler.generateReference(pDataObject, null);
            }
            writeIndent(pMixed);
            writeStartElement(pElementUri, pElementName);
            _output.writeCharacters(key);
            _output.writeEndElement();
            writeNewLine(pMixed);
        } else {
            // key is a DataObject
            DataObject key = (DataObject)DataObjectBehavior.getKey(pDataObject);
            writeElement(pElementUri, pElementName, key, pElement, pMixed, _emptyMap);
        }
    }

    /**
     * @param element
     * @param pUnp
     * @param pDataObject
     * @throws XMLStreamException
     */
    private void writeSimpleElement(
        String pUri,
        SdoProperty element,
        SdoType<?> pValueType,
        String pValue,
        boolean pMixed) throws XMLStreamException {

        writeIndent(pMixed);
        writeStartElement(pUri, element.getXmlName());
        if (pValueType != null) {
            URINamePair xsdName =
                SchemaTypeFactory.getInstance().getXsdName(
                    pValueType.getURI(), pValueType.getName());
            writeAttribute(
                XSI_URI, "type", null,
                getQname(xsdName.getURI(), xsdName.getName()));
        } else if (element.isOpenContent() && element.getContainingType() == null) {
            writeXsiTypeAttribute(null, element, null);
        }
        writeValueCharacters(element, pValue);
        _output.writeEndElement();
        writeNewLine(pMixed);
    }

    /**
     * @param element
     * @param pDataObject
     * @throws XMLStreamException
     */
    private void writeSimpleElement(String pUri, SdoProperty element, String pValue, boolean pMixed)
        throws XMLStreamException {

        writeSimpleElement(pUri, element, null, pValue, pMixed);
    }

    private void writeValueCharacters(SdoProperty pProp, String pValue) throws XMLStreamException {
        String value = DataObjectBehavior.encodeBase64Binary(pProp, pValue);
        if (value != null) {
            if (DataObjectBehavior.isQnameProperty(pProp)) {
                value = getQnameFromUriProperty(value);
            }
            _output.writeCharacters(value);
        } else if (pProp.isNullable()) {
            writeAttribute(XSI_URI, "nil", null, "true");
        }
    }
    private void writeSingleValuedElement(String pUri, SdoProperty pElement, Object pValue, boolean pMixed)
        throws XMLStreamException {

        SdoType<Object> propType = (SdoType<Object>)pElement.getType();
        if (propType.isDataType()) {
            if (JavaSimpleType.OBJECT == propType) {
                writeSimpleElement(
                    pUri, pElement,
                    ((TypeHelperImpl)_helperContext.getTypeHelper()).getResolvedType(pValue.getClass()),
                    propType.convertToJavaClass(pValue, String.class),
                    pMixed);
            } else {
                writeSimpleElement(pUri, pElement, propType.convertToJavaClass(pValue, String.class), pMixed);
            }
        } else if (pElement.isContainment()) {
            writeElement(pUri, pElement.getXmlName(), (DataObject)pValue, pElement, pMixed, _emptyMap);
        } else if (TYPE.equalsUriName(propType)) {
            if (!(pValue instanceof Namespace)) {
                final URINamePair value = propType.convertToJavaClass(pValue, URINamePair.class);
                writeSimpleElement(pUri, pElement, value.toStandardSdoFormat(), pMixed);
            }
        } else  {
            writeReference(pUri, pElement.getXmlName(), (DataObject)pValue, pElement, pMixed);
        }
    }

    /**
     * @param namespaceURI
     * @param localName
     * @throws XMLStreamException
     */
    private void writeStartElement(final String pNamespaceURI, final String pLocalName) throws XMLStreamException {
        if (pNamespaceURI != null && pNamespaceURI.length() > 0) {
            String prefix = getPrefix(pNamespaceURI);
            if (prefix == null) {
                prefix = createPrefix(pNamespaceURI);
                _output.writeStartElement(prefix, pLocalName, pNamespaceURI);
                _output.writeNamespace(prefix, pNamespaceURI);
            } else {
                _output.writeStartElement(pNamespaceURI, pLocalName);
            }
        } else {
            _output.writeStartElement(pLocalName);
        }
    }

    private void writeXsd(
        SdoProperty pElement,
        DataObject pValue,
        GenericDataObject pParent) throws XMLStreamException {
        // TODO:
        // Correct behavior for DataGraphs is not defined.
        // Simply exposing all types in the namespace is actually
        // probably best, but the test cases have already be written
        // for limiting this to only those datatypes that are
        // actually used.
        if (pValue != null && pValue.getSequence().size() == 0
                && DataGraphType.getInstance().isInstance(pParent)) {

            DataObject root = ((DataGraph)pParent.getFacade()).getRootObject();
            if (root != null) {
                final Set<Type> tset = new HashSet<Type>();
                DataObjectBehavior.addUsedTypes(
                    root, tset, ((SdoType<?>)root.getType()).getXmlUri());
                List<Type> types = new ArrayList<Type>(tset);
                if (!types.isEmpty()) {
                    SchemaTranslator translator = new SchemaTranslator(_helperContext);
                    Property schemaProp =
                        _helperContext.getTypeHelper().getOpenContentProperty(
                            PROP_SCHEMA_SCHEMA.getURI(), PROP_SCHEMA_SCHEMA.getName());
                    pValue.set(schemaProp, translator.getSchema(types, new HashMap<String,String>()));
                }
            }
        }

        Map<String, String> nsDefinitions = new HashMap<String,String>();
        nsDefinitions.put("sdox", DATATYPE_XML_URI);
        nsDefinitions.put("sdoj", DATATYPE_JAVA_URI);

        writeElement(pElement.getUri(), pElement.getXmlName(), pValue, pElement, false, nsDefinitions);
    }

    private void writeChangeSummary(final String pUri, final String pName, final ChangeSummary pChangeSummary) throws XMLStreamException {
        final StringBuilder created = new StringBuilder();
        final StringBuilder deleted = new StringBuilder();

        if (pChangeSummary == null) {
            return;
        }
        writeIndentIncrease(false);
        writeStartElement(pUri, pName);

        final List<DataObjectDecorator> changedData = pChangeSummary.getChangedDataObjects();
        for (DataObjectDecorator decorator : changedData) {
            if (pChangeSummary.isCreated(decorator)) {
                created.append(_referenceHandler.generateReference(decorator, pChangeSummary, false));
                created.append(' ');
            } else if (pChangeSummary.isDeleted(decorator)) {
                DataObject container = decorator.getContainer();
                if (container == null || !pChangeSummary.isDeleted(container)) {
                    deleted.append(_referenceHandler.generateReference(decorator, pChangeSummary, false));
                    deleted.append(' ');
                }
            }
        }

        if (created.length() > 0) {
            writeAttribute(null, "create", null, created.toString().trim());
        }
        if (deleted.length() > 0) {
            writeAttribute(null, "delete", null, deleted.toString().trim());
        }
        if (!pChangeSummary.isLogging()) {
            writeAttribute(null, "logging", null, "false");
        }
        if (_output.getPrefix(DATATYPE_URI) == null) {
            _output.setPrefix(PREFIX_SDO, DATATYPE_URI);
            _output.writeNamespace(PREFIX_SDO, DATATYPE_URI);
        }
        writeNewLine(false);

        writeChangedElements(pChangeSummary, changedData);

        writeIndentDecrease(false);
        _output.writeEndElement();
        writeNewLine(false);
    }

    private void writeChangedElements(final ChangeSummary pChangeSummary, final List<DataObjectDecorator> changedData) throws XMLStreamException {
        for (DataObjectDecorator decorator : changedData) {
            if (pChangeSummary.isModified(decorator)) {
                String elementName = _xmlDocument.getRootElementName();
                String elementUri = _xmlDocument.getRootElementURI();
                SdoProperty containmentProperty = null;
                if (DataObjectBehavior.isOrphan(decorator, pChangeSummary)) {
                    OrphanHolder orphanHolder = _orphanHandler.getOrphanHolder(decorator);
                    if (orphanHolder != null) {
                        elementName = orphanHolder.getProperty().getXmlName();
                        elementUri = orphanHolder.getProperty().getUri();
                    }
                } else {
                    containmentProperty = (SdoProperty)decorator.getContainmentProperty();
                    if (containmentProperty != null) {
                        elementName = containmentProperty.getXmlName();
                        elementUri = containmentProperty.getUri();
                    }
                }
                writeCSElement(elementUri, elementName, decorator, containmentProperty, pChangeSummary, false);
            } else if (pChangeSummary.isDeleted(decorator)
                    && DataObjectBehavior.isOrphan(decorator, pChangeSummary)) {
                OrphanHolder orphanHolder = _orphanHandler.getOrphanHolder(decorator);
                if (orphanHolder != null) {
                    SdoProperty property = orphanHolder.getProperty();
                    writeElement(property.getUri(), property.getXmlName(), decorator, property, false, _emptyMap);
                }
            }
        }
    }

    class ReferenceHandlerImpl extends ReferenceHandler {

        /**
         * @param pXmlDocument
         * @param pOrphanHandler
         */
        public ReferenceHandlerImpl(XMLDocument pXmlDocument, OrphanHandler pOrphanHandler) {
            super(pXmlDocument, pOrphanHandler);
        }

        /* (non-Javadoc)
         * @see com.sap.sdo.impl.xml.ReferenceHandler#checkPrefix(java.lang.String)
         */
        @Override
        public String checkPrefix(String pUri) throws XMLStreamException {
            if (pUri != null && pUri.length() > 0) {
                String prefix = getPrefix(pUri);
                if (prefix == null) {
                    prefix = createPrefix(pUri);
                    _output.setPrefix(prefix, pUri);
                    _output.writeNamespace(prefix, pUri);
                }
                return prefix + ':';
            }
            return "";
        }

    }
}
