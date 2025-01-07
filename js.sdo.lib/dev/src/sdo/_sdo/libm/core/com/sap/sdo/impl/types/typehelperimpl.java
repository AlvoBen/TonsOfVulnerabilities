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
package com.sap.sdo.impl.types;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.types.ctx.HelperContexts;
import com.sap.sdo.api.types.schema.Annotated;
import com.sap.sdo.api.types.schema.NarrowMaxMin;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.types.schema.SimpleExplicitGroup;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.HelperContextImpl;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.builtin.ChangeSummaryType;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.DataObjectType;
import com.sap.sdo.impl.types.builtin.MetaDataProperty;
import com.sap.sdo.impl.types.builtin.MetaDataPropertyLogicFacade;
import com.sap.sdo.impl.types.builtin.OpenType;
import com.sap.sdo.impl.types.builtin.PropertyLogic;
import com.sap.sdo.impl.types.builtin.PropertyLogicFacade;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeLogicFacade;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.builtin.UndecidedType;
import com.sap.sdo.impl.types.builtin.cache.HashCache;
import com.sap.sdo.impl.types.builtin.cache.StringInternCache;
import com.sap.sdo.impl.types.builtin.cache.ValueCache;
import com.sap.sdo.impl.types.java.InterfaceGeneratorImpl;
import com.sap.sdo.impl.types.java.JavaTypeFactory;
import com.sap.sdo.impl.types.java.JavaTypeFactory.ReflectionResult;
import com.sap.sdo.impl.types.schema.SchemaTypeFactory;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.types.simple.ListSimpleType;
import com.sap.sdo.impl.util.WeakValueHashMap;
import com.sap.sdo.impl.xml.SchemaLocation;
import com.sap.sdo.impl.xml.XMLHelperImpl;
import com.sap.sdo.impl.xml.XSDHelperImpl;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

public class TypeHelperImpl implements SapTypeHelper {

    public static final String XML_NAMESPACE = "com/sap/sdo/impl/xml/XmlNamespace.xsd";
    private final static Logger LOGGER = Logger.getLogger(TypeHelperImpl.class.getName());
    static {
        LOGGER.setLevel(Level.FINE);
    }

    private final static ValueCache<String> URI_CACHE;
    private final static ValueCache<String> NAME_CACHE;

    private final HelperContextImpl _helperContext;
    private final JavaTypeFactory _javaTypeFactory;
    private final Map<String, Namespace> _uriToNamespace = new HashMap<String, Namespace>();
    private final Map<Class, SdoType> _classToType = new HashMap<Class, SdoType>();

