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
package com.sap.engine.services.scheduler.runtime.cluster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.scheduler.runtime.Event;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class handles all the cluster communication done by the 
 * scheduler adapter service.
 * <p>
 * The following message types are defined:
 * <ul>
 * <li> BROADCAST_EVENT_RAISED: An event has been raised
 * </li>
 * </ul>
 */
public class ClusterCommunication {

    private final static Location location = Location.getLocation(ClusterCommunication.class);

    /**
     * Initialization of the category for SAP logging.
     */  
    private final static Category category = LoggingHelper.SYS_SERVER;

    private SchedulerAdapterMessageListener mListener;
    private MessageContext mMctx;
    private Environment mEnvironment;
    
    // ---------------------------------------------------------------------
    // message types
    // ---------------------------------------------------------------------

    // Events
    
    // this is an event from the job scheduler EventManager
    //
    public static final byte BROADCAST_EVENT_RAISED = 10;
    
    // this event is used internally to invalidate all subscriber 
    // lists for persistent subscribers
    //
    public static final byte BROADCAST_EVENT_INVALIDATE_PERSISTENT_SUBSCRIBER_LISTS = 20;
    
	public void init(Environment env, MessageContext mctx) {
		
        mMctx = mctx;
        mListener = new SchedulerAdapterMessageListener();
        mListener.init(env, this);
        
        try {
            mMctx.registerListener(mListener);
        } catch (ListenerAlreadyRegisteredException ex) {
            // this should never ever happen (coding error in initialization)
            //
            location.errorT("MessageListener already registered.");
        }
	}
	
	/**
	 * This method does the cleanup work
	 */
	public void close() {
		mMctx.unregisterListener();
	}
    
    // ---------------------------------------------------------------------
    // Event notification messages
    // ---------------------------------------------------------------------
    
    public void broadcastEvent(Event ev) {

        // 1. serialize event
        //
        byte[] serialEvent;
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(ev);
            outputStream.flush();
            serialEvent = baos.toByteArray();
            outputStream.close();
            baos.close();
        } catch (IOException e) {
            // must not happen
            //
            category.logThrowableT(Severity.FATAL, location, "Unable to serialize event object.", e);
            return;
        }
        
        // 2. send event
        //
        try {
            mMctx.send(-1, ClusterElement.SERVER, BROADCAST_EVENT_RAISED, serialEvent, 0, serialEvent.length);
        } catch (ClusterException ce) {
            category.logThrowableT(Severity.ERROR, location, "Unable to send BROADCAST_EVENT_RAISED event.", ce);
        }
    }
    
    public void invalidatePersistentSubscriberLists() {
        try {
            mMctx.send(-1, ClusterElement.SERVER, BROADCAST_EVENT_INVALIDATE_PERSISTENT_SUBSCRIBER_LISTS, new byte[0], 0, 0);
        } catch (ClusterException ce) {
            category.logThrowableT(Severity.ERROR, location, "Unable to send BROADCAST_EVENT_INVALIDATE_PERSISTENT_SUBSCRIBER_LISTS event.", ce);
        }
    }

}
