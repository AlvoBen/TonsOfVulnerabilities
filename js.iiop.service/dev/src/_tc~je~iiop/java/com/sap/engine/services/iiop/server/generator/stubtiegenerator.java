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
package com.sap.engine.services.iiop.server.generator;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Public class StubTieGenerator is used to load the XML Document,
 * as well as the XSL template,and using the InQMy XSLTProcessor
 * to generate the source code needed for Stubs, Ties and Proxies.
 * Eventually the XML files are deleted as they are not necessary
 *
 * @author Ralitsa Bozhkova
 * @version 4.0
 */
public class StubTieGenerator {

  private String className;
  private TransformerFactory factory;
  private String workDir;
  private Vector xml;
  private String inXSLStub;
  private String inXSLTie;
  private String[] interfaces;
  private Vector remoteObjects;
  private Class[] remoteInterfaces;
  private boolean onlyStub;
  private Vector stubsAndTies;
  private boolean generateAdditional;
  private DescriptorWriter descriptor;
  private Hashtable remoteMethods;
  private Hashtable access;

  private static String[] strings = {"_Stub.java", "_Tie.java", ".xml"};
  private static String dot = ".";
  private static String unL = "_";

  /**
   * Constructor for the StubTieGenerator
   *
   * @param workDir is the directory name where java files will be generated
   */
  public StubTieGenerator(Class cls, String workDir, Hashtable access, boolean generateAdditional) throws Exception {
    SystemProperties.setProperty("javax.xml.transform.TransformerFactory", "com.sap.engine.lib.jaxp.TransformerFactoryImpl");
    ClassLoader storedContextLoader = null;
    try {
      storedContextLoader = Thread.currentThread().getContextClassLoader();
      // set current contextClassLoader, because of the reference to sapxmltoolkit.jar
      Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
      factory = TransformerFactory.newInstance();
      descriptor = new DescriptorWriter(cls, access);
      className = cls.getName();
      inXSLStub = "com/sap/engine/services/iiop/recources/xsl/template_Stub.xsl";
      inXSLTie = "com/sap/engine/services/iiop/recources/xsl/template_Tie.xsl";
      stubsAndTies = new Vector();
      this.workDir = workDir;

      if (cls.isInterface() || Proxy.isProxyClass(cls)) {
        onlyStub = true;
      }

      this.generateAdditional = generateAdditional;
      this.access = access;
      remoteInterfaces = descriptor.getRemoteInterfaces();
      remoteMethods = descriptor.getRemoteMethods();
      xml = generateXML(className);
    } finally {
      Thread.currentThread().setContextClassLoader(storedContextLoader);
    }
  }

  /**
   * This method starts the generation
   *
   * @return  - a vector with the file names
   */
  public Vector generate() {
    if (!onlyStub) {
      generateTie();
    }

    generateStubs();
    removeXmlFiles();
    return stubsAndTies;
  }

  /**
   * This method processes the generated XML files,
   * generates the java source code for Stubs
   *
   */
  public void generateStubs() {
    try {
      if (xml.size() > 0) {
        String[] stubRes = new String[xml.size()];
        String temp[] = new String[stubRes.length];

        if (xml.size() == 1) {
          if (onlyStub) {
            temp[0] = unL + className.substring(className.lastIndexOf(dot) + 1) + strings[0]; // _Stub.java
            stubRes[0] = makeFile(temp[0], className);
          } else {
            temp[0] = unL + interfaces[0].substring(interfaces[0].lastIndexOf(dot) + 1) + strings[0]; // _Stub.java
            stubRes[0] = makeFile(temp[0], interfaces[0]);
          }
        } else if (!generateAdditional) {
          temp[0] = unL + className.substring(className.lastIndexOf(dot) + 1) + strings[0]; // _Stub.java
          stubRes[0] = makeFile(temp[0], className);

          for (int i = 1; i < interfaces.length + 1; i++) {
            temp[i] = unL + interfaces[i - 1].substring(interfaces[i - 1].lastIndexOf(dot) + 1) + strings[0]; // _Stub.java
            stubRes[i] = makeFile(temp[i], interfaces[i - 1]);
            Transformer transformer;
            transformer = factory.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream(inXSLStub)));
            transformer.transform(new StreamSource((String) xml.elementAt(i)), new StreamResult(stubRes[i]));
            stubsAndTies.addElement(stubRes[i]);
          } // for
        } else {
          int count = interfaces.length;

          if (count == 1) {
            temp[0] = unL + interfaces[0].substring(interfaces[0].lastIndexOf(dot) + 1) + strings[0]; // _Stub.java
            stubRes[0] = makeFile(temp[0], interfaces[0]);
          } else {
            count++;
            temp[0] = unL + className.substring(className.lastIndexOf(dot) + 1) + strings[0]; // _Stub.java
            stubRes[0] = makeFile(temp[0], className);

            for (int i = 1; i < count; i++) {
              temp[i] = unL + interfaces[i - 1].substring(interfaces[i - 1].lastIndexOf(dot) + 1) + strings[0]; // _Stub.java
              stubRes[i] = makeFile(temp[i], interfaces[i - 1]);
              Transformer transformer;
              transformer = factory.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream(inXSLStub)));
              transformer.transform(new StreamSource((String) xml.elementAt(i)), new StreamResult(stubRes[i]));
              stubsAndTies.addElement(stubRes[i]);
            } // for
          }

