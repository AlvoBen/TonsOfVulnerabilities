package com.sap.engine.services.rmi_p4.all;

/**
 * @author Ventcislav Dimitrov
 * @version 6.30
 */
public interface MessageConstants {

  public final static int COMMUNICATION_CONTAINER_STARTED = 0;
  public final static int PROFILE_REQUEST = 1;
  public final static int PROFILE_REPLY = 11;
  public final static int OPENSOCKET_REQUEST = 2;
  public final static int GETSOCKET_REQUEST = 3;
  public final static int GET_CONNECTIONS_REQUEST = 4;
  public final static int OPENSOCKET_REPLY = 12;
  public final static int UPDATE_CONNECTION = 13;
  public final static int NEW_PROFILE_IS_AVAILABLE = 6;
  public final static int BROKERID_REQUEST = 1;
  public final static int OBJECT_REQUEST = 14; // servers communication - between 2 servers
  public final static int CLUSTER_COMMUNICATION = 16;
  public final static int APPCLUSTER_COMMUNICATION = 26;
  public final static int REDIRECTED_APPCLUSTER_COMMUNICATION = 27;
  public final static int GET_HTTP_PORT = 17;
  public final static int REDIRECTABLE_OBJECT = 18; // for redirecting message from one to other server
  public static final int SEARCH_OTHER_REDIRECTED_SERVERS = 19; // for searching other servers which had redirected with a application on
}