    static {
        // TODO allow configuration of usage of StringInternCache
        // determine if the StAX parser uses String.intern()
        // if yes, use StringInternCache
        try {
            String xml = "<ns:element ns2:attribute=\"value2\" xmlns:ns=\"ns\" xmlns:ns2=\"ns\">value1</ns:element>";
            XMLStreamReader xmlStreamReader = XMLHelperImpl.XML_INPUT_FACTORY.createXMLStreamReader(new StringReader(xml));
            while (!xmlStreamReader.isStartElement()) {
                xmlStreamReader.next();
            }
            String elementName = xmlStreamReader.getLocalName();
            String elementUri = xmlStreamReader.getNamespaceURI();
            String attributeName = xmlStreamReader.getAttributeLocalName(0);
            String attributeUri = xmlStreamReader.getAttributeNamespace(0);
            if ((elementName == "element") && (attributeName == "attribute")) {
                NAME_CACHE = StringInternCache.getInstance();
            } else {
                NAME_CACHE = new HashCache<String>(1024, 4096);
            }
            if ((elementUri == "ns") || (attributeUri == "ns2")) {
                URI_CACHE = StringInternCache.getInstance();
            } else {
                URI_CACHE = new HashCache<String>(256, 2048);
            }
        } catch (XMLStreamException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private TypeHelperImpl(HelperContextImpl pHelperContext) {
        _helperContext = pHelperContext;
        _javaTypeFactory = new JavaTypeFactory(pHelperContext);
    }

    public static TypeHelper getInstance(HelperContextImpl pHelperContext) {
        // to avoid illegal instances
        TypeHelper typeHelper = pHelperContext.getTypeHelper();
        if (typeHelper != null) {
            return typeHelper;
        }
        if (pHelperContext.getParent() == null) {
            return new TypeHelperCore(pHelperContext);
        }
        return new TypeHelperImpl(pHelperContext);
    }



    public Namespace getNamespace(String pUri) {
        String uri = pUri;
        if (uri == null) {
            uri = "";
        }
        Namespace namespace = getUriToNamespace().get(uri);
        if (namespace == null) {
            TypeHelperImpl parent = getParent();
            if (parent != null) {
                namespace = parent.getNamespace(uri);
                if (namespace != null) {
                    //if my parent has a namespace, I need this too
                    namespace = new Namespace(uri, _helperContext);
                    getUriToNamespace().put(uri, namespace);
                }
            }
        }
        return namespace;
    }

    TypeHelperImpl getParent() {
        HelperContextImpl parentContext = _helperContext.getParent();
        if (parentContext != null) {
            return (TypeHelperImpl)parentContext.getTypeHelper();
        }
        return null;
    }

    protected Namespace getOrCreateNamespace(final String uri) {
        Namespace ret = getUriToNamespace().get(uri);
        if (ret == null) {
            ret = new Namespace(uri, _helperContext);
            getUriToNamespace().put(uri,ret);
        }
        return ret;
    }
    /**
     * This guarantees the singleton nature of all registered types.
     * @param type
     * @return
     */
    private Type replaceReferencedTypes(final SdoType type, final Set<Type> added) {
        if (added.contains(type)) {
            return type;
        }
        //TODO no unp
        final URINamePair unp = type.getQName();
        if (unp.getURI()==null || unp.getName()==null) {
            throw new IllegalArgumentException("Uri#Name is " + type.getURI() + '#' + type.getName());
        }
        if (unp.getURI().length() == 0 && ((DataObject)type).get(TypeType.URI) == null) {
            // fix types uri
            ((DataObject)type).setString(TypeType.URI, "");
        }
        final Type registeredType = getType(type.getURI(), type.getName());
        if (registeredType != null && URINamePair.fromType(registeredType).equalsUriName(type)) {
            return registeredType;
        }
        if (type instanceof TypeLogicFacade) {
            ((TypeLogicFacade)type).setHelperContext(_helperContext);
        }
        getOrCreateNamespace(unp.getURI()).put(type);
        added.add(type);
        SdoType keyType = (SdoType)((DataObject)type).get(TypeType.KEY_TYPE);
        if (keyType != null) {
            Type newKeyType = replaceReferencedTypes(keyType, added);
            if (!keyType.equals(newKeyType)) {
                ((DataObject)type).set(TypeType.KEY_TYPE, newKeyType);
            }
        }

        final List baseTypes = type.getBaseTypes();
        final ArrayList<Type> newBaseTypes = new ArrayList<Type>(baseTypes.size());
        if (baseTypes.isEmpty()) {
            if (type.getInstanceClass() == List.class) {
                ListSimpleType listSimpleType;
                if (type instanceof ListSimpleType) {
                    //TODO replace the itemType
                } else {
                    SdoType itemType = (SdoType)type.get(ListSimpleType.getItemTypeProperty());
                    Type newItemType = replaceReferencedTypes(itemType, added);
                    if (!itemType.equals(newItemType)) {
                        ((DataObject)type).set(ListSimpleType.getItemTypeProperty(), newItemType);
                    }
                    listSimpleType = new ListSimpleType(_helperContext, (DataObject)type);
                    getOrCreateNamespace(unp.getURI()).put(listSimpleType);
                    added.add(listSimpleType);
                    return listSimpleType;
                }
            }
        } else {
            boolean changes = false;
            for (Object obj: baseTypes) {
                final SdoType oldType = (SdoType)obj;
                final Type newType = replaceReferencedTypes(oldType,added);
                newBaseTypes.add(newType);
                if (!oldType.equals(newType)) {
                    changes = true;
                }
                if (newType.isOpen()) {
                    ((DataObject)type).setBoolean(TypeType.OPEN,true);
                }
            }
            if (changes) {
                ((DataObject)type).set(TypeType.BASE_TYPE,newBaseTypes);
            }
        }
        if (type.isDataType()) {
            if (newBaseTypes.size() == 0) {
                if (type.getInstanceClass() == null) {
                    throw new IllegalArgumentException("DataType with no base type or instanceClass: "+type.getURI()+":"+type.getName());
                }
//                registerTypeByClass(type);
            } else if (newBaseTypes.size() > 1) {
                throw new IllegalArgumentException("DataType cannot have multiple inheritence: "+type.getURI()+":"+type.getName());
            } else if (!newBaseTypes.get(0).isDataType()) {
                throw new IllegalArgumentException("DataType cannot extend a ComplexType: "+type.getURI()+":"+type.getName());
            } else if (type.getInstanceClass() == null) {
                final Class instanceClass = newBaseTypes.get(0).getInstanceClass();
                ((DataObject)type).set(TypeType.getJavaClassProperty(), instanceClass.getName());
                ((TypeLogicFacade)type).setInstanceClass(instanceClass); //TODO use this on SdoType
            }
//            else {
//                registerTypeByClass(type);
//            }
        } else {
            List<SdoProperty> declaredProperties = new ArrayList<SdoProperty>(type.getDeclaredProperties());
            for (SdoProperty prop: declaredProperties) {
                final Type replacedType = replaceReferencedTypes((SdoType)prop.getType(),added);
                if (!replacedType.equals(prop.getType())) {
                    ((DataObject)prop).set(PropertyType.TYPE,replacedType);
                }
                //ignore polymorph properties
                if (isPropInBaseTypes(type, prop)) {
                    ((DataObject)prop).detach();
                } else {
                    ((PropertyLogicFacade)prop).initProperty(null);
                }
            }
            Class instanceClass = type.getInstanceClass();
            if (instanceClass != null) {
                _classToType.put(instanceClass, type);
            }
        }
        return type;
    }


// more tests are necessary (interface generation and parsing)
//    private void registerTypeByClass(SdoType pType) {
//        Class instanceClass = pType.getInstanceClass();
//        if (lookupTypeForClass(instanceClass) == null) {
//            _classToType.put(instanceClass, pType);
//        }
//    }

    public boolean isPropInBaseTypes(Type pType,Property pProperty) {
        List<SdoType<?>> baseTypes = pType.getBaseTypes();
        for (SdoType<?> baseType : baseTypes) {
            SdoProperty property = (SdoProperty)pProperty;
            SdoProperty baseProp =
                baseType.getPropertyFromXmlName(
                    property.getUri(),
                    property.getXmlName(),
                    property.isXmlElement());
            if (baseProp != null) {
                checkExtendProperty(baseProp, (SdoProperty)pProperty);
                return true;
            }
        }
        return false;
    }

    private void checkExtendProperty(SdoProperty pSuperProperty, SdoProperty pPropertyObject) {
        boolean xmlElement = pSuperProperty.isXmlElement();
        if (pPropertyObject.isXmlElement() != xmlElement) {
            throw new IllegalArgumentException("Name conflict between attribute and element with name " +
                pSuperProperty.getName() + " on type " +
                ((SdoType)pPropertyObject.getContainingType()).getQName().toStandardSdoFormat());
        }
        if (xmlElement && !pSuperProperty.isMany() && pPropertyObject.isMany()) {
            checkPropertyDefined(pSuperProperty);
            ((DataObject)pSuperProperty).setBoolean(PropertyType.MANY, true);
        }
        SdoType targetType = (SdoType)pSuperProperty.getType();
        SdoType commonType = getCommonType(targetType, (SdoType)pPropertyObject.getType());
        if (targetType != commonType) {
            checkPropertyDefined(pSuperProperty);
            ((DataObject)pSuperProperty).set(PropertyType.TYPE, commonType);
        }
    }

    private void setXsdType(DataObject pPropertyObject, SdoType pDataType) {
        if (pDataType instanceof ListSimpleType) {
            URINamePair xsdTypeUnp = ((ListSimpleType)pDataType).getXsdType();
            if (xsdTypeUnp != null) {
                pPropertyObject.setString(PropertyType.getXsdTypeProperty(), xsdTypeUnp.toStandardSdoFormat());
            }
        } else {
            for (SdoType baseType: (List<SdoType>)pDataType.getBaseTypes()) {
                setXsdType(pPropertyObject, baseType);
            }
        }
    }

    //
    // SDO interface
    //

    /**
     * language closure: data objects may represent types (or Type can be viewed as
     * a Data Object)
     */
    public synchronized Type define(final DataObject type) {
        if (!TypeType.getInstance().isInstance(type)) {
            throw new IllegalArgumentException("expected data object of type "+TypeType.getInstance());
        }
        final Set<Type> added = new HashSet<Type>();
        final Type ret = replaceReferencedTypes((SdoType)type,added);
        for (Type newType: added) {
            hookupOpposites(newType);
            ((SdoType)newType).useCache();
            if (newType instanceof DataObjectDecorator) {
                GenericDataObject gdo = ((DataObjectDecorator)newType).getInstance();
                gdo.trimMemory();
                gdo.setReadOnlyMode(true);
            }
        }
        return ret;
    }

    private void hookupOpposites(final Type type) {
        for (SdoProperty prop: (List<SdoProperty>)type.getDeclaredProperties()) {
            DataObject propObject = (DataObject)prop;
            if (!propObject.isSet(PropertyType.OPPOSITE_INTERNAL)) {
                continue;
            }
            if (propObject.isSet(PropertyType.OPPOSITE)) {
                continue;
            }
            final String opPropName = propObject.getString(PropertyType.OPPOSITE_INTERNAL);
            final SdoProperty opProp = (SdoProperty)prop.getType().getProperty(opPropName);
            if (opProp == null) {
                LOGGER.severe("Opposite of property "+type.getName()+"."+prop.getName()+" not found");
                continue;
            }
            if (prop.isMany() && opProp.isContainment()) {
                LOGGER.severe("Opposite of multivalue property "+type.getName()+"."+prop.getName()+" is containment");
                continue;
            }
            if (prop.isContainment() && opProp.isMany()) {
                LOGGER.severe("Opposite of contain property "+type.getName()+"."+prop.getName()+" multivalue");
                continue;
            }
            DataObject opPropObject = (DataObject)opProp;
            final String opopPropName = opPropObject.getString(PropertyType.OPPOSITE_INTERNAL);
            if (opopPropName!=null && !opopPropName.equals(prop.getName())) {
                LOGGER.severe("Opposite of property "+type.getName()+"."+prop.getName()+" does not match");
                continue;
            }
            ((PropertyLogicFacade)prop).setOppositeProperty(opProp);
            ((PropertyLogicFacade)opProp).setOppositeProperty(prop);
        }
    }

    public List define(final List types) {
        final int l = types.size();
        final List<Type> list = new ArrayList<Type>(l);
        final Set<Type> added = new HashSet<Type>();
        for (int i = 0; i<l; i++) {
            DataObject typeObj = (DataObject)types.get(i);
            if (!TypeType.getInstance().isInstance(typeObj)) {
                throw new IllegalArgumentException("expected data object of type "+TypeType.getInstance());
            }
            list.add(replaceReferencedTypes((SdoType)typeObj,added));
        }
        for (Type newType: added) {
            hookupOpposites(newType);
            ((SdoType<?>)newType).useCache();
            if (newType instanceof DataObjectDecorator) {
                GenericDataObject gdo = ((DataObjectDecorator)newType).getInstance();
                gdo.trimMemory();
                gdo.setReadOnlyMode(true);
            }
        }
        added.removeAll(list);
        list.addAll(added);
        return list;
    }

    public synchronized Type getType(final Class clz) {
        Type type = getResolvedType(clz);
        if (type != null) {
            return type;
        }
        final URINamePair unp = _javaTypeFactory.getQNameFromClass(clz);
        if (unp == null) {
            return null;
        }
        type = getType(unp.getURI(), unp.getName());
        if (type == null) {
            final JavaTypeFactory.ReflectionResult reflectionResult;
            try {
                reflectionResult = _javaTypeFactory.createTypeFromClass(unp,clz);
            } catch (JavaTypeFactory.NoBeanException e) {
                return null;
            }
            defineTypesFromXsd(clz.getClassLoader(), reflectionResult);
            Type ret = define((DataObject)reflectionResult.root);
            if (reflectionResult.openContentProps != null) {
                for (DataObject p: reflectionResult.openContentProps) {
                    String uri = ((Property)p).getType().getURI();
                    if (uri == null) {
                        uri = "";
                    }
                    defineOpenContentProperty(uri, p);
                }
            }
            _classToType.putAll((Map)reflectionResult.types);
            return ret;
        }
        if (type.isDataType() || clz.equals(type.getInstanceClass())) {
            return type;
        }
        if (type.getInstanceClass() != null) {
            throw new IllegalArgumentException("Attempt to reset instance class of {"+type.getURI()+"}"+type.getName()+
                    " from "+type.getInstanceClass().getName()+" to "+clz.getName() + " in HelperContext " + _helperContext.getId());
        }
        final JavaTypeFactory.ReflectionResult reflectionResult = _javaTypeFactory.createTypeFromClass(unp,clz);
        defineTypesFromXsd(clz.getClassLoader(), reflectionResult);
        _classToType.putAll((Map)reflectionResult.types);
        return type;
    }

    public SdoType getResolvedType(Class pClass) {
        checkNamespacesInitialized();
        SdoType type = _classToType.get(pClass);
        if (type == null) {
            TypeHelperImpl parent = getParent();
            if (parent != null) {
                type = parent.getResolvedType(pClass);
            }
        }
        return type;
    }

    public synchronized Type getType(final String pUri, final String typeName) {
        String uri = pUri;
        if (uri == null) {
            uri = "";
        }
        if (typeName == null) {
            return getNamespace(uri);
        }
        TypeAndContext typeAndContext = getTypeAndContext(pUri, typeName);
        if (typeAndContext != null) {
            return typeAndContext.getSdoType();
        }
        return null;
    }

    public TypeAndContext getTypeAndContext(final String pUri, final String typeName) {
        final Namespace namespace = getNamespace(pUri);
        if (namespace != null) {
            return namespace.getTypeAndContext(typeName);
        }
        return null;
    }

    public TypeAndContext getTypeAndContext(final SdoType pType) {
        if (pType.getHelperContext()==_helperContext) {
            return pType;
        }
        TypeAndContext typeAndContext = getTypeAndContext(pType.getURI(), pType.getName());
        if (typeAndContext == null) {
            throw new IllegalArgumentException("Type " + URINamePair.fromType(pType).toStandardSdoFormat() +
                    " is not in HelperContext " + _helperContext.getId());
        }
        return typeAndContext;
    }

    public Type getTypeByXmlName(String uri, String xmlName) {
        final Namespace namespace = getNamespace(uri);
        if (namespace != null) {
            return namespace.getTypeFromXmlName(xmlName);
        }
        return null;
    }

    private synchronized Property createProperty(final DataObject propObject) {
        if (!(propObject instanceof Property)) {
            throw new IllegalArgumentException("unexpected _type \""+propObject.getType()+"\" - expected "+PropertyType.getInstance());
        }
        PropertyLogicFacade prop = (PropertyLogicFacade)propObject;
        PropKey key = new PropKey(_helperContext);
        return prop.initProperty(key);
    }
    
    public SdoProperty lookupOrRegisterOnDemandProp(PropKey pPropKey, SdoProperty pNewProp) {
        SdoProperty ret = _keyToProperty.get(pPropKey);
        if (ret == null) {
            _keyToProperty.put(pPropKey,pNewProp);
        }
        return ret;
    }
    
    private Map<PropKey,SdoProperty> _keyToProperty = new WeakValueHashMap<PropKey, SdoProperty>();
    public Property getOpenContentProperty(String uri, String propertyName) {
        final Namespace namespace = getNamespace(uri);
        if (namespace != null) {
            return namespace.getProperty(propertyName);
        }
        return null;
    }
    public synchronized Property defineOpenContentProperty(final String uri, final DataObject propObj) {
        SdoProperty property = (SdoProperty)propObj;
        if (property.defined()) {
            if (property == getOpenContentPropertyByXmlName(uri, property.getXmlName(), property.isXmlElement())) {
                return property;
            }
            throw new IllegalArgumentException("Property " + uri + '#'
                + propObj.getString(PropertyType.NAME) + " is already defined");
        }
        propObj.unset(PropertyConstants.OPPOSITE_INTERNAL);
        propObj.unset(PropertyConstants.READ_ONLY);
        propObj.unset(PropertyConstants.CONTAINING_TYPE);

        if (uri == null) {
            // unset invalid properties of on-demand open content
            propObj.unset(PropertyConstants.DEFAULT);
            propObj.unset(PropertyConstants.NULLABLE);

            property = (SdoProperty)createProperty(propObj);
        } else {
            propObj.set(PropertyType.getUriProperty(), uri);
            Namespace namespace = getOrCreateNamespace(uri);
            property = (SdoProperty)defineOpenContentProperty(propObj, namespace);
        }
        return property;
    }

    public InterfaceGenerator createInterfaceGenerator(String pRootPath) {
        return new InterfaceGeneratorImpl(pRootPath, _helperContext);
    }

    public Property getOpenContentPropertyByXmlName(String uri, String xmlName, boolean isElement) {
        final Namespace namespace = getNamespace(uri);
        if (namespace != null) {
            return namespace.getPropertyFromXmlName(xmlName, isElement);
        }
        return null;
    }

    Property defineOpenContentProperty(final DataObject propObj, Namespace namespace) {
        propObj.set(PropertyType.CONTAINING_TYPE, namespace);
        final SdoProperty property = (SdoProperty)propObj;
        final SdoType oldType = (SdoType)property.getType();
        propObj.set(PropertyType.TYPE, replaceReferencedTypes(oldType,new HashSet<Type>()));

        SdoProperty registeredProperty = namespace.getProperty(property.getName());
        if (registeredProperty != null && registeredProperty.isXmlElement()
            && propObj.isSet(PropertyType.getSubstitutesProperty())) {
                final List existing = ((DataObject)registeredProperty).getList(PropertyType.getSubstitutesProperty());
            final List newItems = new ArrayList(propObj.getList(PropertyType.getSubstitutesProperty()));
                newItems.removeAll(existing);
                existing.addAll(newItems);
            return registeredProperty;
            }

        ((PropertyLogicFacade)property).initProperty(null);

        registeredProperty = (SdoProperty)getOpenContentPropertyByXmlName(
            namespace.getURI(), property.getXmlName(), property.isXmlElement());
        if (registeredProperty != null) {
            if (registeredProperty.getName().equals(property.getName())
                && registeredProperty.isMany()==property.isMany()
                && registeredProperty.getType()==property.getType()) {
            return registeredProperty;
            }
            throw new IllegalArgumentException("Property " + property.getUri() + '#'
                + property.getName() + " is already defined");
        }

        namespace.put(property);
        return property;
    }

    public List<Type> getTypesForNamespace(final String uri) {
        final Namespace namespace = getUriToNamespace().get(uri);
        if (namespace == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<Type>(namespace.getTypes()));
    }

    private void defineTypesFromXsd(final ClassLoader cl, final ReflectionResult result) {
        try {
            for (Entry<String, String> schemaLocation: result.schemas.entrySet()) {
                String namespace = schemaLocation.getKey();
                String schemaLoc = schemaLocation.getValue();
                if (schemaLoc != null && !((XSDHelperImpl)_helperContext.getXSDHelper()).containsSchemaLocation(new SchemaLocation(namespace, schemaLoc))) {
                    InputStream s;
                    URI uri = new URI(schemaLoc).normalize();
                    String uriString = uri.toString();
                    if (!(uri.isAbsolute())) {
                        s = cl.getResourceAsStream(uriString);
                        if (s == null) {
                            s = SapHelperProviderImpl.getClassLoader().getResourceAsStream(uriString);
                        }
                    } else {
                        s = uri.toURL().openStream();
                    }
                    if (s == null) {
                        throw new IllegalArgumentException("Schema location couldn't be resolved: " + schemaLoc);
                    }
                    _helperContext.getXSDHelper().define(s,uriString);
                }
            }
            if (result.schemas.size()!=0) {
                _javaTypeFactory.enrichType(result.types);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<Property> getPropertiesForNamespace(String uri) {
        final Namespace namespace = getUriToNamespace().get(uri);
        if (namespace == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<Property>(namespace.getProperties()));

    }
    private Map<String, Namespace> getUriToNamespace() {
        checkNamespacesInitialized();
        return _uriToNamespace;
    }

    /**
     * TODO initialize in contructor
     *
     */
    protected void checkNamespacesInitialized() {
    }

    public JavaTypeFactory getJavaTypeFactory() {
        return _javaTypeFactory;
    }

    public static SdoProperty getSimpleContentProperty(Type pComplexType) {
        return ((SdoType)pComplexType).getSimpleContentValueProperty();
    }

    public static SdoProperty getSimpleContentProperty(DataObject pDataObject) {
        SdoType type = ((SdoType)pDataObject.getType());
        if (type == OpenType.getInstance()) {
            DataObject valueProperty = (DataObject)pDataObject.getInstanceProperty(TypeType.VALUE);
            if ((valueProperty != null) && valueProperty.getBoolean(PropertyType.getSimpleContentProperty())) {
                return (SdoProperty)valueProperty;
            }
            return null;
        }
        return type.getSimpleContentValueProperty();
    }

    public SdoType getCommonType(SdoType pTargetType, SdoType pType) {
        if (!pTargetType.isDataType()) {
            if (!pType.isDataType()) {
                return getCommonTypeComplexComplex(pTargetType, pType);
            }
            return getCommonTypeComplexData(pTargetType, pType);
        }
        if (!pType.isDataType()) {
            return getCommonTypeComplexData(pType, pTargetType);
        }
        return getCommonTypeDataData(pTargetType, pType);
    }

    private SdoType getCommonTypeComplexComplex(SdoType pTargetType, SdoType pType) {
        SdoType commonType = getCommonBaseType(pTargetType, pType);
        if (commonType != null) {
            return commonType;
        }
        if (pTargetType.isMixedContent() || pType.isMixedContent()) {
            return (SdoType)getType(URINamePair.MIXEDTEXT_TYPE.getURI(), URINamePair.MIXEDTEXT_TYPE.getName());
        }
        if (pTargetType.isOpen() || pType.isOpen()) {
            return (SdoType)getType(URINamePair.OPEN.getURI(), URINamePair.OPEN.getName());
        }
        return (SdoType)getType(URINamePair.DATAOBJECT.getURI(), URINamePair.DATAOBJECT.getName());
    }

    private SdoType getCommonTypeDataData(SdoType pTargetType, SdoType pType) {
        ListSimpleType targetListSimpleType = getListSimpleType(pTargetType);
        ListSimpleType listSimpleType = getListSimpleType(pType);
        if ((targetListSimpleType == null) && (listSimpleType == null)) {
            SdoType commonType = getCommonBaseType(pTargetType, pType);
            if (commonType != null) {
                return commonType;
            }
            Class commonBaseClass = getCommonBaseClass(pTargetType.getInstanceClass(), pType.getInstanceClass());
            commonType = (SdoType)getType(commonBaseClass);
            return commonType;
        }
        if ((targetListSimpleType != null) && (listSimpleType != null)) {
            SdoType targetItemType = targetListSimpleType.getItemType();
            SdoType itemType = listSimpleType.getItemType();
            SdoType commonItemType = getCommonTypeDataData(targetItemType, itemType);
            if (commonItemType == targetItemType) {
                return targetListSimpleType;
            }
            return new ListSimpleType(commonItemType, _helperContext);
        }
        if (targetListSimpleType != null) {
            SdoType targetItemType = targetListSimpleType.getItemType();
            return getCommonTypeListData(targetListSimpleType, targetItemType, pType);
        }
        SdoType targetItemType = listSimpleType.getItemType();
        return getCommonTypeListData(listSimpleType, targetItemType, pTargetType);
    }

    private SdoType getCommonTypeListData(ListSimpleType pTargetListSimpleType, SdoType pTargetItemType, SdoType pType) {
        SdoType commonItemType = getCommonTypeDataData(pTargetItemType, pType);
        if (commonItemType == pTargetItemType) {
            return pTargetListSimpleType;
        }
        return new ListSimpleType(commonItemType, _helperContext);
    }

    private ListSimpleType getListSimpleType(SdoType pType) {
        if (pType instanceof ListSimpleType) {
            return (ListSimpleType)pType;
        }
        for (SdoType baseType: (List<SdoType>)pType.getBaseTypes()) {
            ListSimpleType listSimpleType = getListSimpleType(baseType);
            if (listSimpleType != null) {
                return listSimpleType;
            }
        }
        return null;
    }

    private SdoType getCommonTypeComplexData(SdoType pTargetType, SdoType pType) {
        SdoProperty simpleProperty = getSimpleContentProperty(pTargetType);
        if (simpleProperty != null) {
            SdoType simpleType = (SdoType)simpleProperty.getType();
            SdoType commonType = getCommonTypeDataData(simpleType, pType);
            if (commonType != simpleType) {
                checkPropertyDefined(simpleProperty);
                ((DataObject)simpleProperty).set(PropertyType.TYPE, commonType);
            }
            return pTargetType;

        }
        return UndecidedType.getInstance();
    }

    private SdoType getCommonBaseType(SdoType pTargetType, SdoType pType) {
        if (pTargetType.isAssignableType(pType)) {
            return pTargetType;
        }
        for (SdoType baseType: (List<SdoType>)pTargetType.getBaseTypes()) {
            SdoType commonType = getCommonBaseType(baseType, pType);
            if (commonType != null) {
                return commonType;
            }
        }
        if (pTargetType instanceof JavaSimpleType) {
            SdoType internalBaseType = ((JavaSimpleType)pTargetType).getInternalBaseType();
            if (internalBaseType != null) {
                return getCommonBaseType(internalBaseType, pType);
            }
        }
        return null;
    }

    private Class getCommonBaseClass(Class pCommonClass, Class pClass) {
        if (pCommonClass == null) {
            return pClass;
        }
        if (pCommonClass.isAssignableFrom(pClass)) {
            return pCommonClass;
        }
        if (pClass.isAssignableFrom(pCommonClass)) {
            return pClass;
        }
        return Object.class;
    }

    private void checkPropertyDefined(SdoProperty pTargetProperty) {
        if (pTargetProperty.defined()) {
            throw new IllegalArgumentException("Schema forces that property " + pTargetProperty.getName()
                + "of type " + ((SdoType)pTargetProperty.getType()).getQName().toStandardSdoFormat()
                + " must be changed, but it is already finalized");
        }
    }

    public static ValueCache<String> getNameCache() {
        return NAME_CACHE;
    }

    public static ValueCache<String> getUriCache() {
        return URI_CACHE;
    }

    public Type lookupTypeForClass(Class instanceClass) {
        Type ret = _classToType.get(instanceClass);
        if (ret != null) {
            return ret;
        }
        if (getParent()==null) {
            return null;
        }
        return getParent().lookupTypeForClass(instanceClass);
    }

    public void fillHelperContextDO(com.sap.sdo.api.types.ctx.HelperContext pContextDO) {
        DataFactory dataFactory = _helperContext.getDataFactory();
        Collection<Namespace> namespaces = new TreeMap<String, Namespace>(getUriToNamespace()).values();
        for (Namespace namespace: namespaces) {
            com.sap.sdo.api.types.ctx.Namespace namespaceDO =
                (com.sap.sdo.api.types.ctx.Namespace)dataFactory.create(com.sap.sdo.api.types.ctx.Namespace.class);
            namespace.fillNamespaceDO(namespaceDO);
            if (((DataObject)namespaceDO).getSequence().size() > 0) {
                pContextDO.getNamespace().add(namespaceDO);
            }
        }
    }

    public void removeTypesAndProperties(Set<Type> pTypes, Set<Property> pProperties) {
        findTransitiveClosure(pTypes, pProperties);
        for (Type type: pTypes) {
            Namespace namespace = _uriToNamespace.get(type.getURI());
            namespace.removeType((SdoType)type);
        }
        for (Property property: pProperties) {
            SdoProperty sdoProp = (SdoProperty)property;
            Namespace namespace = _uriToNamespace.get(sdoProp.getUri());
            namespace.removeProperty(sdoProp);
        }
    }

    private void findTransitiveClosure(Set<Type> pTypes, Set<Property> pProperties) {
        int numTypes;
        int numProps;
        do {
            numTypes = pTypes.size();
            numProps = pProperties.size();
            for (Namespace namespace: _uriToNamespace.values()) {
                for (SdoType type: namespace.getTypes()) {
                    boolean hit = pTypes.contains(type);
                    if (hit) continue;
                    for (Type baseType: (List<Type>)type.getBaseTypes()) {
                        if (pTypes.contains(baseType)) {
                            pTypes.add(type);
                            hit = true;
                            break;
                        }
                    }
                    if (hit) continue;
                    for (Property property: (List<Property>)type.getDeclaredProperties()) {
                        if (pTypes.contains(property.getType())) {
                            pTypes.add(type);
                            break;
                        }
                    }
                }
                for (Property property: (List<Property>)namespace.getProperties()) {
                    if (!pProperties.contains(property)) {
                        if (pTypes.contains(property.getType())) {
                            pProperties.add(property);
                        }
                    }
                }
            }
        } while (pTypes.size() > numTypes || pProperties.size() > numProps);
    }

    private static class TypeHelperCore extends TypeHelperImpl {
        private boolean _namespacesInitialized = false;

        public TypeHelperCore(HelperContextImpl pHelperContext) {
            super(pHelperContext);
        }

        /**
         * TODO initialize in contructor
         *
         */
        @Override
        protected synchronized void checkNamespacesInitialized() {
            if (!_namespacesInitialized) {
                _namespacesInitialized = true;
                createNamespaces();
                createClassMapping();
                initBuiltInTypes();
                initSchemaBuiltInTypes();
                createCommonjSdoXmlNamespace();
                createCommonjSdoNamespace();
                createCommonjSdoJavaNamespace();
                createSchemaNamespace();
                createCtxNamespace();
                parseSchemas();
            }
        }

        private void createNamespaces() {
            createNameSpace(URINamePair.DATATYPE_URI);
            createNameSpace(URINamePair.DATATYPE_XML_URI);
            createNameSpace(URINamePair.DATATYPE_JAVA_URI);
            createNameSpace(URINamePair.INTERNAL_URI);
            createNameSpace(URINamePair.SCHEMA_URI);
            createNameSpace(URINamePair.XML_URI);
            createNameSpace(URINamePair.CTX_URI);
        }

        private void createNameSpace(String pUri) {
            super._uriToNamespace.put(pUri, new Namespace(pUri, super._helperContext));
        }

        private void createClassMapping() {
            Map<Class, SdoType> classToType = super._classToType;
            classToType.put(Boolean.class,JavaSimpleType.BOOLEANOBJECT);
            classToType.put(boolean.class,JavaSimpleType.BOOLEAN);
            classToType.put(Byte.class,JavaSimpleType.BYTEOBJECT);
            classToType.put(byte.class,JavaSimpleType.BYTE);
            classToType.put(byte[].class,JavaSimpleType.BYTES);
            classToType.put(Character.class,JavaSimpleType.CHARACTEROBJECT);
            classToType.put(char.class,JavaSimpleType.CHARACTER);
            classToType.put(Date.class,JavaSimpleType.DATE);
            classToType.put(BigDecimal.class,JavaSimpleType.DECIMAL);
            classToType.put(Double.class,JavaSimpleType.DOUBLEOBJECT);
            classToType.put(double.class,JavaSimpleType.DOUBLE);
            classToType.put(Float.class,JavaSimpleType.FLOATOBJECT);
            classToType.put(float.class,JavaSimpleType.FLOAT);
            classToType.put(Integer.class,JavaSimpleType.INTOBJECT);
            classToType.put(int.class,JavaSimpleType.INT);
            classToType.put(BigInteger.class,JavaSimpleType.INTEGER);
            classToType.put(Long.class,JavaSimpleType.LONGOBJECT);
            classToType.put(long.class,JavaSimpleType.LONG);
            classToType.put(Object.class,JavaSimpleType.OBJECT);
            classToType.put(Short.class,JavaSimpleType.SHORTOBJECT);
            classToType.put(short.class,JavaSimpleType.SHORT);
            classToType.put(String.class,JavaSimpleType.STRING);
            classToType.put(List.class,JavaSimpleType.STRINGS);
            classToType.put(QName.class,JavaSimpleType.URI);
            classToType.put(URINamePair.class,JavaSimpleType.URI);

            classToType.put(com.sap.sdo.api.types.sdo.Object.class, JavaSimpleType.OBJECT);
            classToType.put(com.sap.sdo.api.types.sdo.Bytes.class, JavaSimpleType.BYTES);
            classToType.put(com.sap.sdo.api.types.sdo.Boolean.class, JavaSimpleType.BOOLEAN);
            classToType.put(com.sap.sdo.api.types.sdo.Byte.class, JavaSimpleType.BYTE);
            classToType.put(com.sap.sdo.api.types.sdo.Character.class, JavaSimpleType.CHARACTER);
            classToType.put(com.sap.sdo.api.types.sdo.YearMonthDay.class, JavaSimpleType.YEARMONTHDAY);
            classToType.put(com.sap.sdo.api.types.sdo.DateTime.class, JavaSimpleType.DATETIME);
            classToType.put(com.sap.sdo.api.types.sdo.Decimal.class, JavaSimpleType.DECIMAL);
            classToType.put(com.sap.sdo.api.types.sdo.Double.class, JavaSimpleType.DOUBLE);
            classToType.put(com.sap.sdo.api.types.sdo.Duration.class, JavaSimpleType.DURATION);
            classToType.put(com.sap.sdo.api.types.sdo.Float.class, JavaSimpleType.FLOAT);
            classToType.put(com.sap.sdo.api.types.sdo.Day.class, JavaSimpleType.DAY);
            classToType.put(com.sap.sdo.api.types.sdo.Month.class, JavaSimpleType.MONTH);
            classToType.put(com.sap.sdo.api.types.sdo.MonthDay.class, JavaSimpleType.MONTHDAY);
            classToType.put(com.sap.sdo.api.types.sdo.Year.class, JavaSimpleType.YEAR);
            classToType.put(com.sap.sdo.api.types.sdo.YearMonth.class, JavaSimpleType.YEARMONTH);
            classToType.put(com.sap.sdo.api.types.sdo.Id.class, JavaSimpleType.ID);
            classToType.put(com.sap.sdo.api.types.sdo.Int.class, JavaSimpleType.INT);
            classToType.put(com.sap.sdo.api.types.sdo.Integer.class, JavaSimpleType.INTEGER);
            classToType.put(com.sap.sdo.api.types.sdo.Long.class, JavaSimpleType.LONG);
            classToType.put(com.sap.sdo.api.types.sdo.Short.class, JavaSimpleType.SHORT);
            classToType.put(com.sap.sdo.api.types.sdo.Time.class, JavaSimpleType.TIME);
            classToType.put(com.sap.sdo.api.types.sdo.String.class, JavaSimpleType.STRING);
            classToType.put(com.sap.sdo.api.types.sdo.Uri.class, JavaSimpleType.URI);

            classToType.put(com.sap.sdo.api.types.sdo.java.BooleanObject.class, JavaSimpleType.BOOLEANOBJECT);
            classToType.put(com.sap.sdo.api.types.sdo.java.ByteObject.class, JavaSimpleType.BYTEOBJECT);
            classToType.put(com.sap.sdo.api.types.sdo.java.CharacterObject.class, JavaSimpleType.CHARACTEROBJECT);
            classToType.put(com.sap.sdo.api.types.sdo.java.DoubleObject.class, JavaSimpleType.DOUBLEOBJECT);
            classToType.put(com.sap.sdo.api.types.sdo.java.FloatObject.class, JavaSimpleType.FLOATOBJECT);
            classToType.put(com.sap.sdo.api.types.sdo.java.IntObject.class, JavaSimpleType.INTOBJECT);
            classToType.put(com.sap.sdo.api.types.sdo.java.LongObject.class, JavaSimpleType.LONGOBJECT);
            classToType.put(com.sap.sdo.api.types.sdo.java.ShortObject.class, JavaSimpleType.SHORTOBJECT);

            classToType.put(commonj.sdo.types.Object.class, JavaSimpleType.OBJECT);
            classToType.put(commonj.sdo.types.Bytes.class, JavaSimpleType.BYTES);
            classToType.put(commonj.sdo.types.Boolean.class, JavaSimpleType.BOOLEAN);
            classToType.put(commonj.sdo.types.Byte.class, JavaSimpleType.BYTE);
            classToType.put(commonj.sdo.types.YearMonthDay.class, JavaSimpleType.YEARMONTHDAY);
            classToType.put(commonj.sdo.types.DateTime.class, JavaSimpleType.DATETIME);
            classToType.put(commonj.sdo.types.Decimal.class, JavaSimpleType.DECIMAL);
            classToType.put(commonj.sdo.types.Double.class, JavaSimpleType.DOUBLE);
            classToType.put(commonj.sdo.types.Duration.class, JavaSimpleType.DURATION);
            classToType.put(commonj.sdo.types.Float.class, JavaSimpleType.FLOAT);
            classToType.put(commonj.sdo.types.Day.class, JavaSimpleType.DAY);
            classToType.put(commonj.sdo.types.Month.class, JavaSimpleType.MONTH);
            classToType.put(commonj.sdo.types.MonthDay.class, JavaSimpleType.MONTHDAY);
            classToType.put(commonj.sdo.types.Year.class, JavaSimpleType.YEAR);
            classToType.put(commonj.sdo.types.YearMonth.class, JavaSimpleType.YEARMONTH);
            classToType.put(commonj.sdo.types.ID.class, JavaSimpleType.ID);
            classToType.put(commonj.sdo.types.Int.class, JavaSimpleType.INT);
            classToType.put(commonj.sdo.types.Integer.class, JavaSimpleType.INTEGER);
            classToType.put(commonj.sdo.types.Long.class, JavaSimpleType.LONG);
            classToType.put(commonj.sdo.types.Short.class, JavaSimpleType.SHORT);
            classToType.put(commonj.sdo.types.Time.class, JavaSimpleType.TIME);
            classToType.put(commonj.sdo.types.String.class, JavaSimpleType.STRING);
            classToType.put(commonj.sdo.types.URI.class, JavaSimpleType.URI);

            classToType.put(DataGraph.class, DataGraphType.getInstance());
            classToType.put(ChangeSummary.class, ChangeSummaryType.getInstance());

            classToType.put(javax.security.auth.Subject.class, JavaSimpleType.OBJECT);
        }

        private void createCommonjSdoXmlNamespace() {
            Namespace namespace = super.getUriToNamespace().get(URINamePair.DATATYPE_XML_URI);

            //Global properties on SDO Type and Property
            final DataFactory dataFactory = super._helperContext.getDataFactory();
            SdoProperty xmlElementProperty = new MetaDataPropertyLogicFacade(new MetaDataProperty(
                PropertyType.XML_ELEMENT, JavaSimpleType.BOOLEAN, namespace, false, false, false, false));
            namespace.putBySdoName(xmlElementProperty.getName(), xmlElementProperty);
            SdoProperty xmlNameProperty = new MetaDataPropertyLogicFacade(new MetaDataProperty(
                PropertyType.XML_NAME, JavaSimpleType.STRING, namespace, false, false, false, false), getNameCache());
            namespace.putBySdoName(xmlNameProperty.getName(), xmlNameProperty);
            namespace.put(xmlElementProperty);
            namespace.put(xmlNameProperty);
            SdoProperty uriProperty = new MetaDataPropertyLogicFacade(new MetaDataProperty(
                PropertyType.URI, JavaSimpleType.URI, namespace, false, false, false, false), getUriCache());
            namespace.put(uriProperty);
            SdoProperty xsdTypeProperty = new MetaDataPropertyLogicFacade(new MetaDataProperty(
                PropertyType.XSD_TYPE, JavaSimpleType.URI, namespace, false, false, false, false));
            namespace.put(xsdTypeProperty);
            SdoProperty formQualifiedProperty = new MetaDataPropertyLogicFacade(new MetaDataProperty(
                PropertyType.FORM_QUALIFIED, JavaSimpleType.BOOLEANOBJECT, namespace, false, false, false, false));
            namespace.put(formQualifiedProperty);
            SdoProperty orphanHolderProperty = new MetaDataPropertyLogicFacade(new MetaDataProperty(
                URINamePair.PROP_XML_ORPHAN_HOLDER.getName(), JavaSimpleType.BOOLEANOBJECT, namespace, false, false, false, false));
            namespace.put(orphanHolderProperty);
            SdoProperty substitutesProperty = new MetaDataPropertyLogicFacade(new MetaDataProperty(
                PropertyType.SUBSTITUTES, null, JavaSimpleType.URI, namespace, false, true, true, true, false, URINamePair.SCHEMA_Q_NAME, null));
            namespace.put(substitutesProperty);
            SdoProperty refProperty = new MetaDataPropertyLogicFacade(new MetaDataProperty(
                PropertyType.REF, null, JavaSimpleType.URI, namespace, false, false, false, false, false, URINamePair.SCHEMA_Q_NAME, null));
            namespace.put(refProperty);
            DataObject prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, PropertyType.XML_SIMPLE_CONTENT);
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEAN);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, TypeType.ATTRIBUTE_FORM_DEFAULT);
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEANOBJECT);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, TypeType.ELEMENT_FORM_DEFAULT);
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEANOBJECT);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, TypeType.MIXED);
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEAN);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);

            // Annotation on Schema items
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_XML_NAME.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.STRING);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            ((PropertyLogic)prop).setValueCache(getNameCache());
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_XML_PROPERTY_TYPE.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.URI);
            prop.set(xsdTypeProperty, URINamePair.SCHEMA_Q_NAME.toStandardSdoFormat());
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_XML_OPPOSITE_PROPERTY.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.STRING);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            ((PropertyLogic)prop).setValueCache(getNameCache());
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_XML_SEQUENCE.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEANOBJECT);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_XML_STRING.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEANOBJECT);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_XML_DATA_TYPE.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.URI);
            prop.set(xsdTypeProperty, URINamePair.SCHEMA_Q_NAME.toStandardSdoFormat());
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_XML_ALIAS_NAME.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.STRINGS);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_XML_READ_ONLY.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEANOBJECT);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_XML_MANY.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEANOBJECT);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.setString(PropertyType.NAME, URINamePair.PROP_XML_KEY.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEANOBJECT);
            prop.setBoolean(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.setString(PropertyType.NAME, URINamePair.PROP_XML_EMBEDDED_KEY.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEANOBJECT);
            prop.setBoolean(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.setString(PropertyType.NAME, URINamePair.PROP_XML_KEY_TYPE.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.URI);
            prop.setString(xsdTypeProperty, URINamePair.SCHEMA_Q_NAME.toStandardSdoFormat());
            prop.setBoolean(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
        }

        private void createCommonjSdoNamespace() {
            Namespace namespace = super.getUriToNamespace().get(URINamePair.DATATYPE_URI);

            final DataFactory dataFactory = super._helperContext.getDataFactory();
            Property xmlElementProperty = getOpenContentProperty(URINamePair.DATATYPE_XML_URI, PropertyType.XML_ELEMENT);

            DataObject prop = dataFactory.create(PropertyType.getInstance());
            prop = dataFactory.create(PropertyType.getInstance());
            prop.setString(PropertyType.NAME, URINamePair.PROP_SDO_TEXT.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.STRING);
            prop.setBoolean(PropertyType.CONTAINMENT, false);
            prop.setBoolean(PropertyType.MANY, true);
            prop.setBoolean(xmlElementProperty, true);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.setString(PropertyType.NAME, URINamePair.PROP_SDO_DATAGRAPH.getName());
            prop.set(PropertyType.TYPE, DataGraphType.getInstance());
            prop.setBoolean(PropertyType.CONTAINMENT, true);
            prop.setBoolean(PropertyType.MANY, true);
            prop.setBoolean(xmlElementProperty, true);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.setString(PropertyType.NAME, URINamePair.PROP_SDO_DATA_OBJECT.getName());
            prop.set(PropertyType.TYPE, DataObjectType.getInstance());
            prop.setBoolean(PropertyType.CONTAINMENT, true);
            prop.setBoolean(PropertyType.MANY, true);
            prop.setBoolean(xmlElementProperty, true);
            defineOpenContentProperty(prop, namespace);
        }

        private void createCommonjSdoJavaNamespace() {
            Namespace namespace = super.getUriToNamespace().get(URINamePair.DATATYPE_JAVA_URI);

            final DataFactory dataFactory = super._helperContext.getDataFactory();
            Property xmlElementProperty = getOpenContentProperty(URINamePair.DATATYPE_XML_URI, PropertyType.XML_ELEMENT);

            DataObject prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, TypeConstants.JAVA_CLASS);
            prop.set(PropertyType.TYPE, JavaSimpleType.STRING);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            //class names are always interned Strings
            ((PropertyLogic)prop).setValueCache(StringInternCache.getInstance());
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, PropertyType.JAVA_NAME);
            prop.set(PropertyType.TYPE, JavaSimpleType.STRING);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, PropertyType.IS_FIELD);
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEAN);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);

            // Annotation on Schema items
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_JAVA_PACKAGE.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.STRING);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_JAVA_INSTANCE_CLASS.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.STRING);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            //class names are always interned Strings
            ((PropertyLogic)prop).setValueCache(StringInternCache.getInstance());
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_JAVA_EXTENDED_INSTANCE_CLASS.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.STRING);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            //class names are always interned Strings
            ((PropertyLogic)prop).setValueCache(StringInternCache.getInstance());
            defineOpenContentProperty(prop, namespace);
            prop = dataFactory.create(PropertyType.getInstance());
            prop.set(PropertyType.NAME, URINamePair.PROP_JAVA_NESTED_INTERFACES.getName());
            prop.set(PropertyType.TYPE, JavaSimpleType.BOOLEANOBJECT);
            prop.set(PropertyType.MANY, false);
            prop.set(xmlElementProperty, false);
            defineOpenContentProperty(prop, namespace);
        }

        private void createSchemaNamespace() {
            // these 3 classes reference all other complex types
            getType(Schema.class);
            getType(NarrowMaxMin.class);
            getType(SimpleExplicitGroup.class);
        }

        private void createCtxNamespace() {
            Namespace namespace = super.getUriToNamespace().get(URINamePair.CTX_URI);
            final DataFactory dataFactory = super._helperContext.getDataFactory();

            DataObject prop = dataFactory.create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
            prop.set(PropertyType.NAME, URINamePair.PROP_CTX_SCHEMA_REFERENCE.getName());
            prop.set(PropertyType.TYPE, getType(Annotated.class));
            prop.set(PropertyType.CONTAINMENT, false);
            prop.set(PropertyType.MANY, false);
            prop.set(PropertyType.getXmlElementProperty(), false);
            defineOpenContentProperty(prop, namespace);
            getType(HelperContexts.class);
        }

        private void initBuiltInTypes() {
            initBuiltInType(JavaSimpleType.BOOLEAN);
            initBuiltInType(JavaSimpleType.BOOLEANOBJECT);
            initBuiltInType(JavaSimpleType.BYTE);
            initBuiltInType(JavaSimpleType.BYTEOBJECT);
            initBuiltInType(JavaSimpleType.BYTES);
            initBuiltInType(JavaSimpleType.CHARACTER);
            initBuiltInType(JavaSimpleType.CHARACTEROBJECT);
            initBuiltInType(JavaSimpleType.DATE);
            initBuiltInType(JavaSimpleType.DATETIME);
            initBuiltInType(JavaSimpleType.DAY);
            initBuiltInType(JavaSimpleType.DECIMAL);
            initBuiltInType(JavaSimpleType.DURATION);
            initBuiltInType(JavaSimpleType.DOUBLE);
            initBuiltInType(JavaSimpleType.DOUBLEOBJECT);
            initBuiltInType(JavaSimpleType.FLOAT);
            initBuiltInType(JavaSimpleType.FLOATOBJECT);
            initBuiltInType(JavaSimpleType.INT);
            initBuiltInType(JavaSimpleType.INTOBJECT);
            initBuiltInType(JavaSimpleType.INTEGER);
            initBuiltInType(JavaSimpleType.LONG);
            initBuiltInType(JavaSimpleType.LONGOBJECT);
            initBuiltInType(JavaSimpleType.MONTH);
            initBuiltInType(JavaSimpleType.MONTHDAY);
            initBuiltInType(JavaSimpleType.OBJECT);
            initBuiltInType(JavaSimpleType.SHORT);
            initBuiltInType(JavaSimpleType.SHORTOBJECT);
            initBuiltInType(JavaSimpleType.STRING);
            initBuiltInType(JavaSimpleType.STRINGS);
            initBuiltInType(JavaSimpleType.TIME);
            initBuiltInType(JavaSimpleType.URI);
            initBuiltInType(JavaSimpleType.YEAR);
            initBuiltInType(JavaSimpleType.YEARMONTH);
            initBuiltInType(JavaSimpleType.YEARMONTHDAY);
            initBuiltInType(JavaSimpleType.CLASS);

            initBuiltInType(JavaSimpleType.ID);

            initBuiltInType(TypeType.getInstance());
            initBuiltInType(PropertyType.getInstance());
            initBuiltInType(ChangeSummaryType.getInstance());
            initBuiltInType(DataGraphType.getInstance());
            initBuiltInType(DataGraphType.XsdType.getInstance());
            initBuiltInType(DataObjectType.getInstance());
            initBuiltInType(OpenType.getInstance());
            initBuiltInType(UndecidedType.getInstance());

            initBuiltInType(TypeType.getFacetsType());
        }

        private void initBuiltInType(SdoType pType) {
            Namespace namespace = super.getUriToNamespace().get(pType.getQName().getURI());
            final String name = pType.getName();
            namespace.putBySdoName(name, pType);
            namespace.putByXmlName(name, pType);
        }

        private void initSchemaBuiltInTypes() {
            Namespace namespace = super.getUriToNamespace().get(URINamePair.SCHEMA_URI);
            Map<URINamePair, URINamePair> xsdToSdoNames = SchemaTypeFactory.getInstance().getXsdToSdoNames();
            for (Entry<URINamePair, URINamePair> xsdToSdoName: xsdToSdoNames.entrySet()) {
                URINamePair sdoUnp = xsdToSdoName.getValue();
                SdoType type = (SdoType)super.getType(sdoUnp.getURI(), sdoUnp.getName());
                namespace.putByXmlName(xsdToSdoName.getKey().getName(), type);
            }
        }

        private void parseSchemas() {
            try {
                XSDHelper xsdHelper = super._helperContext.getXSDHelper();
                URL xmlNamespaceUrl = getClass().getClassLoader().getResource(XML_NAMESPACE);
                xsdHelper.define(xmlNamespaceUrl.openStream(), xmlNamespaceUrl.toString());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        /**
         * Do not create a namespace, just get it.
         * This method is overridden to keep control about the created namespaces.
         * In the {@link TypeHelperCore} the namespaces are only allowed to be
         * created by {@link #createNamespaces()}.
         */
        @Override
        protected Namespace getOrCreateNamespace(String pUri) {
            Namespace namespace = super.getUriToNamespace().get(pUri);
            if (namespace == null) {
                throw new IllegalArgumentException("Namespace " + pUri + " is not allowed in Core-HelperContext");
            }
            return namespace;
        }

        @Override
        public void removeTypesAndProperties(Set<Type> pTypes, Set<Property> pProperties) {
            throw new UnsupportedOperationException("It is not allowed to remove meta data from the core context");
        }

    }

}
