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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduler.runtime.JobParameterVerifyer;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobExecutor;
import com.sap.scheduler.runtime.JobExecutorException;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.MDBJobDefinition;
import com.sap.scheduler.runtime.NoSuchJobDefinitionException;
import com.sap.scheduler.runtime.ParameterValidationException;
import com.sap.scheduler.runtime.SchedulerID;
import com.sap.scheduler.runtime.SchedulerRuntimeException;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.PrincipalIterator;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.services.scheduler.runtime.db.DBHandler;
import com.sap.engine.services.scheduleradapter.jobdeploy.MessageSelectorParser;

/**
 * This class is used to trigger MDB jobs.
 *
 * @author Dirk Marwinski
 *
 */
public class MDBJobExecutor implements JobExecutor {


    private final static Location location = Location.getLocation(MDBJobExecutor.class);
    private final static Category category = LoggingHelper.SYS_SERVER;

    // JNDI names for JMS for external clients
    //
    public static final String FACTORY_NAME = "jmsfactory/default/QueueConnectionFactory";

    public static final String QUEUE_NAME = "jmsqueues/default/JobQueue";

    public static final String INITIAL_CONTEXT_FACTORY = "com.sap.engine.services.jndi.InitialContextFactoryImpl";

    public static final int MAX_RECONNECT_ATTEMPTS = 10;
    public static final long SLEEP_TIME_BETWEEN_RECONNECT_ATTEMPTS = 2000;
    
    private QueueSession mQueueSession;

    private QueueSender mQueueSender;

    private Environment mEnvironment;

    private boolean jmsInitialized = false;

    private DBHandler mDB = null;

    /**
	 *
	 */
	public MDBJobExecutor(Environment env) {
		mEnvironment = env;
        mDB = mEnvironment.getDBHandler();
	}

    public boolean initjms(boolean force) {

        if (force) {
            jmsInitialized = false;
        }
        
        if (jmsInitialized) {
            return true;
        }

        int attempt = 0;
        while (!jmsInitialized && attempt < MAX_RECONNECT_ATTEMPTS) {

            attempt++;
            try {
                initjms2();

            } catch (Exception e) {
                
                if (attempt == MAX_RECONNECT_ATTEMPTS) {
                    // log error 
                    category.logThrowableT(Severity.ERROR, location, "Could not connect ot JMS queue with " + MAX_RECONNECT_ATTEMPTS + ". Giving up.",e);
                } else {
                    
                    if (location.beDebug()) {
                        location.traceThrowableT(Severity.DEBUG, "Unable to conenct to JMS queue. Waiting " + SLEEP_TIME_BETWEEN_RECONNECT_ATTEMPTS + " before trying the next reconnect attempt.",e );
                    }
                    try {
                        // we want to sleep some time to give a chance for the server to become working,
                        Thread.sleep(SLEEP_TIME_BETWEEN_RECONNECT_ATTEMPTS);
                    } catch (InterruptedException e2) {
                        // could be invoked by the kernel if the server is stopping, since it is going down
                        break;
                    }
                }

            }
        }
        return jmsInitialized;
    }
            

    private void initjms2() 
                     throws JMSException,
                            NamingException {
        
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        
        QueueConnection conn = null;
        
        // In some cases we get a wrong context class loader (which should
        // not really happen here, it is a bug). Anyway we try to fix things
        // by explicitly setting the classloader known to work
        //
        final ClassLoader storedContextCL = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            
            Context ctx = new InitialContext(props);
        
            QueueConnectionFactory fac = (QueueConnectionFactory) ctx
                    .lookup(FACTORY_NAME);
        
            conn = fac.createQueueConnection();
        
            Queue queue = (Queue) ctx.lookup(QUEUE_NAME);
        
            mQueueSession = conn.createQueueSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            conn.start();
        
            mQueueSender = mQueueSession.createSender(queue);
        
            if (location.beDebug()) {
                location.debugT("Connection to queue " + QUEUE_NAME
                        + " established.");
            }

        } catch (JMSException e) {
            // try again later
            mQueueSender = null;
            mQueueSession = null;
            throw e;
        } catch (NamingException e) {
            // try again later
            mQueueSender = null;
            mQueueSession = null;
            throw e;
        } finally {
            Thread.currentThread().setContextClassLoader(storedContextCL);
        }

