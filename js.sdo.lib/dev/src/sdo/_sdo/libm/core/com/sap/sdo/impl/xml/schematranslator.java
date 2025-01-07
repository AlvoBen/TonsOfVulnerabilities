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
package com.sap.sdo.impl.xml;

import static com.sap.sdo.api.util.URINamePair.DATATYPE_URI;
import static com.sap.sdo.api.util.URINamePair.PROP_JAVA_EXTENDED_INSTANCE_CLASS;
import static com.sap.sdo.api.util.URINamePair.PROP_JAVA_INSTANCE_CLASS;
import static com.sap.sdo.api.util.URINamePair.PROP_JAVA_PACKAGE;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_ALIAS_NAME;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_DATA_TYPE;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_EMBEDDED_KEY;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_KEY;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_KEY_TYPE;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_MANY;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_NAME;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_OPPOSITE_PROPERTY;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_ORPHAN_HOLDER;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_PROPERTY_TYPE;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_READ_ONLY;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_SEQUENCE;
import static com.sap.sdo.api.util.URINamePair.SCHEMA_ID;
import static com.sap.sdo.api.util.URINamePair.SCHEMA_URI;
import static com.sap.sdo.api.util.URINamePair.STRINGS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.types.schema.Any;
import com.sap.sdo.api.types.schema.Attribute;
import com.sap.sdo.api.types.schema.ComplexType;
import com.sap.sdo.api.types.schema.Element;
import com.sap.sdo.api.types.schema.ExplicitGroup;
import com.sap.sdo.api.types.schema.ExtensionType;
import com.sap.sdo.api.types.schema.Facet;
import com.sap.sdo.api.types.schema.Import;
import com.sap.sdo.api.types.schema.NoFixedFacet;
import com.sap.sdo.api.types.schema.NumFacet;
import com.sap.sdo.api.types.schema.Pattern;
import com.sap.sdo.api.types.schema.Restriction;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.types.schema.SimpleType;
import com.sap.sdo.api.types.schema.TotalDigits;
import com.sap.sdo.api.types.schema.Wildcard;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.Namespace;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.java.NameConverter;
import com.sap.sdo.impl.types.schema.SchemaTypeFactory;
import com.sap.sdo.impl.types.simple.ListSimpleType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;

/**
 * @author D042774
 *
 */
public class SchemaTranslator {
    private Set<SdoType<?>> _rendered = new TreeSet<SdoType<?>>(TypesComparator.INSTANCE);
    private Set<SdoType<?>> _referenced = new TreeSet<SdoType<?>>(TypesComparator.INSTANCE);
    private boolean _elementFormQualified = false;
    private boolean _attributeFormQualified = false;
    private String _targetNamespace;

    private final HelperContext _helperContext;
    private final TypeHelper _typeHelper;
    private Schema _schema;
    private List<Import> _imports;

    /**
     * @param pHelperContext
     */
    public SchemaTranslator(HelperContext pHelperContext) {
        super();
        _helperContext = pHelperContext;
        _typeHelper = _helperContext.getTypeHelper();
    }

