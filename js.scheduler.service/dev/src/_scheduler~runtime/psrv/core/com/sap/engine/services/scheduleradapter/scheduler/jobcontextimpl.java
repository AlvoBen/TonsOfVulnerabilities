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
package com.sap.engine.services.scheduleradapter.scheduler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Handler;

import com.sap.scheduler.runtime.*;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduler.runtime.logging.SAPLoggingWrapperHandler;

/**
 * Simple implementation for the JobContext interface.
 *
 * @author Dirk Marwinski
 */
public class JobContextImpl implements JobContext {

	private final static Location location = Location.getLocation(JobContextImpl.class);

	/**
     * Initialization of the category for SAP logging.
     */
    private final static Category category = LoggingHelper.SYS_SERVER;

    /**
     * Parameters which have been passed to this job
     */
    private HashMap mParameters;
    
    /**
     * A reference to the internal job execution runtime
     */
    private JobExecutionRuntime mJobExecutionRuntime;
    
    /**
     * id of this job
     */
    private JobID mJobId;

    /**
     * registered transient event subscriber for this job
     */
    private EventSubscriber mEventSubscriber;

    /**
     * id of the scheduler who requested this job
     */
    private SchedulerID mSchedulerId;
    
    /**
     * set if the job is supposed to go into state ERROR when finished
     */
    private boolean failureRequest = false;
    
    /**
     * Return code set for this job
     */
    private short mReturnCode=0;
    
    /**
     * This set stores the ids of currently running child jobs
     */
    private ConcurrentHashMap<JobID,Object> mRunningChildJobs;
    
    /**
     * Dummy object to store in mRunningChildJobs map.
     */
    private Object DUMMY_OBJECT = new Object();
    
    /**
     * This set stores the ids of all child jobs.
     */
    private Set mAllChildJobs;
    
    /**
     * JDK logger created for this job
     */
    private Logger mJDKLogger = null;

    /**
     * SAP logging location for this job
     */
    private Location mJobLogLocation;
    
    /**
     * SAP logging category for this job
     */
    private Category mJobLogCategory;
    
    private Environment mEnvironment;

    public JobContextImpl(Environment env,
                          Job job, 
                          Location jobLogLocation, 
                          Category jobLogCategory, 
                          JobParameter[] params, 
                          JobExecutionRuntime jert) {

        mEnvironment = env;
        
        mJobLogLocation = jobLogLocation;
        mJobLogCategory = jobLogCategory;
        
        mJobExecutionRuntime = jert;
        mJobId = job.getId();
        mSchedulerId = job.getScheduler();

        mRunningChildJobs = new ConcurrentHashMap<JobID,Object>();
        mAllChildJobs = new HashSet();
        
        mParameters = new HashMap();
        for (int i=0; i < params.length; i++) {
            mParameters.put(params[i].getName(), params[i]);
        }
    }

    /**
     * @see com.sap.scheduler.runtime.JobContext#getJob()
     */
    public Job getJob() 
                 throws SchedulerRuntimeException {
        try {
            return mJobExecutionRuntime.getJob(mJobId);
        } catch (SQLException sql) {
            throw new SchedulerRuntimeException("Error retrieving the job object.",sql);
        }
    }

    /**
     * @see com.sap.scheduler.runtime.JobContext#getChildJobById(byte[])
     */
    public Job getChildJobById(JobID jobId)
                              throws SchedulerRuntimeException {

        
        if (!mAllChildJobs.contains(jobId)) {
            // not a child job, cannot be accessed from this job
            return null;
        }
        
        try {
            return mJobExecutionRuntime.getJob(jobId);
        } catch (SQLException se) {
            throw new SchedulerRuntimeException("Unable to retrieve job information for job " + jobId + ".",se);
        }
    }
    
    public JobDefinition getJobDefinition(String name) {
        try {
            return mJobExecutionRuntime.getJobDefinitionByName(name);
        } catch (SQLException sql) {
            throw new SchedulerRuntimeException("Unable to retrieve job definition with name \"" + name + "\".");
        }
    }
    
    /**
     * Get a Logger.
     *
     * @return a logger to write log messages to.
     */
    public Logger getLogger() {
        
        if (mJDKLogger == null) {
            mJDKLogger = mEnvironment.getJobLoggingManager().getLogger(mJobLogCategory, mJobLogLocation);            
        }
        return mJDKLogger;
    }
    
    public void returnLogger() {
        if (mJDKLogger != null) {
            mEnvironment.getJobLoggingManager().returnLogger(mJDKLogger);
        }
    }
    
