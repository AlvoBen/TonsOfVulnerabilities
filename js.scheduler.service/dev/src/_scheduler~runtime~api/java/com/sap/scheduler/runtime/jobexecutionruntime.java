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
package com.sap.scheduler.runtime;

import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import com.sap.scheduler.api.SchedulerTaskID;

/**
 * <b>Note: This is not a public API. It may be modified or can 
 * disappear without notice.</b>
 * 
 * @author Dirk Marwinski
 */
public interface JobExecutionRuntime {

    public static final int VENDOR_STRING_MAX_LENGTH = 200;

    //------------------------------------------------------------------
    // General
    //------------------------------------------------------------------
    
    /**
     * Returns the timezone where this cluster is located.
     * 
     * @return String contains java.util.TimeZone.getDefault().getId();
     */
    public String getSystemTimeZone();
        
    //------------------------------------------------------------------
    // Retrieving Job Definitions
    //------------------------------------------------------------------

    /**
     * Returns all Job definitions known to the J2EE Engine
     * 
     * @return Array of JobDefinitions known to the J2EE Engine (may
     * by empty)
     */
    public JobDefinition[] getJobDefinitions()
                                      throws SQLException;

    /**
     * Returns the job defintion for a given job.
     * <p>
     * <b>Note:</b> This method will only return current job definitions. It 
     * will not return job definitions from jobs that have been deleted.  
     * 
     * @param jobDefinitionName name of job definition
     * 
     * @return JobDefinition or null if there is no such job definition
     * retrieving the job definition
     */
    public JobDefinition getJobDefinitionByName(String jobDefinitionName)
                                                         throws SQLException;
    
    public JobDefinition getJobDefinitionByName(JobDefinitionName jobDefinitionName)
                                                                     throws SQLException;
    
    /**
     * Get job definition by its job definition id
     * 
     * @param id job definition id
     * @return JobDefinition or null if there is no such job definition
     * retrieving the job definition
     */
    public JobDefinition getJobDefinitionById(JobDefinitionID id)
                                            throws SQLException;
    
    /**
     * This method remvoes the job definitions specified by the ids 
     * provided. 
     * <p>
     * This method checks, whether the job definitions can be safely removed.
     * This method will not remove job definitions for which there is currently
     * a job deployed or if there are still jobs in the database.
     * <p>
     * <b>Note:</b> This operation can be quite expensive and it should only
     * be called from within a clean job.
     *  
     * @param ids ids for job definitions to remove
     * @return job definitions which haven been removed (for informational 
     * purposes
     */
    public JobDefinition[] removeJobDefinitions(JobDefinitionID[] ids) 
                                                       throws SQLException;
    
    //------------------------------------------------------------------
    // Starting and stopping jobs 
    //------------------------------------------------------------------

    /**
     * This method submits the job for immediate start.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does  
     * not mean that the job has already been started or completed. 
     * <p>
     * 
     * @param jobDefId name of the job definition
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param parentId parent job for this job (may be null)
     * @param schedulerId scheduler which executes this job
     * @param schedTaskID the corresponding SchedulerTaskID (may be null)
     *
     * @return The assigned id for this job
     * 
     * @exception JobExecutorException in case this job cannot be submitted
     * for some reason
     * @exception ParameterValidationException The specified parameters do not 
     * match the required parameters for the specified job definition.
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     */
    public JobID executeJob(JobDefinitionID jobDefId,
                            JobParameter[] jobParameters,
                            JobID parentId,
                            SchedulerID schedulerId,
                            SchedulerTaskID schedTaskID) 
                                     throws JobExecutorException,
                                            ParameterValidationException,
                                            NoSuchJobDefinitionException;
    
    
    /**
     * This method submits the job for immediate start.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does  
     * not mean that the job has already been started or completed. 
     * <p>
     * 
     * @param jobDefId name of the job definition
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param parentId parent job for this job (may be null)
     * @param schedulerId scheduler which executes this job
     *
     * @return The assigned id for this job
     * 
     * @exception JobExecutorException in case this job cannot be submitted
     * for some reason
     * @exception ParameterValidationException The specified parameters do not 
     * match the required parameters for the specified job definition.
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     */
    public JobID executeJob(JobDefinitionID jobDefId,
                            JobParameter[] jobParameters,
                            JobID parentId,
                            SchedulerID schedulerId) 
                                     throws JobExecutorException,
                                            ParameterValidationException,
                                            NoSuchJobDefinitionException;


