package com.sap.engine.interfaces.resourceset;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

public interface ResourceSet {

  public void clear();

  
	/**
	 *
	 * @return the name of the application that uses the resource set or null if resource set
	 * is associated to the thread context not by application
	 */
	public String getApplicationName();

	/**
	 *
	 * @return the name of the application component that uses the resource set or null if resource set
	 * is associated to the thread context not by applicatio component
	 */
	public String getComponentName();

	/**
	 * Set the isolation level needed for persistent manager of ejb container
	 * @param iso_level
	 */
	public void setIsolationLevel(int iso_level);

	/**
	 * return the isolation level needed for persistent manager of ejb container
	 */
	public int getIsolationLevel();

	/**
	 *
	 * @return the transaction object associated to the resource set or null if there is no
	 * transaction associated to the resource set
	 */
	public Transaction getTransaction();

	/**
	 *
	 * @return the name of ejb component method within wich scope the resource set is associated to thread context
	 * or null if the resource set is associated to thread context not by ejb component
	 */
	public String getMethodName();

	/**
	 *
	 * set the name of ejb component method within wich scope the resource set is associated to thread context
	 *
	 * @param methodName name of ejb component method within wich scope the resource set is associated to thread context
	 * @return the last name of ejb component method within wich scope the resource set is associated to thread context
	 * or null if last time the resource set is associated to thread context not by ejb component
	 */
	public String setMethodName(String methodName);


	/**
	 * Enlist in the current transaction all resources referenced in the resource set
	 * and associate the resource set to given transaction
	 * @param transaction to wich the resource set will be associated
	 * @throws RollbackException to indicate that the transaction associated to the resource set has been marked for rollback only.
	 * @throws SystemException if the transaction manager or resource system encounters an unexpected error condition
	 */
	public void enlistAll(Transaction transaction) throws RollbackException, SystemException;


	/**
	 * Delist from the current transaction all resources referenced in the resource set
	 * and deassociate the resource set from transaction
	 * @param flag One of the values of TMSUCCESS, TMSUSPEND, or TMFAIL
	 * @throws SystemException if the transaction manager or resource system encounters an unexpected error condition
	 */
	public void delistAll(int flag) throws SystemException;


	/**
	 * Check if the resource set contains any resource reference objects - that is, if it is empty or not
	 * @return if it is empty
	 */
	public boolean isEmpty();

}

