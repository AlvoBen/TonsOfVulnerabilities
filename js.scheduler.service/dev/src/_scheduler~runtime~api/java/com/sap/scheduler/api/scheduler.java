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

import java.io.Serializable;
import java.sql.SQLException;

import com.sap.scheduler.runtime.EventConsumer;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIllegalStateException;
import com.sap.scheduler.runtime.JobIterator;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.SchedulerRuntimeException;


/**
 * This is the main interface to the NetWeaver Scheduler for Java. Applications
 * can use this interface for all scheduler related tasks which includes scheduling
 * jobs, viewing jobs, and browsing logfiles. 
 * <p>
 * The methods of this class always return information which are related to the 
 * calling user. Administrative users will be able to access all information.
 */
public interface Scheduler {
    
	/**
     * Instances of this class specify a particular expiration of a given scheduler task. An object of
     * the <code>FireTimeEvent</code> class contains the scheduler task id and the time at which the expiration
     * should deliver. For a feature expirations this may not be the exact moment as objects of this
     * class represented a computed time, which may not be exactly the moment of actual expiration.
     * <p>
     * Note: Filtered events are also returned with the <code>filtered</code> field set to 
     * <code>true</code>. Those instances will not be executed.
     */
    public class FireTimeEvent implements Serializable {
        /**
         * The id of the task whose particular expiration is represented by this object.
         */
    	public final SchedulerTaskID taskId;
        /**
         * Time of the particular expiration represented by this object.
         */
    	public final SchedulerTime time;
        /**
         * Indicates whether the current set of filters has filtered this expiration out.
         * True if this expiration has been filtered, false otherwise.
         */
    	public final boolean filtered;
        
        /**
         * Constructs a new <code>FireTimeEvent</code> instance to represent an 
         * expiration of the given scheduler task at the given time.
         * <p>
         * Note: This constructor is only used internally.
         * <p>  
         * @param taskId id of the task whose particular expiration is represented by this object
         * @param time time of this expiration
         * @param filtered sets the filtered attribute of this task. If true than a consumer of
         * this task may assume that the current set of filters of this task has filtered this
         * particular expiration out.
         */
        public FireTimeEvent(SchedulerTaskID taskId, SchedulerTime time, boolean filtered) {
            if ((this.taskId = taskId) == null)
                throw new NullPointerException("taskId");
            if ((this.time = time) == null)
                throw new NullPointerException("time");
            this.filtered = filtered;
        }
        
        
        /**
         * Constructs a new <code>FireTimeEvent</code> instance to represent an 
         * expiration of the given scheduler task at the given time.
         * <p>
         * Note: This constructor is only used internally.
         * <p>  
         * @param taskId id of the task whose particular expiration is represented by this object
         * @param time time of this expiration
         */
        public FireTimeEvent(SchedulerTaskID taskId, SchedulerTime time) {
        	this (taskId, time, false);
        }
    }

    /**
     * This method schedules the supplied task what means that the task will be
     * persisted and performed.
     * @param task the task to be scheduled
     * @throws TaskValidationException thrown if a task with this id is already 
     *                                 scheduled or some of the fields are invalid
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                 there is a problem with the underlying infrastructure. 
     */
    public void schedule(SchedulerTask task) throws TaskValidationException, SchedulerRuntimeException;

    /**
     * Obtains the task with id specified by the <code>id</code> parameter. Only 
     * one task with this id can exist.
     * 
     * @param id the id of the task
     * @return the task with id that equals <code>id</code> parameter
     * @throws TaskDoesNotExistException thrown if task with this id does not exist.
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.
     */
    public SchedulerTask getTask(SchedulerTaskID id) throws TaskDoesNotExistException, SchedulerRuntimeException;

    /**
     * Cancels a scheduled task with id specified by the <code>taskId</code> parameter.
     * 
     * @param taskId the id of the task to be cancelled
     * @throws TaskDoesNotExistException if a task with such id does not exist or 
     *                                   the task can not be cancelled
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.     
     */
    public void cancelTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, SchedulerRuntimeException;
    
