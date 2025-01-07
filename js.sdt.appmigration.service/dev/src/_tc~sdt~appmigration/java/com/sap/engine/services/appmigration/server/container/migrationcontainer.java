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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.appmigration.ExceptionConstants;
import com.sap.engine.services.appmigration.MigrationLogging;
import com.sap.engine.services.appmigration.MigrationResourceAccessor;
import com.sap.engine.services.appmigration.api.applications.ApplicationMigrationIF;
import com.sap.engine.services.appmigration.api.exception.ConfigException;
import com.sap.engine.services.appmigration.api.exception.MigrationDeploymentException;
import com.sap.engine.services.appmigration.api.exception.MigrationException;
import com.sap.engine.services.appmigration.api.exception.MigrationModuleException;
import com.sap.engine.services.appmigration.impl.applications.MigrationContext;
import com.sap.engine.services.appmigration.impl.util.MigrationConstantsIF;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.container.ProgressListener;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.migration.CMigrationInterface;
import com.sap.engine.services.deploy.container.migration.exceptions.CMigrationException;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationInfo;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationResult;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationStatus;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This is the migration service container.
 * It receives migration archives (.mar files)
 * that are called to execute application migration
 *
 * @author Svetla Tsvetkova
 */
public class MigrationContainer implements ContainerInterface,
    CMigrationInterface
{
    public static final String CONTAINER_NAME = "MigrationContainer";
    public static final String MODULE_NAME = "MigrationModule";
    private ContainerInfo containerInfo = null;
    private DeployCommunicator communicator = null;
    private ConfigurationHandlerFactory factory = null;
    private ConfigurationHandler handler = null;
    private Location location = Location.getLocation(MigrationContainer.class);
    private Category category = Category.SYS_SERVER;
    
    private Location moduleLocation = null;
    private Category moduleCategory = null;
    
    private MigrationLogging logging =
        new MigrationLogging(Category.SYS_SERVER, location);
    
    private Configuration migrationConfig = null;
    
    private ServiceContext serviceContext;

    /**
     * Constructor of the migration container
     *
     */
    public MigrationContainer(ServiceContext serviceContext)
        throws ConfigException
    {
        this.serviceContext = serviceContext;
    	
    	// initsializes the container info 
        containerInfo = new ContainerInfo();

        SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000000", 
        		"Initializing container{0}", CONTAINER_NAME);
        
        //logging.logDebug("Initializing container " + CONTAINER_NAME);

        containerInfo.setName(CONTAINER_NAME);
        containerInfo.setModuleName(MODULE_NAME);

        //containerInfo.setJ2EEModuleName(J2EEModule.web);  // only for standard container
        // not a standard container          
        containerInfo.setJ2EEContainer(false);

        // the components to be deployed
        containerInfo.setFileExtensions(new String[] { ".mar" });

        // set of names of components to be deployed on this container
        containerInfo.setFileNames(null);

        // support for parallel deployment added 
        containerInfo.setSupportingParallelism(true);

        containerInfo.setServiceName(serviceContext.getServiceState()
            .getServiceName());

        SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000001", 
        		"Container information: {0}", containerInfo.toString());
               
        containerInfo.setSupportingSingleFileUpdate(false);
        containerInfo.setResourceTypes(null);
        containerInfo.setPriority(ContainerInfo.MIN_PRIORITY);
        
        containerInfo.setSupportingLazyStart(true);
        
        factory =
            serviceContext.getCoreContext().getConfigurationHandlerFactory();

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
        
        SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000002", 
        		"Container {0} successfully initialized", 
        		CONTAINER_NAME);
        
        
        //logging.logDebug(CONTAINER_NAME + " successfully initialized.");
    }

    public void setDeployCommunicator(DeployCommunicator deployCommunicator)
    {
        this.communicator = deployCommunicator;
    }

    public boolean acceptedAppInfoChange(String arg0, AdditionalAppInfo arg1)
        throws DeploymentException
    {
        return false;
    }

    public void addProgressListener(ProgressListener arg0)
    {
    }

    public void appInfoChangedCommit(String arg0) throws WarningException
    {
    }

    public void appInfoChangedRollback(String arg0) throws WarningException
    {
    }

    public void applicationStatusChanged(String arg0, byte arg1)
    {
    }

    public void commitDeploy(String arg0) throws WarningException
    {
    }

    public ApplicationDeployInfo commitRuntimeChanges(String arg0)
        throws WarningException
    {
        return null;
    }

    public ApplicationDeployInfo commitSingleFileUpdate(String arg0)
        throws WarningException
    {
        return null;
    }

    public void commitStart(String arg0) throws WarningException
    {
    }

    public void commitStop(String arg0) throws WarningException
    {
    }

    public ApplicationDeployInfo commitUpdate(String arg0)
        throws WarningException
    {
        return null;
    }

    public ApplicationDeployInfo deploy(File[] archiveFiles,
        ContainerDeploymentInfo deployInfo, Properties properties)
        throws DeploymentException
    {
        String applicationName = deployInfo.getApplicationName();

        SimpleLogger.log(Severity.INFO, category, location, "SDT.migration.000003",
        		"Deploying {0} to the migration container", applicationName);
       
        // logging.logInfo("Deploy to migration container " + applicationName);
      
        Hashtable fileMap = deployInfo.getFileMappings();

        Configuration configuration = null;
        boolean isUpdate = false;

        // Creating subConfiguration for the migration container
        try
        {
            configuration =
                deployInfo.getConfiguration().createSubConfiguration(CONTAINER_NAME);
        }
        catch (NameAlreadyExistsException exc)
        {
            // $JL-EXC$
            // this is an update
            try
            {
                configuration =
                    deployInfo.getConfiguration().getSubConfiguration(CONTAINER_NAME);
                isUpdate = true;
            }
            catch (Exception nexc)
            {
            	SimpleLogger.traceThrowable(Severity.ERROR,location, nexc, 
            			"SDT.migration.000005", "Cannot get container subconfiguration {0}", 
            			CONTAINER_NAME);
                
            	throw new MigrationDeploymentException(
                    "Cannot get container subconfiguration " + CONTAINER_NAME,
                    logging);
            }
        }
        catch (ConfigurationException configEx)
        {
            MigrationDeploymentException migrationExc =
                new MigrationDeploymentException(ExceptionConstants.CANNOT_CREATE_SUBCONFIGURATION,
                    configEx, logging);
            
            SimpleLogger.traceThrowable(Severity.ERROR,location, configEx, "SDT.migration.000004",
            		"Cannot create subconfiguration for migration container");
            
            //logging.logThrowable("Cannot create sub configuration for migration container.",
            //    configEx);
            throw migrationExc;
        }

        // Getting the working directory
        String applicationDirName = null;

        try
        {
            applicationDirName =
                communicator.getMyWorkDirectory(applicationName);

            File appDir = new File(applicationDirName);

            if (!appDir.exists())
            {
                appDir.mkdirs();
            }
        }
        catch (IOException ioEx)
        {
            MigrationDeploymentException migrationExc =
                new MigrationDeploymentException(ExceptionConstants.CANNOT_CREATE_WORKING_DIRECTORY,
                    applicationName, ioEx, logging);
            
            SimpleLogger.traceThrowable(Severity.ERROR,location,migrationExc,
            		"SDT.migration.000006", 
            		"Cannot create working directory for the migration container");
            
            //logging.logThrowable("Cannot create working directory for migration container.",
            //    migrationExc);
            throw migrationExc;
        }

        // construct the component name 
        int marsLength = archiveFiles.length;

        String[] componentNames = new String[marsLength];
        Vector filesForLoader = new Vector();

        try
        {
            for (int i = 0; i < marsLength; i++)
            {
                String archiveEarPath =
                    (String) fileMap.get(archiveFiles[i].getAbsolutePath());

                // throw exception if the archive path is empty 
                if ((archiveEarPath == null) || (archiveEarPath.length() == 0))
                {
                    MigrationDeploymentException migrationExc =
                        new MigrationDeploymentException(ExceptionConstants.FILE_MAPPING_FOR_ARCHIVE_IS_EMPTY,
                            archiveFiles[i], logging);
                    
                    SimpleLogger.traceThrowable(Severity.ERROR,
                    		location, migrationExc, "SDT.migration.000007", "File mapping for archives is empty");
                  
                    //logging.logThrowable("File mapping for archives is empty.",
                    //    migrationExc);
                    throw migrationExc;
                }

                String fileName =
                    applicationDirName + File.separatorChar + archiveEarPath;
               
                filesForLoader.add(fileName);

                File file = new File(fileName);

                FileUtils.copyFile(archiveFiles[i], file);

                try
                {
                    if (isUpdate)
                    {
                        configuration.deleteAllFiles();
                    }
                    configuration.addFileEntry(file);
                }
                catch (ConfigurationException configEx)
                {
                    MigrationDeploymentException migrationExc =
                        new MigrationDeploymentException(ExceptionConstants.UNABLE_TO_UPDATE_DATABASE,
                            file, configEx, logging);
                    
                    
                    SimpleLogger.traceThrowable(Severity.ERROR,
                    		location, configEx, "SDT.migration.000008", 
                    		"It is not possible to update the database for the file {0}", 
                    		file.getName());
                   
                    //logging.logThrowable("It is not possible to update the database for the file ",
                    //    migrationExc);
                    throw migrationExc;
                }

                String mainMigrationClassName =
                    getMainModuleClassName(archiveFiles[i]);

                if ((mainMigrationClassName == null) ||
                    mainMigrationClassName.equals(""))
                {
                    MigrationDeploymentException migrationExc =
                        new MigrationDeploymentException(ExceptionConstants.EMPTY_MIGRATION_MAIN_CLASS_NAME,
                            applicationName, logging);
                    
                    SimpleLogger.traceThrowable(Severity.ERROR,
                    		location,migrationExc, "SDT.migration.000009", 
                    		"No Main-Class property defined in the MANIFEST.MF");

                    throw migrationExc;
                }

                SimpleLogger.trace(Severity.DEBUG,
                		location, "SDT.migration.000010", 
                		"Migration class name in MANIFEST.MF is {0}", 
                		mainMigrationClassName);
                
                // logging.logDebug("Migration class name in MANIFEST.MF is " +
                //     mainMigrationClassName);

                // Search in the jar files for the main module class 
                // defined in the manifest
                String entryName = mainMigrationClassName.replace('.', '/');

                if (!entryName.endsWith("class"))
                {
                    entryName += ".class";
                }

                if ((new JarFile(archiveFiles[i])).getEntry(entryName) == null)
                {
                    MigrationDeploymentException migrationExc =
                        new MigrationDeploymentException(ExceptionConstants.MISSING_MIGRATION_MAIN_CLASS_NAME,
                            new Object[] { applicationName }, logging);
                    
                    SimpleLogger.traceThrowable(Severity.ERROR,
                    		location, migrationExc, "SDT.migration.000011", 
                    		"The main migration class defined in the manifest was not found in the archive");
                   
                    //logging.logThrowable("The main migration class defined in the manifest was not found in the archive",
                    //    migrationExc);
                    
                    throw migrationExc;
                }

                componentNames[i] = fileName;
                SimpleLogger.trace(Severity.DEBUG,location, 
                		"SDT.migration.000012", "Deployed component {0}", fileName); 
                
                //logging.logDebug("Deployed component " + fileName);
            }

            // end for
        }
        catch (IOException ioe)
        {
            MigrationDeploymentException migrationExc =
                new MigrationDeploymentException(ExceptionConstants.COPYING_FILE_EXCEPTION,
                    ioe, logging);
            
            SimpleLogger.traceThrowable(Severity.ERROR,
            		location, migrationExc, "SDT.migration.000013", "Error while copying file");
            
            // logging.logThrowable("Error copying file", migrationExc);
            
            throw migrationExc;
        }

//        if (!isUpdate)
//        {
            try
            {
            	SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000014", 
            			"Setting initial values for the application {0}", applicationName);
            	
                initApplicationConfig(applicationName);

                SimpleLogger.trace(Severity.DEBUG, location, 
                		"SDT.migration.000015", "Initial values for {0} are set", applicationName);

            }
            catch (ConfigException exc)
            {
                MigrationDeploymentException migrationExc =
                    new MigrationDeploymentException(ExceptionConstants.CANNOT_SET_INITIAL_VALUES,
                        exc, logging);
                
                SimpleLogger.traceThrowable(Severity.ERROR, location,
                		migrationExc, "SDT.migration.000016",
                		"Cannot set the initial values for the application {0}", applicationName);
              
                //logging.logThrowable(
                //    "Cannot set the initial values for the application " +
                //    applicationName, migrationExc);
                
                throw migrationExc;
            }
