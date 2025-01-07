package com.sap.engine.interfaces.resourcecontext;

/**
 * Factory for an J2EE ResourceContext 
 */
public interface ResourceContextFactory {

  /**
   *
   * @return a ResourceContext which should be assigned to an application module.
   */
   public ResourceContext createContext ( String appName, String moduleName, boolean isStateless) ;
   
   public ResourceContext createContext ( String appName, String moduleName, boolean isStateless, int isolationLevel) ;   
   
   public ResourceContext createRestrictedContext (String appName, String moduleName) ;
   
   public ResourceContext createContext ( String appName, String moduleName, boolean isStateless, Object additionalInfo) ;
   
   public ResourceContext createContext ( String appName, String moduleName, boolean isStateless, int isolationLevel, Object additionalInfo) ;
   
   public ResourceContext createContext ( String appName, String moduleName, boolean isStateless, ClassLoader contextClassLaoder) ;
   
   public ResourceContext createContext ( String appName, String moduleName, boolean isStateless, int isolationLevel, ClassLoader contextClassLaoder) ;   
   
   public ResourceContext createRestrictedContext (String appName, String moduleName, ClassLoader contextClassLaoder) ;
   
   public ResourceContext createRestrictedContext(String appName, String moduleName, ClassLoader contextClassLaoder, Object additionalInfo);   
   
   public ResourceContext createContext ( String appName, String moduleName, boolean isStateless, Object additionalInfo, ClassLoader contextClassLaoder) ;
   
   public ResourceContext createContext ( String appName, String moduleName, boolean isStateless, int isolationLevel, Object additionalInfo, ClassLoader contextClassLaoder) ;

   public ResourceContext createContext ( String appName, String moduleName, boolean isStateless, ClassLoader contextClassLaoder, boolean forbidJndiAccess) ;   
   /**
    * Method for setting DSRListener to this factory so that events to DSR could be sent. 
    * 
	  * @param dsrListener - the DSRListener implementation instance to be set
	  */
  public void setDSRListener(DSRListener dsrListener);
  
  public void addSharedTransactionListener(Object key, SharedTransactionListener listener);
  
  public SharedTransactionListener getSharedTransactionListener(Object key);
  /**
   * Whether  the specified restriction is valid for current component.
   * @param restrictionsMask mask with restrictions that wil be checked. 
   * @return <code>true</code> if there is a restriction according this mask.
   */
  public boolean isAccessRestricted(int restrictionsMask);
}

