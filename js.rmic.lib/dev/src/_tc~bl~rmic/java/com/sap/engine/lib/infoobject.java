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

/**
 * Object for representng the mapping of files and their entry names for making jar file.
 *
 *
 * @author Rossitca Andreeva
 * @version 4.0
 * Written 20 of May, 2000
 */
public class InfoObject {

  /**
   * The name of the file like it will be included in the jar file.
   * It is called entry for the jar.
   */
  public String entryName = "";
  /**
   * The real path of the file. If the path is .jar, .zip an entry with the same name
   * will be retrieved form this archive file and included in the output jar.
   */
  public String filePath = "";

  /**
   * Constructs empty info object.
   */
  public InfoObject() {

  }

  /**
   * Constructs info object for the specified file name.
   *
   * @param   fileName  the name of the file like it will be included in the jar.
   */
  public InfoObject(String fileName) {
    this.entryName = fileName;
  }

  /**
   * Constructs info object for the specified file name and path.
   *
   * @param   fileName  the name of file like it will be included in the jar.
   * @param   filePath  the path of the file.
   */
  public InfoObject(String fileName, String filePath) {
    this.entryName = fileName;
    this.filePath = filePath;
  }

  /**
   * Sets the real file path when found.
   *
   * @param   path  the path of the file entry.
   */
  public void setFilePath(String path) {
    filePath = path;
  }

  /**
   * Returns file path of entry.
   *
   * @return  the real location of file.
   */
  public String getFilePath() {
    return this.filePath;
  }

  /**
   * Gets the name of entry stored in this object.
   *
   * @return  the entry name.
   */
  public String getEntryName() {
    return this.entryName;
  }

  /**
   * Sets the real entry name for the corresponding file.
   *
   * @param   entry  the entry name for this file.
   */
  public void setEntryName(String entry) {
    entryName = entry;
  }

  /**
   * Equals of object InfoObject equals is only for entry name field of InfoObject.
   *
   * @param info   Object for comaprison
   *
   * @return  boolean true if objects are equals, false - otherwise
   */
  public boolean equals(Object info) {
    if (info == null) {
      return false;
    }

    if (!(info instanceof InfoObject)) {
      return false;
    }

    if (this.entryName != null) {
      if (((InfoObject) info).getEntryName() != null) {
        if (!this.entryName.equals(((InfoObject) info).getEntryName())) {
          return false;
        }
      } else {
        return false;
      }
    } else if (((InfoObject) info).getEntryName() != null) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Returns a string representation of this object.
   *
   * @return the output string.
   */
  public String toString() {
    return entryName;
  }

  public Object clone() {
    InfoObject ttmp = new InfoObject(entryName, filePath);
    return ttmp;
  }

}
