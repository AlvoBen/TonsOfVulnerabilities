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
import com.sap.engine.lib.util.ArrayObject;
import com.sap.engine.lib.util.Stack;

/**
 * Shell command implementing LS
 *
 * @version 6.30 Oct 2002
 * @author Hristo S. Iliev
 */
public class JNDILsServ implements Command {

  /**
   * Used for getting the names/types of the nodes
   */
  private NamingEnumeration ne = null;
  /**
   * Used for constructing the name of the current node
   */
  private String dummy = new String();
  /**
   * Stack for recursing the tree
   */
  private Stack st = null;
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
   * Number of columns to use
   */
  private static final int MAX_COLS = 80;

  /**
   * A method that executes the command .
   *
   * @param   env  An implementation of Environment.
   * @param   is  The InputStream , used by the command.
   * @param   os  The OutputStream , used by the command.
   * @param   s  Parameters of the command.
   */
  public void exec(Environment env, InputStream is, OutputStream os, String s[]) {
    errorStream = new PrintStream(env.getErrorStream());
    writer = new PrintStream(os);

    if ((s.length > 0) && (s[0].equalsIgnoreCase("-h") || s[0].equalsIgnoreCase("-help") || s[0].equals("-?"))) {
      writer.println(getHelpMessage());
      return;
    } else {
      try {
        String loc = null;
        boolean fullNames = false;
        boolean attributes = false;
        boolean details = false;

        //If no parameters found - list the current work context
        if (s.length == 0) {
          loc = utl.relativePath;
        } else {
          //Parameters check
          for (int i = 0; i < s.length; i++) {
            if (s[i].equalsIgnoreCase("-f")) {
              if (details == false) {
                writer.println(getHelpMessage());
                return;
              }
              fullNames = true;
            } else {
              if (s[i].equalsIgnoreCase("-a")) {
                if (details == false) {
                  writer.println(getHelpMessage());
                  return;
                }
                attributes = true;
              } else {
                if (s[i].equalsIgnoreCase("-l")) {
                  details = true;
                } else {
                  if (loc == null) {
                    loc = s[i];
                    if (s.length > i+1) {
                      writer.println(getHelpMessage());
                      return;
                    }
                  }
                }
              }
            }
          }
        }

        //If no location specified - look for relative path
        if (loc == null) {
          loc = utl.relativePath;
        }

        //Check the location
        loc = utl.modifyPath(loc);
        writer.println("[Shell -> LS] Location : " + loc);

        //Connect
        if (utl.ctx == null) {
          writer.println("Trying to connect... ");
          Properties p = new Properties();
          p.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
          utl.ctx = new InitialDirContext(p);
          writer.println("Connected.");
        }

        Object ctx = utl.ctx.lookup(loc);

        if ((ctx instanceof Context) || (ctx instanceof DirContext)) {
          if (details) {
            //Removes leading '/'
            if (loc.charAt(0) == '/') {
              loc = loc.substring(1);
            }

            writer.println();
            printContext(loc, fullNames, attributes);
            writer.println();
          } else {
            printShort((DirContext) ctx);
          }
        } else {
          errorStream.println("[Shell -> LS] '" + loc + "' is not a context !");
        }
      } catch (NamingException eNaming) {
        // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
        // Please do not remove this comment!
        errorStream.println("[Shell -> LS] NamingException");
        errorStream.println("[Shell -> LS] Reason : " + eNaming.getExplanation());
      }
    }
  }

  /**
   * Prints the naming in the "short" format
   *
   * @param  ctx   Location to begin printing from
   */
  public void printShort(DirContext ctx) throws NamingException {
    ArrayObject arr = new ArrayObject(10, 10);
    NamingEnumeration enumeration = ctx.list("");
    int max = 0;
    int lng;

    while (enumeration.hasMore()) {
      NameClassPair item = (NameClassPair) enumeration.next();

      if (utl.isContext(item.getClassName()) == true) {
        lng = item.getName().length() + 2;

        if (lng > max) {
          max = lng;
        }

        arr.add("[" + item.getName() + "]");
      } else {
        lng = item.getName().length();

        if (lng > max) {
          max = lng;
        }

        arr.add(item.getName());
      }
    }

    if (arr.size() > 0) {
      outputFormatted(arr, max);
    } else {
      writer.println("[Shell -> LS] Context '" + ctx.getNameInNamespace() + "' is empty !");
    }
  }

  /**
   * Outputs the items in array parameter formatted
   *
   * @param  array   Array of items to output
   * @param  maxLong  Maximal length of items
   */
  public void outputFormatted(ArrayObject array, int maxLong) {
    int columns = (MAX_COLS - 1) / maxLong;
    int colWidth = MAX_COLS / columns;
    int printedSoFar = 0;
    writer.println();

    for (int i = 0; i < array.size(); i++) {
      if (printedSoFar++ < columns) {
        String item = (String) array.elementAt(i);
        writer.print(item);

        for (int j = item.length(); j < colWidth; j++) {
          writer.print(" ");
        } 
      } else {
        printedSoFar = 0;
        writer.println();
        // this itteration is missed for us without typing binding name in the output => 
        // we need to repeat this itteration once more
        i--;
      }
    } 

    writer.println();
  }

