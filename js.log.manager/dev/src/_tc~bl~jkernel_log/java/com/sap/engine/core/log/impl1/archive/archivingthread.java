package com.sap.engine.core.log.impl1.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Created gorka-i Date: Apr 14, 2004 Time: 3:04:53 PM
 */
class ArchivingThread extends Thread {

  private static final Location location = Location.getLocation(ArchivingThread.class.getName(), "j2ee.krn", "BC-JAS-ADM-LOG");
  private static final String THREAD_NAME = "Log Archiving Thread";
  private static final int BUFFER_LENGTH = 10000;

  private ArchivingQueue archivingQueue;
  private String archivingDir;
  private volatile boolean stopped;


  ArchivingThread(ArchivingQueue queue, String archivingDir) {
    super(THREAD_NAME);
    this.archivingQueue = queue;
    this.archivingDir = archivingDir;
    this.stopped = false;
  }


  public void run() {
    while (true) {
      synchronized(archivingQueue) {
        if (archivingQueue.isEmpty()) {
          if (stopped) {
            return;
          }
          try {
            archivingQueue.wait();
          } catch (InterruptedException e) {
            // $JL-EXC$ - proceed
          }
        }
        ArchivingTask task = archivingQueue.getTask();
        try {
          if (task != null) {
            archiveTask(task);
            archivingQueue.removeTask(task);
          }
        } catch (Exception e) {
          location.traceThrowableT(Severity.ERROR, "Archiving problem", e);
        }
      }
    }
  }


  private void archiveTask(ArchivingTask task) {
    location.logT(Severity.DEBUG, " === THREAD ::: starts archiving of task ["
      + task.getZipFileName() + "]");
    location.logT(Severity.DEBUG, " === THREAD ::: task ---> " + task);

    byte[] buffer = new byte[BUFFER_LENGTH];
    String zipFileName = archivingDir + task.getZipFileName();
    ZipOutputStream zout = null;
    FileInputStream in = null;
    try {
      zout = new ZipOutputStream(new FileOutputStream(zipFileName));
      Collection<File> tempFiles = new ArrayList<File>();
      for(String fileName: task.getLogFileNames()) {
        ZipEntry zipEntry = new ZipEntry(fileName);
        File file = new File(fileName);
        tempFiles.add(file);
        zipEntry.setTime(file.lastModified());
        zout.putNextEntry(zipEntry);
        in = new FileInputStream(fileName);
        while (in.available() != 0) {
          int read = in.read(buffer);
          if (read > 0) {
            zout.write(buffer, 0, read);
          }
        }
        in.close();
        zout.flush();
        zout.closeEntry();
      }
      task.finish();
      for(File file: tempFiles) {
        file.delete();
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.ERROR, e.getMessage(), e);
    } finally {
      try {
        if (in != null) in.close();
      } catch (IOException e) {
        location.logT(Severity.WARNING, " Can't close log file " + zipFileName);
      }
      try {
        zout.finish();
      } catch (IOException e) {
        location.logT(Severity.WARNING, " Can't finish zip file " + zipFileName);
      }
      try {
        zout.close();
      } catch (IOException e) {
        location.logT(Severity.WARNING, " Can't close zip file " + zipFileName);
      }
    }
  }


  void cease() {
    this.stopped = true;
  }
  
  /*
   * Used to set archiving dir when archiving is activated online
   */  
  void setArchivingDir(String newArchivingDir) {
	  archivingDir = newArchivingDir;
  }
}
