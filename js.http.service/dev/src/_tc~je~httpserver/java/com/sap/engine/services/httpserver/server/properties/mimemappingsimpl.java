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
package com.sap.engine.services.httpserver.server.properties;

import com.sap.engine.services.httpserver.interfaces.properties.MimeMappings;

import java.util.Hashtable;

public class MimeMappingsImpl implements MimeMappings {
  private Hashtable mimeMappings = new Hashtable();

  public void initMimeMappings(Hashtable mimeMappings) {
    mimeMappings.put("", "content/unknown");
    this.mimeMappings = mimeMappings;
  }

  public String getMimeType(String extension) {
    return (String)mimeMappings.get(extension);
  }

  public void addMimeType(String extension, String mimeType) {
    mimeMappings.put(extension, mimeType);
  }
}
