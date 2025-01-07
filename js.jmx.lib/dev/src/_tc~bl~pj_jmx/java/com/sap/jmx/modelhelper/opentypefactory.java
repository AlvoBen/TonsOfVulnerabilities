package com.sap.jmx.modelhelper;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.ObjectName;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.sap.tc.logging.Location;

/*
 * Created on Mar 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @author d025700
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class OpenTypeFactory {

  private static final Location LOCATION = Location.getLocation(OpenTypeFactory.class);
  private static OpenTypeFactory factory;

  private OpenTypeFactory() {
  }

  public static OpenTypeFactory getFactory() {
    OpenTypeFactory theFactory = factory;
    if (theFactory == null) {
      theFactory = new OpenTypeFactory();
      factory = theFactory;
    }
    return theFactory;
  }

  private static final int MAX_DEPTH = 20;

  public static CompositeType getCompositeType(Class typeInterface) throws OpenDataException {
    return getCompositeTypeInt(0, typeInterface);
  }

  private static CompositeType getCompositeTypeInt(int depth, Class typeInterface) throws OpenDataException {
    CompositeType type = null;
    if (typeInterface == null) {
      throw new IllegalArgumentException("interface class must not be null");
    }
    if (!typeInterface.isInterface()) {
      throw new IllegalArgumentException("interface class must be an interface");
    }
    Method[] methods = typeInterface.getMethods();
    HashMap attributes = new HashMap();
    // get all readeable attributes
    for (int i = 0; i < methods.length; i++) {
      String attrName = methods[i].getName();
      if (methods[i].getDeclaringClass().getName().equals("javax.management.openmbean.CompositeData")) {
        LOCATION.debugT("ignoring method from CompositeData " + typeInterface.getName() + '.' + attrName);
        continue;
      }
      Class returnType = methods[i].getReturnType();
      Class[] paramTypes = methods[i].getParameterTypes();
      if (attrName.startsWith("get")) { //$NON-NLS-1$
        if (attrName.length() < 4) {
          LOCATION.debugT("ignoring method from CompositeData " + typeInterface.getName() + '.' + attrName + " - no getter method");
          continue;
        }
        if (Void.TYPE.getName().equals(returnType.getName()) || Void.TYPE.getName().equals(returnType.getName())) {
          LOCATION.debugT("ignoring method from CompositeData " + typeInterface.getName() + '.' + attrName + " - return type of getter is void");
          continue;
        }
        if (paramTypes.length > 0) {
          LOCATION.debugT("ignoring method from CompositeData " + typeInterface.getName() + '.' + attrName + " - getter method has parameters");
          continue;
        }
        attributes.put(attrName.substring(3), returnType);
      }
      else {
        LOCATION.debugT("ignoring method from CompositeData " + typeInterface.getName() + '.' + attrName + " - no getter method");
        continue;
      }
    }
    String[] names = new String[attributes.size()];
    String[] descriptions = new String[attributes.size()];
    OpenType[] openTypes = new OpenType[attributes.size()];
    int i = 0;
    for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
          Map.Entry attribute = (Map.Entry) iter.next();
          String name = (String) attribute.getKey();
          names[i] = name;
          descriptions[i] = "attribute " + name;
          openTypes[i] = getOpenTypeForIntern(depth + 1, (Class) attribute.getValue());
          i++;
    }
    type = new CompositeType(typeInterface.getName(), "description", names, descriptions, openTypes);
    return type;
  }
  
  /**
   * @param class1
   * @return
   */
  public static OpenType getOpenTypeFor(Class clazz) throws OpenDataException {
    return getOpenTypeForIntern(0, clazz);
  }

  /**
   * @param class1
   * @return
   */
  private static OpenType getOpenTypeForIntern(int depth, Class clazz) throws OpenDataException {
    if (depth > MAX_DEPTH) {
      throw new OpenDataException("maximum depth of nested structure exceeded or circle in structure definition");
    }
    OpenType result;
    int arrayDimension = 0;
    while(clazz.isArray()) {
      arrayDimension++;
      clazz = clazz.getComponentType();
    }
    String name = clazz.getName();
    if (BigDecimal.class.getName().equals(name)) {
      result = SimpleType.BIGDECIMAL;
    }
    else if (BigInteger.class.getName().equals(name)) {
      result = SimpleType.BIGINTEGER;
    }
    else if (Boolean.class.getName().equals(name)) {
      result = SimpleType.BOOLEAN;
    }
    else if (Boolean.TYPE.getName().equals(name)) {
      result = SimpleType.BOOLEAN;
    }
    else if (Byte.class.getName().equals(name)) {
      result = SimpleType.BYTE;
    }
    else if (Byte.TYPE.getName().equals(name)) {
      result = SimpleType.BYTE;
    }
    else if (Character.class.getName().equals(name)) {
      result = SimpleType.CHARACTER;
    }
    else if (Character.TYPE.getName().equals(name)) {
      result = SimpleType.CHARACTER;
    }
    else if (Date.class.getName().equals(name)) {
      result = SimpleType.DATE;
    }
    else if (Double.class.getName().equals(name)) {
      result = SimpleType.DOUBLE;
    }
    else if (Double.TYPE.getName().equals(name)) {
      result = SimpleType.DOUBLE;
    }
    else if (Float.class.getName().equals(name)) {
      result = SimpleType.FLOAT;
    }
    else if (Float.TYPE.getName().equals(name)) {
      result = SimpleType.FLOAT;
    }
    else if (Integer.class.getName().equals(name)) {
      result = SimpleType.INTEGER;
    }
    else if (Integer.TYPE.getName().equals(name)) {
      result = SimpleType.INTEGER;
    }
    else if (Long.class.getName().equals(name)) {
      result = SimpleType.LONG;
    }
    else if (Long.TYPE.getName().equals(name)) {
      result = SimpleType.LONG;
    }
    else if (ObjectName.class.getName().equals(name)) {
      result = SimpleType.OBJECTNAME;
    }
    else if (Short.class.getName().equals(name)) {
      result = SimpleType.SHORT;
    }
    else if (Short.TYPE.getName().equals(name)) {
      result = SimpleType.SHORT;
    }
    else if (String.class.getName().equals(name)) {
      result = SimpleType.STRING;
    }
    else {
      result = getCompositeTypeInt(depth + 1, clazz);
    }
    if (arrayDimension == 0) {
      return result;
    }
    else {
      return new ArrayType(arrayDimension, result);
    }
  }
  
}