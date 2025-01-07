/*
 * Created on 05.12.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.scheduler.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.sap.engine.services.scheduler.util.Filters;
import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.api.TaskStatus;
import com.sap.scheduler.runtime.EventConsumer;
import com.sap.scheduler.runtime.JobExecutorException;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.NoSuchJobDefinitionException;
import com.sap.scheduler.runtime.NoSuchUserException;
import com.sap.scheduler.runtime.ParameterValidationException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


public class TaskExecutorImpl implements TaskExecutor { 

    private static final Location LOCATION = Location.getLocation(TaskExecutorImpl.class);
    
    private ServiceFrame m_service = null;
    private TaskPersistor m_persistor = null;
    private EventConsumerImpl m_eventConsumer = null;
    
    protected TaskExecutorImpl(ServiceFrame service, TaskPersistor persistor, EventConsumer eventCon) {
        m_service = service;
        m_persistor = persistor;
        m_eventConsumer = (EventConsumerImpl)eventCon;
    }
    
    
    public TaskStatus executeTask(SchedulerTask task, long executionTime) throws SQLException, TaskDoesNotExistException,
        JobExecutorException, ParameterValidationException {
        //filter task out if necessary.
        final Filter whichFiltered = Filters.filterOut(task.getFilters(), executionTime);
        if (whichFiltered != null) { //task is filtered out by filter whichFiltered
            if (LOCATION.beDebug()) LOCATION.debugT("Timeout execution of task " +
                    task.getTaskId() + " at " + executionTime + 
                    " ms. has been filtered out by filter " + whichFiltered);
            return TaskStatus.active;
        }
        //task has passed all filters so continue executing this timeout
        final Connection c = m_service.getScheduler().obtainConnection();
        String user = null;
        try {
            TaskSecurityData tsd = m_persistor.getTaskSecurityData(task.getTaskId(), c);
            user = tsd.runAsUser();
            final Integer retentionPeriod = task.getRetentionPeriod() == -2 
                ? null : new Integer(task.getRetentionPeriod());
                
            JobID executedJobId = null;
            // if the property "enableJobExecution" is set, we execute all jobs
            if ( m_service.getServiceProperties().getProperty(SingletonEnvironment.ENABLE_JOB_EXECUTION_NAME, "true").equals("true") ) {
                synchronized (m_eventConsumer.m_refireJobMonitor) {
                    // is there already a job of this task running
                    if ( m_eventConsumer.m_runningTasksToJobMap.get(task) != null ) {
                        // we have a running job of this task --> do not execute
                        
                        // do we have already waiting jobs of this task
                        Integer countOfWaitingJobs = null;
                        if ( (countOfWaitingJobs = m_eventConsumer.m_waitingTasksToCountMap.get(task)) != null ) {
                            // if yes we increase the waiting count
                            int waitingJobs = countOfWaitingJobs.intValue();
                            waitingJobs++;
                            m_eventConsumer.m_waitingTasksToCountMap.put(task, new Integer(waitingJobs));

                            if (LOCATION.beDebug()) LOCATION.debugT("Task '" + task.getName()+ "' and TaskID '" + task.getTaskId()+ "' added to waiting jobs. Count of waiting jobs="+waitingJobs+".");
                        } else {
                            // there are no jobs of this task which waits --> put the first waiting job of this task to the queue
                            m_eventConsumer.m_waitingTasksToCountMap.put(task, new Integer(1));

                            if (LOCATION.beDebug()) LOCATION.debugT("Task '" + task.getName()+ "' and TaskID " + task.getTaskId()+ " added to waiting jobs. Count of waiting jobs=1.");
                        }
                    } else {
                        // we do NOT have a running job of this task --> execute it
                        executedJobId = m_service.theRuntime().executeJob(task.getJobDefinitionId(),
                                                                                      task.getJobParameters(), 
                                                                                      retentionPeriod, 
                                                                                      null, 
                                                                                      m_service.getScheduler().schedulerId(),  
                                                                                      tsd.runAsUser(),
                                                                                      task.getTaskId()
                                                                                      );
                        m_eventConsumer.m_runningTasksToJobMap.put(task, executedJobId);
                        m_eventConsumer.m_runningJobsToTasksMap.put(executedJobId, task); 

                        if (LOCATION.beDebug()) LOCATION.debugT("Task with id " + task.getTaskId() + "/ JobID "+executedJobId+" was submitted to job execution runtime. Generated job id was " + executedJobId);
                    }
                } // synchronized
                
                // add executed JobID to map to handle erroneous behaviour
                if (executedJobId != null) {
                    synchronized (m_eventConsumer.m_jobsToTaskMap) {
                        m_eventConsumer.m_jobsToTaskMap.put(executedJobId, task);
                    }  
                }
            }               

            return TaskStatus.active;
        } catch (NoSuchJobDefinitionException nsjde) {
            Category.SYS_SERVER.logThrowableT(Severity.ERROR, LOCATION,
                    "Error occurred while executing a job for task id " + task.getTaskId() +
                    ". The job definition has been undeployed", nsjde);
            // TODO: Would it be better to use here the status TaskStatusInternal.finishedUndeployed ?
            return TaskStatus.finished;
        } catch (NoSuchUserException nsu) {
            throw new JobExecutorException("User \"" + user + "\" does not exist. Job not executed.");
        } finally {
            m_service.getScheduler().closeConnection(c);
        }
    }
    
    public void taskStateChanged(SchedulerTask task, TaskStatus newTaskState) throws TaskDoesNotExistException, SQLException {
        // TODO: newTaskState not more used, because this method is 
        // only called for finished tasks (see also todo above)
        m_service.getScheduler().finishTask(null, task.getTaskId());
    }        
        
}