    /**
     * This method submits the job for immediate start.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does  
     * not mean that the job has already been started or completed. 
     * <p>
     * 
     * @param jobDefId name of the job definition
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param retentionPeriod retention period for this job
     * @param parentId parent job for this job (may be null)
     * @param schedulerId scheduler which executes this job
     * @param schedTaskID the corresponding SchedulerTaskID (may be null)
     *
     * @return The assigned id for this job
     * 
     * @exception JobExecutorException in case this job cannot be submitted
     * for some reason
     * @exception ParameterValidationException The specified parameters do not 
     * match the required parameters for the specified job definition.
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     */
    public JobID executeJob(JobDefinitionID jobDefId,
                            JobParameter[] jobParameters,
                            Integer retentionPeriod,
                            JobID parentId,
                            SchedulerID schedulerId,
                            SchedulerTaskID schedTaskID)                                      
                                     throws JobExecutorException,
                                            ParameterValidationException,
                                            NoSuchJobDefinitionException;
    
    /**
     * This method submits the job for immediate start.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does  
     * not mean that the job has already been started or completed. 
     * <p>
     * 
     * @param jobDefId name of the job definition
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param retentionPeriod retention period for this job
     * @param parentId parent job for this job (may be null)
     * @param schedulerId scheduler which executes this job
     *
     * @return The assigned id for this job
     * 
     * @exception JobExecutorException in case this job cannot be submitted
     * for some reason
     * @exception ParameterValidationException The specified parameters do not 
     * match the required parameters for the specified job definition.
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     */
    public JobID executeJob(JobDefinitionID jobDefId,
                            JobParameter[] jobParameters,
                            Integer retentionPeriod,
                            JobID parentId,
                            SchedulerID schedulerId)                                      
                                     throws JobExecutorException,
                                            ParameterValidationException,
                                            NoSuchJobDefinitionException;

    public JobID executeJobJXBP(JobDefinitionID jobDefId,
            JobParameter[] jobParameters,
            Integer retentionPeriod,
            JobID parentId,
            SchedulerID schedulerId,
            String vendorData)                                      
                     throws JobExecutorException,
                            ParameterValidationException,
                            NoSuchJobDefinitionException;
    
    /**
     * This method submits the job for immediate start.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does  
     * not mean that the job has already been started or completed. 
     * <p>
     * 
     * @param jobDefId name of the job definition
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param retentionPeriod retention period for this job
     * @param parentId parent job for this job (may be null)
     * @param schedulerId scheduler which executes this job
     * @param runAsUser user id which will be used to run this job
     * @param schedTaskID the corresponding SchedulerTaskID (may be null)
     *
     * @return The assigned id for this job
     * 
     * @exception JobExecutorException in case this job cannot be submitted
     * for some reason
     * @exception ParameterValidationException The specified parameters do not 
     * match the required parameters for the specified job definition.
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     * @exception NoSuchUserException The specified user does not exist.
     */    
    public JobID executeJob(JobDefinitionID jobDefId,
                            JobParameter[] jobParameters,
                            Integer retentionPeriod,
                            JobID parentId,
                            SchedulerID schedulerId,
                            String runAsUser,
                            SchedulerTaskID schedTaskID) 
                                        throws JobExecutorException,
                                               ParameterValidationException,
                                               NoSuchJobDefinitionException,
                                               NoSuchUserException;
    
    /**
     * This method submits the job for immediate start.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does  
     * not mean that the job has already been started or completed. 
     * <p>
     * 
     * @param jobDefId name of the job definition
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param retentionPeriod retention period for this job
     * @param parentId parent job for this job (may be null)
     * @param schedulerId scheduler which executes this job
     * @param runAsUser user id which will be used to run this job
     *
     * @return The assigned id for this job
     * 
     * @exception JobExecutorException in case this job cannot be submitted
     * for some reason
     * @exception ParameterValidationException The specified parameters do not 
     * match the required parameters for the specified job definition.
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     * @exception NoSuchUserException The specified user does not exist.
     */    
    public JobID executeJob(JobDefinitionID jobDefId,
                            JobParameter[] jobParameters,
                            Integer retentionPeriod,
                            JobID parentId,
                            SchedulerID schedulerId,
                            String runAsUser) 
                                        throws JobExecutorException,
                                               ParameterValidationException,
                                               NoSuchJobDefinitionException,
                                               NoSuchUserException;    

