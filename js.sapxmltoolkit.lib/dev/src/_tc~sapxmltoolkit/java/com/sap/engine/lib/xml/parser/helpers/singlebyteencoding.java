package com.sap.engine.lib.xml.parser.helpers;

public class SingleByteEncoding extends Encoding {

  private char[] mapping = null;
  private CharArray name = new CharArray();
  private boolean allCharsMapping = false;

  public SingleByteEncoding(char[] mapping, CharArray name, boolean allCharsMapping) {
    this.mapping = mapping;
    //this.name.set(name);
    this.name = name;
    name.setStatic();
    this.allCharsMapping = allCharsMapping;
  }

  public SingleByteEncoding(char[] mapping, CharArray name) {
    this(mapping, name, false);
  }

//  private int x1;

  public int process(byte ch) {
    //LogWriter.getSystemLogWriter().println(Integer.toHexString(ch));
    if (ch < 0) {
      //x1 = 0x100 + ch;
      //ch = 0x100 + ch;
      return mapping[0x100 + ch];
    } else {
      if (allCharsMapping) {
        return mapping[ch];
      }

      return ch;
    }

    //LogWriter.getSystemLogWriter().println("-- afetr:" + Integer.toHexString(mapping[x1]));
  }

  public int reverseEncode(byte result[], int ch) {
    for (int i = 0x7f; i <= 0xFF; i++) {
      if (mapping[i] == ch) {
        result[0] = (byte) i;
        return 1;
      }
    } 

    return UNSUPPORTED_CHAR;
  }

  public CharArray getName() {
    return name;
  }

}

