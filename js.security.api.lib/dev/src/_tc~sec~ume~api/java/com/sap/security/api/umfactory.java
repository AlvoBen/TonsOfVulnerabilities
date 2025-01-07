package com.sap.security.api;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.security.api.acl.IAclManager;
import com.sap.security.api.acl.IAclManagerFactory;
import com.sap.security.api.logon.IAnonymousUserFactory;
import com.sap.security.api.logon.ILogonAuthentication;
import com.sap.security.api.logon.ISecurityPolicyFactory;
import com.sap.security.api.srvUser.IServiceUserFactory;
import com.sap.security.api.ticket.TicketVerifier;
import com.sap.security.api.umap.IUserMapping;
import com.sap.security.api.umap.system.ISystemLandscape;
import com.sap.security.api.umap.system.ISystemLandscapeFactory;
import com.sap.security.api.umap.system.ISystemLandscapeWrapper;
import com.sap.security.api.umap.system.WrappingSystemLandscape;
import com.sap.security.api.util.IUMFileIO;
import com.sap.security.api.util.IUMParameters;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

// TODO Remove IEngineResourceHelper (-> replace by IPlatformTools)
// TODO Do we need re-initialization??
// TODO Allow (partly) different configuration per client?

/**
 * Main factory providing access to all UME functionality.
 * 
 * <h4>How to access UME functionality</h4>
 * 
 * <p>
 *   Applications can access UME functionality via a set of public UME factories
 *   like {@link com.sap.security.api.IUserFactory}, {@link com.sap.security.api.IAuthentication}
 *   and {@link com.sap.security.api.umap.IUserMapping}.
 * </p>
 * 
 * <p>
 *   UME factories can be distinguished by their behaviour regarding database
 *   transactions. There are factories that simply account for UME internal
 *   transactions, which is the default behaviour. And there may also be factories
 *   that participate in container wide shared transactions (if supported by the
 *   current runtime environment, e.g. SAP Application Server Java).
 * </p>
 * 
 * <p>
 *   There are two ways to get access to UME factories:
 * </p>
 * 
 * <ol>
 *   <li>
 *     The <code>UMFactory</code> class provides functionality to get access to
 *     UME factory objects. Applications access all the functionality through
 *     this class and its (mostly static) factory getter methods like
 *     {@link #getUserFactory()}, {@link #getAuthenticator()} and {@link #getUserMapping()}.
 *     <br/>
 *     <b>Note:</b> <code>UMFactory</code>'s getter methods always return UME
 *     factories that <b>don't</b> participate in shared transactions.
 *   </li>
 * 
 *   <li>
 *     <p>
 *       In scenarios where a JNDI naming system exists inside of the server, UME
 *       factories can also be retrieved by a JNDI lookup. This is the only way to
 *       get access to UME factories that participate in shared transactions.
 *     </p>
 * 
 *     <p>
 *       Looking up UME factories from JNDI requires declaration of all necessary
 *       factories in the application's deployment descriptor. In SAP Application Server Java,
 *       this requires adding a <code>server-component-ref</code> node to the
 *       web-j2ee-engine.xml / ejb-j2ee-engine.xml / appclient-j2ee-engine.xml
 *       like this
 *       (see <a href="http://help.sap.com/saphelp_erp2004/helpdata/en/d1/84fd48edeb41d0bb69d2689071f4cf/content.htm">Documentation of web-j2ee-engine.dtd</a>):
 *     </p>
 *     
 *     <pre>
 *     &lt;server-component-ref&gt;
 *       &lt;name&gt;
 *         ume/userfactory
 *       &lt;/name&gt;
 *       &lt;type&gt;
 *         interface
 *       &lt;/type&gt;
 *       &lt;jndi-name&gt;
 *         UME/unsharable/com.sap.security.api.IUserFactory
 *       &lt;/jndi-name&gt;
 *     &lt;/server-component-ref&gt;
 *     </pre>
 *     
 *     <p>
 *       In this example, the application would call
 *       <code>lookup("java:comp/env/ume/userfactory")</code> to retrieve an
 *       instance of {@link com.sap.security.api.IUserFactory} (which does not
 *       participate in shared transactions, see below).
 *     </p>
 *     
 *     <p>
 *       Some UME factories are available in two different subcontexts of the main
 *       UME context </code>"UME"</code>:
 *       <ul>
 *         <li>
 *           Factories in context <code>"sharable"</code> participate in shared
 *           database transactions (if the container supports shared transactions
 *           and the actual factory is subject to transactions at all).
 *         </li>
 *         <li>
 *           Factories in context <code>"unsharable"</code> ignore shared transactions
 *           or are not subject to transactions at all.
 *         </li>
 *       </ul>
 *     </p>
 * 
 *     <p>
 *       Each factory object is bound into the UME subcontexts by the fully qualified
 *       name of the corresponding UME interface:
 *     </p>
 *     
 *     <pre>
 *     UME/sharable/com.sap.security.api.IGroupFactory
 *     UME/sharable/com.sap.security.api.IPrincipalFactory
 *     UME/sharable/com.sap.security.api.IRoleFactory
 *     UME/sharable/com.sap.security.api.IUserAccountFactory
 *     UME/sharable/com.sap.security.api.IUserFactory
 *     UME/sharable/com.sap.security.api.acl.IAclManagerFactory
 *     UME/sharable/com.sap.security.api.srvUser.IServiceUserFactory
 *     
 *     UME/unsharable/com.sap.security.api.IGroupFactory
 *     UME/unsharable/com.sap.security.api.IPrincipalFactory
 *     UME/unsharable/com.sap.security.api.IRoleFactory
 *     UME/unsharable/com.sap.security.api.IUserAccountFactory
 *     UME/unsharable/com.sap.security.api.IUserFactory
 *     UME/unsharable/com.sap.security.api.acl.IAclManagerFactory
 *     UME/unsharable/com.sap.security.api.logon.IAnonymousUserFactory
 *     UME/unsharable/com.sap.security.api.logon.IAuthentication
 *     UME/unsharable/com.sap.security.api.logon.ILogonAuthentication
 *     UME/unsharable/com.sap.security.api.logon.ISecurityPolicyFactory
 *     UME/unsharable/com.sap.security.api.srvUser.IServiceUserFactory
 *     UME/unsharable/com.sap.security.api.umap.IUserMapping
 *     UME/unsharable/com.sap.security.api.umap.system.ISystemLandscapeFactory
 *     </pre>
 *   </li>
 * </ol>
 *
 * <h4>Internal: How to initialize UME functionality</h4>
 *
 * There are several ways to initialize UME <code>UMFactory</code>
 * <ul>
 *   <li>
 *     A platform specific service calls 
 *     com.sap.security.core.InternalUMFactory.initializeUME(IUMFileIO, IPlatformTools)
 *     which in turn calls {@link #initialize(Map)} to initialize UMFactory. This is the
 *     standard way which is already used in SAP Application Server Java and SAP JTS.
 *   </li>
 *   <li>
 *     If UME runs standalone (i.e. not as part of an SAP server system, but in a
 *     custom Java application, the application may either call
 *     {@link #initialize(String)} with a directory in the file system which contains
 *     all required UME configuration data.
 *   </li>
 *   <li>
 *     If only UME functionality for verifying SAP logon tickets (but no other UME
 *     functionality) is required in a standalone scenario, see the documentation of
 *     class {@link com.sap.security.api.ticket.TicketVerifier}.
 *   </li>
 * </ul>
 */

