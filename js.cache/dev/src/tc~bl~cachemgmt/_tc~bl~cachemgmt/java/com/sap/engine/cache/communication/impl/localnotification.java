package com.sap.engine.cache.communication.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.cache.admin.impl.MonitorsAccessor;
import com.sap.engine.cache.communication.Notification;
import com.sap.engine.cache.communication.NotificationHook;
import com.sap.engine.cache.communication.NotificationListener;
import com.sap.engine.cache.communication.NotificationMessage;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author Petev, Petio, i024139
 */
public class LocalNotification implements Notification {

  private HashMap listeners;
  private HashMap regions;
  private HashMap slots;
  private NotificationHook hook = null;

  public LocalNotification() {
    listeners = new HashMap();
    slots = new HashMap();
    regions = new HashMap();
  }

  /**
   * Registers a listener that will be notified when changes in the region are made
   *
   * @param listener The listener that cache implementation registers
   */
  public void registerListener(CacheRegion region, NotificationListener listener) {
    synchronized (listeners) {
      listeners.put(new Integer(region.getRegionConfigurationInfo().getId()), listener);
      regions.put(new Integer(region.getRegionConfigurationInfo().getId()), region);
      Integer slot = new Integer(region.getRegionConfigurationInfo().getId());
      SlotStatus status = (SlotStatus) slots.get(slot);
      if (status == null) {
        status = new SlotStatus();
        slots.put(slot, status);
      }
    }
  }

  /**
   * Unregisters a listener
   *
   * @param region The listener instance that was previously registered
   */
  public void unregisterListener(CacheRegion region) {
    synchronized (listeners) {
      listeners.remove(new Integer(region.getRegionConfigurationInfo().getId()));
      regions.remove(new Integer(region.getRegionConfigurationInfo().getId()));
      Integer slot = new Integer(region.getRegionConfigurationInfo().getId());
      slots.remove(slot);
    }
  }

  /**
   * Initiates notification when the cache implementation has registered change in the cache region
   *
   * @param region       The cache region that has been changed
   * @param storage      The storage plugin bound to the cache region
   * @param key          The cached object key of the changed object
   * @param operation    The operation that was made on the cached object
   * @param cachedObject The cached object that will be tried to transport if the storage plugin supports
   *                     transporting of cached objects
   */
  public void notify(CacheRegion region, StoragePlugin storage, String key, byte operation, Object cachedObject, byte invalidationScope) {
    notify(region, storage, key, operation, cachedObject, invalidationScope, false);
  }

  /**
   * The cache implementation may use this method to send several change notifications in one message
   *
   * @param region the id of the region that groups several events in a block
   */
  public void beginEventBlock(CacheRegion region) {
    Integer slot = new Integer(region.getRegionConfigurationInfo().getId());
    SlotStatus status = (SlotStatus) slots.get(slot);
    if (status == null) {
      status = new SlotStatus();
      slots.put(slot, status);
    }
    status.beginBlock();
  }

  public void receiveLocal(int regionId, String key, byte operation) {
    operation = (byte) -operation;
    NotificationListener listener = (NotificationListener) listeners.get(new Integer(regionId));
    if (listener != null) {
      listener.invalidate(regionId, key, operation);
    }
  }

  // **
  // * THIS METHOD IS DONE AS PART OF A PATCH FOR BI
  // * BI DID NOT TESTED AT ALL ONE OF THEIR SCENARIOS WHICH LED TO ESCALLATION FROM 5 CUSTOMERS
  // * THE PATCH WAS DONE TO RESOLVE QUICKLY ALL EXCALLATIONS
  // * 
  // * BI CONTACTS ON THE ISSUE: Krannich, Bernd (D028923); Scheerer, Oliver (D027913); Matthee, Stephan (D032419) 
  // *
  // * @param regionId - the regionID for which the semantical invalidation request is send 
  // * @param attributes - the attribues based on which semantical invalidation must be done 
  // * @param operation - the type of the operation that led to the invalidation (e.x. remove, invalidate)
  // */  
  public void receiveSemanticLocal(int regionId, Map attributes, byte operation) {
    CacheRegion region = (CacheRegion) regions.get(new Integer(regionId));
    if (region != null) {
      region.removeCacheGroup();
    }
//	  NotificationListener listener = (NotificationListener) listeners.get(new Integer(regionId));
//	  msg = "Listeners = " + listeners + "listener = " + listener + "\n"; 
//	  operation = (byte)-operation;
//	  if (listener != null) {
//	    listener.invalidate(attributes, operation);
//	  }
  }

