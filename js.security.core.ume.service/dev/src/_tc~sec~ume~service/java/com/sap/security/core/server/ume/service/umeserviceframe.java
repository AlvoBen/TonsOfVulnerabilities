package com.sap.security.core.server.ume.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import com.sap.engine.frame.ApplicationFrameAdaptor;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.ip.basecomps.consistency.ConsistencyDomains;
import com.sap.ip.j2eeengine.consistency.MessageBroker;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.security.api.AttributeValueAlreadyExistsException;
import com.sap.security.api.IGroup;
import com.sap.security.api.IGroupFactory;
import com.sap.security.api.IPrincipal;
import com.sap.security.api.IRole;
import com.sap.security.api.IRoleFactory;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.IUserAccountFactory;
import com.sap.security.api.IUserFactory;
import com.sap.security.api.IUserMaint;
import com.sap.security.api.NoSuchGroupException;
import com.sap.security.api.NoSuchPrincipalException;
import com.sap.security.api.NoSuchRoleException;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.api.persistence.IDataSourceMetaData;
import com.sap.security.api.persistence.IR3Persistence;
import com.sap.security.core.IEngineResourceHelper;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.admin.UserAdminHelper;
import com.sap.security.core.imp.AbstractUserAccount;
import com.sap.security.core.locking.imp.LockManager;
import com.sap.security.core.logon.imp.AnonymousUser;
import com.sap.security.core.persistence.datasource.PersistenceException;
import com.sap.security.core.persistence.datasource.imp.CompanyGroups;
import com.sap.security.core.persistence.datasource.imp.DummyDSConfigurationModel;
import com.sap.security.core.persistence.imp.PrincipalDatabagFactory;
import com.sap.security.core.role.imp.PermissionRoles;
import com.sap.security.core.role.imp.xml.XMLServiceRepository;
import com.sap.security.core.server.ume.service.jacc.ServiceIntegration;
import com.sap.security.core.server.ume.service.monitor.impl.SecurityUMEManagementInterfaceImpl;
import com.sap.security.core.server.userstore.AssignmentEntry;
import com.sap.security.core.server.userstore.GroupContextUME;
import com.sap.security.core.server.userstore.GroupEntry;
import com.sap.security.core.server.userstore.PropertiesParserUME;
import com.sap.security.core.server.userstore.UserEntry;
import com.sap.security.core.util.config.IUMConfigAdmin;
import com.sap.security.core.util.config.IUMConfiguration;
import com.sap.security.core.util.imp.Util;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sapmarkets.tpd.TradingPartnerDirectoryCommon;

//TODO Remove all IEngineResourceHelper methods after fully switching to new UME initialization

/**
 * @author d031387
 */
public class UMEServiceFrame extends ApplicationFrameAdaptor implements IEngineResourceHelper {

	public static Location myLoc = Location.getLocation(UMEServiceFrame.class);
	public static Category myCat = Category.getCategory(LoggingHelper.SYS_SECURITY, "Usermanagement");

	private SAPJ2EEPlatformTools _platformTools = null;
	private MessageBroker msBroker = null;

	private final static String COMPONENT_COMMON = "common:";

	private ServerInternalLocking mLocking;
	private boolean mTxManagerInterfaceAvailable = false;
	private UMEEventListener mListener;

	public boolean useTxManager()
	{
		return mTxManagerInterfaceAvailable;
	}

	static ServiceIntegration serviceIntegration = null;

	public void setTxManagerAvailable(boolean available)
	{
		mTxManagerInterfaceAvailable = available;
	}

	public boolean isSystemThread()
	{
		return (getServiceContext().getCoreContext().getThreadSystem().getThreadContext() == null);
	}

	public char getSharedMode()
	{
		return LockingConstants.MODE_SHARED;
	}

	public char getExclusiveNonCumulativeMode()
	{
		return LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE;
	}

	public boolean isLockingEnabled()
	{
		return (mLocking != null);
	}

	public void unlock(String[] names,String[] principalIDs,char[] modes) throws com.sap.security.core.locking.LockException, com.sap.security.core.locking.TechnicalLockException
	{
		if (mLocking != null)
		{
			try
			{
				mLocking.unlock(names, principalIDs, modes);
			}
			catch (Exception e)
			{
				//a unexpected error occurred
				throw new com.sap.security.core.locking.TechnicalLockException(e);
			}
		}
	}

	public void unlock(String name,String principalID,char mode) throws com.sap.security.core.locking.LockException, com.sap.security.core.locking.TechnicalLockException
	{
		if (mLocking != null)
		{
			try
			{
				mLocking.unlock(name, principalID, mode);
			}
			catch (Exception e)
			{
				//a unexpected error occurred
				throw new com.sap.security.core.locking.TechnicalLockException(e);
			}
		}
	}

