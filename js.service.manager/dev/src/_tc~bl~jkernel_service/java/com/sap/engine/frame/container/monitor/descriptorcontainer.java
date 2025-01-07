package com.sap.engine.frame.container.monitor;

import com.sap.engine.frame.ServiceException;

import java.io.InputStream;
import java.io.File;

/**
 * General interface to work with component descriptors folder.
 *
 * @author Dimitar Kostadinov
 */
public interface DescriptorContainer {

  /**
   * Gets InputStream from file in component descriptors folder.
   *
   * @param name    - file entry name.
   * @param global  - if true search in common configuration for all cluster nodes
   *                  else search in current cluster node specific configuration.
   * @return        - file entry input stream /if global = false and local file entry
   *                  doesn't exist it tries to return global file entry stream/
   *                  or null if file doesn't exist or error occurs.
   */
  public InputStream getPersistentEntryStream(String name, boolean global);

  /**
   * Gets File from file in component descriptors folder. The file must be deleted after usage.
   *
   * @param name    - file entry name.
   * @param global  - if true search in common configuration for all cluster nodes
   *                  else search in current cluster node specific configuration.
   * @return        - file entry  /if global = false and local file entry
   *                  doesn't exist it tries to return global file entry/
   *                  or null if file doesn't exist - delete the file after usage.
   * @deprecated    - use getPersistentEntryStream instead
   */
  public File getPersistentEntryFile(String name, boolean global);

  /**
   * Stores or updates file entry as stream in component descriptors folder.
   *
   * @param name    - file entry name.
   * @param stream  - input stream to be stored.
   *
   * @param global  - if true stores the input stream in common configuration for all cluster nodes
   *                  else stores it in current cluster node specific configuration.
   */
  public void setPersistentEntryStream(String name, InputStream stream, boolean global);

  /**
   * Stores or updates file entry in component descriptors folder.
   *
   * @param name    - file entry name.
   * @param file    - file to be stored.
   * @param global  - if true stores the file in common configuration for all cluster nodes
   *                  else stores it in current cluster node specific configuration.
   */
  public void setPersistentEntryFile(String name, File file, boolean global);

  /**
   * Removes file entry form component descriptors folder.
   *
   * @param name    - file entry name.
   * @param global  - if true removes the file entry from common configuration for all cluster nodes
   *                  else removes it from current cluster node specific configuration.
   * @throws ServiceException if an error occurs.
   */
  public void removePersistentEntry(String name, boolean global) throws ServiceException;

  /**
   * Gets InputStream from file in component descriptors folder.
   *
   * @param name    - file entry name.
   * @param global  - if true search in common configuration for all cluster nodes
   *                  else search in current cluster node specific configuration.
   * @return        - file entry input stream /if global = false and local file entry
   *                  doesn't exist it tries to return global file entry stream/
   *                  or null if file doesn't exist or error occurs.
   * @deprecated - use getPersistentEntryStream instead
   */
  public InputStream getSecuredPersistentEntryStream(String name, boolean global);

  /**
   * Gets File from file in component descriptors folder. The file must be deleted after usage.
   *
   * @param name    - file entry name.
   * @param global  - if true search in common configuration for all cluster nodes
   *                  else search in current cluster node specific configuration.
   * @return        - file entry  /if global = false and local file entry
   *                  doesn't exist it tries to return global file entry/
   *                  or null if file doesn't exist - delete the file after usage.
   * @deprecated    - use getPersistentEntryStream instead
   */
  public File getSecuredPersistentEntryFile(String name, boolean global);

  /**
   * Stores or updates secured file entry as stream in component descriptors folder.
   *
   * @param name    - file entry name.
   * @param stream  - input stream to be stored.
   *
   * @param global  - if true stores the input stream in common configuration for all cluster nodes
   *                  else stores it in current cluster node specific configuration.
   */
  public void setSecuredPersistentEntryStream(String name, InputStream stream, boolean global);

  /**
   * Stores or updates secured file entry in component descriptors folder.
   *
   * @param name    - file entry name.
   * @param file    - file to be stored.
   * @param global  - if true stores the file in common configuration for all cluster nodes
   *                  else stores it in current cluster node specific configuration.
   */
  public void setSecuredPersistentEntryFile(String name, File file, boolean global);

  /**
   * Removes file entry form component descriptors folder.
   *
   * @param name    - file entry name.
   * @param global  - if true removes the file entry from common configuration for all cluster nodes
   *                  else removes it from current cluster node specific configuration.
   * @throws ServiceException if an error ocurr.
   * @deprecated use removePersistentEntry instead
   */
  public void removeSecuredPersistentEntry(String name, boolean global) throws ServiceException;

}
