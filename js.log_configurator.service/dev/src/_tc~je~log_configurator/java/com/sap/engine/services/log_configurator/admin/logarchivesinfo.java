package com.sap.engine.services.log_configurator.admin;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;

import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.core.monitor.CoreMonitor;

/**
 * Created gorka-i Date: Apr 14, 2004 Time: 2:54:22 PM
 */
public class LogArchivesInfo {
  private static final String FS = System.getProperty("file.separator");
  private static final String PROPERTY_DIR_NAME = "ArchivesDirectory";
  private static final String FS_REPLACE = "~";
  private static final String DEFAULT_ARCHIVES_DIR = // "./log/archive";
    "." + FS + "log" + FS + "archive"; 

  private String dirName;


  private static class ArchivesFilter implements FilenameFilter {
    private String pattern;

    ArchivesFilter(String pattern) {
      this.pattern = pattern;
    }

    public boolean accept(File dir, String name) {
      return name.startsWith(pattern) && name.endsWith(".zip");
    }
  }
  
  
  public LogArchivesInfo(ServiceContext sc) {
    CoreMonitor mon = sc.getCoreContext().getCoreMonitor();
    Properties props = mon.getManagerProperties(LogConfigurator.LOG_MANAGER_NAME);
    dirName = props.getProperty(PROPERTY_DIR_NAME, DEFAULT_ARCHIVES_DIR);
    if(!dirName.endsWith(FS)) {
      dirName += FS;
    }
  }

  // the method is used from LogViewer in online case (i.e. when server is running)
  public String[] getArchiveFileNames(String pattern) {
    return getArchiveFileNames(dirName, pattern);
  }

  // the method is used from LogConfigurator monitor
  public long getArchivesSize() {
    long size = 0;
    File[] files = new File(dirName).listFiles();
    for(int i = files.length; --i >= 0;) {
      File currFile = files[i];
      if(currFile.getName().endsWith(".zip")) {
        size += currFile.length();
      }
    }
    // size is in bytes - convert into KB
    return (size / 1024);
  }

  // the method is used from LogViewer in online case (i.e. when server is running)
  public String getArchiveLogDirectory() {
    return new File(dirName).getAbsolutePath();
  }

  // the method is used from LogViewer in offline case (i.e. when server is not running)
  public static String[] getArchiveFileNames(String archiveLogDirectory, String pattern) {
    File file = new File(pattern);
    String parentName = file.getParentFile().getName();
    String fileName = file.getName();
    String firstPart = fileName.substring(0, fileName.indexOf("."));
    String encodedPattern = parentName + FS_REPLACE + firstPart + FS_REPLACE;
    
    String[] allArchives = 
      new File(archiveLogDirectory).list(new ArchivesFilter((encodedPattern)));

    if(allArchives != null) {
      for(int i = 0; i < allArchives.length; i++) {
        allArchives[i] = archiveLogDirectory + FS + allArchives[i];
      }
    }
    return allArchives;
  }
}
