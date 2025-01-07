package com.sap.sdo.impl.types.builtin;

import java.util.List;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.types.builtin.cache.ValueCache;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public class MetaDataPropertyLogicFacade extends PropertyLogic<MetaDataProperty> {

    private static final long serialVersionUID = 1362993141681630564L;

    public MetaDataPropertyLogicFacade(MetaDataProperty pProp,
        ValueCache pValueCache) {
        super(pProp, pValueCache);
    }

    public MetaDataPropertyLogicFacade(MetaDataProperty pProp) {
        super(pProp);
    }

    @Override
    public String getJavaName() {
        return null;
    }
    
    @Override
    public String getName() {
        return getDelegate().getName();
    }

    @Override
    public Type getContainingType() {
        return getDelegate().getContainingType();
    }

    @Override
    public boolean isReadOnly() {
        return getDelegate().isReadOnly();
    }

    @Override
    public boolean isMany() {
        return getDelegate().isMany();
    }

    @Override
    public boolean isContainment() {
        return getDelegate().isContainment();
    }

    @Override
    public Property getOpposite() {
        return getDelegate().getOpposite();
    }

    @Override
    public List getAliasNames() {
        return getDelegate().getAliasNames();
    }

    @Override
    public boolean isNullable() {
        return getDelegate().isNullable();
    }

    @Override
    public boolean isKey() {
        return getDelegate().isKey();
    }

    @Override
    public boolean isXmlElement() {
        return getDelegate().isXmlElement();
    }

    @Override
    public boolean isOrphanHolder() {
        return getDelegate().isOrphanHolder();
    }

    @Override
    public Class<?> getJavaClass() {
        return getDelegate().getJavaClass();
    }
    
    @Override
    public Type getType() {
        return getDelegate().getPropType();
    }

    @Override
    public Object getDefault() {
        return getDelegate().getDefault();
    }

    @Override
    public HelperContext getHelperContext() {
        return SapHelperProviderImpl.getCoreContext();
    }

    @Override
    public URINamePair getXsdType() {
        return getDelegate().getXsdType();
    }

}
