/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.licensing;

import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import com.sap.engine.frame.core.licensing.LicenseKeyValue;
import com.sap.engine.frame.core.licensing.LicensingContext;
import com.sap.engine.frame.state.ManagementInterface;

/**
 * Interface for the licensing service.
 *
 * @author Jochen Mueller
 * @version 1.1
 */
public interface LicensingRuntimeInterface extends ManagementInterface {
  /**
   * The name, under wich the TableLocking is bound in the jndi.
   */
  public static final String JNDI_NAME = "Licensing";

  public static final String DEVELOPMENT_SYSTEM = LicensingContext.DEVELOPMENT_SYSTEM;
  public static final String TRAINING_SYSTEM = LicensingContext.TRAINING_SYSTEM;
  public static final String TEST_SYSTEM = LicensingContext.TEST_SYSTEM;
  public static final String PRODUCTIVE_SYSTEM = LicensingContext.PRODUCTIVE_SYSTEM;

  //LicenseKey attribute in properties objet
  public static final String SYSTEM_ID = "systemid";
  public static final String HW_KEY = "hardwarekey";
  public static final String SW_PRODUCT = "swproduct";
  public static final String SW_PRODUCT_LIMIT = "swproductlimit";
  public static final String BEGIN_DATE = "begindate";
  public static final String END_DATE = "enddate";
  public static final String TYPE = "type";
  public static final String INST_NO = "instno";
  public static final String SYST_NO = "systno";
  public static final String VALIDITY = "validity";

  /**
   * Returns the basis release of the system.
   * @return String basis release
   */
  String getBasisRelease();

  /**
   * Gets the hardware ID from the server where the message server is running.
   * @return String hardware id of the message server system.
   */
  String getHardwareId();

  /**
   * Gets the system id from the server where the message server is running.
   * @return String system id of the message server.
   */
  String getSystemId();

  /**
   * Returns the software products that are and/or were installed on the engine.
   * @return Vector Vector with objects of class <code>String</code> as elements
   */
  Vector getSwProducts();

  /**
   * Returns all installed licenses.
   * @return Vector contains object of class <code>LicenseKey</code>
   */
  Vector getAllLicenses();

  /**
   * Returns the date by which the license is valid.
   * @param swProduct software product
   * @param hwKey hardware key
   * @return Date validity date
   */
  Date getValidityDate(String swProduct, String hwKey);

  /**
   * Returns the installation number of the system.
   * @return String installation number
   */
  String getInstNo();

  /**
   * Returns the system number of the system.
   * @return String installation number
   */
  String getSysNo();

  /**
   * Returns information about the installed licenses.
   * @return Vector vector with objects of class <code>LicenseKeyValue</code> as elements
   */
  Vector getLicensingInformation();

  /**
   * Returns information about the installed license for the given software product.
   * @return Vector  object of class <code>LicenseKeyValue</code> which contains some license key attributes
   */
  LicenseKeyValue getLicensingInformation(String swProduct);

  /**
   * Returns the internal naming literal that was processed for a given storage vendor id 
   * of the persistence layer.
   * This listeral is intended as suffix of a software product.
   * @return String sap storage vendor id
   */
  String getSapStorageVendorId();

  /**
   * Returns some information about the sap system.
   * The Vector contains String objects in the following order:
   * hardware Id, system Id, installation number, system number, basis release, system state,
   * software product no.1, software product no.2, software product no.3, etc.
   * @return Vector vector with objects of class <code>String</code> as elements
   */
  Vector getSystemData();

  /**
   * Checks the license for the given software product.
   * @param swProduct software product
   * @return boolean <code>true</code> if check was successful, else <code>false</code>
   */
  boolean checkLicense(String swProduct);

  /**
   * Checks the validity of the license for a software product and wheter the SW product limit
   * is not exceeded. This method may only be called for SW product that do support limits.
   * 
   * @param swProduct software product for which the license should be checked
   * @param numberOfActiveObjects number of objects that shall be checked
     *                              against the given limit (object could be active user, etc.)
   * @return boolean <code>true</code> if license is valid, else <code>false</code>
   */
  boolean checkLicense(String swProduct, int numberOfActiveObjects);

  /**
   * Deletes the license for the given parameters.
   * @param systemId system id 
   * @param hardwareId hardware id
   * @param swProduct software product
   * @throws LicensingException thrown if there are problems deleting the license
   */
  void deleteLicense(String systemId, String hardwareId, String swProduct) throws LicensingException;

  /**
   * Installs one or more licenses. Can be used to install license(s) provided in a file.
   * @param fileName filename of the license(s) file
   * @param fileAsByteArray license file as byte array
   * @throws LicensingException thrown if there are problems installing the license
   */
  void installLicense(String fileName, byte[] fileAsByteArray) throws LicensingException;

  /**
   * Installs a temporary subsequent license for a given software product.
   * @param swProduct software product
   * @throws LicensingException  thrown if there are problems installing the license
   */
  void installSubsequentTempLicense(String swProduct) throws LicensingException;

  /**
   * Returns the type of the system.
   * 
   * The types can be <code>DEVELOPMENT_SYSTEM</code>, <code>TRAINING_SYSTEM</code>,
   * <code>TEST_SYSTEM</code>, <code>PRODUCTIVE_SYSTEM</code>.
   * 
   * @return the current system type.
   *
   * @see #DEVELOPMENT_SYSTEM
   * @see #TRAINING_SYSTEM
   * @see #TEST_SYSTEM
   * @see #PRODUCTIVE_SYSTEM
   */
  String getSystemType();

  /**
   * Sets the type of the system.
   * 
   * @param type Can be <code>DEVELOPMENT_SYSTEM</code>, <code>TRAINING_SYSTEM</code>,
   * <code>TEST_SYSTEM</code>, <code>PRODUCTIVE_SYSTEM</code>.
   *
   * @return true if setting the system type was successful, otherwise false
   */
  boolean setSystemType(String type) ;

  /**
   * Returns the software products that are and/or were installed on the engine.
   * @return String[] software products array with objects of class <code>String</code> as elements,
   * if no software product is installed, the return value will be of type String[0]
   */
  String[] getSwProductsAsStringArray();

  /**
   * Returns information about the installed licenses for a given software product.
   * There may be more than one licenses being installed for a software product, especially
   * in an High Availability Environment.
   * 
   * In order to get information about all installed licenses, the swProduct can be set 
   * to <t> null </t>.
   *
   * @param swProduct software product, can be <t> null </t> to get information about all licenses
   * @return Properties[] array whose elements serve as container objects. If no license is installed 
   * for the software product, the return value is of type new Properties[0].
   * 
   * @see #SYSTEM_ID 
   * @see #HW_KEY 
   * @see #SW_PRODUCT
   * @see #SW_PRODUCT_LIMIT 
   * @see #BEGIN_DATE
   * @see #END_DATE 
   * @see #TYPE 
   * @see #INST_NO 
   * @see #SYST_NO
   * @see #VALIDITY 
   */
  Properties[] getLicensingInformationAsProperties(String swProduct);
 
  /**
   * Information about if the license check is activated.
   * Allows to decide whether the license information should be displayed or not.
   * 
   * @return true if activated (single-stack), false if deactivated (double-stack)
   */
  boolean isLicenseCheckEnabled();
  

  /**
   * Checks if the installed license identified by system id, software product and hardware key
   * could be replaced/extended by a subsequent temporary license.
   * 
   * @param systemId
   * @param softwareProduct
   * @param hardwareKey
   * @return true if license can be extended, false otherwise
   */
  boolean isExtendable(String systemId, String softwareProduct, String hardwareKey);
}