	public String lock(String name,String principalID,char mode, int timeout) throws com.sap.security.core.locking.LockException, com.sap.security.core.locking.TechnicalLockException
	{
		if (mLocking != null)
		{
			try
			{
				mLocking.lock(name, principalID, mode, timeout);
				return mLocking.getUniqueOwner();
			}
			catch (com.sap.engine.frame.core.locking.LockException lex)
			{
				//object was locked... transform the lock exception
				throw new com.sap.security.core.locking.LockException(lex);
			}
			catch (Exception e)
			{
				//a unexpected error occurred
				throw new com.sap.security.core.locking.TechnicalLockException(e);
			}
		}
		return null;
	}

	public String lock(String name,String principalID,char mode) throws com.sap.security.core.locking.LockException, com.sap.security.core.locking.TechnicalLockException
	{
		if (mLocking != null)
		{
			try
			{
				mLocking.lock(name, principalID, mode);
				return mLocking.getUniqueOwner();
			}
			catch (com.sap.engine.frame.core.locking.LockException lex)
			{
				//object was locked... transform the lock exception
				throw new com.sap.security.core.locking.LockException(lex);
			}
			catch (Exception e)
			{
				//a unexpected error occurred
				throw new com.sap.security.core.locking.TechnicalLockException(e);
			}
		}
		return null;
	}

	public String lock(String[] names,String[] principalIDs,char[] modes,int timeout) throws com.sap.security.core.locking.LockException, com.sap.security.core.locking.TechnicalLockException
	{
		if (mLocking != null)
		{
			try
			{
				mLocking.lock(names, principalIDs, modes, timeout);
				return mLocking.getUniqueOwner();
			}
			catch (com.sap.engine.frame.core.locking.LockException lex)
			{
				//object was locked... transform the lock exception
				throw new com.sap.security.core.locking.LockException(lex);
			}
			catch (Exception e)
			{
				//a unexpected error occurred
				throw new com.sap.security.core.locking.TechnicalLockException(e);
			}
		}
		return null;
	}

	public String lock(String[] names,String[] principalIDs,char[] modes) throws com.sap.security.core.locking.LockException, com.sap.security.core.locking.TechnicalLockException
	{
		if (mLocking != null)
		{
			try
			{
				mLocking.lock(names, principalIDs, modes);
				return mLocking.getUniqueOwner();
			}
			catch (com.sap.engine.frame.core.locking.LockException lex)
			{
				//object was locked... transform the lock exception
				throw new com.sap.security.core.locking.LockException(lex);
			}
			catch (Exception e)
			{
				//a unexpected error occurred
				throw new com.sap.security.core.locking.TechnicalLockException(e);
			}
		}
		return null;
	}

	/**
	 * Constructor for UMEService.
	 */
	public UMEServiceFrame() {
		super();
	}


	public void addEventListener(int mask, Set interfaces, ContainerEventListener listener)
	{
		if (mListener != null)
		{
			mListener.addEventListener(mask, interfaces, listener);
		}
	}

