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
package com.sap.scheduler.spi;

import com.sap.scheduler.runtime.*;

import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 * This interface specifies the Java eXternal Batch Processing (JXBP) API. It
 * can be used by external job schedulers in order to run jobs inside the 
 * J2EE Engine.
 * <p>
 * External scheduler need to be registered with the scheduler execution 
 * runtime in order to use this service. 
 * <p>
 * <b>New in JXBP 1.1</b>
 * <ul>
 * <li> A new {@link #executeJob(JobDefinitionID, JobParameterWS[], Integer, String)
 * executeJob} method has been added to allow an external scheduler to set the 
 * vendor string while submitting the job.
 * </ul>
 * 
 * 
 * @author Dirk Marwinski
 */
public interface JXBP extends Remote {

    /**
     * JXBP interface version. External schedulers can use the version 
     * String in order to determine whether they are able to use this 
     * JXBP version. Minor number changes are always backward compatible.
     */
    public static final String JXBP_VERSION_STRING = "1.1";

    /**
     * Maximum length for vendor data strings.
     */
    public static final int VENDOR_STRING_MAX_LENGTH = 200;

    /**
     * Maximum fetch size for all operations which could potentially return
     * huge amounts of data.
     */
    public static final int MAX_FETCH_SIZE = 4000;
    
    //------------------------------------------------------------------
    // General
    //------------------------------------------------------------------

    /**
     * Returns the timezone where this cluster is located. When scheduling
     * jobs across different timezones this must be taken into consideration.
     * 
     * @return String which contains the result from 
     * java.util.TimeZone.getDefault().getId();
     */
    public String getSystemTimeZone() throws RemoteException;

    /**
     * Get the version of the JXBP interface. 
     * <p>
     * Minor version changes (e.g. 1.0 -&gt; 1.1) are always backwards 
     * compatible. Major version changes (e.g. 1.2 -&gt; 2.0) are not 
     * backwards compatible.
     * 
     * @see #JXBP_VERSION_STRING
     * 
     * @return String representing the JXBP version
     */
    public String getVersion() throws RemoteException;

    //------------------------------------------------------------------
    // Retrieving Job Definitions
    //------------------------------------------------------------------

    /**
     * Returns all job definitions known to the J2EE Engine. 
     * <p>
     * Job definitions represent jobs which are deployed in the J2EE Engine.
     * For jobs implemented by message driven beans this means that there 
     * is one job definition object for every deployed message driven bean. 
     * <p>
     * This method returns JobDefinition objects for job definitions which are 
     * still deployed in the system only.
     * <p>
     * Job definition objects are always kept in the system, even when the 
     * corresponding message driven bean is undeployed. This is due to the fact
     * that the job definition is still referenced by other objects in the
     * system. The CleanJob takes care of removing job definitions which are 
     * not deployed anymore. 
     * <p>
     * @return array of JobDefinition objects known to the J2EE Engine (may
     * by empty)
     * @exception JXBPException if there is a technical problem 
     * retrieving the JobDefinition objects
     */
    public JobDefinition[] getJobDefinitions()
                                      throws JXBPException, RemoteException;

    /**
     * Returns the JobDefintion object for a given job definition name.
     * <p>
     * <b>Note:</b> This method will only return job definitions for 
     * concrete jobs that are still deployed. Please see 
     * {@link #getJobDefinitions() } for an introduction to job definitions.
     * 
     * @param jobDefinitionName name of job definition 
     * @return JobDefinition or null if there is no such job definition
     * @exception JXBPException if there is a technical problem 
     * retrieving the job definition
     */
    public JobDefinition getJobDefinitionByName(String jobDefinitionName)
                                                         throws JXBPException, RemoteException;

