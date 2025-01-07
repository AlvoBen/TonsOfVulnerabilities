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
package com.sap.jmx.modelhelper;

import java.io.IOException;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.relation.RelationService;

import com.sap.pj.jmx.exception.Messages;
import com.sap.jmx.ObjectNameFactory;

/**
 * Helper class to find related MBeans using the relation service.
 */
public class RelationHelper {
  private static final String OBJECT_NAME = ObjectName.class.getName();
  private static final String OBJECT_NAME_ARRAY = ObjectName[].class.getName();
  private static final String FIND_ASSOC_MBEANS_NAME = "findAssociatedMBeans"; //$NON-NLS-1$
  private static final String[] FIND_ASSOC_MBEANS_SIGNATURE =
    new String[] { ObjectName.class.getName(), String.class.getName(), String.class.getName()};
  public static final ObjectName RELATION_SERVICE_ON;

  // init ObjectName constants
  static {
    try {
      RELATION_SERVICE_ON = ObjectNameFactory.getNameForServerChildPerNode(RelationService.class.getName(), "AdminProviderRelationService", null, null); //$NON-NLS-1$
    }
    catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  /**
   * Find the related MBean in a to 1 relation cardinality.
   * @param mbsc the MbeanServer the source MBean is registered with
   * @param name the name of the source MBean
   * @param relationTypeName the name of the relation type
   * @param roleName the role name of the source MBean
   * @param otherRoleName the role name of the other MBean
   * @return the related MBean
   * @throws MBeanProxyFactoryException
   */
  public static ObjectName getRelatedMBean(
    MBeanServerConnection mbsc,
    ObjectName name,
    String relationTypeName,
    String roleName,
    String otherRoleName)
    throws MBeanProxyFactoryException {
    Set result = getRelatedMBeans(mbsc, name, relationTypeName, roleName, otherRoleName);
    ObjectName on = null;
    if (result != null && result.size() > 0) {
      on = (ObjectName) result.iterator().next();
    }
    return on;
  }

  /**
   * Find the related MBean in a to N relation cardinality.
   * @param mbsc the MbeanServer the source MBean is registered with
   * @param name the name of the source MBean
   * @param relationTypeName the name of the relation type
   * @param roleName the role name of the source MBean
   * @param otherRoleName the role name of the other MBeans
   * @return the related MBeans
   * @throws MBeanProxyFactoryException
   */
  public static Set getRelatedMBeans(
    MBeanServerConnection mbsc,
    ObjectName name,
    String relationTypeName,
    String roleName,
    String otherRoleName)
    throws MBeanProxyFactoryException {
    try {
      // first try whether there is an attribute
      MBeanAttributeInfo[] attrInfo = mbsc.getMBeanInfo(name).getAttributes();
      for (int i = 0; i < attrInfo.length; i++) {
        if (attrInfo[i].getName().equals(otherRoleName)) {
          if (attrInfo[i].getType().equals(OBJECT_NAME)) {
            ObjectName on = (ObjectName) mbsc.getAttribute(name, otherRoleName);
            return new ArraySet(on);
          }
          else if (attrInfo[i].getType().equals(OBJECT_NAME_ARRAY)) {
            ObjectName[] ons = (ObjectName[]) mbsc.getAttribute(name, otherRoleName);
            return new ArraySet(ons);
          }
        }
      }
      // get the related MBeans from the relation service
      return (
        (Map) mbsc.invoke(
          RELATION_SERVICE_ON,
          FIND_ASSOC_MBEANS_NAME,
          new Object[] { name, relationTypeName, roleName },
          FIND_ASSOC_MBEANS_SIGNATURE))
        .keySet();
    }
    catch (JMException e) {
      throw new MBeanProxyFactoryException(Messages.UNABLE_TO_GET_RELATED_MBEANS_FOR_$0_REL_TYPE_$1_ROLE_$2, 
      						new Object[]{name, relationTypeName, roleName}, e);
    }
    catch (IOException e) {
      throw new MBeanProxyFactoryException(Messages.UNABLE_TO_GET_RELATED_MBEANS_FOR_$0_REL_TYPE_$1_ROLE_$2, 
									new Object[]{name, relationTypeName, roleName}, e);
    }
  }

  /*
   * An unmodifiable Set based on an ObjectName[] that keeps the order of the
   * elements in the array when iterating.  
   */
  private static final class ArraySet extends AbstractSet implements Set {
    private final ObjectName[] array;
    private final int size;

    public ArraySet(ObjectName[] names) {
      if (names == null) {
        array = new ObjectName[0];
        size = 0;
      }
      else {
        // remove duplicates from the array
        array = new ObjectName[names.length];
        Set items = new HashSet(names.length, 1);
        int j = 0;
        for (int i = 0; i < names.length; i++) {
          if (names[i] == null) {
            continue;
          }
          if (items.add(names[i])) {
            array[j++] = names[i];
          }
        }
        size = j;
      }
    }

    public ArraySet(ObjectName name) {
      if (name == null) {
        array = new ObjectName[0];
        size = 0;
      }
      else {
        array = new ObjectName[] { name };
        size = 1;
      }
    }

    /*
     * @see java.util.AbstractCollection#iterator()
     */
    public Iterator iterator() {
      return new Iterator() {
        int index = 0;

        public boolean hasNext() {
          return index < size;
        }

        public Object next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return array[index++];
        }

        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }

    /*
     * @see java.util.AbstractCollection#size()
     */
    public int size() {
      return size;
    }

    /*
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection c) {
      throw new UnsupportedOperationException();
    }

    /*
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o) {
      throw new UnsupportedOperationException();
    }

    /*
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c) {
      throw new UnsupportedOperationException();
    }

    /*
     * @see java.util.Collection#clear()
     */
    public void clear() {
      throw new UnsupportedOperationException();
    }

    /*
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
      throw new UnsupportedOperationException();
    }

    /*
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection c) {
      throw new UnsupportedOperationException();
    }

  }

}
