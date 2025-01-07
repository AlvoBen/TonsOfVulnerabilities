/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.mbeaninfo;

import javax.management.*;

/**
 * Utilities for accessing MBeanInfo.
 *
 * @author    d025700
 */
public class MBeanInfoUtilities {
  /**
   * Retrieves the MBeanAttributeInfo for a given <var>name</var> from <var>attributes</info>.
   * Returns <code>null</code> if no matching attribute was found.
   *
   * @param name The name of the attribute to be found.
   * @param attributes the MBeanAttributeInfos where to look for the attribute 
   * @return the MBeanAttributeInfo for the given <var>name</var>.
   */
  public static MBeanAttributeInfo getAttributeInfo(String name, MBeanAttributeInfo[] attributes) {
    // assert(attributes != null);

    for (int i = 0; i < attributes.length; i++) {
      if (attributes[i].getName().equals(name)) {
        return attributes[i];
      }
    }
    return null;
  }

  /**
   * Retrieves the <code>MBeanConstructorInfo</code> from the given <code>constructors</code> for
   * the constructor specified by <code>name</code> and <code>signature</code>. Returns
   * <code>null</code> if no matching constructor was found.
   *
   * @param name The name of the constructor to be found.
   * @param signature The signature of the constructor to be found.
   * @param constructors The MBeanConstructorInfo[] where the constructor is searched for.
   *
   * @return the MBeanConstructorInfo for the constructor specified.
   */
  public static MBeanConstructorInfo getConstructorInfo(
          String name,
          MBeanParameterInfo[] signature,
          MBeanConstructorInfo[] constructors) {
    // assert(signature != null);
    // assert(constructors != null);
    ctors_loop : for (int i = 0; i < constructors.length; i++) {
      if (constructors[i].getName().equals(name)) {
        // matching name found, compare parameters
        MBeanParameterInfo[] paramsInfo = constructors[i].getSignature();
        // check parameter array length
        if (signature.length != paramsInfo.length) {
          // mismatch: different length of parameter arrays, continue with next constructor
          continue ctors_loop;
        }
        // parameter arrays have the same length, check parameter types
        for (int j = 0; j < signature.length; j++) {
          if (!paramsInfo[j].getType().equals(signature[j].getType())) {
            // mismatch: different parameter type found, continue with next constructor
            continue ctors_loop;
          }
        }
        // match: signatures are equal
        return constructors[i];
      }
    }
    // no matching operation found
    return null;
  }

  /**
   * Returns the <code>MBeanOperationInfo</code> from the given <code>operations</code> for the
   * operation specified by <code>name</code> and <code>params</code>. Returns <code>null</code> if
   * no matching operation was found.
   *
   * @param name The name of the operation to be found.
   * @param signature The signature of the operation to be found.
   * @param operations The MBeanOperationInfo[] where the operation is searched for.
   *
   * @return the MBeanOperationInfo for specified operation.
   */
  public static MBeanOperationInfo getOperationInfo(
          String name,
          MBeanParameterInfo[] signature,
          MBeanOperationInfo[] operations) {
    // assert(signature != null);
    // assert(operations != null);
    ops_loop : for (int i = 0; i < operations.length; i++) {
      if (operations[i].getName().equals(name)) {
        // matching name found, compare parameters
        MBeanParameterInfo[] paramInfo = operations[i].getSignature();
        // check parameter array length
        if (signature.length != paramInfo.length) {
          // mismatch: different length of parameter arrays, continue with next operation
          continue ops_loop;
        }
        // parameter arrays have the same length, check parameter types
        for (int j = 0; j < signature.length; j++) {
          if (!paramInfo[j].getType().equals(signature[j].getType())) {
            // mismatch: different parameter type found, continue with next operation
            continue ops_loop;
          }
        }
        // match: signatures are equal
        return operations[i];
      }
    }
    // no matching operation found
    return null;
  }

  /**
   * Returns the <code>MBeanNotificationInfo</code> from the given <code>notifications</code> for
   * the notification specified by <code>name</code>. Returns <code>null</code> if no matching
   * notification was found.
   *
   * @param name The name of the notification to be found.
   * @param notifications The MBeanNotificationInfo[] where the notification is searched for.
   *
   * @return the MBeanNotificationInfo for the given <var>name</var>.
   */
  public static MBeanNotificationInfo getNotificationInfo(
          String name,
          MBeanNotificationInfo[] notifications) {
    // assert(notifications != null);
    for (int i = 0; i < notifications.length; i++) {
      if (notifications[i].getName().equals(name)) {
        return notifications[i];
      }
    }
    return null;
  }

}
