/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sap.engine.services.httpserver.server.rcm;

import com.sap.engine.lib.rcm.ResourceConsumer;

/**
 *
 * @author i024157
 */
public class WebConsumer implements ResourceConsumer {
  public static final String WEB_RESOURCE_CONSUMER = "Web request";
  
  private String id = "empty";
  
  protected void set(String id) {
    this.id = id;
  }
  
  public String getType() {
    return WEB_RESOURCE_CONSUMER;
  }

  public String getId() {
    return id;
  }
}
