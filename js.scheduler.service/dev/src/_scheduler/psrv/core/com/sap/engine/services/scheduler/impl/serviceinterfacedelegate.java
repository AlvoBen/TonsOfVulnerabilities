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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.api.TaskValidationException;
import com.sap.scheduler.api.TooManyFireEventsException;
import com.sap.scheduler.runtime.AbstractIdentifier;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.EventConsumer;
import com.sap.scheduler.runtime.EventSubscriber;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIllegalStateException;
import com.sap.scheduler.runtime.JobIterator;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.SchedulerRuntimeException;
import com.sap.scheduler.runtime.SubscriberID;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Hristo Sabev (i027642)
 */
class ServiceInterfaceDelegate implements com.sap.scheduler.api.Scheduler {
	private static final Location location = Location.getLocation(ServiceInterfaceDelegate.class);

	private final InitialContext ictx;
	private final ServiceFrame theService;
	
	/**
	 * Constructs new ServiceInterfaceDelegate to forward calls to the singleton scheduler.
	 * @throws NamingException - thrown if error ocurred while obtaining InitialContext for
	 * replicated jndi.
	 */
	ServiceInterfaceDelegate(ServiceFrame theService) throws NamingException {
		this.theService = theService;
		ictx = theService.obtainReplicatedInitialContext();
	}	
	
	private FunctionalSingleton getSingleton() throws RuntimeException {
		final ClassLoader storedContextCL = Thread.currentThread().getContextClassLoader();
		try {
			//Sets the classloader of the service as context classloader. Otherwise, the context
			//class loader will be the loader of the caller which is either another service or
			//an application. In both cases however the context loader wouldn't be able to load the
			//FunctionalSingleton interface as it's private to the scheduler service. Thus no stub
			//would be created.
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			Object lookedUp = ictx.lookup(ServiceFrame.singletonJNDIName);
            
            if (lookedUp == null) {
                // we dump here the ClusterLayout, because this NamingException should never occur
                Category.SYS_SERVER.logT(Severity.ERROR, location, SingletonEnvironment.formatClusterLayout(theService.getClusterLayout()));
                throw new SchedulerRuntimeException(ServiceFrame.singletonJNDIName+" is bound with null in JNDI.");
            }
            
			FunctionalSingleton scheduler = (FunctionalSingleton) PortableRemoteObject
						.narrow(lookedUp, FunctionalSingleton.class);
			return scheduler;
		} catch (NamingException ne) {
            // we dump here the ClusterLayout, because this NamingException should never occur
            Category.SYS_SERVER.logT(Severity.ERROR, location, SingletonEnvironment.formatClusterLayout(theService.getClusterLayout()));
            
			final String errMsg = "Error ocurred while forwarding call to the singleton scheduler."
					+ " Unable to lookup sinleton service from replicated JNDI. Possibly there is no"
					+ " singleton currently available or there has been an error while replicationg JNDI";
			Category.SYS_SERVER.logThrowableT(Severity.ERROR, location,
					errMsg, ne);
			throw new SchedulerRuntimeException(errMsg, ne);
		} finally {
			Thread.currentThread().setContextClassLoader(storedContextCL);
		}
	}
	
	private JobExecutionRuntime theRuntime() {
		return theService.theRuntime();
	}
	
	public void addFilters(SchedulerTaskID arg0, Filter[] arg1)
	throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("addFilters(SchedulerTaskID arg0, Filter[] arg1) with taskId "+arg0+" entered");
        
