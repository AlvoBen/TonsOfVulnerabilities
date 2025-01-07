package com.sap.engine.services.dc.api;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.lcm.LifeCycleManager;
import com.sap.engine.services.dc.api.lock_mng.LockManager;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The <code>Client</code> type acts as an entry point of the Deploy API. It
 * exposes operation for getting a <code>ComponentManager</code>, which is used
 * for monitoring and managing the system components.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @see com.sap.engine.services.dc.api.ClientFactory
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public interface Client {

	/**
	 * The component manager servers to get Un/DeployProcessor, ParamsProcessor,
	 * BatchFilterFactory, RepositoryExplorerFactory, SelfChecker,
	 * ArchiveManager and ModelFactory.
	 * 
	 * @return <code>ComponentManager</code> instance
	 */
	public ComponentManager getComponentManager();

	/**
	 * Life Cycle Manager is an entry point for all Life Cycle Management
	 * related operation.
	 * 
	 * @return <code>LifeCycleManager</code> instance
	 */
	public LifeCycleManager getLifeCycleManager();

	/**
	 * Lock Manager serves to lock and unlock the engine for exact action.
	 * 
	 * @return <code>LockManager</code> instance
	 */
	public LockManager getLockManager();

	/**
	 * The method has to be invoked at the end in order to release the used
	 * resourses. It is obligatory to inivoke the method( preffered in finally
	 * block ) if the client is instantiated inside the server. The trace and
	 * log files created for the client are locked until the close method is
	 * invoked.
	 * 
	 * @throws ConnectionException
	 * @see com.sap.engine.services.dc.api.util.DALog
	 */
	public void close() throws ConnectionException;

	/**
	 * All trace and log messages are handled from <code>DALog</code> instance.
	 * Each client instance has own <code>DALog</code> instance.
	 * 
	 * @return <code>DALog</code> instance associated with this client
	 */
	public com.sap.engine.services.dc.api.util.DALog getLog();

}