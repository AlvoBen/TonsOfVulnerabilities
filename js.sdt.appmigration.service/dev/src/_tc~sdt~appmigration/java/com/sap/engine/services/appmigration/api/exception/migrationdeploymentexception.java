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
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Severity;


/**
 * This exception is thrown when problems occur during
 * deployment of the migration module
 *
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public class MigrationDeploymentException
    extends DeploymentException
{
    private static final long serialVersionUID = 8236667162964132057L;

    public MigrationDeploymentException (
        String message, MigrationLogging logging)
    {
        super(
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message),
            logging.getCategory (), Severity.ERROR, logging.getLocation ());
    }

    public MigrationDeploymentException (
        String message, Throwable throwable, MigrationLogging logging)
    {
        super(
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message),
            throwable, logging.getCategory (), Severity.ERROR,
            logging.getLocation ());
    }

    public MigrationDeploymentException (
        String message, Object[] args, MigrationLogging logging)
    {
        super(
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message, args),
            logging.getCategory (), Severity.ERROR, logging.getLocation ());
    }

    public MigrationDeploymentException (
        String message, Object args, Throwable throwable, MigrationLogging logging)
    {
        super(
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message, new Object[]{args}), throwable,
            logging.getCategory (), Severity.ERROR, logging.getLocation ());
    }


    public MigrationDeploymentException (
        String message, Object args, MigrationLogging logging)
    {
        super(
            new LocalizableTextFormatter(
                MigrationResourceAccessor.getResourceAccessor (), message,
                new Object[] { args }), logging.getCategory (), Severity.ERROR,
            logging.getLocation ());
    }
}
