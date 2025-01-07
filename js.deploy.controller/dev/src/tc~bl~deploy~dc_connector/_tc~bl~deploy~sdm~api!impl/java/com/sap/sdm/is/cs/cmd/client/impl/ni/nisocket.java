/*===========================================================================*/
/*                                                                           */
/*  (C) Copyright SAP AG, Walldorf  1998                                     */
/*                                                                           */
/*===========================================================================*/

package com.sap.sdm.is.cs.cmd.client.impl.ni;

/*===========================================================================*/

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImplFactory;
import java.net.UnknownHostException;
import java.util.Enumeration;

/*===========================================================================*/

/**
 * This class implements client sockets (also called just "sockets"). A socket
 * is an endpoint for communication between two machines. The socket can
 * communicate directly with a host or via a saprouter.
 * <p>
 * The actual work of the socket is performed by an instance of the
 * <code>Socket</code> class.
 * <p>
 * The communication via saprouter is initialized by sending and receiving a
 * so-called handshake packet. After exchanging this packet, which is hidden to
 * the client, the NiSocket can be used like the <code>Socket</code> class. If a
 * hostname starts with /H/, a SAP route string is assumed otherwise a 'normal'
 * hostname.
 * 
 * @author Harald Mueller
 * @version 1.0
 * @see java.net.Socket
 */

public class NiSocket {

	/**
	 * Timeout for the socket when exchanging the handshake packet
	 */
	private static final int SO_TIMEOUT = 30000;

	/**
	 * see nixxi.h for constants
	 */
	private static final byte NI_IROUTE_VS = 2;
	private static final byte NI_VERSION = 27;
	private static final byte[] NI_ROUTE_EYEC = { (byte) '\116', (byte) '\111',
			(byte) '\137', (byte) '\122', (byte) '\117', (byte) '\125',
			(byte) '\124', (byte) '\105', (byte) '\0' };
	private static final byte[] NI_ROUTE_ERROR = { (byte) '\116',
			(byte) '\111', (byte) '\137', (byte) '\122', (byte) '\124',
			(byte) '\105', (byte) '\122', (byte) '\122', (byte) '\0' };
	private static final byte[] NI_PONG = { (byte) '\116', (byte) '\111',
			(byte) '\137', (byte) '\120', (byte) '\117', (byte) '\116',
			(byte) '\107', (byte) '\0' };
	private static final byte NI_IROUTE_TALKMODE = 1;
	private static final byte NI_IROUTE_OPTIONS = 0;
	private static final byte NI_IROUTE_UNUSED = 0;
	private static final int NI_IROUT_HEADER_SIZE = 24;
	private static final int NIROUT_ERR_HEADER_SIZE = 20;
	private static final int SAP_ERR_INFO_LN = 500;
	private static final String ENCODING = "iso-8859-1"; // must be ASCII

	/**
	 * The implementation of this NiSocket.
	 */
	private Socket impl;

	/**
	 * Output stream created by the constructor to perform the initial
	 * handshake. This stream has to be left open after exchange of the
	 * handshake packet so that <code>getOutputStream()</code> returns a valid
	 * stream. reopening of the stream fails this purpose.
	 * 
	 * @see NiSocket#getOutputStream()
	 */
	private OutputStream outStream = null;

	/**
	 * Input stream created by the constructor to perform the initial handshake.
	 * This stream has to be left open after exchange of the handshake packet so
	 * that <code>getInputStream()</code> returns a valid stream. reopening of
	 * the stream fails this purpose.
	 * 
	 * @see NiSocket#getInputStream()
	 */
	private InputStream inStream = null;

	/**
	 * The saproute for the NiSocket. Empty if no hostname or SAP route string
	 * is specified for creating an NiSocket, but a InetAddress was used.
	 * 
	 * @see SapRouteString
	 */
	private SapRouteString sapRoute;

