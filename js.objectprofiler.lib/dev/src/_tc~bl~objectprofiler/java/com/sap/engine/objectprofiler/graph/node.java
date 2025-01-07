/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.objectprofiler.graph;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Vector;
import java.util.HashMap;

/**
 * @author Georgi Stanev, Mladen Droshev, Pavel Bonev
 * @version 7.10C
 */
public class Node implements Serializable{
  public static final String DEFINED_SHAREABLE = "defined shareable";
  public static final String NON_SERILIZABLE_BASE_CLASS = "non serializable base class";
  public static final String NON_SHAREABLE_CLASSLOADER = "has non shareable class loader";
  public static final String NON_TRIVIAL_FINALIZER = "has non trivial finalizer";
  public static final String READ_EXTERNAL = "has readExternal()";
  public static final String READ_OBJECT = "has readObject()";
  public static final String READ_RESOLVE = "has readResolve()";
  public static final String SERIAL_PERSISTENT_FIELD = "has serial persistent field";
  public static final String TRANSIENT_FIELD = "has transient field";
  public static final String WRITE_EXTERNAL = "has writeExternal()";
  public static final String WRITE_OBJECT = "has writeObject()";
  public static final String WRITE_REPLACE = "has writeReplace()";
  public static final String NOT_SERIALIZABLE = "not serializable";

  public static final int OBJECT_SHELL_SIZE = 8; // java.lang.Object shell size in bytes
  public static final int OBJECT_REF_SIZE = 4;
  public static final int LONG_FIELD_SIZE = 8;
  public static final int INT_FIELD_SIZE = 4;
  public static final int SHORT_FIELD_SIZE = 2;
  public static final int CHAR_FIELD_SIZE = 2;
  public static final int BYTE_FIELD_SIZE = 1;
  public static final int BOOLEAN_FIELD_SIZE = 1;
  public static final int DOUBLE_FIELD_SIZE = 8;
  public static final int FLOAT_FIELD_SIZE = 4;

  static final long serialVersionUID = 4152683060015157736L;

  private int id = -1;
  private int hashID = 0;
  private int weight = 0;

  private boolean isDummy = false;
  private boolean isCompound = false;
  private boolean isSerializable = false;
  private boolean isShareable = false;
  private boolean hasNonShareableKids = false;

  private transient Object value = null;
  private String type = null;
  private String genericType = null;
  private String stringValue = null;
  private ClassData currentClassData = null;
  private PrimitiveField[] primitiveFields = null;

  private String reasonForNonShareability = null;

  private HashMap props = new HashMap();

  public Node(int hashID, int weight, boolean isDummy,
              boolean isCompound, boolean isSerializable, boolean isShareable,
              boolean hasNonShareableKids, String type, String genericType,
              String stringValue, String reasonForNonShareability, ClassData currentClassData, PrimitiveField[] pfields) {

    this.hashID = hashID;
    this.weight = weight;
    this.isDummy = isDummy;
    this.isCompound = isCompound;
    this.isSerializable = isSerializable;
    this.isShareable = isShareable;
    this.hasNonShareableKids = hasNonShareableKids;
    this.type = type;
    this.genericType = genericType;
    this.stringValue = stringValue;
    this.currentClassData = currentClassData;
    this.primitiveFields = pfields;
    this.reasonForNonShareability = reasonForNonShareability;
  }

  public Node() {
    isDummy = true;
  }

  public HashMap getProps() {
    return props;
  }

  public void setProps(HashMap p) {
    props = p;
  }

  public void addProp(String name, Object prop) {
    props.put(name, prop);
  }

  public void setID(int id) {
    this.id = id;
  }

