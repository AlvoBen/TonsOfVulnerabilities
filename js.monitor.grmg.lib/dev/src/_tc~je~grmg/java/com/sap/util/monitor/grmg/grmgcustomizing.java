/*
 *  last change 2003-11-10 
 */

/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.util.monitor.grmg;

import java.util.ArrayList;

/**
 * @author Miroslav Petrov
 * @version 6.30
 */
public class GrmgCustomizing {
  private boolean grmgRunsFlag = true;
  private boolean runLogFlag = false;
  private boolean errorLogFlag = false;
  private ArrayList scenarios = new ArrayList();

  public boolean isGrmgRunsFlag() {
    return grmgRunsFlag;
  }

  public void setGrmgRunsFlag(boolean grmgRunsFlag) {
    this.grmgRunsFlag = grmgRunsFlag;
  }

  public boolean isRunLogFlag() {
    return runLogFlag;
  }

  public void setRunLogFlag(boolean runLogFlag) {
    this.runLogFlag = runLogFlag;
  }

  public boolean isErrorLogFlag() {
    return errorLogFlag;
  }

  public void setErrorLogFlag(boolean errorLogFlag) {
    this.errorLogFlag = errorLogFlag;
  }

  public ArrayList getScenarios() {
    return scenarios;
  }

  public void setScenarios(ArrayList scenarios) {
    this.scenarios = scenarios;
  }

  public boolean addScenario(GrmgScenario scenario) {
    return scenarios.add(scenario);
  }

}