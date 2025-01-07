package com.sap.sdm.is.cs.cmd.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.sap.sdm.is.cs.cmd.CmdConnectionHandler;
import com.sap.sdm.is.cs.cmd.CmdError;
import com.sap.sdm.is.cs.cmd.CmdErrorFactory;
import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.CmdNoReply;
import com.sap.sdm.is.cs.cmd.CmdXMLFactory;
import com.sap.sdm.is.cs.cmd.NoResponseCmdIF;
import com.sap.sdm.is.cs.cmd.client.CmdClient;
import com.sap.sdm.is.cs.cmd.client.CmdClientPostProcessor;
import com.sap.sdm.is.cs.cmd.client.impl.ni.NiSocket;
import com.sap.sdm.is.cs.ncwrapper.NCWrapperFactory;
import com.sap.sdm.is.cs.ncwrapper.NetComm;
import com.sap.sdm.util.log.Logger;
import com.sap.sdm.util.log.Trace;

final class CmdClientImpl extends CmdClient implements CmdConnectionHandler {

	private final static Trace trace = Trace.getTrace(CmdClientImpl.class);
	private final static Logger log = Logger.getLogger();

	private static final SimpleDateFormat formatter;
	private static long lastTime = 0;
	private static long currentTime = 0;
	private static String currentTimestamp = null;
	static {
		formatter = new SimpleDateFormat("yyyyMMddHHmmss SSSS");
		// Use UTC time as timestamp
		// formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		formatter.setTimeZone(TimeZone.getDefault());
		currentTime = System.currentTimeMillis();
		lastTime = currentTime;

	}

	private String host = null;
	private int port = 0;
	private String localHost = null;
	private int localPort = 0;
	private boolean ioExceptionThrown = false;
	private boolean suppressErrorMessages = false;
	private NiSocket server = null;
	private NetComm nc = null;
	private static int loginTimeout = 0;

	CmdClientImpl(String _localHost, int _localPort, int _port, String _host,
			boolean suppressErrorMessages, int lTimeout) throws IOException {
		CmdClient.setInstance(this);
		this.localHost = _localHost;
		this.localPort = _localPort;
		this.host = _host;
		this.port = _port;
		this.suppressErrorMessages = suppressErrorMessages;
		loginTimeout = lTimeout;
		init();
	}

	CmdClientImpl(int _port, String _host) throws IOException {
		this(DEFAULT_CLIENT_HOST, DEFAULT_CLIENT_PORT, _port, _host, false,
				DEFAULT_LOGIN_TIMEOUT);
	}

	CmdClientImpl(int _port, String _host, boolean suppressErrorMessages)
			throws IOException {
		this(DEFAULT_CLIENT_HOST, DEFAULT_CLIENT_PORT, _port, _host,
				suppressErrorMessages, DEFAULT_LOGIN_TIMEOUT);
	}

	CmdClientImpl(int _localPort, int _port, String _host, int lTimeout)
			throws IOException {
		this(DEFAULT_CLIENT_HOST, _localPort, _port, _host, false, lTimeout);
	}

	CmdClientImpl(String _localHost, int _localPort, int _port, String _host,
			int lTimeout) throws IOException {
		this(_localHost, _localPort, _port, _host, false, lTimeout);
	}

	CmdClientImpl(int _port, String _host, int lTimeout) throws IOException {
		this(DEFAULT_CLIENT_HOST, DEFAULT_CLIENT_PORT, _port, _host, false,
				lTimeout);
	}

	CmdClientImpl(int _port) throws IOException {
		this(DEFAULT_CLIENT_HOST, DEFAULT_CLIENT_PORT, _port,
				DEFAULT_REMOTE_HOST, false, DEFAULT_LOGIN_TIMEOUT);
	}

	CmdClientImpl(int _port, boolean suppressErrorMessages) throws IOException {
		this(DEFAULT_CLIENT_HOST, DEFAULT_CLIENT_PORT, _port,
				DEFAULT_REMOTE_HOST, suppressErrorMessages,
				DEFAULT_LOGIN_TIMEOUT);
	}

