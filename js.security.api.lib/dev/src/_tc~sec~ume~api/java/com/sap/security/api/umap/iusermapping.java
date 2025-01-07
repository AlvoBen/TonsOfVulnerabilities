package com.sap.security.api.umap;

import java.util.Map;
import java.util.Set;

import com.sap.security.api.IPrincipal;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.umap.system.ISystemLandscape;
import com.sap.security.api.umap.system.ISystemLandscapeFactory;
import com.sap.security.api.umap.system.ISystemLandscapeObject;

/**
 * <p>Provides an entry point to the user mapping functionality.
 * </p>
 * 
 * <h4>Main user mapping methods:</h4>
 * <p>In order to get user mapping information for a specific principal, call
 *   {@link #getUserMappingData(ISystemLandscapeObject, IPrincipal)}.
 * </p>
 *
 * <p>In order to get the ABAP user ID corresponding to an IUser object, call
 *   {@link #getR3UserName(IUser, ISystemLandscapeObject, boolean)}.
 * </p>
 *
 * <p>In order to make a reverse lookup from a mapped backend user ID to a local
 *   user, call
 *   {@link #getInverseMappingData(String, ISystemLandscapeObject)}.
 * </p>
 *  
 * <h4>Getting system objects for remote systems:</h4>
 *
 * <p>User mapping data is always associated with a remote system object of type
 *   {@link ISystemLandscapeObject}:<br />
 *   <i>"These are my credentials for remote system ABC."</i><br />
 *   The central instance for getting (read) access to system objects is
 *   {@link ISystemLandscapeFactory}. See the corresponding Javadoc for more
 *   information, e.g. about how to retrieve an instance of that interface.
 * </p>
 * 
 * <h4>Example:</h4>
 * 
 * <p>The following examples shows how to get JCO connection properties for a
 *   remote system which is defined in the Enterprise Portal system landscape
 *   and has the default alias "TestSAPBackend".
 * </p>
 * 
 * <pre>
 * import java.util.Properties;
 * 
 * import com.sap.security.api.IUser;
 * import com.sap.security.api.UMFactory;
 * import com.sap.security.api.umap.system.ExceptionInImplementationException;
 * import com.sap.security.api.umap.system.ISystemLandscape;
 * import com.sap.security.api.umap.system.ISystemLandscapeObject;
 * 
 * ...
 * 
 * IUser user = UMFactory.getAuthenticator().getLoggedInUser();
 * 
 * ISystemLandscape portalLandscape =
 *     UMFactory.getSystemLandscapeFactory().getLandscape(ISystemLandscape.TYPE_ENTERPRISE_PORTAL);
 * if(portalLandscape == null) {
 *     throw new Exception("It seems like no Enterprise Portal is installed.");
 * }
 * ISystemLandscapeObject systemObject;
 * try {
 *     systemObject = portalLandscape.getSystemByAlias("TestSAPBackend");
 * }
 * catch(ExceptionInImplementationException e) {
 *     throw new Exception("An error occurred while retrieving the test system object.", e);
 * }
 * if(systemObject == null) {
 *     throw new Exception("Test system does not exist in Enterprise Portal system landscape.");
 * }
 * 
 * IUserMapping userMapping = UMFactory.getUserMapping();
 * IUserMappingData mappingData = userMapping.getUserMappingData(systemObject, user);
 * Properties jcoProperties = new Properties();
 * try {
 *     mappingData.enrich(jcoProperties);
 * }
 * catch(NoLogonDataAvailableException e) {
 *     throw new Exception("No logon data available for test system and the current user.", e);
 * }
 * </pre>
 */
public interface IUserMapping
{
	/**
	 * <p>System attribute name for logon method of a backend system.
     * </p>
     *
     * <p>Potential values:
     * </p>
     * <ul>
     *   <li>{@link com.sap.security.api.logon.ILoginConstants#SSO_JCO_LOGON_METHOD_TICKET}</li>
     *   <li>{@link com.sap.security.api.logon.ILoginConstants#SSO_JCO_LOGON_METHOD_UIDPW}</li>
     *   <li>{@link com.sap.security.api.logon.ILoginConstants#SSO_JCO_LOGON_METHOD_X509CERT}</li>
     * </ul>
     *
     * <p>To be used with
     *   {@link com.sap.security.api.umap.system.ISystemLandscapeObject#getAttribute(String)}.
     * </p>
	 */
    public static final String UMAP_SYSTEMATTRS_LOGONMETHOD="logonmethod";

