/*
 * Created on 07.12.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.scheduler.impl;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.EventConsumer;
import com.sap.scheduler.runtime.JobExecutorException;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.NoSuchJobDefinitionException;
import com.sap.scheduler.runtime.NoSuchUserException;
import com.sap.scheduler.runtime.ParameterValidationException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


public class EventConsumerImpl implements EventConsumer {
    
    private final static Location LOCATION = Location.getLocation(EventConsumerImpl.class);
    
    private ServiceFrame m_service = null;
    
    protected Map<JobID, SchedulerTask> m_runningJobsToTasksMap = new HashMap<JobID, SchedulerTask>();
    protected Map<SchedulerTask, JobID> m_runningTasksToJobMap = new HashMap<SchedulerTask, JobID>();
    protected Map<SchedulerTask, Integer> m_waitingTasksToCountMap = new HashMap<SchedulerTask, Integer>();    
    protected Object m_refireJobMonitor = new Object();
    
    // Hashtable needed to identify Tasks which have jobs which are failing several times in a row
    protected Hashtable m_jobsToTaskMap = new Hashtable(); // JobID to SchedulerTask
    protected Map<SchedulerTask, Integer> m_tasksToFailedCount = new HashMap<SchedulerTask, Integer>();
    
    protected EventConsumerImpl(ServiceFrame service) {
        m_service = service;
    }
    
    
    public void handle(Event e) {
        String eventType = e.getType();
        
        if ( eventType.equals(Event.EVENT_JOB_FINISHED) ) {
            handleJobFinished(e);
        }            
        else if (eventType.equals(Event.EVENT_JOB_DEFINITION_UNDEPLOYED1)) {        
            handleJobDefinitionUndeployed1(e);
        } 
        else {
            // should not occur
            Category.SYS_SERVER.logT(Severity.WARNING, LOCATION, "Event was sent for which nobody has been registered. Event-details: "+e.toString());
        }
    } // handle
    
    
    public String getName() {
        return this.getClass().getName();
    }
    
    
    private void handleJobFinished(Event e) {
        JobID jobId = JobID.parseID(e.getParameter());
        
        if (LOCATION.beDebug()) LOCATION.debugT("Job with ID '"+jobId+"' has finished. Event-details: "+e.toString());
        
        synchronized (m_refireJobMonitor) {
            if ( m_runningJobsToTasksMap.get(jobId) != null ) {
                SchedulerTask task = m_runningJobsToTasksMap.remove(jobId);
                m_runningTasksToJobMap.remove(task);
                
                // retrigger the next execution if we have waiting executions for this task
                Integer waitingExecs = null;
                if ( (waitingExecs = m_waitingTasksToCountMap.remove(task)) != null ) {
                    int waitingExecutions = waitingExecs.intValue();
                    if (waitingExecutions > 0) {
                        // retrigger job on this server node
                        // log the amount of skipped jobs
                        if ( (waitingExecutions-1) > 0 ) {
                            // TODO: improve exception text with hint for property which can be set for a task
                            Category.SYS_SERVER.logT(Severity.WARNING, LOCATION, (waitingExecutions-1)+" job executions of task '"+task.getName()+"' with TaskID '"+task.getTaskId()+"' has been skipped in cause of avoiding overlapping jobs.");
                        }
                        try {
                            // user info not always stored in SchedulerTask (compare SchedulerTask & SchedulerTaskExtension)
                            TaskSecurityData tsd = m_service.getScheduler().getTaskSecurityData(task.getTaskId());
                          
                            JobID execJobID = m_service.theRuntime().executeJob(task.getJobDefinitionId(),
                                                                                task.getJobParameters(),
                                                                                new Integer(task.getRetentionPeriod()),
                                                                                null, // null for ParentJobID
                                                                                m_service.getScheduler().schedulerId(),
                                                                                tsd.runAsUser(),
                                                                                task.getTaskId());
                            // add here this task again that the TaskExecutorImpl knows this task executes a job currently
                            m_runningJobsToTasksMap.put(execJobID, task);
                            m_runningTasksToJobMap.put(task, execJobID);
                            
                            // add it also to the table responsible for the maintainance of failed jobs
                            m_jobsToTaskMap.put(execJobID, task);

                            if (LOCATION.beDebug()) LOCATION.debugT("Task '"+task.getName()+"' and TaskID '" + task.getTaskId() + "' was submitted to job execution runtime. Generated JobID was '" + execJobID+ "'.");
                        } catch (ParameterValidationException pve) {
                            Category.SYS_SERVER.logT(Severity.ERROR, LOCATION, "Parameter of task '"+task.getName()+"' with TaskID '" + task.getTaskId() + "' not valid.");
                        } catch (NoSuchJobDefinitionException nsjde) {
                            Category.SYS_SERVER.logT(Severity.ERROR, LOCATION, "Job of task '"+task.getName()+"' with TaskID '" + task.getTaskId() + "' does not exist.");
                        } catch (JobExecutorException jee) {
                            Category.SYS_SERVER.logThrowableT(Severity.ERROR, LOCATION, "Error while executing task '"+task.getName()+"' with TaskID '" + task.getTaskId() + "'.", jee);
                        } catch (NoSuchUserException nsue) {
                            Category.SYS_SERVER.logThrowableT(Severity.ERROR, LOCATION, "User '"+task.getRunAsUser()+"' of task '"+task.getName()+"' with TaskID '" + task.getTaskId() + " does not exist.", nsue);
                        } catch (TaskDoesNotExistException tdnee) {
                            Category.SYS_SERVER.logThrowableT(Severity.ERROR, LOCATION, "Task '"+task.getName()+"' with TaskID '" + task.getTaskId() + " does not exist.", tdnee);
                        }
                    }
                }
            }
        }
        
        // Handle the case that more than specific amount of jobs in a row fail
        // we will set the task to hold
        // synchronized to keep the both structures m_jobsToTaskMap and 
        // m_tasksToFailedCount consistent
        synchronized(m_jobsToTaskMap) { 
            if( JobStatus.ERROR.toString().equals(e.getAdditionalParameter()) ) {
                SchedulerTask task = null;            
                Integer count = null;
                // remove the entry in every case
                if ( (task = (SchedulerTask)m_jobsToTaskMap.remove(jobId)) != null ) {
                    if ( (count = m_tasksToFailedCount.get(task)) != null ) {
                        int val = count.intValue();
                        val++;
                        long numberOfFailedJobs = SingletonEnvironment.getLongValueFromProperty(m_service.getServiceProperties(), 
                        																		SingletonEnvironment.NUMBER_OF_FAILED_JOBS_NAME, 
                        																		SingletonEnvironment.NUMBER_OF_FAILED_JOBS);
                        if ( val >=  numberOfFailedJobs) {
                            try {
                                m_service.getScheduler().holdTask(null, task.getTaskId(), TaskStatusInternal.holdError);
                                // cleanup maps
                                m_tasksToFailedCount.remove(task);                                
                                Category.SYS_SERVER.logT(Severity.WARNING, LOCATION, "Task '"+task.getName()+"' with TaskID '"+task.getTaskId()+"' has been set to hold in cause of '"+SingletonEnvironment.NUMBER_OF_FAILED_JOBS+"' failed jobs in a row.");
                            } catch (TaskDoesNotExistException tdnee) {
                                Category.SYS_SERVER.logThrowableT(Severity.ERROR, 
                                        LOCATION, 
                                        "Error while trying to hold task with id '"+task.getTaskId()+"'", 
                                        tdnee);
                            }
                        } else {
                            // increase the count of failed jobs
                            m_tasksToFailedCount.put(task, new Integer(val));
                        }
                    } else {
                        // count is still 0 --> initialize it with 1
                        m_tasksToFailedCount.put(task, new Integer(1));
                    }
                }
            } else {
                // job is not in error status --> cleanup maps
                SchedulerTask task = (SchedulerTask)m_jobsToTaskMap.remove(jobId);
                if(task != null) {
                    m_tasksToFailedCount.remove(task);
                }
            }
        } // synchronized
        
    } // handleJobFinished
    
    
    private void handleJobDefinitionUndeployed1(Event e) {
        // cancel all tasks of undeployed JobDefinition
        String jobDefId = e.getParameter();
        
        // access all active and hold tasks 
        SchedulerTask[] tasks = m_service.getScheduler().getAllSchedulerTasks();
       
        for (int i = 0; i < tasks.length; i++) {
            try {
                if (tasks[i].getJobDefinitionId().toString().equals(jobDefId)) {
                    // Scheduler runs on this node
                    // call with null --> administrative mode 
                    m_service.getScheduler().cancelTaskInternal(null, tasks[i].getTaskId(), TaskStatusInternal.finishedUndeployed, true);
                }
            } catch (TaskDoesNotExistException tdnee) {
                Category.SYS_SERVER.logThrowableT(Severity.ERROR, 
                                                 LOCATION, 
                                                 "Error while canceling a task associated with the undeployed JobDefinition with ID '"+jobDefId+"'", 
                                                 tdnee);
            }
        }
    } //handleJobDefinitionUndeployed1

}
