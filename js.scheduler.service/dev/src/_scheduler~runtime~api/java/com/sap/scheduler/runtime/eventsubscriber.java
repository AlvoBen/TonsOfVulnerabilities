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

import com.sap.scheduler.runtime.AbstractIdentifier;

/**
 * An event subscriber
 * 
 * @author Dirk Marwinski
 */
public class EventSubscriber {

    private SubscriberID mSubscriberId;
    private String[] mFilters;
    private boolean mPersistEvents = false;
    private EventConsumer mConsumer = null;
    private EventAcceptor mEventAcceptor = null;
    
    public EventSubscriber() {
        
    }
    
    public EventSubscriber(SubscriberID subscriberId, String[] filters, boolean persist) {
        mSubscriberId = subscriberId;
        mFilters = filters;
        mPersistEvents = persist;        
    }

    public EventSubscriber(SubscriberID subscriberId, EventAcceptor acceptor, String[] filters, boolean persist) {
        this(subscriberId, filters, persist);
        mEventAcceptor = acceptor;
    }
    
    public EventSubscriber(SubscriberID subscriberId, String[] filters, EventConsumer con) {
        mSubscriberId = subscriberId;
        mFilters = filters;
        mConsumer = con;
    }
    
	/**
	 * @return Returns the filter.
	 */
	public String[] getFilters() {
		return mFilters;
	}
	/**
	 * @param filter The filter to set.
	 */
	public void setFilters(String[] filter) {
		mFilters = filter;
	}
	/**
	 * @return Returns the subscriberId.
	 */
	public SubscriberID getSubscriberId() {
		return mSubscriberId;
	}
	/**
	 * @param subscriberId The subscriberId to set.
	 */
	public void setSubscriberId(SubscriberID subscriberId) {
		mSubscriberId = subscriberId;
	}
    
    public boolean persistEvents() {
        return mPersistEvents;
    }
    
    public void setPersistEvents(boolean persist) {
        mPersistEvents = persist;
    }
    
    public EventConsumer getEventConsumer() {
        return mConsumer;
    }
    
    public String toString() {
        return "id=" + mSubscriberId.toString() + ",persistEvents="+persistEvents(); 
    }
    
    public EventAcceptor getEventAcceptor() {
        return mEventAcceptor;
    }
}
