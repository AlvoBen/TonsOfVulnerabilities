package com.sap.security.api;

import java.util.Date;
import java.util.Locale;

public interface IMessage {
	
	/*Message Type Constants*/
	public static final int TYPE_ERROR   = 1;/*Message text explains an error.*/
	public static final int TYPE_WARNING = 2;/*Message text explains a warning*/
	public static final int TYPE_INFO    = 4;/*Message text is informational*/
	public static final int TYPE_SUCCESS = 8;/*Message text explains a successfully completed operation. Not in use.*/

	/*Message Lifetime Constants*/
	public static final int LIFETIME_PERMANENT = 1;/*Message is valid until explicitly deleted (DEFAULT).*/
	public static final int LIFETIME_ONCE      = 2;/*Message is valid only once. After it has been displayed once, it should not appear again.*/
	public static final int LIFETIME_ONCE_TRX  = 4;/*Message is only valid in the current transaction. Not in use.*/

	/*Message Category Constants*/
	public static final int CATEGORY_OBJECT  = 1; /*Message is related to a specific IPrincipal object*/
	public static final int CATEGORY_PROCESS = 2; /*Message is related to a process like search*/

	/**
	 * Returns the localized message text. If this is not possible,
	 * a localized generic text is returned. The generic text
	 * includes the clear text message ({@link IMessage#getMessage()})
	 * which is not a translated text and not related to the given
	 * locale object.
	 *  
	 * @param locale The locale for which the text should be returned.
	 * @return The localized message.
	 */
	public String getLocalizedMessage(Locale locale);

	/**
	 * Returns the type of the message.
	 * Possible types are: {@link IMessage#TYPE_ERROR},{@link IMessage#TYPE_WARNING},{@link IMessage#TYPE_INFO},{@link IMessage#TYPE_SUCCESS}
	 * {@link IMessage#TYPE_ERROR} means: The message text explains an error.
	 * {@link IMessage#TYPE_WARNING} means: The message text explains a warning.
	 * {@link IMessage#TYPE_INFO} means: The message text is informational.
	 * {@link IMessage#TYPE_SUCCESS} means: The message text explains a successfully completed operation. This type is not in use.
	 * @return The type of the message.
	 */
	public int getType();
	
	/**
	 * Returns the life time of the message.
	 * Possible life times are: {@link IMessage#LIFETIME_PERMANENT},{@link IMessage#LIFETIME_ONCE},{@link IMessage#LIFETIME_ONCE_TRX}
	 * {@link IMessage#LIFETIME_PERMANENT} means: Message is valid until explicitly deleted. This is the default for all messages.
	 * {@link IMessage#LIFETIME_ONCE} means: Message is valid only once. After it has been displayed once, it should not appear again.
	 * {@link IMessage#LIFETIME_ONCE_TRX} means: Message is only valid in the current transaction. This life time is not in use.
	 * @return The life time of the message.
	 */
	public int getLifeTime();
	
	/**
	 * Returns the category of the message.
	 * Possible life times are: {@link IMessage#CATEGORY_OBJECT},{@link IMessage#CATEGORY_PROCESS}
	 * {@link IMessage#CATEGORY_OBJECT} means: The message is related to a specific IPrincipal object.
	 * {@link IMessage#CATEGORY_PROCESS} means: The message is realted to a process (like search).
	 * @return The category of the message.
	 */
	public int getCategory();
	
	/**
	 * Returns the <code>java.util.Date</code> object that represents the time when the message was added to the message buffer.
	 * @return The time when the message was added to the buffer.
	 */
	public Date getTimeStamp();
	
	/**
	 * Returns a not localized clear text which might be assigned to the message, or <code>null</code>.
	 * This message is used to enrich the localized generic message ({@link IMessage#getLocalizedMessage(Locale)}.
	 * @return The not localized clear text which is assigned to this message or <code>null</code>
	 */
	public String getMessage();

	/**
	 * Returns a generated unique ID to identify the message. This GUID can be used in order to trace or log messages on client and server side.
	 * @return The generated GUID
	 */
	public String getGuid();
	
}
