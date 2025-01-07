/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.iiop.internal;

import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.iiop.CORBA.*;
import com.sap.engine.services.iiop.CORBA.interceptors.ORBInitInfoImpl;
import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;
import com.sap.engine.services.iiop.CORBA.portable.CORBAOutputStream;
import com.sap.engine.services.iiop.CORBA.portable.DelegateImpl;
import com.sap.engine.services.iiop.internal.portable.IIOPOutputStream;
import com.sap.engine.services.iiop.internal.store.ConnectionTableHash;
import com.sap.engine.services.iiop.internal.store.KeyObject;
import com.sap.engine.services.iiop.internal.util.IDFactory;
import com.sap.engine.services.iiop.internal.interceptors.InterceptorsStorage;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.system.CommunicationLayer;
import com.sap.engine.services.iiop.PortableServer.ServantDelegate;
import com.sap.engine.services.iiop.server.CorbaServiceFrame;
import com.sap.engine.frame.client.ClientFactory;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.frame.core.thread.ClientThreadContext;
import org.omg.CORBA.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.Interceptor;

import java.util.*;
import java.lang.Object;

/*

 * @author Ivan Atanassov, Nikolai Neichev
 * @version 4.0
 */

public class ClientORB extends com.sap.engine.services.iiop.CORBA.ORB {

  public static final String ROOT_POA_NAME = "RootPOA";
  public static final String NAME_SERVICE_NAME = "NameService";
  public static final String NAME_SERVICE_LOCAL = "NameServiceLocal";
  public static final String PICURRENT_NAME = "PICurrent";
  public static final String CODEC_FACTORY_NAME = "CodecFactory";
  private static String CORBA_OBJECT = "IDL:omg.org/CORBA/Object:1.0";
  protected static String orbId = "";

  public static CommunicationLayer communicationLayer;

  static org.omg.CosNaming.NamingContext cosNaming = null;
  static NameServiceHolder namingServant;
  protected static IDFactory idFac = new IDFactory();

  protected static ConnectionTableHash table = null;
  protected static CodecFactory codecFactory = null;
  private static int corr;

  private HashMap<String, Object> initialReferenceTable = null;

  byte versionMinor = GIOPMessageConstants.VERSION_MINOR;

  public static final String appclientCommunicationLayerClass = "com.sap.engine.services.iiop.client.CommunicationLayerImpl";


  public ClientORB() {
    this(appclientCommunicationLayerClass);
  }

  public ClientORB(String communicationLayerClass) {
    if (table == null) {
      table = new ConnectionTableHash();
    }

    if (codecFactory == null) {
      codecFactory = new CodecFactoryImpl(this);
    }

    if (initialReferenceTable == null) {
      initialReferenceTable = new HashMap<String, Object>(3);
      initialReferenceTable.put(PICURRENT_NAME, null);
      initialReferenceTable.put(CODEC_FACTORY_NAME, codecFactory);
      initialReferenceTable.put(ROOT_POA_NAME, rootPOA);
    }

    if (ClientORB.communicationLayer == null && communicationLayerClass != null) {
      ClientORB.communicationLayer = CommunicationLayer.init(communicationLayerClass, this);

      Properties props = System.getProperties();
      String providerClass = props.getProperty("sslsocket.provider.class");
      String providerParam = props.getProperty("sslsocket.provider.param");
      String providerPass = props.getProperty("sslsocket.provider.password");
      LoggerConfigurator.setType(LoggerConfigurator.TYPE_CLIENT);
      if (providerClass != null) {
        if (providerParam != null) {
          communicationLayer.setSSLProvider(providerClass, new String[]{providerParam, providerPass});
        } else {
          communicationLayer.setSSLProvider(providerClass, null);
        }
      }

      if (props != null) {
        String s1 = props.getProperty("org.omg.CORBA.ORBInitialPort");
        if (s1 != null) {
          try {
            initialPort = Integer.parseInt(s1);
          } catch (NumberFormatException numberformatexception) {
            initialPort = defaultPort;
          }
        }
        s1 = props.getProperty("org.omg.CORBA.ORBInitialHost");
        if (s1 != null) {
          initialHost = s1;
        }
        s1 = props.getProperty("GIOPVersionMinor", "2");
        versionMinor = Byte.parseByte(s1);
      }

      isInitialized = false;
      ORBInitializer[] orbInitializers = registerORBInitializers(props);
      if (orbInitializers != null) {
        ORBInitInfoImpl orbInitInfo = new ORBInitInfoImpl(this, new String[0], orbId, codecFactory, true);
        preInitORBInitializers(orbInitInfo, orbInitializers);
        postInitORBInitializers(orbInitInfo, orbInitializers);
      }
      isInitialized = true;
    }
  }

