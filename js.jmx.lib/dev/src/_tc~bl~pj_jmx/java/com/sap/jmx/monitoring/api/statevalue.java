package com.sap.jmx.monitoring.api;

/**
 * This class is used by <code>StateResourceMBean</code> as return type of the
 * method {@link StateResourceMBean#getState}.
 */
public final class StateValue extends LdTextWithFallback
{
	/**
	 * Alert color GREEN.
	 */
	public static final int GREEN = 1;
	/**
	 * Alert color YELLOW.
	 */
	public static final int YELLOW = 2;
	/**
	 * Alert color RED.
	 */
	public static final int RED = 3;
	
  private int alertColor;
  
  /**
   * Constructs a new <code>StateValue</code> object.
   * 
   * @param state textual description of the state (max. 
   * {@link LdTextWithFallback#MAX_DESCRIPTION_TEXT_LENGTH} characters,
   * truncated otherwise). Language is English.
   * 
   * @param alertColor alert color associated with the state (
   * {@link StateValue#GREEN}, {@link StateValue#YELLOW} or 
   * {@link StateValue#RED}).
   * 
   * @throws IllegalArgumentException if the parameters are out of range.
   */
  public StateValue(
    final String state, 
    final int alertColor) 
    throws IllegalArgumentException
  {
  	super(state);
  	setAlertColor(alertColor);
  }
  
  /**
   * Constructs a new <code>StateValue</code> object.
   * 
   * @param textID the ID of language dependent state text (max. 
   * {@link LanguageDependentText#MAX_TEXT_ID_LENGTH} characters).
   * 
   * @param state textual description of the state (max. 
   * {@link LdTextWithFallback#MAX_DESCRIPTION_TEXT_LENGTH} characters,
   * truncated otherwise). Language is English.
   * 
   * @param alertColor alert color associated with the state (
   * {@link StateValue#GREEN}, {@link StateValue#YELLOW} or 
   * {@link StateValue#RED}).
   * 
   * @throws IllegalArgumentException if the parameters are out of range.
   */
  public StateValue(
    final String textID, 
    final String state, 
    final int alertColor) 
    throws IllegalArgumentException
  {
		super(textID, state);
  	setAlertColor(alertColor);
  }
  
  /**
   * Returns the alert color.
   * 
   * @return the alert color
   */
  public final int getAlertColor()
  {
    return alertColor;
  }
  
  private void setAlertColor(
    final int alertColor) 
    throws IllegalArgumentException
  {
  	if (alertColor < GREEN || alertColor > RED)
  	{
  		throw new IllegalArgumentException("Unkown alert color (" + alertColor + ").");
  	}
  	else
  	{
    	this.alertColor = alertColor;
  	}
  }
}
