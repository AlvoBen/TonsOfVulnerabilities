/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.mbeaninfo;

import javax.management.*;
import java.util.Properties;


/**
 * <p>
 * The <code>StandardMBeanWrapper</code> can be used by resources that want to provide additional
 * meta data for standard MBeans. The wrapper itself is a <code>{@link javax.management.DynamicMBean
 * DynamicMBean}</code> and has to be registered with the <code>{@link
 * javax.management.MBeanServer MBeanServer}</code> instead of the original MBean. The
 * latter is passed as a parameter to the wrapper in conjunction with additional meta data.
 * </p>
 *
 * <p>
 * The meta date can be a simple <code>{@link java.util.Properties Properties}</code> object which
 * applies to the whole MBean only:
 * <pre>
 *  ...
 *  Properties mbeanMetaData = new Properties();
 *  mbeanMetaData.setProperty("com.sap.aKey", "a value");
 *  MyObj mbean = new MyObj();
 *  StandardMBeanWrapper wrapper = new StandardMBeanWrapper(mbean, null, mbeanMetaData);
 *  mbeanServer.registerMBean(wrapper, objectName);
 *  ...
 * </pre>
 * </p>
 *
 * <p>
 * or a complex <code>MBeanInfo</code> skeleton which provides meta data for a single attribute,
 * operation, or even a parameter of an operation. <i>MBeanInfo skeleton</i>, means that only
 * those attribues are filled with values that cannot be generated via introspection, for example
 * a description, or that are necessary to identify the iteme they apply to, e.g. the name of an
 * attribute. <code>NotificationInfo</code> is ignored since there is general a way for all MBeans
 * to provide it via the <code>{@link javax.management.NotificationBroadcaster NotificationBroadcaster}</code>
 * interface:
 * <pre>
 * ...
 *  //
 *  // describe an attribute
 *  //
 *  Properties attributeMetaData = new Properties();
 *  attributeMetaData.setProperty(AdditionalInfo.DESCRIPTION_KEY, "Description of the attribute.");
 *  MBeanAttributeInfo attributeInfo =
 *      new MBeanAttributeInfoSupport("AttibuteA", null, attributeMetaData);
 *  //
 *  // describe an operation
 *  //
 *  Properties parameterMetaData = new Properties();
 *  parameterMetaData.setProperty(AdditionalInfo.NAME_KEY, "param1");
 *  MBeanParameterInfo[] parametersInfo = new MBeanParameterInfo[1];
 *  parametersInfo[0] =
 *      new MBeanParameterInfoSupport("", java.lang.String[].class.getName(), null, parameterMetaData);
 *  Properties operationMetaData = new Properties();
 *  operationMetaData.setProperty("com.sap.bKey", "another value");
 *  MBeanOperationInfo operationInfo =
 *      new MBeanOperationInfoSupport("operationB", null, parametersInfo, operationMetaData);
 *  //
 *  // describe the MBean
 *  //
 *  Properties mbeanMetaData = new Properties();
 *  mbeanMetaData.setProperty("com.sap.aKey", "a value");
 *  MBeanInfo mbeanInfo =
 *      new MBeanInfoSupport(
 *          null,
 *          new MBeanAttributeInfo[] { attributeInfo },
 *          null,
 *          new MBeanOperationInfo[] { operInfo },
 *          null,
 *          mbeanMetaData);
 *  //
 *  // register the MBean
 *  //
 *  MyObj mbean = new MyObj();
 *  StandardMBeanWrapper wrapper = new StandardMBeanWrapper(mbean, null, mBeanMetaData);
 *  mbeanServer.registerMBean(wrapper, objectName);
 * ...
 * </pre>
 * </p>
 *
 * <p>
 * The <code>{@link javax.management.DynamicMBean#getMBeanInfo() getMBeanInfo()}</code> method of
 * the <var>wrapper</var> returns a <code>{@link javax.management.MBeanInfo MBeanInfo}</code>
 * which implements the <code>{@link com.sap.pj.jmx.mbeaninfo.AdditionalInfo
 * AdditionalInfo}</code> interface (the same is true for all <code>MBeanXXXInfo</code>
 * sub-objects of this <code>MBeanInfo</code>). The <code>MBeanInfo</code> is built up by merging
 * meta data from introspection of the given <var>mbean</var> and the additional
 * <var>mbeanMetaData</var> provided.
 * </p>
 *
 * <p>
 * During the merge only the values that cannot be retrieved via introspection are taken from the
 * <var>mbeanMetaData</var>. In particular that are all descriptions, names of operations and
 * constructor parameters, the impact of operations, and all properties in case of <code>{@link
 * com.sap.pj.jmx.mbeaninfo.AdditionalInfo AdditionalInfo}</code> interface is implemented
 * by the meta data. As shown by the sample code, all meta data can be provided via properties
 * (there already exist sub classes for all MBeanXXXInfo classes, e.g. <code>{@link MBeanInfoSupport
 * MBeanInfoSupport}</code>, that implement the <code>AdditionalInfo</code> interface).
 * There exist the follwing predefined keys <code>"javax.management.Description"</code>,
 * <code>"javax.management.Name"</code>, <code>"javax.management.Impact"</code> that are
 * recognized by the <var>wrapper</var> and are used to fill the respective fields in the
 * <code>MBeanXXXInfo</code>. Note that those property values take precedence over the normal
 * <code>MBeanInfo</code> attributes.
 * </p>
 *
 * <p>
 * In the following cases it might be appropriate to subclass <code>StandardMBeanWrapper</code>
 * instead of passing the <var>mbean</var> as a parameter to the constructor:
 *
 * <ul>
 * <li>
 * the <var>mbean</var> implements the <code>{@link javax.management.MBeanRegistration
 * MBeanRegistration}</code> interface or the <code>{@link
 * javax.management.NotificationBroadcaster NotificationBroadcaster}</code> interface.
 * </li>
 * <li>
 * the <var>mbean</var> has to be registered by calling <code>MBeanServer.createMBean(...)</code>
 * since it will not be possible to recreate a wrapped implementation by the
 * <code>MBeanServer</code>.
 * </li>
 * </ul>
 *
 * A subclass can look like this:
 * <pre>
 *     public class MyMBean extends StandardMBeanWrapper implements AnMBeanInterface {
 *         public MyMBean() {
 *             super(AnMBeanInterface.class);
 *         }
 *         // implement methods of Intf
 *     }
 * </pre>
 * </p>
 *
 * @author d025700
 * @see com.sap.pj.jmx.mbeaninfo.AdditionalInfo
 * @see com.sap.pj.jmx.mbeaninfo.MBeanAttributeInfoSupport
 * @see com.sap.pj.jmx.mbeaninfo.MBeanConstructorInfoSupport
 * @see com.sap.pj.jmx.mbeaninfo.MBeanInfoSupport
 * @see com.sap.pj.jmx.mbeaninfo.MBeanNotificationInfoSupport
 * @see com.sap.pj.jmx.mbeaninfo.MBeanOperationInfoSupport
 * @see com.sap.pj.jmx.mbeaninfo.MBeanParameterInfoSupport
 */
