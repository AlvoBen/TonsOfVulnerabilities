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
import com.sap.tc.logging.Location;


/**
 * This is the exception that should be thrown by
 * the application migration module in case the migration
 * fails
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public class MigrationException extends BaseException
{
    private static final long serialVersionUID = 2975945647587220679L;
    
    /**
     * Indicates error occured during the execution
     * of this migration module. The error is only for
     * this migration module and means that the migration 
     * modules of the other applications will be  
     * executed.
     */
    public static final int MIGRATION_MODULE_ERROR = 0;
    
    /**
     * Indicates FATAL errors occured during execution
     * of this migration module. The whole migration process 
     * will be stopped, no other migration modules of other
     * applications will be invoked. Use this error severity
     * ONLY if the error occured will prevent any other migration
     * modules to be executed too. 
     */
    public static final int FATAL_ERROR = 1;
        
    /** The severity */    
    private int severity = 0;

    /**
     * Migration exception with MIGRATION_MODULE_ERROR severity
     * 
     * @param throwable the exception
     */
    public MigrationException(Location location, Throwable throwable) {
      this(location, throwable, MIGRATION_MODULE_ERROR);
    }

    /**
     * Migration exception constructor with throwable and
     * error severity.
     * 
     * @param throwable
     * @param severity the error severity, could be MIGRATION_MODULE_ERROR
     * or FATAL_ERROR
     */
    public MigrationException(Location location, Throwable throwable, int severity) {
      super(location, throwable);
      this.severity = severity;
    }

    /** Get the error severity
     * 
     * @return the error severity
     */
    public int getSeverity() {
      return severity;
    }
}
