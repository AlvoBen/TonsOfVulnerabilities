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
package com.sap.engine.jobs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import com.sap.scheduler.api.SchedulerAdministrator;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobContext;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIterator;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.mdb.MDBJobImplementation;

// Vesselin Mitrov (15th of May 2007):
// We can not use the annotations to add the connectionFactoryName in cause of
// the ConnectionFactoryName is successfully propagated to the ResourceAdapter but 
// is simply not part of the EJB metadata model. It is part of the model only if 
// specified under message-props in ejb-j2ee-engine.xml. This was the case with 
// MDB 2.1 when the factory name was mandatory. During endpoint activation, it is 
// added to the activation-config-properties. In case it is already part of the 
// activation-config-properties, it is directly processed by the ResourceAdapter
// and not part of the metadata model. The workaround would be to add the ejb-j2ee-engine.xml 
// to the application; or change the logic for checking the factory. 

@MessageDriven(name="CleanJobBean", activationConfig = {
	        //@ActivationConfigProperty(propertyName="connectionFactoryName", propertyValue="JobQueueFactory"),
	        @ActivationConfigProperty(propertyName="destination", 			propertyValue="JobQueue"),
	        @ActivationConfigProperty(propertyName="destinationType", 		propertyValue="javax.jms.Queue"),
	        @ActivationConfigProperty(propertyName="messageSelector", 		propertyValue="JobDefinition = 'CleanJob'")
	    })
@TransactionManagement(value=TransactionManagementType.CONTAINER)	 
public class CleanJobBean extends MDBJobImplementation {

    public static final long DEFAULT_SLEEP_TIME = 5000;
    
    public static final String LINE_WRAP = System.getProperty("line.separator");

    private JobExecutionRuntime jert;
    private SchedulerAdministrator schedulerAdmin;
    private boolean handleUnknownState = false;
    private boolean handleStartingJobs = false;
    private boolean handleErrorJobs = false;
    
    // required by jlin message as the logger is not serializable. A logger
    // is only valid as long as a job is running
    //
    transient private Logger logger;
    private JobContext jctx;
    boolean isCancelled = true;
    
    
    // injects a data source object
    @Resource(name="SAP/BC_SCHEDULER2")
    DataSource schedulerDs;
    
    public void onJob(JobContext jctx) {

        // (0) Job initialization
        //
        this.jctx = jctx;
        logger = jctx.getLogger();

        logger.info("************************************************************");
        logger.info("Clean Job");
        logger.info("************************************************************");
        logger.info("");
        logger.info("Configuration for Clean Job:");

        JobParameter p = jctx.getJobParameter("HandleStateUnknown");
        JobParameter p1 = jctx.getJobParameter("HandleStateStarting");
        JobParameter p2 = jctx.getJobParameter("HandleStateError");

        if (p.getBooleanValue() != null) {
            handleUnknownState = p.getBooleanValue().booleanValue();
        } else {
            handleUnknownState = new Boolean(p.getJobParameterDefinition().getDefaultData()).booleanValue();
        }

        if (p1.getBooleanValue() != null) {
            handleStartingJobs = p1.getBooleanValue().booleanValue();
        } else {
            handleStartingJobs = new Boolean(p1.getJobParameterDefinition().getDefaultData()).booleanValue();
        }

        if (p2.getBooleanValue() != null) {
            handleErrorJobs = p2.getBooleanValue().booleanValue();
        } else {
            handleErrorJobs = new Boolean(p2.getJobParameterDefinition().getDefaultData()).booleanValue();
        }
        
        logger.info("Handling Jobs with status \"UNKNOWN\": " + handleUnknownState);
        logger.info("Handling Jobs with status \"ERROR\": " + handleErrorJobs);

        try {
            Context ctx = new InitialContext();
            jert = (JobExecutionRuntime) ctx.lookup("java_scheduler/jert");
        } catch (NamingException ne) {
            jctx.jobFailed();
            StringWriter writer = new StringWriter();
            ne.printStackTrace(new PrintWriter(writer));
            logger.log(Level.SEVERE, "Unable to get reference to job execution runtime. " +
			    "Please ensure that the job is run as user \"Administrator\". Exception was:");
            logger.log(Level.SEVERE, writer.toString());
            return;
        }
        
        // lookup SchedulerAdministrator
        try {
            Context ctx = new InitialContext();
            schedulerAdmin = (SchedulerAdministrator) ctx.lookup("SchedulerAdministrator");
        } catch (NamingException ne) {
            jctx.jobFailed();
            StringWriter writer = new StringWriter();
            ne.printStackTrace(new PrintWriter(writer));
            logger.log(Level.SEVERE, "Unable to get reference to SchedulerAdministrator. " +
                "Please ensure that the job is run as user \"Administrator\". Exception was:");
            logger.log(Level.SEVERE, writer.toString());
            return;
        }
        
        logger.info("");
        
        // (1) remove all expired jobs
        //
        
        removeExpiredJobs();
        if (isCancelled()) {
            return;
        }

        // (2) remove all obsolte job definitions
        //
        removeJobDefinitions();
        if (isCancelled()) {
            return;
        }

        // (3) remove all "STARTING" jobs
        //
        removeStartingJobs();
        if (isCancelled()) {
            return;
        }

        // (4) Make sure event tables for external schedulers do not overflow
        //
        externalSchedulerEventCleanup();
        if (isCancelled()) {
            return;
        }
        
        // (5) Remove all tasks which are not in status active or hold and at least 7 days finished
        //
        removeSchedulerTasks();
        if (isCancelled()) {
            return;
        }
        
        // (6) Remove all jobs which are in status ERROR
        //
        removeErrorJobs();
        
        // (7) Update job entries with null end times
        //
        updateJobEndedNullValues();
        
        // (8) Check for double entries in BC_JOB_DEF_ARGS table
        //
        checkForDoubleEntries_08();
        
    }

