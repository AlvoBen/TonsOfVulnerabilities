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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.management.relation.RelationType;
import javax.management.relation.RelationTypeSupport;
import javax.management.relation.RoleInfo;

import com.sap.exception.standard.SAPIllegalArgumentException;

/**
 * Factory for creating javax.management.relation.RelationType based on a
 * Java interface that follows the pattern for the Web Dynpro JMX model importer.
 */
public class RelationTypeFactory {
  
  private static final Class relation = javax.management.relation.Relation.class;
  
  /**
   * @param relationClass the Java interface that describes the relation accodrding
   * to the pattern defined by the Web Dynpro JMX model importer
   * @return the JMX relation type
   */
  public RelationType getRelationType(Class relationClass) {
    RelationType type;
    if (!isRelation(relationClass)) {
      throw new IllegalArgumentException("class " + relationClass.getName() + " does not implement the interface " + relation.getName());
    }
    RoleInfo[] roles = new RoleInfo[2];
    Method[] methods = relationClass.getDeclaredMethods();
    if (methods == null || methods.length != 2) {
      throw new IllegalArgumentException("class does not comply with the relation pattern, which requires the class to have exactly two public getter methods.");      
    }
    Method firstMethod = methods[0];
    if (firstMethod.getParameterTypes().length != 0) {
      throw new IllegalArgumentException("class does not comply with the relation pattern, which requires the class to have exactly two public getter methods.");      
    }
    if (!firstMethod.getName().startsWith("get") || firstMethod.getName().length() < 4) {
      throw new IllegalArgumentException("class does not comply with the relation pattern, which requires the class to have exactly two public getter methods.");      
    }
    if (!Modifier.isPublic(firstMethod.getModifiers())) {
      throw new IllegalArgumentException("class does not comply with the relation pattern, which requires the class to have exactly two public getter methods.");      
    }
    Method secondMethod = methods[1];
    if (secondMethod.getParameterTypes().length != 0) {
      throw new IllegalArgumentException("class does not comply with the relation pattern, which requires the class to have exactly two public getter methods.");      
    }
    if (!secondMethod.getName().startsWith("get") || secondMethod.getName().length() < 4) {
      throw new IllegalArgumentException("class does not comply with the relation pattern, which requires the class to have exactly two public getter methods.");      
    }
    if (!Modifier.isPublic(secondMethod.getModifiers())) {
      throw new IllegalArgumentException("class does not comply with the relation pattern, which requires the class to have exactly two public getter methods.");      
    }
    Class firstReturnType = firstMethod.getReturnType().isArray() ? firstMethod.getReturnType().getComponentType() : firstMethod.getReturnType();
    Class secondReturnType = secondMethod.getReturnType().isArray() ? secondMethod.getReturnType().getComponentType() : secondMethod.getReturnType();
    try {
      roles[0] = new RoleInfo(firstMethod.getName().substring(3), firstReturnType.getName(), true, true, 0, firstMethod.getReturnType().isArray() ? RoleInfo.ROLE_CARDINALITY_INFINITY : 1, "Description");
      roles[1] = new RoleInfo(secondMethod.getName().substring(3), secondReturnType.getName(), true, true, 0, secondMethod.getReturnType().isArray() ? RoleInfo.ROLE_CARDINALITY_INFINITY : 1, "Description");
      type = new RelationTypeSupport(relationClass.getName(), roles);
    }
    catch (Exception e) {
      throw new SAPIllegalArgumentException(e);
    }
    return type;
  }
  
  private boolean isRelation(Class relationClass) {
    if (relation == relationClass) {
      return true;
    }
    Class[] interfaces = relationClass.getInterfaces();
    if (interfaces == null) {
      return false;
    }
    for (int i = 0; i < interfaces.length; i++) {
      if (isRelation(interfaces[i])) {
        return true;
      }
    }
    return false;
  }
}