    public JobID executeJob(
                JobDefinitionID jobDefId,
                JobParameter[] jobParameters,
                JobID parentId,
                SchedulerID schedulerId,
                JobID jobId) 
                        throws JobExecutorException,
                               ParameterValidationException,
                               NoSuchJobDefinitionException;

    /**
     * This method submits the job for immediate start. The internal scheduler
     * is associated with this job.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does  
     * not mean that the job has already been started or completed. 
     * <p>
     * 
     * @param jobDefId name of the job definition
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param parentId parent job for this job (may be null)
     * @param schedTaskID the corresponding SchedulerTaskID (may be null)
     *
     * @return The assigned id for this job
     * 
     * @exception JobExecutorException in case this job cannot be submitted
     * for some reason
     * @exception ParameterValidationException The specified parameters do not 
     * match the required parameters for the specified job definition.
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     */
    public JobID executeJob(JobDefinitionID jobDefId,
                            JobParameter[] jobParameters,
                            JobID parent,
                            SchedulerTaskID schedTaskID) 
                                    throws JobExecutorException,
                                           ParameterValidationException,
                                           NoSuchJobDefinitionException;
    
    
    /**
     * This method submits the job for immediate start. The internal scheduler
     * is associated with this job.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does  
     * not mean that the job has already been started or completed. 
     * <p>
     * 
     * @param jobDefId name of the job definition
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param parentId parent job for this job (may be null)
     *
     * @return The assigned id for this job
     * 
     * @exception JobExecutorException in case this job cannot be submitted
     * for some reason
     * @exception ParameterValidationException The specified parameters do not 
     * match the required parameters for the specified job definition.
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     */
    public JobID executeJob(JobDefinitionID jobDefId,
                            JobParameter[] jobParameters,
                            JobID parent) 
                                    throws JobExecutorException,
                                           ParameterValidationException,
                                           NoSuchJobDefinitionException;    

    /**
     * This method submits the job for immediate start. The internal scheduler
     * is associated with this job.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does  
     * not mean that the job has already been started or completed. 
     * <p>
     * 
     * @param jobDefId name of the job definition
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param retentionPeriod retention period for this job
     * @param parentId parent job for this job (may be null)
     * @param schedTaskID the corresponding SchedulerTaskID (may be null)
     * 
     * @return The assigned id for this job
     * 
     * @exception JobExecutorException in case this job cannot be submitted
     * for some reason
     * @exception ParameterValidationException The specified parameters do not 
     * match the required parameters for the specified job definition.
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     */
    public JobID executeJob(JobDefinitionID jobDefId,
                            JobParameter[] jobParameters,
                            Integer retentionPeriod,
                            JobID parent,
                            SchedulerTaskID schedTaskID)
                                        throws JobExecutorException,
                                               ParameterValidationException,
                                               NoSuchJobDefinitionException;
        
    
    /**
     * This method submits the job for immediate start. The internal scheduler
     * is associated with this job.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does  
     * not mean that the job has already been started or completed. 
     * <p>
     * 
     * @param jobDefId name of the job definition
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param retentionPeriod retention period for this job
     * @param parentId parent job for this job (may be null)
     * 
     * @return The assigned id for this job
     * 
     * @exception JobExecutorException in case this job cannot be submitted
     * for some reason
     * @exception ParameterValidationException The specified parameters do not 
     * match the required parameters for the specified job definition.
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     */
    public JobID executeJob(JobDefinitionID jobDefId,
                            JobParameter[] jobParameters,
                            Integer retentionPeriod,
                            JobID parent)
                                        throws JobExecutorException,
                                               ParameterValidationException,
                                               NoSuchJobDefinitionException;