	/**
	 * Creates a NiSocket to the specified host and port. Host can be a normal
	 * hostname or a SAP route string. If the SAP route string contains a port
	 * specification for the last host, this is used otherwise the port
	 * specified as parameter is used.
	 * 
	 * @param host
	 *            a hostname or a SAP route string
	 * @param port
	 *            the port number the NiSocket should use, if not overruled by
	 *            the specification in a SAP route string
	 * @exception UnknownHostException
	 *                if the host cannot be found
	 * @exception IOException
	 *                if an I/O error occurs when creating the socket
	 * @exception SapRouteStringFormatException
	 *                if the a specified SAP route string has the wrong format.
	 * @see SapRouteString
	 * @see java.net.Socket
	 */
	public NiSocket(String host, int port) throws UnknownHostException,
			IOException, SapRouteStringFormatException {
		sapRoute = new SapRouteString(host, port);
		initializeSapRoute(sapRoute, port, null, 0);

	}

	/**
	 * Creates a stream socket and connects it to the specified port number at
	 * the specified IP address.
	 * 
	 * @param address
	 *            the IP address.
	 * @param port
	 *            the port number.
	 * @exception IOException
	 *                if an I/O error occurs when creating the socket.
	 * @see java.net.Socket#Socket(java.net.InetAddress, int)
	 */
	public NiSocket(InetAddress address, int port) throws IOException {
		impl = new Socket(address, port);
		Trace.print(Trace.CONTROL_FLOW,
				"NiSocket.NiSocket(InetAddress): Socket constructed: " + impl);

		sapRoute = new SapRouteString();
	}

	/**
	 * Creates a socket and connects it to the specified remote host on the
	 * specified remote port. The NiSocket will also bind() to the local address
	 * and port supplied. Host can be a normal hostname or a SAP route string.
	 * If the SAP route string contains a port specification for the last host,
	 * this is used otherwise the port specified as parameter is used.
	 * 
	 * @param host
	 *            the name of the remote host or a SAP route string
	 * @param port
	 *            the remote port
	 * @param localAddr
	 *            the local address the socket is bound to
	 * @param localPort
	 *            the local port the socket is bound to
	 * @exception UnknownHostException
	 *                if the host cannot be found
	 * @exception IOException
	 *                if an I/O error occurs when creating the socket
	 * @exception SapRouteStringFormatException
	 *                if the a specified SAP route string has the wrong format.
	 * @see SapRouteString
	 * @see java.net.Socket
	 */
	public NiSocket(String host, int port, InetAddress localAddr, int localPort)
			throws IOException, UnknownHostException,
			SapRouteStringFormatException {
		sapRoute = new SapRouteString(host, port);
		initializeSapRoute(sapRoute, port, localAddr, localPort);
	}

	/**
	 * Creates a socket and connects it to the specified remote address on the
	 * specified remote port. The Socket will also bind() to the local address
	 * and port supplied.
	 * 
	 * @param address
	 *            the remote address
	 * @param port
	 *            the remote port
	 * @param localAddr
	 *            the local address the socket is bound to
	 * @param localPort
	 *            the local port the socket is bound to
	 * @exception IOException
	 *                if an I/O error occurs when creating the socket.
	 * @see java.net.Socket#Socket(java.net.InetAddress, int,
	 *      java.net.InetAddress, int)
	 */
	public NiSocket(InetAddress address, int port, InetAddress localAddr,
			int localPort) throws IOException {
		impl = new Socket(address, port, localAddr, localPort);
		Trace.print(Trace.CONTROL_FLOW,
				"NiSocket.NiSocket(InetAddress): Socket constructed: " + impl);

		sapRoute = new SapRouteString();
	}

