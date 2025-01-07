/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.runtime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.TimeZone;

import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.db.JobQueryHandler;
import com.sap.engine.services.scheduler.runtime.event.EventManager;
import com.sap.engine.services.scheduler.runtime.logging.JobLoggingManager;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.runtime.AbstractIdentifier;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.EventConsumer;
import com.sap.scheduler.runtime.EventSubscriber;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobDefinitionName;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.scheduler.runtime.JobExecutor;
import com.sap.scheduler.runtime.JobExecutorException;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIllegalStateException;
import com.sap.scheduler.runtime.JobIterator;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobDefinitionException;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.ParameterValidationException;
import com.sap.scheduler.runtime.SchedulerAlreadyDefinedException;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.SchedulerRemoveException;
import com.sap.scheduler.runtime.SubscriberID;
import com.sap.scheduler.runtime.UserAccountException;
import com.sap.scheduler.runtime.SchedulerID;
import com.sap.scheduler.runtime.SchedulerLogRecordIterator;
import com.sap.scheduler.spi.JXBP;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.engine.services.scheduler.runtime.db.DBHandler;

/**
 * 
 * @author Dirk Marwinski
 *
 */
public class JobExecutionRuntimeImpl implements JobExecutionRuntime {

    /**
     * Initialization of the location for SAP logging.
     */
    private final static Location location = Location
            .getLocation(JobExecutionRuntimeImpl.class);

    /**
     * Initialization of the category for SAP logging.
     */
    private final static Category category = LoggingHelper.SYS_SERVER;

    private Environment mEnvironment;
    
    private DBHandler mDB;
    
    private Hashtable<SchedulerID, Long> mSchedulerLastAccessTable = null;
    private static final long SCHEDULER_LAST_ACCESS_UPDATE_PERIOD = 60000; // 60 sec
    
    
    public JobExecutionRuntimeImpl(Environment env) {
        mEnvironment = env;
        mDB = mEnvironment.getDBHandler();
        mSchedulerLastAccessTable = new Hashtable<SchedulerID, Long>(2); // size 2 should be sufficient
    }
    
    /**
     * @see JobExecutionRuntime#getSystemTimeZone()
     */
    public String getSystemTimeZone() {
        return TimeZone.getDefault().getID();
    }


    /**
     * @see JobExecutionRuntime#getJobDefinitions()
     */
    public JobDefinition[] getJobDefinitions() 
                                      throws SQLException {
        
        return mEnvironment.getJobDefinitionHandler().getJobDefinitions();   
    }

    /**
     * @see JobExecutionRuntime#getJobDefinitionByName(java.lang.String)
     */
    public JobDefinition getJobDefinitionByName(String jobName)
                                                throws SQLException {

        return mEnvironment.getJobDefinitionHandler().getJobDefinitionByName(jobName);
    }
    
    public JobDefinition getJobDefinitionByName(JobDefinitionName jobDefinitionName)
                                                                      throws SQLException {

        return mEnvironment.getJobDefinitionHandler().getJobDefinitionByName(jobDefinitionName);
    }

    /**
     * @see JobExecutionRuntime#getJobDefinitionById(byte[])
     */
    public JobDefinition getJobDefinitionById(JobDefinitionID id)
                                            throws SQLException {
     
        return mEnvironment.getJobDefinitionHandler().getJobDefinitionById(id);
    }

    
    public JobDefinition[] removeJobDefinitions(JobDefinitionID[] ids) 
                                                            throws SQLException {
                
        JobDefinition[] defs = new JobDefinition[ids.length];
        for ( int i=0; i < ids.length ; i++) {
            defs[i] = getJobDefinitionById(ids[i]);
        }
            
        ArrayList<JobDefinition> removeJobDefinitions = new ArrayList<JobDefinition>();
        
        for (JobDefinition def : defs) {
            
            if (def.getRemoveDate() == null) {
                // job is still deployed
                continue;
            }
                
            JobFilter jf = new JobFilter();
            jf.setJobDefinitionId(def.getJobDefinitionId());
            JobIterator it = getJobs(jf, null, 1);
            Job[] j = it.nextChunk();
            if (j.length == 0) {
                removeJobDefinitions.add(def);
            }
        }
        
        // remove job definitions
        //
        for (JobDefinition def : removeJobDefinitions) {
            mEnvironment.getJobDefinitionHandler().removeJobDefinition(def);
        }
        
        return removeJobDefinitions.toArray(new JobDefinition[removeJobDefinitions.size()]);
    }
    
