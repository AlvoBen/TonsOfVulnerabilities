package com.tssap.dtr.client.lib.protocol.socks;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.tssap.dtr.client.lib.protocol.ISessionContext;

/**
 * Interface representing a generic provider for the SOCKS
 * protocol to connect hosts through proxies and firewalls.
 */
public interface ISOCKSProvider {
	
	/**
	 * Returns the SOCKS protocol version this provider supports.
	 * @return  either 4 or 5.
	 */
	int getVersion();
	
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
	Socket getSocket(Socket socket, String host, int port, ISessionContext session)
	throws SocketException, IOException; 
}
