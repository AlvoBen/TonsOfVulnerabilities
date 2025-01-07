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
import java.sql.SQLException;
import java.util.ArrayList;

import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobException;


public class SchedulerJobCommand extends AbstractCommand {
    private static final String NAME = "scheduler_job"; 
    
    private Environment m_env = null;
          
    private static StringBuilder m_usageBuffer = new StringBuilder(); 
    static {
      m_usageBuffer.append("Command for listing jobs.").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("Usage: scheduler_job <-[l]ist> [[s]tarting | c[o]mpleted | [e]rror").append(LINE_WRAP);
      m_usageBuffer.append("                                [u]nknown | [c]ancelled]").append(LINE_WRAP);
      m_usageBuffer.append("                     <-[t]askId taskId>").append(LINE_WRAP);
      m_usageBuffer.append("                     <-[d]etail jobId>").append(LINE_WRAP);
      m_usageBuffer.append("                     <-[j]obLog jobId>").append(LINE_WRAP);
      m_usageBuffer.append("                     <-[n]ame jobName>").append(LINE_WRAP);
      m_usageBuffer.append("                     <-d[e]f [[u]undeployed | -[n]ame name]>").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("-[l]ist             - Dumps out all jobs of JobStatus running (default)").append(LINE_WRAP);
      m_usageBuffer.append("       [s]tarting   - Dumps out all jobs of JobStatus starting").append(LINE_WRAP);
      m_usageBuffer.append("       c[o]mpleted  -                                 completed").append(LINE_WRAP);
      m_usageBuffer.append("       [e]rror      -                                 error").append(LINE_WRAP);
      m_usageBuffer.append("       [u]nknown    -                                 unknown").append(LINE_WRAP);
      m_usageBuffer.append("       [c]ancelled  -                                 cancelled").append(LINE_WRAP);
      m_usageBuffer.append("-[t]askId <taskId>  - Lists the jobs for a given taskId").append(LINE_WRAP);
      m_usageBuffer.append("-[d]etail <jobId>   - Lists the job-details for a given jobId").append(LINE_WRAP);
      m_usageBuffer.append("-[j]obLog <jobId>   - Lists the job-log for a given jobId").append(LINE_WRAP);
      m_usageBuffer.append("-[n]ame <jobName>   - Dumps out all jobs for a given job-name").append(LINE_WRAP);
      m_usageBuffer.append("-d[e]f              - Lists all active job-definitions (default)").append(LINE_WRAP);
      m_usageBuffer.append("      [u]ndeployed  - Lists all undeployed job-definitions").append(LINE_WRAP);
      m_usageBuffer.append("      -[n]ame <name>- Lists the job-definition for a given job-definition name").append(LINE_WRAP);
    }

    
    public SchedulerJobCommand(Environment env) {
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

        JobFilter filter = new JobFilter();
        try {
            if (executeValue == 1) { // list all running jobs
                filter.setJobStatus(JobStatus.RUNNING);                
                int count = printJobs(filter, m_env, out, FORMAT_FOR_STATUS);
                if (count == 0) {
                    out.println("There are no jobs in status "+JobStatus.RUNNING);
                } else {
                    out.println(count+" jobs in status "+JobStatus.RUNNING);
                }
            } 
            else if (executeValue == 2) { // list all starting jobs
                filter.setJobStatus(JobStatus.STARTING);
                int count = printJobs(filter, m_env, out, FORMAT_FOR_STATUS);
                if (count == 0) {
                    out.println("There are no jobs in status "+JobStatus.STARTING);
                } else {
                    out.println(count+" jobs in status "+JobStatus.STARTING);
                }
            } 
            else if (executeValue == 3) { // list all completed jobs
                filter.setJobStatus(JobStatus.COMPLETED);
                int count = printJobs(filter, m_env, out, FORMAT_FOR_STATUS);     
                if (count == 0) {
                    out.println("There are no jobs in status "+JobStatus.COMPLETED);
                } else {
                    out.println(count+" jobs in status "+JobStatus.COMPLETED);
                }
            } 
            else if (executeValue == 4) { // list all error jobs
                filter.setJobStatus(JobStatus.ERROR);
                int count = printJobs(filter, m_env, out, FORMAT_FOR_STATUS);   
                if (count == 0) {
                    out.println("There are no jobs in status "+JobStatus.ERROR);
                } else {
                    out.println(count+" jobs in status "+JobStatus.ERROR);
                }
            } 
            else if (executeValue == 5) { // list all unknown jobs
                filter.setJobStatus(JobStatus.UNKNOWN);
                int count = printJobs(filter, m_env, out, FORMAT_FOR_STATUS);
                if (count == 0) {
                    out.println("There are no jobs in status "+JobStatus.UNKNOWN);
                } else {
                    out.println(count+" jobs in status "+JobStatus.UNKNOWN);
                }
            } 
            else if (executeValue == 6) { // list all cancelled jobs
                filter.setJobStatus(JobStatus.CANCELLED);
                int count = printJobs(filter, m_env, out, FORMAT_FOR_STATUS);
                if (count == 0) {
                    out.println("There are no jobs in status "+JobStatus.CANCELLED);
                } else {
                    out.println(count+" jobs in status "+JobStatus.CANCELLED);
                }
            } 
            else if (executeValue == 7) { // list all jobs for a given taskId
                SchedulerTaskID taskId = null;
                try {
                    taskId = SchedulerTaskID.parseID(params[1]);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace(out);
                    return;
                }
                filter.setSchedulerTaskId(taskId);
                int count = printJobs(filter, m_env, out, FORMAT_FOR_TASK_ID);
                if (count == 0) {
                    out.println("There are no jobs for SchedulerTaskId '"+params[1]+"'");
                } else {
                    out.println(count+" jobs in for SchedulerTaskId '"+params[1]+"'");
                }
            }     
            else if (executeValue == 8) { // list the job details for a given jobId
                JobID jobId = null;
                try {
                    jobId = JobID.parseID(params[1]);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace(out);
                    return;
                }
                Job job = m_env.getJobExecutionRuntime().getJob(jobId);
                out.println("Job-details for jobId '"+params[1]+"'"+LINE_WRAP);
                out.println(job.toFormattedString());                 
            } 
            else if (executeValue == 9) { // list the job-log for a given jobId  
                JobID jobId = null;
                try {
                    jobId = JobID.parseID(params[1]);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace(out);
                    return;
                }
                printJobLog(jobId, out);         
            } 
            else if (executeValue == 10) { // list the jobs for a given job-name
                filter.setName(params[1]);
                int count = printJobs(filter, m_env, out, FORMAT_FOR_NAME);
                if (count == 0) {
                    out.println("There are no jobs for job-name '"+params[1]+"'");
                } else {
                    out.println(count+" jobs with job-name '"+params[1]+"'");
                }
            } 
            else if (executeValue == 11) { // list all active job definitions
                JobDefinition[] jobDefs = m_env.getJobDefinitionHandler().getJobDefinitions();  
                
                // remove the undeployed JobDefinitions
                ArrayList<JobDefinition> list = new ArrayList<JobDefinition>();
                for (int i = 0; i < jobDefs.length; i++) {
                    if (jobDefs[i].getRemoveDate() == null) { // jobDef is still active
                        list.add(jobDefs[i]);
                    }
                }                    
                
                if (list.size() != 0) {
                    for (int i = 0; i < list.size(); i++) {
                        out.println(list.get(i).toString());
                        out.println("------------------------------------");
                    }       
                    out.println(list.size()+" active job-definitions found");
                } else {
                    out.println("No active job-definitions found");
                }             
            } 
            else if (executeValue == 12) { // list all undeployed job definitions
                JobDefinition[] jobDefs = m_env.getJobDefinitionHandler().getJobDefinitions();  
                
                // remove the active JobDefinitions
                ArrayList<JobDefinition> undeployedList = new ArrayList<JobDefinition>();
                for (int i = 0; i < jobDefs.length; i++) {
                    if (jobDefs[i].getRemoveDate() != null) { // jobDef is undeployed
                        undeployedList.add(jobDefs[i]);
                    }
                }                    
                
                if (undeployedList.size() != 0) {
                    for (int i = 0; i < undeployedList.size(); i++) {
                        out.println(undeployedList.get(i).toString());
                        out.println("------------------------------------");
                    }       
                    out.println(undeployedList.size()+" undeployed job-definitions found");
                } else {
                    out.println("No undeployed job-definitions found");
                }             
            } 
            else if (executeValue == 13) { // list job definition for a given job-definition name
                JobDefinition jobDef = m_env.getJobDefinitionHandler().getJobDefinitionByName(params[2]);             
                if (jobDef != null) {
                    out.println(jobDef.toString());
                } else {
                    out.println("No job-definition found for name '"+params[2]+"'. Maybe there are more");
                    out.println("than 1 job-definitions with this name deployed in separate applications.");
                }             
            } 
        } catch (SQLException e) {
            e.printStackTrace(out);
        } catch (NoSuchJobException e) {
            e.printStackTrace(out);
        } 
    } // exec
    
    
    private int parseArgs(String[] args) {  
        if (args.length < 1 || args.length > 3) {
            return ARGS_INVALID;
        } 
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("-list") || args[0].equalsIgnoreCase("-l")) {
                return 1;
            } else if (args[0].equalsIgnoreCase("-def") || args[0].equalsIgnoreCase("-e")) {
                return 11;
            }
        } 
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("-list") || args[0].equalsIgnoreCase("-l")) {
                if (args[1].equalsIgnoreCase("starting") || args[1].equalsIgnoreCase("s")) {
                    return 2;
                } else if (args[1].equalsIgnoreCase("completed") || args[1].equalsIgnoreCase("o")) {
                    return 3;
                } else if (args[1].equalsIgnoreCase("error") || args[1].equalsIgnoreCase("e")) {
                    return 4;
                } else if (args[1].equalsIgnoreCase("unknown") || args[1].equalsIgnoreCase("u")) {
                    return 5;
                } else if (args[1].equalsIgnoreCase("cancelled") || args[1].equalsIgnoreCase("c")) {
                    return 6;
                } 
            } else if (args[0].equalsIgnoreCase("-taskId") || args[0].equalsIgnoreCase("-t")) {
                return 7;
            } else if (args[0].equalsIgnoreCase("-detail") || args[0].equalsIgnoreCase("-d")) {
                return 8;
            } else if (args[0].equalsIgnoreCase("-jobLog") || args[0].equalsIgnoreCase("-j")) {
                return 9;
            } else if (args[0].equalsIgnoreCase("-name") || args[0].equalsIgnoreCase("-n")) {
                return 10;
            } else if (args[0].equalsIgnoreCase("-def") || args[0].equalsIgnoreCase("-e")) {
                if (args[1].equalsIgnoreCase("undeployed") || args[1].equalsIgnoreCase("u")) {
                    return 12;
                }
            }
            else {
                return ARGS_INVALID;
            }
        }
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("-def") || args[0].equalsIgnoreCase("-e")) {
                if (args[1].equalsIgnoreCase("-name") || args[1].equalsIgnoreCase("-n")) {
                    return 13;
                }
            }
        }
        
        return ARGS_INVALID;
    } // parseArgs
       
    
    private void printJobLog(JobID jobId, PrintStream out) throws SQLException, NoSuchJobException {        
        LogIterator iter = m_env.getJobExecutionRuntime().getJobLog(jobId, null, 1000);
        String chunk = iter.nextChunk();
        if (chunk != null && chunk.length() != 0) {
            out.println(chunk);
        } else {
            out.println("Job with id '"+jobId.toString()+"' has no log entries");
        }
        
        while (iter.hasMoreChunks()) {
            iter = m_env.getJobExecutionRuntime().getJobLog(jobId, iter, 1000); 
            out.println(iter.nextChunk());
        }
    }
    
    
    // ---------------------------------------------------------------------------

    public String getHelpMessage() {
      return m_usageBuffer.toString();
    }

    public String getName() {
      return NAME;
    }
    
  }