    public Schema getSchema(List<?> types, Map<String,String> namespaceToSchemaLoc) {
        if (types == null || types.size()==0) {
            return null;
        }

        _schema = (Schema)_helperContext.getDataFactory().create(Schema.class);
        _imports = new ArrayList<Import>();
        for (Map.Entry<String,String> entry: namespaceToSchemaLoc.entrySet()) {
            Import imp = (Import)_helperContext.getDataFactory().create(Import.class);
            imp.setSchemaLocation(entry.getValue());
            imp.setNamespace(entry.getKey());
            _imports.add(imp);
        }

        List<SdoType<?>> sdoTypes = new ArrayList<SdoType<?>>();
        for (Type type : (List<Type>)types) {
            if (type instanceof Namespace) {
                for (Property property : (List<Property>)type.getProperties()) {
                    sdoTypes.add((SdoType<?>)property.getType());
                }
            } else {
                sdoTypes.add((SdoType<?>)type);
            }
        }

        _targetNamespace = sdoTypes.get(0).getXmlUri();
        _schema.setTargetNamespace(_targetNamespace);

        for (int i=0; i<sdoTypes.size(); i++) {
            Class<?> ic = sdoTypes.get(i).getInstanceClass();
            if (ic!=null && ic.isInterface() && !ic.equals(List.class)) {
                String packageName = ic.getPackage().getName();
                Property javaPackage =
                    _typeHelper.getOpenContentProperty(
                        PROP_JAVA_PACKAGE.getURI(), PROP_JAVA_PACKAGE.getName());
                _schema.setString(javaPackage, packageName);
                break;
            }
        }
        SdoType<?> firstComplexType = null;
        for (SdoType<?> type : sdoTypes) {
            if (!type.isDataType() && !type.isLocal()) {
                firstComplexType = type;
                break;
            }
        }
        if (firstComplexType != null && firstComplexType.getElementFormDefaultQualified()) {
            _schema.setElementFormDefault("qualified");
            _elementFormQualified = true;
        }
        if (firstComplexType != null && firstComplexType.getAttributeFormDefaultQualified()) {
            _schema.setAttributeFormDefault("qualified");
            _attributeFormQualified = true;
        }
        _referenced.addAll(sdoTypes);
        final List<Property> propertiesForNamespace = new ArrayList<Property>(((TypeHelperImpl)_helperContext.getTypeHelper()).getPropertiesForNamespace(_targetNamespace));
        for (SdoType<?> referencedType = getFirstUnrenderedClass();
            referencedType!=null;
            referencedType = getFirstUnrenderedClass()) {
            for (Iterator<Property> it=propertiesForNamespace.iterator(); it.hasNext();) {
                Property prop = it.next();
                if (prop.getType()==referencedType) {
                    addProperty((SdoProperty)prop, _schema);
                    it.remove();
                }
            }
            addType(referencedType, _schema);
        }
        for (Property prop: propertiesForNamespace) {
            addProperty((SdoProperty)prop, _schema);
            // look for new unrendered types (type of global element)
            for (SdoType<?> referencedType = getFirstUnrenderedClass();
            referencedType!=null;
            referencedType = getFirstUnrenderedClass()) {
                addType(referencedType, _schema);
            }
        }

        Sequence sequence = _schema.getSequence();
        Property impProp = _schema.getInstanceProperty("import");
        for (int i=0; i<_imports.size(); ++i) {
            sequence.add(i, impProp, _imports.get(i));
        }

        return _schema;
    }

    private SdoType<?> getFirstUnrenderedClass() {
        for (SdoType<?> ret: _referenced) {
            if (!_rendered.contains(ret) && !ret.isLocal() && ret.getXmlUri().equals(_targetNamespace)) {
                return ret;
            }
        }
        return null;
    }

    private void addProperty(SdoProperty prop, DataObject block) {
        if (prop.isXmlElement()) {
            addElement(prop, block);
        } else {
            addAttribute(prop, block);
        }
    }

    private void addAttribute(SdoProperty prop, DataObject block) {
        String xmlName = prop.getName();
        String sdoName = null;
        SdoType<Object> propType = (SdoType<Object>)prop.getType();
        String tmp = ((DataObject)prop).getString(PropertyType.getXmlNameProperty());
        if (tmp != null) {
            sdoName = xmlName;
            xmlName = tmp;
        }
        Attribute attribute = (Attribute)block.createDataObject("attribute");
        attribute.setName(xmlName);
        Object defaultValue = prop.getDefault();
        if (defaultValue != null && !defaultValue.equals(propType.getDefaultValue())) {
            attribute.setDefault(propType.convertToJavaClass(defaultValue, String.class));
        }
        if (!prop.isOpenContent()) {
            String uri = prop.getUri();
            boolean qualified = (uri != null) && (uri.length() > 0);
            if ((qualified || _targetNamespace.length()>0)
                    && qualified != _attributeFormQualified) {
                attribute.setForm(qualified ? "qualified" : "unqualified");
            }
        }

        String xsdTypeUnp = enrichProperty(sdoName, prop, propType, false, attribute);
        if (xsdTypeUnp != null) {
            attribute.setAttributeType(xsdTypeUnp);
        }

        if (propType.isLocal()) {
            addType(propType, attribute);
        }
    }

