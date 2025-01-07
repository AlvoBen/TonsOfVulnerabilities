package com.sap.security.api.umap.system;

import java.util.Enumeration;
import java.util.Locale;

import com.sap.security.api.IPrincipal;

/**
 * <p>This interface serves as wrapper for a system landscape to be plugged into
 *   UME. The {@link com.sap.security.api.umap.system.ISystemLandscapeObject}
 *   objects provided by an <code>ISystemLandscapeWrapper</code> will be used by
 *   the <i>User Mapping</i> functionality of UME (see
 *   {@link com.sap.security.api.umap.IUserMapping} as an entry point).
 * </p>
 * 
 * <p>At runtime, all available implementations of this interface can be retrieved
 *   by calling {@link com.sap.security.api.umap.system.ISystemLandscapeFactory#getAllLandscapes()}.
 *   <br/>
 *   An implementation can register itself in UME via
 *   {@link com.sap.security.api.umap.system.ISystemLandscapeFactory#registerLandscape(ISystemLandscape)}.
 *   <br/>
 *   Unregistering is possible using
 *   {@link com.sap.security.api.umap.system.ISystemLandscapeFactory#unregisterLandscape(ISystemLandscape)}.
 * </p>
 *
 * <p>The concept knows two naming principles:
 * </p>
 * 
 * <b>Unique keys (GUIDs)</b><br/>
 * <ul>
 * <li>Each system must have exactly one unique key.
 * </li>
 * <li>Unique keys must be unique inside of a system landscape represented by an
 *   <code>ISystemLandscapeWrapper</code> implementation.
 * </li>
 * <li>Unique keys are unique in the sense that two systems are distinct if and
 *   only if their unique keys are distinct.
 * </li>
 * </ul>
 * 
 * <b>System aliases</b><br/>
 * <ul>
 * <li>One system can have an arbitrary number of system aliases (please note
 *   that at least one alias is required to make the system available for user
 *   mapping).
 * </li>
 * <li>Systems can not be distinguished based on system aliases (because
 *   different aliases may belong to the same system).
 * </li>
 * <li>System aliases must be unique inside of a system landscape represented by
 *   an <code>ISystemLandscapeWrapper</code> implementation.
 * </li>
 * </ul>
 *  
 * <p>An application using user mapping will usually call only two methods of this
 *   interface:
 * </p>
 * 
 * <ul>
 *   <li>{@link #getSystemByAlias(String)} to retrieve a system object that can
 *     be passed on to {@link com.sap.security.api.umap.IUserMapping} methods to
 *     retrieve user mapping data for that system, and perhaps</li>
 *   <li>{@link #getAllAliases()} to be able to present a list of system aliases
 *     available.</li>
 * </ul>
 */
public interface ISystemLandscape extends ISystemLandscapeWrapper
{
	public static final String TYPE_ENTERPRISE_PORTAL   = "EnterprisePortal";
	public static final String TYPE_DESTINATION_SERVICE = "DestinationService";
    public static final String TYPE_UME_INTEGRATED      = "UME";

	/**
	 * Determine the unique (!) type identifier of the system landscape wrapper.
	 * 
	 * <p>
	 * The return value should be a rather short string that can be displayed on
	 * the UI as well as saved in configuration data.
	 * </p>
	 * @return the type identifier of the system landscape wrapper
	 */
	public String getType();

	/**
	 * Provide a potentially localized name for the system landscape that can be
	 * displayed on the UI.
	 * 
	 * @param locale The locale for which the display name should be provided.
	 * @return the display name
	 */
	public String getDisplayName(Locale locale);

    /**
     * <p>Returns all aliases for systems known to the underlying system landscape
     *   implementation.
     * </p>
     * 
     * <p>If the underlying system landscape implementation supports more than
     *   one system alias per system, an implementation should return only one
     *   alias per system, if possible, i.e. something like a "default" system
     *   alias. Applications calling this method are usually just interested in
     *   a list of all systems in form of system aliases (i.e. no duplicate
     *   systems) instead of a list of all aliases (containing duplicate systems).
     * </p>
     * 
     * @return Enumeration of system aliases (as <code>String</code>s)
     * @throws ExceptionInImplementationException If the underlying system
     *         landscape implementation throws an exception.
     */
    public Enumeration getAllAliases ()
        throws ExceptionInImplementationException;

    /**
     * Returns all aliases for systems known to this landscape object wrapper
     * for which the specified principal has end-user permissions.
     * 
     * @return Enumeration of system aliases
     * @throws ExceptionInImplementationException If the underlying system
     *         landscape implementation throws an exception.
     */
    public Enumeration getAllAliases(IPrincipal principal)
        throws ExceptionInImplementationException;

    /**
     * Returns a unique key for a given system alias.
     * 
     * @param alias The alias pointing to a system
     * @return GUID that corresponds to this alias
     * @throws ExceptionInImplementationException If the underlying system
     *         landscape implementation throws an exception.
     */
    public String getUniqueKeyForAlias (String alias)
        throws ExceptionInImplementationException;

    /**
     * Returns a list of all unique keys of systems in the underlying
     * system landscape.
     * 
     * @return Enumeration containing all unique keys (as <code>String</code>s)
     * @throws ExceptionInImplementationException If the underlying system
     *         landscape implementation throws an exception.
     */    
    public Enumeration getAllUniqueKeys ()
        throws ExceptionInImplementationException;
    
    /**
     * Retrieves a system landscape object stored under this unique key.
     * 
     * @param uniqueKey The unique key of a system
     * @return ISystemLandscapeObject representing this system
     * @throws ExceptionInImplementationException If the underlying system
     *         landscape implementation throws an exception.
     */
    public ISystemLandscapeObject getSystemByUniqueKey (String uniqueKey)
        throws ExceptionInImplementationException;
    
    /**
     * Retrieves a system landscape object stored under this alias.
     * 
     * @param alias The alias of the system that should be retrieved.
     * @return ISystemLandscapeObject representing this system
     * @throws ExceptionInImplementationException If the underlying system
     *         landscape implementation throws an exception.
     */
    public ISystemLandscapeObject getSystemByAlias (String alias)
        throws ExceptionInImplementationException;

    /**
     * Return the system landscape specific prefix for user mapping data
     * being saved in UME database tables.
     * 
     * <p>
     * This must be the same value that is returned by the
     * {@link ISystemLandscapeObject#getStorageKeyPrefix()} method of <b>all</b>
     * system objects for handled by the system landscape wrapper.
     * </p>
     * 
     * <p>
     * Each system landscape wrapper must have its own unique prefix, which
     * allows to determine the responsible system landscape wrapper from the
     * respective database entry.
     * </p>
     *  
     * <p>This key will be &lt;prefix&gt;:&lt;unique key&gt;. To avoid conflicts
     *   with other {@link ISystemLandscape} implementations, this value
     *   must be unique per <code>ISystemLandscapeWrapper</code> implementation.
     * </p>
     * 
     * @return the prefix for the system key
     */
    public String getStorageKeyPrefix();
}