	/**
	 * The function doing the work when connecting via SAP router. i.e. sending
	 * and receiving the handshake packet. Uses the constructor of class
	 * <code> Socket</code>
	 * 
	 * @param routeStr
	 *            the SAP route string to use for connection
	 * @param port
	 *            the port to connect to.
	 * @param localAddr
	 *            the local address the socket is bound to
	 * @param localPort
	 *            the local port the socket is bound to
	 * @exception UnknownHostException
	 *                if the host cannot be found
	 * @exception IOException
	 *                if an I/O error occurs when creating the socket
	 * @exception SapRouteStringFormatException
	 *                if the a specified SAP route string has the wrong format.
	 * @see SapRouteString
	 * @see java.net.Socket#Socket(java.net.InetAddress, int,
	 *      java.net.InetAddress, int)
	 */
	private void initializeSapRoute(SapRouteString routeStr, int port,
			InetAddress localAddr, int localPort) throws UnknownHostException,
			IOException, SapRouteStringFormatException {
		// create a socket connected to the host in the first SAP route
		// substring.
		// If the SAP route string contains only one SAP route substring
		// nothing else is done. Otherwise the handshake packet is exchanged.
		// After that the socket is used as a 'normal' Java-Socket.
		int routePort;
		SapRouteSubString substr = routeStr.getFirstRoute();

		try {
			routePort = substr.getPort();

		} catch (NumberFormatException e) {
			throw new SapRouteStringFormatException(
					"Service specification of the first SAP route "
							+ "substring must be a number");
		}

		impl = new Socket(substr.getHost(), routePort, localAddr, localPort);

		Trace.print(Trace.CONTROL_FLOW,
				"NiSocket.initializeSapRoute(): Socket constructed: " + impl);

		// Do the handshake only if there's more than on host
		// specified in the SAP route string
		if (routeStr.getNuOfRoutes() > 1) {
			try {
				// set the timeout for the socket, so that the
				// receiveReplyPacket()
				// function will not block for infinity. Reset it after that to
				// the
				// value before.
				int saveTimeout = getSoTimeout();
				setSoTimeout(SO_TIMEOUT);

				sendHandshakePacket(makeHandshakePacket(routeStr));

				receiveReplyPacket();
				setSoTimeout(saveTimeout);
			} catch (IOException e) {
				close();
				throw e;
			}
		}
	}

