/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.shellcmd;

import java.io.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;

/**
 * Shell command implementing MATTR
 *
 * @version 6.30 Oct 2002
 * @author Hristo S. Iliev
 */
public class JNDIMEnvServ implements Command {

  /**
   * PrintStream used for output
   */
  private PrintStream writer;
  /**
   * PrintStream used for errors
   */
  private PrintStream errorStream;
  /**
   * User/password/URL managment
   */
  private static ServCLUtils utl = new ServCLUtils();

  /**
   * This method provides the processing of the command's parameters , as the programmer indicates
   * the default input , output and error stream.
   * @param   env  the surrounding ,in which the command is running
   * @param   is  the default input stream
   * @param   os  the default output stream
   * @param   s  the input parameters of command
   */
  public void exec(Environment env, InputStream is, OutputStream os, String s[]) {
    errorStream = new PrintStream(env.getErrorStream());
    writer = new PrintStream(os);
    try {
      //Connect
      if (utl.ctx == null) {
        writer.println("Trying to connect... ");
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
        utl.ctx = new InitialDirContext(p);
        writer.println("Connected.");
      }

      if (s.length == 3) {
        if (s[0].equalsIgnoreCase("-a")) {
          utl.ctx.addToEnvironment(s[1], new String(s[2]));
          writer.println("[Shell -> MENV] Added to environment \"" + s[1] + "\" with value \"" + s[2] + "\".");
        } else {
          errorStream.println("[Shell -> MENV] Parameters not in the expected format !" + utl.newLineSeparator);
          writer.println(getHelpMessage());
        }
      } else {
        if (s.length == 2) {
          if (s[0].equalsIgnoreCase("-r")) {
            utl.ctx.removeFromEnvironment(s[1]);
            writer.println("[Shell - MENV] Removed from environment \"" + s[1] + "\" !");
          } else {
            if (s[0].equalsIgnoreCase("-a")) {
              utl.ctx.addToEnvironment(s[1], new String());
              writer.println("[Shell -> MENV] Added to environment \"" + s[1] + "\" as empty string !");
            } else {
              errorStream.println("[Shell -> MENV] Parameters not in the expected format !" + utl.newLineSeparator);
              writer.println(getHelpMessage());
            }
          }
        } else {
          if (s.length == 1) {
            if (s[0].equalsIgnoreCase("-h") || s[0].equalsIgnoreCase("-help") || s[0].equals("-?")) {
              writer.println(getHelpMessage());
            } else {
              errorStream.println("[Shell -> MENV] Parameters not in the expected format !" + utl.newLineSeparator);
              writer.println(getHelpMessage());
            }
          } else {
            Hashtable e = utl.ctx.getEnvironment();

            if (e.size() != 0) {
              writer.println("[Shell -> MENV] Prining the environment :" + utl.newLineSeparator);
              Enumeration elEnum = e.elements();
              Enumeration kEnum = e.keys();

              while (kEnum.hasMoreElements()) {
                writer.println(kEnum.nextElement() + "=" + elEnum.nextElement());
              }

              writer.println();
            } else {
              writer.println("[Shell -> MENV] The environment is empty !");
            }
          }
        }
      }
    } catch (NamingException eNaming) {
      // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
      // Please do not remove this comment!
      errorStream.println("[Shell -> MENV] NamingException");
      errorStream.println("[Shell -> MENV] Reason : " + eNaming.getExplanation());
    }
  }

  /**
   * Gets the command's name
   *
   * @return Name of the command
   */
  public String getName() {
    return "MENV";
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
    return new String[] {"InQMyShell"};
  }

  /**
   * Gets the command's help message
   *
   * @return Help message
   */
  public String getHelpMessage() {
    return "  Modifies or prints the JNDI's environment." + utl.newLineSeparator +
           "    Usage: MENV [<-a|-r> <entry> [text]]" + utl.newLineSeparator +
           "      Parameters:" + utl.newLineSeparator +
           "        -a - Adds the entry." + utl.newLineSeparator +
           "        -r - Removes the entry." + utl.newLineSeparator +
           "        <entry> - Name of the item" + utl.newLineSeparator +
           "        <text> - Value of the entry." + utl.newLineSeparator +
           "    If no parameters specified prints the environment.";
  }

}

