/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.rmic.iiop;

import com.sap.engine.rmic.iiop.util.RepositoryID;
import com.sap.engine.rmic.log.RMICLogger;

import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import javax.rmi.PortableRemoteObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/*
 * Public class DescriptorWriter is used for passing
 * the class accessors to ClassDescriptor
 *
 * @author Ralitsa Bozhkova, Svetoslav Nedkov, Mladen Droshev
 * @version 6.30
 */

public class DescriptorWriter {

  private ClassDescriptor descriptor;
  private Class remoteClass;
  //private String destDir;
  private Class[] remoteInterfaces;
  private Hashtable remoteMethods = new Hashtable();
  //private String iiop;
  private String name;
  private String methodName;
  private String[] rmiRepositoryID;
  //private String idlRepositoryID;
  //private String[] attribute;
  private String[] superInterfaces;
  private ClassDescriptorField[] field;
  private ClassDescriptorMethod[] method;
  //private ClassDescriptorMethod[] parentMethod;
  private ClassDescriptorMethodParameter[] parameter;
  private ClassDescriptorMethodException[] exception;
  private ClassDescriptorReturnType returnType;
  private Class parent;
  private Class[] interfaces;
  private Method[] methods;
  //private Method[] parentMethods;
  private Class[] exceptions;
  private ExceptionHandler exHandler;
  private Class[] parameters;
  private Field[] fields;
  //private int countAttr;
  private final String yes = "Yes";
  private final String no = "No";
  //private boolean additional;
  private boolean interfaceFlag;
  private Hashtable access;
  private Method[] innerMethods;
  private ClassDescriptorMethod[] innerMethod;
  private ValueHandler vh;


  /**
   * Initialize descriptor
   *
   * @param _class represent class or interface which needs p4 stub and skel
   */
  public DescriptorWriter(Class _class, Hashtable access) throws java.io.IOException {
    remoteClass = _class;

    if (PortableRemoteObject.class.isAssignableFrom(remoteClass)) {
      //iiop = "iiop";
    }

    this.access = access;
    remoteMethods = new Hashtable();
    Vector tempInterfaces = new Vector();
    //get remote Interfaces and methods
    getRemoteInterfaces(remoteClass, tempInterfaces);
    getRemoteMethods(remoteClass, tempInterfaces, remoteMethods);

    //copy buffer into corresponding array
    if (tempInterfaces.size() > 0) {
      remoteInterfaces = new Class[tempInterfaces.size()];
      tempInterfaces.copyInto(remoteInterfaces);
    } else {
      remoteInterfaces = new Class[0];
    }

    exHandler = new ExceptionHandler();
    descriptor = new ClassDescriptor();
  }

