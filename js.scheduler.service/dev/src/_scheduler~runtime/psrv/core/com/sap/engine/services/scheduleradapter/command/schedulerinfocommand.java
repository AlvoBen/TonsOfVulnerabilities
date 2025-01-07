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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import com.sap.scheduler.api.SchedulerAdministrator;


public class SchedulerInfoCommand extends AbstractCommand {
    private static final String NAME = "scheduler_info"; 
              
    private static StringBuilder m_usageBuffer = new StringBuilder(); 
    static {
      m_usageBuffer.append("Lists info for the scheduler-singleton and running tasks.").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("Usage: scheduler_info <-[d]umpClusterLayout>").append(LINE_WRAP);
      m_usageBuffer.append("-[d]umpClusterLayout  - Dumps out the cluster layout").append(LINE_WRAP);
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

        if (executeValue == 1) {
            Map<String, String[]> map = schedulerAdmin.getClusterLayout();
            out.print(formatClusterLayout(map));
        } 
    } // exec
    
    
    private int parseArgs(String[] args) {    
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("-dumpClusterLayout") || args[0].equalsIgnoreCase("-d")) {
                return 1;
            }
        }
        return ARGS_INVALID;
    } // parseArgs
    
    
    private String formatClusterLayout(Map<String, String[]> map) {
        StringBuilder sb = new StringBuilder(); 
        boolean singletonLockAvailable = false;
        String lockKey = null;
        
        sb.append("Cluster-info for the scheduler:").append(LINE_WRAP);
        sb.append("--------------------------------------------------------").append(LINE_WRAP);
        //                      APPS_STARTING
        sb.append(" ClusterId  | Status      | Node-name                   ").append(LINE_WRAP);
        sb.append("--------------------------------------------------------").append(LINE_WRAP);
        
        Set<Map.Entry<String, String[]>> set = map.entrySet();
        for (Iterator<Map.Entry<String, String[]>> iter = set.iterator(); iter.hasNext();) {
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) iter.next();
            String clusterId = entry.getKey();
            String[] strArr = entry.getValue();
            String stateStr = strArr[1];
            
            if ( Boolean.valueOf(strArr[2]).booleanValue() ) {
                clusterId = clusterId+"*";
                singletonLockAvailable = true;
            }
            // process some formatting
            int clusterIdLength = clusterId.length();
            if (clusterIdLength < 12) {
                for (int j = 0; j < (12-clusterIdLength); j++) {
                    clusterId = clusterId + " ";
                }
            }
            
            int stateStrLength = stateStr.length();
            if (stateStrLength < 13) {
                for (int j = 0; j < (13-stateStrLength); j++) {
                    stateStr = stateStr + " ";
                }
            }
            
            sb.append(clusterId).append("|").append(stateStr).append("|").append(strArr[0]).append(LINE_WRAP); 
            
            lockKey = strArr[3];
        }
        
        if (singletonLockAvailable) {
            sb.append("The node with the '*' (see ClusterId) holds the lock for the singleton-scheduler").append(LINE_WRAP);
        } else {
            sb.append("No node holds the lock for the singleton-scheduler").append(LINE_WRAP);
        }        
        
        sb.append("LockKey: ").append(lockKey).append(LINE_WRAP);
        
        return sb.toString();
    }
    
    // ---------------------------------------------------------------------------

    public String getHelpMessage() {
      return m_usageBuffer.toString();
    }

    public String getName() {
      return NAME;
    }
    
  }