    private void removeExpiredJobs() {

        logger.info("************************************************************");
        logger.info("Step 01: Removing Expired Jobs");
        logger.info("************************************************************");
        logger.info("");

        int removedJobs = 0;
        int touchedJobs = 0;
        
        try {
            JobFilter filter = new JobFilter();
            JobIterator ji = jert.getJobs(filter, null, 2000);
            Job[] jobs = ji.nextChunk();
            touchedJobs += jobs.length;
            removedJobs += removeExpiredJobs(jobs);
            
            while (ji.hasMoreChunks()) {
                ji = jert.getJobs(filter, ji, 2000);
                jobs = ji.nextChunk();
                touchedJobs += jobs.length;
                removedJobs += removeExpiredJobs(jobs);
                if (isCancelled()) {
                    return;
                }
            }
            
        } catch (SQLException sql) {
            jctx.jobFailed();
            StringWriter writer = new StringWriter();
            sql.printStackTrace(new PrintWriter(writer));
            logger.log(Level.SEVERE, "Error getting jobs from database. Exception was:");
            logger.log(Level.SEVERE, writer.toString());
            return;
        }

        logger.info("Statistics:");
        logger.info("-----------");
        logger.info("Jobs checked: " + touchedJobs);
        logger.info("Jobs deleted: " + removedJobs);
        logger.info("");
    }
    
    private int removeExpiredJobs(Job[] jobs) {
        ArrayList<Job> jobsToDelete = new ArrayList<Job>();

        for (int i=0; i < jobs.length; i++) {
            Job j = jobs[i];
            if (handleUnknownState) {
                if (!(j.getJobStatus().equals(JobStatus.COMPLETED) 
                        || j.getJobStatus().equals(JobStatus.UNKNOWN))) {
                    continue;
                }
            } else {
                if (!j.getJobStatus().equals(JobStatus.COMPLETED)) {
                    continue;
                }
            }
            if (j.getRetentionPeriod() == -1) {
                continue;
            }
            Calendar jobEndDate = Calendar.getInstance();
            Date endDate = j.getEndDate();
            if (endDate == null) {
                endDate = j.getStartDate();
            }
            jobEndDate.setTime(endDate);
            jobEndDate.add(Calendar.DAY_OF_YEAR, j.getRetentionPeriod());
            
            Calendar currentDate = Calendar.getInstance();
            
            if (currentDate.after(jobEndDate)) {
                // job can be deleted
                jobsToDelete.add(j);
            }
        }
        
        Job[] jobsToDeleteArr = null;
        try {
            jobsToDeleteArr = (Job[])jobsToDelete.toArray(new Job[jobsToDelete.size()]);
            jert.removeJobs(jobsToDeleteArr);
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < jobsToDeleteArr.length; i++) {
                buf.append(jobsToDeleteArr[i].getId().toString()).append(LINE_WRAP);
            }
            logger.info("Jobs with following ids could not be deleted. Exception was:");
            logger.info(writer.toString());
            logger.info("JobIDs are:");
            logger.info(buf.toString());
        }
        