    /**
     * Holds a scheduled task with id specified by the <code>taskId</code> parameter. 
     * Holding a task means that it will be suspended from further execution until
     * <code>realeaseTask(taskId)</code> will be called which sets the task back to active. 
     * @see #releaseTask(SchedulerTaskID taskId)
     * 
     * @param taskId id of the task to be hold
     * @throws TaskDoesNotExistException if a task with such id does not exist.
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public void holdTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, SchedulerRuntimeException;
    
    /**
     * Releases a held task with id specified by the <code>taskId</code> parameter and 
     * set it back to state active. This method takes only effect if the task was 
     * set to hold before.
     * @see #holdTask(SchedulerTaskID taskId)
     * 
     * @param taskId id of the task to be released
     * @throws TaskDoesNotExistException if a task with <code>taskId</code> and set to HOLD
     *                                   does not exist.
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public void releaseTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, SchedulerRuntimeException;

    /**
     * Sets filters for the specified task. This method replaces all filters currently associated to this
     * task with the filters supplied by <code>f</code> parameter. This method could also be useful to remove
     * all filters associated with the given task by calling <code>setFilters(id, new Filter[0])</code>.
     * 
     * @param id id of the task
     * @param f array of filters
     * @throws TaskDoesNotExistException - if task with id specified by <code>id</code> parameter does not exist
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public void setFilters(SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException, SchedulerRuntimeException;

    /**
     * Adds a filter to the list of filters currently associated to the given task.
     * 
     * @param id id of the task
     * @param f array of filters to be associated with the given task
     * @throws TaskDoesNotExistException thrown if the given task does not exist
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public void addFilters(SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException, SchedulerRuntimeException;

    /**
     * Removes the given filters from the list of filters associated with the given task.
     * This method does not throw an exception if any of the filters supplied by the array
     * <code>f</code> are not found among the list of filters associated with the given task. 
     * It simply removes all the filters that are found both in <code>f</code> and the list of currently
     * associated filters.
     * 
     * @param id task whose filters will be removed
     * @param f filters which will be removed from the list of filters associated to task <code>id</code>
     * @throws TaskDoesNotExistException thrown if the task specified by <code>id</code> does not exist.
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public void removeFilters(SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException, SchedulerRuntimeException;

    /**
     * Obtains all currently scheduled scheduler tasks which are in state {@link TaskStatus#active active} or
     * {@link TaskStatus#hold hold}.
     * 
     * @return an array of all currently scheduled tasks. This method never returns null. If there
     *         are no currently scheduled tasks it returns an empty array.
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public SchedulerTaskID[] getAllSchedulerTaskIDs() throws SchedulerRuntimeException;

    /**
     * This method returns the fire times of a <code>SchedulerTask</code> for a 
     * given time interval specified by the <code>startTime</code>, <code>endTime</code>
     * and the <code>id</code> parameters.
     * 
     * @param id the task id
     * @param startTime the start time of the interval
     * @param endTime the end time of the interval
     * @return a possible empty array of {@link FireTimeEvent FireTimeEvents} 
     * 
     * @throws TooManyFireEventsException if there are too many executions of this task 
     *                                    in the given interval
     * @throws TaskDoesNotExistException thrown if the task specified by <code>id</code> does not exist.
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public FireTimeEvent[] getFireTimes(SchedulerTaskID id, SchedulerTime startTime, SchedulerTime endTime) throws TaskDoesNotExistException, TooManyFireEventsException, SchedulerRuntimeException;
    
    /**
     * Returns all job definitions known to the NetWeaver Scheduler. 
     * <p>
     * Note: Also undeployed job definitions are also returned
     * 
     * @return an array of JobDefinitions known to the scheduler (may
     * by empty)
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public JobDefinition[] getJobDefinitions() throws SchedulerRuntimeException;

    /**
     * Returns the job defintion for a given name. The format for a job definitions 
     * name is &lt;providerName&gt;/&lt;applicationName&gt;:&lt;jobName&gt;
     * <p>
     * Note: This method will only return current job definitions. It 
     *       will not return job definitions from undepoyed applications.  
     * 
     * @param jobDefinitionName the name of job definition     
     * @return the job definition or <code>null</code> if there is no such job definition
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public JobDefinition getJobDefinitionByName(String jobDefinitionName) throws SchedulerRuntimeException;
    
    /**
     * Get job definition by its job definition id.
     * <p>
     * Note: This method will return also job definitions from undeployed
     *       applications.
     *       
     * @param id the job definition id
     * @return job definition or <code>null</code> if there is no such job definition
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public JobDefinition getJobDefinitionById(JobDefinitionID id) throws SchedulerRuntimeException;
    

    /**
     * This method cancels a job. If it has not been started it will immediately go into
     * the {@link JobStatus#CANCELLED CANCELLED} state. If it has been started, 
     * it will cooperatively try to abort the job. This method will just return with no 
     * indication whether the job was successfully cancelled or not. 
     * <p>
     * This method will only allow jobs to be cancelled which run under the
     * same user id as the caller.
     * 
     * @see com.sap.scheduler.runtime.JobContext#isCancelled()
     * @param jobid job id for the running job
     * 
     * @throws JobIllegalStateException thrown if the job is not status 
     *                                  {@link JobStatus#RUNNING RUNNING}
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public void cancelJob(JobID jobid) throws JobIllegalStateException, SchedulerRuntimeException;

    
    //------------------------------------------------------------------
    // Parent/Child Functionality
    //------------------------------------------------------------------

    /**
     * Get all child jobs for a given job id.
     * 
     * @param jobid the id for the job
     * @return possible empty array of child jobs
     * 
     * @throws NoSuchJobException thrown if a job with the given job id does not exist 
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure. 
     */
    public Job[] getChildJobs(JobID jobid) throws NoSuchJobException, SchedulerRuntimeException;

