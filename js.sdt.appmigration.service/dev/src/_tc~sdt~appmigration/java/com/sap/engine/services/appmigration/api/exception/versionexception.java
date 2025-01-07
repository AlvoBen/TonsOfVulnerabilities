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
 * The base exception which is thrown when something 
 * is wrong with the version object, two versions cannot
 * be compared, etc.
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public class VersionException extends BaseException
{
   private static final long serialVersionUID = 5631752375806699454L;
   
   public VersionException(Location location, ResourceAccessor resourceAccessor, 
      String msgId, Object[] parameters)
   {
       super(location, resourceAccessor, msgId, parameters);
   }

   public VersionException (Location location, 
      ResourceAccessor resourceAccessor,
       String message, Object args[], Throwable throwable)
   {
       super(location,
           new LocalizableTextFormatter(
           resourceAccessor, message, args), throwable);
   }
   
   public VersionException(Location location, Throwable throwable) {
     super(location, throwable);
   }

   public VersionException(Location location, ResourceAccessor resourceAccessor, String message) {
     super(location, new LocalizableTextFormatter(resourceAccessor, message));
   }

   public VersionException(ResourceAccessor resourceAccessor, 
      String msgId, Object[] parameters)
   {
       super(resourceAccessor, msgId, parameters);
   }
   
   public VersionException(ResourceAccessor resourceAccessor, String message) {
     super(new LocalizableTextFormatter(resourceAccessor, message));
   }
      
   public VersionException(Throwable throwable) {
     super(throwable);
   }   
}