	private void init() throws IOException {
		OutputStream out = null;
		InputStream in = null;
		try {
			/*
			 * this is not able to use routes for saprouter InetAddress inetAddr
			 * = null; if (host != null) inetAddr = InetAddress.getByName
			 * (host); else inetAddr = InetAddress.getLocalHost();
			 * 
			 * this.server = new NiSocket(inetAddr, port);
			 */

			InetAddress localHostAddress = InetAddress.getByName(localHost);
			this.server = new NiSocket(host, port, localHostAddress, localPort);

			this.server.setTcpNoDelay(true);

			out = this.server.getOutputStream();
			in = this.server.getInputStream();
		} catch (IOException ioe) {
			if (!(this.suppressErrorMessages)) {
				log.error("ERROR: Could not establish connection to server "
						+ host + " at port " + port + ": " + ioe.getMessage());
			}
			throw new IOException(ioe.getMessage());
		}

		this.nc = NCWrapperFactory.getInstance().createNetComm(in, out);
	}

	public CmdIF processCommandWithTimeout(CmdIF _outboundCommand) {
		return processCommand(_outboundCommand, true);
	}

	public CmdIF processCommand(CmdIF _outboundCommand) {
		return processCommand(_outboundCommand, false);
	}

	private CmdIF processCommand(CmdIF _outboundCommand, boolean withTimeout) {
		/*
		 * Client eventually may use this method on already closed connection.
		 * As a result of this NullPointerExceptions were triggered
		 */
		if (isClosed()) {
			return onCommunicationClosed();
		}

		try {
			trace.entering("processCommand(CmdIF "
					+ _outboundCommand.getMyName() + ", withTimeout "
					+ withTimeout + ")");
			CmdIF result = null;
			// specifically handle CmdTransferFile (transfer file as bytes)
			if (_outboundCommand != null) {
				boolean reply = true;
				if (_outboundCommand instanceof NoResponseCmdIF) {
					reply = ((NoResponseCmdIF) _outboundCommand).reply();
				}

				mytrace("sendAndReceive");
				result = sendAndReceive(CmdXMLFactory.getInstance(),
						_outboundCommand, withTimeout, reply);
				mytrace("finished sendAndReceive");

			}
			return result;
		} finally {
			trace.exiting();
		}

	}

	private CmdError onCommunicationClosed() {
		if (CmdClient.errorHandler != null) {
			errorHandler.onError();
		}
		return com.sap.sdm.is.cs.cmd.CmdErrorFactory
				.createCmdError("Connection between SDM client and server is broken");

	}

	/**
	 * Clients eventually may use several Threads. Make sure those Threads don't
	 * interfere in sending and receiving of messages
	 ** 
	 * @param factory
	 * @param _outboundCommand
	 * @param withTimeout
	 * @param receive
	 * @return The Cmd received from server or CmdNoReply if no reply is to be
	 *         received. Calls the error handler if the connection seems to be
	 *         broken
	 */
	private synchronized CmdIF sendAndReceive(CmdXMLFactory factory,
			CmdIF outboundCommand, boolean withTimeout, boolean receive) {
		try {
			trace.entering("sendAndReceive(CmdXMLFactory ...," + "CmdIF "
					+ outboundCommand.getMyName() + ", withTimeout "
					+ withTimeout + ", receive " + receive + ")");
			mytrace("xmlize");
			String output = factory.getXMLizer(outboundCommand.getMyName())
					.toXMLString(outboundCommand);
			mytrace("xmlized --- start send and receive");

			String input = null;
			mytrace("send String part");
			nc.send(output);
			mytrace("finished sending string part");

			CmdIF result = null;

			if (receive) {
				mytrace("receive String part from Server");
				if (withTimeout == true) {
					input = this.receiveFromServerWithTimeout(nc, server);
				} else {
					input = this.receiveFromServer(nc);
				}
				if (!(input == null || isIOExceptionThrown())) {
					mytrace("finished receiving String part from Server");
					CmdIF inboundCommand = factory.fromXmlString(input);
					mytrace("returning " + inboundCommand.getMyName());
					result = inboundCommand;
				} else {
					mytrace("connection was broken");
					try {
						close();
					} catch (IOException e) {// $JL-EXC$
						// ignore errors
					}
					return onCommunicationClosed();
				}
			} else {
				mytrace("returning CmdNoReply");
				result = new CmdNoReply();
			}

			mytrace("start post processing");
			result = CmdClientPostProcessor.process(outboundCommand, result,
					this);
			mytrace("finished post processing");

			return result;

		} finally {
			trace.exiting();
		}

	}

