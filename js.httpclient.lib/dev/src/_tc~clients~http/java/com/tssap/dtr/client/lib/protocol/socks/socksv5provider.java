package com.tssap.dtr.client.lib.protocol.socks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.tssap.dtr.client.lib.protocol.ISessionContext;

/**
 * SOCKS v5 provider. Currently only USERNAME/PASSWORD authentication
 * is provided. 
 * EXPERIMENTAL. NOT YET COMPLETED.
 */
public class SOCKSv5Provider implements ISOCKSProvider {
	
	/** Supported SOCKS version */
	private static final int VER = 5;
	/** Sub-negotiation for CONNECT command*/
	private static final int SUB_VER = 1;
	/** CONNECT command */
	private static final byte CONNECT = 1;
	/** Reserved */
	private static final byte RSV = 0;
	
	/** Authentication methods */
	private static final byte NO_AUTHENTICATION_REQUIRED = 0;
	private static final byte USERNAME_PASSWORD = 2;
	private static final byte NO_ACCEPTABLE_METHODS = (byte)0xFF;

	/** Address types */
	private final static byte IP_V4 = 1;
	private final static byte DOMAINNAME = 3;
	private final static byte IP_V6 = 4;
	
	/** Status codes */
	private static final int SUCCEEDED = 0;
	private static final int GENERAL_SOCKS_SERVER_FAILURE = 1;
	private static final int CONNECTION_NOT_ALLOWED = 2;
	private static final int NETWORK_UNREACHABLE = 3;
	private static final int HOST_UNREACHABLE = 4;
	private static final int CONNECTION_REFUSED = 5;
	private static final int TTL_EXPIRED = 6;
	private static final int COMMAND_NOT_SUPPORTED = 7;	
	private static final int ADRESS_TYPE_NOT_SUPPORTED = 8;
	
	/** Socket streams */
	private InputStream in;
	private OutputStream out;
	
	
	/**
	 * Creates a provider for SOCKS v5 protocol communication.
	 */
	public SOCKSv5Provider() {
	}

	/**
	 * Returns the SOCKS protocol version this provider supports.
	 * @return  5.
	 */
	public int getVersion() {
		return VER;
	}

	/**
	 * Returns a socket for the given host, port and session context.
	 * @param socket  the socket conncted to the SOCKS server
	 * @param host   the target host to connect to
	 * @param port   the target port to connect to
	 * @param session  authentication information for the target host
	 * @return a socket connected to the given target host. The connection
	 * wa already established through the SOCKS server.
	 * @throws SocketException  if the provider failed to connect to the
	 * target host through the SOCKS proxy.
	 * @throws IOException  if an i/o error occurs
	 */
	public Socket getSocket(Socket socket, String host, int port, ISessionContext session)
	throws SocketException, IOException 
	{
		in = socket.getInputStream();
		out = socket.getOutputStream();
		connect(host, port, session);
		return socket;				
	}

	/**
	 * Connects to the SOCKS server. Negotiates acceptable authentication methods,
	 * authenticates using the given session context and issues a CONNECT method
	 * for the requested target host.
	 * @param host   the target host to connect to
	 * @param port   the target port to connect to
	 * @param session  authentication information for the target host
	 * @throws SocketException  if the provider failed to connect to the
	 * target host through the SOCKS proxy.
	 * @throws IOException  if an i/o error occurs
	 */
	private void connect(String host, int port, ISessionContext session)
	throws SocketException, IOException 
	{
		
		byte[] SAY_HELLO;
		if (session!=null && session.getUser()!=null && session.getPassword()!=null ) {
			SAY_HELLO = new byte[4];
			SAY_HELLO[0] = VER;
			SAY_HELLO[1] = 2; 
			SAY_HELLO[0] = NO_AUTHENTICATION_REQUIRED;
			SAY_HELLO[0] = USERNAME_PASSWORD;
		} else {
			SAY_HELLO = new byte[3];
			SAY_HELLO[0] = VER;
			SAY_HELLO[1] = 1; 
			SAY_HELLO[0] = NO_AUTHENTICATION_REQUIRED;		
		}
		out.write(SAY_HELLO);
		
		checkVersion(in.read());		
		authenticate(session);
		
		byte[] HOST = host.getBytes("ISO-8859-1");
		byte[] SAY_CONNECT = new byte[5 + HOST.length + 2];
		SAY_CONNECT[0] = VER;
		SAY_CONNECT[1] = CONNECT;
		SAY_CONNECT[2] = RSV;
		SAY_CONNECT[3] = DOMAINNAME;
		SAY_CONNECT[4] = (byte)HOST.length;
		System.arraycopy(HOST, 0, SAY_CONNECT, 5, HOST.length);
		SAY_CONNECT[5+HOST.length] = (byte) ((port >> 8) & 0xFF);
		SAY_CONNECT[6+HOST.length] = (byte) (port & 0xFF);
		out.write(SAY_CONNECT);

		checkVersion(in.read());
		checkResponseStatus(in.read());
		in.read();  // skip reserved byte
		skipAddress();		
	}