  public void receiveLocal(int regionId, NotificationMessage message) {
    // check the following condition:
    // 1. internal invalidation message with sender of the instance
    // 2. region scope is > local
    // 3. this implies that the invalidation message should not be executed.
    if (message.type == 101) { // 1.
      message.type = NotificationListener.EVENT_INTERNAL_INVALIDATION; // return to reality
      CacheRegion region = (CacheRegion) regions.get(new Integer(regionId));
      int scope = region.getRegionConfigurationInfo().getRegionScope();
      if (scope > RegionConfigurationInfo.SCOPE_LOCAL) { // 2.
        return; // 3.
      }
    }

    // Handle the excentric BI case with semantical invalidations
    // BI scenario that was not tested at all and led to escallations from 5 customers
    // BI contacts on the issue: Krannich, Bernd (D028923); Scheerer, Oliver (D027913); Matthee, Stephan (D032419)
    if (message.type == NotificationListener.EVENT_SEMANTIC_INVALIDATION) {
      Map attributes = (HashMap) message.attributes;
      receiveSemanticLocal(message.regiondId, attributes, NotificationListener.EVENT_SEMANTIC_INVALIDATION);
      return;
    }
    ///////////////////////////////////////////////////////////////////////////////////

    boolean flag = message.type == NotificationListener.EVENT_INTERNAL_INVALIDATION;
    if (message.queue != null) {
      Iterator messages = message.queue.listIterator();
      while (messages.hasNext()) {
        NotificationMessage notificationMessage = (NotificationMessage) messages.next();
        receiveLocal(regionId, notificationMessage.key, flag ? NotificationListener.EVENT_INTERNAL_INVALIDATION : notificationMessage.type);
      }
    } else {
      receiveLocal(regionId, message.key, message.type);
    }
  }

  public void receive(int regionId, String key, byte operation) {
    NotificationListener listener = (NotificationListener) listeners.get(new Integer(regionId));
    if (listener != null) {
      listener.invalidate(regionId, key, operation);
    }
  }

  public void receive(int regionId, NotificationMessage message) {
    CacheRegion region = (CacheRegion) regions.get(new Integer(regionId));
    if (region != null) { // It is possible for a region to exist on several nodes, but not all of them
      // check the following condition:
      // 1. internal invalidation message with sender of the instance
      // 2. region scope is > local
      // 3. this implies that the invalidation message should not be executed.

      // Handle the excentric BI case with semantical invalidations
      // BI scenario that was not tested at all and led to escallations from 5 customers
      // BI contacts on the issue: Krannich, Bernd (D028923); Scheerer, Oliver (D027913); Matthee, Stephan (D032419)
      if (message.type == NotificationListener.EVENT_SEMANTIC_INVALIDATION) {
        Map attributes = (HashMap) message.attributes;
        receiveSemanticLocal(message.regiondId, attributes, NotificationListener.EVENT_SEMANTIC_INVALIDATION);
        return;
      }
      ///////////////////////////////////////////////////////////////////////////////////

      if (message.type == 101) {                                          // 1.
        message.type = NotificationListener.EVENT_INTERNAL_INVALIDATION;  // return to reality
        int scope = region.getRegionConfigurationInfo().getRegionScope();
        if (scope > RegionConfigurationInfo.SCOPE_LOCAL) {                // 2.
          return;                                                         // 3.
        }
      }
      boolean flag = message.type == NotificationListener.EVENT_INTERNAL_INVALIDATION;
      if (message.queue != null) {
        Iterator messages = message.queue.listIterator();
        while (messages.hasNext()) {
          NotificationMessage notificationMessage = (NotificationMessage) messages.next();
          receive(regionId, notificationMessage.key, flag ? NotificationListener.EVENT_INTERNAL_INVALIDATION : notificationMessage.type);
        }
      } else {
        receive(regionId, message.key, message.type);
      }
    } else {
//    TODO Add trace here when CML begins to use logging infrastructure.
    }
  }

  public void hook(NotificationHook hook) {
    if (this.hook == null) {
      this.hook = hook;
    }
  }

  /**
   * The cache implementation must use this method with combination of <code>beginEventBlock</code> to denote
   * that the gathering events in group has ended.
   *
   * @param region            the id of the region that groups several events in a block
   * @param sendNotifications If true, the notification will be executed, otherwise no notifications will be sent
   */
  public void endEventBlock(CacheRegion region, boolean sendNotifications) {
    endEventBlock(region, sendNotifications, false);
  }

