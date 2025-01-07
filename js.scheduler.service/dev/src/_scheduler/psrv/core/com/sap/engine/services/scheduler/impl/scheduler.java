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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.sap.engine.frame.NestedProperties;
import com.sap.engine.services.scheduler.util.Filters;
import com.sap.scheduler.api.CronEntry;
import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.RecurringEntry;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.api.TaskStatus;
import com.sap.scheduler.api.TaskValidationException;
import com.sap.scheduler.api.TooManyFireEventsException;
import com.sap.scheduler.api.Scheduler.FireTimeEvent;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.EventConsumer;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobDefinitionName;
import com.sap.scheduler.runtime.JobExecutorException;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobDefinitionException;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.NoSuchUserException;
import com.sap.scheduler.runtime.ParameterValidationException;
import com.sap.scheduler.runtime.SchedulerID;
import com.sap.scheduler.runtime.SchedulerLogRecordIterator;
import com.sap.scheduler.runtime.SchedulerRuntimeException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


public class Scheduler {
	public static final int maxFireEventsNumber = 10000;
    public static final int FETCH_SIZE_IGNORE = -1;
	private static final Location location = Location.getLocation(Scheduler.class);
    private static final Category category = Category.SYS_SERVER;
	
	private final ServiceFrame m_service;
	private final DataSource m_ds;
	private final TaskPersistor m_persistor;
	private final TaskProcessorPQ m_PQ;
	private SchedulerID m_thisSchedulerId;
    private final SingletonEnvironment m_env;
	
	Scheduler(SingletonEnvironment env, EventConsumer eventConsumer) {
        this.m_env = env;
		this.m_service = env.getServiceFrame();
		final String dataSourceName = "jdbc/notx/SAP/BC_SCHEDULER";
		try {
			InitialContext iCtx = new InitialContext();
			m_ds = (DataSource)iCtx.lookup(dataSourceName);
		} catch (NamingException ne) {
			final String errMsg = "Unable to obtain data-source to scheduler data base";
			if (location.beDebug()) location.traceThrowableT(Severity.DEBUG, errMsg, ne);
			throw new SchedulerRuntimeException(errMsg, ne);
		}
		this.m_persistor = new TaskPersistor(m_service);
        this.m_PQ = new TaskProcessorPQ(new TaskExecutorImpl(m_service, m_persistor, eventConsumer), env);
        
        // store the TaskPersistor in the SingletonEnvironment
        m_env.setTaskPersistor(m_persistor);      
	}
		
	synchronized void shutDown() {
		m_PQ.shutDown();
	}
	
	
	/**
     * Filter task list for tasks which refer to job definitions which are no
     * longer deployed. Normally nothing should be done here because the
     * scheduler honors the undeployment events, however there are at least two
     * cases where we catch something here:
     * 
     * <ul>
     * <li>upgrades are normally done in "safe mode" (scheduler inactive)
     * <li>there could be a deployment while no scheduler service is running
     * </ul>
     * 
     * Note: this method should only do someting on very rare occasions
     * 
     */
    synchronized void removeStaleTasks() throws SQLException {

        final Connection c = obtainConnection();
        try {
            m_persistor.removeStaleTasks(c);
        } finally {
            closeConnection(c);
        }
    }
	
