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

import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * Extracts files from jar files. Provides methods for extracting a single file,
 * multiple files with specified extensions or all files from a directory.
 *
 * @author Rossitca Andreeva
 * @author Monika Kovachka
 * @author Mariela Todorova
 * @version 4.0
 */
public class JarExtractor {

  protected File jarFile = null;
  protected String outputDirectory = null;

  /**
   * Constructor for the class.
   */
  public JarExtractor() {

  }

  /**
   * Constructor for the class with the specified jar file name.
   *
   * @param jar  name of jar file that will be extracted.
   */
  public JarExtractor(String jar) {
    setJarFile(jar);
  }

  /**
   * Constructor for the class with the specified jar file name and
   * output directory name.
   *
   * @param jar        name of jar file that will be extracted.
   * @param outputDir  name of output directory.
   */
  public JarExtractor(String jar, String outputDir) {
    setJarFile(jar);
    setOutputDirectory(outputDir);
  }

  /**
   * Sets the name of the extracted jar file.
   *
   * @param  jar   the name of the jar file.
   */
  public void setJarFile(String jar) {
    if (jar != null) {
      this.jarFile = new File(jar);
    } else {
      return;
    }
  }

  /**
   * Sets the name of the ouput directory.
   *
   * @param  outputDir   the name of the output directory.
   */
  public void setOutputDirectory(String outputDir) {
    this.outputDirectory = outputDir;
  }

  /**
   * Extracts all files from the specified jar file to the specified output directory.
   *
   * @param jarName          the name of the jar file which will be extracted.
   * @param outputDir        the name of the output directory where
   *                         files will be extracted.
   *
   * @exception IOException  Exception for opening and reading from zip or
   *                         when writing the output files.
   */
  public void extractJar(String jarName, String outputDir) throws IOException {
    if (jarName == null || outputDir == null) {
      return;
    }

    JarFile zip = new JarFile(jarName);
    try {
      File dir = new File(outputDir);

      if (!dir.exists()) {
        dir.mkdirs();
      }

      if (!dir.isDirectory()) {
        return;
      }

      Enumeration enumer = zip.entries();
      String entryName = null;
      JarEntry theEntry = null;

      while (enumer.hasMoreElements()) {
        theEntry = (JarEntry) enumer.nextElement();
        entryName = theEntry.getName();

        if (entryName != null) {
          extractFile(zip, entryName, outputDir);
        }
      }
    } finally {
      zip.close();
    }
  }

  /**
   * Extracts all files with specified in vector filters extensions
   * from the given jar file to the specified output directory.
   *
   * @param jarName          the name of the jar file which will be extracted.
   * @param outputDir        the name of the output directory
   *                         where files will be extracted.
   * @param filters          the extensions of the files which will be extracted.
   *
   * @exception IOException  Exception for opening and reading from zip or
   *                         when writing the output files.
   */
  public void extractJar(String jarName, String outputDir, Vector filters) throws IOException {
    if (jarName == null || outputDir == null) {
      return;
    }

    JarFile zip = new JarFile(jarName);
    try {
      File dir = new File(outputDir);

      if (!dir.exists()) {
        dir.mkdirs();
      }

      if (!dir.isDirectory()) {
        return;
      }

      Enumeration enumer = zip.entries();
      String entryName = null;
      String ext = null;
      int index = -1;
      JarEntry theEntry = null;

      while (enumer.hasMoreElements()) {
        ext = null;
        index = -1;
        theEntry = (JarEntry) enumer.nextElement();
        entryName = theEntry.getName();
        index = entryName.lastIndexOf(".");

        if (index > 0) {
          ext = entryName.substring(index);
        }

        if ((entryName != null) && (ext != null) && (filters.contains(ext)) && !theEntry.isDirectory()) {
          extractFile(zip, entryName, outputDir);
        }
      }
    } finally {
      zip.close();
    }
  }

