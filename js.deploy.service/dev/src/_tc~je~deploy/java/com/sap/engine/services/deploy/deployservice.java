/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Properties;

import com.sap.engine.lib.io.SerializableFile;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationStatistic;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescription;

/* This class belongs to the public API of the DeployService project. */

/**
 * <p>
 * Deploy Service is a core AS Java service. It controls deploy and life cycle
 * of applications and deals with the online deploy of services, libraries and
 * interfaces, in case they are not core components. It is cluster aware and
 * ensures instance homogeneity.
 * </p>
 * <p>
 * The deploy and life cycle of applications is based on
 * <code>com.sap.engine.services.deploy.container.ContainerInterfaceExtension</code>
 * , which must be implemented by the containers.
 * </p>
 * <p>
 * Container is a service or application that has supplied implementation of
 * <code>ContainerInterfaceExtension</code> and will deploy its own component
 * through this container class. Based on this contract, development components
 * which are applications are transported to interested containers.
 * </p>
 * 
 * @author Monika Kovachka
 */
public interface DeployService extends java.rmi.Remote {
	/**
	 * A property key, used in properties for specifying the application name
	 * when deploying standalone module.
	 * 
	 * @deprecated This Deploy Service property is only to be used by Deploy
	 *             Controller.
	 */
	public static final String applicationProperty = "application_name";

	/**
	 * A property key, used in properties for specifying the provider name when
	 * deploying standalone module.
	 * 
	 * @deprecated This Deploy Service property is only to be used by Deploy
	 *             Controller.
	 */
	public static final String providerProperty = "provider_name";

	/**
	 * A property key, used in deploy and update properties given to containers.
	 * 
	 * @deprecated This Deploy Service property is only to be used by Deploy
	 *             Controller.
	 */
	public static final String scaName = "scaName";

	/**
	 * A property key, used in deploy and update properties given to containers.
	 * 
	 * @deprecated This Deploy Service property is only to be used by Deploy
	 *             Controller.
	 */
	public static final String scaVendor = "scaVendor";

	/**
	 * A property key, used in properties for specifying the software type when
	 * deploying stand alone module.
	 * 
	 * @deprecated This Deploy Service property is only to be used by Deploy
	 *             Controller.
	 */
	public static final String softwareType = "software_type";

	/**
	 * A property key, used in properties for specifying the software sub type
	 * when deploying stand alone module.
	 * 
	 * @deprecated This Deploy Service property is only to be used by Deploy
	 *             Controller.
	 */
	public static final String softwareSubType = "software_sub_type";

	/**
	 * A property key, used in properties while starting an application. If the
	 * value is "true" it stands for that the application will be started in
	 * debug mode.
	 * 
	 * @deprecated This Deploy Service property is only to be used by Deploy
	 *             Controller.
	 */
	public static final String debugProperty = "debug";

	/**
	 * A property key, used in properties while starting/stopping an
	 * application. If the value is "true" it stands for that the operation will
	 * be made synchronously, otherwise it will be made asynchronously.
	 * 
	 * @deprecated This Deploy Service property is only to be used by Deploy
	 *             Controller.
	 */
	public static final String synchProperty = "synch";

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated use
	 *             com.sap.engine.services.deploy.container.op.util.Status.STARTED
	 *             .getName()
	 */
	public static final String STARTED_APP_STATUS = Status.STARTED.getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated use
	 *             com.sap.engine.services.deploy.container.op.util..Status.STOPPED
	 *             .getName()
	 */
	public static final String STOPPED_APP_STATUS = Status.STOPPED.getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated use
	 *             com.sap.engine.services.deploy.container.op.util..Status.STARTING
	 *             .getName()
	 */
	public static final String STARTING_APP_STATUS = Status.STARTING.getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated use
	 *             com.sap.engine.services.deploy.container.op.util.Status.STOPPING
	 *             .getName()
	 */
	public static final String STOPPING_APP_STATUS = Status.STOPPING.getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated use
	 *             com.sap.engine.services.deploy.container.op.util.Status.UPGRADING
	 *             .getName()
	 */
	public static final String UPGRADING_APP_STATUS = Status.UPGRADING
	    .getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated use com.sap.engine.services.deploy.container.op.util.Status.
	 *             IMPLICIT_STOPPED.getName()
	 */
	public static final String IMPLICIT_STOPPED_APP_STATUS = Status.IMPLICIT_STOPPED
	    .getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated use
	 *             com.sap.engine.services.deploy.container.op.util.Status.UNKNOWN
	 *             .getName()
	 */
	public static final String UNKNOWN_APP_STATUS = Status.UNKNOWN.getName();

	
	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated use
	 *             com.sap.engine.services.deploy.container.op.util.Status.MARKED_FOR_REMOVAL
	 *             .getName()
	 */
	public static final String MARKED_FOR_REMOVAL = Status.MARKED_FOR_REMOVAL.getName();

	
	