    public Category getCategory() {
        return mJobLogCategory;
    }
    
    public Location getLocation() {
        return mJobLogLocation;
    }
     
    /**
     * Set the job return code.
     *  
     * @param returnCode the value for the return code.
     */
    public void setReturnCode(short returnCode) 
                                           throws SchedulerRuntimeException {
        
        mReturnCode = returnCode;
        try {
            mJobExecutionRuntime.setReturnCode(mJobId, returnCode);
        } catch (NoSuchJobException nse) {
        	// this should never happen
        	category.errorT(location, "Currently running job \"" + mJobId.toString() + "\" does not exist anymore.");
        	throw new SchedulerRuntimeException("Currently running job \"" + mJobId.toString() + "\" does not exist anymore.");
        } catch (SQLException sql) {
            throw new SchedulerRuntimeException("Unable to set return value for job \"" + mJobId.toString() + "\".", sql);
        }
    }
   
    /**
     * @see JobContext#getJobParameter(java.lang.String)
     */
    public JobParameter getJobParameter(String name) {
    	
        JobParameter p = (JobParameter)mParameters.get(name);
        if (p == null) {
            throw new IllegalArgumentException("There is no parameter with name \"" + name + "\".");
        }
        return p;
    }

    /**
     * @see JobContext#setJobParameter(com.sap.scheduler.runtime.JobParameter)
     */
    public void setJobParameter(JobParameter param)
                                             throws IllegalArgumentException,
                                                    SchedulerRuntimeException {

        mParameters.put(param.getName(), param);

        try {
            mJobExecutionRuntime.setJobParameter(mJobId, param);
        } catch (NoSuchJobException nse) {
        	// this should never happen
        	category.errorT(location, "Currently running job \"" + mJobId.toString() + "\" does not exist anymore.");
        	throw new SchedulerRuntimeException("Currently running job \"" + mJobId.toString() + "\" does not exist anymore.");
        } catch (SQLException sql) {
            throw new SchedulerRuntimeException("Unable to set parameter \"" + param.getName() + "\" due to a database error.", sql);
        }
    }
    
    /**
     * @see com.sap.scheduler.runtime.JobContext#setJobParameterGeneric(java.lang.String, java.lang.Object)
     */
    public void setJobParameterGeneric(String name, Object value) 
                                                    throws IllegalArgumentException, 
                                                    ClassCastException,
                                                    SchedulerRuntimeException {
    
        JobParameter p = (JobParameter)mParameters.get(name);
        if (p == null) {
            throw new IllegalArgumentException("There is no parameter with name \"" + name + "\".");
        }

        if (p.getJobParameterDefinition().isIn()) {
            throw new IllegalArgumentException("Parameter \"" + name + "\" is an IN parameter and cannot be set.");
        }
        
        // TODO need to validate parameter type...
        
        p.setValue(value);
        mParameters.put(name, p);
        
        try {
            mJobExecutionRuntime.setJobParameter(mJobId, p);
        } catch (NoSuchJobException nse) {
        	// this should never happen
        	category.errorT(location, "Currently running job \"" + mJobId.toString() + "\" does not exist anymore.");
        	throw new SchedulerRuntimeException("Currently running job \"" + mJobId.toString() + "\" does not exist anymore.");
        } catch (SQLException sql) {
            throw new SchedulerRuntimeException("Unable to set parameter.", sql);
        }
    }
    
    /**
     * @see JobContext#getJobParameterGeneric(java.lang.String)
     */    
    public Object getJobParameterGeneric(String name) {

        // TODO IllegalArgument
        return ((JobParameter)mParameters.get(name)).getValue();
    }
     
    
    public JobParameterDefinition[] getJobParameterDefinitions(String jobDefinitionName)
                                                                throws SchedulerRuntimeException {
    
        try {
            return mJobExecutionRuntime.getJobParameterDefinition(jobDefinitionName);
        } catch (NoSuchJobDefinitionException nse) {
        	// this is a bit unfortunate here as we lose the exception. This is a bug in
        	// the public api
        	throw new SchedulerRuntimeException("Job definition \"" + jobDefinitionName + "\" does not exist.");
        } catch (SQLException sql) {
            throw new SchedulerRuntimeException("Unable read parameter definitions.", sql);
        }
    }

    public JobParameterDefinition getJobParameterDefinition(String jobDefinitionName,  
                                                            String parameterName)
                                                                 throws SchedulerRuntimeException {
    
        JobParameterDefinition[] def = getJobParameterDefinitions(jobDefinitionName);
        for (int i=0; i < def.length; i++) {
            if (def[i].getName().equals(parameterName)) {
                return def[i];
            }
        }
        return null;
    }
    
