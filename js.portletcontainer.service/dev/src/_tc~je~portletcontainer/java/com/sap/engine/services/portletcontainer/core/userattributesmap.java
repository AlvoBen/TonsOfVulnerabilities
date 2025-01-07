/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.security.api.IUser;

/**
 * Map of the standard user attribtutes names (i.e. recommended by the portlet
 * specification) to the supported both in ABAP and LDAP attributes, exposed
 * via the UME IUser.
 *
 * Currently supportled attributes are:
 * user.jobtitle, user.department, user.name.given (first name),
 * user.name.family, user.home-info.telecom.telephone.number,
 * user.home-info.telecom.mobile.number, user.home-info.telecom.fax.number,
 * and user.home-info.online.email.
 *
 * @author Vera Buchkova
 * @version 7.10
 */
public class UserAttributesMap {

  private static final String NAMESPACE = "com.sap.security.core.usermanagement";

  //The following attributes names are mapped to IUser's get methods:
  private static final String USER_JOB_TITLE = "user.jobtitle";
  private static final String USER_DEPARTMENT = "user.department";
  private static final String USER_FIRST_NAME = "user.name.given";
  private static final String USER_LAST_NAME = "user.name.family";

  private static final String USER_TELEPHONE = "user.home-info.telecom.telephone.number";
  private static final String USER_CELLPHONE = "user.home-info.telecom.mobile.number";
  private static final String USER_FAX = "user.home-info.telecom.fax.number";
  private static final String USER_EMAIL = "user.home-info.online.email";

  private Set standardAttribtues = new HashSet(8);

  /**
   * Constructs a new map object and initializes the default attribute names mapping
   */
  public UserAttributesMap() {
    initStandardAttribtues();
  }

  private void initStandardAttribtues() {
    standardAttribtues.add(USER_JOB_TITLE);
    standardAttribtues.add(USER_DEPARTMENT);
    standardAttribtues.add(USER_FIRST_NAME);
    standardAttribtues.add(USER_LAST_NAME);
    standardAttribtues.add(USER_TELEPHONE);
    standardAttribtues.add(USER_CELLPHONE);
    standardAttribtues.add(USER_FAX);
    standardAttribtues.add(USER_EMAIL);
  }

  /**
   * Chcks whether attribute is supported in Portal/Portlet Container.
   * @return true if the attribute name is supported
   */
  public boolean isSupported(String attributeName) {
    return standardAttribtues.contains(attributeName);
  }

  /**
   * Returns an unmodifiable map object of the runtime attributes
   * names and values provided by the Portal.
   *
   * @param user the UME IUser object
   * @param userAttributesNames	the attributes names declared to be used in
   * 	the deployment descriptor of the portlet application
   * @return	unmodifiable map object; null, if the IUser is null.
   */
  public Map getAttributes(IUser user, Set userAttributesNames) {
    if (user == null || userAttributesNames == null || userAttributesNames.isEmpty()) {
      //the portlet application has not declared to use any user attributes
      return null;
    }
    /*
     * If the request is done in the context of an un-authenticated user,
     * calls to the getAttribute method of the request using the USER_INFO
     * constant must return null.
     */
    if (user == null) {
      return null;
    }

    Map result = getStandardAttribtues(user, userAttributesNames);

    /* Additional attributes that may be set through the UME API */
    for (Iterator it = userAttributesNames.iterator(); it.hasNext(); ) {
      String name = (String) it.next();
      if (user.getAttribute(NAMESPACE, name) != null) {
        result.put(name, user.getAttribute(NAMESPACE, name));
      }
    }

    return Collections.unmodifiableMap(result);
  }//getAttributes

  /**
   * Maps the standard IUser's attributes and the requested names
   * @param user
   * @param userAttributesNames	requested attribtue names
   * @return map containing String name value pairs
   */
  private Map getStandardAttribtues(IUser user, Set userAttributesNames) {
    HashMap result = new HashMap();
    /* Standard attributes from ABAP (SU01) and LDAP directory objects */
    if (userAttributesNames.contains(USER_JOB_TITLE) && user.getJobTitle() != null) {
      result.put(USER_JOB_TITLE, user.getJobTitle());
    }
    if (userAttributesNames.contains(USER_DEPARTMENT) && user.getDepartment() != null) {
      result.put(USER_DEPARTMENT, user.getDepartment());
    }
    if (userAttributesNames.contains(USER_FIRST_NAME) && user.getFirstName() != null) {
      result.put(USER_FIRST_NAME, user.getFirstName());
    }
    if (userAttributesNames.contains(USER_LAST_NAME) && user.getLastName() != null) {
      result.put(USER_LAST_NAME, user.getLastName());
    }
    if (userAttributesNames.contains(USER_TELEPHONE) && user.getTelephone() != null) {
      result.put(USER_TELEPHONE, user.getTelephone());
    }
    if (userAttributesNames.contains(USER_CELLPHONE) && user.getCellPhone() != null) {
      result.put(USER_CELLPHONE, user.getCellPhone());
    }
    if (userAttributesNames.contains(USER_FAX) && user.getFax() != null) {
      result.put(USER_FAX, user.getFax());
    }
    if (userAttributesNames.contains(USER_EMAIL) && user.getEmail() != null) {
      result.put(USER_EMAIL, user.getEmail());
    }
    return result;
  }
}