	/**
	 * @see com.sap.engine.frame.ApplicationFrameAdaptor#start()
	 */
	public void start() throws ServiceException {
		String method = "start()";
		try {
			myLoc.entering(method);

			// Prepare adapter for SAP Application Server Java specific functions.
			_platformTools = new SAPJ2EEPlatformTools(this);

			/** initialize Notification */
			myLoc.infoT(method, "initialising notification");
			msBroker = new MessageBroker(0);
			msBroker.start(_platformTools);
			ClassLoader loader = this.getClass().getClassLoader();
			Properties cacheProps = new Properties();
			cacheProps.setProperty("class", "com.sap.ip.j2eeengine.consistency.J2EEConsistency");
			ConsistencyDomains.createDomain("UME_ConsistencyDomain", cacheProps, loader);
			myLoc.infoT(method, "notification initialized");

			myLoc.infoT(method, "Registering UMEEventListener");
			mListener = new UMEEventListener(this);

			int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE | 
			ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE |
			ContainerEventListener.MASK_SERVICE_STARTED |
			ContainerEventListener.MASK_SERVICE_STOPPED;

			Set names = new HashSet(1);
			names.add(TxManagerEventListener.TX_MANAGER_INTERFACE);

			mListener.addEventListener(mask, names, new TxManagerEventListener(this));

			InternalUMFactory.setEngineResourceHelper(this);

			//initialize UMFactory
			myLoc.infoT(method, "Initializing UME");
			InternalUMFactory.initializeUME(_platformTools);

			//initialize TPD
			myLoc.infoT(method, "Initializing TPD");
			try
			{
				this.initializeTPD();
			}
			catch (Exception e)
			{
				myCat.fatal(myLoc, "Loading of TPD implementation failed. Check TPD configuration.");
				throw e;
			}

			try {
				myLoc.infoT(method, "Try to get locking context");
				String namespace = "$library.security.ume";
				ServerInternalLocking loc = getServiceContext().getCoreContext().getLockingContext().createServerInternalLocking(namespace,"UME internal locking");
				if (loc != null) {
					myLoc.infoT(method, "Got locking context, try to register");
					mLocking = loc;
					LockManager.NAMESPACE = namespace;
					myLoc.infoT(method, "Locking successfully registered");
				} else {
					myLoc.infoT(method, "Got no locking context. No locking will be available in UME.");
				}
			} catch (Exception e) {
				myCat.fatal(myLoc, "Cannot access locking infrastructure. Check availability and configuration of the locking infrastructure like Enqueue Server.");
				throw e;
			}

			myLoc.infoT(method, "Accessing UME persistence");
			try {
				PrincipalDatabagFactory.getInstance();
			} catch (Exception e) {
				myCat.fatal(myLoc, "Initialization of UME persistence failed. Check UME persistence configuration.");
				throw e;
			}

			//access UME db pool to get this done in the service's context
			myLoc.infoT(method, "Accessing DB pool");
			try {          
				InternalUMFactory.getJdbcConnectionPool();
			} 
			catch (Exception e)
			{
				myCat.fatal(myLoc, "Cannot access system database. Check availability of the system database and the configuration of the system connection pool.");
				throw e;
			}

			myLoc.infoT(method, "Accessing UME meta data.");
			com.sap.security.core.imp.PrincipalMetaDataManager.getInstance();
			
			myLoc.infoT(method, "Creating default users if not done yet.");
			try {          
				this.createDefaultUsers();
			} 
			catch (Exception e)
			{
				myCat.fatal(myLoc, "Error while accessing or creating default users. Check technical details below.");
				throw e;
			}

			// migrate old persisted action xml files
			XMLServiceRepository.loadXMLFiles();

			//create default roles
			myLoc.infoT(method, "Updating UME default roles");
			PermissionRoles.createDefaultRoles();

			//register container for deployment of UME role files
			if (_platformTools.getConfiguration().getBooleanStatic("ume.roles.deploy_files", true)) {
				myLoc.infoT(method, "Container object creation");
				ApplicationServiceContext serviceContext = getServiceContext();
				UMEContainer container = new UMEContainer(serviceContext);
				ContainerManagement containerMgmt = null;
				try {
					containerMgmt =
						(ContainerManagement) serviceContext
						.getContainerContext()
						.getObjectRegistry()
						.getProvidedInterface(
						"container");
					myLoc.infoT(method, "Got container management object: " + containerMgmt);
				} catch (Exception e) {
					myLoc.errorT(method, "Cannot get container management object from service framework.");
					throw e;
				}
				if (containerMgmt != null) {
					// registering the deployment manager
					DeployCommunicator deployCommunicator =
						containerMgmt.registerContainer(container.getContainerInfo().getName(), container);
					container.setDeployCommunicator(deployCommunicator);
					myLoc.infoT(method, "Container object bound");
				} else {
					myLoc.infoT(method, "Container object not bound, because missing container management object.");
				}
			}

			if (myLoc.beInfo())
				myLoc.infoT(method, "Trying to register j2ee monitoring interface");
			try {
				getServiceContext().getServiceState().registerManagementInterface(new SecurityUMEManagementInterfaceImpl());
			}
			catch (Exception e) {
				myLoc.traceThrowableT(Severity.WARNING,"Initialization of j2ee monitoring failed", e);
			}

			//          try {
			//              myLoc.infoT(method,"Checking Role: " + ADMIN_ROLE_NAME);
			//              UMFactory.getRoleFactory().getRoleByUniqueName(ADMIN_ROLE_NAME);
			//              myLoc.infoT(method,ADMIN_ROLE_NAME + " does exist");
			//          } catch(NoSuchRoleException nsre){
			//              myLoc.infoT(method,ADMIN_ROLE_NAME + " does not exist. Creating it.");
			//              PermissionRoles.createDefaultRoles();
			//          }
			//      } catch (UMException e) {
			//          ServiceException se = new ServiceException(new LocalizableTextFormatter(UMEServiceResourceAccessor.getResourceAccessor(),"com.sap.security.core.server.ume.service_0002", new Object[] {}),e);
			//          LoggingHelper.logThrowable(Severity.ERROR,myCat,myLoc,method,se);
			//          myLoc.throwing(method, se);
			//          throw se;
			myLoc.infoT(method, "Initialization of the services ume authorization");
			serviceIntegration = new ServiceIntegration(getServiceContext(), this);

			com.sap.security.core.server.ume.service.jacc.JACCMigrationContextImpl.setConfigurationHandlerFactory(getServiceContext().getCoreContext().getConfigurationHandlerFactory());

			myLoc.infoT(method, "Creating assignments for default users and groups if not done yet.");
			this.createDefaultAssignments();

			myLoc.infoT(method, "Initialization finished successfully");
			/**
			 * D032841: Remove as part of log and trace cleanup (CSN 132166 2008). Successful start is irrelevant for system administrators. Only 10 INFO messages allowed for BC-JAS-SEC-UME
			   myCat.infoT(myLoc, "UME Service initialized successfully");
			 */
		}
		catch(Exception e) {
			String msg = MessageFormat.format(
					"Start of UME service failed. Check help topic \"Start of UME Service Failed\". Technical details: {0}",
					new Object[] { e.getMessage() });
			myLoc.traceThrowableT(Severity.ERROR, method, msg, e);
			myCat.fatal(myLoc, msg);            
			throw new ServiceException(new LocalizableTextFormatter(UMEServiceResourceAccessor.getResourceAccessor(),"com.sap.security.core.server.ume.service_0001", new Object[] {e.getMessage()}),e);
		}
		finally {
			myLoc.exiting();
		}
	}

