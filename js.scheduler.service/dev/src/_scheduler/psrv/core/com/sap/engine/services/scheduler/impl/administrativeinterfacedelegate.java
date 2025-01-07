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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.SchedulerAdministrator;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.api.TaskStatus;
import com.sap.scheduler.api.TaskValidationException;
import com.sap.scheduler.api.TooManyFireEventsException;
import com.sap.scheduler.api.Scheduler.FireTimeEvent;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIllegalStateException;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.SchedulerLogRecordIterator;
import com.sap.scheduler.runtime.SchedulerRuntimeException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Hristo Sabev
 */
public class AdministrativeInterfaceDelegate implements SchedulerAdministrator {
	
	
	private static Location location = Location.getLocation(AdministrativeInterfaceDelegate.class);

	private final InitialContext ictx;
	private final ServiceFrame theService;
	
	/**
	 * Constructs new ServiceInterfaceDelegate to forward calls to the singleton scheduler.
	 * @throws NamingException - thrown if error ocurred while obtaining InitialContext for
	 * replicated jndi.
	 */
	AdministrativeInterfaceDelegate(ServiceFrame theService) throws NamingException {
		this.theService = theService;
		ictx = theService.obtainReplicatedInitialContext();
	}

    private JobExecutionRuntime theRuntime() {
        return theService.theRuntime();
    }

