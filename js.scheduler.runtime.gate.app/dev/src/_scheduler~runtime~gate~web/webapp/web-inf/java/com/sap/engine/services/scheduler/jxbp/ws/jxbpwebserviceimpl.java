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
package com.sap.engine.services.scheduler.jxbp.ws;

import java.sql.SQLException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.services.scheduler.jxbp.ws.wrapper.EventWS;
import com.sap.engine.services.scheduler.jxbp.ws.wrapper.JobDefinitionWS;
import com.sap.engine.services.scheduler.jxbp.ws.wrapper.JobFilterWS;
import com.sap.engine.services.scheduler.jxbp.ws.wrapper.JobIteratorWS;
import com.sap.engine.services.scheduler.jxbp.ws.wrapper.JobParameterWS;
import com.sap.engine.services.scheduler.jxbp.ws.wrapper.JobWS;
import com.sap.engine.services.scheduler.jxbp.ws.wrapper.LogIteratorWS;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.EventSubscriber;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.scheduler.runtime.JobExecutorException;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIllegalStateException;
import com.sap.scheduler.runtime.JobIterator;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobDefinitionException;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.ParameterValidationException;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.spi.JXBP;
import com.sap.scheduler.spi.JXBPException;
import com.sap.security.api.IUser;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * @author Thomas Mueller (d040939)
 */
@WebService(serviceName="JXBPWebService", name="JXBPWS", targetNamespace="http://sap.com/engine/services/scheduler/jxbp/ws/", portName="JXBPWebServicePort")
public class JXBPWebServiceImpl {
		
	/**
	 * Initialization of the location for SAP logging.
	 */
	private final static Location m_location = Location.getLocation(JXBPWebServiceImpl.class); 

	/**
	 * Initialization of the category for SAP logging.
	 */
	private final static Category m_category = Category.SYS_SERVER;

	/**
	 * @todo get the internal name from the runtime
	 */
    public static final String SAP_SCHEDULER_NAME = SchedulerDefinition.SAP_SCHEDULER_NAME;
    
    private JobExecutionRuntime m_jert;
           

    /**
     * @see JXBPInternal#getSystemTimeZone()
     */
    @WebMethod(exclude=false, operationName="getSystemTimeZone")
	public String getSystemTimeZone() throws JXBPException {
    	String method = "JXBPWebServiceImpl.getSystemTimeZone()";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed"); }
    	
        // authorizes and authenticates current scheduler
        getScheduler();
        
        return m_jert.getSystemTimeZone();
    }


	/**
	 * @see JXBPInternal#getJobDefinitions()
	 */
	@WebMethod(exclude=false, operationName="getJobDefinitions")
	public JobDefinitionWS[] getJobDefinitions() throws JXBPException {
    	String method = "JXBPWebServiceImpl.getJobDefinitions()";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed"); }
        
		// authorizes and authenticates current scheduler
		getScheduler();

		try {
			JobDefinition[] jobDefs = m_jert.getJobDefinitions();
			JobDefinitionWS[] jobDefsWS = new JobDefinitionWS[jobDefs.length];
			for (int i = 0; i < jobDefs.length; i++) {
				jobDefsWS[i] = new JobDefinitionWS(jobDefs[i]);
			}
            return jobDefsWS;   
        } catch (SQLException sq) {
            throw new JXBPException("Technical problem retrieving job definitions.",sq);
        }        
	}

