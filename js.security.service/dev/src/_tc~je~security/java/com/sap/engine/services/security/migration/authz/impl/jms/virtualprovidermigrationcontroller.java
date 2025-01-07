package com.sap.engine.services.security.migration.authz.impl.jms;

import java.util.Hashtable;


public class VirtualProviderMigrationController {
  private Hashtable roleMap = null;
  private String name = null;
  private String path = null;
  
  private static final String JMS_CONFIGURATION_PATH_PREFIX = "jms_provider/";
  private static final String SECURITY_SUFFIX = "/@SecurityInfo@/";
 
  private J2EERolesMigrationController        roles = null;
  
  private VPAdministrationMigrationController administration = null;
  private VPQueueMigrationController          queue          = null;
  private VPTempQueueMigrationController      tempQueue      = null;
  private VPTopicMigrationController          topic          = null;
  private VPTempTopicMigrationController      tempTopic      = null;
  
  public VirtualProviderMigrationController(String name) {
    this.name = name;
    this.path = JMS_CONFIGURATION_PATH_PREFIX + name + SECURITY_SUFFIX;  
  
    roles = new J2EERolesMigrationController(name, path);
    
    administration = new VPAdministrationMigrationController(name, path);
    queue          = new VPQueueMigrationController(name, path);
    tempQueue      = new VPTempQueueMigrationController(name, path);
    topic          = new VPTopicMigrationController(name, path);
    tempTopic      = new VPTempTopicMigrationController(name, path);
  }
  
  public void migrate() throws Exception {
    roles.migrate();
    roleMap = roles.getResultMappings();

    /**
     * A patch specially for the JMS.
     * There is inconsistency between 6.40(6.45) and 7.10
     * So in order to fix it the migration changes the 
     * j2ee role guests with the UME Role Evryone.
     */
    if (roleMap.get("guests") != null) {
      roleMap.put("guests", "Everyone");
    }
    
    administration.init(roleMap);
    administration.migrate();
    
    queue.init(roleMap);
    queue.migrate();
    
    tempQueue.init(roleMap);
    tempQueue.migrate();
    
    topic.init(roleMap);
    topic.migrate();
    
    tempTopic.init(roleMap);
    tempTopic.migrate();
  }
}
