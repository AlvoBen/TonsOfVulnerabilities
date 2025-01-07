/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobParameter;

public class SchedulerTask implements Serializable {
     
    // Constants for column TASK_SOURCE in table BC_JOB_TASKS 
    public final static short TASK_SOURCE_SCHEDULER_API = 0;
    public final static short TASK_SOURCE_ZERO_ADMIN = 1;
    
    private static Map<Short, String> TASK_SOURCE_DESCRIPTIONS = new HashMap<Short, String>();
    static {
        TASK_SOURCE_DESCRIPTIONS.put(new Short(TASK_SOURCE_ZERO_ADMIN), "ZeroAdmin-Task");
        TASK_SOURCE_DESCRIPTIONS.put(new Short(TASK_SOURCE_SCHEDULER_API), "User-Task"); 
    }
    
    private static final Filter[] noFilters = new Filter[0];
    private static final RecurringEntry[] noRecurrings = new RecurringEntry[0];
    private static final CronEntry[] noCrons = new CronEntry[0];
    private static final JobParameter noParameters[] = new JobParameter[0];
    private final SchedulerTaskID taskId;
    private final JobDefinitionID jobDefinitionId;
    private final JobParameter[] jobParameters;
    private final RecurringEntry[] recurringEntries;
    private final CronEntry[] cronEntries;
    private Filter[] filters;
    private int retentionPeriod;
    private String m_name = null;
    private String m_description = null;
    private String m_customData = null;

    // Properties are set in subclass
    protected short m_taskSource = TASK_SOURCE_SCHEDULER_API;
    protected TaskStatus m_taskStatus = TaskStatus.active;
    protected String m_schedulingUser = null;
    protected String m_runAsUser = null;
   
    
    /**
     * Creates a new <c>SchedulerTask</c> instance with the specified
     * parameters. Weh an object is created with this construtor
     * <c>isRetentionDefault</c> would return false and <c>getRetentionPeriod</c>
     * would return the value of the <c>retentionPeriod</c> argument.
     * 
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
     * 
     * @throws NullPointerException -
     *             thrown if <c>taskId</c> or <c>jobDefinitionId</c> is null.
     * @throws IllegalArgumentException -
     *             thrown if <c>retentionPeriod</c> is less than -2.
     */
    public static SchedulerTask createSchedulerTask(JobDefinitionID jobDefinitionId, JobParameter[] jobParameters, RecurringEntry[] recs, CronEntry[] crons, Filter[] filters, int retentionPeriod, String name, String description, String customData) {
        return new SchedulerTask(SchedulerTaskID.newID(), jobDefinitionId, jobParameters, recs, crons, filters, retentionPeriod, name, description, customData);
    }
     

    /**
     * @deprecated - please use create-method instead 
     * 
     * Constructs a new <c>SchedulerTask</c> instance with the specified id,
     * job definition id, parameters and schedule. When an object is created
     * with this constructor a call to <c>getRetentionPeriod</c> would throw
     * <c>IllegalStateException</c> and <c>isRetentionDefault</c> would return
     * true.
     * 
     * @param taskId -
     *            id of this scheduler task. Can not be null.
     * @param jobDefinitionId -
     *            Id of the job definition whose instances will be triggered by
     *            this <c>SchedulerTask</c>. Can not be null
     * @param jobParameters -
     *            Parameters of the job. These should match the job parameter
     *            definition in the job definition. The passed parameters are
     *            not validated by this constructor. I.e. arbitrary
     *            <c>JobParameter</c> instances can be passed. identity <c>id</c>
     *            with the parameters specified by <c>jobParameters</c> will be
     *            created.
     * @param recs -
     *            recurring entries
     * @param crons -
     *            cron entries
     * @throws NullPointerException -
     *             if <c>taskId</c>, <c>JobDefinitionId</c>, <c>jobParameters</c>
     *             or <c>timeTable</c> is null.
     */
    public SchedulerTask(SchedulerTaskID taskId, JobDefinitionID jobDefinitionId, JobParameter[] jobParameters, RecurringEntry[] recs, CronEntry[] crons) {
        if ((this.taskId = taskId) == null)
            throw new NullPointerException("taskId");
        if ((this.jobDefinitionId = jobDefinitionId) == null)
            throw new NullPointerException("jobDefinitionId");
        if (jobParameters != null)
            this.jobParameters = (JobParameter[]) jobParameters.clone();
        else
            this.jobParameters = jobParameters;
        if ((recs == null || recs.length == 0) && (crons == null || crons.length == 0)) {
            throw new IllegalArgumentException("No recurring and no cron etries were specified");
        }
        recurringEntries = recs;
        cronEntries = crons;
        retentionPeriod = -2;
    }

