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

import com.sap.scheduler.api.SchedulerTaskID;

/**
 * This filter class is used for querying jobs. Setting no filters means 
 * that all jobs will be selected. Setting more than one filter means an
 * an AND relationship between them.
 * 
 * @author Dirk Marwinski
 *
 */
public class JobFilter implements Serializable {

    static final long serialVersionUID = 987147823623151897L;
    
    private Date startedFrom;
    private Date startedTo;

    private Date endedFrom;
    private Date endedTo;

    private JobStatus status;

    private String vendorData;
    private JobDefinitionID id;
    private JobID parentID;
    private JobID jobID;
    
    private SchedulerID scheduler;

    private Short returnCode;
    private String node;
    private String userId;
    
    private SchedulerTaskID m_SchedTaskId;
    
    private String m_name;

    public Date getEndedFrom() {
        return endedFrom;
    }

    /**
     * Selects all jobs which ended after the given point in time
     * 
     * @param endedFrom 
     */
    public void setEndedFrom(Date endedFrom) {
        this.endedFrom = endedFrom;
    }
    public Date getEndedTo() {
        return endedTo;
    }
    
    /**
     * Selects all jobs which ended before the given point in time
     * 
     * @param endedTo
     */
    public void setEndedTo(Date endedTo) {
        this.endedTo = endedTo;
    }
    public JobDefinitionID getJobDefinitionId() {
        return id;
    }
    
    /**
     * Selects all instances of the given job definition
     *
     * @param id
     */
    public void setJobDefinitionId(JobDefinitionID id) {
        this.id = id;
    }
    public String getNode() {
        return node;
    }
    
    /**
     * Selects all jobs which were executed on a particular node
     * 
     * @param node
     */
    public void setNode(String node) {
        this.node = node;
    }
    public JobID getParentId() {
        return parentID;
    }
    
    /**
     * Selects all jobs which are children of the specified parent job
     * 
     * @param parentID
     */
    public void setParentId(JobID parentID) {
        this.parentID = parentID;
    }
    public Short getReturnCode() {
        return returnCode;
    }
    
    /**
     * Selects all jobs with the specified return code
     * @param returnCode
     */
    public void setReturnCode(Short returnCode) {
        this.returnCode = returnCode;
    }
    public SchedulerID getScheduler() {
        return scheduler;
    }
    
    /**
     * Selects all jobs which were triggered by the specified scheduler
     * @param scheduler
     */
    public void setScheduler(SchedulerID scheduler) {
        this.scheduler = scheduler;
    }
    public Date getStartedFrom() {
        return startedFrom;
    }
    
    /**
     * Selects all jobs which started after the given point in time.
     * 
     * @param startedFrom 
     */
    public void setStartedFrom(Date startedFrom) {
        this.startedFrom = startedFrom;
    }
    public Date getStartedTo() {
        return startedTo;
    }

    /**
     * Selects all jobs which started before the given point in time.
     * 
     * @param startedTo
     */
    public void setStartedTo(Date startedTo) {
        this.startedTo = startedTo;
    }
    public JobStatus getJobStatus() {
        return status;
    }
    
    /**
     * Selects all jobs with the specified status.
     * 
     * @param status
     */
    public void setJobStatus(JobStatus status) {
        this.status = status;
    }
    public String getUserId() {
        return userId;
    }
    
    /**
     * Selects all jobs which ran under the specified user id.
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getVendorData() {
        return vendorData;
    }
    
    /**
     * Selects all jobs with the specified vendor data.
     * 
     * @param vendorData
     */
    public void setVendorData(String vendorData) {
        this.vendorData = vendorData;
    }
    
    /**
     * Selects the specified job.
     * @param id
     */
    public void setJobId(JobID id) {
        this.jobID = id;
    }
    public JobID getJobId() {
        return jobID;
    }
    
    
    /**
     * Selects all jobs which were triggered by the specified scheduler task.
     * 
     * @param schedTaskId
     */
    public void setSchedulerTaskId(SchedulerTaskID schedTaskId) {
        this.m_SchedTaskId = schedTaskId;
    }
    public SchedulerTaskID getSchedulerTaskId() {
        return m_SchedTaskId;
    }    
    
    
    /**
     * Selects all jobs with the specified job-name.
     * 
     * @param name
     */
    public void setName(String name) {
        this.m_name = name;
    }
    public String getName() {
        return m_name;
    }  
    
    
    public String toString() {
    	StringBuilder buf = new StringBuilder();
        boolean separator = false;
                
        if (getStartedFrom() != null) {
            appendSeparator(buf, separator);
            buf.append("StartedFrom=\"").append(getStartedFrom().getTime()).append("\"");
            separator = true;
        }
        
        if (getStartedTo() != null) {
            appendSeparator(buf, separator);
            buf.append("StartedTo=\"").append(getStartedTo().getTime()).append("\"");
            separator = true;
        }

        if (getEndedFrom() != null) {
            appendSeparator(buf, separator);
            buf.append("EndedFrom=\"").append(getEndedFrom().getTime()).append("\"");
            separator = true;
        }

        if (getEndedTo() != null) {
            appendSeparator(buf, separator);
            buf.append("EndedTo=\"").append(getEndedTo().getTime()).append("\"");
            separator = true;
        }
        
        if (getJobStatus() != null) {
            appendSeparator(buf, separator);
            buf.append("JobStatus=\"").append(getJobStatus().toString()).append("\"");
            separator = true;
        }

        if (getVendorData() != null) {
            appendSeparator(buf, separator);
            buf.append("VendorData=\"").append(getVendorData()).append("\"");
            separator = true;
        }

        if (getJobDefinitionId() != null) {
            appendSeparator(buf, separator);
            buf.append("JobDefinitionID=\"").append(getJobDefinitionId()).append("\"");
            separator = true;
        }

        if (getParentId() != null) {
            appendSeparator(buf, separator);
            buf.append("ParentID=\"").append(getParentId()).append("\"");
            separator = true;
        }

        if (getJobId() != null) {
            appendSeparator(buf, separator);
            buf.append("JobID=\"").append(getJobId()).append("\"");
            separator = true;
        }

        if (getScheduler() != null) {
            appendSeparator(buf, separator);
            buf.append("SchedulerID=\"").append(getScheduler()).append("\"");
            separator = true;
        }

        if (getReturnCode() != null) {
            appendSeparator(buf, separator);
            buf.append("ReturnCode=\"").append(getReturnCode()).append("\"");
            separator = true;
        }
        
        if (getNode() != null) {
            appendSeparator(buf, separator);
            buf.append("Node=\"").append(getNode()).append("\"");
            separator = true;
        }

        if (getUserId() != null) {
            appendSeparator(buf, separator);
            buf.append("UserID=\"").append(getUserId()).append("\"");
            separator = true;
        }
        
        if (getSchedulerTaskId() != null) {
            appendSeparator(buf, separator);
            buf.append("SchedulerTaskID=\"").append(getSchedulerTaskId().toString()).append("\"");
            separator = true;
        }
        
        return buf.toString();
    }
    
    private void appendSeparator(StringBuilder buf, boolean append) {
        if (append) {
            buf.append(",");
        }
    }
}
