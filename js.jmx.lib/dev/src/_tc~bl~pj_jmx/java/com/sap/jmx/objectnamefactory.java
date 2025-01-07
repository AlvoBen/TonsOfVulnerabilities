/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.jmx;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * <p>
 * ObjectNameFactory offers a set of convenience methods and constants that  make it easier to
 * build JMX ObjectNames that comply with the rules of the J2EE servers JMX implementation. The
 * rules are basically those of the <a href="http://jcp.org/jsr/detail/77.jsp">J2EE Management
 * specification</a> also kown as JSR77.
 * </p>
 * 
 * <p>
 * For the SAP J2EE Engine these rules have been extended in order to be able to distinguish
 * between (VM) local and clustered managed objects. All J2EE standard (JSR77) managed objects
 * describe  clustered object (except for the type <code>JVM</code>). The name of the SAP specific
 * types  always starts with <code>SAP_</code>. Those types that represent a local view of a
 * managed object end with <code>PerNode</code>. For example <code>SAP_J2EEServicePerNode</code>
 * represents the local properties/state of a SAP J2EE Engine service.
 * </p>
 * JMX ObjectNames have in general have the following format:
 * <pre>
 * &lt;domain-name&gt;:&lt;key&gt;=&lt;value&gt;[,&lt;key&gt;=&lt;value&gt;]*
 * </pre>
 * 
 * <p>
 * JSR77 defines further rules for the key-value pairs. There has to be a <code>j2eeType</code>, a
 * <code>name</code>, and, depending on the value of <code>j2eeType</code>,  several parent object
 * keys. The parent key mechanism is used to build hierarchical names. Furthermore, the lifetime
 * of a child object depends on the lifetime of its parent objects(s).
 * </p>
 * 
 * <p>
 * Definition of own types is also possible. They have to integrate into the J2EE/SAP naming
 * hierarchy and should start with the <code>SAP_</code> prefix. Each self defined object has to
 * include at least the <code>SAP_J2EECluster</code> parent key and the
 * <code>SAP_J2EEClusterNode</code> key in the local case. Object types in the scope of J2EE
 * applications usually also include the <code>SAP_J2EEApplication</code> key. For the SAP J2EE
 * Engine the <code>domain-name</code> has to remain empty which maps to the default domain of the
 * JMX MBeanServer. <br>
 * The following shows some examples using predefined J2EE/SAP types. For a description of the
 * J2EE managed object type please refer to the <a href="http://jcp.org/jsr/detail/77.jsp">J2EE
 * Management specification</a>.
 * </p>
 * The entire J2EE server (cluster).
 * <pre>
 *   :j2eeType=SAP_J2EECluster,name=BankServer1
 * </pre>
 * A cluster node (server or dispatcher).
 * <pre>
 *   :j2eeType=SAP_J2EEClusterNode,name=4001,SAP_J2EECluster=BankServer1
 * </pre>
 * The clustered view of the dbpool service.
 * <pre>
 *   :j2eeType=JDBCResource,name=dbpool,SAP_J2EECluster=BankServer1
 * </pre>
 * The local view of the dbpool service.
 * <pre>
 *   :j2eeType=SAP_J2EEServicePerNode,name=dbpool,SAP_J2EECluster=BankServer1,SAP_J2EEClusterNode=4001
 * </pre>
 * The clustered view of an application.
 * <pre>
 *   :j2eeType=SAP_J2EEApplication,name=AccountsController,SAP_J2EECluster=BankServer1
 * </pre>
 * The local view of that application.
 * <pre>
 *   :j2eeType=SAP_J2EEApplicationPerNode,name=AccountsController,SAP_J2EEClusterNode=4001,SAP_J2EECluster=BankServer1
 * </pre>
 * The clustered view of an ejb module.
 * <pre>
 *   :j2eeType=EJBModule,name=BankAccount,SAP_J2EEApplication=AccountsController,SAP_J2EECluster=BankServer1
 * </pre>
 * The local view of that ejb module.
 * <pre>
 *   :j2eeType=SAP_EJBModulePerNode,name=BankAccount,SAP_J2EEApplicationPerNode=AccountsController,SAP_J2EEClusterNode=4001,SAP_J2EECluster=BankServer1
 * </pre>
 * The clustered view of an entity bean.
 * <pre>
 *   :j2eeType=EntityBean,name=Account,EJBModule=BankAccount,SAP_J2EEApplication=AccountsController,SAP_J2EECluster=BankServer1
 * </pre>
 * The local view of that entity bean.
 * <pre>
 *   :j2eeType=SAP_EntityBeanPerNode,name=Account,SAP_EJBModulePerNode=BankAccount,SAP_J2EEApplicationPerNode=AccountsController,SAP_J2EEClusterNode=4001,SAP_J2EECluster=BankServer1
 * </pre>
 * 
 * @version 1.0
 * @author d025700
 * @see javax.management.ObjectName
 */
public class ObjectNameFactory {
  /** Value for j2eeType <code>J2EEDomain</code>. */
  public static final String J2EEDomain = "J2EEDomain"; //$NON-NLS-1$

  /** Value for j2eeType <code>J2EEServer</code>. */
  public static final String J2EEServer = "J2EEServer"; //$NON-NLS-1$

  /** Value for j2eeType <code>J2EEApplication</code>. */
  public static final String J2EEApplication = "J2EEApplication"; //$NON-NLS-1$
  
  /** Value for j2eeType <code>ResourceAdapterTemplateApplication</code>. */
  public static final String ResourceAdapterTemplateApplication = "ResourceAdapterTemplateApplication"; //$NON-NLS-1$

  /** Value for j2eeType <code>AppClientModule</code>. */
  public static final String AppClientModule = "AppClientModule"; //$NON-NLS-1$

  /** Value for j2eeType <code>EJBModule</code>. */
  public static final String EJBModule = "EJBModule"; //$NON-NLS-1$

  /** Value for j2eeType <code>WebModule</code>. */
  public static final String WebModule = "WebModule"; //$NON-NLS-1$

  /** Value for j2eeType <code>ResourceAdapterModule</code>. */
  public static final String ResourceAdapterModule = "ResourceAdapterModule"; //$NON-NLS-1$
  
  /** Value for j2eeType <code>ResourceAdapterTemplateModule</code>. */
  public static final String ResourceAdapterTemplateModule = "ResourceAdapterTemplateModule"; //$NON-NLS-1$

