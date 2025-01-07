package com.tssap.dtr.client.lib.protocol.pool;

import com.tssap.dtr.client.lib.protocol.IConnection;
import com.tssap.dtr.client.lib.protocol.templates.TemplateException;

/**
 * Wrapper class for connection pools providing synchronized access
 * to connection of the wrapped pool.
 */
public class SynchronizedConnectionPool implements IConnectionPool {

	private IConnectionPool pool;

	/**
	 * Creates a synchronized wrapper for the given connection pool.
	 * @param pool  the connection pool to synchronize
	 */
	public SynchronizedConnectionPool(IConnectionPool pool) {
		this.pool = pool;
	}

	/**
	 * Acquires the shared connection derived from the specified connection
	 * template. If the specified template has not yet been defined,
	 * or the shared connection currently is in use exceptions are thrown
	 * @param templateID the template from which to derive the connection.
	 * @throws OutOfConnections  if currently no connections of the given
	 * type are available.
	 * @throws TemplateException  if the templateId has not yet been defined.
	 * @return The shared connection matching the given template.
	 */
	public synchronized IConnection acquireConnection(int templateID) 
	throws OutOfConnectionsException, TemplateException 
	{
		return pool.acquireConnection(templateID);
	}

	/**
	 * Releases the given connection. If the template is unknown or the connection
	 * not aquired the method does nothing.
	 * @param templateID the ID of the connection template to release.
	 */
	public synchronized void releaseConnection(IConnection connection, boolean closeConnection) {
		pool.releaseConnection(connection, closeConnection);
	}

}