    /**
     * Returns true if this job has child jobs. 
     * the caller
     * 
     * @param jobid job id to use
     * @return true if the job has child jobs, false otherwise
     * @throws NoSuchJobException thrown if a job with the given job id does not exist 
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.
     */
    public boolean hasChildJobs(JobID jobid) throws NoSuchJobException, SchedulerRuntimeException;
    

    //------------------------------------------------------------------
    // Maintining runtime job information
    //------------------------------------------------------------------

    /**
     * This method removes all information about this job instance from 
     * the NetWeaver Scheduler (including logs). This method only works on jobs 
     * which are in state {@link JobStatus#COMPLETED COMPLETED}, 
     * {@link JobStatus#ERROR ERROR} or {@link JobStatus#UNKNOWN UNKNOWN}, 
     * {@link JobStatus#CANCELLED CANCELLED}.
     * 
     * @param jobid the job id to use
     * @throws JobIllegalStateException thrown if the to be removed is not in one state 
     *                                  mentioned above. 
     * @throws NoSuchJobException thrown if a job with the given job id does not exist 
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.
     */
    public void removeJob(JobID jobid) throws JobIllegalStateException, NoSuchJobException, SchedulerRuntimeException;

    /**
     * This method removes all information for the given job instances from the 
     * NetWeaver Scheduler (including logs). This is a convenience method. 
     * Logical errors (e.g. one or more jobs in an illegal state) will be ignored.
     * 
     * @param jobids the job ids to use
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.
     */
    public void removeJobs(JobID[] jobids) throws SchedulerRuntimeException;

    /**
     * This method returns a job for a given job id <code>jobid</code>.
     * 
     * @param jobid the job id of the job
     * @return the job or <code>null</code> if there is no such job 
     * 
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.
     */
    public Job getJob(JobID jobid) throws SchedulerRuntimeException;
    
    /**
     * This method returns all parameters for a given job id <code>jobid</code>.
     * 
     * @param jobid the given job is
     * @return job parameters for specified job
     * @throws NoSuchJobException if there is no job with the given id
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.
     */
    public JobParameter[] getJobParameters(JobID jobid) throws NoSuchJobException, SchedulerRuntimeException;

    /**
     * This method returns the jobs for given job ids.
     * 
     * @param jobid the job ids of the jobs
     * @return an array of jobs. The size of the returned array is the same as the size 
     *         of the passed one. If a job does not exist the field in the array will 
     *         be <code>null</code>.
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.
     */
    public Job[] getJobs(JobID[] jobid) throws SchedulerRuntimeException;
    
