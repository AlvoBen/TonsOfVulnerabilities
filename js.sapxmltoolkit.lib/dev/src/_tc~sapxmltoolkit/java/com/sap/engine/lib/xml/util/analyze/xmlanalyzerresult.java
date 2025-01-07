package com.sap.engine.lib.xml.util.analyze;

public class XMLAnalyzerResult {

  public int totchars = -1, tagcount = -1, tagsymbols = -1, docdata = -1, tagchars = -1;
  public long proctime = -1;
  String fname = null;

  public XMLAnalyzerResult(String fname, int totchars, int tagcount, int tagchars, int tagsymbols, int docdata, long proctime) {
    this.totchars = totchars;
    this.tagcount = tagcount;
    this.tagchars = tagchars;
    this.tagsymbols = tagsymbols;
    this.docdata = docdata;
    this.proctime = proctime;
    this.fname = fname;
  }

  public String toString() {
    return "Analyzis report for: " + fname + "\n" + "  Processed in : " + proctime + " ms\n" + "  Doc size     : " + totchars + " bytes\n" + "  Tag count    : " + tagcount + "\n" + "  Tag symbols  : " + tagsymbols + "\n" + "  Tag chars    : " + tagchars + "\n" + "  Doc chars    : " + docdata + "\n";
  }

}