    /**
     * <p>System attribute for the type of authentication ticket to be used for
     *   connections to a backend system.
     * </p>
     * 
     * <p>Only relevant if the system's logon method is set to
     *   <code>SAPLogonTicket</code>.
     * </p>
     * 
     * <p>Potential values:
     * </p>
     * <ul>
     *   <li>{@link #UMAP_TICKET_TYPE_LOGON}</li>
     *   <li>{@link #UMAP_TICKET_TYPE_ASSERTION}</li>
     * </ul>
     */
    public static final String UMAP_SYSTEMATTRS_TICKET_TYPE="AuthenticationTicketType";
    
    /**
     * Potential value for system attribute
     * {@link #UMAP_SYSTEMATTRS_TICKET_TYPE}: Use SAP Logon Tickets.
     */
    public static final String UMAP_TICKET_TYPE_LOGON      ="SAP Logon Ticket";

    /**
     * Potential value for system attribute
     * {@link #UMAP_SYSTEMATTRS_TICKET_TYPE}: SAP Authentication Assertion
     * Tickets.
     */
    public static final String UMAP_TICKET_TYPE_ASSERTION  ="SAP Assertion Ticket";

    /**
     * @deprecated
     * 
     * <p>System attribute name to flag a backend system as SAP reference system
     *   for UME.
     * </p>
     *
     * <p>This logic is no longer supported. Instead, the SAP reference system
     *   is defined in UME configuration property "ume.r3.mastersystem" (see
     *   {@link com.sap.security.api.logon.ILoginConstants#R3_MASTERSYSTEM}).
     * </p>
     */
    @Deprecated
	public static final String UMAP_SYSTEMATTRS_R3NAMEREF  ="r3referencesystem";

    /**
     * <p>System attribute name for additional user mapping fields that can be
     *   defined for a backend system.
     * </p>
     *
     * <p>To be used with
     *   {@link com.sap.security.api.umap.system.ISystemLandscapeObject#getAttribute(String)}.
     * </p>
     */
    public static final String UMAP_USERMAPPING_FIELDS     ="usermappingfields";

    /**
     * <p>System attribute name for "user mapping type" of a backend system.
     * </p>
     *
     * <p>If the attribute value contains the string <code>"user"</code>, user
     *   mapping data for the system can be defined by the affected end-user. If
     *   the attribute value contains the string <code>"admin"</code>, user
     *   mapping data for the system can be defined by a user administrator.
     * </p>
     *
     * <p>To be used with
     *   {@link com.sap.security.api.umap.system.ISystemLandscapeObject#getAttribute(String)}.
     * </p>
     */
    public static final String UMAP_USERMAPPING_TYPE            = "usermappingtype";
    public static final String UMAP_USERMAPPING_TYPE_ADMIN      = "admin";
    public static final String UMAP_USERMAPPING_TYPE_USER       = "user";
    public static final String UMAP_USERMAPPING_TYPE_ADMIN_USER = "admin,user";

    /**
     * <p>System attribute name for the type of backend system.
     * </p>
     * 
     * <p>This attribute should be filled
     *   at least for SAP ABAP systems and may have values like <code>"SAP_R3"</code>,
     *   <code>"SAP_BW"</code>, <code>"SAP_CRM"</code>.
     * </p>
     *
     * <p>To be used with
     *   {@link com.sap.security.api.umap.system.ISystemLandscapeObject#getAttribute(String)}.
     * </p>
     */
    public static final String UMAP_SYSTEM_TYPE            ="SystemType";

	/**
	 * Internal prefix for Enterprise Portal system IDs in user mapping.
	 */
	public static final String UMAP_EP6_ALIAS_PREFIX       ="ep6_sl_alias";

    /**
     * @deprecated
     * 
     * This constant is no longer valid and there's no replacement.
     */
    @Deprecated
	public static final byte   UMAP_SYSTEM_TYPE_EP6_ALIAS  =1;

	/**
	 * @deprecated
     * 
     * This array of constants should not be used any more.
	 */    
    @Deprecated
	public static final String [] UMAP_SYSTEM_FIELDS =
        new String [] { UMAP_SYSTEMATTRS_LOGONMETHOD,
                        UMAP_SYSTEMATTRS_R3NAMEREF,
                        UMAP_USERMAPPING_FIELDS,
                        UMAP_USERMAPPING_TYPE,
                        UMAP_SYSTEM_TYPE
        };
                        
