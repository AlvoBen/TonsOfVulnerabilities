package com.sap.engine.lib.xsl.xslt;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.Symbols;

public class XMLComparer {

  public XMLComparer() {

  }

  public static int prereadWhitespace(InputStream in, int a) throws IOException {
    int c;

    while (Symbols.isWhitespace((char) (c = in.read()))) {
      if (c == 0xA) {
        if (a == 1) {
          row1++;
        } else if (a == 2) {
          row2++;
        }
      }
    }

    return c;
  }

  public static int row1, row2;

  public static boolean compare(String file1, String file2) throws Exception {
    BufferedInputStream in1 = new BufferedInputStream(new FileInputStream(file1));
    BufferedInputStream in2 = new BufferedInputStream(new FileInputStream(file2));
    int a, b;
    int n = -1;
    row1 = 1;
    row2 = 1;

    while (true) {
      n++;
      a = in1.read();
      b = in2.read();

      if (a == -1 && b == -1) {
        return true;
      } else if (a == -1 || b == -1) {
        LogWriter.getSystemLogWriter().println("Difference :  One source continues after the other on byte:" + n); //$JL-SYS_OUT_ERR$
        return false;
      }

      if (a != b && Character.toUpperCase((char) a) != Character.toUpperCase((char) b)) {
        if (Symbols.isWhitespace((char) a)) {
          a = prereadWhitespace(in1, 1);
        }

        if (Symbols.isWhitespace((char) b)) {
          b = prereadWhitespace(in2, 2);
        }

        if (Character.toUpperCase((char) a) == Character.toUpperCase((char) b)) {
          continue;
        }

        if (a == -1 && b == -1) {
          return true;
        } else if (a == -1 || b == -1) {
          LogWriter.getSystemLogWriter().println("Difference :  One source continues after the other on byte:" + n); //$JL-SYS_OUT_ERR$
          return false;
        } else if ((a == '\'' && b == '\"') || (a == '\"' && b == '\'')) {
          continue;
        } else {
          LogWriter.getSystemLogWriter().println("Difference on byte: " + n); //$JL-SYS_OUT_ERR$
          LogWriter.getSystemLogWriter().println("\n" + file1 + ":" + row1 + ": " + (char) a + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read()); //$JL-SYS_OUT_ERR$
          LogWriter.getSystemLogWriter().println("" + file2 + ":" + row2 + ": " + (char) b + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in2.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read() + (char) in1.read()); //$JL-SYS_OUT_ERR$
          return false;
        }
      } else if (a == 0xa && a == b) {
        row1++;
        row2++;
      }
    }
  }

  public static void main(String args[]) throws Exception {
    String fi1, fi2;
    //    String fi1 = "c:/develop/xml2000/bugs/bgsag/xalan.html";
    //    String fi2 = "c:/develop/xml2000/bugs/bgsag/xa-a-51-42-02-00a-010a-a.xml.out.html";
    fi1 = "d:/develop/xml2000/tests/xsltmark2/testcases/xslbench2.out.tmp";
    fi2 = "d:/develop/xml2000/tests/xsltmark2/testcases/xslbench2.ref";
    new XMLComparer().compare(fi1, fi2);
  }

}