    /**
     * Get a JobDefinition object by its job definition id. This method 
     * may return a JobDefinitions object for jobs that are not deployed 
     * anymore.
     * 
     * @param id job definition id
     * @return JobDefinition or null if there is no such job definition
     * @throws JXBPException if there is a technical problem 
     * retrieving the job definition
     */
    public JobDefinition getJobDefinitionById(JobDefinitionID id)
                                            throws JXBPException, RemoteException;

    //------------------------------------------------------------------
    // Starting and stopping jobs
    //------------------------------------------------------------------

    /**
     * This method submits the job for immediate start.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does
     * not mean that the job has already been started or completed. There
     * are many reasons why the execution of this job may be delayed.
     * <p>
     *
     * @param jobDefId job definition id. An instance of the job definition
     * will be created as the result the call to this method.
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param retentionPeriod specifies how many days the job and the job
     * log should be kept. -1 means that the job will not be removed and null
     * means that the parameter will be taken from the job definition.  
     *
     * @return the assigned id for this job
     *
     * @exception JobExecutorException in case there is a problem with the 
     *            job (e.g. job not known) or its parameters
     * @exception JXBPException in case there is a technical problem
     */
    public JobID executeJob(JobDefinitionID jobDefId, 
                            JobParameterWS[] jobParameters,
                            Integer retentionPeriod) 
                                         throws ParameterValidationException,
                                                NoSuchJobDefinitionException,
                                                JXBPException,
                                                RemoteException;

    /**
     * This method submits the job for immediate start.
     * <p>
     * <b>Note:</b> This method will return almost immediately. This does
     * not mean that the job has already been started or completed. There
     * are many reasons why the execution of this job may be delayed.
     * <p>
     *
     * @param jobDefId job definition id. An instance of the job definition
     * will be created as the result the call to this method.
     * @param jobParameters an array of job parameters (must match the names
     * and types from the job definition)
     * @param retentionPeriod specifies how many days the job and the job
     * log should be kept. -1 means that the job will not be removed and null
     * means that the parameter will be taken from the job definition.
     * @param vendorData the vendor specific data for this job  
     *
     * @return the assigned id for this job
     *
     * @exception JobExecutorException in case there is a problem with the 
     *            job (e.g. job not known) or its parameters
     * @exception JXBPException in case there is a technical problem
     */
    public JobID executeJob(JobDefinitionID jobDefId, 
                            JobParameterWS[] jobParameters,
                            Integer retentionPeriod,
                            String vendorData) 
                                         throws ParameterValidationException,
                                                NoSuchJobDefinitionException,
                                                JXBPException,
                                                RemoteException;
    
    
    /**
     * Cancels a job. If it not in { @link JobStatus#RUNNING RUNNING} state it 
     * will immediately go into the {@link JobStatus#CANCELLED CANCELLED}
     * state. If it has been started, it 
     * will cooperatively try to abort the job. The running job can check
     * whether there was a cancel request by calling 
     * {@link JobContext#isCancelled()}.
     * <p>
     * This method will return immediately with no indication whether the job 
     * was successfully cancelled or not. If the job was successfully cancelled
     * a JOB_CANCELLED event will be raised. 
     * 
     * @param jobid job id for job to be cancelled
     *
     * @exception NoSuchJobException if there is no job with the given id
     * @exception IllegalStateException if the job is in an illegal state, e.g.
     * if it is alredy finished and in state {@link JobStatus#COMPLETED COMPLETED} 
     * @exception JXBPException in case there was a technical problem
     */
    public void cancelJob(JobID jobid)
                             throws JobIllegalStateException,
                                    NoSuchJobException,
                                    JXBPException, 
                                    RemoteException;

