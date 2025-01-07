/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.cache;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.ClusterContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.event.ClusterEventListener;
import com.sap.engine.frame.cluster.event.ServiceEventListener;
import com.sap.engine.frame.cluster.message.DestinationLostException;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.frame.cluster.message.MultipleAnswer;
import com.sap.engine.frame.cluster.message.PartialResponseException;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.lib.util.ArrayInt;
import com.sap.engine.lib.util.ConcurrentArrayObject;
import com.sap.engine.services.jndi.JNDIManager;
import com.sap.engine.services.jndi.persistentimpl.memory.JNDIMemoryImpl;
import com.sap.engine.services.jndi.persistentimpl.memory.util.IntNum;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Used for JNDI's cache managment in the cluster.
 * Used for replication of naming operations in cluster.
 *
 * @author Hristo Iliev, Elitsa Pancheva
 * @version 4.0
 */
public class CacheCommunicatorImpl implements ServiceEventListener, MessageListener, ClusterEventListener {

  private final static Location LOG_LOCATION = Location.getLocation(CacheCommunicatorImpl.class);

  /**
   * Stores a message context
   */
  static MessageContext msgContext = null;
  /**
   * Stores a cluster context
   */
  public static ClusterContext clContext = null;
  /**
   * Constant used to indicate that the received message contains object which have to be bound on this server
   */
  public final static int REPLICATE_OBJECT_BIND = 2;
  /**
   * Constant used to indicate that the received message contains object which have to be rebound on this server
   */
  public final static int REPLICATE_OBJECT_REBIND = 3;
  /**
   * Constant used to indicate that the received message contains container ID which have to be used for a new container on this server
   */
  public final static int REPLICATE_CONTAINER_CREATE = 4;
  /**
   * Constant used to indicate that the received message contains container ID and the container with this ID have to be destoyed on this server
   */
  public final static int REPLICATE_CONTAINER_DELETE = 5;
  /**
   * Constant used to indicate that the received message contains container ID and object ID which have to be linked on this server
   */
  public final static int REPLICATE_LINK_OBJECT_TO_CONTAINER = 6;
  /**
   * Constant used to indicate that the received message contains container ID and data and the container with this ID have to be modified on this server
   */
  public final static int REPLICATE_CONTAINER_MODIFY = 7;
  /**
   * Constant used to indicate that the received message contains container ID and object ID which have to be no more linked on this server
   */
  public final static int REPLICATE_REMOVE_LINKED_CONTAINER = 8;
  /**
   * Constant used to indicate that the received message contains object which have to be renamed on this server
   */
  public final static int REPLICATE_RENAME_OBJECT = 9;
  /**
   * Constant used to indicate that the received message contains object which have to be unbound on this server
   */
  public final static int REPLICATE_UNBIND_OBJECT = 10;
  /**
   * Constant used to indicate that the received message contains request for replication of all global bindings this server
   */
  public final static int REPLICATE_ALL_BINDINGS = 11;
  /**
   * Constant used to indicate the end of sendding of global bindings
   */
  public final static int END_OF_GLOBAL_OBJECTS_SENDDING = 12;
  /**
   * Constant used to indicate locking of this server
   */
  public final static int CLUSTER_LOCK = 13;
  /**
   * Constant used to indicate unlocking of this server
   */
  public final static int CLUSTER_UNLOCK = 14;
  /**
   * Constant used to indicate a collection of messages describing global objects
   */
  public final static int REPLICATION_MESSAGE = 15;
  /**
   * Monitor for synchronization purpouse
   */
  public static MonitorBlock monitor = new MonitorBlock();
  /**
   * array with the clusterIds of all servers that have locked this one
   */
  static ArrayInt whoLockedMe = new ArrayInt();
  /**
   * boolean showing if the replication of all global objects is finished on this server
   */
  static boolean replicationCompleted = false;
  /**
   * integer showing how meny objects and containers have been replicated so far
   */
  public ReplicationCounter replicationCounter = new ReplicationCounter(0);

