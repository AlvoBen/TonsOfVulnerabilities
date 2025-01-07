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

import com.sap.sdo.api.helper.ErrorHandler;

public class DefaultErrorHandler implements ErrorHandler {
    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.ErrorHandler#handleInvalidValue(java.lang.RuntimeException)
     */
    public void handleInvalidValue(final RuntimeException pException) {
        throw pException;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.ErrorHandler#handleUnknownProperty(java.lang.RuntimeException)
     */
    public void handleUnknownProperty(final RuntimeException pException) {
        throw pException;
    }
}