  public static Node buildNode(Object value, boolean includeTransients) {
    if (value == null) {
      return null;
    }

    Class _class = value.getClass();

    int hashID = System.identityHashCode(value);
    int weight = OBJECT_SHELL_SIZE;
    boolean isDummy = false;
    boolean isCompound = false;
    boolean isSerializable = false;
    boolean isShareable = false;
    boolean hasNonShareableKids = false;
    String type = null;
    String genericType = null;
    String stringValue = null;
    String reasonForNonShareability = null;
    ClassData currentClassData = new ClassData(_class);
    PrimitiveField[] primitiveFields = null;

    if (_class.equals(String.class)) {
      stringValue = value.toString();
    }

    if (_class.isArray()) {
      isCompound = true;
      int size = Array.getLength(value);
      Class compType = _class.getComponentType();
      weight = arraySize(size, compType);
      genericType = formGenericType(_class);
      type = genericType + "["+size+"]";
    } else {
      type = _class.getName();
      genericType = type;
      Field[] nonPrimitiveFields = getNonPrimitiveFields(_class, includeTransients);
      weight += nonPrimitiveFields.length * OBJECT_REF_SIZE;

      Field[] _primitiveFields = getPrimitiveFields(_class);
      if (_primitiveFields != null) {
        Vector tempFields = new Vector();
        for (int i = 0; i < _primitiveFields.length; i++) {
          Class fieldType = _primitiveFields[i].getType();
          Class fieldCompType = fieldType.getComponentType();

          if (fieldType.isPrimitive()) {
            try {
              weight += sizeofPrimitiveType(fieldType);
              tempFields.addElement(new PrimitiveField(_primitiveFields[i].getName(), fieldType.getName(), _primitiveFields[i].get(value).toString()));
            } catch (IllegalAccessException e) {
              //System.out.println(">> Exception : " + e.getMessage());
              if (Graph.isDebug()) {
                e.printStackTrace();
              }
            }
          } else if (fieldType.isArray() && fieldCompType.isPrimitive()) {
            try {
              if (_primitiveFields[i].get(value) != null) {
                int size = Array.getLength(_primitiveFields[i].get(value));
                weight += arraySize(size, fieldCompType);
                tempFields.addElement(new PrimitiveField(_primitiveFields[i].getName(), fieldCompType.getName() + "[" + Array.getLength(_primitiveFields[i].get(value)) + "]", null));
              }
            } catch (IllegalAccessException e) {
             // System.out.println(">> Exception : " + e.getMessage());
              if (Graph.isDebug()) {
                e.printStackTrace();
              }
            }
          }
        } //for
        if (tempFields.size() > 0) {
          primitiveFields = (PrimitiveField[])tempFields.toArray(new PrimitiveField[0]);
        }
      }
    }

    Node node = new Node(hashID, weight, isDummy, isCompound, isSerializable, isShareable,
                         hasNonShareableKids, type, genericType, stringValue, reasonForNonShareability,
                         currentClassData, primitiveFields);
    return node;
  }

  public String[] getInfo() {
    ArrayList res = new ArrayList();

    res.add("ID : "+id);
    res.add("Hash ID : " + hashID);
    res.add("Type : "+type);
    if (stringValue != null) {
      res.add("Value : "+stringValue);
    }
    res.add("Native Size: " + weight);
    res.add("Shareability: " + isShareable);
    if (!isShareable) {
      res.add("Reason for non-shareability: " + reasonForNonShareability);
    }
    if (currentClassData != null) {
      String[] ss = currentClassData.getInfo();
      for (int i = 0; i < ss.length; i++) {
        res.add("  " + ss[i]);
      }
    }

    if (primitiveFields != null) {
      for (int i = 0; i < primitiveFields.length; i++) {
        res.add(primitiveFields[i].getInfo());

      }
    }

    return (String[]) res.toArray(new String[0]);
  }

  public static int sizeofPrimitiveType(final Class type) {
    if (type == int.class) {
      return INT_FIELD_SIZE;
    } else if (type == long.class) {
      return LONG_FIELD_SIZE;
    } else if (type == short.class) {
      return SHORT_FIELD_SIZE;
    } else if (type == byte.class) {
      return BYTE_FIELD_SIZE;
    } else if (type == boolean.class) {
      return BOOLEAN_FIELD_SIZE;
    } else if (type == char.class) {
      return CHAR_FIELD_SIZE;
    } else if (type == double.class) {
      return DOUBLE_FIELD_SIZE;
    } else if (type == float.class) {
      return FLOAT_FIELD_SIZE;
    } else {
      throw new IllegalArgumentException("not primitive: " + type);
    }
  }

