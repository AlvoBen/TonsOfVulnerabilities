package com.sap.security.api.umap.system;

import java.util.Enumeration;
import java.util.Locale;

import com.sap.security.api.IPrincipal;

/**
 * For internal use only!
 * 
 * <p>
 * Thin wrapper around an {@link com.sap.security.api.umap.system.ISystemLandscapeWrapper}
 * (old system landscape interface) instance that provides a full
 * {@link com.sap.security.api.umap.system.ISystemLandscape} (new, extended system
 * landscape interface) instance.
 * </p>
 * 
 * <p>
 * Used for temporary backwards compatibility with old system landscape implementations
 * which don't implement the new interface.
 * </p>
 * 
 * @author d034567
 *
 */
@SuppressWarnings("deprecation")
public class WrappingSystemLandscape implements ISystemLandscape {

	private ISystemLandscapeWrapper _landscapeWrapper;
	private String                  _type;
	private String                  _storageKeyPrefix = null;

	public WrappingSystemLandscape(ISystemLandscapeWrapper landscapeWrapper, String type, String storagePrefix) {
		_landscapeWrapper = landscapeWrapper;
		_type             = type;
		_storageKeyPrefix = storagePrefix;
	}

	public String getType() {
		return _type;
	}

	public String getDisplayName(Locale locale) {
		return _type;
	}

	public Enumeration getAllAliases(IPrincipal principal)
	throws ExceptionInImplementationException {
		return _landscapeWrapper.getAllAliases(principal);
	}

	public ISystemLandscapeObject getSystemByUniqueKey(String uniqueKey)
	throws ExceptionInImplementationException {
		return _landscapeWrapper.getSystemByUniqueKey(uniqueKey);
	}

	public ISystemLandscapeObject getSystemByAlias(String alias)
	throws ExceptionInImplementationException {
		return _landscapeWrapper.getSystemByAlias(alias);
	}

	public String getStorageKeyPrefix() {
		return _storageKeyPrefix;
	}

	public Enumeration getAllAliases() throws ExceptionInImplementationException {
		return _landscapeWrapper.getAllAliases();
	}

	public String getUniqueKeyForAlias(String alias) throws ExceptionInImplementationException {
		return _landscapeWrapper.getUniqueKeyForAlias(alias);
	}

	public Enumeration getAllUniqueKeys() throws ExceptionInImplementationException {
		return _landscapeWrapper.getAllUniqueKeys();
	}

}