  /** Value for j2eeType <code>EntityBean</code>. */
  public static final String EntityBean = "EntityBean"; //$NON-NLS-1$

  /** Value for j2eeType <code>StatefulSessionBean</code>. */
  public static final String StatefulSessionBean = "StatefulSessionBean"; //$NON-NLS-1$

  /** Value for j2eeType <code>StatelessSessionBean</code>. */
  public static final String StatelessSessionBean = "StatelessSessionBean"; //$NON-NLS-1$

  /** Value for j2eeType <code>MessageDrivenBean</code>. */
  public static final String MessageDrivenBean = "MessageDrivenBean"; //$NON-NLS-1$

  /** Value for j2eeType <code>Servlet</code>. */
  public static final String Servlet = "Servlet"; //$NON-NLS-1$

  /** Value for j2eeType <code>ResourceAdapterTemplate</code>. */
  public static final String ResourceAdapterTemplate = "ResourceAdapterTemplate"; //$NON-NLS-1$
  
  /** Value for j2eeType <code>ResourceAdapter</code>. */
  public static final String ResourceAdapter = "ResourceAdapter"; //$NON-NLS-1$

  /** Value for j2eeType <code>JavaMailResource</code>. */
  public static final String JavaMailResource = "JavaMailResource"; //$NON-NLS-1$

  /** Value for j2eeType <code>JCAResource</code>. */
  public static final String JCAResource = "JCAResource"; //$NON-NLS-1$

  /** Value for j2eeType <code>JCAConnectionFactory</code>. */
  public static final String JCAConnectionFactory = "JCAConnectionFactory"; //$NON-NLS-1$

  /** Value for j2eeType <code>JCAManagedConnectionFactory</code>. */
  public static final String JCAManagedConnectionFactory = "JCAManagedConnectionFactory"; //$NON-NLS-1$

  /** Value for j2eeType <code>JDBCResource</code>. */
  public static final String JDBCResource = "JDBCResource"; //$NON-NLS-1$

  /** Value for j2eeType <code>JDBCDataSource</code>. */
  public static final String JDBCDataSource = "JDBCDataSource"; //$NON-NLS-1$

  /** Value for j2eeType <code>JDBCDriver</code>. */
  public static final String JDBCDriver = "JDBCDriver"; //$NON-NLS-1$

  /** Value for j2eeType <code>JMSResource</code>. */
  public static final String JMSResource = "JMSResource"; //$NON-NLS-1$

  /** Value for j2eeType <code>JNDIResource</code>. */
  public static final String JNDIResource = "JNDIResource"; //$NON-NLS-1$

  /** Value for j2eeType <code>JTAResource</code>. */
  public static final String JTAResource = "JTAResource"; //$NON-NLS-1$

  /** Value for j2eeType <code>RMI_IIOPResource</code>. */
  public static final String RMI_IIOPResource = "RMI_IIOPResource"; //$NON-NLS-1$

  /** Value for j2eeType <code>URLResource</code>. */
  public static final String URLResource = "URLResource"; //$NON-NLS-1$

  /** Value for j2eeType <code>JVM</code>. */
  public static final String JVM = "JVM"; //$NON-NLS-1$

  /** Value for name of <code>JavaMailResource</code>. */
  public static final String JavaMailResource_NAME = "javamail"; //$NON-NLS-1$

  /** Value for name of <code>JCAResource</code>. */
  public static final String JCAResource_NAME = "connector"; //$NON-NLS-1$

  /** Value for name of <code>JDBCResource_NAME</code>. */
  public static final String JDBCResource_NAME = "dbpool"; //$NON-NLS-1$

  /** Value for name of <code>JMSResource_NAME</code>. */
  public static final String JMSResource_NAME = "jms"; //$NON-NLS-1$

  /** Value for name of <code>JNDIResource_NAME</code>. */
  public static final String JNDIResource_NAME = "naming"; //$NON-NLS-1$

  /** Value for name of <code>JTAResource_NAME</code>. */
  public static final String JTAResource_NAME = "jta"; //$NON-NLS-1$

  /** Value for name of <code>RMI_IIOPResource_NAME</code>. */
  public static final String RMI_IIOPResource_NAME = "iiop"; //$NON-NLS-1$

  /** Value for name of <code>URLResource_NAME</code>. */
  public static final String URLResource_NAME = "http"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EECluster</code>. */
  public static final String SAP_J2EECluster = "SAP_J2EECluster"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EEInstance</code>. */
  public static final String SAP_J2EEInstance = "SAP_J2EEInstance"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EEClusterNode</code>. */
  public static final String SAP_J2EEClusterNode = "SAP_J2EEClusterNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EEServicePerNode</code>. */
  public static final String SAP_J2EEServicePerNode = "SAP_J2EEServicePerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EEApplication</code>. */
  public static final String SAP_J2EEApplication = "SAP_J2EEApplication"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EEApplicationPerNode</code>. */
  public static final String SAP_J2EEApplicationPerNode = "SAP_J2EEApplicationPerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_AppClientModulePerNode</code>. */
  public static final String SAP_AppClientModulePerNode = "SAP_AppClientModulePerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_EJBModulePerNode</code>. */
  public static final String SAP_EJBModulePerNode = "SAP_EJBModulePerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_WebModulePerNode</code>. */
  public static final String SAP_WebModulePerNode = "SAP_WebModulePerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_ResourceAdapterModulePerNode</code>. */
  public static final String SAP_ResourceAdapterModulePerNode = "SAP_ResourceAdapterModulePerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_EntityBeanPerNode</code>. */
  public static final String SAP_EntityBeanPerNode = "SAP_EntityBeanPerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_StatefulSessionBeanPerNode</code>. */
  public static final String SAP_StatefulSessionBeanPerNode = "SAP_StatefulSessionBeanPerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_StatelessSessionBeanPerNode</code>. */
  public static final String SAP_StatelessSessionBeanPerNode = "SAP_StatelessSessionBeanPerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_MessageDrivenBeanPerNode</code>. */
  public static final String SAP_MessageDrivenBeanPerNode = "SAP_MessageDrivenBeanPerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_ServletPerNode</code>. */
  public static final String SAP_ServletPerNode = "SAP_ServletPerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_ResourceAdapterPerNode</code>. */
  public static final String SAP_ResourceAdapterPerNode = "SAP_ResourceAdapterPerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EEClassLoader</code>. */
  public static final String SAP_J2EEClassLoader = "SAP_J2EEClassLoader"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EEKernelPerNode</code>. */
  public static final String SAP_J2EEKernelPerNode = "SAP_J2EEKernelPerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EELibraryPerNode</code>. */
  public static final String SAP_J2EELibraryPerNode = "SAP_J2EELibraryPerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EEInterfacePerNode</code>. */
  public static final String SAP_J2EEInterfacePerNode = "SAP_J2EEInterfacePerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EEServiceRuntimePerNode</code>. */
  public static final String SAP_J2EEServiceRuntimePerNode = "SAP_J2EEServiceRuntimePerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EEKernelRuntimePerNode</code>. */
  public static final String SAP_J2EEKernelRuntimePerNode = "SAP_J2EEKernelRuntimePerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_MonitorPerNode</code>. */
  public static final String SAP_MonitorPerNode = "SAP_MonitorPerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_ApplicationResourcePerNode</code>. */
  public static final String SAP_ApplicationResourcePerNode = "SAP_ApplicationResourcePerNode"; //$NON-NLS-1$

