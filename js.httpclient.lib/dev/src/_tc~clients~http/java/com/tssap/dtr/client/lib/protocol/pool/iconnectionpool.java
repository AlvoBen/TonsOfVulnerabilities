package com.tssap.dtr.client.lib.protocol.pool;

import com.tssap.dtr.client.lib.protocol.IConnection;
import com.tssap.dtr.client.lib.protocol.templates.TemplateException;

/**
 * This interface represents generic connection pools. It provides
 * the basic functionality to acquire and release shared connections
 * in a controlled fashion.
 */
public interface IConnectionPool {

	/**
	 * Acquires the shared connection derived from the specified connection
	 * template. If the specified template has not yet been defined,
	 * or currently there is no free connection available exceptions are thrown.
	 * @param templateID the template from which to derive the connection.
	 * @throws OutOfConnections  if currently no connections of the given
	 * template are available.
	 * @throws TemplateException  if the templateId has not yet been defined.
	 * @return a connection matching the given template.
	 */
	IConnection acquireConnection(int templateID)
		throws OutOfConnectionsException, TemplateException;

	/**
	 * Releases the given connection. If the template is unknown or the connection
	 * not aquired the method does nothing.
	 * @param connection the connection to release.
	 * @param closeConnection  closes the associated connection
	 */
	void releaseConnection(IConnection connection, boolean closeConnection);

}
