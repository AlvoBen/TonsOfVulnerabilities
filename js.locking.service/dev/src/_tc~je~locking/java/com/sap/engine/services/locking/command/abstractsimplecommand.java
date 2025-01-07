/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/command/AbstractSimpleCommand.java#11 $ SAP*/
package com.sap.engine.services.locking.command;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sap.engine.frame.core.locking.Util;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.locking.LockingApplicationFrame;
import com.sap.exception.standard.SAPIllegalArgumentException;


/**
 * Abstract command, which has a fixed number of input-parameters and does
 * not return a value.
 */
public abstract class AbstractSimpleCommand extends AbstractCommand
{    
  private static final Util UTIL = new Util();
  
  public AbstractSimpleCommand(LockingApplicationFrame lockingApplicationFrame)
  {
    super(lockingApplicationFrame);
  }


  /** Returns a boolean[] with the parameters, which will be used by exec() */
  public abstract boolean[] getExecParameters();
  /** Receives the parsed parameters to call the business-logic */
  public abstract void exec(String owner, String name, String argument, char mode) throws Exception;
  

  /**
   * A method that executes the command .
   *
   * @param   environment  An implementation of Environment.
   * @param   is  The InputStream , used by the command.
   * @param   os  The OutputStream , used by the command.
   * @param   params  Parameters of the command.
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] params)
  {
    PrintStream out = new PrintStream(os);

    // parse parameters
    String owner = null;
    String name = null;
    String argument = null;
    char mode = (char) 0;    
    try
    {
      boolean p[] = getExecParameters();
      int pos = 0;
      if (p[0]) owner = params[pos++];
      if (p[1]) name = params[pos++];
      if (p[2]) argument = params[pos++];
      if (p[3]) mode = UTIL.getMode(params[pos++]);
      if (params.length > pos)
        throw new SAPIllegalArgumentException(SAPIllegalArgumentException.PARAMETER_TOO_MANY);
    }
    catch (Exception e)
    {
      out.println("Syntax error in command " + getName());
      out.println(getUsage());
      return;
    }
    
    // exec command
    try
    {
      exec(owner, name, argument, mode);
      out.println("... done");
    }
    catch (Exception e)
    {
      displayException(out, e); 
    }
  }
}