    private static SchedulerRuntimeException createAndLogRuntimeException(SQLException e) {
        return Scheduler.logSQLExceptionAndCreateRuntime(e);
    }

	
	private AdministrativeSingleton getSingleton() throws SchedulerRuntimeException {
		final ClassLoader storedContextCL = Thread.currentThread().getContextClassLoader();
		try {
			//Sets the classloader of the service as context classloader. Otherwise, the context
			//class loader will be the loader of the caller which is either another service or
			//an application. In both cases however the context loader wouldn't be able to load the
			//FunctionalSingleton interface as it's private to the scheduler service. Thus no stub
			//would be created.
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			Object lookedUp = ictx.lookup(ServiceFrame.singletonAdministratorJNDIName);
            
            if (lookedUp == null) {
                // we dump here the ClusterLayout, because this NamingException should never occur
                Category.SYS_SERVER.logT(Severity.ERROR, location, SingletonEnvironment.formatClusterLayout(theService.getClusterLayout()));
                throw new SchedulerRuntimeException(ServiceFrame.singletonAdministratorJNDIName+" is bound with null in JNDI.");
            }
            
			AdministrativeSingleton administrator = (AdministrativeSingleton) PortableRemoteObject
						.narrow(lookedUp, AdministrativeSingleton.class);
			return administrator;
		} catch (NamingException ne) {
            // we dump here the ClusterLayout, because this NamingException should never occur
            Category.SYS_SERVER.logT(Severity.ERROR, location, SingletonEnvironment.formatClusterLayout(theService.getClusterLayout()));
            
			final String errMsg = "Error ocurred while forwarding call to the singleton scheduler."
					+ " Unable to lookup sinleton service from replicated JNDI. Possibly there is no"
					+ " singleton currently available or there has been an error while replicationg JNDI";
			Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, errMsg, ne);
			throw new SchedulerRuntimeException(errMsg, ne);
		} finally {
			Thread.currentThread().setContextClassLoader(storedContextCL);
		}
	}
	
	private SchedulerRuntimeException createAndLogRuntimeException(RemoteException re) {
		final String errMsg = "Unable to obtain the administrative singleton while forwarding call to singleton";
		location.traceThrowableT(Severity.ERROR, errMsg, re);
		return new SchedulerRuntimeException(errMsg, re);
	}
	
	public void schedule(SchedulerTask task, String runAsUser) throws TaskValidationException {
        if (location.beDebug()) location.debugT("schedule(SchedulerTask task, String runAsUser) for task with taskId "+task.getTaskId()+" entered");

		try {
			getSingleton().schedule(task, runAsUser);
		} catch (RemoteException re) {
			throw createAndLogRuntimeException(re);
		}
	}

	public SchedulerTask getTask(SchedulerTaskID taskId) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("SchedulerTask getTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");
        
		try {
			return getSingleton().getTask(taskId);
		} catch (RemoteException re) {
			throw createAndLogRuntimeException(re);
		}
	}

	public void cancelTask(SchedulerTaskID taskId) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("cancelTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");
        
		try {
			getSingleton().cancelTask(taskId);
		} catch (RemoteException re) {
			throw createAndLogRuntimeException(re);
		}
	}    
    
    public void holdTask(SchedulerTaskID taskId) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("holdTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");
        
        try {
            getSingleton().holdTask(taskId);
        } catch (RemoteException re) {
            throw createAndLogRuntimeException(re);
        }
    }    
    
    public void releaseTask(SchedulerTaskID taskId) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("releaseTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");
        
        try {
            getSingleton().releaseTask(taskId);
        } catch (RemoteException re) {
            throw createAndLogRuntimeException(re);
        }
    }

	public void setFilters(SchedulerTaskID taskId, Filter[] f) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("setFilters(SchedulerTaskID taskId, Filter[] f) with taskId "+taskId+" entered");
        
		try {
			getSingleton().setFilters(taskId, f);
		} catch (RemoteException re) {
			throw createAndLogRuntimeException(re);
		}
	}

	public void addFilters(SchedulerTaskID taskId, Filter[] f) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("addFilters(SchedulerTaskID taskId, Filter[] f) with taskId "+taskId+" entered");
        
		try {
			getSingleton().addFilters(taskId, f);
		} catch (RemoteException re) {
			throw createAndLogRuntimeException(re);
		}
	}

	public void removeFilters(SchedulerTaskID taskId, Filter[] f) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("removeFilters(SchedulerTaskID taskId, Filter[] f) with taskId "+taskId+" entered");
        
		try {
			getSingleton().removeFilters(taskId, f);
		} catch (RemoteException re) {
			throw createAndLogRuntimeException(re);
		}
	}

	public SchedulerTaskID[] getAllSchedulerTaskIds() {
        if (location.beDebug()) location.debugT("SchedulerTaskID[] getAllSchedulerTaskIds() entered");
        
		try {
			return getSingleton().getAllSchedulerTaskIds();
		} catch (RemoteException re) {
			throw createAndLogRuntimeException(re);
		}
	}
	
	public FireTimeEvent[] getFireTimes(SchedulerTaskID arg0,
			SchedulerTime arg1, SchedulerTime arg2)
			throws TaskDoesNotExistException, TooManyFireEventsException,
			SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("FireTimeEvent[] getFireTimes(SchedulerTaskID arg0, SchedulerTime arg1, SchedulerTime arg2) with taskId "+arg0+" entered");
        
		try {
			return getSingleton().getFireTimes(arg0, arg1, arg2);
		} catch (RemoteException re) {
			throw createAndLogRuntimeException(re);
		}
	}
	
	public FireTimeEvent[] getFireTimes(SchedulerTime arg0, SchedulerTime arg1)
			throws TooManyFireEventsException, SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("FireTimeEvent[] getFireTimes(SchedulerTime arg0, SchedulerTime arg1) entered");
        
		try {
			return getSingleton().getFireTimes(arg0, arg1);
		} catch (RemoteException re) {
			throw createAndLogRuntimeException(re);
		}
	}
    
    
    public FireTimeEvent[] getFireTimes(SchedulerTime arg0, SchedulerTime arg1, int fetchSize) throws TooManyFireEventsException, SchedulerRuntimeException {
        if (location.beDebug())
            location.debugT("FireTimeEvent[] getFireTimes(SchedulerTime arg0, SchedulerTime arg1, int fetchSize) entered");

        try {
            return getSingleton().getFireTimes(arg0, arg1, fetchSize);
        } catch (RemoteException re) {
            throw createAndLogRuntimeException(re);
        }
    }
    
    
    public LogIterator getJobLog(JobID jobId, LogIterator logIter, int resultSetSize) throws NoSuchJobException {
        if (location.beDebug()) location.debugT("LogIterator getJobLog(JobID jobId, LogIterator logIter, int resultSetSize) with jobId "+jobId+" entered");
        
        try {
            return getSingleton().getJobLog(jobId, logIter, resultSetSize);
        } catch (RemoteException re) { 
            throw createAndLogRuntimeException(re);
        }
    }
    
    
    public SchedulerLogRecordIterator getJobLogRecords(JobID jobId, SchedulerLogRecordIterator logIter, int resultSetSize) throws NoSuchJobException {
        if (location.beDebug()) location.debugT("SchedulerLogRecordIterator getJobLogRecords(JobID jobId, SchedulerLogRecordIterator logIter, int resultSetSize) with jobId "+jobId+" entered");
        
        try {
            return getSingleton().getJobLogRecords(jobId, logIter, resultSetSize);
        } catch (RemoteException re) { 
            throw createAndLogRuntimeException(re);
        }
    }
    
    // TODO security check for direct invocations to the runtime
    public void cancelJob(JobID jobid) 
                            throws JobIllegalStateException,
                                   SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("cancelJob(JobID jobid) with jobId "+jobid+" entered");

        try {
            theRuntime().cancelJob(jobid);
        } catch (NoSuchJobException nsj) {
            throw new SchedulerRuntimeException("Job \"" + jobid.toString() + "\" does not exist. It cannot be cancelled.");
        } catch (SQLException sqle) {
            throw createAndLogRuntimeException(sqle);
        } 

    }

    
    public SchedulerTask[] getAllSchedulerTasks() {
        if (location.beDebug()) location.debugT("SchedulerTask[] getAllSchedulerTasks() entered");
        
        try {
            return getSingleton().getAllSchedulerTasks();
        } catch (RemoteException re) {
            throw createAndLogRuntimeException(re);
        } 
    }
    
    
    public int removeSchedulerTasks(Timestamp ts) throws SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("removeSchedulerTasks(Timestamp ts) entered");
        
        try {
            return getSingleton().removeSchedulerTasks(ts);
        } catch (RemoteException re) {
            throw createAndLogRuntimeException(re);
        } 
    }
    
    public Date getServerTime() throws SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("getServerTime()");

        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }
    
    // -------------------------------------------------------------------------
    // in cause that this interface is completely internal, we provide also 
    // analysis methods used by the telnet commands
    // -------------------------------------------------------------------------
    
    public Map<String, String[]> getClusterLayout() throws SchedulerRuntimeException {
        // we can access here the service directly, because the layout info can 
        // be accessed from every node 
        return theService.getClusterLayout();
    }
    
    public SchedulerTask[] getAllSchedulerTasksFromPriorityQueue() throws SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("SchedulerTask[] getAllSchedulerTasksFromPriorityQueue() entered");
        
        try {
            return getSingleton().getAllSchedulerTasksFromPriorityQueue();
        } catch (RemoteException re) {
            throw createAndLogRuntimeException(re);
        }
    }
    
    public SchedulerTask[] getAllSchedulerTasks(TaskStatus status) throws SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("SchedulerTask[] getAllSchedulerTasks(TaskStatus status) for TaskStatus "+status.toString()+" entered");
        
        try {
            return getSingleton().getAllSchedulerTasks(status);
        } catch (RemoteException re) {
            throw createAndLogRuntimeException(re);
        }
    }
    
    public Map getTaskStatusDescriptions() throws SchedulerRuntimeException {
        if (location.beDebug()) location.debugT("Map getTaskStatusDescriptions() entered");
        
        try {
            return getSingleton().getTaskStatusDescriptions();
        } catch (RemoteException re) {
            throw createAndLogRuntimeException(re);
        }
    }
    
}
