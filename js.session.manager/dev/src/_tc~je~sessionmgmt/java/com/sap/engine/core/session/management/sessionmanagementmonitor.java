package com.sap.engine.core.session.management;

import com.sap.engine.frame.state.ManagementInterface;
import java.io.Serializable;

/**
 * User: pavel-b
 * Date: 2006-7-18
 * Time: 11:41:41
 */
public interface SessionManagementMonitor extends ManagementInterface {
  public int getOpenSecuritySessionCount();
  public int getOpenWebSessionCount();
  public int getOpenEJBSessionCount();

  public int getActiveWebSessionCount();

  public int getLoggedInUsersCount();
  public Serializable[][] getLoggedInUsersData();
  public String[] getLoggedInUsersHeader();
}
