package com.tssap.dtr.client.lib.protocol.ssl;

import java.io.IOException;
import java.net.Socket;

/**
 * Interface describing providers of SSL sockets.
 */
public interface ISecureSocketProvider {
	
	/**
	 * Creates a SSL socket for the given host. 
	 * @param host   the host to connect to
	 * @param port	 the port to connect to
	 * @return a SSL socket
	 * @throws IOException  if creation of the socket failed.
	 */		
	Socket createSocket(String host, int port) throws IOException;
	
	/**
	 * Creates a SSL socket for the given host. 
	 * @param host   the host to connect to
	 * @param port	 the port to connect to
	 * @param clientAlias   the client's identity used to
	 * select a certificate from the client's keystore. 
	 * @return a SSL socket
	 * @throws IOException  if creation of the socket failed.
	 */
	Socket createSocket(String host, int port, String clientAlias) throws IOException;
	
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
	Socket createSocket(Socket socket, String host, int port) throws IOException;	
	
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
	Socket createSocket(Socket socket, String host, int port, String clientAlias) throws IOException;
		
}
