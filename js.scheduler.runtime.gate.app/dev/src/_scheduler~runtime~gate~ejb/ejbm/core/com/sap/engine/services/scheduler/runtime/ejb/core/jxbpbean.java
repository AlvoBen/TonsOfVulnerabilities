package com.sap.engine.services.scheduler.runtime.ejb.core;


import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.CreateException;
import javax.ejb.LocalHome;
import javax.ejb.RemoteHome;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.services.scheduler.runtime.ejb.api.JXBPHome;
import com.sap.engine.services.scheduler.runtime.ejb.api.JXBPLocalHome;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.scheduler.runtime.JobExecutorException;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIterator;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.NoSuchJobDefinitionException;
import com.sap.scheduler.runtime.ParameterValidationException;
import com.sap.scheduler.runtime.JobIllegalStateException;
import com.sap.scheduler.runtime.EventSubscriber;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.SchedulerID;

import com.sap.scheduler.spi.JXBP;
import com.sap.scheduler.spi.JXBPException;
import com.sap.scheduler.spi.JobParameterWS;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

@LocalHome(value=JXBPLocalHome.class)
@RemoteHome(value=JXBPHome.class)
@Stateless(name="JXBP")
@TransactionManagement(value=TransactionManagementType.CONTAINER)
public class JXBPBean implements JXBP {

	@Resource SessionContext myContext;
	
	/**
	 * Initialization of the location for SAP logging.
	 */
	private final static Location location = Location
                                            .getLocation(JXBPBean.class);

	/**
	 * Initialization of the category for SAP logging.
	 */
	private final static Category category = Category.SYS_SERVER;

	/**
	 * @todo get the internal name from the runtime
	 */
    public static final String SAP_SCHEDULER_NAME = SchedulerDefinition.SAP_SCHEDULER_NAME;
    
    private JobExecutionRuntime jobExecutionRuntime;
        
    /**
     * @see JXBPInternal#getSystemTimeZone()
     */
    public String getSystemTimeZone() {
        
        // authoizes and authenticates current scheduler
        //
        try {
            SchedulerDefinition def = getScheduler();
        } catch (JXBPException jx) {
            // need to ignore that here but it is not critical
            category.logThrowableT(Severity.ERROR, location, "Exception in getSystemTimeZone(): ", jx);
        }
        return jobExecutionRuntime.getSystemTimeZone();
    }


	/**
	 * @see JXBPInternal#getJobDefinitions()
	 */
	public JobDefinition[] getJobDefinitions()
                                      throws JXBPException {
        
		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

		try {
            return jobExecutionRuntime.getJobDefinitions();   
        } catch (SQLException sq) {
            throw new JXBPException("Technical problem retrieving job definitions.",sq);
        }        
	}

	/**
	 * @see JXBPInternal#getJobDefinitionByName(java.lang.String)
	 */
	public JobDefinition getJobDefinitionByName(String jobName) 
                                                throws JXBPException {

		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();
 
        try {
            return jobExecutionRuntime.getJobDefinitionByName(jobName);
        } catch (SQLException sql) {
            throw new JXBPException("Error getting job definition from database.", sql);
        }        		
	}

    /**
     * @see JXBPInternal#getJobDefinitionById(JobDefinitionID)
     */
    public JobDefinition getJobDefinitionById(JobDefinitionID id)
                                            throws JXBPException {

		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

        try {
            return jobExecutionRuntime.getJobDefinitionById(id);
        } catch (SQLException sql) {
            throw new JXBPException("Error getting job definition from database.", sql);
        }               
        
        
    }

	//------------------------------------------------------------------
	// Starting and stopping jobs
	//------------------------------------------------------------------

    /**
     */
    public JobID executeJob(JobDefinitionID jobDefId, 
            JobParameterWS[] jobParametersWS,
            Integer retentionPeriod) 
                       throws ParameterValidationException,
                              NoSuchJobDefinitionException,
                              JXBPException { 
        return executeJob(jobDefId, jobParametersWS, retentionPeriod, null);
    }

