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
package com.sap.engine.services.iiop.server;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.event.ClusterEventListener;
import com.sap.engine.frame.cluster.message.*;
import com.sap.engine.interfaces.cross.CrossInterface;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.interfaces.cross.MessageProcessor;
import com.sap.engine.interfaces.cross.FCAConnector;
import com.sap.engine.lib.lang.ConvertTools;
import com.sap.engine.lib.util.ConcurrentHashMapIntObject;
import com.sap.engine.services.iiop.CORBA.IOR;
import com.sap.engine.services.iiop.CORBA.Profile;
import com.sap.engine.services.iiop.core.MessageConstants;
import com.sap.engine.services.iiop.csiv2.CSIIOP.*;
import com.sap.engine.services.iiop.csiv2.EJBIORGenerator;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.server.portable.*;
import com.sap.engine.services.iiop.system.CommunicationLayer;
import com.sap.bc.proj.jstartup.sadm.ShmAccessPoint;
import com.sap.bc.proj.jstartup.sadm.ShmException;

import java.io.IOException;

public class CommunicationLayerImpl extends CommunicationLayer implements MessageConstants, MessageListener, ClusterEventListener {

  public static ApplicationServiceContext context;
  private CrossInterface crossInterface;
  public ConcurrentHashMapIntObject clusterElements = new ConcurrentHashMapIntObject();
  public ConcurrentHashMapIntObject clusterSSLElements = new ConcurrentHashMapIntObject();
  public ConvertTools ct = new ConvertTools(false);
  private static final Object so = new Object();
  public static IIOPProvider broker;
  private EJBIORGenerator ejbIORGenerator = null;

