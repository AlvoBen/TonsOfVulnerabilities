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
 * Shell command implementing FIND
 *
 * @version 6.30 Oct 2002
 * @author Hristo S. Iliev
 */
public class JNDIFindServ implements Command {

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
      //Parameters check
      String loc = new String("");
      String filter = new String("");
      int scope = SearchControls.SUBTREE_SCOPE;
      int count = 0;

      //Help
      if (s.length == 0) {
        writer.println(getHelpMessage());
        return;
      } else {
        if (s.length == 1) {
          if (s[0].equalsIgnoreCase("-h") || s[0].equalsIgnoreCase("-help") || s[0].equals("-?")) {
            writer.println(getHelpMessage());
            return;
          }
        } else {
          loc = s[0];
          filter = s[1];
          writer.println("[Shell -> FIND] Filter set to : \"" + filter + "\" !");
        }
      }

      //Parse the scope and count
      if ((s.length == 4) || (s.length == 6) || (s.length == 2)) {
        for (int i = 2; i < s.length; i++) {
          //Scope - parse
          if (s[i].equalsIgnoreCase("-s")) {
            String scopeStr = s[i+1];

            if (scopeStr.equalsIgnoreCase("OBJECT")) {
              scope = SearchControls.OBJECT_SCOPE;
              writer.println("[Shell -> FIND] Object scope selected !");
            }

            if (scopeStr.equalsIgnoreCase("ONELEVEL")) {
              scope = SearchControls.ONELEVEL_SCOPE;
              writer.println("[Shell -> FIND] One level scope selected !");
            }

            if (scopeStr.equalsIgnoreCase("SUBTREE")) {
              scope = SearchControls.SUBTREE_SCOPE;
              writer.println("[Shell -> FIND] Sub-tree scope selected !");
            }
          }

          //Count - parse
          if (s[i].equalsIgnoreCase("-c")) {
            try {
              count = Integer.parseInt(s[i+1]);
              if (count != 0) {
                writer.println("[Shell -> FIND] About to return " + count + " objects !");
              } else {
                writer.println("[Shell -> FIND] About to return all objects !");
              }
            } catch (NumberFormatException e) {
              // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
              // Please do not remove this comment!
              count = 0;
              writer.println("[Shell -> FIND] About to return all objects !");
            }
          }
        } 
      } else {
        writer.println(getHelpMessage());
        return;
      }

      //Connect
      if (utl.ctx == null) {
        writer.println("Trying to connect... ");
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
        utl.ctx = new InitialDirContext(p);
        writer.println("Connected.");
      }

      //Check the location
      loc = utl.modifyPath(loc);
      writer.println("[Shell -> FIND] Location : " + loc);

      //Search
      if ((utl.ctx.lookup(loc) instanceof DirContext) || (utl.ctx.lookup(loc) instanceof Context) || (scope == SearchControls.OBJECT_SCOPE)) {
        NamingEnumeration ne = utl.ctx.search(loc, filter, new SearchControls(scope, count, 0, null, true, false));
        writer.println(utl.newLineSeparator + "[Shell -> FIND] Found :");

        while (ne.hasMore()) {
          SearchResult sr = (SearchResult) ne.next();
          writer.println("+ " + sr.getName() + utl.newLineSeparator +"  [Class name] " +
                         sr.getClassName() + utl.newLineSeparator + "  [Attributes] " + sr.getAttributes());
        }
      } else {
        writer.println(utl.newLineSeparator + "[JNDIShell] Not a subcontext !");
      }
    } catch (NamingException eNaming) {
      // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
      // Please do not remove this comment!
      errorStream.println("[Shell -> FIND] NamingException");
      errorStream.println("[Shell -> FIND] Reason : " + eNaming.getExplanation());
    }
  }

  /**
   * Gets the command's name
   *
   * @return Name of the command
   */
  public String getName() {
    return "FIND";
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
    return "  Searches a location for matches with the attributes from ATTR." + utl.newLineSeparator +
           "    Usage: FIND <location> <filter> [-s <scope>] [-c <count>]" + utl.newLineSeparator +
           "    Parameters:" + utl.newLineSeparator +
           "      <location> - the path to the location." + utl.newLineSeparator +
           "      <filter> - Filter to use" + utl.newLineSeparator +
           "      -s <scope> - Scope to search with (OBJECT, ONELEVEL, SUBTREE)" + utl.newLineSeparator +
           "      -c <count> - Number of the returned items.";
  }

}

