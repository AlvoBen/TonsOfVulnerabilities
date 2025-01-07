
package com.tssap.dtr.client.lib.protocol.ssl;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;

import com.sap.security.core.server.https.SecureConnectionFactory;

/**
 * Wrapper for a socket factory based on the IAIK JCE/SSL providers. 
 */
public final class IAIKSecureSocketProvider implements ISecureSocketProvider {
	
	private SecureConnectionFactory factory;		
	
	/**
	 * Creates a new SSL socket provider supporting host authentication.
	 * The keystore is used to look up for certificates of trusted hosts.  
	 * @param trustStore  a keystore with trusted certificates 
	 * for server authentication.
	 */
	public IAIKSecureSocketProvider(KeyStore trustStore) {
		factory = new SecureConnectionFactory(trustStore, null);
		if (trustStore == null) {
			factory.setIgnoreServerCertificate(true);
		}
	}

	/**
	 * Creates a new SSL socket provider supporting client and host authentication.
	 * The keystore is used to look up certificates of trusted hosts, client
	 * certificates and the client's private keys. 
	 * @param trustAndKeyStore  a keystore with trusted certificates 
	 * for server authentication, client certificates and client's private keys.
	 * @param password  the password used to access the private keys in the keystore
	 */
	public IAIKSecureSocketProvider(KeyStore trustAndKeyStore, String password) {
		factory = new SecureConnectionFactory(trustAndKeyStore, null, trustAndKeyStore, (password!=null)? password.toCharArray(): null);
		if (trustAndKeyStore == null) {
			factory.setIgnoreServerCertificate(true);
		}
	}

	/**
	 * Creates a new SSL socket provider supporting client and host authentication.
	 * The first keystore is used to look up certificates of trusted hosts,
	 * the second is used to look up certificates and private keys of the client.
	 * Note, <code>trustStore</code> and <code>keyStore> may point to the same
	 * keystore.
	 * @param trustStore  a keystore with trusted certificates for server authentication
	 * @param keyStore  a keystore used for client authentication
	 * @param password  the password used to access the private keys in the keystore
	 */
	public IAIKSecureSocketProvider(KeyStore trustStore, KeyStore keyStore, String password) {
		factory = new SecureConnectionFactory(trustStore, null, keyStore, (password!=null)? password.toCharArray(): null);
		if (trustStore == null) {
			factory.setIgnoreServerCertificate(true);
		}		
	}
	
	/**
	 * Creates a new SSL socket provider based on the truststore and keystore of
	 * a SAP J2EE Server. Uses JNDI to access the specified keystores.
	 * @param trustStore   the name of an engine keystore with trusted certificates 
	 * for server authentication
	 * @param keyStore  the name of an engine keystore providing client certificates 
	 * and private keys.  Note, <code>trustStore</code> and <code>keyStore> may 
	 * point to the same engine keystore.
	 */	
	public IAIKSecureSocketProvider(String trustStore, String keyStore) {
		factory = new SecureConnectionFactory(trustStore, keyStore);
		if (trustStore == null) {
			factory.setIgnoreServerCertificate(true);
		}
	}	
	
	/**
	 * Creates a SSL socket for the given host. 
	 * @param host   the host to connect to
	 * @param port	 the port to connect to
	 * @return a SSL socket
	 * @throws IOException  if creation of the socket failed.
	 */	
	public Socket createSocket(String host, int port) throws IOException {
		return factory.createSocket(host, port);
	}

	/**
	 * Creates a SSL socket for the given host. 
	 * @param host   the host to connect to
	 * @param port	 the port to connect to
	 * @param clientAlias   the client's identity used to
	 * select a certificate from the client's keystore. 
	 * @return a SSL socket
	 * @throws IOException  if creation of the socket failed.
	 */
	public Socket createSocket(String host, int port, String clientAlias) throws IOException {
		if (clientAlias==null) {
			return factory.createSocket(host, port);
		}
		return factory.createSocket(host, port, new String[]{clientAlias});
	}


	/**
	 * Creates a SSL wrapper for an existing socket. The given host and port parameters
	 * determines the destination server to contact.
	 * Used for SSL communication through proxies.
	 * @param socket  the socket to wrap with SSL
	 * @param host   the host to connect to
	 * @param port	 the port to connect to
	 * @return a SSL socket
	 * @throws IOException  if creation of the socket failed. 
	 */
	public Socket createSocket(Socket socket, String host, int port) throws IOException {
		return factory.createSocket(host, port, socket);
	}

	/**
	 * Creates a SSL wrapper for an existing socket. The given host and port parameters
	 * determines the destination server to contact.
	 * Used for SSL communication through proxies.
	 * @param socket  the socket to wrap with SSL
	 * @param host   the host to connect to
	 * @param port	 the port to connect to
	 * @param clientAlias   the client's identity used to
	 * select a certificate from the client's keystore.  
	 * @return a SSL socket
	 * @throws IOException  if creation of the socket failed. 
	 */
	public Socket createSocket(Socket socket, String host, int port, String clientAlias) throws IOException {
		if (clientAlias==null) {
			return factory.createSocket(host, port, socket);
		}
		return factory.createSocket(host, port, socket, new String[]{clientAlias});
	}
}
