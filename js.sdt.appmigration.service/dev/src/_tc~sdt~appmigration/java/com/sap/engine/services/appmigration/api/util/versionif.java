/**
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.appmigration.api.util;

import java.io.Serializable;

import com.sap.engine.services.appmigration.api.exception.VersionException;

/**
 * This class is used to build a version object.
 * It contain information about the version name,
 * version number and patch number
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public interface VersionIF extends Serializable {

  /**
   * Returns the name of the component with this version.
   * @return the component name
   */
  public String getName();

  /**
   * Returns the number associated with this version object
   * (e.g. 7.00, 6.40 etc).
   * @return the release number as String
   */
  public String getRelease();

  /**
   * Returns the number associated with this patch
   * @return Patch number
   */
  public String getPatchNumber();

  /**
   * Returns the SP level
   * @return SP level
   */
  public String getSPLevel();
  
  
  /**
   * Gets the name of the vendor (e.g. "sap.com")
   * @return the name of the vendor for the component
   */
  public String getVendor();
  
  /**
   * The component location. For the classic(old) format in BC_COMPVERS
   * table it is rather unspecific, something like "SAP AG", for the 
   * new format(the CMS-format) it is from three pieces
   * @return
   */
  //public String getCompLocation();
  
  /**
   * Return the counter of the component in the BC_COMPVERS
   * table. The counter has two formats. The classical format,
   * which is the old format is as follows:
   * 1000.<Release>.<SupportPackageLevel>.<PatchLevel>.<TimeStamp>
   * <SuppportPackageLevel> and <PatchLevel> are entire numbers >=0
   * The <TimeStamp> is in format <yyyymmddhhmmss>
   * In the new format (CMS format) the counter is just a timestamp
   * in format <yyyymmddhhmmss>
   * The release, support package level and the patch level are in 
   * additional fields
   * 
   * @return the component counter
   */
  public String getCounter();
  
  
  /**
   * Indicates that another version is equal to this one.
   * @param otherVersion The version which is compared to this one.
   * @return True if the versions are equal.
   */
  public boolean equals(Object otherVersion);
  
  /**
   * Compares this version to another one.
   * @param otherVersion The version which is compared to this one.
   * @return 0 if versions are equal, less than 0(<0) if other version is greater 
   * and more than 0(>0) if
   * this version is greater.
   * @exception VersionException Thrown when the two versions are
   * not of a same type or can not be compared.
   */
  public int compareTo(VersionIF otherVersion)
      throws VersionException;


  /**
   * Returns the String representation of this object 
   * @return String representation of this object
   */ 
  public String toString(); 

}
