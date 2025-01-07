/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/command/RunTestsCommand.java#5 $ SAP*/
package com.sap.engine.services.locking.command;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.locking.LockingApplicationFrame;
import com.sap.engine.services.locking.test.AllTests;
import com.sap.engine.services.locking.test.ILockingContextTest;
import com.sap.engine.services.locking.test.TestResult;
import com.sap.exception.standard.SAPIllegalArgumentException;


public class RunTestsCommand extends AbstractCommand
{
  private static final String NAME = "run_locking_tests";
  
  private static final String DESCRIPTION = "Runs several tests to check the locking.";
  
  private static final String USAGE = "Usage: " + NAME + " [options]\n" +
                                      "\n" +
                                      "where options include:\n" +
                                      "    -d  -detail          Display detailed results\n" +
                                      "    -nf -nofunctional    Do not run the functional tests\n" +
                                      "    -nl -noload          Do not run the load tests\n" +
                                      "\n";  
                                      
                                        
  // =============== implementation of default abstract methods ================
  
  
  public RunTestsCommand(LockingApplicationFrame lockingApplicationFrame) { super(lockingApplicationFrame); }
  public final String getName() { return NAME; } 
  public final String getUsage() { return USAGE; } 
  public final String getDescription() { return DESCRIPTION; }


  // =================== implementation of the exec method =====================


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
    OptionParser.FlagOption detail = new OptionParser.FlagOption(new String[] { "d", "detail" });
    OptionParser.FlagOption nofunctional = new OptionParser.FlagOption(new String[] { "nf", "nofunctional" });
    OptionParser.FlagOption noload = new OptionParser.FlagOption(new String[] { "nl", "noload" });
    OptionParser.Option options[] = new OptionParser.Option[] { detail, nofunctional, noload };

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
      String testResults = getTestResults(detail.getValue(), nofunctional.getValue(), noload.getValue());
      out.println(testResults);
    }
    catch (Exception e)
    { 
      displayException(out, e); 
    }
  }
  
  private String getTestResults(boolean detail, boolean nofunctional, boolean noload) throws Exception
  {
    TestResult result[] = runLockingContextTests(nofunctional, noload);
    StringBuffer sb = new StringBuffer(1024);
    
    int successfulTests = 0;
    for (int i = 0; i < result.length; i++)
    {
      if (result[i].getException() == null)
        successfulTests ++;
      if (detail)
        sb.append(result[i].getLog());
    }
    
    if (successfulTests == result.length)
      sb.append("SUCCESS: All ").append(result.length).append(" tests finished successfully");
    else
      sb.append("ERROR: Only ").append(successfulTests).append(" of ").append(result.length).append(" tests finished successfully");

    return sb.toString();    
  }
  
  private TestResult[] runLockingContextTests(boolean nofunctional, boolean noload) throws Exception
  {
    ILockingContextTest test[];
    if (nofunctional && noload)
      return new TestResult[0];
    else if (nofunctional && (! noload))
      test = AllTests.getLoadLockingContextTests();
    else if ((! nofunctional) && noload)
      test = AllTests.getFunctionalLockingContextTests();
    else
      test = AllTests.getAllLockingContextTests();
      
    AdministrativeLocking administrativeLocking = getLockingRuntime().getAdministrativeLocking();
    ThreadSystem threadSystem = getLockingFrame().getThreadSystem();
    TestResult result[] = new TestResult[test.length];
    for (int i = 0; i < test.length; i++)
      result[i] = test[i].start(threadSystem, administrativeLocking, null);
    return result;
  }
}