package com.sap.security.api.persistence;
import java.util.Locale;
import com.sap.security.api.UMException;

public interface IDataSourceMetaData
{
	public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/persistence/IDataSourceMetaData.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/**
	 *  Returns the average search time on the datasource. 
	 *  Returns -1 if no search was done, or monitoring is switched of.<p>
	 *
	 * @return The average search time for the datasource
	 *
	 */
	public double getAverageSearchTime();

	/**
	 *  Returns the minimum search time on the datasource. 
	 *  Returns -1 if no search was done, or monitoring is switched of.<p>
	 *
	 * @return The minimum search time for the datasource
	 *
	 */
	public long getMinSearchTime();

	/**
	 *  Returns the maximum search time on the datasource. 
	 *  Returns -1 if no search was done, or monitoring is switched of.<p>
	 *
	 * @return The maximum search time for the datasource
	 *
	 */
	public long getMaxSearchTime();

   /**
    *  Returns the number of searches on the datasource. 
    *  Returns 0 if no search was done, or monitoring is switched of.<p>
    *
  * @return The number of searches on the datasource
  *
  */
 public long getSearchCounter();

 /**
  *  Returns the total search time on the datasource. 
  *  Returns -1 if no search was done, or monitoring is switched of.<p>
  *
  * @return The total search time for the datasource
  *
  */
 public long getTotalSearchTime();


	/**
	 *  Returns the average create time for principls on the datasource. 
	 *  Returns -1 if no single attribute was populated, or monitoring is switched of.<p>
	 *
	 * @return The average search time on the datasource
	 *
	 */
	public double getAveragePopulateAttributeTime();

	/**
	 *  Returns the maximum populate attribute time for principls on the datasource.  
	 *  Returns -1 if no single attribute was populated, or monitoring is switched of.<p>
	 *
	 * @return The maximum populate attribute time for principls on the datasource
	 *
	 */
	public long getMaxPopulateAttributeTime();

   /**
    *  Returns the number of populating attributes of a principls on the datasource. 
    *  Returns 0 if no single attribute was populated, or monitoring is switched of.<p>
    *
    * @return The number of populating attributes of a principls on the datasource
    *
    */
   public long getPopulateAttributeCounter();

   /**
    *  Returns the total populate attribute time for principls on the datasource. 
    *  Returns -1 if no single attribute was populated, or monitoring is switched of.<p>
    *
    * @return The total populate attribute time for principls on the datasource
    *
    */
   public long getTotalPopulateAttributeTime();

	/**
	 *  Returns the minimum populate attribute time for principls on the datasource. 
	 *  Returns -1 if no single attribute was populated, or monitoring is switched of.<p>
	 *
	 * @return The minimum populate attribute time for principls on the datasource
	 *
	 */
	public long getMinPopulateAttributeTime();


	/**
	 *  Returns the average populate time for a principal on the datasource. 
	 *  Returns -1 if no principal was populated, or monitoring is switched of.<p>
	 *
	 * @return The average populate time for a principal on the datasource
	 *
	 */
	public double getAveragePopulatePrincipalTime();

	/**
	 *  Returns the maximum populate time for a principal on the datasource. 
	 *  Returns -1 if no principal was populated, or monitoring is switched of.<p>
	 *
	 * @return The maximum populate time for a principal on the datasource
	 *
	 */
	public long getMaxPopulatePrincipalTime();

 /**
  *  Returns the number of populate principals on the datasource. 
  *  Returns 0 if no principal was populated, or monitoring is switched of.<p>
  *
  * @return The number of populate principal on the datasource
  *
  */
 public long getPopulatePrincipalCounter();

 /**
  *  Returns the total populate time for a principal on the datasource. 
  *  Returns -1 if no principal was populated, or monitoring is switched of.<p>
  *
  * @return The total populate time for a principal on the datasource
  *
  */
 public long getTotalPopulatePrincipalTime();

