package com.sap.jmx.monitoring.api;

/**
 * The <code>LanguageDependentText</code> class represents translatable
 * texts. Each text belongs to a class and has a unique ID (XMI format). If no 
 * class is specified the class of the J2EE Engine is used as default.
 */
public abstract class LanguageDependentText
{
  /**
   * Maximal length of a text ID (30 characters).
   */  
  public static final int MAX_TEXT_ID_LENGTH = 30;
  /**
   * Maximal length of a text class (16 characters).
   */  
  public static final int MAX_TEXT_CLASS_LENGTH = 16;
  
  /* Must be equal to the definition in 6.30 Mapping */
  private static final String DEFAULT_CLASS = "SAP J2EE Engine";

  private String textID;
  private String textClass = DEFAULT_CLASS;  
  
  LanguageDependentText()
  {
    textID = null;
  }
  
  /**
   * Constructs a new language independent text.
   * 
   * @param textID the ID of this language dependent text (max. 
   * {@link #MAX_TEXT_ID_LENGTH} characters), e.g. "jmx.server.12".
   * 
   * @throws IllegalArgumentException if <code>textID</code> has more than 
   * {@link #MAX_TEXT_ID_LENGTH} characters.
   */
  public LanguageDependentText(final String textID) 
    throws IllegalArgumentException
  {
    setTextID(textID);
  }
  
  /**
   * Constructs a new language independent text.
   * 
   * @param textID the ID of this language dependent text (max. 
   * {@link #MAX_TEXT_ID_LENGTH} characters), e.g. "jmx.server.12".
   * 
   * @param textClass the class of this language dependent text (max. 
   * {@link #MAX_TEXT_CLASS_LENGTH} characters), e.g. "SAP CRM ISA".
   * 
   * @throws IllegalArgumentException if <code>textID</code> has more than 
   * {@link #MAX_TEXT_ID_LENGTH} characters or <code>textClass</code> 
   * has more than {@link #MAX_TEXT_CLASS_LENGTH} characters.
   */
  public LanguageDependentText(
    final String textID,
    final String textClass) 
    throws IllegalArgumentException
  {
    setTextID(textID);
    setTextClass(textClass);
  }
  
  /**
   * Returns  the ID of this language dependent text.
   * 
   * @return  the ID of this language dependent text.
   * 
   * @see #setTextID
   */
  public final String getTextID()
  {
    return textID;
  }

  /**
   * Returns the class of this language dependent text.
   * 
   * @return the class of this language dependent text.
   * 
   * @see #setTextClass
   */
  public final String getTextClass()
  {
    return textClass;
  }

  /**
   * Sets the ID of this language dependent text.
   * 
   * @param textID the ID of this language dependent text (max. 
   * {@link #MAX_TEXT_ID_LENGTH} characters), e.g. "jmx.server.12".
   * 
   * @throws IllegalArgumentException if <code>textID</code> has more than 
   * {@link #MAX_TEXT_ID_LENGTH} characters.
   * 
   * @see #getTextID
   */
  public final void setTextID(final String textID) 
    throws IllegalArgumentException
  {
    checktTextID(textID);
    
    this.textID = textID;
  }
  
  /**
   * Sets the class of this language dependent text.
   * 
   * @param textClass the class of this language dependent text (max. 
   * {@link #MAX_TEXT_CLASS_LENGTH} characters), e.g. "SAP CRM ISA".
   * 
   * @throws IllegalArgumentException if <code>textClass</code> has more than 
   * {@link #MAX_TEXT_CLASS_LENGTH} characters.
   * 
   * @see #getTextClass
   */
  public final void setTextClass(final String textClass) 
    throws IllegalArgumentException
  {
    checkTextClass(textClass);
    
    this.textClass = textClass;
  }
  
  private void checktTextID(String textID)
    throws IllegalArgumentException
  {
    if (textID == null)
    {
      throw new IllegalArgumentException("Text ID is null.");
    }
    else if (textID.length() < 1)
    {
      throw new IllegalArgumentException("Text ID is empty.");
    }
    else if (textID.length() > MAX_TEXT_ID_LENGTH)
    {
      throw new 
        IllegalArgumentException(
          "Text ID has more than " + 
          MAX_TEXT_ID_LENGTH + 
          " characters.");
    }
  }
  
  private void checkTextClass(String textClass)
    throws IllegalArgumentException
  {
    if (textClass == null)
    {
      throw new IllegalArgumentException("Text class is null.");
    }
    else if (textClass.length() < 1)
    {
      throw new IllegalArgumentException("Text class is empty.");
    }
    else if (textClass.length() > MAX_TEXT_CLASS_LENGTH)
    {
      throw new 
        IllegalArgumentException(
          "Text class has more than " + 
          MAX_TEXT_CLASS_LENGTH + 
          " characters.");
    }
  }
}