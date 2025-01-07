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


/**
 * Job status codes.
 */
public class JobStatus implements Serializable
{
    static final long serialVersionUID = 2124872541397168057L;
    
    /**
     * The job was scheduled by the internal job scheduler, it will be 
     * started by the internal scheduler in the future. A job triggered by 
     * an external scheduler cannot reach that state.
     */
    public static final JobStatus SCHEDULED = new JobStatus((short)7);
    
    /**
     * A job which was in state SCHEDULE can be held by the internal or an 
     * external scheduler. In that case the job will go into the HOLD state. 
     */
    public static final JobStatus HOLD      = new JobStatus((short)8);
    
    /**
     * The job is currently executing.
     */
    public static final JobStatus RUNNING   = new JobStatus((short)13);
    
    /**
     * The job is in an unknown state. One possibility to get into that 
     * state is when a node is starting up and detects that there are 
     * currently running jobs on that node.
     */
    public static final JobStatus UNKNOWN   = new JobStatus((short)18);
    
    /**
     * The job has completed.
     */
    public static final JobStatus COMPLETED = new JobStatus((short)19);
    
    /**
     * The job has completed but threw an exception during execution or
     * it is clear that the job failed (e.g. for a http job the http 
     * message could not be sent).
     */
    public static final JobStatus ERROR     = new JobStatus((short)20);
    
    /**
     * A job has been cancelled while it was in state SCHEDULED, 
     * STARTING, or HOLD.
     */
    public static final JobStatus CANCELLED = new JobStatus((short)22);
    
    /**
     * The job is currently being started by the job execution runtime 
     * (e.g. the JMS messages has been sent but was not yet received by 
     * the job). For some job types this state may not be necessary, 
     * however it is guaranteed that a job will assume this state and an 
     * event is sent.
     */
    public static final JobStatus STARTING  = new JobStatus((short)25);
    
      
    private final short statusCode;

    
    private JobStatus(short s)
    {
        statusCode = s;
    }

    public short value() {
  	    return statusCode;
    }
  
    public static JobStatus valueOf(String s)
    {    
        if ("SCHEDULED".equals(s)) {
            return SCHEDULED;
        } else if ("HOLD".equals(s)) {
            return HOLD;
        } else if ("RUNNING".equals(s)) {
            return RUNNING;
        } else if ("UNKNOWN".equals(s)) {
            return UNKNOWN;
        } else if ("COMPLETED".equals(s)) {
            return COMPLETED;
        } else if ("ERROR".equals(s)) {
            return ERROR;
        } else if ("CANCELLED".equals(s)) {
            return CANCELLED;
        } else if ("STARTING".equals(s)) {
            return STARTING;
        } else {
            throw new IllegalArgumentException("Illegal job status '" + s + "'.");
        }
    }

    public static JobStatus valueOf(short s)
    {
        switch (s)
        {
            case 7: return SCHEDULED;
            case 8: return HOLD;
            case 13: return RUNNING;
            case 18: return UNKNOWN;
            case 19: return COMPLETED;
            case 20: return ERROR;
            case 22: return CANCELLED;
            case 25: return STARTING;
            default: throw new IllegalArgumentException("Illegal job status '" + s + "'.");
        }
    }

  public boolean equals(Object obj) {
     if (obj == null || !(obj instanceof JobStatus)) {
        return false;  
     }
     return value() == ((JobStatus)obj).value();
  }
  
  public int hashCode() {
      return new Short(statusCode).hashCode();
  }
  
  public String toString() {
    switch(statusCode) {
      case 7:
           return "SCHEDULED";
      case 8:
           return "HOLD";
      case 13:
           return "RUNNING";
      case 18:
           return "UNKNOWN";
      case 19:
           return "COMPLETED";
      case 20:
           return "ERROR";
      case 22:
           return "CANCELLED";
      case 25:
    	   return "STARTING"; 
      default:
           throw new IllegalArgumentException("Illegal status \"" + statusCode + "\".");
    }
  }
  
}
