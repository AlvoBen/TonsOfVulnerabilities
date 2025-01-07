package com.sap.engine.core.log.impl1.archive;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.sap.tc.logging.FileLog;
import com.sap.tc.logging.OverwriteLogfileEvent;


class ArchivingQueue {

  private static final String DELIM = ";";

  private Map<String, ArchivingTask> tasks;
  private String tmpDir;
  private Set<String> archivedFileNames;

  
  ArchivingQueue(String tmpDir, String archivedFilesProp) {
    tasks = new HashMap<String, ArchivingTask>();
    this.tmpDir = tmpDir;
    this.archivedFileNames = parseArchivedFileNames(archivedFilesProp);
  }
  
  /*
   * Updates the list of files to be archived 
   */
  void updateArchivedFileNames(String archivedFileNames) {
	  this.archivedFileNames = parseArchivedFileNames(archivedFileNames);
  }
  
  /*
   * Used to set archiving dir when archiving is activated online
   */
  void setTmpDir (String tempDir) {
	  tmpDir = tempDir;
  }
  
  private Set<String> parseArchivedFileNames(String propValue) {
    if(propValue == null || propValue.equals("")) {
      return null;
    }
    String[] names = propValue.split(DELIM);
    return new HashSet<String>(Arrays.asList(names));
  }


  ArchivingTask getTask() {
    ArchivingTask res = null;
    for (String pattern : tasks.keySet()) {
      ArchivingTask task = tasks.get(pattern);
      if (task != null) {
        res = task;
      }
    }
    notifyAll();
    return res;
  }
  
  
  private boolean toBeArchived(String pattern) {
    if(archivedFileNames == null) {
      return true;
    }
    if(archivedFileNames.isEmpty()) {
      return true;
    }
    for(String fileName: archivedFileNames) {
      if(correspond(fileName, pattern)) {
        return true;
      }
    }
    return false;
  }
  
  
  private boolean correspond(String fileName, String pattern) {
    if(pattern.equals(fileName)) {
      return true;
    }
    if(pattern.contains(fileName)) {
      return true;
    }
    if(fileName.contains(pattern)) {
      return true;
    }
    return false;
  }
  
  
  synchronized void addTask(OverwriteLogfileEvent evt) {
    FileLog fileLog = evt.getFileLog();
    String pattern = fileLog.getPattern();
    if(!toBeArchived(pattern)) {
      return;
    }
    Collection<String> oldFileNames = new TreeSet<String>(fileLog.calculateFileNames());
    Collection<String> newFileNames = new TreeSet<String>();
    for(String oldName: oldFileNames) {
      File oldFile = new File(oldName);
      String newName = 
        oldFile.getParentFile().getName() + ArchivingTask.FS_REPLACE + oldFile.getName();
      File newFile = new File(tmpDir, newName);
      oldFile.renameTo(newFile);
      newFileNames.add(newFile.getPath());
    }
    ArchivingTask task = new ArchivingTask(pattern);
    task.addLogFileNames(newFileNames);
    tasks.put(pattern, task);
    notifyAll();
  }
  
  
  synchronized void removeTask(ArchivingTask task) {
    String pattern = task.getPattern();
    tasks.remove(pattern);
  }


  String getTmpDir() {
    return tmpDir;
  }


  synchronized boolean isEmpty() {
    return tasks.isEmpty();
  }

}
