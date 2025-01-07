/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.services.iiop.CORBA.IOR;
import com.sap.engine.services.iiop.CORBA.TSIdentificationImpl;
import com.sap.engine.services.iiop.CORBA.GIOPMessageConstants;
import com.sap.engine.services.iiop.CORBA.interceptors.ORBInitInfoImpl;
import com.sap.engine.services.iiop.CORBA.portable.DelegateImpl;
import com.sap.engine.services.iiop.internal.util.IDFactory;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.server.CorbaServiceFrame;
import com.sap.engine.services.iiop.system.CommunicationLayer;
import org.omg.CORBA.TSIdentification;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.PortableInterceptor.ORBInitializer;

import javax.jts.TransactionService;
import javax.naming.NamingException;
import java.util.Properties;

public class ORB extends ClientORB {

  public ORB() {
    super(null);
    LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ORB.ORB()", "Web AS ORB, multiple server threads.");

    if (idFac == null) {
      idFac = new IDFactory();
    }
  }

  //made accessibility public because JLIN tests that forbid .setAccesible method in our ORBProxy and ORBSingeltonProxy implementations
  public void set_parameters(String[] args, Properties props) {
    if (props == null) {
      props = CorbaServiceFrame.getDefaultProperties();
    } else {
      props = cutProperties(props);
    }

    if (args != null) {
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
      ORBInitInfoImpl orbInitInfo = new ORBInitInfoImpl(this, args, orbId, codecFactory, false);
      preInitORBInitializers(orbInitInfo, orbInitializers);
      postInitORBInitializers(orbInitInfo, orbInitializers);
    }
    isInitialized = true;
  }

  public void set_(String[] args, Properties props) {
//    props = mergeProperties(props);
    try {
      javax.naming.Context ctx = new javax.naming.InitialContext();
      TransactionService ts = (TransactionService) ctx.lookup("TransactionService");
      TSIdentification tsIdentification = new TSIdentificationImpl();
      ts.identifyORB(this, tsIdentification, new Properties());
      //      ctx.rebind("^/TSIdentification",  tsIdentification);
    } catch (NamingException nex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ORB.set_(String[], Properties)", LoggerConfigurator.exceptionTrace(nex));
      }
    }

    String cn = props.getProperty("CommunicationLayerClass");
    communicationLayer = CommunicationLayer.init(cn, this);

    if (props.getProperty("IDFactoryUnitSize") != null && props.getProperty("IDFactoryMaxUnits") != null) {
      int newUnitSize = Integer.parseInt(props.getProperty("IDFactoryUnitSize"));
      int newMaxUnits = Integer.parseInt(props.getProperty("IDFactoryMaxUnits"));
      idFac.resize(newUnitSize, newMaxUnits);
    }

    isInitialized = false;
    ORBInitializer[] orbInitializers = registerORBInitializers(SystemProperties.getProperties());
    if (orbInitializers != null) {
      ORBInitInfoImpl orbInitInfo = new ORBInitInfoImpl(this, args, orbId, codecFactory, true);
      preInitORBInitializers(orbInitInfo, orbInitializers);
      postInitORBInitializers(orbInitInfo, orbInitializers);
    }
    isInitialized = true;
  }

  /**
   *  Gets the <code>IDFactory</code> for this class. It's initialized in the constructor and
   *  can be resized by calling <code>set_()</code>.
   *  @return <code>IDFactory</code> attached to this <code>ORB</code>
   */
  public static IDFactory getIDFactory() {
    return idFac;
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
      } catch (ArrayIndexOutOfBoundsException e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beInfo()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).infoT("ORB.connect(org.omg.CORBA.Object)", "There is no connection URL available.");
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).infoT("IIOP Service access point is not started on ICM.");
        }
        // IIOP not started on ICM
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("ORB.connect(org.omg.CORBA.Object)", LoggerConfigurator.exceptionTrace(e));
        }
      }
      boolean loopForKey = true;
      while (loopForKey) {
        byte[] objkey = getObjectKey(CorbaServiceFrame.getClusterId());
        IOR ior = new IOR(this, ids[0], host, port, objkey, GIOPMessageConstants.VERSION_MAJOR, (versionMinor != 1) ? versionMinor : GIOPMessageConstants.VERSION_MINOR);
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
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("ORB.connect(org.omg.CORBA.Object)", LoggerConfigurator.exceptionTrace(e));
      }
    }
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beDebug()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).debugT("ORB.connect(org.omg.CORBA.Object)", "Connection of the servant object to the ORB passed successfully");
    }
  }


  public boolean isServerORB() {
    return true;
  }

  protected void initCosNaming() {
    if (cosNaming == null) {
      cosNaming = new com.sap.engine.services.jndi.cosnaming.NamingContextExtImpl();
      namingServant = new NameServiceHolder((InvokeHandler) cosNaming);
      connect(cosNaming);
    }
  }


  public void shutdown(boolean com) {
    super.shutdown(com);
    //idFac = null;
  }

  public int getClusterId() {
    return CorbaServiceFrame.getClusterId();
  }
}

