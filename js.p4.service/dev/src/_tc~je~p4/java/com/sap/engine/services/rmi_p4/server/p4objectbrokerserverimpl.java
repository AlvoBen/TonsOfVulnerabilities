package com.sap.engine.services.rmi_p4.server;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.IdentityHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.Hashtable;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.frame.core.thread.ClientThreadContext;
import com.sap.engine.interfaces.cross.*;
import com.sap.engine.interfaces.cross.io.ConnectionProvider;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.rmi_p4.*;
import com.sap.engine.services.rmi_p4.RemoteBroker;
import com.sap.engine.services.rmi_p4.garbagecollector.finalize.FinalizeInformer;
import com.sap.engine.services.rmi_p4.all.ConnectionProfile;
import com.sap.engine.services.rmi_p4.all.MessageConstants;
import com.sap.engine.services.rmi_p4.exception.P4BaseConnectionException;
import com.sap.engine.services.rmi_p4.exception.P4BaseIOException;
import com.sap.engine.services.rmi_p4.exception.P4BaseMarshalException;
import com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException;
import com.sap.engine.services.rmi_p4.exception.P4ExceptionConstants;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.interfaces.ConnectionObjectInt;
import com.sap.engine.services.rmi_p4.interfaces.P4RemoteLoadingExt;
import com.sap.engine.services.rmi_p4.reflect.AbstractInvocationHandler;
import com.sap.engine.services.rmi_p4.reflect.LocalInvocationHandler;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @author Georgy Stanev
 * @version 7.10
 */
public class P4ObjectBrokerServerImpl extends P4ObjectBroker implements ObjectBrokerInterface {

  public static final String EJBLOADER = "com.sap.engine.services.ejb.deploy.EJBLoader";
  public static final String APPLOADER = "com.sap.engine.services.deploy.server.ApplicationLoader";

  public static final String ALIAS = "@download@";                                                            // alias for jars
  public static final String DOWNLOAD_SERVICE_DIRS = "Bin Directory";                                         // a property about destination(root directory) of services jars
  public static final String DOWNLOAD_APPS_DIRS = "Applications Directory";                                   // a property about destination(root directory) of application jars
  public static final String HTTP_PREFIX = "http://";                                                         // String tag for composing URL
  public static final String APPDIR = "apps";                                                                 // String for parsing everyone resopurce which points application resource
  public static final String BINDIR = "bin";                                                                  // String for parsing everyone resopurce which points service resource
  public static final String APP_DIR_SEP = File.separator + APPDIR + File.separator;                          // composed application tag
  public static final String BIN_DIR_SEP = File.separator + BINDIR + File.separator;                          // composed service tag
  public static final String APP_ALIAS_SEP = ALIAS + File.separator + APPDIR.toUpperCase() + File.separator;  // application alias tag
  public static final String BIN_ALIAS_SEP = ALIAS + File.separator + BINDIR.toUpperCase() + File.separator;  // service alias tag
  
  public int thisServerId;
  public P4SessionProcessor sessionProcessor;
  private ApplicationServiceContext appContext;
  private Map<Object, WeakReference> portableRemoteObjects = Collections.synchronizedMap(new IdentityHashMap<Object, WeakReference>(256));
  private ConfigurationHandler handler = null;
//  private ComponentExecutionContext cec = null;


  public static final String SLEEP_FOR_NEW_WRITE_BROKERID = "SLEEP_FOR_WRITE_BROKERID";
  public static final String TRIES_TO_WRITE_BROKERID = "TRIES_TO_WRITE_BROKERID";
  public static String p4_Persistent = "P4_Persistent"; // root in configuration - save brokerId
  private final static String p4_Entity = "P4_PersistentObject"; // root in configuration
  protected String[] http = null;

  private static final long DEFAULT_SLEEP_FOR_READ = 2000;
  private static final int DEFALUT_TRIES_FOR_READ = 8;

  public static final String SERVER_DIR = "j2ee" + File.separator + "cluster" + File.separator + "bin" + File.separator;
  public static final String APPLICATION_DIR = "cluster" + File.separator + "server0" + File.separator + "apps" + File.separator; //TODO !!!!! CHANGE IT!!!!!! HARDCODED
  public static final String APPLOADER_NAME = "application:";
  
  //Enable search for generated stubs in case of local stub in server environment
  private static boolean instantiateGeneratedLocalStubs = true;


  /**
   * Constructor invoked for P4 Broker, when P4 core service starts. 
   * @param appContext Service Context for P4 service - base context.
   */
  protected P4ObjectBrokerServerImpl(ApplicationServiceContext appContext) {
    this.id = appContext.getClusterContext().getClusterMonitor().getCurrentParticipant().getClusterId();
    this.thisServerId = id;
    this.appContext = appContext;
    broker = this;
    init();
    CrossObjectBroker.registerP4ProtocolProvider(this);
  }
  
  /**
   * Invoked when P4 core service is stopping.
   */
  public void close() {
    transportType = DEFAULT_COMMUNICATION_LAYER;
    impls.clear();
    initObjects.clear();
    try {
      finalizeInformer.close();
    } catch (java.lang.SecurityException e) {
      if (P4Logger.getLocation().beWarning()) {
    	P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerServerImpl.close()", "Closing Finalizer failed: \r\n {0}", "ASJ.rmip4.rt1011", new Object []{P4Logger.exceptionTrace(e)});  
      }
    }
    CrossObjectBroker.unregisterP4ProtocolProvider();
    finalizeInformer = null;
    broker = null;
  }

  /**
   * Returns new instance of P4RemoteObjectInfo
   */
  public P4RemoteObjectInfo getObjectInfo() {
    return new P4ServerObjectInfo();
  }

  public Object resolveInitialReference(String name, String host, int port) throws IOException {
    return resolveInitialReference(DEFAULT_COMMUNICATION_LAYER, name, host, port, 0);
  }

  public Object resolveInitialReference(String connectionType, String name, String host, int port) throws IOException {
    return resolveInitialReference(connectionType, name, host, port, 0);
  }

