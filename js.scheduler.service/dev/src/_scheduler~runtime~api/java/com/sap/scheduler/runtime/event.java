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
package com.sap.scheduler.runtime;

import java.io.Serializable;
import java.util.Date;
/**
 * @author Dirk Marwinski
 * @author Thomas Mueller (d040939)
 *
 * This class represents an event which will be delivered to a registered
 * EventConsumer.
 * <p>
 * @see com.sap.scheduler.runtime.EventConsumer
 * @see com.sap.scheduler.api.Scheduler#addEventListener(String[], EventConsumer)
 * @see com.sap.scheduler.api.Scheduler#removeEventListener(EventConsumer)
 */
public class Event implements Serializable
{
    static final long serialVersionUID = -7611534664153680898L;
    
    public final static String RUNTIME_ROOT = "com.sap.scheduler.runtime.";
    
    private static final String LINE_WRAP = System.getProperty("line.separator");
    
    // -------------------------------------------------------------------------
    // -------------------------- Events for Jobs ------------------------------
    // -------------------------------------------------------------------------
    
    /**
     * Event raised by the job scheduler when a job is submitted. The
     * parameter contains the job id.
     * 
     * @deprecated event is not used
     */
    public final static String EVENT_JOB_SUBMITTED = RUNTIME_ROOT + "JobSubmitted";
    /**
     * @deprecated event is not used
     */
    public final static String EVENT_JOB_SUBMITTED_DESC = "Event raised by the job scheduler when a job is submitted. The parameter contains the job id.";

    /**
     * Event raised by the job scheduler when a job is held. The
     * parameter contains the job id.
     * 
     * @deprecated event is not used
     */
    public final static String EVENT_JOB_HELD = RUNTIME_ROOT + "JobHeld";
    /**
     * @deprecated event is not used
     */
    public final static String EVENT_JOB_HELD_DESC = "Event raised by the job scheduler when a job is held. The parameter contains the job id.";

    /**
     * Event raised by the job scheduler when a job is released. The
     * parameter contains the job id.
     * 
     * @deprecated event is not used
     */
    public final static String EVENT_JOB_RELEASED = RUNTIME_ROOT + "JobReleased";
    /**
     * @deprecated event is not used
     */
    public final static String EVENT_JOB_RELEASED_DESC = "Event raised by the job scheduler when a job is released. The parameter contains the job id.";
    
    /**
     * Event raised by the job scheduler when a job is starting. The
     * parameter contains the job id.
     */
    public final static String EVENT_JOB_STARTING = RUNTIME_ROOT + "JobStarting";
    public final static String EVENT_JOB_STARTING_DESC = "Event raised by the job scheduler when a job is starting. The parameter contains the job id.";

    /**
     * Event raised by the job scheduler when a job is started. The
     * parameter contains the job id.
     */
    public final static String EVENT_JOB_STARTED = RUNTIME_ROOT + "JobStarted";
    public final static String EVENT_JOB_STARTED_DESC = "Event raised by the job scheduler when a job is started. The parameter contains the job id.";
    
    /**
     * Event raised by the job scheduler when a job has finished. The 
     * parameter contains the job id.
     */
    public final static String EVENT_JOB_FINISHED = RUNTIME_ROOT + "JobFinished";
    public final static String EVENT_JOB_FINISHED_DESC = "Event raised by the job scheduler when a job has finished. The parameter contains the job id.";

    /**
     * Event raised by the job scheduler when a job is cancelled. The 
     * parameter contains the job id.
     */
    public final static String EVENT_JOB_CANCELLED  = RUNTIME_ROOT + "JobCancelled";
    public final static String EVENT_JOB_CANCELLED_DESC  = "Event raised by the job scheduler when a job is cancelled. The parameter contains the job id.";
    
    /**
     * Event raised when a job is deleted. It does not matter is the job 
     * has already run or not. The parameter contains the job id.
     * 
     * @deprecated event is not used
     */
    public final static String EVENT_JOB_REMOVED = RUNTIME_ROOT + "JobRemoved";
    /**
     * @deprecated event is not used
     */
    public final static String EVENT_JOB_REMOVED_DESC = "Event raised when a job is deleted. It does not matter is the job has already run or not. The parameter contains the job id.";

    /**
     * Event raised when a job definition is deployed. The parameter contains the job 
     * definition name. 
     */
    public final static String EVENT_JOB_DEFINITION_DEPLOYED   = RUNTIME_ROOT + "JobDefinitionDeployed";
    public final static String EVENT_JOB_DEFINITION_DEPLOYED_DESC   = "Event raised when a job definition is deployed. The parameter contains the job definition name.";

