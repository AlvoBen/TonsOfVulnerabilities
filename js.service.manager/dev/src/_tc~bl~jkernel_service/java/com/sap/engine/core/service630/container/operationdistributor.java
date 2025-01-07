package com.sap.engine.core.service630.container;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.frame.ProcessEnvironment;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.frame.cluster.message.MultipleAnswer;
import com.sap.engine.frame.cluster.message.PartialResponseException;
import com.sap.engine.frame.container.deploy.zdm.InstanceDescriptor;
import com.sap.engine.frame.container.deploy.zdm.RollingStatus;
import com.sap.engine.frame.container.deploy.zdm.ServerDescriptor;
import com.sap.engine.system.ThreadWrapper;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This singleton class sends notification messages to the active nodes in the cluster (or instance) and trigger
 * and control cluster (or instance) wide operations (1.deploy/remove library; 2.start/stop service). There are
 * five message types, one for remove, one for create component, one for stop and one for start service, and one
 * for synchronization.
 */
public class OperationDistributor implements MessageListener {

  private final static String REGISTRATION_NAME = "tc~bl~jkernel_service";

  //message types
  private final static int TYPE_CREATE          = 0;
  private final static int TYPE_REMOVE          = 1;
  private final static int TYPE_START_SERVICE   = 2;
  private final static int TYPE_STOP_SERVICE    = 3;
  private final static int TYPE_SYNCHRONOZATION = 4;

  //status bytes
  private final static byte COMPLETE = 10;
  private final static byte FAILED   = 20;

  private MemoryContainer memoryContainer;

  private MessageContext messageContext;

  //instance ID
  private int currentInstanceID;
  //node ID
  private int currentNodeID;

  //true if this node is ready for binaries download
  private boolean readyForBinaryDownload;
  //download binaries status
  private boolean isBinariesDownloadSuccessfully;

  //synchronizers
  private final Object lock = new Object();

  private static final Location LOCATION = Location.getLocation(OperationDistributor.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  OperationDistributor(MemoryContainer memoryContainer) {
    this.memoryContainer = memoryContainer;
    ClusterManager clusterManager = (ClusterManager) Framework.getManager(Names.CLUSTER_MANAGER);
    currentInstanceID = clusterManager.getClusterMonitor().getCurrentParticipant().getGroupId();
    currentNodeID = clusterManager.getClusterMonitor().getCurrentParticipant().getClusterId();
    messageContext = clusterManager.getMessageContext(REGISTRATION_NAME);
    try {
      messageContext.registerListener(this);
    } catch (ListenerAlreadyRegisteredException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Listener already registered exception", e);
      }
    }
  }

  void destroy() {
    messageContext.unregisterListener();
  }

