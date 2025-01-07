/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.scheduleradapter.jobdeploy;

import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterfaceExtension;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.ProgressListener;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.deploy.container.op.start.ApplicationStartInfo;
import com.sap.engine.services.deploy.container.op.start.ContainerStartInfo;
import com.sap.engine.services.ejb3.model.Bean;
import com.sap.engine.services.ejb3.model.BeanType;
import com.sap.engine.services.ejb3.model.MessageBean;
import com.sap.engine.services.ejb3.model.MissingOptionalDataException;
import com.sap.engine.services.ejb3.model.Module;
import com.sap.engine.services.ejb3.model.MsgActivationConfig;
import com.sap.engine.services.ejb3.model.MsgActivationConfigProperty;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduler.runtime.event.EventManager;
import com.sap.engine.services.scheduleradapter.repository.DuplicateJobDefinitionException;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Severity;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionName;
import com.sap.scheduler.runtime.MDBJobDefinition;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Set;

/**
 * This container implementation is called by the deploy service when the 
 * deployed archive contains a &lt;container-type&gt;scheduler~container&lt;/container-type&gt;
 * tag.
 * 
 * @author Mladen Droshev
 * @author Dirk Marwinski
 */
public class SchedulerContainerImpl implements ContainerInterfaceExtension {

    public static final String CONTAINER_NAME = "scheduler~container";

    public static final int APP_PRIORITY = 65;

    private final static Location location = Location
                          .getLocation(SchedulerContainerImpl.class);

    private final static Category category = LoggingHelper.SYS_SERVER;

    public static final String BIN = "bin";

    public static final String CFG = "cfg";

    public static final String[] JOB_SCHEDULER_FILES = new String[] { ".jar" };
    
    private final ContainerInfo containerInfo = new ContainerInfo();

    private Environment environment = null;

    private DeployCommunicator communicator = null;

    /**
     * Hashtable to store job definitions for the update procedure. The
     * database is only updated on commit during an update. Must be 
     * synchronized.
     */
    private Hashtable<String, HashMap<JobDefinitionName,JobDefinition>> mUpdatedJobsHash = new Hashtable<String, HashMap<JobDefinitionName,JobDefinition>>();

    public static String JOB_DEPLOY_FAILED = "deploy_job_00";

    public static String JOB_DB_UPDATE_FAILED = "deploy_job_01";

    public SchedulerContainerImpl(ApplicationServiceContext context,
            Environment env) throws ServiceException {
        
        this.environment = env;
        initContainerInfo(context.getServiceState().getServiceName());
        
    }

    public void setCommunicator(DeployCommunicator dCommunnicator) {
        this.communicator = dCommunnicator;
    }

    public DeployCommunicator getCommunicator() {
        return this.communicator;
    }

    public ContainerInfo getContainerInfo() {
        return containerInfo;
    }

