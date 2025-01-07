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
package com.sap.engine.services.scheduler.runtime;

import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

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
import com.sap.scheduler.runtime.NoSuchUserException;
import com.sap.scheduler.runtime.ParameterValidationException;
import com.sap.scheduler.runtime.SchedulerAlreadyDefinedException;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.SchedulerID;
import com.sap.scheduler.runtime.SchedulerLogRecordIterator;
import com.sap.scheduler.runtime.SchedulerRemoveException;
import com.sap.scheduler.runtime.UserAccountException;


public final class RuntimeIntfAccessAsserter implements JobExecutionRuntime {
    private final JobExecutionRuntime impl;

    public RuntimeIntfAccessAsserter(JobExecutionRuntime impl) {
        this.impl = impl;
    }

    public String getSystemTimeZone() {
        assertCallerAuthorized();
        return impl.getSystemTimeZone();
    }

    public JobDefinition[] getJobDefinitions() throws SQLException {
        assertCallerAuthorized();
        return impl.getJobDefinitions();
    }

    public JobDefinition getJobDefinitionByName(String jobDefinitionName) throws SQLException {
        assertCallerAuthorized();
        return impl.getJobDefinitionByName(jobDefinitionName);
    }

    public JobDefinition getJobDefinitionByName(JobDefinitionName jobDefinitionName) throws SQLException {
        assertCallerAuthorized();
        return impl.getJobDefinitionByName(jobDefinitionName);
    }
    
    public JobDefinition getJobDefinitionById(JobDefinitionID id) throws SQLException {
        assertCallerAuthorized();
        return impl.getJobDefinitionById(id);
    }

    public void cancelJob(JobID jobid) throws NoSuchJobException, JobIllegalStateException, SQLException {
        assertCallerAuthorized();
        impl.cancelJob(jobid);
    }

    public Job[] getChildJobs(JobID jobid) throws NoSuchJobException,SQLException {
        assertCallerAuthorized();
        return impl.getChildJobs(jobid);
    }

    public boolean hasChildJobs(JobID jobid) throws NoSuchJobException, SQLException {
        assertCallerAuthorized();
        return impl.hasChildJobs(jobid);
    }

    public boolean[] haveChildJobs(JobID[] jobid) throws SQLException {
        assertCallerAuthorized();
        return impl.haveChildJobs(jobid);
    }

    public void removeJob(JobID jobid) throws NoSuchJobException, JobIllegalStateException, SQLException {
        assertCallerAuthorized();
        impl.removeJob(jobid);
    }

    public void removeJob(JobID jobid, boolean force) throws NoSuchJobException, JobIllegalStateException, SQLException {
        assertCallerAuthorized();
        impl.removeJob(jobid, force);
    }
    
    public void removeJobs(Job[] jobIds) throws SQLException {
        assertCallerAuthorized();
        impl.removeJobs(jobIds);
    }
    
    public void removeJobs(Job[] jobIds, boolean force) throws SQLException {
        assertCallerAuthorized();
        impl.removeJobs(jobIds, force);
    }    

    public void removeJobs(JobID[] jobids) throws SQLException {
        assertCallerAuthorized();
        impl.removeJobs(jobids);
    }

    public Job getJob(JobID jobid) throws SQLException {
        assertCallerAuthorized();
        return impl.getJob(jobid);
    }

    public JobParameter[] getJobParameters(JobID jobid) throws NoSuchJobException, SQLException {
        assertCallerAuthorized();
        return impl.getJobParameters(jobid);
    }

    public Job[] getJobs(JobID[] jobid) throws SQLException {
        assertCallerAuthorized();
        return impl.getJobs(jobid);
    }

    public LogIterator getJobLog(JobID jobid, LogIterator it, int fetchSize) throws SQLException, NoSuchJobException {
        assertCallerAuthorized();
        return impl.getJobLog(jobid, it, fetchSize);
    }
    
    public SchedulerLogRecordIterator getJobLogRecords(JobID jobid, SchedulerLogRecordIterator it, int fetchSize) throws SQLException, NoSuchJobException {
        assertCallerAuthorized();
        return impl.getJobLogRecords(jobid, it, fetchSize);
    }

    public void removeJobLog(JobID jobid) throws SQLException, JobIllegalStateException, NoSuchJobException {
        assertCallerAuthorized();
        impl.removeJobLog(jobid);
    }

    public JobStatus getJobStatus(JobID jobid) throws NoSuchJobException, SQLException {
        assertCallerAuthorized();
        return impl.getJobStatus(jobid);
    }