  /**
   * Resolve initial reference to object exported with name given in parameter "name".
   * @param connectionType Connection type. None, SSL, SAPRouter, etc
   * @param name The name of initial object that is resolved
   * @param host The host for this connection
   * @param port The P4 port for this connection
   * @param destServerId Server node ID for this connection
   */
  public Object resolveInitialReference(String connectionType, String name, String host, int port, int destServerId) throws IOException {
    //replace host with real IP, if localhost is specified
    if (host.equals(LOCALHOST) || host.equals(IPv4_LOCALHOST) || host.equals(IPv6_LOCALHOST)) {
      host = changeLocalHost(host);
    }
    
    if (sessionProcessor != null) {
      Connection rep = sessionProcessor.getMessageProcessor().getConnection(connectionType, host, port, true);
      if (rep.isLocal()) {
        if ((destServerId == 0) || (destServerId == getId())) {
          return this.byteArrayToObject(getInitialObject(name));
        } else {
            rep = sessionProcessor.getConnection(destServerId);
        }
      }
      int nameLength = name.length();
      InitialCall call = InitialCall.getInitialCall(rep, name);
      call.setDestServerId(destServerId);
      byte[] message = new byte[ProtocolHeader.THREAD_CONTEXT_SIZE + 2 * nameLength];
      call.writeId(message, ProtocolHeader.HEADER_SIZE);
      message[ProtocolHeader.MESSAGE_TYPE] = 10;
      ProtocolHeader.writeHeader(message, 0, message.length, destServerId);
      Convert.writeUStringToByteArr(message, ProtocolHeader.THREAD_CONTEXT_SIZE, name);
      rep.sendRequest(message, message.length, call);
      byte[] res = null;
      try {
        res = call.getResult(null);
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
      if ((destServerId != 0) && (destServerId != -1) && (stubInfo.server_id != destServerId)) {
        throw new java.rmi.RemoteException("Server with ID " + destServerId + " does not exist");
      }
      StringBuffer _id = new StringBuffer(String.valueOf(stubInfo.ownerId));
      _id.append(":");
      _id.append(connectionType);
      _id.append(":");
      _id.append(host);
      _id.append(":");
      _id.append(port);
      String ids = _id.toString();
      stubInfo.setIncomingProfile(ids);
      initialProfiles = stubInfo.connectionProfiles;
      return stubInfo;
    } else {
      sessionProcessor = P4SessionProcessor.getSessionProcessor();
      thisServerId = sessionProcessor.getServerId();
    }
    return null;
  }

  public static String getImplName() {
    return "com.sap.engine.services.rmi_p4.server.P4ObjectBrokerServerImpl";
  }

  public void setHttp(String[] http) {
    this.http = http;
  }


  public Connection getConnection(String _connectionType, ConnectionProfile[] _profiles, StubBaseInfo _info) throws P4IOException {
    if (sessionProcessor != null) {
      sessionProcessor.isReady();
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("P4ObjectBrokerServerImpl.getConnection(String, ConnectionProfile[], StubBaseInfo)", "_info.ownerId=" + _info.ownerId + " brokerId=" + brokerId);
      }
      if (_info.ownerId != brokerId) {
        try {
          if (_profiles == null) {
            _profiles = initialProfiles;
          }
          if (_info.getIncomingProfile() != null) {
            try {
              StringTokenizer tokenizer = new StringTokenizer(_info.getIncomingProfile(), ":");
              String type = tokenizer.nextToken();
              String host = tokenizer.nextToken();
              int port = Integer.parseInt(tokenizer.nextToken());
              Connection repliable = sessionProcessor.getMessageProcessor().getConnection(type, host, port, false);
              super.checkCOs(repliable, 0, type);
              return repliable;
            } catch (Exception e) {  // incoming profile no longer available for some reason
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.getConnection(String, ConnectionProfile[], StubBaseInfo)", e.getMessage());
              }
            }
          }
          if ((_connectionType == null) || (_connectionType.equals(""))) {   //just in case
            _connectionType = DEFAULT_COMMUNICATION_LAYER;
          }
          for (int p = 0; p < _profiles.length; p++) {    // first try with only with _connectionType
            if (_profiles[p].getType().equals(_connectionType)) {
              try {
                Connection repliable = sessionProcessor.getMessageProcessor().getConnection(_profiles[p].getType(), _profiles[p].getHost(), _profiles[p].getPort(), false); //sessionProcessor.getConnection(_profiles[p].getType(), _profiles[p].getHost(), _profiles[p].getPort(), _info, force);
                super.checkCOs(repliable, 0, _profiles[p].getType());
                return repliable;
              } catch (Exception e) {
                if (P4Logger.getLocation().beDebug()) {
                  P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.getConnection(String, ConnectionProfile[], StubBaseInfo)", P4Logger.exceptionTrace(e));
                }
              }
            }
          }
          for (int p = 0; p < _profiles.length; p++) {  //then with the rest of the profiles
            if (!_profiles[p].getType().equals(_connectionType)) {
              try {
                Connection repliable = sessionProcessor.getMessageProcessor().getConnection(_profiles[p].getType(), _profiles[p].getHost(), _profiles[p].getPort(), false);//sessionProcessor.getConnection(_profiles[p].getType(), _profiles[p].getHost(), _profiles[p].getPort(), _info);
                super.checkCOs(repliable, 0, _profiles[p].getType());
                return repliable;
              } catch (Exception e) {
                if (P4Logger.getLocation().beDebug()) {
                  P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.getConnection(String, ConnectionProfile[], StubBaseInfo)", P4Logger.exceptionTrace(e));
                }
              }
            }
          }
        } catch (Exception ex) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.getConnection(String, ConnectionProfile[], StubBaseInfo)", P4Logger.exceptionTrace(ex));
          }
          throw (P4IOException) broker.getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Unable_to_Open_Connection, ex);
        }
      } else {
        _info.local_id = _info.server_id;     //TODO Vancho ama kakvo izobshto e tova
        return sessionProcessor.getConnection(_info.server_id);//TODO Vancho  - kakvo da pravja tazi local
      }
   } else {
      sessionProcessor = P4SessionProcessor.getSessionProcessor();
      thisServerId = sessionProcessor.getServerId();
    }
    return null;
  }

  public int generateBrokerId(String sss) {
    int h = 0;
    int off = 0;
    char val[] = sss.toCharArray();
    int len = val.length;

    for (int i = 0; i < len; i++) {
      h = (31 * h) + val[off++];
    }
    return h;
  }

  private void createRedirConfig() {
    try {
      ConfigurationHandlerFactory factory = appContext.getCoreContext().getConfigurationHandlerFactory();
      handler = factory.getConfigurationHandler();
      handler.openConfiguration(p4_Entity, ConfigurationHandler.READ_ACCESS);
      return; //the configuration P4_PersistentObject had created yet. nothing to do
    } catch (NameNotFoundException nn) {
      // $JL-EXC$
      try {
        handler.createRootConfiguration(p4_Entity);
      } catch (NameAlreadyExistsException nae) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.createRedirConfig()", P4Logger.exceptionTrace(nae));
        }
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("P4ObjectBrokerServerImpl.createRedirConfig()", "P4_PersistentObject was created and this try is redundant");
        }
      } catch (ConfigurationLockedException cle) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.createRedirConfig()", P4Logger.exceptionTrace(cle));
        }
        SimpleLogger.log(Severity.WARNING, Category.SYS_SERVER, P4Logger.getLocation(), "ASJ.rmip4.cf0002", "RMI_P4 failed to create its configuration in database. Another server process is starting or possible problem with database: {0}", new Object []{cle.toString()} );
      } catch (ConfigurationException ce) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.createRedirConfig()", P4Logger.exceptionTrace(ce));
        }
        SimpleLogger.log(Severity.WARNING, Category.SYS_SERVER, P4Logger.getLocation(), "ASJ.rmip4.cf0003", "RMI_P4 failed to create its configuration in database; possible problem with database: {0}", new Object []{ce.toString()} );
      }
    } catch (Exception e) {
      SimpleLogger.log(Severity.WARNING, Category.SYS_SERVER, P4Logger.getLocation(), "ASJ.rmip4.cf0004", "RMI_P4 failed to open its configuration in database. Getting of configuration handler failed. Possible problem with database: {0}", new Object []{e.toString()} );
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.createRedirConfig()", P4Logger.exceptionTrace(e));
      }
    } finally {
      if (handler != null) {
        try {
          handler.commit();
          handler.closeAllConfigurations();
        } catch (ConfigurationException e) {
          SimpleLogger.log(Severity.WARNING, Category.SYS_SERVER, P4Logger.getLocation(), "ASJ.rmip4.rt0005", "RMI_P4 failed to commit/close its configuration in database; possible problem with database : {0}", new Object []{e.toString()} );
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.createRedirConfig()", P4Logger.exceptionTrace(e));
          }
        }
      }
    }
  }

  public void postInit(int sPort) {
   // sessionProcessor = P4SessionProcessor.getSessionProcessor();
    //thisServerId = sessionProcessor.getServerId();
    createRedirConfig();
    brokerId = generateBrokerId(com.sap.bc.proj.jstartup.JStartupFramework.getParam("j2ee/ms/host") + "_" + com.sap.bc.proj.jstartup.JStartupFramework.getParam("j2ee/ms/port"));
    finalizeInformer = new FinalizeInformer(10, 2, 3000);
  }

  /**
   * @param info - StubBaseInfo of the stub whose URL-class loaders are to be initialised  
   * 
   */
  private void configureHosts(P4RemoteObjectInfo info) {
    info.hosts = new String[http.length];
    for (int n = 0; n < http.length; n++) {
      info.hosts[n] = HTTP_PREFIX + http[n];
    }
  }

  private String[] configureResources(String[] res, String loaderName) {
    Vector<String> list = new Vector<String>();

    if (res != null) {
      String app_root_dir = SystemProperties.getProperty(DOWNLOAD_APPS_DIRS);
      String server_root_dir = SystemProperties.getProperty(DOWNLOAD_SERVICE_DIRS);

      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(P4ObjectBrokerServerImpl.class + " configureResources() The original SET of Application Directory is :" + app_root_dir + " and the original SET of Bin Directory is : " + server_root_dir + ". If these properties are null, please, see running mode of \"DOWNLOAD\" application and its correct working!");
      }
      if (app_root_dir == null || app_root_dir.indexOf(".") != -1) {//this fix is made because the download application isn't working MAKE TRACE for server root!!!!!
        app_root_dir = APPLICATION_DIR;//APP_DIR_SEP;// /apps/
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT(P4ObjectBrokerServerImpl.class + " configureResources() : APP ROOT DIR : " + app_root_dir);
        }
      }
      if (server_root_dir == null || server_root_dir.indexOf(".") != -1) {// this fix is made because the download application isn't working MAKE TRACE for server root!!!!!
        server_root_dir = SERVER_DIR;//  j2ee/cluster/bin
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT(P4ObjectBrokerServerImpl.class + " configureResources() : BIN ROOT DIR : " + server_root_dir);
        }
      }

      for (int i = 0; i < res.length; i++) {

        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT(P4ObjectBrokerServerImpl.class + " configureResources(): resource : " + res[i]);
        }

        if (res[i].lastIndexOf(app_root_dir) != -1) {
          int temp = res[i].lastIndexOf(app_root_dir);
          if (temp != -1) {
            list.addElement("/" + APP_ALIAS_SEP + res[i].substring(temp + app_root_dir.length()));
          } else {
            res[i] = null;
          }
        } else {
          int temp = res[i].indexOf(server_root_dir);
          if (temp != -1) {
            list.addElement("/" + BIN_ALIAS_SEP + res[i].substring(temp + server_root_dir.length()));  // /@download@/bin/....jar);
          } else {
            res[i] = null;
          }
        }
      }

      return list.toArray(new String[0]);
    }
    return null;
  }

  public void setURLList(P4RemoteObject obj) {

    if ((http == null) || (http.length == 0) || obj == null) {
      return;
    }

    P4RemoteObjectInfo info = obj.getInfo();
    if (info.getUrls() != null) {
      return;
    }

    LoadContext lc = sessionProcessor.serviceContext.getCoreContext().getLoadContext();
    ClassLoader cLoader = obj.delegate.getClass().getClassLoader();
    String loader_name = lc.getName(cLoader);
    String[] listName = null;

    if ((obj.delegate != null) && (obj.delegate instanceof P4RemoteLoadingExt)) {
      listName = configureResources(((P4RemoteLoadingExt) obj.delegate).getResources(), loader_name);
    } else {
      listName = configureResources(lc.getResourceNames(cLoader), loader_name);
    }

    info.urls = listName;
    if (P4Logger.getLocation().beDebug() && info.urls != null) {
      for (int i = 0; i < info.urls.length; i++) {
        P4Logger.getLocation().debugT("Server Broker: Final Resource[" + i + "]= " + info.urls[i]);
      }
    }
    configureHosts(info);//prepare http hosts
  }

  public void connect(P4RemoteObject obj) {
    super.connect(obj);
  }

  public Object narrow(Object info, Class stubClass, String _connectionType) {
    return narrow(info, stubClass, _connectionType, null);
  }

  public Object narrow(Object info, Class stubClass, String _connectionType, ClassLoader cLoader) {
    StubBaseInfo _info = null;
    boolean isPermanentLoader = true;

    if (cLoader == null) {
      cLoader = stubClass.getClassLoader();
      isPermanentLoader = false;
    }

    if (info == null) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(P4ObjectBrokerServerImpl.class + " narrow() null object to stubClass : " + stubClass + " <> classloader : " + cLoader);
      }
      return null;
    }
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT(P4ObjectBrokerServerImpl.class + " narrow() info class : " + info.getClass() + " stubClass : " + stubClass + " <> classloader : " + cLoader);
    }
    if (info instanceof ObjectReferenceImpl) {
      info = ((ObjectReferenceImpl) info).toObject(cLoader);
    }


    if (info instanceof RemoteRef) {
      RemoteObjectInfo remInfo = ((RemoteRef) info).getObjectInfo();
      if (remInfo instanceof StubBaseInfo) {
        _info = (StubBaseInfo) remInfo;
      } else {
        _info = StubBaseInfo.makeStubBaseInfo(remInfo);
      }
    } else if (info instanceof StubBaseInfo) {
      _info = (StubBaseInfo) info;
    } else if (info instanceof P4ServerObjectInfo) {
      _info = StubBaseInfo.makeStubBaseInfo((RemoteObjectInfo) info);
    } else if (stubClass.isAssignableFrom(info.getClass())) {
      return info;
    } else {
      throw new ClassCastException(stubClass.getName());
    }

    if (stubClass.isAssignableFrom(info.getClass())) {
      if (_info.ownerId == brokerId && _info.server_id == thisServerId) {
        if (!Proxy.isProxyClass(info.getClass())) {
          ((StubImpl) info).isLocal = true;
          _info.local_id = id;
          ((StubImpl) info).p4remote = getObject(_info.key);
          _info.connected = true;
        }
        return info;
      }

      if (_info != null && !_info.connected) {
        try {
          makeConnection(_connectionType, (StubImpl) info);
        } catch (Exception ex) {
          if (P4Logger.getLocation().beError()) {
          	P4Logger.trace(P4Logger.ERROR, "P4ObjectBrokerServerImpl.narrow()", "Opening connection failed. Stub instance <{0}> is not connected", "ASJ.rmip4.rt1012", new Object []{info});  
        	  
          }
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.narrow()", P4Logger.exceptionTrace(ex));
          }
        }
      }

      return info;
    }

    if (_info.ownerId == brokerId && _info.server_id == thisServerId) {
      if (!P4ServiceFrame.instantiateLocalStubs) {
        return getObject(_info.key).getDelegate();
      } else {
        return loadLocalStub(_info, stubClass, cLoader, isPermanentLoader);
      }
    } else {
      return super.narrow(info, stubClass, _connectionType, cLoader);
    }
  }

  public ConnectionObjectInt[] listConnections() {
    return sessionProcessor.organizer.listConnections();
  }

  public boolean pingConnection(Object obj) {
    return false;
  }

  public boolean pingObject(Object obj) {
    return false;
  }

  public String getHost() throws IOException {
    return "remotehost";
  }

  public int getPort() throws IOException {
    return 0;
  }

  /**
   * This method raise local stub for server environment. Local stubs replicate its parameters and 
   * return value to every invoked method. Local stubs can be forbidden by setting service property 
   * INSTANTIATE_LOCAL_STUBS = false; from configtool. However, missing of local stubs may have side 
   * effects causing some functionality to fail - if expect stub, but receive exported implementation itself. 
   * @param info Stub base info for the local stub
   * @param stubClass The class, whose stub will be raised
   * @param cl_loader null or custom class-loader for try raising local stub.
   * @param isPermanentLoader Flag, if the class-loader was given to narrow from caller, or 
   *                          got from the stub's class inside narrow method. 
   * @return Local proxy stub, or local generated stub if there is generated stub and service 
   *         property enable searching and loading of generated local stubs. 
   */
  public Object loadLocalStub(StubBaseInfo info, Class stubClass, ClassLoader cl_loader, boolean isPermanentLoader) {
    if (stubClass.isInterface()) {
      Class _class = null;
      String name = stubClass.getName() + "_Stub";
      //class-loader that successfully loaded the generated stub
      ClassLoader stub_loader = null;
      //class-loader that sees the stub class - used in searching generated stub and constructing a proxy
      ClassLoader class_loader = null;
      //Thread-context class-loader - used in searching of generated stub and constructing a proxy
      ClassLoader threadLoader = null;

      if (instantiateGeneratedLocalStubs) {
      //Begin trying of loading generated stub:
      //Loading with specified class-loader. If not specified - with current class-loader(Class.forName())
      try {
        if (cl_loader == null) {
          _class = Class.forName(name);
          isPermanentLoader = false;
        } else {
          _class = cl_loader.loadClass(name);
          stub_loader = cl_loader; 
        }

      } catch (ClassNotFoundException cl_not_found) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("P4ObjectBrokerServerImpl.loadLocalStub()", "A problem with loading stub class: " + name + " with loader of the interface : + " + cl_not_found.toString());
        }
        //If the loader that sees specified stub class is different from loader given as parameter
        try {
          if (isPermanentLoader) { //if this loader is different from loader as parameter
            class_loader = stubClass.getClassLoader();
            if (class_loader == null) {
              _class = Class.forName(name);
            } else {
              _class = class_loader.loadClass(name);
              stub_loader = class_loader; //Only if successfully loaded
            }
          }
        } catch (ClassNotFoundException cnfex) {
          if (P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("P4ObjectBrokerServerImpl.loadLocalStub()", "Cannot find or load generated stub class: " + name + " with loader of the interface : + " + cnfex.toString());
          }
          //If the loader was specified as permanent class-loader, but _Stub class cannot be loaded, 
          //neither with it, nor with the class-loader of specified stub class then as a last chance, 
          //try with context class-loader of current thread, if different from previous ones.
          try {
            threadLoader = Thread.currentThread().getContextClassLoader();
            if (threadLoader != null && threadLoader != class_loader && threadLoader != cl_loader) {
              _class = threadLoader.loadClass(name);
              stub_loader = threadLoader; //Only if successfully loaded
            }
          } catch (ClassNotFoundException cnfex_) {
            if (P4Logger.getLocation().bePath()) {
              P4Logger.getLocation().pathT("P4ObjectBrokerServerImpl.loadLocalStub()", "Cannot find or load generated stub class: " + name + " with the context class loader : + " + cnfex_.toString() + ". A proxy stub will be loaded instead");
            }
          }
        }
      }
      }

      if (_class == null) { //exceptions were thrown - not found generated stub - a proxy will be loaded
        P4RemoteObject delegateImplementation = getObject(info.key);
        AbstractInvocationHandler invocationHandler = new LocalInvocationHandler(info);


        Class[] cl = {stubClass, RemoteRef.class};
        ClassLoader loader = null;

        if (isPermanentLoader && (cl_loader != null)) {
          loader = cl_loader;
        } else {
          if (class_loader == null) {
            class_loader = stubClass.getClassLoader();
          }
          loader = class_loader;
        }

        Vector<Class> remoteInt = new Vector<Class>();
        Object proxy1 = null;
        Object proxy2 = null;
        int successLoad1 = 0;
        int successLoad2 = 0;

        /* try to make proxy with the stub's class loader */
        try {
          for (int i = 0; i < info.stubs.length; i++) {
            try {
              remoteInt.add(loader.loadClass(info.stubs[i]));
              successLoad1++;
            } catch (ClassNotFoundException cnfe) {// $JL-EXC$
              //Expected thrown exception if the class-loader is not suitable
              if (P4Logger.getLocation().beDebug()){
                P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.loadLocalStub()", "Evaluating classloader for local stub: cannot load interface: " + info.stubs[i] + " with classloader: " + loader);
              }
              continue;
            } catch (ThreadDeath td) { // $JL-EXC$
              throw td;
            } catch (VirtualMachineError vme) { //Includes OutOfMemoryError, StackOverflowError
              throw vme;
            } catch (Throwable th) { // $JL-EXC$
              //Shield from buggy custom class-loaders
              if (P4Logger.getLocation().beWarning()){
                P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerServerImpl.loadLocalStub()", "Loading class failed. Classloader {0} was unable to load class for local proxy stub: {1}", "ASJ.rmip4.rt1013", new Object []{loader, th.toString()});  
              }
            }
          }
          remoteInt.add(RemoteRef.class);
          if (!remoteInt.contains(Remote.class)) {
            remoteInt.add(Remote.class);
          }
          proxy1 = Proxy.newProxyInstance(loader, (Class[]) remoteInt.toArray(new Class[0]), invocationHandler);                      //1 try with this.loader
        } catch (Exception ex1) {
          P4Logger.getLocation().traceThrowableT(P4ExceptionConstants.SEVERITY_DEBUG, "loadLocalStub", ex1);
        }

        /* try to make proxy with the context class loader, if different */
        if (threadLoader == null) {
          threadLoader = Thread.currentThread().getContextClassLoader();
        }
        
        if (threadLoader != null && threadLoader != loader) {
          try {
            remoteInt.clear();
            for (int i = 0; i < info.stubs.length; i++) {
              try {
                remoteInt.add(threadLoader.loadClass(info.stubs[i]));
                successLoad2++;
              } catch (ClassNotFoundException cnfe) {// $JL-EXC$
                //Expected thrown exception if the class-loader is not suitable
                if (P4Logger.getLocation().beDebug()){
                  P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.loadLocalStub()", "Evaluating classloader for local stub: cannot load interface: " + info.stubs[i] + " with classloader: " + loader);
                }
                continue;
              } catch (ThreadDeath td) { // $JL-EXC$
                throw td;
              } catch (VirtualMachineError vme) { //Includes OutOfMemoryError, StackOverflowError
                throw vme;
              } catch (Throwable th) { // $JL-EXC$
                //Shield from buggy custom class-loaders
                if (P4Logger.getLocation().beWarning()){
                  P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerServerImpl.loadLocalStub()", "Loading class failed. Classloader {0} was unable to load class for local proxy stub: {1}", "ASJ.rmip4.rt1014", new Object []{loader, th.toString()});  
                }
              }
            }
            remoteInt.add(RemoteRef.class);
            if (!remoteInt.contains(Remote.class)) {
              remoteInt.add(Remote.class);
            }
            proxy2 = Proxy.newProxyInstance(threadLoader, remoteInt.toArray(new Class[0]), invocationHandler);
          } catch (Exception ex2) {
            P4Logger.getLocation().traceThrowableT(P4ExceptionConstants.SEVERITY_DEBUG, "loadLocalStub", ex2);
          }
        }

        if (P4Logger.getLocation().beWarning()){
          int successInterfaces = (successLoad1 >= successLoad2) ? successLoad1 : successLoad2;
          if (successInterfaces == 0){
            String loader_dc_str = null;
            if (loader!=null){
              loader_dc_str = P4Logger.getDcNameByClassLoader(loader);
            }
            P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerServerImpl.loadLocalStub()", "Loaded empty proxy stub. Proxy stub will fail on every remote call. Check context classloader", "ASJ.rmip4.rt1015", loader_dc_str, null);
          }
        }
        
        Object result = null;
        if ((proxy1 != null) && (proxy2 == null)) {
          result = proxy1;
        } else if ((proxy1 != null) && (proxy2 != null)) {
          result = (successLoad1 >= successLoad2) ? proxy1 : proxy2;
        } else if (proxy1 == null && proxy2 == null) {
          if (class_loader == null) {
            class_loader = stubClass.getClassLoader();
          }
          result = Proxy.newProxyInstance(class_loader, cl, invocationHandler);
        } else {
          result = proxy2;
        }

        if (delegateImplementation != null && delegateImplementation.getDelegate() != null) {
          javax.rmi.CORBA.Tie tie = javax.rmi.CORBA.Util.getTie(delegateImplementation.getDelegate());
          if (tie != null) {
            try {
              javax.rmi.CORBA.Util.registerTarget((javax.rmi.CORBA.Tie) tie.thisObject(), (Remote) result);
              delegateImplementation.newIIOPExportedStub(result);
              tie.setTarget(delegateImplementation.getDelegate());
            } catch (Exception e) {
              if (P4Logger.getLocation().beError()) {
                P4Logger.trace(P4Logger.ERROR, "P4ObjectBrokerServerImpl.loadLocalStub()", "Export stub to IIOP failed, check if IIOP service is started: {0}", "ASJ.rmip4.rt1016", new Object []{e.toString()});
                
                if (P4Logger.getLocation().beDebug()) {
                  P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.loadLocalStub()", "Exception: " + P4Logger.exceptionTrace(e));
                }
              }
            }
          }
        }

        return result;
      } else {
        StubImpl ret = null;
        try {
          ret = (StubImpl) _class.newInstance();
          // TODO newInstance() can throw ExceptionInInitializerError !!! 
          // TODO In case of Exception ret == null
        } catch (Exception ex) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.loadLocalStub()", P4Logger.exceptionTrace(ex));
          }
          if (P4Logger.getLocation().beError()) {
            P4Logger.trace(P4Logger.ERROR, "P4ObjectBrokerServerImpl.loadLocalStub()", "Instantiating local stub: {0} failed. Stub's class is not accessible", "ASJ.rmip4.rt1017", new Object []{_class});
        	  
          }
        }
        ret.isLocal = true;

        if (info.stubs == null) {
          info.stubs = new String[]{stubClass.getName()};
        }

        info.local_id = id;
        ret.p4_setInfo(info);
        ret.p4_setClassLoader(stub_loader);
        ret.p4remote = getObject(info.key);

        if (ret.p4remote != null && ret.p4remote.getDelegate() != null) {
          javax.rmi.CORBA.Tie tie = javax.rmi.CORBA.Util.getTie(ret.p4remote.getDelegate());
          if (tie != null) {
            try {
              javax.rmi.CORBA.Util.registerTarget((javax.rmi.CORBA.Tie) tie.thisObject(), (Remote) ret);
              ret.p4remote.newIIOPExportedStub(ret);
              tie.setTarget(ret.p4remote.getDelegate());
            } catch (Exception e) {
              if (P4Logger.getLocation().beWarning()) {
            	  P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerServerImpl.loadLocalStub()", "Export stub to IIOP failed, check if IIOP service is started: {0}", "ASJ.rmip4.rt1018", new Object []{P4Logger.exceptionTrace(e)});            	  
              }
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.loadLocalStub()", "Exception: " + P4Logger.exceptionTrace(e));
              }
              
            }
          }
        }
        return ret;
      }
    } else {
      return null; //TODO dali ne trjabva exception
    }
  }

  public void setInitialObject(String name, P4RemoteObject initObject) throws Exception {
    try {
      super.setInitialObject(name, initObject);
    } catch (Exception ex) {
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "P4ObjectBrokerServerImpl.setInitialObject(String, P4RemoteObject)", "Setting Initial Object: {0}:{1} failed. The object is not accessible", "ASJ.rmip4.rt1019", new Object[]{name,initObject});            	  
    	  
      }
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.setInitialObject(String, P4RemoteObject)", P4Logger.exceptionTrace(ex));
      }
    }
  }

  public int getId() {
//    if (sessionProcessor == null) {
//      sessionProcessor = P4SessionProcessor.getSessionProcessor();
//    }
//    thisServerId = sessionProcessor.getServerId();
//    return sessionProcessor.getServerId();
      return id;
  }

  public ConnectionProfile[] getConnectionProfiles() {
    if (sessionProcessor != null) {
      return sessionProcessor.getConnectionProfiles();
    } else {
      sessionProcessor = P4SessionProcessor.getSessionProcessor();
      thisServerId = sessionProcessor.getServerId();
    }
    return null;
  }
 //TODO
  public void inform(Object _identifer, Message _message) {
    if (_identifer instanceof LocalDispatch) {
      String identifer = "LocalDispatch@" + _identifer.hashCode();
      objManager.inform(identifer, _message.getUnmarshaledRequest());
    } else if (_identifer instanceof MSConnection) {
      objManager.inform(_message.sender_id + ":" + sessionProcessor.currentServerId, _message.getUnmarshaledRequest());
    } else if (_identifer instanceof com.sap.engine.interfaces.cross.Connection) {
      String id = Convert.byteArrToLong(((com.sap.engine.interfaces.cross.Connection) _identifer).getId(), 0) + ":" + sessionProcessor.currentServerId;
      objManager.inform(id, _message.getUnmarshaledRequest());
    }  else {
      String identifer = _message.clusterEl_id + ":" + _message.client_id;
      objManager.inform(identifer, _message.getUnmarshaledRequest());
    }
  }

  public void disposeConnection(Object _connection) {
    objManager.disposeConnection(_connection);
    if (_connection != null && _connection instanceof String) {
      String disposed = (String) _connection;
      int separatorPosition = disposed.indexOf(':');
      if (separatorPosition != -1) { //just in case of LocalDispatch@hashcode
        P4ObjectBroker.getBroker().removeForbiden( disposed.substring(0, separatorPosition).getBytes() );
      }
    } else {
      if (_connection != null && _connection instanceof com.sap.engine.interfaces.cross.Connection){
        P4ObjectBroker.getBroker().removeForbiden( ((com.sap.engine.interfaces.cross.Connection)_connection) );
      }
    }
  }

  public void runGarbageCollector() {
    // nothing
  }

  public P4RemoteObject loadObject(Remote remote) throws ClassNotFoundException {
    
    /* if this is a local stub/proxy, try to return direct a remote object */
    if (remote instanceof RemoteRef) {
      RemoteObjectInfo rInfo = ((RemoteRef) remote).getObjectInfo();
      if ((rInfo.ownerId == brokerId) && (rInfo.server_id == thisServerId)) {
        Object obj = getObject(rInfo.key);
        if (obj instanceof P4RemoteObject) {
          return (P4RemoteObject) obj;
        }
      } //else {
        //RemoteReference - stub or skeleton for another instance - do not load here. 
        //return null; //This null might cause problems. 
      //}
    }
    
    /* if this is a P4 skeleton (P4RemoteObject) - return itself after update URLs */  
    if (remote instanceof P4RemoteObject) {
      setURLList((P4RemoteObject) remote);
      return (P4RemoteObject) remote;
    }

    /* Try to find it in Portable Remote Objects table and return already exported object for it */
    P4RemoteObject obj = null;
    WeakReference weakRef = portableRemoteObjects.get(remote);

    if (weakRef != null) {
      obj = (P4RemoteObject) weakRef.get();

      if (obj != null) {
        byte[] key = obj.getObjectInfo().key;

        if ((obj = getObject(key)) != null) {
          setURLList(obj);
          return obj;
        }
      }
    }
    
    //Here P4 exports IIOP ties, but also all other proxies. Log can track it.
    if (remote instanceof java.lang.reflect.Proxy) { //may be ejb proxy of remote object
      obj = new P4DynamicSkeleton(remote);
    } else {
      if (P4ObjectBroker.isEnabledSkeletonClassCache()) {
        obj = fastGetOfGeneratedOrDynamicSkeleton(remote);
      } else {
        obj = oldGetOfGeneratedOrDynamicSkeleton(remote);
      }
    }

    if (remote instanceof RedirectableExt) {
      try {
        obj.info.isRedirectable = true;
        obj.info.setFactoryName(((RedirectableExt) remote).p4_objIdentity()._getFactoryName());
        obj.info.setObjIdentity(((RedirectableExt) remote).p4_objIdentity()._getObjectId());
      } catch (ClassCastException cce) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.loadObject(Remote)", "Oject Identity of Redirectable object should not be a remote object. Cannot instanctiate object identity for this RedirectableExt remote object: " + remote);
        }
      }
    } else if (remote instanceof Redirectable) {
      obj.info.isRedirectable = true;
      obj.info.redirIdent = ((Redirectable) remote).getIdentifier();
    }

    obj.setDelegate(remote);
    if (useReiterationOfGC) {
      P4ReferenceProxy remoteRef = new P4ReferenceProxy(remote);
      portableRemoteObjects.put(remoteRef, new WeakReference(obj));
    } else {
      portableRemoteObjects.put(remote, new WeakReference(obj));
    }

    setURLList(obj);
    return obj;
  }

  /**
   * The old style of generating skeleton without cache.
   * @param remote The object that have to be exported. 
   * @return The exported skeleton.
   */
  private P4RemoteObject oldGetOfGeneratedOrDynamicSkeleton(Remote remote) {
    P4RemoteObject obj = null;
    //Old way of exporting. No cache used for classes that we know if they have generated skeleton or not. Always check for generated skeleton
    Class remoteClass = remote.getClass();
    String remoteClassName = remoteClass.getName() + "p4_Skel";
    Class skeletonClass = null;
    try {
      skeletonClass = Class.forName(remoteClassName, true, remoteClass.getClassLoader());
      obj = (P4RemoteObject) skeletonClass.newInstance();
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.loadObject(Remote)", "Cannot load skeleton "+ remoteClassName + " : " + e.getMessage());
      }
      try {
        skeletonClass = getParentSkel(remoteClass, "p4_Skel");
        obj = (P4RemoteObject) skeletonClass.newInstance();
      } catch (Exception ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.loadObject(Remote)", "Cannot load parent skeleton "+ remoteClassName + " : " + ex.getMessage());
        }
        obj = new P4DynamicSkeleton(remote);
      }
    }
    return obj;
  }
  
  /**
   * New Style of exporting object. Object will be searched for 
   * generated skeleton only once. Afterwards we will find directly 
   * needed class to raise its instance or we will know that there is 
   * no generated skeleton to raise directly P4DynamicSkeleton.
   * @param remote The object that have to be exported. 
   * @return The exported skeleton.
   */
  private P4RemoteObject newGetOfGenerateOrDynamicSkeleton(Remote remote) {
    //Skeleton object for this class will be loaded for the first time!!!
    //Cache for skeleton's class is enabled. Here we will cache if there is generated skeleton for this class or not 
    P4RemoteObject obj = null;
    Class remoteClass = remote.getClass();
    String remoteClassName = remoteClass.getName() + "p4_Skel";
    Class skeletonClass = null;
    try {
      skeletonClass = Class.forName(remoteClassName, true, remoteClass.getClassLoader());
      obj = (P4RemoteObject) skeletonClass.newInstance();
      skeletonClassCache.put(remoteClassName, new WeakReference(skeletonClass));
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.loadObject(Remote)", "Cannot load skeleton "+ remoteClassName + " : " + e.getMessage());
      }
      try {
        skeletonClass = getParentSkel(remoteClass, "p4_Skel");
        obj = (P4RemoteObject) skeletonClass.newInstance();
        skeletonClassCache.put(remoteClassName, new WeakReference(skeletonClass));
      } catch (Exception ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.loadObject(Remote)", "Cannot load parent skeleton "+ remoteClassName + " : " + ex.getMessage());
        }
        obj = new P4DynamicSkeleton(remote);
        skeletonClassCache.put(remoteClassName, new WeakReference(DYNAMIC_SKELETON));
      }
    }
    return obj;
  }

  /**
   * This method extracts getting of generated or dynamic P4 skeleton from loadObject method.
   * Cache will improve performance in case of often bind of remote object or often return of 
   * object from certain class, which should be exported in P4.
   * @param remote Remote object that have to be exported as generated skeleton or dynamic skeleton.
   * @return The exported in P4 skeleton.
   */
  private P4RemoteObject fastGetOfGeneratedOrDynamicSkeleton(Remote remote) {
    Class remoteClass = remote.getClass();
    String remoteClassName = remoteClass.getName() + "p4_Skel";
    
    //Search the class for this skeleton first in cache
    //If we haven't such key or value is null - process as if this object is exported for the first time.
    if (!skeletonClassCache.containsKey(remoteClassName)) {
      return newGetOfGenerateOrDynamicSkeleton(remote);
    }
    
    WeakReference ref = skeletonClassCache.get(remoteClassName);
    if (ref == null || ref.get() == null) {
      if (ref != null) {
        skeletonClassCache.remove(ref); //For instance if exported service is stopped
      }
      return newGetOfGenerateOrDynamicSkeleton(remote);
    }
    
    //As we are here we have cached class for generated skeleton or cached sting that shows we should raise DynamicSkeleton.
    //This object was already exported for sure and we have a valid value for it.
    Object storedReference = ref.get();
    if (storedReference.equals(DYNAMIC_SKELETON)) {
       return new P4DynamicSkeleton(remote);
    }

    //We have generated skeleton and its class is our stored in cache reference
    P4RemoteObject obj = null;
    try {
      Class skeletonClass = (Class) storedReference;
      obj = (P4RemoteObject) skeletonClass.newInstance();
    } catch (Exception e) {
       //Should not be the case here. Problem with raising generated skeleton, 
       //that we raised successfully in the past. 
       P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerServerImpl.loadObject()", "Cannot instanciate class {0}. Export of generated skeleton failed. P4 will try ti raise DynamicSkeleton. Check if application or service connected with this class is running", "ASJ.rmip4.rt1038", new Object []{storedReference.toString()});
       obj = new P4DynamicSkeleton(remote);
    }
    return obj;
  }

  public void clear(Object key) {
    portableRemoteObjects.remove(key);
  }

  public Hashtable getPortableROsCopy() {
    synchronized (portableRemoteObjects) {
      return new Hashtable<Object, WeakReference>(portableRemoteObjects);
    }
  }

  public void disconnect(P4RemoteObject _object) {
    if (_object.delegate != null) {
      if (!(_object.delegate instanceof P4RemoteObject)) {
        clear(_object.delegate);
      }
    }

    super.disconnect(_object);
  }

  public Object getCrossInterface() {
    return P4ServiceFrame.crossInterface;
  }

  /**
   * This method returns the ClientThreadContext. 
   * NOTE: It can return null for system threads.
   */
  public ClientThreadContext getCTC() {
    if (sessionProcessor != null) {
      return sessionProcessor.getServiceContext().getCoreContext().getThreadSystem().getThreadContext();
    } else {
      sessionProcessor = P4SessionProcessor.getSessionProcessor();
      thisServerId = sessionProcessor.getServerId();
      return sessionProcessor.getServiceContext().getCoreContext().getThreadSystem().getThreadContext();
    }
  }

  public String getAttachedThreadMonitoringInfo() {
    return ThreadWrapper.getSubTaskName();
  }

  public void attachThreadMonitoringInfo(String arg) {
    ThreadWrapper.setSubTaskName(arg);
  }

  public StubBaseInfo stringToObject(String objectRef) {
    if (!objectRef.startsWith("clusteraloc")) {
      return super.stringToObject(objectRef);
    }

    P4Schema schema = new P4Schema(objectRef);
    MessageAnswer answer = null;
    if (sessionProcessor != null) {
      try {
        answer = sessionProcessor.organizer.messageContext.sendAndWaitForAnswer(schema.getClusterId(), MessageConstants.OBJECT_REQUEST, schema.getObjKey().getBytes(), 0, schema.getObjKey().length(), 10000); // servers communication - between 2 servers
      } catch (ClusterException _ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.stringToObject(String)", P4Logger.exceptionTrace(_ex));
        }
      }
      if (answer == null) {
        return null;
      }
      byte[] buf = new byte[answer.getLength()];
      System.arraycopy(answer.getMessage(), answer.getOffset(), buf, 0, buf.length);
      return byteArrayToObject(buf);
    } else {
      sessionProcessor = P4SessionProcessor.getSessionProcessor();
      thisServerId = sessionProcessor.getServerId();
    }
    return null;
  }

  public void setExecContextState(Object o) {

    //deprecated
  }

  public Object applyExecContextState(Object pro) {
    //  deprecated
    return null;
  }

  public void setExecContext(Object o) {
    //  deprecated
  }

  public String[] getHttp() {
    return http;
  }

  public Exception getException(int type, String msgText, Throwable th) {
    return getException(type, msgText, th, null);
  }

  public Exception getException(int type, String msgText, Throwable th, Object[] args) {
    switch (type) {
      case P4_IOException:
        {
          return new P4BaseIOException(msgText, args, th);
        }
      case P4_ConnectionException:
        {
          return new P4BaseConnectionException(msgText, args, th);
        }
      case Initialize_Exception:
        {
          return new InitializingException(msgText, th);
        }
      case P4_MarshalException:
        {
          return new P4BaseMarshalException(msgText, args, th);
        }
      case P4_RuntimeException:
      default:
        return new P4BaseRuntimeException(msgText, args, th);
    }
  }

  public byte[] getInitialObject(String name) throws java.rmi.RemoteException {
    if (initObjects.containsKey(name)) {
      P4RemoteObject initObject = (P4RemoteObject) initObjects.get(name);
      RemoteObjectInfo info = initObject.getObjectInfo();
      info.connectionProfiles = getConnectionProfiles();
      byte[] r = makeObjectRef(info.connectionProfiles, info.server_id, info.key);
      return r;
    } else {
      ClusterElement[] ce = sessionProcessor.organizer.clusterContext.getClusterMonitor().getParticipants();
      for (int i = 0; i < ce.length; i++) {
        if (ce[i].getType() == ClusterElement.SERVER) {
          int id = ce[i].getClusterId();
          if (sessionProcessor != null) {
            try {
              MessageAnswer answer = sessionProcessor.organizer.messageContext.sendAndWaitForAnswer(id, MessageConstants.OBJECT_REQUEST, name.getBytes(), 0, name.length(), 10000);

              if (answer.getLength() > 0) {
                byte[] buf = new byte[answer.getLength()];
                System.arraycopy(answer.getMessage(), answer.getOffset(), buf, 0, answer.getLength());
                return buf;
              }
            } catch (ClusterException _ex) {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.getInitialObject(String)", P4Logger.exceptionTrace(_ex));
              }
            }

          }
        }
      }
      throw new java.rmi.NoSuchObjectException("ID:011781 Object: <" + name + ">");
    }
  }


  /* (non-Javadoc)
   * @see com.sap.engine.services.rmi_p4.P4ObjectBroker#getServiceContext()
   */
  public ApplicationServiceContext getServiceContext() {
    return sessionProcessor.serviceContext;
  }

  public boolean isServerBroker() {
    return true;
  }

  public com.sap.engine.interfaces.cross.RemoteBroker getRemoteBroker(String host, int port, Properties properties) throws Exception {
    Connection connection = getInitialConnection(host, port, properties);
    if (connection.isLocal()) {
      if(properties != null) {
         String dsid = (String)properties.get("DestinationServerId");
          if (dsid != null) {
            int serverId = Integer.parseInt(dsid);
            if (serverId != thisServerId || serverId != 0) {
                connection = sessionProcessor.getConnection(serverId);
            }
          }
       }
    }
    return new RemoteBroker(connection, host, port, properties);
  }

  public ConnectionProvider getConnectionProvider() {
    return null;  //Todo
  }

  static void setEnabledStreamHooks(boolean enabledStreamHooks) {
    P4ObjectBroker.enabledStreamHooks = enabledStreamHooks;
  }

  //TODO
  private Connection getInitialConnection(String host, int port, Properties properties) throws IOException {
    String connectionType = (String)properties.get("TransportLayerQueue");
    if (connectionType == null) {
      connectionType = DEFAULT_COMMUNICATION_LAYER;
    }
    //Change localhost to real host if possible
    if (host.equals(LOCALHOST) || host.equals(IPv4_LOCALHOST) || host.equals(IPv6_LOCALHOST)) {
      host = changeLocalHost(host);
    }
    return sessionProcessor.getMessageProcessor().getConnection(connectionType, host, port, true);
  }

  /**
   * This method transforms "localhost" to real host that can perform remote communication.
   * @param host The original given host.
   * @return
   */
  private String changeLocalHost(String host) {
    String hostName = null;
    try {
      host = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException unknownHost) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.changeLocalHost()", "Error in getting host address " + host + "\r\n" + P4Logger.exceptionTrace(unknownHost));
      }
      try {
        String preferIPv4 = System.getProperty("java.net.preferIPv4Stack");
        String preferIPv6 = System.getProperty("java.net.preferIPv6Addresse");
        boolean preferredIPv6 = preferIPv6 != null && preferIPv6.equals("true") || preferIPv4 != null && preferIPv4.equals("false");
        hostName = IPv4_LOCALHOST;
        if (preferredIPv6){
          hostName = IPv6_LOCALHOST;
        }
        InetAddress.getByName(hostName).getHostAddress();
        
      } catch (UnknownHostException unHost) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ObjectBrokerServerImpl.changeLocalHost()", "Error in getting local host address " + hostName + "\r\n" + P4Logger.exceptionTrace(unHost));
        }
        
        try {
          if (hostName.equals(IPv4_LOCALHOST)) {
            hostName = IPv6_LOCALHOST;
          } else {
            hostName = IPv4_LOCALHOST;
          }
          host = InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException unIPv6Host) {
          if (P4Logger.getLocation().beWarning()) {
            P4Logger.trace(P4Logger.WARNING, "P4ObjectBrokerServerImpl.changeLocalHost()", "Getting local host address failed. \r\n {0}", "ASJ.rmip4.rt1020", new Object []{P4Logger.exceptionTrace(unHost)});            	  
          }
        }
      }
    }
    return host;
  }
  
   //TODO
   public Object resolveInitialReference(String name, Connection repliable, int destServerId) throws IOException {
      if (repliable.isLocal()) {
        if ((destServerId == 0) || (destServerId == getId())) {
          return this.byteArrayToObject(getInitialObject(name));
        }
      }
      int nameLength = name.length();
      InitialCall call = InitialCall.getInitialCall(repliable, name);
      byte[] message = new byte[ProtocolHeader.THREAD_CONTEXT_SIZE + 2 * nameLength];
      call.writeId(message, ProtocolHeader.HEADER_SIZE);
      message[ProtocolHeader.MESSAGE_TYPE] = 10;
      ProtocolHeader.writeHeader(message, 0, message.length, destServerId);
      Convert.writeUStringToByteArr(message, ProtocolHeader.THREAD_CONTEXT_SIZE, name);
      repliable.sendRequest(message, message.length, call);
      byte[] res = null;
      try {
        res = call.getResult(null);
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
      if ((destServerId != 0) && (destServerId != -1) && (stubInfo.server_id != destServerId)) {
        throw new java.rmi.RemoteException("Server with ID " + destServerId + " does not exist");
      }
      StringBuffer _id = new StringBuffer(String.valueOf(stubInfo.ownerId));
      _id.append(":");
      _id.append(repliable.getUnderlyingProfile());
      String ids = _id.toString();
      stubInfo.setIncomingProfile(ids);
      initialProfiles = stubInfo.connectionProfiles;
      return stubInfo;
   }

   /**
    * Receive a Call object and return the destination host for this call.
    * Used in Call and InitialCall for better message destination description
    * in thread's sub-tasks in MMC.
    * This method override client implementation, adding cluster call handling.
    * @param call The Call object, we search host for.
    * @return The host and port of this call in format host:port for non cluster calls;
    *         and format "server node [server id] on host:port" for cluster calls.
    */
   @Override
   public String getRmtHost(P4Call call){
     ConnectionProperties cp = call.repliable.getProperties();
     if (cp != null) {
       return cp.getRemoteAddress() + ":" + cp.getRemotePort() + " ";
     }

     int cluster_id = call.getDestServerId();
     if (sessionProcessor == null || cluster_id==0 || cluster_id == getId()){
       return "server node " + cluster_id;
     }

     ClusterElement server = sessionProcessor.organizer.clusterContext.getClusterMonitor().getParticipant(cluster_id);
     String serverName = "unknown host"; //Will be set to host from InetAddress from getAddress().
     if (server != null) {
       InetAddress address = server.getAddress();
       if (address != null) {
         serverName = address.getHostAddress();
       }
     }
     if (P4Logger.getLocation().bePath()) {
       P4Logger.getLocation().pathT("getRemoteHost(P4Call)", "RMI call to server node " + cluster_id + " on " + serverName + " in cluster");
     }
     return "server node " + cluster_id + " on " + serverName + " ";
   }

   /**
    * Returns if the local stubs are allowed or not.
    * @return If instantiate of local stub stub is allowed or not.
    */
   public boolean localStubsAllowed() {
     return P4ServiceFrame.localStubsAllowed();
   }

   /**
    * Overrides dummy implementation for client environment to productive implementation for server environment. 
    * @param tag The name of transaction for detailed following
    * @param dcAccountedClass Class, whose DC will be used for measure of this transaction. 
    *                         Use EJB's generated skeleton to account statistics to EJB component, JMS' class to account statistics to JMS component. 
    *                         This way P4 will have pure P4 time, without including time for Remote Implementation. 
    */
   public void beginMeasure(String tag, Class dcAccountedClass){
     if (broker.isEnabledAccounting() && Accounting.isEnabled()) {
       Accounting.beginMeasure(tag, dcAccountedClass);
     }
   }
   
   /**
    * Overrides dummy implementation for client environment to productive implementation for server environment.
    * @param tag The name of transaction that ends after invocation of method {@link #beginMeasure(String, Class)} 
    */
   public void endMeasure(String tag){
     if (broker.isEnabledAccounting() && Accounting.isEnabled()) {
       Accounting.endMeasure(tag);
     }
   }
   
   /**
    * Used for monitoring of current configuration of P4 service regarding omit searching of generated 
    * stubs. Returns the status of searching for generated stubs.
    * @return true  if generated stubs are allowed for searching and generation; and
    *         false if generated stubs are omitted for searching and always proxy is raised. 
    */
   public static boolean getInstantiateGeneratedLocalStubs() {
     return instantiateGeneratedLocalStubs;
   }
   
   /**
    * This method is used by P4ServiceFrame to set flag in broker regarding service property INSTANTIATE_GENERATED_LOCAL_STUBS.
    * Use the property in configuration if you need to change this  
    * @param flag The boolean flag for enable/disable the searching for generated local stubs.
    */
   protected static void setInstantiateGeneratedLocalStubs(boolean flag) {
     instantiateGeneratedLocalStubs = flag;
   }
}