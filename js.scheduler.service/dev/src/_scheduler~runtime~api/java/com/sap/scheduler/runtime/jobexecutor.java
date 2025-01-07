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

import com.sap.scheduler.api.SchedulerTaskID;


/**
 * <b>Note: This is not a public API. It may be modified or can 
 * disappear without notice.</b>
 * <p>
 * Job executors implement this interface. There is on job executor
 * for each job type. 
 * 
 * @author Dirk Marwinski
 *
 */
public interface JobExecutor {


    /**
     * Submit a job for immediate execution
     * 
     * @param defID job definition id for the new job
     * @param jobParameters parameters for the job
     * @param retentionPeriod if null the retention period will be taken 
     * from the job definition, otherwise this value will be taken for the new 
     * job 
     * @param parentID this is the id of the parent job if this job was 
     * executed by another job, null otherwise 
     * @param schedulerId the scheduler which triggered this job. If this is
     * a child job the scheduler of the parent job is used here.
     * @param runAsUser
     * @param schedTaskID the corresponding SchedulerTaskID (may be null)
     * @param vendorData the vendor specific data (in case JXBP is used). This
     * value can be null
     * 
     * @return job id
     * @exception JobExecutorException if there is a technical problem 
     *            executing the job
     * @exception NoSuchJobDefinitionException if there is not such job 
     *            definition or if the job definition is not longer available
     * @exception ParameterValidationException if the specified parameters 
     *            do not match the required parameters for the job definition
     */
    public JobID executeJob(JobDefinitionID defID,
                             JobParameter[] jobParameters,
                             Integer retentionPeriod,
                             JobID parentID,
                             SchedulerID schedulerId,
                             String runAsUser,
                             SchedulerTaskID schedTaskID,
                             String vendorData)
                                         throws NoSuchJobDefinitionException,
                                                ParameterValidationException,
                                                JobExecutorException;


    /**
     * Submit a job for immediate execution
     * 
     * @param defID job definition id for the new job
     * @param jobParameters parameters for the job
     * @param retentionPeriod if null the retention period will be taken 
     * from the job definition, otherwise this value will be taken for the new 
     * job 
     * @param parentID this is the id of the parent job if this job was 
     * executed by another job, null otherwise 
     * @param schedulerId the scheduler which triggered this job. If this is
     * a child job the scheduler of the parent job is used here.
     * @param runAsUser
     * @param schedTaskID the corresponding SchedulerTaskID (may be null)
     * @param vendorData the vendor specific data (in case JXBP is used). This
     * value can be null
     * @param jobID job id to be assigned to the job
     * 
     * @return job id
     * @exception JobExecutorException if there is a technical problem 
     *            executing the job
     * @exception NoSuchJobDefinitionException if there is not such job 
     *            definition or if the job definition is not longer available
     * @exception ParameterValidationException if the specified parameters 
     *            do not match the required parameters for the job definition
     */
    public JobID executeJob(JobDefinitionID defID,
                             JobParameter[] jobParameters,
                             Integer retentionPeriod,
                             JobID parentID,
                             SchedulerID schedulerId,
                             String runAsUser,
                             SchedulerTaskID schedTaskID,
                             String vendorData,
                             JobID jobID)
                                         throws NoSuchJobDefinitionException,
                                                ParameterValidationException,
                                                JobExecutorException;

}
