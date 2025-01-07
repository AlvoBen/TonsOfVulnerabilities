package com.sap.jmx.monitoring.api;

/**
 * This interface is used to report a text value. There is no further monitoring logic 
 * associated with this type of resource mbean.
 */
public interface TextResourceMBean extends ResourceMBean
{
  /**
   * Maximal length of a text line (255 characters).
   */  
  public static final int MAX_TEXT_LENGTH = 255;

  /**
   * Returns a text line. The length of this text must not exceed 
   * {@link #MAX_TEXT_LENGTH} characters, otherwise the text is 
   * automatically truncated to {@link #MAX_TEXT_LENGTH} characters. 
   * @return a text line.
   */
  public String getText();
}
