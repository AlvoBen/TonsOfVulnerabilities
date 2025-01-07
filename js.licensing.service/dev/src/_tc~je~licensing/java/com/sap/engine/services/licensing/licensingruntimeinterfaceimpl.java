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
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.sap.engine.frame.core.licensing.LicenseKeyValue;
import com.sap.engine.frame.state.ManagementListener;

/**
 * @author Jochen Mueller
 * @version 1.1
 */

public class LicensingRuntimeInterfaceImpl implements LicensingRuntimeInterface {
  LicensingProcessor licensingProcessor;

  LicensingRuntimeInterfaceImpl(LicensingProcessor lpr) {
    licensingProcessor = lpr;
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getLicenseInformation()
   */
  public Vector getLicensingInformation() {
    return licensingProcessor.getLicensingInformation();

  }
  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getLicenseInformation(String)
   */
  public LicenseKeyValue getLicensingInformation(String swProduct) {
    return licensingProcessor.getLicensingInformation(swProduct);
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getSystemData()
   */
  public Vector getSystemData() {
    Vector result = new Vector();
    result.add(licensingProcessor.getHardwareId());
    result.add(licensingProcessor.getSystemId());
    result.add(licensingProcessor.getInstNo());
    result.add(licensingProcessor.getSysNo());
    result.add(licensingProcessor.getBasisRelease());
    result.add(licensingProcessor.getSystemType());
    Enumeration swProducts = licensingProcessor.getSwProducts().elements();
    while (swProducts.hasMoreElements()) {
      result.add((String) swProducts.nextElement());
    }
    return result;
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#deleteLicense(String, String, String)
   */
  public void deleteLicense(String systemId, String hardwareId, String swProduct) throws LicensingException {
    licensingProcessor.deleteLicense(systemId, hardwareId, swProduct);
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#installLicenses(String)
   */
  public void installLicense(String fileName, byte[] fileAsByteArray) throws LicensingException {
    licensingProcessor.installLicense(fileName, fileAsByteArray);
  }

  /**
  * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#installTempLicense(String)
  */
  public void installSubsequentTempLicense(String swProduct) throws LicensingException {
    licensingProcessor.installSubsequentTempLicense(swProduct);
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#checkLicense(String)
   */
  public boolean checkLicense(String swProduct) {
    return licensingProcessor.checkLicense(swProduct);
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#checkLicense(String, int)
   */
  public boolean checkLicense(String swProduct, int numberOfActiveObjects) {
    return licensingProcessor.checkLicense(swProduct, numberOfActiveObjects);
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getAllLicenses()
   */
  public Vector getAllLicenses() {
    return licensingProcessor.getAllLicenses();
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getBasisRelease()
   */
  public String getBasisRelease() {
    return licensingProcessor.getBasisRelease();
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getHardwareId()
   */
  public String getHardwareId() {
    return licensingProcessor.getHardwareId();
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getInstNo()
   */
  public String getInstNo() {
    return licensingProcessor.getInstNo();
  }
  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getSapStorageVendorId()
   */
  public String getSapStorageVendorId() {
    return licensingProcessor.getSapStorageVendorId();
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getSwProducts()
   */
  public Vector getSwProducts() {
    return licensingProcessor.getSwProducts();
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getSysNo()
   */
  public String getSysNo() {
    return licensingProcessor.getSysNo();
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getSystemId()
   */
  public String getSystemId() {
    return licensingProcessor.getSystemId();
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getValidityDate(String, String)
   */
  public Date getValidityDate(String swProduct, String hwKey) {
    return licensingProcessor.getValidityDate(swProduct, hwKey);
  }

  /**
   * @see com.sap.engine.frame.state.ManagementInterface#registerManagementListener(ManagementListener)
   */
  public void registerManagementListener(ManagementListener arg0) {
  }

  /** 
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getSystemType()
   */
  public String getSystemType() {
    String result = licensingProcessor.getSystemType();
    return result;
  }

  /** 
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#setSystemType(String)
   */
  public boolean setSystemType(String type) {
    boolean result = licensingProcessor.setSystemType(type);
    return result;
  }

  //***************************** additional methods for new JMX model ***************************************
 
  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getLicensingInformationAsProperties(java.lang.String)
   */
  public Properties[] getLicensingInformationAsProperties(String swProduct) {
    return licensingProcessor.getLicensingInformationAsProperties(swProduct);
  }

  /**
   * @see com.sap.engine.services.licensing.LicensingRuntimeInterface#getSwProductsAsStringArray()
   */
  public String[] getSwProductsAsStringArray() {
    return licensingProcessor.getSwProducts2();
  }
  
  public boolean isLicenseCheckEnabled() {
      return licensingProcessor.isLicenseCheckEnabled();
  }
  
  public boolean isExtendable(String systemId, String softwareProduct, String hardwareKey) {
    return licensingProcessor.isExtendable(systemId, softwareProduct, hardwareKey);
  }
}
