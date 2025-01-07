package com.tssap.dtr.client.lib.protocol.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.ssl.util.PKCS12CertificateImport;
import com.tssap.dtr.client.lib.protocol.ssl.util.PKCS7CertificateImport;

/**
 * Manager for client and server certificates.
 */
public class Certificates {

	/** Keystore for server certificates */
	private KeyStore serverCertificates;
	/** Keystore for clien certificates and private keys */
	private KeyStore clientCertificates;
	/** Number of server certificates */

	/** Engine keystore for server certificates */
	private String serverCertStore;
	private KeyStoreType serverCertStoreType;
	
	/** Engine keystore for client certificates */
	private String clientCertStore;
	private KeyStoreType clientCertStoreType;
	
	/** password used to access the keystores */
	private String clientStorePwd;	
	private String clientKeyPwd;
	
	/** password used to access private keys in client certificates */
	private String serverStorePwd;

	/** Alias to select client certificate */
	private String clientAlias;

	/** if true, client certificates are applied to connections */
	private boolean authenticateMe = false;
	/** if true, server certificates are applied to connections */
	private boolean authenticateThem = true;

	/** true, if keystore of the engine should be used for authentication */
	private boolean useEngineStores = false;
	
	/** trace location*/
	private static Location TRACE = Location.getLocation(Certificates.class);	


	/**
	 * Keystore types supported by this certificate manager
	 */
	public static class KeyStoreType {
		private final String name;
		private KeyStoreType(String name) {
			this.name = name;
		}
		
		/** Returns a string representation of the keystore type. */
		public String toString() {
			return name;
		}
		
		/**
		 * Creates a keystore type from the given string
		 * @param type  either "PKCS7", "PKCS12", "JKS", "JCEKS" or "IAIK"
		 * (case is ignored).
		 * @return a keystore type
		 * @throws IllegalArgumentException  if no matching keystore type
		 * is found
		 */
		public static KeyStoreType valueOf(String type) {
			if (type.equalsIgnoreCase("PKCS7")) {
				return PKCS7;
			} 
			if (type.equalsIgnoreCase("PKCS12")) {
				return PKCS12;
			} 
			if (type.equalsIgnoreCase("JKS")) {
				return JKS;
			} 
			if (type.equalsIgnoreCase("JCEKS")) {
				return JCEKS;
			} 
			if (type.equalsIgnoreCase("IAIK")) {
				return IAIK;
			} 
			throw new IllegalArgumentException("Invalid keystore type");
		}
		
		/**
		 * Determines the type of the given keystore.
		 * @param keystore  the keystore
		 * @return the type of the keystore 
		 * type could not be determined.
		 */
		public static KeyStoreType typeOf(KeyStore keystore) {
			String type = keystore.getType();
			if (type.equalsIgnoreCase("jks")) {
				return JKS;
			} 
			if (type.equalsIgnoreCase("jceks")) {
				return JCEKS;
			} 
			if (type.equalsIgnoreCase("iaikkeystore")) {
				return IAIK;
			} 
			return new KeyStoreType(type);
		}
		
		/** The default Java keystore */
		public static final KeyStoreType JKS = new KeyStoreType("JKS");
		/** The keystore from the SUN JCE provider */		
		public static final KeyStoreType JCEKS = new KeyStoreType("JCEKS");
		/** The keystore from the IAIK JCE provider */
		public static final KeyStoreType IAIK = new KeyStoreType("IAIK");
		/** A PKCS7 keystore */
		public static final KeyStoreType PKCS7 = new KeyStoreType("PKCS7");
		/** A PKCS12 keystore */
		public static final KeyStoreType PKCS12 = new KeyStoreType("PKCS12");
		/** A keystore managed by the server engine */
		public static final KeyStoreType SERVER = new KeyStoreType("SERVER");
		
	}

	/**
	 * Creates a new certificate manager.
	 */
	public Certificates() {
	}
	
	/**
	 * Initializes a new certificate manager from another.
	 * Note, the keystores are NOT copied. Thus, the new
	 * certificate manager references the same keystores.
	 * @param certificates  the certificate manager to initialize
	 * from.
	 */
	public Certificates(Certificates certificates)
	{
		authenticateMe = certificates.authenticateMe;
		authenticateThem = certificates.authenticateThem;		
		useEngineStores = certificates.useEngineStores;
		serverCertStore = certificates.serverCertStore;
		serverCertStoreType = certificates.serverCertStoreType;			
		clientCertStore = certificates.clientCertStore;
		clientCertStoreType = certificates.clientCertStoreType;
		serverStorePwd = certificates.serverStorePwd;
		clientStorePwd = certificates.clientStorePwd;
		serverCertificates = certificates.serverCertificates;
		clientCertificates = certificates.clientCertificates;
		clientKeyPwd = certificates.clientKeyPwd;						
	}

