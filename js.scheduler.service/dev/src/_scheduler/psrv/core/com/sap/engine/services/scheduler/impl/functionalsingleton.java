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

import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.api.TaskValidationException;
import com.sap.scheduler.api.TooManyFireEventsException;
import com.sap.scheduler.api.Scheduler.FireTimeEvent;
import com.sap.scheduler.runtime.SchedulerRuntimeException;

public interface FunctionalSingleton extends Remote {

    /**
     * Schedules the supplied task.
     * @param task - to be scheduled
     * @throws TaskValidationException - thrown if a task with this id is already scheduled
     * @throws NullPointerException - if <c>task</c> is null.
     */
    public void schedule(SchedulerTask task) throws TaskValidationException, RemoteException;

    /**
     * Obtains the task with id specified by <c>id</c> parameter. Only one task with this id
     * can exist.
     * @param id - id of the searched task.
     * @return the task with id that equals <c>id</c> parameter.
     * @throws NullPointerException - thrown if <c>id</c> is null.
     * @throws TaskDoesNotExistException - thrown if task with this id does not exist.
     */
    public SchedulerTask getTask(SchedulerTaskID id) throws TaskDoesNotExistException, RemoteException;

    /**
     * Cancels a scheduled task with id specified by the <c>taskId</c> parameter.
     * @param taskId - id of the task to be cancelled
     * @throws NullPointerException - if <c>taskId</c> is null.
     * @throws TaskDoesNotExistException - if a task with such id does not exist.
     */
    public void cancelTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, RemoteException;
    
    /**
     * Holds a scheduled task with id specified by the <c>taskId</c> parameter. 
     * Holding a task means that it will be suspenede from further execution till
     * realease(taskId) will be called which sets the task back to active. ( @see 
     * releaseTask(SchedulerTaskID taskId) ).
     * 
     * @param taskId id of the task to be hold
     * @throws NullPointerException if <c>taskId</c> is null.
     * @throws TaskDoesNotExistException if a task with such id does not exist.
     */
    public void holdTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, RemoteException;
    
    /**
     * Releases a hold task with id specified by the <c>taskId</c> parameter and 
     * set it back to state active. This method takes only effect if the task was 
     * set to hold before ( @see holdTask(SchedulerTaskID taskId) ).
     * @param taskId id of the task to be released
     * @throws NullPointerException if <c>taskId</c> is null.
     * @throws TaskDoesNotExistException if a task with such id does not exist.
     */
    public void releaseTask(SchedulerTaskID taskId) throws TaskDoesNotExistException, RemoteException;

    /**
     * Sets filters for the specified task. This method replaces all filters currently associated to this
     * task with the filters supplied by <c>f</c> parameter. This method could also be useful to remove
     * all filters associated with the given task by calling <c>setFilters(id, new Filter[0])</c>.
     * @param id - id of the task
     * @param f - array of filters
     * @throws NullPointerException - if <c>id</c> of <c>f</c> is null.
     * @throws TaskDoesNotExistException - if task with id specified by <c>id</c> parameter does not exist.
     */
    public void setFilters(SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException, RemoteException;

        /**
     * Adds a filter to the list of filters currently associated to given job
     * @param id - id of the task
     * @param f - array of filters to be associated with the given task
     * @throws NullPointerException - thrown if <c>id</c> or <c>f</c> is null
     * @throws TaskDoesNotExistException - if the given task does not exist
     */
    public void addFilters(SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException, RemoteException;

    /**
     * Removes the given filters from the list of filters associated with the given task.
     * This method does not throw an exception if any of the filters supplied by the array
     * <c>f</c> is not found ammong the list of filters associated with the given task. It simply
     * removes all the filters that are found both in <c>f</c> and the list of currently
     * associated filters
     * @param id - task whose filters will be removed
     * @param f - filters which will be removed from the list of filters associated to task <c>id</c>
     * @throws TaskDoesNotExistException - if the task specified by <c>id</c> does not exist.
     */
    public void removeFilters(SchedulerTaskID id, Filter[] f) throws TaskDoesNotExistException, RemoteException;

    /**
     * Obtains all currently scheduled scheduler tasks.
     * @return an array of all currently scheduled tasks. This method never returns null. If there
     * are no currently scheduled tasks it returns an empty array.
     */
    public SchedulerTaskID[] getAllSchedulerTaskIDs() throws RemoteException;

    public FireTimeEvent[] getFireTimes(SchedulerTaskID id, SchedulerTime startTime, SchedulerTime endTime)
    throws TaskDoesNotExistException, TooManyFireEventsException, RemoteException ;

}
