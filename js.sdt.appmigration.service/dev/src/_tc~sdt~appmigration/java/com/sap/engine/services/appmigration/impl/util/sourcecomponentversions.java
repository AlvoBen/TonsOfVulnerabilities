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
package com.sap.engine.services.appmigration.impl.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.appmigration.ExceptionConstants;
import com.sap.engine.services.appmigration.MigrationLogging;
import com.sap.engine.services.appmigration.MigrationResourceAccessor;
import com.sap.engine.services.appmigration.api.exception.ConfigException;
import com.sap.engine.services.appmigration.api.util.ComponentVersionsIF;
import com.sap.engine.services.appmigration.api.util.VersionIF;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * This class is used to get the source component
 * from the storage of the migration service
 * The upgrade procedure should call at some time
 * the migration service to read the BC_COMPVERS table
 * and to store the data in its own storage
 *
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public class SourceComponentVersions implements ComponentVersionsIF
{
    private ConfigurationHandler configHandler;
    private Location location =
        Location.getLocation(SourceComponentVersions.class);
    private Category category = MigrationConstantsIF.CATEGORY;
    private MigrationLogging logging = null;

    public SourceComponentVersions(ConfigurationHandler handler)
    {
        this.configHandler = handler;
        logging = new MigrationLogging(category, location);
    }

    public VersionIF getComponentVersion(String name) throws ConfigException
    {
        if (name == null)
        {
            return null;
        }

        CompVersion version = null;

        String appName =
            (name.indexOf('/') == -1) ? name : name.replace('/', '~');

        Configuration config = null;
        Configuration appConfig = null;
        try
        {
            config =
                configHandler.openConfiguration(MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME,
                    ConfigurationHandler.READ_ACCESS);
        }
        catch (Exception exc)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_OPEN_CONFIGURATION,
                new Object[] { MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME },
                exc, logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }

        try
        {
            String applicationName =
                MigrationConstantsIF.VENDOR_SAP_COM + "/" +
                replaceAppNameSlashes(appName);
            appConfig = config.getSubConfiguration(applicationName);
        }
        catch (Exception exc)
        {
            try
            {
                configHandler.closeAllConfigurations();
            }
            catch (Exception e)
            {
                throw new ConfigException(ExceptionConstants.CANNOT_CLOSE_CONFIGURATION,
                    new Object[]
                    {
                        MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME
                    }, e, logging.getLocation(),
                    MigrationResourceAccessor.getResourceAccessor());
            }
            throw new ConfigException(ExceptionConstants.CANNOT_OPEN_CONFIGURATION,
                new Object[] { MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME },
                exc, logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }

        try
        {
            Map entryMap = appConfig.getAllConfigEntries();

            version = new CompVersion();
            Set keySet = entryMap.keySet();
            Iterator keyIt = keySet.iterator();

            while (keyIt.hasNext())
            {
                String key = (String) keyIt.next();

                if (key.equals(MigrationConstantsIF.RELEASE))
                {
                    version.setRelease((String) entryMap.get(key));
                }
                else if (key.equals(MigrationConstantsIF.SP_LEVEL))
                {
                    version.setSpLevel((String) entryMap.get(key));
                }
                else if (key.equals(MigrationConstantsIF.PATCH))
                {
                    version.setPatchNumber((String) entryMap.get(key));
                }
                else if (key.equals(MigrationConstantsIF.COUNTER))
                {
                    version.setCounter((String) entryMap.get(key));
                }

                /*   else if (key.equals(MigrationConstantsIF.LOCATION))
                   {
                       version.setCompLocation((String)entryMap.get(key));
                   }  */
                version.setName(name);
            }
        }
        catch (ConfigurationException exc)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_GET_CONFIG_ENTRY,
                new Object[] { name }, exc, logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }
        finally
        {
            try
            {
                configHandler.closeAllConfigurations();
            }
            catch (ConfigurationException e)
            {
                throw new ConfigException(ExceptionConstants.CANNOT_CLOSE_CONFIGURATION,
                    new Object[]
                    {
                        MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME
                    }, e, logging.getLocation(),
                    MigrationResourceAccessor.getResourceAccessor());
            }
        }
        return version;
    }

    public VersionIF getComponentVersion(String vendor, String name,
        String componentType, String subsystem) throws ConfigException
    {
        return getComponentVersion(name);
    }

    private String replaceAppNameSlashes(String appName)
    {
        if (appName.indexOf('/') == -1)
        {
            return appName;
        }
        else
        {
            return appName.replace('/', '~');
        }
    }
}