	/**
	 * Sets the server certificate keystore managed by this
	 * instance.  
	 * @param certificates  a keystore containing trusted server certificates
	 * @throws KeyStoreException  if the keystores is invalid
	 */
	public void setServerCertificates(KeyStore certificates) throws KeyStoreException {		
		if (certificates.size() == 0) {
			throw new KeyStoreException("The keystore contains no valid certificates.");
		}
		serverCertificates = certificates;
		serverCertStoreType = KeyStoreType.typeOf(certificates);
		useEngineStores = false;
	}

	/**
	 * Sets the server certificate keystore managed by this
	 * instance. The certificates are read from the given input stream.
	 * The content of that stream must match the <code>KeyStoreType</code>
	 * parameter. Some keystores (like PKCS12) may require a password to
	 * decrypt the stream.
	 * @param type  the type of the keystore to read
	 * @param certStream  the input stream from which to read certificates
	 * @param password  the password used to access/decrypt the keystore stream
	 * @throws KeyStoreException if the keystore could not be imported
	 */
	public void setServerCertificates(InputStream certStream, String password, KeyStoreType type)
	throws KeyStoreException
	{
		try {		
			if (type == KeyStoreType.PKCS7) {
				setServerCertificates(new PKCS7CertificateImport().importCertificates(certStream, password)); 
			} else if (type == KeyStoreType.PKCS12) {
				setServerCertificates(new PKCS12CertificateImport().importCertificates(certStream, password));
			} else {
				KeyStore keystore = createKeyStore(type);		
				keystore.load(certStream, (password!=null)? password.toCharArray() : null);
				setServerCertificates(keystore);	
				serverCertStoreType = type;
			}
		} catch (Exception e) {
			throw new KeyStoreException("Unable to load keystore: " + e.getMessage());
		}		
	}

	/**
	 * Sets the server certificate keystore managed by this
	 * instance. The certificates are read from the given file.
	 * Tries to determine the keystore type from the extension
	 * of the given file name. Rceoginizes
	 * ".p12" and "pfx" files as PKCS12 keystores, ".iaik" files as IAIK keystores,
	 * ".jceks" files as JCEKS keystores, and ".p7b" files as PKCS7 keystores.
	 * All other files are treated as JKS keystore.<br/>
	 * Note, the import of the keystore is delayed unless it is needed.
	 * @param certStream  the input stream from which to read certificates
	 * @throws KeyStoreException if the keystore could not be imported
	 */
	public void setServerCertificates(String certFile) {
		String fileName = certFile.toLowerCase();
		KeyStoreType type = null;
		if (fileName.endsWith(".p12") || fileName.endsWith(".pfx")) {
			type = KeyStoreType.PKCS12;
		} else if (fileName.endsWith(".p7b")) {
			type = KeyStoreType.PKCS7;			
		} else if (fileName.endsWith(".iaik")) {
			type = KeyStoreType.IAIK;
		} else if (fileName.endsWith(".jceks")) {
			type = KeyStoreType.JCEKS;
		} else {
			type = KeyStoreType.JKS;
		}			
		setServerCertificates(certFile, type);
	}
	
	
	
	/**
	 * Sets the server certificate keystore managed by this
	 * instance. The content of that file must match the <code>KeyStoreType</code>
	 * parameter.<br/>
	 * Note, the import of the keystore is delayed unless it is needed.
	 * @param certStream  the input stream from which to read certificates
	 * @param type  the type of the keystore to read 
	 */
	public void setServerCertificates(String certFile, KeyStoreType type) {
		serverCertStore = certFile;
		serverCertStoreType = type;
		useEngineStores = false;		
	}
	
	/**
	 * Sets the password needed to decrypt the server certificate keystore.
	 * @param password  the password
	 */
	public void setServerCertStorePassword(String password) {
		serverStorePwd = password;
	}



