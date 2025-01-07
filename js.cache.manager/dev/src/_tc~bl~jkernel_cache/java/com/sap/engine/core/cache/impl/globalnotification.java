package com.sap.engine.core.cache.impl;

import com.sap.engine.cache.communication.Notification;
import com.sap.engine.cache.communication.NotificationHook;
import com.sap.engine.cache.communication.NotificationMessage;
import com.sap.engine.cache.core.impl.CacheRegionImpl;
import com.sap.engine.core.Framework;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.core.cluster.MessageObject;
import com.sap.engine.core.cluster.InternalMessageListenerWithCallBack;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.core.cluster.analysis.ClusterCallback;
import com.sap.util.cache.RegionConfigurationInfo;

/**
 * @author Petev, Petio, i024139
 */
class GlobalNotification implements InternalMessageListenerWithCallBack, NotificationHook {

  // these types of messages denote wether a request/respond or plain send will be used
  private static int MESSAGE_TYPE_SYNC = 1;
  private static int MESSAGE_TYPE_NONSYNC = 2;

  // empty message, this is the message array that is sent. It will not become a part
  // of any pool because of its small size :). So it is reused freely.
  // An empty message is used for response message as a response is needed just for
  // synchronization
  private static byte[] emptyArray = new byte[0];

  // the core of the messageing - cluster manager
  private ClusterManager cm = null;
  private int serviceId = -1;
  private int groupId = -1;

  private Notification notification = CacheRegionImpl.getNotification();

  // denotes if the notification is capable of working, i.e. cluster manager is o.k.
  private boolean functional;

  //for cluster message processing analysing
  private com.sap.engine.core.cluster.analysis.ClusterCallback callBack;

  public GlobalNotification() {
    try {
      cm = (ClusterManager) Framework.getManager("ClusterManager");
      if (cm != null) {
        functional = true;
      }
    } catch (NullPointerException npe) {
      CacheManagerImpl.traceT(npe);
      // no cluster manager, the notificator will not be functional
      functional = false;
    }
  }

  private static final String SERVICE_NAME = "_CacheManager";

  // constructor
  public void construct() throws ClusterException {
    if (functional) {
      // register listener and hook to the notification in the cache library
      serviceId = cm.ms_registerService(SERVICE_NAME);
      groupId = cm.getClusterMonitor().getCurrentParticipant().getGroupId();
      cm.ms_registerMessageListener(serviceId, this);
      notification.hook(this);
    }
  }

  // destructor
  public void destruct() {
    if (functional) {
      // unregister. The destruct method is called when the manager is shut down
      cm.ms_unregisterMessageListener(serviceId);
    }
  }

  public void process(int clusterId, MessageObject messageObject) {

    if (functional) {
      int messageType = messageObject.getMessageType();

      Object state = null;
      Exception exception = null;
      if (callBack != null) {
        com.sap.engine.core.cluster.analysis.ClusterCallback.AnalysisMessageObject analysisMessageObject = new com.sap.engine.core.cluster.analysis.ClusterCallback.AnalysisMessageObject(
                messageObject.getMessageType(), messageObject.getBody(), messageObject.getOffset(), messageObject.getLength(),
                messageObject.isRequest(), messageObject.isLazy(), messageObject.isRemoteException());
        if (messageType == MESSAGE_TYPE_SYNC) {
          state = callBack.receiveRequestEnter(serviceId, SERVICE_NAME, clusterId, analysisMessageObject);
        } else {
          state = callBack.receiveOnewayMessageEnter(serviceId, SERVICE_NAME, clusterId, analysisMessageObject);
        }
      }
      try {
        byte[] body = messageObject.getBody();
        NotificationMessage message = NotificationMessage.read(body);
        int regionId = message.regiondId;
        notification.receive(regionId, message);
        if (messageType == MESSAGE_TYPE_SYNC) {
          // we will need to send a response in order to sync on the other side

          if (callBack != null) {
            callBack.receiveRequestMessageProcessed(state, null);
          }

          try {
            cm.ms_sendResponse(messageObject, emptyArray, 0, 0);
          } catch (ClusterException e) {
            CacheManagerImpl.traceT(e);
          }
        }
      } finally {
        if (callBack != null) {
          if (messageType == MESSAGE_TYPE_SYNC) {
            callBack.receiveRequestExit(state, exception);
          } else {
            callBack.receiveOnewayMessageExit(state, exception);
          }
        }
      }
    }
  }

  public void notify(NotificationMessage message) {
    // just delegate
    if (functional) {
      notify(message, RegionConfigurationInfo.SCOPE_CLUSTER, false);
    }
  }

  public void notify(NotificationMessage message, byte scope, boolean sync) {
    if (functional) {
      int groupId = 0;
      if (scope == RegionConfigurationInfo.SCOPE_INSTANCE) {
        groupId = this.groupId;
      } else {
      }
      byte[] data = NotificationMessage.write(message);

      // if the notification must be synchronous, we will use request-response approach,
      // otherwise - just plain old send

      if (sync) {
        try {
          cm.ms_sendRequest(serviceId, groupId, (byte) -1, MESSAGE_TYPE_SYNC, data, 0, data.length, (long) 10000);
        } catch (ClusterException e) {
          CacheManagerImpl.traceT(e);
        }
      } else {
        cm.ms_sendMessage(serviceId, groupId, (byte) -1, MESSAGE_TYPE_NONSYNC, data, 0, data.length);
      }
    }
  }

  public void setCallBackInterface(ClusterCallback callBack) {
    this.callBack = callBack;
  }
}
