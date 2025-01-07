/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.runtime.ejb.api;

import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIllegalStateException;
import com.sap.scheduler.runtime.JobIterator;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobDefinitionException;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.ParameterValidationException;
import com.sap.scheduler.spi.JXBP;
import com.sap.scheduler.spi.JXBPException;


public interface JXBPLocal extends JXBP {

    public String getSystemTimeZone();

    public String getVersion();

    public JobDefinition[] getJobDefinitions()
                                      throws JXBPException;

    public JobDefinition getJobDefinitionByName(String jobDefinitionName)
                                                         throws JXBPException;

    public JobDefinition getJobDefinitionById(JobDefinitionID id)
                                                   throws JXBPException;

    //------------------------------------------------------------------
    // Starting and stopping jobs
    //------------------------------------------------------------------

    public JobID executeJob(JobDefinitionID jobDefId,
    						com.sap.scheduler.spi.JobParameterWS[] jobParameters,
                            Integer retentionPeriod)
                                         throws ParameterValidationException,
                                                NoSuchJobDefinitionException,
                                                JXBPException;

    public JobID executeJob(JobDefinitionID jobDefId, 
    		com.sap.scheduler.spi.JobParameterWS[] jobParametersWS,
            Integer retentionPeriod,
            String vendorData) 
                       throws ParameterValidationException,
                              NoSuchJobDefinitionException,
                              JXBPException;
    
    
    public void cancelJob(JobID jobid)
                             throws JobIllegalStateException,
                                    NoSuchJobException,
                                    JXBPException;


    public void holdJob(JobID jobid)
                                throws NoSuchJobException,
                                       JobIllegalStateException,
                                        JXBPException;

    public void releaseJob(JobID jobid)
                                throws JobIllegalStateException,
                                       NoSuchJobException,
                                       JXBPException;

    public Job[] getChildJobs(JobID jobid)
                                    throws NoSuchJobException,
                                           JXBPException;

    public boolean hasChildJobs(JobID jobid)
                                  throws NoSuchJobException,
                                         JXBPException;

    public boolean[] haveChildJobs(JobID[] jobid)
                                       throws JXBPException;

    public void removeJob(JobID jobid)
                                throws JobIllegalStateException,
                                       NoSuchJobException,
                                       JXBPException;

    public void removeJobs(JobID[] jobids)
                              throws JXBPException;

    Job getJob(JobID jobid)
                     throws JXBPException;

    JobIterator getJobs(JobFilter filter, JobIterator iter, int fetchSize)
                                                           throws JXBPException;

    com.sap.scheduler.spi.JobParameterWS[] getJobParameters(JobID jobid)
                                throws NoSuchJobException,
                                       JXBPException;

    Job[] getJobs(JobID[] jobid)
                         throws JXBPException;

    LogIterator getJobLog(JobID jobid, LogIterator iter, int fetchSize)
                                                         throws NoSuchJobException,
                                                                JXBPException;

    void removeJobLog(JobID jobid)
                            throws NoSuchJobException,
                                   JobIllegalStateException,
                                   JXBPException;

    public JobStatus getJobStatus(JobID jobid)
                                     throws NoSuchJobException,
                                            JXBPException;

    public void setVendorData(JobID[] ids, String value)
                                          throws JXBPException;

    public void setVendorData(JobID id, String value)
                                         throws NoSuchJobException,
                                                JXBPException;

    public String[] getVendorData(JobID[] jobIds)
                                         throws JXBPException;


    public Event[] getUnhandledEvents(int fetchSize)
                                       throws JXBPException;

    public void  clearEvents()
                        throws JXBPException;

    public void setFilter(String[] eventType)
                                   throws JXBPException;

    public String[] getJXBPRuntimeEventTypes();

}