//        }
        ApplicationDeployInfo appInfo = new ApplicationDeployInfo();

        String[] filesForLoaderArray = new String[filesForLoader.size()];

        for (int i = 0; i < filesForLoader.size(); i++)
        {
            filesForLoaderArray[i] = (String) filesForLoader.get(i);

            SimpleLogger.trace(Severity.DEBUG,location, 
            		"SDT.migration.000017", "Setting classes for classloader {0}", 
            		filesForLoader.get(i));

        }

        appInfo.setFilesForClassloader(filesForLoaderArray);
        appInfo.setDeployedComponentNames(componentNames);

        SimpleLogger.log(Severity.INFO, category,location,
        		"SDT.migration.000018", "Application successfully deployed {0}", applicationName);
        //logging.logInfo("Application successfully deployed " + applicationName);

        return appInfo;
    }

    public void downloadApplicationFiles(String applicationName, Configuration applicationConfig)
        throws DeploymentException, WarningException
    {
        try { 
            SimpleLogger.trace(Severity.DEBUG,location, 
            		"SDT.migration.000019", "Downloading application {0}", applicationName);
        	
            //logging.logDebug("MigrationContainer: Downloading application " + applicationName);
        	
            Configuration conf = applicationConfig.getSubConfiguration(CONTAINER_NAME);	
            String workDir = communicator.getMyWorkDirectory(applicationName); 
            Map fileEntries = conf.getAllFileEntries();

            if (!fileEntries.isEmpty()) {
                Set keySet = fileEntries.keySet();
                
                Iterator iter = keySet.iterator();
                while (iter.hasNext()) {
                    String filename = (String) iter.next();
                    FileOutputStream fileout = new FileOutputStream(workDir + File.separatorChar + filename);
                    InputStream in = conf.getFile(filename);
                    byte[] buf = new byte[1024];
                    int received = 0;

                    while ((received = in.read(buf)) != -1) {
                      fileout.write(buf, 0, received);
                    }
                    fileout.close();
                    
                    SimpleLogger.trace(Severity.DEBUG,location, 
                    		"SDT.migration.000020", "Application successfully downloaded {0}", 
                    		workDir + File.separatorChar + filename);
                    
                    //logging.logDebug("MigrationContainer: Application successfully downloaded " + 
                  	//	  workDir + File.separatorChar + filename);
                }
              }

          } catch (IOException ioex) {
       	      // only log exception, if the error occurs during upgrade
        	  // the migration service will return the error
        	  
        	  SimpleLogger.traceThrowable(Severity.ERROR,
        			  location, ioex, "SDT.migration.000021", "Cannot download application {0}", applicationName);
              
          } catch (ConfigurationException cex) {
       	      // only log exception, if the error occurs during upgrade
        	  // the migration service will return the error       	  
        	  SimpleLogger.traceThrowable(Severity.ERROR,
        			  location,cex, "SDT.migration.000022", 
        			  "Problem occured while downloading application {0}", applicationName);
        	  //logging.logThrowable("ConfigurationException while downloading application: " 
        	  //	  + applicationName, cex);        	  
          }

    }

    public String getApplicationName(File arg0) throws DeploymentException
    {
        return null;
    }

    public File[] getClientJar(String arg0)
    {
        return null;
    }

    public ContainerInfo getContainerInfo()
    {
        return containerInfo;
    }

    public String[] getResourcesForTempLoader(String arg0)
        throws DeploymentException
    {
        return null;
    }

    public void makeAppInfoChange(String arg0, AdditionalAppInfo arg1,
        Configuration arg2) throws WarningException, DeploymentException
    {
    }

    public ApplicationDeployInfo makeSingleFileUpdate(FileUpdateInfo[] arg0,
        ContainerDeploymentInfo arg1, Properties arg2)
        throws DeploymentException
    {
        return null;
    }

    public ApplicationDeployInfo makeUpdate(File[] files,
        ContainerDeploymentInfo deployInfo, Properties props)
        throws DeploymentException
    {
    	
        logging.logDebug("Updating " + deployInfo.getApplicationName() +
            " configuration " + deployInfo.getConfiguration().getPath());

        /*    try {
                logging.logDebug("Updating: delete configuration for application "
                                 + deployInfo.getApplicationName() + " path "
                                 + deployInfo.getConfiguration().getPath());

                deployInfo.getConfiguration().deleteConfiguration(CONTAINER_NAME);

            } catch (ConfigurationException cex)
            {
                logging.logThrowable("Cannot delete configuration of application "
                   + deployInfo.getApplicationName(), cex);
            }
            */
        if ((files != null) && (files.length > 0))
        {
        	SimpleLogger.trace(Severity.DEBUG,
        			location, "SDT.migration.000023", "Performing update on application {0}", 
        			deployInfo.getApplicationName());

            ApplicationDeployInfo appDeployInfo =
                deploy(files, deployInfo, props);

            return appDeployInfo;
        }
        else
        {
        	SimpleLogger.trace(Severity.DEBUG,location, 
        			"SDT.migration.000024", 
        			"Performing update. No files to be deployed for application {0}", 
        			deployInfo.getApplicationName());

        	return null;
        }
    }

    public boolean needStopOnAppInfoChanged(String arg0, AdditionalAppInfo arg1)
    {
        return false;
    }

    public boolean needStopOnSingleFileUpdate(FileUpdateInfo[] arg0,
        ContainerDeploymentInfo arg1, Properties arg2)
        throws DeploymentException, WarningException
    {
        return false;
    }

    public boolean needStopOnUpdate(File[] arg0, ContainerDeploymentInfo arg1,
        Properties arg2) throws DeploymentException, WarningException
    {
        return true;
    }

    public boolean needUpdate(File[] arg0, ContainerDeploymentInfo arg1,
        Properties arg2) throws DeploymentException, WarningException
    {
        return true;
    }

    public void notifyAppInfoChanged(String arg0) throws WarningException
    {
    }

    public void notifyDeployedComponents(String arg0, Properties arg1)
        throws WarningException
    {
    }

    public void notifyRuntimeChanges(String arg0, Configuration arg1)
        throws WarningException
    {
    }

    public void notifySingleFileUpdate(String arg0, Configuration arg1,
        Properties arg2) throws WarningException
    {
    }

    public void notifyUpdatedComponents(String arg0, Configuration arg1,
        Properties arg2) throws WarningException
    {
    }

    public void prepareDeploy(String arg0, Configuration arg1)
        throws DeploymentException, WarningException
    {
    }

    public void prepareRuntimeChanges(String arg0)
        throws DeploymentException, WarningException
    {
    }

    public void prepareSingleFileUpdate(String arg0)
        throws DeploymentException, WarningException
    {
    }

    public void prepareStart(String arg0, Configuration arg1)
        throws DeploymentException, WarningException
    {
    }

    public void prepareStop(String arg0, Configuration arg1)
        throws DeploymentException, WarningException
    {
    }

    public void prepareUpdate(String arg0)
        throws DeploymentException, WarningException
    {
    }

    public void remove(String appName)
        throws DeploymentException, WarningException
    {
        // remove the application settings from 
        // the migration service configuration 
        try
        {
            //removeAppConfiguration(appName);
            setApplicationToUndeployed(appName);
        }
        catch (ConfigException configEx)
        {
            MigrationDeploymentException migrationExc =
                new MigrationDeploymentException(ExceptionConstants.CANNOT_SET_APPLICATION_TO_UNDEPLOYED,
                    configEx, logging);
            
            SimpleLogger.traceThrowable(Severity.ERROR,location,configEx, 
            		"SDT.migration.000025", "Cannot delete configuration for application {0}", appName);
            
            //logging.logThrowable("Cannot delete configuration for application " +
            //    appName, configEx);
            
            throw migrationExc;
        }
    }

    public void removeProgressListener(ProgressListener arg0)
    {
    }

    public void rollbackDeploy(String arg0) throws WarningException
    {
    }

    public void rollbackRuntimeChanges(String arg0) throws WarningException
    {
    }

    public void rollbackSingleFileUpdate(String arg0, Configuration arg1)
        throws WarningException
    {
    }

    public void rollbackStart(String arg0) throws WarningException
    {
    }

    public void rollbackStop(String arg0) throws WarningException
    {
    }

    public void rollbackUpdate(String arg0, Configuration arg1, Properties arg2)
        throws WarningException
    {
    }

    /** Used to get the name of the main class in the migration module
     *
     * @param fileName
     * @return
     * @throws MigrationDeploymentException
     */
    private String getMainModuleClassName(File aFile)
        throws MigrationDeploymentException
    {
        JarFile file = null;
        String result = null;

        try
        {
            file = new JarFile(aFile);
            result = getMainMigrationModuleClass(file.getManifest());
        }
        catch (IOException exc)
        {
            MigrationDeploymentException migrationExc =
                new MigrationDeploymentException(ExceptionConstants.UNABLE_TO_READ_FILE_FROM_ARCHIVE,
                    new Object[] { "Main-Class", aFile }, exc, logging);
            
            SimpleLogger.traceThrowable(Severity.ERROR,location,migrationExc, 
            		"SDT.migration.000026", "Unable to read from archives");
           
            //logging.logThrowable("Unable to read from archives", migrationExc);
            throw migrationExc;
        }
        finally
        {
            try
            {
                if (file != null)
                {
                    file.close();
                }
            }
            catch (IOException e)
            {
                MigrationDeploymentException ioEx =
                    new MigrationDeploymentException(ExceptionConstants.ERROR_CLOSING_ARCHIVE,
                        aFile, e, logging);

                SimpleLogger.traceThrowable(Severity.ERROR, location, e, 
                		"SDT.migration.000027", "Error while closing archive {0}", aFile);
                
                //logging.logThrowable("There is an error while closing archive",
                //    ioEx);
            }
        }

        return result;
    }

    /**
     *  Gets the main migration module class from the manifest
     * @param mf the manifest that should be used
     * @return the main module class as string
     */
    private String getMainMigrationModuleClass(Manifest mf)
    {
        Attributes attributes = mf.getMainAttributes();

        return attributes.getValue("Main-Class");
    }

    public CMigrationResult migrateContainerLogic(CMigrationInfo migrationInfo)
        throws CMigrationException
    {
        CMigrationResult result = new CMigrationResult();

    	if (!isEngineInSafeAppmigrateMode())
    	{
    		return result;
    	}
    	SimpleLogger.trace(Severity.DEBUG, location, "SDT.migration.000028", 
    			"Call migrate for application {0}", migrationInfo.getAppName());
       
    	//logging.logDebug("Call migrateContainerLogic for " +
        //    migrationInfo.getAppName());

        DataSource dataSource = null;

        try
        {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("jdbc/SAP/BC_MIGSERVICE");
            
            SimpleLogger.trace(Severity.DEBUG, location, "SDT.migration.000029", 
            		"Look up for DataSource passed successfully");
     
        }
        catch (NamingException exc)
        {
        	SimpleLogger.traceThrowable(Severity.ERROR, location, exc, 
        			"SDT.migration.000030", "Look up for datasource jdbc/SAP/BC_MIGSERVICE failed");
            //$JL-EXC$
            throw new CMigrationException(
                "The datasource cannot be looked up. Looking for " +
                "jdbc/SAP/BC_MIGSERVICE " + exc.getMessage());
        }

        String applicationName = migrationInfo.getAppName();

        ClassLoader applicationLoader = migrationInfo.getAppLoader();
                
        String fileToExecute = null;
        Configuration applicationConfiguration = null;

        String marFile = null; //***
        try
        {
            applicationConfiguration =
                migrationInfo.getAppConfig().getSubConfiguration(CONTAINER_NAME);

            fileToExecute =
                getMigrationClassFromConfig(applicationConfiguration,
                    applicationName);

            marFile = applicationName.replace('/', '~') + ".mar";            
            
            SimpleLogger.trace(Severity.DEBUG, location, "SDT.migration.000031",
            		"Getting information from the config manager about the file to be executed : {0}", fileToExecute);

        }
        catch (Exception exc)
        {
            MigrationModuleException ioEx =
                new MigrationModuleException(ExceptionConstants.ERROR_GETTING_MIGRATION_MODULE_FROM_CONFIG,
                    applicationName, exc, logging);
            
            SimpleLogger.traceThrowable(Severity.ERROR, location, exc, "SDT.migration.000032", 
            		"Problem occured while extracting migration module to be executed from the configuration manager");

            throw new CMigrationException("There is an exception while extracting migration module from Config",
                exc);
        }

        if (fileToExecute != null)
        {
            Class myClass = null;
            ApplicationMigrationIF migrationObj = null;

            try
            {
                myClass = applicationLoader.loadClass(fileToExecute);

                migrationObj = (ApplicationMigrationIF) myClass.newInstance();
            }
            catch (ClassNotFoundException exc)
            {
                MigrationModuleException ioEx =
                    new MigrationModuleException(ExceptionConstants.ERROR_LOADING_MIGRATION_CLASS_FROM_LOADER,
                        applicationName, exc, logging);

                SimpleLogger.traceThrowable(Severity.ERROR, location, exc, 
                		"SDT.migration.000033", 
                		"Problem occured while loading the main migration module class from the application classloader." + 
                				" The file cannot be loaded");

                throw new CMigrationException("The main migration module class cannot be loaded from application classloader",
                    exc);
            }
            catch (IllegalAccessException exc)
            {
                MigrationModuleException ioEx =
                    new MigrationModuleException(ExceptionConstants.ERROR_ACCESSING_MIGRATION_CLASS,
                        applicationName, exc, logging);

                SimpleLogger.traceThrowable(Severity.ERROR, location, exc, "SDT.migration.000034", 
                		"The main migration module class cannot be accessed");

                throw new CMigrationException("The main migration module class cannot be accessed",
                    exc);
            }
            catch (InstantiationException exc)
            {
                MigrationModuleException ioEx =
                    new MigrationModuleException(ExceptionConstants.ERROR_INSTANTIATING_MIGRATION_CLASS,
                        applicationName, exc, logging);
                
                SimpleLogger.traceThrowable(Severity.ERROR, location, exc, "SDT.migration.000035", 
                		"Problem while instantiating the main migration module class");

                throw new CMigrationException("The main migration module class cannot be instantiated",
                    exc);
            }
            catch (ClassCastException exc)
            {
                MigrationModuleException ioEx =
                    new MigrationModuleException(ExceptionConstants.MIGRATION_CLASS_IMPLEMENTATION_ERROR,
                        applicationName, exc, logging);
                
                SimpleLogger.traceThrowable(Severity.ERROR, location, exc, "SDT.migration.000036", 
                		"The main migration module class does not implement ApplicationMigrationIF");

                throw new CMigrationException("The main migration module class does not implement ApplicationMigrationIF",
                    exc);
            }

            // Calling the migration module to execute its migration logic
            String csnComponent = "";

            // store original class loader and set the class loader for the application 
            ClassLoader originalContextClassLoader =
                Thread.currentThread ().getContextClassLoader ();
            Thread.currentThread ().setContextClassLoader (
                myClass.getClassLoader ());
            
            if (migrationObj != null)
            {
                String moduleName = migrationObj.getMigrationModuleName();
                String moduleVersion = migrationObj.getMigrationModuleVersion();

                csnComponent = migrationObj.getCSNComponent();

                if ((csnComponent == null) || (csnComponent.length() == 0))
                {
                	SimpleLogger.trace(Severity.WARNING, location, "SDT.migration.000037", 
                			"The CSN component for application {0} is not specified.", applicationName);
                }
                else
                {
                	try{
                        storeModuleCSNComponent(applicationName, csnComponent);
                	} catch (Exception exc)
					{
                		
                		// do nothing here, only log error
                		SimpleLogger.traceThrowable(Severity.ERROR, location, exc, "SDT.migration.000038", 
                				"Problem while storing CSN component for {0}", applicationName);

					}
                }
                
                SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000039", "Creating migration context {0}", 
                		applicationConfiguration.getPath());

                // create the migration context
                MigrationContext migrationCtx =
                    new MigrationContext(handler, applicationName, 
                    		dataSource, migrationInfo.getAppLoader());
         
                moduleLocation = migrationCtx.getLocation();
                moduleCategory = migrationCtx.getCategory();
                if (moduleName == null)
                {
                	SimpleLogger.trace(Severity.ERROR, location, "SDT.migration.000040", 
                			"The name of the component to be upgraded is null");
                    throw new CMigrationException(
                        "The name of the component to be upgraded is null");
                }

                SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000041", 
                		"Calling init for {0}", moduleName);
                
                int status = -1;

               
                try
                {
                	SimpleLogger.trace(Severity.INFO,location, "SDT.migration.000042", 
                			"Migration of application {0}: module {1} with version {2} started", 
                			applicationName,moduleName,moduleVersion);
                	SimpleLogger.log(Severity.INFO, moduleCategory,moduleLocation, "SDT.migration.001112", "Migration of application {0}: module {1} with version {2} started . CSN component for this migrtion module - {3}.", applicationName,moduleName,moduleVersion,csnComponent);
                	
                	migrationObj.init(migrationCtx);
                    status = migrationObj.migrate(migrationCtx);
                }
                catch (MigrationException e)
                {
                    MigrationModuleException moduleEx =
                        new MigrationModuleException(ExceptionConstants.ERROR_DURING_MIGRATION,
                            applicationName, e, logging);

                    SimpleLogger.traceThrowable(Severity.ERROR, location, moduleEx, "SDT.migration.000043", 
                    		"Problem during migration. Create CSN message to component {0}", csnComponent);
                   
                    SimpleLogger.traceThrowable(Severity.ERROR, moduleLocation, moduleEx, "SDT.migration.001113", 
                    		"Problem during migration. Create CSN message to component {0}", csnComponent);
                    
                    //restore the original classloader 
                    Thread.currentThread ().setContextClassLoader (
                            originalContextClassLoader);
                    
                    //call terminate
                    migrationObj.terminate();
                   
                    try
                    {
                    	storeExceptionObj(applicationName, e);
                    	 
                        setIsRestarted(applicationName);
                        storeModuleStatus(applicationName,
                            MigrationConstantsIF.STATUS_ENTRY_FAILED);
                        SimpleLogger.log(Severity.ERROR, moduleCategory,moduleLocation, "SDT.migration.001114","The execution of application {0} module {1} finished with status: {2}",applicationName,moduleName,MigrationConstantsIF.STATUS_ENTRY_FAILED);
                    }
                    catch (ConfigException ex)
                    {
                        MigrationModuleException exc =
                            new MigrationModuleException(ExceptionConstants.ERROR_DURING_MIGRATION,
                                applicationName, ex, logging);
                        
                        SimpleLogger.traceThrowable(Severity.ERROR, location, exc, "SDT.migration.000044", 
                        		"Problem while storing the status of the migration");
                        

                        throw new CMigrationException("There is an exception while storing migration module status",
                            exc);
                    }

                    throw new CMigrationException(
                        "There is an exception when invoking migration module methods." +
                        "Create CSN message to component " + csnComponent,
                        moduleEx);
                }catch (Exception ex)
                {
                	
                    SimpleLogger.traceThrowable(Severity.ERROR, location, ex, "SDT.migration.001115", 
                       		"Problem during migration. Error message: {0}. Create CSN message to component {1}", ex.getMessage(),csnComponent);
                   
                    SimpleLogger.traceThrowable(Severity.ERROR, moduleLocation, ex, "SDT.migration.001116", 
                    		"Problem during migration. Error message: {0}. Create CSN message to component {1}", ex.getMessage(),csnComponent);
                    
                    
                    try
                    {
                    	storeExceptionObj(applicationName, ex);
                    	 
                        setIsRestarted(applicationName);
                        storeModuleStatus(applicationName,
                            MigrationConstantsIF.STATUS_ENTRY_FAILED);
                        SimpleLogger.log(Severity.ERROR, moduleCategory,moduleLocation, "SDT.migration.001114","The execution of application {0} module {1} finished with status: {2}",applicationName,moduleName,MigrationConstantsIF.STATUS_ENTRY_FAILED);
                    }
                    catch (ConfigException conf_ex)
                    {
                        MigrationModuleException exc =
                            new MigrationModuleException(ExceptionConstants.ERROR_DURING_MIGRATION,
                                applicationName, ex, logging);
                        
                        SimpleLogger.traceThrowable(Severity.ERROR, location, exc, "SDT.migration.000044", 
                        		"Problem while storing the status of the migration");
  
                        throw new CMigrationException("There is an exception while storing migration module status",
                            exc);
                    }

                    throw new CMigrationException(
                            "There is an exception when invoking migration module methods." +
                            "Create CSN message to component " + csnComponent,
                            ex);
                }

                migrationObj.terminate();
                
                //restore the original classloader 
                Thread.currentThread ().setContextClassLoader (
                        originalContextClassLoader);

                SimpleLogger.log(Severity.INFO, category, location, "SDT.migration.000045", 
                		"Migration of application {0}, module {1} ended with status {2}", 
                		applicationName,moduleName + moduleVersion,status);


                try
                {
                    setIsRestarted(applicationName);
                    storeModuleStatus(applicationName,
                        MigrationConstantsIF.STATUS_ENTRY_OK);
                    
                    SimpleLogger.log(Severity.INFO, moduleCategory,moduleLocation, "SDT.migration.001114","The execution of application {0} module {1} finished with status: {2}",applicationName,moduleName,MigrationConstantsIF.STATUS_ENTRY_OK);
                }
                catch (ConfigException ex)
                {
                    MigrationModuleException moduleEx =
                        new MigrationModuleException(ExceptionConstants.ERROR_DURING_MIGRATION,
                            applicationName, ex, logging);
                    
                    
                    SimpleLogger.traceThrowable(Severity.ERROR, location, ex, "SDT.migration.000044", 
                    		"Problem while storing the status of the migration");
                    
                    throw new CMigrationException("There is an exception while storing migration module status",
                        moduleEx);
                }
            }
            else
            {
                throw new CMigrationException("migrationObj is null");
            }
        }
        else
        {
            // no file to execute
            // should not happen(such module should not be deployed)
            MigrationModuleException moduleEx =
                new MigrationModuleException(ExceptionConstants.MISSING_MIGRATION_MAIN_CLASS_NAME,
                    new Object[] { applicationName }, logging);

            SimpleLogger.traceThrowable(Severity.ERROR, location, moduleEx, "SDT.migration.000046", 
            		"Problem while invoking some of the migration module methods");


            throw new CMigrationException(
                "There is no main migration class defined");
        }

        try
        {
            String applicationDirName =
                communicator.getMyWorkDirectory(applicationName);
            String fileName =
                applicationDirName + File.separatorChar + fileToExecute;
            String fullMarFileName = applicationDirName + File.separatorChar + marFile;
            
            result.setFilesForClassLoader(new String[] { fileName, fullMarFileName });
        }
        catch (IOException exc)
        {
            MigrationDeploymentException migrationExc =
                new MigrationDeploymentException(ExceptionConstants.CANNOT_CREATE_WORKING_DIRECTORY,
                    applicationName, exc, logging);
            
            SimpleLogger.traceThrowable(Severity.ERROR, location, migrationExc, "SDT.migration.000047", 
            		"Problem while creating working directory for migration container");

        }

        SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000048", 
        		"Application Migration Container - migration finished");
        

        return result;
    }

    public void notifyForMigrationResult(CMigrationStatus[] arg0)
    {
      //  logging.logDebug("Info notifyForMigrationResult ... " + arg0);
    }

    /**
     * Gets the name of the main class of the migration module
     * from the Config
     * @param config the application configuration
     * @param applicationName
     * @return
     * @throws ConfigurationException
     */
    private String getMigrationClassFromConfig(Configuration config,
        String applicationName) throws ConfigurationException
    {
        String result = null;
        String[] fileNames = config.getAllFileEntryNames();

        if ((fileNames == null) || (fileNames.length < 1))
        {
            return null;
        }

        if (fileNames.length != 1)
        {
            // Should throw an exception here 
            // only one migration module by application 
            // is allowed
            return null;
        }

        JarInputStream zis = null;

        try
        {
            zis = new JarInputStream(config.getFile(fileNames[0]));

            if (zis != null)
            {
                String applicationDirName =
                    communicator.getMyWorkDirectory(applicationName);

                String mainClass =
                    getMainMigrationModuleClass(zis.getManifest());

                return mainClass;
            }
        }
        catch (IOException ioe)
        {
            //$JL-EXC$
            zis = null;
        }
        finally
        {
            if (zis != null)
            {
                try
                {
                    zis.close();
                }
                catch (IOException ioe)
                {
                    //$JL-EXC$
                    zis = null;
                }
            }
        }

        return result;
    }

    /**
     * This method is used when deploying a new migration archive
     * to the migration container in order to clean up already
     * existing values if they was previous migration of this application
     *
     * @param appName the name of the application
     */
    private synchronized void initApplicationConfig(String appName)
        throws ConfigException
    {

        Configuration migModuleConfig = null;
        try
        {
            try
            {
                migrationConfig =
                    handler.openConfiguration(MigrationConstantsIF.MIGRATION_MODULES_CONFIG +
                        "/" + appName, ConfigurationHandler.WRITE_ACCESS);
            }
            catch (NameNotFoundException exc)
            {   //$JL-EXC$
                try
                {
                    migModuleConfig =
                        handler.openConfiguration(MigrationConstantsIF.MIGRATION_MODULES_CONFIG,
                            ConfigurationHandler.WRITE_ACCESS);

                    migrationConfig =
                        migModuleConfig.createSubConfigurationHierachy(appName);
                }
                catch (Exception cex)
                {
                    throw new ConfigException(ExceptionConstants.CANNOT_CREATE_SUBCONFIGURATION,
                        new Object[] { appName }, cex, logging.getLocation(),
                        MigrationResourceAccessor.getResourceAccessor());
                }
            }
            catch (Exception exc)
            {
                throw new ConfigException(ExceptionConstants.CANNOT_GET_SUBCONFIGURATION,
                    new Object[] { MigrationConstantsIF.MIGRATION_MODULES_CONFIG },
                    exc, logging.getLocation(),
                    MigrationResourceAccessor.getResourceAccessor());
            }
            try
            {
                // delete all entries if they already exists
                migrationConfig.deleteAllConfigEntries();

                // add default entries with initial values if there are not exist
                // the entries does not exist on first application deployment, and 
                // they should be cleaned by the upgrade procedure on the start of each 
                // upgrade
                if (!migrationConfig.existsConfigEntry(
                        MigrationConstantsIF.STATUS_ENTRY))
                {
                    migrationConfig.addConfigEntry(MigrationConstantsIF.STATUS_ENTRY,
                        MigrationConstantsIF.STATUS_ENTRY_NOT_PASSED);
                }

                if (!migrationConfig.existsConfigEntry(
                        MigrationConstantsIF.RESTART))
                {
                    migrationConfig.addConfigEntry(MigrationConstantsIF.RESTART,
                        new Boolean(false));
                }

                // add this flag if not exist, set the UNDEPLOYED flag to no,
                // if the application has been removed and after that deployed again
                // this is done because no data for the transactions of the migration 
                // modules should be lost with undeploy/deploy application
                migrationConfig.modifyConfigEntry(MigrationConstantsIF.UNDEPLOYED_FLAG,
                    MigrationConstantsIF.NO_OPTION, true);
                commit();
            }
            catch (Exception exc)
            {
                rollback();
                throw new ConfigException(ExceptionConstants.CANNOT_SET_INITIAL_VALUES,
                    new Object[] { appName }, exc, logging.getLocation(),
                    MigrationResourceAccessor.getResourceAccessor());
            }
        }
        finally
        {
            closeAllConfigurations();
        }
    }


    private void storeModuleCSNComponent(String appName, String csnComponent)
        throws ConfigException
    {
        String appConfigName =
            MigrationConstantsIF.MIGRATION_MODULES_CONFIG + "/" + appName;
    	try
	    {
            SimpleLogger.trace(Severity.DEBUG, location, "SDT.migration.000049", 
            		"Setting CSN component for application {0}", appConfigName);
    		//logging.logDebug("Set CSN component for application " +
            //        appConfigName);

            Configuration migConfig =
                handler.openConfiguration(appConfigName,
                    ConfigurationHandler.WRITE_ACCESS);

            migConfig.modifyConfigEntry(MigrationConstantsIF.MM_CSN_COMPONENT,
                csnComponent, true);
            
            commit();

	    }
	    catch (Exception exc)
	    {
	        rollback();
	        
	        throw new ConfigException(ExceptionConstants.CANNOT_SET_CONFIG_ENTRY,
	            new Object[] { MigrationConstantsIF.MM_CSN_COMPONENT }, exc,
	            logging.getLocation(),
	            MigrationResourceAccessor.getResourceAccessor());
	    }
	    finally
	    {
	        closeAllConfigurations();
	    }
    }
    private void setIsRestarted(String appName) throws ConfigException
    {
        String appConfigName =
            MigrationConstantsIF.MIGRATION_MODULES_CONFIG + "/" + appName;
        try
        {
        	SimpleLogger.trace(Severity.DEBUG, location, "SDT.migration.000050", 
        			"Setting restart flag for application {0}", appConfigName);
           
            Configuration migConfig =
                handler.openConfiguration(appConfigName,
                    ConfigurationHandler.WRITE_ACCESS);

            migConfig.modifyConfigEntry(MigrationConstantsIF.RESTART,
                new Boolean(true), true);
            commit();
        }
        catch (NameNotFoundException exc)
        {
            //$JL-EXC$ do nothing  
            logging.logDebug("The configuration for " + appName + " which is " +
                appConfigName + " does not exist. Cannot set restart flag");
        }
        catch (ConfigurationException exc)
        {
            rollback();
            throw new ConfigException(ExceptionConstants.CANNOT_SET_APPLICATION_TO_UNDEPLOYED,
                new Object[] { appConfigName }, exc, logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }
        finally
        {
            closeAllConfigurations();
        }
    }

    private synchronized void setApplicationToUndeployed(String appName)
        throws ConfigException
    {
        String appConfigName =
            MigrationConstantsIF.MIGRATION_MODULES_CONFIG + "/" + appName;
        try
        {
        	SimpleLogger.trace(Severity.DEBUG, location, "SDT.migration.000051", 
        			"Removing migration service settings for application {0}", appConfigName);

            Configuration migConfig =
                handler.openConfiguration(appConfigName,
                    ConfigurationHandler.WRITE_ACCESS);

            // do not remove the application configuration
            // as later deploy of the application and start of migration
            // will lead to loose of transaction status
            // only set the UNDEPLOYED_FLAG flag in configuration to Yes				
            migConfig.modifyConfigEntry(MigrationConstantsIF.UNDEPLOYED_FLAG,
                MigrationConstantsIF.YES_OPTION, true);
            commit();
        }
        catch (NameNotFoundException exc)
        {
            // $JL-EXC$ do nothing  
            logging.logDebug("The configuration for " + appName + " which is " +
                appConfigName + " does not exist. May be already removed");
        }
        catch (ConfigurationException exc)
        {
            rollback();
            throw new ConfigException(ExceptionConstants.CANNOT_SET_APPLICATION_TO_UNDEPLOYED,
                new Object[] { appConfigName }, exc, logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }
        finally
        {
            closeAllConfigurations();
        }
    }

    /*
    private void removeAppConfiguration(String appName)
        throws ConfigException
    {
        String appConfigName =
            MigrationConstantsIF.MIGRATION_MODULES_CONFIG + "/" + appName;
        try
        {
            logging.logInfo(
                "Removing Migration Service setting for application " +
                appConfigName);
            Configuration migConfig =
                handler.openConfiguration(appConfigName,
                    ConfigurationHandler.WRITE_ACCESS);
            migConfig.deleteConfiguration();
            commit();
        }
        catch (NameNotFoundException exc)
        {
            // $JL-EXC$
            //do nothing  
            logging.logInfo("The configuration for " + appName + " which is " +
                appConfigName + " does not exist. May be already removed");
        }
        catch (ConfigurationException exc)
        {
            rollback();
            throw new ConfigException(ExceptionConstants.CANNOT_DELETE_CONFIGURATION,
                new Object[] { appConfigName }, exc, logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }
        finally
        {
            closeAllConfigurations();
        }
    }
    */

    private Configuration openCfg(String appName, boolean createIfNotExist)
        throws ConfigException
    {
        // open configuration for writing
        String appConfigName =
            MigrationConstantsIF.MIGRATION_MODULES_CONFIG + "/" + appName;
        try
        {
            logging.logDebug("Open configuration " + appConfigName +
                " for writing");
            migrationConfig =
                handler.openConfiguration(appConfigName,
                    ConfigurationHandler.WRITE_ACCESS);
        }
        catch (NameNotFoundException exc)
        {
            // $JL-EXC$
            if (createIfNotExist)
            {
                try
                {
                	SimpleLogger.trace(Severity.DEBUG,location, "SDT.migration.000052", 
                			"Creating configuration {0} as it doesn't exist", appConfigName);
                	
                    //logging.logDebug("Create configuration " + appConfigName +
                    //    " because for some reason it does not exist.");
                    migrationConfig =
                        handler.createSubConfiguration(appConfigName);
                }
                catch (Exception cex)
                {
                    throw new ConfigException(ExceptionConstants.CANNOT_CREATE_SUBCONFIGURATION,
                        new Object[] { appConfigName }, cex,
                        logging.getLocation(),
                        MigrationResourceAccessor.getResourceAccessor());
                }
            }
        }
        catch (Exception exc)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_GET_SUBCONFIGURATION,
                new Object[] { appConfigName }, exc, logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }

        return migrationConfig;
    }

    /**
     * Use this method to submit any pending transactions and
     * to close the open configurations
     */
    private void closeAllConfigurations() throws ConfigException
    {
        try
        {
            // commit any uncommitted transactions 
            // and close configuration 
            if (handler != null)
            {
                handler.commit();
                handler.closeAllConfigurations();
            }
        }
        catch (Exception exc)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_CLOSE_CONFIGURATION,
                new Object[] {  }, exc, logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }
    }

    private void rollback() throws ConfigException
    {
        try
        {
            if (handler != null)
            {
                handler.rollback();
            }
        }
        catch (Exception exc)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_ROLLBACK,
                new Object[] {  }, exc, logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }
    }

    private void commit() throws ConfigException
    {
        try
        {
            if (handler != null)
            {
                handler.commit();
            }
        }
        catch (Exception exc)
        {
            throw new ConfigException(ExceptionConstants.CANNOT_COMMIT_CONFIGURATION,
                new Object[] {  }, exc, logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }
    }

    
    private void storeModuleStatus(String appName, String status)
        throws ConfigException
    {
        try
        {
            Configuration config = openCfg(appName, true);
            config.modifyConfigEntry(MigrationConstantsIF.STATUS_ENTRY, status,
                true);
            commit();
        }
        catch (Exception exc)
        {
            rollback();
            throw new ConfigException(ExceptionConstants.CANNOT_SET_CONFIG_ENTRY,
                new Object[] { MigrationConstantsIF.STATUS_ENTRY }, exc,
                logging.getLocation(),
                MigrationResourceAccessor.getResourceAccessor());
        }
        finally
        {
            closeAllConfigurations();
        }
    }
    
    private void storeExceptionObj(String appName, Exception migrationExc)
        throws ConfigException
    {
        String appConfigName =
            MigrationConstantsIF.MIGRATION_MODULES_CONFIG + "/" + appName;

    	try
	    {

            Configuration migConfig =
                handler.openConfiguration(appConfigName,
                    ConfigurationHandler.WRITE_ACCESS);
            
            String exceptionTxt = " ";
            if(migrationExc.getMessage()!=null)
            {
            	exceptionTxt = migrationExc.getMessage();
            }
            migConfig.modifyConfigEntry(MigrationConstantsIF.STATUS_EXC_TEXT,
            		exceptionTxt, true);
           
	        commit();
	    }
	    catch (Exception exc)
	    {
	        rollback();
	        throw new ConfigException(ExceptionConstants.CANNOT_SET_CONFIG_ENTRY,
	            new Object[] { MigrationConstantsIF.STATUS_EXC_TEXT }, exc,
	            logging.getLocation(),
	            MigrationResourceAccessor.getResourceAccessor());
	    }
	    finally
	    {
	        closeAllConfigurations();
	    }
    }
    
    private boolean isEngineInSafeAppmigrateMode()
    {
        CoreMonitor cm = serviceContext.getCoreContext().getCoreMonitor();

        if ((cm.getRuntimeMode() == CoreMonitor.RUNTIME_MODE_SAFE) &&
            (cm.getRuntimeAction() == CoreMonitor.RUNTIME_ACTION_APPLICATION_MIGRATE))
        {
            return true;
        }

        return false;
    }


}
