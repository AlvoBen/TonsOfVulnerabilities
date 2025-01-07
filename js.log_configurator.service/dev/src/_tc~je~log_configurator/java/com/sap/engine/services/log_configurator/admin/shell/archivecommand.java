/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.services.log_configurator.admin.shell;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.lib.logging.descriptors.LogDestinationDescriptor;
import com.sap.engine.services.log_configurator.admin.Archivator;
import com.sap.engine.services.log_configurator.admin.LogConfigurator;

/**
 * Shell command for archiving log files
 *
 * @author Nikola Marchev
 * @version 7.10
 */

public class ArchiveCommand implements Command {
  
  private static final String LS = System.getProperty("line.separator");

  /**
   * The name for this command in the shell.
   */
  public static final String COMMAND_NAME = "LOG_ARCHIVE";

  /**
   * The group in which this command appears in the shell.
   */
  public static final String COMMAND_GROUP = "LOG";
  
  /**
   * The help message for this command.
   */
  public static final String HELP_MESSAGE = 
    "Creates archive for all log files." + LS + LS
    + "Usage: " + COMMAND_NAME + " [path]" + LS + LS
    + "  [path] - the path to the new archive" + LS
    + "Note: default path is './log/archive/'" + LS
  ;

  /** The interface to log */
  private LogConfigurator logConfigurator;
  private Archivator archivator;
  
  /**
   * @param logConfigurator the interface to log
   */ 
  public ArchiveCommand( LogConfigurator logConfigurator ) {
    this.logConfigurator = logConfigurator;
    this.archivator = new Archivator(logConfigurator);
  }

  /**
   * Gets the name of the command
   *
   * @return The name of the command.
   */
  public String getName() {
    return COMMAND_NAME;
  }

  /**
   * Returns the name of the group the command belongs to
   *
   * @return The name of the group of commands, in which this command belongs.
   */
  public String getGroup() {
    return COMMAND_GROUP;
  }

  /**
   * Gives a short help message about the command
   *
   * @return A help message for this command.
   */
  public String getHelpMessage() {
    return HELP_MESSAGE;
  }

  /**
   * Gives the name of the supported shell providers
   *
   * @return The Shell providers names who supports this command.
   */
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
  /**
   * This method provides the processing of the command's parameters, as the programmer indicates
   * the default input, output and error stream.
   *
   * @param env the surrounding ,in which the command is running
   * @param is the default input stream
   * @param os the default output stream
   * @param params the input parameters of command
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] params) {
    PrintStream out = new PrintStream(os, true);
    PrintStream err = new PrintStream(env.getErrorStream(), true);
    
    String archiveDir = null;
    if(params == null) {
      archiveDir = Archivator.DEFAULT_ARCHIVE_DIR;
    } else {
      if(params.length == 1) {
        archiveDir = params[0].replace('\\', '/');
        if(!archiveDir.endsWith("/")) {
          archiveDir += '/';
        }
      } else {
        err.println(LS + COMMAND_NAME + " command is used with incorrect number of arguments !!!");
        err.println("Use: " + COMMAND_NAME + " [-? | -H] for Help." + LS);
        return;
      }
    }
    
    LogDestinationDescriptor[] destinations = 
      logConfigurator.getCurrentConfiguration().getLogDestinations();
    
    out.println();
    archivator.archive(destinations, true, archiveDir, true, out, err);
    out.println();
  }
}