public class UMFactory {

	private static Location loc = Location.getLocation(UMFactory.class);
	
	public static final String VERSIONSTRING =
		"$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/UMFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

    // System property with UME configuration directory (for standalone scenario)
    private static final String SYS_PROP_UME_CFG_DIR = "ume.cfg.path";

    // Method signatures (for tracing)
    private static final String M_CHECK_INITIALIZED   = "checkInitialized(Object, String)";
    private static final String M_GET_INSTANCE        = "getInstance()";
    private static final String M_GET_TICKET_VERIFIER = "getTicketVerifier()";
    private static final String M_HANDLE_MULTI_INIT   = "handleMultipleInit";
    private static final String M_INIT_MAP            = "initialize(Map)";
    private static final String M_INIT_S              = "initialize(String)";

    // Initialization Map key to signal that isInitialized() should return "true" now
    private static final String INIT_COMPLETE = "InitializationComplete";
    
    // Internal status regarding initialization
    private static final int STATE_INITIAL       = 0;
    private static final int STATE_INIT_RUNNING  = 1;
    private static final int STATE_INIT_COMPLETE = 2;

    // Internal state - Global
    private static int             _initState       = STATE_INITIAL;
    private static UMFactory       _instance        = null;
    private static SecurityManager _securitymanager = null;

    // UME factories:
    // Single factories per type:
    private static IAnonymousUserFactory  _anonymousUserFactory  = null;
    private static IAuthentication        _authFactory           = null;
    private static IGroupFactory          _groupFactory          = null;
    private static ILogonAuthentication   _logonAuthFactory      = null;
    private static IPrincipalFactory      _principalFactory      = null;
    private static IRoleFactory           _roleFactory           = null;
    private static ISecurityPolicyFactory _securityPolicyFactory = null;
    private static IServiceUserFactory    _serviceUserFactory    = null;
    private static IUserAccountFactory    _userAccountFactory    = null;
    private static IUserFactory           _userFactory           = null;
    private static IUserMapping           _userMapping           = null;
    private static IAclManagerFactory     _aclManagerFactory     = null;
    // Multiple factories per type:
    private static Class     _ticketVerifierClass     = null;

    // Configuration (currently client-independent)
	private static IUMParameters _configuration = null;
    private static IUMFileIO     _umeFileIO     = null;

