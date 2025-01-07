/*
 * Created on 2005-5-31
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.validator.xpath;

import com.sap.engine.lib.schema.validator.ReusableObjectsPool;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class AttributeXPathStep extends XPathStep {
  
  private String type;
  private CharArray prefix;
  
  public AttributeXPathStep(ReusableObjectsPool pool) {
    super(pool);
  }
  
  public void setPrefix(CharArray prefix) {
    this.prefix = prefix;
  }
  
  public CharArray getPrefix() {
    return(prefix);
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public String getType() {
    return(type);
  }
  
  protected void initRepresentationBufferWithQName(StringBuffer buffer) {
    buffer.append("@{");
    buffer.append(uriStr);
    buffer.append("}:");
    buffer.append(localNameStr);
  }
  
  public boolean isAttribute() {
    return(true);
  }
  
  public void reuse() {
    super.reuse();
    type = null;
    prefix = null;
  }
}