    /**
     * @deprecated - please use create-method instead 
     * 
     * Constrcuts a new <c>SchedulerTas</c> instance with the specified
     * parameters. Weh an object is created with this construtor
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
     * @throws NullPointerException -
     *             thrown if <c>taskId</c> or <c>jobDefinitionId</c> is null.
     * @throws IllegalArgumentException -
     *             thrown if <c>retentionPeriod</c> is less than -2.
     */
    public SchedulerTask(SchedulerTaskID taskId, JobDefinitionID jobDefinitionId, JobParameter[] jobParameters, RecurringEntry[] recs, CronEntry[] crons, Filter[] filters, int retentionPeriod) {
        this(taskId, jobDefinitionId, jobParameters, recs, crons);
        if (filters != null)
            this.filters = (Filter[]) filters.clone();
        if ((this.retentionPeriod = retentionPeriod) < -2)
            throw new IllegalArgumentException("Parameter retentionPeriod" + " expresses the time to keep logs of the job in days. It cannot be less then -2, where -1 means infinity," + " -2 means the default from job definition and a value greater than 0 means a number of days");
    }

    /**
     * @deprecated - please use create-method instead 
     * 
     * Constrcuts a new <c>SchedulerTas</c> instance with the specified
     * parameters. Weh an object is created with this construtor
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
     * @param name -
     *            Name for this scheduler task. If not set, the name will be
     *            taken from the job definition
     * @param description -
     *            Description for this task, e.g. "General backup job running
     *            every Friday, do not skip". It can be null.
     * 
     * @throws NullPointerException -
     *             thrown if <c>taskId</c> or <c>jobDefinitionId</c> is null.
     * @throws IllegalArgumentException -
     *             thrown if <c>retentionPeriod</c> is less than -2.
     */
    public SchedulerTask(SchedulerTaskID taskId, JobDefinitionID jobDefinitionId, JobParameter[] jobParameters, RecurringEntry[] recs, CronEntry[] crons, String name, String description) {
        this(taskId, jobDefinitionId, jobParameters, recs, crons);
        this.m_name = name;
        this.m_description = description;
    }

    /**
     * @deprecated - please use create-method instead 
     * 
     * Constrcuts a new <c>SchedulerTas</c> instance with the specified
     * parameters. Weh an object is created with this construtor
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
     * 
     * @throws NullPointerException -
     *             thrown if <c>taskId</c> or <c>jobDefinitionId</c> is null.
     * @throws IllegalArgumentException -
     *             thrown if <c>retentionPeriod</c> is less than -2.
     */
    public SchedulerTask(SchedulerTaskID taskId, JobDefinitionID jobDefinitionId, JobParameter[] jobParameters, RecurringEntry[] recs, CronEntry[] crons, String name, String description, String customData) {
        this(taskId, jobDefinitionId, jobParameters, recs, crons, name, description);
        this.m_customData = customData;
    }

    /**
     * @deprecated - please use create-method instead 
     * 
     * Constrcuts a new <c>SchedulerTas</c> instance with the specified
     * parameters. Weh an object is created with this construtor
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
     * 
     * @throws NullPointerException -
     *             thrown if <c>taskId</c> or <c>jobDefinitionId</c> is null.
     * @throws IllegalArgumentException -
     *             thrown if <c>retentionPeriod</c> is less than -2.
     */
    public SchedulerTask(SchedulerTaskID taskId, JobDefinitionID jobDefinitionId, JobParameter[] jobParameters, RecurringEntry[] recs, CronEntry[] crons, Filter[] filters, int retentionPeriod, String name, String description, String customData) {
        this(taskId, jobDefinitionId, jobParameters, recs, crons, filters, retentionPeriod);
        this.m_name = name;
        this.m_description = description;
        this.m_customData = customData;
    }

    /**
     * Obtains the retention period in days.
     * 
     * @return the retention period in days. -2 means the default specified by
     *         the job definition, -1 means infinity, 0 means that records are
     *         not kept. N, where N > 0 means that the records are kept for at
     *         least n days
     */
    public int getRetentionPeriod() {
        return retentionPeriod;
    }

