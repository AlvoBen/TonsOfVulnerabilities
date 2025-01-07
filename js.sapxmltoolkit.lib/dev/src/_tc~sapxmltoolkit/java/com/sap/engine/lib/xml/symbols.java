package com.sap.engine.lib.xml;


/**
 *   Contains methods, recognizing characters as productions
 * from the specification.
 *
 * @author  Nick Nickolov
 */
public final class Symbols {

  public static boolean isWhitespace(char ch) {
    return ((ch == 0x0020) || (ch == 0x000D) || (ch == 0x000A) || (ch == 0x0009));
  }

  public static boolean isWhitespaceNo13(char ch) {
    return ((ch == 0x0020) || (ch == 0x000A) || (ch == 0x0009));
  }

  public static boolean isNameChar(char ch) {
    return (isLetter(ch) || isDigit(ch) || (ch == '.') || (ch == '-') || (ch == '_') || (ch == ':') || isCombiningChar(ch) || isExtender(ch));
  }

  public static boolean isInitialNameChar(char ch) {
    return (isLetter(ch) || (ch == '_') || (ch == ':'));
  }

  public static boolean isLetterOrDigitAndSpace(char ch) {
    return (isWhitespace(ch) || isLetter(ch) || isDigit(ch));
  }

  public static boolean isLetterOrDigit(char ch) {
    return (isLetter(ch) || isDigit(ch));
  }

  public static boolean isChar(char ch) {
    return ((ch == 0x0009) || (ch == 0x000A) || (ch == 0x000D) || (ch != 38 && (ch >= 0x0020) && (ch <= 0xD7FF)) || ((ch >= 0xE000) && (ch <= 0xFFFD)) || ((ch >= 0x10000) && (ch <= 0x10FFFF)));
  }
  
  public static boolean isValidChar(char ch) {
    return (((ch >= 0x0020) && (ch <= 0xD7FF)) || (ch == 0x000D) ||  (ch == 0x000A) || (ch == 0x0009) ||((ch >= 0xE000) && (ch <= 0xFFFD)) || ((ch >= 0x10000) && (ch <= 0x10FFFF)));
  }
  
  public static boolean isLetter(char ch) {
    return (isBaseChar(ch) || isIdeographic(ch));
  }
  
