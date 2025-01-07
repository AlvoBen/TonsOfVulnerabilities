/*
 * Created on 11.04.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.locking.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.locking.LockingApplicationFrame;
import com.sap.exception.standard.SAPIllegalArgumentException;

/**
 * @author d039760
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DisableTimeStatisticsCommand extends AbstractCommand {

  private static final String NAME = "disable_locking_stat";

  private static final String DESCRIPTION = "Disables the time statistics on current node";

  private static final String USAGE =
    "Usage: "
      + NAME
      + "\n";


  // =============== implementation of default abstract methods ================

  public DisableTimeStatisticsCommand(LockingApplicationFrame lockingApplicationFrame) {
    super(lockingApplicationFrame);
  }
  public final String getName() {
    return NAME;
  }
  public final String getUsage() {
    return USAGE;
  }
  public final String getDescription() {
    return DESCRIPTION;
  }


  // =================== implementation of the exec method =====================

  public void exec(Environment env, InputStream is, OutputStream os, String[] params)
 {
   PrintStream out = new PrintStream(os);

   // parse parameters
   OptionParser.Option options[] = new OptionParser.Option[] { };

   try
   {
     String remaining[] = OptionParser.parse(params, options);
     if (remaining.length > 0)
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
     getLockingRuntime().disableTimeStatistics();
     out.println("... done");
   }
   catch (Exception e)
   { 
     displayException(out, e); 
   }
 }
}

