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

/**
 * Handler to affect program logic in case of parse failures.
 * 
 * @author D042774
 *
 */
public interface ErrorHandler {
    /**
     * Indicates what to do in case of parsing undefined properties for non-open types.
     * If this method throws an exception the parsing of XML would be interrupted.
     * Otherwise it is possible to log the exception at this point and parse on.
     * The current property will not be defined.
     * 
     * @param pException raised exception
     */
    public void handleUnknownProperty(RuntimeException pException);

    /**
     * Indicates what to do in case of parsing invalid content of properties.
     * If this method throws an exception the parsing of XML would be interrupted.
     * Otherwise it is possible to log the exception at this point and parse on.
     * The current property stays unset.
     * 
     * @param pException raised exception
     */
    public void handleInvalidValue(RuntimeException pException);
}
