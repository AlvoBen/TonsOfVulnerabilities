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

import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;

import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.engine.services.jndi.cluster.DirObject;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.JNDIHandleEnumeration;
import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.rmi.RemoteException;
import javax.naming.NamingException;

/**
 * Enumeration used when serching
 *
 * @author Petio Petev
 * @version 4.00
 */
class ServerNamingSearchEnumImpl implements ServerNamingEnum {

	private final static Location LOG_LOCATION = Location.getLocation(ServerNamingSearchEnumImpl.class);

  /**
   *  Type of operation (local, global)
   *
   */
  //  private short operation;
  /**
   *  User defined sid of machin to perform local operations on
   *
   */
  //  private String userSid = null;
  /**
   * Flag representing the state wether there are more searchresults in the enumeration
   */
  private boolean moreElements = true;
  /**
   * Flag representing the state wether the context which uses this class is a remote one
   */
  private boolean remote;
  /**
   * The next search result to be returned
   */
  private SearchResult nextSearchResult = null;
  /**
   * Selection of attributes to return
   */
  private String[] attrsToReturn = null;
  /**
   * Atributes to match
   */
  private Attributes matchingAttrs = null;
  /**
   * Handle enumeration to follow
   */
  JNDIHandleEnumeration je = null;
  /**
   * Repository to take objects from
   */
  JNDIPersistentRepository persistent = null;
  /**
   * The absolute name of the starting context
   */
  private Name rootName = null;
  /**
   * Boolean flag showing wether all operations are allowed in naming or only lookup
   */
  private boolean onlyLookupAllowed = false;

  private boolean redirectableContext = false;


  /**
   * Constructor. Iterates the first time to ensure next object.
   *
   * @param persistent Repository to work with
   * @param je Enumeration of handles to follow
   * @param matchingAttributes Attributes for each object to match
   * @param attrsToReturn Attributes to return
   * @param rootName Initial value of this.rootName
   * @param remote is remote
   * @param onlyLookup showing wether all operations are allowed in naming or only lookup
   * @throws java.rmi.RemoteException When there was error in remote procedures
   */
    public ServerNamingSearchEnumImpl(JNDIPersistentRepository persistent,
                                      JNDIHandleEnumeration je,
                                      Attributes matchingAttributes,
                                      String[] attrsToReturn,
                                      Name rootName,
                                      boolean remote,
                                      boolean onlyLookup,
                                      boolean redirectable) throws java.rmi.RemoteException {

    // set up the variables
    this.remote = remote;
    this.je = je;
    this.persistent = persistent;
    //    this.operation = oper;
    this.attrsToReturn = attrsToReturn;
    this.matchingAttrs = matchingAttributes;
    this.rootName = rootName;
    this.onlyLookupAllowed = onlyLookup;
    this.redirectableContext = redirectable;
    try {
      // always make the first iteration here
      next();
    } catch (javax.naming.NamingException ne) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", ne);
      RemoteException re = new RemoteException("Exception during search operation.", ne);
      throw re;
    }
  }

  /**
   * Returns next element of the enumeration. Qualified objects are chosen by their attributes and the locallity of operation.
   *
   * @return The next SearchResult
   * @throws java.rmi.RemoteException When there was error in remote procedures
   * @throws javax.naming.NamingException When there was error in naming procedures
   */
  public Object next() throws java.rmi.RemoteException, javax.naming.NamingException {
    // always return current objects and advance to next ones
    if (!moreElements) {
      throw new java.util.NoSuchElementException("There are no more elements in the naming enumeration.");
    }

    if (je.hasMoreElements()) {
      try {
        // this loop advances to the next object, appropriate to the query
        JNDIHandle jh = je.nextObject(); // next Object
        boolean found = false;
        Attributes attrs = null;
        String objName = null;
        Object dirObject = null;
        SearchResult tempSearchResult = null;

        do {
          objName = persistent.getObjectName(jh);

          if (objName != null) { // a valid object handle

            byte[] dirObj = persistent.readObject(jh);
            JNDIHandle linkedContainer = persistent.getLinkedContainer(jh);

            if (linkedContainer != null) { // Context
              if (redirectableContext) {
                dirObject = new ServerContextRedirectableImpl(persistent.getNewConnection(), jh, linkedContainer, ((Name) (rootName.clone())).add(objName), remote);
                ((ServerContextRedirectableImpl) dirObject).onlyLookUpAllowed = this.onlyLookupAllowed;
              } else {
                dirObject = new ServerContextImpl(persistent.getNewConnection(), jh, linkedContainer, ((Name) (rootName.clone())).add(objName), remote);
                ((ServerContextImpl) dirObject).onlyLookUpAllowed = this.onlyLookupAllowed;
              }
              attrs = dirObj != null ? DirObject.getAttributes(dirObj) : null;
            } else {
              dirObject = dirObj;
              attrs = dirObject != null ? DirObject.getAttributes((byte[]) dirObject) : null;
            }

            // now check if the result is appropriate to the query
            if (attrs == null) {
              attrs = new BasicAttributes();
            }

            if (ModifyAttributes.matchingAttributes(attrs, matchingAttrs)) {
              // the attributes matched
              BasicAttributes selectedAttr = new BasicAttributes();

              // if attrsToReturn is null, return all attributes, by specification
              if (attrsToReturn != null) {
                // if is not null, select the attributes to return
                Attribute tempAttr = null;
                for (int i = 0; i < attrsToReturn.length; i++) {
                  // if the requested attribute set contains an attribute which is not present in the
                  // attributes assosiated with the object - skip it! (by specification)
                  tempAttr = attrs.get(attrsToReturn[i]);
                  if (tempAttr != null) {
                    selectedAttr.put(tempAttr);
                  }
                }

                tempSearchResult = new SearchResult(objName, null, dirObject, selectedAttr);
              } else {
                tempSearchResult = new SearchResult(objName, null, dirObject, attrs);
              }

              found = true;
            } else {
              // the attributes did not match, advance to next if possible
              if (je.hasMoreElements()) {
                found = false;
                jh = je.nextObject();
              } else {
                // there are no more elements, set the flag
                found = true;
                moreElements = false;
              }
            }
          } else {
            // this object is no longer valid, try the next if possible
            if (je.hasMoreElements()) {
              found = false;
              jh = je.nextObject();
            } else {
              // there are no more elements, set the flag
              found = true;
              moreElements = false;
            }
          }
        } while (!found);

        SearchResult oldSearchResult = nextSearchResult;
        // set the next object
        nextSearchResult = tempSearchResult;

        // return current object
        return oldSearchResult;
      } catch (NamingException e) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
        NamingException ne = new NamingException("Exception during search operation.");
        ne.setRootCause(e);
        throw ne;
      }
    } else {
      moreElements = false;
      return nextSearchResult;
    }
  }

  /**
   * Returns the state of the enumeration in terms of object availability
   *
   * @return Flag representing the state whether there are more objects in the enumeration
   * @throws java.rmi.RemoteException When there was error in remote procedures
   */
  public boolean hasMore() throws java.rmi.RemoteException, javax.naming.NamingException {
    if (!moreElements) {
      je.closeEnumeration();
    }

    return moreElements;
  }

  /**
   * Closes the connection of persistent repository
   *
   * @throws java.rmi.RemoteException When there was error in remote procedures
   */
  public void close() throws java.rmi.RemoteException {
    je.closeEnumeration();
  }


}