  public static boolean isBase(char ch) {
    return (((ch >= 0x0041) && (ch <= 0x005A)) || ((ch >= 0x0061) && (ch <= 0x007A)) || ((ch >= 0x00C0) && (ch <= 0x00D6)) || ((ch >= 0x00D8) && (ch <= 0x00F6)) || ((ch >= 0x00F8) && (ch <= 0x00FF)) || ((ch >= 0x0100) && (ch <= 0x0131)) || ((ch >= 0x0134) && (ch <= 0x013E)) || ((ch >= 0x0141) && (ch <= 0x0148)) || ((ch >= 0x014A) && (ch <= 0x017E)) || ((ch >= 0x0180) && (ch <= 0x01C3)) || ((ch >= 0x01CD) && (ch <= 0x01F0)) || ((ch >= 0x01F4) && (ch <= 0x01F5)) || ((ch >= 0x01FA) && (ch <= 0x0217)) || ((ch >= 0x0250) && (ch <= 0x02A8)) || ((ch >= 0x02BB) && (ch <= 0x02C1)) || (ch == 0x0386) || ((ch >= 0x0388) && (ch <= 0x038A)) || (ch == 0x038C) || ((ch >= 0x038E) && (ch <= 0x03A1)) || ((ch >= 0x03A3) && (ch <= 0x03CE)) || ((ch >= 0x03D0) && (ch <= 0x03D6)) || (ch == 0x03DA) || (ch == 0x03DC) || (ch == 0x03DE) || (ch == 0x03E0) || ((ch >= 0x03E2) && (ch <= 0x03F3)) || ((ch >= 0x0401) && (ch <= 0x040C)) || ((ch >= 0x040E) && (ch <= 0x044F)) || ((ch >= 0x0451) && (ch <= 0x045C)) || ((ch >= 0x045E) && (ch <= 0x0481)) || ((ch >= 0x0490) && (ch <= 0x04C4)) || ((ch >= 0x04C7) && (ch <= 0x04C8)) || ((ch >= 0x04CB) && (ch <= 0x04CC)) || ((ch >= 0x04D0) && (ch <= 0x04EB)) || ((ch >= 0x04EE) && (ch <= 0x04F5)) || ((ch >= 0x04F8) && (ch <= 0x04F9)) || ((ch >= 0x0531) && (ch <= 0x0556)) || (ch == 0x0559) || ((ch >= 0x0561) && (ch <= 0x0586)) || ((ch >= 0x05D0) && (ch <= 0x05EA)) || ((ch >= 0x05F0) && (ch <= 0x05F2)) || ((ch >= 0x0621) && (ch <= 0x063A)) || ((ch >= 0x0641) && (ch <= 0x064A)) || ((ch >= 0x0671) && (ch <= 0x06B7)) || ((ch >= 0x06BA) && (ch <= 0x06BE)) || ((ch >= 0x06C0) && (ch <= 0x06CE)) || ((ch >= 0x06D0) && (ch <= 0x06D3)) || (ch == 0x06D5) || ((ch >= 0x06E5) && (ch <= 0x06E6)) || ((ch >= 0x0905) && (ch <= 0x0939)) || (ch == 0x093D) || ((ch >= 0x0958) && (ch <= 0x0961)) || ((ch >= 0x0985) && (ch <= 0x098C)) || ((ch >= 0x098F) && (ch <= 0x0990)) || ((ch >= 0x0993) && (ch <= 0x09A8)) || ((ch >= 0x09AA) && (ch <= 0x09B0)) || (ch == 0x09B2) || ((ch >= 0x09B6) && (ch <= 0x09B9)) || ((ch >= 0x09DC) && (ch <= 0x09DD)) || ((ch >= 0x09DF) && (ch <= 0x09E1)) || ((ch >= 0x09F0) && (ch <= 0x09F1)) || ((ch >= 0x0A05) && (ch <= 0x0A0A)) || ((ch >= 0x0A0F) && (ch <= 0x0A10)) || ((ch >= 0x0A13) && (ch <= 0x0A28)) || ((ch >= 0x0A2A) && (ch <= 0x0A30)) || ((ch >= 0x0A32) && (ch <= 0x0A33)) || ((ch >= 0x0A35) && (ch <= 0x0A36)) || ((ch >= 0x0A38) && (ch <= 0x0A39)) || ((ch >= 0x0A59) && (ch <= 0x0A5C)) || (ch == 0x0A5E) || ((ch >= 0x0A72) && (ch <= 0x0A74)) || ((ch >= 0x0A85) && (ch <= 0x0A8B)) || (ch == 0x0A8D) || ((ch >= 0x0A8F) && (ch <= 0x0A91)) || ((ch >= 0x0A93) && (ch <= 0x0AA8)) || ((ch >= 0x0AAA) && (ch <= 0x0AB0)) || ((ch >= 0x0AB2) && (ch <= 0x0AB3)) || ((ch >= 0x0AB5) && (ch <= 0x0AB9)) || (ch == 0x0ABD) || (ch == 0x0AE0) || ((ch >= 0x0B05) && (ch <= 0x0B0C)) || ((ch >= 0x0B0F) && (ch <= 0x0B10)) || ((ch >= 0x0B13) && (ch <= 0x0B28)) || ((ch >= 0x0B2A) && (ch <= 0x0B30)) || ((ch >= 0x0B32) && (ch <= 0x0B33)) || ((ch >= 0x0B36) && (ch <= 0x0B39)) || (ch == 0x0B3D) || ((ch >= 0x0B5C) && (ch <= 0x0B5D)) || ((ch >= 0x0B5F) && (ch <= 0x0B61)) || ((ch >= 0x0B85) && (ch <= 0x0B8A)) || ((ch >= 0x0B8E) && (ch <= 0x0B90)) || ((ch >= 0x0B92) && (ch <= 0x0B95)) || ((ch >= 0x0B99) && (ch <= 0x0B9A)) || (ch == 0x0B9C) || ((ch >= 0x0B9E) && (ch <= 0x0B9F)) || ((ch >= 0x0BA3) && (ch <= 0x0BA4)) || ((ch >= 0x0BA8) && (ch <= 0x0BAA)) || ((ch >= 0x0BAE) && (ch <= 0x0BB5)) || ((ch >= 0x0BB7) && (ch <= 0x0BB9)) || ((ch >= 0x0C05) && (ch <= 0x0C0C)) || ((ch >= 0x0C0E) && (ch <= 0x0C10)) || ((ch >= 0x0C12) && (ch <= 0x0C28)) || ((ch >= 0x0C2A) && (ch <= 0x0C33)) || ((ch >= 0x0C35) && (ch <= 0x0C39)) || ((ch >= 0x0C60) && (ch <= 0x0C61)) || ((ch >= 0x0C85) && (ch <= 0x0C8C)) || ((ch >= 0x0C8E) && (ch <= 0x0C90)) || ((ch >= 0x0C92) && (ch <= 0x0CA8)) || ((ch >= 0x0CAA) && (ch <= 0x0CB3)) || ((ch >= 0x0CB5) && (ch <= 0x0CB9)) || (ch == 0x0CDE) || ((ch >= 0x0CE0) && (ch <= 0x0CE1)) || ((ch >= 0x0D05) && (ch <= 0x0D0C)) || ((ch >= 0x0D0E) && (ch <= 0x0D10)) || ((ch >= 0x0D12) && (ch <= 0x0D28)) || ((ch >= 0x0D2A) && (ch <= 0x0D39)) || ((ch >= 0x0D60) && (ch <= 0x0D61)) || ((ch >= 0x0E01) && (ch <= 0x0E2E)) || (ch == 0x0E30) || ((ch >= 0x0E32) && (ch <= 0x0E33)) || ((ch >= 0x0E40) && (ch <= 0x0E45)) || ((ch >= 0x0E81) && (ch <= 0x0E82)) || (ch == 0x0E84) || ((ch >= 0x0E87) && (ch <= 0x0E88)) || (ch == 0x0E8A) || (ch == 0x0E8D) || ((ch >= 0x0E94) && (ch <= 0x0E97)) || ((ch >= 0x0E99) && (ch <= 0x0E9F)) || ((ch >= 0x0EA1) && (ch <= 0x0EA3)) || (ch == 0x0EA5) || (ch == 0x0EA7) || ((ch >= 0x0EAA) && (ch <= 0x0EAB)) || ((ch >= 0x0EAD) && (ch <= 0x0EAE)) || (ch == 0x0EB0) || ((ch >= 0x0EB2) && (ch <= 0x0EB3)) || (ch == 0x0EBD) || ((ch >= 0x0EC0) && (ch <= 0x0EC4)) || ((ch >= 0x0F40) && (ch <= 0x0F47)) || ((ch >= 0x0F49) && (ch <= 0x0F69)) || ((ch >= 0x10A0) && (ch <= 0x10C5)) || ((ch >= 0x10D0) && (ch <= 0x10F6)) || (ch == 0x1100) || ((ch >= 0x1102) && (ch <= 0x1103)) || ((ch >= 0x1105) && (ch <= 0x1107)) || (ch == 0x1109) || ((ch >= 0x110B) && (ch <= 0x110C)) || ((ch >= 0x110E) && (ch <= 0x1112)) || (ch == 0x113C) || (ch == 0x113E) || (ch == 0x1140) || (ch == 0x114C) || (ch == 0x114E) || (ch == 0x1150) || ((ch >= 0x1154) && (ch <= 0x1155)) || (ch == 0x1159) || ((ch >= 0x115F) && (ch <= 0x1161)) || (ch == 0x1163) || (ch == 0x1165) || (ch == 0x1167) || (ch == 0x1169) || ((ch >= 0x116D) && (ch <= 0x116E)) || ((ch >= 0x1172) && (ch <= 0x1173)) || (ch == 0x1175) || (ch == 0x119E) || (ch == 0x11A8) || (ch == 0x11AB) || ((ch >= 0x11AE) && (ch <= 0x11AF)) || ((ch >= 0x11B7) && (ch <= 0x11B8)) || (ch == 0x11BA) || ((ch >= 0x11BC) && (ch <= 0x11C2)) || (ch == 0x11EB) || (ch == 0x11F0) || (ch == 0x11F9) || ((ch >= 0x1E00) && (ch <= 0x1E9B)) || ((ch >= 0x1EA0) && (ch <= 0x1EF9)) || ((ch >= 0x1F00) && (ch <= 0x1F15)) || ((ch >= 0x1F18) && (ch <= 0x1F1D)) || ((ch >= 0x1F20) && (ch <= 0x1F45)) || ((ch >= 0x1F48) && (ch <= 0x1F4D)) || ((ch >= 0x1F50) && (ch <= 0x1F57)) || (ch == 0x1F59) || (ch == 0x1F5B) || (ch == 0x1F5D) || ((ch >= 0x1F5F) && (ch <= 0x1F7D)) || ((ch >= 0x1F80) && (ch <= 0x1FB4)) || ((ch >= 0x1FB6) && (ch <= 0x1FBC)) || (ch == 0x1FBE) || ((ch >= 0x1FC2) && (ch <= 0x1FC4)) || ((ch >= 0x1FC6) && (ch <= 0x1FCC)) || ((ch >= 0x1FD0) && (ch <= 0x1FD3)) || ((ch >= 0x1FD6) && (ch <= 0x1FDB)) || ((ch >= 0x1FE0) && (ch <= 0x1FEC)) || ((ch >= 0x1FF2) && (ch <= 0x1FF4)) || ((ch >= 0x1FF6) && (ch <= 0x1FFC)) || (ch == 0x2126) || ((ch >= 0x212A) && (ch <= 0x212B)) || (ch == 0x212E) || ((ch >= 0x2180) && (ch <= 0x2182)) || ((ch >= 0x3041) && (ch <= 0x3094)) || ((ch >= 0x30A1) && (ch <= 0x30FA)) || ((ch >= 0x3105) && (ch <= 0x312C)) || ((ch >= 0xAC00) && (ch <= 0xD7A3)));
  }
  

