/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.mbeaninfo;

import com.sap.pj.jmx.PropertyManager;
import com.sap.pj.jmx.introspect.MBeanIntrospector;
import com.sap.pj.jmx.introspect.MBeanIntrospectorFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import javax.management.*;
import java.util.Iterator;
import java.util.Properties;

/**
 * <p>
 * A wrapper for standard MBeans, very similar to
 * <code>{@link javax.management.StandardMBean StandardMBean}</code>
 * but allows for to provide arbitrary meta data in the
 * <code>MBeanInfo</code>. The client can access this data via the <code>{@link
 * com.sap.pj.jmx.mbeaninfo.AdditionalInfo AdditionalInfo}</code> interface.
 * </p>
 *
 * <p>
 * By making a DynamicMBean out of an MBean, this class makes it possible to select any interface
 * implemented by the MBean as its management interface, provided that it complies with JMX
 * patterns (i.e., attributes defined by getter/setter etc...).
 * </p>
 *
 * <p>
 * This class also provides hooks that make it possible to supply additional
 * <code>Properties</code> to the <code>{@link javax.management.MBeanInfo MBeanInfo}</code> and
 * its sub-objects (<code>MBeanXXXInfo</code>) returned by the DynamicMBean interface. For other
 * ways  to provide the meta data see <code>{@link com.sap.pj.jmx.mbeaninfo.StandardMBeanWrapper
 * StandardMBeanWrapper}</code>.
 * </p>
 *
 * <p>
 * Using this class, an MBean can be created with any implementation class name <i>Impl</i> and
 * with a management interface defined (as for current Standard MBeans) by any interface
 * <i>Intf</i>, in one of two general ways:
 * </p>
 *
 * <ul>
 * <li>
 * Using the public constructor {@link #AdditionalInfoProviderMBean(java.lang.Object,
        * java.lang.Class) <code>AdditionalInfoProviderMBean(impl,interface)</code>}:
 * <pre>
 *     MBeanServer mbs;
 *     ...
 *     Impl impl = new Impl(...);
 *     AdditionalInfoProviderMBean mbean = new AdditionalInfoProviderMBean(impl, Intf.class);
 *     mbs.registerMBean(mbean, objectName);
 * </pre>
 * </li>
 * <li>
 * Subclassing AdditionalInfoProviderMBean:
 * <pre>
 *     public class Impl extends AdditionalInfoProviderMBean implements Intf {
 *         public Impl() {
 *             super(Intf.class);
 *         }
 *         // implement methods of Intf
 *     }
 *     [...]
 *     MBeanServer mbs;
 *     ....
 *     Impl impl = new Impl();
 *     mbs.registerMBean(impl, objectName);
 * </pre>
 * </li>
 * </ul>
 *
 * <p>
 * In either case, the class <i>Impl</i> must implement the  interface <i>Intf</i>.
 * </p>
 *
 * <p>
 * The latter case is also useful if <i>Impl</i> wants to implement <code>MBeanRegistration</code>
 * or <code>NotificationBroadcaster</code> interface.
 * </p>
 *
 * <p></p>
 *
 * @author d025700
 * @see com.sap.pj.jmx.mbeaninfo.AdditionalInfo
 * @see javax.management.StandardMBean
 * @see com.sap.pj.jmx.mbeaninfo.StandardMBeanWrapper
 */
public class AdditionalInfoProviderMBean implements DynamicMBean {
  private static final Location LOCATION = Location.getLocation(AdditionalInfoProviderMBean.class);
  
  /** A marker object for passing <var>this</var> to another constructor. */
  private static final Object THIS = new Object() {
    // empty
  };

  /** Default string for impact property. */
  private static final String IMPACT_DEFAULT_VAL = String.valueOf(MBeanOperationInfo.UNKNOWN);

  /** Default string for name property. */
  private static final String NAME_DEFAULT_VAL = ""; //$NON-NLS-1$

  /** The introspector used to generate the MBeanInfo and to invoke methods of the wrapped MBean. */
  private final MBeanIntrospector introspector;

  /** The cached MBeanInfo. */
  private MBeanInfo cachedMBeanInfo;

  /** The factory to get the introspector and invoker from. */
  private static MBeanIntrospectorFactory introspectorFactory = null;

