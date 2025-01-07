/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.jmx.remote;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.management.MBeanServerConnection;

/**
 * A factory to obtain connections to remote MBean servers.
 * 
 * @author d025700
 */
public class JmxConnectionFactory {
  private static final String JMX_PREFIX = "service:jmx:"; //$NON-NLS-1$
  private static final String PACKAGE_ENGINE_P4 = "com.sap.engine.services.jmx.connector.p4"; //$NON-NLS-1$
  private static final String PACKAGE_ENGINE_LOCAL = "com.sap.engine.services.jmx.connector.local"; //$NON-NLS-1$
  private static final String PACKAGE_ENGINE_N2N = "com.sap.engine.services.jmx.connector.n2n"; //$NON-NLS-1$
  private static final String FACTORY_CLASS = "ConnectorFactory"; //$NON-NLS-1$
  private static final String CLASS_ENGINE_P4 = PACKAGE_ENGINE_P4 + "." + FACTORY_CLASS; //$NON-NLS-1$
  private static final String CLASS_ENGINE_LOCAL = PACKAGE_ENGINE_LOCAL + "." + FACTORY_CLASS; //$NON-NLS-1$
  private static final String CLASS_ENGINE_N2N = PACKAGE_ENGINE_N2N + "." + FACTORY_CLASS; //$NON-NLS-1$

  /** 
   * Used as a key in the connection properties hashtable of getMBeanServerConnection(...)
   * or getConnector(...)
   * to speciy the class loader to be used when de-serializing return values and exceptions
   * in the MBeanServerConnection.
   */
  public static final String STREAM_CLASS_LOADER = "StreamClassLoader"; //$NON-NLS-1$
  /**
   * Used as a key in the connection properties hashtable of getMBeanServerConnection(...)
   * or getConnector(...) to specify the class loader to be used to load the ConnectorFactory
   * for the protocol specified by the given address.
   */
  public static final String CONNECTOR_FACTORY_CLASS_LOADER = "ConnectorFactoryClassLoader"; //$NON-NLS-1$
  /**
   * Address of a RMI-P4 connection. Additional parameters specifying the connection 
   * (host, port, user, password, ...) have to be provided as parameters in a Hashtable.
   * The syntax of the parametes is the same like for getting a javax.naming.InitialContext.
   */
  public static final String PROTOCOL_ENGINE_P4 = JMX_PREFIX + PACKAGE_ENGINE_P4 + ":"; //$NON-NLS-1$
  /**
   * Address of a VM local connection.
   */
  public static final String PROTOCOL_ENGINE_LOCAL = JMX_PREFIX + PACKAGE_ENGINE_LOCAL + ":"; //$NON-NLS-1$
  /**
   * Address prefix of a node to node connection inside a J2EE cluster. It has to 
   * be concatenated with a valid cluster node ID of the target node.
   */
  public static final String PROTOCOL_ENGINE_N2N = JMX_PREFIX + PACKAGE_ENGINE_N2N + ":"; //$NON-NLS-1$

  private static final Hashtable EMPTY_TABLE = new Hashtable();
  private static JmxConnectorFactory localConnectorFactory;
  private static JmxConnectorFactory p4ConnectorFactory;
  private static JmxConnectorFactory n2nConnectorFactory;

  /**
   * Creates a MBeanServerConnection corresponding to the specified server address.
   * Uses the current thread context class loader to de-serialization parameters.
   *
   * @param address the URL of the connector
   * @param props connector specific parameters required to make a connection
   * @return JmxConnector the connector specified by the URL
   * @throws IOException connector could not be created
   */
  public static MBeanServerConnection getMBeanServerConnection(String address) throws IOException {
    return getConnector(address, null).getMBeanServerConnection();
  }

  /**
   * Creates a MBeanServerConnection corresponding to the specified connector address.
   * Uses the current thread context class loader to de-serialization parameters.
   *
   * @param address the URL of the connector
   * @param props connector specific parameters required to make a connection
   * @return JmxConnector the connector specified by the URL
   * @throws IOException connector could not be created
   */
  public static MBeanServerConnection getMBeanServerConnection(String address, Hashtable props)
    throws IOException {
    return getConnector(address, props).getMBeanServerConnection();
  }

  /**
   * Returns a JmxConnector for a given protocol (address) and server (adddress/props).
   * The JmxConnector can be used to create MBeanServerConnections later on.
   */
  public static JmxConnector getConnector(final String address, Hashtable props)
    throws IOException {
    if (address == null) {
      throw new IllegalArgumentException("Connector address must not be null.");
    }

    if (props == null) {
      props = EMPTY_TABLE;
    }
    final String sap;
    final String protocol;
    final int delimPos = address.lastIndexOf(':');

    if (delimPos < 0) {
      sap = null;
      protocol = address;
    }
    else {
      sap = address.substring(delimPos + 1);
      protocol = address.substring(0, delimPos + 1);
    }

    JmxConnectorFactory factory;
    if (protocol.equals(PROTOCOL_ENGINE_N2N)) {
      if (sap == null) {
        throw new IllegalArgumentException(
          "Illegal address, cluster node id missing from " + address);
      }
      factory = n2nConnectorFactory;
    }
    else if (protocol.equals(PROTOCOL_ENGINE_LOCAL)) {
      factory = localConnectorFactory;
    }
    else if (protocol.equals(PROTOCOL_ENGINE_P4)) {
      factory = p4ConnectorFactory;
    }
    else {
      throw new IllegalArgumentException("Invalid connector address " + address);
    }

    if (factory == null) {
      // determine class loader to load factory class
      ClassLoader loader = null;
      Object obj = props.get(CONNECTOR_FACTORY_CLASS_LOADER);
      if (obj instanceof ClassLoader) {
        loader = (ClassLoader) obj;
      }
      if (loader == null) {
        loader = Thread.currentThread().getContextClassLoader();
      }
      if (loader == null) {
        loader = JmxConnectionFactory.class.getClassLoader();
      }
      // try to instantiate factory
      if (protocol.equals(PROTOCOL_ENGINE_N2N)) {
        if (sap == null) {
          throw new IllegalArgumentException(
            "Illegal address, cluster node id missing from " + address);
        }
        try {
          Class clazz = loader.loadClass(CLASS_ENGINE_N2N);
          factory = (JmxConnectorFactory) clazz.newInstance();
          n2nConnectorFactory = factory;
        }
        catch (Throwable e) {
          throw new RemoteException("unable to create connector", e);
        }
      }
      else if (protocol.equals(PROTOCOL_ENGINE_LOCAL)) {
        try {
          Class clazz = loader.loadClass(CLASS_ENGINE_LOCAL);
          factory = (JmxConnectorFactory) clazz.newInstance();
          localConnectorFactory = factory;
        }
        catch (Throwable e) {
          throw new RemoteException("unable to create connector", e);
        }
      }
      else if (protocol.equals(PROTOCOL_ENGINE_P4)) {
        try {
          Class clazz = loader.loadClass(CLASS_ENGINE_P4);
          factory = (JmxConnectorFactory) clazz.newInstance();
          p4ConnectorFactory = factory;
        }
        catch (Throwable e) {
          throw new RemoteException("unable to create connector", e);
        }
      }
      else {
        throw new IllegalArgumentException("Invalid connector address " + address);
      }
    }
    return factory.getJmxConnector(sap, props);
  }

}
