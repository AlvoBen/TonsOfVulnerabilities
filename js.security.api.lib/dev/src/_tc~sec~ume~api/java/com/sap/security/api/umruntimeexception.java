package com.sap.security.api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the superclass of all runtime exceptions employed in the user
 * management; it indicates error conditions that can occur during the normal
 * operation of the JVM.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public class UMRuntimeException
    extends RuntimeException {
	
	private static final long serialVersionUID = -3185783543439221920L;
	
    /** Message Buffer */
    protected transient List mMessageBuffer;
    private Throwable m_throwable;


    public UMRuntimeException (Throwable nestedException, String message) {
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
	else if (nestedException instanceof UMRuntimeException)
	{
        	//copy message buffer of nested exception
        	Iterator iter=((UMRuntimeException)nestedException).getMessages(true);
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

    public UMRuntimeException (Throwable nestedException) {
	this(nestedException, nestedException.getMessage());
    }


    public UMRuntimeException (String message) {
        super(message);
    }


    public UMRuntimeException () {
        super();
    }


    public Throwable getNestedException () {
        return getCause();
    }

}
