package com.sap.engine.core.log.impl1.archive;

import java.io.File;
import java.util.Properties;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.OverwriteLogfileEvent;
import com.sap.tc.logging.OverwriteLogfileListener;
import com.sap.tc.logging.Severity;

/**
 * Created gorka-i Date: Apr 14, 2004 Time: 2:54:22 PM
 */
public class ArchivingManager implements OverwriteLogfileListener {

  private static final Location location = Location.getLocation(ArchivingManager.class.getName(), "j2ee.krn", "BC-JAS-ADM-LOG");
  private static final String FS = System.getProperty("file.separator");
  private static final String PROP_ARCHIVE_OLD_FILES = "ArchiveOldLogFiles";
  private static final String PROP_ARCHIVES_DIR = "ArchivesDirectory";
  private static final String PROP_ARCHIVED_FILES = "ArchivedFileNames";
  private static final String DEFAULT_ARCHIVES_DIR = // "./log/archive";
  "." + FS + "log" + FS + "archive";

  private static ArchivingThread archivingThread;

  private boolean isEnabled;
  private ArchivingQueue archivingQueue;


  public ArchivingManager(Properties properties) {
    String dirName = properties.getProperty(PROP_ARCHIVES_DIR, DEFAULT_ARCHIVES_DIR);
    new File(dirName).mkdirs();
    if (!dirName.endsWith(FS)) {
      dirName += FS;
    }

    String tmpDir = dirName + "tmp";
    File tmpDirectory = new File(tmpDir);
    tmpDirectory.mkdirs();
//    if (!tmpDir.endsWith(FS)) {
//      tmpDir += FS;
//    }
    String enabledStr = properties.getProperty(PROP_ARCHIVE_OLD_FILES, "ON");

    isEnabled = enabledStr.equalsIgnoreCase("ON") || enabledStr.equalsIgnoreCase("YES")
      || enabledStr.equalsIgnoreCase("TRUE");

    String propValue = properties.getProperty(PROP_ARCHIVED_FILES);
    archivingQueue = new ArchivingQueue(tmpDir, propValue);
    archivingThread = new ArchivingThread(archivingQueue, dirName);
  }
  
  /*
   * Used to enable/disable archiving online
   */  
  public void setProperties(Properties properties) {
	  //enable or disable archiving according to the value of property "ArchiveOldLogFiles"
	  String enabledStr = properties.getProperty(PROP_ARCHIVE_OLD_FILES, "ON");
	  boolean toBeEnabled = enabledStr.equalsIgnoreCase("ON") || enabledStr.equalsIgnoreCase("YES")
	      || enabledStr.equalsIgnoreCase("TRUE");
	  
	  if (!toBeEnabled) {
		  //disable archiving
		  isEnabled = false;
		  return;
	  }
	  
	  //create archiving and temporary directories
	  String dirName = properties.getProperty(PROP_ARCHIVES_DIR, DEFAULT_ARCHIVES_DIR);
	  new File(dirName).mkdirs();
	  if (!dirName.endsWith(FS)) {
	    dirName += FS;
	  }

	  String tmpDir = dirName + "tmp";
	  File tmpDirectory = new File(tmpDir);
	  tmpDirectory.mkdirs();
	  
	  //set archiving and temporary directories
	  archivingThread.setArchivingDir(dirName);
	  archivingQueue.setTmpDir(tmpDir);
	  
	  //get ArchivedFileNames property
	  String archivedFileNames = properties.getProperty(PROP_ARCHIVED_FILES);
	  archivingQueue.updateArchivedFileNames(archivedFileNames);
	  
	  //enable archiving
	  isEnabled = toBeEnabled;	  
  }
  
  public void handleEvent(OverwriteLogfileEvent evt) {
    if (!isEnabled) {
      return;
    }
    if (evt.getFileLog().getFormatter().getFormatterName().indexOf("ShortDump") != -1) {
      return;
    }
    try {
      int fileNum = evt.getFileLog().getIndex();
      if (fileNum == 0) {
        archivingQueue.addTask(evt);
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.ERROR,
        "ArchivingManager can not handle an overwrite event", e);
    }
  }


  public void startArchiving() {
    archivingThread.start();
  }


  public void stopArchiving() {
    archivingThread.cease();
    archivingThread = null;
  }
}
