package com.sap.engine.services.rmi_p4.server;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.cluster.ClusterContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.event.ClusterEventListener;
import com.sap.engine.frame.cluster.event.ServiceEventListener;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.frame.cluster.message.NoListenerOnDestinationException;
import com.sap.engine.frame.cluster.message.DestinationNotAvailableException;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.interfaces.cross.CrossInterface;
import com.sap.engine.interfaces.cross.CrossObjectFactory;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.*;
import com.sap.engine.services.rmi_p4.interfaces.ConnectionObjectInt;
import com.sap.engine.services.rmi_p4.all.ConnectionProfile;
import com.sap.engine.services.rmi_p4.all.MessageConstants;
import com.sap.engine.services.rmi_p4.exception.P4BaseIOException;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.bc.proj.jstartup.sadm.ShmAccessPoint;

import java.util.*;
import java.net.InetAddress;
import java.net.Inet6Address;
import java.io.IOException;

/**
 * This class is responsible for internal-cluster communication for P4 protocol.
 * Main consumer of this communication is JMS service.
 * 
 * @author Georgy Stanev, Tsvetko Trendafilov, Slavomir Grigorov
 * @version 7.0
 */
public class ClusterOrganizer implements MessageListener, MessageConstants, ClusterEventListener, ServiceEventListener {

  public MessageContext messageContext;
  public ClusterContext clusterContext;
  public ServiceContext serviceContext;
  private P4SessionProcessor sessionProcessor;
  public static final String SSL_LAYER = "ssl";
  public Hashtable<String, ConnectionProfile> availableProfiles = new Hashtable<String, ConnectionProfile>();
  protected P4ObjectBroker broker = P4ObjectBroker.init();
  private int profileCount = 0;
  
