/*
 * Copyright (c) 2000 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.services.jndi.shellcmd;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;

import com.sap.engine.services.jndi.PermissionAdministrator;
import com.sap.engine.services.jndi.AccessListObject;

/**
 * @author Nikolay Dimitrov
 * @version 6.30
 */
public class JNDILsecServ implements com.sap.engine.interfaces.shell.Command {

  /**
   * PrintStream used for output
   */
  private PrintStream writer;
  /**
   * PrintStream used for errors
   */
  private PrintStream errorStream;
//  private static final String[] permsNames = {"ChnSec:", "LookUp:", "ModAtr:", "Bind  :", "CreCtx:", "GetAtr:", "Unbind:", "DstCtx:", "LstPrm:", "LstBnd:", "Rename:", "Envmnt:", "Stndrd:"};
  private static final String[] permsNames = {"AllOps:    ", "GetInitCtx:"};

  /**
   * Constructor
   */
  public JNDILsecServ() {
  }

  /**
   * This method provides the processing of the command's parameters , as the programmer indicates
   * the default input , output and error stream.
   * @param   env     the surrounding ,in which the command is running
   * @param   is      the default input stream
   * @param   os      the default output stream
   * @param   s     the input parameters of command
   */
  public void exec(com.sap.engine.interfaces.shell.Environment env, InputStream is, OutputStream os, String s[]) {
    errorStream = new PrintStream(env.getErrorStream());
    writer = new PrintStream(os);
    if ((s.length > 0) && (s[0].equalsIgnoreCase("-h") || s[0].equalsIgnoreCase("-help") || s[0].equals("-?"))) {
      writer.println(getHelpMessage());
    } else {
      try {
        //List Security                 
        writer.println("[Shell -> LSEC] Getting permissions...");
        writer.println();
        printSecurity();
        writer.println();
      } catch (RemoteException eRemote) {
        // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
        // Please do not remove this comment!
        errorStream.println("[Shell -> LSEC] RemoteException: " + eRemote.toString());
      }
    }
  }

  private void printSecurity() throws RemoteException {
    AccessListObject[][] users = (AccessListObject[][])(new PermissionAdministrator()).getPermissions();
    for(int i = 0; i < users.length; i++) {      
      for(int j = 0; j < 2; j++) {
        writer.print(" ");
      }
      writer.print(permsNames[i]);
      writer.print(" [");
      for(int j = 0; j < users[i].length; j++) {
        if (j > 0) {
          writer.print(", ");
        }
        writer.print(users[i][j].getName() + ((users[i][j].isUser()) ? "" : "(group)"));
      }
      writer.println("] ");
    }
  }

  /**
   * Gets the command's name
   *
   * @return Name of the command
   */
  public String getName() {
    return "LSEC";
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
    return new String[]{"InQMyShell"};
  }

  /**
   * Gets the command's help message
   *
   * @return Help message
   */
  public String getHelpMessage() {
    return "  Lists the security permissions for all contexts.\r\n" + "    Usage: LSEC ";
  }
}
   

