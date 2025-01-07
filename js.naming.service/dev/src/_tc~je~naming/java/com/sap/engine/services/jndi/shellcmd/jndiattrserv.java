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
import javax.naming.*;
import javax.naming.directory.*;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;

/**
 * Shell command implementing ATTR
 *
 * @version 6.30 Oct 2002
 * @author Hristo S. Iliev
 */
public class JNDIAttrServ implements Command {

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
   * @param   input  The InputStream , used by the command.
   * @param   os  The OutputStream , used by the command.
   * @param   s  Parameters of the command.
   *
   */
  public void exec(Environment env, InputStream input, OutputStream os, String[] s) {
    errorStream = new PrintStream(env.getErrorStream());
    writer = new PrintStream(os);

    //Parameters check
    if (s.length == 3) {
      //Add or create an attribute
      if (s[0].equalsIgnoreCase("-a")) {
        if (utl.attributes.get(s[1]) == null) {
          utl.attributes.put(s[1], s[2]);
          writer.println("[Shell -> ATTR] Attribute \"" + s[1] + "\" is set to \"" + s[2] + "\".");
        } else {
          utl.attributes.get(s[1]).add(s[2]);
          writer.println("[Shell -> ATTR] Added value : \"" + s[2] + "\" to attribute \"" + s[1] + "\".");
        }
      } else {
        //Remove one value of the attribute
        if (s[0].equalsIgnoreCase("-r")) {
          utl.attributes.get(s[1]).remove(s[2]);
          writer.println("[Shell -> ATTR] Removed value : \"" + s[2] + "\" from attribute \"" + s[1] + "\".");
        } else {
          errorStream.println("[Shell -> ATTR] Parameters not in the expected format !" + utl.newLineSeparator);
        }
      }
    } else {
      if (s.length == 2) {
        //Sets or removes all values for the attribute
        if (s[0].equalsIgnoreCase("-a")) {
          if (utl.attributes.get(s[1]) == null) {
            utl.attributes.put(s[1], null);
            writer.println("[Shell -> ATTR] Attribute \"" + s[1] + "\" is set. It has no values !");
          } else {
            utl.attributes.get(s[1]).clear();
            writer.println("[Shell -> ATTR] Attribute \"" + s[1] + "\" is cleared !");
          }
        } else {
          //Remove the attribute itself
          if (s[0].equalsIgnoreCase("-r")) {
            utl.attributes.remove(s[1]);
            writer.println("[Shell -> ATTR] Attribute \"" + s[1] + "\" was removed !");
          } else {
            errorStream.println("[Shell -> ATTR] Parameters not in the expected format !" + utl.newLineSeparator);
          }
        }
      } else {
        if (s.length == 1) {
          //Help requested
          if (s[0].equalsIgnoreCase("-h") || s[0].equalsIgnoreCase("-help") || s[0].equals("-?")) {
            writer.println(getHelpMessage());
          } else {
            //Remove all attributes
            if (s[0].equalsIgnoreCase("-r")) {
              utl.attributes = new BasicAttributes();
              //Print one attribute
            } else {
              writer.print("[Shell -> ATTR] Printing attribute ");

              if (utl.attributes.get(s[0]) != null) {
                writer.println(utl.attributes.get(s[0]));
              } else {
                writer.println();
              }
            }
          }
        } else {
          //Print all attributes
          if (s.length == 0) {
            if (utl.attributes.size() > 0) {
              writer.println("[Shell -> ATTR] Printing attributes : ");
              try {
                NamingEnumeration enumeration = utl.attributes.getAll();

                while (enumeration.hasMore()) {
                  writer.print(utl.newLineSeparator + "[Shell -> ATTR] Attribute " + enumeration.next().toString());
                }

                writer.println();
              } catch (NamingException ne) {
                // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here
                // Please do not remove this comment!
                //Do nothing !
                writer.println();
              }
            } else {
              writer.println("[Shell -> ATTR] No attributes to print !");
            }
          } else {
            errorStream.println("[Shell -> ATTR] Parametes not in the expected format !" + utl.newLineSeparator);
          }
        }
      }
    }

    writer.println();
  }

  /**
   * Gets the name of the command
   *
   * @return   The name of the command.
   */
  public String getName() {
    return "ATTR";
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
   * @return   Shell provider names
   */
  public String[] getSupportedShellProviderNames() {
    return new String[] {"InQMyShell"};
  }

  /**
   * Gives a short help message about the command
   *
   * @return   A help message for this command.
   */
  public String getHelpMessage() {
    return "  Modifies or prints the \"fake\" attributes." + utl.newLineSeparator +
           "    Usage: ATTR [<attribute>|<-a|-r attibute [value]>|<-r>]" + utl.newLineSeparator +
           "      Parameters:" + utl.newLineSeparator +
           "        -a - Sets attribute or adds a value to attribute." + utl.newLineSeparator +
           "        -r - Removes value/attribute/all attributes." + utl.newLineSeparator +
           "        <attribute> - Attribute to modify or print." + utl.newLineSeparator +
           "        <value> - Value to set/add/remove." + utl.newLineSeparator +
           "      If no parameters are specified lists the attributes.";
  }

}

