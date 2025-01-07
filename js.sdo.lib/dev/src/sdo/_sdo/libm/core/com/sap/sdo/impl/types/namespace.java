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

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.HelperContextImpl;
import com.sap.sdo.impl.xml.TypesComparator;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public class Namespace implements Type, Serializable {
    private static final long serialVersionUID = 4835347926603290609L;
    private final HelperContextImpl _helperContext;
    private final String _uri;
	private final transient Map<String, TypeAndContext> _nameToType = new HashMap<String,TypeAndContext>(); 
	private final transient Map<String, SdoProperty> _nameToProperty = new HashMap<String,SdoProperty>();
    private transient final Map<XmlPropKey,SdoProperty> _xmlPropNameToProperty = new HashMap<XmlPropKey,SdoProperty>();
    private transient final Map<String,SdoType> _xmlTypeNameToType = new HashMap<String,SdoType>();
	public Namespace(final String uri, HelperContextImpl pHelperContext) {
        _helperContext = pHelperContext;
		_uri = uri;
	}
	public String getURI() {
		return _uri;
	}
    
	TypeAndContext getTypeAndContext(final String name) {
        TypeAndContext typeAndContext = _nameToType.get(name);
        if (typeAndContext == null) {
            Namespace parent = getParent();
            if (parent != null) {
                typeAndContext = parent.getTypeAndContext(name);
                if (typeAndContext != null) {
                    typeAndContext = put(typeAndContext.getSdoType());
                }
            }
        }
        return typeAndContext;
	}
    
	TypeAndContext put(final SdoType pType) {
        TypeAndContext typeAndContext = 
            pType.getHelperContext()==_helperContext?pType:new TypeAndContextPair(pType, _helperContext);
        final String name = pType.getName();
		putBySdoName(name, typeAndContext);
        List<String> aliasNames = pType.getAliasNames();
        for (int i = 0; i < aliasNames.size(); i++) {
            String alias = aliasNames.get(i);
            if (getTypeAndContext(alias) == null) {
                putBySdoName(alias, typeAndContext);
            }
        }
        String xmlName = pType.getXmlName();
        putByXmlName(xmlName, pType);
        return typeAndContext;
	}
    
    void putBySdoName(final String pSdoName, final TypeAndContext pTypeAndContext) {
        _nameToType.put(pSdoName,pTypeAndContext);
    }
    
    void putByXmlName(String pXmlName, final SdoType pType) {
        _xmlTypeNameToType.put(pXmlName, pType);
    }
    
	Collection<SdoType> getTypes() {
        HashSet<SdoType> typeSet = new HashSet<SdoType>(_nameToType.size());
        for (TypeAndContext typeAndContext: _nameToType.values()) {
            typeSet.add(typeAndContext.getSdoType());
        }
        final ArrayList<SdoType> result = new ArrayList<SdoType>(typeSet);
        Namespace parent = getParent();
        if (parent != null) {
            result.addAll(parent.getTypes());
        }
        return result;
	}
    
	public SdoProperty getProperty(final String name) {
		SdoProperty property = _nameToProperty.get(name);
        if (property == null) {
            Namespace parent = getParent();
            if (parent != null) {
                property = parent.getProperty(name);
                if (property != null) {
                    put(property);
                }
            }
        }
        return property;
	}
    
	void put(final SdoProperty prop) {
        String name = prop.getName();
		if (prop.isXmlElement()) {
            putBySdoName(name, prop);
        } else {
		    putBySdoName('@'+name, prop);
		    SdoProperty element = _nameToProperty.get(name);
		    if (element == null || !element.isXmlElement()) {
	            putBySdoName(name, prop);
		    }
		}
        List<String> aliasNames = prop.getAliasNames();
        for (int i = 0; i < aliasNames.size(); i++) {
            String aliasName = aliasNames.get(i);
            if (!_nameToProperty.containsKey(aliasName)) {
                putBySdoName(aliasName, prop);
            }
        }
        XmlPropKey propKey = new XmlPropKey(prop);
        putByXmlName(propKey, prop);
	}
    
    private void putByXmlName(XmlPropKey pPropKey, final SdoProperty pProp) {
        _xmlPropNameToProperty.put(pPropKey, pProp);
    }
    
    void putBySdoName(String pName, final SdoProperty pProp) {
        _nameToProperty.put(pName, pProp);
    }
    
	public List getDeclaredProperties() {
		final ArrayList<Property> result = new ArrayList<Property>(
                new HashSet<Property>(_nameToProperty.values()));
		Namespace parent = getParent();
        if (parent != null) {
            result.addAll(parent.getDeclaredProperties());
        }
        return result;
	}
	public String getName() {
		return null;
	}
	public Class getInstanceClass() {
		return null;
	}
	public boolean isInstance(final Object object) {
		return false;
	}
	public List getProperties() {
		return getDeclaredProperties();
	}
	public boolean isDataType() {
		return false;
	}
	public boolean isOpen() {
		return false;
	}
	public boolean isSequenced() {
		return false;
	}
	public boolean isAbstract() {
		return false;
	}
	public List getBaseTypes() {
		return Collections.EMPTY_LIST;
	}
	public List getAliasNames() {
		return Collections.EMPTY_LIST;
	}
	public List getInstanceProperties() {
		return Collections.EMPTY_LIST;
	}
	public Object get(Property property) {
		return null;
	}
    
    public SdoProperty getPropertyFromXmlName(final String pXmlName, boolean pIsElement) {
        return getPropertyFromXmlName(new XmlPropKey(pXmlName, pIsElement));
    }
    
    private SdoProperty getPropertyFromXmlName(final XmlPropKey pPropKey) {
        SdoProperty property = _xmlPropNameToProperty.get(pPropKey);
        if (property == null) {
            Namespace parent = getParent();
            if (parent != null) {
                property = parent.getPropertyFromXmlName(pPropKey);
                if (property != null) {
                    put(property);
                }
            }
        }
        return property;
    }
    
    public SdoType getTypeFromXmlName(final String pXmlName) {
        SdoType type = _xmlTypeNameToType.get(pXmlName);
        if (type == null) {
            Namespace parent = getParent();
            if (parent != null) {
                type = parent.getTypeFromXmlName(pXmlName);
                if (type != null && _uri.equals(type.getURI())) {
                    put(type);
                }
            }
        }
        return type;
    }
    
    private Namespace getParent() {
        HelperContextImpl parentContext = _helperContext.getParent();
        if (parentContext != null) {
            TypeHelperImpl parentTypeHelper = (TypeHelperImpl)parentContext.getTypeHelper();
            return parentTypeHelper.getNamespace(getURI());
        }
        return null;
    }
    
    private Object readResolve() throws ObjectStreamException {
        final TypeHelperImpl typeHelper = (TypeHelperImpl)_helperContext.getTypeHelper();
        Namespace namespace = typeHelper.getNamespace(_uri);
        if (namespace == null) {
            throw new InvalidObjectException("Namespace " + _uri + 
                " is unknown in HelperContext with id " + _helperContext.getId());
        }
        return namespace;
    }
    
    private static class XmlPropKey {
        private final  String _name;
        private final  boolean _isElement;
        private final  int hash;
        
        public XmlPropKey(SdoProperty pProperty) {
            _name = pProperty.getXmlName();
            _isElement = pProperty.isXmlElement();
            hash = _name.hashCode() ^ Boolean.valueOf(_isElement).hashCode();
        }
        public XmlPropKey(String pName, boolean pIsElement) {
            _name = pName;
            _isElement = pIsElement;
            hash = _name.hashCode() ^ Boolean.valueOf(_isElement).hashCode();
        }
        @Override
        public boolean equals(Object pObj) {
            if (this == pObj) {
                return true;
            }
            if (!(pObj instanceof XmlPropKey)) {
                return false;
            }
            XmlPropKey other = (XmlPropKey)pObj;
            return (_isElement == other._isElement) && _name.equals(other._name);
        }
        @Override
        public int hashCode() {
            return hash;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (_isElement) {
                sb.append("element: ");
            } else {
                sb.append("attribute: ");
            }
            sb.append(_name);
            return sb.toString();
        }
        
        
    }
	public HelperContext getHelperContext() {
		return _helperContext;
	}
    
    void fillNamespaceDO(com.sap.sdo.api.types.ctx.Namespace pNamespaceDO) {
        pNamespaceDO.setUri(getURI());
        Comparator<SdoProperty> comparator = new Comparator<SdoProperty>() {
            public int compare(SdoProperty p1, SdoProperty p2) {
                if (p1 == p2) {
                    return 0;
                }
                int result = p1.getName().compareTo(p2.getName());
                if (result == 0) {
                    if (!p1.isXmlElement() && p2.isXmlElement()) {
                        return -1;
                    }
                    if (p1.isXmlElement() && !p2.isXmlElement()) {
                        return 1;
                    }
                }
                return result;
            }
        };
        Collection<SdoProperty> properties = new TreeSet<SdoProperty>(comparator);
        properties.addAll(_nameToProperty.values());
        Namespace parent = getParent();
        if (parent != null) {
            properties.removeAll(parent.getProperties());
        }
        List<Schema> schemas = new ArrayList<Schema>();
        Property schemaRefProp = _helperContext.getTypeHelper().getOpenContentProperty(URINamePair.CTX_URI, TypeConstants.SCHEMA_REFERENCE);
        for (Property property: properties) {
            pNamespaceDO.getProperty().add(property);
            DataObject schemaRef = (DataObject)property.get(schemaRefProp);
            addSchema(schemas, schemaRef);
        }
        Collection<SdoType> types = new TreeSet<SdoType>(TypesComparator.INSTANCE);
        for (TypeAndContext typeAndContext: _nameToType.values()) {
            types.add(typeAndContext.getSdoType());
        }
        if (parent != null) {
            types.removeAll(parent.getTypes());
        }
        for (Type type: types) {
            pNamespaceDO.getNamespaceType().add(type);
            DataObject schemaRef = (DataObject)type.get(schemaRefProp);
            addSchema(schemas, schemaRef);
        }
        if (!schemas.isEmpty()) {
            Property schemaProp = _helperContext.getTypeHelper().getOpenContentProperty(URINamePair.PROP_SCHEMA_SCHEMA.getURI(), URINamePair.PROP_SCHEMA_SCHEMA.getName());
            List schemaElements = ((DataObject)pNamespaceDO).getList(schemaProp);
            schemaElements.addAll(schemas);
        }
    }
    
    void removeType(SdoType pType) {
        _nameToType.remove(pType.getName());
        for (String alias: (List<String>)pType.getAliasNames()) {
            TypeAndContext typeAndContext = _nameToType.get(alias);
            if (typeAndContext != null && typeAndContext.getSdoType() == pType) {
                _nameToType.remove(alias);
            }
        }
        _xmlTypeNameToType.remove(pType.getXmlName());
    }
    
    void removeProperty(SdoProperty pProperty) {
        String name = pProperty.getName();
        if (_nameToProperty.get(name) == pProperty) {
            _nameToProperty.remove(name);
        }
        if (!pProperty.isXmlElement()) {
            _nameToProperty.remove('@'+name);
        }
        for (String alias: (List<String>)pProperty.getAliasNames()) {
            if (_nameToProperty.get(alias) == pProperty) {
                _nameToProperty.remove(alias);
            }
        }
        _xmlPropNameToProperty.remove(new XmlPropKey(pProperty));
    }
    
    private void addSchema(List<Schema> pSchemas, DataObject pSchemaRef) {
        if (pSchemaRef != null) {
            Schema schema = getSchema(pSchemaRef);
            if (!pSchemas.contains(schema)) {
                pSchemas.add(schema);
            }
        }
    }
    
    private Schema getSchema(DataObject pSchemaRef) {
        if (URINamePair.SCHEMA_SCHEMA.equalsUriName(pSchemaRef.getType())) {
            return (Schema)pSchemaRef;
        }
        return getSchema(pSchemaRef.getContainer());
    }
    
    public static class TypeAndContextPair implements TypeAndContext {
        
        private HelperContext _helperContext;
        private SdoType _sdoType;

        private TypeAndContextPair(SdoType pSdoType, HelperContext pHelperContext) {
            _sdoType = pSdoType;
            _helperContext = pHelperContext;
        }

        public HelperContext getHelperContext() {
            return _helperContext;
        }

        public SdoType getSdoType() {
            return _sdoType;
        }

    }

}