    /**
     * This method will change the state from 
     * {@link com.sap.scheduler.runtime.JobStatus#SCHEDULED SCHEDULED} to
     * {@link com.sap.scheduler.runtime.JobStatus#HOLD HOLD}. If the job
     * is not in state {@link com.sap.scheduler.runtime.JobStatus#SCHEDULED}
     * an exception will be thrown.
     * 
     * @param jobid job to hold
     * @throws NoSuchJobException if the is no job with the given id
     * @throws JobIllegalStateException The job is not in state {@link com.sap.scheduler.runtime.JobStatus#SCHEDULED}
     * @throws JXBPException The job does not exist or there is another error
     * 
     * @deprecated this method is not supported
     */
    public void holdJob(JobID jobid)
                                throws NoSuchJobException,
                                       JobIllegalStateException,
                                       JXBPException,
                                       RemoteException;

    /**
     * This method will change the state from
     * {@link com.sap.scheduler.runtime.JobStatus#HOLD HOLD} to 
     * {@link com.sap.scheduler.runtime.JobStatus#SCHEDULED SCHEDULED}.
     * If the job is not in 
     * {@link com.sap.scheduler.runtime.JobStatus#HOLD HOLD} state an exception
     * will be thrown.
     * @param jobid job ot release
     * @throws JobIllegalStateException if the job is not in state HOLD
     * @throws NoSuchJobException if there is no job with the given id
     * @exception JXBPException if there was a technical problem 
     * 
     * @deprecated this method is not supported
     */
    public void releaseJob(JobID jobid)
                                throws JobIllegalStateException, 
                                       NoSuchJobException,
                                       RemoteException,
                                       JXBPException;

    //------------------------------------------------------------------
    // Parent/Child Functionality
    //------------------------------------------------------------------

    /**
     * Get all child jobs of a given job. A child job is created and 
     * executed by a parent job through one of its 
     * {@link JobContext#executeJob(JobDefinitionID) executeJob}
     * methods.
     * 
     * @param jobid id of the job
     *
     * @return possible empty array of child jobs
     * @exception NoSuchJobException if the is no job with the given id
     * @exception JXBPException if there was a technical problem 
     */
    public Job[] getChildJobs(JobID jobid)
                                    throws NoSuchJobException,
                                           JXBPException,
                                           RemoteException;

    /**
     * Returns true if this job has at least one child job.
     *
     * @param jobid id of the job
     *
     * @return true if the job has child jobs, false otherwise
     * @exception NoSuchJobException if the is no job with the given id
     * @exception JXBPException if there was a technical problem 
     */
    public boolean hasChildJobs(JobID jobid)
                                  throws NoSuchJobException,
                                         JXBPException, 
                                         RemoteException;

    /**
     * This convenience method checks whether several jobs represented by
     * their job ids have child jobs.
     *
     * @param jobid array of job ids
     *
     * @return array of booleans corresponding to the array of job ids. If 
     * a job does not exist the corresponding entry of the boolean array will
     * be set to false.
     * @exception JXBPException if there was a technical problem 
     */
    public boolean[] haveChildJobs(JobID[] jobid)
                                       throws JXBPException, RemoteException;

    //------------------------------------------------------------------
    // Maintining runtime job information
    //------------------------------------------------------------------

    /**
     * This method removes all information about this job instance fron
     * the J2EE Engine (including logs).
     *
     * @param jobid job id of the job to remove
     * 
     * @exception JobIllegalStateException if the job is not in state
     * {@link JobStatus#COMPLETED COMPLETED}, 
     * {@link JobStatus#UNKNOWN UNKNOWN}, 
     * {@link JobStatus#CANCELLED CANCELLED}, or 
     * {@link JobStatus#ERROR ERROR}
     * @exception NoSuchJobException if there is no job with the given id
     * @exception JXBPException if the was a technical problem removing the job
     */
    public void removeJob(JobID jobid)
                                throws JobIllegalStateException,
                                       NoSuchJobException,
                                       JXBPException, 
                                       RemoteException;

    /**
     * This method removes all records of the given job instances fron the J2EE
     * Engine (including logs). This is a convenience method. Logical errors
     * (e.g. one or more jobs are in an illegal state or non existing jobs) will be 
     * ignored.
     * @param jobids an array of job ids for jobs to be removed
     * @exception JXBPException if ther was a technical problem removing the job
     */
    public void removeJobs(JobID[] jobids)
                              throws JXBPException, 
                                     RemoteException;

