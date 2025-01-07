/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.introspect;

import javax.management.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Provides MBeanInfo and MBeanInvoker via reflection. Not synchronized!
 *
 * @author d025700
 */
public class DefaultMBeanIntrospector implements MBeanIntrospector {
  private Object implementation;
  private final Class mbeanInterface;
  private String className;
  private final MBeanAttributeInfo[] attributes;
  private MBeanConstructorInfo[] constructors;
  private final MBeanOperationInfo[] operations;
  private MBeanInvoker invoker;

  /**
   * Create a DefaultMBeanIntrospector for a given MBean interface and a initial implementation.
   *
   * @param implementation The implementation of the MBean.
   * @param mbeanInterface The management interface exported by <var>implementation</var>. If
   *        <code>null</code>, then this object will use standard JMX design pattern to determine
   *        the management interface associated with the given implementation.
   *
   * @throws IllegalArgumentException if the given <var>implementation</var> is <code>null</code>.
   * @throws NotCompliantMBeanException if the <var>mbeanInterface</var> does not follow JMX design
   *         patterns for Management Interfaces, or if the given <var>implementation</var> does
   *         not implement the specified interface.
   */
  protected DefaultMBeanIntrospector(Object implementation, Class mbeanInterface)
          throws NotCompliantMBeanException {
    if (implementation == null) {
      throw new IllegalArgumentException("The given implementation must not be null.");
    }
    this.mbeanInterface = findMBeanInterface(implementation, mbeanInterface);
    this.implementation = implementation;
    this.className = implementation.getClass().getName();

    // determine final parts of MBeanInfo
    int i = 0;
    ArrayList operationsList = new ArrayList();
    Map attributesMap = new HashMap();
    Method[] meths = this.mbeanInterface.getMethods();

    for (i = 0; i < meths.length; i++) {
      AttributeAccessor aa = AttributeAccessor.getAttributeAccessor(meths[i]);

// RK TCK1.2.1
      if (aa != null) {  
        MBeanAttributeInfo attrInfo = null;

        if ((attrInfo = (MBeanAttributeInfo) attributesMap.get(aa.getName())) != null) {
          // if the attribute info already exists, the new info must be merged
          if (!attrInfo.getType().equals(aa.getType().getName())) {
            throw new NotCompliantMBeanException(
                    "Attribute-type of setter and getter must be identical, attribute: " + aa.getName());
          }
          if (attrInfo.isReadable() == aa.isGetter() && attrInfo.isIs() != aa.isIsGetter()) {
            throw new NotCompliantMBeanException(
                    "Two incompatible getters used for attribute: " + aa.getName());
          }
          attrInfo = new MBeanAttributeInfo(attrInfo.getName(), attrInfo.getType(),
                  attrInfo.getDescription(),
                  attrInfo.isReadable() | aa.isGetter() |
                  aa.isIsGetter(), attrInfo.isWritable() |
                  aa.isSetter(), attrInfo.isIs() | aa.isIsGetter());
        } else {
          attrInfo = new MBeanAttributeInfo(aa.getName(), aa.getType().getName(), null,
                  aa.isGetter() | aa.isIsGetter(), aa.isSetter(),
                  aa.isIsGetter());
        }
        attributesMap.put(aa.getName(), attrInfo);
      } 
      else {
        // not an Attribute
        Class[] params = meths[i].getParameterTypes();
        MBeanParameterInfo[] paramsInfo = new MBeanParameterInfo[params.length];

        for (int j = 0; j < params.length; j++) {
          final String pn = "p" + (i + 1);
          paramsInfo[j] = new MBeanParameterInfo(pn, params[j].getName(), null);
        }
        operationsList.add(
                new MBeanOperationInfo(meths[i].getName(), null, paramsInfo,
                        meths[i].getReturnType().getName(), MBeanOperationInfo.UNKNOWN));
      }
    }
// KR

    attributes = new MBeanAttributeInfo[attributesMap.size()];
    i = 0;

    for (Iterator iter = attributesMap.values().iterator(); iter.hasNext();) {
      attributes[i++] = (MBeanAttributeInfo) iter.next();
    }

    operations = new MBeanOperationInfo[operationsList.size()];
    i = 0;

    for (Iterator iter = operationsList.iterator(); iter.hasNext();) {
      operations[i++] = (MBeanOperationInfo) iter.next();
    }

    constructors = buildMBeanConstructorInfo(implementation);
  }