	synchronized void recoverFromDB() throws SQLException {
		final Connection c = obtainConnection();
		final List<SchedulerTask> allTasks;
		
		try {
            // we select all active tasks from all sources (zeroadmin, default, ...)
			allTasks = m_persistor.getAllTasks(c, null, TaskStatus.active);
		} finally {
			closeConnection(c);
		}
		
		for (int i = 0; i < allTasks.size(); i++) {
			SchedulerTask task = allTasks.get(i);
			TaskProcessor newProcessor = new TaskProcessor(task);
            newProcessor.setActive(true);            
            boolean isScheduled = m_PQ.schedule(newProcessor);
            
            if (!isScheduled) {
            	// this means the task is already in the PQ or it has no more execution times
            	if (m_PQ.get(task.getTaskId()) == null) {
            		// task is not in PQ, thus it has nor more execution times
            		// If the task has now exactly one execution time, we trigger 
            		// it now once and set the task to finished/canceled
            		if ( hasSingleExecutionTime(task) ) {
                        // the current user will be taken for this execution
            			String errorMsg = "job execution of a task with a single execution time has failed after restarting the system";
            			try {
            				JobID execJobID = m_service.theRuntime().executeJob(task.getJobDefinitionId(),
                                                                            	task.getJobParameters(),
                                                                            	new Integer(task.getRetentionPeriod()),
                                                                            	null, // null for JobID
                                                                            	m_service.getScheduler().schedulerId(),
                                                                            	task.getRunAsUser(),
                                                                            	task.getTaskId());
            				StringBuilder msg = new StringBuilder("There was a task with a single execution time and this execution time conflicts with a down time of the system. This execution time was retriggered again. Details: ");
            				msg.append("TaskName: ").append(task.getName()).append(", ");
            				msg.append("TaskId: ").append(task.getTaskId().toString()).append(", ");
            				msg.append("JobId: ").append(execJobID.toString());
            				// write an DEBUG trace
            				if (location.beDebug()) {location.debugT(msg.toString());}
            				
            			} catch (ParameterValidationException pve) {
                            Category.SYS_SERVER.logT(Severity.ERROR, location, errorMsg + " Parameter of task '"+task.getName()+"' with TaskID '" + task.getTaskId() + "' not valid.");
                        } catch (NoSuchJobDefinitionException nsjde) {
                            Category.SYS_SERVER.logT(Severity.ERROR, location, errorMsg + " JobDefinition of task '"+task.getName()+"' with TaskID '" + task.getTaskId() + "' does not more exist.");
                        } catch (JobExecutorException jee) {
                            Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, errorMsg + " Error while executing task '"+task.getName()+"' with TaskID '" + task.getTaskId() + "'.", jee);
                        } catch (NoSuchUserException nsue) {
                        	Category.SYS_SERVER.logT(Severity.ERROR, location, errorMsg + " RunAsUser '"+task.getRunAsUser()+"' of task '"+task.getName()+"' with TaskID '" + task.getTaskId() + "' does not more exist.");
                        }
            		} 
            		
            		// cancel it in every case in cause of it has no more execution times     
            		try { 
						cancelTaskInternal(null, task.getTaskId(), TaskStatusInternal.finishedFinished, true);
						if (location.beDebug()) {location.debugT("Task '"+task.getName()+"' with Id '"+task.getTaskId().toString()+"' has been cancelled in case it has no more execution times.");}
						
					} catch (TaskDoesNotExistException e) {
		                category.logT(Severity.ERROR, location, "Error while cancelling SchedulerTask with Id '"+task.getTaskId());
		                location.traceThrowableT(Severity.ERROR, e.getMessage(), e);
					}
            	}
            }
		}
		// start the PQ
		m_PQ.start();
	}
    
    
    protected synchronized SchedulerTask[] readAllTasksFromZeroAdminTemplate() {
        final String TASKS = "Tasks";
        
        // cluster_config/system/instances/current_instance/cfg/services/scheduler/properties
        NestedProperties properties = (NestedProperties)m_service.getServiceProperties();  
        
        // String errMsg = "Error occurred while reading tasks from ZeroAdmin-template!";

        ArrayList<SchedulerTask> allSchedulerTasks = new ArrayList<SchedulerTask>(); 
        
        NestedProperties tasks = null;
        if ( (tasks = properties.getNestedProperties(TASKS)) != null ) {
            // access all PropertySheets which represent a task
            String[] allTasksPSs = tasks.getAllNestedPropertiesKeys();
            
            for (int i = 0; i < allTasksPSs.length; i++) {                           
                NestedProperties sheet = tasks.getNestedProperties(allTasksPSs[i]);
                SchedulerTask task = readAndConstructSchedulerTask(sheet, allTasksPSs[i]);
                if (task != null) {
                    allSchedulerTasks.add(task);
                }
            }                        
        }            
        
        return allSchedulerTasks.toArray(new SchedulerTask[allSchedulerTasks.size()]);
    }
    
    
    private SchedulerTask readAndConstructSchedulerTask(NestedProperties taskProperties, String sheetName) {
        
        // PropertySheet names
        final String TASK_PARAMETERS = "Parameters";
        final String TASK_RECURRING_ENTRY = "RecurringEntry";
        final String TASK_CRON_ENTRY = "CronEntry";
        final String TASK_FILTER = "Filter";
        
        
        // mandatory meta data
        final String TASK_JOB_DEFINITION   = "JobDefinitionName";
        
        // additional meta data
        final String TASK_DESCRIPTION      = "Description";
        final String TASK_RETENTION_PERIOD = "RetentionPeriod";
        final String TASK_CUSTOM_DATA      = "CustomData";
        
        // CronEntry entry name
        final String CRON_ENTRY_NAME = "entry";
        
        // RecurringEntry entry properties
        final String RECURRING_ENTRY_START_TIME = "StartTime";
        final String RECURRING_ENTRY_END_TIME = "EndTime";
        final String RECURRING_ENTRY_WAIT_PERIOD = "WaitPeriod";
        final String RECURRING_ENTRY_ITERATIONS = "Iterations";
        
        final String FILTER_START_TIME = "StartTime";
        final String FILTER_END_TIME = "EndTime";
        
        DateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        
        // ---------------------------------------------------------------------
        
        // access all meta data stored directly in the incoming PropertySheet
        
        // mandatory meta data
        String taskJobDefinitionName = null;
        String taskName = sheetName;
        
        // optional meta data
        String taskDescription = null;
        String taskCustomData = null;
        int taskRetentionPeriod = 0;
        
        // mandatory meta data --> we write an ERROR log and return
        if ( (taskJobDefinitionName = taskProperties.getProperty(TASK_JOB_DEFINITION)) == null ) {
            Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has no '"+TASK_JOB_DEFINITION+"'.");
            return null;
        }
        
        // optional meta data
        taskDescription = taskProperties.getProperty(TASK_DESCRIPTION);
        taskCustomData = taskProperties.getProperty(TASK_CUSTOM_DATA);
        
        String taskRetentionPeriodStr = null;
        if ( ( taskRetentionPeriodStr = taskProperties.getProperty(TASK_RETENTION_PERIOD)) != null ) {
            try {
                taskRetentionPeriod = Integer.parseInt(taskRetentionPeriodStr);
            } catch (NumberFormatException nfe) {
                Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has an invalid "+TASK_RETENTION_PERIOD+" '"+taskRetentionPeriodStr+"'.");
                return null;
            }
        } 
        
        // appName not mandatory if Job-name is unique --> will be validated within
        // JobParameter validation
        JobDefinitionName jobDefName = new JobDefinitionName(taskJobDefinitionName);
        
        JobDefinition jdef = null;
        try {
            // TODO, get all job definitions here as it may not be unique 
            // -> error
            jdef = m_service.theRuntime().getJobDefinitionByName(jobDefName);
        } catch (SQLException se) {
            Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, "Unable to retrive job definition for name '"+jobDefName+"'.",se);
            return null;
        }
        if (jdef == null) {
            Category.SYS_SERVER.logT(Severity.ERROR, location, "There is no job definition for name '"+jobDefName+"'.");
            return null;
        }
        
        JobDefinitionID jobDefId = jdef.getJobDefinitionId();
        JobParameterDefinition[] jobParamDef = jdef.getParameters();        
        
        ArrayList<JobParameter> jobParamsList = new ArrayList<JobParameter>();
        ArrayList<RecurringEntry> recurringEntryList = new ArrayList<RecurringEntry>();
        ArrayList<CronEntry> cronEntriesList = new ArrayList<CronEntry>();
        ArrayList<Filter> filtersList = new ArrayList<Filter>();
        
        String[] nestedPSNames = taskProperties.getAllNestedPropertiesKeys();
        
        for (int i = 0; i < nestedPSNames.length; i++) {

            // Parameters
            if (nestedPSNames[i].equals(TASK_PARAMETERS)) {
                Properties paramSheet = taskProperties.getNestedProperties(nestedPSNames[i]);      
                String[] paramNames = (String[])paramSheet.keySet().toArray(new String[paramSheet.keySet().size()]);
                
                for (int j = 0; j < paramNames.length; j++) {
                    for (int k = 0; k < jobParamDef.length; k++) {
                        if (jobParamDef[k].getName().equalsIgnoreCase(paramNames[j])) {
                            String value = (String) paramSheet.getProperty(paramNames[j]);
                            try {
                                jobParamsList.add(new JobParameter(jobParamDef[k], value));
                            } catch (IllegalArgumentException iae) {
                                Category.SYS_SERVER.logT(Severity.ERROR, location, "Parameter '" + jobParamDef[k].getName() + "' of job definition '"+jobDefName+"' has invalid type: " + iae.getMessage());
                                return null;
                            }
                        }
                    }
                }                
            }
            // RecurringEntries
            else if (nestedPSNames[i].startsWith(TASK_RECURRING_ENTRY)) {
                Properties paramSheet = taskProperties.getNestedProperties(nestedPSNames[i]);      
                
                Date startTime = null;
                Date endTime = null;
                long waitPeriod = 0;
                int iterations = 0;
                
                // start date has to be set
                String startTimeStr = null;
                if ( (startTimeStr = paramSheet.getProperty(RECURRING_ENTRY_START_TIME)) == null ) {
                    Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has no defined '"+RECURRING_ENTRY_START_TIME+"' in the PropertySheet '"+nestedPSNames[i]+"'.");
                    return null;
                }

                try {
                    startTime = dateFormatter.parse(startTimeStr);
                } catch (ParseException pe) {
                    Category.SYS_SERVER.logT(Severity.ERROR, location, "The recurring entry of deployed task '"+sheetName+"' has an illegal value for the start time '" + startTimeStr + "': " + pe.getMessage());
                    return null;
                }
                
                // end date may be set
                String endTimeStr = null;
                if ( (endTimeStr = paramSheet.getProperty(RECURRING_ENTRY_END_TIME)) != null ) {
                    try {
                        endTime = dateFormatter.parse(endTimeStr);
                    } catch (ParseException pe) {
                        Category.SYS_SERVER.logT(Severity.ERROR, location, "The recurring entry of deployed task '"+sheetName+"' has an illegal value for the end time '" + endTimeStr + "': " + pe.getMessage());
                        return null;
                    }
                }
                
                // read the WaitPeriod and the Iterations
                String waitPeriodStr = null;
                if ( (waitPeriodStr = paramSheet.getProperty(RECURRING_ENTRY_WAIT_PERIOD)) != null ) {
                    try {
                        waitPeriod = Long.parseLong(waitPeriodStr);
                    } catch (NumberFormatException nfe) {
                        Category.SYS_SERVER.logT(Severity.ERROR, location, "The recurring entry of deployed task '"+sheetName+"' has an illegal value for the wait period: " + waitPeriodStr);
                        return null;
                    }
                }
                
                String iterationStr = null;
                if ( (iterationStr = paramSheet.getProperty(RECURRING_ENTRY_ITERATIONS)) != null) {
                    try {
                        iterations = Integer.parseInt(iterationStr);
                    } catch (NumberFormatException nfe) {
                        Category.SYS_SERVER.logT(Severity.ERROR, location, "The recurring entry of deployed task '"+sheetName+"' has an illegal value for the iterations: " + iterationStr);
                        return null;
                    }
                }
                
                RecurringEntry recEntry = null;
                
                try {
                    if (endTime == null) {
                        if (waitPeriod != 0 && iterations != 0) {
                            recEntry = new RecurringEntry(new SchedulerTime(startTime, TimeZone.getDefault()), waitPeriod, iterations);
                        } else if (waitPeriod != 0 && iterations == 0) {
                            recEntry = new RecurringEntry(new SchedulerTime(startTime, TimeZone.getDefault()), waitPeriod);
                        }
                    } else {
                        if (waitPeriod != 0 && iterations == 0) {
                            recEntry = new RecurringEntry(new SchedulerTime(startTime, TimeZone.getDefault()),new SchedulerTime(endTime, TimeZone.getDefault()), (long)waitPeriod);
                        } else if (waitPeriod == 0 && iterations != 0) {
                            recEntry = new RecurringEntry(new SchedulerTime(startTime, TimeZone.getDefault()),new SchedulerTime(endTime, TimeZone.getDefault()), (int)iterations);
                        }
                    }
                } catch (IllegalArgumentException iae) {
                    Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has an invalid recurring entry: " + iae.getMessage());
                    return null;
                }
                
                if (recEntry == null) {
                    Category.SYS_SERVER.logT(Severity.ERROR, location, "The recurring entry of deployed task '"+sheetName+"' has an illegal combination of values.");
                    return null;
                }
                recurringEntryList.add(recEntry);
            }
            // CronEntries
            else if (nestedPSNames[i].startsWith(TASK_CRON_ENTRY)) {
                Properties cronEntrySheet = taskProperties.getNestedProperties(nestedPSNames[i]);      
                // we expect here only one entry with name 'entry'
                
                String cronEntryStr = null;
                if ( (cronEntryStr = cronEntrySheet.getProperty(CRON_ENTRY_NAME)) != null ) {                
                    String value = cronEntryStr.trim();
                    CronEntry cronEntry = null;
                    try {
                        cronEntry = new CronEntry(value);
                    } catch (IllegalArgumentException iae) {
                        Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has an illegal cron entry in the PropertySheet '"+nestedPSNames[i]+"'.");
                        return null;
                    }
                    cronEntriesList.add(cronEntry);
                } else {
                    // there's no entry 'entry' in the CronEntryX PropertySheet
                    Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has no defined entry in the PropertySheet '"+nestedPSNames[i]+"'.");
                    return null;
                }
            } 
            // Filters
            else if (nestedPSNames[i].startsWith(TASK_FILTER)) {
                Properties filterSheet = taskProperties.getNestedProperties(nestedPSNames[i]);      
                
                Date startTime = null;
                Date endTime = null;
                
                // start date and end date has to be set
                String startTimeStr = null;
                if ( (startTimeStr = filterSheet.getProperty(FILTER_START_TIME)) != null ) {                
                    try {
                        startTime = dateFormatter.parse(startTimeStr);
                    } catch (ParseException pe) {
                        Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has an invalid '"+FILTER_START_TIME+"' in the PropertySheet '"+nestedPSNames[i]+"'. StartTime-String: '"+startTimeStr+"'.");
                        return null;
                    }
                } else {
                    // there's no entry 'StartTime' in the FilterX PropertySheet
                    Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has no defined '"+FILTER_START_TIME+"' in the PropertySheet '"+nestedPSNames[i]+"'.");
                    return null;
                }

                // end date
                String endTimeStr = null;
                if ( (endTimeStr = filterSheet.getProperty(FILTER_END_TIME)) != null ) {
                    try {
                        endTime = dateFormatter.parse(endTimeStr);
                    } catch (ParseException pe) {
                        Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has an invalid '"+FILTER_END_TIME+"' in the PropertySheet '"+nestedPSNames[i]+"'. EndTime-String: '"+endTimeStr+"'.");
                        return null;
                    }
                } else {
                    // there's no entry 'EndTime' in the FilterX PropertySheet
                    Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has no defined '"+FILTER_END_TIME+"' in the PropertySheet '"+nestedPSNames[i]+"'.");
                    return null;
                }
                
                Filter filter = null;                
                try {
                    filter = new Filter(new SchedulerTime(startTime, TimeZone.getDefault()), new SchedulerTime(endTime, TimeZone.getDefault()));
                } catch (IllegalArgumentException iae) {
                    Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has an invalid filter entry: " + iae.getMessage());
                    return null;
                }
                filtersList.add(filter);                    
            }
            // invalid nested PropertySheet
            else {
                Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' has too many nested PropertySheets, like '"+nestedPSNames[i]+"'.");
                return null;
            }
        } // for

        // check if we have at least one CronEntry or RecurringEntry defined
        if (cronEntriesList.size() == 0 && recurringEntryList.size() == 0) {
            Category.SYS_SERVER.logT(Severity.ERROR, location, "The deployed task '"+sheetName+"' is not valid. It has no CronEntry and no RecurringEntry defined.");
            return null;
        }
        
        SchedulerTask task = new SchedulerTaskExtension(SchedulerTaskID.newID(), 
                                                        jobDefId, 
                                                        jobParamsList.toArray(new JobParameter[jobParamsList.size()]),
                                                        recurringEntryList.toArray(new RecurringEntry[recurringEntryList.size()]),
                                                        cronEntriesList.toArray(new CronEntry[cronEntriesList.size()]),
                                                        filtersList.toArray(new Filter[filtersList.size()]),
                                                        taskRetentionPeriod,
                                                        taskName,
                                                        taskDescription,
                                                        taskCustomData,
                                                        SchedulerTask.TASK_SOURCE_ZERO_ADMIN,
                                                        TaskStatusInternal.activeInitialZeroAdmin,
                                                        null, // user is not known here
                                                        null);
        return task;
    }
    
    
    protected void markCancelledForZeroAdminTasks() {
        final Connection c = obtainConnection();
        // mark all persisted task from a ZeroAdmin template as cancelled
        try {
            c.setAutoCommit(false);
            m_persistor.markCancelledForZeroAdminTasks(c);
            c.commit();
        } catch (SQLException sqle) {
            throw new SchedulerRuntimeException("Error while marking old ZeroAdmin tasks as cancelled.", sqle);
        } finally {
            closeConnection(c);
        }
    }   
    
    
    protected int removeSchedulerTasks(Timestamp ts) {
        final Connection c = obtainConnection();
        // remove all tasks which are not in status active or hold and older than 7 days
        try {
            c.setAutoCommit(false);
            int countOfRemovedTasks = m_persistor.removeTasks(ts, c);
            c.commit();
            return countOfRemovedTasks;
        } catch (SQLException sqle) {
            throw new SchedulerRuntimeException("Error while removing tasks which are older than "+new Date(ts.getTime()), sqle);
        } finally {
            closeConnection(c);
        }
    } 
    

	/**
	 * Schedules a new task. This method persist the task in the database and schedules it for
	 * execution. This method performs a security check. It can be called only by 
	 */
	public synchronized void schedule(String owner, String runAsUser, SchedulerTask task) throws TaskValidationException {
		//Verify parameters. If parameters are not valid abort the operation.
		try {
    		m_service.theRuntime().verifyParameters(task.getJobDefinitionId(), task.getJobParameters());
    	} catch (ParameterValidationException pve) {
    		//If parameters are not valid just trace the exception. This is not an error,
    		//this is just incorrect input.
    		final String errMsg = "The task being scheduled contained invalid parameters";
    		if (location.beDebug()) location.traceThrowableT(Severity.DEBUG, errMsg, pve);
    		throw new TaskValidationException(errMsg, pve);
    	} catch (NoSuchJobDefinitionException nse) { 
    		throw new TaskValidationException("Task cannot be validated. Job definition \"" + task.getJobDefinitionId() + "\" does not exist.");
    	} catch (SQLException sqle) {
    		//If the runtime threw SQLException log it and throw RuntimeException. There is nothing that the
    		//owner can really do. So throw runtime exception
    		throw logSQLExceptionAndCreateRuntime(sqle);
    		
    	}
    	//Construct a new association between this task and the user that scheduled it.        
    	TaskProcessor newProcessor = new TaskProcessor(task);
    		
		if (newProcessor.getNextExecution() == EntryProcessor.NO_MORE_EXECUTION_TIMES) {
		  	// task which should be scheduled has no more execution times
			// --> throw a TaskValidationException and do not persist it
			throw new TaskValidationException("Task '"+task.getName()+"' with taskId '"+task.getTaskId().toString()+"' has only execution times in the past and thus it can not be scheduled.");	
		}
    		
		final Connection c = obtainConnection();
        try {	
    		c.setAutoCommit(false);
    		m_persistor.persist(task, owner, runAsUser, c);    		
    		m_PQ.schedule(newProcessor);
    		c.commit();
    	} catch (Exception e) { 
    		if (newProcessor != null) m_PQ.remove(task.getTaskId());
    		throw createAndLogRuntimeException(task.getTaskId(), e);
    	} finally {
    		closeConnection(c);
    	}
    	//At this point the jdbc transaction is committed and the processor is scheduled so just make
    	//it active.
    	newProcessor.setActive(true);
        
        // raise the event for a created task
        m_service.theRuntime().raiseEvent(Event.EVENT_TASK_CREATED, task.getTaskId().toString(), null, new Date(), null);
    }

    
    /**
     * Method returns a SchedulerTask where the state is ACTIVE and a given user.
     * If there's a JobDefinition not more available (in case the JobDefinition has
     * been removed meanwhile) to a given task, we will throw a TaskDoesNotExistException.
     * 
     * @param SchedulerTaskID the SchedulerTaskID 
     * @param String the owner string (scheduling- or runAs-user). If the ownerId 
     *               is null we handle it as the Administrator-user
     *  
     * @return SchedulerTask the SchedulerTask 
     *  
     * @exception TaskDoesNotExistException if the JobDefinition does not more exist
     */
    public SchedulerTask getTask(String owner, SchedulerTaskID id) throws TaskDoesNotExistException {
    	final Connection c = obtainConnection();
    	try {
    		return m_persistor.readTask(id, owner, c);
    	} catch (TaskDoesNotExistException tdne) {
    		if (location.beDebug()) location.traceThrowableT(Severity.DEBUG, "", tdne);
    		throw tdne;
    	} catch (Exception e) {
    		throw createAndLogRuntimeException(id, e);
    	} finally {
    		closeConnection(c);
    	}
    }
    
    protected synchronized void finishTask(String owner, SchedulerTaskID taskId) throws TaskDoesNotExistException { 
        Connection c = null;
        try {
            c = obtainConnection();
            c.setAutoCommit(false);
            
            finishTask(owner, taskId, c);
            c.commit();
            // raise the event for a finished task
            m_service.theRuntime().raiseEvent(Event.EVENT_TASK_FINISHED, taskId.toString(), null, new Date(), null);
        } catch (TaskDoesNotExistException tdne) {
            if (location.beDebug()) location.traceThrowableT(Severity.DEBUG, "", tdne);
            throw tdne;
        } catch (Exception e) {
            throw createAndLogRuntimeException(taskId, e);
        } finally {
            closeConnection(c);  
        }
        
    }
    
    private synchronized void finishTask(String owner, SchedulerTaskID taskId, Connection c) throws SQLException, TaskDoesNotExistException {        
        m_persistor.markFinished(owner, taskId, c);        
    }
    
    
    /**
     * Cancels a SchedulerTask where the state is ACTIVE or HOLD for a given user.
     * If the given user is null the owner will be ignored because this method
     * is called by an administrator.
     * 
     * @param SchedulerTaskID the SchedulerTaskID 
     * @param String the owner string (scheduling- or runAs-user). If the ownerId 
     *               is null we handle it as the Administrator-user
     * 
     * @exception TaskDoesNotExistException if the task with the taskId does not more exist
     */
    public synchronized void cancelTask(String owner, SchedulerTaskID taskId, TaskStatus status) throws TaskDoesNotExistException {
    	cancelTaskInternal(owner, taskId, status, false);
    }
    
    
    protected void cancelTaskInternal(String owner, SchedulerTaskID taskId, TaskStatus status, boolean cancelZeroAdminTask) throws TaskDoesNotExistException {        
        final Connection c = obtainConnection();
        TaskProcessor taskProcessor = null;
        try {
            // ZeroAdmin tasks must not be cancelled
            SchedulerTask task = m_persistor.readTask(taskId, owner, c); 
            
            if (!cancelZeroAdminTask) {
                if (task.getTaskSource() == SchedulerTask.TASK_SOURCE_ZERO_ADMIN) {
                    // will be caught below and packed as nested one
                    throw new TaskDoesNotExistException("Task with taskId '"+taskId.toString()+"' can not be cancelled, because it is a ZeroAdmin task.");
                }   
            }
            
            c.setAutoCommit(false);
            m_persistor.markCancelled(taskId, owner, c, status);
            taskProcessor = m_PQ.remove(taskId);
            c.commit();
            
            // raise the event for a cancelled task
            m_service.theRuntime().raiseEvent(Event.EVENT_TASK_CANCELLED, task.getTaskId().toString(), null, new Date(), null);
        } catch (TaskDoesNotExistException tdne) {
            if (location.beDebug()) location.traceThrowableT(Severity.DEBUG, "", tdne);
            throw tdne;
        } catch (Exception e) {
            if (taskProcessor != null ) {
                m_PQ.schedule(taskProcessor);
            }
            throw createAndLogRuntimeException(taskId, e);
        } finally {
            closeConnection(c);
        }
    }
    
    
    /**
     * Sets a SchedulerTask to state HOLD where the state is ACTIVE for 
     * a given user. 
     * If the given user is null the owner will be ignored because this method 
     * is called by an administrator.
     * The task will also be removed from the processing queue.
     * 
     * @param SchedulerTaskID the SchedulerTaskID 
     * @param String the owner string (scheduling- or runAs-user). If the ownerId 
     *               is null we handle it as the Administrator-user
     * 
     * @exception TaskDoesNotExistException if the Task does not exist
     */
    public synchronized void holdTask(String owner, SchedulerTaskID taskId, TaskStatus status) throws TaskDoesNotExistException {
        final Connection c = obtainConnection();
        TaskProcessor taskProcessor = null;
        try {
            c.setAutoCommit(false);
            m_persistor.markHold(taskId, owner, c, status);
            taskProcessor = m_PQ.remove(taskId);
            c.commit();
            
            // raise the event for a held task
            m_service.theRuntime().raiseEvent(Event.EVENT_TASK_HOLD, taskId.toString(), null, new Date(), null);
        } catch (TaskDoesNotExistException tdne) {
            if (location.beDebug()) location.traceThrowableT(Severity.DEBUG, "", tdne);
            throw tdne;
        } catch (Exception e) {
            if (taskProcessor != null ) m_PQ.schedule(taskProcessor);
            throw createAndLogRuntimeException(taskId, e);
        } finally {
            closeConnection(c);
        }
    }
    
    /**
     * Sets a SchedulerTask to state ACTIVE where the state is HOLD for 
     * a given user. The task will also be put again into the PriorityQueue.
     * 
     * If the given user is null the owner will be ignored because this method 
     * is called by an administrator.
     * 
     * @param SchedulerTaskID the SchedulerTaskID 
     * @param String the owner string (scheduling- or runAs-user). If the ownerId 
     *               is null we handle it as the Administrator-user
     * 
     * @exception TaskDoesNotExistException if the Task with taskId and set to 
     *                                      state HOLD does not more exist
     */
    public synchronized void releaseTask(String owner, SchedulerTaskID taskId) throws TaskDoesNotExistException {
        final Connection c = obtainConnection();
        TaskProcessor taskProcessor = null;
        
        try {
            c.setAutoCommit(false);
            Date date = new Date();
            
            // first release the task, because only task in state hold can be released
            m_persistor.markReleased(taskId, owner, c);     
            c.commit();
            // raise the event for a released task
            m_service.theRuntime().raiseEvent(Event.EVENT_TASK_RELEASED, taskId.toString(), null, date, null);
            
            SchedulerTask task = m_persistor.readTask(taskId, owner, c);
            taskProcessor = new TaskProcessor(task);
            
            // in case there were tasks in status hold which have been set back to active,
            // but there are no more execution times in the future, we need to set this 
            // task here to cancel that it will never be accessed again. 
            // In that case we raise 2 events, one event for release and one for finished
            if ( taskProcessor.getNextExecution(System.currentTimeMillis()) == EntryProcessor.NO_MORE_EXECUTION_TIMES ) {
                finishTask(owner, taskId, c);
                c.commit();
                
                // raise the event for a finished task
                m_service.theRuntime().raiseEvent(Event.EVENT_TASK_FINISHED, taskId.toString(), null, date, null);
            } else {
                taskProcessor.setActive(true);            
                m_PQ.schedule(taskProcessor);
            }
            
        } catch (TaskDoesNotExistException tdne) {
            if (location.beDebug()) location.traceThrowableT(Severity.DEBUG, "", tdne);
            throw tdne;
        } catch (Exception e) {
            throw createAndLogRuntimeException(taskId, e);
        } finally {
            closeConnection(c);
        }
    }
    

    /**
     * Method returns all SchedulerTaskIDs which are in state TaskStatus.active or
     * TaskStatus.hold and a given owner. If the owner is null all SchedulerTaskIDs 
     * would be returned, because an Administrator is executing that method.
     * 
     * @param String the owner string (scheduling- or runAs-user). If the ownerId 
     *               is null we handle it as the Administrator-user
     *  
     * @return SchedulerTaskID[] array with the SchedulerTaskIDs 
     */
    public SchedulerTaskID[] getAllSchedulerTaskIDs(String owner) {
    	final Connection c = obtainConnection();
    	try {
    		List<SchedulerTaskID> allTaskIds = m_persistor.getAllTaskIds(owner, c);
    		return (SchedulerTaskID[])allTaskIds.toArray(new SchedulerTaskID[allTaskIds.size()]);
    	} catch (SQLException sqle) {
    		throw logSQLExceptionAndCreateRuntime(sqle);
    	} finally {
    		closeConnection(c);
    	}
    }
    
    
    private interface FiltersObtainer {
    	Filter[] calculateNewFilters(SchedulerTaskID id, Filter[] suppliedFilters,
    			String owner, Connection c) throws TaskDoesNotExistException, SQLException;
    }
    
    
    /**
     * Sets filters for a given task.
     * 
     * @param owner the owner of the task (may be null in cause an Administrator 
     *              has called this method)
     * @param id the SchedulerTaskID
     * @param f the Filter[]
     * @param fo the FiltersObtainer which takes care for the new caluculated filters
     * 
     * @throws TaskDoesNotExistException if a task with the given SchedulerTaskID does not exist
     */
    private synchronized void setFiltersGeneral(String owner, SchedulerTaskID id, Filter[] f, FiltersObtainer fo) throws TaskDoesNotExistException {
    	final Connection c = obtainConnection();
    	TaskProcessor processor = null;
    	SchedulerTask oldTask = null;
    	try {
    		c.setAutoCommit(false);
    		final Filter[] newFilters = fo.calculateNewFilters(id, f, owner, c); 
    		m_persistor.setFilters(id, newFilters, owner, c);
    		processor = m_PQ.get(id);
        	oldTask = processor.getTask();
    		final SchedulerTask taskCopyWithNewFilters = copyTaskWithNewFilters(processor.getTask(), newFilters);
        	processor.setTask(taskCopyWithNewFilters);
    		c.commit();
    	} catch (TaskDoesNotExistException tdne) {
    		if (location.beDebug()) location.traceThrowableT(Severity.DEBUG, "", tdne);
    		throw tdne;
    	} catch (Exception sqle) {
    		if (processor != null && oldTask != null) processor.setTask(oldTask);
    		throw createAndLogRuntimeException(id, sqle);
    	} finally {
    		closeConnection(c);
    	}
    }
    
    public TaskSecurityData getTaskSecurityData(SchedulerTaskID taskId) throws  TaskDoesNotExistException {
    	final Connection c = obtainConnection();
    	try {
    		return m_persistor.getTaskSecurityData(taskId, c);
    	} catch (SQLException sqle) {
    		throw createAndLogRuntimeException(taskId, sqle);
    	} finally {
    		closeConnection(c);
    	}
    	
    }
    
    
    /**
     * Sets filters for a given task.
     * 
     * @param owner the owner of the task (may be null in cause an Administrator 
     *              has called this method)
     * @param id the SchedulerTaskID
     * @param f the Filter[]
     * 
     * @throws TaskDoesNotExistException if a task with the given SchedulerTaskID does not exist
     */
    public void setFilters(String owner, SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException {
    	FiltersObtainer fo = new FiltersObtainer() {
    		public Filter[] calculateNewFilters(SchedulerTaskID id, Filter[] suppliedFilters,
    				String owner, Connection c) {
    			return suppliedFilters;
    		}
    	};
    	setFiltersGeneral(owner, id, f, fo);
    }
    
    /**
     * Adds filters for a given task.
     * 
     * @param owner the owner of the task (may be null in cause an Administrator 
     *              has called this method)
     * @param id the SchedulerTaskID
     * @param f the Filter[]
     * 
     * @throws TaskDoesNotExistException if a task with the given SchedulerTaskID does not exist
     */
    public void addFilters(String owner, SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException {
    	FiltersObtainer fo = new FiltersObtainer() {
    		public Filter[] calculateNewFilters(SchedulerTaskID id, Filter[] suppliedFilters,
    				String owner, Connection c) throws TaskDoesNotExistException, SQLException {
    			SchedulerTask task = m_persistor.readTask(id, owner, c);
        		Set<Filter> filtersSet = new HashSet<Filter>();
        		filtersSet.addAll(Arrays.asList(suppliedFilters));
        		filtersSet.addAll(Arrays.asList(task.getFilters()));
        		return (Filter[])filtersSet.toArray(new Filter[filtersSet.size()]);
        		
    		}
    	};
    	setFiltersGeneral(owner, id, f, fo);
    		
    }

    /**
     * Removes filters for a given task.
     * 
     * @param owner the owner of the task (may be null in cause an Administrator 
     *              has called this method)
     * @param id the SchedulerTaskID
     * @param f the Filter[]
     * 
     * @throws TaskDoesNotExistException if a task with the given SchedulerTaskID does not exist
     */
    public void removeFilters(String owner, SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException { 
    	final FiltersObtainer fo = new FiltersObtainer() {
    		public Filter[] calculateNewFilters(SchedulerTaskID id, Filter[] suppliedFilters,
    				String owner, Connection c) throws TaskDoesNotExistException, SQLException {
    			SchedulerTask task = m_persistor.readTask(id, owner, c);
    			Set<Filter> filtersSet = new HashSet<Filter>();
    			filtersSet.addAll(Arrays.asList(task.getFilters()));
    			filtersSet.removeAll(Arrays.asList(suppliedFilters));
    			return (Filter[])filtersSet.toArray(new Filter[filtersSet.size()]);
    		}
    	};
    	setFiltersGeneral(owner, id, f, fo);
    	
    }
    
    private SchedulerTask copyTaskWithNewFilters(SchedulerTask st, Filter[] newFilters) {
    	return new SchedulerTask(st.getTaskId(),
                                 st.getJobDefinitionId(),
                                 st.getJobParameters(), 
                                 st.getRecurringEntries(),
                                 st.getCronEntries(), 
                                 newFilters, 
                                 st.getRetentionPeriod(),
                                 st.getName(),
                                 st.getDescription(),
                                 st.getCustomData());
    }

    
    /**
     * Method returns the fire times of a SchedulerTask for a given time interval 
     * specified with startTime and endTime.
     * 
     * @param owner the owner of the task (may be null in cause an Administrator 
     *              has called this method)
     * @param id the SchedulerTaskID
     * @param startTime the start time we are interested in fire times
     * @param endTime the end time we are interested in fire times
     * 
     * @return the firetimes fireTime with startTime <= fireTime <= endTime 
     * 
     * @throws TaskDoesNotExistException if a task wit the given id does not exist
     * @throws TooManyFireEventsException if there are more fire times as we allow with
     *                                    the defined maximum
     * @throws IllegalArgumentException if inconsistencies are specified within the both times
     */
    public FireTimeEvent[] getFireTimes(String owner, SchedulerTaskID id, SchedulerTime startTime, SchedulerTime endTime)
                            throws TaskDoesNotExistException, TooManyFireEventsException, IllegalArgumentException {
    	if (startTime.timeMillis() > endTime.timeMillis())
    		throw new IllegalArgumentException("startTime points to a moment in time later than endTime");
    	
    	if (!startTime.getTimeZone().hasSameRules(endTime.getTimeZone()))
    		throw new IllegalArgumentException("startTime and endTime are in different timeZones");
        	
    	final Connection c = obtainConnection();
    	final SchedulerTask task;
    	try {
    		task = m_persistor.readTask(id, owner, c);
    	} catch (TaskDoesNotExistException tdne) {
    		location.traceThrowableT(Severity.DEBUG, "", tdne);
    		throw tdne;
    	} catch (Exception sqle) {
    		throw createAndLogRuntimeException(id, sqle);
    	} finally {
    		closeConnection(c);
    	}
    	final TaskProcessor tp = new TaskProcessor(task);
    	long endTimeLong = endTime.timeMillis();
    	
    	// subtracting 1 to also find the possible execution time startTime.timeMillis()
    	long nextExecution = tp.getNextExecution(startTime.timeMillis() - 1);
    	final ArrayList<FireTimeEvent> events = new ArrayList<FireTimeEvent>(50); 
    	for(int eventsNumber = 0; nextExecution <= endTimeLong && nextExecution != EntryProcessor.NO_MORE_EXECUTION_TIMES; eventsNumber++) {
    		if (eventsNumber > maxFireEventsNumber) {
    			String errMsg = "Fire event times for task " + id +
    					" for the period [" + startTime + " - " + endTime + "] exceed the" +
    					" allowed number of calculated fire times. Please retry this call with" +
    					" a shorter period. The maximum number of calculated timeouts is: " + maxFireEventsNumber;
    			location.debugT(errMsg);
    			throw new TooManyFireEventsException(errMsg);
    		}
    		final SchedulerTime thisEventTime = new SchedulerTime(nextExecution, startTime.getTimeZone());
    		final boolean filtered = Filters.filterOut(task.getFilters(), nextExecution) != null ;
    		final FireTimeEvent thisEvent = new FireTimeEvent(id, thisEventTime, filtered);
    		events.add(thisEvent);
    		nextExecution = tp.getNextExecution(nextExecution);
    	}
    	return (FireTimeEvent[])events.toArray(new FireTimeEvent[events.size()]);
    }
    
    
    public LogIterator getJobLog(JobID jobId, LogIterator logIter, int resultSetSize) throws NoSuchJobException { 
        try {
            return m_service.theRuntime().getJobLog(jobId, logIter, resultSetSize);
        } catch (SQLException sql) { 
            throw new SchedulerRuntimeException("Error occured while DB access.", sql);
        }
    }
    
    
    public SchedulerLogRecordIterator getJobLogRecords(JobID jobId, SchedulerLogRecordIterator logIter, int resultSetSize) throws NoSuchJobException { 
        try {
            return m_service.theRuntime().getJobLogRecords(jobId, logIter, resultSetSize);
        } catch (SQLException sql) { 
            throw new SchedulerRuntimeException("Error occured while DB access.", sql);
        }
    }
    
       
    /**
     * Method returns all Tasks which are in state TaskStatus.active or
     * TaskStatus.hold (independent from any user). 
     * If there's a JobDefinition not more available (in case the JobDefinition has
     * been removed meanwhile) to a given task, we will log an error an continue with 
     * execution.
     * 
     * @return SchedulerTask[] the array of the tasks 
     */
    public SchedulerTask[] getAllSchedulerTasks() {
        return getAllSchedulerTasks(null, null);
    }
    
    
    /*
     * Non-interface Method
     * 
     * Method returns Tasks for a given status and/or taskSource(independent from 
     * any user) (@see SchedulerTask, TaskStatus).
     *  
     * Note: If taskSourceParam is null, it will be ignored by the where clause. 
     *       If status is null both states (TaskStatus.active and TaskStatus.active)
     *       will be considered in the where-clause.
     *       
     * If there's a JobDefinition not more available (in case the JobDefinition has
     * been removed meanwhile) to a given task, we will log an error an continue with 
     * execution.
     * 
     * @return SchedulerTask[] the array of the tasks  
     * @param taskSource the TaskSource
     * @param status the TaskStatus
     * @return
     */
    public SchedulerTask[] getAllSchedulerTasks(Short taskSource, TaskStatus status) {
        final Connection c = obtainConnection();
        try {
            List<SchedulerTask> allSchedulerTasks = null;
            if (taskSource == null && status == null) {
                allSchedulerTasks = m_persistor.getAllTasks(c);
            } else {
                allSchedulerTasks = m_persistor.getAllTasks(c, taskSource, status);
            }
            
            return (SchedulerTask[])allSchedulerTasks.toArray(new SchedulerTask[allSchedulerTasks.size()]);
        } catch (SQLException sqle) {
            throw logSQLExceptionAndCreateRuntime(sqle);
        } finally {
            closeConnection(c);
        }      
    }
    

    protected SchedulerRuntimeException createAndLogRuntimeException(SchedulerTaskID id, Exception sqle) {
    	final String errMsg = "An exception has ocurred while persisting state of task " + id +
			" Please analyse the stack trace and/or send it to SAP Support center."; 
    	Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, 
    			errMsg, sqle);
    	return new SchedulerRuntimeException(errMsg, sqle);
    }
  
    static final SchedulerRuntimeException logSQLExceptionAndCreateRuntime(SQLException sqle) {
    	final String errMsg = "An error has occurred while accessing the database." +
    		" Please analyse the stack trace and/or send it to SAP Support center." +
			" The most probable reason for this error is that the database is not responding";
    	Category.SYS_SERVER.logThrowableT(Severity.ERROR, location,
    			errMsg, sqle);
    	throw new SchedulerRuntimeException(errMsg,sqle);
    }
    
    protected void closeConnection(Connection c) {
    	try {
    		if (c != null) c.close();
    	} catch (SQLException sqle) {
    		Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, 
    				        "Unable to close jdbc connection. Due to this error the connection might" +
    						" not has been returned to the connection pool and thus less jdbc connections might" +
    						" be available for this data-source.", sqle);
    	}
    }
    
    protected Connection obtainConnection() {
    	try {
    		return m_ds.getConnection();
    	} catch (SQLException sqle) {
    		final String errMsg = "Unable to obtain connection from data-source.";
    		Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, errMsg, sqle);
    		throw new SchedulerRuntimeException(errMsg, sqle);
    	}
    }
    
    protected SchedulerID schedulerId() {
    	if (m_thisSchedulerId == null) {
	    	try {
				m_thisSchedulerId = m_service.theRuntime().getBuiltinScheduler().getId();
			} catch (SQLException sqle) {
				throw logSQLExceptionAndCreateRuntime(sqle);
			}
    	}
    	return m_thisSchedulerId;
    }
    
    /**
     * Returns all SchedulerTasks from the PriorityQueue.
     * @return SchedulerTask[] 
     */
    protected SchedulerTask[] getAllSchedulerTasksFromPriorityQueue() {
        SchedulerTaskID[] taskIds = m_PQ.getAllTaskIDsFromPriorityQueue();
        SchedulerTask[] tasks = new SchedulerTask[taskIds.length];
        
        for (int i = 0; i < taskIds.length; i++) {
            try {
                // call with null --> method will only be called in Administrator-Context
                tasks[i] = getTask(null, taskIds[i]);
            } catch (TaskDoesNotExistException e) {
                category.logThrowableT(Severity.ERROR, location, "SchedulerTask with id "+taskIds[i]+" does not exist at db-level.", e);
            }            
        }
        return tasks;
    }
    
    
    protected boolean hasSingleExecutionTime(SchedulerTask task) {
    	// a task can have only a single execution time when it has only one entry
    	// (Recurring-/CronEntry) and this one entry has in case it is an RecurringEntry
    	// the same start- and endTime. And if it is a CronEntry all values should be 
    	// single and static values (no wildcard), except the day_of_week, it might be a
    	// wildcard.
    	
    	if (task.getCronEntries() != null && task.getCronEntries().length == 1) {
    		if (task.getRecurringEntries() == null || task.getRecurringEntries().length == 0) {
    			// we have exactly one cron entry 
    			// check it if it has only a single execution time
    			
    			CronEntry cron = task.getCronEntries()[0];
    			String[] cronStrArr = new String[5];
    			cronStrArr[0] = cron.getYears().persistableValue();
    			cronStrArr[1] = cron.getMonths().persistableValue();
    			cronStrArr[2] = cron.getDays_of_month().persistableValue();
    			cronStrArr[3] = cron.getHours().persistableValue();
    			cronStrArr[4] = cron.getMinutes().persistableValue();
    			
    			// we ignore the day of week, because it might be a wildcard or a concrete value
    			// to have still a single execution time, so long all other fileds have 
    			// concrete values    			
    			for (int i = 0; i < cronStrArr.length; i++) {
	    			try {
	    				Long.parseLong(cronStrArr[i]);
	    			} catch (NumberFormatException nfe) {
	    				// $JL-EXC$
	    				// in case the NumberFormatException is thrown it means that 
	    				// we have not only single, static values
	    				return false;
	    			}  
    			}
    			
    			// cron has only a single execution
    			return true;
    		}
    	} else if (task.getRecurringEntries() != null && task.getRecurringEntries().length == 1) {
    		if (task.getCronEntries() == null || task.getCronEntries().length == 0) {
    			RecurringEntry entry = task.getRecurringEntries()[0];
    			SchedulerTime startTime = entry.getStartTime();
    			SchedulerTime endTime = entry.getEndTime();
    			if (startTime != null && startTime.equals(endTime)) {
    				// start time and end time is equal, we have only a single entry
    				return true;
    			}
    		}    		
    	}
    	
    	// in all other cases we have more than one execution time
    	return false;
    }
    
}
