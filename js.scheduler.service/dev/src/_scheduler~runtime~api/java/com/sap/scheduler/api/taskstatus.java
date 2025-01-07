/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
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
import java.util.HashMap;
import java.util.Map;

/**
 * @author d040939 
 */
public class TaskStatus implements Serializable {
    /**
     * This class represents the TaskStatus. A Task can have the states 
     * active, hold and finished. There are also descriptions available which
     * explains the current state in more detail. 
     */
    
    // Note: TaskStatus 0 has been used formerly for state cancelled
    //       TaskStatus 3 has been used formerly for state undeployed
    protected static final short STATUS_ACTIVE   = 1;
    protected static final short STATUS_FINISHED = 2;
    protected static final short STATUS_HOLD     = 4;
    
    // for backward compatibility reasons where we had no status descripton
    protected static final short DESCRIPTION_UNDEFINED = 0;
    
    /** TaskStatus active */
    public static final TaskStatus active =   new TaskStatus(STATUS_ACTIVE, DESCRIPTION_UNDEFINED);
    /** TaskStatus finished */
    public static final TaskStatus finished = new TaskStatus(STATUS_FINISHED, DESCRIPTION_UNDEFINED);
    /** TaskStatus hold */
    public static final TaskStatus hold =     new TaskStatus(STATUS_HOLD, DESCRIPTION_UNDEFINED);
    
    /** Task has been created by a ZeroAdmin template */
    public static final Short STATUS_ACT_INITIAL_ZEROADMIN          = new Short((short)10);
    /** Task has been created by an API-call */
    public static final Short STATUS_ACT_INITIAL_API                = new Short((short)11);
    /** Task has been finished due to no execution times are left */
    public static final Short STATUS_ACT_TO_FIN_FINISHED            = new Short((short)12);
    /** Task has been finished due to the JobDefinition has been undeployed */
    public static final Short STATUS_ACT_TO_FIN_UNDEPLOYED          = new Short((short)13);
    /** Task has been finished because it has been cancelled by ZeroAdmin logic */
    public static final Short STATUS_ACT_TO_FIN_CANCELLED_ZEROADMIN = new Short((short)14);
    /** Task has been finished because it has been cancelled by an API-call */
    public static final Short STATUS_ACT_TO_FIN_CANCELLED_API       = new Short((short)15);
    /** Task has been set to hold due to an API-call */
    public static final Short STATUS_ACT_TO_HOLD_API                = new Short((short)16);
    /** Task has been set to hold due to error reasons */
    public static final Short STATUS_ACT_TO_HOLD_ERROR              = new Short((short)17); 
    /** Task has been released because it has been set to active by an API-call */
    public static final Short STATUS_HOLD_TO_ACT_API                = new Short((short)18);  
    
	private Short m_value;
    private Short m_descVal;	
    
    
    protected static final Map<Short, String> STATUS_MAP = new HashMap<Short, String>();
    protected static final Map<Short, String> STATUS_CHANGED_MAP = new HashMap<Short, String>();
    static {
        STATUS_MAP.put(new Short(STATUS_ACTIVE),   "active");
        STATUS_MAP.put(new Short(STATUS_FINISHED), "finished");
        STATUS_MAP.put(new Short(STATUS_HOLD),     "hold");
        STATUS_CHANGED_MAP.put(STATUS_ACT_INITIAL_ZEROADMIN,          "Task created by ZeroAdmin-template.");
        STATUS_CHANGED_MAP.put(STATUS_ACT_INITIAL_API,                "Task created by API.");
        STATUS_CHANGED_MAP.put(STATUS_ACT_TO_FIN_FINISHED,            "Active to finished - Task has finished.");
        STATUS_CHANGED_MAP.put(STATUS_ACT_TO_FIN_UNDEPLOYED,          "Active to finished - JobDefinition has been undeployed.");
        STATUS_CHANGED_MAP.put(STATUS_ACT_TO_FIN_CANCELLED_ZEROADMIN, "Task set to finished - Task has been cancelled by ZeroAdmin-logic.");
        STATUS_CHANGED_MAP.put(STATUS_ACT_TO_FIN_CANCELLED_API,       "Task set to finished - Task has been cancelled by API.");
        STATUS_CHANGED_MAP.put(STATUS_ACT_TO_HOLD_API,                "Active to hold - Task has been set to hold through API call.");
        STATUS_CHANGED_MAP.put(STATUS_ACT_TO_HOLD_ERROR,              "Active to hold - Task has been set to hold through error reasons. It failed several times in a row.");
        STATUS_CHANGED_MAP.put(STATUS_HOLD_TO_ACT_API,                "Hold to active - Task has been set to active through API call.");
    }
     
        
    protected TaskStatus(short value, short descVal) {
        this.m_value = new Short(value);
        this.m_descVal = new Short(descVal);
    }
	
    /**
     * Returns the value which represents the task status.
     * @return short the value
     */
	public short getValue() {
		return m_value.shortValue();
	}
    
    /**
     * Returns the description. In case the description has not been set, null
     * will be returned.
     * @return the description
     */
    public String getDescription() {
        return STATUS_CHANGED_MAP.get(m_descVal);        
    }
    
    /**
     * Return the description value. In case the description has not been set, 0
     * will be returned.
     * @return the description value
     */
    public short getDescriptionValue() {
        return m_descVal.shortValue();
    }
	
    /**
     * Returns the status as String, without description.
     * @return the m_value as String
     */
	public String toString() {
		return STATUS_MAP.get(m_value);
	}
    
    /**
     * Is equal if m_value is equal. Does not recognize the description.
     * 
     * return true if equal
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TaskStatus)) {
            return false;
        }
        return (getValue() == ((TaskStatus) obj).getValue());
    }

    /**
     * @see #equals(Object obj)
     */
    public int hashCode() {
        return new Short(m_value).hashCode();
    }    
    
    
    /**
     * return true if the task is in state active, otherwise false.
     * @return true if the task is in state active
     */
    public boolean isActive() {
        return m_value.shortValue() == active.getValue();
    }
    
    /**
     * return true if the task is in state hold, otherwise false.
     * @return true if the task is in state hold
     */
    public boolean isHold() {
        return m_value.shortValue() == hold.getValue();
    }
    
    /**
     * return true if the task is in state finished, otherwise false.
     * @return true if the task is in state finished
     */
    public boolean isFinished() {
        return m_value.shortValue() == finished.getValue();
    }
    
}
