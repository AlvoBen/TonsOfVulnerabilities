package com.sap.security.api;

/**
 * This exception indicates that an attribute value which should be 
 * set for a attribute already exists. 
 */
public class AttributeValueAlreadyExistsException extends UMException 
{
	
	private static final long serialVersionUID = -8690016673720123783L;

	/**
	 * Constructs a new AttributeValueAlreadyExistsException
	 * @param   message   the detail message.
	 * @param   nestedException   the root exception.
	 */
	public AttributeValueAlreadyExistsException(
		Throwable nestedException,
		String message) {
		super(nestedException, message);
	}

	/**
	 * Constructs a new AttributeValueAlreadyExistsException
	 * @param   nestedException   the root exception.
	 */
	public AttributeValueAlreadyExistsException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * Constructs a new AttributeValueAlreadyExistsException
	 * @param   message   the detail message.
	 */
	public AttributeValueAlreadyExistsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new AttributeValueAlreadyExistsException
	 */
	public AttributeValueAlreadyExistsException() {
		super();
	}

}