  public CommunicationLayerImpl() {
    synchronized (so) {
      while (CommunicationLayerImpl.context == null) {
        try {
          so.wait();
        } catch (InterruptedException e) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CommunicationLayerImpl.CommunicationLayerImpl()", LoggerConfigurator.exceptionTrace(e));
          }
        }
      }
    }

    try {
      context.getServiceState().registerClusterEventListener(this);
    } catch(ListenerAlreadyRegisteredException e ) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CommunicationLayerImpl.CommunicationLayerImpl()", e.toString());
      }
    }

    //TODO it is dead code. It's used unly bacause the ICM do not receive event for iiop start without this code. This bug must be repared.
    try {
      context.getClusterContext().getMessageContext().registerListener(this);
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CommunicationLayerImpl.CommunicationLayerImpl()", LoggerConfigurator.exceptionTrace(ex));
      }
    }

  }

  /**
   * This method sets the service context and releases any <code>CommunicationLayerImpls</code> that are waiting in their constructors.
   *
   * @param  ctx  The service context.
   */
  protected static void setContext(ApplicationServiceContext ctx) {
    context = ctx;
    synchronized (so) {
      so.notifyAll();
    }
  }

  protected void registerProvider(CrossInterface crossInterface) {
    this.crossInterface = crossInterface;

    if (broker == null) {
          broker = new IIOPProvider(context);
    }
    crossInterface.registerProtocolProvider(broker);
  }

  protected void stop() {
    if (broker != null) {
      crossInterface.unregisterProtocolProvider(broker.getName());
      broker = null;
    }

    try {
      context.getServiceState().unregisterClusterEventListener();
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CommunicationLayerImpl.stop()", LoggerConfigurator.exceptionTrace(ex));
      }
    }

    //TODO it is dead code. It's used unly bacause the ICM do not receive event for iiop start without this code. This bug must be repared.
    try {
      context.getClusterContext().getMessageContext().unregisterListener();
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CommunicationLayerImpl.stop()", LoggerConfigurator.exceptionTrace(ex));
      }
    }

    thisLayer = null;
    context = null;
  }

  /**
   * Removes the current context.
   *
   */
  protected static void removeContext() {
    context = null;
  }

  /**
   * This method returns all URLs
   *
   * @return    String[] that conians a list of ICM URLs
   */
  public String[] getURL() {
    try {
      ShmAccessPoint[] icm_urls = ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_IIOP);
      ShmAccessPoint[] icm_urls_ssl = ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_IIOPS);
      int length = 0;
      if (icm_urls != null) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("Found IIOP urls : " + icm_urls.length);
        }
        length += icm_urls.length;
      }
      if (icm_urls_ssl != null) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("Found IIOP(ssl) urls : " + icm_urls_ssl.length);
        }
        length += icm_urls_ssl.length;
      }
      String[] all_urls = new String[length];
      int resIndex = 0;
      if (icm_urls != null) {
        for (ShmAccessPoint anIcm_urls : icm_urls) {
          String host = anIcm_urls.getAddress().getHostAddress();
          if (!host.equals("127.0.0.1") && !host.equals("0.0.0.0")) { // not a ICM URL
            int port = anIcm_urls.getPort();
            String url = "@" + host + ":" + port;
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("Adding IIOP url : " + url);
            }
            all_urls[resIndex] = url;
            resIndex++;
          }
        }
      }
        // ssl urls
      if (icm_urls_ssl != null) {
        for (ShmAccessPoint anIcm_urls_ssl : icm_urls_ssl) {
          String host = anIcm_urls_ssl.getAddress().getHostAddress();
          if (!host.equals("127.0.0.1") && !host.equals("0.0.0.0")) { // not a ICM URL
            int port = anIcm_urls_ssl.getPort();
            String url = "@" + host + ":" + port;
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("Adding IIOP(ssl) url : " + url);
            }
            all_urls[resIndex] = url;
            resIndex++;
          }
        }
      }
      String[] result = new String[resIndex];
      System.arraycopy(all_urls, 0, result, 0, result.length);

      if (result.length == 0) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).bePath()) {
           LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).pathT("CommunicationLayerImpl.getURL()", "IIOP urls not found in shared memory, please check if the IIOP service of ICM is active.");
        }
      }
      return result;
    } catch (ShmException shme) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
         LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CommunicationLayerImpl.getURL()", LoggerConfigurator.exceptionTrace(shme));
      }
      return new String[0];
    }
  }

  /**
   * This method returns all SSL URLs
   *
   * @return    String[] that conians a list of ICM  URLs
   */
  public String[] getURL_SSL() {
    try {
      ShmAccessPoint[] icm_urls_ssl = ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_IIOPS);
      int length = 0;
      if (icm_urls_ssl != null) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("Found IIOP(ssl) urls : " + icm_urls_ssl.length);
        }
        length += icm_urls_ssl.length;
      }
      String[] all_urls = new String[length];
      int resIndex = 0;
        // ssl urls
      if (icm_urls_ssl != null) {
        for (ShmAccessPoint anIcm_ssl_url : icm_urls_ssl) {
          String host = anIcm_ssl_url.getAddress().getHostAddress();
          if (!host.equals("127.0.0.1") && !host.equals("0.0.0.0")) { // not a dispatcher URL
            int port = anIcm_ssl_url.getPort();
            String url = "@" + host + ":" + port;
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("Adding IIOP(ssl) url : " + url);
            }
            all_urls[resIndex] = url;
            resIndex++;
          }
        }
      }
      String[] result = new String[resIndex];
      System.arraycopy(all_urls, 0, result, 0, result.length);

      if (result.length == 0) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).bePath()) {
           LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).pathT("CommunicationLayerImpl.getURL()", "IIOP SSL urls not found in shared memory, please check if the IIOPSEC service of ICM is active.");
        }
      }
      return result;
    } catch (ShmException shme) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
         LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CommunicationLayerImpl.getURL()", LoggerConfigurator.exceptionTrace(shme));
      }
      return new String[0];
    }
  }

    //ClusterEventListener methods

  public void elementJoin(ClusterElement clusterElement) {
    if ((clusterElement.getType() == ClusterElement.ICM) && (ejbIORGenerator != null)) {
      ejbIORGenerator.resetHostAndPort(getSSLAddress());
    }
  }

  public void elementLoss(ClusterElement clusterElement) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void elementStateChanged(ClusterElement clusterElement, byte b) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   *
   *
   * @param  ior
   */
  public org.omg.CORBA.portable.Delegate getDelegate(IOR ior) {
    Connection connection = getConnection(ior);

    int versionMinor = ior.getProfile().getVersionMinor();
    Delegate delegate;

    switch (versionMinor) {
      case 0: {
        delegate = new Delegate_1_0(ior);
        break;
      }
      case 1: {
        delegate = new Delegate_1_1(ior);
        break;
      }
      case 2: {
        delegate = new Delegate_1_2(ior);
        break;
      }
      default: {
        delegate = new Delegate_1_0(ior);
      }
    }

    delegate.setConnection(connection);
    delegate.sender = this;
    return delegate;
  }


  public Connection getConnection(IOR ior) {
    Profile prof = ior.getProfile();
    byte[] host;
    int port;
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("CommunicationLayerImpl.getDelegate(IOR)", "Request from " + prof.getHost() + " : " + prof.getPort()+ " IOR:"+ior);
    }
    boolean useSSL = (prof.getPort() == 0);

    if (useSSL) {
      TransportAddress sslAddress = parseSSLTransportAddress(ior.getORB(), prof);
      host = sslAddress.host_name.getBytes();
      port = sslAddress.port;
    } else {
      host = prof.getHost().getBytes();
      port = prof.getPort();
    }

    Connection connection = null;
    try {
      if (useSSL) {
        connection = broker.getMessageProcessor().getConnector().openConnection((byte) MessageProcessor.IIOP_PROCESSOR, (byte) FCAConnector.TRANSPORT_SSL, port, new String(host), false);
      } else {
        connection = broker.getMessageProcessor().getConnector().openConnection((byte) MessageProcessor.IIOP_PROCESSOR, (byte) FCAConnector.TRANSPORT_PLAIN, port, new String(host), false);
      }
    } catch (Exception ioe) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("CommunicationLayerImpl.getConnection(IOR)", LoggerConfigurator.exceptionTrace(ioe));
      }
    }

    return connection;
  }


  public int openSSLServerSocket(int port) throws IOException {
    Object[] sslAddress = getSSLAddress();
    if (sslAddress != null) {
      int sslPort = (Integer) sslAddress[1];
      return sslPort;
    } else {
      return 0;
    }
  }

  public static ApplicationServiceContext getServiceContext() {
    return context;
  }



  public void setEJBIORGenerator(EJBIORGenerator ejbIORGenerator) {
    this.ejbIORGenerator = ejbIORGenerator;
    this.ejbIORGenerator.resetHostAndPort(getSSLAddress());
  }

  public Object[] getSSLAddress() {
    String[] sslUrls = getURL_SSL();
    if (sslUrls == null || sslUrls.length == 0) {
      return null;
    } else {
      String addr = sslUrls[0];
      int sslPort = 0;
      try {
        sslPort = Integer.parseInt(addr.substring(addr.indexOf(':') + 1));
      } catch (NumberFormatException nfe) {
        return null;
      }
      if (sslPort == 0) {
        return null;
      }

      Object[] sslAddress = new Object[2];
      sslAddress[0] = addr.substring(1, addr.indexOf(':'));
      sslAddress[1] = sslPort;

      return sslAddress;
    }
  }

    //TODO it is dead code. It's used unly bacause the ICM do not receive event for iiop start without this code. This bug must be repared.
  public void receive(int i, int i1, byte[] bytes, int i2, int i3) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  //TODO it is dead code. It's used unly bacause the ICM do not receive event for iiop start without this code. This bug must be repared.
  public MessageAnswer receiveWait(int i, int i1, byte[] bytes, int i2, int i3) throws Exception {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

}