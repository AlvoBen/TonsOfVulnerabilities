package com.sap.engine.lib.schema.canonicalizator.helpers;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-3-8
 * Time: 17:19:19
 * To change this template use Options | File Templates.
 */
public class NSAttribStructure {

  private String prefix;
  private String value;

  public NSAttribStructure(String prefix, String value) {
    this.prefix = prefix;
    this.value = value;
  }

  public String getPrefix() {
    return(prefix);
  }

  public String getValue() {
    return(value);
  }
}
