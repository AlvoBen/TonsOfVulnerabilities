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

package com.sap.engine.lib;

import com.sap.engine.rmic.log.RMICLogger;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.jar.*;

/**
 * Creates jar files using InfoObjects which contain information about
 * names of jar entries and their fully specified path.
 * Provides methods for finding class files in standard
 * and additional java class paths.
 *
 * @author Rossitca Andreeva
 * @author Monika Kovachka
 * @author Mariela Todorova
 * @version 4.0
 */
public class JarUtils {

  protected String jarFile = null;
  protected Vector infoObjects = new Vector();
  //  protected Vector additionalFiles = null;
  protected ClassLoader loader;
  protected String additionalPaths = null;
  protected int compressMethod = ZipEntry.STORED;

  /**
   * Constructor for the class.
   */
  public JarUtils() {

  }

  /**
   * Constructor for the class with the specified jar file name.
   *
   * @param jarFile the name of jar file that will be generated.
   */
  public JarUtils(String jarFile) {
    setJarFile(jarFile);
  }

  /**
   * Constructs class with the specified name of the jar
   * which will be created and Vector of InfoObjects
   * which will be used to construct the new jar.
   *
   * @param jarFile the name of the jar file.
   * @param files   Vector of InfoObject classes.
   */
  public JarUtils(String jarFile, Vector files) {
    setJarFile(jarFile);
    setInfoObjects(files);
  }

  /**
   * Constructs the class with the specified name of jar file, Vector of
   * InfoObjects and ClassLoader that is used to load class files where needed.
   *
   * @param jarFile the name of the generated jar file.
   * @param files   Vector of InfoObjects,
   *                with the files that will be included in the jar.
   * @param loader  ClassLoader for loading class files.
   */
  public JarUtils(String jarFile, Vector files, ClassLoader loader) {
    setJarFile(jarFile);
    setInfoObjects(files);
    setLoader(loader);
  }

  public void setCompressMethod(int method) {
    this.compressMethod = method;
  }

  public int getCompressMethod() {
    return this.compressMethod;
  }

  /**
   * Sets the name of the ouput jar file.
   *
   * @param jarName the name of the jar file.
   */
  public void setJarFile(String jarName) {
    this.jarFile = jarName;
  }

  /**
   * Initializes the list of InfoObjects used for making jar file.
   *
   * @param infos list of InfoObjects.
   */
  public void setInfoObjects(Vector infos) {
    this.infoObjects = infos;
  }

  /**
   * Sets the specified ClassLoader, necessary for loading the
   * classes which will be included in the jar file.
   *
   * @param cLoader the class loader value.
   */
  public void setLoader(ClassLoader cLoader) {
    this.loader = cLoader;
  }

  /**
   * Sets additional classpath that is used to search the classes
   * in the directories and archive files in it. The additional path
   * must have the same form as the standard java classpath.
   *
   * @param paths the additional path.
   */
  public void setAdditionalPaths(String paths) {
    this.additionalPaths = paths;
  }

  /**
   * Returns additional path value.
   *
   * @return the value of additional path.
   */
  public String getAdditionalPaths() {
    return this.additionalPaths;
  }

  /**
   * Makes jar file by given jar file name and specified directory
   * with files that will be included in the jar file.
   *
   * @param jarName name of the ouput jar.
   * @param dirName name of the source directory.
   * @throws IOException Exception for opening and reading from zip or
   *                     when writing the output files.
   */
  public void makeJarFromDir(String jarName, String dirName) throws IOException {
    makeJarFromDir(jarName, new String[]{dirName});
  }

  /**
   * Makes jar file by given jar file name and specified list of directories
   * with files that will be included in the jar file.
   *
   * @param jarName  name of the ouput jar.
   * @param dirNames array of dir names.
   * @throws IOException Exception for opening and reading from zip or
   *                     when writing the output files.
   */
  public void makeJarFromDir(String jarName, String[] dirNames) throws IOException {
    Vector vec = new Vector();

    for (int i = 0; i < dirNames.length; i++) {
      listDir(dirNames[i], vec);
    }

    makeJarFromFiles(jarName, vec);
  }