    // Other
    private static ISystemLandscapeFactory _systemLandscapeFactory;
    private static Map<ISystemLandscapeWrapper, ISystemLandscape> _oldLandscapeWrappers
    	= new HashMap<ISystemLandscapeWrapper, ISystemLandscape>();

    // ############################  Constructors  #############################
    
    /**
     * Default constructor must not be used (from outside)
     */
    private UMFactory() { /* Not used. */ }

    // ###################  Static Initialization Methods  #####################

    /**
     * FOR INTERNAL USE ONLY: Initialize UME.
     * This method may be called once with all required factories or several
     * times with one or more factories (if initialization can't be performed
     * in one single step). {@link #isInitialized()} will not return <code>true</code>
     * until the key <code>"InitializationComplete"</code> is contained in the
     * argument <code>Map</code> (see the table of keys and values below).
     * 
     * @param factories Map of UME factory objects.
     *        The following key / value pairs are currently required:
     * <table border="1">
     *   <tr>
     *     <th>Key</th>
     *     <th>Value</th>
     *   </tr>
     *   <tr>
     *     <td>IAclManager.class</td>
     *     <td>Class object of the IAclManager implementation class</td>
     *   </tr>
     *   <tr>
     *     <td>IAnonymousUserFactory.class</td>
     *     <td>Instance of IAnonymousUserFactory</td>
     *   </tr>
     *   <tr>
     *     <td>IAuthentication.class</td>
     *     <td>Instance of IAuthentication</td>
     *   </tr>
     *   <tr>
     *     <td>IGroupFactory.class</td>
     *     <td>Instance of IGroupFactory</td>
     *   </tr>
     *   <tr>
     *     <td>ILogonAuthentication.class</td>
     *     <td>Instance of ILogonAuthentication</td>
     *   </tr>
     *   <tr>
     *     <td>IPrincipalFactory.class</td>
     *     <td>Instance of IPrincipalFactory</td>
     *   </tr>
     *   <tr>
     *     <td>IRoleFactory.class</td>
     *     <td>Instance of IRoleFactory</td>
     *   </tr>
     *   <tr>
     *     <td>ISecurityPolicyFactory.class</td>
     *     <td>Instance of ISecurityPolicyFactory</td>
     *   </tr>
     *   <tr>
     *     <td>IServiceUserFactory.class</td>
     *     <td>Instance of IServiceUserFactory</td>
     *   </tr>
     *   <tr>
     *     <td>ISystemLandscapeFactory.class</td>
     *     <td>Instance of ISystemLandscapeFactory</td>
     *   </tr>
     *   <tr>
     *     <td>IUMFileIO.class</td>
     *     <td>Instance of IUMFileIO</td>
     *   </tr>
     *   <tr>
     *     <td>IUMParameters.class</td>
     *     <td>Instance of IUMParameters</td>
     *   </tr>
     *   <tr>
     *     <td>IUserAccountFactory.class</td>
     *     <td>Instance of IUserAccountFactory</td>
     *   </tr>
     *   <tr>
     *     <td>IUserFactory.class</td>
     *     <td>Instance of IUserFactory</td>
     *   </tr>
     *   <tr>
     *     <td>IUserMapping.class</td>
     *     <td>Instance of IUserMapping</td>
     *   </tr>
     *   <tr>
     *     <td>TicketVerifier.class</td>
     *     <td>Class object of the TicketVerifier implementation class</td>
     *   </tr>
     *   <tr>
     *     <td>"InitializationComplete" (<code>String</code>)</td>
     *     <td><code>Boolean</code> with value <code>true</code>. </td>
     *   </tr>
     * 
     * </table>
     */
    public static synchronized void initialize(Map factories) {
        // Initialization can only be performed once! Has UMFactory already been initialized?
        if(_instance != null) {
            String msg = "UMFactory has already been initialized! Re-initialization is " +
                "currently not supported!";
            // Trace the new exception to have a stacktrace and know the caller(s) of this method
            UMRuntimeException e = new UMRuntimeException(msg);
            loc.traceThrowableT(Severity.ERROR, M_INIT_MAP, msg, e);
            throw e;
        }

        _initState = STATE_INIT_RUNNING;

        // Get all factory object that could be contained in the argument Map
        IAclManagerFactory      aclManagerFactory      = (IAclManagerFactory     )factories.get(IAclManagerFactory     .class);
        IAnonymousUserFactory   anonymousUserFactory   = (IAnonymousUserFactory  )factories.get(IAnonymousUserFactory  .class);
        IAuthentication         authFactory            = (IAuthentication        )factories.get(IAuthentication        .class);
        IGroupFactory           groupFactory           = (IGroupFactory          )factories.get(IGroupFactory          .class);
        ILogonAuthentication    logonAuthFactory       = (ILogonAuthentication   )factories.get(ILogonAuthentication   .class);
        IPrincipalFactory       principalFactory       = (IPrincipalFactory      )factories.get(IPrincipalFactory      .class);
        IRoleFactory            roleFactory            = (IRoleFactory           )factories.get(IRoleFactory           .class);
        ISecurityPolicyFactory  securityPolicyFactory  = (ISecurityPolicyFactory )factories.get(ISecurityPolicyFactory .class);
        IServiceUserFactory     serviceUserFactory     = (IServiceUserFactory    )factories.get(IServiceUserFactory    .class);
        ISystemLandscapeFactory systemLandscapeFactory = (ISystemLandscapeFactory)factories.get(ISystemLandscapeFactory.class);
        IUMFileIO               umeFileIO              = (IUMFileIO              )factories.get(IUMFileIO              .class);
        IUMParameters           configuration          = (IUMParameters          )factories.get(IUMParameters          .class);
        IUserAccountFactory     userAccountFactory     = (IUserAccountFactory    )factories.get(IUserAccountFactory    .class);
        IUserFactory            userFactory            = (IUserFactory           )factories.get(IUserFactory           .class);
        IUserMapping            userMapping            = (IUserMapping           )factories.get(IUserMapping           .class);
        Class                   ticketVerifierClass    = (Class                  )factories.get(TicketVerifier         .class);
        Boolean                 initComplete           = (Boolean                )factories.get(INIT_COMPLETE                );
        // Keep a reference for each new factory
        // (Make sure that each factory is initialized only once!)

        if(aclManagerFactory != null) {
            if(_aclManagerFactory == null) { _aclManagerFactory = aclManagerFactory; }
            else {
                handleMultipleInit(IAclManagerFactory.class);
            }
        }

        if(anonymousUserFactory != null) {
            if(_anonymousUserFactory == null) { _anonymousUserFactory = anonymousUserFactory; }
            else {
                handleMultipleInit(IAnonymousUserFactory.class);
            }
        }

        if(authFactory != null) {
            if(_authFactory == null) { _authFactory = authFactory; }
            else {
                handleMultipleInit(IAuthentication.class);
            }
        }

        if(groupFactory != null) {
            if(_groupFactory == null) { _groupFactory = groupFactory; }
            else {
                handleMultipleInit(IGroupFactory.class);
            }
        }

        if(logonAuthFactory != null) {
            if(_logonAuthFactory == null) { _logonAuthFactory = logonAuthFactory; }
            else {
                handleMultipleInit(ILogonAuthentication.class);
            }
        }

        if(principalFactory != null) {
            if(_principalFactory == null) { _principalFactory = principalFactory; }
            else {
                handleMultipleInit(IPrincipalFactory.class);
            }
        }

        if(roleFactory != null) {
            if(_roleFactory == null) { _roleFactory = roleFactory; }
            else {
                handleMultipleInit(IRoleFactory.class);
            }
        }

        if(securityPolicyFactory != null) {
            if(_securityPolicyFactory == null) { _securityPolicyFactory = securityPolicyFactory; }
            else {
                handleMultipleInit(ISecurityPolicyFactory.class);
            }
        }

        if(serviceUserFactory != null) {
            if(_serviceUserFactory == null) { _serviceUserFactory = serviceUserFactory; }
            else {
                handleMultipleInit(IServiceUserFactory.class);
            }
        }
        
        if(systemLandscapeFactory != null) {
            if(_systemLandscapeFactory == null) { _systemLandscapeFactory = systemLandscapeFactory; }
            else {
                handleMultipleInit(ISystemLandscapeFactory.class);
            }
        }

        if(umeFileIO != null) {
            if(_umeFileIO == null) { _umeFileIO = umeFileIO; }
            else {
                handleMultipleInit(IUMFileIO.class);
            }
        }

        if(configuration != null) {
            if(_configuration == null) { _configuration = configuration; }
            else {
                handleMultipleInit(IUMParameters.class);
            }
        }

        if(userAccountFactory != null) {
            if(_userAccountFactory == null) { _userAccountFactory = userAccountFactory; }
            else {
                handleMultipleInit(IUserAccountFactory.class);
            }
        }

        if(userFactory != null) {
            if(_userFactory == null) { _userFactory = userFactory; }
            else {
                handleMultipleInit(IUserFactory.class);
            }
        }

        if(userMapping != null) {
            if(_userMapping == null) { _userMapping = userMapping; }
            else {
                handleMultipleInit(IUserMapping.class);
            }
        }

        if(ticketVerifierClass != null) {
            if(_ticketVerifierClass == null) { _ticketVerifierClass = ticketVerifierClass; }
            else {
                handleMultipleInit(TicketVerifier.class);
            }
        }

        // Check whether initialization should be completed now
        if(initComplete != null && initComplete.booleanValue()) {
            _instance = new UMFactory();
            _initState = STATE_INIT_COMPLETE;

            loc.infoT(M_INIT_MAP, "UMFactory successfully initialized.");
        }
    }

