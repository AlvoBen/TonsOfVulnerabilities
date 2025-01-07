/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.exception.P4Logger;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class represents java code generator for p4 stubs and skeletons.
 *
 * @author Georgi Stanev, Mladen Droshev
 * @version 7.0
 */
public class P4StubSkeletonGenerator {

  public static final String P4_REMOTE_OBJECT_NAME = "com.sap.engine.services.rmi_p4.P4RemoteObject";

  private static String tab1 = "\t";
  private static String tab2 = "\t\t";
  private static String tab3 = "\t\t\t";
  private static String tab4 = "\t\t\t\t";
  private static String tab5 = "\t\t\t\t\t";
  private static String tab6 = "\t\t\t\t\t\t";
  private Class remote_class = null;
  private String destDir = null;
  private String p4 = "";
  private Class[] remoteInterfaces = {};
  private Hashtable remoteMethods = new Hashtable();
  private boolean onlyStub = false;
  private Vector files = new Vector();
  private Class p4_remote_object = null;

  /**
   * Initialize generator
   *
   * @param _class   represent class or interface which needs need p4 stub and skel
   * @param _destDir directory name where java files will be generated
   */
  public P4StubSkeletonGenerator(Class _class, String _destDir, Hashtable access) throws java.io.IOException {
    this(_class, _destDir);
  }