    /**
     * Obtains the id of this scheduler task
     * 
     * @return the id of this scheduler task
     */
    public SchedulerTaskID getTaskId() {
        return taskId;
    }

    /**
     * Obtains the id of the job definition whose instances are triggered by
     * this scheduler task
     * 
     * @return the id of the job definition whose instances are triggered by
     *         this scheduler task
     */
    public JobDefinitionID getJobDefinitionId() {
        return jobDefinitionId;
    }

    /**
     * Obtains the parameters of this scheduler task. These parameters are
     * passed to each job instance triggered by this scheduler task.
     * 
     * @return the parameters of this scheduler task.
     */
    public JobParameter[] getJobParameters() {
        if (jobParameters != null)
            return (JobParameter[]) jobParameters.clone();
        else
            return noParameters;
    }

    /**
     * Obtains the filters associated with this scheduler task. The return value
     * is never null.
     * 
     * @return the filters associated with this scheduler task.
     */
    public Filter[] getFilters() {
        if (filters == null)
            return noFilters;
        return (Filter[]) filters.clone();
    }

    public RecurringEntry[] getRecurringEntries() {
        if (recurringEntries != null)
            return recurringEntries;
        else
            return noRecurrings;
    }

    public CronEntry[] getCronEntries() {
        if (cronEntries != null)
            return cronEntries;
        else
            return noCrons;
    }

    /**
     * @return Returns the custom data set with this SchedulerTask.
     */
    public String getCustomData() {
        return m_customData;
    }

    /**
     * @return Returns the description of this SchedulerTask.
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * @return Returns the name of this SchedulerTask.
     */
    public String getName() {
        return m_name;
    }

    /**
     * @return Returns the taskSource.
     */
    public short getTaskSource() {
        return m_taskSource;
    }
    
    /**
     * @return Returns the taskSource.
     */
    public String getTaskSourceDescription() {
        return TASK_SOURCE_DESCRIPTIONS.get(new Short(m_taskSource));
    }

