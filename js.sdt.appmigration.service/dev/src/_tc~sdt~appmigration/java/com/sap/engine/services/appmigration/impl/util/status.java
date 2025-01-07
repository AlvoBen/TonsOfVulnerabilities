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

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.appmigration.ExceptionConstants;
import com.sap.engine.services.appmigration.MigrationLogging;
import com.sap.engine.services.appmigration.MigrationResourceAccessor;
import com.sap.engine.services.appmigration.api.exception.ConfigException;
import com.sap.engine.services.appmigration.api.util.StatusIF;

/**
 * The implementation of StatusIF
 * For more information 
 * @see com.sap.engine.services.appmigration.api.util.StatusIF
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public class Status implements StatusIF
{
 //   private String configPath;
    private MigrationLogging logging;
    private ConfigurationHandler configHandler;
    private Configuration migrationConfig;
    private String appName;
    
    public Status(ConfigurationHandler configHandler, String appName, MigrationLogging logging)
    {
       // this.configPath = configPath;
        this.appName = appName;
        this.configHandler = configHandler;
        this.logging = logging;
    }


    public void setTransactionStatus(String name, byte status)
        throws ConfigException
    {
        Configuration appConfig = null;
        try
        {
            openCfg ();
            appConfig = migrationConfig.getSubConfiguration (appName);
        }
        catch (NameNotFoundException exc)
        {
            //$JL-EXC$
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
            logging.logDebug("migration status for transaction " + name + " is " + status);

            // Check if the status param has correct value
            if (status != StatusIF.STATUS_OK &&
                status != StatusIF.STATUS_ERROR)
            {
                ConfigException migrationExc =
                    new ConfigException(
                        ExceptionConstants.INCORRECT_CONFIG_ENTRY_VALUE,
                        new Object[] {name}, logging.getLocation(), 
                        MigrationResourceAccessor.getResourceAccessor());
                    
                logging.logThrowable (
                    "Cannot set config entry " + name + " from config ",
                    migrationExc);     
                throw migrationExc;                          
            }
            // The value of status param seems to be ok, 
            // now try to set it in the config too
            appConfig.modifyConfigEntry(name, new Byte(status), true);
        } 
        catch (Exception exc)
        {
            ConfigException migrationExc =
                new ConfigException(
                    ExceptionConstants.CANNOT_SET_CONFIG_ENTRY,
                    new Object[] {name}, exc, logging.getLocation(), 
                    MigrationResourceAccessor.getResourceAccessor());
                    
            logging.logThrowable (
                "Cannot get config entry " + name + " from config ",
                migrationExc);     
            throw migrationExc;
        } finally
        {
            try
            {
                cleanUp();    
            } catch (Exception exc)
            {
                new ConfigException(
                    ExceptionConstants.CANNOT_CLOSE_CONFIGURATION,
                    new Object[] { migrationConfig.getPath() }, exc, logging.getLocation (),
                    MigrationResourceAccessor.getResourceAccessor ());                
            }
        }
    }


    public byte getTransactionStatus(String name) 
        throws ConfigException
        
    {
        Configuration appConfig = null;
        try
        {
            openCfg ();
            appConfig = migrationConfig.getSubConfiguration (appName);
        }
        catch (NameNotFoundException exc)
        {

               throw new ConfigException(
                     ExceptionConstants.CANNOT_OPEN_CONFIGURATION, 
                     new Object[] {appConfig.getPath()}, exc, 
                     logging.getLocation(), 
                     MigrationResourceAccessor.getResourceAccessor());

        }
        catch (Exception exc)
        {
            throw new ConfigException(
                 ExceptionConstants.CANNOT_GET_SUBCONFIGURATION, 
                 new Object[] {appConfig.getPath()}, exc, 
                 logging.getLocation(), 
                 MigrationResourceAccessor.getResourceAccessor());        
        }
        
        Byte transactionStatus = null;
        try 
        {
            transactionStatus = (Byte)appConfig.getConfigEntry(name);
            logging.logDebug("The config entry " + name + " has value " + transactionStatus); 
        } 
        catch (NameNotFoundException exc)
        {
            // $JL-EXC$
            // not exist in config manager
            return StatusIF.STATUS_NOT_DEFINED;
        }
        catch (Exception exc)
        {
            ConfigException migrationExc =
                new ConfigException(ExceptionConstants.CANNOT_GET_CONFIG_ENTRY,
                   new Object[] {name}, exc, logging.getLocation(), 
                   MigrationResourceAccessor.getResourceAccessor());
                    
            logging.logThrowable (
                "Cannot get config entry " + name + " from config ",
                migrationExc);     
            throw migrationExc;           
        } 
        finally
        {
            try
            {
                cleanUp();    
            } catch (Exception exc)
            {
                new ConfigException(
                    ExceptionConstants.CANNOT_CLOSE_CONFIGURATION,
                    new Object[] { migrationConfig.getPath() }, exc, logging.getLocation (),
                    MigrationResourceAccessor.getResourceAccessor ());                
            }            
        }
        
        if (transactionStatus == null)
        {
            return StatusIF.STATUS_NOT_DEFINED;
        } 
        else if (transactionStatus.byteValue() == StatusIF.STATUS_OK)
        {
            return StatusIF.STATUS_OK;
        } 
        else if (transactionStatus.byteValue() == StatusIF.STATUS_ERROR)
        {
            return StatusIF.STATUS_ERROR;
        }         
        return StatusIF.STATUS_NOT_DEFINED;
        
    }
    
    private void openCfg ()
        throws ConfigurationException
    {
        // open the migration configuration for writing
        migrationConfig =
            configHandler.openConfiguration (
                MigrationConstantsIF.MIGRATION_MODULES_CONFIG,
                ConfigurationHandler.WRITE_ACCESS);

    }
    
    
    /**
     * Use this method to submit any pending transactions and
     * to close the open configurations
     */
    private void cleanUp () 
        throws ConfigurationException
    {
        // commit any uncommitted transactions 
        // and close configuration 
        if (configHandler != null)
        {
            configHandler.commit ();
            configHandler.closeAllConfigurations ();
        }
    }
}