  /**
   * Set remoteClass to descriptor so as an XML file
   * could be generated for it
   *
   * @return the descriptor that would be passed to DOM writer
   */
  public ClassDescriptor setClassToDescriptor() {
    try {
      name = remoteClass.getName();

      // class name and package
      if (name.indexOf(".") != -1) {
        descriptor.setName(name.substring(name.lastIndexOf(".") + 1));
        descriptor.setPackage(name.substring(0, name.lastIndexOf(".")));
      } else {
        descriptor.setName(name);
      }

      // superClass
      parent = remoteClass.getSuperclass();

      if (parent != null) {
        descriptor.setSuperClass(parent.getName());
      }

      interfaces = remoteInterfaces;

      // implemented interfaces
      if (interfaces != null) {
        superInterfaces = new String[interfaces.length];

        for (int k = 0; k < interfaces.length; k++) {
          superInterfaces[k] = interfaces[k].getName();
        }

        descriptor.setSuperInterface(superInterfaces);
      }

      // access flag and attributes
      int modifierClass = remoteClass.getModifiers();
      String modStr = Modifier.toString(modifierClass);

      if (Modifier.isPublic(modifierClass)) {
        descriptor.setAccessFlag("public");
      } else {
        descriptor.setAccessFlag(modStr);
      }

      if (Modifier.isAbstract(modifierClass)) {
        descriptor.setAttribute("abstract");
      }

      if (Modifier.isInterface(modifierClass)) {
        descriptor.setAttribute("interface");
        interfaceFlag = true;
      }

      //This is needed in the case of a class
      //in order to include the keyword 'class' in the attributes list
      if (!interfaceFlag) {
        descriptor.setAttribute("class");
      }

      if (vh == null) {
        vh = Util.createValueHandler();
      }

      rmiRepositoryID = new String[remoteInterfaces.length];
      for (int i = 0; i < remoteInterfaces.length; i++) {
        rmiRepositoryID[i] = vh.getRMIRepositoryID(remoteInterfaces[i]);
      }
      descriptor.setRMIRepositoryID(rmiRepositoryID);
      
      // if the remoteClass is interface
      if (interfaceFlag) {

        descriptor.setForStubName(name.substring(name.lastIndexOf(".") + 1));
      }
      
      // if the remoteClass is not an interface
      else {
        if (interfaces != null) {
          String interfName = interfaces[0].getName();

          if (interfName.indexOf(".") != -1) {
            descriptor.setInterfacePackage(interfName.substring(0, interfName.lastIndexOf(".")));
          }

          if (interfaces.length == 1) {
            descriptor.setForStubName(superInterfaces[0].substring(superInterfaces[0].lastIndexOf(".") + 1));
          } else {

            descriptor.setForStubName(name.substring(name.lastIndexOf(".") + 1));
          }
        }

      } //else
      Integer resourceId = null;
      if (access != null) {
        resourceId = (Integer) access.get(remoteClass);
      }

      if (resourceId != null) {
        descriptor.setResourceId(resourceId.toString());
      }

      // get and set remoteClass' methods
      setMethods();
      // get and set remoteClass' fields
      setFields();
    } catch (Exception e) {// $JL-EXC$
      RMICLogger.throwing(e);
    }
    return descriptor;
  }

