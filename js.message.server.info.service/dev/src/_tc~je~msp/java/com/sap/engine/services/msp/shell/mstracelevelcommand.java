/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.msp.shell;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.msp.MSPProcessor;

/**
 * Class implementing MSInfo shell command for
 * retriving the MessageServer's information
 *
 * @author Hristo Iliev
 * @version 6.30 Oct 2003
 */
public class MSTraceLevelCommand implements Command {

  /**
   * PrintStream used for output
   */
  private PrintStream writer;
  /**
   * PrintStream used for errors
   */
  private PrintStream errorStream;

  /**
   * Used for line separator display and interaction with MSMonitor
   */
  private static MSPProcessor processor = null;

  /**
   * Constructor
   *
   * @param  sc   Service Context used to acess MessageMonitor
   */
  public MSTraceLevelCommand(MSPProcessor prcs) {
    this.processor = prcs;
  }

  /**
   * A method that executes the command .
   *
   * @param   environment  An implementation of Environment.
   * @param   input  The InputStream , used by the command.
   * @param   output  The OutputStream , used by the command.
   * @param   params  Parameters of the command.
   *
   */
  public void exec(Environment environment, InputStream input, OutputStream output, String[] params) {
    errorStream = new PrintStream(environment.getErrorStream());
    writer = new PrintStream(output);

    try {
      // Check if display statistics was requested
      if (params.length == 1) {
        // Check if help was requested
        if (params[0].equalsIgnoreCase("-?") || params[0].equalsIgnoreCase("-?")) {
          writer.println(processor.newLineSeparator + getHelpMessage());
        } else {
          // Activate statistics requested
          if (params[0].equalsIgnoreCase("-i")) {
            writer.print(processor.newLineSeparator +
                         "[Shell -> MSTraceLevel] Increasing MessageServer's trace level... ");
            processor.incrementTraceLevel();
            writer.println("done !");
          } else {
            // Deactivate statistic requested
            if (params[0].equalsIgnoreCase("-d")) {
              writer.print(processor.newLineSeparator +
                           "[Shell -> MSTraceLevel] Decreasing MessageServer's trace level... ");
              processor.decrementTraceLevel();
              writer.println("done !");
            } else {
              // Reset statistic was requested
              if (params[0].equalsIgnoreCase("-r")) {
                writer.print(processor.newLineSeparator +
                             "[Shell -> MSTraceLevel] Reseting MessageServer's trace level... ");
                processor.resetTraceLevel();
                writer.println("done !");
              } else {
                writer.println(processor.newLineSeparator + getHelpMessage());
              }
            }
          }
        }
      } else {
        writer.println(processor.newLineSeparator + getHelpMessage());
      }
    } catch (Exception e) {
      //Excluding the catch block from JLin tests $JL-EXC$
      //Please do not remove this comment!
      errorStream.println("[Shell -> MSTraceLevel] An exception occured : " + e.getMessage());
    }
  }

  /**
   * Gets the name of the command
   *
   * @return   The name of the command.
   */
  public String getName() {
    return "MSTraceLevel";
  }

  /**
   * Returns the name of the group the command belongs to
   *
   * @return   The name of the group of commands, in which this command belongs.
   */
  public String getGroup() {
    return "MSP";
  }

  /**
   * Gives the name of the supported shell providers
   *
   * @return   The Shell providers' names who supports this command.
   */
  public String[] getSupportedShellProviderNames() {
    return new String[]{"InQMyShell"};
  }

  /**
   * Gives a short help message about the command
   *
   * @return   A help message for this command.
   */
  public String getHelpMessage() {
    return "  Manages the MessageServer's trace level" + processor.newLineSeparator +
        "    Usage: MSTraceLevel [<-i|-d|-r>]" + processor.newLineSeparator +
        "      Parameters:" + processor.newLineSeparator +
        "        -i - Increments the trace level." + processor.newLineSeparator +
        "        -r - Resets the trace level." + processor.newLineSeparator +
        "        -d - Decrements the trace level.";
  }
}
