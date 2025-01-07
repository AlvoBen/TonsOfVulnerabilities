package com.sap.engine.tools.offlinedeploy.rdb;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.jar.*;

/**
 * Class for extracting the content of the bootstrap SDA on the filesystem in transactional way
 *
 * @author Hristo Spaschev Iliev
 * @version 7.1
 */
public class BootstrapExtractor {

  /**
   * Size of the transfer portions for copy file
   * <p/>
   * Default value: 64MB default space for NIO divided by the number of the default application threads
   */
  private static final int TRANSFER_SIZE = (64 / 40) * 1024 * 1024; //MBs represented in bytes

  /**
   * Name of the directory where the bootstrap should be extracted
   */
  private static final String FOLDER_NAME = "bootstrap";

  /**
   * Name of the file containing list will all files that has to be copied
   */
  private static final String BOOTSTRAP_COPY_LIST = "bootstrap.lst";

  /**
   * File that contains list with files that need permissions change
   */
  private static final String BOOTSTRAP_PERMISSIONS_LIST = "permissions.lst";

  /**
   * Extracts the content of the bootstrap SDA to a specified location
   * <p/>
   * The extraction is done in transactional way - if one or more operations fail, then the original content will be
   * restored.
   *
   * @param bootstrapSDALocation Path to the bootstrap SDA
   * @param globalDirLocation    Location of the SYS/global directory. The directory should be specified without
   *                             "bootstrap" in the end - for instance: D:\\usr\\sap\\D70\\SYS\\global. The location can
   *                             be obtained from the Java Startup Framework by invoking <code>JStartupFramework.getParam("DIR_GLOBAL")</code>,
   *                             or can be obtained from the ConfigurationManager's ConfigurationHandlerFactory by
   *                             invoking <code>getSystemProfile().getProperty("SYS_GLOBAL_DIR")</code>
   * @param clusterDirLocation   Location of the cluster directory. The directory should be specified without "bootstrap
   *                             " in the end - for instnace D:\\usr\\sap\\D70\\j2ee\\cluster. The location can be
   *                             obtained from the Java Startup Framework by invoking <code>JStartupFramework.getParam("jstartup/DIR_CLUSTER")</code>
   *                             or can be obtained from the ConfigurationManager's ConfigurationHandlerFactory by
   *                             invoking <code>getSystemProfile().getProperty("INSTANCE_DIR")</code> and appending
   *                             "j2ee/cluster"
   * @throws IOException Thrown if there is a problem with the extraction or the input parameters are not valid
   */
  public static void extractBootstrapModule(String bootstrapSDALocation, String globalDirLocation, String clusterDirLocation) throws IOException {
    //{{ Check the input parameters
    File inputFile = new File(bootstrapSDALocation);
    if (!inputFile.exists() && !inputFile.isFile()) {
      throw new FileNotFoundException("The archive with the bootstrap module [" +
                                      bootstrapSDALocation + "] cannot be found");
    }
    // Global directory
    File globalDirDirectory = new File(globalDirLocation, FOLDER_NAME);
    if (!createDirectory(globalDirDirectory)) {
      throw new IOException("Cannot create the path [" + globalDirDirectory.getCanonicalPath() + "]");
    }
    //}}

    // Cluster directory
    File clusterDirectory = new File(clusterDirLocation, FOLDER_NAME);
    if (!createDirectory(clusterDirectory)) {
      throw new IOException("Cannot create the path [" + clusterDirectory.getCanonicalPath() + "]");
    }
    //}}

    //{{ Read the list of files that has to be extracted
    JarFile jar = new JarFile(inputFile);
    JarEntry bootstrapList = jar.getJarEntry(BOOTSTRAP_COPY_LIST);
    if (bootstrapList == null) {
      throw new IOException("Cannot find the entry [" + BOOTSTRAP_COPY_LIST + "] inside the [" +
                            inputFile.getCanonicalPath() + "]. The bootstrap module is too old");
    }
    String line;
    // construct the list with files that has to be copied and add the list itself
    Set<String> filesToProcess = new HashSet<String>();
    filesToProcess.add(BOOTSTRAP_COPY_LIST);
    LineNumberReader lnr = new LineNumberReader(new InputStreamReader(jar.getInputStream(bootstrapList)));
    try {
      while ((line = lnr.readLine()) != null) {
        filesToProcess.add(line);
      }
    } catch (IOException ioe) {
      jar.close();
      throw ioe;
    } finally {
      lnr.close();
    }
    //}}

    //{{ Read the list of files that need permissions change
    jar = new JarFile(inputFile);
    JarEntry permissionsList = jar.getJarEntry(BOOTSTRAP_PERMISSIONS_LIST);
    if (permissionsList == null) {
      throw new IOException("Cannot find the entry [" + BOOTSTRAP_PERMISSIONS_LIST + "] inside the [" +
                            inputFile.getCanonicalPath() + "]. The bootstrap module is too old");
    }
    Map<String, String> permissions = new HashMap<String, String>();
    lnr = new LineNumberReader(new InputStreamReader(jar.getInputStream(permissionsList)));
    int separator;
    try {
      while ((line = lnr.readLine()) != null) {
        separator = line.indexOf("=");
        if (separator != -1) {
          permissions.put(line.substring(0, separator).trim(), line.substring(separator + 1, line.length()).trim());
        }
      }
    } catch (IOException ioe) {
      jar.close();
      throw ioe;
    } finally {
      lnr.close();
    }
    //}}

    //{{ Extract the content of the bootstrap SDA in the extract directory
    Enumeration<JarEntry> jarEntries = jar.entries();
    JarEntry entry;
    InputStream is;
    File f;
    String entryName;
    // Extraction directory (needed to guarantee that the original content will be untouched in case of a problem)
    File extractDirectory = new File(globalDirLocation, FOLDER_NAME + System.currentTimeMillis());
    if (!createDirectory(extractDirectory)) {
      throw new IOException("Cannot create the directory [" + extractDirectory.getCanonicalPath() + "]");
    }
    // Extract the content
    try {
      while (jarEntries.hasMoreElements()) {
        entry = jarEntries.nextElement();
        entryName = entry.getName().replace('\'', '/');
        f = new File(extractDirectory, entryName);
        // Check if the file is in the boostrap.lst - as file name or as part of a directory included in the list
        if (filesToProcess.contains(f.getName()) || filesToProcess.contains(getParent(entryName))) {
          if (entry.isDirectory()) {
            if (!createDirectory(f)) {
              throw new IOException("Cannot create the path [" + f.getCanonicalPath() + "]");
            }
          } else {
            is = jar.getInputStream(entry);
            writeToFile(is, f);
            is.close();
          }
        }
      }
    } catch (IOException ioe) {
      jar.close();
      deleteDirectory(extractDirectory);
      throw ioe;
    }
    //}}

    //{{ Exchange the updated and old content
    File backupDirectory = new File(globalDirDirectory + "_" + System.currentTimeMillis());
    if (globalDirDirectory.renameTo(backupDirectory)) {
      deleteDirectory(globalDirDirectory);

      // Try to exchange the extract and target directories
      if (!extractDirectory.renameTo(globalDirDirectory)) {
        // if not possible restore the target directory content
        backupDirectory.renameTo(globalDirDirectory);
        throw new IOException("Cannot rename the directory [" + globalDirDirectory.getCanonicalPath() + "] to [" + backupDirectory.getCanonicalPath() + "]");
      } else {
        // everything went fine - we do not need backup directory anymore
        deleteDirectory(backupDirectory);
      }
    } else {
      throw new IOException("Cannot rename the directory  [" + globalDirDirectory.getCanonicalPath() + "] to [" + backupDirectory.getCanonicalPath() + "]");
    }
    //}}

    //{{ Copy the new content to cluster folder
    copyDirectory(globalDirDirectory, clusterDirectory);
    //}}

    //{{ Set permissions
    setPermissions(globalDirDirectory, permissions);
    setPermissions(clusterDirectory, permissions);
    //}}
  }

