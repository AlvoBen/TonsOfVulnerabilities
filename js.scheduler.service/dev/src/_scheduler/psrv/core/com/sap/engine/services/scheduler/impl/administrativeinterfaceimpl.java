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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

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
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Hristo Sabev (i027642)
 * @author d040949
 * @author d040939
 * 
 * The public methods of this class implementing a check that it can only be executed 
 * if the calling user is member of the Administrator group.
 * Administrators see all Tasks of all users, also the ones of other Administrators.
 * 
 * If the calling user is not an Administrator an exception will be thrown.
 * 
 * Note: This impl of the interface AdministrativeSingleton is completely internal 
 * and must only be accessed in the context of an Administrator!
 *
 */
public class AdministrativeInterfaceImpl implements AdministrativeSingleton  {
	
	private static final Location location = Location.getLocation(AdministrativeInterfaceImpl.class);
	
	private final ServiceFrame m_service;
	private final Scheduler m_scheduler;
    private final SingletonEnvironment m_env;
    
	
	AdministrativeInterfaceImpl(SingletonEnvironment env) {
        this.m_env = env;
		this.m_service = env.getServiceFrame();
		this.m_scheduler = env.getScheduler();
	}
	
	
	public void schedule(SchedulerTask task, String runAsUser) throws TaskValidationException {
        if (location.beDebug()) location.debugT("schedule(SchedulerTask task, String runAsUser) for task with taskId "+task.getTaskId()+" entered");
        
		final String caller = assertAuthorized();
        // at this point we know that the user an Administrator --> persist the task
        // with this Administrator-user
        m_scheduler.schedule(caller , runAsUser, task);
	}


	public SchedulerTask getTask(SchedulerTaskID taskId) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("SchedulerTask getTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");
        
