package com.sap.engine.cache.communication;

/**
 * @author Petev, Petio, i024139
 */
public interface NotificationHook {

  public void notify(NotificationMessage message, byte scope, boolean synchronous);

}