  /** SAP value for j2eeType <code>SAP_J2EETimerFactory</code>. */
  public static final String SAP_J2EETimerFactory = "SAP_J2EETimerFactory"; //$NON-NLS-1$
  
  /**
   * The default object name of an MLet.
   * The value is <code>:type=MLet</code>.
   */
  public static final String MLET_SERVICE_DEFAULT_NAME = ":type=MLet"; //$NON-NLS-1$

  /**
   * The object name of the MBeanServer delegate object.
   * The value is <code>JMImplementation:type=MBeanServerDelegate</code>.
   */
  public static final String MBEAN_SERVER_DELEGATE_NAME = "JMImplementation:type=MBeanServerDelegate" ; //$NON-NLS-1$
  
  /**
   * An <code>&quot;&quot;</code> is used to designate an empty value. Empty values are allowed for
   * keys the server can provide the value, for example the server name or the cluster ID, i.e.
   * <code>SAP_J2EECluster</code> and <code>SAP_J2EEClusterNode</code>. During registration of the
   * MBean an MBeanServer interceptor replaces all empty values with the actual values. The empty
   * value is represented by the following Java String.
   * <pre>
   *   \&quot;\&quot;&quot;&quot;
   * </pre>
   */
  public static final String EMPTY_VALUE = "\"\""; //$NON-NLS-1$

  /** Name of <code>j2eeType</code> key. */
  public static final String J2EETYPE_KEY = "j2eeType"; //$NON-NLS-1$

  /** Name of <code>name</code> key. */
  public static final String NAME_KEY = "name"; //$NON-NLS-1$

  /** Name of the pattern key, i.e. "". */
  public static final String PATTERN_KEY = ""; //$NON-NLS-1$

  /**
   * Value of <code>SAP_J2EEApplication</code> and <code>J2EEApplication</code> key for standalone
   * modules.
   */
  public static final String STANDALONE_MODULE_APPLICATION_NAME = "null"; //$NON-NLS-1$
  private static final Location LOCATION = Location.getLocation(ObjectNameFactory.class);
  private static final String DEFAULT_DOMAIN = ""; //$NON-NLS-1$
  private static final char PATTERN_VALUE = '*';
  private static final String RUNTIME_PROPERTIES_CLASS_NAME = "com.sap.engine.frame.RuntimeProperties"; //$NON-NLS-1$
  private static final String RUNTIME_PROPERTIES_GET_METHOD_NAME = "get"; //$NON-NLS-1$
  private static final String RUNTIME_PROPERTIES_PROPERTY_APPLICATION = "PROPERTY_APPLICATION"; //$NON-NLS-1$
  private static final ObjectName MBEAN_SERVER_DELEGATE_ON;
  
  private static Method runtimePropertyMethod = null;
  private static Object[] APPLICATION_PROP = null;

  static {
    try {
      Class runtimeProperties = Class.forName(RUNTIME_PROPERTIES_CLASS_NAME);

      runtimePropertyMethod = runtimeProperties.getMethod(RUNTIME_PROPERTIES_GET_METHOD_NAME,
                                                          new Class[] {int.class});

      APPLICATION_PROP = new Object[] {runtimeProperties.getField(RUNTIME_PROPERTIES_PROPERTY_APPLICATION).get(null)};
    }
    catch (ClassNotFoundException e) {
      LOCATION.traceThrowableT(Severity.PATH, "unable to access rumtime properties, JMX is probably used outside the J2EE Engine", e);
    }
    catch (Exception e) {
      LOCATION.traceThrowableT(Severity.PATH, "unable to access rumtime properties, JMX is probably used outside the J2EE Engine", e);
    }
  }

  static {
    try {
      MBEAN_SERVER_DELEGATE_ON = new ObjectName(MBEAN_SERVER_DELEGATE_NAME);
    }
    catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }
  
