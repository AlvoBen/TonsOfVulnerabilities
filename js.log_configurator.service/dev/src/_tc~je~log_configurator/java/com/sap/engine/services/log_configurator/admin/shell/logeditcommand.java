/*
 * Title:        Logging
 * Description:  Logging API
 * Copyright:    Copyright (c) 2002
 * Company:      SAP Labs Bulgaria LTD., Sofia, Bulgaria.
 * Url:          Http://www.saplabs.bg
 *               All rights reserved.
 *
 *               This software is the confidential and proprietary information
 *               of SAP AG International ("Confidential Information").
 *               You shall not disclose such  Confidential Information
 *               and shall use it only in accordance with the terms of
 *               the license agreement you entered into with SAP AG.
 */
package com.sap.engine.services.log_configurator.admin.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.lib.logging.descriptors.LogConfiguration;
import com.sap.engine.lib.logging.descriptors.LogControllerDescriptor;
import com.sap.engine.services.log_configurator.admin.LogConfigurator;

/**
 * This class represents a shell command, which provides
 * means for logging configuration.
 *
 * @author Georgi Manev
 * @version 7.10
 */
public final class LogEditCommand implements Command {//$JL-EXC$

  private static final String LS = System.getProperty("line.separator");

  /**
   * The name for this command in the shell.
   */
  public static final String COMMAND_NAME = "LOG_EDIT";

  /**
   * The group in which this command appears in the shell.
   */
  public static final String COMMAND_GROUP = "LOG";
  
  /**
   * The help message for this command.
   */
  public static final String HELP_MESSAGE = 
    "Reconfigures registered log controllers in the logging service." + LS + LS
    + "Usage: " + COMMAND_NAME + " [-? | -H] [OID]" + LS + LS
    + "  -? | -H          Displays this help message." + LS
    + "  OID              ID of the object that is going to be edited." + LS + LS
    + "The OID identifier must represent a valid name for the logging service." + LS
    + "To obtain information about the existing log controllers use "
    + LogListCommand.COMMAND_NAME + " command." + LS
    + "To create a new log controller use " + LogCreateCommand.COMMAND_NAME + " command." + LS
    + "Note: only \"named\" log controller can be edited (name is case sensitive) and" + LS
    + "this operation will affect all the users of this log controller." + LS
  ;

  /**
   * LogConfigurator object, used in the command.
   */
  private LogConfigurator logConfigurator = null;


  /**
   * Constructor of the class.
   *
   * @param   logConfigurator  logging configurator
   */
  public LogEditCommand(LogConfigurator logConfigurator) {
    this.logConfigurator = logConfigurator;
  }


  /**
   * Returns the name for this command in the shell.
   *
   * @return  the name for this command in the shell.
   */
  public String getName() {
    return COMMAND_NAME;
  }

  /**
   * Returns the group in which this command appears in the shell.
   *
   * @return  the group in which this command appears in the shell.
   */
  public String getGroup() {
    return COMMAND_GROUP;
  }

  /**
   * Returns the help message for this command.
   *
   * @return  the help message for this command.
   */
  public String getHelpMessage() {
    return HELP_MESSAGE;
  }

  /**
   * 
   *
   * @return  
   */
  public String[] getSupportedShellProviderNames() {
    return null;
  }

  /**
   * Executes the command.
   *
   * @param  env      the surrounding environment,in which the command is executed
   * @param  is       the default input stream for this command
   * @param  os       the default output stream for this command
   * @param  params   the input parameters of the command
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] params) {
    PrintStream err = null;
    PrintStream out = null;
    BufferedReader in = null;
    try {
      err = new PrintStream(env.getErrorStream(), true);
      out = new PrintStream(os, true);
      in = new BufferedReader(new InputStreamReader(is));
      if (params.length == 1) {
        String argument = params[0];
        if (argument.equalsIgnoreCase("-?") || argument.equalsIgnoreCase("-H")) {
          out.println(HELP_MESSAGE);
        } else {
          editLogController(argument, in, out, err);
        }
      } else {
        err.println(LS + COMMAND_NAME + " command is used with incorrect number of arguments !!!");
        err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
      }
    } catch (Exception e) {
      err.println(LS + "The following Exception has occurred:" + LS + e.toString() + LS);
    }
  }

  /**
   * Method for ...
   * 
   * @param in
   * @param out
   * @param controller
   * @param cfg
   * @throws IOException
   */
  private void editLogController(
    String name, BufferedReader in, PrintStream out, PrintStream err)
  throws IOException {
    LogControllerDescriptor controller = logConfigurator.getLogControllerDescriptor(name);
    if (controller == null) {
      err.println(LS + "A log controller with name \"" + name + "\" does NOT exist !!!");
      err.println("Use " + LogCreateCommand.COMMAND_NAME + " command to create it,");
      err.println("or type \"" + COMMAND_NAME + " [-? | -H]\" for help on this command." + LS);
      return;
    }

    out.println(LS + "Possible severities: ");
    out.println(LogCreateCommand.SAP_LOG_SEVERITIES);
    out.print("Type the new severity or press Enter for the defaults: ");
    String severity = in.readLine().trim();
    if ( severity.equals("") ) {
      if ( controller.getName().startsWith("/") ) {
        severity = "INFO";
      } else {
        severity = "ERROR";
      }
    }
    controller.setEffectiveSeverity(severity);
    
    LogConfiguration newCfg = new LogConfiguration();
    newCfg.addLogController(controller);

    out.print("Apply this severity to the whole subtree? (Y/N): ");
    controller.setCopyToSubtree(in.readLine().trim().equalsIgnoreCase("y"));
    
    out.print("Apply this severity for the entire scenario? (Y/N): ");
    boolean entire = in.readLine().trim().equalsIgnoreCase("y");
    logConfigurator.modifyConfiguration(newCfg, null, entire);
    out.println("The operation completed successfully." + LS);
  }
}