  /**
   * Searches for a matching standard MBean interface.
   */
  protected static Class findMBeanInterface(Object implementation, Class mbeanInterface)
          throws NotCompliantMBeanException {
    // determine the interface
    if (mbeanInterface != null) {
      if (!mbeanInterface.isInterface()) {
        throw new NotCompliantMBeanException(
                "The given mbeanInterface Class is not a Java interface.");
      }
      // caller has specified the management interface
      if (!mbeanInterface.isAssignableFrom(implementation.getClass())) {
        // the implementation does not implement the given interface
        throw new NotCompliantMBeanException(
                "The given implementation does not implement the specified interface.");
      } else {
        return mbeanInterface;
      }
    } else {
      // determine the management interface by applying standard MBean search pattern
      for (Class clazz = implementation.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
        Class[] intfs = clazz.getInterfaces();

        for (int i = 0; i < intfs.length; i++) {
          if (implementsStandardMBeanInterface(clazz.getName(), intfs[i].getName())) {
            return intfs[i];
          }
        }
      }
    }
    throw new NotCompliantMBeanException("Not a compliant standard MBean");
  }

  /**
   * Checks the pattern for naming standard MBean implementation and interface.
   */
  private static boolean implementsStandardMBeanInterface(String className, String intfName) {
    // obsolete with JMX 1.2:
    // if (!shortname(intfName).equals(shortname(className) + "MBean")) {
    // JMX 1.2 explicitely requires the following
    if (!intfName.equals(className + "MBean")) {
      return false;
    }

    return true;
  }

//  /**
//   * Returns classname without package. Obsolete with JMX 1.2
//   */
//  private static String shortname(String className) {
//    int ip = className.lastIndexOf('.');
//
//    if (ip > 0) {
//      className = className.substring(ip);
//    }
//
//    int id = className.lastIndexOf('$');
//
//    if (id > 0) {
//      className = className.substring(id);
//    }
//
//    return className;
//  }

  /**
   * Builds the constructor info for the given implementation.
   */
  protected static MBeanConstructorInfo[] buildMBeanConstructorInfo(Object implementation) {
    Constructor[] consts = implementation.getClass().getConstructors();

    MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[consts.length];

    for (int i = 0; i < consts.length; i++) {
      Class[] params = consts[i].getParameterTypes();
      MBeanParameterInfo[] paramsInfo = new MBeanParameterInfo[params.length];

      for (int j = 0; j < params.length; j++) {
        final String pn = "p" + (i + 1);
        paramsInfo[j] = new MBeanParameterInfo(pn, params[j].getName(), null);
      }
      constructors[i] = new MBeanConstructorInfo(consts[i].getName(), null, paramsInfo);
    }

    return constructors;
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanIntrospector#getInvoker()
   */
  public MBeanInvoker getInvoker() {
    if (invoker == null) {
      invoker = new DefaultMBeanInvoker(attributes, mbeanInterface);
    }

    return invoker;
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanIntrospector#setImplementation(Object)
   */
  public void setImplementation(Object implementation)
          throws NotCompliantMBeanException {
    if (implementation == null) {
      throw new IllegalArgumentException("The given implementation must not be null.");
    }

    if (!mbeanInterface.isAssignableFrom(implementation.getClass())) {
      // the implementation does not implement the given interface
      throw new NotCompliantMBeanException(
              "The given implementation does not implement the specified interface.");
    }
    this.implementation = implementation;


    // reset chached classname and constructor info since it might have changed
    this.className = implementation.getClass().getName();
    this.constructors = buildMBeanConstructorInfo(implementation);
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanIntrospector#getImplementation()
   */
  public Object getImplementation() {
    return implementation;
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanIntrospector#getMBeanInterface()
   */
  public Class getMBeanInterface() {
    return mbeanInterface;
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanIntrospector#getClassName()
   */
  public String getClassName() {
    return className;
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanIntrospector#getAttributeInfo()
   */
  public MBeanAttributeInfo[] getAttributeInfo() {
    return attributes;
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanIntrospector#getConstructorInfo()
   */
  public MBeanConstructorInfo[] getConstructorInfo() {
    if (constructors == null) {
      constructors = buildMBeanConstructorInfo(implementation);
    }

    return constructors;
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanIntrospector#getNotificationInfo()
   */
  public MBeanNotificationInfo[] getNotificationInfo() {
    if (implementation instanceof NotificationBroadcaster) {
      return ((NotificationBroadcaster) implementation).getNotificationInfo();
    } else {
      return null;
    }
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanIntrospector#getOperationInfo()
   */
  public MBeanOperationInfo[] getOperationInfo() {
    return operations;
  }
}