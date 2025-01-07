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
package com.sap.sdo.testcase.typefac;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface SimpleContainingIntf {
    String getX();

    void setX(String x);

    @SdoPropertyMetaData(containment=true)
    SimpleContainedIntf getInner();

    void setInner(SimpleContainedIntf i);
}
