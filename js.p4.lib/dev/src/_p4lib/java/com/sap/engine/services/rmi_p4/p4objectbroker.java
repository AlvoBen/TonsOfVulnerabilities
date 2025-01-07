package com.sap.engine.services.rmi_p4;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.thread.*;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.all.ConnectionProfile;
import com.sap.engine.services.rmi_p4.classload.DynamicClassLoader;
import com.sap.engine.services.rmi_p4.dsr.DSRP4Instr;
import com.sap.engine.services.rmi_p4.exception.*;
import com.sap.engine.services.rmi_p4.reflect.P4InvocationHandler;
import com.sap.engine.services.rmi_p4.reflect.LocalInvocationHandler;
import com.sap.engine.services.rmi_p4.server.P4ServerObjectInfo;
import com.sap.engine.services.rmi_p4.interfaces.ConnectionObjectInt;
import com.sap.engine.services.rmi_p4.garbagecollector.finalize.FinalizeInformer;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.interfaces.cross.ConnectionProperties;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public abstract class P4ObjectBroker implements P4ExceptionConstants {

  public static boolean debug = SystemProperties.getBoolean("debug");

  /**
   * ********************* Property names *******************************
   */
  public static final String CALL_TIMEOUT = "CallTimeout";
  public static final String TRANSPORT_TYPE = "TransportLayerQueue";
  public static final String SERVER_SOCKET_PORT = "SocketPort";

  public static final String CLIENT_BROKER_CLASS = "com.sap.engine.services.rmi_p4.P4ObjectBrokerClientImpl";
  public static final String SERVER_BROKER_CLASS = "com.sap.engine.services.rmi_p4.server.P4ObjectBrokerServerImpl";

  private static final String PORTABLE_REMOTE_OBJECT_CLASS = "javax.rmi.CORBA.PortableRemoteObjectClass";
  private static final String IDENTITY = "com.sap.engine.system.";

  public static final String USING_OLD_CLASSCACHE = "ConfigurateClassCache";   //  cases -> OldCache / WithoutCache
  public static final String CLASSCACHE_STORE_DIRECTORY = "ClassCacheStoreDirectory"; // path for storing class cache

  /* security's context object name */
  public static final String SECURITY_CO = "security";
  /**
   * *********************************************************************
   */
  public static final String P4_DELIMITER = ":#:#:";

  public static final String STOP_P4_LOADING = "StopP4Loader";

  public static final String P4_LITE_PROPS = "p4Lite";
  public static boolean runP4Lite = false;

  public static final String SERVICE_ALIAS = "@download@";  // alias for services's jars
  public static final String EMPTY_STRING = "";

  protected static final byte[] PROTOCOL_VERSION = {118, 49};

  public static final String INITIALIZE_CONNECTION_TIMEOUT = "InitializeConnectionTimeout";
  public static final String RUNTIME_CONNECTION_TIMEOUT = "RuntimeConnectionTimeout";
  public static final String ENABLE_NAT = "EnableNAT";
  public static final String DEFAULT_COMMUNICATION_LAYER = "None";
  public static final String CACHE_GENERATED_SKEL = "CACHE_GENERATED_SKEL";
  
  public static final String LOCALHOST = "localhost";
  public static final String IPv4_LOCALHOST = "127.0.0.1";
  public static final String IPv6_LOCALHOST = "[::1]";
  
  public static final String INVOKER_KEY = "invoker_key";

  protected static boolean enabledStreamHooks = false;
  protected static boolean accountingServiceStatisticsEnabled = false;

  private boolean initialized = false;

  public static boolean isP4Stopped = false;
  
  /* prefix for operation name which tells that the communication can work via DataStream */
  public static final String OPTIMIZED_COM_PREDIX = "10_p4";

  /**
   * ***************** Current state of property values *****************
   */
  public static String transportType = DEFAULT_COMMUNICATION_LAYER;//transportType of current ObjectBroker
  public static Properties props = null;

  public static final String METHOD_PREFIX = "_p4_";
  public static final String PARAM_OPTIMIZED = "1" + METHOD_PREFIX;
  public static final String RESULT_OPTIMIZED = "2" + METHOD_PREFIX;
  public static final String BOTH_OPTIMIZED = "4" + METHOD_PREFIX;


  /**
   * *************** Singleton instance of P4ObjectBroker***************
   */
  protected static P4ObjectBroker broker;

  protected FinalizeInformer finalizeInformer;


  public Hashtable impls = new Hashtable();
  public Hashtable<String, P4RemoteObject> initObjects = new Hashtable<String, P4RemoteObject>();
  protected Hashtable redirectableObjects = new Hashtable();

  //Map for cache if skeleton is generated or dynamic not to search for generated skeleton every time
  public HashMap<String, WeakReference> skeletonClassCache = new HashMap<String, WeakReference>();
  //Flag if cache of found generated or dynamic skeletons is enabled or disabled.
  private static boolean skeletonClassCacheFlag = false;
  //Object for mark classes that has no generated skeleton and have to be raised dynamic one. 
  public static final String DYNAMIC_SKELETON = "DynamicSkel";
  
  //147 services are exported by default. May enlarge used memory in client side.
  public ObjectManager objManager = new ObjectManager(256);

  public int brokerId;
  public int id;

  public boolean useReiterationOfGC = false;

  public ConnectionProfile[] initialProfiles = null;

  protected WeakHashMap weaks = new WeakHashMap();

  //Thread local flag for checking Context Objects state
  public ThreadLocal coLoadFilter = new ThreadLocal();

  //Hash-map for names of forbidden context objects, by connection ID 
  private Hashtable<Connection, HashSet<String>> forbidenCOs = new Hashtable<Connection,  HashSet<String>>(2);

  public static boolean enableNAT = true;

  private static byte[] timeStamp = new byte[4];
  
  private static boolean useLocalCallOptimization = true;

  public static P4ObjectBroker getBroker() {
    return broker;
  }

  public FinalizeInformer getFinalizeInformer() {
    return finalizeInformer;
  }

  /**
   * Returns the P4ObjectBroker singleton object. This method always returns the same P4ObjectBroker instance, which
   * is an instance of the com.sap.engine.services.p4.P4ObjectBrokerClientImpl class or
   * com.sap.engine.services.p4.server.P4ObjectBrokerServerImpl if it is used inside in the SAP J2EE Server.
   *
   * @return server or client implementation of P4ObjectBroker instance.
   */
  public static P4ObjectBroker init() {
    return init(null);
  }

  /**
   * Returns the P4ObjectBroker singleton object. This method always returns the same P4ObjectBroker instance, which
   * is an instance of the com.sap.engine.services.p4.P4ObjectBrokerClientImpl class or
   * com.sap.engine.services.p4.server.P4ObjectBrokerServerImpl if it is used inside in the SAP J2EE Server.
   *
   * @param env Properties which will configure the P4ObjectBroker instance
   * @return server or client implementation of P4ObjectBroker instance.
   */
  public static P4ObjectBroker init(Properties env) throws InitializingException {
    if (isP4Stopped) {
      throw new InitializingException("P4 Service is stopping");
    }
    P4ObjectBroker temp = broker;
    if (temp != null && temp.isInitialized()) {
      return temp;
    } else {
      return  initInstance(env);

    }
  }



  private synchronized static P4ObjectBroker initInstance(Properties env) {
    if (broker != null && broker.isInitialized()) {
      return broker;
    }
    if(!P4Logger.isPrepared()){
      P4Logger.prepare();
    }
    int stamp = (int)((System.currentTimeMillis() - 60904821600000L)/1000);
    Convert.writeIntToByteArr(timeStamp, 0, stamp);
    String serverPort = null;
    if (env != null) {
      props = env;
      String callTimeOut = env.getProperty(CALL_TIMEOUT);
      String transport = env.getProperty(TRANSPORT_TYPE);
      serverPort = env.getProperty(SERVER_SOCKET_PORT);
      String connTimeOut = env.getProperty(INITIALIZE_CONNECTION_TIMEOUT);
      String runtimeTimeOut = env.getProperty(RUNTIME_CONNECTION_TIMEOUT);
      String natEnabled = env.getProperty(ENABLE_NAT);
      String cacheGeneratedSkel = env.getProperty(CACHE_GENERATED_SKEL);
      try {
        if (callTimeOut != null) {
          P4Call.TIMEOUT = Integer.parseInt(callTimeOut);
        }
        if (connTimeOut != null) {
          ClientConnection.INITIALIZE_CONNECTION_TIMEOUT = Integer.parseInt(connTimeOut);
        }
        if (runtimeTimeOut != null) {
          ClientConnection.RUNTIME_CONNECTION_TIMEOUT = Integer.parseInt(runtimeTimeOut);
        }
        if ( natEnabled != null && natEnabled.equals("false")) {
          enableNAT = false;
        }
        if (cacheGeneratedSkel != null && cacheGeneratedSkel.equals("true")) {
          setSkeletonClassCacheFlag(true);
        }
      } catch (Exception ex) {
        throw new InitializingException(ex.getMessage() + "\r\nIllegal value in properties\r\n<" + props + ">", ex);
      }

      if ((transport != null) && !transport.trim().equals("") && !transport.trim().equalsIgnoreCase("SAPRouter") ) {
        transportType = transport;
      } else {
        transportType = DEFAULT_COMMUNICATION_LAYER;
      }
    }
    P4ObjectBroker tempBroker = null;
    if (broker == null)  {
        Class _class = null;
        try {
          _class = Class.forName(P4ObjectBroker.getImplName());
        } catch (Exception e1) {
          throw new InitializingException(e1.getMessage() + "\r\nImplementation <" + P4ObjectBroker.getImplName() + "> of P4ObjectBroker not found: ", e1);
        }
        try {
          tempBroker = (P4ObjectBroker) _class.newInstance();
        } catch (Exception e2) {
          throw new InitializingException(e2.getMessage() + "\r\nCannot instantiate P4ObjectBroker: ", e2);
        }
    } else {
     tempBroker = broker;
    }
    tempBroker.id = tempBroker.getId();
    try {
      tempBroker.postInit(((serverPort != null) && (serverPort.length() != 0) ? Integer.parseInt(serverPort) : 0));
    } catch (Exception e3) {
      if (tempBroker != null) {
        tempBroker.close();
      }
      throw new InitializingException(e3.getMessage() + "\r\nCheck configuration for port <" + serverPort + "> for transport type <" + transportType + ">", e3);
    }
    tempBroker.initialized = true;
    broker = tempBroker;
    return broker;
  }

  protected synchronized static void closeInstance() {
    broker = null; // thats all we can do here currently
  }

  public static String getImplName() {
    String name = null;
    try {
      name = System.getProperty(PORTABLE_REMOTE_OBJECT_CLASS);
    } catch (SecurityException e) {
      if (P4Logger.getLocation().beWarning()) {
        P4Logger.trace(P4Logger.WARNING, "P4ObjectBroker.getImplName()", "P4ObjectBroker cannot read '{0}'. This causes security exception: {1}", "ASJ.rmip4.rt2009", new Object []{PORTABLE_REMOTE_OBJECT_CLASS, P4Logger.exceptionTrace(e)});
      }
      return CLIENT_BROKER_CLASS;
    }
    if ((name != null) && name.startsWith(IDENTITY)) {
      return SERVER_BROKER_CLASS;
    }
    return CLIENT_BROKER_CLASS;
  }


  protected P4ObjectBroker() {
    brokerId = (int) System.currentTimeMillis();
    if (objManager == null) {
      objManager = new ObjectManager(256);
    }
    runGarbageCollector();
  }

  protected Class getParentSkel(Class c, String suff) throws Exception {
    Class parent = c;
    Class skel = null;
    while (skel == null) {
      if (!(java.rmi.Remote.class.isAssignableFrom(parent))) {
        throw new Exception(parent.getName() + " does not implement or inherit java.rmi.Remote");
      }
      try {
        skel = parent.getClassLoader().loadClass(parent.getName() + suff);
      } catch (ClassNotFoundException e) { //$JL-EXC$
        parent = parent.getSuperclass();
      }
    }
    return skel;
  }


  public void connect(P4RemoteObject obj) {
    P4RemoteObjectInfo info = (P4RemoteObjectInfo) obj.getObjectInfo();

    if (info != null) {
      return;
    }

    info = getObjectInfo();
    info.protocol_id = PROTOCOL_VERSION;
    info.server_id = id;
    info.local_id = id;
    info.ownerId = brokerId;

    obj.setInfo(info);
    info.key = objManager.storeObject(obj);
    Skeleton skel = null;

    if (obj instanceof Skeleton) {
      skel = (Skeleton) obj;
    } else {
      Class _class = obj.getClass();
      try {
        _class = getParentSkel(obj.getClass(), "_Skel");
        try {
          skel = (Skeleton) _class.newInstance();
        } catch (Exception ex) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBroker.connect(P4RemoteObject)", "Cannot instantiate skeleton: " + _class + " in P4ObjectBroker.connect().\n Exception: " + P4Logger.exceptionTrace(ex));
          }
          skel = new P4DynamicSkeleton(obj);
        }
      } catch (Exception cnfex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBroker.connect(P4RemoteObject)", "Cannot instantiate skeleton: " + _class + " in P4ObjectBroker.connect().\n Exception: " + P4Logger.exceptionTrace(cnfex));
        }
        skel = new P4DynamicSkeleton(obj);
      }
    }

    info.skeleton = skel;
    if(SkeletonOpt.class.isAssignableFrom(skel.getClass())){
      info.setOptimization(true);
    }
    info.stubs = info.skeleton.getImplemntsObjects();
    obj.setConnected();
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("P4ObjectBroker.connect(P4RemoteObject)", "Connected remote object: " + obj + " delegate: " + obj.delegate + " info: " + obj.info);
    }
  }

  public void disconnect(P4RemoteObject object) {
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("P4ObjectBroker.disconnect(P4RemoteObject)", "Disconnecting remote object: " + object + " delegate: " + object.delegate + " info: " + object.info);
    }
    object.setDisconnected();
    if (objManager != null) {
      objManager.deleteObject(object);
    }
  }

  public void setInitialObject(String name, P4RemoteObject initObject) throws Exception {
    initObjects.put(name, initObject);
  }

  public byte[] getInitialObject(String name) throws java.rmi.RemoteException {
    if (initObjects.containsKey(name)) {
      P4RemoteObject initObject = initObjects.get(name);
      RemoteObjectInfo info = initObject.getObjectInfo();
      info.connectionProfiles = getConnectionProfiles();
      byte[] r = makeObjectRef(info.connectionProfiles, info.server_id, info.key);
      return r;
    } else {
      if (P4Logger.getLocation().beWarning()) {
        P4Logger.trace(P4Logger.WARNING, "P4ObjectBroker.getInitialObject(String)", "Object [{0}] was not found and could not be loaded. Client will not be able to lookup this object", "ASJ.rmip4.rt2010", new Object []{name});
      }
      throw new java.rmi.NoSuchObjectException("ID:011781 Object <" + name + ">");
    }
  }

  public byte[] objectToByteArr(P4RemoteObject _obj) {
    RemoteObjectInfo info = _obj.getObjectInfo();
    info.connectionProfiles = getConnectionProfiles();
    return makeObjectRef(info.connectionProfiles, info.server_id, info.key);
  }

  public StubBaseInfo byteArrayToObject(byte[] objectRef) {
    StubBaseInfo info = new StubBaseInfo();
    info.server_id = serverIdFromRef(objectRef);
    info.local_id = id;
    info.ownerId = ownerIdFromRef(objectRef);
    info.key = objectKeyFromRef(objectRef);
    info.connectionProfiles = connectionProfileFromRef(objectRef);
    info.client_id = -1;
    return info;
  }

  public StubBaseInfo stringToObject(String objectRef) {
    if (objectRef.startsWith("clusteraloc")) {
      throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Not_Implemented, null);
    }

    StringReader reader = new StringReader(objectRef);
    StringBuffer buffer = new StringBuffer(4);
    int ch;
    int k = 0;
    try {
      reader.skip(4);
      while ((ch = reader.read()) != -1) {
        if ((char) ch == ' ') {
          k++;
        }
      }

      reader.reset();
      reader.skip(5);
      byte[] ref = new byte[k];
      k = 0;

      while (((ch = reader.read()) != -1) && k < ref.length) {
        if ((char) ch != ' ') {
          buffer.append((char) ch);
        } else {
          int value = Integer.parseInt(buffer.toString());
          ref[k] = (byte) value;
          k++;
          buffer.delete(0, 4);
        }
      }
      int value = Integer.parseInt(buffer.toString());
      ref[k] = (byte) value;
      buffer.delete(0, 4);
      return byteArrayToObject(ref);
    } catch (IOException ioe) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBroker.stringToObject(String)", P4Logger.exceptionTrace(ioe));
      }
      return null;
    }
  }

  public String objectToString(P4RemoteObject obj) {
    RemoteObjectInfo info = obj.getObjectInfo();
    info.connectionProfiles = getConnectionProfiles();
    byte[] ref = makeObjectRef(info.connectionProfiles, info.server_id, info.key);
    return makeStringIOR(ref);
  }

  public String makeStringIOR(byte[] _ref) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("IOR:");

    for (byte a_ref : _ref) {
      stringBuffer.append(" ");
      stringBuffer.append((int) a_ref);
    }

    return stringBuffer.toString();
  }

  /**
   * @param info
   * @param stubClass
   * @return narrowed Object
   */
  public Object narrow(Object info, Class stubClass) {
    return narrow(info, stubClass, null);
  }

  public Object narrow(Object info, Class stubClass, String _connectionType) {
    return narrow(info, stubClass, _connectionType, stubClass.getClassLoader());
  }

  /**
   * @param info
   * @param stubClass
   * @param _connectionType
   * @return narrowed Object
   */
  public Object narrow(Object info, Class stubClass, String _connectionType, ClassLoader cLoader) {
    if (P4Logger.getLocation().bePath()) {
        if (info != null) {
          P4Logger.getLocation().pathT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Narrow to: " + stubClass + " object: \r\n" + info +  " Class: " + info.getClass() + "@" + Integer.toHexString(System.identityHashCode(info)) + " to " + stubClass + " connection type: " + _connectionType + " classloader: " +cLoader);
        } else {
          P4Logger.getLocation().pathT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Narrow to: " + stubClass + " object: null connection type: " + _connectionType + " classloader: " +cLoader);
        }
    }
    //if(stubClass.isAssignableFrom(info.getClass())){ TODO EDIT IT
    //  return info;
    //}
    // for backwards compatibility with some ATS tests
    if ((_connectionType != null) && (_connectionType.equals(""))) {
      _connectionType = transportType;
    }
    StubBaseInfo _info = null;
    if (info == null) {
      return null;
    } else if (info instanceof StubImpl) {
      _info = ((StubImpl) info).p4_getInfo();
    } else if (info instanceof StubBaseInfo) {
      _info = (StubBaseInfo) info;
    } else if (info instanceof P4ServerObjectInfo) {
      _info = StubBaseInfo.makeStubBaseInfo((RemoteObjectInfo) info);
    }
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "StubBaseInfo is:\r\n" + _info);
    }
    if (stubClass.isAssignableFrom(info.getClass())) {
      if (_info != null && !_info.connected) {
        try {
          makeConnection(_connectionType, (StubImpl) info);
          ClassLoader cl = getClassLoader(info, stubClass, cLoader);
          if (cl instanceof DynamicClassLoader) {
            //Check if DynamicClassLoader's stub is still connected, and 
            //if not, replace the loader's stub with current one... 
            if (!((DynamicClassLoader)cl).isConnected()) {
              if (P4Logger.getLocation().beDebug()){
                P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Change classloader's stub in case of already created stub to: " + info);
              }
              ((DynamicClassLoader)cl).setStubContext((StubImpl) info);
            }
          }
          ((StubImpl) info).p4_setClassLoader(cl);
        } catch (Exception ex) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", P4Logger.exceptionTrace(ex));
          }
          if (P4Logger.getLocation().beError()) {
            P4Logger.trace(P4Logger.ERROR, "P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Making connection for a stub failed.\r\n Stub instance <{0}> is not connected. {1}", "ASJ.rmip4.rt2011", new Object []{info, ex.toString()});
          }
        }
      }

      return info;
    }

    if (_info == null) {
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "P4ObjectBroker cannot narrow <{0}> to <{1}>", "ASJ.rmip4.rt2012", new Object []{info, stubClass});
      }
      return info;
    }

    if (_info.stubs == null) {
      _info.stubs = new String[1];
      _info.stubs[0] = stubClass.getName();
    }

    _info.local_id = id;
    StubImpl ret = null;
    Class _class = null;

    if (stubClass.isInterface()) {
      String name = stubClass.getName();
      _info = _info.cloneStubBaseInfo(); //skip connected flag
      ClassLoader urlcl = getClassLoader(info, stubClass, cLoader);
      if (urlcl instanceof DynamicClassLoader && !((DynamicClassLoader) urlcl).isConnected()) { // set to the dynamic loader's stub
        StubBaseImpl dynStub = new StubBaseImpl();
        dynStub.p4_setInfo(_info);
        dynStub.setClassLoader(urlcl);
        dynStub.setConnectionType(_connectionType);
        try {
          Connection r = getConnection(_connectionType, dynStub.p4_getInfo().connectionProfiles, dynStub.p4_getInfo());
          dynStub.setConnection(r);
          if (r != null) {
            if (P4Logger.getLocation().bePath()) {
              P4Logger.getLocation().pathT("P4ObjectBroker.narrow", "Stub " + dynStub + " connected to " + r);
            }
          } else {
            if (P4Logger.getLocation().beWarning()) {
              P4Logger.trace(P4Logger.WARNING, "P4ObjectBroker.narrow()", "Making connection for a stub failed.\r\n Stub instance <{0}> is not connected. Connection type: {1}", "ASJ.rmip4.rt2013", new Object []{dynStub, _connectionType});
            }
          }
        } catch (P4IOException io) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", P4Logger.exceptionTrace(io));
          }
          if (P4Logger.getLocation().beError()) {
            P4Logger.trace(P4Logger.ERROR, "P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Failed to load stub class: {0}", "ASJ.rmip4.rt2014", new Object []{name});
          }
        }
        
        if (P4Logger.getLocation().beDebug()){
          String message;
          if ( ((DynamicClassLoader)urlcl).isPrepared()) {
            message = "Change classloader's stub to: " + dynStub;
          } else {
            message = "Initialize classloader's stub to: " + dynStub;
          }
          P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", message);
        }
        ((DynamicClassLoader)urlcl).setStubContext((StubImpl) dynStub);
      } 
      try {
        if (urlcl == null) {
          _class = Class.forName(name + "_Stub");
        } else {
          _class = urlcl.loadClass(name + "_Stub");
        }
      } catch (ClassNotFoundException cnfex) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "P4 will use a proxy stub - not found generated stub: " + cnfex.toString());
        }
        StubImpl base = new StubBaseImpl();
        base.p4_setInfo(_info);
        base.p4_setClassLoader(urlcl);
        base.p4_setConnectionType(_connectionType);
        try {
          Connection r =   getConnection(_connectionType, base.p4_getInfo().connectionProfiles, base.p4_getInfo());
          base.p4_setConnection(r);
          if (r != null) {
            if (P4Logger.getLocation().bePath()) {
              P4Logger.getLocation().pathT("P4ObjectBroker.narrow", "Stub " + base + " connected to " + r);
            }
          } else {
            if (P4Logger.getLocation().beWarning()) {
              P4Logger.trace(P4Logger.WARNING, "P4ObjectBroker.narrow()", "Making connection for a stub failed.\n Stub instance <{0}> is not connected. Connection type: {1}", "ASJ.rmip4.rt2015", new Object []{base, _connectionType});
            }
          }
        } catch (P4IOException io) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", P4Logger.exceptionTrace(io));
          }
          if (P4Logger.getLocation().beError()) {
            P4Logger.trace(P4Logger.ERROR, "P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Failed to load stub class: {0}. Exception: {1}", "ASJ.rmip4.rt2016", new Object []{name, io});
          }
          return ret;
        }
        P4InvocationHandler p4Handler = new P4InvocationHandler();
        p4Handler.setInfo(base);

        Object proxyStubCandiadate = null;
        try {
          proxyStubCandiadate = getProxyOfInterfaces(_info.stubs, urlcl, p4Handler);
          if (proxyStubCandiadate != null) {
            return proxyStubCandiadate;
          }
        } catch (IllegalArgumentException cNotFound) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Proxy could not be created with classloader of the interface. Exception: \r\n" + P4Logger.exceptionTrace(cNotFound));
          }
        }

        proxyStubCandiadate = getProxyOfInterfaces(_info.stubs, Thread.currentThread().getContextClassLoader(), p4Handler);
        if (proxyStubCandiadate != null) {
          return proxyStubCandiadate;
        } else {
          throw new ClassCastException("Could not load a proxy stub. Possible reason: the context classloader does not have reference to P4 service");
        }
      }
      try {
        ret = (StubImpl) _class.newInstance();
      } catch (Exception ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", P4Logger.exceptionTrace(ex));
        }
        if (P4Logger.getLocation().beError()) {
          P4Logger.trace(P4Logger.ERROR, "P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Failed to instantiate the stub: {0}. Stub instance is not accessible", "ASJ.rmip4.rt2017", new Object []{_class});
        }
      }
      ret.p4_setInfo(_info);
      ret.p4_setConnectionType(_connectionType);
      ret.p4_setClassLoader(urlcl);
      try {
        makeConnection(_connectionType, ret);
      } catch (Exception ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", P4Logger.exceptionTrace(ex));
        }
        if (P4Logger.getLocation().beError()) {
          P4Logger.trace(P4Logger.ERROR, "P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Making connection for a stub failed.\r\n Stub instance <{0}> is not connected", "ASJ.rmip4.rt2018", new Object []{ret});
        }
      }
    }
    return ret;
  }

  public void makeConnection(String _connType, StubImpl _stub) throws P4IOException {
    Connection repl = getConnection(_connType, _stub.p4_getInfo().connectionProfiles, _stub.p4_getInfo());
    _stub.p4_setConnection(repl);
    if (repl != null) {
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("P4ObjectBroker.makeConnection(String, StubImpl)", "Stub " + _stub + " connected to " + repl);
      }
    } else {
      if (P4Logger.getLocation().beWarning()) {
        P4Logger.trace(P4Logger.WARNING, "P4ObjectBroker.makeConnection(String, StubImpl)", "Making connection for a stub failed.\r\n Stub instance <{0}> is not connected. Connection type: {1}", "ASJ.rmip4.rt2019", new Object []{ _stub, _connType});
      }
    }
  }

  public P4RemoteObject getObject(byte[] obj_key) {
    if (!(timeStamp[0] == obj_key[8] && timeStamp[1] == obj_key[9] && timeStamp[2] == obj_key[10] && timeStamp[3] == obj_key[11])) {
      return null;
    }
    return objManager.getObject(obj_key);
  }

  public void addLink(byte[] _key) {
    objManager.addLink(_key);
  }

  protected byte[] makeObjectRef(ConnectionProfile[] profiles, int _server_id, byte[] _key) {
    StringBuffer buffer = new StringBuffer();

    for (int i = 0; i < profiles.length; i++) {
      buffer.append(profiles[i].toString());

      if (i < profiles.length - 1) {
        buffer.append(":");
      }
    }

    int buffLength = buffer.length();
    char[] charArr = new char[buffLength];
    buffer.getChars(0, buffLength, charArr, 0);
    int index = 0;
    byte[] ref = new byte[8 + _key.length + 2 * buffLength];
    Convert.writeIntToByteArr(ref, 0, _server_id);

    Convert.writeIntToByteArr(ref, 4, brokerId);
    index += 8;
    System.arraycopy(_key, 0, ref, index, _key.length);
    index += _key.length;
    Convert.charArrToByteArr(ref, index, charArr, 0, buffLength);
    return ref;
  }

  public int serverIdFromRef(byte[] ref) {
    return Convert.byteArrToInt(ref, 0);
  }

  public int ownerIdFromRef(byte[] ref) {
    return Convert.byteArrToInt(ref, 4);
  }

  public byte[] objectKeyFromRef(byte[] ref) {
    byte[] _key = new byte[Message.OBJECT_KEY_SIZE];
    System.arraycopy(ref, 8, _key, 0, _key.length);
    return _key;
  }

  protected synchronized ConnectionProfile[] connectionProfileFromRef(byte[] _ref) {
    int length = _ref.length - (8 + Message.OBJECT_KEY_SIZE);
    char[] temp = Convert.byteArrToCharArr(_ref, 8 + Message.OBJECT_KEY_SIZE, length / 2);
    StringTokenizer tokenizer = new StringTokenizer(new String(temp), ":");
    int count = tokenizer.countTokens() / 3;
    ConnectionProfile[] result = new ConnectionProfile[count];

    for (int i = 0; i < count; i++) {
      result[i] = new ConnectionProfile(tokenizer.nextToken(), tokenizer.nextToken(), Integer.parseInt(tokenizer.nextToken()));
    }
    return result;
  }

  public ClassLoader getClassLoader(Object obj, Class s) {
    return s.getClassLoader();
  }

  public ClassLoader getClassLoader(Object obj, Class s, ClassLoader cl) {
    return cl;
  }

  public boolean NATEnabled() {
    return enableNAT;
  }

  public void setURLList(P4RemoteObject obj) {
  }

  public abstract ConnectionObjectInt[] listConnections();

  public abstract String getHost() throws IOException;

  public abstract int getPort() throws IOException;

  public abstract Exception getException(int type, String msgText, Throwable th);

  public abstract Exception getException(int type, String msgText, Throwable th, Object[] args);

  public abstract int getId();

  public abstract void postInit(int sPort);

  public abstract void close();

  public abstract void runGarbageCollector();

  public abstract void disposeConnection(Object _connection);

  public abstract void inform(Object _identifer, Message _message);

  public abstract ConnectionProfile[] getConnectionProfiles();

  public abstract P4RemoteObjectInfo getObjectInfo();

  public abstract P4RemoteObject loadObject(Remote obj) throws ClassNotFoundException;

  public abstract Object resolveInitialReference(String Name, String hos, int port) throws IOException;

  public abstract Object resolveInitialReference(String connectionType, String Name, String hos, int port) throws IOException;

  public abstract Object resolveInitialReference(String connectionType, String Name, String hos, int port, int destServerId) throws IOException;

  public abstract Object resolveInitialReference(String name, Connection repliable, int destServerId) throws IOException;

  public abstract Connection getConnection(String _type, ConnectionProfile[] _profile, StubBaseInfo _info) throws P4IOException;

  public abstract Object getCrossInterface(); //?????????

  public void log(String message, boolean d) {
    P4Logger.getLocation().logT(SEVERITY_INFO, "P4:" + Thread.currentThread() + ": " + message);
  }

  public void log(Exception ex, boolean d) {
    P4Logger.getLocation().logT(SEVERITY_INFO, ex.getMessage());
  }

  public abstract boolean localStubsAllowed();

  public abstract ClientThreadContext getCTC();

  public abstract void attachThreadMonitoringInfo(String arg);

  public abstract String getAttachedThreadMonitoringInfo();

  public abstract String[] getHttp();           //just for server impl

  public abstract ApplicationServiceContext getServiceContext();//just for server impl

  public abstract boolean isServerBroker();

  public static boolean isEnabledStreamHooks() {
    return enabledStreamHooks;
  }

  /**
   * @param o
   * @deprecated
   */
  public abstract void setExecContextState(Object o);

  /**
   * @param pro
   * @return
   * @deprecated
   */
  public abstract Object applyExecContextState(Object pro);

  /**
   * @param o
   * @deprecated
   */
  public abstract void setExecContext(Object o);

  private Object getProxyOfInterfaces(String[] interfaces, ClassLoader loader, P4InvocationHandler handler) {
    if (loader == null) {
      return null;
    }
    Class[] cl = new Class[interfaces.length + 1];
    int successfullyLoadedInterfaces = 0;
    try {
      cl[successfullyLoadedInterfaces] = loader.loadClass(RemoteRef.class.getName()); //it must possible to be loaded from that classloader  - else go to the context loader in catch block
      successfullyLoadedInterfaces++;
    } catch(ClassNotFoundException cnfex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBroker.getProxyOfInterfaces(String[], ClassLoader, P4InvocationHandler)", "Proxy could not be created with this classloader. Possible it do not reference the P4 service loader. Exception : \n" + P4Logger.exceptionTrace(cnfex));
      }
      return null; //RemoteRef is required interface of the proxy
    }


    for (int i = 0; i < interfaces.length; i++) {
      try {
        cl[successfullyLoadedInterfaces] = loader.loadClass(interfaces[i]);
        successfullyLoadedInterfaces++;
      } catch (ClassNotFoundException notFound) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Proxy will not implement that interface, bacause can not load it. Exception : \r\n" + P4Logger.exceptionTrace(notFound));
        }
      }
    }
    
    boolean notAllInterfacesLoaded = cl.length > successfullyLoadedInterfaces;
    
    if (notAllInterfacesLoaded) {
      Class[] temp = cl;
      cl = new Class[successfullyLoadedInterfaces];
      System.arraycopy(temp, 0, cl, 0, successfullyLoadedInterfaces);
    }

    Object candidate = Proxy.newProxyInstance(loader, cl, handler);
    
    //If we could not load all remote interfaces, try with context classloader, before return;
    if (notAllInterfacesLoaded) {
      ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
      if (contextLoader != null && contextLoader != loader) {
        Class[] contextCl = new Class[interfaces.length + 1];
        int successfullyContextLoadedInterfaces = 0;
        try {
          contextCl[successfullyContextLoadedInterfaces] = contextLoader.loadClass(RemoteRef.class.getName()); //it must possible to be loaded from that classloader  - else do not try with this classloader
          successfullyContextLoadedInterfaces++;
        } catch(ClassNotFoundException cnfex) {
          return candidate; //RemoteRef is required interface of the proxy, return what was loaded with current classlaoder.
        }
        for (int i = 0; i < interfaces.length; i++) {
          try {
            contextCl[successfullyContextLoadedInterfaces] = contextLoader.loadClass(interfaces[i]);
            successfullyContextLoadedInterfaces++;
          } catch (ClassNotFoundException notFound) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Proxy will not implement that interface, bacause can not load it. Exception : \r\n" + P4Logger.exceptionTrace(notFound));
            }
          }
        }
        if (successfullyContextLoadedInterfaces <= successfullyLoadedInterfaces) {
          return candidate;
        }
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("P4ObjectBroker.narrow(Object, Class, String, ClassLoader)", "Proxy will be raised with context classloader: " + contextLoader + ", because it loads more remote interfaces than original classloader: " + loader);
        }
        //Not all remote interface loaded, but more, than with original classloader.
        if (contextCl.length > successfullyContextLoadedInterfaces) {
          Class[] temp = contextCl;
          contextCl = new Class[successfullyContextLoadedInterfaces];
          System.arraycopy(temp, 0, contextCl, 0, successfullyContextLoadedInterfaces);
        }

        return Proxy.newProxyInstance(contextLoader, contextCl, handler);
      }
    }
    
    return candidate;
  }

  public boolean release(Remote stub) {
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("P4ObjectBroker.release(Remote)", "release called for: " + stub);
    }
    StubImpl impl = null;
    if (stub instanceof StubImpl) {
      impl = (StubImpl)stub;
    } else if (Proxy.isProxyClass(stub.getClass())) {
      InvocationHandler handler = Proxy.getInvocationHandler(stub);
      if (handler instanceof P4InvocationHandler) {
        impl = ((P4InvocationHandler)handler).getInfo();
      } else {
        return false;
      }
    } else {
      return false;
    }
    if (impl.info.connected) {
      impl.finalize();//$JL-FINALIZE$
      impl.p4_setConnection(null);
      impl.info.connected = false;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Should be used in combination with isLocal() method for assuring that the given argument 
   * to isLocal(Remote) method is a P4 stub or P4 dynamic stub. 
   * @param stub The Remote object that should be checked if it is a stub or not.
   * @return False if the given argument is not a P4 stub or P4 dynamic (proxy) stub; 
   *               or if the given argument is null; 
   */
  public boolean isP4Stub(Remote stub){
    if (stub == null) {
      return false;
    }
    if (stub instanceof StubImpl) {
      return true;
    }
    if (Proxy.isProxyClass(stub.getClass())) {
      InvocationHandler handler = Proxy.getInvocationHandler(stub);
      if (handler instanceof P4InvocationHandler || handler instanceof LocalInvocationHandler) {
        return true;
      } else { // some kind of a proxy but not a p4 dynamic stub
        return false;
      }
    } else { // some unknown Remote object
       return false;
    }
  }
  
  /**
   * Check if a P4 stub or P4 dynamic stub (proxy stub) is local.  
   * @param stub The P4 stub, that we want to check if it is local or not
   * @return true - if the stub is local;
   *         false - if the stub is not local (implementation is on remote VM)
   * @throws NullPointerException - if given Remote object is null;
   * @throws IllegalArgumentException - if the given Remote object is nor a P4 stub, 
   *                                    neither P4 dynamic stub (proxy stub)
   */
  public boolean isLocal(Remote stub) {
    if (stub == null) {
      throw new NullPointerException("null passed to isLocal");
    }
    StubBaseInfo info;
    if (stub instanceof StubImpl) {
      info = ((StubImpl)stub).info;
    } else if (Proxy.isProxyClass(stub.getClass())) {
      InvocationHandler handler = Proxy.getInvocationHandler(stub);
      if (handler instanceof P4InvocationHandler) {
        info = ((P4InvocationHandler)Proxy.getInvocationHandler(stub)).getInfo().info;
      } else if (handler instanceof LocalInvocationHandler) {
        return true;
      } else { // some kind of a proxy but not a p4 dynamic stub
        throw new IllegalArgumentException(stub + " is not P4 proxy");
      }
    } else { // some unknown object
       throw new IllegalArgumentException(stub + " is not P4 stub or P4 proxy");
    }
      return info.ownerId == brokerId && info.server_id == id;
  }

  /**
   * Check if the current implementation of P4ObjectBroker is initialized or not.
   * Used internal when init() or init(Properties) method is invoked several times, to 
   * synchronize initialization of the broker in order to create a singleton instance. 
   * @return true - if the broker is initialized;
   *         false - if the broker is not initialized yet.
   */
  public boolean isInitialized() {
      return initialized;
  }

  protected byte[] getTimeStamp() {
      return timeStamp;
  }

  /**
   * This method will not count objects with zero length any more.
   * We will skip context objects with zero length, because of ClientIDPropagator
   * in replies. It should not be send for optimization and backward compatibility.
   * 
   * @return The size of not empty known Transferable or TransferableExt context objects
   */
  public int getTCSize() {
    int tcSize = 0;
    int byteArrLen = 0;
    ClientThreadContext ctc = getCTC();
    if (ctc == null) {
        return 0;
    }
    try {
      ContextObjectNameIterator coIt = ctc.getTransferableContextObjectNames();
      tcSize = tcSize + 2;

      while (coIt.hasNext()) {
        String n = coIt.nextName();
        ContextObject co = ctc.getContextObject(n);
        if (co != null) {
          byteArrLen = ((Transferable) co).size();
          if (byteArrLen > 0) {
            tcSize = tcSize + 2;
            tcSize = tcSize + n.length();
            tcSize = tcSize + 2;
            tcSize = tcSize + byteArrLen;
          }
        }
      }
    } catch (Exception ex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBroker.getTCSize()", P4Logger.exceptionTrace(ex));
      }
        throw new com.sap.engine.services.rmi_p4.P4RuntimeException("Cannot determine the TheadContext objects size", ex);
    }
    return tcSize;
  }

  public int getTCSize(int owner_broker_id) {
    int tcSize = 0;
    int byteArrLen = 0;
    ClientThreadContext ctc = getCTC();
     if (ctc == null) {
        return 0;
    }
    try{
      ContextObjectNameIterator coIt = ctc.getTransferableContextObjectNames();

      while (coIt.hasNext()) {
        String n = coIt.nextName();
        ContextObject co = ctc.getContextObject(n);
        //n.equals(P4ObjectBroker.SECURITY_CO) &&
        if ( (co instanceof TransferableExt)) {
          byteArrLen = ((TransferableExt) co).size(owner_broker_id);
        } else {
          byteArrLen = ((Transferable) co).size();
        }
        tcSize = tcSize + 2 + n.length(); // 2 for name length
        tcSize = tcSize + 2 + byteArrLen; // 2 for co_size
      }
      tcSize += 2; //for co count

    } catch (Exception ex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBroker.getTCSize(int)", P4Logger.exceptionTrace(ex));
      }
      throw new P4RuntimeException("Cannot determine the TheadContext objects size", ex);
    }
    return tcSize;
  }


   static ArrayList<String> classStat = new ArrayList<String>(1);
   static AtomicInteger bytesLength = new AtomicInteger(0);

  /**
   * Only for Remote ClassLoading
   * @return total download bytes from the server
   */
  public static int getBytesStatistics(){
    return bytesLength.get();
  }

  /**
   * Only for Remote ClassLoading
   * @return List of all download resources
   */
  public static List getClassStatistics(){
    return classStat;
  }

  /**
   * Only for Remote ClassLoading
   * @param res register the download resource
   */
  public static void addRes(String res){
    classStat.add(res);
  }

  /**
   * Only for Remote ClassLoading
   * @param s increase total download bytes
   */
  public static void increaseClassStat(int s){
    bytesLength.addAndGet(s);
  }

  public void setCallTimeout(Remote stub, long timeout) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBroker.setCallTimeout(long)", "setting the call timeout of " + stub + " to " + timeout);
      }
      if (stub instanceof StubImpl) {
          ((StubImpl)stub).setCallTimeout(timeout);
      } else if (Proxy.isProxyClass(stub.getClass())) {
         InvocationHandler handler = Proxy.getInvocationHandler(stub);
         if (handler instanceof P4InvocationHandler) {
           ((P4InvocationHandler)Proxy.getInvocationHandler(stub)).getInfo().setCallTimeout(timeout);
         } else if (handler instanceof LocalInvocationHandler) {
           throw new IllegalArgumentException("Call Timeout cannot be set for local stubs");
         } else { // some kind of a proxy but not a p4 dynamic stub
        throw new IllegalArgumentException(stub + " is not P4 proxy");
      }
    } else { // some unknown object
       throw new IllegalArgumentException(stub + " is not P4 stub or P4 proxy");
    }
  }
  
  /**
   * Receive a Call object and return the destination host for this call.
   * Used in Call and InitialCall for better message destination description 
   * in thread's sub-tasks in MMC. 
   * 
   * @param call The Call object, we search host for.
   * @return The host and port of this call in format host:port
   */
  public static String getRemoteHost(P4Call call){
    if (broker == null){
      //Broker is closed
      return "";
    }
    return broker.getRmtHost(call);
  }
  
  /**
   * In Server implementation this method is overridden.
   * @param call The Call object, we search host for.
   * @return The host and port of this call in format host:port
   */
  public String getRmtHost(P4Call call){
    ConnectionProperties cp = call.repliable.getProperties();
    if (cp!= null) {
      return cp.getRemoteAddress() + ":" + cp.getRemotePort() + " ";
    } else {
      //This situation must not be possible for the client.
      //Connection Properties are not initialized in internal cluster Call only.
      return "server node " + call.getDestServerId() + " ";
    }
  }
  
  /*
   * This method checks if the remote side supports all context objects that are in the thread.
   * If there is some object that is not available in remote side, but remote side supports cocr, 
   * this context object is added in structure with forbidden context objects per this connection.
   * 
   * This method grant backward compatibility between new client jar and old server, that do not 
   * support ClientIDPropagator context object.
   * @param conn Connection that we check
   * @param destServerId Destination server node: 0 if not load-balanced yet. 
   * @param _type Connection type - used for narrow of ContextObjectClassReceiver stub.
   * @return destination server after load-balancing in case of not load-balanced connection 
   *         when method invoked. Returns the destination server id for connection if already 
   *         load-balanced to any server node.  
   */
  public int checkCOs(Connection conn, int destServerId, String _type){
    //Optimization for already checked connection, and prevent endless loop 
    //in checkCOs -> narrow -> getConnection -> CheckCOs -> ...
    if(forbidenCOs.get(conn) != null || coLoadFilter.get() != null) {
      return destServerId;
    }
    if (conn == null || broker == null) {//Check for closed or not initialized yet P4
      return destServerId;
    }
    
    
    if (P4Logger.isPrepared() && P4Logger.getLocation().beDebug()) {
      byte[] connID = conn.getId();
      if (connID != null){
        P4Logger.getLocation().debugT("P4ObjectBroker.checkCOs()", "Checking Context Objects for connection: " + conn + " for ID = " + Convert.byteArrToLong(conn.getId(), 0));        
      } else {
        P4Logger.getLocation().debugT("P4ObjectBroker.checkCOs()", "Checking Context Objects for connection: " + conn);
      }
      
    }
    
    ClientThreadContext ctc = broker.getCTC();
    if (ctc == null) { //Check for closed or not existing ClinetThreadContext
      return destServerId;
    }
    
    ContextObjectNameIterator coIt = ctc.getTransferableContextObjectNames();
    
    try {
      HashSet<String> tmpForbidden = new HashSet<String>(3);
      forbidenCOs.put(conn, tmpForbidden);
      
      coLoadFilter.set(new Boolean(true));
      //Here we prevent cycle invocation of resolveInitialReference(String connectionType, String Name, String hos, int port, int destServerId)
      StubBaseInfo info = (StubBaseInfo)P4ObjectBroker.getBroker().resolveInitialReference("cocr", conn, destServerId);
      if (destServerId == 0) {
        destServerId = info.server_id;
      }
      //Think for else if the received server id is different, but original destServerID is not zero 
      ContextObjectClassReceiver oo = (ContextObjectClassReceiver) P4ObjectBroker.getBroker().narrow(info, ContextObjectClassReceiver.class, _type);
      String co_name = null;
      Class c = null;

      
      /* context objects' iteration - put them all in forbidden list initially */
      while (coIt.hasNext()) {
        co_name = coIt.nextName();
        if (co_name.length() > 0) {
          tmpForbidden.add(co_name);
        }
      }
      
      //forbidenCOs.put(conn, tmpForbidden);
      Iterator<String> coIter = tmpForbidden.iterator();
      while (coIter.hasNext()) {
        co_name = coIter.next();
        try{
          if (P4Logger.isPrepared() && P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBroker.checkCOs()", "Checking context object " + co_name);
          }
          c = oo.getClassByName(co_name);
          if (c != null) {
            if (P4Logger.isPrepared() && P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("P4ObjectBroker.checkCOs()", "Removing " + co_name + " from forbidden objects...");
            }
            try {
              coIter.remove();
            } catch(UnsupportedOperationException noRemoveMehtodImplementedInThisJDK) {
              tmpForbidden.remove(co_name);
            }
          }
        }catch (RemoteException re){ //$JL-EXC$
          //in case of impossibility to check current context object, remove it from list only if it is known one.
          if (co_name.equals(SECURITY_CO) || co_name.equals(P4ContextObject.NAME)) { 
            coIter.remove(); 
          }
          if (P4Logger.getLocation().beInfo()) {
            P4Logger.getLocation().infoT("P4ObjectBroker.checkCOs()", "Context object " + co_name + " could not be checked because of exception. " + re.toString());
          }
        }
      }
      if (tmpForbidden.size() == 0 && P4Logger.isPrepared() && P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBroker.checkCOs()", "There is no unsupported context objects for remote side");
      }

    } catch (java.rmi.NoSuchObjectException noCOCR) { //$JL-EXC$
      //The other side is a stand-alone client. It has no cocr exported
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBroker.checkCOs()", "Remote side is a standalone client that cannot check support for transferable context objects");
      }
    } catch (P4ConnectionException connFailed) { //$JL-EXC$ 
      //The other side is not started yet or connection is failing 
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBroker.checkCOs()", "Remote side is restarting or stopped. Geting of initial object for Transferable Context Object Check failed. Original Exception: " + connFailed.toString());
      }
    } catch (IOException ioEx) {
      //The other side is stopping in JSPM scenario or just connection is failing with IOException. 
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBroker.checkCOs()", "Remote side is restarting or stopped. Geting of initial object for Transferable Context Object Check failed. Original Exception: " + ioEx.toString());
      }
    } catch (Exception e) { //$JL-EXC$
      //Unexpected Exception while resolving ContextObjectClassReceiver initial object from remote side:
      if (P4Logger.getLocation().beError()) {
        P4Logger.getLocation().errorT("P4ObjectBroker.checkCOs()", "Resolving of initial object from remote side failed. Check InitialContext's properties. Check exception for details: " + P4Logger.exceptionTrace(e));
      }
    } finally {
      coLoadFilter.remove();
    }
    return destServerId;
  }
  
  /*
   * This method checks if there is forbidden context objects about this connection
   * 
   * @param conn The connection as object
   * @return null if there is no forbidden elements for such connection, or 
   *              there is no such connection at all;
   *         not empty HashSet&lt;String&gt; with forbidden context objects names 
   *              if there are such ones.  
   */
  public HashSet<String> getForbiden(Connection conn){
    if (conn == null){
      return null;
    }
    return forbidenCOs.get(conn);
  }
  
  /*
   * This method removes forbidden elements for connection. It should be invoked when 
   * connection is disposed.
   * 
   * @param conn The connection as ID. Used in server environment
   */
  public void removeForbiden(byte[] conn) {
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("P4ObjectBroker.removeForbiden()", "Removing forbidden context objects for connection: " + conn);
    }
    if (conn != null){
      Iterator<Connection> cIt = forbidenCOs.keySet().iterator();
      try{
        while (cIt.hasNext()){
          if (cIt.next().getId().equals(conn)){
            cIt.remove();          
          }
        }
      }catch (Exception e){//$JL-EXC$
        //Could not remove connection with such ID
      }
    }
  }
  
  /*
   * This method removes forbidden elements for connection. It should be invoked when 
   * connection is disposed.
   * 
   * @param conn The connection as object.
   */
  public void removeForbiden(Connection conn) {
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("P4ObjectBroker.removeForbiden()", "Removing forbidden context objects for connection: " + conn);
    }
    if (conn != null){
      forbidenCOs.remove(conn);
    }
  }

  /**
   * Returns true if the monitoring of P4 communication via DSR service is enabled or not.
   * @return true if the tracing is enabled; 
   *         false if the tracing is disabled.
   */
  public static boolean isDSRMonitorable() {
    return DSRP4Instr.isRegistered();
  }
  
  /**
   * Check status for integration with Accounting service infrastructure. 
   * If accounting statistics for P4 are enabled returns true, otherwise returns false.
   * 
   * @return true, if accounting statistics for P4 are enabled
   *         false - otherwise
   */
  public static boolean isEnabledAccounting() {
    return accountingServiceStatisticsEnabled;
  }
  
  /**
   * Used in P4ServiceFrame, when enable or disable gathering of statistics from Accounting Service infrastructure.
   * Default value is false - statistics are off.
   * @param flag true - to enable statistics gathering. Cause performance degradation;
   *             false - to disable statistics gathering by accounting service.
   */
  public static void setAccountingFlag(boolean flag) {
    accountingServiceStatisticsEnabled = flag;
  }
  
  /**
   * Tricky integration with accounting service, delegating functionality to P4 service only.
   * Excluding P4 library from references to Accounting class in Accounting service. 
   * @param tag The name of transaction for detailed following
   * @param dcAccountedClass Class, whose DC will be used for measure of this transaction. 
   *                         Use EJB's generated skeleton to account statistics to EJB component, JMS' class to account statistics to JMS component. 
   *                         This way P4 will have pure P4 time, without including time for Remote Implementation. 
   */
  public void beginMeasure(String tag, Class dcAccountedClass){
    //Implemented only for ServerImpl
  }
  
  /**
   * Tricky integration with accounting service, delegating functionality to P4 service only.
   * Excluding P4 library from references to Accounting class in Accounting service. 
   * @param tag The name of transaction that ends after invocation of method {@link #beginMeasure(String, Class)} 
   */
  public void endMeasure(String tag){
    //Implemented only for ServerImpl
  }
  
  /**
   * Check if caching of skeleton's classes is enabled. 
   * This can be modified from P4 service property.
   * @return false if caching is disabled; and true if caching is enabled.
   */
  public static boolean isEnabledSkeletonClassCache() {
    return skeletonClassCacheFlag;
  }
  
  /**
   * Used from P4ServiceFrame to set the cache Flag according to service property that was read.
   * @param flag The flag if caching for seketon's classes is enabled or disabled.
   */
  public static void setSkeletonClassCacheFlag(boolean flag) {
    skeletonClassCacheFlag = flag;
  }
  
  /**
   * Used from P4ServiceFrame to set the local calls optimization. Default value is true
   * @param flag
   */
  public static void setLocalCallOptimization(boolean flag) {
	useLocalCallOptimization = flag;
  }
  
  /**
   * Check if local call optimization is enabled. 
   * This can be modified from P4 service property.
   * @return false if local call optimization is disabled - in this case all parameters, results and exception
   * will be (de)serialized instate of implementing LocalRemoteByRef interface.
   */
  public static boolean isLocalCallOptimization() {
	  return useLocalCallOptimization;
  }
  
}