		try {
			getSingleton().addFilters(arg0, arg1);
		} catch (RemoteException re) {
			throw createAndLogForwardingException(re);
		}

	}
	
	public void cancelTask(SchedulerTaskID arg0)
	throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("cancelTask(SchedulerTaskID arg0) with taskId "+arg0+" entered");
        
		try {
			getSingleton().cancelTask(arg0);
		} catch (RemoteException re) {
			throw createAndLogForwardingException(re);
		}
	}
    
    public void holdTask(SchedulerTaskID taskId) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("holdTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");
        
        try {
            getSingleton().holdTask(taskId);
        } catch (RemoteException re) {
            throw createAndLogForwardingException(re);
        }
    }    

    public void releaseTask(SchedulerTaskID taskId) throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("releaseTask(SchedulerTaskID taskId) with taskId "+taskId+" entered");
        
        try {
            getSingleton().releaseTask(taskId);
        } catch (RemoteException re) {
            throw createAndLogForwardingException(re);
        }
    }
	
	public SchedulerTaskID[] getAllSchedulerTaskIDs() {
        if (location.beDebug()) location.debugT("SchedulerTaskID[] getAllSchedulerTaskIDs() entered");
        
		try {
			return getSingleton().getAllSchedulerTaskIDs();
		} catch (RemoteException re) {
			throw createAndLogForwardingException(re);
		}
	}
	
	public FireTimeEvent[] getFireTimes(SchedulerTaskID arg0,
			SchedulerTime arg1, SchedulerTime arg2)
	throws TooManyFireEventsException, TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("FireTimeEvent[] getFireTimes(SchedulerTaskID arg0, SchedulerTime arg1, SchedulerTime arg2) with taskId "+arg0+" entered");
        
		try {
			return getSingleton().getFireTimes(arg0, arg1, arg2);
		} catch (RemoteException re) {
			throw createAndLogForwardingException(re);
		}
	}
	
	public SchedulerTask getTask(SchedulerTaskID arg0)
	throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("SchedulerTask getTask(SchedulerTaskID arg0) with taskId "+arg0+" entered");
        
		try {
			return getSingleton().getTask(arg0);
		} catch (RemoteException re) {
			throw createAndLogForwardingException(re);
		}
	}
	
	public void removeFilters(SchedulerTaskID arg0, Filter[] arg1)
	throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("removeFilters(SchedulerTaskID arg0, Filter[] arg1) with taskId "+arg0+" entered");
        
		try {
			getSingleton().removeFilters(arg0, arg1);
		} catch (RemoteException re) {
			throw createAndLogForwardingException(re);
		}
	
	}
	
	public void schedule(SchedulerTask arg0) throws TaskValidationException {
        if (location.beDebug()) location.debugT("schedule(SchedulerTask arg0) for task with taskId "+arg0+" entered");
        
		try {
			getSingleton().schedule(arg0);
		} catch (RemoteException re) {
			throw createAndLogForwardingException(re);
		}
	}
	
	public void setFilters(SchedulerTaskID arg0, Filter[] arg1)
	throws TaskDoesNotExistException {
        if (location.beDebug()) location.debugT("setFilters(SchedulerTaskID arg0, Filter[] arg1) with taskId "+arg0+" entered");
        
		try {
			getSingleton().setFilters(arg0, arg1);
		} catch (RemoteException re) {
			throw createAndLogForwardingException(re);
		}
	}
	
	public void cancelJob(JobID arg0) throws JobIllegalStateException {
        if (location.beDebug()) location.debugT("cancelJob(JobID arg0) with jobId "+arg0+" entered");
        
		try {
			theRuntime().cancelJob(arg0);
		} catch (NoSuchJobException nsj) {
			throw new JobIllegalStateException("Job with id \"" + arg0.toString() + "\" does not exist. It cannot be cancelled.");
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		} 
	}
    
	public Job[] getChildJobs(JobID arg0) throws NoSuchJobException {
        if (location.beDebug()) location.debugT("Job[] getChildJobs(JobID arg0) with jobId "+arg0+" entered");
        
		try {
			return theRuntime().getChildJobs(arg0);
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		} 
	}
    
	public Job getJob(JobID arg0) {
        if (location.beDebug()) location.debugT("Job getJob(JobID arg0) with jobId "+arg0+" entered");
        
		try {
			return theRuntime().getJob(arg0);
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		} 
	}
    
	public JobDefinition getJobDefinitionById(JobDefinitionID arg0) {
        if (location.beDebug()) location.debugT("JobDefinition getJobDefinitionById(JobDefinitionID arg0) with jobDefId "+arg0+" entered");
        
		try {
			return theRuntime().getJobDefinitionById(arg0);
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		} 
	}
    
	public JobDefinition getJobDefinitionByName(String arg0) {
        if (location.beDebug()) location.debugT("JobDefinition getJobDefinitionByName(String arg0) with name "+arg0+" entered");
        
		try {
			return theRuntime().getJobDefinitionByName(arg0);
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		} 
	}
    
	public JobDefinition[] getJobDefinitions() {
        if (location.beDebug()) location.debugT("JobDefinition[] getJobDefinitions() entered");
        
		try {
			return theRuntime().getJobDefinitions();
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		} 
	}
    
	public LogIterator getJobLog(JobID arg0, LogIterator arg1, int arg2)
			throws NoSuchJobException {
        if (location.beDebug()) location.debugT("LogIterator getJobLog(JobID arg0, LogIterator arg1, int arg2) with jobId "+arg0+" entered");
        
		try {
			return theRuntime().getJobLog(arg0, arg1, arg2);
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		}
	}
    
	public JobParameter[] getJobParameters(JobID arg0)
			throws NoSuchJobException {
        if (location.beDebug()) location.debugT("JobParameter[] getJobParameters(JobID arg0) with jobId "+arg0+" entered");
        
		try {
			return theRuntime().getJobParameters(arg0);
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		}
	}
    
	public JobIterator getJobs(JobFilter arg0, JobIterator arg1, int arg2) {
        if (location.beDebug()) location.debugT("JobIterator getJobs(JobFilter arg0, JobIterator arg1, int arg2) entered");
        
		try {
			return theRuntime().getJobs(arg0, arg1, arg2);
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		}
	}
	public Job[] getJobs(JobID[] arg0) {
        if (location.beDebug()) location.debugT("Job[] getJobs(JobID[] arg0) entered");
        
		try {
			return theRuntime().getJobs(arg0);
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		}
	}

	public JobStatus getJobStatus(JobID arg0) {
        if (location.beDebug()) location.debugT("JobStatus getJobStatus(JobID arg0) with jobId "+arg0+" entered");
        
		try {
			return theRuntime().getJobStatus(arg0);
		} catch (NoSuchJobException nsj) {
			throw new SchedulerRuntimeException("Job \"" + arg0.toString() + "\" does not exist.");
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		}
	}
	
	public boolean hasChildJobs(JobID arg0) {
        if (location.beDebug()) location.debugT("boolean hasChildJobs(JobID arg0) with jobId "+arg0+" entered");
        
		try {
			return theRuntime().hasChildJobs(arg0);
		} catch (NoSuchJobException nsj) {
			throw new SchedulerRuntimeException("Job \"" + arg0.toString() + "\" does not exist.");
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		}
	}
    
	public void removeJob(JobID arg0) throws JobIllegalStateException, NoSuchJobException {
        if (location.beDebug()) location.debugT("removeJob(JobID arg0) with jobId "+arg0+" entered");
        
		try {
			theRuntime().removeJob(arg0);
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		}
	}
	
	public void removeJobs(JobID[] arg0) {
        if (location.beDebug()) location.debugT("removeJobs(JobID[] arg0) with jobId "+arg0+" entered");
        
		try {
			theRuntime().removeJobs(arg0);
		} catch (SQLException sqle) {
			throw createAndLogRuntimeException(sqle);
		}
	}
    
    public void addEventListener(String[] eventsRegisteredFor, EventConsumer consumer) {
        EventSubscriber sub = new EventSubscriber(SubscriberID.newID(), eventsRegisteredFor, consumer);              
        try {
            theRuntime().registerEventSubscriber(sub);
        } catch (SQLException sqle) {
            throw createAndLogRuntimeException(sqle);
        }  
    }
    
    public void removeEventListener(EventConsumer consumer) {
        try {
            theRuntime().unregisterEventSubscriber(consumer);
        } catch (SQLException sqle) {
            throw createAndLogRuntimeException(sqle);
        }
    }

    
    // -------------------------------------------------------------------------
    // ----------------------- private helper methods --------------------------
    // -------------------------------------------------------------------------
    
	private static SchedulerRuntimeException createAndLogRuntimeException(SQLException e) {
		return Scheduler.logSQLExceptionAndCreateRuntime(e);
	}

	private SchedulerRuntimeException createAndLogForwardingException(RemoteException re) {
		String errMsg = "RemoteException has ocurred while forwarding call to singleton";
		Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, 
				errMsg, re);
		return new SchedulerRuntimeException(errMsg, re);
	}
	
	
	
}
