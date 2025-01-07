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
package com.sap.scheduler.runtime;

import com.sap.scheduler.runtime.JobDefinition;

import java.io.Serializable;


/**
 * This class represntes the key fields of the ebj jar descriptor
 * for a job
 * 
 * @author Dirk Marwinski
 */
public class MDBJobDefinition implements Serializable {

    private static final String messageSelectorProperty = "jobdefinition.mdb.message-selector";
    private static final String ejbNameProperty = "jobdefinition.mdb.ejb-name";
    private static final String destinationTypeProperty = "jobdefinition.mdb.destination-type";
    private static final String displayNameProperty = "job.definition.mdb.display-name";

    // 1: only job name (must be unique)
    // 2: job name and application name
    private static final String jobVersion = "job.definition.mdb.version";

    static final long serialVersionUID = -7700135834071077406L;
    
    // variant 1: JobDefinition = '...'
    // variant 2: JobDefinition = '...' AND ApplicationName = '...' 
    static public enum JobVersion { v1, v2 };

    private final JobDefinition impl;

    public MDBJobDefinition(JobDefinition jd) {
        if ((this.impl = jd) == null)
            throw new NullPointerException("jd");


    }

    // derived from message selector, empty if not a job
    public String[] jobNames;

	/**
	 * @return Returns the destinationType.
	 */
	public String getDestinationType() {
        return impl.readProperty(destinationTypeProperty);
	}
	/**
	 * @param destinationType The destinationType to set.
	 */
	public void setDestinationType(String destinationType) {
        impl.putProperty(destinationTypeProperty, destinationType);
	}
	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
        return impl.readProperty(displayNameProperty);
	}

	/**
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		impl.putProperty(displayNameProperty, displayName);
	}
	/**
	 * @return Returns the ejbName.
	 */
	public String getEjbName() {
		return impl.readProperty(ejbNameProperty);
	}
	/**
	 * @param ejbName The ejbName to set.
	 */
	public void setEjbName(String ejbName) {
        impl.putProperty(ejbNameProperty, ejbName);
	}
	/**
	 * @return Returns the jobNames.
	 */
	public String[] getJobNames() {
		return jobNames;
	}
	/**
	 * @param jobNames The jobNames to set.
	 */
	public void setJobNames(String[] jobNames) {
		this.jobNames = jobNames;
	}

	/**
	 * @return Returns the messageSelector.
	 */
	public String getMessageSelector() {
		return impl.readProperty(messageSelectorProperty);
	}
	/**
	 * @param messageSelector The messageSelector to set.
	 */
	public void setMessageSelector(String messageSelector) {
        impl.putProperty(messageSelectorProperty, messageSelector);
	}
	
	public JobDefinition getJobDefinition() {
        return impl;
    }
    
    public String getApplicationName() {
        return impl.getJobDefinitionName().getApplicationName();
    }

    public void setVersion(JobVersion version) {
        impl.putProperty(jobVersion, version == JobVersion.v1 ? "1" : "2");
    }
    
    public JobVersion getVersion() {
        String version = impl.readProperty(jobVersion);
        
        if (version == null) {
            // old installations do not have this, so consider them old
            return JobVersion.v1;
        }
        if ("1".equals(version)) {
            return JobVersion.v1;
        } else {
            return JobVersion.v2;
        }
    }
}
