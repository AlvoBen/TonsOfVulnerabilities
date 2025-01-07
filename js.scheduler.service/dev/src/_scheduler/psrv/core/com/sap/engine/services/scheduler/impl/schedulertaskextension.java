package com.sap.engine.services.scheduler.impl;

import com.sap.scheduler.api.CronEntry;
import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.RecurringEntry;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.TaskStatus;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobParameter;

public class SchedulerTaskExtension extends SchedulerTask { 

    /**
     * Constrcuts a new <c>SchedulerTask</c> instance with the specified
     * parameters. When an object is created with this construtor
     * <c>isRetentionDefault</c> would return false and <c>getRetentionPeriod</c>
     * would return the value of the <c>retentionPeriod</c> argument.
     * 
     * @param taskId -
     *            Id of this scheduler task. Can not be null.
     * @param jobDefinitionId -
     *            id of the job definition whose instances will be triggered by
     *            this <c>SchedulerTask</c>. Can not be null
     * @param jobParameters -
     *            Parameters of the job. These should match the job parameter
     *            definition in the job definition. The passed parameters are
     *            not validated by this constructor. I.e. arbitrary
     *            <c>JobParameter</c> instances can be passed. A copy of the
     *            passed arguments are stored in the <c>SchedulerTask</c>
     *            instance. Thus changes to the passed array of parameters would
     *            not reflect this instance. identity <c>id</c> with the
     *            parameters specified by <c>jobParameters</c> will be created.
     * @param recs -
     *            recurring entries
     * @param crons -
     *            cron entries
     * @param filters -
     *            A list of filters to filter out specific job expirations. Can
     *            be null. If the passed parameter is not null then a copy of
     *            the array is stored in the <c>SchedulerTask</c> instance.
     *            Thus changes to the passed array of filters would not reflect
     *            this instance.
     * @param retentionPeriod -
     *            Number of days to keep the job log records after each job
     *            execution. -2 means the default from the job definition -1
     *            means infinity i.e. job log records are never deleted.
     * @param name -
     *            Name for this scheduler task. If not set, the name will be
     *            taken from the job definition
     * @param description -
     *            Description for this task, e.g. "General backup job running
     *            every Friday, do not skip". It can be null.
     * @param customData -
     *            String, can be used by an application to store application
     *            dependent context data. Ignored by the job scheduler. It can
     *            be null.
     * @param taskSource -
     *            wher the task come from (ZeroAdmin-template, API-call)
     *            
     * @param TaskStatus -
     *            the status of the SchedulerTask
     * 
     * @throws NullPointerException -
     *             thrown if <c>taskId</c> or <c>jobDefinitionId</c> is null.
     * @throws IllegalArgumentException -
     *             thrown if <c>retentionPeriod</c> is less than -2.
     */
    public SchedulerTaskExtension(SchedulerTaskID taskId, JobDefinitionID jobDefinitionId, JobParameter[] jobParameters, RecurringEntry[] recs, CronEntry[] crons, Filter[] filters, int retentionPeriod, String name, String description, String customData, short taskSource, TaskStatus taskStatus, String schedulingUser, String runAsUser) {
        super(taskId, jobDefinitionId, jobParameters, recs, crons, filters, retentionPeriod, name, description, customData);
        m_taskSource = taskSource;
        m_taskStatus = taskStatus;   
        m_schedulingUser = schedulingUser;
        m_runAsUser = runAsUser;
    }
    
    
    /**
     * Sets the task-source.
     * 
     * @param taskSource the task-source (see constants in class SchedulerTask)
     */
    public void setTaskSource(short taskSource) {
        m_taskSource = taskSource;
    }
    
    
    /**
     * Sets the TaskStatus.
     * 
     * @param taskStatus the TaskStatus (see constants in class TaskStatus)
     */
    public void setTaskStatus(TaskStatus taskStatus) {
        m_taskStatus = taskStatus;
    }
}