	/**
	 * @see com.sap.engine.frame.ServiceFrame#stop()
	 */
	public void stop() throws ServiceRuntimeException {
		String method = "stop()";
		myLoc.entering(method);

		// Shut down message broker.
		msBroker.stop();

		// Shut down configuration handler (-> change listener(s)).
		_platformTools.stop();

		myLoc.infoT(method, "Stopping finished successfully");
		myLoc.exiting();
	}

	private void createDefaultUsers() throws com.sap.security.api.UMException
	{
		final String method = "createDefaultUsers()";
		boolean beDebug = myLoc.beDebug();
		try
		{
			myLoc.entering(method);

			IUserFactory userFactory = UMFactory.getUserFactory();
			IUserAccountFactory userAccountFactory = UMFactory.getUserAccountFactory();
			// TODO Remove the following line because role factory is never used?
			IRoleFactory roleFactory = UMFactory.getRoleFactory();

			// Create default users     
			Collection usersAndGroups = PropertiesParserUME.getUsersAndGroupsAndAssignments();
			if (beDebug) myLoc.debugT(method,"Creating missing default users");
			String lockOwner = null;
			String lockNamespace = "$library.security.ume.core";
			String lockName = "INITIAL_USER_CREATION";
			char lockMode = this.getExclusiveNonCumulativeMode();
			try
			{
				if (beDebug) myLoc.debugT(method,"Acquiring lock...");
				lockOwner = this.lock(lockNamespace, lockName, lockMode, 60000);
				if (beDebug) myLoc.debugT(method,"Got lock.");
				for (Iterator usersGroupsIter = usersAndGroups.iterator(); usersGroupsIter.hasNext();) 
				{
					Object o = usersGroupsIter.next();
					if (o instanceof UserEntry)
					{
						UserEntry user = (UserEntry)o;
						String userName = user.getName();
						try 
						{
							userFactory.getUserByLogonID(userName);
							if (beDebug) myLoc.debugT(method,"User " + userName + "already exists");
						} 
						catch (NoSuchPrincipalException nsue) 
						{
							IUserMaint newUser = userFactory.newUser(userName);
							newUser.setLastName(userName);
							IUserAccount newAccount = userAccountFactory.newUserAccount(userName);
							if (user.getPassword() != null) 
							{
								String now = Util.getTimeStamp();						
								newAccount.setAttribute(IPrincipal.DEFAULT_NAMESPACE,
										com.sap.security.api.logon.ILoginConstants.LOGON_PWD_ALIAS,
										new String[]{user.getPassword()} );						
								newAccount.setAttribute(IPrincipal.DEFAULT_NAMESPACE,
										AbstractUserAccount.IS_PASSWORD_DISABLED,
										new String[]{AbstractUserAccount.FALSE} );
								newAccount.setAttribute(IPrincipal.DEFAULT_NAMESPACE,
										AbstractUserAccount.LAST_PASSWORD_CHANGE,
										new String[]{now});
								if (UMFactory.getSecurityPolicy().getPasswordMaxIdleTime() > 0)
								{
									newAccount.setAttribute(IPrincipal.DEFAULT_NAMESPACE, 
											AbstractUserAccount.LAST_SUCCESSFUL_PASSWORD_CHECK,
											new String[]{now});			        
								}
								newAccount.setAttribute(IPrincipal.DEFAULT_NAMESPACE, 
										AbstractUserAccount.FAILED_LOGON_ATTEMPTS,
										new String[]{"0"});
								newAccount.setAttribute(IPrincipal.DEFAULT_NAMESPACE,
										AbstractUserAccount.PASSWORD_CHANGE_REQUIRED,
										new String[]{AbstractUserAccount.FALSE} );
							}
							if (user.isLocked())
							{
								newAccount.setLocked(true,IUserAccount.LOCKED_BY_ADMIN);
								if (beDebug) myLoc.debugT(method,"User " + userName + " locked");
							}
							userFactory.commitUser(newUser,newAccount);

							if (user.getPassword() != null)
							{
								if (beDebug) myLoc.debugT(method,"User " + userName + " created with productive password");
							}
							else
							{
								if (beDebug) myLoc.debugT(method,"User " + userName + " created without password");
							}
						}       					
						user.cleanupSecStoreEntries(false);
					}
				}
			}
			finally
			{
				if (lockOwner != null)
				{
					if (beDebug) myLoc.debugT(method,"Releasing lock...");
					this.unlock(lockNamespace, lockName, lockMode);
					if (beDebug) myLoc.debugT(method,"Lock released.");
				}
			}
			if (beDebug) myLoc.debugT(method,"Finished creation of missing default users");
		}
		catch (Exception thr)
		{
			myLoc.traceThrowableT(Severity.ERROR, "Error during creation of default users or role assignments: {0}", new Object[] {thr.getMessage()}, thr);
			if (thr instanceof UMException)
			{
				throw (UMException)thr;
			}
			else
			{
				throw new UMException(thr);
			}
		}

		//check whether UME guest user is now accessible
		try
		{
			//guest users are now created --> allow access and check the existence. Otherwise stop the server.
			AnonymousUser.enableGuestUserAccess();
			UMFactory.getAnonymousUserFactory().getAnonymousUser();
		}
		catch (UMException umex)
		{
			myLoc.traceThrowableT(Severity.ERROR, "Error while trying to get the anonymous user: {0}", new Object[] {umex.getMessage()}, umex);
			throw umex;
		}
	}

