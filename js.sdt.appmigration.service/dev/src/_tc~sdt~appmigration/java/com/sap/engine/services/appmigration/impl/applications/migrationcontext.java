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
package com.sap.engine.services.appmigration.impl.applications;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.appmigration.ExceptionConstants;
import com.sap.engine.services.appmigration.MigrationLogging;
import com.sap.engine.services.appmigration.MigrationResourceAccessor;
import com.sap.engine.services.appmigration.api.applications.MigrationContextIF;
import com.sap.engine.services.appmigration.api.exception.ConfigException;
import com.sap.engine.services.appmigration.api.util.ComponentVersionsIF;
import com.sap.engine.services.appmigration.api.util.StatusIF;
import com.sap.engine.services.appmigration.api.util.VersionFactoryIF;
import com.sap.engine.services.appmigration.api.util.VersionIF;
import com.sap.engine.services.appmigration.impl.util.CompVersion;
import com.sap.engine.services.appmigration.impl.util.MigrationConstantsIF;
import com.sap.engine.services.appmigration.impl.util.SourceComponentVersions;
import com.sap.engine.services.appmigration.impl.util.Status;
import com.sap.engine.services.appmigration.impl.util.TargetComponentVersions;
import com.sap.engine.services.appmigration.impl.util.VersionFactory;
import com.sap.sl.util.cvers.api.CVersAccessException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * This is the implementation of the MigrationContextIF,
 * that is passed to the applications
 *
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public class MigrationContext implements MigrationContextIF
{
    // private Configuration config;
    private MigrationLogging logging;
    private Status status;
    private SourceComponentVersions sourceCompVersions;
    private TargetComponentVersions targetCompVersions;
    private VersionFactoryIF versionFactory;
    private ConfigurationHandler configHandler;
    private String applicationName;
    private Configuration migrationConfig;
    private Location location = Location.getLocation(MigrationContext.class);
    private Category category = MigrationConstantsIF.CATEGORY;
    private final Location migModuleLocation = Location.getLocation(MigrationContextIF.class);
    private ClassLoader appLoader;
    private DataSource dataSource;

    public MigrationContext(ConfigurationHandler configHandler, String appName,
        DataSource dataSource, ClassLoader appLoader)
    {
        // create the migration context object
        this.configHandler = configHandler;
        this.applicationName = appName;
        this.appLoader = appLoader; 
        this.dataSource = dataSource;

        //init logging
        this.logging = new MigrationLogging(category, location);

        // create VersionFactory and status object            
        status = new Status(configHandler, appName, logging);
        versionFactory = new VersionFactory();

        try
        { // the targetComponentVersions object is used
          // to get the target version of specified components
            targetCompVersions = new TargetComponentVersions(dataSource);
        }
        catch (CVersAccessException exc)
        {
            ConfigException migrationExc =
                new ConfigException(ExceptionConstants.CANNOT_CREATE_COMPONENT,
                    new Object[] {  }, exc, logging.getLocation(),
                    MigrationResourceAccessor.getResourceAccessor());

            logging.logThrowable("Cannot create target component version ",
                migrationExc);
        }

        // used to get the source components versions
        sourceCompVersions = new SourceComponentVersions(configHandler);

        logging.logDebug("Created migration context for " + appName);
    }

    public boolean isRestart() throws ConfigException
    {
        logging.logDebug("start isRestart()");

        Boolean isRestart = null;
        Configuration appConfig = null;
        try
        {
            try
            {
                openCfg();
                appConfig =
                    migrationConfig.getSubConfiguration(applicationName);
            }
            catch (Exception exc)
            {
                throw new ConfigException(ExceptionConstants.CANNOT_GET_SUBCONFIGURATION,
                    new Object[] { appConfig.getPath() }, exc,
                    logging.getLocation(),
                    MigrationResourceAccessor.getResourceAccessor());
            }
            try
            {
                if (appConfig != null)
                {
                    isRestart =
                        (Boolean) appConfig.getConfigEntry(MigrationConstantsIF.RESTART);
                }
                else
                {
                    return false;
                }
            }
            catch (NameNotFoundException exc)
            {
                //$JL-EXC$
                logging.logInfo("The config entry " +
                    MigrationConstantsIF.RESTART + " does not exist in config");

                // not exist in config manager
                return false;
            }
            catch (Exception exc)
            {
                ConfigException migrationExc =
                    new ConfigException(ExceptionConstants.CANNOT_GET_CONFIG_ENTRY,
                        new Object[] { MigrationConstantsIF.RESTART }, exc,
                        logging.getLocation(),
                        MigrationResourceAccessor.getResourceAccessor());

                logging.logThrowable("Cannot get config entry " +
                    MigrationConstantsIF.RESTART + " from config ", migrationExc);
            }
        }
        finally
        {
            try
            {
                cleanUp();
            }
            catch (Exception exc)
            {
                throw new ConfigException(ExceptionConstants.CANNOT_CLOSE_CONFIGURATION,
                    new Object[] { MigrationConstantsIF.RESTART }, exc,
                    logging.getLocation(),
                    MigrationResourceAccessor.getResourceAccessor());
            }
        }
        logging.logDebug("end isRestart() ");
        return (isRestart != null) && (isRestart.equals(Boolean.TRUE));
    }

/*
    private void setIsRestarted(String appName)
        throws ConfigException
    {
        logging.logDebug("setIsRestarted ... ");
        Configuration appConfig = null;
        try
        {
            openCfg ();
            appConfig = migrationConfig.getSubConfiguration (appName);
        }
        catch (NameNotFoundException exc)
        {
            try
            {
                appConfig =
                    migrationConfig.createSubConfiguration (appName);
            }
            catch (Exception cex)
            {
                throw new ConfigException(
                     ExceptionConstants.CANNOT_CREATE_SUBCONFIGURATION,
                     new Object[] {appConfig.getPath()}, cex,
                     logging.getLocation(),
                     MigrationResourceAccessor.getResourceAccessor());
            }
        }
        catch (Exception exc)
        {
            throw new ConfigException(
                 ExceptionConstants.CANNOT_GET_SUBCONFIGURATION,
                 new Object[] {appConfig.getPath()}, exc,
                 logging.getLocation(),
                 MigrationResourceAccessor.getResourceAccessor());
        }
        try
        {
            appConfig.modifyConfigEntry (
            MigrationConstantsIF.RESTART, new Boolean(true), true);
            logging.logDebug("isRestart property modified");
        } catch (Exception exc)
        {
            throw new ConfigException(
                 ExceptionConstants.CANNOT_SET_CONFIG_ENTRY,
                 new Object[] {MigrationConstantsIF.RESTART}, exc,
                 logging.getLocation(),
                 MigrationResourceAccessor.getResourceAccessor());
        } finally
        {
            try
            {
                cleanUp();
            } catch (Exception exc)
            {
                throw new ConfigException(
                ExceptionConstants.CANNOT_CLOSE_CONFIGURATION,
                new Object[] { MigrationConstantsIF.RESTART }, exc, logging.getLocation (),
                MigrationResourceAccessor.getResourceAccessor ()
                );
            }
        }
    }
*/

    /**
     * This private method is used to get the product version
     * set by the upgrade procedure.
     * isSourceVersion is true to get the product source version
     * and false to get the target version
     */
    private VersionIF getProductVersion(boolean isSourceVersion)
        throws ConfigException
    {
        CompVersion compVersion = new CompVersion();

        String engineConfig = null;
        if (isSourceVersion)
        {
            engineConfig = MigrationConstantsIF.ENGINE_VERSION_SOURCE;
        }
        else
        {
            engineConfig = MigrationConstantsIF.ENGINE_VERSION_TARGET;
        }

        if (configHandler != null)
        {
            Configuration containerConfig = null;
            try
            {
                String configPath =
                    MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME + "/" +
                    MigrationConstantsIF.VENDOR_SAP_COM + "/" + engineConfig;

                logging.logDebug("getProductVersion open configuration " +
                    configPath);

                containerConfig =
                    configHandler.openConfiguration(configPath,
                        ConfigurationHandler.READ_ACCESS);

                if (containerConfig != null)
                {
                    Map entryMap = containerConfig.getAllConfigEntries();
                    Set keySet = entryMap.keySet();
                    Iterator keyIt = keySet.iterator();

                    while (keyIt.hasNext())
                    {
                        String key = (String) keyIt.next();

                        if (key.equals(MigrationConstantsIF.RELEASE))
                        {
                            compVersion.setRelease((String) entryMap.get(key));
                        }
                        else if (key.equals(MigrationConstantsIF.SP_LEVEL))
                        {
                            compVersion.setSpLevel((String) entryMap.get(key));
                        }
                        else if (key.equals(MigrationConstantsIF.PATCH))
                        {
                            compVersion.setPatchNumber((String) entryMap.get(
                                    key));
                        }
                        else if (key.equals(MigrationConstantsIF.COUNTER))
                        {
                            compVersion.setCounter((String) entryMap.get(key));
                        }

                        /* else if (key.equals (MigrationConstantsIF.LOCATION))
                         {
                             compVersion.setCompLocation (
                                 (String) entryMap.get (key));
                         } */
                    }

                    compVersion.setName(MigrationConstantsIF.ENGINE_VERSION_SOURCE);
                }
            }
            catch (ConfigurationException exc)
            {
                throw new ConfigException(ExceptionConstants.CANNOT_GET_CONFIG_ENTRY,
                    new Object[] { engineConfig }, exc, logging.getLocation(),
                    MigrationResourceAccessor.getResourceAccessor());
            }
            finally
            {
                try
                {
                    cleanUp();
                }
                catch (Exception exc)
                {
                    throw new ConfigException(ExceptionConstants.CANNOT_CLOSE_CONFIGURATION,
                        new Object[]
                        {
                            MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME
                        }, exc, logging.getLocation(),
                        MigrationResourceAccessor.getResourceAccessor());
                }
            }
        }
        logging.logDebug("End getProductVersion() " + compVersion);
        return compVersion;
    }

    public VersionIF getProductSourceVersion() throws ConfigException
    {
        logging.logDebug("Start getProductSourceVersion()");
        return getProductVersion(true);
    }

    public VersionIF getProductTargetVersion() throws ConfigException
    {
        logging.logDebug("Start getProductTargetVersion()");
        return getProductVersion(false);
    }

    public ComponentVersionsIF getSourceComponentVersions()
    {
        return sourceCompVersions;
    }

    public ComponentVersionsIF getTargetComponentVersions()
    {
        return targetCompVersions;
    }

    public StatusIF getStatusObj()
    {
        return status;
    }

    public Category getCategory()
    {
        return MigrationConstantsIF.MIGRATION_MODULE_CATEGORY;
    }

    public Location getLocation()
    {
        return migModuleLocation;
    }
    public Object getApplicationParam(String name, boolean isFileParam) throws ConfigException
    {
        logging.logDebug("Start getApplicationParam()");
        Object appParam = null;
        Configuration appConfig = null;
        try
        {
            try
            {
                openCfg();
                appConfig =
                    migrationConfig.getSubConfiguration(applicationName);
            }
            catch (Exception exc)
            {
                throw new ConfigException(ExceptionConstants.CANNOT_GET_SUBCONFIGURATION,
                    new Object[] { appConfig.getPath() }, exc,
                    logging.getLocation(),
                    MigrationResourceAccessor.getResourceAccessor());
            }

            try
            {
                String appName = applicationName;

                if (appConfig != null)
                {
                    if (isFileParam)
                    {
                        appParam = appConfig.getFile(name);
                    } else
                    {
                        appParam = appConfig.getConfigEntry(name);   
                    }

                    logging.logInfo("The config entry " + name + " has value " +
                        appParam);
                }
                else
                {
                   
                    // not exist in config manager
                    return appParam;
                }
            }
            catch (NameNotFoundException exc)
            {
                // $JL-EXC$ 
                // not exist in config manager, such entry has not been
                // set for this application by the upgrade procedure, do nothing
                logging.logInfo("The application parameter " + name +
                    " does not exist for the application " + applicationName +
                    " path " + appConfig.getPath());
                logging.logDebug("End getApplicationParam()");
                return appParam;
            }
            catch (Exception exc)
            {
                ConfigException migrationExc =
                    new ConfigException(ExceptionConstants.CANNOT_GET_CONFIG_ENTRY,
                        new Object[] { name }, exc, logging.getLocation(),
                        MigrationResourceAccessor.getResourceAccessor());

                logging.logThrowable("Cannot get config entry " + name +
                    " from config ", migrationExc);
            }
        }
        finally
        {
            try
            {
                cleanUp();
            }
            catch (Exception exc)
            {
                throw new ConfigException(ExceptionConstants.CANNOT_CLOSE_CONFIGURATION,
                    new Object[] { MigrationConstantsIF.RESTART }, exc,
                    logging.getLocation(),
                    MigrationResourceAccessor.getResourceAccessor());
            }
        }
        logging.logDebug("End getApplicationParam()");
        return appParam;

    }

    public Object getApplicationParam(String name) throws ConfigException
    {
        return getApplicationParam(name, true);
    }

    public Object getApplicationParamEntry(String name) throws ConfigException
    {
        return getApplicationParam(name, false);
    }
    
    public VersionFactoryIF getVersionFactory()
    {
        return versionFactory;
    }

    public ConfigurationHandler getConfigHandler()
    {
        return configHandler;
    }

    public void setConfigHandler(ConfigurationHandler configHandler)
    {
        this.configHandler = configHandler;
    }

    private void openCfg() throws ConfigurationException
    {
        // open the migration configuration for writing
        migrationConfig =
            configHandler.openConfiguration(
            	MigrationConstantsIF.MIGRATION_MODULES_CONFIG,
                ConfigurationHandler.WRITE_ACCESS);
    }

    private void cleanUp() throws ConfigurationException
    {
        // commit any uncommitted transactions 
        // and close configuration 
        if (configHandler != null)
        {
            migrationConfig = null;
            configHandler.commit();
            configHandler.closeAllConfigurations();
        }
    }

    public ClassLoader getApplicationLoader() {
        
        return appLoader;
    }
    
    
    public String[] getSourceProducts()
    {
    	return getEntries(MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME
    			+ "/" + MigrationConstantsIF.SOURCE_PRODUCTS);
    }

  	
	public String[] getTargetProducts()
	{
		return getEntries(MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME
    			+ "/" + MigrationConstantsIF.TARGET_PRODUCTS);
	}
	  
  
	public String[] getSourceUsages()
	{
		return getEntries(MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME
    			+ "/" + MigrationConstantsIF.SOURCE_USAGES);
	}
	  

	public String[] getTargetUsages()
	{
		return getEntries(MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME
    			+ "/" + MigrationConstantsIF.TARGET_USAGES);
	}
	
	private String[] getEntries(String configPath)
	{
		String[] result = null;
		if (configHandler != null)
		{
			Configuration config = null;
		
			try
			{
				config = 
					configHandler.openConfiguration(
							configPath, ConfigurationHandler.READ_ACCESS);
				result = config.getAllConfigEntryNames();
			} catch (Exception exc)
			{
				logging.logThrowable("Cannot open configuration " 
					+ configPath,
				    exc);
				return result;
			} finally
			{
				if (config != null)
				{
					try
					{
					    config.close();
					} catch (Exception closeExc)
					{
						logging.logThrowable("Cannot close configuration " 
								+ configPath,
							    closeExc);
					}
				}
			}
				
		}
		return result;
	}
	
	
	public DataSource getSystemDataSource()
	{
	 	return dataSource;
	}
}
