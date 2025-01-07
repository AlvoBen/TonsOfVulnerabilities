package com.tssap.dtr.client.lib.protocol.pool;

import java.util.NoSuchElementException;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.Connection;
import com.tssap.dtr.client.lib.protocol.IConnection;
import com.tssap.dtr.client.lib.protocol.IConnectionTemplate;
import com.tssap.dtr.client.lib.protocol.impl.HashMapIntInt;
import com.tssap.dtr.client.lib.protocol.impl.HashMapIntObject;
import com.tssap.dtr.client.lib.protocol.templates.ITemplateProvider;
import com.tssap.dtr.client.lib.protocol.templates.InvalidTemplateIDException;
import com.tssap.dtr.client.lib.protocol.templates.TemplateException;

/**
 * Basic connection pool implementation capable of providing and pooling 
 * connection to various hosts in parallel. The pool is backed by a user-defined
 * template provider. The pool allows to define limits for the overall amount of
 * connections provided, and also individual limits for each template.
 * Templates can be placed in the running system by calling the <code>refresh</code>
 * or <code>refreshAll</code> method, respectively.<br/>
 * Note, this pool implementation is not properly synchronized yet! 
 * If you need synchronization for the method <code>acquireConnection</code> 
 * and <code>releaseConnection</code> wrap the pool with a 
 * <code>SynchronizedConnectionPool</code>.
 * Connection entries can be in one of the following states:
 * - NONEXISTING
 * - CREATED
 * - FREE
 * - USED
 *
 */
public final class ConnectionPool implements IConnectionPool
{
	/** The table of free connection instances provided by this factory */
	private HashMapIntObject freeConnections = new HashMapIntObject();

	/** The table of currently used connection instances provided by this factory */
	private HashMapIntObject usedConnections = new HashMapIntObject();

	/** The overall limit for the number of connections */
	private int limit = -1;
	/** The number of currently stored connections */
	private int size;

	/** The limit for the number of connections per template */
	private HashMapIntInt limits = new HashMapIntInt();
	private int defaultLimit = -1;

	/** The number of currently stored connections per template */
	private HashMapIntInt sizes = new HashMapIntInt();
	/** The number of currently used connections */
	private HashMapIntInt countUsed = new HashMapIntInt();

	/** The provider for connection templates */
	private ITemplateProvider templateProvider;

	/** Expiration timeout monitor */
	private Thread monitor;
	private volatile boolean monitoring;
	private HashMapIntObject monitored = new HashMapIntObject();

	/** trace location*/
	private static Location TRACE = Location.getLocation(ConnectionPool.class);

	/** Entry of the pool tables */
	private class ConnectionEntry
	{
		public int templateID;
		public Connection connection;
		public ConnectionEntry next;
		public ConnectionEntry(int templateID, Connection connection)
		{
			this.templateID = templateID;
			this.connection = connection;
		}
		public String toString()
		{
			return "templateID=" + templateID + ", connID="
				+ Integer.toHexString(connection.hashCode());
		}
	}

	/**
	 * Create a connection pool for the given template provider.
	 * @param templateProvider  the template provider to use.
	 */
	public ConnectionPool(ITemplateProvider templateProvider)
	{
		this.templateProvider = templateProvider;
	}

	/**
	 * Sets the overall limit for the number of connections provided by this pool.
	 * @param limit  the limit for the number of connections, or -1 if no limit
	 * should be applied.
	 */
	public void setOverallLimit(int limit)
	{
		this.limit = limit;
		if (getSize() > limit) removeFreeEntries();
	}

	/**
	 * Returns the overall limit for the number of connections provided by this pool.
	 * @return  the limit for the number of connections, or -1 if no limit is applied.
	 */
	public int getOverallLimit()
	{
		return limit;
	}

	/**
	 * Sets the limit for the number of connections provided by this pool for
	 * a certain template. The default value is unbounded.
	 * @param templateID  the ID of the template for which to limit the number
	 * of connections, or -1 for an unbounded limit.
	 * @param  the limit for the number of connections.
	 */
	public void setLimit(int templateID, int limit)
	{
		limits.put(templateID, limit);
		// sizes.put(templateID, 0);
		// OK: instead just implements a soft release strategy
		int n = getSize(templateID);
		n = n > 0 ? n : 0;
		if (limit > 0 && n > limit)
			removeFreeEntries(templateID, n - limit);
		// try to remove some unused entries
	}

