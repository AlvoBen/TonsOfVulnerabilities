/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.jmx.sso2;

import javax.management.openmbean.CompositeData;

/**
 * 
 * @author Krasimira Velikova
 */
public interface TicketIssuer extends CompositeData {
  
  /**
   * @return
   */
  public String getSystemID();
  
  /**
   * @return
   */
  public String getClient();
  
  /**
   * @return
   */
  public String getCertificateSubject();
  
  /**
   * @return
   */
  public String getCertificateIssuer();
}
