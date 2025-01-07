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

import com.sap.engine.lib.util.ConcurrentArrayObject;
import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.engine.services.jndi.cluster.DirObject;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.JNDIHandleEnumeration;
import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.rmi.RemoteException;
import javax.naming.SizeLimitExceededException;

/**
 * Enumeration used when searching. "Search filter" implementation
 *
 * @author Petio Petev
 * @version 4.00
 */
class ServerNamingSearchFilterEnumImpl implements ServerNamingEnum {

	private final static Location LOG_LOCATION = Location.getLocation(ServerNamingSearchFilterEnumImpl.class);

  /**
   *  Type of operation (local, global)
   *
   */
  //  private short operation;
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
   * The level of recursion
   */
  private int levelPos = 0;
  /**
   * Vector of enumerations for each level of recursion
   */
  private ConcurrentArrayObject jes = null;
  /**
   * Vector of prefixes for names to each level of recursion
   */
  private ConcurrentArrayObject prefix = null;
  /**
   * Repository to take objects from
   */
  private JNDIPersistentRepository persistent = null;
  /**
   * The filter to satisfy according RFC 2254
   */
  private SearchFilter searchFilter = null;
  /**
   * The search controls containing information for qualification of objects and their type
   */
  private SearchControls searchControls;
  /**
   * Global number of objects return
   */
  private long objCount = 0;
  /**
   * Flag representing exceeding of maximum objects allowed to return
   */
  private boolean objCountExceeded = false;
  /**
   *  User defined sid of machin to perform local operations on
   *
   */
  //  private String userSid = null;
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
   * @param container The container to start to recurse in
   * @param parent parent object of the start container
   * @param dirObject DirObject of the start container
   * @param filter String filter to satisfy according RFC 2254
   * @param filterArgs Arguments to filter according RFC 2254
   * @param cons Search controls containg information on how the process will proceed
   * @param rootName Initial value of this.rootName
   * @param remote is remote
   * @param onlyLookup showing wether all operations are allowed in naming or only lookup
   * @throws java.rmi.RemoteException When there was error in remote procedures
   */
    public ServerNamingSearchFilterEnumImpl(JNDIPersistentRepository persistent,
                                            JNDIHandle parent,
                                            JNDIHandle container,
                                            byte[] dirObject,
                                            String filter,
                                            Object filterArgs[],
                                            SearchControls cons,
                                            Name rootName,
                                            boolean remote,
                                            boolean onlyLookup,
                                            boolean redirectable) throws java.rmi.RemoteException {

    this.remote = remote;
    this.rootName = rootName;
    this.onlyLookupAllowed = onlyLookup;
    this.redirectableContext = redirectable;

    try {
      // set the searchFilter variable appropriately
      if (filterArgs != null) {
        String formattedFilter = SearchFilter.format(filter, filterArgs);
        this.searchFilter = new SearchFilter(formattedFilter);
      } else {
        this.searchFilter = new SearchFilter(filter);
      }

      // initialize stacks
      jes = new ConcurrentArrayObject();
      prefix = new ConcurrentArrayObject();
      // zero level context
      prefix.add("");
      jes.add(persistent.listObjects(container, "*"));
      this.persistent = persistent;

      if (cons == null) {
        cons = new SearchControls();
      }

      this.searchControls = cons;
      Attributes attr = null;

      // if initial container's cluster object is not null - set the next to return object to container's
      // data if all conditions met
      if ((dirObject != null) && searchFilter.check(attr = DirObject.getAttributes(dirObject))) {
        // searchFilter.check passed
        if (attr == null) {
          attr = new BasicAttributes();
        }

        ++objCount;
        BasicAttributes selectedAttr = new BasicAttributes();
        String[] attrsToReturn = searchControls.getReturningAttributes();

        if (attrsToReturn != null) {
          for (int i = 0; i < attrsToReturn.length; i++) {
            if (attrsToReturn[i] != null) {
              Attribute attribute = attr.get(attrsToReturn[i]);

              if (attribute != null) {
                selectedAttr.put(attribute);
              }
            }
          }

          if (searchControls.getReturningObjFlag()) {
            ServerContextImpl sc = null;
            if (redirectableContext) {
              sc = new ServerContextRedirectableImpl(persistent.getNewConnection(), parent, persistent.getLinkedContainer(parent), ((Name) (rootName.clone())), remote);
              sc.onlyLookUpAllowed = this.onlyLookupAllowed;
            } else {
              sc = new ServerContextImpl(persistent.getNewConnection(), parent, persistent.getLinkedContainer(parent), ((Name) (rootName.clone())), remote);
              sc.onlyLookUpAllowed = this.onlyLookupAllowed;
            }
            nextSearchResult = new SearchResult("", "javax.naming.Context", sc, selectedAttr);
          } else {
            nextSearchResult = new SearchResult("", "javax.naming.Context", null, selectedAttr);
          }
        } else {
          if (searchControls.getReturningObjFlag()) {
            ServerContextImpl sc = null;
            if (redirectableContext) {
              sc = new ServerContextRedirectableImpl(persistent.getNewConnection(), parent, persistent.getLinkedContainer(parent), ((Name) (rootName.clone())), remote);
              sc.onlyLookUpAllowed = this.onlyLookupAllowed;
            } else {
              sc = new ServerContextImpl(persistent.getNewConnection(), parent, persistent.getLinkedContainer(parent), ((Name) (rootName.clone())), remote);
              sc.onlyLookUpAllowed = this.onlyLookupAllowed;
            }
            nextSearchResult = new SearchResult("", "javax.naming.Context", sc, attr);
          } else {
            nextSearchResult = new SearchResult("", "javax.naming.Context", null, attr);
          }
        }
      } else {
        // cluster is null, advance to next
        next();
      }
    } catch (NamingException ne) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", ne);
      RemoteException re = new RemoteException("Exception during search operation.", ne);
      throw re;
    }
  }

  /**
   * Returns next element of the enumeration. Qualified objects are chosen by their attributes according RFC 2254 and the locallity of operation.
   *
   * @return The next SearchResult
   * @throws java.rmi.RemoteException When there was error in remote procedures
   * @throws NamingException When there was error in naming procedures
   */
  public Object next() throws java.rmi.RemoteException, NamingException {
    // advances to the next object in current container, when none left
    // go to previous container, and proceed there, if the current level is 0,
    // clear the moreElements flag
    if (!moreElements) {
      throw new java.util.NoSuchElementException("There are no more elements in the naming enumeration.");
    }

    try {
      if (((JNDIHandleEnumeration) jes.elementAt(levelPos)).hasMoreElements()) {
        // get the next object in current enumeration
        JNDIHandle jh = ((JNDIHandleEnumeration) jes.elementAt(levelPos)).nextObject(); // next Object
        boolean found = false;
        Attributes attr = null;
        String className = null;
        String objName = null;
        Object object = null;
        SearchResult tempSearchResult = null;

        do {
          // loop until conditions are met
          byte[] dirObj = persistent.readObject(jh);
          objName = persistent.getObjectName(jh);

          if (objName != null) {
            JNDIHandle linkedContainer = persistent.getLinkedContainer(jh);

            if (linkedContainer != null) { //context
              // Context - ser the next to return object, and dive into the context
              className = "javax.naming.Context";
              byte[] dobj = dirObj;
              if (redirectableContext) {
                object = new ServerContextRedirectableImpl(persistent.getNewConnection(), jh, linkedContainer, ((Name) (rootName.clone())), remote);
                ((ServerContextRedirectableImpl) object).onlyLookUpAllowed = this.onlyLookupAllowed;
              } else {
                object = new ServerContextImpl(persistent.getNewConnection(), jh, linkedContainer, ((Name) (rootName.clone())), remote);
                ((ServerContextImpl) object).onlyLookUpAllowed = this.onlyLookupAllowed;
              }
              attr = dobj != null ? DirObject.getAttributes(dobj) : null;

              if (searchControls.getSearchScope() == SearchControls.SUBTREE_SCOPE) {
                // dive only if SUBTREE_SCOPE is needed
                if (levelPos != 0) {
                  prefix.add((String) prefix.elementAt(levelPos) + "/" + objName);
                } else {
                  prefix.add(objName);
                }

                // clear the name - it is in the prefix now
                objName = "";
                // dive into next level
                levelPos++;
                jes.add(persistent.listObjects(persistent.getLinkedContainer(jh), "*"));
              }

              found = true;
            } else {
              object = dirObj;

              if (object != null) {
                className = DirObject.getClassName((byte[]) object);
                attr = DirObject.getAttributes((byte[]) object);
              } else {
                className = null;
                attr = null;
              }

              found = true;
            }

            if (moreElements && found) {
              // if there are more elements in the enumeration, check with filter
              if (attr == null) {
                attr = new BasicAttributes();
              }

              if (searchFilter.check(attr)) {
                BasicAttributes selectedAttr = new BasicAttributes();
                String[] attrsToReturn = searchControls.getReturningAttributes();

                if (linkedContainer != null || levelPos == 0) {
                  objName = (String) prefix.elementAt(levelPos) + objName;
                } else {
                  objName = (String) prefix.elementAt(levelPos) + "/" + objName;
                }

                tempSearchResult = null;

                if (attrsToReturn != null) {
                  for (int i = 0; i < attrsToReturn.length; i++) {
                    if (attrsToReturn[i] != null) {
                      Attribute attribute = attr.get(attrsToReturn[i]);

                      if (attribute != null) {
                        selectedAttr.put(attribute);
                      }
                    }
                  }

                  if (searchControls.getReturningObjFlag()) {
                    tempSearchResult = new SearchResult(objName, className, object, selectedAttr);
                  } else {
                    tempSearchResult = new SearchResult(objName, className, null, selectedAttr);
                  }
                } else {
                  if (searchControls.getReturningObjFlag()) {
                    tempSearchResult = new SearchResult(objName, className, object, attr);
                  } else {
                    tempSearchResult = new SearchResult(objName, className, null, attr);
                  }
                }

                found = true;
              } else {
                // advance to next one
                if (((JNDIHandleEnumeration) jes.elementAt(levelPos)).hasMoreElements()) {
                  found = false;
                  jh = ((JNDIHandleEnumeration) jes.elementAt(levelPos)).nextObject();
                } else {
                  while ((levelPos > 0) && (!((JNDIHandleEnumeration) jes.elementAt(levelPos)).hasMoreElements())) {
                    levelPos--;
                    jes.removeElementAt(jes.size() - 1);
                    prefix.removeElementAt(prefix.size() - 1);
                  }

                  if (((JNDIHandleEnumeration) jes.elementAt(levelPos)).hasMoreElements()) {
                    found = false;
                    jh = ((JNDIHandleEnumeration) jes.elementAt(levelPos)).nextObject();
                  } else {
                    found = true;
                    moreElements = false;
                  }
                }
              }
            }
          } else { // not a valid object handle anymore
            // advance to next one
            if (((JNDIHandleEnumeration) jes.elementAt(levelPos)).hasMoreElements()) {
              found = false;
              jh = ((JNDIHandleEnumeration) jes.elementAt(levelPos)).nextObject();
            } else {
              while ((levelPos > 0) && (!((JNDIHandleEnumeration) jes.elementAt(levelPos)).hasMoreElements())) {
                levelPos--;
                jes.removeElementAt(jes.size() - 1);
                prefix.removeElementAt(prefix.size() - 1);
              }

              if (((JNDIHandleEnumeration) jes.elementAt(levelPos)).hasMoreElements()) {
                found = false;
                jh = ((JNDIHandleEnumeration) jes.elementAt(levelPos)).nextObject();
              } else {
                found = true;
                moreElements = false;
              }
            }
          }
        } while (!found);

        // count limit check
        if (searchControls.getCountLimit() != 0) {
          if (++objCount > searchControls.getCountLimit()) {
            moreElements = false;
            objCountExceeded = true;
            levelPos = 0;
          }
        }

        SearchResult oldSearchResult = nextSearchResult;
        nextSearchResult = tempSearchResult;

        //        persistent.commit();
        return oldSearchResult;
      } else {
        if (levelPos == 0) {
          // previous was the last object at all
          moreElements = false;
          return nextSearchResult;
        } else {
          // decrease the level
          levelPos--;
          jes.removeElementAt(jes.size() - 1);
          prefix.removeElementAt(prefix.size() - 1);
          return next();
        }
      }
    } catch (NamingException e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      javax.naming.NamingException nge = new javax.naming.NamingException("Exception during search operation.");
      nge.setRootCause(e);
      throw nge;
    }
  }

  /**
   * Returns the state of the enumeration in terms of object availability
   *
   * @return Flag representing the state whether there are more objects in the enumeration
   * @throws java.rmi.RemoteException When there was error in remote procedures
   */
  public boolean hasMore() throws java.rmi.RemoteException, NamingException {
    // simply return the flag
    if (objCountExceeded) {
      moreElements = false;

      for (int i = jes.size() - 1; i >= 0; i--) {
        ((JNDIHandleEnumeration) jes.elementAt(i)).closeEnumeration();
      }
      SizeLimitExceededException slee = new SizeLimitExceededException("The object requested exceeds the given maximum number of objects in a search operation.");
      throw slee;
    }

    if (moreElements == false) {
      for (int i = jes.size() - 1; i >= 0; i--) {
        ((JNDIHandleEnumeration) jes.elementAt(i)).closeEnumeration();
      }
    }

    return moreElements;
  }

  /**
   * Closes the connection of persistent repository
   *
   * @throws java.rmi.RemoteException When there was error in remote procedures
   */
  public void close() throws java.rmi.RemoteException {
    moreElements = false;
  }


}

