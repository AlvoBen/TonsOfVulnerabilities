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
package com.sap.sdo.impl.xml;

import com.sap.sdo.api.helper.NamingHandler;
import com.sap.sdo.impl.types.builtin.PropertyType;

import commonj.sdo.DataObject;

public class DefaultRenamingHandler implements NamingHandler {

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.NamingHandler#nameOpenContentProperty(commonj.sdo.DataObject, java.lang.String, java.lang.String, boolean)
     */
    public void nameOpenContentProperty(
        final DataObject propertyObj,
        final String uri,
        final String localName,
        final boolean isElement, final DataObject pParent) {

        propertyObj.set(PropertyType.NAME, localName);
    }

}