    /**
     * Event raised when a job definition is undeployed. The parameter contains the job 
     * definition name. See { @link #EVENT_JOB_DEFINITION_DEPLOYED } also.
     */
    public final static String EVENT_JOB_DEFINITION_UNDEPLOYED = RUNTIME_ROOT + "JobDefinitionUndeployed";
    public final static String EVENT_JOB_DEFINITION_UNDEPLOYED_DESC = "Event raised when a job definition is undeployed. The parameter contains the job definition name.";

    /**
     * Event raised when a job definition is deployed. The parameter contains the job 
     * definition id. 
     */
    public final static String EVENT_JOB_DEFINITION_DEPLOYED1 = RUNTIME_ROOT + "JobDefinitionDeployed1";
    public final static String EVENT_JOB_DEFINITION_DEPLOYED1_DESC = "Event raised when a job definition is deployed. The parameter contains the job definition id.";

    /**
     * Event raised when a job definition is undeployed. The parameter contains the job 
     * definition id. See { @link EVENT_JOB_DEFINITION_DEPLOYED1} also.
     */
    public final static String EVENT_JOB_DEFINITION_UNDEPLOYED1 = RUNTIME_ROOT + "JobDefinitionUndeployed1";
    public final static String EVENT_JOB_DEFINITION_UNDEPLOYED1_DESC = "Event raised when a job definition is undeployed. The parameter contains the job definition id."; 

    /**
     * Event raised when a job definition is redeployed. The parameter contains the job 
     * definition name. If there is no change to the job definition, no event 
     * will be raised.
     * 
     * @deprecated event is not used
     */
    public final static String EVENT_JOB_DEFINITION_REDEPLOYED = RUNTIME_ROOT + "JobDefinitionRedeployed";
    
    
    // -------------------------------------------------------------------------
    // -------------------------- Events for Tasks -----------------------------
    // -------------------------------------------------------------------------
    
    /**
     * Event raised when a task has been created. The parameter contains the task id.
     */
    public final static String EVENT_TASK_CREATED = RUNTIME_ROOT + "TaskCreated";
    public final static String EVENT_TASK_CREATED_DESC = "Event raised when a task has been created. The parameter contains the task id."; 
    /**
     * Event raised when a task has been set to hold. The parameter contains the task id.
     */
    public final static String EVENT_TASK_HOLD = RUNTIME_ROOT + "TaskHold";
    public final static String EVENT_TASK_HOLD_DESC = "Event raised when a task has been set to hold. The parameter contains the task id.";
    /**
     * Event raised when a task has been set to release. The parameter contains the task id.
     */
    public final static String EVENT_TASK_RELEASED = RUNTIME_ROOT + "TaskReleased";
    public final static String EVENT_TASK_RELEASED_DESC = "Event raised when a task has been set to released. The parameter contains the task id.";
    /**
     * Event raised when a task has been cancelled. The parameter contains the task id.
     */
    public final static String EVENT_TASK_CANCELLED = RUNTIME_ROOT + "TaskCancelled";
    public final static String EVENT_TASK_CANCELLED_DESC = "Event raised when a task has been cancelled. The parameter contains the task id.";
    /**
     * Event raised when a task has been finished. The parameter contains the task id.
     */
    public final static String EVENT_TASK_FINISHED = RUNTIME_ROOT + "TaskFinished";
    public final static String EVENT_TASK_FINISHED_DESC = "Event raised when a task has been finished. The parameter contains the task id.";
    
    
    private static final String[] EVENT_TYPES_TASK = new String[] {
                                                        EVENT_TASK_CREATED,
                                                        EVENT_TASK_HOLD,
                                                        EVENT_TASK_RELEASED,
                                                        EVENT_TASK_CANCELLED,
                                                        EVENT_TASK_FINISHED
                                                    };
    private static final String[] EVENT_TYPES_TASK_DESC = new String[] {
                                                        EVENT_TASK_CREATED_DESC,
                                                        EVENT_TASK_HOLD_DESC,
                                                        EVENT_TASK_RELEASED_DESC,
                                                        EVENT_TASK_CANCELLED_DESC,
                                                        EVENT_TASK_FINISHED_DESC
                                                    };    
    /**
     * Represents the event types which are available for users of the JXBP API
     */
    public final static String[] JXBP_RUNTIME_EVENT_TYPES = new String[] {
                                                        EVENT_JOB_STARTING,
                                                        EVENT_JOB_STARTED,
                                                        EVENT_JOB_FINISHED,
                                                        EVENT_JOB_CANCELLED,
                                                        EVENT_JOB_DEFINITION_DEPLOYED,
                                                        EVENT_JOB_DEFINITION_UNDEPLOYED,
                                                        EVENT_JOB_DEFINITION_DEPLOYED1,
                                                        EVENT_JOB_DEFINITION_UNDEPLOYED1
                                                    };    
    /**
     * Represents the event type descriptions which are available for users of the JXBP API
     */
    public final static String[] JXBP_RUNTIME_EVENT_TYPES_DESC = new String[] {
                                                        EVENT_JOB_STARTING_DESC,
                                                        EVENT_JOB_STARTED_DESC,
                                                        EVENT_JOB_FINISHED_DESC,
                                                        EVENT_JOB_CANCELLED_DESC,
                                                        EVENT_JOB_DEFINITION_DEPLOYED_DESC,
                                                        EVENT_JOB_DEFINITION_UNDEPLOYED_DESC,
                                                        EVENT_JOB_DEFINITION_DEPLOYED1_DESC,
                                                        EVENT_JOB_DEFINITION_UNDEPLOYED1_DESC
                                                    };   
    /**
     * Represents the event types which are available for users of the Scheduler API
     */
    public final static String[] RUNTIME_EVENT_TYPES      = new String[JXBP_RUNTIME_EVENT_TYPES.length+EVENT_TYPES_TASK.length];
    /**
     * Represents the event type descriptions which are available for users of the Scheduler API
     */
    public final static String[] RUNTIME_EVENT_TYPES_DESC = new String[JXBP_RUNTIME_EVENT_TYPES_DESC.length+EVENT_TYPES_TASK_DESC.length];
    static {
        for (int i = 0; i < JXBP_RUNTIME_EVENT_TYPES.length; i++) {
            RUNTIME_EVENT_TYPES[i]      = JXBP_RUNTIME_EVENT_TYPES[i];
            RUNTIME_EVENT_TYPES_DESC[i] = JXBP_RUNTIME_EVENT_TYPES_DESC[i];
        }
        for (int i = JXBP_RUNTIME_EVENT_TYPES.length; i < RUNTIME_EVENT_TYPES.length; i++) {
            RUNTIME_EVENT_TYPES[i]      = EVENT_TYPES_TASK[i-JXBP_RUNTIME_EVENT_TYPES.length];
            RUNTIME_EVENT_TYPES_DESC[i] = EVENT_TYPES_TASK_DESC[i-JXBP_RUNTIME_EVENT_TYPES.length];
        }        
    }


