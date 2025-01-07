package com.sap.liteadmin.application;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;


/**
 * Representation for 
 * <!ELEMENT cookie (type, path?, domain?, max-age?)>
 */
public class CookiesSettings {
  private String type = null;
  private String path = null;
  private String domain = null;
  private int maxAge = -1;
  //needed only in case of the update - do not show them
  private String caption = null;
  private String description = null;
  private String elementName = null;
  
  public CookiesSettings() {
    
  }
  
  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public int getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(int maxAge) {
    this.maxAge = maxAge;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getElementName() {
    return elementName;
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
  }

  public List getTypesList() {    
    ArrayList<SelectItem> list = new ArrayList<SelectItem>();
    list.add(new SelectItem("APPLICATION", "APPLICATION"));    //1
    list.add(new SelectItem("SESSION", "SESSION"));  //2
    return list;    
  }
  
  
}
