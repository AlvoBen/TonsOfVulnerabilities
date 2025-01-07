package com.sap.security.api;

import java.util.Map;

/**
 * The principal factory provides means to
 * <ol>
 *   <li>instantiate principal objects,</li>
 *   <li>create new principal (possibly by copying the data of an existing one),</li>
 *   <li>delete principals,</li>
 *   <li>search for principals based on different criteria and</li>
 *   <li>get meta-data for custom principal objects.</li>
 * </ol>
 * 
 * <p><b>NOTE</b>:  As this interface�can be extended, this interface can be freely used, 
 * but must not be implemented.
 * 
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
 
public interface IPrincipalFactory extends IConfigurable
{
    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IPrincipalFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

    public static final String IUSER                         = "IUser";
    public static final String IUSERACCOUNT                  = "IUserAccount";
    public static final String IGROUP                        = "IGroup";
    public static final String IROLE                         = "IRole";
    public static final String IPRINCIPAL                    = "IPrincipal";
    public static final String IPRINCIPALSET                 = "IPrincipalSet";

    /**
     * @deprecated
     */
    public static final String ICUSTOM_OBJECT                = IPRINCIPAL;


	/**
	 * get the data type out of a principal's uniqueId. Possible values are
     * {@link #IUSER}, {@link #IUSERACCOUNT}, 
     * {@link #IGROUP}, {@link #IROLE},
     * {@link #IPRINCIPAL}, {@link #IPRINCIPALSET}
     * 
	 * @param uniqueId The unique id of a principal
	 * @return String The data type of the principal
	 * @throws UMException
	 */
    public String getPrincipalType(String uniqueId) throws UMException;
    

	/**
	 * Get a principal by using its id. Throws exception, if the principal doesn't exist.
     	 * 
	 * @param uniqueId The unique id of the principal
	 * @return IPrincipal The principal object
	 * @throws UMException
	 */
    public IPrincipal getPrincipal(String uniqueId) throws UMException;

	/**
	 * Get a principal by using its uniqueId and the populateAttributes 
	 * which should be populated. 
         * Throws exception, if the principal doesn't exist.
         * 
	 * @param uniqueId The unique id of the principal
	 * @param populateAttributes The attributes which should be populated
	 * @return IPrincipal The principal object
	 * @throws UMException
	 */
    public IPrincipal getPrincipal(String uniqueId,
        AttributeList populateAttributes) throws UMException;

    /**
     * Checks whether at least one attribute of the principal with the given uniqueId
     * is modifiable. Returns true if at least one attribute is modifiable, false otherwise.
     * Throws exception, if an error occurs during the check.
     * 
     * @param uniqueId The unique id of the principal
     * @return boolean The result of the check
     * @throws UMException
     */
    public boolean isPrincipalModifiable(String uniqueId) throws UMException;

    /**
     * Checks whether the principal with the given uniqueId can be deleted.
     * Returns true if the principal can be deleted, false otherwise.
     * Throws exception, if a error occurs during the ceck.
     * 
     * @param uniqueId The unique id of the principal
     * @return boolean The result of the check
     * @throws UMException
     */
    public boolean isPrincipalDeletable(String uniqueId) throws UMException;

    /**
     * Checks whether a specific attribute of the given principal is modifiable. 
     * Returns true if the attribute is modifiable, false otherwise.
     * Throws exception, if a error occurs during the ceck.
     * 
     * @param principal The principal
     * @param namespace The namespace of the attribute
     * @param attributename The name of the attribute
     * @return boolean The result of the check
     * @throws UMException
     */
    public boolean isPrincipalAttributeModifiable(IPrincipal principal, String namespace, String attributename) throws UMException;
    
    /**
     * Checks whether a specific attribute of the principal with the given uniqueId
     * is modifiable. Returns true if the attribute is modifiable, false otherwise.
     * Throws exception, if a error occurs during the ceck.
     * 
     * @param uniqueId The unique id of the principal
     * @param namespace The namespace of the attribute
     * @param attributename The name of the attribute
     * @return boolean The result of the check
     * @throws UMException
     */
    public boolean isPrincipalAttributeModifiable(String uniqueId, String namespace, String attributename) throws UMException;


/**
 * Gets the objects for multiple unique IDs
 * @param uniqueIDs array of uniqueIDs which are used to identify the principal objects
 * @exception NoSuchPrincipalException if one or more of the given unique IDs
 * are not assigned to any object
 * @return IPrincipal[] An array of IPrincipal objects.
 */
    public IPrincipal[] getPrincipals (String[] uniqueIDs)
        throws NoSuchPrincipalException;

