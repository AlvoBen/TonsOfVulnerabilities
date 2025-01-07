/*
* Copyright (c) 2002 by SAP AG, Walldorf.,
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
 * Shell command implementing CD
 *
 * @version 6.30 Oct 2002
 * @author Hristo S. Iliev
 */
public class JNDIDelTreeServ implements Command {

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
   * A method that executes the command .
   *
   * @param   env  An implementation of Environment.
   * @param   is  The InputStream , used by the command.
   * @param   os  The OutputStream , used by the command.
   * @param   s  Parameters of the command.
   *
   */
  public void exec(Environment env, InputStream is, OutputStream os, String s[]) {
    errorStream = new PrintStream(env.getErrorStream());
    writer = new PrintStream(os);

    if (s.length == 0 || s[0].equalsIgnoreCase("-h") || s[0].equalsIgnoreCase("-help") || s[0].equals("-?")) {
      writer.println(getHelpMessage());
      return;
    } else {
      if (s.length == 1) {
        try {
          //Parameters check
          String loc = s[0];

          //Check the location
          loc = utl.modifyPath(loc);
          writer.println("[Shell -> DELTREE] Location : " + loc);

          //Connect
          if (utl.ctx == null) {
            writer.println("Trying to connect... ");
            Properties p = new Properties();
            p.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
            utl.ctx = new InitialDirContext(p);
            writer.println("Connected.");
          }

          //Check the location
          if (loc.equals("/")) {
            errorStream.println("[Shell -> DELTREE] NamingException");
            errorStream.println("[Shell -> DELTREE] Reason : Can not delete the root context");
            return;
          }
          Object obj = utl.ctx.lookup(loc);
          if (obj != null) {
            if ((obj instanceof Context) || (obj instanceof DirContext)) {
              deleteSubTree(loc);
              utl.ctx.destroySubcontext(loc);
              writer.println("[Shell -> DELTREE] Deleted : " + loc);
              writer.println();
            }
          } else {
            writer.println("[Shell -> DELTREE] The location specified is not a context : " + loc);
            writer.println();
          }
        } catch (NamingException eNaming) {
          // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
          // Please do not remove this comment!
          errorStream.println("[Shell -> DELTREE] NamingException");
          errorStream.println("[Shell -> DELTREE] Reason : " + eNaming.getExplanation());
        }
      } else {
        writer.println(getHelpMessage());
        return;
      }
    }
  }

  /**
   * Deletes the tree under the specified location including all contexts and objects
   *
   * @param  location   Location to delete
   */
  private void deleteSubTree(String location) throws NamingException {
    NamingEnumeration enumeration = utl.ctx.list(location);
    while (enumeration.hasMore()) {
      NameClassPair item = (NameClassPair) enumeration.next();

      if (utl.isContext(item.getClassName()) == true) {
        deleteSubTree(location + "/" + item.getName());
        utl.ctx.destroySubcontext(location + "/" + item.getName());
      } else {
        utl.ctx.unbind(location + "/" + item.getName());
      }
    }
  }

  /**
   * Gets the command's name
   *
   * @return Name of the command
   */
  public String getName() {
    return "DELTREE";
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
    return "  Deletes all entities under the specified context." + utl.newLineSeparator +
           "    Usage: DELTREE <location>" + utl.newLineSeparator +
           "    Parameters:" + utl.newLineSeparator +
           "      <location> - the path to the location.";
  }

}

