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
 * Helper class for the import of single X509 certificates.
 */
public class X509CertificateImport implements ICertificateImport
{
	/** trace location*/
	private static Location TRACE =	Location.getLocation(X509CertificateImport.class);

	public boolean hasClientCertificates()
	{
		return false;
	}

	public List getClientAliases()
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * Imports a X509 certificate from the given stream.
	 * Note, the stream must only contain a single certificate. 
	 * @param certStream  the stream to read from.
	 * @param password  the password is not used in this case and
	 * should be null.
	 * @return a keystore containg a single X509 certificate.
	 * @throws KeyStoreException  if the in memoria keystore could not be
	 * created or there was a problem while storing the certificate
	 * @throws CertificateException  if the input stream contained
	 * an invalid certificate.
	 */
	public KeyStore importCertificates(InputStream certStream, String password)
		throws KeyStoreException, CertificateException
	{
		char[] pwd = password != null ? password.toCharArray() : null;
		return Utils.convertCertificateToKeyStore(certStream, pwd);
	}

	static
	{
		Utils.addIAIKasJDK14Provider();
	}

}
