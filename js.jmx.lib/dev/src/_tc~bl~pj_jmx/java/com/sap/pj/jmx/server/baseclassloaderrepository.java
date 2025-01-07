package com.sap.pj.jmx.server;

import javax.management.loading.ClassLoaderRepository;

/**
 * Base class to extend to create custom ClassLoaderRepositories
 */

public abstract class BaseClassLoaderRepository implements ClassLoaderRepository {
  protected BaseClassLoaderRepository() {
  }

  protected abstract void add(ClassLoader cl);

  protected abstract void remove(ClassLoader cl);
}
