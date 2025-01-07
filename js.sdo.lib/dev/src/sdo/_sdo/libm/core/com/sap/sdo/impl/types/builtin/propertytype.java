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
import static com.sap.sdo.api.util.URINamePair.PROPERTY;
import static com.sap.sdo.api.util.URINamePair.PROP_CTX_SCHEMA_REFERENCE;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_ORPHAN_HOLDER;

import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.IHasDelegator;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.simple.JavaSimpleType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

public class PropertyType extends MetaDataType<Property> implements IHasDelegator, PropertyConstants
{
    private static final long serialVersionUID = -8797310995242777888L;
	private static PropertyType _instance = new PropertyType();

	public static PropertyType getInstance() {
		return _instance;
	}
	@Override
    public Object readResolve() {
		return getInstance();
	}
	private PropertyType() {
        _instance = this;
        setUNP(PROPERTY);
		setDeclaredProperties(new SdoProperty[] {
			new MetaDataPropertyLogicFacade(new MetaDataProperty(ALIAS_NAME_STR,"aliasNames",JavaSimpleType.STRING,this,false,true,false,true)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(NAME_STR,JavaSimpleType.STRING,this,true,false,false,false), TypeHelperImpl.getNameCache()),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(MANY_STR,JavaSimpleType.BOOLEAN,this,true,false,false,false)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(CONTAINMENT_STR,JavaSimpleType.BOOLEAN,this,true,false,false,false)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(DEFAULT_STR,JavaSimpleType.OBJECT,this,true,false,false,false)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(READ_ONLY_STR,JavaSimpleType.BOOLEAN,this,true,false,false,false)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(TYPE_STR,TypeType.getInstance(),this,true,false,false,false)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(OPPOSITE_STR,OPPOSITE_STR,this,this,false,false,false,false)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(NULLABLE_STR, JavaSimpleType.BOOLEAN,this,true,false,false,false)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(CONTAINING_TYPE_STR,TypeType.getInstance(),this,true,false,false,false)),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(KEY_STR, JavaSimpleType.BOOLEAN,this,true,false,false,false)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty("_"+OPPOSITE_STR,OPPOSITE_STR+"Name",JavaSimpleType.STRING,this,false,false,false,false), TypeHelperImpl.getNameCache()),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(JAVA_CLASS_STR,JavaSimpleType.CLASS,this,true,false,false,false))
		});
		setOpen(true);
		setSequenced(false);
		setInstanceClass(Property.class);
		useCache();
	}

    @Override
    public Property convertFromJavaClass(final Object data) {
        if (data==null) {
            return null;
        }
        if (data instanceof Property) {
        	return (Property)data;
        }
        if (data instanceof DataObjectDecorator) {
            DataObjectDecorator facade = ((DataObjectDecorator)data).getInstance().getFacade();
            if (facade instanceof Property) {
                return (Property)facade;
            }
        }
        throw new ClassCastException("Can not convert from " + data.getClass().getName() +
            " to " + getInstanceClass().getName());
    }
	public Class<PropertyLogicFacade> getFacadeClass() {
		return PropertyLogicFacade.class;
	}

    @Override
    public boolean getElementFormDefaultQualified() {
        return true;
    }

    private static Property _substitutesProperty;
	public static Property getSubstitutesProperty() {
        if (_substitutesProperty == null) {
            _substitutesProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, SUBSTITUTES);
        }
		return _substitutesProperty;
	}

    private static Property _referenceProperty;
	public static Property getReferenceProperty() {
        if (_referenceProperty == null) {
            _referenceProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, REF);
        }
        return _referenceProperty;
	}

    private static Property _xmlElementProperty;
    public static Property getXmlElementProperty() {
        if (_xmlElementProperty == null) {
            _xmlElementProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, XML_ELEMENT);
        }
        return _xmlElementProperty;
	}

    private static Property _xmlNameProperty;
	public static Property getXmlNameProperty() {
        if (_xmlNameProperty == null) {
            _xmlNameProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, XML_NAME);
        }
        return _xmlNameProperty;
	}

    private static Property _uriProperty;
    public static Property getUriProperty() {
        if (_uriProperty == null) {
            _uriProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, URI);
        }
        return _uriProperty;
    }

    private static Property _xsdTypeProperty;
	public static Property getXsdTypeProperty() {
        if (_xsdTypeProperty == null) {
            _xsdTypeProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, XSD_TYPE);
        }
        return _xsdTypeProperty;
	}

    private static Property _simpleContentProperty;
    public static Property getSimpleContentProperty() {
        if (_simpleContentProperty == null) {
            _simpleContentProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, XML_SIMPLE_CONTENT);
        }
        return _simpleContentProperty;
	}

    private static Property _formQualifiedProperty;
    public static Property getFormQualifiedProperty() {
        if (_formQualifiedProperty == null) {
            _formQualifiedProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, FORM_QUALIFIED);
        }
        return _formQualifiedProperty;
    }

    private static Property _javaNameProperty;
	public static Property getJavaNameProperty() {
        if (_javaNameProperty == null) {
            _javaNameProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_JAVA_URI, JAVA_NAME);
        }
        return _javaNameProperty;
	}
    private static Property _javaFieldProperty;
	public static Property getJavaFieldProperty() {
        if (_javaFieldProperty == null) {
            _javaFieldProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(DATATYPE_JAVA_URI, IS_FIELD);
        }
        return _javaFieldProperty;
	}
    private static Property _schemaReferenceProperty;
    public static Property getSchemaReferenceProperty() {
        if (_schemaReferenceProperty == null) {
            _schemaReferenceProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(PROP_CTX_SCHEMA_REFERENCE.getURI(), PROP_CTX_SCHEMA_REFERENCE.getName());
        }
        return _schemaReferenceProperty;
    }

    private static Property _manyUnknownProperty;
    public static synchronized Property getManyUnknownProperty() {
        if (_manyUnknownProperty == null) {
            _manyUnknownProperty = new MetaDataPropertyLogicFacade(new MetaDataProperty(
                "manyUnknown", JavaSimpleType.BOOLEAN, null, false, false, false, false));
        }
        return _manyUnknownProperty;
    }

    private static Property _orphanHolderProperty;
    public static Property getOrphanHolderProperty() {
        if (_orphanHolderProperty == null) {
            _orphanHolderProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper().getOpenContentProperty(PROP_XML_ORPHAN_HOLDER.getURI(), PROP_XML_ORPHAN_HOLDER.getName());
        }
        return _orphanHolderProperty;
    }

}