  private static int[] basechars = null;
 
  static {
    basechars = new int[1726]; // 1726 = ((0xD7A4 / 32) + 1) + 1;
    fillBaseChars();
  //    test();
  }
  
  //    public static void test() {
  //      LogWriter.getSystemLogWriter().println("Symbols: testing base chars ..");
  //      for (int c = 0; c <= Character.MAX_VALUE; c ++) {
  //        if (isBase((char) c) != isBaseChar((char) c)) {
  //          throw new RuntimeException("basechars filled incorrectly ! problem is " + c);
  //        }
  //      }
  //      
  //    }
  
  private static void fillBaseChars() {
    int N;
    
    for (char i = 0; i < 0xD7A4;  i ++ ) {
      N = i >> 5;
  //      try {
        if (isBase(i)) {
          basechars[N] = basechars[N] | (1 << (i & 31));
        }
  //      } catch (Throwable t) {
  //        LogWriter.getSystemLogWriter().println((int) i);
  //        t.printStackTrace();
  //      }
    }
  }
  
  public static boolean isLetterOrDigitSimple(char ch) {
    if ((ch>='a' && ch <='z') || (ch>='A' && ch <= 'Z')|| (ch >= '0' && ch <= '9')) {
      return true;
    }
    return false;
  }
  
  public static boolean isBaseChar(char ch) {
    if (ch > 0xD7A3) {
      return false;
    }
    return (basechars[ch >> 5] & (1 << (ch & 31))) != 0;
  }

