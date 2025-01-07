package com.sap.security.api.umap.system;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.api.umap.IUserMapping;

/**
 * Dummy implementation of a backend system object for scenarios where no real
 * system landscape is available (see also
 * {@link com.sap.security.api.umap.system.DummySystemLandscapeWrapperImpl}).
 * 
 * Properties of this implemenation:
 * <ul>
 *   <li>The system's unique key is the same as the system's alias.</li>
 * </ul>
 */
public class DummySystemObjectImpl implements ISystemLandscapeObject {

    private String                          _alias;
    private String                          _logonMethod;
    private DummySystemLandscapeWrapperImpl _landscape;
    private Map<Object, Object>             _attributes;

    /**
     * Create a new dummy system landscape object.
     * 
     * This constructor implies the following default values:
     * <ul>
     *   <li>Logon method: {@link ILoginConstants#SSO_JCO_LOGON_METHOD_UIDPW}
     *   <li>Storage prefix: {@link IUserMapping#UMAP_EP6_ALIAS_PREFIX}
     * </ul>
     * 
     * @param alias System alias (which is also used as unique key)
     */
    public DummySystemObjectImpl(String alias) {
        this(alias, ILoginConstants.SSO_JCO_LOGON_METHOD_UIDPW);
    }

    /**
     * Create a new dummy system landscape object.
     * 
     * This constructor implies the following default values:
     * <ul>
     *   <li>Storage prefix: {@link IUserMapping#UMAP_EP6_ALIAS_PREFIX}
     * </ul>
     *
     * @param alias System alias (which is also used as unique key)
     * @param logonMethod Logon method to be used for the system
     */
    public DummySystemObjectImpl(String alias, String logonMethod) {
        // The corresponding system landscape is unknown
        this(alias, logonMethod, null);
    }

    /**
     * Create a new dummy system landscape object.
     * 
     * @param alias System alias (which is also used as unique key)
     * @param logonMethod Logon method to be used for the system
     * @param landscape Corresponding system landscape implementation which
     *                  contains this system landscape object.
     */
    public DummySystemObjectImpl(String alias, String logonMethod,
        DummySystemLandscapeWrapperImpl landscape)
    {
        _alias       = alias;
        _logonMethod = logonMethod;
        _landscape   = landscape;
        _attributes  = new HashMap<Object, Object>();

        // Default system attributes:

        // User mapping data for the system can only be maintained by administrators
        _attributes.put(IUserMapping.UMAP_USERMAPPING_TYPE, IUserMapping.UMAP_USERMAPPING_TYPE_ADMIN);
        // Logon method from usual arguments
        _attributes.put(IUserMapping.UMAP_SYSTEMATTRS_LOGONMETHOD, _logonMethod);
        // System type needs to be a valid value to make sure the system appears
        // as SAP reference system candidate in the UI
        _attributes.put(IUserMapping.UMAP_SYSTEM_TYPE, "SAP_R3");
        // No additional user mapping fields
        _attributes.put(IUserMapping.UMAP_USERMAPPING_FIELDS, null);
    }

    /**
     * See {@link ISystemLandscapeObject#getAlias()}
     */
    public String getAlias() {
        return _alias;
    }

    public Class getLandscapeClass() {
        if(_landscape != null) {
            return _landscape.getClass();
        }
        else {
            // Default: Not the landscape's class, but this object's own class
            return getClass();
        }
    }

    public String getLandscapeType() {
    	if(_landscape != null) {
        	return _landscape.getType();
    	}
    	else {
    		return DummySystemLandscapeWrapperImpl.TYPE_DUMMY;
    	}
    }

    /**
     * See {@link ISystemLandscapeObject#getLogonMethod()}
     */
    public String getLogonMethod() {
        return _logonMethod;
    }

    /**
     * See {@link ISystemLandscapeObject#getPrintableName()}
     */
    public String getPrintableName() {
        return "Dummy system: " + _alias;
    }

    /**
     * See {@link ISystemLandscapeObject#getStorageKeyPrefix()}
     */
    public String getStorageKeyPrefix() {
        if(_landscape != null) {
            return _landscape.getStorageKeyPrefix();
        }
        else {
            return IUserMapping.UMAP_EP6_ALIAS_PREFIX;
        }
    }

    /**
     * See {@link ISystemLandscapeObject#getSystemDescription()}
     */
    public String getSystemDescription() {
        return getPrintableName();
    }

    /**
     * See {@link ISystemLandscapeObject#getUniqueKey()}
     */
    public String getUniqueKey() {
        return _alias;
    }

    /**
     * See {@link ISystemLandscapeObject#getAttribute(String)}
     */
    public Object getAttribute(String attributeKey) {
        if(_attributes != null) {
            return _attributes.get(attributeKey);
        }
        else {
            return null;
        }
    }

    /**
     * See {@link ISystemLandscapeObject#getAttributeNames()}
     */
    public Enumeration getAttributeNames() {
        if(_attributes != null) {
            final Iterator attributeNames = _attributes.keySet().iterator();

            // Enumeration returning existing attributes
            return new Enumeration() {
                public boolean hasMoreElements() {
                    return attributeNames.hasNext();
                }

                public Object nextElement() {
                    return attributeNames.next();
                }
            };

        }
        else {

            // Empty enumeration because there are no existing attributes
            return new Enumeration() {
                public boolean hasMoreElements() {
                    return false;
                }

                public Object nextElement() {
                    return null;
                }
            };

        }
    }

    @Override
	public String toString() {
        return MessageFormat.format(
            "Dummy system landscape object: {0}\n" +            "Alias, unique key            : ''{1}''\n" +            "Logon method                 : ''{2}''\n" +            "Attributes                   : {3}\n" +            "System landscape object type : {4}\n" +
            "System landscape wrapper type: {5}",
            new Object[] {
                _alias,
                _alias,
                _logonMethod,
                _attributes,
                getClass().getName(),
                _landscape != null ? _landscape.getClass().getName() : "(unknown)"
            }
        );
    }

    // Helper method for UME JVer tests, not contained in interface ISystemLandscapeObject
    public void addAttribute(Object name, Object value) {
        _attributes.put(name, value);
    }

}
