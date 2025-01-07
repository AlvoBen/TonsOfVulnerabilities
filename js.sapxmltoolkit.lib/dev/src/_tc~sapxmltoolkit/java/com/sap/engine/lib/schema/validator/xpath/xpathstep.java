package com.sap.engine.lib.schema.validator.xpath;

import com.sap.engine.lib.schema.validator.ReusableObjectsPool;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-9-14
 * Time: 9:38:53
 * To change this template use Options | File Templates.
 */
public abstract class XPathStep {

  protected CharArray uri;
  protected CharArray localName;
  protected CharArray value;
  protected CharArray qName;
  protected String localNameStr;
  protected String valueStr;
  protected String uriStr;
  protected int line;
  protected int column;
  protected String representation;
  protected ReusableObjectsPool pool;
  
  public XPathStep(ReusableObjectsPool pool) {
    this.pool = pool;
  }
  
  public void setUri(CharArray uri) {
    this.uri = uri;
  }
  
  public CharArray getUri() {
    return(uri);
  }

  public void setLocalName(CharArray localName) {
    this.localName = localName;
  }
  
  public CharArray getLocalName() {
    return(localName);
  }

  public void setQName(CharArray qName) {
    this.qName = qName;
  }
  
  public CharArray getQName() {
    return(qName);
  }
  
  public void setValue(CharArray value) {
    this.value = value;
  }
  
  public CharArray getValue() {
    return(value);
  }
  
  public void setUriStr(String uriStr) {
    this.uriStr = uriStr;
  }
  
  public String getUriStr() {
    return(uriStr);
  }

  public void setLocalNameStr(String localNameStr) {
    this.localNameStr = localNameStr;
  }

  public String getLocalNameStr() {
    return(localNameStr);
  }
  
  public void setValueStr(String valueStr) {
    this.valueStr = valueStr;
  }
  
  public String getValueStr() {
    return(valueStr);
  }
  
  public void setLine(int line) {
    this.line = line;
  }
  
  public int getRow() {
    return(line);
  }
  
  public void setColumn(int column) {
    this.column = column;
  }

  public int getColumn() {
    return(column);
  }
  
  public String getRepresentation() {
    if(representation == null) {
      StringBuffer buffer = pool.getStringBuffer();
      initRepresentationBufferWithRowAndLine(buffer);
      initRepresentationBufferWithQName(buffer);
      representation = buffer.toString();
      pool.reuseStringBuffer(buffer);
    }
    return(representation);
  }
  
  public abstract boolean isAttribute();
  
  public void reuse() {
    uri = null;
    localName = null;
    value = null;
    qName = null;
    localNameStr = null;
    valueStr = null;
    uriStr = null;
    line = -1;
    column = -1;
    representation = null;
  }
  
  protected abstract void initRepresentationBufferWithQName(StringBuffer buffer);
  
  protected void initRepresentationBufferWithRowAndLine(StringBuffer buffer) {
    buffer.append("line: ");
    buffer.append(line);
    buffer.append("; ");
    buffer.append("col: ");
    buffer.append(column);
    buffer.append("; ");
  }
}