    /**
     * For internal use.
     */
    public static final String UMAP_ENCRYPTION_KEYTYPE_ALIAS="ume.umap.encryption.keytype";
                            
	/**
	 * For internal use.
	 */
    public static final String UMAP_ENCRYPTION_ALGO_ALIAS= "ume.umap.encryption.algo";
                        
	/**
	 * For internal use.
	 */
    public static final String UMAP_R3_MASTER = "ume.r3.mastersystem";
                        
	/**
	 * For internal use.
	 */
    public static final String UMAP_R3_MASTER_UID_MODE = "ume.r3.mastersystem.uid.mode";

    /**
     * Retrieve an {@link IUserMappingData} object to perform user mapping
     * activities for the specified principal and backend system.
     * 
     * @param system The system object for the backend system.
     *        <br/>
     *        May be <code>null</code> if you only need an authentication ticket
     *        (e.g. SAP logon ticket) for the principal. If there is a valid SAP
     *        reference system defined in UME configuration, <code>null</code>
     *        will be internally substituted by the reference system. Providing
     *        <code>null</code> as backend system is NOT valid if there is no
     *        SAP reference system and you use
     *        {@link IUserMappingData#enrich(Map)} or
     *        {@link IUserMappingData#saveLogonData(Map)}. 
     * @param principal The principal for which user mapping data is requested.
     *        In most cases, this will be an <code>IUser</code> object.
     * @return The user mapping data object that provides access to the actual
     *         mapped logon data.
     */
    public IUserMappingData getUserMappingData (ISystemLandscapeObject system, IPrincipal principal);

    /**
     * <p>
     * Note: This method can only handle user mapping data for systems in the
     * Enterprise Portal system landscape (Portal system) or the UME integrated
     * dummy system landscape (Duet system).
     * </p>
     * 
     * @deprecated Use {@link #getUserMappingData(ISystemLandscapeObject,IPrincipal)} instead.
     */    
    @Deprecated
	public IUserMappingData getUserMappingData (String sysid, IPrincipal  principal, Map sysAttrBag);

    /**
     * <p>
     * Note: This method can only handle user mapping data for systems in the
     * Enterprise Portal system landscape (Portal system) or the UME integrated
     * dummy system landscape (Duet system).
     * </p>
     * 
     * @deprecated Use {@link #getR3UserName(IUser,ISystemLandscapeObject,boolean)} instead.
     */
    @Deprecated
	public String getR3UserName (IUser principal, String sysid, Map sysAttrBag, boolean bGenerateId)
        throws UMException;
        
    /**
     * <p>
     * Note: This method can only handle user mapping data for systems in the
     * Enterprise Portal system landscape (Portal system) or the UME integrated
     * dummy system landscape (Duet system).
     * </p>
     * 
     * @deprecated Use {@link #getInverseMappingData(String, ISystemLandscapeObject)} instead.
     */
    @Deprecated
	public String getInverseMappingData (String sysid, String userid, byte system_type)
        throws NoLogonDataAvailableException, UMException;

    /**
     * <p>Determine the ABAP user ID of the provided user in the specified
     *   backend system.
     * </p>
     *
     * <p>The return value is as follows:
     * </p>
     * 
     * <table border="1">
     *   <tr>
     *     <th>Logon method</th>
     *     <th>Return value</th>
     *   </tr>
     * 
     *   <tr>
     *     <td><code>SAPLogonTicket</code></td>
     *     <td>
     *       <table border="1">
     *         <tr>
     *           <th>Value of <code>ume.r3.mastersystem.uid.mode</code></th>
     *           <th>Return value</th>
     *         </tr>
     *         <tr>
     *           <td>0</td>
     *           <td>the backend user ID if one is maintained, otherwise
     *             <code>null</code></td>
     *         </tr>
     *         <tr>
     *           <td>1</td>
     *           <td>the backend user ID if one is maintained, otherwise the
     *             local logon ID</td>
     *         </tr>
     *         <tr>
     *           <td>2</td>
     *           <td>the backend user ID if one is maintained, otherwise one is
     *             generated and stored in the user mapping (unless
     *             <code>bGenerateId</code> is false, in which case
     *             <code>null</code> is returned).<br/>
     *             <b>Please note that this is currently not implemented and
     *             throws a <code>RuntimeException</code>.</b></td>
     *         </tr>
     *       </table>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td><code>UIDPW</code></td>
     *     <td>the backend user ID if one is maintained, otherwise
     *       <code>null</code></td>
     *   </tr>
     *   <tr>
     *     <td><code>X509CERT</code></td>
     *     <td><code>null</code> (since, in this case, there is no logon ID
     *       passed to the ABAP system as part of the authentication
     *       credentials; instead, the ABAP system has its own mapping from the
     *       X.509 certificate to the local ABAP logon ID)</td>
     *   </tr>
     * </table>
     * 
     * Note: The return value of this method is filtered against a black list
     * of some values that could cause security issues. "SAP*" is not allowed and
     * will result in <code>null</code> being returned.
     * User IDs longer than 12 characters are not allowed on ABAP systems, that's
     * why such mapped IDs will result in <code>null</code>, too.
     * 
     * @param user The local user for which the mapped user ID is requested
     * @param system The backend system for which the mapped user ID is
     *        requested. May be <code>null</code> to express that you need the
     *        mapped user ID for the SAP reference system (i.e. the mapped user
     *        ID that is contained in SAP logon tickets).
     * @param bGenerateId Flag that controls the generation of ABAP user IDs
     *        (<b>currently not implemented, please see explanation above -
     *        please always set to <code>false</code></b>.).
     * @return mapped user ID in the backend system
     * @see #getInverseMappingData(String, ISystemLandscapeObject)
     */  
    public String getR3UserName (IUser user, ISystemLandscapeObject system, boolean bGenerateId)
        throws UMException;