	/**
	 * Sets the client certificate keystore managed by this instance. 
	 * Note, a client certificate keystore usually contains the client's 
	 * private key that is separately encrypted. 
	 * @param certificates  a keystore containing client certificates and private keys 
	 * @param keyPassword  password used to decrypt private keys in the keystore
	 * @throws KeyStoreException  if the keystores is invalid
	 */
	public void setClientCertificates(KeyStore certificates, String keyPassword) throws KeyStoreException {
		if (certificates.size() == 0) {
			throw new KeyStoreException("The keystore contains no valid certificates.");
		}
		clientCertificates = certificates;
		clientCertStoreType = KeyStoreType.typeOf(certificates);
		clientKeyPwd = keyPassword;
		useEngineStores = false;
	}


	/**
	 * Sets the client certificate keystore managed by this instance.
	 * Note, not all keystores types support safe storage of private keys. Therefore
	 * this method only supports <code>KeyStoreType.PKCS12</code>, <code>KeyStoreType.JCEKS</code>
	 * and <code>KeyStoreType.IAIK</code>, respectively.
	 * @param certStream  a stream, transporting a keystore, containing client certificates and private keys  
	 * @param password  password used to decrypt the keystore.
	 * @param keyPassword  password used to decrypt private keys in the keystore. May be the
	 * same as <code>password</code>.
	 * @param type  the type of the keystore.
	 * @throws KeyStoreException  if the keystores is invalid, could not be loaded,
	 * or the keystore type does not support safe storage of private keys.
	 */
	public void setClientCertificates(InputStream certStream, String password, String keyPassword, KeyStoreType type)
	throws KeyStoreException {
		try {		
			 if (type == KeyStoreType.PKCS12) {
				setClientCertificates(new PKCS12CertificateImport().importCertificates(certStream, password), keyPassword);
			} else {
				KeyStore keystore = null;		
			 	if (type == KeyStoreType.IAIK) {
					keystore = KeyStore.getInstance("IAIKKeyStore", "IAIK");	
			 	} else if (type == KeyStoreType.JCEKS) {
					keystore = KeyStore.getInstance("JCEKS", "SunJCE");
				} else {
					throw new KeyStoreException("The type " + type.toString() +
						" does not support safe storage of private keys.");					
				}
// OK <20.12.2004>: Should be set or not?
// In principle it is just to decrypt the store, once 
// it is decrypted the password may be thrown away...
//				clientStorePwd = password;
				keystore.load(certStream, (password!=null)? password.toCharArray() : null);
				setClientCertificates(keystore, keyPassword);
				clientCertStoreType = type;
			}
		} catch (Exception e) {
			throw new KeyStoreException("Unable to load keystore: " + e.getMessage());
		}			
	}
	
	/**
	 * Sets the client certificate keystore managed by this instance.
	 * Tries to determine the keystore from from the file extension. Rceoginizes
	 * ".p12" and "pfx" files as PKCS12 keystore, ".iaik" files as IAIK keystore
	 * and ".jceks" files as JCEKS keystores.
	 * @param certFile  name of a keystore file containing client certificates and private keys  
	 * @param type  the type of the keystore.
	 * @throws KeyStoreException  if the keystores is invalid, could not be loaded,
	 * the keystore type does not support safe storage of private keys, or the type of the
	 * keystore could not be determined. 
	 */		
	public void setClientCertificates(String certFile) throws KeyStoreException {
		String fileName = certFile.toLowerCase();
		KeyStoreType type = null;
		if (fileName.endsWith(".p12") || fileName.endsWith(".pfx")) {
			type = KeyStoreType.PKCS12;
		} else if (fileName.endsWith(".iaik")) {
			type = KeyStoreType.IAIK;
		} else if (fileName.endsWith(".jceks")) {
			type = KeyStoreType.JCEKS;
		} else {
			throw new KeyStoreException("Unable to determine type of keystore");
		}		
		setClientCertificates(certFile, type);
	}	
	
	/**
	 * Sets the client certificate keystore managed by this instance.
	 * Note, not all keystores types support safe storage of private keys. Therefore
	 * this method only supports <code>KeyStoreType.PKCS12</code>, <code>KeyStoreType.JCEKS</code>
	 * and <code>KeyStoreType.IAIK</code>, respectively.
	 * @param certFile  name of a keystore file containing client certificates and private keys  
	 * @param type  the type of the keystore.
	 */	
	public void setClientCertificates(String certFile, KeyStoreType type) {		
		clientCertStore = certFile;
		clientCertStoreType = type;
	}	
	
	/**
	 * Sets the alias name used to select a client certificate
	 * from the keystore.
	 * @param alias  the alias to use
	 */
	public void setClientAliase(String alias) {
		clientAlias = alias;
	}	
	