  /**
   * A helper method for setting class's methods
   */
  private void setMethods() {
    if (!interfaceFlag) {
      Enumeration metKeys = remoteMethods.keys();
      method = new ClassDescriptorMethod[remoteMethods.size()];
      methods = new Method[remoteMethods.size()];
      int k = 0;

      while (metKeys.hasMoreElements()) {
        methodName = (String) metKeys.nextElement();
        methods[k] = (Method) remoteMethods.get(methodName);
        k++;
      }
    } else {
      methods = remoteClass.getMethods();

      if (methods != null) {
        method = new ClassDescriptorMethod[methods.length];
      }
    }

    String[] idlNames = IDLNames.getIDLnames(methods);

    for (int count = 0; count < methods.length; count++) {
      method[count] = new ClassDescriptorMethod();
      method[count].setName(methods[count].getName());
      method[count].setIDLname(idlNames[count]);
      // methods' access flags and attributes
      int modifierMethod = methods[count].getModifiers();
      String modifierStr = Modifier.toString(modifierMethod);

      if (Modifier.isPublic(modifierMethod)) {
        method[count].setAccessFlag("public");
      } else if (Modifier.isPrivate(modifierMethod)) {
        method[count].setAccessFlag("private");
      } else if (Modifier.isProtected(modifierMethod)) {
        method[count].setAccessFlag("protected");
      } else if (modifierStr != null) {
        method[count].setAttribute(modifierStr);
      }

      // methods' exceptions
      exceptions = exHandler.handler(methods[count].getExceptionTypes());

      if (exceptions != null) {
        exception = new ClassDescriptorMethodException[exceptions.length];

        for (int i = 0; i < exceptions.length; i++) {
          exception[i] = new ClassDescriptorMethodException();
          exception[i].setName(exceptions[i].getName().replace('$', '.')); // replace for inner class

          if (java.rmi.RemoteException.class.isAssignableFrom(exceptions[i])) {
            exception[i].setIsRemoteException(yes);
          } else {
            exception[i].setIsRemoteException(no);
          }

          if (exceptions[i].getName() != "java.rmi.RemoteException") {
            exception[i].setIDLRepID(replaceStr(new String(RepositoryID.getIDLRepositoryID(exceptions[i])), "$", "__"));
          }
        }

        method[count].setException(exception);
      }

      returnType = new ClassDescriptorReturnType();
      Class type = methods[count].getReturnType();
      String rType = signature(type);

      if (rType.equals("void")) {
        returnType.setIsReturnType(no);
      } else {
        returnType.setIsReturnType(yes);
      }
      String clasname = type.getName();
      if (clasname.indexOf(".") != -1) {
        returnType.setForStubName((clasname.substring(clasname.lastIndexOf(".") + 1)).replace('$', '.'));
      } else {
        returnType.setForStubName("NoStub");
      }
      returnType.setClassType((rType.replace('$', '.')));      //replace for inner class

      if (type.isInterface()) {
        returnType.setIsInterface(yes);
      } else {
        returnType.setIsInterface(no);
      }

      if (org.omg.CORBA.Object.class.isAssignableFrom(type)) {
        returnType.setToWriteAsObject(yes);
      } else {
        returnType.setToWriteAsObject(no);
      }

      innerMethods = type.getMethods(); //current methods
      innerMethod = new ClassDescriptorMethod[innerMethods.length];

      for (int c = 0; c < innerMethods.length; c++) {
        innerMethod[c] = new ClassDescriptorMethod();
        innerMethod[c].setName(innerMethods[c].getName());
        Class[] exc = new Class[innerMethods[c].getExceptionTypes().length];
        exc = innerMethods[c].getExceptionTypes();

        for (int rr = 0; rr < exc.length; rr++) {

        }

        exceptions = exHandler.handler(innerMethods[c].getExceptionTypes());

        if (exceptions != null) {
          exception = new ClassDescriptorMethodException[exceptions.length];

          for (int k = 0; k < exceptions.length; k++) {
            exception[k] = new ClassDescriptorMethodException();
            exception[k].setName(exceptions[k].getName().replace('$', '.'));    // replace for inner class

            if (java.rmi.RemoteException.class.isAssignableFrom(exceptions[k])) {
              exception[k].setIsRemoteException(yes);
            } else {
              exception[k].setIsRemoteException(no);
            }

            if (exceptions[k].getName() != "java.rmi.RemoteException") {
              exception[k].setIDLRepID(replaceStr(new String(RepositoryID.getIDLRepositoryID(exceptions[k])), "$", "__"));
            }
          }

          innerMethod[c].setException(exception);
        }
      }

      returnType.setMethod(innerMethod);
      method[count].setReturnType(returnType);
      //methods' parameters
      parameters = methods[count].getParameterTypes();

      if (parameters != null) {
        parameter = new ClassDescriptorMethodParameter[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
          parameter[i] = new ClassDescriptorMethodParameter();
          parameter[i].setType(signature(parameters[i]).replace('$', '.')); // replace for inner class

          if (parameters[i].isInterface()) {
            parameter[i].setInterface(yes);
          } else {
            parameter[i].setInterface(no);
          }
          String classsname = parameters[i].getName();
          if (classsname.indexOf(".") != -1) {
            parameter[i].setForStubName((classsname.substring(classsname.lastIndexOf(".") + 1)).replace('$', '.'));
          } else {
            parameter[i].setForStubName("NoStub");
          }

          if (java.rmi.Remote.class.isAssignableFrom(parameters[i])) {
            parameter[i].setRemote(yes);
          } else {
            parameter[i].setRemote(no);
          }

          if (org.omg.CORBA.Object.class.isAssignableFrom(parameters[i])) {
            parameter[i].setToWriteAsObject(yes);
          } else {
            parameter[i].setToWriteAsObject(no);
          }

          innerMethods = parameters[i].getMethods(); // current methods
          innerMethod = new ClassDescriptorMethod[innerMethods.length];
          //boolean isEmptyExce = true;

          for (int c = 0; c < innerMethods.length; c++) {
            innerMethod[c] = new ClassDescriptorMethod();
            innerMethod[c].setName(innerMethods[c].getName());
            exceptions = exHandler.handler(innerMethods[c].getExceptionTypes());

            if (exceptions != null) {
              //isEmptyExce = false;
              exception = new ClassDescriptorMethodException[exceptions.length];

              for (int k = 0; k < exceptions.length; k++) {
                exception[k] = new ClassDescriptorMethodException();
                exception[k].setName(exceptions[k].getName().replace('$', '.'));    //replace for inner class

                if (java.rmi.RemoteException.class.isAssignableFrom(exceptions[k])) {
                  exception[k].setIsRemoteException(yes);
                } else {
                  exception[k].setIsRemoteException(no);
                }

                if (exceptions[k].getName() != "java.rmi.RemoteException") {
                  exception[k].setIDLRepID(replaceStr(new String(RepositoryID.getIDLRepositoryID(exceptions[k])), "$", "__"));
                }
              }

              innerMethod[c].setException(exception);
            }
          }

          parameter[i].setMethod(innerMethod);
        }

        method[count].setParameter(parameter);
      }

      // methods' return types
      //      method[count].setType(returnType);
      if (java.rmi.Remote.class.isAssignableFrom(type)) {
        method[count].setRemoteType(yes);
      } else {
        method[count].setRemoteType(no);
      }
    }//for each method

    descriptor.setMethod(method);
  }

//  private void setInnerMethods(Class paramClass) {
//  }

//  private boolean isThrownRemoteException(java.lang.reflect.Method[] mets) {
//    if (mets == null) {
//      return false;
//    } else {
//      Class[] exc = null;
//
//      for (int i = 0; i < mets.length; i++) {
//        if ((exc = mets[i].getExceptionTypes()) != null) {
//          for (int j = 0; j < exc.length; j++) {
//            if (java.rmi.RemoteException.class.isAssignableFrom(exc[j])) {
//              return true;
//            }
//          }
//        }
//      }
//    }
//    return false;
//  }