    private void addElement(SdoProperty prop, DataObject block) {
        // Global properties don't get a maxOccurs info and also
        // not elements in sequences, because there are in a choice that has maxOccurs="unbounded"
        boolean allowMulti = !prop.isOpenContent();
        String xmlName = prop.getName();
        String sdoName = null;
        SdoType<Object> propType = (SdoType<Object>)prop.getType();
        String tmp = ((DataObject)prop).getString(PropertyType.getXmlNameProperty());
        if (tmp != null) {
            sdoName = xmlName;
            xmlName = tmp;
        }
        Element element = (Element)block.createDataObject("element");
        element.setName(xmlName);
        Object defaultValue = prop.getDefault();
        if (defaultValue != null && !defaultValue.equals(propType.getDefaultValue())) {
            element.setDefault(propType.convertToJavaClass(defaultValue, String.class));
        }
        Type containingType = prop.getContainingType();
        if (!containingType.isSequenced() && !(containingType instanceof Namespace)) {
            element.setMinOccurs(BigInteger.ZERO);
        }
        if (allowMulti) {
            if (containingType.isSequenced()) {
                if (!prop.isMany()) {
                    Property many =
                        _typeHelper.getOpenContentProperty(
                            PROP_XML_MANY.getURI(),
                            PROP_XML_MANY.getName());
                    element.setString(many, "false");
                }
            } else {
                if (prop.isMany()) {
                    element.setMaxOccurs("unbounded");
                }
            }
        }
        if (prop.isNullable()) {
            element.setNillable(true);
        }
        if (prop.isOrphanHolder()) {
            Property orphanHolder =
                _typeHelper.getOpenContentProperty(
                    PROP_XML_ORPHAN_HOLDER.getURI(),
                    PROP_XML_ORPHAN_HOLDER.getName());
            element.setString(orphanHolder, "true");
        }
        if (!prop.isOpenContent()) {
            String uri = prop.getUri();
            boolean qualified = (uri != null) && (uri.length() > 0);
            if ((qualified || _targetNamespace.length()>0) && qualified != _elementFormQualified) {
                element.setForm(qualified ? "qualified" : "unqualified");
            }
        }

        String xsdTypeUnp = enrichProperty(sdoName, prop, propType, allowMulti, element);
        if (xsdTypeUnp != null) {
            element.setElementType(xsdTypeUnp);
        }

        if (propType.isLocal()) {
            addType(propType, element);
        }
    }

    private String getAliasNames(List<String> aliases) {
        StringBuilder buf = new StringBuilder(aliases.get(0));
        for (int i=1; i<aliases.size(); ++i) {
            buf.append(' ');
            buf.append(aliases.get(i));
        }
        return buf.toString();
    }

