/*
 *
 * Copyright (c) 2003 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria AG.
 */
package com.sap.engine.interfaces.resourcecontext;

import javax.transaction.Transaction;
public interface ResourceContext {
	
	public static int RESTRICT_JNDI_ACCESS = 16;		// restrict JNDI access
	public static int RESTRICT_RM_ACCESS = 32;		// restrict access to resource managers (DataSources, ConnectionFactories, ResourceAdapters...)
	public static int RESTRICT_EMF_ACCESS = 64;		// restrict access to EntityManagerFactories
	public static int RESTRICT_CM_EM_ACCESS = 128;	// restrict access to Container managed EntityManagers
	public static int RESTRICT_UT_ACESS = 256;		// restrict access to UserTransaction
	
	
  /**
   * the application module calls this method if it enters an method. 
   * @param methodName  the current method name of the application module
   *           ejb: methodname
   *           web: GET/POST
   *           ...
   *  This should set initialize the current resource set assigned to this component.
   */
   public void enterMethod ( String methodName) throws ResourceContextException;
	
  /**
   * the application module calls this method if it enters an method. 
   * @param methodName  the current method name of the application module
   * @param isolationLevel | restrictionsMask. IsolationLevel is default isolation level during 
   * execution of this method. This information is used only from EJB 2.1 container.RestrictionsMask is mask 
   * with restricted operations during this method.   * 
   *           ejb: methodname
   *           web: GET/POST
   *           ...
   *  This should set initialize the current resource set assigned to this component.
   */
   public void enterMethod ( String methodName, int isolationLevelAndRestrictionsMask) throws ResourceContextException;

   // future enhancement
   //    control the tx-boundary for this method.
   //    internally uses the tm to control the jta transaction for this method. 
   //   void enterMethod ( String methodName, TxAttribute attribute );  // Required, RequiresNew, ..

  /**
   * the application module calls this method if it exits from a method. 
   * @param methodName  the current method name of the application module
   *           ejb: methodname
   *           web: GET/POST
   *           ...
   *  This should restore the resource set which had been used by this component.
   */
   public void exitMethod ( String methodName ,boolean exitStatus) throws ResourceContextException;

   // future enhancement with tx attribute
   //    internally uses the tm to control the jta transaction for this method.    
   // Throwable parameter might be an Application exception or an System exception 
   //   see EJB-2.1 Chapter 18, Exception handling should be specified by the spec.
   //
   //   void exitMethod ( String methodName, TxAttribute attribute, Throwable th );  // Required, RequiresNew, ..

   
  /**
   * the application module calls this method during cleanup.
   * this should cleanup all resources which are still assigned to this context.
   */
   public void destroy ();
   
   
  /**
   *  completes the localtransactions with MC.cleanup  LOG Error 
   *  dissociate the connections that are not closed , if dissociation is not supported 
   *  the connectionHandles are invalidated LOG.ERROR in this case 
   */
   public void clearConnections() throws ResourceContextException;
   
   
   public Transaction getTransaction() ;
   
}