	private void createDefaultAssignments() throws UMException
	{
		final String method = "enginePropertiesChanged(java.util.Properties)";
		boolean beDebug = myLoc.beDebug();
		try
		{
			myLoc.entering(method);

			IGroupFactory groupFactory = UMFactory.getGroupFactory(); 
			IUserFactory userFactory = UMFactory.getUserFactory();
			IUserAccountFactory accountFactory = UMFactory.getUserAccountFactory(); // TODO Remove this? Never used...
			IRoleFactory roleFactory = UMFactory.getRoleFactory();

			// Create default groups 
			Collection usersAndGroups = PropertiesParserUME.getUsersAndGroupsAndAssignments();
			if (beDebug) myLoc.debugT(method,"Creating missing default groups");
			String lockOwner = null;
			String lockNamespace = "$library.security.ume.core";
			String lockName = "INITIAL_USER_CREATION";
			char lockMode = this.getExclusiveNonCumulativeMode();
			try
			{
				if (beDebug) myLoc.debugT(method,"Acquiring lock...");
				lockOwner = this.lock(lockNamespace, lockName, lockMode, 60000);
				if (beDebug) myLoc.debugT(method,"Got lock.");
				for (Iterator usersGroupsIter = usersAndGroups.iterator(); usersGroupsIter.hasNext();) 
				{
					Object o = usersGroupsIter.next();
					if (o instanceof GroupEntry)
					{
						GroupEntry group = (GroupEntry)o;
						String groupName = group.getName();
						try 
						{
							groupFactory.getGroupByUniqueName(groupName);
							if (beDebug) myLoc.debugT(method,"Group " + groupName + "already exists");				
						} 
						catch (NoSuchGroupException nsue) 
						{
							IGroup newGroup = groupFactory.newGroup(groupName);
							newGroup.save();
							newGroup.commit();
							if (beDebug) myLoc.debugT(method,"Group " + groupName + " created");
						}

						Collection roles = group.getRoles();
						if (roles != null)
						{
							for (Iterator rolesIter = roles.iterator(); rolesIter.hasNext();)
							{
								String roleName = (String)rolesIter.next();
								IRole role = roleFactory.getRoleByUniqueName(roleName);
								IGroup umeGroup = groupFactory.getGroupByUniqueName(groupName);
								if (role.isGroupMember(umeGroup.getUniqueID(),false)) 
								{
									if (beDebug) myLoc.debugT(method,"Group " + groupName + " is already in UME role" + roleName);							
								} 
								else 
								{
									roleFactory.addGroupToRole(umeGroup.getUniqueID(),role.getUniqueID());
									if (beDebug) myLoc.debugT(method,"Group " + groupName + " added to UME role" + roleName);							
								}
								if (GroupContextUME.UME_ADMIN_ROLE_NAME.equals(roleName))
								{
									//mark the assigned group for assignment of portal admin role
									IGroup g = groupFactory.getMutableGroup(umeGroup.getUniqueID());
									g.setAttribute(IPrincipal.DEFAULT_NAMESPACE,"isadmingroup",new String[]{"true"});
									g.save();
									g.commit();
								}
							}
						}
						group.cleanupSecStoreEntries();
					}
					if (o instanceof UserEntry)
					{
						UserEntry user = (UserEntry)o;
						String userName = user.getName();
						Collection roles = user.getRoles();
						boolean cleanupRoleAssignments = true; 
						if (roles != null)
						{
							for (Iterator roleIter = roles.iterator(); roleIter.hasNext();)
							{
								String roleName = (String)roleIter.next();
								try
								{
									IRole role = roleFactory.getRoleByUniqueName(roleName);
									IUser umeUser = userFactory.getUserByLogonID(userName);
									if (role.isUserMember(umeUser.getUniqueID(),false)) 
									{
										if (beDebug) myLoc.debugT(method,"User " + userName + " is already in UME role " + roleName);							
									} 
									else 
									{
										roleFactory.addUserToRole(umeUser.getUniqueID(),role.getUniqueID());
										if (beDebug) myLoc.debugT(method,"User " + userName + " added to UME role " + roleName);							
									}
								}
								catch (NoSuchRoleException nsrex)
								{
									myLoc.traceThrowableT(Severity.INFO, "Can't read role {0}", new Object[] {roleName}, nsrex);
									cleanupRoleAssignments = false;
								}
							}
						}				
						user.cleanupSecStoreEntries(cleanupRoleAssignments);
					}
				}

				//Create initial group assignments
				if (beDebug) myLoc.debugT(method,"Adding default users to default groups");

				for (Iterator usersGroupsIter = usersAndGroups.iterator(); usersGroupsIter.hasNext();) 
				{
					Object o = usersGroupsIter.next();
					if (o instanceof AssignmentEntry)
					{
						AssignmentEntry assignment = (AssignmentEntry)o;
						String userName = assignment.getUserName();		    		
						Collection parentGroups = assignment.getParentGroups();
						if (parentGroups != null)
						{
							for (Iterator groupIterator=parentGroups.iterator(); groupIterator.hasNext();)
							{
								String groupName = (String)groupIterator.next();
								IGroup g = groupFactory.getGroupByUniqueName(groupName);
								IUser  u = userFactory.getUserByLogonID(userName);
								if (!g.isUserMember(u.getUniqueID(),false))
								{
									if (UMFactory.getPrincipalFactory().isPrincipalAttributeModifiable(g.getUniqueID(),IPrincipal.DEFAULT_RELATION_NAMESPACE, IPrincipal.PRINCIPAL_RELATION_MEMBER_ATTRIBUTE))
									{
										try
										{
											IGroup mutableGroup = groupFactory.getMutableGroup(g.getUniqueID());      		    				
											mutableGroup.addUserMember(u.getUniqueID());
											mutableGroup.save();
											mutableGroup.commit();
											if (beDebug) myLoc.debugT(method,"User " + userName + " added to group " + groupName);    			
										}
										catch (AttributeValueAlreadyExistsException avaex)
										{
											if (beDebug) myLoc.debugT(method,"User " + userName + " is already assigned to group " + groupName);
											LoggingHelper.traceThrowable(Severity.DEBUG, myLoc, method, avaex);
										}
									}
									else
									{
										if (beDebug) myLoc.debugT(method,"Member attribute of group " + groupName + " is not modifiable. Therefore user " + userName + " is not added to the group.");
									}
								}
								else
								{
									if (beDebug) myLoc.debugT(method,"User " + userName + " is already in group " + groupName);							
								}
							}
						}
						assignment.cleanupSecStoreEntries();
					}
				}
			}
			finally
			{
				if (lockOwner != null)
				{
					if (beDebug) myLoc.debugT(method,"Releasing lock...");
					this.unlock(lockNamespace, lockName, lockMode);
					if (beDebug) myLoc.debugT(method,"Lock released.");
				}
			}
		}
		catch (Exception thr)
		{
			myLoc.traceThrowableT(Severity.ERROR,  "Error during creation of default groups or role assignments: {0}", new Object[] {thr.getMessage()}, thr);
			if (thr instanceof UMException)
			{
				throw (UMException)thr;
			}
			else
			{
				throw new UMException(thr);
			}
		}	
	}

