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

package com.sap.engine.services.rmi_p4.interfaces;

/**
 * @author Mladen Droshev
 * @version 7.0
 */
public interface ConnectionObjectInt {

  public boolean isAlive();

  public int getDispId();

  public void setDispId(int dispId);

  public String getType() ;

  public void setType(String type);

  public int getConId() ;

  public void setConId(int conId) ;

  public String getHost();

  public void setHost(String host);

  public int getPort() ;

  public void setPort(int port);
}