  /**
   * Extracts file with the specified name from the specified jar file
   * to the specified directory.
   *
   * @param zipf             the zip file where the file takes place.
   * @param entryName        the name of the file like a JarEntry in the zip file.
   * @param dir              the output directory.
   *
   * @exception IOException  Exception for opening and reading from zip or
   *                         when writing the output file.
   */
  public void extractFile(JarFile zipf, String entryName, String dir) throws IOException {
    if (zipf == null) {
      throw new IllegalArgumentException("Can not extract zip file, that is null.");
    }

    if (entryName == null) {
      throw new IllegalArgumentException("Can not extract zip entry, that is null.");
    }

    if (dir == null) {
      throw new IllegalArgumentException("Can not extract zip file to directory that is null.");
    }

    String fName = entryName;
    fName = fName.replace('/', File.separatorChar);
    fName = fName.replace('\\', File.separatorChar);
    File f = new File(dir + File.separator + fName);

    if (f.isDirectory()) {
      return;
    }

    File parent = f.getParentFile();
    parent.mkdirs();
    JarEntry entry = (JarEntry) zipf.getEntry(entryName);

    if (entry == null) {
      entry = (JarEntry) zipf.getEntry(entryName.replace('\\', '/'));
    }

    if (entry == null) {
      entry = (JarEntry) zipf.getEntry(entryName.replace('/', '\\'));
    }

    JarEntry tempent = new JarEntry(entry);

    if (tempent == null || tempent.isDirectory()) {
      return;
    }

    InputStream in = new BufferedInputStream(zipf.getInputStream(tempent));
    FileOutputStream fos = new FileOutputStream(f);
    int count = 1024;
    byte[] buff = new byte[count];
    try {
      while (count == 1024) {
        count = in.read(buff);

        if (count > 0) {
          fos.write(buff, 0, count);
        }
      }

      in.close();
      fos.flush();
      fos.close();
    } catch (EOFException ex) {  //$JL-EXC$
      buff = new byte[(int) tempent.getSize()];
      in.read(buff);
      in.close();
      fos.flush();
      fos.close();
    }
  }


  public void extractFile(JarFile zipf, String fName, String dir, String relativeFileName) throws IOException {
    if (zipf == null) {
      throw new IllegalArgumentException("Can not extract zip file, that is null.");
    }

    if (fName == null) {
      throw new IllegalArgumentException("Can not extract zip entry, that is null.");
    }

    if (dir == null) {
      throw new IllegalArgumentException("Can not extract zip file to directory that is null.");
    }

    if (relativeFileName == null) {
      throw new IllegalArgumentException("Can not extract zip file to relative file name that is null.");
    }

    fName = fName.replace('\\', '/');
    String relative = relativeFileName.replace('/', File.separatorChar);
    relative = relativeFileName.replace('\\', File.separatorChar);
    File f = new File(dir + File.separator + relative);
    JarEntry entry = (JarEntry) zipf.getEntry(fName);
    if (entry.isDirectory()) {
      return;
    }
    File parent = f.getParentFile();
    parent.mkdirs();

    InputStream in = new BufferedInputStream(zipf.getInputStream(entry));
    FileOutputStream fos = new FileOutputStream(f);
    int count = 0;
    byte[] buff = new byte[1024];
    try {
      while ((count = in.read(buff)) > 0) {
        fos.write(buff, 0, count);
      }
      in.close();
      fos.flush();
      fos.close();
    } catch (EOFException ex) {//$JL-EXC$
      in.close();
      fos.flush();
      fos.close();
    }
  }
  /**
   * Extracts all files from jar file to output directory, both specified when
   * constructing JarExtractor or by using JarExtractor's set-methods.
   * If jar file or output directory have not been specified
   * jar file will not be extracted.
   *
   * @exception IOException  Exception for opening and reading from zip or
   *                         when writing the output files.
   */
  public void extractJar() throws IOException {
    if (jarFile == null || outputDirectory == null) {
      return;
    }

    JarFile jFile = new JarFile(jarFile);
    try {
      File dir = new File(outputDirectory);

      if (!dir.exists()) {
        dir.mkdirs();
      }

      if (!dir.isDirectory()) {
        return;
      }

      Enumeration enumer = jFile.entries();
      String entryName = null;
      JarEntry theEntry = null;

      while (enumer.hasMoreElements()) {
        theEntry = (JarEntry) enumer.nextElement();
        entryName = theEntry.getName();

        if (entryName != null) {
          extractFile(jFile, entryName, outputDirectory);
        }
      }
    } finally {
      jFile.close();
    }
  }

}