          for (int i = count; i < xml.size(); i++) {
            String name = (String) remoteObjects.elementAt(i - count);
            temp[i] = unL + name.substring(name.lastIndexOf(dot) + 1) + strings[0]; // _Stub.java
            stubRes[i] = makeFile(temp[i], name);
            Transformer transformer;
            transformer = factory.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream(inXSLStub)));
            transformer.transform(new StreamSource((String) xml.elementAt(i)), new StreamResult(stubRes[i]));
            stubsAndTies.addElement(stubRes[i]);
          } // for
        }// if generateAdditional

        Transformer transformer;
        transformer = factory.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream(inXSLStub)));
        transformer.transform(new StreamSource((String) xml.elementAt(0)), new StreamResult(stubRes[0]));
        stubsAndTies.addElement(stubRes[0]);
      } // big if
    } catch (Exception ex) {
	  if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
		LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("StubTieGenerator.generateStubs()", "Error during parsing XML file. Cannot generate stub file." + LoggerConfigurator.exceptionTrace(ex));
	  }	
    }
  }

  /**
   * This method processes the generated XML files,
   * generates the java source code for Ties
   *
   */
  public void generateTie() {
    try {
      if (xml.size() > 0) {
        String temp = unL + className.substring(className.lastIndexOf(dot) + 1) + strings[1]; // _Tie.java
        String tieRes = makeFile(temp, className);
        Transformer transformer;
        transformer = factory.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream(inXSLTie)));
        transformer.transform(new StreamSource((String) xml.elementAt(0)), new StreamResult(tieRes));
        stubsAndTies.addElement(tieRes);
      } // if
    } catch (Exception ex) {
   	  if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
	    LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("StubTieGenerator.generateTie()", "Error during parsing XML file. Cannot generate tie file." + LoggerConfigurator.exceptionTrace(ex));
	  }	
    }
  }

  /**
   * This method processes the class name and generates
   * the XML files as necessary
   *
   * @param clsName  - clsName represents the name of the class for which the Stub
   *            and/or Skeleton is generated
   * @return  - the names of the generated XML files, as a vector of strings
   */
  public Vector generateXML(String clsName) {
    int interfLen = remoteInterfaces.length;
    interfaces = new String[interfLen];
    Vector xmls = new Vector();
    try {
      // if the class implements more than one interfaces
      // XML files are generated for each one of them
      if (remoteInterfaces != null) {
        if (interfLen > 1) {
          for (int k = 0; k < interfLen; k++) {
            interfaces[k] = remoteInterfaces[k].getName();
          } 

          String temp = clsName.substring(clsName.lastIndexOf(dot) + 1) + strings[2]; // .xml
          String xmlFile = makeFile(temp, clsName);
          xmls.addElement(xmlFile);

          for (int c = 1; c < interfLen + 1; c++) {
            temp = interfaces[c - 1].substring(interfaces[c - 1].lastIndexOf(dot) + 1) + strings[2]; // .xml
            xmlFile = makeFile(temp, interfaces[c - 1]);
            xmls.addElement(xmlFile);
          } 

          DescriptorWriter[] dw = new DescriptorWriter[interfLen];
          ClassDescriptor[] cd = new ClassDescriptor[interfLen];
          ClassDescriptorDocument[] cdd = new ClassDescriptorDocument[interfLen];

          for (int k = 0; k < interfLen; k++) {
            dw[k] = new DescriptorWriter(remoteInterfaces[k], access);
            cd[k] = dw[k].setClassToDescriptor();
            cdd[k] = new ClassDescriptorDocument(cd[k]);
            cdd[k].writeDTDtoFile((String) xmls.elementAt(k + 1));
          } // for
        }// if
        // otherwise only one XML file is necessary
        else {
          if (interfLen == 1) {
            interfaces[0] = remoteInterfaces[0].getName();
            String temp = className.substring(className.lastIndexOf(dot) + 1) + strings[2]; // .xml
            String xmlFile = makeFile(temp, className);
            xmls.addElement(xmlFile);
          } // if implement.length == 1
          else {
            return new Vector();
          } // last else
        }// else

        ClassDescriptor cd = descriptor.setClassToDescriptor();
        ClassDescriptorDocument cdd = new ClassDescriptorDocument(cd);
        cdd.writeDTDtoFile((String) xmls.elementAt(0));

        if (generateAdditional) {
          remoteObjects = new Vector();
          String methName = "";

          for (Enumeration en = remoteMethods.keys(); en.hasMoreElements();) {
            methName = (String) en.nextElement();
            Method method = (Method) remoteMethods.get(methName);
            Class[] param = method.getParameterTypes();
            Class retType = method.getReturnType();
            String type = retType.getName();

            if (java.rmi.Remote.class != retType && java.rmi.Remote.class.isAssignableFrom(retType) && !remoteObjects.contains(type)) {
              remoteObjects.addElement(type);
            }

            for (int j = 0; j < param.length; j++) {
              String var = param[j].getName();

              if (java.rmi.Remote.class != param[j] && java.rmi.Remote.class.isAssignableFrom(param[j]) && !remoteObjects.contains(var)) {
                remoteObjects.addElement(var);
              }
            } 
          }// for 

          int objSize = remoteObjects.size();

          if (objSize > 0) {
            for (int j = 0; j < objSize; j++) {
              String name = (String) remoteObjects.elementAt(j);
              String temp = name.substring(name.lastIndexOf(dot) + 1) + strings[2]; // .xml
              String xmlFile = makeFile(temp, name);
              xmls.addElement(xmlFile);
            } 

            DescriptorWriter[] dw = new DescriptorWriter[objSize];
            ClassDescriptor[] classd = new ClassDescriptor[objSize];
            ClassDescriptorDocument[] classdd = new ClassDescriptorDocument[objSize];

            for (int k = 0; k < objSize; k++) {
              dw[k] = new DescriptorWriter(Class.forName((String) remoteObjects.elementAt(k)), access);
              classd[k] = dw[k].setClassToDescriptor();
              classdd[k] = new ClassDescriptorDocument(classd[k]);

              if (interfLen == 1) {
                classdd[k].writeDTDtoFile((String) xmls.elementAt(1 + k));
              } else {
                classdd[k].writeDTDtoFile((String) xmls.elementAt(interfLen + 1 + k));
              }
            }// for 
          }// if objSize > 0
        }// if generateAdditional
      }// big if // remoteInterfaces != null 
      else {
        return new Vector();
      }
    } catch (Exception e) {
	  if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
	    LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("StubTieGenerator.generateXML(String)", "Needed XML file, but it cannot be generated." + LoggerConfigurator.exceptionTrace(e));
	  }	
    }
    return xmls;
  }

  /**
   * A helper method for replacing a substring of a
   * given string with another substring
   *
   * @param   s - the initial string
   * @param   s1 - the string to be replaced
   * @param   s2 - the string to replace s1 with
   * @return  the new string, where s1 is replaced by s2
   */
  private String replace(String s, String s1, String s2) {
    StringBuffer buffer = new StringBuffer(s);
    int i = s.indexOf(s1);

    while (i != -1) {
      buffer.replace(i, s1.length() + s.indexOf(s1), s2);
      s = buffer.toString();
      i = s.indexOf(s1);
    }

    return s;
  }

  /**
   * A helper method for removing the unnecessary
   * XML files from server directory
   *
   */
  public void removeXmlFiles() {
    for (int k = 0; k < xml.size(); k++) {
      File f = new File((String) xml.elementAt(k));
      f.delete();
      xml.removeElementAt(k--);
    } 
  }

  /**
   * A helper method for creating xml, Stub and Tie files
   * and path directories
   *
   * @param   fileName  the filename for which generation is made
   * @param   className  the class name for which generation is made
   * @return  the absolute class path to the generated file
   */
  private String makeFile(String fileName, String className) {
    StringBuffer buffer = new StringBuffer(workDir);
    buffer.append(File.separator);

    if (className.indexOf(dot) != -1) {
      buffer.append(replace(className.substring(0, className.lastIndexOf(dot)), dot, File.separator));
    }

    buffer.append(File.separator);
    buffer.append(fileName);
    String fileRes = buffer.toString();
    File file = new File(fileRes);
    file.getParentFile().mkdirs();
    return fileRes;
  }

}

