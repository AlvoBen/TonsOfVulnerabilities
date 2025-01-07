/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.xml.stream.adapters.impl;

import static com.sap.sdo.api.util.URINamePair.DATAGRAPH_TYPE;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_JAVA_URI;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_URI;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_XML_URI;
import static com.sap.sdo.api.util.URINamePair.MIXEDTEXT_TYPE;
import static com.sap.sdo.api.util.URINamePair.SCHEMA_SCHEMA;
import static com.sap.sdo.api.util.URINamePair.XSI_URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import com.sap.sdo.api.types.schema.Import;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.schema.SchemaTypeFactory;
import com.sap.sdo.impl.util.DataObjectBehavior;
import com.sap.sdo.impl.xml.OrphanHandler;
import com.sap.sdo.impl.xml.ReferenceHandler;
import com.sap.sdo.impl.xml.stream.SdoNamespaceContext;
import com.sap.sdo.impl.xml.stream.adapters.AttributeAdapter;
import com.sap.sdo.impl.xml.stream.adapters.ElementAdapter;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public abstract class AbstractElementAdapter extends AbstractPropertyAdapter implements ElementAdapter {

    private static final String PREFIX_NS = "ns";
    private static final String PREFIX_TNS = "tns";
    protected static final String PREFIX_SDO = "sdo";
    private static final String PREFIX_SDO_JAVA = "sdoj";
    private static final String PREFIX_SDO_XML = "sdox";

    private final List<SdoProperty> _attributes = new ArrayList<SdoProperty>();

    private final List<AttributeAdapter> _additionalAttributes = new ArrayList<AttributeAdapter>();

    private AbstractElementAdapter _parent;
    private RootElementProperties _rootElementProperties;
    private ReferenceHandler _referenceHandler;
    protected HelperContext _helperContext;

    private boolean _isKey;

    private enum Status {UNKNOWN, STARTED, ENDED};
    private Status _status = Status.UNKNOWN;
    private SdoNamespaceContext _nsCtx;

    protected AbstractElementAdapter(AdapterPool pAdapterPool) {
        super(pAdapterPool);
    }

    protected void fillIn(
        String uri,
        String name,
        GenericDataObject dataObject,
        String value,
        AbstractElementAdapter parent,
        HelperContext context) {

        fillIn(uri, name, dataObject, value, false, parent, context);
    }

    protected void fillIn(
        String uri,
        String name,
        GenericDataObject dataObject,
        boolean isKey,
        AbstractElementAdapter parent,
        HelperContext context) {

        fillIn(uri, name, dataObject, null, isKey, parent, context);
    }

    private void fillIn(
        String uri,
        String name,
        GenericDataObject dataObject,
        String value,
        boolean isKey,
        AbstractElementAdapter parent,
        HelperContext context) {

        super.fillIn(uri, name, dataObject, value);
        boolean isSchema = SCHEMA_SCHEMA.equalsUriName(uri, name);
        _parent = parent;
        if (_parent != null) {
            _rootElementProperties = _parent._rootElementProperties;
        } else {
            _rootElementProperties = new RootElementProperties(getDataObject());
            // root context
            createNamespaceContext(true, isSchema);
        }
        _helperContext = context;

        if (uri != null && uri.length() > 0) {
            String prefix = getPrefix(uri);
            if (prefix == null) {
                prefix = createPrefix(uri);
            }
            setNamespacePrefix(prefix);
        }
        if (isSchema) {
            List<Import> imports = dataObject.getList("import");
            for (int i=0; i<imports.size(); ++i) {
                String nsUri = imports.get(i).getNamespace();
                String prefix = getPrefix(nsUri);
                if (prefix == null) {
                    prefix = createPrefix(nsUri);
                }
            }
        }

        _isKey = isKey || (parent != null && parent._isKey);
    }

    @Override
    public void clear() {
        super.clear();
        _attributes.clear();
        for(int i=0; i<_additionalAttributes.size(); ++i) {
            _additionalAttributes.get(i).clear();
        }
        _additionalAttributes.clear();

        _parent = null;
        _rootElementProperties = null;
        _helperContext = null;
        _isKey = false;
        _nsCtx = null;
        _referenceHandler = null;
        _status = Status.UNKNOWN;
    }

    private static SdoProperty checkRefProperty(Property prop, DataObject value, HelperContext context) {
        URINamePair refPropUnp = ((SdoProperty)prop).getRef();
        if (refPropUnp != null && value != null && (prop.getType() != value.getType())) {
            final Property headProp =
                context.getXSDHelper().getGlobalProperty(
                    refPropUnp.getURI(), refPropUnp.getName(), true);
            if (headProp != null) {
                return (SdoProperty)findBestMatch(value, headProp, context);
            }
        }
        return (SdoProperty)prop;
    }

    protected static Property findBestMatch(DataObject pDataObject, Property headProp, HelperContext context) {
        Property best = headProp;
        DataObject headPropObj = (DataObject)headProp;
        if (headPropObj.isSet(PropertyType.getSubstitutesProperty())) {
            for (Object refObj: headPropObj.getList(PropertyType.getSubstitutesProperty())) {
                final URINamePair unp = URINamePair.fromStandardSdoFormat((String)refObj);
                final Property s =
                    context.getXSDHelper().getGlobalProperty(
                        unp.getURI(), unp.getName(), true);
                if (!s.getType().isInstance(pDataObject)) {
                    continue;
                }
                if (s.getType() == pDataObject.getType()) {
                    return s;
                } else if (((SdoType<?>)best.getType()).isAssignableType(s.getType())) {
                    best = s;
                } else if (!(((SdoType<?>)s.getType()).isAssignableType(best.getType()))) {
                    throw new IllegalArgumentException("Types are from different hierarchies in substitution group "+headProp.getName());
                }
            }
        }
        return best;
    }

    private void checkXsiNil(SdoProperty pProp, GenericDataObject pData, String pValue) {
        if (pProp != null && pProp.isNullable()) {
            if (pValue == null && (pData == null || pData.getXsiNil())) {
                addAttribute(XSI_URI, "nil", "true");
            }
        }
    }

    private void checkXsiType(SdoProperty pProp, GenericDataObject pData, SdoProperty pValueProp, SdoType<?> pType) {
        if (pData != null) {
            SdoType<?> type = (SdoType<?>)pData.getType();
            if ((type != null) && type.getElementFormDefaultQualified()) {
                String uri = type.getXmlUri();
                if (uri.length() > 0) {
                    try {
                        getReferenceHandler().checkPrefix(uri);
                    } catch (XMLStreamException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                }
            }

        }
        URINamePair unp =
            DataObjectBehavior.getXsiTypeUnpForElement(
                pData, pType, pProp, pValueProp, _nsCtx == null || !_nsCtx.isRootCtx());
        if (unp != null) {
            addAttribute(XSI_URI, "type", getQname(unp.getURI(), unp.getName()));
        }
    }

    protected void addXsiType(DataObject pDataObject, SdoProperty pProperty, SdoProperty pValueProp) {
        URINamePair unp = DataObjectBehavior.getXsiTypeUnp(pDataObject, pProperty, pValueProp);
        if (unp != null) {
            addAttribute(XSI_URI, "type", getQname(unp.getURI(), unp.getName()));
        }
    }

    protected void addXsiType(Class<? extends Object> pClass) {
        SdoType<?> type = ((TypeHelperImpl)_helperContext.getTypeHelper()).getResolvedType(pClass);
        URINamePair xsdName = SchemaTypeFactory.getInstance().getXsdName(type.getURI(), type.getName());
        addAttribute(XSI_URI, "type", getQname(xsdName.getURI(), xsdName.getName()));
    }

    protected void addAttribute(String uri, String name, String value) {
        String prefix = null;
        if (uri != null && uri.length() > 0) {
            prefix = getPrefix(uri);
            if (prefix == null) {
                prefix = createPrefix(uri);
            }
        }
        AttributeValueAdapter attribute = _adapterPool.getAttributeValueAdapter();
        attribute.fillIn(prefix, uri, name, value);
        _additionalAttributes.add(attribute);
    }

    protected void addAttribute(SdoProperty prop) {
        _attributes.add(prop);
        if (DataObjectBehavior.isQnameProperty(prop)) {
            String value = getDataObject().getString(prop);
            if (value != null && value.indexOf(' ') > 0) {
                StringTokenizer tokenizer = new StringTokenizer(value, " ");
                while (tokenizer.hasMoreTokens()) {
                    String uri = URINamePair.fromStandardSdoFormat(tokenizer.nextToken()).getURI();
                    if (uri.length() > 0 && getPrefix(uri) == null) {
                        createPrefix(uri);
                    }
                }
            } else {
                String uri = URINamePair.fromStandardSdoFormat(value).getURI();
                if (uri.length() > 0 && getPrefix(uri) == null) {
                    createPrefix(uri);
                }
            }
        }
        if ("targetNamespace".equals(prop.getXmlName())) {
            String value = getDataObject().getString(prop);
            if (value != null && value.length() > 0 && getPrefix(value) == null) {
                String prefix = createPrefix(value, PREFIX_TNS);
            }
        }
        String ns = prop.getUri();
        if (ns != null && ns.length() > 0 && getPrefix(ns) == null) {
            createPrefix(ns);
        }
    }

    protected String getQname(final Type pType) {
        SdoType<?> type = (SdoType<?>)pType;
        return getQname(type.getXmlUri(), type.getXmlName());
    }

    protected String getQname(final String uri, String name) {
        String prefix = getPrefix(uri);
        if (prefix == null && uri.length() > 0) {
            prefix = createPrefix(uri);
        }
        if (prefix != null && prefix.length()>0) {
            return prefix + ':' + name;
        }
        return name;
    }

    public SdoNamespaceContext getParentNamespaceContext() {
        if (_parent != null) {
            SdoNamespaceContext nsCtx = _parent._nsCtx;
            if (nsCtx != null) {
                return nsCtx;
            }
            return _parent.getParentNamespaceContext();
        }
        return null;
    }

    private void createNamespaceContext(boolean pIsRootContext, boolean pIsSchema) {
        if (pIsRootContext) {
            _nsCtx = new SdoNamespaceContext();
            if (pIsSchema) {
                _nsCtx.initSchemaCtx();
            } else {
                _nsCtx.initRootCtx();
            }
        } else {
            _nsCtx = new SdoNamespaceContext(getParentNamespaceContext());
        }
    }

    /**
     * @param uri
     * @return
     */
    protected String getPrefix(String uri) {
        if (_nsCtx != null) {
            return _nsCtx.getPrefix(uri);
        }
        // at least the root adapter has to have a namespace context
        return _parent.getPrefix(uri);
    }

    protected String createPrefix(final String namespace) {
        if (_nsCtx == null) {
            createNamespaceContext(false, false);
        }
        if (DATATYPE_URI.equals(namespace)) {
            _nsCtx.addPrefix(PREFIX_SDO, namespace);
            return PREFIX_SDO;
        }
        if (DATATYPE_JAVA_URI.equals(namespace)) {
            _nsCtx.addPrefix(PREFIX_SDO_JAVA, namespace);
            return PREFIX_SDO_JAVA;
        }
        if (DATATYPE_XML_URI.equals(namespace)) {
            _nsCtx.addPrefix(PREFIX_SDO_XML, namespace);
            return PREFIX_SDO_XML;
        }
        return createPrefix(namespace, PREFIX_NS);
    }

    private String createPrefix(final String namespace, final String prefixGroup) {
        if (_nsCtx == null) {
            createNamespaceContext(false, false);
        }
        Map<String, Integer> lastIndexMap = getLastIndexMap();
        Integer lastIndex = lastIndexMap.get(prefixGroup);
        if (lastIndex == null) {
            lastIndex = 0;
        }
        ++lastIndex;
        String prefix;
        if (PREFIX_TNS.equals(prefixGroup)) {
            prefix = prefixGroup;
        } else{
            prefix = prefixGroup + lastIndex;
        }
        lastIndexMap.put(prefixGroup, lastIndex);
        _nsCtx.addPrefix(prefix, namespace);
        return prefix;
    }

    public int getAttributeCount() {
        return _attributes.size() + _additionalAttributes.size();
    }

    public String getAttributeNamespace(int index) {
        int offset = _additionalAttributes.size();
        if (index < offset) {
            return _additionalAttributes.get(index).getNamespaceURI();
        }
        SdoProperty prop = _attributes.get(index - offset);
        return prop.getUri();
    }

    public String getAttributeLocalName(int index) {
        int offset = _additionalAttributes.size();
        if (index < offset) {
            return _additionalAttributes.get(index).getLocalName();
        }
        SdoProperty prop = _attributes.get(index - offset);
        return prop.getXmlName();
    }

    public String getAttributePrefix(int index) {
        int offset = _additionalAttributes.size();
        if (index < offset) {
            return _additionalAttributes.get(index).getNamespacePrefix();
        }
        SdoProperty prop = _attributes.get(index - offset);
        String ns = prop.getUri();
        if (ns != null && ns.length() > 0) {
            return getPrefix(ns);
        }
        return null;
    }

    public String getAttributeValue(int index) {
        int offset = _additionalAttributes.size();
        if (index < offset) {
            return _additionalAttributes.get(index).getValue();
        }
        return calculateValue(_attributes.get(index - offset));
    }

    public String getAttributeType(int index) {
        int offset = _additionalAttributes.size();
        if (index < offset) {
            return "CDATA";
        }
        final URINamePair unp = _attributes.get(index - offset).getXsdType();
        if (unp != null) {
            final String name = unp.getName();
            if ("ID".equals(name)
                    || "IDREF".equals(name) || "IDREFs".equals(name)
                    || "NMTOKEN".equals(name) || "NMTOKENS".equals(name)
                    || "ENTITY".equals(name) || "ENTITIES".equals(name)
                    || "NOTATION".equals(name)) {
                return name;
            }
        }
        return "CDATA";
    }

    public QName getAttributeName(int index) {
        String local;
        String prefix;
        String ns;
        int offset = _additionalAttributes.size();
        if (index < offset) {
            AttributeAdapter att = _additionalAttributes.get(index);
            local = att.getLocalName();
            prefix = att.getNamespacePrefix();
            ns = att.getNamespaceURI();
        } else {
            SdoProperty prop = _attributes.get(index - offset);
            local = prop.getXmlName();
            ns = prop.getUri();
            if (ns != null && ns.length() > 0) {
                prefix = getPrefix(ns);
            } else {
                prefix = null;
            }
        }

        if (ns != null) {
            if (prefix != null) {
                return new QName(ns, local, prefix);
            } else {
                return new QName(ns, local);
            }
        }
        return new QName(local);
    }

    public String getAttributeValue(String ns, String local) {
        if (getDataObject() != null) {
            SdoProperty prop = (SdoProperty)getDataObject().getInstanceProperty(ns, local, false);
            if (prop != null) {
                return calculateValue(prop);
            }
        }
        int size = _additionalAttributes.size();
        for (int i=0; i<size; ++i) {
            AttributeAdapter adapter = _additionalAttributes.get(i);
            String uri = adapter.getNamespaceURI();
            String name = adapter.getLocalName();
            if ((uri == ns || (uri != null && uri.length() == 0 && ns == null) || (uri != null && uri.equals(ns)))
                    && ((name == local || (name != null && name.equals(local))))) {
                return adapter.getValue();
            }
        }
        return null;
    }

    private String calculateValue(SdoProperty prop) {
        String value;
        if (prop.getType().isDataType()) {
            value = normalizeValue(prop, getDataObject().getString(prop));
        } else if (!prop.isContainment()) {
            try {
                value = getReferenceHandler().generateReference(getDataObject().getDataObject(prop), null);
            } catch (XMLStreamException ex) {
                throw new IllegalArgumentException(ex);
            }
        } else {
            value ="";
        }
        return value;
    }

    protected String normalizeValue(SdoProperty pProp, String pValue) {
        String value = pValue;
        if (value != null) {
            value = DataObjectBehavior.encodeBase64Binary(pProp, value);
            if (DataObjectBehavior.isQnameProperty(pProp)) {
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
        return value;
    }

    public String getNamespaceURI(int index) {
        if (_nsCtx != null) {
            return _nsCtx.getNamespaceURI(index);
        }
        return _parent.getNamespaceURI(index);
    }

    public String getNamespaceURI(String prefix) {
        if (_nsCtx != null) {
            return _nsCtx.getNamespaceURI(prefix);
        }
        return _parent.getNamespaceURI(prefix);
    }

    public String getNamespacePrefix(int index) {
        if (_nsCtx != null) {
            return _nsCtx.getNamespacePrefix(index);
        }
        return _parent.getNamespacePrefix(index);
    }

    public int getNamespaceDeclarationCount() {
        if (_nsCtx == null) {
            return 0;
        }
        return _nsCtx.getNamespaceDeclarationCount();
    }

    public SdoNamespaceContext getNamespaceContext() {
        if (_nsCtx == null) {
            createNamespaceContext(false, false);
        }
        return _nsCtx;
    }

    protected AbstractElementAdapter getRootElement() {
        if (_parent != null) {
            return _parent.getRootElement();
        }
        return this;
    }

    /**
     * @param value
     * @param ret
     * @return
     * @throws XMLStreamException
     */
    protected String getQnameFromUriProperty(final String value) {
        URINamePair uriName = URINamePair.fromStandardSdoFormat(value);
        if (uriName.getURI().length() > 0) {
            String prefix = getPrefix(uriName.getURI());
            if (prefix == null) {
                prefix = createPrefix(uriName.getURI());
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

    protected void init(SdoProperty pProp, GenericDataObject pData, SdoProperty pValueProp, SdoType<?> pType, String pValue) {
        checkXsiType(pProp, pData, pValueProp, pType);
        checkXsiNil(pProp, pData, pValue);
    }

    public static ElementAdapter getElementAdapter(XMLDocument doc, HelperContext ctx, AdapterPool pool) {
        final AbstractElementAdapter element;
        Property globalProperty =
            ctx.getXSDHelper().getGlobalProperty(
                doc.getRootElementURI(), doc.getRootElementName(), true);

        GenericDataObject gdo = ((DataObjectDecorator)doc.getRootObject()).getInstance();
        Type type = gdo.getType();
        SdoProperty valueProp = TypeHelperImpl.getSimpleContentProperty(gdo);
        if (globalProperty != null) {
            SdoProperty prop = checkRefProperty(globalProperty, doc.getRootObject(), ctx);
            if (valueProp != null) {
                element = pool.getSimpleContentElementAdapter();
                ((SimpleContentElementAdapter)element).fillIn(prop, gdo, valueProp, null, ctx);
            } else if (type.isSequenced()) {
                element = pool.getSequencedElementAdapter();
                ((SequencedElementAdapter)element).fillIn(prop, gdo, null, ctx);
            }else {
                element = pool.getNonSequencedElementAdapter();
                ((NonSequencedElementAdapter)element).fillIn(prop, gdo, null, ctx);
            }
        } else {
            if (valueProp != null) {
                element = pool.getSimpleContentElementAdapter();
                ((SimpleContentElementAdapter)element).fillIn(
                    doc.getRootElementURI(),
                    doc.getRootElementName(),
                    gdo,
                    valueProp,
                    gdo.getString(valueProp),
                    null, ctx);
            } else if (type.isSequenced()) {
                element = pool.getSequencedElementAdapter();
                ((SequencedElementAdapter)element).fillIn(
                    doc.getRootElementURI(), doc.getRootElementName(), gdo, null, ctx);
            }else {
                element = pool.getNonSequencedElementAdapter();
                ((NonSequencedElementAdapter)element).fillIn(
                    doc.getRootElementURI(), doc.getRootElementName(), gdo, null, ctx);
            }
        }
        if (DATAGRAPH_TYPE.equalsUriName(type)) {
            final DataObject root = ((DataGraph)doc.getRootObject()).getRootObject();
            if (root!=null) {
                String uri = getURI(root.getType());
                if (uri != null && uri.length() > 0) {
                    element.getNamespaceContext().addPrefix("data", uri);
                }
            }
        }
        String schemaLocation = doc.getSchemaLocation();
        if (schemaLocation != null) {
            element.addAttribute(XSI_URI, "schemaLocation", schemaLocation);
        }
        String noNamespaceSchemaLocation = doc.getNoNamespaceSchemaLocation();
        if (noNamespaceSchemaLocation != null) {
            element.addAttribute(
                XSI_URI, "noNamespaceSchemaLocation", noNamespaceSchemaLocation);
        }

        return element;
    }

    private static String getURI(final Type pType) {
        if (MIXEDTEXT_TYPE.equalsUriName(pType)) {
            return null;
        }
        return pType.getURI();
    }

    protected ElementAdapter internGetElementAdapter(
        Property globalProperty,
        DataObject graph,
        HelperContext ctx) {

        SdoProperty prop = checkRefProperty(globalProperty, graph, ctx);
        if (graph != null) {
            GenericDataObject gdo = ((DataObjectDecorator)graph).getInstance();
            SdoProperty valueProp = TypeHelperImpl.getSimpleContentProperty(gdo);
            if (valueProp != null) {
                SimpleContentElementAdapter adapter = _adapterPool.getSimpleContentElementAdapter();
                adapter.fillIn(prop, gdo, valueProp, this, ctx);
                return adapter;
            } else if (!prop.isContainment()) {
                Type keyType = ((SdoType)gdo.getType()).getKeyType();
                if (keyType == null) {
                    SimpleContentElementAdapter adapter = _adapterPool.getSimpleContentElementAdapter();
                    adapter.fillReference(prop, gdo, this, ctx);
                    return adapter;
                } else if (keyType.isDataType()) {
                    // key is a string
                    String key = (String)DataObjectBehavior.getKey(gdo);
                    SimpleContentElementAdapter adapter = _adapterPool.getSimpleContentElementAdapter();
                    if (key != null) {
                        adapter.fillIn(prop, key, this, ctx);
                    } else {
                        // fall back to reference
                        adapter.fillReference(prop, gdo, this, ctx);
                    }
                    return adapter;
                } else {
                    // key is a DataObject
                    DataObjectDecorator key = (DataObjectDecorator)DataObjectBehavior.getKey(gdo);
                    NonSequencedElementAdapter adapter = _adapterPool.getNonSequencedElementAdapter();
                    adapter.fillIn(prop, key.getInstance(), true, this, ctx);
                    return adapter;
                }
            } else {
                if (gdo.getType().isSequenced()) {
                    SequencedElementAdapter adapter = _adapterPool.getSequencedElementAdapter();
                    adapter.fillIn(prop, gdo, this, ctx);
                    return adapter;
                }else {
                    NonSequencedElementAdapter adapter = _adapterPool.getNonSequencedElementAdapter();
                    adapter.fillIn(prop, gdo, this, ctx);
                    return adapter;
                }
            }
        } else {
            NonSequencedElementAdapter adapter = _adapterPool.getNonSequencedElementAdapter();
            adapter.fillIn(prop, null, this, ctx);
            return adapter;
        }
    }

    public String generateReference() {
        if (_isKey) {
            AbstractElementAdapter key = this;
            while (key._parent != null && key._parent._isKey) {
                key = key._parent;
            }
            return key.getDataObject().toString();
        }
        GenericDataObject dataObject = getDataObject();
        try {
            if (dataObject != null) {
                return getReferenceHandler().generateReference(dataObject, null);
            }
            AbstractElementAdapter parent = _parent;
            while (dataObject == null && parent != null) {
                dataObject = parent.getDataObject();
                parent = parent._parent;
            }
            return (dataObject != null ? getReferenceHandler().generateReference(dataObject, null) : "") + ' ' + getLocalName();
        } catch (XMLStreamException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    protected ReferenceHandler getReferenceHandler() {
        if (_referenceHandler == null) {
            _referenceHandler =
                new ReferenceHandlerImpl(getRootElement(), _rootElementProperties._orphanHandler);
        }
        return _referenceHandler;
    }

    protected OrphanHandler getOrphanHandler() {
        return _rootElementProperties._orphanHandler;
    }

    private Map<String,Integer> getLastIndexMap() {
        return _rootElementProperties._lastIndexMap;
    }

    public boolean isStarted() {
        return _status.equals(Status.STARTED);
    }

    public boolean isEnded() {
        return _status.equals(Status.ENDED);
    }

    public void start() {
        _status = Status.STARTED;
    }

    public void end() {
        _status = Status.ENDED;
    }

    static class RootElementProperties {
        private final Map<String,Integer> _lastIndexMap = new HashMap<String,Integer>();;

        private final OrphanHandler _orphanHandler;

        /**
         *
         */
        private RootElementProperties(DataObject pRootObject) {
            super();
            _orphanHandler = new OrphanHandler(pRootObject);
        }


    }

    class ReferenceHandlerImpl extends ReferenceHandler {

        /**
         * @param pAbstractElementAdapter
         * @param pOrphanHandler
         */
        protected ReferenceHandlerImpl(
            AbstractElementAdapter pAbstractElementAdapter,
            OrphanHandler pOrphanHandler) {
            super(pAbstractElementAdapter, pOrphanHandler);
        }

        /* (non-Javadoc)
         * @see com.sap.sdo.impl.xml.ReferenceHandler#checkPrefix(java.lang.String)
         */
        @Override
        public String checkPrefix(String pUri) {
            if (pUri != null && pUri.length() > 0) {
                String prefix = getPrefix(pUri);
                if (prefix == null) {
                    prefix = createPrefix(pUri);
                }
                return prefix + ':';
            }
            return "";
        }

    }
}