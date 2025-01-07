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

import java.util.logging.Logger;

import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;


/**
 * The job context of a running job which allows the job to access the 
 * scheduler runtime environment. Every job has a job context. The JobContext 
 * is passed as an argument to the 
 * {@link com.sap.scheduler.runtime.mdb.MDBJobImplementation#onJob(JobContext) onJob}
 * method and is specific to the job that
 * is passed to. You are always authorized to call methods of this interface. 
 * <p>
 * <b>Note:</b> Most of the methods of the interface throw a 
 * {@link com.sap.scheduler.runtime.SchedulerRuntimeException SchedulerRuntimeException}
 *  which indicates a problem with the application server infrastructure. 
 *  This exception should not be caught by the implementation of a job since it 
 *  is caught by the scheduler runtime environment.
 */
public interface JobContext
{
    /**
     * Get the {@link Job} object associated with this job.
     * 
     * @return the job object
     * @throws SchedulerRuntimeException if there was a technical problem
     * retrieving the value
     */
    public Job getJob()
                  throws SchedulerRuntimeException;

    /**
     * Get an existing job instance by its job id. It is only possible to 
     * retrieve child jobs.
     *
     * @param jobId the id of the job to get.
     *
     * @return the job, or null if the job cannot be found or is not a child
     * job
     * @throws SchedulerRuntimeException if there was a technical problem
     * retrieving the values
     */
    public Job getChildJobById(JobID jobId)
                                  throws SchedulerRuntimeException;


    /**
     * Get the {@link JobDefinition JobDefinition} object which represents 
     * the job metadata for the given name
     * 
     * @param jobDefinitionName name of the job definition
     * @return a JobDefinition object or null in case there is no such job
     * definition.
     * @throws SchedulerRuntimeException if there was a technical problem
     * retrieving the values
     */
    public JobDefinition getJobDefinition(String jobDefinitionName)
                                                   throws SchedulerRuntimeException;
    
    /**
     * Returns the job definitions parameters for the given job defintion
     * which were provided when the job was deployed.
     * 
     * @param jobDefinitionName job definition name for which to retrieve the 
     * parameter definitions
     * @return array of job definition parameters (may be empty). If there is 
     * no such job definition a null pointer will be returned.
     * @throws SchedulerRuntimeException if there was a technical problem
     * retrieving the values
     */
    public JobParameterDefinition[] getJobParameterDefinitions(String jobDefinitionName)
                                                        throws SchedulerRuntimeException;

    /**
     * Returns the named job definition parameter for the given job defintion
     * which was provided when the job was deployed.
     * 
     * @param jobDefinitionName the name of the job definition
     * @return JobDefinitionParameter or null if there is no such parameter for
     *         the specified job or if there is no such job
     * @throws SchedulerRuntimeException if there was a technical problem
     * retrieving the value
     */
    public JobParameterDefinition getJobParameterDefinition(String jobDefinitionName,  
                                                            String parameterName)
                                                                 throws SchedulerRuntimeException;

    //------------------------------------------------------------------
    // Logging
    //------------------------------------------------------------------

    /**
     * Get a JDK-compliant Logger object. Log records written to this logger 
     * are persisted in the context of this particular job.
     *
     * @return a logger to write log messages to.
     */
    public Logger getLogger();
   
    /**
     * Get the category for this job which can be used to log messages in the
     * context of the job. The log messages written to this category will be
     * persisted in the database and are available with the job information.
     * 
     * @return the Category object
     */
    public Category getCategory();
    
    /**
     * Get the location that is required in order to be able to log to the 
     * Job's category. Records written to this category are discarded. Use
     * only in conjunction with the category object.
     * 
     * @return the Location object
     */
    public Location getLocation();

    //------------------------------------------------------------------
    // Parameter Handling
    //------------------------------------------------------------------
    
    /** 
     * Get a job parameter. If the job parameter has not been set and there
     * is a default value it will contain the default value, otherwise null
     * will be returned.
     * 
     * @param name the name of the parameter
     * 
     * @return {@link com.sap.scheduler.runtime.JobParameter} object or null if there is no such
     * parameter
     * @throws IllegalArgumentException if the parameter is not an IN or INOUT
     * parameter or if there is no such parameter
     */
    public JobParameter getJobParameter(String name)
                                        throws IllegalArgumentException;
  
    /**
     * Update the runtime with the given {@link JobParameter}. The  
     * parameter is immediately updated in the database.
     * 
     * @param param JobParameter to update
     * @throws IllegalArgumentException if this is an IN parameter or it does
     * not match the job parameter definition of the job definition.
     * @throws SchedulerRuntimeException if there was a technical problem
     * setting the value
     */
    public void setJobParameter(JobParameter param) 
                                throws IllegalArgumentException, 
                                       SchedulerRuntimeException;

    /**
     *  Set a job parameter. Only for out and inout parameters
     * 
     *  @param name of the parameter
     * 
     * @exception ClassCastException if the value is of an incorrect type
     * @throws IllegalArgumentException if this is an IN parameter or it does
     * not match the job parameter definition of the job definition.
     * @throws SchedulerRuntimeException if there was a technical problem
     * setting the value
     */
    public void setJobParameterGeneric(String name, Object value)
                                       throws IllegalArgumentException, 
                                              ClassCastException,
                                              SchedulerRuntimeException;
  
