package com.sap.engine.lib.schema.canonicalizator.helpers;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-3-9
 * Time: 17:25:34
 * To change this template use Options | File Templates.
 */
public class Triple {

  private int offset;
  private String prefix;
  private String uri;

  public Triple(int offset, String prefix, String uri) {
    this.offset = offset;
    this.prefix = prefix;
    this.uri = uri;
  }

  public int getOffset() {
    return(offset);
  }

  public String getUri() {
    return(uri);
  }

  public String getPrefix() {
    return(prefix);
  }

  public String toString() {
    return("[offset : " + offset + "; prefix : " + prefix + "; uri : " + uri + "]");
  }
}
