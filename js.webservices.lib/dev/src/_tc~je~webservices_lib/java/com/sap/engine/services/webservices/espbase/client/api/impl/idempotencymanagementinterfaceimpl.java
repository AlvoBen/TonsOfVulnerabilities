package com.sap.engine.services.webservices.espbase.client.api.impl;

import com.sap.engine.services.webservices.espbase.client.api.IdempotencyManagementInterface;
import com.sap.engine.services.webservices.espbase.client.bindings.ClientConfigurationContext;
import com.sap.engine.services.webservices.espbase.client.bindings.PublicProperties;

public class IdempotencyManagementInterfaceImpl implements IdempotencyManagementInterface {
  
  private ClientConfigurationContext ctx;
  
  public IdempotencyManagementInterfaceImpl(ClientConfigurationContext ctx) {
    this.ctx = ctx;
  }
  
  public void activateGlobalIdeopotency(boolean activate) {
    PublicProperties.setGlobalIdempotencyActive(activate, ctx);
  }
  
  public boolean isGlobalIdeopotencyActive() {
    return(PublicProperties.isGlobalIdempotencyActive(ctx));
  }
  
  public void setRetrySleep(long sleep) {
    if(sleep < 0) {
      throw new IllegalArgumentException("Incorrect retry sleep value : " + sleep + "!");
    }
    PublicProperties.setIdempotencyRetrySleep(sleep, ctx);
  }
  
  public long getRetrySleep() {
    return(PublicProperties.getIdempotencyRetrySleep(ctx));
  }
  
  public void setRetriesCount(int count) {
    if(count < 0) {
      throw new IllegalArgumentException("Incorrect retrys count value : " + count + "!");
    }
    PublicProperties.setIdempotencyRetriesCount(count, ctx);
  }
  
  public int getRetriesCount() {
    return(PublicProperties.getIdempotencyRetriesCount(ctx));
  }
}
