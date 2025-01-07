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
import commonj.sdo.helper.CopyHelper;

/**
 * @author D042774
 *
 */
public class CopyHelperDelegator implements CopyHelper {
    private static final CopyHelper INSTANCE = new CopyHelperDelegator();

    /**
     * 
     */
    private CopyHelperDelegator() {
        super();
    }
    
    public static CopyHelper getInstance() {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.CopyHelper#copy(commonj.sdo.DataObject)
     */
    public DataObject copy(DataObject pDataObject) {
        return SapHelperProviderImpl.getDefaultContext().getCopyHelper().copy(pDataObject);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.CopyHelper#copyShallow(commonj.sdo.DataObject)
     */
    public DataObject copyShallow(DataObject pDataObject) {
        return SapHelperProviderImpl.getDefaultContext().getCopyHelper().copyShallow(pDataObject);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append(" delegate: ");
        buf.append(SapHelperProviderImpl.getDefaultContext().getCopyHelper());
        return buf.toString();
    }
}