/**
 * Gets the objects for multiple unique IDs and populates all attributes in populateAttributes
 * @param uniqueIDs array of uniqueIDs which are used to identify the principal objects
 * @param populateAttributes {@link com.sap.security.api.AttributeList} 
 * @return IPrincipal[] An array of IPrincipal objects.
 * @exception NoSuchPrincipalException if one or more of the given unique IDs
 * are not assigned to any object
 */
    public IPrincipal[] getPrincipals (String[] uniqueIDs, AttributeList populateAttributes)
        throws NoSuchPrincipalException;

/**
 * Creates a new, initially blank object. The unique ID for the new object will
 * be generated by the object factory. The principalTypeIdentifier has to have exactly
 * 4 uppercase letters (A-Z), whereas identifiers starting with X-Z are reserved for 
 * customers, and identifiers starting with A-W are reserved for SAP. Allowed are all 
 * values except USER, ROLE, GRUP, TEAM, UACC, OOOO and COMP. After
 * setting the appropriate data via IPrincipalMaint's set-methods, the object object
 * must be commited to the object store via {@link IPrincipalMaint#commit()}.
 *
 * Note: don't forget to add this type to the data source configuration. One data source
 * has to be home for this object type
 *
 * @param principalTypeIdentifier 4 letter Identifier for this new principal 
 * @return IPrincipalMaint A principal object implementing IPrincipalMaint.     
 */
    public IPrincipalMaint newPrincipal (String principalTypeIdentifier);

/**
 * Creates a new, initially blank object. The unique ID for the new object will
 * be generated by the object factory. For details about the principalTypeIdentifier
 * see {@link #newPrincipal(String)} After setting the appropriate data via 
 * IPrincipalMaint's set-methods, the object object must be commited to the object 
 * store via {@link IPrincipalMaint#commit()}.
 *
 * Note: don't forget to add this type to the data source configuration. One data source
 * has to be home for this object type
 * @param principalTypeIdentifier 4 letter Identifier for this new principal 
 * @return IPrincipalSet The new object implementing IPrincipalSet
 */
    public IPrincipalSet newPrincipalSet (String principalTypeIdentifier);

/**
 * Creates a new object. The existing object
 * <code>copyFrom</code> will be used as a template, i.e. some (but not
 * necessarily all) attributes will be copied to the new object. The unique
 * ID for the new object will be generated by the object factory.
 * After setting the appropriate data via set-methods, the principal object
 * must be commited to the principal store via {@link IPrincipalMaint#commit()}. 
 * @param copyFrom The object which is used as a blueprint
 * @return IPrincipalSet The new object implementing IPrincipalSet
 */
    public IPrincipalSet newPrincipalSet (IPrincipalSet copyFrom);


/**
 * Creates a new object. The existing object
 * <code>copyFrom</code> will be used as a template, i.e. some (but not
 * necessarily all) attributes will be copied to the new object. The unique
 * ID for the new object will be generated by the object factory.
 * After setting the appropriate data via set-methods, the principal object
 * must be commited to the principal store via {@link IPrincipalMaint#commit()}. 
 * @param copyFrom The object which is used as a blueprint
 * @return IPrincipalMaint The new object implementing IPrincipalMaint
 */
    public IPrincipalMaint newPrincipal (IPrincipal copyFrom);


/**
 * Delete a object from the use store
 * @param uniqueID The unique id of the principal
 * @exception UMException if the object can't be deleted
 */
    public void deletePrincipal (String uniqueID)
        throws UMException;


/**
 * Delete objects from the principal store
 * @param uniqueIDs The unique ids of the principals
 * @exception UMException if the objects can't be deleted
 */
	public void deletePrincipals (String[] uniqueIDs)
		throws UMException;


