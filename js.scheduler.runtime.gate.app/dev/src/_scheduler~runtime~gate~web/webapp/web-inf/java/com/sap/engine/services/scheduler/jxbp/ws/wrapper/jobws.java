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
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.SchedulerID;

/**
 * This class represents a concrete job to be used by the JXBP Web Service.
 * 
 * @author Thomas Mueller (d040939)
 */
public class JobWS implements Serializable {
	
    static final long serialVersionUID = 1L;
    
    private JobID           m_Id;
    private String    	    m_name;
    private JobDefinitionID m_jobDefinitionId;
    private SchedulerID     m_schedulerId;
    private String 			m_jobStatus;
    private Date      		m_startDate;
    private Date      		m_endDate;
    private Date      		m_submitDate;
    private String    		m_node;
    private short     		m_returnCode;
    private String    		m_userId;
    private JobID     		m_parentJobId;
    private String    		m_vendorData;
    private boolean   		m_cancelRequest;
    private int       		m_retentionPeriod;
    private long      		m_CPUTime;
    private long      		m_memoryAllocation;
    private SchedulerTaskID m_schedulerTaskId;
    
    
    // default constructor needed for WebService
    public JobWS() {}
    
    
    public JobWS(Job job) {
        m_Id = job.getId();
        m_name = job.getName();
        m_jobDefinitionId = job.getJobDefinitionId();
        m_schedulerId = job.getScheduler();
        m_jobStatus = job.getJobStatus().toString();
        m_startDate = job.getStartDate();
        m_endDate = job.getEndDate();
        m_submitDate = job.getSubmitDate();
        m_node = job.getNode();
        m_returnCode = job.getReturnCode();
        m_userId = job.getUser();
        m_parentJobId = job.getParent();
        m_vendorData = job.getVendorData();
        m_cancelRequest = job.getCancelRequest();
        m_retentionPeriod = job.getRetentionPeriod();
        m_CPUTime = job.getCPUTime();
        m_memoryAllocation = job.getMemoryAllocation();
        m_schedulerTaskId = job.getSchedulerTaskId();    
    }


	public boolean isCancelRequest() {
		return m_cancelRequest;
	}


	public void setCancelRequest(boolean request) {
		m_cancelRequest = request;
	}


	public long getCPUTime() {
		return m_CPUTime;
	}


	public void setCPUTime(long time) {
		m_CPUTime = time;
	}


	public Date getEndDate() {
		return m_endDate;
	}


	public void setEndDate(Date date) {
		m_endDate = date;
	}


	public JobID getId() {
		return m_Id;
	}


	public void setId(JobID id) {
		m_Id = id;
	}


	public JobDefinitionID getJobDefinitionId() {
		return m_jobDefinitionId;
	}


	public void setJobDefinitionId(JobDefinitionID definitionId) {
		m_jobDefinitionId = definitionId;
	}


	public String getJobStatus() {
		return m_jobStatus;
	}


	public void setJobStatus(String status) {
		m_jobStatus = status;
	}


	public long getMemoryAllocation() {
		return m_memoryAllocation;
	}


	public void setMemoryAllocation(long allocation) {
		m_memoryAllocation = allocation;
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


	public int getRetentionPeriod() {
		return m_retentionPeriod;
	}


	public void setRetentionPeriod(int period) {
		m_retentionPeriod = period;
	}


	public short getReturnCode() {
		return m_returnCode;
	}


	public void setReturnCode(short code) {
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


	public void setSchedulerTaskId(SchedulerTaskID taskId) {
		m_schedulerTaskId = taskId;
	}


	public Date getStartDate() {
		return m_startDate;
	}


	public void setStartDate(Date date) {
		m_startDate = date;
	}


	public Date getSubmitDate() {
		return m_submitDate;
	}


	public void setSubmitDate(Date date) {
		m_submitDate = date;
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
    
    
	public Job getJob() {
		return new Job(getId(), 
					   getJobDefinitionId(), 
					   getSchedulerId(), 
					   getName(), 
					   JobStatus.valueOf(getJobStatus()), 
					   getStartDate(), 
					   getEndDate(), 
					   getSubmitDate(), 
					   getNode(), 
					   getReturnCode(), 
					   getUserId(), 
					   getParentJobId(), 
					   getVendorData(), 
					   isCancelRequest(), 
					   getRetentionPeriod(), 
					   getSchedulerTaskId(), 
					   getCPUTime(), 
					   getMemoryAllocation());
	}
	
	/*
    public String toFormattedString() {
        String LINE_WRAP = System.getProperty("line.separator");
        StringBuilder buf = new StringBuilder();   
        
        buf.append("JobID:            ").append(m_Id.toString());
        buf.append(LINE_WRAP);
        buf.append("Job-name:         ").append(m_name);
        buf.append(LINE_WRAP);
        buf.append("JobDefinitionID:  ").append(m_jobDefinitionId.toString());
        buf.append(LINE_WRAP);
        buf.append("SchedulerID:      ").append(m_schedulerId.toString());
        buf.append(LINE_WRAP);
        buf.append("JobStatus:        ").append(m_jobStatus.toString());
        buf.append(LINE_WRAP);
        if (m_startDate != null)buf.append("StartDate:        ").append(m_startDate.toString()); else buf.append("StartDate:        null");
        buf.append(LINE_WRAP);
        if (m_endDate != null)  buf.append("EndDate:          ").append(m_endDate.toString()); else buf.append("EndDate:          null"); 
        buf.append(LINE_WRAP);
        if (m_submitDate != null) buf.append("SubmitDate:       ").append(m_submitDate.toString()); else buf.append("SubmitDate:       null");
        buf.append(LINE_WRAP);
        buf.append("Node:             ").append(m_node);
        buf.append(LINE_WRAP);
        buf.append("ReturnCode:       ").append(m_returnCode);
        buf.append(LINE_WRAP);
        buf.append("User:             ").append(m_userId);
        buf.append(LINE_WRAP);
        if (m_parentJobId != null) buf.append("ParentJobID:      ").append(m_parentJobId.toString()); else buf.append("ParentJobID:      null");
        buf.append(LINE_WRAP);
        buf.append("VendorData:       ").append(m_vendorData);
        buf.append(LINE_WRAP);
        buf.append("CancelRequest:    ").append(m_cancelRequest);
        buf.append(LINE_WRAP);
        buf.append("RetentionPeriod:  ").append(m_retentionPeriod);
        buf.append(LINE_WRAP);
        buf.append("CPUTime:          ").append(m_CPUTime);
        buf.append(LINE_WRAP);
        buf.append("MemoryAllocation: ").append(m_memoryAllocation);
        buf.append(LINE_WRAP);
        if (m_schedulerTaskId != null) buf.append("SchedulerTaskID:  ").append(m_schedulerTaskId.toString()); else buf.append("SchedulerTaskID:  null");
        buf.append(LINE_WRAP);

        return buf.toString();
    }*/
    
    
}