    private String enrichProperty(String sdoName, SdoProperty prop, SdoType<Object> propType, boolean allowMulti, DataObject elementOrAttribute) {
        String xsdTypeUnp = null;
        String xsdType = ((DataObject)prop).getString(PropertyType.getXsdTypeProperty());;

        if (sdoName != null) {
            Property nameProp =
                _typeHelper.getOpenContentProperty(
                    PROP_XML_NAME.getURI(), PROP_XML_NAME.getName());
            elementOrAttribute.setString(nameProp, sdoName);
        }
        List<String> aliases = prop.getAliasNames();
        if (aliases != null && !aliases.isEmpty()) {
            Property aliasProp =
                _typeHelper.getOpenContentProperty(
                    PROP_XML_ALIAS_NAME.getURI(), PROP_XML_ALIAS_NAME.getName());
            elementOrAttribute.setString(aliasProp, getAliasNames(aliases));
        }
        if (!propType.isLocal()) {
            if (xsdType != null) {
                xsdTypeUnp = xsdType;
            }
            if (!prop.isContainment() && !propType.isDataType()) {
                if (xsdTypeUnp == null) {
                    Type keyType = propType.getKeyType();
                    if (keyType == null) {
                        xsdTypeUnp = SCHEMA_URI + "#anyURI";
                    } else if (propType.getKeyProperties().size() == 1 &&
                        SCHEMA_ID.equals(propType.getKeyProperties().get(0).getXsdType())) {
                        if (prop.isMany() && !allowMulti) {
                            xsdTypeUnp = SCHEMA_URI + "#IDREFS";
                        } else {
                            xsdTypeUnp = SCHEMA_URI + "#IDREF";
                        }
                    } else {
                        xsdTypeUnp = getUriName(keyType).toStandardSdoFormat();
                    }
                }
                Property typeProp =
                    _typeHelper.getOpenContentProperty(
                        PROP_XML_PROPERTY_TYPE.getURI(),
                        PROP_XML_PROPERTY_TYPE.getName());
                elementOrAttribute.setString(typeProp, getUriName(propType).toStandardSdoFormat());
            } else {
                if (xsdTypeUnp == null) {
                    xsdTypeUnp = getUriName(propType).toStandardSdoFormat();
                }
            }
            if (STRINGS.equalsUriName(propType)) {
                Property dataType =
                    _typeHelper.getOpenContentProperty(
                        PROP_XML_DATA_TYPE.getURI(),
                        PROP_XML_DATA_TYPE.getName());
                elementOrAttribute.setString(dataType, STRINGS.toStandardSdoFormat());
            }
        }
        if (prop.getOpposite() != null) {
            Property opposite =
                _typeHelper.getOpenContentProperty(
                    PROP_XML_OPPOSITE_PROPERTY.getURI(),
                    PROP_XML_OPPOSITE_PROPERTY.getName());
            elementOrAttribute.setString(opposite, prop.getOpposite().getName());
        }
        if (prop.isReadOnly()) {
            Property readOnly =
                _typeHelper.getOpenContentProperty(
                    PROP_XML_READ_ONLY.getURI(),
                    PROP_XML_READ_ONLY.getName());
            elementOrAttribute.setString(readOnly, "true");
        }
        if (prop.isKey() && !SCHEMA_ID.toStandardSdoFormat().equals(xsdType)) {
            if (prop.isContainment()
                    && ((SdoType<?>)prop.getContainingType()).getKeyType() == prop.getType()) {
                Property embeddedKey =
                    _typeHelper.getOpenContentProperty(
                        PROP_XML_EMBEDDED_KEY.getURI(),
                        PROP_XML_EMBEDDED_KEY.getName());
                elementOrAttribute.setString(embeddedKey, "true");
            } else {
                Property xmlKey =
                    _typeHelper.getOpenContentProperty(
                        PROP_XML_KEY.getURI(),
                        PROP_XML_KEY.getName());
                elementOrAttribute.setString(xmlKey, "true");
            }
        }
        return xsdTypeUnp;
    }

    /**
     * @param referencedType
     */
    private void addType(SdoType<?> referencedType, DataObject block) {
        if (referencedType.isDataType()) {
            addSimpleType(referencedType, block);
        } else {
            addComplexType(referencedType, block);
        }
    }