	/**
	 * create a handshake packet which is sent to a SAP router when setting up
	 * the connection. This function contains the implementation details of the
	 * SAP router interface
	 * 
	 * @param route
	 *            the SAP route string
	 * @return the handshake packet as an array of bytes
	 * @exception ProtocolException
	 *                if the SAP route string cannot be converted to ASCII
	 * @see java.lang.String#getBytes(String)
	 */
	private byte[] makeHandshakePacket(SapRouteString route)
			throws ProtocolException {
		// set initial size to approx three times the size of NI_IROUTE
		// without space field
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		byte tot_entries = (new Integer(route.getNuOfRoutes())).byteValue();
		int SpaceLength = 0;

		// next_entry points to the second route entry in the space field
		// containing all SAP route substrings
		// omit password string since this will be inserted in the second
		// SAP route entry
		SapRouteSubString substr = route.getFirstRoute();
		int next_entry = substr.getHost().length()
				+ substr.getPortAsString().length() + 3;

		// password string of the first route will be written to the
		// second host/service/passwd entry of the handshake packet
		// and so on. so the password entry of the last route will
		// be ignored
		String TmpPasswd = "";
		for (Enumeration e = route.getRoutes(); e.hasMoreElements();) {
			SapRouteSubString str = (SapRouteSubString) e.nextElement();
			SpaceLength += str.getHost().length();
			SpaceLength += str.getPortAsString().length();
			SpaceLength += TmpPasswd.length();
			SpaceLength += 3; // 3 null bytes per substring
			TmpPasswd = str.getPassword();
		}

		int PacketLength = SpaceLength + NI_IROUT_HEADER_SIZE;

		byteStream.write((PacketLength >>> 24) & 0x000000ff);
		byteStream.write((PacketLength >>> 16) & 0x000000ff);
		byteStream.write((PacketLength >>> 8) & 0x000000ff);
		byteStream.write((PacketLength >>> 0) & 0x000000ff);
		byteStream.write(NI_ROUTE_EYEC, 0, NI_ROUTE_EYEC.length);
		byteStream.write(NI_IROUTE_VS);
		byteStream.write(NI_VERSION);
		byteStream.write(tot_entries);
		byteStream.write(NI_IROUTE_TALKMODE);
		byteStream.write(NI_IROUTE_OPTIONS);
		byteStream.write(NI_IROUTE_UNUSED);
		byteStream.write(tot_entries - 1);
		byteStream.write((SpaceLength >>> 24) & 0x000000ff);
		byteStream.write((SpaceLength >>> 16) & 0x000000ff);
		byteStream.write((SpaceLength >>> 8) & 0x000000ff);
		byteStream.write((SpaceLength >>> 0) & 0x000000ff);
		byteStream.write((next_entry >>> 24) & 0x000000ff);
		byteStream.write((next_entry >>> 16) & 0x000000ff);
		byteStream.write((next_entry >>> 8) & 0x000000ff);
		byteStream.write((next_entry >>> 0) & 0x000000ff);

		// write the host/service/password strings
		// note that the password for the current SAP route string entry
		// must appear in the following entry
		String KeepPasswd = "";

		for (Enumeration e = route.getRoutes(); e.hasMoreElements();) {
			byte[] NullByte = { (byte) '\0' };

			SapRouteSubString str = (SapRouteSubString) e.nextElement();

			byte[] buf;
			try {
				buf = str.getHost().getBytes(ENCODING);
			} catch (UnsupportedEncodingException ex) {
				throw new ProtocolException(
						"Cannot generate handshake packet: "
								+ "cannot convert hostname to " + ENCODING);
			}
			byteStream.write(buf, 0, str.getHost().length());
			byteStream.write(NullByte, 0, 1);

			try {
				buf = str.getPortAsString().getBytes(ENCODING);
			} catch (UnsupportedEncodingException ex) {
				throw new ProtocolException(
						"Cannot generate handshake packet: "
								+ "cannot convert port to " + ENCODING);
			}
			byteStream.write(buf, 0, str.getPortAsString().length());
			byteStream.write(NullByte, 0, 1);

			try {
				buf = KeepPasswd.getBytes(ENCODING);
			} catch (UnsupportedEncodingException ex) {
				throw new ProtocolException(
						"Cannot generate handshake packet: "
								+ "cannot convert password to " + ENCODING);
			}
			byteStream.write(buf, 0, KeepPasswd.length());
			byteStream.write(NullByte, 0, 1);
			KeepPasswd = str.getPassword();

		}

		Trace.print(Trace.CONTROL_FLOW,
				"NiSocket.makeHandshakePacket(): Handshake packet built");

		// format the packet for trace output
		if (Trace.getLevel() == Trace.ALL) {
			byte[] packet = byteStream.toByteArray();
			String message = "Handshake packet:\n";

			for (int i = 0; i < packet.length; i++) {
				message = message + "|" + packet[i];
			}
			message += "|";
			Trace.print(Trace.ALL, message);
		}

		return byteStream.toByteArray();

	}

	/**
	 * Writes the handshake packet to the socket
	 * 
	 * @param packet
	 *            the handshake packet
	 */
	private void sendHandshakePacket(byte[] packet) throws IOException {
		OutputStream out = getOutputStream();

		Trace.print(Trace.ALL,
				"NiSocket.sendHandshakePacket(): Output stream opened.");

		out.write(packet);
		out.flush();

		Trace.print(Trace.CONTROL_FLOW,
				"NiSocket.sendHandshakePacket(): Handshake packet sent.");

	}

