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
package com.sap.scheduler.api;


import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import com.sap.scheduler.api.Scheduler.FireTimeEvent;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIllegalStateException;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.SchedulerLogRecordIterator;
import com.sap.scheduler.runtime.SchedulerRuntimeException;

/**
 * @author Hristo Sabev (i027642)
 
 */
public interface SchedulerAdministrator {
	public void schedule(SchedulerTask task, String runAsUser) throws TaskValidationException;

	public SchedulerTask getTask(SchedulerTaskID taskId) throws TaskDoesNotExistException;

	public void cancelTask(SchedulerTaskID taskId) throws TaskDoesNotExistException;
    
    public void holdTask(SchedulerTaskID taskId) throws TaskDoesNotExistException;
    
    public void releaseTask(SchedulerTaskID taskId) throws TaskDoesNotExistException;

	public void setFilters(SchedulerTaskID taskId, Filter[] f) throws TaskDoesNotExistException;

	public void addFilters(SchedulerTaskID taskId, Filter[] f) throws TaskDoesNotExistException;

	public void removeFilters(SchedulerTaskID taskId, Filter[] f) throws TaskDoesNotExistException;

	public SchedulerTaskID[] getAllSchedulerTaskIds();
	
	public FireTimeEvent[] getFireTimes(SchedulerTaskID id, SchedulerTime startTime, SchedulerTime endTime)
    throws TaskDoesNotExistException, TooManyFireEventsException, SchedulerRuntimeException;
	
	public FireTimeEvent[] getFireTimes(SchedulerTime startTime, SchedulerTime endTime)
	throws TooManyFireEventsException, SchedulerRuntimeException;
    
    public FireTimeEvent[] getFireTimes(SchedulerTime startTime, SchedulerTime endTime, int fetchSize)
    throws TooManyFireEventsException, SchedulerRuntimeException;
    
    public LogIterator getJobLog(JobID arg0, LogIterator arg1, int arg2) throws NoSuchJobException;
    
    public SchedulerLogRecordIterator getJobLogRecords(JobID arg0, SchedulerLogRecordIterator arg1, int arg2) throws NoSuchJobException;
    
    public SchedulerTask[] getAllSchedulerTasks();
    
    public void cancelJob(JobID jobid) throws JobIllegalStateException,
                                              SchedulerRuntimeException;
    
    public int removeSchedulerTasks(Timestamp ts) throws SchedulerRuntimeException;
    
    public Date getServerTime() throws SchedulerRuntimeException;
    
    
    // -------------------------------------------------------------------------
    // in cause that this interface is completely internal, we provide also 
    // analysis methods used by the telnet commands
    // -------------------------------------------------------------------------
    
    public Map<String, String[]> getClusterLayout() throws SchedulerRuntimeException;
    
    public SchedulerTask[] getAllSchedulerTasksFromPriorityQueue() throws SchedulerRuntimeException;
    
    public SchedulerTask[] getAllSchedulerTasks(TaskStatus status) throws SchedulerRuntimeException;
    
    public Map getTaskStatusDescriptions() throws SchedulerRuntimeException;
    
}
