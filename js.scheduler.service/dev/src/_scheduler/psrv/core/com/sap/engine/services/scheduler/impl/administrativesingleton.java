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
package com.sap.engine.services.scheduler.impl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Map;

import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.api.TaskStatus;
import com.sap.scheduler.api.TaskValidationException;
import com.sap.scheduler.api.TooManyFireEventsException;
import com.sap.scheduler.api.Scheduler.FireTimeEvent;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.SchedulerLogRecordIterator;
import com.sap.scheduler.runtime.SchedulerRuntimeException;

/**
 * @author Hristo Sabev (i027642)
 * 
 * Note: This interface is completely internal and must only be accessed in the 
 * context of an Administrator!
 * 
 */
public interface AdministrativeSingleton extends Remote {
	public void schedule(SchedulerTask task, String runAsUser)
	throws TaskValidationException, RemoteException;

	public SchedulerTask getTask(SchedulerTaskID taskId)
	throws TaskDoesNotExistException, RemoteException;

	public void cancelTask(SchedulerTaskID taskId)
	throws TaskDoesNotExistException, RemoteException;
    
    public void holdTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, RemoteException;
    
    public void releaseTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, RemoteException;

	public void setFilters(SchedulerTaskID taskId, Filter[] f)
	throws TaskDoesNotExistException, RemoteException;

	public void addFilters(SchedulerTaskID taskId, Filter[] f)
	throws TaskDoesNotExistException, RemoteException;

	public void removeFilters(SchedulerTaskID taskId, Filter[] f)
	throws TaskDoesNotExistException, RemoteException;

	public SchedulerTaskID[] getAllSchedulerTaskIds()
	throws RemoteException;
	
	public FireTimeEvent[] getFireTimes(SchedulerTaskID arg0,
			SchedulerTime arg1, SchedulerTime arg2)
			throws TaskDoesNotExistException, TooManyFireEventsException,
			SchedulerRuntimeException, RemoteException;
	
	public FireTimeEvent[] getFireTimes(SchedulerTime arg0, SchedulerTime arg1)
			throws TooManyFireEventsException, SchedulerRuntimeException, RemoteException;
    
    public FireTimeEvent[] getFireTimes(SchedulerTime arg0, SchedulerTime arg1, int fetchSize)
            throws TooManyFireEventsException, SchedulerRuntimeException, RemoteException;
    
    public LogIterator getJobLog(JobID jobId, LogIterator logIter, int resultSetSize) throws NoSuchJobException, RemoteException;
    
    public SchedulerLogRecordIterator getJobLogRecords(JobID jobId, SchedulerLogRecordIterator logIter, int resultSetSize) throws NoSuchJobException, RemoteException;
    
    public SchedulerTask[] getAllSchedulerTasks() throws RemoteException;
    
    public int removeSchedulerTasks(Timestamp ts) throws RemoteException;
    
    // -------------------------------------------------------------------------
    // in cause that this interface is completely internal, we provide also 
    // analysis methods used by the telnet commands
    // -------------------------------------------------------------------------
    public SchedulerTask[] getAllSchedulerTasksFromPriorityQueue() throws RemoteException;
    
    public SchedulerTask[] getAllSchedulerTasks(TaskStatus status) throws RemoteException;
    
    public Map getTaskStatusDescriptions() throws RemoteException;
}
