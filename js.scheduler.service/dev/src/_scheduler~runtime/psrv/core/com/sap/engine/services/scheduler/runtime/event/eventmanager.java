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
package com.sap.engine.services.scheduler.runtime.event;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.scheduler.runtime.AbstractIdentifier;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.EventConsumer;
import com.sap.scheduler.runtime.EventSubscriber;
import com.sap.scheduler.runtime.SubscriberID;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.core.thread.execution.Executor;


/**
 * Event manager which does all the event handling for the J2EE Job
 * scheduler.
 * 
 * @author Dirk Marwinski
 */
public class EventManager {

    /**
     * Initialization of the location for SAP logging.
     */
    private final static Location location = Location
                                    .getLocation(EventManager.class);

    /**
     * Initialization of the category for SAP logging.
     */
    private final static Category category = LoggingHelper.SYS_SERVER;
    
    /**
     * Mapping between an event and a set of subscriber for efficient
     * event delivery
     */
    private HashMap<String,Set<EventSubscriber>>    mEventTypesHash;
    
    /**
     * Mapping between subscriber id and the corresponding envent 
     * queue.
     */
    private HashMap<AbstractIdentifier, EventQueue> mEventQueues;

    /** 
     * List of persistent event subscribers
     */
    private HashMap<SubscriberID, EventSubscriber> mPersistentEventSubscribers = null;

    /**
     * Mutex for accessing the list of persistent subscribers.
     */
    private Object mPersistentEventSubscriberMutex = new Object();
    
    private Environment mEnvironment;
    private Executor    mCallbackExecutor;
    
    private Map<String, EventSubscriber> mRegisteredSubscribers = new HashMap<String, EventSubscriber>();
    
    class CallbackEventSupplier implements Runnable {
        
        ArrayList<EventConsumer> mConsumers;
        Event mEvent;
        
        CallbackEventSupplier(ArrayList<EventConsumer> consumers, Event ev) {
            
            mConsumers = consumers;
            mEvent = ev;
        }
        
        public void run() {
            
            for (EventConsumer con : mConsumers) {
                con.handle(mEvent);
            }
        }
    };
    
    public EventManager(Environment env) {
        
        mEventTypesHash   = new HashMap<String,Set<EventSubscriber>>();
        mEventQueues = new HashMap<AbstractIdentifier, EventQueue>();
        mEnvironment = env;

        ThreadSystem threadSystem = mEnvironment.getServiceContext().getCoreContext().getThreadSystem();
        
        // Create the executor of the AS Java thread system:
        // - Max parallelism is 1 which means that the EventSubscriber.handle()
        //   method does not need to be synchronized
        // - Queue size is 10, if there are more concurrent requests the 
        //   sending thread is blocked (very bad but 10 should be sufficient
        
        mCallbackExecutor = threadSystem.createExecutor(
                                     "Scheduler Callback Executor",
                                     1,
                                     10,
                                     Executor.WAIT_TO_QUEUE_POLICY);
    }

    /**
     * This method is called from the local component that raises an event. 
     * 
     * @param type type of event { @see Event}
     * @param parameter event parameter
     */
    public void raiseEvent(String type, String parameter) {
        
        raiseEvent(type, parameter, new Date(), null);
    }
    
    /**
     * This method is called from the local component that raises an event. 
     * 
     * @param type type of event { @see Event}
     * @param parameter event parameter
     * @param additionalParameter additional parameter defined for some event types
     */
    public void raiseEvent(String type, String parameter, String additionalParameter) {
        
        raiseEvent(type, parameter, additionalParameter, new Date());
    }
    
    /**
     * This method is called from the local component that raises an event. 
     * 
     * @param type type of event { @see Event}
     * @param parameter event parameter
     * @param additionalParameter additional parameter defined for some event types
     * @param date date when the event was raised
     */
    public void raiseEvent(String type, String parameter, String additionalParameter, Date date) {
 
        raiseEvent(type, parameter, additionalParameter, date, null);
    }
    
    /**
     * This method is called from the local component that raises an event. 
     * 
     * @param type type of event { @see Event}
     * @param parameter event parameter
     * @param date date when the event was raised
     */
    public void raiseEvent(String type, String parameter, Date date) {
        
        raiseEvent(type, parameter, date, null);
    }
    
    /**
     * This method is called from the local component that raises an event. 
     * 
     * @param type type of event { @see Event}
     * @param parameter event parameter
     * @param additionalParameter additional parameter defined for some event types
     * @param date date when the event was raised
     * @deprecated not used anymore
     */
    public void raiseEvent(String type, String parameter, Date date, AbstractIdentifier raisedByDetails) {

        raiseEvent (type, parameter, null, date, raisedByDetails);
    }
    