  public void receive(int clusterId, int messageType, byte[] body, int offset, int length) {
    switch (messageType) {
      case TYPE_SYNCHRONOZATION : {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("> Binaries synchronized message received from " + clusterId);
        }
        //notify that synchronization of the binaries for this instance is complete
        synchronized (lock) {
          isBinariesDownloadSuccessfully = body[offset] == COMPLETE;
          lock.notify();
        }
      } break;
      case TYPE_START_SERVICE :
      case TYPE_STOP_SERVICE : {
        String name = new String(body, offset, length);
        ServiceWrapper service = memoryContainer.getServices().get(name);
        try {
          if (messageType == TYPE_START_SERVICE) {
            if (LOCATION.beDebug()) {
              LOCATION.debugT("> Start service " + name + " message received from " + clusterId);
            }
            memoryContainer.startServiceRuntime(service);
          } else {
            if (LOCATION.beDebug()) {
              LOCATION.debugT("> Stop service " + name + " message received from " + clusterId);
            }
            memoryContainer.stopServiceRuntime(service);
          }
        } catch (ServiceException e) {
          if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
            SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
                "Operation fails at element with cluster ID [" + 
                  currentNodeID + "]", e);
          }
        }
      } break;
    }
  }

  public MessageAnswer receiveWait(int clusterId, int messageType, byte[] body, int offset, int length) {
    MessageAnswer result = new MessageAnswer();
    byte[] resultBody = new byte[] {FAILED};
    switch (messageType) {
      case TYPE_CREATE :
      case TYPE_REMOVE : {
        readyForBinaryDownload = false; //initial status
        try {
          byte type = body[offset];
          String name = new String(body, offset + 1, length - 1);
          if (messageType == TYPE_CREATE) {
            if (LOCATION.beDebug()) {
              LOCATION.debugT("> Create " + name + " message received from " + clusterId);
            }
            memoryContainer.startCreate(name, type);
          } else {
            if (LOCATION.beDebug()) {
              LOCATION.debugT("> Remove " + name + " message received from " + clusterId);
            }
            memoryContainer.startRemove(name, type);
          }
          resultBody = new byte[] {COMPLETE};
        } catch (OutOfMemoryError oom) {
          //$JL-EXC$
          ProcessEnvironment.handleOOM(oom);
        } catch (Throwable throwable) {
          //$JL-EXC$
          resultBody = handleError(throwable, messageType);
          if (throwable instanceof ThreadDeath) {
            throw (ThreadDeath) throwable;
          }
        } finally {
          synchronized (lock) {
            readyForBinaryDownload = true; //set this flag true to avoid eventual block of the reply
            lock.notify();
          }
          result.setMessage(resultBody, 0, resultBody.length);
        }
      } break;
      case TYPE_SYNCHRONOZATION : {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("> Ready for binary synchronization message received from " + clusterId);
        }
        synchronized (lock) {
          try {
            while (!readyForBinaryDownload) {
              lock.wait();
            }
          } catch (InterruptedException e) {
            //$JL-EXC$ - If occurs stealthy return the result
          }
        }
      } break;
    }
    return result;
  }

  /**
   * @return the instance ID
   */
  int getInstanceID() {
    return currentInstanceID;
  }

  /**
   * Send message to all server nodes in the cluster to trigger the cluster wide deploy operation.
   * Wait for deploy status on each server node and throw error if on some nodes the deployment
   * was not complete successfully.
   *
   * @param name - the name of the deployed component
   * @param type - component type (service, library or interface)
   * @throws ServiceException - if the deployment fails.
   */
  void sendCreate(String name, byte type) throws ServiceException {
    byte[] body = prepareData(name, type);
    MultipleAnswer answers;
    try {
      answers = messageContext.sendAndWaitForAnswer(0, ClusterElement.SERVER, TYPE_CREATE, body, 0, body.length, 0);
    } catch (PartialResponseException e) {
      LOCATION.traceThrowableT(Severity.WARNING, ResourceUtils.getString(ResourceUtils.PARTIAL_RESPONSE), e);
      answers = e.getPartialResponse();
    } catch (ClusterException e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_SEND_MESSAGE), new Object[] {e.toString()}), e);
    }
    checkForErrors(answers);
  }

  /**
   * Send message to all server nodes in the cluster to trigger the cluster wide undeploy operation.
   * Wait for undeploy status on each server node and throw error if on some nodes the undeploy
   * was not complete successfully.
   *
   * @param name - the name of the deployed component
   * @param type - component type (service, library or interface)
   * @throws ServiceException - if the deployment fails.
   */
  void sendRemove(String name, byte type) throws ServiceException {
    byte[] body = prepareData(name, type);
    MultipleAnswer answers;
    try {
      answers = messageContext.sendAndWaitForAnswer(0, ClusterElement.SERVER, TYPE_REMOVE, body, 0, body.length, 0);
    } catch (PartialResponseException e) {
      LOCATION.traceThrowableT(Severity.WARNING, ResourceUtils.getString(ResourceUtils.PARTIAL_RESPONSE), e);
      answers = e.getPartialResponse();
    } catch (ClusterException e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_SEND_MESSAGE), new Object[] {e.toString()}), e);
    }
    checkForErrors(answers);
  }

  /**
   * Send message to instance server nodes to trigger the instance wide deploy operation.
   * Wait for deploy status on each server node and return instance descriptor which contains operation status
   * for each instance node.
   *
   * @param name - the name of the deployed component
   * @param type - component type (service, library or interface)
   *
   * @return instance descriptor
   * @throws ServiceException - if can not send message.
   */
  InstanceDescriptor sendRolling(String name, byte type) throws ServiceException {
    byte[] body = prepareData(name, type);
    MultipleAnswer answers;
    Set<ServerDescriptor> set = new HashSet<ServerDescriptor>();
    try {
      answers = messageContext.sendAndWaitForAnswer(currentInstanceID, ClusterElement.SERVER, TYPE_CREATE, body, 0, body.length, 0);
    } catch (PartialResponseException e) {
      LOCATION.traceThrowableT(Severity.WARNING, ResourceUtils.getString(ResourceUtils.PARTIAL_RESPONSE), e);
      answers = e.getPartialResponse();
    } catch (ClusterException e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_SEND_MESSAGE), new Object[] {e.toString()}), e);
    }
    int[] participants = answers.participants();
    for (int participant : participants) {
      try {
        MessageAnswer answer = answers.getAnswer(participant);
        byte[] message = answer.getMessage();
        byte result = message[answer.getOffset()];
        if (result == COMPLETE) {
          set.add(new ServerDescriptor(RollingStatus.SUCCESS, participant, null));
        } else {
          String operationFail = ResourceUtils.formatString(ResourceUtils.ACTION_FAILED_AT_CLUSTER_ELEMENT, new Object[] {"" + participant});
          if (answer.getLength() > 1) {
            operationFail += " : " + new String(message, answer.getOffset() + 1, answer.getLength() - 1);
          }
          LOCATION.logT(Severity.ERROR, operationFail);
          set.add(new ServerDescriptor(RollingStatus.ERROR, participant, operationFail));
        }
      } catch (ClusterException e) {
        String message = ResourceUtils.formatString(ResourceUtils.CANT_RECEIVE_MESSAGE, new Object[] {participant + " : " + e.toString()});
        LOCATION.traceThrowableT(Severity.WARNING, message, e);
        set.add(new ServerDescriptor(RollingStatus.ERROR, participant, message));
      }
    }
    return new InstanceDescriptor(set, currentInstanceID);
  }

  /**
   * Notify other nodes in the instance that the instance lock is obtained and wait for 'ready' replys
   */
  void instanceLockObtained() throws ServiceException {
    try {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("> Send instanceLockObtained to instance " + currentInstanceID + " from node " + currentNodeID);
      }
      messageContext.sendAndWaitForAnswer(currentInstanceID, ClusterElement.SERVER, TYPE_SYNCHRONOZATION, new byte[0], 0, 0, 0);
    } catch (PartialResponseException e) {
      //$JL-EXC$
      //some of the nodes has crashed, but the others return the responses
    } catch (ClusterException e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_SEND_MESSAGE), new Object[] {e.toString()}), e);
    }
  }

  /**
   * Notify other nodes in the instance that binaries are synchronized
   */
  void binariesSynchronized(boolean successful) throws ServiceException {
    byte[] body = new byte[1];
    body[0] = (successful) ? COMPLETE : FAILED;
    try {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("> Send binariesSynchronized to instance " + currentInstanceID + " from node " + currentNodeID);
      }
      messageContext.send(currentInstanceID, ClusterElement.SERVER, TYPE_SYNCHRONOZATION, body, 0, body.length);
    } catch (ClusterException e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_SEND_MESSAGE), new Object[] {e.toString()}), e);
    }
  }

  /**
   * Calling this methos will block the thread untill the binaries are synchronized
   */
  boolean isBinariesDownloaded() throws ServiceException {
    synchronized (lock) {
      readyForBinaryDownload = true;
      lock.notify();
      try {
        lock.wait();
        return isBinariesDownloadSuccessfully;
      } catch (InterruptedException e) {
        throw new ServiceException(LOCATION, e);
      }
    }
  }

  /**
   * Send message to all nodes in the instance to trigger the instance wide service start operation.
   *
   * @param name - the name of the service
   * @throws ServiceException - if error occurs.
   */
  void sendStartService(String name) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("> Send start service " + name + " message to instance " + currentInstanceID + " from node " + currentNodeID);
    }
    byte[] body = name.getBytes();
    try {
      messageContext.send(currentInstanceID, ClusterElement.SERVER, TYPE_START_SERVICE, body, 0, body.length);
    } catch (ClusterException e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_SEND_MESSAGE), new Object[] {e.toString()}), e);
    }
  }

  /**
   * Send message to all nodes in the instance to trigger the instance wide service stop operation.
   *
   * @param name - the name of the service
   * @throws ServiceException - if error occurs.
   */
  void sendStopService(String name) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("> Send stop service " + name + " message to instance " + currentInstanceID + " from node " + currentNodeID);
    }
    byte[] body = name.getBytes();
    try {
      messageContext.send(currentInstanceID, ClusterElement.SERVER, TYPE_STOP_SERVICE, body, 0, body.length);
    } catch (ClusterException e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_SEND_MESSAGE), new Object[] {e.toString()}), e);
    }
  }

  private byte[] prepareData(String name, byte type) {
    byte[] result;
    byte[] nameByte = name.getBytes();
    result = new byte[nameByte.length + 1];
    result[0] = type;
    System.arraycopy(nameByte, 0, result, 1, nameByte.length);
    return result;
  }

  private byte[] handleError(Throwable throwable, int messageType) {
    String type = (messageType == TYPE_CREATE) ? "deploy" : "remove";
    LOCATION.traceThrowableT(Severity.ERROR, ResourceUtils.formatString(ResourceUtils.ERROR_DURING_DEPLOY, new Object[] {type}), throwable);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(baos);
    baos.write(FAILED);
    throwable.printStackTrace(printWriter);
    printWriter.close();
    return baos.toByteArray();
  }

  private void checkForErrors(MultipleAnswer answers) throws ServiceException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(baos);
    int[] participants = answers.participants();
    for (int participant : participants) {
      MessageAnswer answer;
      try {
        answer = answers.getAnswer(participant);
      } catch (ClusterException e) {
        //$JL-EXC$
        printWriter.append("\r\n" + ResourceUtils.formatString(ResourceUtils.CANT_RECEIVE_MESSAGE, new Object[] {"" + participant}));
        e.printStackTrace(printWriter);
        continue;
      }
      byte[] message = answer.getMessage();
      byte result = message[answer.getOffset()];
      if (result == FAILED) {
        printWriter.append("\r\n");
        printWriter.append(ResourceUtils.formatString(ResourceUtils.ACTION_FAILED_AT_CLUSTER_ELEMENT_CORRELATOR_ID, new Object[] {"" + participant, ThreadWrapper.getCurrentTaskId()}));
        if (answer.getLength() > 1 && LOCATION.beError()) {
          LOCATION.logT(Severity.ERROR, ResourceUtils.formatString(ResourceUtils.ACTION_FAILED_AT_CLUSTER_ELEMENT, new Object[] {"" + participant}) + " : " + new String(message, answer.getOffset() + 1, answer.getLength() - 1));
        }
      }
    }
    printWriter.close();
    if (baos.size() > 0) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR), new Object[] {baos.toString()}));
    }
  }

}