	/**
	 * Returns the limit for the number of connections for the given template
	 * @param templateID  the ID of the template for which to return the limit
	 * @return the limit for the number of connections for the given template
	 */
	public int getLimit(int templateID)
	{
		int result = -1;
		if (limits != null)
		{
			try
			{
				result = limits.get(templateID);
			}
			catch (NoSuchElementException e)
			{ //$JL-EXC$
				TRACE.debugT("getLimit(int)",
					"Template ID not yet defined [ID=" + templateID + "]");
			}
		}
		return (result < 0) ? defaultLimit : result;
	}

	/**
	 * Sets the default limit for the number of connections provided by this pool for
	 * each template. If no limit is defined for a certain limit, this
	 * default limit is applied. The default value is unbounded.
	 * @param limit  the limit for the number of connections.
	 */
	public void setDefaultLimit(int limit)
	{
		defaultLimit = limit;
	}

	/**
	 * Returns the default limit for the number of connections provided by this pool for
	 * each template. 
	 * @return  the limit for the number of connections.
	 */
	public int getDefaultLimit()
	{
		return defaultLimit;
	}

	/**
	 * Returns the overall size of the pool. Counts the number of currently
	 * used and free connections in the pool and returns the sum.
	 * @return the number of currently attached connections
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Returns the overall number of connections currently attached
	 * to the pool for the given template.
	 * @param templateID  the template
	 * @return the number of currently attached connections
	 */
	public int getSize(int templateID)
	{
		// OK: Why so????
		// Inconsistent with countUsed(int)
		int n = -1;
		try
		{
			n = sizes.get(templateID);
		}
		catch (NoSuchElementException e)
		{ //$JL-EXC$
			TRACE.debugT("getSize(int)",
				"Template ID not yet defined [ID=" + templateID + "]");
			try
			{
				templateProvider.getConnectionTemplate(templateID);
				n = 0;
			}
			catch (InvalidTemplateIDException e1)
			{ //$JL-EXC$
				TRACE.catching("getSize(int)", e1);
				TRACE.debugT("getSize(int)",
					"Template provider reports error for [ID=" + templateID	+ "]");
			}
		}

		return n;
	}

	/**
	 * Returns the number of currently used connections in the pool.
	 * @param templateID  the template
	 * @return the number of currently used connections
	 * @throws InvalidTemplateIDException  if the given template ID is unknown
	 * or invalid.
	 */
	public int countUsed(int templateID) throws InvalidTemplateIDException
	{
		try
		{
			return countUsed.get(templateID);
		}
		catch (NoSuchElementException e)
		{
			templateProvider.getConnectionTemplate(templateID);
			//			throw new InvalidTemplateIDException("The requested connection template " 
			//				+ templateID + " is unknown.");
			// OK: InvalidTemplateIDException will be thrown by
			// getConnectionTemplate if it is unknown
			return 0;
		}
	}

	/**
	 * Returns the number of currently free connections in the pool.
	 * @param templateID  the template
	 * @return the number of currently used connections
	 * @throws InvalidTemplateIDException  if the given template ID is unknown
	 * or invalid.
	 */
	public int countFree(int templateID) throws InvalidTemplateIDException
	{
		return getSize(templateID) - countUsed(templateID);
	}

	/**
	 * Determines whether a connection for the given template is available
	 * or could be created.
	 * @param templateID  the template
	 * @return  true, if a free connection is available, or a new connection
	 * could be created on demand.
	 * @throws InvalidTemplateIDException  if the given template ID is unknown
	 * or invalid.
	 */
	public boolean hasConnection(int templateID)
		throws InvalidTemplateIDException
	{
		int free = countFree(templateID);
		return free > 0 || (free == 0 && checkCapacity() && checkCapacity(templateID));
	}

