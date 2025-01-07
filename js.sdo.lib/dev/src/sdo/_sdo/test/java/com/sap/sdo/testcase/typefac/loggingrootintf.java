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

import commonj.sdo.ChangeSummary;

public interface LoggingRootIntf {
    
    ChangeSummary getChangeSummary();

    @SdoPropertyMetaData(containment=true)
    SimpleContainingIntf getSimpleContainingIntf();
    void setSimpleContainingIntf(SimpleContainingIntf i);

    @SdoPropertyMetaData(containment=true)
    OpenInterface getOpenInterface();
    void setOpenInterface(OpenInterface i);

    @SdoPropertyMetaData(containment=true)
    OpenSequencedInterface getOpenSequencedInterface();
    void setOpenSequencedInterface(OpenSequencedInterface i);
}