  /**
   * Sets permissions to the specified files
   *
   * @param directory   Folder in which files are located
   * @param permissions Permissions structure that contains mapping between filename and permisssions string
   */
  private static void setPermissions(File directory, Map<String, String> permissions) {
    for (String file : permissions.keySet()) {
      try {
        chmod(new File(directory, file), permissions.get(file));
      } catch (IOException e) {
        // $JL-EXC$
      } catch (InterruptedException e) {
        // $JL-EXC$
      }
    }
  }

  /**
   * Returns the top-most parent directory of the file
   *
   * @param entryName Jar entry name (META-INF/123)
   * @return The top-most parent (META-INF)
   */
  private static String getParent(String entryName) {
    int index = entryName.indexOf("/");
    if (index != -1) {
      return entryName.substring(0, index);
    } else {
      return entryName;
    }
  }

  /**
   * Transfers information from source stream to a target channel notifying the provided watchdog for progress.
   *
   * @param in  Source input stream
   * @param out Target file to write to
   * @throws IOException Thrown if there is a problem during the transfer
   */
  private static void writeToFile(InputStream in, File out) throws IOException {
    FileChannel channel = new FileOutputStream(out).getChannel();

    try {
      byte[] buf = new byte[TRANSFER_SIZE];
      ByteBuffer buffer = ByteBuffer.allocateDirect(buf.length);
      int received;
      while ((received = in.read(buf)) != -1) {
        buffer.clear();
        buffer.put(buf, 0, received);
        buffer.flip();
        channel.write(buffer);
      }
    } finally {
      channel.close();
    }
  }

