package com.sap.security.core.server.ume.service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.interfaces.keystore.KeystoreManager;
import com.sap.security.api.IGroup;
import com.sap.security.api.IGroupFactory;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.api.UMRuntimeException;
import com.sap.security.api.umap.IUserMapping;
import com.sap.security.api.umap.IUserMappingData;
import com.sap.security.core.umap.imp.UserMapping;
import com.sap.security.core.util.Base64;
import com.sap.security.core.util.config.IUMConfigFull;
import com.sap.security.core.util.config.UMConfigurationException;
import com.sap.security.core.vault.SecretKeyGenerator;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

class KeyStoreEventListener implements ContainerEventListener {

    private static final String M_GENERATE_UMAP_KEY   = "generateUserMappingKey()";
    private static final String M_GET_UMAP_KEY        = "getUserMappingKey(KeystoreManager)";
    private static final String M_INIT_UMAP_KEY       = "initializeUserMappingKey(KeystoreManager)";
    private static final String M_INTERFACE_AVAILABLE = "interfaceAvailable(String, Object )";
    private static final String M_MIGRATE_UMAP_KEY    = "migrateUmapKeyFromProperties(KeystoreManager)";
    private static final String M_SET_UMAP_KEY_LOCK   = "setUmapKeyLockProperty()";

    private static final Location _loc = Location.getLocation(KeyStoreEventListener.class);

    private static final String KEYSTORE_API_INTERFACE  = "keystore_api"; 
    private static final String UME_KEYSTORE_VIEW       = "UMEKeystore";
    private static final String UMAP_KEY_ALIAS          = "UserMappingMasterKey";	  
    private static final String PROP_UMAP_KEY           = "com.sap.security.core.umap.key";
    private static final String PROP_UMAP_KEY_LOCK      = "ume.usermapping.key.protection";
    private static final String PROP_SECURE_RANDOM_ALGO = "ume.secure_random_algorithm";
    private static final String UMAP_KEY_LOCK           = "com.sap.security.core.umap.key.lock";
		
    private final static String LOCK_NAMESPACE = "[ume:ume.umap.key]";
    private final static String LOCK_ARGUMENT = "KEY_LOCK|createkey";	    
    
    private IUMConfigFull     _configuration   = null;
    private LockingContext    _lockingContext  = null;
    private KeystoreManager   _keystoreManager = null;

    private boolean         _handleUserMappingKey = false;
    private boolean         _userMappingKeyHandled = false;

    // Default constructor must not be used!
    private KeyStoreEventListener() { /* Must not be used. */ }
        
    public KeyStoreEventListener(IUMConfigFull configuration, LockingContext lockingContext) {
        _configuration  = configuration;
        _lockingContext = lockingContext;
    }

    /**
     * Tell the listener to initialize user mapping functionality of UME when the
     * keystore service is available.
     */
    public void enableUserMappingKeyHandling() {
        _handleUserMappingKey = true;

        // Perhaps the keystore manager is already available:
        // Try to initialize user mapping functionality immediately
        initializeUserMapping();
    }

    public void containerStarted() {
    	/* Nothing to do here. */
    }

    public void beginContainerStop() {
    	/* Nothing to do here. */
    }

    public void serviceStarted(@SuppressWarnings("unused") String arg0, @SuppressWarnings("unused") Object arg1) {	
    	/* Nothing to do here. */
    }

    public void serviceNotStarted(@SuppressWarnings("unused") String arg0) {
    	/* Nothing to do here. */
    }

    public void beginServiceStop(@SuppressWarnings("unused") String arg0) {
    	/* Nothing to do here. */
    }

    public void serviceStopped(@SuppressWarnings("unused") String arg0) {
    	/* Nothing to do here. */
    }