    /**
     * FOR INTERNAL USE ONLY: Initialize UME
     * For standalone issues.
     * 
     * The current classloader MUST be able to load classes that are part of UME
     * Core, especially <code>com.sap.security.core.InternalUMFactory</code>!
     * 
     * @param umeCfgPath    String object that contains the path
     *                      to the sapum.properties file and the additional xml files
     */
    // This initialization method is used for UME JVer tests.
    public static void initialize(String umeCfgPath) {
        try {
            Class internalUMFactoryClass = Class.forName("com.sap.security.core.InternalUMFactory");
            Method initUMEMethod = internalUMFactoryClass.getMethod(
                "initializeUME", new Class[] { String.class }
            );
            initUMEMethod.invoke(null, new Object[] { umeCfgPath } );
        }
        catch(Exception e) {
            // $JL-SYS_OUT_ERR$
            String msg = "An error occurred while initializing UME for standalone scenario.";
            // Print to stderr to make sure that this is really printed in standalone scenario
            System.err.println(msg);
            // Print and trace the exception to have a stacktrace and know the caller(s)
            // of this method
            e.printStackTrace();
            loc.traceThrowableT(Severity.ERROR, M_INIT_S, msg, e);
            throw new UMRuntimeException(e, msg);
        }
    }

    // ########################  Normal Static Methods  #########################

