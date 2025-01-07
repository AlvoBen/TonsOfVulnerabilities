package com.sap.engine.services.httpserver.interfaces.properties;

import java.lang.reflect.*;

/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

public interface HttpCompressedProperties {
  public String getGZipImplementation();

  public Constructor getGZipImpConstructor();

  public Constructor getGZipDeflaterConstructor();

  public Constructor getGZipCRC32Constructor();

  public void setGZipImplementation(String zipImplementation);

  public String[] getAlwaysCompressedExtensions();

  public String[] getAlwaysCompressedMIMETypes();

  public String[] getNeverCompressedExtensions();

  public String[] getNeverCompressedMIMETypes();
  
  public int getMaximumCompressedURLLength();

  public boolean isCompressedOthers();

  public int getMinGZipLength();

  public boolean isGzip(String fileName, String contentTypeHeader);
}
