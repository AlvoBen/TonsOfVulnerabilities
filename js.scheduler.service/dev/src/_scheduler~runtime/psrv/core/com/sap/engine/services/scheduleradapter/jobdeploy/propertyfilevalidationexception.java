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
 * Exeception class if used when the localization property files are not valid.
 * 
 * @author d040939
 */
public class PropertyFileValidationException extends Exception {
    
    private static final long serialVersionUID = 4199080844227259137L;
    
    public PropertyFileValidationException(String msg) {

        super(msg);
    }
    
    public PropertyFileValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
