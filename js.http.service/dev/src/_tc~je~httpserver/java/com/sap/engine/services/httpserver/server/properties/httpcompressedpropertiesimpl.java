/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */

package com.sap.engine.services.httpserver.server.properties;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;

import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.interfaces.properties.HttpCompressedProperties;

import java.util.Vector;
import java.lang.reflect.*;
import java.io.*;

/**
 *
 * @author Violeta Uzunova
 * @version 6.30
 */
public class HttpCompressedPropertiesImpl implements HttpCompressedProperties {
  private static final String unknown = "[unknown]";

  private String alwaysCompressedExtensions[] = {".htm",".html"};
  private String alwaysCompressedMIMETypes[] = {"text/html"};
  private String neverCompressedExtensions[] = {".zip",".cs", ".rar", ".arj", ".z", ".gz", ".tar",
  					".lzh", ".cab", ".hqx", ".ace", ".jar", ".ear", ".war", ".css", ".pdf", ".js", 
  					".gzip", ".uue", ".bz2", ".iso",".sda", ".sar"};
  private String neverCompressedMIMETypes[] = {"image", "application/x-compressed", "application/zip", "application/x-gzip", "application/x-zip-compressed", 
  					"application/pdf", "content/unknown", "text/javascript", "[unknown]"};
  private boolean commpressedOthers = true;
  /**
   * Minimum length of zipped response
   */
  private int minimumGZipLength = 8192;
  private int maximumCompressedURLLength = -1;

  /**
   * External GZIP Library entries
   */
  private String gzipImplementation = null;
  private Constructor gzipImpConstructor = null;
  private Constructor gzipDeflaterConstructor = null;
  private Constructor gzipCRC32Constructor = null;

  private Vector alwaysCompressedExtensionsVector = null;
  private Vector alwaysCompressedMIMETypesVector = null;
  private Vector neverCompressedExtensionsVector = null;
  private Vector neverCompressedMIMETypesVector = null;

  public HttpCompressedPropertiesImpl() {
  }

  public Constructor getGZipImpConstructor() {
    return gzipImpConstructor;
  }

  public String getGZipImplementation() {
    return gzipImplementation;
  }

  public Constructor getGZipDeflaterConstructor() {
    return gzipDeflaterConstructor;
  }

  public Constructor getGZipCRC32Constructor() {
    return gzipCRC32Constructor;
  }

  public void setGZipImplementation(String zipImplementation) {
    if (zipImplementation != null && !zipImplementation.trim().equals("")) {
      this.gzipImplementation = zipImplementation.trim();
      String deflater = this.gzipImplementation.substring(0, this.gzipImplementation.lastIndexOf(".")) + ".Deflater";
      String crc = this.gzipImplementation.substring(0, this.gzipImplementation.lastIndexOf(".")) + ".CRC32";
      try {
        this.gzipImpConstructor = Class.forName(gzipImplementation).getDeclaredConstructor(new Class[]{OutputStream.class});
        this.gzipDeflaterConstructor = Class.forName(deflater).getDeclaredConstructor(new Class[]{Integer.TYPE, Boolean.TYPE});
        this.gzipCRC32Constructor = Class.forName(crc).getDeclaredConstructor(new Class[]{});
        Log.logWarning("ASJ.http.000064", 
          "Configurable external implementation for writing compressed data in GZIP file format will be used for [{0}, {1}, {2}].", 
          new Object[]{this.gzipImplementation, deflater, crc}, null, null, null);
      } catch (Exception e){
          Log.logWarning("ASJ.http.000065", 
            "Error configuring the external implementation for writing compressed data in GZIP file format [{0}, {1}, {2}].", 
            new Object[]{this.gzipImplementation, deflater, crc}, e, null, null, null);
        this.gzipImplementation = null;
        this.gzipImpConstructor = null;
        this.gzipDeflaterConstructor = null;
        this.gzipCRC32Constructor = null;
      }
    } else {
      if (LOCATION_HTTP.bePath()) {
				LOCATION_HTTP.pathT("HttpCompressedPropertiesImpl.setGZipImplementation(): The default (JDK) implementation will be used for writing compressed data in the GZIP file format!");
			}
			this.gzipImplementation = null;
      this.gzipImpConstructor = null;
      this.gzipDeflaterConstructor = null;
      this.gzipCRC32Constructor = null;
    }
  }
  
  public String[] getAlwaysCompressedExtensions() {
    return alwaysCompressedExtensions;
  }

  protected void setAlwaysCompressedExtensions(Vector alwaysCompressedExtensionsVector) {
    this.alwaysCompressedExtensionsVector = alwaysCompressedExtensionsVector;
  }

  public String[] getAlwaysCompressedMIMETypes() {
    return alwaysCompressedMIMETypes;
  }

  protected void setAlwaysCompressedMIMETypes(Vector alwaysCompressedMIMETypesVector) {
    this.alwaysCompressedMIMETypesVector = alwaysCompressedMIMETypesVector;
  }

  public String[] getNeverCompressedExtensions() {
    return neverCompressedExtensions;
  }

  protected void setNeverCompressedExtensions(Vector neverCompressedExtensionsVector) {
    this.neverCompressedExtensionsVector = neverCompressedExtensionsVector;
  }

  public String[] getNeverCompressedMIMETypes() {
    return neverCompressedMIMETypes;
  }

  protected void setNeverCompressedMIMETypes(Vector neverCompressedMIMETypesVector) {
    this.neverCompressedMIMETypesVector = neverCompressedMIMETypesVector;
  }

  public boolean isCompressedOthers() {
    return commpressedOthers;
  }

  public void setCompressedOthers(boolean commpressedOthers) {
    this.commpressedOthers = commpressedOthers;
  }