    private void addComplexType(SdoType<?> type, DataObject block) {
        _rendered.add(type);
        ComplexType complexType = (ComplexType)block.createDataObject("complexType");
        String typeName = handleXmlName(type, complexType);
        if (!type.isLocal()) {
            complexType.setName(typeName);
        }
        addAliasNames(type, complexType);
        if (type.isMixedContent()) {
            complexType.setMixed(true);
        }
        Property sequence = _typeHelper.getOpenContentProperty(
            PROP_XML_SEQUENCE.getURI(), PROP_XML_SEQUENCE.getName());
        if (type.isSequenced()) {
            complexType.setString(sequence, "true");
        } else if (type.isOpen()) {
            complexType.setString(sequence, "false");
        }
        if (type.isAbstract()) {
            complexType.setAbstract(true);
        }
        Type keyType = type.getKeyType();
        if (keyType != null) {
            List<SdoProperty> keyProperties = type.getKeyProperties();
            boolean singleKey = false;
            if (keyProperties.size() == 1) {
                SdoProperty keyProperty = keyProperties.get(0);
                if (keyProperty.getType() == keyType) {
                    singleKey = true;
                }
            }
            if (!singleKey) {
                Property keyTypeProp = _typeHelper.getOpenContentProperty(
                    PROP_XML_KEY_TYPE.getURI(), PROP_XML_KEY_TYPE.getName());
                complexType.setString(keyTypeProp, getUriName(keyType).toStandardSdoFormat());
            }
        }

        List<Type> baseTypes = type.getBaseTypes();
        Class<?> instanceClass = type.getInstanceClass();
        if (instanceClass != null && instanceClass.isInterface() && !instanceClass.equals(List.class)) {
            String name = instanceClass.getName();
            String javaName;
            Boolean mixedCase =
                (Boolean)((SapHelperContext)_helperContext).getContextOption(
                    SapHelperContext.OPTION_KEY_MIXED_CASE_JAVA_NAMES);
            if (mixedCase == null || mixedCase) {
                javaName = NameConverter.CONVERTER.toClassName(type.getName());
            } else {
                String sdoName = type.getName();
                javaName = Character.toUpperCase(sdoName.charAt(0)) + sdoName.substring(1);
            }
            if (!name.substring(name.lastIndexOf('.')+1).equals(javaName)) {
                addInstanceClass(complexType, instanceClass, baseTypes);
            }
        }

        Property valueProp = TypeHelperImpl.getSimpleContentProperty(type);
        boolean hasBaseType = baseTypes != null && baseTypes.size() > 0;
        DataObject contentContainer = complexType;
        if (hasBaseType || valueProp != null) {
            String contentType;
            String baseName;
            // Only the first baseType is used
            Type baseType = (hasBaseType ? (Type)baseTypes.get(0) : null);
            if ((baseType != null && baseType.isDataType()) || valueProp != null) {
                contentType = "simpleContent";
            } else {
                contentType = "complexContent";
            }
            if (baseType == null && valueProp != null) {
                baseName = (String)valueProp.get(PropertyType.getXsdTypeProperty());
                if (baseName == null) {
                    baseName = getUriName(valueProp.getType()).toStandardSdoFormat();
                }
            } else {
                baseName = getUriName(baseType).toStandardSdoFormat();
            }
            DataObject content = complexType.createDataObject(contentType);
            ExtensionType extension = (ExtensionType)content.createDataObject("extension");
            extension.setBase(baseName);
            contentContainer = extension;
        }

        Iterator<?> propIterator = type.getDeclaredProperties().iterator();
        List<SdoProperty> attributes = new ArrayList<SdoProperty>();
        ExplicitGroup group = null;
        while (propIterator.hasNext()) {
            SdoProperty prop = (SdoProperty)propIterator.next();
            if (!prop.isOppositeContainment()) {
                if (!prop.isXmlElement()) {
                    attributes.add(prop);
                } else if (prop != valueProp) {
                    if (group == null) {
                        if (type.isSequenced()) {
                            group = (ExplicitGroup)contentContainer.createDataObject("choice");
                            group.setMaxOccurs("unbounded");
                        } else {
                            group = (ExplicitGroup)contentContainer.createDataObject("sequence");
                        }
                    }
                    addElement(prop, group);
                }
            }
        }
        for (int i=0; i<attributes.size(); ++i) {
            addAttribute(attributes.get(i), contentContainer);
        }
        if (type.isOpen()) {
            if (group == null) {
                group = (ExplicitGroup)contentContainer.createDataObject("choice");
                group.setMaxOccurs("unbounded");
            } else if (!type.isSequenced()) {
                group = (ExplicitGroup)group.createDataObject("choice");
                group.setMaxOccurs("unbounded");
            }
            Any otherAny = (Any)group.createDataObject("any");
            otherAny.setMinOccurs(BigInteger.ZERO);
            otherAny.setNamespace(Collections.singletonList("##other"));
            otherAny.setProcessContents("lax");
            Any localAny = (Any)group.createDataObject("any");
            localAny.setMinOccurs(BigInteger.ZERO);
            localAny.setNamespace(
                Collections.singletonList(_elementFormQualified?"##local":"##targetNamespace"));
            localAny.setProcessContents("lax");

            Wildcard anyAttr = (Wildcard)contentContainer.createDataObject("anyAttribute");
            anyAttr.setNamespace(Collections.singletonList("##any"));
            anyAttr.setProcessContents("lax");
        }
    }

