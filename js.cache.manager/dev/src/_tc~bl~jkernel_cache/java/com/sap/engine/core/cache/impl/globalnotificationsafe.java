/*
 * Created on 2004.12.1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import com.sap.engine.cache.communication.Notification;
import com.sap.engine.cache.communication.NotificationHook;
import com.sap.engine.cache.communication.NotificationListener;
import com.sap.engine.cache.communication.NotificationMessage;
import com.sap.engine.cache.core.impl.CacheRegionImpl;
import com.sap.engine.core.Framework;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.util.cache.RegionConfigurationInfo;

/**
 * @author Petev, Petio, i024139
 */
class GlobalNotificationSafe implements MessageListener, NotificationHook {

  // the core of the messageing - cluster manager
  private ClusterManager cm = null;
  private MessageContext mc = null;
  private int groupId = -1;
  private int thisClusterId = -1;

  private Notification notification = CacheRegionImpl.getNotification();

  // denotes if the notification is capable of working, i.e. cluster manager is o.k.
  private boolean functional;

  public GlobalNotificationSafe() {
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

  // constructor
  public void construct() throws ClusterException {
    if (functional) {
      // register listener and hook to the notification in the cache library
      mc = cm.getMessageContext("_CacheManager");
      groupId = cm.getClusterMonitor().getCurrentParticipant().getGroupId();
      thisClusterId = cm.getClusterMonitor().getCurrentParticipant().getClusterId();
      mc.registerListener(this);
      notification.hook(this);
    }
  }

  // destructor
  public void destruct() {
    if (functional) {
      // unregister. The destruct method is called when the manager is shut down
      mc.unregisterListener();
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
      }
      byte[] data = NotificationMessage.write(message);
      // if the notification must be synchronous, we will use send and wait for answer,
      // otherwise - just plain old send
      if (sync) {
        try {
          mc.sendAndWaitForAnswer(groupId, (byte) -1, scope, data, 0, data.length, (long) 10000);
        } catch (ClusterException e) {
          CacheManagerImpl.traceT(e);
        }
      } else {
        try {
          mc.send(groupId, (byte) -1, scope, data, 0, data.length);
        } catch (ClusterException e) {
          CacheManagerImpl.traceT(e);
        }
      }
    }
  }
  
  public void receive(int clusterId, int messageType, byte[] body, int offset, int length) {
    if (functional) {
      if (clusterId != thisClusterId) {
        NotificationMessage message = NotificationMessage.read(body);
        int regionId = message.regiondId;
        // check the condition of instance bound sender for internal invalidation
        if (message.type == NotificationListener.EVENT_INTERNAL_INVALIDATION) {
          int senderGroupId = cm.getClusterMonitor().getParticipant(clusterId).getGroupId();
          if (groupId == senderGroupId) {
            message.type = 101; // !!! this is the notion that there is internal invalidation
                                // within the same instance
          }
        }
        notification.receive(regionId, message);
      }
    }
  }

  private static final MessageAnswer emptyAnswer = new MessageAnswer();
  
  public MessageAnswer receiveWait(int clusterId, int messageType, byte[] body, int offset, int length) throws Exception {
    receive(clusterId, messageType, body, offset, length);
    return emptyAnswer;
  }

  // DUMP
  
//  private PrintWriter writer = null;
//
//  private void initDump(String fileName) {
//    try {
//      fileName += " (" + (new Date()).toString() + ")";
//      fileName = fileName.replace(':', '_');
//      File file = (new File(fileName)).getCanonicalFile();
//      file.getParentFile().mkdirs();
//      writer = new PrintWriter(new FileOutputStream(file), false);
//    } catch (IOException e) {
//      CacheManagerImpl.traceT(e);
//      writer = null;
//    }
//  }
//
//  void write(String s) {
//    if (writer == null) {
//      initDump("K:\\temp\\dump_notify.txt");
//    }
//    if (writer != null) {
//      synchronized (this) {
//        writer.print("[");
//        writer.print(new Date().toString());
//        writer.print("] : ");
//        writer.println(s);
//        writer.flush();
//      }
//    }
//  }
//
//  private void _dump(String what) {
//    write(what);
//  }

}
