package com.sap.engine.cache.spi.policy.impl;

import java.util.Properties;

import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.PluginContext;
import com.sap.util.cache.spi.policy.ElementAttributes;
import com.sap.util.cache.spi.policy.EvictionPolicy;

/**
 * @author Petev, Petio, i024139
 */
public class DummyEvictionPolicy implements EvictionPolicy {

  private String name = "DummyEvictionPolicy";

  public void init(String name, Properties properties) {
    this.name = name;
  }

  public boolean exists(String key) {
    return false;
  }

  public String choose() {
    return null;
  }

  public void onAccess(String key) {
  }

  public void onRemove(String key) {
  }

  public int getCount() {
    return 0;
  }

  public int getSize() {
    return 0;
  }

  public void start() throws PluginException {
  }

  public Pluggable getInstance() throws PluginException {
    return this;
  }

  public void setPluginContext(PluginContext ctx) {
  }

  public void stop() {
  }

  public void shutdown() {
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return null;
  }

	public ElementAttributes getElementAttributes(String arg0) {
		return null;
	}

	public void onInvalidate(String arg0) {

	}

	public void onPut(String arg0, ElementAttributes arg1) {

	}

}
