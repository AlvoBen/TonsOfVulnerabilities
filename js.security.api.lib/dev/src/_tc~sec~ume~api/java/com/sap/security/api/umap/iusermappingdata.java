package com.sap.security.api.umap;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Properties;
import java.io.IOException;

import javax.xml.soap.SOAPMessage;

import com.sap.security.api.IPrincipal;
import com.sap.security.api.UMException;


/**
 * <p>Main interface for a user mapping entry (= a mapping from a specific local
 *   principal to a specific user in a specific backend system).
 * </p>
 *
 * <p>Using this interface, components can retrieve logon credentials for their
 *   backend connections in two ways:
 * </p>
 * <ul>
 *   <li>
 *     add logon credentials to a JCo properties object
 *     ({@link #enrich(Properties)}), a SOAP message
 *     ({@link #enrich(SOAPMessage)}) or an HTTP request
 *     ({@link #enrich(HttpURLConnection)}). The actual type of credentials
 *     (e.g. user ID and password, SAP logon ticket, ...) depends on the logon
 *     method that is configured for the backend system.
 *   </li>
 *   <li>
 *     retrieve all logon data that has been saved for the selected principal
 *     and backend system (as cleartext, i.e. as it has been saved). This may be
 *     relevant if, for some reason, you don't want to retrieve logon method
 *     specific credentials. See {@link #enrich(Map)}.
 *   </li> 
 * </ul>
 * 
 * <p>If you're interested in the SAP logon ticket of a user and the backend
 *   system is either <code>null</code>, the SAP reference system or another
 *   system configured to use logon method "SAPLogonTicket", you can use the
 *   following code:
 * </p>
 * 
 * <pre>
 * IUserMappingData mappingData = ...;
 * Properties jcoProperties = new Properties();
 * mappingData.enrich(jcoProperties);
 * String logonTicket = jcoProperties.getProperty(IUserMappingData.UMAP_JCO_PASSWORD);
 * </pre> 
 * 
 * <p>Besides the different flavours of <code>enrich()</code>, this interface
 *   provides some additional methods for maintenance of user mapping data.
 * </p>
 */
public interface IUserMappingData
{
    /**
     * Namespace used to store user mapping data in UME tables.
     */ 
    public static final String USER_MAPPING_NAMESPACE = "$usermapping$";
    
    /**
     * Namespace used to store inverse user mapping data in UME tables.
     */ 
    public static final String USER_MAPPING_INVERSE_NAMESPACE = "$inverse_usermapping$";
    
    /**
     * <p>Logical attribute name used to store a user mapping for a SAP
     *   reference system for a user as plaintext.
     * </p>
     * 
     * <p><b>Use case:</b> UME persistence and ABAP LDAP sync use the same LDAP
     *   server. One attribute contains the ABAP user ID which is different from
     *   the UME logon ID. By assigning the physical LDAP attribute to this
     *   logical attribute in UME datasource configuration, UME retrieves the
     *   mapped ABAP user ID for SAP logon tickets from this attribute. This
     *   way, there is no need to manually maintain user mapping data for all
     *   users that exist in both technology stacks.
     * </p>
     */
    public static final String USER_MAPPING_REFSYS_ATTRIBUTE = "REFERENCE_SYSTEM_USER";
    
    /**
     * <p>Key for the mapped backend user ID in the <code>Properties</code>
     *   object enriched by {@link #enrich(Properties)}.
     * </p>
     */    
    public static final String UMAP_JCO_USER     = "jco.client.user";

    /**
     * <p>Key for the mapped backend password (or equivalent) in the
     *   <code>Properties</code> object enriched by {@link #enrich(Properties)}.
     * </p>
     */    
    public static final String UMAP_JCO_PASSWORD = "jco.client.passwd";

    /**
     * <p>Key for the mapped backend user ID in the <code>Map</code> object
     *   enriched by {@link #enrich(Map)}.
     * </p>
     */    
    public static final String UMAP_USER     = "user";

    /**
     * <p>Key for the mapped backend user password in the <code>Map</code>
     *   object enriched by {@link #enrich(Map)}.
     * </p>
     */    
    public static final String UMAP_PASSWORD = "mappedpassword";

    /**
     * <p>Enrich a <code>Map</code> with logon credentials.
     * </p>
     * 
     * <p>Adds the following keys and the respective values (if available)
     *   to the <code>Map</code>: {@link #UMAP_USER} and {@link #UMAP_PASSWORD}.
     *   There may be additional key/value pairs in the <code>Map</code>,
     *   depending on the availability of additional user mapping data fields
     *   defined in the system definition (system attribute
     *   {@link IUserMapping#UMAP_USERMAPPING_FIELDS}) and the availability of
     *   respective values in the user mapping entry.
     * </p>
     * 
     * @param logonData A <code>Map</code> that may already contain some logon
     *        data. <code>enrich()</code> will add the available user mapping
     *        data for the system.
     * @throws NoLogonDataAvailableException If there is no logon data
     *         available that matches the system's logon method
     */
    public void enrich (Map logonData)
        throws NoLogonDataAvailableException;

    /**
     * <p>Enriches an <code>HttpURLConnection</code> with logon credentials.
     * </p>
     * 
     * <p>Depending on the logon method this either adds the SAP logon ticket
     *   of the (authenticated!) user or basic authentication data based on the
     *   contents of this user mapping entry to the request.
     * </p>
     * 
     * @param conn The connection object to be enriched.
     * @throws NoLogonDataAvailableException If there is no logon data available
     *         that matches the system's logon method
     */
    public void enrich (HttpURLConnection conn)
        throws NoLogonDataAvailableException;