    /**
     * Returns the instance of UMFactory.
     * If the <code>UMFactory</code> is not already initialized this function throws
     * an <code>IllegalStateException</code>.
     * Note:    UMFactory will be initialized by EP6 Portal or SAP Application Server Java 630.
     *          If UMFactory should be used standalone, it has to be initialized explicitly with
     *                             the method initialize(String umeCfgPath)
     *          
     * @return    Instance of <code>UMFactory</code>
     * @exception java.lang.IllegalStateException If the <code>UMFactory</code> is not 
     *            already initialized.
     * @exception com.sap.security.api.UMRuntimeException If UME is running with
     *            client ("Mandant") concept enabled and the current client can
     *            not be determined.
     */
    public static UMFactory getInstance() throws UMRuntimeException {
        // Quick solution for the usual scenario
        // ("_instance != null" implies "_initState = STATE_INIT_COMPLETE",
        //  so no need to check separately)
        if(_instance != null) return _instance;

        // Normally, UME is initialized by an external, platform specific service
        // or other piece of code via InternalUMFactory.initializeUME(...) which
        // in turn calls UMFactory.initialize(Map).
        // However, there's also the use cases "UME standalone" and "TicketVerifier
        // standalone" where there's no such code that explicitely calls UME
        // initialization methods. As a replacement, the system property
        // "ume.cfg.path" (SYS_PROP_UME_CFG_DIR) must to be set to a directory
        // where UME can read its configuration data from. In that case, UMFactory
        // initializes itself when getInstance() is called.

        // --> Check whether UME is running standalone and there's a system property
        //     with the path to the configuration files
        String umeCfgPath = System.getProperty(SYS_PROP_UME_CFG_DIR);
        if(umeCfgPath == null) {
            // No configuration path in system properties
            // -> Not standalone, simply not initialized yet.
            String msg = "UME has not been initialized yet. Please check " +
                "UMFactory.isInitialized() before calling UMFactory.getInstance().";
            UMRuntimeException e = new UMRuntimeException(msg);
            loc.traceThrowableT(Severity.ERROR, M_GET_INSTANCE, msg, e);
            throw e;
        }
        else {
            // Standalone scenario -> Try self-initialization

            // Self-initialization must not run more than once in parallel.
            // This should be enough to prevent several initializations at the same
            // time, so no need to check the value of _initState here.
            synchronized(UMFactory.class) {
                // Perform minimal initialization for TicketVerifier usage
                try {
                    Class internalUMFactoryClass = Class.forName("com.sap.security.core.InternalUMFactory");
                    Method initUMEMethod = internalUMFactoryClass.getMethod(
                        "initializeUMEForTicketVerifier", new Class[] { String.class }
                    );
                    initUMEMethod.invoke(null, new Object[] { umeCfgPath } );
                
                    // Check whether initialization was really successful
                    if(_initState == STATE_INIT_COMPLETE && _instance == null) {
                        String msg = "UME initialization for standalone usage of TicketVerifier " +
                            "didn't throw any exception, but UMFactory is still not initialized. " +
                            "This is probably a UME internal problem.";
                        UMRuntimeException e = new UMRuntimeException(msg);
                        loc.traceThrowableT(Severity.ERROR, M_GET_INSTANCE, msg, e);
                        throw e;
                    }
                
                    return _instance;
                }
                catch(Exception e) {
                    String msg = "An error occurred while trying to initialize UME for " +
                        "standalone usage of TicketVerifier.";
                    loc.traceThrowableT(Severity.ERROR, M_GET_INSTANCE, msg, e);
                    throw new UMRuntimeException(e, msg);
                }
            } // synchronized(UMFactory.class)
        } // else - if(umeCfgPath == null)
    }
    
