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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import com.sap.sdo.api.OpenContentProperty;
import com.sap.sdo.api.SchemaInfo;
import com.sap.sdo.api.SdoFacets;
import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.SdoTypeMetaData;
import com.sap.sdo.api.XmlPropertyMetaData;
import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.MetaDataType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.types.simple.ListSimpleType;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * Generates Java interfaces for SDO types.
 * Use {@link #addSchemaLocation(String, String)} to add a schema location
 * annotation to the interfaces.
 * The package names will be calculated from the uri of the types. If other
 * package names are required, use {@link #addPackage(String, String)}.
 * @author D042807
 *
 */
public class InterfaceGeneratorImpl implements InterfaceGenerator {
    private static final String SDO_INTERNAL_PACKAGE = com.sap.sdo.api.types.sdo.Object.class.getPackage().getName();
    private static final String SDO_INTERNAL_JAVA_PACKAGE = com.sap.sdo.api.types.sdo.java.IntObject.class.getPackage().getName();
    private final Set<SdoType> _rendered = new HashSet<SdoType>();
    private final Set<SdoType> _referenced = new HashSet<SdoType>();
    private final List<String> _createdClasses = new ArrayList<String>();
    private PrintWriter _writer;
    private final String _rootPath;
    private final SapHelperContext _helperContext;
    private final Map<String, List<Property>> _namespaceToProperties;
    private final Map<String, String> _namespaceToPackage = new HashMap<String, String>();
    private final Map<String, String> _uriNameToSchemaLocation = new HashMap<String, String>();
    private boolean _generateAnnotations = true;

    public InterfaceGeneratorImpl(String rootPathString, SapHelperContext pHelperContext) {
        _rootPath = rootPathString;
        _helperContext = pHelperContext;
        _namespaceToProperties = null;
        addPackage(URINamePair.DATATYPE_URI, SDO_INTERNAL_PACKAGE);
        addPackage(URINamePair.DATATYPE_JAVA_URI, SDO_INTERNAL_JAVA_PACKAGE);
    }
    
    public InterfaceGeneratorImpl(String rootPathString, Map<String, List<Property>> pNamespaceToProperties) {
        _rootPath = rootPathString;
        _helperContext = null;
        _namespaceToProperties = pNamespaceToProperties;
        addPackage(URINamePair.DATATYPE_URI, SDO_INTERNAL_PACKAGE);
        addPackage(URINamePair.DATATYPE_JAVA_URI, SDO_INTERNAL_JAVA_PACKAGE);
    }
    
    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.java.InterfaceGenerator#addPackage(java.lang.String, java.lang.String)
     */
    public void addPackage(String pNamespace, String pPackage) {
        _namespaceToPackage.put(pNamespace, pPackage);
    }
    
    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.java.InterfaceGenerator#addSchemaLocation(java.lang.String, java.lang.String)
     */
    public void addSchemaLocation(String pUriName, String pSchemaLocation) {
        _uriNameToSchemaLocation.put(pUriName, pSchemaLocation);
    }
    
    private void writeComplexType(final SdoType type) throws FileNotFoundException {
        _rendered.add(type);
        if (URINamePair.DATAOBJECT.equalsUriName(type)) {
            return;
        }
        checkConstraints(type);
        ClassName className = getClassName(type);
        String packageName = className.getPackageName();
        String javaName = className.getJavaName();

        String schemaLocation = getSchemaLocaction(type);
        final TypeAnnotationData annotationData = new TypeAnnotationData(type, packageName, javaName);
        final File directory = getDirectory(packageName);
        final File javaFile = new File(directory, javaName+".java");
        _writer = new PrintWriter(javaFile);
        _createdClasses.add(className.getFullClassName());
        if (packageName.length() > 0) {
            _writer.write("package "+packageName+";\n\n");
        }
        if (_generateAnnotations) {
            final SchemaAnnotationData schemaAnnotations = new SchemaAnnotationData(schemaLocation);
            schemaAnnotations.write(_writer);
            annotationData.write(_writer);
        }        
        _writer.write("public interface ");
        _writer.write(javaName + " ");
        final List<Type> baseTypes = new ArrayList<Type>(type.getBaseTypes());
        if (baseTypes!=null && baseTypes.size()>0) {
            _writer.write("extends ");
            boolean first = true;
            for (Type baseType: baseTypes) {
                if (!first) {
                    _writer.append(", ");
                }
                first = false;
                final ClassName baseClassName = getClassName(baseType);
                _writer.append(baseClassName.getFullClassName());
                if (!baseClassName.isInClassPath()) {
                    _referenced.add((SdoType)baseType);
                }
            }
        }
        _writer.write(" {\n\n");
        Set<String> renderedProps = new HashSet<String>();
        for (SdoProperty prop: (List<SdoProperty>)type.getDeclaredProperties()) {
            String propStr = prop.getName()
                            + prop.getJavaName()
                            + prop.getXmlName()
                            + prop.getType()
                            + prop.isContainment()
                            + prop.isMany()
                            + prop.isReadOnly()
                            + prop.isXmlElement();
            if (!renderedProps.contains(propStr)) {
                writeProperty(prop, schemaLocation);
                renderedProps.add(propStr);
            }
        }
        _writer.write("}\n");
        _writer.flush();
        _writer.close();
    }
    
    private void checkConstraints(SdoType pType) {
        List<Property> properties = pType.getProperties();
        HashSet<String> propNames = new HashSet<String>();
        for (Property prop: properties) {
            String propName = prop.getName();
            if (propNames.contains(propName)) {
                throw new IllegalArgumentException("Property name " + propName + 
                    " is not unique on type " + pType.getQName().toStandardSdoFormat()
                    + ". Use annotations!");
            }
            propNames.add(propName);
        }
    }
    
    private Set<OpenContentAnnotations> getOpenContentAnnotations(Type type) {
        Set<OpenContentAnnotations> ret = new TreeSet<OpenContentAnnotations>();
        List<Property> allProperties = getPropertiesForNamespace(type.getURI());
        for (Property prop: allProperties) {
            if (prop.getType() == type) {
                ret.add(new OpenContentAnnotations(prop));
            }
        }
        return ret;
    }
    
    private List<Property> getPropertiesForNamespace(String pNamespace) {
        List<Property> properties;
        if (_namespaceToProperties == null) {
            properties = ((TypeHelperImpl)_helperContext.getTypeHelper()).getPropertiesForNamespace(pNamespace);
        } else {
            properties = _namespaceToProperties.get(pNamespace);
        }
        if (properties == null) {
            properties = Collections.emptyList();
        }
        return properties;
    }
    
    private SdoType getFirstUnrenderedClass() {
        for (SdoType ret: _referenced) {
            if (!_rendered.contains(ret)) {
                return ret;
            }
        }
        return null;
    }
    private String propertyType(final SdoProperty prop) {
        final SdoType type = (SdoType)prop.getType();
        if (URINamePair.CHANGESUMMARY_TYPE.equalsUriName(type)) {
            return ChangeSummary.class.getName();
        }
        if ((type.getInstanceClass() == null) ||
            (type.isDataType() && type.getHelperContext() != SapHelperProvider.getContext(SapHelperProvider.CORE_CONTEXT_ID))) {
            _referenced.add(type);
        }
        String typeName;
        if (type.isDataType()) {
            Class propClass = prop.getJavaClass();
            if (propClass == null) {
                propClass = type.getInstanceClass();
            }
            typeName = propClass.getCanonicalName();
            typeName = NameConverter.CONVERTER.normalizeClassname(typeName);
            if (List.class.getName().equals(typeName)) {
                Type itemType = getListItemType(type);
                if (itemType != null) {
                    Class itemClass = getListItemClass(itemType.getInstanceClass());
                    typeName += '<'
                        + NameConverter.CONVERTER.normalizeClassname(itemClass.getCanonicalName())
                        + '>';
                }
            }
        } else {
            typeName = getClassName(type).getFullClassName();
        }
        if (prop.isMany()) {
            final StringBuilder ret = new StringBuilder();
            ret.append("java.util.List<");
            ret.append(typeName);
            ret.append('>');
            return ret.toString();
        }
        return typeName;        
    }
    
    private Type getListItemType(Type type) {
        if (type instanceof ListSimpleType) {
            return ((ListSimpleType)type).getItemType();
        }
        List<Type> baseTypes = type.getBaseTypes();
        if (!baseTypes.isEmpty()) {
            return getListItemType(baseTypes.get(0));
        }
        return null;
    }
    
    private Class getListItemClass(Class pClass) {
        if (!pClass.isPrimitive()) {
            return pClass;
        }
        JavaSimpleType simpleType = (JavaSimpleType)_helperContext.getTypeHelper().getType(pClass);
        return simpleType.getNillableType().getInstanceClass();
    }
    
    private static class XmlAnnotationData extends AnnotationData {
        private boolean _empty = true;
        XmlAnnotationData(DataObject xmlInfo) {
            super();
            if (xmlInfo.isSet(PropertyType.getXmlElementProperty())) {
                _empty = false;
                _annotations.put("xmlElement",xmlInfo.getString(PropertyType.getXmlElementProperty()));
            }
            String xsdType = xmlInfo.getString(PropertyType.getXsdTypeProperty());
            if (xsdType != null) {
                _empty = false;
                _annotations.put("xsdType",'"'+xsdType+'"');
            }
            String xmlName = xmlInfo.getString(PropertyType.getXmlNameProperty());
            if (xmlName != null && xmlName.length() > 0) {
                _empty = false;
                _annotations.put("xsdName",'"'+xmlName+'"');
            }
            String ref = xmlInfo.getString(PropertyType.getReferenceProperty());
            if (ref != null && ref.length() > 0) {
                _empty = false;
                _annotations.put("ref",'"'+ref+'"');
            }
        }
        boolean isEmpty() {
            return _empty;
        }
        @Override
        protected String getAnnotationClassName() {
            return XmlPropertyMetaData.class.getName();
        }
        @Override
        protected int getIndent() {
            return 2;
        }
        
    }
    private class PropertyAnnotationData extends AnnotationData {
        private final String _javaName;
        <S> PropertyAnnotationData(final SdoProperty prop, final String schemaLocation) {
            super();
            Boolean mixedCase = null;
            if (_helperContext != null) {
                mixedCase =
                    (Boolean)_helperContext.getContextOption(
                        SapHelperContext.OPTION_KEY_MIXED_CASE_JAVA_NAMES);
            }
            if (prop.getJavaName()!=null && prop.getJavaName().length()>0) {
                _javaName = prop.getJavaName();
            } else {
                String propName = prop.getName();
                if (mixedCase == null || mixedCase) {
                    propName = NameConverter.CONVERTER.toPropertyName(propName);
                }
                _javaName =
                    handleNameConflictsWithDataObject(propName, prop.getContainingType());
            }
            if (!_javaName.equals(prop.getName())) {
                _annotations.put("sdoName",'"'+prop.getName()+'"');
            }
            final SdoType<S> type = (SdoType<S>)prop.getType();
            if (type.isDataType()) {
                Class javaClass = prop.getJavaClass();
                if (javaClass == null) {
                    javaClass = type.getInstanceClass();
                }
                if (javaClass == null) {
                    throw new IllegalArgumentException("No instanceClass specified for "+type.getName());
                }
                Type defaultType = ((TypeHelperImpl)SapHelperProviderImpl.getCoreContext().getTypeHelper()).lookupTypeForClass(javaClass);
                if (!type.equals(defaultType)) {
                    _annotations.put("sdoType",'"'+type.getQName().toStandardSdoFormat()+'"');
                }
            } else {
                _annotations.put("containment", String.valueOf(prop.isContainment()));
            }
            boolean defaultNotNullable = type.isDataType() &&
                    type.getInstanceClass()!=null &&
                    type.getInstanceClass().isPrimitive();
            if (prop.isNullable() && defaultNotNullable) {
                _annotations.put("nullable", "com.sap.sdo.api.Bool.TRUE");
            } else if (!prop.isNullable() && !defaultNotNullable){
                _annotations.put("nullable", "com.sap.sdo.api.Bool.FALSE");
            }
            if (prop.getOpposite()!=null) {
                String oppositeProperty = prop.getOpposite().getName();
                if (mixedCase == null || mixedCase) {
                    oppositeProperty = NameConverter.CONVERTER.toVariableName(oppositeProperty);
                }
                oppositeProperty = Character.toLowerCase(oppositeProperty.charAt(0)) + oppositeProperty.substring(1);
                _annotations.put("opposite", '"'+oppositeProperty+'"');
            }
            Object defaultValue = prop.getDefault();
            if (prop.getDefault()!=null) {
                if (!(defaultValue instanceof String)) {
                    defaultValue = type.convertToJavaClass((S)prop.getDefault(), String.class);
                }
                _annotations.put("defaultValue",'"'+ defaultValue.toString()+'"');
            }
            if (prop.getIndex()>=0) {
                _annotations.put("propertyIndex", String.valueOf(prop.getIndex()));
            }
            if (prop instanceof DataObject && schemaLocation == null) {
                XmlAnnotationData xmlAnnotations = new XmlAnnotationData((DataObject)prop);
                if (!xmlAnnotations.isEmpty()) {
                    OutputStream os = new ByteArrayOutputStream();
                    PrintWriter ps = new PrintWriter(os);
                    xmlAnnotations.write(ps);
                    ps.close();
                    if (os.toString().length()>0) {
                        _annotations.put("xmlInfo",os.toString());
                    }
                }
            }
            List<String> aliaseNames = prop.getAliasNames();
            if (!aliaseNames.isEmpty()) {
                StringBuilder aliasString = new StringBuilder();
                aliasString.append('{');
                for (String alias: aliaseNames) {
                    if (aliasString.length() > 1) {
                        aliasString.append(", ");
                    }
                    aliasString.append('"').append(alias).append('"');
                }
                aliasString.append('}');
                _annotations.put("aliasNames", aliasString.toString());
            }
        }
        private String handleNameConflictsWithDataObject(final String pName, final Type containingType) {
            final String name = pName;
            final String getterName = "get"+name.substring(0,1).toUpperCase(Locale.ENGLISH)+name.substring(1);
            try {
                DataObject.class.getMethod(getterName,new Class[]{});
                return containingType.getName().substring(0,1).toUpperCase(Locale.ENGLISH)+
                       containingType.getName().substring(1)+
                       getterName.substring(3);
                
            } catch (NoSuchMethodException e) { //$JL-EXC$
                return name;
            }
        }
        public String getJavaName() {
            return _javaName;
        }
        protected String getAnnotationClassName() {
            return SdoPropertyMetaData.class.getName();
        }
        @Override
        protected int getIndent() {
            return 1;
        }
    }
    private static String getDefaultUri(String packageName) {
        return packageName;
    }
    private class TypeAnnotationData extends AnnotationData {

        TypeAnnotationData(final SdoType type, final String packageName, final String javaName) {
            String uri = type.getQName().getURI();
            if (!uri.equals(getDefaultUri(packageName))) {
                if (uri.length() == 0) {
                    _annotations.put("noNamespace","true");
                } else {
                    _annotations.put("uri",'"'+uri+'"');
                }
            }
            if (!type.getName().equals(javaName)) {
                _annotations.put("sdoName",'"'+type.getName()+'"');
            }
            if (type.isOpen()) {
                _annotations.put("open","true");
            }
            if (type.isSequenced()) {
                _annotations.put("sequenced","true");
            }
            if (type.isAbstract()) {
                _annotations.put("abstractDataObject","true");
            }
            Set<OpenContentAnnotations> openContentAnnotations = getOpenContentAnnotations(type);
            if (!openContentAnnotations.isEmpty()) {
                OutputStream os = new ByteArrayOutputStream();
                PrintWriter ps = new PrintWriter(os);
                boolean first = true;
                for (OpenContentAnnotations annotation: openContentAnnotations) {
                    if (!first) {
                        ps.println(",");
                    }
                    first = false;
                    annotation.write(ps);
                }
                ps.close();
                if (os.toString().length()>0) {
                    _annotations.put("openContentProperties","{\n"+os.toString()+'}');
                }
            }
            if (((DataObject)type).getBoolean(TypeType.getAttributeFormDefaultProperty())) {
                _annotations.put("attributeFormDefault", "true");
            }
            if (((DataObject)type).getBoolean(TypeType.getElementFormDefaultProperty())) {
                _annotations.put("elementFormDefault", "true");
            }
            if (((DataObject)type).getBoolean(TypeType.getMixedProperty())) {
                _annotations.put("mixed", "true");
            }
        }
        protected String getAnnotationClassName() {
            return SdoTypeMetaData.class.getName();
        }

        @Override
        protected int getIndent() {
            return 0;
        }
    }
    private static class OpenContentAnnotations extends AnnotationData implements Comparable {

        public OpenContentAnnotations(Property prop) {
            _annotations.put("name", '"'+prop.getName()+'"');
            if (prop.isMany()) {
                _annotations.put("many", "true");
            }
            if (!prop.isContainment()) {
                _annotations.put("containment", "false");
            }
        }
        protected String getAnnotationClassName() {
            return OpenContentProperty.class.getName();
        }
        @Override
        protected int getIndent() {
            return 2;
        }
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) {
            if (o == null || !(o instanceof OpenContentAnnotations)) {
                return -1;
            }
            return _annotations.get("name").compareTo(
                ((OpenContentAnnotations)o)._annotations.get("name"));
        }
    }
    private static class SchemaAnnotationData extends AnnotationData {

        SchemaAnnotationData(final String schemaLocation) { 
            if (schemaLocation != null) {
                _annotations.put("schemaLocation",'"'+schemaLocation+'"');
            }
        }
        protected String getAnnotationClassName() {
            return SchemaInfo.class.getName();
        }
        @Override
        protected int getIndent() {
            return 0;
        }
    }
    private static class FacetOfStringAnnotationData extends AnnotationData {
        FacetOfStringAnnotationData(final DataObject facet) {
            if (facet == null) {
                return;
            }
            if (facet.isSet(TypeType.FACET_MINLENGTH)) {
                _annotations.put("minLength",facet.get(TypeType.FACET_MINLENGTH).toString());
            }
            if (facet.isSet(TypeType.FACET_MAXLENGTH)) {
                _annotations.put("maxLength",facet.get(TypeType.FACET_MAXLENGTH).toString());
            }
            if (facet.isSet(TypeType.FACET_LENGTH)) {
                _annotations.put("length",facet.get(TypeType.FACET_LENGTH).toString());
            }
            if (facet.isSet(TypeType.FACET_ENUMERATION)) {
                _annotations.put("enumeration", "{"+getArrayString(facet.getList(TypeType.FACET_ENUMERATION))+"}");
            }
            if (facet.isSet(TypeType.FACET_MININCLUSIVE)) {
                _annotations.put("minInclusive",facet.get(TypeType.FACET_MININCLUSIVE).toString());
            }
            if (facet.isSet(TypeType.FACET_MAXINCLUSIVE)) {
                _annotations.put("maxInclusive",facet.get(TypeType.FACET_MAXINCLUSIVE).toString());
            }
            if (facet.isSet(TypeType.FACET_MINEXCLUSIVE)) {
                _annotations.put("minExclusive",facet.get(TypeType.FACET_MINEXCLUSIVE).toString());
            }
            if (facet.isSet(TypeType.FACET_MAXEXCLUSIVE)) {
                _annotations.put("maxExclusive",facet.get(TypeType.FACET_MAXEXCLUSIVE).toString());
            }
            if (facet.isSet(TypeType.FACET_PATTERN)) {
                _annotations.put("pattern", "{"+getArrayString(facet.getList(TypeType.FACET_PATTERN))+"}");
            }
            if (facet.isSet(TypeType.FACET_TOTALDIGITS)) {
                _annotations.put("totalDigits",facet.get(TypeType.FACET_TOTALDIGITS).toString());
            }
            if (facet.isSet(TypeType.FACET_FRACTIONDIGITS)) {
                _annotations.put("fractionDigits",facet.get(TypeType.FACET_FRACTIONDIGITS).toString());
            }
        }
        protected String getAnnotationClassName() {
            return SdoFacets.class.getName();
        }
        @Override
        protected int getIndent() {
            return 0;
        }
        private String getArrayString(final List list) {
            final StringBuilder ret = new StringBuilder();
            boolean first = true;
            for (Object item: list) {
                if (!first) {
                    ret.append(", ");
                } else {
                    first = false;
                }
                ret.append('"');
                ret.append(item.toString());
                ret.append('"');
            }
            return ret.toString();
        }
    }
    
    private abstract static class AnnotationData {
        protected final Map<String,String> _annotations = new TreeMap<String,String>();
        protected abstract String getAnnotationClassName();
        protected abstract int getIndent();
        private void indent(final PrintWriter writer, final int tabs) {
            for (int i=0; i<tabs; i++) {
                writer.write("    ");
            }
        }
        AnnotationData() {
            super();
        }
        void write(final PrintWriter writer) {
            if (_annotations.size() < 1) {
                return;
            }
            indent(writer,getIndent());
            writer.write("@"+getAnnotationClassName()+"(\n");
            final Iterator<Map.Entry<String,String>> it = _annotations.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = it.next();
                indent(writer,getIndent()+1);
                writer.write(pair.getKey()+" = ");
                writeEscaped(writer, pair.getValue().toString());
                if (it.hasNext()) {
                    writer.write(",\n");
                }
            }
            writer.write("\n");
            indent(writer,getIndent());
            writer.write(")");
            if (getIndent() <= 1) {
                writer.write("\n");
            }
        }
        private void writeEscaped(final PrintWriter writer, String pValue) {
            int i = pValue.indexOf('\\');
            int start = 0;
            while (i >= 0) {
                writer.append(pValue, start, i);
                writer.append('\\');
                start = i;
                i = pValue.indexOf('\\', i + 1);
            }
            writer.append(pValue, start, pValue.length());
        }

    }
        
    private void writeProperty(final SdoProperty prop, String schemaLocation) {
        final PropertyAnnotationData annotations = new PropertyAnnotationData(prop, schemaLocation);
        final String propType = propertyType(prop);
        if (_generateAnnotations) {
            annotations.write(_writer);
        }
        _writer.write("    "+propType+" ");
        String name = annotations.getJavaName();
        name = name.substring(0,1).toUpperCase(Locale.ENGLISH)+name.substring(1);
        if ("boolean".equals(propType)) {
            _writer.write("is");
        } else {
            _writer.write("get");
        }
        _writer.write(name+"();\n");
        if (!prop.isReadOnly()) {
            _writer.write("    void set"+name+"("+propType+" p"+name+");\n");
        }
        _writer.write('\n');
    }

    private void writeSimpleType(final SdoType type) throws FileNotFoundException {
        _rendered.add(type);
        if (type instanceof MetaDataType) {
            return;
        }
        ClassName className = getClassName(type);
        String packageName = className.getPackageName();
        String javaName = className.getJavaName();

        final TypeAnnotationData annotationData = new TypeAnnotationData(type, packageName, javaName);
        final File directory = getDirectory(packageName);
        final File javaFile = new File(directory, javaName+".java");
        _writer = new PrintWriter(javaFile);
        _writer.write("package "+packageName+";\n\n");
        if (_generateAnnotations) {
            final AnnotationData facetAnnotations = new FacetOfStringAnnotationData(type.getFacets());
            facetAnnotations.write(_writer);
            annotationData.write(_writer);
        }        
        _writer.write("public interface ");
        _writer.write(javaName+" ");
        final List baseTypes = type.getBaseTypes();
        if (baseTypes!=null && baseTypes.size()>0) {
            _writer.write("extends ");
            final Iterator it = baseTypes.iterator();
            while (it.hasNext()) {
                final Type baseType = (Type)it.next();
                if (baseType instanceof ListSimpleType) {
                    _writer.append("java.util.List<");
                    Type elementType = ((ListSimpleType)baseType).getItemType();
                    writeBaseSimpleTypeName(elementType);
                    _writer.append(">");
                    continue;
                }
                writeBaseSimpleTypeName(baseType);
                if (it.hasNext()) {
                    _writer.append(", ");
                }
            }
        }
        _writer.write(" {}\n\n");
        _writer.flush();
        _writer.close();
    }
    private void writeBaseSimpleTypeName(Type elementType) {
        _writer.append(getClassName(elementType).getFullClassName());
    }
    
    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.java.InterfaceGenerator#generate(commonj.sdo.Type)
     */
    public List<String> generate(final Type type) throws IOException {
        return generate(Collections.singletonList(type));
    }
    public String getFullClassName(Type t) {
    	return getClassName(t).getFullClassName();
    }
    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.java.InterfaceGenerator#generate(java.util.List)
     */
    public List<String> generate(final List<Type> types) throws IOException {
        _referenced.addAll((List)types);
        for (SdoType referencedType = getFirstUnrenderedClass();
            referencedType!=null;
            referencedType = getFirstUnrenderedClass()) {
            if (referencedType.isDataType()) {
                writeSimpleType(referencedType);
            } else {
                writeComplexType(referencedType);
            }
        }
        return _createdClasses;
    }
    
    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.java.InterfaceGenerator#generate(java.lang.String)
     */
    public List<String> generate(String namespace) throws IOException {
        if (_helperContext == null) {
            throw new IllegalStateException("Use constructor InterfaceGenerator(String , HelperContext)");
        }
        TypeHelperImpl typeHelper = (TypeHelperImpl)_helperContext.getTypeHelper();
        return generate(typeHelper.getTypesForNamespace(namespace));
    }
    
    public String getPackage(String namespace) {
        String packageName = _namespaceToPackage.get(namespace);
        if (packageName != null) {
            return packageName;
        }
        if (namespace.matches("[a-z0-9\\.]*")) {
            return namespace;
        }
        List<String> packageParts = new ArrayList<String>();
        StringTokenizer slashTokenizer = new StringTokenizer(namespace, "/:");
        String token = slashTokenizer.nextToken();
        String tokenLc = token.toLowerCase(Locale.ENGLISH);
        if (tokenLc.equals("http") || tokenLc.equals("urn")) {
            token = slashTokenizer.nextToken();
        }
        StringTokenizer dotTokenizer = new StringTokenizer(token, ".");
        while (dotTokenizer.hasMoreElements()) {
            packageParts.add(0, NameConverter.CONVERTER.toPackageName(dotTokenizer.nextToken()));
        }
        while (slashTokenizer.hasMoreElements()) {
            packageParts.add(NameConverter.CONVERTER.toPackageName(slashTokenizer.nextToken()));
        }
        StringBuilder sb = new StringBuilder();
        for (String part: packageParts) {
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(part);
        }
        packageName = sb.toString();
        _namespaceToPackage.put(namespace, packageName);
        return packageName;
    }
    
    public String getSchemaLocaction(Type type) {
        URINamePair uriName = URINamePair.fromType(type);
        String schemaLocation = _uriNameToSchemaLocation.get(uriName.toStandardSdoFormat());
        if (schemaLocation != null) {
            return schemaLocation;
        }
        return _uriNameToSchemaLocation.get(uriName.getURI());
    }
    
    private ClassName getClassName(Type type) {
        if (!type.isDataType()) {
            Class instanceClass = type.getInstanceClass();
            if (instanceClass != null) {
                return new ClassName(instanceClass);
            }
            String instanceClassName = (String)type.get(TypeType.getJavaClassProperty());
            if (instanceClassName != null) {
                return new ClassName(instanceClassName);
            }
        }
        String packageName = (String)type.get(TypeType.getPackageProperty());
        if (packageName == null) {
            String uri = type.getURI();
            if (uri == null) {
                uri = "";
            }
            packageName = getPackage(uri);
        }
        String javaName;
        Boolean mixedCase = null;
        if (_helperContext != null) {
            mixedCase =
                (Boolean)_helperContext.getContextOption(
                    SapHelperContext.OPTION_KEY_MIXED_CASE_JAVA_NAMES);
        }
        if (mixedCase == null || mixedCase) {
            javaName = NameConverter.CONVERTER.toClassName(type.getName());
        } else {
            String typeName = type.getName();
            javaName = Character.toUpperCase(typeName.charAt(0)) + typeName.substring(1);
        }
        return new ClassName(packageName, javaName);
    }
    
    public File getDirectory(String packageName) {
        File directory = new File(_rootPath+File.separatorChar+
            packageName.replace('.',File.separatorChar));
        directory.mkdirs();
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Directory "+directory.getPath()+" could not be created.");
        }
        return directory;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.java.InterfaceGenerator#getGenerateAnnotations()
     */
    public boolean getGenerateAnnotations() {
        return _generateAnnotations;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.java.InterfaceGenerator#setGenerateAnnotations(boolean)
     */
    public void setGenerateAnnotations(boolean pGenerateAnnotations) {
        _generateAnnotations = pGenerateAnnotations;
    }
    
    private class ClassName {
        private final String _className;
        private final int _delemiter;
        private Boolean _inClassPath;
        
        public ClassName(final Class pClass) {
            _className = pClass.getName();
            _delemiter = _className.lastIndexOf('.');
            _inClassPath = true;
        }
        
        public ClassName(final String pClassName) {
            _className = pClassName;
            _delemiter = pClassName.lastIndexOf('.');
        }
        
        public ClassName(final String pPackageName, final String pJavaName) {
            _className = pPackageName + '.' + pJavaName;
            _delemiter = pPackageName.length();
        }

        public String getPackageName() {
            return _className.substring(0, _delemiter);
        }
        
        public String getJavaName() {
            return _className.substring(_delemiter + 1);
        }
        
        public String getFullClassName() {
            return _className;
        }

        public boolean isInClassPath() {
            if (_inClassPath != null) {
                return _inClassPath;
            }
            try {
                Class.forName(_className);
                _inClassPath = true;
            } catch (ClassNotFoundException e) {
                _inClassPath = false;
            }
            return _inClassPath;
        }

        @Override
        public String toString() {
            return _className;
        }
        
    }
}
