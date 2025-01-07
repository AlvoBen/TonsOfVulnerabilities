/*
 * Created on 24.07.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.scheduler.runtime.mbean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.management.MBeanException;

import com.sap.scheduler.runtime.util.LocalizationHelper;
import com.sap.scheduler.api.CronEntry;
import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.RecurringEntry;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.api.Scheduler.FireTimeEvent;
import com.sap.scheduler.api.fields.CronDOMField;
import com.sap.scheduler.api.fields.CronDOWField;
import com.sap.scheduler.api.fields.CronHourField;
import com.sap.scheduler.api.fields.CronMinuteField;
import com.sap.scheduler.api.fields.CronMonthField;
import com.sap.scheduler.api.fields.CronYearField;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobDefinitionName;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIterator;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.scheduler.runtime.JobParameterType;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.SchedulerID;
import com.sap.scheduler.runtime.SchedulerLogRecord;
import com.sap.scheduler.runtime.SchedulerLogRecordIterator;
import com.sap.scheduler.runtime.SubscriberID;
import com.sap.scheduler.runtime.SchedulerDefinition.SchedulerStatus;

public class SAP_ITSAM_Helper {
    
    
    // -------------------------------------------------------------------------
    // ------------ Methods to convert TO SAP_ITSAM format ---------------------
    // -------------------------------------------------------------------------
    //
    // currently implemented:
    // ----------------------
    // JobDefinition[]          --> SAP_ITSAMJavaSchedulerJobDefinition[]
    // JobDefinition            --> SAP_ITSAMJavaSchedulerJobDefinition
    // Job[]                    --> SAP_ITSAMJavaSchedulerJob[]
    // Job                      --> SAP_ITSAMJavaSchedulerJob
    // JobParameterDefinition[] --> SAP_ITSAMJavaSchedulerJobParameterDefinition[]
    // JobParameterDefinition   --> SAP_ITSAMJavaSchedulerJobParameterDefinition
    // JobParameter[]           --> SAP_ITSAMJavaSchedulerJobParameter[]
    // JobParameter             --> SAP_ITSAMJavaSchedulerJobParameter
    // SchedulerDefinition      --> SAP_ITSAMJavaSchedulerDefinition
    // JobID[]                  --> String[]
    // JobDefinitionID[]        --> String[]
    // FireTimeEvent[]          --> SAP_ITSAMJavaSchedulerFireTimeEvent[]
    // FireTimeEvent            --> SAP_ITSAMJavaSchedulerFireTimeEvent
    // SchedulerTime            --> SAP_ITSAMJavaSchedulerTime
    // SchedulerTask            --> SAP_ITSAMJavaSchedulerTask
    // Filter[]                 --> SAP_ITSAMJavaSchedulerFilter[]
    // Filter                   --> SAP_ITSAMJavaSchedulerFilter
    // RecurringEntry[]         --> SAP_ITSAMJavaSchedulerRecurringEntry[]
    // RecurringEntry           --> SAP_ITSAMJavaSchedulerRecurringEntry
    // CronEntry[]              --> SAP_ITSAMJavaSchedulerCronEntry[]
    // CronEntry                --> SAP_ITSAMJavaSchedulerCronEntry
    // LogIterator              --> SAP_ITSAMJavaSchedulerLogIterator
    // JobIterator              --> SAP_ITSAMJavaSchedulerJobIterator
    // JobDefinitionName        --> SAP_ITSAMJavaSchedulerJobDefinitionName
    // String[][]               --> SAP_ITSAMProperty[] (Properties of JobDefinition)
    // SchedulerLogRecord       --> SAP_ITSAMJavaSchedulerLogRecord
    // SchedulerLogRecord[]     --> SAP_ITSAMJavaSchedulerLogRecord[]
    // Localization-info-Map    --> SAP_ITSAMJavaSchedulerProperty[]
    
    // *************************************************************************
    
    // -------------------------------------------------------------------------
    // ------------ Methods to convert FROM SAP_ITSAM format -------------------
    // -------------------------------------------------------------------------
    //
    // currently implemented:
    // ----------------------
    // SAP_ITSAMJavaSchedulerJob[]                  --> Job[]
    // SAP_ITSAMJavaSchedulerJob                    --> Job
    // SAP_ITSAMJavaSchedulerJobParameter[]         --> JobParameter[]
    // SAP_ITSAMJavaSchedulerJobParameterDefinition --> JobParameterDefinition
    // SAP_ITSAMJavaSchedulerDefinition             --> SchedulerDefinition 
    // SAP_ITSAMJavaSchedulerFilter                 --> Filter
    // SAP_ITSAMJavaSchedulerFilter[]               --> Filter[]
    // SAP_ITSAMJavaSchedulerTime                   --> SchedulerTime
    // String[]                                     --> JobID[]
    // String[]                                     --> JobDefinitionID[]
    // SAP_ITSAMJavaSchedulerFireTimeEvent          --> FireTimeEvent
    // SAP_ITSAMJavaSchedulerFireTimeEvent[]        --> FireTimeEvent[]
    // SAP_ITSAMJavaSchedulerTask                   --> SchedulerTask 
    // SAP_ITSAMJavaSchedulerRecurringEntry[]       --> RecurringEntry[] 
    // SAP_ITSAMJavaSchedulerRecurringEntry         --> RecurringEntry 
    // SAP_ITSAMJavaSchedulerCronEntry[]            --> CronEntry[]
    // SAP_ITSAMJavaSchedulerCronEntry              --> CronEntry    
    // SAP_ITSAMJavaSchedulerLogIterator            --> LogIterator
    // SAP_ITSAMJavaSchedulerJobIterator            --> JobIterator
    // SAP_ITSAMJavaSchedulerProperty[]             --> Localization-info-Map 
    
    
    /**
     * The attribute insatance_id which is used by several composite types is 
     * extended from the root CIM class (CIM_SettingData) and we could not exclude 
     * it because it is marked as a key. We could set an empty string for it's 
     * value (the UI will not use it). It's idea is to show to which managed element 
     * this setting belongs.
     */
    private static final String DUMMY_INSTANCE_ID = "";
    private static final String DUMMY_DESCRIPTION = "";
    
    public static final String NO_MORE_CHUNKS_JOB_ITERATOR = JobIterator.StateDescriptor.NO_MORE_CHUNKS;
    public static final long NO_MORE_CHUNKS_LOG_ITERATOR = SchedulerLogRecordIterator.NO_MORE_CHUNKS;
    public static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");    
    
    public static SAP_ITSAMJavaSchedulerJobDefinition[] convertJobDefinitionArrayToSAP_ITSAM(JobDefinition[] jobDefs) {
        SAP_ITSAMJavaSchedulerJobDefinition[] result = new SAP_ITSAMJavaSchedulerJobDefinition[jobDefs.length];
        
        for (int i = 0; i < jobDefs.length; i++) {
            result[i] = convertJobDefinitionToSAP_ITSAM(jobDefs[i]);
        }
        return result;
    }
    
    
    // JobDefinition --> SAP_ITSAMJavaSchedulerJobDefinition
    public static SAP_ITSAMJavaSchedulerJobDefinition convertJobDefinitionToSAP_ITSAM(JobDefinition jobDef) {
        JobParameterDefinition jobParamDef[] = jobDef.getParameters();
        // we need first to convert the members which are modeled with its own CompositeType 
        SAP_ITSAMJavaSchedulerJobParameterDefinition[] paramDefResult = convertJobParameterDefinitionArrayToSAP_ITSAM(jobParamDef);      
        SAP_ITSAMJavaSchedulerJobDefinitionName jobDefName = convertJobDefinitionNameToSAP_ITSAM(jobDef.getJobDefinitionName());
        SAP_ITSAMProperty[] props = convertStringArrayPropertiesToSAP_ITSAM(jobDef.getProperties());
        SAP_ITSAMJavaSchedulerProperty[] localeProps = convertLocalizationMapToSAP_ITSAM(jobDef.getLocalizationInfoMap());
        
        SAP_ITSAMJavaSchedulerJobDefinition result = new SAP_ITSAMJavaSchedulerJobDefinition(DUMMY_INSTANCE_ID, 
                                                                                             jobDef.getJobDefinitionName().getName(),
                                                                                             jobDef.getDescription(), 
                                                                                             jobDef.getApplication(),
                                                                                             jobDef.getJobDefinitionId().toString(),
                                                                                             jobDef.getJobType(), 
                                                                                             jobDefName, 
                                                                                             paramDefResult, 
                                                                                             props, 
                                                                                             jobDef.getRemoveDate(), 
                                                                                             jobDef.getRetentionPeriod(),
                                                                                             getRetentionPeriodFormatted(jobDef.getRetentionPeriod()),
                                                                                             localeProps);
        return result;
    }
    
    
    // Job --> SAP_ITSAMJavaSchedulerJob
    public static SAP_ITSAMJavaSchedulerJob convertJobToSAP_ITSAM(Job job, String schedulerName, String appName) {  

        SAP_ITSAMJavaSchedulerJob result = new SAP_ITSAMJavaSchedulerJob(DUMMY_INSTANCE_ID, 
                                                                         job.getId().toString(),
                                                                         job.getSchedulerTaskId() == null ? "" : job.getSchedulerTaskId().toString(),
                                                                         job.getName(), 
                                                                         job.getStartDate(), 
                                                                         job.getCancelRequest(),
                                                                         job.getEndDate(), 
                                                                         job.getJobDefinitionId().toString(),
                                                                         job.getNode(), 
                                                                         job.getParent() == null ? "" : job.getParent().toString(), 
                                                                         job.getRetentionPeriod(),
                                                                         getRetentionPeriodFormatted(job.getRetentionPeriod()),
                                                                         job.getReturnCode(), 
                                                                         job.getScheduler() == null ? "" : job.getScheduler().toString(),
                                                                         schedulerName, 
                                                                         appName,
                                                                         job.getSubmitDate(), 
                                                                         job.getUser(), 
                                                                         job.getVendorData(),
                                                                         job.getJobStatus().toString(),
                                                                         DUMMY_DESCRIPTION, // empty String for description
                                                                         job.getName());        
        return result;
    }
    
    
    // Job[] --> SAP_ITSAMJavaSchedulerJob[]
    public static SAP_ITSAMJavaSchedulerJob[] convertJobArrayToSAP_ITSAM(Job[] jobs, Map<JobID, String[]> names) {
        SAP_ITSAMJavaSchedulerJob[] jobResult = new SAP_ITSAMJavaSchedulerJob[jobs.length];
        
        for (int j = 0; j < jobs.length; j++) {
            String[] strArray = names.get(jobs[j].getId());
            jobResult[j] = convertJobToSAP_ITSAM(jobs[j], strArray[0], strArray[1]);
        }        
        return jobResult;
    }
    
    
    // JobParameterDefinition[] --> SAP_ITSAMJavaSchedulerJobParameterDefinition[]
    public static SAP_ITSAMJavaSchedulerJobParameterDefinition[] convertJobParameterDefinitionArrayToSAP_ITSAM(JobParameterDefinition[] jobParamDef) {
        SAP_ITSAMJavaSchedulerJobParameterDefinition[] paramDefResult = new SAP_ITSAMJavaSchedulerJobParameterDefinition[jobParamDef.length];
        
        for (int j = 0; j < jobParamDef.length; j++) {
            paramDefResult[j] = convertJobParameterDefinitionToSAP_ITSAM(jobParamDef[j]);
        }        
        return paramDefResult;
    }
    
    
    // JobParameterDefinition --> SAP_ITSAMJavaSchedulerJobParameterDefinition
    public static SAP_ITSAMJavaSchedulerJobParameterDefinition convertJobParameterDefinitionToSAP_ITSAM(JobParameterDefinition jobParamDef) {
        SAP_ITSAMJavaSchedulerJobParameterDefinition paramDefResult = null;
        SAP_ITSAMJavaSchedulerProperty[] localeProps = convertLocalizationMapToSAP_ITSAM(jobParamDef.getLocalizationInfoMap());

        paramDefResult = new SAP_ITSAMJavaSchedulerJobParameterDefinition(DUMMY_INSTANCE_ID, 
                                                                          jobParamDef.getName(),
                                                                          jobParamDef.getDescription(), 
                                                                          jobParamDef.getDefaultData(), 
                                                                          jobParamDef.isDisplay(), 
                                                                          jobParamDef.getDirection(), 
                                                                          jobParamDef.getGroup(), 
                                                                          jobParamDef.getType().toString(), 
                                                                          jobParamDef.isNullable(),
                                                                          localeProps);
        return paramDefResult;
    }
    
    
    // JobParameter[] --> SAP_ITSAMJavaSchedulerJobParameter[]
    public static SAP_ITSAMJavaSchedulerJobParameter[] convertJobParameterArrayToSAP_ITSAM(JobParameter[] jobParam) {
        SAP_ITSAMJavaSchedulerJobParameter[] jobParams = new SAP_ITSAMJavaSchedulerJobParameter[jobParam.length];
        
        for (int j = 0; j < jobParam.length; j++) {
            jobParams[j] = convertJobParameterToSAP_ITSAM(jobParam[j]);
        }
        return jobParams;
    }
    
    
    // JobParameter --> SAP_ITSAMJavaSchedulerJobParameter
    public static SAP_ITSAMJavaSchedulerJobParameter convertJobParameterToSAP_ITSAM(JobParameter jobParam) {
        SAP_ITSAMJavaSchedulerJobParameterDefinition jobParamDefSAP_ITSAM = convertJobParameterDefinitionToSAP_ITSAM(jobParam.getJobParameterDefinition());
        // jobParam.getValue() might be null in case the parameter is not required
        String jobParamValue = null;
        if (jobParam.getValue() != null) {
            jobParamValue = jobParam.getValue().toString();
        }
        SAP_ITSAMJavaSchedulerJobParameter jobParamResult = new SAP_ITSAMJavaSchedulerJobParameter(DUMMY_INSTANCE_ID, 
                                                                                                   jobParamDefSAP_ITSAM,
                                                                                                   jobParamValue);
        return jobParamResult;
    }    
    
    
    // SchedulerDefinition --> SAP_ITSAMJavaSchedulerDefinition 
    public static SAP_ITSAMJavaSchedulerDefinition convertSchedulerDefinitionToSAP_ITSAM(SchedulerDefinition schedulerDef) {
        SAP_ITSAMJavaSchedulerDefinition schedulerDefResult = new SAP_ITSAMJavaSchedulerDefinition(DUMMY_INSTANCE_ID,
                                                                                                   schedulerDef.getId().toString(),
                                                                                                   schedulerDef.getSubscriberId().toString(),
                                                                                                   schedulerDef.getName(),
                                                                                                   schedulerDef.getDescription(),
                                                                                                   schedulerDef.getUser(),
                                                                                                   schedulerDef.getSchedulerStatus().getValue(),
                                                                                                   schedulerDef.getLastAccess(),
                                                                                                   getLastAccessTimeFormatted(schedulerDef.getLastAccess()), 
                                                                                                   schedulerDef.getInactivityGracePeriod(),
                                                                                                   getInactivityGracePeriodFormatted(schedulerDef.getInactivityGracePeriod()));
        return schedulerDefResult;
    }
    
    
    public static String[] convertJobIDArrayToStringArray(JobID[] jobIds) {
        String[] result = new String[jobIds.length];
        for (int i = 0; i < jobIds.length; i++) {
            result[i] = jobIds[i].toString();
        }
        return result;
    }
    
    
    public static String[] convertJobDefinitionIDArrayToStringArray(JobDefinitionID[] jobDefIds) {
        String[] result = new String[jobDefIds.length];
        for (int i = 0; i < jobDefIds.length; i++) {
            result[i] = jobDefIds[i].toString();
        }
        return result;        
    }
    
        
    // SchedulerTime --> SAP_ITSAMJavaSchedulerTime
    public static SAP_ITSAMJavaSchedulerTime convertSchedulerTimeToSAP_ITSAM(SchedulerTime schedulerTime) {
        SAP_ITSAMJavaSchedulerTime schedTime = new SAP_ITSAMJavaSchedulerTime(schedulerTime.getTimeZone().getID(), schedulerTime.getTime());
        return schedTime;
    }
    
    
    // FireTimeEvent[] --> SAP_ITSAMJavaSchedulerFireTimeEvent[]
    public static SAP_ITSAMJavaSchedulerFireTimeEvent[] convertFireTimeEventArrayToSAP_ITSAM(FireTimeEvent[] events) {
        SAP_ITSAMJavaSchedulerFireTimeEvent[] result = new SAP_ITSAMJavaSchedulerFireTimeEvent[events.length];
        
        for (int j = 0; j < events.length; j++) {
            result[j] = convertFireTimeEventToSAP_ITSAM(events[j]);
        }        
        return result;
    }
    
    
    // FireTimeEvent --> SAP_ITSAMJavaSchedulerFireTimeEvent
    public static SAP_ITSAMJavaSchedulerFireTimeEvent convertFireTimeEventToSAP_ITSAM(FireTimeEvent event) {
        SAP_ITSAMJavaSchedulerTime schedTime = convertSchedulerTimeToSAP_ITSAM(event.time); 
        String schedTaskId = event.taskId.toString();
        boolean filtered = event.filtered;
        
        SAP_ITSAMJavaSchedulerFireTimeEvent result = new SAP_ITSAMJavaSchedulerFireTimeEvent(schedTime, filtered, schedTaskId);

        return result;
    }
    
    
    // Filter --> SAP_ITSAMJavaSchedulerFilter
    public static SAP_ITSAMJavaSchedulerFilter convertFilterToSAP_ITSAM(Filter filter) {
        SAP_ITSAMJavaSchedulerTime startTime = convertSchedulerTimeToSAP_ITSAM(filter.getStartTime());
        SAP_ITSAMJavaSchedulerTime endTime = convertSchedulerTimeToSAP_ITSAM(filter.getEndTime()); 
        
        SAP_ITSAMJavaSchedulerFilter result = new SAP_ITSAMJavaSchedulerFilter(startTime, endTime);

        return result;
    }
    
    
    // RecurringEntry[] --> SAP_ITSAMJavaSchedulerRecurringEntry[]
    public static SAP_ITSAMJavaSchedulerRecurringEntry[] convertRecurringEntryArrayToSAP_ITSAM(RecurringEntry[] entries) {
        SAP_ITSAMJavaSchedulerRecurringEntry[] result = new SAP_ITSAMJavaSchedulerRecurringEntry[entries.length];
        
        for (int j = 0; j < entries.length; j++) {
            result[j] = convertRecurringEntryToSAP_ITSAM(entries[j]);
        }        
        return result;
    }
    
    
    // RecurringEntry --> SAP_ITSAMJavaSchedulerRecurringEntry
    public static SAP_ITSAMJavaSchedulerRecurringEntry convertRecurringEntryToSAP_ITSAM(RecurringEntry entry) {
        SAP_ITSAMJavaSchedulerTime startTime = convertSchedulerTimeToSAP_ITSAM(entry.getStartTime());
        SAP_ITSAMJavaSchedulerTime endTime = null;
        if (entry.getEndTime() != null) {
            endTime = convertSchedulerTimeToSAP_ITSAM(entry.getEndTime());
        }
        long period = entry.getPeriod();
        
        SAP_ITSAMJavaSchedulerRecurringEntry result = new SAP_ITSAMJavaSchedulerRecurringEntry(DUMMY_INSTANCE_ID,
                                                                                               startTime,
                                                                                               endTime,
                                                                                               period);
        return result;
    }

    
    // CronEntry[] --> SAP_ITSAMJavaSchedulerCronEntry[]
    public static SAP_ITSAMJavaSchedulerCronEntry[] convertCronEntryArrayToSAP_ITSAM(CronEntry[] entries) {
        SAP_ITSAMJavaSchedulerCronEntry[] result = new SAP_ITSAMJavaSchedulerCronEntry[entries.length];
        
        for (int j = 0; j < entries.length; j++) {
            result[j] = convertCronEntryToSAP_ITSAM(entries[j]);
        }        
        return result;
    }
    
    
    // CronEntry --> SAP_ITSAMJavaSchedulerCronEntry
    public static SAP_ITSAMJavaSchedulerCronEntry convertCronEntryToSAP_ITSAM(CronEntry entry) {
        String timeZone = entry.getTimeZone().getID();
        String years = entry.getYears().toString();
        String months = entry.getMonths().toString();
        String hours = entry.getHours().toString();
        String min = entry.getMinutes().toString();
        String dayOfMonth = entry.getDays_of_month().toString();
        String dayOfWeek = entry.getDays_of_week().toString();
        
        SAP_ITSAMJavaSchedulerCronEntry result = new SAP_ITSAMJavaSchedulerCronEntry(DUMMY_INSTANCE_ID,
                                                                                     timeZone,
                                                                                     years,
                                                                                     months,
                                                                                     dayOfMonth,
                                                                                     dayOfWeek,
                                                                                     hours,
                                                                                     min);
        return result;
    }
    
    
    // Filter[] --> SAP_ITSAMJavaSchedulerFilter[]
    public static SAP_ITSAMJavaSchedulerFilter[] convertFilterArrayToSAP_ITSAM(Filter[] filters) {
        SAP_ITSAMJavaSchedulerFilter[] result = new SAP_ITSAMJavaSchedulerFilter[filters.length];
        
        for (int j = 0; j < filters.length; j++) {
            result[j] = convertFilterToSAP_ITSAM(filters[j]);
        }        
        return result;
    }
    
    
    // SchedulerTask --> SAP_ITSAMJavaSchedulerTask
    public static SAP_ITSAMJavaSchedulerTask convertSchedulerTaskToSAP_ITSAM(SchedulerTask task, String jobDefName, String appName) {
        SAP_ITSAMJavaSchedulerFilter[] filters = convertFilterArrayToSAP_ITSAM(task.getFilters());
        SAP_ITSAMJavaSchedulerJobParameter[] jobParameters = convertJobParameterArrayToSAP_ITSAM(task.getJobParameters());
        SAP_ITSAMJavaSchedulerRecurringEntry[] recurringEntries = convertRecurringEntryArrayToSAP_ITSAM(task.getRecurringEntries());
        SAP_ITSAMJavaSchedulerCronEntry[] cronEntries = convertCronEntryArrayToSAP_ITSAM(task.getCronEntries());
                
        SAP_ITSAMJavaSchedulerTask result = new SAP_ITSAMJavaSchedulerTask(task.getName(),
                                                                           task.getDescription(),
                                                                           DUMMY_INSTANCE_ID,
                                                                           task.getRetentionPeriod(),
                                                                           getRetentionPeriodFormatted(task.getRetentionPeriod()),
                                                                           filters,
                                                                           task.getJobDefinitionId().toString(),
                                                                           jobDefName, 
                                                                           appName, 
                                                                           jobParameters,
                                                                           recurringEntries,
                                                                           cronEntries,
                                                                           task.getTaskId().toString(),
                                                                           task.getTaskStatus().toString(),
                                                                           task.getTaskStatus().getDescription(),
                                                                           task.getSchedulingUser(),
                                                                           task.getRunAsUser(),
                                                                           task.getTaskSourceDescription()); 
        return result;
    }
    
    
    // SchedulerLogIterator --> SAP_ITSAMJavaSchedulerLogIterator
    public static SAP_ITSAMJavaSchedulerLogIterator convertSchedulerLogIteratorToSAP_ITSAM(SchedulerLogRecordIterator logIter) {
        // read the whole log
        SchedulerLogRecord[] logRecords = logIter.nextChunk();
        SAP_ITSAMJavaSchedulerLogRecord[] logRecs = convertSchedulerLogRecordArrayToSAP_ITSAM(logRecords);
        SAP_ITSAMJavaSchedulerLogIterator result = new SAP_ITSAMJavaSchedulerLogIterator(logRecs, String.valueOf(logIter.getPos()));
        
        return result;
    }
    
    
    // JobIterator --> SAP_ITSAMJavaSchedulerJobIterator
    public static SAP_ITSAMJavaSchedulerJobIterator convertJobIteratorToSAP_ITSAM(JobIterator jobIter, Map<JobID, String[]> names) {
        SAP_ITSAMJavaSchedulerJob[] jobs = convertJobArrayToSAP_ITSAM(jobIter.getJobs(), names);
        String stateDescriptor = jobIter.getStateDescriptor().toString();
        SAP_ITSAMJavaSchedulerJobIterator jobIterResult = new SAP_ITSAMJavaSchedulerJobIterator(jobs, stateDescriptor);
                
        return jobIterResult;
    }
    
    
    // JobDefinitionName --> SAP_ITSAMJavaSchedulerJobDefinitionName
    public static SAP_ITSAMJavaSchedulerJobDefinitionName convertJobDefinitionNameToSAP_ITSAM(JobDefinitionName jobDefName) {
        SAP_ITSAMJavaSchedulerJobDefinitionName result = new SAP_ITSAMJavaSchedulerJobDefinitionName(jobDefName.getApplicationName(),
                                                                                                     jobDefName.getName());                
        return result;
    }
    
    
    // String[][] --> SAP_ITSAMProperty[] (Properties of JobDefinition)
    public static SAP_ITSAMProperty[] convertStringArrayPropertiesToSAP_ITSAM(String[][] props) {
        SAP_ITSAMProperty[] result = new SAP_ITSAMProperty[props.length];
        
        for (int i = 0; i < props.length; i++) {
            String[] prop = props[i];
            result[i] = new SAP_ITSAMProperty();
            result[i].setElementName(prop[0]);
            result[i].setValue(prop[1]);
        }
              
        return result;
    }    
    
    
    // SchedulerLogRecord[] --> SAP_ITSAMJavaSchedulerLogRecord[]
    public static SAP_ITSAMJavaSchedulerLogRecord[] convertSchedulerLogRecordArrayToSAP_ITSAM(SchedulerLogRecord[] logRecords) {
        SAP_ITSAMJavaSchedulerLogRecord[] result = new SAP_ITSAMJavaSchedulerLogRecord[logRecords.length];
        
        for (int i = 0; i < logRecords.length; i++) {
            result[i] = convertSchedulerLogRecordToSAP_ITSAM(logRecords[i]);
        }
              
        return result;
    } 
    
    
    
    // SchedulerLogRecord --> SAP_ITSAMJavaSchedulerLogRecord
    public static SAP_ITSAMJavaSchedulerLogRecord convertSchedulerLogRecordToSAP_ITSAM(SchedulerLogRecord logRecord) {
        SAP_ITSAMJavaSchedulerLogRecord result = new SAP_ITSAMJavaSchedulerLogRecord(logRecord.getMessage(),
                                                                                     logRecord.getDate(),
                                                                                     logRecord.getSeverity());              
        return result;
    }
    
    
    // Localization-info-Map --> SAP_ITSAMJavaSchedulerProperty[]
    /**
     * Method converts the localization to the MBean format
     * 
     * The outgoing format looks as follows:
     * 
     * SAP_ITSAMJavaSchedulerProperty has the java.util.Locale and a 
     * SAP_ITSAMProperty[] as members.
     * 
     * The entries in SAP_ITSAMProperty[] have as key the key for the JobDefinition,
     * respectively the JobParameterDefinition. The value is the localized String.
     */
    public static SAP_ITSAMJavaSchedulerProperty[] convertLocalizationMapToSAP_ITSAM(HashMap<String, HashMap<String, String>> localeMap) {
        if (localeMap == null) {
            return null;
        }
        
        SAP_ITSAMJavaSchedulerProperty[] result = new SAP_ITSAMJavaSchedulerProperty[localeMap.size()];
        
        // the incoming map has the format
        // Key: Locale
        // Value: Properties with the key-value mapping
        int i = 0;
        for (Iterator<Map.Entry<String, HashMap<String, String>>> iter = localeMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<String, HashMap<String, String>> entry = iter.next();
            
            String l = entry.getKey();
            HashMap<String, String> props = entry.getValue();
            
            SAP_ITSAMProperty[] itsamProps = new SAP_ITSAMProperty[props.size()];    
            int j = 0;
            
            for (Iterator<Map.Entry<String, String>> iterator = props.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String, String> property = iterator.next();
                
                itsamProps[j] = new SAP_ITSAMProperty();
                itsamProps[j].setElementName(property.getKey());
                itsamProps[j].setValue(property.getValue());
                
                j++;
            }            
            result[i] = new SAP_ITSAMJavaSchedulerProperty(DUMMY_INSTANCE_ID, l.toString(), itsamProps);
            
            i++;
        }
        
        return result;
    }
    
    
    
    // -------------------------------------------------------------------------
    // ------------ Methods to convert from SAP_ITSAM format -------------------
    // -------------------------------------------------------------------------
    
    // SAP_ITSAMJavaSchedulerJobParameter[] into JobParameter[]
    public static JobParameter[] convertSAP_ITSAMToJobParameterArray(SAP_ITSAMJavaSchedulerJobParameter[] jobParamSAP_ITSAM) throws MBeanException {
                
        JobParameter[] result = new JobParameter[jobParamSAP_ITSAM.length];
        for (int i = 0; i < jobParamSAP_ITSAM.length; i++) {
            JobParameterDefinition jobParamDef = SAP_ITSAM_Helper.convertSAP_ITSAMToJobParameterDefinition(jobParamSAP_ITSAM[i].getType());
            result[i] = new JobParameter(jobParamDef, jobParamSAP_ITSAM[i].getValue());
        }
        
        return result;
    }
    
    
    // SAP_ITSAMJavaSchedulerJobParameterDefinition --> JobParameterDefinition 
    public static JobParameterDefinition convertSAP_ITSAMToJobParameterDefinition(SAP_ITSAMJavaSchedulerJobParameterDefinition jobParamDefSAP_ITSAM) throws MBeanException {
        HashMap<String, HashMap<String, String>> localeMap = convertSAP_ITSAMToLocalizationMap(jobParamDefSAP_ITSAM.getLocalizationInfo());
        JobParameterDefinition jobParamDef = new JobParameterDefinition(jobParamDefSAP_ITSAM.getElementName(),
                                                                        JobParameterType.valueOf(jobParamDefSAP_ITSAM.getType()),
                                                                        jobParamDefSAP_ITSAM.getNullable(),
                                                                        jobParamDefSAP_ITSAM.getDescription(),
                                                                        jobParamDefSAP_ITSAM.getDataDefault(),
                                                                        jobParamDefSAP_ITSAM.getDisplay(),
                                                                        jobParamDefSAP_ITSAM.getDirection(),
                                                                        jobParamDefSAP_ITSAM.getGroup(),
                                                                        localeMap); 
                                                                             
        return jobParamDef;
    }
    
    
    public static JobID[] convertStringArrayToJobIDArray(String[] ids) {
        JobID[] jobIds = new JobID[ids.length];
        for (int i = 0; i < ids.length; i++) {
            jobIds[i] = JobID.parseID(ids[i]);
        }
        return jobIds;
    }
    
    
    public static JobDefinitionID[] convertStringArrayToJobDefinitionIdArray(String[] ids) {
        JobDefinitionID[] result = new JobDefinitionID[ids.length];
        for (int i = 0; i < result.length; i++) {
           result[i] = JobDefinitionID.parseID(ids[i]); 
        }
        return result;
    }
    
    // SAP_ITSAMJavaSchedulerDefinition --> SchedulerDefinition
    public static SchedulerDefinition convertSAP_ITSAMToSchedulerDefinition(SAP_ITSAMJavaSchedulerDefinition schedDef) {        
        SchedulerDefinition result = new SchedulerDefinition(SchedulerID.parseID(schedDef.getSchedulerID()),
                                                             schedDef.getElementName(),
                                                             schedDef.getUser(),
                                                             schedDef.getDescription(),
                                                             SubscriberID.parseID(schedDef.getSubscriberID()),
                                                             schedDef.getSchedulerStatus() == (short)0 ? SchedulerStatus.inactive : SchedulerStatus.active,
                                                             schedDef.getLastAccess(),
                                                             schedDef.getInactivityGracePeriod());
        
        return result;
    }
    
    
    public static Filter[] convertSAP_ITSAMToFilterArray(SAP_ITSAMJavaSchedulerFilter[] filter) {
        Filter[] result = new Filter[filter.length];
        
        for (int i = 0; i < filter.length; i++) {
            result[i] = convertSAP_ITSAMToFilter(filter[i]);
        }
        
        return result;
    }
    
    
    // SAP_ITSAMJavaSchedulerFilter --> Filter
    public static Filter convertSAP_ITSAMToFilter(SAP_ITSAMJavaSchedulerFilter filter) {
        SchedulerTime startTime = convertSAP_ITSAMToSchedulerTime(filter.getStartTime());
        SchedulerTime endTime = convertSAP_ITSAMToSchedulerTime(filter.getEndTime());                
        Filter result = new Filter(startTime, endTime);        
        
        return result;
    }
    
    // SAP_ITSAMJavaSchedulerTime --> SchedulerTime
    public static SchedulerTime convertSAP_ITSAMToSchedulerTime(SAP_ITSAMJavaSchedulerTime schedTime) {
        if (schedTime != null && schedTime.getTimeZone() != null && schedTime.getTime() != null) {
            TimeZone timeZone = TimeZone.getTimeZone(schedTime.getTimeZone());
            return new SchedulerTime(schedTime.getTime(), timeZone);
        }

        return null;        
    }
    
    
    // SAP_ITSAMJavaSchedulerFireTimeEvent --> FireTimeEvent
    public static FireTimeEvent convertSAP_ITSAMToFireTimeEvent(SAP_ITSAMJavaSchedulerFireTimeEvent event) {
        SchedulerTaskID taskID = SchedulerTaskID.parseID(event.getTaskId());
        SchedulerTime time = convertSAP_ITSAMToSchedulerTime(event.getTime());
        boolean filtered = event.getFiltered();
        
        return new FireTimeEvent(taskID, time, filtered);      
    }
    
    
    // SAP_ITSAMJavaSchedulerFireTimeEvent[] --> FireTimeEvent[]
    public static FireTimeEvent[] convertSAP_ITSAMToFireTimeEventArray(SAP_ITSAMJavaSchedulerFireTimeEvent[] events) {
        FireTimeEvent[] resultArray = new FireTimeEvent[events.length];
        
        for (int i = 0; i < events.length; i++) {
            resultArray[i] = convertSAP_ITSAMToFireTimeEvent(events[i]);
        }
        return resultArray;      
    }
    
    
    // SAP_ITSAMJavaSchedulerRecurringEntry --> RecurringEntry
    public static RecurringEntry convertSAP_ITSAMToRecurringEntry(SAP_ITSAMJavaSchedulerRecurringEntry entry) {
        SchedulerTime startTime = convertSAP_ITSAMToSchedulerTime(entry.getStartTime());
        SchedulerTime endTime = convertSAP_ITSAMToSchedulerTime(entry.getEndTime());
        long period = entry.getPeriod();
        
        return new RecurringEntry(startTime, endTime, period);
    }
    
    
    // SAP_ITSAMJavaSchedulerRecurringEntry[] --> RecurringEntry[]
    public static RecurringEntry[] convertSAP_ITSAMToRecurringEntryArray(SAP_ITSAMJavaSchedulerRecurringEntry[] entries) {
        RecurringEntry[] result = new RecurringEntry[entries.length];
        
        for (int i = 0; i < entries.length; i++) {
            result[i] = convertSAP_ITSAMToRecurringEntry(entries[i]);
        }        
        return result;
    }
    
    
    // SAP_ITSAMJavaSchedulerCronEntry --> CronEntry
    public static CronEntry convertSAP_ITSAMToCronEntry(SAP_ITSAMJavaSchedulerCronEntry entry) {
        CronYearField years = new CronYearField(entry.getYears());
        CronMonthField months = new CronMonthField(entry.getMonths());
        CronDOMField dayOfMonth = new CronDOMField(entry.getDaysOfMonth());
        CronDOWField dayOfWeek = new CronDOWField(entry.getDaysOfWeek());
        CronHourField hours = new CronHourField(entry.getHours());
        CronMinuteField minutes = new CronMinuteField(entry.getMinutes());
        TimeZone tz = TimeZone.getTimeZone(entry.getTimeZone());
        
        return new CronEntry(years,
                             months,
                             dayOfMonth,
                             dayOfWeek,
                             hours, 
                             minutes,
                             tz);
    }
    
    
    // SAP_ITSAMJavaSchedulerCronEntry[] --> CronEntry[]
    public static CronEntry[] convertSAP_ITSAMToCronEntryArray(SAP_ITSAMJavaSchedulerCronEntry[] entries) {
        CronEntry[] result = new CronEntry[entries.length];
        
        for (int i = 0; i < entries.length; i++) {
            result[i] = convertSAP_ITSAMToCronEntry(entries[i]);
        }        
        return result;
    }

    
    // SAP_ITSAMJavaSchedulerTask --> SchedulerTask
    // TODO --> use other constructor?
    public static SchedulerTask convertSAP_ITSAMToSchedulerTask(SAP_ITSAMJavaSchedulerTask task) throws MBeanException {
        JobDefinitionID jobDefId = JobDefinitionID.parseID(task.getJobDefinitionId());
        JobParameter[] jobParams = convertSAP_ITSAMToJobParameterArray(task.getJobParameters());
        RecurringEntry[] recEntries = convertSAP_ITSAMToRecurringEntryArray(task.getRecurringEntries());
        CronEntry[] cronEntries = convertSAP_ITSAMToCronEntryArray(task.getCronEntries());
        int retentionPeriod = task.getRetentionPeriod();
        String taskName = task.getElementName();
        String taskDesc = task.getDescription();
      
        SchedulerTask result = SchedulerTask.createSchedulerTask(jobDefId, jobParams, recEntries, cronEntries, null, retentionPeriod, taskName, taskDesc, null);
        return result;      
    }
    
    
    // SAP_ITSAMJavaSchedulerLogIterator --> SchedulerLogRecordIterator
    public static SchedulerLogRecordIterator convertSAP_ITSAMToSchedulerLogIterator(SAP_ITSAMJavaSchedulerLogIterator iter) {        
        SchedulerLogRecordIterator result = new SchedulerLogRecordIterator();
        if (iter != null) // iter can be null while the first call
            result.setPos(new Long(iter.getStateDescriptor()));
        return result;
    }
    
    
    // SAP_ITSAMJavaSchedulerJobIterator --> JobIterator
    public static JobIterator convertSAP_ITSAMToJobIterator(SAP_ITSAMJavaSchedulerJobIterator iter) { 
        JobIterator result = new JobIterator();
        Job[] jobs = convertSAP_ITSAMToJobArray(iter.getJobs());
        result.setJobs(jobs);
        result.getStateDescriptor().readDescriptor(iter.getStateDescriptor());
        
        return result;
    }
    
    
    // SAP_ITSAMJavaSchedulerJob --> Job
    public static Job convertSAP_ITSAMToJob(SAP_ITSAMJavaSchedulerJob job) {
        JobStatus jobStatus = JobStatus.valueOf(job.getJobStatus());
        
        Job resultJob = new Job(JobID.parseID(job.getJobID()),
                                JobDefinitionID.parseID(job.getJobDefinitionId()),
                                SchedulerID.parseID(job.getSchedulerID()),
                                job.getName(),
                                jobStatus,
                                job.getStartTime(),
                                job.getEndDate(),
                                job.getSubmitDate(),
                                job.getNode(),
                                job.getReturnCode(),
                                job.getUser(),
                                JobID.parseID(job.getParentId()),
                                job.getVendorData(),
                                job.getCancelRequest(),
                                job.getRetentionPeriod(),
                                SchedulerTaskID.parseID(job.getTaskID()));
        return resultJob;
    }
    
    
    // SAP_ITSAMJavaSchedulerJob[] --> Job[]
    public static Job[] convertSAP_ITSAMToJobArray(SAP_ITSAMJavaSchedulerJob[] jobs) {
        Job[] result = new Job[jobs.length];
        
        for (int i = 0; i < jobs.length; i++) {
            result[i] = convertSAP_ITSAMToJob(jobs[i]);
        }        
        return result;
    }
    
    
    // SAP_ITSAMJavaSchedulerProperty[] --> Localization-info-Map
    public static HashMap<String, HashMap<String, String>> convertSAP_ITSAMToLocalizationMap(SAP_ITSAMJavaSchedulerProperty[] props) throws MBeanException {
        if (props == null) {
            return null;
        }
        
        HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
        
        for (int i = 0; i < props.length; i++) {
            SAP_ITSAMProperty[] itsamProps = props[i].getProperties();
            
            HashMap<String, String> properties = new HashMap<String, String>();
            for (int j = 0; j < itsamProps.length; j++) {
                properties.put( itsamProps[j].getElementName(), itsamProps[j].getValue() );
            } 
            
            result.put(props[i].getLocale(), properties);           
        }        
        
        return result;
    }


    // -------------------------------------------------------------------------
    // ---------------------- Helper-Methods -----------------------------------
    // -------------------------------------------------------------------------
    
    public static String getRetentionPeriodFormatted(int retPeriod) {
        // RetentionPeriod means the number of days to keep the job log records
        // after each job execution
        
        switch(retPeriod) {
            case -2: {
                // take the default retention period deployed with the JobDefinition
                return "Default from deployed JobDefinition";           
            }
            case -1: {
                // means job log records are never deleted
                return "Infinite";
            }  
            
            default: {
                // we return the specified value
                return String.valueOf(retPeriod)+" days";
            }
        }
    }
    
    
    public static String getLastAccessTimeFormatted(long lastAccess) {
        // Last access time means the time when this Scheduler was accessed the 
        // last time --> express it in a java.util.Date().toString()
        
        return DATE_FORMATTER.format(new Date(lastAccess));        
    }
    
    
    public static String getInactivityGracePeriodFormatted(long igp) {
        // the inactivity grace period (in ms) means the period after which the external 
        // scheduler will be set to inactive
        
        long days = igp/(1000*60*60*24);
        igp = igp - (days*(1000*60*60*24));
        
        long hours = igp/(1000*60*60);
        igp = igp - (hours*(1000*60*60));
            
        long min = igp/(1000*60);
        igp = igp - (min*(1000*60));
        
        long sec = igp/(1000);
        igp = igp - (sec*(1000));
        
        // we transform it into the format '1 day(s), 8:14:49 hour(s)' 
        String result = "";        
        result = days+" day(s), "+hours+":"+min+":"+sec+" hour(s)";  
        
        return result;
    }
    
}
