/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.deployment.proxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.naming.NamingException;

import org.xml.sax.SAXException;

import com.sap.engine.deployment.Constants;
import com.sap.engine.deployment.Logger;
import com.sap.engine.deployment.SAPTarget;
import com.sap.engine.deployment.SAPTargetModuleID;
import com.sap.engine.deployment.exceptions.ExceptionConstants;
import com.sap.engine.deployment.exceptions.SAPDeploymentManagerCreationException;
import com.sap.engine.deployment.exceptions.SAPIllegalStateException;
import com.sap.engine.deployment.exceptions.SAPRemoteException;
import com.sap.engine.deployment.proxy.dc.DCLogger;
import com.sap.engine.lib.deploy.sda.SDADescriptor;
import com.sap.engine.lib.deploy.sda.SDAProducer;
import com.sap.engine.lib.deploy.sda.SDUChecker;
import com.sap.engine.lib.deploy.sda.SoftwareType;
import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;
import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.Client;
import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.api.deploy.DeployException;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeployProcessor;
import com.sap.engine.services.dc.api.deploy.DeployResult;
import com.sap.engine.services.dc.api.deploy.DeployResultStatus;
import com.sap.engine.services.dc.api.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.api.lcm.LCMResult;
import com.sap.engine.services.dc.api.lcm.LCMResultStatus;
import com.sap.engine.services.dc.api.undeploy.UndeployItem;
import com.sap.engine.services.dc.api.undeploy.UndeployProcessor;
import com.sap.engine.services.dc.api.undeploy.UndeployResult;
import com.sap.engine.services.dc.api.undeploy.UndeployResultStatus;
import com.sap.engine.services.dc.api.undeploy.UndeploymentStrategy;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.exception.standard.SAPUnsupportedOperationException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Mariela Todorova
 */