    /**
     * Provide access to an implementation of IAuthentication
     * @return IAuthentication object used for authentication handling.
         *  For more details see {@link com.sap.security.api.IAuthentication}
     **/
    public static IAuthentication getAuthenticator() {
        checkInitialized(_authFactory, IAuthentication.class);

        return _authFactory;
    }

    /**
     * Returns an implementation of IUserFactory.
     * This method should be called to get the
     * user factory for all user related operations.
     *
     * @return UserFactory object
     * @deprecated : use {@link #getUserFactory()} instead
     */
    @Deprecated
	public static IUserFactory getDefaultFactory() {
        return getUserFactory();
    }

    /*******
     * Returns an implementation of IGroupFactory.
     * This method should be called to get the
     * group factory for all group related operations.
     *
     * @return IGroupFactory object used for handling group operations
     *******/
    public static IGroupFactory getGroupFactory() {
        checkInitialized(_groupFactory, IGroupFactory.class);

        return _groupFactory;
    }

	/**
	 * Provide access to an implementation of ILogonAuthentication
	 * @return ILogonAuthentication object used for extended authentication handling.
	 *  For more details see {@link com.sap.security.api.logon.ILogonAuthentication}
	 *
	 **/
	public static ILogonAuthentication getLogonAuthenticator() {
        checkInitialized(_logonAuthFactory, ILogonAuthentication.class);

        return _logonAuthFactory;
	}

	/**
	 * A method in the usermanagement that wants to check whether the caller is
	 * allowed to call it should call this method instead of
	 * System.getSecurityManager() to get a security manager to perfom the
	 * checkPermission call.
	 * @return SecurityManager object, if a security manager was set using
	 * 			method setSecurityManager or if s system security manager
	 * 			exists.<br>
	 * 			null otherwise.
	 */
	public static SecurityManager getSecurityManager() {
		return _securitymanager;
	}

	/** Gets the global user mapping object which provides access to all user mapping data.
	 *  @return IUserMapping object used for handling user mapping operations.
	 */
	public static IUserMapping getUserMapping() {
        checkInitialized(_userMapping, IUserMapping.class);

		return _userMapping;
	}

	/****
	* NOTE: Released for internal use only.
	*/
	public static IUMFileIO getUMFileIO() {
        checkInitialized(_umeFileIO, IUMFileIO.class);

		return _umeFileIO;
	}

	/**
	 * NOTE: Released for internal use only.
	 * <p>Get access to <code>IUMParameters</code> interface
	 * @return API for accessing <code>IUMParameters</code>
     * @deprecated Released for internal use only
	 */
	public static IUMParameters getProperties() {
        checkInitialized(_configuration, IUMParameters.class);

		return _configuration;
	}
	
	/**
	 * Returns an implementation of IRoleFactory.
	 * This method should be called to get the
	 * role factory for all role related operations.
	 *
	 * @return IRoleFactory object used for handling role operations
	 **/
	public static IRoleFactory getRoleFactory() {
        checkInitialized(_roleFactory, IRoleFactory.class);

		return _roleFactory;
	}

	/**
	 * Returns an implementation of IUserAccountFactory.
	 * This method should be called to get the
	 * user account factory for all user account related operations.
	 *
	 * @return IUserAccountFactory object used for handling user account operations
	 **/
	public static IUserAccountFactory getUserAccountFactory() {
        checkInitialized(_userAccountFactory, IUserAccountFactory.class);

		return _userAccountFactory;
	}

	/**
	 * Returns an implementation of IPrincipalFactory.
	 * This method should be called to get the
	 * principal factory for all principal related operations.
	 *
	 * @return IPrincipalFactory object used for handling principal operations
	 **/
	public static IPrincipalFactory getPrincipalFactory() {
        checkInitialized(_principalFactory, IPrincipalFactory.class);

		return _principalFactory;
	}

	/**
	 * Returns an implementation of IUserFactory.
	 * This method should be called to get the
	 * user factory for all user related operations.
	 *
	 * @return IUserFactory object used for handling user operations
	 **/
	public static IUserFactory getUserFactory() {
        checkInitialized(_userFactory, IUserFactory.class);

		return _userFactory;
	}

	/**
	 * NOTE: Released for internal use only.
	 * <p>Returns an implementation of IServiceUserFactory.
	 * This method should be called to get the service
	 * user factory for all service user related operations.
	 *
	 * @return IServiceUserFactory object used for handling user operations
	 **/
	public static IServiceUserFactory getServiceUserFactory() {
        checkInitialized(_serviceUserFactory, IServiceUserFactory.class);

		return _serviceUserFactory;
	}

	/**
	 * Gets the default Access Control List (ACL) Manager.
	 * @return IAclManager object used for handling Access Control Lists
	 * For further details check com.sap.security.api.acl.IAclManager
	 */
	public static IAclManager getAclManager() {
		return getAclManager("default");
	}

