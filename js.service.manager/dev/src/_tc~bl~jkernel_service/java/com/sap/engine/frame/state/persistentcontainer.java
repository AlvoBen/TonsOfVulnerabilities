package com.sap.engine.frame.state;

import com.sap.engine.frame.ServiceException;

import java.io.InputStream;
import java.io.File;

/**
 * General interface to work with component persistent folder.
 *
 * @author Dimitar Kostadinov
 */
public interface PersistentContainer {

  /**
   * Gets InputStream from file in component persistent folder.
   *
   * @param name    - file entry name.
   * @param global  - if true search in common configuration for all cluster nodes
   *                  else search in current cluster node specific configuration.
   * @return        - file entry input stream /if global = false and local file entry
   *                  doesn't exist it tries to return global file entry stream/
   *                  or null if file doesn't exist.
   * @throws ServiceException if an error occurs.
   */
  public InputStream getPersistentEntryStream(String name, boolean global) throws ServiceException;

  /**
   * Gets File from file in component persistent folder. The file must be deleted after usage.
   *
   * @param name    - file entry name.
   * @param global  - if true search in common configuration for all cluster nodes
   *                  else search in current cluster node specific configuration.
   * @return        - file entry  /if global = false and local file entry
   *                  doesn't exist it tries to return global file entry/
   *                  or null if file doesn't exist - delete the file after usage.
   * @throws ServiceException if an error occurs.
   * @deprecated    - use getPersistentEntryStream instead 
   */
  public File getPersistentEntryFile(String name, boolean global) throws ServiceException;

  /**
   * Stores or updates file entry as stream in component persistent folder.
   *
   * @param name    - file entry name.
   * @param stream  - input stream to be stored.
   * 
   * @param global  - if true stores the input stream in common configuration for all cluster nodes
   *                  else stores it in current cluster node specific configuration.
   * @throws ServiceException if an error occurs.
   */
  public void setPersistentEntryStream(String name, InputStream stream, boolean global) throws ServiceException;

  /**
   * Stores or updates file entry in component persistent folder.
   *
   * @param name    - file entry name.
   * @param file    - file to be stored.
   * @param global  - if true stores the file in common configuration for all cluster nodes
   *                  else stores it in current cluster node specific configuration.
   * @throws ServiceException if an error occurs.
   */
  public void setPersistentEntryFile(String name, File file, boolean global) throws ServiceException;

  /**
   * Removes file entry form component persistent folder.
   *
   * @param name    - file entry name.
   * @param global  - if true removes the file entry from common configuration for all cluster nodes
   *                  else removes it from current cluster node specific configuration.
   * @throws ServiceException if an error occurs.
   */
  public void removePersistentEntry(String name, boolean global) throws ServiceException;

  /**
   * List file entry names in component persistent folder.
   *
   * @param global  - if true lists the file entry names from common configuration for all cluster nodes
   *                  else lists them from specific configuration for current cluster node.
   * @return         - string array contains all file entry names including subfolders
   * @throws ServiceException if an error occurs.
   */
  public String[] listPersistentEntryNames(boolean global) throws ServiceException;

  /**
   * Sets a migration version information for the particular component. This method is deprecated
   * because has to be used only by the engine inhouse upgrade procedure.
   * 
   * @param version version to be set. 
   * @throws ServiceException thrown in case the value can't be stored in the database. 
   * @deprecated
   */
  public void setMigrationVersion(int version) throws ServiceException;

  /**
   * Gets the migration version for the particular component. This method is deprecated because
   * has to be used only by the engine inhouse upgrade procedure. 
   * 
   * @return version for the particular component - if not set - 0 will be returned.
   * @deprecated
   */  
  public int getMigrationVersion();

  /**
   * Stores or updates secured file entry as stream in component persistent folder.
   *
   * @param name    - file entry name.
   * @param stream  - input stream to be stored.
   *
   * @param global  - if true stores the input stream in common configuration for all cluster nodes
   *                  else stores it in current cluster node specific configuration.
   * @throws ServiceException if an error occurs.
   */
  public void setSecuredPersistentEntryStream(String name, InputStream stream, boolean global) throws ServiceException;

  /**
   * Stores or updates secured file entry in component persistent folder.
   *
   * @param name    - file entry name.
   * @param file    - file to be stored.
   * @param global  - if true stores the file in common configuration for all cluster nodes
   *                  else stores it in current cluster node specific configuration.
   * @throws ServiceException if an error occurs.
   */
  public void setSecuredPersistentEntryFile(String name, File file, boolean global) throws ServiceException;

}
