package com.sap.security.api.umap.system;

import java.util.Enumeration;

/**
 * <p>Wrapper object for a backend system provided by an
 *   {@link ISystemLandscapeWrapper}.
 * </p>
 * 
 * <p>An <code>ISystemLandscapeObject</code> may represent an arbitrary backend
 *   system, e.g. different SAP systems, HTTP systems or any other type that may
 *   be relevant for user mapping. In general, all systems are treated equally
 *   in that UME simply manages credentials (user mapping data) for them, but
 *   does not try to open connections to them. That's why UME does not need to
 *   know how to contact backends systems.
 * </p>
 * 
 * <p><b>There is one exception from the above rule</b>: UME knows the concept of
 *   the so-called "SAP reference system", which may only be an SAP system. If a
 *   user has mapped himself to a user in the SAP reference system, that backend
 *   user ID is used in SAP authentication tickets (e.g. logon tickets) issued
 *   by the local server. As the mapped password is never used during ticket
 *   issuing, it must be verified while saving user mapping data (to avoid that
 *   a user could get a ticket issued for another backend user simply by mapping
 *   himself to a backend user that is not her own).
 * </p>
 * 
 * <p>For the verification of user mapping credentials against the backend system,
 *   UME opens a JCO connection using the entered credentials. Obviously, it needs
 *   valid JCO connection properties for that, so, for the SAP reference system,
 *   an implementation must be able to provide all JCO connection properties as
 *   system attributes as required by the JCO release currently used in the local
 *   server. For details, see method {@link #getAttribute(String)}.
 * </p>
 */
public interface ISystemLandscapeObject
{
    /**
     * <p>Retrieve the unique key (GUID) of this system.
     * </p>
     * 
     * <p>In combination with the storage key prefix
     *   (see {@link #getStorageKeyPrefix()}), the unique key is used as
     *   identifier for a system in the user mapping database tables.
     * </p>
     *
     * @return unique key as String
     */
    public String   getUniqueKey ();
    
    /**
     * <p>Retrieve the logon method configured for this system.
     * </p>
     * 
     * <p>The result must be the value of system attribute
     *   {@link com.sap.security.api.umap.IUserMapping#UMAP_SYSTEMATTRS_LOGONMETHOD}.
     *   For details, see method {@link #getAttribute(String)}.
     * </p>
     * 
     * @return returns the logon method or <code>null</code> if logon issues are
     *     not relevant for this system
     */
    public String   getLogonMethod ();
    