        return jobsToDelete.size();
    }

    private int removeStartingJobs(Job[] jobs) {
        ArrayList<Job> jobsToDelete = new ArrayList<Job>();

        for (int i=0; i < jobs.length; i++) {
            Job j = jobs[i];

            Calendar c = Calendar.getInstance();
            
            c.setTime(j.getSubmitDate());     // submitted must not be null
            c.add(Calendar.DAY_OF_YEAR, 1);

            Calendar currentDate = Calendar.getInstance();
            
            if (currentDate.after(c)) {
                // job can be deleted
                jobsToDelete.add(j);
            }
        }
        
        
        Job[] jobsToDeleteArr = null;
        try {
            jobsToDeleteArr = (Job[])jobsToDelete.toArray(new Job[jobsToDelete.size()]);
            jert.removeJobs(jobsToDeleteArr, true);
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < jobsToDeleteArr.length; i++) {
                buf.append(jobsToDeleteArr[i].getId().toString()).append(LINE_WRAP);
            }
            logger.info("Jobs with following ids could not be deleted. Exception was:");
            logger.info(writer.toString());
            logger.info("JobIDs are:");
            logger.info(buf.toString());
        }
        
        return jobsToDelete.size();
    }
    
    
    /**
     * Remove job definitions which are no longer deployed. Check that there
     * are no jobs for this job definitions anymore.
     */ 
    private void removeJobDefinitions() {
        
	logger.info("************************************************************");
	logger.info("Step 02: Removing obsolete job definitions");
	logger.info("************************************************************");
	logger.info("");

        try {
            JobDefinition[] defs = jert.getJobDefinitions();
            if (isCancelled()) {
                return;
            }
	        ArrayList<JobDefinitionID> toRemove = new ArrayList<JobDefinitionID>();
            
            for (JobDefinition def : defs) {

                if (def.getRemoveDate() == null) {
                    continue;
                } else {
                    toRemove.add(def.getJobDefinitionId());
		}
	    }

	    JobDefinition[] removed = jert.removeJobDefinitions(toRemove.toArray(new JobDefinitionID[toRemove.size()]));

	    logger.info("Removed " + removed.length + " job definitions.");

	    for (JobDefinition def : removed) {
                logger.info(def.getApplication() + " / " + def.getJobDefinitionName());
	    }

        } catch (SQLException sql) {
            StringWriter writer = new StringWriter();
            sql.printStackTrace(new PrintWriter(writer));
            logger.info("Error removing outdated job definitions. Some " +
		    "old job definitions may have been deleted. Exception was: ");
            logger.info(writer.toString());
        }
        logger.info("");
    }
    
    private void removeStartingJobs() {

        logger.info("************************************************************");
        logger.info("Step 03: Remove jobs which are in status starting");
        logger.info("************************************************************");
        logger.info("");
        
        if (!handleStartingJobs) {
            logger.info("Step skipped because \"handleStartingJobs\" was set to false.");
            logger.info("");
            return;
        }
        
        int removedJobs = 0;
        int touchedJobs = 0;
        
        try {
            JobFilter filter = new JobFilter();
            filter.setJobStatus(JobStatus.STARTING);
            JobIterator ji = jert.getJobs(filter, null, 2000);
            Job[] jobs = ji.nextChunk();
            touchedJobs += jobs.length;
            removedJobs += removeStartingJobs(jobs);
            
            while (ji.hasMoreChunks()) {
                ji = jert.getJobs(filter, ji, 2000);
                jobs = ji.nextChunk();
                touchedJobs += jobs.length;
                removedJobs += removeStartingJobs(jobs);
                if (isCancelled()) {
                    return;
                }
            }
            
        } catch (SQLException sql) {
            jctx.jobFailed();
            StringWriter writer = new StringWriter();
            sql.printStackTrace(new PrintWriter(writer));
            logger.log(Level.SEVERE, "Error getting jobs from database. Exception was:");
            logger.log(Level.SEVERE, writer.toString());
            return;
        }

        logger.info("Statistics:");
        logger.info("-----------");
        logger.info("Jobs checked: " + touchedJobs);
        logger.info("Jobs deleted: " + removedJobs);
        logger.info("");    }

    
    
    private void externalSchedulerEventCleanup() {

        logger.info("************************************************************");
        logger.info("Step 04: Deactivate idle external schedulers");
        logger.info("************************************************************");
        logger.info("");

        long currentTime = System.currentTimeMillis();
        SchedulerDefinition[] defs;
        
        try {
            defs = jert.getAllSchedulers();
        } catch (SQLException sql) {
            jctx.jobFailed();
            StringWriter writer = new StringWriter();
            sql.printStackTrace(new PrintWriter(writer));
            logger.log(Level.SEVERE, "Error getting list of schedulers. Exception was: ");
            logger.log(Level.SEVERE, writer.toString());
            return;
        }
        
        for (SchedulerDefinition def : defs) {
            
            // do not deactivate internal scheduler
            //
            if (SchedulerDefinition.SAP_SCHEDULER_NAME.equals(def.getName())) {
                continue;
            }
            
            long lastAccess = def.getLastAccess();
            long inactivityPeriod = currentTime - lastAccess;
            if (inactivityPeriod > def.getInactivityGracePeriod()) {
                try {
                    jert.deactivateScheduler(def.getId());
                    long inactiveHours = inactivityPeriod / (3600 * 1000);
                    long graceHours = def.getInactivityGracePeriod() / (3600 * 1000);
                    logger.info("Scheduler \"" + def.getName() + "\" deactivated. " +
                            "Scheduler was inactive for " + inactiveHours + " hours but " +
                            "but grace period id only " + graceHours + ".");
                } catch (SQLException sql) {
                    jctx.jobFailed();
                    StringWriter writer = new StringWriter();
                    sql.printStackTrace(new PrintWriter(writer));
                    logger.log(Level.SEVERE, "Error deactivating external scheduler. Exception was: ");
                    logger.log(Level.SEVERE, writer.toString());
                    return;
                }
            }
        }
    }
    
    
    private void removeSchedulerTasks() {

        logger.info("*******************************************************************************************");
        logger.info("Step 05: Remove tasks which are not in status active or hold and more than 30 days finished");
        logger.info("*******************************************************************************************");
        logger.info("");

        // all tasks which are in status finished and older than 30 days should be removed
        // construct the date
        long oneMonthInMillis = (30*24*60*60*1000);
        Timestamp ts = new Timestamp( (System.currentTimeMillis()-oneMonthInMillis) );
        
        int countOfRemovedTasks = 0;
        
        try {
            countOfRemovedTasks = schedulerAdmin.removeSchedulerTasks(ts);
            
            logger.info("Statistics:");
            logger.info("-----------");
            logger.info("Count of removed tasks: " + countOfRemovedTasks);
            logger.info("");
            
        } catch (Exception sql) {
            jctx.jobFailed();
            StringWriter writer = new StringWriter();
            sql.printStackTrace(new PrintWriter(writer));
            logger.log(Level.SEVERE, "Error while removing old SchedulerTasks. Exception was: ");
            logger.log(Level.SEVERE, writer.toString());
            return;
        }
    }
    
    private void removeErrorJobs() {

        logger.info("************************************************************");
        logger.info("Step 06: Removing Expired Jobs in status ERROR");
        logger.info("************************************************************");
        logger.info("");

        if (!handleErrorJobs) {
            logger.info("Step skipped because HandleErrorJobs is set to false.");
            logger.info("");
            return;
        }
        
        int removedJobs = 0;
        int touchedJobs = 0;
        
        try {
            JobFilter filter = new JobFilter();
            filter.setJobStatus(JobStatus.ERROR);
            JobIterator ji = jert.getJobs(filter, null, 2000);
            Job[] jobs = ji.nextChunk();
            touchedJobs += jobs.length;
            removedJobs += removeExpiredErrorJobs(jobs);
            
            while (ji.hasMoreChunks()) {
                ji = jert.getJobs(filter, ji, 2000);
                jobs = ji.nextChunk();
                touchedJobs += jobs.length;
                removedJobs += removeExpiredErrorJobs(jobs);
                if (isCancelled()) {
                    return;
                }
            }
            
        } catch (SQLException sql) {
            jctx.jobFailed();
            StringWriter writer = new StringWriter();
            sql.printStackTrace(new PrintWriter(writer));
            logger.log(Level.SEVERE, "Error getting jobs from database. Exception was:");
            logger.log(Level.SEVERE, writer.toString());
            return;
        }

        logger.info("Statistics:");
        logger.info("-----------");
        logger.info("Jobs checked: " + touchedJobs);
        logger.info("Jobs deleted: " + removedJobs);
        logger.info("");
    }
    
    private void checkForDoubleEntries_08() {
    	
        logger.info("************************************************************");
        logger.info("Step 08: Checking for BC_JOB_DEF_ARGS consistency");
        logger.info("************************************************************");
        logger.info("");


    	String strStmt = "select  ARG_NAME, JOB_DEF_ID from BC_JOB_DEF_ARGS group by ARG_NAME, JOB_DEF_ID HAVING count(*) > 1";
    	Connection conn = null;;
    	
    	try {
    		Statement stmt;
    		conn = schedulerDs.getConnection();
    		stmt = conn.createStatement();
    		ResultSet res = stmt.executeQuery(strStmt);
    		
    		boolean hasDouble = res.next();
    		
    		if (hasDouble) {
    			logger.info("There are double entries in the BC_JOB_DEF_ARGS table:");
    			logger.info("");
    			
    			do {
    				String argname = res.getString(1);
    				JobDefinitionID id = JobDefinitionID.parseID(res.getBytes(2));

    				logger.info("Parameter '" + argname + "' of job definition '" + id.toString() + "' is stored twice.");
    				
    			} while (res.next());
    			
    		} else {
    			logger.info("The BC_JOB_DEF_ARGS table is consistent.");
    			logger.info("");
    		}
    		
    		res.close();
    		stmt.close();
    		
    	} catch (SQLException sql) {
    		
    	} finally {
    		try {
    			if (conn != null) {
    				conn.close();
    			}
    		} catch (SQLException sql) {
                jctx.jobFailed();
                StringWriter writer = new StringWriter();
                sql.printStackTrace(new PrintWriter(writer));
                logger.log(Level.SEVERE, "Error while closing SQL Exception: " + writer.toString());
    		}
    	}
    	
    }
    
    private int removeExpiredErrorJobs(Job[] jobs) {
        ArrayList<Job> jobsToDelete = new ArrayList<Job>();

        for (int i=0; i < jobs.length; i++) {
            Job j = jobs[i];
            
            if (!j.getJobStatus().equals(JobStatus.ERROR)) {
                // safety net, should never get here
                //
                continue;
            }
            if (j.getRetentionPeriod() == -1) {
                continue;
            }

            Calendar jobEndDate = Calendar.getInstance();
            Date endDate = j.getEndDate();
            if (endDate == null) {
                endDate = j.getStartDate();
            }
            if (endDate == null) {
                // submit date cannot be null. Only set if jms forgets
                // about a message.
                endDate = j.getSubmitDate();
            }
            jobEndDate.setTime(endDate);
            jobEndDate.add(Calendar.DAY_OF_YEAR, j.getRetentionPeriod());
            
            Calendar currentDate = Calendar.getInstance();
            
            if (currentDate.after(jobEndDate)) {
                // job can be deleted
                jobsToDelete.add(j);
            }
        }
        
        Job[] jobsToDeleteArr = null;
        try {
            jobsToDeleteArr = (Job[])jobsToDelete.toArray(new Job[jobsToDelete.size()]);
            jert.removeJobs(jobsToDeleteArr);
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < jobsToDeleteArr.length; i++) {
                buf.append(jobsToDeleteArr[i].getId().toString()).append(LINE_WRAP);
            }
            logger.info("Jobs with following ids could not be deleted. Exception was:");
            logger.info(writer.toString());
            logger.info("JobIDs are:");
            logger.info(buf.toString());
        }
        
        return jobsToDelete.size();
    }
    
    private void updateJobEndedNullValues() {

        // Note: this step is quiet unless it is doing something
        //
        
        // Make sure we are using the correct version
        //
        try {
            Class c = jert.getClass();
            c.getMethod("updateEndedNullValues", null);
        } catch (NoSuchMethodException nse) {
            // ok, we are still getting an old API, we do fail
            // silently
            return;
        }

        int updated = 0;
        SQLException sqlOuter = null;
        try {
            updated = jert.updateEndedNullValues();
            
        } catch (SQLException sql) {
            sqlOuter = sql;
        }
        
        if (updated > 0 || sqlOuter != null) {
            // don't keep silent
            logger.info("************************************************************");
            logger.info("Step 07: Fixing end time property for some jobs");
            logger.info("************************************************************");
            logger.info("");
            if (updated > 0) {
                logger.info("Updated " + updated + " rows.");
                logger.info("");
            } else {
                StringWriter writer = new StringWriter();
                sqlOuter.printStackTrace(new PrintWriter(writer));

                logger.info(writer.toString());
                logger.info("");
            }
        }
        
    }
    
    
    private boolean isCancelled() {
        if (isCancelled = jctx.isCancelled()) {
            logger.info("");
            logger.info("************************************************************");
            logger.info("Job was cancelled.");
            return true;
        }
        return false;
    }
}