	public String getAuthenticationConfiguration() {
		final String method = "getAuthenticationConfiguration";
		Method m = null;

		Object o = getSecurityContext();
		if (o == null)
			return null;

		try {
			m = o.getClass().getMethod("getSession", new Class[0]);
			if (m == null) {
				throw new NoSuchMethodException("");
			}
			// o2 ist jetzt ein SecuritySession
			Object o2 = m.invoke(o, new Object[0]);

			m = o2.getClass().getMethod("getAuthenticationConfiguration", new Class[0]);
			return (String) m.invoke(o2, new Object[0]);
		} catch (SecurityException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, method, e);
			myLoc.errorT(myCat, method, "reflection calls failed");
		} catch (NoSuchMethodException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, method, e);
			myLoc.errorT(myCat, method, "reflection calls failed");
		} catch (IllegalArgumentException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, method, e);
			myLoc.errorT(myCat, method, "reflection calls failed");
		} catch (IllegalAccessException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, method, e);
			myLoc.errorT(myCat, method, "reflection calls failed");
		} catch (InvocationTargetException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, method, e);
			myLoc.errorT(myCat, method, "reflection calls failed");
		}
		return null;
	}

	private Object getSecurityContext() {
		final String METHOD = "getSecurityContext";
		Object o = null;

		try {
			o =
				this.getServiceContext().getCoreContext().getThreadSystem().getThreadContext().getContextObject(
				"security");

			if (o == null) {
				myLoc.infoT(myCat, METHOD, "can't get security context");
				return null;
			} else
				return o;
		} catch (NullPointerException e) {
			LoggingHelper.traceThrowable(Severity.INFO, myLoc, METHOD, e);
			myLoc.infoT(METHOD, "Cannot get SecurityContextObject");
			return null;
		}
	}

	public Subject getCurrentSubject() {
		final String METHOD = "getCurrentSubject";
		Method m = null;

		Object o = getSecurityContext();
		if (o == null)
			return null;

		try {
			m = o.getClass().getMethod("getSession", new Class[0]);
			if (m == null)
				throw new NoSuchMethodException("");
			// o2 ist jetzt ein SecuritySession
			Object o2 = m.invoke(o, new Object[0]);

			m = o2.getClass().getMethod("getSubject", new Class[0]);
			return (Subject) m.invoke(o2, new Object[0]);
		} catch (SecurityException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			myLoc.errorT(myCat, METHOD, "reflection calls failed");
		} catch (NoSuchMethodException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			myLoc.errorT(myCat, METHOD, "reflection calls failed");
		} catch (IllegalArgumentException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			myLoc.errorT(myCat, METHOD, "reflection calls failed");
		} catch (IllegalAccessException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			myLoc.errorT(myCat, METHOD, "reflection calls failed");
		} catch (InvocationTargetException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			myLoc.errorT(myCat, METHOD, "reflection calls failed");
		}
		return null;
	}

	public void invalidateSecuritySession() {
		try {
			Object context = getSecurityContext();
			Method toInvoke = context.getClass().getMethod("getSession", new Class[] {});
			Object session = toInvoke.invoke(context, new Object[] {});
			toInvoke = session.getClass().getMethod("setExpirationPeriod", new Class[] {long.class});
			toInvoke.invoke(session, new Object[] {new Long(0)});
		} catch (Exception ex) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, "invalidateSecuritySession", ex);
		}
	}

	public void logoutFromSecuritySession(String auth_config, CallbackHandler ch) {
		final String METHOD = "logoffFromSecuritySession";
		Method m = null;
		Exception ontheway = null;

		Object o = getSecurityContext();
		if (o == null)
			return;

		try {
			m = o.getClass().getMethod("getSession", new Class[0]);
			if (m == null)
				throw new NoSuchMethodException("");
			// o2 ist jetzt ein SecuritySession
			Object o2 = m.invoke(o, new Object[0]);

			m = o2.getClass().getMethod("logout", new Class[] { String.class, CallbackHandler.class });
			m.invoke(o2, new Object[] { auth_config, ch });
		} catch (SecurityException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			ontheway = e;
		} catch (NoSuchMethodException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			ontheway = e;
		} catch (IllegalArgumentException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			ontheway = e;
		} catch (IllegalAccessException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			ontheway = e;
		} catch (InvocationTargetException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			ontheway = e;
		}

		if (ontheway != null) {
			return;
		}
	}

	public Object getSecuritySession() {
		final String METHOD = "getSecuritySession";
		Method method = null;

		Object securityContext = getSecurityContext();

		if (securityContext != null) {
			try {
				//todo
				method = securityContext.getClass().getMethod("getLoginSession", new Class[0]);
				if (method == null) {
					throw new NoSuchMethodException("getLoginSession");
				}

				return method.invoke(securityContext, new Object[0]);
			} catch (SecurityException e) {
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			} catch (NoSuchMethodException e) {
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			} catch (IllegalArgumentException e) {
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			} catch (IllegalAccessException e) {
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			} catch (InvocationTargetException e) {
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			}
		}

		return null;
	}

	public void logoutFromSecuritySession(Object securitySession, String authConfig, CallbackHandler callbackHandler) {
		final String METHOD = "logoutFromSecuritySession";
		Method method = null;

		if (securitySession != null) {
			try {
				method = securitySession.getClass().getMethod("logout", new Class[] { String.class, CallbackHandler.class });
				method.invoke(securitySession, new Object[] {authConfig, callbackHandler});
			} catch (SecurityException e) {
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			} catch (NoSuchMethodException e) {
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			} catch (IllegalArgumentException e) {
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			} catch (IllegalAccessException e) {
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			} catch (InvocationTargetException e) {
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			}
		}
	}

	private void initializeTPD() throws Exception
	{
		String mn = "initializeTPD()";
		//tpd class loader handling

		final String defaultTPDClass = "com.sap.security.core.tpd.SimpleTPD";

		IUMConfiguration config = _platformTools.getConfiguration();
		String classLoaders = config.getStringStatic("ume.tpd.classloader", "");
		String tpdClass = config.getStringStatic("ume.tpd.imp.class", defaultTPDClass);
		if (tpdClass.equals(defaultTPDClass) && config.getStringStatic("ume.tpd.companies").trim().equals("0")) {
			try {
				IDataSourceMetaData[] allmetaData = PrincipalDatabagFactory.getInstance().getDataSourceMetaData();
				for (int i = 0; i < allmetaData.length; i++) {
					if (allmetaData[i].getClassName().equals(IR3Persistence.R3PERSISTENCE_CLASS_NAME)) {
						tpdClass = "com.sap.security.core.tpd.abap.R3GroupTPD";
						if (myLoc.beInfo())
							myLoc.infoT("Changing online the TPD implementation to com.sap.security.core.tpd.abap.R3GroupTPD as requested by configurtion");
						IUMConfigAdmin cadmin = _platformTools.getConfiguration();
						cadmin.setString("ume.tpd.imp.class", "com.sap.security.core.tpd.abap.R3GroupTPD");
						if (myLoc.beInfo())
							myLoc.infoT("Changing the property ume.tpd.imp.class to com.sap.security.core.tpd.abap.R3GroupTPD");
						cadmin.setString("ume.tpd.prefix","");
						if (myLoc.beInfo())
							myLoc.infoT("Changing the property ume.tpd.prefix to \"\"");
						cadmin.setString("ume.tpd.companies", "");
						if (myLoc.beInfo())
							myLoc.infoT("Changing the property ume.tpd.companies to \"\"");
						break;
					}
				}
			}
			catch (UMException e) {
				myLoc.traceThrowableT(Severity.ERROR, "Error when checking ABAP group - Java company integration", e);
				myLoc.infoT("Switching back to the standard implementation");
				tpdClass = "com.sap.security.core.tpd.SimpleTPD";
			}
		}

		StringTokenizer tokens = new StringTokenizer(classLoaders, ",");
		Class result = null;
		if (!tokens.hasMoreTokens())
		{
			result = loadClass(tpdClass, null);
		}

		while (tokens.hasMoreTokens())
		{
			try {
				String tpdloader = tokens.nextToken().trim();
				result = loadClass(tpdClass, tpdloader);
				if (result != null) {
					break;
				}
			} catch (ClassNotFoundException cnfe) {
				LoggingHelper.traceThrowable(Severity.DEBUG, myLoc, mn, cnfe);
				if (!tokens.hasMoreTokens()) {
					throw cnfe;
				}
			}
		}

		try
		{
			TradingPartnerDirectoryCommon.initialize(result);
		}
		catch (Exception e)
		{
			myCat.fatalT(myLoc, "Initialization of TPD failed. Check TPD configuration.");
			throw e;
		}
		// end of tpd classloader

		//add the company groups datasource if necessary
		try {
			if (
					UserAdminHelper.isDefaultTPDEnabled()
					||
					(
							UserAdminHelper.isCompanyConceptEnabled()
							&&
							UMFactory.getProperties().getBoolean(CompanyGroups.COMPANY_GROUPS_ENABLED,false)
					)
			)
			{
				if (TradingPartnerDirectoryCommon.getTPD() != null)
				{
					CompanyGroups companyGroupsDataSource = new CompanyGroups();
					companyGroupsDataSource.init(new DummyDSConfigurationModel(PrincipalDatabagFactory.getInstance().getConfiguration()));
					//TODO Clients?
					PrincipalDatabagFactory.getInstance().addDataSource(companyGroupsDataSource);
				}
				else
				{
					if (myLoc.beError())
					{
						myLoc.errorT("init","No TPD available because TradingPartnerDirectoryCommon.getTPD() returned null. CompanyGroups datasource will NOT be added.");
					}
				}
			}		
		}
		catch (PersistenceException pe)
		{
			if (myLoc.beError())
			{
				myLoc.errorT("init","No TPD available because TradingPartnerDirectoryCommon.getTPD() returned null. CompanyGroups datasource will NOT be added.");
			}		
		}
	}

	private Class loadClass(String className, String loaderName) throws ClassNotFoundException {
		String mn = "loadClass(String className, String loaderName)";
		if (loaderName == null || (loaderName.length() == 0)) {
			return UMEServiceFrame.class.getClassLoader().loadClass(className);
		}

		ClassLoader loader = null;
		LoadContext loadContext = getServiceContext().getCoreContext().getLoadContext();
		loader = loadContext.getClassLoader(loaderName);

		if (loader != null) {
			return loader.loadClass(className);
		}

		if (loaderName.startsWith(COMPONENT_COMMON)) {
			StringTokenizer loaderParser = new StringTokenizer(loaderName.substring(COMPONENT_COMMON.length()), ";");
			Class classInstance = null;

			while (loaderParser.hasMoreTokens() && classInstance == null) {
				try {
					classInstance = loadContext.getClassLoader(loaderParser.nextToken()).loadClass(className);

					if (classInstance != null) {
						return classInstance;
					}
				} catch (ClassNotFoundException cnfe) {
					LoggingHelper.traceThrowable(Severity.DEBUG, myLoc, mn, cnfe);
					continue;
				}
			}
		}

		return UMEServiceFrame.class.getClassLoader().loadClass(className);
	}

	public Principal getAuthenticatedPrincipal(){
		final String METHOD = "getAuthenticatedPrincipal";
		Method m = null;
		Object o = getSecurityContext();
		if (o == null)
			return null;

		try {
			m = o.getClass().getMethod("getSession", new Class[0]);
			if (m == null)
				throw new NoSuchMethodException("");
			// o2 is a SecuritySession
			Object o2 = m.invoke(o, new Object[0]);

			m = o2.getClass().getMethod("getPrincipal", new Class[0]);
			return (Principal) m.invoke(o2, new Object[0]);
			//may throw
			//(SecurityException e)
			//(NoSuchMethodException e)
			//(IllegalArgumentException e)
			//(IllegalAccessException e)
			//(InvocationTargetException e)
		} catch (Exception e) {
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, METHOD, e);
			myLoc.errorT(myCat, METHOD, "reflection calls failed");
		}
		return null;
	}

}
