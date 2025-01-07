package com.sap.sdm.is.cs.cmd;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * Title: Software Deployment Manager
 * 
 * Description: The interface defines operations related with comunication
 * between SDM command server and its clients.
 * 
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date 2003-11-21
 * 
 * @author dimitar-d
 * @version 1.0
 * @since 6.40
 * 
 */
public interface CmdConnectionHandler {

	/**
	 * @return <code>InputStream</code> object reference, which is get from the
	 *         established connection between server and client.
	 */
	public InputStream getInputStream();

	/**
	 * @return <code>OutputStream</code> object reference, which is get from the
	 *         established connection between server and client.
	 */
	public OutputStream getOutputStream();

	/**
	 * @return <code>CmdIF</code> which is read from the opened connection.
	 */
	public CmdIF receive();

	/**
	 * The timeout is specific for each of the implementors.
	 * 
	 * @return <code>CmdIF</code> which is read with timeout from the opened
	 *         connection.
	 */
	public CmdIF receiveWithTimeout();

	/**
	 * @param command
	 *            <code>CmdIF</code> object reference, which has to be send.
	 */
	public void send(CmdIF command);
}
