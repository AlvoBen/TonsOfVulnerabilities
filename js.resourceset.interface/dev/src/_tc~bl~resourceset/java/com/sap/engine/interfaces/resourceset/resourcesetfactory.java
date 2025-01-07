package com.sap.engine.interfaces.resourceset;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

public interface ResourceSetFactory {


	
	public ResourceSet createResourceSet(String application_name, String component_name);

	public ResourceSet createResourceSet(String application_name, String component_name, int iso_level);

	public ResourceSet createResourceSet(Transaction transaction, String application_name, String component_name) throws RollbackException, SystemException;

	public ResourceSet createResourceSet(Transaction transaction, String application_name, String component_name, int iso_level) throws RollbackException, SystemException;

	
	public ResourceSet getCurrentResourceSet() ;

	public ResourceSet replaceCurrentResourceSet(ResourceSet resource_set);

	
}

