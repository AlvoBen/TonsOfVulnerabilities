package com.sap.engine.interfaces.security;

/**
 *  Context attached to the thread with identifier "security_modification".
 *  It is accessible as ContextObject in the ThreadContext object under "security_modification".
 *
 * @author  Ekaterina Zheleva
 * @version 6.30
 *
 * @see com.sap.engine.frame.core.thread.ContextObject
 * @see com.sap.engine.frame.core.thread.ThreadContext
 */
public interface SecurityModificationContextObject {


  /**
   *  Name identifier in ThreadContext.
   *
   *  Value is "security_modification".
   */
  public final static String NAME = "security_modification";

  /**
   *  Associates a configuration to the thread.
   *
   * @param  configuration  an instance of Configuration or null.
   */
  public void setConfiguration(Object configuration);

  /**
   *  Associates a ModificationContext to the thread.
   *
   * @param  modificationContext  an instance of ModificationContext or null.
   */
  public void setModificationContext(Object modificationContext);
  
	/**
   *  Associates an AppConfigurationHandler to the thread.
   *
   * @param  appConfigurationHandler  an instance of AppConfigurationHandler or null.
   */
  public void setAppConfigurationHandler(Object appConfigurationHandler);

  /**
   *  Returns the thread's associated Configuration.
   *
   * @return  an instance of Configuration or null.
   */
  public Object getConfiguration();

  /**
   *  Returns the thread's associated ModificationContext.
   *
   * @return  an instance of ModificationContext or null.
   */
  public Object getModificationContext();
  
	/**
   *  Returns the thread's associated AppConfigurationHandler.
   *
   * @return  an instance of AppConfigurationHandler or null.
   */
  public Object getAppConfigurationHandler();
}