  /**
   * Initiates notification when the cache implementation has registered change in the cache region
   *
   * @param region       The cache region that has been changed
   * @param storage      The storage plugin bound to the cache region
   * @param key          The cached object key of the changed object
   * @param operation    The operation that was made on the cached object
   * @param cachedObject The cached object that will be tried to transport if the storage plugin supports
   *                     transporting of cached objects
   * @param synchronous  If true, the method will return after all destinations are notified
   */
  public void notify(CacheRegion region, StoragePlugin storage, String key, byte operation, Object cachedObject, byte invalidationScope, boolean synchronous) {
    int regionId = region.getRegionConfigurationInfo().getId();
    byte scope = invalidationScope == -1 ? region.getRegionConfigurationInfo().getInvalidationScope() : invalidationScope;
    Integer slot = new Integer(regionId);
    SlotStatus status = (SlotStatus) slots.get(slot);

    // Handle the excentric BI case with semantical invalidations
    // BI scenario that was not tested at all and led to escallations from 5 customers
    // BI contacts on the issue: Krannich, Bernd (D028923); Scheerer, Oliver (D027913); Matthee, Stephan (D032419)
    if (scope > RegionConfigurationInfo.SCOPE_LOCAL && operation == NotificationListener.EVENT_SEMANTIC_INVALIDATION) {

      NotificationMessage message = new NotificationMessage();
      message.key = key;
      message.type = operation;
      if (message.type == NotificationListener.EVENT_SEMANTIC_INVALIDATION) {
        message.attributes = (Map) cachedObject;
      }
      message.regiondId = region.getRegionConfigurationInfo().getId();//regionId;
      message.transported = null;
      MonitorsAccessor.getMonitor(region.getRegionConfigurationInfo().getName()).onNotify(scope);
      hook.notify(message, scope, true);
      return;
    }
    //////////////////////////////////////////////////////////////////////////////

    // local listeners
    if (scope != RegionConfigurationInfo.SCOPE_NONE) {
      if (region.getRegionConfigurationInfo().getSenderIsReceiverMode()) {
        receiveLocal(regionId, key, operation); // SLD: This operation should not be called if the user does not want the sender to receive its own notifications
      }
      if (hook != null) {
        if (scope == RegionConfigurationInfo.SCOPE_CLUSTER ||
            scope == RegionConfigurationInfo.SCOPE_INSTANCE) {
          // Internal notify in case of SHM usage
          if (storage.getScope() > RegionConfigurationInfo.SCOPE_LOCAL) { // if storage plugin uses SHM then send internal message in the cluster for invalidation
            NotificationMessage internalMsg = new NotificationMessage();
            internalMsg.key = key;
            internalMsg.type = NotificationListener.EVENT_INTERNAL_INVALIDATION;
            internalMsg.transported = null;
            internalMsg.regiondId = regionId;
            MonitorsAccessor.getMonitor(region.getRegionConfigurationInfo().getName()).onNotify(scope);
            hook.notify(internalMsg, scope, true);
          }
          // call hook
          NotificationMessage message = new NotificationMessage();
          message.key = key;
          message.type = operation;
          if (message.type == NotificationListener.EVENT_SEMANTIC_INVALIDATION) {
            message.attributes = (Map) cachedObject;
          }
          message.transported = null;
          message.regiondId = regionId;
          MonitorsAccessor.getMonitor(region.getRegionConfigurationInfo().getName()).onNotify(scope);
          hook.notify(message, scope, synchronous);
        }
      }
    }
  }

  /**
   * The cache implementation must use this method with combination of <code>beginEventBlock</code> to denote
   * that the gathering events in group has ended.
   *
   * @param region            the id of the region that groups several events in a block
   * @param sendNotifications If true, the notification will be executed, otherwise no notifications will be sent
   * @param synchronous       If true, the method will return after all destinations are notified
   */
  public void endEventBlock(CacheRegion region, boolean sendNotifications, boolean synchronous) {
    byte scope = region.getRegionConfigurationInfo().getInvalidationScope();
    int regionId = region.getRegionConfigurationInfo().getId();
    Integer slot = new Integer(regionId);
    SlotStatus status = (SlotStatus) slots.get(slot);
    if (status != null) {
      if (sendNotifications) {
        NotificationMessage message = new NotificationMessage();
        message.queue = status.getQueue();
        // local notification
        if (scope != RegionConfigurationInfo.SCOPE_NONE) {
          receiveLocal(regionId, message);
          if (hook != null) {
            if (scope == RegionConfigurationInfo.SCOPE_CLUSTER ||
                scope == RegionConfigurationInfo.SCOPE_INSTANCE) {
              // call hook
              NotificationMessage internalMsg = new NotificationMessage();
              internalMsg.type = NotificationListener.EVENT_INTERNAL_INVALIDATION;
              internalMsg.queue = status.getQueue();
              internalMsg.regiondId = regionId;
              MonitorsAccessor.getMonitor(region.getRegionConfigurationInfo().getName()).onNotify(scope);
              hook.notify(internalMsg, scope, true);

              message.regiondId = regionId;
              MonitorsAccessor.getMonitor(region.getRegionConfigurationInfo().getName()).onNotify(scope);
              hook.notify(message, scope, synchronous);
            }
          }
        }
      }
      status.endBlock();
    }
  }

}