	/**
	 *  Returns the minimum populate time for a principal on the datasource. 
	 *  Returns -1 if no principal was populated, or monitoring is switched of.<p>
	 *
	 * @return The minimum populate time for a principal on the datasource
	 *
	 */
	public long getMinPopulatePrincipalTime();


	/**
	 *  Returns the average populate time for creating a principal on the datasource. 
	 *  Returns -1 if no principal was created, or monitoring is switched of.<p>
	 *
	 * @return The average populate time for creating a principal on the datasource
	 *
	 */
	public double getAverageCreatePrincipalTime();

	/**
	 *  Returns the maximum populate time for creating a principal on the datasource. 
	 *  Returns -1 if no principal was created, or monitoring is switched of.<p>
	 *
	 * @return The maximum populate time for creating a principal on the datasource
	 *
	 */
	public long getMaxCreatePrincipalTime();

   /**
    *  Returns the number of creating a principal on the datasource. 
    *  Returns 0 if no principal was created, or monitoring is switched of.<p>
    *
    * @return The number of creating a principal on the datasource
    *
    */
   public long getCreatePrincipalCounter();

   /**
    *  Returns the total populate time for creating a principal on the datasource. 
    *  Returns -1 if no principal was created, or monitoring is switched of.<p>
    *
    * @return The total populate time for creating a principal on the datasource
    *
    */
   public long getTotalCreatePrincipalTime();

	/**
	 *  Returns the minimum populate time for creating a principal on the datasource. 
	 *  Returns -1 if no principal was created, or monitoring is switched of.<p>
	 *
	 * @return The minimum populate time for creating a principal on the datasource
	 *
	 */
	public long getMinCreatePrincipalTime();


	/**
	 *  Returns the average populate time for committing a principal on the datasource. 
	 *  Returns -1 if no principal was committed, or monitoring is switched of.<p>
	 *
	 * @return The average populate time for committing a principal on the datasource
	 *
	 */
	public double getAverageCommitPrincipalTime();

	/**
	 *  Returns the maximum populate time for committing a principal on the datasource. 
	 *  Returns -1 if no principal was committed, or monitoring is switched of.<p>
	 *
	 * @return The maximum populate time for committing a principal on the datasource
	 *
	 */
	public long getMaxCommitPrincipalTime();

   /**
    *  Returns the number of committing a principal on the datasource. 
    *  Returns 0 if no principal was committed, or monitoring is switched of.<p>
    *
    * @return The number of committing a principal on the datasource
    *
    */
   public long getCommitPrincipalCounter();

   /**
    *  Returns the total populate time for committing a principal on the datasource. 
    *  Returns -1 if no principal was committed, or monitoring is switched of.<p>
    *
    * @return The total populate time for committing a principal on the datasource
    *
    */
   public long getTotalCommitPrincipalTime();

	/**
	 *  Returns the minimum populate time for committing a principal on the datasource. 
	 *  Returns -1 if no principal was committed, or monitoring is switched of.<p>
	 *
	 * @return The minimum populate time for committing a principal on the datasource
	 *
	 */
	public long getMinCommitPrincipalTime();


	/**
	 *  Returns the average time for checking credentials on the datasource. 
	 *  Returns -1 if no credentials are checked so far, or monitoring is switched of.<p>
	 *
	 * @return The average time for checking credentials on the datasource
	 *
	 */
	public double getAverageCheckCredentialsTime();

	/**
	 *  Returns the maximum time for checking credentials on the datasource. 
	 *  Returns -1 if no credentials are checked so far, or monitoring is switched of.<p>
	 *
	 * @return The maximum time for checking credentials on the datasource
	 *
	 */
	public long getMaxCheckCredentialsTime();

	/**
	 *  Returns the number of checking credentials on the datasource. 
	 *  Returns 0 if no credentials are checked so far, or monitoring is switched of.<p>
	 *
	 * @return The maximum time for checking credentials on the datasource
	 *
	 */
	public long getCheckCredentialsCounter();

