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

import static com.sap.sdo.api.util.URINamePair.DATATYPE_JAVA_URI;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_XML_URI;
import static com.sap.sdo.api.util.URINamePair.PROP_SCHEMA_SCHEMA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.helper.SchemaResolver;
import com.sap.sdo.api.types.SapType;
import com.sap.sdo.api.types.schema.Annotated;
import com.sap.sdo.api.types.schema.Annotation;
import com.sap.sdo.api.types.schema.Appinfo;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.schema.SchemaTypeFactory;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;

/**
 * @author D042774
 *
 */
public class XSDHelperImpl implements SapXsdHelper {

    private Set<SchemaLocation> _schemaLocation = new HashSet<SchemaLocation>();

    private final SapHelperContext _helperContext;

    private SchemaResolver _defaultSchemaResolver;

    private XSDHelperImpl(SapHelperContext pHelperContext) {
        _helperContext = pHelperContext;
        setDefaultSchemaResolver(new DefaultSchemaResolver(pHelperContext));
    }

    public static XSDHelper getInstance(SapHelperContext pHelperContext) {
        // to avoid illegal instances
        XSDHelper xsdHelper = pHelperContext.getXSDHelper();
        if (xsdHelper != null) {
            return xsdHelper;
        }
        return new XSDHelperImpl(pHelperContext);
    }

    public String getLocalName(Type pType) {
        URINamePair xsdName =
            SchemaTypeFactory.getInstance().getXsdName(pType.getURI(), pType.getName());
        if (xsdName != null) {
            return xsdName.getName();
        }
        return ((SdoType)pType).getXmlName();
    }

    public String getLocalName(Property property) {
        return ((SdoProperty)property).getXmlName();
    }

    public String getNamespaceURI(Property property) {
        return ((SdoProperty)property).getUri();
    }

    public String getNamespaceURI(Type type) {
        URINamePair xsdName =
            SchemaTypeFactory.getInstance().getXsdName(type.getURI(), type.getName());
        if (xsdName != null) {
            return xsdName.getURI();
        }
        return ((SapType)type).getXmlUri();
    }

    public boolean isAttribute(Property property) {
        return !isElement(property);
    }

    public boolean isElement(Property property) {
        return ((SdoProperty)property).isXmlElement();
    }

    public boolean isMixed(Type type) {
        return ((SdoType)type).isMixedContent();
    }

    public boolean isXSD(Type type) {
        return ((DataObject)type).isSet(TypeType.getSchemaReferenceProperty());
    }

    public Property getGlobalProperty(String uri, String propertyName, boolean isElement) {
        return ((TypeHelperImpl)_helperContext.getTypeHelper()).getOpenContentPropertyByXmlName(uri, propertyName, isElement);
    }
    public String getAppinfo(Type type, String source) {
        Annotated annotated = (Annotated)((DataObject)type).getDataObject(TypeType.getSchemaReferenceProperty());
        return getAppinfo(annotated, source);
    }

    public String getAppinfo(Property property, String source) {
        Annotated annotated = (Annotated)((DataObject)property).getDataObject(PropertyType.getSchemaReferenceProperty());
        String propertyAppInfo = getAppinfo(annotated, source);
        String refAppInfo = null;
        URINamePair ref = ((SdoProperty)property).getRef();
        if (ref != null) {
            Property refProperty = _helperContext.getTypeHelper().getOpenContentProperty(ref.getURI(), ref.getName());
            if (refProperty != null) {
                refAppInfo = getAppinfo(refProperty, source);
            }
        }
        if (refAppInfo == null) {
            return propertyAppInfo;
        }
        if (propertyAppInfo == null) {
            return refAppInfo;
        }
        return propertyAppInfo + refAppInfo;
    }