    public void raiseEvent(String type, String parameter, String additionalParameter, Date date, AbstractIdentifier raisedByDetails) {
        
        Event event = new Event(type, parameter, additionalParameter, raisedByDetails, date);
        
        if (location.beDebug()) {
            location.debugT("Event raised: [Type=" + type + "][Parameter=" + parameter + "][Date=" + date.toString() +"]");
        }
        
        // 1. store event for persistent event subscribers
        //
        storeEventForPersistentSubscribers(event);

        // 2. deliver event to local event subscribers
        //
        deliverEventLocally(event);
        
        // 3. send cluster broadcast message
        //
        deliverEventToCluster(event);
    
    }
    
    /**
     * This method delivers the event to the locally subscribed event 
     * subscribers (also called for events that are received through the 
     * cluster)
     * 
     * @param ev event object
     */
    public void deliverEventLocally(Event ev) {

        addEventToQueues(ev);
        
    }
    
    private void storeEventForPersistentSubscribers(Event ev) {

        HashMap<SubscriberID,EventSubscriber> persistentSubscribers = getPersistentEventSubscribers();

        if (persistentSubscribers.size() == 0) {
            return;
        }
        try {
            mEnvironment.getEventPersistor().persistEvent(persistentSubscribers, ev);
        
        } catch (SQLException sql) {
            category.logThrowableT(Severity.ERROR, location, "Cannot persist event to database. Event \"" + ev.toString() + "\" lost: ", sql);
        }
    }
    
    private void deliverEventToCluster(Event ev) {
        
        mEnvironment.getClusterCommuniction().broadcastEvent(ev);
    }

    private void addEventToQueues(Event ev) {
        
        ArrayList<EventConsumer> consumers = null;
        String type = ev.getType();
        
        // need to synchronize read access to mEventTypesHash HashMap
        //
        synchronized(this) {
            
            Set<EventSubscriber> s = mEventTypesHash.get(type);
            
            if (s == null) {
                // nobody here is interested in the event
                return;
            }
            
            Iterator<EventSubscriber> it = s.iterator();
            while (it.hasNext()) {
                EventSubscriber sub = it.next();
                if (sub.getEventConsumer() == null) {
    
                    if (sub.getEventAcceptor() == null ||
                            sub.getEventAcceptor().acceptEvent(ev)) {
                        
                        EventQueue q = (EventQueue)mEventQueues.get(sub.getSubscriberId());
                        q.addEvent(ev);
                    }
                } else {
                    // handle callback delivery here
                    if (consumers == null) {
                        consumers = new ArrayList<EventConsumer>();
                    }
                    consumers.add(sub.getEventConsumer());
                }
            }        
        }
        
        if (consumers != null) {
            Runnable supplier = new CallbackEventSupplier(consumers, ev);
            mCallbackExecutor.executeMonitored(
                                          supplier,
                                          "This task delivers scheduler event to the registered suppliers.",
                                          "Scheduler_Event_Delivery");
        }
        
    }

    /**
     * This methods adds a transient event subscriber.
     * 
     * @param sub
     * @throws IllegalArgumentException
     */
    public synchronized void registerEventSubscriber(EventSubscriber sub) {
        if (sub.getEventConsumer() != null) {
            String subscriberName = sub.getEventConsumer().getName();
            if (subscriberName == null) {
                subscriberName = sub.getEventConsumer().getClass().getName();
            }
            if ( mRegisteredSubscribers.containsKey(subscriberName) ) {
                throw new IllegalArgumentException("EventSubscriber '"+subscriberName+"' with Id '"+sub.getSubscriberId().toString()+"' is already registered.");
            }
            // add it to the map
            mRegisteredSubscribers.put(subscriberName, sub);
        }
        
        if (sub.persistEvents()) {
            throw new UnsupportedOperationException("The method EventManager.registerEventSubscriber() cannot be used to register persistent subscribers.");
        }

        String filters[] = sub.getFilters();
        for (int i=0; i < filters.length;i++) {
            
            Set<EventSubscriber> s = mEventTypesHash.get(filters[i]);
            if (s == null) {
                s = new HashSet<EventSubscriber>();
                mEventTypesHash.put(filters[i], s);
            }
            s.add(sub);
        }
        
        if (sub.getEventConsumer() == null) {
            mEventQueues.put(sub.getSubscriberId(), new EventQueue());
        }
    }
    
    
    public synchronized EventSubscriber getEventSubscriber(EventConsumer consumer) {
        String subscriberName = consumer.getName();
        if (subscriberName == null) {
            subscriberName = consumer.getClass().getName();
        }        
        EventSubscriber sub = mRegisteredSubscribers.get(subscriberName);
        
        if (sub == null) {
            category.logT(Severity.INFO, location, "EventSubscriber '"+subscriberName+"' is not registered.");
        }
        return sub; 
    }
    
    
    public synchronized void unregisterEventSubscriber(EventSubscriber sub) {
        
        mEventQueues.remove(sub.getSubscriberId());
        
        Iterator it = mEventTypesHash.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Set s = (Set)entry.getValue();
            s.remove(sub);
        }        
        