        if (mQueueSender != null && mQueueSession != null) {
            jmsInitialized = true;
        }

    }

    public JobID executeJob(JobDefinitionID jobDefId, 
            JobParameter[] jobParameters, 
            Integer retentionPeriod,
            JobID parentID, 
            SchedulerID schedulerId,
            String runAsUser,
            SchedulerTaskID schedTaskID,
            String vendorData ) throws NoSuchJobDefinitionException,
                                                ParameterValidationException,
                                                JobExecutorException {

        return executeJob(jobDefId, 
                          jobParameters,
                          retentionPeriod,
                          parentID,
                          schedulerId,
                          runAsUser,
                          schedTaskID,
                          vendorData,
                          null);
    }

    public JobID executeJob(JobDefinitionID jobDefId, 
                       JobParameter[] jobParameters, 
                       Integer retentionPeriod,
                       JobID parentID, 
                       SchedulerID schedulerId,
                       String runAsUser,
                       SchedulerTaskID schedTaskID,
                       String vendorData,
                       JobID jobId) throws NoSuchJobDefinitionException,
                                                           ParameterValidationException,
                                                           JobExecutorException {

        
        // ------------------------------------------------------------------
        // Validate job and job parameters
        // ------------------------------------------------------------------
        
        try {
            JobParameterVerifyer.verifyParameters(jobDefId, jobParameters, mEnvironment);
        } catch (SQLException sql) {
            throw new JobExecutorException("Unable to validate submitted job due to " +
                    "underlying infrastructure problem.", sql);
        }

        JobDefinition jd = null;
        try {
            jd = mEnvironment.getJobDefinitionHandler().getJobDefinitionById(jobDefId);
        } catch (SQLException sql) {
            throw new JobExecutorException("Unable to submitted job due to " +
                    "underlying infrastructure problem.", sql);
        }

        if (jd==null) {
            throw new ParameterValidationException("There is no job definition with id \"" + jobDefId.toString() + "\".");
        }
        if (jd.getRemoveDate() != null) {
            throw new ParameterValidationException("The job implementation for job \"" + jd.getJobDefinitionName() + "\" has been undeployed and is no longer available.");
        }

        if (location.beDebug()) {
            location.debugT("Job definition for job \"" + jd.getJobDefinitionName() + "\" found.");
        }
        
        MDBJobDefinition mjd = new MDBJobDefinition(jd);

        
        //----------------------------------------------------
        // Add job to active job table
        //----------------------------------------------------

        if (retentionPeriod == null) {
            retentionPeriod = new Integer(jd.getRetentionPeriod());
        }
        
        if (jobId == null) {
            jobId = JobID.newID();
        }
        
        // We need to check if it is a ZeroAdmin-Task (user is then always null)
        if (runAsUser == null && schedTaskID != null) {                
            try {
                if ( isZeroAdminTask(schedTaskID) ) {
                    runAsUser = getAdministratorUser(); 
                }
            } catch (UMException ume) {
                category.logT(Severity.ERROR, location, "Error while accessing user for job '"+jobId+"' triggered from ZeroAdmin-Task '"+schedTaskID+"'.");
                location.traceThrowableT(Severity.ERROR, ume.getMessage(), ume);
            } catch (TaskDoesNotExistException tdnee) {
                category.logT(Severity.ERROR, location, "Error while accessing ZeroAdmin-Task '"+schedTaskID+"' for job '"+jobId+"'.");
                location.traceThrowableT(Severity.ERROR, tdnee.getMessage(), tdnee);
            } catch (Exception e) {
                category.logT(Severity.ERROR, location, "Error while triggering job '"+jobId+"' from ZeroAdmin-Task '"+schedTaskID+"'.");
                location.traceThrowableT(Severity.ERROR, e.getMessage(), e);
            }
            // if an exception has been thrown, the userName keeps null --> job will be triggered with user under which JMS runs
        }         
        
        Job ji = new Job(jobId,
                         jd.getJobDefinitionId(),
                         schedulerId,
                         jd.getJobDefinitionName().getName(),
                         JobStatus.STARTING,
                         null,                    // start date
                         null,                    // end date
                         new Date(),              // submit date
                         null,                    // node
                         (short)0,                // return code
                         runAsUser,                    // user id
                         parentID,
                         vendorData,                    // vendor data
                         false,
                         retentionPeriod.intValue(),
                         schedTaskID);
                
        try {
            mDB.addJob(ji, jobParameters);
        } catch (SQLException sql) {
            category
                    .logThrowableT(
                            Severity.ERROR,
                            location,
                            "Unable to write job information to database. Job \"" + jd.getName() + "\" not queued.",
                            sql);
            throw new JobExecutorException("Datatbase error. Job not submitted.",
                    sql);
        }
        
        if (location.beDebug()) {
            location.debugT("Added job information for job \"" + jd.getName() + "\" with job id \"" + jobId.toString() + "\" to database.");
        }

        sendMessage(ji,jd,mjd,schedulerId);
        
        mEnvironment.getEventManager().raiseEvent(Event.EVENT_JOB_STARTING, jobId.toString(), JobStatus.STARTING.toString(), new Date());
        
        return jobId;
    }
    
    //----------------------------------------------------
    // Send JMS message in order to start job
    //----------------------------------------------------

    private synchronized void sendMessage(
                              Job job, 
                              JobDefinition jd, 
                              MDBJobDefinition mjd, 
                              SchedulerID schedulerId) 
                                      throws JobExecutorException {
        
        JobID jobId = job.getId();

        // make sure the connection to the queue is available
        //
        boolean initialized = initjms(false);

        if (!initialized) {
            // connect failed, clean up and return
            //
            category
                    .errorT(
                            location,
                            "Unable to send JMS message in order to trigger job. Connection to queue could not be established.");
            try {
                // mark job as bad
                //
                mDB.updateStatus(jobId, JobStatus.ERROR);
            } catch (SQLException sql) {
                category
                        .logThrowableT(
                                Severity.FATAL,
                                location,
                                "Unable to start job and job table inconsistent. Job status for job \""
                                        + jobId.toString()
                                        + "\" should have been set to ERROR but this failed due to a database exception.",
                                sql);
                throw new JobExecutorException(
                        "Fatal error. Job status for job \""
                                + jobId.toString()
                                + "\" in database is wrong. Job did not run.",
                        sql);
            }
            throw new JobExecutorException("Job could not be started. JMS message could not be sent.", null);
        }

        try {
            sendMessage2(job, jd, mjd, schedulerId);
        } catch (JMSException jms) {
            
            category.logThrowableT(Severity.WARNING, location,
                    "Unable to send jms message to job. Trying to re-acquire jms connection.",
                    jms);

            // try to recreate connection and send message again.
            
            try {
                initjms(true);
                sendMessage2(job, jd, mjd, schedulerId);
            } catch (JMSException jms2) {
                category.logThrowableT(Severity.ERROR, location,
                              "Unable to send JMS message in order to trigger job.", jms2);
                try {
                    // mark job as bad
                    //
                    mDB.updateStatus(jobId, JobStatus.ERROR);
                } catch (SQLException sql) {
                    category
                            .logThrowableT(
                                    Severity.FATAL,
                                    location,
                                    "Unable to start job and job table inconsistent. Job status for job \""
                                            + jobId.toString()
                                            + "\" should have been set to ERROR but this failed due to a database exception.",
                                    sql);
                }
                throw new JobExecutorException(
                        "Job could not be started. JMS message could not be sent.",
                        jms2);
            }
        }
    }
    
    private void sendMessage2(
                        Job job, 
                        JobDefinition jd, 
                        MDBJobDefinition mjd, 
                        SchedulerID schedulerId) 
                                      throws JMSException {

        JobID jobId = job.getId();
        Message mm = mQueueSession.createMessage();

        if (location.beDebug()) {
            location.debugT("Setting job name to \"" + jd.getJobDefinitionName().getName()
                    + "\". Provider is \"" + schedulerId + "\".");
        }
        mm.setStringProperty(MessageSelectorParser.JOB_DEFINITION, jd.getJobDefinitionName().getName());
            
        // if this is a version 2 job, we need to send the application name
        // as well
        
        if (mjd.getVersion().equals(MDBJobDefinition.JobVersion.v2)) {
            if (location.beDebug()) {
                location.debugT("Setting application name to \"" + jd.getJobDefinitionName().getApplicationName() + "\".");
            }
            mm.setStringProperty(MessageSelectorParser.APPLICATION_NAME, jd.getJobDefinitionName().getApplicationName());
        }
            
        mm.setStringProperty("provider-name", schedulerId.toString());
        mm.setStringProperty(MessageSelectorParser.JOB_ID, jobId.toString());

        if (location.beDebug()) {
            location.debugT("About to send JMS message to job \""
                    + jd.getJobDefinitionName().getName() + "\" with id \"" + jobId.toString() + "\".");
        }
        mQueueSender.send(mm);

        if (location.beDebug()) {
            location.debugT("JMS message successfully sent to job \""
                    + jd.getJobDefinitionName().getName() + "\" with id \"" + jobId.toString()
                    + "\" successfully sent.");
        }

    }
    
    
    private String getAdministratorUser() throws UMException {
        Iterator it = UMFactory.getRoleFactory().getRoleByUniqueName("Administrator").getUserMembers(true);
        it = new PrincipalIterator(it, PrincipalIterator.ITERATOR_TYPE_PRINCIPALS);
        
        while (it.hasNext()) {
            IUser iUser = (IUser)it.next();
            IUserAccount[] accounts = iUser.getUserAccounts();
            if (accounts != null && accounts.length > 0)        {
                return accounts[0].getLogonUid();
            }
        }
        
        // user data inconsistent
        throw new UMException("Error while accessing the Administrator-user for a ZeroAdmin-Task. Please see SAP note 1016283 \"UME Consistency Check Tool\".");        
    }
    
    
    private boolean isZeroAdminTask(SchedulerTaskID taskID) throws TaskDoesNotExistException, SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String stmt = "select TASK_SOURCE from BC_JOB_TASKS where TASK_ID = ?";
            con = mEnvironment.getDataSource().getConnection();
            ps = con.prepareStatement(stmt);
            ps.setBytes(1, taskID.getBytes());            
            rs = ps.executeQuery();

            if ( !rs.next() ) {
                throw new TaskDoesNotExistException(taskID, null);
            }
            
            final short taskSource = rs.getShort("TASK_SOURCE");
            
            if ( rs.next() ) {
                throw new SchedulerRuntimeException("More than 1 task with id " + taskID +" were found in the database." +
                    " Probably this means that the database is not in cosistent state." +
                    " Please analyse the database and fix the problem.");
            }
            
            if (taskSource == SchedulerTask.TASK_SOURCE_ZERO_ADMIN) {
                return true;
            } else {
                return false;
            }
            
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
    
}
