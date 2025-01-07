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
package com.sap.sdo.impl.types.builtin;

import static com.sap.sdo.api.util.URINamePair.DATATYPE_JAVA_URI;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_XML_URI;

import java.util.ArrayList;

import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.types.ctx.Facets;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.objects.IHasDelegator;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.simple.JavaSimpleType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;


public class TypeType extends MetaDataType<Type> implements IHasDelegator, TypeConstants
{
    public static char CHAR_ATTRIBUTE = '@';
    public static char CHAR_ELEMENT = '+';
    public static char CHAR_TYPE = '*';
    // paragraph sign
    public static char CHAR_GROUP = '\u00A7';
    public static char CHAR_FORM = '~';

    private static final long serialVersionUID = -4607620790873614677L;
    private static TypeType _instance = new TypeType();

    public static TypeType getInstance() {
        return _instance;
    }
    @Override
    public Object readResolve() {
        return getInstance();
    }
    private TypeType() {
        _instance = this;
        setUNP(URINamePair.TYPE);
        SdoProperty[] propsa = new SdoProperty[] {
            new MetaDataPropertyLogicFacade(new MetaDataProperty(BASE_TYPE_STR,"baseTypes",this,this,true,true,false,true)),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(PROPERTY_STR,"declaredProperties",PropertyType.getInstance(),this,true,true,true,true,false,null,new ArrayList())),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(ALIAS_NAME_STR,"aliasNames",JavaSimpleType.STRING,this,true,true,false,true)),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(NAME_STR,JavaSimpleType.STRING,this,true,false,false,false), TypeHelperImpl.getNameCache()),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(URI_STR,"uRI",JavaSimpleType.STRING,this,true,false,false,false), TypeHelperImpl.getUriCache()),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(DATA_TYPE_STR,JavaSimpleType.BOOLEAN,this,true,false,false,false)),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(OPEN_STR,JavaSimpleType.BOOLEAN,this,true,false,false,false)),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(SEQUENCED_STR,JavaSimpleType.BOOLEAN,this,true,false,false,false)),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(ABSTRACT_STR,JavaSimpleType.BOOLEAN,this,true,false,false,false)),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(KEY_TYPE_STR,this,this,true,false,false,false)),
        };
        setDeclaredProperties(propsa);
        setOpen(true);
        setSequenced(false);
        setInstanceClass(Type.class);
        useCache();
    }
    @Override
    public Type convertFromJavaClass(final Object data) {
        if (data==null) {
            return null;
        }
        if (data instanceof Type) {
            return (Type)data;
        }
        return super.convertFromJavaClass(data);
    }
    static class FacetsType extends MetaDataType<Facets> {
        private static final long serialVersionUID = 2130997949545403253L;

        private FacetsType() {
            _facetsType = this;
            setUNP(URINamePair.FACETS);
            setDeclaredProperties(new SdoProperty[] {
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_LENGTH, JavaSimpleType.INTOBJECT,this,false,false,false,false)),
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_MAXLENGTH, JavaSimpleType.INTOBJECT,this,false,false,false,false)),
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_MINLENGTH, JavaSimpleType.INTOBJECT,this,false,false,false,false)),
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_ENUMERATION, JavaSimpleType.STRING,this,false,true,false,true)),
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_MININCLUSIVE, JavaSimpleType.STRING,this,false,false,false,false)),
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_MAXINCLUSIVE, JavaSimpleType.STRING,this,false,false,false,false)),
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_MINEXCLUSIVE, JavaSimpleType.STRING,this,false,false,false,false)),
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_MAXEXCLUSIVE, JavaSimpleType.STRING,this,false,false,false,false)),
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_PATTERN, JavaSimpleType.STRING,this,false,true,false,true)),
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_TOTALDIGITS, JavaSimpleType.INTOBJECT,this,false,false,false,false)),
                new MetaDataPropertyLogicFacade(new MetaDataProperty(FACET_FRACTIONDIGITS, JavaSimpleType.INTOBJECT,this,false,false,false,false))
            });
            setOpen(false);
            setSequenced(false);
            setInstanceClass(Facets.class);
        }
        @Override
        public Object readResolve() {
            return TypeType.getFacetsType();
        }
    }

    private static FacetsType _facetsType = new FacetsType();
    public static SdoType getFacetsType() {
        return _facetsType;
    }

    private static Property _facetsProperty;
    public static synchronized Property getFacetsProperty() {
        if (_facetsProperty == null) {
            DataObject prop = SapHelperProviderImpl.getCoreContext().getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
            prop.set(PropertyType.NAME,FACETS);
            prop.set(PropertyType.TYPE, getFacetsType());
            prop.set(PropertyType.CONTAINMENT,true);
            prop.set(PropertyType.MANY, false);
            _facetsProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().defineOpenContentProperty(URINamePair.CTX_URI, prop);
        }
        return _facetsProperty;
    }

    private static Property _schemaReferenceProperty;
    public static synchronized Property getSchemaReferenceProperty() {
        if (_schemaReferenceProperty == null) {
            _schemaReferenceProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(URINamePair.CTX_URI, SCHEMA_REFERENCE);
        }
        return _schemaReferenceProperty;
    }

    private static Property _packageProperty;
    public static synchronized Property getPackageProperty() {
        if (_packageProperty == null) {
            DataObject prop = SapHelperProviderImpl.getCoreContext().getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
            prop.set(PropertyType.NAME, PACKAGE);
            prop.set(PropertyType.TYPE, SapHelperProviderImpl.getCoreContext().getTypeHelper().getType(String.class));
            prop.set(PropertyType.CONTAINMENT, false);
            _packageProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().defineOpenContentProperty(null, prop);
        }
        return _packageProperty;
    }

    private static Property _elementFormDefaultProperty;
    public static Property getElementFormDefaultProperty() {
        if (_elementFormDefaultProperty == null) {
            _elementFormDefaultProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, ELEMENT_FORM_DEFAULT);
        }
        return _elementFormDefaultProperty;
    }

    private static Property _attributeFormDefaultProperty;
    public static Property getAttributeFormDefaultProperty() {
        if (_attributeFormDefaultProperty == null) {
            _attributeFormDefaultProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, ATTRIBUTE_FORM_DEFAULT);
        }
        return _attributeFormDefaultProperty;
    }

    private static Property _mixedProperty;
    public static Property getMixedProperty() {
        if (_mixedProperty == null) {
            _mixedProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, MIXED);
        }
        return _mixedProperty;
    }

    private static Property _xmlNameProperty;
    public static Property getXmlNameProperty() {
        if (_xmlNameProperty == null) {
            _xmlNameProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, XML_NAME);
        }
        return _xmlNameProperty;
    }

    private static Property _javaClassProperty;
    public static Property getJavaClassProperty() {
        if (_javaClassProperty == null) {
            _javaClassProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_JAVA_URI, JAVA_CLASS);
        }
        return _javaClassProperty;
    }

    private static Property _specialBaseTypeProperty;
    public static Property getSpecialBaseTypeProperty() {
        if (_specialBaseTypeProperty == null) {
            DataObject prop = SapHelperProviderImpl.getCoreContext().getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
            prop.set(PropertyType.NAME, SPECIAL_BASE_TYPE);
            prop.set(PropertyType.TYPE, SapHelperProviderImpl.getCoreContext().getTypeHelper().getType(String.class));
            prop.set(PropertyType.CONTAINMENT, false);
            _specialBaseTypeProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().defineOpenContentProperty(null, prop);
        }
        return _specialBaseTypeProperty;
    }

    public Class<? extends DataObject> getFacadeClass() {
        return TypeLogicFacade.class;
    }
    @Override
    public boolean getElementFormDefaultQualified() {
        return true;
    }
}
