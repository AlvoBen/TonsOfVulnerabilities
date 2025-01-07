package com.sap.engine.services.scheduleradapter.command;

import java.io.PrintStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduler.runtime.mdb.MDBJobExecutor;
import com.sap.scheduler.api.SchedulerAdministrator;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.api.TaskStatus;
import com.sap.scheduler.api.Scheduler.FireTimeEvent;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobIterator;

/**
 * Abstract command, which should be the base-class for all commands.
 */
public abstract class AbstractCommand implements Command {
    protected static final String LINE_WRAP = Environment.LINE_WRAP;
    private static String GROUP = "scheduler";
    
    protected static final int ARGS_INVALID = 0;
    
    protected static final int FORMAT_FOR_STATUS  = 0;
    protected static final int FORMAT_FOR_TASK_ID = 1;
    protected static final int FORMAT_FOR_NAME    = 2;
    

    /**
     * Returns the name of the group the command belongs to.
     * 
     * @return The name of the group of commands, in which this command belongs.
     */
    public String getGroup() {
        return GROUP;
    }

    /**
     * Gives the name of the supported shell providers
     * 
     * @return The Shell providers' names who supports this command.
     */
    public String[] getSupportedShellProviderNames() {
        return new String[] { "InQMyShell" };
    }
    
    
    
