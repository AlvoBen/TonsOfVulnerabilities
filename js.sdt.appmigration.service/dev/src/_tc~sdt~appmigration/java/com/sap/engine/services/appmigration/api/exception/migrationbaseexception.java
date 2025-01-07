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

import com.sap.engine.services.appmigration.MigrationLogging;
import com.sap.engine.services.appmigration.MigrationResourceAccessor;
import com.sap.exception.BaseException;
import com.sap.localization.LocalizableTextFormatter;

/**
 * This is the base exception for the migration service
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public class MigrationBaseException extends BaseException
{
    private static final long serialVersionUID = -7398315810093087776L;
    
    public MigrationBaseException (
        String message, MigrationLogging logging)
    {
        super(logging.getLocation(),
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message));
    }

    public MigrationBaseException (
        String message, Throwable throwable, MigrationLogging logging)
    {
        super(logging.getLocation(),
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message),
            throwable);
    }

    public MigrationBaseException (
        String message, Object[] args, MigrationLogging logging)
    {
        super(logging.getLocation(),
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message, args));
    }

    public MigrationBaseException (
        String message, Object args, Throwable throwable, MigrationLogging logging)
    {
        super(logging.getLocation(),
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message, new Object[]{args}), throwable);
    }

    public MigrationBaseException (
        String message, Object args[], Throwable throwable, MigrationLogging logging)
    {
        super(logging.getLocation(),
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message, args), throwable);
    }

    public MigrationBaseException (
        String message, Object args, MigrationLogging logging)
    {
        super(logging.getLocation(),
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message,
                new Object[] { args }));
    }
}