    //------------------------------------------------------------------
    // Starting and stopping jobs
    //------------------------------------------------------------------

    /**
     *  @see JobExecutionRuntime#cancelJob(JobID)
     */
    public void cancelJob(JobID jobId) 
                           throws SQLException {

        mDB.cancelJob(jobId);
    }
    
    //------------------------------------------------------------------
    // Parent/Child Functionality
    //------------------------------------------------------------------

    /**
     * @see JobExecutionRuntime#getChildJobs(byte[])
     */
    public Job[] getChildJobs(JobID jobid) throws NoSuchJobException, SQLException {        
    	JobQueryHandler db = mEnvironment.getJobQueryHandler();         
        return db.getChildJobs(jobid); 
    }

    /**
     * @see JobExecutionRuntime#hasChildJobs(byte[])
     */
    public boolean hasChildJobs(JobID jobid) throws SQLException {
        JobQueryHandler db = mEnvironment.getJobQueryHandler();        
        return db.hasChildJobs(jobid);
    }

    /**
     * @see JobExecutionRuntime#haveChildJobs(byte[][])
     */
    public boolean[] haveChildJobs(JobID[] jobids) throws SQLException {
        JobQueryHandler db = mEnvironment.getJobQueryHandler();        
        boolean[] results = new boolean[jobids.length];
        
        for (int i=0; i<results.length; i++) {
            results[i] = db.hasChildJobs(jobids[i]);
        }
        return results;
    }

    //------------------------------------------------------------------
    // Maintaining runtime job information
    //------------------------------------------------------------------

    /**
     * @see JobExecutionRuntime#removeJob(JobID)
     */
    public void removeJob(JobID jobid) throws JobIllegalStateException, NoSuchJobException, SQLException {
        removeJob(jobid, false);
    }

    /**
     * @see JobExecutionRuntime#removeJob(JobID, boolean)
     */
    public void removeJob(JobID jobid, boolean force) throws NoSuchJobException, JobIllegalStateException, SQLException {
        JobQueryHandler db = mEnvironment.getJobQueryHandler();
        db.removeJob(jobid, force);
    }
    
    
    /**
     * @see JobExecutionRuntime#removeJobs(Job[])
     */
    public void removeJobs(Job[] jobs) throws SQLException {
        removeJobs(jobs, false);
    }

    /**
     * @see JobExecutionRuntime#removeJobs(JobID[])
     */
    public void removeJobs(JobID[] jobids) throws SQLException {
        removeJobs(jobids, false);
    }
    
    /**
     * @see JobExecutionRuntime#removeJobs(Job[], boolean)
     */
    public void removeJobs(Job[] jobs, boolean force) throws SQLException {        
        JobID[] jobIds = new JobID[jobs.length];
        for (int i = 0; i < jobs.length; i++) {
            jobIds[i] = jobs[i].getId();
        }
        
        removeJobs(jobIds, force);
    }    
    
    /**
     * non-interface method
     */
    public void removeJobs(JobID[] jobids, boolean force) throws SQLException {
        for (int i=0; i < jobids.length; i++) {
            try {
                removeJob(jobids[i], force);
            } catch (JobIllegalStateException e) {
                // $JL-EXC
                category.warningT(location, "Unable remove job with id '"+jobids[i].toString()+"'. "+
                        "Job should be in state COMPLETED, ERROR, UNKNOWN or CANCELLED but is most likely in state RUNNING.");
            } catch (NoSuchJobException e) {
                // $JL-EXC
                category.warningT(location, "Unable remove job with id '"+jobids[i].toString()+"', "+
                        "because it couldn't be found.");
            }
        }
    }


