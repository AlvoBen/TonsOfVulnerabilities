package com.sap.engine.tools.offlinedeploy.rdb;

import java.util.ArrayList;

/**
 * ResultStatus implementation
 * @see com.sap.engine.tools.offlinedeploy.rdb.ResultStatus
 */
public class ResultStatusImpl implements ResultStatus {

  //result status
  private byte status;
  //holds the warning messages;
  private String[] warnings;
  //SDA archive path
  private String sdaPath;
  //CSN component
  private String csnComponent;
  //component runtime name
  private String runtimeName;

  ResultStatusImpl(ArrayList<String> warnings, String sdaPath, String csnComponent, String runtimeName) {
    this.warnings = warnings.toArray(new String[warnings.size()]);
    if (warnings.size() == 0) {
      this.status = SUCCESS;
    } else {
      this.status = WARNING;
    }
    this.sdaPath = sdaPath;
    this.csnComponent = csnComponent;
    this.runtimeName = runtimeName;
  }

  public byte getStatus() {
    return status;
  }

  public String[] getWarnings() {
    return warnings;
  }

  public String getSDAPath() {
    return sdaPath;
  }

  public String getCSNComponent() {
    return csnComponent;
  }

  public String getRuntimeName() {
    return runtimeName;
  }

}