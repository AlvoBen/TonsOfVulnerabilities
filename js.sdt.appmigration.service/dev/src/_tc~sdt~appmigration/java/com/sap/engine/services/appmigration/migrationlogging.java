/*
 * Copyright (C) 2000 - 2005 by SAP AG, Walldorf,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.appmigration;

import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


/**
 * Creates MigrationLogging object with usefull methods for 
 * logging and tracing purposes
 *
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public class MigrationLogging
{
    /**
     * The category of the resource accessor.
     */
    private Category category = null;

    /**
     * The location of the resource accessor.
     */
    private Location location = null;
 
    
    //~ Methods ----------------------------------------------------------------
    public MigrationLogging(Category _category, Location _location)
    {
        category = _category;
        location = _location;        
    }
    
   
    /**
     * Logs debug messages.
     *
     * @param message the message to be logged.
     */
    public void logDebug (String message)
    {   
        if (
            (category != null) &&
                (location != null))
        {
            location.logT(Severity.DEBUG, category, message);
           // category.logT (
           //     Severity.DEBUG, location, message);
        }
    }

    /**
     * Logs error messages.
     *
     * @param message the message to be logged.
     */
    public void logDebug (String message, Object[] objects)
    {

        if (
            (category != null) &&
                (location != null))
        {
            location.logT(Severity.DEBUG, category, message);
            //category.logT(
            //    Severity.DEBUG, location, message, objects);
        }
    }
    
    /**
     * Logs warning messages.
     *
     * @param message the message to be logged.
     */
    public void logWarning (String message)
    {
        if (
            (category != null) &&
                (location != null))
        {
          //  location.logT(Severity.WARNING, category, message);
            category.logT (
                Severity.WARNING, location, message);
        }
    }

    /**
     * Logs error messages.
     *
     * @param message the message to be logged.
     */
    public void logError (String message)
    {
        if (
            (category != null) &&
                (location != null))
        {
            //location.logT(Severity.ERROR, category, message);
            category.logT (
                Severity.ERROR, location, message);
        }
    }

    /**
     * Logs error messages.
     *
     * @param message the message to be logged.
     */
    public void logThrowable (String message, Throwable throwable)
    {

        if (
            (category != null) &&
                (location != null))
        {
           
            category.logThrowableT(
                Severity.ERROR, location, message, throwable);
        }
    }

    /**
     * Logs info messages.
     *
     * @param message the message to be logged.
     */
    public void logInfo (String message)
    {
        if (
            (category != null) &&
                (location != null))
        {
           // location.logT(Severity.INFO, category, message);
           
            category.logT (
                Severity.INFO, location, message);
        }
    }

    /**
     * Logs path messages.
     *
     * @param message the message to be logged.
     */
    public void logPath (String message)
    {
        if (
            (category != null) &&
                (location != null))
        {
            location.logT(Severity.PATH, category, message);

            //category.logT (
            //    Severity.PATH, location, message);
        }
    }

    /**
     * Logs fatal messages.
     *
     * @param message the message to be logged.
     */
    public void logFatal (String message)
    {
        if (
            (category != null) &&
                (location != null))
        {
            //location.fatal(category, message);
            category.logT (
                Severity.FATAL, location, message);
        }
    }
        
    public Category getCategory()
    {
        return category;
    }
    
    public Location getLocation()
    {
        return location;
    }
}
