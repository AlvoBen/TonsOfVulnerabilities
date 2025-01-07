/*
 * Created on 29.12.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.scheduleradapter.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.naming.NamingException;

import com.sap.engine.services.dc.api.Client;
import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.engine.services.dc.api.lcm.LCMStatus;
import com.sap.engine.services.dc.api.lcm.LifeCycleManager;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduleradapter.jobdeploy.MessageSelectorParser;
import com.sap.scheduler.api.SchedulerAdministrator;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.TaskStatus;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionName;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobStatus;


public class SchedulerCheckCommand extends AbstractCommand {
    private static final String NAME = "scheduler_check"; 
    
    private Environment m_env = null;
              
    private static StringBuilder m_usageBuffer = new StringBuilder(); 
    static {
      m_usageBuffer.append("Performs checks related to health of the scheduler.").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("Usage: scheduler_check <-[j]obs | -[t]asks>").append(LINE_WRAP);
      m_usageBuffer.append("-[j]obs   - Checks if there are jobs in status unknown, error").append(LINE_WRAP);
      m_usageBuffer.append("            or starting. If a job is in starting it will also").append(LINE_WRAP);
      m_usageBuffer.append("            be checked if there are still jms-messages undelivered").append(LINE_WRAP);
      m_usageBuffer.append("            or the corresponding applications are not started").append(LINE_WRAP);
      m_usageBuffer.append("-[t]asks  - Checks if there are tasks which are implicitly set to").append(LINE_WRAP);
      m_usageBuffer.append("            status hold after several failed jobs of this task").append(LINE_WRAP);
    }

    
    public SchedulerCheckCommand(Environment env) {
        super();
        m_env = env;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.sap.engine.interfaces.shell.Command#exec(com.sap.engine.interfaces.shell.Environment,
     *      java.io.InputStream, java.io.OutputStream, java.lang.String[])
     */
    public void exec(com.sap.engine.interfaces.shell.Environment env, InputStream input, OutputStream output, String[] params) {
        PrintStream out = new PrintStream(output, true); // true for auto-flush
        int executeValue = ARGS_INVALID;
        
        if ( (executeValue = parseArgs(params)) == ARGS_INVALID ) {
            out.println(getHelpMessage());
            return;
        }

        if (executeValue == 1) {
            analyseJobs(out);            
        } else if (executeValue == 2) {
            analyseTasks(out);
        } 
    } // exec
    
    
    private void analyseJobs(PrintStream out) {
        QueueConnection queueConn = null;
        QueueSession queueSession = null;
        QueueBrowser queueBrowser = null;
        
        try {
            // jobs in status unknown
            JobFilter filter = new JobFilter();
            filter.setJobStatus(JobStatus.UNKNOWN);
            int count = printJobs(filter, m_env, out, FORMAT_FOR_STATUS);
            if (count == 0) {
                out.println("There are no jobs in status "+JobStatus.UNKNOWN);
            } else {
                out.println(count+" jobs in status "+JobStatus.UNKNOWN);
            }
            out.println("-----------------------------------");
            
            // jobs in status error
            filter.setJobStatus(JobStatus.ERROR);
            count = printJobs(filter, m_env, out, FORMAT_FOR_STATUS);
            if (count == 0) {
                out.println("There are no jobs in status "+JobStatus.ERROR);
            } else {
                out.println(count+" jobs in status "+JobStatus.ERROR);
            }
            out.println("-----------------------------------");
            
            // jobs in status starting
            // special handling for jobs in status starting
            filter.setJobStatus(JobStatus.STARTING);
            Job[] startingJobs = getJobs(filter, m_env);
                        
            // starting jobs
            if (startingJobs.length != 0) {
                printJobsFormatted(startingJobs, out, true); 
                out.println(startingJobs.length+" jobs in status "+JobStatus.STARTING.toString());
                out.println();
                
                // Compare with jms-queue                       
                queueConn = lookupSchedulerJMS();
                queueSession = getQueueSession(queueConn);
                queueBrowser = getQueueBrowser(queueSession);
                    
                Enumeration msgs = queueBrowser.getEnumeration();
                Map<JobID, JobDefinitionName> mapJMS = new HashMap<JobID, JobDefinitionName>();
                
                if (!msgs.hasMoreElements()) {
                    out.println("No messages in scheduler jms-queue");
                } else {
                    while (msgs.hasMoreElements()) {
                        Message msg = (Message) msgs.nextElement();
                        Enumeration enumeration = msg.getPropertyNames();
                        
                        if (enumeration != null) {
                            JobID jobId = null;
                            String jobDefName = null;
                            String appName = null;
                            while (enumeration.hasMoreElements()) {
                                String prop = (String) enumeration.nextElement();
                                if (prop.equals(MessageSelectorParser.JOB_ID)) {
                                    jobId = JobID.parseID((String) msg.getObjectProperty(prop));
                                } else if (prop.equals(MessageSelectorParser.JOB_DEFINITION)) {
                                    jobDefName = (String) msg.getObjectProperty(prop);
                                } else if (prop.equals(MessageSelectorParser.APPLICATION_NAME)) {
                                    appName = (String) msg.getObjectProperty(prop);
                                }
                            } // inner while
                            
                            // it is possible that the application-name is not set
                            JobDefinitionName jobDefinitionName = null;
                            if (appName == null) {
                                jobDefinitionName = new JobDefinitionName(jobDefName);
                            } else {
                                jobDefinitionName = new JobDefinitionName(appName, jobDefName);
                            }
                            mapJMS.put(jobId, jobDefinitionName);
                        }
                    } // outer while
                } // else

                // Compare the starting jobs from db with the jobs in jms-queue
                // Possibly there are 3 different categories:
                // 1.) jobs which are on DB in starting and in jms-queue
                //     1.1.) corresponding application is started --> jms didn't deliver the message
                //     1.2.) corresponding application is not started
                // 2.) jobs which are on DB in starting and not in jms-queue

                // access the LifeCycleManager to get info about the applications
                LifeCycleManager lifeCycleManager = null;
                try {
                    Client client = ClientFactory.getInstance().createClient();
                    lifeCycleManager = client.getLifeCycleManager();//get manager for starting/stopping/getting status of applications
                } catch (Throwable t) {
                    t.printStackTrace(out);
                }

                                
                for (int i = 0; i < startingJobs.length; i++) {
                    JobDefinitionName jobDefinitionName = null;
                    if ( (jobDefinitionName = mapJMS.get(startingJobs[i].getId())) != null ) {
                        JobDefinition jobDef = m_env.getJobDefinitionHandler().getJobDefinitionByName(jobDefinitionName);
                        // JobDefinition might be null if there are the same JobDefinitions in different application
                        if (jobDef != null) {
                            String name = jobDef.getApplication();
                            // check with deploy service if application is started      
                            LCMStatus lcmStatus = null;
                            int idx = name.indexOf('/');
                            
                            if (idx != -1) {
                                String providerName = name.substring(0, idx);
                                String appName = name.substring(idx+1);
                                lcmStatus = lifeCycleManager.getLCMStatus(appName, providerName); 
                            } else {
                                out.println("JMS-message with JobId '"+startingJobs[i].getId()+"' was not delivered");
                                out.println("Application name '"+name+"' is not correct. Not able to check if application is started");
                                return;
                            }
                            
                            if (LCMStatus.STARTED.equals(lcmStatus)) {
                                // case 1.1.)
                                out.println("JMS-message with JobId '"+startingJobs[i].getId()+"' was not delivered");
                                out.println("although the application '"+name+"' is in status '"+LCMStatus.STARTED.getName()+"'");                                
                            } else {
                                // case 1.2.)
                                out.println("JMS-message with JobId '"+startingJobs[i].getId()+"' was not delivered");
                                out.println("because the application '"+name+"' is in status '"+lcmStatus.getName()+"'");
                                out.println("Details: "+lcmStatus.getLCMStatusDetails().getDescription());
                            }
                        } else {
                            out.println("No JobDefinition found in DB for JobDefinitionName '"+jobDefinitionName.toString()+"'");
                        }
                    } else {
                        // case 2.)
                        out.println("Job with JobId '"+startingJobs[i].getId()+"' is in status '"+JobStatus.STARTING.toString()+"'");
                        out.println("but there's no undelivered JMS-message");
                    }
                }
            } else {
                out.println("There are no jobs in status "+JobStatus.STARTING.toString());
                out.println();
            }
            
        } catch (Exception e) {
            e.printStackTrace(out);
        } finally {
            try {
                closeSchedulerJMS(queueConn, queueBrowser, queueSession);
            } catch (JMSException e) {
                e.printStackTrace(out);
            }
        }
    }
    
    
    private void analyseTasks(PrintStream out) {
        SchedulerAdministrator schedulerAdmin = null;
        try {
            schedulerAdmin = lookupSchedulerAdministrator();
        } catch (NamingException e) {
            e.printStackTrace(out);
        }
        
        SchedulerTask[] tasks = schedulerAdmin.getAllSchedulerTasks(TaskStatus.hold);
        ArrayList<SchedulerTask> list = new ArrayList<SchedulerTask>();
        for (int i = 0; i < tasks.length; i++) {
            // 17 ist the non-public constant for setting an task to hold due to error reasons
            if (tasks[i].getTaskStatus().getDescriptionValue() == 17) {
                list.add(tasks[i]);    
            }                
        }
        if (list.isEmpty()) {
            out.println("There are no tasks which have been set to hold due to error reasons");
        } else {
            out.println("The following tasks have been set to hold due to error reasons");
            out.println(formatSchedulerTasks((SchedulerTask[])list.toArray(new SchedulerTask[list.size()])));   
        }
    }
    
    private int parseArgs(String[] args) {    
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("-jobs") || args[0].equalsIgnoreCase("-j")) {
                return 1;
            } 
            else if (args[0].equalsIgnoreCase("-tasks") || args[0].equalsIgnoreCase("-t")) {
                return 2;
            }
        }
        return ARGS_INVALID;
    } // parseArgs    
    
    // ---------------------------------------------------------------------------

    public String getHelpMessage() {
      return m_usageBuffer.toString();
    }

    public String getName() {
      return NAME;
    }
    
  }