    /**
     * @see JobExecutionRuntime#getJob(java.lang.String)
     */
    public Job getJob(JobID jobid) 
                              throws SQLException {

        return JobQueryHandler.Instance().getJob(jobid);
    }

    /**
     * @see JobExecutionRuntime#getJobParameters(java.lang.String)
     */
    public JobParameter[] getJobParameters(JobID jobid)
                                           throws SQLException,
                                                  NoSuchJobException {

        return mDB.getJobParameters(jobid);
    }
    
    /**
     * @see JobExecutionRuntime#getJobs(java.lang.String[])
     */
    public Job[] getJobs(JobID[] jobid) 
                                     throws SQLException {
        
        Job[] result = new Job[jobid.length];
        for (int i=0; i < jobid.length; i++) {
            result[i] = JobQueryHandler.Instance().getJob(jobid[i]);
        }
        return result;
    }

    /**
     * Returns the job log in chunks as String
     */
    public LogIterator getJobLog(JobID jobid, LogIterator iter, int fetchSize) 
                                                     throws SQLException, 
                                                            NoSuchJobException {

        JobLoggingManager jm = mEnvironment.getJobLoggingManager();

        return jm.getLog(jobid, iter, fetchSize);
    }

    
    /**
     * Returns the job log in chunks as SchedulerLogRecord[]
     */
    public SchedulerLogRecordIterator getJobLogRecords(JobID jobid, SchedulerLogRecordIterator iter, int fetchSize) 
                                                     throws SQLException, 
                                                            NoSuchJobException {

        JobLoggingManager jm = mEnvironment.getJobLoggingManager();

        return jm.getLog(jobid, iter, fetchSize);
    }
    
    
    /**
     * Removes the job log
     */
    public void removeJobLog(JobID jobid) throws SQLException, NoSuchJobException {

        JobLoggingManager jm = mEnvironment.getJobLoggingManager();
        jm.deleteLog(jobid);
    }

    public JobStatus getJobStatus(JobID jobid) 
                                        throws SQLException {
        
        Job j = getJob(jobid);
        return j.getJobStatus();
    }
        
    public JobIterator getJobs(JobFilter filter, JobIterator iter, int fetchSize)
                                                                throws SQLException {
        
        return JobQueryHandler.Instance().getJobs(filter, iter, fetchSize);
    }
    

    //------------------------------------------------------------------
    // Events
    //------------------------------------------------------------------

    /**
     * @see JXBP#getEvents()
     */
    public Event[] getEvents(EventSubscriber es, int fetchSize) {
        return mEnvironment.getEventManager().getEvents(es, fetchSize);
    }

    /**
     * @see JXBP#clearEvents()
     */
    public void clearEvents(EventSubscriber es) {
        mEnvironment.getEventManager().clearEvents(es);
    }

    public EventSubscriber getEventSubscriberByID(AbstractIdentifier esId) throws SQLException {
        
        return mEnvironment.getEventManager().getPersistentEventSubscriberById(SubscriberID.parseID(esId.getBytes()));
    }
    
    /**
     * @see JobExecutionRuntime#setFilter(com.sap.scheduler.spi.SchedulerDefinition, java.lang.String[])
     */
    public void setFilter(SchedulerDefinition def, String[] eventType) throws SQLException {        
        EventManager mgr = mEnvironment.getEventManager();
        mgr.setFilter(def.getSubscriberId(), mEnvironment.getEventManager().cleanupEventTypesForJXBP(eventType));        
    }

    /**
     * @see JXBPInternal#getJXBPRuntimeEvents()
     */
    public String[] getJXBPRuntimeEventTypes() {
        
        return Event.JXBP_RUNTIME_EVENT_TYPES;
    }
    
    /**
     * @see JobExecutionRuntime#getSchedulerForUser(String)
     */
    public SchedulerDefinition getSchedulerForUser(String principal) 
                                                        throws SQLException {
     
        return mEnvironment.getSchedulerManager().getSchedulerForUser(principal);
    }

