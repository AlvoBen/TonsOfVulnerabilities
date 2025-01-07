package com.sap.jmx.provider.adapter;

import java.util.Set;

import javax.management.JMException;
import javax.management.ObjectName;
import javax.management.QueryExp;

import com.sap.jmx.provider.ExtendedMBeanProvider;
import com.sap.jmx.provider.ProviderException;
import com.sap.jmx.provider.ProviderRootContext;

public class ComplexLazyMBeanProvider implements ExtendedMBeanProvider {

	private String[] cimValues;
	private String[] jsrValues;
	
	protected final String cimKey = "cimclass";
	protected final String jsrKey = "j2eeType";
	
	protected ProviderRootContext context;
	
	public ComplexLazyMBeanProvider(String cimClassName, String j2eeType) {
		cimValues = new String[] { cimClassName };
		jsrValues = new String[] { j2eeType };
	}
	
	public ComplexLazyMBeanProvider(String[] cimClassNames, String[] j2eeTypes) {
		cimValues = cimClassNames;
		jsrValues = j2eeTypes; 
	}

	public final String[] getSuppliedKeys() {
		return new String[] { cimKey, jsrKey };
	}

	public final String[] getSuppliedValues(String key) {
		if (cimKey.equals(key)) {
			return cimValues;
		} else if (jsrKey.equals(key)){
			return jsrValues;
		} else {
			return null;
		}
	}

	public final void init(ProviderRootContext context) throws JMException {
		this.context = context;
		init();
	}
	
	public Set queryNames(ObjectName name, QueryExp query) {
		return null;
	}

	public Object instantiateMBean(ObjectName name) {
		return null;
	}

	public ObjectName[] getConnected(ObjectName name, Object mBean)	throws ProviderException, JMException {
		return null;
	}

	protected void init() throws JMException {
	}
	
	public void destroy() throws JMException {
	}
	
	public boolean isLazyRegistered(ObjectName name) {
		Set result = queryNames(name, null);
		if ((result != null) && (!result.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getLazyMBeanCount() {
		Set result = queryNames(null, null);
		if (result != null) {
			return result.size();
		} else {
			return -1;
		}
	}	
	
	public final boolean staticKeyValueQueryCheck(ObjectName name) {
		if (name != null) {
			String cimclassValue = name.getKeyProperty(cimKey);
			if (cimclassValue != null) {
				if (cimValues != null) {
					for (int i = 0; i < cimValues.length; i++) {
						if (cimclassValue.equals(cimValues[i])) {
							return true;
						}
					}
					return false;
				} 
			} else {
				String j2eeTypeValue = name.getKeyProperty(jsrKey);
				if (j2eeTypeValue != null) {
					if (jsrValues != null) {
						for (int i = 0; i < jsrValues.length; i++) {
							if (j2eeTypeValue.equals(jsrValues[i])) {
								return true;
							}
						}
						return false;
					}
				}
			}
		}
		return true;
	}	
}
