/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.mejb.notification;

import javax.management.NotificationListener;
import java.io.Serializable;

/**
 * Encapsulates listener and id. Resides only on the MEJB server side
 * Date: 2004-7-20
 * 
 * @author Nikolai Angelov
 */
public class IDHolder implements Serializable {
  int id;
  private transient NotificationListener listener;

  public IDHolder(int id) {
    this.id = id;
  }
  
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public NotificationListener getListener() {
    return listener;
  }

  public void setListener(NotificationListener reference) {
    this.listener = reference;
  }
}
