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
package com.sap.engine.mejb;

import javax.management.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Date: 2004-6-8
 * 
 * @author Nikolai Angelov
 */
public class ManagementProxy implements MBeanServerConnection {
  
  public static final String JMX_SERVER_LOOKUP_STRING = "jmx";
  private static ManagementProxy instance = null;
  private MBeanServer defaultServer;
//  private ListenerRegistry listenerRegistry;
  ArrayList serversList = null;  

  // this class implements Singleton pattern
  private ManagementProxy() throws NamingException {
    serversList = findMBeanServers();
    defaultServer = (MBeanServer) serversList.get(0);
    // TODO: use sap logging
  }

  private ArrayList findMBeanServers() throws NamingException {
    ArrayList servers = new ArrayList();
    InitialContext jndiContext = new InitialContext();
    Object server = jndiContext.lookup(JMX_SERVER_LOOKUP_STRING);
    if (server instanceof MBeanServer) {
      servers.add(server);
    }

    return servers;
  }

  public static ManagementProxy getManagementProxy() throws NamingException {
    synchronized (ManagementProxy.class) {
      if (instance == null) {
        instance = new ManagementProxy();
      }
    }
    return instance;
  }

  public Object getAttribute(ObjectName name, String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, RemoteException {
    return defaultServer.getAttribute(name, attribute);
  }

  public AttributeList getAttributes(ObjectName name, String[] attributes) throws InstanceNotFoundException, ReflectionException, RemoteException {
    return defaultServer.getAttributes(name, attributes);
  }

  public String getDefaultDomain() throws RemoteException {
    return defaultServer.getDefaultDomain();
  }

  public Integer getMBeanCount() throws RemoteException {
    return defaultServer.getMBeanCount();
  }

  public MBeanInfo getMBeanInfo(ObjectName name) throws IntrospectionException, InstanceNotFoundException, ReflectionException, RemoteException {
    return defaultServer.getMBeanInfo(name);
  }

  public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException, RemoteException {
    return defaultServer.invoke(name, operationName, params, signature);
  }

  public boolean isRegistered(ObjectName name) throws RemoteException {
    return defaultServer.isRegistered(name);
  }

  public Set queryNames(ObjectName name, QueryExp query) throws RemoteException {
    return defaultServer.queryNames(name, query);
  }

  public void setAttribute(ObjectName name, Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, RemoteException {
    defaultServer.setAttribute(name, attribute);
  }

  public AttributeList setAttributes(ObjectName name, AttributeList attributes) throws InstanceNotFoundException, ReflectionException, RemoteException {
    return defaultServer.setAttributes(name, attributes);
  }

  public void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException {
    defaultServer.addNotificationListener(name, listener, filter, handback);
  }

  public void removeNotificationListener(ObjectName name, NotificationListener listener) throws ListenerNotFoundException, InstanceNotFoundException {
    defaultServer.removeNotificationListener(name, listener);
  }

  
  /**
   * @param arg0
   * @param arg1
   * @param arg2
   * @param arg3
   * @throws javax.management.InstanceNotFoundException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#addNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
   */
  public void addNotificationListener(
    ObjectName arg0,
    ObjectName arg1,
    NotificationFilter arg2,
    Object arg3)
    throws InstanceNotFoundException, IOException {
    defaultServer.addNotificationListener(arg0, arg1, arg2, arg3);

  }

  /**
   * @param arg0
   * @param arg1
   * @param arg2
   * @param arg3
   * @return
   * @throws javax.management.ReflectionException
   * @throws javax.management.InstanceAlreadyExistsException
   * @throws javax.management.MBeanRegistrationException
   * @throws javax.management.MBeanException
   * @throws javax.management.NotCompliantMBeanException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
   */
  public ObjectInstance createMBean(
    String arg0,
    ObjectName arg1,
    Object[] arg2,
    String[] arg3)
    throws
      ReflectionException,
      InstanceAlreadyExistsException,
      MBeanRegistrationException,
      MBeanException,
      NotCompliantMBeanException,
      IOException {
    return defaultServer.createMBean(arg0, arg1, arg2, arg3);
  }

  /**
   * @param arg0
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   * @return
   * @throws javax.management.ReflectionException
   * @throws javax.management.InstanceAlreadyExistsException
   * @throws javax.management.MBeanRegistrationException
   * @throws javax.management.MBeanException
   * @throws javax.management.NotCompliantMBeanException
   * @throws javax.management.InstanceNotFoundException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
   */
  public ObjectInstance createMBean(
    String arg0,
    ObjectName arg1,
    ObjectName arg2,
    Object[] arg3,
    String[] arg4)
    throws
      ReflectionException,
      InstanceAlreadyExistsException,
      MBeanRegistrationException,
      MBeanException,
      NotCompliantMBeanException,
      InstanceNotFoundException,
      IOException {
    return defaultServer.createMBean(arg0, arg1, arg2, arg3, arg4);
  }

  /**
   * @param arg0
   * @param arg1
   * @param arg2
   * @return
   * @throws javax.management.ReflectionException
   * @throws javax.management.InstanceAlreadyExistsException
   * @throws javax.management.MBeanRegistrationException
   * @throws javax.management.MBeanException
   * @throws javax.management.NotCompliantMBeanException
   * @throws javax.management.InstanceNotFoundException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName)
   */
  public ObjectInstance createMBean(
    String arg0,
    ObjectName arg1,
    ObjectName arg2)
    throws
      ReflectionException,
      InstanceAlreadyExistsException,
      MBeanRegistrationException,
      MBeanException,
      NotCompliantMBeanException,
      InstanceNotFoundException,
      IOException {
    return defaultServer.createMBean(arg0, arg1, arg2);
  }

  /**
   * @param arg0
   * @param arg1
   * @return
   * @throws javax.management.ReflectionException
   * @throws javax.management.InstanceAlreadyExistsException
   * @throws javax.management.MBeanRegistrationException
   * @throws javax.management.MBeanException
   * @throws javax.management.NotCompliantMBeanException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName)
   */
  public ObjectInstance createMBean(String arg0, ObjectName arg1)
    throws
      ReflectionException,
      InstanceAlreadyExistsException,
      MBeanRegistrationException,
      MBeanException,
      NotCompliantMBeanException,
      IOException {
    return defaultServer.createMBean(arg0, arg1);
  }

  /**
   * @return
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#getDomains()
   */
  public String[] getDomains() throws IOException {
    return defaultServer.getDomains();
  }

  /**
   * @param arg0
   * @return
   * @throws javax.management.InstanceNotFoundException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#getObjectInstance(javax.management.ObjectName)
   */
  public ObjectInstance getObjectInstance(ObjectName arg0)
    throws InstanceNotFoundException, IOException {
    return defaultServer.getObjectInstance(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   * @return
   * @throws javax.management.InstanceNotFoundException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#isInstanceOf(javax.management.ObjectName, java.lang.String)
   */
  public boolean isInstanceOf(ObjectName arg0, String arg1)
    throws InstanceNotFoundException, IOException {
    return defaultServer.isInstanceOf(arg0, arg1);
  }

  /**
   * @param arg0
   * @param arg1
   * @return
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#queryMBeans(javax.management.ObjectName, javax.management.QueryExp)
   */
  public Set queryMBeans(ObjectName arg0, QueryExp arg1) throws IOException {
    return defaultServer.queryMBeans(arg0, arg1);
  }

  /**
   * @param arg0
   * @param arg1
   * @param arg2
   * @param arg3
   * @throws javax.management.InstanceNotFoundException
   * @throws javax.management.ListenerNotFoundException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
   */
  public void removeNotificationListener(
    ObjectName arg0,
    NotificationListener arg1,
    NotificationFilter arg2,
    Object arg3)
    throws InstanceNotFoundException, ListenerNotFoundException, IOException {
    defaultServer.removeNotificationListener(arg0, arg1, arg2, arg3);
  }

  /**
   * @param arg0
   * @param arg1
   * @param arg2
   * @param arg3
   * @throws javax.management.InstanceNotFoundException
   * @throws javax.management.ListenerNotFoundException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
   */
  public void removeNotificationListener(
    ObjectName arg0,
    ObjectName arg1,
    NotificationFilter arg2,
    Object arg3)
    throws InstanceNotFoundException, ListenerNotFoundException, IOException {
    defaultServer.removeNotificationListener(arg0, arg1, arg2, arg3);
  }

  /**
   * @param arg0
   * @param arg1
   * @throws javax.management.InstanceNotFoundException
   * @throws javax.management.ListenerNotFoundException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName)
   */
  public void removeNotificationListener(ObjectName arg0, ObjectName arg1)
    throws InstanceNotFoundException, ListenerNotFoundException, IOException {
    defaultServer.removeNotificationListener(arg0, arg1);
  }

  /**
   * @param arg0
   * @throws javax.management.InstanceNotFoundException
   * @throws javax.management.MBeanRegistrationException
   * @throws java.io.IOException
   * @see javax.management.MBeanServerConnection#unregisterMBean(javax.management.ObjectName)
   */
  public void unregisterMBean(ObjectName arg0)
    throws InstanceNotFoundException, MBeanRegistrationException, IOException {
    defaultServer.unregisterMBean(arg0);
  }

}
