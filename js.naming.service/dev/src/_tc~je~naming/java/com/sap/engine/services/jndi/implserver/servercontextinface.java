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
package com.sap.engine.services.jndi.implserver;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.*;
import java.io.PrintStream;

/**
 * Remote interface for JNDI Context implementation
 *
 * @author Panayot Dobrikov
 * @author Petio Petev
 * @version 4.00
 */
public interface ServerContextInface extends java.rmi.Remote {

  /**
   * Get the context's name
   *
   * @return The Context's name
   * @throws NamingException Thrown if problems occured in determining the name of the context
   */
  public Name getCtxName() throws java.rmi.RemoteException, NamingException;


  /**
   * Performs local and global binding.
   * When global binding is invoked, binds object if it doesn't exist, if it exsists, throws exception.
   * When local binding is invoked, if local object doesn't exist - binds it, if local object exists, or object is bound as global on some other server throws an exception.
   *
   * @param name The name to be bound
   * @param obj The object that the name will be bound to
   * @param attr The attributes to set on the object
   * @param usersid User defined name of local machine to work with when local operations are prefromed
   * @param type The type of the operation
   * @throws NamingException Thrown when NamingException occurs
   * @throws java.rmi.RemoteException Thrown when RemoteException occurs
   */
  //  public void bind(Name name, Object obj, Attributes attr, , short type) throws java.rmi.RemoteException, NamingException;
  public void bind(Name name, byte[] object, short type, boolean lastSerializationType) throws java.rmi.RemoteException, NamingException;


  /**
   * Performs local and global rebind operation.
   * <p/>
   * When global rebind is invoked, if an object with that name does not exist, creates it and a container, links the object with that container, else does nothing at this step. Then takes an enumeration of the linked container and deletes all objects, except that bound to the local sid, which rebinds. If the local sid is not bound, it is created.
   * <p/>
   * When local rebind is invoked, if the local object (with the same sid as this machine) is not bound, creates it, otherwise rebinds it without processing other sids.
   *
   * @param name The name to be rebound
   * @param obj The new object that the name will be bound to
   * @param attr The attributes to set on the new object
   * @param usersid User defined name of local machine to work with when local operations are prefromed
   * @param type The type of the operation
   * @throws NamingException Thrown when NamingException occurs
   * @throws java.rmi.RemoteException Thrown when RemoteException occurs
   */
  //  public void rebind(Name name, Object obj, Attributes attr, , short type)
  public void rebind(Name name, byte[] objectData, short type, boolean lastSerializationType) throws java.rmi.RemoteException, NamingException;


  /**
   * Performs local and global lookup.
   * <p/>
   * If name is empty name, returns instance of this context. When the name leads to subcontext, returns instance of its context. Both instances may be accessed concurrently
   * <p/>
   * When global lookup is invoked, checks if an object with that name exists in the container, and if it does not, throws an exception. Otherwise takes its linked container and tries to get the object with the sid of the current machine, if it exists, returns the object in the bound to the sid DirObject, otherwise takes an enumeration of the container's objects (which are machines' sids) and invokes selectObject method, which selects the appropriate machine to take object from.
   * <p/>
   * When local lookup is invoked, checks if the object exists and throws exception if it doesn't, otherwise tries to take the object with this machine's sid, and if it does not exist throws an exception
   *
   * @param name The name of the object to be looked up
   * @param type The type of the operation
   * @return The looked up object
   * @throws NamingException Thrown if problem is encountered when looking up the name
   * @throws java.rmi.RemoteException Thrown if problem is encountered when looking up the name
   */
  public Object lookup(Name name, short type) throws java.rmi.RemoteException, NamingException;


  /**
   * Performs local and global unbind.
   * <p/>
   * If the object with that name does not exist, doesn't throw any exception but exits the method.
   * <p/>
   * When global, if it exists, removes the linked-to-it container and its objects. Then removes the object.
   * <p/>
   * When local, if it exists, removes only the object in the linked-to-it container which is bound with the current machine's sid if it exists. If afterwards the linked-to-object container has been emptied, removes the object.
   *
   * @param name The name of the object to be unbound
   * @param type The type of the operation
   * @throws NamingException If problem occurs while unbinding the object
   * @throws java.rmi.RemoteException If problem occurs while unbinding the object
   */
  public void unbind(Name name, short type) throws java.rmi.RemoteException, NamingException;


  /**
   * Performs local and global rename.
   * <p/>
   * When global rename is invoked, simply renames the object in the repository if the two names' last contexts are the same, otherwise invokes repository's move method.
   * <p/>
   * When local rename is invoked, dispite of equality of the names' last contexts unbinds the current machine's sid in the object's linked container, and binds it in the newName's object one if it exists. If newName's object does not exist, creates new ClusterObject and Container linked to it, and binds the object took, from oldName's container in the new Container.
   *
   * @param oldName The old name of the object
   * @param newName The new name of the object
   * @param type Type of operation
   * @throws NamingException Thrown when a NamingException occurs while renaming
   * @throws java.rmi.RemoteException Thrown when a RemoteException occurs while renaming
   */
  public void rename(Name oldName, Name newName, short type) throws java.rmi.RemoteException, NamingException;