	/**
	 * Acquires a connection derived from the specified connection
	 * template. If the specified template has not yet been defined,
	 * or currently there is no free connection available exceptions are thrown.
	 * Usually the pool will return the connection that most recently has been
	 * released to the pool. This strategy makes it more likely that the
	 * connection still is valid and usable. 
	 * @param templateID the template from which to derive the connection.
	 * @throws OutOfConnections  if currently no connections of the given
	 * template are available.
	 * @throws TemplateException  if the templateId has not yet been defined.
	 * @return a connection matching the given template.
	 */
	public IConnection acquireConnection(int templateID)
		throws OutOfConnectionsException, TemplateException
	{
		return aquireFreeEntry(templateID).connection;
	}

	/**
	 * Releases the given connection. If the connection is not acquired the method 
	 * does nothing.
	 * Resets changed connection parameters (like read timeout) to the values of
	 * the corresponding template. The socket of the connection and the current
	 * session remain untouched, unless <code>closeConnection</code> is set.  
	 * @param connection the connection to release.
	 * @param closeConnection  closes the associated connection
	 */
	public void releaseConnection(IConnection connection, boolean closeConnection)
	{
		releaseUsedEntry(connection, closeConnection);
	}

	/**
	 * Removes all connections from the pool. For consecutive calls to
	 * <code>aquireConnection</code> the template provider is ask for
	 * a new current template. This method should be used, if templates
	 * in the provider have been changed. 
	 * Acquired connections still remain valid.  However, if such 
	 * a connection is released it is not returned to the pool.
	 */
	public void refreshAll()
	{
		removeFreeEntries();
		removeUsedEntries();
	}

	/**
	 * Removes all connections corresponding to the given template from the pool. 
	 * For consecutive calls to <code>aquireConnection</code> the template 
	 * provider is ask for a new template. This method should be used, if 
	 * the corresponding templates in the provider has been changed. 
	 * Acquired connections still remain valid. However, if such 
	 * a connection is released it is not returned to the pool.
	 */
	public void refresh(int templateID)
	{
		removeFreeEntries(templateID);
		removeUsedEntries(templateID);
	}

	/**
	 * Experimental. Start a thread that monitors
	 * and closes expired connections
	 * @param enable
	 */
	private void enableAutoClose(boolean enable)
	{
		if (enable)
		{
			if (monitor != null)
			{
				monitoring = false;
				monitor.interrupt();
			}
			Runnable r = new Runnable()
			{
				public void run()
				{
					try
					{
						monitor();
					}
					catch (Exception e)
					{ //$JL-EXC$
						TRACE.catching("enableAutoClose(boolean enable)", e);
					}
				}
			};
			monitoring = true;
			monitor = new Thread(r, IConnectionTemplate.USER_AGENT);
			monitor.start();
		}
		else
		{
			if (monitor != null)
			{
				monitoring = false;
				monitor.interrupt();
			}
		}
	}

	private IConnectionTemplate getTemplate(int templateID)
		throws InvalidTemplateIDException
	{
		return templateProvider.getConnectionTemplate(templateID);
	}

	private ConnectionEntry aquireFreeEntry(int templateID)
		throws InvalidTemplateIDException, OutOfConnectionsException
	{
		ConnectionEntry entry =
			(ConnectionEntry) freeConnections.get(templateID);
		if (entry == null) // OK: State NONEXISTING
			entry = addEntry(templateID); // OK: State CREATED
		seizeEntry(entry); // OK: State USED
		return entry;
	}

	private void releaseUsedEntry(IConnection connection, boolean closeConnection)
	{
		ConnectionEntry entry = getUsedEntry(connection);
		if (entry != null)
		{
			releaseEntry(entry); // OK: State FREE
			try
			{
				restoreConnectionAttributes(entry);
			}
			catch (InvalidTemplateIDException e)
			{
				// if the template is invalid or has been removed
				// don't put the connection back into pool
				TRACE.catching("releaseUsedEntry(IConnection,boolean)", e);
				TRACE.debugT("releaseUsedEntry(IConnection,boolean)",
					"The requested connection template [ID=" + entry.templateID
						+ "] is invalid or has been removed");
			}
		}
		if (closeConnection) connection.close();
	}

	// OK: <23.12.2004> Added methods, dealing with states of the entry in the pool

	/**
	 * Creates an entry, Transition NONEXISTING->CREATED, endstate CREATED
	 * @param templateID Template ID the entry should be created for
	 */
	private ConnectionEntry addEntry(int templateID)
		throws InvalidTemplateIDException, OutOfConnectionsException
	{
		ConnectionEntry entry = createEntry(templateID);
		// OK: State_CREATED
		increaseSize(templateID);
		return entry;
	}