public class StandardMBeanWrapper extends AdditionalInfoProviderMBean {
  /** The additional info provided via the constructor. */
  private MBeanInfo addInfo;

  /**
   * Creates a new StandardMBeanWrapper object. The meta data from <code>info</code> is merged with
   * the data retrieved by introspection of the given <code>implementation</code>.
   *
   * @param implementation The instance of the standard MBean to be wrapped.
   * @param mbeanInterface The management interface exported by this MBean's implementation. If
   *        <code>null</code>, then this object will use standard JMX design pattern to determine
   *        the management interface associated with the given implementation. Otherwise the
   *        <var>implementation</var> is required to implement this interface.
   * @param info Additional meta data for the standard MBean.
   *
   * @throws NotCompliantMBeanException if the management interface does not follow JMX design
   *         patterns for Management Interfaces, or if the given <var>implementation</var> does
   *         not implement the specified interface.
   */
  public StandardMBeanWrapper(Object implementation, Class mbeanInterface, MBeanInfo info)
          throws NotCompliantMBeanException {
    super(implementation, mbeanInterface);
    addInfo = info;
  }

  /**
   * Creates a new StandardMBeanWrapper object. The meta data from <code>info</code> is merged with
   * the data retrieved by introspection of <code>this</code> object.  This constructor is
   * reserved to subclasses.
   *
   * @param mbeanInterface The management interface exported by this MBean's implementation. If
   *        <code>null</code>, then this object will use standard JMX design pattern to determine
   *        it's management interface. Otherwise it is required to implement this interface.
   * @param info Additional meta data for the standard MBean.
   *
   * @throws NotCompliantMBeanException if the management interface does not follow JMX design
   *         patterns for Management Interfaces, or <code>this</code> object does not implement
   *         the specified interface.
   */
  protected StandardMBeanWrapper(Class mbeanInterface, MBeanInfo info)
          throws NotCompliantMBeanException {
    super(mbeanInterface);
    addInfo = info;
  }

