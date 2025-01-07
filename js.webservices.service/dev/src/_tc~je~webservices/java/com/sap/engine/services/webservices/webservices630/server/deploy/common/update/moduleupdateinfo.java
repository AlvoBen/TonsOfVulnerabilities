package com.sap.engine.services.webservices.webservices630.server.deploy.common.update;

import java.io.File;
import java.util.Properties;
import java.util.Hashtable;

/**
 * Title: ModuleUpdateInfo
 * Description: The class is a container for  deleted, newly deployed and updated modules.
 * Copyright: Copyright (c) 2004
 * Company: Sap Labs Sofia
 * 
 * @author Dimitrina Stoyanova
 * @version 6.30
 */

public class ModuleUpdateInfo {

  private Properties moduleMappingsForDelete = new Properties();
  private File[] modulesForDeploy= new File[0];
  private Properties modulesForUpdateOldMappings = new Properties();
  private Hashtable modulesForUpdateNewFiles = new Properties();

  public ModuleUpdateInfo() {
  }

  public Properties getModuleMappingsForDelete() {
    return moduleMappingsForDelete;
  }

  public void setModuleMappingsForDelete(Properties moduleMappingsForDelete) {
    this.moduleMappingsForDelete = moduleMappingsForDelete;
  }

  public File[] getModulesForDeploy() {
    return modulesForDeploy;
  }

  public void setModulesForDeploy(File[] modulesForDeploy) {
    this.modulesForDeploy = modulesForDeploy;
  }

  public Properties getModulesForUpdateOldMappings() {
    return modulesForUpdateOldMappings;
  }

  public void setModulesForUpdateOldMappings(Properties modulesForUpdateOldMappings) {
    this.modulesForUpdateOldMappings = modulesForUpdateOldMappings;
  }

  public Hashtable getModulesForUpdateNewFiles() {
    return modulesForUpdateNewFiles;
  }

  public void setModulesForUpdateNewFiles(Hashtable modulesForUpdateNewFiles) {
    this.modulesForUpdateNewFiles = modulesForUpdateNewFiles;
  }


}
