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

import java.io.*;
import java.util.*;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.log_configurator.admin.LogConfigurator;

/**
 * This class represents a shell command, which provides
 * means for logging configuration.
 *
 * @author Georgi Manev
 * @version 7.10
 */
public final class LogListCommand implements Command {

  private static final String LS = System.getProperty("line.separator");

  /**
   * The name for this command in the shell.
   */
  public static final String COMMAND_NAME = "LOG_LIST";

  /**
   * The group in which this command appears in the shell.
   */
  public static final String COMMAND_GROUP = "LOG";

  /**
   * The help message for this command.
   */
  public static final String HELP_MESSAGE = 
    "Shows information about the objects in the logging service." + LS
    + "Usage: " + COMMAND_NAME + " [-? | -H]  [-T]  [-C | -CA | -D | -F [OID | -T]]" + LS
    + "  -? | -H  Displays this Help Message." + LS
    + "  -C       Lists only configured log controllers" + LS
    + "  -CA      Lists all runtime log controllers" + LS
    + "  -D       Lists log destinations" + LS
    + "  -F       Lists log formatters" + LS
    + "  -T       Specifies that a thorough info should be provided." + LS
    + "  OID      ID of the object that is going to be inspected." + LS + LS
    + "The OID identifier must represent a valid name for the logging service." + LS
    + "Note that only \"named\" objects can be displayed (name is case sensitive)." + LS
    + "If the command is used without arguments it provides a complete listing" + LS
    + "of all the registered object names sorted by their corresponding types." + LS
    + "If it is executed with a type specifier but with NO object identifier" + LS
    + "then this command lists the name of each and every object of the respective" + LS
    + "type that is configured with the logging service. The -T switch can be used" + LS
    + "as a single argument or after a preceding type specifier argument " + LS
    + "(i.e. -C, -CA, -D, or -F). In this case the command behaves as described " + LS
    + "above (i.e. as if NO -T was present) with the difference that a detailed " + LS
    + "information will be provided for the appropriate objects instead of simple " + LS
    + "listing of their names. The object identifier (i.e. the name of an object " + LS
    + "as printed by this command) is intended for displaying of thorough " + LS
    + "information about the settings of a particular object only. It must be used " + LS
    + "with a preceding type specifier but WITHOUT the -T switch. The -T switch " + LS
    + "might be used only when NO object ID is given." + LS
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
  public LogListCommand(LogConfigurator logConfigurator) {
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
        synchronized (logConfigurator) {
          out.println();
          out.println( "-----------------------------------------------------------------------------" );
          out.println( "             Logging Information on " + new Date().toString() );
          out.println( "-----------------------------------------------------------------------------" );
          logConfigurator.listLogControllersNames(out);
          out.println( "  * to see all runtime log controllers use 'LOG_LIST -CA'" );
          logConfigurator.listLogsNames(out);
          logConfigurator.listFormattersNames(out);
          out.println( "-----------------------------------------------------------------------------" );
          out.println();
        }
        return;
      }
      case 1 : {
        String argument = params[0].toUpperCase();
        if (argument.equals("-?") || argument.equals("-H")) {
          out.println(HELP_MESSAGE);
        } else if ( argument.equals("-C") ) {
          out.println();
          out.println( "-----------------------------------------------------------------------------" );
          out.println( "             Logging Information on " + new Date().toString() );
          out.println( "-----------------------------------------------------------------------------" );
          logConfigurator.listLogControllersNames(out);
          out.println( "  * to see all runtime log controllers use 'LOG_LIST -CA'" );
          out.println( "-----------------------------------------------------------------------------" );
          out.println();
        } else if ( argument.equals("-CA") ) {
          out.println();
          out.println( "-----------------------------------------------------------------------------" );
          out.println( "             Logging Information on " + new Date().toString() );
          out.println( "-----------------------------------------------------------------------------" );
          logConfigurator.listLogControllersNamesAll(out);
          out.println( "-----------------------------------------------------------------------------" );
          out.println();
        } else if ( argument.equals("-D") ) {
          out.println();
          out.println( "-----------------------------------------------------------------------------" );
          out.println( "             Logging Information on " + new Date().toString() );
          out.println( "-----------------------------------------------------------------------------" );
          logConfigurator.listLogsNames(out);
          out.println( "-----------------------------------------------------------------------------" );
          out.println();
        } else if ( argument.equals("-F") ) {
          out.println();
          out.println( "-----------------------------------------------------------------------------" );
          out.println( "             Logging Information on " + new Date().toString() );
          out.println( "-----------------------------------------------------------------------------" );
          logConfigurator.listFormattersNames(out);
          out.println( "-----------------------------------------------------------------------------" );
          out.println();
        } else if ( argument.equals("-T") ) {
          synchronized (logConfigurator) {
            out.println();
            out.println( "-----------------------------------------------------------------------------" );
            out.println( "             Logging Information on " + new Date().toString() );
            out.println( "-----------------------------------------------------------------------------" );
            logConfigurator.listLogControllers(null, out);
            logConfigurator.listLogs(null, out);
            logConfigurator.listFormatters(null, out);
            out.println( "-----------------------------------------------------------------------------" );
            out.println();
          }
        } else {
          err.println();
          err.println(COMMAND_NAME + " command is used with incorrect argument: " + params[0] + " !!!");
          err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help.");
          err.println();
        }
        return;
      }
      case 2 : {
        String argument = params[0].toUpperCase();
        if ( argument.equals("-C") ) {
          if ( params[1].equalsIgnoreCase("-T") ) {
            out.println();
            out.println( "-----------------------------------------------------------------------------" );
            out.println( "             Logging Information on " + new Date().toString() );
            out.println( "-----------------------------------------------------------------------------" );
            logConfigurator.listLogControllers(null, out);
            out.println( "  * to see all runtime log controllers use 'LOG_LIST -CA -T'" );
            out.println( "-----------------------------------------------------------------------------" );
            out.println();
          } else if ( logConfigurator.existsLogController(params[1]) ) {
            out.println();
            out.println( "-----------------------------------------------------------------------------" );
            out.println( "             Logging Information on " + new Date().toString() );
            out.println( "-----------------------------------------------------------------------------" );
            logConfigurator.listLogControllers(new String[] { params[1] }, out);
            out.println( "-----------------------------------------------------------------------------" );
            out.println();
          } else {
            err.println(LS + "A log controller with name \"" + params[1] + "\" does NOT exist !!!");
            err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
          }
        } else if ( argument.equals("-CA") ) {
          if ( params[1].equalsIgnoreCase("-T") ) {
            out.println();
            out.println( "-----------------------------------------------------------------------------" );
            out.println( "             Logging Information on " + new Date().toString() );
            out.println( "-----------------------------------------------------------------------------" );
            logConfigurator.listLogControllersAll(null, out);
            out.println( "-----------------------------------------------------------------------------" );
            out.println();
          } else if ( logConfigurator.existsLogControllerAll(params[1]) ) {
            out.println();
            out.println( "-----------------------------------------------------------------------------" );
            out.println( "             Logging Information on " + new Date().toString() );
            out.println( "-----------------------------------------------------------------------------" );
            logConfigurator.listLogControllersAll(new String[] { params[1] }, out);
            out.println( "-----------------------------------------------------------------------------" );
            out.println();
          } else {
            err.println(LS + "A log controller with name \"" + params[1] + "\" does NOT exist !!!");
            err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
          }
        } else if ( argument.equals("-D") ) {
          if ( params[1].equalsIgnoreCase("-T") ) {
            out.println();
            out.println( "-----------------------------------------------------------------------------" );
            out.println( "             Logging Information on " + new Date().toString() );
            out.println( "-----------------------------------------------------------------------------" );
            logConfigurator.listLogs(null, out);
            out.println( "-----------------------------------------------------------------------------" );
            out.println();
          } else if ( logConfigurator.existsLog(params[1]) ) {
            out.println();
            out.println( "-----------------------------------------------------------------------------" );
            out.println( "             Logging Information on " + new Date().toString() );
            out.println( "-----------------------------------------------------------------------------" );
            logConfigurator.listLogs(new String[] { params[1] }, out);
            out.println( "-----------------------------------------------------------------------------" );
            out.println();
          } else {
            err.println(LS + "A log destination with name \"" + params[1] + "\" does NOT exist !!!");
            err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
          }
        } else if ( argument.equals("-F") ) {
          if ( params[1].equalsIgnoreCase("-T") ) {
            out.println();
            out.println( "-----------------------------------------------------------------------------" );
            out.println( "             Logging Information on " + new Date().toString() );
            out.println( "-----------------------------------------------------------------------------" );
            logConfigurator.listFormatters(null, out);
            out.println( "-----------------------------------------------------------------------------" );
            out.println();
          } else if ( logConfigurator.existsFormatter(params[1]) ) {
            out.println();
            out.println( "-----------------------------------------------------------------------------" );
            out.println( "             Logging Information on " + new Date().toString() );
            out.println( "-----------------------------------------------------------------------------" );
            logConfigurator.listFormatters(new String[] { params[1] }, out);
            out.println( "-----------------------------------------------------------------------------" );
            out.println();
          } else {
            err.println(LS + "A log formatter with name \"" + params[1] + "\" does NOT exist !!!");
            err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
          }
        } else {
          err.println(LS + COMMAND_NAME + " command is used with incorrect argument: " + params[0] + " !!!");
          err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
        }
        return;
      }
      default : {
        err.println(LS + COMMAND_NAME + " command is used with incorrect number of arguments !!!");
        err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
      }
    } // </switch(..)>
  } // </exec(..)>
}
