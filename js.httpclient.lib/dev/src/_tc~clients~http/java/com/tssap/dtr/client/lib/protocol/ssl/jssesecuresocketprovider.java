package com.tssap.dtr.client.lib.protocol.ssl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import com.sap.tc.logging.Location;

/**
 * Wrapper for a socket factory based on the Java JCE/JSSE providers.
 */
public class JSSESecureSocketProvider implements ISecureSocketProvider {

	private Object JSSE_SOCKET_FACTORY;
	private Method CREATE_SOCKET_METHOD_HOST_PORT;
	private Method CREATE_SOCKET_METHOD_HOST_PORT_SOCKET;
	
	/** client trace */
	private static Location TRACE = Location.getLocation(JSSESecureSocketProvider.class);	


	private JSSESecureSocketProvider() {
	}
	
	/** 
	 * Returns a new instance of JSSESecureSocketProvider without client
	 * or server creadentials, and default security algorithms.
	 */
	public static ISecureSocketProvider getDefault() {
		return new JSSESecureSocketProvider();
	}	

	/**
	 * Creates a SSL socket for the given host. 
	 * @param host   the host to connect to
	 * @param port	 the port to connect to
	 * @return a SSL socket
	 * @throws IOException  if creation of the socket failed.
	 */	
	public Socket createSocket(String host, int port) throws IOException {
		try {
			if (JSSE_SOCKET_FACTORY == null) {
				initProvider();
			}
			return (Socket)CREATE_SOCKET_METHOD_HOST_PORT.invoke(
				JSSE_SOCKET_FACTORY, new Object[] { host, new Integer(port)});
		} catch (Exception e) {
			TRACE.catching("createSocket(String,int)", e);
			throw new IOException("failed to create SSL socket [" + e.getMessage() + "]");
		}
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
		try {
			if (JSSE_SOCKET_FACTORY == null) {
				initProvider();
			}
			return (Socket)CREATE_SOCKET_METHOD_HOST_PORT_SOCKET.invoke(
				JSSE_SOCKET_FACTORY, new Object[] { socket, host, new Integer(port)});
		} catch (Exception e) {
			TRACE.catching("createSocket(Socket,String,int)", e);
			throw new IOException("failed to create SSL socket [" + e.getMessage() + "]");
		}
	}
	
	/**
	 * Creates a SSL socket for the given host. 
	 * @param host   the host to connect to
	 * @param port	 the port to connect to
	 * @param clientAlias   this paramter currently is ignored
	 * @return a SSL socket
	 * @throws IOException  if creation of the socket failed.
	 */
	public Socket createSocket(String host, int port, String clientAlias) throws IOException {
		return createSocket(host, port);
	}

	/**
	 * Creates a SSL wrapper for an existing socket. The given host and port parameters
	 * determines the destination server to contact.
	 * Used for SSL communication through proxies.
	 * @param socket  the socket to wrap with SSL
	 * @param host   the host to connect to
	 * @param port	 the port to connect to
	 * @param clientAlias  this paramter currently is ignored
	 * @return a SSL socket
	 * @throws IOException  if creation of the socket failed. 
	 */
	public Socket createSocket(Socket socket, String host, int port, String clientAlias) throws IOException {
		return createSocket(socket, host, port);
	}	


 	private void initProvider() throws IOException {
 		try {
			Class FACTORY_CLASS = Class.forName("javax.net.ssl.SSLSocketFactory");
			JSSE_SOCKET_FACTORY = FACTORY_CLASS.getMethod("getDefault", null).invoke(null, null);
			CREATE_SOCKET_METHOD_HOST_PORT =
				JSSE_SOCKET_FACTORY.getClass().getMethod("createSocket", new Class[] { String.class, int.class });				
			CREATE_SOCKET_METHOD_HOST_PORT_SOCKET = 
				JSSE_SOCKET_FACTORY.getClass().getMethod("createSocket", new Class[] { Socket.class, String.class, int.class});
		} catch (Exception e) {
			TRACE.catching("initProvider()", e);
			throw new IOException("failed to initialize JSSE library [" + e.getMessage() + "]");
 		}
	}
}
