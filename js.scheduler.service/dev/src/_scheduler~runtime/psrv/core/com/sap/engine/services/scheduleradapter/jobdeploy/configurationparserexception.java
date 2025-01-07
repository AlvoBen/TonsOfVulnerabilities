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
package com.sap.engine.services.scheduleradapter.jobdeploy;

/**
 * Exception that the configuration parser may throw.
 * 
 * @author Dirk Marwinski
 */
public class ConfigurationParserException extends Exception {

    /**
     * 
     */
    public ConfigurationParserException(String msg) {

        super(msg);
    }
    
    public ConfigurationParserException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
    