	/**
	 * @see JXBPInternal#getJobDefinitionByName(java.lang.String)
	 */
	@WebMethod(exclude=false, operationName="getJobDefinitionByName")
	public JobDefinitionWS getJobDefinitionByName(@WebParam(name="jobName") String jobName) throws JXBPException {
    	String method = "JXBPWebServiceImpl.getJobDefinitionByName(String jobName)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with jobName '"+jobName+"'"); }

		// authorizes and authenticates current scheduler
		getScheduler();
 
        try {
            return new JobDefinitionWS(m_jert.getJobDefinitionByName(jobName));
        } catch (SQLException sql) {
            throw new JXBPException("Error getting job definition from database.", sql);
        }        		
	}

    /**
     * @see JXBPInternal#getJobDefinitionById(JobDefinitionID)
     */
    @WebMethod(exclude=false, operationName="getJobDefinitionById")
	public JobDefinitionWS getJobDefinitionById(@WebParam(name="id") JobDefinitionID id) throws JXBPException {
    	String method = "JXBPWebServiceImpl.getJobDefinitionById(JobDefinitionID id)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobDefinitionID '"+id.toString()+"'"); }

		// authorizes and authenticates current scheduler
    	getScheduler();

        try {
            return new JobDefinitionWS(m_jert.getJobDefinitionById(id));
        } catch (SQLException sql) {
            throw new JXBPException("Error getting job definition from database.", sql);
        }               
        
        
    }

	//------------------------------------------------------------------
	// Starting and stopping jobs
	//------------------------------------------------------------------

    /**
     */
    @WebMethod(exclude=false, operationName="executeJob")
    @RequestWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.ExecuteJob")
    @ResponseWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.ExecuteJobResponse")
	public JobID executeJob(@WebParam(name="jobDefId")JobDefinitionID jobDefId, 
            				@WebParam(name="jobParametersWS")JobParameterWS[] jobParametersWS,
            				@WebParam(name="retentionPeriod")Integer retentionPeriod) 
                       throws ParameterValidationException,
                              NoSuchJobDefinitionException,
                              JXBPException { 
    	String method = "JXBPWebServiceImpl.executeJob(JobDefinitionID jobDefId, JobParameterWS[] jobParametersWS, Integer retentionPeriod)";
    	if (m_location.bePath()) { 
    		m_location.pathT(method+" accessed with JobDefinitionID '"+jobDefId.toString()+
    						 "', JobParameterWS[] length '"+jobParametersWS.length+
    						 "', retentionPeriod '"+retentionPeriod+"'"); 
        }
    	
        return executeJob(jobDefId, jobParametersWS, retentionPeriod, null);
    }

	/**
	 * @see JXBPInternal#executeJob()
	 */
    @WebMethod(exclude=false, operationName="executeJob1")
    @RequestWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.ExecuteJob1")
    @ResponseWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.ExecuteJob1Response")
	public JobID executeJob(@WebParam(name="jobDefId") JobDefinitionID jobDefId, 
                            @WebParam(name="jobParametersWS") JobParameterWS[] jobParametersWS,
                            @WebParam(name="retentionPeriod") Integer retentionPeriod,
                            @WebParam(name="vendorData") String vendorData) 
                                       throws ParameterValidationException,
                                              NoSuchJobDefinitionException,
                                              JXBPException { 
    	String method = "JXBPWebServiceImpl.executeJob(JobDefinitionID jobDefId, JobParameterWS[] jobParametersWS, Integer retentionPeriod, String vendorData)";
    	if (m_location.bePath()) { 
    		m_location.pathT(method+" accessed with JobDefinitionID '"+jobDefId.toString()+
    						 "', JobParameterWS[] length '"+jobParametersWS.length+
    						 "', retentionPeriod '"+retentionPeriod+
    						 "', vendorData '"+vendorData+"'"); 
        }
        
        if (vendorData != null && vendorData.length() > JXBP.VENDOR_STRING_MAX_LENGTH) {
            throw new JXBPException("Vendor data string it too long. The maximum length is " + JXBP.VENDOR_STRING_MAX_LENGTH + ".");
        }

        SchedulerDefinition def = getScheduler();
        
        // OSS message 221142 2009 (Dow Chemical): NullPointerException when
        // a job is triggered which does not have any arguments
        //
        int length = jobParametersWS == null ? 0 : jobParametersWS.length; 
        JobParameter[] jobParameters = new JobParameter[length];
        for (int i=0; i < length; i++) {
            jobParameters[i] = jobParametersWS[i].getJobParameter();
        }

        try {
            return m_jert.executeJobJXBP(jobDefId, jobParameters, retentionPeriod, null, def.getId(), vendorData);
        } catch (JobExecutorException jee) {
            throw new JXBPException(jee.getMessage(), jee);
        }
    }

	/**
	 * @see JXBPInternal#cancelJob(JobID)
	 */
	@WebMethod(exclude=false, operationName="cancelJob")
	public void cancelJob(@WebParam(name="jobid") JobID jobid) throws JXBPException,
                                  									  JobIllegalStateException,
                                  									  NoSuchJobException {
    	String method = "JXBPWebServiceImpl.cancelJob(JobID jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobid.toString()+"'"); }

		// authorizes and authenticates current scheduler
		getScheduler();

		try {
            m_jert.cancelJob(jobid);
        } catch (SQLException sql) {
            throw new JXBPException("Error cancelling job \"" + jobid.toString() + "\". Job not cancelled.", sql);
        }
	}
    
    /**
     * @see JXBPInternal#holdJob(JobID)
     */
    @WebMethod(exclude=false, operationName="holdJob")
	public void holdJob(@WebParam(name="jobid")	JobID jobid) throws JobIllegalStateException,
                     												NoSuchJobException,
                     												JXBPException {
    	String method = "JXBPWebServiceImpl.holdJob(JobID jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobid.toString()+"'"); }
    	
    	throw new UnsupportedOperationException();
    }

    /**
     * @see JXBPInternal#releaseJob(JobID)
     */
    @WebMethod(exclude=false, operationName="releaseJob")
	public void releaseJob(@WebParam(name="jobid") JobID jobid) throws JobIllegalStateException,
                                 									   NoSuchJobException,
                                 									   JXBPException {
    	String method = "JXBPWebServiceImpl.releaseJob(JobID jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobid.toString()+"'"); }
    	
        throw new UnsupportedOperationException();
    }

	//------------------------------------------------------------------
	// Parent/Child Functionality
	//------------------------------------------------------------------

	/**
	 * @see JXBPInternal#getChildJobs(JobID)
	 */
	@WebMethod(exclude=false, operationName="getChildJobs")
	public JobWS[] getChildJobs(@WebParam(name="jobid")	JobID jobid) throws NoSuchJobException,
                                              								JXBPException {
    	String method = "JXBPWebServiceImpl.getChildJobs(JobID jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobid.toString()+"'"); }

		// authorizes and authenticates current scheduler
		getScheduler();

        try {
        	Job[] jobs = m_jert.getChildJobs(jobid);
        	JobWS[] jobsWS = new JobWS[jobs.length];
        	for (int i = 0; i < jobs.length; i++) {
        		jobsWS[i] = new JobWS(jobs[i]);
			}
            
        	return jobsWS;
            
        } catch (SQLException sql) {
            throw new JXBPException("Databse error retrieving child jobs.", sql);
        }
	}

	/**
     * @see JXBPInternal#hasChildJobs(JobID)
	 */
	@WebMethod(exclude=false, operationName="hasChildJobs") 
	public boolean hasChildJobs(@WebParam(name="jobid") JobID jobid) throws NoSuchJobException,
																			JXBPException {
    	String method = "JXBPWebServiceImpl.hasChildJobs(JobID jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobid.toString()+"'"); }

		// authorizes and authenticates current scheduler
		getScheduler();

        try {
            return m_jert.hasChildJobs(jobid);
        } catch (SQLException sql) {
            throw new JXBPException("Error checking for child jobs.", sql);
        }
    }

    /**
     * @see JXBPInternal#haveChildJobs(JobID[])
     */
    @WebMethod(exclude=false, operationName="haveChildJobs")
	public boolean[] haveChildJobs(@WebParam(name="jobids") JobID[] jobids) throws JXBPException {
    	String method = "JXBPWebServiceImpl.haveChildJobs(JobID[] jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID[] length '"+jobids.length+"'"); }

    	// authorizes and authenticates current scheduler
    	getScheduler();

        try {
            return m_jert.haveChildJobs(jobids);
        } catch (SQLException sql) {
            throw new JXBPException("Error checking for child jobs.", sql);
        }
    }

	//------------------------------------------------------------------
	// Maintaining runtime job information
	//------------------------------------------------------------------

	/**
	 * @see JXBPInternal#removeJob(JobID)
	 */
	@WebMethod(exclude=false, operationName="removeJob")
	public void removeJob(@WebParam(name="jobid") JobID jobid) throws NoSuchJobException,
                                          							  JobIllegalStateException,
                                          							  JXBPException {
    	String method = "JXBPWebServiceImpl.removeJob(JobID jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobid.toString()+"'"); }

		// authorizes and authenticates current scheduler
		getScheduler();
		
        try {
            m_jert.removeJob(jobid);
        } catch (SQLException sql) {
        	m_location.traceThrowableT(Severity.ERROR, "removeJob", sql);
            throw new JXBPException("Error removing job information for job \"" + jobid + "\" from database.", sql);
        }
	}
    
    /**
     * This method removes all information the given job instances fron the J2EE
     * Engine (including logs). This is a convenience method
     */
    @WebMethod(exclude=false, operationName="removeJobs")
	public void removeJobs(@WebParam(name="jobids") JobID[] jobids) throws JXBPException {
    	String method = "JXBPWebServiceImpl.removeJobs(JobID[] jobids)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID[] length '"+jobids.length+"'"); }

    	// authorizes and authenticates current scheduler
    	getScheduler();

        try {
            m_jert.removeJobs(jobids);
        } catch (SQLException sql) {
        	m_location.traceThrowableT(Severity.ERROR, "removeJobs", sql);
            throw new JXBPException("Error removing job information for jobs from database.", sql);
        }        
    }

	/**
     * @see JXBP#getJob(JobID)
	 */
	@WebMethod(exclude=false, operationName="getJob")
	public JobWS getJob(@WebParam(name="jobid") JobID jobid) throws JXBPException {
    	String method = "JXBPWebServiceImpl.getJob(JobID jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobid.toString()+"'"); }
		
		// authorizes and authenticates current scheduler
		getScheduler();

		try {
			Job job = m_jert.getJob(jobid);
            if (job != null) {
            	return new JobWS(job);	
            }
            
            return null;			
            
        } catch (SQLException sql) {
        	m_location.traceThrowableT(Severity.ERROR, "getJob", sql);
            throw new JXBPException("Error getting job information from database.", sql);
        }
	}

    /**
     * @see JXBP#getJobParameters(JobID)
     */
    @WebMethod(exclude=false, operationName="getJobParameters")
	public JobParameterWS[] getJobParameters(@WebParam(name="jobid") JobID jobid) throws JXBPException {
    	String method = "JXBPWebServiceImpl.getJobParameters(JobID jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobid.toString()+"'"); }

    	// authorizes and authenticates current scheduler
    	getScheduler();

    	try {
            JobParameter[] params = m_jert.getJobParameters(jobid);
            JobParameterWS[] paramsWS = new JobParameterWS[params.length];
            for (int i=0; i< params.length;i++) {
                paramsWS[i] = JobParameterWS.getJobParameterWS(params[i]);
            }
            return paramsWS;
        } catch (Exception e) {
        	m_location.traceThrowableT(Severity.ERROR, "getJobParameters", e);

            throw new JXBPException("Error getting job information from database.", e);
        } 
       
    }
    
	/**
	 * @see JXBPInternal#getJobs(JobID[])
	 */
	@WebMethod(exclude=false, operationName="getJobs")
  @RequestWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.GetJobs")
  @ResponseWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.GetJobsResponse")
	public JobWS[] getJobs(@WebParam(name="jobids") JobID[] jobids) throws JXBPException {
    	String method = "JXBPWebServiceImpl.getJobs(JobID[] jobids)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID[] legth '"+jobids.length+"'"); }
        
		// authorizes and authenticates current scheduler
		getScheduler();

		try {
			Job[] jobs = m_jert.getJobs(jobids);
        	JobWS[] jobsWS = new JobWS[jobs.length];
        	for (int i = 0; i < jobs.length; i++) {
        		if (jobs[i] != null) {
        			jobsWS[i] = new JobWS(jobs[i]);
        		} else {
        			jobsWS[i] = null;
        		}
			}
            
        	return jobsWS;
			
        } catch (SQLException sql) {
            throw new JXBPException("Error getting job information from database.", sql);
        }
	}
    
    @WebMethod(exclude=false, operationName="getJobs1")
    @RequestWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.GetJobs1")
    @ResponseWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.GetJobs1Response")
	public JobIteratorWS getJobs(@WebParam(name="filterWS") JobFilterWS filterWS, 
								 @WebParam(name="iterWS") JobIteratorWS iterWS, 
								 @WebParam(name="fetchSize") int fetchSize)
                            throws JXBPException {
    	String method = "JXBPWebServiceImpl.getJobs(JobFilterWS filterWS, JobIteratorWS iterWS, int fetchSize)";
    	if (m_location.bePath()) { 
    		String stateDesc = null;
    		if (iterWS != null) {
    			stateDesc = iterWS.getStateDescriptor();
    		}
    		m_location.pathT(method+" accessed with JobFilterWS '"+filterWS.getJobFilter().toString()+
    						 "', iterWS '"+stateDesc+
    						 "', fetchSize '"+fetchSize+"'"); 
        }
        
		// authorizes and authenticates current scheduler
		getScheduler();

		if (fetchSize > JXBP.MAX_FETCH_SIZE) {
            fetchSize = JXBP.MAX_FETCH_SIZE;
        }
        
        try {
        	JobIterator jobIter = null;
        	
        	if (iterWS != null) {
        		jobIter = iterWS.getJobIterator();
        	}
        	
        	jobIter = m_jert.getJobs(filterWS.getJobFilter(), jobIter, fetchSize);        	
            
        	return new JobIteratorWS(jobIter);
            
        } catch (SQLException sql) {
            throw new JXBPException("Error getting job records from database.", sql);
        }
    }

	/**
	 * @see JXBP#getJobLog(JobID, com.sap.scheduler.runtime.LogIterator, int)
	 */
	@WebMethod(exclude=false, operationName="getJobLog")
	public LogIteratorWS getJobLog(@WebParam(name="jobid") JobID jobid, 
								   @WebParam(name="it") LogIteratorWS it, 
								   @WebParam(name="fetchSize")int fetchSize) 
                               throws NoSuchJobException, JXBPException {
    	String method = "JXBPWebServiceImpl.getJobLog(JobID jobid, LogIteratorWS it, int fetchSize)";
    	if (m_location.bePath()) { 
    		long logPos = 0;
    		if (it != null) {
    			logPos = it.getPos();
    		}
    		m_location.pathT(method+" accessed with JobID '"+jobid.toString()+
    						 "', iterWS position '"+logPos+
    						 "', fetchSize '"+fetchSize+"'"); 
        }

		// authorizes and authenticates current scheduler
		getScheduler();

        if (fetchSize > JXBP.MAX_FETCH_SIZE) {
            fetchSize = JXBP.MAX_FETCH_SIZE;
        } else if (fetchSize <= 0) {
            throw new JXBPException("Fetch size must be greater than 0");
        }

        try {
        	LogIterator iterator = null;
        	if (it != null) {
        		iterator = new LogIterator();
        		iterator.setLog(it.getNextChunk());
        		iterator.setPos(it.getPos());
        	}
        	
            LogIterator iter = m_jert.getJobLog(jobid, iterator, fetchSize);
            
            LogIteratorWS iterWS = new LogIteratorWS();
            iterWS.setNextChunk(iter.nextChunk());
            iterWS.setPos(iter.getPos());
            
            return iterWS;
            
        } catch (SQLException se) {
            throw new JXBPException("Database error retrieving logfile.",se);
        }
	}
        
	/**
	 * @see JXBPInternal#removeJobLog(JobID)
	 */
	@WebMethod(exclude=false, operationName="removeJobLog")
	public void removeJobLog(@WebParam(name="jobid") JobID jobid) throws NoSuchJobException,
                                                 						 JobIllegalStateException,
                                                 						 JXBPException {
    	String method = "JXBPWebServiceImpl.removeJobLog(JobID jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobid.toString()+"'"); }

		// authorizes and authenticates current scheduler
		getScheduler();

        try {
            m_jert.removeJobLog(jobid);
        } catch (SQLException sq) {
            throw new JXBPException("Database error deleting logfile.",sq);
        }
	}

    /**
     * @see JXBPInternal#getJobStatus(JobID)
     */
	@WebMethod(exclude=false, operationName="getJobStatus")
	public String getJobStatus(@WebParam(name="jobid") JobID jobid) throws NoSuchJobException,
                                               							   JXBPException {
    	String method = "JXBPWebServiceImpl.getJobStaus(JobID jobid)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobid.toString()+"'"); }
		
		// authorizes and authenticates current scheduler
		getScheduler();

		try {
            return m_jert.getJobStatus(jobid).toString();
        } catch (SQLException sql) {
            throw new JXBPException("Unable to get job status.", sql);
        }
	}

    /**
     * @see JXBPInternal#getJobsByStatus(JobStatus, JobIterator, int)
     */
	@WebMethod(exclude=false, operationName="getJobsByStatus")
	public JobIteratorWS getJobsByStatus(@WebParam(name="s") String s, 
										 @WebParam(name="iterWS") JobIteratorWS iterWS, 
										 @WebParam(name="fetchSize") int fetchSize) 
                                     throws JXBPException {
    	String method = "JXBPWebServiceImpl.getJobsByStatus(String s, JobIteratorWS iterWS, int fetchSize)";
    	if (m_location.bePath()) { 
    		String stateDesc = null;
    		if (iterWS != null) {
    			stateDesc = iterWS.getStateDescriptor();
    		}
    		m_location.pathT(method+" accessed with status '"+s+
    						 "', iterWS '"+stateDesc+
    						 "', fetchSize '"+fetchSize+"'"); 
        }

		// authorizes and authenticates current scheduler
		getScheduler();

        if (iterWS == null) {
            throw new NullPointerException("JobIteratorWS object must not be null");
        }
        
        JobIterator jobIter = null;
        
        if (iterWS != null) {
        	jobIter = iterWS.getJobIterator();	
        	
            if (!jobIter.hasMoreChunks()) {
                // no more jobs, this is a client error
                return iterWS;
            }
        }
                
        if (fetchSize > JXBP.MAX_FETCH_SIZE) {
            fetchSize = JXBP.MAX_FETCH_SIZE;
        } else if (fetchSize <= 0) {
            throw new JXBPException("Fetch size must be greater than 0");
        }
		
        try {
            JobFilter filter = new JobFilter();
            filter.setJobStatus(JobStatus.valueOf(s));
            
            jobIter = m_jert.getJobs(filter, jobIter, fetchSize);

            return new JobIteratorWS(jobIter);
            
        } catch (SQLException sql) {
            throw new JXBPException("Error getting job information from database.", sql);
        }
	}
        
    

	//------------------------------------------------------------------
	// Events
	//------------------------------------------------------------------

    /**
     * @see JXBP#getUnhandledEvents(int)
     */
    @WebMethod(exclude=false, operationName="getUnhandledEvents")
	public EventWS[] getUnhandledEvents(@WebParam(name="fetchSize") int fetchSize) throws JXBPException {
    	String method = "JXBPWebServiceImpl.getUnhandledEvents(int fetchSize)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with fetchSize '"+fetchSize+"'"); }
    	
        if (fetchSize > JXBP.MAX_FETCH_SIZE) {
            fetchSize = JXBP.MAX_FETCH_SIZE;
        } else if (fetchSize <= 0) {
            throw new JXBPException("fetchSize variable must be greater than 0");
        }
        
    	EventSubscriber ev = getEventSubscriber();
    	
    	Event[] events = m_jert.getEvents(ev, fetchSize);
    	EventWS[] eventsWS = new EventWS[events.length];
    	
    	for (int i = 0; i < events.length; i++) {
			eventsWS[i] = new EventWS(events[i]);
		}
    	
        return eventsWS;
    }
    
    /**
     * @see JXBP#clearEvents()
     */
    @WebMethod(exclude=false, operationName="clearEvents")
	public void clearEvents() throws JXBPException {
    	String method = "JXBPWebServiceImpl.clearEvents()";
    	if (m_location.bePath()) { m_location.pathT(method); }
    	
        m_jert.clearEvents(getEventSubscriber());
    }
    
    /**
     * @see JXBPInternal#setFilter(java.lang.String[])
     */
    @WebMethod(exclude=false, operationName="setFilter")
	public void setFilter(@WebParam(name="eventType") String[] eventType) throws JXBPException {
    	String method = "JXBPWebServiceImpl.setFilter(String[] eventType)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with String[] length '"+eventType.length+"'"); }
        
        // event type must not be null
        //
        if (eventType == null) {
            throw new NullPointerException("Event type must not be null.");
        }
        
        SchedulerDefinition def = getScheduler();

        try {
            m_jert.setFilter(def, eventType);
        } catch (SQLException sql) {
            throw new JXBPException("Unable to set filter due to technical exception.", sql);
        }
    }

    /**
     * @see JXBP#getJXBPRuntimeEventTypes()
     */
    @WebMethod(exclude=false, operationName="getJXBPRuntimeEventTypes")
	public String[] getJXBPRuntimeEventTypes() {
    	String method = "JXBPWebServiceImpl.getJXBPRuntimeEventTypes()";
    	if (m_location.bePath()) { m_location.pathT(method); }
    	
        return Event.JXBP_RUNTIME_EVENT_TYPES;
    }

    /**
     * @see JXBPInternal#setVendorData(JobID[], java.lang.String)
     */
    @WebMethod(exclude=false, operationName="setVendorData")
    @RequestWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.SetVendorData")
    @ResponseWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.SetVendorDataResponse")
	public void setVendorData(@WebParam(name="jobIds") JobID[] jobIds, 
							  @WebParam(name="data") String data) 
                          throws JXBPException {
    	String method = "JXBPWebServiceImpl.setVendorData(JobID[] jobIds, String data)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID[] length '"+jobIds.length+
    							  "', data '"+data+"'"); 
    	}

        if (data.length() > JXBP.VENDOR_STRING_MAX_LENGTH) {
            throw new JXBPException("Vendor data string is too long. The maximum length is " + JXBP.VENDOR_STRING_MAX_LENGTH + ".");
        }
        
        SchedulerDefinition def = getScheduler();
        
        try {
            m_jert.setVendorData(def, jobIds, data);
        } catch (SQLException sql) {
            throw new JXBPException("Error setting vendor data for jobs. No value set.", sql);
        }
    }

    /**
     * @see JXBPInternal#setVendorData(JobID[], java.lang.String)
     */	
    @WebMethod(exclude=false, operationName="setVendorData1")
    @RequestWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.SetVendor1Data")
    @ResponseWrapper(className="com.sap.engine.services.scheduler.jxbp.ws.jaxws.SetVendorData1Response")
	public void setVendorData(@WebParam(name="jobId") JobID jobId, 
							  @WebParam(name="data") String data)  
                          throws NoSuchJobException,
                                 JXBPException {
    	String method = "JXBPWebServiceImpl.setVendorData(JobID jobIds, String data)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID '"+jobId.toString()+
    							  "', data '"+data+"'"); 
    	}

        if (data.length() > JXBP.VENDOR_STRING_MAX_LENGTH) {
            throw new JXBPException("Data string it too long. The maximum length is " + JXBP.VENDOR_STRING_MAX_LENGTH + ".");
        }
        
        SchedulerDefinition def = getScheduler();
        
        try {
            m_jert.setVendorData(def, new JobID[]{jobId}, data);
        } catch (SQLException sql) {
            throw new JXBPException("Error setting vendor data for jobs. No value set.", sql);
        }
    }


    /**
     * @see JXBPInternal#getVendorData(JobID[])
     */
    @WebMethod(exclude=false, operationName="getVendorData")
	public String[] getVendorData(@WebParam(name="jobIds") JobID[] jobIds) throws JXBPException {
    	String method = "JXBPWebServiceImpl.getVendorData(JobID[] jobIds)";
    	if (m_location.bePath()) { m_location.pathT(method+" accessed with JobID[] length '"+jobIds.length); }

    	getScheduler();
    	
        try {
            return m_jert.getVendorData(jobIds);
        } catch (SQLException sql) {
            throw new JXBPException("Error getting vendor data for jobs.", sql);
        }
    }

    /**
     * @see com.sap.scheduler.spi.JXBPInternal#getJobsByVendorData(java.lang.String, com.sap.scheduler.runtime.JobIterator, int)
     */
    @WebMethod(exclude=false, operationName="getJobsByVendorData")
	public JobIteratorWS getJobsByVendorData(@WebParam(name="data") String data, 
											 @WebParam(name="iterWS") JobIteratorWS iterWS, 
											 @WebParam(name="fetchSize") int fetchSize) 
                                         throws JXBPException {
    	String method = "JXBPWebServiceImpl.getJobsByVendorData(String data, JobIteratorWS iterWS, int fetchSize)";
    	if (m_location.bePath()) { 
    		String stateDesc = null;
    		if (iterWS != null) {
    			stateDesc = iterWS.getStateDescriptor();
    		}
    		m_location.pathT(method+" accessed with vendor data '"+data+
    						 "', iterWS '"+stateDesc+
    						 "', fetchSize '"+fetchSize+"'"); 
        }
        
		// authorizes and authenticates current scheduler
    	getScheduler();

		if (iterWS == null) {
            throw new NullPointerException("JobIteratorWS object must not be null");
        }
		
        JobIterator jobIter = null;
        
        if (iterWS != null) {
        	jobIter = iterWS.getJobIterator();	
        	
            if (!jobIter.hasMoreChunks()) {
                // no more jobs, this is a client error
                return iterWS;
            }
        }
                
        if (fetchSize > JXBP.MAX_FETCH_SIZE) {
            fetchSize = JXBP.MAX_FETCH_SIZE;
        } else if (fetchSize <= 0) {
            throw new JXBPException("Fetch size must be greater than 0");
        }

        try {
            JobFilter filter = new JobFilter();
            filter.setVendorData(data);
                    
            jobIter = m_jert.getJobs(filter, jobIter, fetchSize);
            
            return new JobIteratorWS(jobIter);
            
        } catch (SQLException sql) {
            throw new JXBPException("Error getting jobs by vendor data.", sql);
        }
    }
    
    @WebMethod(exclude=false, operationName="getVersion")
	public String getVersion() {
    	String method = "JXBPWebServiceImpl.getVersion()";
    	if (m_location.bePath()) { m_location.pathT(method); }
    	
        return JXBP.JXBP_VERSION_STRING;
    }
            

    // private methods ---------------------------------------------------
    
    private void checkInternalScheduler(SchedulerDefinition def) throws JXBPException {
    	
        if (SAP_SCHEDULER_NAME.equals(def.getName())) {
        	throw new JXBPException("JXBP access to internal scheduler not allowed.");
        }
    }
    
    /**
     * Return the SchedulerDefinition object for the external scheduler that 
     * is logged on.
     */
    private synchronized SchedulerDefinition getScheduler() throws JXBPException {
    	// init the job execution runtime
    	init(); 
    	
        String user = getCurrentUser();
        if (user == null) {
            throw new JXBPException("No authenticated user.");
        }
        
        SchedulerDefinition def = null;
                
        try {
            def = m_jert.getSchedulerForUser(user);
        } catch (SQLException sql) {
            // not reported as this may be critical
            //
            m_category.logThrowableT(Severity.ERROR, m_location, "Error retrieving scheduler information for user \"" + user + "\".", sql); 
        }
        if (def == null) {
            throw new JXBPException("No scheduler assigned to authenticated user \"" + user + "\".");
        }
        checkInternalScheduler(def);
        m_jert.updateTimestamp(def.getId());
        
        return def;
    }

    private EventSubscriber getEventSubscriber() throws JXBPException {
    	// authorizes and authenticates current scheduler
		SchedulerDefinition def = getScheduler();
		try {
			return m_jert.getEventSubscriberByID(def
					.getSubscriberId());
		} catch (SQLException sql) {
			throw new JXBPException(
					"Unable to retrieve subscriber for scheduler \""
							+ def.getName() + "\".", sql);
		}
	}
    
    // must only be called by synchronized blocks (synched against this)    
    private JobExecutionRuntime init() throws JXBPException {
    	if (m_jert == null) {
			try {
				Context ctx = new InitialContext();
				m_jert = (JobExecutionRuntime) ctx.lookup("java:comp/env/scheduler/jert");
			} catch (NamingException e) {
				throw new JXBPException(e);
			}    		
    	}
		return m_jert;   
    }
    
    
    private String getCurrentUser() {
    	IUser user = UMFactory.getAuthenticator().getLoggedInUser();
    	return user.getName();    	 
    }
}
