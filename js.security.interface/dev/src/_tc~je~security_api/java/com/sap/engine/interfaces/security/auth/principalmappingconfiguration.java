/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security.auth;

import java.util.*;
import javax.security.auth.login.AppConfigurationEntry;

import com.sap.engine.lib.util.HashMapObjectObject;
import com.sap.engine.interfaces.security.userstore.UserStore;

/**
 *  Principal mapping configuration for a user mapping login module.
 * Each user is acting as a preconfigured target user of the other user store.
 * The mapping for different users can be different.
 *
 *  The mapping login modules are used for JCA resource adapters.
 *
 * @author  Ekaterina Zheleva
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @see com.sap.engine.interfaces.security.AuthenticationContext
 */
public class PrincipalMappingConfiguration extends AppConfigurationEntry {

  /**
   *  Key for the user store to be used by the configuration entry in the options.
   *
   *  Value is "User_Store".
   */
  public static final String USER_STORE = "User_Store";

  /**
   *  Managed Connection Factory ( MCF ) associated with the configuration entry.
   *
   *  Value is "Managed_Connection_Factory".
   */
  public static final String MCF = "Managed_Connection_Factory";

  /**
   *  Key for the user mapping to be used by the configuration entry in the options.
   *
   *  Value is "Users_Mapping".
   */
  public static final String USER_MAPPING = "Users_Mapping";

  /**
   *  Key for the group mapping to be used by the configuration entry in the options.
   *
   *  Value is "Groups_Mapping".
   */
  public static final String GROUP_MAPPING = "Groups_Mapping";

  /**
   *   Field containing the user mapping for use by the login module.
   */
  private HashMapObjectObject userMapping = null;

  /**
   *   Field containing the group mapping for use by the login module.
   */
  private HashMapObjectObject groupMapping = null;

  /**
   *   Field containing the options of the login module.
   */
  private Map options = null;

  /**
   *  Constructs the entry with a login module class name, flag and options.
   *
   * @param  module   class name of the login module
   * @param  flag     OPTIONAL, REQUIRED, REQUISITE or SUFFICIENT
   * @param  options  options of the login module
   */
  public PrincipalMappingConfiguration(String module, AppConfigurationEntry.LoginModuleControlFlag flag, Map options) {
    super(module, flag, options);
    this.options = options;
    userMapping = convert((Map) options.get(USER_MAPPING));
    groupMapping = convert((Map) options.get(GROUP_MAPPING));
  }

  /**
   *  Constructs the entry with options.
   *  Login module class name is:
   *    com.sap.engine.services.security.server.jaas.mapping.PrincipalMappingLoginModule
   *  Flag is REQUIRED.
   *
   * @param  options  options of the login module
   */
  public PrincipalMappingConfiguration(Map options) {
    super("com.sap.engine.services.security.server.jaas.mapping.PrincipalMappingLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
    this.options = options;
  }

  /**
   *  Tests if the group of the given user store is mapped to the specified identity.
   *
   * @param userStore  user store name.
   * @param groupName  target group name
   * @param identity   target identity
   *
   * @return  true if the mapping is valid, otherwise false.
   */
  public boolean checkGroupMapping(UserStore userStore, String groupName, String identity) {
    String target = (String) groupMapping.get(groupName);

    if (target != null) {
      return (target.equals(identity));
    }

    Iterator parents = userStore.getGroupContext().getGroupInfo(groupName).getParentGroups();

    while (parents.hasNext()) {
      if (checkGroupMapping(userStore, (String) parents.next(), identity)) {
        return true;
      }
    }

    return false;
  }

  /**
   *  Tests if the user of the given user store is mapped to the specified identity.
   *
   * @param userStore  user store name.
   * @param userName   target user name
   * @param identity   target identity
   *
   * @return  true if the mapping is valid, otherwise false.
   */
  public boolean checkUserMapping(UserStore userStore, String userName, String identity) {
    String target = (String) userMapping.get(userName);

    if (target != null) {
      return (target.equals(identity));
    }

    Iterator parents = userStore.getUserContext().getUserInfo(userName).getParentGroups();

    while (parents.hasNext()) {
      if (checkGroupMapping(userStore, (String) parents.next(), identity)) {
        return true;
      }
    }

    return false;
  }

  /**
   *  Returns the identity mapping of the specified group.
   *
   * @param groupName  not used
   *
   * @return  table of the identities mapping for groups.
   */
  public HashMapObjectObject getGroupMapping(String groupName) {
    return groupMapping;
  }

  /**
   *  Returns the options of the login module.
   *
   * @return  options of the login module.
   */
  public Map getOptions() {
    if (options != null && userMapping != null) {
      options.put(USER_MAPPING, userMapping);
    }
    if (options != null && groupMapping != null) {
      options.put(GROUP_MAPPING, groupMapping);
    }
    return options;
  }

  /**
   *  Returns the identity mapping of the specified user.
   *
   * @param userName  not used
   *
   * @return  table of the identities mappings for users.
   */
  public HashMapObjectObject getUserMapping(String userName) {
    return userMapping;
  }

  /**
   *  Adds user mapping from the specified group to the identity.
   *
   * @param groupName  target user name
   * @param identity  target identity
   */
  public void mapGroup(String groupName, String identity) {
    groupMapping.put(groupName, identity);
  }

  /**
   *  Adds user mapping from the specified user to the identity.
   *
   * @param userName  target user name
   * @param identity  target identity
   */
  public void mapUser(String userName, String identity) {
    userMapping.put(userName, identity);
  }

  /**
   *  Removes group mapping from the specified group to the identity.
   *
   * @param groupName  target group name
   * @param identity   target identity
   */
  public void unmapGroup(String groupName, String identity) {
    groupMapping.remove(groupName);
  }

  /**
   *  Removes user mapping from the specified user to the identity.
   *
   * @param userName  target user name
   * @param identity  target identity
   */
  public void unmapUser(String userName, String identity) {
    userMapping.remove(userName);
  }

  /**
   *  Method used to convert user mapping into table that is used by the login module.
   *
   * @param  map  external representation
   *
   * @return  internal representation
   */
  private final static HashMapObjectObject convert(Map map) {
    if (map instanceof HashMapObjectObject) {
      return (HashMapObjectObject) map;
    } else {
      HashMapObjectObject result = new HashMapObjectObject();
      Iterator keys = map.keySet().iterator();
      Object key = null;

      while (keys.hasNext()) {
        key = keys.next();
        result.put(key, map.get(key));
      }

      return result;
    }
  }

}

