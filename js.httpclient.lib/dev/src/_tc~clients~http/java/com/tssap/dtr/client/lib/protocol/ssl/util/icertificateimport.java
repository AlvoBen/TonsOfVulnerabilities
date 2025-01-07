package com.tssap.dtr.client.lib.protocol.ssl.util;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.List;

/**
 * Interface for the import of certificates from a keystore.
 */
public interface ICertificateImport {
	
	/**
	 * Checks whether the recently imported keystore contained
	 * client certificates.
	 * @return true, if the keystore contained client
	 * certificates.
	 */
	boolean hasClientCertificates();
	
	/**
	 * Returns a list of client aliases identifying
	 * client certificates in the recently imported keystore
	 * @return a list of strings
	 */
	List getClientAliases();

	/**
	 * Imports certificates and probably private keys from the given
	 * stream. The passwort is used to decyrpt the keystore.
	 * if necessary.
	 * @param certStream  the stream to read from.
	 * @param password  the password used to decrypt the stream.
	 * @return a keystore containg certificates and private keys.
	 * @throws KeyStoreException  if the in memoria keystore could not be
	 * created or there was a problem while storing certificates
	 * @throws CertificateException  if the input stream contained
	 * invalid certificates.
	 */
	KeyStore importCertificates(InputStream certStream, String password) 
		throws KeyStoreException, CertificateException;

}
