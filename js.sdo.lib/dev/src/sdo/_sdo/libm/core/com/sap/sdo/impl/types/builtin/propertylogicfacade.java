package com.sap.sdo.impl.types.builtin;

import java.util.Collections;
import java.util.List;

import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.InternalDataObjectModifier;
import com.sap.sdo.impl.types.PropKey;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.types.simple.ListSimpleType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class PropertyLogicFacade extends PropertyLogic<GenericDataObject> implements DataObjectDecorator {

	private static final long serialVersionUID = 571162335392429588L;
	public PropertyLogicFacade(GenericDataObject o) {
		super(o);
        o.setReadOnlyMode(false);
	}

    public PropertyLogicFacade(DataObject o) {
        this((GenericDataObject)o);
    }

    public GenericDataObject getInstance() {
		return getDelegate();
	}

	public InternalDataObjectModifier getInternalModifier() {
        return getDelegate();
    }

	private String _propName;
	private PropKey _key;
    private transient boolean _useCache = false;
    private transient List _aliasNames;
    private Type _containingType;
    private transient Object _default;
    private transient Class<?> _javaClass;
    private transient Property _opposite;
    private transient SdoType _type;
    private transient boolean _containment;
    private transient boolean _many;
    private transient boolean _nullable;
    private transient boolean _openContent;
    private transient boolean _readOnly;
    private transient String _xmlName;
    private transient URINamePair _ref;
    private transient URINamePair _xsdType;
    private transient boolean _xmlElement;
    private transient Boolean _formQualified;
    private transient boolean _oppositeContainment;
    private transient String _uri;
    private transient boolean _orphanHolder;

    @Override
	public boolean defined() {
		return _propName != null;
	}

	private Object readResolve() {
		if (_containingType != null) {
            int index = getIndex();
            if (index >= 0) {
                _containingType.getProperties().get(index);
            }
            if (index == -2) {
                // it is an open content attribute
                return _containingType.getProperty('@' + _propName);
            }
            // it is an open content element
            return _containingType.getProperty(_propName);
		}
        return _key.getHelperContext().getTypeHelper().defineOpenContentProperty(null, _key.getPropertyDescription());
	}

    public <S> PropertyLogicFacade initProperty(PropKey propKey) {
        GenericDataObject propObject = getInstance();
        boolean isOnDemandProp = propKey != null;
        Boolean xmlElement = null;
        if (propObject.isSet(PropertyType.getXmlElementProperty())) {
            xmlElement = propObject.getBoolean(PropertyType.getXmlElementProperty());
        }
        Boolean nullable = null;
        if (!isOnDemandProp && propObject.isSet(PropertyType.NULLABLE)) {
            nullable = propObject.getBoolean(PropertyType.NULLABLE);
        }
        if (nullable == null && Boolean.FALSE.equals(xmlElement)) {
            nullable = false;
        } else if ((xmlElement == null) && (Boolean.TRUE.equals(nullable))) {
            xmlElement = true;
        }
        _many = propObject.getBoolean(PropertyType.MANY);
        if ((xmlElement == null) && _many) {
            xmlElement = true;
        }
        _type = (SdoType<S>)propObject.get(PropertyType.TYPE);
        _containment = propObject.getBoolean(PropertyType.CONTAINMENT);
        Type keyType = null;
        boolean key = false;
        if (!isOnDemandProp) {
            keyType = _type.getKeyType();
            key = propObject.getBoolean(PropertyType.KEY);
            if ((xmlElement == null) && key) {
                xmlElement = !_type.isDataType() && (_containment || keyType ==null || !keyType.isDataType());
            }
        }
        Property referenceProperty = PropertyType.getReferenceProperty();
        if (referenceProperty != null) {
            String refString = propObject.getString(referenceProperty);
            if (refString != null) {
                _ref = URINamePair.fromStandardSdoFormat(refString);
                _uri = _ref.getURI();
                propObject.setString(PropertyType.getUriProperty(), _uri);
            }
        }
        if (_uri == null) {
            _uri = propObject.getString(PropertyType.getUriProperty());
        }
        DataObject container = null;
        Type containingType = null;
        if (!isOnDemandProp) {
            container = propObject.getContainer();
            if (container == null || !(container instanceof Type)) {
                containingType = (Type)propObject.get(PropertyType.CONTAINING_TYPE);
            } else {
                containingType = (Type)container;
            }
        }
        _formQualified = super.getFormQualified();
        
        if (_uri == null) {
            if (isOpenContent()) {
                if (containingType != null) {
                    _uri = containingType.getURI();
                }
            } else {
                boolean formQualified;
                if (_formQualified == null) {
                    if (isXmlElement()) {
                        formQualified = ((SdoType)containingType).getElementFormDefaultQualified();
                    } else {
                        formQualified = ((SdoType)containingType).getAttributeFormDefaultQualified();
                    }
                } else {
                    formQualified = _formQualified;
                }
                if (formQualified) {
                    _uri = containingType.getURI();
                } else {
                    _uri = "";
                }
            }
        }
        if (_type.isDataType() || Boolean.FALSE.equals(xmlElement)) {
            propObject.set(PropertyType.CONTAINMENT, false);
            _containment = false;
            _default = getDefault();
            if (_default == null) {
                if (!_many) {
                    _default = _type.getDefaultValue();
                }
            } else {
                _default = _type.convertFromJavaClass(_default);
            }
            if (_default != null) {
                if (_default instanceof List) {
                    _default = Collections.unmodifiableList((List)_default);
                } else {
                    _default = getCachedValue(_default);
                }
                propObject.set(PropertyType.DEFAULT, _default);
            }
            String xsdTypeString = propObject.getString(PropertyType.getXsdTypeProperty());
            if (xsdTypeString == null) {
                if (_type.getInstanceClass() == List.class) {
                    setXsdType(_type);
                }
            } else {
                _xsdType = URINamePair.fromStandardSdoFormat(xsdTypeString);
            }
        } else if (xmlElement == null && !_containment) {
            xmlElement = keyType != null && !keyType.isDataType();
        }
        if (_containment) {
            xmlElement = true;
        }
        _xmlElement = Boolean.TRUE.equals(xmlElement);
        _propName = super.getName();
        _xmlName = propObject.getString(PropertyType.getXmlNameProperty());
        if (_xmlName == null) {
            _xmlName = _propName;
        }
        _aliasNames = propObject.getList(PropertyType.ALIAS_NAME);
        if (propKey != null) {
            propKey.setName(_propName);
            propKey.setAlias(_aliasNames);
            propKey.setXmlName(_xmlName);
            propKey.setType(_type.getQName());
            propKey.setMany(_many);
            propKey.setContainment(_containment);
            propKey.setXmlElement(_xmlElement);
            propKey.setSimpleContentProperty(propObject.getBoolean(PropertyType.getSimpleContentProperty()));
            propKey.setXsdType((_xsdType==null)? PropKey.DUMMY_UNP: _xsdType);
            propKey.setRef((_ref==null)? PropKey.DUMMY_UNP: _ref);
            propKey.setManyUnknown(propObject.getBoolean(PropertyType.getManyUnknownProperty()));
            SdoProperty cached = ((TypeHelperImpl)propKey.getHelperContext().getTypeHelper()).lookupOrRegisterOnDemandProp(propKey, this);
            if (cached != null) {
                return (PropertyLogicFacade)cached;
            }
            _key = propKey;
        }
        if (container != null) {
            if (_type == JavaSimpleType.ID) {
                propObject.set(PropertyType.KEY, true);
                key = true;
            }
            if (key) {
                nullable = Boolean.FALSE;
                Type containerKeyType = (Type)container.get(TypeType.KEY_TYPE);
                if (containerKeyType == null) {
                    container.set(TypeType.KEY_TYPE, _type);
                } else if (containerKeyType.isDataType() && containerKeyType != _type && containerKeyType != _type.getKeyType()) {
                    throw new IllegalArgumentException("Error on property "
                        + getName() + ": type of the key doesn't match the keyType");
                }
            }
        }
        if (nullable == null) {
            if (_xmlElement) {
                Class instanceClass = _type.getInstanceClass();
                if ((instanceClass != null) && instanceClass.isPrimitive()) {
                    nullable = false;
                } else {
                    nullable = true;
                }
            } else {
                nullable = false;
            }
        }
        propObject.setBoolean(PropertyType.NULLABLE, nullable);
        propObject.setBoolean(PropertyType.getXmlElementProperty(), Boolean.TRUE.equals(_xmlElement));
        _javaClass = (Class)propObject.get(PropertyConstants.JAVA_CLASS);
        if (_many) {
            if (_javaClass == null) {
                _javaClass = _type.getInstanceClass();
            }
            if ((_javaClass != null) && (_javaClass.isPrimitive())) {
                JavaSimpleType simpleType = (JavaSimpleType)SapHelperProviderImpl
                    .getCoreContext().getTypeHelper().getType(_javaClass);
                _javaClass = simpleType.getNillableType().getInstanceClass();
                propObject.set(PropertyConstants.JAVA_CLASS, _javaClass);
            }
        }
        propObject.trimMemory();
        propObject.setReadOnlyMode(true);
        
        
        _useCache = true;
        _containingType = containingType;
        _opposite = super.getOpposite();
        _nullable = nullable;
        int index = getIndex();
        if (index > 0) {
            _openContent = false;
        } else if (propKey != null) {
            _openContent = true;
        } else {
            _openContent = !(container instanceof Type);
        }
        _readOnly = super.isReadOnly();
        if (!_xmlElement && index == -1) {
            setIndex(-2);
        }
        if (_opposite != null) {
            _oppositeContainment = _opposite.isContainment();
        }
        _orphanHolder = super.isOrphanHolder();
        //TODO _key = key;
        return this;

    }
    
    private void setXsdType(SdoType pDataType) {
        if (pDataType instanceof ListSimpleType) {
            URINamePair xsdTypeUnp = ((ListSimpleType)pDataType).getXsdType();
            if (xsdTypeUnp != null) {
                getInstance().setString(PropertyType.getXsdTypeProperty(), xsdTypeUnp.toStandardSdoFormat());
                _xsdType = xsdTypeUnp;
            }
        } else {
            for (SdoType baseType: (List<SdoType>)pDataType.getBaseTypes()) {
                setXsdType(baseType);
            }
        }
    }
    
    public void setOppositeProperty(SdoProperty pOpposite) {
        GenericDataObject instance = getDelegate();
        boolean readOnlyMode = instance.isReadOnlyMode();
        instance.setReadOnlyMode(false);
        instance.set(PropertyType.OPPOSITE, pOpposite);
        instance.set(PropertyType.OPPOSITE_INTERNAL, pOpposite.getName());
        _opposite = pOpposite;
        if (pOpposite.isContainment()) {
            _oppositeContainment = true;
            instance.unset(PropertyType.getXmlElementProperty());
            _xmlElement = false;
            instance.unset(PropertyType.NULLABLE);
            _nullable = false;
        }
        if (readOnlyMode) {
            instance.setReadOnlyMode(true);
        }
    }

    @Override
    public List getAliasNames() {
        if (_useCache) {
            return _aliasNames;
        }
        return super.getAliasNames();
    }

    @Override
    public Type getContainingType() {
        if (_useCache) {
            return _containingType;
        }
        return super.getContainingType();
    }

    @Override
    public Object getDefault() {
        if (_useCache) {
            return _default;
        }
        return super.getDefault();
    }

    @Override
    public Class<?> getJavaClass() {
        if (_useCache) {
            return _javaClass;
        }
        return super.getJavaClass();
    }

    @Override
    public String getName() {
        if (_useCache) {
            return _propName;
        }
        return super.getName();
    }

    @Override
    public Property getOpposite() {
        if (_useCache) {
            return _opposite;
        }
        return super.getOpposite();
    }

    @Override
    public Type getType() {
        if (_useCache) {
            return _type;
        }
        return super.getType();
    }

    @Override
    public boolean isContainment() {
        if (_useCache) {
            return _containment;
        }
        return super.isContainment();
    }

    @Override
    public boolean isMany() {
        if (_useCache) {
            return _many;
        }
        return super.isMany();
    }

    @Override
    public boolean isNullable() {
        if (_useCache) {
            return _nullable;
        }
        return super.isNullable();
    }

    @Override
    public boolean isOpenContent() {
        if (_useCache) {
            return _openContent;
        }
        return super.isOpenContent();
    }

    @Override
    public boolean isReadOnly() {
        if (_useCache) {
            return _readOnly;
        }
        return super.isReadOnly();
    }

    @Override
    public String getXmlName() {
        if (_useCache) {
            return _xmlName;
        }
        return super.getXmlName();
    }

    @Override
    public URINamePair getRef() {
        if (_useCache) {
            return _ref;
        }
        return super.getRef();
    }

    @Override
    public URINamePair getXsdType() {
        if (_useCache) {
            return _xsdType;
        }
        return super.getXsdType();
    }

    @Override
    public boolean isXmlElement() {
        if (_useCache) {
            return _xmlElement;
        }
        return super.isXmlElement();
    }

    @Override
    public Boolean getFormQualified() {
        if (_useCache) {
            return _formQualified;
        }
        return super.getFormQualified();
    }

    @Override
    public boolean isOppositeContainment() {
        if (_useCache) {
            return _oppositeContainment;
        }
        return super.isOppositeContainment();
    }

    @Override
    public String getUri() {
        if (_useCache) {
            return _uri;
        }
        return super.getUri();
    }

    @Override
    public int getIndex(Type pType) {
        int index = getIndex();
        if (index < 0) {
            return index;
        }
        if (_containingType==pType || pType.getProperties().get(index)== this) {
            return index;
        }
        index = pType.getProperties().indexOf(this);
        if (index < 0) {
            throw new IllegalArgumentException("Property is defined for another type "
                + getName());
        }
        return index;
    }

    @Override
    public boolean isOrphanHolder() {
        if (_useCache) {
            return _orphanHolder;
        }
        return super.isOrphanHolder();
    }

}
