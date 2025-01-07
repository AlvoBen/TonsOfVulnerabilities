package com.sap.jmx.provider.adapter;

import javax.management.JMException;
import javax.management.ObjectName;

import com.sap.jmx.provider.ProviderException;
import com.sap.jmx.provider.ProviderRootContext;
import com.sap.jmx.provider.StandardMBeanProvider;

public class CIMStandardLazyMBeanProvider implements StandardMBeanProvider {

	private String[] values;
	
	protected final String key = "cimclass";
	protected ProviderRootContext context;
	
	public CIMStandardLazyMBeanProvider(String className) {
		values = new String[] { className };
	}
	
	public CIMStandardLazyMBeanProvider(String[] classNames) {
		values = classNames;
	}

	public final String[] getSuppliedKeys() {
		return new String[] { key };
	}

	public final String[] getSuppliedValues(String key) {
		if (this.key.equals(key)) {
			return values;
		} else {
			return null;
		}
	}

	public void init(ProviderRootContext context) throws JMException {
		this.context = context;
		init();
	}

	public Object instantiateMBean(ObjectName name) {
		return null;
	}

	public ObjectName[] getConnected(ObjectName name, Object mBean)	throws ProviderException, JMException {
		return null;
	}

	//here is the place to initialize provider & to register the static MBeans
	protected void init() throws JMException {
	}
	
	public void destroy() throws JMException {
	}
}