	/**
	 * Receive and analyze the reply packet from the SAP router. Therefore an
	 * InputStream is created. Every byte has to be read one after the other to
	 * get the unsigned value which is not possible reading to a byte array.
	 * 
	 * @exception IOException
	 *                if the connection via saprouter failed
	 * @exception ProtocolException
	 *                if the reply packet is corrupted
	 * @see java.io.BufferedInputStream#read
	 */
	private void receiveReplyPacket() throws IOException {
		InputStream in = getInputStream();

		Trace.print(Trace.ALL,
				"NiSocket.receiveReplyPacket(): Input stream opened.");

		// get the packet size returned by the saprouter: 4 bytes in
		// net byte order
		int tmp;
		int PacketSize = 0;
		int shift = 24;

		for (int i = 0; i < 4; i++) {
			if ((tmp = in.read()) == -1) {
				throw new ProtocolException("Malformed reply from saprouter");
			}

			PacketSize += (tmp << shift);
			shift -= 8;
		}

		Trace.print(Trace.ALL,
				"NiSocket.receiveReplyPacket(): Size of reply packet: "
						+ PacketSize);

		// Read the packet into a buffer
		// An integer array is used because
		// the range of the byte type in Java is from -127 to 127
		int inBuffer[] = new int[PacketSize];

		for (int i = 0; i < PacketSize; i++) {
			if ((inBuffer[i] = in.read()) == -1) {
				throw new ProtocolException("Malformed reply from saprouter");
			}

		}

		Trace.print(Trace.CONTROL_FLOW,
				"NiSocket.receiveReplyPacket(): Reply packet received.");

		if (Trace.getLevel() == Trace.ALL) {
			String message = "Reply packet from saprouter"
					+ " (without first 4 bytes containing the packet size):\n";

			for (int i = 0; i < inBuffer.length; i++) {
				message = message + "|" + inBuffer[i];
			}
			message += "|";
			Trace.print(Trace.ALL, message);
		}

		// check for NI_PONG
		boolean niPongRec = true;

		for (int i = 0; i < NI_PONG.length; i++) {
			if (NI_PONG[i] != inBuffer[i]) {
				niPongRec = false;
				break;
			}
		}
		if (niPongRec == true) {
			Trace.print(Trace.ALL,
					"NiSocket.receiveReplyPacket(): NI_PONG received.");

			return;
		}

		// check for NI_ROUTE_ERROR
		boolean niRouteErrorRec = true;

		for (int i = 0; i < NI_ROUTE_ERROR.length; i++) {
			if (NI_ROUTE_ERROR[i] != inBuffer[i]) {
				niRouteErrorRec = false;
				break;
			}
		}

		if (niRouteErrorRec == false) {
			close();

			throw new ProtocolException("Malformed reply from saprouter");
		} else {

			Trace.print(Trace.ALL,
					"NiSocket.receiveReplyPacket(): NI_ROUTE_ERROR received.");

			ErrInfo reason = null;

			try {
				reason = analyzeNiRouteError(inBuffer);
			} catch (Exception ex) {
				Trace.print(Trace.ERROR, ex.toString());

				close();
				throw new ProtocolException("Malformed reply from saprouter");

			}

			close();

			Trace.print(Trace.ERROR, reason.getDescription());

			throw new IOException("Connect via saprouter failed: "
					+ reason.getDescription());
		}
	}

	/**
	 * still has to be implemented Analyzes the reply handshake packet in case
	 * of NI_ROUTE_ERR and returns a string with the reason for the error
	 * 
	 * @param inBuffer
	 *            the reply packet containing the error
	 * @return the reason for the error
	 */
	private ErrInfo analyzeNiRouteError(int[] inBuffer)
			throws ErrInfo.ParseException {

		// extract nirc
		int shift = 24;
		int nirc = 0;
		int datalen = 0;
		char data[] = null;
		ErrInfo err = null;

		for (int i = NIROUT_ERR_HEADER_SIZE - 8; i < NIROUT_ERR_HEADER_SIZE - 4; i++) {
			nirc += (inBuffer[i] << shift);
			shift -= 8;
		}

		shift = 24;

		for (int i = NIROUT_ERR_HEADER_SIZE - 4; i < NIROUT_ERR_HEADER_SIZE; i++) {
			datalen += (inBuffer[i] << shift);
			shift -= 8;
		}

		data = new char[datalen + 1];
		for (int i = NIROUT_ERR_HEADER_SIZE; i < datalen
				+ NIROUT_ERR_HEADER_SIZE; i++) {
			data[i - NIROUT_ERR_HEADER_SIZE] = (char) inBuffer[i];
		}

		Trace.print(Trace.CONTROL_FLOW, "NiSocket.analyzeNiRouteError(): "
				+ String.valueOf(nirc));

		return new ErrInfo(data);

	}

