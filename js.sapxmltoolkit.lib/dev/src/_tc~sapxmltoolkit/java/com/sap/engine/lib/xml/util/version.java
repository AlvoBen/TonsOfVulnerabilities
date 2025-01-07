/**
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.lib.xml.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class may be used in order to obtain information about the version of the SAP XMLToolkit
 * 
 * @author Alexander Zubev
 */
public class Version {

  /**
   * A method for getting the current version of the SAPXMLToolkit.
   * It read the information from the <code>/META-INF/sapxmltoolkit.properties</code> file.
   * 
   * @return The version as a String or <code>null</code> if the version cannot be determined.
   * @throws IOException in case the file cannot be read.
   */
  public static String getVersion() throws IOException {
    InputStream in = Version.class.getResourceAsStream("/META-INF/sapxmltoolkit.properties");
    if (in == null) {
      throw new IOException("Cannot load /META-INF/sapxmltoolkit.properties");
    }
    Properties props = new Properties();
    props.load(in);
    return props.getProperty("version");
  }
}