  protected void set_parameters(String[] args, Properties props) {
    if (args == null) {
      args = new String[0];
    }
    props = cutProperties(props);
    String providerClass = props.getProperty("sslsocket.provider.class");
    String providerParam = props.getProperty("sslsocket.provider.param");
    String providerPass = props.getProperty("sslsocket.provider.password");
    LoggerConfigurator.setType(LoggerConfigurator.TYPE_CLIENT);

    if (providerClass != null) {
      if (providerParam != null) {
        communicationLayer.setSSLProvider(providerClass, new String[]{providerParam, providerPass});
      } else {
        communicationLayer.setSSLProvider(providerClass, null);
      }
    }

    int i = 0;
    while (i < args.length) {
      if (args[i].equals(initialArguments[0])) {
        try {
          initialPort = Integer.parseInt(args[++i]);
        } catch (NumberFormatException e) {
          initialPort = defaultPort;
        }
      } else if (args[i].equals(initialArguments[1])) {
        initialHost = args[++i];
      } else {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("Invalid startup parameter: " + args[i]);
        }
      }
      ++i;
    }

    if (props != null) {
      String s1 = props.getProperty("org.omg.CORBA.ORBInitialPort");
      if (s1 != null) {
        try {
          initialPort = Integer.parseInt(s1);
        } catch (NumberFormatException numberformatexception) {
          initialPort = defaultPort;
        }
      }
      s1 = props.getProperty("org.omg.CORBA.ORBInitialHost");
      if (s1 != null) {
        initialHost = s1;
      }
      s1 = props.getProperty("GIOPVersionMinor", "2");
      versionMinor = Byte.parseByte(s1);
    }