    /** 
     * Get a job parameter.
     * 
     * @param name the name of the parameter
     * 
     * @return the parameter object or null if there is no such
     * parameter. The return value must be casted to the specified type.
     * 
     * @throws IllegalArgumentException if the parameter is not an IN or INOUT
     * parameter
     */
    public Object getJobParameterGeneric(String name)
                                            throws IllegalArgumentException;

    //------------------------------------------------------------------
    // Return code, job failures, and cancellation requests
    //------------------------------------------------------------------

    /**
     * Set the job return code.
     *  
     * @param returnCode the value for the return code.
     * @throws SchedulerRuntimeException if there was a technical problem
     * setting the return value
     */
    public void setReturnCode(short returnCode)
                                       throws SchedulerRuntimeException;
  
    /**
     * Returns true if the job has received a request for 
     * cancellation.
     * 
     * @return true if a requet for cancellation has been received, false
     * otherwise.
     * @throws SchedulerRuntimeException if there was a technical problem
     * retrieving the value
     */
    public boolean isCancelled() 
                         throws SchedulerRuntimeException;

    /**
     * This method should be called in order to indicate that this job 
     * should be set to the status {@link JobStatus#ERROR ERROR} 
     * after it has been completed. 
     */
    public void jobFailed();
    
    //------------------------------------------------------------------
    // Submitting child jobs
    //------------------------------------------------------------------

    /**
     * Submit a job for immediate execution. The new job is considered a child
     * job of this job.
     * 
     * @param jobDefId the job definition for the job to submit
     *
     * @return the {@link JobID} object representing id of the submitted job.
     * 
     * @exception ParameterValidationException if the provided parameters 
     * do not match the required parameters for the job
     * @exception NoSuchJobDefinitionException if there is no such job 
     * definition or the job definition is not deployed anymore
     * @exception SchedulerRuntimeException if there was a technical problem 
     * submitting the job.
     */
    public JobID executeJob(JobDefinitionID jobDefId)
                                   throws 
                                          ParameterValidationException,
                                          NoSuchJobDefinitionException,
                                          SchedulerRuntimeException;

    /**
     * Submit a job for immediate execution. The new job is considered a child
     * job of this job.
     * 
     * @param jobDefinitionName the job definition name for the job to submit
     *
     * @return the {@link JobID} object representing the if of the submitted job.
     * 
     * @exception ParameterValidationException if the provided parameters 
     * do not match the required parameters for the job
     * @exception NoSuchJobDefinitionException if there is no such job 
     * definition or the job definition is not deployed anymore
     * @exception SchedulerRuntimeException if there was a technical problem 
     * submitting the job.
     */
    public JobID executeJob(String jobDefinitionName)
                                 throws 
                                        ParameterValidationException,
                                        NoSuchJobDefinitionException,
                                        SchedulerRuntimeException;
    
    /**
     * Submit a job for immediate execution. A parameter set may optionally be specified. 
     * Schedule parameters may be specified by appropriate names in the parameter map.
     *
     * @param jobDefId the job definition for the job to submit
     * @param parameters an optional list of parameter/value pairs.
     *
     * @return the {@link JobID} object representing the id of the submitted job.
     *
     * @exception ParameterValidationException if the provided parameters 
     * do not match the required parameters for the job
     * @exception NoSuchJobDefinitionException if there is no such job 
     * definition or the job definition is not deployed anymore
     * @exception SchedulerRuntimeException if there was a technical problem 
     * submitting the job.
     */
    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] parameters)
                                                       throws 
                                                              ParameterValidationException,
                                                              NoSuchJobDefinitionException,
                                                              SchedulerRuntimeException;

    /**
     * Submit a job for immediate execution. A parameter set may optionally be specified. 
     * Schedule parameters may be specified by appropriate names in the parameter map.
     *
     * @param jobDefinitionName the job definition name for the job to submit
     * @param parameters an optional list of parameter/value pairs.
     *
     * @return the {@link JobID} object representing the id of the submitted job.
     *
     * @exception ParameterValidationException if the provided parameters 
     * do not match the required parameters for the job
     * @exception NoSuchJobDefinitionException if there is no such job 
     * definition or the job definition is not deployed anymore
     * @exception SchedulerRuntimeException if there was a technical problem 
     * submitting the job.
     */
    public JobID executeJob(String jobDefinitionName, JobParameter[] parameters)
                                                     throws 
                                                            ParameterValidationException,
                                                            NoSuchJobDefinitionException,
                                                            SchedulerRuntimeException;

    //------------------------------------------------------------------
    // Child jobs join
    //------------------------------------------------------------------
  
    /**
     * Wait for all child jobs to finish.
     */ 
    public void waitForChildJobs();

    /**
     * Wait for all child jobs to finish.
     * 
     * @param timeout timeout for waiting, zero means no timeout
     * 
     * @return true if all child jobs have been completed in the given
     * timeout period, false otherwise
     */ 
    public boolean waitForChildJobs(long timeout);

    /**
     * Wait for the specified child jobs to wait for.
     * 
     * @param jobs an array of child job ids
     */
    public void waitForChildJobs(JobID[] jobs);

    /**
     * Wait for the specified child jobs to complete.
     * 
     * @param jobs an array of child jobs to wait for
     * @param timeout timeout for waiting, zero means no timeout

     * @return true if all child jobs have been completed in the given
     *         timeout period, false otherwise
     */
    public boolean waitForChildJobs(JobID[] jobs, long timeout);
}