    /**
     * Cancels a job. If it has not been started it will immediately go into
     * the CANCELLED state. If it has not been started, it will cooperatively
     * try to abort the job (@see com.sap.scheduler.runtime.JobContext#isCancelled()).
     * This method will just return with no indication whether the job was 
     * successfully cancelled or not. 
     * 
     * @param jobid job id for the running job
     * 
     * @throws JobIllegalStateException The job is not in state 
     * {@link com.sap.scheduler.runtime.JobStatus#SCHEDULED SCHEDULED} or 
     * {@link com.sap.scheduler.runtime.JobStatus#RUNNING RUNNING}
     * @exception NoSuchJobDefinitionException The specified job definition does
     * not exist.
     */
    public void cancelJob(JobID jobid)
                             throws JobIllegalStateException,
                                    NoSuchJobException,
                                    SQLException;

    
    //------------------------------------------------------------------
    // Parent/Child Functionality
    //------------------------------------------------------------------

    /**
     * Get all child jobs
     * 
     * @param jobid id for the job
     * 
     * @return possible empty array of child jobs
     * @exception NoSuchJobException The specified job does not 
     * exist.
     */
    public Job[] getChildJobs(JobID jobid)
                                    throws NoSuchJobException,
                                           SQLException;

    /**
     * Returns true if this job has child jobs
     * 
     * @param jobid jobid to use
     * 
     * @return true if the job has child jobs, false otherwise
     */
    public boolean hasChildJobs(JobID jobid)
                                  throws NoSuchJobException,
                                         SQLException;
    
    /**
     * Returns true if this job has child jobs
     * 
     * @param jobid array of job ids to use
     * 
     * @return array of booleans corresponding to the array of job ids
     */
    public boolean[] haveChildJobs(JobID[] jobid)
                                       throws SQLException;
    
    //------------------------------------------------------------------
    // Maintining runtime job information
    //------------------------------------------------------------------

    /**
     * This method removes all information about this job instance from 
     * the NetWeaver Scheduler (including logs). This method only works on jobs 
     * which are in state {@link JobStatus#COMPLETED COMPLETED}, 
     * {@link JobStatus#ERROR ERROR} or {@link JobStatus#UNKNOWN UNKNOWN}, 
     * {@link JobStatus#CANCELLED CANCELLED}.
     * 
     * @param jobid job id to use
     * @throws JobIllegalStateException thrown if the to be removed is not in one state 
     *                                  mentioned above. 
     * @throws NoSuchJobException thrown if a job with the given job id does not exist 
     * @throws SQLException  thrown if there is a problem with the underlying infrastructure
     */
    public void removeJob(JobID jobid)
                                throws JobIllegalStateException,
                                       NoSuchJobException,
                                       SQLException;

    /**
     * This method removes all information about this job instance from 
     * the NetWeaver Scheduler (including logs). This method only works on jobs 
     * which are in state {@link JobStatus#COMPLETED COMPLETED}, 
     * {@link JobStatus#ERROR ERROR} or {@link JobStatus#UNKNOWN UNKNOWN}, 
     * {@link JobStatus#CANCELLED CANCELLED}.
     * 
     * @param jobid job id to use
     * @param force if set to true the method will remove the job regardless
     *              of its state otherwise it will be equivalent to 
     *              (@link JobExecutionRuntime#removeJob(JobID)). Use with care.
     * @throws JobIllegalStateException thrown if the to be removed is not in one state 
     *                                  mentioned above (only possible if <code>force</code> 
     *                                  is set to false)
     * @throws NoSuchJobException thrown if a job with the given job id does not exist 
     * @throws SQLException thrown if there is a problem with the underlying infrastructure
     */
    public void removeJob(JobID jobid, boolean force)
                                           throws NoSuchJobException,
                                                  JobIllegalStateException,
                                                  SQLException;
    
    /**
     * This method removes all information for the given job instances from the 
     * NetWeaver Scheduler (including logs). This is a convenience method. 
     * Logical errors (e.g. one or more jobs in an illegal state or job does not more exist) 
     * will be ignored.
     * 
     * @param jobids the job ids to use
     * @throws SQLException thrown if there is a problem with the underlying infrastructure
     */
    public void removeJobs(Job[] jobid) throws SQLException;

    /**
     * This method removes all information about the Job[] instances from 
     * the J2EE Engine (including logs).
     * This is a convenience method. Logical errors (e.g. one or more jobs in an 
     * illegal state or job does not more exist) will be ignored.
     * 
     * @param jobid the job id for the job to remove
     * @param force if set to true the method will remove the job regardless
     * of its state otherwise it will be equivalent to 
     * (@link JobExecutionRuntime#removeJob(JobID)). Use with care.
     * 
     * @exception SQLException thrown if there is a problem with the underlying infrastructure
     */    
    public void removeJobs(Job[] jobid, boolean force) throws SQLException;

