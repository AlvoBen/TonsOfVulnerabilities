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

import com.sap.guid.GUID;
import com.sap.guid.GUIDFormatException;
import com.sap.scheduler.api.SchedulerTaskID;

/**
 * This object represents a job. A job is an instance of a job definition
 * and runs once. It is considered a new job if the job is restarted.
 * 
 * @author Dirk Marwinski
 */
public class Job implements Serializable, Comparable<Job>
{
    static final long serialVersionUID = -3729155478737014355L;
    
    private JobID     mId;
    private String    mName;
    private JobDefinitionID    mJobDefinitionId;
    private SchedulerID    mScheduler;
    private JobStatus mJobStatus;
    private Date      mStartDate;
    private Date      mEndDate;
    private Date      mSubmitTime;
    private String    mNode;
    private short     mReturnCode;
    private String    mUserId;
    private JobID     mParent;
    private String    mVendorData;
    private boolean   mCancelRequest;
    private int       mRetentionPeriod;
    private long      mCPUTime;
    private long      mMemoryAllocation;
    private SchedulerTaskID mSchedTaskID;

    
    /**
     * @deprecated Please use {@link #Job(JobID, JobDefinitionID, SchedulerID, String, JobStatus, Date, Date, Date, String, short, String, JobID, String, boolean, int, SchedulerTaskID)} instead
     */
    public Job(JobID id,
               JobDefinitionID defID,
               SchedulerID schedID,
               String name,
               JobStatus status,
               Date startDate,
               Date endDate,
               Date submitTime,
               String node,
               short returnCode,
               String userID,
               JobID parent,
               String vendorData,
               boolean cancelRequest,
               int retentionPeriod) {
    
        mId = id;
        mJobDefinitionId = defID;
        mScheduler = schedID;
        mName = name;
        mJobStatus = status;
        mStartDate = startDate;
        mEndDate = endDate;
        mSubmitTime = submitTime;
        mNode = node;
        mReturnCode = returnCode;
        mUserId = userID;
        mParent = parent;
        mVendorData = vendorData;
        mCancelRequest = cancelRequest;
        mRetentionPeriod = retentionPeriod;
    }
    
    
  /**
   * Create a new job object from the parameters provided.
   * 
   * @param id id of the job
   * @param defID id of the job definition
   * @param schedID id of the scheduler that triggered this job
   * @param name name of this job
   * @param status current status of the job
   * @param startDate date when the job was started
   * @param endDate date when the job ended
   * @param submitTime date when the job was submitted
   * @param node node where this job is running
   * @param returnCode return code of the job
   * @param userID user which was used to run this job
   * @param parent parent job (if any)
   * @param vendorData vendor specific data
   * @param cancelRequest indicates whether there is a cancel request for this job
   * @param retentionPeriod time this record will be kept after the job has ended
   * @param schedTaskID task id from which this job was scheduled
   */    
  public Job(JobID id, JobDefinitionID defID, SchedulerID schedID, String name, JobStatus status, Date startDate, Date endDate, Date submitTime, String node, short returnCode, String userID, JobID parent, String vendorData, boolean cancelRequest, int retentionPeriod, SchedulerTaskID schedTaskID) {
    this (id, defID, schedID, name, status, startDate, endDate, submitTime, node, returnCode, userID, parent, vendorData, cancelRequest, retentionPeriod);
    mSchedTaskID = schedTaskID;
  }

  /**
   * Create a new job object from the parameters provided.
   * 
   * @param id id of the job
   * @param defID id of the job definition
   * @param schedID id of the scheduler that triggered this job
   * @param name name of this job
   * @param status current status of the job
   * @param startDate date when the job was started
   * @param endDate date when the job ended
   * @param submitTime date when the job was submitted
   * @param node node where this job is running
   * @param returnCode return code of the job
   * @param userID user which was used to run this job
   * @param parent parent job (if any)
   * @param vendorData vendor specific data
   * @param cancelRequest indicates whether there is a cancel request for this job
   * @param retentionPeriod time this record will be kept after the job has ended
   * @param schedTaskID task id from which this job was scheduled
   * @param cpuTime CPU time for this job
   * @param memoryAllocation memory allocated by this job
   */    
  public Job(JobID id, JobDefinitionID defID, SchedulerID schedID, String name, JobStatus status, Date startDate, Date endDate, Date submitTime, String node, short returnCode, String userID, JobID parent, String vendorData, boolean cancelRequest, int retentionPeriod, SchedulerTaskID schedTaskID, long cpuTime, long memoryAllocation) {
      this (id, defID, schedID, name, status, startDate, endDate, submitTime, node, returnCode, userID, parent, vendorData, cancelRequest, retentionPeriod, schedTaskID);
      mCPUTime = cpuTime;
      mMemoryAllocation = memoryAllocation;
  }
  
