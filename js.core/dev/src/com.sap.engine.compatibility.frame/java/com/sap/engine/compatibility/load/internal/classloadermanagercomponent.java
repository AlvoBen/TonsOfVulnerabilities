package com.sap.engine.compatibility.load.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.packageadmin.PackageAdmin;

import com.sap.engine.boot.loader.BytecodeModifier;
import com.sap.engine.frame.core.load.ClassInfo;
import com.sap.engine.frame.core.load.ClassLoaderResource;
import com.sap.engine.frame.core.load.ClassWithLoaderInfo;
import com.sap.engine.frame.core.load.Component;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.frame.core.load.ReferencedLoader;
import com.sap.engine.frame.core.load.UnknownClassLoaderException;
import com.sap.engine.frame.core.load.gui.ComponentInfo;
import com.sap.engine.lib.util.HashMapObjectInt;

public class ClassLoaderManagerComponent implements LoadContext {

	private ComponentContext cc;
	private BundleContext bc;
	private PackageAdmin packageAdmin;

	void activate(ComponentContext context) {
		cc = context;
		bc = cc.getBundleContext();
	}

	void deactivate(ComponentContext context) {
		bc = null;
		cc = null;
	}

	void bindPackageAdmin(PackageAdmin pa) {
		packageAdmin = pa;
	}

	void unbindPackageAdmin(PackageAdmin pa) {
		if (packageAdmin == pa)
			packageAdmin = null;
	}

	public ClassLoader createClassLoader(ClassLoader[] parents, URL[] paths,
			String name) throws UnknownClassLoaderException,
			MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createClassLoader(ClassLoader[] parents, String[] paths,
			String name) throws UnknownClassLoaderException,
			MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createClassLoader(ClassLoader[] parents, URL[] paths,
			String name, String componentName, int componentType)
			throws UnknownClassLoaderException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createClassLoader(ClassLoader[] parents, String[] paths,
			String name, String componentName, int componentType)
			throws UnknownClassLoaderException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createClassLoader(ClassLoader[] parents, URL[] paths,
			String name, String csnComponentName, String componentName,
			int componentType) throws UnknownClassLoaderException,
			MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createClassLoader(ClassLoader[] parents, String[] paths,
			String name, String csnComponentName, String componentName,
			int componentType) throws UnknownClassLoaderException,
			MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createLocalClassLoader(ClassLoader[] parents,
			URL[] paths, String name, BytecodeModifier modifier)
			throws UnknownClassLoaderException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createLocalClassLoader(ClassLoader[] parents,
			String[] paths, String name, BytecodeModifier modifier)
			throws UnknownClassLoaderException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createLocalClassLoader(ClassLoader[] parents,
			URL[] paths, String name, String componentName, int componentType,
			BytecodeModifier modifier) throws UnknownClassLoaderException,
			MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createLocalClassLoader(ClassLoader[] parents,
			String[] paths, String name, String componentName,
			int componentType, BytecodeModifier modifier)
			throws UnknownClassLoaderException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createLocalClassLoader(ClassLoader[] parents,
			URL[] paths, String name, String csnComponentName,
			String componentName, int componentType, BytecodeModifier modifier)
			throws UnknownClassLoaderException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader createLocalClassLoader(ClassLoader[] parents,
			String[] paths, String name, String csnComponentName,
			String componentName, int componentType, BytecodeModifier modifier)
			throws UnknownClassLoaderException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getChildren(String loaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassInfo getClassInfo(Class cls) {
		Bundle b = packageAdmin.getBundle(cls);
		if (b != null) {
			@SuppressWarnings("unchecked")
			Dictionary<String, String> headers = (Dictionary<String, String>) b
					.getHeaders();
			String csnComponent = headers.get("SAP-CSNComponent");
			String dcName = headers.get("SAP-DCName");
			// TODO Component Type.
			return new ClassWithLoaderInfo(cls.getName(), b.getSymbolicName(),
					new Component(csnComponent, dcName, COMP_TYPE_LIBRARY));
		}
		return null;
	}

	public ClassLoader getClassLoader(String loaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader[] getClassLoaderParents(ClassLoader loader) {
		// TODO Auto-generated method stub
		return null;
	}

	public URL[] getClassLoaderURLs(ClassLoader loader)
			throws MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLeakedClassLoaders(Properties properties) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassInfo getLoaderComponentInfo(ClassLoader loader) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassInfo getLoaderComponentInfo(String loaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLoaderDetailedInfo(ClassLoader loader) {
		// TODO Auto-generated method stub
		return null;
	}

	public ComponentInfo getLoaderWrapper(String loaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public ComponentInfo[] getLoaderWrappers() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMapObjectInt getLoadersCompTypesMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName(ClassLoader loader) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getParents(String loaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getReferences(String loaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getReferences(ClassLoader loader) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getReferencesFrom(String loaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getReferencesFrom(ClassLoader loader) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getResourceNames(ClassLoader loader) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getResourceNames(String loaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ClassLoaderResource> getResources(
			List<String> classloaderNames, String extension) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getTransitiveParents(String loaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getTransitiveParents(ClassLoader loader) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getTransitiveReferences(String loaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getTransitiveReferences(ClassLoader loader) {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration listLoaders() {
		final Bundle[] bundles = bc.getBundles();
		return new Enumeration<String>() {
			int i = 0;

			public boolean hasMoreElements() {
				return i < bundles.length;
			}

			public String nextElement() {
				return bundles[i++].getSymbolicName();
			}
		};
	}

	public boolean register(ReferencedLoader loader) throws Exception {
		throw new UnsupportedOperationException(
				"Used dropped 6.40 functionality.");
	}

	public boolean register(ReferencedLoader loader, String componentName,
			int componentType) throws Exception {
		throw new UnsupportedOperationException(
				"Used dropped 6.40 functionality.");
	}

	public boolean registerReference(String fromLoaderName, String toLoaderName) {
		throw new UnsupportedOperationException(
				"Used dropped 6.40 functionality.");
	}

	public boolean unregister(ClassLoader loader) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean unregisterReference(String fromLoaderName,
			String toLoaderName) {
		throw new UnsupportedOperationException();
	}
}
