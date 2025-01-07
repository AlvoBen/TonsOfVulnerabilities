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
package com.sap.engine.services.scheduler.runtime.executor;

import java.sql.SQLException;
import java.util.Hashtable;

import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduler.runtime.db.JobDefinitionHandler;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.runtime.*;

public class JobExecutorImpl implements JobExecutor {

    
    Hashtable mJobExecutors = new Hashtable();
    
    Environment mEnvironment;
    
    public JobExecutorImpl(Environment env) {
        mEnvironment = env;
    }
    
    
    
    
    public JobID executeJob(JobDefinitionID jobDefId, 
            JobParameter[] jobParameters, 
            Integer retentionPeriod,
            JobID parentId,
            SchedulerID schedulerId,
            String user, 
            SchedulerTaskID schedTaskID,
            String vendorData) 
                          throws NoSuchJobDefinitionException,
                                 ParameterValidationException,
                                 JobExecutorException {
        
        return executeJob(
                      jobDefId,
                      jobParameters,
                      retentionPeriod,
                      parentId,
                      schedulerId,
                      user,
                      schedTaskID,
                      vendorData,
                      null);
    }
        
    public JobID executeJob(JobDefinitionID jobDefId, 
                JobParameter[] jobParameters, 
                Integer retentionPeriod,
                JobID parentId, 
                SchedulerID schedulerId,
                String runAsUser,
                SchedulerTaskID schedTaskID,
                String vendorData,
                JobID jobId) throws NoSuchJobDefinitionException,
                                                    ParameterValidationException,
                                                    JobExecutorException {
    
        JobDefinitionHandler hdlr = mEnvironment.getJobDefinitionHandler();

        //----------------------------------------------------
        // Get job definition from database
        //----------------------------------------------------
        
        JobDefinition jd = null;
        try {
            jd = hdlr.getJobDefinitionById(jobDefId);
            if (jd==null) {
                throw new NoSuchJobDefinitionException("There is no job definition for job \"" + jobDefId.toString() + "\".");   
            }
        } catch (SQLException sql) {
            throw new JobExecutorException("Unable to retrieve job information. Job \""  + jobDefId.toString() + "\" cannot be started.", sql);
        }
        
        int jobType = jd.getJobType();
        
        JobExecutor je = (JobExecutor)mJobExecutors.get(new Integer(jobType));
        
        if (je == null) {
            throw new JobExecutorException("Unknwon job type \"" + jobType + "\".");    
        }
        
        return je.executeJob(jobDefId, jobParameters, retentionPeriod, parentId, schedulerId, runAsUser, schedTaskID,vendorData,jobId);
    }

    public void addJobExecutor(JobExecutor exe, int type) {
        mJobExecutors.put(new Integer(type), exe);
    }
}
