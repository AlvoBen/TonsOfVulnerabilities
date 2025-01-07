package com.sap.engine.lib.xml.util.analyze;

import java.io.*;

public class XMLAnalyzer {

  public XMLAnalyzerResult analyze(String fname) throws IOException {
    BufferedInputStream in = new BufferedInputStream(new FileInputStream(fname));
    //FileInputStream in = new FileInputStream(fname);
    int ii;
    boolean inTag = false;
    int tagcount = 0;
    int tagchars = 0;
    int tagsyms = 0;
    int docchars = 0;
    int totchars = 0;
    long proctime = System.currentTimeMillis();

    while ((ii = in.read()) != -1) {
      if (ii == '<') {
        inTag = true;
        tagcount++;
        tagsyms++;
      } else if (ii == '/') {
        tagsyms++;
      } else if (ii == '>') {
        tagsyms++;
        inTag = false;
      } else if (inTag) {
        tagchars++;
      } else {
        docchars++;
      }

      totchars++;
    }

    proctime = System.currentTimeMillis() - proctime;
    in.close();
    return new XMLAnalyzerResult(fname, totchars, tagcount, tagchars, tagsyms, docchars, proctime);
  }

}

