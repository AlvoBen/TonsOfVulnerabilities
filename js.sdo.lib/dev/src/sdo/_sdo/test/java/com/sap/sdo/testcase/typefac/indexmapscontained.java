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
import com.sap.sdo.api.SdoTypeMetaData;

/**
 * @author D042774
 *
 */
@SdoTypeMetaData(open=true)
public interface IndexMapsContained {

    String getReadOnlyString();

    Integer getReadOnlyInteger();

    String getString();
    void setString(String string);
    

    Integer getInteger();
    void setInteger(Integer integer);
    
    @SdoPropertyMetaData(
        opposite = "references",
        containment = false
    )
    IndexMapsOpposite getOpposite();
    void setOpposite(IndexMapsOpposite opposite);
}
