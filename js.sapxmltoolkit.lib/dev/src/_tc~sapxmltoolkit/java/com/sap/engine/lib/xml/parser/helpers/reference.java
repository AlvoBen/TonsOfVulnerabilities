package com.sap.engine.lib.xml.parser.helpers;

//import com.sap.engine.lib.xml.utils.*;
/**
 * Class description -
 *
 * @author Vladimir Savtchenko
 * @version 1.00
 */
public class Reference {

  public static final int RX = 1;
  public static final int RD = 2;
  public static final int RENT = 3;
  public static final int RPE = 4;
  public final static CharArray PREDEF_LT = new CharArray("lt").setStatic();
  public final static CharArray PREDEF_GT = new CharArray("gt").setStatic();
  public final static CharArray PREDEF_APOS = new CharArray("apos").setStatic();
  public final static CharArray PREDEF_QUOT = new CharArray("quot").setStatic();
  public final static CharArray PREDEF_AMP = new CharArray("amp").setStatic();
  public final static CharArray caNoname = new CharArray("noname").setStatic();

//  public static boolean isChar(int ch) {
//    if (ch == 0x9 || ch == 0xA || ch == 0xD || (ch >= 0x20 && ch <= 0xD7FF) || (ch >= 0xE000 && ch <= 0xFFFD) || (ch >= 0x10000 && ch < 0x10FFFF)) {
//      return true;
//    } else {
//      return false;
//    }
//  }

  public char getChar() throws Exception {
    return (char) getCharAsInt();
  }

  public int getCharAsInt() throws Exception {
    switch (type) {
      case RX:
      case RD: {
        return num;
      }
      case RENT:
      case RPE: {
        if (name.equals(PREDEF_LT)) {
          return '<';
        } else if (name.equals(PREDEF_GT)) {
          return '>';
        } else if (name.equals(PREDEF_APOS)) {
          return '\'';
        } else if (name.equals(PREDEF_QUOT)) {
          return '\"';
        } else if (name.equals(PREDEF_AMP)) {
          return '&';
        } else {
          throw new Exception("XMLParser: Cannot get reference by char. Reference name is: " + name);
        }
      }
      default: {
        throw new Exception("XMLParser: Bad REference type...");
      }
    }
  }

  public CharArray getName() {
    return name;
  }

  int num;
  String resolvedValue;
  String resolvedName;

  public String resolve() {
    return resolvedName;
  }

//  public boolean hasCont() {
//    return bCont;
//  }

  private int type;

  public int getType() {
    return type;
  }

//  public String restore() {
//    switch (type) {
//      case RD: {
//        return "&#" + Integer.toString(num, 10) + ";";
//      }
//      case RX: {
//        return "&#x" + Integer.toString(num, 16) + ";";
//      }
//      case RENT: {
//        return "&" + name + ";";
//      }
//      case RPE: {
//        return "%" + name + ";";
//      }
//    }
//
//    return "";
//  }

  public Reference reuse(int type, CharArray value) {
    this.type = type;

    switch (type) {
      case RX: {
        num = Integer.parseInt(value.toString(), 16);
        resolvedValue = "" + (char) num;
        name = caNoname;
        break;
      }
      case RD: {
        num = Integer.parseInt(value.toString(), 10);
        resolvedValue = "" + (char) num;
        name = caNoname;
        break;
      }
      case RENT:
      case RPE: {
        name = value.copy();
        break;
      }
      default:
    }

    return this;
  }

  CharArray name = null;
  //String resolvedName = null;
  boolean bCont;

  public boolean isPredefined() {
    if (name.equals(PREDEF_LT) || name.equals(PREDEF_GT) || name.equals(PREDEF_APOS) || name.equals(PREDEF_QUOT) || name.equals(PREDEF_AMP)) {
      return true;
    } else {
      return false;
    }
  }

}