  /**
   * Creates a new StandardMBeanWrapper object. The <code>properties</code> are added to the
   * <code>MBeanInfo</code> retrieved by introspection of the given <code>implementation</code>.
   * This is a shortcut for {@link #StandardMBeanWrapper(Object, Class, MBeanInfo)
   * <code>StandardMBeanWrapper(implementation, interface, new MBeanInfoSupport(null, null, null,
   * null, properties))} which can be used in situations where only the MBean itself an not a
   * particular attribute or operation requires additional meta data.
   *
   * @param implementation The instance of the standard MBean to be wrapped.
   * @param mbeanInterface The management interface exported by this MBean's implementation. If
   *        <code>null</code>, then this object will use standard JMX design pattern to determine
   *        the management interface associated with the given implementation. Otherwise the
   *        <var>implementation</var> is required to implement this interface.
   * @param properties A set of additional properties that describe the MBean.
   *
   * @throws NotCompliantMBeanException if the management interface does not follow JMX design
   *         patterns for Management Interfaces, or if the given <var>implementation</var> does
   *         not implement the specified interface.
   */
  public StandardMBeanWrapper(Object implementation, Class mbeanInterface, Properties properties)
          throws NotCompliantMBeanException {
    /*this(implementation, mbeanInterface,
         new MBeanInfoSupport(null, null, null, null, null, properties));*/

    this(implementation, mbeanInterface,
            new MBeanInfoSupport(implementation.getClass().getName(), null, null, null, null, null, properties));
  }

  /**
   * Creates a new StandardMBeanWrapper object. The <code>properties</code> are added to the
   * <code>MBeanInfo</code> retrieved by introspection of <code>this</code> object. This is a
   * shortcut for {@link #StandardMBeanWrapper(Class, MBeanInfo)
   * <code>StandardMBeanWrapper(interface, new MBeanInfoSupport(null, null, null, null,
   * properties))} which can be used in situations where only the MBean itself an not a particular
   * attribute or operation requires additional meta data. This constructor is reserved to
   * subclasses.
   *
   * @param mbeanInterface The management interface exported by this MBean's implementation. If
   *        <code>null</code>, then this object will use standard JMX design pattern to determine
   *        it's management interface. Otherwise it is required to implement this interface.
   * @param properties A set of additional properties that describe the MBean.
   *
   * @throws NotCompliantMBeanException if the management interface does not follow JMX design
   *         patterns for Management Interfaces, or <code>this</code> object does not implement
   *         the specified interface.
   */
  protected StandardMBeanWrapper(Class mbeanInterface, Properties properties)
          throws NotCompliantMBeanException {
    this(mbeanInterface, (MBeanInfo) null);
    addInfo = new MBeanInfoSupport(this.getClass().getName(), null, null, null, null, null, properties);
  }

