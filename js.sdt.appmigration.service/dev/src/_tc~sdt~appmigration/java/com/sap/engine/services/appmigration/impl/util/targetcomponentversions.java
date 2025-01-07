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
package com.sap.engine.services.appmigration.impl.util;


import javax.sql.DataSource;

import com.sap.engine.services.appmigration.ExceptionConstants;
import com.sap.engine.services.appmigration.MigrationLogging;
import com.sap.engine.services.appmigration.MigrationResourceAccessor;
import com.sap.engine.services.appmigration.api.exception.ConfigException;
import com.sap.engine.services.appmigration.api.util.ComponentVersionsIF;
import com.sap.engine.services.appmigration.api.util.VersionIF;
import com.sap.sl.util.components.api.ComponentElementIF;
import com.sap.sl.util.cvers.api.CVersAccessException;
import com.sap.sl.util.cvers.api.CVersFactoryIF;
import com.sap.sl.util.cvers.api.CVersManagerIF;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * This class is used to get the target component
 * from the storage of the migration service
 * The upgrade procedure should call at some time
 * the migration service to read the BC_COMPVERS table
 * and to store the data in its own storage
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public class TargetComponentVersions implements ComponentVersionsIF
{

    private Location location = Location.getLocation(TargetComponentVersions.class);
    private Category category = MigrationConstantsIF.CATEGORY;
    
    private CVersFactoryIF factory;
    private CVersManagerIF manager;
    private MigrationLogging logging;
   
    public TargetComponentVersions(DataSource dataSource)
        throws CVersAccessException
    {
        factory = CVersFactoryIF.getInstance();
        manager = factory.createCVersManager(dataSource);        
        logging = new MigrationLogging(category, location);
    }
   
    /* (non-Javadoc)
     * @see com.sap.engine.services.appmigration.api.util.ComponentVersionsIF#getComponentVersion(java.lang.String)
     */
    public VersionIF getComponentVersion(String vendor, String name, String componentType, String subsystem)
        throws ConfigException
    {
        CompVersion version = null;
        logging.logDebug("Search for component from COMPVERS with name " +
                         name + " vendor " + vendor);

        try
        {   
            ComponentElementIF component = manager.readCVers(vendor, name, componentType, subsystem);
            
            if (component != null)
            {
                version = new CompVersion();
                version.setName(component.getName());
                version.setPatchNumber(component.getPatchLevel());
                version.setSpLevel(component.getSPVersion());
                version.setVendor(component.getVendor());
               // version.setCompLocation(component.getLocation());
                version.setCounter(component.getCounter());
                version.setRelease(component.getRelease());
                
            } else
            {
                // just for logging purposes
                // the component with this parameters was not found
                logging.logInfo("Component with name " + name + " vendor " + 
                                vendor + " componentType " + componentType +
                                " subsystem " + subsystem + " was not found in database.");
            }
            
        } catch (Exception exc)
        {
            ConfigException e = new ConfigException(ExceptionConstants.CANNOT_GET_CONFIG_ENTRY,
            new Object[] {name}, exc, location, MigrationResourceAccessor.getResourceAccessor()); 
            
            throw e;
        }
        
        return version;
    }


    public VersionIF getComponentVersion(String name)
        throws ConfigException
    {
        CompVersion version = null;
        logging.logDebug("Search for component from COMPVERS with name " + name);

        try
        {
            ComponentElementIF component = manager.readCVers(
                 MigrationConstantsIF.VENDOR_SAP_COM,    
                 name, 
                 ComponentElementIF.COMPONENTTYPE_SC, 
                 ComponentElementIF.DEFAULTSUBSYSTEM);
            
            if (component != null)
            {
                version = new CompVersion();
                version.setName(component.getName());
                version.setPatchNumber(component.getPatchLevel());
                version.setSpLevel(component.getSPVersion());
                version.setVendor(component.getVendor());
              //  version.setCompLocation(component.getLocation());
                version.setCounter(component.getCounter());
                version.setRelease(component.getRelease());
                
            } else 
            {
                // just for logging purposes
                // the component with this parameters was not found
                logging.logInfo("Component with name " + name 
                                + " was not found in database.");
            }
        
        } catch (Exception exc)
        {
            ConfigException e = new ConfigException(ExceptionConstants.CANNOT_GET_CONFIG_ENTRY,
            new Object[] {name}, exc, location, MigrationResourceAccessor.getResourceAccessor()); 
            
            throw e;
        }
        
        return version;
    }
    

}
