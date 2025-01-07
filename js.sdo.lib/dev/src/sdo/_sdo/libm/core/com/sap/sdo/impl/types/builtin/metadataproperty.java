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


import java.util.Collections;
import java.util.List;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.simple.JavaSimpleType;

import commonj.sdo.Property;
import commonj.sdo.Type;
/**
 * Built-in type representing the {commonj.sdo}Property interface.  In this implementation,
 * instances of commonj.sdo.Property will also be DataObjects of type {commonj.sdo}Property.
 */
public class MetaDataProperty extends MetaDataObject
{
    private static final long serialVersionUID = -1410865434728653812L;

    private static final int XML_ELEMENT = 13;
    private static final int ORPHAN_HOLDER = 14;
    private static final int XSD_TYPE = 15;

    private String _name;
    private Type _type;
    private transient Type _containmentType;
    private boolean _readOnly = false;
    private boolean _many = false;
    private boolean _containment = false;
    private Object _def;
    private List _aliasNames;
    private boolean _nullable;
    private boolean _key;

    private boolean _xmlElement;
    private boolean _orphanHolder;


	private String _javaName;
    private Class<?> _javaClass;
    private URINamePair _xsdType;

    public MetaDataProperty(
            String name,
            String javaName,
            Type type,
            Type containmentType,
            boolean readOnly,
            boolean many,
            boolean containment,
            boolean xmlElement,
            boolean orphanHolder,
            URINamePair xsdType,
            Object def) {
        super();
        _type = type;
        _javaName = javaName;
        _containment = containment;
        _containmentType = containmentType;
        _def = def;
        if (def == null && type==JavaSimpleType.BOOLEAN) {
        	_def = Boolean.FALSE;
        }
        _many = many;
        _name = name;
        if (name==null) {
            throw new NullPointerException("_name parameter must be non-null");
        }
        _aliasNames = Collections.EMPTY_LIST;
        _readOnly = readOnly;
        _nullable = type.isDataType() && !many;
        _xmlElement = xmlElement;
        _orphanHolder = orphanHolder;
        _xsdType = xsdType;
    }

    public MetaDataProperty(
            String name,
            Type type,
            Type containmentType,
            boolean readOnly,
            boolean many,
            boolean containment,
            boolean xmlElement) {
        this(name,null,type,containmentType,readOnly,many,containment,xmlElement,false,null,null);
    }

    public MetaDataProperty(
            String name,
            String javaName,
            Type type,
            Type containmentType,
            boolean readOnly,
            boolean many,
            boolean containment,
            boolean xmlElement) {
        this(name,javaName,type,containmentType,readOnly,many,containment,xmlElement,false,null,null);
    }

    public MetaDataProperty(
            String name,
            Type type,
            Type containmentType,
            boolean readOnly,
            boolean many,
            boolean containment,
            boolean xmlElement,
            boolean orphanHolder) {
        this(name,null,type,containmentType,readOnly,many,containment,xmlElement,orphanHolder,null,null);
    }

    @Override
    public Type getType() {
        return PropertyType.getInstance();
    }

    @Override
    public String toString() {
        return this._name+" ("+_type+")";
    }
    public String getJavaName() {
    	return _javaName;
    }
	public List getInstanceProperties() {
		return PropertyType.getInstance().getDeclaredProperties();
	}
	public Object get(int i) {
		switch (i) {
		case PropertyType.ALIAS_NAME:
			return _aliasNames;
		case PropertyType.CONTAINING_TYPE:
			return _containmentType;
		case PropertyType.CONTAINMENT:
			return _containment;
		case PropertyType.DEFAULT:
			return _def;
		case PropertyType.MANY:
			return _many;
		case PropertyType.NAME:
			return _name;
		case PropertyType.READ_ONLY:
			return _readOnly;
		case PropertyType.TYPE:
			return _type;
        case PropertyType.JAVA_CLASS:
            return _javaClass;
        case PropertyType.NULLABLE:
            return _nullable;
        case PropertyType.OPPOSITE:
            return null;
        case PropertyType.KEY:
            return _key;
        case XML_ELEMENT:
            return _xmlElement;
        case ORPHAN_HOLDER:
            return _orphanHolder;
        case XSD_TYPE:
            return _xsdType.toStandardSdoFormat();
		default:
			return null;
		}
	}
	public Object get(Property property) {
		return get(getPropertyIndex(property));
	}
	public Property getInstanceProperty(String propertyName) {
		return PropertyType.getInstance().getInstanceProperty(propertyName);
	}
	public Property getProperty(String propertyName) {
		return getInstanceProperty(propertyName);
	}
	public Object get(String path) {
		return get(getInstanceProperty(path));
	}
	public boolean defined() {
		return true;
	}

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.builtin.MetaDataObject#isSet(int)
     */
    @Override
    public boolean isSet(int propertyIndex) {
        return propertyIndex >= 0 && propertyIndex <= ORPHAN_HOLDER;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.builtin.MetaDataObject#getPropertyIndex(commonj.sdo.Property)
     */
    @Override
    protected int getPropertyIndex(Property property) {
        if (property == PropertyType.getXmlElementProperty()) {
            return XML_ELEMENT;
        }
        if (property == PropertyType.getOrphanHolderProperty()) {
            return ORPHAN_HOLDER;
        }
        return super.getPropertyIndex(property);
    }

    String getName() {
        return _name;
    }

    Type getContainingType() {
        return _containmentType;
    }

    boolean isReadOnly() {
        return _readOnly;
    }

    boolean isMany() {
        return _many;
    }

    boolean isContainment() {
        return _containment;
    }

    Property getOpposite() {
        return null;
    }

    List getAliasNames() {
        return _aliasNames;
    }

    boolean isNullable() {
        return _nullable;
    }

    boolean isKey() {
        return _key;
    }

    boolean isXmlElement() {
        return _xmlElement;
    }

    boolean isOrphanHolder() {
        return _orphanHolder;
    }

    Class<?> getJavaClass() {
        return _javaClass;
    }
    
    Type getPropType() {
        return _type;
    }

    Object getDefault() {
        return _def;
    }

    URINamePair getXsdType() {
        return _xsdType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object pObj) {
        if (pObj == this) {
            return true;
        }
        if (pObj instanceof MetaDataProperty) {
            return false;
        }
        if (pObj != null) {
            pObj.equals(this);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