	/**
	 * @see JXBPInternal#executeJob()
	 */
    public JobID executeJob(JobDefinitionID jobDefId, 
                            JobParameterWS[] jobParametersWS,
                            Integer retentionPeriod,
                            String vendorData) 
                                       throws ParameterValidationException,
                                              NoSuchJobDefinitionException,
                                              JXBPException { 
        
        if (vendorData != null && vendorData.length() > JXBP.VENDOR_STRING_MAX_LENGTH) {
            throw new JXBPException("Vendor data string it too long. The maximum length is " + JXBP.VENDOR_STRING_MAX_LENGTH + ".");
        }

        SchedulerDefinition def = getScheduler();
        
        // Customer OSS 221142 2009 (NullPointerException when invoking 
        // the Web Service with a null pointer or an empty array for no 
        // argument jobs
        //
        int length = jobParametersWS == null ? 0 : jobParametersWS.length;
        
        JobParameter[] jobParameters = new JobParameter[length];
        for (int i=0; i < length; i++) {
            jobParameters[i] = jobParametersWS[i].getJobParameter();
        }

        try {
            return jobExecutionRuntime.executeJobJXBP(jobDefId, jobParameters, retentionPeriod, null, def.getId(), vendorData);
        } catch (JobExecutorException jee) {
            throw new JXBPException(jee.getMessage(), jee);
        }
    }

	/**
	 * @see JXBPInternal#cancelJob(JobID)
	 */
	public void cancelJob(JobID jobid) 
                           throws JXBPException,
                                  JobIllegalStateException,
                                  NoSuchJobException {

		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

		try {
            jobExecutionRuntime.cancelJob(jobid);
        } catch (SQLException sql) {
            throw new JXBPException("Error cancelling job \"" + jobid.toString() + "\". Job not cancelled.", sql);
        }
	}
    
    /**
     * @see JXBPInternal#holdJob(JobID)
     */
    public void holdJob(JobID jobid) 
              throws JobIllegalStateException,
                     NoSuchJobException,
                     JXBPException {
    	throw new UnsupportedOperationException();
    }

    /**
     * @see JXBPInternal#releaseJob(JobID)
     */
    public void releaseJob(JobID jobid)
                          throws JobIllegalStateException,
                                 NoSuchJobException,
                                 JXBPException {
        throw new UnsupportedOperationException();
    }

	//------------------------------------------------------------------
	// Parent/Child Functionality
	//------------------------------------------------------------------

	/**
	 * @see JXBPInternal#getChildJobs(JobID)
	 */
	public Job[] getChildJobs(JobID jobid)
                                       throws NoSuchJobException,
                                              JXBPException {

		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

        try {
            return jobExecutionRuntime.getChildJobs(jobid);
        } catch (SQLException sql) {
            throw new JXBPException("Databse error retrieving child jobs.", sql);
        }
	}

	/**
     * @see JXBPInternal#hasChildJobs(JobID)
	 */
	public boolean hasChildJobs(JobID jobid) 
                                     throws NoSuchJobException,
                                            JXBPException {

		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

        try {
            return jobExecutionRuntime.hasChildJobs(jobid);
        } catch (SQLException sql) {
            throw new JXBPException("Error checking for child jobs.", sql);
        }
    }