  /**
   * Make a DynamicMBean out of the object <var>implementation</var>, using the specified
   * <var>mbeanInterface</var> class.
   *
   * @param implementation The implementation of this MBean.
   * @param mbeanInterface The Management Interface exported by this MBean's implementation. If
   *        <code>null</code>, then this object will use standard JMX design pattern to determine
   *        the management interface associated with the given implementation.
   *
   * @throws NotCompliantMBeanException if the <var>mbeanInterface</var> does not follow JMX design
   *         patterns for Management Interfaces, or if the given <var>implementation</var> does
   *         not implement the specified interface.
   */
  public AdditionalInfoProviderMBean(Object implementation, Class mbeanInterface)
          throws NotCompliantMBeanException {
    if (implementation == THIS) {
      implementation = this;
    }
    if (introspectorFactory == null) {
      String className = PropertyManager.getMBeanIntrospectorFactoryClass();
      Class clazz = null;

      try {
        clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
      } catch (ClassNotFoundException ignored) { //$JL-EXC$
      }

      if (clazz == null) {
        try {
          clazz = MBeanServer.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException ignored) { //$JL-EXC$
        }
      }

      if ((clazz != null) && MBeanIntrospectorFactory.class.isAssignableFrom(clazz)) {
        try {
          introspectorFactory = (MBeanIntrospectorFactory) clazz.newInstance();
        } catch (Exception e) {
          introspectorFactory = null;
        }
      }
      if (introspectorFactory == null) {
        throw new IllegalStateException("Unable to load MBeanIntrospectorFactory: " + className);
      }
    }
    introspector = introspectorFactory.getMBeanIntrospector(implementation, mbeanInterface);
  }

  /**
   * Make a DynamicMBean out of <var>this</var>, using the specified <var>mbeanInterface</var>
   * class.
   *
   * <p>
   * Call {@link #AdditionalInfoProviderMBean(java.lang.Object, java.lang.Class) <code>this(this,
   * mbeanInterface)</code>}. This constructor is reserved to subclasses.
   * </p>
   *
   * @param mbeanInterface The Management Interface exported by this MBean.
   *
   * @throws NotCompliantMBeanException if the <var>mbeanInterface</var> does not follow JMX design
   *         patterns for Management Interfaces, or if <var>this</var> does not implement the
   *         specified interface.
   */
  protected AdditionalInfoProviderMBean(Class mbeanInterface) throws NotCompliantMBeanException {
    // pass a marker since this(this, mbeanInterface) is not allowed
    this(THIS, mbeanInterface);
  }

  /**
   * Replace the implementation object wrapped in this object.
   *
   * @param implementation The new implementation of this MBean. The <var>implementation</var>
   *        object must implement the MBean interface that was supplied when this
   *        <code>StandardMBean</code> was constructed.
   *
   * @throws NotCompliantMBeanException if the given <var>implementation</var> does not implement
   *         the MBean interface that was supplied at construction.
   */
  public void setImplementation(Object implementation) throws NotCompliantMBeanException {
    synchronized (introspector) {
      introspector.setImplementation(implementation);
    }
    cacheMBeanInfo(null);
  }

  /**
   * Get the implementation of this MBean.
   *
   * @return The implementation of this MBean.
   */
  public Object getImplementation() {
    return introspector.getImplementation();
  }

  /**
   * Get the Management Interface of this MBean.
   *
   * @return The management interface of this MBean.
   */
  public final Class getMBeanInterface() {
    return introspector.getMBeanInterface();
  }

  /**
   * Get the class of the implementation of this MBean.
   *
   * @return The class of the implementation of this MBean.
   */
  public Class getImplementationClass() {
    return introspector.getImplementation().getClass();
  }

  /**
   * @see javax.management.DynamicMBean#getAttribute(String)
   */
  public Object getAttribute(String attribute)
          throws AttributeNotFoundException, MBeanException, ReflectionException {
    return introspector.getInvoker().getAttribute(introspector.getImplementation(), attribute);
  }

  /**
   * @see javax.management.DynamicMBean#setAttribute(Attribute)
   */
  public void setAttribute(Attribute attribute)
          throws
          AttributeNotFoundException,
          InvalidAttributeValueException,
          MBeanException,
          ReflectionException {
    introspector.getInvoker().setAttribute(introspector.getImplementation(), attribute);
  }

  /**
   * @see javax.management.DynamicMBean#getAttributes(String[])
   */
  public AttributeList getAttributes(String[] attributes) {
    // assert(attributes != null);
    AttributeList returnList = new AttributeList(attributes.length);

    for (int i = 0; i < attributes.length; i++) {
      try {
        returnList.add(new Attribute(attributes[i], getAttribute(attributes[i])));
      } catch (Exception e) {
        LOCATION.traceThrowableT(Severity.PATH, "getAttributes()", "unable to get MBean attribute " + attributes[i], e);
      }
    }

    return returnList;
  }

