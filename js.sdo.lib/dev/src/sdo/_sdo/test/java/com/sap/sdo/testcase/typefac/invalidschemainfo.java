/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.typefac;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "non-existing.xsd"
)
public interface InvalidSchemaInfo {
    String getProp();
    void setProp(String pString);
}
