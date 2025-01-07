package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;

/**
 * Title:        xml2000
 * Description:  org.w3c.dom.* ;
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Chavdar Baykov, Chavdarb@abv.bg
 * @version      August 2001
 * @deprecated Use org.w3c.dom.* official API.
 */
@Deprecated
public class EntityImpl extends NodeImpl implements Entity {

  private String publicId = null;
  private String systemId = null;
  //private Document ownerDocument = null;
  private String name = null;
  private String value = null;
  private String notationName;

  protected EntityImpl() {

  }

  protected EntityImpl(Document owner) {
    setOwnerDocument(owner);
  }

  /**
   * What's the public id and system id for ?
   */
  public final EntityImpl init(String name, String publicId, String systemId, String value, Document owner) {
    this.ownerDocument = owner;
    this.publicId = publicId;
    this.systemId = systemId;
    this.value = value;
    this.name = name;
    return this;
  }

  public String getNotationName() {
    return notationName;
  }

  protected void setNotationName(String s) {
    notationName = s;
  }

  public String getPublicId() {
    return publicId;
  }

  public String getSystemId() {
    return systemId;
  }

  public String getNodeName() {
    return name;
  }

  public String getNodeValue() {
    return value;
  }

  public void setNodeValue(String value) throws DOMException {
    throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "Entity content can not be modified.");
  }

  public short getNodeType() {
    return Node.ENTITY_NODE;
  }

  public Node cloneNode(boolean deep) {
    EntityImpl result = new EntityImpl();
    result.init(this.name, this.publicId, this.systemId, this.value, ownerDocument);
    super.transferData(this, result, deep);
    result.setOwnerDocument(getOwnerDocument());
    return result;
  }
  
  public String getEncoding() {
    return "";
  }
  
  public boolean getStandalone() {
    return true;
  }
  
  public boolean getStrictErrorChecking() {
    return false;
  }
  
  public String getVersion() {
    return "";
  }
  
  public void setEncoding(String enc) {
  }
  
  public void setStandalone(boolean b) {
  }
  
  public void setStrictErrorChecking(boolean b) {
  }
  
  public void setVersion(String s){
  }

	public String getInputEncoding() {
		throw new NullPointerException("Not implemented!");
	}

	public String getXmlEncoding() {
		throw new NullPointerException("Not implemented!");
	}

	public String getXmlVersion() {
		throw new NullPointerException("Not implemented!");
	}  

}

