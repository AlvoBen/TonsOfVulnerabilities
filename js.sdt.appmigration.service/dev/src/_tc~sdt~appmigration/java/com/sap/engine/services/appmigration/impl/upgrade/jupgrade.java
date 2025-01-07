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
package com.sap.engine.services.appmigration.impl.upgrade;

import java.io.File;
import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.rmi.PortableRemoteObject;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.appmigration.api.upgrade.JUpgradeIF;
import com.sap.engine.services.appmigration.api.upgrade.RemoteVersionFactoryIF;
import com.sap.engine.services.appmigration.api.util.VersionIF;
import com.sap.engine.services.appmigration.impl.util.CompVersion;
import com.sap.engine.services.appmigration.impl.util.MigrationConstantsIF;
import com.sap.sl.util.components.api.ComponentElementIF;
import com.sap.sl.util.cvers.api.CVersFactoryIF;
import com.sap.sl.util.cvers.api.CVersManagerIF;

/**
 * The implementation of the interface that is provided to the
 * upgrade procedure
 *
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public class JUpgrade extends PortableRemoteObject implements JUpgradeIF
{
    private ConfigurationHandler configHandler;
    private Configuration migrationConfig;
    private RemoteVersionFactory versionFactory;

    public JUpgrade(ConfigurationHandler configHandler)
        throws RemoteException
    {
        this.configHandler = configHandler;
        versionFactory = new RemoteVersionFactory();
    }

    public void openCfg(String configName) throws RemoteException
    {
        openCfg(configName, false, ConfigurationHandler.WRITE_ACCESS);
    }

    private void openCfg(String configName, boolean createIfNotExist,
        int accessMode) throws RemoteException
    {
        try
        {
            // open the migration configuration for writing
            migrationConfig =
                configHandler.openConfiguration(configName, accessMode);
          
        }
        catch (NameNotFoundException exc)
        {
            if (createIfNotExist)
            {
                try
                {
                    migrationConfig =
                        configHandler.createSubConfiguration(configName);
                }
                catch (Exception cex)
                {
                    throw new RemoteException("Cannot create subconfiguration " +
                        migrationConfig.getPath(), exc);
                }
            }
            else
            {
                throw new RemoteException("Cannot open subconfiguration " +
                    migrationConfig.getPath(), exc);
            }
        }
        catch (ConfigurationLockedException cle)
        {
            throw new RemoteException("Configuration locked ", cle);
        }
        catch (ConfigurationException ce)
        {
            throw new RemoteException("Configuration exception ", ce);
        }
    }

    
    private void openCfgHierarchy(
    		Configuration config, 
    		String configName, 
    		boolean createIfNotExist,
            int accessMode)
        throws RemoteException
        
	{
	    try
	    {
	        // open the migration configuration for writing
	        migrationConfig =
	            configHandler.openConfiguration(configName, accessMode);
	      
	    }
	    catch (NameNotFoundException exc)
	    {
	        if (createIfNotExist)
	        {
	            try
	            {
	                migrationConfig =
	                    configHandler.createSubConfiguration(configName);
	            }
	            catch (Exception cex)
	            {
	                throw new RemoteException("Cannot create subconfiguration " +
	                    migrationConfig.getPath(), exc);
	            }
	        }
	        else
	        {
	            throw new RemoteException("Cannot open subconfiguration " +
	                migrationConfig.getPath(), exc);
	        }
	    }
	    catch (ConfigurationLockedException cle)
	    {
	        throw new RemoteException("Configuration locked ", cle);
	    }
	    catch (ConfigurationException ce)
	    {
	        throw new RemoteException("Configuration exception ", ce);
	    }
	}
    
    public void setSourceComponentVersions(String name, String vendor,
        VersionIF version) throws RemoteException
    {
        if (vendor == null)
        {
            throw new RemoteException("The vendor of the component " + name +
                " is not specified");
        }
        openCfg(MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME);

        try
        {
            modifyCompVersion(name, vendor, version, true);
            commit();
        }
        catch (RemoteException exc)
        {
            rollback();
            throw new RemoteException("Error setting source component version",
                exc);
        }
        finally
        {
            closeAllConfigurations();
        }
    }

    private void setProductVersion(VersionIF version, boolean isSourceVersion)
        throws RemoteException
    {
        String versionConfig = null;
        if (isSourceVersion)
        {
            versionConfig = MigrationConstantsIF.ENGINE_VERSION_SOURCE;
        }
        else
        {
            versionConfig = MigrationConstantsIF.ENGINE_VERSION_TARGET;
        }

        openCfg(MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME);

        try
        {
            modifyCompVersion(versionConfig, "sap.com", version, true);

            commit();
        }
        catch (Exception exc)
        {
            rollback();
            throw new RemoteException("Cannot set product version ", exc);
        }
        finally
        {
            closeAllConfigurations();
        }
    }

    public void setSourceProductVersion(VersionIF version)
        throws RemoteException
    {
        setProductVersion(version, true);
    }

    public void setTargetProductVersion(VersionIF version)
        throws RemoteException
    {
        setProductVersion(version, false);
    }

    private void setApplicationParam(String appName, String vendor,
    String paramName, Object param, boolean isFileParam) throws RemoteException
    {
        if ((vendor == null) || (vendor.length() == 0))
        {
            throw new RemoteException("Cannot set parameter " + paramName +
                " for application " + appName +
                ", because the vendor is not specified");
        }

        Configuration appConfig = null;
        String applicationName = vendor + "/" + replaceAppNameSlashes(appName);

        try
        {
            openCfg(MigrationConstantsIF.MIGRATION_MODULES_CONFIG);

            appConfig = migrationConfig.getSubConfiguration(applicationName);
        }
        catch (NameNotFoundException exc)
        {
            //$JL-EXC$
            // if such entry does not exist then creat it
            // this is normal case because such application may not has been
            // deployed at this time
            try
            {
                appConfig =
                    migrationConfig.createSubConfigurationHierachy(applicationName);
            }
            catch (Exception cex)
            {
                rollback();
                closeAllConfigurations();

                throw new RemoteException("Cannot create subconfiguration " +
                    appConfig.getPath(), exc);
            }
        }
        catch (Exception exc)
        {
            rollback();
            closeAllConfigurations();

            throw new RemoteException("Cannot get subconfiguration " +
                appConfig.getPath(), exc);
        }

        try
        {  
            if (isFileParam)
            {
                if (param instanceof File)
                {
                    appConfig.updateFileByKey(paramName, (File)param, true);
                } else
                {
                    throw new RemoteException("Cannot add/update file " 
                        + paramName + ", because the parameter given is not a File object" + param);
                }
                
            } else
            {
                appConfig.modifyConfigEntry(paramName, param, true);
            }
            
            commit();
        }
        catch (Exception e)
        {
            rollback();
            throw new RemoteException(
                "Configuration exception while modifying parameters for application " +
                appConfig.getPath(), e);
        }
        finally
        {
            closeAllConfigurations();
        }
    }

    public void setApplicationParam(String appName, String vendor,
        String paramName, Object param) throws RemoteException
    {
        setApplicationParam(appName, vendor, paramName, param, true);
    }

    public void setApplicationParamEntry(String appName, String vendor,
        String paramName, Object param) throws RemoteException
    {
        setApplicationParam(appName, vendor, paramName, param, false);
    }
    
    public void deleteAMCEntries() throws RemoteException
    {
        // delete everything from migration service configuration 
        openCfg(MigrationConstantsIF.MIGRATION_MODULES_CONFIG);

        if (migrationConfig != null)
        {
            try
            {
                migrationConfig.deleteAllSubConfigurations();
                migrationConfig.deleteAllConfigEntries();
                commit();
            }
            catch (Exception exc)
            {
                rollback();
                throw new RemoteException("The entries cannot be deleted", exc);
            }
            finally
            {
                closeAllConfigurations();
            }
        }
        else
        {
            throw new RemoteException(
                "The migration service configuration is null");
        }

        // delete everything from migration service configuration 
        openCfg(MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME);

        if (migrationConfig != null)
        {
            try
            {
                migrationConfig.deleteAllSubConfigurations();
                migrationConfig.deleteAllConfigEntries();
                commit();
            }
            catch (Exception exc)
            {
                rollback();
                throw new RemoteException("The entries cannot be deleted", exc);
            }
            finally
            {
                closeAllConfigurations();
            }
        }
        else
        {
            throw new RemoteException(
                "The migration service configuration is null");
        }
    }

    public void readComponentVersions() throws RemoteException
    {
        CompVersion version = null;

        try
        {
            CVersFactoryIF factory = CVersFactoryIF.getInstance();
            CVersManagerIF manager = factory.createCVersManager();

            ComponentElementIF[] components = manager.readCVers();
            ComponentElementIF component = null;

            if (components != null)
            {
                openCfg(MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME,
                    true, ConfigurationHandler.WRITE_ACCESS);

                String[] subconfigurations =
                    migrationConfig.getAllSubConfigurationNames();
                List subconfigVector = null;

                if (subconfigurations != null)
                {
                    subconfigVector = new ArrayList();

                    for (int i = 0; i < subconfigurations.length; i++)
                    {
                        subconfigVector.add(subconfigurations[i]);
                    }

                    // subconfigVector = Arrays.asList (subconfigurations);
                }
                else
                {
                    subconfigVector = new ArrayList();
                }

                for (int i = 0; i < components.length; i++)
                {
                    component = components[i];

                    /*
                    System.out.println (
                        "component " + component.getName () + " counter = " +
                        component.getCounter () + " release = " +
                        component.getRelease () + " SP = " +
                        component.getSPVersion () + " vendor = " +
                        component.getVendor () + " location = " +
                        component.getLocation ());
                    */
                    version = new CompVersion();
                    version.setName(component.getName());
                    version.setPatchNumber(component.getPatchLevel());
                    version.setSpLevel(component.getSPVersion());
                    version.setVendor(component.getVendor());

                    // version.setCompLocation (component.getLocation ());
                    version.setCounter(component.getCounter());
                    version.setRelease(component.getRelease());

                    modifyCompVersion(component.getName(),
                        component.getVendor(), version, true);

                    subconfigVector.remove(component.getName());

                    String[] namesToBeRemoved =
                        new String[subconfigVector.size()];

                    for (int j = 0; j < subconfigVector.size(); j++)
                    {
                        namesToBeRemoved[j] = (String) subconfigVector.get(j);
                    }

                    migrationConfig.deleteSubConfigurations(namesToBeRemoved);
                }

                commit();
            }
            else
            {
                throw new RemoteException("No components in BC_COMPVERS");
            }
        }
        catch (Exception exc)
        {
            rollback();
            throw new RemoteException("Exception while reading components from BC_COMPVERS",
                exc);
        }
        finally
        {
            closeAllConfigurations();
        }
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

    private void modifyCompVersion(String appName, String vendor,
        VersionIF version, boolean addIfNotExist) throws RemoteException
    {
        if (vendor != null)
        {
            String subConfigName =
                vendor + "/" + replaceAppNameSlashes(appName);

            if (migrationConfig != null)
            {
                Configuration appConfig = null;

                try
                {
                    appConfig =
                        migrationConfig.getSubConfiguration(subConfigName);
                }
                catch (NameNotFoundException exc)
                {
                    //$JL-EXC$
                    try
                    {
                        appConfig =
                            migrationConfig.createSubConfigurationHierachy(subConfigName);
                    }
                    catch (Exception cex)
                    {
                        rollback();
                        throw new RemoteException(
                            "Error while creating subconfiguration " +
                            subConfigName, cex);
                    }
                }
                catch (Exception exc)
                {
                    rollback();
                    throw new RemoteException(
                        "Error while getting subconfiguration " +
                        subConfigName, exc);
                }

                try
                {
                    if (version.getRelease() != null)
                    {
                        appConfig.modifyConfigEntry(MigrationConstantsIF.RELEASE,
                            version.getRelease(), addIfNotExist);
                    }

                    if (version.getSPLevel() != null)
                    {
                        appConfig.modifyConfigEntry(MigrationConstantsIF.SP_LEVEL,
                            version.getSPLevel(), addIfNotExist);
                    }

                    if (version.getPatchNumber() != null)
                    {
                        appConfig.modifyConfigEntry(MigrationConstantsIF.PATCH,
                            version.getPatchNumber(), addIfNotExist);
                    }

                    if (version.getVendor() != null)
                    {
                        appConfig.modifyConfigEntry(MigrationConstantsIF.VENDOR_ENTRY,
                            version.getVendor(), addIfNotExist);
                    }

                    /*
                    if (version.getCompLocation () != null)
                    {
                        appConfig.modifyConfigEntry (
                            MigrationConstantsIF.LOCATION,
                            version.getCompLocation (), addIfNotExist);
                    }
                    */
                    if (version.getCounter() != null)
                    {
                        appConfig.modifyConfigEntry(MigrationConstantsIF.COUNTER,
                            version.getCounter(), addIfNotExist);
                    }
                }
                catch (Exception exc)
                {
                    rollback();
                    throw new RemoteException("Error while modifying config entry",
                        exc);
                }
            }
            else
            {
                throw new RemoteException(
                    "Migration configuration not initialized. It is null");
            }
        }
    }

    public RemoteVersionFactoryIF getVersionFactory() throws RemoteException
    {
        return versionFactory;
    }

    public List getFailedModulesInfo() throws RemoteException
    {
        List resultList = new ArrayList();

        // open the migration_service configuration for reading the
        // status of the migration modules  
        try
        {
            openCfg(MigrationConstantsIF.MIGRATION_MODULES_CONFIG, false,
                ConfigurationHandler.READ_ACCESS);
        }
        catch (Exception exc)
        {
            // the configuration does not exist, or cannot be open
            closeAllConfigurations();

            throw new RemoteException("Cannot open configuration " + 
                MigrationConstantsIF.MIGRATION_MODULES_CONFIG, exc);
        }
        try
        {
            // get the vendors configurations
            // like sap.com
            Map vendorConfigurations =
                migrationConfig.getAllSubConfigurations();

            if ((vendorConfigurations != null) &&
                (vendorConfigurations.keySet() != null))
            {
                Iterator vendorConfigIt =
                    vendorConfigurations.keySet().iterator();

                while (vendorConfigIt.hasNext())
                {
                    String vendorName = (String) vendorConfigIt.next();

                    // get the applications for the vendor
                    Configuration vendorConfig =
                        (Configuration) vendorConfigurations.get(vendorName);
                    Map appConfigurations =
                        vendorConfig.getAllSubConfigurations();

                    if (appConfigurations != null)
                    {
                        if (appConfigurations.keySet() != null)
                        {
                            Iterator keysIterator =
                                appConfigurations.keySet().iterator();

                            if (keysIterator != null)
                            {
                                while (keysIterator.hasNext())
                                {
                                    String key = (String) keysIterator.next();
                                    Configuration config =
                                        (Configuration) appConfigurations.get(key);

                                    try
                                    {
                                        String status =
                                            (String) config.getConfigEntry(MigrationConstantsIF.STATUS_ENTRY);
                                        

                                        // check the UNDEPLOYED flag
                                        boolean isDeployed = false;
                                        if (config.existsConfigEntry(
                                                MigrationConstantsIF.UNDEPLOYED_FLAG))
                                        {
                                            //there is UNDEPLOYED flag, check it
                                            isDeployed =
                                                MigrationConstantsIF.NO_OPTION.equals(config.getConfigEntry(
                                                        MigrationConstantsIF.UNDEPLOYED_FLAG));
                                        }
                                        

                                        String csnComponent = null;
                                        String exceptionTxt = null;
                                        if (config.existsConfigEntry(MigrationConstantsIF.STATUS_EXC_TEXT))
                                        {
                                        	exceptionTxt = 
                                        		(String)config.getConfigEntry(MigrationConstantsIF.STATUS_EXC_TEXT);
                                        }
                                        
                                        if (config.existsConfigEntry(MigrationConstantsIF.MM_CSN_COMPONENT))
                                        {
                                        	csnComponent = 
                                        		(String)config.getConfigEntry(MigrationConstantsIF.MM_CSN_COMPONENT);
                                        }
                                        
                                        //check now the status
                                        if (!status.equals(
                                                MigrationConstantsIF.STATUS_ENTRY_NOT_PASSED) &&
                                            !status.equals(
                                                MigrationConstantsIF.STATUS_ENTRY_OK) &&
                                            isDeployed)
                                        {
                                            MigrationModuleInfo mmInfo =
                                                new MigrationModuleInfo(vendorName,
                                                    key, csnComponent);
                                  
                                            mmInfo.setExceptionText(exceptionTxt);
                                            mmInfo.setStatus(status);
                                            resultList.add(mmInfo);
                                        }
                                    }
                                    catch (NameNotFoundException exc)
                                    {
                                        // $JL-EXC$
                                        // status not found, nothing to do
                                        //MigrationModuleInfo mmInfo =
                                        //    new MigrationModuleInfo(vendorName, key);
                                        // resultList.add (mmInfo);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            commit();
        }
        catch (Exception exc)
        {
            rollback();
            throw new RemoteException("The list with failed migration modules cannot be get from DB",
                exc);
        }
        finally
        {
            closeAllConfigurations();
        }

        return resultList;
    }

    public List getSuccessfulModulesInfo() throws RemoteException
    {
        List resultList = new ArrayList();

        // open the migration_service configuration for reading the
        // status of the migration modules  
        try
        {
            openCfg(MigrationConstantsIF.MIGRATION_MODULES_CONFIG, false,
                ConfigurationHandler.READ_ACCESS);
        }
        catch (Exception exc)
        {
            // the configuration does not exist, or cannot be open
            closeAllConfigurations();
            throw new RemoteException("Cannot open configuration " + 
                MigrationConstantsIF.MIGRATION_MODULES_CONFIG, exc);
        }
        try
        {
            // get the vendors configurations
            // like sap.com
            Map vendorConfigurations =
                migrationConfig.getAllSubConfigurations();

            if ((vendorConfigurations != null) &&
                (vendorConfigurations.keySet() != null))
            {
                Iterator vendorConfigIt =
                    vendorConfigurations.keySet().iterator();

                while (vendorConfigIt.hasNext())
                {
                    String vendorName = (String) vendorConfigIt.next();

                    // get the applications for the vendor
                    Configuration vendorConfig =
                        (Configuration) vendorConfigurations.get(vendorName);
                    Map appConfigurations =
                        vendorConfig.getAllSubConfigurations();

                    if (appConfigurations != null)
                    {
                        if (appConfigurations.keySet() != null)
                        {
                            Iterator keysIterator =
                                appConfigurations.keySet().iterator();

                            if (keysIterator != null)
                            {
                                while (keysIterator.hasNext())
                                {
                                    String key = (String) keysIterator.next();
                                    Configuration config =
                                        (Configuration) appConfigurations.get(key);

                                    try
                                    {
                                        String status =
                                            (String) config.getConfigEntry(MigrationConstantsIF.STATUS_ENTRY);

                                        // check the UNDEPLOYED flag
                                        boolean isDeployed = false;
                                        if (config.existsConfigEntry(
                                                MigrationConstantsIF.UNDEPLOYED_FLAG))
                                        {
                                            //there is UNDEPLOYED flag, check it
                                            isDeployed =
                                                MigrationConstantsIF.NO_OPTION.equals(config.getConfigEntry(
                                                        MigrationConstantsIF.UNDEPLOYED_FLAG));
                                        }

                                        String csnComponent = null;
                                        
                                        if (config.existsConfigEntry(MigrationConstantsIF.MM_CSN_COMPONENT))
                                        {
                                        	csnComponent = 
                                        		(String)config.getConfigEntry(MigrationConstantsIF.MM_CSN_COMPONENT);
                                        }
                                        
                                        //check now the status
                                        if (status.equals(
                                                MigrationConstantsIF.STATUS_ENTRY_OK) &&
                                            isDeployed)
                                        {
                                            MigrationModuleInfo mmInfo =
                                                new MigrationModuleInfo(vendorName,
                                                    key, csnComponent);
                                            resultList.add(mmInfo);
                                        }
                                    }
                                    catch (NameNotFoundException exc)
                                    {
                                        // $JL-EXC$
                                        // status not found,nothing to do
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception exc)
        {
            throw new RemoteException("The list with successful migration modules cannot be get from DB",
                exc);
        }
        finally
        {
            closeAllConfigurations();
        }

        return resultList;
    }

    public List getNotPassedModulesInfo() throws RemoteException
    {
        List resultList = new ArrayList();

        // open the migration_service configuration for reading the
        // status of the migration modules  
        try
        {
            openCfg(MigrationConstantsIF.MIGRATION_MODULES_CONFIG, false,
                ConfigurationHandler.READ_ACCESS);
        }
        catch (Exception exc)
        {
            // the configuration does not exist, or cannot be open
            closeAllConfigurations();
            throw new RemoteException("Cannot open configuration " + 
                MigrationConstantsIF.MIGRATION_MODULES_CONFIG, exc);
        }
        try
        {
            // get the vendors configurations
            // like sap.com
            Map vendorConfigurations =
                migrationConfig.getAllSubConfigurations();

            if ((vendorConfigurations != null) &&
                (vendorConfigurations.keySet() != null))
            {
                Iterator vendorConfigIt =
                    vendorConfigurations.keySet().iterator();

                while (vendorConfigIt.hasNext())
                {
                    String vendorName = (String) vendorConfigIt.next();

                    // get the applications for the vendor
                    Configuration vendorConfig =
                        (Configuration) vendorConfigurations.get(vendorName);
                    Map appConfigurations =
                        vendorConfig.getAllSubConfigurations();

                    if (appConfigurations != null)
                    {
                        if (appConfigurations.keySet() != null)
                        {
                            Iterator keysIterator =
                                appConfigurations.keySet().iterator();

                            if (keysIterator != null)
                            {
                                while (keysIterator.hasNext())
                                {
                                    String key = (String) keysIterator.next();
                                    Configuration config =
                                        (Configuration) appConfigurations.get(key);

                                    try
                                    {
                                        String status =
                                            (String) config.getConfigEntry(MigrationConstantsIF.STATUS_ENTRY);

                                        // check the UNDEPLOYED flag
                                        boolean isDeployed = false;
                                        if (config.existsConfigEntry(
                                                MigrationConstantsIF.UNDEPLOYED_FLAG))
                                        {
                                            //there is UNDEPLOYED flag, check it
                                            isDeployed =
                                                MigrationConstantsIF.NO_OPTION.equals(config.getConfigEntry(
                                                        MigrationConstantsIF.UNDEPLOYED_FLAG));
                                        }

                                        String csnComponent = null;
                                        
                                        if (config.existsConfigEntry(MigrationConstantsIF.MM_CSN_COMPONENT))
                                        {
                                        	csnComponent = 
                                        		(String)config.getConfigEntry(MigrationConstantsIF.MM_CSN_COMPONENT);
                                        }
                                        
                                        //check now the status
                                        if (status.equals(
                                                MigrationConstantsIF.STATUS_ENTRY_NOT_PASSED) &&
                                            isDeployed)
                                        {
                                            MigrationModuleInfo mmInfo =
                                                new MigrationModuleInfo(vendorName,
                                                    key, csnComponent);
                                            resultList.add(mmInfo);
                                        }
                                    }
                                    catch (NameNotFoundException exc)
                                    {
                                        // $JL-EXC$
                                        // status not found, nothing to do
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception exc)
        {
            throw new RemoteException("The list with not passed migration modules cannot be get from DB",
                exc);
        }
        finally
        {
            closeAllConfigurations();
        }
        return resultList;
    }

    public List getAllModulesInfo() throws RemoteException
    {
        List resultList = new ArrayList();

        // open the migration_service configuration for reading the
        // status of the migration modules  
        try
        {
            openCfg(MigrationConstantsIF.MIGRATION_MODULES_CONFIG, false,
                ConfigurationHandler.READ_ACCESS);
        }
        catch (Exception exc)
        {
            // the configuration does not exist, or cannot be open
            closeAllConfigurations();
            throw new RemoteException("Cannot open configuration " + 
                MigrationConstantsIF.MIGRATION_MODULES_CONFIG, exc);
        }
        try
        {
            // get the vendors configurations
            // like sap.com
            Map vendorConfigurations =
                migrationConfig.getAllSubConfigurations();

            if ((vendorConfigurations != null) &&
                (vendorConfigurations.keySet() != null))
            {
                Iterator vendorConfigIt =
                    vendorConfigurations.keySet().iterator();

                while (vendorConfigIt.hasNext())
                {
                    String vendorName = (String) vendorConfigIt.next();

                    // get the applications for the vendor
                    Configuration vendorConfig =
                        (Configuration) vendorConfigurations.get(vendorName);
                    Map appConfigurations =
                        vendorConfig.getAllSubConfigurations();

                    if (appConfigurations != null)
                    {
                        if (appConfigurations.keySet() != null)
                        {
                            Iterator keysIterator =
                                appConfigurations.keySet().iterator();

                            if (keysIterator != null)
                            {
                                while (keysIterator.hasNext())
                                {
                                    String key = (String) keysIterator.next();

                                    Configuration appConfig =
                                        (Configuration) appConfigurations.get(key);
                                    String csnComponent = null;
                                    String exceptionTxt = null;
                                    String status =
                                        (String) appConfig.getConfigEntry(MigrationConstantsIF.STATUS_ENTRY);
                                    
                                    if (appConfig.existsConfigEntry(MigrationConstantsIF.MM_CSN_COMPONENT))
                                    {
                                    	csnComponent = 
                                    		(String)appConfig.getConfigEntry(MigrationConstantsIF.MM_CSN_COMPONENT);
                                    }
                                    
                                    if (appConfig.existsConfigEntry(MigrationConstantsIF.STATUS_EXC_TEXT))
                                    {
                                    	exceptionTxt = 
                                    		(String)appConfig.getConfigEntry(MigrationConstantsIF.STATUS_EXC_TEXT);
                                    }
                                    
                                    if (appConfig.existsConfigEntry(
                                            MigrationConstantsIF.UNDEPLOYED_FLAG))
                                    {
                                        if (MigrationConstantsIF.NO_OPTION.equals(
                                                appConfig.getConfigEntry(
                                                    MigrationConstantsIF.UNDEPLOYED_FLAG)))
                                        {
                                            MigrationModuleInfo mmInfo =
                                                new MigrationModuleInfo(vendorName,
                                                    key, csnComponent);
                                            mmInfo.setStatus(status);
                                            mmInfo.setExceptionText(exceptionTxt);
                                            resultList.add(mmInfo);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception exc)
        {
            throw new RemoteException("The list with migration modules cannot be get from DB",
                exc);
        }
        finally
        {
            closeAllConfigurations();
        }

        return resultList;
    }

    public boolean hasMigrationModules() throws RemoteException
    {
        return (((getNotPassedModulesInfo() != null) &&
        (getNotPassedModulesInfo().size() > 0)) ||
        ((getFailedModulesInfo() != null) &&
        (getFailedModulesInfo().size() > 0)));
    }

    public int getMigrationModulesLenght() throws RemoteException
    {
        int length = 0;
        try
        {
            // open the migration configuration for reading
            migrationConfig =
                configHandler.openConfiguration(MigrationConstantsIF.MIGRATION_MODULES_CONFIG,
                    ConfigurationHandler.READ_ACCESS);

            Map vendorConfigurations =
                migrationConfig.getAllSubConfigurations();

            if ((vendorConfigurations != null) &&
                (vendorConfigurations.keySet() != null))
            {
                Iterator vendorConfigIt =
                    vendorConfigurations.keySet().iterator();

                while (vendorConfigIt.hasNext())
                {
                    String vendorName = (String) vendorConfigIt.next();

                    // get the applications for the vendor
                    Configuration vendorConfig =
                        (Configuration) vendorConfigurations.get(vendorName);
                    String[] appConfigurationsNames =
                        vendorConfig.getAllSubConfigurationNames();

                    int appsLength =
                        (appConfigurationsNames == null) ? 0
                        : appConfigurationsNames.length;

                    for (int i = 0; i < appsLength; i++)
                    {
                        Configuration appConfig =
                            vendorConfig.getSubConfiguration(appConfigurationsNames[i]);
                        if (appConfig.existsConfigEntry(
                                MigrationConstantsIF.UNDEPLOYED_FLAG))
                        {
                            if (MigrationConstantsIF.NO_OPTION.equals(
                                    appConfig.getConfigEntry(
                                        MigrationConstantsIF.UNDEPLOYED_FLAG)))
                            {
                                length++;
                            }
                        }
                    }
                }
            }
        }
        catch (NameNotFoundException exc)
        {
            //$JL-EXC$ should never occur
            return length;
        }
        catch (ConfigurationException exc)
        {
            throw new RemoteException("Cannot get the number of the migration modules",
                exc);
        }
        finally
        {
            closeAllConfigurations();
        }
        return length;
    }

    public void cleanUp() throws RemoteException
    {
        commit();
        closeAllConfigurations();
    }
    
	
	private void addEntries(String[] entries, String rootConfig, String configurationStr)
	    throws RemoteException
	{
        if (entries == null || entries.length == 0)
        {
            throw new RemoteException("The list to be set is empty");
        }
        
        openCfg(rootConfig, true, ConfigurationHandler.WRITE_ACCESS);
        
        
        try
        {
            if (migrationConfig != null)
            {
            	if (!migrationConfig.existsSubConfiguration(configurationStr))
            	{
             		migrationConfig = 
            			migrationConfig.createSubConfigurationHierachy(configurationStr);

            	} else
            	{
            		openCfg(rootConfig + "/" + configurationStr, false, 
            				ConfigurationHandler.WRITE_ACCESS);
            	
            	}
            	
            	if (!migrationConfig.getAllConfigEntries().isEmpty())
            	{
            		// delete the components, may be there 
            		// are left from previous upgrade
            		migrationConfig.deleteAllConfigEntries();
            	}
                
            	for (int i = 0; i < entries.length; i++)
            	{
            		//now adding new entries - products and usages 
            		migrationConfig.addConfigEntry(entries[i], entries[i]);
            	}
            			
            }
            commit();
        }
        catch (ConfigurationException exc)
		{
        	rollback();
        	throw new RemoteException(
        			"Configuration Exception while setting components in " +
        			configurationStr,
                    exc);
		}
        catch (RemoteException exc)
        {
            rollback();
            throw new RemoteException("Error setting components in "
            		+ configurationStr,
                exc);
        }
        finally
        {
            closeAllConfigurations();
        }		
		
	}

	public void setSourceProducts(String[] sourceProducts) throws RemoteException
	{
        addEntries(sourceProducts, 
        		MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME, 
        		MigrationConstantsIF.SOURCE_PRODUCTS);
	}
	
	public void setTargetProducts(String[] targetProducts) throws RemoteException
	{
		addEntries(targetProducts, 
        		MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME, 
				MigrationConstantsIF.TARGET_PRODUCTS);
	}
	

	public void setSourceUsages(String[] sourceUsages) throws RemoteException
	{
		addEntries(sourceUsages, 
        		MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME, 
				MigrationConstantsIF.SOURCE_USAGES);
	}
	

	public void setTargetUsages(String[] targetUsages) throws RemoteException
	{
		addEntries(targetUsages,
        		MigrationConstantsIF.MIGRATION_COMPVERS_COPY_NAME, 
				MigrationConstantsIF.TARGET_USAGES);		
	}
	
    private void closeAllConfigurations() throws RemoteException
    {
        try
        {
            migrationConfig = null;

            // commit any uncommitted transactions 
            // and close configuration 
            if (configHandler != null)
            {
                //		configHandler.commit ();
                configHandler.closeAllConfigurations();
            }
        }
        catch (Exception exc)
        {
            throw new RemoteException("Cannot clean up ", exc);
        }
    }

    private void rollback() throws RemoteException
    {
        try
        {
            if (configHandler != null)
            {
                configHandler.rollback();
            }
        }
        catch (Exception exc)
        {
            throw new RemoteException("Cannot rollback ", exc);
        }
    }

    private void commit() throws RemoteException
    {
        try
        {
            if (configHandler != null)
            {
                configHandler.commit();
            }
        }
        catch (Exception exc)
        {
            throw new RemoteException("Cannot commit ", exc);
        }
    }

    /*
    protected void finalize() throws Throwable
    {
        try
        {
            // commit any uncommitted transactions 
            // and close configuration 
            if (configHandler != null)
            {
                configHandler.commit();
                configHandler.closeAllConfigurations();
            }
        }
        finally
        {
            super.finalize();
        }
    } */


}
