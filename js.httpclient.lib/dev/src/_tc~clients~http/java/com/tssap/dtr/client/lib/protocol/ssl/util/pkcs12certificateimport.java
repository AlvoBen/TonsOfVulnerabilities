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
 * Helper class for the import of PKCS12 keystores.
 * PKCS12 keystore usually are used to store or transport
 * private keys together with their certificate chains.
 */
public class PKCS12CertificateImport implements ICertificateImport
{
	private static Location TRACE =	Location.getLocation(PKCS12CertificateImport.class);

	private List _clientAliases = Collections.EMPTY_LIST;

	public boolean hasClientCertificates()
	{
		return !_clientAliases.isEmpty();
	}

	public List getClientAliases()
	{
		return _clientAliases;
	}

	public KeyStore importCertificates(InputStream certStream, String password)
		throws KeyStoreException, CertificateException
	{
		char[] pwd = password != null ? password.toCharArray() : null;
		KeyStore ks = Utils.convertPKCS12ToKeyStore(certStream, pwd); 
		_clientAliases = Utils.getClientAliases(ks);
		return ks;
	}

	static
	{
		Utils.addIAIKasJDK14Provider();
	}
}