  /**
   * Ouptuts tha attributes of a node
   *
   * @param  kwo   Node to scan and print attributes for
   */
  void outputAttr(String kwo) throws NamingException {
    NamingEnumeration atrbs = utl.ctx.getAttributes(kwo).getAll();
    String atrName = new String();

    while (atrbs.hasMore()) {
      atrName = atrbs.next().toString();
      writer.print(atrName);

      if (atrbs.hasMore()) {
        writer.print("; ");
      }
    }
  }

  /**
   * Recurse the tree
   *
   * @param  nm   Starting node
   * @param  fullNames   Request full names printed
   * @param  showAttr   Request attributes printed
   */
  private void prnt(String nm, boolean fullNames, boolean showAttr) {
    try {
      while (ne.hasMoreElements()) {
        NameClassPair item = (NameClassPair) ne.next();

        if (utl.isContext(item.getClassName()) == true) {
          //{{Output the current directory
          dummy = nm + "/" + item.getName();
          int j = utl.countDirNum(dummy) - 1;

          for (int i = 0; i <= j; i++) {
            writer.print("  ");
          } 

          //Show full names ?
          if (fullNames == true) {
            writer.print(dummy + " *");
          } else {
            writer.print(item.getName() + " *");
          }

          //Show attributes ?
          if (showAttr == true) {
            writer.print(" [");
            outputAttr(dummy);
            writer.println("]");
          } else {
            writer.println();
          }

          //}}
          //{{Recurse the inner level
          st.push(ne); //Save the location
          ne = utl.ctx.list(dummy);
          prnt(dummy, fullNames, showAttr);
          ne = (NamingEnumeration) st.pop(); //Load the location
          dummy = nm;
          //}}
        } else {
          //Output a binding
          int j = 0;

          if (dummy.length() != 0) {
            j = utl.countDirNum(dummy) - 1;
            writer.print("  ");
          }

          for (int i = 0; i <= j; i++) {
            writer.print("  ");
          } 

          //Show full names ?
          if (fullNames == true) {
            if (dummy.length() != 0) {
              writer.print(dummy + "/" + item.getName());
            } else {
              writer.print(item.getName());
            }
          } else {
            writer.print(item.getName());
          }

          //Show attributes ?
          if (showAttr == true) {
            writer.print(" [");
            outputAttr(dummy + '/' + item.getName());
            writer.println("]");
          } else {
            writer.println();
          }
        }
      }
    } catch (NamingException e) {
      // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
      // Please do not remove this comment!
      errorStream.println(utl.newLineSeparator + "[Shell -> LS] Error when printing the tree !");
      errorStream.println("Reason : " + e.getExplanation() + utl.newLineSeparator);
    } catch (NullPointerException ex) {
      // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
      // Please do not remove this comment!
      errorStream.println(utl.newLineSeparator + "[Shell -> LS] No such subcontext !");
    }
  }

  /**
   * Prints the tree
   *
   * @param  nm   Starting node
   * @param  full   Request full names printed
   * @param  attrs   Request attributes printed
   */
  public void printContext(String nm, boolean full, boolean attrs) {
    try {
      if (utl.ctx.lookup(nm) instanceof Context) {
        ne = utl.ctx.list(nm);
        st = new Stack();

        if (nm.length() != 0) {
          writer.print("  ");
        }

        writer.println("/" + nm + " *");
        dummy = nm;
        prnt(nm, full, attrs);
      } else {
        System.out.println(utl.newLineSeparator + "[Shell -> LS] Not a subcontext !"); //$JL-SYS_OUT_ERR$
        System.out.println("[Shell -> LS] Object contains : " + utl.ctx.lookup(nm)); //$JL-SYS_OUT_ERR$
      }
    } catch (NamingException e) {
      // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
      // Please do not remove this comment!
      errorStream.println(utl.newLineSeparator + "[Shell -> LS] Error while printing the tree !");
      errorStream.println("Reason : " + e.getExplanation() + utl.newLineSeparator);
    } catch (NullPointerException ex) {
      // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here; shell command
      // Please do not remove this comment!
      errorStream.println(utl.newLineSeparator + "[Shell -> LS] No such subcontext !");
    }
  }

  /**
   * Gets the command's name
   *
   * @return Name of the command
   */
  public String getName() {
    return "LS";
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
    return "  Lists the contents of the current context." + utl.newLineSeparator +
           "     Usage: LS [-l [-a] [-f]] [location]" + utl.newLineSeparator +
           "     Parameters:" + utl.newLineSeparator +
           "       [-l] - Lists details." + utl.newLineSeparator +
           "         [-a] - Shows the attributes. Can be used only with -l parameter." + utl.newLineSeparator +
           "         [-f] - Shows the full names. Can be used only with -l parameter." + utl.newLineSeparator +
           "       [location] - the path to the location." + utl.newLineSeparator +
           "       If no parameters are specified, the current context is listed.";
  }

}

