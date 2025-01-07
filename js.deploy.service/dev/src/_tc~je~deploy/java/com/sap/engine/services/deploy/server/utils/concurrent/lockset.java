package com.sap.engine.services.deploy.server.utils.concurrent;

/**
 * This is just a marking interface used to hold the result of an lock
 * operation. The client just has to store this result during the critical
 * operation, after the lock acquisition. When the critical operation is done,
 * the locks have to be released, passing the lock set to the LockManager unlock
 * method.
 * 
 * @author Emil Dinchev
 */
public interface LockSet {
	// Marking interface
}