	/**
	 * Deletes free entry, Transition FREE->NONEXISTING, endstate NONEXISTING
	 * @param entry
	 */
	private void delFreeEntry(ConnectionEntry entry)
	{
		decreaseSize(entry.templateID);
		freeConnections.put(entry.templateID, entry.next);
		entry.connection = null;
		entry.next = null;
	}

	/**
	 * Deletes used entry, Transition USED->NONEXISTING, endstate NONEXISTING
	 * @param entry
	 */
	private void delUsedEntry(ConnectionEntry entry)
	{
		usedConnections.remove(entry.connection.hashCode());
		decreaseUsed(entry.templateID);
		decreaseSize(entry.templateID);
	}
	/**
	 * Seizes free entry, Transition FREE->USED, endstate USED
	 * @param entry
	 */
	private void seizeEntry(ConnectionEntry entry)
	{
		usedConnections.put(entry.connection.hashCode(), entry);
		increaseUsed(entry.templateID);
		freeConnections.put(entry.templateID, entry.next);
	}

	/**
	 * Releases used entry, Transition USED->FREE, endstate FREE
	 * @param entry
	 */
	private void releaseEntry(ConnectionEntry entry)
	{
		usedConnections.remove(entry.connection.hashCode());
		decreaseUsed(entry.templateID);
		ConnectionEntry currentFree =
			(ConnectionEntry)freeConnections.get(entry.templateID);
		entry.next = currentFree;
		freeConnections.put(entry.templateID, entry);
	}

	private void restoreConnectionAttributes(ConnectionEntry entry)
		throws InvalidTemplateIDException
	{
		IConnectionTemplate template =
			templateProvider.getConnectionTemplate(entry.templateID);
		entry.connection.setSocketReadTimeout(template.getSocketReadTimeout());
		entry.connection.setRequestRepetitions(template.getRequestRepetitions(),
			template.getRepeatOnTimeout());
	}

	private ConnectionEntry getUsedEntry(IConnection connection)
	{
		return (ConnectionEntry)usedConnections.get(connection.hashCode());
	}

	/**
	 * Factory method, just creating an entry with checking prerequisites (capacities)
	 * @param templateID
	 * @return
	 * @throws InvalidTemplateIDException
	 * @throws OutOfConnectionsException
	 */
	private ConnectionEntry createEntry(int templateID)
		throws InvalidTemplateIDException, OutOfConnectionsException
	{
		IConnectionTemplate template = getTemplate(templateID);
		if (template == null)
		{
			throw new InvalidTemplateIDException("The requested connection template [ID="
				+ templateID + "] is unknown");
		}

		checkCapacities(template, templateID);

		ConnectionEntry newEntry = new ConnectionEntry(templateID, new Connection(template));

		if (TRACE.beDebug())
		{
			TRACE.debugT("createEntry(int)",
				"Connection pool entry created [{0}]",
				new Object[] { newEntry.toString()});
		}

		return newEntry;
	}

	private boolean checkCapacities(IConnectionTemplate template, int templateID)
		throws OutOfConnectionsException
	{
		boolean ok = checkCapacity();
		if (!ok) compactFreeEntries(); // try to remove some unused entries
		if (!ok)
			throw new OutOfConnectionsException(
				"The pool size has reached the"
					+ " maximum number of connections [limit=" + limit + "]");
		ok = checkCapacity(templateID);
		if (!ok) // TODO: Check if getUrl() can throw NullPointerException
			throw new OutOfConnectionsException("The pool size has reached the"
				+ " maximum number of connections for destination '"
				+ template.getUrl() + "' [ID=" + templateID + "]");
		return true;
	}

	private boolean checkCapacity()
	{
		return (limit < 0 || size < limit);
	}

	private boolean checkCapacity(int templateID)
	{
		int n = getLimit(templateID);
		n = (n < 0) ? defaultLimit : n;
		return (n < 0 || getSize(templateID) < n);
	}

	private void compactFreeEntries()
	{
		int[] keys = freeConnections.keys();
		for (int i = 0; i < keys.length; ++i)
		{
			ConnectionEntry entry =
				(ConnectionEntry) freeConnections.get(keys[i]);
			if (entry != null)
			{
				entry.connection.close();
				delFreeEntry(entry);
			}
		}
	}

