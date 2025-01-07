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
 * Class implementing MSSystemID shell command for
 * retriving the MessageServer's system id
 *
 * @author Hristo Iliev
 * @version 6.30 Oct 2003
 */
public class MSSystemId implements Command {

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
   * @param  sc   Runtime interface used to acess MessageMonitor
   */
  public MSSystemId(MSPProcessor prcs) {
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

    // Check if proper parameters are passed
    if (params.length == 1) {
      if (params[0].equalsIgnoreCase("-h") || params[0].equalsIgnoreCase("-?")) {
        writer.println(getHelpMessage());
      }
    } else {
      try {
        writer.println(processor.newLineSeparator +
                       "[Shell -> MSSystemID] The MessageServer's SystemID is : " +
                       processor.getSystemId() + processor.newLineSeparator);
      } catch (Exception e) {
        //Excluding the catch block from JLin tests $JL-EXC$
        //Please do not remove this comment!
        errorStream.println("[Shell -> MSSystemID] An exception occured : " + e.getMessage());
      }
    }
  }

  /**
   * Gets the name of the command
   *
   * @return   The name of the command.
   */
  public String getName() {
    return "MSSystemID";
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
    return "  Prints the MessageServer's system ID" + processor.newLineSeparator +
        "    Usage: MSSystemID";
  }
}
