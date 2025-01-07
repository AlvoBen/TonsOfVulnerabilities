/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.services.httpserver.server;


import com.sap.engine.session.runtime.http.HttpSessionRequest;

/**
 * The class present the session management view of one http request.
 *   
 * @author georgi-s
 *
 */
public class SessionRequestImpl extends HttpSessionRequest {

  public SessionRequestImpl() {
    this.isProtected = true;
  }

  public String requestDescription() {
    return "Http Session Request";
  }

  public void setProtectionData(Object obj) {
    this.protectionData = obj;
  }  

  /**
   * Important!!!
   * This method should be invoke after invoking setProtectionData in ApplicationSelector (where the protection data are set for the request).
   * It sets the markid to the Session management depending on if the mark id is set before
   */
  public void setMarkIdInProtectionData(String markid) {
    if (this.protectionData != null && ((String[])protectionData)[1]==null) {
      //checks for protection data is string [2] - is necessary
      ((String[])protectionData)[1] = markid;
    }
    else if (this.protectionData == null) {
      //when there is no protection data
      this.protectionData = new String[] {null, markid};
    }
  }
}
