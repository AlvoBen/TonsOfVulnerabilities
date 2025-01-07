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
package com.sap.scheduler.runtime;

import com.sap.scheduler.runtime.Job;

import java.io.Serializable;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Iterator object used to retrieve a possibly large number of job
 * object from a query.
 * 
 * @author Dirk Marwinski
 *
 */
public class JobIterator implements Serializable {

    static final long serialVersionUID = 8205937876536634496L;
    
    public class StateDescriptor implements Serializable {
        
        public static final String NO_MORE_CHUNKS = "none";
        
        public static final int TABLE_JOBS     = 0;
        public static final int TABLE_JOBS_CO  = 1;
        public static final int TABLE_JOBS_ALL = 2;
    
        static final long serialVersionUID = 3699222900222696665L;
        
        private int table;
        private JobID id;
        private boolean moreChunks;
        private Date endDate;
        
        public StateDescriptor() {
            this.table = TABLE_JOBS_ALL;
            this.id = null;
            this.moreChunks = true;
        }

        public void readDescriptor(String strDesc) throws IllegalArgumentException {
        	String ERROR_MSG = "Illegal state descriptor \"" + strDesc + "\" provided.";
            
        	if (strDesc == null) {
        		throw new IllegalArgumentException(ERROR_MSG);
        	}
        	
        	strDesc = strDesc.trim();
        	
            this.table = TABLE_JOBS_ALL;
            this.id = null;
            this.moreChunks = true;
            
            if (NO_MORE_CHUNKS.equals(strDesc)) {
                this.moreChunks = false;
                return;
            }
            
            StringTokenizer st = new StringTokenizer(strDesc, ":");
            
            // we can have the formats
            // 1.) <tableName>:<jobId>:<timeStamp>
            // 2.) <tableName>:<jobId>: 			(in case we have a job in state STARTING where the iterator ends)
            // 3.) <tableName>:: 			        (in case we select only with a JobStatus --> only the info is mapped for the corresponding table) 
            
            // table
            if (st.hasMoreTokens()) { 
            	String tbl = st.nextToken();
                try {
                    this.table = Integer.parseInt(tbl);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException(ERROR_MSG);
                }
            } else {
            	throw new IllegalArgumentException(ERROR_MSG);
            }
            
            // jobId
            if (st.hasMoreTokens()) { 
                String jobId = st.nextToken();
                this.id = JobID.parseID(jobId);
            } else {
            	// valid: no jobId and no endDate
            	return;
            }
            
            // endDate
            if (st.hasMoreTokens()) { 
            	String date = st.nextToken();
                try {
                    this.endDate = new Date(Long.parseLong(date));
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException(ERROR_MSG);
                }
            }            
        }
        
        public int getTable() {
            return this.table;
        }
        public void setTable(int table) {
            this.table = table;
        }
        
        public JobID getJobId() {
            return this.id;
        }

        public void setJobId(JobID id) {
            this.id = id;
        }
        
        public Date getEndDate() {
            return this.endDate;
        }

        public void setEndDate(Date date) {
            this.endDate = date;
        }
        
        public String toString() {
            if (this.moreChunks) {
                return table + ":" + (id == null ? "" : id.toString())+ ":" + (endDate == null ? "" : endDate.getTime());
            } else {
                return NO_MORE_CHUNKS;
            }
        }
        
        public void setMoreChunks(boolean val) {
            this.moreChunks = val;
        }
        
        public boolean hasMoreChunks() {
            return this.moreChunks;
        }
    }

    private StateDescriptor state = null;

    private Job[] jobs;
    
    public JobIterator() {
        state = new StateDescriptor();
    }    

    /**
     * This method returns true if there is more data available.
     * 
     * @return true if more data is available, false otherwise
     */
    public boolean hasMoreChunks()
    {
        return state.hasMoreChunks();
    }

    /**
     * This method returns the next chunk of job objects. This method can only
     * be invoked once on the job iterator.
     * 
     * @return the next chunk of data
     */
    public Job[] nextChunk() {
        
        if (jobs == null) {
            throw new NoSuchElementException("No more elements");
        }
        Job[] js = this.jobs;
        this.jobs = null;
        return js;
    }
    
    /**
     * Method to be used by the scheduler runtime only!
     */
    public void setJobs(Job[] jobs) {
        this.jobs = jobs;
    }
    
    /**
     * Method to be used by the scheduler runtime only!
     */
    public Job[] getJobs() {
        return jobs;
    }
    
    /**
     * Method to be used by the scheduler runtime only!
     */
    public StateDescriptor getStateDescriptor() {
        return this.state;
    }
}
