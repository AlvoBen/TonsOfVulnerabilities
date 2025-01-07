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

import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobIterator;

/**
 * This class represents a concrete job iterator to be used by the 
 * JXBP Web Service.
 * 
 * @author Thomas Mueller (d040939)
 */
public class JobIteratorWS implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private JobWS[] m_jobsWS = null;
    private String m_stateDescriptor = null;
	
	// default constructor needed for WebService
	public JobIteratorWS() {		
	}
	
	
	public JobIteratorWS(JobIterator iter) {
		if (iter != null) {
			if (iter.getJobs() != null) {
				Job[] jobs = iter.getJobs();
				m_jobsWS = new JobWS[jobs.length];
				
				for (int i = 0; i < jobs.length; i++) {
					m_jobsWS[i] = new JobWS(jobs[i]);
				}
			}
			
			m_stateDescriptor = iter.getStateDescriptor().toString();
		}
		
	}


	public JobWS[] getJobs() {
		return m_jobsWS;
	}

	public void setJobs(JobWS[] jobs) {
		m_jobsWS = jobs;
	}
	
	
	public String getStateDescriptor() {
		return m_stateDescriptor;
	}
	
	public void setStateDescriptor(String desc) {
		m_stateDescriptor = desc;
	}
	
	
	public JobIterator getJobIterator() {
		JobIterator jobIter = null;
		
		jobIter = new JobIterator();
		jobIter.getStateDescriptor().readDescriptor(getStateDescriptor());
		
		JobWS[] jobsWS = getJobs();
		Job[] jobs = new Job[jobsWS.length];
		
		if (jobsWS != null) {
			for (int i = 0; i < jobsWS.length; i++) {
				jobs[i] = jobsWS[i].getJob();
			}
		}
		
		jobIter.setJobs(jobs);    		
		
		return jobIter;
	}
	
}
