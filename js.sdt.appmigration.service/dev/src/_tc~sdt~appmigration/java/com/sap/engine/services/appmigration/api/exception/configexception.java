/**
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.appmigration.api.exception;

import com.sap.exception.BaseException;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.ResourceAccessor;
import com.sap.tc.logging.Location;

/**
 * The ConfigException is thrown when there are problems 
 * to get/store in the config manager.
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public class ConfigException extends BaseException
{
    private static final long serialVersionUID = -7409472478279185369L;
    
    public ConfigException(String msgId, Object[] parameters, 
        Location location, ResourceAccessor resourceAccessor)
    {
        super(location, resourceAccessor, msgId, parameters);
    }
    
    public ConfigException (
        String message, Object args[], Throwable throwable, 
        Location location, ResourceAccessor resourceAccessor)
    {
        super(location,
            new LocalizableTextFormatter(
            resourceAccessor, message, args), throwable);
    }
}
