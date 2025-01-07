package com.sap.security.api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/*******************************************************************************
 * This is the superclass of all checked exceptions employed in the user
 * management; it indicates error conditions that reasonable applications
 * might want to catch.
 * 
 * @author d026337
 * @version 2.0
 * 
 ******************************************************************************/
public class UMException extends Exception {

    private static final long serialVersionUID = -7017344532462046873L;
    
    private static final String GLUE_TEXT = ": ";
    private static final String NO_MESSAGE_TEXT = "(No text available)";

    /** Nested exception for backwards compatibility with NW04s (federated portal) */
    protected Throwable m_throwable;
    
    /** Message Buffer */
    protected transient List mMessageBuffer;

    /* Recursion preventing indicators. See comment in getLocalizedMessage() */
    private boolean _getLocalizedMessageAlreadyActive = false;
    private boolean _getMessageAlreadyActive = false;

    /** 
     * Constructor with nested exception and additional explanation text.
     * 
     * @param 
     *   nestedException
     *     The exception that is wrapped into the PersistenceException.
     * 
     * @param 
     *   message
     *     A String with the message of the PersistenceException. Callers
     *     should not repeat the message text of the nested exception becasue
     *     its message is already printed out automatically.
     */
    public UMException(Throwable nestedException, String message) {
        super(message, nestedException);
        m_throwable = nestedException;
        if (nestedException instanceof UMException)
        {
            //copy message buffer of nested exception
            Iterator iter=((UMException)nestedException).getMessages(true);
            if (iter != null)
            {
                while (iter.hasNext())
                {
                    this.addMessage((IMessage)iter.next());
                }
            }
        }
    }
    
    /**
     * Adds a message to the message buffer of this exception.
     * @param message The message to add at the end of the message buffer.
     */
    public void addMessage(IMessage message)
    {
        if (mMessageBuffer == null)
        {
            mMessageBuffer = new LinkedList();
        }
        else
        {
            if (mMessageBuffer.contains(message))
            {
                return;
            }
        }
        mMessageBuffer.add(message);
    }

    /**
     * Constructor for PersistenceException wrapping another exception.
     * 
     * @param 
     *   nestedException
     *     The wrapped exception.
     */
    public UMException(Throwable nestedException) {
        this(nestedException,nestedException.getMessage());
    }

    public UMException(String message) {
        super(message);
    }

    /**
     * This constructor should not be used because it does not reveal 
     * any error information to the caller.
     */
    public UMException() {
        super();
    }

    /**
     * Return the nested exception.
     * 
     * @return
     *   The wrapped exception, or null if there is none.
     */
    public Throwable getNestedException() {
        return m_throwable;
    }

    /**
     * Returns the message text of the exception and of all nested
     * exceptions in concatenated form. 
     * 
     * The message texts of this exception and of the nested exceptions
     * are determined by calling method getLocalizedMessage() on the 
     * respective objects.
     * 
     * If the method does not return a useful content (which might be 
     * the case for faulty implementations of exceptions), then the 
     * getMessage() method is used instead. Better any information 
     * than no information at all. 
     * 
     * The concatenation recognizes if the nested exception starts
     * with the same text as the wrapping exception and in this case
     * removes the duplicate parts. This avoids duplicate appearance
     * of the same message if when filling the UMException the
     * caller fills in a nested exception and uses its texts as the 
     * message text for the wrapper itself.
     * 
     * @return
     *   The most information about the exception and the nested
     *   exception as possible. 
     */
    public String getLocalizedMessage() {

        /* Exit if already called in previous level of call stack */
        if (_getLocalizedMessageAlreadyActive) {

            return null;

        }

        /* Read own message text with getLocalizedMessage() */
        String ownMessage;

        /* 
         * Get localized message text from the super class. 
         * 
         * The indicator is set to indicate that this method was called. 
         * If due to delegation of methods the call comes back 
         * to this method, it returns immedeately to avoid recursion.
         */
        try {

            _getLocalizedMessageAlreadyActive = true;

            ownMessage = super.getLocalizedMessage();

            /* If nothing found, try getMessage() instead */
            if (isUsable(ownMessage) == false) {
                ownMessage = super.getMessage();
            }

            /* If still nothing found, use default text */
            if (isUsable(ownMessage) == false) {
                ownMessage = NO_MESSAGE_TEXT;
            }

        } finally {

            _getLocalizedMessageAlreadyActive = false;

        }

        /* 
         * If nested exception not present, we are through, otherwise 
         * get nested text and concatenate. Exception: This exception
         * is currently already in the call stack. In this case the 
         * concatenation will be done by the outermost exception. 
         */
        if ((m_throwable == null)
            || _getLocalizedMessageAlreadyActive
            || _getMessageAlreadyActive) {

            return ownMessage;

        } else {

            /* Get nested text */
            String nestedMessage = m_throwable.getLocalizedMessage();

            if (isUsable(nestedMessage) == false) {
                nestedMessage = m_throwable.getMessage();
            }

            if (isUsable(nestedMessage) == false) {
                nestedMessage = NO_MESSAGE_TEXT;
            }

            return concatenate(ownMessage, nestedMessage);

        }

    }

