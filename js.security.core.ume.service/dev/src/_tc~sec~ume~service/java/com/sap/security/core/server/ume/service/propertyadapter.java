package com.sap.security.core.server.ume.service;

import java.util.Map;

import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.security.core.util.config.IProperty;

/**
 * Adapter for configuration manager's {@link PropertyEntry}.
 * 
 * @author d034567
 * 
 */
public class PropertyAdapter implements IProperty {

    private PropertyEntry       _delegate;

    private Map<String, String> _differingInstanceValues;

    /**
     * Create a new PropertyAdapter.
     * 
     * @param delegate
     *            {@link PropertyEntry} for the "custom_global" configuration level
     */
    public PropertyAdapter(PropertyEntry delegate, Map<String, String> differingInstanceValues) {
        _delegate = delegate;
        _differingInstanceValues = differingInstanceValues;
    }

    public String getName() {
        return _delegate.getName();
    }

    public String getGlobalValue() {
        return _delegate.getValue().toString();
    }

    public String getDefaultValue() {
        return _delegate.getDefault().toString();
    }

    public Map<String, String> getDifferingInstanceValues() {
        return _differingInstanceValues;
    }

    public boolean isOnlineModifiable() {
        // TODO Use the following later (unfortunately it has a "throws" clause)?
        // return _delegate.isOnlineModifiable();

        int flags = _delegate.getCustomFlags();
        if (flags == - 1) {
            flags = _delegate.getDefaultFlags();
        }
        return (flags & PropertyEntry.ENTRY_TYPE_ONLINE_MODIFIABLE) != 0;
    }

    public boolean isSecure() {
        return _delegate.isSecure();
    }

}
