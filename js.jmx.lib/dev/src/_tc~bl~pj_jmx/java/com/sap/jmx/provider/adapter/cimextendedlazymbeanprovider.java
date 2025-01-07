package com.sap.jmx.provider.adapter;

import java.util.Set;

import javax.management.ObjectName;
import javax.management.QueryExp;

import com.sap.jmx.provider.ExtendedMBeanProvider;

public class CIMExtendedLazyMBeanProvider extends CIMStandardLazyMBeanProvider implements ExtendedMBeanProvider {
	
	//constructor
	public CIMExtendedLazyMBeanProvider(String className) {
		super(className);
	}
	
	//constructor
	public CIMExtendedLazyMBeanProvider(String[] classNames) {
		super(classNames);
	}
	
	/**
	 * @see com.sap.jmx.provider.ExtendedMBeanProvider#queryNames(javax.management.ObjectName)
	 */
	public Set queryNames(ObjectName name, QueryExp query) {
		return null;
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
			String cimclassValue = name.getKeyProperty(key);
			if (cimclassValue != null) {
				String[] values = getSuppliedValues(key);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						if (cimclassValue.equals(values[i])) {
							return true;
						}
					}
					return false;
				} 
			}
		}
		return true;		
	}
}