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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.core.CoreContext;
import com.sap.engine.frame.core.licensing.LicenseKeyValue;
import com.sap.engine.frame.core.licensing.LicensingContext;

/**
 * @author Jochen Mueller
 * @version 1.1
 */
public class LicensingProcessor{	
	//Stores the line separator used by the commands for output
	public static String newLineSeparator = SystemProperties.getProperty("line.separator");
	
	//context that provides acces to the core of the engine
	private CoreContext coreContext;
	
	//context to access the functionality provided by the <code>LicensingManager</code>
	private LicensingContext licensingContext;

	/**
	 * Initializes some members
	 * @param cc CoreContext
	 */
	public LicensingProcessor(CoreContext cc) {
		this.coreContext = cc;
		licensingContext = coreContext.getLicensingContext();
	}

	/**
	 * Returns information abou the installed licenses
	 * @return Vector
	 */
	public Vector getLicensingInformation() {
		return licensingContext.getLicensingInformation();
	}
  
	/**
	 * Returns information about the given software product
	 * @param swProduct
	 * @return LicenseKeyValue
	 */
	public LicenseKeyValue getLicensingInformation(String swProduct) {
		return licensingContext.getLicensingInformation(swProduct);
	}

  public Properties[] getLicensingInformationAsProperties(String swProduct) {
      Vector licenses = licensingContext.getLicensingInformation();
      if(licenses.isEmpty()) 
        return new Properties[0];

      Iterator iterator = licenses.iterator();
      List swProductList = new ArrayList();
      while(iterator.hasNext()) {
        LicenseKeyValue licenseKeyValue = (LicenseKeyValue) iterator.next();
        String tempSWProduct = licenseKeyValue.getSwProduct();
        if(swProduct == null || tempSWProduct.equals(swProduct)) {
          Properties tempProperties = transformToProperties(licenseKeyValue);
          swProductList.add(tempProperties);
        }
      }
      Properties[] result = (Properties[]) swProductList.toArray(new Properties[swProductList.size()]);
      return result;
   }

	/**
	 * Gets the hardware ID from the server where the message server is running.
	 * @return String hardware id of the message server system.
	 */
	public String getHardwareId() {
		return licensingContext.getHardwareId();
	}

	/**
	 * Gets the system id from the server where the message server is running.
	 *
	 * @return String system id of the message server.
	 */
	public String getSystemId() {
		return licensingContext.getSystemId();
	}

	/**
	 * Returns the installation number
	 * @return String
	 */
	public String getInstNo() {
		return licensingContext.getInstNo();
	}

	/**
	 * Returns the system number
	 * @return String
	 */
	public String getSysNo() {
		return licensingContext.getSysNo();
	}

	/**
	 * Returns the basis release of the engine
	 * @return String
	 */
	public String getBasisRelease() {
		return licensingContext.getBasisRelease();
	}
	
	/**
	 * Returns the sap storage vendor id of the persistence layer
	 * @return String sap storage vendor id
	 */
	public String getSapStorageVendorId() {
		return licensingContext.getSapStorageVendorId();
	}

	/**
	 * Returns the software products installed on the engine
	 * @return Vector
	 */
	public Vector getSwProducts() {
		return licensingContext.getSwProducts();
	}
  
  /**
   * Returns the software products installed on the engine
   * @return Vector
   */
  public String[] getSwProducts2() {
    Vector v = licensingContext.getSwProducts();
    int size = v.size();
    String[] result = new String[size];
    Iterator iterator = v.iterator();
    int i = 0;
    while(iterator.hasNext()) {
      result[i++] = (String) iterator.next();
    }
    
    if(size == 1 && result[0] == "")
      return new String[0];
      
    return result;
  }

	/**
	 * Installs one or more licenses.
	 * @param fileName filename of the license(s) file
	 * @param fileAsByteArray license file as byte array
	 * @throws LicensingException thrown if there are problems installing the license
	 */
	public void installLicense(String fileName, byte[] fileAsByteArray) throws LicensingException {
		Vector errorMessage = new Vector();
		boolean ret = licensingContext.installLicense(fileName, fileAsByteArray, errorMessage);
		if (ret == false) {
			throw new LicensingException(errorMessage);
		}
	}

