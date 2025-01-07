package com.sap.engine.lib.schema.components;

import javax.xml.transform.Result;

/**
 * Provided for integration with JAXP.
 *
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version 15-Apr-02, 09:26:38
 */
public final class SchemaComponentResult implements Result {

  private Schema schema;
  private String systemId;

  public SchemaComponentResult() {

  }

  public Schema getSchema() {
    return schema;
  }

  public void setSchema(Schema schema) {
    this.schema = schema;
  }

  public void setSystemId(String s) {
    this.systemId = s;
  }

  public String getSystemId() {
    return systemId;
  }

}

