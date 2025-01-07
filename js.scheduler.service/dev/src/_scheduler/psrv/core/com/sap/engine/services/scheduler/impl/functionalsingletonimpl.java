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

import java.rmi.RemoteException;
import java.security.AccessControlException;

import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.api.TaskValidationException;
import com.sap.scheduler.api.TooManyFireEventsException;
import com.sap.scheduler.api.Scheduler.FireTimeEvent;
import com.sap.tc.logging.Location;

/**
 * @author d040949
 * @author Hristo Sabev
 * @author d040939
 * 
 * In this class we need also to check if the caller is Administrator or not. If yes
 * the Administrator will access the calling methods in a way to see all data (Tasks),
 * also the data of other Administrators. 
 * If the user is not Administrator the view will be limited.
 */

public class FunctionalSingletonImpl implements FunctionalSingleton  {
    private static final Location location = Location.getLocation(FunctionalSingletonImpl.class);
    
	private final ServiceFrame m_service;
	private final Scheduler m_scheduler;
    private final SingletonEnvironment m_env;

    
	public FunctionalSingletonImpl(SingletonEnvironment env) {
        this.m_env = env;
		this.m_service = env.getServiceFrame();
		this.m_scheduler = env.getScheduler();
	}
    
    
	public void addFilters(SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException, RemoteException {
        if (location.beDebug()) location.debugT("addFilters(SchedulerTaskID id, Filter[] f) with taskId "+id+" entered");
        
		String caller = getCaller();
        if (isAdminUser(caller)) {
            m_scheduler.addFilters(null, id, f);
        } else {
            m_scheduler.addFilters(caller, id, f);   
        }
	}

	public void cancelTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, RemoteException {
        if (location.beDebug()) location.debugT("cancelTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");
        
		String caller = getCaller();
        if (isAdminUser(caller)) {
            m_scheduler.cancelTask(null, taskId, TaskStatusInternal.finishedCancelledAPI);
        } else {
            m_scheduler.cancelTask(caller, taskId, TaskStatusInternal.finishedCancelledAPI);
        }		
	}
    
    public void holdTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, RemoteException {
        if (location.beDebug()) location.debugT("holdTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");
        
        String caller = getCaller();
        if (isAdminUser(caller)) {
            m_scheduler.holdTask(null, taskId, TaskStatusInternal.holdApi);
        } else {
            m_scheduler.holdTask(caller, taskId, TaskStatusInternal.holdApi);
        }       
    }
    
    public void releaseTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, RemoteException {
        if (location.beDebug()) location.debugT("releaseTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");
        
        String caller = getCaller();
        if (isAdminUser(caller)) {
            m_scheduler.releaseTask(null, taskId);
        } else {
            m_scheduler.releaseTask(caller, taskId);
        }       
    }
    
	public SchedulerTaskID[] getAllSchedulerTaskIDs() throws RemoteException {
        if (location.beDebug()) location.debugT("SchedulerTaskID[] getAllSchedulerTaskIDs() entered");
        
		String caller = getCaller();
        if (isAdminUser(caller)) {
            return m_scheduler.getAllSchedulerTaskIDs(null);   
        } else {
            return m_scheduler.getAllSchedulerTaskIDs(caller);   
        }
	}
	
	public FireTimeEvent[] getFireTimes(SchedulerTaskID id, SchedulerTime startTime, SchedulerTime endTime)
              throws TaskDoesNotExistException, TooManyFireEventsException,	RemoteException {
        if (location.beDebug()) location.debugT("FireTimeEvent[] getFireTimes(SchedulerTaskID id, SchedulerTime startTime, SchedulerTime endTime) with taskId "+id+" entered");
        
		String caller = getCaller();
        if (isAdminUser(caller)) {
            return m_scheduler.getFireTimes(null, id, startTime, endTime);
        } else {
            return m_scheduler.getFireTimes(caller, id, startTime, endTime);   
        }
	}

	public SchedulerTask getTask(SchedulerTaskID id) throws TaskDoesNotExistException, RemoteException {
        if (location.beDebug()) location.debugT("SchedulerTask getTask(SchedulerTaskID id) with taskId "+id+" entered");
        
		String caller = getCaller();
        if (isAdminUser(caller)) {
            return m_scheduler.getTask(null, id);
        } else {
            return m_scheduler.getTask(caller, id);   
        }
	}

	public void removeFilters(SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException, RemoteException {
        if (location.beDebug()) location.debugT("removeFilters(SchedulerTaskID id, Filter[] f) with taskId "+id+" entered");
        
		String caller = getCaller();
        if (isAdminUser(caller)) {
            m_scheduler.removeFilters(null, id, f);
        } else {
            m_scheduler.removeFilters(caller, id, f);   
        }
	}
	public void schedule(SchedulerTask task) throws TaskValidationException, RemoteException {
        if (location.beDebug()) location.debugT("schedule(SchedulerTask task) for task with taskId "+task.getTaskId()+" entered");
        
		String caller = getCaller();
        // also for Administrators, we need to persist the caller name
        m_scheduler.schedule(caller, caller, task);
	}

	public void setFilters(SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException, RemoteException {
        if (location.beDebug()) location.debugT("setFilters(SchedulerTaskID id, Filter[] f) with taskId "+id+" entered");
        
		String caller = getCaller();
        if (isAdminUser(caller)) {
            m_scheduler.setFilters(null, id, f);
        } else {
            m_scheduler.setFilters(caller, id, f);   
        }
	}
    
    
    // -------------------------------------------------------------------------
    // --------------- Private helper methods ----------------------------------
    // -------------------------------------------------------------------------
    
    private boolean isAdminUser(String caller) {
        boolean isAdminUser = false;
        
        try {
            m_env.assertAuthorizedAdministrativeMethods(caller);
            isAdminUser = true;
        } catch (AccessControlException ace) {
            // $JL-EXC$
        }
        
        return isAdminUser;
    }  
    
    private String getCaller() {
        return m_env.getCaller();
    }    
}
