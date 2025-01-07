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
package com.sap.engine.services.scheduler.runtime.mdb;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.security.auth.Subject;

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduler.runtime.db.DBHandler;
import com.sap.engine.services.scheduler.runtime.db.JobQueryHandler;
import com.sap.engine.services.scheduleradapter.jobdeploy.MessageSelectorParser;
import com.sap.engine.services.scheduleradapter.scheduler.JobContextImpl;
import com.sap.jvm.monitor.vm.ThreadMemoryInfo;
import com.sap.jvm.monitor.vm.ThreadTimeInfo;
import com.sap.jvm.monitor.vm.VmInfo;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.EventAcceptor;
import com.sap.scheduler.runtime.EventSubscriber;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobContext;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.SchedulerRuntimeException;
import com.sap.scheduler.runtime.SubscriberID;
import com.sap.scheduler.runtime.mdb.MDBJob;
import com.sap.scheduler.runtime.mdb.MDBJobDelegate;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.jvm.Capabilities;

/**
 * 
 *
 * @author Dirk Marwinski
 */
public class MDBJobDelegateImpl implements MDBJobDelegate {

    private final static Location location = Location.getLocation(MDBJobDelegateImpl.class);
    private final static Category category = LoggingHelper.SYS_SERVER;

    private Environment mEnvironment;
    private JobExecutionRuntime mJERT;
    private boolean hasSAPJvmMonitoring;    
    
    public MDBJobDelegateImpl(Environment env, JobExecutionRuntime jert) {
        mEnvironment = env;
        mJERT = jert;
        
        hasSAPJvmMonitoring = Capabilities.hasVmMonitoring();
    }
        