  public static boolean isIdeographic(char ch) {
    return (((ch >= 0x4E00) && (ch <= 0x9FA5)) || (ch == 0x3007) || ((ch >= 0x3021) && (ch <= 0x3029)));
  }

  public static boolean isCombiningChar(char ch) {
    return (((ch >= 0x0300) && (ch <= 0x0345)) || ((ch >= 0x0360) && (ch <= 0x0361)) || ((ch >= 0x0483) && (ch <= 0x0486)) || ((ch >= 0x0591) && (ch <= 0x05A1)) || ((ch >= 0x05A3) && (ch <= 0x05B9)) || ((ch >= 0x05BB) && (ch <= 0x05BD)) || (ch == 0x05BF) || ((ch >= 0x05C1) && (ch <= 0x05C2)) || (ch == 0x05C4) || ((ch >= 0x064B) && (ch <= 0x0652)) || (ch == 0x0670) || ((ch >= 0x06D6) && (ch <= 0x06DC)) || ((ch >= 0x06DD) && (ch <= 0x06DF)) || ((ch >= 0x06E0) && (ch <= 0x06E4)) || ((ch >= 0x06E7) && (ch <= 0x06E8)) || ((ch >= 0x06EA) && (ch <= 0x06ED)) || ((ch >= 0x0901) && (ch <= 0x0903)) || (ch == 0x093C) || ((ch >= 0x093E) && (ch <= 0x094C)) || (ch == 0x094D) || ((ch >= 0x0951) && (ch <= 0x0954)) || ((ch >= 0x0962) && (ch <= 0x0963)) || ((ch >= 0x0981) && (ch <= 0x0983)) || (ch == 0x09BC) || (ch == 0x09BE) || (ch == 0x09BF) || ((ch >= 0x09C0) && (ch <= 0x09C4)) || ((ch >= 0x09C7) && (ch <= 0x09C8)) || ((ch >= 0x09CB) && (ch <= 0x09CD)) || (ch == 0x09D7) || ((ch >= 0x09E2) && (ch <= 0x09E3)) || (ch == 0x0A02) || (ch == 0x0A3C) || (ch == 0x0A3E) || (ch == 0x0A3F) || ((ch >= 0x0A40) && (ch <= 0x0A42)) || ((ch >= 0x0A47) && (ch <= 0x0A48)) || ((ch >= 0x0A4B) && (ch <= 0x0A4D)) || ((ch >= 0x0A70) && (ch <= 0x0A71)) || ((ch >= 0x0A81) && (ch <= 0x0A83)) || (ch == 0x0ABC) || ((ch >= 0x0ABE) && (ch <= 0x0AC5)) || ((ch >= 0x0AC7) && (ch <= 0x0AC9)) || ((ch >= 0x0ACB) && (ch <= 0x0ACD)) || ((ch >= 0x0B01) && (ch <= 0x0B03)) || (ch == 0x0B3C) || ((ch >= 0x0B3E) && (ch <= 0x0B43)) || ((ch >= 0x0B47) && (ch <= 0x0B48)) || ((ch >= 0x0B4B) && (ch <= 0x0B4D)) || ((ch >= 0x0B56) && (ch <= 0x0B57)) || ((ch >= 0x0B82) && (ch <= 0x0B83)) || ((ch >= 0x0BBE) && (ch <= 0x0BC2)) || ((ch >= 0x0BC6) && (ch <= 0x0BC8)) || ((ch >= 0x0BCA) && (ch <= 0x0BCD)) || (ch == 0x0BD7) || ((ch >= 0x0C01) && (ch <= 0x0C03)) || ((ch >= 0x0C3E) && (ch <= 0x0C44)) || ((ch >= 0x0C46) && (ch <= 0x0C48)) || ((ch >= 0x0C4A) && (ch <= 0x0C4D)) || ((ch >= 0x0C55) && (ch <= 0x0C56)) || ((ch >= 0x0C82) && (ch <= 0x0C83)) || ((ch >= 0x0CBE) && (ch <= 0x0CC4)) || ((ch >= 0x0CC6) && (ch <= 0x0CC8)) || ((ch >= 0x0CCA) && (ch <= 0x0CCD)) || ((ch >= 0x0CD5) && (ch <= 0x0CD6)) || ((ch >= 0x0D02) && (ch <= 0x0D03)) || ((ch >= 0x0D3E) && (ch <= 0x0D43)) || ((ch >= 0x0D46) && (ch <= 0x0D48)) || ((ch >= 0x0D4A) && (ch <= 0x0D4D)) || (ch == 0x0D57) || (ch == 0x0E31) || ((ch >= 0x0E34) && (ch <= 0x0E3A)) || ((ch >= 0x0E47) && (ch <= 0x0E4E)) || (ch == 0x0EB1) || ((ch >= 0x0EB4) && (ch <= 0x0EB9)) || ((ch >= 0x0EBB) && (ch <= 0x0EBC)) || ((ch >= 0x0EC8) && (ch <= 0x0ECD)) || ((ch >= 0x0F18) && (ch <= 0x0F19)) || (ch == 0x0F35) || (ch == 0x0F37) || (ch == 0x0F39) || (ch == 0x0F3E) || (ch == 0x0F3F) || ((ch >= 0x0F71) && (ch <= 0x0F84)) || ((ch >= 0x0F86) && (ch <= 0x0F8B)) || ((ch >= 0x0F90) && (ch <= 0x0F95)) || (ch == 0x0F97) || ((ch >= 0x0F99) && (ch <= 0x0FAD)) || ((ch >= 0x0FB1) && (ch <= 0x0FB7)) || (ch == 0x0FB9) || ((ch >= 0x20D0) && (ch <= 0x20DC)) || (ch == 0x20E1) || ((ch >= 0x302A) && (ch <= 0x302F)) || (ch == 0x3099) || (ch == 0x309A));
  }

