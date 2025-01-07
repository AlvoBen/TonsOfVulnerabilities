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
public class JNDIMAttrServ implements Command {

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

    if (s.length == 0 || s[0].equalsIgnoreCase("-h") || s[0].equalsIgnoreCase("-help") || s[0].equals("-?")) {
      writer.println(getHelpMessage());
      return;
    } else {
      try {
        //Parameters check
        String loc = new String("");
        int mode = DirContext.ADD_ATTRIBUTE;

        for (int i = 0; i < s.length; i++) {
          //location check
          if (s[i].equalsIgnoreCase("-a")) {
            mode = DirContext.ADD_ATTRIBUTE;
          } else {
            if (s[i].equalsIgnoreCase("-rm")) {
              mode = DirContext.REMOVE_ATTRIBUTE;
            } else {
              if (s[i].equalsIgnoreCase("-rp")) {
                mode = DirContext.REPLACE_ATTRIBUTE;
              } else {
                loc = s[i];
                if (s.length > i+1) {
                  writer.println(getHelpMessage());
                  return;
                }
              }
            }
          }
        } 

        //Check the location
        loc = utl.modifyPath(loc);
        writer.println("[Shell -> MATTR] Location : " + loc);

        //Connect
        if (utl.ctx == null) {
          writer.println("Trying to connect... ");
          Properties p = new Properties();
          p.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
          utl.ctx = new InitialDirContext(p);
          writer.println("Connected.");
        }

        //Lookup
        utl.ctx.modifyAttributes(loc, mode, utl.attributes);
        writer.println("[Shell -> MATTR] Modified : " + loc);
        writer.println();
      } catch (NamingException eNaming) {
        // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
        // Please do not remove this comment!
        errorStream.println("[Shell -> MATTR] NamingException");
        errorStream.println("[Shell -> MATTR] Reason : " + eNaming.getExplanation());
      }
    }
  }

  /**
   * Gets the command's name
   *
   * @return Name of the command
   */
  public String getName() {
    return "MATTR";
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
    return "  Modifies attributes using the attributes from ATTR." + utl.newLineSeparator +
           "    Usage: MATTR <-a|-rp|-rm> <location>" + utl.newLineSeparator +
           "      Parameters:" + utl.newLineSeparator +
           "        <-a>  - Adds the attributes. Default." + utl.newLineSeparator +
           "        <-rp> - Replaces the attributes." + utl.newLineSeparator +
           "        <-rm> - Removes the attributes." + utl.newLineSeparator +
           "        <location> - the name of the item";
  }

}

