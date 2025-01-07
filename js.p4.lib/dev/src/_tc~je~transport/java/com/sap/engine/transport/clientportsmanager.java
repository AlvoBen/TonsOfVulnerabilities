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
package com.sap.engine.transport;

/**
 * public class ClientPortsManager is used by client to open
 * Sockets and ServerSocket on layers
 *
 * @author  Nickolay Neychev
 */
import java.io.*;
import java.net.*;
import java.util.*;
import com.sap.engine.frame.cluster.transport.TransportFactory;

public class ClientPortsManager {

  Hashtable propertyData;
  //  ClientPortsManager thisCPM = null;
  public ClientPortsManager() {
    //    thisCPM = this;
    propertyData = new Hashtable();
    propertyData.put("ssl", "com.sap.engine.services.ssl.factory.SSLTransportFactory");
    propertyData.put("SSL", "com.sap.engine.services.ssl.factory.SSLTransportFactory");
    propertyData.put("https", "com.sap.engine.services.ssl.factory.SSLHttpTransportFactory");
    propertyData.put("HTTPS", "com.sap.engine.services.ssl.factory.SSLHttpTransportFactory");
    propertyData.put("SAPRouter","com.sap.engine.services.rmi_p4.SAPTransportFactory");
    propertyData.put("saprouter","com.sap.engine.services.rmi_p4.SAPTransportFactory");
  }

  private TransportFactory getFactory(String queue) throws Exception {

    if (queue.endsWith("/")) {
      queue = queue.substring(0, queue.length() - 1);
    }

    queue.trim();

    if (queue.length() > 0) {
      int lastIndex = queue.lastIndexOf('/');
      String current = null;

      if (lastIndex == -1) {
        current = queue;
        queue = "";
      } else {
        current = queue.substring(lastIndex + 1);
        queue = queue.substring(0, lastIndex);
      }
      try {
        Class cl = Class.forName((String) propertyData.get(current));
        TransportFactory result = (TransportFactory) cl.newInstance();
        result.setFactory(getFactory(queue));
        return result;
      } catch (NoClassDefFoundError ncdfe) {
        throw new IOException("Cannot create socket factory for transport " + current + ". NoClassDefFoundError: " + ncdfe.getMessage());
      }
    } else {
      Class cl = Class.forName("com.sap.engine.transport.DefaultTransportFactory");
      TransportFactory result = (TransportFactory) cl.newInstance();
      return result;
    }
  }

  public ServerSocket getRealServerSocket(int port, String queue) throws IOException {
    try {
      return getFactory(queue).getServerSocket(port);
    } catch (Exception e) {
      throw new IOException("Can't get ServerSocket. Reason:" + e.getMessage());
    }
  }

  public ServerSocket getServerSocket(int port, String queue, String type) throws IOException {
    try {
      Class cl = Class.forName((String) propertyData.get(type));
      TransportFactory factory = (TransportFactory) cl.newInstance();
      factory.setFactory(getFactory(queue));
      return factory.getServerSocket(port);
    } catch (NoClassDefFoundError ncdfe) {
      throw new IOException("Cannot create socket factory for transport " + type + ". NoClassDefFoundError: " + ncdfe.getMessage());
    } catch (Exception e) {
      throw new IOException("Can't get ServerSocket. Reason:" + e.getMessage());
    }
  }

  public ServerSocket getRealServerSocket(int port, int acceptSize, String queue) throws IOException {
    try {
      return getFactory(queue).getServerSocket(port, acceptSize);
    } catch (Exception e) {
      throw new IOException("Can't get ServerSocket. Reason:" + e.getMessage());
    }
  }

  public ServerSocket getServerSocket(int port, int acceptSize, String queue, String type) throws IOException {
    try {
      Class cl = Class.forName((String) propertyData.get(type));
      TransportFactory factory = (TransportFactory) cl.newInstance();
      factory.setFactory(getFactory(queue));
      return factory.getServerSocket(port);
    } catch (NoClassDefFoundError ncdfe) {
      throw new IOException("Cannot create socket factory for transport " + type + ". NoClassDefFoundError: " + ncdfe.getMessage());
    } catch (Exception e) {
      throw new IOException("Can't get ServerSocket. Reason:" + e.getMessage());
    }
  }

  public Socket getRealSocket(String host, int port, String queue) throws IOException {
    try {
      return getFactory(queue).getSocket(host, port);
    } catch (Exception e) {
      throw new IOException("Can't get ServerSocket. Reason:" + e.getMessage());
    }
  }

  public Socket getSocket(String host, int port, String queue, String type) throws IOException {
    try {
      Class cl = Class.forName((String) propertyData.get(type));
      TransportFactory factory = (TransportFactory) cl.newInstance();
      factory.setFactory(getFactory(queue));
      return factory.getSocket(host, port);
    } catch (NoClassDefFoundError ncdfe) {
       throw new IOException("Cannot create socket factory for transport " + type + ". NoClassDefFoundError: " + ncdfe.getMessage());
    } catch (Exception e) {
      throw new IOException("Can't get Socket. Reason:" + e.getMessage());
    }
  }

  public Socket getRealSocket(String host, int port, String queue, Properties props) throws IOException {
    try {     
        return getFactory(queue).getSocket(host, port, props);
    } catch (Exception e) {
       throw new IOException("Can't get Socket. Reason:" + e.getMessage());
    }
  }

  public Socket getSocket(String host, int port, String queue, String type, Properties props) throws IOException {
    try {
      Class cl = Class.forName((String) propertyData.get(type));
      TransportFactory factory = (TransportFactory) cl.newInstance();
      factory.setFactory(getFactory(queue));
      return factory.getSocket(host, port, props);
    } catch (NoClassDefFoundError ncdfe) {
      throw new IOException("Cannot create socket factory for transport " + type + ". NoClassDefFoundError: " + ncdfe.getMessage());
    } catch (Exception e) {
       throw new IOException("Can't get Socket. Reason:" + e.getMessage());
    }
  }

}

