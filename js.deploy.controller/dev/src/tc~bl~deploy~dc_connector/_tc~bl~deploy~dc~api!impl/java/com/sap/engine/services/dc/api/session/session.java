/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.session;

import javax.naming.InitialContext;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.impl.IRemoteReferenceHandler;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Session container responsible for keeping all needed information about a
 * single client session.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Georgi Danov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public interface Session {
	public static final String CTX_FORCE_REMOTE = "force_remote";
	public static final String CTX_TRANSPORT_LAYER_QUEUE = "TransportLayerQueue";
	public static final String CTX_TRANSPORT_LAYER_QUEUE_NONE = "None";
	public static final String CTX_TRANSPORT_LAYER_QUEUE_SSL = "ssl";
	public static final String CTX_TRANSPORT_LAYER_QUEUE_HTTPTUNNELING = "httptunneling";
	public static final String CTX_TRANSPORT_LAYER_QUEUE_HTTPS = "https";

	/**
	 * closes this session and frees all resources
	 * 
	 * @throws ConnectionException
	 */
	public void close() throws ConnectionException;

	/**
	 * creates <code>InitialContext</code>
	 * 
	 * @return InitialContext
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public InitialContext getContext() throws AuthenticationException,
			ConnectionException;

	/**
	 * 
	 * @return <code>FileTransfer<code> reference
	 * @throws ConnectionException
	 */
	public com.sap.engine.services.file.FileTransfer getFileTransfer()
			throws ConnectionException;

	/**
	 * creates new CM object.
	 * 
	 * @return @throws APIConnectionException
	 */
	public com.sap.engine.services.dc.cm.CM createCM()
			throws ConnectionException;

	/**
	 * 
	 * @return associated with this session <code>DALog</code>
	 */
	public com.sap.engine.services.dc.api.util.DALog getLog();

	/**
	 * enable/disable traces to the trace and log file. This method is only for
	 * internal purposes and clients should not invoke this method implicitely.
	 * 
	 * @param dumpTrace
	 *            enable/disable traces
	 */
	public void setDumpTrace(boolean dumpTrace);

	/**
	 * @return host name to which should connect
	 */
	public String getHost();

	/**
	 * @return p4 port to which should connect
	 */
	public int getP4Port();

	/**
	 * 
	 * @return the port of the sapcontol web service of the java instance
	 */
	public int getSapcontrolPort();

	/**
	 * The method should be used for managing remote reference handlers, which
	 * should be closed when the session is terminated.
	 * 
	 * @param remRefsHandler
	 */
	public void addRemoteReferenceHandler(IRemoteReferenceHandler remRefsHandler);

}