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

import java.util.ArrayList;

import com.sap.scheduler.runtime.Event;

/**
 * @author Dirk Marwinski
 */
public class EventQueue {


    private ArrayList mEvents = new ArrayList();

    public synchronized void addEvent(Event ev) {
        mEvents.add(ev);
        notify();
    }

    public synchronized Event getEvent() {
        if (mEvents.size() == 0) {
            return null;
        } else {
            return (Event)mEvents.remove(0);
        }
    }

    public synchronized Event[] getAllEvents() {
        Event[] evs = (Event[])mEvents.toArray(new Event[mEvents.size()]);
        clear();
        return evs;
    }

    public synchronized Event waitForEvent() {

        while(mEvents.size() == 0) {
            try {
                wait();
            } catch (InterruptedException ie) {
                // $JL-EXC$
                // ignore
            }
        }
        return (Event)mEvents.remove(0);
    }

    public synchronized void clear() {
        mEvents.clear();
    }

    public synchronized Event waitForEvent(long timeout) {

        long timeSlept = 0;
        long timeToSleep;

        while(mEvents.size() == 0) {
            timeToSleep = timeout - timeSlept;
            if (timeToSleep <= 0) {
                return null;
            }
            long startSleep = System.currentTimeMillis();

            try {
                wait(timeToSleep);
            } catch (InterruptedException ie) {
                // $JL-EXC$
                // ignore
            }
            long endSleep = System.currentTimeMillis();

            timeSlept += (endSleep - startSleep);
        }
        return (Event)mEvents.remove(0);
    }

}
