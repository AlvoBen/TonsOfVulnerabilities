package com.sap.engine.services.security.patch;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.lib.lang.ConvertTools;

public class Change2 implements Change {
  private static final String ROOT = "root";
  private static final String ALL = "all";
  private final static String GROUPS_LIST = "groups";
  private static final String DEFAULT_USERSTORE = "DBMS User Store";
  private static final String DEFAULT_USERSTORE_ROLES_PATH = SecurityConfigurationPath.ROLES_PATH + "/" + DEFAULT_USERSTORE;
  private static final String SECURITY_USERSTORES_DEFAULT_PATH = SecurityConfigurationPath.USERSTORES_PATH + "/" + DEFAULT_USERSTORE;
  private static final String GROUP_CONTAINER_NAME = "dbuserstore.groups";
  private static final String TREE_CONTAINER_NAME  = "dbuserstore.tree";
  private static final String GROUP_TREE_CONTAINER = "dbuserstore.tree.groups";

  private ConfigurationHandler configHandler = null;
  private ConvertTools convertTool = new ConvertTools(false);

  public void run() throws Exception {
    configHandler = null;
    try {
      configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
      scanSecurityRolesStorage(null);
      Configuration configuration = configHandler.openConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, ConfigurationHandler.WRITE_ACCESS);
      String[] allSecurityConfigurations = configuration.getAllSubConfigurationNames();
      for (int i = 0; i < allSecurityConfigurations.length; i++) {
        scanSecurityRolesStorage(configuration.getSubConfiguration(allSecurityConfigurations[i]));
      }
      scanSecurityUserstoresDefaultStorage();
      scanDefaultUserstoreStorage();
      configHandler.commit();
    } catch (Exception e) {
      try {
        configHandler.rollback();
      } catch (Exception re) {
        throw re;
      }
      throw e;
    } finally {
      try {
        configHandler.closeAllConfigurations();
      } catch (Exception e) {
        throw e;
      }
    }
  }

  private void scanSecurityRolesStorage(Configuration configuration) throws Exception {
    Configuration config = null;
    try {
      if (configuration == null) {
        config = configHandler.openConfiguration(DEFAULT_USERSTORE_ROLES_PATH, ConfigurationHandler.WRITE_ACCESS);
      } else {
        config = configuration.getSubConfiguration(DEFAULT_USERSTORE_ROLES_PATH);
      }
    } catch (NameNotFoundException nnfe) {
      //$JL-EXC$
      return;
    }

    if (config != null) {
      String[] allRoleEntries = config.getAllSubConfigurationNames();
      Configuration roleConfig = null;
      for (int i = 0; i < allRoleEntries.length; i++) {
        roleConfig = config.getSubConfiguration(allRoleEntries[i]);
        if (!roleConfig.existsSubConfiguration(GROUPS_LIST)) {
          continue;
        }
        roleConfig = roleConfig.getSubConfiguration(GROUPS_LIST);
        if (!roleConfig.existsConfigEntry(ROOT)) {
          continue;
        }
        roleConfig.deleteConfigEntry(ROOT);
        roleConfig.addConfigEntry(ALL, "");
      }
    }
  }

  private void scanSecurityUserstoresDefaultStorage() throws Exception {
    Configuration config = null;
    try {
      config = configHandler.openConfiguration(SECURITY_USERSTORES_DEFAULT_PATH + "/configuration", ConfigurationHandler.WRITE_ACCESS);
    } catch (NameNotFoundException nnfe) {
      //$JL-EXC$
      return;
    }
    if (config != null) {
      String[] allEntries = config.getAllConfigEntryNames();
      String value = null;
      for (int i = 0; i < allEntries.length; i++) {
        value = (String) config.getConfigEntry(allEntries[i]);
        if (allEntries[i].endsWith(".parentGroups") && value.indexOf(ROOT) > -1) {
          config.modifyConfigEntry(allEntries[i], value.substring(0, value.indexOf(ROOT)) + ALL + value.substring(value.indexOf(ROOT) + ROOT.length()));
        }
      }
    }
  }

  private void scanDefaultUserstoreStorage() throws Exception {
    Configuration config = null;
    try {
      config = configHandler.openConfiguration("userstore", ConfigurationHandler.WRITE_ACCESS);
    } catch (NameNotFoundException nnfe) {
      //$JL-EXC$
      return;
    }
    if (config != null) {
      Configuration subConfig = config.getSubConfiguration(GROUP_CONTAINER_NAME);
      String[] allGroups = subConfig.getAllConfigEntryNames();
      for (int i = 0; i < allGroups.length; i++) {
        if (allGroups[i].equals(ROOT)) {
          subConfig.deleteConfigEntry(ROOT);
          subConfig.addConfigEntry(ALL, groupToByteArray());
          break;
        }
      }
      int rootSid = 0;
      subConfig = config.getSubConfiguration(TREE_CONTAINER_NAME + "/" + GROUP_TREE_CONTAINER);
      allGroups = subConfig.getAllConfigEntryNames();
      for (int i = 0; i < allGroups.length; i++) {
        if (allGroups[i].equals(ROOT)) {
          byte[] rootValue = (byte[]) subConfig.getConfigEntry(ROOT);
          subConfig.deleteConfigEntry(ROOT);
          subConfig.addConfigEntry(ALL, rootValue);
          rootSid = Convert.byteArrToInt(rootValue, 0);
          break;
        }
      }
      if (rootSid > 0) {
        subConfig = config.getSubConfiguration(TREE_CONTAINER_NAME);
        String[] allSids = subConfig.getAllConfigEntryNames();
        for (int i = 0; i < allSids.length; i++) {
          if (allSids[i].equals(Integer.toString(rootSid))) {
            byte[] rootValue = (byte[]) subConfig.getConfigEntry(allSids[i]);
            subConfig.modifyConfigEntry(allSids[i], new TreeLinkNode(rootValue).toByteArray());
            break;
          }
        }
      }
    }
  }

  private byte[] groupToByteArray() {
    byte[] bytes = null;
    bytes = new byte[4 + ALL.length() * 2];
    convertTool.intToArr(ALL.length() * 2, bytes, 0);
    Convert.writeUStringToByteArr(bytes, 4, ALL);

    return bytes;
  }

  class TreeLinkNode {

    private int sid;
    private String name;
    private int[] children;
    private int[] parents;

    TreeLinkNode(byte[] data) {
      int len = convertTool.arrToInt(data, 0);
      byte[] newData = new byte[len];
      System.arraycopy(data, 4, newData, 0, len);
      readNode(this, newData);
    }

    byte[] toByteArray() {
      byte[] result = new byte[(name.length() * 2) + 4 * (children.length + parents.length + 3)];

      convertTool.intToArr(sid, result, 0);
      convertTool.intToArr(parents.length, result, 4);
      convertTool.intToArr(children.length, result, 8);

      int position = 12;
      for (int i = 0; i < parents.length; i++, position += 4) {
        convertTool.intToArr(parents[i], result, position);
      }

      for (int i = 0; i < children.length; i++, position += 4) {
        convertTool.intToArr(children[i], result, position);
      }

      Convert.writeUStringToByteArr(result, position, name);

      byte[] newResult = new byte[4 + result.length];
      convertTool.intToArr(result.length, newResult, 0);
      System.arraycopy(result, 0, newResult, 4, result.length);
      return newResult;
    }

    void readNode(TreeLinkNode node, byte[] array) {
      int[] ids = null;
      this.sid = byteArrayToInt(array, 0);
      ids = new int[byteArrayToInt(array, 4)];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = byteArrayToInt(array, i*4 + 12);
      }
      this.parents = ids;

      int position = ids.length*4 + 12;
      ids = new int[byteArrayToInt(array, 8)];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = byteArrayToInt(array, position + i*4);
      }
      this.children = ids;

      this.name = ALL;
    }

    private final int byteArrayToInt(byte[] array, int offset) {
      int result = 0;

      //result |= ((int) (array[offset] & 0xFF)) << 32;
      result |= ((int) (array[offset + 1] & 0xFF)) << 16;
      result |= ((int) (array[offset + 2] & 0xFF)) << 8;
      result |= (int) (array[offset + 3] & 0xFF);

      return result;
    }
  }
}