    /**
     * <p>Search for users which are mapped to the given user ID in the
     *   specified backend system.
     * </p>
     * 
     * <p>Even if no mapping is maintained, a non-<code>null</code> value is
     *   returned if the following conditions apply:
     * </p>
     * <ul>
     *   <li>The logon method of the backend system is
     *     <code>SAPLogonTicket</code></li>
     *   <li>The value of the parameter
     *     <code>ume.r3.mastersystem.uid.mode</code> is 1</li>
     *   <li>A user with the specified mapped backend user ID as unique name
     *     exists in UME</li>
     * </ul>
     *  
     * <p>In this case the unique ID of the UME user with logon uid
     *   <code>mappedUser</code> is returned. In other words: Like with
     *   {@link #getR3UserName(IUser,ISystemLandscapeObject,boolean)}, identity
     *   mapping between equal user IDs in the backend system and the local
     *   system should always work (in this case in the opposite direction)
     *   without manual maintenance of user mapping data. If this behaviour is
     *   not convenient, it can be switched off by setting UME property
     *   <code>ume.r3.mastersystem.uid.mode=0</code>.</p>
     * 
     * @param mappedUser The mapped backend user ID the user searched for has
     *        maintained.
     * @param system The backend system to which the mapped user ID applies. If
     *        <code>null</code>, the system identified by UME configuration
     *        property <code>ume.r3.mastersystem</code> is used. If no system
     *        landscape is available (usually if there is no SAP Enterprise
     *        Portal installed), the call behaves as if no user mapping was
     *        available for a system with logon method
     *        <code>SAPLogonTicket</code>.
     * @throws NoLogonDataAvailableException If no user with the provided
     *         backend user ID could be found
     * @throws MultipleHitException If more than one user are mapped to the
     *         specified user ID in the backend system. In order to get the
     *         unique IDs of all matching users, you can use
     *         {@link MultipleHitException#getUserNames()}.
     * @throws UMException If some internal operation fails unexpectedly.
     * @return unique ID of the (single) user that has maintained the specified
     *         backend user ID for the provided system. To retrieve the
     *         corresponding IUser object, call
     *         {@link com.sap.security.api.IUserFactory#getUser(String)}
     * @see #getR3UserName(IUser, ISystemLandscapeObject, boolean)
     */
    public String getInverseMappingData (String mappedUser, ISystemLandscapeObject system)
        throws NoLogonDataAvailableException, MultipleHitException, UMException;
    
    /**
     * <p>Optimized batch processing version of
     *   {@link #getInverseMappingData(String,ISystemLandscapeObject)} for a
     *   whole set of backend user IDs.
     * </p>
     * 
     * <p>The logic is identical except that exceptions thrown while processing
     *   a single (inverse) user mapping - i.e. which don't affect the whole
     *   call - are not rethrown, but only logged. The intention is to provide
     *   robustness of this mass call against problems that only affect single
     *   users / single user mapping entries.
     * </p> 
     * 
     * @param mappedUsers Array of the mapped backend user IDs
     * @param system The backend system for which to search for matching users.
     *        May be <code>null</code>, see
     *        {@link #getInverseMappingData(String,ISystemLandscapeObject)}.
     * @return Result map. Each entry contains the mapped user ID (i.e. a single
     *         entry of mappedUsers[]) as key and the corresponding
     *         {@link com.sap.security.api.IUser} object (or <code>null</code>
     *         if no such mapping exists) as value.
     * @throws UMException If an unexpected problem occurs
     */
    public Map getInverseMappingData (String [] mappedUsers, ISystemLandscapeObject system)
        throws UMException;
    
