package com.sap.engine.services.rmi_p4;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.client.ClientFactory;
import com.sap.engine.frame.core.thread.ClientThreadContext;
import com.sap.engine.frame.core.thread.Transferable;
import com.sap.engine.frame.core.thread.TransferableExt;
import com.sap.engine.interfaces.cross.CrossObjectBroker;
import com.sap.engine.interfaces.cross.ObjectBrokerInterface;
import com.sap.engine.interfaces.cross.RemoteEnvironment;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.interfaces.cross.io.ConnectionProvider;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.all.ConnectionProfile;
import com.sap.engine.services.rmi_p4.classload.ClassLoaderContext;
import com.sap.engine.services.rmi_p4.classload.DynamicClassLoader;
import com.sap.engine.services.rmi_p4.exception.P4BaseIOException;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.exception.P4ResourceAccessor;
import com.sap.engine.services.rmi_p4.garbagecollector.GarbageCollector;
import com.sap.engine.services.rmi_p4.garbagecollector.finalize.FinalizeInformer;
import com.sap.engine.services.rmi_p4.monitor.ConnectionObject;
import com.sap.engine.services.rmi_p4.server.P4ServerObjectInfo;
import com.sap.engine.services.rmi_p4.interfaces.ConnectionObjectInt;
import com.sap.tc.logging.Location;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author Georgy Stanev
 * @version 7.0
 */
public class P4ObjectBrokerClientImpl extends P4ObjectBroker implements ObjectBrokerInterface {

  public static final String SECURITY_CO = "security"; // name of security context object
  public static final String TICKET = "?ticket=";      // String tag for composing URL

  public ConnectionManager manager;
  private Thread garbageCollector;
  private GarbageCollector garbageCollectorInstance;
  private boolean isClose = true;

  protected P4ObjectBrokerClientImpl() {
    this.weaks = new WeakHashMap();
  }

  public void inform(Object _identifer, Message _message) {
    objManager.inform(_identifer, _message.getUnmarshaledRequest());
  }

