package com.sap.engine.lib.xml.parser.helpers;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SUNEncodings extends Encoding {

//  private ByteToCharConverter btocconv; // = new ByteToCharConverter();
//  private CharToByteConverter ctobconv; // = new CharToByteConverter();
  private Object btocconv; // = new ByteToCharConverter();
  private Object ctobconv; // = new CharToByteConverter();
//  private boolean hasfirstbyte = false;
//  private byte[] twobytecharacter = new byte[2];
  private byte[] byteHolder = new byte[1];
  private char[] decoded = new char[1];
  private CharArray name = null;
  int bytecount;

  //  public static void main(String[] args) {
  //    ByteToCharGBK gbk = new ByteToCharGBK();
  //    byte[] data = new byte[4];
  //    data[0] = (byte)0x80;
  //    data[1] = (byte)0x81;
  //    data[2] = (byte)0x81;
  //    data[3] = (byte)0x30;
  //    char[] chars = new char[5];
  //    try {
  //      LogWriter.getSystemLogWriter().println(gbk.convert(data,0,1,chars,0,3));
  //      LogWriter.getSystemLogWriter().println((int)chars[0]);
  //      LogWriter.getSystemLogWriter().println((int)chars[1]);
  //    } catch (Exception e) {
  //      e.printStackTrace();
  //    }
  //  }
  public SUNEncodings(String encoding) throws UnsupportedEncodingException {
    btocconv = getConverter("sun.io.ByteToCharConverter", encoding); //ByteToCharConverter.getConverter(encoding);
    ctobconv = getConverter("sun.io.CharToByteConverter", encoding); //CharToByteConverter.getConverter(encoding);
    name = new CharArray(encoding);
  }
  
  public static Object getConverter(String className, String encoding) throws UnsupportedEncodingException {
    try {
      Class clazz = Class.forName(className);
      Method getConverterMethod = clazz.getMethod("getConverter", new Class[] {String.class});
      return getConverterMethod.invoke(null, new String[] {encoding});
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Cannot process non-standard encoding " + encoding + " on a JDK that is not provided by Sun: " + e);
    } catch (SecurityException e) {
      throw new RuntimeException("Cannot process non-standard encoding " + encoding + " on a JDK that is not provided by Sun: " + e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Cannot process non-standard encoding " + encoding + " on a JDK that is not provided by Sun: " + e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Cannot process non-standard encoding " + encoding + " on a JDK that is not provided by Sun: " + e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Cannot process non-standard encoding " + encoding + " on a JDK that is not provided by Sun: " + e);
    } catch (InvocationTargetException e) {
      Throwable targetException = e.getTargetException();
      if (targetException instanceof UnsupportedEncodingException) {
        throw (UnsupportedEncodingException) targetException;
      }
      throw new RuntimeException("Cannot process non-standard encoding " + encoding + " on a JDK that is not provided by Sun: " + e);
    }
  }

  public SUNEncodings(CharArray encoding) throws UnsupportedEncodingException {
    btocconv = getConverter("sun.io.ByteToCharConverter", encoding.toString()); //ByteToCharConverter.getConverter(encoding.toString());
    ctobconv = getConverter("sun.io.CharToByteConverter", encoding.toString()); //CharToByteConverter.getConverter(encoding.toString());
    name = encoding;
  }

  public int process(byte ch) {
    byteHolder[0] = ch;
    try {
      bytecount = byteToCharConvert(btocconv, byteHolder, 0, 1, decoded, 0, 1); //btocconv.convert(byteHolder, 0, 1, decoded, 0, 1);
    } catch (Exception e) {
      //$JL-EXC$
      return UNSUPPORTED_CHAR;
    }  
    
    if (bytecount == 0) {
      return NEEDS_MORE_DATA;
    } else {
      return decoded[0];
    }
        
        
//    if (!hasfirstbyte && ch < 0x80 && ch >= 0) {
//      return ch;
//    } else {
//      if (!hasfirstbyte) {
//        twobytecharacter[0] = ch;
//        try {
//          bytecount = btocconv.convert(twobytecharacter, 0, 1, decoded, 0, 1);
//        } catch (Exception e) {
//          return UNSUPPORTED_CHAR;
//        }
//
//        if (bytecount == 0) {
//          hasfirstbyte = true;
//          return NEEDS_MORE_DATA;
//        } else {
//          return decoded[0];
//        }
//      } else {
//        twobytecharacter[1] = ch;
//        try {
//          btocconv.convert(twobytecharacter, 0, 1, decoded, 0, 1);
//        } catch (Exception e) {
//          return UNSUPPORTED_CHAR;
//        }
//        return decoded[0];
//      }
//    } 
  }

  public int reverseEncode(byte[] result, int ch) {
    decoded[0] = (char) ch;
    try {
      return charToByteConvert(ctobconv, decoded, 0, 1, result, 0, result.length); //ctobconv.convert(decoded, 0, 1, result, 0, result.length);
    } catch (Exception e) {
      //$JL-EXC$
      return UNSUPPORTED_CHAR;
    }
  }

  public CharArray getName() {
    return name;
  }

  private static final Class byteArrayClass = (new byte[0]).getClass();
  private static final Class charArrayClass = (new char[0]).getClass();
  
  public static int byteToCharConvert(Object btocconv, byte[] abyte0, int i, int j, char ac[], int k, int l) {
    try {
      Method method = btocconv.getClass().getMethod("convert", new Class[] {byteArrayClass, Integer.TYPE, Integer.TYPE, charArrayClass, Integer.TYPE, Integer.TYPE});

      return ((Integer) method.invoke(btocconv, new Object[] {abyte0, new Integer(i), new Integer(j), ac, new Integer(k), new Integer(l)})).intValue();
    } catch (SecurityException e) {
      throw new RuntimeException("Cannot process non-standard encoding on a JDK that is not provided by Sun: " + e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Cannot process non-standard encoding on a JDK that is not provided by Sun: " + e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Cannot process non-standard encoding on a JDK that is not provided by Sun: " + e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Cannot process non-standard encoding on a JDK that is not provided by Sun: " + e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Cannot process non-standard encoding on a JDK that is not provided by Sun: " + e);
    }
  }

  public static int charToByteConvert(Object ctobconv, char ac[], int i, int j, byte abyte0[], int k, int l) {
    try {
      Method method = ctobconv.getClass().getMethod("convert", new Class[] {charArrayClass, Integer.TYPE, Integer.TYPE, byteArrayClass, Integer.TYPE, Integer.TYPE});

      return ((Integer) method.invoke(ctobconv, new Object[] {ac, new Integer(i), new Integer(j), abyte0, new Integer(k), new Integer(l)})).intValue();
    } catch (SecurityException e) {
      throw new RuntimeException("Cannot process non-standard encoding on a JDK that is not provided by Sun: " + e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Cannot process non-standard encoding on a JDK that is not provided by Sun: " + e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Cannot process non-standard encoding on a JDK that is not provided by Sun: " + e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Cannot process non-standard encoding on a JDK that is not provided by Sun: " + e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Cannot process non-standard encoding on a JDK that is not provided by Sun: " + e);
    }
  }
}

