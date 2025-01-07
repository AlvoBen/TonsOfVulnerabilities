/*
 * Created on 2005-5-31
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.validator.xpath;

import java.util.Vector;

import com.sap.engine.lib.schema.components.ElementDeclaration;
import com.sap.engine.lib.schema.components.TypeDefinitionBase;
import com.sap.engine.lib.schema.validator.ReusableObjectsPool;
import com.sap.engine.lib.schema.validator.automat.ContentAutomat;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class ElementXPathStep extends XPathStep {
  
  private ElementDeclaration elemDeclaration;
  private TypeDefinitionBase typeDefinition;
  private ContentAutomat contentAutomat;
  private String xsiNillAttribValue;
  private String xsiTypeAttribValue;
  private boolean isNil;
  private boolean isLax;
  private boolean isSkip;
  private Vector attribXPathSteps;
  private boolean bDisableOutputEscaping;
  private boolean hasTextNodeChild;
  
  public ElementXPathStep(ReusableObjectsPool pool) {
    super(pool);
    attribXPathSteps = new Vector();
  }
  
  public void setElementDeclaration(ElementDeclaration elemDeclaration) {
    this.elemDeclaration = elemDeclaration;
  }
  
  public ElementDeclaration getElementDeclaration() {
    return(elemDeclaration);
  }
  
  public void setTypeDefinition(TypeDefinitionBase typeDefinition) {
    this.typeDefinition = typeDefinition;
  }
  
  public TypeDefinitionBase getTypeDefinition() {
    return(typeDefinition);
  }
  
  public void appendValue(CharArray value) {
    if(this.value == null) {
      this.value = new CharArray(value);
    } else {
      this.value.append(value);
    }
  }

  public void setBDisableOutputEscaping(boolean bDisableOutputEscaping) {
    this.bDisableOutputEscaping = bDisableOutputEscaping;
  }

  public boolean getBDisableOutputEscaping() {
    return(bDisableOutputEscaping);
  }

  public void addAttributeXPathStep(AttributeXPathStep attribXPathStep) {
    attribXPathSteps.add(attribXPathStep);
  }
  
  public void removeAttributeXPathStep(AttributeXPathStep attribXPathStep) {
    attribXPathSteps.remove(attribXPathStep);
  }

  public Vector getAttributeXPathSteps() {
    return(attribXPathSteps);
  }

  public String getXsiNillAttributeValue() {
    return(xsiNillAttribValue);
  }

  public void setXsiNillAttributeValue(String xsiNillAttribValue) {
    this.xsiNillAttribValue = xsiNillAttribValue;
  }

  public ContentAutomat getContentAutomat() {
    return(contentAutomat);
  }

  public void setContentAutomat(ContentAutomat contentAutomat) {
    this.contentAutomat = contentAutomat;
  }

  public String getXsiTypeAttributeValue() {
    return(xsiTypeAttribValue);
  }

  public void setXsiTypeAttributeValue(String xsiTypeAttribValue) {
    this.xsiTypeAttribValue = xsiTypeAttribValue;
  }

  public boolean isNil() {
    return(isNil);
  }

  public void setNil(boolean isNil) {
    this.isNil = isNil;
  }

  public boolean isLax() {
    return(isLax);
  }

  public void setLax(boolean isLax) {
    this.isLax = isLax;
  }

  public boolean isSkip() {
    return(isSkip);
  }

  public void setSkip(boolean isSkip) {
    this.isSkip = isSkip;
  }
  
  public void setHasTextNodeChild(boolean hasTextNodeChild) {
    this.hasTextNodeChild = hasTextNodeChild;
  }
  
  public boolean hasTextNodeChild() {
    return(hasTextNodeChild);
  }
  
  protected void initRepresentationBufferWithQName(StringBuffer buffer) {
    buffer.append("{");
    buffer.append(uriStr);
    buffer.append("}:");
    buffer.append(localNameStr);
  }
  
  public void reuse() {
    super.reuse();
    elemDeclaration = null;
    typeDefinition = null;
    contentAutomat = null;
    xsiNillAttribValue = null;
    xsiTypeAttribValue = null;
    isNil = false;
    isLax = false;
    isSkip = false;
    attribXPathSteps.clear();
    bDisableOutputEscaping = false;
    hasTextNodeChild = false;
  }
  
  public boolean isAttribute() {
    return(false);
  }
}