    /**
     * <p>Enriches a JCo <code>Properties</code> object (containing information
     *   used to open an RFC connection to an ABAP backend system) with logon
     *   credentials.
     * </p>
     * 
     * <p>Depending on the supported logon method of the backend system, this
     *   method adds authentication credentials of the required type to the
     *   <code>Properties</code> object. The object is meant to be passed to JCo
     *   without modification.
     * </p>
     * 
     * <p>The <code>Properties</code> object may be enriched e.g. with user
     *   ID/password, SAP logon ticket or client certificate. Usual keys used in
     *   the <code>Properties</code> object are {@link #UMAP_JCO_USER} and
     *   {@link #UMAP_JCO_PASSWORD}. Please note that these are only
     *   <b>potential</b> keys: They don't necessarily exist after enrich(), and
     *   there may be other keys that can be interpreted by JCo/RFC resp. the
     *   backend system.
     * </p>
     * 
     * @param jcoProps A JCo <code>Properties</code> object to be enriched with
     *        authentication credentials
     * @throws NoLogonDataAvailableException If there is no logon data available
     *         that matches the system's logon method
     */
    public void enrich (Properties jcoProps)
        throws NoLogonDataAvailableException;
    
    /**
     * <p>Enriches a SOAP message with logon credentials.
     * </p>
     *
     * <p>This is achieved by adding either a SAP logon ticket or a basic
     *   authentication header (for logon method <code>UIDPW</code>; only
     *   supported in conjunction with SAP Enterprise Portal) to the HTTP
     *   headers of the underlying HTTP connection assigned to the SOAP message.
     * </p>
     * 
     * @param message The SOAP message object to be enriched
     * @throws NoLogonDataAvailableException If there is no logon data available
     *         that matches the system's logon method
     */
    public void enrich (SOAPMessage message)
        throws NoLogonDataAvailableException;

    /**
     * Returns the principal (usually an IUser) this object was created for.
     */
    public IPrincipal getPrincipal ();

    /**
     * Returns the system ID this object was created for (in an Enterprise
     * Portal environment: the system alias).
     */
    public String getSystemId ();

    /**
     * @deprecated Please use {@link #saveLogonData(Map)} instead which supports
     *             more differentiated exception handling.
     * 
     * <p>Store logon data for a specific user and backend system.
     * </p>
     * 
     * <p>Please consider calling {@link #isReadOnly()} before to make sure this
     *   user mapping entry can be (over)written.
     * </p>
     * 
     * @param logonData The logon data to store. Should contain at least values
     *        for keys {@link #UMAP_USER} and {@link #UMAP_PASSWORD}.
     * @throws IOException If an error occurs while storing the logon data.
     */
    @Deprecated
	public void storeLogonData (Map logonData) throws IOException;

    /**
     * <p>Store logon data for a specific user and backend system.
     * </p>
     * 
     * <p>Please consider calling {@link #isReadOnly()} before to make sure this
     *   user mapping entry can be (over)written.
     * </p>
     * 
     * @param logonData The logon data to store. Should contain at least values
     *        for keys {@link #UMAP_USER} and {@link #UMAP_PASSWORD}.
     * @throws UMException If an error occurs while storing the logon data.<br/>
     *         If the exception has the subtype
     *         {@link com.sap.security.api.AuthenticationFailedException},
     *         the error did not occur while actually saving, but when verifying
     *         the logon data against the backend system. Usually that is the
     *         result of incorrect logon data or errors in the system connection
     *         data which prevent opening a connection to the system.
     */
    public void saveLogonData(Map logonData) throws UMException;

    /**
     * Indicates whether the mapping found has been maintained for the principal
     * itself or for one of its parent principals, e.g. groups or roles. To
     * determine the principal from which the mapping is "inherited", you can
     * use {@link #getSourceOfIndirectMapping()}.
     *
     * @return <code>true</code> if the mapping found has been maintained for
     *         the principal itself. <code>false</code> if the mapping has been
     *         "inherited" from a parent principal.
     */        
    public boolean isMappingDirect ();
    
    /**
     * <p>Determines whether the user mapping data represented by this object
     *   can be altered (store new data, delete existing data).
     * </p>
     *
     * <p>If you want to call {@link #saveLogonData(Map)}, you can call this
     *   method first to check whether storing will be able to succeed.
     * </p>
     *
     * <p>Background: Depending on the datasource where user mapping data is
     *   stored, changing mapping data will not be possible (read-only
     *   datasource).
     * </p>
     * 
     * @return <code>true</code> if this user mapping data can only be read;
     *         <code>false</code> if it can be altered, too.
     */
	public boolean isReadOnly();
    
    /**
     * In case of an indirect mapping ({@link #isMappingDirect()} returns
     * <code>true</code>): Determine the principal from which the mapping is
     * "inherited".
     * 
     * @return The principal from which the mapping is "inherited" or
     * <code>null</code> if
     * <ol>
     *   <li>this is a direct mapping</li>
     *   <li>there is no mapping at all for this principal, neither a direct nor
     *     an indirect one</li>
     * </ol>
     */
    public IPrincipal getSourceOfIndirectMapping();
}
