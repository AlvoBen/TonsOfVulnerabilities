package com.sap.engine.services.httpserver.interfaces;

/**
 * This class holds some supportability info that can be used later 
 * when generating error reports triggered by the end user.
 * 
 * @author Violeta Georgieva
 */
public class SupportabilityData {
  /**
   * Indicates whether user action is needed when 'Generate Error Report' feature is enabled. 
   * Sometimes when an error page is return it is because for example the server is overloaded or
   * something like this, in such cases it is not needed to show a button for generating error report,
   * because there is no chance for this special request to be processed (e.g. there is no free thread
   * to process this request).  
   */
  private boolean userActionNeeded = false;
  private String stackTrace = "";
  private String logId = "";
  private String messageId = "";
  private String dcName = "";
  private String csnComponent = "";
  private String correctionHints = "";
  
  public SupportabilityData () {
  }//end of constructor
  
  public SupportabilityData (boolean userActionNeeded, String stackTrace, String logId) {
    this.userActionNeeded = userActionNeeded;
    this.stackTrace = stackTrace != null ? stackTrace : "";
    this.logId = logId != null ? logId : "";
    if (!this.stackTrace.equals("")) { 
      init();
    }
  }//end of constructor

  public SupportabilityData (boolean userActionNeeded, String stackTrace, String logId, String messageId, String dcName, String csnComponent) {
    this.userActionNeeded = userActionNeeded;
    this.stackTrace = stackTrace != null ? stackTrace : "";
    this.logId = logId != null ? logId : "";
    this.messageId = messageId != null ? messageId : "";
    this.dcName = dcName != null ? dcName : "";
    this.csnComponent = csnComponent != null ? csnComponent : "";
  }//end of constructor

  public boolean isUserActionNeeded() {
    return userActionNeeded;
  }//end of isUserActionNeeded()

  public void setUserActionNeeded(boolean userActionNeeded) {
    this.userActionNeeded = userActionNeeded;
  }//end of setUserActionNeeded(boolean userActionNeeded)

  public String getStackTrace() {
    return stackTrace;
  }//end of getStackTrace()

  public void setStackTrace(String stackTrace) {
    this.stackTrace = stackTrace;
  }//end of setStackTrace(String stackTrace)

  public String getLogId() {
    return logId;
  }//end of getLogId()

  public void setLogId(String logId) {
    this.logId = logId != null ? logId : "";
  }//end of setLogId(String logId)

  public String getMessageId() {
    return messageId;
  }//end of getMessageId()

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }//end of setMessageId(String messageId)

  public String getDcName() {
    return dcName;
  }//end of getDcName()

  public void setDcName(String dcName) {
    this.dcName = dcName;
  }//end of setDcName(String dcName)

  public String getCsnComponent() {
    return csnComponent;
  }//end of getCsnComponent()

  public void setCsnComponent(String csnComponent) {
    this.csnComponent = csnComponent;
  }//end of setCsnComponent(String csnComponent)

  public String getCorrectionHints() {
    return correctionHints;
  }//end of getCorrectionHints()

  public void setCorrectionHints(String correctionHints) {
    this.correctionHints = correctionHints;
  }//end of setCorrectionHints(String correctionHints)

  /**
   * Parses the message ID, DC name and CSN component from the throwable stacktrace.
   */
  private void init() {
    //Always search for the deepest message ID, this can appear to be the root cause of the problem.
    int index = stackTrace.lastIndexOf("com.sap.ASJ.");
    if (index > -1) {
      int a = stackTrace.indexOf(" ", index);
      if (a > -1) {
        messageId = stackTrace.substring(index, a);
        if (messageId.endsWith("at")) {
          messageId = messageId.substring(0, messageId.length() - 2);
        }
      }
    } 
  
    //Then try to get the DC name and CSN component if there are any.
    if (index > -1) {
      //If there is already message ID then try to get also the DC name and CSN component. 
      index = stackTrace.indexOf("(Failed in component:", index + 1 + messageId.length());
    } else {
      //If there is no message ID then search from the beginning for DC name and CSN component. 
      index = stackTrace.lastIndexOf("(Failed in component:");
    }

    if (index > -1) {
      int b = stackTrace.indexOf(")", index);
      if (b > -1) {
        String tmp = stackTrace.substring(index + 22, b);
        int c = tmp.indexOf(",", 0);
        if (c > -1) { 
          dcName = tmp.substring(0, c);
          csnComponent = tmp.substring(dcName.length() + 2);
        }
      }
    } 
  }//end of init()
  
}//end of class
