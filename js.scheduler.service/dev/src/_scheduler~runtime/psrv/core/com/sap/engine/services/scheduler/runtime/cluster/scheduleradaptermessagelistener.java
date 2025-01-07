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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.scheduler.runtime.Event;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class is used to receive an dispatch scheduler cluster messages.
 * 
 * @author Dirk Marwinski
 */
public class SchedulerAdapterMessageListener implements MessageListener {


	private final static Location location = Location.getLocation(SchedulerAdapterMessageListener.class);
	private final static Category category = LoggingHelper.SYS_SERVER;

    private ClusterCommunication mClusterCommunication;
    private Environment mEnvironment;
    
    void init(Environment env, ClusterCommunication comm) {
        mEnvironment = env;
    	mClusterCommunication = comm;
    }
    
	public void receive(int clusterId, int messageType, byte[] body, int offset, int length) {
	
        location.infoT("Scheduler adapter received message: " + messageType + " from cluster node with clusterid " + clusterId);
        
        switch(messageType) {
            case(ClusterCommunication.BROADCAST_EVENT_RAISED):
            	                
                Event ev = deserializeEvent(body, offset, length);
                
                // only deliver non null event, if there was a problem the 
                // error has been reported already
                //
                if (ev != null) {
                    mEnvironment.getEventManager().deliverEventLocally(ev);
                }            
            	break;
            
            case ClusterCommunication.BROADCAST_EVENT_INVALIDATE_PERSISTENT_SUBSCRIBER_LISTS:
                
                // no message body
                //
                
                mEnvironment.getEventManager().invalidatePersistentEventSubscriberListLocal();
                
                break;
                
            default:
            	
            	category.errorT(location, "Ignoring undefined cluster message "+ messageType);
        }

	}

    private Event deserializeEvent(byte[] serializedEvent, int offset, int length) {
        
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(serializedEvent, offset, length);
            ObjectInputStream dis = new ObjectInputStream(bais);
            
            Event ev = (Event)dis.readObject();
            dis.close();
            bais.close();
            return ev;
        } catch (IOException io) {
            category.logThrowableT(Severity.ERROR, location, "Unable to deserialied received event object.", io);
            return null;
        } catch (ClassNotFoundException cnfe) {
            category.logThrowableT(Severity.ERROR, location, "Unable to deserialied received event object.", cnfe);
            return null;
        }
    }
    
	public MessageAnswer receiveWait(int clusterId, int messageType, byte[] body, int offset, int length) throws Exception {
		
		// we should not receive that kind of mesage, log warning message
		//
		location.warningT("The scheduler adapter service should not receive a receiveWait message.");
		
		// send empty reply
		return new MessageAnswer();
	}

}
