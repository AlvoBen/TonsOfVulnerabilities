package com.sap.sdo.impl.types.builtin;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.util.Collections;
import java.util.List;

import com.sap.sdo.api.types.schema.LocalComplexType;
import com.sap.sdo.api.types.schema.LocalSimpleType;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.InternalDataObjectModifier;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public class TypeLogicFacade<S> extends TypeLogic<S, GenericDataObject> implements DataObjectDecorator {

    private static final long serialVersionUID = -6381018750518214567L;
    private transient String _uri;
    private transient List _aliasNames;
    private transient List _baseTypes;
    private transient List _declaredProperties;
    private transient Class<S> _instanceClass;
    private transient boolean _abstract;
    private transient boolean _dataType;
    private transient boolean _open;
    private transient boolean _sequenced;
    private transient S _defaultValue;
    private transient boolean _mixedContent;
    private transient String _xmlName;
    private transient boolean _attributeFormDefaultQualified;
    private transient boolean _elementFormDefaultQualified;
    private transient DataObject _facets;
    private transient List<SdoProperty> _orphanHolderProperties;
    private transient Type _keyType;
    private transient SdoType _typeForKeyUniqueness;
    private transient Boolean _hasXmlFriendlyKey;
    private transient SdoProperty _simpleContentValueProperty;
    
    private HelperContext _helperContext;
    

    public TypeLogicFacade(DataObject o, HelperContext pHelperContext) {
		this((GenericDataObject)o, pHelperContext);
	}

    public TypeLogicFacade(GenericDataObject o, HelperContext pHelperContext) {
        super(o);
        o.setReadOnlyMode(false);
        _helperContext = pHelperContext;
    }

    public GenericDataObject getInstance() {
		return getDelegate();
	}

    public InternalDataObjectModifier getInternalModifier() {
        return getDelegate();
    }

	private Object readResolve() throws ObjectStreamException {
		final Type type = _helperContext.getTypeHelper().getType(_qName.getURI(), _qName.getName());
        if (type == null) {
            throw new InvalidObjectException("Type " + _qName.toStandardSdoFormat() +
                " is unknown in HelperContext with id " + _helperContext);
        }
        return type;
	}

    @Override
    public void useCache() {
        _props = null;
        loadProperties();
        _uri = super.getURI();
        _qName = super.getQName();
        _dataType = super.isDataType();
        _baseTypes = super.getBaseTypes();
        _aliasNames = super.getAliasNames();
        _declaredProperties = Collections.unmodifiableList(super.getDeclaredProperties());
        if (_instanceClass == null) {
            _instanceClass = super.getInstanceClass();
        }
        _abstract = super.isAbstract();
        _open = super.isOpen();
        _sequenced = super.isSequenced();
        _mixedContent = super.isMixedContent();
        _xmlName = super.getXmlName();
        _attributeFormDefaultQualified = super.getAttributeFormDefaultQualified();
        _elementFormDefaultQualified = super.getElementFormDefaultQualified();
        _facets = super.getFacets();
        _defaultValue = super.getDefaultValue();
        _orphanHolderProperties = super.getOrphanHolderProperties();
        _keyType = super.getKeyType();
        _typeForKeyUniqueness = super.getTypeForKeyUniqueness();
        _hasXmlFriendlyKey = super.hasXmlFriendlyKey();
        _simpleContentValueProperty = super.getSimpleContentValueProperty();
        _useCache = true;
    }
    
    

    @Override
    public boolean defined() {
        return _useCache;
    }

    @Override
    public S getDefaultValue() {
        if (_useCache) {
            return _defaultValue;
        }
        return super.getDefaultValue();
    }

    @Override
    public List getAliasNames() {
        if (_useCache) {
            return _aliasNames;
        }
        return super.getAliasNames();
    }

    @Override
    public List getBaseTypes() {
        if (_useCache) {
            return _baseTypes;
        }
        return super.getBaseTypes();
    }

    @Override
    public List getDeclaredProperties() {
        if (_useCache) {
            return _declaredProperties;
        }
        return super.getDeclaredProperties();
    }

    @Override
    public Class<S> getInstanceClass() {
        if (_useCache || (_instanceClass != null)) {
            return _instanceClass;
        }
        return super.getInstanceClass();
    }
    
    public void setInstanceClass(Class<S> pInstanceClass) {
        _instanceClass = pInstanceClass;
    }

    @Override
    public String getName() {
        if (_useCache) {
            return _qName.getName();
        }
        return super.getName();
    }

    @Override
    public String getURI() {
        if (_useCache) {
            return _uri;
        }
        return super.getURI();
    }

    @Override
    public boolean isAbstract() {
        if (_useCache) {
            return _abstract;
        }
        return super.isAbstract();
    }

    @Override
    public boolean isDataType() {
        if (_useCache) {
            return _dataType;
        }
        return super.isDataType();
    }

    @Override
    public boolean isOpen() {
        if (_useCache) {
            return _open;
        }
        return super.isOpen();
    }

    @Override
    public boolean isSequenced() {
        if (_useCache) {
            return _sequenced;
        }
        return super.isSequenced();
    }

    @Override
    public URINamePair getQName() {
        if (_useCache) {
            return _qName;
        }
        return super.getQName();
    }

    @Override
    public boolean isMixedContent() {
        if (_useCache) {
            return _mixedContent;
        }
        return super.isMixedContent();
    }
    
    @Override
    public boolean isLocal() {
        Property schemaReferenceProperty = TypeType.getSchemaReferenceProperty();
        if (schemaReferenceProperty == null) {
            return false;
        }
        DataObject schemaRef = getDataObject(schemaReferenceProperty);
        return schemaRef instanceof LocalComplexType || schemaRef instanceof LocalSimpleType;
    }
    
    @Override
    public String getXmlName() {
        if (_useCache) {
            return _xmlName;
        }
        return super.getXmlName();
    }

    @Override
    public String getXmlUri() {
        return getQName().getURI();
    }

    @Override
    public boolean getAttributeFormDefaultQualified() {
        if (_useCache) {
            return _attributeFormDefaultQualified;
        }
        return super.getAttributeFormDefaultQualified();
    }

    @Override
    public boolean getElementFormDefaultQualified() {
        if (_useCache) {
            return _elementFormDefaultQualified;
        }
        return super.getElementFormDefaultQualified();
    }

    @Override
    public DataObject getFacets() {
        if (_useCache) {
            return _facets;
        }
        return super.getFacets();
    }

    @Override
    public List<SdoProperty> getOrphanHolderProperties() {
        if (_useCache) {
            return _orphanHolderProperties;
        }
        return super.getOrphanHolderProperties();
    }

    @Override
    public Type getKeyType() {
        if (_useCache) {
            return _keyType;
        }
        return super.getKeyType();
    }

    @Override
    public SdoType getTypeForKeyUniqueness() {
        if (_useCache) {
            return _typeForKeyUniqueness;
        }
        return super.getTypeForKeyUniqueness();
    }
    
    @Override
    public Boolean hasXmlFriendlyKey() {
        if (_useCache) {
            return _hasXmlFriendlyKey;
        }
        return super.hasXmlFriendlyKey();
    }

    @Override
    public SdoProperty getSimpleContentValueProperty() {
        if (_useCache) {
            return _simpleContentValueProperty;
        }
        return super.getSimpleContentValueProperty();
    }
    
    @Override
    public HelperContext getHelperContext() {
        return _helperContext;
    }

    public void setHelperContext(HelperContext pHelperContext) {
        _helperContext = pHelperContext;
    }

}