    /**
     * This method will return the log for the given job in chunks. If the result 
     * set is bigger than the provided fetchSize parameter the remaining entries 
     * can be retrieved using the returned iterator. The following code snipped 
     * shows how to retrieve the result from the call.
     * <pre>
     * LogIterator iter = scheduler.getJobLog(jobId, null, 1000);
     * String log = iter.nextChunk();
     * // do something with the returned log String
     * while(iter.hasMoreChunks()) {
     *     iter = scheduler.getJobLog(jobId, iter, 1000);
     *     log = iter.nextChunk();
     *     // do something with the returned log
     * }
     * </pre>
     * 
     * @return the whole log file as string
     * @throws NoSuchJobException if the job with the id <code>jobId</code> does not exist
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.
     */
    public LogIterator getJobLog(JobID jobId, LogIterator it, int fetchSize) throws NoSuchJobException, SchedulerRuntimeException;

    /**
     * Returns the status of a job.
     * 
     * @param jobid the given job id
     * @return status for job with the provided job id
     * @throws NoSuchJobException if the job with the id <code>jobId</code> does not exist
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.
     */
    public JobStatus getJobStatus(JobID jobid) throws NoSuchJobException, SchedulerRuntimeException;

    /**
     * This method will return all jobs which match the provided filter
     * criteria. If the result set is bigger than the provided fetchSize
     * parameter the remaining entries can be retrieved using the returned
     * iterator. The following code snipped shows how to retrieve the 
     * result from the call.
     * <pre>
     * JobIterator iter = scheduler.getJobs(myFilter, null, 1000);
     * Job[] jobs = iter.nextChunk();
     * // do something with the returned job ojbects
     * while(iter.hasMoreChunks()) {
     *     iter = scheduler.getJobs(myFilter, iter, 1000);
     *     Job[] jobs = iter.nextChunk();
     *     // do something with the returned job objects
     *     //
     * }
     * </pre>
     * 
     * @param filter the pre-initialized filter object
     * @param iter an iterator which has been returned by a previous invocation
     * or <code>null</code> if this is the first call
     * @param fetchSize the number of records to fetch
     * @return the {@link JobIterator} object which contains the next chunk of
     *         data
     * @throws SchedulerRuntimeException <code>RuntimeException</code> thrown if 
     *                                   there is a problem with the underlying infrastructure.
     */
    public JobIterator getJobs(JobFilter filter, JobIterator iter, int fetchSize) throws SchedulerRuntimeException;
    
    /**
     * This method registers a EventConsumer for Jobs and /or tasks. Users of this 
     * method need to implement the methods of the {@link com.sap.scheduler.runtime.EventConsumer 
     * EventConsumer} interface and register for the events to listen for. The available 
     * events are listed in class {@link com.sap.scheduler.runtime.Event Event}.
     * Examples afor events are 
     * {@link com.sap.scheduler.runtime.Event#EVENT_JOB_STARTING EVENT_JOB_STARTING},
     * {@link com.sap.scheduler.runtime.Event#EVENT_JOB_FINISHED EVENT_JOB_FINISHED},
     * {@link com.sap.scheduler.runtime.Event#EVENT_TASK_CREATED EVENT_TASK_CREATED},
     * {@link com.sap.scheduler.runtime.Event#EVENT_TASK_FINISHED EVENT_TASK_FINISHED}, 
     * and so on.
     * <p> 
     * If you want to register for all available events you might use the String[] 
     * from {@link com.sap.scheduler.runtime.Event#RUNTIME_EVENT_TYPES RUNTIME_EVENT_TYPES}.
     * <p> 
     * If one and the same EventConsumer is registered a second time an 
     * <code>IllegalArgumentException</code> will be thrown.
     * <p>
     * Note: All events for which the user registers here for are delivered to every 
     * server node within the cluster. The user has to take care if events should 
     * be processed only at one server node.
     * <p>
     * @param eventsRegisteredFor the String[] with the events to register for
     * @param consumer the implementation of an EventConsumer
     * @see com.sap.scheduler.runtime.Event
     * @see com.sap.scheduler.runtime.EventConsumer
     * @see #removeEventListener(EventConsumer)
     */
    public void addEventListener(String[] eventsRegisteredFor, EventConsumer consumer);
    
    /**
     * This methods removes a previous registered EventConsumer. If this method 
     * is called several times or the EventConsumer has not been registered before 
     * the call would be ignored.
     * <p>
     * @param consumer the EventConsumer to remove
     */
    public void removeEventListener(EventConsumer consumer);
    
}
