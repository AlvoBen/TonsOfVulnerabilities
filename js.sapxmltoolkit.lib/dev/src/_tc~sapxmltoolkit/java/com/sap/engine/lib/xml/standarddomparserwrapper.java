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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 *
 * @author Monika Kovachka
 * @version 4.0.0
 *
 * @deprecated    Use JAXP
 */
@Deprecated
public interface StandardDOMParserWrapper {

  public Document parse(String uri) throws SAXException, IOException;


  public Document parse(InputStream is) throws SAXException, IOException;

}

