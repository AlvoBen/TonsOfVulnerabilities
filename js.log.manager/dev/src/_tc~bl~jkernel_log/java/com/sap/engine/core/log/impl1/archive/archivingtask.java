package com.sap.engine.core.log.impl1.archive;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;


class ArchivingTask {
  
  static final String FS = System.getProperty("file.separator");
  static final String DOT = ".";
  static final String DASH = "-";
  static final String TILDE = "~";
  static final String FS_REPLACE = TILDE;
  /* date-time pattern "yyyy.MMM.dd'~'HH.mm.ss" */
  private static final String timePattern = "yyyy.MMM.dd'" + FS_REPLACE + "'HH.mm.ss";
  private static final DateFormat dateFormat = new SimpleDateFormat(timePattern); 
  
  private String zipFileName;
  private String pattern;
  private Collection<String> logFileNames;

  ArchivingTask(String pattern) {
    this.pattern = pattern;
    this.zipFileName = buildZipFileName(pattern);
    this.logFileNames = new TreeSet<String>();
  }
  
  private static String buildZipFileName(String pattern) {
    File file = new File(pattern);
    StringBuilder fileName = new StringBuilder();
    fileName.append(file.getParentFile().getName());
    fileName.append(FS_REPLACE);
    String name = file.getName();
    fileName.append(name.substring(0, name.lastIndexOf(DOT)));
    fileName.append(FS_REPLACE);
    fileName.append(dateFormat.format(new Date()));
    fileName.append(".zip");
    return fileName.toString();
  }
  
  void addLogFileName(String fileName) {
    logFileNames.add(fileName);
  }

  void addLogFileNames(Collection<String> fileNames) {
    logFileNames.addAll(fileNames);
  }

  String getPattern() {
    return pattern;
  }

  public String toString() {
    return pattern;
  }

  synchronized void finish() {
    notifyAll();
  }
  
  Collection<String> getLogFileNames() {
    return logFileNames;
  }
  
  String getZipFileName() {
    return zipFileName;
  }

}