  /**
   * Initialize generator
   *
   * @param _class   represent class or interface which needs need p4 stub and skel
   * @param _destDir directory name where java files will be generated
   */
  public P4StubSkeletonGenerator(Class _class, String _destDir) throws java.io.IOException {

    try {
      this.p4_remote_object = Class.forName(P4_REMOTE_OBJECT_NAME);
    } catch (ClassNotFoundException e) {
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "P4StubSkeletonGenerator(Class, String)", "Class P4RemoteObject was not found. RMI-P4 service will not work. Check if you have 'sap.com~tc~je~clientlib~impl.jar' in your classpath. \r\nException: {0}", "ASJ.rmip4.cf2038", new Object[]{P4Logger.exceptionTrace(e)});
      }
    }
    remote_class = _class;

    if (!p4_remote_object.isAssignableFrom(remote_class)) {
      p4 = "p4";
    }

    destDir = _destDir;

    if (_class.isInterface() || Proxy.isProxyClass(_class)) {
      onlyStub = true;
    }

    Vector temp_interfaces = new Vector();
    //get remote Interfaces and methods
    getRemoteInterfaces(remote_class, temp_interfaces);
    getRemoteMethods(temp_interfaces, remoteMethods);

    //copy buffer into corresponding array
    if (temp_interfaces.size() > 0) {
      remoteInterfaces = new Class[temp_interfaces.size()];
      temp_interfaces.copyInto(remoteInterfaces);
    }
  }

  /**
   * @param applyExecutionContext
   * @deprecated
   */
  public void setApplyExecutionContext(boolean applyExecutionContext) {
//    this.applyExecutionContext = applyExecutionContext;
  }

  /**
   * Check class _target for all remote interfaces
   *
   * @param _target         class for checking
   * @param interfaceBuffer
   */

  private void getRemoteInterfaces(Class _target, Vector interfaceBuffer) {
    //_target is null when previous call _target is java.lang.Object or interface
    if (_target != null) {
      //get remote interfaces from supperclass
      getRemoteInterfaces(_target.getSuperclass(), interfaceBuffer);
      Class[] temp_interfaces = _target.getInterfaces();

      for (int k = 0; k < temp_interfaces.length; k++) {
        //get remote inheriting interfaces from consecutive interface
        update(temp_interfaces[k], interfaceBuffer);
      }
    }
  }

  public void getRemoteMethods(Vector _target, Hashtable methodBuffer) {
    for (int k = 0; k < _target.size(); k++) {
      Method[] methods = ((Class) _target.elementAt(k)).getMethods();
      String methodName = "";

      //if methods is not in methodsBuffer - add
      for (int i = 0; i < methods.length; i++) {
        if ((methods[i].getName()).startsWith("<clinit>")) {
          continue;
        }

        methodName = methods[i].toString();
        methodName = methodName.substring(methodName.indexOf('('), methodName.lastIndexOf(')') + 1);
        methodName = methods[i].getName() + methodName;

        if (!methodBuffer.contains(methodName)) {
          methodBuffer.put(methodName, methods[i]);
        }
      }
    }
  }

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
   * Start generator
   *
   * @throws java.io.IOException
   */
  public Vector generate() throws java.io.IOException {

    if (!onlyStub) {
      generateSkeleton();

      for (int i = 0; i < remoteInterfaces.length; i++) {
        generateStub(remoteInterfaces[i]);
      }
    } else {
      generateStub(remote_class);
    }

    return files;
  }

  /**
   * Generate Stub
   *
   * @throws java.io.IOException
   */
  public void generateStub(Class remote_interface) throws java.io.IOException {
    Method[] methods1 = remote_interface.getMethods();
    String stubName = makeStubName(remote_interface.getName());
    //make stub file and write header
    PrintWriter file = makeFile(stubName, remote_interface);
    //write import classes
    file.println("\r\n");
    file.println("import com.sap.engine.services.rmi_p4.*;");
    file.println("\r\n");
    file.println("/**");
    file.println("*");
    file.println("* @author  SAP's RMIC Generator");
    file.println("* @version SAP Java EE Application Server");
    file.println("*/");
    //write stub class name
    file.print("public class " + stubName);
    file.println(" extends com.sap.engine.services.rmi_p4.StubImpl ");
    file.print(tab2 + "implements " + remote_interface.getName() + " {");
    file.println("\r\n");
    //write field Operations
    file.print(tab1 + "private static final String[] operations = {");
    Vector vec1 = new Vector();
    Vector vec2 = new Vector();

    for (int i = 0; i < methods1.length; i++) {
      if ((methods1[i].getName()).startsWith("<clinit>")) {
        continue;
      }

      String methodName = "";
      methodName = methods1[i].toString();
      methodName = methodName.substring(methodName.indexOf('('), methodName.lastIndexOf(')') + 1);
      methodName = methods1[i].getName() + methodName;

      if (!vec1.contains(methodName)) {
        vec1.add(methodName);
        vec2.add(methods1[i]);
        file.print("\r\n" + tab3 + "\"" + methodName + "\"" + ((i == methods1.length - 1) ? "" : ","));
      }
    }

    file.println("};\r\n");
    Method[] methods = (Method[]) vec2.toArray(new Method[0]);
    //write getOperations() method
    file.println(tab1 + "public String[] p4_getOperations() {");
    file.println(tab2 + "return operations;");
    file.println(tab1 + "}\r\n");



    //write methods
    for (int p = 0; p < methods.length; p++) {
      //get consecutive method
      Class returnType = methods[p].getReturnType();
      Class[] exceptions = methods[p].getExceptionTypes();
      boolean declaredRemoteException = false;
      if (exceptions.length != 0) {
        exceptions = (new com.sap.engine.services.rmi_p4.exception.ExceptionHandler()).handler(exceptions);
      }
      for (int i = 0; i < exceptions.length; i++) {
        if (java.rmi.RemoteException.class.equals(exceptions[i])) {
          declaredRemoteException = true;
          break;
        }
      }
      Class[] parameters = methods[p].getParameterTypes();
      int modifier = methods[p].getModifiers();

      //
      //write modifier
      if (Modifier.isPublic(modifier)) {
        file.print(tab1 + "public ");
      } else if (Modifier.isPrivate(modifier)) {
        file.print(tab1 + "private ");
      } else if (Modifier.isProtected(modifier)) {
        file.print(tab1 + "protected ");
      }

      //
      //write return type
      file.print(nameToType(returnType) + " ");
      //
      //write method name
      file.print(methods[p].getName() + "(");

      //
      //write parameters
      for (int i = 0; i < parameters.length; i++) {
        file.print(nameToType(parameters[i]) + " _param" + i);
        file.print((i == parameters.length - 1) ? ")" : ", ");
      }

      file.print((parameters.length == 0) ? ") " : " ");
      //
      //write throws exceptions
      file.print((exceptions.length == 0) ? " {\r\n" : "throws ");

      for (int i = 0; i < exceptions.length; i++) {
        file.print(exceptions[i].getName().replace('$', '.'));
        file.print((i == exceptions.length - 1) ? " {\r\n" : ", ");
      }
      String mName = "";
      mName = methods[p].toString();
      mName = mName.substring(mName.indexOf('('), mName.lastIndexOf(')') + 1);
      mName = methods[p].getName() + mName;
      //
      //write method body
      file.print("\r\n");
      file.println(tab2 + "if (isLocal) {");
      file.println(tab3 + "java.rmi.Remote remote;");
      file.println(tab3 + "if (p4remote == null) {");
      if (declaredRemoteException) {
        file.println(tab4 + "throw new java.rmi.NoSuchObjectException(\"\");");
      } else {
        file.println(tab4 + "throw (com.sap.engine.services.rmi_p4.P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);");
      }

      file.println(tab3 + "} else {");
      String tab = tab4;
      if (!declaredRemoteException) {
        file.println(tab4 + "try {");
        tab = tab5;
      }
      file.println(tab + "remote = p4remote.delegate();");
      if (!declaredRemoteException) {
        file.println(tab4 + "} catch (java.rmi.NoSuchObjectException nso) {");
        file.println(tab5 + "//$JL-EXC$ exclude for JLin");
        file.println(tab5 + "throw (com.sap.engine.services.rmi_p4.P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);");
        file.println(tab4 + "}");
      }

      file.println(tab3 + "}");
      file.println(tab3 + "try {");

      file.print(tab4 + remote_interface.getName() + " remoteInterface = ");
      file.println("(" + remote_interface.getName() + ") remote;");
      boolean useStreamss = false;
      for (int i = 0; i < parameters.length; i++) {
        if (!checkElementType(parameters[i])) {
          useStreamss = true;
        }
      }
      if (useStreamss) {
        file.println(tab4 + "ReplicateOutputStream out1 = p4_getReplicateOutput();");
        file.println(tab4 + "ReplicateInputStream inn1 = p4_getReplicateInput(out1);");
      }
      if (returnType.getName().equals("void")) {
        file.print(tab4 + "remoteInterface." + methods[p].getName() + "(");
        // replicating method parameters !!!
        file.print(replicateMethodParameters(parameters));
        file.println(");");
        file.println(tab4 + "return;");
      } else {
        String cast = "";

        if (!checkElementType(returnType)) {
          cast = "(" + nameToType(returnType) + ") P4ObjectBroker.init().narrow(p4_replicate(";
          file.print(tab4 + "return " + cast + "remoteInterface." + methods[p].getName() + "(");
          // replicating method parameters !!!
          file.print(replicateMethodParameters(parameters));
          file.println(")), " + nameToType(returnType) + ".class);");

        } else {
          file.print(tab4 + "return remoteInterface." + methods[p].getName() + "(");
          // replicating method parameters !!!
          file.print(replicateMethodParameters(parameters));
          file.println(");");
        }
      }
      file.println(tab3 + "} catch (java.lang.ClassCastException rex) {");
      file.println(tab5 + "//$JL-EXC$ exclude for JLin");
      boolean useStreamsss = false;
      for (int i = 0; i < parameters.length; i++) {
        if (!checkElementType(parameters[i])) {
          useStreamsss = true;
        }
      }
      if (useStreamsss) {
        file.println(tab4 + "ReplicateOutputStream outt = p4_getReplicateOutput();");
        file.println(tab4 + "ReplicateInputStream inn = p4_getReplicateInput(outt);");
      }
      file.print(tab4 + "Object[] params = new Object[]{");
      for (int i = 0; i < parameters.length; i++) {
        if (parameters[i].isPrimitive() || parameters[i].isArray()) {
          if (ioType(parameters[i]).equals("Int")) {
            file.print("new Integer(_param" + new Integer(i).toString() + ")");
          } else if (ioType(parameters[i]).equals("Char")) {
            file.print("new Character(_param" + new Integer(i).toString() + ")");
          } else if (parameters[i].isArray() && checkElementType(parameters[i])) {
            file.print("_param" + new Integer(i).toString());
          } else if (parameters[i].isArray() && !checkElementType(parameters[i])) {
            file.print("p4_replicateWithStreams(inn,outt,_param" + new Integer(i).toString() + ")");
          } else {
            file.print("new " + ioType(parameters[i]) + "(" + " _param" + new Integer(i).toString() + ")");
          }
        } else if (!nameToType(parameters[i]).startsWith("java.lang.String")) {
          file.print("p4_replicateWithStreams(inn,outt,");
          file.print("_param" + new Integer(i).toString());
          file.print(")");
        } else {
          file.print("_param" + new Integer(i).toString());
        }

        if (i < (parameters.length - 1)) {
          file.print(",");
        }
      }
      file.println("};");
      file.print(tab4 + "Class[] p = new Class[]{");
      for (int i = 0; i < parameters.length; i++) {
        if (parameters[i].isPrimitive() || (parameters[i].isArray() && checkElementType(parameters[i]))) {
          file.print(nameToType(parameters[i]) + ".class");
        } else if (!nameToType(parameters[i]).startsWith("java.lang.String")) {
          file.print("(Class) p4_replicateWithStreams(inn,outt," + nameToType(parameters[i]) + ".class)");
        } else {
          file.print(nameToType(parameters[i]) + ".class");
        }
        
        if (i < (parameters.length - 1)) {
          file.print(",");
        }
      }
      file.println("};");
      file.println(tab4 + "try { ");
      if (!returnType.getName().equalsIgnoreCase("void")) {
        file.print(tab5 + "return ");
        if (!returnType.isPrimitive() && !returnType.equals(java.lang.String.class)) {
          file.println("(" + nameToType(returnType) + ")P4ObjectBroker.init().narrow(p4_replicate(p4_invokeReflect(remote,\"" + methods[p].getName() + "\",params,p))," + nameToType(returnType) + ".class);");
        } else {
          if (ioType(returnType).equals("Int")) {
            file.println("((Integer) p4_invokeReflect(remote,\"" + methods[p].getName() + "\",params,p)).intValue();");
          } else if (ioType(returnType).equals("Char")) {
            file.println("((Character) p4_invokeReflect(remote,\"" + methods[p].getName() + "\",params, p)).charValue();");
          } else if (returnType.equals(java.lang.String.class)) {
            file.println("((String) p4_invokeReflect(remote,\"" + methods[p].getName() + "\",params,p));");
          } else {
            file.println("((" + ioType(returnType) + ") p4_invokeReflect(remote,\"" + methods[p].getName() + "\",params,p))." + returnType + "Value();");
          }
        }
      } else {
        file.println(tab5 + "p4_invokeReflect(remote,\"" + methods[p].getName() + "\",params,p);");
        file.println(tab5 + "return ;");
      }

      file.println(tab4 + "} catch (java.lang.NoSuchMethodException nsme) {");
      file.println(tab5 + "//$JL-EXC$ exclude for JLin");
      file.println(tab5 + "throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);");
      file.println(tab4 + "} catch (java.lang.IllegalAccessException iae) {");
      file.println(tab5 + "//$JL-EXC$ exclude for JLin");
      file.println(tab5 + "throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);");
      file.println(tab4 + "} catch (java.lang.reflect.InvocationTargetException ite) {");
      file.println(tab5 + "//$JL-EXC$ exclude for JLin");
      file.println(tab5 + "Throwable target = (Throwable) p4_replicate(ite.getTargetException());");
      file.print(tab5);
      for (int ii = 0; ii < exceptions.length; ii++) {
        file.println("if (target instanceof " + nameToType(exceptions[ii]) + ") {");
        file.println(tab5 + " throw (" + (nameToType(exceptions[ii])).replace('$', '.') + ")target;");
        file.print(tab5 + "} else ");
      }
      file.println("{ ");
      file.println(tab5 + " throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);");
      file.println(tab5 + "}");
      file.println(tab4 + "} ");
      file.println(tab3 + "} catch (java.lang.RuntimeException rex) {");
      file.println(tab5 + "//$JL-EXC$ exclude for JLin");
      file.println(tab4 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getCategory().logT(com.sap.engine.services.rmi_p4.exception.P4ExceptionConstants.SEVERITY_DEBUG, com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation(), \"P4 Call exception: Exception in execute <" + methods[p].getName() + ">\");");
      file.println(tab4 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this.getClass() + \" method <" + methods[p].getName() + ">\" + rex.getMessage());");
      file.println(tab4 + "throw rex;");

      for (int k = 0; k < exceptions.length; k++) {
        file.println(tab3 + "} catch (" + exceptions[k].getName().replace('$', '.') + " ex) {");
        file.println(tab5 + "//$JL-EXC$ exclude for JLin");
        file.println(tab4 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getCategory().logT(com.sap.engine.services.rmi_p4.exception.P4ExceptionConstants.SEVERITY_DEBUG, com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation(), \"P4 Call exception: Exception in execute <" + methods[p].getName() + ">\");");
        file.println(tab4 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this.getClass() + \" method<" + methods[p].getName() + ">\" +  ex.getMessage());");
        file.println(tab4 + "throw (" + exceptions[k].getName().replace('$', '.') + ") p4_replicate(ex);");

      }

      file.println(tab3 + "} ");

      file.println(tab2 + "}"); // if isLocal
      file.println(tab3 + "com.sap.engine.services.rmi_p4.Call call = null;");
      file.println(tab3 + "try {");
      file.println(tab4 + "call = p4_newCall(" + p + ");");
      file.println(tab4 + "P4ObjectOutput out = call.getOutputStream();");

      for (int i = 0; i < parameters.length; i++) {
        file.print(tab4 + "out.write" + ioType(parameters[i]));
        file.println("( _param" + i + ");");
      }

      file.println(tab4 + "p4_invoke(call);");
      file.println(tab4 + "P4ObjectInput in = call.getResultStream();");

      if (!returnType.getName().equals("void")) {
        String type = nameToType(returnType);

        if (returnType.isPrimitive()) {
          file.print(tab4 + type + " _result = ");
          file.println("in.read" + ioType(returnType) + "();");
        } else {
          file.println(tab5 + "Object obj;");
          if (!(returnType.getName().equals("java.lang.Class"))) {
            file.println(tab5 + type + " _result;");
          }
          if (returnType.getName().equals("java.lang.Class")) {
            file.println(tab5 + "obj = in.readObject" + "();");
          } else {
            file.println(tab5 + "obj = in.read" + ioType(returnType) + "();");
          }
          if (!(returnType.getName().equals("java.lang.Class"))) {
            file.println(tab5 + "try {");
            file.print(tab6 + " _result = ");
            file.println("(" + type + ")obj;");
            file.println(tab5 + "} catch (java.lang.ClassCastException ex) {");
            file.println(tab5 + "//$JL-EXC$ exclude for JLin");
            file.print(tab6 + " _result = ");
            file.println("(" + type + ") P4ObjectBroker.init().narrow(obj," + type + ".class);");
            file.println(tab5 + "}");
          }
        }

      }

      if (!returnType.getName().equals("void")) {
        if (returnType.getName().equals("java.lang.Class")) {
          file.println(tab4 + "if (obj == null) {");
          file.println(tab5 + "return null;");
          file.println(tab4 + "}");
          file.println(tab4 + "((com.sap.engine.services.rmi_p4.P4ClassWrapper) obj).setStub(this);");
          file.println(tab4 + "return  (((com.sap.engine.services.rmi_p4.P4ClassWrapper)obj).getCarriedClass());");
        } else {
          file.println(tab4 + "return _result;");
        }
      }

      exceptions = (new com.sap.engine.services.rmi_p4.exception.ExceptionHandler()).handler(exceptions);

      for (int k = 0; k < exceptions.length; k++) {
        if (exceptions[k].getName().equals("java.lang.Exception")) {
          continue;
        }

        file.println(tab3 + "} catch (" + exceptions[k].getName().replace('$', '.') + " ex) {");
        file.println(tab5 + "//$JL-EXC$ exclude for JLin");
        file.println(tab4 + "throw ex;");
      }

      file.println(tab3 + "} catch (java.lang.Exception tr) {");
      file.println(tab5 + "//$JL-EXC$ exclude for JLin");
      file.println(tab4 + "if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {");
      file.println(tab5 + "throw (RuntimeException)tr;");
      file.println(tab4 + "} else {");
      file.println(tab5 + "throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);");
      file.println(tab4 + "}"); // else isAssignable
      file.println(tab3 + "} finally {"); // catch
      file.println(tab4 + "p4_done(call);");
      //file.println(tab4 + "call.releaseInputStream();");
      file.println(tab3 + "}");  // finally
      file.println(tab1 + "}"); // method end
      file.println("\n");
    } //for write methods

    //
    file.println("}");
    file.close();
  }

  /**
   * Generate Skeleton
   *
   * @throws java.io.IOException
   */
  public void generateSkeleton() throws java.io.IOException {


    String skelName = makeSkeletonName(remote_class.getName());
    //make skel file and write header
    PrintWriter file = makeFile(skelName, remote_class);
    //write import class
    file.println("import java.rmi.server.Operation;");
    file.println();
    file.println("import com.sap.engine.services.rmi_p4.*;");
    //useSecurity = false;

    file.println();
    file.println("/**");
    file.println(" *");
    file.println("* @author  SAP's RMIC Generator");
    file.println(" * @version SAP Java EE Application Server");
    file.println(" */");
    //write skel class name
    file.print("public class " + skelName);

    if (!p4_remote_object.isAssignableFrom(remote_class)) {
      file.print(" extends P4RemoteObject ");
      file.println(" implements com.sap.engine.services.rmi_p4.Skeleton, java.rmi.Remote {");
    } else {
      file.println(" implements com.sap.engine.services.rmi_p4.Skeleton {");
    }

    file.println();
    file.print(tab1 + "public P4ObjectBroker broker = P4ObjectBroker.init();\r\n");


    //write field Operations
    file.print(tab1 + "private static final Operation[] operations = {");
    Enumeration enumeration = remoteMethods.keys();

    while (enumeration.hasMoreElements()) {
      String key = (String) enumeration.nextElement();
      file.print("\r\n" + tab3 + "new Operation(\"" + key + "\")" + (enumeration.hasMoreElements() ? "," : ""));
    }

    file.println("};\r\n");
    // write Skeleton's constructor
    file.println(tab1 + "public " + skelName + " () {");

    file.println(tab1 + "}\r\n");
    //write getOperations() method
    file.println(tab1 + "public Operation[] getOperations() {");
    file.println(tab2 + "return operations;");
    file.println(tab1 + "}\r\n");
    file.print(tab1 + "private static final String[] _implements = {");

    for (int i = 0; i < remoteInterfaces.length; i++) {
      file.print("\r\n" + tab3 + "\"" + remoteInterfaces[i].getName() + "\"" + (i != (remoteInterfaces.length - 1) ? "," : ""));
    }

    file.println("};\r\n");
    file.println(tab1 + "public String[] getImplemntsObjects() {");
    file.println(tab2 + "return _implements;");
    file.println(tab1 + "}\r\n");


    //write dispatch() method
    file.println(tab1 + "public void dispatch(java.rmi.Remote remote, Dispatch call, int opnum) throws Exception {");
    file.println();

    if (!p4_remote_object.isAssignableFrom(remote_class)) {
      file.println(tab2 + remote_class.getName() + " impl = (" + remote_class.getName() + ") delegate();");
    } else {
      file.println(tab2 + remote_class.getName() + " impl = (" + remote_class.getName() + ") remote;");
    }

    file.println(tab2 + "P4ObjectInput in = call.getInputStream();");

    file.println();
    file.println(tab2 + "switch (opnum) {\r\n");
    enumeration = remoteMethods.keys();
    int opnum = 0;

    while (enumeration.hasMoreElements()) {
      String key = (String) enumeration.nextElement();
      Method mm = (Method) remoteMethods.get(key);
      Class returnType = mm.getReturnType();
      Class[] parameters = mm.getParameterTypes();
      file.println(tab3 + "case " + opnum + " : {" + "  //method " + mm);
      file.println(tab4 + "try {");

      //write reading parameters from in stream
      for (int i = 0; i < parameters.length; i++) {
        String type = nameToType(parameters[i]);

        if (parameters[i].isPrimitive()) {
          file.print(tab5 + type + " param" + i + " = ");
          file.println("in.read" + ioType(parameters[i]) + "();");
        } else {
          file.println(tab5 + "Object obj" + i + ";");
          file.println(tab5 + type + " param" + i + ";");
          file.println(tab5 + "obj" + i + " = in.read" + ioType(parameters[i]) + "();");
          file.println(tab5 + "try {");
          file.print(tab6 + " param" + i + " = ");
          file.println("(" + type + ")obj" + i + ";");
          file.println(tab5 + "} catch (ClassCastException ex) {");
          file.println(tab5 + "//$JL-EXC$ exclude for JLin");
          file.print(tab6 + " param" + i + " = ");
          file.println("(" + type + ") P4ObjectBroker.init().narrow(obj" + i + "," + type + ".class);");
          file.println(tab5 + "}");
        }
      }

      if (!returnType.getName().equals("void")) {
        file.print(tab5 + nameToType(returnType) + " _result = impl." + mm.getName() + "(");

        for (int i = 0; i < parameters.length; i++) {
          file.print("param" + i + ((i == parameters.length - 1) ? "" : ", "));
        }

        file.println(");");
        file.println(tab5 + "P4ObjectOutput out = call.getOutputStream();");
        if (returnType.getName().equals("java.lang.Class")) {
          file.println(tab5 + "if(_result != null){");
          file.print(tab6 + "out.writeObject");
          file.println("(new com.sap.engine.services.rmi_p4.P4ClassWrapper((java.lang.Class)_result));"); // : ("(_result);"));
          file.println(tab5 + "} else {");
          file.println(tab6 + "out.writeObject(null);");
          file.println(tab5 + "}");
        } else {
          file.print(tab5 + "out.write" + ioType(returnType));
          file.println("( _result);"); // : ("(_result);"));
        }
        file.println(tab5 + "out.flush();");
      } else {
        file.print(tab5 + "impl." + mm.getName() + "(");
        for (int i = 0; i < parameters.length; i++) {
          file.print("param" + i + ((i == parameters.length - 1) ? "" : ", "));
        }

        file.println(");");
        file.println(tab5 + "P4ObjectOutput out = call.getOutputStream();");
      }

      file.println(tab4 + "} catch (java.lang.Exception ex) {");
      file.println(tab5 + "//$JL-EXC$ exclude for JLin");
      file.println(tab5 + "throw ex;");
      file.println(tab4 + "}");
      file.println(tab4 + "break;");
      file.println(tab3 + "}");
      opnum++;
    } //while

    file.println(tab2 + "}");
    file.println(tab1 + "}\r\n\r\n");

    file.println("}");
    file.close();
  }

  /**
   * Make Stub class name from remote interface class name
   *
   * @param className
   * @return Stub name
   */
  private String makeStubName(String className) {
    String stubName = className.substring(className.lastIndexOf('.') + 1);
    return stubName += "_" + "Stub";
  }

  private String makeSkeletonName(String className) {
    String skeletonName = className.substring(className.lastIndexOf('.') + 1);
    return skeletonName += p4 + "_" + "Skel";
  }

  private String ioType(Class _toRW) {
    if (_toRW.getName().equals("int")) {
      return "Int";
    } else if (_toRW.getName().equals("long")) {
      return "Long";
    } else if (_toRW.getName().equals("short")) {
      return "Short";
    } else if (_toRW.getName().equals("boolean")) {
      return "Boolean";
    } else if (_toRW.getName().equals("byte")) {
      return "Byte";
    } else if (_toRW.getName().equals("char")) {
      return "Char";
    } else if (_toRW.getName().equals("double")) {
      return "Double";
    } else if (_toRW.getName().equals("float")) {
      return "Float";
    }

    return "Object";
  }

  private String nameToType(Class _class) {
    String type = _class.getName().replace('$', '.');
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
      case 'L':
        {
          type = type.substring(1, type.length() - 1) + arrDimension;
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
    }

    return type;
  }

  private PrintWriter makeFile(String _fileName, Class _forClass) throws java.io.IOException {
    String fileName = "";
    String package_ = "";
    package_ = getPackageName(_forClass.getName());
    fileName = package_.replace('.', File.separatorChar); // +File.separatorChar + _fileName;
    File __file = new File(destDir + File.separatorChar + fileName);
    __file.mkdirs();
    File _file = new File(__file, _fileName + ".java");

    if (fileName.length() > 0) {
      fileName += File.separatorChar + _fileName;
    } else {
      fileName = _fileName;
    }

    files.addElement(fileName);
    fileName += ".java";

    FileOutputStream fos = new FileOutputStream(_file.getCanonicalPath(),false);
    OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
    PrintWriter file = new PrintWriter(osw,true);


    file.println("// Class generated by SAP Labs Bulgaria Generator");
    file.println("// Don't change it !!");
    file.println();
    file.println();

    //write package
    if (package_ != null) {
      if (!package_.equals("")) {
        file.println("/**");
        file.println(" * Copyright (c) 2000 by SAP AG, Walldorf.,");
        file.println(" * url: http:////www.sap.com");
        file.println(" * All rights reserved.");
        file.println(" *");
        file.println(" * This software is the confidential and proprietary information");
        file.println(" * of SAP AG, Walldorf.. You shall not disclose such Confidential");
        file.println(" * Information and shall use it only in accordance with the terms");
        file.println(" * of the license agreement you entered into with SAP.");
        file.println(" */");
        file.println("package " + package_ + ";");
        file.println();
      }
    }

    return file;
  }

  private static String getPackageName(String fullClassName) {
    if (fullClassName.indexOf(".") == -1) {
      return "";
    } else {
      return fullClassName.substring(0, fullClassName.lastIndexOf("."));
    }
  }

  private String replicateMethodParameters(Class[] parameters) {
    String result = "";

    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].isPrimitive() || parameters[i].getName().startsWith("java.lang.String") || (parameters[i].isArray() && checkElementType(parameters[i]))) {
        result += " _param" + new Integer(i).toString();
      } else {
        result += "(" + nameToType(parameters[i]) + ") P4ObjectBroker.init().narrow(p4_replicateWithStreams(inn1,out1,_param" + new Integer(i).toString() + ")," + nameToType(parameters[i]) + ".class)";
      }

      result += (i == parameters.length - 1) ? "" : ", ";
    }

    return result;
  }

  private boolean checkElementType(Class _class) {
    if (_class.isArray()) {
      return checkElementType(_class.getComponentType());
    } else {
      if (_class.isPrimitive() || _class.getName().startsWith("java.lang.String")) {
        return true;
      } else {
        return false;
      }
    }
  }

}