	/**
	 * Check whether user mapping data for the specified system and principal
     * exists.
     * 
	 * @param system The system for which to search for user mapping data. Must
     *        not be <code>null</code>.
	 * @param principal The principal for which to search for user mapping data.
	 * @return <code>true</code> if a mapping for the specified system and
     *         principal was found, <code>false</code> if no mapping was found.
	 * @throws UMException If some unexpected problem occurs
	 */
	public boolean existsMappingData(ISystemLandscapeObject system,
		IPrincipal principal) throws UMException;

	/**
     * <p>
     * Note: This method can only handle user mapping data for systems in the
     * Enterprise Portal system landscape (Portal system) or the UME integrated
     * dummy system landscape (Duet system).
     * </p>
     * 
	 * @param principal The principal for which to check for user mapping data
	 * @return Set containing IDs (as <code>String</code>s) of the systems for
     *         which user mapping data has been found.
	 * @throws UMException If an unexpected problem occurs
	 * @deprecated Call {@link #existsMappingData(ISystemLandscapeObject, IPrincipal)}
	 *         for every relevant system.
	 */
	@Deprecated
	public Set getMappedSystemsForPrincipal(IPrincipal principal) throws UMException;

	/**
	 * <p>Check whether the configuration of cryptography usage in user mapping
     *   is valid for the given system.
     * </p>
     *
	 * <p>Background: The strongly encrypted storage method for user mapping
     *   data requires the availability of some extra software packages (SAP
     *   Java Crypto Toolkit and JDK specific JCE policy files for unlimited
     *   strength encryption). When user mapping is configured to use strong
     *   encryption, but at least one of these additional packages are missing,
     *   most user mapping operations will fail.
     * </p>
     * 
	 * <p>Please note that it's possible to configure user mapping in a way to
     *   store the special mapping data for the SAP reference system in an LDAP
     *   directory (i.e. without encryption, see
     *   {@link IUserMappingData#USER_MAPPING_REFSYS_ATTRIBUTE}). As encryption
     *   configuration is irrelevant for that kind of user mapping data, you
     *   need to provide the backend system as argument to allow distinction
     *   between systems which rely on correct encryption configuration and
     *   systems that don't.
     * </p>
     * 
	 * @param systemObject The system for which you would like to read or
     *        write user mapping data.
	 * @return <code>true</code> if user mapping data for the passed system can
	 *         be handled.<br/>
	 *         <code>false</code> if the user mapping configuration does not
     *         match the (non-)existence of the additional encryption software,
     *         but user mapping operations for the passed system would require
     *         them and fail.
	 */
	public boolean checkCryptoConfiguration(ISystemLandscapeObject systemObject);

	/**
     * <p>
     * Note: This method can only handle user mapping data for systems in the
     * Enterprise Portal system landscape (Portal system) or the UME integrated
     * dummy system landscape (Duet system).
     * </p>
     * 
	 * @deprecated Use {@link #checkCryptoConfiguration(ISystemLandscapeObject)} instead.
	 */
	@Deprecated
	public boolean checkCryptoConfiguration(String systemId);

    /**
     * <p>For internal use only.
     * </p>
     * 
     * <p>Retrieve an array of all user mapping converters (no matter whether
     *   they could be applied or not, so check
     *   {@link IUserMappingConverter#isConversionPossible()} before trying to
     *   start any conversion).
     * </p>
     * 
     * @return Array of available user mapping converters.
     */
	public IUserMappingConverter[] getAvailableConverters();

	/**
	 * Get the system ID for the currently configured ABAP reference system.
	 * 
	 * <p>
	 * The system ID consists of the system landscape type
	 * (see {@link ISystemLandscape#getType()})
	 * followed by the separator character ':' followed by the system alias
	 * (see {@link ISystemLandscapeObject#getAlias()}.
	 * </p>
	 * 
	 * @return The reference system ID or <code>null</code> if the reference
	 *         system is not configured.
	 */
	public String getReferenceSystemID();

}