	/**
	 * Gets an application specific Access Control List (ACL) Manager.
	 * @return IAclManager object used for handling Access Control Lists
	 * For further details check com.sap.security.api.acl.IAclManager
	 */
	public synchronized static IAclManager getAclManager(String applicationId) {
        checkInitialized(_aclManagerFactory, IAclManagerFactory.class);

        return _aclManagerFactory.getAclManager(applicationId);
	}

	/**
	 * Returns an array of all used Access Control List (ACL) Managers.
	 * @return String[]   applicationIDs of used ACL managers
	 */
	public static String[] getAllAclManagers() {
        checkInitialized(_aclManagerFactory, IAclManagerFactory.class);

        return _aclManagerFactory.getAllAclManagers();
	}

    /**
     *  Set the security manager that is used to protect the API.
     *  The security manager can only be set once. More attempts to set
     *  a security manager result in an IllegalStateException.
     *  An IllegalStateException is also thrown if there is a system
     *  security manager and this method is called.
     *  If the SecurityManagerFactory is visible for the UMFactory, this
     *  this method must be called before the UMFactory is initialized,
     *  because in this case during intialization a security manager is set if there is
     *  neither a system security manager nor this method was called.
     *  @param securitymanager security manager to be used
     *  @exception IllegalStateException in case this method has already
     *             been called before or there is a system security manager
     */
    public synchronized static void setSecurityManager(SecurityManager securitymanager) {
        if (UMFactory._securitymanager != null)
            throw new IllegalStateException("securitymanager already set!");

        _securitymanager = securitymanager;
    }

	/**
	 * NOTE: Released for internal use only.
     * 
     * @deprecated Use {@link #getSystemLandscapeFactory()} and
     * {@link ISystemLandscapeFactory#registerLandscape(ISystemLandscape)} instead.
     */
	@Deprecated
	public synchronized static void addSystemLandscapeWrapper(ISystemLandscapeWrapper slw) {
		ISystemLandscape landscape;
		if(slw instanceof ISystemLandscape) {
			landscape = (ISystemLandscape) slw;
		}
		else {
			// If the system landscape does not implement the new interface ISystemLandscape,
			// (we assume that) it can only be the existing instance for the Enterprise
			// Portal system landscape service.
			landscape = new WrappingSystemLandscape(slw, ISystemLandscape.TYPE_ENTERPRISE_PORTAL, IUserMapping.UMAP_EP6_ALIAS_PREFIX);
		}

		// For removeSystemLandscapeWrapper(...).
		_oldLandscapeWrappers.put(slw, landscape);

		_systemLandscapeFactory.registerLandscape(landscape);
	}

	/**
	 * Retrieve the list of all {@link com.sap.security.api.umap.system.ISystemLandscapeWrapper}
     * implementations that are currently registered.
     * 
     * @deprecated Use {@link #getSystemLandscapeFactory()} and
     * {@link ISystemLandscapeFactory#getAllLandscapes()} resp.
     * {@link ISystemLandscapeFactory#getLandscape(String)} instead.
	 */
	@Deprecated
	public static ArrayList getSystemLandscapeWrappers() {
		List<ISystemLandscape> landscapesList = _systemLandscapeFactory.getAllLandscapes();

		// Using ArrayList<ISystemLandscape> instead of ArrayList<ISystemLandscapeWrapper>
		// works as long as ISystemLandscape extends ISystemLandscapeWrapper.
		ArrayList<ISystemLandscape> wrappersArrayList = null;
		if(landscapesList instanceof ArrayList) {
			wrappersArrayList = (ArrayList<ISystemLandscape>) landscapesList;
		}
		else {
			wrappersArrayList = new ArrayList<ISystemLandscape>(landscapesList);
		}

		return wrappersArrayList;
	}

	/**
	 * NOTE: Released for internal use only.
     * 
     * @deprecated Use {@link #getSystemLandscapeFactory()} and
     * {@link ISystemLandscapeFactory#unregisterLandscape(ISystemLandscape)} instead.
 	 */
	@Deprecated
	public synchronized static void removeSystemLandscapeWrapper(ISystemLandscapeWrapper slw) {
		ISystemLandscape landscape = _oldLandscapeWrappers.get(slw);
		if(landscape != null) {
			_systemLandscapeFactory.unregisterLandscape(landscape);
		}
	}

	public static ISystemLandscapeFactory getSystemLandscapeFactory() {
		return _systemLandscapeFactory;
	}

	/**
	 * Gets the anonymous user factory for retrieving anonymous user.
	 * @return IAnonymousUserFactory factory handling anonymous user objects.
	 * For further details refer to {com.sap.security.api.logon.IAnonymousUserFactory}
	 */
	public static IAnonymousUserFactory getAnonymousUserFactory() {
        checkInitialized(_anonymousUserFactory, IAnonymousUserFactory.class);

		return _anonymousUserFactory;
	}