  /**
   * Creates subContext.
   * The last component of name points to a context.
   * There is no difference between local and global invoking because the context structure is always global.
   *
   * @param name The name of the newly created context
   * @param attr The attributes to put on the new context
   * @param type Type of operation
   * @return An instance of the newly created context
   * @throws NamingException If NamingException is thrown while creating the context.
   * @throws java.rmi.RemoteException If RemoteException is thrown while creating the context.
   */
  public ServerContextInface createSubcontext(Name name, Attributes attr, short type) throws java.rmi.RemoteException, NamingException;


  /**
   * Destroys subContext.
   * The last component of name points to a context.
   * There is no difference between local and global invoking because the context structure is always global.
   * If the context doesn't exist, does nothing. If the pointed context is not empty, throws ContextNotEmptyException.
   * If any of intermediate context does not exist, throws NameNotFoundException.
   * If the object is not a context, throws NotContextException.
   *
   * @param name The name of the context to be destroyed
   * @param type Type of operation
   * @throws NamingException If NamingException is thrown while destroying the context.
   * @throws java.rmi.RemoteException If RemoteException is thrown while destroying the context.
   */
  public void destroySubcontext(Name name, short type) throws java.rmi.RemoteException, NamingException;


  /**
   * Lists bindings
   *
   * @param name The name of the context to be listed
   * @param type Type of operation
   * @return
   * @throws java.rmi.RemoteException
   * @throws NamingException
   */
  public ServerNamingEnum listBindings(Name name, short type, boolean onserver) throws java.rmi.RemoteException, NamingException;


  /**
   * Returns enumeration of Bindings in the Context
   * <p/>
   * When global listBindings is invoked, enumerates objects in context using handles. Later, when nextElement method of the returned enumeration is invoked, it selects which object to return, similarly to lookup method.
   * <p/>
   * When local listBindigs is invoked, enumerates only objects in the context that have a local binding
   *
   * @param name The name of the context to be listed
   * @param type Type of operation
   * @return Enumeration of the Binding-s, according to objects in the context
   * @throws NamingException If NamingException is thrown while listing the context.
   * @throws java.rmi.RemoteException If RemoteException is thrown while listing the context.
   */
  public ServerNamingEnum list(Name name, short type, boolean onserver) throws java.rmi.RemoteException, NamingException;


  /**
   * Performs local and global getAttributes.
   * <p/>
   * When local invoked, try to return bound to this machine's sid object, and if it doesn't exist, throw NamingException.
   * <p/>
   * When global, try to access bound to this machine's sid object, if it does not exist, use selectObject method
   *
   * @param name The name of the object to take attributes from
   * @param type The type of the operation
   * @return The attributes of the object named by name
   * @throws NamingException If NamingException is thrown while getting the attributes of the object.
   * @throws java.rmi.RemoteException If RemoteException is thrown while getting the attributes of the object.
   */
  public Attributes getAttributes(Name name, short type) throws java.rmi.RemoteException, NamingException;


  /**
   * Performs local and global getAttributes.
   * <p/>
   * When local invoked, try to return bound to this machine's sid object, and if it doesn't exist, throw NamingException.
   * <p/>
   * When global, try to access bound to this machine's sid object, if it does not exist, use selectObject method
   * <p/>
   * Returns attributes with the selected IDs
   *
   * @param name The name of the object to take attributes from
   * @param attrIDs The set of attributes to return in result
   * @param type The type of the operation
   * @return The attributes of the object named by name
   * @throws NamingException If NamingException is thrown while getting the attributes of the object.
   * @throws java.rmi.RemoteException If RemoteException is thrown while getting the attributes of the object.
   */
  public Attributes getAttributes(Name name, String[] attrIDs, short type) throws java.rmi.RemoteException, NamingException;


  /**
   * Performs local and global modifyAttributes.
   * <p/>
   * When local invoked, try to modify bound to this machine's sid object, and if it doesn't exist, throw NamingException.
   * <p/>
   * When global, modify objects bound to all sids in the linked-to-object container.
   * <p/>
   * Modifies by array ModificationItems
   *
   * @param name The name bound to object whose attributes are to be modified
   * @param mod_op Array of ModificationItem-s, which determine the modifying operations
   * @param type Type of operation
   * @throws AttributeModificationException When Exception occurs while modifying the attributes
   * @throws NamingException When there is a problem encountered while performing naming operations
   * @throws java.rmi.RemoteException When there is a problem encountered while performing remote operations
   */
  public void modifyAttributes(Name name, int mod_op, Attributes attrs, short type) throws java.rmi.RemoteException, AttributeModificationException, NamingException;