    public ApplicationDeployInfo deploy(File[] files,
                    ContainerDeploymentInfo containerDeploymentInfo,
                    Properties properties) throws DeploymentException {

        String applicationName = containerDeploymentInfo.getApplicationName();

        HashMap<JobDefinitionName,JobDefinition> mdbJobDefinitions = getJobDefinitionsFromDeployment(files, containerDeploymentInfo, properties);
        
        // ------------------------------------------------------------------
        // store job definitions in database
        // ------------------------------------------------------------------

        ArrayList<UpdateJobDefinition> updatedJobDefinitions = null;
        
        try {
            updatedJobDefinitions = environment.getJobRepository().updateRepository(applicationName, mdbJobDefinitions);
        } catch (DuplicateJobDefinitionException dje) {
            
            throw new DeploymentException(SchedulerContainerImpl.JOB_DEPLOY_FAILED, new Object[] {applicationName, dje.getMessage()}, dje);
        } catch (ConfigurationParserException cpe) {

            throw new DeploymentException(SchedulerContainerImpl.JOB_DEPLOY_FAILED, new Object[] {applicationName, cpe.getMessage()}, cpe);
        } catch (SQLException sql) {

            throw new DeploymentException(SchedulerContainerImpl.JOB_DB_UPDATE_FAILED, new Object[] {applicationName}, sql);
        }

        // ------------------------------------------------------------------
        // tell everyone interested about the new jobs
        // ------------------------------------------------------------------

        raiseChangeEvents(updatedJobDefinitions);

        // debug messages
        //
        if (location.beDebug()) {
            StringBuffer msg = new StringBuffer("The following jobs have been deployed as part of application \"" 
                    + applicationName + "\": ");
            for (UpdateJobDefinition udef : updatedJobDefinitions) {
                msg.append(udef.getJobDefinition().getJobDefinitionName().toString()).append(", ");
            }
            location.debugT(msg.toString());
        }

        
        String[] jobNames = new String[updatedJobDefinitions.size()];
        int i=0;
        for (UpdateJobDefinition udef : updatedJobDefinitions) {
            jobNames[i++] = udef.getJobDefinition().getJobDefinitionName().toString();
        }

        ApplicationDeployInfo appInfo = new ApplicationDeployInfo();
        appInfo.setDeployedComponentNames(jobNames);
        return appInfo;
    }


    public void notifyDeployedComponents(String applicationName,
                                         Properties properties)  
                                                     throws WarningException {

        // called on all nodes except one (where deploy is invoked)
        // we are not intested in it.
    }

    public void prepareDeploy(String applicationName,
                      Configuration configuration) throws DeploymentException,
                                                          WarningException {

        // we never object here
    }

    public void commitDeploy(String applicationName) throws WarningException {

        // we do nothing here (as we have already written the records to
        // the database
    }

    public void rollbackDeploy(String applicationName) throws WarningException {
        
        if (location.beDebug()) {
            location.debugT("Received rollback request for application \"" +
                    applicationName + "\". Deactivating job definitions.");
        }
        
        // ---------------------------------------------------------------
        // deactivate all jobs which belong to this application from the 
        // database (e.g. mark them outdated)
        // ---------------------------------------------------------------

        try {
            environment.getJobRepository().deactivateJobDefinitions(applicationName);
        } catch (SQLException sql) {
            // It it too late to report an error to the deploy service, so 
            // log as a fatal error here. If this fails something is very 
            // wrong an not only we are affected.
            //
            category.logThrowableT(Severity.FATAL, location, "Application deploy request for application \"" + applicationName +
                    "\" was rolled back but error deactivating the corresponding job definitions from" +
                    " the database.", sql);
        }
    }

    public boolean needUpdate(File[] archiveFiles,
            ContainerDeploymentInfo containerDeploymentInfo,
            Properties properties) throws DeploymentException, WarningException {

        // do nothing here, checking whether an update is needed is 
        // nearly as expensive as the code in makeUpdate. Possible future 
        // optimization is to store the CRC of the jar files
        //
        return true;
    }

    public boolean needStopOnUpdate(File[] archiveFiles,
            ContainerDeploymentInfo containerDeploymentInfo,
            Properties properties) throws DeploymentException, WarningException {

        // do nothing here, application may run during deployment (for us)
        //
        return false;
    }

