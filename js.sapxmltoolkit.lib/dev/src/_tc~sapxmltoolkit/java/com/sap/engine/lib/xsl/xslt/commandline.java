package com.sap.engine.lib.xsl.xslt;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.Info;
import com.sap.engine.lib.xml.SystemProperties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public final class CommandLine {

  public static void printUsage() {
    LogWriter.getSystemLogWriter().println(Info.COMPANY + " XSLT engine."); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("\n"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("Usage: " + Info.BATCH_XSLT + " (<parameter> | <option>)*"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println(""); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("Parameters:"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        [-xml] <filename_or_url>                      (required)"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        [-xsl] <filename_or_url>                      (optional)"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        [-out] <filename_or_url>                      (optional)"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        If you do not specify the -xml -xsl or -out prefixes then this order is assumed"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println(""); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("Options:"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        -indent"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        -omitxmldecl"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        -encoding <name_of_encoding>"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        -version <name_of_version>"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        -method  ( xml | text | html | dump )"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        -cdata   ( <name_of_element> )*  [ -endcdata ]"); //$JL-SYS_OUT_ERR$
    //LogWriter.getSystemLogWriter().println("        -quiet                                        (does not print to the console)");
    LogWriter.getSystemLogWriter().println("        -P<name>=<value>                              (set the specified <value> to the <name> parameter )"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        -apache                                       (run with Apache's Xalan transformer)"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("        -other   <TransformerFactory_class_name>      (run with other implementations)"); //$JL-SYS_OUT_ERR$
  }

  public static void main(String[] args) throws TransformerException, IOException, ClassNotFoundException {
    String xml = null;
    String xsl = null;
    String out = null;
    boolean defaulted = false;
    Properties p = new Properties();
    Properties params = new Properties();
    boolean useApache = false;
    String other = null;
    try {
      for (int i = 0; i < args.length; i++) {
        String s = args[i];

        if (s.equals("-xml")) {
          xml = args[++i];
        } else if (s.equals("-xsl")) {
          xsl = args[++i];
        } else if (s.equals("-out")) {
          out = args[++i];
        } else if (s.equals("-indent")) {
          p.setProperty(OutputKeys.INDENT, "yes");
        } else if (s.equals("-omitxmldecl")) {
          p.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        } else if (s.equals("-version")) {
          p.setProperty(OutputKeys.VERSION, args[++i]);
        } else if (s.equals("-encoding")) {
          p.setProperty(OutputKeys.ENCODING, args[++i]);
        } else if (s.equals("-method")) {
          p.setProperty(OutputKeys.METHOD, args[++i]);
        } else if (s.equals("-cdata")) {
          i++;
          String u = "";

          while (i < args.length) {
            if (args[i].equals("-endcdata")) {
              break;
            }

            u += args[i] + ' ';
            i++;
          }

          p.setProperty(OutputKeys.CDATA_SECTION_ELEMENTS, u);
        } else if (s.equals("-other")) {
          other = args[++i];
        } else if (s.equals("-apache")) {
          useApache = true;
          /*
           } else if (s.equals("-quiet")) {
           quiet = true;
           */
        } else if (s.startsWith("-P")) {
          int equalsIndex = s.indexOf("=");
          String name = s.substring(2, equalsIndex);
          String value = s.substring(equalsIndex + 1);
          params.setProperty(name, value);
        } else {
          defaulted = true;

          if (xml == null) {
            xml = s;
          } else if (xsl == null) {
            xsl = s;
          } else if (out == null) {
            out = s;
          } else {
            printUsage();
            return;
          }
        }
      } 
    } catch (RuntimeException e) {
      //$JL-EXC$
      //A command line file. In case that there is an exception - print the usage and the exception
      e.printStackTrace();
      printUsage();
      return;
    }

    if (other != null) {
      SystemProperties.setProperty("javax.xml.transform.TransformerFactory", other);
    } else if (!useApache) {
      SystemProperties.setProperty("javax.xml.transform.TransformerFactory", "com.sap.engine.lib.jaxp.TransformerFactoryImpl");
    } else {
      SystemProperties.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
    }

    /*
     if (defaulted) {
     if (
     ((xml != null) && !xml.endsWith(".xml")) ||
     ((xsl != null) && !xsl.endsWith(".xsl")) ||
     ((out != null) && !out .endsWith(".out") && !xml.endsWith(".txt"))
     ) {
     LogWriter.getSystemLogWriter().println("");
     LogWriter.getSystemLogWriter().println("  XML: " + nonNull(xml));
     LogWriter.getSystemLogWriter().println("  XSL: " + nonNull(xsl));
     LogWriter.getSystemLogWriter().println("  OUT: " + nonNull(out));
     LogWriter.getSystemLogWriter().println("");
     while (true) {
     LogWriter.getSystemLogWriter().print("Are you sure you want to use these? (y/n): ");
     String reply = new BufferedReader(new InputStreamReader(System.in)).readLine();
     if ((reply == null) || (reply.trim().length() == 0)) {
     continue;
     }
     char ch = reply.trim().charAt(0);
     if ((ch != 'y') && (ch != 'Y')) {
     LogWriter.getSystemLogWriter().println("Transformation aborted.");
     return;
     }
     break;
     }
     }
     }
     */
    if (xml == null) {
      printUsage();
      return;
    }

    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer t = (xsl == null) ? tf.newTransformer() : tf.newTransformer(new StreamSource(xsl));

    for (Enumeration e = p.propertyNames(); e.hasMoreElements();) {
      String s = (String) e.nextElement();
      t.setOutputProperty(s, p.getProperty(s));
    } 
    for (Enumeration e = params.propertyNames(); e.hasMoreElements(); ) {
      String s = (String) e.nextElement();
      t.setParameter(s, params.getProperty(s));
    }
    
    ByteArrayOutputStream byteArrayOutputStream = null;
    try {
      Result result = null;
      if(out == null) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        result = new StreamResult(byteArrayOutputStream);
      } else {
        result = new StreamResult(out);
      }
        
      try {
        t.transform(new StreamSource(xml), result);
      } catch (StackOverflowError e) {
        LogWriter.getSystemLogWriter().println(e.toString()); //$JL-SYS_OUT_ERR$
      }
    } finally {
      if(byteArrayOutputStream != null) {
        byteArrayOutputStream.flush();
        LogWriter.getSystemLogWriter().println(new String(byteArrayOutputStream.toByteArray())); //$JL-I18N$
        byteArrayOutputStream.close();
      }
    }
  }

  private static String nonNull(String s) {
    return (s == null) ? "NONE" : s;
  }

}