	private void removeFreeEntries()
	{
		int[] keys = freeConnections.keys();
		if (keys != null)
		{
			for (int i = 0; i < keys.length; ++i)
			{
				removeFreeEntries(keys[i]);
			}
		}
	}

	private void removeFreeEntries(int templateID, int num)
	{
		ConnectionEntry entry;
		while ((entry = (ConnectionEntry)freeConnections.get(templateID)) != null && num > 0)
		{
			delFreeEntry(entry);
			num--;
		}
		freeConnections.remove(templateID);
	}

	private void removeFreeEntries(int templateID)
	{
		ConnectionEntry entry;
		while ((entry = (ConnectionEntry)freeConnections.get(templateID)) != null)
		{
			delFreeEntry(entry);
		}
		freeConnections.remove(templateID);
	}

	private void removeUsedEntries()
	{
		int[] keys = usedConnections.keys();
		if (keys != null)
		{
			for (int i = 0; i < keys.length; ++i)
			{
				ConnectionEntry entry = (ConnectionEntry)usedConnections.get(keys[i]);
				delUsedEntry(entry);
			}
		}
	}

	private void removeUsedEntries(int templateID)
	{
		int[] keys = usedConnections.keys();
		if (keys != null)
		{
			for (int i = 0; i < keys.length; ++i)
			{
				ConnectionEntry entry = (ConnectionEntry)usedConnections.get(keys[i]);
				if (entry.templateID == templateID)
				{
					delUsedEntry(entry);
				}
			}
		}
	}

	private void increaseSize(int templateID)
	{
		int n = 0;
		try
		{
			n = sizes.get(templateID);
		}
		catch (NoSuchElementException e)
		{ //$JL-EXC$							
			TRACE.debugT("increaseSize(int templateID)",
				"Non-existing template ID is requested: [ID="
				+ templateID + "]; adding to the pool");
		}
		sizes.put(templateID, n + 1);
		size++;
	}

	private void decreaseSize(int templateID)
	{
		try
		{
			int n = sizes.get(templateID);
			sizes.put(templateID, n - 1);
			size--;
		}
		catch (NoSuchElementException e)
		{ //$JL-EXC$
			TRACE.debugT("decreaseSize(int templateID)",
				"Non-existing template ID is requested: [ID="
				+ templateID + "]; decrease size ignored");
		}
	}

	private void increaseUsed(int templateID)
	{
		int n = 0;
		try
		{
			n = countUsed.get(templateID);
		}
		catch (NoSuchElementException e)
		{ //$JL-EXC$					
			TRACE.debugT("increaseUsed(int templateID)",
				"Non-existing template ID is requested: [ID="
				+ templateID + "]; adding to the pool");
		}
		countUsed.put(templateID, n + 1);
	}

	private void decreaseUsed(int templateID)
	{
		try
		{
			int n = countUsed.get(templateID);
			countUsed.put(templateID, n - 1);
		}
		catch (NoSuchElementException e)
		{ //$JL-EXC$	
			TRACE.debugT("decreaseUsed(int templateID)",
				"Non-existing template ID is requested: [ID="
				+ templateID + "]; decrease size ignored");
		}
	}

	private void monitor()
	{
		while (true)
		{
			synchronized (monitored)
			{
				try
				{
					while (monitoring)
					{
						monitored.wait(Connection.EXPIRATION_TIMEOUT);
					}
				}
				catch (InterruptedException e)
				{ //$JL-EXC$
					TRACE.catching("monitor()", e);
					TRACE.infoT("monitor()",
						"connection auto close monitor [stopped]");
					return;
				}

				if (!monitoring)
				{
					TRACE.infoT("monitor()",
						"connection auto close monitor [stopped]");
					return;
				}

				if (!monitored.isEmpty())
				{
					int[] keys = monitored.keys();
					for (int i = 0; i < keys.length; ++i)
					{
						Connection conn = (Connection)monitored.get(keys[i]);
						if (conn.hasExpired())
						{
							conn.close();
							monitored.remove(keys[i]);
						}
					}
				}
			}
		}
	}

}
