package com.tssap.dtr.client.lib.protocol.ssl.util;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;

import com.sap.security.core.server.https.Utils;
import com.sap.tc.logging.Location;

/**
 * Helper class for the import of PKCS7 keystores.
 * PKCS7 keystore usually are used to store or transport
 * lists of certificates. In contrast to the PKCS12 keystore the cannot
 * store private keys.
 */
public class PKCS7CertificateImport implements ICertificateImport
{
	/** trace location*/
	private static Location TRACE =	Location.getLocation(PKCS7CertificateImport.class);

	public boolean hasClientCertificates()
	{
		return false;
	}

	public List getClientAliases()
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * Imports certificates from the given
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
	public KeyStore importCertificates(InputStream certStream, String password)
		throws KeyStoreException, CertificateException
	{
		char[] pwd = password != null ? password.toCharArray() : null;
		return Utils.convertPKCS7ToKeyStore(certStream, pwd);
	}

	static
	{
		Utils.addIAIKasJDK14Provider();
	}

}
