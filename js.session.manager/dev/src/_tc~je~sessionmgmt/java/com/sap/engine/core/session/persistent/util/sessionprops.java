package com.sap.engine.core.session.persistent.util;

import java.io.Serializable;

/**
 * User: pavel-b
 * Date: 2006-8-29
 * Time: 10:14:07
 */
public class SessionProps implements Serializable {
    private String id;
    private long creationTime = 0;
    private long expTime = 0;
    private String lockInfo = null;

    public SessionProps(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }

    public long getExpTime() {
      return expTime;
    }

    public void setExpTime(long expTime) {
      this.expTime = expTime;
    }

    public long getCreationTime() {
      return creationTime;
    }

    public void setCreationTime(long creationTime) {
      this.creationTime = creationTime;
    }

    public String getLockInfo() {
      return lockInfo;
    }

    public void setLockInfo(String lockInfo) {
      this.lockInfo = lockInfo;
    }
  }