    private String getAppinfo(Annotated annotated, String source) {
        if (annotated == null) {
            return null;
        }
        Annotation annotation = annotated.getAnnotation();
        if (annotation == null) {
            return null;
        }
        List<Appinfo> appinfos = annotation.getAppinfo();
        StringWriter writer = new StringWriter();
        for (Appinfo appinfo: appinfos) {
            if (source == null || source.equals(appinfo.getSource())) {
                XMLDocument document = new XMLDocumentImpl((DataObject)appinfo, URINamePair.SCHEMA_URI, "appinfo");
                document.setXMLDeclaration(false);
                try {
                    _helperContext.getXMLHelper().save(document, writer, null);
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return writer.toString();
    }

    public List<? extends Type> define(String xsd) {
        return define(new StringReader(xsd), null);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.lang.String, com.sap.sdo.api.helper.SchemaResolver)
     */
    public List<Type> define(String pXsd, Map pOptions) {
        return define(new StringReader(pXsd), null, pOptions);
    }

    public List<? extends Type> define(Reader xsdReader, String schemaLocation) {
        return define(xsdReader, schemaLocation, null);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.io.Reader, java.lang.String, com.sap.sdo.api.helper.SchemaResolver)
     */
    public List<Type> define(Reader pXsdReader, String pSchemaLocation, Map pOptions) {
        try {
            SapXmlDocument document = ((SapXmlHelper)_helperContext.getXMLHelper()).load(pXsdReader, pSchemaLocation, getOptions(pOptions));
            return (List)document.getDefinedTypes();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List define(InputStream xsdInputStream, String schemaLocation) {
        return define(xsdInputStream, schemaLocation, null);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.io.InputStream, java.lang.String, com.sap.sdo.api.helper.SchemaResolver)
     */
    public List<Type> define(InputStream pXsdInputStream, String pSchemaLocation, Map pOptions) {
        try {
            SapXmlDocument document = ((SapXmlHelper)_helperContext.getXMLHelper()).load(pXsdInputStream, pSchemaLocation, getOptions(pOptions));
            return (List)document.getNewDefinedTypes();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Map getOptions(Map pOptions) {
        Map result = new HashMap();
        if (pOptions != null) {
            result.putAll(pOptions);
        }
		if (!result.containsKey(SapXmlHelper.OPTION_KEY_SCHEMA_RESOLVER)) {
			result.put(SapXmlHelper.OPTION_KEY_SCHEMA_RESOLVER, getDefaultSchemaResolver());
		}
        result.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_TRUE);
        result.put(SapXmlHelper.OPTION_KEY_SIMPLIFY_OPEN_CONTENT, SapXmlHelper.OPTION_VALUE_FALSE);
        return result;
    }

    public List<Type> define(List<Schema> pSchemas, Map pOptions) throws IOException {
        Map<SchemaLocation,Schema> schemaLocation2Schema =
            new HashMap<SchemaLocation,Schema>(pSchemas.size());
        for (Schema schema : pSchemas) {
            schemaLocation2Schema.put(
                new SchemaLocation(schema.getTargetNamespace(),null), schema);
        }
        XsdToTypesTranslator translator = new XsdToTypesTranslator(_helperContext, pOptions);
        translator.addSchemas(schemaLocation2Schema);
        return translator.defineTypes();
    }

    public String generate(List types) {
        return generate(types, new HashMap());
    }
    public String generate(List types, Map namespaceToSchemaLocation) {
        return generate(types,namespaceToSchemaLocation,new ArrayList<Property>(),new ArrayDeque<Map<String,String>>());
    }
    public String generate(List types, Map<String,String> namespaceToSchemaLocation, List<Property> properties, Deque<Map<String,String>> surroundingUriToPrefix) {
        try {
            StringWriter out = new StringWriter();
            XMLOutputFactory factory = XMLHelperImpl.XML_OUTPUT_FACTORY;
            XMLStreamWriter writer = factory.createXMLStreamWriter(out);
            if (writer == null) {
                //Fallback for old SAP-XMLStreamWriter
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                writer = factory.createXMLStreamWriter(os, "UTF-8");
                generate(types, namespaceToSchemaLocation, surroundingUriToPrefix, writer);
                return os.toString("UTF-8");
            } else {
                generate(types, namespaceToSchemaLocation, surroundingUriToPrefix, writer);
                out.flush();
                return out.toString();
            }
        } catch (XMLStreamException ex) {
            throw new IllegalArgumentException("could not generate XSD",ex);
        } catch (IOException ex) {
            throw new IllegalArgumentException("could not generate XSD",ex);
        }
    }
    
    public Schema generateSchema(String pNamespace, Map<String, String> pNamespaceToSchemaLocation, Map pOptions) {
        Map<String, String> namespaceToSchemaLocation = pNamespaceToSchemaLocation;
        if (namespaceToSchemaLocation == null) {
            namespaceToSchemaLocation = Collections.emptyMap();
        }
        TypeHelperImpl typeHelper = (TypeHelperImpl)_helperContext.getTypeHelper();
        SchemaTranslator schemaTranslator = new SchemaTranslator(_helperContext);
        //TODO global properties?
        return schemaTranslator.getSchema(typeHelper.getTypesForNamespace(pNamespace), namespaceToSchemaLocation);
    }

    /**
     * @param types
     * @param namespaceToSchemaLocation
     * @param surroundingUriToPrefix
     * @param writer
     * @throws XMLStreamException
     */
    private void generate(List types, Map<String, String> namespaceToSchemaLocation, Deque<Map<String, String>> surroundingUriToPrefix, XMLStreamWriter writer) throws XMLStreamException {
//        XsdStaxWriter w = new XsdStaxWriter(writer, _helperContext);
//        w.generate(types, namespaceToSchemaLocation, surroundingUriToPrefix);
//        writer.writeEndDocument();


        SchemaTranslator translator = new SchemaTranslator(_helperContext);
        Schema schema = translator.getSchema(types, namespaceToSchemaLocation);
        XMLDocument doc =
            new XMLDocumentImpl(
                schema, PROP_SCHEMA_SCHEMA.getURI(), PROP_SCHEMA_SCHEMA.getName());
        Map<String,String> nsDefinitions = new HashMap<String,String>();
        nsDefinitions.put("sdox", DATATYPE_XML_URI);
        nsDefinitions.put("sdoj", DATATYPE_JAVA_URI);
        Map<String,Map<String,String>> options = Collections.singletonMap(
            SapXmlHelper.OPTION_KEY_PREFIX_MAP, nsDefinitions);
        XmlStaxWriter w = new XmlStaxWriter(writer, _helperContext);
        w.generate(
            doc,
            _helperContext.getTypeHelper().getOpenContentProperty(
                PROP_SCHEMA_SCHEMA.getURI(), PROP_SCHEMA_SCHEMA.getName()),
                options);
    }

    public boolean containsSchemaLocation(SchemaLocation pSchemaLocation) {
        return _schemaLocation.contains(pSchemaLocation);
    }
    public boolean containsSchemaLocation(String a, String b) {
        return _schemaLocation.contains(new SchemaLocation(a,b));
    }
    public void addSchemaLocation(SchemaLocation pSchemaLocation) {
        _schemaLocation.add(pSchemaLocation);
    }

    @Override
    public String toString() {
        return super.toString() + _schemaLocation.toString();
    }

    @Deprecated
    private Deque<SchemaResolver> _resolverStack = new ArrayDeque<SchemaResolver>();

    @Deprecated
    public SchemaResolver peekResolver() {
    	return getDefaultSchemaResolver();
    }

    @Deprecated
	public void pushResolver(SchemaResolver resolver) {
        setDefaultSchemaResolver(resolver);
	}

    @Deprecated
	public SchemaResolver popResolver() {
        SchemaResolver resolver = _resolverStack.pop();
        _defaultSchemaResolver = _resolverStack.peek();
		return resolver;
	}

    public SchemaResolver getDefaultSchemaResolver() {
        return _defaultSchemaResolver;
    }

    public void setDefaultSchemaResolver(SchemaResolver pSchemaResolver) {
        if (pSchemaResolver == null) {
            throw new NullPointerException("SchemaResolver is null");
        }
        _defaultSchemaResolver = pSchemaResolver;
        // just to keep the old code in sync
        _resolverStack.push(pSchemaResolver);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#getSdoName(com.sap.sdo.api.util.URINamePair)
     */
    public URINamePair getSdoName(URINamePair pUnp) {
        URINamePair sdoName = SchemaTypeFactory.getInstance().getSdoName(pUnp);
        if (sdoName != null) {
            return sdoName;
        }
        Type type = ((TypeHelperImpl)_helperContext.getTypeHelper()).getTypeByXmlName(pUnp.getURI(), pUnp.getName());
        if (type != null) {
            return new URINamePair(type.getURI(), type.getName());
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#getXsdName(com.sap.sdo.api.util.URINamePair)
     */
    public URINamePair getXsdName(URINamePair pUnp) {
        URINamePair xsdName = SchemaTypeFactory.getInstance().getXsdName(pUnp);
        if (xsdName != null) {
            return xsdName;
        }
        Type type = _helperContext.getTypeHelper().getType(pUnp.getURI(), pUnp.getName());
        if (type != null) {
            return new URINamePair(type.getURI(), getLocalName(type));
        }
        return null;
    }

    public boolean isNil(DataObject pDataObject) {
        if (pDataObject == null) {
            return true;
        }
        return ((DataObjectDecorator)pDataObject).getInstance().getXsiNil();
    }

    public void setNil(DataObject pDataObject, boolean pXsiNil) {
        ((DataObjectDecorator)pDataObject).getInstance().setXsiNil(pXsiNil);
    }

    public Property getProperty(Type type, String uri, String xsdName, boolean isElement) {
        if (type == null) {
            return getGlobalProperty(uri, xsdName, isElement);
        }
        return ((SdoType)type).getPropertyFromXmlName(uri, xsdName, isElement);
    }

    public Property getInstanceProperty(DataObject pDataObject, String pUri, String pXsdName, boolean pIsElement) {
        if (pDataObject instanceof DataObjectDecorator) {
            return ((DataObjectDecorator)pDataObject).getInstance().getInstanceProperty(pUri, pXsdName, pIsElement);
        }
        if (pDataObject == null) {
            return getGlobalProperty(pUri, pXsdName, pIsElement);
        }
        SdoProperty property = (SdoProperty)pDataObject.getInstanceProperty(pXsdName);
        if (property != null) {
            if (property.isXmlElement() == pIsElement) {
                String uri = property.getUri();
                if (uri == null) {
                    if (pUri == null) {
                        return property;
                    }
                } else {
                    if (uri.equals(pUri)) {
                        return property;
                    }
                }
            }
        }
        return null;
    }

}