	private String receiveFromServer(NetComm _nc) {
		try {
			trace.entering("receiveFromServer(NetComm ..)");
			StringBuffer inputBuffer = new StringBuffer(100);
			String oneInput = null;
			String result = null;
			ioExceptionThrown = false;
			boolean goon = true;
			do {
				try {
					oneInput = _nc.receive();
					// trace.debug("   received part:" + oneInput);
					if (oneInput.equals(_nc.getEOCS()))
						goon = false;
					else
						inputBuffer.append(oneInput);
				} catch (IOException ioe) {
					assert (Logger.getLogger() != null);
					Logger
							.getLogger()
							.error(
									"An IOException occurred while reading from the Server: ",
									ioe);
					ioExceptionThrown = true;
					return null;
				}
			} while (goon);

			result = inputBuffer.toString();
			return result;
		} finally {
			trace.exiting();
		}

	}

	private String receiveFromServerWithTimeout(NetComm _nc, NiSocket socket) {
		trace.entering("receiveFromServerWithTimeout(NetComm ...)");
		int oldTimeout = 0;
		try {
			StringBuffer inputBuffer = new StringBuffer(100);
			String oneInput = null;
			String result = null;
			ioExceptionThrown = false;
			boolean goon = true;
			try {
				oldTimeout = socket.getSoTimeout();
				socket.setSoTimeout(loginTimeout);
			} catch (SocketException soE) {
				trace.debug("Caught SocketException: " + soE.getMessage());
				log.error("Caught SocketException: " + soE.getMessage());
				return null;
			}
			do {
				try {
					oneInput = _nc.receive();
					if (oneInput.equals(_nc.getEOCS()))
						goon = false;
					else
						inputBuffer.append(oneInput);
				} catch (InterruptedIOException interruptE) {
					ioExceptionThrown = true;
					return null;
				} catch (IOException ioe) {
					assert (Logger.getLogger() != null);
					Logger
							.getLogger()
							.error(
									"An IOException occurred while reading from the Server: ",
									ioe);
					ioExceptionThrown = true;
					return null;
				}
			} while (goon);

			result = inputBuffer.toString();
			return result;

		} finally {
			try {
				socket.setSoTimeout(oldTimeout);
			} catch (SocketException sE) {
				trace.debug("Caught SocketException: " + sE.getMessage());
				log.error("Caught SocketException: " + sE.getMessage());
			}
			trace.exiting();
		}
	}

	public boolean isIOExceptionThrown() {
		return ioExceptionThrown;
	}

	public boolean isClosed() {
		return (server == null && nc == null);
	}

	public void close() throws IOException {
		if (server != null) {
			this.server.setSoLinger(true, 0);
			this.server.close();
		}
		this.server = null;
		this.nc = null;
	}

	private static void mytrace(String msg) {
		// Format the current time.
		currentTime = System.currentTimeMillis();
		currentTimestamp = formatter.format(new Date(currentTime));
		trace.debug(currentTimestamp + "/" + (currentTime - lastTime)
				+ " Client: " + msg);
		lastTime = currentTime;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdConnectionHandler#getInputStream()
	 */
	public InputStream getInputStream() {
		return this.nc != null ? this.nc.getInputStream() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdConnectionHandler#getOutputStream()
	 */
	public OutputStream getOutputStream() {
		return this.nc != null ? this.nc.getOutputStream() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdConnectionHandler#receive()
	 */
	public CmdIF receive() {
		if (!isClosed()) {
			final String strReceived = receiveFromServer(this.nc);
			if (strReceived != null) {
				return CmdXMLFactory.getInstance().fromXmlString(strReceived);
			} else if (isIOExceptionThrown()) {
				try {
					close();
				} catch (IOException e) {// $JL-EXC$
					// ignore errors
				}
				return onCommunicationClosed();
			} else {
				return CmdErrorFactory
						.createCmdError("Not a valid data has been received!");
			}
		} else {
			return onCommunicationClosed();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdConnectionHandler#receiveWithTimeout()
	 */
	public CmdIF receiveWithTimeout() {
		if (!isClosed()) {
			final String strReceived = receiveFromServerWithTimeout(this.nc,
					this.server);
			if (strReceived != null) {
				return CmdXMLFactory.getInstance().fromXmlString(strReceived);
			}
			if (isIOExceptionThrown()) {
				try {
					close();
				} catch (IOException e) {// $JL-EXC$
					// ignore errors
				}
				return onCommunicationClosed();
			} else {
				return CmdErrorFactory
						.createCmdError("Not a valid data has been received!");
			}
		} else {
			return onCommunicationClosed();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.is.cs.cmd.CmdConnectionHandler#send(com.sap.sdm.is.cs.cmd
	 * .CmdIF)
	 */
	public void send(CmdIF command) {
		final String xmlizedCommand = CmdXMLFactory.getInstance().getXMLizer(
				command.getMyName()).toXMLString(command);
		this.nc.send(xmlizedCommand);
	}

}
