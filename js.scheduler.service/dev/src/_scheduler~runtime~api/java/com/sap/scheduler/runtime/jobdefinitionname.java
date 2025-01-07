/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.runtime;

import java.io.Serializable;

/**
 * This class represnts the name of a job defintion. A name consists 
 * of an application name and a job name. The application name corresponds
 * to the name of the application where the job definition is deployed in.
 * 
 * @author Dirk Marwinski
 */
public class JobDefinitionName implements Serializable {

    private String mApp = null;
    private String mName = null;

    
    /**
     * Constructs a new JobDefinitionName from a string. If the string
     * has a colon (":") in its name, the first part is considered the 
     * application name and the second part is considered the job name.
     * 
     * @param name the name of a job definition represented as a String. This
     * can either be the name of the job definition or the name of the application
     * followed by a colon folloed by the name of the job definition.
     * @throws NullPointerException if the name is null
     * @throws IllegalArgumentException if the name is not a legal job definition
     * name
     */
    public JobDefinitionName(String name) 
                   throws NullPointerException, IllegalArgumentException {
        
        if (name == null) {
            throw new NullPointerException("Name argument must not be null");
        }
        
        // Figure out whether the name contains a ":" or not
        //
        
        int pos = name.indexOf(":");
        if (pos == -1) {
            mName = name;
        } else {
            
            mApp = name.substring(0, pos);
            // +1 to remove the ':'
            mName = name.substring(pos+1, name.length());
            
            if (mApp.length() == 0 || mName.length() == 0) {
                throw new IllegalArgumentException("\"" + name + "\" not a valid job definition name.");
            }
        }
    }

    /**
     * Constructs a new JobDefinitionName from an application name and a job
     * name.
     * 
     *  @param appName application name
     * @param name name of job definition within the application
     * @throws NullPointerException if <code>appName</code> or 
     * <code>name</code> are null.
     */
    public JobDefinitionName(String appName, String name) {
       
        if (appName == null || 
                name == null) {
            throw new NullPointerException("Name arguments must not be null");
        }
        mApp = appName;
        mName = name;
    }
    
    /**
     * Returns the job definition name.
     * 
     * @return the job definition name
     */
    public String getName() {
        return mName;
    }
    
    /**
     * Returns the application name.
     * 
     * @return the application name
     */
    public String getApplicationName() {
        return mApp;
    }
    
    /**
     * Returns the name of this job definition as a string.
     * 
     * @return the name of this job definition
     */
    public String toString() {
        
        if (mApp != null) {
            return mApp + ":" + mName;
        } else {
            return mName;
        }
    }

    /**
     * Returns the hash code of this job definition name.
     * 
     * @return hash code of this job definition name object.
     */
    public int hashCode() {
        return toString().hashCode();
    }
    
    /**
     * Returns true if the provided object represents the same application
     * name.
     * @param o the object to compare
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof JobDefinitionName)) {
            return false;
        }
        
        JobDefinitionName d = (JobDefinitionName)o;
        if (!mName.equals(d.mName)) {
            return false;
        }
        
        if (mApp != null) {
            if (!mApp.equals(d.mApp)) {
                return false;
            }
        } else if (d.mApp != null) {
            return false;
        }
        
        return true;
        
    }
}
