package com.sap.sdo.api.helper;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;

public interface SapDataFactory extends DataFactory {
    
    DataObject cast(Object pojo);
    
    DataObject project(DataObject dataObject);

}
