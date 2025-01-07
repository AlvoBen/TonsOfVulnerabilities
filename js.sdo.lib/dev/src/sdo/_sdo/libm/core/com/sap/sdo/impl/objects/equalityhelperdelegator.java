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
package com.sap.sdo.impl.objects;

import com.sap.sdo.impl.context.SapHelperProviderImpl;

import commonj.sdo.DataObject;
import commonj.sdo.helper.EqualityHelper;

/**
 * @author D042774
 *
 */
public class EqualityHelperDelegator implements EqualityHelper {
    private static final EqualityHelper INSTANCE = new EqualityHelperDelegator();

    /**
     * 
     */
    private EqualityHelperDelegator() {
        super();
    }
    
    public static EqualityHelper getInstance() {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.EqualityHelper#equal(commonj.sdo.DataObject, commonj.sdo.DataObject)
     */
    public boolean equal(DataObject pDataObject1, DataObject pDataObject2) {
        return SapHelperProviderImpl.getDefaultContext().getEqualityHelper().equal(pDataObject1, pDataObject2);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.EqualityHelper#equalShallow(commonj.sdo.DataObject, commonj.sdo.DataObject)
     */
    public boolean equalShallow(DataObject pDataObject1, DataObject pDataObject2) {
        return SapHelperProviderImpl.getDefaultContext().getEqualityHelper().equalShallow(pDataObject1, pDataObject2);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append(" delegate: ");
        buf.append(SapHelperProviderImpl.getDefaultContext().getEqualityHelper());
        return buf.toString();
    }
}
