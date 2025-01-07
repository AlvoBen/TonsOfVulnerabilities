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
package com.sap.sdo.impl.types.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.sap.sdo.api.Bool;
import com.sap.sdo.api.SchemaInfo;
import com.sap.sdo.api.SdoFacets;
import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.SdoTypeMetaData;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeLogicFacade;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.simple.ListSimpleType;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * provides types based on schemas
 *
 */
public class JavaTypeFactory
{
    private final SapHelperContext _helperContext;
    
    private PropComparator _propComparator = new PropComparator();

    private static SdoPropertyMetaData _defaults;
    
    static {
        try {
            _defaults =
                DefaultAnnotated.class.getDeclaredMethods()[0].getAnnotation(
                    SdoPropertyMetaData.class);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public JavaTypeFactory(SapHelperContext pHelperContext) {
        _helperContext = pHelperContext;
    }
    
    // TODO find right class loader by naming scheme
    public Class getClassByQName(URINamePair unp, Class container, String containerUri) {
        String packageName;
        if (container != null) {
            packageName = container.getPackage().getName();
            if (unp.getURI().startsWith(containerUri)) {
                packageName = packageName+unp.getURI().substring(containerUri.length()).replace('/','.');
            } else {
                // TODO: try getting relative path.
            }
        } else {
            packageName = unp.getURI();
        }
        StringBuilder buf = new StringBuilder();
        buf.append(packageName);
        if (packageName.length()>0) {
            buf.append(".");
        }
        Boolean mixedCase =
            (Boolean)_helperContext.getContextOption(
                SapHelperContext.OPTION_KEY_MIXED_CASE_JAVA_NAMES);
        if (mixedCase == null || mixedCase) {
            buf.append(NameConverter.CONVERTER.toClassName(unp.getName()));
        } else {
            String typeName = unp.getName();
            buf.append(Character.toUpperCase(typeName.charAt(0)) + typeName.substring(1));
        }
        String className = buf.toString();
        try {
            return SapHelperProviderImpl.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            if (container != null ) {
                try {
                    return container.getClassLoader().loadClass(className);
                } catch (ClassNotFoundException e1) {
                    IllegalArgumentException iae = new IllegalArgumentException("cannot provide JAVA based type");
                    iae.initCause(e1);
                    throw iae;
                }
            } else {
                IllegalArgumentException iae = new IllegalArgumentException("cannot provide JAVA based type");
                iae.initCause(e);
                throw iae;
            }
        }
    }
    
    private URINamePair getQNameForClass(Class<?> pClass) {
        SdoType type =((TypeHelperImpl)_helperContext.getTypeHelper()).getResolvedType(pClass);
        if (type != null) {
            return type.getQName();
        }
        return getQNameFromClass(pClass);
    }
    
    public URINamePair getQNameFromClass(Class<?> pClass) {
        //if (pClass.isInterface()) {
            int p = pClass.getName().lastIndexOf('.');
            String pn;
            String cn;
            if (p < 0) {
                pn = "";
                cn = pClass.getName();
            } else {
                pn = pClass.getName().substring(0,p);
                cn = pClass.getName().substring(p+1);
            }
            String uri= pn;
            SdoTypeMetaData metaData = pClass.getAnnotation(SdoTypeMetaData.class);
            if (metaData != null) {
                if (metaData.uri().length()>0) {
                    uri = metaData.uri();
                } else if (metaData.noNamespace()){
                    uri = "";
                }
                if (metaData.sdoName().length() > 0) {
                    cn = metaData.sdoName();
                }                
            }
            return new URINamePair(uri,cn);
//        } else {
//            return null;
//        }
    }

    /**
     * @author D042774
     *
     */
    private static final class PropComparator implements Comparator<Property> {
        public int compare(final Property p1, final Property p2) {
            // properties in this list and its names are null-safe
            return p1.getName().compareTo(p2.getName());
        }
    }

    public static class ReflectionResult {
        public ReflectionResult(Type pRoot, Map<Class, Type> pTypes, Map<String, String> pSchemas, List<DataObject> pOpenContentProps) {
            root = pRoot;
            types = pTypes;
            schemas = pSchemas;
            openContentProps = pOpenContentProps;
        }
        public Type root;
        public Map<Class, Type> types;
        public Map<String, String> schemas;
        public List<DataObject> openContentProps;
    }
    public ReflectionResult createTypeFromClass(URINamePair unp, Class<?> clz) {
        return createDataObjectTypeFromClass(unp, clz);
    }
    private Type getOrCreateDataObjectType(Class jclz, Context context) {
        URINamePair qname = getQNameForClass(jclz);
        if (qname == null) {
            throw new IllegalArgumentException("Cannot define dataobject:  "+jclz.getName()+" is not an interface");
        }
        Type ret = _helperContext.getTypeHelper().getType(qname.getURI(), qname.getName());
        if (ret != null) {
            if (ret.isDataType()) {
                return ret;
            }
            final Class instanceClass = ret.getInstanceClass();
            if (instanceClass == jclz) {
                return ret;
            }
            if (instanceClass != null) {
                throw new IllegalArgumentException("Attempt to reset instance class of {"+ret.getURI()+"}"+ret.getName()+
                        " from "+instanceClass.getName()+" to "+jclz.getName());
            }
        }
        ret = context.classToModelType.get(jclz);
        if (ret == null) {
            ret = (Type)_helperContext.getDataFactory().create(TypeType.getInstance());
            context.classToModelType.put(jclz, ret);
        }
        return ret;
    }
    private ReflectionResult createDataObjectTypeFromClass(URINamePair unp, Class jclz) {
        final Context context = new Context();
        Type root = generateDataObjectTypeFromClass(unp, jclz, context);
        for (Class referencedClass = getFirstUndefinedClass(context.classToModelType);
             referencedClass!=null;
             referencedClass = getFirstUndefinedClass(context.classToModelType)) {
            URINamePair referencedUnp = getQNameForClass(referencedClass);
            if (referencedUnp == null) {
                throw new IllegalArgumentException("Cannot define dataobject:  "+referencedClass.getName()+" is not an interface");
            }
            generateDataObjectTypeFromClass(referencedUnp, referencedClass, context);
        }
        return new ReflectionResult(root, context.classToModelType, context.schemas, context.openContentProps);  
    }
    private Class getFirstUndefinedClass(Map<Class,Type> classToModelType) {
        for (Map.Entry<Class,Type> entry: classToModelType.entrySet()) {
            if (entry.getValue().getName() == null) {
                return entry.getKey();
            }
        }
        return null;
    }
    private class Context {
        Map<Class,Type> classToModelType = new HashMap<Class,Type>();
        Map<String, String> schemas = new HashMap<String, String>();
        List<DataObject> openContentProps = new ArrayList<DataObject>();

    }
    private Type generateDataObjectTypeFromClass(URINamePair unp, Class<?> jclz, Context context) {
        SchemaInfo schemaMetaData = jclz.getAnnotation(SchemaInfo.class);
        try {
            String schemaUrl = null;
            if (schemaMetaData != null) {
                String schemaLocation = schemaMetaData.schemaLocation();
                URI schemaLocationUri = new URI(schemaLocation);
                if (schemaLocation.length()>0 && !schemaLocationUri.isAbsolute()) {
                    if (schemaLocation.charAt(0) == '/') {
                        schemaUrl = schemaLocation.substring(1);
                    } else {
                        URI packageUri = new URI(jclz.getPackage().getName().replace('.', '/') + '/');
                        URI resolvedUri = packageUri.resolve(schemaLocationUri);
                        schemaUrl = resolvedUri.toString();
                    }
                } else {
                    schemaUrl = schemaLocation;
                }
            }
            if (!context.schemas.containsKey(unp.getURI()) || (schemaUrl != null)) {
                context.schemas.put(unp.getURI(), schemaUrl);                
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        SdoTypeMetaData metaData = jclz.getAnnotation(SdoTypeMetaData.class);
        DataObject typeAsDO = (DataObject)getOrCreateDataObjectType(jclz, context); 
        String uri = unp.getURI();
        typeAsDO.set(TypeType.URI,uri);
        typeAsDO.set(TypeType.NAME,unp.getName());

        if (metaData != null) {
            typeAsDO.set(TypeType.OPEN,metaData.open());
            typeAsDO.set(TypeType.SEQUENCED,metaData.sequenced());
            typeAsDO.set(TypeType.ABSTRACT,metaData.abstractDataObject());
            typeAsDO.set(TypeType.getAttributeFormDefaultProperty(),metaData.attributeFormDefault());
            typeAsDO.set(TypeType.getElementFormDefaultProperty(),metaData.elementFormDefault());
            typeAsDO.set(TypeType.getMixedProperty(),metaData.mixed());
        } else {
            typeAsDO.set(TypeType.OPEN,false);
            typeAsDO.set(TypeType.SEQUENCED,false); 
            typeAsDO.set(TypeType.ABSTRACT,false);
        }
        
        // build set of all methods. We keep removing upon successful identification of a prop.
        // set should be empty at the end.
        Method[] declmeths = jclz.getDeclaredMethods();

        // look for base types
        Class sc = jclz.getSuperclass();
        Class[] is = jclz.getInterfaces();
        java.lang.reflect.Type[] genericIs = jclz.getGenericInterfaces();
        List<Type> bt = new ArrayList<Type>(is.length+1);
        Type baseType = null;
        if (sc!=null && sc!=Object.class) {
            baseType = getOrCreateDataObjectType(sc, context);
            bt.add(baseType);
        }
        for (int i = 0; i < is.length; i++) {
            java.lang.reflect.Type interf = genericIs[i];
            if ((interf instanceof ParameterizedType) && List.class.isAssignableFrom(is[i])) {
                java.lang.reflect.Type parameter = ((ParameterizedType)interf).getActualTypeArguments()[0];
                Class parameterClass = (Class)parameter; //TODO could be another Type instance
                SdoType innerType = (SdoType)getOrCreateDataObjectType(parameterClass, context);
                baseType = new ListSimpleType(innerType, _helperContext);
            } else {
                baseType = getOrCreateDataObjectType(is[i], context);
            }
            bt.add(baseType);
        }
        typeAsDO.set(TypeType.BASE_TYPE,bt);
        if (baseType != null && baseType.isDataType()) {
            typeAsDO.set(TypeType.DATA_TYPE,true);
        } else {
            typeAsDO.set(TypeType.DATA_TYPE,false);
            typeAsDO.set(TypeType.getJavaClassProperty(), jclz.getName());
            ((TypeLogicFacade)typeAsDO).setInstanceClass(jclz); //TODO use this on SdoType
        }
        // introspect
        List<Property> allprops = new ArrayList<Property>(declmeths.length);
        Type type = (Type)typeAsDO;
        for (int i = 0; i < declmeths.length; i++) {
            Property p = checkForProperty(jclz,declmeths[i],type, context);
            if (p!=null) {
                allprops.add(p);
            }
        }
        // build set of all methods. We keep removing upon successful identification of a prop.
        // set should be empty at the end.
        Field[] declFields = jclz.getDeclaredFields();
        for (int i = 0; i < declFields.length; i++) {
            Property p = checkForProperty(jclz,declFields[i],type, context);
            if (p!=null) {
                allprops.add(p);
            }
        }
        Collections.sort(allprops, _propComparator);
        typeAsDO.set(TypeType.PROPERTY,allprops);
        // Cannot use reflection here: all interfaces are abstract
        //typeAsDO.set(TypeType.ABSTRACT, Modifier.isAbstract(jclz.getModifiers()));
        
        processFacets(jclz, typeAsDO);

        if (metaData != null && metaData.openContentProperties().length > 0) {
            for (int i=0; i<metaData.openContentProperties().length; i++) {
                DataObject ocProp = _helperContext.getDataFactory().create(PropertyType.getInstance());
                ocProp.set(PropertyType.NAME, metaData.openContentProperties()[i].name());
                ocProp.set(PropertyType.TYPE, typeAsDO);
                ocProp.set(PropertyType.MANY, metaData.openContentProperties()[i].many());
                ocProp.set(PropertyType.CONTAINMENT, metaData.openContentProperties()[i].containment());
                context.openContentProps.add(ocProp);
            }
        }
        if (!type.isOpen() && !type.isDataType() && 
                typeAsDO.getList(TypeType.PROPERTY).isEmpty() && typeAsDO.getList(TypeType.BASE_TYPE).isEmpty()
                && (jclz.getDeclaredMethods().length > 0 || jclz.getDeclaredFields().length > 0)) {
                //If this type has no properties, the class was not in a bean style
                throw new NoBeanException("Can not create a type for class " + jclz.getName());
            }
            return type;
    }
    private void processFacets(Class<?> jclz, DataObject typeAsDO) {
        SdoFacets facetAnnotation = jclz.getAnnotation(SdoFacets.class);
        if (facetAnnotation != null) {
            DataObject facet = _helperContext.getDataFactory().create(TypeType.getInstance().getFacetsType());
            if (facetAnnotation.minLength() >= 0) {
                facet.set(TypeType.FACET_MINLENGTH, facetAnnotation.minLength());
            }
            if (facetAnnotation.maxLength() >= 0) {
                facet.set(TypeType.FACET_MAXLENGTH, facetAnnotation.maxLength());
            }
            if (facetAnnotation.length() >= 0) {
                facet.set(TypeType.FACET_LENGTH, facetAnnotation.length());
            }
            if (facetAnnotation.enumeration().length > 0) {
                facet.setList(TypeType.FACET_ENUMERATION, Arrays.asList(facetAnnotation.enumeration()));
            }
            if (facetAnnotation.minExclusive() > Integer.MIN_VALUE) {
                facet.set(TypeType.FACET_MINEXCLUSIVE, facetAnnotation.minExclusive());
            }
            if (facetAnnotation.minInclusive() > Integer.MIN_VALUE) {
                facet.set(TypeType.FACET_MININCLUSIVE, facetAnnotation.minInclusive());
            }
            if (facetAnnotation.maxExclusive() < Integer.MAX_VALUE) {
                facet.set(TypeType.FACET_MAXEXCLUSIVE, facetAnnotation.maxExclusive());
            }
            if (facetAnnotation.maxInclusive() < Integer.MAX_VALUE) {
                facet.set(TypeType.FACET_MAXINCLUSIVE, facetAnnotation.maxInclusive());
            }
            if (facetAnnotation.pattern().length > 0) {
                facet.setList(TypeType.FACET_PATTERN, Arrays.asList(facetAnnotation.pattern()));
            }
            if (facetAnnotation.totalDigits() >= 0) {
                facet.set(TypeType.FACET_TOTALDIGITS, facetAnnotation.totalDigits());
            }
            if (facetAnnotation.fractionDigits() >= 0) {
                facet.set(TypeType.FACET_FRACTIONDIGITS, facetAnnotation.fractionDigits());
            }
            typeAsDO.set(TypeType.getInstance().getFacetsProperty(), facet);            
        }
    }    

    //
    // helper
    //
    private static boolean isPublic(Method m) {
        return ((m.getModifiers() & Modifier.PUBLIC)!=0) &&
               ((m.getModifiers() & Modifier.STATIC)==0);
    }
    private Property checkForProperty(Class clz, Field f, Type containment, Context context) {
        SdoPropertyMetaData metaData = f.getAnnotation(SdoPropertyMetaData.class);
        if (metaData == null) {
            return null;
        }
        DataObject propAsDO = createPropertyObject(clz, containment, context, f.getName(), f.getType(), metaData, f.getGenericType());
        propAsDO.set(PropertyType.NAME,metaData.sdoName());
        propAsDO.set(PropertyType.getJavaNameProperty(), f.getName());
        propAsDO.set(PropertyType.getJavaFieldProperty(), true);
        return (Property)propAsDO;
    }
    private Property checkForProperty(Class clz, Method m, Type containment, Context context) {
        boolean bool=(m.getName().startsWith("is")) && (m.getName().length()>2);
        boolean nonbool = (!bool) && (m.getName().startsWith("get")) && (m.getName().length()>3);
        if (nonbool && m.getAnnotation(Deprecated.class) != null) {
            // skip
            return null;
        }
        if ((bool || nonbool) && (isPublic(m))
                && (m.getParameterTypes().length == 0)) {
            // looks like a getter
            Class rt = m.getReturnType();
            if (rt.equals(void.class)) {
                return null;
            }
            if ((bool) && (!rt.equals(boolean.class))) {
                throw new IllegalArgumentException("found method "+m.getName()+" with unexpected return type "+rt.getName());
            }
            StringBuilder pn = new StringBuilder(m.getName().length());
            if (bool) {
                pn.append(Character.toLowerCase(m.getName().charAt(2)));
                pn.append(m.getName().substring(3));
            } else {
                pn.append(Character.toLowerCase(m.getName().charAt(3)));
                pn.append(m.getName().substring(4));
            }
            logger.fine("found property "+pn);
            SdoPropertyMetaData metaData = m.getAnnotation(SdoPropertyMetaData.class);
            if (metaData == null) {
                metaData = _defaults;
            }
            java.lang.reflect.Type genericReturnType = m.getGenericReturnType();
            final String nameFromMethod = pn.toString();
            DataObject propAsDO = createPropertyObject(clz, containment, context, nameFromMethod, rt, metaData, genericReturnType);
            
            // check for setter.
            StringBuilder sn = new StringBuilder(m.getName().length());
            sn.append("set").append(Character.toUpperCase(pn.charAt(0))).append(pn.toString().substring(1));
            try {
                Method s = clz.getMethod(sn.toString(),new Class[]{m.getReturnType()});
                if (!isPublic(s)) {
                    throw new IllegalArgumentException("found setter "+s+" - but it is not public!");
                }
                propAsDO.set(PropertyType.READ_ONLY,false);
            } catch (NoSuchMethodException e) { //$JL-EXC$
                logger.fine("no setter "+sn+" found");
                propAsDO.set(PropertyType.READ_ONLY,true);
            }

            return (Property)propAsDO;

        }
        return null;
    }
    
    private DataObject createPropertyObject(Class clz, Type containment, Context context, String nameFromMethod, Class rt, SdoPropertyMetaData metaData, java.lang.reflect.Type genericReturnType) {

        DataObject propAsDO = _helperContext.getDataFactory().create(PropertyType.getInstance());

        boolean xmlElement = metaData.xmlInfo().xmlElement();
        boolean isList = rt.equals(List.class);
        Class listItemClass = null;
        boolean many = isList && xmlElement;
        if (isList) {
            listItemClass = getTypeParameter(genericReturnType);
            if (many) {
                if (listItemClass == null) {
                    // That means, it's the simple type "List"
                    many = false;
                    listItemClass = Object.class;
                } else {
                    rt = listItemClass;
                    listItemClass = null;
                }
            } else if (listItemClass == null) {
                listItemClass = Object.class;
            }
        }

        Type type;
        if (metaData.sdoType()!=null && metaData.sdoType().length()>0) {
            logger.fine("using metadata to determine type");
            URINamePair unp = URINamePair.fromStandardSdoFormat(metaData.sdoType());
            type = _helperContext.getTypeHelper().getType(unp.getURI(), unp.getName());
            if (type == null) {
                String uri = containment.getURI();
                if (uri == null) {
                    uri = "";
                }
                Class typeClass = getClassByQName(unp, clz, uri);
                if (typeClass != null) {
                    type = getOrCreateDataObjectType(typeClass, context);
                } else {
                    throw new IllegalArgumentException("Java Model not complete: "+unp+" could not be found");
                }
            }
        } else if (ChangeSummary.class.equals(rt)) {
            logger.fine("it's a changeSummary");
            type = _helperContext.getTypeHelper().getType(URINamePair.CHANGESUMMARY_TYPE.getURI(),
                                               URINamePair.CHANGESUMMARY_TYPE.getName());
        } else if (DataObject.class.equals(rt)) {
            logger.fine("it's an abstract type");
            type = _helperContext.getTypeHelper().getType(URINamePair.DATAOBJECT.getURI(),
                                               URINamePair.DATAOBJECT.getName());
        } else {
            logger.fine("determined return type "+rt);
            type = getOrCreateDataObjectType(rt,context);
            if (listItemClass != null) {
                // if it is a ListSimpleType, check if it fits with the return value
                if (!listItemClass.equals(((ListSimpleType)type).getItemType().getInstanceClass())) {
                    type = new ListSimpleType((SdoType)getOrCreateDataObjectType(listItemClass, context), _helperContext);
                }
            }
        }
        
        //Note that type can be hollow with all properties unset at this time!
        propAsDO.set(PropertyType.TYPE,type);
        final Class instanceClass = type.getInstanceClass();
        if (many) {
            //TODO threre could be cases where the property in an element and inheritedInstanceClass is unknown
            if (instanceClass == List.class) {
                many = false;
            }
        }
        propAsDO.set(PropertyType.MANY,many);
        
        // check for instance class differente from return type
        if (!many && instanceClass != null
                && !instanceClass.equals(rt)) {
            propAsDO.set(PropertyType.JAVA_CLASS, rt);
        }

        if (metaData.sdoName()!=null && metaData.sdoName().length()>0) {
            propAsDO.set(PropertyType.NAME,metaData.sdoName());
            propAsDO.set(PropertyType.getJavaNameProperty(), nameFromMethod);
        } else {
            propAsDO.set(PropertyType.NAME,nameFromMethod);             
        }
        if (metaData.defaultValue()!=null && metaData.defaultValue().length()>0) {
            propAsDO.set(PropertyType.DEFAULT, metaData.defaultValue());
        }
        if (metaData.opposite()!=null && metaData.opposite().length()>0) {
            propAsDO.set(PropertyType.OPPOSITE_INTERNAL,metaData.opposite());
        }
        propAsDO.set(PropertyType.getXmlElementProperty(), xmlElement);
        if (metaData.xmlInfo().xsdType().length() > 0) {
            propAsDO.set(PropertyType.getXsdTypeProperty(), metaData.xmlInfo().xsdType());
        }
        if (metaData.xmlInfo().xsdName().length() > 0) {
            propAsDO.set(PropertyType.getXmlNameProperty(), metaData.xmlInfo().xsdName());
        }
        String ref = metaData.xmlInfo().ref();
        if (ref.length() > 0) {
            propAsDO.set(PropertyType.getReferenceProperty(), ref);
            propAsDO.set(PropertyType.getUriProperty(), ref);
        }
        if (metaData.nullable()!=Bool.UNSET) {
            propAsDO.set(PropertyType.NULLABLE, metaData.nullable()==Bool.TRUE);
        }
        propAsDO.set(PropertyType.CONTAINMENT, metaData.containment());
        if (metaData.propertyIndex() >= 0) {
            ((SdoProperty)propAsDO).setRequestedIndex(metaData.propertyIndex());
        }
        propAsDO.setList(PropertyType.ALIAS_NAME, Arrays.asList(metaData.aliasNames()));
        if (!propAsDO.isSet(PropertyType.getUriProperty())) {
            String uri;
            if ((xmlElement && Boolean.TRUE == containment.get(TypeType.getElementFormDefaultProperty()))
                || (!xmlElement && Boolean.TRUE == containment.get(TypeType.getAttributeFormDefaultProperty()))) {
                uri = ((SdoType)containment).getXmlUri();
            } else {
                uri = "";
            }
            propAsDO.setString(PropertyType.getUriProperty(), uri);
        }
        return propAsDO;
    }

    public static Class getTypeParameter(java.lang.reflect.Type t) {
        if (t instanceof ParameterizedType) {
            java.lang.reflect.Type arg = ((ParameterizedType)t).getActualTypeArguments()[0];
            if (arg instanceof Class) {
                return (Class)arg;
            }
        }
        return null;
    }   
    private final static Logger logger = Logger.getLogger(JavaTypeFactory.class.getName());

    public void enrichType(Map<Class, Type> pClassToType) {
        int propTypeOpenOffset = PropertyType.getInstance().getDeclaredProperties().size();
        Set<Type> types = new HashSet(pClassToType.values());
        for (Type javaType: types) {
            Type oldType = _helperContext.getTypeHelper().getType(javaType.getURI(), javaType.getName());
            if (oldType == null) {
                continue;
            }
            final Class oldInstanceClass = oldType.getInstanceClass();
            final Class javaInstanceClass = javaType.getInstanceClass();
            if (oldInstanceClass == javaInstanceClass) {
                pClassToType.put(javaInstanceClass, oldType);
                continue;
            }
            if (oldInstanceClass == null && (oldType instanceof DataObjectDecorator) && !oldType.isDataType()) {
                DataObjectDecorator oldTypeObj = (DataObjectDecorator)oldType;
                boolean readOnly = oldTypeObj.getInstance().isReadOnlyMode();
                oldTypeObj.getInstance().setReadOnlyMode(false);
                oldTypeObj.set(TypeType.getJavaClassProperty(), javaInstanceClass.getName());
                ((TypeLogicFacade)oldTypeObj).setInstanceClass(javaInstanceClass); //TODO use this on SdoType
                for (Object o: oldType.getDeclaredProperties()) {
                    DataObject p = (DataObject)o;
                    String propName = p.getString(PropertyType.NAME);
                    Property javaProp = javaType.getProperty(propName);
                    if (javaProp == null) {
                        String javaPropName = p.getString(PropertyType.getJavaNameProperty());
                        if (javaPropName == null) {
                            Boolean mixedCase = null;
                            if (_helperContext != null) {
                                mixedCase =
                                    (Boolean)_helperContext.getContextOption(
                                        SapHelperContext.OPTION_KEY_MIXED_CASE_JAVA_NAMES);
                            }
                            if (mixedCase == null || mixedCase) {
                                javaPropName = NameConverter.CONVERTER.toVariableName(propName);
                            } else {
                                javaPropName = propName;
                            }
                        }
                        javaProp = javaType.getProperty(javaPropName);
                        if (javaProp == null) {
                            throw new IllegalArgumentException("Found no matching property named " 
                                + propName + " on class " + javaInstanceClass.getName());
                        }
                        p.set(PropertyType.getJavaNameProperty(), javaPropName);
                    }
                    List<SdoProperty> instanceProps = javaProp.getInstanceProperties();
                    int size = instanceProps.size();
                    for (int i=propTypeOpenOffset; i<size; ++i) {
                        SdoProperty instanceProp = instanceProps.get(i);
                        if (URINamePair.DATATYPE_JAVA_URI.equals(instanceProp.getUri())) {
                            p.set(instanceProp, javaProp.get(instanceProp));
                        }
                    }
                }
                ((SdoType)oldType).useCache();
                oldTypeObj.getInstance().setReadOnlyMode(readOnly);
                pClassToType.put(javaInstanceClass, oldType);
            }
        }
    }

    private static interface DefaultAnnotated {
        @SdoPropertyMetaData()
        void annotationDefaults();
    }
    
    public class NoBeanException extends IllegalArgumentException {

        public NoBeanException(String pS) {
            super(pS);
        }
        
    }
}
