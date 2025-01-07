package com.sap.security.core.server.jaas.spnego.util;

/**
 * Base64-encoded and decoding
 *
 */
public class Base64 {
  /**
   * Returns a base64-encoded string to represent the passed data array.
   *
   * @param data the array of bytes to encode
   * @return base64-coded string.
   */
  static public String encode(byte[] data) {
    return new String(encodeAsArray(data));
  }

  /**
   * Returns an array of bytes which were encoded in the passed string
   *
   * @param data the base64-encoded string
   * @return decoded data array
   */
  static public byte[] decode(String data) {
    char[] tdata = new char[data.length()];
    data.getChars(0, data.length(), tdata, 0);
    return decode(tdata);
  }

  /**
   * Returns an array of base64-encoded characters to represent the
   * passed data array.
   *
   * @param data the array of bytes to encode
   * @return base64-coded character array.
   */
  static public char[] encodeAsArray(byte[] data) {
    char[] out = new char[((data.length + 2) / 3) * 4];

    //
    // 3 bytes encode to 4 chars.  Output is always an even
    // multiple of 4 characters.
    //
    for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
      boolean quad = false;
      boolean trip = false;

      int val = (0xFF & (int) data[i]);
      val <<= 8;
      if ((i + 1) < data.length) {
        val |= (0xFF & (int) data[i + 1]);
        trip = true;
      }

      val <<= 8;
      if ((i + 2) < data.length) {
        val |= (0xFF & (int) data[i + 2]);
        quad = true;
      }

      out[index + 3] = b64code[(quad ? (val & 0x3F) : 64)];
      val >>= 6;
      out[index + 2] = b64code[(trip ? (val & 0x3F) : 64)];
      val >>= 6;
      out[index + 1] = b64code[val & 0x3F];
      val >>= 6;
      out[index + 0] = b64code[val & 0x3F];
    }
    return out;
  }

  /**
   * Returns an array of bytes which were encoded in the passed
   * character array.
   *
   * @param data the array of base64-encoded characters
   * @return decoded data array
   */
  static public byte[] decode(char[] inputChars) {

    //the minimum size of Base64 encoded strings is 4 
    if (inputChars.length < 4) {
      return new byte[0];
    }

    int len = ((inputChars.length + 3) / 4) * 3;
    if (inputChars[inputChars.length - 1] == '=') {
      --len;
    }
    if (inputChars[inputChars.length - 2] == '=') {
      --len;
    }

    byte[] outputBytes = new byte[len];

    int shift = 0; // number of excess bits stored in accum
    int accum = 0;
    int index = 0;

    //accum contains 0 bits 
    //take char 1 => accum contains 0 + 6 bits 
    //take char 2 => accum contains 6+6=12 bits
    //               take the first 8 bits from accum
    //               add a new byte to the output, 
    //               accum contains now 12-8=4 bits 
    //take char 3 => accum contains 4+6=10 bits
    //               add a new byte to the output 
    //               accum contains now 10-8=2 bits 
    //take char 4 => accum contains 2+6=8 bits
    //               add a new byte to the output 
    //               accum contains now 8-8=0 bits 
    //4 chars from input become 3 bytes in output
    //continue with the loop

    for (int ix = 0; ix < inputChars.length; ix++) {
      int value;
      if ((inputChars[ix] & 0xFF) == 33) {
        // R/3 systems use '!' istead of '+' in tickets
        value = (byte) 62;
      } else {
        value = b64icode[inputChars[ix] & 0xFF]; // ignore high byte of char
      }

      //value is a byte of type [00ab cdef]
      if (value >= 0) {

        accum <<= 6; // move current bits to the left 
        // by 6 for each byte 
        shift += 6; // loop, with new bits being put in
        accum |= value; // at the bottom.
        if (shift >= 8) {
          //whenever there are 8 or more shifted in,
          //write them out (from the top, leaving any
          //excess at the bottom for next iteration.
          shift -= 8;
          outputBytes[index++] = (byte) ((accum >> shift) & 0xff);
        }
      }
    }
    if (index != outputBytes.length) {
      throw new RuntimeException("miscalculated data length!");
    }

    return outputBytes;
  }

  //
  // code characters for values 0..63
  //
  private static char[] b64code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

  //
  // lookup table for converting base64 characters to value in range 0..63
  // example: 
  // encoding
  // bits [0000 01xx] are coded to [000001][xx] 
  // first part is [000001] which is 1,
  // base62[1] is 'B', => [000001xx] is encoded to By 
  // where y is the next generated symbol
  // decoding:
  // the ASCII code of 'B' is 66 => we take base64icode[66]=1    
  // then 1 becomes [000 001] then we generate a byte [ 0000 01 xx ] 
  // where the last 2 bits come from the decoding of the next input char        
  //

  private static byte[] b64icode = new byte[128];
  static {
    for (int i = 0; i < 128; i++) {
      b64icode[i] = -1;
    }
    for (int i = 0; i < b64code.length; i++) {
      b64icode[b64code[i]] = (byte) i;
    }
    b64icode['='] = -1;
  }

  public static void main(String args[]) {
    for (int i = 0; i < 128; i++) {
      System.out.println(b64icode[i]);
    }

    String strings[] = { "carnal pleasure.", "carnal pleasure", "carnal pleasur", "carnal pleasu" };

    for (int i = 0; i < strings.length; i++) {

      System.out.print(strings[i]);
      String encoded = new String(encode(strings[i].getBytes()));
      System.out.print("  " + encoded);
      String decoded = new String(decode(encoded));
      System.out.println("  " + decoded);

    }

  }
}
