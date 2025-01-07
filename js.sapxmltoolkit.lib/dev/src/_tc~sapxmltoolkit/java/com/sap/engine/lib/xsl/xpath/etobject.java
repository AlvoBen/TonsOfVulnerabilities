package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author Vladimir Savchenko
 * @version June 2001
 */
public class ETObject {

  public String squery = null;
  public CharArray chquery = null;
  public ETItem et = null;

  public ETObject(String query, ETItem et) {
    this.squery = query;
    this.et = et;
    this.chquery = new CharArray(this.squery);
  }

  public ETObject(CharArray query, ETItem et) {
    this.chquery = query.copy();
    this.et = et;
    this.squery = chquery.getString();
  }

  public ETObject cloneIt() { //???
    return this;
  }

  public void print() {
    et.print(10);
  }

}

