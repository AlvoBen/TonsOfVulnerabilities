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
package com.sap.engine.rmic.p4;

import com.sap.engine.rmic.log.RMICLogger;

import java.io.File;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.rmi.RemoteException;

/**
 * This class represents java code generator for p4 stubs and skeletons.
 *
 * @author Georgi Stanev, Mladen Droshev
 * @version 7.10/7.20
 */
public class P4StubSkeletonGenerator {

  public static final String P4_REMOTE_OBJECT_NAME = "com.sap.engine.services.rmi_p4.P4RemoteObject";

  public static final String METHOD_PREFIX = "_p4_";
  public static final String PARAM_OPTIMIZED = "1" + METHOD_PREFIX;
  public static final String RESULT_OPTIMIZED = "2" + METHOD_PREFIX;
  public static final String BOTH_OPTIMIZED = "4" + METHOD_PREFIX;

  private static String tab1 = "  ";//\t";
  private static String tab2 = tab1 + tab1;//"\t\t";
  private static String tab3 = tab1 + tab2;//"\t\t\t";
  private static String tab4 = tab2 + tab2;//"\t\t\t\t";
  private static String tab5 = tab3 + tab2;//"\t\t\t\t\t";
  private static String tab6 = tab3 + tab3;//"\t\t\t\t\t\t";
  private Class remote_class = null;
  private String destDir = null;
  private String p4 = "";
  private Class[] remoteInterfaces = {};
  private Hashtable remoteMethods = new Hashtable();
  private boolean onlyStub = false;
  private Vector<String> files = new Vector<String>();
  private Class p4_remote_object = null;

  private String NEW_LINE = "\r\n";

  /**
   * Initialize generator
   *
   * @param _class   represent class or interface which needs need p4 stub and skel
   * @param _destDir directory name where java files will be generated
   */
  public P4StubSkeletonGenerator(Class _class, String _destDir, Hashtable access) throws java.io.IOException, java.lang.ClassNotFoundException {
    this(_class, _destDir, P4StubSkeletonGenerator.class.getClassLoader());
  }

  /**
   * Initialize generator
   *
   * @param _class   represent class or interface which needs need p4 stub and skel
   * @param _destDir directory name where java files will be generated
   */
  public P4StubSkeletonGenerator(Class _class, String _destDir, ClassLoader loader) throws java.io.IOException, java.lang.ClassNotFoundException {

    try {
      this.p4_remote_object = loader.loadClass(P4_REMOTE_OBJECT_NAME);
    } catch (ClassNotFoundException e) {
      RMICLogger.throwing(e);
      try {
        this.p4_remote_object = Class.forName(P4_REMOTE_OBJECT_NAME);
      } catch (ClassNotFoundException e1) {
        RMICLogger.throwing(e1);
        throw e1;
      }
    }
    remote_class = _class;

    if (!p4_remote_object.isAssignableFrom(remote_class)) {
      p4 = "p4";
    }

    destDir = _destDir;

    if (_class.isInterface() || java.lang.reflect.Proxy.isProxyClass(_class)) {
      onlyStub = true;
    }

    Vector temp_interfaces = new Vector();

    /* get remote Interfaces and methods */
    getRemoteInterfaces(remote_class, temp_interfaces);
    getRemoteMethods(temp_interfaces, remoteMethods);

    /* copy buffer into corresponding array */
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
  }

  /**
   * Check class _target for all remote interfaces
   *
   * @param _target         class for checking
   * @param interfaceBuffer
   */

  private void getRemoteInterfaces(Class _target, Vector interfaceBuffer) {
    /*_target is null when previous call _target is java.lang.Object or interface */
    if (_target != null) {
      /* get remote interfaces from supperclass */
      getRemoteInterfaces(_target.getSuperclass(), interfaceBuffer);
      Class[] temp_interfaces = _target.getInterfaces();

      for (Class temp_interface : temp_interfaces) {
        /* get remote inheriting interfaces from consecutive interface */
        update(temp_interface, interfaceBuffer);
      }
    }
  }

