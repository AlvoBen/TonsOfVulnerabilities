package com.sap.jmx.monitoring.api;

/**
 * The <code>LdTextWithFallback</code> class represents translatable
 * texts with a default String as fallback if the text IDs can not be resolved.
 * The default text should be English.
 */
public abstract class LdTextWithFallback extends LanguageDependentText
{
  /**
   * Maximal length of a description text (120 characters).
   */  
  public static final int MAX_DESCRIPTION_TEXT_LENGTH = 120;

  private String defaultText;

  LdTextWithFallback(final String defaultText)
  {
    super();
    setDefaultText(defaultText);
  }
  
  /**
   * Constructs a new language dependent text with default String as fallback.
   * 
   * @param textID the ID of this language dependent text (max. 
   * {@link LanguageDependentText#MAX_TEXT_ID_LENGTH} characters),
   * e.g. "jmx.server.12".
   * 
   * @param defaultText the default text (max. 
   * {@link #MAX_DESCRIPTION_TEXT_LENGTH} characters,
   * truncated otherwise). The language of the default text should be English.
   * 
   * @throws IllegalArgumentException if <code>textID</code> has more than
   * {@link LanguageDependentText#MAX_TEXT_ID_LENGTH} characters.
   */
  public LdTextWithFallback(
    final String textID, 
    final String defaultText) 
    throws IllegalArgumentException
  {
  	super(textID);	
  	setDefaultText(defaultText);
  }
  
  /**
   * Returns the default text.
   * 
   * @return the default text.
   * 
   * @see #setDefaultText
   */
  public final String getDefaultText()
  {
    return defaultText;
  }

  /**
   * Sets the default text.
   * 
   * @param defaultText the default text (max. 
   * {@link #MAX_DESCRIPTION_TEXT_LENGTH} characters, truncated otherwise).
   * The language of the default text should be English.
   * 
   * @see #getDefaultText
   */
  public final void setDefaultText(final String defaultText) 
  {
    this.defaultText = defaultText;
  }
}