	/**
	 * Deploys all recognized components found in the archive file using
	 * specified remote support and applying given properties necessary for
	 * deployment.
	 * 
	 * @param earFile
	 *            the file which is transfered on the local machine and points
	 *            to the ear.
	 * @param remoteSupport
	 *            the remote support that this component will have.
	 * @param props
	 *            properties holding information necessary for the process of
	 *            deployment; they indicate J2EE Specification type, root
	 *            lookup, container type.
	 * 
	 * @return array of names of the deployed components concatenated with dash
	 *         and String representation of their type.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             Thrown in cases when, regardless of the successful
	 *             deployment, the application may not work, and additional user
	 *             intervention is needed in order to make the application
	 *             running correctly. This exception extends
	 *             <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             If some of the components could not be deployed or errors
	 *             occurred while reading archive, for example improper
	 *             <code>xml</code> format of ear descriptor or component
	 *             descriptors.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] deploy(String earFile, String[] remoteSupport,
	    Properties props) throws java.rmi.RemoteException;

	/**
	 * Deploys components found in the archive file to the specified container
	 * using given remote support and applying properties necessary for
	 * deployment.
	 * 
	 * @param archiveFile
	 *            the name of the archive that is being deployed.
	 * @param containerName
	 *            the name of the container to which this archive belongs. If it
	 *            is null, <code>Deploy Service</code> searches for a suitable
	 *            container using its mechanism for automatic recognition of the
	 *            suitable container.
	 * @param remoteSupport
	 *            the remote support that this component will have.
	 * @param props
	 *            properties holding information necessary for the process of
	 *            deployment; they indicate J2EE Specification type, root
	 *            lookup, container type, application name (a the dummy
	 *            application which is created for this standalone module).
	 * 
	 * @return array of names of the deployed components concatenated with dash
	 *         and <code>String</code> representation of their type.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             Thrown in cases when, regardless of the successful
	 *             deployment, the application may not work, so additional user
	 *             intervention is needed in order to make the application
	 *             running correctly. This exception extends
	 *             <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             If some of the components could not be deployed or errors
	 *             occurred while reading archive, for example improper
	 *             <code>xml</code> format or missing classes.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] deploy(String archiveFile, String containerName,
	    String[] remoteSupport, Properties props)
	    throws java.rmi.RemoteException;

	/**
	 * Updates components found in the archive file to the specified container
	 * using given remote support and applying properties necessary for
	 * deployment.
	 * 
	 * @param archiveFile
	 *            the name of the archive that is being deployed.
	 * @param containerName
	 *            the name of the container for which this archive is. If it is
	 *            null, deploy service searches for a suitable container using
	 *            its mechanism for automatically recognition of the suitable
	 *            container.
	 * @param remoteSupport
	 *            the remote support that this component will have.
	 * @param props
	 *            properties holding information necessary for the process of
	 *            deployment; they indicate J2EE Specification type, root
	 *            lookup, container type, application name (a dummy application
	 *            which is created for this standalone module).
	 * 
	 * @return array of names of the deployed components concatenated with dash
	 *         and <code>String</code> representation of their type.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             Thrown in cases when, regardless of the successful update,
	 *             the application may not work, so additional user intervention
	 *             is needed in order to make the application running correctly.
	 *             This exception extends <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             If some of the components could not be deployed or errors
	 *             occurred while reading archive, for example improper
	 *             <code>xml</code> format or missing classes.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] update(String archiveFile, String containerName,
	    String[] remoteSupport, Properties props)
	    throws java.rmi.RemoteException;

	/**
	 * Updates previously deployed components found in the ear file applying
	 * given properties necessary for update.
	 * 
	 * @param archiveName
	 *            the name of the ear that contains the update information.
	 * @param props
	 *            properties holding information necessary for the process of
	 *            update; they indicate J2EE Specification type, root lookup,
	 *            container type, etc.
	 * 
	 * @return array of names of the updated components concatenated with dash
	 *         and <code>String</code> representation of their type.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             Thrown in cases when, regardless of the successful update,
	 *             the application may not work, so additional user intervention
	 *             is needed in order to make the application running correctly.
	 *             This exception extends <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             If some of the components could not be updated or errors
	 *             occurred while reading archive, for example improper
	 *             <code>xml</code> format of ear descriptor or component
	 *             descriptors.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] update(String archiveName, Properties props)
	    throws java.rmi.RemoteException;

	/**
	 * Updates previously deployed application with the given single files that
	 * are part of it.
	 * 
	 * @param files
	 *            contains information about the changed files for the
	 *            application.
	 * @param appName
	 *            the name of the application which is being updated.
	 * @param props
	 *            properties holding information necessary for the process of
	 *            update. It is container dependent.
	 * 
	 * @throws RemoteException
	 *             If some of the components could not be updated or errors
	 *             occurred while reading archive, for example improper
	 *             <code>xml</code> format of ear descriptor or component
	 *             descriptors.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void singleFileUpdate(FileUpdateInfo[] files, String appName,
	    Properties props) throws java.rmi.RemoteException;

	/**
	 * Removes the specified application from servers in cluster.
	 * 
	 * @param applicationName
	 *            the name of the application that will be removed.
	 * 
	 * @throws RemoteException
	 *             if some of the components of the application could not be
	 *             removed.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void remove(String applicationName) throws java.rmi.RemoteException;

	/**
	 * Stops an application so that it will not be accessible for any actions
	 * from client side. Only existing application can be stopped. This method
	 * does not remove the application from server though application is not
	 * accessible unless it is started again. This method has asynchronous
	 * implementation, i.e. its execution does not guarantee the completion of
	 * stopping process. If this method is called during initial server start,
	 * the application will be stopped locally only on the current server.
	 * 
	 * @param applicationName
	 *            name of application to be stopped.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             if the application was started successfully, but with some
	 *             warnings. This exception extends
	 *             <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             if the current user has no authorization to perform the
	 *             operation; a remote problem during the start process occurs
	 *             or the specified application is currently not deployed.
	 * 
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void stopApplication(String applicationName) throws RemoteException;

	/**
	 * Stops an application so that it will not be accessible for any actions
	 * from client side. Only existing application can be stopped. This method
	 * does not remove the application from server though application is not
	 * accessible unless it is started again. This method has synchronous
	 * implementation, i.e. its execution guarantees that application is
	 * completely stopped (or its status is not changed if exception is thrown).
	 * 
	 * @param applicationName
	 *            name of application to be stopped.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             if the application was started successfully, but with some
	 *             warnings. This exception extends
	 *             <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             thrown if a remote problem during stop process occurs.
	 * 
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void stopApplicationAndWait(String applicationName)
	    throws RemoteException;

	/**
	 * Stops an application so that it will not be accessible for any actions
	 * from client side. Only deployed application can be stopped. This method
	 * does not remove the application from server though application is not
	 * accessible unless it is started again. This method has synchronous
	 * implementation, i.e. its execution guarantees that application is
	 * completely stopped (or its status is not changed if exception is thrown).
	 * 
	 * @param appName
	 *            name of application to be stopped.
	 * @param serverNames
	 *            the names of the server nodes, where the application will be
	 *            stopped.
	 *            <p>
	 *            NOTE: This method will try to stop the application on all
	 *            server nodes in the current instance.
	 *            </p>
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             if the application was started successfully, but with some
	 *             warnings. This exception extends
	 *             <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             thrown if a remote problem during stop process occurs.
	 * 
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void stopApplicationAndWait(String appName, String[] serverNames)
	    throws RemoteException;

	/**
	 * Stops an application on the specified servers so that it will not be
	 * accessible for any actions from client side. Only existing application
	 * can be stopped. This method does not remove the application from server
	 * though application is not accessible unless it is started again. This
	 * method has asynchronous implementation, i.e. its execution does not
	 * guarantee the completion of stopping process. If this method is called
	 * during initial server start, the application will be stopped locally only
	 * on the current server.
	 * 
	 * @param applicationName
	 *            name of application to be stopped.
	 * @param serverNames
	 *            names of the servers on which the application will be stopped.
	 *            NOTE: This method will try to stop the application on all
	 *            server nodes in the current instance.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             if the application was started successfully, but with some
	 *             warnings. This exception extends
	 *             <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             if the current user has no authorization to perform the
	 *             operation; a remote problem during the stop process occurs or
	 *             the specified application is currently not deployed.
	 * 
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void stopApplication(String applicationName, String[] serverNames)
	    throws RemoteException;

	/**
	 * Starts an application thus making it accessible for the clients. Only
	 * existing applications that are in status "STOPPED" or "IMPLICIT_STOPPED"
	 * can be started. This method has asynchronous implementation, i.e. its
	 * execution does not guarantee the completion of starting process. If this
	 * method is called during initial server start, the application will be
	 * started locally only on the current server.
	 * 
	 * @param applicationName
	 *            name of application to be started.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             if the application was started successfully, but with some
	 *             warnings. This exception extends
	 *             <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             if the current user has no authorization; a remote problem
	 *             during start process occurs or the specified application is
	 *             currently not deployed.
	 * 
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void startApplication(String applicationName) throws RemoteException;

	/**
	 * Starts an application thus making it accessible for the clients. Only
	 * existing applications that are in status "STOPPED" or "IMPLICIT_STOPPED"
	 * can be started. This method has synchronous implementation, i.e. its
	 * execution guarantees that application is completely started (or stopped
	 * if exception is thrown). If this method is called during initial server
	 * start, the application will be started locally only on the current
	 * server.
	 * 
	 * @param applicationName
	 *            name of application to be started.
	 * 
	 * @throws RemoteException
	 *             if the current user has no authorization; a remote problem
	 *             during start process occurs or the specified application is
	 *             currently not deployed.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             if the application was started successfully, but with some
	 *             warnings. This exception extends
	 *             <code>RemoteException.</code>
	 * 
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void startApplicationAndWait(String applicationName)
	    throws RemoteException;

	/**
	 * Starts an application thus making it accessible for the clients. Only
	 * deployed applications that are in status "STOPPED" or "IMPLICIT_STOPPED"
	 * can be started. This method has synchronous implementation, i.e. its
	 * execution guarantees that application is completely started (or stopped
	 * if exception is thrown).
	 * 
	 * @param appName
	 *            name of application to be started.
	 * @param serverNames
	 *            the names of the server nodes, where the application will be
	 *            started.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             if the application was started successfully, but with some
	 *             warnings. This exception extends
	 *             <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             thrown if a remote problem during start process occurs or the
	 *             specified application is currently not deployed.
	 * 
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void startApplicationAndWait(String appName, String[] serverNames)
	    throws RemoteException;

	/**
	 * Starts an application on the specified servers thus making it accessible
	 * for the clients. Only existing applications that are in status "STOPPED"
	 * or "IMPLICIT_STOPPED" can be started. This method has asynchronous
	 * implementation, i.e. its execution does not guarantee the completion of
	 * starting process. If this method is called during initial server start,
	 * the application will be started locally only on the current server.
	 * 
	 * @param applicationName
	 *            name of application to be started.
	 * 
	 * @param serverNames
	 *            names of the servers on which the application will be started.
	 *            If this parameter is null, the application will be started in
	 *            the whole cluster.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             if the application was started successfully, but with some
	 *             warnings. This exception extends
	 *             <code>RemoteException.</code>
	 * 
	 * @throws RemoteException
	 *             if the current user has no authorization; a remote problem
	 *             during start process occurs or the specified application is
	 *             currently not deployed.
	 * 
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void startApplication(String applicationName, String[] serverNames)
	    throws RemoteException;

	/**
	 * This method returns the status of the application, which is deployed on
	 * the server, that processes the request. Application status can be:
	 * 
	 * <code>STOPPED, IMPLICIT_STOPPED, STARTED, STARTING, STOPPING, UPGRADING, UNKNOWN.</code>
	 * 
	 * @param applicationName
	 *            the name of the application, which status will be returned.
	 * 
	 * @return String representation of application status.
	 * 
	 * @throws RemoteException
	 *             if a problem during getting application status occurs.
	 * @deprecated This functionality will be removed from Deploy Service and
	 *             will be part of LIFE-CYCLE Management
	 */
	public String getApplicationStatus(String applicationName)
	    throws RemoteException;

