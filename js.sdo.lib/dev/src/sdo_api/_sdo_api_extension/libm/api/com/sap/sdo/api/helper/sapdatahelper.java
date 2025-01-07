/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.api.helper;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public interface SapDataHelper extends DataHelper {

    /**
     * Set it to false to disable the read-only checks.
     * @param pDataObject data object to change read-only mode for.
     * @param pActivated true, to enable and false to disable.
     */
    void setReadOnlyMode(DataObject pDataObject, boolean pActivated);

    /**
     * Get helper context where the given data object's type is defined in.
     * @param pDataObject data object assigned to a helper context.
     * @return used helper context.
     */
    HelperContext getHelperContext(DataObject pDataObject);
    
    Object project(DataObject pDataObject);
}