  private static String getApplicationName() {
    Object appName = null;
    if (runtimePropertyMethod != null) {
      if (System.getSecurityManager() != null) {
        appName = AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            try {
              return runtimePropertyMethod.invoke(null, APPLICATION_PROP);
            }
            catch (Exception e) {
              LOCATION.traceThrowableT(Severity.PATH, "unable to get application name from rumtime properties", e);
              return null;
            }
          }
        });
      }
      else {
        try {
          appName = runtimePropertyMethod.invoke(null, APPLICATION_PROP);
        }
        catch (Exception e) {
          LOCATION.traceThrowableT(Severity.PATH, "unable to get application name from rumtime properties", e);
          return null;
        }
      }
    }
    if (appName == null || appName instanceof String) {
      return (String) appName;
    }
    else {
      LOCATION.logT(Severity.PATH, "unable to get application name from rumtime properties, the name returned was " + (appName == null ? "" : appName.getClass().getName()) + appName);
      return null;
    }
  }
  
  /**
   * @return the ObjectName for the MBeanServerDalagate which is always <code>JMImplementation:type=MBeanServerDelegate</code>.
   */
  public static final ObjectName getMBeanServerDelegateName() {
    return MBEAN_SERVER_DELEGATE_ON;
  }

  /**
   * Returns a J2EE ObjectName without a parent key conforming to the following pattern. Name and
   * type parameter must not be <code>null</code>.
   * <pre>
   *   :j2eeType=&lt;type&gt;,name=&lt;name&gt;
   * </pre>
   * 
   * @param type value of the <code>j2eeType</code> key, e.g. <code>J2EEApplication</code> or
   *        <code>SAP_J2EEClusterNode</code>.
   * @param name value of the <code>name</code> key.
   * 
   * @return the generated <code>ObjectName</code>.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static final ObjectName getObjectName(String type, String name)
    throws MalformedObjectNameException {
    return getObjectName(type, name, null, null);
  }

  /**
   * Returns a J2EE ObjectName with a single parent key conforming to the following pattern. Name
   * and type parameter must not be <code>null</code>. If either parentName or parent type is
   * <code>null</code> both are ignored.
   * <pre>
   *   :j2eeType=&lt;type&gt;,name=&lt;name&gt;[,&lt;parenType&gt;=&lt;parentName&gt;]
   * </pre>
   * 
   * @param type value of the <code>j2eeType</code> key, e.g. <code>J2EEApplication</code> or
   *        <code>SAP_J2EEClusterNode</code>.
   * @param name value of the <code>name</code> key.
   * @param parentType name of the parent key, e.g. <code>J2EEApplication</code> or
   *        <code>SAP_J2EEClusterNode</code>.
   * @param parentName value of the parent key.
   * 
   * @return the generated <code>ObjectName</code>.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static final ObjectName getObjectName(
    String type,
    String name,
    String parentType,
    String parentName)
    throws MalformedObjectNameException {
    if ((type == null) || (name == null)) {
      throw new MalformedObjectNameException("ObjectName can not be created. Type and name must not be null.");
    }

    Hashtable table = new Hashtable();
    table.put(J2EETYPE_KEY, type);
    table.put(NAME_KEY, name);

    if ((parentType != null) && (parentName != null)) {
      table.put(parentType, parentName);
    }

    return new ObjectName(DEFAULT_DOMAIN, table);
  }

  /**
   * Returns a J2EE ObjectName with multiple parent keys conforming to the following pattern. Name
   * and type parameter must not be <code>null</code>.
   * <pre>
   *   :j2eeType=&lt;type&gt;,name=&lt;name&gt;[,&lt;parenType&gt;=&lt;parentName&gt;]*
   * </pre>
   * 
   * @param type value of the <code>j2eeType</code> key, e.g. <code>J2EEApplication</code> or
   *        <code>SAP_J2EEClusterNode</code>.
   * @param name value of the <code>name</code> key.
   * @param parentKeys contains the parent keys as type-name pair. Both key and value have to be
   *        Strings.
   * 
   * @return the generated <code>ObjectName</code>.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getObjectName(String type, String name, Hashtable parentKeys)
    throws MalformedObjectNameException {
    if ((type == null) || (name == null)) {
      throw new MalformedObjectNameException("ObjectName can not be created. Type and name must not be null.");
    }

    Hashtable table;

    if (parentKeys == null) {
      table = new Hashtable();
    }
    else {
      table = (Hashtable) parentKeys.clone();
    }

    table.put(J2EETYPE_KEY, type);
    table.put(NAME_KEY, name);

    return new ObjectName(DEFAULT_DOMAIN, table);
  }

  /**
   * Returns a J2EE ObjectName based on a given ObjectName. Its <code>j2eeType</code>,
   * <code>name</code>, and parent keys are are taken as parent keys for the new ObjectName. Name
   * and type parameter must not be <code>null</code>. The result conforms to the following
   * pattern.
   * <pre>
   *   :j2eeType=&lt;type&gt;,name=&lt;name&gt;[,&lt;parenType&gt;=&lt;parentName&gt;]*
   * </pre>
   * 
   * @param type value of the <code>j2eeType</code> key, e.g. <code>J2EEApplication</code> or
   *        <code>SAP_J2EEClusterNode</code>.
   * @param name value of the <code>name</code> key.
   * @param parentObject ObjectName the parent keys are taken from. It must be a valid J2EE
   *        ObjectName that includes a <code>j2eeType</code> and a <code>name</code> key.
   * 
   * @return the generated <code>ObjectName</code>.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getObjectName(String type, String name, ObjectName parentObject)
    throws MalformedObjectNameException {
    if ((type == null) || (name == null)) {
      throw new MalformedObjectNameException("ObjectName can not be created. Type and name must not be null.");
    }

    Hashtable table;

    if (parentObject == null) {
      table = new Hashtable();
    }
    else {
      table = (Hashtable) parentObject.getKeyPropertyList();

      String parentName = (String) table.remove(NAME_KEY);
      String parentType = (String) table.remove(J2EETYPE_KEY);

      if ((parentType == null) || (parentName == null)) {
        throw new MalformedObjectNameException("ObjectName can not be created. Either j2eeType or name key is missing.");
      }

      table.put(parentType, parentName);
    }

    table.put(J2EETYPE_KEY, type);
    table.put(NAME_KEY, name);

    return new ObjectName(DEFAULT_DOMAIN, table);
  }

  /**
   * Returns a J2EE ObjectName for a child object (regarding parent keys) of a SAP_J2EEApplication.
   * The <var>type</var> and <var>name</var> parameter must not be <code>null</code>. The result
   * conforms to the following pattern.
   * <pre>
   *   :j2eeType=&lt;type&gt;,name=&lt;name&gt;,SAP_J2EEApplication=&lt;applicationName&gt;,SAP_J2EECluster=&lt;serverName&gt;
   * </pre>
   * 
   * @param type value of the <code>j2eeType</code> key, e.g. <code>EJBModule</code>.
   * @param name value of the <code>name</code> key.
   * @param applicationName value of the <code>SAP_J2EEApplication</code> key. A <code>null</code>
   *        value is substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * @param serverName value of the <code>SAP_J2EECluster</code> key.  A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * 
   * @return the generated <code>ObjectName</code>.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getNameForApplicationChild(
    String type,
    String name,
    String applicationName,
    String serverName)
    throws MalformedObjectNameException {
    if ((type == null) || (name == null)) {
      throw new MalformedObjectNameException("ObjectName can not be created. Type and name parameter must not be null.");
    }

    if (serverName == null) {
      serverName = EMPTY_VALUE;
    }

    if (applicationName == null) {
      applicationName = getApplicationName();
    }
    
    if (applicationName == null) {
      throw new MalformedObjectNameException("ObjectName can not be created. ApplicationName parameter is null an cannot be set by the factory when running outside the J2EE Engine.");
    }

    Hashtable table = new Hashtable();
    table.put(J2EETYPE_KEY, type);
    table.put(NAME_KEY, name);
    table.put(SAP_J2EEApplication, applicationName);
    table.put(SAP_J2EECluster, serverName);

    return new ObjectName(DEFAULT_DOMAIN, table);
  }

  /**
   * Returns a J2EE ObjectName for a child object (regarding parent keys) of a
   * SAP_J2EEApplicationPerNode which represents the VM-local view of an application. The
   * <var>type</var>, <var>name</var>, and <var>applicationName</var> parameter must not
   * be <code>null</code>. The result conforms to the following pattern.
   * <pre>
   *   :j2eeType=&lt;type&gt;,name=&lt;name&gt;,SAP_J2EEApplication=&lt;applicationName&gt;,SAP_J2EEClusterNode=&lt;clusterID&gt;,SAP_J2EECluster=&lt;serverName&gt;
   * </pre>
   * 
   * @param type value of the <code>j2eeType</code> key, e.g. <code>SAP_WebModulePerNode</code>.
   *        This should end with PerNode by convention.
   * @param name value of the <code>name</code> key.
   * @param applicationName value of the <code>SAP_J2EEApplicationPerNode</code> key.
   * @param clusterID value of the <code>SAP_J2EEClusterNode</code> key. A <code>null</code> value
   *        is substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * @param serverName value of the <code>SAP_J2EECluster</code> key. A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * 
   * @return the generated <code>ObjectName</code>.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getNameForApplicationChildPerNode(
    String type,
    String name,
    String applicationName,
    String clusterID,
    String serverName)
    throws MalformedObjectNameException {
    if ((type == null) || (name == null)) {
      throw new MalformedObjectNameException("ObjectName can not be created. Type, and name parameter must not be null.");
    }

    if (serverName == null) {
      serverName = EMPTY_VALUE;
    }

    if (clusterID == null) {
      clusterID = EMPTY_VALUE;
    }

    if (applicationName == null) {
      applicationName = getApplicationName();
    }
    
    if (applicationName == null) {
      throw new MalformedObjectNameException("ObjectName can not be created. ApplicationName parameter is null an cannot be set by the factory when running outside the J2EE Engine.");
    }

    Hashtable table = new Hashtable();
    table.put(J2EETYPE_KEY, type);
    table.put(NAME_KEY, name);
    table.put(SAP_J2EEApplicationPerNode, applicationName);
    table.put(SAP_J2EEClusterNode, clusterID);
    table.put(SAP_J2EECluster, serverName);

    return new ObjectName(DEFAULT_DOMAIN, table);
  }

  /**
   * Returns a J2EE ObjectName for a child object (regarding parent keys) of a SAP_J2EECluster. The
   * <var>type</var> and <var>name</var> parameter must not be <code>null</code>. The result
   * conforms to the following pattern.
   * <pre>
   *   :j2eeType=&lt;type&gt;,name=&lt;name&gt;,SAP_J2EECluster=&lt;serverName&gt;
   * </pre>
   * 
   * @param type value of the <code>j2eeType</code> key, e.g. <code>EJBModule</code>.
   * @param name value of the <code>name</code> key.
   * @param serverName value of the <code>SAP_J2EECluster</code> key. A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * 
   * @return the generated <code>ObjectName</code>.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getNameForServerChild(String type, String name, String serverName)
    throws MalformedObjectNameException {
    if ((type == null) || (name == null)) {
      throw new MalformedObjectNameException("ObjectName can not be created. Type and name parameter must not be null.");
    }

    if (serverName == null) {
      serverName = EMPTY_VALUE;
    }

    Hashtable table = new Hashtable();
    table.put(J2EETYPE_KEY, type);
    table.put(NAME_KEY, name);
    table.put(SAP_J2EECluster, serverName);

    return new ObjectName(DEFAULT_DOMAIN, table);
  }

  /**
   * Returns a J2EE ObjectName for a child object (regarding parent keys) of a SAP_J2EEClusterNode
   * which represents the VM-local view of a server. The <var>type</var> and <var>name</var>
   * parameter must not be <code>null</code>. The result conforms to the following pattern.
   * <pre>
   *   :j2eeType=&lt;type&gt;,name=&lt;name&gt;,SAP_J2EEClusterNode=&lt;clusterID&gt;,SAP_J2EECluster=&lt;serverName&gt;
   * </pre>
   * 
   * @param type value of the <code>j2eeType</code> key, e.g. <code>SAP_WebModulePerNode</code>.
   *        This should end with PerNode by convention.
   * @param name value of the <code>name</code> key.
   * @param clusterID value of the <code>SAP_J2EEClusterNode</code> key. A <code>null</code> value
   *        is substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * @param serverName value of the <code>SAP_J2EECluster</code> key. A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * 
   * @return the generated ObjectName.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getNameForServerChildPerNode(
    String type,
    String name,
    String clusterID,
    String serverName)
    throws MalformedObjectNameException {
    if ((type == null) || (name == null)) {
      throw new MalformedObjectNameException("ObjectName can not be created. Type and name parameter must not be null.");
    }

    if (serverName == null) {
      serverName = EMPTY_VALUE;
    }

    if (clusterID == null) {
      clusterID = EMPTY_VALUE;
    }

    Hashtable table = new Hashtable();
    table.put(J2EETYPE_KEY, type);
    table.put(NAME_KEY, name);
    table.put(SAP_J2EEClusterNode, clusterID);
    table.put(SAP_J2EECluster, serverName);

    return new ObjectName(DEFAULT_DOMAIN, table);
  }

  /**
   * Returns a J2EE ObjectName patterns suitable for querying all child objects of the given
   * SAP_J2EEClusterNode specified by <var>clusterID</var> and <var>serverName</var> that match
   * the given <var>type</var>. The <var>type</var> parameter must not be <code>null</code>. The
   * result conforms to the following pattern.
   * <pre>
   *   :j2eeType=&lt;type&gt;,*,SAP_J2EEClusterNode=&lt;clusterID&gt;,SAP_J2EECluster=&lt;serverName&gt;
   * </pre>
   * 
   * @param type type value of the <code>j2eeType</code> key, e.g.
   *        <code>SAP_WebModulePerNode</code>. This should end with PerNode by convention.
   * @param clusterID value of the <code>SAP_J2EEClusterNode</code> key. A <code>null</code> value
   *        is substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * @param serverName value of the <code>SAP_J2EECluster</code> key. A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * 
   * @return the generated <code>ObjectName</code> pattern.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getPatternForServerChildPerNode(
    String type,
    String clusterID,
    String serverName)
    throws MalformedObjectNameException {
    if ((type == null)) {
      throw new MalformedObjectNameException("ObjectName can not be created. Type parameter must not be null.");
    }

    if (serverName == null) {
      serverName = EMPTY_VALUE;
    }

    if (clusterID == null) {
      clusterID = EMPTY_VALUE;
    }

    StringBuffer sb = new StringBuffer();
    sb.append("*:"); //$NON-NLS-1$
    sb.append(PATTERN_VALUE);
    sb.append(',');
    sb.append(J2EETYPE_KEY);
    sb.append('=');
    sb.append(type);
    sb.append(',');
    sb.append(SAP_J2EEClusterNode);
    sb.append('=');
    sb.append(clusterID);
    sb.append(',');
    sb.append(SAP_J2EECluster);
    sb.append('=');
    sb.append(serverName);
    return new ObjectName(sb.toString());
  }

  /**
   * Returns a J2EE ObjectName patterns suitable for querying all child objects of the given
   * SAP_J2EEClusterNode specified by <var>clusterID</var> and <var>serverName</var>. The result
   * conforms to the following pattern.
   * <pre>
   *   :*,SAP_J2EEClusterNode=&lt;clusterID&gt;,SAP_J2EECluster=&lt;serverName&gt;
   * </pre>
   * 
   * @param clusterID value of the <code>SAP_J2EEClusterNode</code> key. A <code>null</code> value
   *        is substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * @param serverName value of the <code>SAP_J2EECluster</code> key. A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * 
   * @return the generated <code>ObjectName</code> pattern.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getPatternForServerChildPerNode(String clusterID, String serverName)
    throws MalformedObjectNameException {
    if (serverName == null) {
      serverName = EMPTY_VALUE;
    }

    if (clusterID == null) {
      clusterID = EMPTY_VALUE;
    }

    StringBuffer sb = new StringBuffer();
    sb.append("*:"); //$NON-NLS-1$
    sb.append(PATTERN_VALUE);
    sb.append(',');
    sb.append(SAP_J2EEClusterNode);
    sb.append('=');
    sb.append(clusterID);
    sb.append(',');
    sb.append(SAP_J2EECluster);
    sb.append('=');
    sb.append(serverName);
    return new ObjectName(sb.toString());
  }

  /**
   * Returns a J2EE ObjectName patterns suitable for querying all child objects of the given
   * SAP_J2EECluster <var>serverName</var> that match the given <var>type</var>. The
   * <var>type</var> parameter must not be <code>null</code>. The result conforms to the following
   * pattern.
   * <pre>
   *   :j2eeType=&lt;type&gt;,*,SAP_J2EECluster=&lt;serverName&gt;
   * </pre>
   * 
   * @param type type value of the <code>j2eeType</code> key, e.g.
   *        <code>SAP_WebModulePerNode</code>. This should end with PerNode by convention.
   * @param serverName value of the <code>SAP_J2EECluster</code> key. A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * 
   * @return the generated <code>ObjectName</code> pattern.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getPatternForServerChild(String type, String serverName)
    throws MalformedObjectNameException {
    if ((type == null)) {
      throw new MalformedObjectNameException("ObjectName can not be created. Type parameter must not be null.");
    }

    if (serverName == null) {
      serverName = EMPTY_VALUE;
    }

    StringBuffer sb = new StringBuffer();
    sb.append("*:"); //$NON-NLS-1$
    sb.append(PATTERN_VALUE);
    sb.append(',');
    sb.append(J2EETYPE_KEY);
    sb.append('=');
    sb.append(type);
    sb.append(',');
    sb.append(SAP_J2EECluster);
    sb.append('=');
    sb.append(serverName);
    return new ObjectName(sb.toString());
  }

  /**
   * Returns a J2EE ObjectName patterns suitable for querying all child objects of the given
   * SAP_J2EECluster specified by <var>serverName</var>. The result conforms to the following
   * pattern.
   * <pre>
   *   :*,SAP_J2EECluster=&lt;serverName&gt;
   * </pre>
   * 
   * @param serverName value of the <code>SAP_J2EECluster</code> key. A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * 
   * @return the generated <code>ObjectName</code> pattern.
   * 
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getPatternForServerChild(String serverName)
    throws MalformedObjectNameException {
    if (serverName == null) {
      serverName = EMPTY_VALUE;
    }

    StringBuffer sb = new StringBuffer();
    sb.append("*:"); //$NON-NLS-1$
    sb.append(PATTERN_VALUE);
    sb.append(',');
    sb.append(SAP_J2EECluster);
    sb.append('=');
    sb.append(serverName);
    return new ObjectName(sb.toString());
  }

  /**
   * Checks if the given ObjectName is a local name. This is true if either:
   * 
   * <ul>
   * <li>
   * the <var>name</var> designates this cluster node, i.e. includes the <code>j2eeType</code> key,
   * the value of that key equals <code>SAP_J2EEClusterNode</code> and the value of the
   * <code>name</code> key equals either the given <var>nodeID</var> or <code>EMPTY_VALUE</code>.
   * </li>
   * <li>
   * the <var>name</var> designates a child of this cluster node, i.e. includes the
   * <code>j2eeType</code> key and a <code>SAP_J2EEClusterNode</code> key the value of the which
   * is equal to either the given <var>nodeID</var> or <code>EMPTY_VALUE</code>.
   * </li>
   * </ul>
   * 
   * 
   * @param name The ObjectName to be checked.
   * @param nodeID The local cluster node ID to be compared with.
   * 
   * @return <code>true</code> if name fulfills the conditions above, <code>false</code> otherwise.
   */
  public static boolean isJ2eeLocalName(ObjectName name, String nodeID) {
    if (name == null) {
      return true;
    }
    String j2eeType = getJ2eeType(name);

    // the name of the cluster node itself
    if (SAP_J2EEClusterNode.equals(j2eeType)) {
      String nameValue = getName(name);
      if ((nameValue != null) && (nameValue.equals(EMPTY_VALUE) || nameValue.equals(nodeID))) {
        return true;
      }
      else {
        return false;
      }
    }

    // a child of the cluster node
    String elementID = getClusterNode(name);
    if ((elementID != null) && (elementID.equals(EMPTY_VALUE) || elementID.equals(nodeID))) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Checks if the given ObjectName is a remote name. This is true if either:
   * 
   * <ul>
   * <li>
   * the <var>name</var> designates a remote cluster node, i.e. includes the <code>j2eeType</code>
   * key, the value of that key equals <code>SAP_J2EEClusterNode</code> and the value of the
   * <code>name</code> key does equal neither the given <var>nodeID</var> nor
   * <code>EMPTY_VALUE</code>.
   * </li>
   * <li>
   * the <var>name</var> designates a child of a remote cluster node, i.e. includes the
   * <code>j2eeType</code> key and a <code>SAP_J2EEClusterNode</code> key the value of the which
   * is equal to neither the given <var>nodeID</var> nor <code>EMPTY_VALUE</code>.
   * </li>
   * </ul>
   * 
   * 
   * @param name The ObjectName to be checked.
   * @param nodeID The local cluster node ID to be compared with.
   * 
   * @return <code>true</code> if name fulfills the conditions above, <code>false</code> otherwise.
   */
  public static boolean isJ2eeRemoteName(ObjectName name, String nodeID) {
    if (name == null) {
      return false;
    }
    String j2eeType = getJ2eeType(name);

    // the name of the remote cluster node
    if (SAP_J2EEClusterNode.equals(j2eeType)) {
      String nameValue = getName(name);
      if ((nameValue != null) && !nameValue.equals(EMPTY_VALUE) && !nameValue.equals(nodeID)) {
        return true;
      }
      else {
        return false;
      }
    }

    // a child of the remote cluster node
    String elementID = getClusterNode(name);
    if ((elementID != null) && !elementID.equals(EMPTY_VALUE) && !elementID.equals(nodeID)) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Checks if the given ObjectName is a name of a clustered MBean. This is true if:
   * 
   * <ul>
   * <li>
   * the <var>name</var> does not designate a cluster node, i.e. the <code>j2eeType</code> key has
   * a value different from <code>SAP_J2EEClusterNode</code>.
   * </li>
   * <li>
   * the <var>name</var> does not designate a child of a cluster node, i.e. does nor include a
   * <code>SAP_J2EEClusterNode</code> key.
   * </li>
   * </ul>
   * 
   * 
   * @param name The ObjectName to be checked.
   * 
   * @return <code>true</code> if name fulfills the conditions above, <code>false</code> otherwise.
   */
  public static boolean isJ2eeClusteredName(ObjectName name) {
    if (name == null) {
      return true;
    }
    String j2eeType = getJ2eeType(name);

    // neither a cluster node nor a child
    if (!SAP_J2EEClusterNode.equals(j2eeType) && (getClusterNode(name) == null)) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Returns the value of the <code>SAP_J2EEClusterNode</code> key or the value of the
   * <code>name</code> key in case of <code>j2eeType</code> contains
   * <code>SAP_J2EEClusterNode</code>.
   * 
   * @param name An ObjectName.
   * 
   * @return The value of the <code>SAP_J2EEClusterNode</code> key or <code>null</code> if the key
   *         does not exist.
   */
  public static String getClusterNode(ObjectName name) {
  	if (name == null) {
  		return null;
  	}
    if (SAP_J2EEClusterNode.equals(getJ2eeType(name))) {
      return name.getKeyProperty(NAME_KEY);
    }
    else {
      return name.getKeyProperty(SAP_J2EEClusterNode);
    }
  }

  /**
   * Returns the value of the <code>j2eeType</code> key.
   * 
   * @param name An ObjectName.
   * 
   * @return The value of the <code>j2eeType</code> key or <code>null</code> if the key does not
   *         exist.
   */
  public static String getJ2eeType(ObjectName name) {
		if (name == null) {
			return null;
		}
		else {
      return name.getKeyProperty(J2EETYPE_KEY);
		}
  }

  /**
   * Returns the value of the <code>name</code> key.
   * 
   * @param name An ObjectName.
   * 
   * @return The value of the <code>name</code> key or <code>null</code> if the key does not exist.
   */
  public static String getName(ObjectName name) {
		if (name == null) {
			return null;
		}
		else {
      return name.getKeyProperty(NAME_KEY);
		}
  }

  /**
   * Returns the value of the <code>SAP_J2EECluster</code> key.
   * 
   * @param name An ObjectName.
   * 
   * @return The value of the <code>SAP_J2EECluster</code> key or <code>null</code> if the key does not exist.
   */
  public static String getClusterName(ObjectName name) {
		if (name == null) {
			return null;
		}
		else {
      return name.getKeyProperty(SAP_J2EECluster);
		}
  }

  /**
   * Returns a J2EE ObjectName for a class loader of the SAP J2EE Engine. The name can be used in
   * {@link javax.management.MBeanServerConnection#createMBean(String, ObjectName, ObjectName)
   * <code>createMBean</code>} calls.
   * 
   * @param name The name used to reference the class loader. The name will be quoted.
   * @param clusterID value of the <code>SAP_J2EEClusterNode</code> key. A <code>null</code> value
   *        is substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * @param serverName value of the <code>SAP_J2EECluster</code> key. A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * 
   * @return ObjectName the JMX object name of the class loader.
   * 
   * @throws MalformedObjectNameException if the <var>name</var> was <code>null</code>.
   */
  public static ObjectName getNameForClassLoader(String name, String clusterID, String serverName)
    throws MalformedObjectNameException {
    return getNameForServerChildPerNode(
      SAP_J2EEClassLoader,
      '"' + name + '"',
      clusterID,
      serverName);
  }

  /**
   * Returns a J2EE ObjectName for a monitor MBean.
   * 
   * @param name The name used to identify the monitor. The name has a path like syntax where the 
   *        path elements are separated by a slash. Each element in that path corresponds to a node
   *        in the monitor tree. Therefore, it is easy to calculate the name of the parent node in
   *        the tree for a given name.
   * @param clusterID value of the <code>SAP_J2EEClusterNode</code> key. A <code>null</code> value
   *        is substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * @param serverName value of the <code>SAP_J2EECluster</code> key. A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * 
   * @return ObjectName the JMX object name of the monitor MBean.
   * 
   * @throws MalformedObjectNameException if the <var>name</var> was <code>null</code>.
   */
  public static ObjectName getNameForMonitorPerNode(
    String name,
    String clusterID,
    String serverName)
    throws MalformedObjectNameException {
    if ((name == null)) {
      throw new MalformedObjectNameException("ObjectName can not be created. Name parameter must not be null.");
    }

    if (serverName == null) {
      serverName = EMPTY_VALUE;
    }

    if (clusterID == null) {
      clusterID = EMPTY_VALUE;
    }

    Hashtable table = new Hashtable();
    table.put(J2EETYPE_KEY, SAP_MonitorPerNode);
    table.put(NAME_KEY, name);
    table.put(SAP_J2EEClusterNode, clusterID);
    table.put(SAP_J2EECluster, serverName);

    return new ObjectName(DEFAULT_DOMAIN, table);
  }

  /**
   * Returns a J2EE ObjectName for a resource MBean provided by an application to be
   * monitored by a monitor MBean.
   * @param resourceName The name used to identify the resource. The name has to be
   *        unique within the application specified by <var>applicationName</var>.
   * @param applicationName The name of the application, the resource belongs to.
   * @param clusterID value of the <code>SAP_J2EEClusterNode</code> key. A <code>null</code> value
   *        is substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * @param serverName value of the <code>SAP_J2EECluster</code> key. A <code>null</code> value is
   *        substituted by {@link #EMPTY_VALUE EMPTY_VALUE}.
   * @return ObjectName the JMX object name of the application resource MBean.
   * @throws MalformedObjectNameException The parameters do not have the right format.
   */
  public static ObjectName getNameForApplicationResourcePerNode(
    String resourceName,
  	String applicationName,
    String clusterID,
    String serverName)
    throws MalformedObjectNameException {
    return getNameForApplicationChildPerNode(
      SAP_ApplicationResourcePerNode,
      resourceName,
      applicationName,
      clusterID,
      serverName);
  }

  //RK not used by monitoring
  //  /**
  //   * Returns the ObjectName of the parent monitor.
  //   * @param objectName the name of a monitor MBean
  //   * @return ObjectName the name of the monitor MBean that represents the parent node in the monitor
  //   *         tree for the given <var>objectName</var>
  //   * @throws JmxIllegalArgumentException
  //   */
  //  public static ObjectName getNameOfParentMonitorPerNode(ObjectName objectName) {
  //    if (objectName == null) {
  //      throw new JmxIllegalArgumentException(JmxIllegalArgumentException.MONITOR_NAME_NULL);
  //    }
  //    String name = getName(objectName);
  //    if (!SAP_MonitorPerNode.equals(getJ2eeType(objectName)) || name == null) {
  //      throw new JmxIllegalArgumentException(
  //        JmxIllegalArgumentException.PARAMETER_$1_VALUE_$2_BUT_DOES_NOT_CONTAIN_KEY_$3,
  //        new Object[] { "objectName", objectName, J2EETYPE_KEY + "=" + SAP_MonitorPerNode });
  //    }
  //    Hashtable keys = (Hashtable) objectName.getKeyPropertyList().clone();
  //    int lastSepIndex = name.lastIndexOf(MONITOR_PATH_SEPARATOR);
  //    keys.put(NAME_KEY, name.substring(0, lastSepIndex <= 0 ? name.length() : lastSepIndex));
  //    ObjectName parentName = null;
  //    try {
  //      parentName = new ObjectName(objectName.getDomain(), keys);
  //    }
  //    catch (MalformedObjectNameException ignored) {
  //    }
  //    return parentName;
  //  }

  // RK not used by monitoring
  //  /**
  //   * Returns the ObjectName for a child monitor of the given parent monitor MBean (<var>parentName</var>).
  //   * @param parentName the name of the parent monitor MBean
  //   * @param treeNodeName the node name of the child node
  //   * @param monitorType indicates the data type that can be monitored.
  //   * @return ObjectName the ObjectName representing the child monitor node.
  //   */
  //  public static ObjectName getNameOfChildMonitorPerNode(
  //    ObjectName parentName,
  //    String treeNodeName,
  //    String monitorType) {
  //    // check valid tree parameters
  //    if (parentName == null) {
  //      throw new JmxIllegalArgumentException(JmxIllegalArgumentException.MONITOR_NAME_NULL);
  //    }
  //    String name = getName(parentName);
  //    if (!SAP_MonitorPerNode.equals(getJ2eeType(parentName)) || name == null) {
  //      throw new JmxIllegalArgumentException(
  //        JmxIllegalArgumentException.PARAMETER_$1_VALUE_$2_BUT_DOES_NOT_CONTAIN_KEY_$3,
  //        new Object[] { "parentName", parentName, J2EETYPE_KEY + "=" + SAP_MonitorPerNode });
  //    }
  //    if (treeNodeName == null
  //      || treeNodeName.length() == 0
  //      || treeNodeName.indexOf(MONITOR_PATH_SEPARATOR) >= 0) {
  //      throw new JmxIllegalArgumentException(
  //        JmxIllegalArgumentException
  //          .PARAMETER_$1_VALUE_$2_ILLEGAL_MONITOR_NAME_MUST_NOT_CONTAIN_CHAR_$3,
  //        new Object[] { "treeNodeName", treeNodeName, String.valueOf(MONITOR_PATH_SEPARATOR)});
  //    }
  //    if (monitorType == null) {
  //      throw new JmxIllegalArgumentException(JmxIllegalArgumentException.MONITOR_TYPE_NULL);
  //    }
  //    // clone parent name and append tree node name
  //    Hashtable keys = (Hashtable) parentName.getKeyPropertyList().clone();
  //    keys.put(NAME_KEY, name + MONITOR_PATH_SEPARATOR + treeNodeName);
  //    keys.put(MONITOR_TYPE_KEY, monitorType);
  //    ObjectName childName = null;
  //    try {
  //      childName = new ObjectName(parentName.getDomain(), keys);
  //    }
  //    catch (MalformedObjectNameException ignored) {
  //    }
  //    return childName;
  //  }

}