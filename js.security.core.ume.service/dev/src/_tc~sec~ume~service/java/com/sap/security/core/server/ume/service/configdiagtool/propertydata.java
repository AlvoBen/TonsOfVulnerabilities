package com.sap.security.core.server.ume.service.configdiagtool;

import com.sap.engine.frame.core.configuration.addons.PropertyEntry;

public class PropertyData {

	private Object  _value;
	private boolean _isInherited;
	private boolean _isCustom;
	private boolean _isFinal;
	private boolean _isOnlineModifiable;

	private PropertyData() { /* Must not be used. */ }

	/**
	 * Constructor for a real PropertyEntry from the configuration manager API.
	 * 
	 * <p>
	 * All getter methods of the resulting object return valid data.
	 * </p>
	 * @param propertyEntry
	 */
	public PropertyData(PropertyEntry propertyEntry) {
		int flags = propertyEntry.getCustomFlags();
		if(flags == -1) {
			flags = propertyEntry.getDefaultFlags();
		}

		_value              = propertyEntry.getValue();
		_isInherited        = propertyEntry.isInherited();
		_isCustom           = propertyEntry.getCustom() != null;
		_isFinal            = (flags & PropertyEntry.ENTRY_TYPE_FINAL)             != 0;
		_isOnlineModifiable = (flags & PropertyEntry.ENTRY_TYPE_ONLINE_MODIFIABLE) != 0;
	}

	/**
	 * Constructor for a runtime property value.
	 * 
	 * <p>
	 * The resulting object only knows the property value, while all other information
	 * (status information, flags) are not available!
	 * </p>
	 * @param value
	 */
	public PropertyData(String value) {
		_value = value;
	}

	public Object getValue() {
		return _value;
	}

	public boolean isCustom() {
		return _isCustom;
	}

	public boolean isFinal() {
		return _isFinal;
	}

	public boolean isInherited() {
		return _isInherited;
	}

	public boolean isOnlineModifiable() {
		return _isOnlineModifiable;
	}

}