    private void addSimpleType(SdoType<?> type, DataObject block) {
        _rendered.add(type);
        if (type instanceof ListSimpleType) {
            //this is no real XSD type
            return;
        }
        SimpleType simpleType = (SimpleType)block.createDataObject("simpleType");
        String name = handleXmlName(type, simpleType);
        if (!type.isLocal()) {
            simpleType.setName(name);
        }
        addAliasNames(type, simpleType);


        List<Type> baseTypes = type.getBaseTypes();
        final Class<?> instanceClass = type.getInstanceClass();
        if (instanceClass != null) {
            if (baseTypes.size()==0) {
                Property instanceProp = _typeHelper.getOpenContentProperty(
                    PROP_JAVA_INSTANCE_CLASS.getURI(), PROP_JAVA_INSTANCE_CLASS.getName());
                simpleType.setString(instanceProp, instanceClass.getName());

                Restriction restriction = (Restriction)simpleType.createDataObject("restriction");
                restriction.setBase(new URINamePair(SCHEMA_URI, "string").toStandardSdoFormat());
                return;
            } else {
                addInstanceClass(simpleType, instanceClass, baseTypes);
            }
        }

        final SdoType<?> baseType = (SdoType<?>)type.getBaseTypes().get(0);

        if (baseType instanceof ListSimpleType) {
            ListSimpleType listSimpleType = (ListSimpleType)baseType;
            com.sap.sdo.api.types.schema.List list =
                (com.sap.sdo.api.types.schema.List)simpleType.createDataObject("list");
            list.setItemType(getUriName(listSimpleType.getItemType()).toStandardSdoFormat());
        } else {
            Restriction restriction = (Restriction)simpleType.createDataObject("restriction");
            restriction.setBase(getUriName(baseType).toStandardSdoFormat());
            addFacets(restriction, type);
        }
    }

    private void addAliasNames(SdoType<?> type, DataObject schemaTyPe) {
        List<String> aliases = type.getAliasNames();
        if (aliases != null && !aliases.isEmpty()) {
            Property aliasProp = _typeHelper.getOpenContentProperty(
                PROP_XML_ALIAS_NAME.getURI(), PROP_XML_ALIAS_NAME.getName());
            schemaTyPe.setString(aliasProp, getAliasNames(aliases));
        }
    }

    private String handleXmlName(SdoType<?> type, DataObject schemaType) {
        String name = type.getName();
        String xmlName = type.getXmlName();
        if (type.isLocal()
                || (xmlName != null && xmlName.length() > 0 && !xmlName.equals(name))) {
            Property nameProp = _typeHelper.getOpenContentProperty(
                PROP_XML_NAME.getURI(), PROP_XML_NAME.getName());
            schemaType.setString(nameProp, name);
            return xmlName;
        }
        return name;
    }