    protected SchedulerAdministrator lookupSchedulerAdministrator() throws NamingException {
        InitialContext ctx = new InitialContext();
        return (SchedulerAdministrator) ctx.lookup(Environment.SCHEDULER_ADMINISTRATOR_BINDING_JNDI_NAME);
    }
    
    
    protected QueueConnection lookupSchedulerJMS() throws NamingException, JMSException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, MDBJobExecutor.INITIAL_CONTEXT_FACTORY);
        InitialContext ctx = new InitialContext(props);
        
        QueueConnectionFactory fac = (QueueConnectionFactory) ctx.lookup(MDBJobExecutor.FACTORY_NAME);
        QueueConnection queueConn = fac.createQueueConnection();
        queueConn.start();
        
        return queueConn;
    }
    
    protected QueueSession getQueueSession(QueueConnection queueConn) throws JMSException {
        return queueConn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE); 
    }
       
    
    protected QueueBrowser getQueueBrowser(QueueSession queueSession) throws NamingException, JMSException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, MDBJobExecutor.INITIAL_CONTEXT_FACTORY);
        InitialContext ctx = new InitialContext(props);
        
        Queue queue = (Queue) ctx.lookup(MDBJobExecutor.QUEUE_NAME);        
        return queueSession.createBrowser(queue); 
    }
    
    
    protected void closeSchedulerJMS(QueueConnection queueConn, QueueBrowser queueBrowser, QueueSession queueSession) throws JMSException {
        if (queueBrowser != null) {
            queueBrowser.close();
        }
        if (queueSession != null) {
            queueSession.close();
        }
        if (queueConn != null) {
            queueConn.stop();
            queueConn.close();
        }
    }
    
    
    protected Job[] getJobs(JobFilter filter, Environment env) throws SQLException {
        TreeSet<Job> set = getTreeSet();
        
        JobIterator iter = env.getJobExecutionRuntime().getJobs(filter, null, 1000);
        Job[] jobs = iter.nextChunk();
                
        for (int i = 0; i < jobs.length; i++) {
            set.add(jobs[i]);
        }
        
        while (iter.hasMoreChunks()) {
            iter = env.getJobExecutionRuntime().getJobs(filter, iter, 1000); 
            jobs = iter.nextChunk();
            for (int i = 0; i < jobs.length; i++) {
                set.add(jobs[i]);
            }
        }
        
        return (Job[])set.toArray(new Job[set.size()]);
    }
    
    
    protected int printJobs(JobFilter filter, Environment env, PrintStream out, int formatOption) throws SQLException {
        int countOfJobs = 0;
        
        JobIterator iter = env.getJobExecutionRuntime().getJobs(filter, null, 1000);
        Job[] jobs = iter.nextChunk();
        countOfJobs = jobs.length;
        TreeSet<Job> set = addToTreeSet(jobs);
        
        if (set.size() != 0) {
            if (formatOption == FORMAT_FOR_STATUS) {
                printJobsFormatted( (Job[])set.toArray(new Job[set.size()]), out, true );
            } else if (formatOption == FORMAT_FOR_TASK_ID) {
                printJobsFormattedForTaskId( (Job[])set.toArray(new Job[set.size()]), out, true );            
            } else if (formatOption == FORMAT_FOR_NAME) {
                printJobsFormattedForName( (Job[])set.toArray(new Job[set.size()]), out, true );            
            }
        }
        
        while (iter.hasMoreChunks()) {
            iter = env.getJobExecutionRuntime().getJobs(filter, iter, 1000); 
            set = addToTreeSet(iter.nextChunk());
            countOfJobs = countOfJobs+set.size();
            
            if (set.size() != 0) {
                if (formatOption == FORMAT_FOR_STATUS) {
                    printJobsFormatted( (Job[])set.toArray(new Job[set.size()]), out, false );
                } else if (formatOption == FORMAT_FOR_TASK_ID) {
                    printJobsFormattedForTaskId( (Job[])set.toArray(new Job[set.size()]), out, false );            
                } else if (formatOption == FORMAT_FOR_NAME) {
                    printJobsFormattedForName( (Job[])set.toArray(new Job[set.size()]), out, false );            
                }
            }
        }
        
        return countOfJobs;
        
    }
    
    
    private TreeSet<Job> addToTreeSet(Job[] jobs) {
        TreeSet<Job> set = getTreeSet();
        
        for (int i = 0; i < jobs.length; i++) {
            set.add(jobs[i]);
        }        
        return set;
    }
    
    
    /**
     * Formats SchedulerTasks.
     * 
     * @param tasks the task to format
     * @return the formatted tasks
     */
    protected String formatSchedulerTasks(SchedulerTask[] tasks) {
        StringBuilder strBuf = new StringBuilder();
        strBuf.append(LINE_WRAP);
        //             DDE25D00972711DB8C33000C290E33F3                |
        strBuf.append("-------------------------------------------------------------------------"+LINE_WRAP);
        strBuf.append(" SchedulerTaskID                | Status/Desc   | Source | Task-name     "+LINE_WRAP);
        strBuf.append("-------------------------------------------------------------------------"+LINE_WRAP);
        
        for (int i = 0; i < tasks.length; i++) {
            String statusStr = null;
            // all in all we need ti fill 16 chars
            if ( tasks[i].getTaskStatus().equals(TaskStatus.active)) {
                statusStr = tasks[i].getTaskStatus().toString()+"      "+tasks[i].getTaskStatus().getDescriptionValue()+" "; // 6
            } else if (tasks[i].getTaskStatus().equals(TaskStatus.hold)) {
                statusStr = tasks[i].getTaskStatus().toString()+"        "+tasks[i].getTaskStatus().getDescriptionValue()+" "; // 8
            } else if (tasks[i].getTaskStatus().equals(TaskStatus.finished)) {
                statusStr = tasks[i].getTaskStatus().toString()+"    "+tasks[i].getTaskStatus().getDescriptionValue()+" "; // 4
            }
            strBuf.append(tasks[i].getTaskId()+"|"+statusStr+"|   "+tasks[i].getTaskSource()+"    |"+tasks[i].getName()+LINE_WRAP);
        }
        
        return strBuf.toString();
    }
    
    
    /**
     * Formats FireTimesEvents.
     * 
     * @param tasks the task to format
     * @return the formatted tasks
     */
    protected String formatFireTimesEvents(FireTimeEvent[] events, SchedulerAdministrator schedulerAdmin, Environment env) throws SQLException, TaskDoesNotExistException {
        StringBuilder strBuf = new StringBuilder();
        strBuf.append(LINE_WRAP);
        //             yyyy.MM.dd HH:mm:ss z     |
        strBuf.append("-------------------------------------------------------------------------").append(LINE_WRAP);
        strBuf.append(" Date                     | Task-Name (resp. taken from JobDefinition)   ").append(LINE_WRAP);
        strBuf.append("-------------------------------------------------------------------------").append(LINE_WRAP);
        
        for (int i = 0; i < events.length; i++) {
            if (events[i].filtered) {
                continue; // ignore filtered events
            }
            SchedulerTaskID taskID = events[i].taskId;
            SchedulerTask task = schedulerAdmin.getTask(taskID);
            
            String taskName = null;
            if ( task.getName() != null && !task.getName().equals("") ) {
                taskName = task.getName();
            } else {
                // no task name specified --> take it from JobDefinition
                JobDefinition def = env.getJobExecutionRuntime().getJobDefinitionById(task.getJobDefinitionId());
                taskName = def.getJobDefinitionName().getName();
            }
            
            String fireTimeStr = SchedulerTaskCommand.DATE_FORMATTER_TZ.format(new Date(events[i].time.timeMillis()));
            if (fireTimeStr.length() < 26) {
            	int spaceCount = 26-fireTimeStr.length();
            	for (int j = 0; j < spaceCount; j++) {
            		fireTimeStr = fireTimeStr+" ";
				}
            }
            
            strBuf.append(fireTimeStr).append("|").append(taskName).append(LINE_WRAP);
        }
        
        return strBuf.toString();
    }
    
    
    /**
     * Formats Scheduler jobs.
     * 
     * @param jobs the Job to format
     * @return the formatted jobs
     */
    protected void printJobsFormatted(Job[] jobs, PrintStream out, boolean printHeader) {
        StringBuilder strBuf = new StringBuilder();
        if (printHeader) {
            strBuf.append(LINE_WRAP);
            //             DDE25D00972711DB8C33000C290E33F3 DDE25D00972711DB8C33000C290E33F3
            strBuf.append("------------------------------------------------------------------------------").append(LINE_WRAP);
            strBuf.append(" JobID                          | TaskID                         | Job-name   ").append(LINE_WRAP);
            strBuf.append("------------------------------------------------------------------------------").append(LINE_WRAP);
        } 
        
        for (int i = 0; i < jobs.length; i++) {
            //               DDE25D00972711DB8C33000C290E33F3       
            String taskId = "<no task available>             ";
            if ( jobs[i].getSchedulerTaskId() != null ) {
                taskId = jobs[i].getSchedulerTaskId().toString();
            }
            strBuf.append(jobs[i].getId().toString()).append("|").append(taskId).append("|").append(jobs[i].getName()).append(LINE_WRAP);
        }
        
        out.println(strBuf.toString());
    }
    
    
    protected void printJobsFormattedForName(Job[] jobs, PrintStream out, boolean printHeader) {
        StringBuilder strBuf = new StringBuilder();
        if (printHeader && jobs.length != 0) {
            strBuf.append(LINE_WRAP);
            //             DDE25D00972711DB8C33000C290E33F3 DDE25D00972711DB8C33000C290E33F3 UNKNOWN  
            strBuf.append("---------------------------------------------------------------------------").append(LINE_WRAP);
            strBuf.append(" JobID                          | TaskID                         | Status  ").append(LINE_WRAP);
            strBuf.append("---------------------------------------------------------------------------").append(LINE_WRAP);
        } else if (printHeader && jobs.length == 0) {
            strBuf.append("No jobs available for the specified filter criteria").append(LINE_WRAP);
        }
        
        for (int i = 0; i < jobs.length; i++) {
            //               DDE25D00972711DB8C33000C290E33F3       
            String taskId = "<no task available>             ";
            if ( jobs[i].getSchedulerTaskId() != null ) {
                taskId = jobs[i].getSchedulerTaskId().toString();
            }
            
            // JobStatus-String has the maximal length of 9 characters
            String status = jobs[i].getJobStatus().toString();
            status = addSpaces(status, 9);            
            strBuf.append(jobs[i].getId().toString()).append("|").append(taskId).append("|").append(status).append(LINE_WRAP);
        }
        
        out.println(strBuf.toString());
    }
    
    
    /**
     * Formats Scheduler jobs.
     * 
     * @param jobs the Job to format
     * @return the formatted jobs
     */
    private void printJobsFormattedForTaskId(Job[] jobs, PrintStream out, boolean printHeader) {
        StringBuilder strBuf = new StringBuilder();
        if (printHeader && jobs.length != 0) {
            strBuf.append(LINE_WRAP);
            //             DDE25D00972711DB8C33000C290E33F3 UNKNOWN  
            strBuf.append("-------------------------------------------------------").append(LINE_WRAP);
            strBuf.append(" JobID                          | Status  | Job-name   ").append(LINE_WRAP);
            strBuf.append("-------------------------------------------------------").append(LINE_WRAP);
        } else if (printHeader && jobs.length == 0) {
            strBuf.append("No jobs available for the specified filter criteria").append(LINE_WRAP);
        }
            
        for (int i = 0; i < jobs.length; i++) {
            // JobStatus-String has the maximal length of 9 characters
            String status = jobs[i].getJobStatus().toString();
            status = addSpaces(status, 9);
            strBuf.append(jobs[i].getId().toString()).append("|").append(status).append("|").append(jobs[i].getName()).append(LINE_WRAP);
        }
        
        out.println(strBuf.toString());
    }
    
    
    private String addSpaces(String orgStr, int targetLength) {
        int orgLength = orgStr.length();      
        if ( orgLength < targetLength) {
            for (int j = 0; j < (targetLength-orgLength); j++) {
                orgStr = orgStr+" ";
            }
        }
        return orgStr;        
    }
    
    
    private TreeSet<Job> getTreeSet() {
        return new TreeSet<Job>(new Comparator<Job>() {

            public int compare(Job j1, Job j2) {
                Date d1 = j1.getEndDate();
                Date d2 = j2.getEndDate();

                if (d1 == null && d2 != null) {
                    return 1;
                }

                if (d1 != null && d2 == null) {
                    return -1;
                }

                if (d1 == null && d2 == null) {
                    // try the start date
                    d1 = j1.getStartDate();
                    d2 = j2.getStartDate();
                    if (d1 == null && d2 != null) {
                        return 1;
                    }

                    if (d1 != null && d2 == null) {
                        return -1;
                    }
                    if (d1 == null && d2 == null) {
                        // cannot say
                        return -1;
                    }
                    if (d1.before(d2)) {
                        return -1;
                    } else if (d2.before(d1)) {
                        return 1;
                    } else {
                        return -1;
                    }

                }

                if (d1.before(d2)) {
                    return -1;
                } else if (d2.before(d1)) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

}