	/**
	 * Deletes the license for the given parameters
	 * @param systemId system id 
	 * @param hardwareId hardware id
	 * @param swProduct software product
	 * @throws LicensingException thrown if there are problems deleting the license
	 */
	public void deleteLicense(String systemId, String hardwareId, String swProduct) throws LicensingException {
		Vector errorMessage = new Vector();
		boolean ret = licensingContext.deleteLicense(systemId, hardwareId, swProduct, errorMessage);
		if (ret == false) {
			throw new LicensingException(errorMessage);
		}
	}

	/**
	 * Installs a temporary subsequent license
	 * @param swProduct software product
	 * @throws LicensingException  thrown if there are problems installing the license
	 */
	public void installSubsequentTempLicense(String swProduct) throws LicensingException {
		Vector errorMessage = new Vector();
		boolean ret = licensingContext.installSubsequentTempLicense(swProduct, errorMessage);
		if (ret == false) {
			throw new LicensingException(errorMessage);
		}
	}

	/**
	 * Checks the license for the given software product
	 * @param swProduct software product
	 * @return boolean <code>true</code> if check was successful, else <code>false</code>
	 */
	public boolean checkLicense(String swProduct) {
		return licensingContext.checkLicense(swProduct);
	}

    /**
	 * Checks the validity of the license for a software product and wheter the SW product limit
	 * is not exceeded. This method may only be called for SW product that do support limits.
	 * 
	 * @param swProduct software product for which the license should be checked
	 * @param numberOfActiveObjects number of objects that shall be checked
     *                              against the given limit (object could be active user, etc.)
	 * @return boolean <code>true</code> if license is valid, else <code>false</code>
	 */
	public boolean checkLicense(String swProduct, int numberOfActiveObjects) {
		return licensingContext.checkLicense(swProduct, numberOfActiveObjects);
	} 

	/**
	 * Returns all installed licenses
	 * @return Vector contains object of class <code>LicenseKey</code>
	 */
	public Vector getAllLicenses() {
		return licensingContext.getAllLicenses();
	}

	/**
	 * Returns the date by which the license is valid.
	 * @param swProduct software product
	 * @param hwKey hardware key
	 * @return Date validity date
	 */
	public Date getValidityDate(String swProduct, String hwKey) {
		return licensingContext.getValidityDate(swProduct, hwKey);
	}

  public String getSystemType() {
    String result = licensingContext.getSystemType(); 
    return result;
  }
  
  public boolean setSystemType(String type) {
    boolean result = licensingContext.setSystemType(type);
    return result;
  }
  
  public boolean isLicenseCheckEnabled() {
      return licensingContext.isLicenseCheckEnabled();
  }
  
  public boolean isExtendable(String systemId, String softwareProduct, String hardwareKey) {
      return licensingContext.isExtendable(systemId, softwareProduct, hardwareKey);
  }
  
  private Properties transformToProperties(LicenseKeyValue licenseKeyValue) {
      Properties result = new Properties();
      result.setProperty(LicensingRuntimeInterface.SYSTEM_ID, licenseKeyValue.getSystemID());
      result.setProperty(LicensingRuntimeInterface.HW_KEY, licenseKeyValue.getHwKey());
      result.setProperty(LicensingRuntimeInterface.SW_PRODUCT, licenseKeyValue.getSwProduct());
      result.setProperty(LicensingRuntimeInterface.SW_PRODUCT_LIMIT, licenseKeyValue.getSwProductLimit());
      result.setProperty(LicensingRuntimeInterface.BEGIN_DATE, licenseKeyValue.getBeginDate());
      result.setProperty(LicensingRuntimeInterface.END_DATE, licenseKeyValue.getEndDate());
      result.setProperty(LicensingRuntimeInterface.TYPE, licenseKeyValue.getType());
      result.setProperty(LicensingRuntimeInterface.INST_NO, licenseKeyValue.getInstNo());
      result.setProperty(LicensingRuntimeInterface.SYST_NO, licenseKeyValue.getSystNo());
      result.setProperty(LicensingRuntimeInterface.VALIDITY, licenseKeyValue.getValidity());
      return result;
    }
}