    public void setVendorData(SchedulerDefinition def, JobID[] ids, String value) throws SQLException {
        assertCallerAuthorized();
        impl.setVendorData(def, ids, value);
    }

    public String[] getVendorData(JobID[] jobIds) throws SQLException {
        assertCallerAuthorized();
        return impl.getVendorData(jobIds);
    }

    public Event[] getEvents(EventSubscriber es, int fetchSize) {
        assertCallerAuthorized();
        return impl.getEvents(es, fetchSize);
    }

    public void clearEvents(EventSubscriber es)  {
        assertCallerAuthorized();
        impl.clearEvents(es);
    }

    public void setFilter(SchedulerDefinition def, String[] eventType) throws SQLException {
        assertCallerAuthorized();
        impl.setFilter(def, eventType);
    }

    public String[] getJXBPRuntimeEventTypes() {
        assertCallerAuthorized();
        return impl.getJXBPRuntimeEventTypes();
    }

    public SchedulerDefinition getSchedulerForUser(String principal) throws SQLException {
        assertCallerAuthorized();
        return impl.getSchedulerForUser(principal);
    }

    public SchedulerDefinition getSchedulerByName(String name) throws SQLException {
        assertCallerAuthorized();
        return impl.getSchedulerByName(name);
    }
    
    public SchedulerDefinition getSchedulerById(SchedulerID id) throws SQLException {
        assertCallerAuthorized();
        return impl.getSchedulerById(id);
    }

    public SchedulerDefinition addScheduler(
                            String name, 
                            String user, 
                            String userPassword,
                            String description,
                            long inactivityGracePeriod,
                            String[] events)
                                        throws SQLException,
                                        UserAccountException,
                                        SchedulerAlreadyDefinedException {
        assertCallerAuthorized();
        return impl.addScheduler(name, user, userPassword, description, inactivityGracePeriod, events);
    }
    
    
    public void removeScheduler(SchedulerDefinition def) throws SchedulerRemoveException, SQLException {
    	assertCallerAuthorized();
    	impl.removeScheduler(def);    	
    }


    public SchedulerDefinition[] getAllSchedulers() throws SQLException {
        assertCallerAuthorized();
        return impl.getAllSchedulers();
    }
    
    public void deactivateScheduler(SchedulerID id) 
                                      throws SQLException {
        assertCallerAuthorized();
        impl.deactivateScheduler(id);
    }