    /**
     * @see JobExecutionRuntime#setVendorData(com.sap.scheduler.spi.SchedulerDefinition, byte[][], java.lang.String) 
     */
    public void setVendorData(SchedulerDefinition def, JobID[] jobIds, String data) 
                                                                   throws SQLException {
        
        DBHandler db = mEnvironment.getDBHandler();
        db.setVendorData(jobIds, data);
        
    }
    
    /**
     * @see JobExecutionRuntime#getVendorData(byte[][])
     */
    public String[] getVendorData(JobID[] jobIds)
                                         throws SQLException
    {
        DBHandler db = mEnvironment.getDBHandler();
        return db.getVendorData(jobIds);
    }

    public SchedulerDefinition addScheduler(String name, 
                                            String user, 
                                            String userPassword,
                                            String description,
                                            long inactivityGracePeriod,
                                            String[] events)
                                                throws SQLException,
                                                       UserAccountException,
                                                       SchedulerAlreadyDefinedException {
    
        return mEnvironment.getSchedulerManager().addScheduler(name, user, userPassword, description, inactivityGracePeriod, events);
    }
    
    public void removeScheduler(SchedulerDefinition def) throws SchedulerRemoveException, SQLException {
    	mEnvironment.getSchedulerManager().removeScheduler(def);
    	
      // cleanup the last access table
      mSchedulerLastAccessTable.remove(def.getId());
    }
    
    
    public SchedulerDefinition[] getAllSchedulers()
                                        throws SQLException {
        
        return mEnvironment.getSchedulerManager().getAllSchedulers();
    }
    
    public void updateTimestamp(SchedulerID id) {
      Long ts = null;
      long currentTime = System.currentTimeMillis();
      
      // update the last access timestamp when it has NOT been updated in the last 
      // minute or it IS null  
      if ( ( (ts = mSchedulerLastAccessTable.get(id)) != null && ((currentTime - ts.longValue()) >= SCHEDULER_LAST_ACCESS_UPDATE_PERIOD) ) 
          || mSchedulerLastAccessTable.get(id) == null ) {
      
        mEnvironment.getSchedulerManager().updateTimestamp(id);
        // update the last access for this scheduler
        mSchedulerLastAccessTable.put(id, new Long(currentTime));
      }     
    }