    private String  mType;
    private String  mParameter;
    private String  mAdditionalParameter;
    private Date    mRaisedDate;
    private AbstractIdentifier  mRaisedByDetails;
    private byte[]  mId;
    
    public Event(String type, String parameter, String additionalParameter, Date raisedDate) {
        this(type, parameter, additionalParameter, null, raisedDate);
    }
    
    /**
     * @deprecated
     */
    public Event(String type, String parameter, AbstractIdentifier raisedByDetails, Date raisedDate) {
       this(type, parameter, null, raisedByDetails, raisedDate); 
    }

    /**
     * @deprecated
     */
    public Event(String type, String parameter, AbstractIdentifier raisedByDetails) {
        this(type, parameter, null, raisedByDetails, new Date());
    }

    /**
     * @deprecated
     */
    public Event(String type, String parameter, String additionalParameter, AbstractIdentifier raisedByDetails, Date raisedDate) {
        mType = type;
        mParameter = parameter;
        mAdditionalParameter = additionalParameter;
        mRaisedByDetails = raisedByDetails;
        mRaisedDate = raisedDate;
    }

    /**
     * Returns the type of this event. 
     * 
     * @return Event type
     */
    public String getType() {
        return mType;
    }
    
    /**
     * Returns the parameter for this event
     * 
     * @return Event parameter
     */
    public String getParameter() {
        return mParameter;
    }

    /**
     * Returns the additional parameter which is sent with some events.
     * 
     * @return the additional parameter (can be null)
     */
    public String getAdditionalParameter() {
        return mAdditionalParameter;
    }
    
    /**
     * Returns the date when this event was raised
     * 
     * @return The exact date then this event was raised.
     */
    public Date getRaisedDate() {
        return mRaisedDate;
    }
        
    /**
     * Further details about the source of this event.
     * 
     * @return more details on this event
     */
    public AbstractIdentifier getRaisedByDetails() {
        return mRaisedByDetails;
    }
    
    /**
     * @deprecated Event id is not used
     */
    public byte[] getId() {
        return mId;
    }
    
    /**
     * @deprecated Event id is not used
     */
    public void setId(byte[] id) {
        mId = id;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Type:              ").append(mType).append(LINE_WRAP);
        buf.append("Parameter:         ").append(mParameter).append(LINE_WRAP);
        buf.append("Additional param:  ").append(mAdditionalParameter).append(LINE_WRAP);
        buf.append("Raised by details: ").append(mRaisedByDetails).append(LINE_WRAP);
        buf.append("Date:              ").append(mRaisedDate).append(LINE_WRAP);
        return buf.toString();
    }
}