  public static boolean isDigit(char ch) {
    return (((ch >= 0x0030) && (ch <= 0x0039)) || ((ch >= 0x0660) && (ch <= 0x0669)) || ((ch >= 0x06F0) && (ch <= 0x06F9)) || ((ch >= 0x0966) && (ch <= 0x096F)) || ((ch >= 0x09E6) && (ch <= 0x09EF)) || ((ch >= 0x0A66) && (ch <= 0x0A6F)) || ((ch >= 0x0AE6) && (ch <= 0x0AEF)) || ((ch >= 0x0B66) && (ch <= 0x0B6F)) || ((ch >= 0x0BE7) && (ch <= 0x0BEF)) || ((ch >= 0x0C66) && (ch <= 0x0C6F)) || ((ch >= 0x0CE6) && (ch <= 0x0CEF)) || ((ch >= 0x0D66) && (ch <= 0x0D6F)) || ((ch >= 0x0E50) && (ch <= 0x0E59)) || ((ch >= 0x0ED0) && (ch <= 0x0ED9)) || ((ch >= 0x0F20) && (ch <= 0x0F29)));
  }

  public static boolean isExtender(char ch) {
    return ((ch == 0x00B7) || (ch == 0x02D0) || (ch == 0x02D1) || (ch == 0x0387) || (ch == 0x0640) || (ch == 0x0E46) || (ch == 0x0EC6) || (ch == 0x3005) || ((ch >= 0x3031) && (ch <= 0x3035)) || ((ch >= 0x309D) && (ch <= 0x309E)) || ((ch >= 0x30FC) && (ch <= 0x30FE)));
  }

