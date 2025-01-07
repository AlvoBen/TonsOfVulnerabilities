package com.sap.security.api;

import java.util.Collection;

public interface PrincipalAttributeListener extends PrincipalListener {

	/***
	 * Constant used for the event that a value of an attribute of a principal was added
	 ***/	
    public static final int INFORM_ON_ATTRIBUTE_VALUE_ADDED  = 0x100;

	/***
	 * Constant used for the event that a value of an attribute of a principal was added
	 ***/	
    public static final int INFORM_ON_ATTRIBUTE_VALUE_REMOVED  = 0x200;
	    
	/***
	 * Constant used for the event that an attribute of a principal was set
	 ***/	
    public static final int INFORM_ON_ATTRIBUTE_SET  = 0x400;

	/***
	 * Constant used for the event that an attribute of a principal was deleted
	 ***/	
    public static final int INFORM_ON_ATTRIBUTE_DELETED  = 0x800;
    
    /**
     * attributeValueAdded(String,String,String,Object) is called if event INFORM_ON_ATTRIBUTE_VALUE_ADDED is fired from the
     * registering factory
     * @param uniqueID of the modified principal
     * @param namespace the namespace
     * @param attribute the attribute
     * @param values the added attribute's value
     * @throws UMException
     */
    public void attributeValueAdded(String uniqueID, String namespace, String attribute, Collection values) throws UMException;

    /**
     * attributeValueRemoved(String,String,String,Object) is called if event INFORM_ON_ATTRIBUTE_VALUE_REMOVED is fired from the
     * registering factory
     * @param uniqueID of the modified principal
     * @param namespace the namespace
     * @param attribute the attribute
     * @param values the removed attribute's value
     * @throws UMException
     */
    public void attributeValueRemoved(String uniqueID, String namespace, String attribute, Collection values) throws UMException;

    /**
     * attributeSet(String,String,String,Object) is called if event INFORM_ON_ATTRIBUTE_SET is fired from the
     * registering factory
     * @param uniqueID of the modified principal
     * @param namespace the namespace
     * @param attribute the attribute
     * @param values the set attribute's values
     * @throws UMException
     */
    public void attributeSet(String uniqueID, String namespace, String attribute, Collection values) throws UMException;

    /**
     * attributeDeleted(String,String,String,Object) is called if event INFORM_ON_ATTRIBUTE_DELETED is fired from the
     * registering factory
     * @param uniqueID of the modified principal
     * @param namespace the namespace
     * @param attribute the attribute
     * @param formerValues the former values of the deleted attribute or null	
     * @throws UMException
     */
    public void attributeDeleted(String uniqueID, String namespace, String attribute, Collection formerValues) throws UMException;
    
}