	/**
	 * Sets the client and server certificate keystores managed by this
	 * instance. The stores are identified by their names.<br/>
	 * Note, this method can only be used when running inside the SAP NetWeaver server.
	 * @param serverCertStore  a cert store containing trusted server certificates
	 * @param clientCertStore a cert store containing certificates the client owns 
	 */
	public void setEngineCertificateStores(String serverCertStore, String clientCertStore) {
		this.serverCertStore = serverCertStore;
		this.clientCertStore = clientCertStore;
		this.serverCertStoreType = KeyStoreType.SERVER;
		this.clientCertStoreType = KeyStoreType.SERVER;
		useEngineStores = true;
	}

	/**
	 * Enables or disables the usage of server certificates.
	 * Make sure to provide the necessary server certificates before enabling
	 * server authentication.<br/>
	 * @param enable  if true, servers must authenticate themselves
	 */
	public void setAuthenticateThem(boolean enable) {
		if (!useEngineStores) {
			authenticateThem = enable;
		}
	}

	/**
	 * Determines whether servers must authenticate themselves in SSL connections.
	 * By default, authentication of servers is enabled.
	 * @param enable  if true, servers must authenticate themselves
	 */
	public boolean authenticateThem() {
		return (useEngineStores) ? true : authenticateThem;
	}

	/**
	 * Enables or disables the usage of client certificates.
	 * Make sure to provide the necessary client certificates before enabling
	 * client authentication.<br/>
	 * @param enable  if true, the client must authenticate itself
	 */
	public void setAuthenticateMe(boolean enable) {
		if (!useEngineStores) {
			authenticateMe = enable;
		}
	}

	/**
	 * Determines whether the client must authenticate itself in SSL connections.
	 * By default, authentication of the client is disabled.
	 * @param enable  if true, the client must authenticate itself
	 */
	public boolean authenticateMe() {
		return (useEngineStores) ? true : authenticateMe;
	}

	/**
	 * Returns the keystore with server certificates.
	 * @return the server certificates
	 */
	public KeyStore getServerCertificates() throws KeyStoreException {
		if (useEngineStores) {
			return KeyStore.getInstance("JKS");
		}
		if (serverCertificates == null  &&  serverCertStore != null) {
			try {
				FileInputStream certStream = new FileInputStream(serverCertStore);
				setServerCertificates(certStream, serverStorePwd, serverCertStoreType);				
			} catch (FileNotFoundException e) {
				throw new KeyStoreException("Server certifcates keystore not found.");
			}			
		}
		return serverCertificates;
	}

	/**
	 * Returns the keystore with client certificates.
	 * @return the server certificates
	 */
	public KeyStore getClientCertificates() throws KeyStoreException {
		if (useEngineStores) {
			return KeyStore.getInstance("JKS");
		}
		if (clientCertificates == null && clientCertStore != null) {
			try {
				FileInputStream certStream = new FileInputStream(clientCertStore);
				setClientCertificates(certStream, clientStorePwd, clientKeyPwd, clientCertStoreType);				
			} catch (FileNotFoundException e) {
				throw new KeyStoreException("Server certifcates keystore not found.");
			}			
		}
		return clientCertificates;
	}

	/**
	 * Returns the certificate keystore for server certificates. 
	 * @return the server certificate keystore
	 */
	public String getServerCertStore() {
		return serverCertStore;
	}

	/**
	 * Returns the type of the server certificate keystore.
	 * @return the keystore type
	 */
	public KeyStoreType getServerCertStoreType() {	
		return serverCertStoreType;
	}

	/**
	 * Returns the certificate keystore for client certificates. 
	 * @return the client certificate keystore
	 */
	public String getClientCertStore() {
		return clientCertStore;
	}

	/**	
	 * Returns the type of the client certificate keystore.
	 * @return the keystore type
	 */
	public KeyStoreType getClientCertStoreType() {
		return clientCertStoreType;
	}	


	/**
	 * Sets the password needed to decrypt the client certificate keystore.
	 * @param password  the password
	 */
	public void setClientCertStorePassword(String password) {
		clientStorePwd = password;
	}	
	
	/**
	 * Sets the password needed to decrypt private keys in the client's certificate
	 * keystore.
	 * @param password  the password
	 */
	public void setClientCertStoreKeyPassword(String password) {
		clientKeyPwd = password;
	}	
	

	/**
	 * Returns the password used to decrypt the server keystore
	 * @return the password
	 */
	public String getServerStorePassword() {
		return serverStorePwd;
	}

	/**
	 * Returns the password used to decrypt the client keystore
	 * @return the password
	 */
	public String getClientStorePassword() {
		return clientStorePwd;
	}