  /**
   * @see javax.management.DynamicMBean#setAttributes(AttributeList)
   */
  public AttributeList setAttributes(AttributeList attributes) {
    AttributeList returnList = new AttributeList(attributes.size());

    for (Iterator iter = attributes.iterator(); iter.hasNext();) {
      Attribute attribute = (Attribute) iter.next();
      try {
        setAttribute(attribute);
        returnList.add(attribute);
      } catch (Exception e) {
        LOCATION.traceThrowableT(Severity.PATH, "setAttributes()", "unable to set MBean attribute " + attribute.getName(), e);
      }
    }

    return returnList;
  }

  /**
   * @see javax.management.DynamicMBean#invoke(String, Object[], String[])
   */
  public Object invoke(String actionName, Object[] params, String[] signature)
          throws MBeanException, ReflectionException {
    return introspector.getInvoker().invoke(
            introspector.getImplementation(),
            actionName,
            params,
            signature);
  }

  /**
   * Get the MBeanInfo for this MBean.
   *
   * <p>
   * This method implements {@link javax.management.DynamicMBean#getMBeanInfo()
   * <code>DynamicMBean.getMBeanInfo()</code>}.
   * </p>
   *
   * <p>
   * This method first calls {@link #getCachedMBeanInfo() <code>getCachedMBeanInfo()</code>} in
   * order to retrieve the cached MBeanInfo for this MBean, if any. If the MBeanInfo returned by
   * {@link #getCachedMBeanInfo() <code>getCachedMBeanInfo()</code>} is not <code>null</code>,
   * then it is returned. Otherwise, this method builds a default MBeanInfo for this MBean, using
   * the management interface specified for this MBean.
   * </p>
   *
   * <p>
   * While building the MBeanInfo, this method calls the customization hooks that make it possible
   * for subclasses to supply their custom properties.<br>
   * Finally, it calls {@link #cacheMBeanInfo(MBeanInfo) <code>cacheMBeanInfo()</code>} in order
   * to cache the new MBeanInfo.
   * </p>
   *
   * @return An instance of <code>MBeanInfo</code> allowing all attributes and actions exposed by
   *         this Dynamic MBean to be retrieved.
   */
  public MBeanInfo getMBeanInfo() {
    MBeanInfo info = getCachedMBeanInfo();

    if (info == null) {
      // attributes
      MBeanAttributeInfo[] attrInfo = introspector.getAttributeInfo();

      for (int i = 0; i < attrInfo.length; i++) {
        Properties props = getProperties(attrInfo[i]);
        attrInfo[i] =
                new MBeanAttributeInfoSupport(
                        attrInfo[i].getName(),
                        attrInfo[i].getType(),
                        props.getProperty(AdditionalInfo.DESCRIPTION_KEY),
                        attrInfo[i].isReadable(),
                        attrInfo[i].isWritable(),
                        attrInfo[i].isIs(),
                        props);
      }

      // operations
      MBeanOperationInfo[] opInfo = introspector.getOperationInfo();

      for (int i = 0; i < opInfo.length; i++) {
        MBeanParameterInfo[] paramInfo = opInfo[i].getSignature();

        for (int j = 0; j < paramInfo.length; j++) {
          Properties props = getProperties(opInfo[i], paramInfo[j], j);
          paramInfo[j] =
                  new MBeanParameterInfoSupport(
                          props.getProperty(AdditionalInfo.NAME_KEY, NAME_DEFAULT_VAL),
                          paramInfo[j].getType(),
                          props.getProperty(AdditionalInfo.DESCRIPTION_KEY),
                          props);
        }

        Properties props = getProperties(opInfo[i]);
        opInfo[i] =
                new MBeanOperationInfoSupport(
                        opInfo[i].getName(),
                        props.getProperty(AdditionalInfo.DESCRIPTION_KEY),
                        paramInfo,
                        opInfo[i].getReturnType(),
                        Integer.parseInt(props.getProperty(AdditionalInfo.IMPACT_KEY, IMPACT_DEFAULT_VAL)),
                        props);
      }

      // chache non-final values in order to be thread safe
      MBeanConstructorInfo[] ctorInfo;
      MBeanNotificationInfo[] notfInfo;
      Object implementation;
      String className;
      synchronized (introspector) {
        ctorInfo = introspector.getConstructorInfo();
        notfInfo = introspector.getNotificationInfo();
        implementation = introspector.getImplementation();
        className = introspector.getClassName();
      }

      // constructors
      ctorInfo = getConstructors(introspector.getConstructorInfo(), implementation);

      if (ctorInfo != null) {
        for (int i = 0; i < ctorInfo.length; i++) {
          MBeanParameterInfo[] paramInfo = ctorInfo[i].getSignature();

          for (int j = 0; j < paramInfo.length; j++) {
            Properties props = getProperties(ctorInfo[i], paramInfo[j], j);
            paramInfo[j] =
                    new MBeanParameterInfoSupport(
                            props.getProperty(AdditionalInfo.NAME_KEY, NAME_DEFAULT_VAL),
                            paramInfo[j].getType(),
                            props.getProperty(AdditionalInfo.DESCRIPTION_KEY),
                            props);
          }

          Properties props = getProperties(ctorInfo[i]);
          ctorInfo[i] =
                  new MBeanConstructorInfoSupport(
                          ctorInfo[i].getName(),
                          props.getProperty(AdditionalInfo.DESCRIPTION_KEY),
                          paramInfo,
                          props);
        }
      }

      // intermediate mbean info in order to get class name and description
      info = new MBeanInfo(className, null, attrInfo, ctorInfo, opInfo, notfInfo);

      Properties props = getProperties(info);

      // MBean info
      info =
              new MBeanInfoSupport(
                      className,
                      props.getProperty(AdditionalInfo.DESCRIPTION_KEY),
                      attrInfo,
                      ctorInfo,
                      opInfo,
                      notfInfo,
                      props);
      cacheMBeanInfo(info);
    }

    return info;
  }