  private String replaceStr(String base, String old, String rep) {
    int i = base.indexOf(old);
    if (i != -1) {
      return base.substring(0, (i - 1)) + rep + base.substring(i + 1);
    } else {
      return base;
    }

  }

  /**
   * A helper method for setting class's fields
   */
  private void setFields() {
    fields = remoteClass.getFields();

    if (fields != null) {
      field = new ClassDescriptorField[fields.length];

      for (int count = 0; count < fields.length; count++) {
        field[count] = new ClassDescriptorField();
        field[count].setName(fields[count].getName());
        // fields' access flags and attributes
        int modifierField = fields[count].getModifiers();
        String modifStr = Modifier.toString(modifierField);

        if (Modifier.isPublic(modifierField)) {
          field[count].setAccessFlag("public");
        } else if (Modifier.isPrivate(modifierField)) {
          field[count].setAccessFlag("private");
        } else if (Modifier.isProtected(modifierField)) {
          field[count].setAccessFlag("protected");
        } else if (!modifStr.equals("")) {
          field[count].setAttribute(modifStr);
        }

        //        if (!modifStr.equals("")) {
        //          StringTokenizer stf = new StringTokenizer(modifStr);
        //
        //          while (stf.hasMoreTokens()) {
        //            String tempf = stf.nextToken();
        //
        //            if (tempf.equals("public")) {
        //              field[count].setAccessFlag("public");
        //            } else if (tempf.equals("private")) {
        //              field[count].setAccessFlag("private");
        //            } else if (tempf.equals("protected")) {
        //              field[count].setAccessFlag("protected");
        //            } else if (tempf.equals("package")) {
        //              field[count].setAccessFlag("package");
        //            } else {
        //              field[count].setAttribute(tempf);
        //            }
        //          }// while
        //        }// if
        //fields' types
        String fieldType = signature(fields[count].getType());
        field[count].setType(fieldType);
      } //for each field

      descriptor.setField(field);
    } //if
  }

  /**
   * Check class _target for all remote interfaces
   *
   * @param _target         class for checking
   * @param interfaceBuffer
   */
  public static void getRemoteInterfaces(Class _target, Vector interfaceBuffer) {
    if (_target != null) {
//      getRemoteInterfaces(_target.getSuperclass(), interfaceBuffer);
//      Class[] temp_interfaces = _target.getInterfaces();
//
//      for (int k = 0; k < temp_interfaces.length; k++) {
//        update(temp_interfaces[k], interfaceBuffer);
//      }
      if (_target.isInterface() &&
              !java.rmi.Remote.class.equals(_target) &&
              java.rmi.Remote.class.isAssignableFrom(_target)) {
        interfaceBuffer.add(_target);
      }
      Class[] interf = _target.getInterfaces();
      for (int i = 0; i < interf.length; i++) {
        getRemoteInterfaces(interf[i], interfaceBuffer);
      }

      Class parent = _target.getSuperclass();
      getRemoteInterfaces(parent, interfaceBuffer);
    }
  }