	/**
	 * Returns the password used to access the private keys in the
	 * client's certificate store.
	 * @return the password
	 */
	public String getClientStoreKeyPassword() {
		return clientKeyPwd;
	}
	
	/**
	 * Returns the alias name used to select a client certificate
	 * from the keystore.
	 * @return  an alias
	 */	
	public String getClientAlias() {
		return clientAlias;
	}		

	/**
	 * Determines whether the keystores of the engine should be used.
	 * @return if true, the certficates are taken from keystores of the engine.
	 */
	public boolean usingEngineKeyStores() {
		return useEngineStores;
	}

	/**
	 * Tries to load the default "cacert" keystore provided by the JRE
	 * installation.
	 * @return a default keystore with trusted root certificates
	 */
	public static KeyStore loadDefaultKeystore() {
		return loadDefaultKeystore("changeit");
	}

	/**
	 * Tries to load the default "cacert" keystore provided by the JRE
	 * installation.
	 * @param password  the password of the "cacerts" keystore (if it
	 * has been changed).
	 * @return a default keystore with trusted root certificates
	 */
	public static KeyStore loadDefaultKeystore(String password) {
		String filename = System.getProperty("java.home")
				+ File.separator + "jre" + File.separator + "lib" + File.separator
				+ "security" + File.separator + "cacerts";

		if (!(new File(filename)).exists()) {
			filename = System.getProperty("java.home")
					+ File.separator + "lib" + File.separator + "security"
					+ File.separator + "cacerts";
		}

		KeyStore keystore = null;
		try {		
			keystore = KeyStore.getInstance("JKS");
			String pwd = (password == null) ? "changeit" : password;
			keystore.load(new FileInputStream(filename), pwd.toCharArray());
		} catch (KeyStoreException e) {
			TRACE.infoT("loadDefaultKeystore(String)", 
				"Unable to instantiate keystore of type 'JKS'.");			
			TRACE.catching("loadDefaultKeystore(String)", e);
		} catch (NoSuchAlgorithmException e) {
			TRACE.infoT("loadDefaultKeystore(String)", 
				"Unable to load keystore: Unknown encryption algoritm.");
			TRACE.catching("loadDefaultKeystore(String)", e);
		} catch (CertificateException e) {
			TRACE.infoT("loadDefaultKeystore(String)", 
				"Unable to load keystore: Invalid certificates found.");
			TRACE.catching("loadDefaultKeystore(String)", e);
		} catch (FileNotFoundException e) {
			TRACE.infoT("loadDefaultKeystore(String)", 
				"Unable to load keystore: The keystore file '{0}' does not exist.",
				new Object[]{filename});			
			TRACE.catching("loadDefaultKeystore(String)", e);
		} catch (IOException e) {
			TRACE.infoT("loadDefaultKeystore(String)", 
				"Unable to load keystore: an i/o problem occured.");			
			TRACE.catching("loadDefaultKeystore(String)", e);
		}
		return keystore;
	}
	
//	private KeyStore copyCerts(KeyStore certs) throws KeyStoreException {
//		ByteArrayOutputStream buf = new ByteArrayOutputStream();
//		try {
//			certs.store(buf, "oiu0cdsifjek".toCharArray());
//			KeyStore newCerts = createKeyStore(KeyStoreType.typeOf(certs));
//			newCerts.load(new ByteArrayInputStream(buf.toByteArray()), "oiu0cdsifjek".toCharArray());
//			return newCerts;			
//		} catch (Exception e){
//			throw new KeyStoreException("Failed to copy keystore. " + e.getMessage());
//		}		
//	}
	
	private KeyStore createKeyStore(KeyStoreType type) throws KeyStoreException, NoSuchProviderException 
	{
		KeyStore keystore = null;			
		if (type == KeyStoreType.JKS) {
			keystore = KeyStore.getInstance("JKS");
		} else if (type == KeyStoreType.IAIK) {
			keystore = KeyStore.getInstance("IAIKKeyStore", "IAIK");
		} else if (type == KeyStoreType.JCEKS) {
			keystore = KeyStore.getInstance("JCEKS", "SunJCE");					
		}
		return keystore;		
	}

//	public static void main(String[] args) throws Exception {
//		SessionContext ctx = new SessionContext();
//		ctx.certificates().setClientCertificates( 
//			new FileInputStream("c:\\temp\\PSE.p12"),
//			"Focault",
//			Certificates.KeyStoreType.PKCS12);
//	}
}
