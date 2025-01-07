/*
 * Created on 08.12.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.scheduler.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.services.scheduler.security.ExecuteAdministrativeMethod;
import com.sap.scheduler.runtime.SchedulerRuntimeException;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


public final class SingletonEnvironment {
    public static final String LINE_WRAP = System.getProperty("line.separator");
    
    private static final Location LOCATION = Location.getLocation(SingletonEnvironment.class);
    
    // -------------------------------------------------------------------------
    // ------------- Defaults and names for service properties -----------------
    // -------------------------------------------------------------------------
    // identifies the count of jobs which might fail before the task is set to hold by default
    protected static final int  NUMBER_OF_FAILED_JOBS = 4;
    protected static final long IGNORE_BLACKOUT_PERIOD = 120000; // 2 min
    
    protected static final String IGNORE_BLACKOUT_PERIOD_NAME = "ignoreBlackoutPeriod";
    protected static final String ENABLE_JOB_EXECUTION_NAME   = "enableJobExecution";
    protected static final String NUMBER_OF_FAILED_JOBS_NAME  = "numberOfFailedJobs";    
    
    
    private TaskPersistor m_taskPersistor = null;
    private Scheduler m_scheduler = null;
    private ServiceFrame m_service = null;
    
    private final ExecuteAdministrativeMethod m_executeAdministrativeMethodPermission =
        new ExecuteAdministrativeMethod("ExecuteAdministrativeMethod", "AdministerScheduler");
    
    
    /**
     * constructor with default visibility
     */
    SingletonEnvironment() { }
    
    /**
     * @return Returns the TaskPersistor.
     */
    protected TaskPersistor getTaskPersistor() {
        return m_taskPersistor;
    }

    /**
     * @param persistor The TaskPersistor to set.
     */
    protected void setTaskPersistor(TaskPersistor persistor) {
        m_taskPersistor = persistor;
    }
    
    /**
     * @return Returns the ServiceFrame.
     */
    protected ServiceFrame getServiceFrame() {
        return m_service;
    }

    /**
     * @param service The ServiceFrame to set.
     */
    protected void setServiceFrame(ServiceFrame service) {
        m_service = service;
    }
    
    /**
     * @return Returns the Scheduler.
     */
    protected Scheduler getScheduler() {
        return m_scheduler;
    }

    /**
     * @param scheduler The Scheduler to set.
     */
    protected void setScheduler(Scheduler scheduler) {
        m_scheduler = scheduler;
    }
    
    
    /**
     * Checks if the current caller is authenticated to access administrator
     * methods. If not an exception (java.security.AccessControlException) will 
     * be thrown.
     * 
     * @param callerName the caller as a String
     */
    protected void assertAuthorizedAdministrativeMethods(String callerName) {
        final IUser caller;
        try {
            caller = UMFactory.getUserFactory().getUserByUniqueName(callerName);
        } catch (UMException ume) {
            String errMsg = "Unable to obtain necessary security information for caller: " + callerName;
            Category.SYS_SERVER.logThrowableT(Severity.ERROR, LOCATION, errMsg, ume);
            throw new SchedulerRuntimeException(errMsg, ume);
        }
        
        if (LOCATION.beDebug()) {
            LOCATION.debugT("Caller name is \"" + callerName + "\".");
        }
        
        caller.checkPermission(m_executeAdministrativeMethodPermission);
    }

    
    /**
     * Returns the current caller from current security context
     * 
     * @return the caller String
     */
    protected String getCaller() {
        SecurityContextObject securityContext = (SecurityContextObject)m_service
        .getServiceContext().getCoreContext().getThreadSystem()
        .getThreadContext().getContextObject("security");
        return securityContext.getSession().getPrincipal().getName();
    }
    
    
    
    /**
     * Returns the cluster layout info regarding the scheduler singleton in a 
     * formatted way.
     * 
     * @return the formatted cluster layout info
     */
    protected static String formatClusterLayout(Map<String, String[]> map) {
        StringBuilder sb = new StringBuilder();        
        Set<Map.Entry<String, String[]>> set = map.entrySet();        
        
        sb.append("Cluster-layout for the singleton-scheduler: ");
        String nodeWithLock = null;
        String lockKey = null;
        
        for (Iterator<Map.Entry<String, String[]>> iter = set.iterator(); iter.hasNext();) {
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) iter.next();
            String clusterId = entry.getKey();
            String[] strArr = entry.getValue();
            sb.append("ClusterId: ").append(clusterId).append(", Node-name: ").append(strArr[0]).append(", Status: ").append(strArr[1]).append("; ");
            
            if (Boolean.valueOf(strArr[2]).booleanValue()) {
                nodeWithLock = "The node with clusterId '"+clusterId+"' holds the lock for the scheduler-singleton. LockKey: "+strArr[3];
            }    
            
            lockKey = strArr[3];
        }
        
        if (nodeWithLock == null) {
            nodeWithLock = "No node holds the lock for the scheduler-singleton. LockKey: "+lockKey;
        }
        
        sb.append(nodeWithLock);
        
        return sb.toString();
    }
    
    
    protected static long getLongValueFromProperty(Properties props, String propName, long defaultValue) {
        String prop = props.getProperty(propName);
        long result = defaultValue;
        
        if (prop != null) {
      	  try {
      		  result = Long.parseLong(prop);
      	  } catch (NumberFormatException nfe) {
      		  // $JL-EXC$
      	  }
        }
        
        return result;
    }
    
}
