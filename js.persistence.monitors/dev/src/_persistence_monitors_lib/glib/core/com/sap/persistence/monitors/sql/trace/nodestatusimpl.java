package com.sap.persistence.monitors.sql.trace;



/**
 * Simple class to hold SQLTrace status.
 * <p>
 * Simple class that containes all attributes describing SQLTrace status.
 * Currently, these attributes are:
 * <ul>
 *   <li>The status, i.e. whether trace on is or off (<code>boolean</code>).
 *   <li>In case SQLTrace is on: the prefix of the trace currently being
 *       written (is <code>null</code> in case trace is off).
 *   <li>Whether stack trace has to be written
 *   <li> stacktrace method pattern
 *   <li> stacktrace duration threshold (in milliseconds)
 * </ul> 
 * <p>
 * Copyright (c) 2008 SAP AG
 * @author Enno Folkerts
 */

public class NodeStatusImpl implements NodeStatus {
    
    
    
    /**
	 * 
	 */
	
	private boolean isOn;
    private String currentPrefix = null;
    private boolean isStackTrace = false;
    private String methodPattern = null;
    private long threshold = 0; 

    //--------------
    // Constructors ------------------------------------------------------------
    //--------------
      
    public NodeStatusImpl( boolean isOn ) {
        this.isOn = isOn;
    }
    
    public NodeStatusImpl( boolean isOn, String prefix ) {
        this.isOn = isOn;
        this.currentPrefix = prefix;
    }

    public NodeStatusImpl( boolean isOn, String prefix, boolean isStacktrace,
            String methodPattern, long threshold ) {
        this.isOn = isOn;
        this.currentPrefix = prefix;
        this.isStackTrace = isStacktrace;
        this.methodPattern = methodPattern;
        this.threshold = threshold;
    }

    public NodeStatusImpl( Boolean switchVal, String traceId , Boolean backVal,  
            String methodPattern, Long threshold ) {
        this.isOn = switchVal.booleanValue();
        this.currentPrefix = traceId;
        this.isStackTrace = backVal.booleanValue();
        this.methodPattern = methodPattern;
        this.threshold = threshold.longValue();
    }
    //----------------
    // Public Methods ----------------------------------------------------------
    //----------------
    
    
    public String getCurrentPrefix() {
        return this.currentPrefix;
    }

    public boolean isOn() {
        return this.isOn;
    }

    public void setCurrentPrefix(String prefix) {
        this.currentPrefix = prefix;
    }

    public boolean isStackTrace() {
        return this.isStackTrace;
    }

    public String getMethodPattern() {
        return ( this.methodPattern == null ? "" : this.methodPattern );
    }

    public void setStackTrace(boolean b) {
        this.isStackTrace = b;
    }

    public void setMethodPattern(String mPat) {
        this.methodPattern = mPat;
    }

    public long getThreshold() {
        return threshold;
    }

    public void setThreshold(long i) {
        threshold = i;
    }

}