package com.sap.engine.services.rmi_p4.server;

/**
 * This class parse input String in format:
 * IDServerNode1:IDServerNode2/Object_KEY
 * Where IDServerNode1 can be int - with server ID or String - with cluster element name 
 */
public class P4Schema {

  public static final int DEFAULT_PORT = 3011;

  private String name = null;
  private int clusterId = 0;

  private String clusterName = null;
  private String objKey = null;
  private char sep = ':';
  int[] found;
  int index;

  public P4Schema(String name) {
    this.name = name;
    parseName();
  }

  void parseName() {
    init(name);
    index = name.indexOf('/');
    objKey = name.substring(index + 1);
    String temp = name.substring(0, index - 1);
    index = 0;

    if (found.length > 1) {
      try {
        clusterId = Integer.parseInt(temp.substring(found[index + 2] + 1, found[index + 3]));
      } catch (NumberFormatException _e) {
        //$JL-EXC$
        clusterId = 0;
      }
      clusterName = temp.substring(found[index + 3] + 1);
    } else {
      try {
        clusterId = Integer.parseInt(temp.substring(1));
      } catch (NumberFormatException e) {
        //$JL-EXC$
        clusterName = temp.substring(1);
      }
    }
  }

  protected void init(String n) {
    int count = 0;

    for (int i = 0; i < n.length(); i++) {
      if (n.charAt(i) == sep) {
        count++;
      }
    }

    found = new int[count];
    count = 0;

    for (int i = 0; i < n.length(); i++) {
      if (n.charAt(i) == sep) {
        found[count] = i;
        count++;
      }
    }
  }

  public int getClusterId() {
    return clusterId;
  }

  public String getClusterName() {
    return clusterName;
  }

  public String getObjKey() {
    return objKey;
  }

}