  /**
   * Customization hook: Get the properties that will be used in the MBeanInfo returned by this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom MBean meta data. The
   * default implementation returns a <code>Properties</code> table with the following (key,
   * value) pairs:  <code>(AdditionalInfo.DESCRIPTION_KEY, info.getDescription())</code>.
   *
   * @param info The default MBeanInfo derived by reflection.
   *
   * @return properties for the new MBeanInfo.
   */
  protected Properties getProperties(MBeanInfo info) {
    Properties props = new Properties();
    if (info.getDescription() != null) {
      props.put(AdditionalInfo.DESCRIPTION_KEY, info.getDescription());
    }
    return props;
  }

  /**
   * Customization hook: Get the properties that will be used in the MBeanAttributeInfo returned by
   * this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom meta data. The default
   * implementation returns a <code>Properties</code> table with the following (key, value) pair:
   * <code>(AdditionalInfo.DESCRIPTION_KEY, info.getDescription())</code>.
   *
   * @param info The default MBeanAttributeInfo derived by reflection.
   *
   * @return the properties for the given MBeanAttributeInfo.
   */
  protected Properties getProperties(MBeanAttributeInfo info) {
    Properties props = new Properties();
    if (info.getDescription() != null) {
      props.put(AdditionalInfo.DESCRIPTION_KEY, info.getDescription());
    }
    return props;
  }

  /**
   * Customization hook: Get the properties that will be used in the MBeanConstructorInfo returned
   * by this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom meta data. The default
   * implementation returns a <code>Properties</code> table with the following (key, value) pair:
   * <code>(AdditionalInfo.DESCRIPTION_KEY, info.getDescription())</code>.
   *
   * @param info The default MBeanConstructorInfo derived by reflection.
   *
   * @return the properties for the given MBeanConstructorInfo.
   */
  protected Properties getProperties(MBeanConstructorInfo info) {
    Properties props = new Properties();
    if (info.getDescription() != null) {
      props.put(AdditionalInfo.DESCRIPTION_KEY, info.getDescription());
    }
    return props;
  }

  /**
   * Customization hook: Get the properties that will be used for the <var>sequence</var>
   * MBeanParameterInfo of the MBeanConstructorInfo returned by this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom meta data. The default
   * implementation returns a <code>Properties</code> table with the following (key, value) pairs:
   * <code>(AdditionalInfo.DESCRIPTION_KEY, param.getDescription())</code>,
   * <code>(AdditionalInfo.NAME_KEY, param.getName())</code>.
   *
   * @param ctor The default MBeanConstructorInfo derived by reflection.
   * @param param The default MBeanParameterInfo derived by reflection.
   * @param sequence The sequence number of the parameter considered ("0" for the first parameter,
   *        "1" for the second parameter, etc...).
   *
   * @return the properties for the given MBeanParameterInfo.
   */
  protected Properties getProperties(
          MBeanConstructorInfo ctor,
          MBeanParameterInfo param,
          int sequence) {
    Properties props = new Properties();
    if (param.getDescription() != null) {
      props.put(AdditionalInfo.DESCRIPTION_KEY, param.getDescription());
    }
    if (param.getName() != null) {
      props.put(AdditionalInfo.NAME_KEY, param.getName());
    }
    return props;
  }

