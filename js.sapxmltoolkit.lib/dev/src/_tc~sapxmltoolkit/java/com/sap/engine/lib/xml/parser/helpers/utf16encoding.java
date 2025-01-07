package com.sap.engine.lib.xml.parser.helpers;

import com.sap.engine.lib.xml.parser.handlers.EncodingHandler;

public class UTF16Encoding extends Encoding {

  //  private int res[] = new int[2];
  //  private byte utf8buf[] = new byte[10];
  //  private byte utf8buflen = 0;
  //  private int utf8left = 0;
  private byte utf16buf[] = new byte[2];
  //  private int x1;
  public boolean hasbyte = false;
  private CharArray name;
  private boolean bigEndian;

  public UTF16Encoding(boolean bigEndian) {
    this.bigEndian = bigEndian;
    if (bigEndian) {
      name = EncodingHandler.utf_16be ;
    } else {
      name = EncodingHandler.utf_16;
    }
  }

  public int process(byte ch) {
    //    LogWriter.getSystemLogWriter().println("UTF16.process: " + Integer.toHexString(ch));
    if (!hasbyte) {
      utf16buf[0] = ch;
      hasbyte = true;
      return NEEDS_MORE_DATA;
    } else {
      hasbyte = false;
      //      int ret = (int)((ch << 8) | (utf16buf[0]));
      //      LogWriter.getSystemLogWriter().println("UTF16.return: " + Integer.toHexString(ret));
      //      return ret;
      int b1;
      int b2;
      if (bigEndian) {
        b1 = ((int)utf16buf[0]  << 8) & 0xFF00;
        b2 = ch & 0xFF;
      } else {
        b1 = (ch << 8) & 0xFF00;
        b2 = (int)utf16buf[0] & 0xFF;
      }
      return b1 | b2;
      //return (int)(((ch << 8)&0xFF00) | (utf16buf[0]));
    }
  }

  public int reverseEncode(byte[] converted, int ch) {
    if (!bigEndian) {
      converted[0] = (byte) (ch & 0xFF);
      converted[1] = (byte) ((ch & 0xFF00) >> 8);
    } else {
      converted[0] = (byte) ((ch & 0xFF00) >> 8);
      converted[1] = (byte) (ch & 0xFF);
    }
    return 2;
  }

