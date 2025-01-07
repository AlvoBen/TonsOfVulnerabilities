/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.persistent;

import java.io.PrintStream;

/**
 * JNDI's Persistent Repository
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public interface JNDIPersistentRepository {

    // get new connection
    /**
     * Open connection, if it is not open, if it is open - nothing
     *
     * @throws javax.naming.NamingException
     */
    public void open() throws javax.naming.NamingException;


    /**
     * Close connection (dispose resources used by this instance)
     *
     * @throws javax.naming.NamingException
     */
    public void close() throws javax.naming.NamingException;


    /**
     * Returns a new connection to the db
     *
     * @return The new connection
     * @throws javax.naming.NamingException
     */
    public JNDIPersistentRepository getNewConnection() throws javax.naming.NamingException;


    /* creating and managing Container Objects */
    /**
     * Creates a new container
     *
     * @param name        The name of the new container
     * @param data        The byte array to be stored at that name
     * @param toReplicate true if the container is replicated, false otherwise
     * @return The handle to the container created
     * @throws javax.naming.NamingException
     */
    public JNDIHandle createContainer(String name, byte data[], boolean toReplicate) throws javax.naming.NamingException;

    /* creating and managing Contexts */
    /**
     * Creates a new context
     *
     * @param objContainerName    The name of the container where the object linked to the new container is stored
     * @param objectName          The name of the object that is linked to the new container
     * @param containerData       The new container's data
     * @param objData             The object's data
     * @param type                The type of the operation(replicated or not)
     * @return The handle to the container created
     * @throws javax.naming.NamingException
     */
    public JNDIHandle createSubcontext(String objContainerName, String objectName, byte[] containerData, byte[] objData, short type) throws javax.naming.NamingException;
    
    /**
     * Destroys a context
     *
     * @param toDeleteContainer   The name of the container marked for deletion
     * @param lastContextObject   The name of the object that is linked to the container marked for deletion
     * @param type                The type of the operation(replicated or not)
     * @throws javax.naming.NamingException
     */
    public void destroySubcontext(String toDeleteContainer, JNDIHandle lastContextObject, short type) throws javax.naming.NamingException;
    
    /**
     * Creates a new named container
     *
     * @param name        The name of the new container
     * @param data        The byte array to be stored at that name
     * @param toReplicate true if the container is replicated, false otherwise
     * @return The handle to the container created
     * @throws javax.naming.NamingException 
     */
    public JNDIHandle createNamedContainer(String name, byte data[], boolean toReplicate) throws javax.naming.NamingException;


    /**
     * Modifies container's data
     *
     * @param handle      Handle of the container to be modified
     * @param data        New byte array to be stored
     * @param toReplicate true if the container is replicated, false otherwise
     * @throws javax.naming.NamingException
     */
    public void modifyContainer(JNDIHandle handle, byte data[], boolean toReplicate) throws javax.naming.NamingException;


    /**
     * Deletes a container
     *
     * @param handle      Handle of the container to be deleted
     * @param toReplicate true if the container is replicated, false otherwise
     * @throws javax.naming.NamingException
     */
    public void deleteContainer(JNDIHandle handle, boolean toReplicate) throws javax.naming.NamingException;


    /**
     * Reads data from container
     *
     * @param handle Handle of the container to be read
     * @return The byte array stored in the container
     * @throws javax.naming.NamingException 
     */
    public byte[] readContainer(JNDIHandle handle) throws javax.naming.NamingException;


    /**
     * Renames a container
     *
     * @param container Handle to container to be renamed
     * @param newname   New name of the container
     * @throws javax.naming.NamingException 
     */
    public void renameContainer(JNDIHandle container, String newname) throws javax.naming.NamingException;


    /**
     * Returns a container's name
     *
     * @param container The handle to the container to get name from
     * @return The name of the container
     * @throws javax.naming.NamingException 
     */
    public String getContainerName(JNDIHandle container) throws javax.naming.NamingException;


    /* creating handle */
    /**
     * Creates new handle
     *
     * @return The newly created handle
     * @throws javax.naming.NamingException 
     */
    public JNDIHandle createHandle() throws javax.naming.NamingException;


    /**
     * Returns the root container of the db
     *
     * @return Handle to the root container
     * @throws javax.naming.NamingException
     */
    public JNDIHandle getRootContainer() throws javax.naming.NamingException;


    /* bind */
    /**
     * Binds a name to an object
     *
     * @param container       The container where the name will be bound
     * @param name            The name to be bound
     * @param data            The object that name will be bound to
     * @param typeOfOperation The type of the operation
     * @return Handle to the newly bound object
     * @throws javax.naming.NamingException 
     */
    public JNDIHandle bindObject(JNDIHandle container, String name, byte data[], short typeOfOperation) throws javax.naming.NamingException;


    /* unbind */
    /**
     * Unbinds object
     *
     * @param container       Handle to container where the object is held
     * @param name            The name of the object to be unbound
     * @param typeOfOperation The type of the operation
     * @throws javax.naming.NamingException 
     */
    public void unbindObject(JNDIHandle container, String name, short typeOfOperation) throws javax.naming.NamingException;


    /**
     * Unbinds object
     *
     * @param objectHandle    Handle to the object to be unbound
     * @param typeOfOperation The type of the operation
     * @throws javax.naming.NamingException
     */
    public void unbindObject(JNDIHandle objectHandle, short typeOfOperation) throws javax.naming.NamingException;


    /* rebind */
    /**
     * Rebinds object
     *
     * @param container       Container where the object to rebound is held
     * @param name            Name of the object to be rebound
     * @param newdata         New byte array to store
     * @param typeOfOperation The type of the operation
     * @throws javax.naming.NamingException 
     */
    public void rebindObject(JNDIHandle container, String name, byte[] newdata, short typeOfOperation) throws javax.naming.NamingException;


    /**
     * Rebinds object
     *
     * @param objectHandle    Handle of object to rebind
     * @param newdata         New byte array to store
     * @param typeOfOperation The type of the operation
     * @throws javax.naming.NamingException
     */
    public void rebindObject(JNDIHandle objectHandle, byte[] newdata, short typeOfOperation) throws javax.naming.NamingException;


    /* rename */
    /**
     * Rename object
     *
     * @param container       Container, containing the object to be renamed
     * @param name            The old name of the object
     * @param newname         The new name of the object
     * @param typeOfOperation The type of the operation
     * @throws javax.naming.NamingException 
     */
    public void renameObject(JNDIHandle container, String name, String newname, short typeOfOperation) throws javax.naming.NamingException;


    /**
     * Rename object
     *
     * @param objectHandle    The handle of the object to be renamed
     * @param newname         New name of the object
     * @param typeOfOperation The type of the operation
     * @throws javax.naming.NamingException
     */
    public void renameObject(JNDIHandle objectHandle, String newname, short typeOfOperation) throws javax.naming.NamingException;


    /* move */
    /**
     * Moves object
     *
     * @param container       Old container where the object was held
     * @param name            Old name of the object
     * @param newContainer    New container where the object to move will be held
     * @param newName         New name of the object
     * @param typeOfOperation The type of the operation
     * @throws javax.naming.NamingException 
     */
    public void moveObject(JNDIHandle container, String name, JNDIHandle newContainer, String newName, short typeOfOperation) throws javax.naming.NamingException;


    /**
     * Moves object
     *
     * @param objectHandle    Handle of the object to move
     * @param newContainer    New container where the object to move will be held
     * @param newName         New name of the object
     * @param typeOfOperation The type of the operation
     * @throws javax.naming.NamingException 
     */
    public void moveObject(JNDIHandle objectHandle, JNDIHandle newContainer, String newName, short typeOfOperation) throws javax.naming.NamingException;


    /* lookup object */
    /**
     * Reads data bound to a name
     *
     * @param container The container where the object to read is held
     * @param name      The name of the object to read
     * @return Serialized form of the read object
     * @throws javax.naming.NamingException 
     */
    public byte[] readObject(JNDIHandle container, String name) throws javax.naming.NamingException;


    /**
     * Reads data with a specific handle
     *
     * @param objectHandle Handle to object to read data of
     * @return Serialized form of the read object
     * @throws javax.naming.NamingException
     */
    public byte[] readObject(JNDIHandle objectHandle) throws javax.naming.NamingException;


    /* find object, returns null if object doesn't esists */
    /**
     * Returns the handle (if exists) of an object
     *
     * @param containerHandle Handle to container, hypothetically containing the object which handle will be returned
     * @param name            The name of the object to return handle of
     * @return The handle of the object (null if it does not exist)
     * @throws javax.naming.NamingException
     */
    public JNDIHandle findObject(JNDIHandle containerHandle, String name) throws javax.naming.NamingException;


    /* list object - string "*" lists all objects */
    /**
     * Creates an enumeration of one or more objects in a container
     *
     * @param container Container to be listed
     * @param name      Name of the object to be enumerated (if "*" or "", all objects are enumerated)
     * @return The enumeration of the listed objects
     * @throws javax.naming.NamingException
     */
    public JNDIHandleEnumeration listObjects(JNDIHandle container, String name) throws javax.naming.NamingException;


    /*  get name */
    /**
     * Returns object's name by handle
     *
     * @param object Handle of the object to return handle of
     * @return Name of the object with the specific handle
     * @throws javax.naming.NamingException
     */
    public String getObjectName(JNDIHandle object) throws javax.naming.NamingException;


    /**
     * Returns a object's type
     *
     * @param object The handle to the object to get name from
     * @return The type of the object
     * @throws javax.naming.NamingException
     */
    public short getObjectType(JNDIHandle object) throws javax.naming.NamingException;


    /* link object to container, get linked container */
    /**
     * Links a object to container
     *
     * @param objectHandle    The handle of the object to be linked
     * @param containerHandle The handle of the container to be
     * @param typeOfOperation The type of the operation
     * @throws javax.naming.NamingException 
     */
    public void linkObjectToContainer(JNDIHandle objectHandle, JNDIHandle containerHandle, short typeOfOperation) throws javax.naming.NamingException;


    // remove the link between the object and the linked container (do nothing if no conainer linked)
    /**
     * Removes a link between object and container
     *
     * @param objectHandle    The object to remove link to container
     * @param typeOfOperation The type of the operation
     * @throws javax.naming.NamingException
     */
    public void removeLinkedContainer(JNDIHandle objectHandle, short typeOfOperation) throws javax.naming.NamingException;


    // this bellow returns null if hasn't linked container!
    /**
     * Returns a linked to object container
     *
     * @param objectHandle Handle of the object to return linked container of
     * @return The handle of the linked to object container
     * @throws javax.naming.NamingException 
     */
    public JNDIHandle getLinkedContainer(JNDIHandle objectHandle) throws javax.naming.NamingException;


    /* for dubuging */
    /**
     * Prints the naming
     */
    public void printAll();


    /**
     * Prints the naming in tree-form
     *
     * @throws javax.naming.NamingException 
     */
    public void printTree(PrintStream outStrm) throws javax.naming.NamingException;


    /**
     * Creates container Handle from given Object Handle
     *
     * @param j New container's handle
     */
    public JNDIHandle createContianerHandle(JNDIHandle j);


    

}