  /**
   * @deprecated Please use {@link #Job(JobID, JobDefinitionID, SchedulerID, String, JobStatus, Date, Date, Date, String, short, String, JobID, String, boolean, int, SchedulerTaskID)} instead
   */
    public Job() {
    }

    /**
     * Get the job id (unique identifier).
     *
     * @return the job id.
     */
    public JobID getId() {
        return mId;
    }

    /**
     * @deprecated use the constructor
     */
    public void setId(JobID id) {
        mId = id;
    }

    /**
     * Get the status.
     *
     * @return the status code.
     */
    public JobStatus getJobStatus() {
        return mJobStatus;
    }

    /**
     * @deprecated use the constructor
     */
    public void setJobStatus(JobStatus jobStatus) {
        mJobStatus = jobStatus;
    }

    /**
     * Get the node which executes or has execute this job
     *
     * @return Node or null if this job has not been executed
     */
    public String getNode() {
        return mNode;
    }

    /**
     * @deprecated use the constructor
     */
    public void setNode(String node) {
        mNode = node;
    }

    /**
     * Get the parent job, or null if it has no parent.
     *
     * @return the parent job.
     */
    public JobID getParent() {
        return mParent;
    }

    /**
     * @deprecated use the constructor
     */
    public void setParent(JobID parent) {
        mParent = parent;
    }

    /**
     * Get the return code of this job.
     *
     * @return the return code.
     */
    public short getReturnCode() {
        return mReturnCode;
    }

    /**
     * @deprecated use the constructor
     */
    public void setReturnCode(short returnCode) {
        mReturnCode = returnCode;
    }

    /**
     * Returns the name of the scheduler which controls this job.
     */
    public SchedulerID getScheduler() {
        return mScheduler;
    }

    /**
     * @deprecated use the constructor
     */
    public void setScheduler(SchedulerID scheduler) {
        mScheduler = scheduler;
    }

    /**
     * Returns the start date of this job.
     *
     * @return the date when this job was started or null if it has not
     * been started
     */
    public Date getStartDate() {
        return mStartDate;
    }

    /**
     * @deprecated use the constructor
     */
    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    /**
     * Returns the date when this job finished
     *
     * @return date when this job is finished of null if it has not finished
     */
    public Date getEndDate() {
        return mEndDate;
    }

    /**
     * @deprecated use the constructor
     */
    public void setEndDate(Date end) {
        mEndDate = end;
    }

    /**
     * date when this job was submitted
     * 
     * @return a Date object representing the time when this job was submitted
     */
    public Date getSubmitDate() {
        return mSubmitTime;
    }    
    
    /**
     * @deprecated use the constructor
     */
    public void setSubmitDate(Date date) {
        mSubmitTime = date;
    }
    
    /**
     * User id which is used to run this job
     *
     * @return user id
     */
    public String getUser() {
        return mUserId;
    }

    /**
     * @deprecated use the constructor
     */
    public void setUser(String userId) {
        mUserId = userId;
    }

    public JobDefinitionID getJobDefinitionId() {
        return mJobDefinitionId;
    }

    /**
     * @deprecated use the constructor
     */
    public void setJobDefinitionId(JobDefinitionID id) {
        mJobDefinitionId = id;
    }
    
    /**
     * Get the name of the job definition.
     *
     * @return the job name.
     */
    public String getName() {
        return mName;
    }
    
    /**
     * @deprecated use the constructor
     */
    public void setName(String name) {
        mName = name;
    }

    public String getVendorData() {
        return mVendorData;
    }
        
    /**
     * @deprecated use the constructor
     */
    public void setVendorData(String value) {
        mVendorData = value;
    }
    
    /**
     * @deprecated use the constructor
     */
    public void setCancelRequest(boolean value) {
        mCancelRequest = value;
    }
    
    public boolean getCancelRequest() {
        return mCancelRequest;
    }
    
    public int getRetentionPeriod() {
        return mRetentionPeriod;
    }
    
    public SchedulerTaskID getSchedulerTaskId() {
      return mSchedTaskID;
    }
    
    /**
     * Returns the CPU time required to run this job. It is 0 if the
     * job has not finished.
     *  
     * @return CPU time in microseconds
     */
    public long getCPUTime() {
        return mCPUTime;
    }
    
    /**
     * Returns the overall memory allocated by this job. Memory freed by
     * the garbage collector is not considered.
     * 
     * @return memory allocated by the job in bytes
     */
    public long getMemoryAllocation() {
        return mMemoryAllocation;
    }
    
