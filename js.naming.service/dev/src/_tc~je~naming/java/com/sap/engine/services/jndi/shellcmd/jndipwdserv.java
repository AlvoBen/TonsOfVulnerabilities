/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.shellcmd;

import java.io.*;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;

/**
 * Shell command implementing PWD
 *
 * @version 6.30 Oct 2002
 * @author Hristo S. Iliev
 */
public class JNDIPwdServ implements Command {

  /**
   * PrintStream used for output
   */
  private PrintStream writer;
  /**
   * User/password/URL managment
   */
  private static ServCLUtils utl = new ServCLUtils();

  /**
   * A method that executes the command .
   *
   * @param   env  An implementation of Environment.
   * @param   is  The InputStream , used by the command.
   * @param   os  The OutputStream , used by the command.
   * @param   s  Parameters of the command.
   *
   */
  public void exec(Environment env, InputStream is, OutputStream os, String s[]) {
    writer = new PrintStream(os);

    if (s.length > 0) {
      writer.println(getHelpMessage());
    } else {
      writer.println();
      writer.println(utl.relativePath);
      writer.println();
    }
  }

  /**
   * Gets the command's name
   *
   * @return Name of the command
   */
  public String getName() {
    return "PWD";
  }

  /**
   * Gets the command's group
   *
   * @return Group name of the command
   */
  public String getGroup() {
    return "NAMING";
  }

  /**
   * Gets the supported shell provider names
   *
   * @return Shell provider names
   */
  public String[] getSupportedShellProviderNames() {
    return new String[] {"InQMyShell"};
  }

  /**
   * Gets the command's help message
   *
   * @return Help message
   */
  public String getHelpMessage() {
    return "  Displays the name of the current context." + utl.newLineSeparator +
           "    Usage: PWD";
  }

}