  /**
   * Customization hook: Get the properties that will be used in the MBeanAttributeInfo returned by
   * this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom properties. The default
   * implementation returns the meta data (description, properties) from the matching
   * MBeanAttributeInfo provided to the constructor. If no matching MBeanAttributeInfo was found
   * it returns an empty property table.
   *
   * @param info The default MBeanAttributeInfo derived by reflection.
   *
   * @return the additional properties for the given MBeanAttributeInfo.
   */
  protected Properties getProperties(MBeanAttributeInfo info) {
    Properties props = new Properties();
    MBeanAttributeInfo addAttrInfo = MBeanInfoUtilities.getAttributeInfo(info.getName(),
            addInfo.getAttributes());

    if (addAttrInfo != null) {
      String descr = addAttrInfo.getDescription();

      if (descr != null) {
        props.setProperty(AdditionalInfo.DESCRIPTION_KEY, descr);
      }

      if (addAttrInfo instanceof AdditionalInfo) {
        props.putAll(((AdditionalInfo) addAttrInfo).getProperties());
      }
    }

    return props;
  }

  /**
   * Customization hook: Get the properties that will be used in the MBeanConstructorInfo returned
   * by this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom properties. The default
   * implementation returns the meta data (description, parameter names, properties) from the
   * matching MBeanConstructorInfo provided to the constructor. If no matching
   * MBeanConstructorInfo was found it returns an empty property table.
   *
   * @param ctor The default MBeanConstructorInfo derived by reflection.
   * @param param The default MBeanParameterInfo derived by reflection.
   * @param sequence The sequence number of the parameter considered ("0" for the first parameter,
   *        "1" for the second parameter, etc...).
   *
   * @return the additional properties for the given MBeanConstructorInfo.
   */
  protected Properties getProperties(MBeanConstructorInfo ctor, MBeanParameterInfo param,
                                     int sequence) {
    Properties props = new Properties();
    MBeanConstructorInfo addCtorInfo = MBeanInfoUtilities.getConstructorInfo(ctor.getName(),
            ctor.getSignature(),
            addInfo.getConstructors());
    MBeanParameterInfo addParamInfo = (addCtorInfo == null)
            ? null : addCtorInfo.getSignature()[sequence];

    if (addParamInfo != null) {
      props.setProperty(AdditionalInfo.NAME_KEY, addParamInfo.getName());

      String descr = addParamInfo.getDescription();

      if (descr != null) {
        props.setProperty(AdditionalInfo.DESCRIPTION_KEY, descr);
      }

      if (addParamInfo instanceof AdditionalInfo) {
        props.putAll(((AdditionalInfo) addParamInfo).getProperties());
      }
    }

    return props;
  }

  /**
   * Customization hook: Get the properties that will be used in the MBeanConstructorInfo returned
   * by this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom properties. The default
   * implementation returns the meta data (description, properties) from the matching
   * MBeanConstructorInfo provided to the constructor. If no matching MBeanConstructorInfo was
   * found it returns an empty property table.
   *
   * @param info The default MBeanConstructorInfo derived by reflection.
   *
   * @return the additional properties for the given MBeanConstructorInfo.
   */
  protected Properties getProperties(MBeanConstructorInfo info) {
    Properties props = new Properties();
    MBeanConstructorInfo addCtorInfo = MBeanInfoUtilities.getConstructorInfo(info.getName(),
            info.getSignature(),
            addInfo.getConstructors());

    if (addCtorInfo != null) {
      String descr = addCtorInfo.getDescription();

      if (descr != null) {
        props.setProperty(AdditionalInfo.DESCRIPTION_KEY, descr);
      }

      if (addCtorInfo instanceof AdditionalInfo) {
        props.putAll(((AdditionalInfo) addCtorInfo).getProperties());
      }
    }

    return props;
  }

