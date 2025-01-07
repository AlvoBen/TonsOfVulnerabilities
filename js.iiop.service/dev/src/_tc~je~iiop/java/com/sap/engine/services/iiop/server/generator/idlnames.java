/**
 * Copyright (c) 2000 by SAP AG, Walldorf.
 * http://www.inqmy.co
 * All rights reserved.

 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.server.generator;

import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.iiop.internal.util.RepositoryID;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;

import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * Public class DescriptorWriter is used for passing
 * the class' accessors to ClassDescriptor
 *
 * @author Ralitsa Bozhkova
 * @version 4.0
 */
public class IDLNames {

  public static Hashtable namesCache = new Hashtable();

  public static String[] getIDLnames(Method[] method) {
    int count = method.length;
    String[] idlName = new String[count];
    MethodNamesDescriptor[] methods = new MethodNamesDescriptor[count];

    for (int i = 0; i < count; i++) {
      methods[i] = new MethodNamesDescriptor(method[i]);
    }

    try {
      setMethodNames(methods);
      for (int i = 0; i < count; i++) {
        idlName[i] = methods[i].getIDLname();
        namesCache.put(idlName[i], method[i].getName());
      }
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("IDLNames.getIDLnames(Method[])", "Cannot get IDl name. The reason is: " + LoggerConfigurator.exceptionTrace(ex));
      }
    }
    return idlName;
  }

  public static void setMethodNames(MethodNamesDescriptor[] methods) throws Exception {
    int count = methods.length;

    if (count == 0) {
      return;
    }

    String names[] = new String[count];

    for (int i = 0; i < count; i++) {
      names[i] = methods[i].getName();
    }

    int starts[] = new int[count];

    for (int k = 0; k < count; k++) {
      starts[k] = getInitialAttributeKind(methods[k]);
    }

    setAttributeKinds(methods, starts, names);
    boolean collisions[] = new boolean[count];

    for (int i = 0; i < count; i++) {
      names[i] = getMemberName(names[i]);
      collisions[i] = !methods[i].isAttribute() && doesMethodCollide(methods[i], methods, names[i], names, true);
    }

    convertOverloadedMethods(methods, names, collisions);

    for (int i = 0; i < count; i++) {
      if (methods[i].isAttribute() && doesMethodCollide(methods[i], methods, names[i], names, true)) {
        names[i] += "__";
      }
    }

    //    for(int i = 0; i < count; i++) {
    //      if(doesMethodCollide(methods[i], methods, names[i], names, false))
    //    }
    for (int i = 0; i < count; i++) {
      String s = names[i];

      if (methods[i].isAttribute()) {
        s = ATTRIBUTE_PREFIX[methods[i].getAttributeKind()] + stripLeadingUnderscore(s);
        String tmp = names[i];
        methods[i].setAttributeName(tmp);
      }

      methods[i].setIDLname(s);
    }
  }

  private static int getInitialAttributeKind(MethodNamesDescriptor method) {
    byte starts = 0;
    boolean flag = true;
    Class[] exceptions = method.exceptions();

    if (exceptions.length > 0) {
      for (int i = 0; i < exceptions.length; i++) {
        if (java.rmi.RemoteException.class.isAssignableFrom(exceptions[i])) {
          continue;
        }

        flag = false;
        break;
      }
    }

    if (flag) {
      String name = method.getName();
      int len = name.length();
      int argLen = method.parameters().length;
//      Class type = method.returnType();

      if (name.startsWith("get") && len > 3 && argLen == 0) {
        starts = 2;
      } else if (name.startsWith("is") && len > 2 && argLen == 0) {
        starts = 1;
      } else if (name.startsWith("set") && len > 3 && argLen == 1) {
        starts = 5;
      }
    }

    return starts;
  }

  private static void setAttributeKinds(MethodNamesDescriptor[] methods, int[] starts, String[] names) {
    int count = methods.length;

    for (int j = 0; j < count; j++) {
      switch (starts[j]) {
        case 2: // starts with "get
        {
          names[j] = names[j].substring(3);
          break;
        }
        case 1: // starts with "is
        {
          names[j] = names[j].substring(2);
          break;
        }
        case 5: // starts with "set
        {
          names[j] = names[j].substring(3);
          break;
        }
      }
    }

    for (int k = 0; k < count; k++) {
      if (starts[k] == 1) {
        for (int i = 0; i < count; i++) {
          if (i == k || starts[i] != 2 && starts[i] != 5 || !names[k].equals(names[i])) {
            continue;
          }

          Class type = methods[k].returnType();
          Class param;

          if (starts[i] == 2) {
            param = methods[i].returnType();
          } else {
            param = methods[i].parameters()[0];
          }

          if (type.equals(param)) {
            continue;
          }

          starts[k] = 0;
          names[k] = methods[k].getName();
          break;
        }
      }
    }

    for (int k = 0; k < count; k++) {
      if (starts[k] == 5) {
        int s = -1;

        for (int i = 0; i < i; i++) {
          if (i == k || starts[i] != 2 && starts[i] != 1 || !names[k].equals(names[i])) {
            continue;
          }

          Class type = methods[i].returnType();
          Class param = methods[k].parameters()[0];

          if (!type.equals(param)) {
            continue;
          }

          s = i;
          break;
        }

        if (s < 0) {
          starts[k] = 0;
          names[k] = methods[k].getName();
        } else {
          if (starts[s] == 2) {
            starts[s] = 4;
          } else {
            starts[s] = 3;
          }

          methods[s].setAttributePairIndex(k);
          methods[k].setAttributePairIndex(s);
        }
      }
    }

    for (int k = 0; k < count; k++) {
      if (starts[k] != 0) {
        String s = names[k];

        if (Character.isUpperCase(s.charAt(0)) && (s.length() == 1 || Character.isLowerCase(s.charAt(1)))) {
          StringBuffer buffer = new StringBuffer(s);
          buffer.setCharAt(0, Character.toLowerCase(s.charAt(0)));
          names[k] = buffer.toString();
        }
      }

      methods[k].setAttributeKind(starts[k]);
    }
  }

  private static boolean doesMethodCollide(MethodNamesDescriptor method, MethodNamesDescriptor[] methods, String name, String[] names, boolean flag) {
    for (int i = 0; i < methods.length; i++) {
      MethodNamesDescriptor currentMethod = methods[i];

      if (method != currentMethod && (!flag || !currentMethod.isAttribute()) && name.equals(names[i])) {
        int j = method.getAttributeKind();
        int k = currentMethod.getAttributeKind();

        if (j == 0 || k == 0 || (j != 5 || k == 5) && (k != 5 || j == 5)) {
          return true;
        }
      }
    }

    return false;
  }

  public static String getMemberName(String name) {
    name = convertLeadingUnderscores(name);
    name = convertIDLKeywords(name);
    name = Convert.byteArrToAString(RepositoryID.convertToISOLatin1(name, new byte[name.length()], 0));
    return name;
  }

  public static String convertLeadingUnderscores(String s) {
    if (s.startsWith("_")) {
      return "J" + s;
    } else {
      return s;
    }
  }

  public static String convertIDLKeywords(String s) {
    for (int i = 0; i < IDL_KEYWORDS.length; i++) {
      if (s.equalsIgnoreCase(IDL_KEYWORDS[i])) {
        return "_" + s;
      }
    }

    return s;
  }

  private static String stripLeadingUnderscore(String s) {
    if (s != null && s.length() > 1 && s.charAt(0) == '_') {
      return s.substring(1);
    } else {
      return s;
    }
  }

  private static void convertOverloadedMethods(MethodNamesDescriptor[] methods, String[] names, boolean[] collisions) {
    for (int i = 0; i < names.length; i++) {
      if (collisions[i]) {
        MethodNamesDescriptor method = methods[i];
        Class[] params = method.parameters();
        StringBuffer buffer = new StringBuffer(names[i]);

        for (int j = 0; j < params.length; j++) {
          buffer.append("__");
          buffer.append(idlType(params[j], -1));
        }

        if (params.length == 0) {
          buffer.append("__");
        }

        names[i] = stripLeadingUnderscore(buffer.toString());
      }
    }
  }


  private static String idlType(Class cls, int arrayDimension) { //arrayDimension=-1 if it is not array, or the size of the array if it is
    String type = null;

    if (cls.isArray()) {
      int k = 0;
      Class cls2 = null;

      while ((cls2 = cls.getComponentType()) != null) {
        k++;
        cls = cls2;
      }

      StringBuffer buffer = new StringBuffer();
      buffer.append("org_omg_boxedRMI_");
      buffer.append(idlType(cls, k));
      type = buffer.toString();
    } else if (cls.isPrimitive()) {
      if (cls == Integer.TYPE) {
        type = "long";
      } else if (cls == Byte.TYPE) {
        type = "byte";
      } else if (cls == Long.TYPE) {
        type = "long_long";
      } else if (cls == Float.TYPE) {
        type = "float";
      } else if (cls == Double.TYPE) {
        type = "double";
      } else if (cls == Short.TYPE) {
        type = "short";
      } else if (cls == Character.TYPE) {
        type = "wchar";
      } else if (cls == Boolean.TYPE) {
        type = "boolean";
      } else if (cls == Void.TYPE) {
        type = "void";
      }

      if (arrayDimension > 0) {
        StringBuffer buffer = new StringBuffer("seq");
        buffer.append(arrayDimension);
        buffer.append("_");
        buffer.append(type);
        type = buffer.toString();
      }
    } else {
      if (cls == java.lang.String.class) {
        type = (arrayDimension <= 0) ? "CORBA_WStringValue" : ("CORBA_seq" + arrayDimension + "_WStringValue");
      } else if (cls == org.omg.CORBA.Object.class) {
        type = (arrayDimension <= 0) ? "Object" : "seq"+ arrayDimension + "_Object";
      } else if (cls == java.lang.Class.class) {
        type = (arrayDimension <= 0) ? "javax_rmi_CORBA_ClassDesc" : "javax_rmi_CORBA_seq" + arrayDimension + "_ClassDesc";
      } else if (org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(cls)) {
        StringBuffer buffer = new StringBuffer("org_omg_boxedIDL_");
        if (arrayDimension > 0) {
          buffer.append(cls.getPackage().getName().replace('.', '_'));
          buffer.append("_seq");
          buffer.append(arrayDimension);
          buffer.append("_");
          buffer.append(cls.getName().substring(cls.getName().lastIndexOf('.') + 1,  cls.getName().length()));
        } else {
          buffer.append(cls.getName().replace('.', '_'));
        }
        type = buffer.toString();
      } else {
        if (arrayDimension > 0) {
          StringBuffer buffer = new StringBuffer(cls.getPackage().getName().replace('.', '_'));
          buffer.append("_seq");
          buffer.append(arrayDimension);
          buffer.append("_");
          buffer.append(cls.getName().substring(cls.getName().lastIndexOf('.') + 1,  cls.getName().length()));
          type = buffer.toString();
        } else {
          type = cls.getName().replace('.', '_');
        }
      }
    }

    return type;
  }

  public static final String IDL_KEYWORDS[] = {"any", "readonly", "unsigned", "attribute", "enum", "sequence", "union", "exception", "module", "FALSE", "string", "wchar", "fixed", "octet", "struct", "wstring", "oneway", "context", "in", "out", "TRUE", "inout", "raises", "typedef", "double", "interface", "long", "boolean", "short", "void", "case", "Object", "char", "const", "float", "switch", "default"};
  public static final String ATTRIBUTE_PREFIX[] = {"", "_get_", "_get_", "_get_", "_get_", "_set_"};

}