	/**
	 * This method returns the status of the application, which is deployed on
	 * the server with specified serverName. Application status can be:
	 * 
	 * <code>STOPPED, IMPLICIT_STOPPED, STARTED, STARTING, STOPPING, UPGRADING, UNKNOWN.</code>
	 * 
	 * @param applicationName
	 *            the name of the application, which status will be returned.
	 * 
	 * @param serverName
	 *            the name of the server, on which the application is deployed.
	 * 
	 * @return String representation of application status.
	 * 
	 * @throws RemoteException
	 *             if a problem during getting application status occurs.
	 * @deprecatedDeploy Service functionality is replaced by Deploy Controller.
	 *                   See <a
	 *                   href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *                   >Deploy Controller Manual</a> and <a href=
	 *                   "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *                   >Deploy Controller Twiki page</a> for detailed
	 *                   information.
	 */
	public String getApplicationStatus(String applicationName, String serverName)
	    throws RemoteException;

	/**
	 * This method returns the status description of the application, which is
	 * deployed on the server, that processes the request. Application status
	 * can be:
	 * <code>STOPPED, IMPLICIT_STOPPED, STARTED, STARTING, STOPPING, UPGRADING, UNKNOWN.</code>
	 * The description is applied runtime and is of particular interest in
	 * complex scenarios like starting and stopping because of references
	 * handling etc.
	 * 
	 * @param applicationName
	 *            the name of the application, which status will be returned.
	 * 
	 * @return the human readable application status description
	 * 
	 * @throws RemoteException
	 *             if a problem during getting application status description
	 *             occurs.
	 * @deprecated TDeploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public StatusDescription getApplicationStatusDescription(
	    String applicationName) throws RemoteException;

	/**
	 * This method returns the status description of the application, which is
	 * deployed on the server with specified serverName. Application status can
	 * be:
	 * <code>STOPPED, IMPLICIT_STOPPED, STARTED, STARTING, STOPPING, UPGRADING, UNKNOWN.</code>
	 * The description is applied runtime and is of particular interest in
	 * complex scenarios like starting and stopping because of references
	 * handling etc.
	 * 
	 * @param applicationName
	 *            the name of the application, which status will be returned.
	 * 
	 * @param serverName
	 *            the name of the server, on which the application is deployed.
	 * 
	 * @return the human readable application status description
	 * 
	 * @throws RemoteException
	 *             if a problem during getting application status description
	 *             occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public StatusDescription getApplicationStatusDescription(
	    String applicationName, String serverName) throws RemoteException;

	/**
	 * This method returns the status description of the application, which is
	 * deployed on the server, that processes the request. Application status
	 * can be:
	 * <code>STOPPED, IMPLICIT_STOPPED, STARTED, STARTING, STOPPING, UPGRADING, UNKNOWN.</code>
	 * The description is applied runtime and is of particular interest in
	 * complex scenarios like starting and stopping because of references
	 * handling etc.
	 * 
	 * @param providerName
	 *            the name of the provider, which provides the application.
	 * 
	 * @param applicationName
	 *            the name of the application, which status will be returned.
	 * 
	 * @param serverName
	 *            the name of the server, on which the application is deployed.
	 * 
	 * @return the human readable application status description
	 * 
	 * @throws RemoteException
	 *             if a problem during getting application status description
	 *             occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public StatusDescription getApplicationStatusDescription(
	    String providerName, String applicationName, String serverName)
	    throws RemoteException;

	/**
	 * Lists the names of all currently working containers in cluster on which
	 * components can be deployed.
	 * 
	 * @param serverNames
	 *            list of servers which containers will be listed. If this
	 *            parameter is null containers on all servers in cluster will be
	 *            listed.
	 * 
	 * @return a <code>String</code> array of all containers that work on any of
	 *         the specified servers.
	 * 
	 * @throws RemoteException
	 *             if some abnormal condition occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] listContainers(String[] serverNames)
	    throws java.rmi.RemoteException;

	/**
	 * Gets the names of all deployed applications to the specified container on
	 * the specified servers. If container name is null - this notifies that all
	 * applications on all registered containers will be listed. If serverNames
	 * is null applications from all servers in the cluster will be listed.
	 * 
	 * @param containerName
	 *            the name of the container which applications are listed.
	 * @param serverNames
	 *            list of servers on which containers applications will be
	 *            listed.
	 * 
	 * @return a <code>String</code> array of all deployed applications.
	 * 
	 * @throws RemoteException
	 *             if some abnormal condition occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] listApplications(String containerName, String[] serverNames)
	    throws java.rmi.RemoteException;

	/**
	 * Gets the names of all deployed J2EE applications to the specified
	 * container on the specified servers. If container name is null - this
	 * notifies that all J2EE applications on all registered containers will be
	 * listed. If serverNames is null J2EE applications from all servers in the
	 * cluster will be listed.
	 * 
	 * @param containerName
	 *            the name of the container which J2EE applications are listed.
	 * @param serverNames
	 *            list of servers on which containers J2EE applications will be
	 *            listed.
	 * 
	 * @return a <code>String</code> array of all deployed J2EE applications.
	 * 
	 * @throws RemoteException
	 *             if some abnormal condition occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] listJ2EEApplications(String containerName,
	    String[] serverNames) throws java.rmi.RemoteException;

	/**
	 * Gets the names of all deployed components for this application on the
	 * specified container on the specified servers. If container name is null -
	 * this notifies that all components for the specified application on all
	 * registered containers will be listed. If serverNames is null this will
	 * list applications from all servers in cluster.
	 * 
	 * @param containerName
	 *            the name of the container which components are listed.
	 * @param applicationName
	 *            the name of the application to which the listed components
	 *            belong.
	 * @param serverNames
	 *            list of servers on which containers components will be listed.
	 * 
	 * @return a <code>String</code> array of all components that belong to the
	 *         application and are deployed on the specified container that
	 *         works on the particular servers.
	 * 
	 * @throws RemoteException
	 *             if some abnormal condition occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] listElements(String containerName, String applicationName,
	    String[] serverNames) throws java.rmi.RemoteException;

	/**
	 * Gets a jar file containing all classes that client application would need
	 * to run the specified application.
	 * 
	 * @param applicationName
	 *            the name of the application.
	 * 
	 * @return SerializableFile containing the bytes of client jar with the
	 *         component classes.
	 * 
	 * @throws RemoteException
	 *             if some abnormal condition occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public SerializableFile getClientJar(String applicationName)
	    throws java.rmi.RemoteException;

	/**
	 * Lists the names of the services that can generate remote support.
	 * 
	 * @return list of services that can be used for remote support.
	 * 
	 * @throws RemoteException
	 *             if some abnormal condition occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] getSupports() throws java.rmi.RemoteException;

	/**
	 * Deploys a library. The jar contains all the needed information about
	 * library.
	 * 
	 * @param jar
	 *            deployment jar of a library.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during deploy process occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void deployLibrary(String jar) throws RemoteException;

	/**
	 * Removes an existing library with the specified name from the server.
	 * 
	 * @param libName
	 *            name of this library.
	 * 
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             thrown if the specified library has not been deployed.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during remove process occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void removeLibrary(String libName) throws RemoteException;

	/**
	 * Makes references from this application to the specified components. Each
	 * reference means that an application needs all the referenced components
	 * for its regular work.
	 * 
	 * @param fromApplication
	 *            name of application.
	 * @param toComponents
	 *            list of component names to be referenced by this application.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during making references process occurs.
	 * @deprecated Should not be used any more. Current class loading mechanism
	 *             does not support adding references after a ClassLoader
	 *             instance is initially created.
	 */
	public void makeReferences(String fromApplication, String[] toComponents)
	    throws RemoteException;