  public void disposeConnection(Object _connection) {
    try {
      objManager.disposeConnection(_connection);
      manager.removeConnection((ClientConnection) _connection);
      if (_connection != null && _connection instanceof ClientConnection){
        P4ObjectBroker.getBroker().removeForbiden( ((ClientConnection)_connection) );
      }//It cannot be LocalDispatch@hashCode; And local connections cannot contain forbidden objects 
    } catch (Exception ex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().traceThrowableT(P4Logger.DEBUG, "P4ObjectBrokerClientImpl.disposeConnection", ex);
      }
    }
  }

  public P4RemoteObjectInfo getObjectInfo() {
    P4RemoteObjectInfo info = new P4ServerObjectInfo();
    info.connectionProfiles = getConnectionProfiles();
    return info;
  }

  public Object resolveInitialReference(String name, String host, int port) throws IOException {
    return resolveInitialReference(DEFAULT_COMMUNICATION_LAYER, name, host, port);
  }

  public Object resolveInitialReference(String connectionType, String name, String host, int port) throws IOException {
    return resolveInitialReference(connectionType, name, host, port, 0);
  }

  public Object resolveInitialReference(String connectionType, String name, String host, int port, int destServerId) throws IOException {
    return resolveInitialReference(connectionType, name, host, port, destServerId, null);
  }

  public Object resolveInitialReference(String connectionType, String name, String host, int port, int destServerId, Properties properties) throws IOException {
    if(P4Logger.getLocation().bePath()) {
      P4Logger.getLocation().pathT("P4ObjectBrokerClientImpl.resolveInitialReference","Resolve \"" + name + "\" from " + connectionType + ":"+ host + ":" + port + "#" + destServerId + "\r\n" + properties);
    }
    ClientConnection con = null;
    try {
      if (properties == null) {
       properties = new Properties();
      }
      properties.put("TransportLayerQueue", connectionType);
      con =  getInitialConnection(host, port, properties);
      //destServerId = 0 - will be load-balanced to somewhere
      checkCOs(con, destServerId, connectionType);
    } catch (IOException e) {
       checkClosed();
       if (P4Logger.getLocation().beInfo()) {
         P4Logger.getLocation().infoT("P4ObjectBrokerClientImpl.resolveInitialReference", "Cannot open RMI/P4 connection to: " + connectionType + ":" + host +":" + port);
       }
    }
    return resolveInitialReference(name, con, destServerId);
  }
  
  //Not synchronized method may lead to deadlock in getResult and sendRequest->setException to monitors of InitialCall and ClientConnection, via one and the same connection
  public synchronized Object resolveInitialReference(String name, Connection repliable, int destServerId) throws IOException {

    ClientConnection con = (ClientConnection)repliable;
    int nameLength = name.length();
    InitialCall call = InitialCall.getInitialCall(con, name);
    byte[] message = new byte[ProtocolHeader.THREAD_CONTEXT_SIZE + 2 * nameLength];
    call.writeId(message, ProtocolHeader.CALL_ID);
    message[ProtocolHeader.MESSAGE_TYPE] = 10;
    Convert.writeUStringToByteArr(message, ProtocolHeader.THREAD_CONTEXT_SIZE, name);  // initial calls have no thread context so the object name starts at that offset
    ProtocolHeader.writeHeader(message, 0, message.length, destServerId);
    con.sendRequest(message, message.length, null);
    byte[] res = null;
    try {
      res = call.getResult(con);
    }catch (Exception e) {
      if (e instanceof IOException){
        throw (IOException) e;
      } else {
        IOException ioe = new IOException("Reading reply for initial call failed. Check remote side state");
        ioe.initCause(e);
        throw ioe;
      }
    }
    StubBaseInfo stubInfo = byteArrayToObject(res);
    if ((destServerId != 0) && (stubInfo.server_id != destServerId)) {
      if(P4Logger.getLocation().beWarning()) {
        P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerClientImpl.resolveInitialReference()", "No server process with ID: {0} was found; the reply was sent from: {1}", "ASJ.rmip4.rt2020" , new Object []{destServerId, stubInfo.server_id});
      }
      throw new java.rmi.RemoteException("Server process with ID " + destServerId + " does not exist");
    }
    if (!con.connectionId.equalsIgnoreCase(con.destinationId)) {
      StringBuffer _id = new StringBuffer(String.valueOf(stubInfo.ownerId));
      _id.append(':');
      _id.append(con.destinationId);
      String id = _id.toString();
      con.underlyingProfile = id;
      stubInfo.setIncomingProfile(id);
    }
    initialProfiles = new ConnectionProfile[1];
    //new ConnectionProfile[stubInfo.connectionProfiles.length +1];
    initialProfiles[0] = new ConnectionProfile(con.type, con.host, con.port);
    //System.arraycopy(stubInfo.connectionProfiles, 0, initialProfiles, 1, stubInfo.connectionProfiles.length);
    if (P4Logger.getLocation().bePath()) {
      P4Logger.getLocation().pathT("P4ObjectBrokerClientImpl.resolveInitialReference", "Initial reference received");
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubBaseInfo@" + Integer.toHexString(System.identityHashCode(stubInfo)) + "\r\n" +stubInfo.toString());
      }
    }
    return stubInfo;
  }

  public synchronized void close() {
    if (isClose) {
      return;
    }
    if(P4Logger.getLocation().bePath()) {
      P4Logger.getLocation().pathT("P4ObjectBrokerClientImpl.close","Closing the RMI/P4 broker...");
    }
    isClose = true;
    try {
      manager.close();
    } catch (Throwable e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(this.getClass() + ".close() \r\n " + P4Logger.exceptionTrace(e));
      }
    }

    try {
      impls.clear();
    } catch (Throwable e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(this.getClass() + ".close() \r\n " + P4Logger.exceptionTrace(e));
      }
    }

    try {
      initObjects.clear();
    } catch (Throwable e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(this.getClass() + ".close() \r\n " + P4Logger.exceptionTrace(e));
      }
    }

    try {
      Parser.close();
    } catch (Throwable e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(this.getClass() + ".close() \r\n " + P4Logger.exceptionTrace(e));
      }
    }

    try {
      garbageCollectorInstance.stop();
      garbageCollector.interrupt();
    } catch (Throwable e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(this.getClass() + ".close() \r\n " + P4Logger.exceptionTrace(e));
      }
    }
    try {
      garbageCollector.stop();
    } catch (Throwable e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(this.getClass() + ".close() \r\n " + P4Logger.exceptionTrace(e));
      }
    }

    try {
      finalizeInformer.close();
    } catch (Throwable e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(this.getClass() + ".close() \r\n " + P4Logger.exceptionTrace(e));
      }
    }

    CrossObjectBroker.unregisterP4ProtocolProvider();
    garbageCollector = null;
    finalizeInformer = null;
    manager = null;
    closeInstance();
    if(P4Logger.getLocation().bePath()) {
      P4Logger.getLocation().pathT("P4ObjectBrokerClientImpl.close","RMI/P4 broker closed");
    }
  }

  public ClientConnection getConnection(String _connectionType, ConnectionProfile[] _profiles, StubBaseInfo _info) throws P4IOException {
    try {
    ClientConnection repl = null;
    if (_info.getIncomingProfile() != null) {
      StringTokenizer tokenizer = new StringTokenizer(_info.getIncomingProfile(), ":");
      try {
        tokenizer.nextToken(); //skip brokerID
        String type = tokenizer.nextToken();
        String host = tokenizer.nextToken();
        int port = Integer.parseInt(tokenizer.nextToken());
        repl = manager.getConnection(type, host, port, P4ObjectBroker.props);
        super.checkCOs(repl, 0, type);
        return repl;
      } catch (NoSuchElementException nsee) {  //$JL-EXC$
        //remove malformed incoming profile
        _info.setIncomingProfile(null);
      } catch (NumberFormatException nfe) { //$JL-EXC$
        //remove malformed incoming profile
        _info.setIncomingProfile(null);
      } catch (P4IOException p4ioe) {
        // the incoming profile is no longer valid
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.getConnection(String, ConnectionProfile[], StubBaseInfo)", P4Logger.exceptionTrace(p4ioe));
        }
      }
    }

    // this is workaround for stubs serialized before the ICM (dispatcher) was up and running
    if (_profiles == null || _profiles.length == 0) {
      _profiles = initialProfiles;
    }
    // try to find existing connection
    for (int ii = 0; ii < _profiles.length; ii++) {
      if ((_connectionType == null) || _profiles[ii].supplyType(_connectionType)) {
        if ((repl = manager.takeConnection(_profiles[ii].getType(), _profiles[ii].getHost(), _profiles[ii].getPort())) != null) {
        super.checkCOs(repl, 0, _profiles[ii].getType()); // in case of opened connection from remote side
        return repl;
      }
    }
    }

    String connType = _connectionType;
    if (_connectionType != null) {
      repl = openConnection(_connectionType, _profiles);
    } else {
      //first try with the transport that the broker was initialized
      repl = openConnection(transportType, _profiles);
      connType = transportType;
      if (repl == null) {
        //then try the default
        repl = openConnection(P4ObjectBroker.DEFAULT_COMMUNICATION_LAYER, _profiles);
        connType = DEFAULT_COMMUNICATION_LAYER;
      }
      if (repl == null) {
        //the try with ssl
        repl = openConnection("ssl", _profiles);
        connType = "ssl";
      }
      if (repl == null) {
        //then with httptunneling
        repl = openConnection("httptunneling", _profiles);
        connType = "httptunneling";
      }
      // the other profiles cannot be used without user-provided properties so no need to try them
    }
    if (repl != null) {
      super.checkCOs(repl, 0, connType);
      return repl;
    } else {
      if (P4Logger.getLocation().beWarning()) {
        P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerClientImpl.getConnection()", "Cannot establish connection with any profile. Retrying with the initial profiles", "ASJ.rmip4.rt2021");
      }
      //last resort - try with the initial profiles
      _profiles = initialProfiles;
      //this connection must be already opened
      for (int ii = 0; ii < _profiles.length; ii++) {
        if ((_connectionType == null) || _profiles[ii].supplyType(_connectionType)) {
          if ((repl = manager.takeConnection(_profiles[ii].getType(), _profiles[ii].getHost(), _profiles[ii].getPort())) != null) {
            super.checkCOs(repl, 0, _profiles[ii].getType()); // in case of opened connection from remote side
            return repl;
          }
        }
      }
    }
    if (P4Logger.getLocation().beWarning()) {
      P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerClientImpl.getConnection()", "Cannot establish connection with initial profile. Connection type:{0}, incoming profile:{1}, stub info\r\n{2}", "ASJ.rmip4.rt2022" , new Object []{_connectionType, _info.incomingProfile, _info});
    }
    } catch (RuntimeException rte) {
       checkClosed();
       throw rte;
    }
    throw (P4IOException) getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Cannot_Make_Connection, null);
  }

  private ClientConnection openConnection(String type, ConnectionProfile[] profiles) {
    for (int i = 0; i < profiles.length; i++) {
        try {
            if (profiles[i].supplyType(type)) {
              return manager.getConnection(type, profiles[i].getHost(), profiles[i].getPort(), P4ObjectBroker.props);
            }
        } catch (Exception _) {
           if (P4Logger.getLocation().beInfo()) {
            P4Logger.getLocation().infoT("P4ObjectBrokerClientImpl.getConnection","Cannot open connection with profile:" + profiles[i]);
             if (P4Logger.getLocation().beDebug()) {
               P4Logger.getLocation().traceThrowableT(P4Logger.DEBUG, "P4ObjectBrokerClientImpl.getConnection", _);
             }
          }
        }
      }
      return null;
  }

  public void postInit(int sPort) {
    Parser.init();
    try {
      manager = new ConnectionManager(this, sPort);
    } catch (IOException ioex) {
       if (P4Logger.getLocation().beError()) {
         P4Logger.trace(P4Logger.ERROR, ConnectionManager.class.getName(), "Connection manager initialization failed. Exception: {0}", "ASJ.rmip4.rt2023" , new Object []{ioex.toString()});
      }
      throw new InitializingException("P4 Connection Manager failed to initialize", ioex);
    }
    isClose = false;
    finalizeInformer = new FinalizeInformer(10, 2, 3000);
  }

  public int getId() {
    return -1;
  }

  public int getPort() throws IOException {
    try {
    return manager.port;
    } catch (NullPointerException npe) {
      throw new P4IOException("P4 Broker is closed");
    }
  }

  public String getHost() throws IOException {
    try {
    return manager.hostNameIP;
    } catch (NullPointerException npe) {
      throw new P4IOException("P4 Broker is closed");
    }
  }

  public ConnectionProfile[] getConnectionProfiles() {
    try {
      ConnectionProfile[] toReturn = {new ConnectionProfile(transportType, getHost(), getPort())};
      return toReturn;
    } catch (Exception ex) {
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "P4ObjectBrokerClientImpl.getConnectionProfiles()", "Getting connection profiles failed. Exception: {0}", "ASJ.rmip4.rt2024" , new Object []{ex.getMessage()});
      }
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.getConnectionProfiles()", P4Logger.exceptionTrace(ex));
      }
      checkClosed();
    }
    return null;
  }

  public void runGarbageCollector() {
    garbageCollectorInstance = new GarbageCollector(objManager);
    garbageCollector = new Thread(garbageCollectorInstance);
    garbageCollector.setDaemon(true);
    garbageCollector.start();
  }

  public Object getCrossInterface() {
    return null;
  }

    public boolean localStubsAllowed() {
        return false;  
    }

    /**
     * Export skeleton in stand-alone environment.
     * @param obj The remote object that have to be exported. 
     * @return The exported skeleton.
     */
    public P4RemoteObject loadObject(java.rmi.Remote obj) throws ClassNotFoundException {
      if (obj instanceof P4RemoteObject) {
        return (P4RemoteObject) obj;
      }
      
      if (P4ObjectBroker.isEnabledSkeletonClassCache()) {
        return fastGetOfGeneratedOrDynamicSkeleton(obj);
      } else {
        return oldGetOfGeneratedOrDynamicSkeleton(obj);
      }
    }

    private P4RemoteObject oldGetOfGeneratedOrDynamicSkeleton(java.rmi.Remote obj) {
      P4RemoteObject retObj = null;
      Class remoteClass = obj.getClass();
      String remoteClassName = remoteClass.getName() + "p4_Skel";
      Class skeletonClass = null;
      //Try to load generated skeleton directly
      try {
        skeletonClass = Class.forName(remoteClassName, true, remoteClass.getClassLoader());
        retObj = (P4RemoteObject) skeletonClass.newInstance();
      } catch (Exception e) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.loadObject(Remote)", "Skeleton class not found for: " + remoteClassName);
        }
        //Try to load generated skeleton from some parent that implements remote interface
        try {
          skeletonClass = getParentSkel(remoteClass, "p4_Skel");
          retObj = (P4RemoteObject) skeletonClass.newInstance();
        } catch (Exception ex) {
          //No generated skeleton found
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.loadObject(Remote)", "Skeleton class not found for parents of: " + remoteClassName);
          }
          if (P4Logger.getLocation().beInfo()) {
            P4Logger.getLocation().infoT("P4ObjectBrokerClientImpl.loadObject", "Cannot instantinate Skeleton for " + obj + " trying to create a Dynamic Skeleton");
          }
          retObj = new P4DynamicSkeleton(obj);
          retObj.setDelegate(obj);       
        }
      }
      return retObj;
    }
    
    private P4RemoteObject newGetOfGenerateOrDynamicSkeleton(java.rmi.Remote obj) {
      P4RemoteObject retObj = null;
      Class remoteClass = obj.getClass();
      String remoteClassName = remoteClass.getName() + "p4_Skel";
      Class skeletonClass = null;
      //Try to load generated skeleton directly
      try {
        skeletonClass = Class.forName(remoteClassName, true, remoteClass.getClassLoader());
        retObj = (P4RemoteObject) skeletonClass.newInstance();
        skeletonClassCache.put(remoteClassName, new WeakReference(skeletonClass));
      } catch (Exception e) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.loadObject(Remote)", "Skeleton class not found for: " + remoteClassName);
        }
        //Try to load generated skeleton from some parent that implements remote interface
        try {
          skeletonClass = getParentSkel(remoteClass, "p4_Skel");
          retObj = (P4RemoteObject) skeletonClass.newInstance();
          skeletonClassCache.put(remoteClassName, new WeakReference(skeletonClass));
        } catch (Exception ex) {
          //No generated skeleton found
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.loadObject(Remote)", "Skeleton class not found for parents of: " + remoteClassName);
          }
          if (P4Logger.getLocation().beInfo()) {
            P4Logger.getLocation().infoT("P4ObjectBrokerClientImpl.loadObject", "Cannot instantinate Skeleton for " + obj + " trying to create a Dynamic Skeleton");
          }
          retObj = new P4DynamicSkeleton(obj);
          retObj.setDelegate(obj);
          skeletonClassCache.put(remoteClassName, new WeakReference(DYNAMIC_SKELETON));
        }
      }
      return retObj;
    }
    
    private P4RemoteObject fastGetOfGeneratedOrDynamicSkeleton(java.rmi.Remote obj) {
      Class remoteClass = obj.getClass();
      String remoteClassName = remoteClass.getName() + "p4_Skel";
      
      //Search the class for this skeleton first in cache
      //If we haven't such key or value is null - process as if this object is exported for the first time.
      if (!skeletonClassCache.containsKey(remoteClassName)) {
        return newGetOfGenerateOrDynamicSkeleton(obj);
      }
      
      WeakReference ref = skeletonClassCache.get(remoteClassName);
      if (ref == null || ref.get() == null) {
        if (ref != null) {
          skeletonClassCache.remove(ref);
        }
        return newGetOfGenerateOrDynamicSkeleton(obj);
      }
      
      //As we are here we have cached class for generated skeleton or cached sting that shows we should raise DynamicSkeleton.
      //This object was already exported for sure and ve have a valid value for it.
      P4RemoteObject retObj = null;
      Object storedReference = ref.get();
      //If this stored reference is not Class, but String this means that we have DynamicSkeleton for raising.
      //Omit searching for generated skeleton for these exported classes, instances of which were already exported.
      if (storedReference instanceof String) {
         return new P4DynamicSkeleton(obj);
      }
      
      //We have generated skeleton and its class is our stored in cache reference
      try {
        Class skeletonClass = (Class) storedReference;
        retObj = (P4RemoteObject) skeletonClass.newInstance();
      } catch (Exception e) {
        retObj = new P4DynamicSkeleton(obj);
      }
      return retObj;
    }
    

  /**
   * Get Security ticket as readable string if the user is authorised. Otherwise return null.
   * @param info - Given stub
   * @return
   */
  public String getTicket(StubBaseInfo info) {
    try {
      Object trans = (getCTC().getContextObject(SECURITY_CO));
      if (trans != null) {
        byte[] result = null;
        if (trans instanceof TransferableExt) {
          result = new byte[((TransferableExt) trans).size(new Integer(info.ownerId))];
          ((TransferableExt) trans).store(new Integer(info.ownerId), result, 0);
        } else {
          result = new byte[((Transferable) trans).size()];
          ((Transferable) trans).store(result, 0);
        }
        BigInteger big = new BigInteger(1, result);
        return big.toString(16);
      }
    } catch (Exception e) {
      if (P4Logger.getLocation().beError()  || P4Logger.getSecLocation().beError()) {
        Location loc;
        if (P4Logger.getSecLocation().beError()) {
          loc = P4Logger.getSecLocation();
        } else {
          loc = P4Logger.getLocation();
        }
        P4Logger.trace(P4Logger.ERROR, loc, "P4ObjectBrokerClientImpl.getTicket(StubBaseInfo)", "Getting secutity ticket failed. Exception: {0}", "ASJ.rmip4.rt2025" , new Object []{P4Logger.exceptionTrace(e)});
      }
    }
    return null;
  }

  public ClassLoader getClassLoader(Object obj, Class cl) {
    if(System.getProperty(STOP_P4_LOADING, "false").equalsIgnoreCase("true")){
      return cl.getClassLoader();
    }


    StubBaseInfo info = null;
    if (obj instanceof StubImpl) {
      info = ((StubImpl) obj).p4_getInfo();
    } else if (obj instanceof StubBaseInfo) {
      info = (StubBaseInfo) obj;
    }

//    return ClassLoaderContext.getDynamicLoader(info.ownerId);
    try {
      if (System.getProperty("P4ClassLoad", "").equals("P4Connection")) {
        P4URLLoader urlLoader = (P4URLLoader) weaks.get(cl);
        if (urlLoader != null) {
          return urlLoader;
        }
        if ((info != null) && (info.getUrls() != null)) {
          for (int i = 0; i < info.urls.length; i++) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("P4 client broker: get Classloader url[" + i + "]:=" + info.urls[i]);
            }
          }
        }
        String[] urlList = info.getUrls();
        String[] hostss = info.hosts;
        if ((urlList != null && hostss != null)) {
          String ticket = EMPTY_STRING;
          if (urlList[0].indexOf(SERVICE_ALIAS) > 0) {
            ticket = getTicket(info);
          if ((ticket == null) || (ticket.equals(EMPTY_STRING))) {
              return cl.getClassLoader();
            }
            ticket = TICKET + ticket;
          }
          URL[] urls = new URL[urlList.length * hostss.length];
          int y = 0;
          for (int i = 0; i < urlList.length; i++) {
            for (int k = 0; k < hostss.length; k++) {
              try {
                urls[y++] = new URL(hostss[k] + urlList[i] + ticket);
              } catch (java.net.MalformedURLException muf) {
                if (P4Logger.getLocation().beDebug()) {
                  P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.getClassLoader(Object, Class)", P4Logger.exceptionTrace(muf));
                }
              }
            }
          }
          urlLoader = new P4URLLoader(urls, cl.getClassLoader());
          weaks.put(cl, urlLoader);
          return urlLoader;
        }
        return cl.getClassLoader();
      }
    } catch (SecurityException e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("SecurityException:The Property key:\"P4ClassLoad\" value:\"P4Connection\" cannot be used because it is not allowed.)");
      }
    }
    if (info != null) {
      return ClassLoaderContext.getDynamicLoader(info.ownerId);
    }
    return null;
  }

  public ClassLoader getClassLoader(Object obj, Class cl, ClassLoader cLoader) {
    try {
      if(System.getProperty(STOP_P4_LOADING, "false").equalsIgnoreCase("true")){
        return cLoader;
      }
      StubBaseInfo info = null;
      if (obj instanceof StubImpl) {
        info = ((StubImpl) obj).p4_getInfo();
      } else if (obj instanceof StubBaseInfo) {
        info = (StubBaseInfo) obj;
      }
      if (System.getProperty("P4ClassLoad", "").equalsIgnoreCase("P4Connection")) {
        P4URLLoader urlLoader = (P4URLLoader) weaks.get(cl);
        if (urlLoader != null) {
          return urlLoader;
        }

        if (P4Logger.getLocation().beDebug()) {
          if ((info != null) && (info.getUrls() != null)) {
            for (int i = 0; i < info.urls.length; i++) {
              P4Logger.getLocation().debugT("P4 client broker: get Classloader parent loader : " + cLoader + " url[" + i + "]:=" + info.urls[i]);
            }
          }
        }
        String[] urlList = info.urls;
        String[] hostss = info.hosts;
        if ((urlList != null && hostss != null)) {
          if (urlList.length > 0) {
            String ticket = EMPTY_STRING;
            if (urlList[0].indexOf(SERVICE_ALIAS) > 0) {
              ticket = getTicket(info);
            if ((ticket == null) || (ticket.equals(EMPTY_STRING))) {
                return cl.getClassLoader();
              }
              ticket = TICKET + ticket;
            }
            URL[] urls = new URL[urlList.length * hostss.length];
            int y = 0;
            for (int i = 0; i < urlList.length; i++) {
              for (int k = 0; k < hostss.length; k++) {
                try {
                  urls[y++] = new URL(hostss[k] + urlList[i] + ticket);
                } catch (java.net.MalformedURLException muf) {
                  if (P4Logger.getLocation().beDebug()) {
                    P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.getClassLoader(Object, Class, ClassLoader)", P4Logger.exceptionTrace(muf));
                  }
                }
              }
            }
            if (P4URLLoader.class.isAssignableFrom(cLoader.getClass())) {
              ((P4URLLoader) cLoader).addMissedURLs(urls);
              return cLoader;
            } else {
              urlLoader = new P4URLLoader(urls, cLoader);
            }
            weaks.put(cl, urlLoader);
            return urlLoader;
          }
        }
        return cLoader;
      }
      if (info != null) {
        ClassLoader loader = ClassLoaderContext.getDynamicLoader(info.ownerId);
        if (loader instanceof DynamicClassLoader && !((DynamicClassLoader)loader).isConnected() && obj instanceof StubImpl) {
          ((DynamicClassLoader)loader).setStubContext((StubImpl)obj);
        }
        return loader;
      }
    } catch (SecurityException se) {
      if (P4Logger.getLocation().beWarning()) {
        P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerClientImpl.getClassLoader(Object, Class, ClassLoader)", "Getting of URL class loader failed. Security Exception: {0}", "ASJ.rmip4.rt2026" , new Object []{P4Logger.exceptionTrace(se)});
      }
    }
    return cLoader;
  }

  public ClientThreadContext getCTC() {
    try {
      return ClientFactory.getThreadContextFactory().getThreadContext();
    } catch (com.sap.engine.frame.client.ClientException e) { //$JL-EXC
      return null;
    }
  }

  public void attachThreadMonitoringInfo(String arg) {
  }

  public String getAttachedThreadMonitoringInfo() {
    return null;
  }

  public void setExecContextState(Object o) {
  }

  public Object applyExecContextState(Object pro) {
    return null;
  }

  public void setExecContext(Object o) {
  }


  public Exception getException(int type, String msgText, Throwable th) {
    return getException(type, msgText, th, null);
  }

  public Exception getException(int type, String msgText, Throwable th, Object[] args) {
    if (msgText.startsWith("p4_")) {
      try {
        msgText = P4ResourceAccessor.getResourceAccessor().getMessageText(Locale.getDefault(), msgText);
      } catch (MissingResourceException mre) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().traceThrowableT(P4Logger.DEBUG, "P4ObjectBrokerClientImpl.getException", mre);
        }
      }
    }
    switch (type) {
      case P4_IOException: {
          return new P4IOException(msgText, th, args);
        }
      case P4_ConnectionException:{
          return new P4ConnectionException(msgText, th, args);
        }
      case Initialize_Exception: {
          return new InitializingException(msgText, th, args);
        }
      case P4_MarshalException: {
          return new MarshalException(msgText, th, args);
        }
      case P4_ParseRequestException: {
          return new P4ParseRequestException(msgText);
        }
      case P4_RuntimeException:
      default: {
          return new P4RuntimeException(msgText, th, args);
        }
    }
  }

  public ConnectionObjectInt[] listConnections() {
    checkClosed(); // not thread safe but this method is not important ...
    ConnectionObject[] result = null;
    Hashtable allCons = this.manager.connections;
    result = new ConnectionObject[allCons.size()];
    Enumeration cons = allCons.elements();
    int index = 0;
    while (cons.hasMoreElements()) {
      ClientConnection c = (ClientConnection) cons.nextElement();
      int ind = 0;
      boolean ok = false;
      while (!ok) {
        try {
          Integer.parseInt("" + (c.connectionId.charAt(ind)));
          ok = true;
        } catch (Exception e) {  //$JL-EXC$
          ind++;
        }
      }
      if (ind < 3) {
        result[index] = new ConnectionObject(c.isAlive(), DEFAULT_COMMUNICATION_LAYER, c.host, c.port, -1, -1);
      } else {
        result[index] = new ConnectionObject(c.isAlive(), c.connectionId.substring(0, ind - 1), c.host, c.port, -1, -1);
      }
      index++;
    }
    return result;
  }


  /* (non-Javadoc)
   * @see com.sap.engine.services.rmi_p4.P4ObjectBroker#getHttp()
   */
  public String[] getHttp() {
    return null;//this cannot be used
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.rmi_p4.P4ObjectBroker#getServiceContext()
   */
  public ApplicationServiceContext getServiceContext() {
    return null;//this cannot be used
  }

  public boolean isServerBroker() {
    return false;
  }

  public com.sap.engine.interfaces.cross.RemoteBroker getRemoteBroker(String host, int port, Properties properties) throws Exception {
    Connection con = getInitialConnection(host, port, properties);
    String dsid = (String)properties.get("DestinationServerId");
    String _type = (String)properties.get("TransportLayerQueue");
    int destinationServerId  = 0;
    if (dsid != null) {
      destinationServerId = Integer.parseInt(dsid);
    }
    int dstId = checkCOs(con, destinationServerId, _type);
    //Check for load-balancing of connection to server node between resolve initial reference and cocr call 
    if (destinationServerId == 0) {
      properties.put("DestinationServerId", String.valueOf(dstId));
    } else {
      if (dstId != destinationServerId) {
        throw new P4IOException("DestinationServerId is different after context objects check. No loadbalancing detected. Tried to connect to " + destinationServerId + " but was connected to " + dstId);
      }
    }
    return new RemoteBroker(con, host, port, properties);
  }

  public ConnectionProvider getConnectionProvider() {
    return null;  //Todo
  }

  private ClientConnection getInitialConnection(String host, int port, Properties properties) throws IOException {
    String connectionType = null;
    if (properties != null) {
      connectionType = (String)properties.get("TransportLayerQueue");
      if (connectionType == null || connectionType.equals("")) {
        connectionType = DEFAULT_COMMUNICATION_LAYER;
      }
      String routerString = (String)properties.get(RemoteEnvironment.ROUTER_STRING);
      if (routerString !=null) {
        if (properties.get(RemoteEnvironment.ROUTER_PASSWORD) != null) {
          properties.put("SAPRouterString", routerString + "/H/" + host + "/S/" + port + "/P/" + properties.get(RemoteEnvironment.ROUTER_PASSWORD));
        } else {
          properties.put("SAPRouterString", routerString + "/H/" + host + "/S/" + port);
        }
      }
    }
    if (host.equals(LOCALHOST)) {
      try {
        host = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException unknownHost) {
        if (P4Logger.isPrepared() && P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.resolveInitialReference(String, String, int, Properties)", " Error in getting host address." + P4Logger.exceptionTrace(unknownHost));
        }
        try {
          host = InetAddress.getByName(IPv4_LOCALHOST).getHostAddress();
        } catch (UnknownHostException unHost) {
          if (P4Logger.isPrepared() && P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.resolveInitialReference(String, String, int, Properties)", " Error in getting local host address." + P4Logger.exceptionTrace(unHost));
          }
        }
      }
    } else {
      try {
        host = InetAddress.getByName(host).getHostAddress();
      } catch (UnknownHostException nohost) {
        if (connectionType.equalsIgnoreCase("SAPRouter")) {
          if (P4Logger.isPrepared() && P4Logger.getLocation().beError()) {
            P4Logger.getLocation().errorT("P4ObjectBrokerClientImpl.resolveInitialReference()", "The requested hostname " + host + " cannot be resolved");
          }
          if (P4Logger.isPrepared() && P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBrokerClientImpl.resolveInitialReference(String, String, int, Properties)", P4Logger.exceptionTrace(nohost));
          }
        } else {
          if (properties.getProperty(RemoteEnvironment.PROXY_HOST)!= null && properties.getProperty(RemoteEnvironment.PROXY_PORT)!= null ){
            if (P4Logger.isPrepared() && P4Logger.getLocation().bePath()) {
              P4Logger.getLocation().pathT("P4ObjectBrokerClientImpl.getInitialConnection(String, int, Properties)", "Can not get local host address. But connection is through proxy. Try to get address from proxy server");
            }
            //Try to get connection with the host as it is given originally, through the configured PROXY.
            //Do not throw UnknownHostException here, but try to connect stub through the proxy with the given host as it is.
          } else {
            throw nohost;            
          }
        }
      }
    }
    try {
    ClientConnection con =  manager.getConnection(connectionType, host, port, properties);
    con.host = host;
    con.port = port;
    return con;
    } catch (IOException e) {
        checkClosed();
        throw e;
    }
  }

   private synchronized void checkClosed() {
     if(isClose) {
         throw new P4RuntimeException("P4 Broker is closed");
     }
  }

}