    public void onMessage(Message msg, MessageDrivenContext mdctx, MDBJob job) {

        JobID jobId;
        
        String jmsMessageID;
        String jobDefinitionNameFromMessage = null;
        
        // get job id
        //
        try {
        	 String strJobId = msg.getStringProperty("job-id");
        	 if (strJobId == null) {
                category.errorT(location,"JMS message for job did not contain a job id. Job not run.");
                return;
            }
        	jobId = JobID.parseID(strJobId);
            jobDefinitionNameFromMessage = msg.getStringProperty(MessageSelectorParser.JOB_DEFINITION);
        } catch (JMSException e) {
            category.logThrowableT(Severity.ERROR, location, "Cannot get \"job-id\" property from JMS message. Job not run.",e);
            return;
        }
        
        // get jms message id
        //
        try {
            jmsMessageID = msg.getJMSMessageID();
        } catch (JMSException e) {
            category.logThrowableT(Severity.ERROR, location, "Cannot get JMS mesasge id from JMS message. Job not run.",e);
            return;
        }
        
        if (jmsMessageID == null) {
            category.errorT(location, "JMS message for job \"" + jobId.toString()+ "\" did not contain a JMS message id. Job not run.");
        }
        
        
        // OK, now we have everything to start with
        //        
        if (location.beDebug()) {
            location.debugT("MDB received request to run job \"" + jobDefinitionNameFromMessage + "\" with id \"" + jobId.toString() + "\".");   
        }
        
        // ---------------------------------------------------------------
        // Sanity checkes on jobs:
        // - does the job exist and is in the correct state?  
        // - are all parameters of the correct type and constraints set 
        // ---------------------------------------------------------------

        // This is the official start time of the job
        //
        Date startTime = new Date();
        
        // get job information from database
        //
        DBHandler db = mEnvironment.getDBHandler();
        Job ji = null;
        JobParameter[] params = null;
        
        try {
            ji = JobQueryHandler.Instance().getJob(jobId);
        } catch (SQLException ex) {
            category.logThrowableT(Severity.ERROR, location, "Job \"" + jobId + "\" cannot be started. It could not e retrieved from the database.", ex);
            return;
        }
        
        if (ji == null) {
            category.errorT(location, "Job \"" + jobId + "\" cannot be started. It is either not known or the record from the database cannot be retrieved.");
            return;
        }


        try {
            params = db.getJobParameters(ji);
        } catch (SQLException sql) {
            category.logThrowableT(Severity.ERROR, location, "Unable to read parameters for job \"" + jobId.toString() + "\".", sql);
            return;
        }

        
        if (ji.getJobStatus().equals(JobStatus.CANCELLED)) {
            // job has been cancelled between sending the jms message and 
            // receiving it. Nothing to do, everything has been done.
            return;
        }

        if (!ji.getJobStatus().equals(JobStatus.STARTING)) {
            // The job is in a wrong status, it must go into "UNKNOWN" now
            //
            category.errorT(location, "Job \"" + jobId.toString() + "\" must be in status STARTING in order to be started. Is is in status " + ji.getJobStatus().toString());
            db.jobEnded(jobId, JobStatus.UNKNOWN, (short)0, startTime, 0, 0);
            mEnvironment.getEventManager().raiseEvent(Event.EVENT_JOB_FINISHED, jobId.toString(), JobStatus.UNKNOWN.toString(), startTime);
            return;
        }        
        
        // mark job as started in the database
        //
        if (!db.jobStarted(jobId, startTime, mEnvironment.getNodeName())) {
            // updating the status failed, try to set it to "ERROR" and 
            // exit
            db.jobEnded(jobId, JobStatus.ERROR, (short)0, startTime, 0, 0);
            mEnvironment.getEventManager().raiseEvent(Event.EVENT_JOB_FINISHED, jobId.toString(), JobStatus.ERROR.toString(), startTime);
            return;
        }
        
        // TODO: need to add a lot of sanity checks here

        // ---------------------------------------------------------------
        // Initialize Logging
        // ---------------------------------------------------------------        

        Location jobLogLocation = mEnvironment.getJobLoggingManager().initializeLocation(jobId);
        Category jobLogCategory = mEnvironment.getJobLoggingManager().initializeCategory(jobId);
        
        final JobContextImpl ctx = new JobContextImpl(mEnvironment, ji, jobLogLocation, jobLogCategory, params, mJERT);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss:SSS z");

        Logger logger = ctx.getLogger();
    	logger.info("Job " + ji.getName() + " (ID: " + jobId.toString() + ", JMS ID: " + jmsMessageID + ") started on " + dateFormatter.format(startTime) + " by scheduler: " + ji.getScheduler());
        mEnvironment.getEventManager().raiseEvent(Event.EVENT_JOB_STARTED, jobId.toString(), startTime);
        
        // job must subsribe to events in order to get job started / ended
        // events
        String[] filters = new String[] { Event.EVENT_JOB_STARTED, 
                                          Event.EVENT_JOB_FINISHED };
        
        EventAcceptor acceptor = new EventAcceptor() {
            public boolean acceptEvent(Event e) {
                return ctx.acceptEvent(e);
            }
        };
        
        EventSubscriber sub = new EventSubscriber(SubscriberID.parseID(jobId.getBytes()), acceptor, filters, false);
        ctx.setEventSubscriber(sub);
        
        long cpuStart = 0, cpuEnd = 0;
        long allocationStart = 0, allocationEnd = 0;
        
        try {
            mEnvironment.getEventManager().registerEventSubscriber(sub);
            
            if (ji.getUser() != null) {
                
                // run-as the specified user if specified
                //
                String userName = ji.getUser();
                                
                final MDBJob job1 =job;
                final JobContext ctx1 = ctx;
                PrivilegedExceptionAction jobAction = new PrivilegedExceptionAction() {
                    
                    public Object run() throws Exception {
                        job1.onJob(ctx1);
                        return null;
                    }
                };
                
                SecurityContext rootSecurityContext = (SecurityContext)mEnvironment.getServiceContext().getContainerContext().getObjectRegistry().getProvidedInterface("security");
                UserStoreFactory userStoreFactory = rootSecurityContext.getUserStoreContext();

                UserStore userStore = userStoreFactory.getActiveUserStore();
                final UserContext userCtx = userStore.getUserContext();
                final UserInfo user = userCtx.getUserInfo(userName);

                final Subject runAsSubject = new Subject();
                
                // Stephan Zlatarev (28th Feb 2007):
                // In cause we are called from JMS which delivers messages in 
                // application-threads we need to fill the subject in an priviliged
                // block
                PrivilegedAction privilegedAction = new PrivilegedAction() {
                    public Object run() {
                        userCtx.fillSubject(user, runAsSubject);
                        return null;
                    }
                };
                AccessController.doPrivileged(privilegedAction);
                
                try {
                    if (hasSAPJvmMonitoring) {
                        cpuStart = getCurrentCPU();
                        allocationStart = getThreadMemoryInfo();
                    }
                    
                    if (location.beDebug()) location.debugT("Trigger job \"" + jobId.toString() + "\" on behalf of (Subject.doAs(...)) with user '"+userName+"'");
                    
                    Object result = Subject.doAs(runAsSubject, jobAction);

                    if (hasSAPJvmMonitoring) {
                        cpuEnd = getCurrentCPU();
                        allocationEnd = getThreadMemoryInfo();
                    }

                } catch (PrivilegedActionException pae) {
                    // Job failed to run as the specified user
                    //
                    category.logThrowableT(Severity.ERROR, location, "Job \"" + jobId + "\" could not be run as user\"" + userName + "\".", pae);
                    jobLogCategory.logThrowableT(Severity.ERROR, jobLogLocation, "Job \"" + jobId + "\" could not be run as user\"" + userName + "\".", pae);
                    db.jobEnded(jobId, JobStatus.ERROR, (short)0, startTime, 0, 0);
                    mEnvironment.getEventManager().raiseEvent(Event.EVENT_JOB_FINISHED, jobId.toString(), JobStatus.ERROR.toString(), startTime);
                    return;
                }
            
            } else {
                if (hasSAPJvmMonitoring) {
                    cpuStart = getCurrentCPU();
                    allocationStart = getThreadMemoryInfo();
                }

                job.onJob(ctx);
                
                if (hasSAPJvmMonitoring) {
                    cpuEnd = getCurrentCPU();
                    allocationEnd = getThreadMemoryInfo();
                }
            }
            
            Date jobEnded = new Date();
            
            if (ctx.getJobFailed()) {
                // job failed on job request
                //
                if (location.beDebug()) {
                    location.debugT("Job \"" + jobId.toString() + "\" ended with errors (caused by job request).");
                }
                
                db.jobEnded(jobId, JobStatus.ERROR, ctx.getReturnCode(), jobEnded, (cpuEnd - cpuStart), (allocationEnd - allocationStart));
                mEnvironment.getEventManager().raiseEvent(Event.EVENT_JOB_FINISHED, jobId.toString(), JobStatus.ERROR.toString(), jobEnded);                
            } else {
                // job was successful
                //
                if (location.beDebug()) {
                    location.debugT("Job \"" + jobId.toString() + "\" finished successfully.");
                }
                db.jobEnded(jobId, JobStatus.COMPLETED, ctx.getReturnCode(), jobEnded, (cpuEnd - cpuStart), (allocationEnd - allocationStart));
                mEnvironment.getEventManager().raiseEvent(Event.EVENT_JOB_FINISHED, jobId.toString(), JobStatus.COMPLETED.toString(), jobEnded);                
            }

        } catch (SchedulerRuntimeException sre)  {
            
            if (location.beDebug()) {
                location.debugT("Job \"" + jobId.toString() + "\" ended with errors (unexpected exception).");
            }

            if (hasSAPJvmMonitoring) {
                cpuEnd = getCurrentCPU();
                allocationEnd = getThreadMemoryInfo();
            }
            // TODO special handling required, most likely a database issue
            // 
            jobLogCategory.errorT(jobLogLocation,"Unexpected exception during job execution.\n" +
                 printStackTrace(sre) + "\n" +
                     "Job failed.");
            Date jobEnded = new Date();
            db.jobEnded(jobId, JobStatus.ERROR, ctx.getReturnCode(), jobEnded, (cpuEnd - cpuStart), (allocationEnd - allocationStart));
            mEnvironment.getEventManager().raiseEvent(Event.EVENT_JOB_FINISHED, jobId.toString(), JobStatus.ERROR.toString(), jobEnded);
        
        } catch (Exception t) {

            if (location.beDebug()) {
                location.debugT("Job \"" + jobId.toString() + "\" ended with errors (unexpected exception).");
            }
            
            if (hasSAPJvmMonitoring) {
                cpuEnd = getCurrentCPU();
                allocationEnd = getThreadMemoryInfo();
            }
            
            // $JL-EXC$
            // Throwable required here in order to log RuntimeExceptions
            // DM, 9.11.2006: Changed from Throwable to Exception. We should
            //                not catch Errors here but leave it to the 
            //                infrastructre (e.g. OutOfMemoryError)
            //
        	jobLogCategory.errorT(jobLogLocation,"Unexpected exception during job execution.\n" +
        			     printStackTrace(t) + "\n" +
					     "Job failed.");
            Date jobEnded = new Date();
            db.jobEnded(jobId, JobStatus.ERROR, ctx.getReturnCode(), jobEnded, (cpuEnd - cpuStart), (allocationEnd - allocationStart));
            mEnvironment.getEventManager().raiseEvent(Event.EVENT_JOB_FINISHED, jobId.toString(), JobStatus.ERROR.toString(),jobEnded);                

        } finally {
            
            if (hasSAPJvmMonitoring && cpuEnd == 0) {
                // if there is an exception we may get slightly wrong data
                //
                cpuEnd = getCurrentCPU();
                allocationEnd = getThreadMemoryInfo();
            }
            if (hasSAPJvmMonitoring && location.beDebug()) {
                location.debugT("Job \"" + ji.getName() + "\": CPU: " + (cpuEnd - cpuStart) + " Allocation: " + (allocationEnd - allocationStart));
            }
            
            // make final entry in log and close log
            //
            logger.info("Job " + ji.getName() + " (ID: " + jobId.toString() + ", JMS ID: " + jmsMessageID + ") ended on " + dateFormatter.format(new Date()));
            mEnvironment.getJobLoggingManager().closeCategory(jobLogCategory);
            mEnvironment.getJobLoggingManager().closeLocation(jobLogLocation);
            
            // unregister this job as event listener
            mEnvironment.getEventManager().unregisterEventSubscriber(sub);
            
            // make sure the JDK logger is returned to the pool
            //
            ctx.returnLogger();
        }
        
    }
    
    private long getCurrentCPU() {
        
        ThreadTimeInfo ti = VmInfo.getThreadTimeInfo(Thread.currentThread());
        return ti.getCpuTime();
    }
    
    private long getThreadMemoryInfo() {
            
        ThreadMemoryInfo tm = VmInfo.getThreadMemoryInfo(Thread.currentThread());
        return tm.getMemoryConsumption();
    }
    
    private String printStackTrace(Throwable t) {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	PrintWriter writer = new PrintWriter(bos);
    	t.printStackTrace(writer);
    	writer.flush();
    	return bos.toString();
    }
}