	/**
	 * Makes references from this application to the specified components. Each
	 * reference means that an application needs all the referenced components
	 * for its regular work.
	 * 
	 * @param fromApplication
	 *            name of application.
	 * @param toComponents
	 *            Properties with key - the component name, and value - type of
	 *            the reference : hard or weak. These are the valid component
	 *            names: for application - the application name; for service -
	 *            service:service_name; for interface -
	 *            interface:interface_name; for library - library:library_name.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during making references process occurs.
	 * @deprecated Should not be used any more. Current class loading mechanism
	 *             does not support adding references after a ClassLoader
	 *             instance is initially created.
	 */
	public void makeReferences(String fromApplication, Properties toComponents)
	    throws RemoteException;

	/**
	 * Removes references from this application to the specified components.
	 * 
	 * @param fromApplication
	 *            name of application.
	 * @param toComponents
	 *            list of component names references to which will be removed.
	 * @throws com.sap.engine.services.deploy.container.WarningException
	 *             thrown if the references have not been made.
	 * @throws RemoteException
	 *             thrown if a problem during removing references process
	 *             occurs.
	 * @deprecated Should not be used any more. Current class loading mechanism
	 *             does not support adding references after a ClassLoader
	 *             instance is initially created.
	 */
	public void removeReferences(String fromApplication, String[] toComponents)
	    throws RemoteException;