    /**
     * The same as getLocalizedMessage(), but using getMessage() first
     * and if nothing delivered from there then using getLocalizedMessage().
     * 
     * @see UMException#getLocalizedMessage()
     */
    public String getMessage() {

        /* Exit if already called in previous level of call stack */
        if (_getMessageAlreadyActive) {

            return null;

        }

        /* Read own message text with getLocalizedMessage() */
        String ownMessage;

        try {

            _getMessageAlreadyActive = true;

            ownMessage = super.getMessage();

            /* If nothing found, try getMessage() instead */
            if (isUsable(ownMessage) == false) {
                ownMessage = super.getLocalizedMessage();
            }

            /* If still nothing found, use default text */
            if (isUsable(ownMessage) == false) {
                ownMessage = NO_MESSAGE_TEXT;
            }

        } finally {

            _getMessageAlreadyActive = false;

        }

        /* 
         * If nested exception not present, we are through, otherwise 
         * get nested text and concatenate. Exception: This exception
         * is currently already in the call stack. In this case the 
         * concatenation will be done by the outermost exception. 
         */
        if ((m_throwable == null)
            || _getLocalizedMessageAlreadyActive
            || _getMessageAlreadyActive) {

            return ownMessage;

        } else {

            /* Get nested text */
            String nestedMessage = m_throwable.getMessage();

            if (isUsable(nestedMessage) == false) {
                nestedMessage = m_throwable.getLocalizedMessage();
            }

            if (isUsable(nestedMessage) == false) {
                nestedMessage = NO_MESSAGE_TEXT;
            }

            return concatenate(ownMessage, nestedMessage);

        }

    }

    /**
     * Checks the usability of a message text. 
     * 
     * A message is considered usable if it is not "null" and 
     * not the empty string. 
     * 
     * @param 
     *   message
     *     A String object with the message.
     * 
     * @return
     *   Boolean indicator specifying whether a message is usable. 
     */
    private static boolean isUsable(String message) {

        if ((message != null) && (message.trim().length() > 0)) {

            return true;

        } else {

            return false;

        }

    }

    /**
     * Concatenate own message and nested Message with ": ".
     * If the nested message starts with the same text as the own 
     * message, the identical parts are cut off.
     * 
     * @param 
     *   ownMessage
     *     String with the message of the wrapper exception. 
     * 
     * @param 
     *   nestedMessage
     *     String with the message of the nested exception.
     * 
     * @return
     *   Concatenated message, omitting duplicates.
     */
    private static String concatenate(
        String ownMessage,
        String nestedMessage) {

        String usedPartOfNested = nestedMessage;

        /* If necessary, remove duplicate part of nested message */
        while (usedPartOfNested.startsWith(ownMessage)) {

            /* Start after the identical part */
            usedPartOfNested = usedPartOfNested.substring(ownMessage.length());

            /* If nested text contains glue text, remove this, too */
            if (usedPartOfNested.startsWith(GLUE_TEXT)) {

                usedPartOfNested =
                    usedPartOfNested.substring(GLUE_TEXT.length());

            }

        }

        /* Return concatenation, if nested message still has content */
        if (usedPartOfNested.trim().length() > 0) {

            return ownMessage + GLUE_TEXT + usedPartOfNested;

        } else {

            return ownMessage;

        }

    }
    
    /**
     * Returns an iterator which contains the {@link IMessage} objects assigned to this {@link IPrincipal} or
     * <code>null</code> if no messages are assigned. If the method is called with parameter <code>true</code>, 
     * every subsequent call will return <code>null</code> if no new message was assigned to this {@link IPrincipal}.
     * @param clearPermanentMessages Specifies whether permanent messages with life time {@link IMessage#LIFETIME_PERMANENT} should be removed from the message buffer.
     * @return The messages assigned to this {@link IPrincipal} object or <code>null</code>.
     */
    public java.util.Iterator getMessages(boolean clearPermanentMessages)
    {
        if (mMessageBuffer != null)
        {
            java.util.Iterator result = mMessageBuffer.iterator();
            if (clearPermanentMessages)
            {
                mMessageBuffer = null;
            }
            return result;
        }
        return null;
    }
    
//    /**
//     * Quick demonstration how this exception works
//     */
//    public static void main(String[] args) {
//
//        Exception[] exceptions =
//            new Exception[] {
//                new UMException(),
//                new UMException("Only own message"),
//                new UMException(
//                    new NullPointerException("Only nested exception")),
//                new UMException(
//                    new NullPointerException("Nested message"),
//                    "Own message"),
//                new Exception("Outer exception", 
//                    new UMException(
//                        new Exception("Inner exception"), "Wrapped in UMException"))
//                };
//
//        for (int i = 0; i < exceptions.length; i++) {
//
//            System.err.println(
//                "Exception "
//                    + i
//                    + " getMessage()         : "
//                    + exceptions[i].getMessage());
//
//            System.err.println(
//                "Exception "
//                    + i
//                    + " getLocalizedMessage(): "
//                    + exceptions[i].getLocalizedMessage());
//
//            System.err.println("Stack trace: ");
//            exceptions[i].printStackTrace();
//            System.err.println("\n");
//
//        }
//
//    }

}