  /**
   * Makes jar file by given jar file name and specified list of directories
   * with files that will be included in the jar file.
   *
   * @param jarName  name of the ouput jar.
   * @param dirNames array of dir names.
   * @param filters  extensions of the files which must be included.
   * @throws IOException Exception for opening and reading from zip or
   *                     when writing the output files.
   */
  public void makeJarFromDir(String jarName, String[] dirNames, Vector filters) throws IOException {
    Vector vec = new Vector();

    for (int i = 0; i < dirNames.length; i++) {
      listDir(dirNames[i], vec);
    }

    //TODO : Da se napravi pri list elements
    if (filters != null && !filters.isEmpty()) {
      InfoObject info = null;
      boolean found = false;

      for (int j = vec.size() - 1; j >= 0; j--) {
        info = (InfoObject) vec.elementAt(j);
        found = false;

        for (int ind = 0; ind < filters.size(); ind++) {
          if (info.getEntryName().endsWith((String) filters.elementAt(ind))) {
            found = true;
            break;
          }
        }

        if (!found) {
          vec.removeElementAt(j);
        }
      }
    }

    makeJarFromFiles(jarName, vec);
  }

  /**
   * Makes jar file by given jar file name and specified list of InfoObjects
   * with files that will be included in the jar file.
   *
   * @param jarName name of the ouput jar.
   * @param files   Vector of InfoObjects which to be added in the jar.
   * @throws IOException Exception for opening and reading from zip or
   *                     when writing the output files.
   */
  public void makeJarFromFiles(String jarName, Vector files) throws IOException {
    writeJarFile(jarName, files);
  }

  /**
   * Makes jar file using jar file name and list of InfoObjects
   * with files that will be included in the jar file.
   * The name of jar file and the list of InfoObjects are specified when
   * constructing JarUtils or by using JarUtils' set-methods.
   * If jar file or list of InfoObjects have not been specified,
   * jar file will not be created.
   *
   * @throws IOException Exception for opening and reading from zip or
   *                     when writing the output files.
   */
  public void makeJar() throws IOException {
    writeJarFile(jarFile, infoObjects);
  }

  /*
   * Method for writing jar file. It uses copyJFile method.
   * Opens JarOutputStream for writing in it the jar file.
   */
  private void writeJarFile(String jarName, Vector infos) throws IOException {
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    JarOutputStream zip = null;
    try {
      // Instance ZIP stream
      fos = new FileOutputStream(jarName);
      bos = new BufferedOutputStream(fos);
      zip = new JarOutputStream(bos);
      zip.setMethod(this.compressMethod);

      // Copy all files pointed for jar file:
      writeAdditionalEntries(zip);
      if (infos.size() > 0) {
        Enumeration fileObjects = infos.elements();
        InfoObject infoObj = null;

        while (fileObjects.hasMoreElements()) {
          // Copy InfoObjects :
          infoObj = (InfoObject) fileObjects.nextElement();

          if (infoObj != null) {
            copyJFile(zip, infoObj.getEntryName(), infoObj.getFilePath());
          }
        }
      } else {
        byte[] empty = new byte[]{};
        ZipEntry manifest = new ZipEntry(JarFile.MANIFEST_NAME);
        manifest.setSize(0);
        CRC32 crc = new CRC32();
        crc.update(empty);
        manifest.setCrc(crc.getValue());
        try {
          zip.putNextEntry(manifest);
          zip.write(empty, 0, 0);
        } catch (Exception e) { // $JL-EXC$
          RMICLogger.throwing(e);
        } finally {
          zip.closeEntry();
        }
      }
    } catch (ZipException ze) {// $JL-EXC$
      RMICLogger.throwing(ze);
    } finally {
      try {
        // Close ZIP stream:
        zip.flush();
        zip.finish();
        zip.close();
        bos.close();
        fos.close();
      } catch (Exception exx) {// $JL-EXC$
        RMICLogger.throwing(exx);

      }
    }
  }

