package com.sap.engine.lib.schema.components.impl;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import com.sap.engine.lib.schema.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-5-10
 * Time: 15:28:09
 * To change this template use Options | File Templates.
 */
public class SchemaForSchemaEntityResolver implements EntityResolver, Constants {

  public InputSource resolveEntity(String publicId, String systemId) {
    if(systemId.equals(SCHEMA_FOR_SCHEMA_FILE_NAME)) {
      return(new InputSource(getClass().getClassLoader().getResourceAsStream(SCHEMA_FOR_SCHEMA_LOCATION)));
    }
    return(new InputSource(getClass().getClassLoader().getResourceAsStream(XML_ATTRIBS_SCHEMA_LOCATION)));
  }
}