  public int getMinGZipLength() {
    return minimumGZipLength;
  }

  public void setMaximumCompressURLLength(int urlLength) {
    this.maximumCompressedURLLength = urlLength;
  }
 
  public int getMaximumCompressedURLLength() {
    return maximumCompressedURLLength;
  }

  public boolean isGzip(String fileName, String contentTypeHeader) {
    if (contentTypeHeader == null) {
      contentTypeHeader = unknown;
    } else {
      contentTypeHeader = contentTypeHeader.toLowerCase();
    }
    for (int i = 0; i < getAlwaysCompressedMIMETypes().length; i++) {
      if (contentTypeHeader.startsWith(getAlwaysCompressedMIMETypes()[i])) {
        return true;
      }
    }
    if (fileName != null) {
      for (int i = 0; i < getAlwaysCompressedExtensions().length; i++) {
        if (fileName.endsWith(getAlwaysCompressedExtensions()[i])) {
          return true;
        }
      }
    }
    if (!isCompressedOthers()) {
      return false;
    }
    for (int i = 0; i < getNeverCompressedMIMETypes().length; i++) {
      if (contentTypeHeader.startsWith(getNeverCompressedMIMETypes()[i])) {
        return false;
      }
    }
    if (fileName == null) {
      return true;
    }
    for (int i = 0; i < getNeverCompressedExtensions().length; i++) {
      if (fileName.endsWith(getNeverCompressedExtensions()[i])) {
        return false;
      }
    }
    return true;
  }

  protected void setMinGZipLength(int minimumGZipLength) {
    this.minimumGZipLength = minimumGZipLength;
  }

  protected void initGzipSettings() {
    if (alwaysCompressedExtensionsVector != null && alwaysCompressedExtensionsVector.size() != 0 &&
          neverCompressedExtensionsVector != null && neverCompressedExtensionsVector.size() != 0) {
      for (int i = 0; i < alwaysCompressedExtensionsVector.size(); i++) {
        if (neverCompressedExtensionsVector.contains(alwaysCompressedExtensionsVector.elementAt(i))) {
          Log.logWarning("ASJ.http.000066", 
            "[{0}] entry exists in [{1}] and [{2}] lists. " +
            "It will be removed from both lists and will be treated according to [{3}] property.", 
            new Object[]{alwaysCompressedExtensionsVector.elementAt(i), HttpPropertiesImpl.ALWAYS_COMPRESS_KEY , HttpPropertiesImpl.NEVER_COMPRESS_KEY, HttpPropertiesImpl.COMPRESSED_OTHERS_KEY}, null, null, null);
          neverCompressedExtensionsVector.remove(alwaysCompressedExtensionsVector.elementAt(i));
          alwaysCompressedExtensionsVector.removeElementAt(i--);
        }
      }
    }
    if (alwaysCompressedMIMETypesVector != null && alwaysCompressedMIMETypesVector.size() != 0 &&
          neverCompressedMIMETypesVector != null && neverCompressedMIMETypesVector.size() != 0) {
      for (int i = 0; i < alwaysCompressedMIMETypesVector.size(); i++) {
        if (neverCompressedMIMETypesVector.contains(alwaysCompressedMIMETypesVector.elementAt(i))) {
          Log.logWarning("ASJ.http.000067" , 
            "[{0}] entry exists in [{1}] and [{2}] lists. " +
            "It will be removed from both lists and will be treated according to [{3}] property.", 
            new Object[]{alwaysCompressedMIMETypesVector.elementAt(i), HttpPropertiesImpl.ALWAYS_COMPRESS_KEY, HttpPropertiesImpl.NEVER_COMPRESS_KEY, HttpPropertiesImpl.COMPRESSED_OTHERS_KEY}, null, null, null);
          neverCompressedMIMETypesVector.remove(alwaysCompressedMIMETypesVector.elementAt(i));
          alwaysCompressedMIMETypesVector.removeElementAt(i--);
        }
      }
    }
    if (alwaysCompressedExtensionsVector == null) {
      alwaysCompressedExtensions = new String[0];
    } else {
      alwaysCompressedExtensions = new String[alwaysCompressedExtensionsVector.size()];
      for (int i = 0; i < alwaysCompressedExtensionsVector.size(); i++) {
        alwaysCompressedExtensions[i] = (String)alwaysCompressedExtensionsVector.elementAt(i);
      }
    }
    if (alwaysCompressedMIMETypesVector == null) {
      alwaysCompressedMIMETypes = new String[0];
    } else {
      alwaysCompressedMIMETypes = new String[alwaysCompressedMIMETypesVector.size()];
      for (int i = 0; i < alwaysCompressedMIMETypesVector.size(); i++) {
        alwaysCompressedMIMETypes[i] = (String)alwaysCompressedMIMETypesVector.elementAt(i);
      }
    }
    if (neverCompressedExtensionsVector == null) {
      neverCompressedExtensions = new String[0];
    } else {
      neverCompressedExtensions = new String[neverCompressedExtensionsVector.size()];
      for (int i = 0; i < neverCompressedExtensionsVector.size(); i++) {
        neverCompressedExtensions[i] = (String)neverCompressedExtensionsVector.elementAt(i);
      }
    }
    if (neverCompressedMIMETypesVector == null) {
      neverCompressedMIMETypes = new String[0];
    } else {
      neverCompressedMIMETypes = new String[neverCompressedMIMETypesVector.size()];
      for (int i = 0; i < neverCompressedMIMETypesVector.size(); i++) {
        neverCompressedMIMETypes[i] = (String)neverCompressedMIMETypesVector.elementAt(i);
      }
    }
  }
}