	/**
	 * Returns the address to which the socket is connected.
	 * 
	 * @return the remote IP address to which this socket is connected.
	 * @see java.net.Socket#getInetAddress()
	 */
	public InetAddress getInetAddress() {
		return impl.getInetAddress();
	}

	/**
	 * Returns the local address to which the socket is bound.
	 * 
	 * @return the local IP address to which this socket is connected.
	 * @see java.net.Socket#getLocalAddress()
	 */
	public InetAddress getLocalAddress() {
		return impl.getLocalAddress();
	}

	/**
	 * Returns the remote port to which this socket is connected.
	 * 
	 * @return the remote port number to which this socket is connected.
	 * @see java.net.Socket#getPort()
	 */
	public int getPort() {
		return impl.getPort();
	}

	/**
	 * Returns the local port to which this socket is bound.
	 * 
	 * @return the local port number to which this socket is connected.
	 * @see java.net.Socket#getLocalPort()
	 */
	public int getLocalPort() {
		return impl.getLocalPort();
	}

	/**
	 * Returns the target host of the connection This cannot be obtained with
	 * the getPort() function when connecting via a SAP router. In case of a
	 * connection via SAP router the value returned is the last entry in the SAP
	 * route string
	 * 
	 * @return the remote port of the connection
	 * @exception NumberFormatException
	 *                if the port string in the SAP route string cannot be
	 *                converted to an integer (only relevant when using SAP
	 *                route strings
	 */
	public int getTargetPort() throws NumberFormatException {
		if (sapRoute.getNuOfRoutes() > 1) {
			return sapRoute.getLastRoute().getPort();
		} else {
			return getPort();
		}
	}

	/**
	 * Returns the target port of the connection This cannot be obtained with
	 * the getInetAddress().getHostName() function when connecting via a SAP
	 * router. In case of a connection via SAP router the value returned is the
	 * last entry in the SAP route string
	 * 
	 * @return the remote host of the connection
	 * @see SapRouteString
	 */
	public String getTargetHost() {
		if (sapRoute.getNuOfRoutes() > 1) {
			return sapRoute.getLastRoute().getHost();
		} else {
			return getInetAddress().getHostName();
		}
	}

	/**
	 * Returns an input stream for this socket. The input stream cannot be
	 * closed and reopened, so it has to stay open after sending the handshake
	 * packet. So the same input stream as the one used for the handshake packet
	 * is returned.
	 * 
	 * @return an input stream for reading bytes from this socket.
	 * @exception IOException
	 *                if an I/O error occurs when creating the input stream.
	 * @see NiSocket#inStream
	 */
	public InputStream getInputStream() throws IOException {
		// since opened input stream cannot be closed and reopened
		// we store it as private member
		if (inStream == null) {
			inStream = impl.getInputStream();
		}

		return inStream;
	}

	/**
	 * Returns an output stream for this socket. The output stream cannot be
	 * closed and reopened, so it has to stay open after sending the handshake
	 * packet. So the same output stream as the one used for the handshake
	 * packet is returned.
	 * 
	 * @return an output stream for writing bytes to this socket.
	 * @exception IOException
	 *                if an I/O error occurs when creating the output stream.
	 * @see NiSocket#outStream
	 */
	public OutputStream getOutputStream() throws IOException {
		// since opened output stream cannot be closed and reopened
		// we store it as private member
		if (outStream == null) {
			outStream = impl.getOutputStream();
		}
		return outStream;
	}

