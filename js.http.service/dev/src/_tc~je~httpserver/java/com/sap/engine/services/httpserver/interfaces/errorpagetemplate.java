package com.sap.engine.services.httpserver.interfaces;

/**
 * The class defines the look and feel of the error page.
 * 
 * @author I026706
 * @version 7.20
 */
public class ErrorPageTemplate {
  private String errorPageTitle = null;
  private String messageIDFragment = null;
  private String correctionHintsFragment = null;
  private String tsgUrlFragment = null;
  private String generateErrorReportFragment = null;
  private String headBodyFragment = null;
  private String bodyBeginFragment = null;
  private String responseCodeServerVersionFragment = null;//response code + server version
  private String mainMessageFragment = null;
  private String detailsFragment = null;
  private String additionalDetailsFragment = null;
  
  public ErrorPageTemplate() {
  }//end of constructor 
  
  public ErrorPageTemplate(String errorPageTitle, String messageIDFragment, String correctionHintsFragment, String tsgUrlFragment, String generateErrorReportFragment, 
                           String headBodyFragment, String bodyBeginFragment, String responseCodeServerVersionFragment, String mainMessageFragment, String detailsFragment) {
    this.errorPageTitle = errorPageTitle;
    this.messageIDFragment = messageIDFragment;
    this.correctionHintsFragment = correctionHintsFragment;
    this.tsgUrlFragment = tsgUrlFragment;
    this.generateErrorReportFragment = generateErrorReportFragment;
    this.headBodyFragment = headBodyFragment;
    this.bodyBeginFragment = bodyBeginFragment;
    this.responseCodeServerVersionFragment = responseCodeServerVersionFragment;
    this.mainMessageFragment = mainMessageFragment;
    this.detailsFragment = detailsFragment;
  }//end of constructor

  public String getErrorPageTitle() {
    return errorPageTitle;
  }//end of getErrorPageTitle()

  public String getMessageIDFragment() {
    return messageIDFragment;
  }//end of getMessageIDFragment()

  public String getCorrectionHintsFragment() {
    return correctionHintsFragment;
  }//end of getCorrectionHintsFragment()

  public String getTsgUrlFragment() {
    return tsgUrlFragment;
  }//end of getTsgUrlFragment()

  public String getGenerateErrorReportFragment() {
    return generateErrorReportFragment;
  }//end of getGenerateErrorReportFragment()

  public String getHeadBodyFragment() {
    return headBodyFragment;
  }//end of getHeadBodyFragment()

  public String getBodyBeginFragment() {
    return bodyBeginFragment;
  }//end of getBodyBeginFragment()

  public String getResponseCodeServerVersionFragment() {
    return responseCodeServerVersionFragment;
  }//end of getResponseCodeServerVersionFragment()

  public String getMainMessageFragment() {
    return mainMessageFragment;
  }//end of getMainMessageFragment()

  public String getDetailsFragment() {
    return detailsFragment;
  }//end of getDetailsFragment()

  public String getAdditionalDetailsFragment() {
    return additionalDetailsFragment;
  }//end of getAdditionalDetailsFragment()

  public void setErrorPageTitle(String errorPageTitle) {
    this.errorPageTitle = errorPageTitle;
  }//end of setErrorPageTitle(String errorPageTitle)

  public void setMessageIDFragment(String messageIDFragment) {
    this.messageIDFragment = messageIDFragment;
  }//end of setMessageIDFragment(String messageIDFragment)

  public void setCorrectionHintsFragment(String correctionHintsFragment) {
    this.correctionHintsFragment = correctionHintsFragment;
  }//end of setCorrectionHintsFragment(String correctionHintsFragment) 

  public void setTsgUrlFragment(String tsgUrlFragment) {
    this.tsgUrlFragment = tsgUrlFragment;
  }//end of setTsgUrlFragment(String tsgUrlFragment)

  public void setGenerateErrorReportFragment(String generateErrorReportFragment) {
    this.generateErrorReportFragment = generateErrorReportFragment;
  }//end of setGenerateErrorReportFragment(String generateErrorReportFragment)

  public void setHeadBodyFragment(String headBodyFragment) {
    this.headBodyFragment = headBodyFragment;
  }//end of setHeadBodyFragment(String headBodyFragment)

  public void setBodyBeginFragment(String bodyBeginFragment) {
    this.bodyBeginFragment = bodyBeginFragment;
  }//end of setBodyBeginFragment(String bodyBeginFragment)

  public void setResponseCodeServerVersionFragment(String responseCodeServerVersionFragment) {
    this.responseCodeServerVersionFragment = responseCodeServerVersionFragment;
  }//end of setResponseCodeServerVersionFragment(String responseCodeServerVersionFragment)

  public void setMainMessageFragment(String mainMessageFragment) {
    this.mainMessageFragment = mainMessageFragment;
  }//end of setMainMessageFragment(String mainMessageFragment)

  public void setDetailsFragment(String detailsFragment) {
    this.detailsFragment = detailsFragment;
  }//end of setDetailsFragment(String detailsFragment)

  public void setAdditionalDetailsFragment(String additionalDetailsFragment) {
    this.additionalDetailsFragment = additionalDetailsFragment;
  }//end of setAdditionalDetailsFragment(String additionalDetailsFragment)
  
}//end of class
