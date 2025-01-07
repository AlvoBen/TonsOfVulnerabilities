/*
 * Created on 09.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.security.api;

import java.util.Collection;

/**
 * @author d032841
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CertificateAlreadyInUseException extends UMException {

	private static final long serialVersionUID = 8541337136083131441L;
	
	private Collection mOwners;
	
	/**
	 * Constructs a new AttributeValueAlreadyExistsException
	 * @param   message   the detail message.
	 * @param   nestedException   the root exception.
	 */
	public CertificateAlreadyInUseException(
		Throwable nestedException,
		String message) {
		super(nestedException, message);
	}

	/**
	 * Constructs a new AttributeValueAlreadyExistsException
	 * @param   nestedException   the root exception.
	 */
	public CertificateAlreadyInUseException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * Constructs a new AttributeValueAlreadyExistsException
	 * @param   message   the detail message.
	 */
	public CertificateAlreadyInUseException(String message) {
		super(message);
	}

	/**
	 * Constructs a new AttributeValueAlreadyExistsException
	 */
	public CertificateAlreadyInUseException() {
		super();
	}
	
	/**
	 * Stores the owners who already own this certificate.
	 * @param owners The owners to be stored.
	 */
	public void setCertificateOwners(Collection owners){
		mOwners = owners;
	}
	
	/**
	 * Returns the owners who already own the certificate.
	 * 
	 * @return The unique IDs of accounts who already own the certificate or null.
	 */
	public Collection getCertificateOwners()
	{
		return mOwners;
	}


}