	/**
	 * Returns Hashtable containing all server libraries. The key in table is
	 * library name, and the value is an array of library jars.
	 * 
	 * @return Hashtable with the server libraries.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during getting libraries from server
	 *             occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public Hashtable getServerLibraries() throws RemoteException;

	/**
	 * Returns all references of deployed libraries.
	 * 
	 * @return the list of all libraries references.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during getting libraries references from
	 *             server occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] getServerReferences() throws RemoteException;

	/**
	 * Registers a callback to listen for deployment events to the specified
	 * servers.
	 * 
	 * @param callback
	 *            callback object that will receive the events from servers.
	 * @param serverNames
	 *            list of all servers that will have the callback object to send
	 *            events.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during registration occurs.
	 */
	public void registerDeployCallback(DeployCallback callback,
	    String[] serverNames) throws RemoteException;

	/**
	 * Unregisters previously registered callback object from the specified
	 * servers.
	 * 
	 * @param callback
	 *            the callback object.
	 * @param serverNames
	 *            list of all servers that will unregister the callback object.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during unregistration occurs.
	 */
	public void unregisterDeployCallback(DeployCallback callback,
	    String[] serverNames) throws RemoteException;

	/**
	 * Registers references from an application to applications, libraries,
	 * services or interfaces, described in a ReferenceObject[] array. Each
	 * reference means that an application needs all listed reference objects
	 * for its regular work.
	 * 
	 * @param fromApplication
	 *            name of application.
	 * @param references
	 *            list of reference objects to be referenced by this
	 *            application.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during making references process occurs.
	 * @deprecated Should not be used any more. Current class loading mechanism
	 *             does not support adding references after a ClassLoader
	 *             instance is initially created.
	 */
	public void makeReferences(String fromApplication,
	    ReferenceObject[] references) throws RemoteException;