		assertAuthorized();
		// User is Administrator --> call method with user 'null'
		return m_scheduler.getTask(null, taskId);
	}

	public void cancelTask(SchedulerTaskID taskId) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("cancelTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");

		assertAuthorized();
        // User is Administrator --> call method with user 'null'
        m_scheduler.cancelTask(null, taskId, TaskStatusInternal.finishedCancelledAPI);
	}

    public void holdTask(SchedulerTaskID taskId) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("holdTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");

        assertAuthorized();
        // User is Administrator --> call method with user 'null'
        m_scheduler.holdTask(null, taskId, TaskStatusInternal.holdApi);
    }
    
    public void releaseTask(SchedulerTaskID taskId) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("releaseTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");

        assertAuthorized();
        // User is Administrator --> call method with user 'null'
        m_scheduler.releaseTask(null, taskId);
    }

	public void setFilters(SchedulerTaskID taskId, Filter[] f) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("setFilters(SchedulerTaskID taskId, Filter[] f) with taskId "+taskId+" entered");

		assertAuthorized();
		// User is Administrator --> call method with user 'null'
        m_scheduler.setFilters(null, taskId, f);
		
	}

	public void addFilters(SchedulerTaskID taskId, Filter[] f) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("addFilters(SchedulerTaskID taskId, Filter[] f) with taskId "+taskId+" entered");

		assertAuthorized();
		// User is Administrator --> call method with user 'null'
        m_scheduler.addFilters(null, taskId, f);
		
	}

	public void removeFilters(SchedulerTaskID taskId, Filter[] f) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("removeFilters(SchedulerTaskID taskId, Filter[] f) with taskId "+taskId+" entered");

		assertAuthorized();
		// User is Administrator --> call method with user 'null'
        m_scheduler.removeFilters(null, taskId, f);
	}

	public SchedulerTaskID[] getAllSchedulerTaskIds() {
        if (location.beDebug()) location.debugT("SchedulerTaskID[] getAllSchedulerTaskIds()");

		assertAuthorized();
        // User is Administrator --> call method with user 'null'
		return m_scheduler.getAllSchedulerTaskIDs(null);
	}
	
	public FireTimeEvent[] getFireTimes(SchedulerTaskID taskId, SchedulerTime startTime, SchedulerTime endTime) throws TaskDoesNotExistException, TooManyFireEventsException, SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("FireTimeEvent[] getFireTimes(SchedulerTaskID taskId, SchedulerTime startTime, SchedulerTime endTime) with taskId "+taskId+" entered");

        assertAuthorized();
        // User is Administrator --> call method with user 'null'
        FireTimeEvent[] events = m_scheduler.getFireTimes(null, taskId, startTime, endTime);     
        TreeSet<FireTimeEvent> sortedSet = sort(events);
        
        return getSortedArrayFromTreeSet(sortedSet);
    }

    
    public FireTimeEvent[] getFireTimes(SchedulerTime startTime, SchedulerTime endTime, int fetchSize) throws TooManyFireEventsException, SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("FireTimeEvent[] getFireTimes(SchedulerTime startTime, SchedulerTime endTime, int fetchSize) entered");

        assertAuthorized();
        
        if ( fetchSize < Scheduler.FETCH_SIZE_IGNORE ) {
            throw new SchedulerRuntimeException("Fetch size smaller than '"+Scheduler.FETCH_SIZE_IGNORE+"' is not allowed.");
        }
        
        // result we get is already sorted
        FireTimeEvent[] sortedEvents = getFireTimes(startTime, endTime);        
        
        // consider fetchSize
        if ( fetchSize != Scheduler.FETCH_SIZE_IGNORE && fetchSize < sortedEvents.length ) {
            FireTimeEvent[] newSortedEvents = new FireTimeEvent[fetchSize];            
            System.arraycopy(sortedEvents, 0, newSortedEvents, 0, fetchSize);
            return newSortedEvents;            
        } else {
            return sortedEvents;
        }
    }
    
    
	public FireTimeEvent[] getFireTimes(SchedulerTime startTime, SchedulerTime endTime) throws TooManyFireEventsException, SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("FireTimeEvent[] getFireTimes(SchedulerTime startTime, SchedulerTime endTime) entered");

		assertAuthorized();
        
        // calculate the events only for tasks which are not in status hold
        SchedulerTask[] allTasks = getAllSchedulerTasks(TaskStatus.active);

        // init sorted TreeSet
        TreeSet<FireTimeEvent> allEventsTreeSet = sort(new FireTimeEvent[0]);
		for (int i = 0; i < allTasks.length; i++) {
			try {
				FireTimeEvent[] currentTaskEvents = getFireTimes(allTasks[i].getTaskId(), startTime, endTime);
				if (currentTaskEvents.length + allEventsTreeSet.size() > Scheduler.maxFireEventsNumber)
					throw new TooManyFireEventsException("The number of calculted fire events for all tasks grew" +
							" over the maximum allowed number of " + Scheduler.maxFireEventsNumber +
							". The allowed number of fire time events was exceeded while calculing events for task: " + 
                            allTasks[i].getTaskId());
				//allFireEvents = merge(allFireEvents, currentTaskEvents);
                for (int j = 0; j < currentTaskEvents.length; j++) {
                    allEventsTreeSet.add(currentTaskEvents[j]);
                }
			} catch (TaskDoesNotExistException tdne) {
				location.traceThrowableT(Severity.DEBUG, "Task was reported to not exist while calculating" +
						" fire time events of all tasks. However the task id was selected when the list of all" +
						" task was queried. Probably the task has been deleted between the moments of selecting" +
						" the list of all tasks and the moment of fire time events calculation for this task." +
						" Task id is: " + allTasks[i].getTaskId(), tdne);
			}
		}
		return getSortedArrayFromTreeSet(allEventsTreeSet);
        //return allFireEvents;
	}
    
    
    public LogIterator getJobLog(JobID jobId, LogIterator logIter, int resultSetSize) throws NoSuchJobException {
        if (location.beDebug()) location.debugT("LogIterator getJobLog(JobID jobId, LogIterator logIter, int resultSetSize) for jobId "+jobId+" entered");

        assertAuthorized();
        return m_scheduler.getJobLog(jobId, logIter, resultSetSize);
    } 
    
    
    public SchedulerLogRecordIterator getJobLogRecords(JobID jobId, SchedulerLogRecordIterator logIter, int resultSetSize) throws NoSuchJobException {
        if (location.beDebug()) location.debugT("SchedulerLogRecordIterator getJobLogRecords(JobID jobId, SchedulerLogRecordIterator logIter, int resultSetSize) with jobId "+jobId+" entered");

        assertAuthorized();
        return m_scheduler.getJobLogRecords(jobId, logIter, resultSetSize);
    } 
    
    
    public SchedulerTask[] getAllSchedulerTasks() {
        if (location.beDebug()) location.debugT("SchedulerTask[] getAllSchedulerTasks() entered");

        assertAuthorized();
        return m_scheduler.getAllSchedulerTasks();
    }
    
    public int removeSchedulerTasks(Timestamp ts) throws SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("removeSchedulerTasks(Timestamp ts) entered");

        assertAuthorized();
        return m_scheduler.removeSchedulerTasks(ts);
    }
    
    
    // -------------------------------------------------------------------------
    // in cause that this interface is completely internal, we provide also 
    // analysis methods used by the telnet commands
    // -------------------------------------------------------------------------
    public SchedulerTask[] getAllSchedulerTasksFromPriorityQueue() {
        if (location.beDebug()) location.debugT("SchedulerTask[] getAllSchedulerTasksFromPriorityQueue() entered");

        assertAuthorized();
        return m_scheduler.getAllSchedulerTasksFromPriorityQueue();
    }
    
    public SchedulerTask[] getAllSchedulerTasks(TaskStatus status) throws SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("SchedulerTask[] getAllSchedulerTasks(TaskStatus status) woth TaskStatus "+status.toString()+" entered");

        assertAuthorized();
        return m_scheduler.getAllSchedulerTasks(null, status);
    }
    
    public Map getTaskStatusDescriptions() throws SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("Map getTaskStatusDescriptions() entered");

        assertAuthorized();
        return TaskStatusInternal.STATUS_CHANGED_MAP;
    }
    
    
    // -------------------------------------------------------------------------
    // --------------- Private helper methods ----------------------------------
    // -------------------------------------------------------------------------
    
    private String assertAuthorized() {
        String caller = m_env.getCaller();
        m_env.assertAuthorizedAdministrativeMethods(caller);
        
        return caller;
    }
    
    
    private TreeSet<FireTimeEvent> sort(FireTimeEvent[] events) {        
        TreeSet<FireTimeEvent> set = new TreeSet<FireTimeEvent>(new Comparator<FireTimeEvent>() {            
            public int compare(FireTimeEvent e1, FireTimeEvent e2) {
                if (e1.time.timeMillis() < e2.time.timeMillis()) {
                    return -1;
                }  else if (e1.time.timeMillis() == e2.time.timeMillis()) {
                    return -1;
                } else {
                    return 1;
                }
            }  
        });
        
        for (int i=0; i < events.length; i++) {
            set.add(events[i]);
        }
        
        return set;
    }
    
    
    private FireTimeEvent[] getSortedArrayFromTreeSet(TreeSet<FireTimeEvent> set) {
        FireTimeEvent[] res = new FireTimeEvent[set.size()];
        Iterator it = set.iterator();
        int pos=0;
        while (it.hasNext()) {
            res[pos++] = (FireTimeEvent)it.next();
        }
        return res;
    }
}