    isInitialized = false;
    ORBInitializer[] orbInitializers = registerORBInitializers(props);
    if (orbInitializers != null) {
      ORBInitInfoImpl orbInitInfo = new ORBInitInfoImpl(this, new String[0], orbId, codecFactory, false);
      preInitORBInitializers(orbInitInfo, orbInitializers);
      postInitORBInitializers(orbInitInfo, orbInitializers);
    }
    isInitialized = true;
  }

  public void set_(String[] args, Properties props) {
    if (props.getProperty("IDFactoryUnitSize") != null && props.getProperty("IDFactoryMaxUnits") != null) {
      int newUnitSize = Integer.parseInt(props.getProperty("IDFactoryUnitSize"));
      int newMaxUnits = Integer.parseInt(props.getProperty("IDFactoryMaxUnits"));
      idFac.resize(newUnitSize, newMaxUnits);
    }
  }

  public CommunicationLayer getCommLayer() {
    return communicationLayer;
  }

  public void connect(org.omg.CORBA.Object obj) {
    ObjectImpl oi = (ObjectImpl) obj;
    // check if this object is already connected
    try {
      DelegateImpl di = (DelegateImpl) oi._get_delegate();
      if (di.isConnected()) {
        return;
      }
    } catch (Exception e) {
      //$JL-EXC$ Here is catched BAD_OPERATION Exception thrown in _get_delegate() if delegate was not be set
    }

    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beInfo()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).infoT("ClientORB.connect(org.omg.CORBA.Object)", " Object: " + obj);
    }
    String[] ids = oi._ids();
    try {
      String host = "localhost";
      int port = 3333;
      try {
        String url = communicationLayer.getURL()[0];
        int idx = url.indexOf("@") + 1;
        int idy = url.lastIndexOf(":");
        if (idy < 0) {
          host = url.substring(idx);
        } else {
          host = url.substring(idx, idy);
          port = Integer.parseInt(url.substring(idy + 1));
        }
      } catch (ArrayIndexOutOfBoundsException aiobe) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beInfo()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).infoT("ClientORB.connect(org.omg.CORBA.Object)", "There is no connection URL available.");
        }
        // IIOP not started on ICM
      }
      boolean loopForKey = true;
      while (loopForKey) {
        byte[] objkey = getObjectKey(0);
        IOR ior = new IOR(this, ids[0], host, port, objkey, GIOPMessageConstants.VERSION_MAJOR, (versionMinor != 1) ? versionMinor : GIOPMessageConstants.VERSION_MINOR);  //TODO Vancho I do not remember why is this check
        DelegateImpl d = new DelegateImpl(ior);
        oi._set_delegate(d);
        try {
          table.put(objkey, obj);
          d.setConnected(true);
          loopForKey = false;
        } catch (IllegalArgumentException e) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).bePath()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).pathT("ClientORB.connect(org.omg.CORBA.Object)", "Duplicate key generated in connect: " + e);
          }
        }
      }
    } catch (NumberFormatException e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("ClientORB.connect(org.omg.CORBA.Object)", LoggerConfigurator.exceptionTrace(e));
      }
    }
  }

    /**
   * Destroys the ORB so that its resources can be reclaimed.
   * Any operation invoked on a destroyed ORB reference will throw the
   * <code>OBJECT_NOT_EXIST</code> exception.
   * Once an ORB has been destroyed, another call to <code>init</code>
   * with the same ORBid will return a reference to a newly constructed ORB.<p>
   * If <code>destroy</code> is called on an ORB that has not been shut down,
   * it will start the shut down process and block until the ORB has shut down
   * before it destroys the ORB.<br>
   * If an application calls <code>destroy</code> in a thread that is currently servicing
   * an invocation, the <code>BAD_INV_ORDER</code> system exception will be thrown
   * with the OMG minor code 3, since blocking would result in a deadlock.<p>
   * For maximum portability and to avoid resource leaks, an application should
   * always call <code>shutdown</code> and <code>destroy</code>
   * on all ORB instances before exiting.
   *
   * @throws org.omg.CORBA.BAD_INV_ORDER if the current thread is servicing an invocation
   */
  public void destroy( ) {
    if (status == STATUS_OPERATING) {
      shutdown(true);
    } else if (status == STATUS_DESTROYED) {
      throw new OBJECT_NOT_EXIST();
    }

    IORInterceptor[] ior_interceptors = InterceptorsStorage.getIORInterceptors(this);
    for (int i = 0; i < ior_interceptors.length; i++) {
      ior_interceptors[i].destroy();
    }
    Interceptor[] client_interceptors = InterceptorsStorage.getClientInterceptors(this);
    for (int i = 0; i < client_interceptors.length; i++) {
      client_interceptors[i].destroy();
    }
    Interceptor[] server_interceptors = InterceptorsStorage.getServerInterceptors(this);
    for (int i = 0; i < server_interceptors.length; i++) {
      server_interceptors[i].destroy();
    }

    status = STATUS_DESTROYED;
  }

  byte[] getObjectKey(int serverId) {
    byte[] objkey = new byte[12];
    Convert.writeIntToByteArr(objkey, 0, serverId);
    //get time
    long time = System.currentTimeMillis();

    synchronized (this) {
      objkey[4] = (byte) corr++; //only 256 Keys  per mls !!!!!!!!!!!!!!!!
    }
    for (int i = 0; i < 7; i++) {
      objkey[i + 5] = (byte) (((time >> (i * 8))) & 0xffL);
    }

    return objkey;
  }

  public void disconnect(org.omg.CORBA.Object obj) {
    table.delete(obj);
  }

  public org.omg.CORBA.Object getObject(byte[] bKey) {
    return table.get(bKey);
  }

  public TargetHolder getServant(byte[] bKey) {
    return table.getServant(bKey);
  }

  public String[] list_initial_services() {
    Set<String> init_services_set = initialReferenceTable.keySet();
    String[] init_services = new String[init_services_set.size() + 1];
    init_services_set.toArray(init_services);
    init_services[init_services_set.size()] = NAME_SERVICE_NAME;
    return init_services;
  }

  public org.omg.CORBA.Object resolve_initial_references(String object_name) throws InvalidName {
    return resolve_initial_references(object_name, (byte) 1, (byte) 0, initialHost, initialPort);
  }

  private org.omg.CORBA.Object resolve_initial_references(String object_name, byte majorVersion, byte minorVersion, String host, int port) throws InvalidName {
    org.omg.CORBA.Object result = null;

    if ((NAME_SERVICE_LOCAL).equals(object_name)) {
      result = getCosNaming();
    } else if (initialReferenceTable.containsKey(object_name)) {
      result = (org.omg.CORBA.Object) initialReferenceTable.get(object_name);
      if (object_name.equals(PICURRENT_NAME) ) {
        if (currentThreadIsSystemThread()) {
          result = null;
        } else if (result == null) {
          piCurrent = getPICurrent();
          initialReferenceTable.put(object_name, piCurrent);
          result = piCurrent;
        }
      }

    } else {
      switch (minorVersion) {
        case 0:
          {
            byte[] obj_key = "INIT".getBytes();
            IOR ior = new IOR(this, CORBA_OBJECT, host, port, obj_key, majorVersion, minorVersion);
            Delegate delegate = communicationLayer.getDelegate(ior);
            org.omg.CORBA.Object corbaobject = new CORBAObject(ior);
            org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) delegate.request(corbaobject, "get", true);
            out.write_string(object_name); //obj name
            org.omg.CORBA_2_3.portable.InputStream inputStream = null;
            try {
              inputStream = (org.omg.CORBA_2_3.portable.InputStream) delegate.invoke(corbaobject, out);
            } catch (ApplicationException e) {
              // never will be thrown
              if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beDebug()) {
                LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).debugT("ClientORB.resolve_initial_references(String, byte, byte, String, int)", LoggerConfigurator.exceptionTrace(e));
              }
              inputStream = (org.omg.CORBA_2_3.portable.InputStream) e.getInputStream();
              String id = inputStream.read_string();
              throw new RuntimeException(id);
            } catch (RemarshalException e) {
              if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beDebug()) {
                LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).debugT("ClientORB.resolve_initial_references(String, byte, byte, String, int)", LoggerConfigurator.exceptionTrace(e));
              }
              return resolve_initial_references(object_name, majorVersion, minorVersion, host, port);
            }
            result = inputStream.read_Object();
            break;
          }
        case 1:
        case 2:
          {
            IOR ior = new IOR(this, CORBA_OBJECT, host, port, object_name.getBytes(), majorVersion, minorVersion);
            result = new CORBAObject(ior);
            //        this.connect(result);
          }
      }
    }

    if (result != null) {
      return result;
    } else {
      throw new InvalidName(object_name);
    }
  }

  public void register_initial_reference(String id, org.omg.CORBA.Object obj) throws InvalidName {
    if ((id == null) || (id.length() == 0)) {
      throw new InvalidName("Invalid ID - null or empty string");
    }

    java.lang.Object initial_ref = initialReferenceTable.get(id);
    if (initial_ref != null) {
      throw new InvalidName(id + " already registered");
    }

    initialReferenceTable.put(id, obj);
  }

  public static IDFactory getIDFactory() {
    return idFac;
  }

  private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  private String toHex(byte[] ba, int offset, int length) {
    char[] buf = new char[length * 2];
    int j = 0;
    int k;
    for (int i = offset; i < offset + length; i++) {
      k = ba[i];
      buf[j++] = hexDigits[(k >>> 4) & 0x0F];
      buf[j++] = hexDigits[k & 0x0F];
    }
    return new String(buf);
  }

  public String object_to_string(org.omg.CORBA.Object obj) {
    try {
      IOR ior = null;
      if (obj == null) {
        ior = IOR.NULL_IOR(this);
      } else {
        ObjectImpl oi = (ObjectImpl) obj;
        try {
          ior = ((DelegateImpl) (oi._get_delegate())).getIOR();
        } catch (Exception e) {
          connect(obj);
          ior = ((DelegateImpl) (oi._get_delegate())).getIOR();
        }
      }
      CORBAOutputStream os = new CORBAOutputStream(this, 512);
      os.write_boolean(os.getEndian());
      ior.write_object(os);
      String sResult = toHex(os.toByteArray_forSend(), 0, os.byteArray_forSend_length());
      sResult = "IOR:" + sResult;
      return sResult;
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("ClientORB.object_to_string(org.omg.CORBA.Object)", LoggerConfigurator.exceptionTrace(e));
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("Object to string for " + obj + " failed." + LoggerConfigurator.exceptionTrace(e));
      }
      return null;
    }
  }// abstract

  private byte getByteFromHex(String s, int off) {
    int i = 0;
    char ch = s.charAt(off);
    if (ch >= '0' && ch <= '9') {
      i = (ch - '0') & 0x0F;
    } else if (ch >= 'A' && ch <= 'Z') {
      i = (ch - 'A' + 10) & 0x0F;
    } else if (ch >= 'a' && ch <= 'z') {
      i = (ch - 'a' + 10) & 0x0F;
    } else {
      throw new org.omg.CORBA.DATA_CONVERSION("ID019089: Bad input parameter. Invalid hex string");
    }
    i <<= 4;
    ch = s.charAt(off + 1);
    if (ch >= '0' && ch <= '9') {
      i |= ((ch - '0') & 0x0F);
    } else if (ch >= 'A' && ch <= 'Z') {
      i |= ((ch - 'A' + 10) & 0x0F);
    } else if (ch >= 'a' && ch <= 'z') {
      i |= ((ch - 'a' + 10) & 0x0F);
    } else {
      throw new org.omg.CORBA.DATA_CONVERSION("ID019090: Bad input parameter. Invalid hex string");
    }
    byte bRes = (byte) (i & 0xFF);
    return bRes;
  } // getByteFromHex()

  static String ORB_Host_Property = "org.omg.CORBA.ORBInitialHost";
  static String ORB_Port_Property = "org.omg.CORBA.ORBInitialPort";

  private org.omg.CORBA.Object string_to_object_Addr(String addr, String keyString) {
    String host = "";
    String port = "";
    byte majorVersion = 1;
    byte minorVersion = 0;
    if(!addr.startsWith(":")) {
        StringTokenizer tokenizer = new StringTokenizer(addr, ":");
        String token = tokenizer.nextToken(); // <obj_addr_list>
        // <prot_addr>
        if (token.equals("rir")) { // resolve initial reference identifier
          org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
          if (keyString.length() == 0) {
            keyString = NAME_SERVICE_NAME;
          }
          try {
            return orb.resolve_initial_references(keyString);
          } catch (Exception e) {
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("ClientORB.string_to_object_Addr(String, String)", LoggerConfigurator.exceptionTrace(e));
            }
            throw new org.omg.CORBA.BAD_PARAM("Bad resolve initial reference address: " + e.getMessage(), 9, CompletionStatus.COMPLETED_NO);
          }
        } else if (token.equals("iiop")) { // <iiop_prot_addr>
           addr = addr.substring(5);
        } else {
          throw new org.omg.CORBA.BAD_PARAM("string_to_object() failed - bad URL: " + addr, 10, CompletionStatus.COMPLETED_NO);
        }
    } else {
      addr = addr.substring(1);
    }
     int verIndex =addr.indexOf('@');
      if (verIndex > -1) { // version included
        int dotIndex = addr.indexOf('.');
        majorVersion = (byte) Integer.parseInt(addr.substring(0, dotIndex));
        minorVersion = (byte) Integer.parseInt(addr.substring(dotIndex + 1, verIndex));
        host = addr.substring(verIndex + 1);
      } else { // no version
        host = addr;
      }
      port = "900";//default

      //check if it looks like ipv6 url
      if (host.charAt(0) == '[') {
        //seems like ipv6 url e.g. [fec0::1]:50007
        int ipv6end = host.indexOf(']');
        if (ipv6end == -1) {
          throw new org.omg.CORBA.BAD_PARAM("string_to_object() failed - bad URL: " + addr, 10, CompletionStatus.COMPLETED_NO);
        }
        //check if there is a :port at the end
        if ((host.length() > ipv6end + 2) && (host.charAt(ipv6end+1) == ':')) {
          port = host.substring(ipv6end + 2);
        }
        host = host.substring(1, ipv6end);
      } else {
        // ipv4 or a hostname
        int colonIndex = host.indexOf(':');
        if (colonIndex != -1) {
          port = host.substring(colonIndex + 1);
          host = host.substring(0, colonIndex);
        }
      }

    try {
      return resolve_initial_references(keyString, majorVersion, minorVersion, host, Integer.parseInt(port));
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("ClientORB.string_to_object_Addr(String, String)", LoggerConfigurator.exceptionTrace(e));
      }
      throw new org.omg.CORBA.BAD_PARAM("string_to_object() failed: " + e.getMessage(), 10, CompletionStatus.COMPLETED_NO);
    }
  }

  public org.omg.CORBA.Object string_to_object(String str) {
    if (str == null) {
      throw new org.omg.CORBA.BAD_PARAM("ID019091: Null input parameter - require valid String");
    }
    if (str.startsWith("IOR:")) {
      byte[] b_tmp = new byte[(str.length() - 4) / 2];
      int off = 4;
      for (int i = 0; i < b_tmp.length; i++) {
        b_tmp[i] = getByteFromHex(str, off);
        off += 2;
      }
      CORBAInputStream is = new CORBAInputStream(this, b_tmp);
      is.setEndian(is.read_boolean());

      IOR ior = new IOR(this, is);
      if (ior.is_nil()) {
        return null;
      } else {
        return new CORBAObject(ior);
      }
    } else if (str.startsWith("corbaloc:")) { // 9 chars
      int slashInd = str.indexOf('/');
      String keyString = str.substring(slashInd + 1);
      String obj_adds = str.substring(9, slashInd);
      int komaIndex = obj_adds.indexOf(',');
      if (komaIndex == -1) { // only one addres
        return string_to_object_Addr(obj_adds, keyString);
      } else { // more addresses separated by ','
        if (obj_adds.indexOf("rir") > -1) { // use only this
          StringTokenizer addToken = new StringTokenizer(obj_adds, ",");
          while (addToken.hasMoreTokens()) {
            String addr = addToken.nextToken();
            if (addr.startsWith("rir")) {
              return string_to_object_Addr(addr, keyString);
            }
          }
        } else {
          StringTokenizer addToken = new StringTokenizer(obj_adds, ",");
          while (addToken.hasMoreTokens()) {
            try {
              return string_to_object_Addr(addToken.nextToken(), keyString);
            } catch (Exception e) {
              if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
                LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("ClientORB.string_to_object(String)", LoggerConfigurator.exceptionTrace(e));
              }
              continue;
            }
//            throw new org.omg.CORBA.BAD_PARAM("PROBLEM : " + str, 10, CompletionStatus.COMPLETED_NO);
          }
        }
      }
      throw new org.omg.CORBA.BAD_PARAM("Bad address: " + str, 8, CompletionStatus.COMPLETED_NO);
    } else {
      throw new org.omg.CORBA.BAD_PARAM("Bad schema name: " + str, 7, CompletionStatus.COMPLETED_NO);
    }
  }

  public org.omg.CORBA.portable.OutputStream create_output_stream() {
    return new IIOPOutputStream(this);
  }

  public Delegate getDelegate(IOR ior) {
    return communicationLayer.getDelegate(ior);
  }

  public void shutdown(boolean com) {
    //communicationLayer = null;
    //table = null;
    //codecFactory = null;
//    time = 0;
//    last_time = 0;
//    corr = 0;
    super.shutdown(com);
  }

  ORBInitializer[] registerORBInitializers(Properties props) {
    String orbInitPrefix = "org.omg.PortableInterceptor.ORBInitializerClass."; //ORBConstants.PI_ORB_INITIALIZER_CLASS_PREFIX;
    Enumeration propertyNames = props.propertyNames();
    ArrayList<ORBInitializer> initializerList = new ArrayList<ORBInitializer>();
    while (propertyNames.hasMoreElements()) {
      String propertyName = (String) propertyNames.nextElement();
      if (propertyName.startsWith(orbInitPrefix)) {
        String initClassName = propertyName.substring(orbInitPrefix.length());
        try {
          //          Class initClass = ORBClassLoader.loadClass(initClassName);
          Class initClass = null;
          try {
            initClass = Class.forName(initClassName);
          } catch (Exception ex) {
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beDebug()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).debugT("ClientORB.registerORBInitializers(Properties)", LoggerConfigurator.exceptionTrace(ex));
            }
            ClassLoader tcxLoder = Thread.currentThread().getContextClassLoader();
            initClass = Class.forName(initClassName, true, tcxLoder);
          } catch (NoClassDefFoundError ncdf) {
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beDebug()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).debugT("ClientORB.registerORBInitializers(Properties)", LoggerConfigurator.exceptionTrace(ncdf));
            }
            ClassLoader tcxLoder = Thread.currentThread().getContextClassLoader();
            initClass = Class.forName(initClassName, true, tcxLoder);
          }
          if (ORBInitializer.class.isAssignableFrom(initClass)) {
            if (initClass != null) {
              ORBInitializer initializer = (ORBInitializer) initClass.newInstance();
              initializerList.add(initializer);
            }
          }
        } catch (Exception e) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("ClientORB.registerORBInitializers(Properties)", LoggerConfigurator.exceptionTrace(e));
          }
          // As per ptc/00-08-06, section 21.7.3.1., "If there
          // are any exceptions the ORB shall ignore them and
          // proceed."
          continue;
        }
      }
    }

    if (initializerList.size() > 0) {
      return (ORBInitializer[]) initializerList.toArray(new ORBInitializer[0]);
    } else {
      return null;
    }
  }

  void preInitORBInitializers(ORBInitInfoImpl info, ORBInitializer[] orbInitializers) {
    info.setStage(ORBInitInfoImpl.STAGE_PRE_INIT);
    for (int i = 0; i < orbInitializers.length; i++) {
      ORBInitializer init = orbInitializers[i];
      if (init != null) {
        try {
          init.pre_init(info);
        } catch (Exception e) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("ClientORB.preInitORBInitializers(ORBInitInfoImpl, ORBInitializer[])", LoggerConfigurator.exceptionTrace(e));
          }
          // As per orbos/99-12-02, section 9.3.1.2, "If there are
          // any exceptions, the ORB shall ignore them and proceed."
          continue;
        }
      }
    }
  }

  void postInitORBInitializers(ORBInitInfoImpl info, ORBInitializer[] orbInitializers) {
    info.setStage(ORBInitInfoImpl.STAGE_POST_INIT);
    for (int i = 0; i < orbInitializers.length; i++) {
      ORBInitializer init = orbInitializers[i];
      if (init != null) {
        try {
          init.post_init(info);
        } catch (Exception e) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("ClientORB.postInitORBInitializers(ORBInitInfoImpl, ORBInitializer[])", LoggerConfigurator.exceptionTrace(e));
          }
          // As per orbos/99-12-02, section 9.3.1.2, "If there are
          // any exceptions, the ORB shall ignore them and proceed."
          continue;
        }
      }
    }
  }

  public boolean is_local(byte[] key) {
    if (table.get(key) != null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Gets the object count
   * @return The object count
   */
  public int getObjectCount() {
    return table.getSize();
  }

  public NameServiceHolder getNamingServant() {
    initCosNaming();
    return namingServant;
  }

  public boolean isServerORB() {
    return false;
  }

  protected org.omg.CosNaming.NamingContext getCosNaming() {
    initCosNaming();
    return cosNaming;
  }

  protected void initCosNaming() {
  }

  // only properties not described as system
  protected Properties cutProperties(Properties props) {
    if (props != null) {
      Properties sysProps = System.getProperties();
      Properties mergeProps = (Properties) props.clone();

      Enumeration names = sysProps.propertyNames();
      while (names.hasMoreElements()) {
        String name = (String) names.nextElement();
        mergeProps.remove(name);
      }
      return mergeProps;
    } else {
      return new Properties();
    }
  }

  public void dropdown() {
    idFac = null;
    communicationLayer = null;
    cosNaming = null;
    namingServant = null;
    table = null;
    codecFactory = null;
    corr = 0;
    piCurrent = null;
  }

  public int getClusterId() {
    return 0;
  }

  public void set_delegate(java.lang.Object servant) {
    ((org.omg.PortableServer.Servant) servant)
            ._set_delegate(new ServantDelegate(this));
  }


  private boolean currentThreadIsSystemThread() {
    boolean result = false;

    try {
      if (isServerORB()) {
        ThreadContext context = CorbaServiceFrame.getThreadSystem().getThreadContext();
        if (context == null) {
          result = true;
        }
      } else {
        ClientThreadContext clientContext = ClientFactory.getThreadContextFactory().getThreadContext();
        if (clientContext == null) {
          result = true;
        }
      }
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beInfo()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).infoT("ClientORB.currentThreadIsSystemThread()", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return result;
  }

  public Hashtable<KeyObject, TargetHolder> getExportedObjects() {
    return table.getObjects();
  }
}