	/**
	 * Removes references from this application to the specified reference
	 * objects.
	 * 
	 * @param fromApplication
	 *            name of application.
	 * @param references
	 *            list of reference objects references to which will be removed.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during removing references process
	 *             occurs.
	 * @deprecated Should not be used any more. Current class loading mechanism
	 *             does not support adding references after a ClassLoader
	 *             instance is initially created.
	 */
	public void removeReferences(String fromApplication,
	    ReferenceObject[] references) throws RemoteException;

	/**
	 * Registers references from an application to applications, libraries,
	 * services or interfaces, described in a ReferenceObject[] array. Each
	 * reference means that an application needs all listed reference objects
	 * for its regular work.
	 * 
	 * @param providerName
	 *            the name of the provider, which provides the application.
	 * @param fromApplication
	 *            name of application.
	 * @param references
	 *            list of reference objects to be referenced by this
	 *            application.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during making references process occurs.
	 * @deprecated Should not be used any more. Current class loading mechanism
	 *             does not support adding references after a ClassLoader
	 *             instance is initially created.
	 */
	public void makeReferences(String providerName, String fromApplication,
	    ReferenceObject[] references) throws RemoteException;

	/**
	 * Removes references from this application to the specified reference
	 * objects.
	 * 
	 * @param providerName
	 *            the name of the provider, which provides the application.
	 * @param fromApplication
	 *            name of application.
	 * @param references
	 *            list of reference objects references to which will be removed.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during removing references process
	 *             occurs.
	 * @deprecated Should not be used any more. Current class loading mechanism
	 *             does not support adding references after a ClassLoader
	 *             instance is initially created.
	 */
	public void removeReferences(String providerName, String fromApplication,
	    ReferenceObject[] references) throws RemoteException;