  /**
   * Customization hook: Get the properties that will be used in the MBeanInfo returned by this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom properties. The default
   * implementation returns the meta data (description, properties) from the MBeanInfo provided to
   * the constructor. If no MBeanInfo has been provided it returns an empty property table.
   *
   * @param info The default MBeanInfo derived by reflection.
   *
   * @return the additional properties for the given MBeanInfo.
   */
  protected Properties getProperties(MBeanInfo info) {
    Properties props = super.getProperties(info);
    String descr = addInfo.getDescription();

    if (descr != null) {
      props.setProperty(AdditionalInfo.DESCRIPTION_KEY, descr);
    }

    if (addInfo instanceof AdditionalInfo) {
      props.putAll(((AdditionalInfo) addInfo).getProperties());
    }

    return props;
  }

  /**
   * Customization hook: Get the properties that will be used in the MBeanOperationInfo returned by
   * this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom properties. The default
   * implementation returns the meta data (description, parameter names, properties) from the
   * matching MBeanOperationInfo provided to the constructor. If no matching MBeanOperationInfo
   * was found it returns an empty property table.
   *
   * @param op The default MBeanOperationInfo derived by reflection.
   * @param param The default MBeanParameterInfo derived by reflection.
   * @param sequence The sequence number of the parameter considered ("0" for the first parameter,
   *        "1" for the second parameter, etc...).
   *
   * @return the additional properties for the given MBeanOperationInfo.
   */
  protected Properties getProperties(MBeanOperationInfo op, MBeanParameterInfo param, int sequence) {
    Properties props = new Properties();
    MBeanOperationInfo addOpInfo = MBeanInfoUtilities.getOperationInfo(op.getName(),
            op.getSignature(),
            addInfo.getOperations());
    MBeanParameterInfo addParamInfo = (addOpInfo == null) ? null : addOpInfo.getSignature()[sequence];

    if (addParamInfo != null) {
      props.setProperty(AdditionalInfo.NAME_KEY, addParamInfo.getName());

      String descr = addParamInfo.getDescription();

      if (descr != null) {
        props.setProperty(AdditionalInfo.DESCRIPTION_KEY, descr);
      }

      if (addParamInfo instanceof AdditionalInfo) {
        props.putAll(((AdditionalInfo) addParamInfo).getProperties());
      }
    }

    return props;
  }

  /**
   * Customization hook: Get the properties that will be used in the MBeanOperationInfo returned by
   * this MBean.<br>
   * Subclasses may redefine this method in order to supply their custom properties. The default
   * implementation returns the meta data (description, impact, properties) from the matching
   * MBeanOperationInfo provided to the constructor. If no matching MBeanOperationInfo was found
   * it returns an empty property table.
   *
   * @param info The default MBeanOperationInfo derived by reflection.
   *
   * @return the additional properties for the given MBeanOperationInfo.
   */
  protected Properties getProperties(MBeanOperationInfo info) {
    Properties props = new Properties();
    MBeanOperationInfo addOpInfo = MBeanInfoUtilities.getOperationInfo(info.getName(),
            info.getSignature(),
            addInfo.getOperations());

    if (addOpInfo != null) {
      String descr = addOpInfo.getDescription();

      if (descr != null) {
        props.setProperty(AdditionalInfo.DESCRIPTION_KEY, descr);
      }
      props.setProperty(AdditionalInfo.IMPACT_KEY, String.valueOf(addOpInfo.getImpact()));

      if (addOpInfo instanceof AdditionalInfo) {
        props.putAll(((AdditionalInfo) addOpInfo).getProperties());
      }
    }

    return props;
  }
}