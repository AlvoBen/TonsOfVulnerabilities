package com.sap.sdo.impl.objects;

import com.sap.sdo.api.helper.SapDataFactory;
import com.sap.sdo.impl.context.SapHelperProviderImpl;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;

public class DataFactoryDelegator implements SapDataFactory {

    private static final DataFactory INSTANCE = new DataFactoryDelegator();

    private DataFactoryDelegator() {
        super();
    }
    
    public static DataFactory getInstance() {
        return INSTANCE;
    }

    public DataObject create(String pUri, String pTypeName) {
        return SapHelperProviderImpl.getDefaultContext().getDataFactory().create(pUri, pTypeName);
    }

    public DataObject create(Class pInterfaceClass) {
        return SapHelperProviderImpl.getDefaultContext().getDataFactory().create(pInterfaceClass);
    }

    public DataObject create(Type pType) {
        return SapHelperProviderImpl.getDefaultContext().getDataFactory().create(pType);
    }

	public DataObject cast(Object obj) {
        return ((SapDataFactory)SapHelperProviderImpl.getDefaultContext().getDataFactory()).cast(obj);
	}

    public DataObject project(DataObject pDataObject) {
        return ((SapDataFactory)SapHelperProviderImpl.getDefaultContext().getDataFactory()).project(pDataObject);
    }

}