  /*
   * Lists all files in the specified directory to the given vector.
   */
  private void listDir(String dirName, Vector allFiles) {
    if (dirName == null || dirName == "") {
      return;
    }

    File directory = new File(dirName);

    if (!directory.isDirectory()) {
      return;
    } else {
      File[] files = directory.listFiles();

      if ((files != null) && (files.length > 0)) {
        InfoObject info = null;

        for (int i = 0; i < files.length; i++) {
          if (!files[i].isDirectory()) {
            info = new InfoObject(getEntryName(files[i], ""), files[i].getAbsolutePath());
            allFiles.addElement(info);
          } else {
            recursivePack(files[i].getAbsolutePath(), "", allFiles);
          }
        }
      }
    }
  }

  /*
   * Writes a file with given entry name and file path into a JarOutputStream.
   * The method is divided in two :
   * --filepath is archieve - .jar, .war, .ear - if the file is archieve
   * then if this entry ends with .jar (.war, .ear etc.) this archieve
   * file is all added to the new jar (in this case JarOutputStream),
   * else if entry name ends with .class then a file with this entry name
   * will be searched in the given archieve file and if there is one found
   * the method adds it to the new jar, otherwise none of entries
   * will be included in the new jar;
   * --files are not archieve, then method adds them to the given JarOutputStream zip.
   */
  private void copyJFile(JarOutputStream zip, String entryName, String filePath) throws IOException {
    ZipFile zFile = null;
    ZipEntry entry = null;
    InputStream inStream = null;

    if (filePath.endsWith(".jar") || filePath.endsWith(".ear") || filePath.endsWith(".war") || filePath.endsWith(".rar") || filePath.endsWith(".zip")) {
      if ((!entryName.endsWith(".jar")) && (!entryName.endsWith(".ear")) && (!entryName.endsWith(".war")) && (!entryName.endsWith(".rar")) && (!entryName.endsWith(".zip"))) {
        zFile = new ZipFile(filePath);
        try {
          zFile = new ZipFile(filePath);
          entry = zFile.getEntry(entryName);

          if (entry != null) {
            makeEntry(zip, entryName, zFile.getInputStream(new ZipEntry(entryName)), zFile.getInputStream(new ZipEntry(entryName)));
          }
        } catch (Exception e) { //$JL-EXC$
          throw new IOException(e.getMessage());
        } finally {
          if (zFile != null) {
            zFile.close();
          }
        }
      } else {
        try {
          try {
            zFile = new ZipFile(filePath);
            entry = zFile.getEntry(entryName);
          } catch (Exception e) {//$JL-EXC$
            RMICLogger.throwing(e);
          }

          if (entry != null) {
            makeEntry(zip, entryName, zFile.getInputStream(new ZipEntry(entryName)), zFile.getInputStream(new ZipEntry(entryName)));
          } else {
            makeEntry(zip, entryName, new FileInputStream(filePath), new FileInputStream(filePath));
          }
        } catch (Exception e) { //$JL-EXC$
          throw new IOException(e.getMessage());
        } finally {
          if (zFile != null) {
            zFile.close();
          }
        }
      }
    } else { // file extension (of filePath) is different from .jar, .ear, .war
      try {
        makeEntry(zip, entryName, new FileInputStream(filePath), new FileInputStream(filePath));
      } catch (Exception e) {     //$JL-EXC$
        throw new IOException(e.getMessage());
      }
    }
  }

  private JarEntry makeEntry(ZipOutputStream zip, String entryName, InputStream in, InputStream check) throws IOException {
    if (in == null) {
      return null;
    }

    JarEntry entry = null;
    try {
      int count = 0;
      int size = 0;
      byte[] buff = new byte[16 * 1024];
      CRC32 crc = new CRC32();
      entry = new JarEntry(entryName.replace('\\', '/'));
      try {
        while ((count = check.read(buff)) != -1) {
          size += count;
          crc.update(buff, 0, count);
        }

        entry.setMethod(this.compressMethod);
        entry.setCrc(crc.getValue());
        entry.setSize(size);
        if (this.compressMethod == ZipEntry.STORED) {
          entry.setCompressedSize(size);
        }
        zip.putNextEntry(entry);

        while ((count = in.read(buff)) != -1) {
          zip.write(buff, 0, count);
        }
      } catch (EOFException ex) {//$JL-EXC$
        throw new IOException(ex.getMessage());
      }
    } catch (ZipException zipEx) {  //$JL-EXC$
      throw new IOException(zipEx.getMessage());
    } finally {
      in.close();
      zip.closeEntry();
      check.close();
    }
    return entry;
  }