    /**
     * This method removes all information for the given job instances from the 
     * NetWeaver Scheduler (including logs). This is a convenience method. 
     * Logical errors (e.g. one or more jobs in an illegal state or job does not more exist) 
     * will be ignored.
     * 
     * @param jobids the job ids to use
     * @throws SQLException thrown if there is a problem with the underlying infrastructure
     */
    public void removeJobs(JobID[] jobids) throws SQLException;

    /**
     * This method returns the job for the given job id.
     * <p>
     * Note: The job parameter values are not set on the returned Job object
     * 
     * @param jobid job id of the job
     * @return the job or null if there is no such job 
     */
    Job getJob(JobID jobid) 
                     throws SQLException;
    
    /**
     * This method returns all parameters for the given job.
     * 
     * @param jobid
     * @return job parameters for specified job
     * @throws NoSuchJobException if there is no job with the given id
     */
    JobParameter[] getJobParameters(JobID jobid)
                                throws NoSuchJobException,
                                       SQLException;

    /**
     * This method returns the jobs for the given job ids.
     * 
     * @param jobid job ids of the jobs
     * @return an array of jobs. The array may be empty if ther is no 
     * such job. 
     */
    Job[] getJobs(JobID[] jobid)
                         throws SQLException;
    
    /**
     * Returns the job log
     * 
     * @return the whole log file as string or null if there is no such log
     * @exception NoSuchJobException if there is no job with the given id
     */
    public LogIterator getJobLog(JobID jobid, LogIterator it, int fetchSize)
                                                      throws SQLException,
                                                             NoSuchJobException;
    
    
    
    /**
     * Returns the job log
     * 
     * @return the whole log file as SchedulerLogRecord[] or null if there is no such log
     * @exception NoSuchJobException if there is no job with the given id
     */
    public SchedulerLogRecordIterator getJobLogRecords(JobID jobid, SchedulerLogRecordIterator it, int fetchSize)
                                                      throws SQLException,
                                                             NoSuchJobException;
    
    /**
     * Removes the job log for the given job.
     * 
     * @param jobid 
     * @exception NoSuchJobException if there is no job with the given id
     * @exception JobIllegalStateException
     */
    public void removeJobLog(JobID jobid)
                                    throws NoSuchJobException, 
                                           JobIllegalStateException,
                                           SQLException;

    /**
     * Returns the status of a job
     * 
     * @param jobid
     * @return status for job with the provided job id
     */
    public JobStatus getJobStatus(JobID jobid)
                                     throws NoSuchJobException,
                                            SQLException;
    
    /**
     * This method will return all jobs which match the provided filter
     * criteria. If the result set is bigger than the provided fetchSize
     * parameter the remaining entries can be retrieved using the returned
     * iterator. The following code snipped shows how to retrieve the 
     * result from the call.
     * <pre>
     * JobIterator iter = runtime.getJobs(myFilter, null, 1000);
     * Job[] jobs = iter.nextChunk();
     * // do something with the returned job ojbects
     * while(iter.hasMoreChunks()) {
     *     iter= jxbp.getJobs(JobStatus.COMPLETED, iter, 1000);
     *     Job[] jobs = iter.nextChunk();
     *     // do something with the returned job objects
     *     //
     * }
     * </pre>
     * @param filter the pre-initialized filter object
     * @param it An iterator which has been returned by a previous invocation
     * of null if this is the first call
     * @param fetchSize the number of records to fetch
     * @return
     * @throws SQLException
     */
    public JobIterator getJobs(JobFilter filter, JobIterator iter, int fetchSize)
                                                                throws SQLException;
    
    /**
     * This method associates vendor data with the given job.
     * <p> 
     * TODO document maximum length of the string
     * <p>
     * @param ids
     * @param value
     */
    public void setVendorData(SchedulerDefinition def, JobID[] ids, String value)
                                                                throws SQLException;

    /**
     * This method returns vendor data associated with the given job ids.
     * <p> 
     * TODO document maximum length of the string
     * <p>
     * @param jobIds an array of job ids
     * @return an array of vendor data strings for the specified job ids
     */
    public String[] getVendorData(JobID[] jobIds)
                                         throws SQLException;
    
        
    //------------------------------------------------------------------
    // Events
    //------------------------------------------------------------------
        