/**
 * Commit the changes applied to a set of <code>objects</code> to the object
 * store in one pass. Depending on the object factory's implementation, this
 * will result in better performance than calling {@link IPrincipalMaint#commit()}
 * on each object object individually.
 * Note that either commiting or rolling back changes will be
 * required to unlock objects if the object factory employs pessimistic
 * locking.
 * @param objects Objects which should be stored to the data store
 * @throws UMException if one or more of the IPrincipalMaint objects cannot
 * be stored successfully.
 */
    public void commitPrincipals (IPrincipalMaint[] objects)
        throws UMException;


    /**
     * Roll back (i.e. discard) the changes applied to a set of <code>objects
     * </code>.
     * Note that either commiting or rolling back changes will be
     * required to unlock objects if the object factory employs pessimistic
     * locking.
     * @param objects Objects which should be stored to the data store
     * @throws UMException if one or more of the IPrincipalMaint objects cannot
     * be discarded successfully.
     */
    public void rollbackPrincipals (IPrincipalMaint[] objects) throws UMException;


   /**
    * Search for objects in the objects store which match the criteria specified in the<p>
    * given <code>filter</code>. In order to get a principal search filter use  
    * {@link #getPrincipalSearchFilter(boolean,String)}.
    * You can define a search filter using methods of {@link IPrincipalSearchFilter}.    
    * @param filter filter defined to search for principals
    * @return ISearchResult The result of the search
    **/
    public ISearchResult searchPrincipals (IPrincipalSearchFilter filter) throws UMException;


   /**
    * Batch save, the data is not made permanent until commitObjects() is called
    * @param objects objects which should be saved
    */
  public void savePrincipals(IPrincipalMaint[] objects) throws UMException;

   /**
    * Creates new, initially blank object objects. The unique ID for the new object will
    * be generated by the object factory. After
    * setting the appropriate data via IPrincipalMaint's set-methods, each object object
    * must be saved and commited to the object store via {@link IPrincipalMaint#commit()}. 
    * For batch save and commit {@link #savePrincipals(IPrincipalMaint[])} and 
    * {@link #commitPrincipals(IPrincipalMaint[])}
    * @param principalTypeIdentifier The principal type identifier 
    * @param  num   number of new blank objects requested
    */
  public IPrincipalMaint[] newPrincipals(String principalTypeIdentifier, int num);

  /**
    * Get a object which can be modified. This method returns an object
    * which implements
    * <code>IPrincipalMaint</code> interface which contains the corresponding
    * set-methods.
    * @param uniqueId The unique id of a principal
    * @return IPrincipalMaint The principal object which can be modified
    * @throws UMException if no modifiable object can be provided.
    */
  public IPrincipalMaint getMutablePrincipal(String uniqueId) throws UMException;

  /**
    * Get the principal type identifier for this custom object. This method returns a string
    * which represents the type identifier of the object or throws a NoPrincipalException
    * if the given id is a id of a ume object like IUser or IGroup etc.
    * 
    * @param uniqueId the unique id of the object
    * @return the principal type identifier
    */
  public String getPrincipalTypeIdentifier(String uniqueId) 
    throws UMException;


  /**
    *  Returns an IPrincipalSearchFilter object to be used to specify query attributes
    * <p>
    * IPrincipalSearchFilter only contains attributes which will be queried
    * @param   orMode used to define the logical operator of the searched
    * attributes. If orMode is <code>true</code> the searchfilter will combine the 
    * specified attributes with the logical OR operator. If orMode is 
    * <code>false</code> the defined search attributes are combined with the logical
    * AND operator.
    *   	
    * @param   principalTypeIdentifier specifies the type of the principal. Examples
    * of reserved principal types are "USER", "ROLE" or "GRUP". For details about the 
    * principalTypeIdentifier see {@link #newPrincipal(String)}   
    *
    * @return     IPrincipalSearchFilter
    * @exception  UMException
    * @exception  FeatureNotAvailableException 
    */
  public IPrincipalSearchFilter getPrincipalSearchFilter(boolean orMode, String principalTypeIdentifier) throws UMException;

    /**
     * assign customObject with customObjectId to parent-customOjbect with parentPrincipalId. Implicitly
     * a commit is done if you call this method.
     *
     * @param   customObjectId    uniqueId of Principal
     * @param   parentPrincipalId uniqueId of the parent Principal
     * @exception   UMException if data cannot be stored successfully
     */
  public void addPrincipalToParent(String customObjectId, String parentPrincipalId) throws UMException;

    /**
     * unassign customObject with customObjectId to parent-customOjbect with parentPrincipalId. Implicitly
     * a commit is done if you call this method.
     *
     * @param   customObjectId    uniqueId of Principal
     * @param   parentPrincipalId uniqueId of the parent Principal
     * @exception   UMException if data cannot be stored successfully
     */
  public void removePrincipalFromParent(String customObjectId, String parentPrincipalId) throws UMException;

  // -----------------------------
  // Register/UnRegister observers ---------------------------------------------
  // -----------------------------
  /**
   * registerListener allows to subscribe to a predefined eventName
   * {@link PrincipalListener}
   * The caller has to provide a receiver object which implements PrincipalListener
   * @param objectListener object which should be registered
   * @param modifier constant defined in {@link PrincipalListener}
   */
  public void registerListener( PrincipalListener objectListener, int modifier);

  
  /**
   * registerListener allows to subscribe to a predefined eventName
   * {@link PrincipalListener}
   * The caller has to provide a receiver object which implements PrincipalListener
   * @param objectListener object which should be registered
   * @param modifier constant defined in {@link PrincipalListener}
   * @param notifyAfterPhysicalCommitCompleted Allows callers when set to false, to get a notification before the physical transaction is completed in order to include their actions into the same physical transaction.
   */
  public void registerListener( PrincipalListener objectListener, int modifier, boolean notifyAfterPhysicalCommitCompleted);

  /***
   * unregisterListener unsubscribes a receiver from a previously subscribed event.
   * @param objectListener object which should be unregistered   
   */
  public void unregisterListener( PrincipalListener objectListener);

