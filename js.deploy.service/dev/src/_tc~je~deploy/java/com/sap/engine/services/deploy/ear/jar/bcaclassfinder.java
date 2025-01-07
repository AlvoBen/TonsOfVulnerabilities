/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.ear.jar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ApplicationStatusResolver;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.lib.javalang.tool.ClassFinder;
import com.sap.tc.logging.Location;

/**
 * An implementation of the <b>Byte Code Analysis</b> <tt>ClassFinder</tt>
 * interface.   
 * @author Luchesar Cekov
 */
public class BcaClassFinder implements ClassFinder {
	private static final Location location = 
		Location.getLocation(BcaClassFinder.class);
	
	private final Set<ZipFileWrapper> zipResources;
	private final Set<File> folderResources;
	private final Set<ClassLoader> parents;

	private final List<FileInputStream> forClose = new LinkedList<FileInputStream>();

	public BcaClassFinder(EarDescriptor descriptor) throws IOException {
		Set<File> zipFiles = new LinkedHashSet<File>();
		folderResources = new LinkedHashSet<File>();
		parents = new LinkedHashSet<ClassLoader>();
		OfflineApplicationClassPathResolver.getCLResourcesOrCLs(descriptor,
			zipFiles, folderResources, parents);
		if (location.beDebug()) {
			DSLog.traceDebugObject(location, "BcaClassFinder.init calculated zipResources - ",
				zipFiles);
			DSLog.traceDebugObject(location, "\n BcaClassFinder.init calculated folderResources - ",
				folderResources);
			DSLog.traceDebugObject(location, "\n BcaClassFinder.init calculated parent loaders - ",
				parents);
		}
		zipResources = new LinkedHashSet<ZipFileWrapper>(zipFiles.size());
		for (File file : zipFiles) {
			addZipResources(file);
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.lib.javalang.tool.ClassFinder#getClassAsStream(java.lang.String)
	 */
	public InputStream getClassAsStream(String className) {
		if (location.beDebug()) {
			DSLog.traceDebug(location, "BcaClassFinder.getClassAsStream [{0}].",
				className);
		}
		if (className == null) {
			return null;
		}
		StringBuilder classNameBuilder = new StringBuilder(
			className.replace('.', '/'));
		if (!className.endsWith(".class")) {
			classNameBuilder.append(".class");
		}
		className = classNameBuilder.toString();

		InputStream result = null;
		for (ClassLoader cl : parents) {
			result = cl.getResourceAsStream(className);
			if (result != null) {
				return result;
			}
		}

		try {
			for (File folder : folderResources) {
				File classFile = new File(folder, className);
				if (classFile.exists() && !classFile.isDirectory()) {
					FileInputStream fis = new FileInputStream(classFile);
					forClose.add(fis);
					return fis;
				}
			}

			for (ZipFileWrapper zip : zipResources) {
				ZipEntry entry = zip.zip().getEntry(className);
				if (entry != null) {
					return zip.zip().getInputStream(entry);
				}
			}
		} catch (IOException e) {
			DSLog.logErrorThrowable(location, e);
		}

		return null;
	}

	public void addResources(List<File> resources) throws IOException {
		if (location.beDebug()) {
			DSLog.traceDebugObject(location, "BcaClassFinder.addResources: ",
				resources);
		}
		for (File f : resources) {
			addResource(f);
		}
	}

	private void addZipResources(File f) throws IOException {
		try {
			ZipFileWrapper zfw = new ZipFileWrapper(f);
			if (!zipResources.add(zfw)) {
				try {
					zfw.zip().close();
				} catch (IOException e) {
					if (location.beDebug()) {
						DSLog.traceDebug(location, "Cannot close zip file [{0}]",
							zfw.getName());
					}
				}
			}
		} catch (ZipException e) {
			if (location.beDebug()) {
				DSLog.traceDebug(
					location, 
					"BcaClassFinder.addResources: Cannot open zip file [{0}], because it is not a valid zip archive",
					f.getAbsolutePath());
			}
		}
	}

	public void addResource(File f) throws IOException {
		if (location.beDebug()) {
			DSLog.traceDebugObject(location, "BcaClassFinder.addResource ",
				f);
		}
		if (f.isDirectory()) {
			folderResources.add(f);
		} else {
			addZipResources(f);
		}
	}

	public void clear() {
		for (ZipFileWrapper zipFile : zipResources) {
			try {
				zipFile.zip().close();
			} catch (IOException e) {
				if (location.beDebug()) {
					DSLog.traceDebug(location, "Cannot close zip file [{0}]",
						zipFile.getName());
				}
			}
		}

		for (FileInputStream fis : forClose) {
			try {
				fis.close();
			} catch (Exception e) {
				if (location.beDebug()) {
					DSLog.traceDebug(location, "Cannot close file input stream [{0}]",
						fis);
				}
			}
		}

	}

	public Set<File> getFolderResources() {
		return folderResources;
	}

	public Set<ClassLoader> getParents() {
		return parents;
	}

	public Set<? extends File> getZipResources() {
		return zipResources;
	}

	private class ZipFileWrapper extends File {
		private static final long serialVersionUID = -7005270652608147318L;
		private final ZipFile internalZip;

		public ZipFileWrapper(File f) throws ZipException, IOException {
			super(f.getAbsolutePath());
			internalZip = new ZipFile(f);
		}

		public ZipFile zip() {
			return internalZip;
		}
	}

	private static class OfflineApplicationClassPathResolver {
		public static void getCLResourcesOrCLs(EarDescriptor descriptor,
			Set<File> clZipResources, Set<File> clFolderResources,
			Set<ClassLoader> allCLParents) {
			final HashSet<String> visited = new HashSet<String>();
			if (location.beDebug()) {
				DSLog.traceDebug(location, "Collects class loader resources or class loaders for application [{0}].",
					descriptor.getDisplayName());
			}
			addDeepCLResourcesOrCLs(clZipResources, clFolderResources,
				allCLParents, getStandardAppRefs(), visited);
			addDeepCLResourcesOrCLs(clZipResources, clFolderResources,
				allCLParents, getRefs(descriptor), visited);
		}

		private static String[] getStandardAppRefs() {
			try {
				final List<Component> lstAppRefs = PropManager.getInstance()
					.getStandardAppRefs();
				final String[] result = new String[lstAppRefs.size()];
				for (int i = 0; i < result.length; i++) {
					result[i] = lstAppRefs.get(i).toString();
				}
				return result;
			} catch (IllegalStateException e) {
				return new String[0];
			}
		}

		private static void addDeepCLResourcesOrCLs(Set<File> clZipResources,
			Set<File> clFolderResources, Set<ClassLoader> parents,
			String[] refsNames, Set<String> visited) {

			if (refsNames != null) {
				String loaderName = null;
				ClassLoader loader = null;
				DeploymentInfo withoutLoader = null;
				File fileForAdd = null;
				for (int i = 0; i < refsNames.length; i++) {
					loaderName = refsNames[i];
					if (visited(visited, loaderName)) {
						continue;
					}
					loader = getClassLoader(loaderName);
					if (loader != null) {
						if (location.beDebug()) {
							DSLog.traceDebug(location, "Adds class loader in depth for a component loader with name [{0}] and instance [{1}].",
								loaderName,
								loader);
						}
						parents.add(loader);
					} else {
						withoutLoader = Applications.get(loaderName);
						if (location.beDebug()) {
							DSLog.traceDebugObject(location, "Adds resource files in depth for a component loader with name [{0}] and instance ",
								withoutLoader,
								loaderName);
						}
						if (withoutLoader != null) {
							addDeepCLResourcesOrCLs(clZipResources,
								clFolderResources, parents,
								getRefs(withoutLoader), visited);
							final String appLoaderFiles[] = withoutLoader
								.getApplicationLoaderFiles();
							if (appLoaderFiles != null) {
								for (int f = 0; f < appLoaderFiles.length; f++) {
									fileForAdd = new File(appLoaderFiles[f]);
									if (fileForAdd.isDirectory()) {
										clFolderResources.add(fileForAdd);
									} else {
										clZipResources.add(fileForAdd);
									}
								}
							}
						} else {
							if (location.beDebug()) {
								DSLog.traceDebug(location, "A component loader with name [{0}] does not exist, because it is stopped or not deployed.",
									loaderName);
							}
						}
					}
				}
			}
		}

		private static ClassLoader getClassLoader(String loaderName) {
			try {
				return PropManager.getInstance().getLoadContext()
					.getClassLoader(loaderName);
			} catch (NullPointerException ex) {
				return null;
			}
		}

		private static String[] getRefs(EarDescriptor descriptor) {
			if (descriptor.getDisplayName() == null) {
				return new String[0];
			}
			Set<Edge<Component>> referencesTo = 
				Applications.getReferenceGraph()
					.getReferencesToOthersFrom(// TODO: optimize it
						new Component(descriptor.getProviderName()
							+ DeployConstants.DELIMITER_4_PROVIDER_AND_NAME
							+ descriptor.getDisplayName(),
							Component.Type.APPLICATION));
			String[] result = new String[referencesTo.size()
				+ (descriptor.getReferences() == null ? 0 : descriptor
				.getReferences().length)];
			int i = 0;
			for (Edge<Component> edge : referencesTo) {
				// TODO: research whether we can filter RESOURCE_NOT_PROVIDED
				result[i++] = edge.getSecond().toString();
			}

			if (descriptor.getReferences() != null) {
				for (ReferenceObjectIntf declaredRef : descriptor
					.getReferences()) {
					result[i++] = ((ReferenceObject) declaredRef).toString();
				}
			}

			return result;
		}

		private static String[] getRefs(DeploymentInfo info) {
			Set<Edge<Component>> referencesTo = Applications
					.getReferenceGraph().getReferencesToOthersFrom(
							new Component(info.getApplicationName(),
								Component.Type.APPLICATION));
			String[] result = new String[referencesTo.size()
					+ (info.getReferences() == null ? 0
							: info.getReferences().length)];
			int i = 0;
			for (Edge<Component> edge : referencesTo) {
				result[i++] = edge.getSecond().toString();
			}

			if (info.getReferences() != null) {
				for (ReferenceObject declaredRef : info.getReferences()) {
					result[i++] = declaredRef.getName();
				}
			}

			return result;
		}

		private static boolean visited(Set<String> visited, String name) {
			if (visited.contains(name)) {
				return true;
			}
			visited.add(name);
			return false;
		}
	}
}