package com.sap.sdo.impl.xml;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.util.DataObjectBehavior;

import commonj.sdo.DataObject;

public class Key extends Reference {

    private final SdoType<?> _type;
    private final Object _key;
    private final int hashCode;

    public Key(SdoType<?> pType, Object pKey) {
        if (pKey == null) {
            throw new IllegalArgumentException("Incomplete key for type " + pType);
        }
        _type = pType;
        _key = pKey;
        hashCode = _type.hashCode() + keyHashCode();
    }

    public Object getKey() {
        return _key;
    }

    public SdoType<?> getType() {
        return _type;
    }

    @Override
    public boolean isKeyReference() {
        return true;
    }

    @Override
    public boolean equals(Object pObj) {
        if (this == pObj) {
            return true;
        }
        if (!(pObj instanceof Key)) {
            return false;
        }
        Key other = (Key)pObj;
        if (_type != other.getType()) {
            return false;
        }
        Object otherKey = other.getKey();
        if (_key instanceof DataObject && otherKey instanceof DataObject) {
            DataObject myDo = (DataObject)_key;
            return DataObjectBehavior.getHelperContext(myDo).getEqualityHelper().equal(myDo, (DataObject)otherKey);
        }
        return _key.equals(otherKey);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
    
    private int keyHashCode() {
        if (_key instanceof DataObject) {
            return DataObjectBehavior.hashCode((DataObject)_key);
        }
        return _key.hashCode(); 
    }

    @Override
    public String toString() {
        return URINamePair.fromType(_type) + " " + _key;
    }

}
