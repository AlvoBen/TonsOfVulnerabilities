/*
 * Copyright (c) 2000-2009 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import com.sap.engine.services.httpserver.chain.impl.HTTPRequestImpl;
import com.sap.engine.services.httpserver.chain.impl.HTTPResponseImpl;

public class ClientRequest {
  
  private Client client = null;
  private HTTPRequestImpl request =  null;
  private HTTPResponseImpl response = null;

  public ClientRequest() {
    
  }

  public void initClientRequest(Client client, HTTPRequestImpl request, HTTPResponseImpl response) {
    this.client = client;
    this.request = request;
    this.response = response;
    this.request.init(this.client);
    this.response.init(this.client);
  }

  public Client getClient() {
    return client;
  }

  public HTTPRequestImpl getRequest() {
    return request;
  }
  
  public HTTPResponseImpl getResponse() {
    return response;
  }

  public void setClient(Client client) {
    this.client = client;
  }
}