  public static void getIDLRemoteMethods(Class remoteClass, Vector _target, Hashtable methodBuffer) {
    for (int k = 0; k < _target.size(); k++) {
      Method[] methods = ((Class) _target.elementAt(k)).getMethods();

      //if methodi is not in methodsBuffer - add
      if (methods != null) {
        String[] idlNames = IDLNames.getIDLnames(methods);
        for (int i = 0; i < methods.length; i++) {
          if ((methods[i].getName()).startsWith("<clinit>")) {
            continue;
          }

          if (!methodBuffer.containsKey(idlNames[i])) {
            Method mm = null;
            try {
              mm = remoteClass.getMethod(methods[i].getName(), methods[i].getParameterTypes());
            } catch (Exception e) {//$JL-EXC$
              mm = methods[i];
            }

            methodBuffer.put(idlNames[i], mm);
          }
        }
      }
    }
  }

  public static void getRemoteMethods(Class remoteClass, Vector _target, Hashtable methodBuffer) {
    for (int k = 0; k < _target.size(); k++) {
      Method[] methodi = ((Class) _target.elementAt(k)).getMethods();

      //if methodi is not in methodsBuffer - add
      if (methodi != null) {
        for (int i = 0; i < methodi.length; i++) {
          if ((methodi[i].getName()).startsWith("<clinit>")) {
            continue;
          }

          String methodiName = methodi[i].toString();
          methodiName = methodiName.substring(methodiName.indexOf('('), methodiName.lastIndexOf(')') + 1);
          methodiName = methodi[i].getName() + methodiName;

          if (!methodBuffer.containsKey(methodiName)) {
            Method mm = null;
            try {
              mm = remoteClass.getMethod(methodi[i].getName(), methodi[i].getParameterTypes());
            } catch (Exception e) {//$JL-EXC$
              mm = methodi[i];
            }

            methodBuffer.put(methodiName, mm);
          }
        }
      }
    }
  }

  /**
   * Especially for use in class StubTieGenerator
   *
   * @return hashtable with method name maped to a method
   */
  public Hashtable getRemoteMethods() {
    return remoteMethods;
  }

  /**
   * Especially for use in class StubTieGenerator
   *
   * @return array of remote interfaces
   */
  public Class[] getRemoteInterfaces() {
    return remoteInterfaces;
  }

  /**
   * This is a helper class for selecting remote methods
   *
   * @param _interface - the remote interface
   * @param buffer
   * @return true if this class is to be added to the buffer and false otherwise
   */
  public boolean update(Class _interface, Vector buffer) {
    if (!java.rmi.Remote.class.isAssignableFrom(_interface)) {
      return false;
    }

    for (int i = 0; i < buffer.size(); i++) {
      if (_interface.isAssignableFrom((Class) buffer.elementAt(i))) {
        return false;
      }

      if (((Class) buffer.elementAt(i)).isAssignableFrom(_interface)) {
        buffer.removeElementAt(i--);
      }
    }

    buffer.addElement(_interface);
    return true;
  }

  /**
   * Converts the return types according
   * to the Java Virtual Machine Specification
   *
   * @param _class - the target class
   */
  public static String signature(Class _class) {
    String type = _class.getName();
    int last = type.lastIndexOf('[');

    if (last < 0) {
      return type;
    }

    char descriptor = type.charAt(last + 1);
    type = type.substring(last + 1);
    String arrDimension = "[]";

    for (int i = 0; i < last; i++) {
      arrDimension += "[]";
    }

    switch (descriptor) {
      case 'B':
        {
          type = "byte" + arrDimension;
          break;
        }
      case 'C':
        {
          type = "char" + arrDimension;
          break;
        }
      case 'D':
        {
          type = "double" + arrDimension;
          break;
        }
      case 'F':
        {
          type = "float" + arrDimension;
          break;
        }
      case 'I':
        {
          type = "int" + arrDimension;
          break;
        }
      case 'J':
        {
          type = "long" + arrDimension;
          break;
        }
      case 'S':
        {
          type = "short" + arrDimension;
          break;
        }
      case 'Z':
        {
          type = "boolean" + arrDimension;
          break;
        }
      case 'L':
        {
          type = type.substring(1, type.length() - 1) + arrDimension;
          break;
        }
    }

    return type;
  }

}