	/**
	 * Enable/disable TCP_NODELAY (disable/enable Nagle's algorithm).
	 * 
	 * @see java.net.Socket#setTcpNoDelay
	 */
	public void setTcpNoDelay(boolean on) throws SocketException {
		impl.setTcpNoDelay(on);
	}

	/**
	 * Tests if TCP_NODELAY is enabled.
	 * 
	 * @see java.net.Socket#getTcpNoDelay
	 */
	public boolean getTcpNoDelay() throws SocketException {
		return impl.getTcpNoDelay();
	}

	/**
	 * Enable/disable SO_LINGER with the specified linger time.
	 * 
	 * @see java.net.Socket#setSoLinger
	 */
	public void setSoLinger(boolean on, int val) throws SocketException {
		impl.setSoLinger(on, val);
	}

	/**
	 * Returns setting for SO_LINGER. -1 returns implies that the option is
	 * disabled.
	 * 
	 * @see java.net.Socket#getSoLinger
	 */
	public int getSoLinger() throws SocketException {
		return impl.getSoLinger();
	}

	/**
	 * Enable/disable SO_TIMEOUT with the specified timeout, in milliseconds.
	 * 
	 * @see java.net.Socket#setSoTimeout
	 */
	public synchronized void setSoTimeout(int timeout) throws SocketException {
		impl.setSoTimeout(timeout);
	}

	/**
	 * Returns setting for SO_TIMEOUT. 0 returns implies that the option is
	 * disabled (i.e., timeout of infinity).
	 * 
	 * @see java.net.Socket#getSoTimeout
	 */
	public synchronized int getSoTimeout() throws SocketException {
		return impl.getSoTimeout();
	}

	/**
	 * Closes this socket. If the input and/or output streams are open close
	 * them before
	 * 
	 * @exception IOException
	 *                if an I/O error occurs when closing this socket.
	 * @see java.net.Socket#close
	 */
	public synchronized void close() throws IOException {
		// if a stream was closed before the reference is not null
		// therefore the exception has to be ignored to get to the
		// closing of the socket

		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException e) {// $JL-EXC$
				// ignore errors
			}
		}

		if (outStream != null) {
			try {
				outStream.close();
			} catch (IOException e) {// $JL-EXC$
				// ignore errors
			}
		}

		impl.close();
	}

	/**
	 * Converts this socket to a <code>String</code>.
	 * 
	 * @return a string representation of this socket.
	 */
	public String toString() {
		String retval;
		if (sapRoute.getNuOfRoutes() > 1) {
			// Socket via saprouter
			retval = "NiSocket[route = " + sapRoute + "\n"
					+ "direct Sap router addr=" + impl.getInetAddress() + "\n"
					+ "direct Sap router port=" + impl.getPort() + "\n"
					+ "localport=" + impl.getLocalPort() + "]";
		} else {
			// normal Socket
			retval = "Socket[addr=" + impl.getInetAddress() + ",port="
					+ impl.getPort() + ",localport=" + impl.getLocalPort()
					+ "]";
		}
		return new String(retval);
	}

	/**
	 * specify a SokcetImplFactory which should be used by the
	 * <code>Socket</code>
	 * 
	 * @see java.net.Socket#setSocketImplFactory
	 */
	public static synchronized void setSocketImplFactory(SocketImplFactory fac)
			throws IOException {
		Socket.setSocketImplFactory(fac);
	}

	/**
	 * Do the clean up after an NiSocket is not used any more. Release operating
	 * system resources
	 */
	protected void finalize() throws Throwable {
		try {
			close();
		} catch (IOException e) {// $JL-EXC$
			// ignore errors
		} finally {
			super.finalize();
		}
	}
}