public class DeploymentProxyImpl extends ManagementProxyImpl implements
		DeploymentProxy, Constants {
	private static final Location location = Location
			.getLocation(DeploymentProxyImpl.class);
	private Client client = null;

	public DeploymentProxyImpl(LoginInfo login)
			throws SAPDeploymentManagerCreationException {
		setLoginInfo(login);
		connect();

		try {
			client = getNewClient();
		} catch (AuthenticationException ae) {
			SAPDeploymentManagerCreationException dmce = new SAPDeploymentManagerCreationException(
					location, ExceptionConstants.CANNOT_CONNECT, ae);
			Logger.logThrowable(location, Severity.ERROR,
					"Could not get Deploy Controller client", dmce);
			throw dmce;
		} catch (ConnectionException ce) {
			SAPDeploymentManagerCreationException dmce = new SAPDeploymentManagerCreationException(
					location, ExceptionConstants.CANNOT_CONNECT, ce);
			Logger.logThrowable(location, Severity.ERROR,
					"Could not get Deploy Controller client", dmce);
			throw dmce;
		}
	}

	private Client getNewClient() throws AuthenticationException,
			ConnectionException {
		return client = ClientFactory.getInstance().createClient(
				DALog.getInstance(new DCLogger()), login.getHost(),
				Integer.parseInt(login.getPort()), login.getUser(),
				login.getPassword());
	}

	public String[] distribute(SAPTarget targetList[], File moduleArchive,
			Properties props) throws DeployLibException, SAXException,
			IOException {

		if (moduleArchive == null || !moduleArchive.isFile()) {
			Logger
					.trace(location, Severity.WARNING,
							"No archive to distribute");
			return null;
		}

		String archive = moduleArchive.getAbsolutePath();
		Logger.trace(location, Severity.DEBUG, "Distributing " + archive
				+ " with properties " + props);
		SAPTarget[] destinations = getDestinations(targetList, true);

		if (destinations == null || destinations.length == 0) {
			Logger.trace(location, Severity.DEBUG, "No destinations returned");
			return null;
		}

		String sda = null;
		String name = null;
		String vendor = null;
		SDADescriptor descr = null;

		if (props != null && !props.isEmpty()) {

			descr = processProps(props);

			if (SoftwareType.J2EE.equals(descr.getType())) {
				String aliases = props.getProperty(CONTEXTS);

				if (aliases != null && !aliases.equals("")) {
					defineAliases(props, descr);
				}
			} else if (SoftwareType.SINGLE_MODULE.equals(descr.getType())
					&& archive.toLowerCase().endsWith(WAR)) {
				defineAlias(moduleArchive.getName(), props, descr);
			}

			sda = prepareSDA(archive, descr);

		} else {

			if (SDUChecker.check(archive)) {
				Logger.trace(location, Severity.DEBUG, "File " + archive
						+ " is in SDA format");

				sda = archive;
				Attributes attributes = this.getSapManifestAttributes(sda);
				name = attributes.getValue("keyname");
				vendor = attributes.getValue("keyvendor");
				Logger.trace(location, Severity.DEBUG, "name=" + name
						+ "; vendor=" + vendor);

			} else {
				Logger.trace(location, Severity.DEBUG, "File " + archive
						+ " is not in SDA format");
				descr = new SDADescriptor();
				sda = prepareSDA(archive, descr);
			}
		}

		if (name == null || vendor == null) { // ???
			name = descr.getName();
			vendor = descr.getVendor();
			Logger.trace(location, Severity.DEBUG, "name=" + name + "; vendor="
					+ vendor);
		}

		if (name == null || name.equals("") || vendor == null
				|| vendor.equals("")) {// ???
			return null;
		}

		this.deployAndValidateResult(sda);

		if (props != null && !props.isEmpty()) {
			String reference = props.getProperty(REFERENCE);
			Logger.trace(location, Severity.DEBUG, "reference=" + reference);

			if (reference != null) {
				registerReference(vendor + "/" + name, reference);
				props.remove(REFERENCE);
			}
		}

		String[] result = determineDeployResult(vendor + "/" + name);

		if (result == null) {
			Logger.trace(location, Severity.ERROR, "Could not deploy "
					+ archive + " with properties " + props);
			SAPRemoteException sre = new SAPRemoteException(location,
					ExceptionConstants.COULD_NOT_DEPLOY,
					new String[] { archive,
							props != null ? props.toString() : "null" });
			throw sre;
		}

		return result;
	}

	public void undeploy(SAPTargetModuleID targetModuleIDs[])
			throws SAPRemoteException {
		if (targetModuleIDs == null || targetModuleIDs.length == 0) {
			Logger.trace(location, Severity.DEBUG,
					"No target modules to undeploy");
			return;
		}

		SAPTargetModuleID tMod = null;
		String mod = null;
		String[] name = null;
		UndeployProcessor undeployer = null;
		UndeployItem item = null;
		UndeployResult result = null;
		UndeployResultStatus status = null;
		ArrayList items = new ArrayList();

		try {
			for (int i = 0; i < targetModuleIDs.length; i++) {
				tMod = targetModuleIDs[i];

				if (tMod == null) {
					continue;
				}

				mod = tMod.getModuleID();

				if (items.contains(mod)) {
					continue;
				}

				items.add(mod);
				name = defineAppName(mod);

				if (name[0] != null && name[1] != null) {
					undeployer = client.getComponentManager()
							.getUndeployProcessor();
					undeployer
							.setUndeploymentStrategy(UndeploymentStrategy.IF_DEPENDING_STOP);
					undeployer.setErrorStrategy(
							ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
							ErrorStrategy.ON_ERROR_SKIP_DEPENDING);
					undeployer.setErrorStrategy(
							ErrorStrategyAction.UNDEPLOYMENT_ACTION,
							ErrorStrategy.ON_ERROR_SKIP_DEPENDING);
					item = undeployer.createUndeployItem(name[1], name[0]);
					Logger
							.trace(location, Severity.DEBUG, "Undeploying "
									+ mod);
					result = undeployer.undeploy(new UndeployItem[] { item });

					if (result == null) {
						Logger.log(location, Severity.WARNING,
								"Undeployment of " + item
										+ " returned no result");
					} else {
						status = result.getUndeployStatus();

						if (status.equals(UndeployResultStatus.ERROR)) {
							SAPRemoteException sre = new SAPRemoteException(
									location,
									ExceptionConstants.COULD_NOT_REMOVE,
									new String[] { mod });
							throw sre;
						}
					}
				}
			}
		} catch (APIException apie) {
			Logger.trace(location, Severity.ERROR,
					"Could not remove application");
			SAPRemoteException sre = new SAPRemoteException(location,
					ExceptionConstants.COULD_NOT_REMOVE, new String[] { mod },
					apie);
			throw sre;
		}
	}

	public String[] redeploy(SAPTargetModuleID targetModuleIDs[],
			File moduleArchive, Properties props)
			throws SAPUnsupportedOperationException, SAPIllegalStateException,
			IOException, DeployLibException, SAXException {

		if (moduleArchive == null || !moduleArchive.isFile()) {
			Logger.trace(location, Severity.WARNING, "No archive to redeploy");
			return null;
		}

		String archive = moduleArchive.getAbsolutePath();
		Logger.trace(location, Severity.DEBUG, "Distributing " + archive
				+ " with properties " + props);
		SAPTargetModuleID tMod = null;
		String[] appName = null;

		for (int i = 0; i < targetModuleIDs.length; i++) {
			tMod = targetModuleIDs[i];

			if (tMod == null) {
				continue;
			}

			appName = defineAppName(tMod.getModuleID());

			if (appName[0] != null && appName[1] != null) {// ???
				break;
			}
		}

		if (appName == null) {
			return null;
		}

		SDADescriptor descr = null;
		String sda = null;
		String name = appName[1];
		String vendor = appName[0];

		if (props != null && !props.isEmpty()) {

			descr = processProps(props);
			descr.setName(name);
			descr.setVendor(vendor);

			if (SoftwareType.J2EE.equals(descr.getType())) {
				String aliases = props.getProperty(CONTEXTS);

				if (aliases != null && !aliases.equals("")) {
					defineAliases(props, descr);
				}
			} else if (SoftwareType.SINGLE_MODULE.equals(descr.getType())
					&& archive.toLowerCase().endsWith(WAR)) {
				defineAlias(moduleArchive.getName(), props, descr);
			}

			sda = prepareSDA(archive, descr);

		} else {

			if (SDUChecker.check(archive)) {
				Logger.trace(location, Severity.DEBUG, "File " + archive
						+ " is in SDA format");

				Attributes attributes = this.getSapManifestAttributes(sda);
				String sapManifestName = attributes.getValue("keyname");
				String sapManifestVendor = attributes.getValue("keyvendor");

				if (name.equals(sapManifestName)
						&& vendor.equals(sapManifestVendor)) {
					sda = archive;
					Logger.trace(location, Severity.DEBUG, "name=" + name
							+ "; vendor=" + vendor);
				} else {
					descr = new SDADescriptor();
					descr.setName(name);
					descr.setVendor(vendor);
					sda = prepareSDA(archive, descr);
				}

			} else {
				Logger.trace(location, Severity.DEBUG, "File " + archive
						+ " is not in SDA format");
				sda = prepareSDA(archive, descr);
			}
		}

		this.deployAndValidateResult(sda);

		if (props != null && !props.isEmpty()) {
			String reference = props.getProperty(REFERENCE);
			Logger.trace(location, Severity.DEBUG, "reference=" + reference);

			if (reference != null) {
				registerReference(vendor + "/" + name, reference);
				props.remove(REFERENCE);
			}
		}

		String[] result = determineDeployResult(vendor + "/" + name);

		if (result == null) {
			Logger.trace(location, Severity.ERROR, "Could not deploy "
					+ archive + " with properties " + props);
			SAPRemoteException sre = new SAPRemoteException(location,
					ExceptionConstants.COULD_NOT_UPDATE,
					new String[] { archive,
							props != null ? props.toString() : "null" });
			throw sre;
		}

		return result;
	}

	public void start(SAPTargetModuleID targetModuleIDs[])
			throws SAPRemoteException {
		if (targetModuleIDs == null || targetModuleIDs.length == 0) {
			Logger
					.trace(location, Severity.DEBUG,
							"No target modules to start");
			return;
		}

		SAPTargetModuleID tMod = null;
		String mod = null;
		String[] appName = null;
		SAPTarget[] destinations = null;
		String[] dest = null;
		LCMResult result = null;
		LCMResultStatus status = null;

		for (int i = 0; i < targetModuleIDs.length; i++) {
			tMod = targetModuleIDs[i];

			if (tMod == null) {
				continue;
			}

			mod = tMod.getModuleID();
			appName = defineAppName(mod);

			if (appName[0] == null || appName[1] == null) {
				continue;
			}

			destinations = getDestinations(new SAPTarget[] { (SAPTarget) tMod
					.getTarget() }, false);

			if (destinations == null || destinations.length == 0) {
				Logger.trace(location, Severity.DEBUG,
						"No destinations returned");
				return;
			}

			dest = new String[destinations.length];

			for (int j = 0; j < destinations.length; j++) {
				dest[j] = destinations[j].getName();
				Logger.trace(location, Severity.DEBUG, "Starting " + mod
						+ " @ " + dest[j]);
			}

			try {
				result = client.getLifeCycleManager().start(appName[1],
						appName[0]);

				if (result == null) {
					Logger.log(location, Severity.WARNING, "Starting " + mod
							+ " returned no result");
				} else {
					status = result.getLCMResultStatus();

					if (status == null
							|| status.equals(LCMResultStatus.WARNING)
							|| status.equals(LCMResultStatus.NOT_SUPPORTED)) {
						Logger.log(location, Severity.WARNING, "Starting "
								+ mod + " returned result status " + status);
						Logger.log(location, Severity.WARNING,
								"Result description: "
										+ result.getDescription());
					} else if (status.equals(LCMResultStatus.ERROR)) {
						Logger.log(location, Severity.ERROR, "Starting " + mod
								+ " returned result status " + status);
						Logger.log(location, Severity.ERROR,
								"Result description: "
										+ result.getDescription());
						SAPRemoteException sre = new SAPRemoteException(
								location, ExceptionConstants.COULD_NOT_START,
								new String[] { mod });
						throw sre;
					} else if (status.equals(LCMResultStatus.SUCCESS)) {
						Logger.log(location, Severity.PATH, "Starting " + mod
								+ " returned result status " + status);
					}
				}
			} catch (APIException apie) {
				Logger.trace(location, Severity.ERROR,
						"Could not start application");
				SAPRemoteException sre = new SAPRemoteException(location,
						ExceptionConstants.COULD_NOT_START,
						new String[] { mod }, apie);
				throw sre;
			}
		}
	}

	public void stop(SAPTargetModuleID targetModuleIDs[])
			throws SAPRemoteException {
		if (targetModuleIDs == null || targetModuleIDs.length == 0) {
			Logger.trace(location, Severity.DEBUG, "No target modules to stop");
			return;
		}

		SAPTargetModuleID tMod = null;
		SAPTarget[] destinations = null;
		String mod = null;
		String[] appName = null;
		String dest[] = null;
		LCMResult result = null;
		LCMResultStatus status = null;

		for (int i = 0; i < targetModuleIDs.length; i++) {
			tMod = targetModuleIDs[i];

			if (tMod == null) {
				continue;
			}

			mod = tMod.getModuleID();
			appName = defineAppName(mod);

			if (appName[0] == null || appName[1] == null) {
				continue;
			}

			destinations = getDestinations(new SAPTarget[] { (SAPTarget) tMod
					.getTarget() }, false);

			if (destinations == null || destinations.length == 0) {
				Logger.trace(location, Severity.DEBUG,
						"No destinations returned");
				return;
			}

			dest = new String[destinations.length];

			for (int j = 0; j < destinations.length; j++) {
				dest[j] = destinations[j].getName();
				Logger.trace(location, Severity.DEBUG, "Stopping " + mod
						+ " @ " + dest[j]);
			}

			try {
				result = client.getLifeCycleManager().stop(appName[1],
						appName[0]);

				if (result == null) {
					Logger.log(location, Severity.WARNING, "Stopping " + mod
							+ " returned no result");
				} else {
					status = result.getLCMResultStatus();

					if (status == null
							|| status.equals(LCMResultStatus.WARNING)
							|| status.equals(LCMResultStatus.NOT_SUPPORTED)) {
						Logger.log(location, Severity.WARNING, "Stopping "
								+ mod + " returned result status " + status);
						Logger.log(location, Severity.WARNING,
								"Result description: "
										+ result.getDescription());
					} else if (status.equals(LCMResultStatus.ERROR)) {
						Logger.log(location, Severity.ERROR, "Stopping " + mod
								+ " returned result status " + status);
						Logger.log(location, Severity.ERROR,
								"Result description: "
										+ result.getDescription());
						SAPRemoteException sre = new SAPRemoteException(
								location, ExceptionConstants.COULD_NOT_STOP,
								new String[] { mod });
						throw sre;
					} else if (status.equals(LCMResultStatus.SUCCESS)) {
						Logger.log(location, Severity.PATH, "Stopping " + mod
								+ " returned result status " + status);
					}
				}
			} catch (APIException apie) {
				Logger.trace(location, Severity.ERROR,
						"Could not stop application");
				SAPRemoteException sre = new SAPRemoteException(location,
						ExceptionConstants.COULD_NOT_STOP,
						new String[] { mod }, apie);
				throw sre;
			}
		}
	}

	public void disconnect() throws NamingException, ConnectionException {

		closeClient();
		ctx.close();
		Logger.log(location, Severity.INFO, "Disconnected from "
				+ login.getHost() + ":" + login.getPort());

	}

	private void closeClient() throws ConnectionException {
		if (client != null) {
			client.close();
		}
	}

	private SDADescriptor processProps(Properties props)
			throws DeployLibException {
		SDADescriptor descr = new SDADescriptor();
		String alone = props.getProperty(STAND_ALONE);
		Logger.trace(location, Severity.DEBUG, "stand_alone=" + alone);

		if (alone != null) {
			props.remove(STAND_ALONE);
		}

		if (TRUE.equals(alone)) {
			descr.setType(SoftwareType.SINGLE_MODULE);
		} else {
			descr.setType(SoftwareType.J2EE);
		}

		String name = props.getProperty(ROOT_MODULE_NAME);
		Logger.trace(location, Severity.DEBUG, "root_module_name=" + name);

		if (name != null) {
			props.remove(ROOT_MODULE_NAME);
			descr.setName(name);
		}

		return descr;
	}

	private String prepareSDA(String archive, SDADescriptor descr)
			throws DeployLibException, SAXException, IOException {
		Logger.trace(location, Severity.DEBUG, "Preparing SDA file for "
				+ archive);
		SDAProducer maker = new SDAProducer(archive);
		maker.setDescriptor(descr);
		maker.produce();
		return maker.getDestinationFile();
	}

	/**
	 * Returns the SAP manifest attributes as a java.util.jar.Attributes
	 * collection
	 * 
	 * @return attributes
	 * @throws IOException
	 */
	private Attributes getSapManifestAttributes(String archive)
			throws IOException {
		JarFile jar = new JarFile(archive);
		InputStream stream = jar.getInputStream(new ZipEntry(SDUChecker
				.getSapManifestZipEntryName(archive)));
		Manifest manifest = new Manifest(stream);
		Attributes attributes = manifest.getMainAttributes();
		stream.close();
		jar.close();
		return attributes;
	}

	/**
	 * Invokes the DeployController's deploy method and validates that the
	 * deployment finishes successfully. Throws a new
	 * <code>SAPRemoteException</code> on error with a message coming from the
	 * DeployController.
	 * 
	 * @param sda
	 * @throws SAPRemoteException
	 *             on deployment failure
	 */
	private void deployAndValidateResult(String sda) throws SAPRemoteException {
		DeployResult deployResult = null;
		DeployItem[] deployItems = null;
		try {
			deployResult = deploy(sda);
			if (deployResult.getDeployResultStatus().equals(
					DeployResultStatus.ERROR)) {
				deployItems = deployResult.getDeploymentItems();
				this.throwNewSAPRemoteException(deployItems, null);
			}
		} catch (DeployException de) {
			deployItems = de.getDeploymentItems();
			this.throwNewSAPRemoteException(deployItems, de);
		} catch (APIException e) {
			throw new SAPRemoteException(location, e);
		}
	}

	private DeployResult deploy(String sda) throws APIException {
		DeployProcessor deployer = client.getComponentManager()
				.getDeployProcessor();
		deployer
				.setComponentVersionHandlingRule(ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS);
		deployer.setErrorStrategy(
				ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
				ErrorStrategy.ON_ERROR_STOP);
		deployer.setErrorStrategy(ErrorStrategyAction.DEPLOYMENT_ACTION,
				ErrorStrategy.ON_ERROR_STOP);
		deployer
				.setLifeCycleDeployStrategy(LifeCycleDeployStrategy.DISABLE_LCM);
		DeployItem item = deployer.createDeployItem(sda);
		Logger.trace(location, Severity.DEBUG, "Deploying " + sda);
		return deployer.deploy(new DeployItem[] { item });
	}

	private void throwNewSAPRemoteException(DeployItem[] deployItems,
			Exception e) throws SAPRemoteException {
		StringBuilder message = new StringBuilder("");
		for (DeployItem deployItem : deployItems) {
			message.append(deployItem.getDescription()).append("\n\n");
		}
		throw new SAPRemoteException(location,
				ExceptionConstants.DEPLOY_ITEM_DESCRIPTIONS,
				new String[] { message.toString() }, e);
	}

	private String[] determineDeployResult(String appName)
			throws RemoteException {
		String[] result = null;
		ArrayList list = new ArrayList();
		list.add("Application : " + appName);

		String[] temp = ds.listElements(null, appName, null);

		if (temp == null || temp.length == 0) {
			Logger.trace(location, Severity.ERROR, "No modules for " + appName);
			return result;
		}

		for (int i = 0; i < temp.length; i++) {
			list.add(temp[i]);
		}

		result = (String[]) list.toArray(new String[0]);

		return result;
	}

	private String[] defineAppName(String moduleID) {
		String result[] = new String[2];
		Logger.trace(location, Severity.DEBUG, "Module " + moduleID);
		int index = moduleID.indexOf('/');

		if (index > -1) {
			result[0] = moduleID.substring(0, index);

			if (index != moduleID.length() - 1) {
				result[1] = moduleID.substring(index + 1);
			}
		}

		return result;
	}

	private void defineAlias(String moduleName, Properties props,
			SDADescriptor descr) {
		String alias = props.getProperty(CONTEXT);
		Logger.trace(location, Severity.DEBUG, "context_root=" + alias);

		if (alias != null) {
			props.remove(CONTEXT);
		} else {
			alias = moduleName.substring(0, moduleName.lastIndexOf("."));
		}

		descr.addAlias(moduleName, alias);
		Logger.trace(location, Severity.DEBUG, "Alias " + alias + " set for "
				+ moduleName);
	}

	private void defineAliases(Properties props, SDADescriptor descr) {
		String aliases = props.getProperty(CONTEXTS);
		Logger.trace(location, Severity.DEBUG, "context_roots=" + aliases);
		props.remove(CONTEXTS);
		String current = null;
		String alias = null;
		String webModule = null;
		int index = -1;
		int innerIndex = -1;

		// context-roots=web_uri1:context_root1::web_uri2:context_root2
		do {
			if ((index = aliases.indexOf("::")) > -1) {
				current = aliases.substring(0, index);
				aliases = aliases.substring(index + 2);
			} else {
				current = aliases;
			}

			if ((innerIndex = current.indexOf(':')) > -1) {
				webModule = current.substring(0, innerIndex).trim();
				alias = current.substring(innerIndex + 1).trim();
				descr.addAlias(webModule, alias);
				Logger.trace(location, Severity.DEBUG, "Alias " + alias
						+ " set for " + webModule);
			}
		} while (index > -1);
	}

	private void registerReference(String applicationName, String ref)
			throws SAPRemoteException {
		if (ref.equals("")) {
			return;
		}

		String target = ref.substring(0, ref.indexOf(" "));
		String targetType = ref.substring(ref.indexOf(" ") + 1, ref
				.lastIndexOf(" "));
		String refType = ref.substring(ref.lastIndexOf(" ") + 1);
		Logger.trace(location, Severity.DEBUG, "Registering " + refType
				+ " reference from " + applicationName + " to " + targetType
				+ " " + target);

		try {
			ds.makeReferences(applicationName,
					new ReferenceObject[] { new ReferenceObject(target,
							targetType, refType) });
		} catch (RemoteException re) {
			Logger.trace(location, Severity.ERROR, "Could not register "
					+ refType + " reference from " + applicationName + " to "
					+ targetType + " " + target + " due to "
					+ re.getStackTrace());
			throw new SAPRemoteException(location,
					ExceptionConstants.COULD_NOT_REGISTER_REFERENCE, re);
		}
	}

}