  /**
   * Deletes the specified directory and all its contents including all sub-directories.
   *
   * @param tempDir the directory that is being deleted.
   * @return TRUE if the directory was deleted successfully
   */
  private static boolean deleteDirectory(File tempDir) {
    boolean status = true;
    if (tempDir != null) {
      File[] files = tempDir.listFiles();

      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            status = deleteDirectory(file) && status;
          } else {
            status = deleteFile(file) && status;
          }
        }
      }
      status = deleteFile(tempDir) && status;
    } else {
      status = false;
    }
    return status;
  }

  /**
   * Deletes a file and checks if it was deleted successfully
   *
   * @param f File that has to be deleted
   * @return TRUE if the file was successfully deleted
   */
  private static boolean deleteFile(File f) {
    boolean result = f.delete();
    if (result) {
      if (f.exists()) {
        return false;
      }
    }
    return result;
  }

  /**
   * Creates a directory
   *
   * @param f Directory to create
   * @return TRUE if the directory was created successfully or if it already exists
   */
  private static boolean createDirectory(File f) {
    return f.exists() || f.mkdirs();
  }

  /**
   * Changes the permissions of a file
   *
   * @param file File that needs permissions change
   * @param mode String with the permissions
   * @throws IOException          Thrown if the chmod was not successful
   * @throws InterruptedException Thrown if the chmod process was terminated
   */
  private static void chmod(File file, String mode) throws IOException, InterruptedException {
    // Check if this is WINDOWS
    if (File.separatorChar == '\\') {
      return;
    }

    new ProcessBuilder("chmod", mode, file.getCanonicalPath()).start();
  }

  /**
   * Copies all files from source directory to destination directory.
   *
   * @param source source directory
   * @param dest   destination directory
   * @throws IOException Thrown if there is an error during the poperation
   */
  public static void copyDirectory(File source, File dest) throws IOException {
    File[] files = source.listFiles();
    File tempFile;

    if (files != null) {
      for (File file : files) {
        tempFile = new File(dest, file.getName());

        if (file.isFile()) {
          copyFile(file, tempFile);
        } else {
          tempFile.mkdirs();
          copyDirectory(file, tempFile);
        }
      }
    }
  }

  /**
   * Copies file using NIO
   *
   * @param src  Source file to copy from
   * @param dest Destination file to copy to
   * @throws IOException Thrown if problem with the copy occurs
   */
  public static void copyFile(File src, File dest) throws IOException {
    FileChannel fis = null;
    FileChannel fos = null;
    long startTime = System.currentTimeMillis();

    if (src == null || dest == null || !src.exists()) {
      throw new IllegalArgumentException("Copy operation is not possible!");
    }

    if (!dest.getParentFile().exists()) {
      dest.getParentFile().mkdirs();
    }

    try {
      fis = new FileInputStream(src).getChannel();
      fos = new FileOutputStream(dest).getChannel();
      long size = fis.size();
      long count;
      try {
        count = fos.transferFrom(fis, 0, size);
        if (count != size) {
          throw new IOException("Can not copy the file [" + src.getAbsolutePath() + "] to [" + dest.getAbsolutePath() +
                                "]. Transfered [" + count + "] from [" + size + "] bytes.");
        }
      } catch (IOException ioe) {
        count = 0;
        long step = (TRANSFER_SIZE > size) ? size : TRANSFER_SIZE;
        while (count < size) {
          count += fos.transferFrom(fis, count, step);
          if (TRANSFER_SIZE > size - count) {
            step = size - count;
          }
        }
      }
    } finally {
      try {
        fis.close();
      } catch (Exception e) {
        fis = null;
      }
      try {
        fos.close();
      } catch (Exception e) {
        fos = null;
      }
    }
  }

  public static void main(String[] args) throws IOException {
    BootstrapExtractor.extractBootstrapModule(args[0], args[1], args[2]);
  }
}