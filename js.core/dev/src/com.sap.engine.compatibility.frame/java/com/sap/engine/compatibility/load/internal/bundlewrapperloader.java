package com.sap.engine.compatibility.load.internal;

import org.osgi.framework.Bundle;

/**
 * The Bundle Wrapper Loader, attempting to bridge the class-loader centric
 * world, and the component centric world that is OSGi. Currently extends
 * {@link ClassLoader} instead of MultiParentClassLoader, but this will probably
 * change in the near future.
 */
public class BundleWrapperLoader extends ClassLoader {
	Bundle bundle;

	public BundleWrapperLoader(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		return bundle.loadClass(name);
	}
}