	 /**
	  *  Returns the total time for checking credentials on the datasource. 
	  *  Returns -1 if no credentials are checked so far, or monitoring is switched of.<p>
	  *
	  * @return The total time for checking credentials on the datasource
	  *
	  */
	 public long getTotalCheckCredentialsTime();


	/**
	 *  Returns the minimum time for checking credentials on the datasource. 
	 *  Returns -1 if no credentials are checked so far, or monitoring is switched of.<p>
	 *
	 * @return The minimum time for checking credentials on the datasource
	 *
	 */
	public long getMinCheckCredentialsTime();


	/**
	 *  Returns the average populate time for deleting a principal on the datasource. 
	 *  Returns -1 if no principal was deleted, or monitoring is switched of.<p>
	 *
	 * @return The average populate time for deleting a principal on the datasource
	 *
	 */
	public double getAverageDeletePrincipalTime();

	/**
	 *  Returns the minimum populate time for deleting a principal on the datasource. 
	 *  Returns -1 if no principal was deleted, or monitoring is switched of.<p>
	 *
	 * @return The minimum populate time for deleting a principal on the datasource
	 *
	 */
	public long getMinDeletePrincipalTime();

	/**
	 *  Returns the maximum populate time for deleting a principal on the datasource. 
	 *  Returns -1 if no principal was deleted, or monitoring is switched of.<p>
	 *
	 * @return The maximum populate time for deleting a principal on the datasource
	 *
	 */
	public long getMaxDeletePrincipalTime();

   /**
    *  Returns the number of deleting a principal on the datasource. 
    *  Returns 0 if no principal was deleted, or monitoring is switched of.<p>
    *
    * @return The number of deleting a principal on the datasource
    *
    */
   public long getDeletePrincipalCounter();
 
   /**
    *  Returns the total populate time for deleting a principal on the datasource. 
    *  Returns -1 if no principal was deleted, or monitoring is switched of.<p>
    *
    * @return The total populate time for deleting a principal on the datasource
    *
    */
   public long getTotalDeletePrincipalTime();


	/**
	 *  Returns the average populate time for updating a principal on the datasource. 
	 *  Returns -1 if no principal was updated, or monitoring is switched of.<p>
	 *
	 * @return The average populate time for updating a principal on the datasource
	 *
	 */
	public double getAverageUpdatePrincipalTime();

	/**
	 *  Returns the maximum populate time for updating a principal on the datasource. 
	 *  Returns -1 if no principal was updated, or monitoring is switched of.<p>
	 *
	 * @return The maximum populate time for updating a principal on the datasource
	 *
	 */
	public long getMaxUpdatePrincipalTime();

   /**
    *  Returns the number of updating a principal on the datasource. 
    *  Returns -1 if no principal was updated, or monitoring is switched of.<p>
    *
    * @return The number of updating a principal on the datasource
    *
    */
   public long getUpdatePrincipalCounter();

   /**
    *  Returns the total populate time for updating a principal on the datasource. 
    *  Returns -1 if no principal was updated, or monitoring is switched of.<p>
    *
    * @return The total populate time for updating a principal on the datasource
    *
    */
   public long getTotalUpdatePrincipalTime();

	/**
	 *  Returns the minimum populate time for updating a principal on the datasource. 
	 *  Returns -1 if no principal was updated, or monitoring is switched of.<p>
	 *
	 * @return The minimum populate time for updating a principal on the datasource
	 *
	 */
	public long getMinUpdatePrincipalTime();

	/**
	 *  Returns the id of the datasource. <p>
	 *
	 * @return The id of the datasource
	 *
	 */
	public String getDataSourceID();

	/**
	 *  Returns the displayname of the datasource. <p>
	 *
	 * @return The displayname of the datasource
	 *
	 */
	public String getDisplayName();

