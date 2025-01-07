package com.sap.engine.services.webservices.webservices630.server.deploy.event.system;

import com.sap.engine.interfaces.webservices.server.event.EventFactory;
import com.sap.engine.interfaces.webservices.server.event.EventListener;
import com.sap.engine.interfaces.webservices.server.event.EventContext;

/**
 * Title:
 * Description:
 * Copyright: Copyright (c) 2004
 * Company: Sap Labs Bulgaria
 * @author Dimitrina Stoyanova
 * @version 6.30
 */

public class EventContextImpl implements EventContext {

  private EventListenerRegistry eventListenerRegistry = new EventListenerRegistry();
  private EventFactoryRegistry eventFactoryRegistry = new EventFactoryRegistry();
  private EventHandler eventHandler = new EventHandler();

  public EventContextImpl() {

  }

  public EventListenerRegistry getEventListenerRegistry() {
    return eventListenerRegistry;
  }

  public EventFactoryRegistry getEventFactoryRegistry() {
    return eventFactoryRegistry;
  }

  public EventHandler getEventHandler() {
    return eventHandler;
  }

  public boolean isRegisteredEventFactory(String eventId) {
    return eventFactoryRegistry.containsFactory(eventId);
  }

  public void registerEventFactory(String eventId, EventFactory eventFactory) {
    eventFactoryRegistry.registerFactory(eventId, eventFactory);
  }

  public void unregisterEventFactory(String eventId) {
    eventFactoryRegistry.unregisterFactory(eventId);
  }

  public boolean isRegisteredEventListener(String eventId, String listenerId) {
    return eventListenerRegistry.contains(eventId, listenerId);
  }

  public void registerEventListener(String eventId, EventListener eventListener) {
    eventListenerRegistry.registerListener(eventId, eventListener);
  }

  public void unregisterEventListener(String eventId) {
    eventListenerRegistry.unregisterListener(eventId);
  }

}