    public ApplicationDeployInfo makeUpdate(File[] files,
                                            ContainerDeploymentInfo containerDeploymentInfo,
                                            Properties properties) 
                                                        throws DeploymentException {

        String applicationName = containerDeploymentInfo.getApplicationName();

        // If we get an empty file array here it an update deployment where
        // the original application did contain jobs but the new application
        // does not contain jobs anymore, so we just remove all jobs from
        // this application.
        //
        // Note: the jobs are removed here and they will not be recreated 
        // even if the deployment fails (e.g. if there is a rollbackUpdate)
        //
        if (files.length == 0) {
            if (location.beDebug()) {
                location.debugT("Application \"" + applicationName +
                        "\" is being redeployed. The updated application does " +
                        "not contain any jobs anymore. Removing all jobs which were part " +
                        "of the original application.");
            }

            ArrayList<JobDefinition> removedDefinitions = null;
            try {
                removedDefinitions = environment.getJobRepository().deactivateJobDefinitions(applicationName);
            } catch (SQLException sql) {
                category.logThrowableT(Severity.FATAL, location, "Application \"" + applicationName +
                        "\" undeployed but error deactivating the corresponding job definitions from" +
                        " the database. The job definition repository is inconsistent.", sql);
            }
            
            if (removedDefinitions != null) {
                EventManager mgr = environment.getEventManager();
                for (JobDefinition def : removedDefinitions) {
                    mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_UNDEPLOYED, def.getJobDefinitionName().toString());
                    mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_UNDEPLOYED1, def.getJobDefinitionId().toString());
                }
                
            }
            return new ApplicationDeployInfo();
        }

        HashMap<JobDefinitionName,JobDefinition> mdbJobDefinitions = getJobDefinitionsFromDeployment(files, containerDeploymentInfo, properties);

        // ------------------------------------------------------------------
        // We need to keep the job definitions for later. We can only update 
        // our database when there is no rollback of the update. In case  
        // of a rollback we would not be able to restore the state before
        // the update
        // ------------------------------------------------------------------
        
        mUpdatedJobsHash.put(applicationName, mdbJobDefinitions);
        
        // ------------------------------------------------------------------
        // return some info to the deploy service about deployed jobs
        // ------------------------------------------------------------------

        Set<Map.Entry<JobDefinitionName, JobDefinition>> jobEntries = mdbJobDefinitions.entrySet();
        
        String[] jobNames = new String[jobEntries.size()];
        int i=0;
        for (Map.Entry<JobDefinitionName, JobDefinition> jobEntry : jobEntries) {
            JobDefinition def = jobEntry.getValue();
            jobNames[i++] = def.getJobDefinitionName().toString();
        }

        ApplicationDeployInfo appInfo = new ApplicationDeployInfo();
        appInfo.setDeployedComponentNames(jobNames);
        return appInfo;
    }

    public void notifyUpdatedComponents(String applicationName,
            Configuration configuration, Properties properties)
            throws WarningException {

        // event which is sent to all other nodes 

    }

    public void prepareUpdate(String applicationName)
            throws DeploymentException, WarningException {

        // nothing to be done here

    }

    public ApplicationDeployInfo commitUpdate(String applicationName)
                                                      throws WarningException {
        
        HashMap<JobDefinitionName,JobDefinition> jobDefinitionsToUpdate = 
                                    mUpdatedJobsHash.remove(applicationName);
        
        if (jobDefinitionsToUpdate == null) {
            // this application was of no interest to us
            return null;
        }
        
        // ----------------------------------------------------------------
        // update our database to match the newly deployed jobs
        // ----------------------------------------------------------------

        ArrayList<UpdateJobDefinition> updatedJobDefinitions = null;
        
        try {
            updatedJobDefinitions = environment.getJobRepository().updateRepository(applicationName, jobDefinitionsToUpdate);
        } catch (DuplicateJobDefinitionException dje) {
            
            category.logThrowableT(Severity.FATAL, location, "Application update request for application \"" + applicationName +
                    "\" was commited but job definition repository could not be updated. This means that the " +
                    "job repository is not consistent for this application.", dje);
            // make sure we do not raise any events for updated jobs
            //
            updatedJobDefinitions = new ArrayList<UpdateJobDefinition>();
        } catch (ConfigurationParserException cpe) {
        
            category.logThrowableT(Severity.FATAL, location, "Application update request for application \"" + applicationName +
                    "\" was commited but job definition repository could not be updated. This means that the " +
                    "job repository is not consistent for this application.", cpe);
            // make sure we do not raise any events for updated jobs
            //
            updatedJobDefinitions = new ArrayList<UpdateJobDefinition>();
        } catch (SQLException sql) {
            category.logThrowableT(Severity.FATAL, location, "Application update request for application \"" + applicationName +
                    "\" was commited but job definition repository could not be updated. This means that the " +
                    "job repository is not consistent for this application.", sql);
            // make sure we do not raise any events for updated jobs
            //
            updatedJobDefinitions = new ArrayList<UpdateJobDefinition>();
        }
        
        // ------------------------------------------------------------------
        // tell everyone about the newly deployed and/or updated jobs
        // ------------------------------------------------------------------
        
        raiseChangeEvents(updatedJobDefinitions);

        // null is ok here
        //
        return null;
    }
    
    private void raiseChangeEvents(ArrayList<UpdateJobDefinition> updatedJobDefinitions) {

        EventManager mgr = environment.getEventManager();
        for (UpdateJobDefinition udef : updatedJobDefinitions) {
            
            switch (udef.getUpdateType()) {
            case NEW_JOB_DEFINITION:
                mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_DEPLOYED, udef.getJobDefinition().getJobDefinitionName().toString());
                mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_DEPLOYED1, udef.getJobDefinition().getJobDefinitionId().toString());
                break;
            case CHANGED_JOB_DEFINITION:
                // undeploy events
                mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_UNDEPLOYED, udef.getOldJobDefinition().getJobDefinitionName().toString());
                mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_UNDEPLOYED1, udef.getOldJobDefinition().getJobDefinitionId().toString());
                // deploy events
                mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_DEPLOYED, udef.getJobDefinition().getJobDefinitionName().toString());
                mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_DEPLOYED1, udef.getJobDefinition().getJobDefinitionId().toString());
                break;
            case REMOVED_JOB_DEFINITION:
                // undeploy events
                mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_UNDEPLOYED, udef.getRemovedJobDefinition().getJobDefinitionName().toString());
                mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_UNDEPLOYED1, udef.getRemovedJobDefinition().getJobDefinitionId().toString());
                break;
            case UNCHANGED_JOB_DEFINITION:
                // nothing to do, just for reference
                break;
            default:
                // just for making is shatter if new update types are added
                throw new IllegalArgumentException("Update type \"" + udef.getUpdateType().toString() + "\" not known.");
            }
        }
    }

    public void rollbackUpdate(String applicationName,
            Configuration configuration, Properties properties)
            throws WarningException {
        
        // nothing to do here, we update the our database on commit

    }

    private void initContainerInfo(String serviceName) throws ServiceException {
        containerInfo.setName(CONTAINER_NAME);
        containerInfo.setModuleName(CONTAINER_NAME);
        containerInfo.setJ2EEContainer(false);
        
        // name of service which registers container
        containerInfo.setServiceName(serviceName);
        containerInfo.setPriority(APP_PRIORITY);
        // enable parallel deployment support
        containerInfo.setSupportingParallelism(true);
    }
    
    public void notifyRemove(String applicationName) throws WarningException {
        // ignore
    }
    
    public void remove(String applicationName, ConfigurationHandler operationHandler, Configuration appConfiguration) throws DeploymentException, WarningException {
    
        // ------------------------------------------------------------
        // remove all jobs for this application from the database
        // ------------------------------------------------------------

        if (location.beDebug()) {
            location.debugT("Application \"" + applicationName +
                    "\" is being undeployed. Removing all jobs which are part " +
                    "of this application.");
        }

        ArrayList<JobDefinition> removedDefinitions = null;
        try {
            removedDefinitions = environment.getJobRepository().deactivateJobDefinitions(applicationName);
        } catch (SQLException sql) {
            category.logThrowableT(Severity.FATAL, location, "Application \"" + applicationName +
                    "\" undeployed but error deactivating the corresponding job definitions from" +
                    " the database. The job definition repository is inconsistent.", sql);
        }
        
        if (removedDefinitions != null) {
            EventManager mgr = environment.getEventManager();
            for (JobDefinition def : removedDefinitions) {
                mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_UNDEPLOYED, def.getJobDefinitionName().toString());
                mgr.raiseEvent(Event.EVENT_JOB_DEFINITION_UNDEPLOYED1, def.getJobDefinitionId().toString());
            }
            
        }
    }

    public void commitRemove(String applicationName) throws WarningException {

        // deactivated jobs during remove phase, nothing to do
        // here
    }

    // ----------------------------------------------------------------------
    // We do not implement these methods below
    // ----------------------------------------------------------------------

    public ApplicationDeployInfo makeSingleFileUpdate(
            FileUpdateInfo[] fileUpdateInfos,
            ContainerDeploymentInfo containerDeploymentInfo,
            Properties properties) throws DeploymentException {

        return null;
    }
    
    public ApplicationStartInfo makeStartInitially(ContainerStartInfo csInfo) throws DeploymentException {
        // not needed
        return null;
    }

    public void remove(String applicationName) throws DeploymentException,
                                                      WarningException {

        // not called as we implement the ContainerInterfaceExtension interface
        // (see  public void remove(String applicationName, 
        //                          ConfigurationHandler operationHandler, 
        //                          Configuration appConfiguration) 
        //                     throws DeploymentException, WarningException 

    }

    public void downloadApplicationFiles(String applicationName,
            Configuration configuration) throws DeploymentException,
            WarningException {

    }

    public void prepareStart(String applicationName, Configuration configuration)
            throws DeploymentException, WarningException {

    }

    public void commitStart(String applicationName) throws WarningException {

    }

    public void rollbackStart(String applicationName) throws WarningException {

    }

    public void prepareStop(String applicationName, Configuration configuration)
            throws DeploymentException, WarningException {

    }

    public void commitStop(String applicationName) throws WarningException {

    }

    public void rollbackStop(String applicationName) throws WarningException {

    }

    public void notifyRuntimeChanges(String applicationName,
            Configuration configuration) throws WarningException {

    }

    public void prepareRuntimeChanges(String applicationName)
            throws DeploymentException, WarningException {

    }

    public ApplicationDeployInfo commitRuntimeChanges(String applicationName)
            throws WarningException {
        return null;
    }

    public void rollbackRuntimeChanges(String applicationName)
            throws WarningException {

    }

    public File[] getClientJar(String applicationName) {
        return new File[0];
    }

    public void addProgressListener(ProgressListener progressListener) {

    }

    public void removeProgressListener(ProgressListener progressListener) {

    }

    public boolean needStopOnSingleFileUpdate(FileUpdateInfo[] fileUpdateInfos,
            ContainerDeploymentInfo containerDeploymentInfo,
            Properties properties) throws DeploymentException, WarningException {
        return false;
    }

    public void notifySingleFileUpdate(String applicationName,
            Configuration configuration, Properties properties)
            throws WarningException {

    }

    public void prepareSingleFileUpdate(String applicationName)
            throws DeploymentException, WarningException {

    }

    public ApplicationDeployInfo commitSingleFileUpdate(String applicationName)
            throws WarningException {
        return null;
    }

    public void rollbackSingleFileUpdate(String applicationName,
            Configuration configuration) throws WarningException {

    }

    public void applicationStatusChanged(String applicationName, byte b) {

    }

    public String[] getResourcesForTempLoader(String applicationName)
            throws DeploymentException {
        return new String[0];
    }

    public boolean acceptedAppInfoChange(String applicationName,
            AdditionalAppInfo additionalAppInfo) throws DeploymentException {
        return false;
    }

    public boolean needStopOnAppInfoChanged(String applicationName,
            AdditionalAppInfo additionalAppInfo) {
        return false;
    }

    public void makeAppInfoChange(String applicationName,
            AdditionalAppInfo additionalAppInfo, Configuration configuration)
            throws WarningException, DeploymentException {

    }

    public void appInfoChangedCommit(String applicationName)
            throws WarningException {

    }

    public void appInfoChangedRollback(String applicationName)
            throws WarningException {

    }

    public void notifyAppInfoChanged(String applicationName)
            throws WarningException {

    }

    public String getApplicationName(File file) throws DeploymentException {
        return null;
    }
    
    // ----------------------------------------------------------------------
    // private methods
    // ----------------------------------------------------------------------
    
    
    private void validateJobBean(MessageBean mb, String jobName, String applicationName)
            throws DeploymentException {

        // ------------------------------------------------------------------
        // validate destination type
        // ------------------------------------------------------------------

        try {
            String dest = mb.getDestinationType();
            if (!"javax.jms.Queue".equals(dest)) {
                // illegal job
                throw new DeploymentException(SchedulerContainerImpl.JOB_DEPLOY_FAILED, new Object[] {applicationName,
                        "Job \""
                                + jobName
                                + "\" of bean \""
                                + mb.getBeanName()
                                + "\" does not have the correct destination type. "
                                + "The destination for jobs must be \"javax.jms.Queue\" but is \""
                                + dest + "\" for this job."});

            }
        } catch (MissingOptionalDataException mde) {
            // illegal job
            throw new DeploymentException(SchedulerContainerImpl.JOB_DEPLOY_FAILED, new Object[] {applicationName,
                    "Job \"" + jobName + "\" of bean \""
                    + mb.getBeanName()
                    + "\" does not have a destination type. "
                    + "The destination for jobs must be \"javax.jms.Queue\"."});
        }

        // ------------------------------------------------------------------
        // validate connection factory name
        // ------------------------------------------------------------------

        try {
            String fac = mb.getConnectionFactoryName();
            if (!"JobQueueFactory".equals(fac)) {
                // illegal job
                throw new DeploymentException(SchedulerContainerImpl.JOB_DEPLOY_FAILED, new Object[] {applicationName,
                        "Job \""
                                + jobName
                                + "\" of bean \""
                                + mb.getBeanName()
                                + "\" does not have the correct connection factory name. "
                                + "The connection factory name for jobs must "
                                + "be \"JobQueueFactory\" but it \"" + fac
                                + "\" for this job."});

            }
        } catch (MissingOptionalDataException mde) {
            // illegal job
            throw new DeploymentException(SchedulerContainerImpl.JOB_DEPLOY_FAILED, new Object[] {applicationName,
                           "Job \""
                            + jobName
                            + "\" of bean \""
                            + mb.getBeanName()
                            + "\" does not have a connection factory name. "
                            + "The connection factory name for jobs must be \"JobQueueFactory\"."});
        }
    }

    private void validateApplicationName(HashMap<String, String> selectors,
            String expectedApplicationName) throws DeploymentException {

        String app = selectors.get(MessageSelectorParser.APPLICATION_NAME);
        if (app != null) {
            if (!expectedApplicationName.equals(app)) {

                throw new DeploymentException(SchedulerContainerImpl.JOB_DEPLOY_FAILED, new Object[] {expectedApplicationName,
                        "Application name in message selector for job \""
                                + selectors
                                        .get(MessageSelectorParser.JOB_DEFINITION)
                                + "\" must match the application name \""
                                + expectedApplicationName + "\" but is \""
                                + app + "\"."});

            }
        }
    }
    
    private HashMap<JobDefinitionName,JobDefinition> getJobDefinitionsFromDeployment(
                                        File[] files,
                                        ContainerDeploymentInfo containerDeploymentInfo,
                                        Properties properties)
                                                throws DeploymentException {

        String applicationName = containerDeploymentInfo.getApplicationName();

        if (location.beDebug()) {
            StringBuffer msg = new StringBuffer();
            msg.append("Checking new application \"" + applicationName +
                    "\" for jobs. It contains " +
                    files.length + " files: ");
            for (int i=0 ; i < files.length; i++) {
                msg.append(files[i].getName() + ", ");
            }
            location.debugT(msg.toString());
            
            location.debugT("Module configuration: " + containerDeploymentInfo.getModuleProvider().toString());
        }
        
        if (files == null || files.length == 0) {
            // No jar files contained in archive, we are not 
            // interested (why did we get this in the first place?
            //
            return null;
        }

        // ------------------------------------------------------------------
        // read job-definition.xml from all archives
        // ------------------------------------------------------------------

        HashMap<JobDefinitionName,JobDefinition> jobDefinitions;
        try {
            jobDefinitions = environment.getConfigurationParser().readJobDefinitions(files, applicationName);
        } catch (Exception e) {   

            throw new DeploymentException(SchedulerContainerImpl.JOB_DEPLOY_FAILED, new Object[] {applicationName, "Error parsing job meta data files."}, e);
        }
        
        if (jobDefinitions.size() == 0) {
            // this message should really pop up in the deploy view as this
            // was probably not the user's intention
            category.warningT(location, "Deploying job module in application \"" + applicationName + "\" but no jobs defined in it.");
            return new HashMap<JobDefinitionName,JobDefinition>();
        }

        
        // ------------------------------------------------------------------
        // Get all ejb meta data for this application module
        // ------------------------------------------------------------------
        
        Set<Bean> allBeans = new HashSet<Bean>();
        
        Map ejbContainerCache = (Map) containerDeploymentInfo.getCache("EJBContainer");
        if (ejbContainerCache != null) {
            
            for (File f : files) {
                Map m = (Map)ejbContainerCache.get(f.getAbsolutePath());
                if (m == null) {
                    continue;
                }
                Module module = (Module)m.get("EJB module");
                if (module == null) {
                    continue;
                }
                Set<Bean> beans = module.getBeans();
                allBeans.addAll(beans);
            }
        }
        
        // ------------------------------------------------------------------
        // Identify all job beans
        // ------------------------------------------------------------------
        
        HashMap<String,MessageBean> allJobBeans = new HashMap<String,MessageBean>();
        HashMap<String,HashMap<String,String>> allJobBeansMS = new HashMap<String,HashMap<String,String>>();
        ArrayList<MessageBean> errorJobBeanCandidates = new ArrayList<MessageBean>();
        
        for (Bean b : allBeans) {
            if (b.getBeanType() != BeanType.msg) {
                // not a message driven bean, not interested
                continue;
            }
            MessageBean mb = (MessageBean)b;
            MsgActivationConfig msgConfig = null;
            try {
                msgConfig = mb.getActivationConfiguration();
            } catch (MissingOptionalDataException m) {
                // messge driven ben but not a job
                continue;
            }

            Collection<MsgActivationConfigProperty> props = msgConfig.getActivationConfigProperties();
            HashMap <String,String> selectors = null;

            for (MsgActivationConfigProperty p : props) {
                if ("messageSelector".equals(p.getActivationConfigPropertyName())) {
                    
                    try {
                        selectors = MessageSelectorParser.parseMessageSelector(p.getActivationConfigPropertyValue(), mb.getBeanName());
                        break;
                    } catch (IllegalMessageSelector il) {
                        // either not a job or a faulty descriptor,
                        location.infoT(il.getMessage());
                        errorJobBeanCandidates.add(mb);
                        continue;
                    }
                }
            }
            
            if (selectors == null) {
                // not a job, continue
                continue;
            }

            String jobName = selectors.get(MessageSelectorParser.JOB_DEFINITION);
            
            if (allJobBeans.containsKey(jobName)) {

                throw new DeploymentException(SchedulerContainerImpl.JOB_DEPLOY_FAILED, new Object[] {applicationName,
                                          "Application contains at least two EJBs which refer to the same job name \"" + jobName + "\""});
            }
                
            // --------------------------------------------------
            // it is a job, make some validation
            // --------------------------------------------------

            validateJobBean(mb, jobName, applicationName);
            validateApplicationName(selectors, applicationName);
                                        
            allJobBeans.put(jobName, mb);
            allJobBeansMS.put(jobName, selectors);
        }
        
        // ------------------------------------------------------------------
        // Make sure there is exaclty one MDB for every job definition and 
        // vice versa (error validation)
        // ------------------------------------------------------------------

        HashMap<JobDefinitionName, JobDefinition> mdbJobDefinitions = new HashMap<JobDefinitionName, JobDefinition>();

        for (JobDefinitionName jdname : jobDefinitions.keySet()) {
            JobDefinition jd = jobDefinitions.get(jdname);
            
            MessageBean mb = allJobBeans.remove(jdname.getName());
            if (mb == null) {
       
                StringBuffer buf = new StringBuffer();        
                for (MessageBean mbt : errorJobBeanCandidates) {
                    if (buf.length() == 0) {
                        buf.append(mbt.getBeanName());
                    } else {
                        buf.append(", " + mbt.getBeanName());
                    }
                }
                if (buf.length() == 0) {
                    throw new DeploymentException(
                            SchedulerContainerImpl.JOB_DEPLOY_FAILED, 
                            new Object[] {applicationName,
                                    "There is no bean for job definition \"" + jdname.getName() +
                                    "\"."});
                } else {
                    throw new DeploymentException(SchedulerContainerImpl.JOB_DEPLOY_FAILED, 
                            new Object[] {applicationName,
                            "There is no bean for job definition \"" + jdname.getName() +
                            "\". The following beans have faulty message selectors which might be the reason for this problem: " + buf.toString()});
                }
            }
            
            HashMap<String,String> selectors = allJobBeansMS.get(jdname.getName());
            String appName = selectors.get(MessageSelectorParser.APPLICATION_NAME);

            MDBJobDefinition jDef = new MDBJobDefinition(jd);

            try {
                MsgActivationConfig msgConfig = mb.getActivationConfiguration();
                Collection<MsgActivationConfigProperty> props = msgConfig.getActivationConfigProperties();

                for (MsgActivationConfigProperty p : props) {
                    if ("messageSelector".equals(p.getActivationConfigPropertyName())) {
                        
                        jDef.setMessageSelector(p.getActivationConfigPropertyValue());
                    }
                }
            } catch (MissingOptionalDataException m) {
                // $JL-EXC$
                // can be ignored
            }

            try {
                jDef.setDisplayName(mb.getDisplayName());
            } catch (MissingOptionalDataException mde) {
                // $JL-EXC$
                // can be ignored
            }

            jDef.setEjbName(mb.getBeanName());
            try {
                jDef.setDestinationType(mb.getDestinationType());
            } catch (MissingOptionalDataException mde) {
                // $JL-EXC$
                // can be ignored
            }

            if (appName == null) {
                jDef.setVersion(MDBJobDefinition.JobVersion.v1);
            } else {
                jDef.setVersion(MDBJobDefinition.JobVersion.v2);
            }
            
            mdbJobDefinitions.put(jDef.getJobDefinition().getJobDefinitionName(), jDef.getJobDefinition());
        }
        
        // ------------------------------------------------------------------
        // check for job beans without job definiton object
        // ------------------------------------------------------------------

        if (allJobBeans.size() != 0) {
            
            StringBuffer buf = new StringBuffer();
            for (String name : allJobBeans.keySet()) {
                if (buf.length() == 0) {
                    buf.append(name);
                } else {
                    buf.append(", " + name);
                }
            }
            
            category.warningT(location, "The following mesage driven beans look like jobs but there is no job definition for it: " + buf.toString());
        }
        
        return mdbJobDefinitions;
    }
}