  /**
   * Constructor
   *
   * @param srvCtx Application service context to use
   */
  public CacheCommunicatorImpl(ApplicationServiceContext srvCtx) {
    this.msgContext = srvCtx.getClusterContext().getMessageContext();
    //this.clMonitor = srvCtx.getClusterContext().getClusterMonitor();
    //clMonitor.getClusterEventMonitor().registerListener(this);
    this.clContext = srvCtx.getClusterContext();
    try {
      msgContext.registerListener(this);
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Message listener successfully registered.");
      }
    } catch (ListenerAlreadyRegisteredException lare) {
      // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here.
      // Please do not remove this comment!
      msgContext.unregisterListener();
      try {
        msgContext.registerListener(this);
        if (LOG_LOCATION.bePath()) {
          LOG_LOCATION.pathT("Message listener successfully registered.");
        }
      } catch (ListenerAlreadyRegisteredException e) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "ListenerAlreadyRegisteredException while trying to register MessageListener.", e);
        }
      }
    }

    try {
      srvCtx.getServiceState().registerServiceEventListener(this);
      srvCtx.getServiceState().registerClusterEventListener(this);
    } catch (ListenerAlreadyRegisteredException e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "ListenerAlreadyRegisteredException while trying to register ServiceEventListener.", e);
      }
    }
  }

  //invoked when on the specified elementInfo the service is ready for communication
  public void serviceStarted(ClusterElement elementInfo) {
//    if (elementInfo.getType() == ClusterElement.SERVER) {
//      clusterElementsIds.add(new Integer(elementInfo.getClusterId()));
//    }
  }

  //invoked when on the specified elementInfo the service is goinhready for communication
  public void serviceStopped(ClusterElement elementInfo) {
//    int elementID = elementInfo.getClusterId();
//    clusterElementsIds.remove(new Integer(elementID));
  }

  /**
   * Invoked when a message from a cluster element is sent and there is no waiting for answer.
   *
   * @param clusterId The id of the cluster element which has sent the message.
   * @param messageId The id of the message.
   * @param message The message content.
   */
  public void receive(int clusterId, int messageId, byte[] message, int offset, int length) {
//    System.out.println("in receive() - msg id = " + messageId);
    try {
      if (messageId == REPLICATION_MESSAGE) {
        // proccessing the big message, bytes are ordered this way :
        //           number of bytes used for message type,
        //           message type,
        //           number of bytes used for message lenght,
        //           message lenght,
        //           message

        int i = 0;
        do {
          byte sizeOfMessageType = message[i];
          byte[] msgType_ = new byte[sizeOfMessageType];
          i = i + 1;
          if (sizeOfMessageType > 0) {
            for (int j = 0; j < sizeOfMessageType; j++, i++) {
              msgType_[j] = message[i];
            }
          }

          int msgType = Convert.byteArrToInt(msgType_, 0);
          byte sizeOfMessageLength = message[i];
          byte[] msgLength_ = new byte[sizeOfMessageLength];
          i = i + 1;

          if (sizeOfMessageLength > 0) {
            for (int j = 0; j < sizeOfMessageLength; j++, i++) {
              msgLength_[j] = message[i];
            }
          }

          int msgLength = Convert.byteArrToInt(msgLength_, 0);
          byte[] msg = new byte[msgLength];

          if (msgLength > 0) {
            for (int j = 0; j < msgLength; j++, i++) {
              msg[j] = message[i];
            }
          }
          messageProcesing(clusterId, msgType, msg);
        } while (i < length);
      } else {
        messageProcesing(clusterId, messageId, message);
      }

    } catch (Exception e) {
      if (LOG_LOCATION.beError()) {
        String opType = "unknown";
        if (messageId == REPLICATE_OBJECT_BIND) { //2=bind , 3=rebind, 4=createContainer, 5=deleteContainer, 6=link object to container, 7= modify container, 8=replicateRemoveLinkedContainer, 9=renameObject, 10=replicate unbind
          opType = "bind";
        } else if (messageId == REPLICATE_OBJECT_REBIND) {
          opType = "rebind";
        } else if (messageId == REPLICATE_CONTAINER_CREATE) {
          opType = "createSubcontext";
        } else if (messageId == REPLICATE_CONTAINER_DELETE) {
          opType = "destroySubcontext";
        } else if (messageId == REPLICATE_CONTAINER_MODIFY) {
          opType = "modifyContainer";
        } else if (messageId == REPLICATE_RENAME_OBJECT) {
          opType = "rename";
        } else if (messageId == REPLICATE_UNBIND_OBJECT) {
          opType = "unbind";
        }
        SimpleLogger.traceThrowable(Severity.ERROR,LOG_LOCATION,e, "ASJ.jndi.000012", "Exception while processing message from cluster element with ID [{0}]. The replicated jndi operation[{1}] failed to complete on this server node[{2}]",  new Object[] { clusterId, opType, clContext.getClusterMonitor().getCurrentParticipant().getClusterId()});
      }
      RuntimeException re = new RuntimeException("Exception while processing message from cluster element with ID " + clusterId + ".", e);

      throw re;
    }
  }

  //generates the String that was encoded in the byte[]
  private String getStringFromBytes(byte[] source, IntNum counter) {
    //extract the length of the string's byte[]
    byte stringLenInBytesLen = source[counter.getNum()];
    byte[] stringLen = new byte[stringLenInBytesLen];
    counter.inc();

    if (stringLenInBytesLen > 0) {
      for (int j = 0; j < stringLenInBytesLen; j++, counter.inc()) {
        stringLen[j] = source[counter.getNum()];
      }
    }
    //extract the byte[] that represents the String
    byte[] result = new byte[Convert.byteArrToInt(stringLen, 0)];

    if (result.length > 0) {
      for (int j = 0; j < result.length; j++, counter.inc()) {
        result[j] = source[counter.getNum()];
      }
    }
    return new String(result);
  }
  
  //generates an int that was encoded in the byte[]
  private int getIntFromBytes(byte[] source, IntNum counter) {
    byte intByteLen = source[counter.getNum()];
    byte[] intByte = new byte[intByteLen];
    counter.inc();

    if (intByteLen > 0) {
      for (int j = 0; j < intByteLen; j++, counter.inc()) {
        intByte[j] = source[counter.getNum()];
      }
    }
    return Convert.byteArrToInt(intByte, 0);
  }
  
  //extract data bytes from a byte[]
  private byte[] getDataFromBytes(byte[] source, IntNum counter) {
    int dataLen = getIntFromBytes(source, counter);
    byte[] result = new byte[dataLen]; 

    if (dataLen > 0) {
      for (int j = 0; j < dataLen; j++, counter.inc()) {
        result[j] = source[counter.getNum()];
      }
    }
    return result;
  }
  
  private void messageProcesing(int clusterId, int messageId, byte[] message) throws javax.naming.NamingException {
//     proccessing the message, bytes are ordered this way in case of createSubcontext operation:
//            number of bytes used for container data,
//            containerData,
//            clusterID size,
//            clusterID,
//            number of bytes used for objectName's size,
//            objectName's size,
//            objectName,
//            number of bytes used for containerName's size,
//            containerName's size,
//            containerName,
//            number of bytes used for obj data,
//            objData
//     other operation:
//            number of bytes used for additonalString in case of rename/name of the container to delete in case of destroy subcontext,
//            additonalString size,
//            additonalString id,
//            clusterID size,
//            clusterID,
//            number of bytes used for objectName's size,
//            objectName's size,
//            objectName,
//            number of bytes used for containerName's size,
//            containerName's size,
//            containerName,
//            number of bytes used for obj data,
//            objData
    IntNum i = new IntNum(1);
    int clID = 0;
    String oName_ = null;
    String containerName_ = null;
    String objectName_ = null;
    byte[] objData = null;
    byte[] contData = null;
    
    //perform control bit check
    if(message[0] == 0) {
      //get the additional String (new object name in case of Rename) or linkString in cases of create subcontext
      //or in case of destroy subcontext - the name of the container to delete
      oName_ = getStringFromBytes(message, i);
    } else {
      //get container's data 
      contData = getDataFromBytes(message, i);
    }
    
    //get source cluster ID
    clID = getIntFromBytes(message, i);
    //get the object name
    objectName_ = getStringFromBytes(message, i);
    //get the object's container name
    containerName_ = getStringFromBytes(message, i);
    //get object's data
    objData = getDataFromBytes(message, i);
    
    short operationType = message[i.getNum()];

    if (messageId == REPLICATE_OBJECT_BIND) { //2=bind , 3=rebind, 4=createContainer, 5=deleteContainer, 6=link object to container, 7= modify container, 8=replicateRemoveLinkedContainer, 9=renameObject, 10=replicate unbind
      ((JNDIMemoryImpl) JNDIManager.db).replicateObjectBind(containerName_, objectName_, objData, operationType, clID);
    } else if (messageId == REPLICATE_OBJECT_REBIND) {
      ((JNDIMemoryImpl) JNDIManager.db).replicateObjectRebind(containerName_, objectName_, objData, operationType);
    } else if (messageId == REPLICATE_CONTAINER_CREATE) {
      ((JNDIMemoryImpl) JNDIManager.db).replicateContext(objectName_, containerName_, objData, contData, clID, operationType);
    } else if (messageId == REPLICATE_CONTAINER_DELETE) {
      ((JNDIMemoryImpl) JNDIManager.db).replicateContextDelete(containerName_, objectName_, oName_);
    } else if (messageId == REPLICATE_LINK_OBJECT_TO_CONTAINER) {
      ((JNDIMemoryImpl) JNDIManager.db).replicateLinkObjectToContainer(containerName_, objectName_, oName_);
    } else if (messageId == REPLICATE_CONTAINER_MODIFY) {
      ((JNDIMemoryImpl) JNDIManager.db).replicateModifyContainer(containerName_, objData);
    } else if (messageId == REPLICATE_REMOVE_LINKED_CONTAINER) {
      ((JNDIMemoryImpl) JNDIManager.db).replicateRemoveLinkedContainer(containerName_, objectName_);
    } else if (messageId == REPLICATE_RENAME_OBJECT) {
      ((JNDIMemoryImpl) JNDIManager.db).replicateRenameObject(containerName_, objectName_, oName_);
    } else if (messageId == REPLICATE_UNBIND_OBJECT) {
      ((JNDIMemoryImpl) JNDIManager.db).replicateUnbindObject(containerName_, objectName_);
    }
  }

  /**
   * Invoked when a message from a cluster element is sent and there is a waiting for answer at
   * the specified cluster element.
   *
   * @param clusterId The id of the cluster element which has sent the message.
   * @param messageId The id of the message.
   * @param message The message content.
   * @return The reply message the listener must return.
   */
  public MessageAnswer receiveWait(int clusterId, int messageId, byte[] message, int offset, int length) {
//    System.out.println("Received message from ["+clusterId+"] with id ["+messageId+"] with length ["+length+"]  thread [ " + Thread.currentThread() +" ]");
      byte[] id = new byte[4];
      if (messageId == CLUSTER_LOCK) {
        if (!replicationCompleted) {
          Convert.writeIntToByteArr(id, 0, 2);
          tryToLock(clusterId);
          return new MessageAnswer(id, 0, 4);
        } else {
          Convert.writeIntToByteArr(id, 0, tryToLock(clusterId));
          return new MessageAnswer(id, 0, 4);
        }
      } else if (messageId == CLUSTER_UNLOCK) {
        Convert.writeIntToByteArr(id, 0, unlock(clusterId));
        return new MessageAnswer(id, 0, 4);
      } else if (messageId == REPLICATE_ALL_BINDINGS) {
        int objectsCounter = -1;
        try {
          objectsCounter = ((JNDIMemoryImpl) JNDIManager.db).toSendGlobalObjects(clusterId, null);
        } catch (javax.naming.NamingException e) {
          if (LOG_LOCATION.beWarning()) {
            SimpleLogger.traceThrowable(Severity.WARNING,LOG_LOCATION,e, "ASJ.jndi.000023", "Exception while sending information for global bindings to server process [{0}]. Exception is: [{1}]. Result: some or all global objects/contexts will not be available for lookup from the naming system on this server process[{2}]",  new Object[] { String.valueOf(clusterId),e.toString(), clContext.getClusterMonitor().getCurrentParticipant().getClusterId()});
          }
        }
        byte[] answer = new byte[4];
        Convert.writeIntToByteArr(answer, 0, objectsCounter);
        return new MessageAnswer(answer, 0, answer.length);
      }

    return new MessageAnswer(new byte[0], 0, 0);
  }

  private int tryToLock(int clusterId) {
    synchronized (whoLockedMe) {
      if (whoLockedMe.isEmpty()) {
        monitor.tryToChange();
        whoLockedMe.add(clusterId);
        return 0;
      } else if (!whoLockedMe.contains(clusterId)) {
        whoLockedMe.add(clusterId);
        return 0;
      } else {
        return 1;
      }
    }
  }

  private int unlock(int clusterId) {
    synchronized (whoLockedMe) {
      if (whoLockedMe.contains(clusterId)) {
        whoLockedMe.remove(clusterId);
        if (whoLockedMe.isEmpty()) {
          monitor.endChange();
        }
      }
      return 0;
    }
  }

  /**
   * Send a message to all servers in cluster
   * to make a replicated operation
   *
   * @param message byte array with necessary information for replication
   * @param type which naming operation have to be replicated
   * @param length the length of the message
   */
  public void sendToAll(byte[] message, int type, int length, String fullObjPath) {
    if (clContext.getClusterMonitor().getCurrentParticipant().getState() != ClusterElement.DEBUGGING) {
      try {
        // sends a message to all server nodes except the current one
        msgContext.send(-1, ClusterElement.SERVER, type, message, 0, length);
      } catch (ClusterException ce) {
        if (LOG_LOCATION.beError()) {
          String opType = "unknown";
          if (type == REPLICATE_OBJECT_BIND) { //2=bind , 3=rebind, 4=createContainer, 5=deleteContainer, 6=link object to container, 7= modify container, 8=replicateRemoveLinkedContainer, 9=renameObject, 10=replicate unbind
            opType = "bind";
          } else if (type == REPLICATE_OBJECT_REBIND) {
            opType = "rebind";
          } else if (type == REPLICATE_CONTAINER_CREATE) {
            opType = "createSubcontext";
          } else if (type == REPLICATE_CONTAINER_DELETE) {
            opType = "destroySubcontext";
          } else if (type == REPLICATE_CONTAINER_MODIFY) {
            opType = "modifyContainer";
          } else if (type == REPLICATE_RENAME_OBJECT) {
            opType = "rename";
          } else if (type == REPLICATE_UNBIND_OBJECT) {
            opType = "unbind";
          }
          SimpleLogger.traceThrowable(Severity.ERROR,LOG_LOCATION,ce, "ASJ.jndi.000013", "ClusterException while sending message to all cluster elements. The message isn't sent, the jndi operation[{1}] on object[{2}] will fail to replicate", new Object[] { opType, fullObjPath});
        }
        RuntimeException re = new RuntimeException("ClusterException while sending message to all cluster elements.", ce);
        throw re;
      }
    }
  }

  public MultipleAnswer sendToAllAndWaitForAnswer(byte[] message, int type, int length) {
    MultipleAnswer ma = null;
    if (clContext.getClusterMonitor().getCurrentParticipant().getState() != ClusterElement.DEBUGGING) {
      try {
        // sends a message to all server nodes except the current one
        ma = msgContext.sendAndWaitForAnswer(0, ClusterElement.SERVER, type, message, 0, length, 100 * 1000);
      } catch (PartialResponseException pre) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Not all server nodes have responded to the message with type: " + type + ". Exception is: " + pre.toString(), pre);
        }
        ma = pre.getPartialResponse();
      } catch (ClusterException ce) {
        if (LOG_LOCATION.beError()) {
          SimpleLogger.traceThrowable(Severity.ERROR,LOG_LOCATION,ce, "ASJ.jndi.000014", "ClusterException while sending message to all cluster elements. Replication of all global naming bindings failed on the current server process[{0}]", new Object[] { clContext.getClusterMonitor().getCurrentParticipant().getClusterId()});
        }
        RuntimeException re = new RuntimeException("ClusterException while sending message to all cluster elements.", ce);

        throw re;
      }
    }
    return ma;
  }

  /**
   * Send a message to a server in cluster
   * to make a replicated operation
   *
   * @param clusterId clusterId of the server to which this message will be sent
   * @param message byte array with necessary information for replication
   * @param type which naming operation have to be replicated
   * @param length the length of the message
   */
  public void sendToServer(int clusterId, byte[] message, int type, int length) {
    int idToSend = 0;
    if (clContext.getClusterMonitor().getCurrentParticipant().getState() != ClusterElement.DEBUGGING) {
      ClusterElement clElement = clContext.getClusterMonitor().getParticipant(clusterId);
      if (clusterId == 0) {
        ConcurrentArrayObject clusterElements = getClusterElements();

        if (clusterElements.size() > 0) {
          idToSend = ((Integer) clusterElements.firstElement()).intValue();
        } else {
          return;
        }
      } else if (clElement != null && clElement.getState() != ClusterElement.DEBUGGING) {
        idToSend = clusterId;
      } else {
        return;
      }

      try {
        msgContext.send(idToSend, type, message, 0, length);
      } catch (ClusterException ce) {
        if (LOG_LOCATION.beError()) {
          SimpleLogger.traceThrowable(Severity.ERROR,LOG_LOCATION,ce, "ASJ.jndi.000015", "ClusterException while sending message to cluster element with ID [{0}]. The message isn't sent, not all global bindings are replicated on this server process[{1}]",  new Object[] { idToSend, clContext.getClusterMonitor().getCurrentParticipant().getClusterId()});
        }
        RuntimeException re = new RuntimeException("ClusterException while sending message to cluster element with ID " + String.valueOf(idToSend) + ".", ce);

        throw re;
      }
    }
  }

  public MessageAnswer sendToServerAndWaitForAnswer(int clusterId, byte[] message, int type, int length) {
    if (clContext.getClusterMonitor().getCurrentParticipant().getState() != ClusterElement.DEBUGGING) {
      ClusterElement clElement = clContext.getClusterMonitor().getParticipant(clusterId);
      if (clElement != null && clElement.getState() != ClusterElement.DEBUGGING) {
        try {
          return msgContext.sendAndWaitForAnswer(clusterId, type, message, 0, length, 100 * 1000);
        } catch (ClusterException ce) {
          if (ce instanceof DestinationLostException) {
            if (LOG_LOCATION.beError()) {
              SimpleLogger.traceThrowable(Severity.ERROR,LOG_LOCATION,ce, "ASJ.jndi.000016", "The server process: [{0}] crashed while sending all global naming data to the current server node[{1}]. Replication of all global naming bindings failed",  new Object[] { ((DestinationLostException) ce).getClusterId(), clContext.getClusterMonitor().getCurrentParticipant().getClusterId()});
            }

            RuntimeException bre = new RuntimeException("The server node: " + String.valueOf(((DestinationLostException) ce).getClusterId()) + " crashed while sending all the global naming data to the current server node. Replication of all global naming bindings failed.", ce);
            throw bre;
          }

          if (LOG_LOCATION.beError()) {
            SimpleLogger.traceThrowable(Severity.ERROR,LOG_LOCATION,ce, "ASJ.jndi.000017", "ClusterException while sending message to cluster element with ID [{0}]. Replication of all global naming bindings failed on the current server process[{1}]",  new Object[] { clusterId, clContext.getClusterMonitor().getCurrentParticipant().getClusterId()});
          }
          RuntimeException re = new RuntimeException("ClusterException while sending message to cluster element with ID " + String.valueOf(clusterId) + ".", ce);

          throw re;
        }
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * Gets all servers participating in the cluster
   *
   * @return ConcurrentArrayObject with clusterIds
   */
  public ConcurrentArrayObject getClusterElements() {
    ClusterMonitor clusterMonitor = clContext.getClusterMonitor();
    ClusterElement[] infos = clusterMonitor.getServiceNodes();
    ConcurrentArrayObject clusterElements = new ConcurrentArrayObject();

    for (int i = 0; i < infos.length; i++) {
      if (infos[i] != null && infos[i].getType() == ClusterElement.SERVER && infos[i].getType() != ClusterElement.DEBUGGING) {
        clusterElements.add(new Integer(infos[i].getClusterId()));
      }
    }

    return clusterElements;
  }

  public void setReplicationCompleted(boolean value) {
    replicationCompleted = value;
  }
  
  public void elementJoin(ClusterElement clusterElement) {
    //do nothing
  }

  public void elementLoss(ClusterElement clusterElement) {
    unlock(clusterElement.getClusterId());
  }

  public void elementStateChanged(ClusterElement clusterElement, byte b) {
    //do nothing
  }
}