    public void deactivateScheduler(SchedulerID id) 
                                       throws SQLException {
        
        mEnvironment.getSchedulerManager().deactivateScheduler(id);
        
        // cleanup the last access table
        mSchedulerLastAccessTable.remove(id);
    }

    
    /**
     * Return the userid which is associated with this context
     * 
     * @return user id
     */
    private String getCurrentUser() {
        
            Object obj = mEnvironment.getServiceContext().getCoreContext()
                       .getThreadSystem().getThreadContext().getContextObject("security");

            SecurityContextObject securityContext = (SecurityContextObject)obj;

            String callerName = securityContext.getSession().getPrincipal().getName();

            return callerName;

    }
    
    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, JobID parentId, SchedulerID schedulerId, SchedulerTaskID schedTaskId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException { 
        return executeJobForAllMethods(jobDefId, jobParameters, null, parentId, schedulerId, getCurrentUser(), schedTaskId, null, null);
    }

    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, JobID parentId, SchedulerID schedulerId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        return executeJobForAllMethods(jobDefId, jobParameters, null, parentId, schedulerId, getCurrentUser(), null, null, null);
    }
    
    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, SchedulerID schedulerId, SchedulerTaskID schedTaskId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        return executeJobForAllMethods(jobDefId, jobParameters, retentionPeriod, parentId, schedulerId, getCurrentUser(), schedTaskId, null, null);
    }
    
    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, SchedulerID schedulerId)
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        return executeJobForAllMethods(jobDefId, jobParameters, retentionPeriod, parentId, schedulerId, getCurrentUser(), null, null, null);
    }
    
    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, JobID parentId, SchedulerTaskID schedTaskId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        return executeJobForAllMethods(jobDefId, jobParameters, null, parentId, getBuiltinSchedulerID(jobDefId), getCurrentUser(), schedTaskId, null, null);
    }    

    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, JobID parentId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        return executeJobForAllMethods(jobDefId, jobParameters, null, parentId, getBuiltinSchedulerID(jobDefId), getCurrentUser(), null, null, null);
    }
    
    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, SchedulerID schedulerId, String runAsUser, SchedulerTaskID schedTaskId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        return executeJobForAllMethods(jobDefId, jobParameters, retentionPeriod, parentId, schedulerId, runAsUser, schedTaskId, null, null);
    }

    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, SchedulerID schedulerId, String runAsUser) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        return executeJobForAllMethods(jobDefId, jobParameters, retentionPeriod, parentId, schedulerId, runAsUser, null, null, null);
    }   
     
    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, SchedulerTaskID schedTaskId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        return executeJobForAllMethods(jobDefId, jobParameters, retentionPeriod, parentId, getBuiltinSchedulerID(jobDefId), getCurrentUser(), schedTaskId, null, null);
    }

    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        return executeJobForAllMethods(jobDefId, jobParameters, retentionPeriod, parentId, getBuiltinSchedulerID(jobDefId), getCurrentUser(), null, null, null);
    }
    
    public JobID executeJobJXBP(JobDefinitionID jobDefId,
            JobParameter[] jobParameters,
            Integer retentionPeriod,
            JobID parentId,
            SchedulerID schedulerId,
            String vendorData)                                      
                     throws JobExecutorException,
                            ParameterValidationException,
                            NoSuchJobDefinitionException {
        
        return executeJobForAllMethods(jobDefId, jobParameters, retentionPeriod, parentId, schedulerId, getCurrentUser(), null, vendorData, null);
    }
    
    public JobID executeJob(JobDefinitionID jobDefId,
            JobParameter[] jobParameters,
            JobID parentId,
            SchedulerID schedulerId,
            JobID jobId) 
                        throws JobExecutorException,
                               ParameterValidationException,
                               NoSuchJobDefinitionException {

        return executeJobForAllMethods(jobDefId, jobParameters, null, parentId, schedulerId, getCurrentUser(), null, null, jobId);
    }

    private JobID executeJobForAllMethods(JobDefinitionID jobDefId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, 
                SchedulerID schedulerId, String runAsUser, SchedulerTaskID schedTaskId, String vendorData, JobID jobId) throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        JobExecutor exe = mEnvironment.getJobExecutor();
        return exe.executeJob(jobDefId, jobParameters, retentionPeriod, parentId, schedulerId, runAsUser, schedTaskId, vendorData, jobId);        
    }
    
    private SchedulerID getBuiltinSchedulerID(JobDefinitionID jobDefId) throws JobExecutorException {
        SchedulerDefinition schedDef;
        try {
            schedDef = getBuiltinScheduler();
        } catch (SQLException sql) {
            throw new JobExecutorException("Unable to find builtin scheduler. Job \"" + jobDefId.toString() + "\" not started.", sql);
        }
        return schedDef.getId();
    }

    public SchedulerDefinition getSchedulerByName(String name)
                                                         throws SQLException {

        return mEnvironment.getSchedulerManager().getSchedulerByName(name);
    }
    
    
    public SchedulerDefinition getSchedulerById(SchedulerID id) throws SQLException {
        return mEnvironment.getSchedulerManager().getSchedulerById(id);
    }
    
    public SchedulerDefinition getBuiltinScheduler()
                                              throws SQLException {
        
        return mEnvironment.getSchedulerManager().getBuiltinScheduler();
    }
    
    public void setReturnCode(JobID jobId, short returnCode)
                                                  throws SQLException {

        mDB.setReturnCode(jobId, returnCode);
    }
    
    /**
     * @see JobExecutionRuntime#setJobParameter(JobID, JobParameter)
     */
    public void setJobParameter(JobID jobid, JobParameter param)
                                            throws IllegalArgumentException,
                                                   SQLException {

        if (param.getJobParameterDefinition().isIn()) {
            throw new IllegalArgumentException("Parameter " + param.getName() + 
                                               " is an IN parameter and cannot " +
                                               "be set.");
        }
        
        Job j = getJob(jobid);
        if (j == null) {
            throw new IllegalArgumentException("A job with id \"" + 
                                               jobid.toString() +
                                               "\" does not exist. Cannot set " +
                                               "parameter.");
        }

        JobDefinition def = getJobDefinitionById(j.getJobDefinitionId());
        JobParameterDefinition cpdef = def.getParameter(param.getJobParameterDefinition().getName());

        if (cpdef==null) {
            throw new IllegalArgumentException(
                          "There is no parameter \"" + 
                          param.getJobParameterDefinition().getName() +
                          "\" for job with id \"" + jobid.toString() + "\".");
        }
        
        if (!cpdef.equals(param.getJobParameterDefinition())) {
            throw new IllegalArgumentException(
                          "The type of the specified parameter with name \"" +
                          param.getJobParameterDefinition().getName() + 
                          "\" and type \"" + 
                          param.getJobParameterDefinition().getType().toString() +
                          "\" does not match the required type \"" +
                          cpdef.getType().toString() + 
                          "\".");
        }
        
        // The parameter provided is correct and can be written
        //
        mDB.updateParameter(param, jobid);
    }

    public JobParameterDefinition[] getJobParameterDefinition(String jobDefinitionName)
                                                                              throws SQLException {
        
        JobDefinition def = mEnvironment.getJobDefinitionHandler().getJobDefinitionByName(jobDefinitionName);
        return def.getParameters();
    }
    
    public JobParameterDefinition[] getJobParameterDefinition(JobDefinitionID id)
                                                                         throws SQLException {

        JobDefinition def = mEnvironment.getJobDefinitionHandler().getJobDefinitionById(id);
        return def.getParameters();
    }

    public void verifyParameters(JobDefinitionID jobDefinitionId, 
                                  JobParameter[] parameters)
                                          throws ParameterValidationException, 
                                                 NoSuchJobDefinitionException,
                                                 SQLException {
         
         JobParameterVerifyer.verifyParameters(jobDefinitionId, parameters, mEnvironment);
     }
     
    public Event waitForEvent(EventSubscriber sub) {
        return mEnvironment.getEventManager().waitForEvent(sub);
    }

    public Event waitForEvent(EventSubscriber sub, long timeout) {
        return mEnvironment.getEventManager().waitForEvent(sub, timeout);
    }
    
    public void raiseEvent(String type, String parameter, String additionalParameter, Date raisedDate, AbstractIdentifier raisedByDetails) {
        mEnvironment.getEventManager().raiseEvent(type, parameter, additionalParameter, raisedDate, raisedByDetails);
    }

    public boolean isJobCancelled(JobID jobId) 
                                        throws SQLException {
        
        return mDB.isJobCancelled(jobId);
    }

    public void registerEventSubscriber(EventSubscriber sub) throws SQLException {
        mEnvironment.getEventManager().registerEventSubscriber(sub);
    }
    
    public void unregisterEventSubscriber(EventConsumer consumer) throws SQLException {
        EventSubscriber sub = mEnvironment.getEventManager().getEventSubscriber(consumer);
        mEnvironment.getEventManager().unregisterEventSubscriber(sub);
    }
    
    
    /**
     * Return the event types. The mapping is as folows:
     * key: event-name
     * value: event-description
     * 
     * @return the Properties with the event names and descriptions
     */
    public Properties getEventTypes() {
        Properties props = new Properties();
        
        for (int i = 0; i < Event.JXBP_RUNTIME_EVENT_TYPES.length; i++) {
            props.put(Event.JXBP_RUNTIME_EVENT_TYPES[i], Event.JXBP_RUNTIME_EVENT_TYPES_DESC[i]);
        }
        
        return props;
    }

    public int updateEndedNullValues() 
                               throws SQLException {
        
        return mEnvironment.getJobQueryHandler().updateEndedNullValues();
    }

    
}
