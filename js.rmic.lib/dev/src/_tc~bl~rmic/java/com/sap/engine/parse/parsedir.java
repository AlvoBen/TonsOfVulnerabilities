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

package com.sap.engine.parse;

import com.sap.engine.lib.JarExtractor;
import com.sap.engine.lib.JarUtils;
import com.sap.engine.rmic.RMIC;
import com.sap.engine.rmic.log.RMICLogger;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

/**
 * @author Mladen Droshev
 *         user: i024084
 *         e-mail: mladen.droshev@sap.com
 *         Date: 2004-11-3
 */
public class ParseDir {

  private Vector remoteClasses = new Vector();
  private String jarFileName = null;
  private String rootDir = null;
  private String tempDir = "." + File.pathSeparator + "rmic_logs";
  public static final String END = ".class";
  String[] data = null;

  public ParseDir(String jarFileName, String tempDir) {
    this.tempDir = tempDir;
    this.jarFileName = jarFileName;
  }

  public ParseDir(String rootDir) {
    this.rootDir = rootDir;
  }

  public static Class[] super_classez = {java.rmi.Remote.class, javax.rmi.PortableRemoteObject.class};

  public void getClasses(String rootDir) {
    File root = new File(rootDir);
    listClassesFromDirs(root, root);
    data = new String[remoteClasses.size()];
    data = (String[]) remoteClasses.toArray(new String[0]);
  }

  public String[] getData() {
    return this.data;
  }

  public String[] listClassesFromDirs(File current, File root) {
    if (current.isDirectory()) {
      File[] list = current.listFiles();
      for (int i = 0; i < list.length; i++) {
        listClassesFromDirs(list[i], root);
      }
    } else {
      if (current.getName().endsWith(END)) {
        remoteClasses.addElement(getClassName(root, current));
      }
    }
    return (String[]) remoteClasses.toArray(new String[0]);
  }

  public Class[] findRemoteSupportClasses(String[] allClasses, ClassLoader loader) {
    Class[] results = null;
    Vector remote_classes = new Vector();
    if (allClasses != null) {
      for (int i = 0; i < allClasses.length; i++) {
        try {
          Class temp_class = loader.loadClass(allClasses[i]);
          for (int j = 0; j < super_classez.length; j++) {   // for all supported classes
            if (super_classez[j].isAssignableFrom(temp_class) && (!temp_class.getName().endsWith("_Stub")) && (!temp_class.getName().endsWith("_Skel")) && (!temp_class.getName().endsWith("_Tie"))) {
              remote_classes.addElement(temp_class);
            }
          }
        } catch (ClassNotFoundException e) {
          RMICLogger.logMSG("DEBUG : ParseDir There is a problem with find classes for remote support : class : " + allClasses[i]);
          RMICLogger.throwing(e);
        }

      }
    }
    results = (Class[]) remote_classes.toArray(new Class[0]);
    return results;
  }

  public Class[] getRemSupportClassesFromJar(String jarFile, String classpath, String tempDir) {
    extractJar();
    return getRemSupportClassesFromDir(tempDir, classpath);
  }

  public Class[] getRemSupportClassesFromDir(String dir, String classpath) {
    ClassLoader cLoader = makeURLLoader(this.getClass().getClassLoader(), makeURLResources(classpath));
    this.rootDir = dir;
    File root = new File(dir);
    String remCl[] = listClassesFromDirs(root, root);
    RMICLogger.logMSG(("DEBUG : " + (com.sap.engine.parse.ParseDir.class) + ".getRemSupportClassesFromDir : " + dir));
    if (remCl != null) {
      for (int i = 0; i < remCl.length; i++)
        RMICLogger.logMSG("DEBUG RemoteClass : " + remCl[i]);

    }
    return findRemoteSupportClasses(remCl, cLoader);
  }

  private ClassLoader makeURLLoader(ClassLoader parent, URL[] resources){
    return new URLClassLoader(resources, parent);
  }

  private URL createURL(String res){
    try {
      return (new File(res)).toURL();
    } catch (MalformedURLException e) {//$JL-EXC$
      RMICLogger.logMSG("DEBUG : ParseDir.createURL() Exception : " + e.getMessage());
      RMICLogger.throwing(e);
      return null;
    }
  }

  private URL[] makeURLResources(String classpath){
    URL[] res = null;
    if (classpath != null) {
      if (classpath.indexOf(RMIC.RMIC_CLASSPATH_DELIMETER) != -1) {
        Vector allRes = new Vector();
        StringTokenizer st = new StringTokenizer(classpath, RMIC.RMIC_CLASSPATH_DELIMETER);
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          URL url = createURL(token);
          if (url != null && token.length() > 0) {
            allRes.addElement(url);
          }
        }
        res = new URL[allRes.size()];
        res = (URL[]) allRes.toArray(new URL[0]);

      }
    }
    return res;
  }


  public String getClassName(File root, File current) {
    String rootPath = root.getAbsolutePath();
    String currentPath = current.getAbsolutePath();
    String temp = currentPath.substring(rootPath.length());
    return (temp.substring(1, temp.indexOf(END))).replace(File.separatorChar, '.');
  }

  public void extractJar() {
    JarExtractor extractor = new JarExtractor();
    try {
      extractor.extractJar(jarFileName, tempDir);
    } catch (IOException e) {
      RMICLogger.throwing(e);
    }
  }

  public void makeJarFile() {
    JarUtils jarMaker = new JarUtils();
    try {
      jarMaker.makeJarFromDir(jarFileName, tempDir);
    } catch (IOException e) {
      RMICLogger.logMSG("DEBUG : ParseDir : Cannot makeJarFromDir : jarname : " + jarFileName + "<> dir : " + tempDir);
      RMICLogger.throwing(e);
    }
  }

  public void setTempDir(String tempDir) {
    this.tempDir = tempDir;
  }

  public void setJarFileName(String jarFileName) {
    this.jarFileName = jarFileName;
  }

  public void setRootDir(String rootDir) {
    this.rootDir = rootDir;
  }

  public void deleteTempDir() {
    File temp = new File(tempDir);
    deleteCurrentFile(temp);
  }

  private void deleteCurrentFile(File roor) {
    if (roor != null)
      if (roor.isFile()) {
        roor.delete();
      } else {
        File lists[] = roor.listFiles();
        if (lists != null && lists.length > 0) {
          for (int i = 0; i < lists.length; i++) {
            deleteCurrentFile(lists[i]);
          }
        }
        roor.delete();
      }
  }


}
