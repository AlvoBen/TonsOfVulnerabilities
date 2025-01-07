// TODO - this class has to be adapted for using with jdk 1.4
/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.compilation;

/**
 * Represents the internal Java compiler
 *
 * @author Nikolai Neichev
 */
public class InternalJavaCompiler implements Runnable {

  Compiler compiler;
  protected String error = "";
  protected String output = "";
  public int errors = 0;

  public InternalJavaCompiler(Compiler compiler) {
    this.compiler = compiler;
  }

  public void go() {
  }


  public void run() {
  }


}