  /**
   * Performs local and global modifyAttributes.
   * <p/>
   * When local invoked, try to modify bound to this machine's sid object, and if it doesn't exist, throw NamingException.
   * <p/>
   * When global, modify objects bound to all sids in the linked-to-object container.
   * <p/>
   * Modifies by modify operation
   *
   * @param name The name bound to object whose attributes are to be modified
   * @param mods The modifying operation
   * @param type Type of operation
   * @throws AttributeModificationException When Exception occurs while modifying the attributes
   * @throws NamingException When there is a problem encountered while performing naming operations
   * @throws java.rmi.RemoteException When there is a problem encountered while performing remote operations
   */
  public void modifyAttributes(Name name, ModificationItem[] mods, short type) throws java.rmi.RemoteException, AttributeModificationException, NamingException;


  /**
   * Performs local and global search with specific attributes to match.
   * <p/>
   * The enumeration skips the objects with attributes that do not match with matchingAttributes.
   * <p/>
   * When local search invoked, the enumeration skips objects not on this machine.
   * <p/>
   * Objects in the enumeration are SearchResults with attributes, got by filtering each object's attribute with attributesToReturn
   *
   * @param name The name of the context or object, the search process will be applied to
   * @param matchingAttributes The attributes an object has to match to be added to resulting enumeration
   * @param attributesToReturn The selection of the attributes to return in result
   * @param type Type of operation
   * @return Enumeration of the SearchResult-s according to the qualified objects
   * @throws NamingException When there was a problem in naming operations
   * @throws java.rmi.RemoteException When there was a problem in remote operations
   */
  public ServerNamingEnum search(Name name, Attributes matchingAttributes, String[] attributesToReturn, short type) throws NamingException, java.rmi.RemoteException;


  /**
   * Performs search according to RFC 2254's string filter and search controls.
   * The enumeration skips the not-qualified objects
   *
   * @param name The name of the context or object, the search process will be applied to
   * @param filterExpr The string filter according to RFC 2254
   * @param filterArgs The arguments passed to the filter to complete it
   * @param cons The searchcontrols containg information on which and how much of the objects will qualify, and the type of the SearchResult
   * @param type Operation type
   * @return Enumeration of the SearchResult-s according to the qualified objects
   * @throws NamingException When there was a problem in naming operations
   * @throws java.rmi.RemoteException When there was a problem in remote operations
   */
  public ServerNamingEnum search(Name name, String filterExpr, Object filterArgs[], SearchControls cons, short type) throws java.rmi.RemoteException, NamingException;


  /**
   * Returns the environment of the context.
   *
   * @param type Type of the operation
   * @return The environment of the context
   * @throws NamingException When there was a problem in naming operations
   * @throws java.rmi.RemoteException When there was a problem in remote operations
   */
  public Properties getEnvironment(short type) throws NamingException, java.rmi.RemoteException;


  /**
   * Removes a property from the environment
   *
   * @param propName The nonnull property to remove.
   * @param type Type of the operation
   * @return The value of the property before removing.
   * @throws NamingException When there was a problem in naming operations
   * @throws java.rmi.RemoteException When there was a problem in remote operations
   */
  public String removeFromEnvironment(String propName, short type) throws NamingException, java.rmi.RemoteException;


  /**
   * Adds a property to the environment
   *
   * @param propName The nonnull property to remove.
   * @param propVal The nonnul value of the property.
   * @param type Type of the operation
   * @return The value of the property prior adding the new value.
   * @throws NamingException When there was a problem in naming operations
   * @throws java.rmi.RemoteException When there was a problem in remote operations
   */
  public String addToEnvironment(String propName, String propVal, short type) throws NamingException, java.rmi.RemoteException;


  /**
   * Returns the flag whether the implementation has the ability to dynamiclly listen to afterwards created handles
   *
   * @return Always false as dynaming listening is not implemented
   * @throws NamingException When there was a problem in naming operations
   * @throws java.rmi.RemoteException When there was a problem in remote operations
   */
  public boolean targetMustExist() throws java.rmi.RemoteException, NamingException;


  /**
   * Close the repository
   *
   * @throws java.rmi.RemoteException When there was a problem in naming operations
   * @throws NamingException When there was a problem in remote operations
   */
  public void close() throws java.rmi.RemoteException, NamingException;


  /**
   * Prints the naming using repository's printTree()
   *
   * @throws java.rmi.RemoteException When there was a problem in naming operations
   * @throws NamingException When there was a problem in remote operations
   */
  public void print(PrintStream outStrm) throws java.rmi.RemoteException, NamingException;


  /**
   * Deny specific operation for specific user (denoted by sid)
   *
   * @param s The string to print to the screen.
   */
  //  public void allowOperation(int sid, Name ctxName, byte operation) throws NamingException, java.rmi.RemoteException;
  public void allowOperation(String userName, String permissionName, boolean isGroup) throws NamingException, java.rmi.RemoteException;


  /**
   * Deny specific operation for specific user (denoted by sid)
   *
   * @param s The string to print to the screen.
   */
  //  public void denyOperation(int sid, Name ctxName, byte operation) throws NamingException, java.rmi.RemoteException;
  public void denyOperation(String userName, String permissionName, boolean isGroup) throws NamingException, java.rmi.RemoteException;

}

