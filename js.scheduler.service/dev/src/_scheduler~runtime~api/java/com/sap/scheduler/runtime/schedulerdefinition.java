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

import java.io.Serializable;

import com.sap.scheduler.runtime.AbstractIdentifier;

/**
 * This class represents a scheduler that is known to the scheduler 
 * runtime component. This is either the internal scheduler or an external 
 * scheduler connected via JXBP.
 * 
 * @author Dirk Marwinski
 */
public class SchedulerDefinition implements Serializable {

    static final long serialVersionUID = 7973288607926485218L;

    public static final String SAP_SCHEDULER_NAME = "SAP J2EE Scheduler";
    
    public enum SchedulerStatus {
        
        active ((short)1), 
        inactive ((short)0);
        
        private short val;
        
        SchedulerStatus(short val) {
            this.val = val;
        }
        
        public short getValue() {
            return this.val;
        }
    };

    private SchedulerID id;
    private String name;
    private String user;
    private String description;
    private SubscriberID subscriberId;
    private SchedulerStatus status;

    /**
     *  Timestamp when the scheduler accessed the J2EE Engine for the last
     *  time.
     */
    private long lastAccess;

    /**
     * Period after which the external scheduler will be set to inactive
     * 
     */
    private long inactivityGracePeriod;

    
    public SchedulerDefinition(
                      SchedulerID id,
                      String name,
                      String user,
                      String description,
                      SubscriberID subscriberId,
                      SchedulerStatus status,
                      long lastAccess,
                      long inactivityGracePeriod
                      ) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.description = description;
        this.subscriberId = subscriberId;
        this.status = status;
        this.lastAccess = lastAccess;
        this.inactivityGracePeriod = inactivityGracePeriod;
    }
    
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Returns the id.
	 */
	public SchedulerID getId() {
		return id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the user.
	 */
	public String getUser() {
		return user;
	}
        
    public SubscriberID getSubscriberId() {
        return subscriberId;
    }
    
    public SchedulerStatus getSchedulerStatus() {
        return status;
    }

    /**
     *  Timestamp when the scheduler accessed the J2EE Engine for the last
     *  time.
     */
    public long getLastAccess() {
        return lastAccess;
    }

    /**
     * Period after which the external scheduler will be set to inactive
     * 
     */
    public long getInactivityGracePeriod() {
        return inactivityGracePeriod;
    }
}