  //    if (utf8left > 0) {
  //      utf8buf[utf8buflen++] = ch;
  //      utf8left--;
  //      if (utf8left > 0) return NEEDS_MORE_DATA;
  //    }
  //    if (utf8left == 0 && utf8buflen > 0) {
  //      x1 = parseUTF8toUCS4(utf8buf, 0, utf8buflen)[1];
  //      utf8buflen = 0;
  //      return x1;
  //    } else {
  //      x1 = (int)ch;
  //    }
  //    if (ch < 0 && utf8left == 0) {
  //      utf8left = getUTF8CharCount(ch);
  //      utf8buf[utf8buflen++] = ch;
  //      utf8left--;
  //      return NEEDS_MORE_DATA;
  //    }
  //    
  //    return x1;
  //  }
  //
  //  public int reverseEncode(byte converted[], int character) {
  //    int length=-1;
  //    if (character<0x80) {
  //      if (converted.length<1) return -1;
  //      converted[0] = (byte)character;
  //      length = 1;
  //    } else if(character<0x800) {
  //      if (converted.length<2) return -1;
  //      converted[0] = (byte)(0xc0 | (character >> 6));
  //      converted[1] = (byte)(0x80 | (character & 0x3f));
  //      length = 2;
  //    } else if(character<0x10000) {
  //      if (converted.length<3) return -1;
  //      converted[0] = (byte)(0xe0 | (character >> 12));
  //      converted[1] = (byte)(0x80 | ((character >> 6) & 0x3f));
  //      converted[2] = (byte)(0x80 | (character & 0x3f));
  //      length = 3;
  //    } else if(character<0x200000) {
  //      if (converted.length<4) return -1;
  //      converted[0] = (byte)(0xf0 | (character >> 18));
  //      converted[1] = (byte)(0x80 | ((character >> 12) & 0x3f));
  //      converted[2] = (byte)(0x80 | ((character >> 6) & 0x3f));
  //      converted[3] = (byte)(0x80 | (character & 0x3f));
  //      length = 4;
  //    } else if(character<0x4000000) {
  //      if (converted.length<5) return -1;
  //      converted[0] = (byte)(0xf8 | (character >> 24));
  //      converted[1] = (byte)(0x80 | ((character >> 18) & 0x3f));
  //      converted[2] = (byte)(0x80 | ((character >> 12) & 0x3f));
  //      converted[3] = (byte)(0x80 | ((character >> 6) & 0x3f));
  //      converted[4] = (byte)(0x80 | (character & 0x3f));
  //      length = 5;
  //    } else {
  //      if (converted.length<6) return -1;
  //      converted[0] = (byte)(0xfc | (character >> 30));
  //      converted[1] = (byte)(0x80 | ((character >> 24) & 0x3f));
  //      converted[2] = (byte)(0x80 | ((character >> 18) & 0x3f));
  //      converted[3] = (byte)(0x80 | ((character >> 12) & 0x3f));
  //      converted[4] = (byte)(0x80 | ((character >> 6) & 0x3f));
  //      converted[5] = (byte)(0x80 | (character & 0x3f));
  //      length = 6;
  //    }
  //    return length;
  //  }
  //  
  //  protected  int[] parseUTF8toUCS4(byte[]data, int off, int len) {
  //    res[0] = len;
  //    if (len == 6) {
  //      res[1] = (data[off] & 0x1) << 30;
  //      res[1] |= (data[off+1] & 0x3F) << 24;
  //      res[1] |= (data[off+2] & 0x3F) << 18;
  //      res[1] |= (data[off+3] & 0x3F) << 12;
  //      res[1] |= (data[off+4] & 0x3F) << 6;
  //      res[1] |= (data[off+5] & 0x3F);
  //    } else if (len == 5) {
  //      res[1] = (data[off] & 0x3) << 24;
  //      res[1] |= (data[off+1] & 0x3F) << 18;
  //      res[1] |= (data[off+2] & 0x3F) << 12;
  //      res[1] |= (data[off+3] & 0x3F) << 6;
  //      res[1] |= (data[off+4] & 0x3F);
  //    } else if (len == 4) {
  //      res[1] = (data[off] & 0x7) << 18;
  //      res[1] |= (data[off+1] & 0x3F) << 12;
  //      res[1] |= (data[off+2] & 0x3F) << 6;
  //      res[1] |= (data[off+3] & 0x3F);
  //    } else if (len == 3) {
  //      res[1] = (data[off] & 0xF) << 12;
  //      res[1] |= (data[off+1] & 0x3F) << 6;
  //      res[1] |= (data[off+2] & 0x3F);
  //    } else if (len == 2) {
  //      res[1] = (data[off] & 0x1F) << 6;
  //      res[1] |= (data[off+1] & 0x3F);
  //    } else if (len == 1) {
  //      res[1] = data[off];
  //    }
  //    return res;
  //  }
  //  
  //  protected  int getUTF8CharCount(byte ch) {
  //    if ((ch & 0xFC) == 0xFC) {
  //      return 6;
  //    } else if ((ch & 0xF8) == 0xF8) {
  //      return 5;
  //    } else if ((ch & 0xF0) == 0xF0) {
  //      return 4;
  //    } else if ((ch & 0xE0) == 0xE0) {
  //      return 3;
  //    } else if ((ch & 0xC0) == 0xC0) {
  //      return 2;
  //    } else {
  //      return 1;
  //    }
  //  }
  //  
  public CharArray getName() {
    return name;
  }

}