    public JobID executeJob(JobDefinitionID defId, JobParameter[] jobParameters, JobID parentId, SchedulerID schedulerId, SchedulerTaskID schedTaskID) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        assertCallerAuthorized();
        return impl.executeJob(defId, jobParameters, parentId, schedulerId, schedTaskID);
    }
    public JobID executeJob(JobDefinitionID defId, JobParameter[] jobParameters, JobID parentId, SchedulerID schedulerId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        assertCallerAuthorized();
        return impl.executeJob(defId, jobParameters, parentId, schedulerId);
    }
    

    public JobID executeJob(JobDefinitionID defId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, SchedulerID schedulerId, SchedulerTaskID schedTaskID) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        assertCallerAuthorized();
        return impl.executeJob(defId, jobParameters, retentionPeriod, parentId, schedulerId, schedTaskID);
    }
    public JobID executeJob(JobDefinitionID defId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, SchedulerID schedulerId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        assertCallerAuthorized();
        return impl.executeJob(defId, jobParameters, retentionPeriod, parentId, schedulerId);
    }
    
    
    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, SchedulerID schedulerId, String runAsUser, SchedulerTaskID schedTaskID) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException, NoSuchUserException {
        assertCallerAuthorized();
        return impl.executeJob(jobDefId, jobParameters, retentionPeriod, parentId, schedulerId, runAsUser, schedTaskID);
    }
    public JobID executeJob(JobDefinitionID jobDefId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, SchedulerID schedulerId, String runAsUser) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException, NoSuchUserException {
        assertCallerAuthorized();
        return impl.executeJob(jobDefId, jobParameters, retentionPeriod, parentId, schedulerId, runAsUser);
    }
    
    
    public JobID executeJob(JobDefinitionID defId, JobParameter[] jobParameters, JobID parentId, SchedulerTaskID schedTaskID) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        assertCallerAuthorized();
        return impl.executeJob(defId, jobParameters, parentId, schedTaskID);
    }
    public JobID executeJob(JobDefinitionID defId, JobParameter[] jobParameters, JobID parentId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        assertCallerAuthorized();
        return impl.executeJob(defId, jobParameters, parentId);
    }
    
    
    public JobID executeJob(JobDefinitionID defId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId, SchedulerTaskID schedTaskID) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        assertCallerAuthorized();
        return impl.executeJob(defId, jobParameters, retentionPeriod, parentId, schedTaskID);
    }
    public JobID executeJob(JobDefinitionID defId, JobParameter[] jobParameters, Integer retentionPeriod, JobID parentId) 
            throws JobExecutorException, ParameterValidationException, NoSuchJobDefinitionException {
        assertCallerAuthorized();
        return impl.executeJob(defId, jobParameters, retentionPeriod, parentId);
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

        assertCallerAuthorized();
        return impl.executeJobJXBP(jobDefId, jobParameters, retentionPeriod, parentId, schedulerId, vendorData);
    }

    public JobID executeJob(JobDefinitionID jobDefId,
            JobParameter[] jobParameters,
            JobID parentId,
            SchedulerID schedulerId,
            JobID jobId) 
                        throws JobExecutorException,
                               ParameterValidationException,
                               NoSuchJobDefinitionException {
        assertCallerAuthorized();
        return impl.executeJob(jobDefId, jobParameters, parentId, schedulerId, jobId);
    }


    public void setReturnCode(JobID jobId, short returnCode) throws NoSuchJobException, SQLException {
        assertCallerAuthorized();
        impl.setReturnCode(jobId, returnCode);
    }

    public void setJobParameter(JobID jobid, JobParameter param) throws NoSuchJobException, IllegalArgumentException, SQLException {
        assertCallerAuthorized();
        impl.setJobParameter(jobid, param);
    }

    public JobParameterDefinition[] getJobParameterDefinition(String jobDefinitionName) throws NoSuchJobDefinitionException, SQLException {
        assertCallerAuthorized();
        return impl.getJobParameterDefinition(jobDefinitionName);
    }

    public Event waitForEvent(EventSubscriber sub) {
        assertCallerAuthorized();
        return impl.waitForEvent(sub);
    }
    
    public Event waitForEvent(EventSubscriber sub, long timeout) {
        assertCallerAuthorized();
        return impl.waitForEvent(sub, timeout);
    }

    public EventSubscriber getEventSubscriberByID(AbstractIdentifier esId) throws SQLException {
        assertCallerAuthorized();
        return impl.getEventSubscriberByID(esId);
    }

    public SchedulerDefinition getBuiltinScheduler()
                                          throws SQLException {
        assertCallerAuthorized();
        return impl.getBuiltinScheduler();
    }

    private void assertCallerAuthorized() {

    }

    public JobIterator getJobs(JobFilter filter, JobIterator iter, int fetchSize)
                                                                    throws SQLException {
        assertCallerAuthorized();
        return impl.getJobs(filter, iter, fetchSize);
    }
    
    public boolean isJobCancelled(JobID id)
                                    throws SQLException {
        assertCallerAuthorized();
        return impl.isJobCancelled(id);
    }

    public void registerEventSubscriber(EventSubscriber sub) throws SQLException {

        assertCallerAuthorized();
        impl.registerEventSubscriber(sub);

    }
    public void verifyParameters(JobDefinitionID jobDefinitionId, JobParameter[] parameters)
    throws ParameterValidationException, NoSuchJobDefinitionException, SQLException {
        assertCallerAuthorized();
    	impl.verifyParameters(jobDefinitionId, parameters);	
	}	

    public JobDefinition[] removeJobDefinitions(JobDefinitionID[] ids) 
                                                           throws SQLException {
        assertCallerAuthorized();
        return impl.removeJobDefinitions(ids);
    }

    public void updateTimestamp(SchedulerID id) {
        assertCallerAuthorized();
        impl.updateTimestamp(id);
    }
    
    
    public Properties getEventTypes() {
        assertCallerAuthorized();
        return impl.getEventTypes(); 
    }
    
    public void unregisterEventSubscriber(EventConsumer consumer) throws SQLException {
        assertCallerAuthorized();
        impl.unregisterEventSubscriber(consumer);
    }
    
    public void raiseEvent(String type, String parameter, String additionalParameter, Date raisedDate, AbstractIdentifier raisedByDetails) {
        assertCallerAuthorized();
        impl.raiseEvent(type, parameter, additionalParameter, raisedDate, raisedByDetails);
    }

    
    public int updateEndedNullValues() 
                           throws SQLException {
        assertCallerAuthorized();
        return impl.updateEndedNullValues();
    }
    
}
