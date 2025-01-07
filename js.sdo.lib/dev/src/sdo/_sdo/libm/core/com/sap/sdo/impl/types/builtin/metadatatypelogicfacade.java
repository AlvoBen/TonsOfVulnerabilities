package com.sap.sdo.impl.types.builtin;

import java.util.List;

import com.sap.sdo.impl.context.SapHelperProviderImpl;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public class MetaDataTypeLogicFacade<S> extends TypeLogic<S, MetaDataType> {

    private static final long serialVersionUID = -3837968270996112664L;

    public MetaDataTypeLogicFacade(MetaDataType pO) {
        super(pO);
    }
    
    @Override
    public List getAliasNames() {
        return getDelegate().getAliasNames();
    }

    @Override
    public List getBaseTypes() {
        return getDelegate().getBaseTypes();
    }

    @Override
    public Type getKeyType() {
        return getDelegate().getKeyType();
    }

    @Override
    public String getName() {
        return getDelegate().getName();
    }

    @Override
    public List getProperties() {
        return getDelegate().getDeclaredProperties();
    }

    @Override
    public String getURI() {
        return getDelegate().getURI();
    }

    @Override
    public boolean isAbstract() {
        return getDelegate().isAbstract();
    }

    @Override
    public boolean isDataType() {
        return getDelegate().isDataType();
    }

    @Override
    public boolean isOpen() {
        return getDelegate().isOpen();
    }

    @Override
    public boolean isSequenced() {
        return getDelegate().isSequenced();
    }

    @Override
    public HelperContext getHelperContext() {
        return SapHelperProviderImpl.getCoreContext();
    }

}