        if (sub.getEventConsumer() != null) {
            String subscriberName = sub.getEventConsumer().getName();
            if (subscriberName == null) {
                subscriberName = sub.getEventConsumer().getClass().getName();
            }
            
            EventSubscriber subscriber = mRegisteredSubscribers.remove(subscriberName);
            if (subscriber == null) {
                category.logT(Severity.INFO, location, "EventSubscriber '"+subscriberName+"' is not registered.");
            }
        }
    }
    
    public synchronized Event[] getEvents(EventSubscriber sub) {
        return getEvents(sub, 0);
    }

    public synchronized Event[] getEvents(EventSubscriber sub, int fetchSize) {
        if (sub.getEventConsumer() != null) {
            throw new UnsupportedOperationException("The method getEvents() is not supported for event subscriber which have defined a callback.");
        }

        if (sub.persistEvents()) {
            Event[] evs = new Event[0];
            try {
                evs = mEnvironment.getEventPersistor().getUnreadEvents(sub.getSubscriberId(), fetchSize);
            } catch (SQLException sql) {
                category.fatalT(location, "Unable to get events for subscriber \"" + sub.getSubscriberId() + "\".");
            }
            return evs;
        } else {
            synchronized(this) {
                EventQueue q = (EventQueue)mEventQueues.get(sub.getSubscriberId());
                return q.getAllEvents();
            }               
        }
    }

    
    public synchronized void clearEvents(EventSubscriber sub) {

        if (sub.getEventConsumer() != null) {
            throw new UnsupportedOperationException("The method clearEvents() is not supported for event subscriber which have defined a callback.");
        }

        if (location.beDebug()) {
            location.debugT("clearing events for event subscriber " + sub.toString());
        }
        if (sub.persistEvents()) {
            
            // remove all persisted events
            //
            try {
                mEnvironment.getEventPersistor().clearEvents(sub);
            } catch (SQLException sql) {
                category.fatalT(location, "Unable to clear events for subscriber \"" + sub.getSubscriberId() + "\".");
            }
        } else {
            // remove all local events
            //
            synchronized(this) {
                EventQueue q = (EventQueue)mEventQueues.get(sub.getSubscriberId());
                q.clear();
            }
        }        
    }
    
    public Event waitForEvent(EventSubscriber sub) {

        if (sub.getEventConsumer() != null) {
            throw new UnsupportedOperationException("The method waitForEvent() is not supported for event subscriber which have defined a callback.");
        }
        if (sub.persistEvents()) {
            throw new UnsupportedOperationException("The method waitForEvent() cannot be used for persistent event subscribers.");
        }
        EventQueue eq;        
        synchronized(this) {
            eq = (EventQueue)mEventQueues.get(sub.getSubscriberId());
        }
        
        Event ev = eq.waitForEvent();
        return ev;
    }
    
    public Event waitForEvent(EventSubscriber sub, String type) {

        if (sub.getEventConsumer() != null) {
            throw new UnsupportedOperationException("The method waitForEvent() is not supported for event subscriber which have defined a callback.");
        }
        if (sub.persistEvents()) {
            throw new UnsupportedOperationException("The method waitForEvent() cannot be used for persistent event subscribers.");
        }
        EventQueue eq;        
        synchronized(this) {
            eq = (EventQueue)mEventQueues.get(sub.getSubscriberId());
        }
        
        while (true) {
            Event ev = eq.waitForEvent();
            if (ev.getType().equals(type)) {
                return ev;
            }
        }
    }
    
    /**
     * Wait for an event for a specified amount of time
     * 
     * @param sub
     * @param timeout
     * @return
     */
    public Event waitForEvent(EventSubscriber sub, long timeout) {

        if (sub.getEventConsumer() != null) {
            throw new UnsupportedOperationException("The method waitForEvent() is not supported for event subscriber which have defined a callback.");
        }
        if (sub.persistEvents()) {
            throw new UnsupportedOperationException("The method waitForEvent() cannot be used for persistent event subscribers.");
        }
        EventQueue eq;
        
        synchronized(this) {
            eq = (EventQueue)mEventQueues.get(sub.getSubscriberId());
        }
        
        long timeSlept = 0;
        long timeToSleep;
        while (true) {
            
            timeToSleep = timeout - timeSlept;
            if (timeToSleep <= 0) {
                // time is up
                return null;
            }
            
            long startSleep = System.currentTimeMillis();
            
            Event ev = eq.waitForEvent(timeToSleep);
            
            long endSleep = System.currentTimeMillis();            
            timeSlept += (endSleep - startSleep);
            
            if (ev != null) {
                // got event
                return ev;
            }
        }
    }
    
    public Event waitForEvent(EventSubscriber sub, String type, long timeout) {

        if (sub.getEventConsumer() != null) {
            throw new UnsupportedOperationException("The method waitForEvent() is not supported for event subscriber which have defined a callback.");
        }
        EventQueue eq;
        
        synchronized(this) {
            eq = (EventQueue)mEventQueues.get(sub.getSubscriberId());
        }
        
        long timeSlept = 0;
        long timeToSleep;
        while (true) {
            
            timeToSleep = timeout - timeSlept;
            if (timeToSleep <= 0) {
                return null;
            }
            
            long startSleep = System.currentTimeMillis();
            
            Event ev = eq.waitForEvent(timeToSleep);
            
            long endSleep = System.currentTimeMillis();            
            timeSlept += (endSleep - startSleep);
            
            if (ev == null) {
                // time is up
                //
                return null;
            }

            if (type.equals(type)) {
                return ev;
            }
        }
    }
    
    public void setFilter(AbstractIdentifier persistentSubscriber, String[] filter) 
                                                    throws SQLException {

        EventSubscriber sub = mEnvironment.getEventPersistor().getPersistentEventSubscriberById(persistentSubscriber);
        if (sub == null) {
            location.errorT("Event subscriber \"" + persistentSubscriber + "\" not known.");
            return;
        }
        mEnvironment.getEventPersistor().setFilter(persistentSubscriber, filter);
        
        // notify of filter change
        //
        invalidatePersistentEventSubscriberList();
    }
    
    public void invalidatePersistentEventSubscriberList() {
        
        invalidatePersistentEventSubscriberListLocal();
        mEnvironment.getClusterCommuniction().invalidatePersistentSubscriberLists();        
    }
    
    public void invalidatePersistentEventSubscriberListLocal() {
        synchronized (mPersistentEventSubscriberMutex) {

            mPersistentEventSubscribers = null;
        }
    }
    
    private HashMap<SubscriberID,EventSubscriber> getPersistentEventSubscribers() {
        
        synchronized(mPersistentEventSubscriberMutex) {
            
            if (mPersistentEventSubscribers == null) {

                ArrayList<EventSubscriber> candidates;
                try {
                    candidates = mEnvironment.getEventPersistor().getPersistentEventSubscribers();                
                } catch (SQLException sql) {
                    category.logThrowableT(Severity.ERROR, location, "Unable to retrieve persistent event subscribers. ", sql);
                    // need a temporary fix: just return an empty list
                    // and try again the next time
                    
                    return new HashMap<SubscriberID,EventSubscriber>();
                }

                mPersistentEventSubscribers = new HashMap<SubscriberID,EventSubscriber>();
                
                for (EventSubscriber sub: candidates) {
                    if (sub.persistEvents()) {
                        mPersistentEventSubscribers.put(sub.getSubscriberId(), sub);
                    }
                }
            }
            return mPersistentEventSubscribers;
        }
    }
    
    public EventSubscriber getPersistentEventSubscriberById(SubscriberID id) {
        
        HashMap<SubscriberID, EventSubscriber> subscribers = getPersistentEventSubscribers();
        return subscribers.get(id);
    }
    
    
    /**
     * We need to check here if we the user tries to register for Task-events
     * what is not possible for external schedulers. We will return then only
     * the events which are possible for JXBP (all job related events).
     * <p>
     * @param eventTypes the Event[] with the event types to register for
     * @return the eventTypes without task event types
     */
    public String[] cleanupEventTypesForJXBP(String[] eventTypes) {
        ArrayList<String> supportedEvents = new ArrayList<String>();
        for (int i = 0; i < Event.JXBP_RUNTIME_EVENT_TYPES.length; i++) {
            supportedEvents.add(Event.JXBP_RUNTIME_EVENT_TYPES[i]);
        }
        
        ArrayList<String> eventTypesList = new ArrayList<String>();
        for (int i = 0; i < eventTypes.length; i++) {
            if ( supportedEvents.contains(eventTypes[i]) ) {
                eventTypesList.add(eventTypes[i]);
            }
        }  
        
        return eventTypesList.toArray(new String[eventTypesList.size()]);
    }
}
