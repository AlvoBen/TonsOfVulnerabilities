package com.sap.engine.services.dc.event;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-5-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class ListenerEntry {

	private final DeployControllerListener listener;
	private final ListenerMode listenerMode;
	private final EventMode eventMode;

	public static ListenerEntry createInstance(
			DeployControllerListener listener, ListenerMode listenerMode,
			EventMode eventMode) {
		return new ListenerEntry(listener, listenerMode, eventMode);
	}

	private ListenerEntry(DeployControllerListener _listener,
			ListenerMode _listenerMode, EventMode _eventMode) {
		listener = _listener;
		listenerMode = _listenerMode;
		eventMode = _eventMode;
	}

	public DeployControllerListener getDeployControllerListener() {
		return listener;
	}

	public ListenerMode getListenerMode() {
		return listenerMode;
	}

	public EventMode getEventMode() {
		return eventMode;
	}

}
