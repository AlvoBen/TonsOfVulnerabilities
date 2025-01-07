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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.objects.CopyHelperImpl;
import com.sap.sdo.impl.types.Invoker;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public abstract class MetaDataType<S> extends MetaDataObject implements SdoType<S>
{
	private static final long serialVersionUID = -2119336527849020394L;
	private URINamePair _unp;
	private transient boolean _open;
	private transient Class<S> _instanceClass;
	private transient boolean _sequenced;
    private transient boolean _abstract;
    private transient boolean _dataType = false;

    private transient List _declPropsList = Collections.EMPTY_LIST;

    private transient final TypeLogic _typeLogic;

    public Object readResolve() {
    	return getHelperContext().getTypeHelper().getType(_unp.getURI(), _unp.getName());
    }
    public MetaDataType() {
        super();
        _typeLogic = new MetaDataTypeLogicFacade<S>(this);
    }

    public boolean defined() {
        return true;
    }

    public URINamePair getQName() {
        return this._unp;
    }
    protected void setUNP(final URINamePair unp) {
		this._unp = unp;
	}

	protected void setOpen(final boolean open) {
		this._open = open;
	}

    protected void setAbstract(final boolean babstract) {
        this._abstract = babstract;
    }

    protected void setSequenced(final boolean sequenced) {
        this._sequenced = sequenced;
    }
    protected void setDataType(final boolean dataType) {
        this._dataType = dataType;
    }
    protected void setInstanceClass(final Class<S> instanceClass) {
        this._instanceClass = instanceClass;
    }

    public String getName() {
        return this._unp.getName();
    }

    public String getURI() {
		return this._unp.getURI();
	}

	public Class<S> getInstanceClass() {
        return this._instanceClass;
    }

    public boolean isInstance(final Object o) {
    	return _typeLogic.isInstance(o);
	}

	public List getProperties() {
        return _typeLogic.getProperties();
    }

    public Property getProperty(final String propertyName) {
        return getInstanceProperty(propertyName);
    }
    public Property getInstanceProperty(final String propertyName) {
        return _typeLogic.getProperty(propertyName);
    }

    public boolean isDataType() {
		return _dataType;
	}

	public boolean isOpen() {
        return this._open;
    }

    public boolean isSequenced() {
        return this._sequenced;
    }

    public boolean isAbstract() {
        return this._abstract;
    }

    public List getBaseTypes() {
        return Collections.EMPTY_LIST;
    }
    protected void setDeclaredProperties(final SdoProperty[] props) {
    	_declPropsList = Arrays.asList(props);
    	for (int i=0; i<props.length; i++) {
    		props[i].setIndex(i);
    	}
    	_typeLogic.useCache();
    }
    public List getDeclaredProperties() {
		return this._declPropsList;
	}

	public List getAliasNames() {
        return Collections.EMPTY_LIST;
    }

    public S getDefaultValue() {
        return null;
    }

    public S convertFromJavaClass(final Object data) {
    	return (S)_typeLogic.convertFromJavaClass(data);
	}

	public <T> T convertToJavaClass(final S data, final Class<T> targetType) {
    	return (T)_typeLogic.convertToJavaClass(data, targetType);
	}

    /**
     * TODO same as {@link TypeLogic#copy(Object, boolean)}
     */
    public S copy(final S o, final boolean shallow) {
        final CopyHelperImpl copyHelper = (CopyHelperImpl)getHelperContext().getCopyHelper();
        return (S)copyHelper.copyDataObject((DataObject)o, shallow);
    }

    @Override
    public String toString() {
        return this._unp.toStandardSdoFormat();
    }

	public Invoker getInvokerForMethod(Method pMethod) {
        return _typeLogic.getInvokerForMethod(pMethod);
    }

    public void useCache() {
		_typeLogic.useCache();
	}

    public boolean isMixedContent() {
		return false;
	}

	public boolean isLocal() {
        return false;
    }

    public String getXmlName() {
        return getName();
    }

    public String getXmlUri() {
        return getURI();
    }
    public boolean getAttributeFormDefaultQualified() {
        return false;
    }

    public boolean getElementFormDefaultQualified() {
        return false;
    }

    public DataObject getFacets() {
        return null;
    }

    public Property getIdProperty() {
		return null;
	}
	public String getId(final DataObject dataObject) {
		if (dataObject instanceof SdoType) {
			return ((SdoType<?>)dataObject).getQName().toString();
		}
		return null;
	}
	public int getIdPropertyIndex() {
		return -1;
	}
	public Class<S> getInheritedInstanceClass() {
		return getInstanceClass();
	}
	public boolean isAssignableType(final Type assignableFrom) {
		return _typeLogic.isAssignableType(assignableFrom);
	}

	public int getCsPropertyIndex() {
        return -1;
    }

    public int getXsdPropertyIndex() {
        return -1;
    }

    public List getInstanceProperties() {
		return TypeType.getInstance().getDeclaredProperties();
	}

	public Object get(int i) {
		switch (i) {
        case -1:
            return null;
		case TypeType.ABSTRACT:
			return isAbstract();
		case TypeType.ALIAS_NAME:
			return getAliasNames();
		case TypeType.BASE_TYPE:
			return getBaseTypes();
		case TypeType.DATA_TYPE:
			return isDataType();
		case TypeType.NAME:
			return getName();
		case TypeType.OPEN:
			return isOpen();
		case TypeType.PROPERTY:
			return getDeclaredProperties();
		case TypeType.SEQUENCED:
			return isSequenced();
		case TypeType.URI:
			return getURI();
        case TypeType.KEY_TYPE:
            return getKeyType();
		default:
			throw new IllegalArgumentException();
		}
	}
	public Object get(Property property) {
		final int propertyIndex = getPropertyIndex(property);
        if (propertyIndex >= 0) {
            return get(propertyIndex);
        }
        if (property == TypeType.getJavaClassProperty()) {
            return getInstanceClass().getName();
        }
        return null;
	}
	public Object get(String path) {
		Property p = _typeLogic.getProperty(path);
		if (p == null) {
			return null;
		}
		return get(p);
	}

    public SdoProperty getPropertyFromXmlName(String pUri, String pXmlPropertyName, boolean isElement) {
        SdoProperty property = (SdoProperty)getProperty(pXmlPropertyName);
        if (property != null) {
            if (property.isXmlElement() == isElement) {
                if (pUri == null) {
                    return null;
                }
                String uri = property.getUri();
                if (pUri.equals(uri)) {
                    return property;
                }
            }
        }
        return null;
    }

    public Property getPropertyFromJavaName(String javaMethodName) {
    	throw new UnsupportedOperationException();
    }
    public Property getPropertyFromJavaMethodName(String javaMethodName) {
    	throw new UnsupportedOperationException();
    }
	private Map<String,Object> _extraData = new HashMap<String,Object>();
    public Object getExtraData(String group, String item) {
    	final String key = group + '#' + item;
    	return _extraData.get(key);
    }
    public Object putExtraData(String group, String item, Object value) {
    	final String key = group + '#' + item;
    	return _extraData.put(key,value);
    }
    public List<SdoProperty> getSingleOppositeProperties() {
        return Collections.emptyList();
    }

    public HelperContext getHelperContext() {
        return SapHelperProviderImpl.getCoreContext();
    }

    public SdoType getSdoType() {
        return this;
    }

    public List<SdoProperty> getOrphanHolderProperties() {
        return Collections.emptyList();
    }

    public SdoType getKeyType() {
        return null;
    }
    public SdoType getTypeForKeyUniqueness() {
        return null;
    }
    public List<SdoProperty> getKeyProperties() {
        return Collections.emptyList();
    }

    public Boolean hasXmlFriendlyKey() {
        return null;
    }

    public SdoProperty getSimpleContentValueProperty() {
        return null;
    }

    public URINamePair getSpecialBaseType() {
        return null;
    }

}
