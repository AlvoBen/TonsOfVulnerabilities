/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.event.impl;

import java.util.ArrayList;

import com.sap.engine.lib.util.WaitQueue;
import com.sap.engine.services.dc.api.event.DAEvent;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.util.DALog;

/**
 *@author Shenol Yousouf
 */
public abstract class AbstractListenerImpl {

	public static final String THREAD_NAME = "DC API Listener Event Dispatcher";
	private transient final WaitQueue queue = new WaitQueue();
	private transient EventDispatcher asynchListenersProcessor = null;

	protected transient final ArrayList listenersSynch = new ArrayList();
	protected transient final ArrayList listenersAsynch = new ArrayList();
	protected transient DALog daLog;
	private transient boolean isLocal;

	protected AbstractListenerImpl(DALog daLog, boolean isLocalListener) {
		this.daLog = daLog;
		this.isLocal = isLocalListener;
	}

	protected abstract void dispatchEvent(final DAEvent event,
			ArrayList listenersList);

	protected abstract boolean checkListenerType(final Object listener);

	protected void dispatch(final DAEvent event) {
		// do not invoke the methods if the list is empty
		if (hasListenersAsynch()) {
			queue.enqueue(event);
		}
		if (hasListenersSynch()) {
			dispatchEvent(event, listenersSynch);
		}
	}

	protected boolean hasListenersAsynch() {
		return this.listenersAsynch != null && !this.listenersAsynch.isEmpty();
	}

	protected boolean hasListenersSynch() {
		return this.listenersSynch != null && !this.listenersSynch.isEmpty();
	}

	protected void initQueue() {
		
		// there is one event processor per instance of this
		// class that shall be explicity cleaned up when the
		// instance is no longer needed (see cancel() )
		if (asynchListenersProcessor == null) {
			
			asynchListenersProcessor = 
				new EventDispatcher(queue,
									listenersAsynch,
									this);
			
			asynchListenersProcessor.start();
		}
	}

	protected void abstractJoin(AbstractListenerImpl one,
			AbstractListenerImpl other) {
		if (one != null && one.listenersSynch != null) {
			listenersSynch.addAll(one.listenersSynch);
		}
		if (other != null && other.listenersSynch != null) {
			listenersSynch.addAll(other.listenersSynch);
		}
		boolean bListenersAsynch = false;
		if (one != null && one.listenersAsynch != null) {
			listenersAsynch.addAll(one.listenersAsynch);
			bListenersAsynch = true;
		}
		if (other != null && other.listenersAsynch != null) {
			listenersAsynch.addAll(other.listenersAsynch);
			bListenersAsynch = true;
		}
		if (bListenersAsynch) {
			initQueue();
		}
	}

	protected static DALog joinLog(AbstractListenerImpl one,
			AbstractListenerImpl other) {
		DALog daLog1 = (one != null) ? one.daLog : null;
		DALog daLog2 = (other != null) ? other.daLog : null;
		return daLog1 != null ? daLog1 : daLog2;
	}

	public synchronized void addListener(Object listener, EventMode eventMode) {
		if (checkListenerType(listener)) {
			removeListener(listener);
			if (EventMode.SYNCHRONOUS.equals(eventMode)) {
				this.listenersSynch.add(listener);
			} else if (EventMode.ASYNCHRONOUS.equals(eventMode)) {
				this.listenersAsynch.add(listener);
				initQueue();
			} else {
				throw new IllegalArgumentException(
						"[ERROR CODE DPL.DCAPI.1049] Unknown Event Mode '"
								+ eventMode + "'.");
			}
		}
	}

	public synchronized void removeListener(Object listener) {
		if (checkListenerType(listener)) {
			this.listenersSynch.remove(listener);
			this.listenersAsynch.remove(listener);
		}
	}

	public boolean isLocalListener() {
		return isLocal;
	}

	/**
	 * Required for the unique identification of the listener on server for
	 * add/remove
	 * 
	 * @see com.sap.engine.services.dc.event.DeploymentListener#getId()
	 */
	public int getId() {
		return hashCode();
	}

	public synchronized int getListenersCount() {
		return this.listenersSynch.size() + this.listenersAsynch.size();
	}
	
	public void cleanUp(){
		
		// cancel the thread
		if(this.asynchListenersProcessor != null){
			this.queue.enqueue(new StopEvent());
		}
		
	}
}

class StopEvent{}

class EventDispatcher extends Thread {
	
	private WaitQueue queue;
	private ArrayList listeners;
	private AbstractListenerImpl listenerInstance;
	
	public EventDispatcher(WaitQueue queue, ArrayList listeners, AbstractListenerImpl listenerInstance) {
		this.queue = queue;
		this.listeners = listeners;
		this.listenerInstance = listenerInstance;
		this.setName( AbstractListenerImpl.THREAD_NAME );
		this.setDaemon(true);
	}
	
	
	public void run() {
	
		while(true){
			
			Object obj = queue.dequeue();
			if(obj instanceof StopEvent){
				break;
			}
			DAEvent event = (DAEvent)obj; 
			
			this.listenerInstance.dispatchEvent(event, listeners);
			
		}
		
	}
	
}