  /**
   * Customization hook: Get the properties that will be used in the MBeanOperationInfo returned by
   * this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom meta data. The default
   * implementation returns a <code>Properties</code> table with the following (key, value) pairs:
   * <code>(AdditionalInfo.DESCRIPTION_KEY, info.getDescription())</code>,
   * <code>(AdditionalInfo.IMPACT_KEY, info.getImpact())</code>.
   *
   * @param info The default MBeanOperationInfo derived by reflection.
   *
   * @return the properties for the given MBeanOperationInfo.
   */
  protected Properties getProperties(MBeanOperationInfo info) {
    Properties props = new Properties();
    if (info.getDescription() != null) {
      props.put(AdditionalInfo.DESCRIPTION_KEY, info.getDescription());
    }
    props.put(AdditionalInfo.IMPACT_KEY, String.valueOf(info.getImpact()));
    return props;
  }

  /**
   * Customization hook: Get the properties that will be used for the <var>sequence</var>
   * MBeanParameterInfo of the MBeanOperationInfo returned by this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom meta data. The default
   * implementation returns a <code>Properties</code> table with the following (key, value) pairs:
   * <code>(AdditionalInfo.DESCRIPTION_KEY, param.getDescription())</code>,
   * <code>(AdditionalInfo.NAME_KEY, param.getName())</code>.
   *
   * @param op The default MBeanOperationInfo derived by reflection.
   * @param param The default MBeanParameterInfo derived by reflection.
   * @param sequence The sequence number of the parameter considered ("0" for the first parameter,
   *        "1" for the second parameter, etc...).
   *
   * @return the properties for the given MBeanParameterInfo.
   */
  protected Properties getProperties(
          MBeanOperationInfo op,
          MBeanParameterInfo param,
          int sequence) {
    Properties props = new Properties();
    if (param.getDescription() != null) {
      props.put(AdditionalInfo.DESCRIPTION_KEY, param.getDescription());
    }
    if (param.getName() != null) {
      props.put(AdditionalInfo.NAME_KEY, param.getName());
    }
    return props;
  }

  /**
   * Customization hook: Get the MBeanConstructorInfo[] that will be used in the MBeanInfo returned
   * by this MBean.<br>
   * By default, this method returns <code>null</code> if the wrapped implementation is not
   * <var>this</var>. Indeed, if the wrapped implementation is not this object itself, it will not
   * be possible to recreate a wrapped implementation by calling the implementation constructors
   * through <code>MBeanServer.createMBean(...)</code>.<br>
   * Otherwise, if the wrapped implementation is <var>this, ctors</var> is returned.<br>
   * Subclasses may redefine this method in order to modify this behaviour, if needed.
   *
   * @param ctors The default MBeanConstructorInfo[] derived by reflection.
   * @param impl The wrapped implementation. If <code>null</code> is passed, the wrapped
   *        implementation is ignored and <var>ctors</var> is returned.
   *
   * @return the MBeanConstructorInfo[] for the new MBeanInfo.
   */
  protected MBeanConstructorInfo[] getConstructors(MBeanConstructorInfo[] ctors, Object impl) {
    if (impl != this) {
      return null;
    } else {
      return ctors;
    }
  }

  /**
   * Customization hook: Return the MBeanInfo cached for this object.<br>
   * Subclasses may redefine this method in order to implement their own caching policy. The
   * default implementation stores one <code>{@link javax.management.MBeanInfo MBeanInfo}</code>
   * object per instance.
   *
   * @return the cached MBeanInfo, or null if no MBeanInfo is cached.
   *
   * @see #cacheMBeanInfo(MBeanInfo)
   */
  protected MBeanInfo getCachedMBeanInfo() {
    return cachedMBeanInfo;
  }

  /**
   * Customization hook: cache the MBeanInfo built for this object.<br>
   * Subclasses may redefine this method in order to implement their own caching policy. The
   * default implementation stores <var>info</var> in this instance. A subclass can define other
   * policies,  such as not saving <var>info</var> (so it is reconstructed every time <code>{@link
   * #getMBeanInfo() getMBeanInfo()}</code> is called) or sharing a unique <code>{@link
   * javax.management.MBeanInfo MBeanInfo}</code> object when several
   * <code>StandardMBean</code> instances have equal <code>{@link javax.management.MBeanInfo
   * MBeanInfo}</code> values.
   *
   * @param info the new <code>MBeanInfo</code> to cache. Any previously cached value is discarded.
   *        This parameter may be null, in which case there is no new cached value.
   *
   * @see #getCachedMBeanInfo()
   */
  protected void cacheMBeanInfo(MBeanInfo info) {
    cachedMBeanInfo = info;
  }
}