    /**
     * This method returns the job for the given job id.
     *
     * @param jobid job id of the job
     * @return the job object or null if there is no such job
     * @exception JXBPException if there was a technical problem getting
     * the job object
     */
    Job getJob(JobID jobid)
                     throws JXBPException, RemoteException;

    /**
     * This method returns all parameters for the given job. 
     *
     * @param jobid jog id of the job
     * @return an arry of job parameters (possibly empty)
     * @throws NoSuchJobException if there is not job for the given id
     * @exception JXBPException if there was a technical problem getting
     * the job parameter objects
     */
    JobParameterWS[] getJobParameters(JobID jobid)
                                throws NoSuchJobException,
                                       JXBPException, 
                                       RemoteException;

    /**
     * This method will return all jobs which match the provided filter
     * criteria. If the result set is bigger than the provided fetchSize
     * parameter the remaining entries can be retrieved using the returned
     * iterator. The following code snipped shows how to retrieve the 
     * result from an invocation to the JXBP. The filter is set to return 
     * all job objects which are in state {@link JobStatus#ERROR ERROR}. 
     * <pre>
     * JobFilter myFilter = new JobFilter();
     * myFilter.setJobStatus(JobStatus.ERROR);
     * 
     * JobIterator iter = jxbp.getJobs(myFilter, null, 1000);
     * 
     * Job[] jobs = iter.nextChunk();
     * // do something with the returned job ojbects
     * 
     * while(iter.hasMoreChunks()) {
     * 
     *     iter= jxbp.getJobs(myFilter, iter, 1000);
     * 
     *     Job[] jobs = iter.nextChunk();
     *     // do something with the returned job objects
     *     //
     * }
     * </pre>
     * @param filter the pre-initialized filter object
     * @param iter An iterator which has been returned by a previous invocation
     * of null if this is the first call
     * @param fetchSize the number of records to fetch
     * @return a JobIterator which contains a number of jobs and state 
     * information in case there are more than fetchSize jobs
     * @throws JXBPException if there was a technical problem retrieving 
     * the jobs
     */
    public JobIterator getJobs(JobFilter filter, JobIterator iter, int fetchSize)
                                                                      throws JXBPException,
                                                                             RemoteException;

    /**
     * This method returns the jobs for the given job ids.
     *
     * @param jobid and array of job ids
     * @return an array of jobs with the same length as the array of job ids.
     * Elements of the array may be empty if the job id did not reference a 
     * valid job.
     * @throws JXBPException if there was a technical problem retrieving 
     * the jobs
     */
    Job[] getJobs(JobID[] jobid)
                         throws JXBPException, RemoteException;

    /**
     * Returns the job log.
     * <p>
     * The following code snipped shows how the complete log for a particular 
     * job log can be retrieved:
     * <pre>
     * LogIterator iter = jxbp.getJobLog(jobid, null, 2000);
     * String log = iter.nextChunk();
     * // do something with the log snippet
     * 
     * while (iter.hasMoreChunks()) {
     * 
     *     iter = jxbp.getJobLog(jobid, iter, 2000);
     *     log = iter.nextChunk();
     *     // do something with the log snippet
     * }
     * </pre>
     * @param jobid job id of the job
     * @param iter LogIterator object or null if this is the first call 
     * to this method
     * @param fetchSize number of lines to retrieve. If fetchSize is larger
     * than {@link #MAX_FETCH_SIZE} it will be reduced to {@link #MAX_FETCH_SIZE}. 
     * @return An iterator which contains the next chunk of the log
     * 
     * @exception NoSuchJobException if there is no job for the provided id
     * @exception JXBPException if there was another technical problem
     */
    public LogIterator getJobLog(JobID jobid, LogIterator iter, int fetchSize)
                                                             throws NoSuchJobException,
                                                                    JXBPException,
                                                                    RemoteException;

