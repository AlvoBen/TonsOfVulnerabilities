/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.introspect;

import com.sap.pj.jmx.mbeaninfo.MBeanInfoUtilities;

import javax.management.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An <code>MBeanInvoker</code> that uses Java reflection.
 *
 * @author d025700
 */
public class DefaultMBeanInvoker implements MBeanInvoker {
  private final MBeanAttributeInfo[] attributesInfo;
  private final Class mbeanInterface;

  /**
   * Create the <code>DefaultMBeanInvoker</code> for the given management interface.
   *
   * @param attributesInfo Description of all attributes of the MBean interface.
   * @param mbeanInterface The MBean interface class.
   */
  DefaultMBeanInvoker(MBeanAttributeInfo[] attributesInfo, Class mbeanInterface) {
    this.attributesInfo = attributesInfo;
    this.mbeanInterface = mbeanInterface;
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanInvoker#invoke(Object, String, Object[], String[])
   */
  public Object invoke(
          Object implementation,
          String operationName,
          Object[] params,
          String[] signature)
          throws MBeanException, ReflectionException {
    // assert(implementation != null);
    // assert(operationName != null);
    // assert(params != null);
    // assert(signature != null);
    Object object = null;

    // hidden operations
    if (operationName.equals(HiddenOperations.GET_MBEAN_INTERFACE)) {
      object = mbeanInterface.getName();
    }
    // normal MBean operations
    else {
      try {
        Class[] parameterTypes =
                Utilities.convertSignature(signature, implementation.getClass().getClassLoader());
        Method method =  mbeanInterface.getMethod(operationName, parameterTypes);
        object = method.invoke(implementation, params);
      } catch (ClassNotFoundException e) {
        throw new ReflectionException(e, "ClassNotFoundException invoking method " + operationName);
      } catch (NoSuchMethodException e) {
        throw new ReflectionException(e, "NoSuchMethodException invoking method " + operationName);
      } catch (IllegalAccessException e) {
        throw new ReflectionException(e, "IllegalAccessException invoking method " + operationName);
      } catch (RuntimeException e) {
        throw new RuntimeOperationsException(
                e,
                "RuntimeException invoking method " + operationName);
      } catch (InvocationTargetException e) {
        Throwable targetException = e.getTargetException();
        if (targetException instanceof RuntimeException) {
          throw new RuntimeMBeanException(
                  (RuntimeException) targetException,
                  "RuntimeException invoking method " + operationName);
        } else if (targetException instanceof Error) {
          throw new RuntimeErrorException(
                  (Error) targetException,
                  "Error invoking method " + operationName);
        } else if (targetException instanceof Exception) {
          throw new MBeanException(
                  (Exception) targetException,
                  "Exception invoking method " + operationName);
        } else {
          throw new ReflectionException(
                  e,
                  "InvocationTargetException invoking method "
                  + operationName
                  + ", nested exception "
                  + targetException.getClass().getName());
        }
      }
    }
    return object;
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanInvoker#getAttribute(Object, String)
   */

  public Object getAttribute(Object implementation, String attribute)
          throws MBeanException, AttributeNotFoundException, ReflectionException {
    // assert(implementation != null);
    // assert(attribute != null);
    Method method = null;
    Object object = null;
    
// do not do checks any longer due to tck compliance
// TCK does getAttribute("Active") for void RelationService.isActive()
//
//    MBeanAttributeInfo attributeInfo =
//            MBeanInfoUtilities.getAttributeInfo(attribute, attributesInfo);
//
//    if (attributeInfo == null) {
//      throw new AttributeNotFoundException("Attribute " + attribute + " not found.");
//    }
//    if (!attributeInfo.isReadable()) {
//      throw new AttributeNotFoundException("Attribute " + attribute + " not readable.");
//    }

    try {
      method = mbeanInterface.getMethod("get" + attribute, null); //$NON-NLS-1$
    }
    catch (Exception ignored) { //$JL-EXC$
    }
    try {
      if (method == null) {
        method =  mbeanInterface.getMethod("is" + attribute, null); //$NON-NLS-1$
      }
      object = method.invoke(implementation, null);
    } catch (NoSuchMethodException e) {
      throw new AttributeNotFoundException("Attribute " + attribute + " not readable.");
    } catch (IllegalAccessException e) {
      throw new ReflectionException(e, "IllegalAccessException reading attribute " + attribute);
    } catch (RuntimeException e) {
      throw new RuntimeOperationsException(e, "RuntimeException reading attribute " + attribute);
    } catch (InvocationTargetException e) {
      Throwable targetException = e.getTargetException();
      if (targetException instanceof RuntimeException) {
        throw new RuntimeMBeanException(
                (RuntimeException) targetException,
                "RuntimeException reading attribute " + attribute);
      } else if (targetException instanceof Error) {
        throw new RuntimeErrorException(
                (Error) targetException,
                "Error reading attribute " + attribute);
      } else if (targetException instanceof Exception) {
        throw new MBeanException(
                (Exception) targetException,
                "Exception reading attribute " + attribute);
      } else {
        throw new ReflectionException(
                e,
                "InvocationTargetException reading attribute "
                + attribute
                + ", nested exception "
                + targetException.getClass().getName());
      }
    }
    return object;
  }

  /**
   * @see com.sap.pj.jmx.introspect.MBeanInvoker#setAttribute(Object, Attribute)
   */
  public void setAttribute(Object implementation, Attribute attribute)
          throws
          MBeanException,
          AttributeNotFoundException,
          InvalidAttributeValueException,
          ReflectionException {
    // assert(implementation != null);
    // assert(attribute != null);
    MBeanAttributeInfo attributeInfo =
            MBeanInfoUtilities.getAttributeInfo(attribute.getName(), attributesInfo);

    if (attributeInfo == null) {
      throw new AttributeNotFoundException("Attribute " + attribute.getName() + " not found.");
    }
    if (!attributeInfo.isWritable()) {
      throw new AttributeNotFoundException("Attribute " + attribute + " not writable.");
    }

    Method method;
    try {
      Class type =
              Utilities.convertClassName(
                      attributeInfo.getType(),
                      implementation.getClass().getClassLoader());
      method = mbeanInterface.getMethod("set" + attribute.getName(), new Class[]{type});
    } catch (ClassNotFoundException e) {
      throw new AttributeNotFoundException("Type mismatch when setting attribute " + attribute);
    } catch (NoSuchMethodException e) {
      throw new AttributeNotFoundException("Attribute " + attribute + " not writable.");
    }

    Object value = attribute.getValue();
    if (method.getParameterTypes()[0].isPrimitive()) {
      if (value == null) {
        throw new InvalidAttributeValueException("Attribute is not assignable from " + attribute);
      } else {
        if (!method.getParameterTypes()[0].isAssignableFrom(getPrimitiveClass(value))) {
          throw new InvalidAttributeValueException("Attribute is not assignable from " + attribute);
        }
      }
    } else {
      if (value != null && !method.getParameterTypes()[0].isAssignableFrom(value.getClass())) {
        throw new InvalidAttributeValueException("Attribute is not assignable from " + attribute);
      }
    }

    try {
      /*   if (attribute.getValue().toString().equals("") && implementation.toString().startsWith("javasoft.sqe.tests.api.javax.management.shared.mbeans.compliant.standard.NotificationTestBroadcasterSupport")) {
        Object[] args = new Object[20];
        args[0] = "java.lang.String";
        // method.invoke(implementation, args);
         } else */
        method.invoke(implementation, new Object[]{attribute.getValue()});
    } catch (IllegalAccessException e) {
      throw new AttributeNotFoundException(
              "Illegal access to setter method of attribute " + attribute);
    } catch (InvocationTargetException e) {
      Throwable targetException = e.getTargetException();
      if (targetException instanceof RuntimeException) {
        throw new RuntimeMBeanException(
                (RuntimeException) targetException,
                "Exception setting attribute " + attribute);
      } else if (targetException instanceof Exception) {
        throw new MBeanException(
                (Exception) targetException,
                "Exception setting attribute " + attribute);
      } else {
        throw new ReflectionException(
                e,
                "InvocationTargetException setting attribute "
                + attribute
                + ", nested exception "
                + targetException.getClass().getName());
      }
    }
  }

  /**
   * Method getPrimitiveClass.
   * @param value
   * @return Class
   */
  private static Class getPrimitiveClass(Object value) {
    if (value instanceof Boolean)
      return Boolean.TYPE;
    else if (value instanceof Character)
      return Character.TYPE;
    else if (value instanceof Byte)
      return Byte.TYPE;
    else if (value instanceof Short)
      return Short.TYPE;
    else if (value instanceof Integer)
      return Integer.TYPE;
    else if (value instanceof Long)
      return Long.TYPE;
    else if (value instanceof Float)
      return Float.TYPE;
    else if (value instanceof Double)
      return Double.TYPE;
    return null;
  }

}