	/**
	 * Checks whether the given SOCKS version is supported.
	 * @param version  the version to check
	 * @throws SocketException  if not version is VER. 
	 */
	private void checkVersion(int version) throws SocketException {
		if (version != VER) {
			throw new SocketException("Unsupported protocol version: SOCKS v" + version);			
		}
	}
	
	/**
	 * Checks the given SOCKS response state.
	 * @param status  the status to check.
	 * @throws SocketException  if not status is SUCCEEDED.
	 */
	private void checkResponseStatus(int status) throws SocketException {
		switch (status) {
			case SUCCEEDED :
				break;
			case GENERAL_SOCKS_SERVER_FAILURE :
				throw new SocketException("General SOCKS server failure.");
			case CONNECTION_NOT_ALLOWED :
				throw new SocketException("Connecting the SOCKS server is not allowed.");
			case NETWORK_UNREACHABLE :
				throw new SocketException("The requested network is unreachable.");
			case HOST_UNREACHABLE :
				throw new SocketException("The requested host is unreachable.");
			case CONNECTION_REFUSED :
				throw new SocketException("Connecting the SOCKS server was refused.");
			case TTL_EXPIRED :
				throw new SocketException("TTL expired.");
			case COMMAND_NOT_SUPPORTED :
				throw new SocketException("SOCKS command not supported.");
			case ADRESS_TYPE_NOT_SUPPORTED :
				throw new SocketException("Address type not supported.");
			default :
				throw new SocketException("SOCKS server returnd invalid or unknown status.");
		}		
	}
	
	/**
	 * Skips the address part of the CONNECT response.
	 * @throws SocketException  if the returned address type is not supported.
	 * @throws IOException    if an i/o error occurs
	 */
	private void skipAddress()throws SocketException, IOException 
	{
		int addressType = in.read();
		int n = 2;
		if (addressType == IP_V4) {
			n += 4;
		} else if (addressType == IP_V6) {
			n += 16;
		} else if (addressType == DOMAINNAME) {
			n += in.read();
		} else {
			throw new SocketException("Address type not supported.");
		}		
		in.skip(n);
	}
	
	/**
	 * Authenticates the client using the given session context.
	 * Currently this method does only support USERNAME/PASSWORD authentication.
	 * @param session  authentication information for the target host
	 * @throws SocketException  if authentication failed, or no valid authentication
	 * method could be negotiated with the SOCKS server.
	 * @throws IOException  if an i/o error occurs
	 */
	private void authenticate(ISessionContext session) 
	throws SocketException, IOException
	{
		int method = in.read();
		if (method == NO_ACCEPTABLE_METHODS) {
			throw new SocketException("SOCKS server supports no acceptable authentication methods.");
		} else if (method != NO_AUTHENTICATION_REQUIRED  && method != USERNAME_PASSWORD) {
			throw new SocketException("SOCKS server requires unknown authentication method.");
		}
		
		if (method == USERNAME_PASSWORD) {		
			byte[] user = session.getUser().getBytes();
			byte[] password = session.getPassword().getBytes();
			byte[] AUTHENTICATE = new byte [2 + user.length + 1 + password.length];
			AUTHENTICATE[0] = SUB_VER;
			AUTHENTICATE[1] = (byte)user.length;
			System.arraycopy(user,0,AUTHENTICATE,2,user.length);
			AUTHENTICATE[2+user.length] = (byte)password.length;
			System.arraycopy(password, 0, AUTHENTICATE, 3+user.length, password.length);
			out.write(AUTHENTICATE);
			
			int version = in.read();
			int status = in.read(); 
			if ( version != 1  || status != 0) {
				throw new SocketException("Authentication with SOCKS server failed.");
			}
		}
	}	


}