	/**
	 * getSecurityPolicy provides access to the security policy object
	 * @return ISecurityPolicy object used for security policy handling.
		 *  For more details see {@link com.sap.security.api.ISecurityPolicy}
	 **/
	public static ISecurityPolicy getSecurityPolicy() {
        checkInitialized(_securityPolicyFactory, ISecurityPolicy.class);

		return _securityPolicyFactory.getSecurityPolicy();
	}

	/***
	 * isInitialized provides information about the state of UMFactory.
	 * @return true if UMFactory is already initialized and configured,
	 * false otherwise
	 **/
	public static boolean isInitialized() {
        // If initialization is still running, some factories may already be
        // usable, but this is not a really "initialized" status.
		return _initState == STATE_INIT_COMPLETE;
	}
    
    /**
     * getTicketVerifier provides access to an object which can be used for verifing
     * Tickets
     * @return TicketVerifier object used for ticket handling.
     *  For more details see {@link com.sap.security.api.ticket.TicketVerifier}
     * 
     * @deprecated Please use the native library "SAPSSOEXT" and the corresponding
     * Java wrapper. For further information, including where to download the
     * "SAPSSOEXT" package, please refer to the
     * <a href="http://help.sap.com/saphelp_nw2004s/helpdata/en/12/9f244183bb8639e10000000a1550b0/frameset.htm">
     * online documentation</a>.
     **/
    @Deprecated
	public TicketVerifier getTicketVerifier() {
        checkInitialized(_ticketVerifierClass, TicketVerifier.class);

        try {
            TicketVerifier ticketVerifier = (TicketVerifier) _ticketVerifierClass.newInstance();
            ticketVerifier.setEnforceVerify(false);
            return ticketVerifier;
        }
        catch(Exception e) {
            String msg = "An error occurred while instantiating a TicketVerifier " +
                "instance. Please check whether UME configuration is correct and " +
                "whether all required classes can be accessed.";
            loc.traceThrowableT(Severity.ERROR, M_GET_TICKET_VERIFIER, msg, e);
            throw new UMRuntimeException(e, msg);
        }
    }

    // #######################  Private Helper Methods  #######################

    /**
     * Handle attempted multiple initialization of the same factory.
     * Includes logging, tracing and throwing a runtime exception.
     * @param factoryType Type of UME factory which was attempted to be initialized
     *        a second time.
     */
    private static void handleMultipleInit(Class factoryType) {
        String msg = MessageFormat.format(
            "Rejected attempt to initialize UME factory of type {0} a second time " +
            "because re-initialization is currently not supported.",
            new Object[] { factoryType.getName() }
        );
        RuntimeException e = new UMRuntimeException(msg);
        loc.traceThrowableT(Severity.ERROR, M_HANDLE_MULTI_INIT, msg, e);
        throw e;
    }

    /**
     * Check whether the specified UME factory object is already initialized.
     * If it is, the method silently returns. If not, it throws a UMRuntimeException
     * and writes appropriate log and trace messages.
     * 
     * @param factoryObject UME factory object to check
     * @param factoryInterface Java interface of the factory object for tracing
     * @throws UMRuntimeException if the factory object has not been initialized yet
     */
    private static void checkInitialized(Object factoryObject, Class factoryInterface)
    throws UMRuntimeException {
        // First check whether the requested factory has already been initialized
        if(factoryObject != null) {
            // Factory object is ok, so no need for further checks
            return;
        }

        // Factory object is still null.
        // Did initialization already start?
        switch(_initState) {
            case STATE_INITIAL : {
                String msg = MessageFormat.format("UME factory ''{0}'' cannot be " +
                    "accessed because UME initialization has not started yet. Please " +
                    "check UMFactory.isInitialized() before using UME functionality.",
                    new Object[] { factoryInterface.getName() }
                );
                UMRuntimeException e = new UMRuntimeException(msg);
                loc.traceThrowableT(Severity.ERROR, M_CHECK_INITIALIZED, msg, e);
                throw new UMRuntimeException(msg);
            }
            case STATE_INIT_RUNNING : {
                String msg = MessageFormat.format("UME factory ''{0}'' cannot be " +
                    "accessed because UME initialization has not finished yet. Please " +
                    "check UMFactory.isInitialized() before using UME functionality.",
                    new Object[] { factoryInterface.getName() }
                );
                UMRuntimeException e = new UMRuntimeException(msg);
                loc.traceThrowableT(Severity.ERROR, M_CHECK_INITIALIZED, msg, e);
                throw new UMRuntimeException(msg);
            }
            // STATE_INIT_COMPLETE is the only potential state left, so is the same as "default"
            case STATE_INIT_COMPLETE :
            default : {
                String msg = MessageFormat.format("UME factory ''{0}'' has not been " +
                    "initialized although UME initialization has already finished. This " +
                    "is most probably an error in UME initialization.",
                    new Object[] { factoryInterface.getName() }
                );
                UMRuntimeException e = new UMRuntimeException(msg);
                loc.traceThrowableT(Severity.ERROR, M_CHECK_INITIALIZED, msg, e);
                throw new UMRuntimeException(msg);
            }
        }
    }

}