    public void setEventSubscriber(EventSubscriber sub) {
        mEventSubscriber = sub;
    }
    
    public EventSubscriber getEventSubscriber() {
        return mEventSubscriber;
    }
    
    /**
     * @see JobContext#isCancelled()
     */
    public boolean isCancelled() 
                            throws SchedulerRuntimeException {
        
        try {
            return mJobExecutionRuntime.isJobCancelled(mJobId);
        } catch (SQLException sql) {
            throw new SchedulerRuntimeException("Cannot check whether this job has been cancelled.", sql);
        }
    }

    
    public JobID executeJob(String jobDefinitionName) 
                                          throws ParameterValidationException,
                                                 NoSuchJobDefinitionException,
                                                 SchedulerRuntimeException {

        JobDefinition def = getJobDefinition(jobDefinitionName);
        if (def == null) {
            throw new NoSuchJobDefinitionException("Job \"" + jobDefinitionName + "\" does not exist.");
        } else if (def.getRemoveDate() != null) {
            throw new NoSuchJobDefinitionException("Job \"" + jobDefinitionName + "\" has been removed.");
        }
        
        return executeJob(def.getJobDefinitionId());
    }

    public JobID executeJob(String jobDefinitionName, JobParameter[] parameters) 
                                              throws ParameterValidationException,
                                                     NoSuchJobDefinitionException,
                                                     SchedulerRuntimeException {

        JobDefinition def = getJobDefinition(jobDefinitionName);
        if (def == null) {
            throw new NoSuchJobDefinitionException("Job \"" + jobDefinitionName + "\" does not exist.");
        } else if (def.getRemoveDate() != null) {
            throw new NoSuchJobDefinitionException("Job \"" + jobDefinitionName + "\" has been removed.");
        }
        
        return executeJob(def.getJobDefinitionId(), parameters);
    }

    /**
     */
    public JobID executeJob(JobDefinitionID jobDefId)
                                 throws ParameterValidationException,
                                        NoSuchJobDefinitionException,
                                        SchedulerRuntimeException {
        
        return executeJob(jobDefId, new JobParameter[0]);
    }