  public static boolean isName(String s) {
    if ((s == null) || (s.length() == 0) || (!isInitialNameChar(s.charAt(0)))) {
      return false;
    }

//    char[] a = s.toCharArray();
 
    for (int i = 1; i < s.length(); i++) {
      if (!isNameChar(s.charAt(i))) {
        return false;
      }
    } 

    return true;
  }

  public static boolean isWhitespace(String s) {
//    char[] a = s.toCharArray();

    for (int i = 0; i < s.length(); i++) {
      if (!isWhitespace(s.charAt(i))) {
        return false;
      }
    } 

    return true;
  }

  public static boolean isNmtoken(String s) {
    if ((s == null) || (s.length() == 0)) {
      return false;
    }

//    char[] a = s.toCharArray();

    for (int i = 0; i < s.length(); i++) {
      if (!isNameChar(s.charAt(i))) {
        return false;
      }
    } 

    return true;
  }

  public static boolean isNmtokens(String s) {
    if ((s == null) || (s.length() == 0)) {
      return false;
    }

//    char[] a = s.toCharArray();
    char temp;
    for (int i = 0; i < s.length(); i++) {
      if (!isNameChar(temp = s.charAt(i)) && !isWhitespace(temp)) {
        return false;
      }
    } 

    return true;
  }

  public static boolean isNCName(String s) {
//    return isName(s) && (s.indexOf(':') == -1);
    if ((s == null) || (s.length() == 0) || (!isInitialNameChar(s.charAt(0)))) {
      return false;
    }

//    char[] a = s.toCharArray();
    char temp;
    for (int i = 1; i < s.length(); i++) {
      temp = s.charAt(i);
      if ((temp==':')||!isNameChar(temp)) {
        return false;
      }
    } 

    return true;    
    
  }
  /**
   * @return -1 in case <code>s</code> is valid NCName, otherwise the index of the first found invalid character is returned. 
   * @throws IllegalArgumentException when <code>s</code> is null or with length 0. 
   */  
  public static int checkNCName(String s) {
    if ((s == null) || (s.length() == 0)) {
      throw new IllegalArgumentException();
    }

    if (! isInitialNameChar(s.charAt(0))) {
      return 0;
    }

    char temp;
    for (int i = 1; i < s.length(); i++) {
      temp = s.charAt(i);
      if ((temp==':') || !isNameChar(temp)) {
        return i;
      }
    } 
    return -1;    
  }
  /**
   * Checks whether <code>s</code> begins with 'xml' or any string which match (('X'|'x') ('M'|'m') ('L'|'l')).
   * Returns true if <code>s</code> begins with such string, false otherwise. 
   * @param s xml name to be checked.
   */
  public static boolean hasNameXMLProlog(String s) {
    if (s == null) {
      return false;
    }
    if (s.length() < 3) {
      return false;
    }
    String xml = s.substring(0, 3);
    if (xml.equalsIgnoreCase("xml")) {
      return true;
    }
    return false;
  }
}

      
      
      