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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.lib.logging.LogConfigurationUpdater;
import com.sap.engine.lib.logging.StandardConsoleLog;
import com.sap.engine.services.log_configurator.admin.LogConfigurator;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Log;
import com.sap.tc.logging.LogController;

/**
 * This class represents a shell command, which provides
 * means for logging configuration.
 *
 * @author Georgi Manev
 * @version 7.10
 */
public final class LogDumpsCommand implements Command {//$JL-LOG_CONFIG$

  private static final String LS = System.getProperty("line.separator");

  /**
   * The name for this command in the shell.
   */
  public static final String COMMAND_NAME = "LOG_DUMPS";

  /**
   * The group in which this command appears in the shell.
   */
  public static final String COMMAND_GROUP = "LOG";

  /**
   * The help message for this command.
   */
  public static final String HELP_MESSAGE = 
    "Enables or disables additional console output for a specified log controller." + LS + LS
    + "Usage: " + COMMAND_NAME + " [-? | -H]  [OID]  [ON | OFF]" + LS + LS
    + "  -? | -H         Displays this Help Message." + LS
    + "  OID             ID of the log controller that is going to be debugged." + LS
    + "  ON | OFF        Turns the additional console output ON or OFF." + LS + LS
    + "The OID identifier must represent a valid name for the logging service." + LS
    + "To obtain information about the existing log controllers use "
    + LogListCommand.COMMAND_NAME + " command." + LS
    + "Note: if this log controller already has an associated" + LS
    + "log destination of type ConsoleLog some messages may be displayed twice." + LS
    + "Also keep in mind the hierarchy of the log controllers. If neither \"ON\"" + LS
    + "nor \"OFF\" is specified then the current status is shown. If no OID is" + LS
    + "given then a \"global\" configuration is assumed." + LS
  ;

  /**
   * 
   */
  private static final String GLOBAL_NAME = "GLOBAL";

  /**
   * 
   */
  private static final String DEBUG_SUFFIX = "_CONSOLE_DUMPS";

  /**
   * LogConfigurator object, used in the command.
   */
  private LogConfigurator logConfigurator = null;


  /**
   * Constructor of the class.
   *
   * @param   logConfigurator  logging configurator
   */
  public LogDumpsCommand(LogConfigurator logConfigurator) {
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
    PrintStream out = new PrintStream(os, true);
    PrintStream err = new PrintStream(env.getErrorStream(), true);
    
    switch (params.length) {
      case 0 : {
        out.println(LS + "The \"global\" additional console output is currently " + dumpsStatus(null) + "." + LS);
        return;
      }
      case 1 : {
        String argument = params[0].toUpperCase();
        
        if (argument.equals("-?") || argument.equals("-H")) {
          out.println(HELP_MESSAGE);
        } else if ( argument.equals("ON") ) {
          enableDumps(null);
          out.println(LS + "The \"global\" additional console output has been enabled." + LS);
        } else if ( argument.equals("OFF") ) {
          disableDumps(null);
          out.println(LS + "The \"global\" additional console output has been disabled." + LS);
        } else {
          LogController controller = logConfigurator.getLogController(params[0]);
          
          if (controller != null) {
            out.println(LS + "The additional console output for the log controller with name");
            out.println("\"" + params[0] + "\" is currently " + dumpsStatus(controller) + "." + LS);
          } else {
            err.println(LS + "A log controller with name \"" + params[0] + "\" does NOT exist !!!");
            err.println("Use: " + COMMAND_NAME + " -? for Help." + LS);
          }
        }
        return;
      }
      case 2 : {
        if ( logConfigurator.existsLogController(params[0]) ) {
          if ( params[1].equalsIgnoreCase("ON") ) {
            enableDumps(logConfigurator.getLogController(params[0]));
            out.println(LS + "The additional console output for the log controller with name");
            out.println("\"" + params[0] + "\" has been enabled." + LS);
          } else if ( params[1].equalsIgnoreCase("OFF") ) {
            disableDumps(logConfigurator.getLogController(params[0]));
            out.println(LS + "The additional console output for the log controller with name");
            out.println("\"" + params[0] + "\" has been disabled." + LS);
          } else {
            err.println(LS + COMMAND_NAME + " command is used with incorrect argument: " + params[1] + " !!!");
            err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
          }
        } else {
          err.println(LS + "A log controller with name \"" + params[0] + "\" does NOT exist !!!");
          err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
        }
        return;
      }
      default : {
        err.println(LS + COMMAND_NAME + " command is used with incorrect number of arguments !!!");
        err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
      }
    }
  } // </exec(..)>


  /**
   * 
   */
  private static final void enableDumps(LogController controller) {
    String consoleName = null;
    
    if (controller == null) {
      controller = Location.getRoot();
      consoleName = GLOBAL_NAME + DEBUG_SUFFIX;
    } else {
      consoleName = controller.getName() + DEBUG_SUFFIX;
    }
    
    Log[] dests = (Log[]) controller.getLogs().toArray(new Log[0]);
    for (int i = dests.length; --i >= 0; ) {
      if ( dests[i] instanceof StandardConsoleLog ) {
        if ( consoleName.equals(dests[i].getName()) ) {
          return;
        }
      }
    }
    
    Log consoleDumps = new StandardConsoleLog();
    consoleDumps.setName(consoleName);
    controller.addLog(consoleDumps);
    
    /* SHORT DUMP WORKAROUND. */
//    if ( "".equals(controller.getName()) ) {
//      LogConfigurationUpdater.SHORT_DUMP_HEADER_TRACER.removeLog(consoleDumps);
//      LogConfigurationUpdater.SHORT_DUMP_BODY_TRACER.removeLog(consoleDumps);
//    }
    
    // logConfigurator.registerLog(consoleName, consoleDumps);
  } // </enableDumps(..)>

  /**
   * 
   */
  private static final void disableDumps(LogController controller) {
    String consoleName = null;
    
    if (controller == null) {
      controller = Location.getRoot();
      consoleName = GLOBAL_NAME + DEBUG_SUFFIX;
    } else {
      consoleName = controller.getName() + DEBUG_SUFFIX;
    }
    
    Log[] dests = (Log[]) controller.getLogs().toArray(new Log[0]);
    for (int i = dests.length; --i >= 0; ) {
      if ( dests[i] instanceof StandardConsoleLog ) {
        if ( consoleName.equals(dests[i].getName()) ) {
          controller.removeLog(dests[i]);
          // logConfigurator.unregisterLog(consoleName);
          return;
        }
      }
    }
  } // </disableDumps(..)>

  /**
   * 
   */
  private static final String dumpsStatus(LogController controller) {
    String consoleName = null;
    
    if (controller == null) {
      controller = Location.getRoot();
      consoleName = GLOBAL_NAME + DEBUG_SUFFIX;
    } else {
      consoleName = controller.getName() + DEBUG_SUFFIX;
    }
    
    Log[] dests = (Log[]) controller.getLogs().toArray(new Log[0]);
    for (int i = dests.length; --i >= 0; ) {
      if ( dests[i] instanceof StandardConsoleLog ) {
        if ( consoleName.equals(dests[i].getName()) ) {
          return "ON";
        }
      }
    }
    return "OFF";
  } // </dumpsStatus(..)>
}