    private void addFacets(Restriction restriction, Type type) {
        if (!((DataObject)type).isSet(TypeType.FACETS)) {
            return;
        }
        DataObject object = ((DataObject)type).getDataObject(TypeType.FACETS);
        if (object.isSet(TypeType.FACET_MAXLENGTH)) {
            NumFacet maxLength = (NumFacet)restriction.createDataObject("maxLength");
            maxLength.setValue(object.get(TypeType.FACET_MAXLENGTH).toString());
        }
        if (object.isSet(TypeType.FACET_MINLENGTH)) {
            NumFacet minLength = (NumFacet)restriction.createDataObject("minLength");
            minLength.setValue(object.get(TypeType.FACET_MINLENGTH).toString());
        }
        if (object.isSet(TypeType.FACET_LENGTH)) {
            NumFacet length = (NumFacet)restriction.createDataObject("length");
            length.setValue(object.get(TypeType.FACET_LENGTH).toString());
        }
        for (Object o:object.getList(TypeType.FACET_ENUMERATION)) {
            NoFixedFacet enumeration = (NoFixedFacet)restriction.createDataObject("enumeration");
            enumeration.setValue(o.toString());
        }
        if (object.isSet(TypeType.FACET_MAXINCLUSIVE)) {
            Facet maxInclusive = (Facet)restriction.createDataObject("maxInclusive");
            maxInclusive.setValue(object.get(TypeType.FACET_MAXINCLUSIVE).toString());
        }
        if (object.isSet(TypeType.FACET_MININCLUSIVE)) {
            Facet minInclusive = (Facet)restriction.createDataObject("minInclusive");
            minInclusive.setValue(object.get(TypeType.FACET_MININCLUSIVE).toString());
        }
        if (object.isSet(TypeType.FACET_MAXEXCLUSIVE)) {
            Facet maxExclusive = (Facet)restriction.createDataObject("maxExclusive");
            maxExclusive.setValue(object.get(TypeType.FACET_MAXEXCLUSIVE).toString());
        }
        if (object.isSet(TypeType.FACET_MINEXCLUSIVE)) {
            Facet minExclusive = (Facet)restriction.createDataObject("minExclusive");
            minExclusive.setValue(object.get(TypeType.FACET_MINEXCLUSIVE).toString());
        }
        for (Object o:object.getList(TypeType.FACET_PATTERN)) {
            Pattern pattern = (Pattern)restriction.createDataObject("pattern");
            pattern.setValue(o.toString());
        }
        if (object.isSet(TypeType.FACET_TOTALDIGITS)) {
            TotalDigits totalDigits = (TotalDigits)restriction.createDataObject("totalDigits");
            totalDigits.setValue(object.get(TypeType.FACET_TOTALDIGITS).toString());
        }
        if (object.isSet(TypeType.FACET_FRACTIONDIGITS)) {
            NumFacet fractionDigits = (NumFacet)restriction.createDataObject("fractionDigits");
            fractionDigits.setValue(object.get(TypeType.FACET_FRACTIONDIGITS).toString());
        }
    }

    private void addInstanceClass(DataObject type, Class<?> instanceClass, List<Type> baseTypes) {
        boolean needInstanceClass = true;
        if (baseTypes != null && baseTypes.size()>0) {
            // Only the first baseType is used
            Type baseType = baseTypes.get(0);
            Class<?> baseClass = baseType.getInstanceClass();
            if (baseClass==instanceClass) {
                needInstanceClass = false;
            } else if (baseClass != null && baseClass.isAssignableFrom(instanceClass)) {
                Property extendedInstanceClass = _typeHelper.getOpenContentProperty(
                    PROP_JAVA_EXTENDED_INSTANCE_CLASS.getURI(), PROP_JAVA_EXTENDED_INSTANCE_CLASS.getName());
                type.setString(extendedInstanceClass, instanceClass.getName());
                needInstanceClass = false;
            }
        }
        if (needInstanceClass) {
            Property instanceProp = _typeHelper.getOpenContentProperty(
                PROP_JAVA_INSTANCE_CLASS.getURI(), PROP_JAVA_INSTANCE_CLASS.getName());
            type.setString(instanceProp, instanceClass.getName());
        }
    }

    private URINamePair getUriName(Type aType) {
        String uri = aType.getURI();
        String name = aType.getName();
        URINamePair xsdName = SchemaTypeFactory.getInstance().getXsdName(uri, name);
        if (xsdName == null) {
            _referenced.add((SdoType<?>)aType);
            xsdName = new URINamePair(uri, name);
        }
        String xsdUri = xsdName.getURI();
        if (!_targetNamespace.equals(xsdUri) && !DATATYPE_URI.equals(xsdUri) && !SCHEMA_URI.equals(xsdUri)) {
            boolean insert = true;
            for (int i=0; i<_imports.size(); ++i) {
                if (_imports.get(i).getNamespace().equals(xsdUri)) {
                    insert = false;
                    break;
                }
            }
            if (insert) {
                Import imp = (Import)_helperContext.getDataFactory().create(Import.class);
                imp.setNamespace(xsdUri);
                _imports.add(imp);
            }
        }
        return xsdName;
    }
}
