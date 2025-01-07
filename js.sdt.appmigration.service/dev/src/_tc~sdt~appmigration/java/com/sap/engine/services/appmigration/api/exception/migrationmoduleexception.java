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


/**
 * This exception is thrown when something is wrong with the 
 * migration module structure - the main method does not implement
 * the ApplicationMigrationIF, something wrong with its classes, etc.
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public class MigrationModuleException extends MigrationBaseException
{
   private static final long serialVersionUID = 6570976012527598993L;
    
   public MigrationModuleException(String message, MigrationLogging logging)
   {
       super(message, logging);   
   }
   
   public MigrationModuleException( String message, Throwable throwable, MigrationLogging logging)
   {
       super(message, throwable, logging);
   }
   
   public MigrationModuleException(String message, Object[] args, MigrationLogging logging)
   {
       super(message, args, logging);
   }
   
   public MigrationModuleException(String message, Object args[], Throwable throwable, MigrationLogging logging)
   {
       super(message, args, throwable, logging);
   }

   public MigrationModuleException(String message, Object args, Throwable throwable, MigrationLogging logging)
   {
       super(message, args, throwable, logging);
   }   
}
