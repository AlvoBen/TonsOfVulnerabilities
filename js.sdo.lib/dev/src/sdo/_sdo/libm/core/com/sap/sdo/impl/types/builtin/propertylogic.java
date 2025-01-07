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

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.Namespace;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.cache.BooleanCache;
import com.sap.sdo.impl.types.builtin.cache.ByteCache;
import com.sap.sdo.impl.types.builtin.cache.CharacterCache;
import com.sap.sdo.impl.types.builtin.cache.HashCache;
import com.sap.sdo.impl.types.builtin.cache.IntegerCache;
import com.sap.sdo.impl.types.builtin.cache.LongCache;
import com.sap.sdo.impl.types.builtin.cache.NoCache;
import com.sap.sdo.impl.types.builtin.cache.ShortCache;
import com.sap.sdo.impl.types.builtin.cache.ValueCache;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public abstract class PropertyLogic<D extends DataObject> extends DelegatingDataObject<D> implements SdoProperty, Serializable {

    private static final long serialVersionUID = 8810539355356686327L;
	private int _index = -1;
    private transient int _requestedIndex = -1;
	private transient D _delegate;
    private transient ValueCache _valueCache;
    private transient SoftReference<Map<DataObject, Map<Object, Integer>[]>> _indexMaps;

	public PropertyLogic(D prop) {
		_delegate = prop;
	}
	
    public PropertyLogic(D pProp, ValueCache pValueCache) {
        this(pProp);
        _valueCache = pValueCache;
    }
    
	public boolean defined() {
		return true;
	}
	public String getJavaName() {
		if (_delegate instanceof MetaDataProperty) {
			return null;
		}
		return getString(PropertyType.getJavaNameProperty());
	}
	public boolean isXmlElement() {
		if (isSet(PropertyType.getXmlElementProperty())) {
			return getBoolean(PropertyType.getXmlElementProperty());
		}
		return isNullable() || isMany() || isContainment();
	}
    public boolean isOrphanHolder() {
        if (isSet(PropertyType.getOrphanHolderProperty())) {
            return getBoolean(PropertyType.getOrphanHolderProperty());
        }
        return false;
    }
	public int getIndex() {
		return _index;
	}

	public int getIndex(Type pType) {
        return _index;
    }

    public void setIndex(int i) {
		_index = i;
	}

	public int getRequestedIndex() {
        return _requestedIndex;
    }
    public void setRequestedIndex(int pRequestedIndex) {
        _requestedIndex = pRequestedIndex;
    }

    public String getXmlName() {
        String xmlName = getString(PropertyType.getXmlNameProperty());
        if (xmlName != null) {
            return xmlName;
        }
        return getName();
	}

	public URINamePair getRef() {
        String ref = getString(PropertyType.getReferenceProperty());
        if (ref == null) {
            return null;
        }
        URINamePair unp = URINamePair.fromStandardSdoFormat(ref);
        intern(unp);
        return unp;
    }

    public URINamePair getXsdType() {
        String xsdType = getString(PropertyType.getXsdTypeProperty());
        if (xsdType == null) {
            return null;
        }
        URINamePair unp = URINamePair.fromStandardSdoFormat(xsdType);
        intern(unp);
        return unp;
    }

    private void intern(URINamePair unp) {
        unp.setURI(TypeHelperImpl.getUriCache().getCachedValue(unp.getURI()));
        unp.setName(TypeHelperImpl.getNameCache().getCachedValue(unp.getName()));
    }
    public Type getContainingType() {
		Object ret = getContainer();
		if (ret == null || !(ret instanceof Type)) {
			ret = get(PropertyType.CONTAINING_TYPE);
		}
		return (Type)ret;
	}
	@Override
    public Type getType() {
		return (Type)get(PropertyType.TYPE);
	}
    public boolean isOpenContent() {
        if (getIndex() >= 0) {
            return false;
        }
        return !(getContainer() instanceof Type);
    }
	public Class<?> getJavaClass() {
		return (Class<?>)get(PropertyType.JAVA_CLASS);
	}
	public List getAliasNames() {
		return getList(PropertyType.ALIAS_NAME);
	}
	public Object getDefault() {
		return get(PropertyType.DEFAULT);
	}
	public String getName() {
		return getString(PropertyType.NAME);
	}
	public Property getOpposite() {
		return (Property)get(PropertyType.OPPOSITE);
	}
	public boolean isContainment() {
		return getBoolean(PropertyType.CONTAINMENT);
	}
	public boolean isMany() {
		return getBoolean(PropertyType.MANY);
	}
	public boolean isNullable() {
		return getBoolean(PropertyType.NULLABLE);
	}
	public boolean isReadOnly() {
		return getBoolean(PropertyType.READ_ONLY);
	}

    public Boolean getFormQualified() {
        return (Boolean)get(PropertyType.getFormQualifiedProperty());
    }

    @Override
	protected D getDelegate() {
		return _delegate;
	}

    public boolean isOppositeContainment() {
        Property opposite = getOpposite();
        if (opposite == null) {
            return false;
        }
        return opposite.isContainment();
    }

    public String getUri() {
        String uri = getString(PropertyType.getUriProperty());
        if (uri != null) {
            return uri;
        }
        URINamePair ref = getRef();
        if (ref != null) {
            return ref.getURI();
        }
        Type containingType = getContainingType();
        if (isOpenContent()) {
            if (containingType == null) {
                return null; //TODO or better ""?
            }
        } else {
            Boolean formQualified = getFormQualified();
            if (formQualified == null) {
                if (isXmlElement()) {
                    formQualified = ((SdoType)containingType).getElementFormDefaultQualified();
                } else {
                    formQualified = ((SdoType)containingType).getAttributeFormDefaultQualified();
                }
            }
            if (!formQualified) {
                return "";
            }
        }
        uri = containingType.getURI();
        if (uri == null) {
            uri = "";
        }
        return uri;
    }

    public Object getCachedValue(Object value) {
        if (value == null) {
            return null;
        }
        return getValueCache().getCachedValue(value);
    }

    public void setValueCache(ValueCache pValueCache) {
        _valueCache = pValueCache;
    }

    /**
     * Returns the ValueCache for this property.
     * @return The ValueCache.
     */
    private ValueCache getValueCache() {
        if (_valueCache == null) {
            final Type type = getType();
            if (type.isDataType()) {
                final Class instanceClass = type.getInstanceClass();
                if ((instanceClass == Boolean.class) || (instanceClass == boolean.class)) {
                    _valueCache = BooleanCache.getInstance();
                } else if ((instanceClass == Byte.class) || (instanceClass == byte.class)) {
                    _valueCache = ByteCache.getInstance();
                } else if (getContainingType() == null) {
                    // for open content properties that are used once,
                    // only chaches are used that don't need space per property
                    if ((instanceClass == Integer.class) || (instanceClass == int.class)) {
                        _valueCache = IntegerCache.getInstance();
                    } else if ((instanceClass == Long.class) || (instanceClass == long.class)) {
                        _valueCache = LongCache.getInstance();
                    } else if ((instanceClass == Short.class) || (instanceClass == short.class)) {
                        _valueCache = ShortCache.getInstance();
                    } else if ((instanceClass == Character.class) || (instanceClass == char.class)) {
                        _valueCache = CharacterCache.getInstance();
                    } else {
                        _valueCache = NoCache.getInstance();
                    }
                } else {
                    // for porperties that are defined on types or as global
                    // property, caches are used that take advantage of HashCache
                    if (instanceClass == String.class) {
                        _valueCache = new HashCache(256, 4096);
                    } else if ((instanceClass == Integer.class) || (instanceClass == int.class)) {
                        _valueCache = new IntegerCache(256, 4096);
                    } else if ((instanceClass == Long.class) || (instanceClass == long.class)) {
                        _valueCache = new LongCache(256, 4096);
                    } else if ((instanceClass == Short.class) || (instanceClass == short.class)) {
                        _valueCache = new ShortCache(256, 4096);
                    } else if ((instanceClass == Character.class) || (instanceClass == char.class)) {
                        _valueCache = new CharacterCache(256, 4096);
                    } else {
                        _valueCache = NoCache.getInstance();
                    }
                }
            } else {
                _valueCache = NoCache.getInstance();
            }
        }
        return _valueCache;
    }
	public HelperContext getHelperContext() {
		Type t = getContainingType();
		if (t instanceof SdoType) {
			return ((SdoType)t).getHelperContext();
		}
		if (t instanceof Namespace) {
			return ((Namespace)t).getHelperContext();
		}
		return null;
	}

    public Map<Object, Integer>[] getIndexMaps(DataObject pContainingObject) {
        if (_indexMaps == null) {
            return null;
        }
        Map<DataObject, Map<Object, Integer>[]> indexMaps = _indexMaps.get();
        if (indexMaps == null) {
            _indexMaps = null;
            return null;
        }
        if (indexMaps.isEmpty()) {
            return null;
        }
        return indexMaps.get(pContainingObject);
    }

    public void removeIndexMaps(DataObject pContainingObject) {
        if (_indexMaps == null) {
            return;
        }
        Map<DataObject, Map<Object, Integer>[]> indexMaps = _indexMaps.get();
        if (indexMaps == null) {
            _indexMaps = null;
            return;
        }
        if (indexMaps.isEmpty()) {
            _indexMaps = null;
            return;
        }
        indexMaps.remove(pContainingObject);
        if (indexMaps.isEmpty()) {
            _indexMaps = null;
        }
    }

    public Map<Object, Integer>[] createIndexMaps(DataObject pContainingObject) {
        Map<DataObject, Map<Object, Integer>[]> indexMaps = null;
        if (_indexMaps != null) {
            indexMaps = _indexMaps.get();
        }
        if (indexMaps == null) {
            indexMaps = new WeakHashMap<DataObject, Map<Object, Integer>[]>();
            _indexMaps = new SoftReference<Map<DataObject, Map<Object, Integer>[]>>(indexMaps);
        }
        Map<Object, Integer>[] result = new HashMap[getType().getProperties().size()];
        indexMaps.put(pContainingObject, result);
        return result;
    }

    public boolean isKey() {
        return _delegate.getBoolean(PropertyType.KEY);
    }
    
    @Override
    public String toString() {
        return "Property: " + getName() + ' ' + super.toString();
    }

}
