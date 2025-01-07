/*
 * Created on Oct 19, 2004
 */
package com.sap.engine.services.dc.api.session.impl;

import java.rmi.Remote;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.impl.IRemoteReferenceHandler;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.api.util.DAUtils;
import com.sap.engine.services.dc.api.util.NetUtils;
import com.sap.engine.services.dc.api.util.ServiceTimeWatcher;
import com.sap.engine.services.dc.api.util.exception.APIExceptionConstants;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.file.FileTransfer;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;

/**
 * @author Georgi Danov
 * @author Boris Savov
 * 
 */

final class SessionImpl implements Session, IRemoteReferenceHandler {
	private static final String FILE_TRANSFER_NAME = "file";
	private static final String SERVICE_NAME = CM.SERVICE_NAME;// "tc~bl~deploy_controller"
	// ;

	private InitialContext context = null;
	private final Properties ctxProps;
	private final String toString;
	private final DALog daLog;
	private final String host;
	private final int p4port;
	private final int sapcontrolPort;

	// remote references to be handled within an instance of this class
	private Set remoteRefs = new HashSet();
	// remote reference handlers that were detected during the session lifetime
	private Set remoteRefsHandlers = new HashSet();
	// a flag indicating whether the session was explicitly closed
	private boolean isClosed = false;

