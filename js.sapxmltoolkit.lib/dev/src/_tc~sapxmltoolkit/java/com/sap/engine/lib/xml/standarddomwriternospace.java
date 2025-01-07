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
package com.sap.engine.lib.xml;

import javax.xml.transform.OutputKeys;

/**
 * StandardDOMWriter is the basic class for printing a DOM structure in a xml file.
 *
 * @deprecated    Use JAXP
 */
@Deprecated
public class StandardDOMWriterNoSpace extends StandardDOMWriter {
  // constructors
  public StandardDOMWriterNoSpace() {
    super();
    transformer.setOutputProperty(OutputKeys.INDENT, "no");
  }

  public StandardDOMWriterNoSpace(boolean _canonical) {
    super(_canonical);
    transformer.setOutputProperty(OutputKeys.INDENT, "no");
  }
}