  public static int arraySize(int length, Class componentType) {
    int itemSize = 0;
    if (componentType.isPrimitive()) {
      itemSize = sizeofPrimitiveType(componentType);
    } else {
      itemSize = OBJECT_REF_SIZE;
    }
    int size = OBJECT_SHELL_SIZE + INT_FIELD_SIZE + OBJECT_REF_SIZE + length * itemSize;
    //System.out.println(componentType+" : "+size+" array length = "+length+" sizeof item = "+itemSize);
    return size;
  }

  public static String formGenericType(Class _class) {
    while (_class.isArray()) {
      _class = _class.getComponentType();
    }

    return _class.getName();
  }

  public static Field[] getPrimitiveFields(Class _class) {
    Field[] fields = getAllFields(_class);
    Vector primV = new Vector();

    for (int i = 0; i < fields.length; i++) {
      fields[i].setAccessible(true);
      Class fieldType = fields[i].getType();
      Class fieldCompType = fieldType.getComponentType();

      if ((fields[i].getType().isPrimitive())) {
        primV.addElement(fields[i]);
      } else if (fieldType.isArray() && fieldCompType.isPrimitive()) {
        //primV.addElement(fields[i]);
      }

    }
    if (primV.size() > 0) {
      Field[] result = new Field[primV.size()];
      primV.copyInto(result);
      return result;
    }
    return null;
  }

  public static Field[] getNonPrimitiveFields(Class _class, boolean includeTransients) {
    Field[] fields = getAllFields(_class);
    String parentObjectClassName = _class.getName();
    Vector nonPrimV = new Vector();

    for (int i = 0; i < fields.length; i++) {
      fields[i].setAccessible(true);
      Class fieldType = fields[i].getType();

//      if (!includeTransients &&
//          Modifier.isTransient(fields[i].getModifiers()) &&
//          !(true)) {
//        continue;
//      }

      if (!fieldType.isPrimitive() && !Modifier.isStatic(fields[i].getModifiers())) {
        nonPrimV.addElement(fields[i]);
      }
    }

    Field[] result = new Field[nonPrimV.size()];
    nonPrimV.copyInto(result);
    return result;
  }

  public static Field[] getAllFields(Class clazz) {
    Vector allFields = new Vector();
    for (; clazz != null; clazz = clazz.getSuperclass()) {
      Field fields[] = clazz.getDeclaredFields();
      for (int i = 0; i < fields.length; i++)
        allFields.addElement(fields[i]);
    }

    Field retval[] = new Field[allFields.size()];
    allFields.copyInto(retval);
    return retval;
  }


  public void setCompound(boolean compound) {
    isCompound = compound;
  }

  public boolean isCompound() {
    return isCompound;
  }

  public boolean isDummy() {
    return isDummy;
  }

  public boolean isShareable(){
    return isShareable;
  }

  public void setShareable(boolean flag){
    isShareable = flag;
  }

  public boolean hasNonShareableKids(){
    return hasNonShareableKids;
  }

  public void setNonShareableKids(boolean flag){
    hasNonShareableKids = flag;
  }

  public int getId() {
    return id;
  }

  public int getWeight() {
    return weight;
  }

  public String getReasonForNonShareability() {
    return reasonForNonShareability;
  }

  public void setReasonForNonShareability(String reason) {
    reasonForNonShareability = reason;
  }

  public ClassData getCurrentClassData() {
    return currentClassData;
  }

  public Object getValue() {
    return value;
  }


  public boolean isSerializable() {
    return isSerializable;
  }


  public String getType() {
    return type;
  }

  public String getGenericType() {
    return genericType;
  }
}
