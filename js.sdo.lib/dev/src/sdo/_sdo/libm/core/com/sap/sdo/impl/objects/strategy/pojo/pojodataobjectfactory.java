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
package com.sap.sdo.impl.objects.strategy.pojo;

import java.util.HashMap;
import java.util.Map;

import com.sap.sdo.impl.context.HelperContextImpl;
import com.sap.sdo.impl.objects.DataFactoryImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeAndContext;
import com.sap.sdo.impl.types.TypeHelperImpl;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public class PojoDataObjectFactory {

    private PojoDataObjectFactory() {
    }
    
    public static DataObject createDataObject(Type pType, Object pPojo) {
        return createDataObject(pType, pPojo, new HashMap<Object, DataObjectDecorator>());
    }

    public static DataObject createDataObject(Type pType, Object pPojo, Map<? extends Object, ? extends DataObject> pPojoToDataObject) {
        return createDataObject(pType, pPojo, pPojoToDataObject, ((SdoType)pType).getHelperContext());
    }
    
    public static DataObject createDataObject(Type pType, Object pPojo, Map<? extends Object, ? extends DataObject> pPojoToDataObject, HelperContext pHelperContext) {
        DataObject dataObject = pPojoToDataObject.get(pPojo);
        if (dataObject != null) {
            return dataObject;
        }
        TypeHelperImpl typeHelper = (TypeHelperImpl)pHelperContext.getTypeHelper();
        TypeAndContext typeAndContext = typeHelper.getTypeAndContext((SdoType)pType);
        dataObject = new GenericDataObject(typeAndContext, new PojoDataStrategy(pPojo, (Map<Object, DataObjectDecorator>)pPojoToDataObject));
        
        Class intf = pType.getInstanceClass();
        if (intf != null) {
            dataObject = ((DataFactoryImpl)((SdoType)pType).getHelperContext().getDataFactory()).facade(((GenericDataObject)dataObject).getInstance(), intf, pType);
        }
        return dataObject;
    
    }
}
