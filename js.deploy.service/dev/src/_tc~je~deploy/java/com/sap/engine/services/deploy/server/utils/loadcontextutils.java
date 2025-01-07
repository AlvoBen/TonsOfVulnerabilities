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
package com.sap.engine.services.deploy.server.utils;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.frame.core.load.UnknownClassLoaderException;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.logging.DSLogConstants;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.ReferenceResolver;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.cache.resources.ComponentsRepository;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.engine.services.deploy.timestat.ITimeStatConstants;
import com.sap.tc.logging.Location;

/**
 * This class is used to define shared application class loaders. Used only
 * internally by <tt>js.deploy.service</tt> project. It is thread safe.
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public final class LoadContextUtils {
	
	private static final Location location = 
		Location.getLocation(LoadContextUtils.class);
	
	private final ReferenceResolver resolver;
	private final boolean migrationMode;
	
	/**
	 * Creates application class loader factory. Here we use the fact that the
	 * runtime mode of the server cannot be changed without restart.
	 * @param resolver the used reference resolver.
	 * @param migrationMode <tt>true</tt> if the server is in migration mode.
	 */
	public LoadContextUtils(final ReferenceResolver resolver,
		final boolean migrationMode) {
		this.resolver = resolver;
		this.migrationMode = migrationMode;
	}


	/**
	 * Used to check that loader name is not null, and the corresponding class
	 * loader is unregistered.
	 * 
	 * @param appName application name provided by DeployInfo.
	 * @throws IllegalStateException if there is already existing classloader.
	 * @throws IllegalArgumentException if the provided loaderName is null.
	 */
	private static void ensureLoaderIsUnregistered(
		final String appName) throws IllegalStateException, 
		IllegalArgumentException {
		final ClassLoader appLoader = PropManager.getInstance()
			.getLoadContext().getClassLoader(appName);
		if (appLoader != null) {
			throw new IllegalStateException(
				"ASJ.dpl_ds.006102 The " + appName +
				" class loader is not null, but " + appLoader +
				" for the LoadContext, which is wrong /n" +
				"Hint: The reason could be leaking class loaders. " +
				"You can also check, whether above in the stack trace, " +
				"there is a call to unregister the loader. ");
		}
	}

	/**
	 * Creates shared application class loader.
	 * 
	 * @param dInfo deployment info.
	 * @return shared application class loader.
	 * @throws DeploymentException
	 * @throws IllegalStateException in case the loader is registered in the 
	 * LoadContext.
	 * @throws IllegalArgumentException in case of wrong loader name.
	 */
	public ClassLoader defineSharedApplicationLoader(
		final DeploymentInfo dInfo) throws DeploymentException, 
		IllegalStateException, IllegalArgumentException {
		try {
			ensureLoaderIsUnregistered(dInfo.getApplicationName());

			final LinkedHashSet<String> parentCLNames = 
				getParentCLNames(dInfo);

			final List<ClassLoader> parentClassLoaders = 
				createParentClassLoaders(dInfo, parentCLNames);

			final ClassLoader classLoader = createApplicationClassLoader(
				parentClassLoaders, dInfo.getApplicationLoaderFiles(),
				dInfo.getApplicationName(), dInfo.getApplicationName());
			if (location.beDebug()) {
				DSLog.traceDebug(
						location,
						"{0}", 
						PropManager.getInstance().getLoadContext()
						.getLoaderDetailedInfo(classLoader));
			}
			return classLoader;
		} catch (Exception ex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_CREATE_APPLOADER,
				new String[] { dInfo.getApplicationName() }, ex);
			sde.setMessageID("ASJ.dpl_ds.005004");
			throw sde;
		}
	}

	/**
	 * @return ordered set of parent classloader names.
	 * @param dInfo deployment info.
	 */
	private LinkedHashSet<String> getParentCLNames(final DeploymentInfo dInfo)
		throws DeploymentException {
		final ComponentsRepository components = 
			resolver.getComponentsRepository();
		final LinkedHashSet<String> result = new LinkedHashSet<String>();
		result.add(LoadContext.LDR_NAME_SYSTEM_FRAME);
		addStandardAppRefs(result, components);
		addRegularAppRefs(result, dInfo, components);
		addUsedComponentLoaders(result, dInfo);
		return result;
	}

	/**
	 * Collect classloader names for available resources, which are used by the
	 * given consumer.
	 * 
	 * @param loaders set of already collected classloader names. Cannot be 
	 * null.
	 * @param consumer the deployment info for the resource consumer. Only
	 * applications can provide or consume resources, excluding local alone 
	 * components, which are provided by the containers. Cannot be null.
	 */
	private void addUsedComponentLoaders(final Set<String> loaders,
		final DeploymentInfo consumer) {

		final Set<ResourceReference> references = 
			consumer.getResourceReferences();
		for (ResourceReference resRef : references) {
			final String resourceName = resRef.getResRefName();
			final String resourceType = resRef.getResRefType();
			// Check whether the resource is provided and ignore all unprovided
			// resources, resources provided by services or libraries
			final String provider = Applications
				.getResourceProviderIfApplication(
					new Resource(
						resourceName, resourceType));
			// We will ignore and null resources and resources provided by the
			// same application.
			if (provider != null &&
				!provider.equals(consumer.getApplicationName())) {
				// There is another application providing the resource.
				final DeploymentInfo providerInfo = Applications.get(provider);
				if (providerInfo.getStatus().equals(Status.STARTED)) {
					loaders.add(providerInfo.getApplicationName());
				}
			}
		}
	}

	private void addStandardAppRefs(final LinkedHashSet<String> parentCLNames,
		final ComponentsRepository components) throws DeploymentException {

		final List<Component> stdAppRefs = PropManager.getInstance()
			.getStandardAppRefs();
		for (Component comp : stdAppRefs) {
			if (isAllowedForParentCL(components, comp)) {
				parentCLNames.add(comp.toString());
			} else {
				if (comp.getType().equals(Component.Type.SERVICE)) {
					try {
						String contName = "unknown";
						ContainerWrapper container = Containers.getInstance()
								.getContainer(comp.getName());
						if (container != null) {
							contName = container.getContainerInfo().getName();
						}
						ServiceUtils.startComponentAndWait(comp, contName);
						parentCLNames.add(comp.toString());
					} catch (ServerDeploymentException sde) {
						throw sde;
					} catch (ServiceException se) {
						throw new DeploymentException(se.getMessage(), se);
					}
				} else {
					// TODO - if interface -> start the service providing it ->
					// @ReferenceResolver
				}
			}
		}
	}


	private boolean isAllowedForParentCL(final ComponentsRepository components,
		final Component comp) {
		return comp.getType() == Component.Type.APPLICATION ?
			migrationMode || components.isComponentEnabled(comp) :
			components.isComponentEnabled(comp);
	}

	private void addRegularAppRefs(final LinkedHashSet<String> parentCLNames,
		final DeploymentInfo dInfo, final ComponentsRepository components) {

		final ReferenceObject[] appRefs = dInfo.getReferences();
		if (appRefs == null) {
			return;
		}
		for (int i = 0; i < appRefs.length; i++) {
			final Component component = Component.create(appRefs[i].toString());
			if (isAllowedForParentCL(components, component)) {
				parentCLNames.add(appRefs[i].toString());
			}
		}
	}

	private static ClassLoader createHeavyClassLoader(
		final DeploymentInfo dInfo) throws MalformedURLException, 
		UnknownClassLoaderException, ServerDeploymentException {

		final String[] heavyClassLoaderJars = 
			dInfo.getHeavyApplicationLoaderFiles();
		if (heavyClassLoaderJars.length <= 0) {
			return null;
		}

		final LoadContext loadContext = PropManager.getInstance()
				.getLoadContext();
		final String[] standardJEELibrariesNames = PropManager.getInstance()
				.getStandardJEELibraryNames();
		if (standardJEELibrariesNames.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.STANDARD_JEE_LIBRARY_NAME_NOT_SPECIFIED);
			sde.setMessageID("ASJ.dpl_ds.006312");
			throw sde;

		}
		final ArrayList<ClassLoader> standardLibraryLoaders = new ArrayList<ClassLoader>(
				standardJEELibrariesNames.length);
		for (String libraryName : standardJEELibrariesNames) {
			ClassLoader loader = loadContext.getClassLoader(libraryName);
			if (loader != null) {
				standardLibraryLoaders.add(loader);
			} else {
				DSLog.traceWarning(location, DSLogConstants.PARENT_LOADER_DOESNT_EXISTS,
						libraryName);
			}
		}
		if (standardLibraryLoaders.isEmpty()) {
			final StringBuilder sb = new StringBuilder(
				"Library Names, that do not have parent loaders: ");
			for (String libraryName : standardJEELibrariesNames) {
				sb.append(" ").append(libraryName).append(", ");
			}
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.PARENT_CLASS_LOADER_DOES_NOT_EXIST,
				sb.toString());
			sde.setMessageID("ASJ.dpl_ds.005115");
			throw sde;
		}

		return createApplicationClassLoader(standardLibraryLoaders,
			heavyClassLoaderJars, dInfo.getApplicationName()
				+ DeployConstants.LIBRARY_LOADER_SUFFIX, 
				dInfo.getApplicationName());
	}

	private static List<ClassLoader> createParentClassLoaders(
		final DeploymentInfo dInfo, final Set<String> parentCLNames)
		throws ServerDeploymentException, MalformedURLException,
		UnknownClassLoaderException {

		final List<ClassLoader> result = new ArrayList<ClassLoader>();

		// The heavy files class loader has to be first.
		final ClassLoader heavyClassLoader = createHeavyClassLoader(dInfo);
		if (heavyClassLoader != null) {
			result.add(heavyClassLoader);
		}

		final Set<String> missingCLNames = new LinkedHashSet<String>();
		for (String clName : parentCLNames) {
			final ClassLoader classLoader = PropManager.getInstance()
					.getLoadContext().getClassLoader(clName);
			if (classLoader != null) {
				result.add(classLoader);
			} else {
				missingCLNames.add(clName);
			}
		}
		if (missingCLNames.size() > 0) {
			DSLog.logWarning(
					location,
				"ASJ.dpl_ds.000399",
				"The application [{0}] has explicit or implicit reference to [{1}], but there are no loaders with such names and they will be ignored.",
				dInfo.getApplicationName(), 
				CAConvertor.toString(missingCLNames, ""));
		}
		return result;
	}

	// we need to make sure that createClassLoader have CSN component as
	// parameter
	private static ClassLoader createApplicationClassLoader(
		final List<ClassLoader> parentClassLoaders, final String[] paths,
		final String loaderName, final String appName) 
		throws MalformedURLException, UnknownClassLoaderException {
		final ClassLoader[] parents = new ClassLoader[parentClassLoaders.size()];
		parentClassLoaders.toArray(parents);

		DSLog.traceDebug(location,
			"Will create [{0}] class loader for [{1}] application with parents=[{2}] and paths=[{3}].",
			loaderName, appName, CAConvertor.toString(parents, ""), 
			CAConvertor.toString(paths, ""));

		Accounting.beginMeasure(ITimeStatConstants.CREATE_CLASSLOADER,
			PropManager.getInstance().getLoadContext().getClass());
		try {
			return PropManager.getInstance().getLoadContext()
				.createClassLoader(parents, paths, loaderName, appName,
					LoadContext.COMP_TYPE_APPLICATION);
		} finally {
			Accounting.endMeasure(ITimeStatConstants.CREATE_CLASSLOADER);
		}
	}

	public static void unregisterLoader(String loaderName, boolean isError2Exist)
		throws DeploymentException {
		try {
			final ClassLoader appClassLoader = PropManager.getInstance()
				.getLoadContext().getClassLoader(loaderName);
			DSLog.traceDebug(location, "Will unregister [{0}] class loader instance [{1}].",
				loaderName, appClassLoader);
			if (appClassLoader != null) {
				if (isError2Exist) {
					DSLog.logErrorThrowable(
							location,
						"ASJ.dpl_ds.006395",
						"Issue on unregister class loader", new Exception(
							"Existing ["
							+ loaderName
							+ "] class loader instance ["
							+ appClassLoader
							+ "] "
							+ "shows issue related to application start or stop operation and system will try to recover."));
				}

				Accounting.beginMeasure(
					ITimeStatConstants.UNREGISTER_CLASSLOADER,
					PropManager.getInstance().getLoadContext().getClass());
				try {
					PropManager.getInstance().getLoadContext().unregister(
						appClassLoader);
				} finally {
					Accounting.endMeasure(
						ITimeStatConstants.UNREGISTER_CLASSLOADER);
				}
			}
		} catch (Exception e) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_REMOVE_APPLOADER,
				new String[] { loaderName }, e);
			sde.setMessageID("ASJ.dpl_ds.005066");
			throw sde;
		}
	}
}