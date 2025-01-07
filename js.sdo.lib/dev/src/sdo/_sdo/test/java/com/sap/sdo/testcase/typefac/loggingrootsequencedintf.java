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

import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.SdoTypeMetaData;

import commonj.sdo.ChangeSummary;

@SdoTypeMetaData(open=true, sequenced=true)
public interface LoggingRootSequencedIntf {
    
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
    
    List<String> getData();
    void setData(List<String> strings);
}