    /**
     * @see JXBPInternal#haveChildJobs(JobID[])
     */
    public boolean[] haveChildJobs(JobID[] jobids)
                                     throws JXBPException {

    	// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

        try {
            return jobExecutionRuntime.haveChildJobs(jobids);
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
	public void removeJob(JobID jobid) 
                                   throws NoSuchJobException,
                                          JobIllegalStateException,
                                          JXBPException {

		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();
		
        try {
            jobExecutionRuntime.removeJob(jobid);
        } catch (SQLException sql) {
            throw new JXBPException("Error removing job information for job \"" + jobid + "\" from database.", sql);
        }
	}
    
    /**
     * This method removes all information the given job instances fron the J2EE
     * Engine (including logs). This is a convenience method
     */
    public void removeJobs(JobID[] jobids) 
                                     throws JXBPException {

    	// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

        try {
            jobExecutionRuntime.removeJobs(jobids);
        } catch (SQLException sql) {
            throw new JXBPException("Error removing job information for jobs from database.", sql);
        }        
    }

	/**
     * @see JXBP#getJob(JobID)
	 */
	public Job getJob(JobID jobid) 
                              throws JXBPException {
		
		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

		try {
            return jobExecutionRuntime.getJob(jobid);
        } catch (SQLException sql) {
            throw new JXBPException("Error getting job information from database.", sql);
        }
	}

    /**
     * @see JXBP#getJobParameters(JobID)
     */
    public JobParameterWS[] getJobParameters(JobID jobid)
                                        throws JXBPException {

    	// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

    	try {
            JobParameter[] params = jobExecutionRuntime.getJobParameters(jobid);
            JobParameterWS[] paramsWS = new JobParameterWS[params.length];
            for (int i=0; i< params.length;i++) {
                paramsWS[i] = JobParameterWS.getJobParameterWS(params[i]);
            }
            return paramsWS;
        } catch (Exception e) {
            throw new JXBPException("Error getting job information from database.", e);
        } 
       
    }
    
	/**
	 * @see JXBPInternal#getJobs(JobID[])
	 */
	public Job[] getJobs(JobID[] jobids) 
                                     throws JXBPException {
        
		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

		try {
            return jobExecutionRuntime.getJobs(jobids);
        } catch (SQLException sql) {
            throw new JXBPException("Error getting job information from database.", sql);
        }
	}
    
    public JobIterator getJobs(JobFilter filter, JobIterator iter, int fetchSize)
                                                                    throws JXBPException {
        
		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

		if (fetchSize > JXBP.MAX_FETCH_SIZE) {
            fetchSize = JXBP.MAX_FETCH_SIZE;
        }
        
        try {
            return jobExecutionRuntime.getJobs(filter, iter, fetchSize);
        } catch (SQLException sql) {
            throw new JXBPException("Error getting job records from database.", sql);
        }
    }

	/**
	 * @see JXBP#getJobLog(JobID, com.sap.scheduler.runtime.LogIterator, int)
	 */
	public LogIterator getJobLog(JobID jobid, LogIterator it, int fetchSize) 
                                                    throws NoSuchJobException, JXBPException {

		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

        if (fetchSize > JXBP.MAX_FETCH_SIZE) {
            fetchSize = JXBP.MAX_FETCH_SIZE;
        } else if (fetchSize <= 0) {
            throw new JXBPException("Fetch size must be greater than 0");
        }

        try {
            return jobExecutionRuntime.getJobLog(jobid, it, fetchSize);
        } catch (SQLException se) {
            throw new JXBPException("Database error retriveing logfile.",se);
        }
	}
        
	/**
	 * @see JXBPInternal#removeJobLog(JobID)
	 */
	public void removeJobLog(JobID jobid) throws NoSuchJobException,
                                                 JobIllegalStateException,
                                                 JXBPException {

		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

        try {
            jobExecutionRuntime.removeJobLog(jobid);
        } catch (SQLException sq) {
            throw new JXBPException("Database error deleting logfile.",sq);
        }
	}

    /**
     * @see JXBPInternal#getJobStatus(JobID)
     */
	public JobStatus getJobStatus(JobID jobid) 
                                        throws NoSuchJobException,
                                               JXBPException {
		
		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

		try {
            return jobExecutionRuntime.getJobStatus(jobid);
        } catch (SQLException sql) {
            throw new JXBPException("Unable to get job status.", sql);
        }
	}

    /**
     * @see JXBPInternal#getJobsByStatus(JobStatus, JobIterator, int)
     */
	public JobIterator getJobsByStatus(JobStatus s, JobIterator it, int fetchSize) 
                                                            throws JXBPException {

		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

        if (it == null) {
            throw new NullPointerException("JobIterator object must not be null");
        }
        
        if (!it.hasMoreChunks()) {
            // no more jobs, this is a client error
            return it;
        }
        
        if (fetchSize > MAX_FETCH_SIZE) {
            fetchSize = MAX_FETCH_SIZE;
        } else if (fetchSize <= 0) {
            throw new JXBPException("Fetch size must be greater than 0");
        }

        try {
            JobFilter filter = new JobFilter();
            filter.setJobStatus(s);
            
            return jobExecutionRuntime.getJobs(filter, it, fetchSize);
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
    public Event[] getUnhandledEvents(int fetchSize)
                                            throws JXBPException
    {
        if (fetchSize > MAX_FETCH_SIZE) {
            fetchSize = MAX_FETCH_SIZE;
        } else if (fetchSize <= 0) {
            throw new JXBPException("fetchSize variable must be greater than 0");
        }
        
    	EventSubscriber ev = getEventSubscriber();
        return jobExecutionRuntime.getEvents(ev, fetchSize);
    }
    
    /**
     * @see JXBP#clearEvents()
     */
    public void clearEvents() 
                      throws JXBPException {
        jobExecutionRuntime.clearEvents(getEventSubscriber());
    }
    
    /**
     * @see JXBPInternal#setFilter(java.lang.String[])
     */
    public void setFilter(String[] eventType) 
                                       throws JXBPException {
        
        // event type must not be null
        //
        if (eventType == null) {
            throw new NullPointerException("Event type must not be null.");
        }
        
        SchedulerDefinition def = getScheduler();

        try {
            jobExecutionRuntime.setFilter(def, eventType);
        } catch (SQLException sql) {
            throw new JXBPException("Unable to set filter due to technical exception.", sql);
        }
    }

    /**
     * @see JXBP#getJXBPRuntimeEventTypes()
     */
    public String[] getJXBPRuntimeEventTypes() {
        
        return Event.JXBP_RUNTIME_EVENT_TYPES;
    }

    /**
     * @see JXBPInternal#setVendorData(JobID[], java.lang.String)
     */
    public void setVendorData(JobID[] jobIds, String data) 
                                                  throws JXBPException {

        if (data.length() > JXBP.VENDOR_STRING_MAX_LENGTH) {
            throw new JXBPException("Vendor data string is too long. The maximum length is " + JXBP.VENDOR_STRING_MAX_LENGTH + ".");
        }
        
        SchedulerDefinition def = getScheduler();
        
        try {
            jobExecutionRuntime.setVendorData(def, jobIds, data);
        } catch (SQLException sql) {
            throw new JXBPException("Error setting vendor data for jobs. No value set.", sql);
        }
    }

    /**
     * @see JXBPInternal#setVendorData(JobID[], java.lang.String)
     */	
    public void setVendorData(JobID jobId, String data) 
                                                  throws NoSuchJobException,
                                                         JXBPException {

        if (data.length() > JXBP.VENDOR_STRING_MAX_LENGTH) {
            throw new JXBPException("Data string it too long. The maximum length is " + JXBP.VENDOR_STRING_MAX_LENGTH + ".");
        }
        
        SchedulerDefinition def = getScheduler();
        
        try {
            jobExecutionRuntime.setVendorData(def, new JobID[]{jobId}, data);
        } catch (SQLException sql) {
            throw new JXBPException("Error setting vendor data for jobs. No value set.", sql);
        }
    }


    /**
     * @see JXBPInternal#getVendorData(JobID[])
     */
    public String[] getVendorData(JobID[] jobIds)
                                     throws JXBPException
    {

    	SchedulerDefinition def = getScheduler();
        try {
            return jobExecutionRuntime.getVendorData(jobIds);
        } catch (SQLException sql) {
            throw new JXBPException("Error getting vendor data for jobs.", sql);
        }
    }

    /**
     * @see com.sap.scheduler.spi.JXBPInternal#getJobsByVendorData(java.lang.String, com.sap.scheduler.runtime.JobIterator, int)
     */
    public JobIterator getJobsByVendorData(String data, JobIterator it, int fetchSize) 
                                                                throws JXBPException {
        
		// authoizes and authenticates current scheduler
		//
		SchedulerDefinition def = getScheduler();

		if (it == null) {
            throw new NullPointerException("JobIterator object must not be null");
        }
        
        if (!it.hasMoreChunks()) {
            // no more jobs
            return it;
        }
        
        if (fetchSize > MAX_FETCH_SIZE) {
            fetchSize = MAX_FETCH_SIZE;
        } else if (fetchSize <= 0) {
            throw new JXBPException("Fetch size must be greater than 0");
        }
        
        try {
            JobFilter filter = new JobFilter();
            filter.setVendorData(data);
                    
            return jobExecutionRuntime.getJobs(filter, it, fetchSize);
        } catch (SQLException sql) {
            throw new JXBPException("Error getting jobs by vendor data.", sql);
        }
    }
    
    public String getVersion() {
        return JXBP_VERSION_STRING;
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
    
        String user = myContext.getCallerPrincipal().getName();
        if (user == null) {
            throw new JXBPException("No authenticated user.");
        }
        
        SchedulerDefinition def = null;
                
        try {
            def = jobExecutionRuntime.getSchedulerForUser(user);
        } catch (SQLException sql) {
            // not reported as this may be critical
            //
            category.logThrowableT(Severity.ERROR, location, "Error retrieving scheduler information for user \"" + user + "\".", sql); 
        }
        if (def == null) {
            throw new JXBPException("No scheduler assigned to authenticated user \"" + user + "\".");
        }
        checkInternalScheduler(def);
        jobExecutionRuntime.updateTimestamp(def.getId());
        
        return def;
    }

    private EventSubscriber getEventSubscriber() throws JXBPException {
		SchedulerDefinition def = getScheduler();
		try {
			return jobExecutionRuntime.getEventSubscriberByID(def
					.getSubscriberId());
		} catch (SQLException sql) {
			throw new JXBPException(
					"Unable to retrieve subscriber for scheduler \""
							+ def.getName() + "\".", sql);
		}
	}

	private SchedulerID getSchedulerId() throws JXBPException {

		SchedulerDefinition def = getScheduler();
		return def.getId();
	}


	// ejb methods ---------------------------------------------------------

	/**
	 * Create Method.
	 */
	@PostConstruct
	public void ejbCreate() throws CreateException {
		try {
			InitialContext ictx = new InitialContext();
			jobExecutionRuntime = (JobExecutionRuntime)ictx.lookup("java:comp/env/JobExecutionRuntimeService");
		} catch (NamingException ne) {
			throw new CreateException("Unable to obtain Job Execution Runtime Environment: " + ne.getMessage());
		}
	}

}

