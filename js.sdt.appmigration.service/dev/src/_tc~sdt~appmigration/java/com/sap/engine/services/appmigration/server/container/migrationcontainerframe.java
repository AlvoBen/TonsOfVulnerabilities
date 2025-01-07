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
package com.sap.engine.services.appmigration.server.container;

import java.rmi.RemoteException;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.services.appmigration.ExceptionConstants;
import com.sap.engine.services.appmigration.MigrationLogging;
import com.sap.engine.services.appmigration.MigrationResourceAccessor;
import com.sap.engine.services.appmigration.api.exception.ConfigException;
import com.sap.engine.services.appmigration.impl.upgrade.JUpgrade;
import com.sap.engine.services.appmigration.impl.util.MigrationConstantsIF;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.deploy.container.migration.exceptions.CMigrationException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This class is a migration service, that
 * provides to the applications possibility to
 * migrate their data between different engine
 * releases. It provides also to the upgrade
 * procedure an API that can be used to get information
 * about the migration modules, their status, etc.
 *
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public final class MigrationContainerFrame implements ApplicationServiceFrame,
    ContainerEventListener
{
    private static final int mask =
        ContainerEventListener.MASK_INTERFACE_AVAILABLE |
        ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE;
    private static Location location =
        Location.getLocation(MigrationContainerFrame.class);
    private static MigrationLogging logging =
        new MigrationLogging(Category.SYS_SERVER, location);
    private static ConfigurationHandlerFactory factory = null;
    private static ConfigurationHandler serviceConfigHandler = null;
    private ApplicationServiceContext appServiceContext;
    private MigrationContainer migrationContainer;
    private ContainerManagement deployService = null;
    private DeployCommunicator communicator = null;
    private JUpgrade upgrade = null;

    public void start(ApplicationServiceContext serviceContext)
        throws ServiceException
    {
    	
    	SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000054", "Starting Migration Service");
       
        this.appServiceContext = serviceContext;


        try
        { // register JUpgradeIF to be accessible for lookup 
          // from the upgrade procedure
            if (factory == null)
            {
                factory =
                    appServiceContext.getCoreContext()
                    .getConfigurationHandlerFactory();
            }
            if (serviceConfigHandler == null)
            {
                serviceConfigHandler = getServiceConfigHandler(factory);
            }
            if (needConfiguration(serviceConfigHandler))
            {
            	SimpleLogger.trace(Severity.DEBUG, location, "SDT.migration.000055", 
            			"Configuration should be created for the migration service");

                createConfig(serviceConfigHandler);
            }
            else
            {
            	SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000056", 
            			"Configuration for the migration service already exist. It won't be created.");
            }

            SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000057", 
            		"Start migration service {0}", serviceConfigHandler);
            
            upgrade = new JUpgrade(serviceConfigHandler);
            serviceContext.getContainerContext().getObjectRegistry()
            .registerInterface(upgrade);
        }
        catch (ConfigException exc)
        {
        	SimpleLogger.traceThrowable(Severity.ERROR, location,exc, "SDT.migration.000058",
        			"Cannot create/get Migration Service configuration");
            //logging.logThrowable("Config exception Cannot create/get Migration configuration",
            //    exc);
            
            throw new ServiceException(ServiceException.SERVICE_NOT_STARTED,
                new String[] { "Migration Service" }, exc);
        }
        catch (RemoteException exc)
        {
        	
        	SimpleLogger.traceThrowable(Severity.ERROR, location, exc, "SDT.migration.000059", 
        			"Cannot instantiate JUpgrade object");
            //logging.logThrowable("Remote exception Cannot instantiate JUpgrade",
            //    exc);
            
            throw new ServiceException(ServiceException.SERVICE_NOT_STARTED,
                new String[] { "MIGRATION" }, exc);
        }

        // initsializes and start 
        // the migration container to perform the migration
        try
        {
            migrationContainer = new MigrationContainer(appServiceContext);
        }
        catch (ConfigException exc)
        {
        	SimpleLogger.traceThrowable(Severity.ERROR, location, exc, 
        			"SDT.migration.000060", "Configuration exception");
            
        	//logging.logThrowable("ConfigException ", exc);
          
        	throw new ServiceException(ServiceException.SERVICE_NOT_STARTED,
                new String[] { "MIGRATION" }, exc);
        }

        Set names = new HashSet(5);
        names.add("container");
        names.add("deploy");
        appServiceContext.getServiceState().registerContainerEventListener(mask,
            names, this);

        SimpleLogger.trace(Severity.DEBUG, location, "SDT.migration.000061",
        		"Migration Service started successfully");
       
    }

    public void containerStarted()
    {
    	SimpleLogger.trace(Severity.DEBUG, location, "SDT.migration.000062", 
    			"The migration container is started.");
        //logging.logDebug("The migration container is started.");
    }

    public void beginContainerStop()
    {
        SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000063", 
        		"The migration container will be stopped.");
    	//logging.logDebug("The migration container will be stopped.");
    }

    public void serviceStarted(String serviceName, Object serviceInterface)
    {
    	
      //  logging.logDebug("Migration: serviceStarted method. Service started " +
      //      serviceName);
    }

    public void serviceNotStarted(String arg0)
    {
       // logging.logDebug("Migration: serviceNotStarted method. Service  " +
       //     arg0);
    }

    public void beginServiceStop(String arg0)
    {
    }

    public void serviceStopped(String arg0)
    {
    }

    public void interfaceAvailable(String interfaceName, Object interfaceImpl)
    {
        if (interfaceName.equals("container"))
        {
            deployService =
                (ContainerManagement) (appServiceContext.getContainerContext()
                .getObjectRegistry().getProvidedInterface("container"));

            communicator =
                deployService.registerContainer(migrationContainer.getContainerInfo()
                    .getName(), migrationContainer);

            try
            {
                communicator.setMigrator(migrationContainer);
            }
            catch (CMigrationException exc)
            {
                SimpleLogger.traceThrowable(Severity.ERROR, location, exc, "SDT.migration.000064", 
                		"Cannot set migrator for deploy communicator, the migration container is not registered for migration");	
                //logging.logThrowable("Cannot set migrator for deploy communicator. The container is not registered for migration.",
                //    exc);
            }

            migrationContainer.setDeployCommunicator(communicator);
        }
    }

    public void interfaceNotAvailable(String arg0)
    {
    }

    public void markForShutdown(long arg0)
    {
    }

    public boolean setServiceProperty(String arg0, String arg1)
    {
        return false;
    }

    public boolean setServiceProperties(Properties arg0)
    {
        return false;
    }

    public void stop() throws ServiceRuntimeException
    {
        // unregister the containder event listener
        appServiceContext.getServiceState().unregisterContainerEventListener();

        // unregister the service
        appServiceContext.getContainerContext().getObjectRegistry()
        .unregisterInterface();

        if (upgrade != null)
        {
            try
            {
                upgrade.cleanUp();
                upgrade = null;
            }
            catch (RemoteException exc)
            {
                throw new ServiceRuntimeException(exc);
            }
        }

        // unregister the container
        deployService.unregisterContainer(migrationContainer.getContainerInfo()
            .getName());
        SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000065", 
        		"Migration Service stopped successfully"); 
       // logging.logDebug("MIGRATION service stopped successfully.");
    }

    private boolean isEngineInSafeMigrateMode() throws ServiceException
    {
        CoreMonitor cm = appServiceContext.getCoreContext().getCoreMonitor();

        if ((cm.getRuntimeMode() == CoreMonitor.RUNTIME_MODE_SAFE) &&
            (cm.getRuntimeAction() == CoreMonitor.RUNTIME_ACTION_MIGRATE))
        {
            return true;
        }

        return false;
    }

    private ConfigurationHandler getServiceConfigHandler(
        ConfigurationHandlerFactory factory) throws ConfigException
    {
        ConfigurationHandler handler = null;

        //ConfigurationHandlerFactory factory =
        //  appServiceContext.getCoreContext ().getConfigurationHandlerFactory ();
        if (factory != null)
        {
            try
            {
                handler = factory.getConfigurationHandler();
            }
            catch (ConfigurationException ce)
            {
                throw new ConfigException(ExceptionConstants.CANNOT_GET_HANDLER_ON_PRINCIPLE,
                    new Object[] {  }, ce, location,
                    MigrationResourceAccessor.getResourceAccessor());
            }
        }

        return handler;
    }

    private boolean needConfiguration(ConfigurationHandler handler)
        throws ConfigException
    {
        try
        {
            String[] rootNames = handler.getAllRootNames();
            if ((rootNames != null) && (rootNames.length > 0))
            {
                for (int i = 0; i < rootNames.length; i++)
                {
                    if (MigrationConstantsIF.MIGRATION_CONFIG_NAME.equals(
                            rootNames[i]))
                    {
                        return false;
                    }
                }
            }
        }
        catch (ConfigurationException exc)
        {
            // throw an exception
            throw new ConfigException(ExceptionConstants.CANNOT_GET_ALL_ROOT_NAMES,
                new Object[] {  }, exc, location,
                MigrationResourceAccessor.getResourceAccessor());
        }
        return true;
    }

    /**
     * Get the configuration for the migration service,
     * if it does not exist, create it
     *
     * @return the configuration for the migration service
     * @throws ConfigException
     */
    private synchronized void createConfig(ConfigurationHandler handler)
        throws ConfigException
    {
        if (handler == null)
        {
        	SimpleLogger.trace(Severity.ERROR, location, "SDT.migration.000066", 
        			"Configuration handler is null");
           // logging.logError(
           //     "getServiceConfigHandler the confighandler is null");
            throw new ConfigException(ExceptionConstants.CONFIGURATION_NOT_AVAILABLE,
                new Object[] {  }, location,
                MigrationResourceAccessor.getResourceAccessor());
        }

        // the configuration for the migration service does not exist
        // a new one will be created
        Configuration migrationServiceConfiguration = null;

        try
        {
            migrationServiceConfiguration =
                handler.openConfiguration(MigrationConstantsIF.MIGRATION_CONFIG_NAME,
                    ConfigurationHandler.WRITE_ACCESS);
        }
        catch (NameNotFoundException e)
        {
            // $JL-EXC$  Create new configuration
            try
            {
                migrationServiceConfiguration =
                    handler.createRootConfiguration(MigrationConstantsIF.MIGRATION_CONFIG_NAME);
            }
            catch (ConfigurationException ce)
            {
                // the configuration cannot be created, rollback
                rollback(handler);

                // close all opened configurations
                closeAllConfigurations(handler);

                // and throw an exception
                throw new ConfigException(ExceptionConstants.CANNOT_CREATE_SUBCONFIGURATION,
                    new Object[] { MigrationConstantsIF.MIGRATION_CONFIG_NAME },
                    ce, location,
                    MigrationResourceAccessor.getResourceAccessor());
            }
        }
        catch (ConfigurationException exc)
        {
            // there is nothing at this point to be rollbacked, 
            // only close the opened configurations
            closeAllConfigurations(handler);

            // and throw an exception
            throw new ConfigException(ExceptionConstants.CANNOT_OPEN_CONFIGURATION,
                new Object[] { MigrationConstantsIF.MIGRATION_CONFIG_NAME },
                exc, location, MigrationResourceAccessor.getResourceAccessor());
        }

        // now create the subconfigurations
        boolean subConfigurationExist = false;
        try
        {
            subConfigurationExist =
                migrationServiceConfiguration.existsSubConfiguration(MigrationConstantsIF.MIGRATION_BCCOMPDATA);
        }
        catch (ConfigurationException exc)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_CHECK_FOR_SUBCONFIGURATION,
                new Object[] { MigrationConstantsIF.MIGRATION_BCCOMPDATA },
                exc, location, MigrationResourceAccessor.getResourceAccessor());
        }
        if (!subConfigurationExist)
        {
            try
            {
                migrationServiceConfiguration.createSubConfiguration(MigrationConstantsIF.MIGRATION_BCCOMPDATA);
            }
            catch (ConfigurationException exc)
            {
                // the configuration cannot be created, rollback
                rollback(handler);

                // close all opened configurations
                closeAllConfigurations(handler);

                throw new ConfigException(ExceptionConstants.CANNOT_CREATE_SUBCONFIGURATION,
                    new Object[]
                    {
                        MigrationConstantsIF.MIGRATION_CONFIG_NAME + "/" +
                        MigrationConstantsIF.MIGRATION_BCCOMPDATA
                    }, exc, location,
                    MigrationResourceAccessor.getResourceAccessor());
            }
        }

        try
        {
            subConfigurationExist =
                migrationServiceConfiguration.existsSubConfiguration(MigrationConstantsIF.MIGRATION_MODULES);
        }
        catch (ConfigurationException exc)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_CHECK_FOR_SUBCONFIGURATION,
                new Object[] { MigrationConstantsIF.MIGRATION_MODULES }, exc,
                location, MigrationResourceAccessor.getResourceAccessor());
        }
        if (!subConfigurationExist)
        {
            try
            {
                migrationServiceConfiguration.createSubConfiguration(MigrationConstantsIF.MIGRATION_MODULES);
            }
            catch (ConfigurationException exc)
            {
                // the configuration cannot be created, rollback
                rollback(handler);

                // close all opened configurations
                closeAllConfigurations(handler);

                throw new ConfigException(ExceptionConstants.CANNOT_CREATE_SUBCONFIGURATION,
                    new Object[]
                    {
                        MigrationConstantsIF.MIGRATION_CONFIG_NAME + "/" +
                        MigrationConstantsIF.MIGRATION_BCCOMPDATA
                    }, exc, location,
                    MigrationResourceAccessor.getResourceAccessor());
            }
        }

        commit(handler);
        closeAllConfigurations(handler);

        //	return handler;
    }

    private void closeAllConfigurations(ConfigurationHandler handler)
        throws ConfigException
    {
        try
        {
            handler.closeAllConfigurations();
        }
        catch (ConfigurationException ce)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_CLOSE_CONFIGURATION,
                new Object[] {  }, ce, location,
                MigrationResourceAccessor.getResourceAccessor());
        }
    }

    private void rollback(ConfigurationHandler handler)
        throws ConfigException
    {
        try
        {
            handler.rollback();
        }
        catch (ConfigurationException ce)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_ROLLBACK,
                new Object[] {  }, ce, location,
                MigrationResourceAccessor.getResourceAccessor());
        }
    }

    private void commit(ConfigurationHandler handler) throws ConfigException
    {
        try
        {
            handler.commit();
        }
        catch (ConfigurationException ce)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_COMMIT_CONFIGURATION,
                new Object[] {  }, ce, location,
                MigrationResourceAccessor.getResourceAccessor());
        }
    }
}
