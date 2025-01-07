/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.interfaces.textcontainer.recipient;




/**
*
* Interface for Text Container recipient event.
*
 * <br/><br/>Copyright (c) 2007, SAP AG
* @author  Thomas Goering
* @version 1.0
*/
public interface TextContainerRecipientChangedEvent extends TextContainerRecipientEvent
{

	final public static int TXV_CHANGED_INDUSTRIES = 1; //$NON-NLS-1$

	final public static int TXV_CHANGED_REGIONS = 2; //$NON-NLS-1$

	final public static int TXV_CHANGED_EXTENSIONS = 4; //$NON-NLS-1$

	final public static int TXV_CHANGED_LANGUAGES = 8; //$NON-NLS-1$

	final public static int TXV_CHANGED_LOCALES = 16; //$NON-NLS-1$

	final public static int TXV_CHANGED_SYSTEM_CONTEXT = 32; //$NON-NLS-1$

	final public static int TXV_CHANGED_CONTEXT_IDS = 64; //$NON-NLS-1$

	/**
	 * With this method the recipient can determine what change(s) occured.
	 * 
	 * @return Combination of constants:
	 * <br>- {@link TextContainerRecipientChangedEvent.TXV_CHANGED_INDUSTRIES}
	 * <br>- {@link TextContainerRecipientChangedEvent.TXV_CHANGED_REGIONS}
	 * <br>- {@link TextContainerRecipientChangedEvent.TXV_CHANGED_EXTENSIONS}
	 * <br>- {@link TextContainerRecipientChangedEvent.TXV_CHANGED_LANGUAGES}
	 * <br>- {@link TextContainerRecipientChangedEvent.TXV_CHANGED_LOCALES}
	 * <br>- {@link TextContainerRecipientChangedEvent.TXV_CHANGED_SYSTEM_CONTEXT}
	 * <br>- {@link TextContainerRecipientChangedEvent.TXV_CHANGED_CONTEXT_IDS}
	 * <br><br>A recipient has to check with:
	 * <pre><code>Example:
	 * 
	 *   int changed = changedEvent.getChanged();
	 * 
	 *   if (changed & TextContainerRecipientChangedEvent.TXV_CHANGED_SYSTEM_CONTEXT)
	 *     ...</code></pre>
	 */
	public int getChanged();

}