  public void getRemoteMethods(Vector _target, Hashtable methodBuffer) {
    for (int k = 0; k < _target.size(); k++) {
      Method[] methods = ((Class) _target.elementAt(k)).getMethods();

      /* if methods is not in methodsBuffer - add */
      for (Method method : methods) {
        if ((method.getName()).startsWith("<clinit>")) {
          continue;
        }

        String methodName = method.toString();
        methodName = methodName.substring(methodName.indexOf('('), methodName.lastIndexOf(')') + 1);
        methodName = method.getName() + methodName;

        if (!methodBuffer.contains(methodName)) {
          methodBuffer.put(methodName, method);
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

      for (Class remoteInterface : remoteInterfaces) {
        generateStub(remoteInterface);
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
    Method[] rem_methods = remote_interface.getMethods();
    String stubName = makeStubName(remote_interface.getName());

    /* make stub file and write header */
    PrintWriter file = makeFile(stubName, remote_interface);

    generateStubSignature(stubName, remote_interface, file);

    generateStubOperation(rem_methods, file);

    /* write methods */
    for (Method rem_method : rem_methods) {
      //generateStubMethod(remote_interface, rem_methods[p], false, file);

      /* if the method can be optimized */
      if (isOptimizableParams(rem_method) || isOptimizableReturnType(rem_method)) {
        generateStubMethod(rem_method, true, file, remote_interface);
      } else {
        generateStubMethod(rem_method, false, file, remote_interface);
      }

    } //for - write methods

    /* write read byte array methods */
    generateSerializeMethods(file);

    //
    file.println("}");
    file.close();
  }

  private void generateStubSignature(String stubName, Class remote_interface, PrintWriter file) {
    /* write import classes */
    file.println(NEW_LINE);
    file.println("import com.sap.engine.services.rmi_p4.*;");
    file.println(NEW_LINE);
    file.println("/**");
    file.println(" *");
    file.println(" * @author  SAP's RMIC Generator");
    file.println(" * @version SAP Java EE Application Server");
    file.println(" */");

    /* write stub class name */
    file.print("public class " + stubName);
    file.println(" extends com.sap.engine.services.rmi_p4.StubImpl ");
    file.print(tab2 + "implements " + remote_interface.getName() + " {");
    file.println(NEW_LINE);
  }

  private void generateStubOperation(Method[] rem_methods, PrintWriter file) {
    /* write field Operations */
    file.print(tab1 + "private static final String[] operations = {");

    for (int i = 0; i < rem_methods.length; i++) {
      if ((rem_methods[i].getName()).startsWith("<clinit>")) {
        continue;
      }

      /* write standart field Operations */
      String methodName = getOperationName(rem_methods[i], false);

      if (i > 0 && i < rem_methods.length) {
        file.print(",");
      }
      file.print(NEW_LINE + tab3 + "\"" + methodName + "\"");

      /* write optimized field Operations */
      if (isOptimizableParams(rem_methods[i]) || isOptimizableReturnType(rem_methods[i])) {
        methodName = getOperationName(rem_methods[i], true);


        if (i < rem_methods.length) {
          file.print(",");
        }
        file.print(NEW_LINE + tab3 + "\"" + methodName + "\"");
      }
    }


    file.println("};" + NEW_LINE);

    /* write getOperations() method */
    file.println(tab1 + "public String[] p4_getOperations() {");
    file.println(tab2 + "return operations;");
    file.println(tab1 + "}" + NEW_LINE);
  }

  private String getOperationName(Method rem_method, boolean isOpt){
    String methodName = rem_method.toString();
    methodName = methodName.substring(methodName.indexOf('('), methodName.lastIndexOf(')') + 1);
    if (isOpt) {
      if(isOptimizableParams(rem_method) && isOptimizableReturnType(rem_method)){
        methodName = new StringBuilder().append(BOTH_OPTIMIZED).append(rem_method.getName()).append(methodName).toString();
      } else if(isOptimizableParams(rem_method)){
        methodName = new StringBuilder().append(PARAM_OPTIMIZED).append(rem_method.getName()).append(methodName).toString();
      } else {
        methodName = new StringBuilder().append(RESULT_OPTIMIZED).append(rem_method.getName()).append(methodName).toString();
      }
    } else {
      methodName = new StringBuilder().append(rem_method.getName()).append(methodName).toString();
    }
    return methodName;
  }

  private void generateStubMethod(Method remoteMethod, boolean optimized, PrintWriter toWrite, Class remote_interface) {
    /* generate Method's signature */
    generateMethodSignature(remoteMethod, toWrite);

    /* write method body */
    toWrite.print(NEW_LINE);

    /* generate Local Block */
    generateLocalBlock(remoteMethod, toWrite, remote_interface);
    toWrite.print(NEW_LINE);

    /* generate Remote Block */
    generateRemoteBlock(remoteMethod, toWrite, optimized);
    toWrite.print(NEW_LINE);

    toWrite.println(tab1 + "}"); // method end
    toWrite.print(NEW_LINE);
    toWrite.print(NEW_LINE);

  }

  private void generateMethodSignature(Method remoteMethod, PrintWriter toWrite) {

    /* get consecutive method */
    Class returnType = remoteMethod.getReturnType();
    Class[] parameters = remoteMethod.getParameterTypes();

    Class[] exceptions = exceptionTypes(remoteMethod);

    /* write modifier */
    int modifier = remoteMethod.getModifiers();
    if (Modifier.isPublic(modifier)) {
      toWrite.print(tab1 + "public ");
    } else if (Modifier.isPrivate(modifier)) {
      toWrite.print(tab1 + "private ");
    } else if (Modifier.isProtected(modifier)) {
      toWrite.print(tab1 + "protected ");
    }

    /* write return type */
    toWrite.print(nameToType(returnType) + " ");

    /* write method name */
    //if (optimized) {
    //  toWrite.print(METHOD_PREFIX + remoteMethod.getName() + "(");
    //} else {
      toWrite.print(remoteMethod.getName() + "(");
    //}

    /* write parameters */
    for (int i = 0; i < parameters.length; i++) {
      RMICLogger.logMSG("P4Generator:Stub:method[" + remoteMethod + "] parameter[" + i + "]" + parameters[i]);
      toWrite.print(nameToType(parameters[i]) + " _param" + i);
      toWrite.print((i == parameters.length - 1) ? ")" : ", ");
    }

    toWrite.print((parameters.length == 0) ? ") " : " ");

    /* write throws exceptions */
    toWrite.print((exceptions.length == 0) ? " {" + NEW_LINE : "throws ");
    for (int i = 0; i < exceptions.length; i++) {
      toWrite.print(exceptions[i].getName().replace('$', '.'));
      toWrite.print((i == exceptions.length - 1) ? " {" + NEW_LINE : ", ");
    }
  }

  private void generateLocalBlock(Method remoteMethod, PrintWriter file, Class remote_interface) {

    boolean declaredRemoteException = checkForDeclRemoteExcp(remoteMethod);
    Class[] exceptions = exceptionTypes(remoteMethod);
    Class[] parameters = remoteMethod.getParameterTypes();
    Class returnType = remoteMethod.getReturnType();

    /* Local block */
    file.println(tab2 + "/* Local Stub Block */ ");
    file.println(tab2 + "if (isLocal) {");
    file.println(tab3 + "java.rmi.Remote remote;");
    file.println(tab3 + "if (p4remote == null) {");
    if (declaredRemoteException) {
      file.println(tab4 + "throw new java.rmi.NoSuchObjectException(\"\");");
    } else {
      file.println(tab4 + "throw (com.sap.engine.services.rmi_p4.P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException," + NEW_LINE +
              tab6 + " com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);");
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
      file.println(tab5 + "throw (com.sap.engine.services.rmi_p4.P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException," + NEW_LINE +
              tab6 + " com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);");
      file.println(tab4 + "}");
    }

    file.println(tab3 + "}");
    file.println(tab3 + "try {");

    file.println(tab4 + remote_interface.getName() + " remoteInterface = null;");
    boolean useStreamss = false;
    for (Class parameter1 : parameters) {
      if (!checkElementType(parameter1)) {
        useStreamss = true;
      }
    }
    if (useStreamss) {
      file.println(tab4 + "ReplicateOutputStream out1 = p4_getReplicateOutput();");
      file.println(tab4 + "ReplicateInputStream inn1 = p4_getReplicateInput(out1);");
    }
    file.println(tab4 + "try{");
    file.print(tab5 + "remoteInterface = ");
    file.println("(" + remote_interface.getName() + ") remote;");


    file.println(tab4 + "} catch (java.lang.ClassCastException rex) {");
    boolean useStreamsss = false;
    for (Class parameter : parameters) {
      if (!checkElementType(parameter)) {
        useStreamsss = true;
      }
    }
    if (useStreamsss) {
      file.println(tab5 + "ReplicateOutputStream outt = p4_getReplicateOutput();");
      file.println(tab5 + "ReplicateInputStream inn = p4_getReplicateInput(outt);");
    }
    file.print(tab5 + "Object[] params = new Object[]{");
    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].isPrimitive() || parameters[i].isArray()) {
        if (ioType(parameters[i]).equals("Int")) {
          file.print("new Integer(_param" + Integer.toString(i) + ")");
        } else if (ioType(parameters[i]).equals("Char")) {
          file.print("new Character(_param" + Integer.toString(i) + ")");
        } else if (parameters[i].isArray() && checkElementType(parameters[i])) {
          file.print("_param" + Integer.toString(i));
        } else if (parameters[i].isArray() && !checkElementType(parameters[i])) {
          file.print("p4_replicateWithStreams(inn,outt,_param" + Integer.toString(i) + ")");
        } else {
          file.print("new " + ioType(parameters[i]) + "(" + " _param" + Integer.toString(i) + ")");
        }
      } else if (!nameToType(parameters[i]).startsWith("java.lang.String")) {
        file.print("p4_replicateWithStreams(inn,outt,");
        file.print("_param" + Integer.toString(i));
        file.print(")");
      } else {
        file.print("_param" + Integer.toString(i));
      }

      if (i < (parameters.length - 1)) {
        file.print(",");
      }
    }
    file.println("};");
    file.print(tab5 + "Class[] p = new Class[]{");
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
    file.println(tab5 + "try { ");
    if (!returnType.getName().equalsIgnoreCase("void")) {
      file.print(tab6 + "return ");
      if (!returnType.isPrimitive() && !returnType.equals(java.lang.String.class)) {
        file.println("(" + nameToType(returnType) + ")P4ObjectBroker.init().narrow(p4_replicate(p4_invokeReflect(remote, \"" +
                remoteMethod.getName() + "\", params, p)), " + nameToType(returnType) + ".class);");
      } else {
        if (ioType(returnType).equals("Int")) {
          file.println("((Integer) p4_invokeReflect(remote, \"" + remoteMethod.getName() + "\", params, p)).intValue();");
        } else if (ioType(returnType).equals("Char")) {
          file.println("((Character) p4_invokeReflect(remote, \"" + remoteMethod.getName() + "\", params, p)).charValue();");
        } else if (returnType.equals(java.lang.String.class)) {
          file.println("((String) p4_invokeReflect(remote, \"" + remoteMethod.getName() + "\", params, p));");
        } else {
          file.println("((" + ioType(returnType) + ") p4_invokeReflect(remote,\"" + remoteMethod.getName() + "\",params,p))." + returnType + "Value();");
        }
      }
    } else {
      file.println(tab6 + "p4_invokeReflect(remote,\"" + remoteMethod.getName() + "\",params,p);");
      file.println(tab6 + "return ;");
    }

    file.println(tab5 + "} catch (java.lang.NoSuchMethodException nsme) {");
    file.println(tab6 + "throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException," +
            NEW_LINE + tab6 + " com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method, nsme);");
    file.println(tab5 + "} catch (java.lang.IllegalAccessException iae) {");
    file.println(tab6 + "throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException," +
            NEW_LINE + tab6 + " com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access, iae);");
    file.println(tab5 + "} catch (java.lang.reflect.InvocationTargetException ite) {");
    file.println(tab6 + "Throwable target = (Throwable) p4_replicate(ite.getTargetException());");
    file.print(tab6);
    for (Class exception1 : exceptions) {
      file.println("if (target instanceof " + nameToType(exception1) + ") {");
      file.println(tab6 + " throw (" + (nameToType(exception1)).replace('$', '.') + ")target;");
      file.print(tab6 + "} else ");
    }
    file.println("{ ");
    file.println(tab6 + " throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException," +
            NEW_LINE + tab6 + " com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);");
    file.println(tab6 + "}");
    file.println(tab5 + "}");
    file.println(tab3 + "} ");

    if (returnType.getName().equals("void")) {
      file.print(tab4 + "remoteInterface." + remoteMethod.getName() + "(");
      /* replicating method parameters !!! */
      file.print(replicateMethodParameters(parameters));
      file.println(");");
      file.println(tab4 + "return;");
    } else {

      if (!checkElementType(returnType)) {
        String cast = "(" + nameToType(returnType) + ") P4ObjectBroker.init().narrow(p4_replicate(";
        file.print(tab4 + "return " + cast + "remoteInterface." + remoteMethod.getName() + "(");
        /* replicating method parameters !!! */
        file.print(replicateMethodParameters(parameters));
        file.println(")), " + nameToType(returnType) + ".class);");

      } else {
        file.print(tab4 + "return remoteInterface." + remoteMethod.getName() + "(");
        /* replicating method parameters !!! */
        file.print(replicateMethodParameters(parameters));
        file.println(");");
      }
    }

    file.println(tab4 + "} catch (java.lang.RuntimeException rex) {");
    file.println(tab5 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getCategory().logT(com.sap.engine.services.rmi_p4.exception.P4ExceptionConstants.SEVERITY_DEBUG," +
            NEW_LINE + tab6 + " com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation(), \"P4 Call exception: Exception in execute <" + remoteMethod.getName() + ">\");");
    file.println(tab5 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this.getClass() + \" method <" + remoteMethod.getName() + ">\" + rex.getMessage());");
    file.println(tab5 + "throw rex;");

    for (Class exception : exceptions) {
      file.println(tab4 + "} catch (" + exception.getName().replace('$', '.') + " ex) {");
      file.println(tab5 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getCategory().logT(com.sap.engine.services.rmi_p4.exception.P4ExceptionConstants.SEVERITY_DEBUG," +
              NEW_LINE + tab6 + " com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation(), \"P4 Call exception: Exception in execute <" + remoteMethod.getName() + ">\");");
      file.println(tab5 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this.getClass() + \" method<" + remoteMethod.getName() + ">\" +  ex.getMessage());");
      file.println(tab5 + "throw (" + exception.getName().replace('$', '.') + ") p4_replicate(ex);");

    }

    file.println(tab4 + "} ");

    file.println(tab2 + "}");

    file.println(tab2 + "/* End Of Local Stub Block */ " + NEW_LINE);
    /* if isLocal  - end local block */
  }

  private void generateRemoteBlock(Method remoteMethod, PrintWriter file, boolean optimized) {

    Class[] parameters = remoteMethod.getParameterTypes();
    Class returnType = remoteMethod.getReturnType();

    Class[] exceptions = exceptionTypes(remoteMethod);

    boolean isOptimizableReturn = isOptimizableReturnType(remoteMethod) && optimized;
    boolean isOptimizableParams = isOptimizableParams(remoteMethod) && optimized;

    /* remote Block */
    file.println(tab3 + "/* Remote Stub Block */ ");
    file.println(tab3 + "com.sap.engine.services.rmi_p4.Call call = null;");
    file.println(tab3 + "try {");
    if (isOptimizableParams || isOptimizableReturn) {
      file.println(tab4 + "if(p4_getInfo() != null && p4_getInfo().supportOptimization()){");
      if(isOptimizableParams){
        file.println(tab5 + "call = p4_newCall(\"" + getOperationName(remoteMethod, true) + "\", true);");
      } else {
        file.println(tab5 + "call = p4_newCall(\"" + getOperationName(remoteMethod, true) + "\", false);");
      }
//      if(isOptimizableReturn && isOptimizableParams){
//        file.println(tab5 + "call = p4_newCall(\"" + BOTH_OPTIMIZED + remoteMethod.getName() + "\", true);");
//      } else if(isOptimizableParams){
//        file.println(tab5 + "call = p4_newCall(\"" + PARAM_OPTIMIZED + remoteMethod.getName() + "\", true);");
//      } else {
//        file.println(tab5 + "call = p4_newCall(\"" + RESULT_OPTIMIZED + remoteMethod.getName() + "\", true);");
//      }

      file.println(tab4 + "} else {");
      file.println(tab5 + "call = p4_newCall(\"" + getOperationName(remoteMethod, false)+ "\");");
      file.println(tab4 + "}");
    } else {
      file.println(tab4 + "call = p4_newCall(\"" + getOperationName(remoteMethod, false) + "\");");
    }

    if (isOptimizableParams) { // if params allow sending via data stream
      file.println(tab4 + "if(p4_getInfo() != null && p4_getInfo().supportOptimization()){");

      file.println(tab5 + "DataOptOutputStream dout = call.getDataOutputStream();");
      for (int i = 0; i < parameters.length; i++) {
        if (parameters[i].isArray() && parameters[i].getName().equals("[B")) {
          file.println(tab5 + "writeByteArray(dout,  _param" + i + ");");
        } else if (parameters[i].getName().equals("java.lang.String")) {
          file.println(tab5 + "if(_param" + i + " == null){");
          file.println(tab6 + "writeByteArray(dout, null);");
          file.println(tab5 + "} else {");
          file.println(tab6 + "writeByteArray(dout, _param" + i + ".getBytes());");
          file.println(tab5 + "}");
        } else {
          file.print(tab5 + "dout.write" + ioOptType(parameters[i]));
          file.println("( _param" + i + ");");
        }

      }
      file.println(tab5);
      file.println(tab4 + "} else {");
    }

    file.println(tab5 + "P4ObjectOutput out = call.getOutputStream();");

    for (int i = 0; i < parameters.length; i++) {
      file.print(tab5 + "out.write" + ioType(parameters[i]));
      file.println("( _param" + i + ");");
    }

    if (isOptimizableParams) {
      file.println(tab4 + "}" + NEW_LINE); //close
    }
    file.println(tab4 + "p4_invoke(call);" + NEW_LINE);


    if (optimized && isOptimizableReturn) {
      file.println(tab4 + "DataOptInputStream din = null;");
    }
    file.println(tab4 + "P4ObjectInput in = null;");

    if (isOptimizableReturn) {
      file.println(tab4 + "if(p4_getInfo() != null && p4_getInfo().supportOptimization()){");
      file.println(tab5 + "din = call.getDataResultStream();");
      file.println(tab4 + "} else {");
      file.println(tab5 + "in = call.getResultStream();");
      file.println(tab4 + "}" + NEW_LINE);
    } else {
      file.println(tab4 + "in = call.getResultStream();");

    }

    if (!returnType.getName().equals("void")) {
      String type = nameToType(returnType);
      if (returnType.isPrimitive()) {
        if (boolean.class.isAssignableFrom(returnType)) {
          file.println(tab4 + type + " _result = false;");
        } else {
          file.println(tab4 + type + " _result = 0;");
        }
      } else {
        file.println(tab4 + type + " _result = null;");
      }

      if (isOptimizableReturn) {
        file.println(tab4 + "if(p4_getInfo() != null && p4_getInfo().supportOptimization()){");
        if (returnType.isArray() && returnType.getName().equals("[B")) {
          file.println(tab5 + "_result = readByteArray(din);");
          //generateOptReadByteArr("_result", "din", file, tab5);
        } else if (returnType.getName().equals("java.lang.String")) {
          generateOptReadString("_result", -1, "din", file, tab5);
        } else {
          file.print(tab5 + " _result = ");
          file.println("din.read" + ioOptType(returnType) + "();");
        }

        file.println(tab4 + "} else {");
      }

      if (returnType.isPrimitive()) {
        file.print(tab5 + " _result = ");
        file.println("in.read" + ioType(returnType) + "();");
      } else {
        file.println(tab5 + "Object obj;");
        if (!(returnType.getName().equals("java.lang.Class"))) {
          //file.println(tab5 + type + " _result;");
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
          file.print(tab6 + " _result = ");
          file.println("(" + type + ") P4ObjectBroker.init().narrow(obj," + type + ".class);");
          file.println(tab5 + "}");
        }
      }
      if (isOptimizableReturn) {
        file.println(tab4 + "}");
      }

    }

    if (!returnType.getName().equals("void")) {
      if (returnType.getName().equals("java.lang.Class")) {
        file.println(tab4 + "if(obj == null){");
        file.println(tab5 + "return null;");
        file.println(tab4 + "}");
        file.println(tab4 + "((com.sap.engine.services.rmi_p4.P4ClassWrapper)obj).setStub(this);");
        file.println(tab4 + "return  (((com.sap.engine.services.rmi_p4.P4ClassWrapper)obj).getCarriedClass());");
        //file.println(tab4 + "return (obj == null? null: ((com.sap.engine.services.rmi_p4.P4ClassWrapper)obj).getCarriedClass());");
      } else {
        file.println(tab4 + "return _result;");
      }
    }

    exceptions = (new ExceptionHandler()).handler(exceptions);

    for (Class exception : exceptions) {
      if (exception.getName().equals("java.lang.Exception")) {
        continue;
      }

      file.println(tab3 + "} catch (" + exception.getName().replace('$', '.') + " ex) {");
      file.println(tab4 + "throw ex;");
    }

    file.println(tab3 + "} catch (java.lang.Exception tr) {");
    file.println(tab4 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this.getClass() + " + "\"method <" + remoteMethod.getName() + "> \"" + " + tr.getMessage());");
    file.println(tab4 + " // ex.printStackTrace();");
    file.println(tab4 + "if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {");
    file.println(tab5 + "throw (RuntimeException)tr;");
    file.println(tab4 + "} else {");
    file.println(tab5 + "throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException," +
            " com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);");
    file.println(tab4 + "}"); // else isAssignable
    file.println(tab3 + "} finally {"); // catch
    file.println(tab4 + "p4_done(call);");
    //file.println(tab4 + "call.releaseInputStream();");
    file.println(tab3 + "}");  // finally

    file.println(tab2 + "/* End Of Remote Stub Block */ ");
  }

  private boolean checkForDeclRemoteExcp(Method remoteMethod) {
    boolean declaredRemoteException = false;
    Class[] exceptions = remoteMethod.getExceptionTypes();
    if (exceptions.length != 0) {
      exceptions = (new ExceptionHandler()).handler(exceptions);
    }
    for (Class exception : exceptions) {
      if (RemoteException.class.equals(exception)) {
        declaredRemoteException = true;
        break;
      }
    }
    return declaredRemoteException;
  }

  private Class[] exceptionTypes(Method remoteMethod) {
    Class[] exceptions = remoteMethod.getExceptionTypes();
    if (exceptions.length != 0) {
      exceptions = (new ExceptionHandler()).handler(exceptions);
    }
    for (Class exception : exceptions) {
      if (RemoteException.class.equals(exception)) {
        break;
      }
    }
    return exceptions;
  }

  private void generateSerializeMethods(PrintWriter file) {
    file.println(tab1 + "public byte[] readByteArray(com.sap.engine.services.rmi_p4.P4ObjectInput in, int length) throws java.io.IOException{");
    file.println(tab2 + "/*  read  param  as byte[] */");
    file.println(tab2 + "byte[] buffer = null;");
    file.println(tab2 + "try {");
    file.println(tab3 + "int byteArrayLength = in.readInt();");
    file.println(tab3 + "if(byteArrayLength == -1){");
    file.println(tab4 + "return null;");
    file.println(tab3 + "} else if(byteArrayLength == 0){");
    file.println(tab4 + "return new byte[0];");
    file.println(tab3 + "} if(byteArrayLength > 0){");
    file.println(tab4 + "buffer = new byte[byteArrayLength];");
    file.println(tab4 + "int readed = 0;");
    file.println(tab4 + "int offset = 0;");
    file.println(tab4 + "while (readed != -1 && offset < byteArrayLength) {");
    file.println(tab5 + "readed = in.read(buffer, offset, byteArrayLength - offset);");
    file.println(tab6 + "offset += readed;");
    file.println(tab4 + "}");
    file.println(tab3 + "}");
    file.println(tab2 + "} catch (java.io.IOException ioe) {");
    file.println(tab3 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this + \". Problem while read byte[] from the stream : \" + ioe.getMessage());");
    file.println(tab3 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().throwing(ioe);");
    file.println(tab3 + "throw ioe;");
    file.println(tab2 + "}");
    file.println(tab2 + "return buffer;");
    file.println(tab1 + "}" + NEW_LINE + NEW_LINE);

    file.println(tab1 + "public byte[] readByteArray(com.sap.engine.services.rmi_p4.DataOptInputStream in) throws java.io.IOException{");
    file.println(tab2 + "/*  read  param  as byte[] */");
    file.println(tab2 + "byte[] buffer = null;");
    file.println(tab2 + "try {");
    file.println(tab3 + "int byteArrayLength = in.readInt();");
    file.println(tab3 + "if(byteArrayLength == -1){");
    file.println(tab4 + "return null;");
    file.println(tab3 + "} else if(byteArrayLength == 0){");
    file.println(tab4 + "return new byte[0];");
    file.println(tab3 + "} if(byteArrayLength > 0){");
    file.println(tab4 + "buffer = new byte[byteArrayLength];");
    file.println(tab4 + "int readed = 0;");
    file.println(tab4 + "int offset = 0;");
    file.println(tab4 + "while (readed != -1 && offset < byteArrayLength) {");
    file.println(tab5 + "readed = in.read(buffer, offset, byteArrayLength - offset);");
    file.println(tab6 + "offset += readed;");
    file.println(tab4 + "}");
    file.println(tab3 + "}");
    file.println(tab2 + "} catch (java.io.IOException ioe) {");
    file.println(tab3 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this + \". Problem while read byte[] from the stream : \" + ioe.getMessage());");
    file.println(tab3 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().throwing(ioe);");
    file.println(tab3 + "throw ioe;");
    file.println(tab2 + "}");
    file.println(tab2 + "return buffer;");
    file.println(tab1 + "}" + NEW_LINE + NEW_LINE);

    file.println(tab1 + "public void writeByteArray(com.sap.engine.services.rmi_p4.DataOptOutputStream out, byte[] data) throws java.io.IOException{");
    file.println(tab2 + "try{");
    file.println(tab3 + "if(data == null){");
    file.println(tab4 + "out.writeInt(-1);");
    file.println(tab3 + "} else if(data != null && data.length == 0){");
    file.println(tab4 + "out.writeInt(0);");
    file.println(tab3 + "} else {");
    file.println(tab4 + "out.writeInt(data.length);");
    file.println(tab4 + "out.write(data, 0, data.length);");
    file.println(tab3 + "}");
    file.println(tab2 + "}catch(java.io.IOException ioe){");
    file.println(tab3 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this + \". Problem while write byte[] in the stream : \" + ioe.getMessage());");
    file.println(tab3 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().throwing(ioe);");
    file.println(tab3 + "throw ioe;");
    file.println(tab2 + "}");
    file.println(tab1 + "}" + NEW_LINE + NEW_LINE);

    file.println(tab1 + "public void writeByteArray(com.sap.engine.services.rmi_p4.P4ObjectOutput out, byte[] data) throws java.io.IOException {");
    file.println(tab2 + "try{");
    file.println(tab3 + "if(data == null){");
    file.println(tab4 + "out.writeInt(-1);");
    file.println(tab3 + "} else if(data != null && data.length == 0){");
    file.println(tab4 + "out.writeInt(0);");
    file.println(tab3 + "} else {");
    file.println(tab4 + "out.writeInt(data.length);");
    file.println(tab4 + "out.write(data);");
    file.println(tab3 + "}");
    file.println(tab2 + "}catch(java.io.IOException ioe){");
    file.println(tab3 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this + \". Problem while write byte[] in the stream : \" + ioe.getMessage());");
    file.println(tab3 + "com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().throwing(ioe);");
    file.println(tab3 + "throw ioe;");
    file.println(tab2 + "}");
    file.println(tab1 + "}" + NEW_LINE + NEW_LINE);
  }

  /**
   * Generate Skeleton
   *
   * @throws java.io.IOException
   */
  public void generateSkeleton() throws java.io.IOException {
    String skelName = makeSkeletonName(remote_class.getName());

    /* make skel file and write header */
    PrintWriter file = makeFile(skelName, remote_class);

    generateSkelSignature(skelName, file);

    generateSkelOperation(skelName, file);

    generateSkelDispatchSignature(file);

    generateSkelCases(file);

    /* generate read-write byte array methods */
    generateSerializeMethods(file);

    file.println("}");
    file.close();
  }

  private void generateSkelCases(PrintWriter file) {

    file.println(tab2 + "P4ObjectInput in = call.getInputStream();");
    file.println(tab2 + "DataOptInputStream din = call.getDataInputStream();");
    file.println(NEW_LINE);

    file.println(tab2 + "switch (opnum) {" + NEW_LINE);
    Enumeration enumer = remoteMethods.keys();
    int opnum = 0;
    while (enumer.hasMoreElements()) {
      String key = (String) enumer.nextElement();
      Method remote_method = (Method) remoteMethods.get(key);
      //System.out.println("1. Key : " + key + "| remote_method = " + remote_method + "| is opt params : " + isOptimizableParams(remote_method) + "| is opt ret: " + isOptimizableReturnType(remote_method));
      generateSkelCase(remote_method, opnum++, file, false);

      if (isOptimizableParams(remote_method) || isOptimizableReturnType(remote_method)) {
        generateSkelCase(remote_method, opnum++, file, true);
      }
    }

    file.println(tab2 + "}");
    file.println(tab1 + "}" + NEW_LINE + NEW_LINE);
  }

  private void generateSkelSignature(String skelName, PrintWriter file) {
    /* write import class */
    file.println("import java.rmi.server.Operation;");
    file.println();
    file.println("import com.sap.engine.services.rmi_p4.*;");

    file.println(NEW_LINE);
    file.println("/**");
    file.println(" *");
    file.println(" * @author  SAP's RMIC Generator");
    file.println(" * @version SAP Java EE Application Server");
    file.println(" */");

    /* write skel class name */
    file.print("public class " + skelName);

    if (!p4_remote_object.isAssignableFrom(remote_class)) {
      file.print(" extends P4RemoteObject ");
      file.println(" implements com.sap.engine.services.rmi_p4.SkeletonOpt, java.rmi.Remote {");
    } else {
      file.println(" implements com.sap.engine.services.rmi_p4.SkeletonOpt {");
    }
    file.println();
  }

  private void generateSkelOperation(String skelName, PrintWriter file) {

    /* write field Operations */
    file.print(tab1 + "private static final Operation[] operations = {");
    Enumeration enumer = remoteMethods.keys();

    while (enumer.hasMoreElements()) {
      String key = (String) enumer.nextElement();

      file.print("" + NEW_LINE + tab3 + "new Operation(\"" + key + "\")");

      Method remote_method = (Method) remoteMethods.get(key);

      boolean isParamOpt = isOptimizableParams(remote_method);
      boolean isResultOpt = isOptimizableReturnType(remote_method);
      if (isParamOpt || isResultOpt) {
        if(isParamOpt && isResultOpt){
          file.print("," + NEW_LINE + tab3 + "new Operation(\"" + BOTH_OPTIMIZED + key + "\")");
        } else if(isParamOpt){
          file.print("," + NEW_LINE + tab3 + "new Operation(\"" + PARAM_OPTIMIZED + key + "\")");
        } else {
          file.print("," + NEW_LINE + tab3 + "new Operation(\"" + RESULT_OPTIMIZED + key + "\")");
        }
      }

      if (enumer.hasMoreElements()) {
        file.print(",");
      } else {
        file.print("");
      }

    }

    file.println("};" + NEW_LINE);

    /*  write Skeleton's constructor */
    file.println(tab1 + "public " + skelName + " () {");
    file.println(tab1 + "}" + NEW_LINE);

    /* write getOperations() method */
    file.println(tab1 + "public Operation[] getOperations() {");
    file.println(tab2 + "return operations;");
    file.println(tab1 + "}" + NEW_LINE);
    file.print(tab1 + "private static final String[] _implements = {");

    for (int i = 0; i < remoteInterfaces.length; i++) {
      file.print("" + NEW_LINE + tab3 + "\"" + remoteInterfaces[i].getName() + "\"" + (i != (remoteInterfaces.length - 1) ? "," : ""));
    }

    file.println("};" + NEW_LINE);
    file.println(tab1 + "public String[] getImplemntsObjects() {");
    file.println(tab2 + "return _implements;");
    file.println(tab1 + "}" + NEW_LINE);
  }

  private void generateSkelDispatchSignature(PrintWriter file) {
    /* write dispatch() method */
    file.println(tab1 + "public void dispatch(java.rmi.Remote remote, Dispatch call, int opnum) throws Exception {");
    file.println();

    if (!p4_remote_object.isAssignableFrom(remote_class)) {
      file.println(tab2 + remote_class.getName() + " impl = (" + remote_class.getName() + ") delegate();");
    } else {
      file.println(tab2 + remote_class.getName() + " impl = (" + remote_class.getName() + ") remote;");
    }

  }

  private void generateSkelCase(Method mm, int opnum, PrintWriter file, boolean optimized) {

    Class returnType = mm.getReturnType();
    Class[] parameters = mm.getParameterTypes();
    boolean isOptParams = isOptimizableParams(mm);
    boolean isOptResult = isOptimizableReturnType(mm);

    if (optimized) {
      if(isOptParams && isOptResult){
        file.println(tab3 + "case " + opnum + " : {" + "  //method " + BOTH_OPTIMIZED + mm);
      } else if(isOptParams){
        file.println(tab3 + "case " + opnum + " : {" + "  //method " + PARAM_OPTIMIZED + mm);
      } else {
        file.println(tab3 + "case " + opnum + " : {" + "  //method " + RESULT_OPTIMIZED + mm);
      }
    } else {
      file.println(tab3 + "case " + opnum + " : {" + "  //method " + mm);
    }
    file.println(tab4 + "try {");


    /* write reading parameters from in stream */
    for (int i = 0; i < parameters.length; i++) {
      generateSkelReadParam(parameters[i], i, file, (optimized && isOptParams));
    }

    if (!returnType.getName().equals("void")) {
      file.print(tab5 + nameToType(returnType) + " _result = impl." + mm.getName() + "(");

      for (int i = 0; i < parameters.length; i++) {
        file.print("param" + i + ((i == parameters.length - 1) ? "" : ", "));
      }

      file.println(");");
      if(optimized && isOptResult){
        file.println(tab5 + "DataOptOutputStream dout = call.getDataOutputStream();");
      } else {
        file.println(tab5 + "P4ObjectOutput out = call.getOutputStream();");
      }

      if (optimized && isOptResult && returnType.isAssignableFrom((new byte[0]).getClass())){
        file.println(tab5 + "writeByteArray(dout, _result); // write result as byte[]");
      } else if(optimized && isOptResult && returnType.getName().equals("java.lang.String")){
        file.println(tab5 + "if(_result == null){");
        file.println(tab6 + "writeByteArray(dout, null); // write null");
        file.println(tab5 + "} else {");
        file.println(tab6 + "writeByteArray(dout, _result.getBytes()); // write result as byte[]");
        file.println(tab5 + "}");
      } else if (returnType.getName().equals("java.lang.Class")) {
        file.println(tab5 + "if(_result != null){");
        file.print(tab6 + "out.writeObject");
        file.println("(new com.sap.engine.services.rmi_p4.P4ClassWrapper((java.lang.Class)_result));"); // : ("(_result);"));
        file.println(tab5 + "} else {");
        file.println(tab6 + "out.writeObject(null);");
        file.println(tab5 + "}");
      } else {
        if(optimized && isOptResult){
          file.print(tab5 + "dout.write" + ioType(returnType));
        } else {
          file.print(tab5 + "out.write" + ioType(returnType));
        }
        file.println("( _result);"); // : ("(_result);"));
      }
      if(optimized && isOptResult){
        file.println(tab5 + "dout.flush();");
      } else {
        file.println(tab5 + "out.flush();");
      }
    } else {
      file.print(tab5 + "impl." + mm.getName() + "(");
      for (int i = 0; i < parameters.length; i++) {
        file.print("param" + i + ((i == parameters.length - 1) ? "" : ", "));
      }

      file.println(");");

      if (optimized && isOptResult) {
        file.println(tab5 + "DataOptOutputStream dout = call.getDataOutputStream();");
      } else {
        file.println(tab5 + "P4ObjectOutput out = call.getOutputStream();");
      }
    }

    file.println(tab4 + "} catch (java.lang.Exception ex) {");
    file.println(tab5 + "throw ex;");
    file.println(tab4 + "}");
    file.println(tab4 + "break;");
    file.println(tab3 + "}");
  }

  private void generateSkelReadParam(Class parameter, int count, PrintWriter file, boolean optimized) {
    String type = nameToType(parameter);

    if (parameter.isPrimitive()) {
      if (optimized) {
        file.print(tab5 + type + " param" + count + " = ");
        file.println("din.read" + ioType(parameter) + "();");
      } else {
        file.print(tab5 + type + " param" + count + " = ");
        file.println("in.read" + ioType(parameter) + "();");
      }

    } else if (optimized && parameter.isAssignableFrom((new byte[0]).getClass())) {
      file.println(tab5 + "byte[] param" + count + " = null;");
      file.println(tab5 + "param" + count + " = readByteArray(din);");
      //generateOptReadByteArr("param" + count, "din", file, tab5);
    } else if (optimized && parameter.getName().equals("java.lang.String")) {
      file.println(tab5 + "String param" + count + " = null;");
      generateOptReadString("param",count, "din", file, tab5);

    } else {
      file.println(tab5 + "Object obj" + count + ";");
      file.println(tab5 + type + " param" + count + ";");
      file.println(tab5 + "obj" + count + " = in.read" + ioType(parameter) + "();");
      file.println(tab5 + "try {");
      file.print(tab6 + " param" + count + " = ");
      file.println("(" + type + ")obj" + count + ";");
      file.println(tab5 + "} catch (ClassCastException ex) {");
      file.print(tab6 + " param" + count + " = ");
      file.println("(" + type + ") P4ObjectBroker.init().narrow(obj" + count + "," + type + ".class);");
      file.println(tab5 + "}");
    }
  }

  private void generateOptReadString(String param, int count, String stream, PrintWriter file, String tab) {
    String addd = "";
    if(count >= 0){
      addd += count;
    }
    file.println(tab + "/*  read " + param + " as byte[] */");
    file.println(tab + "byte[] __obj" + addd + " = readByteArray(" + stream + ");");
    file.println(tab + "if(__obj" + addd + " == null){");
    file.println(tab + tab1 + param + addd + " = null;");
    file.println(tab + "} else {");
    file.println(tab + tab1 + param + addd + " = new String(__obj" + addd + ");");
    file.println(tab + "}");
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

  /**
   * Make Skel class name from class name
   *
   * @param className
   * @return SkeletonName
   */
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


  private String ioOptType(Class _toRW) {
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
    } else if (_toRW.isArray() && _toRW.getName().equals("[B")) {
      return "";
    } else if (_toRW.getName().equals("java.lang.String")) {
      return "Bytes";
    }

    return "";//TODO PROBLEM!!!
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
      case 'B': {
        type = "byte" + arrDimension;
        break;
      }
      case 'C': {
        type = "char" + arrDimension;
        break;
      }
      case 'D': {
        type = "double" + arrDimension;
        break;
      }
      case 'F': {
        type = "float" + arrDimension;
        break;
      }
      case 'I': {
        type = "int" + arrDimension;
        break;
      }
      case 'J': {
        type = "long" + arrDimension;
        break;
      }
      case 'L': {
        type = type.substring(1, type.length() - 1) + arrDimension;
        break;
      }
      case 'S': {
        type = "short" + arrDimension;
        break;
      }
      case 'Z': {
        type = "boolean" + arrDimension;
        break;
      }
    }

    return type;
  }

  private PrintWriter makeFile(String _fileName, Class _forClass) throws java.io.IOException {
    String package_ = getPackageName(_forClass.getName());
    String fileName = package_.replace('.', File.separatorChar); // +File.separatorChar + _fileName;
    File __file = new File(destDir + File.separatorChar + fileName);
    RMICLogger.logMSG("P4StubSkeletonGenerator create dir: " + __file + ":" + __file.mkdirs());
    File _file = new File(__file, _fileName + ".java");

    if (fileName.length() > 0) {
      fileName += File.separatorChar + _fileName;
    } else {
      fileName = _fileName;
    }

    files.addElement(fileName);
    fileName += ".java";

    FileOutputStream fos = new FileOutputStream(_file.getCanonicalPath(), false);
    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
    PrintWriter file = new PrintWriter(osw, true);

    file.println("// Class generated by SAP Labs Bulgaria's RMIC Generator");
    file.println("// Don't change it !!");
    file.println();
    file.println();

    //write package
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
        result += " _param" + Integer.toString(i);
      } else {
        result += "(" + nameToType(parameters[i]) + ") P4ObjectBroker.init().narrow(p4_replicateWithStreams(inn1,out1,_param" + Integer.toString(i) + ")," + nameToType(parameters[i]) + ".class)";
      }

      result += (i == parameters.length - 1) ? "" : ", ";
    }

    return result;
  }

  private boolean checkElementType(Class _class) {
    if (_class.isArray()) {
      return checkElementType(_class.getComponentType());
    } else {
      return _class.isPrimitive() || _class.getName().startsWith("java.lang.String");
    }
  }

  private boolean isOptimizable(Class _class) {
    String type = _class.getName().replace('$', '.');
    int last = type.lastIndexOf('[');
    char descriptor = type.charAt(last + 1);
    type = type.substring(last + 1);
    if (type.equals("boolean")) {
      return true;
    } else if (type.equals("byte")) {
      return true;
    } else if (type.equals("short")) {
      return true;
    } else if (type.equals("char")) {
      return true;
    } else if (type.equals("int")) {
      return true;
    } else if (type.equals("long")) {
      return true;
    } else if (type.equals("float")) {
      return true;
    } else if (type.equals("double")) {
      return true;
    } else if (type.equals("void")) {
      return true;
    } else if (type.equals("java.lang.String")) {
      return true;
    }
    switch (descriptor) {
      case 'B': {
        return true;
      }
    }
    return false;
  }

  public boolean isOptimizableMethod(Method m) {
    if (m != null) {
      boolean opt = isOptimizableParams(m);
      opt &= isOptimizableReturnType(m);
      return opt;
    }
    return false;
  }

  public boolean isOptimizableParams(Method m) {
    if (m != null) {
      boolean opt = true;
      Class[] params = m.getParameterTypes();
      for (Class param : params) {
        opt &= isOptimizable(param);
      }
      return opt;
    }
    return false;
  }

  public boolean isOptimizableReturnType(Method m) {
    if (m != null) {
      Class retType = m.getReturnType();
      return isOptimizable(retType);
    }
    return false;
  }

}