    /**
     * @return Returns the TaskStatus.
     */
    public TaskStatus getTaskStatus() {
        return m_taskStatus;
    }
    
    
    /**
     * @return Returns the scheduling user.
     */
    public String getSchedulingUser() {
        return m_schedulingUser;
    }
    
    
    /**
     * @return Returns the scheduling user.
     */
    public String getRunAsUser() {
        return m_runAsUser;
    }

    
    /**
     * Compares a SchedulerTask with this.
     * 
     * @param task the SchedulerTask to compare
     * @return true if the SchedulerTask are equal in case of its members, 
     *              false otherwise.
     */
    public boolean compareSchedulerTask(SchedulerTask task) {
        // name
        if (!m_name.equals(task.m_name)) {
            return false;
        }
        // JobDefinition
        if (!jobDefinitionId.equals(task.jobDefinitionId)) {
            return false;
        }
        // description
        if (m_description != null) {
            if (!m_description.equals(task.m_description)) {
                return false;
            }
        }
        // customData
        if (m_customData != null) {
            if (!m_customData.equals(task.m_customData)) {
                return false;
            }
        }
        // retentionPeriod
        if (retentionPeriod != task.retentionPeriod) {
            return false;
        }
        // RecurringEntry
        if ( (recurringEntries != null && task.recurringEntries == null) 
                || (recurringEntries == null && task.recurringEntries != null) ) {
            return false;
        }
        if (recurringEntries != null) {
            if (recurringEntries.length != task.recurringEntries.length) {
                return false;
            } else {
                for (int i = 0; i < recurringEntries.length; i++) {
                    boolean equal = false;
                    for (int j = 0; j < task.recurringEntries.length; j++) {
                        if ( recurringEntries[i].compareRecurringEntry(task.recurringEntries[j]) ) {
                            equal = true;
                        }
                    } 
                    if (!equal) {
                        return false;
                    }
                }
            }
        }
        // CronEntry
        if ((cronEntries != null && task.cronEntries == null) 
                || (cronEntries == null && task.cronEntries != null)) {
            return false;
        }
        if (cronEntries != null) {
            if (cronEntries.length != task.cronEntries.length) {
                return false;
            } else {
                for (int i = 0; i < cronEntries.length; i++) {
                    boolean equal = false;
                    for (int j = 0; j < task.cronEntries.length; j++) {
                        if (cronEntries[i].compareCronEntry(task.cronEntries[j])) {
                            equal = true;
                        }
                    }
                    if (!equal) {
                        return false;
                    }
                }
            }
        }
        // JobParameters
        if ((jobParameters != null && task.jobParameters == null) 
                || (jobParameters == null && task.jobParameters != null)) {
            return false;
        }
        if (jobParameters != null) {
            if (jobParameters.length != task.jobParameters.length) {
                return false;
            } else {
                for (int i = 0; i < jobParameters.length; i++) {
                    boolean equal = false;
                    for (int j = 0; j < task.jobParameters.length; j++) {
                        if (jobParameters[i].compareJobParameter(task.jobParameters[j])) {
                            equal = true;
                        }
                    }
                    if (!equal) {
                        return false;
                    }
                }
            }
        }
        // Filters
        if ((filters != null && task.filters == null) 
                || (filters == null && task.filters != null)) {
            return false;
        }
        if (filters != null) {
            if (filters.length != task.filters.length) {
                return false;
            } else {
                for (int i = 0; i < filters.length; i++) {
                    boolean equal = false;
                    for (int j = 0; j < task.filters.length; j++) {
                        if (filters[i].compareFilter(task.filters[j])) {
                            equal = true;
                        }
                    }
                    if (!equal) {
                        return false;
                    }
                }
            }
        }

        return true;   
    } 
    
    
    /**
     * Returns this SchedulerTask in a formatted way
     * 
     * @return SchedulerTask in formatted way
     */
    public String toFormattedString() {
        String LINE_WRAP = System.getProperty("line.separator");
        StringBuilder buf = new StringBuilder();   
        
        buf.append("SchedulerTaskID: ").append(taskId.toString());  
        buf.append(LINE_WRAP);
        buf.append("JobDefinitionID: ").append(jobDefinitionId.toString());
        buf.append(LINE_WRAP);
        buf.append("RetentionPeriod: ").append(retentionPeriod);
        buf.append(LINE_WRAP);
        buf.append("Name:            ").append(m_name);
        buf.append(LINE_WRAP);
        buf.append("Description:     ").append(m_description);
        buf.append(LINE_WRAP);
        buf.append("CustomData:      ").append(m_customData);
        buf.append(LINE_WRAP);        
        buf.append("TaskSource:      ").append(m_taskSource);
        buf.append(LINE_WRAP);   
        buf.append("TaskStatus:      ").append(m_taskStatus.toString());
        buf.append(LINE_WRAP); 
        buf.append("TaskStatus-Desc: ").append(m_taskStatus.getDescription());
        buf.append(LINE_WRAP); 
        buf.append("SchedulingUser : ").append(m_schedulingUser);
        buf.append(LINE_WRAP); 
        buf.append("RunAsUser      : ").append(m_runAsUser);
        buf.append(LINE_WRAP); 
        
        // Crons
        if (cronEntries != null && cronEntries.length != 0) {
            for (int i = 0; i < cronEntries.length; i++) {
                buf.append("CronEntry").append(i+1).append(":      ").append(cronEntries[i].persistableValue());
                buf.append(LINE_WRAP);
            }
        } else {
            buf.append("CronEntry:      null");
            buf.append(LINE_WRAP);
        }
        
        // Recurrings
        if (recurringEntries != null && recurringEntries.length != 0) {
            for (int i = 0; i < recurringEntries.length; i++) {
                buf.append("RecurringEntry").append(i+1).append(":");
                buf.append(LINE_WRAP);
                buf.append(recurringEntries[i].toString());
                buf.append(LINE_WRAP);
            }
        } else {
            buf.append("RecurringEntry:  null");
            buf.append(LINE_WRAP);
        }
        
        // Parameters
        if (jobParameters != null && jobParameters.length != 0) {
            for (int i = 0; i < jobParameters.length; i++) {
                buf.append("JobParameter").append(i+1).append(":   ").append(jobParameters[i].getName()).append("=").append(jobParameters[i].toString());
                buf.append(LINE_WRAP);
            }
        } else {
            buf.append("JobParameter:    null");
            buf.append(LINE_WRAP);
        }
        
        // Filters
        if (filters != null && filters.length != 0) {
            for (int i = 0; i < filters.length; i++) {
                buf.append("Filter").append(i+1).append(":         ").append(filters[i].toString());
                buf.append(LINE_WRAP);
            }
        } else {
            buf.append("Filter:          null");
            buf.append(LINE_WRAP);
        }


        return buf.toString();
    }

}