    /**
     * Returns all events for this subscriber. The events which have been
     * queued for this subscriber will be removed in the scheduler.
     * 
     * @return All events which have been queued so far
     */
    public Event[] getEvents(EventSubscriber es, int fetchSize);

    /**
     * Clear all events which have been returned by getEvents()
     * 
     */
    public void  clearEvents(EventSubscriber es);

    /**
     * This method allows a scheduler to specify which events they are 
     * interested in. Matches must be exact.
     * 
     * @param eventType
     */
    public void setFilter(SchedulerDefinition def, String[] eventType)
                                                         throws SQLException;
    
    /**
     * Returns runtime event type names which are used by the 
     * scheduler runtime.
     * 
     * @return array of runtime event names
     */
    public String[] getJXBPRuntimeEventTypes();
    
    public SchedulerDefinition getSchedulerForUser(String principal)
                                                        throws SQLException;
    
    public SchedulerDefinition getSchedulerByName(String name)
                                                         throws SQLException;
    
    public SchedulerDefinition getSchedulerById(SchedulerID id) throws SQLException;
    
    public SchedulerDefinition getBuiltinScheduler()
                                          throws SQLException;

    /**
     * Registers a new external scheduler.
     * @param name name of the scheduler
     * @param user registered user 
     * @param descritpion
     * @param events Events that the external scheduler is interested in 
     */
    public SchedulerDefinition addScheduler(String name, 
                                            String user, 
                                            String userPassword,
                                            String description,
                                            long inactivityGracePeriod,
                                            String[] events)
                                                        throws SQLException,
                                                        UserAccountException,
                                                        SchedulerAlreadyDefinedException;
    
    
    /**
     * Removes a scheduler and its user (if it was created by the scheduler).
     * 
     * @param def the SchedulerDefinition
     * @throws SchedulerRemoveException if something went wrong while removing the scheduler
     * @throws SQLException if an error occurrs in the underlying infrastructure
     */
    public void removeScheduler(SchedulerDefinition def) throws SchedulerRemoveException, SQLException;
    
    
    public SchedulerDefinition[] getAllSchedulers()
                                             throws SQLException;

    public void deactivateScheduler(SchedulerID id) 
                                             throws SQLException;

    public void updateTimestamp(SchedulerID id);
    
    public void setReturnCode(JobID jobId, short returnCode)
                                                throws NoSuchJobException,
                                                       SQLException;
    
    /**
     * Set the specified job parameter for the specified job. This
     * method is usually used by running jobs to set their OUT and INOUT
     * parameters.
     * @param jobid Job Id for which to set the parameter
     * @param param the parameter
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public void setJobParameter(JobID jobid, JobParameter param)
                                           throws IllegalArgumentException,
                                                  NoSuchJobException,
                                                  SQLException;

    public JobParameterDefinition[] getJobParameterDefinition(String jobDefinitionName)
                                                                        throws NoSuchJobDefinitionException,
                                                                               SQLException;

    public void verifyParameters(JobDefinitionID jobDefinitionId, JobParameter[] parameters)
                                       throws ParameterValidationException, 
                                              NoSuchJobDefinitionException,
                                              SQLException;
    
    public Event waitForEvent(EventSubscriber sub);

    public Event waitForEvent(EventSubscriber sub, long timeout);
    
    public EventSubscriber getEventSubscriberByID(AbstractIdentifier esId) throws SQLException;

    public boolean isJobCancelled(JobID id)
                                       throws SQLException;

    public void registerEventSubscriber(EventSubscriber sub) throws SQLException;
    
    public void unregisterEventSubscriber(EventConsumer consumer) throws SQLException;
    
    public void raiseEvent(String type, String parameter, String additionalParameter, Date raisedDate, AbstractIdentifier raisedByDetails);
    
    /**
     * Return the event types. The mapping is as folows:
     * key: event-name
     * value: event-description
     * 
     * @return the Properties with the event names and descriptions
     */
    public Properties getEventTypes();

    /**
     * This method is invoked by the clean job in order to fix NULL entries in
     * ENDED columns (this is handled differently by databases).
     */
    public int updateEndedNullValues() 
                          throws SQLException;

}
