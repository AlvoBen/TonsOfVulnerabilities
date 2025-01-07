package com.sap.jms;

public class JMSProviderException extends javax.jms.JMSException {
	
  public JMSProviderException(String message) {
    super(message);
  }

  public JMSProviderException(String message, Throwable cause) {  
    super(message);
    super.initCause(cause);
  }

  public JMSProviderException(Throwable cause) {
    this("", cause);
  }
}