    // As soon as the keystore service is available, this method is called and
    // the user mapping master key is generated, if necessary, or retrieved
    // from UMEKeystore.
    public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
        if(interfaceName == null || interfaceImpl == null) {
            _loc.errorT(M_INTERFACE_AVAILABLE, "UME service event listener has been notified " +
                "about the availability of a new interface, but the interface name, the " +
                "implementing object or both are null. This is probably an error in the " +
                "service framework.\n" +
                "Interface name  : \"{0}\"\n" +
                "Interface object: \"{1}\"",
            new Object[] { interfaceName, interfaceImpl }); //$JL-SEVERITY_TEST$
            throw new UMRuntimeException("Received notification \"interfaceAvailable\" with " +
                "either the interface name or implementation object being null!");
        }
        else {
            if (interfaceName.equals(KEYSTORE_API_INTERFACE)) {
                if(interfaceImpl instanceof KeystoreManager) {
                    _loc.infoT(M_INTERFACE_AVAILABLE, "Keystore manager interface is available. " +
                        "Checking whether it is necessary to read a user mapping master key and " +
                        "perform respective initialization tasks."
                    );

                    _keystoreManager = (KeystoreManager) interfaceImpl;

                    // Access UMEKeystore and initialize user mapping functionality
                    // (this method checks whether there's anything to do on their own).
                    initializeUserMapping();
                }
                else {
                    UMEServiceFrame.myCat.warningT(_loc, "Could not connect to " +
                        "keystore service to retrieve the main key for UME user mapping " +
                        "functionality.\n" +
                        "User mapping will not be functional.\n" +
                        "Please open a trouble ticket for SAP support.");
      
                    if(_loc.beError()) {
                        // $JL-SEVERITY_TEST$
                        String msg = MessageFormat.format(
                            "The interface named \"{0}\" is not an instance of " +
                            "KeystoreManager! Cannot retrieve UME user mapping master key, so " +
                            "user mapping functionality will not work.",
                            new Object[] { KEYSTORE_API_INTERFACE }
                        );
                        _loc.errorT(UMEServiceFrame.myCat, M_INTERFACE_AVAILABLE, msg);
                    }
                }
            }
        }
    }

    private synchronized void initializeUserMapping() {
        // Only continue if the keystore manager is already available and the listener
        // should handle the user mapping key at all.
        //
        // This handle two scenarios:
        // - UME service already told this listener to handle the user mapping key. Now
        //   the keystore manager gets available which is signaled by the event manager
        //   calling interfaceAvailable().
        // - interfaceAvailable() has already been called when user mapping key handling
        //   had not been activated yet. Now someone called enableUserMappingKeyHandling().
        //
        // Note: By only checking which keystores/keystore entries to handle WHEN
        //       interfaceAvailable() is called, it is theoretically possible that this
        //       happens BEFORE enableUserMappingKeyHandling() is called, so the user
        //       mapping key would never been read and user mapping would never get
        //       initialized.

        if(_userMappingKeyHandled) {
            _loc.infoT(M_INIT_UMAP_KEY, "User mapping master key has already been read and " +
                "user mapping functionality of UME has already been initialized. Nothing " +
                "to do."
            );
            return;
        }
        if(_keystoreManager == null) {
            _loc.infoT(M_INIT_UMAP_KEY, "The keystore manager interface is not available yet. " +
                "Remembering that user mapping key initialization is necessary. This " +
                "will be performed as soon as the interface will be available."
            );
            return;
        }
        if(! _handleUserMappingKey) {
            _loc.infoT(M_INIT_UMAP_KEY, "The keystore manager interface is now available, but " +
                "handling of the main user mapping key has not been triggered yet. If " +
                "user mapping key initialization will be triggered later, it will be " +
                "performed then."
            );
            return;
        }

        // First try to get key without locking (normal case)
        _loc.infoT(M_INIT_UMAP_KEY, "Trying to retrieve user mapping master key from " +
          "keystore service (first try - usual case)..."
        );
        SecretKey umapKey = getUserMappingKey(_keystoreManager);
      
        // If no key could be retrieved, use locking (to be the only one in the
        // cluster performing these steps), try again to read (which should succeed
        // when entering the locked area if someone else created the key in the
        // meanwhile; which should fail if noone else created it and should prevent
        // all others from interfering during creation of a new key)
        if(umapKey == null) {
            ServerInternalLocking lock = null;
            try {
                // Set the lock
                lock = _lockingContext.createServerInternalLocking(LOCK_NAMESPACE, LOCK_ARGUMENT);                        
                lock.lock(LOCK_NAMESPACE, LOCK_ARGUMENT, LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE);
      
                // Try again to read the key from the keystore
                // (to be sure noone else wrote it in the meanwhile)
                _loc.infoT(M_INIT_UMAP_KEY, "Trying to retrieve user mapping master key " +
                    "from keystore service (second try - with lock, to guarantee " +
                        "consistency)..."
                );
                umapKey = getUserMappingKey(_keystoreManager);
      
                // Key still doesn't exist (in detail: not in keystore)
                if(umapKey == null) {
                    // Check for key in UME properties (migration from WebAS Java of
                	// NetWeaver 2004 SP9 or earlier).
                    umapKey = migrateUmapKeyFromProperties(_keystoreManager);
                }
      
                // Key still doesn't exist (in detail: not in properties)
                if(umapKey == null) {
                    // Generate and write a new key
                    _loc.infoT(M_INIT_UMAP_KEY, "No user mapping master key found in " +
                            "keystore. Generating and saving new key..."
                    );

                    try {
                        SecretKey key = generateUserMappingKey();                        
                        saveUserMappingKey(_keystoreManager, key);

                        // Make the key only available for UserMapping code if
                        // saving was successful --> Avoid encryption of data
                        // using a key that can't be retrieved later for decryption
                        umapKey = key;

                        if(_loc.beInfo()) {
                            _loc.infoT(M_INIT_UMAP_KEY, MessageFormat.format("New user mapping " +
                                "master key successfully generated and stored in keystore.",
                                new Object[] { UME_KEYSTORE_VIEW, UMAP_KEY_ALIAS }
                            ));
                        }
                    }
                    catch(NoSuchAlgorithmException e) {
                        _loc.traceThrowableT(Severity.ERROR, M_INIT_UMAP_KEY,
                            "Generation of new user mapping master key failed. User Mapping " +
                            "will not be functional until a key has been generated.", e);
                    }
                }
            }
            catch(LockException e) {
                String msg = "Could not set lock for access to UME UserMapping " +
                    "key in the keystore. UserMapping won't be functional.";
                _loc.traceThrowableT(Severity.ERROR, M_INIT_UMAP_KEY, msg, e);
            }
            catch(TechnicalLockException e) {
                String msg = "Could not set lock for access to UME UserMapping " +
                "key in the keystore. UserMapping won't be functional.";
                _loc.traceThrowableT(Severity.ERROR, M_INIT_UMAP_KEY, msg, e);
            }
            catch(IOException e) {
                String msg = MessageFormat.format("Either generating and " +
                    "saving a new User Mapping key or migrating an existing key in " +
                    "the keystore service (keystore view ''{0}'', " +
                    "keystore entry ''{1}'') failed. To avoid problems with " +
                    "undecryptable user mapping data, user mapping won''t be " +
                    "functional until all related problems have been solved.",
                    new Object[] { UME_KEYSTORE_VIEW, UMAP_KEY_ALIAS }
                );
                _loc.traceThrowableT(Severity.ERROR, M_INIT_UMAP_KEY, msg, e);
            }
            // Release the lock in any case (if it has been set)
            finally {
                if (lock != null) {
                    try {
                        lock.unlock(LOCK_NAMESPACE, LOCK_ARGUMENT, LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE);
                    }
                    catch(Exception e) {
                        String msg = "Couldn't release lock for user mapping key.";
                        _loc.traceThrowableT(Severity.ERROR, M_INIT_UMAP_KEY, msg, e);
                    }
                }
            }
        }
      
        // If we still don't have the user mapping key, it's time to write an
        // appropriate log message...
        if(umapKey == null) {
            UMEServiceFrame.myCat.warningT(_loc, "The main encryption key " +
                "for user mapping could neither be retrieved nor generated although " +
                "strong encryption functionality is available in this system.\n" +
                "User mapping will not be functional until this issue has been solved.\n" +
                "Please open a trouble ticket for SAP support."
            );
        }
        else {
            UserMapping.initialize(umapKey);
            // Success is traced in class UserMapping
        }
    }

    public void interfaceNotAvailable(@SuppressWarnings("unused") String interfaceName) {
    	/* Nothing to do here. */
    }

    public void markForShutdown(@SuppressWarnings("unused") long arg0) {
    	/* Nothing to do here. */
    }

    public boolean setServiceProperty(@SuppressWarnings("unused") String arg0, @SuppressWarnings("unused") String arg1) {
        return false;
    }

    public boolean setServiceProperties(@SuppressWarnings("unused") Properties arg0) {
        return false;
    }
    
	private void saveUserMappingKey(KeystoreManager keystoreManager, SecretKey key)
		throws IOException
    {
        final String method = "saveUserMappingKey(KeystoreManager, SecretKey)";

		_loc.entering(M_GET_UMAP_KEY);

		// Check User Mapping key lock
		try {
		  if(isUmapKeyLocked()) {
		 	if(isUmapKeyLockPropertySet()) {
				String msg = MessageFormat.format("User Mapping key lock is set. Overwriting " +
					"an existing key is prevented to avoid undecryptable mapping data. Usually, " +
					"this is a symptom for the User Mapping key having been deleted from " +
					"keystore view ''{0}'' or for the key being not accessible for some " +
					"reason.\n" +
					"If you are really sure you want a new key to be generated or an " +
					"existing key to be migrated to the keystore, please deactivate the lock by " +
					"setting UME property ''{1}'' to value ''FALSE'' and restart the server. " +
					"After the key has been written to the keystore, the lock will automatically " +
					"be enabled once again to protect the new key.",
					new Object[] { UME_KEYSTORE_VIEW, PROP_UMAP_KEY_LOCK }
				);
				UMEServiceFrame.myCat.warningT(_loc, msg);
				throw new IOException(msg);
			}

			UMEServiceFrame.myCat.warningT(_loc, MessageFormat.format("Protection of User Mapping key " +
					"has been temporarily disabled by setting UME property ''{0}'' to " +
					"''FALSE''. Allowing write access to the key this time. Protection will " +
					"be automatically enabled again after the key has been written.",
				new Object[] { PROP_UMAP_KEY_LOCK }
			));
		  }
		}
		catch(UMException e) {
			String msg = "Could not determine state of User Mapping key lock. Aborting write " +
					"access to user mapping key to avoid undecryptable mapping data.";
			_loc.traceThrowableT(Severity.ERROR, M_GET_UMAP_KEY, msg, e);
			throw new IOException(msg);
		}

		try {
			// Create keystore view, if necessary
			if(! keystoreManager.existKeystoreView(UME_KEYSTORE_VIEW)) {
				if(_loc.beInfo()) {
					_loc.infoT(M_GET_UMAP_KEY, MessageFormat.format("Keystore view ''{0}'' does not " +
						"yet exist; creating it...",
						new Object[] { UME_KEYSTORE_VIEW }
					));
				}
				keystoreManager.createKeystoreView(UME_KEYSTORE_VIEW, null);
			}
			KeyStore keystore = keystoreManager.getKeystore(UME_KEYSTORE_VIEW);

			// Make sure there is no existing keystore entry with the same alias
			if(keystore.containsAlias(UMAP_KEY_ALIAS)) {
                if(_loc.beError()) {//$JL-SEVERITY_TEST$
                    _loc.errorT(method, "Keystore entry with alias \"{0}\" in keystore view \"{1}\" " +
                        "already exists. Will not save the new user mapping key as this would " +
                        "overwrite the existing entry. This is to protect existing user mapping " +
                        "data.", new Object[] { UME_KEYSTORE_VIEW, UMAP_KEY_ALIAS } );
                }
                throw new IOException("Keystore entry already exists: " + UME_KEYSTORE_VIEW);
			}

			// Store the user mapping master key in the keystore
			keystore.setKeyEntry(UMAP_KEY_ALIAS, key, null, null);

			if(_loc.beInfo()) {
				_loc.infoT(M_GET_UMAP_KEY, MessageFormat.format("Successfully stored user mapping " +
						"master key under alias ''{0}'' in keystore view ''{1}''.",
					new Object[] { UMAP_KEY_ALIAS, UME_KEYSTORE_VIEW }
				));
			}

			// Activate User Mapping key lock and enable protection property
			try {
				setUmapKeyLock();
				setUmapKeyLockProperty(); // to avoid that admin forgets to enable protection
										  // after disabling it for overwriting the key

				if(_loc.beInfo()) {
					_loc.infoT(M_GET_UMAP_KEY, "User Mapping key protection has been enabled.");
				}
			}
			catch(UMConfigurationException e) {
				keystore.deleteEntry(UMAP_KEY_ALIAS);
				String msg = MessageFormat.format("Could not enable User Mapping key " +
						"protection by setting UME property ''{0}''. Deleted written key " +
					"to avoid potential user mapping decryption problems.",
					new Object[] { PROP_UMAP_KEY_LOCK }
				);
				_loc.traceThrowableT(Severity.ERROR, M_GET_UMAP_KEY, msg, e);
				throw new IOException(msg);
			}
			catch(UMException e) {
				keystore.deleteEntry(UMAP_KEY_ALIAS);
				String msg = "Could not set User Mapping key lock. Deleted written key " +
						"to avoid potential user mapping decryption problems.";
				_loc.traceThrowableT(Severity.ERROR, M_GET_UMAP_KEY, msg, e);
				throw new IOException(msg);
			}
		}
		catch (RemoteException e) {
			String msg = MessageFormat.format("Access to keystore view ''{0}'' failed: {1}",
				new Object[] { UME_KEYSTORE_VIEW, e }
			);
			_loc.traceThrowableT(Severity.ERROR, M_GET_UMAP_KEY, msg, e);
			throw new IOException(msg);
		}
		catch (KeyStoreException e) {
			String msg = MessageFormat.format("Access to entry ''{0}'' in keystore view " +
					"''{1}'' failed: {2}",
				new Object[] { UMAP_KEY_ALIAS, UME_KEYSTORE_VIEW, e }
			);
			_loc.traceThrowableT(Severity.ERROR, M_GET_UMAP_KEY, msg, e);
			throw new IOException(msg);
		}

		_loc.exiting();
	}

	// - If key is found and migration succeeds, returns the key.
	// - If key is not found, returns null.
	// - If errors occur (i.e. it's not clear whether there is an existing key or not),
	//   throws IOException.
	private SecretKey migrateUmapKeyFromProperties(KeystoreManager keystoreManager)
	throws IOException {
		SecretKey key = null;

		char[] encodedKey = _configuration.getSecurePropertyStatic(PROP_UMAP_KEY);
		if(encodedKey == null || encodedKey.length == 0) {
			if(_loc.beInfo()) {
				_loc.infoT(M_MIGRATE_UMAP_KEY, MessageFormat.format("No existing user mapping key " +
					"found in UME properties (property ''{0}''). No key migration possible.",
					new Object[] { PROP_UMAP_KEY }
				));
			}
		}
		else { // Seems we really have a valid key here
			byte[] keyBytes = Base64.decode(encodedKey);
			if(keyBytes == null) {
				String msg = MessageFormat.format("Could not decode user mapping " +
					"master key from (expected) Base64 encoded string provided " +
					"with UME properties (property ''{0}'').",
					new Object[] { PROP_UMAP_KEY }
				);
				_loc.errorT(M_MIGRATE_UMAP_KEY, msg);
				throw new IOException(msg);
			}

			key = new SecretKeySpec(keyBytes,
				System.getProperties().getProperty(
					IUserMapping.UMAP_ENCRYPTION_KEYTYPE_ALIAS, "DESede"
				)
			);
			if(key == null) {
				String msg = MessageFormat.format("Could not create SecretKeySpec " +
					"object from user mapping master key data retrieved from UME " +
					"properties (property ''{0}'').",
					new Object[] { PROP_UMAP_KEY }
				);
				_loc.errorT(M_MIGRATE_UMAP_KEY, msg);
				throw new IOException(msg);
			}

			// We finally got a valid key; now save in the keystore
			_loc.infoT(M_MIGRATE_UMAP_KEY, "Successfully retrieved User Mapping key from UME " +
				"properties for migration to keystore."
			);

			saveUserMappingKey(keystoreManager, key);

			_loc.infoT(M_MIGRATE_UMAP_KEY, "Successfully migrated User Mapping key from UME " +
				"properties to keystore."
			);
		}

		_loc.exiting();

		return key;
	}

	private SecretKey generateUserMappingKey() throws NoSuchAlgorithmException {
		ConfigurationHandler configHandler = null;
		SecretKey            key           = null;

		try {
			String randomAlgo = _configuration.getStringStatic(PROP_SECURE_RANDOM_ALGO);
			String encryptionAlgo = System.getProperties()
				.getProperty(IUserMapping.UMAP_ENCRYPTION_KEYTYPE_ALIAS, "DESede");

			key = SecretKeyGenerator.createRandom(randomAlgo, encryptionAlgo);
		}
		finally {
			if (configHandler != null) {
				try {
					configHandler.closeAllConfigurations();
				}
				catch(ConfigurationException ce) {
                    String msg = "Error while closing UME configuration: " + ce.toString();
					IOException ioe = new IOException(msg);
					_loc.traceThrowableT(Severity.ERROR, M_GENERATE_UMAP_KEY, msg, ioe);
				}
			}
		}

		_loc.exiting();

		return key;
	}

	private SecretKey getUserMappingKey(KeystoreManager keystoreManager) {
		// "Services and libraries all have code based permissions by default"
		// --> No need to get special permissions for this JAR

		SecretKey umapKey = null;
		Throwable exc     = null;

		try {
			// Get keystore view
			if(! keystoreManager.existKeystoreView(UME_KEYSTORE_VIEW)) {
				if(_loc.beInfo()) {
					_loc.infoT(M_GET_UMAP_KEY, MessageFormat.format("Keystore view ''{0}'' does not " +
						"exist, so user mapping master key could not be retrieved.",
						new Object[] { UME_KEYSTORE_VIEW }
					));
				}
				return null;
			}
			KeyStore keystore = keystoreManager.getKeystore(UME_KEYSTORE_VIEW);

			// Get key from keystore
			if(! keystore.containsAlias(UMAP_KEY_ALIAS)) {
				if(_loc.beInfo()) {
					_loc.infoT(M_GET_UMAP_KEY, MessageFormat.format("Keystore view ''{0}'' does not " +
						"contain user mapping master key (keystore entry alias ''{1}'').",
						new Object[] { UME_KEYSTORE_VIEW, UMAP_KEY_ALIAS }
					));
				}
				return null;
			}
			if(! keystore.isKeyEntry(UMAP_KEY_ALIAS)) {
                if(_loc.beError()) {//$JL-SEVERITY_TEST$
                    _loc.errorT("Keystore entry with alias \"{0}\" in keystore view \"{1}\" is not a key.",
                        new Object[] { UMAP_KEY_ALIAS, UME_KEYSTORE_VIEW } );
                }

				return null;
			}
			Key key = keystore.getKey(UMAP_KEY_ALIAS, null);

			if(key instanceof SecretKey) {
				umapKey = (SecretKey) key;
				if(_loc.beInfo()) {
					_loc.infoT(M_GET_UMAP_KEY, MessageFormat.format("Successfully retrieved SecretKey " +
						"object aliased ''{0}'' from keystore view ''{1}''.",
						new Object[] { UMAP_KEY_ALIAS, UME_KEYSTORE_VIEW }
					));
				}
			}
			else {
                if(_loc.beError()) {//$JL-SEVERITY_TEST$
                    _loc.errorT("The key with alias \"{0}\" read from keystore view \"{1}\" " +
                        "is not a SecretKey object, but an object of type \"{2}\". User " +
                        "Mapping needs a SecretKey object to work and will not be functional.",
                            new Object[] { UMAP_KEY_ALIAS, UME_KEYSTORE_VIEW, key.getClass().getName() } );
                }
			}
		}
		catch (RemoteException e) {
			exc = e;
		}
		catch (KeyStoreException e) {
			exc = e;
		}
		catch (NoSuchAlgorithmException e) {
			exc = e;
		}
		catch (UnrecoverableKeyException e) {
			exc = e;
		}

		if(exc != null) {
            if(_loc.beError()) {
                // $JL-SEVERITY_TEST$
                String msg = MessageFormat.format("An error occurred while reading keystore " +
                    "entry aliased ''{0}'' from keystore view ''{1}''.",
                    new Object[] {
                        UMAP_KEY_ALIAS, UME_KEYSTORE_VIEW
                    }
                );
                _loc.traceThrowableT(Severity.ERROR, M_GET_UMAP_KEY, msg, exc);
            }
		}

		return umapKey;
	}

	private boolean isUmapKeyLocked() throws UMException {
		IGroup everyone = UMFactory.getGroupFactory().getGroup(IGroupFactory.EVERYONE_UNIQUEID);
		Object o = everyone.getAttribute(IUserMappingData.USER_MAPPING_NAMESPACE, UMAP_KEY_LOCK);
		return (o != null);
	}

	private void setUmapKeyLock() throws UMException {
		IGroup everyone = UMFactory.getGroupFactory()
			.getMutableGroup(IGroupFactory.EVERYONE_UNIQUEID);
		everyone.setAttribute(IUserMappingData.USER_MAPPING_NAMESPACE,
			UMAP_KEY_LOCK, new String [] { "locked" }
		);
		everyone.commit();
	}

	private boolean isUmapKeyLockPropertySet() {
		return _configuration.getBooleanStatic(PROP_UMAP_KEY_LOCK, true);
	}

	private void setUmapKeyLockProperty() throws UMConfigurationException {
        _configuration.setBoolean(PROP_UMAP_KEY_LOCK, true);
    	_loc.infoT(M_SET_UMAP_KEY_LOCK, "User Mapping key protection property has been set.");
	}    

}