    /**
     * Removes the job log for the given job.
     *
     * @param jobid job id of the job
     * @exception NoSuchJobException if there was a problem deleting the
     * log.
     * @exception JobIllegalStateException if the job is not in one of the 
     * final states
     * @exception NoSuchJobException if there is no job for the provided id
     * @exception JXBPException if there was another technical problem
     */
    public void removeJobLog(JobID jobid)
                                    throws NoSuchJobException,
                                           JobIllegalStateException,
                                           JXBPException, 
                                           RemoteException;

    /**
     * Returns the status of a job
     *
     * @param jobid job id of the job
     * @return a JobStatus object
     * @exception NoSuchJobException if there is no job for the provided id
     * @exception JXBPException if there was a technical problem getting 
     * the job status
     */
    public JobStatus getJobStatus(JobID jobid)
                                     throws NoSuchJobException,
                                            JXBPException, 
                                            RemoteException;

    /**
     * This method associates vendor data with the given job.
     *     
     * @param id an array of job ids
     * @param value Vendor data string. The maximum length must be at most
     * {@link #VENDOR_STRING_MAX_LENGTH}
     * @exception NoSuchJobException if there is no job for the provided id
     * @exception JXBPException if there was a technical problem setting 
     * the vendor data
     */
    public void setVendorData(JobID id, String value)
                                          throws NoSuchJobException,
                                                 JXBPException, 
                                                 RemoteException;
    
    /**
     * This method associates vendor data with the given jobs. This method 
     * ignores if there is no job for a provided job id. 
     *     
     * @param ids an array of job ids
     * @param value Vendor data string. The maximum length must be at most
     * {@link #VENDOR_STRING_MAX_LENGTH}
     * @exception JXBPException if there was a technical problem setting 
     * the vendor data
     */
    public void setVendorData(JobID[] ids, String value)
                                          throws JXBPException, 
                                                 RemoteException;    
    
    /**
     * This method returns vendor data associated with the given job ids.
     *
     * @param jobIds an array of job ids
     * @return an array of Strings which contain the vendor data for the 
     * corresponding job ids. In case there is not job for a provided job id 
     * the corresponding vendor data will be null.
     * @exception JXBPException if there was a technical problem getting 
     * the vendor data
     */
    public String[] getVendorData(JobID[] jobIds)
                                         throws JXBPException, 
                                                RemoteException;

    //------------------------------------------------------------------
    // Events
    //------------------------------------------------------------------

    /**
     * Returns all unhandled events. The events will be marked and
     * can be removed with the clearEvents method.
     *
     * @param fetchSize number of events to retrieve. If fetchSize if greater
     * than {@link #MAX_FETCH_SIZE} it will be reduced to {@link #MAX_FETCH_SIZE}. 
     * @return All events which have been queued so far.
     * @exception JXBPException if there was a technical problem getting the 
     * events
     */
    public Event[] getUnhandledEvents(int fetchSize)
                                       throws JXBPException, RemoteException;

    /**
     * Clear all events which have been returned by {@link #getUnhandledEvents(int)}
     *
     * @exception JXBPException if there was a technical problem clearing 
     * the events
     */
    public void clearEvents()
                        throws JXBPException, 
                               RemoteException;

    /**
     * This method allows a scheduler to specify which events they are
     * interested in. Wildcards are not supported, matches must be exact. A list
     * of possible event types can be obtained by the method
     * {@link #getJXBPRuntimeEventTypes()}.
     *
     * @param eventType an array of event types
     * @throws JXBPException
     */
    public void setFilter(String[] eventType)
                                   throws JXBPException, 
                                          RemoteException;

    /**
     * Returns runtime event type names which are used by the
     * scheduler runtime.
     *
     * @return array of runtime event names
     */
    public String[] getJXBPRuntimeEventTypes() throws RemoteException;

}