	/**
	 * This method returns the status of the application, which is deployed on
	 * the server, that processes the request. Application status can be:
	 * 
	 * <code>STOPPED, IMPLICIT_STOPPED, STARTED, STARTING, STOPPING, UPGRADING, UNKNOWN.</code>
	 * 
	 * @param providerName
	 *            the name of the provider, which provides the application.
	 * @param applicationName
	 *            the name of the application, which status will be returned.
	 * @param serverName
	 *            the name of the server, on which the application is deployed.
	 * 
	 * @return String representation of application status.
	 * 
	 * @throws RemoteException
	 *             if a problem during getting application status occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String getApplicationStatus(String providerName,
	    String applicationName, String serverName) throws RemoteException;

	/**
	 * Gets a jar file containing all classes that client application would need
	 * to run the specified application.
	 * 
	 * @param providerName
	 *            the name of the provider, which provides the application.
	 * @param applicationName
	 *            the name of the application.
	 * 
	 * @return SerializableFile containing the bytes of client jar with the
	 *         component classes.
	 * 
	 * @throws RemoteException
	 *             if some abnormal condition occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public SerializableFile getClientJar(String providerName,
	    String applicationName) throws java.rmi.RemoteException;

	/**
	 * Gets the names of all deployed components for this application on the
	 * specified container on the specified servers. If container name is null -
	 * this notifies that all components for the specified application on all
	 * registered containers will be listed. If serverNames is null this will
	 * list applications from all servers in cluster.
	 * 
	 * @param containerName
	 *            the name of the container which components are listed.
	 * @param providerName
	 *            the name of the provider, which provides the application.
	 * @param applicationName
	 *            the name of the application to which the listed components
	 *            belong.
	 * @param serverNames
	 *            list of servers on which containers components will be listed.
	 * 
	 * @return list of all components that belong to the application and are
	 *         deployed on the specified container that works on the particular
	 *         servers.
	 * 
	 * @throws RemoteException
	 *             if some abnormal condition occurs.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public String[] listElements(String containerName, String providerName,
	    String applicationName, String[] serverNames)
	    throws java.rmi.RemoteException;

	/**
	 * Removes the specified application from servers in cluster.
	 * 
	 * @param providerName
	 *            the name of the provider, which provides the application.
	 * @param applicationName
	 *            the name of the application that will be removed.
	 * 
	 * @throws RemoteException
	 *             if some of the components of the application could not be
	 *             removed.
	 * @deprecated DDeploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void remove(String providerName, String applicationName)
	    throws java.rmi.RemoteException;

	/**
	 * Removes an existing library with the specified name from the server.
	 * 
	 * @param providerName
	 *            name of the provider which provides the specified library.
	 * @param libName
	 *            name of this library.
	 * 
	 * @throws RemoteException
	 *             thrown if a problem during remove process occurs.
	 * @deprecated DDeploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void removeLibrary(String providerName, String libName)
	    throws RemoteException;

	/**
	 * Updates previously deployed application with the given single files that
	 * are part of it.
	 * 
	 * @param files
	 *            contains information about the changed files for the
	 *            application.
	 * @param providerName
	 *            the name of the provider, which provides the application.
	 * @param appName
	 *            the name of the application which is being updated.
	 * @param props
	 *            properties holding information necessary for the process of
	 *            update. It is container dependent.
	 * 
	 * @throws RemoteException
	 *             If some of the components could not be updated or errors
	 *             occurred while reading archive, for example improper
	 *             <code>xml</code> format of ear descriptor or component
	 *             descriptors.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public void singleFileUpdate(FileUpdateInfo[] files, String providerName,
	    String appName, Properties props) throws java.rmi.RemoteException;

	/**
	 * Gets information about the application with the given application name.
	 * 
	 * @param applicationName
	 *            name of application for which info will be returned.
	 * 
	 * @return information about the application with the given application
	 *         name.
	 * 
	 * @throws RemoteException
	 *             thrown if a remote problem during getting process occurs or
	 *             the specified application is currently not deployed.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public ApplicationInformation getApplicationInformation(
	    String applicationName) throws RemoteException;

	/**
	 * Obtains information about specified container from specified server. If
	 * serverName is null then the ContainerInfo object is given from Deploy
	 * Service, which processes the request.
	 * 
	 * @param containerName
	 *            the name of the container.
	 * @param serverName
	 *            It will be ignored and the method will return the info from
	 *            the current node.
	 * 
	 * @return ContainerInfo object, which contains information about the
	 *         specified container.
	 * 
	 * @throws RemoteException
	 *             if containerName is null or the specified container is not
	 *             started on the specified server.
	 * @deprecated Deploy Service functionality is replaced by Deploy
	 *             Controller. See <a
	 *             href="http://jst.wdf.sap.corp:1080/display/JSTTSG/%28Deploy%29Api-All"
	 *             >Deploy Controller Manual</a> and <a href=
	 *             "http://bis.wdf.sap.corp:1080/twiki/bin/view/Techdev/DeployController"
	 *             >Deploy Controller Twiki page</a> for detailed information.
	 */
	public ContainerInfo getContainerInfo(String containerName,
	    String serverName) throws RemoteException;

	/**
	 * Provides summary about the result of the performed migration with the
	 * appropriate state, which is relevant only for the current server start
	 * up. If migration has not, is not and will not be performed till the next
	 * server start up, its status will be <code>CMigrationState</code>
	 * .WONT_BE_STARTED.
	 * 
	 * @return
	 *         <code>CMigrationStatistic<code> object, which holds the statistic of the 
	 * performed application migration from all migrators.
	 * 
	 * @throws RemoteException
	 *             in case the operation cannot be performed.
	 */
	public CMigrationStatistic getCMigrationStatistic() throws RemoteException;

}