    public String toFormattedString() {
        String LINE_WRAP = System.getProperty("line.separator");
        StringBuilder buf = new StringBuilder();   
        
        buf.append("JobID:            ").append(mId.toString());
        buf.append(LINE_WRAP);
        buf.append("Job-name:         ").append(mName);
        buf.append(LINE_WRAP);
        buf.append("JobDefinitionID:  ").append(mJobDefinitionId.toString());
        buf.append(LINE_WRAP);
        buf.append("SchedulerID:      ").append(mScheduler.toString());
        buf.append(LINE_WRAP);
        buf.append("JobStatus:        ").append(mJobStatus.toString());
        buf.append(LINE_WRAP);
        if (mStartDate != null)buf.append("StartDate:        ").append(mStartDate.toString()); else buf.append("StartDate:        null");
        buf.append(LINE_WRAP);
        if (mEndDate != null)  buf.append("EndDate:          ").append(mEndDate.toString()); else buf.append("EndDate:          null"); 
        buf.append(LINE_WRAP);
        if (mSubmitTime != null) buf.append("SubmitDate:       ").append(mSubmitTime.toString()); else buf.append("SubmitDate:       null");
        buf.append(LINE_WRAP);
        buf.append("Node:             ").append(mNode);
        buf.append(LINE_WRAP);
        buf.append("ReturnCode:       ").append(mReturnCode);
        buf.append(LINE_WRAP);
        buf.append("User:             ").append(mUserId);
        buf.append(LINE_WRAP);
        if (mParent != null) buf.append("ParentJobID:      ").append(mParent.toString()); else buf.append("ParentJobID:      null");
        buf.append(LINE_WRAP);
        buf.append("VendorData:       ").append(mVendorData);
        buf.append(LINE_WRAP);
        buf.append("CancelRequest:    ").append(mCancelRequest);
        buf.append(LINE_WRAP);
        buf.append("RetentionPeriod:  ").append(mRetentionPeriod);
        buf.append(LINE_WRAP);
        buf.append("CPUTime:          ").append(mCPUTime);
        buf.append(LINE_WRAP);
        buf.append("MemoryAllocation: ").append(mMemoryAllocation);
        buf.append(LINE_WRAP);
        if (mSchedTaskID != null) buf.append("SchedulerTaskID:  ").append(mSchedTaskID.toString()); else buf.append("SchedulerTaskID:  null");
        buf.append(LINE_WRAP);

        return buf.toString();
    }


	
	/**
	 * Compare the current job with the job <code>otherJob</code><br/>
	 * The comparison is doing first by end date(asc), then by id (ascending)
	 */
    @Override
	public int compareTo(Job otherJob) {
		int res = compareEndDates(this, otherJob);
		
		if (res != 0) {
			return res;
		}
        // equal - > compare by id        
        return compareIds(this, otherJob);
	}


	/**
	 * Compares id of 2 jobs according to parameter <code>asc</code>
	 * @param j1 First Job
	 * @param j2 Second Job
	 * @param asc true for ascending, false for descending order (the order is applied only on Job IDs)
	 * @return
	 */
	protected static int compareIds(Job j1, Job j2) {
		if (j1.getId() == null || j2.getId() == null) {	        	
        	throw new IllegalArgumentException("JobsComparator: Comparison of JOBs by IDs but one or both of them is null. JobID 1:" + j1.getId() + ", JobID 2:" + j2.getId());
        } 
        
        GUID guid1 = null; 
        GUID guid2 = null;

        try {
			guid1 = new GUID(j1.getId().getBytes());
			guid2 = new GUID(j2.getId().getBytes());
		} catch (GUIDFormatException e) {
			throw new RuntimeException("JobsComparator: Problem in comparing of ID of 2 jobs!! Creating of GUID(" + j1.getId() + " or " + j2.getId() + ") threw an expection:", e);
		}
		
		return guid1.compareTo(guid2); 
	}


	/**
	 * Compares end dates of 2 jobs
	 * @param j1 First Job
	 * @param j2 Second Job
	 */
	protected static int compareEndDates(Job j1, Job j2) {
		// special case in case one or both values are null (not set)	
        if (j1.getEndDate() == null && j2.getEndDate() != null) {
            return 1;
        }
        if (j1.getEndDate() != null && j2.getEndDate() == null) {
            return -1;
        }
        if (j1.getEndDate() == null && j2.getEndDate() == null) {
            return 0;
        }

        return j1.getEndDate().compareTo(j2.getEndDate());        
	}
}                                                                               