/**
 * Returns a new IPrincipalMetaData object, or throws a ObjectAlreadyExistsException,
 * if there is already a IPrincipalMetaData object for the given principalTypeIdentifier.
 * The IPrincipalMetaData object will NOT become persistent until it is registered.
 * 
 * @param principalTypeIdentifier The {@link #newPrincipal(String) principalTypeIdentifier} which is described by the new IPrincipalMetaData object
 * @param principalType The semantic principal type, e.g. IPrincipalMetaData.IPrincipalSet or IPrincipalMetaData.IPrincipal
 * @return IPrincipalMetaData the new object
 * @throws ObjectAlreadyExistsException if there is already a object for the given principal type
 */
  public IPrincipalMetaData newPrincipalMetaData(String principalTypeIdentifier, int principalType) throws UMException;
        
/**
 * Registers the given IPrincipalMetaData object. After the registration the IPrincipalMetaData
 * object is visible for other applications and returned by the method getAvailablePrincipalMetaData().
 * This method modifies a already existing IPrincipalMetaData object which has the same
 * {@link #newPrincipal(String) principal type identifier} as the given one. The object becomes persistent, and will also be
 * available after the restart of the system.
 * 
 * @param metadata The IPrincipalMetaData object that should be registered
 */
  public void registerPrincipalMetaData(IPrincipalMetaData metadata) throws UMException; 
  
/**
 * Deregisters the IPrincipalMetaData object. After the deregistration the IPrincipalMetaData
 * object is no longer visible for all applications and is no longer returned by the method 
 * getAvailablePrincipalMetaData().
 * @param principalTypeIdentifier The {@link #newPrincipal(String) principal type identifier}.
 */
  public void deregisterPrincipalMetaData(String principalTypeIdentifier) throws UMException;     
  
/**
 * Get all available IPrincipalMetaData objects. The returned array is empty, if no
 * IPrincipalMetaData object is available.
 * 
 * @return IPrincipalMetaData[] The list of available IPrincipalMetaData objects
 */
  public IPrincipalMetaData[] getAvailablePrincipalMetaData() throws UMException;
  
/**
 * Get a IPrincipalMetaData object for the given {@link #newPrincipal(String) principal type identifier}. If no 
 * IPrincipalMetaData object is registered for the given {@link #newPrincipal(String) principal type identifier}, a
 * NoSuchObjectException is thrown.
 * 
 * @param principalTypeIdentifier The {@link #newPrincipal(String) principal type identifier}
 * @return IPrincipalMetaData The found IPrincipalMetaData object
 * @throws NoSuchObjectException If no IPrincipalMetaData object is registered for the given principal type identifier
 */
  public IPrincipalMetaData getPrincipalMetaData(String principalTypeIdentifier) throws NoSuchObjectException, UMException;
 
  /**
   *  Adds a new datasource dynamically to IPrincipalFactory. 
   *  An object for the new data source will be instantiated and
   *  initialized by an XML formatted file. This file contains information 
   *  about the principal datatype and the relevant implementing java class.
   * 
   *  <p><b>
   *  NOTE: The configuration of the new datasource has to be compatible
   *        to the configurations of already loaded datasources. Otherwise
   *        malfunctions might occur.
   * The implementing class must be accessible from UME-core.
   * </b><p>
   *
   * @param configuration a java.io.InputStream containing the 
   * data source information in xml format which should be used 
   * to initialize the given datasource
   * 
   * @exception UMException if an error occurs. 
   */
  public void addDataSource(java.io.InputStream configuration) throws UMException;
  
  /**
   * Search for principals using the default attributes (or combined) which are 
   * defined in the UME configuration.
   * 
   * @param searchCriteria The search criteria which may contain wildcards if ISearchAttribute.LIKE_OPERATOR is used as mode
   * @param principalType The type of principal to search. Possible values are: {@link #IUSER}, {@link #IUSERACCOUNT}, {@link #IGROUP}, {@link #IROLE}
   * @param mode The modes defined in ISearchAttribute (e.g. ISearchAttribute.EQUALS_OPERATOR)
   * @param caseSensitive The case sensitivity
   * @param searchAttributes A map with additional search attributes
   * 		like key:"company", value:"SAP"
   * @return ISearchResult
   * @throws UMException
   */
  public ISearchResult simplePrincipalSearch(String searchCriteria, String principalType, int mode, boolean caseSensitive, Map searchAttributes) throws UMException;
  
}
