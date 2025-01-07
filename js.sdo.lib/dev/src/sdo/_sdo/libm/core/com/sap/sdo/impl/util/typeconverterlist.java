package com.sap.sdo.impl.util;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

import com.sap.sdo.impl.types.SdoType;

public class TypeConverterList<C, T> extends AbstractList<C> implements RandomAccess {

    private final List<T> _list;
    private final SdoType<T> _type;
    private final Class<C> _itemClass;
    
    public TypeConverterList(final List<T> pList, SdoType<T> pType, final Class<C> pItemType) {
        super();
        _list = pList;
        _type = pType;
        _itemClass = pItemType;
    }

    @Override
    public C get(int pIndex) {
        return _type.convertToJavaClass(_list.get(pIndex), _itemClass);
    }

    @Override
    public int size() {
        return _list.size();
    }

    @Override
    public void add(int pIndex, C pElement) {
        _list.add(pIndex, _type.convertFromJavaClass(pElement));
    }

    @Override
    public C remove(int pIndex) {
        return _type.convertToJavaClass(_list.remove(pIndex), _itemClass);
    }

    @Override
    public C set(int pIndex, C pElement) {
        T old = _list.set(pIndex, _type.convertFromJavaClass(pElement));
        return _type.convertToJavaClass(old, _itemClass);
    }

}