  /**
   * A protected constructor with MessageContext and ApplicationServiceContext.
   * Initialized from P4SessionProcessor, when P4 service starts.
   * @param _messageContext Message context received from service context
   * @param _baseContext Service context for P4 service, received when service starts.
   */
  protected ClusterOrganizer(MessageContext _messageContext, ApplicationServiceContext _baseContext) {
    this.messageContext = _messageContext; 
    this.clusterContext = _baseContext.getClusterContext();
    this.serviceContext = _baseContext;

    try {
      messageContext.registerListener(this);
    } catch(Exception e) {
      if (P4Logger.getLocation().beInfo()) {
        P4Logger.getLocation().infoT("ClusterOrganizer constructor", P4Logger.exceptionTrace(e));
      }
    }
    sessionProcessor = P4SessionProcessor.thisProcessor;
    
    try {
      serviceContext.getServiceState().registerClusterEventListener(this);
      serviceContext.getServiceState().registerServiceEventListener(this);
    } catch(ListenerAlreadyRegisteredException e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ClusterOrganizer.<init>", P4Logger.exceptionTrace(e));
      }
    }
  }

  /**
   * Get all available profiles from class ShmAccessPoint from JStartup
   * We have assured that there are any AccessPoints during start of P4 service, before this initialization.
   * @return Array of ConnectionProfile objects with all profiles configured from received AaccessPoints.
   */
  public ConnectionProfile[] getAllProfiles() {

    try {
      ShmAccessPoint[] pid_p4 = ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_P4);
      ShmAccessPoint[] pid_p4ssl = ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_P4S);
      ArrayList<ConnectionProfile> icmProfiles = new ArrayList<ConnectionProfile>(pid_p4.length + pid_p4ssl.length);

      icmProfiles.addAll(extractP4Profiles(pid_p4, P4ObjectBroker.DEFAULT_COMMUNICATION_LAYER));
      icmProfiles.addAll(extractP4Profiles(pid_p4ssl, SSL_LAYER));

      ConnectionProfile[] profiles = icmProfiles.toArray(new ConnectionProfile[icmProfiles.size()]);
      if (profiles.length == 0) {
        if (P4Logger.getLocation().beWarning()) {
          P4Logger.trace(P4Logger.WARNING, "ClusterOrganizer.getAllProfiles()",  "Getting all connection profiles from ICM failed. There are no profiles registred in shared memory. Check shared memory state and if not full, or check connection profiles in ICM", "ASJ.rmip4.cf1002");
        }
      }
      return profiles;
    } catch (Exception shmex) {
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "ClusterOrganizer.getAllProfiles()", "Getting all connection profiles from JStartup failed. RMI-P4 protocol will not work properly. Exception: {0}", "ASJ.rmip4.cf1003", new Object[] {P4Logger.exceptionTrace(shmex)}, ShmAccessPoint.class.getClassLoader(), null);
      }
    }
    return new ConnectionProfile[0];
  }
  
  /**
   * Private method which simplify getAllProfiles() method. It receive array of ShmAccessPoint,
   * constructs ArrayList of ConnectionProfile(s) from received array and return the ArrayList of ConnectionProfile(s).  
   * @param pid_p4 array of ShmAccessPoint for connection type specified as second parameter
   * @param ConnectionType Connection type corresponding to profiles given pid_p4 parameter ("None" or "ssl").
   * @return ArrayList of ConnectionProfile(s) with all ConnectionProfiles constructed from received array. 
   */
  private ArrayList<ConnectionProfile> extractP4Profiles(ShmAccessPoint[] pid_p4, String ConnectionType) {
    String hostAddress;
    ArrayList<ConnectionProfile> icmProfiles = new ArrayList<ConnectionProfile>(pid_p4.length);
    if(pid_p4 != null) {
      for (int i = 0; i < pid_p4.length; i++) {
        if(!pid_p4[i].getAddress().isLoopbackAddress() ) {
          InetAddress address =  pid_p4[i].getAddress();
          if (address instanceof Inet6Address) {
            hostAddress = "#" + address.getHostAddress().replace(':', '.');
          } else {
            hostAddress = address.getHostAddress();
          }
          icmProfiles.add(new ConnectionProfile(ConnectionType, hostAddress, pid_p4[i].getPort()));
        }
      }
    }
    return icmProfiles;
  }

  /**
   * Called when message for current server node was received.
   * Implements the method for interface MessageListener. 
   * 
   * @param   elementId Caller server ID
   * @param   id Message type
   * @param   message the message itself as byte array
   * @param   off Start of real message
   * @param   size The size of the message
   * 
   * @see com.sap.engine.frame.cluster.message.MessageListener#receive(int, int, byte[], int, int)
   */
  public void receive(int elementId, int id, byte[] message, int off, int size) {
    switch(id) {
      case CLUSTER_COMMUNICATION:{
        //System threads, Not redirected communication
        createMessageAndProcess(elementId, message, off, size, true, false, "CLUSTER_COMMUNICATION");
        break;
      }
      case APPCLUSTER_COMMUNICATION:{
        //Application threads, Not redirected communication
        createMessageAndProcess(elementId, message, off, size, false, false, "APPCLUSTER_COMMUNICATION");
        break;
      }
      case REDIRECTED_APPCLUSTER_COMMUNICATION:{
        //Application threads, redirected communication
        createMessageAndProcess(elementId, message, off, size, false, true, "REDIRECTED_APPCLUSTER_COMMUNICATION");
        break;
      }
      //Case when receive message for object, which was redirected to current server node, by another server node in the cluster.
      //This is the case when we receive message from one server node and have to return the result to another server node.
      case REDIRECTABLE_OBJECT:{
        /* redirected message from other server, because this application is working on this server */
        byte[] mss = new byte[size - 8];
        int clusterEl_id = Convert.byteArrToInt(message, off); // serverId from which is the redirected request
        int clientId = Convert.byteArrToInt(message, off + 4);
        System.arraycopy(message, off + 8, mss, 0, size - 8);
        Message msg = new Message(elementId, clusterEl_id, clientId, mss, size);
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("ClusterOrganizer.receive()", "Redirected " + msg.getType() + " size: " + msg.getSize() + " bytes received from server: " + elementId + " and initially send by server " + clusterEl_id);
          if (P4Logger.getLocation().beDebug()) {
               P4Logger.getLocation().debugT("\r\n" + Message.toString(mss, 0, size));
            }
        }
        Connection ms_connection = null;
        try {
          ms_connection = sessionProcessor.getConnection(msg.clusterEl_id);
        } catch (IOException ioex) {
          if (P4Logger.getLocation().beWarning()) {
            P4Logger.trace(P4Logger.WARNING, "ClusterOrganizer.receive()", "Request from server: {0} received. But opening connection back to server process {1} failed (see the following exception for details). If server {2} process is alive it is possible that client connected to it will hang. Exception: {3}", "ASJ.rmip4.rt1007", new Object [] {elementId, msg.clusterEl_id, msg.clusterEl_id, ioex.toString()});
            if (P4Logger.getLocation().beDebug()) {
               P4Logger.getLocation().debugT("REDIRECTABLE_OBJECT", P4Logger.exceptionTrace(ioex));
            }
          }
          ms_connection = new MSErrorConnection(msg.clusterEl_id, ioex);
        }
        //TODO how to solve failure during processing, server node clusterEl_id does not have call to this connection.
        ServerDispatchImpl sdisp = new ServerDispatchImpl(msg, broker, sessionProcessor, ms_connection);
        ThreadSystem threadSystem = serviceContext.getCoreContext().getThreadSystem();
        threadSystem.startThread(sdisp, false, true);
        break;
      }
    }
  }
  
  private void createMessageAndProcess(int elementId, byte[] message, int off, int size, boolean isAppThread, boolean isRedirected, String messageType){
    byte[] mss = new byte[size];
    System.arraycopy(message, off, mss, 0, size);
    //Parameters elementId are equal and duplicated, because the second parameter is used only for REDIRECTABLE_OBJECT message type.There it is not elementId, but clusterEl_id.
    Message msg = new Message(elementId, elementId, -1, mss, size);
    
    if (!isRedirected) {
      //For redirected messages, do not set id of current server node into message, 
      //because there is no exported P4RemoteObject (skeleton) for this Call.   
      msg.own_id = broker.getId();
    }
    
    if (P4Logger.getLocation().bePath()) {
      P4Logger.getLocation().pathT("ClusterOrganizer.receive(int, int, byte[], int, int)", msg.getType() + " message with size: " + msg.getSize() + " bytes received. Initiated from server node: " + elementId);
      if (P4Logger.getLocation().beDebug()) {
         P4Logger.getLocation().debugT("\n" + Message.toString(mss, 0, size));
      }
    }
    
    Connection ms_connection = null;
    try {
      //Server id to server node that have to receive reply is get from Message
      ms_connection = sessionProcessor.getConnection(msg.clusterEl_id);
    } catch (IOException ioex) {
      if (msg.type == Message.CALL_REQUEST) {
        if (P4Logger.getLocation().beWarning()) {
          P4Logger.trace(P4Logger.WARNING, "ClusterOrganizer.receive()", "Request from server: {0} received. But opening connection back to server process {1} failed (see the following exception for details). Request type {2}. If this server process is alive it is possible client application, which sent the request, to hang. Exception: {3}", "ASJ.rmip4.rt1004", new Object[]{elementId, elementId, messageType, ioex});
          if (P4Logger.getLocation().beDebug()) {
             P4Logger.getLocation().debugT(messageType, P4Logger.exceptionTrace(ioex));
          }
        }
      //TODO think for performance optimization if possible in case of Request, not to process it with repliable MSErrorConnection.
      }
      ms_connection = new MSErrorConnection(msg.clusterEl_id, ioex);
    } 
    DispatchImpl disp = new ServerDispatchImpl(msg, broker, sessionProcessor, ms_connection);
    ThreadSystem threadSystem = serviceContext.getCoreContext().getThreadSystem();

    if (msg.type == Message.CALL_REQUEST && !isAppThread) { 
      threadSystem.startThread(disp, true, true); //second parameter true shows that the thread will be SYSTEM thread. 
    } else {
      //disp.run();
      threadSystem.startThread(disp, false, true); //second parameter false stands for application thread.
    }
  }

  /**
   * Parse factory name part from key for redirectable object.
   *   
   * @param str The key received in header of redirectable message call. 
   * @return parsed factory name from message without object identifier part.
   */
  protected String getFactoryPart(String str) {
    int ind = str.indexOf(':');
    if(ind == -1) {
      return str;
    } else {
      return str.substring(0, ind);
    }
  }

  /**
   * Special request message receive from another server node in the instance and reply to such special requests internal in instance.
   * Implements the method for interface MessageListener. 
   *
   * @param   serverId Caller server ID for the server node process which sends the special request 
   * @param   _id Message type
   * @param   message The message as byte array. It has different semantic for different message types.
   * @param   off The start byte for the message
   * @param   size The size of the message counted from start byte position
   * @return MessageAnswer object with byte array with wanted information depending of type of received message.
   * 
   * @see com.sap.engine.frame.cluster.message.MessageListener#receiveWait(int, int, byte[], int, int)
   */
  public MessageAnswer receiveWait(int serverId, int _id, byte[] message, int off, int size) {
    switch(_id) {
      case BROKERID_REQUEST: {
        byte[] id = new byte[4];
        Convert.writeIntToByteArr(id, 0, broker.brokerId);
        return new MessageAnswer(id, 0, 4);
      }
      case OBJECT_REQUEST: {
          try {
            String objName = new String(message, off, size);
            if (broker.initObjects.containsKey(objName)) {
              byte[] msg = broker.getInitialObject(objName);
              return new MessageAnswer(msg, 0, msg.length);
            } else {
              return new MessageAnswer(new byte[0], 0, 0);
            }
          } catch (Exception e) {
            if (P4Logger.getLocation().bePath()) {
              P4Logger.getLocation().pathT("ClusterOrganizer.receiveWait(int, int, byte[], int, int)", P4Logger.exceptionTrace(e));
            }
            return new MessageAnswer(new byte[0], 0, 0);
          }
      }
      case SEARCH_OTHER_REDIRECTED_SERVERS: {
        /* search in this server for factory registered in the cross */
        String msg_ident = new String(message, off, size);
        CrossInterface cctx = (CrossInterface) broker.getCrossInterface();
        CrossObjectFactory cof = cctx.getObjectFactory(msg_ident);

        /* if in this server has registered factory in cross service for wanted object current server node will return 1, otherwise empty byte array*/
        if(cof != null) {
          return new MessageAnswer(new byte[]{1});
        } else {
          return new MessageAnswer(new byte[0]);
        }
      }
    }

    return new MessageAnswer(new byte[0], 0, 0);
  }

  /**
   * Used by Dispatcher in 6.40. ICM do not use this method in 7.1X or later versions.
   */
  protected void updateProfiles(int id, byte[] answer, int off, int length) {
    synchronized(availableProfiles) {
      String transportProfile = Convert.byteArrToAString(answer, off, length);
      if (P4Logger.getLocation().beInfo()) {
        P4Logger.getLocation().infoT("ClusterOrganizer.updateProfiles(int, byte[], int, int)", "New profile is available: " + transportProfile);
      }
      StringTokenizer tokenizer = new StringTokenizer(transportProfile, ":");
      String hosts = tokenizer.nextToken();
      StringTokenizer hostsTokens = new StringTokenizer(hosts, "@");
      String[] nameHosts = new String[hostsTokens.countTokens()];
      int i = 0;
      while(hostsTokens.hasMoreTokens()) {
        nameHosts[i] = hostsTokens.nextToken();
        i++;
      }
      String type;
      int port;
      while(tokenizer.hasMoreTokens()) {
        type = tokenizer.nextToken();
        port = Integer.parseInt(tokenizer.nextToken());
        ConnectionProfile[] newProfile = new ConnectionProfile[nameHosts.length];
        for(int ii = 0; ii < nameHosts.length; ii++) {
          newProfile[ii] = new ConnectionProfile(type, nameHosts[ii], port);
          Enumeration it = availableProfiles.elements();
          boolean contains = false;
          while(it.hasMoreElements()) {
            Object o = it.nextElement();
            if((o.equals(newProfile[ii]))) {
              contains = true;
              break;
            }
          }
          if(!contains) {
            StringBuffer buf = new StringBuffer();
            buf.append(id);
            buf.append(profileCount++);
            availableProfiles.put(buf.toString(), newProfile[ii]);
          }
        }
      }
    }
  }

  public void send(int serverId, byte[] toSend, int off, int length, boolean isAppThread) throws P4IOException {
    if (P4Logger.getLocation().bePath()) {
      P4Logger.getLocation().pathT("ClusterOrganizer.send(int, byte[], int, int, boolean)", "Sending message to server node " + serverId);
    }

    if (serverId == sessionProcessor.getServerId()) {
      receive(serverId, CLUSTER_COMMUNICATION, toSend, off, length);
      return;
    } 
    
    int threadType = APPCLUSTER_COMMUNICATION;
    if (!isAppThread) {
      threadType = CLUSTER_COMMUNICATION;
    }
    
    try {
        messageContext.send(serverId, threadType, toSend, off, length);
    } catch(NoListenerOnDestinationException nlode) { //$JL-EXC$ 
      //Server is starting, but not started P4 yet. Just wait for a while and retry
      try {
        Thread.sleep(500);
        messageContext.send(serverId, threadType, toSend, off, length);
      } catch(DestinationNotAvailableException destExc) {
        receiveIfRedirectable(serverId, toSend, off, length, isAppThread, destExc);
      } catch(NoListenerOnDestinationException nlode2) {
        receiveIfRedirectable(serverId, toSend, off, length, isAppThread, nlode2);
      } catch(Exception e) {
        throw ((P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Couldnt_send_message, e));
      }
    } catch(DestinationNotAvailableException destExc) {
      receiveIfRedirectable(serverId, toSend, off, length, isAppThread, destExc);
    } catch (Exception e) {
      throw ((P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Couldnt_send_message, e));
    }
  }

  /* (Not Java Doc)
   * This method simplifies method send(). It trace warning that initial try to send internal for instance message failed.
   * Then If the message is request tries to receive it. If the message is redirectable it will be received successfully.
   * If the message is not redirectable or is not a request, sending will fail with P4IOException("Cannot send message"); (resource bundle code p4_0006)
   */
  private void receiveIfRedirectable(int serverId, byte[] toSend, int off, int length, boolean isAppThread, Exception originalEx) throws P4IOException {
    if (P4Logger.getLocation().beWarning()) {
      P4Logger.trace(P4Logger.WARNING, "ClusterOrganizer.send()", "Sending message to server process {0} failed. The message processing will fail if the destination object is not redirectable, otherwise the current server process will try to process it" , "ASJ.rmip4.rt1010", new Object []{serverId} );
      if (P4Logger.getLocation().beDebug()) {
        //Here exception that we log is some kind of ClusterException: DestinationNotAvailableException or NoListenerOnDestinationException
        P4Logger.getLocation().debugT("ClusterOrganizer.send()", "Exception while sending message to server process " + serverId + " : " + P4Logger.exceptionTrace(originalEx));  
      }
    }

    //TODO Eventually only for Redirectable messages - needs parsing to skip context objects (variable size) in order to go to redirectable flag. 
    if (toSend[off + ProtocolHeader.MESSAGE_TYPE] == Message.CALL_REQUEST ) { //Skip inform and reply message types  
      try {
        //Message via system threads cannot be redirectable.
        //Try to redirect message to itself(current server node) as original destination is not available.
        receive(sessionProcessor.getServerId(), REDIRECTED_APPCLUSTER_COMMUNICATION, toSend, off, length);  
      } catch(Exception e) {
        throw ((P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Couldnt_send_message, e));
      }
    } else {
      throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Couldnt_send_message, originalEx);
    }
  }

  ///////////////////// ClusterEventListener implementation ///////////////
  /**
   * Called when a new cluster node attaches to the cluster.
   *
   * @param   element  the element, witch joins into the cluster
   */
  public void elementJoin(ClusterElement element) {
  }

  /**
   * This method is invoked when a cluster participant changes its current running state.
   * For instance change state from "Running" to "Stopping".
   * 
   * @param   element  The element that changes its current running state 
   *                   (this instance of <code>ClusterElement</code> contains the new state).
   * @param id         The old state of the cluster node.  
   */
  public void elementStateChanged(ClusterElement element, byte id) {
    // nothing to do
  }

  /**
   * Called when element of the cluster is lost, when a cluster participant detaches the cluster.
   * RMI-P4 closes the internal-cluster connection if it had such and release all skeletons exported for stubs
   * located in lost cluster element. If the element is MS or ICM - no action in this listener.
   *
   * @param   element  the element witch is lost
   */
  public void elementLoss(ClusterElement element) {
    if (element.getType() == ClusterElement.SERVER) {
      if (P4Logger.getLocation().beInfo()) {
        P4Logger.getLocation().infoT("ClusterOrganizer.elementLoss()", "Cluster element " + element.getClusterId() + " is lost - notifying waiting calls");
      }
      int _elementId = element.getClusterId();
      P4Call call = null;
      try {
        Object[] calls = P4Call.getAllCalls();
        for (int cn = 0; cn < calls.length; cn++) {
          call = (P4Call) calls[cn];
          if (call.isToClusterElement(_elementId)) {
            synchronized(call) {
              call.setException(new P4ConnectionException("Connection to server node " + _elementId + " is lost. Probably the server is down"));
            }
          }
        }

        broker.disposeConnection(_elementId + ":" + sessionProcessor.currentServerId); //Internal for cluster connection is closed
        broker.disposeConnection(_elementId + ":" + -1); //TODO check if obsolete.

      } catch(Exception ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("ClusterOrganizer.elementLoss(ClusterElement)", P4Logger.exceptionTrace(ex));
        }
      }
    }
  }

  /**
   * Unregister itself as listener from contexts, where it was registered.
   */
  public void unregister() {
    messageContext.unregisterListener();
    serviceContext.getServiceState().unregisterClusterEventListener();
  }

  public ConnectionObjectInt[] listConnections() {
   return new ConnectionObjectInt[0]; //TODO make it gather real information and sent it to telnet command for monitoring
  }

  ///////////////////// ServiceEventListener implementation ///////////////
  /**
   * This method is called by the <code>ServiceState</code> in which this ServiceEventListener 
   * listener is registered, when the monitored service on the specified server node is ready 
   * for internal communication (has registered a <code>MessageListener</code>).
   *
   * @param element the node on which the monitored service is ready.
   */
  public void serviceStarted(ClusterElement element) {

  }

  /**
   * This method is called by the <code>ServiceState</code> in which this ServiceEventListener
   * listener is registered, when the monitored service on the specified server node is no more 
   * available for internal cluster communication (has unregistered its <code>MessageListener</code>).
   *
   * @param element The server node, on which the monitored service is not available any more.
   */
  public void serviceStopped(ClusterElement element) {
    
  }
}