  /*
   * Returns the entry name of this file.
   * If file is some of the specified xml files,
   * it should be put into META-INF directory.
   * If package name is specified,
   * this file should be put in a directory with such a name.
   */
  private String getEntryName(File file, String packName) {
    String tempName = file.getName();

    if (tempName.equals("jar-mappings.xml") || tempName.equals("service-jar.xml") || tempName.equals("commands.xml")) {
      tempName = "META-INF\\" + tempName;
    } else {
      tempName = (packName.trim().equals("") ? "" : packName + "\\") + tempName;
    }

    return tempName;
  }

  /*
   * Goes recursively through the given directory (and its subdirectories) and
   * lists all files in it to the specified Vector of files.
   */
  private void recursivePack(String dirName, String packageName, Vector files) {
    InfoObject info = null;
    File f = new File(dirName);

    if (f.isDirectory()) {
      File[] dirFiles = f.listFiles();

      if (dirFiles != null) {
        for (int i = 0; i < dirFiles.length; i++) {
          if (dirFiles[i].isDirectory()) {
            recursivePack(dirFiles[i].getAbsolutePath(), (packageName.equals("") ? "" : packageName + "\\") + f.getName(), files);
          } else {
            info = new InfoObject(getEntryName(dirFiles[i], (packageName.equals("") ? "" : packageName + "\\") + f.getName()),
                    //(packageName.equals("") ? "" : packageName + "\\") + f.getName() + "\\" + dirFiles[i].getName(),
                    dirFiles[i].getAbsolutePath());
            files.addElement(info);
          }
        }
      }
    } else {
      info = new InfoObject((packageName.equals("") ? f.getName() : packageName + "\\" + f.getName()), f.getAbsolutePath());
      files.addElement(info);
    }
  }

  /*
   * Adds empty entries corresponding to directory names in filepath of this jar entry.
   */
  protected void addDirEntries(JarOutputStream jos, String jarEntryName) throws IOException {
    int ind = jarEntryName.indexOf('/');
    String dir = "";
    JarEntry jentry = null;

    while (ind >= 0) {
      dir = dir + jarEntryName.substring(0, ind + 1);
      jarEntryName = jarEntryName.substring(ind + 1);
      ind = jarEntryName.indexOf('/');
      jentry = new JarEntry(dir);
      jentry.setSize(0);
      try {
        jos.putNextEntry(jentry);
        jos.closeEntry();
      } catch (ZipException z) { // $JL-EXC$
        jos.closeEntry();
      }
    }
  }

  /**
   * Writes additional entries into a JarOutputStream for one jar file.
   * If you want to write additional entries to the created jar file
   * by makeJarFromFiles() method or when distributing the jar files
   * in 6 jars it will be added to all of them.
   * You have to overwrite this method in your subclass of this class.
   *
   * @param zip the jar file output stream
   *            in which you can write the additional entries.
   */
  public void writeAdditionalEntries(JarOutputStream zip) throws IOException {

  }

  /**
   * Parses the given classpath and converts it into elements of Vector using
   * File.pathSeparator for separating the elements.
   *
   * @param clPath string representing the variable which will be parsed.
   * @return parsed classpath, i.e. list of String elements.
   */
  public Vector parsePath(String clPath) {
    Vector values = new Vector();

    if (clPath == null) {
      return values;
    }

    String tempPath = clPath;
    int index = -1;
    index = tempPath.indexOf(File.pathSeparator);
    String tempValue = "";

    while (index != -1) {
      tempValue = tempPath.substring(0, index);

      if (new File(tempValue).isDirectory()) {
        tempValue += File.separator;
      }

      values.addElement(tempValue);
      tempPath = tempPath.substring(index + 1, tempPath.length());
      index = tempPath.indexOf(File.pathSeparator);
    }

    if ((index == -1) && (tempPath.length() > 0)) {
      values.addElement(tempPath);
    }

    return values;
  }

}