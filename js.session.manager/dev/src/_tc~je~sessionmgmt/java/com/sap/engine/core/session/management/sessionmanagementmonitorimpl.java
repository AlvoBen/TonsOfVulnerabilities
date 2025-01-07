package com.sap.engine.core.session.management;

import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.session.exec.ClientContextImpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;

/**
 * @author Nikolai Neichev
 */
public class SessionManagementMonitorImpl implements SessionManagementMonitor {

  private SessionContextMonitor sessionContextMonitor = null;
  private static final String[] LOGGED_IN_USERS_HEADER = {"User", "Security Sessions", "Web Sessions"};

  public SessionManagementMonitorImpl() {
    sessionContextMonitor = new SessionContextMonitor();
  }

  public int getOpenSecuritySessionCount() {
    return ClientContextImpl.clientContexts().size();
  }

  public int getOpenWebSessionCount() {
    return sessionContextMonitor.getOpenWebSessionCount();
  }

  public int getActiveWebSessionCount() {
    return sessionContextMonitor.getActiveWebSessionCount();
  }

  public int getOpenEJBSessionCount() {
    return sessionContextMonitor.getOpenEJBSessionCount();
  }

  public int getLoggedInUsersCount() {
    return collectData().size();
  }

  public Serializable[][] getLoggedInUsersData() {
    HashMap map = collectData();
    int rows = map.size()+1;
    int sumWeb = 0;
    int sumSec = 0;
    Serializable[][] data = new Serializable[rows][3];
    int i = 0;
    for (Object o : map.entrySet()) {
      Map.Entry entry = (Map.Entry) o;
      String userID = (String) entry.getKey();
      UserStats stats = (UserStats) entry.getValue();
      data[i][0] = userID;
      data[i][1] = stats.userContexts;
      data[i][2] = stats.userAppSessions;
      sumSec += stats.userContexts;
      sumWeb += stats.userAppSessions;
      i++;
    }
    data[i][0] = "Total Users: "+(rows-1);
    data[i][1] = "Total Sec. Sessions: "+ sumSec;
    data[i][2] = "Total Web Sessions: " + sumWeb;
    return data;
  }

  public String[] getLoggedInUsersHeader() {
    return LOGGED_IN_USERS_HEADER;
  }

  public void registerManagementListener(ManagementListener listener) {
  }

  private HashMap<String, UserStats> collectData() {
    HashMap<String, UserStats> map = new HashMap<String, UserStats>();
    Collection col = ClientContextImpl.clientContexts();
     for (Object aCol : col) {
       ClientContextImpl context = (ClientContextImpl) aCol;
       String userID = context.getUser();
       UserStats stats = map.get(userID);
       if (stats == null) {
         stats = new UserStats(userID);
         map.put(userID, stats);
       }
       stats.userContexts++;
       stats.userAppSessions += context.appSessionsSize();
     }
     return map;
  }

  private class UserStats {

    public final String userID;
    public int userContexts = 0;
    public int userAppSessions = 0;

    public UserStats(String userID) {
      this.userID = userID;
    }
  }

}