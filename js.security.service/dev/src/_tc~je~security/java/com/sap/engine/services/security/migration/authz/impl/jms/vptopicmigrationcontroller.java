﻿package com.sap.engine.services.security.migration.authz.impl.jms;

import com.sap.engine.services.security.migration.authz.InstanceActionMappingEntry;
import com.sap.engine.services.security.migration.authz.ResourceDescriptor;
import com.sap.engine.services.security.migration.authz.ResourceMigrationController;

public class VPTopicMigrationController extends ResourceMigrationController{
  public static final String SERVICE_NAME = "jms_provider";

  private String path = null;
  private String instanceName = null;
  
  public VPTopicMigrationController(String instanceName, String path) {
    super(SERVICE_NAME, "topic");
    this.path = path;
    this.instanceName = instanceName;
  }

  public String getConfigurationPath() {
    return path;
  }

  public String getMigrationPermissionClass() {
    return "com.sap.jms.server.service.impl.JMSDestinationPermission";
  }

  public String getMigrationPermissionName() {
    return instanceName + ".topic"; 
  }

  public ResourceDescriptor getResourceDescriptor() {
    InstanceActionMappingEntry entry = new InstanceActionMappingEntry("ALL", "ALL", new String[]{"administrators", "clients"});
    return new ResourceDescriptor(new InstanceActionMappingEntry[]{entry});
  }

  public String getActionPrefix() {
    return "topic";
  }

}