    /**
      * @see com.sap.scheduler.runtime.JobContext#submitJob(java.lang.String, com.sap.scheduler.runtime.JobParameter[])
      */
    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] parameters)
                                                            throws ParameterValidationException,
                                                                   NoSuchJobDefinitionException,
                                                                   SchedulerRuntimeException {
        JobID jobid = JobID.newID();
        boolean success = false;
        try {
            mAllChildJobs.add(jobid);
            mRunningChildJobs.put(jobid,DUMMY_OBJECT);
            mJobExecutionRuntime.executeJob(jobDefId, parameters, mJobId, mSchedulerId,jobid);
            success = true;
            return jobid;
        } catch (JobExecutorException jee) {
            throw new SchedulerRuntimeException(jee.getMessage(), jee);
        } finally {
            if (!success) {
                mAllChildJobs.remove(jobid);
                mRunningChildJobs.remove(jobid);
            }
        }
    }

    /**
     * @see com.sap.scheduler.runtime.JobContext#waitForChildJobs()
     */ 
    public void waitForChildJobs() {
        
        if (mRunningChildJobs.isEmpty()) {
            return;
        }
        
        while (true) {
            Event e = mJobExecutionRuntime.waitForEvent(getEventSubscriber());
            
            if (e.getType().equals(Event.EVENT_JOB_FINISHED)
                    || e.getType().equals(Event.EVENT_JOB_CANCELLED)) {

                JobID jobId = JobID.parseID(e.getParameter());
                mRunningChildJobs.remove(jobId);
                if (mRunningChildJobs.isEmpty()) {
                    // all jobs finished
                    //
                    return;
                }
            }
        }        
    }

    /**
     * @see JobContext#waitForChildJobs(long)
     */ 
    public boolean waitForChildJobs(long timeout)
    {
        
        long startWait = System.currentTimeMillis();
        long endWait = startWait + timeout;

        if (mRunningChildJobs.isEmpty()) {
            return true;
        }
        
        while (true) {
            long currentTime = System.currentTimeMillis();
            long timeToWait = endWait - currentTime;
            if (timeToWait < 0) {
                // wait time has expired but there are still jobs to wait 
                // for
                return false;
            }
            Event e = mJobExecutionRuntime.waitForEvent(getEventSubscriber(), timeToWait);
            if (e==null) {
                continue;
            }

            if (e.getType().equals(Event.EVENT_JOB_FINISHED)
                    || e.getType().equals(Event.EVENT_JOB_CANCELLED)) {

                JobID jobId = JobID.parseID(e.getParameter());
                mRunningChildJobs.remove(jobId);
                if (mRunningChildJobs.isEmpty()) {
                    // all jobs finished
                    //
                    return true;
                }
            }
        }        
    }
    
    /**
     * @see JobContext#waitForChildJob(byte[])
     */
    public void waitForChildJob(JobID jobid)
    {
        if (!mRunningChildJobs.containsKey(jobid)) {
            // nothing to do, this is either not a child job
            // or it has already finished
            return;
        }
        
        while (true) {

            Event e = mJobExecutionRuntime.waitForEvent(getEventSubscriber());
            
            if (e.getType().equals(Event.EVENT_JOB_FINISHED)
                    || e.getType().equals(Event.EVENT_JOB_CANCELLED)) {

                JobID fjobId = JobID.parseID(e.getParameter());    
                mRunningChildJobs.remove(fjobId);
                if (jobid.equals(fjobId)) {
                    return;
                }
            }
        }
    }

    /**
     * @see JobContext#waitForChildJobs(byte[][])
     */
    public void waitForChildJobs(JobID[] ids) {
        
        HashSet jobs = new HashSet();

        // sanity check, do all child jobs exist ?
        // build jobs set, ignore doubles and non-existing
        //
        for (int i=0; i < ids.length; i++) {
            if (mRunningChildJobs.containsKey(ids[i])) {
                jobs.add(ids[i]);
            }
        }        
        
        while (true) {

            Event e = mJobExecutionRuntime.waitForEvent(getEventSubscriber());
            
            if (e.getType().equals(Event.EVENT_JOB_FINISHED)
                    || e.getType().equals(Event.EVENT_JOB_CANCELLED)) {

                JobID fjobId = JobID.parseID(e.getParameter());    
                mRunningChildJobs.remove(fjobId);
                jobs.remove(fjobId);

                if (jobs.isEmpty()) {
                    // all jobs finished
                    //
                    return;
                }
            }
        }
    }

    /**
     * @see JobContext#waitForChildJobs(com.sap.scheduler.runtime.JobID[], long)
     */
    public boolean waitForChildJobs(JobID[] ids, long timeout) {
        
        long startWait = System.currentTimeMillis();
        long endWait = startWait + timeout;
        
        HashSet jobs = new HashSet();

        // sanity check, do all child jobs exist ?
        // build jobs set, ignore doubles and missing
        // jobs
        for (int i=0; i < ids.length; i++) {
            if (mRunningChildJobs.containsKey(ids[i])) {
                jobs.add(ids[i]);
            }
        }        
        
        if (jobs.isEmpty()) {
            // no jobs to wait for
            return true;
        }
        
        while (true) {

            long currentTime = System.currentTimeMillis();
            long timeToWait = endWait - currentTime;
            if (timeToWait < 0) {
                // wait time has expired but there are still jobs to wait 
                // for
                return false;
            }
            Event e = mJobExecutionRuntime.waitForEvent(getEventSubscriber(), timeToWait);
            
            if (e==null) {
                // no event received
                continue;
            }
            
            if (e.getType().equals(Event.EVENT_JOB_FINISHED)
                    || e.getType().equals(Event.EVENT_JOB_CANCELLED)) {

                JobID fjobId = JobID.parseID(e.getParameter());    
                mRunningChildJobs.remove(fjobId);
                jobs.remove(fjobId);
                
                if (jobs.isEmpty()) {
                    // all jobs finished
                    //
                    return true;
                }
            }
        }
    }
    

    /**
     * @see com.sap.scheduler.runtime.JobContext#fail()
     *
     */
    public void jobFailed() {
        failureRequest = true;
    }
    
    public boolean getJobFailed() {
        return failureRequest;
    }

    public short getReturnCode() {
        return mReturnCode;
    }
    
    /**
     * Returns true if the event is related to a child job of this
     * job. It it returns false the event will not be put onto the 
     * queue of this job.
     */
    public boolean acceptEvent(Event e) {
        
        if (e.getType().equals(Event.EVENT_JOB_FINISHED)
                || e.getType().equals(Event.EVENT_JOB_CANCELLED)) {
            
            JobID fjobId = JobID.parseID(e.getParameter());
            if (mRunningChildJobs.containsKey(fjobId)) {
                return true;
            }
        }
        return false;
    }
}

