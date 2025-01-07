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
package com.sap.engine.services.scheduler.jxbp.ws.wrapper;

import java.io.Serializable;
import java.util.Date;

import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.SchedulerID;

/**
 * This class represents a concrete job filter to be used by the 
 * JXBP Web Service.
 * 
 * @author Thomas Mueller (d040939)
 */
public class JobFilterWS implements Serializable {

    static final long serialVersionUID = 1L;
    
    private Date m_startedFrom;
    private Date m_startedTo;
    private Date m_endedFrom;
    private Date m_endedTo;
    private String m_jobStatus;
    private String m_vendorData;
    private JobDefinitionID m_jobDefinitionId;
    private JobID m_parentJobId;
    private JobID m_jobID;    
    private SchedulerID m_schedulerId;
    private Short m_returnCode;
    private String m_node;
    private String m_userId;    
    private SchedulerTaskID m_schedulerTaskId;    
    private String m_name;
    
    // default constructor needed for WebService
    public JobFilterWS() {}
    
    public JobFilterWS(JobFilter fil) {
        m_startedFrom = fil.getStartedFrom();
        m_startedTo = fil.getStartedTo();
        m_endedFrom = fil.getEndedFrom();
        m_endedTo = fil.getEndedTo();
        m_jobStatus = fil.getJobStatus().toString();
        m_vendorData = fil.getVendorData();
        m_jobDefinitionId = fil.getJobDefinitionId();
        m_parentJobId = fil.getParentId();
        m_jobID = fil.getJobId();    
        m_schedulerId = fil.getScheduler();
        m_returnCode = fil.getReturnCode();
        m_node = fil.getNode();
        m_userId = fil.getUserId();    
        m_schedulerTaskId = fil.getSchedulerTaskId();    
        m_name = fil.getName();    	
    }
    

	public Date getEndedFrom() {
		return m_endedFrom;
	}

	public void setM_endedFrom(Date from) {
		m_endedFrom = from;
	}

	public Date getEndedTo() {
		return m_endedTo;
	}

	public void setEndedTo(Date to) {
		m_endedTo = to;
	}

	public JobDefinitionID getJobDefinitionId() {
		return m_jobDefinitionId;
	}

	public void setJobDefinitionId(JobDefinitionID definitionId) {
		m_jobDefinitionId = definitionId;
	}

	public JobID getJobID() {
		return m_jobID;
	}

	public void setJobID(JobID m_jobid) {
		m_jobID = m_jobid;
	}

	public String getJobStatus() {
		return m_jobStatus;
	}

	public void setJobStatus(String status) {
		m_jobStatus = status;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String m_name) {
		this.m_name = m_name;
	}

	public String getNode() {
		return m_node;
	}

	public void setNode(String m_node) {
		this.m_node = m_node;
	}

	public JobID getParentJobId() {
		return m_parentJobId;
	}

	public void setParentJobId(JobID jobId) {
		m_parentJobId = jobId;
	}

	public Short getReturnCode() {
		return m_returnCode;
	}

	public void setReturnCode(Short code) {
		m_returnCode = code;
	}

	public SchedulerID getSchedulerId() {
		return m_schedulerId;
	}

	public void setSchedulerId(SchedulerID id) {
		m_schedulerId = id;
	}

	public SchedulerTaskID getSchedulerTaskId() {
		return m_schedulerTaskId;
	}

	public void setSchedulerTaskId(SchedulerTaskID schedulerTaskId) {
		m_schedulerTaskId = schedulerTaskId;
	}

	public Date getStartedFrom() {
		return m_startedFrom;
	}

	public void setStartedFrom(Date from) {
		m_startedFrom = from;
	}

	public Date getStartedTo() {
		return m_startedTo;
	}

	public void setStartedTo(Date to) {
		m_startedTo = to;
	}

	public String getUserId() {
		return m_userId;
	}

	public void setUserId(String id) {
		m_userId = id;
	}

	public String getVendorData() {
		return m_vendorData;
	}

	public void setVendorData(String data) {
		m_vendorData = data;
	}
	
	public JobFilter getJobFilter() {
		JobFilter fil = new JobFilter();
		
		fil.setEndedFrom(getEndedFrom());
		fil.setEndedTo(getEndedTo());
		fil.setJobDefinitionId(getJobDefinitionId());
		fil.setJobId(getJobID());
		if (getJobStatus() != null) { // avoid IllegalArgumentException in case JobStatus is not set
			fil.setJobStatus( JobStatus.valueOf(getJobStatus()) );
		}
		fil.setName(getName());
		fil.setNode(getNode());
		fil.setParentId(getParentJobId());
		fil.setReturnCode(getReturnCode());
		fil.setScheduler(getSchedulerId());
		fil.setSchedulerTaskId(getSchedulerTaskId());
		fil.setStartedFrom(getStartedFrom());
		fil.setStartedTo(getStartedTo());
		fil.setUserId(getUserId());
		fil.setVendorData(getVendorData());
		
		return fil;
	}
    
    

}