    /**
     * <p>Retrieve the value of a system attribute.
     * </p>
     * 
     * <p>The following attributes are required by user mapping itself. The value
     *   data type for these attributes is <code>String</code>.
     * </p>
     * 
     * <table border="1" style="margin-top:1.5ex; margin-bottom:1.5ex;">
     *   <tr>
     *     <th>Status (SAP systems / other systems)</th>
     *     <th>Attribute name</th>
     *     <th>Range</th>
     *   </tr>
     *   <tr>
     *     <th colspan="3">Description</th>
     *   </tr>
     *   <tr>
     *     <td>Mand./Mand.</th>
     *     <td>{@link com.sap.security.api.umap.IUserMapping#UMAP_USERMAPPING_TYPE}</td>
     *     <td>"<code>user</code>",
     *         "<code>admin</code>",
     *         "<code>admin,user</code>"
     *     </td>
     *   </tr>
     *   <tr>
     *     <td colspan="3">Determines whether only (user) administrators, only
     *       end-users or both may edit user mapping data for the system.
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>Mand./Opt.</th>
     *     <td>{@link com.sap.security.api.umap.IUserMapping#UMAP_SYSTEMATTRS_LOGONMETHOD}</td>
     *     <td>{@link com.sap.security.api.logon.ILoginConstants#SSO_JCO_LOGON_METHOD_UIDPW},
     *         {@link com.sap.security.api.logon.ILoginConstants#SSO_JCO_LOGON_METHOD_TICKET},
     *         {@link com.sap.security.api.logon.ILoginConstants#SSO_JCO_LOGON_METHOD_X509CERT}
     *     </td>
     *   </tr>
     *   <tr>
     *     <td colspan="3">The logon method (type of authentication credentials)
     *       to use for connections to the backend system.
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>Mand./NA</th>
     *     <td>{@link com.sap.security.api.umap.IUserMapping#UMAP_SYSTEM_TYPE}</td>
     *     <td>"<code>SAP_R3</code>",
     *         "<code>SAP_BW</code>",
     *         "<code>SAP_CRM</code>"
     *     </td>
     *   </tr>
     *   <tr>
     *     <td colspan="3">Type of SAP system.</td>
     *   </tr>
     *   <tr>
     *     <td>Opt./Opt.</th>
     *     <td>{@link com.sap.security.api.umap.IUserMapping#UMAP_USERMAPPING_FIELDS}</td>
     *     <td>A string following the rules documented in
     *       <a href="http://help.sap.com/saphelp_nw04/helpdata/en/0d/fd76a0c4e0834ba1a17698d0b5553d/content.htm"
     *          target="_blank">System Properties for User Mapping
     *       </a>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td colspan="3">Definition of additional user mapping fields.
     *       <br/>
     *       By default, a user mapping entry only consists of a mapped user ID
     *       and a mapped password. Using this attribute, it can be extended to
     *       an arbitrary number of additional fields (e.g. the logon language
     *       for SAP systems).
     *     </td>
     *   </tr>
     * </table>
     * 
     * <p>(Opt. = Optional; Mand. = Mandatory; NA = Not applicable)</p>
     * 
     * <p>Additionally, at least for the SAP reference system, an implementation
     *   must be able to provide JCO connection properties as system attributes.
     *   The property key is used as system attribute name, the property value is
     *   the attribute value.
     * </p>
     * 
     * <p><b>It is in the responsibility of an implementation</b> to be able to
     *   provide the JCO properties by attribute names and as data types as
     *   required by the current JCO release. UME does not use these values
     *   directly, but passes them to JCO when opening connetions to backend
     *   systems to verify user mapping credentials.
     * </p>
     * 
     * <p>As a first hint: At the time when this documentation has been written,
     *   the following JCO properties were supported by the system landscape
     *   wrapper / object implementation for SAP Enterprise Portal's system
     *   landscape:
     * </p>
     * 
     * <code>
     * <ul>
     *   <li>"ashost"</li>
     *   <li>"sysnr"</li>
     *   <li>"client"</li>
     *   <li>"ServerPort"</li>
     *   <li>"msserv"</li>
     *   <li>"gwserv"</li>
     *   <li>"gwhost"</li>
     *   <li>"mshost"</li>
     *   <li>"group"</li>
     *   <li>"ConnectionString"</li>
     *   <li>"r3name"</li>
     *   <li>"SAPApplicationR3name"</li>
     *   <li>"SystemAlias"</li>
     *   <li>"snc_lib"</li>
     *   <li>"snc_myname"</li>
     *   <li>"snc_qop"</li>
     *   <li>"snc_mode"</li>
     *   <li>"snc_partner_name"</li>
     *   <li>"tphost"</li>
     *   <li>"tpname"</li>
     *   <li>"trace"</li>
     *   <li>"lang"</li>
     * </ul>
     * </code>
     * 
     * @param attributeName The name of the requested attribute.
     * @return The attribute value as <code>Object</code>. Usually, this is a
     *         <code>String</code>, but this is not guaranteed.
     */
    public Object   getAttribute(String attributeName);

    /**
     * Retrieve a plain text description for this system.
     * 
     * @return Plain text system description.
     */    
    public String   getSystemDescription ();
    
    /**
     * Retrieve a printable name for this system (e.g. for select lists).
     * 
     * @return Printable name for the system.
     */
    public String   getPrintableName ();
    
    /**
     * Return the implementation of the {@link ISystemLandscapeWrapper} that
     * is responsible for this system. Can be used to be able to distinguish
     * system objects that come from different system landscapes.
     * 
     * @return <code>Class</code> of the system landscape wrapper.
     */
    public Class    getLandscapeClass ();
    
    /**
     * Return the system landscape specific prefix for user mapping data
     * being saved in UME database tables.
     *  
     * <p>
     * This must be the same value that is returned by the
     * {@link ISystemLandscape#getStorageKeyPrefix()} method of the
     * system landscape wrapper which is responsible for the system object.
     * That means this prefix must be identical for all system objects
     * handled by the same system landscape wrapper. The recommended
     * implementation of this method is a delegated call to
     * {@link ISystemLandscape#getStorageKeyPrefix()}.
     * </p>
     * 
     * @return the prefix for the unique key in user mapping database entries
     */
    public String   getStorageKeyPrefix ();
    
    /**
     * Retrieve the names of all available system attributes.
     * 
     * @return <code>Enumeration</code> of attribute names (as
     *         <code>String</code>s)
     */
    public Enumeration getAttributeNames ();
    
    /**
     * Retrieve the alias of this system object.
     * 
     * @return the system alias
     */
    public String getAlias ();
}
