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
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.engine.services.jndi.cluster.DirObject;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.rmi.RemoteException;

/**
 * Enumeration used when searching. "One object" implementation
 *
 * @author Petio Petev
 * @version 4.00
 */
class ServerNamingOneObjectEnumImpl implements ServerNamingEnum {

	private final static Location LOG_LOCATION = Location.getLocation(ServerNamingOneObjectEnumImpl.class);

  /**
   *  Type of operation (local, global)
   *
   */
  //  private short operation;
  /**
   *  User defined sid for local operations
   *
   */
  //  private String userSid = null;
  /**
   * Flag representing wether there are more elements in the enumeration
   */
  private boolean moreElements = true;
  /**
   * Flag representing the state wether the context which uses this class is a remote one
   */
  private boolean remote;
  /**
   * The absolute name of starting context
   */
  private Name rootName = null;

  /**
   * The object to return on next invokation of next()
   */
  private SearchResult nextSearchResult = null;
  /**
   * SearchFilter implementing RFC 2254 String filter for checking of attributes conditions
   */
  private SearchFilter searchFilter = null;
  /**
   * Boolean flag showing wether all operations are allowed in naming or only lookup
   */
  private boolean onlyLookupAllowed = false;

  private boolean redirectableContext = false;

  /**
   * Constructor. Does most of the work as we want only one object in this enumeration.
   *
   * @param persistent The repository we will take objects from
   * @param object Handle to the object or container we will check for qualification
   * @param filter String filter according RFC 2254
   * @param filterArgs Arguments to the filter according RFC 2254
   * @param searchControls SearchControls containing information for qualifying object and return type
   * @param remote is remote
   * @param rootName Initial value of this.rootName
   * @param onlyLookup showing wether all operations are allowed in naming or only lookup
   * @throws java.rmi.RemoteException When remote operation lead to problem
   */
    public ServerNamingOneObjectEnumImpl(JNDIPersistentRepository persistent,
                                         JNDIHandle object, String filter,
                                         Object filterArgs[],
                                         SearchControls searchControls,
                                         Name rootName,
                                         boolean remote,
                                         boolean onlyLookup,
                                         boolean redirectable) throws java.rmi.RemoteException {

    this.rootName = rootName;
    this.remote = remote;
    this.onlyLookupAllowed = onlyLookup;
    this.redirectableContext = redirectable;

    try {
      if (searchControls == null) {
        searchControls = new SearchControls();
      }

      if (filterArgs != null) {
        String formattedFilter = SearchFilter.format(filter, filterArgs);
        this.searchFilter = new SearchFilter(formattedFilter);
      } else {
        this.searchFilter = new SearchFilter(filter);
      }

      byte[] data = persistent.readObject(object);
      JNDIHandle linkedContainer = persistent.getLinkedContainer(object);

      if (linkedContainer != null) { // is context
        String className = "javax.naming.Context";
        //byte[] dobj = enumClusterObject.getDirObject();
        Object objectToReturn = null;
        if (redirectableContext) {
          objectToReturn = new ServerContextRedirectableImpl(persistent.getNewConnection(), object, linkedContainer, ((Name) (rootName.clone())), remote);
          ((ServerContextRedirectableImpl) objectToReturn).onlyLookUpAllowed = this.onlyLookupAllowed;

        } else {
          objectToReturn = new ServerContextImpl(persistent.getNewConnection(), object, linkedContainer, ((Name) (rootName.clone())), remote);
          ((ServerContextImpl) objectToReturn).onlyLookUpAllowed = this.onlyLookupAllowed;
        }

        Attributes attr = data != null ? DirObject.getAttributes(data) : null;

        if (searchFilter.check(attr)) {
          BasicAttributes selectedAttr = new BasicAttributes();
          String[] attrsToReturn = searchControls.getReturningAttributes();

          if (attrsToReturn != null) {
            for (int i = 0; i < attrsToReturn.length; i++) {
              selectedAttr.put(attr.get(attrsToReturn[i]));
            }

            if (searchControls.getReturningObjFlag()) {
              nextSearchResult = new SearchResult("", className, objectToReturn, selectedAttr);
            } else {
              nextSearchResult = new SearchResult("", className, null, selectedAttr);
            }
          } else {
            if (searchControls.getReturningObjFlag()) {
              nextSearchResult = new SearchResult("", className, objectToReturn, attr);
            } else {
              nextSearchResult = new SearchResult("", className, null, attr);
            }
          }

          moreElements = true;
        } else {
          moreElements = false;
        }
      } else {
        //ClusterObject gobj = enumClusterObject.read(data);
        String className = null;

        if (data != null) {
          className = DirObject.getClassName(data);
        }

        Attributes attr = data != null ? DirObject.getAttributes(data) : null;

        if (searchFilter.check(attr)) {
          BasicAttributes selectedAttr = new BasicAttributes();
          String[] attrsToReturn = searchControls.getReturningAttributes();

          if (attrsToReturn != null) {
            Attribute tempAttr = null;
            for (int i = 0; i < attrsToReturn.length; i++) {
              // if the requested attribute set contains an attribute which is not present in the
              // attributes assosiated with the object - skip it! (by specification)
              tempAttr = attr.get(attrsToReturn[i]);
              if (tempAttr != null) {
                selectedAttr.put(tempAttr);
              }
            }

            if (searchControls.getReturningObjFlag()) {
              Object objectToReturn = data;
              //persistent.commit();
              nextSearchResult = new SearchResult("", className, objectToReturn, selectedAttr);
            } else {
              nextSearchResult = new SearchResult("", className, null, selectedAttr);
            }
          } else {
            if (searchControls.getReturningObjFlag()) {
              Object objectToReturn = data;
              //persistent.commit();
              nextSearchResult = new SearchResult("", className, objectToReturn, attr);
            } else {
              nextSearchResult = new SearchResult("", className, null, attr);
            }
          }

          moreElements = true;
        } else {
          moreElements = false;
        }
      }
    } catch (NamingException e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      RuntimeException re = new RuntimeException("Exception during search operation.", e);
      throw re;
    }
  }

  /**
   * Simply returns nextSearchResult and clears the moreElements flag
   *
   * @return Next SearchResult
   * @throws java.rmi.RemoteException When remote operation lead to problem
   * @throws NamingException When naming operation lead to problem
   */
  public Object next() throws java.rmi.RemoteException, NamingException {
    if (!moreElements) {
      throw new java.util.NoSuchElementException("There are no more elements in the naming enumeration.");
    } else {
      moreElements = false;
      return nextSearchResult;
    }
  }

  /**
   * Returns the moreElements flag
   *
   * @return Flag representing whether the only element is not "read" yet
   * @throws java.rmi.RemoteException When remote operation lead to problem
   */
  public boolean hasMore() throws java.rmi.RemoteException, NamingException {
    return moreElements;
  }

  /**
   * Closes the connection of persistent repository
   *
   * @throws java.rmi.RemoteException When remote operation lead to problem
   */
  public void close() throws java.rmi.RemoteException {

  }


}

