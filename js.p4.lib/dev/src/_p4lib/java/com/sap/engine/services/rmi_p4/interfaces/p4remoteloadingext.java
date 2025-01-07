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
 * @author Georgi Stanev, Mladen Droshev
 * @version 7.10C
 */
public interface P4RemoteLoadingExt {

  /**
   * indicate special list of jars that the remote object wich to be provided remotely
   * possilbe paths: -> j2ee/cluster/....
   * @return jars array
   */
  public String[] getResources();
}