	SessionImpl(DALog daLog, String host, int p4port, int sapcontrolPort,
			String user, String password, Properties p4props)
			throws AuthenticationException, ConnectionException {

		if (daLog == null) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1143] Log instance can not be null.");
		}
		this.daLog = daLog;

		String tmpHost = null;
		int tmpPort = -1;

		if (host != null) {
			tmpHost = host;
			tmpPort = p4port;
			this.ctxProps = (p4props != null) ? p4props : new Properties();
			this.ctxProps.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sap.engine.services.jndi.InitialContextFactoryImpl");
			this.ctxProps.put(Context.PROVIDER_URL, host + ":" + p4port);
			this.ctxProps.put(Context.SECURITY_PRINCIPAL, user);
			this.ctxProps.put(Context.SECURITY_CREDENTIALS, password);

			// default value is true
			this.ctxProps.put(CTX_FORCE_REMOTE, this.ctxProps.getProperty(
					CTX_FORCE_REMOTE, "true"));

		} else if (p4props != null) {
			String tmp = p4props.getProperty(Context.PROVIDER_URL);
			if (tmp != null && tmp.length() > 0) {
				String[] terms = tmp.split(":");
				if (terms != null && terms.length == 2) {
					try {
						tmpPort = Integer.decode(terms[1]).intValue();
						tmpHost = terms[0];
					} catch (NumberFormatException nfe) {
						daLog
								.logThrowable(
										"ASJ.dpl_api.001166",
										"Wrong values in the additional P4 properties.",
										nfe);
					}
				}
			}
			this.ctxProps = p4props;
		} else {
			this.ctxProps = null;
		}

		if (tmpHost == null) {
			tmpHost = NetUtils.LOCALHOST;
		}

		this.host = tmpHost;
		this.p4port = tmpPort;
		this.sapcontrolPort = sapcontrolPort;

		this.toString = "Session["
		// + user + ":****@"
				+ this.host + ":" + this.p4port + ",hash=" + hashCode() + "]";
		daLog.traceInfo("ASJ.dpl_api.001167",
				"Will try to connect [{0}]:[{1}]", new Object[] {
						this.host, new Integer(this.p4port) });

		getContext();

		if(this.host.equals(NetUtils.LOCALHOST) && this.p4port == -1){
			daLog.traceInfo("ASJ.dpl_api.001290", "Connected to local deploy controller");
		} else {
			daLog.traceInfo("ASJ.dpl_api.001168",
					"Connected to [{0}]:[{1}]", new Object[] {
					this.host, new Integer(this.p4port) });
		}
	}

	public void close() throws ConnectionException {
		Iterator iter = this.remoteRefsHandlers.iterator();
		while (iter.hasNext()) {
			IRemoteReferenceHandler remoteRefsHandler = (IRemoteReferenceHandler) iter
					.next();
			remoteRefsHandler.releaseRemoteReferences();
		}
		remoteRefsHandlers.clear();
		closeContext();
		if (this.daLog != null) {
			this.daLog.close();
		}
		// mark that the session was explicitly closed
		isClosed = true;
	}

	private void closeContext() {
		if (this.context != null) {
			if (daLog.isDebugTraceable()) {
				this.daLog
						.traceDebug("closeContext has been invoked: invoking 'this.context.close()'");
				try {
					this.context.close();
				} catch (final NamingException e) {// $JL-EXC$
				}
			}
		}
	}

	public InitialContext getContext() throws AuthenticationException,
			ConnectionException {
		ClassLoader saveLoader = Thread.currentThread().getContextClassLoader();
		try {
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug("Set context class loader");
			}

			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader());
			ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();

			closeContext();

			try {
				if (daLog.isDebugTraceable()) {
					this.daLog
							.traceDebug("[ B E G I N ].timerId=[{0}]",
									new Object[] { new Long(serviceTimeWatcher
											.getId()) });
				}

				// TODO: trying to get InitialContext is not a very nice way to
				// check if the server is available;
				// use SAP Control WebService, instead
				this.context = (this.ctxProps != null) ? new InitialContext(
						this.ctxProps) : new InitialContext();
				// } catch (NoPerimissionException e){
			} catch (NameNotFoundException e) {
				String exceptionName = DAUtils.getThrowableClassName(e);
				if (daLog.isDebugLoggable()) {
					this.daLog.logThrowable("ASJ.dpl_api.001169",
							"get ctx. [{0}],cause=[{1}]", e, new Object[] {
									exceptionName, e.getMessage() });
				}
				throw new ConnectionException(this.daLog.getLocation(),
						APIExceptionConstants.DA_CANNOT_GET_CONTEXT,
						new String[] { exceptionName, e.getMessage() }, e);

			} catch (NamingException e) {
				checkForTicketException(e);
				String exceptionName = DAUtils.getThrowableClassName(e);
				if (daLog.isDebugLoggable()) {
					this.daLog.logThrowable("ASJ.dpl_api.001170",
							"get ctx. [{0}],cause=[{1}]", e, new Object[] {
									exceptionName, e.getMessage() });
				}
				throw new ConnectionException(this.daLog.getLocation(),
						APIExceptionConstants.DA_CANNOT_GET_CONTEXT,
						new String[] { exceptionName, e.getMessage() }, e);
			} finally {
				if (daLog.isDebugTraceable()) {
					this.daLog.traceDebug("[ E N D ].timerId=[{0}]",
							new Object[] { serviceTimeWatcher
									.getElapsedTimeAsString() });
				}

			}
			return this.context;
		} finally {
			if (saveLoader != null) {
				if (daLog.isDebugTraceable()) {
					this.daLog
							.traceDebug("Get back the original context class loader");
				}

				Thread.currentThread().setContextClassLoader(saveLoader);
			}
		}
	}

	private void checkForTicketException(NamingException ne)
			throws AuthenticationException {
		if (ne == null) {
			return;
		}
		Throwable cause = ne/* .getCause() */;
		int countDown = 50;
		do {
			if (cause == null) {
				break;
			} else if (cause instanceof com.sap.engine.lib.security.LoginExceptionDetails) {
				com.sap.engine.lib.security.LoginExceptionDetails led = (com.sap.engine.lib.security.LoginExceptionDetails) cause;
				byte exceptionCause = led.getExceptionCause();
				if (exceptionCause >= 0) {
					String exceptionName = DAUtils.getThrowableClassName(cause);

					this.daLog.logThrowable("ASJ.dpl_api.001171",
							"[{0}],cause=[{1}]", cause, new Object[] {
									exceptionName, cause.getMessage() });

					throw new AuthenticationException(this.daLog.getLocation(),
							APIExceptionConstants.DA_CANNOT_GET_CONTEXT,
							new String[] { exceptionName,
									cause.getLocalizedMessage() }, cause);
					// com.sap.engine.lib.security.LoginExceptionDetails.
					// WRONG_USERNAME_PASSWORD_COMBINATION
				}
			}
			if (--countDown <= 0) {
				this.daLog
						.logError(
								"ASJ.dpl_api.001172",
								"Too many causes. Break in order to prevent endless loop.Cause=[{0}]",
								new Object[] { cause });
				break;// as a backup. Never fall in endless loop
			}
		} while ((cause = cause.getCause()) != null);
	}

	public FileTransfer getFileTransfer() throws ConnectionException {
		ClassLoader saveLoader = Thread.currentThread().getContextClassLoader();
		try {
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug("Set context class loader");
			}

			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader());
			final FileTransfer ft = (FileTransfer) this.context
					.lookup(FILE_TRANSFER_NAME);
			if (ft == null) {
				if (daLog.isDebugLoggable()) {
					this.daLog.logDebug("The FileTransfer is null.");
				}

				throw new ConnectionException(this.daLog.getLocation(),
						APIExceptionConstants.DA_CANNOT_LOOKUP_SERVICE,
						new String[] { FILE_TRANSFER_NAME });
			}
			// register the obtained remote object reference
			registerRemoteReference(ft);
			return ft;
		} catch (NamingException e) {
			throw new ConnectionException(this.daLog.getLocation(),
					APIExceptionConstants.DC_CONNECTION_EXCEPTION,
					new String[] { DAUtils.getThrowableClassName(e),
							e.getMessage() }, e);
		} finally {
			if (saveLoader != null) {
				if (daLog.isDebugTraceable()) {
					this.daLog
							.traceDebug("Get back the original context class loader");
				}

				Thread.currentThread().setContextClassLoader(saveLoader);
			}
		}
	}

	public CM createCM() throws ConnectionException {
		ClassLoader saveLoader = Thread.currentThread().getContextClassLoader();
		try {
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug("Set context class loader");
			}

			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader());
			CM cm = (CM) this.context.lookup(SERVICE_NAME);
			if (cm == null) {
				if (daLog.isDebugLoggable()) {
					this.daLog
							.logDebug("Try to get CM from InitialContext: the CM is null.");
				}

				throw new ConnectionException(this.daLog.getLocation(),
						APIExceptionConstants.DA_CANNOT_LOOKUP_SERVICE,
						new String[] { SERVICE_NAME });
			}
			// register the obtained remote object reference
			registerRemoteReference(cm);
			return cm;
		} catch (Exception e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog.logThrowable("ASJ.dpl_api.001173", "[{0}],cause=[{1}]",
					e, new Object[] { exceptionName, e.getMessage() });

			throw new ConnectionException(this.daLog.getLocation(),
					APIExceptionConstants.DA_SERVICE_IS_NOT_RUNNING,
					new String[] { DAUtils.getThrowableClassName(e),
							SERVICE_NAME, e.getMessage() }, e);
		} finally {
			if (saveLoader != null) {
				if (daLog.isDebugTraceable()) {
					this.daLog
							.traceDebug("Get back the original context class loader");
				}

				Thread.currentThread().setContextClassLoader(saveLoader);
			}
		}
	}

	/**
	 * @deprecated
	 */
	public void setDumpTrace(boolean dumpTrace) {

	}

	public String toString() {
		return this.toString;
	}

	/**
	 * @return
	 */
	public DALog getLog() {
		return this.daLog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.session.Session#getHost()
	 */
	public String getHost() {
		return this.host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.session.Session#getP4Port()
	 */
	public int getP4Port() {
		return this.p4port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.session.Session#addRemoteReferenceHandler
	 * (IRemoteReferenceHandler)
	 */
	public void addRemoteReferenceHandler(IRemoteReferenceHandler remRefsHandler) {
		this.remoteRefsHandlers.add(remRefsHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.impl.IRemoteReferenceHandler#
	 * registerRemoteReference(Remote)
	 */
	public void registerRemoteReference(Remote remote) {
		remoteRefs.add(remote);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.impl.IRemoteReferenceHandler#
	 * releaseRemoteReferences()
	 */
	public void releaseRemoteReferences() {
		P4ObjectBroker broker = P4ObjectBroker.getBroker();
		if (broker == null) {
			this.daLog
					.logDebug(
							"ASJ.dpl_api.001174",
							"The P4ObjectBroker is null while trying to release remote references. The release operation is aborted!");
		} else {
			Iterator iter = this.remoteRefs.iterator();
			while (iter.hasNext()) {
				Remote remoteRef = (Remote) iter.next();
				// separate error handling for each resource
				// to release as much remote refs. as possible
				try {
					broker.release(remoteRef);
				} catch (Exception e) {
					this.daLog
							.logThrowable(
									"ASJ.dpl_api.001175",
									"An exception occured while trying to release remote reference for object [{0}]",
									e, new Object[] { remoteRef });
				}
			}
		}
		remoteRefs.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		try {
			// the close method is explicitly invoked
			// to release any associated resources and
			// remote references
			if (!isClosed) {
				this.close();
			}
		} finally {
			super.finalize();
		}
	}

	public int getSapcontrolPort() {

		return this.sapcontrolPort;
	}
}
