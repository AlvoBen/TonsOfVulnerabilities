package com.sap.engine.lib.schema.canonicalizator.helpers;

import com.sap.engine.lib.schema.exception.CanonicalizationException;
import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.util.LexicalParser;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-3-5
 * Time: 17:10:18
 * To change this template use Options | File Templates.
 */
public class SSCAnnotation implements Constants {

  private String embeddedLang;
  private String embeddedLangAttributeUri;
  private String embeddedLangAttributeName;
  private String caseMap;
  private String caseMapAttributeUri;
  private String caseMapAttributeName;
  private String caseMapKind;

  public String getEmbededLang() {
    return(embeddedLang);
  }

  public String getEmbededLangAttributeUri() {
    return(embeddedLangAttributeUri);
  }

  public String getEmbededLangAttributeName() {
    return(embeddedLangAttributeName);
  }

  public String getCaseMap() {
    return(caseMap);
  }

  public String getCaseMapAttributeUri() {
    return(caseMapAttributeUri);
  }

  public String getCaseMapAttributeName() {
    return(caseMapAttributeName);
  }

  public String getCaseMapKind() {
    return(caseMapKind);
  }

  public void setEmbededLang(String embeddedLang) {
    this.embeddedLang = embeddedLang;
  }

  public void setEmbededLangAttributeUri(String embeddedLangAttributeUri) {
    this.embeddedLangAttributeUri = embeddedLangAttributeUri;
  }

  public void setEmbededLangAttributeName(String embeddedLangAttributeName) {
    this.embeddedLangAttributeName = embeddedLangAttributeName;
  }

  public void setCaseMap(String caseMap) {
    this.caseMap = caseMap;
  }

  public void setCaseMapAttributeUri(String caseMapAttributeUri) {
    this.caseMapAttributeUri = caseMapAttributeUri;
  }

  public void setCaseMapAttributeName(String caseMapAttributeName) {
    this.caseMapAttributeName = caseMapAttributeName;
  }

  public void setCaseMapKind(String caseMapKind) {
    this.caseMapKind = caseMapKind;
  }
}
