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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.naming.NamingException;

import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.scheduler.api.SchedulerAdministrator;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.api.TaskStatus;
import com.sap.scheduler.api.Scheduler.FireTimeEvent;


public class SchedulerTaskCommand extends AbstractCommand {
    private static final String NAME = "scheduler_task"; 
    private Environment m_env = null;
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy.MM.dd_hh:mm:ss");
    public static final SimpleDateFormat DATE_FORMATTER_TZ = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
              
    private static StringBuilder m_usageBuffer = new StringBuilder(); 
    static {
      m_usageBuffer.append("Command for modifying/listing tasks."+LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("Usage: scheduler_task <-[l]ist> [[a]ctive | [h]old | [f]inished]").append(LINE_WRAP);
      m_usageBuffer.append("                      <-[c]ancel | -h[o]ld | -[r]elease taskId>").append(LINE_WRAP);
      m_usageBuffer.append("                      <-[s]tatus> [detail]").append(LINE_WRAP);
      m_usageBuffer.append("                      <-[p]riorityQueue>").append(LINE_WRAP);
      m_usageBuffer.append("                      <-[d]etail taskId>").append(LINE_WRAP);
      m_usageBuffer.append("                      <-[f]ireTimes [t1 t2 size]>").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("-[l]ist             - Dumps out all tasks of TaskStatus active and hold (default)").append(LINE_WRAP);
      m_usageBuffer.append("       [a]ctive     - Dumps out all tasks of TaskStatus active").append(LINE_WRAP);
      m_usageBuffer.append("       [h]old       - Dumps out all tasks of TaskStatus hold").append(LINE_WRAP);
      m_usageBuffer.append("       [f]inished   - Dumps out all tasks of TaskStatus finished").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("-[c]ancel <taskId>  - Cancels a task (hold or active) for a given taskId").append(LINE_WRAP);
      m_usageBuffer.append("-h[o]ld <taskId>    - Holds an active task for a given taskId").append(LINE_WRAP);
      m_usageBuffer.append("-[r]elease <taskId> - Releases a held task for a given taskId").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("-[s]tatus [detail]  - Lists all status descriptions (default). With a given status").append(LINE_WRAP);
      m_usageBuffer.append("                      value the exact status string will be displayed.").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("-[p]riorityQueue    - Lists the content of the task queue").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("-[d]etail <taskId>  - Lists the task-details for a given taskId").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("-[f]ireTimes [t1 t2 size] - Lists the fireTimes for the next day from now on (default).").append(LINE_WRAP);
      m_usageBuffer.append("                      With a given time frame and fetch size (t1, t2, size) the fire times").append(LINE_WRAP);
      m_usageBuffer.append("                      will be displayed for that intervall according to the fetch size.").append(LINE_WRAP);
      m_usageBuffer.append("                      The times should have the format 'yyyy.MM.dd_hh:mm:ss'. Fetch size").append(LINE_WRAP);
      m_usageBuffer.append("                      -1 means that it will be ignored.").append(LINE_WRAP);
    }

    
    public SchedulerTaskCommand(Environment env) {
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

        SchedulerAdministrator schedulerAdmin = null;
        try {
            schedulerAdmin = lookupSchedulerAdministrator();
        } catch (NamingException e) {
            e.printStackTrace(out);
        }

        SchedulerTask[] tasks = null;
        if (executeValue == 1) { // list all active and hold tasks
            tasks = schedulerAdmin.getAllSchedulerTasks();           
            if (tasks.length != 0) {
                out.println(formatSchedulerTasks(tasks));
                out.println(tasks.length+" tasks in status "+TaskStatus.active.toString()+" or "+TaskStatus.hold.toString());
            } else {
                out.println("No tasks in status "+TaskStatus.active.toString()+" or "+TaskStatus.hold.toString());
            }             
        } else if (executeValue == 2) { // all hold tasks
            tasks = schedulerAdmin.getAllSchedulerTasks(TaskStatus.hold);
            if (tasks.length != 0) {
                out.println(formatSchedulerTasks(tasks));
                out.println(tasks.length+" tasks in status "+TaskStatus.hold.toString());
            } else {
                out.println("No tasks in status "+TaskStatus.hold.toString());
            } 
        } else if (executeValue == 3) { // all active tasks
            tasks = schedulerAdmin.getAllSchedulerTasks(TaskStatus.active);
            if (tasks.length != 0) {
                out.println(formatSchedulerTasks(tasks));
                out.println(tasks.length+" tasks in status "+TaskStatus.active.toString());
            } else {
                out.println("No tasks in status "+TaskStatus.active.toString());
            } 
        } else if (executeValue == 4) { // all finished tasks
            tasks = schedulerAdmin.getAllSchedulerTasks(TaskStatus.finished);
            if (tasks.length != 0) {
                out.println(formatSchedulerTasks(tasks));
                out.println(tasks.length+" tasks in status "+TaskStatus.finished.toString());
            } else {
                out.println("No tasks in status "+TaskStatus.finished.toString());
            } 
        } else if (executeValue == 5) { // cancels a task
            try {
                SchedulerTaskID taskId = SchedulerTaskID.parseID(params[1]);
                schedulerAdmin.cancelTask(taskId);
                out.println("Task with id '"+params[1]+"' has been cancelled");
            } catch (Exception e) {
                e.printStackTrace(out);
            }
        } else if (executeValue == 6) { // holds a task
            try {
                SchedulerTaskID taskId = SchedulerTaskID.parseID(params[1]);
                schedulerAdmin.holdTask(taskId);
                out.println("Task with id '"+params[1]+"' has been set to hold");
            } catch (Exception e) {
                e.printStackTrace(out);
            }
        } else if (executeValue == 7) { // releases a task
            try {
                SchedulerTaskID taskId = SchedulerTaskID.parseID(params[1]);
                schedulerAdmin.releaseTask(taskId);
                out.println("Task with id '"+params[1]+"' has been set to active");
            } catch (Exception e) {
                out.println("Task with id '"+params[1]+"' couldn't be set to active. Details:");
                e.printStackTrace(out);
            }
        } else if (executeValue == 8) { // display all status descriptions
            Map map = schedulerAdmin.getTaskStatusDescriptions();
            Set entrySet = map.entrySet();
            out.println("Available status descriptions: ");
            for (Iterator iter = entrySet.iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                out.println( ((Short)entry.getKey()).shortValue()+" - "+(String)entry.getValue());                
            }            
        } else if (executeValue == 9) { // display a specific status description
            short val=0;
            try {
                val = Short.parseShort(params[1]);
            } catch (NumberFormatException e) {
                // $JL-EXC$
                out.println("Value '"+params[1]+"' could not be converted to short");
                return;
            }
            
            String desc = (String)schedulerAdmin.getTaskStatusDescriptions().get(new Short(val));
            if (desc == null) {
                out.println("Status description value '"+params[1]+"' is not valid");
            } else {
                out.println("Value '"+params[1]+"' means: "+desc);
            }
        } else if (executeValue == 10) {
            tasks = schedulerAdmin.getAllSchedulerTasksFromPriorityQueue();
            
            if (tasks.length != 0) {                
                out.println(formatSchedulerTasks(tasks));
                out.println(tasks.length+" tasks in PriorityQueue");
            } else {
                out.println("The PriorityQueue is empty (no active tasks).");
            }             
        } else if (executeValue == 11) {
            SchedulerTask task = null;
            try {
                SchedulerTaskID taskId = SchedulerTaskID.parseID(params[1]);
                task = schedulerAdmin.getTask(taskId);
                out.println("Task-details for taskId '"+params[1]+"'"+LINE_WRAP);
                out.println(task.toFormattedString()); 
            } catch (Exception e) {
                e.printStackTrace(out);
            }  
        } else if (executeValue == 12) {
            // list fire times for the next 24 hours
            Date startDate = new Date(System.currentTimeMillis());
            Date endDate = new Date(System.currentTimeMillis()+24*60*60*1000);
            SchedulerTime startTime = new SchedulerTime(startDate, TimeZone.getDefault());
            SchedulerTime endTime = new SchedulerTime(endDate, TimeZone.getDefault());

            try {
                FireTimeEvent[] fireTimes = schedulerAdmin.getFireTimes(startTime, endTime, -1);
                
                out.println("Fire times from "+startDate.toString()+" to "+endDate.toString());
                out.println(formatFireTimesEvents(fireTimes, schedulerAdmin, m_env)); 
            } catch (Exception e) {
                e.printStackTrace(out);
            }  
        } else if (executeValue == 13) {
            // list fire times for a given intervall with a specified fetch size
            
            // start time
            Date startDate = null;
            try {
                startDate = DATE_FORMATTER.parse(params[1]);
            } catch (ParseException pe) {
                out.print("Start time '"+params[1]+"' has not the format 'yyyy.MM.dd_hh:mm:ss'.");
                return;
            }
            
            // end time
            Date endDate = null;
            try {
                endDate = DATE_FORMATTER.parse(params[2]);
            } catch (ParseException pe) {
                out.print("End time '"+params[2]+"' has not the format 'yyyy.MM.dd_hh:mm:ss'.");
                return;
            }
            
            // fetch size
            int fetchSize = 0;
            try {
                fetchSize = Integer.parseInt(params[3]);
                if (fetchSize < -1) {
                    out.print("Fetch size '"+params[3]+"' is not valid. It must not be smaller than -1.");
                    return;
                }
            } catch (NumberFormatException nfe) {
                out.print("Fetch size '"+params[3]+"' is not valid.");
                return;
            }
            
            SchedulerTime startTime = new SchedulerTime(startDate, TimeZone.getDefault());
            SchedulerTime endTime = new SchedulerTime(endDate, TimeZone.getDefault());

            try {
                FireTimeEvent[] fireTimes = schedulerAdmin.getFireTimes(startTime, endTime, fetchSize);
                
                out.println("Fire times from "+startDate.toString()+" to "+endDate.toString());
                out.println(formatFireTimesEvents(fireTimes, schedulerAdmin, m_env)); 
            } catch (Exception e) {
                e.printStackTrace(out);
            }  
        }
    } // exec
    
    
    private int parseArgs(String[] args) {  
        if (args.length < 1 || args.length > 4) {
            return ARGS_INVALID;
        } 
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("-list") || args[0].equalsIgnoreCase("-l")) {
                return 1;
            } else if (args[0].equalsIgnoreCase("-status") || args[0].equalsIgnoreCase("-s")) {
                return 8;
            } else if (args[0].equalsIgnoreCase("-priorityQueue") || args[0].equalsIgnoreCase("-p")) {
                return 10;
            } else if (args[0].equalsIgnoreCase("-fireTimes") || args[0].equalsIgnoreCase("-f")) {
                return 12;
            } else {
                return ARGS_INVALID;
            }
        } 
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("-list") || args[0].equalsIgnoreCase("-l")) {
                if (args[1].equalsIgnoreCase("hold") || args[1].equalsIgnoreCase("h")) {
                    return 2;
                } else if (args[1].equalsIgnoreCase("active") || args[1].equalsIgnoreCase("a")) {
                    return 3;
                } else if (args[1].equalsIgnoreCase("finished") || args[1].equalsIgnoreCase("f")) {
                    return 4;
                } else {
                    return ARGS_INVALID;
                }
            } 
            else if (args[0].equalsIgnoreCase("-cancel") || args[0].equalsIgnoreCase("-c")) {
                return 5;
            }     
            else if (args[0].equalsIgnoreCase("-hold") || args[0].equalsIgnoreCase("-o")) {
                return 6;
            }   
            else if (args[0].equalsIgnoreCase("-release") || args[0].equalsIgnoreCase("-r")) {
                return 7;
            } else if (args[0].equalsIgnoreCase("-status") || args[0].equalsIgnoreCase("-s")) {
                return 9;
            } else if (args[0].equalsIgnoreCase("-detail") || args[0].equalsIgnoreCase("-d")) {
                return 11;
            } 
            else {
                return ARGS_INVALID;
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("-fireTimes") || args[0].equalsIgnoreCase("-f")) {
                return 13;
            } else {
                return ARGS_INVALID;
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
