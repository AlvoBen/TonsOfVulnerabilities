package com.sap.security.api.umap.system;

/**
 * Provides access to all registered system landscapes and allows (un)registration
 * of system landscapes.
 * 
 * <p>
 * The current instance of this interface can be retrieved by calling
 * {@link com.sap.security.api.UMFactory#getSystemLandscapeFactory()}.
 * </p>
 */
import java.util.List;

public interface ISystemLandscapeFactory {

	/**
	 * Retrieve the list of all {@link com.sap.security.api.umap.system.ISystemLandscape}
     * implementations that are currently registered.
	 */
	public List<ISystemLandscape> getAllLandscapes();

	/**
	 * Retrieve the system landscape for the specified type.
	 * 
	 * @param type The required landscape type
	 * @return The system landscape registered for the specified type or <code>null</code>
	 *         if there is no registered landscape for the type.
	 */
	public ISystemLandscape getLandscape(String type);

	/**
	 * For internal use only: Retrieve the system landscape for the specified
	 * storage prefix.
	 * 
	 * @param storagePrefix The required storage prefix
	 * @return The system landscape registered for the specified storage prefix or
	 *         <code>null</code> if there is no registered landscape for the type.
	 */
	public ISystemLandscape getLandscapeByStoragePrefix(String storagePrefix);

	/**
	 * Register a system landscape.
	 * 
	 * <p>
	 * The landscape type is taken from its {@link ISystemLandscape#getType()} method.
	 * Registering is only possible if no system landscape has been registered for the
	 * same type.
	 * </p>
	 * @param landscape The system landscape to register.
	 */
	public void registerLandscape(ISystemLandscape landscape);

	/**
	 * Unregister a registered system landscape.
     * 
	 * @param landscape The system landscape to unregister.
	 */
	public void unregisterLandscape(ISystemLandscape landscape);

}
