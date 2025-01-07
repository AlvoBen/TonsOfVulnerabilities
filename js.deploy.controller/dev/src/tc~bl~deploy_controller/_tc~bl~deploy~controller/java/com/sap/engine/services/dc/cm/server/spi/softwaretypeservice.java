package com.sap.engine.services.dc.cm.server.spi;

import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.repo.SoftwareType;

/**
 * 
 * Title: J2EE Deployment Team Description: Describes the semantics of the SW
 * types eg. which type is offline, online etc
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface SoftwareTypeService extends ServerService {

	static final String FS_TYPE = "isFSType";
	static final String DB_TYPE = "isDBType";
	static final String APPLICATION_TYPE = "isApplicationType";
	static final String JEE_SOFTWARE_TYPE = "isJEESoftwareType";
	static final String SUPPORTED_FOR_UNDEPLOY = "isSupportedForUndeploy";
	static final String STAND_ALONE = "isStandAlone";
	static final String STAND_ALONE_BUT_NOT_APPLICATION = "isStandAloneButNotApplication";
	static final String ROLLING = "isRolling";
	static final String SUPPORT_SAFE_MODE = "doesSupportSafeMode";
	static final String JEE_APPLICATION = "isJEEApplication";
	static final String WAR = "isWar";
	static final String APPLICATION_DELIVERY = "isApplicationDelivery";
	static final String LIBRARY_DELIVERY = "isLibraryDelivery";
	static final String DB_SCHEMA = "isDBSchema";
	static final String DB_CONTENT = "isDBContent";
	static final String EXTERNAL_OFFLINE_COMPONENT_TYPE = "externalOfflineComponentType";
	static final String EXTRAMILE_DEPLOYMENT_PRIORITY = "extramileDeploymentPriority";

	// slots
	static final String OFFLINE_SLOT = "Offline";
	static final String ONLINE_SLOT = "Online";
	static final String POST_ONLINE_SLOT = "PostOnline";

	/**
	 * @return <code>OnlineOfflineSoftwareType</code> which represents whether
	 *         the components with "online" or "offline" software type have to
	 *         be deployed first.
	 */
	public OnlineOfflineSoftwareType getFirstDeployedSoftwareType();

	/**
	 * @return <code>OnlineOfflineSoftwareType</code> which represents whether
	 *         the components with "online" or "offline" software type have to
	 *         be undeployed first.
	 */
	public OnlineOfflineSoftwareType getFirstUndeployedSoftwareType();

	/**
	 * 
	 * @return <code>Set</code> with all the software types which are not
	 *         supported for undeployment.
	 */
	public Set<SoftwareType> getUnsupportedForUndeploySoftwareTypes();

	/**
	 * Gets the software types which require the Engine to be offline for
	 * deployment. Note that a software type which is not returned by this
	 * method may also be deployed offline as long as it is not explicitely
	 * declared as an online software type.
	 */
	public Set<SoftwareType> getOfflineSoftwareTypes();

	/**
	 * Gets the software types which require the Engine to be online for
	 * deployment. Note that a software type which is not returned by this
	 * method may also be deployed online as long as it is not explicitely
	 * declared as an offline software type.
	 */
	public Set<SoftwareType> getOnlineSoftwareTypes();

	/**
	 * Use this method to obtain the software types which require that the
	 * applications are started ( eg content deployment )
	 * 
	 * @return the post online software types
	 */
	public Set<SoftwareType> getPostOnlineSoftwareTypes();

	/**
	 * @return <code>Set</code> with the software types which are applicable for
	 *         J2EE applications.
	 */
	public Set<SoftwareType> getApplicationSoftwareTypes();

	/**
	 * @return <code>Set</code> with the software types which are related to
	 *         file system components.
	 */
	public Set<SoftwareType> getFSSoftwareTypes();

	/**
	 * @return <code>Set</code> with the software types which are related to
	 *         database components.
	 */
	public Set<SoftwareType> getDBSoftwareTypes();

	/**
	 * @return <code>Set</code> with the software types which are related to
	 *         J2EE components.
	 */
	public Set<SoftwareType> getJ2EESoftwareTypes();

	/**
	 * @return <code>Set</code> with the software types that are suitable for
	 *         rolling workflow strategy
	 */
	public Set<SoftwareType> getRollingSoftwareTypes();

	/**
	 * 
	 * @return the softwre types that can be deployed in safe mode
	 */
	public Set<SoftwareType> getSafeModeSoftwareTypes();

	/**
	 * A given software type is allowed to depend on only certains software
	 * types.
	 * 
	 * @param type
	 *            the software type which dependencies you want to obtain
	 * @return a set of software types that this software type could depend on
	 */
	public Set<SoftwareType> getAllowedDependencies(SoftwareType type);

	/**
	 * Check software type a is allowed to depend on type b
	 * 
	 * @param a
	 *            the software type that has declared a dependency
	 * @param b
	 *            the software type on which the first one depends
	 * @return true if type a is allowed to declare dependency on type b
	 */
	public boolean isDependencyAllowed(SoftwareType a, SoftwareType b);

	public Set<SoftwareType> getSoftwareTypesByAttribute(String stAttribute);

	public Map<String, String> getSoftwareTypeAttributes(
			SoftwareType softwareType);

	public Set<SoftwareType> getSoftwareTypesBySlot(String slot);

	public String getNextSlot(String slot);

	public String getFirstSlot();

	public boolean isSupported(SoftwareType type);
}