	/**
	 *  Returns the displayname of the datasource or null. <p>
	 *@param locale The locale
	 * @return The displayname of the datasource
	 *
	 */
	public String getDisplayName(Locale locale);
	
	/**
	 *  Returns whether the datasource is used for checking credentials. <p>
	 *
	 * @return true if the datasource is used to check credentials, otherwise false.
	 *
	 */
	public boolean isUsedForCheckingCredentials();

	/**
	 *  Returns the implementation class name <p>
	 *
	 * @return the implementation class.
	 *
	 */
	public String getClassName();
	
	/**
	 * Returns whether the datasource is readonly or not.
	 * @return <code>true</code> if the datasource is readonly, otherwise <code>false</code>.
	 */
	public boolean isReadonly();
	
	/**
	 * Returns whether the datasource is a primary datasource or not.
	 * @return <code>true</code> if the datasource is a primary datasource, otherwise <code>false</code>.
	 */
	public boolean isPrimary();
	
	/**
	 * Returns whether the datasource is a global datasource or not.
	 * @return <code>true</code> if the datasource is a global datasource, otherwise <code>false</code>.
	 */
	public boolean isGlobal();
	
	/**
	 * Returns whether the datasource does policy enforcement for the password policies.
	 * @return <code>true</code> if the datasource does policy enforcement, otherwise <code>false</code>.
	 */
	public boolean isPolicyEnforced();

	/**
	 *  Returns the average create time for principls on the datasource. 
	 *  Returns -1 if no single attribute was populated, or monitoring is switched of.<p>
	 *
	 * @return The average search time on the datasource
	 *
	 */
	public double getAverageGetDirectParentsTime();

	/**
	 *  Returns the maximum populate attribute time for principls on the datasource.  
	 *  Returns -1 if no single attribute was populated, or monitoring is switched of.<p>
	 *
	 * @return The maximum populate attribute time for principls on the datasource
	 *
	 */
	public long getMaxGetDirectParentsTime();

   /**
	*  Returns the number of populating attributes of a principls on the datasource. 
	*  Returns 0 if no single attribute was populated, or monitoring is switched of.<p>
	*
	* @return The number of populating attributes of a principls on the datasource
	*
	*/
   public long getGetDirectParentsCounter();

   /**
	*  Returns the total populate attribute time for principls on the datasource. 
	*  Returns -1 if no single attribute was populated, or monitoring is switched of.<p>
	*
	* @return The total populate attribute time for principls on the datasource
	*
	*/
   public long getTotalGetDirectParentsTime();

	/**
	 *  Returns the minimum populate attribute time for principls on the datasource. 
	 *  Returns -1 if no single attribute was populated, or monitoring is switched of.<p>
	 *
	 * @return The minimum populate attribute time for principls on the datasource
	 *
	 */
	public long getMinGetDirectParentsTime();

	/**
	 *  Allows to set data source specific properties
	 * 
	 *  @param key the key
	 *  @param value the value
	 *  @throws PersistenceException if the a error occured
	 */	
	public void setProperty(String key, Object value) throws UMException;

	/**
	 *  Allows to get data source specific properties
	 * 
	 *  @param key the key
	 *  @return the value or null if no value was found
	 *  @throws PersistenceException if the a error occured
	 */	
	public Object getProperty(String key) throws UMException;

	/**
	 *  Returns whether the data source is activated
	 *
	 * @return  activation state
	 */
	public boolean isActive() throws UMException;

	/**
	 *  Returns whether a login can be performed on this adapter
	 *
	 * @return  login activation state
	 */
	public boolean isLogonActive() throws UMException;

	/**
	 *  Returns the data source specific logon id
	 *
	 * @return  logonid the adapter specific logon id
	 */
	public String getDataSourceSpecificLogonId(String logonid) throws UMException;
	public boolean isResponsibleFor(String uniqueID, String namespace, String attribute) throws UMException;
}
